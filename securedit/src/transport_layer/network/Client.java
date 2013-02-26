package transport_layer.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class Client {
    private ConcurrentMap<String, Client.Channel> channelMap = new ConcurrentHashMap<>();
    
    Client(){}
    
    void closeSocketWith(Node n) {
        String key = n.toString();
        if(channelMap.containsKey(key)){
            channelMap.get(key).close();
        }
    }
    
    void closeConnections() {
        for (String key : channelMap.keySet()) {
            channelMap.get(key).close();
        }
    }
    
    boolean send(Node destNode, NetworkMessage m) {  
        String key = destNode.toString();
        
        System.out.println("[debug] sned 1 ");
        if (!channelMap.containsKey(key)) {
            Channel c = new Channel(destNode);
            channelMap.putIfAbsent(key, c);
        }
        
        return channelMap.get(key).send(m);
    }
    

    private class Channel {
        private Socket socket;
        private ObjectOutputStream out;
        private BufferedReader in;
        private Node node;
        
        private Channel(Node n){
            node = n;
            initialize();
        }
        
        private void initialize() {
            try{
                socket = new Socket(node.host, node.port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch(UnknownHostException ex){
                System.err.println("Unknown host: " + node);
                close();
            } catch(IOException ex){
                System.err.println("Couldn't get I/O for the connection : " + node);
                close();
            }
        }
        
        private boolean send(Serializable message) {
            String response = "";
            if (out != null && in != null) { 
                try {                    
                    // send message
                    System.out.println("[debug] write object");
                    out.writeObject(message);
                    out.flush();
                    System.out.println("[debug] read");
                    response = in.readLine();
                    System.out.println("[debug] read it");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.err.println("Client error reading from : " + node + " after send");
                }
            }
            
            return response.equals(NetworkTransport.MESSAGE_RECIEVED_ACK);
        }
        
        private void close() {
            System.out.println("client closing socket");
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                System.err.println("Couldn't close socket to : " + node);
            }
        }   
    }
}