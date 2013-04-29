package transport_layer.files;

/**
 * Interface for basic low level file operations
 * See FileHandler.java for implementation and specific comments
 */
public interface FileTransportInterface {
    public String openUnserializedFile(String filename);
    public java.io.Serializable readFile(String filename);
    public boolean writeFile(String filename, java.io.Serializable content);
}
