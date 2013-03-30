/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import configuration.Constants;
import java.io.UnsupportedEncodingException;
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
    
    public String getPIN(String seed){
        try {
            SecureRandom random = new SecureRandom(seed.getBytes(Constants.GLOBAL_ENCODING));
            return generatePIN(random);
        } catch (UnsupportedEncodingException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(PINFunctionality.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    public String getPIN(){
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            random.nextBytes(new byte[1]);  //Forces it to seed. Best practice.
            return generatePIN(random);
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            if(Constants.DEBUG_ON){
                Logger.getLogger(PINFunctionality.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }        
    }
    /**
     * General method used to generate PIN of required length with correct
     *   character set. Constants.java has these parameters.
     * @param seededSecureRandom Seeded either manually or with getBytes.
     * @return 
     */
    private String generatePIN(SecureRandom seededSecureRandom){
        return new BigInteger(numBytesPIN, seededSecureRandom).toString(32);
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
