package network;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
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
        byte[] toSend = m.serialize();
        Node destNode = m.getTo();
        byte encType = Server.ENC_TYPE_NONE;
        return send(destNode, toSend, encType);
    }
    
    public boolean send(Node destNode, byte[] bytes, byte encType) {
        String key = destNode.toString();
        
        if (!channelMap.containsKey(key)){
            channelMap.putIfAbsent(key, new Channel(destNode));
        }
        
        return channelMap.get(key).send(bytes, encType);
    }
    
    private class Channel {
        private Socket socket;
        private OutputStream out;
        private BufferedReader in;
        private Node node;
        
        public Channel(Node n){
            node = n;
            initialize();
        }
        
        private void initialize() {
            try{
                socket = new Socket(node.getHost(), node.getPort());
                out = socket.getOutputStream();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch(UnknownHostException ex){
                Network.logError("Unknown host: " + node);
                close();
            } catch(IOException ex){
                Network.logError("Couldn't get I/O for the connection : " + node);
                close();
            }
        }
        
        public byte[] toByteArray(int value) {
            return  ByteBuffer.allocate(4).putInt(value).array();
        }

        public boolean send(byte[] message, byte encType) {
            String response = "";
            
            byte[] messageLength = toByteArray(message.length);  // 4 bytes
            byte[] encryptionType = new byte[1];
            encryptionType[0] = encType;
            byte[] fullMessage = new byte[message.length + 4 + 1];
            
            System.arraycopy(messageLength, 0, fullMessage, 0, 4 - 1);
            System.arraycopy(encryptionType, 0, fullMessage, 4, 1);
            System.arraycopy(message, 0, fullMessage, 4 + 1, message.length - 1);
            
            if (out != null && in != null) { 
                try {
                    // send length of message
                    out.write(messageLength);
                    out.flush();
                    
                    // send encryption type
                    out.write(encryptionType);
                    out.flush();
                    
                    // send message
                    out.write(message);
                    out.flush();
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
                    out.write(toByteArray(-1));
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