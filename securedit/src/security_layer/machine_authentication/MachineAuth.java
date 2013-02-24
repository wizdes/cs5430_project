/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer.machine_authentication;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import application.encryption_demo.Communication;
import application.messages.Message;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;
import javax.crypto.SecretKey;
import transport_layer.network.Node;


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
public class MachineAuth implements MachineAuthInterface {

    private String IP;
    private String Machine_Auth_State;
    private SecretKey client_key;
    private Communication communication;
    
    public MachineAuth(Communication communication) {
        this.communication = communication;
    }
    
    @Override
    public boolean requestAuthenticationWith(Node node) {
        
        //encrypted 'client' with the public key
//        PublicKey pub_k = null;
//        
//        int nonce = new SecureRandom().nextInt();
//        Msg01_AuthenticationRequest m = new Msg01_AuthenticationRequest(node, "1", nonce);
//        Message response = communication.sendRSAEncryptedMessageAndAwaitReply(m, null);
//        Msg02_KeyResponse authResponse = null;
//        if (response instanceof Msg02_KeyResponse) {
//            authResponse = (Msg02_KeyResponse)response;
//        } else {
//            System.out.println("Did not recieve an auth response");
//            return false;
//        }
//
//        client_key = authResponse.getSymmetricKey();
//        Integer nonceResponse = authResponse.getNonce2() + 1;
//        
//        Msg03_AuthenticationAgreement challengeResponse = 
//                new Msg03_AuthenticationAgreement(node, "2", nonceResponse);
//        communication.sendAESEncryptedMessage(challengeResponse, null);
          
        return true;        
    }
    
    @Override
    public boolean processAuthenticationRequest(Msg01_AuthenticationRequest m) {
//        String clientId = m.getFrom().toString();
//        
//        session_keys.put(clientId, genRandAES());
//        SecretKey secret = session_keys.get(clientId).getKey();
//        int nonce = new SecureRandom().nextInt();
//        PublicKey pub_k = _key.id_pub_key.get(clientId);
//        
//        AuthResponse authResponse = new AuthResponse(m.getFrom(), "server-1", secret, nonce, pub_k);
//        Message response = network.sendMessageAndAwaitReply(authResponse);
//        
//        boolean confirmed = false;
//        if (response instanceof ChallengeResponse) {
//            ChallengeResponse challengeResponse = (ChallengeResponse)response;
//            confirmed = nonce == challengeResponse.getNonce();
//        }
//        
//        if (!confirmed) {
//            session_keys.remove("client");
//        }

//        return confirmed; 
        return true;
    }

    
    
}
