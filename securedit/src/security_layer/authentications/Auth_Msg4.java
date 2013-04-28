package security_layer.authentications;

/**
 *SRP Authentication Message 3
 * -Server: Sends server MAC for K(M2)
 * @author Patrick
 */
class Auth_Msg4 extends SRPAuthenticationMessage {
    byte[] M2;   //Server's Message Authentication Code

    Auth_Msg4(byte[] M2, String docID) {
        super(docID);
        this.M2 = M2;
    }
}
