/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import configuration.Constants;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import security_layer.authentications.AuthenticationTransport;

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
    
    /**
     * Generate symmetric key, just from password.
     * -Uses default salt, just so it can use PBEKeySpec which increases key
     *    computation time. This is helpful to make generation of keys from PIN
     *    and K(from SRP) even more infeasible.
     * @param password Char array password
     * @return 
     */
    public static SecretKey generateSymmetricKey(char[] password){
        byte[] salt = null;
        try {
            salt = "IAmADefaultSalt".getBytes("UTF-16");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return generateSymmetricKey(password, salt);
    }
        /**
     * Generate symmetric key, just from password.
     * -Uses default salt, just so it can use PBEKeySpec which increases key
     *    computation time. This is helpful to make generation of keys from PIN
     *    and K(from SRP) even more infeasible.
     * @param password Password in bytes.
     * @return 
     */
    public static SecretKey generateSymmetricKey(byte[] password){
        byte[] salt = null;
        try {
            salt = "IAmADefaultSalt".getBytes("UTF-16");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return generateSymmetricKey(new String(password).toCharArray(), salt);
    }
    
    public static SecretKey generateSymmetricKey(char[] password, byte[] salt){
        try {
            char[] pass16 = fix_pass_length_16(password);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec ks = new PBEKeySpec(pass16, salt, 61398, 128);
            SecretKey temp = f.generateSecret(ks);
            SecretKey k = new SecretKeySpec(temp.getEncoded(),"AES");
            return k;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(AuthenticationTransport.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
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
    
    public static byte[] generateSalt(){
        try {
            byte[] salt = new byte[20];
            SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
            rand.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
       
    private static char[] fix_pass_length_16(char[] pass){
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(new String(pass).getBytes("UTF-16"));
            return new BigInteger(1, key).toString().substring(0, 16).toCharArray();
            //return Arrays.copyOf(key, 16); // use only first 128 bit
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;        
    }
    
    public static char[] generatePIN() {
        SecureRandom seededSecureRandom = null;
        try {
            seededSecureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            seededSecureRandom.nextBytes(new byte[1]);  //Forces it to seed. Best practice.
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        //String randomPIN = "";
        char[] randomPIN = new char[Constants.PIN_LENGTH];
        for(int i = 0; i < randomPIN.length; i++){
            randomPIN[i] = (char)new BigInteger(Constants.numBytesPIN, seededSecureRandom).intValue();
        }
        
        char[] retPIN = new char[Constants.PIN_LENGTH];
        //this makes capital letters
        for(int i = 0; i < Constants.PIN_LENGTH; i++){
            char insertPIN = randomPIN[i];
            int upper = seededSecureRandom.nextInt(2);
            if(upper == 1 && !Character.isDigit(insertPIN)){
                insertPIN = Character.toUpperCase(insertPIN);
            }
            retPIN[i] = insertPIN;
        }
        return retPIN;
    }
}
