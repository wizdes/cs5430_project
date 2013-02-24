/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import java.util.Arrays;
import javax.crypto.SecretKey;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Patrick C. Berens
 */
public class AESTest {
    
    private AES aes;
    
    public AESTest (){
        String stupidPresharedPass = "IAmAPassword";
        String stupidPresharedSalt = "IAmASalt";
        aes = new AES(stupidPresharedPass, stupidPresharedSalt);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    /**
     * Ensures decrypted plaintext matches original plaintext.
     */
    @Test
    public void testBasicEncryption(){
        String original = "test 1321314123k;389nnvp ion kljdsa";
        byte[] input = new byte[original.length()];
        for (int i = 0; i < original.length(); i++) {
            input[i] = (byte)original.charAt(i);
        }
        
        byte[] ciphertext1 = aes.encrypt(input);
        byte[] plaintext1 = aes.decrypt(ciphertext1);
        
        for (int i = 0; i < original.length(); i++) {
            assertEquals(input[i], plaintext1[i]);
        }
    }    
    
    /**
     * Ensures IV is working as intended by not producing identical output
     *   for same plaintext.
     */
    @Test
    public void testDifferentCiphertext(){
        String original = "test 1321314123k;389nnvp ion kljdsa";
        byte[] input = new byte[original.length()];
        for (int i = 0; i < original.length(); i++) {
            input[i] = (byte)original.charAt(i);
        }
        
        byte[] ciphertext1 = aes.encrypt(input);
        byte[] ciphertext2 = aes.encrypt(input);
        
        // make sure they aren't all the same
        if (ciphertext1.length == ciphertext2.length) {
            boolean theSame = true;
            for (int i = 0; i < ciphertext2.length; i++) {
                theSame = theSame && ciphertext1[i] == ciphertext2[i];
            }
            assertFalse(theSame);
        }
        // if they aren't the same length, they aren't equal, so we're done
    }

    @Test
    public void testGetKey() {
        System.out.println("getKey");
        AES instance = null;
        SecretKey expResult = null;
        SecretKey result = instance.getKey();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testEncrypt_String() {
        System.out.println("encrypt");
        String plaintext = "";
        AES instance = null;
        String expResult = "";
        String result = instance.encrypt(plaintext);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testEncrypt_byteArr() {
        System.out.println("encrypt");
        byte[] rawData = null;
        AES instance = null;
        byte[] expResult = null;
        byte[] result = instance.encrypt(rawData);
        assertArrayEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testDecrypt_String() {
        System.out.println("decrypt");
        String ciphertext = "";
        AES instance = null;
        String expResult = "";
        String result = instance.decrypt(ciphertext);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testDecrypt_byteArr() {
        System.out.println("decrypt");
        byte[] data = null;
        AES instance = null;
        byte[] expResult = null;
        byte[] result = instance.decrypt(data);
        assertArrayEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGenerateKey_String_String() {
        System.out.println("generateKey");
        String password = "";
        String salt = "";
        SecretKey expResult = null;
        SecretKey result = AES.generateKey(password, salt);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGenerateKey_charArr_byteArr() {
        System.out.println("generateKey");
        char[] password = null;
        byte[] salt = null;
        SecretKey expResult = null;
        SecretKey result = AES.generateKey(password, salt);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }
}
