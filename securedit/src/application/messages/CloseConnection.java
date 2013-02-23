/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.messages;

import transport_layer.network.Node;

/**
 *
 */
public class CloseConnection extends Message {
    public CloseConnection(Node from, Node to, String messageId) {
        super(to, from, messageId);
    }
}
