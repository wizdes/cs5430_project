/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.discovery;

import configuration.Constants;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import security_layer.Profile;
import transport_layer.network.NetworkTransportInterface;

/**
 * This is a Multicast "Client" which processes Discovery packets.
 *    In our application, Mutlicast "clients" are owners of documents being shared.
 *         So Multicast "servers" are users looking to discover available documents.
 * @author Patrick C. Berens
 */
class MulticastClient extends Thread {
    private static final String groupAddress = "230.0.0.1";    //All "clients"(owners of documents) listen on this.
    private static final int discoveryPort = 5446;      //All "clients" use this port.
    private static final String ENCODING = "UTF-8";
    
    private Profile profile;
    private NetworkTransportInterface networkTransport;
    
    MulticastClient(Profile profile, NetworkTransportInterface networkTransport){
        this.profile = profile;
        this.networkTransport = networkTransport;
    }
    
    @Override
    public void run(){
        MulticastSocket socket = null;
        InetAddress address = null;
        try {
            /* Join group */
            socket = new MulticastSocket(discoveryPort);
            address = InetAddress.getByName(groupAddress);
            socket.joinGroup(address);
            
            if(Constants.DEBUG_ON){
                Logger.getLogger(MulticastClient.class.getName()).log(Level.INFO, "[User: " + profile.ident + "] Owner waiting for discovery packets.");
            }
         
            do{
                /* Receive discovery packet */
                DatagramPacket packet;
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength(), ENCODING);
                //Check checksum here...
                DiscoveryPacket discoveryPacket = DiscoveryPacket.fromString(received);
                if (discoveryPacket != null) {
                    //Process packet.
                    if(!discoveryPacket.sourceID.equals(profile.ident) && this.profile.documentsOpenForDiscovery.size() > 0){
                        List<String> documentNames = new ArrayList<>(this.profile.documentsOpenForDiscovery);   //must copy here, possibly due to transient flag
                        long myKeyVersion = profile.getAsymmetricKeyVersionNumber(this.profile.ident);
                        long clientsKeyVersion = profile.getAsymmetricKeyVersionNumber(discoveryPacket.sourceID);
                        DiscoveryResponseMessage responseMessage = new DiscoveryResponseMessage(profile.ident, profile.host, profile.port, documentNames, myKeyVersion, clientsKeyVersion);
                        networkTransport.addPeer(discoveryPacket.sourceID, discoveryPacket.myIP, discoveryPacket.myPort);
                        networkTransport.send(discoveryPacket.sourceID, responseMessage);
                    }
                }
            } while (true);
        } catch (IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, "[User: " + profile.ident + "]", ex);
            }
        }
        
        /* Leave group and close socket */
        try {
            if(socket != null){
                socket.leaveGroup(address);
                socket.close();
            }
        } catch (IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, "[User: " + profile.ident + "]", ex);
            }
        }
    } 
}
