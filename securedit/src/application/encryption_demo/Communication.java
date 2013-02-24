/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;

import application.messages.Message;
import java.io.Serializable;
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
    private SecureTransportInterface secureTransport;
    
    public Communication(String password, Node host) {
        this.secureTransport = new SecureTransport(password, host, this);
    }
    
    @Override
    public java.io.Serializable sendAESEncryptedMessage(Message m) {
        return this.secureTransport.sendAESEncryptedMessage(m);
    }
        
    @Override
    public java.io.Serializable sendRSAEncryptedMessage(Message m) {
        return this.secureTransport.sendRSAEncryptedMessage(m);
    }
    @Override
    public boolean authenticateMachine(Node dest){
        return secureTransport.authenticate(dest);
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
        this.secureTransport.shutdown();
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
        this.queueLock.lock();
        try {
            this.messageQueue.add(m);
            this.newMessageArrived.signal();
        } finally {
            this.queueLock.unlock();
        }
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
