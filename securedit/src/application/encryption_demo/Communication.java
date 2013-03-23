/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import security_layer.Profile;
import security_layer.SecureTransport;
import security_layer.SecureTransportInterface;

/**
 *
 */
public class Communication implements CommunicationInterface {
    private final Lock queueLock = new ReentrantLock(true);
    private Condition newMessageArrived = queueLock.newCondition();
    private BlockingQueue<Message> messageQueue = new LinkedBlockingDeque<>();
    private SecureTransportInterface secureTransport;
    
    public Communication(Profile profile, String password) {
        this.secureTransport = new SecureTransport(profile, password, this);
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
    public boolean sendMessage(String destination, Message msg) {
        return secureTransport.sendAESEncryptedMessage(destination, msg);
    }

    @Override
    public boolean authenticateMachine(String machineIdent) {
        return secureTransport.authenticate(machineIdent);
    }

    @Override
    public boolean writeEncryptedFile(String filename, String contents) {
        return secureTransport.writeEncryptedFile(filename, new StringMessage(contents));
    }

    @Override
    public String readEncryptedFile(String filename) {
        return ((StringMessage)secureTransport.readEncryptedFile(filename)).contents;
    }

    @Override
    public String readFile(String filename) {
        return secureTransport.readUnencryptedFile(filename);
    }

    @Override
    public void shutdown() {
        secureTransport.shutdown();
    }
}
