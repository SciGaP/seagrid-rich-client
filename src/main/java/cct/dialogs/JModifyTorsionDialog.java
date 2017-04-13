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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cct.modelling.MolecularEditor;
import cct.modelling.OperationsOnAtoms;

/**
 *
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class JModifyTorsionDialog
    extends JDialog implements OperationsOnAtoms {
   JPanel mainPanel = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel displPanel = new JPanel();
   JPanel jAtom_1 = new JPanel();
   JPanel Atom_2 = new JPanel();
   ButtonGroup buttonGroup1 = new ButtonGroup();
   ButtonGroup buttonGroup2 = new ButtonGroup();
   JRadioButton Fixed_1 = new JRadioButton();
   JRadioButton rotateAtom_1 = new JRadioButton();
   JRadioButton rotateGroup_1 = new JRadioButton();
   JRadioButton rotateAtom_4 = new JRadioButton();
   JRadioButton rotateGroup_4 = new JRadioButton();
   JRadioButton Fixed_4 = new JRadioButton();
   BorderLayout borderLayout2 = new BorderLayout();
   GridLayout gridLayout1 = new GridLayout();
   BorderLayout borderLayout3 = new BorderLayout();
   JPanel buttonPanel = new JPanel();
   JButton Reset = new JButton();
   JButton Undo = new JButton();
   JButton Finish = new JButton();
   JButton OK_button = new JButton();
   JPanel centralPanel = new JPanel();
   JCheckBox instantView = new JCheckBox();
   JPanel sliderPanel = new JPanel();
   JSlider changeAngle = new JSlider();
   JPanel viewPanel = new JPanel();
   JPanel valuesPanel = new JPanel();
   GridLayout gridLayout2 = new GridLayout();
   FlowLayout flowLayout1 = new FlowLayout();
   GridLayout gridLayout3 = new GridLayout();
   JLabel jLabel1 = new JLabel();
   JLabel jLabel2 = new JLabel();
   JLabel jLabel3 = new JLabel();
   JLabel jLabel4 = new JLabel();
   JTextField angleValue = new JTextField();
   GridLayout gridLayout4 = new GridLayout();
   JPanel jPanel1 = new JPanel();
   BorderLayout borderLayout4 = new BorderLayout();

   MolecularEditor daddy;
   float oldAngle;
   boolean setOnlySlider = false;
   JButton helpButton = new JButton();

   public JModifyTorsionDialog(Frame owner, String title, boolean modal) {
      super(owner, title, modal);
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public JModifyTorsionDialog() {
      this(new Frame(), "JModifyTorsionDialog", false);
   }

   private void jbInit() throws Exception {
      mainPanel.setLayout(borderLayout1);
      jAtom_1.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.
          lightGray, 1), "Atom 1 Displacement"));
      jAtom_1.setLayout(borderLayout2);
      Atom_2.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.
          lightGray, 1), "Atom 4 Displacement"));
      Atom_2.setLayout(borderLayout3);
      Fixed_1.setText("Fixed");
      Fixed_1.addChangeListener(new JModifyTorsionDialog_Fixed_1_changeAdapter(this));
      rotateAtom_1.setToolTipText("");
      rotateAtom_1.setSelected(true);
      rotateAtom_1.setText("Rotate Atom");
      rotateGroup_1.setToolTipText("");
      rotateGroup_1.setSelected(true);
      rotateGroup_1.setText("Rotate Group");
      rotateAtom_4.setToolTipText("");
      rotateAtom_4.setSelected(false);
      rotateAtom_4.setText("Rotate Atom");
      rotateGroup_4.setToolTipText("");
      rotateGroup_4.setSelected(true);
      rotateGroup_4.setText("Rotate Group");
      Fixed_4.setText("Fixed");
      Fixed_4.addChangeListener(new JModifyTorsionDialog_Fixed_4_changeAdapter(this));
      displPanel.setLayout(gridLayout1);
      Reset.setText("Reset");
      Reset.addActionListener(new JModifyTorsionDialog_Reset_actionAdapter(this));
      Undo.setText("Undo last selection");
      Undo.addActionListener(new JModifyTorsionDialog_Undo_actionAdapter(this));
      Finish.setToolTipText("Finish Editing");
      Finish.setText("Finish");
      Finish.addActionListener(new JModifyTorsionDialog_Finish_actionAdapter(this));
      OK_button.setText("Accept");
      OK_button.addActionListener(new
                                  JModifyTorsionDialog_OK_button_actionAdapter(this));
      instantView.setHorizontalAlignment(SwingConstants.LEFT);
      instantView.setSelected(true);
      instantView.setText("Instant View");
      changeAngle.setMajorTickSpacing(10);
    changeAngle.setMaximum(360);
    changeAngle.setPaintTicks(true);
      changeAngle.addChangeListener(new
                                    JModifyTorsionDialog_changeAngle_changeAdapter(this));
      centralPanel.setLayout(gridLayout2);
      gridLayout2.setColumns(1);
      gridLayout2.setRows(3);
      viewPanel.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      sliderPanel.setLayout(gridLayout3);
      jLabel1.setToolTipText("");
      jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
      jLabel1.setText("180  ");
      jLabel1.setVerticalAlignment(SwingConstants.BOTTOM);
      jLabel4.setText("  -180");
      jLabel4.setVerticalAlignment(SwingConstants.BOTTOM);
      angleValue.setToolTipText("Torsional Angle Value");
      angleValue.setText("0.0");
      angleValue.setColumns(8);
      angleValue.setHorizontalAlignment(SwingConstants.CENTER);
      angleValue.addActionListener(new
                                   JModifyTorsionDialog_angleValue_actionAdapter(this));
      valuesPanel.setLayout(gridLayout4);
      jPanel1.setLayout(borderLayout4);
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      helpButton.setEnabled(false);
      helpButton.setToolTipText("");
      helpButton.setText("Help");
      getContentPane().add(mainPanel);
      jAtom_1.add(rotateGroup_1, BorderLayout.NORTH);
      jAtom_1.add(rotateAtom_1, BorderLayout.CENTER);
      jAtom_1.add(Fixed_1, BorderLayout.SOUTH);
      displPanel.add(jAtom_1, null);
      displPanel.add(Atom_2, null);
      buttonGroup1.add(rotateGroup_1);
      Atom_2.add(rotateAtom_4, BorderLayout.CENTER);
      buttonGroup1.add(rotateAtom_1);
      buttonGroup1.add(Fixed_1);
      Atom_2.add(Fixed_4, BorderLayout.SOUTH);
      Atom_2.add(rotateGroup_4, BorderLayout.NORTH);
      buttonGroup2.add(rotateAtom_4);
      buttonGroup2.add(Fixed_4);
      buttonGroup2.add(rotateGroup_4);
      this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
      buttonPanel.add(OK_button);
      buttonPanel.add(Reset);
      buttonPanel.add(Finish);
      buttonPanel.add(Undo);
      buttonPanel.add(helpButton);
      this.getContentPane().add(centralPanel, BorderLayout.CENTER);
      centralPanel.add(viewPanel, null);
      viewPanel.add(instantView);
      centralPanel.add(valuesPanel);
      valuesPanel.add(jLabel4);
      valuesPanel.add(jLabel2);
      valuesPanel.add(jPanel1);
      valuesPanel.add(jLabel3);
      valuesPanel.add(jLabel1);
      centralPanel.add(sliderPanel, null);
      sliderPanel.add(changeAngle);
      this.getContentPane().add(displPanel, BorderLayout.NORTH);
      jPanel1.add(angleValue, BorderLayout.SOUTH);
   }

   public void setTargetClass(MolecularEditor target) {
      daddy = target;
   }

   public void setAngle(double angle) {
      setAngle( (float) angle);
   }

   public void setAngle(float angle) {

      if (angle < -180.0f || angle > 180.0f) {
         angle -= Math.floor( (angle + 180.0f) / 360.0f) * 360.0f - 180.f;
      }

      oldAngle = angle;
      setOnlySlider = true;
      setTextAreaOnly(angle);
      setSliderOnly(angle);
      setOnlySlider = false;
   }

   private void setTextAreaOnly(float value) {
      angleValue.setText(String.format("%8.4f", value));
   }

   /**
    * values should be always within -180 - 180 interval
    * @param value float
    */
   private void setSliderOnly(float value) {
      boolean store = instantView.isSelected();
      instantView.setSelected(false);
      changeAngle.setValueIsAdjusting(true);
      float max = changeAngle.getMaximum();
      changeAngle.setValue( (int) ( (value + 180.0f) / 360.0f * max));
      changeAngle.setValueIsAdjusting(false);
      instantView.setSelected(store);
   }

   public void OK_button_actionPerformed(ActionEvent e) {
      daddy.confirmChanges();
   }

   public void Undo_actionPerformed(ActionEvent e) {
      daddy.undoLastSelection();
   }

   public void Reset_actionPerformed(ActionEvent e) {
      daddy.resetGeometry();
   }

   public void Finish_actionPerformed(ActionEvent e) {
      //daddy.processSelectedAtoms();
      daddy.endProcessingSelectedAtoms();
      setVisible(false);
   }

   public void Fixed_1_stateChanged(ChangeEvent e) {
      if (Fixed_1.isSelected()) {
         Fixed_4.setEnabled(false);
      }
      else {
         Fixed_4.setEnabled(true);
      }
   }

   public void Fixed_4_stateChanged(ChangeEvent e) {
      if (Fixed_4.isSelected()) {
         Fixed_1.setEnabled(false);
      }
      else {
         Fixed_1.setEnabled(true);
      }
   }

   public void angleValue_actionPerformed(ActionEvent e) {
      String new_value = angleValue.getText();
      float value;
      try {
         value = Float.parseFloat(new_value);
      }
      catch (NumberFormatException ex) {
         JOptionPane.showMessageDialog(this, "Wrong input for dihedral angle value: " + angleValue.getText(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         angleValue.setText(String.valueOf(oldAngle));
         return;
      }

      // -- now set slider

      oldAngle = value;
      setOnlySlider = true;
      setSliderOnly(oldAngle);
      modifyAngle(oldAngle);
      setOnlySlider = false;

   }

   public void modifyAngle(float angle) {
      int I_status = FIXED_ATOM;
      if (rotateGroup_1.isSelected()) {
         I_status = ROTATE_GROUP;
      }
      else if (rotateAtom_1.isSelected()) {
         I_status = ROTATE_ATOM;
      }

      int L_status = FIXED_ATOM;
      if (rotateGroup_4.isSelected()) {
         L_status = ROTATE_GROUP;
      }
      else if (rotateAtom_4.isSelected()) {
         L_status = ROTATE_ATOM;
      }

      daddy.changeDihedralForSelectedAtoms(angle, I_status, L_status);
   }

   public void changeAngle_stateChanged(ChangeEvent e) {
      float value = changeAngle.getValue();
      float max = changeAngle.getMaximum();
      float min = changeAngle.getMinimum();
      oldAngle = (value - min) / max * 360.0f - 180.0f;
      //logger.info("Value: "+value+" Curr Bond: "+currentBond+" min: "+min+" max: "+max);

      if (setOnlySlider) {
         return;
      }

      setTextAreaOnly(oldAngle);

      if (changeAngle.getValueIsAdjusting() && !instantView.isSelected()) { //doing adjusting

      }
      else { //done adjusting
         modifyAngle(oldAngle);
      }

   }

   public JButton getHelpButton() {
      return helpButton;
   }
}

class JModifyTorsionDialog_changeAngle_changeAdapter
    implements ChangeListener {
   private JModifyTorsionDialog adaptee;
   JModifyTorsionDialog_changeAngle_changeAdapter(JModifyTorsionDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void stateChanged(ChangeEvent e) {
      adaptee.changeAngle_stateChanged(e);
   }
}

class JModifyTorsionDialog_angleValue_actionAdapter
    implements ActionListener {
   private JModifyTorsionDialog adaptee;
   JModifyTorsionDialog_angleValue_actionAdapter(JModifyTorsionDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.angleValue_actionPerformed(e);
   }
}

class JModifyTorsionDialog_Fixed_4_changeAdapter
    implements ChangeListener {
   private JModifyTorsionDialog adaptee;
   JModifyTorsionDialog_Fixed_4_changeAdapter(JModifyTorsionDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void stateChanged(ChangeEvent e) {
      adaptee.Fixed_4_stateChanged(e);
   }
}

class JModifyTorsionDialog_Fixed_1_changeAdapter
    implements ChangeListener {
   private JModifyTorsionDialog adaptee;
   JModifyTorsionDialog_Fixed_1_changeAdapter(JModifyTorsionDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void stateChanged(ChangeEvent e) {
      adaptee.Fixed_1_stateChanged(e);
   }
}

class JModifyTorsionDialog_Reset_actionAdapter
    implements ActionListener {
   private JModifyTorsionDialog adaptee;
   JModifyTorsionDialog_Reset_actionAdapter(JModifyTorsionDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.Reset_actionPerformed(e);
   }
}

class JModifyTorsionDialog_Finish_actionAdapter
    implements ActionListener {
   private JModifyTorsionDialog adaptee;
   JModifyTorsionDialog_Finish_actionAdapter(JModifyTorsionDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.Finish_actionPerformed(e);
   }
}

class JModifyTorsionDialog_Undo_actionAdapter
    implements ActionListener {
   private JModifyTorsionDialog adaptee;
   JModifyTorsionDialog_Undo_actionAdapter(JModifyTorsionDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.Undo_actionPerformed(e);
   }
}

class JModifyTorsionDialog_OK_button_actionAdapter
    implements ActionListener {
   private JModifyTorsionDialog adaptee;
   JModifyTorsionDialog_OK_button_actionAdapter(JModifyTorsionDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.OK_button_actionPerformed(e);
   }
}
