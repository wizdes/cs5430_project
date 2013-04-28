/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.crypto.SecretKey;

/**
 * SessionKey states, both AES encryption and HMAC keys.
 * -Can set from anywhere, but can only get from security layer.
 * @author Patrick
 */
public class SessionKeys {
    private static final String DELIMITER = ":::";
    private ConcurrentMap<String, SecretKey> sessionKeys = new ConcurrentHashMap<>();
    private ConcurrentMap<String, SecretKey> hmacKeys = new ConcurrentHashMap<>();
    
    public void addSessionKey(String ident, String docID, SecretKey sessionKey){
        sessionKeys.put(ident + DELIMITER + docID, sessionKey);
    }
    public void addHmacKey(String ident, String docID, SecretKey hmacKey){
        hmacKeys.put(ident + DELIMITER + docID, hmacKey);
    }
    
    /* ONLY SECURETRANSPORT AS ACCESS TO KEYS */
    SecretKey getSessionKey(String ident, String docID){
        return sessionKeys.get(ident + DELIMITER + docID);
    }
    
    SecretKey getHmacKey(String ident, String docID){
        return hmacKeys.get(ident + DELIMITER + docID);
    }
}
