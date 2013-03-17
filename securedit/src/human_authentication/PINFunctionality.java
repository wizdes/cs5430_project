/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package human_authentication;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.SecretKey;
import security_layer.*;

/**
 *
 * @author Yi
 */
public class PINFunctionality {
    
    private List<String> validPins = new ArrayList<String>();
    
    int numBytesPIN = 20 * 8;
    
    public String getPIN(String seed){
        SecureRandom random = new SecureRandom(seed.getBytes());
        return new BigInteger(numBytesPIN, random).toString(32);
    }
    
    public String getPIN(){
        return getPIN("");
    }
    
    public void storePIN(String PIN){
        validPins.add(PIN);
    }
    
    public boolean checkPIN(String PIN){
        if(validPins.contains(PIN)) return true;
        return false;
    }
    
    public void safeRemove(String PIN){
        if(validPins.contains(PIN)){
            validPins.remove(PIN);
        }
    }
}
