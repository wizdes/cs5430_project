package transport_layer.network;

import java.io.Serializable;

/**
 * This is the parent class of messages used by the network layer; the header contains
 * the destination node for this message, the sending node, the document it is intended for
 * and whatever the serialized message content will be
 */
class NetworkMessage implements Serializable{
    Node destination;
    String docID;
    Node source;
    Serializable content;
    
    NetworkMessage(Node source, Node destination, Serializable content) {
        this.destination = destination;
        this.source = source;
        this.content = content;
    }

    public NetworkMessage(Node source, String docID, Node destination, Serializable content) {
        this.destination = destination;
        this.docID = docID;
        this.source = source;
        this.content = content;
    }
}
