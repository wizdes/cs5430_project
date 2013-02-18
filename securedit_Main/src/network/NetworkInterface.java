/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.File;
import java.util.Collection;
import messages.Message;

/**
 *
 * @author goggin
 */
public interface NetworkInterface {
    
    public Collection<Message> waitForMessages();
    
    public void sendMessage(Message m);
    
    public void addNeighbor(Node n);
    
    public Collection<Node> readNeighbors(File f);
    
    public Collection<Node> getNeighbors();
}
