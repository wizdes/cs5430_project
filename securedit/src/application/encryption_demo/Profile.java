/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import java.util.ArrayList;

/**
 *
 * @author Patrick
 */
public class Profile {
    public String username;
    public String host;
    public int port;
    public transient ArrayList<String> documentsOpenForDiscovery = new ArrayList<>();
    
    public Profile(String username, String host, int port) {
        this.username = username;
        this.host = host;
        this.port = port;
    }
}