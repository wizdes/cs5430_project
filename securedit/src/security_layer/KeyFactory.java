/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import configuration.Constants;
import java.io.UnsupportedEncodingException;
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
    
    static KeyPair generateAsymmetricKeys(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//            keyGen.initialize(3584);
            keyGen.initialize(2048);            
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    
    public static Key generateSymmetricKey(String password){
        try {
            //System.out.println("generateSymmetricKey " + password);
            if (password == null || password.contains("null")) {
                throw new NumberFormatException();
            }
            byte[] passBytes = password.getBytes("UTF-8");
            return new SecretKeySpec(fix_bad_length_key(passBytes), "AES");
        } catch (UnsupportedEncodingException | NumberFormatException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    
    static Key generateSymmetricKey(String password, String salt){
        return generateSymmetricKey(password + salt);
    }
    static Key generateSymmetricKey(){
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] passBytes = new byte[16];
            sr.nextBytes(passBytes);
            return new SecretKeySpec(passBytes, "AES");
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    public static int generateNonce() {
        SecureRandom rand;
        try {
            rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
            return rand.nextInt();
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return -1;
        }
    }
    
    static String generateSalt(){
        try {
            byte[] salt = new byte[20];
            SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
            rand.nextBytes(salt);
            return new String(salt, "UTF-8");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchProviderException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
       
    private static byte[] fix_bad_length_key(byte[] passBytes){
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(passBytes);
            return Arrays.copyOf(key, 16); // use only first 128 bit
        } catch (NoSuchAlgorithmException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;        
    }
}
