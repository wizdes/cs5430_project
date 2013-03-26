/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer;

import application.encryption_demo.Message;
import java.beans.Transient;
import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class Profile implements Message {
    public String ident;
    public String host;
    public int port;
    KeysObject keys;
    public long keyVersion;
    public transient ArrayList<String> documents = new ArrayList<>();
    
    public void save(String pw) {
        SecureTransport transport = new SecureTransport(pw);
        transport.writeEncryptedFile(ident + ".profile", this);
    }
    
    public void addPublicKeysFrom(Profile other) {
        this.keys.publicKeys.put(other.ident, other.keys.publicKeys.get(other.ident));
        this.keys.verifiyngKeys.put(other.ident, other.keys.verifiyngKeys.get(other.ident));
    }
    
    public static void deleteProfile(String username) {
        File f = new File(username + ".profile");
        if (f.exists()) {
            f.delete();
        }
    }
    
    public static Profile readProfile(String username, String pw) {
        System.out.println("reading profile for " + username + ", " + pw);
        SecureTransport transport = new SecureTransport(pw);
        return (Profile)transport.readEncryptedFile(username + ".profile");
    }

    public static Profile writeProfile(String username, String pw, int port, String host) {
        System.out.println("creating new profile for " + username + ", " + pw);
        Profile profile = new Profile();
        profile.host = host;
        profile.port = port;
        profile.ident = username;
        profile.keys = new KeysObject();
        KeyPair keys = KeyFactory.generateAsymmetricKeys();
        profile.keys.publicKeys = new ConcurrentHashMap<>();
        profile.keys.publicKeys.put(username, keys.getPublic());
        profile.keys.privateKey = keys.getPrivate();
        KeyPair signingKeyPair = KeyFactory.generateAsymmetricKeys();
        profile.keys.verifiyngKeys = new ConcurrentHashMap<>();
        profile.keys.verifiyngKeys.put(username, signingKeyPair.getPublic());
        profile.keys.signingKey = signingKeyPair.getPrivate();
        profile.save(pw);
        
        profile.keyVersion = profile.keys.publicKeys.get(profile.ident).serialVersionUID;
        return profile;
    }    
}
