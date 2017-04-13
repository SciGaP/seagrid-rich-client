/* ***** BEGIN LICENSE BLOCK *****
   Version: Apache 2.0/GPL 3.0/LGPL 3.0

   CCT - Computational Chemistry Tools
   Jamberoo - Java Molecules Editor

   Copyright 2008-2015 Dr. Vladislav Vasilyev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Contributor(s):
     Dr. Vladislav Vasilyev <vvv900@gmail.com>       (original author)

  Alternatively, the contents of this file may be used under the terms of
  either the GNU General Public License Version 2 or later (the "GPL"), or
  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  in which case the provisions of the GPL or the LGPL are applicable instead
  of those above. If you wish to allow use of your version of this file only
  under the terms of either the GPL or the LGPL, and not to allow others to
  use your version of this file under the terms of the Apache 2.0, indicate your
  decision by deleting the provisions above and replace them with the notice
  and other provisions required by the GPL or the LGPL. If you do not delete
  the provisions above, a recipient may use your version of this file under
  the terms of any one of the Apache 2.0, the GPL or the LGPL.

 ***** END LICENSE BLOCK *****/

package cct.vasp.ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cct.interfaces.MoleculeInterface;
import cct.vasp.Vasp;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class ReadPoscarDialog
    extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton helpButton = new JButton();
  JLabel jLabel1 = new JLabel();
  JPanel filesPanel = new JPanel();
  JPanel buttonsPanel = new JPanel();
  JLabel jLabel2 = new JLabel();
  JButton cancelButton = new JButton();
  JButton loadButton = new JButton();
  JButton potcarButton = new JButton();
  JButton browsePoscarButton = new JButton();
  JTextField potcarTextField = new JTextField();
  JTextField poscarTextField = new JTextField();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  JFileChooser chooser = null;
  Vasp vaspFile = null;
  //Java3dUniverse java3dUniverse = null;
  MoleculeInterface molecule = null;
  private static String lastPWDKey = "lastPWD";
  Preferences prefs = Preferences.userNodeForPackage(getClass());
  File currentWorkingDirectory = null;
  boolean OKPressed = false;
  boolean atomTypesResolved = false;
  static final Logger logger = Logger.getLogger(ReadPoscarDialog.class.getCanonicalName());

  public ReadPoscarDialog(Frame owner, String title, boolean modal, MoleculeInterface mol) {
    super(owner, title, modal);
    molecule = mol;
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void setMolecule(MoleculeInterface mol) {
    molecule = mol;
  }

  public MoleculeInterface getMolecule() {
    return molecule;
  }

  private ReadPoscarDialog() {
    this(new Frame(), "ReadPoscarDialog", false, null);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    helpButton.setToolTipText("");
    helpButton.setText("Help");
    helpButton.addActionListener(new ReadPoscarDialog_helpButton_actionAdapter(this));
    jLabel1.setToolTipText("");
    jLabel1.setText("POTCAR File:");
    jLabel2.setToolTipText("");
    jLabel2.setText("POSCAR file:");
    cancelButton.setToolTipText("");
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ReadPoscarDialog_cancelButton_actionAdapter(this));
    loadButton.setEnabled(false);
    loadButton.setToolTipText("");
    loadButton.setText("Load");
    loadButton.addActionListener(new ReadPoscarDialog_loadButton_actionAdapter(this));
    potcarButton.setEnabled(false);
    potcarButton.setToolTipText("");
    potcarButton.setText("Browse");
    potcarButton.addActionListener(new ReadPoscarDialog_coordButton_actionAdapter(this));
    browsePoscarButton.setToolTipText("");
    browsePoscarButton.setText("Browse");
    browsePoscarButton.addActionListener(new ReadPoscarDialog_browsePrmtopButton_actionAdapter(this));
    potcarTextField.setEnabled(false);
    potcarTextField.setEditable(false);
    potcarTextField.setText("");
    potcarTextField.setColumns(30);
    poscarTextField.setToolTipText("");
    poscarTextField.setEditable(false);
    poscarTextField.setText("");
    poscarTextField.setColumns(30);
    filesPanel.setLayout(gridBagLayout1);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    getContentPane().add(panel1);
    buttonsPanel.add(loadButton);
    buttonsPanel.add(cancelButton);
    buttonsPanel.add(helpButton);
    panel1.add(buttonsPanel, BorderLayout.SOUTH);
    panel1.add(filesPanel, BorderLayout.CENTER);
    filesPanel.add(jLabel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    filesPanel.add(poscarTextField,
                   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
                                          , GridBagConstraints.CENTER,
                                          GridBagConstraints.HORIZONTAL,
                                          new Insets(2, 2, 2, 2), 0, 0));
    filesPanel.add(browsePoscarButton,
                   new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                          , GridBagConstraints.CENTER,
                                          GridBagConstraints.NONE,
                                          new Insets(2, 2, 2, 2), 0, 0));
    filesPanel.add(jLabel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    filesPanel.add(potcarTextField,
                   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
                                          , GridBagConstraints.NORTH,
                                          GridBagConstraints.HORIZONTAL,
                                          new Insets(2, 2, 2, 2), 0, 0));
    filesPanel.add(potcarButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 1.0
        , GridBagConstraints.NORTH, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
  }

  public boolean isOKPressed() {
    return OKPressed;
  }

  public float[][] getLatticeVectors() {
    return vaspFile.getLatticeVectors();
  }

  public void coordButton_actionPerformed(ActionEvent e) {

    /*
           if (chooser == null) {
       javax.swing.filechooser.FileFilter[] filters = FileFilterImpl.getFileFilters( "potcar" );
       chooser = new JFileChooser();
       for (int i = 0; i < filters.length; i++) {
          chooser.addChoosableFileFilter(filters[i]);
       }

       chooser.setAcceptAllFileFilterUsed(false);
       chooser.setDialogTitle("Open POTCAR File");
       chooser.setDialogType(JFileChooser.OPEN_DIALOG);
           }
     */

    FileDialog fd = new FileDialog(this, "Open VASP POTCAR File (potcar)", FileDialog.LOAD);
    fd.setFile("*.dat;*.inp;potcar");

    if (currentWorkingDirectory == null) {
      String lastPWD = prefs.get(lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory() &&
            currentWorkingDirectory.exists()) {
          //chooser.setCurrentDirectory(currentWorkingDirectory);
        }
      }
    }

    if (currentWorkingDirectory != null) {
      fd.setDirectory(currentWorkingDirectory.getAbsolutePath());
    }
    fd.setVisible(true);
    if (fd.getFile() == null) {
      return;
    }

    String fileName = fd.getFile();
    String workingDirectory = fd.getDirectory();

    currentWorkingDirectory = new File(workingDirectory);

    /*
           if (currentWorkingDirectory != null) {
       chooser.setCurrentDirectory(currentWorkingDirectory);
           }
           else {
       String lastPWD = prefs.get(lastPWDKey, "");
       if (lastPWD.length() > 0) {
          currentWorkingDirectory = new File(lastPWD);
          if (currentWorkingDirectory.isDirectory() &&
              currentWorkingDirectory.exists()) {
             chooser.setCurrentDirectory(currentWorkingDirectory);
          }
       }
           }
     */

    //int returnVal = chooser.showOpenDialog(this);

    //if (returnVal == JFileChooser.APPROVE_OPTION) {
    //   String fileName = chooser.getSelectedFile().getPath();
    //   currentWorkingDirectory = chooser.getCurrentDirectory();

    try {
      prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
    }
    catch (Exception ex) {
      System.err.println("Cannot save cwd: " + ex.getMessage() + " Ignored...");
    }
    logger.info("You chose to open this file: " + fileName);

    try {
      vaspFile.parsePotcar(workingDirectory + fileName, 0);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Try to resolve atom types

    try {
      vaspFile.resolveAtomTypes();
      atomTypesResolved = true;
    }
    catch (Exception ex) {
      atomTypesResolved = false;
      JOptionPane.showMessageDialog(this, "Cannot resolve atom types: " +
                                    ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;

    }

    //loadButton.setEnabled(true);
    potcarTextField.setText(workingDirectory + fileName);
    //return fileName;
    //}

  }

  public void browsePrmtopButton_actionPerformed(ActionEvent e) {
    FileDialog fd = new FileDialog(this, "Open VASP POSCAR File (poscar)", FileDialog.LOAD);
    fd.setFile("*.dat;*.inp;poscar");
    fd.setVisible(true);
    if (fd.getFile() == null) {
      return;
    }

    String fileName = fd.getFile();
    String workingDirectory = fd.getDirectory();

    vaspFile = new Vasp();
    try {
      vaspFile.parsePoscar(workingDirectory + fileName, 0);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (vaspFile.getNumberOfAtoms() < 1) {
      JOptionPane.showMessageDialog(this, "Didn't find atoms in file", "Didn't find atoms in file", JOptionPane.ERROR_MESSAGE);
      return;
    }

    JOptionPane.showMessageDialog(this, "Found " + vaspFile.getNumberOfAtoms() + " atoms in file", "Info",
                                  JOptionPane.INFORMATION_MESSAGE);

    potcarTextField.setEnabled(true);
    potcarButton.setEnabled(true);

    loadButton.setEnabled(true);

    poscarTextField.setEnabled(false);
    poscarTextField.setText(workingDirectory + fileName);
    poscarTextField.setEnabled(true);
  }

  public void loadButton_actionPerformed(ActionEvent e) {
    this.OKPressed = true;
    poscarTextField.setText("");
    potcarTextField.setText("");
    this.loadButton.setEnabled(false);
    potcarButton.setEnabled(false);
    this.setVisible(false);

    // Try to resolve atom types

    try {
      vaspFile.resolveAtomTypes();
      atomTypesResolved = true;
    }
    catch (Exception ex) {
      atomTypesResolved = false;
      JOptionPane.showMessageDialog(this, "Cannot resolve atom types: " + ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
      return;

    }

    try {
      vaspFile.getMolecularInterface(molecule);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error getting molecule: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    atomTypesResolved = false;
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    this.OKPressed = false;
    poscarTextField.setText("");
    potcarTextField.setText("");
    this.loadButton.setEnabled(false);
    potcarButton.setEnabled(false);
    this.setVisible(false);
  }

  public void helpButton_actionPerformed(ActionEvent e) {
    JOptionPane.showMessageDialog(this,
                                  "Download POSCAR file first. Then download POTCAR file and press Load button",
                                  "Help",
                                  JOptionPane.INFORMATION_MESSAGE);
  }

  private class ReadPoscarDialog_helpButton_actionAdapter
      implements ActionListener {
    private ReadPoscarDialog adaptee;
    ReadPoscarDialog_helpButton_actionAdapter(ReadPoscarDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.helpButton_actionPerformed(e);
    }
  }

  private class ReadPoscarDialog_cancelButton_actionAdapter
      implements ActionListener {
    private ReadPoscarDialog adaptee;
    ReadPoscarDialog_cancelButton_actionAdapter(ReadPoscarDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.cancelButton_actionPerformed(e);
    }
  }

  private class ReadPoscarDialog_loadButton_actionAdapter
      implements ActionListener {
    private ReadPoscarDialog adaptee;
    ReadPoscarDialog_loadButton_actionAdapter(ReadPoscarDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.loadButton_actionPerformed(e);
    }
  }

  private class ReadPoscarDialog_browsePrmtopButton_actionAdapter
      implements ActionListener {
    private ReadPoscarDialog adaptee;
    ReadPoscarDialog_browsePrmtopButton_actionAdapter(ReadPoscarDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.browsePrmtopButton_actionPerformed(e);
    }
  }

  private class ReadPoscarDialog_coordButton_actionAdapter
      implements ActionListener {
    private ReadPoscarDialog adaptee;
    ReadPoscarDialog_coordButton_actionAdapter(ReadPoscarDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.coordButton_actionPerformed(e);
    }
  }

}
