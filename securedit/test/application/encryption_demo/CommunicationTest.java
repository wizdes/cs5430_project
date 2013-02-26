/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import security_layer.KeyFactory;
import security_layer.SecureTransportInterface;
import security_layer.MA_Msg1;
import security_layer.MA_Msg2;
import security_layer.MA_Msg3;
import transport_layer.network.Node;

/**
 *
 * @author goggin
 */
public class CommunicationTest {
    
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
        theirCommunicator = new Communication(password_1, theirNode);
    }

    @After
    public void tearDown() throws Exception {
        myCommunicator.shutdown();
        theirCommunicator.shutdown();
    }

    // they authenticate and send
    @Test
    public void testSendAESMessage_1() {
        int iterations = 100;
        int recieved = 0;
        assertTrue(theirCommunicator.authenticateMachine(myNode));
       
        new SendingThread(theirCommunicator, new AESMessageSender(), myNode, iterations).start();
        
        while (recieved < iterations) {
            Collection<Message> messages = myCommunicator.waitForMessages();
            for (Message m : messages) {
                StringMessage dm = (StringMessage)m;
                assertEquals("hello world " + recieved++, dm.getContents());
            }
        }
        
        assertEquals(recieved, iterations);
    }    
    
    // I authenticate they send
    @Test
    public void testSendAESMessage_2() {
        int iterations = 100;
        int recieved = 0;
        assertTrue(myCommunicator.authenticateMachine(theirNode));
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        
        new SendingThread(theirCommunicator, new AESMessageSender(), myNode, iterations).start();
        
        while (recieved < iterations) {
            Collection<Message> messages = myCommunicator.waitForMessages();
            for (Message m : messages) {
                StringMessage dm = (StringMessage)m;
                assertEquals("hello world " + recieved++, dm.getContents());
            }
        }
        
        assertEquals(recieved, iterations);
    }
    
    //@Test
 /*   public void testSendRSAMessage_Msg01() {
        int iterations = 100;
        int recieved = 0;
        new SendingThread(theirCommunicator, new RSAMessageSender_msg01(), myNode, iterations).start();
        
        while (recieved < iterations) {
            Collection<Message> messages = myCommunicator.waitForMessages();
            for (Message m : messages) {
                MA_Msg1 dm = (MA_Msg1)m;
                assertEquals(recieved, dm.getNonce());
                recieved++;
            }
        }
        
        assertEquals(recieved, iterations);
    }     

    //@Test
    public void testSendRSAMessage_Msg02() {
        int iterations = 100;
        int recieved = 0;
        new SendingThread(theirCommunicator, new RSAMessageSender_msg02(), myNode, iterations).start();
        
        while (recieved < iterations) {
            Collection<Message> messages = myCommunicator.waitForMessages();
            for (Message m : messages) {
                MA_Msg2 dm = (MA_Msg2)m;
                assertEquals(recieved, dm.getNonce2());
                assertEquals(recieved, dm.getNonce1Response());
                recieved++;
            }
        }
        
        assertEquals(recieved, iterations);
    }  
    
    //@Test
    public void testSendRSAMessage_Msg03() {
        int iterations = 100;
        int recieved = 0;
        new SendingThread(theirCommunicator, new RSAMessageSender_msg03(), myNode, iterations).start();
        
        while (recieved < iterations) {
            Collection<Message> messages = myCommunicator.waitForMessages();
            for (Message m : messages) {
                MA_Msg3 dm = (MA_Msg3)m;
                assertEquals(recieved, dm.getNonce2Response());
                recieved++;
            }
        }
        
        assertEquals(recieved, iterations);
    }     */
    
    private static class SendingThread extends Thread {
        private CommunicationInterface communicator;
        int iterations;
        private Node target;
        private MessageSender sender;
        
        public SendingThread(CommunicationInterface c, MessageSender sender, Node target, int i) {
            communicator = c;
            this.target = target;
            iterations = i;
            this.sender = sender;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                sender.sendMessage(communicator, target, i);
            }
        }
    }
    
    private static interface MessageSender {
        public void sendMessage(CommunicationInterface c, Node target, int i);
    }    
        
    private static class AESMessageSender implements MessageSender {
        
        @Override
        public void sendMessage(CommunicationInterface c, Node target, int i) {
            StringMessage dm = new StringMessage(target, "message-" + i, "hello world " + i);
            c.sendAESEncryptedMessage(dm);
        }

    }
    
    private static class RSAMessageSender_msg01 implements MessageSender {
        @Override
        public void sendMessage(CommunicationInterface c, Node target, int i) {
            Message m = new MA_Msg1(target, i);
            c.sendRSAEncryptedMessage(m);
        }
    }       

    private static class RSAMessageSender_msg02 implements MessageSender {
        @Override
        public void sendMessage(CommunicationInterface c, Node target, int i) {
            Message m = new MA_Msg2(target, secretKey, i, i);
            c.sendRSAEncryptedMessage(m);
        }
    }
    
    private static class RSAMessageSender_msg03 implements MessageSender {
        @Override
        public void sendMessage(CommunicationInterface c, Node target, int i) {
            Message m = new MA_Msg3(target, i);
            c.sendRSAEncryptedMessage(m);
        }
    }     
}
