/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends Thread {
    private Socket socket = null;
    private Server server = null;
    private PrintWriter out = null;
    private ObjectInputStream in = null;
        
    ServerThread(Socket socket, Server server) {
        super("ServerThread");
        this.socket = socket;
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new ObjectInputStream(socket.getInputStream());
            
            NetworkMessage m = (NetworkMessage)in.readObject();
            out.println(NetworkTransport.MESSAGE_RECIEVED_ACK);
            while (! (m instanceof CloseConnection)) {
                
                try {
                    this.server.processNetworkMessage(m);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                m = (NetworkMessage)in.readObject();
                out.println(NetworkTransport.MESSAGE_RECIEVED_ACK);
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.io.EOFException ex) {
            // this is expected when server closes socket
        } catch (IOException e) {
            System.out.println("server thread IOException");
        } finally {
            close();
        }
    }
    
    void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}