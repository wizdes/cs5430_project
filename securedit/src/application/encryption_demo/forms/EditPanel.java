/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo.forms;

import application.encryption_demo.CustomDocument;
import document.NetworkDocumentInterface;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
        SimpleAttributeSet aset = new SimpleAttributeSet();
        StyleConstants.setForeground(aset, c);
        colors.add(aset);
    }
        
    public void populateColorsList(ArrayList<Color> colors, ArrayList<String> labels){
        this.labels = labels;
        cr.giveColorList(colors);
        for(Color c:colors){
            addColor(c);
        }
    }
    
    public void setDefaultColorsAndLabels() {
        ArrayList<Color> defaultColors = new ArrayList<>();
        defaultColors.add(Color.black);
        defaultColors.add(Color.blue);
        defaultColors.add(Color.green);
        defaultColors.add(Color.red);
        
        ArrayList<String> defaultLabels = new ArrayList<>();
        defaultLabels.add("NORMAL");
        defaultLabels.add("PRIVILEGED");
        defaultLabels.add("SECRET");
        defaultLabels.add("TOP SECRET");
        
        populateColorsList(defaultColors, defaultLabels);
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
            defaultLabels.add(label);
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
            defaultColors.add(color);
        }
        
        populateColorsList(defaultColors, defaultLabels);
    }
    
    public void promptForLevelsAndColors() {
        int r = JOptionPane.showConfirmDialog(this, "Would you like to use the default levels?");
        if (r == JOptionPane.OK_OPTION) {
            setDefaultColorsAndLabels();
        } else {
            setupColorsAndLabels();
        }
    }    
    
    public EditPanel() {
        initComponents();
        peerModel = new DefaultListModel();
        cr = new newCellRenderer();
        labels = new ArrayList<String>();
        colors = new ArrayList<SimpleAttributeSet>();
        
        promptForLevelsAndColors();
        
        for (String l : labels) {
            LevelSelect.addItem(l);
        }
        
        String[] elements = new String[labels.size()];
        elements = labels.toArray(elements);
        jList1.setListData(elements);
        jList1.setCellRenderer(cr);
        
        PeersList.setModel(peerModel);

        cd = new CustomDocument();
        documentArea.setDocument(cd);
        cd.setEditorReference(this);
        
        displayedUsername = new ArrayList<>();
    }
    
    public void giveDocument(NetworkDocumentInterface nd){
        this.nd = nd;
        cd.giveDocument(nd);
        peerModel.addElement(nd.getOwnerID() + " - Document Owner");
        displayedUsername.add(nd.getOwnerID());
        if(nd.isOwner() == false){
            beginCursor.setEnabled(false);
            endCursor.setEnabled(false);
            LevelSelect.setEnabled(false);
            setLevelButton.setEnabled(false);
            changeUserLevel.setText("Request Change Level");
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
        saveRegFile = new javax.swing.JButton();
        saveEncryptedFile = new javax.swing.JButton();

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

        openNormalFile.setText("Open Normal File");

        saveRegFile.setText("Save Unencrypted File");

        saveEncryptedFile.setText("Save Encrypted File");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(openFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openNormalFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveRegFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveEncryptedFile)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(beginCursor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(endCursor, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LevelSelect, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(PeersLabel)
                    .addComponent(cursorInfo)
                    .addComponent(LevelList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(LevelLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(changeUserLevel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(setLevelButton)))
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
                        .addComponent(LevelList, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(openFile)
                            .addComponent(openNormalFile)
                            .addComponent(saveRegFile)
                            .addComponent(saveEncryptedFile))))
                .addContainerGap(56, Short.MAX_VALUE))
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
        jf.setLabelsandUsername(labels, displayedUsername);
        jf.setVisible(true);
    }//GEN-LAST:event_changeUserLevelMousePressed

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
            if(offset <= documentArea.getCaretPosition()){
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
    
    public void observedDelta(int offset, int length, String string){
        //defunct, will use this later
    }
    
    public void displayMessages(String plaintext){
        documentArea.setText(documentArea.getText() + plaintext + "\n");
    }
    
    public void addUser(String username, int levelIdentifier){
        if(displayedUsername.contains(username)) return;
        peerModel.addElement(username +" - " + labels.get(levelIdentifier)); 
        displayedUsername.add(username);
    }
    
    private CustomDocument cd;
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton openFile;
    private javax.swing.JButton openNormalFile;
    private javax.swing.JButton saveEncryptedFile;
    private javax.swing.JButton saveRegFile;
    private javax.swing.JToggleButton setLevelButton;
    // End of variables declaration//GEN-END:variables
    private ArrayList<String> labels;
    private ArrayList<SimpleAttributeSet> colors;
    DefaultListModel peerModel;
    newCellRenderer cr;
    private ArrayList<String> displayedUsername;
    NetworkDocumentInterface nd;
}