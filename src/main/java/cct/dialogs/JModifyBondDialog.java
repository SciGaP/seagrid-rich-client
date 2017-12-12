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

import cct.interfaces.MoleculeInterface;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cct.j3d.Java3dUniverse;
import cct.modelling.MolecularEditor;
import cct.modelling.Molecule;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.WindowConstants;

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
public class JModifyBondDialog
        extends JDialog {

  JPanel mainPanel = new JPanel();
  JPanel Displ_panel = new JPanel();
  ButtonGroup Atom_1 = new ButtonGroup();
  JRadioButton Fixed_1 = new JRadioButton();
  JRadioButton translateAtom_1 = new JRadioButton();
  JRadioButton translateGroup_1 = new JRadioButton();
  ButtonGroup Atom_2 = new ButtonGroup();
  JRadioButton translateGroup_2 = new JRadioButton();
  JRadioButton Fixed_2 = new JRadioButton();
  JRadioButton translateAtom_2 = new JRadioButton();
  JPanel Displace_1 = new JPanel();

  JPanel Displace_2 = new JPanel();
  JPanel changeBondPanel = new JPanel();
  JSlider bondLengthSlider = new JSlider();
  JPanel bondInfoPanel = new JPanel();
  JLabel minBond = new JLabel();
  JLabel maxBond = new JLabel();
  JTextField currentBondLength = new JTextField();
  GridLayout gridLayout1 = new GridLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JPanel bondPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel buttonsPanel = new JPanel();
  JButton Cancel = new JButton();
  public JButton Help = new JButton();
  JButton Ok = new JButton();
  JCheckBox drawBond = new JCheckBox();

  // --- My variables
  MolecularEditor daddy = null;
  float bondMin = 0.001f;
  float bondMax = 10.0f;
  float currentBond = 5.0f;
  boolean setSlideOnly = false;

  // --- Store variables
  float oldBondLength = 0.0f;
  boolean oldDrawBond = true;
  JPanel checkboxesPanel = new JPanel();
  JCheckBox instantView = new JCheckBox();
  GridLayout gridLayout2 = new GridLayout();
  JButton undoLastSelection = new JButton();
  JButton Reset = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();

  public JModifyBondDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    try {
      this.addWindowListener(new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent we) {
          CloseWindowGracefully();
        }
      });
      //setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JModifyBondDialog() {
    this(new Frame(), "JModifyBondDialog", false);
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    mainPanel.setLayout(gridBagLayout3);
    Fixed_1.setText("jRadioButton1");
    translateAtom_1.setToolTipText("Translate only Selected Atom");
    translateAtom_1.setText("Translate this Atom only");
    translateAtom_1.addChangeListener(new JModifyBondDialog_translateAtom_1_changeAdapter(this));
    translateGroup_1.setToolTipText("Translate Atom and Group it belongs to");
    translateGroup_1.setSelected(true);
    translateGroup_1.setText("Translate group");
    translateGroup_1.addChangeListener(new JModifyBondDialog_translateGroup_1_changeAdapter(this));
    translateGroup_2.setToolTipText("Translate Atom and Group it belongs to");
    translateGroup_2.setSelected(true);
    translateGroup_2.setText("Translate Group");
    translateGroup_2.addChangeListener(new JModifyBondDialog_translateGroup_2_changeAdapter(this));
    Fixed_2.setToolTipText("Keep Atom position fixed");
    Fixed_2.setText("Fixed Atom Position");
    Fixed_2.addChangeListener(new JModifyBondDialog_Fixed_2_changeAdapter(this));
    translateAtom_2.setToolTipText("Translate only Selected Atom");
    translateAtom_2.setText("Translate this Atom only");
    translateAtom_2.addChangeListener(new JModifyBondDialog_translateAtom_2_changeAdapter(this));
    Displace_1.setLayout(gridBagLayout2);
    Fixed_1.setToolTipText("Keep Atom position fixed");
    Fixed_1.setActionCommand("Fixed_1");
    Fixed_1.setText("Fixed Atom Position");
    Fixed_1.addChangeListener(new JModifyBondDialog_Fixed_1_changeAdapter(this));
    Displace_2.setLayout(gridBagLayout1);
    Displace_1.setBorder(new TitledBorder(BorderFactory.createLineBorder(
            Color.lightGray, 1), "Atom 1 Displacement"));
    Displace_2.setBorder(new TitledBorder(BorderFactory.createLineBorder(
            Color.lightGray, 1), "Atom 2 Displacement"));
    changeBondPanel.setLayout(borderLayout2);
    bondLengthSlider.setMajorTickSpacing(10);
    bondLengthSlider.setPaintLabels(false);
    bondLengthSlider.setPaintTicks(true);
    bondLengthSlider.setToolTipText("Drag the slider to update Bond Length");
    bondLengthSlider.addChangeListener(new JModifyBondDialog_bondLengthSlider_changeAdapter(this));
    //bondLengthSlider.addChangeListener(new
    //                                   JModifyBondDialog_bondLengthSlider_changeAdapter(this));
    bondInfoPanel.setLayout(gridLayout1);
    minBond.setToolTipText("Minimal Bond Length Value");
    minBond.setHorizontalAlignment(SwingConstants.LEFT);
    minBond.setText("0.0");
    maxBond.setToolTipText("Maximal Bond Length Available");
    maxBond.setHorizontalAlignment(SwingConstants.RIGHT);
    maxBond.setText("10");
    currentBondLength.setText("5");
    currentBondLength.setToolTipText(
            "Current Bond Length; Type and press Enter for Value");
    currentBondLength.setColumns(8);
    currentBondLength.setHorizontalAlignment(SwingConstants.CENTER);
    currentBondLength.addActionListener(new JModifyBondDialog_currentBondLength_actionAdapter(this));
    gridLayout1.setHgap(10);
    jLabel1.setText("");
    jLabel2.setText("");
    bondPanel.setLayout(borderLayout3);
    bondPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1), "Bond Length Control"));
    bondPanel.setToolTipText(
            "Bond Length Contols; For longer Bond Length type its value in textbox");
    Displ_panel.setLayout(borderLayout1);
    Cancel.setToolTipText("Exit Bond Editing");
    Cancel.setText("Finish");
    Cancel.addActionListener(new JModifyBondDialog_Cancel_actionAdapter(this));
    Help.setEnabled(false);
    Help.setText("Help");
    Ok.setText("Accept");
    Ok.setVisible(true);
    Ok.setToolTipText("Accept Changes and start new selection");
    Ok.addActionListener(new JModifyBondDialog_Ok_actionAdapter(this));
    drawBond.setSelected(true);
    drawBond.setText("Connect Atoms with Bond");
    drawBond.addChangeListener(new JModifyBondDialog_drawBond_changeAdapter(this));
    checkboxesPanel.setLayout(gridLayout2);
    instantView.setToolTipText("See changes in bond length while drugging");
    instantView.setHorizontalAlignment(SwingConstants.RIGHT);
    instantView.setSelected(true);
    instantView.setText("Instant View");
    undoLastSelection.setToolTipText("Deselect last selected Atom");
    undoLastSelection.setText("Undo Last Selection");
    undoLastSelection.addActionListener(new JModifyBondDialog_undoLastSelection_actionAdapter(this));
    Reset.setToolTipText("Undo all editing");
    Reset.setText("Reset");
    Reset.addActionListener(new JModifyBondDialog_Reset_actionAdapter(this));
    getContentPane().add(mainPanel);
    checkboxesPanel.add(drawBond, null);
    checkboxesPanel.add(instantView);
    buttonsPanel.add(Ok);
    buttonsPanel.add(Reset);
    buttonsPanel.add(Cancel);
    buttonsPanel.add(undoLastSelection);
    buttonsPanel.add(Help);
    Displ_panel.add(Displace_2, BorderLayout.EAST);
    Displ_panel.add(Displace_1, BorderLayout.WEST);
    bondInfoPanel.add(minBond, null);
    bondInfoPanel.add(jLabel1);
    bondInfoPanel.add(currentBondLength, null);
    bondInfoPanel.add(jLabel2);
    bondInfoPanel.add(maxBond, null);
    Atom_1.add(translateGroup_1);
    Atom_1.add(translateAtom_1);
    Atom_1.add(Fixed_1);

    Atom_2.add(translateGroup_2);
    Atom_2.add(translateAtom_2);
    Atom_2.add(Fixed_2);
    Displace_2.add(translateGroup_2,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE,
                    new Insets(2, 2, 2, 2), 0, 0)); //minBond.setText(String.valueOf(bondMin));
    Displace_2.add(translateAtom_2,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE,
                    new Insets(2, 2, 2, 2), 0, 0));
    Displace_2.add(Fixed_2, new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    Displace_1.add(translateGroup_1,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE,
                    new Insets(2, 2, 2, 2), 0, 0));
    Displace_1.add(translateAtom_1,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE,
                    new Insets(2, 2, 2, 2), 0, 0));
    Displace_1.add(Fixed_1, new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(2, 2, 2, 2), 0, 0));
    changeBondPanel.add(bondLengthSlider, BorderLayout.NORTH);
    bondPanel.add(changeBondPanel, BorderLayout.CENTER);
    bondPanel.add(bondInfoPanel, BorderLayout.NORTH);
    mainPanel.add(Displ_panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(checkboxesPanel,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(bondPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 5, 5), 0, 0));
    mainPanel.add(buttonsPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
    setNewBondBounds();

  }

  public void bondLengthSlider_stateChanged(ChangeEvent e) {

    //bondLengthSlider.removeChangeListener(bondLengthSlider_changeAdapter);
    float value = bondLengthSlider.getValue();
    float max = bondLengthSlider.getMaximum();
    float min = bondLengthSlider.getMinimum();
    currentBond = bondMin + (value - min) / max * (bondMax - bondMin);
    //logger.info("Value: "+value+" Curr Bond: "+currentBond+" min: "+min+" max: "+max);

    if (setSlideOnly) {
      return;
    }

    currentBondLength.setText(String.format("%8.4f", currentBond));

    if (bondLengthSlider.getValueIsAdjusting() && !instantView.isSelected()) { //doing adjusting

    } else { //done adjusting

      setNewDistance(currentBond);
    }

  }

  void setNewDistance(float new_bond) {
    int I_status = 0;
    if (translateAtom_1.isSelected()) {
      I_status = 1;
    } else if (translateGroup_1.isSelected()) {
      I_status = 2;
    }

    int J_status = 0;
    if (translateAtom_2.isSelected()) {
      J_status = 1;
    } else if (translateGroup_2.isSelected()) {
      J_status = 2;
    }

    if (daddy instanceof Java3dUniverse) {
      Java3dUniverse target = (Java3dUniverse) daddy;
      target.changeDistanceBetweenSelectedAtoms(new_bond, I_status, J_status);
    }
  }

  public void setTargetClass(MolecularEditor target) {
    daddy = target;
  }

  public void setBondLenght(float length) {
    setSlideOnly = true;
    oldBondLength = length;
    currentBond = length;
    bondMax = currentBond * 2.0f;
    setNewBondBounds();
    setSlideOnly = false;
  }

  public void setDrawBond(boolean draw) {
    drawBond.setSelected(draw);
  }

  private void setNewBondBounds() {
    maxBond.setText(String.valueOf(bondMax));
    currentBondLength.setText(String.valueOf(currentBond));
    setSlider(currentBond);
  }

  private void setSlider(float value) {
    boolean store = instantView.isSelected();
    instantView.setSelected(false);
    bondLengthSlider.setValueIsAdjusting(true);
    bondLengthSlider.setValue((int) (value / bondMax * 100.0f));
    bondLengthSlider.setValueIsAdjusting(false);
    instantView.setSelected(store);

    //bondLengthSlider.setValue( (int) ( (value - bondMin) / bondMax *
    //                                  100.0f));
  }

  public void Ok_actionPerformed(ActionEvent e) {
    daddy.confirmChanges();
  }

  public void CloseWindowGracefully() {
    daddy.endProcessingSelectedAtoms();
    setVisible(false);

  }

  public void Cancel_actionPerformed(ActionEvent e) {
    CloseWindowGracefully();
  }

  public void Fixed_1_stateChanged(ChangeEvent e) {
    if (Fixed_1.isSelected()) {
      Fixed_2.setEnabled(false);
    } else {
      Fixed_2.setEnabled(true);
    }
  }

  public void Fixed_2_stateChanged(ChangeEvent e) {
    if (Fixed_2.isSelected()) {
      Fixed_1.setEnabled(false);
    } else {
      Fixed_1.setEnabled(true);
    }
  }

  public void translateGroup_2_itemStateChanged(ItemEvent e) {
    if (translateGroup_2.isSelected()) {
      Fixed_1.setEnabled(true);
    }
  }

  public void translateAtom_2_itemStateChanged(ItemEvent e) {
    if (translateAtom_2.isSelected()) {
      Fixed_1.setEnabled(true);
    }
  }

  public void drawBond_itemStateChanged(ItemEvent e) {
    if (daddy instanceof Java3dUniverse) {
      Java3dUniverse target = (Java3dUniverse) daddy;
      target.drawBondBetweenSelectedAtoms(drawBond.isSelected());
    }
  }

  public void translateAtom_1_stateChanged(ChangeEvent e) {
    if (translateAtom_1.isSelected()) {
      Fixed_2.setEnabled(true);
    }
  }

  public void translateGroup_1_stateChanged(ChangeEvent e) {
    if (translateGroup_1.isSelected()) {
      Fixed_2.setEnabled(true);
    }
  }

  public void translateGroup_2_stateChanged(ChangeEvent e) {
    if (translateGroup_2.isSelected()) {
      Fixed_1.setEnabled(true);
    }
  }

  public void translateAtom_2_stateChanged(ChangeEvent e) {
    if (translateAtom_2.isSelected()) {
      Fixed_1.setEnabled(true);
    }

  }

  public void drawBond_stateChanged(ChangeEvent e) {
    if (daddy instanceof Java3dUniverse) {
      Java3dUniverse target = (Java3dUniverse) daddy;
      target.drawBondBetweenSelectedAtoms(drawBond.isSelected());
    }

  }

  public void currentBondLength_actionPerformed(ActionEvent e) {

    String new_value = currentBondLength.getText();
    float value;
    try {
      value = Float.parseFloat(new_value);
    } catch (NumberFormatException ex) {

      return;
    }

    if (value >= bondMax) {
      setBondLenght(value);
    } else if (value < 0.001f) {
      value = 0.001f;
    }

    // -- now set slider
    setSlideOnly = true;
    setSlider(value);
    setSlideOnly = false;

    setNewDistance(value);
    //currentBondLength.setText(String.format("%8.4f", value));
  }

  public void undoLastSelection_actionPerformed(ActionEvent e) {
    daddy.undoLastSelection();
  }

  public void Reset_actionPerformed(ActionEvent e) {
    daddy.resetGeometry();
    setBondLenght(oldBondLength);
  }
}

