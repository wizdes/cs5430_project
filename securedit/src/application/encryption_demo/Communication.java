/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package application.encryption_demo;

import application.encryption_demo.Messages.Message;
import application.encryption_demo.Messages.StringMessage;
import application.encryption_demo.DiscoveredPeers.Peer;
import configuration.Constants;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Profile profile;
    public Communication(Profile profile, String password) {
        //Only used for test packages
        this.profile = profile;
        this.secureTransport = new SecureTransport(profile, password, this);
    }
    public Communication(Profile profile, String password, EncryptionDemoFunctionality guiFunctionality) {
        this.profile = profile;
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
              if(Constants.DEBUG_ON){
                  Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, "[User: " + profile.ident + "] Messages Queue interrupted exception.", ex);
              }
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
    
//    @Override
//    public boolean broadcastMessage(Message msg){
//        //Might need to protect this when iterating..but probably not
//        boolean failure = false;
//        
//        for(String peerID: discoveredPeers.peers.keySet()){
//            authenticateMachine(peerID);
//            failure = !sendMessage(peerID, msg) ? true : failure;
//        }
//        if(failure){
//            return false;
//        } else{
//            return true;
//        }
//    }

    @Override
    public boolean authenticateMachine(String machineIdent) {
        return secureTransport.authenticate(machineIdent);
    }
    
    @Override
    public boolean authenticateHuman(String machineIdent) {
        if (!discoveredPeers.getPeer(machineIdent).hasHumanAuthenticated) {
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
    public void updatePeers(String ident, String ip, int port, List<String> docs, boolean hasHumanAuthenticated) {
        discoveredPeers.addPeer(ident, ip, port, docs, hasHumanAuthenticated);
        secureTransport.addPeer(ident, ip, port);
        
        if(guiFunctionality != null){
            guiFunctionality.updatePeersInGUI(discoveredPeers);
        }
    }
    @Override
    public void updateHumanAuthStatus(String ident, boolean hasHumanAuthenticated){
        discoveredPeers.updateHumanAuthStatus(ident, hasHumanAuthenticated);
        guiFunctionality.updatePeersInGUI(discoveredPeers);
    }

    @Override
    public boolean updatePin(String ownerID, String PIN) {
        return secureTransport.addPIN(ownerID, PIN);
    }
    
    @Override
    public String getPIN(String ID){
        return secureTransport.getPIN(ID);
    }
}
