/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

/**
 *
 */
public class DoReplace extends DocumentCommand {
    public String identifier;
    public String newValue;
    
    public DoReplace(String identifier, String newValue) {
        this.identifier = identifier;
        this.newValue = newValue;
    }
    
    @Override
    public String toString() {
        return "DoReplace(" + identifier + " ~>  " + newValue + ")";
    }
}
