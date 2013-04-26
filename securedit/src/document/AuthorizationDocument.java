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
import security_layer.authentications.ServerAuthenticationPersistantState;

/**
 *
 */
public class AuthorizationDocument implements AuthorizationDocumentInterface {
    
    protected Map<String, Integer> peers = new HashMap<>();
    private Document document;
    private ServerAuthenticationPersistantState serverAuthenticationPersistantState;
    
    public AuthorizationDocument() {
        this("OwnerId", "Document");
    }
    
    public AuthorizationDocument(String ownerId, String name) {
        this.document = new Document(ownerId, name);
        this.serverAuthenticationPersistantState = new ServerAuthenticationPersistantState();
    }
    
    @Override
    public ServerAuthenticationPersistantState getServerAuthenticationPersistantState() {
        return this.serverAuthenticationPersistantState;
    }       
    
    @Override
    public void addUserToLevel(String userId, int level) {
        peers.put(userId, level);
        this.document.addLevel(level);
    }
    
    @Override
    public int getLevelForUser(String userId) {
        return peers.containsKey(userId) ? peers.get(userId) : -1;    
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
        
        this.document.doInsert(level, leftIdentifier, rightIdentifier, text);
        
        Collection<CommandMessage> updates = new LinkedList<>();
        
        for(Entry<String, Integer> entry : peers.entrySet()) {
            String uid = entry.getKey();
            if (uid.equals(this.document.getOwnerID())) {
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
            updates.add(new CommandMessage(uid, this.document.getOwnerID(), this.document.getName(), di));
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
            int level = this.document.getLevelAtIdentifier(id);
            if (level > -1 && (canAccess(userId, level) || this.document.getOwnerID().equals(userId))) {
                this.document.doRemove(id);
                toRemove.add(id);
            }
        }
        
        if (toRemove.isEmpty()) {
            return updates;
        }
        
        for(Entry<String, Integer> entry : peers.entrySet()) {
            String uid = entry.getKey();
            if (uid.equals(this.document.getOwnerID())) {
                continue;
            }
            DoRemove dr = new DoRemove(toRemove);
            updates.add(new CommandMessage(uid, this.document.getOwnerID(), this.document.getName(), dr));
        }
        
        return updates;
    }
    
    @Override
    public Document getDocument() {
        return document;
    }
    
    @Override
    public void setDocument(Document d) {
        this.document = d;
    }    
}
