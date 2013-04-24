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
    public static String username;
    public static String host;
    public static int port;
    public static transient ArrayList<String> documentsOpenForDiscovery = new ArrayList<>();
    
    public Profile(String username, String host, int port) {
        Profile.username = username;
        Profile.host = host;
        Profile.port = port;
    }
}