/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.encryption_demo.CommunicationTest;
import application.messages.EncryptedMessage;
import java.io.IOException;
import java.io.Serializable;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import transport_layer.network.Node;

/**
 *
 * @author goggin
 */
public class SecureTransportTest {
        
    @Test
    public void testSendAESEncryptedMessage() throws NoSuchAlgorithmException {
        SecureTransport st = new SecureTransport("0123456789123456", null);
        String contents = "hello world";
        byte[] iv = CipherFactory.generateRandomIV();
        EncryptedMessage m = new EncryptedMessage(new Node("me", "localhost", 4000), "mid");
        Key key = st.getKeys().secretKey;
        Cipher cipher = CipherFactory.constructAESEncryptionCipher(key, iv);        
        SealedObject encryptedObject = null;
        
        try {
            encryptedObject = new SealedObject(contents, cipher);
        } catch (IOException ex) {
            Logger.getLogger(CommunicationTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(CommunicationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        EncryptedObject encryptedMessage = new EncryptedObject(encryptedObject, iv);
        m.setEncryptedObject(encryptedMessage);
        
        EncryptedMessage em = st.processEncryptedMessage(m);
        assertEquals(contents, (String)em.getDecryptedObject());
    }

    
}
