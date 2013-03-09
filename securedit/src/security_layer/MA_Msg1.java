/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

/**
 * A -> B: {r1}K_B
 * @author Patrick C. Berens
 */
class MA_Msg1 implements MachineAuthenticationMessage {
    int nonce;             //r1
    MA_Msg1(int nonce1) {
        this.nonce = nonce1;
    }
}
