/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.messages;

import transport_layer.network.Node;

/**
 *
 */
public class DemoMessage extends Message {
    private String contents;
    
    public DemoMessage(Node to, String messageId, String contents) {
        super(to, null);
        this.contents = contents;
    }
    
    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }    
}
