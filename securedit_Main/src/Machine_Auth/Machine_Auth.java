/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Machine_Auth;
import Keys.Keys_Func;
import messages.Message;
import messages.TextMessage;
import messages.NetworkMessage;
import network.NetworkInterface;
import network.Node;
import encryption.RSA_Crypto_Interface;
import java.security.PublicKey;
import java.util.Collection;
import encryption.AES;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yjli_000
 */
public class Machine_Auth implements Machine_Auth_Interface{

    String IP;
    Keys_Func _key;
    NetworkInterface network;
    RSA_Crypto_Interface RSA;
    String Machine_Auth_State;
    Map<String, AES> session_keys;
    String client_key;
    int port_num;
    
    Machine_Auth()
    {
        _key = new Keys_Func();
    }
    
    void read_file(String username, String password, String filename){
        try {
            // reads the file
            // populates Keys class
            ObjectInput input = new ObjectInputStream (new BufferedInputStream(new FileInputStream(filename)));
            //deserialize the List
            _key = (Keys_Func)input.readObject();
            //display its data
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(Machine_Auth.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void setup(String username, String password, String filename, NetworkInterface _network, RSA_Crypto_Interface _RSA) {
        //!!!! WARNING! THIS ONLY READS THE FILE; DOESN'T DECRYPT
        read_file(username, password, filename);
        
        Machine_Auth_State = "";
        network = _network;
        client_key = "";
        port_num = 8000;
    }

    @Override
    public boolean authenticate_as_client(String IP) {
        // calls network send
        // sends over a session key encrypted and hashed
        Machine_Auth_State = "client";
        Node n = new Node("server", IP, port_num);
        //encrypted 'client' with the public key
        String auth_request = "client_auth_req";
        PublicKey pub_k = _key.id_pub_key.get("server");
        auth_request = new String(RSA.PublicKeyEncrypt(pub_k, auth_request.getBytes()));
        TextMessage m = new TextMessage(n, auth_request);
        m.setmessageId("client_auth_req");
        network.sendMessage(m);
        Collection<Message> response = network.waitForMessages();
        for(Message elt:response)
        {
           if(elt.getMessageId().equals("server_auth_resp")){
               String encrypted_session_key = ((NetworkMessage)elt).getContent();
               client_key = new String(RSA.PrivateKeyDecrypt(_key.my_priv_key, encrypted_session_key.getBytes()));
               m = new TextMessage(n ,new String(RSA.PublicKeyEncrypt(pub_k, 
                    ((NetworkMessage)elt).getNonce().toString().getBytes())));
               m.setmessageId("confirm_nonce");
               network.sendMessage(m);
           }
        }        
        
        return true;
    }
    
    private AES genRandAES()
    {
        SecureRandom random = new SecureRandom();
        String pw = new BigInteger(130, random).toString(32);
        String salt = new BigInteger(130, random).toString(32);
        return new AES(pw, salt);
    }
    
    private boolean server_confirm_nonce(Integer nonce){
        Collection<Message> response = network.waitForMessages();
        Machine_Auth_State = "server";
        for(Message elt:response){
            if(elt.getMessageId().equals("confirm_nonce"))
            {
                Integer client_nonce = Integer.parseInt(((TextMessage)elt).getContent());
                if(nonce.equals(client_nonce)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean authenticate_as_server() {
        Collection<Message> response = network.waitForMessages();
        Machine_Auth_State = "server";
        for(Message elt:response)
        {
           if(elt.getMessageId().equals("client_auth_req")){
               Node n = new Node("server", IP, port_num);
               session_keys.put("client", genRandAES());
               String auth_resp = session_keys.get("client").getKey().toString();
               PublicKey pub_k = _key.id_pub_key.get("client");
               auth_resp = new String(RSA.PublicKeyEncrypt(pub_k, auth_resp.getBytes()));
               SecureRandom random = new SecureRandom();
               int nonce = random.nextInt();
               NetworkMessage m = new NetworkMessage(n, auth_resp, nonce);
               m.setmessageId("server_auth_resp");
               network.sendMessage(m);
               if(!server_confirm_nonce(nonce))
               {
                   session_keys.remove("client");
               }
           }
        } 
        return true;
    }
    
}