class JModifyBondDialog_undoLastSelection_actionAdapter
        implements ActionListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_undoLastSelection_actionAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.undoLastSelection_actionPerformed(e);
  }
}

class JModifyBondDialog_currentBondLength_actionAdapter
        implements ActionListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_currentBondLength_actionAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    adaptee.currentBondLength_actionPerformed(e);
  }
}

class JModifyBondDialog_drawBond_changeAdapter
        implements ChangeListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_drawBond_changeAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.drawBond_stateChanged(e);
  }
}

class JModifyBondDialog_translateAtom_2_changeAdapter
        implements ChangeListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_translateAtom_2_changeAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.translateAtom_2_stateChanged(e);
  }
}

class JModifyBondDialog_translateGroup_2_changeAdapter
        implements ChangeListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_translateGroup_2_changeAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.translateGroup_2_stateChanged(e);
  }
}

class JModifyBondDialog_translateGroup_1_changeAdapter
        implements ChangeListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_translateGroup_1_changeAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.translateGroup_1_stateChanged(e);
  }
}

class JModifyBondDialog_Fixed_2_changeAdapter
        implements ChangeListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_Fixed_2_changeAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.Fixed_2_stateChanged(e);
  }
}

class JModifyBondDialog_Fixed_1_changeAdapter
        implements ChangeListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_Fixed_1_changeAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.Fixed_1_stateChanged(e);
  }
}

class JModifyBondDialog_Cancel_actionAdapter
        implements ActionListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_Cancel_actionAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Cancel_actionPerformed(e);
  }
}

class JModifyBondDialog_Ok_actionAdapter
        implements ActionListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_Ok_actionAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Ok_actionPerformed(e);
  }
}

class JModifyBondDialog_Reset_actionAdapter
        implements ActionListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_Reset_actionAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Reset_actionPerformed(e);
  }
}

// --- End of Class
class JModifyBondDialog_bondLengthSlider_changeAdapter
        implements ChangeListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_bondLengthSlider_changeAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {

    adaptee.bondLengthSlider_stateChanged(e);
  }
}

class JModifyBondDialog_translateAtom_1_changeAdapter
        implements ChangeListener {

  private JModifyBondDialog adaptee;

  JModifyBondDialog_translateAtom_1_changeAdapter(JModifyBondDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.translateAtom_1_stateChanged(e);
  }
}
