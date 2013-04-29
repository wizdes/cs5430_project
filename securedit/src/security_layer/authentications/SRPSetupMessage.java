package security_layer.authentications;

/**
 * Message class used for SRP setup(exchanging s,v)
 * @author Patrick
 */
public class SRPSetupMessage implements AuthenticationMessage {
    String docID;

    SRPSetupMessage(String docID) {
        this.docID = docID;
    }   
}
