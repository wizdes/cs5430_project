/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;


import java.io.IOException;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
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
    private NetworkTransportInterface networkTransport = new NetworkTransport();
    /********************************************
     * patrick's 
     * *******************************************/
    public SecureTransport(String password){
        assert password.length() == 16: password.length();
        
        Key personalKey = KeyFactory.generateSymmetricKey(password);
        Key secretKey = KeyFactory.generateSymmetricKey();
        KeyPair asymmetricKeys = KeyFactory.generateAsymmetricKeys();
        
        keys = new EncryptionKeys(personalKey, secretKey, asymmetricKeys.getPublic(), asymmetricKeys.getPrivate());
    }

    @Override
    public Serializable sendAESEncryptedMessage(Serializable msg) {
        byte[] iv = CipherFactory.generateRandomIV();
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(keys.secretKey, iv);

        return sendEncryptedMessage(msg, cipher, iv);
    }

    @Override
    public Serializable sendRSAEncryptedMessage(Serializable msg) {
        byte[] iv = new byte[16];
        Cipher cipher = CipherFactory.constructRSAEncryptionCipher(keys.publicKey);
        
        return sendEncryptedMessage(msg, cipher, iv);
    }

    private Serializable sendEncryptedMessage(Serializable msg, Cipher cipher, byte[] iv) {
        try {
            SealedObject encryptedObject = new SealedObject(msg, cipher);
            EncryptedObject encryptedMessage = new EncryptedObject(encryptedObject, iv);

            networkTransport.send(encryptedMessage);
            return encryptedObject.toString();          //Doesn't return encrypted text
        } catch (IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void processEncryptedMessage(Serializable encryptedNetMsg) throws NoSuchAlgorithmException {
        try {
            
            EncryptedObject encryptedMessage = (EncryptedObject) encryptedNetMsg;
            Cipher cipher = null;
            switch(encryptedMessage.encryptedObject.getAlgorithm()){
                case "AES/CBC/PKCS5Padding":
                    cipher = CipherFactory.constructAESDecryptionCipher(keys.privateKey, encryptedMessage.iv);
                    break;
                case "RSA/ECB/PKCS1Padding":
                    cipher = CipherFactory.constructRSADecryptionCipher(keys.privateKey);
                    break;
                default:
                    throw new NoSuchAlgorithmException("Attempted to process a message encrypted with an unsupported algorithm.");
            }
            
            Serializable decryptedObj = (Serializable)encryptedMessage.encryptedObject.getObject(cipher);
            
            //TODO MATT: Needs to be put on application message queue or handled somewhere else
            //  This is just a demo of how things might be done...do it however/whereever you want. 
            
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SecureTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
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
}
