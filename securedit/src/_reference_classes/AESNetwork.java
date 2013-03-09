/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package _reference_classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author Patrick C. Berens
 */
public class AESNetwork {
    public void sendEncryptedMessage(ObjectOutputStream out, Message msg, Key key){
        try {
            
            //SIMPLE AES ENCRYPTION SETUP
            SecretKey secret = (SecretKey)key;
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] iv = new byte[16];
            random.nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
            
            //-Send IV
            out.write(iv);
            
            //-Send Encrypted Message
            //   SealedObject and Message are serializable.
            SealedObject sealedObject = new SealedObject(msg, cipher);
            out.writeObject(sealedObject);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException | IOException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Object receiveEncryptedMessage(ObjectInputStream in, Key key){
        try {
            //Read IV
            byte[] iv = new byte[16];
            in.read(iv);
            
            //Read SealedObject
            SealedObject sealedObject = (SealedObject)in.readObject();
            
            //Decrypt Sealed Object
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            
            return sealedObject.getObject(cipher);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
