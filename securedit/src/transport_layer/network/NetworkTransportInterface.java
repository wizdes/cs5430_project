/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.network;

import application.messages.Message;
import java.io.Serializable;

/**
 *
 * @author Patrick C. Berens
 */
public interface NetworkTransportInterface {
    /***************************************
     * Matt's package
     * **************************************/
    public void send(Node to, Serializable msg);
    public void shutdown();
}
