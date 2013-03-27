package security_layer;


import application.encryption_demo.CommunicationInterface;
import application.encryption_demo.Messages.Message;
import application.encryption_demo.DiscoveredPeers;
import application.encryption_demo.DiscoveredPeers.Peer;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.swing.*;
import transport_layer.discovery.DiscoveryResponseMessage;
import transport_layer.discovery.DiscoveryTransport;
import transport_layer.files.FileHandler;
import transport_layer.files.FileTransportInterface;
import transport_layer.network.NetworkTransport;
import transport_layer.network.NetworkTransportInterface;

/**
 *
 * @author Patrick C. Berens
 */
public class SecureTransport implements SecureTransportInterface{
    private EncryptionKeys keys;
    private FileTransportInterface fileTransport = new FileHandler();
    private NetworkTransportInterface networkTransport;
    private DiscoveryTransport discoveryTransport;
    private CommunicationInterface communication;
    private Authentications authInstance;
    private ConcurrentMap<String, Integer> lastReceived = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger();
    private ConcurrentMap<String, EncryptedAESMessage> pendingHumanAuth = new ConcurrentHashMap<>();
    
    
    public SecureTransport(String password){
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        keys = new EncryptionKeys(personalKey, password);
        authInstance = new Authentications(keys);
    }
    
    public SecureTransport(Profile profile, String password, CommunicationInterface communication) {        
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        keys = new EncryptionKeys(personalKey, profile.ident, password);
                
        keys.privateKey = profile.keys.privateKey;
        keys.signingKey = profile.keys.signingKey;
        keys.publicKeys = profile.keys.publicKeys;
        keys.verifyingKeys = profile.keys.verifiyngKeys;

        //Add a symmetricKey for self so can send messages to self
        keys.addSymmetricKey(profile.ident, (SecretKey)KeyFactory.generateSymmetricKey());
        keys.addHMACKey(profile.ident, (SecretKey)KeyFactory.generateSymmetricKey());
        
       
        this.networkTransport = new NetworkTransport(profile.ident, profile.host, profile.port, this);
        this.discoveryTransport = new DiscoveryTransport(profile, networkTransport);
        this.communication = communication;
        
        authInstance = new Authentications(keys);
        
        //Hacked for now
        for(String peerId: keys.publicKeys.keySet()){
            if(!peerId.equals(profile.ident)){
                networkTransport.addPeer(peerId, "localhost", 4000 + Integer.parseInt(peerId));
            }
        }
    }
    
    @Override
    public void addPeer(String peerIdent, String host, int port){
        System.out.println("adding " + peerIdent);
        networkTransport.addPeer(peerIdent, host, port);
    }
    
