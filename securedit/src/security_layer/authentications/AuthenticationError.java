package security_layer.authentications;

import application.encryption_demo.Message;

/**
 * Sent back to client if authentication failed.
 * -Without this, client doesn't know to "wake up"
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
