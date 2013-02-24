/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.machine_authentication;

/**
 * A -> B: {A, r2 + 1}K_B
 * @author Patrick C. Berens
 */
public class Msg03_AuthenticationAgreement implements java.io.Serializable {
    String fromIdent;       //A
    int nonce2Response;     //r2 + 1

    Msg03_AuthenticationAgreement(String fromIdent, int nonce2Response) {
        this.fromIdent = fromIdent;
        this.nonce2Response = nonce2Response;
    }
}
