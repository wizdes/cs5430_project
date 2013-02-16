package network;

import java.io.*;
import java.net.*;
import messages.Message;

class Client {
    
    public static final String NEW_LINE_TRANSLATION = "%%%%%";
    
    public boolean send(Message m) {
        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            kkSocket = new Socket(m.getTo().getHost(), m.getTo().getPort());
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + m.getTo());
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + m.getTo());
        }
        
        String response = "";
        if (in == null) return false;
        
        try {
            String toSend = m.serialize().replaceAll("\\n", NEW_LINE_TRANSLATION);
            out.println(toSend);
            response = in.readLine();
            out.close();
            in.close();
            kkSocket.close();
        } catch (IOException ex) {
            System.err.println("IOException reading from server");
            ex.printStackTrace();
        }
        
        return response.equals(ServerThread.MESSAGE_RECIEVED_ACK);
    }
}