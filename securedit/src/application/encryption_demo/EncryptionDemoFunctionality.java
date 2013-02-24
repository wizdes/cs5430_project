/*
 * Purpose is to perform all operations the GUI requires and to allow manipulation
 *   of the GUI.
 * 
 */
package application.encryption_demo;

import application.messages.DemoMessage;
import application.messages.Message;
import java.util.Collection;
import transport_layer.network.Node;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptionDemoFunctionality {
    private EncryptionDemoGUI gui;
    private String openedFilename;
    private CommunicationInterface communication;
    private Node collaborator;
        
    public EncryptionDemoFunctionality(EncryptionDemoGUI gui, Node host){
        this.gui = gui;
        
        //Temporary Field
        String password = "d2cb415e067c7b13";   //should be 16 bytes
        
        
        if (host.getID().equals("1")) {
            System.out.println("I'm talking to 2");
            collaborator = new Node("2", "localhost", 4002);
        } else {
            System.out.println("I'm talking to 1");
            collaborator = new Node("1", "localhost", 4001);
        }
        
        communication = new Communication(password, host);
        listenForMessages();
    }
    
    private void listenForMessages() {
        new GUIListenerThread().start();
    }
    
    /**
     * Open a file to be displayed in the GUI
     * @param filename
     * @return String representation of the file.
     */
    public String openFile(String filename){
        openedFilename = filename;
        String contents = (String)communication.readUnencryptedFile(filename);
        return contents;
    }
    
    /**
     * Encrypts a string and writes it out to a file.
     * @param plaintext String representation of file in plaintext
     * @return Encrypted version of the file.
     */
    public String encryptFile(String plaintext){
        String ciphertext = (String)communication.writeEncryptedFile(openedFilename, plaintext);
        return ciphertext;
    }
    
    /**
     * Decrypts a file.
     * @param ciphertext String representation of file in ciphertext
     * @return Plaintext of file after being decrypted.
     */
    public String decryptFile(String ciphertext){
        String plaintext = (String)communication.readEncryptedFile(openedFilename);
        return plaintext;
    }
    
    /**
     * Sends encrypted message.
     * @param plaintextMsg Message in plaintext
     * @return Encrypted version of message.
     */
    public String sendEncryptedMessage(String plaintextMsg) {
        DemoMessage dm = new DemoMessage(collaborator, "1", plaintextMsg);
        
        String ciphertext = (String)communication.sendAESEncryptedMessage(dm);
        return ciphertext;
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
    public void displayIncomingMessage(String plaintext, String ciphertext){
        //This should be called by thread that handles reading the message queue.
        gui.displayMessages(plaintext, ciphertext);
    }
    
    private class GUIListenerThread extends Thread {
        
        public GUIListenerThread() {
            super("GUIListenerThread");
        }

        @Override
        public void run() {
            while (true) {
                Collection<Message> messages = communication.waitForMessages();
                for (Message m : messages) {
                    System.out.println(m);
                    if (m instanceof DemoMessage) {
                        DemoMessage dm = (DemoMessage)m;
                        String crypted = "Hmmmm.....";   
                        System.out.println("here");
                        System.out.println(dm.getContents());
                        displayIncomingMessage(dm.getContents(), crypted);
                    }
                }
            }
        }
    }
}
