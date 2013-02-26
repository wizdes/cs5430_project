/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import javax.crypto.SecretKey;

/**
 * B -> A: {K_AB, r1 + 1, r2}K_A
 * @author Patrick C. Berens
 */
class MA_Msg2 implements AuthenticationMessage {
    SecretKey SK;     //K_AB
    int r1;           //r1 + 1
    int r2;           //r2
    
    MA_Msg2(SecretKey symmetricKey, int nonce1Response, int nonce2) {
        this.SK = symmetricKey;
        this.r1 = nonce1Response;
        this.r2 = nonce2;
    } 
}
