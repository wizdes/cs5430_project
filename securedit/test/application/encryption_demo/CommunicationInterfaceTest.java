/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.messages.Message;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import javax.crypto.SecretKey;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import security_layer.KeyFactory;
import security_layer.SecureTransportInterface;
import transport_layer.network.Node;

/**
 *
 * @author goggin
 */
public class CommunicationInterfaceTest {
        Node myNode = new Node("0", "localhost", 4001);
    Node theirNode = new Node("1", "localhost", 4002);
    CommunicationInterface myCommunicator;
    CommunicationInterface theirCommunicator;
    
    static String password_0 = "pass0000pass0000";
    static String password_1 = "pass1111pass1111";
    static SecretKey secretKey =  (SecretKey)KeyFactory.generateSymmetricKey();
        
    @Before
    public void setUp() throws Exception {
        myCommunicator = new Communication(password_0, myNode);
        System.out.println("will we see this?");
        SecureTransportInterface s1 = myCommunicator.getSecureTransport();
        theirCommunicator = new Communication(password_1, theirNode);
    }

    @After
    public void tearDown() throws Exception {
        myCommunicator.shutdown();
        theirCommunicator.shutdown();
    }
    
    @Test
    public void testMachineAuthentication() {
        boolean result = myCommunicator.authenticateMachine(theirNode);
        assertTrue(result);
    }
    
}
