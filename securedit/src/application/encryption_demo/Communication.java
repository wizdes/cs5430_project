/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;

import application.encryption_demo.Messages.Message;
import application.encryption_demo.Messages.StringMessage;
import application.encryption_demo.DiscoveredPeers.Peer;
import transport_layer.discovery.DiscoveryTransport;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
    private DiscoveredPeers discoveredPeers = new DiscoveredPeers();
    private EncryptionDemoFunctionality guiFunctionality;
    
    public Communication(Profile profile, String password) {
        //Only used for test packages
        this.secureTransport = new SecureTransport(profile, password, this);
    }
    public Communication(Profile profile, String password, EncryptionDemoFunctionality guiFunctionality) {
        this.secureTransport = new SecureTransport(profile, password, this);
        this.guiFunctionality = guiFunctionality;
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
    public boolean broadcastMessage(Message msg){
        boolean failure = false;
        System.out.println("Num peers:" + discoveredPeers.getPeers().values().size());
        for(Peer peer: discoveredPeers.getPeers().values()){
            authenticateMachine(peer.id);
            failure = !sendMessage(peer.id, msg) ? true : failure;
        }
        if(failure){
            return false;
        } else{
            return true;
        }
    }

    @Override
    public boolean authenticateMachine(String machineIdent) {
        return secureTransport.authenticate(machineIdent);
    }
    
    @Override
    public boolean authenticateHuman(String machineIdent) {
        if (discoveredPeers.getPeer(machineIdent).needsHumanAuth) {
            return secureTransport.initializeHumanAuthenticate(machineIdent);
        } else {
            return true;
        }
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

    @Override
    public void broadcastDiscovery() {
        secureTransport.broadcastDiscovery();
    }

    @Override
    public void updatePeers(String ident, String ip, int port, List<String> docs, boolean needsHumanAuth) {
        discoveredPeers.addPeer(ident, ip, port, docs, needsHumanAuth);
        secureTransport.addPeer(ident, ip, port);
        //guiFunctionality.addPeerToGUI(peers.getPeer(ident));
        if(guiFunctionality != null) guiFunctionality.updatePeersInGUI(discoveredPeers);
    }

    @Override
    public boolean updatePin(String ID, String PIN) {
        return secureTransport.addPIN(ID, PIN);
    }
}
