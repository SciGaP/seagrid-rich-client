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

package cct.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cct.j3d.Java3dUniverse;

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
public class JAddMoleculeDialog
    extends JFrame {
  JPanel controlsPanel = new JPanel();
  JButton helpButton = new JButton();
  JButton cancelButton = new JButton();
  JButton okButton = new JButton();

  Java3dUniverse java3d = null;
  Java3dUniverse parent = null;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JCheckBox jCheckBox1 = new JCheckBox();
  JTextField jTextField1 = new JTextField();
  JComboBox jComboBox1 = new JComboBox();
  FlowLayout flowLayout1 = new FlowLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  JLabel jLabel2 = new JLabel();

  private JAddMoleculeDialog(Frame owner, String title, boolean modal) {
    //super(owner, title, modal)
    super(title);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JAddMoleculeDialog(Frame owner, String title, boolean modal,
                            Java3dUniverse j3d) {

    //super(owner, title, modal);
    java3d = j3d;
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }

  }

  private JAddMoleculeDialog() {
    this(new Frame(), "JAddMoleculeDialog", false);
  }

  public boolean isPredefinedDistance() {
    return jCheckBox1.isSelected();
  }

  public float getPredefinedDistance() {
    float d = -1.0f;
    try {
      d = Float.parseFloat(jTextField1.getText());
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(this,
                                    "Distance " + jTextField1.getText() +
                                    " is not a valid positive value",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
    }
    return d;
  }

  public void setRenderer(Java3dUniverse j3d) {
    java3d = j3d;
    pack();
    validate();
  }

  public void setParent(Java3dUniverse j3d) {
    parent = j3d;
  }

  public JButton getHelpButton() {
    return helpButton;
  }

  private void jbInit() throws Exception {
    this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                  DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent we) {
        //setLabel("Thwarted user attempt to close window.");
        finishDialog();
      }
    });

    helpButton.setEnabled(false);
    helpButton.setText("Help");
    cancelButton.setToolTipText("Cancel adding");
    cancelButton.setText("Finish");
    cancelButton.addActionListener(new
                                   JAddMoleculeDialog_cancelButton_actionAdapter(this));
    okButton.setToolTipText("Confirm adding molecule");
    okButton.setText("OK");
    okButton.addActionListener(new JAddMoleculeDialog_okButton_actionAdapter(this));
    controlsPanel.setLayout(borderLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel1.setText(
        "Press Shift-Left-Click on the spot to add a Molecule");
    jCheckBox1.setToolTipText("");
    jCheckBox1.setHorizontalAlignment(SwingConstants.LEFT);
    jCheckBox1.setText("Predefined Distance Between Atoms");
    jCheckBox1.addChangeListener(new
                                 JAddMoleculeDialog_jCheckBox1_changeAdapter(this));
    jPanel2.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    jPanel3.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    jTextField1.setToolTipText("Enter New Positive Value & Press Enter");
    jTextField1.setColumns(6);
    jTextField1.addActionListener(new
                                  JAddMoleculeDialog_jTextField1_actionAdapter(this));
    jComboBox1.addItemListener(new JAddMoleculeDialog_jComboBox1_itemAdapter(this));
    jLabel2.setToolTipText("");
    jLabel2.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel2.setText(
        "Select Atom in the Main Window and");
    getContentPane().add(controlsPanel, BorderLayout.SOUTH);
    getContentPane().add(java3d.getCanvas3D(), BorderLayout.CENTER);
    jPanel1.add(okButton);
    jPanel1.add(cancelButton);
    jPanel1.add(helpButton);
    controlsPanel.add(jPanel3, BorderLayout.NORTH);
    jPanel3.add(jLabel2);
    jPanel3.add(jLabel1, FlowLayout.LEFT);
    controlsPanel.add(jPanel1, BorderLayout.SOUTH);
    controlsPanel.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jCheckBox1, FlowLayout.LEFT);
    jPanel2.add(jTextField1);
    jPanel2.add(jComboBox1);
    okButton.setVisible(false);

    jComboBox1.addItem("0.9");
    jComboBox1.addItem("1.0");
    jComboBox1.addItem("1.1");
    jComboBox1.addItem("1.2");
    jComboBox1.addItem("1.3");
    jComboBox1.addItem("1.4");
    jComboBox1.addItem("1.5");
    jComboBox1.addItem("1.6");
    jComboBox1.addItem("1.7");
    jComboBox1.addItem("1.8");
    jComboBox1.addItem("1.9");
    jComboBox1.addItem("2.0");
    jComboBox1.addItem("2.2");
    jComboBox1.addItem("2.5");
    jComboBox1.addItem("3.0");
    jComboBox1.addItem("4.0");

    jTextField1.setText(jComboBox1.getItemAt(0).toString());

    if (jCheckBox1.isSelected()) {
      jTextField1.setEnabled(true);
      jComboBox1.setEnabled(true);
    }
    else {
      jTextField1.setEnabled(false);
      jComboBox1.setEnabled(false);
    }
  }

  public void setGraphics(Java3dUniverse j3d) {
    validate();
  }

  public void okButton_actionPerformed(ActionEvent e) {
    parent.appendMolecule(java3d);
  }

  private void finishDialog() {
    setVisible(false);
    parent.endProcessingSelectedAtoms();
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    finishDialog();
  }

  public void jCheckBox1_stateChanged(ChangeEvent e) {
    if (jCheckBox1.isSelected()) {
      jTextField1.setEnabled(true);
      jComboBox1.setEnabled(true);
    }
    else {
      jTextField1.setEnabled(false);
      jComboBox1.setEnabled(false);
    }
  }

  public void jComboBox1_itemStateChanged(ItemEvent e) {
    jTextField1.setEnabled(false);
    jTextField1.setText(jComboBox1.getSelectedItem().toString());
    jTextField1.setEnabled(true);
  }

  public void jTextField1_actionPerformed(ActionEvent e) {
    if (!jTextField1.isEnabled()) {
      return;
    }
    jTextField1.setEnabled(false);
    float d = -1.0f;
    try {
      d = Float.parseFloat(jTextField1.getText());
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Distance " + jTextField1.getText() + " is not a valid positive value", "Error",
                                    JOptionPane.ERROR_MESSAGE);
      jTextField1.setText(jComboBox1.getItemAt(0).toString());
    }
    jTextField1.setEnabled(true);
  }

}

