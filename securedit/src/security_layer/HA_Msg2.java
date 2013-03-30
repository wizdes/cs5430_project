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
class HA_Msg2 implements HumanAuthenticationMessage{
    PublicKey publicKey;
    PublicKey verifyingKey;
    long ownersAsymmetricKeyVersion;
    int nonce;
    HA_Msg2(PublicKey publicKey, PublicKey verifyingKey, long asymmetricKeyVersion, int nonceResponse1) {
        this.publicKey = publicKey;
        this.verifyingKey = verifyingKey;
        this.ownersAsymmetricKeyVersion = asymmetricKeyVersion;      
        this.nonce = nonceResponse1;
    }
}
