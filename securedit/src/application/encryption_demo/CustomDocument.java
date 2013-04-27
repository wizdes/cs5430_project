/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.encryption_demo.forms.EditPanel;
import document.NetworkDocumentHandler;
import document.NetworkDocumentInterface;
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
    NetworkDocumentInterface nd;
    public int insertLevel;
    
    public CustomDocument(){
        isServer = false;
        insertLevel = 0;
    }

    public void giveDocument(NetworkDocumentInterface nd){
        this.nd = nd;
        isServer = nd.isOwner();
    }
    
    public void manualInsert(int offset, String string, AttributeSet attributeSet)
            throws BadLocationException {
        // Do something here
        System.out.println("Inserting: " + string);  
        if(attributeSet == null) attributeSet = curDoc.colors.get(insertLevel);
        super.insertString(offset, string, attributeSet);
    }    
    
    public void manualRemove(int offset, int length)
            throws BadLocationException {
        // Do something here
        System.out.println("Removing: " + offset);      
        super.remove(offset, length);
    } 
    
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
        if(nd.isEmpty()){
            levelStr = 0;
        }
        else{
            levelStr = nd.getLevelAtIndex(offset - 1);
        }
        nd.requestInsert(insertLevel, lOffset, rOffset, string);
        
        //take thsi out when you're done
        if(isServer) {
            attributeSet = curDoc.colors.get(insertLevel);
            manualInsert(offset, string, attributeSet);
        }
    }
    
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
    
    public void setColors(int offset, int length, AttributeSet as, boolean replace){
        super.setCharacterAttributes(offset, length, as, true);
    }

    public void setEditorReference(EditPanel d) {
        this.curDoc = d;
    }
}