class JAddMoleculeDialog_jTextField1_actionAdapter
    implements ActionListener {
  private JAddMoleculeDialog adaptee;
  JAddMoleculeDialog_jTextField1_actionAdapter(JAddMoleculeDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.jTextField1_actionPerformed(e);
  }
}

class JAddMoleculeDialog_jComboBox1_itemAdapter
    implements ItemListener {
  private JAddMoleculeDialog adaptee;
  JAddMoleculeDialog_jComboBox1_itemAdapter(JAddMoleculeDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    adaptee.jComboBox1_itemStateChanged(e);
  }
}

class JAddMoleculeDialog_jCheckBox1_changeAdapter
    implements ChangeListener {
  private JAddMoleculeDialog adaptee;
  JAddMoleculeDialog_jCheckBox1_changeAdapter(JAddMoleculeDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.jCheckBox1_stateChanged(e);
  }
}

class JAddMoleculeDialog_okButton_actionAdapter
    implements ActionListener {
  private JAddMoleculeDialog adaptee;
  JAddMoleculeDialog_okButton_actionAdapter(JAddMoleculeDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class JAddMoleculeDialog_cancelButton_actionAdapter
    implements ActionListener {
  private JAddMoleculeDialog adaptee;
  JAddMoleculeDialog_cancelButton_actionAdapter(JAddMoleculeDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}
