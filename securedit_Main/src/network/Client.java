package network;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.Message;

class Client {
    
    public static final String NEW_LINE_TRANSLATION = "%%%%%";
    
    private HashMap<String, Socket> socketMap = new HashMap<>();
    
    private static Socket kkSocket = null;
    private static PrintWriter out = null;
    private static BufferedReader in = null;
    
    private Socket getSocketForMessage(Message m) {
        String key = m.getTo().getHost() + ":" + m.getTo().getPort();
        Socket result = null;
        
        if (socketMap.containsKey(key)) {
            result = socketMap.get(key);
            System.out.println("reusing socket with " + m.getTo());
        } else {
            try {
                System.out.println("new socket with " + m.getTo());
                result = new Socket(m.getTo().getHost(), m.getTo().getPort());
                socketMap.put(key, result);
            } catch (UnknownHostException ex) {
                System.err.println("Unknown host: " + m.getTo());
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to: " + m.getTo());
            }
        }
//        try {
//            new Socket(m.getTo().getHost(), m.getTo().getPort());
//
//        } catch (IOException ex) {
//            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//        }
        System.out.println("returning a socket? " + (result == null ? "no" : "yes"));
        return result;
    }
    
    public boolean send(Message m) {
        //Socket kkSocket = null;
        //PrintWriter out = null;
        //BufferedReader in = null;

        try {
            if (kkSocket == null) {
                kkSocket = getSocketForMessage(m);
                out = new PrintWriter(kkSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
            }
            //kkSocket = socket;
            //kkSocket = new Socket(m.getTo().getHost(), m.getTo().getPort());

        } catch (IOException e) {
             e.printStackTrace();
            System.err.println("Failed reading or writing to: " + m.getTo());
        }

        String response = "";
        if (in == null) {
            return false;
        }
        
        try {
            String toSend = m.serialize().replaceAll("\\n", NEW_LINE_TRANSLATION);
            out.println(toSend);
            response = in.readLine();
            System.out.println("client recd " + response); 
           //out.close();
            //in.close();
        } catch (IOException ex) {
            System.err.println("IOException reading from server");
        }
        
        return response.equals(ServerThread.MESSAGE_RECIEVED_ACK);
    }
}