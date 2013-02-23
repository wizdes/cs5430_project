/*
 * Purpose is to perform all operations the GUI requires and to allow manipulation
 *   of the GUI.
 * 
 */
package application.encryption_demo;

import security_layer.SecureTransport;
import security_layer.SecureTransportInterface;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptionDemoFunctionality {
    private EncryptionDemoGUI gui;
    private String openedFilename;
    private SecureTransportInterface secureTransport;
        
    public EncryptionDemoFunctionality(EncryptionDemoGUI gui){
        this.gui = gui;
        
        //Temporary Field
        String password = "d2cb415e067c7b13";   //should be 16 bytes
        
        secureTransport = new SecureTransport(password);
    }
    
    /**
     * Open a file to be displayed in the GUI
     * @param filename
     * @return String representation of the file.
     */
    public String openFile(String filename){
        openedFilename = filename;
        String contents = (String)secureTransport.readUnencryptedFile(filename);
        return contents;
    }
    
    /**
     * Encrypts a string and writes it out to a file.
     * @param plaintext String representation of file in plaintext
     * @return Encrypted version of the file.
     */
    public String encryptFile(String plaintext){
        String ciphertext = (String)secureTransport.writeEncryptedFile(openedFilename, plaintext);
        return ciphertext;
    }
    
    /**
     * Decrypts a file.
     * @param ciphertext String representation of file in ciphertext
     * @return Plaintext of file after being decrypted.
     */
    public String decryptFile(String ciphertext){
        String plaintext = (String)secureTransport.readEncryptedFile(openedFilename);
        return plaintext;
    }
    
    /**
     * Sends encrypted message.
     * @param plaintextMsg Message in plaintext
     * @return Encrypted version of message.
     */
    public String sendEncryptedMessage(String plaintextMsg) {
        String ciphertext = (String)secureTransport.sendAESEncryptedMessage(plaintextMsg);
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
}
