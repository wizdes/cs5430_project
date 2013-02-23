/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.Key;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptionKeys {
    //Have access to keys directly only inside security_layer package.
    Key personalKey;    //Generated from password for AES files
    Key secretKey;      //Generated randomly for AES communication
    Key publicKey;      //RSA
    Key privateKey;     //RSA
    
    EncryptionKeys(){}
    EncryptionKeys(Key secretKey){
        this.secretKey = secretKey;
    }
    EncryptionKeys(Key publicKey, Key privateKey){
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
    EncryptionKeys(Key personalKey, Key secretKey, Key publicKey, Key privateKey){
        this.personalKey = personalKey;
        this.secretKey = secretKey;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
