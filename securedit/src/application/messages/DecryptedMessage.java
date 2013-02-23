/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.messages;

import java.io.Serializable;
import transport_layer.network.Node;

/**
 *
 */
public class DecryptedMessage extends Message {
    
    private Serializable decryptedObject;
    
    public DecryptedMessage(Node to, String messageId) {
        super(to, messageId);
    }
    
    public Serializable getDecryptedObject() {
        return decryptedObject;
    }

    public void setDecryptedObject(Serializable decryptedObject) {
        this.decryptedObject = decryptedObject;
    }
}
