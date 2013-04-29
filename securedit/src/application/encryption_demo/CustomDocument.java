/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.encryption_demo.forms.EditPanel;
import document.NetworkDocumentHandler;
import document.NetworkDocumentHandlerInterface;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Yi
 */
public class CustomDocument extends DefaultStyledDocument { 

    boolean isServer;
    private EditPanel curDoc;
    NetworkDocumentHandlerInterface nd;
    public int insertLevel;
    
    //the default construction of this class
    public CustomDocument(){
        isServer = false;
        insertLevel = 0;
    }

    //this custom document contains a document to apply operations
    public void giveDocument(NetworkDocumentHandlerInterface nd){
        this.nd = nd;
        isServer = nd.isOwner();
    }
    
    //sets the editor panel directly
    public void setEditorReference(EditPanel d) {
        this.curDoc = d;
    }
    
    //allows a straight to documentArea insert of text
    public void manualInsert(int offset, String string, AttributeSet attributeSet)
            throws BadLocationException {
        //System.out.println("Inserting: " + string);  
        if(attributeSet == null) attributeSet = curDoc.getColors().get(insertLevel);
        System.out.println("inserting: " + string);
        super.insertString(offset, string, attributeSet);
    }
    
    public void manualReplace(int offset, int length, String string, AttributeSet attributeSet)
            throws BadLocationException{
        super.remove(offset, length);
        if(string.length() == 1 && string.charAt(0) == 164){
            attributeSet = curDoc.getColors().get(0);
        }
        super.insertString(offset, string, attributeSet);
    }
    
    //allows a straight to documentArea remove of text
    public void manualRemove(int offset, int length)
            throws BadLocationException {
        //System.out.println("Removing: " + offset + " of length: " + length);      
        super.remove(offset, length);
    } 
    
    // applies the insert operation and potentially sends it to the peers
    @Override
    public void insertString(int offset, String string, AttributeSet attributeSet)
            throws BadLocationException {
        //actually, send it over the network
        //curDoc.observedDelta(offset, string.length(), string);
                
        //get the IDs
        int leftOffset = offset - 1;
        int rightOffset = offset;
        String lOffset = nd.getIdentifierAtIndex(leftOffset);
        String rOffset = nd.getIdentifierAtIndex(rightOffset);
        int levelStr = 0;
        if(nd.isEmpty() || (string.length() == 1 && string.charAt(0) == 164)){
            levelStr = 0;
        }
        else{
            levelStr = nd.getLevelAtIndex(offset - 1);
        }
        nd.requestInsert(insertLevel, lOffset, rOffset, string);
        
        //take thsi out when you're done
        if(isServer) {
            attributeSet = curDoc.getColors().get(insertLevel);
            manualInsert(offset, string, attributeSet);
        }
    }
    
    //applies the remove operation and potentially sends it to its peers
    @Override
    public void remove(int offset, int length) {
        //curDoc.observedDelta(offset, length, "");
        //take thsi out when you're done
        int originalLength = length;
        Set<String> remove = new HashSet<String>();
        while(length > 0){
            remove.add(nd.getIdentifierAtIndex(offset + length - 1));
            length -= 1;
        }
        
        nd.requestRemove(remove);
        if(isServer) {
            try {
                manualRemove(offset, originalLength);
                //curDoc.deltaCaretPosition(-1 * originalLength);
            } catch (BadLocationException ex) {
                Logger.getLogger(CustomDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    //sets the colors directly
    public void setColors(int offset, int length, AttributeSet as, boolean replace){
        super.setCharacterAttributes(offset, length, as, true);
    }
}