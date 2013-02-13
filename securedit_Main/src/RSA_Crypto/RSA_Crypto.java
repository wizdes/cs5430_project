/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RSA_Crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);
            KeyPair pair = keyGen.generateKeyPair();
            pri_k = pair.getPrivate();
            pub_k = pair.getPublic();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSA_Crypto.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (NoSuchProviderException ex) {
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
    
}
