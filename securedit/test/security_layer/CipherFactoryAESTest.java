/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;
import java.security.Key;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import org.junit.Test;


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
