package security_layer;


import application.encryption_demo.CommunicationInterface;
import transport_layer.discovery.DiscoveryMessage;
import application.encryption_demo.Messages.Message;
import application.encryption_demo.Profile;
import configuration.Constants;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import security_layer.authentications.Authentication;
import transport_layer.discovery.DiscoveryResponseMessage;
import transport_layer.discovery.DiscoveryTransport;
import transport_layer.files.FileHandler;
import transport_layer.files.FileTransportInterface;
import transport_layer.network.NetworkTransportInterface;

/**
 *
 * @author Patrick C. Berens
 */
public class SecureTransport implements SecureTransportInterface{
    private FileTransportInterface fileTransport = new FileHandler();
    private NetworkTransportInterface networkTransport;
    private DiscoveryTransport discoveryTransport;
    private CommunicationInterface communication;
//    private Authentications authInstance;
    private ConcurrentMap<String, Long> lastReceived = new ConcurrentHashMap<>();    //<Ident, Counter>
    private AtomicLong counter = new AtomicLong();
//    private ConcurrentMap<String, EncryptedAESMessage> pendingHumanAuth = new ConcurrentHashMap<>();
    private SessionKeys keys = new SessionKeys();
    private Authentication authentication;
    
    public SecureTransport(NetworkTransportInterface networkTransport, 
                           Authentication authentication, 
                           CommunicationInterface communication) {               
        this.networkTransport = networkTransport;
        this.networkTransport.setSecureTransport(this);
        this.discoveryTransport = new DiscoveryTransport(networkTransport);
        this.communication = communication;
        this.authentication = authentication;
    }
    
    @Override
    public void addPeer(String peerIdent, String host, int port){
        networkTransport.addPeer(peerIdent, host, port);
    }
    
//    @Override
//    public boolean authenticate(String machineIdent) {
//        
//        Constants.log(Profile.username + " : authenticating");
//        
//        //If machine has been authenticated or trying to authenticate with itself, return
//        if (authInstance.hasAuthenticated(machineIdent) || machineIdent.equals(Profile.username)) {
//            Constants.log(Profile.username + " : has already authenticated with " + machineIdent);
//            return true;
//        }
//        final Lock authenticateLock = new ReentrantLock(true);
//
//        MachineAuthenticationMessage msg = authInstance.constructInitialAuthMessage();
//        Condition authenticationComplete = authenticateLock.newCondition();
//        
//        try {
//            authenticateLock.lock();
//            authInstance.addAuthentication(machineIdent, msg, authenticationComplete, authenticateLock);
//            sendRSAEncryptedMessage(machineIdent, msg);
//            authenticationComplete.await();
//            authInstance.removeAuthentication(machineIdent);
//        } catch (InterruptedException ex) {
//            if(Constants.DEBUG_ON){
//                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "]", ex);
//            }
//            return false;
//        }
//        finally{
//            authenticateLock.unlock();
//            return true;
//        }
//    }
        
    @Override
    public boolean sendAESEncryptedMessage(String destination, String docID, Message m) {
        return sendAESEncryptedMessage(destination, docID, m, keys.getSessionKey(destination, docID), keys.getHmacKey(destination, docID));
    }
    
    @Override
    public boolean sendAESEncryptedMessage(String destination, String docID, Message m, SecretKey secretKey, SecretKey HMACKey) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Sending " + EncryptedAESMessage.class.getName() + " to " + destination + ".");
        }
        byte[] iv = CipherFactory.generateRandomIV();
        
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(secretKey, iv);
        
        Serializable sendingMessage = new ApplicationMessage(m, counter.incrementAndGet()); 
        try {
            SealedObject encryptedObject = new SealedObject(sendingMessage, cipher);
            byte[] hmac = CipherFactory.HMAC(HMACKey, encryptedObject);
            
            EncryptedAESMessage encryptedMessage;
            if (m instanceof HumanAuthenticationMessage) {
                encryptedMessage = new EncryptedAuthenticationMessage(encryptedObject, iv, hmac);
            } else {
                encryptedMessage = new EncryptedAESMessage(encryptedObject, iv, hmac);
            }
            
            return networkTransport.send(destination, docID, encryptedMessage);
        } catch (IOException | IllegalBlockSizeException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "]", ex);
            }
            return false;
        }
    }

