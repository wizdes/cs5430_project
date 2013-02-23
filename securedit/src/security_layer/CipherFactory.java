/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author Patrick C. Berens
 */
public class CipherFactory {
    /**************************************
     * patrick's
     * ****************************************/
    public Cipher constructAESEncryptionCipher(Key key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public Cipher constructAESDecryptionCipher(Key key, byte[] iv){
        assert iv.length == 16 : iv;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public Cipher constructRSAEncryptionCipher(Key key){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public Cipher constructRSADecryptionCipher(Key key){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }
    
    public byte[] generateRandomIV(){
        try {
            //SecureRandom random = new SecureRandom();
           SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] iv = new byte[16];
            sr.nextBytes(iv);
            return iv;
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
