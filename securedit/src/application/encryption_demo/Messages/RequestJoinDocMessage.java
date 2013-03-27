package application.encryption_demo.Messages;

/**
 * Client sends to owner asking to join document.
 * -Currently, owner automatically allows him(no authorization)
 * 
 * @author Patrick
 */
public class RequestJoinDocMessage implements Message {
    public String sourceID;
    public String docName;

    public RequestJoinDocMessage(String myID, String docName) {
        this.sourceID = myID;
        this.docName = docName;
    }
}
