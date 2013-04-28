/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

import java.math.BigInteger;

/**
 *SRP Authentication Message 2
 * -Server: Sends server's ephemeral public key(B), and preshared salt(s) and nonce(u).
 * @author Patrick
 */
class Auth_Msg2 extends SRPAuthenticationMessage {
    byte[] salt;
    BigInteger B;
    BigInteger u;  //Random value, similar to nonce

    Auth_Msg2(byte[] salt, BigInteger B, BigInteger u, String docID) {
        super(docID);
        this.salt = salt;
        this.B = B;
        this.u = u;
    }
}
