/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Machine_Auth;

import encryption.AES;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.SecretKey;
import messages.AESEncryptedMessage;
import messages.AuthRequest;
import messages.Message;
import messages.MessageInitRequest;
import messages.MessageTest;
import network.Network;
import network.Node;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author yjli_000
 */
public class Machine_AuthMsgSend extends MessageTest {
    public Machine_AuthMsgSend(){
    }
    
    @Test
    public void test()
    {
        // create a server and a client
        SecretKey secret = AES.generateKey("password", "secret");
        Network myNetwork = new Network(to);
        Network clientNetwork = new Network(from);
        
        //why does the network have a secret?
        myNetwork.setSecret(secret);
        clientNetwork.setSecret(secret);
        
        Machine_Auth client_ma = new Machine_Auth();
        Machine_Auth server_ma = new Machine_Auth();
        
        SecretKey shared_key = AES.generateKey("new_pass", "new_secret");
        server_ma.setSecretKeyAsClient(shared_key);
        client_ma.setSecretKeyAsClient(shared_key);

        server_ma.setNetwork(myNetwork);
        client_ma.setNetwork(clientNetwork);

        String test_msg = "IS THIS THE CRUSTY KRAB? NO MY NAME IS PATRICK!";
        clientWorker thread = new clientWorker(client_ma, test_msg, to);
        
        thread.start();
        
        List<Message> messages = (LinkedList)myNetwork.waitForMessages();
        
        assertEquals(messages.size(), 1);
        
        Message obj = messages.get(0);
        
        String result = server_ma.rcvInitRequest((MessageInitRequest) obj);
        assertEquals(test_msg, result);

        myNetwork.shutdown();
        clientNetwork.shutdown();
        // Run the stuffs
        // Ensure the message are correct
    }
    
    protected static class clientWorker extends Thread {
        private Machine_Auth ma;
        private String message;
        private Node to;
        
        public clientWorker(Machine_Auth n, String m, Node to) {
            ma = n;
            message = m;
            this.to = to;
        }
        
        @Override
        public void run() {
            ma.send(message, to);
        }
    }
}
