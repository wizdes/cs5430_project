package security_layer.authentications;

/**
 * Server responds with this if SRPSetup was successful.
 */
public class InitAuth_MsgSuccess extends SRPSetupMessage {
    
    public InitAuth_MsgSuccess(String docID) {
        super(docID);
    }
    
}
