package security_layer.authentications;

/**
 * AuthenticationError message saying error was during creation.
 */
public class AccountCreationError extends AuthenticationError {
        
    public AccountCreationError(String documentId, String msg) {
        super(documentId, msg);
    }
    
}
