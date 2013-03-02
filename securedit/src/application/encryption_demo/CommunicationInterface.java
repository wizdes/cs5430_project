/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import security_layer.SecureTransportInterface;

/**
 *
 */
public interface CommunicationInterface {
    
    public boolean sendMessage(String destination, Message m);
    public boolean authenticateMachine(String machineIdent);
    
    public Collection<Message> waitForMessages();
    
    public void depositMessage(Message m) throws NoSuchAlgorithmException;
    
    public boolean writeEncryptedFile(String filename, String contents);
    public String readEncryptedFile(String filename);
    public String readFile(String filename);
    
    public void shutdown();
    
    // for testing only
    public SecureTransportInterface getSecureTransport();
}
