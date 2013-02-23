/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package _reference_classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
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
public class RSANetwork {
    public void sendEncryptedMessage(ObjectOutputStream out, Serializable msg, Key key){
        try {
            
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            //-Send Encrypted Message
            //   SealedObject and Message are serializable.
            SealedObject sealedObject = new SealedObject(msg, cipher);
            out.writeObject(sealedObject);
        } catch (IllegalBlockSizeException | IOException | NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Object receiveEncryptedMessage(ObjectInputStream in, Key key){
        try {
           //Read SealedObject
            SealedObject sealedObject = (SealedObject)in.readObject();
            
            //Decrypt Sealed Object
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            return sealedObject.getObject(cipher);
        } catch (IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException ex) {
            Logger.getLogger(AESNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
