/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.messages;

import java.io.Serializable;
import security_layer.EncryptedObject;
import transport_layer.network.Node;

/**
 *
 */
public class EncryptedMessage extends Message {
     
    private EncryptedObject encryptedObject;
    private Serializable decryptedObject;
    
    public EncryptedMessage(Node to, String messageId) {
        super(to, messageId);
    }
    
    public EncryptedObject getEncryptedObject() {
        return encryptedObject;
    }

    public void setEncryptedObject(EncryptedObject encryptedObject) {
        this.encryptedObject = encryptedObject;
    }   
    
    public Serializable getDecryptedObject() {
        return decryptedObject;
    }

    public void setDecryptedObject(Serializable decryptedObject) {
        this.decryptedObject = decryptedObject;
    }
}
