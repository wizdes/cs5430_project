package application.encryption_demo;


import application.encryption_demo.forms.ApplicationWindow;
import document.CommandMessage;
import document.NetworkDocumentInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import security_layer.Profile;
import transport_layer.discovery.DiscoveryMessage;

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
        
        this.communication = new Communication(this, profile, docInstances);
        listenForMessages();
    }
    public boolean authenticate(String machineIdent, String docID, char[] password){
        return communication.authenticate(machineIdent, docID, password);
    }
    public boolean initializeSRPAuthentication(String serverID, String docID, char[] password, char[] PIN){
        return communication.initializeSRPAuthentication(serverID, docID, password, PIN);
    }
    private void listenForMessages() {
        new GUIListenerThread().start();
    }
    public void broadcastDiscovery(){
        communication.broadcastDiscovery();
    }
    
    public void manuallyAddPeer(String id, String host, int port, ArrayList<String> docs) {
        communication.updatePeers(id, host, port, docs, false);
        DiscoveryMessage dm = new DiscoveryMessage(profile.username, profile.host, profile.port);
        communication.sendManualDiscoverMessage(id, host, port, dm);
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
     * @param filename name of the file to encrypt to
     * @return Encrypted version of the file.
     */
    public String encryptFile(String filename, Message m, char[] password){
        communication.writeEncryptedFile(filename, password, m);
        return "This file was encrypted. Open it to see the encrpted text\n";
    }

    
    public Message decryptObjFile(String filename, char[] password){
        return communication.readEncryptedObjectFile(filename, password);
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
                    if(m instanceof RequestJoinDocMessage){
                        //Owner: Adds sourceID to collaborators for document instance
                        RequestJoinDocMessage joinMsg = (RequestJoinDocMessage)m;
                        
                        NetworkDocumentInterface instance = docInstances.get(joinMsg.docName);
                        instance.addUserToLevel(joinMsg.sourceID, 0);
                        //instance.addUserToLevel(joinMsg.docName, 0);
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
