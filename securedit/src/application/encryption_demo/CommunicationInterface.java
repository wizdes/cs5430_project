/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;

import application.encryption_demo.Messages.Message;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface CommunicationInterface {
    public void broadcastDiscovery();
    public boolean sendMessage(String destination, String docID, Message m);
    public boolean sendManualDiscoverMessage(String destination, String ip, int port, Message m);
    
//    public boolean broadcastMessage(Message m);
//    public boolean authenticateMachine(String machineIdent);
//    public boolean authenticateHuman(String machineIdent);
    public boolean authenticate(String machineIdent, String docID, String password);
    public boolean initializeSRPAuthentication(String serverID, String docID, String password, String PIN);    
    public Collection<Message> waitForMessages();
    
    public void depositMessage(Message m) throws NoSuchAlgorithmException;
    
    public boolean writeEncryptedFile(String filename, String password, String contents);
    public String readEncryptedFile(String filename, String password);
    public String readFile(String filename);
    
    public void shutdown();
    
    public void updatePeers(String ident, String ip, int port, List<String> docs, boolean needsHumanAuth);
//    public void updateHumanAuthStatus(String ident, boolean hasHumanAuthenticated);
//    
//    public boolean updatePin(String ID, String PIN);
//    public String getPIN(String ID);
//    public void displayPIN(String ID, String PIN);
    public String generatePIN(String userID, String docID);
}
