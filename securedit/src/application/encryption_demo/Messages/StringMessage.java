/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo.Messages;


/**
 *
 */
public class StringMessage implements Message {
    public String contents;
    
    public StringMessage(String contents) {
        this.contents = contents;
    }
}
