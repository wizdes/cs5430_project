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
import security_layer.SecureTransportInterface;

/**
 *
 * @author Patrick C. Berens
 */
public class NetworkTransport implements NetworkTransportInterface{
    static final String MESSAGE_RECIEVED_ACK = "OK";
    static final String CONNECTION_FINISHED = "MSG_FIN";
    private Server server;
    private Client client;
    //private HashMap<String, Node> neighbors = new HashMap<>();
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
    public boolean send(String destination, EncryptedMessage m) {
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

    void depositEncryptedMessage(NetworkMessage msg) {
        try {
            secureTransport.processEncryptedMessage(msg.source.id, msg.content);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(NetworkTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
