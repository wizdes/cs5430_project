package transport_layer.discovery;

import configuration.Constants;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Multicast "server" broadcasts a discovery packet to "clients".
 *    In our application, Mutlicast "clients" are owners of documents being shared.
 *         So Multicast "servers" are users looking to discover available documents.
 * @author Patrick C. Berens
 */
class MulticastServer extends Thread{
    
    private DatagramSocket socket = null;
    
    private DiscoveryPacket discoveryPacket;
    
    @Override
    public void run() {
        try {
            /* Convert packet to byte buffer */
            byte[] buf = discoveryPacket.toString().getBytes(Constants.ENCODING);    //Check max size...
            //May want to use Base64 instead/in addition to ENCODING....

            /* Try to send packet a few times in case dropped */
            for (int i = 0; i < Constants.numTimesBroadcast; i++) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(MulticastServer.class.getName()).log(Level.INFO, "[User: " + discoveryPacket.sourceID + "] Broadcasting " + DiscoveryPacket.class.getName() + ": " + discoveryPacket);
                }
                /* Send data to anyone listening to group 230.0.0.1 and sleep for 5 secs*/
                InetAddress group = InetAddress.getByName(Constants.groupAddress);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, Constants.discoveryPort);
                socket.send(packet);
                if(i+1 != Constants.numTimesBroadcast){
                    Thread.sleep(1000); //Sleep if not last broadcast
                }
            }
        } catch (InterruptedException | IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, "[User: " + discoveryPacket.sourceID + "] Failed when broadcasting " + DiscoveryPacket.class.getName() + ": " + discoveryPacket, ex);
            }
        }
        socket.close();
    }
    
    /**
     * Start broadcasting discovery packets and waiting for clients to respond
     * @param ident the username to broadcast for yourself
     * @param host  the host you are sending from
     * @param port  the port you are sending from
     */
    void broadcast(String ident, String host, int port){
        try {
            socket = new DatagramSocket(1000 + port);  //Must not be used by anyone else on server. So maybe port + offset(so for id=0, tcp=4000, udp=4000+500)
            discoveryPacket = new DiscoveryPacket(ident, host, port);
            start();
        } catch (SocketException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, "[User: " + ident + "] Failed when intializing DatagramSocket @port: " + (1000 + port) + " for broadcast to (" + ident + ", " + host + ", " + port + ")", ex);
            }
        }
    }   
}
