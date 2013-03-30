/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

/**
 *
 * @author Patrick
 */
public class InvalidHMACException extends Exception{
    public InvalidHMACException() { super(); }
    public InvalidHMACException(String message) { super(message); }
    public InvalidHMACException(String message, Throwable cause) { super(message, cause); }
    public InvalidHMACException(Throwable cause) { super(cause); }
}
