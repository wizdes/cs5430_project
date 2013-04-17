/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.encryption_demo.forms.EditPanel;
import document.NetworkDocument;
import document.NetworkDocumentInterface;
import java.awt.Color;
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
    
    public CustomDocument(){
        isServer = false;
    }

    public void giveDocument(NetworkDocumentInterface nd){
        this.nd = nd;
        isServer = nd.isOwner();
    }
    
    public void manualInsert(int offset, String string, AttributeSet attributeSet)
            throws BadLocationException {
        // Do something here
        System.out.println("Inserting: " + string);        
        super.insertString(offset, string, attributeSet);
    }    
    
    public void manualRemove(int offset, int length)
            throws BadLocationException {
        // Do something here
        super.remove(offset, length);
    } 
    
    @Override
    public void insertString(int offset, String string, AttributeSet attributeSet)
            throws BadLocationException {
        //actually, send it over the network
        //curDoc.observedDelta(offset, string.length(), string);
        
        //get the IDs
        int leftOffset = offset;
        int rightOffset = offset + 1;
        String lOffset = nd.getIdentifierAtIndex(leftOffset);
        String rOffset = nd.getIdentifierAtIndex(rightOffset);
        int levelStr = nd.getLevelAtIndex(offset);
        nd.requestInsert(levelStr, lOffset, rOffset, string);
        
        //take thsi out when you're done
        if(isServer) {
            manualInsert(offset, string, attributeSet);
        }
    }
    
    @Override
    public void remove(int offset, int length)
            throws BadLocationException {
        //curDoc.observedDelta(offset, length, "");
        //take thsi out when you're done
        if(isServer) {
            manualRemove(offset, length);
        }
    }
    
    public void setColors(int offset, int length, AttributeSet as, boolean replace){
        super.setCharacterAttributes(offset, length, as, true);
    }

    public void setEditorReference(EditPanel d) {
        this.curDoc = d;
    }
}