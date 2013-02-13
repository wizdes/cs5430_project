package network;

import java.io.*;
import java.net.*;

class Client {
    
    private Node node;
    
    public Client(Node n) {
      this.node = n;
    }
    
    public String send(String message) {
        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            kkSocket = new Socket(this.node.getHost(), this.node.getPort());
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + this.node);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + this.node);
        }
        
        String response = "";
        if (in == null) {
            return response;
        }
        
        try {
            out.println(message);
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
    
    public static void main(String[] args) {
        Node n = new Node("me", "localhost", 4444);
        Client client = new Client(n);
        String response = client.send("hello world");
        System.out.println("Recieved from server : " + response);
    }
}