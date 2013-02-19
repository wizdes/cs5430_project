/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;
import encryption.AES;
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

    public Message(Node t) {
        this.to = t;
    }
    
    public Message(Node t, Node f, String mid) {
        this.to = t;
        this.from = f;
        this.messageId = mid;
    }
    
    public boolean isValid() {
        return from != null && to != null && this.messageId != null;
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
    
    public static Message fromBytes(byte[] data) {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
        } catch (IOException ex) {
            System.out.println("IOException parsing " + data);
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException parsing " + data);
        }

        return obj == null ? null : (Message)obj;
    }
    
    public static Message fromEncryptedBytes(byte[] s, String password, String salt) {
       AES aes = new AES(password, salt);
       byte[] serialized = aes.decrypt(s);
       return fromBytes(serialized);
    }
    
    public byte[] serialize() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
        } catch (IOException ex) {
            System.out.println("FAILED TO SERIALIZE " + this);
        }
         
        return baos.toByteArray();
    }
    
    public byte[] serializeEncrypted(String password, String salt) {
        AES aes = new AES(password, salt);
        byte[] cryptedBytes = aes.encrypt(serialize());
        return cryptedBytes;
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
