package transport_layer.network;

import configuration.Constants;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

class Client {
    private ConcurrentMap<Node, Client.Channel> channelMap = new ConcurrentHashMap<>();
        
    void closeSocketWith(Node n) {
        if(channelMap.containsKey(n)){
            channelMap.get(n).close();
        }
    }
    
    void closeConnections() {
        for (Node key : channelMap.keySet()) {
            channelMap.get(key).close();
        }
    }
    
    boolean send(Node destNode, NetworkMessage m) {  
        if (!channelMap.containsKey(destNode)) {
            Channel c = new Channel(destNode);
            if (c.initialize()) {
                channelMap.put(destNode, c);
            } else {
                return false;
            }
        }
        return channelMap.get(destNode).send(m);
    }
    
    private class Channel {
        private Socket socket;
        private ObjectOutputStream out;
        private BufferedReader in;
        private Node node;
        
        private Channel(Node n){
            node = n;
        }
        
        public boolean initialize() {
            boolean success = false;
            try{
                socket = new Socket(node.host, node.port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                success = true;
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
            
            return success;
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
                    close();                    
                }
            }
            
            return response.equals(NetworkTransport.MESSAGE_RECIEVED_ACK);
        }
        
        private void close() {
            channelMap.remove(node);
            
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