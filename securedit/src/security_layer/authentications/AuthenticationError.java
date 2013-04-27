/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer.authentications;

import application.encryption_demo.Messages.Message;

/**
 *
 */
public class AuthenticationError implements Message {
    
    private String docID;
    private String message;
    
    public AuthenticationError(String docID, String msg) {
        this.docID = docID;
        this.message = msg;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
