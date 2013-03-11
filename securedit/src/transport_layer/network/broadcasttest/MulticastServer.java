package transport_layer.network.broadcasttest;

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
public class MulticastServer extends Thread {
    private static final String groupAddress = "230.0.0.1";    //All "clients"(owners of documents) listen on this.
    private static final int discoveryPort = 5446;      //All "clients" use this port.
    private static final String ENCODING = "UTF-8";
    
    private DatagramSocket socket = null;
    private static final int numTimesBroadcast = 3;
    
    public static void main(String[] args) throws java.io.IOException {
        new MulticastServer().start();
    }
    MulticastServer(){
        try {
            socket = new DatagramSocket(4445);  //Must not be used by anyone else on server. So maybe port + offset(so for id=0, tcp=4000, udp=4000+500)
        } catch (SocketException ex) {
            Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void run(){
        try {
            /* Create packet and convert to byte buffer */
            DiscoveryPacket data = new DiscoveryPacket("0", "127.0.0.1", 4000);
            byte[] buf = data.toString().getBytes(ENCODING);    //Check max size...
            //May want to use Base64 instead/in addition to ENCODING....

            /* Try to send packet a few times in case dropped */
            for (int i = 0; i < numTimesBroadcast; i++) {
                System.out.println("User(MulticastServer): Sending discovery packet.");
                /* Send data to anyone listening to group 230.0.0.1 and sleep for 5 secs*/
                InetAddress group = InetAddress.getByName(groupAddress);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, discoveryPort);
                socket.send(packet);
                sleep(5000);
            }
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        socket.close();
    }
}
