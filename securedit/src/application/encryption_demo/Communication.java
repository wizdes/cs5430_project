/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;

import application.messages.EncryptedMessage;
import application.messages.Message;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import security_layer.SecureTransport;
import security_layer.SecureTransportInterface;
import transport_layer.network.NetworkTransport;
import transport_layer.network.NetworkTransportInterface;
import transport_layer.network.Node;
import transport_layer.network.Pair;

/**
 *
 */
public class Communication implements CommunicationInterface {
    
    private final Lock queueLock = new ReentrantLock(true);
    private final Lock replyLock = new ReentrantLock(true);
    private Condition newMessageArrived = queueLock.newCondition();
    private BlockingQueue<Message> messageQueue = new LinkedBlockingDeque<>();
    private Map<String, Pair<Condition, Message>> awaitingReplyQueue = new HashMap<>();
    private NetworkTransportInterface networkTransport;
    private SecureTransportInterface secureTransport;
    
    public Communication(String password, Node host) {
        this.networkTransport = new NetworkTransport(host, this);
        this.secureTransport = new SecureTransport(password, this.networkTransport);
    }
    
    @Override
    public java.io.Serializable sendAESEncryptedMessage(EncryptedMessage m, java.io.Serializable contents) {
        return this.secureTransport.sendAESEncryptedMessage(m, contents);
    }
    
    @Override
    public Message sendAESEncryptedMessageAndAwaitReply(EncryptedMessage m, java.io.Serializable contents) {
        sendAESEncryptedMessage(m, contents);
        return waitForReplyTo(m);
    }
    
    @Override
    public java.io.Serializable sendRSAEncryptedMessage(EncryptedMessage m, java.io.Serializable contents) {
        return this.secureTransport.sendRSAEncryptedMessage(m, contents);
    }
    
    @Override
    public Message sendRSAEncryptedMessageAndAwaitReply(EncryptedMessage m, java.io.Serializable contents) {
        sendRSAEncryptedMessage(m, contents);
        return sendMessageAndAwaitReply(m);
    }
    
    @Override
    public java.io.Serializable writeEncryptedFile(String filename, java.io.Serializable contents) {
        return this.secureTransport.writeEncryptedFile(filename, contents);
    }
    
    @Override
    public java.io.Serializable readEncryptedFile(String filename) {
        return this.secureTransport.readEncryptedFile(filename);
    }
    
    @Override
    public java.io.Serializable readUnencryptedFile(String filename) {
        return this.secureTransport.readUnencryptedFile(filename);
    }
    
    @Override
    public void shutdown() {
        this.networkTransport.shutdown();
    }
    
    @Override
    public void sendMessage(Message m) {
        this.networkTransport.send(m);
    }
    
    @Override
    public Message sendMessageAndAwaitReply(Message m) {
        this.networkTransport.send(m);
        return waitForReplyTo(m);
    }    
    
    @Override
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
    
    @Override
    public void depositMessage(Message m) throws NoSuchAlgorithmException{
        if (m instanceof EncryptedMessage) {
            m = this.secureTransport.processEncryptedMessage((EncryptedMessage)m);
        }
        
        // process replies separately
        if (m.getReplyTo() != null) {
            depositReply(m);
            return;
        }
        
        // if its not a reply it goes in the general queue
        this.queueLock.lock();
        try {
            this.messageQueue.add(m);
            this.newMessageArrived.signal();
        } finally {
            this.queueLock.unlock();
        }
    }
    
    private void depositReply(Message m) {
        String reply = m.getReplyTo();
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
        } finally {
            this.replyLock.unlock();
        }        
    }
    
    
    private Message waitForReplyTo(Message m) {
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
    
    @Override
    public SecureTransportInterface getSecureTransport() {
        return this.secureTransport;
    }

    @Override
    public void setSecureTransport(SecureTransportInterface sti) {
        this.secureTransport = sti;
    }
}
