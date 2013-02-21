/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Machine_Auth;
import Keys.Keys_Func;
import network.NetworkInterface;

/**
 *
 * @author yjli_000
 */
public class Machine_Auth implements Machine_Auth_Interface{

    String IP;
    Keys_Func _key;
    NetworkInterface network;
    
    Machine_Auth()
    {
        _key = new Keys_Func();
    }
    
    void read_file(String username, String password, String filename){
        // reads the file
        // decryptes the file with the password
        // populates Keys class
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setup(String username, String password, String filename, NetworkInterface _network) {
        read_file(username, password, filename);
    }

    @Override
    public boolean authenticate(String IP) {
        // calls network send
        // sends over a session key encrypted and hashed
        // 
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
