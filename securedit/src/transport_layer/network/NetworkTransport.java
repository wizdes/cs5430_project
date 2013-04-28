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
import security_layer.authentications.AuthenticationError;
import security_layer.authentications.SRPAuthenticationTransport;
import security_layer.authentications.AuthenticationMessage;
import security_layer.authentications.InitAuth_MsgSuccess;
import transport_layer.discovery.DiscoveryMessage;
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
    private SRPAuthenticationTransport authenticationTransport;
    
    public NetworkTransport(String ident, String host, int port) {
        this.topology = new Topology(ident, host, port);
        client = new Client();
        server = new Server(port, this);
        server.listen();
    }
    
    @Override
    public void setSecureTransport(SecureTransportInterface secureTransport){
        this.secureTransport = secureTransport;
    }
    @Override
    public void setAuthenticationTransport(SRPAuthenticationTransport authenticationTransport){
        this.authenticationTransport = authenticationTransport;
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
    public boolean send(String destination, String docID, Serializable msg){
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
        NetworkMessage netMsg = new NetworkMessage(topology.getMyNode(), docID, destNode, msg);
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
        secureTransport.processEncryptedMessage(msg.source.id, msg.docID, (EncryptedMessage)msg.content);
    }
    void depositDiscoveryMessage(NetworkMessage msg) {
        if(msg.content instanceof DiscoveryMessage){
            secureTransport.processDiscoveryMessage((DiscoveryMessage)msg.content);
        } else if(msg.content instanceof DiscoveryResponseMessage){
            secureTransport.processDiscoveryResponse((DiscoveryResponseMessage)msg.content);
        }
    }
    
    void depositAuthenticationMessage(NetworkMessage msg){
        authenticationTransport.processAuthenticationMessage(msg.source.id, msg.docID, (AuthenticationMessage)msg.content);
    }
    
    void depositAuthenticationError(NetworkMessage msg){
        PlaintextMessage pm = (PlaintextMessage)msg.content;
        if (pm.message instanceof AuthenticationError) {
            AuthenticationError error = (AuthenticationError)pm.message;
            authenticationTransport.processAuthenticationError(msg.source.id, msg.docID, error);
        } else if (pm.message instanceof InitAuth_MsgSuccess) {
            InitAuth_MsgSuccess ack = (InitAuth_MsgSuccess)pm.message;
            authenticationTransport.processAuthenticationMessage(msg.source.id, msg.docID, ack);
        }
    }    
}