//    @Override
//    public boolean sendRSAEncryptedMessage(String destination, Message m) {
//        if(Constants.DEBUG_ON){
//            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Sending " + EncryptedRSAMessage.class.getName() + " to " + destination + ".");
//        }
//        byte[] iv = new byte[16];
//        PublicKey publicKey = Profile.keys.getPublicKey(destination);
//        if (publicKey == null) {
//            if(Constants.DEBUG_ON){
//                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "] No public key found for " + destination + ".");
//            }
//            return false;
//        }
//        Cipher cipher = CipherFactory.constructRSAEncryptionCipher(publicKey);
//        try {
//            SealedObject encryptedObject = new SealedObject(m, cipher);
//            Signature signature = Signature.getInstance(CipherFactory.SIGNING_ALGORITHM);
//            SignedObject signedObject = new SignedObject(encryptedObject, (PrivateKey)Profile.keys.signingKey, signature);
//
//            EncryptedMessage encryptedMessage = new EncryptedRSAMessage(signedObject);
//
//            boolean wasSuccessful = networkTransport.send(destination, encryptedMessage);
//            return wasSuccessful;
//        } catch (IOException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
//            if(Constants.DEBUG_ON){
//                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "]", ex);
//            }
//            return false;
//        }
//    }

    @Override
    public boolean processEncryptedMessage(String sourceOfMessage, String docID, EncryptedMessage encryptedMessage) throws InvalidHMACException {
        boolean success = false;
        SecretKey secretKey = null;
        SecretKey HMACKey = null;
        if (Constants.DEBUG_ON) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Processing " + EncryptedMessage.class.getName() + " from " + sourceOfMessage + ".");
        }
        SealedObject encryptedObject;

        Cipher cipher = null;
        try {
            Constants.log("AES");
            
            EncryptedAESMessage aesMessage = (EncryptedAESMessage) encryptedMessage;
            encryptedObject = aesMessage.encryptedObject;
            
            if (encryptedMessage instanceof EncryptedAuthenticationMessage) {
                String PIN = this.authentication.getPIN(sourceOfMessage, docID);
                secretKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN);
                HMACKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN + "HMAC");
            } else {
                HMACKey = keys.getHmacKey(sourceOfMessage, docID);
                secretKey = keys.getSessionKey(sourceOfMessage, docID);
            }

            byte[] hmac = CipherFactory.HMAC(HMACKey, encryptedObject);

            if (secretKey == null || HMACKey == null) {    //Never enters because of null check above, but leave since someone might remove that
                if (Constants.DEBUG_ON) {
                    Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "] No symemetric key found for " + sourceOfMessage + ".");
                }
                return false;
            }
            
            if (!Arrays.equals(hmac, aesMessage.HMAC)) {
                throw new InvalidHMACException("[User: " + Profile.username + "] Invalid HMAC from " + sourceOfMessage + ".");
            }

            
            cipher = CipherFactory.constructAESDecryptionCipher(secretKey, aesMessage.iv);

            Object decryptedObj = encryptedObject.getObject(cipher);
            if (Constants.DEBUG_ON) {
                Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Processing decrypted " + ApplicationMessage.class.getName() + " from " + sourceOfMessage + ".");
            }
            ApplicationMessage appMessage = (ApplicationMessage) decryptedObj;
            Long currentCounter = lastReceived.get(sourceOfMessage);
            if (currentCounter == null || appMessage.counter > currentCounter) {
                communication.depositMessage(appMessage.message);
                success = true;
                lastReceived.put(sourceOfMessage, appMessage.counter);
            } else {
                if (Constants.DEBUG_ON) {
                    Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "] Invalid counter on " + ApplicationMessage.class.getName() + " from " + sourceOfMessage + ".");
                }
            }
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException ex) {
            if (Constants.DEBUG_ON) {
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "]", ex);
            }
        }
        
        return success;
    }
    
    @Override
    public void processDiscoveryResponse(DiscoveryResponseMessage msg){
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Processing " + DiscoveryResponseMessage.class.getName() + " from " + msg.owner + ".");
        }
        
        communication.updatePeers(msg.owner, msg.ip, msg.port, msg.documents, false);
    }
    
    @Override
    public  void processDiscoveryMessage(DiscoveryMessage dm) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Processing " + DiscoveryMessage.class.getName() + " from " + dm.sourceID + ".");
        }
        if(!dm.sourceID.equals(Profile.username) && Profile.documentsOpenForDiscovery.size() > 0){
            List<String> documentNames = new ArrayList<>(Profile.documentsOpenForDiscovery);   //must copy here, possibly due to transient flag
            DiscoveryResponseMessage responseMessage = new DiscoveryResponseMessage(Profile.username, 
                    Profile.host, Profile.port, documentNames);
            networkTransport.addPeer(dm.sourceID, dm.sourceIP, dm.sourcePort);
            networkTransport.send(dm.sourceID, responseMessage);
        }
    }
    
