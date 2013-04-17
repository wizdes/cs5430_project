/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

import java.util.Set;

/**
 *
 */
public class DoRemove extends DocumentCommand {
    
    public Set<String> identifiers;
    
    public DoRemove(Set<String> identifiers) {
        this.identifiers = identifiers;
    }
    
    @Override
    public String toString() {
        String s = "DoRemove(";
        for (String s2 : identifiers) {
            s += s2 + ", ";
        }
        return s + ")";
    }    
}
