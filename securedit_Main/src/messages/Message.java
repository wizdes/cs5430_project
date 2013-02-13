/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.Node;


public class Message implements Serializable {
    
    private String messageId = null;
    private Node from = null;
    private Node to = null;
    
    public Message(Node t, Node f, String mid) {
        this.from = f;
        this.to = t;
        this.messageId = mid;
    }
    
    public boolean isValid() {
        return from != null && to != null;
    }
    
    public void setmessageId(String i) {
        this.messageId = i;
    }
    
    public String getMessageId() {
        return messageId;
    }

    public void setFrom(Node f) {
        this.from = f;
    }
    
    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }
    
    public void setTo(Node t) {
        this.to = t;
    }
             
    public static Message fromString(String s) {
        Object obj = null;
        try {
            byte [] data = Base64Coder.decode(s);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            obj  = ois.readObject();
            ois.close();
        } catch (IOException ex) {
            System.out.println("IOException parsing " + s);
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException parsing " + s);
        }

        return obj == null ? null : (Message)obj;
    }

    public String serialize() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
        } catch (IOException ex) {
            System.out.println("FAILED TO SERIALIZE " + this);
        }
        return new String(Base64Coder.encode(baos.toByteArray()));
    }
    
    @Override
    public String toString() {
        return "(" + this.getMessageId() + " : " + from + " ~> " + to + ")";
    }
    
    @Override
    public boolean equals(Object other) {
        return other instanceof Message 
                && ((Message)other).getMessageId().equals(this.getMessageId());
    }
    
    public static void main(String[] args) {
        System.out.println("hell world");
    }
}
