package network;

import messages.Message;
import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {

    private Node node;
    private ClientListener clientListener;
    private Lock queueLock = new ReentrantLock();
    private Condition newMessageArrived = queueLock.newCondition();
    private BlockingQueue<Message> messageQueue = new LinkedBlockingDeque<Message>();
    
    public Server(Node node) {
        this.node = node;
    }
    
    public Node getNode() {
      return this.node;
    }
    
    public void depositMessage(String msg) {
        Message m = Message.fromString(msg);
        if (m == null) return;
        
        this.queueLock.lock();
        this.messageQueue.add(m);
        this.newMessageArrived.signal();
        this.queueLock.unlock();
    }
    
    public Collection<Message> waitForMessages() {
      Collection<Message> messages = new LinkedList<Message>();
      
      queueLock.lock();
      while (this.messageQueue.isEmpty()) {
        try {
          this.newMessageArrived.await();
        } catch (InterruptedException ex) {
          System.out.println("queueItemAdded.await() interrupted");
        }
      }
      
      this.messageQueue.drainTo(messages);
      queueLock.unlock();
      return messages;
    }
    
    public void listen() {
        System.out.println(this.node + " listening...");
        this.clientListener = new ClientListener(this);
        this.clientListener.start();
    }

    public void shutdown() {
        this.clientListener.stopListening();
    }
        
    private class ClientListener extends Thread {
        
        private boolean listening = true;
        private ServerSocket serverSocket; 
        private Server server;
        
        public ClientListener(Server server) {
            super("ClientListener");
            this.server = server;
        }
        
        public void stopListening() {
            this.listening = false;
            try {
                if (this.serverSocket != null) {
                    this.serverSocket.close();
                }
            } catch (IOException ex) {
                
            }
        } 
        
        @Override
        public void run() {

            try {
                this.serverSocket = new ServerSocket(this.server.getNode().getPort());
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + this.server.getNode().getPort() + ".");
                System.exit(-1);
            }

            try {
                while (this.listening) {
                    Socket socket = serverSocket.accept();
                    ServerThread t = new ServerThread(socket, this.server);
                    t.start();
                }
                serverSocket.close();
            } catch (SocketException ex) {
                System.out.println("socket closed");
            } catch (IOException ex) {
                ex.printStackTrace();
            } 
        }
    }
    
    public static void main(String[] args) {
        Node n = new Node("server", "localhost", 4444);
        Server s = new Server(n);
        s.listen();
    }
}
