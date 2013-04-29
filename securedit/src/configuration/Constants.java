/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

import java.util.logging.Level;
import java.util.logging.Logger;
import security_layer.EncryptedMessage;
import security_layer.SecureTransport;
import security_layer.authentications.SRPAuthenticationTransport;

/**
 *
 * @author Patrick
 */
public class Constants {
    public static final boolean DEBUG_ON = true;
    public static final String passwordCharacterRegex = ".*[^a-zA-Z\\s0-9].*";
    public static final int MIN_PASSWORD_LENGTH = 12;
    
    public static final int PIN_LENGTH = 1;
    public static final String GLOBAL_ENCODING = "UTF-8";
    
    public static final String PIN_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int numBytesPIN = 20 * 8;
    public static final String groupAddress = "230.0.0.1";    //All "clients"(owners of documents) listen on this.
    public static final int discoveryPort = 5446;      //All "clients" use this port.
    public static final String ENCODING = "UTF-8";
    public static final int numTimesBroadcast = 1;
    public static int ALLOWABLE_LABEL_COUNT = 10;
    
    public static void log(String className, String m) {
        if(Constants.DEBUG_ON){
            Logger.getLogger(className).log(Level.INFO, m);
        }
    }
    
    public static void log(String m) {
        if(Constants.DEBUG_ON){
            System.out.println(m);
        }
    }
    
    public static void logger(Class klass, Exception ex) {
        logger(klass, "", ex);      
    }

    public static void logger(Class klass, String msg, Exception ex) {
        if (Constants.DEBUG_ON){
            Logger.getLogger(klass.getName()).log(Level.SEVERE, msg, ex);
        }        
    }

}
