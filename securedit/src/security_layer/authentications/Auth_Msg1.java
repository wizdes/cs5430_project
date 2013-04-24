/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

import java.math.BigInteger;
import java.security.PublicKey;

/**
 *
 * @author Patrick
 */
class Auth_Msg1 extends SRPAuthenticationMessage{
    String clientID;
    BigInteger A;

    Auth_Msg1(String clientID, BigInteger A, String docID) {
        super(docID);
        this.clientID = clientID;
        this.A = A;
    }
}
