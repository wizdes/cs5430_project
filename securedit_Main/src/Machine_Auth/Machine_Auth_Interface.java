/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Machine_Auth;

import encryption.RSA_Crypto_Interface;
import network.NetworkInterface;

/**
 *
 * @author yjli_000
 */
public interface Machine_Auth_Interface {
    void setup(String username, String password, String filename, NetworkInterface _network, RSA_Crypto_Interface _RSA);
    boolean authenticate_as_client(String IP);
    boolean authenticate_as_server();
}
