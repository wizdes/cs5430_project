/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

/**
 *
 */
public class DoInsert extends DocumentCommand {
    
    public int levelIdentifier;
    public String leftIdentifier;
    public String rightIdentifier;
    public String text;
    
    public DoInsert(String leftIdentifier, String rightIdentifier, int levelIdentifier, String text) {
        this.leftIdentifier = leftIdentifier;
        this.rightIdentifier = rightIdentifier;
        this.levelIdentifier = levelIdentifier;
        this.text = text;
    }
    
}
