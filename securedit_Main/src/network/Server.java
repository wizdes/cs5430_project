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

    public static byte ENC_TYPE_NONE = 0;
    public static byte ENC_TYPE_AES = 1;
    public static byte ENC_TYPE_RSA = 2;
    
    private Node node;
    private ClientListenerThread clientListener;
    private final Lock queueLock = new ReentrantLock(true);
    private Condition newMessageArrived = queueLock.newCondition();
    private BlockingQueue<Message> messageQueue = new LinkedBlockingDeque<>();
    
    private String password;
    private String salt;
        
    public Server(Node node) {
        this.node = node;
    }
    
    public Server(String id, String host, int port) {
        this(new Node(id, host, port));
    }
    
    public Node getNode() {
      return this.node;
    }
    
    public void depositMessage(byte encType, byte[] bytes) {
        Message m = null;
        
        if (encType == ENC_TYPE_NONE) {
            m = Message.fromBytes(bytes);
        } else if (encType == ENC_TYPE_AES) {
            m = Message.fromEncryptedBytes(bytes, password, salt);
        } else {
            System.err.print("NO ENCRYPTION TYPE FOUND FOR " + encType);
        }
        
        if (m == null) {
            return;
        }
        
        this.queueLock.lock();
        try {
            this.messageQueue.add(m);
            this.newMessageArrived.signal();
        } finally {
            this.queueLock.unlock();
        }
    }
    
    public Collection<Message> waitForMessages() {
      Collection<Message> messages = new LinkedList<>();
      
      queueLock.lock();
      try {
        while (this.messageQueue.isEmpty()) {
          try {
            this.newMessageArrived.await();
          } catch (InterruptedException ex) {
            System.out.println("queueItemAdded.await() interrupted");
          }
        }
        this.messageQueue.drainTo(messages);
      } finally {
          queueLock.unlock();
      }
      
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
    
        public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
