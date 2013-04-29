package transport_layer.network;


/**
 * This message type indicates to the receiver that the connection is being closed by the other 
 * side
 */
class CloseConnection extends NetworkMessage {
    CloseConnection(Node source, Node destination) {
        super(source, destination, null);
    }
}
