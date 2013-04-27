package application.encryption_demo;

import application.encryption_demo.Messages.Message;

/**
 * Client sends to owner asking to join document.
 * -Currently, owner automatically allows him(no authorization)
 * 
 * @author Patrick
 */
class RequestJoinDocMessage implements Message {
    String sourceID;
    String docName;

    RequestJoinDocMessage(String myID, String docName) {
        this.sourceID = myID;
        this.docName = docName;
    }
}
