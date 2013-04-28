package security_layer.authentications;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This holds the persistent variables for SRP authentication.
 * -Every clients s,v for a given document(stored on fs).
 * @author Patrick
 */
public class ServerAuthenticationPersistantState implements Serializable {
    ConcurrentMap<String, SaltAndVerifier> state = new ConcurrentHashMap<>();

    void addClientPasswordState(String clientID, byte[] salt, BigInteger passwordVerifier){
        state.put(clientID, new SaltAndVerifier(salt, passwordVerifier));
    }
    byte[] getClientSalt(String clientID){
        SaltAndVerifier s = state.get(clientID);
        return s == null ? null : s.salt;
    }
    BigInteger getClientVerifier(String clientID){
        SaltAndVerifier s = state.get(clientID);
        return s == null ? null : s.passwordVerifier;
    }
    private class SaltAndVerifier implements Serializable { 
        private byte[] salt;
        private BigInteger passwordVerifier;

        private SaltAndVerifier(byte[] salt, BigInteger passwordVerifier) {
            this.salt = salt;
            this.passwordVerifier = passwordVerifier;
        }
    }
}
