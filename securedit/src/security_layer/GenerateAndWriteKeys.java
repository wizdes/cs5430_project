/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.messages.DemoMessage;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.crypto.Cipher;
import transport_layer.network.Node;
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
        
        //Test personal key
        DemoMessage dm = new DemoMessage(new Node(null, null, 0), "", "Hello Matt, I hope this works.");
        //Cipher cipher = CipherFactory.constructAESEncryptionCipher(personal, iv) 
        
        //Write keys files
        KeysObject keysObject = new KeysObject();
        keysObject.setPublicKeys(publicKeys);
        for(int i = 0; i < 3; i++){
            transport = new SecureTransport("pass" + i + i + i + i + "pass" + i + i + i + i);
            keysObject.setPrivateKey(i + "", privateKeys[i]);
            transport.writeEncryptedFile("keys_" + i, dm);
            System.out.println("pre-encryption dm: " + dm.getContents());
            DemoMessage msg = (DemoMessage)transport.readEncryptedFile("keys_" + i);
            System.out.println("decrypted dm: " + dm.getContents());
        }
        
        //Test file decryption
        
        
//        KeysObject readObj = (KeysObject)transport.readEncryptedFile(filename);
//        if(readObj.getPrivateKey().equals(keysObject.getPrivateKey())){
//            System.out.println("equals;");
//        } else{
//            System.out.println("not equals;");
//        }
    }
}
