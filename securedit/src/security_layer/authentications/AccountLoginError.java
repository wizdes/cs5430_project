package security_layer.authentications;

/**
 * AuthenticationError message saying error was during login.
 */
public class AccountLoginError extends AuthenticationError {
        
    public AccountLoginError(String documentId, String msg) {
        super(documentId, msg);
    }
    
}
