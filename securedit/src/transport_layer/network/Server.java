/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;

import transport_layer.discovery.DiscoveryMessage;
import configuration.Constants;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import security_layer.EncryptedMessage;
import security_layer.PlaintextMessage;
import security_layer.authentications.AuthenticationMessage;
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
        if(Constants.DEBUG_ON){
            Logger.getLogger(NetworkTransport.class.getName()).log(Level.INFO, "Server at " + this.port + " now listening...");
        }
        this.clientListener = new ClientListenerThread(this, port);
        this.clientListener.start();
    }

    void processNetworkMessage(NetworkMessage m){
        this.network.addPeer(m.source.id, m.source.host, m.source.port);
            
        if (m.content instanceof EncryptedMessage){
            this.network.depositEncryptedMessage(m);
        } else if(m.content instanceof DiscoveryResponseMessage){
            this.network.depositDiscoveryMessage(m);
        } else if(m.content instanceof DiscoveryMessage){
            this.network.depositDiscoveryMessage(m);
        } else if(m.content instanceof AuthenticationMessage){
            this.network.depositAuthenticationMessage(m);
        }
    }    
    
    void shutdown() {
        this.clientListener.stopListening();
    }
}
