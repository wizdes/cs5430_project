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
public class HA_Msg3 implements HumanAuthenticationMessage{
    PublicKey publicKey;
    PublicKey verifyingKey;
    int nonce;
    HA_Msg3(PublicKey publicKey, PublicKey verifyingKey, int nonceResponse1) {
        this.publicKey = publicKey;
        this.verifyingKey = verifyingKey;
        this.nonce = nonceResponse1;
    }
    
}
