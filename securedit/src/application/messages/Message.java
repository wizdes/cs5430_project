/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.messages;

import java.io.Serializable;
import transport_layer.network.Node;

/**
 *
 */
public class Message implements Serializable {
    private String from = null;
    private String to = null;
    
    public Message(Node to, String messageId) {
        this.to = to.toString();
    }
    
    public Message(Node to, Node from, String messageId) {
        this.to = to.toString();
        this.from = from.toString();
    }    

    public Node getFrom() {
        return Node.fromString(from);
    }

    public void setFrom(Node from) {
        this.from = from.toString();
    }

    public Node getTo() {
        return Node.fromString(to);
    }

    public void setTo(Node to) {
        this.to = to.toString();
    }
}
