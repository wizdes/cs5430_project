/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package securedit_main;

import RSA_Crypto.RSA_Crypto;

/**
 *
 * @author Yi
 */
public class Securedit_Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        RSA_Crypto test = new RSA_Crypto();
        System.out.println(test.getPrivateKey());
        System.out.println(test.getPublicKey());
    }
}
