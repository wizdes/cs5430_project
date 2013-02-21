/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

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
    public byte[] PublicKeyEncrypt(PublicKey pk, byte[] raw_data);
    public byte[] PrivateKeyDecrypt(PrivateKey pk, byte[] encrypted_data);
}
