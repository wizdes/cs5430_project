/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.network;

/**
 *
 * @author Patrick C. Berens
 */
public interface NetworkTransportInterface {
    /***************************************
     * Matt's package
     * **************************************/
    public void send(java.io.Serializable msg);
    public java.io.Serializable read();
}
