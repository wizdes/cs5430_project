package transport_layer.network;

import configuration.Constants;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                if(Constants.DEBUG_ON){
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Unknown host: " + node, ex);
                }
                close();
            } catch(IOException ex){
                if(Constants.DEBUG_ON){
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Couldn't get I/O for the connection : " + node, ex);
                }
                close();
            }
        }
        
        private boolean send(Serializable message) {
            String response = "";
            if (out != null && in != null) { 
                try {                    
                    // send message
                    out.writeObject(message);
                    out.flush();
                    response = in.readLine();
                } catch (IOException ex) {
                    if(Constants.DEBUG_ON){
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Client error reading from : " + node + " after send", ex);
                    }
                }
            }
            
            return response.equals(NetworkTransport.MESSAGE_RECIEVED_ACK);
        }
        
        private void close() {
            if(Constants.DEBUG_ON){
                Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client " + node + " closing socket.");
            }
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
                if(Constants.DEBUG_ON){
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Couldn't close socket to : " + node, ex);
                }
            }
        }   
    }
}