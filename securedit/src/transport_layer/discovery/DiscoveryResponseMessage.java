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
    public String owner;
    public String ip;
    public int port;
    public List<String> documents;
    public long ownerAsymmetricKeyVersion;
    public long clientsKeyVersionNumberHeldByOwner;
    public DiscoveryResponseMessage(String owner, String ip, int port, List<String> docs, long ownerAsymmetricKeyVersion, long clientsKeyVersionNumberHeldByOwner) {
        this.owner = owner;
        this.ip = ip;
        this.port = port;
        this.documents = docs;
        this.ownerAsymmetricKeyVersion = ownerAsymmetricKeyVersion;
        this.clientsKeyVersionNumberHeldByOwner = clientsKeyVersionNumberHeldByOwner;
    }
    @Override
    public String toString(){
        return "owner: " + owner + ", ip: " + ip + ", port: " + port + "docs size: " + documents.size();
    }
}
