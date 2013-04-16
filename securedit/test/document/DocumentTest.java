/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package document;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author goggin
 */
public class DocumentTest {
    
    private Document document;

    @Test
    public void testAssignLevel() {
        boolean r;
        document = new Document();
        document.addLevel(0);
        document.addLevel(1);
        document.doInsert(0, Document.BOF, Document.EOF, "000111000");
        String iLeft = document.getIdentifierAtIndex(3);
        String iRight = document.getIdentifierAtIndex(5);
        
        // it should return false if the left identifier doesn't exist
        r = document.assignLevel(1, iLeft, "non-existant");
        assertFalse(r);
        
        // it should return false if the right identifier doesn't exist
        r = document.assignLevel(1, "non-existant", iRight);
        assertFalse(r);
        
        // it should return false if the level doesn't exist
        r = document.assignLevel(15, iLeft, iRight);
        assertFalse(r);
        
        // it should return true if both identifiers are present
        r = document.assignLevel(1, iLeft, iRight);
        assertTrue(r);
        
        DocumentValue head = document.getValues().getNext();
        while (!head.getIdentifier().equals(Document.EOF)) {
            assertEquals(head.getValue(), head.getLevel() + "");
            head = head.getNext();
        }
    }

    @Test
    public void testDoInsert() {
        int r;
        document = new Document();        
        document.addLevel(0);
        
        // it should return -1 if neither identifiers exist
        r = document.doInsert(0, "Non", "existing", "Hello World");
        assertEquals(-1, r);
        
        // it should insert after the left identifier if the left identifier is present
        r = document.doInsert(0, Document.BOF, Document.EOF, "Hello World");
        assertEquals(0, r);
        assertEquals("Hello World", document.getString());
        String iLeft = document.getIdentifierAtIndex(1);
        String iRight = document.getIdentifierAtIndex(2);
        
        r = document.doInsert(0, iLeft, iRight, "7");
        assertEquals(0, r);
        assertEquals("He7llo World", document.getString());
        
        r = document.doInsert(0, "doesn't exist", iRight, "7");
        assertEquals(0, r);
        assertEquals("He77llo World", document.getString());
        
        document.addLevel(1);
        r = document.doInsert(1, "doesn't exist", Document.EOF, "7");
        assertEquals(1, r);
        assertEquals("He77llo World7", document.getString());
        
        for (int i = 0; i < "He77llo World".length(); i++) {
            assertEquals(0, document.getLevelAtIndex(i));
        }
        assertEquals(1, document.getLevelAtIndex("He77llo World".length()));
    }

    @Test
    public void testDoRemove() {
        document = new Document();
        document.addLevel(0);
        document.doInsert(0, Document.BOF, Document.EOF, "Hello");
        
        Set<String> toRemove = new HashSet<>();
        toRemove.add(document.getIdentifierAtIndex(0));
        toRemove.add(document.getIdentifierAtIndex(2));
        toRemove.add(document.getIdentifierAtIndex(4));
        
        document.doRemove(toRemove);
        assertEquals("el", document.getString());
    }
}
