/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.machine_authentication;

import application.messages.Message;
import transport_layer.network.Node;

/**
 * A -> B: {A, r1}K_B
 * @author Patrick C. Berens
 */
public class Msg01_AuthenticationRequest extends Message implements AuthenticationMessage {
    String fromIdentifier;  //A
    int nonce1;             //r1
    public Msg01_AuthenticationRequest(Node to, int nonce1) {
        super(to, null);
        this.fromIdentifier = to.toString();
        this.nonce1 = nonce1;
    }
    
    public int getNonce() {
        return nonce1;
    }
}
