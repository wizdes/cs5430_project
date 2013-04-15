/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package document;

import java.util.List;
import java.util.Set;

/**
 *
 * @author goggin
 */
public interface DocumentInterface {
    
    /* Create a new level, with no sections assigned to it */
    public void addLevel(int levelIdentifier);
    
    /* Assign an existing level to a section of text */
    public boolean assignLevel(int levelIdentifier, String leftIdentifier, String rightIdentifier);
    
    /* 
     * Attempt to insert the given text at the requested level, at the requested location.
     * Returns the actual level the text was inserted at.
     * Returns -1 if the text was not able to be inserted (neither leftIdentifier nor
     * rightIdentifier existed).
     */
    public int doInsert(int levelIdentifier, 
                        String leftIdentifier, 
                        String rightIdentifier, 
                        String text);
    
    /* The string identifier for the character at the given index */
    public String getIdentifierAtIndex(int index);
    
    /* Return the level for the given index */
    public int getLevelAtIndex(int index);
    
    /* Remove the given identifiers from the document */
    public void doRemove(Set<String> identifiers);
    public void doRemove(String identifier);
        
    public DocumentValue getValues();
    
    public String getString();
    
}
