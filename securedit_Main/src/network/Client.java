package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import messages.Message;

class Client {
    
    public static final String NEW_LINE_TRANSLATION = "%%%%%";    
    private ConcurrentMap<String, Channel> channelMap = new ConcurrentHashMap<>();
    
    public void closeSocketWith(Node n) {
        String key = n.toString();
        if(channelMap.containsKey(key)){
            channelMap.get(key).close();
        }
    }
    
    public boolean send(Message m) {
        Node destNode = m.getTo();
        String key = destNode.toString();
        
        if (!channelMap.containsKey(key)){
            channelMap.putIfAbsent(key, new Channel(destNode));
        }
        
        String toSend = m.serialize().replaceAll("\\n", NEW_LINE_TRANSLATION);
        return channelMap.get(key).send(toSend);
    }
    
    private class Channel {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private Node node;
        
        public Channel(Node n){
            node = n;
            initialize();
        }
        
        private void initialize() {
            try{
                socket = new Socket(node.getHost(), node.getPort());
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch(UnknownHostException ex){
                Network.logError("Unknown host: " + node);
                close();
            } catch(IOException ex){
                Network.logError("Couldn't get I/O for the connection : " + node);
                close();
            }
        }
        
        public boolean send(String message) {
            String response = "";
            
            if (out != null && in != null) { 
                out.println(message);
                Network.log("client sent " + message); 
                try {
                    response = in.readLine();
                } catch (IOException ex) {
                    Network.logError("Client error reading from : " + node);
                }
            }
            
            return response.equals(ServerThread.MESSAGE_RECIEVED_ACK);
        }
        
        public void close() {
            Network.log("Client closing socket with " + node);
            try {
                if(out != null) {
                    out.println(ServerThread.CONNECTION_FINISHED);
                    out.close();
                }
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                Network.logError("Couldn't close socket to : " + node);
            }
        }   
    }
}