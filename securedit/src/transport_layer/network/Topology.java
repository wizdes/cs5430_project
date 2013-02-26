package transport_layer.network;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Patrick C. Berens
 */
class Topology {
    private Node host;
    private ConcurrentMap<String, Node> nodes = new ConcurrentHashMap<>();

    Topology(Node host) {
        this.host = host;
    }
    Node getNode(String ident){
        return nodes.get(ident);
    }
    void addNode(String ident, String host, int port){
        nodes.putIfAbsent(ident, new Node(ident, host, port));
    }
    Node removeNode(String ident){
        return nodes.remove(ident);
    }
    Node getMyNode(){
        return host;
    }
    String getMyId(){
        return host.id;
    }
    String getMyHost(){
        return host.host;
    }
    int getMyPort(){
        return host.port;
    }
}
