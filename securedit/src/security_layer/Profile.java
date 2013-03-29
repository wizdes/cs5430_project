/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer;

import application.encryption_demo.Messages.Message;
import configuration.Constants;
import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Profile implements Message {
    public String ident;
    public String host;
    public int port;
    KeysObject keys;
    
    //These documents will be returned when a discovery broadcast is received.
    public transient ArrayList<String> documentsOpenForDiscovery = new ArrayList<>();
    
    //These documents are only available by being manually entered.
    public transient ArrayList<String> documentsHiddenFromDiscovery = new ArrayList<>();
    
    public void save(String pw) {
        SecureTransport transport = new SecureTransport(pw);
        transport.writeEncryptedFile(ident + ".profile", this);
    }
    
    public void addPublicKeysFrom(Profile other) {
        this.keys.publicKeys.put(other.ident, other.keys.publicKeys.get(other.ident));
        this.keys.verifiyngKeys.put(other.ident, other.keys.verifiyngKeys.get(other.ident));
        this.keys.asymmetricKeyVersions.put(other.ident, keys.asymmetricKeyVersions.get(other.ident));
    }
    /**
     * Exposes this part of keys to world. Only passes out copy.
     * @param ident
     * @return 
     */
    public long getAsymmetricKeyVersionNumber(String ident){
        Long keyVersion = this.keys.asymmetricKeyVersions.get(ident);
        return (keyVersion == null) ? new Long(-1) : keyVersion;
    }
    public static void deleteProfile(String username) {
        File f = new File(username + ".profile");
        if (f.exists()) {
            f.delete();
        }
    }
    
    public static Profile readProfile(String username, String pw) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(Profile.class.getName()).log(Level.INFO, "Reading profile for " + username + ", " + pw);
        }
        SecureTransport transport = new SecureTransport(pw);
        return (Profile)transport.readEncryptedFile(username + ".profile");
    }

    public static Profile writeProfile(String username, String pw, int port, String host) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(Profile.class.getName()).log(Level.INFO, "Creating new profile for " + username + ", " + pw);
        }
        Profile profile = new Profile();
        profile.host = host;
        profile.port = port;
        profile.ident = username;
        profile.keys = new KeysObject();
        KeyPair asymmetricKeys = KeyFactory.generateAsymmetricKeys();
        profile.keys.publicKeys = new ConcurrentHashMap<>();
        profile.keys.publicKeys.put(username, asymmetricKeys.getPublic());
        profile.keys.privateKey = asymmetricKeys.getPrivate();
        KeyPair signingKeyPair = KeyFactory.generateAsymmetricKeys();
        profile.keys.verifiyngKeys = new ConcurrentHashMap<>();
        profile.keys.verifiyngKeys.put(username, signingKeyPair.getPublic());
        profile.keys.signingKey = signingKeyPair.getPrivate();
        profile.keys.asymmetricKeyVersions.put(username, (long)0);
        profile.save(pw);
        
        return profile;
    }
    
    public void updateProfilePassword(String newPassword){
        if(Constants.DEBUG_ON){
            Logger.getLogger(Profile.class.getName()).log(Level.INFO, "Updating profile for: " + ident + " with new password: " + newPassword);
        }
        
        //Update asymmetric keys
        KeyPair asymmetricKeys = KeyFactory.generateAsymmetricKeys();
        keys.publicKeys.put(ident, asymmetricKeys.getPublic());
        keys.privateKey = asymmetricKeys.getPrivate();
        
        //Update signing keys
        KeyPair signingKeyPair = KeyFactory.generateAsymmetricKeys();
        keys.verifiyngKeys.put(ident, signingKeyPair.getPublic());
        keys.signingKey = signingKeyPair.getPrivate();
        
        Long versionNum = keys.asymmetricKeyVersions.get(ident);
        keys.asymmetricKeyVersions.put(ident, versionNum+1);
        
        //Update profile with new keys and encrypted with new password
        save(newPassword);
        
    }
}
