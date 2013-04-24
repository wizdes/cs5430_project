/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package document;

import application.encryption_demo.Communication;
import application.encryption_demo.CommunicationInterface;
import application.encryption_demo.CommunicationInterfaceTest;
import application.encryption_demo.Messages.Message;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import security_layer.Profile;

/**
 *
 * @author goggin
 */
public class NetworkDocumentTest {
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
        p1 = Profile.createProfile(p1Ident, password_1, p1Port, "localhost");
//        p1 = Profile.readProfile(p1Ident, password_1);
        
        Profile.deleteProfile(p2Ident);
        p2 = Profile.createProfile(p2Ident, password_2, p2Port, "localhost");
//        p2 = Profile.readProfile(p2Ident, password_2);
        
        Profile.deleteProfile(p3Ident);
        p3 = Profile.createProfile(p3Ident, password_3, p3Port, "localhost");
//        p3 = Profile.readProfile(p3Ident, password_3);
        
        p1.addPublicKeysFrom(p2); p1.addPublicKeysFrom(p3); p1.save(password_1);
        p2.addPublicKeysFrom(p1); p2.addPublicKeysFrom(p3); p2.save(password_2);
        p3.addPublicKeysFrom(p1); p3.addPublicKeysFrom(p2); p3.save(password_3);
        
        p1Communicator = new Communication(p1, password_1);
        p2Communicator = new Communication(p2, password_2);
        p3Communicator = new Communication(p3, password_3);  
        
        ArrayList<String> documents = new ArrayList<>();
        p1Communicator.updatePeers(p2Ident, "localhost", p2Port, documents, false);
        p1Communicator.updatePeers(p3Ident, "localhost", p3Port, documents, false);
        p2Communicator.updatePeers(p1Ident, "localhost", p1Port, documents, false);
        p2Communicator.updatePeers(p3Ident, "localhost", p3Port, documents, false);
        p3Communicator.updatePeers(p1Ident, "localhost", p1Port, documents, false);
        p3Communicator.updatePeers(p2Ident, "localhost", p2Port, documents, false);    
        
        assertTrue(p1Communicator.authenticateMachine(p2Ident));
        assertTrue(p1Communicator.authenticateMachine(p3Ident));        
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
    
    public NetworkDocumentTest() {
        
    }

