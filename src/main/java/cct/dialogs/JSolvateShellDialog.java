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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import cct.modelling.ui.OpenFile;

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
public class JSolvateShellDialog
    extends JDialog {
  boolean OK_pressed = false;
  OpenFile openFile = null;
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel solventPanel = new JPanel();
  JPanel paramPanel = new JPanel();
  JPanel buttonPanel = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JButton SolvateButton = new JButton();
  JTextField jTextField1 = new JTextField();
  JLabel jLabel1 = new JLabel();
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel jLabel2 = new JLabel();
  JTextField jTextField2 = new JTextField();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JButton browseButton = new JButton();
  JComboBox jComboBox1 = new JComboBox();
  JRadioButton predefinedRadioButton = new JRadioButton();
  JRadioButton userDefinedRadioButton = new JRadioButton();
  ButtonGroup buttonGroup1 = new ButtonGroup();
  JTextField fileTextField = new JTextField();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  Object[] customObjects = null;

  public JSolvateShellDialog(Frame owner, String title, boolean modal,
                             java.util.List simpleList) {
    super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      for (int i = 0; i < simpleList.size(); i++) {
        jComboBox1.addItem(simpleList.get(i));
      }
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JSolvateShellDialog(Frame owner, String title, boolean modal, Map simpleList) {
    super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();

      Set set = simpleList.entrySet();
      Iterator iter = set.iterator();

      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        String dicName = me.getKey().toString();
        jComboBox1.addItem(dicName);
      }

      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                  DO_NOTHING_ON_CLOSE);
    jButton1.setEnabled(false);
    jButton1.setToolTipText("Get Help about Solvation parameters");
    jButton1.setText("Help");
    jButton2.setToolTipText("");
    jButton2.setText("Cancel");
    jButton2.addActionListener(new JSolvateShellDialog_jButton2_actionAdapter(this));
    SolvateButton.setToolTipText("");
    SolvateButton.setText("Solvate");
    SolvateButton.addActionListener(new
                                    JSolvateShellDialog_SolvateButton_actionAdapter(this));
    jTextField1.setToolTipText("");
    jTextField1.setText("1.0");
    jTextField1.setColumns(6);
    jTextField1.setHorizontalAlignment(SwingConstants.CENTER);
    jTextField1.addActionListener(new
                                  JSolvateShellDialog_jTextField1_actionAdapter(this));
    jLabel1.setToolTipText("");
    jLabel1.setText("   Closeness: ");
    paramPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setVgap(15);
    jLabel2.setToolTipText("Radius of Solvent Sphere");
    jLabel2.setText("Radius: ");
    jTextField2.setToolTipText("Enter Radius of solvation and press Enter");
    jTextField2.setText("5.0");
    jTextField2.setColumns(6);
    jTextField2.setHorizontalAlignment(SwingConstants.CENTER);
    jTextField2.addActionListener(new
                                  JSolvateShellDialog_jTextField2_actionAdapter(this));
    solventPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(
        Color.GRAY, 1), "Solvent"));
    solventPanel.setLayout(gridBagLayout1);
    jLabel3.setToolTipText("");
    jLabel3.setText("   User Defined: ");
    jLabel4.setToolTipText("");
    jLabel4.setText("Predefined: ");
    browseButton.setToolTipText("");
    browseButton.setText("Browse");
    browseButton.addActionListener(new JSolvateShellDialog_browseButton_actionAdapter(this));
    predefinedRadioButton.setToolTipText("");
    predefinedRadioButton.setSelected(true);
    predefinedRadioButton.setText("Predefined");
    predefinedRadioButton.addItemListener(new JSolvateShellDialog_predefinedRadioButton_itemAdapter(this));
    userDefinedRadioButton.setToolTipText("");
    userDefinedRadioButton.setText("User Defined");
    fileTextField.setToolTipText("");
    fileTextField.setEditable(false);
    fileTextField.setColumns(50);
    getContentPane().add(panel1);
    paramPanel.add(jLabel2);
    paramPanel.add(jTextField2);
    paramPanel.add(jLabel1);
    paramPanel.add(jTextField1);
    panel1.add(buttonPanel, BorderLayout.SOUTH);
    buttonPanel.add(SolvateButton);
    buttonPanel.add(jButton2);
    buttonPanel.add(jButton1);
    buttonGroup1.add(userDefinedRadioButton);
    buttonGroup1.add(predefinedRadioButton);
    solventPanel.add(fileTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    solventPanel.add(browseButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    solventPanel.add(jComboBox1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    solventPanel.add(userDefinedRadioButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    solventPanel.add(predefinedRadioButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    solventPanel.add(jLabel4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    solventPanel.add(jLabel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    panel1.add(paramPanel, BorderLayout.CENTER);
    panel1.add(solventPanel, BorderLayout.NORTH);

    // --- My stuff

    jComboBox1.setEnabled(predefinedRadioButton.isSelected());
    fileTextField.setEnabled(!predefinedRadioButton.isSelected());
    browseButton.setEnabled(!predefinedRadioButton.isSelected());
  }

  public int getSelectedIndex() {
    return jComboBox1.getSelectedIndex();
  }

  public Object getSelectedItem() {
    return jComboBox1.getSelectedItem();
  }

  public String getCustomSolventFile() {
    return fileTextField.getText().trim();
  }

  public JButton getHelpButton() {
    return jButton1;
  }

  public boolean isOKpressed() {
    return OK_pressed;
  }

  public boolean isPredefinedSolvent() {
    return predefinedRadioButton.isSelected();
  }

  public float getCloseness() throws Exception {
    return getFloat(jTextField1.getText());
  }

  public String getClosenessAsText() {
    return jTextField1.getText();
  }

  public float getRadius() throws Exception {
    return getFloat(jTextField2.getText());
  }

  public String getRadiusAsText() {
    return jTextField2.getText();
  }

  public void jTextField2_actionPerformed(ActionEvent e) {
    try {
      getFloat(jTextField2.getText());
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "ERROR: Wrong value for radius: " +
                                    jTextField2.getText() + " " +
                                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  float getFloat(String value) throws Exception {
    float f = 0;
    try {
      f = Float.parseFloat(value.trim());
    }
    catch (Exception ex) {
      throw ex;
    }
    return f;
  }

  public void jTextField1_actionPerformed(ActionEvent e) {
    try {
      getFloat(jTextField1.getText());
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "ERROR: Wrong value for closeness: " + jTextField1.getText() + " " +
                                    ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
    }

  }

  class JSolvateShellDialog_jTextField1_actionAdapter
      implements ActionListener {
    private JSolvateShellDialog adaptee;
    JSolvateShellDialog_jTextField1_actionAdapter(JSolvateShellDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.jTextField1_actionPerformed(e);
    }
  }

  public void SolvateButton_actionPerformed(ActionEvent e) {
    OK_pressed = true;
    setVisible(false);
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    OK_pressed = false;
    setVisible(false);
  }

  public void predefinedRadioButton_itemStateChanged(ItemEvent e) {
    jComboBox1.setEnabled(predefinedRadioButton.isSelected());
    fileTextField.setEnabled(!predefinedRadioButton.isSelected());
    browseButton.setEnabled(!predefinedRadioButton.isSelected());
  }

  public Object[] getCustomObjects() {
    return customObjects;
  }

  public void browseButton_actionPerformed(ActionEvent e) {
    if (openFile == null) {
      openFile = new OpenFile();
    }
    try {
      customObjects = openFile.loadMolecule(null, null);
      fileTextField.setText(openFile.getFileName());
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  private class JSolvateShellDialog_jTextField2_actionAdapter
      implements ActionListener {
    private JSolvateShellDialog adaptee;
    JSolvateShellDialog_jTextField2_actionAdapter(JSolvateShellDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.jTextField2_actionPerformed(e);
    }
  }

  private class JSolvateShellDialog_SolvateButton_actionAdapter
      implements ActionListener {
    private JSolvateShellDialog adaptee;
    JSolvateShellDialog_SolvateButton_actionAdapter(JSolvateShellDialog
        adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.SolvateButton_actionPerformed(e);
    }
  }

  private class JSolvateShellDialog_jButton2_actionAdapter
      implements ActionListener {
    private JSolvateShellDialog adaptee;
    JSolvateShellDialog_jButton2_actionAdapter(JSolvateShellDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton2_actionPerformed(e);
    }
  }

}

class JSolvateShellDialog_browseButton_actionAdapter
    implements ActionListener {
  private JSolvateShellDialog adaptee;
  JSolvateShellDialog_browseButton_actionAdapter(JSolvateShellDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.browseButton_actionPerformed(e);
  }
}

class JSolvateShellDialog_predefinedRadioButton_itemAdapter
    implements ItemListener {
  private JSolvateShellDialog adaptee;
  JSolvateShellDialog_predefinedRadioButton_itemAdapter(JSolvateShellDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    adaptee.predefinedRadioButton_itemStateChanged(e);
  }

}
