/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import _old_stuff.EncryptionDemoGUI;
import configuration.Constants;

/**
 *
 * @author Patrick C. Berens
 */
public class SingleGUIDemoDriver {
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
                java.util.logging.Logger.getLogger(EncryptionDemoGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        //</editor-fold>
        
        if(args.length != 4){
            System.out.println("Usage: java -jar SingleGUIDemoDriver.jar id host port password");
            System.exit(-1);
        }
        EncryptionDemoGUI gui_0 = new EncryptionDemoGUI(args[0], args[1], Integer.parseInt(args[2]), args[3]);
        gui_0.launchGUI();
    }
}
