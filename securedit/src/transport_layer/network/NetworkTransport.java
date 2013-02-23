/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.network;

import application.encryption_demo.CommunicationInterface;
import application.messages.Message;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Patrick C. Berens
 */
public class NetworkTransport implements NetworkTransportInterface{
    
    private Server server;
    private Client client;
    private HashMap<String, Node> neighbors = new HashMap<>();
    private Node host;
    
    public NetworkTransport(Node host, CommunicationInterface network) {
        client = new Client();
        server = new Server(host, network);
        server.listen();
        this.host = host;
    }
        
    @Override
    public Message read() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void send(Message m) {
        m.setFrom(host);
        client.send(m);
    }
        
    @Override
    public void shutdown() {
        this.server.shutdown();
        this.client.closeConnections();
    }
    
    public static void debugBytes(byte[] bytes, String label) {
        try {
            System.out.println(label + "[" + bytes.length + "]");
            System.out.println(new String(bytes, "UTF-8"));
            System.out.println("");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
}
