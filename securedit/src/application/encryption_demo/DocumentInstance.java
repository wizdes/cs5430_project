/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Patrick
 */
public class DocumentInstance {
    public String ownerID;
    public String docName;
    
    public final ConcurrentMap<String, Object> collaborators = new ConcurrentHashMap<>(); //<ident, whatever>

    public DocumentInstance(String ownerID, String docName) {
        this.ownerID = ownerID;
        this.docName = docName;
        collaborators.put(ownerID, true);
    }
    public void addCollaborators(String collaboratorID){
        collaborators.put(collaboratorID, true);
    }
    public String getDocumentIdentifer(){
        return toDocumentIdentifier(ownerID, docName);
    }
    public static String toDocumentIdentifier(String ownerID, String docName){
        return ownerID + "," + docName;
    }
}
