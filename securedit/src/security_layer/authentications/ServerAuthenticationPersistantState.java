/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer.authentications;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Patrick
 */
class ServerAuthenticationPersistantState {
    ConcurrentMap<String, SaltAndVerifier> state = new ConcurrentHashMap<>();

    void addClientPasswordState(String clientID, byte[] salt, BigInteger passwordVerifier){
        state.put(clientID, new SaltAndVerifier(salt, passwordVerifier));
    }
    byte[] getClientSalt(String clientID){
        return state.get(clientID).salt;
    }
    BigInteger getClientVerifier(String clientID){
        return state.get(clientID).passwordVerifier;
    }
    private class SaltAndVerifier{
        private byte[] salt;
        private BigInteger passwordVerifier;

        private SaltAndVerifier(byte[] salt, BigInteger passwordVerifier) {
            this.salt = salt;
            this.passwordVerifier = passwordVerifier;
        }
    }
}
