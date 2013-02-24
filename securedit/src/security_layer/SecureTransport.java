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
import java.security.SecureRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
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
    private Authentications authInstance = new Authentications();
    
    /********************************************
     * patrick's 
     * *******************************************/
    public SecureTransport(String password){
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        keys = new EncryptionKeys(personalKey);
    }
    public SecureTransport(String password, Node host, CommunicationInterface communication) {
        assert password.length() == 16: password.length();
        
        this.networkTransport = new NetworkTransport(host, this);
        this.communication = communication;
        
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        KeysObject keyObj = (KeysObject)readEncryptedFile("keys_" + host.getID());
        Key privateKey = keyObj.getPrivateKey();
        Key publicKey = keyObj.getPublicKey(host.getID());
        
        Key secretKey = KeyFactory.generateSymmetricKey();  //Replace with authentication
        //KeyPair asymmetricKeys = KeyFactory.generateAsymmetricKeys();
        
        keys = new EncryptionKeys(host.getID(), personalKey, secretKey, publicKey, privateKey);
    }
    
    @Override
    public boolean authenticate(Node dest) {
        final Lock authenticateLock = new ReentrantLock(true);
        int nonce1 = generateNonce();
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
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(keys.secretKey, iv);
        return sendEncryptedMessage(m, cipher, iv);
    }

    @Override
    public Serializable sendRSAEncryptedMessage(Message m) {
        byte[] iv = new byte[16];
        Cipher cipher = CipherFactory.constructRSAEncryptionCipher(keys.publicKey);
        return sendEncryptedMessage(m, cipher, iv);
    }

    private Serializable sendEncryptedMessage(Message m, Cipher cipher, byte[] iv) {
        try {
            SealedObject encryptedObject = new SealedObject(m, cipher);
            EncryptedObject encryptedMessage = new EncryptedObject(encryptedObject, iv);
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
        try {
            Cipher cipher = null;
            switch(encryptedObject.encryptedObject.getAlgorithm()){
                case "AES/CBC/PKCS5Padding":
                    cipher = CipherFactory.constructAESDecryptionCipher(keys.secretKey, encryptedObject.iv);
                    break;
                case "RSA/ECB/PKCS1Padding":
                    cipher = CipherFactory.constructRSADecryptionCipher(keys.privateKey);
                    break;
                default:
                    throw new NoSuchAlgorithmException("Attempted to process a message encrypted with an unsupported algorithm.");
            }
            
            decryptedMsg = (Message)encryptedObject.encryptedObject.getObject(cipher);
            communication.depositMessage(decryptedMsg);
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return decryptedMsg;
    }

    @Override
    public Serializable writeEncryptedFile(String filename, Serializable contents) {
        try {
            //Encrypt file with personal key
            byte[] iv = CipherFactory.generateRandomIV();
            Cipher cipher = CipherFactory.constructAESEncryptionCipher(keys.personalKey, iv);
            SealedObject encryptedObject = new SealedObject(contents, cipher);
            
            EncryptedObject file = new EncryptedObject(encryptedObject, iv);
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
            EncryptedObject file = (EncryptedObject)fileTransport.readFile(filename);
            
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

    private int generateNonce() {
        SecureRandom rand = new SecureRandom();
        return rand.nextInt();
    }
}
