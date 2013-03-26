/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Patrick
 */
public class Peers {
    private ConcurrentMap<String, Peer> peers = new ConcurrentHashMap<>();
    
    public void addPeer(String id, String ip, int port, List<String> documents, boolean needsHumanAuth){
        if(!peers.containsKey(id)){
            peers.put(id, new Peer(id, ip, port, documents, needsHumanAuth));
        } else {
            Peer peer = peers.get(id);
            peer.addDocuments(documents);
        }
    }
    public void removePeer(String id){
        peers.remove(id);
    }
    public ConcurrentMap<String, Peer> getPeers(){
        return peers;
    }
    public Peer getPeer(String id){
        return peers.get(id);
    }
    
    public class Peer {
        public String id;
        public String ip;
        public int port;
        public Set<String> documents;
        public boolean needsHumanAuth;

        private Peer(String id, String ip, int port, List<String> docs, boolean needsHumanAuth) {
            this.id = id;
            this.ip = ip;
            this.port = port;
            this.documents = new HashSet<>(docs);
            this.needsHumanAuth = needsHumanAuth;
        }
        private void addDocuments(List<String> docs){
            documents.addAll(docs);
        }
        private void removeDocument(String doc){
            documents.remove(doc);
        }
        public Object[][] getRowRepresentations(){
            Object[][] rows = new Object[documents.size()][5];
            Object[] docs = documents.toArray();
            for(int i = 0; i < documents.size(); i++){
                rows[i] = new Object[]{id, ip, port, docs[i], needsHumanAuth};
            }
            return rows;
        }
    }
}
