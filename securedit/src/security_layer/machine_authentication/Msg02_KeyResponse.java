/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.machine_authentication;

import application.messages.Message;
import javax.crypto.SecretKey;
import transport_layer.network.Node;

/**
 * B -> A: {K_AB, B, r1 + 1, r2}K_A
 * @author Patrick C. Berens
 */
public class Msg02_KeyResponse extends Message {
    SecretKey symmetricKey;     //K_AB
    String fromIdent;           //B
    int nonce1Response;         //r1 + 1
    int nonce2;                 //r2
    
    public Msg02_KeyResponse(Node to, SecretKey symmetricKey, int nonce1Response, int nonce2) {
        super(to, null);
        this.fromIdent = to.toString();
        this.symmetricKey = symmetricKey;
        this.nonce1Response = nonce1Response;
        this.nonce2 = nonce2;
    }
    
    public SecretKey getSymmetricKey() {
        return symmetricKey;
    }

    public String getFromIdent() {
        return fromIdent;
    }

    public int getNonce1Response() {
        return nonce1Response;
    }

    public int getNonce2() {
        return nonce2;
    }    
}
