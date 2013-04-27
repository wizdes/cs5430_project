/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer.authentications;

/**
 *
 */
public class AccountCreationError extends AuthenticationError {
        
    public AccountCreationError(String documentId, String msg) {
        super(documentId, msg);
    }
    
}
