/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

/**
 *
 * @author Patrick
 */
public class Constants {
    public static final boolean DEBUG_ON = true;
    public static final String passwordCharacterRegex = ".*[^a-zA-Z\\s0-9].*";
    
    public static final int PIN_LENGTH = 12;
    public static final String GLOBAL_ENCODING = "UTF-8";
    
    //Ascii without alphanumeric, spaces, and junk stuff don't want.
    public static final String PIN_SYMBOLS_REGEX = ".*[^a-zA-Z\\d\\s].*";
    

    public static final int numBytesPIN = 20 * 8;
    public static final int lengthPIN = 8;
    public static final String groupAddress = "230.0.0.1";    //All "clients"(owners of documents) listen on this.
    public static final int discoveryPort = 5446;      //All "clients" use this port.
    public static final String ENCODING = "UTF-8";
    public static final int numTimesBroadcast = 1;
}
