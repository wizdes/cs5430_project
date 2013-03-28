/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.network;

import configuration.Constants;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import security_layer.EncryptedMessage;
import security_layer.PlaintextMessage;
import security_layer.SecureTransportInterface;
import transport_layer.discovery.DiscoveryResponseMessage;

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
        if(Constants.DEBUG_ON){
            Logger.getLogger(NetworkTransport.class.getName()).log(Level.INFO, "[User: " + topology.getMyId() + "] Sending " + NetworkMessage.class.getName() + " to " + destination + ".");
        }
        if(destNode == null){
            if(Constants.DEBUG_ON){
                Logger.getLogger(NetworkTransport.class.getName()).log(Level.SEVERE, "[User: " + topology.getMyId() + "] Desintation: " + destination + " isn't in topology file.");
            }
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
        secureTransport.processPlaintextMessage(msg.source.id, (PlaintextMessage)msg.content);
    }

    void depositEncryptedMessage(NetworkMessage msg) {
        try {
            secureTransport.processEncryptedMessage(msg.source.id, (EncryptedMessage)msg.content);
        } catch (NoSuchAlgorithmException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(NetworkTransport.class.getName()).log(Level.SEVERE, "[User: " + topology.getMyId() + "] Couldn't find encryption algorithm when processing " + EncryptedMessage.class.getName() + ".", ex);
            }
        }
    }
    void depositDiscoveryMessage(NetworkMessage msg) {
        secureTransport.processDiscoveryResponse((DiscoveryResponseMessage)msg.content);
    }
}
