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
    private String ident;
    Key personalKey;    //Generated from password for AES files
    public Key secretKey;      //Generated randomly for AES communication
    Key publicKey;      //RSA
    Key privateKey;     //RSA
    
    EncryptionKeys(){}
    EncryptionKeys(Key personalKey){
        this.personalKey = personalKey;
    }
    EncryptionKeys(Key publicKey, Key privateKey){
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
    EncryptionKeys(String ident, Key personalKey, Key secretKey, Key publicKey, Key privateKey){
        this.ident = ident;
        this.personalKey = personalKey;
        this.secretKey = secretKey;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
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
