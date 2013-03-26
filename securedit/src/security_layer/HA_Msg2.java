/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.PublicKey;
import javax.crypto.SecretKey;

/**
 *
 * @author Yi
 */
public class HA_Msg2 implements HumanAuthenticationMessage{
    PublicKey publicKey;
    PublicKey verifyingKey;
    int nonce;
    String ID;
    HA_Msg2(PublicKey publicKey, PublicKey verifyingKey, int nonceResponse1, String ID) {
        this.publicKey = publicKey;
        this.verifyingKey = verifyingKey;
        this.nonce = nonceResponse1;
        this.ID = ID;
    }
    
}
