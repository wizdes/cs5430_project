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
    
    public static final int PIN_LENGTH = 8;
    public static final String GLOBAL_ENCODING = "UTF-8";
    
    //Ascii without alphanumeric, spaces, and junk stuff don't want.
    public static final String PIN_SYMBOLS_REGEX = "[^a-zA-Z\\d\\s]";
    
}
