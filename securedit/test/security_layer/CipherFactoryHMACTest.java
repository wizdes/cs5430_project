/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;
import application.encryption_demo.StringMessage;
import application.encryption_demo.Message;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
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
public class CipherFactoryHMACTest {

    Node theirNode = new Node("1", "localhost", 4002);
    Node myNode = new Node("2", "localhost", 4002);
    
    @Test
    public void testHMACGen(){
        Message m = new Message(theirNode, "asdf");
        Message Trudy_M = new Message(myNode, "asdf");
        Key sk = KeyFactory.generateSymmetricKey("password123");
        byte[] HMAC_prime = CipherFactory.HMAC(sk, m);
        assert(HMAC_prime != null);
        assert(Arrays.equals(HMAC_prime, CipherFactory.HMAC(sk, m)));
        assert(!Arrays.equals(HMAC_prime, CipherFactory.HMAC(sk, Trudy_M)));
    }
}
