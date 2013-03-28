/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.discovery;

import configuration.Constants;
import java.util.logging.Level;
import java.util.logging.Logger;
import security_layer.Profile;
import transport_layer.network.NetworkTransportInterface;


/**
 *
 * @author Patrick
 */
public class DiscoveryTransport {
    private Profile profile;

    public DiscoveryTransport(Profile profile, NetworkTransportInterface networkTransport) {
        this.profile = profile;
        new MulticastClient(profile, networkTransport).start();
    }

    public void broadcastDiscovery(){
        if(Constants.DEBUG_ON){
            Logger.getLogger(DiscoveryTransport.class.getName()).log(Level.INFO, "[User: " + profile.ident + "] Broadcasting Discovery Message");
        }
        new MulticastServer().broadcast(profile.ident, profile.host, profile.port);
    }
    
}
