/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

/**
 *
 * @author Patrick
 */
class Auth_Msg3 extends SRPAuthenticationMessage {
    byte[] M1;   //Client's Message Authentication Code

    Auth_Msg3(byte[] M1, String docID) {
        super(docID);
        this.M1 = M1;
    }
}
