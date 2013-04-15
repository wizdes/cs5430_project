/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
public class Document implements DocumentInterface {
    
    public static final String BOF = "bof";
    public static final String EOF = "eof";
    
    private List<Integer> levels = new LinkedList<>();
    private DocumentValue bofDV = new DocumentValue(BOF, "", -1);
    private DocumentValue eofDV = new DocumentValue(EOF, "", -1);
    private Map<String, DocumentValue> valuesMap = new HashMap<>();
    private long uid = 0L;
    
    public Document() {
        bofDV.appendHere(eofDV);
        valuesMap.put(BOF, bofDV);
        valuesMap.put(EOF, eofDV);
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
    public int doInsert(int level, String leftIdentifier, String rightIdentifier, String text) {
        if (!levels.contains(level)) {
            return -1;
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
        DocumentValue dv = getDocumentValueAtIndex(index);
        return dv == null ? null : dv.getIdentifier();
    }
    
    private DocumentValue getDocumentValueAtIndex(int index) {
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
        return dv == null ? null : dv.getLevel();
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
}
