/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.Message;

/**
 *
 */
public class Network implements NetworkInterface {
    
    private Server server;
    private Client client;
    private Collection<Node> neighbors = new LinkedList<>();
    
    public Network(Node host) {
        client = new Client();
        server = new Server(host);
        server.listen();
    }
    
    @Override
    public void sendMessage(Message m) {
        client.send(m);
    }
    
    @Override
    public Collection<Message> waitForMessages() {
        return server.waitForMessages();
    }
    
    @Override
    public void addNeighbor(Node n) {
        neighbors.add(n);
    }
    
    @Override
    public Collection<Node> readNeighbors(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (br == null) {
            return null;
        }
        
        String line;
        try {
            while ((line = br.readLine()) != null) {
               String[] parts = line.split(":");
               int port = Integer.parseInt(parts[2]);
               addNeighbor(new Node(parts[0], parts[1], port));
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return getNeighbors();
    }
    
    @Override
    public Collection<Node> getNeighbors() {
        return neighbors;
    }
    
    public static void log(String msg) {
        System.out.println(msg);
    }
    
    public static void logError(String msg) {
        System.err.println(msg);
    }


}
