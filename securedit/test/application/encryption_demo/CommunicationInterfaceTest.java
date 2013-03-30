/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.encryption_demo.Messages.Message;
import application.encryption_demo.Messages.StringMessage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import security_layer.Profile;

/**
 *
 * @author goggin
 */
public class CommunicationInterfaceTest {
    String p1Ident = "1";
    String p2Ident = "2";
    String p3Ident = "3";
    
    CommunicationInterface p1Communicator;
    CommunicationInterface p2Communicator;
    CommunicationInterface p3Communicator;
    
    static String password_1 = "pass0000pass0000";
    static String password_2 = "pass1111pass1111";
    static String password_3 = "pass2222pass2222";
    
    static int p1Port = 4000;
    static int p2Port = 4001;
    static int p3Port = 4002;
    
    private Profile p1;
    private Profile p2;
    private Profile p3;
    
    @Before
    public void setUp() throws Exception {
        Profile.deleteProfile(p1Ident);
        p1 = Profile.writeProfile(p1Ident, password_1, p1Port, "localhost");
        
        Profile.deleteProfile(p2Ident);
        p2 = Profile.writeProfile(p2Ident, password_2, p2Port, "localhost");
        
        Profile.deleteProfile(p3Ident);
        p3 = Profile.writeProfile(p3Ident, password_3, p3Port, "localhost");
    }

    @After
    public void tearDown() throws Exception {
        p1Communicator.shutdown();
        p2Communicator.shutdown();
        p3Communicator.shutdown();
    }
    
    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationInterfaceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void testSendMessagesFrom(CommunicationInterface c1, 
                                     CommunicationInterface c2, 
                                     String c2Ident,
                                     int iterations) {
        int received = 0;
        for (int i = 0; i < iterations; i++) {
            StringMessage dm = new StringMessage("hello world " + i);
            c1.sendMessage(c2Ident, dm);
        }

        while (received < iterations) {
            for (Message m : c2.waitForMessages()) {
                if (m instanceof StringMessage) {
                    StringMessage dm2 = (StringMessage)m;
                    System.out.println(dm2.contents);
                    assertEquals("hello world " + received++, dm2.contents);
                }
            }
        }
        assertTrue(received == iterations);    
    }
    
    @Test
    public void testHumanAuthentication() {
        // Connect once from scratch
        int iterations = 100;
        ArrayList<String> documents = new ArrayList<>();
        documents.add("chat");
        p1Communicator = new Communication(p1, password_1);
        p2Communicator = new Communication(p2, password_2);
        p3Communicator = new Communication(p3, password_3);
        
        p1Communicator.updatePeers(p2Ident, "localhost", p2Port, documents, false);
        p2Communicator.updatePeers(p1Ident, "localhost", p1Port, documents, false);
        
        p1Communicator.authenticateHuman(p2Ident);
        pause(250);
        String pin = p2Communicator.getPIN(p1Ident);
        assertTrue(p1Communicator.updatePin(p2Ident, pin));
        assertTrue(p1Communicator.authenticateMachine(p2Ident));

        testSendMessagesFrom(p1Communicator, p2Communicator, p2Ident, iterations);
        testSendMessagesFrom(p2Communicator, p1Communicator, p1Ident, iterations);
        
        // Now start with fresh communicators, and we should be able to start
        // right from machineAuth
        p1Communicator.shutdown();
        p2Communicator.shutdown();
        p1Communicator = new Communication(p1, password_1);
        p2Communicator = new Communication(p2, password_2);   
        p1Communicator.updatePeers(p2Ident, "localhost", p2Port, documents, false);
        p2Communicator.updatePeers(p1Ident, "localhost", p1Port, documents, false);
        
        assertTrue(p1Communicator.authenticateMachine(p2Ident));
        testSendMessagesFrom(p1Communicator, p2Communicator, p2Ident, 100);
        testSendMessagesFrom(p2Communicator, p1Communicator, p1Ident, 100);  
        
        // Client resets keys
//        p1.save(password_1);
//        p1.updateProfilePassword(password_1);
//        p1Communicator.shutdown();
//        p2Communicator.shutdown();
//        p1Communicator = new Communication(p1, password_1);
//        p2Communicator = new Communication(p2, password_2); 
//        
//        p1Communicator.updatePeers(p2Ident, "localhost", p2Port, documents, false);
//        p2Communicator.updatePeers(p1Ident, "localhost", p1Port, documents, false);
//        
//        p1Communicator.authenticateHuman(p2Ident);
//        pause(250);
//        pin = p2Communicator.getPIN(p1Ident);
//        assertTrue(p1Communicator.updatePin(p2Ident, pin));
//        assertTrue(p1Communicator.authenticateMachine(p2Ident));
        
        // Server resets keys
//        p2.save(password_2);
//        p2.updateProfilePassword(password_2);
//        p1Communicator.shutdown();
//        p2Communicator.shutdown();
//        p1Communicator = new Communication(p1, password_1);
//        p2Communicator = new Communication(p2, password_2); 
//        p1Communicator.authenticateHuman(p2Ident);
//        pause(250);
//        pin = p2Communicator.getPINFor(p1Ident);
//        assertTrue(p1Communicator.updatePin(p2Ident, pin));
//        assertTrue(p1Communicator.authenticateMachine(p2Ident));
    }
    
    @Test
    public void testMachineAuthentication() {
        p1.addPublicKeysFrom(p2); p1.addPublicKeysFrom(p3); p1.save(password_1);
        p2.addPublicKeysFrom(p1); p2.addPublicKeysFrom(p3); p2.save(password_2);
        p3.addPublicKeysFrom(p1); p3.addPublicKeysFrom(p2); p3.save(password_3);
        
        p1Communicator = new Communication(p1, password_1);
        p2Communicator = new Communication(p2, password_2);
        p3Communicator = new Communication(p3, password_3);  
        
        ArrayList<String> documents = new ArrayList<>();
        documents.add("chat");
        p1Communicator.updatePeers(p2Ident, "localhost", p2Port, documents, false);
        p1Communicator.updatePeers(p3Ident, "localhost", p3Port, documents, false);
        p2Communicator.updatePeers(p1Ident, "localhost", p1Port, documents, false);
        p2Communicator.updatePeers(p3Ident, "localhost", p3Port, documents, false);
        p3Communicator.updatePeers(p1Ident, "localhost", p1Port, documents, false);
        p3Communicator.updatePeers(p2Ident, "localhost", p2Port, documents, false);
        
        assertTrue(p1Communicator.authenticateMachine(p2Ident));
        assertTrue(p1Communicator.authenticateMachine(p3Ident));
        int iterations = 100;
        int received = 0;
        for (int i = 0; i < iterations; i++) {
            StringMessage dm = new StringMessage("hello world " + i);
            p1Communicator.sendMessage(p2Ident, dm);
            dm = new StringMessage("hello world " + i);
            p1Communicator.sendMessage(p3Ident, dm);
        }
        
        while (received < iterations) {
            for (Message m : p2Communicator.waitForMessages()) {
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
            for (Message m : p3Communicator.waitForMessages()) {
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
            p2Communicator.sendMessage(p1Ident, dm);
        }
        
        while (received < iterations) {
            for (Message m : p1Communicator.waitForMessages()) {
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
            p3Communicator.sendMessage(p1Ident, dm);
        }
        
        while (received < iterations) {
            for (Message m : p1Communicator.waitForMessages()) {
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
