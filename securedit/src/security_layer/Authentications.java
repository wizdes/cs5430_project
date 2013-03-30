/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.encryption_demo.Messages.Message;
import configuration.Constants;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;

/**
 *
 * @author Patrick C. Berens
 */
class Authentications {
    private ConcurrentMap<String, Authentication> authentications = new ConcurrentHashMap<>();
    private ConcurrentMap<String, String> pins = new ConcurrentHashMap<>();
    private EncryptionKeys keys;
    private Profile profile;
    PINFunctionality pf;
    SecureTransportInterface secureTransport;
    
    Authentications(Profile profile, SecureTransportInterface secureTransport) {
        pf = new PINFunctionality();
        this.keys = profile.keys;
        this.profile = profile;
        this.secureTransport = secureTransport;
    }
    
    private void addAuthentication(String ident, Message message){
        authentications.put(ident, new Authentication(message));
    }
    
    void addAuthentication(String ident, Message message, Condition cond, Lock lock){
        authentications.put(ident, new Authentication(message, cond, lock));
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
    
    HumanAuthenticationMessage constructInitialHAuthMessage(){
        return new HA_Msg1(KeyFactory.generateNonce());
    }

    public boolean signalID(String ID) {
        Authentication auth = authentications.get(ID);
        auth.lock.lock();
        try {
            auth.cond.signal();
        } finally {
            auth.lock.unlock();
        }
        return true;
    }
    
    public void waitID(String ID){
        Authentication auth = authentications.get(ID);
        auth.lock.lock();
        try {
            auth.cond.await();
        }
        catch (InterruptedException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "]", ex);
            }
        }finally {
            auth.lock.unlock();
        }
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
    
    public String getPIN(String ID){
        return pins.get(ID);
    }
    
    //This should send a  HumanAuthenticationMessage TCP saying "Hi, I'm A and I am discovering"
    //     which is a dummy message for now. it should send it to one of the peers(you pick).
    //MAJOR ASSUMPTION. PIN OPERATIONS FOR A USER ARE DONE OUTSIDE OF THIS
    //THEY NEED TO BE PUT IN keys.getSymmetricKey(sourceOfMessage)
    void processHumanAuthenticationRequest(String sourceOfMsg, HumanAuthenticationMessage message, SecureTransportInterface sti) {
        String idOfNodeAuthenticationWith = sourceOfMsg;
        if (message instanceof HA_Msg1) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(Authentications.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing Human Authentication Message: " + HA_Msg1.class.getName());
            }
            
            //generate a PIN
            String PIN = pf.getPIN();
            String PINHMAC = PIN + "HMAC";
            System.out.println("PIN is: " + PIN);
            pins.put(sourceOfMsg, PIN);
            secureTransport.displayPIN(idOfNodeAuthenticationWith, PIN);
            //send info into GUI somehow
            
            HA_Msg1 msg = (HA_Msg1)message;
            PublicKey publicKey = keys.getPublicKey(keys.ident);
            PublicKey verifyingKey = keys.getVerifyingKey(keys.ident);
            long myAsymmetricKeyVersion = keys.getAsymmetricKeyVersion(keys.ident);
            SecretKey pinKey = (SecretKey) KeyFactory.generateSymmetricKey(PIN);
            SecretKey HMACKey = (SecretKey) KeyFactory.generateSymmetricKey(PINHMAC);
            
            keys.secretKeys.put(sourceOfMsg, pinKey);
            keys.HMACKeys.put(sourceOfMsg, HMACKey);
            
            addAuthentication(sourceOfMsg, msg, null, null);
            int nonceResponse1 = msg.nonce + 1;
            Message m = new HA_Msg2(publicKey, verifyingKey, myAsymmetricKeyVersion, nonceResponse1);
            sti.sendAESEncryptedMessage(idOfNodeAuthenticationWith, m, pinKey, HMACKey);
                        
        } else if (message instanceof HA_Msg2) {
            if(Constants.DEBUG_ON){    
                Logger.getLogger(Authentications.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing Human Authentication Message: " + HA_Msg2.class.getName());
            }
            HA_Msg2 msg = (HA_Msg2)message;
            Authentication auth = authentications.get(idOfNodeAuthenticationWith);
            if (msg.nonce - 1 != ((HA_Msg1)auth.message).nonce) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(Authentications.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] MA_Msg2 processing: Bad Nonce msg02! Expecting: " + (((MA_Msg1)auth.message).nonce + 1) + ", found: " + msg.nonce);
                }
                authentications.remove(idOfNodeAuthenticationWith);
                return;
            }
            
            
            PublicKey otherPublicKey = msg.publicKey;
            keys.addPublicKey(idOfNodeAuthenticationWith, otherPublicKey, msg.ownersAsymmetricKeyVersion);
            keys.addVerifyingKey(idOfNodeAuthenticationWith, msg.verifyingKey);
            
            
            PublicKey publicKey = keys.publicKeys.get(keys.ident);
            PublicKey verifyingKey = keys.verifyingKeys.get(keys.ident);
            long asymmetricKeyVersion = keys.asymmetricKeyVersions.get(keys.ident);
            