    @Override
    public boolean authenticate(String machineIdent) {
        System.out.println("Starting authentication...");
        //If machine has been authenticated or trying to authenticate with itself, return
        if (authInstance.hasAuthenticated(machineIdent) || machineIdent.equals(keys.ident)) {
            return true;
        }
        final Lock authenticateLock = new ReentrantLock(true);

        MachineAuthenticationMessage msg = authInstance.constructInitialAuthMessage();
        Condition authenticationComplete = authenticateLock.newCondition();
        
        try {
            authenticateLock.lock();
            authInstance.addAuthentication(machineIdent, msg, authenticationComplete, authenticateLock);
            sendRSAEncryptedMessage(machineIdent, msg);
            authenticationComplete.await();
            authInstance.removeAuthentication(machineIdent);
        } catch (InterruptedException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        finally{
            authenticateLock.unlock();
            return true;
        }
    }

    public boolean sendClearMessage(String destination, Message m){
        SecretKey commonKey = (SecretKey) KeyFactory.generateSymmetricKey("CommonKey");
        SecretKey HMACKey = (SecretKey) KeyFactory.generateSymmetricKey("CommonKeyHMAC");
        return sendAESEncryptedMessage(destination, m, commonKey, HMACKey);
    }
        
    @Override
    public boolean sendAESEncryptedMessage(String destination, Message m) {
        return sendAESEncryptedMessage(destination, m, keys.getSymmetricKey(destination), keys.getHMACKey(destination));
    }
    
    @Override
    public boolean sendAESEncryptedMessage(String destination, Message m, SecretKey secretKey, SecretKey HMACKey) {
        System.out.println("Sending AES Message to " + destination);
        if (secretKey == null) secretKey = keys.getSymmetricKey(destination);
        
        if (secretKey == null) {
            System.out.println("No symemetric key found for " + destination);
            return false;
        }
        byte[] iv = CipherFactory.generateRandomIV();
        
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(secretKey, iv);
        
        Serializable sendingMessage = null;
        if(m instanceof HumanAuthenticationMessage){
            sendingMessage = m;
        } else { 
            sendingMessage = new ApplicationMessage(m, counter.incrementAndGet()); 
        }
        try {
            SealedObject encryptedObject = new SealedObject(sendingMessage, cipher);
            byte[] hmac = CipherFactory.HMAC(HMACKey, encryptedObject);
            EncryptedAESMessage encryptedMessage = new EncryptedAESMessage(encryptedObject, iv, hmac);
            return networkTransport.send(destination, encryptedMessage);
        } catch (IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean sendRSAEncryptedMessage(String destination, Message m) {
        System.out.println("Sending RSA Message to " + destination);
        byte[] iv = new byte[16];
        PublicKey publicKey = keys.getPublicKey(destination);
        if (publicKey == null) {
            System.out.println("No public key found for " + destination);
            return false;
        }
        Cipher cipher = CipherFactory.constructRSAEncryptionCipher(publicKey);
        try {
            SealedObject encryptedObject = new SealedObject(m, cipher);
            Signature signature = Signature.getInstance(CipherFactory.SIGNING_ALGORITHM);
            SignedObject signedObject = new SignedObject(encryptedObject, (PrivateKey)keys.signingKey, signature);

            EncryptedMessage encryptedMessage = new EncryptedRSAMessage(signedObject);

            boolean wasSuccessful = networkTransport.send(destination, encryptedMessage);
            return wasSuccessful;
        } catch (IOException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean processEncryptedMessage(String sourceOfMessage, EncryptedMessage encryptedMessage) throws NoSuchAlgorithmException {
        boolean success = false;
        Message decryptedMsg = null;
        SecretKey secretKey = null;
        SecretKey HMACKey = null;
        System.out.println("processing message" + sourceOfMessage);
        SealedObject encryptedObject;
        try {
            Cipher cipher = null;
            switch(encryptedMessage.getAlgorithm()){
                case CipherFactory.AES_ALGORITHM:
     
//                    if (encryptedMessage instanceof EncryptedAESHumanAuthMessage) {
//                        EncryptedAESMessage m = ((EncryptedAESHumanAuthMessage)encryptedMessage).aesMessage;
//                        this.pendingHumanAuth.put(sourceOfMessage, m);
//                        System.out.println("saving hman auth message");
//                        return true;
//                    }
                    
                    EncryptedAESMessage aesMessage = (EncryptedAESMessage)encryptedMessage;
                    encryptedObject = aesMessage.encryptedObject;

                    HMACKey = keys.getHMACKey(sourceOfMessage);
                    secretKey = keys.getSymmetricKey(sourceOfMessage);
                    System.out.println("I have " + keys.secretKeys.size() + " secret keys");
                    
                    if (HMACKey == null || secretKey == null) {
                        this.pendingHumanAuth.put(sourceOfMessage, aesMessage);
                        System.out.println("saving hman auth message");
                        return true;
                    }                    
                    
                    byte[] hmac = CipherFactory.HMAC(HMACKey, encryptedObject);
                    
                    if (!Arrays.equals(hmac, aesMessage.HMAC)) {
                        throw new SignatureException("Invalid HMAC");
                    }                           
                    
                    if (secretKey == null) {
                        System.out.println("No symmetric key for " + sourceOfMessage);
                    }
                    cipher = CipherFactory.constructAESDecryptionCipher(secretKey, aesMessage.iv);
                    break;
                    
                case CipherFactory.SIGNING_ALGORITHM:
                    EncryptedRSAMessage rsaMessage = (EncryptedRSAMessage)encryptedMessage;
                    SignedObject signedObject = rsaMessage.signedObject;
                    PublicKey verifyingKey = keys.getVerifyingKey(sourceOfMessage);
                    Signature sig = Signature.getInstance(CipherFactory.SIGNING_ALGORITHM);
                    boolean verified = signedObject.verify(verifyingKey, sig);
                    if (!verified) {
                        throw new SignatureException("Unverified message from " + sourceOfMessage);
                    } else {
                        encryptedObject = (SealedObject)signedObject.getObject();
                        cipher = CipherFactory.constructRSADecryptionCipher(keys.privateKey);
                    }
                    break;
                   
                default:
                    throw new NoSuchAlgorithmException("Attempted to process a message encrypted with an unsupported algorithm.");
            }
            
            Object decryptedObj = encryptedObject.getObject(cipher);
            if (decryptedObj instanceof HumanAuthenticationMessage) {
                System.out.println("[DEBUG] processing HumanAuthenticationMessage");
                authInstance.processHumanAuthenticationRequest(sourceOfMessage, 
                                                          (HumanAuthenticationMessage)decryptedObj,
                                                          this);
                success = true;
            } else if (decryptedObj instanceof MachineAuthenticationMessage) {
                System.out.println("[DEBUG] processing MachineAuthenticationMessage");
                authInstance.processMachineAuthenticationRequest(sourceOfMessage, 
                                                          (MachineAuthenticationMessage)decryptedObj,
                                                          this);
                success = true;
            } else if (decryptedObj instanceof ApplicationMessage) {
                ApplicationMessage appMessage = (ApplicationMessage)decryptedObj;
                Integer currentCounter = lastReceived.get(sourceOfMessage);
                if (currentCounter == null || appMessage.counter > currentCounter) {
                    communication.depositMessage(appMessage.message);
                    success = true;
                    lastReceived.put(sourceOfMessage, appMessage.counter);
                } else {
                    System.out.println("Invalid counter on AES message");
                }
            }
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return success;
    }
    
    @Override
    public void processDiscoveryResponse(DiscoveryResponseMessage msg){
        System.out.println(keys.ident + ": Process discovery response");
        PublicKey pk = keys.getPublicKey(msg.ident);
        if(pk != null && pk.serialVersionUID == msg.keyVersion){
            communication.updatePeers(msg.ident, msg.ip, msg.port, msg.documents, false);
        } else{
            communication.updatePeers(msg.ident, msg.ip, msg.port, msg.documents, true);
        }
    }
    @Override
    public boolean writeEncryptedFile(String filename, Message contents) {
        try {
            //Encrypt file with personal key
            byte[] iv = CipherFactory.generateRandomIV();
            Cipher cipher = CipherFactory.constructAESEncryptionCipher(keys.personalKey, iv);
            SealedObject encryptedObject = new SealedObject(contents, cipher);
            
            String salt = KeyFactory.generateSalt();
            Key hmacKey = KeyFactory.generateSymmetricKey(keys.password, salt);
            byte[] hmac = CipherFactory.HMAC(hmacKey, encryptedObject);
            
            EncryptedAESFile file = new EncryptedAESFile(encryptedObject, iv, hmac, salt);
            return fileTransport.writeFile(filename, file);
            
        } catch (IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }   
    }

    @Override
    public Message readEncryptedFile(String filename) {
        if (!new File(filename).exists()) {
            return null;
        }
        try {
            EncryptedAESFile file = (EncryptedAESFile)fileTransport.readFile(filename);
            
            //Verify hmac
            Key hmacKey = KeyFactory.generateSymmetricKey(keys.password, file.hmacSalt);
            byte[] hmac = CipherFactory.HMAC(hmacKey, file.encryptedObject);        
            if (!Arrays.equals(hmac, file.HMAC)) {
                throw new SignatureException("Invalid HMAC");
            }
            
            Cipher cipher = CipherFactory.constructAESDecryptionCipher(keys.personalKey, file.iv);
            return (Message)file.encryptedObject.getObject(cipher);
        } catch (SignatureException ex) {
            System.out.println("Bad signature reading " + filename);
            return null;
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
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

    @Override
    public boolean initializeHumanAuthenticate(String ID) {
        //authInstance
        System.out.println("Starting authentication...");

        final Lock authenticateLock = new ReentrantLock(true);
        
        System.out.println("send initial hauth message");
        HumanAuthenticationMessage msg = authInstance.constructInitialHAuthMessage();
        Condition authenticationComplete = authenticateLock.newCondition();
        System.out.println("recd initial hauth message");
        
        try {
            authenticateLock.lock();
            // send the message and wait
            authInstance.addAuthentication(ID, msg, authenticationComplete, authenticateLock);
            sendPlainTextMessage(ID, msg);
        }
        finally{
            authenticateLock.unlock();
            return true;
        }
    }

    @Override
    public void broadcastDiscovery() {
        discoveryTransport.broadcastDiscovery();
    }

    @Override
    public boolean addPIN(String ID, String PIN) {
        if (!this.pendingHumanAuth.containsKey(ID)) {
            System.out.println("haven't received message from server");
            return false;
        }
        
        SecretKey pinKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN);
        SecretKey HMACKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN + "HMAC");
        keys.addSymmetricKey(ID, pinKey);
        keys.addHMACKey(ID, HMACKey);
        
        EncryptedAESMessage m = this.pendingHumanAuth.get(ID);
        try {
            boolean r = processEncryptedMessage(ID, m);
            System.out.println("decrypted : " +r);
            return r;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("failed to decrpy with error");
            return false;
        }
    }

    @Override
    public boolean sendPlainTextMessage(String destination, Message m) {
        System.out.println("Sending plainText Message to " + destination);
        PlainTextMessage sendMsg = new PlainTextMessage();
        sendMsg.m = m;
        return networkTransport.send(destination, sendMsg);
    }

    @Override
    public boolean processPlainTextMessage(String sourceOfMessage, PlainTextMessage msg) {
        System.out.println("[DEBUG] processing AuthenticationMessage");
        authInstance.processHumanAuthenticationRequest(sourceOfMessage, 
                                                  (HumanAuthenticationMessage)msg.m,
                                                  this);
        return true;
    }
}
