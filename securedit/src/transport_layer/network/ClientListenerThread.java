/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

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

        }
    } 

    @Override
    public void run() {

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ".");
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
            System.out.println("socket closed from socket exception");
            //ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("IOException in client listener socket closed");
           // ex.printStackTrace();
        } 
    }
}