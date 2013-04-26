/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package document;

import application.encryption_demo.forms.EditPanel;
import java.util.Set;
import security_layer.authentications.ServerAuthenticationPersistantState;

/**
 *
 * @author goggin
 */
public interface NetworkDocumentInterface {
    
    /* Called by the GUI when the user inserts.
     * If this document is owned by the current GUI
     *   it immediately applies the change
     *   broadcasts to everyone else who is listening
     * Else
     *   it forwards the request to the owners machine
     */
    void requestInsert(int level, String left, String right, String text);
    void requestInsert(int level, int left, int right, String text);
    
    void requestChangeLevel(int level);
    
    /* same as requestInsert, only for removing */
    void requestRemove(Set<String> identsToRemove);
    void requestRemove(int left, int right);
    
    /* Called by EncryptionDemoFunctionality when a CommandMessage is received */
    void processMessage(CommandMessage m);
    
    public boolean isOwner();
    
    public void giveGUI(EditPanel cd);
    
    public String getUserID();
    
    public void addUserToLevel(String userId, int levelIdentifier);
    public boolean assignLevel(int levelIdentifier, String leftIdentifier, String rightIdentifier);
    public boolean assignLevel(int levelIdentifier, int leftOffset, int rightOffset);
    
    /* Document proxies */
    public String getIdentifierAtIndex(int index);
    public boolean isEmpty();
    public int getLevelAtIndex(int index);
    public String getName();
    public String getOwnerID();
    public String getString();
    public int getLevelForUser(String userId);
    public ServerAuthenticationPersistantState getServerAuthenticationPersistantState();
    public AuthorizationDocumentInterface getAuthDocument();
    public void setAuthDocument(AuthorizationDocument ad);
    public void bootstrap();
    
}
