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
public class EncryptionKeys {
    //Have access to keys directly only inside security_layer package.
    private String ident;
    Key personalKey;    //Generated from password for AES files
    
    ConcurrentMap<String, SecretKey> secretKeys = new ConcurrentHashMap<>();      //Generated randomly for AES communication
    ConcurrentMap<String, PublicKey> publicKeys = new ConcurrentHashMap<>();      
    
    Key publicKey;      //RSA
    Key privateKey;     //RSA
    
    EncryptionKeys(){}
    EncryptionKeys(Key personalKey){
        this.personalKey = personalKey;
    }
    EncryptionKeys(String ident, Key personalKey){
        this.ident = ident;
        this.personalKey = personalKey;
    }
    EncryptionKeys(Key publicKey, Key privateKey){
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
    EncryptionKeys(String ident, Key personalKey, PublicKey publicKey, PrivateKey privateKey){
        this.ident = ident;
        this.personalKey = personalKey;
        addPublicKey(ident, publicKey);
        this.privateKey = privateKey;
    }
    
    public void addSymmetricKey(String ident, SecretKey secretKey) {
        secretKeys.putIfAbsent(ident, secretKey);
    }
    
    public SecretKey getSymmetricKey(String ident) {
        return secretKeys.get(ident);
    }    
    
    public void addPublicKey(String ident, PublicKey publicKey) {
        publicKeys.putIfAbsent(ident, publicKey);
    }
    
    public PublicKey getPublicKey(String ident) {
        return publicKeys.get(ident);
    }
        
    /**
     * @return the ident
     */
    public String getIdent() {
        return ident;
    }

    /**
     * @param ident the ident to set
     */
    public void setIdent(String ident) {
        this.ident = ident;
    }
}
