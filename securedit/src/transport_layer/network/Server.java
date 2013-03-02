/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;

import java.security.NoSuchAlgorithmException;


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
        this.network.depositEncryptedMessage(m);
    }    
    
    void shutdown() {
        this.clientListener.stopListening();
    }
}
