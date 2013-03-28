/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import configuration.Constants;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yi
 */
public class PINFunctionality {
    
    private final List<String> validPins = Collections.synchronizedList(new ArrayList<String>());
    
    int numBytesPIN = 20 * 8;
    
    public String getPIN(){
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            return new BigInteger(numBytesPIN, random).toString(32);
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(PINFunctionality.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    
    public void storePIN(String PIN){
        validPins.add(PIN);
    }
    
    public boolean checkPIN(String PIN){
        return validPins.contains(PIN);
    }
    
    public void safeRemove(String PIN){
        synchronized(validPins){
            if(validPins.contains(PIN)){
                validPins.remove(PIN);
            }
        }
    }
}
