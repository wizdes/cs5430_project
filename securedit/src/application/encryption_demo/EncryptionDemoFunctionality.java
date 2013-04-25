package application.encryption_demo;


import security_layer.Profile;
import application.encryption_demo.Messages.Message;
import application.encryption_demo.Messages.StringMessage;
import application.encryption_demo.DiscoveredPeers.Peer;
import transport_layer.discovery.DiscoveryMessage;
import application.encryption_demo.Messages.RequestDocUpdateMessage;
import application.encryption_demo.Messages.RequestJoinDocMessage;
import application.encryption_demo.Messages.UpdateDocumentMessage;
import application.encryption_demo.forms.ApplicationWindow;
import document.CommandMessage;
import document.Document;
import document.DocumentInterface;
import document.NetworkDocumentInterface;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import security_layer.PINFunctionality;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptionDemoFunctionality {
    private ApplicationWindow gui;
    private String openedFilename;
    private CommunicationInterface communication;
//    public PINFunctionality properPINInfo;
    private ConcurrentMap<String, NetworkDocumentInterface> docInstances = new ConcurrentHashMap<>();
    private Profile profile;
    
    public CommunicationInterface getCommunicationInterface(){
        return communication;
    }
    
    public EncryptionDemoFunctionality(ApplicationWindow gui, Profile profile){
//        this.properPINInfo = new PINFunctionality();
        this.profile = profile;
        this.gui = gui;
        
        this.communication = new Communication(this, profile);
        listenForMessages();
    }
    
    private void listenForMessages() {
        new GUIListenerThread().start();
    }
    public void broadcastDiscovery(){
        communication.broadcastDiscovery();
    }
    
    public void manuallyAddPeer(String id, String host, int port, ArrayList<String> docs) {
//        communication.updatePeers(id, host, port, docs, false);
        DiscoveryMessage dm = new DiscoveryMessage(profile.username, profile.host, profile.port);
        communication.sendManualDiscoverMessage(id, host, port, dm);
    }
    
    public boolean login(String machineId, String docID, char[] password){
        return communication.authenticate(machineId, docID, password);
    }
    
    /**
     * Open a file to be displayed in the GUI
     * @param filename
     * @return String representation of the file.
     */
    String openFile(String filename){
        openedFilename = filename;
        String contents = (String)communication.readFile(filename);
        return contents;
    }
    
    /**
     * Encrypts a string and writes it out to a file.
     * @param plaintext String representation of file in plaintext
     * @return Encrypted version of the file.
     */
    String encryptFile(String plaintext, char[] password){
        communication.writeEncryptedFile(openedFilename, password, plaintext);
        return "This file was encrypted. Open it to see the encrpted text\n";
    }
    
    /**
     * Decrypts a file.
     * @param ciphertext String representation of file in ciphertext
     * @return Plaintext of file after being decrypted.
     */
    String decryptFile(String ciphertext, char[] password){
        String plaintext = (String)communication.readEncryptedFile(openedFilename, password);
        return plaintext;
    }
    
//    /**
//     * Sends encrypted message.
//     * @param plaintextMsg Message in plaintext
//     * @return Encrypted version of message.
//     */
//
//    public boolean sendEncryptedMessage(String ident, String plaintextMsg) {
//        return communication.sendMessage(ident, new StringMessage(plaintextMsg));
//    }
    
    public String createDocumentInstance(NetworkDocumentInterface instance) {
        docInstances.put(instance.getName(), instance);
        return instance.getName();
    }
    
    public boolean sendRequestDocUpdate(String docID, String text){
        NetworkDocumentInterface instance = docInstances.get(docID);
        return communication.sendMessage(instance.getOwnerID(), docID, new RequestDocUpdateMessage(profile.username, instance.getName(), text));
    }
    
    public boolean sendJoinRequestMessage(String ownerIdent, String docName){
        return communication.sendMessage(ownerIdent, docName, new RequestJoinDocMessage(profile.username, docName));
    }
    
    
//    public boolean broadcastEncryptedMessage(String plaintextMsg){        
//        return communication.broadcastMessage(new StringMessage(plaintextMsg));
//    }
    
//    public boolean authenticateHuman(String ident) {
//        return this.communication.authenticateHuman(ident);
//    }
//    
//    public boolean authenticateMachine(String ident){
//        return communication.authenticateMachine(ident);
//    }
    
//    public boolean addPIN(String ident, String pin) {
//        return this.communication.updatePin(ident, pin);
//    }
    
//    public void updateHumanAuthStatus(String id, boolean hasHumanAuthenticated){
//        this.communication.updateHumanAuthStatus(id, hasHumanAuthenticated);
//    }
    
//    public void addPeerToGUI(Peer peer){
//        gui.addDiscoveredPeer(peer);
//    }
    public void updatePeersInGUI(DiscoveredPeers peers){
        gui.updateDiscoveredPeers(peers);
    }

/**********************************************************************
 *  Methods used to control the GUI. 
 *   -Currently only need to allow displaying received messages.
 **********************************************************************/
    /**
     * Displays both the plaintext and the ciphertext of the received message.
     * @param plaintext Decrypted version of the message.
     * @param ciphertext Encrypted version of the message.
     */
    void displayIncomingMessage(String docID, String plaintext){
        //This should be called by thread that handles reading the message queue.
        gui.displayMessages(docID, plaintext);
    }
    private final ReentrantLock atomicBroadcastLock = new ReentrantLock();
    
    public char[] generatePIN(String userId, String docID) {
        return this.communication.generatePIN(userId, docID);
    }
    
    private class GUIListenerThread extends Thread {
        
        private GUIListenerThread() {
            super("GUIListenerThread");
        }

        @Override
        public void run() {
            while (true) {
                Collection<Message> messages = communication.waitForMessages();
                
                for (Message m : messages) {
                    if (m instanceof StringMessage) {
//                        String message = ((StringMessage)m).contents;
//                        String crypted = "This has been encrypted, trust us...";   
//                        displayIncomingMessage(message, crypted);
                    }
                    else if(m instanceof RequestJoinDocMessage){
                        //Owner: Adds sourceID to collaborators for document instance
                        RequestJoinDocMessage joinMsg = (RequestJoinDocMessage)m;
                        
                        NetworkDocumentInterface instance = docInstances.get(joinMsg.docName);
                        instance.addUserToLevel(joinMsg.sourceID, 0);
                        //instance.addUserToLevel(joinMsg.docName, 0);
                    }
                    else if(m instanceof UpdateDocumentMessage){
                        //Collaborator: Add text to GUI
                        UpdateDocumentMessage updateMsg = (UpdateDocumentMessage)m;
                        String docID = DocumentInstance.toDocumentIdentifier(updateMsg.ownerID, updateMsg.docName);
                        
                        displayIncomingMessage(docID, updateMsg.text);
                    }
                    else if (m instanceof DiscoveryMessage) {

                    } else if (m instanceof CommandMessage) {
                        CommandMessage cm = (CommandMessage)m;
                        NetworkDocumentInterface instance = docInstances.get(cm.documentName);
                        instance.processMessage(cm);
                    }
                }
            }
        }
    }
}
