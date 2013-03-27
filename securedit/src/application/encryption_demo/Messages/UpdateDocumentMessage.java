package application.encryption_demo.Messages;

/**
 * Sent from owner to clients telling them to add text to document.
 * 
 * @author Patrick
 */
public class UpdateDocumentMessage implements Message{
    public String ownerID;  //Owner id - Needed to key into correct document window.
    public String docName;      //Document name - Needed to key into correct document window.
    public String text;     //Currently, this is just new text to be concatenated to the end of the chat(not the whole doc).

    public UpdateDocumentMessage(String ownerID, String docName, String text) {
        this.ownerID = ownerID;
        this.docName = docName;
        this.text = text;
    }
}
