/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package transport_layer.network;

import java.io.Serializable;

/**
 *
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
    
    static Node fromString(String str) {
        String[] arr = str.split(":");
        return new Node(arr[0], arr[1], Integer.parseInt(arr[2]));
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
