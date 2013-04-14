/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.encryption_demo.forms.EditPanel;
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

    private EditPanel curDoc;
    
    public void manualInsert(int offset, String string, AttributeSet attributeSet)
            throws BadLocationException {
        // Do something here
        System.out.println("Inserting: " + string);
        super.insertString(offset, string, attributeSet);
    }    
    
    @Override
    public void insertString(int offset, String string, AttributeSet attributeSet)
            throws BadLocationException {
        //actually, send it over the network
        
        //
        curDoc.observedDelta(offset, string);
        //take thsi out when you're done
        manualInsert(offset, string, attributeSet);
    }
    
    public void setColors(int offset, int length, AttributeSet as, boolean replace){
        super.setCharacterAttributes(offset, length, as, true);
    }

    public void setEditorReference(EditPanel d) {
        this.curDoc = d;
    }
}