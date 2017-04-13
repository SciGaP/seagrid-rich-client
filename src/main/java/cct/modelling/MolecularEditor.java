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
package cct.modelling;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cct.amber.Sander8JobControl;
import cct.awtdialogs.ConnectSQLServer;
import cct.database.ChemistryDatabaseDialog;
import cct.database.SQLChemistryDatabase;
import cct.database.SQLDatabaseAccess;
import cct.database.new_ChemistryDatabaseDialog;
import cct.database.new_SQLChemistryDatabase;
import cct.dialogs.AtomSelectionDialog;
import cct.dialogs.JModifyTorsionDialog;
import cct.gaussian.Gaussian;
import cct.interfaces.HelperInterface;
import cct.interfaces.MoleculeInterface;

/**
 * <p>Title: Picking</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class MolecularEditor
        implements OperationsOnAtoms {

  private HelperInterface helper = null;
  protected MoleculeInterface molecule = null;
  public Gaussian gaussianData = null;
  public Sander8JobControl Sander_8_Controls = null;
  public SQLDatabaseAccess databaseAccess = null;
  public ConnectSQLServer sqlServerDialog = null;
  protected boolean connectedToDB = false;
  public SQLChemistryDatabase sqlChemistryDatabase = null;
  public ChemistryDatabaseDialog chemistryDatabase = null;
  protected AtomSelectionDialog jAtomSelectionDialog = null;
  public JModifyTorsionDialog jModifyTorsionDialog = null;
  public new_SQLChemistryDatabase new_sqlChemistryDatabase = null;
  public new_ChemistryDatabaseDialog new_chemistryDatabase = null;
  protected boolean HighlighSelectedAtoms = true;
  //protected int selectionType = SELECT_ATOMS;
  protected SELECTION_TYPE selectionType = SELECTION_TYPE.ATOMS;
  //protected int selectionRule = RULE_UNION;
  protected SELECTION_RULE selectionRule = SELECTION_RULE.UNION;
  protected int processingSelected = SELECTED_NOTHING;
  public int selectedMode = SELECTION_UNLIMITED;
  public boolean selectAndProcessDialog = false;
  static final Logger logger = Logger.getLogger(MolecularEditor.class.getCanonicalName());

  // --- Abstract Functions
  public abstract void cancelSelection();

  public abstract void changeAngleBetweenSelectedAtoms(float new_angle,
          int I_atom_status,
          int J_atom_status,
          int K_atom_status);

  public abstract void changeDihedralForSelectedAtoms(float new_angle,
          int I_atom_status,
          int L_atom_status);

  public abstract void clearSelection();

  public abstract void confirmChanges();

  public abstract void enableMousePicking(boolean enable);

  public abstract void endProcessingSelectedAtoms();

  public abstract Set<String> getAtomNamesInMolecule();

  public abstract Set<String> getElementsInMolecule();

  public abstract boolean getMousePickingStatus();

  public abstract void invertSelection();

  public abstract void makeMonomersSelection(int monomers[]);

  public abstract void makeMonomersSelection(Object monomers[]);

  public abstract void makeSelection(boolean selectedAtoms[]);

  public abstract void makeSelection(Object elements[], Object atom_names[]);

  public abstract void processSelectedAtoms();

  public abstract void resetGeometry();

  public abstract void selectAll();

  public abstract void undoLastSelection();

  public abstract void highlighSelectedAtoms(boolean enable);

  // --- Real function
  /**
   *
   * @return int
   */
  public int getJobType() {
    return this.processingSelected;
  }

  public void setHelper(HelperInterface h) {
    helper = h;
  }

  public void setJobType(int type) {
    processingSelected = type;
  }

  public SELECTION_TYPE getSelectionType() {
    return selectionType;
  }

  public List getMolecularSubstructure() {
    return molecule.getMolecularSubstructure();
  }

  public Set<String> getUniqueMonomersInMolecule() {
    //return molecule.getUniqueMonomersInMolecule();
    return Molecule.getUniqueMonomersInMolecule(molecule);
  }

  /*
   * @Deprecated public void openAtomSelectionDialog(Frame owner, int jobType) {
   *
   * // --- Common for all selections
   *
   * if (molecule == null) { JOptionPane.showMessageDialog(owner, "Load Molecule first!", "Warning", JOptionPane.WARNING_MESSAGE);
   * return; }
   *
   * if (getMousePickingStatus()) { JOptionPane.showMessageDialog(owner, "Another Selection is already in progress!", "Error",
   * JOptionPane.ERROR_MESSAGE); return; }
   *
   * if (jAtomSelectionDialog == null) { jAtomSelectionDialog = new AtomSelectionDialog(owner, "Atom Selection", false);
   * jAtomSelectionDialog.setTargetClass(this); jAtomSelectionDialog.setLocationRelativeTo(owner); if (helper != null) { JButton h =
   * jAtomSelectionDialog.getHelpButton(); h.setEnabled(true); helper.setHelpIDString(h, HelperInterface.JMD_SELECT_ATOMS_ID);
   * helper.displayHelpFromSourceActionListener(h); } } enableMousePicking(true); selectedMode = SELECTION_UNLIMITED;
   * processingSelected = jobType; selectAndProcessDialog = false; jAtomSelectionDialog.setVisible(true); }
   */
  public void setSelectionType(SELECTION_TYPE type) {
    //if (type != SELECT_ATOMS && type != SELECT_MONOMERS && type != SELECT_MOLECULES) {
    //  JOptionPane.showMessageDialog(new JFrame(), "INTERNAL ERROR: setSelectionType: wrong selection type!",
    //          "Error", JOptionPane.ERROR_MESSAGE);
    //  logger.info("INTERNAL ERROR: setSelectionType: wrong selection type!");
    //  return;
    //}

    selectionType = type;
  }

  public void setSelectionRule(SELECTION_RULE type) {
    //if (type != RULE_UNION && type != RULE_DIFFERENCE  && type != RULE_INTERSECTION) {
    //  JOptionPane.showMessageDialog(new JFrame(),
    //          "INTERNAL ERROR: setSelectionRule: wrong selection rule!",
    //          "Error", JOptionPane.ERROR_MESSAGE);
    //  logger.info("INTERNAL ERROR: setSelectionRule: wrong selection rule!");
    //  return;

    //}

    selectionRule = type;
  }
}
