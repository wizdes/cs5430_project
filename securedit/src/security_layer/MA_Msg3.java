/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import javax.crypto.SecretKey;


/**
 * A -> B: {r2 + 1}K_B
 * @author Patrick C. Berens
 */
class MA_Msg3 implements AuthenticationMessage{
    SecretKey SK;     //K_AB
    int r;           //r1 + 1
    
    MA_Msg3(SecretKey symmetricKey, int nonce1Response) {
        this.SK = symmetricKey;
        this.r = nonce1Response;
    } 
}
