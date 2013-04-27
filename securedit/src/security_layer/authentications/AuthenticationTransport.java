/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

import security_layer.Profile;
import configuration.Constants;
import document.NetworkDocumentInterface;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import security_layer.KeyFactory;
import security_layer.SecureTransportInterface;
import transport_layer.network.NetworkTransportInterface;

/**
 *
 * @author Patrick
 */
public class AuthenticationTransport {
    private NetworkTransportInterface transport;
    private SecureTransportInterface secureTransport;
    private ConcurrentMap<String, AuthenticationSession> authSessions = new ConcurrentHashMap<>();
    private ConcurrentMap<String, char[]> pendingPINs = new ConcurrentHashMap<>();
    private ConcurrentMap<String, NetworkDocumentInterface> docInstances;
    private Profile profile;
    
    private static final BigInteger n = new BigInteger(MODPGroups.MODP_GROUP_3072, 16);
    private static final BigInteger g = new BigInteger(MODPGroups.GENERATOR + "");
    
    public AuthenticationTransport(NetworkTransportInterface transport, 
                                   SecureTransportInterface secureTransport, 
                                   Profile profile,
                                   ConcurrentMap<String, NetworkDocumentInterface> docInstances) {
        this.profile = profile;
        this.transport = transport;
        this.secureTransport = secureTransport;
        this.docInstances = docInstances;
        this.transport.setAuthenticationTransport(this);
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
                Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public boolean authenticate(String serverID, String docID, char[] password){
        // Create new session state
        BigInteger a = generateEphemeralPrivateKey();     //a = ephemeral private key
        BigInteger A = g.modPow(a, n);                    //A = g^a = ephemeral public key
        AuthenticationSession session = new AuthenticationSession(profile.username, serverID, docID);
        session.password = password;
        session.a = a;
        session.A = A;
        System.out.println("putting session for " + getAuthSessionMapKey(serverID, docID));
        authSessions.put(getAuthSessionMapKey(serverID, docID), session);
        
        // Send initial message
        System.out.println("m1" + docID);
        Auth_Msg1 initialMsg = new Auth_Msg1(profile.username, A, docID);
        transport.send(serverID, docID, initialMsg);
        System.out.println("m1" + docID);
        
        //Wait for authentication to complete
        session.await();
        
        return session.success;
    }
    
    public void processAuthenticationMessage(String sourceID, String docID, AuthenticationMessage receivedMsg){
        System.out.println("processAuthenticationMessage");
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
    
    private void processSRPMessage(String sourceID, SRPAuthenticationMessage receivedSRPMsg) throws InvalidSRPMessageException, InconsistentSessionKeyException{
        ServerAuthenticationPersistantState persistantServerState;
        
        if (receivedSRPMsg instanceof Auth_Msg1) {    //Server
            Auth_Msg1 msg1 = (Auth_Msg1)receivedSRPMsg;
            //Fetch Persistent state
            persistantServerState = docInstances.get(msg1.docID).getServerAuthenticationPersistantState();
            byte[] s = persistantServerState.getClientSalt(sourceID);           //s = salt
            BigInteger v = persistantServerState.getClientVerifier(sourceID);   //v = verifier = g^x
            if (s == null || v == null){
                throw new InvalidSRPMessageException("Client: " + sourceID + " has no account[s,v pair] on record.");
            }
            
            //Create new session
            BigInteger b = generateEphemeralPrivateKey();           //b
            BigInteger B = g.modPow(b, n);                          //g^b
            B = B.add(v);                                               //B = v + g^b
            AuthenticationSession session = new AuthenticationSession(sourceID, profile.username, msg1.docID);
            session.b = b;
            session.B = B;
            session.A = msg1.A;
            authSessions.put(getAuthSessionMapKey(sourceID, msg1.docID), session);
            
            //Compute S(on the server) which is the common exponential value
            BigInteger u = new BigInteger(KeyFactory.generateNonce() + "");
//            System.out.println("A: " + Arrays.toString(session.A.toByteArray()));
//            System.out.println("B: " + Arrays.toString(session.B.toByteArray()));
//            System.out.println("u: " + Arrays.toString(u.toByteArray()));
//            System.out.println("v: " + Arrays.toString(v.toByteArray()));
//            System.out.println("n: " + Arrays.toString(n.toByteArray()));
//            System.out.println("g: " + Arrays.toString(g.toByteArray()));
            BigInteger S = v.modPow(u, n);                      //v^u
            S = session.A.multiply(S);                                 //A * v^u
            S = S.modPow(b, n);                                     //S = (A * v^u)^b mod n
//            System.out.println("S: " + Arrays.toString(S.toByteArray()));
            
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
            
//            System.out.println("A: " + Arrays.toString(session.A.toByteArray()));
//            System.out.println("B: " + Arrays.toString(session.B.toByteArray()));
//            System.out.println("u: " + Arrays.toString(msg2.u.toByteArray()));
            //System.out.println("v: " + Arrays.toString(.toByteArray()));
//            System.out.println("n: " + Arrays.toString(n.toByteArray()));
//            System.out.println("g: " + Arrays.toString(g.toByteArray()));
            //Compute S(on the client) which is the common exponential value
            BigInteger x = new BigInteger(KeyFactory.generateSymmetricKey(session.password, msg2.salt).getEncoded());
            BigInteger S = session.B.subtract(g.modPow(x, n));  //(B - g^x)
            BigInteger exp = session.a.add(msg2.u.multiply(x));                     //(a + u*x)
            S = S.modPow(exp, n);                                                       //S = (B - g^x)^(a + u*x)
//            System.out.println("S: " + Arrays.toString(S.toByteArray()));
            
            //Generate key from S
            session.K = H(S.toByteArray());                                         //K = H(S)
            
            //Send Auth_Msg3
//            System.out.println("K: " + Arrays.toString(session.K));
            byte[] M1 = H(session.A.toByteArray(), session.B.toByteArray(), session.K);
//            System.out.println("Client's M1: " + Arrays.toString(M1));
            Auth_Msg3 msg3 = new Auth_Msg3(M1, msg2.docID);
            transport.send(sourceID, msg2.docID, msg3);
        } else if(receivedSRPMsg instanceof Auth_Msg3){ //Server
            Auth_Msg3 msg3 = (Auth_Msg3)receivedSRPMsg;
            AuthenticationSession session = authSessions.get(getAuthSessionMapKey(sourceID, msg3.docID));
            if(session == null){
                throw new InvalidSRPMessageException("Client: " + sourceID + " has no session data stored on the server.");
            }
            //System.out.println("Client's M1: " + Arrays.toString(msg3.M1));
//            System.out.println("K: " + Arrays.toString(session.K));
            byte[] serversM1 = H(session.A.toByteArray(), session.B.toByteArray(), session.K);
//            System.out.println("Server's M1: " + Arrays.toString(serversM1));
            
            if(!Arrays.equals(msg3.M1, serversM1)){
                throw new InconsistentSessionKeyException("Client's M1 doesn't match servers");
            }
            //Postcondition: Keys match according to the server
            
            //Place session key K inside data structure.
            //   ASK ELENANOR ABOUT THIS
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
            //   ASK ELENANOR ABOUT THIS
            produceAndSaveSessionKeys(sourceID, msg4.docID, session.K);
            
            //Zero memory for session
            cleanupSession(sourceID, msg4.docID);
            
            //Wake up client
            session.wakeup(true);
        }
    }
    private void produceAndSaveSessionKeys(String sourceID, String docID, byte[] key) {
        try {
            SecretKey sessionKey = KeyFactory.generateSymmetricKey(new String(key).toCharArray(), "SESSION_KEY".getBytes("UTF-16"));
            SecretKey hmacKey = KeyFactory.generateSymmetricKey(new String(key).toCharArray(), "HMAC".getBytes("UTF-16"));
            profile.keys.addSessionKey(sourceID, docID, sessionKey);
            profile.keys.addHmacKey(sourceID, docID, hmacKey);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
//    private byte[] H(char[] pass, byte[] salt){
//        try {
//            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//            KeySpec ks = new PBEKeySpec(pass, salt, 36359, 128);
//            SecretKey s = f.generateSecret(ks);
//            Key k = new SecretKeySpec(s.getEncoded(),"AES");
//            return k.getEncoded();
//        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
//            if(Constants.DEBUG_ON){
//                Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            return null;
//        }
//    }
//    private byte[] MAC(byte[] A, byte[] B, byte[] K){
//        return H(A, B, K);
//    }
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
            Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }
    
    /**
     * Cleans up secure data in memory for the given session.
     * @return Whether or not the session had completed authentication at the time of removal
     */
    private boolean cleanupSession(String id, String docID){
        AuthenticationSession session = authSessions.get(id + ":::" + docID);
        session.authenticateLock.lock();
        try{
            session.cleanup();
            authSessions.remove(id + ":::" + docID);
        } finally{
            session.authenticateLock.unlock();
        }
        return session.authenticationCompleted;
    }

    public void processAuthenticationError(String sourceID, String docID, AuthenticationError authenticationError) {
        System.out.println("processAuthenticationError");
        if (authenticationError instanceof AccountLoginError) {
            AccountLoginError loginError = (AccountLoginError)authenticationError;
            AuthenticationSession session = authSessions.get(getAuthSessionMapKey(sourceID, docID));
            session.wakeup(false);
        } else if (authenticationError instanceof AccountCreationError) {
            System.out.println("AccountCreationError");
            AccountCreationError createError = (AccountCreationError)authenticationError;
            AuthenticationSession session = authSessions.get(getAuthSessionMapKey(sourceID, docID));
            session.wakeup(false);
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
        
        private AuthenticationSession(String clientID, String serverID, String docID){
            this.clientID = clientID;
            this.serverID = serverID;
            this.docID = docID;
            this.authenticateLock = new ReentrantLock(true);
            this.authenticationComplete = authenticateLock.newCondition();
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
            authenticateLock.lock();
            try{
                while(!authenticationCompleted){
                    authenticationComplete.await();
                }
            } catch (InterruptedException ex) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "] authentication with " + serverID + ":" + docID, ex);
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
//                Destroyable destroyableK = (Destroyable)K;
//                try {
//                    destroyableK.destroy();
//                } catch (DestroyFailedException ex) {
//                    Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
            if(password != null)   Arrays.fill(password, '0');
            
            a = null; b = null; A = null; B = null; K = null; password = null;
            return true;
            
        }
    }
}
