/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Machine_Auth;

import network.NetworkInterface;

/**
 *
 * @author yjli_000
 */
public interface Machine_Auth_Interface {
    void setup(String username, String password, String filename, NetworkInterface _network);
    boolean authenticate(String IP);
}
