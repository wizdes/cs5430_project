/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;

import network.Node;

/**
 *
 */
public class AuthRequest extends Message {
    
    public AuthRequest(Node t, String mid) {
        super(t, mid);
    }
}
