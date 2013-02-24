/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.Key;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

        System.out.print("Your Identifier: ");
        String myIdent = scan.next().trim();
        System.out.print("Password for " + myIdent + ": ");
        String password = scan.next().trim();

        transport = new SecureTransport(password, null);
        
        KeyPair keys1 = KeyFactory.generateAsymmetricKeys();
        KeyPair keys2 = KeyFactory.generateAsymmetricKeys();
        KeyPair keys3 = KeyFactory.generateAsymmetricKeys();
        
        Map<String, Key> publicKeys = new HashMap<>();
        publicKeys.put("1", keys1.getPublic());
        publicKeys.put("2", keys2.getPublic());
        publicKeys.put("3", keys3.getPublic());
        
        String filename = "keys_";
        KeysObject keysObject = new KeysObject();
        keysObject.setPublicKeys(publicKeys);
        switch (myIdent) {
            case "1":
                {            
                    keysObject.setPrivateKey("1", keys1.getPrivate());
                    filename += "1";
                    break;
                }
            case "2":
                {
                    keysObject.setPrivateKey("2", keys1.getPrivate());
                    filename += "2";
                    break;
                }
            case "3":
                {
                    keysObject.setPrivateKey("3", keys1.getPrivate());
                    filename += "3";
                    break;
                }
            default:
                System.out.println("The identifer: " + myIdent + " isn't valid. Valid Identifiers: 1, 2, 3");
                System.exit(-1);
                break;
        }
        transport.writeEncryptedFile(filename, keysObject);
        KeysObject readObj = (KeysObject)transport.readEncryptedFile(filename);
        if(readObj.getPrivateKey().equals(keysObject.getPrivateKey())){
            System.out.println("equals;");
        } else{
            System.out.println("not equals;");
        }
    }
}
