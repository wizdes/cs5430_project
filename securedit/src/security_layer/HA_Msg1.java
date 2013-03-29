/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

/**
 *
 * @author Yi
 */
class HA_Msg1 implements HumanAuthenticationMessage{
    int nonce;             //r1
    HA_Msg1(int nonce){
        this.nonce = nonce;
    }
}
