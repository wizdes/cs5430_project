/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

/**
 * A -> B: {r1}K_B
 * @author Patrick C. Berens
 */
class MA_Msg1 implements AuthenticationMessage {
    int nonce1;             //r1
    MA_Msg1(int nonce1) {
        this.nonce1 = nonce1;
    }
}
