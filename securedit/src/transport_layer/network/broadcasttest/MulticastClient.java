/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.network.broadcasttest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a Multicast "Client" which processes Discovery packets.
 *    In our application, Mutlicast "clients" are owners of documents being shared.
 *         So Multicast "servers" are users looking to discover available documents.
 * @author Patrick C. Berens
 */
public class MulticastClient  {
    private static final String groupAddress = "230.0.0.1";    //All "clients"(owners of documents) listen on this.
    private static final int discoveryPort = 5446;      //All "clients" use this port.
    private static final String ENCODING = "UTF-8";
    
    public static void main(String[] args) {
        MulticastSocket socket = null;
        InetAddress address = null;
        try {
            /* Join group */
            socket = new MulticastSocket(discoveryPort);
            address = InetAddress.getByName(groupAddress);
            socket.joinGroup(address);
            
            System.out.println("Document Owner(MulticastClient) is waiting for discovery packets...");
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
                    System.out.println("Discovery Packet Contents: " + discoveryPacket.toString());   
                    //Process packet. If not currently doing human authentication or if not currently connected with,
                    //  then alert OnwerGUI of potential human to authenticate with.
                }
            } while (true);
                
        } catch (IOException ex) {
            Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /* Leave group and close socket */
        try {
            socket.leaveGroup(address);
        } catch (IOException ex) {
            Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        socket.close();
    }
}
