/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package document;

import application.encryption_demo.CommunicationInterface;
import application.encryption_demo.forms.EditPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import security_layer.authentications.ServerAuthenticationPersistantState;

/**
 *
 */
public class NetworkDocumentHandler implements NetworkDocumentHandlerInterface {
    
    private CommunicationInterface communication;
    private String collaboratorId;
    private EditPanel curDoc;
    private AuthorizationDocument authDocument;
    private String ownerId;
    private final Lock lock = new ReentrantLock(true);
    private boolean connected = true;

    
    public static boolean autoApprove = false;
    public static boolean autoDeny = false;
    
    public NetworkDocumentHandler(CommunicationInterface ci, 
                           String collaboratorId, 
                           String documentOwnerId, 
                           String documentName) {
        this.authDocument = new AuthorizationDocument(documentOwnerId, documentName);
        communication = ci;
        this.ownerId = documentOwnerId;
        this.collaboratorId = collaboratorId;
    }
    
    @Override
    public void lock(){
        lock.lock();
    }
    
    @Override
    public void unlock(){
        lock.unlock();
    }
    
    @Override
    public void addUserToLevel(String userId, int newLevel){
        if (curDoc != null && !authDocument.peers.containsKey(userId)) {
            curDoc.addUser(userId, newLevel);
        }
        
        int previousLevel = authDocument.getLevelForUser(userId);
        boolean levelChanged = previousLevel > -1 && previousLevel != newLevel;
        boolean levelIncreased = previousLevel > newLevel;

        if (levelChanged && curDoc != null) {
            curDoc.reviseUser(userId, newLevel);
        }
        
        authDocument.addUserToLevel(userId, newLevel);
        
        if (!userId.equals(this.collaboratorId)) {
            UpdateLevel ul = new UpdateLevel(userId, newLevel);
            this.sendCommandMessage(userId, ul);            
        }
     
        if (levelChanged && this.isOwner() && !userId.equals(this.collaboratorId)) {
            Document d = this.authDocument.getDocument();

            DocumentValue iter = d.bofDV.getNext();
            while (!iter.getIdentifier().equals(Document.EOF)) {
                int level = iter.getLevel();
                DoReplace dr = null;
                if (previousLevel >= level && newLevel < level) {
                    dr = new DoReplace(iter.getIdentifier(), "X");
                } else if (previousLevel < level && newLevel >= level) {
                    dr = new DoReplace(iter.getIdentifier(), iter.getValue());
                }
                iter = iter.getNext();
                if (dr != null) {
                    this.sendCommandMessage(userId, dr);
                }
            }
        }
    }
    
    public void deleteUser(String userId){
        if (curDoc != null){
            curDoc.removeUser(userId);
        }
        authDocument.removeUser(userId);
    }
    
    @Override
    public void requestChangeLevel(int level) {
        if (this.isOwner()) {
            authDocument.addUserToLevel(ownerId, level);
        } else {
            RequestLevel rl = new RequestLevel(level);
            sendCommandMessage(ownerId, rl);
        }
    }
    
    @Override
    public boolean assignLevel(int levelIdentifier, String leftIdentifier, String rightIdentifier) {
        // remove the nodes and save up the values
        DocumentValue v = authDocument.getDocument().valuesMap.get(leftIdentifier);
        if (v == null) {
            return false;
        }
        
        Set<String> toRemove = new HashSet<>();
        String removedText = "";
        DocumentValue leftBoundary = v.getPrev();
        boolean completed = false;
        
        while (v != null && !completed) {
            toRemove.add(v.getIdentifier());
            
            removedText = v.getValue();
            DocumentValue rightBoundary = v != null ? v.getNext() : null;
            String rightIdent = rightBoundary == null ? Document.EOF : rightBoundary.getIdentifier();
            String leftIdent = leftBoundary == null ? Document.BOF : leftBoundary.getIdentifier();

            requestRemove(toRemove);
            requestInsert(levelIdentifier, leftIdent, rightIdent, removedText);            
            
            completed = v.getIdentifier().equals(rightIdentifier);
            v = v.getNext();
        }
        
        return true;
    }
    
    @Override
    public boolean assignLevel(int levelIdentifier, int leftOffset, int rightOffset) {
        String leftIdent = authDocument.getDocument().getIdentifierAtIndex(leftOffset);
        String rightIdent = authDocument.getDocument().getIdentifierAtIndex(rightOffset);
        if (curDoc != null) {
            curDoc.setColors(leftOffset, rightOffset - leftOffset + 1, levelIdentifier);
        }
        return assignLevel(levelIdentifier, leftIdent, rightIdent);
    }
    
