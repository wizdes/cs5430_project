package network;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import messages.Message;


public class Server {
    
    private Node node;
    private ClientListenerThread clientListener;
    private final Lock queueLock = new ReentrantLock(true);
    private final Lock replyLock = new ReentrantLock(true);
    private Condition newMessageArrived = queueLock.newCondition();
    private BlockingQueue<Message> messageQueue = new LinkedBlockingDeque<>();
    private Map<String, Pair<Condition, Message>> awaitingReplyQueue = new HashMap<>();
    
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
    
    public void depositMessage(byte[] bytes) {
        Message m = Message.fromBytes(bytes);
        if (m == null) {
            return;
        }
        
        String reply = m.getReplyTo();        
        if (reply != null) {
            this.replyLock.lock();
            try {
                if (this.awaitingReplyQueue.containsKey(reply)) {
                    Pair<Condition, Message> waiting = this.awaitingReplyQueue.get(reply);
                    Pair<Condition, Message> withReply = new Pair(waiting.x, m);
                    this.awaitingReplyQueue.put(reply, withReply);
                    waiting.x.signal();
                } else {
                    this.awaitingReplyQueue.put(reply, new Pair(null, m));
                }
                return;
            } finally {
                this.replyLock.unlock();
            }
        }

        
        this.queueLock.lock();
        try {
            this.messageQueue.add(m);
            this.newMessageArrived.signal();
        } finally {
            this.queueLock.unlock();
        }
    }
    
    public Message waitForReplyTo(Message m) {
        Condition replyArrived = this.replyLock.newCondition();
        Message response = null;
        String key = m.getMessageId();
        
        this.replyLock.lock();
        try {
            if (!this.awaitingReplyQueue.containsKey(key)) {
                this.awaitingReplyQueue.put(key, new Pair(replyArrived, null));
                replyArrived.await();
            }
            response = this.awaitingReplyQueue.get(key).y;
            this.awaitingReplyQueue.remove(key);
        } catch (InterruptedException ex) {
            System.err.println("Thread interrupted waiting for message reply");
        } finally {
            this.replyLock.unlock();
        }
        
        return response;
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
