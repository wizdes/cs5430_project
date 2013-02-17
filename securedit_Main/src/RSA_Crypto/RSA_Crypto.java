/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RSA_Crypto;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author yjli_000
 */
public class RSA_Crypto implements RSA_Crypto_Interface{

    private PrivateKey pri_k = null;
    private PublicKey pub_k = null;
    
    public RSA_Crypto(){
        genNewKeys();
    }
    
    @Override
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

    @Override
    public PrivateKey getPrivateKey() {
        return pri_k;
    }

    @Override
    public PublicKey getPublicKey() {
        return pub_k;
    }

    @Override
    public byte[] PublicKeyEncrypt(PublicKey pk, byte[] raw_data) {
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

    @Override
    public byte[] PrivateKeyDecrypt(PrivateKey pk, byte[] encrypted_data) {
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