    @Override
    public boolean isOwner() {
        return authDocument.getDocument().getOwnerID().equals(collaboratorId);
    }
    
    @Override
    public void giveGUI(EditPanel cd){
        curDoc = cd;
    }
    
    @Override
    public void requestInsert(int level, String left, String right, String text) {
        
        if (isOwner()) {
            authDocument.addUserToLevel(authDocument.getDocument().getOwnerID(), level);
            requestInsertFor(authDocument.getDocument().getOwnerID(), level, left, right, text);
        } else {
            DoInsert di = new DoInsert(left, right, level, text);
            this.sendCommandMessage(authDocument.getDocument().getOwnerID(), di);
        }
    }

    private void requestInsertFor(String userId, int level, String left, String right, String text) {
        Collection<CommandMessage> updates = authDocument.applyInsert(userId, level, left, right, text);
        if (updates != null) {
            for (CommandMessage m : updates) {
                System.out.println(m);
                communication.sendMessage(m.to, authDocument.getDocument().getName(), m);
            }
        }
    }
    
    @Override
    public void requestRemove(Set<String> identsToRemove) {
        if (isOwner()) {
            requestRemoveFor(authDocument.getDocument().getOwnerID(), identsToRemove);
        } else {
            DoRemove dr = new DoRemove(identsToRemove);
            this.sendCommandMessage(authDocument.getDocument().getOwnerID(), dr);
        }        
    }
    
    @Override
    public void requestRemove(int left, int right) {
        Set<String> toRemove = new HashSet<>();
        for (int i = left; i <= right; i++) {
            toRemove.add(authDocument.getDocument().getIdentifierAtIndex(i));
        }
        requestRemove(toRemove);
    }    
        
    private void requestRemoveFor(String userId, Set<String> toRemove) {
        Collection<CommandMessage> updates = authDocument.applyRemove(userId, toRemove);
        if (updates != null) {
            for (CommandMessage m : updates) {
                System.out.println(m);
                communication.sendMessage(m.to, authDocument.getDocument().getName(), m);
            }
        }        
    }
    
    @Override
    public void processMessage(CommandMessage m) {
        System.out.println("processMessage : " + m);
        if (!this.isConnected()) {
            System.out.println("\tdisconnected");
            return;
        }
        lock();
        try{
        if (isOwner()) {
            processMessageOwner(m);
        } else {
            processMessageClient(m);
        }
        }finally{
            unlock();
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
                curDoc.manualInsert(authDocument.getDocument().getOffsetForIdentifier(di.leftIdentifier) + 1, di.text, null, true); 
            }
        } else if (m.command instanceof DoRemove) {
            DoRemove dr = (DoRemove)m.command;
            int smallestOffset = -1;
            for(String s:dr.identifiers){
                int candidate = authDocument.getDocument().getOffsetForIdentifier(s);
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
        } else if (m.command instanceof BootstrapRequest) {
            BootstrapRequest req = (BootstrapRequest)m.command;
            int level = this.authDocument.getLevelForUser(m.from);
            if (level == -1) {
                level = 0;
                this.authDocument.addUserToLevel(m.from, level);
                if (this.curDoc != null) {
                    curDoc.addUser(m.from, level);
                }
            }
            BootstrapResponse resp = new BootstrapResponse(this.authDocument.getDocument().formatFor(level));
            this.sendCommandMessage(m.from, resp);
        } else if(m.command instanceof DeleteUser){
            String userId = ((DeleteUser)m.command).getUserID();
            this.deleteUser(userId);
        }
    }
    
