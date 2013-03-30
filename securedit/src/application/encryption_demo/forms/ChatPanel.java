/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo.forms;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Patrick
 */
public class ChatPanel extends javax.swing.JPanel {
    /**
     * Creates new form ChatPanel
     */
    public ChatPanel() {
        initComponents();
        SendChatTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    SendChatButton.doClick();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane7 = new javax.swing.JScrollPane();
        chatWindowTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        PeersList = new javax.swing.JList();
        SendChatTextField = new javax.swing.JTextField();
        SendChatButton = new javax.swing.JButton();
        PeersLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        PINList = new javax.swing.JList();

        setPreferredSize(new java.awt.Dimension(825, 428));

        chatWindowTextArea.setEditable(false);
        chatWindowTextArea.setColumns(20);
        chatWindowTextArea.setRows(5);
        jScrollPane7.setViewportView(chatWindowTextArea);

        PeersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(PeersList);

        SendChatTextField.setName("enteredPlainText"); // NOI18N

        SendChatButton.setText("Send");
        SendChatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendChatButtonActionPerformed(evt);
            }
        });

        PeersLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        PeersLabel.setText("Peers(not shown currently)");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Chat");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("PINs");

        PINList.setToolTipText("");
        jScrollPane1.setViewportView(PINList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(SendChatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 3, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PeersLabel)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SendChatButton)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(PeersLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SendChatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SendChatButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void displayMessages(String plaintext){
        chatWindowTextArea.setText(chatWindowTextArea.getText() + plaintext + "\n");
    }    
    
    private void SendChatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendChatButtonActionPerformed
        ChatWindow chatWindow = (ChatWindow) SwingUtilities.getWindowAncestor(this);
        
        String plaintext = SendChatTextField.getText();

        //Send message
        int currentTabIndex = chatWindow.tabbedPane.getSelectedIndex();
        String docID = chatWindow.docIDs.get(currentTabIndex);

        boolean encryptionAndSendSuccessful = chatWindow.functionality.sendRequestDocUpdate(docID, chatWindow.profile.ident + ": " + plaintext);

        //Update GUI if message sent successfully
        if(encryptionAndSendSuccessful){
            SendChatTextField.setText("");
        } else{
            JOptionPane.showMessageDialog(chatWindow, "Message: \"" +  plaintext + "\" failed to encrypt and broadcast.");
        }
    }//GEN-LAST:event_SendChatButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList PINList;
    private javax.swing.JLabel PeersLabel;
    private javax.swing.JList PeersList;
    private javax.swing.JButton SendChatButton;
    private javax.swing.JTextField SendChatTextField;
    private javax.swing.JTextArea chatWindowTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    // End of variables declaration//GEN-END:variables
}
