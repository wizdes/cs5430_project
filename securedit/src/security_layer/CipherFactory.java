/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import configuration.Constants;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author Patrick C. Berens
 */
class CipherFactory {
    static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    static final String SIGNING_ALGORITHM = "SHA1withRSA";
    
    static Cipher constructAESEncryptionCipher(Key key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    static Cipher constructAESDecryptionCipher(Key key, byte[] iv){
        assert iv.length == 16 : iv;
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    static Cipher constructRSAEncryptionCipher(Key key){
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    static Cipher constructRSADecryptionCipher(Key key){
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
        
    }
    
    static byte[] generateRandomIV(){
        try {
           SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] iv = new byte[16];
            sr.nextBytes(iv);
            return iv;
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    
    static byte[] HMAC(Key sk, Object DataToHash){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos); 
            out.writeObject(DataToHash);
            return HMAC(sk, bos.toByteArray());
        }
        catch (IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    
    private static byte[] HMAC(Key sk, byte[] DataToHash){
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(sk);
            return mac.doFinal(DataToHash);
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(CipherFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
}
