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
import java.util.Objects;
import network.Node;

public class Message implements Serializable {
    
    private String messageId = null;
    private Node from = null;
    private Node to = null;
    static private BASE64Encoder encode = new BASE64Encoder();
    static private BASE64Decoder decode = new BASE64Decoder();

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
            byte [] data = javax.xml.bind.DatatypeConverter.parseBase64Binary(s);
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
         
        return javax.xml.bind.DatatypeConverter.printBase64Binary(baos.toByteArray());
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.messageId);
        hash = 29 * hash + Objects.hashCode(this.from);
        hash = 29 * hash + Objects.hashCode(this.to);
        return hash;
    }
}
