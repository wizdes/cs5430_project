/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

/**
 *
 * @author yjli_000
 */
public class RSA_Crypto {

    private PrivateKey pri_k = null;
    private PublicKey pub_k = null;
    
    public RSA_Crypto(){
        genNewKeys();
    }
    
    public boolean genNewKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            //SecureRandom random = SecureRandom.getInstance("SHA256");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            pri_k = pair.getPrivate();
            pub_k = pair.getPublic();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSA_Crypto.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    public PrivateKey getPrivateKey() {
        return pri_k;
    }

    public PublicKey getPublicKey() {
        return pub_k;
    }

    static public byte[] PublicKeyEncrypt(PublicKey pk, byte[] raw_data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            byte[] encrypted = cipher.doFinal(raw_data);
            return encrypted;
        } catch (Exception ex){
            Logger.getLogger(RSA_Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    static public byte[] PrivateKeyDecrypt(PrivateKey pk, byte[] encrypted_data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pk);
            byte[] raw = cipher.doFinal(encrypted_data);
            return raw;
        } catch (Exception ex){
            Logger.getLogger(RSA_Crypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    
    
}
