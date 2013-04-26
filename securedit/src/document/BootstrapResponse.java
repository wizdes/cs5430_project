/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

/**
 *
 */
public class BootstrapResponse extends DocumentCommand {
    
    Document document;
    
    public BootstrapResponse(Document document) {
        this.document = document;
    }
    
    @Override
    public String toString() {
        return "BootstrapResponse";
    }
}
