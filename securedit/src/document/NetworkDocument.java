/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

import application.encryption_demo.CommunicationInterface;
import application.encryption_demo.Messages.Message;
import application.encryption_demo.forms.EditPanel;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class NetworkDocument extends AuthorizationDocument implements NetworkDocumentInterface {
    
    private CommunicationInterface communication;
    private String collaboratorId;
    private EditPanel curDoc;
    
    public NetworkDocument(CommunicationInterface ci, 
                           String collaboratorId, 
                           String documentOwnerId, 
                           String documentName) {
        super(documentOwnerId, documentName);
        communication = ci;
        this.collaboratorId = collaboratorId;
    }
    
    @Override
    public void addUserToLevel(String userId, int levelIdentifier){
        curDoc.addUser(userId, levelIdentifier);
        super.addUserToLevel(userId, levelIdentifier);
    }
    
    @Override
    public boolean isOwner() {
        return this.getOwnerID().equals(collaboratorId);
    }
    
    @Override
    public void giveGUI(EditPanel cd){
        curDoc = cd;
    }
    
    @Override
    public void requestInsert(int level, String left, String right, String text) {
        if (isOwner()) {
            this.addUserToLevel(this.getOwnerID(), level);
            requestInsertFor(this.getOwnerID(), level, left, right, text);
        } else {
            DoInsert di = new DoInsert(left, right, level, text);
            CommandMessage cm = new CommandMessage(this.getOwnerID(), this.collaboratorId, this.getName(), di);
//            System.out.println(this.collaboratorId + " : " + cm);
            communication.sendMessage(cm.to, cm);
        }
    }

    private void requestInsertFor(String userId, int level, String left, String right, String text) {
        Collection<CommandMessage> updates = this.applyInsert(userId, level, left, right, text);
        if (updates != null) {
            for (CommandMessage m : updates) {
//                System.out.println(this.collaboratorId + " : " + m);
                communication.sendMessage(m.to, m);
            }
        }
    }
    
    @Override
    public void requestRemove(Set<String> identsToRemove) {
        if (isOwner()) {
            requestRemoveFor(this.getOwnerID(), identsToRemove);
        } else {
            DoRemove dr = new DoRemove(identsToRemove);
            CommandMessage cm = new CommandMessage(this.getOwnerID(), this.collaboratorId, this.getName(), dr);
            System.out.println(this.collaboratorId + " : " + cm);
            communication.sendMessage(cm.to, cm);
        }        
    }
    
    @Override
    public void requestRemove(int left, int right) {
        Set<String> toRemove = new HashSet<>();
        for (int i = left; i <= right; i++) {
            toRemove.add(getIdentifierAtIndex(i));
        }
        requestRemove(toRemove);
    }    
    
    private void requestRemoveFor(String userId, Set<String> toRemove) {
        Collection<CommandMessage> updates = this.applyRemove(userId, toRemove);
        if (updates != null) {
            for (CommandMessage m : updates) {
                communication.sendMessage(m.to, m);
            }
        }        
    }
    
    @Override
    public void processMessage(CommandMessage m) {
        if (isOwner()) {
            processMessageOwner(m);
        } else {
            processMessageClient(m);
        }
    }
    
    private void processMessageOwner(CommandMessage m) {
        if (m.command instanceof DoInsert) {
            DoInsert di = (DoInsert)m.command;
            requestInsertFor(m.from,
                             di.levelIdentifier, 
                             di.leftIdentifier, 
                             di.rightIdentifier, 
                             di.text);
            if(curDoc != null) {
                curDoc.manualInsert(this.getOffsetForIdentifier(di.leftIdentifier) + 1, di.text, null);
            }
        } else if (m.command instanceof DoRemove) {
            DoRemove dr = (DoRemove)m.command;
            int smallestOffset = -1;
            for(String s:dr.identifiers){
                int candidate = this.getOffsetForIdentifier(s);
                if(smallestOffset == -1 || smallestOffset > candidate){
                    smallestOffset = candidate;
                }
            }
            requestRemoveFor(m.from, dr.identifiers);
            if(curDoc != null) {
                curDoc.manualRemove(smallestOffset, dr.identifiers.size());
            }
        }
    }
    
    private void processMessageClient(CommandMessage m) {
        if (m.command instanceof DoInsert) {
            DoInsert di = (DoInsert)m.command;
            this.doInsert(di.levelIdentifier, 
                          di.leftIdentifier, 
                          di.rightIdentifier, 
                          di.text);
            if(curDoc != null) {
                curDoc.manualInsert(this.getOffsetForIdentifier(di.leftIdentifier) + 1, di.text, null);
            }
        }  else if (m.command instanceof DoRemove) {
            DoRemove dr = (DoRemove)m.command;
            int smallestOffset = -1;
            for(String s:dr.identifiers){
                int candidate = this.getOffsetForIdentifier(s);
                if(smallestOffset == -1 || smallestOffset > candidate){
                    smallestOffset = candidate;
                }
            }
            this.doRemove(dr.identifiers);
            if(curDoc != null) {
                curDoc.manualRemove(smallestOffset, dr.identifiers.size());
            }
        }
    }    

    @Override
    public void requestInsert(int level, int left, int right, String text) {
        String leftIdent = this.getIdentifierAtIndex(left);
        String rightIdent = this.getIdentifierAtIndex(right);
        requestInsert(level, leftIdent, rightIdent, text);
    }

}
