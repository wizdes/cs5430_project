/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Patrick C. Berens
 */
public class AESTest {
    /**
     * Ensures decrypted plaintext matches original plaintext.
     */
    @Test
    public void testBasicEncryption(){
        String stupidPresharedPass = "IAmAPassword";
        String stupidPresharedSalt = "IAmASalt";
        AES aes = new AES(stupidPresharedPass, stupidPresharedSalt);
        
        String original = "test 1321314123k;389nnvp ion kljdsa";
        byte[] ciphertext1 = aes.encrypt(original);
        String plaintext1 = aes.decrypt(ciphertext1);
        
        assertEquals(plaintext1, original);
    }    
    
    /**
     * Ensures IV is working as intended by not producing identical output
     *   for same plaintext.
     */
    @Test
    public void testDifferentCiphertext(){
        String stupidPresharedPass = "IAmAPassword";
        String stupidPresharedSalt = "IAmASalt";
        AES aes = new AES(stupidPresharedPass, stupidPresharedSalt);
        
        String original = "test 1321314123k;389nnvp ion kljdsa";
        byte[] ciphertext1 = aes.encrypt(original);
        byte[] ciphertext2 = aes.encrypt(original);
        String plaintext1 = aes.decrypt(ciphertext1);
        String plaintext2 = aes.decrypt(ciphertext2);
        
        assertFalse(ciphertext1.equals(ciphertext2));
    }
    
    /***************************************************************
     *  EDGE CASES
     **************************************************************/ 
    
    @Test
    public void testEmptyString(){
        String stupidPresharedPass = "IAmAPassword";
        String stupidPresharedSalt = "IAmASalt";
        AES aes = new AES(stupidPresharedPass, stupidPresharedSalt);
        
        String original = "";
        byte[] ciphertext1 = aes.encrypt(original);
        String plaintext1 = aes.decrypt(ciphertext1);
        
        assertEquals(original, plaintext1);
    }
    
    @Test(expected=NullPointerException.class)
    public void testNullPtr(){
        String stupidPresharedPass = "IAmAPassword";
        String stupidPresharedSalt = "IAmASalt";
        AES aes = new AES(stupidPresharedPass, stupidPresharedSalt);
        
        String original = null;
        byte[] ciphertext1 = aes.encrypt(original);
        String plaintext1 = aes.decrypt(ciphertext1);
        
        assertEquals(original, plaintext1);
    }
    
    @Test
    public void testEscapeChars(){
        String stupidPresharedPass = "IAmAPassword";
        String stupidPresharedSalt = "IAmASalt";
        AES aes = new AES(stupidPresharedPass, stupidPresharedSalt);
        
        String original = "\t\b\n\r\f\'\"\\";
        byte[] ciphertext1 = aes.encrypt(original);
        String plaintext1 = aes.decrypt(ciphertext1);
        
        assertEquals(original, plaintext1);
    }
    
    @Test
    public void testNonAsciiChars(){
        String stupidPresharedPass = "IAmAPassword";
        String stupidPresharedSalt = "IAmASalt";
        AES aes = new AES(stupidPresharedPass, stupidPresharedSalt);
        
        String nonAscii = "A função, Ãugent";
        byte[] ciphertext1 = aes.encrypt(nonAscii);
        String plaintext1 = aes.decrypt(ciphertext1);
        
        assertEquals(nonAscii, plaintext1);
        
        
        
    }
}
