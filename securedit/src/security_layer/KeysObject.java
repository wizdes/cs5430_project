/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Patrick C. Berens
 */
public class KeysObject implements java.io.Serializable {
    private Map<String, Key> publicKeys = new HashMap<>();  //<Ident, publicKey>
    private Key privateKey;
    private String ident;
    

    public void addPublicKey(String ident, PublicKey key){
        publicKeys.put(ident, key);
    }
    public Key getPublicKey(String ident){
        return publicKeys.get(ident);
    }
    public void setPrivateKey(String ident, PrivateKey key){
        this.ident = ident;
        this.privateKey = key;
    }
    
    /**
     * @return the publicKeys
     */
    public Map<String, Key> getPublicKeys() {
        return publicKeys;
    }

    /**
     * @param publicKeys the publicKeys to set
     */
    public void setPublicKeys(Map<String, Key> publicKeys) {
        this.publicKeys = publicKeys;
    }

    /**
     * @return the privateKey
     */
    public Key getPrivateKey() {
        return privateKey;
    }
}
