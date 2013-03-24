/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.discovery;

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
        System.out.println("Broadcast discovery");
        new MulticastServer().broadcast(profile.ident, profile.host, profile.port);
    }
    
}
