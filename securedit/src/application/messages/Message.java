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
    private String messageId = null;
    private String replyTo = null;
    private Node from = null;
    private Node to = null;
    
    public Message(Node to, String messageId) {
        this.to = to;
        this.messageId = messageId;
    }
    
    public Message(Node to, Node from, String messageId) {
        this.to = to;
        this.from = from;
        this.messageId = messageId;
    }    
    
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }
}
