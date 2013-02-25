/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Patrick C. Berens
 */
public class KeyFactory {
    /**********************************************
     * patrick's
     * ********************************************/
    public static KeyPair generateAsymmetricKeys(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(3584);
//            keyGen.initialize(2048);            
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static byte[] fix_bad_length_key(byte[] passBytes){
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] key = sha.digest(passBytes);
            return Arrays.copyOf(key, 16); // use only first 128 bit
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;        
    }
    
    public static Key generateSymmetricKey(String password){
        byte[] passBytes = password.getBytes();
        //assert passBytes.length == 16 : passBytes.length;
        if(passBytes.length != 16){
           // passBytes = fix_bad_length_key(passBytes);
        }
        return new SecretKeySpec(passBytes, "AES");
    }
    public static Key generateSymmetricKey(){
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] passBytes = new byte[16];
            sr.nextBytes(passBytes);
            return new SecretKeySpec(passBytes, "AES");
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static int generateNonce() {
        SecureRandom rand = new SecureRandom();
        return rand.nextInt();
    }
}
