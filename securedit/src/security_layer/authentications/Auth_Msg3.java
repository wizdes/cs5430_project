package security_layer.authentications;

/**
 *SRP Authentication Message 3
 * -Client: Sends client MAC for K(M1)
 * @author Patrick
 */
class Auth_Msg3 extends SRPAuthenticationMessage {
    byte[] M1;   //Client's Message Authentication Code

    Auth_Msg3(byte[] M1, String docID) {
        super(docID);
        this.M1 = M1;
    }
}
