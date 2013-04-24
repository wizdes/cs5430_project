/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

/**
 * This is thrown when a client tries to perform SRP authentication, without
 *   first setting up an account.
 *  -(s,v) pair doesn't exist in persistent state on server
 * @author Patrick
 */
public class InvalidSRPMessageException extends Exception{
    public InvalidSRPMessageException() { super(); }
    public InvalidSRPMessageException(String message) { super(message); }
    public InvalidSRPMessageException(String message, Throwable cause) { super(message, cause); }
    public InvalidSRPMessageException(Throwable cause) { super(cause); }
}
