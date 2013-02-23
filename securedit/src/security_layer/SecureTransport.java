/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.io.Serializable;



/**
 *
 * @author Patrick C. Berens
 */
public class SecureTransport implements SecureTransportInterface{
    /********************************************
     * patrick's 
     * *******************************************/
    @Override
    public void sendEncryptedMessage(java.io.Serializable msg){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public java.io.Serializable receiveEncryptedMessage(){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeEncryptedFile(String filename, Serializable contents) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Serializable readEncryptedFile(String filename) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
