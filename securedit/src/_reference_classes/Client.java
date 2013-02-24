/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package _reference_classes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Patrick C. Berens
 */
public class Client {
    Key key;
    public Client(Key key){
        this.key = key;
    }
    public void sendMsgObj(int port, Serializable msg) {
        try{
                Socket sock = new Socket("localhost", port);
                ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
                
                //AESNetwork network = new AESNetwork();
                RSANetwork network = new RSANetwork();
                network.sendEncryptedMessage(out, msg, key);
        }
        catch (UnknownHostException ex) {        
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
