package security_layer;

import java.util.ArrayList;


/**
 * Profile contains program wide variables. 
 * -None of this information is ever saved persistently.
 * @author Patrick
 */
public class Profile {
    public String username;
    public String host;
    public int port;
    //Documents which are being shared.
    public ArrayList<String> documentsOpenForDiscovery = new ArrayList<>();
    //Session keys of servers which have already authenticated since program start.
    public SessionKeys keys;
    public Profile(String username, String host, int port) {
        this.username = username;
        this.host = host;
        this.port = port;
    }
    public void setSessionKeys(SessionKeys keys){
        this.keys = keys;
    }
}