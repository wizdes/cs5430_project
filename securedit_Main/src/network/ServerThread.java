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
            String recd = in.readLine();
            System.out.println("recieved " + recd);
            this.server.depositMessage(recd);
            out.println("ok");
            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}