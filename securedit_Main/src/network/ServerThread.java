package network;


import java.io.*;
import java.net.*;

public class ServerThread extends Thread {

    private Socket socket = null;
    private Server server = null;
    
    public ServerThread(Socket socket, Server server) {
        super("ServerThread");
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            this.server.depositMessage(in.readLine());
            out.println("ok");
            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}