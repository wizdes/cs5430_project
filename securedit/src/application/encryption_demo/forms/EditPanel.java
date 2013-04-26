/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo.forms;

import _old_stuff.EncryptionDemoGUI;
import application.encryption_demo.CustomDocument;
import application.encryption_demo.EncryptionDemoFunctionality;
import document.AuthorizationDocument;
import document.DocumentValue;
import document.NetworkDocument;
import document.NetworkDocumentInterface;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Patrick
 */
public class EditPanel extends javax.swing.JPanel {
    /**
     * Creates new form ChatPanel
     */
    private EncryptionDemoFunctionality functionality = null;
    public void addColor(Color c){
        SimpleAttributeSet aset = new SimpleAttributeSet();
        StyleConstants.setForeground(aset, c);
        colors.add(aset);
    }
        
    public void populateColorsList(ArrayList<Color> colors, ArrayList<String> labels){
        this.labels = labels;
        for (String l : labels) {
            LevelSelect.addItem(l);
            ownerWriteLevel.addItem(l);
        }
        
        String[] elements = new String[labels.size()];
        elements = labels.toArray(elements);
        jList1.setListData(elements);
        jList1.setCellRenderer(cr);

        cr.giveColorList(colors);
        for(Color c:colors){
            addColor(c);
        }
    }
    
    public void setDefaultColorsAndLabels() {
        nd.addColor(Color.black);
        nd.addColor(Color.blue);
        nd.addColor(Color.green);
        nd.addColor(Color.red);
        
        nd.addLabel("NORMAL");
        nd.addLabel("PRIVILEGED");
        nd.addLabel("SECRET");
        nd.addLabel("TOP SECRET");
        populateColorsList(nd.getColors(), nd.getLabels());
    }
    
    private HashMap<String, Color> getColorChoices() {
                
        HashMap<String, Color> colorChoices = new HashMap<>();
        colorChoices.put("black", Color.black);
        colorChoices.put("blue", Color.blue);
        colorChoices.put("green", Color.green);
        colorChoices.put("red", Color.red);
        colorChoices.put("gray", Color.GRAY);
        colorChoices.put("white", Color.white);
        colorChoices.put("cyan", Color.CYAN);
        
        return colorChoices;
    }
    
    private String getColorChoicePrompt(HashMap<String, Color> colorChoices) {
        String colorChoiceList = "(";
        for (String c : colorChoices.keySet()) {
            colorChoiceList += c + ", ";
        }
        colorChoiceList = colorChoiceList.substring(0, colorChoiceList.length() - 2) + ")";
        String colorPrompt = "choose a color for this group '\n" + colorChoiceList + " : ";
        
        return colorPrompt;
    }
    
    private void setupColorsAndLabels() {
        ArrayList<Color> defaultColors = new ArrayList<>();
        ArrayList<String> defaultLabels = new ArrayList<>();

        HashMap<String, Color> colorChoices = getColorChoices();

        String labelPrompt = "Enter a security group label (type 'done' when finished)";
        String label = JOptionPane.showInputDialog(labelPrompt);
        while (!label.equals("done")) {
            nd.addLabel(label);
            label = JOptionPane.showInputDialog(labelPrompt);
        }
        
        for (String l : defaultLabels) {
            String colorPrompt = getColorChoicePrompt(colorChoices);
            String colorName = JOptionPane.showInputDialog(l + " ~> " + colorPrompt);
            Color color = colorChoices.get(colorName);
            while (color == null) {
                JOptionPane.showMessageDialog(this, "Invalid color selection");
                colorName = JOptionPane.showInputDialog(l + " ~> " + colorPrompt);
                color = colorChoices.get(colorName);                
            }
            colorChoices.remove(colorName);
            nd.addColor(color);
        }
        populateColorsList(nd.getColors(), nd.getLabels());
    }
    
    public void promptForLevelsAndColors() {
        int r = JOptionPane.showConfirmDialog(this, "Would you like to use the default levels?");
        if (r == JOptionPane.OK_OPTION) {
            setDefaultColorsAndLabels();
        } else {
            setupColorsAndLabels();
        }
    }    
    
    public EditPanel(EncryptionDemoFunctionality functionality) {
        this.functionality = functionality;
        initComponents();
        peerModel = new DefaultListModel();
        cr = new newCellRenderer();
        labels = new ArrayList<String>();
        colors = new ArrayList<SimpleAttributeSet>();
        
        PeersList.setModel(peerModel);

        cd = new CustomDocument();
        documentArea.setDocument(cd);
        cd.setEditorReference(this);
        
        displayedUsername = new ArrayList<>();
        
    }
    
