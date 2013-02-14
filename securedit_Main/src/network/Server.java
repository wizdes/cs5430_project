package network;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import messages.Message;


public class Server {

    private Node node;
    private ClientListenerThread clientListener;
    private Lock queueLock = new ReentrantLock();
    private Condition newMessageArrived = queueLock.newCondition();
    private BlockingQueue<Message> messageQueue = new LinkedBlockingDeque<>();
    
    public Server(Node node) {
        this.node = node;
    }
    
    public Server(String id, String host, int port) {
        this(new Node(id, host, port));
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
        this.clientListener = new ClientListenerThread(this);
        this.clientListener.start();
    }

    public void shutdown() {
        this.clientListener.stopListening();
    }
    
    public static void main(String[] args) {
        Node n = new Node("server", "localhost", 4444);
        Server s = new Server(n);
        s.listen();
    }
}