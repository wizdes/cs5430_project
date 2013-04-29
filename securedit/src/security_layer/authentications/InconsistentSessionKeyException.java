package security_layer.authentications;

/**
 * Exception which is thrown when MACs don't match(K's aren't the same).
 * @author Patrick
 */
public class InconsistentSessionKeyException extends Exception{
    public InconsistentSessionKeyException() { super(); }
    public InconsistentSessionKeyException(String message) { super(message); }
    public InconsistentSessionKeyException(String message, Throwable cause) { super(message, cause); }
    public InconsistentSessionKeyException(Throwable cause) { super(cause); }
}
