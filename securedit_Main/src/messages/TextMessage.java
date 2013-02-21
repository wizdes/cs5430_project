/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import network.Node;

/**
 *
 * @author Yi
 */
public class TextMessage extends Message {
    protected String message;
    
    public TextMessage(Node t, String message) {
        super(t);
        this.message = message;
    }
    
    public String getContent() {
        return message;
    }
}
