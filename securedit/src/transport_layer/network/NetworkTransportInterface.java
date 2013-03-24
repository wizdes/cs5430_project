/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.network;

import java.io.Serializable;
import security_layer.EncryptedMessage;


/**
 *
 * @author Patrick C. Berens
 */
public interface NetworkTransportInterface {
    /***************************************
     * Matt's package
     * **************************************/
    public boolean send(String destination, Serializable msg);
    public void addPeer(String peerIdent, String host, int port);
    public void shutdown();
}
