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

package cct.modelling.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import cct.interfaces.ForceFieldInterface;
import cct.math.MinimizerInterface;

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
public class JSetupEnergyDialog
    extends JDialog {

   ForceFieldInterface forceFields[] = null;
   MinimizerInterface minimizers[] = null;
   boolean okPressed = true;
   private int energySetupTab = 0;
   private int minimizationSetupTab = 1;
   float nbCutoff = 10;

   JTabbedPane jTabbedPane1 = new JTabbedPane();
   JPanel energyPanel = new JPanel();
   JLabel jLabel1 = new JLabel();
   JComboBox forceFieldComboBox = new JComboBox();
   JLabel jLabel2 = new JLabel();
   JFormattedTextField nbCutoffTextField = new JFormattedTextField();
   JCheckBox nbCutoffCheckBox = new JCheckBox();
   JLabel jLabel3 = new JLabel();
   JTextField vdw14TextField = new JTextField();
   JLabel jLabel4 = new JLabel();
   JTextField els14TextField = new JTextField();
   JCheckBox calculateElsCheckBox = new JCheckBox();
   JLabel jLabel5 = new JLabel();
   JComboBox dielFuncComboBox = new JComboBox();
   JLabel jLabel6 = new JLabel();
   JTextField dielConstTextField = new JTextField();
   JCheckBox ignoreAtomsCheckBox = new JCheckBox();
   JButton setupIgnoreAtomsButton = new JButton();
   JCheckBox constraintsCheckBox = new JCheckBox();
   JButton setupConstraintsButton = new JButton();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   JPanel jPanel3 = new JPanel();
   JButton CalcButton = new JButton();
   JButton cancelButton = new JButton();
   JButton helpButton = new JButton();
   JPanel minPanel = new JPanel();
   JLabel jLabel7 = new JLabel();
   JComboBox minimizersComboBox = new JComboBox();
   JPanel minimizationPanel = new JPanel();
   JCheckBox jCheckBox1 = new JCheckBox();
   JTextField jTextField1 = new JTextField();
   JLabel jLabel9 = new JLabel();
   Border border1 = BorderFactory.createLineBorder(SystemColor.controlShadow, 1);
   Border border2 = new TitledBorder(border1, "Termination Conditions");
   JCheckBox maxGradCheckBox = new JCheckBox();
   JTextField maxGradTextField = new JTextField();
   JLabel jLabel10 = new JLabel();
   JLabel jLabel8 = new JLabel();
   JPanel jPanel4 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   FlowLayout flowLayout1 = new FlowLayout();
   JTextField maxIterTextField = new JTextField();
   JPanel jPanel5 = new JPanel();
   JLabel jLabel12 = new JLabel();
   FlowLayout flowLayout2 = new FlowLayout();
   JSpinner refreshRateTextField = new JSpinner(new SpinnerNumberModel(1, 0, 1000,
       1));
   GridBagLayout gridBagLayout2 = new GridBagLayout();
   JCheckBox maxGRMSCheckBox = new JCheckBox();
   JTextField maxGRSMTextField = new JTextField();
   JLabel jLabel13 = new JLabel();

   public JSetupEnergyDialog(Frame owner, String title, boolean modal) {
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

   public JSetupEnergyDialog() {
      this(new Frame(), "JSetupEnergyDialog", false);
   }

   private void jbInit() throws Exception {
      jLabel2.setToolTipText("Cutoff for Nonbonded Interactions");
      nbCutoffTextField.setFormatterFactory(null);
      nbCutoffTextField.setToolTipText("Non-Bonded Cutoff Value");
      nbCutoffTextField.setText(String.valueOf(nbCutoff));
      nbCutoffTextField.setColumns(5);
      nbCutoffTextField.addActionListener(new
                                          JSetupEnergyDialog_nbCutoffTextField_actionAdapter(this));
      nbCutoffCheckBox.setEnabled(false);
      nbCutoffCheckBox.setToolTipText("");
      nbCutoffCheckBox.setText("Use NB Cutoff");
      nbCutoffCheckBox.addItemListener(new
                                       JSetupEnergyDialog_nbCutoffCheckBox_itemAdapter(this));
      jLabel3.setToolTipText("");
      jLabel3.setText("1-4 van der Waals Scaling: ");
      vdw14TextField.setToolTipText("");
      vdw14TextField.setText("2");
      vdw14TextField.setColumns(5);
      jLabel4.setToolTipText("");
      jLabel4.setText("1-4 Electrostatics Scaling: ");
      els14TextField.setToolTipText("");
      els14TextField.setText("1");
      els14TextField.setColumns(5);
      calculateElsCheckBox.setEnabled(false);
      calculateElsCheckBox.setToolTipText("");
      calculateElsCheckBox.setText("Calculate Electrostatics");
      calculateElsCheckBox.addItemListener(new
                                           JSetupEnergyDialog_calculateElsCheckBox_itemAdapter(this));
      jLabel5.setToolTipText("");
      jLabel5.setText("Dielectric Function: ");
      jLabel6.setToolTipText("");
      jLabel6.setText("Dielectric Constant: ");
      dielConstTextField.setToolTipText("");
      dielConstTextField.setText("1");
      dielConstTextField.setColumns(5);
      ignoreAtomsCheckBox.setEnabled(false);
      ignoreAtomsCheckBox.setToolTipText("");
      ignoreAtomsCheckBox.setText("Ignore Atoms");
      ignoreAtomsCheckBox.addItemListener(new
                                          JSetupEnergyDialog_ignoreAtomsCheckBox_itemAdapter(this));
      setupIgnoreAtomsButton.setText("Setup");
      constraintsCheckBox.setEnabled(false);
      constraintsCheckBox.setToolTipText("");
      constraintsCheckBox.setText("Constraints");
      constraintsCheckBox.addItemListener(new
                                          JSetupEnergyDialog_constraintsCheckBox_itemAdapter(this));
      setupConstraintsButton.setToolTipText("");
      setupConstraintsButton.setText("Setup");
      energyPanel.setLayout(gridBagLayout1);
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      CalcButton.setToolTipText("Start Calculation with selected parameters");
      CalcButton.setText("Calculate");
      CalcButton.addActionListener(new
                                   CalcButton_actionAdapter(this));
      cancelButton.setToolTipText("Cancel Setup");
      cancelButton.setText("Cancel");
      cancelButton.addActionListener(new
                                     JSetupEnergyDialog_cancelButton_actionAdapter(this));
      helpButton.setEnabled(false);
      helpButton.setToolTipText("Help on Energy Setup");
      helpButton.setText("Help");
      helpButton.addActionListener(new
                                   JSetupEnergyDialog_helpButton_actionAdapter(this));
      forceFieldComboBox.addActionListener(new
                                           JSetupEnergyDialog_forceFieldComboBox_actionAdapter(this));
      jLabel7.setToolTipText("");
      jLabel7.setHorizontalAlignment(SwingConstants.LEFT);
      jLabel7.setText("Method: ");
      jCheckBox1.setEnabled(false);
      jCheckBox1.setToolTipText("");
      jCheckBox1.setText("Min Function Change:");
      jTextField1.setEnabled(false);
      jTextField1.setToolTipText("");
      jTextField1.setText("0.01");
      jTextField1.setColumns(5);
      jLabel9.setToolTipText("");
      jLabel9.setText(" kcal/mol");
      minimizationPanel.setBorder(border2);
      minimizationPanel.setLayout(gridBagLayout2);
      maxGradCheckBox.setToolTipText("");
      maxGradCheckBox.setText("Max Gradient:");
      maxGradTextField.setToolTipText("");
      maxGradTextField.setText("0.5");
      maxGradTextField.setColumns(5);
      jLabel10.setToolTipText("");
      jLabel10.setText(" kcal/(mol*A)");
      jLabel8.setToolTipText("");
      jLabel8.setText("Max Iterations:");
      minPanel.setLayout(borderLayout1);
      jPanel4.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      maxIterTextField.setToolTipText("");
      maxIterTextField.setText("1000");
      maxIterTextField.setColumns(5);
      jLabel12.setToolTipText("");
      jLabel12.setText("Screen Refresh Period:");
      jPanel5.setLayout(flowLayout2);
      flowLayout2.setAlignment(FlowLayout.LEFT);
      refreshRateTextField.setToolTipText("");

      maxGRMSCheckBox.setToolTipText("");
      maxGRMSCheckBox.setText("Max RMS Gradient:");
      maxGRSMTextField.setToolTipText("");
      maxGRSMTextField.setText("0.25");
      maxGRSMTextField.setColumns(5);
      jLabel13.setToolTipText("");
      jLabel13.setText(" kcal/(mol*A)");
      this.getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
      jLabel1.setToolTipText("");
      jLabel1.setText("Force Field: ");
      jLabel2.setText("NB Cutoff: ");
      energyPanel.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST,
          GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(forceFieldComboBox,
                      new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      jPanel3.add(CalcButton);
      jPanel3.add(cancelButton);
      jPanel3.add(helpButton);
      this.getContentPane().add(jPanel3, BorderLayout.SOUTH);
      energyPanel.add(nbCutoffCheckBox,
                      new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(vdw14TextField,
                      new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(jLabel3, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
          , GridBagConstraints.EAST,
          GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      jPanel4.add(jLabel7);
      jPanel4.add(minimizersComboBox);
      jPanel5.add(jLabel12);
      jPanel5.add(refreshRateTextField);
      minPanel.add(minimizationPanel, BorderLayout.CENTER);
      minPanel.add(jPanel5, BorderLayout.SOUTH);
      minPanel.add(jPanel4, BorderLayout.NORTH);
      jTabbedPane1.add(energyPanel, "Energy Setup");
      jTabbedPane1.add(minPanel, "Minimization Setup");
      energyPanel.add(ignoreAtomsCheckBox,
                      new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.NORTHEAST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(setupIgnoreAtomsButton,
                      new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.NORTHWEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(constraintsCheckBox,
                      new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.NORTHEAST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(setupConstraintsButton,
                      new GridBagConstraints(3, 6, 1, 1, 0.0, 1.0
                                             , GridBagConstraints.NORTHWEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(calculateElsCheckBox,
                      new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(jLabel4, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(jLabel2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(nbCutoffTextField,
                      new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(jLabel5, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(dielFuncComboBox,
                      new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(jLabel6, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(dielConstTextField,
                      new GridBagConstraints(3, 5, 1, 1, 1.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      energyPanel.add(els14TextField,
                      new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(jCheckBox1,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(jTextField1,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(jLabel8,
                            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(maxIterTextField,
                            new GridBagConstraints(1, 3, 1, 1, 0.0, 1.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(maxGradTextField,
                            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(jLabel10,
                            new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(maxGRSMTextField,
                            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
      minimizationPanel.add(jLabel13,
                            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(jLabel9,
                            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(maxGRMSCheckBox,
                            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      minimizationPanel.add(maxGradCheckBox,
                            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      nbCutoffTextField.setEnabled(nbCutoffCheckBox.isSelected());

      els14TextField.setEnabled(calculateElsCheckBox.isSelected());
      dielFuncComboBox.setEnabled(calculateElsCheckBox.isSelected());
      dielConstTextField.setEnabled(calculateElsCheckBox.isSelected());

      setupIgnoreAtomsButton.setEnabled(ignoreAtomsCheckBox.isSelected());

      setupConstraintsButton.setEnabled(constraintsCheckBox.isSelected());

      dielFuncComboBox.addItem("Constant");
      dielFuncComboBox.addItem("Distance");
      dielFuncComboBox.setSelectedIndex(0);
   }

   public boolean areMinimizersSet() {
       return minimizers != null;
   }

   public void setMinimizers(MinimizerInterface mins[]) {
      if (mins == null || mins.length < 1) {
         return;
      }
      minimizers = mins;
      minimizersComboBox.removeAllItems();
      for (int i = 0; i < mins.length; i++) {
         minimizersComboBox.addItem(mins[i].getName());
      }
      this.selectMinimizer(mins[0].getName());
   }

   public MinimizerInterface getSelectedMinimizer() {
      if (minimizers == null || minimizers.length < 1) {
         return null;
      }
      return minimizers[minimizersComboBox.getSelectedIndex()];
   }

   public void setForceFields(ForceFieldInterface ff[]) {
      if (ff == null || ff.length < 1) {
         return;
      }
      forceFields = ff;
      forceFieldComboBox.removeAllItems();
      for (int i = 0; i < ff.length; i++) {
         forceFieldComboBox.addItem(ff[i].getName());
      }
      selectForceField(ff[0].getName());
   }

   public void selectMinimizer(String minName) {
      if (minimizers == null) {
         return;
      }
      minimizersComboBox.setEnabled(false);
      minimizersComboBox.setSelectedItem(minName);
      int n = minimizersComboBox.getSelectedIndex();
      MinimizerInterface min = minimizers[n];

      // Set Max iterations
      maxIterTextField.setEnabled(false);
      maxIterTextField.setText(String.valueOf(min.getMaxIterations()));
      maxIterTextField.setEnabled(true);

      // Max gradient

      if (min.useGradients()) {
         maxGradCheckBox.setSelected(min.isMaxGradConvergence());

         maxGradTextField.setEnabled(false);
         maxGradTextField.setText(String.valueOf(min.getMaxGradientConv()));
         maxGradTextField.setEnabled(maxGradCheckBox.isSelected());

         maxGRMSCheckBox.setSelected(min.isMaxRMSGradConvergence());
         maxGRSMTextField.setEnabled(false);
         maxGRSMTextField.setText(String.valueOf(min.getMaxRMSGradientConv()));
         maxGRSMTextField.setEnabled(true);
      }
      else {
         maxGradCheckBox.setEnabled(false);
         maxGradTextField.setEnabled(false);

         maxGRMSCheckBox.setEnabled(false);
         maxGRSMTextField.setEnabled(false);
      }

      minimizersComboBox.setEnabled(true);
   }

   public void selectForceField(String ffName) {
      if (forceFields == null) {
         return;
      }
      forceFieldComboBox.setEnabled(false);
      forceFieldComboBox.setSelectedItem(ffName);
      int n = forceFieldComboBox.getSelectedIndex();
      ForceFieldInterface ff = forceFields[n];

      // --- Electrostatic setup
      calculateElsCheckBox.setSelected(ff.isCalculateElectrostatics());
      dielFuncComboBox.setEnabled(calculateElsCheckBox.isSelected());

      dielFuncComboBox.setSelectedIndex(ff.getDielectricFunction());

      dielConstTextField.setEnabled(calculateElsCheckBox.isSelected());
      dielConstTextField.setText(String.valueOf(ff.getDielectricConstant()));

      nbCutoffCheckBox.setSelected(ff.isUseNBCutoff());
      nbCutoffTextField.setEnabled(nbCutoffCheckBox.isSelected());
      nbCutoffTextField.setText(String.valueOf(ff.getNBCutoff()));

      // --- 1-4 setup

      vdw14TextField.setEnabled(false);
      vdw14TextField.setText(String.valueOf(ff.get14NBScale()));
      vdw14TextField.setEnabled(true);

      els14TextField.setEnabled(false);
      els14TextField.setText(String.valueOf(ff.get14ElsScale()));
      els14TextField.setEnabled(true);
      els14TextField.setEnabled(calculateElsCheckBox.isSelected());

      forceFieldComboBox.setEnabled(true);

   }

   public void energySetupMode() {
      jTabbedPane1.setSelectedIndex(energySetupTab);
      jTabbedPane1.setEnabledAt(minimizationSetupTab, false);
   }

   public void minimizationSetupMode() {
      jTabbedPane1.setEnabledAt(minimizationSetupTab, true);
      jTabbedPane1.setSelectedIndex(minimizationSetupTab);
   }

   public void nbCutoffCheckBox_itemStateChanged(ItemEvent e) {
      nbCutoffTextField.setEnabled(nbCutoffCheckBox.isSelected());
      int n = forceFieldComboBox.getSelectedIndex();
      ForceFieldInterface ff = forceFields[n];
      ff.enableUseNBCutoff(nbCutoffCheckBox.isSelected());
   }

   public boolean isOkPressed() {
      return okPressed;
   }

   public void cancelButton_actionPerformed(ActionEvent e) {
      okPressed = false;
      setVisible(false);
   }

   public void CalcButton_actionPerformed(ActionEvent e) {

      // First, process energy setup (always)

      int n = forceFieldComboBox.getSelectedIndex();
      ForceFieldInterface ff = forceFields[n];

      // --- Use NB Cutoff

      ff.enableUseNBCutoff(nbCutoffCheckBox.isSelected());

      // --- Do calculate electrostatics?

      ff.enableCalculateElectrostatics(calculateElsCheckBox.isSelected());

      // --- Dielectric function

      ff.setDielectricFunction(dielFuncComboBox.getSelectedIndex());

      // --- Parse cutoff

      float cutoff = 0;
      try {
         cutoff = parseCutoff(nbCutoffTextField.getText());
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this,
                                       ex.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         return;
      }
      ff.setNBCutoff(cutoff);

      // --- Parse 1-4 vdw

      float vdw14 = 0;
      try {
         vdw14 = parsePositiveFloat(vdw14TextField.getText());
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this,
                                       "1-4 VDW Scale factor: " + ex.getMessage(),
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
         return;
      }
      ff.set14NBScale(vdw14);

      // --- Parse 1-4 els

      if (calculateElsCheckBox.isSelected()) {
         float els14 = 0;
         try {
            els14 = parsePositiveFloat(els14TextField.getText());
         }
         catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                                          "1-4 Electrostatics Scale factor: " +
                                          ex.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
         }
         ff.set14ElsScale(els14);
      }

      // --- Dielectric constant

      if (calculateElsCheckBox.isSelected()) {
         float dielC = 0;
         try {
            dielC = parsePositiveFloat(dielConstTextField.getText());
         }
         catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                                          "Dielectric Constant: " +
                                          ex.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
         }
         ff.setDielectricConstant(dielC);
      }

      // -- Now process minimization setup (if needed)

      if (jTabbedPane1.isEnabledAt(minimizationSetupTab)) {
         int m = minimizersComboBox.getSelectedIndex();
         MinimizerInterface min = minimizers[m];

         // --- Parse Max iterations
         int maxIter = 0;
         try {
            maxIter = parsePositiveInteger(maxIterTextField.getText());
         }
         catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                                          "Max Iterations: " +
                                          ex.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
         }
         min.setMaxIterations(maxIter);

         // --- Enable max rms grad convergence

         min.enableMaxGRMSConvergence(maxGRMSCheckBox.isSelected());

         // --- Max GRMS

         float maxGRMS = 0;
         try {
            maxGRMS = parsePositiveFloat(this.maxGRSMTextField.getText());
         }
         catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                                          "Max RMS Gradient: " +
                                          ex.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
         }
         min.setMaxRMSGradientConv(maxGRMS);

         // --- Enable max grad convergence

         min.enableMaxGradConvergence(maxGradCheckBox.isSelected());

         // --- max gradient

         float maxG = 0;
         try {
            maxG = parsePositiveFloat(maxGradTextField.getText());
         }
         catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                                          "Max Gradient: " +
                                          ex.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
         }
         min.setMaxGradientConv(maxG);

      }

      okPressed = true;
      setVisible(false);
   }

   public void nbCutoffTextField_actionPerformed(ActionEvent e) {
      if (!nbCutoffTextField.isEnabled()) {
         return;
      }
      nbCutoffTextField.setEnabled(false);
      float cutoff = 0;
      try {
         cutoff = parseCutoff(nbCutoffTextField.getText());
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this,
                                       ex.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         nbCutoffTextField.setEnabled(true);
         return;
      }

      int n = forceFieldComboBox.getSelectedIndex();
      ForceFieldInterface ff = forceFields[n];
      ff.setNBCutoff(cutoff);
      nbCutoffTextField.setEnabled(true);
   }

   public boolean isUseCutoff() {
      return nbCutoffCheckBox.isSelected();
   }

   public int getScreenRefreshRate() {
      int rate = 1;
      try {
         String size = refreshRateTextField.getModel().getValue().toString();
         rate = Integer.parseInt(size);
      }
      catch (NumberFormatException nfe) {
      }
      return rate;
   }

   public int getSelectedFFIndex() {
      return forceFieldComboBox.getSelectedIndex();
   }

   public ForceFieldInterface getSelectedFF() {
      return forceFields[getSelectedFFIndex()];
   }

   public static float parseCutoff(String text) throws Exception {
      float cutoff = 0.0f;
      try {
         cutoff = Float.parseFloat(text.trim());
      }
      catch (Exception ex) {
         throw new Exception("Wrong number for cutoff: " +
                             text + " : " +
                             ex.getMessage());
      }

      if (cutoff < 0) {
         throw new Exception(
             "Cutoff should be positive number. Current value: " +
             text);
      }

      return cutoff;
   }

   public static int parsePositiveInteger(String text) throws Exception {
      int number = 0;
      try {
         number = Integer.parseInt(text.trim());
      }
      catch (Exception ex) {
         throw new Exception("Wrong number: " + text + " : " +
                             ex.getMessage());
      }

      if (number < 0) {
         throw new Exception(
             "Number should be positive. Current value: " +
             text);
      }

      return number;
   }

   public static float parsePositiveFloat(String text) throws Exception {
      float cutoff = 0.0f;
      try {
         cutoff = Float.parseFloat(text.trim());
      }
      catch (Exception ex) {
         throw new Exception("Wrong number: " + text + " : " +
                             ex.getMessage());
      }

      if (cutoff < 0) {
         throw new Exception(
             "Number should be positive. Current value: " +
             text);
      }

      return cutoff;
   }

   public void calculateElsCheckBox_itemStateChanged(ItemEvent e) {
      els14TextField.setEnabled(calculateElsCheckBox.isSelected());
      dielFuncComboBox.setEnabled(calculateElsCheckBox.isSelected());
      dielConstTextField.setEnabled(calculateElsCheckBox.isSelected());
   }

   public void forceFieldComboBox_actionPerformed(ActionEvent e) {
      if (!forceFieldComboBox.isEnabled()) {
         return;
      }
      forceFieldComboBox.setEnabled(false);
      selectForceField(forceFieldComboBox.getSelectedItem().toString());
      forceFieldComboBox.setEnabled(true);
   }

   public void helpButton_actionPerformed(ActionEvent e) {
      JOptionPane.showMessageDialog(this,
                                    "It's very easy to setup energy",
                                    "Help",
                                    JOptionPane.INFORMATION_MESSAGE);

   }

   public void ignoreAtomsCheckBox_itemStateChanged(ItemEvent e) {
      setupIgnoreAtomsButton.setEnabled(ignoreAtomsCheckBox.isSelected());
   }

   public void constraintsCheckBox_itemStateChanged(ItemEvent e) {
      setupConstraintsButton.setEnabled(constraintsCheckBox.isSelected());
   }

   private class JSetupEnergyDialog_constraintsCheckBox_itemAdapter
       implements ItemListener {
      private JSetupEnergyDialog adaptee;
      JSetupEnergyDialog_constraintsCheckBox_itemAdapter(JSetupEnergyDialog
          adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void itemStateChanged(ItemEvent e) {
         adaptee.constraintsCheckBox_itemStateChanged(e);
      }
   }

}

class JSetupEnergyDialog_ignoreAtomsCheckBox_itemAdapter
    implements ItemListener {
   private JSetupEnergyDialog adaptee;
   JSetupEnergyDialog_ignoreAtomsCheckBox_itemAdapter(JSetupEnergyDialog
       adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void itemStateChanged(ItemEvent e) {
      adaptee.ignoreAtomsCheckBox_itemStateChanged(e);
   }
}

class JSetupEnergyDialog_helpButton_actionAdapter
    implements ActionListener {
   private JSetupEnergyDialog adaptee;
   JSetupEnergyDialog_helpButton_actionAdapter(JSetupEnergyDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.helpButton_actionPerformed(e);
   }
}

class JSetupEnergyDialog_forceFieldComboBox_actionAdapter
    implements ActionListener {
   private JSetupEnergyDialog adaptee;
   JSetupEnergyDialog_forceFieldComboBox_actionAdapter(JSetupEnergyDialog
       adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.forceFieldComboBox_actionPerformed(e);
   }
}

class JSetupEnergyDialog_calculateElsCheckBox_itemAdapter
    implements ItemListener {
   private JSetupEnergyDialog adaptee;
   JSetupEnergyDialog_calculateElsCheckBox_itemAdapter(JSetupEnergyDialog
       adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void itemStateChanged(ItemEvent e) {
      adaptee.calculateElsCheckBox_itemStateChanged(e);
   }
}

class JSetupEnergyDialog_nbCutoffTextField_actionAdapter
    implements ActionListener {
   private JSetupEnergyDialog adaptee;
   JSetupEnergyDialog_nbCutoffTextField_actionAdapter(JSetupEnergyDialog
       adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.nbCutoffTextField_actionPerformed(e);
   }
}

class CalcButton_actionAdapter
    implements ActionListener {
   private JSetupEnergyDialog adaptee;
   CalcButton_actionAdapter(JSetupEnergyDialog adaptee) {
      this.adaptee = adaptee;
   }

   public void actionPerformed(ActionEvent e) {
      adaptee.CalcButton_actionPerformed(e);
   }
}

class JSetupEnergyDialog_nbCutoffCheckBox_itemAdapter
    implements ItemListener {
   private JSetupEnergyDialog adaptee;
   JSetupEnergyDialog_nbCutoffCheckBox_itemAdapter(JSetupEnergyDialog adaptee) {
      this.adaptee = adaptee;
   }

   public void itemStateChanged(ItemEvent e) {
      adaptee.nbCutoffCheckBox_itemStateChanged(e);
   }
}

class JSetupEnergyDialog_cancelButton_actionAdapter
    implements ActionListener {
   private JSetupEnergyDialog adaptee;
   JSetupEnergyDialog_cancelButton_actionAdapter(JSetupEnergyDialog adaptee) {
      this.adaptee = adaptee;
   }

   public void actionPerformed(ActionEvent e) {
      adaptee.cancelButton_actionPerformed(e);
   }
}
