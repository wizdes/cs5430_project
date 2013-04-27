/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

import application.encryption_demo.Message;

/**
 *
 */
public class CommandMessage implements Message {
    
    public String to; // id of network peer to send to
    public String from;
    public String documentName;  
    public DocumentCommand command;

    public CommandMessage(String to, String from, String documentName, DocumentCommand command) {
        this.to = to;
        this.documentName = documentName;
        this.command = command;
        this.from = from;
    }
    
    @Override
    public String toString() {
        return "[CommandMessage " +  this.from + " ~> " + this.to + " ] " + documentName + " : " + this.command;
    }
    
}
