/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.encryption_demo.forms.ChatWindow;
import application.encryption_demo.forms.LoginForm;
import configuration.Constants;
import security_layer.Profile;

/**
 *
 * @author Patrick C. Berens
 */
public class MultiGUIDemoDriver {
    public static void main(String[] args){
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            if(Constants.DEBUG_ON){
//                java.util.logging.Logger.getLogger(MultiGUIDemoDriver.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//            }
//        }
//        //</editor-fold>
//        
//        Profile.deleteProfile("0");
//        Profile.deleteProfile("1");
//        
//        Profile p1 = Profile.writeProfile("0", "pass0000pass000!", 5000, "localhost");
//        Profile p2 = Profile.writeProfile("1", "pass0000pass000!", 5001, "localhost");
//        
//        p1.addPublicKeysFrom(p2);
//        p2.addPublicKeysFrom(p1);
//        
//        p1.save("pass0000pass000!");
//        p2.save("pass0000pass000!");
        
        new LoginForm().setVisible(true);
        new LoginForm().setVisible(true);

//        Profile.deleteProfile("0");
//        Profile p1 = Profile.writeProfile("0", "pass0000pass0000", 4000, "localhost");
//        ChatWindow form1 = new ChatWindow(p1, "pass0000pass0000");
//        form1.setVisible(true); 
//        
//        Profile.deleteProfile("1");
//        Profile p2 = Profile.writeProfile("1", "pass1111pass1111", 4001, "localhost");
//        ChatWindow form2 = new ChatWindow(p2, "pass1111pass1111");
//        form2.setVisible(true); 
        
//        Profile.deleteProfile("2");
//        Profile p3 = Profile.writeProfile("2", "pass2222pass2222", 4002, "localhost");
//        ChatWindow form3 = new ChatWindow(p3, "pass2222pass2222");
//        form3.setVisible(true); 
    }
}
