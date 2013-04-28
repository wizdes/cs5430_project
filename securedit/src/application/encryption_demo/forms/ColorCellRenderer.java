/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo.forms;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author yjli_000
 */
class ColorCellRenderer extends DefaultListCellRenderer {  
    
    public ColorCellRenderer() {
        
    }
    
    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {  
        Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );  
        c.setBackground(colorsList.get(index));  
        return c;  
    }
    
    public void giveColorList(ArrayList<Color> colorsList){
        this.colorsList = colorsList;
    }
    
    ArrayList<Color> colorsList;
}  
  