            int nonceResponse1 = msg.nonce + 1;
            Message m = new HA_Msg3(publicKey, verifyingKey, asymmetricKeyVersion, nonceResponse1);
            sti.sendAESEncryptedMessage(idOfNodeAuthenticationWith, m,
                    keys.getSymmetricKey(idOfNodeAuthenticationWith), keys.getHMACKey(idOfNodeAuthenticationWith));
            keys.secretKeys.remove(sourceOfMsg);
            keys.HMACKeys.remove(sourceOfMsg);
            
//            profile.save(keys.password);
        }
        else {
            if(Constants.DEBUG_ON){
                Logger.getLogger(Authentications.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing Human Authentication Message: " + HA_Msg3.class.getName());
            }
            HA_Msg3 msg = (HA_Msg3)message;
            Authentication auth = authentications.get(idOfNodeAuthenticationWith);
            if (msg.nonce - 2 != ((HA_Msg1)auth.message).nonce) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(Authentications.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] MA_Msg2 processing: Bad Nonce msg02! Expecting: " + (((MA_Msg1)auth.message).nonce + 1) + ", found: " + msg.nonce);
                }
                authentications.remove(idOfNodeAuthenticationWith);
                return;
            }
            authentications.remove(idOfNodeAuthenticationWith);
            PublicKey otherPublicKey = msg.publicKey;
            keys.addPublicKey(idOfNodeAuthenticationWith, otherPublicKey, msg.asymmetricKeyVersion);
            keys.addVerifyingKey(idOfNodeAuthenticationWith, msg.verifyingKey);

            keys.secretKeys.remove(sourceOfMsg);
            keys.HMACKeys.remove(sourceOfMsg);
            
//            profile.save(keys.password);            
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
            if(Constants.DEBUG_ON){
                Logger.getLogger(Authentications.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing Machine Authentication Message: " + MA_Msg1.class.getName());
            }
            
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
            if(Constants.DEBUG_ON){
                Logger.getLogger(Authentications.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing Machine Authentication Message: " + MA_Msg2.class.getName());
            }
            
            MA_Msg2 msg = (MA_Msg2)message;
            Authentication auth = authentications.get(idOfNodeAuthenticationWith);
            
            if (msg.r - 1 != ((MA_Msg1)auth.message).nonce) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(Authentications.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] MA_Msg2 processing: Bad Nonce msg02! Expecting: " + (((MA_Msg1)auth.message).nonce + 1) + ", found: " + msg.r);
                }
                authentications.remove(idOfNodeAuthenticationWith);
                return;
            }
            
            keys.addSymmetricKey(idOfNodeAuthenticationWith, msg.SK);
            
        } else if (message instanceof MA_Msg3) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(Authentications.class.getName()).log(Level.INFO, "[User: " + keys.ident + "] Processing Machine Authentication Message: " + MA_Msg3.class.getName());
            }
            
            MA_Msg3 msg = (MA_Msg3)message;
            Authentication auth = authentications.get(idOfNodeAuthenticationWith);
            
            if (msg.r - 2 != ((MA_Msg1)auth.message).nonce) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(Authentications.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] MA_Msg3 processing: Bad Nonce msg02! Expecting: " + (((MA_Msg1)auth.message).nonce + 1) + ", found: " + msg.r);
                }
                authentications.remove(idOfNodeAuthenticationWith);
                return;
            }
            
            keys.addHMACKey(idOfNodeAuthenticationWith, msg.SK);
            
            if (!keys.hasSymmetricKey(idOfNodeAuthenticationWith)) {
                if(Constants.DEBUG_ON){
                    Logger.getLogger(Authentications.class.getName()).log(Level.SEVERE, "[User: " + keys.ident + "] MA_Msg3 processing: No symmetric key found for " + idOfNodeAuthenticationWith);
                }
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
