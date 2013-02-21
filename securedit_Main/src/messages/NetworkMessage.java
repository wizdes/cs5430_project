/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import network.Node;

/**
 *
 * @author Yi
 */
public class NetworkMessage extends TextMessage{
    private int nonce;
    
    public NetworkMessage(Node t, String message, int _nonce) {
        super(t, message);
        nonce = _nonce;
    }
    
    public Integer getNonce() {
        return nonce;
    }
}
