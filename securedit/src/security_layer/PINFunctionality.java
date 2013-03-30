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
    
    public final byte[] seedBytes = new byte[512];
    private final List<String> validPins = Collections.synchronizedList(new ArrayList<String>());
    
    public String getPIN(){
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            random.nextBytes(seedBytes);
            String randomPIN = "";
            while(randomPIN.length() < Constants.lengthPIN){
                randomPIN = new BigInteger(Constants.numBytesPIN, random).toString(Character.MAX_RADIX);
            }
            String retPIN = "";
            //this makes capital letters
            for(int i = 0; i < Constants.lengthPIN; i++){
                char insertPIN = randomPIN.charAt(i);
                int upper = random.nextInt(2);
                if(upper == 1 && !Character.isDigit(insertPIN)){
                    insertPIN = Character.toUpperCase(insertPIN);
                }
                retPIN += insertPIN;
            }
            return retPIN;
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
