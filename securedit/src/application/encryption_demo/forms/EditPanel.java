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
import document.NetworkDocumentHandler;
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
    
    public void giveDocument(NetworkDocumentHandler nd){
        
        this.nd = nd;
        cd.giveDocument(nd);
        displayedUsername.add(nd.getUserID());
        if (nd.isOwner()) {
            peerModel.addElement(nd.getOwnerID() + " - Document Owner");
            promptForLevelsAndColors();
        } else if (!nd.isOwner()) {
            beginCursor.setEnabled(false);
            endCursor.setEnabled(false);
            LevelSelect.setEnabled(false);
            setLevelButton.setEnabled(false);
            generatePINButton.setEnabled(false);
            changeUserLevelButton.setText("Request Change Level");
        }
    }

    public void handleBootstrap(AuthorizationDocument ad) {
        peerModel.addElement(nd.getOwnerID() + " - Document Owner");
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
        //Replaced with action listener
    }//GEN-LAST:event_setLevelButtonMousePressed

    private void changeUserLevelButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_changeUserLevelButtonKeyPressed
        // TODO add your handling code here:

        
    }//GEN-LAST:event_changeUserLevelButtonKeyPressed

    private void changeUserLevelButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeUserLevelButtonMousePressed
        //Replaced with changeUserLevelButtonActionPerformed

    }//GEN-LAST:event_changeUserLevelButtonMousePressed

    private void ownerWriteLevelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ownerWriteLevelItemStateChanged
        // TODO add your handling code here:
        if (nd != null && nd.isOwner()){
            cd.insertLevel = ownerWriteLevel.getSelectedIndex();
            System.out.println("Selected: " + ownerWriteLevel.getSelectedIndex());
            System.out.println("Level: " + cd.insertLevel);
        }
    }//GEN-LAST:event_ownerWriteLevelItemStateChanged

    private void openNormalFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openNormalFileButtonActionPerformed
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
    }//GEN-LAST:event_openNormalFileButtonActionPerformed

    private void openNormalFileButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openNormalFileButtonMousePressed
        //Replaced with openNormalFileButtonActionPerformed
    }//GEN-LAST:event_openNormalFileButtonMousePressed

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

    private void saveEncryptedFileButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveEncryptedFileButtonMousePressed
       //replaced with saveEncryptedFileActionPeformed
       
    }//GEN-LAST:event_saveEncryptedFileButtonMousePressed

    private void openEncryptedFileButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openEncryptedFileButtonMousePressed
        //replaced with openEncryptedFileActionPerformed
    }//GEN-LAST:event_openEncryptedFileButtonMousePressed

    private void openEncryptedFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openEncryptedFileButtonActionPerformed
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
    }//GEN-LAST:event_openEncryptedFileButtonActionPerformed

    private void saveEncryptedFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEncryptedFileButtonActionPerformed
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
        int returnVal = jFileChooser1.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser1.getSelectedFile();
            String fileName = file.getAbsolutePath();
            functionality.encryptFile(fileName, nd.getAuthDocument(), password);
        } else {
            System.out.println("File access cancelled by user.");
        } 
    }//GEN-LAST:event_saveEncryptedFileButtonActionPerformed

    private void changeUserLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeUserLevelButtonActionPerformed
        System.out.println("Pressed Button");
        ChangeUserAccess jf = new ChangeUserAccess();
        jf.setLabelsandUsername(labels, displayedUsername, nd);
        //jf.setVisible(true);

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
    }//GEN-LAST:event_changeUserLevelButtonActionPerformed

    private void setLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setLevelButtonActionPerformed
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
    }//GEN-LAST:event_setLevelButtonActionPerformed

    private void DisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisconnectButtonActionPerformed
        
    }//GEN-LAST:event_DisconnectButtonActionPerformed

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
    private ArrayList<String> labels;
    public ArrayList<SimpleAttributeSet> colors;
    DefaultListModel peerModel;
    newCellRenderer cr;
    private ArrayList<String> displayedUsername;
    NetworkDocumentHandler nd;
}