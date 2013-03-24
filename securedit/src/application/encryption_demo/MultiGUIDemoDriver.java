/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EncryptionDemoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EncryptionDemoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EncryptionDemoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EncryptionDemoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
                
        Profile.deleteProfile("0");
        Profile p1 = Profile.writeProfile("0", "pass0000pass0000", 4000, "localhost");
        EncryptionDemoGUI gui_0 = new EncryptionDemoGUI("0", "localhost", 4000, "pass0000pass0000");
        gui_0.launchGUI();
        
        Profile.deleteProfile("1");
        Profile p2 = Profile.writeProfile("1", "pass1111pass1111", 4001, "localhost");
        EncryptionDemoGUI gui_1 = new EncryptionDemoGUI("1", "localhost", 4001, "pass1111pass1111");
        gui_1.launchGUI();
      
        Profile.deleteProfile("2");
        Profile p3 = Profile.writeProfile("2", "pass2222pass2222", 4002, "localhost");
        EncryptionDemoGUI gui_2 = new EncryptionDemoGUI("2", "localhost", 4002, "pass2222pass2222");
        gui_2.launchGUI();
    }
}
