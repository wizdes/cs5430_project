/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

import application.encryption_demo.CommunicationInterface;
import java.util.Set;

/**
 *
 */
public class NetworkDocument extends AuthorizationDocument implements NetworkDocumentInterface {

    public NetworkDocument(CommunicationInterface ci, String ownerId, String documentName) {
        super(ownerId, documentName);
    }
    
    @Override
    public void requestInsert(int level, String left, String right, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void requestRemove(Set<String> identsToRemove) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processMessage(CommandMessage m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
