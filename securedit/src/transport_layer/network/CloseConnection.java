/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;


/**
 *
 */
class CloseConnection extends NetworkMessage {
    CloseConnection(Node source, Node destination) {
        super(source, destination, null);
    }
}
