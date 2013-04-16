/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

import application.encryption_demo.CommunicationInterface;
import application.encryption_demo.Messages.Message;
import java.util.Collection;
import java.util.Set;

/**
 *
 */
public class NetworkDocument extends AuthorizationDocument implements NetworkDocumentInterface {
    
    private CommunicationInterface communication;
    private String collaboratorId;
    
    public NetworkDocument(CommunicationInterface ci, 
                           String collaboratorId, 
                           String documentOwnerId, 
                           String documentName) {
        super(documentOwnerId, documentName);
        communication = ci;
        this.collaboratorId = collaboratorId;
    }
    
    private boolean isOwner() {
        return this.getOwnerID().equals(collaboratorId);
    }
    
    @Override
    public void requestInsert(int level, String left, String right, String text) {
        if (isOwner()) {
            this.addUserToLevel(this.getOwnerID(), level);
            requestInsertFor(this.getOwnerID(), level, left, right, text);
        } else {
            DoInsert di = new DoInsert(left, right, level, text);
            CommandMessage cm = new CommandMessage(this.getOwnerID(), this.collaboratorId, this.getName(), di);
            System.out.println(this.collaboratorId + " : " + cm);
            communication.sendMessage(cm.to, cm);
        }
    }

    private void requestInsertFor(String userId, int level, String left, String right, String text) {
        Collection<CommandMessage> updates = this.applyInsert(userId, level, left, right, text);
        if (updates != null) {
            for (CommandMessage m : updates) {
                System.out.println(this.collaboratorId + " : " + m);
                communication.sendMessage(m.to, m);
            }
        }
    }
    
    @Override
    public void requestRemove(Set<String> identsToRemove) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processMessage(CommandMessage m) {
        System.out.println(this.collaboratorId + " : processMessage");
        if (isOwner()) {
            processMessageOwner(m);
        } else {
            processMessageClient(m);
        }
    }
    
    private void processMessageOwner(CommandMessage m) {
        System.out.println(this.collaboratorId + " : processMessageOwner");
        if (m.command instanceof DoInsert) {
            DoInsert di = (DoInsert)m.command;
            requestInsertFor(m.from,
                             di.levelIdentifier, 
                             di.leftIdentifier, 
                             di.rightIdentifier, 
                             di.text);
        }
    }
    
    private void processMessageClient(CommandMessage m) {
        if (m.command instanceof DoInsert) {
            DoInsert di = (DoInsert)m.command;
            this.doInsert(di.levelIdentifier, 
                          di.leftIdentifier, 
                          di.rightIdentifier, 
                          di.text);
        }
    }    

    @Override
    public void requestInsert(int level, int left, int right, String text) {
        String leftIdent = this.getIdentifierAtIndex(left);
        String rightIdent = this.getIdentifierAtIndex(right);
        requestInsert(level, leftIdent, rightIdent, text);
    }

}
