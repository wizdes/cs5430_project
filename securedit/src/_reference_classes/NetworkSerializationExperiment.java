/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package _reference_classes;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Patrick C. Berens
 */
public class NetworkSerializationExperiment {
    private static PublicKey publicKey;
    private static PrivateKey privateKey;
    private static SecretKey secret;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String password = "d2cb415e067c7b13";   //should be 16 bytes
        secret = generateKey(password);
//
//        //Server
//        ServerThread server = new ServerThread(5555, key);
//        server.start();
//
//        //Client
//
//        Client client = new Client(key);
        
        genNewKeys();
        
        //Server
        ServerThread server = new ServerThread(5555, privateKey);
        server.start();
        
        //Client
        
        Client client = new Client(publicKey);
        
        //Serialize and send message
//        String longMsg = "";
//        for(int i = 0; i < 10000; i++){
//            longMsg += "abc";
//        }
//        Message msgObj = new Message(longMsg, "me", "you");
        System.out.println("Sending message: " + secret.hashCode());
        client.sendMsgObj(5555, secret);
    }
    
    public static SecretKey generateKey(String password) {
        SecretKey key = new SecretKeySpec(password.getBytes(), "AES");
        return key;
    }
    
    public static boolean genNewKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            //SecureRandom random = SecureRandom.getInstance("SHA256");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(NetworkSerializationExperiment.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }
}
