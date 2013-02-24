/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package _reference_classes;

/**
 *
 * @author Patrick C. Berens
 */
public class Message implements java.io.Serializable {
    public String message;
    public String from;
    public String to;
    public Message(String message, String from, String to){
        this.message = message;
        this.from = from;
        this.to = to;
    }
}
