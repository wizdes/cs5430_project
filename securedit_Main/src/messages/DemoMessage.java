/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;

import network.Node;

/**
 *
 */
public class DemoMessage extends Message {
    private String message;
    
    public DemoMessage(Node t, String message) {
        super(t);
        this.message = message;
    }
    
    public String getContent() {
        return message;
    }

}
