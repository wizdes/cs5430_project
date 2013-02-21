/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RSA_Crypto;

import encryption.RSA_Crypto;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author yjli_000
 */
public class RSA_CryptoTest {
    public RSA_CryptoTest(){
    }
    
    @Test
    public void TestEn_DeCrypt()
    {
        try {
            // TODO code application logic here
            RSA_Crypto test = new RSA_Crypto();
            
            //System.out.println(test.getPrivateKey());
            //System.out.println(test.getPublicKey());
            
            String elt = "This is a test";
            byte[] raw_data = new byte[elt.getBytes("UTF8").length];
            System.arraycopy(elt.getBytes("UTF8"), 0, raw_data, 0, elt.getBytes().length);
            
            assertEquals(new String(raw_data), elt);
            
            byte[] print_data = test.PublicKeyEncrypt(test.getPublicKey(), raw_data);
            
            //System.out.println("Encrypted: " + new String(print_data));
            assertEquals(new String(test.PrivateKeyDecrypt(test.getPrivateKey(), print_data)), elt);
        }
        catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RSA_CryptoTest.class.getName()).log(Level.SEVERE, null, ex);
        }    }
}
