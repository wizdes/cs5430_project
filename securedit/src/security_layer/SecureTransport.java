/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;


import application.encryption_demo.CommunicationInterface;
import application.messages.Message;
import java.io.IOException;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import security_layer.machine_authentication.AuthenticationMessage;
import security_layer.machine_authentication.Authentications;
import security_layer.machine_authentication.Msg01_AuthenticationRequest;
import transport_layer.files.FileHandler;
import transport_layer.files.FileTransportInterface;
import transport_layer.network.NetworkTransport;
import transport_layer.network.NetworkTransportInterface;
import transport_layer.network.Node;



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
        System.out.println(personalKey.hashCode());
        keys = new EncryptionKeys(personalKey);
        authInstance = new Authentications(keys);
    }
    
    public SecureTransport(String password, Node host, CommunicationInterface communication) {
        assert password.length() == 16: password.length();
        
        System.out.println("st " + host);
        this.networkTransport = new NetworkTransport(host, this);
        this.communication = communication;
        
        
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        System.out.println("personal key = " + personalKey.hashCode());
        keys = new EncryptionKeys(host.getID(), personalKey);
        KeysObject keyObj = (KeysObject)readEncryptedFile("keys_" + host.getID());
        PrivateKey privateKey = keyObj.getPrivateKey();
        keys.privateKey = privateKey;
        keys.publicKeys = keyObj.getPublicKeys();
//        keys.secretKeys.put("0", keyObj.getSecretKey());
//        keys.secretKeys.put("1", keyObj.getSecretKey());
//        keys.secretKeys.put("2", keyObj.getSecretKey());
                
//        Key secretKey = KeyFactory.generateSymmetricKey();  //Replace with authentication
        //KeyPair asymmetricKeys = KeyFactory.generateAsymmetricKeys();
        
        authInstance = new Authentications(keys);
    }
    
    @Override
    public boolean authenticate(Node dest) {
        if(authInstance.hasAuthenticated(dest.getID())){
            return true;
        }
        final Lock authenticateLock = new ReentrantLock(true);
        int nonce1 = KeyFactory.generateNonce();
        Msg01_AuthenticationRequest msg = new Msg01_AuthenticationRequest(dest, nonce1);
        Condition authenticationComplete = authenticateLock.newCondition();
        
        try {
            authenticateLock.lock();
            authInstance.addAuthentication(dest.getID(), msg, authenticationComplete, authenticateLock);
            sendRSAEncryptedMessage(msg);
            authenticationComplete.await();
            authInstance.removeAuthentication(dest.getID());
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
    public Serializable sendAESEncryptedMessage(Message m) {
        byte[] iv = CipherFactory.generateRandomIV();
        SecretKey secretKey = keys.getSymmetricKey(m.getTo().getID());
        
        if (secretKey == null) {
            System.out.println("No symemetric key found for " + m.getTo().getID());
            return null;
        }
        
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(secretKey, iv);
        m.setFrom(this.networkTransport.getHost());
        return sendEncryptedMessage(m, cipher, iv, CipherFactory.HMAC(secretKey, m));
    }

    @Override
    public Serializable sendRSAEncryptedMessage(Message m) {
        byte[] iv = new byte[16];
        PublicKey publicKey = keys.getPublicKey(m.getTo().getID());
        Cipher cipher = CipherFactory.constructRSAEncryptionCipher(publicKey);
        m.setFrom(this.networkTransport.getHost());
        return sendEncryptedMessage(m, cipher, iv, null);
    }

    private Serializable sendEncryptedMessage(Message m, Cipher cipher, byte[] iv, byte[] hmac) {
        Node from = this.networkTransport.getHost();
        
        try {
            Serializable send_obj = m;
            if (hmac != null){
                send_obj = new HMACMessage(m, hmac);
            }
            SealedObject encryptedObject = new SealedObject(send_obj, cipher);
            EncryptedObject encryptedMessage = new EncryptedObject(encryptedObject, iv, from);
            
            networkTransport.send(m.getTo(), encryptedMessage);
            return encryptedObject.toString();          //Doesn't return encrypted text
        } catch (IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Message processEncryptedMessage(Serializable encryptedNetMsg) throws NoSuchAlgorithmException {
        EncryptedObject encryptedObject = (EncryptedObject)encryptedNetMsg;
        Message decryptedMsg = null;
        SecretKey secretKey = null;
                
        try {
            Cipher cipher = null;
            switch(encryptedObject.encryptedObject.getAlgorithm()){
                case "AES/CBC/PKCS5Padding":
                    secretKey = keys.getSymmetricKey(encryptedObject.getFrom().getID());
                    cipher = CipherFactory.constructAESDecryptionCipher(secretKey, encryptedObject.iv);
                    break;
                case "RSA/ECB/PKCS1Padding":
                    cipher = CipherFactory.constructRSADecryptionCipher(keys.privateKey);
                    break;
                default:
                    throw new NoSuchAlgorithmException("Attempted to process a message encrypted with an unsupported algorithm.");
            }
            
            Object obj = encryptedObject.encryptedObject.getObject(cipher);
            decryptedMsg = messageFromEncryptedObject(obj, secretKey);
            if (decryptedMsg instanceof AuthenticationMessage) {
                System.out.println("[DEBUG] processing AuthenticationMessage");
                Message m = authInstance.processAuthenticationRequest((AuthenticationMessage)decryptedMsg);
                if (m != null) {
                    System.out.println("[DEBUG] sendRSAEncryptedMessage");
                    sendRSAEncryptedMessage(m);
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
        if (obj instanceof HMACMessage) {
            HMACMessage hmacMessage = (HMACMessage)obj;
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
    public Serializable writeEncryptedFile(String filename, Serializable contents) {
        try {
            //Encrypt file with personal key
            byte[] iv = CipherFactory.generateRandomIV();
            System.out.println("file.iv = " + Arrays.toString(iv));
            Cipher cipher = CipherFactory.constructAESEncryptionCipher(keys.personalKey, iv);
            SealedObject encryptedObject = new SealedObject(contents, cipher);
            
            EncryptedObject file = new EncryptedObject(encryptedObject, iv, null);
            fileTransport.writeFile(filename, file);
            return encryptedObject.toString();          //Doens't return encrypted text
        } catch (IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }

    @Override
    public Serializable readEncryptedFile(String filename) {
        try {
            //return fileTransport.readFile(filename);
            System.out.println("filename: " + filename);
            EncryptedObject file = (EncryptedObject)fileTransport.readFile(filename);
            
            System.out.println("keys.personalKey = " + keys.personalKey.hashCode());
            System.out.println("file.iv = " + Arrays.toString(file.iv));
            Cipher cipher = CipherFactory.constructAESDecryptionCipher(keys.personalKey, file.iv);
            return (Serializable)file.encryptedObject.getObject(cipher);
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Serializable readUnencryptedFile(String filename) {
        return (Serializable)fileTransport.openUnserializedFile(filename);
    }
    
    @Override
    public void shutdown() {
        this.networkTransport.shutdown();
    }
}
