/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Patrick C. Berens
 */
public class GenerateAndWriteKeys {
    private SecureTransportInterface transport;
    
    public static void main(String[] args) {
        GenerateAndWriteKeys generateKeys = new GenerateAndWriteKeys();
        generateKeys.run(args);
        
    }
    private void run(String[] args){
        Scanner scan = new Scanner(System.in);

        //Generate keys
        PrivateKey privateKeys[] = new PrivateKey[3];
        ConcurrentMap<String, PublicKey> publicKeys = new ConcurrentHashMap<>();
        for(int i = 0; i < 3; i++){
            String pw = "pass" + i + i + i + i + "pass" + i + i + i + i;
            transport = new SecureTransport(pw);
            KeyPair keys = KeyFactory.generateAsymmetricKeys();
            publicKeys.put("1", keys.getPublic());
            privateKeys[i] = keys.getPrivate();
        }
        
        //Write keys files
        KeysObject keysObject = new KeysObject();
        keysObject.setPublicKeys(publicKeys);
        for(int i = 0; i < 3; i++){
            keysObject.setPrivateKey(i + "", privateKeys[i]);
            transport.writeEncryptedFile("keys_" + i, keysObject);
        }
        
        
//        KeysObject readObj = (KeysObject)transport.readEncryptedFile(filename);
//        if(readObj.getPrivateKey().equals(keysObject.getPrivateKey())){
//            System.out.println("equals;");
//        } else{
//            System.out.println("not equals;");
//        }
    }
}
