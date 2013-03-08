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
    MachineAuthenticationMessage constructInitialAuthMessage(){
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
    * A -> B : {r} K_B
    * B generates K_AB
    * B -> A : {K_AB, r + 1} K_A
    *  verify r+1
    * B -> A : {HMAC_K_AB, r + 2} K_A
    *  verify r+2
    */
    void processMachineAuthenticationRequest(String sourceOfMsg, MachineAuthenticationMessage message, SecureTransportInterface sti) {
        String idOfNodeAuthenticationWith = sourceOfMsg;
        if (message instanceof MA_Msg1) {
            System.out.println("[DEBUG] processing Msg01_AuthenticationRequest");
            
            MA_Msg1 msg = (MA_Msg1)message;
            
            SecretKey symmetricKey = (SecretKey)KeyFactory.generateSymmetricKey();            
            SecretKey HMACKey = (SecretKey)KeyFactory.generateSymmetricKey();
            
            int nonceResponse1 = msg.nonce + 1;
            
            Message m = new MA_Msg2(symmetricKey, nonceResponse1);
            
            sti.sendRSAEncryptedMessage(sourceOfMsg, m);
            keys.addSymmetricKey(idOfNodeAuthenticationWith, symmetricKey);
            
            int nonceResponse2 = msg.nonce + 2;
            Message m3 = new MA_Msg3(HMACKey, nonceResponse2);
            sti.sendRSAEncryptedMessage(sourceOfMsg, m3);
            keys.addHMACKey(idOfNodeAuthenticationWith, HMACKey);            
        } else if (message instanceof MA_Msg2) {
            System.out.println("[DEBUG] processing Msg02_KeyResponse");
            
            MA_Msg2 msg = (MA_Msg2)message;
            Authentication auth = authentications.get(idOfNodeAuthenticationWith);
            
            if (msg.r - 1 != ((MA_Msg1)auth.message).nonce) {
                System.out.println("[DEBUG] Bad Nonce msg02! Expecting: " + (((MA_Msg1)auth.message).nonce + 1) + ", found: " + msg.r);
                authentications.remove(idOfNodeAuthenticationWith);
                return;
            }
            
            keys.addSymmetricKey(idOfNodeAuthenticationWith, msg.SK);
            
        } else if (message instanceof MA_Msg3) {
            System.out.println("[DEBUG] processing Msg03_AuthenticationAgreement");
            
            MA_Msg3 msg = (MA_Msg3)message;
            Authentication auth = authentications.get(idOfNodeAuthenticationWith);
            
            if (msg.r - 2 != ((MA_Msg1)auth.message).nonce) {
                System.out.println("[DEBUG] Bad Nonce msg02! Expecting: " + (((MA_Msg1)auth.message).nonce + 1) + ", found: " + msg.r);
                authentications.remove(idOfNodeAuthenticationWith);
                return;
            }
            
            keys.addHMACKey(idOfNodeAuthenticationWith, msg.SK);
            
            if (!keys.hasSymmetricKey(idOfNodeAuthenticationWith)) {
                System.out.println("no symmetric key found for "+ idOfNodeAuthenticationWith);
                return;
            }
            
            auth.lock.lock();
            try {
                auth.cond.signal();
            } finally {
                auth.lock.unlock();
            }
            
            removeAuthentication(idOfNodeAuthenticationWith);
        }

    }
}
