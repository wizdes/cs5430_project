/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.io.Serializable;

/**
 *
 */
public class Node implements Serializable {

    private String host;
    private int port;
    private String id;
    
    public Node(String id, String h, int p) {
        this.host = h;
        this.port = p;
        this.id = id;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }
    
    public String getID() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return this.id + " @ " + this.host + ":" + this.port;
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
        return getID().equals(other.getID())
               && getPort() == other.getPort()
               && getHost().equals(other.getHost());
    }
    
}
