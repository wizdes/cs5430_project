/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package document;

import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author goggin
 */
public class AuthorizationDocumentTest {
    
    @Test
    public void testApplyInsert() {
        boolean r;
        AuthorizationDocument document = new AuthorizationDocument();
        document.addUserToLevel("matt", 0);
        document.addUserToLevel("yi", 1);
        document.addUserToLevel("patrick", 2);
        
        Collection<CommandMessage> messages = document.applyInsert("matt", 0, Document.BOF, Document.EOF, "hello world");
        assertEquals(2, messages.size());
        for (CommandMessage cm : messages) {
            DoInsert di = (DoInsert)cm.command;
            assertEquals("hello world", di.text);
        }
        
        messages = document.applyInsert("patrick", 0, Document.BOF, Document.EOF, "hello world");
        assertEquals(2, messages.size());
        for (CommandMessage cm : messages) {
            DoInsert di = (DoInsert)cm.command;
            assertEquals("hello world", di.text);
        }
        
        messages = document.applyInsert("patrick", 2, Document.BOF, Document.EOF, "hello world");
        assertEquals(2, messages.size());
        for (CommandMessage cm : messages) {
            DoInsert di = (DoInsert)cm.command;
            assertEquals("XXXXXXXXXXX", di.text);
        }        
    }

    @Test
    public void testApplyRemove() {
    }

}