    public void giveDocument(NetworkDocument nd){
        
        this.nd = nd;
        cd.giveDocument(nd);
        peerModel.addElement(nd.getOwnerID() + " - Document Owner");
        displayedUsername.add(nd.getOwnerID());
        if (nd.isOwner()) {
            promptForLevelsAndColors();
        } else if (!nd.isOwner()) {
            displayedUsername.add(nd.getUserID());
            beginCursor.setEnabled(false);
            endCursor.setEnabled(false);
            LevelSelect.setEnabled(false);
            setLevelButton.setEnabled(false);
            generatePINButton.setEnabled(false);
            changeUserLevel.setText("Request Change Level");
        }
    }

    public void handleBootstrap(AuthorizationDocument ad) {
        nd.setAuthDocument(ad);
        this.giveDocument(nd);
        labels = nd.getLabels();
        System.out.println(nd.getLabels());
        System.out.println(nd.getColors());
        this.populateColorsList(nd.getColors(), nd.getLabels());
        peerModel.addElement(nd.getUserID() + " - " + labels.get(cd.insertLevel));
        this.repaint(ad);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        PeersList = new javax.swing.JList();
        PeersLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        documentArea = new javax.swing.JTextPane();
        cursorInfo = new javax.swing.JLabel();
        beginCursor = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        endCursor = new javax.swing.JTextField();
        LevelSelect = new javax.swing.JComboBox();
        setLevelButton = new javax.swing.JToggleButton();
        LevelList = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        LevelLabel = new javax.swing.JLabel();
        changeUserLevel = new javax.swing.JButton();
        openFile = new javax.swing.JButton();
        openNormalFile = new javax.swing.JButton();
        saveEncryptedFile = new javax.swing.JButton();
        DisconnectButton = new javax.swing.JButton();
        ownerWriteLevel = new javax.swing.JComboBox();
        generatePINButton = new javax.swing.JButton();

        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });

        setPreferredSize(new java.awt.Dimension(825, 450));

        PeersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(PeersList);

        PeersLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        PeersLabel.setText("Peers(not shown currently)");

        documentArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                documentAreaCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(documentArea);

        cursorInfo.setText("Cursor At: ");

        beginCursor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beginCursorActionPerformed(evt);
            }
        });

        jLabel1.setText("to");

        LevelSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LevelSelectActionPerformed(evt);
            }
        });

        setLevelButton.setText("Set Level");
        setLevelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                setLevelButtonMousePressed(evt);
            }
        });

        LevelList.setViewportView(jList1);

        LevelLabel.setText("Levels");

        changeUserLevel.setText("Change User Level");
        changeUserLevel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                changeUserLevelMousePressed(evt);
            }
        });
        changeUserLevel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                changeUserLevelKeyPressed(evt);
            }
        });

        openFile.setText("Open Encrypted File");
        openFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                openFileMousePressed(evt);
            }
        });

        openNormalFile.setText("Open Normal File");
        openNormalFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                openNormalFileMousePressed(evt);
            }
        });
        openNormalFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openNormalFileActionPerformed(evt);
            }
        });

        saveEncryptedFile.setText("Save Encrypted File");
        saveEncryptedFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                saveEncryptedFileMousePressed(evt);
            }
        });

        DisconnectButton.setText("Disconnect");

        ownerWriteLevel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ownerWriteLevelItemStateChanged(evt);
            }
        });

        generatePINButton.setText("Generate PIN");
        generatePINButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generatePINButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(openFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openNormalFile)
                        .addGap(18, 18, 18)
                        .addComponent(saveEncryptedFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(generatePINButton)))
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(ownerWriteLevel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DisconnectButton))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(beginCursor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(endCursor, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LevelSelect, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(LevelList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PeersLabel)
                            .addComponent(cursorInfo)
                            .addComponent(LevelLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(changeUserLevel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(setLevelButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PeersLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cursorInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(beginCursor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(endCursor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LevelSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(setLevelButton)
                            .addComponent(changeUserLevel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LevelLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LevelList, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(DisconnectButton)
                            .addComponent(ownerWriteLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(openFile)
                            .addComponent(openNormalFile)
                            .addComponent(saveEncryptedFile)
                            .addComponent(generatePINButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void documentAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_documentAreaCaretUpdate
        // TODO add your handling code here:
        cursorInfo.setText("Cursor at: " + evt.getDot());
    }//GEN-LAST:event_documentAreaCaretUpdate

    private void beginCursorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beginCursorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_beginCursorActionPerformed

    private void LevelSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LevelSelectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LevelSelectActionPerformed

    private void setLevelButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setLevelButtonMousePressed
        // TODO add your handling code here:
        if(beginCursor.getText().equals("") || endCursor.getText().equals("")){
            return;
        }
        int beginCursorInt = Integer.parseInt(beginCursor.getText());
        int endCursorInt = Integer.parseInt(endCursor.getText());
        int colorPosition = LevelSelect.getSelectedIndex();
        AttributeSet s = colors.get(colorPosition);
        //send it to everyone else
        //setColors(beginCursorInt, endCursorInt - beginCursorInt, colorPosition);
        nd.assignLevel(colorPosition, beginCursorInt, endCursorInt);
    }//GEN-LAST:event_setLevelButtonMousePressed

    private void changeUserLevelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_changeUserLevelKeyPressed
        // TODO add your handling code here:

        
    }//GEN-LAST:event_changeUserLevelKeyPressed

    private void changeUserLevelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeUserLevelMousePressed
        // TODO add your handling code here:
        System.out.println("Pressed Button");
        ChangeRequestAccess jf = new ChangeRequestAccess();
        jf.setLabelsandUsername(labels, displayedUsername, nd);
        jf.setVisible(true);
    }//GEN-LAST:event_changeUserLevelMousePressed

    private void ownerWriteLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ownerWriteLevelItemStateChanged
        // TODO add your handling code here:
        if (nd != null && nd.isOwner()){
            cd.insertLevel = ownerWriteLevel.getSelectedIndex();
            System.out.println("Selected: " + ownerWriteLevel.getSelectedIndex());
            System.out.println("Level: " + cd.insertLevel);
        }
    }//GEN-LAST:event_ownerWriteLevelItemStateChanged

    private void openNormalFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openNormalFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_openNormalFileActionPerformed

    private void openNormalFileMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openNormalFileMousePressed
        jFileChooser1.setVisible(true);
        int returnVal = jFileChooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = jFileChooser1.getSelectedFile();
                documentArea.removeAll();
                documentArea.read(new FileReader( file.getAbsolutePath() ), null);
            } catch (IOException ex) {
                Logger.getLogger(EditPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_openNormalFileMousePressed

    private void generatePINButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generatePINButtonActionPerformed
        String username = JOptionPane.showInputDialog("Enter the username:").trim();
        if (username.isEmpty()) {
            return;
        }
        char[] pin = this.functionality.generatePIN(username, nd.getName());
        System.out.println("PIN: " + new String(pin));
        new PINDisplayDialog(username, pin).setVisible(true);
    }//GEN-LAST:event_generatePINButtonActionPerformed

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        // TODO add your handling code here:
        //System.out.println("File Selected");
    }//GEN-LAST:event_jFileChooser1ActionPerformed

    private void saveEncryptedFileMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveEncryptedFileMousePressed
        // TODO add your handling code here:
        //Using a JPanel as the message for the JOptionPane
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new GridLayout(2,2));

        JLabel passwordLbl = new JLabel("Password:");
        JPasswordField passwordFld = new JPasswordField();
        char[] password = passwordFld.getPassword();

        userPanel.add(passwordLbl);
        userPanel.add(passwordFld);

        //As the JOptionPane accepts an object as the message
        //it allows us to use any component we like - in this case 
        //a JPanel containing the dialog components we want
        int input = JOptionPane.showConfirmDialog(null, userPanel, "Enter your password:"
                      ,JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        jFileChooser1.setVisible(true);
        int returnVal = jFileChooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser1.getSelectedFile();
            String fileName = file.getAbsolutePath();
            functionality.encryptFile(fileName, nd.getAuthDocument(), password);
        } else {
            System.out.println("File access cancelled by user.");
        }        
    }//GEN-LAST:event_saveEncryptedFileMousePressed

    private void openFileMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openFileMousePressed
        // TODO add your handling code here:
        // TODO add your handling code here:
        //Using a JPanel as the message for the JOptionPane

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new GridLayout(2,2));

        JLabel passwordLbl = new JLabel("Password:");
        JPasswordField passwordFld = new JPasswordField();
        char[] password = passwordFld.getPassword();

        userPanel.add(passwordLbl);
        userPanel.add(passwordFld);

        //As the JOptionPane accepts an object as the message
        //it allows us to use any component we like - in this case 
        //a JPanel containing the dialog components we want
        int input = JOptionPane.showConfirmDialog(null, userPanel, "Enter your password:"
                      ,JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        jFileChooser1.setVisible(true);
        int returnVal = jFileChooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser1.getSelectedFile();
            String fileName = file.getAbsolutePath();
            AuthorizationDocument ad = (AuthorizationDocument) functionality.decryptObjFile(fileName, password);
            nd.setAuthDocument(ad);
            this.manualRemove(0, documentArea.getText().length());
            repaint(ad);
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_openFileMousePressed

    public void repaint(AuthorizationDocument ad){
        String text = ad.getDocument().getString();
        
        int length = text.length();
        DocumentValue dv = ad.getDocument().getValues().getNext();
        for(int i = 0; i < length; i++){
            System.out.println(dv.getLevel());
            this.manualInsert(i, String.valueOf(text.charAt(i)) , colors.get(dv.getLevel()));
            dv = dv.getNext();
        }
        
    }
    
    public void setColors(int begin, int end, int colorLevel){
        cd.setColors(begin, end, colors.get(colorLevel), true);
    }
    
    public boolean approveUserForLevel(String userId, int level) { 
        String msg = "Add " + userId + " to level " + level + "?";
        return JOptionPane.showConfirmDialog(this, msg) == JOptionPane.OK_OPTION;
    }
    
    public void manualInsert(int offset, String string, AttributeSet attributeSet){
        try {
            // Do something here
            System.out.println("Inserting: " + string);
            cd.manualInsert(offset, string, attributeSet);
            if(offset <= documentArea.getCaretPosition() 
                    && documentArea.getCaretPosition() + string.length() < documentArea.getText().length()){
                documentArea.setCaretPosition(documentArea.getCaretPosition() + string.length());
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(EditPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void manualRemove(int offset, int length){
        try {
            cd.manualRemove(offset, length);
            if(offset < documentArea.getCaretPosition() && documentArea.getCaretPosition() - length >= 0){
                documentArea.setCaretPosition(documentArea.getCaretPosition() - length);
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(EditPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deltaCaretPosition(int delta){
        documentArea.setCaretPosition(documentArea.getCaretPosition() + delta);
    }
    
    public void update(String s){
        //documentArea.setText(s);
    }
    
    public void setColors(int offset, int length, AttributeSet as, boolean replace){
        cd.setColors(offset, length, as, true);
    }
    
    public void displayMessages(String plaintext){
        documentArea.setText(documentArea.getText() + plaintext + "\n");
    }
    
    public void addUser(String username, int levelIdentifier){
        if(displayedUsername.contains(username)){
                for(int i = 0; i < peerModel.size(); i++){
                String x = peerModel.get(i).toString().split(" - ")[0];
                if(peerModel.get(i).toString().split(" - ")[1].equals("Document Owner")){
                    continue;
                }
                if(x.equals(username)){
                    peerModel.remove(i);
                    peerModel.addElement(username +" - " + labels.get(levelIdentifier));
                    cd.insertLevel = levelIdentifier;
                    return;
                }
            }
            return;
        }
        peerModel.addElement(username +" - " + labels.get(levelIdentifier)); 
        displayedUsername.add(username);
    }
    
    public void reviseUser(String username, int levelIdentifier){
        for(int i = 0; i < peerModel.size(); i++){
            String x = peerModel.get(i).toString().split(" - ")[0];
            if(peerModel.get(i).toString().split(" - ")[1].equals("Document Owner")){
                continue;
            }
            if(x.equals(username)){
                peerModel.remove(i);
                peerModel.addElement(username +" - " + labels.get(levelIdentifier));
                cd.insertLevel = levelIdentifier;
                return;
            }
        }
    }
    
    private CustomDocument cd;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DisconnectButton;
    private javax.swing.JLabel LevelLabel;
    private javax.swing.JScrollPane LevelList;
    private javax.swing.JComboBox LevelSelect;
    private javax.swing.JLabel PeersLabel;
    private javax.swing.JList PeersList;
    private javax.swing.JTextField beginCursor;
    private javax.swing.JButton changeUserLevel;
    private javax.swing.JLabel cursorInfo;
    private javax.swing.JTextPane documentArea;
    private javax.swing.JTextField endCursor;
    private javax.swing.JButton generatePINButton;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton openFile;
    private javax.swing.JButton openNormalFile;
    private javax.swing.JComboBox ownerWriteLevel;
    private javax.swing.JButton saveEncryptedFile;
    private javax.swing.JToggleButton setLevelButton;
    // End of variables declaration//GEN-END:variables
    private ArrayList<String> labels;
    public ArrayList<SimpleAttributeSet> colors;
    DefaultListModel peerModel;
    newCellRenderer cr;
    private ArrayList<String> displayedUsername;
    NetworkDocument nd;
}