/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
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
    public static KeyPair generateAsymmetricKey(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            //SecureRandom random = SecureRandom.getInstance("SHA256");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeyFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public static Key generateSymmetricKey(String password){
        byte[] passBytes = password.getBytes();
        assert passBytes.length == 16 : passBytes.length;
        
        /*****THIS SHOULD USE SALT, ITERATIONS, ETC***/
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
}
