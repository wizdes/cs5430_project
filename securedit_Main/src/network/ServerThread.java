package network;


import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class ServerThread extends Thread {
    
    public static final String MESSAGE_RECIEVED_ACK = "OK";
    public static final String CONNECTION_FINISHED = "MSG_FIN";
    private Socket socket = null;
    private Server server = null;
    
    public ServerThread(Socket socket, Server server) {
        super("ServerThread");
        this.socket = socket;
        this.server = server;
    }

    public static int fromByteArray(byte[] bytes) {
     return ByteBuffer.wrap(bytes).getInt();
    }
    
    @Override
    public void run() {
        try {
            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int offset = 0;
            
            byte buffer[] = new byte[1024];
            byte[] messageLength = new byte[4];
            byte[] encryptionType = new byte[1];
            
            int l = in.read(messageLength, 0, 4);
            int length = fromByteArray(messageLength);
            
            while (length > -1) {
                                
                in.read(encryptionType, 0, 1);
                
                byte[] result = new byte[length];
                in.read(result, 0, length);
                this.server.depositMessage(encryptionType[0], result);
                out.println(MESSAGE_RECIEVED_ACK);
                
                l = in.read(messageLength, 0, 4);
                length = fromByteArray(messageLength);
            }
            
            Network.log("Server closed socket");
            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}