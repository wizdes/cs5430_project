/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.network;

import java.io.Serializable;

/**
 *
 * @author Patrick C. Berens
 */
public class NetworkTransport implements NetworkTransportInterface{

    @Override
    public void send(Serializable msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Serializable read() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
