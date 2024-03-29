/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.network;

import java.io.Serializable;
import security_layer.SecureTransportInterface;
import security_layer.authentications.SRPAuthenticationTransport;


/**
 * A simple wrapper for lower level network utilities;
 * see NetworkTransport for implementation and specific comments
 */
public interface NetworkTransportInterface {
    public boolean send(String destination, Serializable msg);
    public boolean send(String destination, String docID, Serializable msg);
    public void addPeer(String peerIdent, String host, int port);
    public void shutdown();
    public void setSecureTransport(SecureTransportInterface secureTransport);
    public void setAuthenticationTransport(SRPAuthenticationTransport authenticationTransport);
}
