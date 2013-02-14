package network;


import java.io.*;
import java.net.*;

public class ServerThread extends Thread {
    
    public static final String MESSAGE_RECIEVED_ACK = "OK";
    private Socket socket = null;
    private Server server = null;
    
    public ServerThread(Socket socket, Server server) {
        super("ServerThread");
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String recd = in.readLine().replaceAll(Client.NEW_LINE_TRANSLATION, "\n");
            this.server.depositMessage(recd);
            out.println(MESSAGE_RECIEVED_ACK);
            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}