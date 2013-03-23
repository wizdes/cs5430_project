/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.crypto.SecretKey;

/**
 *
 * @author Patrick C. Berens
 */
class GenerateAndWriteKeys {
    private SecureTransportInterface transport;
    
    public static void main(String[] args) {
        GenerateAndWriteKeys generateKeys = new GenerateAndWriteKeys();
        generateKeys.run(args);
    }
    private void run(String[] args){

        //Generate keys
        PrivateKey privateKeys[] = new PrivateKey[3];
        PrivateKey signingKeys[] = new PrivateKey[3];
        ConcurrentMap<String, PublicKey> publicKeys = new ConcurrentHashMap<>();
        ConcurrentMap<String, PublicKey> verifyingKeys = new ConcurrentHashMap<>();
        
        for(int i = 0; i < 3; i++){
            String pw = "pass" + i + i + i + i + "pass" + i + i + i + i;
            transport = new SecureTransport(pw);
            KeyPair keys = KeyFactory.generateAsymmetricKeys();
            publicKeys.put(i + "", keys.getPublic());
            privateKeys[i] = keys.getPrivate();
            System.out.println("Generated public/private key: " + i);
            
            KeyPair signingKeyPair = KeyFactory.generateAsymmetricKeys();
            verifyingKeys.put(i + "", signingKeyPair.getPublic());
            signingKeys[i] = signingKeyPair.getPrivate();
            System.out.println("Generated verifying/signing key: " + i);            
        }
        
        //Write keys files
        KeysObject keysObject = new KeysObject();
        keysObject.publicKeys = publicKeys;        
        keysObject.verifiyngKeys = verifyingKeys;        
       
        for(int i = 0; i < 3; i++){
            transport = new SecureTransport("pass" + i + i + i + i + "pass" + i + i + i + i);
            keysObject.ident = i + "";
            keysObject.privateKey = privateKeys[i];
            keysObject.signingKey = signingKeys[i];
            transport.writeEncryptedFile("keys_" + i, keysObject);
        }
    }
}
