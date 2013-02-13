/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author goggin
 */
public class ServerTest {
    
    public ServerTest() {
    }
        
    private void sleep(int pause) {
        try {
            Thread.sleep(pause);
        } catch (InterruptedException ex) {
            System.out.println("Thread.sleep interrupted");
        }
    }
    
    @Test
    public void testSendAndReceive() {
        Node serverNode = new Node("1", "localhost", 4445);
        Server server = new Server(serverNode);
        
        Collection<Message> messages;
        Vector<String> recieved_ids = new Vector<String>();
        int tries = 0;
        
        ServerThread serverThread = new ServerThread(server);
        serverThread.start();
        ClientThread clientThread = new ClientThread(serverNode);
        clientThread.start();
        
        while (recieved_ids.size() != 100 && tries++ < 1000) {
            System.out.println("waiting for messages...");
            messages = server.waitForMessages();
            for (Message m : messages) {
                recieved_ids.add(m.getMessageId());
            }
            System.out.println("recieved " + recieved_ids.size() + " messages!");
        }
        
        assertEquals(100, recieved_ids.size());
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String mid = recieved_ids.get(i * 10 + j);
                System.out.println(mid);
                assertEquals(i + "-" + j, mid);
            }
        }
    }
    
    private static class ClientThread extends Thread {
        private Node recipient;
        
        public ClientThread(Node r) {
            recipient = r;
        }
        
        public void run() {
            Client client = new Client();
            Node clientNode = new Node("client", "not-used", 9999);
            Message m = new Message(recipient, clientNode, "1");
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    m.setmessageId(i + "-" + j);
                    client.send(m);
                }
            }
            
        }
    }
    
    private static class ServerThread extends Thread {
        private Server server;
        
        public ServerThread(Server s) {
            server = s;
        }
        
        public void run() {
            this.server.listen();
        }
   }
}
