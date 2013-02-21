/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Machine_Auth;
import Keys.Keys_Func;
import encryption.AES;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import messages.AuthRequest;
import messages.AuthResponse;
import messages.ChallengeResponse;
import messages.Message;
import network.NetworkInterface;
import network.Node;


/*
 * 
 * A wishes to authenticate with B
 * A -> B : {A, r} K_B
 * B generates K_AB
 * B -> A : {K_AB, B, r + 1, r'} K_A
 * A -> B : {r' + 1} K_B
 * 
 */

/**
 *
 * @author yjli_000
 */
public class Machine_Auth implements Machine_Auth_Interface{

    private String IP;
    private Keys_Func _key;
    private NetworkInterface network;
    private String Machine_Auth_State;
    private Map<String, AES> session_keys;
    private SecretKey client_key;
    
    Machine_Auth() {
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
    public void setup(String username, String password, String filename, NetworkInterface _network) {
        //!!!! WARNING! THIS ONLY READS THE FILE; DOESN'T DECRYPT
        read_file(username, password, filename);
        
        Machine_Auth_State = "";
        network = _network;
    }

    @Override
    public boolean authenticate_as_client(Node server) {
        // calls network send
        // sends over a session key encrypted and hashed
        Machine_Auth_State = "client";
        
        //encrypted 'client' with the public key
        PublicKey pub_k = _key.id_pub_key.get("server");
        
        AuthRequest m = new AuthRequest(server, "1");
        Message response = network.sendMessageAndAwaitReply(m);
        AuthResponse authResponse = null;
        if (response instanceof AuthResponse) {
            authResponse = (AuthResponse)response;
        } else {
            System.out.println("Did not recieve an auth response");
            return false;
        }

        client_key = authResponse.getSecret();
        Integer nonceResponse = authResponse.getNonce() + 1;
        
        ChallengeResponse challengeResponse = new ChallengeResponse(server, "2", nonceResponse, client_key);
        network.sendMessage(challengeResponse);
          
        return true;
    }
    
    private AES genRandAES()
    {
        SecureRandom random = new SecureRandom();
        String pw = new BigInteger(130, random).toString(32);
        String salt = new BigInteger(130, random).toString(32);
        return new AES(pw, salt);
    }
        
    @Override
    public void listenForMessages() {
        for (Message m : network.waitForMessages()) {
           if (m instanceof AuthRequest) {
            authenticate_as_server((AuthRequest)m);
           }
        } 
    }
    
    
    public boolean authenticate_as_server(AuthRequest m ) {
        String clientId = m.getFrom().toString();
        
        session_keys.put(clientId, genRandAES());
        SecretKey secret = session_keys.get(clientId).getKey();
        int nonce = new SecureRandom().nextInt();
        PublicKey pub_k = _key.id_pub_key.get(clientId);
        
        AuthResponse authResponse = new AuthResponse(m.getFrom(), "server-1", secret, nonce, pub_k);
        Message response = network.sendMessageAndAwaitReply(m);
        
        boolean confirmed = false;
        if (response instanceof ChallengeResponse) {
            ChallengeResponse challengeResponse = (ChallengeResponse)response;
            confirmed = nonce == challengeResponse.getNonce();
        }
        
        if (!confirmed) {
            session_keys.remove("client");
        }

        return confirmed;
    }
    
}
