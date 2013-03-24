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
class NetworkMessage implements Serializable{
    Node destination;
    Node source;
    Serializable content;
    NetworkMessage(Node source, Node destination, Serializable content) {
        this.destination = destination;
        this.source = source;
        this.content = content;
    }
}
