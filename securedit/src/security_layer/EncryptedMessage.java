package security_layer;

/**
 * Main interface for messages in the security_layer.
 * -Implements getAlgorithm so knows whether AES or RSA is being utilized.
 * @author Patrick C. Berens
 */
public interface EncryptedMessage extends java.io.Serializable {
    public String getAlgorithm();
}
