/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;

import application.messages.Message;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import security_layer.SecureTransportInterface;
import transport_layer.network.Node;

/**
 *
 */
public interface CommunicationInterface {
    
    public java.io.Serializable sendAESEncryptedMessage(Message m);
    public java.io.Serializable sendRSAEncryptedMessage(Message m);
    public boolean authenticateMachine(Node dest);
    
    public Collection<Message> waitForMessages();
    
    public void depositMessage(Message m) throws NoSuchAlgorithmException;
    
    public java.io.Serializable writeEncryptedFile(String filename, java.io.Serializable contents);
    public java.io.Serializable readEncryptedFile(String filename);
    public java.io.Serializable readUnencryptedFile(String filename);
    
    public void shutdown();
    
    // for testing only
    public SecureTransportInterface getSecureTransport();
    public void setSecureTransport(SecureTransportInterface sti);
}
