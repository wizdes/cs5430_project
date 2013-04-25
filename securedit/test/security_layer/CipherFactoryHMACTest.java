/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;
import application.encryption_demo.Messages.Message;
import java.security.Key;
import java.util.Arrays;
import org.junit.Test;


/**
 *
 * @author yjli_000
 */
public class CipherFactoryHMACTest {
    
    @Test
    public void testHMACGen(){
//        Message m = new TestMessage("Hello World");
//        Message Trudy_M = new TestMessage("Goodbye World");
//        Key sk = KeyFactory.generateSymmetricKey("passwordpassword");
//        byte[] HMAC_prime = CipherFactory.HMAC(sk, m);
//        assert(HMAC_prime != null);
//        assert(Arrays.equals(HMAC_prime, CipherFactory.HMAC(sk, m)));
//        assert(!Arrays.equals(HMAC_prime, CipherFactory.HMAC(sk, Trudy_M)));
    }
    
    private static class TestMessage implements Message {
        String contents;

        TestMessage(String contents) {
            this.contents = contents;
        }
    }
}
