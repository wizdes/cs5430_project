package application.encryption_demo;


import application.encryption_demo.Messages.Message;
import application.encryption_demo.Messages.StringMessage;
import application.encryption_demo.DiscoveredPeers.Peer;
import application.encryption_demo.Messages.RequestDocUpdateMessage;
import application.encryption_demo.Messages.RequestJoinDocMessage;
import application.encryption_demo.Messages.UpdateDocumentMessage;
import application.encryption_demo.forms.ChatWindow;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import security_layer.PINFunctionality;
import security_layer.Profile;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptionDemoFunctionality {
    private ChatWindow gui;
    private String openedFilename;
    private CommunicationInterface communication;
    public PINFunctionality properPINInfo;
    private ConcurrentMap<String, DocumentInstance> docInstances = new ConcurrentHashMap<>();
    private Profile profile;
    
    public EncryptionDemoFunctionality(ChatWindow gui, Profile profile, String password){
        this.properPINInfo = new PINFunctionality();
        this.gui = gui;
        this.profile = profile;
        this.communication = new Communication(profile, password, this);
        listenForMessages();
    }
    
    private void listenForMessages() {
        new GUIListenerThread().start();
    }
    public void broadcastDiscovery(){
        communication.broadcastDiscovery();
    }
    
    public void manuallyAddPeer(String id, String host, int port, ArrayList<String> docs) {
        communication.updatePeers(id, host, port, docs, true);
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
    String encryptFile(String plaintext){
        communication.writeEncryptedFile(openedFilename, plaintext);
        return "This file was encrypted. Open it to see the encrpted text\n";
    }
    
    /**
     * Decrypts a file.
     * @param ciphertext String representation of file in ciphertext
     * @return Plaintext of file after being decrypted.
     */
    String decryptFile(String ciphertext){
        String plaintext = (String)communication.readEncryptedFile(openedFilename);
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
    
    public String createDocumentInstance(String ownerID, String docName){
        DocumentInstance instance = new DocumentInstance(ownerID, docName);
        instance.addCollaborators(profile.ident);
        docInstances.put(instance.getDocumentIdentifer(), instance);
        return instance.getDocumentIdentifer();
    }
    public boolean sendRequestDocUpdate(String docID, String text){
        DocumentInstance instance = docInstances.get(docID);
        return communication.sendMessage(instance.ownerID, new RequestDocUpdateMessage(profile.ident, instance.docName, text));
    }
    
    public boolean sendJoinRequestMessage(String ownerIdent, String docName){
        return communication.sendMessage(ownerIdent, new RequestJoinDocMessage(profile.ident, docName));
    }
    
    
//    public boolean broadcastEncryptedMessage(String plaintextMsg){        
//        return communication.broadcastMessage(new StringMessage(plaintextMsg));
//    }
    
    public boolean authenticateHuman(String ident) {
        return this.communication.authenticateHuman(ident);
    }
    
    public boolean authenticateMachine(String ident){
        return communication.authenticateMachine(ident);
    }
    
    public boolean addPIN(String ident, String pin) {
        return this.communication.updatePin(ident, pin);
    }
    
//    public void addPeerToGUI(Peer peer){
//        gui.addDiscoveredPeer(peer);
//    }
    public void updatePeersInGUI(DiscoveredPeers peers){
        System.out.println("Updating gui peers");
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
    
    private class GUIListenerThread extends Thread {
        
        private GUIListenerThread() {
            super("GUIListenerThread");
        }

        @Override
        public void run() {
            while (true) {
                Collection<Message> messages = communication.waitForMessages();
                for (Message m : messages) {
                    System.out.println(m);
                    if (m instanceof StringMessage) {
//                        String message = ((StringMessage)m).contents;
//                        String crypted = "This has been encrypted, trust us...";   
//                        System.out.println("here");
//                        System.out.println(message);
//                        displayIncomingMessage(message, crypted);
                    }
                    else if(m instanceof RequestJoinDocMessage){
                        //Owner: Adds sourceID to collaborators for document instance
                        RequestJoinDocMessage joinMsg = (RequestJoinDocMessage)m;
                        String docID = DocumentInstance.toDocumentIdentifier(profile.ident, joinMsg.docName);
                        
                        DocumentInstance instance = docInstances.get(docID);
                        instance.addCollaborators(joinMsg.sourceID);
                    }
                    else if(m instanceof RequestDocUpdateMessage){
                        //Owner: Broadcast doc update to everyone(including self)
                        RequestDocUpdateMessage docUpdate = (RequestDocUpdateMessage)m;
                        String docID = DocumentInstance.toDocumentIdentifier(profile.ident, docUpdate.docName);
                        
                        //Broadcast all atomically
                        DocumentInstance instance = docInstances.get(docID);
                        synchronized(instance.collaborators){
                            for(String collaborator: instance.collaborators.keySet()){
                                communication.sendMessage(collaborator, new UpdateDocumentMessage(instance.ownerID, instance.docName, docUpdate.text));
                            }
                        }
                    }
                    else if(m instanceof UpdateDocumentMessage){
                        //Collaborator: Add text to GUI
                        UpdateDocumentMessage updateMsg = (UpdateDocumentMessage)m;
                        String docID = DocumentInstance.toDocumentIdentifier(updateMsg.ownerID, updateMsg.docName);
                        
                        displayIncomingMessage(docID, updateMsg.text);
                    }
                }
            }
        }
    }
}
