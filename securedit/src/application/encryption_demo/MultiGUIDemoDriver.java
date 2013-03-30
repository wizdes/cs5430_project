/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.encryption_demo.forms.LoginForm;
import configuration.Constants;
import security_layer.Profile;

/**
 *
 * @author Patrick C. Berens
 */
public class MultiGUIDemoDriver {
    public static void main(String[] args){
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            if(Constants.DEBUG_ON){
                java.util.logging.Logger.getLogger(MultiGUIDemoDriver.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        //</editor-fold>
                
        new LoginForm().setVisible(true);
        new LoginForm().setVisible(true);
       
//        Profile.deleteProfile("2");
//        Profile p3 = Profile.writeProfile("2", "pass2222pass2222", 4002, "localhost");
//        ChatWindow form3 = new ChatWindow(p3, "pass2222pass2222");
//        form3.setVisible(true); 
    }
}
