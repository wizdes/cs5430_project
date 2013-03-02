/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;


import application.encryption_demo.CommunicationInterface;
import application.encryption_demo.Message;
import java.io.IOException;
import java.io.Serializable;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
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
    
    /********************************************
     * patrick's 
     * *******************************************/
    public SecureTransport(String password){
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        keys = new EncryptionKeys(personalKey);
        authInstance = new Authentications(keys);
    }
    
    public SecureTransport(String ident, String host, int port, String password, CommunicationInterface communication) {
        assert password.length() == 16: password.length();
        
        this.networkTransport = new NetworkTransport(ident, host, port, this);
        this.communication = communication;
        
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        keys = new EncryptionKeys(ident, personalKey);
        KeysObject keyObj = (KeysObject)readEncryptedFile("keys_" + ident);
        PrivateKey privateKey = keyObj.privateKey;
        keys.privateKey = privateKey;
        keys.publicKeys = keyObj.publicKeys;
        
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
        if(authInstance.hasAuthenticated(machineIdent) || machineIdent.equals(keys.ident)){
            return true;
        }
        final Lock authenticateLock = new ReentrantLock(true);

        AuthenticationMessage msg = authInstance.constructInitialAuthMessage();
        Condition authenticationComplete = authenticateLock.newCondition();
        
        System.out.println("grabbing lock");
        try {
            authenticateLock.lock();
            authInstance.addAuthentication(machineIdent, msg, authenticationComplete, authenticateLock);
            sendRSAEncryptedMessage(machineIdent, msg);
            System.out.println("Waiting");
            authenticationComplete.await();
            System.out.println("Awoke");
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
        
    @Override
    public boolean sendAESEncryptedMessage(String destination, Message m) {
        SecretKey secretKey = keys.getSymmetricKey(destination);
        
        if (secretKey == null) {
            System.out.println("No symemetric key found for " + destination);
            return false;
        }
        byte[] iv = CipherFactory.generateRandomIV();
        
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(secretKey, iv);
        byte[] hmac = CipherFactory.HMAC(secretKey, m);
        
        AESMessage aesMessage = new AESMessage(m, hmac);
        
        try {
            SealedObject encryptedObject = new SealedObject(aesMessage, cipher);
            EncryptedAESMessage encryptedMessage = new EncryptedAESMessage(encryptedObject, iv);
            return networkTransport.send(destination, encryptedMessage);
        } catch (IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean sendRSAEncryptedMessage(String destination, Message m) {
        System.out.println("Sending RSA Message");
        byte[] iv = new byte[16];
        PublicKey publicKey = keys.getPublicKey(destination);
        if (publicKey == null) {
            System.out.println("No public key found for " + destination);
            return false;
        }        
        Cipher cipher = CipherFactory.constructRSAEncryptionCipher(publicKey);
        try {
            SealedObject encryptedObject = new SealedObject(m, cipher);
            EncryptedMessage encryptedMessage = new EncryptedRSAMessage(encryptedObject);

            boolean wasSuccessful = networkTransport.send(destination, encryptedMessage);
            return wasSuccessful;
        } catch (IOException | IllegalBlockSizeException ex) {
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
                case "AES/CBC/PKCS5Padding":
                    EncryptedAESMessage aesMessage = (EncryptedAESMessage)encryptedMsg;
                    encryptedObject = aesMessage.encryptedObject;
                    secretKey = keys.getSymmetricKey(sourceOfMessage);
                    cipher = CipherFactory.constructAESDecryptionCipher(secretKey, aesMessage.iv);
                    break;
                case "RSA/ECB/PKCS1Padding":
                    EncryptedRSAMessage rsaMessage = (EncryptedRSAMessage)encryptedMsg;
                    encryptedObject = rsaMessage.encryptedObject;
                    System.out.println("Received RSA message");
                    cipher = CipherFactory.constructRSADecryptionCipher(keys.privateKey);
                    break;
                default:
                    throw new NoSuchAlgorithmException("Attempted to process a message encrypted with an unsupported algorithm.");
            }
            
            Object obj = encryptedObject.getObject(cipher);
            decryptedMsg = messageFromEncryptedObject(obj, secretKey);
            if (decryptedMsg instanceof AuthenticationMessage) {
                System.out.println("[DEBUG] processing AuthenticationMessage");
                Message m = authInstance.processAuthenticationRequest(sourceOfMessage, (AuthenticationMessage)decryptedMsg);
                if (m != null) {
                    System.out.println("[DEBUG] sendRSAEncryptedMessage");
                    sendRSAEncryptedMessage(sourceOfMessage, m);
                }
            } else {
                communication.depositMessage(decryptedMsg);
            }
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return decryptedMsg;
    }
        
    private Message messageFromEncryptedObject(Object obj, SecretKey secretKey) {
        Message decryptedMsg = null;
        if (obj instanceof AESMessage) {
            AESMessage hmacMessage = (AESMessage)obj;
            byte[] hmac = CipherFactory.HMAC(secretKey, hmacMessage.getMessage());
            if (Arrays.equals(hmac, hmacMessage.getHMAC())) {
                decryptedMsg = hmacMessage.getMessage();
            } else {
                System.out.println("Invalid HMAC");
            }
        } else {
            decryptedMsg = (Message)obj;
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
            
            EncryptedAESMessage file = new EncryptedAESMessage(encryptedObject, iv);
            return fileTransport.writeFile(filename, file);
            
        } catch (IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }

    @Override
    public Message readEncryptedFile(String filename) {
        try {
            System.out.println("filename: " + filename);
            EncryptedAESMessage file = (EncryptedAESMessage)fileTransport.readFile(filename);
            
            Cipher cipher = CipherFactory.constructAESDecryptionCipher(keys.personalKey, file.iv);
            return (Message)file.encryptedObject.getObject(cipher);
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
}
