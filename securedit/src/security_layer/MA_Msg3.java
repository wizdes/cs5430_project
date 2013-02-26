/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;


/**
 * A -> B: {r2 + 1}K_B
 * @author Patrick C. Berens
 */
class MA_Msg3 implements AuthenticationMessage{
    int nonce2Response;     //r2 + 1

    MA_Msg3(int nonce2Response) {
        this.nonce2Response = nonce2Response;
    }
}