//    @Override
//    public boolean processPlaintextMessage(String sourceOfMessage, PlaintextMessage msg) {
//        if(Constants.DEBUG_ON){
//            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Processing " + PlaintextMessage.class.getName() + " from " + sourceOfMessage + ".");
//        }
//        if (msg.m instanceof DiscoveryMessage) {
//            processDiscoveryMessage((DiscoveryMessage)msg.m);
//        }
//        return true;
//    }
    
    @Override
    public boolean writeEncryptedFile(String filename, String password, Message contents) {
        try {
            //Encrypt file with personal key
            byte[] iv = CipherFactory.generateRandomIV();
            byte[] hmacSalt = KeyFactory.generateSalt();
            byte[] salt = KeyFactory.generateSalt();
            
            SecretKey key = KeyFactory.generateSymmetricKey(password, salt);
            Cipher cipher = CipherFactory.constructAESEncryptionCipher(key, iv);
            SealedObject encryptedObject = new SealedObject(contents, cipher);
            
            
            Key hmacKey = KeyFactory.generateSymmetricKey(password, hmacSalt);
            byte[] hmac = CipherFactory.HMAC(hmacKey, encryptedObject);
            
            EncryptedAESFile file = new EncryptedAESFile(encryptedObject, iv, hmac, hmacSalt, salt);
            return fileTransport.writeFile(filename, file);
            
        } catch (IOException | IllegalBlockSizeException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "]", ex);
            }
            return false;
        }   
    }

    @Override
    public Message readEncryptedFile(String filename, String password) {
        if (!new File(filename).exists()) {
            return null;
        }
        try {
            EncryptedAESFile file = (EncryptedAESFile)fileTransport.readFile(filename);
            
            //Verify hmac
            Key hmacKey = KeyFactory.generateSymmetricKey(password, file.hmacSalt);
            SecretKey key = KeyFactory.generateSymmetricKey(password, file.salt);
            
            byte[] hmac = CipherFactory.HMAC(hmacKey, file.encryptedObject);        
            if (!Arrays.equals(hmac, file.HMAC)) {
                throw new SignatureException("[User: " + Profile.username + "] Invalid HMAC");
            }
            
            Cipher cipher = CipherFactory.constructAESDecryptionCipher(key, file.iv);
            return (Message)file.encryptedObject.getObject(cipher);
        } catch (SignatureException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "] Bad signature reading " + filename + ".");
            }
            return null;
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "]", ex);
            }
            return null;
        }
    }

    @Override
    public String readUnencryptedFile(String filename) {
        return fileTransport.openUnserializedFile(filename);
    }
    
    @Override
    public void shutdown() {
        this.networkTransport.shutdown();
    }


    @Override
    public ArrayList<Integer> findPeers(int myID) {
        ArrayList<Integer> peers = new ArrayList<Integer>();
        peers.add(0);
        peers.add(1);
        peers.add(2);
        return peers;
    }

//    @Override
//    public boolean initializeHumanAuthenticate(String destination) {
//        if(Constants.DEBUG_ON){
//            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Starting initial human authentication with " + destination + ".");
//        }
//
//        final Lock authenticateLock = new ReentrantLock(true);
//        
//        HumanAuthenticationMessage msg = authInstance.constructInitialHAuthMessage();
//        Condition authenticationComplete = authenticateLock.newCondition();
//        
//        try {
//            authenticateLock.lock();
//            // send the message and wait
//            authInstance.addAuthentication(destination, msg, authenticationComplete, authenticateLock);
//            sendPlainTextMessage(destination, msg);
//        }
//        finally{
//            authenticateLock.unlock();
//            return true;
//        }
//    }

    @Override
    public void broadcastDiscovery() {
        discoveryTransport.broadcastDiscovery();
    }
    
//    @Override
//    public boolean addPIN(String ownerID, String PIN) {
//        if (!this.pendingHumanAuth.containsKey(ownerID)) {
//            if(Constants.DEBUG_ON){
//                Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Haven't yet received PIN message from owner: " + ownerID + ".");
//            }
//            return false;
//        }
//        
//        SecretKey pinKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN);
//        SecretKey HMACKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN + "HMAC");
//        Profile.keys.addSymmetricKey(ownerID, pinKey);
//        Profile.keys.addHMACKey(ownerID, HMACKey);
//        
//        EncryptedAESMessage m = this.pendingHumanAuth.get(ownerID);
//        try {
//            boolean success = processEncryptedMessage(ownerID, m);
//            if(success){
//                Profile.keys.removeSymmetricKey(ownerID);
//                Profile.keys.removeHMACKey(ownerID);
//                pendingHumanAuth.remove(ownerID);
//            }
//            return success;
//        } catch (InvalidHMACException ex) {
//            Profile.keys.removeSymmetricKey(ownerID);
//            Profile.keys.removeHMACKey(ownerID);
//            if(Constants.DEBUG_ON){
//                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + Profile.username + "] Failed to decrypt PIN message from owner: " + ownerID + ".", ex);
//            }
//            return false;
//        }
//    }

    @Override
    public boolean sendPlainTextMessage(String destination, Message m) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Sending " + PlaintextMessage.class.getName() + " to " + destination + ".");
        }
        PlaintextMessage sendMsg = new PlaintextMessage();
        sendMsg.m = m;
        return networkTransport.send(destination, sendMsg);
    }

//    @Override
//    public void displayPIN(String ID, String PIN) {
//        communication.displayPIN(ID, PIN);
//    }
}
