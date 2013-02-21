package encryption;

import encryption_demo.EncryptionDemoFunctionality;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Patrick C. Berens
 */
public class AES {
    private static final String ENCODING = "UTF-8";
    public static final int lengthOfIV = 16;
    //private static final byte[] presharedSalt = "IAmASalt".getBytes();
    
    private SecretKey secret;
    
    public AES(String password, String presharedSalt){
        secret = generateKey(password.toCharArray(), presharedSalt.getBytes());
    }
    
    public String encrypt(String plaintext){
        byte[] ciphertext = encrypt(plaintext.getBytes());
        return DatatypeConverter.printBase64Binary(ciphertext);
    }
    public byte[] encrypt(byte[] rawData){
        byte[] encryptedData = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV(); //16 bytes
            byte[] ciphertext = cipher.doFinal(rawData);
            
            //Concat iv to front of ciphertext
            //  Too slow???
            encryptedData = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(ciphertext, 0, encryptedData, iv.length, ciphertext.length);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidParameterSpecException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encryptedData;
    }
    
    public String decrypt(String ciphertext){
        byte[] plaintext = decrypt(DatatypeConverter.parseBase64Binary(ciphertext));
        try {
            return new String(plaintext, ENCODING);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EncryptionDemoFunctionality.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public byte[] decrypt(byte[] data){
        byte[] result = null;
        //Unpack iv and ciphertext
        byte[] iv = new byte[lengthOfIV];
        byte[] ciphertext = new byte[data.length - lengthOfIV];
        System.arraycopy(data, 0, iv, 0, lengthOfIV);
        System.arraycopy(data, lengthOfIV, ciphertext, 0, ciphertext.length);
        
        try {    
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            result = cipher.doFinal(ciphertext);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    
    
    private SecretKey generateKey(char[] password, byte[] salt) {
        SecretKey key = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");  //SHA2???
            PBEKeySpec spec = new PBEKeySpec(password, salt, 65513, 128);
            SecretKey temp = factory.generateSecret(spec);
            key = new SecretKeySpec(temp.getEncoded(), "AES");
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
        return key;
    }
}