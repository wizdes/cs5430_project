package security_layer;


import application.encryption_demo.CommunicationInterface;
import security_layer.authentications.AccountCreationError;
import transport_layer.discovery.DiscoveryMessage;
import application.encryption_demo.Message;
import configuration.Constants;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
import javax.swing.JComboBox;
import security_layer.authentications.AuthenticationTransport;
import security_layer.authentications.AuthenticationMessage;
import security_layer.authentications.SRPSetupMessage;
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
    private AuthenticationTransport authentication;
    private Profile profile;
    
    public SecureTransport(NetworkTransportInterface networkTransport, 
                           AuthenticationTransport authentication, 
                           CommunicationInterface communication,
                           Profile profile) {               
        this.profile = profile;
        this.profile.setSessionKeys(keys);
        this.networkTransport = networkTransport;
        this.networkTransport.setSecureTransport(this);
        this.discoveryTransport = new DiscoveryTransport(networkTransport, profile);
        this.communication = communication;
        this.authentication = authentication;
    }
    
    @Override
    public void addPeer(String peerIdent, String host, int port){
        networkTransport.addPeer(peerIdent, host, port);
    }
    
    @Override
    public boolean sendAESEncryptedMessage(String destination, String docID, Message m) {
        return sendAESEncryptedMessage(destination, docID, m, keys.getSessionKey(destination, docID), keys.getHmacKey(destination, docID));
    }
    
    @Override
    public boolean sendAESEncryptedMessage(String destination, String docID, Message m, SecretKey secretKey, SecretKey HMACKey) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + profile.username + "] Sending " + EncryptedAESMessage.class.getName() + " to " + destination + ".");
        }
        byte[] iv = CipherFactory.generateRandomIV();
        
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(secretKey, iv);
        
        Serializable sendingMessage = new ApplicationMessage(m, counter.incrementAndGet()); 
        try {
            SealedObject encryptedObject = new SealedObject(sendingMessage, cipher);
            byte[] hmac = CipherFactory.HMAC(HMACKey, encryptedObject);
            
            EncryptedAESMessage encryptedMessage;
            if (m instanceof AuthenticationMessage) {
                Constants.log("sending AuthenticationMessage");
                encryptedMessage = new EncryptedAESAuthenticationMessage(encryptedObject, iv, hmac);
            } else {
                Constants.log("sending EncryptedAESMessage");
                encryptedMessage = new EncryptedAESMessage(encryptedObject, iv, hmac);
            }
            return networkTransport.send(destination, docID, encryptedMessage);
        } catch (IOException | IllegalBlockSizeException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "]", ex);
            }
            return false;
        }
    }
    
    private void replyAuthFailure(String sourceOfMessage, String docID) {
        Constants.log("failed to authenticate " + sourceOfMessage + " for " + docID);
        AccountCreationError error = new AccountCreationError(docID, "");
        this.sendPlainTextMessage(sourceOfMessage, docID, error);
    }
    
    @Override
    public boolean processEncryptedMessage(String sourceOfMessage, String docID, EncryptedMessage encryptedMessage) {
        boolean success = false;
        SecretKey secretKey;
        SecretKey HMACKey;
        if (Constants.DEBUG_ON) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + profile.username + "] Processing " + EncryptedMessage.class.getName() + " from " + sourceOfMessage + ".");
        }
        SealedObject encryptedObject;

        Cipher cipher;
        try {
            EncryptedAESMessage aesMessage = (EncryptedAESMessage) encryptedMessage;
            encryptedObject = aesMessage.encryptedObject;
            
            if (encryptedMessage instanceof EncryptedAESAuthenticationMessage) {
                char[] PIN = this.authentication.getPIN(sourceOfMessage, docID);
                if (PIN == null) {
                    replyAuthFailure(sourceOfMessage, docID);
                    return false;
                }
                try {
                    secretKey = KeyFactory.generateSymmetricKey(PIN, "PIN".getBytes("UTF-16"));
                    HMACKey = KeyFactory.generateSymmetricKey(PIN, "HMAC".getBytes("UTF-16"));
                } catch (UnsupportedEncodingException ex) {
                    return false;
                }
            } else {    //Application message
                HMACKey = keys.getHmacKey(sourceOfMessage, docID);
                secretKey = keys.getSessionKey(sourceOfMessage, docID);
            }

            byte[] hmac = CipherFactory.HMAC(HMACKey, encryptedObject);

            if (secretKey == null || HMACKey == null) {
                if (Constants.DEBUG_ON) {
                    Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "] No symemetric key found for " + sourceOfMessage + ".");
                }
                Constants.log("returning false");
                return false;
            }
            
            if (!Arrays.equals(hmac, aesMessage.HMAC)) {
                replyAuthFailure(sourceOfMessage, docID);
                return false;
            }

            
            cipher = CipherFactory.constructAESDecryptionCipher(secretKey, aesMessage.iv);

            Object decryptedObj = encryptedObject.getObject(cipher);
            if (Constants.DEBUG_ON) {
                Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + profile.username + "] Processing decrypted " + ApplicationMessage.class.getName() + " from " + sourceOfMessage + ".");
            }
            
            ApplicationMessage appMessage = (ApplicationMessage) decryptedObj;
            Long currentCounter = lastReceived.get(sourceOfMessage);
            if (currentCounter == null || appMessage.counter > currentCounter) {
                if (appMessage.message instanceof AuthenticationMessage) {
                    Constants.log("instanceof AuthenticationMessage");
                    AuthenticationMessage msg = (AuthenticationMessage) appMessage.message;
                    this.authentication.processAuthenticationMessage(sourceOfMessage, docID, msg);
                    success = true;
                } else if (appMessage.message instanceof Message) {
                    Constants.log("instanceof ApplicationMessage");
                    communication.depositMessage(appMessage.message);
                    success = true;
                }
                lastReceived.put(sourceOfMessage, appMessage.counter);
            } else {
                if (Constants.DEBUG_ON) {
                    Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "] Invalid counter on " + ApplicationMessage.class.getName() + " from " + sourceOfMessage + ".");
                }
            }
            
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException ex) {
            if (Constants.DEBUG_ON) {
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "]", ex);
            }
        }
        
        return success;
    }
    
    @Override
    public void processDiscoveryResponse(DiscoveryResponseMessage msg){
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + profile.username + "] Processing " + DiscoveryResponseMessage.class.getName() + " from " + msg.owner + ".");
        }
        
        communication.updatePeers(msg.owner, msg.ip, msg.port, msg.documents);
    }
    
    @Override
    public  void processDiscoveryMessage(DiscoveryMessage dm) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + profile.username + "] Processing " + DiscoveryMessage.class.getName() + " from " + dm.sourceID + ".");
        }
        if(!dm.sourceID.equals(profile.username) && profile.documentsOpenForDiscovery.size() > 0){
            List<String> documentNames = new ArrayList<>(profile.documentsOpenForDiscovery);   //must copy here, possibly due to transient flag
            DiscoveryResponseMessage responseMessage = new DiscoveryResponseMessage(profile.username, 
                    profile.host, profile.port, documentNames);
            networkTransport.addPeer(dm.sourceID, dm.sourceIP, dm.sourcePort);
            networkTransport.send(dm.sourceID, responseMessage);
        }
    }
    
    @Override
    public boolean writeEncryptedFile(String filename, char[] password, Message contents) {
        try {
            //Encrypt file with personal key
            byte[] iv = CipherFactory.generateRandomIV();
            byte[] hmacSalt = KeyFactory.generateSalt();
            byte[] salt = KeyFactory.generateSalt();
            
            SecretKey key = KeyFactory.generateSymmetricKey(password, salt);
            SecretKey hmacKey = KeyFactory.generateSymmetricKey(password, hmacSalt);
            cleanupPassword(password);
            
            Cipher cipher = CipherFactory.constructAESEncryptionCipher(key, iv);
            SealedObject encryptedObject = new SealedObject(contents, cipher);
            byte[] hmac = CipherFactory.HMAC(hmacKey, encryptedObject);
            
            EncryptedAESFile file = new EncryptedAESFile(encryptedObject, iv, hmac, hmacSalt, salt);
            return fileTransport.writeFile(filename, file);
            
        } catch (IOException | IllegalBlockSizeException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "]", ex);
            }
            return false;
        }   
    }

    @Override
    public Message readEncryptedFile(String filename, char[] password) {
        if (!new File(filename).exists()) {
            return null;
        }
        try {
            EncryptedAESFile file = (EncryptedAESFile)fileTransport.readFile(filename);
            
            //Verify hmac
            SecretKey hmacKey = KeyFactory.generateSymmetricKey(password, file.hmacSalt);
            SecretKey key = KeyFactory.generateSymmetricKey(password, file.salt);
            cleanupPassword(password);
            
            byte[] hmac = CipherFactory.HMAC(hmacKey, file.encryptedObject);        
            if (!Arrays.equals(hmac, file.HMAC)) {
                throw new SignatureException("[User: " + profile.username + "] Invalid HMAC");
            }
            
            Cipher cipher = CipherFactory.constructAESDecryptionCipher(key, file.iv);
            return (Message)file.encryptedObject.getObject(cipher);
        } catch (SignatureException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "] Bad signature reading " + filename + ".");
            }
            return null;
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, "[User: " + profile.username + "]", ex);
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
    public void broadcastDiscovery() {
        discoveryTransport.broadcastDiscovery();
    }
    
    @Override
    public boolean sendPlainTextMessage(String destination, Message m) {
        return this.sendPlainTextMessage(destination, null, m);
    }

//    @Override
//    public void displayPIN(String ID, String PIN) {
//        communication.displayPIN(ID, PIN);
//    }

    @Override
    public void setAuthenticationTransport(AuthenticationTransport a) {
        this.authentication = a;
    }
    private void cleanupPassword(char[] password){
        if(password != null){
            Arrays.fill(password, '0');
        }
    }

    @Override
    public boolean sendPlainTextMessage(String destination, String docID, Message m) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(SecureTransport.class.getName()).log(Level.INFO, "[User: " + profile.username + "] Sending " + PlaintextMessage.class.getName() + " to " + destination + ".");
        }
        PlaintextMessage sendMsg = new PlaintextMessage();
        sendMsg.message = m;
        return networkTransport.send(destination, docID, sendMsg);
    }
}
