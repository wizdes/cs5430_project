/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Patrick
 */
public class Profile implements Serializable{
    public String username;
    public String host;
    public int port;
    public transient ArrayList<String> documentsOpenForDiscovery = new ArrayList<>();
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