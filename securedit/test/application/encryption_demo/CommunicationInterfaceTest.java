/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

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
        Profile.deleteProfile(myIdent);
        Profile p1 = Profile.writeProfile(myIdent, password_0, 4000, "localhost");
        
        Profile.deleteProfile(theirIdent);
        Profile p2 = Profile.writeProfile(theirIdent, password_1, 4001, "localhost");
        
        Profile.deleteProfile(thirdIdent);
        Profile p3 = Profile.writeProfile(thirdIdent, password_2, 4002, "localhost");
        
        p1.addPublicKeysFrom(p2); p1.addPublicKeysFrom(p3); p1.save(password_0);
        p2.addPublicKeysFrom(p1); p2.addPublicKeysFrom(p3); p2.save(password_1);
        p3.addPublicKeysFrom(p1); p3.addPublicKeysFrom(p2); p3.save(password_2);
        
        myCommunicator = new Communication(p1, password_0);
        theirCommunicator = new Communication(p2, password_1);
        thirdCommunicator = new Communication(p3, password_2);        
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
