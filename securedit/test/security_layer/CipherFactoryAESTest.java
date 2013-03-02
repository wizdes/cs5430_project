/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;
import application.encryption_demo.Message;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import security_layer.KeyFactory;
import security_layer.SecureTransportInterface;
import transport_layer.network.Node;


/**
 *
 * @author yjli_000
 */
public class CipherFactoryAESTest {

    
    @Test
    public void testAESGen(){
        try {
            byte[] iv = CipherFactory.generateRandomIV();
            Key sk = KeyFactory.generateSymmetricKey("password123");
            Cipher encrypt = CipherFactory.constructAESEncryptionCipher(sk, iv);
            Cipher decrypt = CipherFactory.constructAESDecryptionCipher(sk, iv);
            String test = "A NEW HOPE";
            byte[] raw_bytes = test.getBytes();
            byte[] encrypted = encrypt.doFinal(raw_bytes);
            assert(!Arrays.equals(encrypted, raw_bytes));
            byte[] decrypted = decrypt.doFinal(encrypted);
            assert(Arrays.equals(raw_bytes, decrypted));
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CipherFactoryAESTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CipherFactoryAESTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
