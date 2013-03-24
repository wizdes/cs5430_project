package transport_layer.discovery;

/**
 *
 * @author Patrick C. Berens
 */
class DiscoveryPacket {
    String myID;
    String myIP;
    Integer myPort;
    private static final String DELIMINATOR = ",";
    DiscoveryPacket(String myID, String myIP, Integer myPort) {
        this.myID = myID;
        this.myIP = myIP;
        this.myPort = myPort;
    }
    @Override
    public String toString(){
        return myID + DELIMINATOR + myIP + DELIMINATOR + myPort;
    }
    static DiscoveryPacket fromString(String data){
        String[] fields = data.split(DELIMINATOR);
        if(fields.length != 3){
            return null;
        } else{
           return new DiscoveryPacket(fields[0], fields[1], Integer.parseInt(fields[2])); 
        }
    }
}
