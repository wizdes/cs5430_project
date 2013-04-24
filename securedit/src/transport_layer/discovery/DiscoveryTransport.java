/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.discovery;

import configuration.Constants;
import java.util.logging.Level;
import java.util.logging.Logger;
import application.encryption_demo.Profile;
import transport_layer.network.NetworkTransportInterface;


/**
 *
 * @author Patrick
 */
public class DiscoveryTransport {

    public DiscoveryTransport(NetworkTransportInterface networkTransport) {
        new MulticastClient(networkTransport).start();
    }

    public void broadcastDiscovery(){
        if(Constants.DEBUG_ON){
            Logger.getLogger(DiscoveryTransport.class.getName()).log(Level.INFO, "[User: " + Profile.username + "] Broadcasting Discovery Message");
        }
        new MulticastServer().broadcast(Profile.username, Profile.host, Profile.port);
    }
    
}
