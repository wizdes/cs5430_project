/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;

import java.security.NoSuchAlgorithmException;
import security_layer.EncryptedMessage;
import security_layer.PlainTextMessage;
import transport_layer.discovery.DiscoveryResponseMessage;


public class Server {
    
    private ClientListenerThread clientListener;
    private int port;
    private NetworkTransport network;
    
    Server(int port, NetworkTransport network) {
        this.port = port;
        this.network = network;
    }
    
    void listen() {
        System.out.println(this.port + " listening...");
        this.clientListener = new ClientListenerThread(this, port);
        this.clientListener.start();
    }

    void processNetworkMessage(NetworkMessage m) throws NoSuchAlgorithmException {
        
        this.network.addPeer(m.source.id, m.source.host, m.source.port);
        
        if (m.content instanceof EncryptedMessage){
            this.network.depositEncryptedMessage(m);
        } else if(m.content instanceof DiscoveryResponseMessage){
            this.network.depositDiscoveryMessage(m);
        } else if(m.content instanceof PlainTextMessage){
            this.network.depositPlainTextMessage(m);
        }
    }    
    
    void shutdown() {
        this.clientListener.stopListening();
    }
}
