/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;
import application.messages.DemoMessage;
import application.messages.Message;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyPair;
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
import security_layer.machine_authentication.Msg01_AuthenticationRequest;
import security_layer.machine_authentication.Msg02_KeyResponse;
import security_layer.machine_authentication.Msg03_AuthenticationAgreement;
import transport_layer.network.Node;


/**
 *
 * @author yjli_000
 */
public class CipherFactoryRSATest {
    
    @Test
    public void testRSAGen(){
        try {
            KeyPair sk = KeyFactory.generateAsymmetricKeys();
            Cipher encrypt = CipherFactory.constructRSAEncryptionCipher(sk.getPublic());
            Cipher decrypt = CipherFactory.constructRSADecryptionCipher(sk.getPrivate());
            String elt = "This is a test";
            byte[] raw_data = new byte[elt.getBytes("UTF8").length];
            System.arraycopy(elt.getBytes("UTF8"), 0, raw_data, 0, elt.getBytes().length);
            byte[] cipher_data = encrypt.doFinal(raw_data);
            assert(!Arrays.equals(cipher_data, raw_data));
            byte[] decrypted_data = decrypt.doFinal(cipher_data);
            assert(Arrays.equals(decrypted_data, raw_data));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CipherFactoryRSATest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CipherFactoryAESTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(CipherFactoryAESTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
