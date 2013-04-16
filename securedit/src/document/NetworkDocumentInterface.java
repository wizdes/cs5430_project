/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package document;

import java.util.Set;

/**
 *
 * @author goggin
 */
public interface NetworkDocumentInterface extends AuthorizationDocumentInterface {
    
    /* Called by the GUI when the user inserts.
     * If this document is owned by the current GUI
     *   it immediately applies the change
     *   broadcasts to everyone else who is listening
     * Else
     *   it forwards the request to the owners machine
     */
    void requestInsert(int level, String left, String right, String text);
    
    /* same as requestInsert, only for removing */
    void requestRemove(Set<String> identsToRemove);
    
    /* Called by EncryptionDemoFunctionality when a CommandMessage is received */
    void processMessage(CommandMessage m);
    
    
}
