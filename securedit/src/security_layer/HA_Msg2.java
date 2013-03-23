/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import javax.crypto.SecretKey;

/**
 *
 * @author Yi
 */
public class HA_Msg2 implements HumanAuthenticationMessage{
    SecretKey publicKey;
    int nonce;
    String ID;
    HA_Msg2(SecretKey publicKey, int nonceResponse1, String ID) {
        this.publicKey = publicKey;
        this.nonce = nonceResponse1;
        this.ID = ID;
    }
    
}
