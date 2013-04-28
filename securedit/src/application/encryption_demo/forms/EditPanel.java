/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo.forms;

import application.encryption_demo.CustomDocument;
import application.encryption_demo.EncryptionDemoFunctionality;
import configuration.Constants;
import document.AuthorizationDocument;
import document.DocumentValue;
import document.NetworkDocumentHandler;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedReader;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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
    public void addColor(Color c){
        //locks operations
        nd.lock();
        try{
            // creates an attribute set that corresponds to the color and adds the color
            SimpleAttributeSet aset = new SimpleAttributeSet();
            StyleConstants.setForeground(aset, c);
            colors.add(aset);
        } finally{
            nd.unlock();
        }
    }
    
    // populates the colors list with the list of colors and labels passed in
    public void populateColorsList(ArrayList<Color> colors, ArrayList<String> labels){
        nd.lock();
        try{
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
        } finally{
            nd.unlock();
        }
    }
    
    // these are the default colors and labels (a selectable option)
    public void setDefaultColorsAndLabels() {
        nd.lock();
        try{
            nd.addColor(Color.black);
            nd.addColor(Color.blue);
            nd.addColor(Color.green);
            nd.addColor(Color.red);

            nd.addLabel("NORMAL");
            nd.addLabel("PRIVILEGED");
            nd.addLabel("SECRET");
            nd.addLabel("TOP SECRET");
            populateColorsList(nd.getColors(), nd.getLabels());
        } finally{
            nd.unlock();
        }
    }
    
    // this gets the color choices available
    private HashMap<String, Color> getColorChoices() {
        nd.lock();
        try{
            HashMap<String, Color> colorChoices = new HashMap<>();
            colorChoices.put("black", Color.black);
            colorChoices.put("blue", Color.blue);
            colorChoices.put("green", Color.green);
            colorChoices.put("red", Color.red);
            colorChoices.put("gray", Color.GRAY);
            colorChoices.put("white", Color.white);
            colorChoices.put("cyan", Color.CYAN);
            colorChoices.put("magenta", Color.MAGENTA);
            colorChoices.put("orange", Color.orange);
            colorChoices.put("pink", Color.PINK);            

            assert colorChoices.size() == Constants.ALLOWABLE_LABEL_COUNT;
            return colorChoices;
        } finally{
            nd.unlock();
        }
    }
    
    //this prompts for the color choices that are available
    private String getColorChoicePrompt(HashMap<String, Color> colorChoices) {
        nd.lock();
        try{
            String colorChoiceList = "(";
            for (String c : colorChoices.keySet()) {
                colorChoiceList += c + ", ";
            }
            colorChoiceList = colorChoiceList.substring(0, colorChoiceList.length() - 2) + ")";
            String colorPrompt = "choose a color for this group '\n" + colorChoiceList + " : ";

            return colorPrompt;
        } finally{
            nd.unlock();
        }
    }
    
    //this sets up all the colors and labels
    //this creates a GUI that pops up and populates the colors/labels list
    private void setupColorsAndLabels() {
        nd.lock();
        try{

            HashMap<String, Color> colorChoices = getColorChoices();
            String labelPrompt = "Enter a security group label (up to 10, or type 'done' when finished)";
            String label = "not done";
            
            int count = 0;
            while (count++ < Constants.ALLOWABLE_LABEL_COUNT && !label.equals("done")) {
                label = JOptionPane.showInputDialog(labelPrompt);
                if (labels.contains(label)) {
                    count--;
                    JOptionPane.showMessageDialog(this, "The label " + label + " already exists");
                } else if (!label.equals("done")) {
                    nd.addLabel(label);
                }
            }

            for (String l : nd.getLabels()) {
                String colorPrompt = getColorChoicePrompt(colorChoices);
                String colorName = JOptionPane.showInputDialog(l + " ~> " + colorPrompt);
                Color color = colorChoices.get(colorName);
                while (color == null) {
                    JOptionPane.showMessageDialog(this, "Invalid color selection");
                    String defaultSelection = colorChoices.keySet().toArray()[0].toString();
                    colorName = JOptionPane.showInputDialog(l + " ~> " + colorPrompt, defaultSelection);
                    color = colorChoices.get(colorName);                
                }
                colorChoices.remove(colorName);
                nd.addColor(color);
            }
            
            populateColorsList(nd.getColors(), nd.getLabels());
        } finally{
            nd.unlock();
        }
    }
    
    //this is the first prompt for the levels and colors used 
    public void promptForLevelsAndColors() {
        nd.lock();
        try{    
            int r = JOptionPane.showConfirmDialog(this, "Would you like to use the default levels?");
            if (r == JOptionPane.OK_OPTION) {
                setDefaultColorsAndLabels();
            } else {
                setupColorsAndLabels();
            }
        } finally{
            nd.unlock();
        }
    }    
    
    // constructor for the EditPanel (this is used to instantiate this panel)
    public EditPanel(EncryptionDemoFunctionality functionality) {
        this.functionality = functionality;
        initComponents();
        
        PeersList.setModel(peerModel);

        documentArea.setDocument(cd);
        cd.setEditorReference(this);
        
        displayedUsername = new ArrayList<>();
    }

    // this hands the document interface to the GUI
    public void giveDocument(NetworkDocumentHandler nd){
        this.nd = nd;
        this.nd.lock();
        try{
            //this passes it to the GUI custom document class
            cd.giveDocument(nd);
            
            //depending on the role of the contributor, display different things
            displayedUsername.add(nd.getOwnerID());
            if (nd.isOwner()) {
                peerModel.addElement(nd.getOwnerID() + " - Document Owner");
                promptForLevelsAndColors();
            } else if (!nd.isOwner()) {
                beginCursor.setEnabled(false);
                endCursor.setEnabled(false);
                LevelSelect.setEnabled(false);
                setLevelButton.setEnabled(false);
                generatePINButton.setEnabled(false);
                ownerWriteLevel.setEnabled(false);
                changeUserLevelButton.setText("Request Change Level");
            }
        } finally{
            this.nd.unlock();
        }
    }
    
    // this removes the user from the GUI only
    // only takes it out of the peerModel data structure
    public void removeUser(String username) {
        nd.lock();
        try {
            for (int i = 0; i < peerModel.size(); i++) {
                String x = peerModel.get(i).toString().split(" - ")[0];
                if (peerModel.get(i).toString().split(" - ")[1].equals("Document Owner")) {
                    continue;
                }
                if (x.equals(username)) {
                    peerModel.remove(i);
                    return;
                }
            }
        } finally {
            nd.unlock();
        }
    }

    // when a client tries to connect to it, this boostraps all the labels/colors/text
    public void handleBootstrap(AuthorizationDocument ad) {
        nd.lock();
        try{
            //add the peer name to the peers list
            peerModel.addElement(nd.getOwnerID() + " - Document Owner");

            //sets all the documents properly
            nd.setAuthDocument(ad);
            this.giveDocument(nd);
            
            // adds the colors 
            this.populateColorsList(nd.getColors(), nd.getLabels());
            
            //re populates the text
            this.repaint(ad);
        } finally{
            nd.unlock();
        }
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
        changeUserLevelButton = new javax.swing.JButton();
        openEncryptedFileButton = new javax.swing.JButton();
        openNormalFileButton = new javax.swing.JButton();
        saveEncryptedFileButton = new javax.swing.JButton();
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
        PeersLabel.setText("Visible Peers");

        documentArea.setFont(new java.awt.Font("Courier New", 0, 13)); // NOI18N
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
        setLevelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setLevelButtonActionPerformed(evt);
            }
        });

        LevelList.setViewportView(jList1);

        LevelLabel.setText("Levels");

        changeUserLevelButton.setText("Change User Level");
        changeUserLevelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                changeUserLevelButtonMousePressed(evt);
            }
        });
        changeUserLevelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeUserLevelButtonActionPerformed(evt);
            }
        });
        changeUserLevelButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                changeUserLevelButtonKeyPressed(evt);
            }
        });

        openEncryptedFileButton.setText("Open Encrypted File");
        openEncryptedFileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                openEncryptedFileButtonMousePressed(evt);
            }
        });
        openEncryptedFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openEncryptedFileButtonActionPerformed(evt);
            }
        });

        openNormalFileButton.setText("Open Normal File");
        openNormalFileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                openNormalFileButtonMousePressed(evt);
            }
        });
        openNormalFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openNormalFileButtonActionPerformed(evt);
            }
        });

        saveEncryptedFileButton.setText("Save Encrypted File");
        saveEncryptedFileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                saveEncryptedFileButtonMousePressed(evt);
            }
        });
        saveEncryptedFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEncryptedFileButtonActionPerformed(evt);
            }
        });

        DisconnectButton.setText("Disconnect");
        DisconnectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DisconnectButtonMouseClicked(evt);
            }
        });
        DisconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisconnectButtonActionPerformed(evt);
            }
        });

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
                        .addComponent(openEncryptedFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openNormalFileButton)
                        .addGap(18, 18, 18)
                        .addComponent(saveEncryptedFileButton)
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
                                .addComponent(changeUserLevelButton)
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
                            .addComponent(changeUserLevelButton))
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
                            .addComponent(openEncryptedFileButton)
                            .addComponent(openNormalFileButton)
                            .addComponent(saveEncryptedFileButton)
                            .addComponent(generatePINButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void documentAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_documentAreaCaretUpdate
        nd.lock();
        try{
            cursorInfo.setText("Cursor at: " + evt.getDot());
        } finally{
            nd.unlock();
        }
    }//GEN-LAST:event_documentAreaCaretUpdate

    private void beginCursorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_beginCursorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_beginCursorActionPerformed

    private void LevelSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LevelSelectActionPerformed
    }//GEN-LAST:event_LevelSelectActionPerformed

    private void setLevelButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setLevelButtonMousePressed
        //Replaced with action listener
    }//GEN-LAST:event_setLevelButtonMousePressed

    private void changeUserLevelButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_changeUserLevelButtonKeyPressed
    }//GEN-LAST:event_changeUserLevelButtonKeyPressed

    private void changeUserLevelButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeUserLevelButtonMousePressed
        //Replaced with changeUserLevelButtonActionPerformed
    }//GEN-LAST:event_changeUserLevelButtonMousePressed

    private void ownerWriteLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ownerWriteLevelItemStateChanged
        nd.lock();
        try{
            // this is the ability for the owner to make modifications to the level of different text
            if (nd != null && nd.isOwner()){
                cd.insertLevel = ownerWriteLevel.getSelectedIndex();
                System.out.println("Selected: " + ownerWriteLevel.getSelectedIndex());
                System.out.println("Level: " + cd.insertLevel);
            }
        } finally{
            nd.unlock();
        }
    }//GEN-LAST:event_ownerWriteLevelItemStateChanged

    //reads the contents of a file
    static public String getStringfromFile(File aFile) {
       StringBuilder elements = new StringBuilder();
       try {
           BufferedReader inBuf =  new BufferedReader(new FileReader(aFile));
           try {
               String line = null; 
               while (( line = inBuf.readLine()) != null){
                   elements.append(line);
                   elements.append(System.getProperty("line.separator"));
               }
           }
           finally {
               inBuf.close();
           }
       }
       catch (IOException e){
           System.out.println("Could not read the file properly.");
       }
       return elements.toString();
     }    

    //this is the function that is called to an unecrypted file
    private void openNormalFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openNormalFileButtonActionPerformed
        nd.lock();
        try{
            jFileChooser1.setVisible(true);
            int returnVal = jFileChooser1.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser1.getSelectedFile();
                // this read the the file as String
                String contents = getStringfromFile(file);
                
                // remove the current text in the document
                this.manualRemove(0, documentArea.getText().length());

                try {
                    //sanitizes the file and puts the contents in the file
                    contents = contents.trim();
                    cd.insertString(0, contents, colors.get(0));
                    System.out.println(contents.length());
                    System.out.println(documentArea.getText().length());
                    //documentArea.read(new FileReader( file.getAbsolutePath() ), null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(EditPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("File access cancelled by user.");
            }
        } finally{
            nd.unlock();
        }
    }//GEN-LAST:event_openNormalFileButtonActionPerformed

    private void openNormalFileButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openNormalFileButtonMousePressed
        //Replaced with openNormalFileButtonActionPerformed
    }//GEN-LAST:event_openNormalFileButtonMousePressed

    private void generatePINButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generatePINButtonActionPerformed
        nd.lock();
        try{
            // this generates a PIN value
            String username = JOptionPane.showInputDialog("Enter the username:").trim();
            if (username.isEmpty()) {
                return;
            }
            char[] pin = this.functionality.generatePIN(username, nd.getName());
            System.out.println("PIN: " + new String(pin));
            new PINDisplayDialog(username, pin).setVisible(true);
        } finally{
            nd.unlock();
        }
    }//GEN-LAST:event_generatePINButtonActionPerformed

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
    }//GEN-LAST:event_jFileChooser1ActionPerformed

    private void saveEncryptedFileButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveEncryptedFileButtonMousePressed
       //replaced with saveEncryptedFileActionPeformed
    }//GEN-LAST:event_saveEncryptedFileButtonMousePressed

    private void openEncryptedFileButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openEncryptedFileButtonMousePressed
        //replaced with openEncryptedFileActionPerformed
    }//GEN-LAST:event_openEncryptedFileButtonMousePressed

    private void openEncryptedFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openEncryptedFileButtonActionPerformed
        //Using a JPanel as the message for the JOptionPane
        nd.lock();
        try {
            //this allows users to open encrypted files
            JPanel userPanel = new JPanel();
            userPanel.setLayout(new GridLayout(2, 2));

            JLabel passwordLbl = new JLabel("Password:");
            JPasswordField passwordFld = new JPasswordField();
            userPanel.add(passwordLbl);
            userPanel.add(passwordFld);

            //As the JOptionPane accepts an object as the message
            int input = JOptionPane.showConfirmDialog(null, userPanel, "Enter your password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            jFileChooser1.setVisible(true);
            char[] password = passwordFld.getPassword();

            int returnVal = jFileChooser1.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser1.getSelectedFile();
                String fileName = file.getAbsolutePath();
                AuthorizationDocument ad = (AuthorizationDocument) functionality.decryptObjFile(fileName, password);
                if(ad != null){
                    this.manualRemove(0, documentArea.getText().length());
                    nd.setAuthDocument(ad);
                    repaint(ad);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Couldn't open file.");
                }
            } else {
                System.out.println("File access cancelled by user.");
            }
        } finally {
            nd.unlock();
        }
    }//GEN-LAST:event_openEncryptedFileButtonActionPerformed

    //allows users to save files as an encrypted version of their files
    private void saveEncryptedFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEncryptedFileButtonActionPerformed
        nd.lock();
        try {
            //Select file to save to
            jFileChooser1.setVisible(true);
            int returnVal = jFileChooser1.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser1.getSelectedFile();
                String fileName = file.getAbsolutePath();

                //Prompt for password, continue prompting until receive valid password.
                char[] password;
                int input;
                String passwordStr = "Password:";
                do {
                    //Using a JPanel as the message for the JOptionPane
                    JPanel userPanel = new JPanel();
                    userPanel.setLayout(new GridLayout(2, 2));

                    JLabel passwordLbl = new JLabel(passwordStr);
                    JPasswordField passwordFld = new JPasswordField();

                    userPanel.add(passwordLbl);
                    userPanel.add(passwordFld);

                    //As the JOptionPane accepts an object as the message
                    input = JOptionPane.showConfirmDialog(null, userPanel, "Enter your password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    password = passwordFld.getPassword();
                    if(!isValidPassword(password)) passwordStr = "Valid Password (12 Characters, Alphanumeric, Upper/Lower:";
                } while(!isValidPassword(password) && input == JOptionPane.YES_OPTION);
                functionality.encryptFile(fileName, nd.getAuthDocument(), password);
            } else {
                System.out.println("File access cancelled by user.");
            }
        } finally {
            nd.unlock();
        }
    }//GEN-LAST:event_saveEncryptedFileButtonActionPerformed

    //checks to make sure the valid that is inputted is strong enough
    private boolean isValidPassword(char[] pass) {
        
        boolean containsLowerCase = false;
        boolean containsUpperCase = false;
        boolean containsDigit = false;
    
        for(char c: pass){
            containsLowerCase   = containsLowerCase || Character.isLowerCase(c);
            containsUpperCase   = containsUpperCase || Character.isUpperCase(c);
            containsDigit       = containsDigit || Character.isDigit(c); 
        }
        System.out.println("Pass: "  + Arrays.toString(pass));
        System.out.println("lower: " + containsLowerCase);
        System.out.println("upper: " + containsUpperCase);
        System.out.println("digit: " + containsDigit);
        return pass.length >= Constants.MIN_PASSWORD_LENGTH && containsLowerCase && containsUpperCase && containsDigit;
    }
    
    //allows the document owner to determine which levels to change
    private void changeUserLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeUserLevelButtonActionPerformed
        nd.lock();
        try{
            System.out.println("Pressed Button");
            ChangeUserAccess jf = new ChangeUserAccess();
            jf.setLabelsandUsername(labels, displayedUsername, nd);

            Object[] options = {"Close"};
            int r = JOptionPane.showOptionDialog(
                    this,
                    jf, 
                    "Login",
                    JOptionPane.OK_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);
        } finally{
            nd.unlock();
        }
    }//GEN-LAST:event_changeUserLevelButtonActionPerformed

    // allows the user to define the level of a specific user
    private void setLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLevelButtonActionPerformed
        nd.lock();
        try {
            if (beginCursor.getText().equals("") || endCursor.getText().equals("")) {
                return;
            }
            int beginCursorInt = Integer.parseInt(beginCursor.getText());
            int endCursorInt = Integer.parseInt(endCursor.getText());
            int colorPosition = LevelSelect.getSelectedIndex();
            AttributeSet s = colors.get(colorPosition);
            //send it to everyone else
            nd.assignLevel(colorPosition, beginCursorInt, endCursorInt);
        } finally {
            nd.unlock();
        }
    }//GEN-LAST:event_setLevelButtonActionPerformed

    private void DisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisconnectButtonActionPerformed
        endEditingSession();
    }//GEN-LAST:event_DisconnectButtonActionPerformed

    public void endEditingSession() {
        this.nd.disconnect();
        this.functionality.closeEditingSession(nd);
    }

    // allows for the disconnect of a user
    private void DisconnectButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DisconnectButtonMouseClicked
    }//GEN-LAST:event_DisconnectButtonMouseClicked

    // repopulates the text with the new authorization document that was passed in
    public void repaint(AuthorizationDocument ad){
        nd.lock();
        try{
            String text = ad.getDocument().getString();

            int length = text.length();
            DocumentValue dv = ad.getDocument().getValues().getNext();
            for(int i = 0; i < length; i++){
                System.out.println(dv.getLevel());
                this.manualInsert(i, String.valueOf(text.charAt(i)) , colors.get(dv.getLevel()));
                dv = dv.getNext();
            }
        } finally{
            nd.unlock();
        }
    }
    
    // allows colors for different labels to be set outside EditPanel
    public void setColors(int begin, int end, int colorLevel){
        nd.lock();
        try{
            cd.setColors(begin, end, colors.get(colorLevel), true);
        } finally{
            nd.unlock();
        }
    }
    
    // this is allowing the document owner to approve a level change request from a peer
    public boolean approveUserForLevel(String userId, int level) { 
        nd.lock();
        try{
            String msg = "Add " + userId + " to level " + level + "?";
            return JOptionPane.showConfirmDialog(this, msg) == JOptionPane.OK_OPTION;
        } finally{
            nd.unlock();
        }
    }
    
    // pushes a text change at an offset and level into the document
    public void manualInsert(int offset, String string, AttributeSet attributeSet){
        nd.lock();
        try {
            cd.manualInsert(offset, string, attributeSet);
            if(offset <= documentArea.getCaretPosition() 
                    && documentArea.getCaretPosition() + string.length() <= documentArea.getText().length()){
                documentArea.setCaretPosition(documentArea.getCaretPosition() + string.length());
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(EditPanel.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            nd.unlock();
        }
    }
    
    public void manualReplace(int offset, int length){
        try {
            String replaceStr = "";
            for(int i = 0; i < length; i++) replaceStr += "X";
            cd.replace(offset, length, replaceStr, null);
        } catch (BadLocationException ex) {
            Logger.getLogger(EditPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // pushes a text removal at an offset and length straight into the document
    public void manualRemove(int offset, int length){
        nd.lock();
        try {
            cd.manualRemove(offset, length);
            if(offset < documentArea.getCaretPosition() && documentArea.getCaretPosition() - length >= 0){
                documentArea.setCaretPosition(documentArea.getCaretPosition() - length);
            }
        } catch (BadLocationException ex) {
            System.out.println(ex);
            Logger.getLogger(EditPanel.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            nd.unlock();
        }
    }

    //allows for functions to manually define colors (corresponding to levels)
    public void setColors(int offset, int length, AttributeSet as, boolean replace){
        nd.lock();
        try{
            cd.setColors(offset, length, as, true);
        } finally{
            nd.unlock();
        }
    }
    
    //displays text in the documentArea
    public void displayMessages(String plaintext){
        nd.lock();
        try{
            documentArea.setText(documentArea.getText() + plaintext + "\n");
        } finally{
            nd.unlock();
        }
    }
    
    //adds a user to the GUI (just in the peerModel)
    //checks to make sure that it hasn't already been added
    public void addUser(String username, int levelIdentifier) {
        nd.lock();
        try {
            if (displayedUsername.contains(username)) {
                for (int i = 0; i < peerModel.size(); i++) {
                    String x = peerModel.get(i).toString().split(" - ")[0];
                    if (peerModel.get(i).toString().split(" - ")[1].equals("Document Owner")) {
                        continue;
                    }
                    if (x.equals(username)) {
                        peerModel.remove(i);
                        peerModel.addElement(username + " - " + labels.get(levelIdentifier));
                        cd.insertLevel = levelIdentifier;
                        return;
                    }
                }
                return;
            }
            peerModel.addElement(username + " - " + labels.get(levelIdentifier));
            displayedUsername.add(username);
        } finally {
            nd.unlock();
        }
    }
    
    // changes a user's level 
    public void reviseUser(String username, int levelIdentifier){
        nd.lock();
        try {
            for (int i = 0; i < peerModel.size(); i++) {
                String x = peerModel.get(i).toString().split(" - ")[0];
                if (peerModel.get(i).toString().split(" - ")[1].equals("Document Owner")) {
                    continue;
                }
                if (x.equals(username)) {
                    peerModel.remove(i);
                    peerModel.addElement(username + " - " + labels.get(levelIdentifier));
                    cd.insertLevel = levelIdentifier;           
                    return;
                }
            }
        } finally {
            nd.unlock();
        }
    }
    
    public ArrayList<SimpleAttributeSet> getColors(){
        return this.colors;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DisconnectButton;
    private javax.swing.JLabel LevelLabel;
    private javax.swing.JScrollPane LevelList;
    private javax.swing.JComboBox LevelSelect;
    private javax.swing.JLabel PeersLabel;
    private javax.swing.JList PeersList;
    private javax.swing.JTextField beginCursor;
    private javax.swing.JButton changeUserLevelButton;
    private javax.swing.JLabel cursorInfo;
    private javax.swing.JTextPane documentArea;
    private javax.swing.JTextField endCursor;
    private javax.swing.JButton generatePINButton;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton openEncryptedFileButton;
    private javax.swing.JButton openNormalFileButton;
    private javax.swing.JComboBox ownerWriteLevel;
    private javax.swing.JButton saveEncryptedFileButton;
    private javax.swing.JToggleButton setLevelButton;
    // End of variables declaration//GEN-END:variables
    private ArrayList<String> labels = new ArrayList<>();
    private ArrayList<SimpleAttributeSet> colors = new ArrayList<>();
    private DefaultListModel peerModel = new DefaultListModel();
    private ColorCellRenderer cr = new ColorCellRenderer();
    private ArrayList<String> displayedUsername;
    private NetworkDocumentHandler nd;
    private EncryptionDemoFunctionality functionality = null;
    private CustomDocument cd = new CustomDocument();
        
}