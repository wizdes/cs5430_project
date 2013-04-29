package security_layer.authentications;

import java.math.BigInteger;

/**
 * SRP Authentication Message 1
 * -Client: Sends client identifier(C) and client ephemeral public key(A)
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
