package transport_layer.discovery;

/**
 * MultiCast Servers use this message type query the network for potential document
 * collaborators
 */
class DiscoveryPacket {
    String sourceID;
    String myIP;
    Integer myPort;
    private static final String DELIMINATOR = ",";
    
    DiscoveryPacket(String myID, String myIP, Integer myPort) {
        this.sourceID = myID;
        this.myIP = myIP;
        this.myPort = myPort;
    }
    
    @Override
    public String toString(){
        return sourceID + DELIMINATOR + myIP + DELIMINATOR + myPort;
    }
    
    /**
     * Turns a string representation of a DiscoveryPacket into a DiscoveryPacket
     * @param data the string representation of the packet to deserialize
     * @return the deserialized packet
     */
    static DiscoveryPacket fromString(String data){
        String[] fields = data.split(DELIMINATOR);
        if(fields.length != 3){
            return null;
        } else{
           return new DiscoveryPacket(fields[0], fields[1], Integer.parseInt(fields[2])); 
        }
    }
}
