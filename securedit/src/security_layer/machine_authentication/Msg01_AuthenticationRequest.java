/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.machine_authentication;

/**
 * A -> B: {A, r1}K_B
 * @author Patrick C. Berens
 */
public class Msg01_AuthenticationRequest implements java.io.Serializable {
    String fromIdentifier;  //A
    int nonce1;             //r1
    Msg01_AuthenticationRequest(String fromIdentifer, int nonce1) {
        this.fromIdentifier = fromIdentifer;
        this.nonce1 = nonce1;
    }
}
