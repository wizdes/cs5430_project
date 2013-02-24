/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.machine_authentication;

import transport_layer.network.Node;

/**
 *
 * @author goggin
 */
public interface MachineAuthInterface {
    
    public boolean requestAuthenticationWith(Node n);
    public boolean processAuthenticationRequest(Msg01_AuthenticationRequest m);
    
}
