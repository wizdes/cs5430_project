/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;

import configuration.Constants;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientListenerThread extends Thread {
        
    private boolean listening = true;
    private ServerSocket serverSocket; 
    private int port;
    private Server server;

    ClientListenerThread(Server server, int port) {
        super("ClientListener");
        this.server = server;
        this.port = port;
    }

    void stopListening() {
        this.listening = false;
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(ClientListenerThread.class.getName()).log(Level.SEVERE, "Failed to close server socket at port: " + port, ex);
            }
        }
    } 

    @Override
    public void run() {

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(ClientListenerThread.class.getName()).log(Level.SEVERE, "Could not listen on port: " + port + ".", ex);
            }
            System.exit(-1);
        }

        try {
            while (this.listening) {
                Socket socket = serverSocket.accept();
                ServerThread t = new ServerThread(socket, this.server);
                t.start();
            }
            serverSocket.close();
        } catch (SocketException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(ClientListenerThread.class.getName()).log(Level.SEVERE, "ServerSocket closed, port: " + port, ex);
            }
        } catch (IOException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(ClientListenerThread.class.getName()).log(Level.SEVERE, "ServerSocket closed, port: " + port, ex);
            }
        } 
    }
}