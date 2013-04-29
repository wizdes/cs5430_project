/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo.forms;

import application.encryption_demo.DiscoveredPeers;
import application.encryption_demo.EncryptionDemoFunctionality;
import configuration.Constants;
import document.NetworkDocumentHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import security_layer.Profile;

/**
 *
 * @author goggin
 */
public class ApplicationWindow extends javax.swing.JFrame {
    EncryptionDemoFunctionality functionality;
    Profile profile;
    ConcurrentMap<Integer, String> docIDs = new ConcurrentHashMap<>();   //<tab index, docID>
    ConcurrentMap<String, EditPanel> chatPanels = new ConcurrentHashMap<>();    //<docID, chatPanel>
    
    // this is the default settings. 
    public static void main(String[] args) {
        String username = "0";
        String ip = "localhost";
        int port = 6000;
        ApplicationWindow form = new ApplicationWindow(username, ip, port);
        form.setVisible(true);
    }
    
    /**
     * Creates new form ChatWindow
     */
    // creates this class with the proper elements
    public ApplicationWindow(String username, String ip, int port) {
        profile = new Profile(username, ip, port);
        this.functionality =  new EncryptionDemoFunctionality(this, profile);
        initComponents();
        this.setTitle("Secure Document Viewer - User: " + profile.username);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        peerPanel = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        DiscoveredPeersTable = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;   //Disallow the editing of any cell
            }
        };
        DiscoverPeersButton = new javax.swing.JButton();
        addDefaultPeersButton = new javax.swing.JButton();
        startChatButton = new javax.swing.JButton();
        addManualPeer = new javax.swing.JButton();
        Close = new javax.swing.JButton();
        loginButton = new javax.swing.JButton();
        createAccountButton = new javax.swing.JButton();
        peerLabel = new javax.swing.JLabel();
        clientLabel = new javax.swing.JLabel();
        ownerLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabbedPane.setPreferredSize(new java.awt.Dimension(900, 500));

        peerPanel.setToolTipText("");

        DiscoveredPeersTable.setModel(new DefaultTableModel(new String[]{"ID", "IP", "Port", "Document"}, 0));
        DiscoveredPeersTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(DiscoveredPeersTable);

        DiscoverPeersButton.setText("Discover Peers");
        DiscoverPeersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiscoverPeersButtonActionPerformed(evt);
            }
        });

        addDefaultPeersButton.setText("Add Default Peers");
        addDefaultPeersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDefaultPeersButtonActionPerformed(evt);
            }
        });

        startChatButton.setText("Start Document");
        startChatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startChatButtonActionPerformed(evt);
            }
        });

        addManualPeer.setText("Add Peer Manually");
        addManualPeer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addManualPeerActionPerformed(evt);
            }
        });

        Close.setText("Close Program");

        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        createAccountButton.setText("Create Account");
        createAccountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createAccountButtonActionPerformed(evt);
            }
        });

        peerLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        peerLabel.setText("Peer");

        clientLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        clientLabel.setText("Document Client");

        ownerLabel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        ownerLabel.setText("Document Owner");

        org.jdesktop.layout.GroupLayout peerPanelLayout = new org.jdesktop.layout.GroupLayout(peerPanel);
        peerPanel.setLayout(peerPanelLayout);
        peerPanelLayout.setHorizontalGroup(
            peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(peerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(addDefaultPeersButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(addManualPeer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(DiscoverPeersButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, peerLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 445, Short.MAX_VALUE)
                .add(peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(clientLabel)
                    .add(loginButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(createAccountButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(30, 30, 30)
                .add(peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ownerLabel)
                    .add(peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, Close, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, startChatButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .add(41, 41, 41))
            .add(peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(peerPanelLayout.createSequentialGroup()
                    .add(11, 11, 11)
                    .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE)
                    .add(12, 12, 12)))
        );
        peerPanelLayout.setVerticalGroup(
            peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(peerPanelLayout.createSequentialGroup()
                .addContainerGap(348, Short.MAX_VALUE)
                .add(peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(peerLabel)
                    .add(clientLabel)
                    .add(ownerLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(DiscoverPeersButton)
                    .add(loginButton)
                    .add(startChatButton))
                .add(12, 12, 12)
                .add(peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addManualPeer)
                    .add(createAccountButton)
                    .add(Close))
                .add(10, 10, 10)
                .add(addDefaultPeersButton)
                .addContainerGap())
            .add(peerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(peerPanelLayout.createSequentialGroup()
                    .add(22, 22, 22)
                    .add(jScrollPane8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 300, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(150, Short.MAX_VALUE)))
        );

        tabbedPane.addTab("Peers", peerPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // this will update the peers that are available
    public synchronized void updateDiscoveredPeers(DiscoveredPeers discoveredPeers){
        //Clear table and repopulate it
        ((DefaultTableModel) DiscoveredPeersTable.getModel()).setRowCount(0);
        for (DiscoveredPeers.Peer peer : discoveredPeers.peers.values()) {
            Object[][] rows = peer.getRowRepresentations();
            for (int i = 0; i < rows.length; i++) {
                ((DefaultTableModel) DiscoveredPeersTable.getModel()).addRow(rows[i]);
            }
        }
    }
    
    // displays message in the specified panel's document Area
    public void displayMessages(String docID, String plaintext){
        EditPanel panel = chatPanels.get(docID);
        if(panel != null){
            panel.displayMessages(plaintext);
        }
    }    
    
    private void DiscoverPeersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DiscoverPeersButtonActionPerformed
        functionality.broadcastDiscovery();
    }//GEN-LAST:event_DiscoverPeersButtonActionPerformed

    private void addDefaultPeersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDefaultPeersButtonActionPerformed
        ArrayList<String> documents = new ArrayList<>();
        documents.add("Doc1");
        
        for (int i = 0; i < 3; i++) {
            if (!(i + "").equals(this.profile.username)) {
                int port = 6000 + i;
                String host = "localhost";
                functionality.manuallyAddPeer(i + "", host, port, documents);                
            }
        }
    }//GEN-LAST:event_addDefaultPeersButtonActionPerformed

    private void startChatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startChatButtonActionPerformed
        //Prompt for document name - make sure it is unique
        String docName = "Doc1"; //the default document name
        String docID = null;
        NetworkDocumentHandler nd = null;
        while(docID == null){
            String enteredDocName = JOptionPane.showInputDialog("Enter document name", docName);
            if(enteredDocName == null){
                return;
            }
            docName = enteredDocName.trim();
            if(docName.isEmpty()){
                continue;
            }
            
            //docID = this.functionality.createDocumentInstance(profile.username, docName);
            // this allows for a document to be created/propagated/sent
            nd = new NetworkDocumentHandler(
                functionality.getCommunicationInterface(), profile.username, profile.username, docName );
            docID = this.functionality.createDocumentInstance(nd);
            if(docID == null){
                showMessage("The document name: " + docName + " is already in use.");
            }
        }
        
        //Create a new chat panel
        docIDs.put(this.tabbedPane.getTabCount(), docID);
        this.profile.documentsOpenForDiscovery.add(docName);
        
        EditPanel panel = new EditPanel(this.functionality);
        panel.giveDocument(nd);
        nd.giveGUI(panel);
        chatPanels.put(docID, panel);
        this.tabbedPane.add("Owner: " + profile.username + ", Doc: " + docName, panel);
        this.tabbedPane.setSelectedComponent(panel);
    }//GEN-LAST:event_startChatButtonActionPerformed
    
    public void closeEditingSession(NetworkDocumentHandler document) {
        EditPanel panel = chatPanels.get(document.getName());
        chatPanels.remove(document.getName());
        this.tabbedPane.remove(panel);
    }
    
    private void addManualPeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addManualPeerActionPerformed
        // allows a user to manually input an action
        String id = JOptionPane.showInputDialog("Username:");
        if (id == null || id.trim().equals("")) {
            return;
        }
        String host = JOptionPane.showInputDialog("Host:", "localhost");
        if (host == null || host.trim().equals("")) {
            return;
        }        
        int port = 0;
        try {
            port = Integer.parseInt(JOptionPane.showInputDialog("Port:"));
        } catch (java.lang.NumberFormatException e) {
            return;
        }
        String docName = JOptionPane.showInputDialog("Document Name:");
        if (docName == null || docName.trim().equals("")) {
            return;
        }      
        ArrayList<String> documents = new ArrayList<>();
        documents.add(docName);
        functionality.manuallyAddPeer(id.trim(), host.trim(), port, documents);
    }//GEN-LAST:event_addManualPeerActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        // allows a user to log into a server hosting a document
        int selectedRow = this.DiscoveredPeersTable.getSelectedRow();
        if(selectedRow < 0) return;
        String ownerId = (String)this.DiscoveredPeersTable.getModel().getValueAt(selectedRow, 0);
        String docName = (String)this.DiscoveredPeersTable.getModel().getValueAt(selectedRow, 3);
        
        DocumentLoginPanel loginForm = new DocumentLoginPanel(profile.username);

        Object[] options = {"Login"};
        int r = JOptionPane.showOptionDialog(
                this,
                loginForm, 
                "Login",
                JOptionPane.OK_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
        
        if (r != JOptionPane.OK_OPTION) {
            System.out.println("cancelled");
            return;
        }
        char[] password = loginForm.passwordTextField.getPassword();
        if (!functionality.authenticate(ownerId, docName, password)){
            showMessage("Failed to login. Authentication wasn't successful.");
            return;
        }
        
        //Create document instance and send join request for doc
        NetworkDocumentHandler nd = new NetworkDocumentHandler(
            functionality.getCommunicationInterface(), profile.username, ownerId, docName );
        
        /* The order of these is important!! -- MPG */
        
        String docID = this.functionality.createDocumentInstance(nd);
        docIDs.put(this.tabbedPane.getTabCount(), docID);
        
        EditPanel panel = new EditPanel(functionality);        
        chatPanels.put(docID, panel);
        panel.giveDocument(nd);
        nd.giveGUI(panel);
        nd.bootstrap();
        
        if(!this.functionality.sendJoinRequestMessage(ownerId, docName)){
            showMessage("Join chat request failed to send!");
            return;
        }
        
        /* The order of these is important!! -- MPG */
        
        this.tabbedPane.add("Owner: " + ownerId + ", Doc: " + docName, panel);
        this.tabbedPane.setSelectedComponent(panel);
    }//GEN-LAST:event_loginButtonActionPerformed

    private void createAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAccountButtonActionPerformed
        //creates a user account on a server for a specific document
        int selectedRow = this.DiscoveredPeersTable.getSelectedRow();
        if(selectedRow < 0) return;
        String ownerId = (String)this.DiscoveredPeersTable.getModel().getValueAt(selectedRow, 0);
        String docName = (String)this.DiscoveredPeersTable.getModel().getValueAt(selectedRow, 3);
        
        CreateAccountPanel createAccountPanel = new CreateAccountPanel(profile.username);

        Object[] options = {"Create Account"};
        int r = JOptionPane.showOptionDialog(
                this,
                createAccountPanel, 
                "Create Account",
                JOptionPane.OK_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
        
        if (r != JOptionPane.OK_OPTION) {
            System.out.println("Cancel");
            return;
        }
            
        char[] newPassword = createAccountPanel.passwordTextField.getPassword();
        char[] retypedPassword = createAccountPanel.retypePasswordTextField.getPassword();
        char[] pin = createAccountPanel.pinTextField.getPassword();
        
        if(!Arrays.equals(newPassword, retypedPassword)) {
            JOptionPane.showMessageDialog(this, "The two passwords you entered do not match.");
            return;
        }
        if (!isValidPassword(newPassword)) {
            String message = "Passwords must contain >= 1 non alphanumeric character, be 12 or more characters in length, and contain no spaces.";
            JOptionPane.showMessageDialog(this, message);
            return;
        }
        
        if(functionality.initializeSRPAuthentication(ownerId, docName, newPassword, pin)){
            showMessage("Account Created");
        } else{
            showMessage("Account wasn't created. An error occured during setup with server.");
        }
    }//GEN-LAST:event_createAccountButtonActionPerformed

    // checks to see if the password is valid with our criteria
    // (upper, lower, and digits)
    private boolean isValidPassword(char[] pass) {
        
        boolean containsLowerCase = false;
        boolean containsUpperCase = false;
        boolean containsDigit = false;
    
        for(char c: pass){
            containsLowerCase   = containsLowerCase || Character.isLowerCase(c);
            containsUpperCase   = containsUpperCase || Character.isUpperCase(c);
            containsDigit       = containsDigit || Character.isDigit(c); 
        }
        return pass.length >= Constants.MIN_PASSWORD_LENGTH && containsLowerCase && containsUpperCase && containsDigit;
    }
    
    private void showMessage(String m) {
        JOptionPane.showMessageDialog(this, m);
    }
    
    private void handleException(Exception ex) {
        showMessage(ex.getMessage());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Close;
    private javax.swing.JButton DiscoverPeersButton;
    private javax.swing.JTable DiscoveredPeersTable;
    private javax.swing.JButton addDefaultPeersButton;
    private javax.swing.JButton addManualPeer;
    private javax.swing.JLabel clientLabel;
    private javax.swing.JButton createAccountButton;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JButton loginButton;
    private javax.swing.JLabel ownerLabel;
    private javax.swing.JLabel peerLabel;
    private javax.swing.JPanel peerPanel;
    private javax.swing.JButton startChatButton;
    javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

}
