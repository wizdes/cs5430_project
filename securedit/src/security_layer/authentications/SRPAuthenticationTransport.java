package security_layer.authentications;

import configuration.Constants;
import document.NetworkDocumentHandlerInterface;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import security_layer.KeyFactory;
import security_layer.Profile;
import security_layer.SecureTransportInterface;
import transport_layer.network.NetworkTransportInterface;

/**
 * Main SRP authentication processor.
 * -Sends authentication messages.
 * -Processes authentication messages.
 * 
 * SRP Setup protocol:
 * Precondition: Client goes to document owner and they exchange a shared key.
 *               Server has <ClientID, PIN> in memory.
 *    Client                                    Server
 * 1. Client picks password P
 *    x = H(s, P) 
 *    v = g^x                   s,v -->         Store <C, (s,v)>
 * 2.                           <-- Success                           
 * 
 * SRP Authentication protocol:
 * Precondition: Server has s,v. Client remembers password P.
 *      Client                                  Server
 *      A = g^a	A
 * 1.                           C, A -->        (lookup s, v)
 *                                              B = k*v + g^b    
 * 2.                           <-- B,u,s
 *      x = H(s, P)   	
 *      S = (B - k*g^x)^(a + ux)                S = (A · v^u)^b
 * 	K = H(S)                                K = H(S)
 * 7.	M[1] = H(A, B, K)	M[1] -->	(verify M[1])
 * 8.	(verify M[2])           <-- M[2]	M[2] = H(A, M[1], K)
 * 
 * Postcondition: Client and Server share session key K.
 * 
 * @author Patrick
 */
public class SRPAuthenticationTransport {
    public static int AUTH_TIMEOUT_DELAY = 16000;
    private NetworkTransportInterface transport;
    private SecureTransportInterface secureTransport;
    private ConcurrentMap<String, AuthenticationSession> authSessions = new ConcurrentHashMap<>();
    private ConcurrentMap<String, char[]> pendingPINs = new ConcurrentHashMap<>();
    private ConcurrentMap<String, NetworkDocumentHandlerInterface> docInstances;
    private Profile profile;
    
    private static long sessionIdCounter = 0L;
    private static final BigInteger n = new BigInteger(MODPGroups.MODP_GROUP_3072, 16);
    private static final BigInteger g = new BigInteger(MODPGroups.GENERATOR + "");
    private final BigInteger k = new BigInteger(H(n.toByteArray(), g.toByteArray()));
    
    public SRPAuthenticationTransport(NetworkTransportInterface transport, 
                                   SecureTransportInterface secureTransport, 
                                   Profile profile,
                                   ConcurrentMap<String, NetworkDocumentHandlerInterface> docInstances) {
        this.profile = profile;
        this.transport = transport;
        this.secureTransport = secureTransport;
        this.docInstances = docInstances;
        this.transport.setAuthenticationTransport(this);
    }
   
