/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package document;

import java.io.Serializable;

/**
 *
 * @author goggin
 */
public class DocumentValue implements Serializable{
    
    private String identifier;
    private String value;
    private int level;
    private DocumentValue next;
    private DocumentValue prev;
    
    public DocumentValue(String identifier, String value, int level, DocumentValue next, DocumentValue prev) {
        this.identifier = identifier;
        this.value = value;
        this.level = level;
        this.next = next;
        this.prev = prev;
    }
    
    public DocumentValue(String identifier, String value, int level) {
        this(identifier, value, level, null, null);
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public String getValue() {
        return value;
    }

    public DocumentValue getNext() {
        return next;
    }

    public void setNext(DocumentValue n) {
        this.next = n;
    }

    public DocumentValue getPrev() {
        return prev;
    }

    public void setPrev(DocumentValue p) {
        this.prev = p;
    }
    
    public void appendHere(DocumentValue dv) {
        dv.setNext(this.next);
        if (this.next != null) {
            this.next.setPrev(dv);
        }
        dv.setPrev(this);
        this.setNext(dv);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
}