    private void processMessageClient(CommandMessage m) {
        if (m.command instanceof DoInsert) {
            DoInsert di = (DoInsert)m.command;
            int r = authDocument.getDocument().doInsert(di.levelIdentifier, 
                                                        di.leftIdentifier, 
                                                        di.rightIdentifier, 
                                                        di.text);
            if (r == -1) {
                System.out.println("ERROR INSERTING INTO DOCUMENT");
            } else {
                System.out.println("Inserted item " + di.text);
                System.out.println(authDocument.getDocument().getString());
            }
            if (curDoc != null) {
                curDoc.manualInsert(authDocument.getDocument().getOffsetForIdentifier(di.leftIdentifier) + 1, di.text, null,true);
            }
        }  else if (m.command instanceof DoRemove) {
            DoRemove dr = (DoRemove)m.command;
            int smallestOffset = -1;
            for(String s:dr.identifiers){
                int candidate = authDocument.getDocument().getOffsetForIdentifier(s);
                if(smallestOffset == -1 || smallestOffset > candidate){
                    smallestOffset = candidate;
                }
            }
            authDocument.getDocument().doRemove(dr.identifiers);
            if (curDoc != null) {
                curDoc.manualRemove(smallestOffset, dr.identifiers.size());
            }
        } else if (m.command instanceof UpdateLevel) {
            UpdateLevel update = (UpdateLevel)m.command;
            authDocument.addUserToLevel(update.getUserId(), update.getLevel());
            if (curDoc != null) {
                curDoc.addUser(update.getUserId(), update.getLevel());
            }            
        }  else if (m.command instanceof BootstrapResponse) {
            BootstrapResponse resp = (BootstrapResponse)m.command;
            this.authDocument.setDocument(resp.document);
            if (curDoc != null) {
                curDoc.handleBootstrap(this.authDocument);
            }
        } else if(m.command instanceof DeleteUser){
            String userId = ((DeleteUser)m.command).getUserID();
            this.deleteUser(userId);
            if (userId.equals(this.getOwnerID())) {
                this.disconnect();
            }
        } else if(m.command instanceof DoReplace) {
            DoReplace dr = ((DoReplace)m.command);
            Document document = this.authDocument.getDocument();
            DocumentValue dv = document.valuesMap.get(dr.identifier);
            dv.setValue(dr.newValue);
            if (curDoc != null) {
                int offset = document.getOffsetForIdentifier(dr.identifier);
                int level = document.getLevelAtIdentifier(dr.identifier);
                curDoc.manualReplace(offset, 1, dr.newValue, level);
            }
        }
    }    

    @Override
    public void requestInsert(int level, int left, int right, String text) {
        String leftIdent = authDocument.getDocument().getIdentifierAtIndex(left);
        String rightIdent = authDocument.getDocument().getIdentifierAtIndex(right);
        requestInsert(level, leftIdent, rightIdent, text);
    }
    
    private void sendCommandMessage(String userId, DocumentCommand dc) {
        CommandMessage cm = new CommandMessage(userId, this.collaboratorId, authDocument.getDocument().getName(), dc);
        System.out.println(cm);
        this.communication.sendMessage(userId, authDocument.getDocument().getName(), cm);
    }

    @Override
    public String getUserID() {
        return collaboratorId;
    }

    @Override
    public String getIdentifierAtIndex(int index) {
        return this.authDocument.getDocument().getIdentifierAtIndex(index);
    }

    @Override
    public boolean isEmpty() {
        return this.authDocument.getDocument().isEmpty();
    }

    @Override
    public int getLevelAtIndex(int index) {
        return this.authDocument.getDocument().getLevelAtIndex(index);
    }

    @Override
    public String getName() {
        return this.authDocument.getDocument().getName();
    }

    @Override
    public String getOwnerID() {
        return this.authDocument.getDocument().getOwnerID();
    }

    @Override
    public String getString() {
        return this.authDocument.getDocument().getString();
    }
    
    @Override
    public AuthorizationDocument getAuthDocument() {
        return this.authDocument;
    }
    
    @Override
    public void setAuthDocument(AuthorizationDocument ad) {
        this.authDocument = ad;
    }

    @Override
    public ServerAuthenticationPersistantState getServerAuthenticationPersistantState() {
        return this.authDocument.getServerAuthenticationPersistantState();
    }

    @Override
    public int getLevelForUser(String userId) {
        return this.authDocument.getLevelForUser(userId);
    }
    
    @Override
    public void bootstrap() {
        this.sendCommandMessage(ownerId, new BootstrapRequest());
    }
    
    @Override
    public void addColor(Color c) {
        this.authDocument.getDocument().colors.add(c);
    }
    
    @Override
    public void addLabel(String l){
        this.authDocument.getDocument().labels.add(l);
    }
    
    @Override
    public ArrayList<String> getLabels(){
        return this.authDocument.getDocument().labels;
    }
    
    @Override
    public ArrayList<Color> getColors() {
        return this.authDocument.getDocument().colors;
    }

    @Override
    public void disconnect() {
        if (!isConnected()) {
            return;
        }
        
        this.connected = false;
        
        this.deleteUser(this.collaboratorId);
        
        DeleteUser dl = new DeleteUser(this.collaboratorId);
        
        if (this.isOwner()) {
            for (String userId : this.authDocument.peers.keySet()) {
                this.sendCommandMessage(userId, dl);
            }
        } else {
            this.sendCommandMessage(this.getOwnerID(), dl);
        }
        
        if (curDoc != null) {
            curDoc.endEditingSession();
        }                
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }
}