    @Test
    public void testRequestInsert() {
        NetworkDocumentInterface owner = new NetworkDocument(p1Communicator, p1Ident, p1Ident, "document");
        NetworkDocumentInterface client2 = new NetworkDocument(p2Communicator, p2Ident, p1Ident, "document");
        NetworkDocumentInterface client3 = new NetworkDocument(p3Communicator, p3Ident, p1Ident, "document");
        
        owner.addUserToLevel(p2Ident, 0);
        owner.addUserToLevel(p3Ident, 0);
        
        DocumentCommListener thread1 = new DocumentCommListener(p1Communicator, owner);
        DocumentCommListener thread2 = new DocumentCommListener(p2Communicator, client2);
        DocumentCommListener thread3 = new DocumentCommListener(p3Communicator, client3);
        thread1.start(); thread2.start(); thread3.start();
        
        // when the owner applies an update it should get broadcast to all of the other documents
        owner.requestInsert(0, Document.BOF, Document.EOF, "hello");
        pause(100);
        
        assertEquals("hello", client2.getString());
        assertEquals("hello", client3.getString());
        
        // When a client requests an appropriate update, that should be propogated everywhere
        client2.requestInsert(0, 4, 5, " world");
        pause(100);
        
        assertEquals("hello world", owner.getString());
        assertEquals("hello world", client2.getString());
        assertEquals("hello world", client3.getString());
        
        // when a client requests an inappropriate update, it should be reported nowhere
        client2.requestInsert(99, -1, 0, "shouldn't see this");
        pause(100);
        
        assertEquals("hello world", owner.getString());
        assertEquals("hello world", client2.getString());
        assertEquals("hello world", client3.getString());
        
        // when the owner updates something at a higher level, the clients should see 'xxx'
        owner.requestInsert(4, -1, 0, "owner ");
        
        pause(100);
        
        assertEquals("owner hello world", owner.getString());
        assertEquals("XXXXXXhello world", client2.getString());
        assertEquals("XXXXXXhello world", client3.getString());  
        
        // a clients request should be forward just to those who can see them
        owner.addUserToLevel(p2Ident, 1);
        client2.requestInsert(1, -1, 0, "client ");
        pause(100);
        
        assertEquals("client owner hello world", owner.getString());
        assertEquals("client XXXXXXhello world", client2.getString());
        assertEquals("XXXXXXXXXXXXXhello world", client3.getString());

        // when a client requests to remove items he can't see, the request is ignored
        client3.requestRemove(0, 6);
        pause(1000);

        assertEquals("client owner hello world", owner.getString());
        assertEquals("client XXXXXXhello world", client2.getString());
        assertEquals("XXXXXXXXXXXXXhello world", client3.getString());

        // an authorized client can remove the text and it is broadcast
        client2.requestRemove(0, 6);
        pause(100);

        assertEquals("owner hello world", owner.getString());
        assertEquals("XXXXXXhello world", client2.getString());
        assertEquals("XXXXXXhello world", client3.getString());
        
        // when the owner removes items, they are broadcast to clients
        owner.requestRemove(0, 5);
        owner.requestRemove(5, 10);
        pause(100);

        assertEquals("hello", owner.getString());
        assertEquals("hello", client2.getString());
        assertEquals("hello", client3.getString());        
        
        thread1.stopListening(); thread2.stopListening(); thread3.stopListening();
    }
    
    
    @Test
    public void testLevelPropogation() {
        NetworkDocumentInterface owner = new NetworkDocument(p1Communicator, p1Ident, p1Ident, "document");
        NetworkDocumentInterface client2 = new NetworkDocument(p2Communicator, p2Ident, p1Ident, "document");
        NetworkDocumentInterface client3 = new NetworkDocument(p3Communicator, p3Ident, p1Ident, "document");
        
        owner.addUserToLevel(p2Ident, 1);
        owner.addUserToLevel(p3Ident, 2);
        
        DocumentCommListener thread1 = new DocumentCommListener(p1Communicator, owner);
        DocumentCommListener thread2 = new DocumentCommListener(p2Communicator, client2);
        DocumentCommListener thread3 = new DocumentCommListener(p3Communicator, client3);
        thread1.start(); thread2.start(); thread3.start();
        
        // when the owner applies an update it should get broadcast to all of the other documents
        owner.requestInsert(0, Document.BOF, Document.EOF, "000");
        pause(100);
        
        assertEquals("000", owner.getString());
        assertEquals("000", client2.getString());
        assertEquals("000", client3.getString());
        
        owner.requestInsert(1, Document.BOF, Document.EOF, "111 ");
        pause(100);
        
        assertEquals("111 000", owner.getString());
        assertEquals("111 000", client2.getString());
        assertEquals("111 000", client3.getString());

        owner.requestInsert(2, Document.BOF, Document.EOF, "222 ");
        pause(100);
        
        assertEquals("222 111 000", owner.getString());
        assertEquals("XXXX111 000", client2.getString());
        assertEquals("222 111 000", client3.getString());

        owner.requestInsert(3, Document.BOF, Document.EOF, "333 ");
        pause(100);
        
        assertEquals("333 222 111 000", owner.getString());
        assertEquals("XXXXXXXX111 000", client2.getString());
        assertEquals("XXXX222 111 000", client3.getString());
        
        // declassify 222
        owner.assignLevel(1, 4, 7);
        pause(200);

        assertEquals("333 222 111 000", owner.getString());
        assertEquals("XXXX222 111 000", client2.getString());
        assertEquals("XXXX222 111 000", client3.getString());
        
        // classify 000
        owner.assignLevel(3, 12, 14);
        pause(200);

        assertEquals("333 222 111 000", owner.getString());
        assertEquals("XXXX222 111 XXX", client2.getString());
        assertEquals("XXXX222 111 XXX", client3.getString());
        
        // set 111 to 2
        owner.assignLevel(2, 8, 11);
        pause(200);

        assertEquals("333 222 111 000", owner.getString());
        assertEquals("XXXX222 XXXXXXX", client2.getString());
        assertEquals("XXXX222 111 XXX", client3.getString());        
        
        thread1.stopListening(); thread2.stopListening(); thread3.stopListening();
    }
    
    @Test
    public void testUserLevelPropogation() {
        NetworkDocumentInterface owner = new NetworkDocument(p1Communicator, p1Ident, p1Ident, "document");
        NetworkDocumentInterface client2 = new NetworkDocument(p2Communicator, p2Ident, p1Ident, "document");
        NetworkDocumentInterface client3 = new NetworkDocument(p3Communicator, p3Ident, p1Ident, "document");
                
        DocumentCommListener thread1 = new DocumentCommListener(p1Communicator, owner);
        DocumentCommListener thread2 = new DocumentCommListener(p2Communicator, client2);
        DocumentCommListener thread3 = new DocumentCommListener(p3Communicator, client3);
        thread1.start(); thread2.start(); thread3.start();
        
        owner.addUserToLevel(p2Ident, 1);
        owner.addUserToLevel(p3Ident, 2);
        pause(100);
        
        assertEquals(1, client2.getLevelForUser(p2Ident));
        assertEquals(2, client3.getLevelForUser(p3Ident));
        
        owner.addUserToLevel(p2Ident, 2);
        pause(100);

        assertEquals(2, client2.getLevelForUser(p2Ident));
        assertEquals(2, client3.getLevelForUser(p3Ident));        
        
        thread1.stopListening(); thread2.stopListening(); thread3.stopListening();
    }    
    
    public class DocumentCommListener extends Thread {
        private CommunicationInterface comm;
        private NetworkDocumentInterface doc;
        private boolean listening = true;
        
        public DocumentCommListener(CommunicationInterface ci, NetworkDocumentInterface ndi) {
            this.comm = ci;
            this.doc = ndi;
        }
        
        public void stopListening() {
            this.listening = false;
        }
        
        @Override
        public void run() {
            while (listening) {
                for (Message m : this.comm.waitForMessages()) {
                    if (m instanceof CommandMessage) {
                        this.doc.processMessage((CommandMessage)m);
                    }
                }
            }
        }
        
    }

}
