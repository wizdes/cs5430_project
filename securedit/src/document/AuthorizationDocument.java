/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 */
public class AuthorizationDocument extends Document implements AuthorizationDocumentInterface {
    
    protected Map<String, Integer> peers = new HashMap<>();
    
    public AuthorizationDocument() {
        this("OwnerId", "Document");
    }
    
    public AuthorizationDocument(String ownerId, String name) {
        super(ownerId, name);
    }
    
    @Override
    public void addUserToLevel(String userId, int level) {
        peers.put(userId, level);
        addLevel(level);
    }

    @Override
    public Collection<CommandMessage> applyInsert(String userId, 
                                                  int level, 
                                                  String leftIdentifier, 
                                                  String rightIdentifier, 
                                                  String text) {
        
        if (!canAccess(userId, level)) {
            return null;
        }
        
        doInsert(level, leftIdentifier, rightIdentifier, text);
        
        Collection<CommandMessage> updates = new LinkedList<>();
        
        for(Entry<String, Integer> entry : peers.entrySet()) {
            String uid = entry.getKey();
            if (uid.equals(this.getOwnerID())) {
                continue;
            }
            Integer l = entry.getValue();
            DoInsert di;
            if (l >= level) {
                di = new DoInsert(leftIdentifier, rightIdentifier, level, text);
            } else {
                String hidden = new String(new char[text.length()]).replace('\0', 'X');
                di = new DoInsert(leftIdentifier, rightIdentifier, level, hidden);
            }
            updates.add(new CommandMessage(uid, this.getOwnerID(), getName(), di));
        }
        
        return updates;
    }
    
    private boolean canAccess(String userId, int level) {
        return peers.containsKey(userId) && peers.get(userId) >= level;
    }
    
    @Override
    public Collection<CommandMessage> applyRemove(String userId, Set<String> identifiers) {
        
        Collection<CommandMessage> updates = new LinkedList<>();
        Set<String> toRemove = new HashSet<>();
        
        for (String id : identifiers) {
            int level = getLevelAtIdentifier(id);
            if (level > -1 && (canAccess(userId, level) || this.getOwnerID().equals(userId))) {
                doRemove(id);
                toRemove.add(id);
            }
        }
        
        if (toRemove.isEmpty()) {
            return updates;
        }
        
        for(Entry<String, Integer> entry : peers.entrySet()) {
            String uid = entry.getKey();
            if (uid.equals(this.getOwnerID())) {
                continue;
            }
            DoRemove dr = new DoRemove(toRemove);
            updates.add(new CommandMessage(uid, this.getOwnerID(), getName(), dr));
        }
        
        return updates;
    }
}
