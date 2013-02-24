/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.machine_authentication;

import application.messages.Message;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author Patrick C. Berens
 */
public class Authentications {
    ConcurrentMap<String, Authentication> authentications = new ConcurrentHashMap<>();
    
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
}
