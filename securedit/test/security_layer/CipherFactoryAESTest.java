/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;
import application.messages.DemoMessage;
import application.messages.Message;
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
import security_layer.machine_authentication.Msg01_AuthenticationRequest;
import security_layer.machine_authentication.Msg02_KeyResponse;
import security_layer.machine_authentication.Msg03_AuthenticationAgreement;
import transport_layer.network.Node;


/**
 *
 * @author yjli_000
 */
public class CipherFactoryAESTest {

    Node theirNode = new Node("1", "localhost", 4002);
    Node myNode = new Node("2", "localhost", 4002);
    
    @Test
    public void testAESGen(){
        try {
            Message m = new Message(theirNode, "asdf");
            Message Trudy_M = new Message(myNode, "asdf");
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
