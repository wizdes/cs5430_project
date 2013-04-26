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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
public class NetworkDocumentTest {
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
    
    NetworkDocumentInterface owner;
    NetworkDocumentInterface client2;
    NetworkDocumentInterface client3;
    
    DocumentCommListener thread1;
    DocumentCommListener thread2;
    DocumentCommListener thread3;
    
    @Before
    public void setUp() throws Exception {
        p1 = new Profile(p1Ident, "localhost", p1Port);
        p2 = new Profile(p2Ident, "localhost", p2Port);
        p3 = new Profile(p3Ident, "localhost", p3Port);
        
        ConcurrentMap <String, NetworkDocumentInterface> documentMap1 = new ConcurrentHashMap<>();
        ConcurrentMap <String, NetworkDocumentInterface> documentMap2 = new ConcurrentHashMap<>();
        ConcurrentMap <String, NetworkDocumentInterface> documentMap3 = new ConcurrentHashMap<>();
        
        p1Communicator = new Communication(p1, documentMap1);
        p2Communicator = new Communication(p2, documentMap2);
        p3Communicator = new Communication(p3, documentMap3);
                
        NetworkDocument nd1 = new NetworkDocument(p1Communicator, p1Ident, p1Ident, "document");
        documentMap1.put("document", nd1);
        NetworkDocument nd2 = new NetworkDocument(p1Communicator, p2Ident, p1Ident, "document");
        documentMap2.put("document", nd2);
        NetworkDocument nd3 = new NetworkDocument(p1Communicator, p3Ident, p1Ident, "document");
        documentMap3.put("document", nd3);
        
        ArrayList<String> documents = new ArrayList(documentMap1.keySet());
        p1Communicator.updatePeers(p2Ident, "localhost", p2Port, new ArrayList<String>(), false);
        p1Communicator.updatePeers(p3Ident, "localhost", p3Port, new ArrayList<String>(), false);
        p2Communicator.updatePeers(p1Ident, "localhost", p1Port, documents, false);
        p2Communicator.updatePeers(p3Ident, "localhost", p3Port, new ArrayList<String>(), false);
        p3Communicator.updatePeers(p1Ident, "localhost", p1Port, documents, false);
        p3Communicator.updatePeers(p2Ident, "localhost", p2Port, new ArrayList<String>(), false);    
        
        char[] PIN2 = p1Communicator.generatePIN(p2Ident, documents.get(0));
        p2Communicator.initializeSRPAuthentication(p1Ident, documents.get(0), password_2, PIN2);
        assertTrue(p2Communicator.authenticate(p1Ident, documents.get(0), password_2));

        char[] PIN3 = p1Communicator.generatePIN(p3Ident, documents.get(0));
        p3Communicator.initializeSRPAuthentication(p1Ident, documents.get(0), password_3, PIN3);
        assertTrue(p3Communicator.authenticate(p1Ident, documents.get(0), password_3));
        
        owner = new NetworkDocument(p1Communicator, p1Ident, p1Ident, "document");
        client2 = new NetworkDocument(p2Communicator, p2Ident, p1Ident, "document");
        client3 = new NetworkDocument(p3Communicator, p3Ident, p1Ident, "document");   
        
        thread1 = new DocumentCommListener(p1Communicator, owner);
        thread2 = new DocumentCommListener(p2Communicator, client2);
        thread3 = new DocumentCommListener(p3Communicator, client3);
        thread1.start(); thread2.start(); thread3.start();        
    }

    @After
    public void tearDown() throws Exception {
        thread1.stopListening(); 
        thread2.stopListening(); 
        thread3.stopListening();
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

    @Test   
    public void testRequestInsert() {
        
        owner.addUserToLevel(p2Ident, 0);
        owner.addUserToLevel(p3Ident, 0);
        
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
    }
    
    
    @Test
    public void testLevelPropogation() {
        
        owner.addUserToLevel(p2Ident, 1);
        owner.addUserToLevel(p3Ident, 2);
        
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
    }
    
    @Test
    public void testUserLevelPropogation() {        
        owner.addUserToLevel(p2Ident, 1);
        owner.addUserToLevel(p3Ident, 2);
        pause(100);
        
        assertEquals(1, client2.getLevelForUser(p2Ident));
        assertEquals(2, client3.getLevelForUser(p3Ident));
        
        owner.addUserToLevel(p2Ident, 2);
        pause(100);

        assertEquals(2, client2.getLevelForUser(p2Ident));
        assertEquals(2, client3.getLevelForUser(p3Ident));
    } 
    
    @Test
    public void testRequestLevelChange() {
        owner.addUserToLevel(p2Ident, 1);
        owner.addUserToLevel(p3Ident, 2);
        pause(100);
        
        assertEquals(1, client2.getLevelForUser(p2Ident));
        assertEquals(2, client3.getLevelForUser(p3Ident));
        
        NetworkDocument.autoApprove = true;
        client2.requestChangeLevel(3);
        client3.requestChangeLevel(4);
        pause(100);

        assertEquals(3, client2.getLevelForUser(p2Ident));
        assertEquals(4, client3.getLevelForUser(p3Ident));
        
        NetworkDocument.autoApprove = false;
        NetworkDocument.autoDeny = true;
        client2.requestChangeLevel(5);
        client3.requestChangeLevel(6);
        pause(100);
  
        assertEquals(3, client2.getLevelForUser(p2Ident));
        assertEquals(4, client3.getLevelForUser(p3Ident));
    }
    
    @Test
    public void testBootstrap() {        
        owner.requestInsert(0, Document.BOF, Document.EOF, "000");
        owner.requestInsert(1, Document.BOF, Document.EOF, "111");
        owner.requestInsert(2, Document.BOF, Document.EOF, "222");
        
        owner.addUserToLevel(p2Ident, 1);
        pause(100);
        
        assertEquals("222111000", owner.getString());
        assertEquals("", client2.getString());
        assertEquals("", client3.getString());
        
        client2.bootstrap();
        pause(100);
        
        assertEquals("222111000", owner.getString());
        assertEquals("XXX111000", client2.getString());
        assertEquals("", client3.getString());    
        
        client2.requestInsert(0, Document.BOF, Document.EOF, "000");
        pause(100);
        
        assertEquals("000222111000", owner.getString());
        assertEquals("000XXX111000", client2.getString());
        assertEquals("", client3.getString());         
        
        owner.addUserToLevel(p3Ident, 2);
        client3.bootstrap();
        client2.requestInsert(1, Document.BOF, Document.EOF, "111");
        pause(100);  
        
        assertEquals("111000222111000", owner.getString());
        assertEquals("111000XXX111000", client2.getString());
        assertEquals("111000222111000", client3.getString()); 
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
