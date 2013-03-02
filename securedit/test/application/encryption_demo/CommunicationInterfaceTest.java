/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class CommunicationInterfaceTest {
    String myIdent = "0";
    String theirIdent = "1";
    String thirdIdent = "2";
    
    CommunicationInterface myCommunicator;
    CommunicationInterface theirCommunicator;
    CommunicationInterface thirdCommunicator;
    
    static String password_0 = "pass0000pass0000";
    static String password_1 = "pass1111pass1111";
    static String password_2 = "pass2222pass2222";
        
    @Before
    public void setUp() throws Exception {
        myCommunicator = new Communication(myIdent, "localhost", 4000, password_0);
        theirCommunicator = new Communication(theirIdent, "localhost", 4001, password_1);
        thirdCommunicator = new Communication(thirdIdent, "localhost", 4002, password_2);
    }

    @After
    public void tearDown() throws Exception {
        myCommunicator.shutdown();
        theirCommunicator.shutdown();
        thirdCommunicator.shutdown();
    }
    
    @Test
    public void testMachineAuthentication() {
        assertTrue(myCommunicator.authenticateMachine(theirIdent));
        assertTrue(myCommunicator.authenticateMachine(thirdIdent));
        int iterations = 100;
        int received = 0;
        for (int i = 0; i < iterations; i++) {
            StringMessage dm = new StringMessage("hello world " + i);
            myCommunicator.sendMessage(theirIdent, dm);
            dm = new StringMessage("hello world " + i);
            myCommunicator.sendMessage(thirdIdent, dm);
        }
        
        while (received < iterations) {
            for (Message m : theirCommunicator.waitForMessages()) {
                if (m instanceof StringMessage) {
                    StringMessage dm2 = (StringMessage)m;
                    System.out.println(dm2.contents);
                    assertEquals("hello world " + received++, dm2.contents);
                }
            }
        }
        assertTrue(received == iterations);
        
        received = 0;
        while (received < iterations) {
            for (Message m : thirdCommunicator.waitForMessages()) {
                if (m instanceof StringMessage) {
                    StringMessage dm2 = (StringMessage)m;
                    System.out.println(dm2.contents);
                    assertEquals("hello world " + received++, dm2.contents);
                }
            }
        }
        assertTrue(received == iterations);
        
        received = 0;
        for (int i = 0; i < iterations; i++) {
            StringMessage dm = new StringMessage("hello world " + i);
            theirCommunicator.sendMessage(myIdent, dm);
        }
        
        while (received < iterations) {
            for (Message m : myCommunicator.waitForMessages()) {
                if (m instanceof StringMessage) {
                    StringMessage dm2 = (StringMessage)m;
                    System.out.println(dm2.contents);
                    assertEquals("hello world " + received++, dm2.contents);
                }
            }
        }        
        assertTrue(received == iterations);
        
        received = 0;
        for (int i = 0; i < iterations; i++) {
            StringMessage dm = new StringMessage("hello world " + i);
            thirdCommunicator.sendMessage(myIdent, dm);
        }
        
        while (received < iterations) {
            for (Message m : myCommunicator.waitForMessages()) {
                if (m instanceof StringMessage) {
                    StringMessage dm2 = (StringMessage)m;
                    System.out.println(dm2.contents);
                    assertEquals("hello world " + received++, dm2.contents);
                }
            }
        }        
        assertTrue(received == iterations);        
    }
    
}
