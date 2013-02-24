/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;

import application.encryption_demo.CommunicationInterface;
import application.messages.Message;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import security_layer.SecureTransportInterface;


public class Server {
    
    private ClientListenerThread clientListener;
    private Node host;
    private SecureTransportInterface secureTransport;
    
    public Server(Node host, SecureTransportInterface secureTransport) {
        this.host = host;
        this.secureTransport = secureTransport;
    }
    
    public void listen() {
        System.out.println(this.host + " listening...");
        this.clientListener = new ClientListenerThread(this);
        this.clientListener.start();
    }

    public void depositMessage(Serializable m) throws NoSuchAlgorithmException {
        this.secureTransport.processEncryptedMessage(m);
    }    
    
    public void shutdown() {
        this.clientListener.stopListening();
    }
    
    public Node getHost() {
        return this.host;
    }
  
}
