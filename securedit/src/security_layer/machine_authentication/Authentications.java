/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.machine_authentication;

import application.messages.Message;
import java.security.Key;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import javax.crypto.SecretKey;
import security_layer.EncryptionKeys;
import security_layer.KeyFactory;
import transport_layer.network.Node;

/**
 *
 * @author Patrick C. Berens
 */
public class Authentications {
    private ConcurrentMap<String, Authentication> authentications = new ConcurrentHashMap<>();
    private EncryptionKeys keys;
    
    public Authentications(EncryptionKeys keys) {
        this.keys = keys;
    }
    
    public void addAuthentication(String ident, Message message, Condition cond, Lock lock){
        authentications.putIfAbsent(ident, new Authentication(message, cond, lock));
    }
    
    public void removeAuthentication(String ident){
        authentications.remove(ident);
    }
        
    private class Authentication{
        Message message;
        Condition cond;
        Lock lock;

        public Authentication(Message message, Condition cond, Lock lock) {
            this.message = message;
            this.cond = cond;
            this.lock = lock;
        }
    }
    
    /*
    * A -> B : {A, r} K_B
    * B generates K_AB
    * B -> A : {K_AB, B, r + 1, r'} K_A
    *  verify r+1, generate
    * A -> B : {r' + 1} K_B
    */
    public Message processAuthenticationRequest(AuthenticationMessage message) {
        
        Node to = ((Message)message).getFrom();
        
        if (message instanceof Msg01_AuthenticationRequest) {
            System.out.println("[DEBUG] processing Msg01_AuthenticationRequest");
            
            Msg01_AuthenticationRequest msg = (Msg01_AuthenticationRequest)message;
            
            SecretKey symmetricKey = (SecretKey)KeyFactory.generateSymmetricKey();
            
            int nonce1Response = msg.getNonce() + 1;
            int nonce2 = KeyFactory.generateNonce();
            
            Message m = new Msg02_KeyResponse(to, symmetricKey, nonce1Response, nonce2);
            addAuthentication(to.getID(), m, null, null);
            return m;
            
        } else if (message instanceof Msg02_KeyResponse) {
            System.out.println("[DEBUG] processing Msg02_KeyResponse");
            
            Msg02_KeyResponse msg = (Msg02_KeyResponse)message;
            Authentication auth = authentications.get(to.getID());
            
            int nonce2Response = msg.getNonce2() + 1;
            if (msg.getNonce1Response() - 1 != ((Msg01_AuthenticationRequest)auth.message).getNonce()) {
                System.out.println("[DEBUG] Bad Nonce msg02!");
                authentications.remove(to.getID());
                return null;
            }
            Message m = new Msg03_AuthenticationAgreement(to, nonce2Response);
            
            
            keys.addSymmetricKey(to.getID(), msg.getSymmetricKey());
            
            System.out.println("[DEBUG] grabbinglock");
            auth.lock.lock();
            try {
                auth.cond.signal();
            } finally {
                auth.lock.unlock();
            }
            
            System.out.println("[DEBUG] releasing lock");
            return m;
            
        } else if (message instanceof Msg03_AuthenticationAgreement) {
            System.out.println("[DEBUG] processing Msg03_AuthenticationAgreement");
            
            Msg03_AuthenticationAgreement msg = (Msg03_AuthenticationAgreement)message;
            Authentication auth = authentications.get(to.getID());
            Msg02_KeyResponse msg2 = (Msg02_KeyResponse)auth.message;
            
            if (msg.getNonce2Response() - 1 != msg2.getNonce2()) {
                System.out.println("[DEBUG] Bad Nonce msg03!");
                authentications.remove(to.getID());
                return null;
            }
            
            keys.addSymmetricKey(to.getID(), msg2.getSymmetricKey());
            
            return null;
        }
        
        return null;
    }
}
