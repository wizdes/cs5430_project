/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.messages.DemoMessage;
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
    Node thirdNode = new Node("2", "localhost", 4003);
    
    CommunicationInterface myCommunicator;
    CommunicationInterface theirCommunicator;
    CommunicationInterface thirdCommunicator;
    
    static String password_0 = "pass0000pass0000";
    static String password_1 = "pass1111pass1111";
    static String password_2 = "pass2222pass2222";
    static SecretKey secretKey =  (SecretKey)KeyFactory.generateSymmetricKey();
        
    @Before
    public void setUp() throws Exception {
        myCommunicator = new Communication(password_0, myNode);
        theirCommunicator = new Communication(password_1, theirNode);
        thirdCommunicator = new Communication(password_2, thirdNode);
    }

    @After
    public void tearDown() throws Exception {
        myCommunicator.shutdown();
        theirCommunicator.shutdown();
        thirdCommunicator.shutdown();
    }
    
    @Test
    public void testMachineAuthentication() {
        assertTrue(myCommunicator.authenticateMachine(theirNode));
        assertTrue(myCommunicator.authenticateMachine(thirdNode));
        
        int iterations = 100;
        int received = 0;
        for (int i = 0; i < iterations; i++) {
            DemoMessage dm = new DemoMessage(theirNode, "", "hello world " + i);
            myCommunicator.sendAESEncryptedMessage(dm);
            dm = new DemoMessage(thirdNode, "", "hello world " + i);
            myCommunicator.sendAESEncryptedMessage(dm);
        }
        
        while (received < iterations) {
            for (Message m : theirCommunicator.waitForMessages()) {
                if (m instanceof DemoMessage) {
                    DemoMessage dm2 = (DemoMessage)m;
                    System.out.println(dm2.getContents());
                    assertEquals("hello world " + received++, dm2.getContents());
                }
            }
        }
        assertTrue(received == iterations);
        
        received = 0;
        while (received < iterations) {
            for (Message m : thirdCommunicator.waitForMessages()) {
                if (m instanceof DemoMessage) {
                    DemoMessage dm2 = (DemoMessage)m;
                    System.out.println(dm2.getContents());
                    assertEquals("hello world " + received++, dm2.getContents());
                }
            }
        }
        assertTrue(received == iterations);
        
        received = 0;
        for (int i = 0; i < iterations; i++) {
            DemoMessage dm = new DemoMessage(myNode, "", "hello world " + i);
            theirCommunicator.sendAESEncryptedMessage(dm);
        }
        
        while (received < iterations) {
            for (Message m : myCommunicator.waitForMessages()) {
                if (m instanceof DemoMessage) {
                    DemoMessage dm2 = (DemoMessage)m;
                    System.out.println(dm2.getContents());
                    assertEquals("hello world " + received++, dm2.getContents());
                }
            }
        }        
        assertTrue(received == iterations);
        
        received = 0;
        for (int i = 0; i < iterations; i++) {
            DemoMessage dm = new DemoMessage(myNode, "", "hello world " + i);
            thirdCommunicator.sendAESEncryptedMessage(dm);
        }
        
        while (received < iterations) {
            for (Message m : myCommunicator.waitForMessages()) {
                if (m instanceof DemoMessage) {
                    DemoMessage dm2 = (DemoMessage)m;
                    System.out.println(dm2.getContents());
                    assertEquals("hello world " + received++, dm2.getContents());
                }
            }
        }        
        assertTrue(received == iterations);        
    }
    
}
