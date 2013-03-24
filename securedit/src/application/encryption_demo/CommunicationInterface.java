/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;

import application.encryption_demo.Peers.Peer;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import security_layer.SecureTransportInterface;

/**
 *
 */
public interface CommunicationInterface {
    public void broadcastDiscovery();
    public boolean sendMessage(String destination, Message m);
    public boolean authenticateMachine(String machineIdent);
    
    public Collection<Message> waitForMessages();
    
    public void depositMessage(Message m) throws NoSuchAlgorithmException;
    
    public boolean writeEncryptedFile(String filename, String contents);
    public String readEncryptedFile(String filename);
    public String readFile(String filename);
    
    public void shutdown();
    
    public void updatePeers(String ident, String ip, int port, List<String> docs, boolean needsHumanAuth);
}