    /**
     * Initializes SRP authentication during account creation.
     * -Exchanges salt and verifier.
     * -Uses PIN to encrypt them.
     * @param serverID Server want to authenticate with.
     * @param docID Document trying to connect to.
     * @param password Password using for this server/document pair.
     * @param PIN PIN server gave to them.
     * @return Was able to send SRP authentication message successfully.
     */
    public boolean initializeSRPAuthentication(String serverID, String docID, char[] password, char[] PIN){
        byte[] salt = KeyFactory.generateSalt();
        BigInteger x = new BigInteger(KeyFactory.generateSymmetricKey(password, salt).getEncoded());
        BigInteger v = g.modPow(x, n);

        SecretKey pinKey = null;
        SecretKey HMACKey = null;
        try {
            pinKey = KeyFactory.generateSymmetricKey(PIN, "PIN".getBytes("UTF-16"));
            HMACKey = KeyFactory.generateSymmetricKey(PIN, "HMAC".getBytes("UTF-16"));
        } catch (UnsupportedEncodingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SRPAuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        InitAuth_Msg initMsg = new InitAuth_Msg(v, salt, docID);
        
        AuthenticationSession session = new AuthenticationSession(profile.username, serverID, docID);
        authSessions.put(getAuthSessionMapKey(serverID, docID), session);
        
        if (!this.secureTransport.sendAESEncryptedMessage(serverID, docID, initMsg, pinKey, HMACKey)) {
            return false;
        }
        
        session.await();
        return session.success;
    }
    
    /**
     * Starts SRP authentication with a server(login).
     * @param serverID Server want to authenticate with.
     * @param docID Document trying to connect to.
     * @param password Password using for this server/document pair.
     * @return Whether authentication response message was sent successfully.
     */
    public boolean authenticate(String serverID, String docID, char[] password){
        // Create new session state
        BigInteger a = generateEphemeralPrivateKey();     //a = ephemeral private key
        BigInteger A = g.modPow(a, n);                    //A = g^a = ephemeral public key
        AuthenticationSession session = new AuthenticationSession(profile.username, serverID, docID);
        session.password = password;
        session.a = a;
        session.A = A;
        authSessions.put(getAuthSessionMapKey(serverID, docID), session);
        
        // Send initial message
        Auth_Msg1 initialMsg = new Auth_Msg1(profile.username, A, docID);
        transport.send(serverID, docID, initialMsg);
        
        //Wait for authentication to complete
        session.await();
        
        return session.success;
    }
    
    /**
     * Processes both SRP initialize/setup and authenticate messages.
     * @param sourceID Server want to authenticate with.
     * @param docID Document trying to connect to.
     * @param receivedMsg Authentication message to process.
     */
    public void processAuthenticationMessage(String sourceID, String docID, AuthenticationMessage receivedMsg){
        if(receivedMsg instanceof SRPSetupMessage){
            processSRPSetupMessage(sourceID, (SRPSetupMessage)receivedMsg);
        } else if(receivedMsg instanceof SRPAuthenticationMessage){
            try {
                processSRPMessage(sourceID, (SRPAuthenticationMessage)receivedMsg);
            } catch (InvalidSRPMessageException ex) {
                AccountLoginError error = new AccountLoginError(docID, "");
                this.secureTransport.sendPlainTextMessage(sourceID, docID, error);                
            } catch(InconsistentSessionKeyException ex){
                AccountLoginError error = new AccountLoginError(docID, "");
                this.secureTransport.sendPlainTextMessage(sourceID, docID, error);   
            }
        }
    }
    
    /**
     * Processes SRP setup message.
     * @param sourceID  Server want to authenticate with.
     * @param receivedSetupMsg SRP setup message to process.
     */
    private void processSRPSetupMessage(String sourceID, SRPSetupMessage receivedSetupMsg) {
        if (receivedSetupMsg instanceof InitAuth_Msg) {
            InitAuth_Msg initMsg = (InitAuth_Msg)receivedSetupMsg;
            docInstances.get(initMsg.docID).getServerAuthenticationPersistantState()
                                           .addClientPasswordState(sourceID, initMsg.s, initMsg.v);
            
            InitAuth_MsgSuccess ack = new InitAuth_MsgSuccess(initMsg.docID);
            this.secureTransport.sendPlainTextMessage(sourceID, initMsg.docID, ack);
        } else if (receivedSetupMsg instanceof InitAuth_MsgSuccess) {
            InitAuth_MsgSuccess msg = (InitAuth_MsgSuccess)receivedSetupMsg;
            AuthenticationSession session = authSessions.get(getAuthSessionMapKey(sourceID, msg.docID));
            session.wakeup(true);            
        }
    }
    
    private String getAuthSessionMapKey(String s, String d) {
        return s + ":::" + d;
    }
    
    /**
     * Process SRP authentication message.
     * -Multiplexes the message, processes it, and creates/sends reply message.
     * 
     * Messages: 
     * C ==> S	C, A    //Auth_Msg1
     * C <== S	s, B    //Auth_Msg2    
     * C ==> S	M[1]    //Auth_Msg3
     * C <== S	M[2]    //Auth_Msg4
     * 
     * 
     * @param sourceID Server want to authenticate with.
     * @param receivedSRPMsg Received SRP authentication message to process.
     * @throws InvalidSRPMessageException
     * @throws InconsistentSessionKeyException If key MAC doesn't match
     * 
     */
    private void processSRPMessage(String sourceID, SRPAuthenticationMessage receivedSRPMsg) throws InvalidSRPMessageException, InconsistentSessionKeyException{
        ServerAuthenticationPersistantState persistantServerState;
        
        if (receivedSRPMsg instanceof Auth_Msg1) {    //Server
            Auth_Msg1 msg1 = (Auth_Msg1)receivedSRPMsg;
            //Fetch Persistent state
            NetworkDocumentHandlerInterface doc = docInstances.get(msg1.docID);
            if (doc == null) {
                throw new InvalidSRPMessageException("Client: " + sourceID + " has no account[s,v pair] on record.");
            }
            
            persistantServerState = doc.getServerAuthenticationPersistantState();
            
            byte[] s = persistantServerState.getClientSalt(sourceID);           //s = salt
            BigInteger v = persistantServerState.getClientVerifier(sourceID);   //v = verifier = g^x
            if (s == null || v == null){
                throw new InvalidSRPMessageException("Client: " + sourceID + " has no account[s,v pair] on record.");
            }
            
            //Create new session
            BigInteger b = generateEphemeralPrivateKey();           //b
            BigInteger B = g.modPow(b, n);                          //g^b
            B = B.add(k.multiply(v));                               //B = kv + g^b
            AuthenticationSession session = new AuthenticationSession(sourceID, profile.username, msg1.docID);
            session.b = b;
            session.B = B;
            session.A = msg1.A;
            authSessions.put(getAuthSessionMapKey(sourceID, msg1.docID), session);
            
            //Compute S(on the server) which is the common exponential value
            BigInteger u = new BigInteger(KeyFactory.generateNonce() + "");
            BigInteger S = v.modPow(u, n);                      //v^u
            S = session.A.multiply(S);                                 //A * v^u
            S = S.modPow(b, n);                                     //S = (A * v^u)^b mod n
            
            //Generate key from S
            session.K = H(S.toByteArray());                     //K = H(S)
            
            //Reply with Auth_Msg2
            Auth_Msg2 replyMsg = new Auth_Msg2(s, B, u, receivedSRPMsg.docID);
            transport.send(sourceID, replyMsg);
        } else if(receivedSRPMsg instanceof Auth_Msg2){ //Client
            Auth_Msg2 msg2 = (Auth_Msg2)receivedSRPMsg;
            AuthenticationSession session = authSessions.get(getAuthSessionMapKey(sourceID, msg2.docID));
            if(session == null){
                throw new InvalidSRPMessageException("Server: " + sourceID + " has no session data stored on the client.");
            }
            session.B = msg2.B;

            //Compute S(on the client) which is the common exponential value
            BigInteger x = new BigInteger(KeyFactory.generateSymmetricKey(session.password, msg2.salt).getEncoded());
            BigInteger S = session.B.subtract(k.multiply(g.modPow(x, n)));  //(B - g^x)
            BigInteger exp = session.a.add(msg2.u.multiply(x));                     //(a + u*x)
            S = S.modPow(exp, n);                                                       //S = (B - g^x)^(a + u*x)
            
            //Generate key from S
            session.K = H(S.toByteArray());                                         //K = H(S)
            
            //Send Auth_Msg3
            byte[] M1 = H(session.A.toByteArray(), session.B.toByteArray(), session.K);
            Auth_Msg3 msg3 = new Auth_Msg3(M1, msg2.docID);
            transport.send(sourceID, msg2.docID, msg3);
        } else if(receivedSRPMsg instanceof Auth_Msg3){ //Server
            Auth_Msg3 msg3 = (Auth_Msg3)receivedSRPMsg;
            AuthenticationSession session = authSessions.get(getAuthSessionMapKey(sourceID, msg3.docID));
            if(session == null){
                throw new InvalidSRPMessageException("Client: " + sourceID + " has no session data stored on the server.");
            }

            byte[] serversM1 = H(session.A.toByteArray(), session.B.toByteArray(), session.K);
            
            if(!Arrays.equals(msg3.M1, serversM1)){
                throw new InconsistentSessionKeyException("Client's M1 doesn't match servers");
            }
            //Postcondition: Keys match according to the server
            
            //Place session key K inside data structure.
            produceAndSaveSessionKeys(sourceID, msg3.docID, session.K);
            
            byte[] M2 = H(session.A.toByteArray(), msg3.M1, session.K);
            Auth_Msg4 msg4 = new Auth_Msg4(M2, msg3.docID);
            transport.send(sourceID, msg3.docID, msg4);
            
            //Zero memory for session
            cleanupSession(sourceID, msg3.docID);
            
        } else if(receivedSRPMsg instanceof Auth_Msg4){ //Client
            Auth_Msg4 msg4 = (Auth_Msg4)receivedSRPMsg;
            AuthenticationSession session = authSessions.get(getAuthSessionMapKey(sourceID, msg4.docID));
            if(session == null){
                throw new InvalidSRPMessageException("Server: " + sourceID + " has no session data stored on the client.");
            }
            byte[] M1 = H(session.A.toByteArray(), session.B.toByteArray(), session.K);   //recompute M1 since didn't save it
            if(!Arrays.equals(msg4.M2, H(session.A.toByteArray(), M1, session.K))){
                throw new InconsistentSessionKeyException("Server's M2 doesn't match clients");
            }
            //Postcondition: Keys match according to the client
            
            //Place session key K inside data structure.
            produceAndSaveSessionKeys(sourceID, msg4.docID, session.K);
            
            //Zero memory for session
            cleanupSession(sourceID, msg4.docID);
            
            //Wake up client
            session.wakeup(true);
        }
    }
    
    public char[] generatePIN(String userID, String docID) {
        char[] PIN = KeyFactory.generatePIN();
        pendingPINs.put(userID + "-" + docID, PIN);
        Constants.log("generated pin for " + userID + "-" + docID + " -> " + this.getPIN(userID, docID));
        return PIN;
    }
    
    public char[] getPIN(String userID, String docID) {
        return pendingPINs.get(userID + "-" + docID);
    }    
 
    
    private void produceAndSaveSessionKeys(String sourceID, String docID, byte[] key) {
        try {
            SecretKey sessionKey = KeyFactory.generateSymmetricKey(new String(key).toCharArray(), "SESSION_KEY".getBytes("UTF-16"));
            SecretKey hmacKey = KeyFactory.generateSymmetricKey(new String(key).toCharArray(), "HMAC".getBytes("UTF-16"));
            profile.keys.addSessionKey(sourceID, docID, sessionKey);
            profile.keys.addHmacKey(sourceID, docID, hmacKey);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SRPAuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private byte[] H(byte[]... input) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            for (byte[] byteArray : input) {
                sha.update(byteArray);
            }
            return sha.digest();
        } catch (NoSuchAlgorithmException ex) {
            if (Constants.DEBUG_ON) {
                Logger.getLogger(SRPAuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    private BigInteger generateEphemeralPrivateKey(){
        BigInteger key = null;
        do{
            key = randomBigInteger(BigInteger.ONE, n);
            //check that key > log_g(n)...might help: a = log_2(n) == n = 2^a
        }while(false);
        return key;
    }
    /**
     * Generates a random big integer.
     * @param min Min exclusive
     * @param max Max exclusive
     * @return Random big integer from min(exclusive) to max(exclusive)
     */
    private BigInteger randomBigInteger(BigInteger min, BigInteger max){
        BigInteger val = null;
        try {
            SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
            rand.nextBytes(new byte[1]);    //force seed
            do{
                val = new BigInteger(max.bitLength(), rand);
            } while(val.compareTo(max) >= 0 || val.compareTo(min) <= 0);
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            Logger.getLogger(SRPAuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }
    
    /**
     * Cleans up secure data in memory for the given session.
     * @return Whether or not the session had completed authentication at the time of removal
     */
    private boolean cleanupSession(String id, String docID){
        String key = this.getAuthSessionMapKey(id, docID);
        AuthenticationSession session = authSessions.get(key);
        session.authenticateLock.lock();
        try{
            session.cleanup();
            authSessions.remove(key);
        } finally{
            session.authenticateLock.unlock();
        }
        return session.authenticationCompleted;
    }
    
    private void setAuthTimeout(final String sourceID, final String docID, final long sessionId) {
       final String key = this.getAuthSessionMapKey(sourceID, docID);
       new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            AuthenticationSession session = authSessions.get(key);
            if (session != null && session.id == sessionId) {
                session.wakeup(false);
                authSessions.remove(key);
            }
         }
       }, AUTH_TIMEOUT_DELAY);
    }
    
    public void processAuthenticationError(String sourceID, String docID, AuthenticationError authenticationError) {
        if (authenticationError instanceof AccountLoginError) {
            AccountLoginError loginError = (AccountLoginError)authenticationError;
            AuthenticationSession session = authSessions.get(getAuthSessionMapKey(sourceID, docID));
            if (session != null) { 
                session.wakeup(false);
            }
        } else if (authenticationError instanceof AccountCreationError) {
            AccountCreationError createError = (AccountCreationError)authenticationError;
            AuthenticationSession session = authSessions.get(getAuthSessionMapKey(sourceID, docID));
            if (session != null) { 
                session.wakeup(false);
            }
        }
    }
    
    private class AuthenticationSession {
        private String clientID;
        private String serverID;
        private String docID;
        private BigInteger a, b;
        private BigInteger A, B;
        private byte[] K;
        private char[] password;
        private final Condition authenticationComplete;
        private boolean authenticationCompleted = false;
        private boolean success = false;
        private final Lock authenticateLock;
        private long id;
        
        private AuthenticationSession(String clientID, String serverID, String docID){
            this.clientID = clientID;
            this.serverID = serverID;
            this.docID = docID;
            this.authenticateLock = new ReentrantLock(true);
            this.authenticationComplete = authenticateLock.newCondition();
            this.id = sessionIdCounter++;
        }
        
        private void wakeup(boolean success) {
            this.authenticateLock.lock();
            try {
                authenticationCompleted = true;
                this.success = success;
                authSessions.remove(getAuthSessionMapKey(serverID, docID));
                authenticationComplete.signal();
            } finally {
                authenticateLock.unlock();
            }
        }
        
        private void await() {
            setAuthTimeout(this.serverID, this.docID, this.id);
            authenticateLock.lock();
            try{
                while(!authenticationCompleted){
                    authenticationComplete.await();
                }
            } catch (InterruptedException ex) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(SRPAuthenticationTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "] authentication with " + serverID + ":" + docID, ex);
                }
            } finally{
                authenticateLock.unlock();
            }
        }
        
        /**
         * Cleans up secure data from memory.
         * -Specifically destroys keys securely.
         * @return Whether or not session cleanup was successful
         */
        private boolean cleanup() {
            if(a != null)   a.and(BigInteger.ZERO);
            if(b != null)   b.and(BigInteger.ZERO);
            if(A != null)   A.and(BigInteger.ZERO);
            if(B != null)   B.and(BigInteger.ZERO);
            
            if(K != null){
                Arrays.fill(K, (byte)0);
            }
            if(password != null)   Arrays.fill(password, '0');
            
            a = null; b = null; A = null; B = null; K = null; password = null;
            return true;
            
        }
    }
}
