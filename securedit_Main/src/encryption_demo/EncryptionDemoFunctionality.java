/*
 * Purpose is to perform all operations the GUI requires and to allow manipulation
 *   of the GUI.
 * 
 */
package encryption_demo;

import File_Handler.File_Handler;
import encryption.AES;
import java.io.File;
import java.util.Collection;
import javax.crypto.SecretKey;
import messages.DemoMessage;
import messages.Message;
import network.Network;
import network.Node;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptionDemoFunctionality {
    private EncryptionDemoGUI gui;
    private Network network;
    private Node collaboratorNode;
    private String password = "I am a password";
    private String salt = "I am a salt";
    private SecretKey secret = AES.generateKey(password, salt);
    private File_Handler fHandler = new File_Handler();
    private String openedFilename;
    
    public EncryptionDemoFunctionality(EncryptionDemoGUI gui, Node appNode){
        this.gui = gui;
        this.network = new Network(appNode);
        File neighbors = new File("securedit_Main/src/network/hosts.txt");
        this.network.readNeighbors(neighbors);
        this.network.setSecret(secret);
        this.collaboratorNode = this.network.getNeighbors().get(0);
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
        return fHandler.readStringFile(filename);
    }
    
    /**
     * Encrypts a string and writes it out to a file.
     * @param plaintext String representation of file in plaintext
     * @return Encrypted version of the file.
     */
    public String encryptFile(String plaintext){
        //Encrypts the currently opened file and writes it back to the filesystem.
        AES aes = new AES(password, salt);
        String ciphertext = aes.encrypt(plaintext);
        fHandler.writeToFile(openedFilename, ciphertext);
        return ciphertext;
    }
    
    /**
     * Decrypts a file.
     * @param ciphertext String representation of file in ciphertext
     * @return Plaintext of file after being decrypted.
     */
    public String decryptFile(String ciphertext){
        //Decrypts the currently opened file.
        AES aes = new AES(password, salt);
        String plaintext = aes.decrypt(ciphertext);
        fHandler.writeToFile(openedFilename, plaintext);
        return plaintext;
    }
    
    /**
     * Sends encrypted message.
     * @param plaintextMsg Message in plaintext
     * @return Encrypted version of message.
     */
    public String sendEncryptedMessage(String plaintextMsg) {
        DemoMessage dm = new DemoMessage(this.collaboratorNode, plaintextMsg, secret);
        network.sendMessage(dm);
        return "the cipher text";
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
                Collection<Message> messages = network.waitForMessages();
                for (Message m : messages) {
                    if (m instanceof DemoMessage) {
                        DemoMessage dm = (DemoMessage)m;
                        String msg = dm.getContent();
                        gui.displayMessages(msg, "the cipher text");
                    }

                }
            }
        }
    }
    
}
