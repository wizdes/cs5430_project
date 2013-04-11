/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package document;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author goggin
 */
public interface AuthorizationDocument extends Document {
    
    /* Add a user to the collection of users collaborating on this document */
    public void addUser(String userId);
    
    /* Mark the given user as having the ability to edit the given level */
    public void addUserToLevel(String userId, int levelIdentifier);
    
    /* 
     * Returns a collection of messages ready to be broadcast to all of the peers
     * collaborating on this document, containing the appropriate changes they should see 
     * (e.g. they may get inserts with 'XXX' instead of text if they do not have the
     * required priviliges).
     * 
     * Returns NULL if the given userId did not have the requisite privileges.
     */
    public Collection<CommandMessage> applyInsert(String userId, 
                                                  int levelIdentifier, 
                                                  String leftIdentifier, 
                                                  String rightIdentifier, 
                                                  String text);
    
    /*
     * Returns a collection of messages that can be broadcast to all the peers 
     * collaborating on this document.
     * 
     * Returns null if the given userId does not have the priviliges to remove those
     * identifiers
     */
    public Collection<CommandMessage> applyRemove(String userId, Set<String> identifiers);
    
}
