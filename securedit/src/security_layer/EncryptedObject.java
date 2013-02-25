/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import javax.crypto.SealedObject;
import transport_layer.network.Node;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptedObject implements java.io.Serializable {
    byte[] iv;
    SealedObject encryptedObject;
    private Node from; 
    
    public EncryptedObject(SealedObject encryptedObject, byte[] iv, Node from){
        assert iv.length == 16: iv;
        this.iv = iv;
        this.encryptedObject = encryptedObject;
        this.from = from;
    }
    
    public Node getFrom() {
        return this.from;
    }
}
