/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package _reference_classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

/**
 *
 * @author Patrick C. Berens
 */
public class ServerThread extends Thread {
    private ServerSocket serverSocket = null;
    private Key key;
    public ServerThread(int port, Key key) {
        try {
            this.key = key;
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            Socket socket = serverSocket.accept();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            //AESNetwork network = new AESNetwork();
            RSANetwork network = new RSANetwork();
            while (true) {
                Object obj = network.receiveEncryptedMessage(in, key);
                if (obj instanceof Message) {
                    Message msg = (Message) obj;
                    System.out.println("Message received: " + msg.message);
                } else if(obj instanceof SecretKey){
                    SecretKey key = (SecretKey)obj;
                    System.out.println("Message received: " + key.hashCode());
                }else {
                    System.out.println("Object received not supported!");
                }
            }
            //serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
