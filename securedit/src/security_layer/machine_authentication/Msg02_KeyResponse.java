/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.machine_authentication;

import javax.crypto.SecretKey;

/**
 * B -> A: {K_AB, B, r1 + 1, r2}K_A
 * @author Patrick C. Berens
 */
public class Msg02_KeyResponse implements java.io.Serializable {
    SecretKey symmetricKey;     //K_AB
    String fromIdent;           //B
    int nonce1Response;         //r1 + 1
    int nonce2;                 //r2
    Msg02_KeyResponse(SecretKey symmetricKey, String fromIdent, int nonce1Response, int nonce2){
        this.symmetricKey = symmetricKey;
        this.fromIdent = fromIdent;
        this.nonce1Response = nonce1Response;
        this.nonce2 = nonce2;
    }
}
