/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.PublicKey;

/**
 *
 * @author Yi
 */
class HA_Msg3 implements HumanAuthenticationMessage{
    PublicKey publicKey;
    PublicKey verifyingKey;
    long asymmetricKeyVersion;
    int nonce;
    HA_Msg3(PublicKey publicKey, PublicKey verifyingKey, long asymmetricKeyVersion, int nonceResponse1) {
        this.publicKey = publicKey;
        this.verifyingKey = verifyingKey;
        this.asymmetricKeyVersion = asymmetricKeyVersion;
        this.nonce = nonceResponse1;
    }
    
}
