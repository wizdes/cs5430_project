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
import network.Network;
import network.Node;

public class Message implements Serializable {
    
    public static byte ENC_TYPE_NONE = 0;
    public static byte ENC_TYPE_AES = 1;
    public static byte ENC_TYPE_RSA = 2;
    
    private String messageId = null;
    private String replyTo = null;
    private Node from = null;
    private Node to = null;

    public Message(Node t) {
        this.to = t;
    }

    public Message(Node t, String mid) {
        this.to = t;
        this.messageId = mid;
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
    
    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
    
    public static Message fromBytes(byte[] data) {
        byte[] messageData = new byte[data.length - 1];
        System.arraycopy(data, 1, messageData, 0, data.length - 1);
        
        Object obj = null;
        
        ByteArrayInputStream bis = new ByteArrayInputStream(messageData);
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
        
        byte[] message = baos.toByteArray();   
        return withEncryptionType(message, ENC_TYPE_NONE);
    }
    
    protected byte[] withEncryptionType(byte[] message, byte encType) {
        
        byte[] encryptionType = new byte[1];
        encryptionType[0] = encType;
        byte[] fullMessage = new byte[message.length + 1];

        System.arraycopy(encryptionType, 0, fullMessage, 0, 1);
        System.arraycopy(message, 0, fullMessage, 1, message.length);

        return fullMessage;
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
