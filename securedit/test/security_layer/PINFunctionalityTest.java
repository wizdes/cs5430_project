/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import configuration.Constants;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Patrick
 */
public class PINFunctionalityTest {
    @Test
    public void testGetPINSize(){
        PINFunctionality pinFunctionality = new PINFunctionality();
        
        String pin = pinFunctionality.getPIN();
        
        assertEquals(pin.length(), Constants.PIN_LENGTH);
    }
    @Test
    public void testGetPINParams(){
        //Not sure if this will work across all, lets say Linux systems, but
        //  it works for our test machine so that is all that matters.
        String seedToGenLowercase = "this seed should make a lowercase letter appear in the set";
        String seedToGenUppercase = "this seed should make an uppercase letter appear in set";
        String seedToGenDigit = "this seed should make a digit appear";
        String seedToGenSymbol = "this seed should make some symbol appear";
        
        PINFunctionality pinFunctionality = new PINFunctionality();
        String pinWithLowercase = pinFunctionality.getPIN(seedToGenLowercase);
        assertTrue(pinWithLowercase.contains("[a-z]"));
        
        String pinWithUppercase = pinFunctionality.getPIN(seedToGenUppercase);
        assertTrue(pinWithUppercase.contains("[A-Z]"));
        
        String pinWithDigit = pinFunctionality.getPIN(seedToGenDigit);
        assertTrue(pinWithDigit.contains("[0-9]"));
        
        String pinWithSymbol = pinFunctionality.getPIN(seedToGenSymbol);
        assertTrue(pinWithSymbol.contains(Constants.PIN_SYMBOLS_REGEX));
    }
}
