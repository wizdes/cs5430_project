/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;


import application.encryption_demo.CommunicationInterface;
import application.encryption_demo.Message;
import java.io.IOException;
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
    private CommunicationInterface communication;
    private Authentications authInstance;
    private ConcurrentMap<String, Integer> lastReceived = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger();
    
    public SecureTransport(String password){
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        keys = new EncryptionKeys(personalKey, password);
        authInstance = new Authentications(keys);
    }
    
    public SecureTransport(String ident, String host, int port, String password, CommunicationInterface communication) {
        assert port == 4000 + Integer.parseInt(ident);
        
        this.networkTransport = new NetworkTransport(ident, host, port, this);
        this.communication = communication;
        
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        keys = new EncryptionKeys(personalKey, ident, password);
        KeysObject keyObj = (KeysObject)readEncryptedFile("keys_" + ident);
        keys.privateKey = keyObj.privateKey;
        keys.signingKey = keyObj.signingKey;
        keys.publicKeys = keyObj.publicKeys;
        keys.verifyingKeys = keyObj.verifiyngKeys;
        
        authInstance = new Authentications(keys);
        
        //Hacked for now
        for(String peerId: keys.publicKeys.keySet()){
            if(!peerId.equals(ident)){
                networkTransport.addPeer(peerId, "localhost", 4000 + Integer.parseInt(peerId));
            }
        }
    }
    
    @Override
    public boolean authenticate(String machineIdent) {
        System.out.println("Starting authentication...");
        //If machine has been authenticated or trying to authenticate with itself, return
        if(authInstance.hasAuthenticated(machineIdent) || machineIdent.equals(keys.ident)){
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
        return sendAESEncryptedMessage(destination, m, commonKey);
    }
        
    @Override
    public boolean sendAESEncryptedMessage(String destination, Message m) {
        return sendAESEncryptedMessage(destination, m, keys.getSymmetricKey(destination));
    }
    
    @Override
    public boolean sendAESEncryptedMessage(String destination, Message m, SecretKey secretKey) {
        System.out.println("Sending AES Message to " + destination);
        if(secretKey == null) secretKey = keys.getSymmetricKey(destination);
        
        if (secretKey == null) {
            System.out.println("No symemetric key found for " + destination);
            return false;
        }
        byte[] iv = CipherFactory.generateRandomIV();
        
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(secretKey, iv);
        ApplicationMessage appMessage = new ApplicationMessage(m, counter.incrementAndGet());    
        try {
            SealedObject encryptedObject = new SealedObject(appMessage, cipher);
            byte[] hmac = CipherFactory.HMAC(keys.getHMACKey(destination), encryptedObject);
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
    public Message processEncryptedMessage(String sourceOfMessage, EncryptedMessage encryptedMsg) throws NoSuchAlgorithmException {
        EncryptedMessage encryptedMessage = (EncryptedMessage)encryptedMsg;
        Message decryptedMsg = null;
        SecretKey secretKey = null;
        
        SealedObject encryptedObject;
        try {
            Cipher cipher = null;
            switch(encryptedMessage.getAlgorithm()){
                case CipherFactory.AES_ALGORITHM:
                    EncryptedAESMessage aesMessage = (EncryptedAESMessage)encryptedMsg;
                    encryptedObject = aesMessage.encryptedObject;
                    byte[] hmac = CipherFactory.HMAC(keys.getHMACKey(sourceOfMessage), encryptedObject);
                    
                    if (!Arrays.equals(hmac, aesMessage.HMAC)) {
                        throw new SignatureException("Invalid HMAC");
                    }                           
                    secretKey = keys.getSymmetricKey(sourceOfMessage);
                    if (secretKey == null) {
                        System.out.println("No symmetric key for " + sourceOfMessage);
                    }
                    cipher = CipherFactory.constructAESDecryptionCipher(secretKey, aesMessage.iv);
                    break;
                    
                case CipherFactory.SIGNING_ALGORITHM:
                    EncryptedRSAMessage rsaMessage = (EncryptedRSAMessage)encryptedMsg;
                    SignedObject signedObject = rsaMessage.signedObject;
                    PublicKey publicKey = keys.getVerifyingKey(sourceOfMessage);
                    Signature sig = Signature.getInstance(CipherFactory.SIGNING_ALGORITHM);
                    boolean verified = signedObject.verify(publicKey, sig);
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
                System.out.println("[DEBUG] processing AuthenticationMessage");
                authInstance.processHumanAuthenticationRequest(sourceOfMessage, 
                                                          (HumanAuthenticationMessage)decryptedObj,
                                                          this);
            } else if (decryptedObj instanceof MachineAuthenticationMessage) {
                System.out.println("[DEBUG] processing AuthenticationMessage");
                authInstance.processMachineAuthenticationRequest(sourceOfMessage, 
                                                          (MachineAuthenticationMessage)decryptedObj,
                                                          this);
            } else if (decryptedObj instanceof ApplicationMessage) {
                ApplicationMessage appMessage = (ApplicationMessage)decryptedObj;
                Integer currentCounter = lastReceived.get(sourceOfMessage);
                if (currentCounter == null || appMessage.counter > currentCounter) {
                    communication.depositMessage(appMessage.message);
                    lastReceived.put(sourceOfMessage, appMessage.counter);
                } else {
                    System.out.println("Invalid counter on AES message");
                }
            }
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return decryptedMsg;
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
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | SignatureException | BadPaddingException ex) {
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

        HumanAuthenticationMessage msg = authInstance.constructInitialHAuthMessage();
        Condition authenticationComplete = authenticateLock.newCondition();
        
        try {
            authenticateLock.lock();
            // send the message and wait
            authInstance.addAuthentication(ID, msg, authenticationComplete, authenticateLock);
            sendClearMessage(ID, msg);
            authenticationComplete.await();
            authInstance.removeAuthentication(ID);
        } catch (InterruptedException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        finally{
            authenticateLock.unlock();
            return true;
        }
    }
}
