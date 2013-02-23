package transport_layer.network;

import application.messages.CloseConnection;
import application.messages.Message;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class Client {
    
    public static final String NEW_LINE_TRANSLATION = "%%%%%";    
    private ConcurrentMap<String, Client.Channel> channelMap = new ConcurrentHashMap<>();
    
    public void closeSocketWith(Node n) {
        String key = n.toString();
        if(channelMap.containsKey(key)){
            channelMap.get(key).close();
        }
    }
    
    public void closeConnections() {
        for (String key : channelMap.keySet()) {
            channelMap.get(key).close();
        }
    }
    
    public boolean send(Message m) {  
        Node destNode = m.getTo();
        String key = destNode.toString();
        
        if (!channelMap.containsKey(key)){
            channelMap.putIfAbsent(key, new Client.Channel(destNode));
        }
        
        return channelMap.get(key).send(m);
    }
    

    private class Channel {
        private Socket socket;
        private ObjectOutputStream out;
        private BufferedReader in;
        private Node node;
        
        public Channel(Node n){
            node = n;
            initialize();
        }
        
        private void initialize() {
            try{
                socket = new Socket(node.getHost(), node.getPort());
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
        
        public boolean send(Message message) {
            String response = "";
            
                        
            if (out != null && in != null) { 
                try {                    
                    // send message
                    out.writeObject(message);
                    out.flush();
                    response = in.readLine();
                } catch (IOException ex) {
                    System.err.println("Client error reading from : " + node);
                }
            }
            
            return response.equals(ServerThread.MESSAGE_RECIEVED_ACK);
        }
        
        public void close() {
            try {
                //Node dummy = new Node("close-connection", "not-used", 0);
                //send(new CloseConnection(dummy, node, "close-connection"));
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