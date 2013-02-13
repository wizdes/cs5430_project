package network;

import java.io.*;
import java.net.*;
import messages.Message;

class Client {
        
    public String send(Message m) {
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
        if (in == null) {
            return response;
        }
        
        try {
            out.println(m.serialize());
            response = in.readLine();
            out.close();
            in.close();
            kkSocket.close();
        } catch (IOException ex) {
            System.err.println("IOException reading from server");
            ex.printStackTrace();
        }
        
        return response;
    }
}