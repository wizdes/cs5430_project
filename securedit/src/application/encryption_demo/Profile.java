/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

/**
 *
 * @author Patrick
 */
public class Profile {
    public static String username;
    public static String host;
    public static int port;

    public Profile(String username, String host, int port) {
        this.username = username;
        this.host = host;
        this.port = port;
    }
}