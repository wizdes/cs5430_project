package application.encryption_demo.Messages;

/**
 * Client sends to owner asking him to update everyone's documents with new text.
 * -No authorization currently so owner just broadcasts the message to all clients.
 * -Currently, everyone just concatenates the message to the end of the document(chat).
 * 
 * @author Patrick
 */
public class RequestDocUpdateMessage implements Message{
    public String myID; //Tell owner who the update came from(not necessary at the moment since no authorization)
    public String docName;  //Document name - Tells owner which of his documents to update
    public String text; //Text to be concatendated.

    public RequestDocUpdateMessage(String myID, String docName, String text) {
        this.myID = myID;
        this.docName = docName;
        this.text = text;
    }
}
