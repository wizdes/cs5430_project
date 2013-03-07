/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.encryption_demo.Message;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.crypto.SecretKey;

/**
 *
 * @author Patrick C. Berens
 */
class KeysObject implements Message {
    ConcurrentMap<String, PublicKey> publicKeys = new ConcurrentHashMap<>();  //<Ident, publicKey>
    ConcurrentMap<String, PublicKey> verifiyngKeys = new ConcurrentHashMap<>();  //<Ident, publicKey>
    PrivateKey privateKey;
    String ident;
    PrivateKey signingKey;
    
    KeysObject(){}
}
