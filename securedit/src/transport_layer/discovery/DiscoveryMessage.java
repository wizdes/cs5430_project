package transport_layer.discovery;

import application.encryption_demo.Message;

/**
 * Broadcast manually by application instances that wish to discover peers manually (as opposed
 * to the standard UDP broadcast).  It acts like a DiscoveryPacket.
 */
public class DiscoveryMessage implements Message {
    public String sourceID;
    public String sourceIP;
    public Integer sourcePort;

    public DiscoveryMessage(String myID, String myIP, Integer myPort) {
        this.sourceID = myID;
        this.sourceIP = myIP;
        this.sourcePort = myPort;
    }
    
    @Override
    public String toString(){
        return sourceID + ", " + sourceIP + ", " + sourcePort;
    }
}
