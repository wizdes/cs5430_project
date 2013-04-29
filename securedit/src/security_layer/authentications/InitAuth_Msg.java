package security_layer.authentications;

import java.math.BigInteger;

/**
 * Used during SRP setup phase.
 * -Client: Sends salt(s) and v(verifier) before SRP authentication begins.
 */
public class InitAuth_Msg extends SRPSetupMessage {
    
    BigInteger v;
    byte[] s;

    public InitAuth_Msg(BigInteger v, byte[] s, String docID) {
        super(docID);
        this.v = v;
        this.s = s;
    }
}
