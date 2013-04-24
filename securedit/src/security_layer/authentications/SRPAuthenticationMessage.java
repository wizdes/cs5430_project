/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

/**
 *
 * @author Patrick
 */
class SRPAuthenticationMessage implements AuthenticationMessage {
    String docID;

    SRPAuthenticationMessage(String docID) {
        this.docID = docID;
    }
    
}
