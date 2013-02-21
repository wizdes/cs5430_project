/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.File;
import java.util.Collection;
import java.util.List;
import javax.crypto.SecretKey;
import messages.Message;

/**
 *
 * @author goggin
 */
public interface NetworkInterface {
    
    public Collection<Message> waitForMessages();
    
    public void sendMessage(Message m);
    public Message sendMessageAndAwaitReply(Message m);
        
    public void addNeighbor(Node n);
    
    public List<Node> readNeighbors(File f);
    
    public List<Node> getNeighbors();
    
    public Node getNeighbor(String nid);
    
    public void setSecret(SecretKey secret);
    
    public void shutdown();

    public Node getHostNode();
}

