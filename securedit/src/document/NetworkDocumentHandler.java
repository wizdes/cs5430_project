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
    private Document document;
    private String ownerId;
    private final Lock lock = new ReentrantLock(true);
    
    public static boolean autoApprove = false;
    public static boolean autoDeny = false;
    
    public NetworkDocumentHandler(CommunicationInterface ci, 
                           String collaboratorId, 
                           String documentOwnerId, 
                           String documentName) {
        this.authDocument = new AuthorizationDocument(documentOwnerId, documentName);
        this.document = this.authDocument.getDocument();
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
    public void addUserToLevel(String userId, int levelIdentifier){
        if (curDoc != null && !authDocument.peers.containsKey(userId)) {
            curDoc.addUser(userId, levelIdentifier);
        }
        if (curDoc != null) {
            curDoc.reviseUser(userId, levelIdentifier);
        }
        authDocument.addUserToLevel(userId, levelIdentifier);
        
        if (!userId.equals(this.collaboratorId)) {
            UpdateLevel ul = new UpdateLevel(userId, levelIdentifier);
            this.sendCommandMessage(userId, ul);            
        }
    }
    
    public void deleteUser(String userId){
        if(curDoc != null){
            curDoc.removeUser(userId);
        }
        authDocument.removeUser(userId);
        
        if(userId.equals(this.collaboratorId)){
            DeleteUser dl = new DeleteUser(userId);
            this.sendCommandMessage(this.getOwnerID(), dl);
        }
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
        DocumentValue v = document.valuesMap.get(leftIdentifier);
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
        String leftIdent = document.getIdentifierAtIndex(leftOffset);
        String rightIdent = document.getIdentifierAtIndex(rightOffset);
        if (curDoc != null) {
            curDoc.setColors(leftOffset, rightOffset, levelIdentifier);
        }
        return assignLevel(levelIdentifier, leftIdent, rightIdent);
    }
    
    @Override
    public boolean isOwner() {
        return document.getOwnerID().equals(collaboratorId);
    }
    
    @Override
    public void giveGUI(EditPanel cd){
        curDoc = cd;
    }
    
    @Override
    public void requestInsert(int level, String left, String right, String text) {
        
        if (isOwner()) {
            authDocument.addUserToLevel(document.getOwnerID(), level);
            requestInsertFor(document.getOwnerID(), level, left, right, text);
        } else {
            DoInsert di = new DoInsert(left, right, level, text);
            this.sendCommandMessage(document.getOwnerID(), di);
        }
    }

    private void requestInsertFor(String userId, int level, String left, String right, String text) {
        Collection<CommandMessage> updates = authDocument.applyInsert(userId, level, left, right, text);
        if (updates != null) {
            for (CommandMessage m : updates) {
                System.out.println(m);
                communication.sendMessage(m.to, document.getName(), m);
            }
        }
    }
    
    @Override
    public void requestRemove(Set<String> identsToRemove) {
        if (isOwner()) {
            requestRemoveFor(document.getOwnerID(), identsToRemove);
        } else {
            DoRemove dr = new DoRemove(identsToRemove);
            this.sendCommandMessage(document.getOwnerID(), dr);
        }        
    }
    
    @Override
    public void requestRemove(int left, int right) {
        Set<String> toRemove = new HashSet<>();
        for (int i = left; i <= right; i++) {
            toRemove.add(document.getIdentifierAtIndex(i));
        }
        requestRemove(toRemove);
    }    
        
    private void requestRemoveFor(String userId, Set<String> toRemove) {
        Collection<CommandMessage> updates = authDocument.applyRemove(userId, toRemove);
        if (updates != null) {
            for (CommandMessage m : updates) {
                System.out.println(m);
                communication.sendMessage(m.to, document.getName(), m);
            }
        }        
    }
    
    @Override
    public void processMessage(CommandMessage m) {
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
                curDoc.manualInsert(document.getOffsetForIdentifier(di.leftIdentifier) + 1, di.text, null);
            }
        } else if (m.command instanceof DoRemove) {
            DoRemove dr = (DoRemove)m.command;
            int smallestOffset = -1;
            for(String s:dr.identifiers){
                int candidate = document.getOffsetForIdentifier(s);
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
            BootstrapResponse resp = new BootstrapResponse(this.document.formatFor(level));
            this.sendCommandMessage(m.from, resp);
        } else if(m.command instanceof DeleteUser){
            this.deleteUser(((DeleteUser)m.command).getUserID());
        }
    }
    
    private void processMessageClient(CommandMessage m) {
        if (m.command instanceof DoInsert) {
            DoInsert di = (DoInsert)m.command;
            document.doInsert(di.levelIdentifier, 
                              di.leftIdentifier, 
                              di.rightIdentifier, 
                              di.text);
            if (curDoc != null) {
                curDoc.manualInsert(document.getOffsetForIdentifier(di.leftIdentifier) + 1, di.text, null);
            }
        }  else if (m.command instanceof DoRemove) {
            DoRemove dr = (DoRemove)m.command;
            int smallestOffset = -1;
            for(String s:dr.identifiers){
                int candidate = document.getOffsetForIdentifier(s);
                if(smallestOffset == -1 || smallestOffset > candidate){
                    smallestOffset = candidate;
                }
            }
            document.doRemove(dr.identifiers);
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
            this.document = resp.document;
            this.authDocument.setDocument(document);
            if (curDoc != null) {
                curDoc.handleBootstrap(this.authDocument);
            }
        } else if(m.command instanceof DeleteUser){
            this.deleteUser(((DeleteUser)m.command).getUserID());
        }

    }    

    @Override
    public void requestInsert(int level, int left, int right, String text) {
        String leftIdent = document.getIdentifierAtIndex(left);
        String rightIdent = document.getIdentifierAtIndex(right);
        requestInsert(level, leftIdent, rightIdent, text);
    }
    
    private void sendCommandMessage(String userId, DocumentCommand dc) {
        CommandMessage cm = new CommandMessage(userId, this.collaboratorId, document.getName(), dc);
        System.out.println(cm);
        this.communication.sendMessage(userId, document.getName(), cm);
    }

    @Override
    public String getUserID() {
        return collaboratorId;
    }

    @Override
    public String getIdentifierAtIndex(int index) {
        return this.document.getIdentifierAtIndex(index);
    }

    @Override
    public boolean isEmpty() {
        return this.document.isEmpty();
    }

    @Override
    public int getLevelAtIndex(int index) {
        return this.document.getLevelAtIndex(index);
    }

    @Override
    public String getName() {
        return this.document.getName();
    }

    @Override
    public String getOwnerID() {
        return this.document.getOwnerID();
    }

    @Override
    public String getString() {
        return this.document.getString();
    }
    
    @Override
    public AuthorizationDocument getAuthDocument() {
        return this.authDocument;
    }
    
    @Override
    public void setAuthDocument(AuthorizationDocument ad) {
        this.authDocument = ad;
        this.document = this.authDocument.getDocument();
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
        this.document.colors.add(c);
    }
    
    @Override
    public void addLabel(String l){
        this.document.labels.add(l);
    }
    
    @Override
    public ArrayList<String> getLabels(){
        return this.document.labels;
    }
    
    @Override
    public ArrayList<Color> getColors() {
        return this.document.colors;
    }
}
