package application.encryption_demo;


import application.encryption_demo.Peers.Peer;
import java.util.ArrayList;
import java.util.Collection;
import security_layer.PINFunctionality;
import security_layer.Profile;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptionDemoFunctionality {
    private EncryptionDemoGUI gui;
    private String openedFilename;
    private CommunicationInterface communication;
    public PINFunctionality properPINInfo;
        
    public EncryptionDemoFunctionality(EncryptionDemoGUI gui, Profile profile, String password){
        this.properPINInfo = new PINFunctionality();
        this.gui = gui;
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
    
    /**
     * Sends encrypted message.
     * @param plaintextMsg Message in plaintext
     * @return Encrypted version of message.
     */

    public boolean sendEncryptedMessage(String ident, String plaintextMsg) {
        return communication.sendMessage(ident, new StringMessage(plaintextMsg));
    }
    
    public boolean broadcastEncryptedMessage(String plaintextMsg){        
        boolean failure = false;
        for(String peer: gui.peers){
            authenticateMachine(peer);
            failure = !sendEncryptedMessage(peer, plaintextMsg) ? true : failure;
        }
        if(failure){
            return false;
        } else{
            return true;
        }
    }
    
    public String authenticateMachine(String ident){
        boolean wasSuccessful = communication.authenticateMachine(ident);
        if(wasSuccessful){
            return "Successfully authenticated machine\n";
        } else{
            return "Machine authentication was not successful\n";
        }
    }
    
//    public void addPeerToGUI(Peer peer){
//        gui.addDiscoveredPeer(peer);
//    }
    public void updatePeersInGUI(Peers peers){
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
    void displayIncomingMessage(String plaintext, String ciphertext){
        //This should be called by thread that handles reading the message queue.
        gui.displayMessages(plaintext, ciphertext);
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
                        String message = ((StringMessage)m).contents;
                        String crypted = "This has been encrypted, trust us...";   
                        System.out.println("here");
                        System.out.println(message);
                        displayIncomingMessage(message, crypted);
                    }
                }
            }
        }
    }
}
