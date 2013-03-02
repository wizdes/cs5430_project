/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.encryption_demo.Message;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import javax.crypto.SecretKey;

/**
 *
 * @author Patrick C. Berens
 */
class Authentications {
    private ConcurrentMap<String, Authentication> authentications = new ConcurrentHashMap<>();
    private EncryptionKeys keys;
    
    Authentications(EncryptionKeys keys) {
        this.keys = keys;
    }
    
    private void addAuthentication(String ident, Message message){
        authentications.putIfAbsent(ident, new Authentication(message));
    }
    
    void addAuthentication(String ident, Message message, Condition cond, Lock lock){
        authentications.putIfAbsent(ident, new Authentication(message, cond, lock));
    }
    
    void removeAuthentication(String ident){
        authentications.remove(ident);
    }
    boolean hasAuthenticated(String ident){
        return keys.hasSymmetricKey(ident);
    }
    AuthenticationMessage constructInitialAuthMessage(){
        return new MA_Msg1(KeyFactory.generateNonce());
    }
    private class Authentication{
        Message message;
        Condition cond;
        Lock lock;

        private Authentication(Message message, Condition cond, Lock lock) {
            this.message = message;
            this.cond = cond;
            this.lock = lock;
        }

        private Authentication(Message message) {
            this.message = message;
        }
    }
    
    /*
    * A -> B : {r2} K_B
    * B generates K_AB
    * B -> A : {K_AB, r2 + 1, r2'} K_A
    *  verify r2+1, generate
    * A -> B : {r2' + 1} K_B
    */
    Message processAuthenticationRequest(String sourceOfMsg, AuthenticationMessage message) {
        String idOfNodeAuthenticationWith = sourceOfMsg;
        if (message instanceof MA_Msg1) {
            System.out.println("[DEBUG] processing Msg01_AuthenticationRequest");
            
            MA_Msg1 msg = (MA_Msg1)message;
            
            SecretKey symmetricKey = (SecretKey)KeyFactory.generateSymmetricKey();
            
            int nonce1Response = msg.nonce1 + 1;
            int nonce2 = KeyFactory.generateNonce();
            
            Message m = new MA_Msg2(symmetricKey, nonce1Response, nonce2);
            addAuthentication(idOfNodeAuthenticationWith, m);
            return m;
            
        } else if (message instanceof MA_Msg2) {
            System.out.println("[DEBUG] processing Msg02_KeyResponse");
            
            MA_Msg2 msg = (MA_Msg2)message;
            Authentication auth = authentications.get(idOfNodeAuthenticationWith);
            
            int nonce2Response = msg.r2 + 1;
            if (msg.r1 - 1 != ((MA_Msg1)auth.message).nonce1) {
                System.out.println("[DEBUG] Bad Nonce msg02! Expecting: " + (((MA_Msg1)auth.message).nonce1 + 1) + ", found: " + msg.r1);
                authentications.remove(idOfNodeAuthenticationWith);
                return null;
            }
            Message m = new MA_Msg3(nonce2Response);
            
            keys.addSymmetricKey(idOfNodeAuthenticationWith, msg.SK);
            
            System.out.println("[DEBUG] grabbinglock");
            auth.lock.lock();
            try {
                auth.cond.signal();
            } finally {
                auth.lock.unlock();
            }
            
            System.out.println("[DEBUG] releasing lock");
            return m;
            
        } else if (message instanceof MA_Msg3) {
            System.out.println("[DEBUG] processing Msg03_AuthenticationAgreement");
            
            MA_Msg3 msg = (MA_Msg3)message;
            Authentication auth = authentications.get(idOfNodeAuthenticationWith);
            MA_Msg2 msg2 = (MA_Msg2)auth.message;
            
            if (msg.nonce2Response - 1 != msg2.r2) {
                System.out.println("[DEBUG] Bad Nonce msg03!");
                authentications.remove(idOfNodeAuthenticationWith);
                return null;
            }   
            keys.addSymmetricKey(idOfNodeAuthenticationWith, msg2.SK);
            removeAuthentication(idOfNodeAuthenticationWith);
            return null;
        }
        return null;
    }
}
