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
    int nonce;
    HA_Msg3(PublicKey publicKey, int nonceResponse1) {
        this.publicKey = publicKey;
        this.nonce = nonceResponse1;
    }
    
}
