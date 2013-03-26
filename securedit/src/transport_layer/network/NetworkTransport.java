/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.network;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import security_layer.EncryptedMessage;
import security_layer.PlainTextMessage;
import security_layer.SecureTransportInterface;
import transport_layer.discovery.DiscoveryResponseMessage;
import transport_layer.discovery.DiscoveryTransport;

/**
 *
 * @author Patrick C. Berens
 */
public class NetworkTransport implements NetworkTransportInterface{
    static final String MESSAGE_RECIEVED_ACK = "OK";
    static final String CONNECTION_FINISHED = "MSG_FIN";
    private Server server;
    private Client client;
    
    private Topology topology;
    private SecureTransportInterface secureTransport;
    
    public NetworkTransport(String ident, String host, int port, SecureTransportInterface secureTransport) {
        this.topology = new Topology(new Node(ident, host, port));
        this.secureTransport = secureTransport;
        client = new Client();
        server = new Server(port, this);
        server.listen();
    }
    
    @Override
    public boolean send(String destination, Serializable m) {
        Node destNode = topology.getNode(destination);
        if(destNode == null){
            return false;
        }
        NetworkMessage netMsg = new NetworkMessage(topology.getMyNode(), destNode, m);
        client.send(destNode, netMsg);
        return true;
    }
        
    @Override
    public void shutdown() {
        this.server.shutdown();
        this.client.closeConnections();
    }

    @Override
    public void addPeer(String peerIdent, String host, int port) {
        topology.addNode(peerIdent, host, port);
    }
    
    void depositPlainTextMessage(NetworkMessage msg){
        secureTransport.processPlainTextMessage(msg.source.id, (PlainTextMessage)msg.content);
    }

    void depositEncryptedMessage(NetworkMessage msg) {
        try {
            secureTransport.processEncryptedMessage(msg.source.id, (EncryptedMessage)msg.content);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(NetworkTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void depositDiscoveryMessage(NetworkMessage msg) {
        secureTransport.processDiscoveryResponse((DiscoveryResponseMessage)msg.content);
    }
}
