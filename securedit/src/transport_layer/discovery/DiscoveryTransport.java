package transport_layer.discovery;

import configuration.Constants;
import java.util.logging.Level;
import java.util.logging.Logger;
import security_layer.Profile;
import transport_layer.network.NetworkTransportInterface;


/**
 * A simple wrapper for the work of instantiating Multicast Clients and Servers
 */
public class DiscoveryTransport {
    
    private Profile profile;
    
    public DiscoveryTransport(NetworkTransportInterface networkTransport, Profile profile) {
        this.profile = profile;
        new MulticastClient(networkTransport, profile).start();
    }

    public void broadcastDiscovery(){
        if(Constants.DEBUG_ON){
            Logger.getLogger(DiscoveryTransport.class.getName()).log(Level.INFO, "[User: " + profile.username + "] Broadcasting Discovery Message");
        }
        new MulticastServer().broadcast(profile.username, profile.host, profile.port);
    }
    
}
