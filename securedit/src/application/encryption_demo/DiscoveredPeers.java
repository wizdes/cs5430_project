/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Patrick
 */
public class DiscoveredPeers {
    public final ConcurrentMap<String, Peer> peers = new ConcurrentHashMap<>();
    
    public void addPeer(String id, String ip, int port, List<String> documents){
        Peer peer;
        if(!peers.containsKey(id)){  //Hasn't been discovered before this session
            peer = new Peer(id, ip, port);
            peers.put(id, peer);
        } else{ //Just update documents and if has authenticated
            peer = peers.get(id);
        }
        peer.addDocuments(documents);
    }
    public void removePeer(String id){
        peers.remove(id);
    }
    public Peer getPeer(String id){
        return peers.get(id);
    }
    
    public class Peer {
        public String id;
        public String ip;
        public int port;
        public Set<String> documents;

        private Peer(String id, String ip, int port) {
            this.id = id;
            this.ip = ip;
            this.port = port;
            //this.documents = new HashSet<>(docs);
            this.documents = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
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
                rows[i] = new Object[]{id, ip, port, docs[i]};
            }
            return rows;
        }
    }
}
