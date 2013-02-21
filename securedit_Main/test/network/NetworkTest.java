/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import messages.Message;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class NetworkTest {
    
    public NetworkTest() {
    }
    
    @Test
    public void testReadNeighbors() {
        Node me = new Node("1", "localhost", 4001);
        Network network = new Network(me);
        
        File f = new File("test/network/test_hosts.txt");
        Collection<Node> neighbors = network.readNeighbors(f);
        
        assertEquals(4, neighbors.size());
        assertTrue(neighbors.contains(new Node("2", "localhost", 4002)));
        assertTrue(neighbors.contains(new Node("3", "localhost", 4003)));
        assertTrue(neighbors.contains(new Node("4", "localhost", 4004)));
        assertTrue(neighbors.contains(new Node("5", "localhost", 4005)));
        
        assertFalse(neighbors.contains(new Node("1", "localhost", 4001)));
        
        network.shutdown();
    }
    
    @Test
    public void testSendAndAwaitReply() {
        int iterations = 1000;
        Node serverNode = new Node("1", "localhost", 4001);
        Node echoNode = new Node("2", "localhost", 4002);
        
        Network myNetwork = new Network(serverNode);
        Network echoNetwork = new Network(echoNode);
        
        Collection<Message> messages;
        ArrayList<String> recieved_ids = new ArrayList<>();
                
        ReplyServerThread echoThread = new ReplyServerThread(echoNetwork, iterations);
        echoThread.start();
        
        for (int i = 0; i < iterations; i++) {
            Message toSend = new Message(echoNode, i + "");
            Message response = myNetwork.sendMessageAndAwaitReply(toSend);
            assertNotNull(response);
            assertEquals(toSend.getMessageId(), response.getReplyTo());
        }
        
        myNetwork.shutdown();
        echoNetwork.shutdown();
    }
    
    
    private static class ReplyServerThread extends Thread {
        private Network echoNetwork;
        int iterations;

        public ReplyServerThread(Network n, int i) {
            echoNetwork = n;
            iterations = i;
        }
        
        @Override
        public void run() {
            int recieved = 0;
            while (recieved < iterations) {
                for (Message m : echoNetwork.waitForMessages()) {
                    Message reply = new Message(m.getFrom(), "client-" + recieved++);
                    reply.setReplyTo(m.getMessageId());
                    echoNetwork.sendMessage(reply);
                }
            }
        }
    }

}
