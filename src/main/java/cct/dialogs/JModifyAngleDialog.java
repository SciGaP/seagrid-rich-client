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

import cct.j3d.Java3dUniverse;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cct.modelling.MolecularEditor;
import cct.modelling.OperationsOnAtoms;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <p>
 * Title: Picking</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005</p>
 *
 * <p>
 * Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class JModifyAngleDialog
        extends JDialog implements OperationsOnAtoms {

  JPanel mainPanel = new JPanel();
  JPanel DisplPanel = new JPanel();
  JPanel Atom_1 = new JPanel();
  JPanel Atom_3 = new JPanel();
  JPanel Atom_2 = new JPanel();
  Border border1 = BorderFactory.createLineBorder(Color.lightGray, 1);
  Border border2 = new TitledBorder(border1, "Atom 2 Displacement");
  ButtonGroup buttonGroup1 = new ButtonGroup();
  ButtonGroup buttonGroup2 = new ButtonGroup();
  ButtonGroup buttonGroup3 = new ButtonGroup();
  JRadioButton Fixed_1 = new JRadioButton();
  JRadioButton Rotate_atom_1 = new JRadioButton();
  JRadioButton Translate_group_1 = new JRadioButton();
  JRadioButton Rotate_group_1 = new JRadioButton();
  JRadioButton Translate_group_2 = new JRadioButton();
  JRadioButton Translate_atom_2 = new JRadioButton();
  JRadioButton Fixed_2 = new JRadioButton();
  JRadioButton Rotate_group_3 = new JRadioButton();
  JRadioButton Translate_group_3 = new JRadioButton();
  JRadioButton Rotate_atom_3 = new JRadioButton();
  JRadioButton Fixed_3 = new JRadioButton();
  GridLayout gridLayout1 = new GridLayout();
  JPanel InfoPanel = new JPanel();
  JLabel maxAngle = new JLabel();
  JLabel minAngle = new JLabel();
  JTextField currentAngle = new JTextField();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JPanel changeAnglePanel = new JPanel();
  JSlider changeAngle = new JSlider();
  // --- My variables

  Map Labels = new HashMap();
  JPanel anglePanel = new JPanel();
  MolecularEditor daddy = null;
  float oldAngle;
  float maxAngleValue = 180.0f;
  boolean setOnlySlider = false;
  JPanel buttonPanel = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton Finish = new JButton();
  JCheckBox instantView = new JCheckBox();
  JButton undoLastSelection = new JButton();
  JButton Ok = new JButton();
  JButton Reset = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  JButton helpButton = new JButton();

  public JModifyAngleDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    try {
      this.addWindowListener(new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent we) {
          CloseWindowGracefully();
        }
      });
      //setDefaultCloseOperation(HIDE_ON_CLOSE);
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JModifyAngleDialog() {
    this(new Frame(), "JModifyAngleDialog", false);
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    Labels.put(0, "0.0");
    Labels.put(50, "90.0");
    Labels.put(100, "180.0");

    mainPanel.setLayout(borderLayout1);
    DisplPanel.setBorder(null);
    DisplPanel.setLayout(gridLayout1);
    Atom_1.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1), "Atom 1 Displacement"));
    Atom_1.setLayout(gridBagLayout1);
    Atom_2.setBorder(border2);
    Atom_2.setLayout(gridBagLayout2);
    Atom_3.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1), "Atom 3 Displacement"));
    Atom_3.setLayout(gridBagLayout3);
    Fixed_1.setText("Fixed");
    Fixed_1.addChangeListener(new JModifyAngleDialog_Fixed_1_changeAdapter(this));
    Rotate_atom_1.setSelected(false);
    Rotate_atom_1.setText("Rotate Atom");
    Rotate_atom_1.addChangeListener(new JModifyAngleDialog_Rotate_atom_1_changeAdapter(this));
    Translate_group_1.setEnabled(true);
    Translate_group_1.setToolTipText("");
    Translate_group_1.setRolloverSelectedIcon(null);
    Translate_group_1.setSelected(false);
    Translate_group_1.setText("Translate Group");
    Translate_group_1.addChangeListener(new JModifyAngleDialog_Translate_group_1_changeAdapter(this));
    Rotate_group_1.setSelected(true);
    Rotate_group_1.setText("Rotate Group");
    Translate_group_2.setEnabled(false);
    Translate_group_2.setToolTipText("");
    Translate_group_2.setText("Translate Group");
    Translate_atom_2.setEnabled(false);
    Translate_atom_2.setText("Translate Atom");
    Fixed_2.setEnabled(true);
    Fixed_2.setSelected(true);
    Fixed_2.setText("Fixed");
    Fixed_2.addChangeListener(new JModifyAngleDialog_Fixed_2_changeAdapter(this));
    Rotate_group_3.setToolTipText("");
    Rotate_group_3.setSelected(true);
    Rotate_group_3.setText("Rotate Group");
    Translate_group_3.setEnabled(true);
    Translate_group_3.setToolTipText("");
    Translate_group_3.setSelected(false);
    Translate_group_3.setText("Translate Group");
    Rotate_atom_3.setText("Rotate Atom");
    Fixed_3.setText("Fixed");
    Fixed_3.addChangeListener(new JModifyAngleDialog_Fixed_3_changeAdapter(this));
    maxAngle.setToolTipText("");
    maxAngle.setHorizontalAlignment(SwingConstants.RIGHT);
    maxAngle.setHorizontalTextPosition(SwingConstants.RIGHT);
    maxAngle.setText("180.0");
    minAngle.setHorizontalAlignment(SwingConstants.LEFT);
    minAngle.setHorizontalTextPosition(SwingConstants.LEFT);
    minAngle.setText("0.0");
    currentAngle.setToolTipText("");
    currentAngle.setText("90.0");
    currentAngle.setColumns(8);
    currentAngle.setHorizontalAlignment(SwingConstants.CENTER);
    currentAngle.addActionListener(new JModifyAngleDialog_currentAngle_actionAdapter(this));
    InfoPanel.setLayout(gridBagLayout5);
    jLabel1.setToolTipText("");
    jLabel1.setText("");
    jLabel2.setText("");
    changeAnglePanel.setInputVerifier(null);
    changeAnglePanel.setLayout(borderLayout2);
    changeAngle.setLabelTable(null);
    changeAngle.setMajorTickSpacing(36);
    changeAngle.setMaximum(360);
    changeAngle.setPaintTicks(true);
    changeAngle.addChangeListener(new JModifyAngleDialog_changeAngle_changeAdapter(this));
    anglePanel.setLayout(gridBagLayout4);
    buttonPanel.setLayout(flowLayout1);
    Finish.setToolTipText("Accept current editing and Finish");
    Finish.setText("Finish");
    Finish.addActionListener(new JModifyAngleDialog_Finish_actionAdapter(this));
    instantView.setToolTipText("");
    instantView.setSelected(true);
    instantView.setText("Instant View");
    undoLastSelection.setToolTipText("Deselect last selected atom");
    undoLastSelection.setText("Undo Last Selection");
    undoLastSelection.addActionListener(new JModifyAngleDialog_undoLastSelection_actionAdapter(this));
    Ok.setToolTipText("Accept current editing and start a new one");
    Ok.setText("Accept");
    Ok.addActionListener(new JModifyAngleDialog_Ok_actionAdapter(this));
    Reset.setToolTipText("Reset geometry");
    Reset.setText("Reset");
    Reset.addActionListener(new JModifyAngleDialog_Reset_actionAdapter(this));
    helpButton.setEnabled(false);
    helpButton.setToolTipText("Accept current editing and Finish");
    helpButton.setText("Help");
    //changeAngle.setLabelTable(Labels);
    getContentPane().add(mainPanel);
    DisplPanel.add(Atom_1, null);
    DisplPanel.add(Atom_2);
    DisplPanel.add(Atom_3, null);
    buttonPanel.add(Ok);
    buttonPanel.add(Reset);
    buttonPanel.add(Finish);
    buttonPanel.add(undoLastSelection);
    buttonPanel.add(helpButton);
    buttonGroup1.add(Fixed_1);
    buttonGroup1.add(Rotate_atom_1);
    buttonGroup1.add(Translate_group_1);
    buttonGroup1.add(Rotate_group_1);

    buttonGroup2.add(Fixed_2);
    buttonGroup2.add(Translate_group_2);
    buttonGroup2.add(Translate_atom_2);

    buttonGroup3.add(Fixed_3);
    buttonGroup3.add(Rotate_atom_3);
    buttonGroup3.add(Translate_group_3);
    buttonGroup3.add(Rotate_group_3);
    mainPanel.add(DisplPanel, BorderLayout.NORTH);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    mainPanel.add(anglePanel, BorderLayout.CENTER);
    Atom_1.add(Translate_group_1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_1.add(Rotate_group_1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_1.add(Rotate_atom_1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_2.add(Translate_group_2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_2.add(Translate_atom_2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_2.add(Fixed_2, new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_3.add(Rotate_group_3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_3.add(Translate_group_3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_3.add(Rotate_atom_3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_3.add(Fixed_3, new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Atom_1.add(Fixed_1, new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 62, 0));
    anglePanel.add(changeAnglePanel,
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 190, 0));
    anglePanel.add(instantView, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    anglePanel.add(InfoPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
    changeAnglePanel.add(changeAngle, BorderLayout.NORTH);
    InfoPanel.add(jLabel1, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
    InfoPanel.add(jLabel2, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
    InfoPanel.add(maxAngle, new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
    InfoPanel.add(currentAngle, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    InfoPanel.add(minAngle, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(2, 2, 2, 2), 0, 0));
  }

  public void setTargetClass(MolecularEditor target) {
    daddy = target;
  }

  public void setAngle(float angle) {
    oldAngle = angle;
    setOnlySlider = true;
    setTextAreaOnly(angle);
    setSliderOnly(angle);
    setOnlySlider = false;
  }

  public void modifyAngle(float angle) {
    int I_status = FIXED_ATOM;
    if (Rotate_group_1.isSelected()) {
      I_status = ROTATE_GROUP;
    } else if (Translate_group_1.isSelected()) {
      I_status = TRANSLATE_GROUP;
    } else if (Rotate_atom_1.isSelected()) {
      I_status = ROTATE_ATOM;
    }

    int J_status = 0;
    if (Translate_group_2.isSelected()) {
      J_status = TRANSLATE_GROUP;
    } else if (Translate_atom_2.isSelected()) {
      J_status = TRANSLATE_ATOM;
    }

    int K_status = FIXED_ATOM;
    if (Rotate_group_3.isSelected()) {
      K_status = ROTATE_GROUP;
    } else if (Translate_group_3.isSelected()) {
      K_status = TRANSLATE_GROUP;
    } else if (Rotate_atom_3.isSelected()) {
      K_status = ROTATE_ATOM;
    }

    daddy.changeAngleBetweenSelectedAtoms(angle, I_status,
            J_status, K_status);
  }

  private void setTextAreaOnly(float value) {
    currentAngle.setText(String.format("%8.4f", value));
  }

  private void setSliderOnly(float value) {
    boolean store = instantView.isSelected();
    instantView.setSelected(false);
    changeAngle.setValueIsAdjusting(true);
    float max = changeAngle.getMaximum();
    changeAngle.setValue((int) (value / maxAngleValue * max));
    changeAngle.setValueIsAdjusting(false);
    instantView.setSelected(store);
  }

  public void CloseWindowGracefully() {
    Java3dUniverse target = (Java3dUniverse) daddy;
    //target.processSelectedAtoms();
    target.endProcessingSelectedAtoms();
    setVisible(false);

  }

  public void Finish_actionPerformed(ActionEvent e) {
    CloseWindowGracefully();
  }

  public void changeAngle_stateChanged(ChangeEvent e) {
    float value = changeAngle.getValue();
    float max = changeAngle.getMaximum();
    float min = changeAngle.getMinimum();
    oldAngle = (value - min) / max * maxAngleValue;
    //logger.info("Value: "+value+" Curr Bond: "+currentBond+" min: "+min+" max: "+max);

    if (setOnlySlider) {
      return;
    }

    setTextAreaOnly(oldAngle);

    if (changeAngle.getValueIsAdjusting() && !instantView.isSelected()) { //doing adjusting

    } else { //done adjusting
      modifyAngle(oldAngle);
    }
  }

  public void Fixed_1_stateChanged(ChangeEvent e) {
    if (Fixed_1.isSelected()) {
      if (Fixed_2.isSelected()) {
        Fixed_3.setEnabled(false);
      }
      if (Fixed_3.isSelected()) {
        Fixed_2.setEnabled(false);
      }
    } else {
      Fixed_2.setEnabled(true);
      Fixed_3.setEnabled(true);
    }
  }

  public void Fixed_2_stateChanged(ChangeEvent e) {
    if (Fixed_2.isSelected()) {
      if (Fixed_1.isSelected()) {
        Fixed_3.setEnabled(false);
      }
      if (Fixed_3.isSelected()) {
        Fixed_1.setEnabled(false);
      }
    } else {
      Fixed_1.setEnabled(true);
      Fixed_3.setEnabled(true);
    }
  }

  public void Fixed_3_stateChanged(ChangeEvent e) {
    if (Fixed_3.isSelected()) {
      if (Fixed_1.isSelected()) {
        Fixed_2.setEnabled(false);
      }
      if (Fixed_2.isSelected()) {
        Fixed_1.setEnabled(false);
      }
    } else {
      Fixed_1.setEnabled(true);
      Fixed_2.setEnabled(true);
    }

  }

  public void Translate_group_1_stateChanged(ChangeEvent e) {
    //Fixed_2.setEnabled(true);
    //Fixed_3.setEnabled(true);
  }

  public void Rotate_atom_1_stateChanged(ChangeEvent e) {
    //Fixed_2.setEnabled(true);
    //Fixed_3.setEnabled(true);
  }

  public void undoLastSelection_actionPerformed(ActionEvent e) {
    daddy.undoLastSelection();
  }

  public void Ok_actionPerformed(ActionEvent e) {
    daddy.confirmChanges();
  }

  public void Reset_actionPerformed(ActionEvent e) {
    daddy.resetGeometry();
  }

  public void currentAngle_actionPerformed(ActionEvent e) {
    String new_value = currentAngle.getText();
    float value;
    try {
      value = Float.parseFloat(new_value);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,
              "Wrong input for angle value: "
              + currentAngle.getText(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
      currentAngle.setText(String.valueOf(oldAngle));
      return;
    }

    // Second test
    if (value < 0 || value > 180.0) {
      JOptionPane.showMessageDialog(this,
              "Angle value should be in the range from 0 to 180 degrees",
              "Error",
              JOptionPane.ERROR_MESSAGE);
      currentAngle.setText(String.valueOf(oldAngle));

      return;
    }

    // -- now set slider
    oldAngle = value;
    setOnlySlider = true;
    setSliderOnly(oldAngle);
    modifyAngle(oldAngle);
    setOnlySlider = false;
  }

  public JButton getHelpButton() {
    return helpButton;
  }
}

class JModifyAngleDialog_currentAngle_actionAdapter
        implements ActionListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_currentAngle_actionAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.currentAngle_actionPerformed(e);
  }
}

class JModifyAngleDialog_Reset_actionAdapter
        implements ActionListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_Reset_actionAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Reset_actionPerformed(e);
  }
}

class JModifyAngleDialog_Ok_actionAdapter
        implements ActionListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_Ok_actionAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Ok_actionPerformed(e);
  }
}

class JModifyAngleDialog_undoLastSelection_actionAdapter
        implements ActionListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_undoLastSelection_actionAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.undoLastSelection_actionPerformed(e);
  }
}

class JModifyAngleDialog_Rotate_atom_1_changeAdapter
        implements ChangeListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_Rotate_atom_1_changeAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.Rotate_atom_1_stateChanged(e);
  }
}

class JModifyAngleDialog_Fixed_3_changeAdapter
        implements ChangeListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_Fixed_3_changeAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.Fixed_3_stateChanged(e);
  }
}

class JModifyAngleDialog_Fixed_2_changeAdapter
        implements ChangeListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_Fixed_2_changeAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.Fixed_2_stateChanged(e);
  }
}

class JModifyAngleDialog_Fixed_1_changeAdapter
        implements ChangeListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_Fixed_1_changeAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.Fixed_1_stateChanged(e);
  }
}

class JModifyAngleDialog_Translate_group_1_changeAdapter
        implements ChangeListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_Translate_group_1_changeAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.Translate_group_1_stateChanged(e);
  }
}

class JModifyAngleDialog_changeAngle_changeAdapter
        implements ChangeListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_changeAngle_changeAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.changeAngle_stateChanged(e);
  }
}

class JModifyAngleDialog_Finish_actionAdapter
        implements ActionListener {

  private JModifyAngleDialog adaptee;

  JModifyAngleDialog_Finish_actionAdapter(JModifyAngleDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Finish_actionPerformed(e);
  }
}
