/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer.authentications;

/**
 *
 */
public class AccountLoginError extends AuthenticationError {
        
    public AccountLoginError(String documentId, String msg) {
        super(documentId, msg);
    }
    
}
