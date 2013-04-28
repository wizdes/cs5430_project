/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import document.Document;
import document.NetworkDocumentHandler;
import document.NetworkDocumentHandlerInterface;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import security_layer.Profile;
import security_layer.authentications.SRPAuthenticationTransport;

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
    
    static char[] password_1 = "pass00a000".toCharArray();          //less than 16
    static char[] password_2 = "pass1111passsfdasfa1111".toCharArray(); //more than 16
    static char[] password_3 = "pass2222pass2222".toCharArray();        //exactly 16
    
    static int p1Port = 4000;
    static int p2Port = 4001;
    static int p3Port = 4002;
    
    private Profile p1;
    private Profile p2;
    private Profile p3;
    
    @Before
    public void setUp() throws Exception {
        p1 = new Profile(p1Ident, "localhost", p1Port); 
        p2 = new Profile(p2Ident, "localhost", p2Port); 
        p3 = new Profile(p3Ident, "localhost", p3Port);         
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
                                     String docID,
                                     int iterations) {
        int received = 0;
        for (int i = 0; i < iterations; i++) {
            Document dm = new Document(c2Ident, docID);
            //StringMessage dm = new StringMessage("hello world " + i);
            c1.sendMessage(c2Ident, docID, dm);
        }

        while (received < iterations) {
            for (Message m : c2.waitForMessages()) {
                if (m instanceof Document) {
                    Document dm2 = (Document)m;
//                    System.out.println(dm2.contents);
                    received++;
                }
            }
        }
        assertTrue(received == iterations);    
    }
    
    @Test
    public void testHumanAuthentication() {
        // Connect once from scratch
        int iterations = 100;
        ConcurrentMap <String, NetworkDocumentHandlerInterface> documentMap = new ConcurrentHashMap<>();
        
        p1Communicator = new Communication(p1, documentMap);
        p2Communicator = new Communication(p2, null);
        p3Communicator = new Communication(p3, null);
                
        NetworkDocumentHandler nd = new NetworkDocumentHandler(p1Communicator, p1Ident, p1Ident, "document");
        documentMap.put("document", nd);
        
        ArrayList<String> documents = new ArrayList(documentMap.keySet());
        p2Communicator.updatePeers(p1Ident, "localhost", p1Port, documents);
        
        char[] PIN = p1Communicator.generatePIN(p2Ident, documents.get(0));
        assertTrue(p2Communicator.initializeSRPAuthentication(p1Ident, documents.get(0), password_2, PIN));
        assertTrue(p2Communicator.authenticate(p1Ident, documents.get(0), password_2));
        
        testSendMessagesFrom(p1Communicator, p2Communicator, p2Ident, documents.get(0), iterations);
        testSendMessagesFrom(p2Communicator, p1Communicator, p1Ident, documents.get(0), iterations);
    }
    
    @Test
    public void testAuthenticationFailures() {
        ConcurrentMap <String, NetworkDocumentHandlerInterface> documentMap = new ConcurrentHashMap<>();
        
        
        p2Communicator = new Communication(p2, null);
                                
        ArrayList<String> documents = new ArrayList();
        documents.add("document");
        p2Communicator.updatePeers(p1Ident, "localhost", p1Port, documents);
          
        boolean r;
        SRPAuthenticationTransport.AUTH_TIMEOUT_DELAY = 500;
        
        // Try to create an account when the owner is not online
        r = p2Communicator.initializeSRPAuthentication(p1Ident, "document", password_2, "SomePIN".toCharArray());
        assertFalse(r);
        
        // Try to login when the owner is not online
        r = p2Communicator.authenticate(p1Ident, documents.get(0), password_2);
        assertFalse(r);  
        
        SRPAuthenticationTransport.AUTH_TIMEOUT_DELAY = 1000;
        
        p1Communicator = new Communication(p1, documentMap);
        p3Communicator = new Communication(p3, null);        
        
        NetworkDocumentHandler nd = new NetworkDocumentHandler(p1Communicator, p1Ident, p1Ident, "document");
        documentMap.put("document", nd);
        
        // Try to create an account when the owner is not hosting a document
        r = p2Communicator.initializeSRPAuthentication(p1Ident, "Fakedocument", password_2, "SomePIN".toCharArray());
        assertFalse(r);
        
        // Try to create an account when no pin has been generated
        r = p2Communicator.initializeSRPAuthentication(p1Ident, documents.get(0), password_2, "SomePIN".toCharArray());
        assertFalse(r);
        
        // Try to create an account with the wrong PIN (but one has been generated)
        char[] PIN = p1Communicator.generatePIN(p2Ident, documents.get(0));
        r = p2Communicator.initializeSRPAuthentication(p1Ident, documents.get(0), password_2, "SomePIN".toCharArray());
        assertFalse(r);
        
        // Logging in should fail when you do not have an account
        r = p2Communicator.authenticate(p1Ident, documents.get(0), password_2);
        assertFalse(r);

        // Now account creation should succeed with correct PIN
        r = p2Communicator.initializeSRPAuthentication(p1Ident, documents.get(0), password_2, PIN);
        assertTrue(r);

        // Logging in should fail with the wrong password
        r = p2Communicator.authenticate(p1Ident, documents.get(0), "not_my_pass".toCharArray());
        assertFalse(r);
        
        // and finally succeed with the correct password
        r = p2Communicator.authenticate(p1Ident, documents.get(0), password_2);
        assertTrue(r);        
     }

}
