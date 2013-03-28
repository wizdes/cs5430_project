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
    String msg;
    int nonce;             //r1
    HA_Msg1(String ID, int nonce){
        this.msg = "I AM." + ID;
        this.nonce = nonce;
    }
}
