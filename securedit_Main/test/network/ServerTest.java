/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.ArrayList;
import java.util.Collection;
import messages.Message;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class ServerTest {
    
    @Test
    public void testSendAndReceive() {
        Node serverNode = new Node("1", "localhost", 4445);
        Server server = new Server(serverNode);
        int iterations = 10;
        
        Collection<Message> messages;
        ArrayList<String> recieved_ids = new ArrayList<>();
        int tries = 0;

        server.listen();
        ClientThread clientThread = new ClientThread(serverNode, iterations);
        clientThread.start();
        
        while (recieved_ids.size() != (iterations * 10) && tries++ < 1000) {
            messages = server.waitForMessages();
            for (Message m : messages) {
                recieved_ids.add(m.getMessageId());
            }
        }
        
        // make sure we recieved all the messages
        assertEquals((iterations * 10), recieved_ids.size());
        
        // iterate and assert we saw all the messages we sent
        for (int i = 0; i < iterations; i++) {
            for (int j = 0; j < 10; j++) {
                String mid = recieved_ids.get(i * 10 + j);
                assertEquals(i + "-" + j, mid);
            }
        }
        
        // Assert all the client.send calls returned true
        assertEquals(true, clientThread.success);
        
        server.shutdown();
    }
    
    private static class ClientThread extends Thread {
        private Node recipient;
        private int iterations;
        public boolean success = true;
        
        public ClientThread(Node r, int i) {
            recipient = r;
            iterations = i;
        }
        
        @Override
        public void run() {
            Client client = new Client();
            Node clientNode = new Node("client", "not-used", 9999);
            Message m = new Message(recipient, clientNode, "1");
            for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < 10; j++) {
                    m.setmessageId(i + "-" + j);
                    success = success && client.send(m);
                }
            }
            
            client.closeSocketWith(recipient);            
        }
    }
}
