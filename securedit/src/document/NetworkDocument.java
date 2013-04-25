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
import javax.swing.JOptionPane;

/**
 *
 */
public class NetworkDocument extends AuthorizationDocument implements NetworkDocumentInterface {
    
    private CommunicationInterface communication;
    private String collaboratorId;
    private EditPanel curDoc;
    
    public static boolean autoApprove = false;
    public static boolean autoDeny = false;
    
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
        if (curDoc != null && !peers.containsKey(userId)) {
            curDoc.addUser(userId, levelIdentifier);
        }
        curDoc.reviseUser(userId, levelIdentifier);
        super.addUserToLevel(userId, levelIdentifier);
        UpdateLevel ul = new UpdateLevel(userId, levelIdentifier);
        this.sendCommandMessage(userId, ul);
    }
    
    @Override
    public void requestChangeLevel(int level) {
        if (this.isOwner()) {
            super.addUserToLevel(this.getOwnerID(), level);
        } else {
            RequestLevel rl = new RequestLevel(level);
            sendCommandMessage(this.getOwnerID(), rl);
        }
    }
    
    @Override
    public boolean assignLevel(int levelIdentifier, String leftIdentifier, String rightIdentifier) {
        // remove the nodes and save up the values
        DocumentValue v = this.valuesMap.get(leftIdentifier);
        if (v == null) {
            return false;
        }
        
        Set<String> toRemove = new HashSet<>();
        String removedText = "";
        DocumentValue leftBoundary = v.getPrev();
        boolean completed = false;
        
        while (v != null && !completed) {
            toRemove.add(v.getIdentifier());
            removedText += v.getValue();
            completed = v.getIdentifier().equals(rightIdentifier);
            v = v.getNext();
        }
        
        DocumentValue rightBoundary = v != null ? v.getNext() : null;
        String rightIdent = rightBoundary == null ? Document.EOF : rightBoundary.getIdentifier();
        String leftIdent = leftBoundary == null ? Document.BOF : leftBoundary.getIdentifier();

        requestRemove(toRemove);
        requestInsert(levelIdentifier, leftIdent, rightIdent, removedText);
        return true;
    }
    
    @Override
    public boolean assignLevel(int levelIdentifier, int leftOffset, int rightOffset) {
        String leftIdent = this.getIdentifierAtIndex(leftOffset);
        String rightIdent = this.getIdentifierAtIndex(rightOffset);
        if (curDoc != null) {
            curDoc.setColors(leftOffset, rightOffset, levelIdentifier);
        }
        return assignLevel(levelIdentifier, leftIdent, rightIdent);
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
            this.sendCommandMessage(this.getOwnerID(), di);
        }
    }

    private void requestInsertFor(String userId, int level, String left, String right, String text) {
        Collection<CommandMessage> updates = this.applyInsert(userId, level, left, right, text);
        if (updates != null) {
            for (CommandMessage m : updates) {
                communication.sendMessage(m.to, this.getName(), m);
            }
        }
    }
    
    @Override
    public void requestRemove(Set<String> identsToRemove) {
        if (isOwner()) {
            requestRemoveFor(this.getOwnerID(), identsToRemove);
        } else {
            DoRemove dr = new DoRemove(identsToRemove);
            this.sendCommandMessage(this.getOwnerID(), dr);
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
                communication.sendMessage(m.to, this.getName(), m);
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
            if (curDoc != null) {
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
        } else if (m.command instanceof RequestLevel) {
            RequestLevel rl = (RequestLevel)m.command;
            boolean approved = autoApprove || 
                               (!autoDeny
                                &&
                                curDoc.approveUserForLevel(m.from, rl.getLevel()));
            
            if (approved) {
                this.addUserToLevel(m.from, rl.getLevel());
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
            if (curDoc != null) {
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
            if (curDoc != null) {
                curDoc.manualRemove(smallestOffset, dr.identifiers.size());
            }
        } else if (m.command instanceof UpdateLevel) {
            UpdateLevel update = (UpdateLevel)m.command;
            super.addUserToLevel(update.getUserId(), update.getLevel());
            if (curDoc != null) {
                curDoc.addUser(update.getUserId(), update.getLevel());
            }            
        }
    }    

    @Override
    public void requestInsert(int level, int left, int right, String text) {
        String leftIdent = this.getIdentifierAtIndex(left);
        String rightIdent = this.getIdentifierAtIndex(right);
        requestInsert(level, leftIdent, rightIdent, text);
    }
    
    private void sendCommandMessage(String userId, DocumentCommand dc) {
        CommandMessage cm = new CommandMessage(userId, this.collaboratorId, this.getName(), dc);
        System.out.println(cm);
        this.communication.sendMessage(userId, this.getName(), cm);
    }

    @Override
    public String getUserID() {
        return collaboratorId;
    }
    
}
