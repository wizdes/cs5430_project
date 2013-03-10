/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.crypto.SecretKey;

/**
 * Security Note:
 *  This class allows symmetric keys to be added if they don't already exist, and 
 *   tells a querier if a key exists. However, it doesn't allow keys to be seen or
 *   changed outside of the security_layer package.
 *  This class should only be used within security_layer and security_layer.machine_authentication.
 * 
 * @author Patrick C. Berens
 */
class EncryptionKeys {
    //Have access to keys directly only inside security_layer package.
    String ident;
    String password;
    Key personalKey;    //Generated from password for AES files
    Key personalHMACKey;
    
    ConcurrentMap<String, SecretKey> secretKeys = new ConcurrentHashMap<>();      //Generated randomly for AES communication
    ConcurrentMap<String, SecretKey> HMACKeys = new ConcurrentHashMap<>();
    ConcurrentMap<String, PublicKey> publicKeys = new ConcurrentHashMap<>();      
    ConcurrentMap<String, PublicKey> verifyingKeys = new ConcurrentHashMap<>();
    
    Key privateKey;     //RSA
    Key signingKey;     //RSA
    
    EncryptionKeys(){}
    EncryptionKeys(Key personalKey, String password){
        this.personalKey = personalKey;
        this.password = password;
    }
    EncryptionKeys(Key personalKey, String ident, String password){
        this.ident = ident;
        this.password = password;
        this.personalKey = personalKey;
    }

    EncryptionKeys(Key personalKey, PublicKey publicKey, PrivateKey privateKey, String ident, String password){
        this.ident = ident;
        this.password = password;
        this.personalKey = personalKey;
        addPublicKey(ident, publicKey);
        this.privateKey = privateKey;
    }
    
    void addSymmetricKey(String ident, SecretKey secretKey) {
        secretKeys.putIfAbsent(ident, secretKey);
    }
    void addVerifyingKey(String ident, PublicKey publicKey) {
        verifyingKeys.putIfAbsent(ident, publicKey);
    }    
    void addHMACKey(String ident, SecretKey secretKey) {
        HMACKeys.putIfAbsent(ident, secretKey);
    }
    boolean hasSymmetricKey(String ident){
        return secretKeys.containsKey(ident);
    }
    boolean hasVerifyingKey(String ident){
        return verifyingKeys.containsKey(ident);
    }    
    boolean hasHMACKey(String ident){
        return HMACKeys.containsKey(ident);
    }    
    SecretKey getSymmetricKey(String ident) {
        return secretKeys.get(ident);
    }   
    PublicKey getVerifyingKey(String ident) {
        return verifyingKeys.get(ident);
    }     
    SecretKey getHMACKey(String ident) {
        return HMACKeys.get(ident);
    }      
    
    void addPublicKey(String ident, PublicKey publicKey) {
        publicKeys.putIfAbsent(ident, publicKey);
    }
    
    PublicKey getPublicKey(String ident) {
        return publicKeys.get(ident);
    }
}
