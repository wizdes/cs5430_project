/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

import application.encryption_demo.Messages.Message;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import security_layer.authentications.ServerAuthenticationPersistantState;

/**
 *
 */
public class Document implements DocumentInterface, Message {
    
    public static final String BOF = "bof";
    public static final String EOF = "eof";
    
    protected List<Integer> levels = new LinkedList<>();
    protected DocumentValue bofDV = new DocumentValue(BOF, "", -1);
    protected DocumentValue eofDV = new DocumentValue(EOF, "", -1);
    Map<String, DocumentValue> valuesMap = new HashMap<>();
    private long uid = 0L;
    private String name;
    private String ownerId;
    private ServerAuthenticationPersistantState serverAuthenticationPersistantState;
    
    public Document() {
        this("OwnerId", "Document");
    }
    
    public Document(String ownerId, String name) {
        bofDV.appendHere(eofDV);
        valuesMap.put(BOF, bofDV);
        valuesMap.put(EOF, eofDV);
        this.name = name;
        this.ownerId = ownerId;
        this.serverAuthenticationPersistantState = new ServerAuthenticationPersistantState();
    }
    
    @Override
    public ServerAuthenticationPersistantState getServerAuthenticationPersistantState() {
        return this.serverAuthenticationPersistantState;
    }    
    
    @Override
    public void addLevel(int levelIdentifier) {
        levels.add(levelIdentifier);
    }

    @Override
    public boolean assignLevel(int level, String leftIdentifier, String rightIdentifier) {
        if (!levels.contains(level)
             || !valuesMap.containsKey(leftIdentifier)
             || !valuesMap.containsKey(rightIdentifier)) {
            return false;
        }
        
        DocumentValue position = valuesMap.get(leftIdentifier);
        position.setLevel(level);
        do {
            position = position.getNext();
            position.setLevel(level);
        } while (position != null && !position.getIdentifier().equals(rightIdentifier));
        
        return true;
    }
    
    @Override
    public boolean assignLevel(int level, int leftOffset, int rightOffset) {
        String leftIdent = this.getIdentifierAtIndex(leftOffset);
        String rightIdent = this.getIdentifierAtIndex(rightOffset);
        return assignLevel(level, leftIdent, rightIdent);
    }
    
    @Override
    public int doInsert(int level, String leftIdentifier, String rightIdentifier, String text) {
        if (!levels.contains(level)) {
            levels.add(level);
        }
        
        DocumentValue position;
        if (valuesMap.containsKey(leftIdentifier)) {
            position = valuesMap.get(leftIdentifier);                    
        } else if (valuesMap.containsKey(rightIdentifier)) {
            position = valuesMap.get(rightIdentifier).getPrev();
        } else {
            return -1;
        }
            
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String identiifer = uid++ + "";
            DocumentValue dv = new DocumentValue(identiifer, c + "", level);
            valuesMap.put(identiifer, dv);
            position.appendHere(dv);
            position = dv;
        }

        return level;
    }
    
    @Override
    public String getIdentifierAtIndex(int index) {
        if (index == -1) {
            return Document.BOF;
        } else if (index >= valuesMap.size()) {
            return Document.EOF;
        }
        DocumentValue dv = getDocumentValueAtIndex(index);
        return dv == null ? null : dv.getIdentifier();
    }
    
    protected DocumentValue getDocumentValueAtIndex(int index) {
        DocumentValue dv = bofDV;
        for (int i = 0; i <= index; i++) {
            dv = dv.getNext();
            if (dv == null) {
                return null;
            }
        }
            
        return dv;
    }

    @Override
    public void doRemove(Set<String> identifiers) {
        for (String i : identifiers) {
            doRemove(i);
        }
    }
    
    @Override
    public void doRemove(String identifier) {
        DocumentValue dv = valuesMap.get(identifier);
        if (dv == null) {
            return;
        }
        
        DocumentValue prev = dv.getPrev();
        DocumentValue next = dv.getNext();

        prev.setNext(next);
        next.setPrev(prev);

        valuesMap.remove(identifier);
    }

    @Override
    public DocumentValue getValues() {
        return bofDV;
    }
    
    @Override
    public int getLevelAtIndex(int index) {
        DocumentValue dv = getDocumentValueAtIndex(index);
        return dv == null ? -1 : dv.getLevel();
    }
    
    @Override
    public int getLevelAtIdentifier(String id) {
        DocumentValue dv = valuesMap.get(id);
        return dv == null ? -1 : dv.getLevel();
    }    
    
    @Override
    public String getString() {
        DocumentValue dv = bofDV;
        String r = "";
        while (dv != null) {
            r += dv.getValue();
            dv = dv.getNext();
        }
        return r;
    }    

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getOwnerID() {
        return ownerId;
    }

    @Override
    public void setOwnerID(String name) {
        this.ownerId = name;
    }

    @Override
    public int getOffsetForIdentifier(String ident) {
        int offset = -1;
        DocumentValue dv = bofDV;
        while (dv != null && !dv.getIdentifier().equals(ident)) {
            offset++;
            dv = dv.getNext();
        }
        return offset;
    }
    
    @Override
    public boolean isEmpty() {
        return bofDV.getNext().getIdentifier().equals(Document.EOF);
    }
}
