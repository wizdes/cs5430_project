/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptionDemoGUI extends javax.swing.JFrame {
    private static String appNodeId = "";
    private static String appHost = "";
    private static int appPort;
    private static String username;
    private static String password;
    
    private String filename;
    private JFileChooser fileChooser;
    private EncryptionDemoFunctionality functionality;
    /**
     * Creates new form EncryptionDemoGUI
     */
    public EncryptionDemoGUI() {
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        initComponents();
        
        NodeIDTextField.setText(appNodeId);
        HostTextField.setText(appHost);
        PortTextField.setText(appPort + "");

        SendTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    SendButton.doClick();
                }
            }
        });
    }
    
    public void displayMessages(String plaintext, String ciphertext){
        ReceivedCiphertextTextArea.setText(ReceivedCiphertextTextArea.getText() + ciphertext + "\n");
        DecryptedPlaintextTextArea.setText(DecryptedPlaintextTextArea.getText() + plaintext + "\n");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CryptographyTabbedPane = new javax.swing.JTabbedPane();
        AuthorizationLabel = new javax.swing.JPanel();
        AuthorizeMachineButton = new javax.swing.JButton();
        NodeIDTextField = new javax.swing.JTextField();
        NodeIDLabel = new javax.swing.JLabel();
        HostLabel = new javax.swing.JLabel();
        HostTextField = new javax.swing.JTextField();
        PortLabel = new javax.swing.JLabel();
        PortTextField = new javax.swing.JTextField();
        UpdatePropertiesButton = new javax.swing.JButton();
        UsernameLabel = new javax.swing.JLabel();
        UsernameTextField = new javax.swing.JTextField();
        PasswordLabel = new javax.swing.JLabel();
        PasswordTextField = new javax.swing.JTextField();
        FilePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        FileTextArea = new javax.swing.JTextArea();
        OpenFileButton = new javax.swing.JButton();
        EncryptButton = new javax.swing.JButton();
        DecryptButton = new javax.swing.JButton();
        FileLabel = new javax.swing.JLabel();
        SentMessagesPanel = new javax.swing.JPanel();
        SendTextField = new javax.swing.JTextField();
        SendButton = new javax.swing.JButton();
        SentMessagesLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        EnteredPlaintext = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        SentCipherText = new javax.swing.JTextArea();
        EncryptedMessagesLabel = new javax.swing.JLabel();
        SentMessagesPanel1 = new javax.swing.JPanel();
        ReceivedCiphertextLabel = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        ReceivedCiphertextTextArea = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        DecryptedPlaintextTextArea = new javax.swing.JTextArea();
        DecryptedPlaintextLabel = new javax.swing.JLabel();
        CryptographyLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        AuthorizeMachineButton.setText("Authorize Machine");
        AuthorizeMachineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AuthorizeMachineButtonActionPerformed(evt);
            }
        });

        NodeIDLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        NodeIDLabel.setText("Node ID");

        HostLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        HostLabel.setText("Host");

        PortLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        PortLabel.setText("Port");

        UpdatePropertiesButton.setText("Update Properties");
        UpdatePropertiesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdatePropertiesButtonActionPerformed(evt);
            }
        });

        UsernameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        UsernameLabel.setText("Username");

        PasswordLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        PasswordLabel.setText("Password");

        javax.swing.GroupLayout AuthorizationLabelLayout = new javax.swing.GroupLayout(AuthorizationLabel);
        AuthorizationLabel.setLayout(AuthorizationLabelLayout);
        AuthorizationLabelLayout.setHorizontalGroup(
            AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AuthorizationLabelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(HostLabel)
                    .addComponent(PortLabel)
                    .addComponent(UsernameLabel)
                    .addGroup(AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(UpdatePropertiesButton)
                        .addGroup(AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AuthorizationLabelLayout.createSequentialGroup()
                                .addComponent(PasswordLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(PasswordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(AuthorizationLabelLayout.createSequentialGroup()
                                .addComponent(NodeIDLabel)
                                .addGap(43, 43, 43)
                                .addGroup(AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(HostTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(NodeIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(PortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(UsernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(42, 482, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AuthorizationLabelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AuthorizeMachineButton)
                .addGap(27, 27, 27))
        );
        AuthorizationLabelLayout.setVerticalGroup(
            AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AuthorizationLabelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NodeIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NodeIDLabel))
                .addGap(9, 9, 9)
                .addGroup(AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HostLabel)
                    .addComponent(HostTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PortLabel)
                    .addComponent(PortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(UsernameLabel)
                    .addComponent(UsernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AuthorizationLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PasswordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PasswordLabel))
                .addGap(18, 18, 18)
                .addComponent(UpdatePropertiesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(AuthorizeMachineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        CryptographyTabbedPane.addTab("Authorization", AuthorizationLabel);

        FileTextArea.setEditable(false);
        FileTextArea.setColumns(20);
        FileTextArea.setRows(5);
        jScrollPane1.setViewportView(FileTextArea);

        OpenFileButton.setText("Open File");
        OpenFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenFileButtonActionPerformed(evt);
            }
        });

        EncryptButton.setText("Encrypt");
        EncryptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EncryptButtonActionPerformed(evt);
            }
        });

        DecryptButton.setText("Decrypt");
        DecryptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DecryptButtonActionPerformed(evt);
            }
        });

        FileLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        FileLabel.setText("File:");

        javax.swing.GroupLayout FilePanelLayout = new javax.swing.GroupLayout(FilePanel);
        FilePanel.setLayout(FilePanelLayout);
        FilePanelLayout.setHorizontalGroup(
            FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FilePanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
                        .addGroup(FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FilePanelLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(EncryptButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(DecryptButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FilePanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(OpenFileButton))))
                    .addGroup(FilePanelLayout.createSequentialGroup()
                        .addComponent(FileLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        FilePanelLayout.setVerticalGroup(
            FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FilePanelLayout.createSequentialGroup()
                .addComponent(FileLabel)
                .addGap(9, 9, 9)
                .addGroup(FilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FilePanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(FilePanelLayout.createSequentialGroup()
                        .addComponent(OpenFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                        .addComponent(EncryptButton)
                        .addGap(18, 18, 18)
                        .addComponent(DecryptButton)
                        .addGap(68, 68, 68))))
        );

        CryptographyTabbedPane.addTab("File", FilePanel);

        SendButton.setText("Send");
        SendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButtonActionPerformed(evt);
            }
        });

        SentMessagesLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        SentMessagesLabel.setText("Entered Plaintext");

        EnteredPlaintext.setEditable(false);
        EnteredPlaintext.setColumns(20);
        EnteredPlaintext.setRows(5);
        jScrollPane3.setViewportView(EnteredPlaintext);

        SentCipherText.setEditable(false);
        SentCipherText.setColumns(20);
        SentCipherText.setRows(5);
        jScrollPane4.setViewportView(SentCipherText);

        EncryptedMessagesLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        EncryptedMessagesLabel.setText("Sent Ciphertext");

        javax.swing.GroupLayout SentMessagesPanelLayout = new javax.swing.GroupLayout(SentMessagesPanel);
        SentMessagesPanel.setLayout(SentMessagesPanelLayout);
        SentMessagesPanelLayout.setHorizontalGroup(
            SentMessagesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SentMessagesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SentMessagesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(SentMessagesPanelLayout.createSequentialGroup()
                        .addComponent(SendTextField)
                        .addGap(18, 18, 18)
                        .addComponent(SendButton))
                    .addGroup(SentMessagesPanelLayout.createSequentialGroup()
                        .addGroup(SentMessagesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(SentMessagesPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(SentMessagesPanelLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(SentMessagesLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(SentMessagesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(EncryptedMessagesLabel)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(30, 30, 30))
        );
        SentMessagesPanelLayout.setVerticalGroup(
            SentMessagesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SentMessagesPanelLayout.createSequentialGroup()
                .addGroup(SentMessagesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SentMessagesLabel)
                    .addComponent(EncryptedMessagesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SentMessagesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SentMessagesPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addGroup(SentMessagesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SendButton)
                            .addComponent(SendTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        CryptographyTabbedPane.addTab("Sent Messages", SentMessagesPanel);

        ReceivedCiphertextLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        ReceivedCiphertextLabel.setText("Received Ciphertext");

        ReceivedCiphertextTextArea.setEditable(false);
        ReceivedCiphertextTextArea.setColumns(20);
        ReceivedCiphertextTextArea.setRows(5);
        jScrollPane5.setViewportView(ReceivedCiphertextTextArea);

        DecryptedPlaintextTextArea.setEditable(false);
        DecryptedPlaintextTextArea.setColumns(20);
        DecryptedPlaintextTextArea.setRows(5);
        jScrollPane6.setViewportView(DecryptedPlaintextTextArea);

        DecryptedPlaintextLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        DecryptedPlaintextLabel.setText("Decrypted Plaintext");

        javax.swing.GroupLayout SentMessagesPanel1Layout = new javax.swing.GroupLayout(SentMessagesPanel1);
        SentMessagesPanel1.setLayout(SentMessagesPanel1Layout);
        SentMessagesPanel1Layout.setHorizontalGroup(
            SentMessagesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SentMessagesPanel1Layout.createSequentialGroup()
                .addGroup(SentMessagesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SentMessagesPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(SentMessagesPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(ReceivedCiphertextLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(SentMessagesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DecryptedPlaintextLabel)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );
        SentMessagesPanel1Layout.setVerticalGroup(
            SentMessagesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SentMessagesPanel1Layout.createSequentialGroup()
                .addGroup(SentMessagesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ReceivedCiphertextLabel)
                    .addComponent(DecryptedPlaintextLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SentMessagesPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        CryptographyTabbedPane.addTab("Received Messages", SentMessagesPanel1);

        CryptographyLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        CryptographyLabel.setText("Cryptography Demonstration");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CryptographyTabbedPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CryptographyLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(CryptographyLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CryptographyTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OpenFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenFileButtonActionPerformed
        int ret = fileChooser.showOpenDialog(EncryptionDemoGUI.this);
        if(ret == JFileChooser.APPROVE_OPTION){
            filename = fileChooser.getSelectedFile().getName();
            FileLabel.setText(filename);
            String filetext = functionality.openFile(filename);
            FileTextArea.setText(filetext);
        }
    }//GEN-LAST:event_OpenFileButtonActionPerformed

    private void EncryptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EncryptButtonActionPerformed
        FileTextArea.setText(functionality.encryptFile(FileTextArea.getText()));
    }//GEN-LAST:event_EncryptButtonActionPerformed

    private void DecryptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DecryptButtonActionPerformed
        FileTextArea.setText(functionality.decryptFile(FileTextArea.getText()));
    }//GEN-LAST:event_DecryptButtonActionPerformed

    private void SendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButtonActionPerformed
        String plaintext = SendTextField.getText();
        String ciphertext = functionality.sendEncryptedMessage(plaintext);
       
        EnteredPlaintext.setText(EnteredPlaintext.getText() + plaintext + "\n");
        SentCipherText.setText(SentCipherText.getText() + ciphertext + "\n");
        SendTextField.setText("");
    }//GEN-LAST:event_SendButtonActionPerformed

    private void UpdatePropertiesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdatePropertiesButtonActionPerformed
        appNodeId = NodeIDTextField.getText();
        appHost = HostTextField.getText();
        username = UsernameTextField.getText();
        password = PasswordTextField.getText();
        try{
            appPort = Integer.parseInt(PortTextField.getText());
        } catch(NumberFormatException ex){
            handleException(ex);
        }
        functionality = new EncryptionDemoFunctionality(this);
    }//GEN-LAST:event_UpdatePropertiesButtonActionPerformed

    private void AuthorizeMachineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AuthorizeMachineButtonActionPerformed
        //Yi, this is where you call your method: functionality.author..
    }//GEN-LAST:event_AuthorizeMachineButtonActionPerformed
    private void handleException(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
        
        if(args.length == 3){
            appNodeId = args[0];
            appHost = args[1];
            appPort = Integer.parseInt(args[2]);
        }
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EncryptionDemoGUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AuthorizationLabel;
    private javax.swing.JButton AuthorizeMachineButton;
    private javax.swing.JLabel CryptographyLabel;
    private javax.swing.JTabbedPane CryptographyTabbedPane;
    private javax.swing.JButton DecryptButton;
    private javax.swing.JLabel DecryptedPlaintextLabel;
    private javax.swing.JTextArea DecryptedPlaintextTextArea;
    private javax.swing.JButton EncryptButton;
    private javax.swing.JLabel EncryptedMessagesLabel;
    private javax.swing.JTextArea EnteredPlaintext;
    private javax.swing.JLabel FileLabel;
    private javax.swing.JPanel FilePanel;
    private javax.swing.JTextArea FileTextArea;
    private javax.swing.JLabel HostLabel;
    private javax.swing.JTextField HostTextField;
    private javax.swing.JLabel NodeIDLabel;
    private javax.swing.JTextField NodeIDTextField;
    private javax.swing.JButton OpenFileButton;
    private javax.swing.JLabel PasswordLabel;
    private javax.swing.JTextField PasswordTextField;
    private javax.swing.JLabel PortLabel;
    private javax.swing.JTextField PortTextField;
    private javax.swing.JLabel ReceivedCiphertextLabel;
    private javax.swing.JTextArea ReceivedCiphertextTextArea;
    private javax.swing.JButton SendButton;
    private javax.swing.JTextField SendTextField;
    private javax.swing.JTextArea SentCipherText;
    private javax.swing.JLabel SentMessagesLabel;
    private javax.swing.JPanel SentMessagesPanel;
    private javax.swing.JPanel SentMessagesPanel1;
    private javax.swing.JButton UpdatePropertiesButton;
    private javax.swing.JLabel UsernameLabel;
    private javax.swing.JTextField UsernameTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    // End of variables declaration//GEN-END:variables
}
