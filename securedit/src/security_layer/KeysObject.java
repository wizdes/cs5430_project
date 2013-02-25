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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.crypto.SecretKey;

/**
 *
 * @author Patrick C. Berens
 */
public class KeysObject implements java.io.Serializable {
    private ConcurrentMap<String, PublicKey> publicKeys = new ConcurrentHashMap<>();  //<Ident, publicKey>
    private PrivateKey privateKey;
    private String ident;
    private SecretKey secretKey;
    

    public void addPublicKey(String ident, PublicKey key){
        publicKeys.put(ident, key);
    }
    public PublicKey getPublicKey(String ident){
        return publicKeys.get(ident);
    }
    public void setPrivateKey(String ident, PrivateKey key){
        this.ident = ident;
        this.privateKey = key;
    }
    public void setSecretKey(SecretKey sk) {
        secretKey = sk;
    }
    public SecretKey getSecretKey() {
        return secretKey;
    }
    
    /**
     * @return the publicKeys
     */
    public ConcurrentMap<String, PublicKey> getPublicKeys() {
        return publicKeys;
    }

    /**
     * @param publicKeys the publicKeys to set
     */
    public void setPublicKeys(ConcurrentMap<String, PublicKey> publicKeys) {
        this.publicKeys = publicKeys;
    }

    /**
     * @return the privateKey
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
