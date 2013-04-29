package transport_layer.network;

import java.io.Serializable;

/**
 * Represents a single node in the network (IP, host, port)
 */
public class Node implements Serializable {
    String host;
    int port;
    String id;
    
    Node(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }    
    
    @Override
    public String toString() {
        return this.id + ":" + this.host + ":" + this.port;
    }
    
    @Override
    public boolean equals(Object other) {
        return other instanceof Node
               && other != null
               && equals((Node)other);
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    
    public boolean equals(Node other) {
        return id.equals(other.id)
               && port == other.port
               && host.equals(other.host);
    }
    
}
