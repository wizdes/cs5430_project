/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transport_layer.discovery;

import java.util.List;

/**
 *
 * @author Patrick
 */
public class DiscoveryResponseMessage implements java.io.Serializable{
    public String ident;
    public String ip;
    public int port;
    public List<String> documents;
    public long keyVersion;

    public DiscoveryResponseMessage(String ident, String ip, int port, List<String> docs, long keyVersion) {
        this.ident = ident;
        this.ip = ip;
        this.port = port;
        this.documents = docs;
        this.keyVersion = keyVersion;
    }
}
