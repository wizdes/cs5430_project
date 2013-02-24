/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;

import application.messages.CloseConnection;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends Thread {
    
    public static final String MESSAGE_RECIEVED_ACK = "OK";
    public static final String CONNECTION_FINISHED = "MSG_FIN";
    private Socket socket = null;
    private Server server = null;
    private PrintWriter out = null;
    private ObjectInputStream in = null;
    private ByteArrayOutputStream baos = null;
        
    public ServerThread(Socket socket, Server server) {
        super("ServerThread");
        this.socket = socket;
        this.server = server;
    }
    
    @Override
    public void run() {
        
        try {
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new ObjectInputStream(socket.getInputStream());
            baos = new ByteArrayOutputStream();
            
            Serializable m = (Serializable)in.readObject();          
            while (! (m instanceof CloseConnection)) {
                
                try {
                    this.server.depositMessage(m);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                out.println(MESSAGE_RECIEVED_ACK);
                m = (Serializable)in.readObject();
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
    
    private void close() {
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