/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer.authentications;

import java.math.BigInteger;

/**
 *
 */
public class InitAuth_Msg extends SRPSetupMessage {
    
    BigInteger v;
    byte[] s;

    public InitAuth_Msg(BigInteger v, byte[] s) {
        this.v = v;
        this.s = s;
    }
    
}
