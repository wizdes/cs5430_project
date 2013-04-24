/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

import application.encryption_demo.Profile;
import configuration.Constants;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
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
    private ConcurrentMap<String, String> pendingPINs = new ConcurrentHashMap<>();
    private ServerAuthenticationPersistantState persistantServerState;
    
    private Profile profile;
    
    private static final BigInteger n = new BigInteger(MODPGroups.MODP_GROUP_3072, 16);
    private static final BigInteger g = new BigInteger(MODPGroups.GENERATOR + "");
    
    public AuthenticationTransport(NetworkTransportInterface transport, SecureTransportInterface secureTransport, Profile profile) {
        this.profile = profile;
        this.transport = transport;
        this.secureTransport = secureTransport;
        this.transport.setAuthenticationTransport(this);
        this.persistantServerState = new ServerAuthenticationPersistantState();
    }
    
    public String generatePIN(String userID, String docID) {
        String PIN = KeyFactory.generatePIN();
        pendingPINs.put(userID + "-" + docID, PIN);
        Constants.log("generated pin for " + userID + "-" + docID + " -> " + this.getPIN(userID, docID));
        return PIN;
    }
    
    public String getPIN(String userID, String docID) {
        Constants.log("retrieve pin for " + userID + "-" + docID + " -> " + pendingPINs.get(userID + "-" + docID));
        return pendingPINs.get(userID + "-" + docID);
    }    
    
    public boolean initializeSRPAuthentication(String serverID, String docID, String password, String PIN){
        byte[] salt = KeyFactory.generateSalt();
        BigInteger x;
        try {
            x = new BigInteger(H(salt, password.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        BigInteger v = g.modPow(x, n);
        SecretKey pinKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN);
        SecretKey HMACKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN + "HMAC");
        
        InitAuth_Msg initMsg = new InitAuth_Msg(v, salt);
        return this.secureTransport.sendAESEncryptedMessage(serverID, docID, initMsg, pinKey, HMACKey);
    }
    
    public boolean authenticate(String serverID, String docID, String password){
        //Create new session state
        BigInteger a = generateEphemeralPrivateKey();     //a = ephemeral private key
        BigInteger A = g.modPow(a, n);                    //A = g^a = ephemeral public key
        AuthenticationSession session = new AuthenticationSession(profile.username, serverID, docID);
        session.password = password;
        session.a = a;
        session.A = A;
        authSessions.put(serverID + ":::" + docID, session);
        
        //Send initial message
        Auth_Msg1 initialMsg = new Auth_Msg1(profile.username, A, docID);
        transport.send(serverID, initialMsg);
        
        //Wait for authentication to complete
        session.authenticateLock.lock();
        try{
            while(!session.authenticationCompleted){
                session.authenticationComplete.await();
            }
        } catch (InterruptedException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "] authentication with " + serverID + ":" + docID, ex);
            }
            return false;
        } finally{
            session.authenticateLock.unlock();
        }
        return true;
    }
    
    public void processAuthenticationMessage(String sourceID, String docID, AuthenticationMessage receivedMsg){
        if(receivedMsg instanceof SRPSetupMessage){
            processSRPSetupMessage(sourceID, (SRPSetupMessage)receivedMsg);
        }
        else if(receivedMsg instanceof SRPAuthenticationMessage){
            try {
                processSRPMessage(sourceID, (SRPAuthenticationMessage)receivedMsg);
            } catch (InvalidSRPMessageException ex) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "] processing authentication from " + sourceID, ex);
                }
                //TODO: Handle error here, may need to wake up client
            } catch(InconsistentSessionKeyException ex){
                if(Constants.DEBUG_ON){
                    Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "] processing authentication from " + sourceID, ex);
                }
                //TODO: Handle different error here, may need to wake up client
            }
        }
    }
    
    private void processSRPSetupMessage(String sourceID, SRPSetupMessage receivedSetupMsg){
        InitAuth_Msg initMsg = (InitAuth_Msg)receivedSetupMsg;
        System.out.println("RAN");
        persistantServerState.addClientPasswordState(sourceID, initMsg.s, initMsg.v);
    }
    
    private void processSRPMessage(String sourceID, SRPAuthenticationMessage receivedSRPMsg) throws InvalidSRPMessageException, InconsistentSessionKeyException{
        System.out.println("processSRPMessage from " + sourceID);
        if(receivedSRPMsg instanceof Auth_Msg1){    //Server
            Auth_Msg1 msg1 = (Auth_Msg1)receivedSRPMsg;
            //Fetch Persistent state
            byte[] s = persistantServerState.getClientSalt(sourceID);           //s = salt
            BigInteger v = persistantServerState.getClientVerifier(sourceID);   //v = verifier = g^x
            if(s == null || v == null){
                throw new InvalidSRPMessageException("Client: " + sourceID + " has no account[s,v pair] on record.");
            }
            
            //Create new session
            BigInteger b = generateEphemeralPrivateKey();           //b
            BigInteger B = g.modPow(b, n);                          //g^b
            B.add(v);                                               //B = v + g^b
            AuthenticationSession session = new AuthenticationSession(sourceID, profile.username, msg1.docID);
            session.b = b;
            session.B = B;
            session.A = msg1.A;
            authSessions.put(sourceID + ":::" + msg1.docID, session);
            
            //Compute S(on the server) which is the common exponential value
            BigInteger u = new BigInteger(KeyFactory.generateNonce() + "");
            BigInteger S = v.modPow(u, n);                      //v^u
            S.multiply(session.A);                                 //A * v^u
            S.modPow(b, n);                                     //S = (A * v^u)^b mod n
            
            //Generate key from S
            session.K = H(S.toByteArray());                     //K = H(S)
            
            //Reply with Auth_Msg2
            Auth_Msg2 replyMsg = new Auth_Msg2(s, B, u, receivedSRPMsg.docID);
            transport.send(sourceID, replyMsg);
        } else if(receivedSRPMsg instanceof Auth_Msg2){ //Client
            Auth_Msg2 msg2 = (Auth_Msg2)receivedSRPMsg;
            AuthenticationSession session = authSessions.get(sourceID + ":::" + msg2.docID);
            if(session == null){
                throw new InvalidSRPMessageException("Server: " + sourceID + " has no session data stored on the client.");
            }
            session.B = msg2.B;
            
            //Compute S(on the client) which is the common exponential value
            BigInteger x = new BigInteger(H(session.password.toCharArray(), msg2.salt));
            BigInteger S = session.B.subtract(g.modPow(x, n));  //(B - g^x)
            BigInteger exp = session.a.add(msg2.u.multiply(x));                     //(a + u*x)
            S.modPow(exp, n);                                                       //S = (B - g^x)^(a + u*x)
            
            //Generate key from S
            session.K = H(S.toByteArray());                                         //K = H(S)
            
            //Send Auth_Msg3
            byte[] M1 = MAC(session.A.toByteArray(), session.B.toByteArray(), session.K);
            Auth_Msg3 msg3 = new Auth_Msg3(M1, msg2.docID);
            transport.send(sourceID, msg3);
        } else if(receivedSRPMsg instanceof Auth_Msg3){ //Server
            Auth_Msg3 msg3 = (Auth_Msg3)receivedSRPMsg;
            AuthenticationSession session = authSessions.get(sourceID + ":::" + msg3.docID);
            if(session == null){
                throw new InvalidSRPMessageException("Client: " + sourceID + " has no session data stored on the server.");
            }
            if(!Arrays.equals(msg3.M1, MAC(session.A.toByteArray(), session.B.toByteArray(), session.K))){
                throw new InconsistentSessionKeyException("Client's M1 doesn't match servers");
            }
            //Postcondition: Keys match according to the server
            
            byte[] M2 = MAC(session.A.toByteArray(), msg3.M1, session.K);
            Auth_Msg4 msg4 = new Auth_Msg4(M2, msg3.docID);
            transport.send(sourceID, msg4);
            
            //TODO: Ensure memory is zero'ed out and session is cleaned up
            session.cleanup();
            
            //TODO: Place session key K inside data structure.
        } else if(receivedSRPMsg instanceof Auth_Msg4){ //Client
            Auth_Msg4 msg4 = (Auth_Msg4)receivedSRPMsg;
            AuthenticationSession session = authSessions.get(sourceID + ":::" + msg4.docID);
            if(session == null){
                throw new InvalidSRPMessageException("Server: " + sourceID + " has no session data stored on the client.");
            }
            byte[] M1 = MAC(session.A.toByteArray(), session.B.toByteArray(), session.K);   //recompute M1 since didn't save it
            if(!Arrays.equals(msg4.M2, MAC(session.A.toByteArray(), M1, session.K))){
                throw new InconsistentSessionKeyException("Server's M2 doesn't match clients");
            }
            //Postcondition: Keys match according to the client
            
            //TODO: Ensure memory is zero'ed out and session is cleaned up
            cleanupSession(sourceID, msg4.docID);
            
            //TODO: Place session key K inside data structure.
            
            //Wake up client
            session.authenticateLock.lock();
            try {
                session.authenticationCompleted = true;
                session.authenticationComplete.signal();
            } finally {
                session.authenticateLock.unlock();
            }
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
    private byte[] H(char[] pass, byte[] salt){
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec ks = new PBEKeySpec(pass, salt, 36359, 128);
            SecretKey s = f.generateSecret(ks);
            Key k = new SecretKeySpec(s.getEncoded(),"AES");
            return k.getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    private byte[] MAC(byte[] A, byte[] B, byte[] K){
        return H(A, B, K);
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
    
    private class AuthenticationSession {
        private String clientID;
        private String serverID;
        private String docID;
        private BigInteger a, b;
        private BigInteger A, B;
        private byte[] K = null;
        private String password;
        private final Condition authenticationComplete;
        private boolean authenticationCompleted = false;
        private final Lock authenticateLock;
        private AuthenticationSession(String clientID, String serverID, String docID){
            this.clientID = clientID;
            this.serverID = serverID;
            this.docID = docID;
            this.authenticateLock = new ReentrantLock(true);
            this.authenticationComplete = authenticateLock.newCondition();
        }
        
        /**
         * Cleans up secure data from memory.
         * -Specifically destroys keys securely.
         * @return Whether or not session cleanup was successful
         */
        private boolean cleanup(){   //Is this enough?!?!?!?
            Destroyable destroyableA = (Destroyable)A;
            try {
                destroyableA.destroy();    //Or: ephemeralPublicKey.and(BigInteger.ZERO);
                
            } catch (DestroyFailedException ex) {
                Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }
    }
}
