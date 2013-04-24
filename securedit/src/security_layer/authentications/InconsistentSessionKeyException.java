/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

/**
 *
 * @author Patrick
 */
public class InconsistentSessionKeyException extends Exception{
    public InconsistentSessionKeyException() { super(); }
    public InconsistentSessionKeyException(String message) { super(message); }
    public InconsistentSessionKeyException(String message, Throwable cause) { super(message, cause); }
    public InconsistentSessionKeyException(Throwable cause) { super(cause); }
}
