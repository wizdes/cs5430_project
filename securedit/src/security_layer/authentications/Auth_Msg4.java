/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

/**
 *
 * @author Patrick
 */
class Auth_Msg4 extends SRPAuthenticationMessage {
    byte[] M2;   //Server's Message Authentication Code

    Auth_Msg4(byte[] M2, String docID) {
        super(docID);
        this.M2 = M2;
    }
}
