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

import cct.interfaces.AtomInterface;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import cct.modelling.*;
import cct.tools.ui.TaskProgessDialog;
import java.awt.*;
import javax.swing.*;

/**
 * <p>
 * Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class AtomSelectionDialog
        extends JDialog implements OperationsOnAtoms, ActionListener {

  enum SELECTION_TYPE {

    SELECT_ALL_ATOMS, PROCESS_SELECTED_ATOMS, CLEAR_SELECTION, INVERT_SELECTION, SELECT_ATOM_TYPES, SELECT_MONOMER_TYPES,
    SELECT_SUBSTRUCTURE, SELECT_SETS, SELECT_WITHIN_SPHERE, CANCEL_DIALOG
  }
  JButton Invert = new JButton();
  JButton Clear = new JButton();
  JButton All = new JButton();
  JButton Undo = new JButton();
  JPanel quickSelPanel = new JPanel();
  JButton atomTypes = new JButton();
  JButton monomer = new JButton();
  JButton Sphere = new JButton();
  JButton Substructure = new JButton();
  JButton Sets = new JButton();
  JPanel specialPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JRadioButton typeMonomer = new JRadioButton();
  JRadioButton typeMolecule = new JRadioButton();
  JRadioButton typeAtom = new JRadioButton();
  JPanel selTypePanel = new JPanel();
  ButtonGroup buttonGroup1 = new ButtonGroup();
  GridLayout gridLayout1 = new GridLayout();
  JRadioButton Union = new JRadioButton();
  JRadioButton Difference = new JRadioButton();
  JRadioButton Intersection = new JRadioButton();
  JPanel selRulePanel = new JPanel();
  GridLayout gridLayout2 = new GridLayout();
  ButtonGroup buttonGroup2 = new ButtonGroup();
  JPanel P1 = new JPanel();
  GridLayout gridLayout3 = new GridLayout();
  JPanel P2 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel P3 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JCheckBox highlightCheckBox = new JCheckBox();
  private JLabel jLabel1 = new JLabel();
  private JTextField numSelectedAtomsTextField = new JTextField();
  private JPanel infoPanel = new JPanel();
  private JPanel buttonPanel = new JPanel();
  private JButton Cancel = new JButton();
  private JButton OK = new JButton();
  private JButton Help = new JButton();
  private Java3dUniverse daddy = null;
  private Frame parentFrame = null;
  private JSelectAtomTypesDialog jSelectAtomTypesDialog = null;
  private JSelectMonomersDialog jSelectMonomersDialog = null;
  private JSelectSubstructureDialog jSelectSubstructureDialog = null;
  private JDialog nextDialog = null;
  private JPanel jPanel2 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private FlowLayout flowLayout1 = new FlowLayout();
  private JComponent userComponent = null;
  private TaskProgessDialog taskProgessDialog = null;

  public AtomSelectionDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    parentFrame = owner;
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public AtomSelectionDialog() {
    this(new Frame(), "Atom Selection", false);
  }

  public JButton getHelpButton() {
    return Help;
  }

  public void setNextDialog(JDialog next) {
    nextDialog = next;
  }

  public void addUserComponent(JComponent uC) {
    if (userComponent != null) {
      infoPanel.remove(userComponent);
    }
    userComponent = uC;
    infoPanel.add(userComponent, BorderLayout.SOUTH);
    this.validate();
    this.pack();
  }

  private void jbInit() throws Exception {
    Invert.setToolTipText("Invert Selection");
    Invert.setText("Invert");
    //Invert.addActionListener(new JAtomSelectionDialog_Invert_actionAdapter(this));
    Invert.addActionListener(this);
    Clear.setToolTipText("Clear Selection");
    Clear.setText("Clear");
    Clear.addActionListener(new JAtomSelectionDialog_Clear_actionAdapter(this));
    All.setToolTipText("Select all atoms");
    All.setText("All");
    All.addActionListener(new JAtomSelectionDialog_All_actionAdapter(this));
    Undo.setEnabled(false);
    Undo.setToolTipText("Undo last Selection");
    Undo.setText("Undo");
    atomTypes.setToolTipText("Select Atoms of particular Type");
    atomTypes.setText("Atom Types");
    atomTypes.addActionListener(new JAtomSelectionDialog_atomTypes_actionAdapter(this));
    monomer.setToolTipText("Select all Atoms of particular Monomers");
    monomer.setVerifyInputWhenFocusTarget(false);
    monomer.setText("Monomer Types");
    monomer.addActionListener(new JAtomSelectionDialog_monomer_actionAdapter(this));
    Sphere.setEnabled(true);
    Sphere.setToolTipText("Select all Atoms within specified radius");
    Sphere.setText("Sphere");
    Sphere.addActionListener(this);
    Substructure.setToolTipText("Select Atoms in Substructure");
    Substructure.setText("Substructure");
    Substructure.addActionListener(new JAtomSelectionDialog_Substructure_actionAdapter(this));
    Sets.setToolTipText("Select Atoms in Set(s)");
    Sets.setText("Sets");
    Sets.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Sets_actionPerformed(e);
      }
    });
    specialPanel.setLayout(gridBagLayout1);
    typeMonomer.setToolTipText("Select all atoms in monomer");
    typeMonomer.setText("Monomer");
    typeMonomer.addChangeListener(new JAtomSelectionDialog_typeMonomer_changeAdapter(this));
    typeMolecule.setToolTipText("Select all atoms in Molecule");
    typeMolecule.setText("Molecule");
    typeMolecule.addChangeListener(new JAtomSelectionDialog_typeMolecule_changeAdapter(this));
    typeAtom.setToolTipText("Select Atom");
    typeAtom.setSelected(true);
    typeAtom.setText("Atom");
    typeAtom.addChangeListener(new JAtomSelectionDialog_typeAtom_changeAdapter(this));
    selTypePanel.setLayout(gridLayout1);
    gridLayout1.setColumns(-1);
    gridLayout1.setRows(3);
    Union.setToolTipText("Add to Selection");
    Union.setSelected(true);
    Union.setText("Union");
    Union.addChangeListener(new JAtomSelectionDialog_Union_changeAdapter(this));
    Difference.setToolTipText("Subtract from Selection");
    Difference.setText("Difference");
    Difference.addChangeListener(new JAtomSelectionDialog_Difference_changeAdapter(this));
    Intersection.setToolTipText("Intersect with Selection");
    Intersection.setText("Intersection");
    Intersection.addChangeListener(new JAtomSelectionDialog_Intersection_changeAdapter(this));
    selRulePanel.setLayout(gridLayout2);
    gridLayout2.setColumns(1);
    gridLayout2.setRows(3);
    selRulePanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(
            Color.lightGray, 1), "Selection Rule"));
    selTypePanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(
            Color.lightGray, 1), "Selection Type"));
    P1.setLayout(gridLayout3);
    quickSelPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(
            Color.lightGray, 1), "Quick Selection"));
    P2.setLayout(borderLayout2);
    P3.setLayout(borderLayout3);
    highlightCheckBox.setEnabled(true);
    highlightCheckBox.setAlignmentX((float) 1.0);
    highlightCheckBox.setHorizontalAlignment(SwingConstants.RIGHT);
    highlightCheckBox.setHorizontalTextPosition(SwingConstants.RIGHT);
    highlightCheckBox.setMargin(new Insets(2, 15, 2, 2));
    highlightCheckBox.setSelected(true);
    highlightCheckBox.setText("Highlight selected atoms");
    highlightCheckBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        highlightCheckBox_itemStateChanged(e);
      }
    });
    jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel1.setHorizontalTextPosition(SwingConstants.LEFT);
    jLabel1.setText("Selected Atoms:");
    numSelectedAtomsTextField.setMaximumSize(new Dimension(60, 100));
    numSelectedAtomsTextField.setToolTipText("Shows number of selected atoms");
    numSelectedAtomsTextField.setEditable(false);
    numSelectedAtomsTextField.setColumns(5);
    Cancel.setToolTipText("Cancel Selection");
    Cancel.setText("Cancel");
    //Cancel.addActionListener(new JAtomSelectionDialog_Cancel_actionAdapter(this));
    Cancel.addActionListener(this);
    OK.setToolTipText("Confirm Selection");
    OK.setText("OK");
    OK.addActionListener(new JAtomSelectionDialog_OK_actionAdapter(this));
    Help.setEnabled(false);
    Help.setText("Help");
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent we) {
        //setLabel("Thwarted user attempt to close window.");
        finishDialog();
      }
    });
    infoPanel.setLayout(borderLayout1);
    jPanel2.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    quickSelPanel.add(All);
    quickSelPanel.add(Clear);
    quickSelPanel.add(Invert);
    quickSelPanel.add(Undo);
    P2.add(P1, BorderLayout.NORTH);

    selTypePanel.add(typeAtom);
    selTypePanel.add(typeMonomer);
    selTypePanel.add(typeMolecule);

    P1.add(selTypePanel);
    P1.add(selRulePanel);
    selRulePanel.add(Union);
    selRulePanel.add(Difference);
    selRulePanel.add(Intersection);
    buttonGroup1.add(typeAtom);
    buttonGroup1.add(typeMolecule);
    buttonGroup1.add(typeMonomer);
    buttonGroup2.add(Union);
    buttonGroup2.add(Difference);
    buttonGroup2.add(Intersection);
    P2.add(quickSelPanel, BorderLayout.SOUTH);
    P3.add(P2, BorderLayout.CENTER);
    P3.add(specialPanel, BorderLayout.EAST);
    specialPanel.add(atomTypes,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 0, 5), 0, 0));
    specialPanel.add(monomer,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 0, 5), 0, 0));
    specialPanel.add(Substructure,
            new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 0, 5), 0, 0));
    specialPanel.add(Sets,
            new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 0, 5), 0, 0));
    specialPanel.add(Sphere,
            new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 0, 5), 0, 0));
    jPanel2.add(jLabel1);
    jPanel2.add(numSelectedAtomsTextField);
    jPanel2.add(highlightCheckBox);
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    buttonPanel.add(OK);
    buttonPanel.add(Cancel);
    buttonPanel.add(Help);

    this.getContentPane().add(infoPanel, BorderLayout.CENTER);

    this.getContentPane().add(P3, BorderLayout.NORTH);
    infoPanel.add(jPanel2, BorderLayout.NORTH);
  }

  public void setTargetClass(Java3dUniverse parent) {
    daddy = parent;
  }

  public void initTaskProgressDialog() {
    if (taskProgessDialog == null) {
      Frame parentFrame = null;
      Object parentObj = null;
      while ((parentObj = this.getParent()) != null) {
        if (parentObj instanceof Frame) {
          parentFrame = (Frame) parentObj;
          break;
        }
      }
      taskProgessDialog = new TaskProgessDialog(parentFrame, false);
      taskProgessDialog.setLocationRelativeTo(this);
      taskProgessDialog.showHelpButton(false);
      taskProgessDialog.setAlwaysOnTop(true);
    } else {
      taskProgessDialog.setProgress(0); // Reset progress bar
    }
  }

  private void perfomTask(Java3dUniverse daddy, SELECTION_TYPE job) {
    perfomTask(daddy, job, null, null);
  }

  private void perfomTask(Java3dUniverse daddy, SELECTION_TYPE job, int[] intArray) {
    perfomTask(daddy, job, null, null, intArray, null);
  }

  private void perfomTask(Java3dUniverse daddy, SELECTION_TYPE job, boolean[] booleanArray) {
    perfomTask(daddy, job, null, null, null, booleanArray);
  }

  private void perfomTask(Java3dUniverse daddy, SELECTION_TYPE job, Object[] array_1, Object[] array_2) {
    perfomTask(daddy, job, array_1, array_2, null, null);
  }

  private void perfomTask(Java3dUniverse daddy, SELECTION_TYPE job, Object[] array_1, Object[] array_2, int[] intArray,
          boolean[] booleanArray) {
    initTaskProgressDialog();
    SelectAtomsTask task = new SelectAtomsTask(this, daddy, taskProgessDialog, job, array_1, array_2, intArray, booleanArray);

    task.execute();
  }

  public void enableControls(boolean enable) {
    Invert.setEnabled(enable);
    Clear.setEnabled(enable);
    All.setEnabled(enable);
    //Undo.setEnabled(enable);
    atomTypes.setEnabled(enable);
    monomer.setEnabled(enable);
    Sphere.setEnabled(enable);
    Substructure.setEnabled(enable);
    Sets.setEnabled(enable);
    typeMonomer.setEnabled(enable);
    typeMolecule.setEnabled(enable);
    typeAtom.setEnabled(enable);

    Union.setEnabled(enable);
    Difference.setEnabled(enable);
    Intersection.setEnabled(enable);

    highlightCheckBox.setEnabled(enable);

    Cancel.setEnabled(enable);
    OK.setEnabled(enable);
  }

  public void OK_actionPerformed(ActionEvent e) {
    setVisible(false);
    if (nextDialog != null) {
      daddy.enableMousePicking(false);
      nextDialog.setVisible(true);
    } else {
      perfomTask(daddy, SELECTION_TYPE.PROCESS_SELECTED_ATOMS);

      //taskProgessDialog.showTaskProgress(true);
      //showProgress.start();
      //daddy.processSelectedAtoms(showProgress);
      //taskProgessDialog.showTaskProgress(false);
      //showProgress.interrupt();
    }

    removeUserComponent();
  }

  public void removeUserComponent() {
    if (userComponent != null) {
      infoPanel.remove(userComponent);
    }
    validate();
    pack();
  }

  private void finishDialog() {
    finishDialog(taskProgessDialog);
  }

  private void finishDialog(TaskProgessDialog progress) {
    daddy.cancelSelection(progress);
    setVisible(false);
  }

  //public void Cancel_actionPerformed(ActionEvent e) {
  //  finishDialog();
  //  removeUserComponent();
  //}
  public void Clear_actionPerformed(ActionEvent e) {
    perfomTask(daddy, SELECTION_TYPE.CLEAR_SELECTION);
    //daddy.clearSelection();
  }

  public void All_actionPerformed(ActionEvent e) {

    perfomTask(daddy, SELECTION_TYPE.SELECT_ALL_ATOMS);

    //taskProgessDialog.showTaskProgress(true);
    //showProgress.start();
    //daddy.selectAll(showProgress);
    //taskProgessDialog.showTaskProgress(false);
    //showProgress.interrupt();
  }

  //public void Invert_actionPerformed(ActionEvent e) {
  //  perfomTask(daddy, SELECTION_TYPE.INVERT_SELECTION);
  //}
  public void Union_stateChanged(ChangeEvent e) {
    if (Union.isSelected()) {
      daddy.setSelectionRule(SELECTION_RULE.UNION);
      //Union.setSelected(true);
    }
    validate();
  }

  public void Difference_stateChanged(ChangeEvent e) {
    if (Difference.isSelected()) {
      daddy.setSelectionRule(SELECTION_RULE.DIFFERENCE);
      //Difference.setSelected(true);
    }

    validate();
  }

  public void Intersection_stateChanged(ChangeEvent e) {
    if (Intersection.isSelected()) {
      daddy.setSelectionRule(SELECTION_RULE.INTERSECTION);
      //Intersection.setSelected(true);
    }

    validate();
  }

  public void typeAtom_stateChanged(ChangeEvent e) {
    if (typeAtom.isSelected()) {
      daddy.setSelectionType(cct.modelling.SELECTION_TYPE.ATOMS);
    }
    validate();
  }

  public void typeMolecule_stateChanged(ChangeEvent e) {
    if (typeMolecule.isSelected()) {
      daddy.setSelectionType(cct.modelling.SELECTION_TYPE.MOLECULES);
    }
    validate();
  }

  public void typeMonomer_stateChanged(ChangeEvent e) {
    if (typeMonomer.isSelected()) {
      daddy.setSelectionType(cct.modelling.SELECTION_TYPE.MONOMERS);
    }
    validate();
  }

  public void atomTypes_actionPerformed(ActionEvent e) {
    if (jSelectAtomTypesDialog == null) {
      jSelectAtomTypesDialog = new JSelectAtomTypesDialog(parentFrame != null ? parentFrame : new Frame(), "Select Atom Types", true);
      jSelectAtomTypesDialog.setLocationRelativeTo(this);
    }

    jSelectAtomTypesDialog.setupElements(daddy.getElementsInMolecule());
    jSelectAtomTypesDialog.setupAtomNames(daddy.getAtomNamesInMolecule());
    jSelectAtomTypesDialog.setVisible(true);

    if (jSelectAtomTypesDialog.isOK()) {
      Object elements[] = jSelectAtomTypesDialog.getSelectedElements();
      Object atom_names[] = jSelectAtomTypesDialog.getSelectedAtomNames();
      perfomTask(daddy, SELECTION_TYPE.SELECT_ATOM_TYPES, elements, atom_names);
      //daddy.makeSelection(elements, atom_names);
    }
  }

  public void monomer_actionPerformed(ActionEvent e) {
    if (jSelectMonomersDialog == null) {
      String title = "Select Monomers";
      jSelectMonomersDialog = new JSelectMonomersDialog(parentFrame != null ? parentFrame : new Frame(), title, true);
      jSelectMonomersDialog.setLocationRelativeTo(this);
    }

    jSelectMonomersDialog.setupMonomers(daddy.getUniqueMonomersInMolecule());
    jSelectMonomersDialog.setVisible(true);

    if (jSelectMonomersDialog.isOK()) {
      Object monomers[] = jSelectMonomersDialog.getSelectedMonomers();
      perfomTask(daddy, SELECTION_TYPE.SELECT_MONOMER_TYPES, monomers, null);
      //daddy.makeMonomersSelection(monomers);
    }

  }

  public void Substructure_actionPerformed(ActionEvent e) {
    if (jSelectSubstructureDialog == null) {
      String title = "Select Monomers";
      jSelectSubstructureDialog = new JSelectSubstructureDialog(parentFrame != null ? parentFrame : new Frame(), title, true);
      jSelectSubstructureDialog.setLocationRelativeTo(this);
    }

    jSelectSubstructureDialog.setupSubstructure(daddy.getMolecularSubstructure());
    jSelectSubstructureDialog.setVisible(true);

    if (jSelectSubstructureDialog.isOK()) {
      int monomers[] = jSelectSubstructureDialog.getSelectedMonomers();
      perfomTask(daddy, SELECTION_TYPE.SELECT_SUBSTRUCTURE, monomers);
      //daddy.makeMonomersSelection(monomers);
    }
  }

  public void highlightCheckBox_itemStateChanged(ItemEvent e) {
    daddy.highlighSelectedAtoms(highlightCheckBox.isSelected());
  }

  private class JAtomSelectionDialog_Substructure_actionAdapter
          implements ActionListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_Substructure_actionAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.Substructure_actionPerformed(e);
    }
  }

  private class JAtomSelectionDialog_atomTypes_actionAdapter
          implements ActionListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_atomTypes_actionAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.atomTypes_actionPerformed(e);
    }
  }

  private class JAtomSelectionDialog_monomer_actionAdapter
          implements ActionListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_monomer_actionAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.monomer_actionPerformed(e);
    }
  }

  public void Sets_actionPerformed(ActionEvent e) {
    if (daddy == null) {
      return;
    }
    Sets.setEnabled(false);
    Object obj = daddy.getMoleculeInterface().getProperty(MoleculeInterface.AtomicSets);
    if (obj == null) {
      JOptionPane.showMessageDialog(this, "No atomic set(s) in molecule", "No sets", JOptionPane.WARNING_MESSAGE);
      Sets.setEnabled(true);
      return;
    } else if (!(obj instanceof AtomicSets)) {
      System.err.println(this.getClass().getCanonicalName() + ": Sets_actionPerformed: expected AtomicSets, got "
              + obj.getClass().getCanonicalName() + " Ignored...");
      Sets.setEnabled(true);
      return;
    }
    AtomicSets sets = (AtomicSets) obj;
    if (sets.size() < 1) {
      JOptionPane.showMessageDialog(this, "No atomic set(s) in molecule", "No sets", JOptionPane.WARNING_MESSAGE);
      Sets.setEnabled(true);
      return;
    }

    String[] names = new String[sets.size()];
    sets.keySet().toArray(names);
    Arrays.sort(names);

    SelectListDialog selectListDialog = new SelectListDialog(new Frame(), "Select available set(s)");
    selectListDialog.setList(names);
    selectListDialog.setLocationRelativeTo(this);
    if (!selectListDialog.showDialog()) {
      Sets.setEnabled(true);
      return;
    }

    Object[] selSets = selectListDialog.getSelectedValues();
    if (selSets == null || selSets.length < 1) {
      Sets.setEnabled(true);
      return;
    }

    boolean selectedAtoms[] = new boolean[daddy.getMoleculeInterface().getNumberOfAtoms()];
    for (int i = 0; i < selectedAtoms.length; i++) {
      selectedAtoms[i] = false;
    }

    for (int i = 0; i < selSets.length; i++) {
      AtomicSet aset = sets.get(selSets[i]);
      for (int j = 0; j < aset.size(); j++) {
        int n = daddy.getMoleculeInterface().getAtomIndex(aset.get(j));
        if (n == -1) {
          continue;
        }
        selectedAtoms[n] = true;
      }
    }

    perfomTask(daddy, SELECTION_TYPE.SELECT_SETS, selectedAtoms);
    //daddy.makeSelection(selectedAtoms);

    Sets.setEnabled(true);

  }

  class JAtomSelectionDialog_typeMonomer_changeAdapter
          implements ChangeListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_typeMonomer_changeAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      adaptee.typeMonomer_stateChanged(e);
    }
  }

  class JAtomSelectionDialog_typeMolecule_changeAdapter
          implements ChangeListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_typeMolecule_changeAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      adaptee.typeMolecule_stateChanged(e);
    }
  }

  class JAtomSelectionDialog_typeAtom_changeAdapter
          implements ChangeListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_typeAtom_changeAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      adaptee.typeAtom_stateChanged(e);
    }
  }

  class JAtomSelectionDialog_Union_changeAdapter
          implements ChangeListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_Union_changeAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      adaptee.Union_stateChanged(e);
    }
  }

  class JAtomSelectionDialog_Difference_changeAdapter
          implements ChangeListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_Difference_changeAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      adaptee.Difference_stateChanged(e);
    }
  }

  class JAtomSelectionDialog_Intersection_changeAdapter
          implements ChangeListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_Intersection_changeAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      adaptee.Intersection_stateChanged(e);
    }
  }

  class JAtomSelectionDialog_All_actionAdapter
          implements ActionListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_All_actionAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.All_actionPerformed(e);
    }
  }

  class JAtomSelectionDialog_OK_actionAdapter
          implements ActionListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_OK_actionAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.OK_actionPerformed(e);
    }
  }

  //class JAtomSelectionDialog_Cancel_actionAdapter
  //        implements ActionListener {
  //  private AtomSelectionDialog adaptee;
  //  JAtomSelectionDialog_Cancel_actionAdapter(AtomSelectionDialog adaptee) {
  //    this.adaptee = adaptee;
  //  }
  //  @Override
  //  public void actionPerformed(ActionEvent e) {
  //    adaptee.Cancel_actionPerformed(e);
  //  }
  //}
  class JAtomSelectionDialog_Clear_actionAdapter
          implements ActionListener {

    private AtomSelectionDialog adaptee;

    JAtomSelectionDialog_Clear_actionAdapter(AtomSelectionDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.Clear_actionPerformed(e);
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == Invert) {
      perfomTask(daddy, SELECTION_TYPE.INVERT_SELECTION);
      //************************************************************************

    } else if (e.getSource() == Cancel) {
      //finishDialog();
      //removeUserComponent();
      this.perfomTask(daddy, SELECTION_TYPE.CLEAR_SELECTION);
      //***** Sphere Selection
    } else if (e.getSource() == Sphere) {
      if (daddy == null) {
        JOptionPane.showMessageDialog(this, "daddy == null", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      String str = JOptionPane.showInputDialog(this, "Input Shere radius: ", "Input Shere radius", JOptionPane.QUESTION_MESSAGE);
      if (str == null || str.trim().length() < 1) {
        return;
      }

      double rad = 0;
      try {
        rad = Double.parseDouble(str.trim());
        if (rad <= 0) {
          JOptionPane.showMessageDialog(this, "Radius should be positive value, got " + rad, "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Wrong number " + str.trim() + " : " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      MoleculeInterface mol = daddy.getMoleculeInterface();
      //boolean selectedAtoms[] = new boolean[mol.getNumberOfAtoms()];
      int nsel = 0;
      boolean noSelectedAtoms = true;
      for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
        if (mol.getAtomInterface(i).isSelected()) {
          noSelectedAtoms = false;
          break;
        }
      }

      if (noSelectedAtoms) {
        JOptionPane.showMessageDialog(this, "Select some atom(s) first!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      boolean selectedAtoms[] = null;
      try {
        selectedAtoms = AtomSelection.selectAtomsWithinSphere(mol, rad, daddy.getSelectionType(), null);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error while selected atoms withon a sphere: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      //for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
      //  AtomInterface at = mol.getAtomInterface(i);
      //  if (!at.isSelected()) {
      //    continue;
      //  }
      //  for (int j = 0; j < mol.getNumberOfAtoms(); j++) {
      //    AtomInterface atom = mol.getAtomInterface(j);
      //    if (atom.isSelected()) {
      //      continue;
      //    }
      //    if (atom.distanceTo(at) <= rad) {
      //      selectedAtoms[j] = true;
      //    }
      //  }
      //}
      this.perfomTask(daddy, SELECTION_TYPE.SELECT_WITHIN_SPHERE, selectedAtoms);
      //daddy.makeSelection(selectedAtoms);
    }
  }

  class SelectAtomsTask extends SwingWorker<Void, Integer> {

    AtomSelectionDialog parentDialog;
    TaskProgessDialog progress;
    Java3dUniverse daddy;
    SELECTION_TYPE job;
    Object[] array_1, array_2;
    int[] intArray;
    boolean[] booleanArray;
    boolean done = false;

    SelectAtomsTask(AtomSelectionDialog parentDialog, Java3dUniverse daddy, TaskProgessDialog progress, SELECTION_TYPE job) {
      this(parentDialog, daddy, progress, job, null, null, null, null);
    }

    SelectAtomsTask(AtomSelectionDialog parentDialog, Java3dUniverse daddy, TaskProgessDialog progress, SELECTION_TYPE job,
            boolean[] booleanArray) {
      this(parentDialog, daddy, progress, job, null, null, null, booleanArray);
    }

    SelectAtomsTask(AtomSelectionDialog parentDialog, Java3dUniverse daddy, TaskProgessDialog progress, SELECTION_TYPE job,
            Object[] array_1, Object[] array_2, int[] intArray, boolean[] booleanArray) {
      //initialize
      this.parentDialog = parentDialog;
      this.daddy = daddy;
      this.progress = progress;
      this.job = job;
      this.array_1 = array_1;
      this.array_2 = array_2;
      this.intArray = intArray;
      this.booleanArray = booleanArray;
    }

    @Override
    public Void doInBackground() {
      parentDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      parentDialog.enableControls(false);
      progress.setCanceled(false);
      progress.setVisible(true);
      try {
        switch (job) {
          case PROCESS_SELECTED_ATOMS:
            daddy.processSelectedAtoms(progress);
            return null;
          case CANCEL_DIALOG:
            finishDialog();
            removeUserComponent();
            break;
          case SELECT_ALL_ATOMS:
            daddy.selectAll(progress);
            break;
          case CLEAR_SELECTION:
            daddy.clearSelection(progress);
            break;
          case INVERT_SELECTION:
            daddy.invertSelection(progress);
            break;
          case SELECT_ATOM_TYPES:
            daddy.makeSelection(array_1, array_2, progress);
            break;
          case SELECT_MONOMER_TYPES:
            daddy.makeMonomersSelection(array_1, progress);
            break;
          case SELECT_SUBSTRUCTURE:
            daddy.makeMonomersSelection(intArray, progress);
            break;
          case SELECT_SETS:
            daddy.makeSelection(booleanArray, progress);
            break;
          case SELECT_WITHIN_SPHERE:
            daddy.makeSelection(booleanArray, progress);
            break;
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      numSelectedAtomsTextField.setText(String.valueOf(daddy.getNumberSelectedAtoms()));
      return null;
    }

    @Override
    public void done() {
      //parentDialog.setEnabled(true);
      parentDialog.enableControls(true);
      parentDialog.setCursor(null);
      progress.setVisible(false);
      progress.setCanceled(false);
      done = true;
    }
  }
}
