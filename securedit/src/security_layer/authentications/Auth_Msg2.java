/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

import java.math.BigInteger;

/**
 *
 * @author Patrick
 */
class Auth_Msg2 extends SRPAuthenticationMessage {
    String salt;
    BigInteger B;
    BigInteger u;  //Random value, similar to nonce

    Auth_Msg2(String salt, BigInteger B, BigInteger u, String docID) {
        super(docID);
        this.salt = salt;
        this.B = B;
        this.u = u;
    }
}
