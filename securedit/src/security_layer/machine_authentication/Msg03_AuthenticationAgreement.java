/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.machine_authentication;

import application.messages.Message;
import transport_layer.network.Node;

/**
 * A -> B: {A, r2 + 1}K_B
 * @author Patrick C. Berens
 */
public class Msg03_AuthenticationAgreement extends Message implements AuthenticationMessage{
    String fromIdent;       //A
    int nonce2Response;     //r2 + 1

    public Msg03_AuthenticationAgreement(Node to, int nonce2Response) {
        super(to, null);
        this.fromIdent = to.toString();
        this.nonce2Response = nonce2Response;
    }
    
    public int getNonce2Response() {
        return nonce2Response;
    }
}
