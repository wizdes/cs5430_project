/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RSA_Crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * @author Yi
 */
interface RSA_Crypto_Interface {
    public boolean genNewKeys();
    public PrivateKey getPrivateKey();
    public PublicKey getPublicKey();
}
