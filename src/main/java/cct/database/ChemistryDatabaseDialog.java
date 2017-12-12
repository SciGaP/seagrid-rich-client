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

package cct.database;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cct.awtdialogs.MessageWindow;
import cct.awtdialogs.TextEntryDialog;
import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import cct.modelling.Molecule;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ChemistryDatabaseDialog
    extends Dialog implements ActionListener, ItemListener {
  java.awt.List Molecules = null, Structures = null;
  Java3dUniverse parent;
  TextField name, formula, aliases;
  SQLChemistryDatabase sql_data;
  int SelectedMolecule = 0;
  java.util.List allMolecules = null;
  java.util.List allStructures = null;
  String oldName;
  boolean error = false;

  static String moleculeNameKey = "name";
  static String structureMethodKey = "method";

  // Names for controls
  static String controlMolList = "mollist";
  static String controlStrList = "strlist";
  static String controlMolName = "molname";
  static String controlButtonOK = "buttonOK";
  static final Logger logger = Logger.getLogger(ChemistryDatabaseDialog.class.getCanonicalName());

  public ChemistryDatabaseDialog(Java3dUniverse p, SQLChemistryDatabase database,
                                 String Title, boolean modal) {
    super(new Frame(), Title, modal);
    parent = p;
    sql_data = database;

    GridBagLayout gridbag = new GridBagLayout();

    setLayout(gridbag);

    // ---

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.gridheight = 20;
    //c.weightx = 1;
    //c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(3, 3, 3, 3);

    Molecules = new java.awt.List(10, false);

    allMolecules = sql_data.getMolecules();
    for (int i = 0; i < allMolecules.size(); i++) {
      Map row = (Map) allMolecules.get(i);
      Molecules.add(row.get(moleculeNameKey).toString());
    }
    SelectedMolecule = 0;
    Map row = (Map) allMolecules.get(SelectedMolecule);
    String currentMolecule = row.get(moleculeNameKey).toString();

    Molecules.select(SelectedMolecule);
    Molecules.setName(controlMolList);
    Molecules.addActionListener(this);
    Molecules.addItemListener(this);

    gridbag.setConstraints(Molecules, c);
    add(Molecules);

    // ---

    GridBagConstraints structureC = new GridBagConstraints();
    structureC.gridx = 2;
    structureC.gridy = 0;
    structureC.gridwidth = 1; //end row
    structureC.gridheight = 15;
    structureC.fill = GridBagConstraints.BOTH;
    structureC.insets = new Insets(3, 3, 3, 3);

    allStructures = sql_data.getStructures();
    Structures = new java.awt.List(15, false);

    for (int i = 0; i < allStructures.size(); i++) {
      row = (Map) allStructures.get(i);
      String name = row.get(moleculeNameKey).toString();
      if (name.compareToIgnoreCase(currentMolecule) == 0) {
        Structures.add(row.get(structureMethodKey).toString());
      }
    }
    Structures.setName(controlStrList);

    Structures.addActionListener(this);
    Structures.addItemListener(this);

    gridbag.setConstraints(Structures, structureC);
    add(Structures);

    // ---

    GridBagConstraints addMolC = new GridBagConstraints();

    addMolC.gridwidth = 1; //end row
    addMolC.gridheight = 1; //reset to the default
    addMolC.gridx = 2;
    addMolC.gridy = 15;
    addMolC.fill = GridBagConstraints.BOTH;
    addMolC.insets = new Insets(3, 3, 3, 3);

    Button addMol = new Button("Add Molecule");
    gridbag.setConstraints(addMol, addMolC);
    add(addMol);
    addMol.addActionListener(this);

    // ---

    GridBagConstraints delMolC = new GridBagConstraints();

    delMolC.gridx = 2;
    delMolC.gridy = 16;
    delMolC.gridwidth = 1; //end row
    delMolC.gridheight = 1; //reset to the default
    delMolC.fill = GridBagConstraints.BOTH;

    Button delMol = new Button("Delete Molecule");
    gridbag.setConstraints(delMol, delMolC);
    add(delMol);
    delMol.addActionListener(this);

    // ---

    GridBagConstraints addStrC = new GridBagConstraints();

    addStrC.gridx = 2;
    addStrC.gridy = 17;
    addStrC.gridwidth = 1; //end row
    addStrC.gridheight = 1; //reset to the default

    Button addStr = new Button("Add Structure");
    gridbag.setConstraints(addStr, addStrC);
    add(addStr);
    addStr.addActionListener(this);

    // ----

    GridBagConstraints delStrC = new GridBagConstraints();

    delStrC.gridx = 2;
    delStrC.gridy = 18;
    delStrC.gridwidth = GridBagConstraints.REMAINDER; //end row
    delStrC.gridheight = 1; //reset to the default

    Button delStr = new Button("Delete Structure");
    gridbag.setConstraints(delStr, delStrC);
    add(delStr);
    delStr.addActionListener(this);

    // ---
    GridBagConstraints loadStrC = new GridBagConstraints();

    loadStrC.gridx = 2;
    loadStrC.gridy = 19;
    loadStrC.gridwidth = GridBagConstraints.REMAINDER; //end row
    loadStrC.gridheight = 1; //reset to the default

    Button loadStr = new Button("Load Structure");
    gridbag.setConstraints(loadStr, loadStrC);
    add(loadStr);
    loadStr.addActionListener(this);

    // ---

    GridBagConstraints hideC = new GridBagConstraints();

    hideC.gridx = 1;
    hideC.gridy = 21;
    hideC.gridwidth = 1;
    hideC.gridheight = 1;
    hideC.fill = GridBagConstraints.BOTH;

    Button OK = new Button("Hide");
    OK.addActionListener(this);
    gridbag.setConstraints(OK, hideC);
    add(OK);

    name = new TextField(20);

    /*
            //FlowLayout sizer = new  FlowLayout( FlowLayout.CENTER);
            GridLayout sizer = new GridLayout(0, 2, 5, 5);
            setLayout(sizer);

            Molecules = new java.awt.List(10, false);

            allMolecules = sql_data.getMolecules();
            for (int i = 0; i < allMolecules.size(); i++) {
                HashMap row = (HashMap) allMolecules.get(i);
                Molecules.add(row.get(moleculeNameKey).toString());
            }
            SelectedMolecule = 0;
            HashMap row = (HashMap) allMolecules.get(SelectedMolecule);
            String currentMolecule = row.get(moleculeNameKey).toString();

            Molecules.select(SelectedMolecule);
            Molecules.setName(controlMolList);
            add(Molecules);

            allStructures = sql_data.getStructures();
            Structures = new java.awt.List(10, false);

            for (int i = 0; i < allStructures.size(); i++) {
                row = (HashMap) allStructures.get(i);
                String name = row.get(moleculeNameKey).toString();
                if (name.compareToIgnoreCase(currentMolecule) == 0) {
                    Structures.add(row.get(structureMethodKey).toString());
                }
            }
            Structures.setName(controlStrList);
            add(Structures);

            add(new Label("Name: ", Label.LEFT));
            name = new TextField(20);
            name.setName(controlMolName);
            name.setText(currentMolecule);
            oldName = currentMolecule; // remember name
            add(name);
            name.addActionListener(this);

            add(new Label("Formula: ", Label.LEFT));
            formula = new TextField(20);

            add(new Label("Name Aliases: ", Label.LEFT));
            aliases = new TextField(20);

            Button addMol = new Button("Add Molecule");
            add(addMol);
            addMol.addActionListener(this);

            Button delMol = new Button("Delete Molecule");
            add(delMol);
            delMol.addActionListener(this);


            Button addStr = new Button("Add Structure");
            add(addStr);
            addStr.addActionListener(this);

            Button delStr = new Button("Delete Structure");
            add(delStr);
            delStr.addActionListener(this);


            Button loadStr = new Button("Load Structure");
            add(loadStr);
            loadStr.addActionListener(this);


            Button OK = new Button("OK");
            add(OK);
     */


    setSize(300, 450);

  }

  @Override
  public void itemStateChanged(ItemEvent ie) {
    String controlName = ie.getItemSelectable().toString();
    controlName = controlName.substring(controlName.indexOf('[') + 1);
    controlName = controlName.substring(0, controlName.indexOf(','));

    logger.info("Item: " + ie.getItem());
    logger.info("Item Selectable: " + ie.getItemSelectable());
    logger.info("ControlName: " + controlName);

    if (controlName.equals(controlMolList)) {
      SelectedMolecule = Molecules.getSelectedIndex();
      selectMolecule(SelectedMolecule);
    }
    else if (controlName.equals(controlStrList)) {

    }
  }

  /**
   *
   * @param ae ActionEvent
   */
  @Override
  public void actionPerformed(ActionEvent ae) {

    String controlName = ae.getSource().toString();
    controlName = controlName.substring(controlName.indexOf('[') + 1);
    controlName = controlName.substring(0, controlName.indexOf(','));
    logger.info("controlName: " + controlName);
    logger.info("ActionCommand: " + ae.getActionCommand());
    logger.info("paramString: " + ae.paramString());
    logger.info("getSource: " + ae.getSource());
    logger.info("getSource.getClass: " + ae.getSource().getClass());
    logger.info("getSource.hasgCode: " + ae.getSource().hashCode());

    // --- Molecule name is changed
    if (controlName.equals(controlMolName) &&
        isMoleculeNameUnique(name.getText())) {
      // ??? for now...
      if (sql_data.changeMoleculeName(oldName, name.getText())) {
        Map row = (Map) allMolecules.get(SelectedMolecule);
        row.put(moleculeNameKey, name.getText());
        String currentMolecule = row.get(moleculeNameKey).toString();
        Molecules.remove(SelectedMolecule);
        Molecules.add(name.getText(), SelectedMolecule);

        for (int i = 0; i < allStructures.size(); i++) {
          row = (Map) allStructures.get(i);
          row.put(moleculeNameKey, name.getText());
        }
        oldName = currentMolecule;

      }
      else {
        // ERROR
      }
    }

    // --- New molecule from the list is selected
    else if (controlName.equals(controlMolList)) {
      SelectedMolecule = Molecules.getSelectedIndex();
      selectMolecule(SelectedMolecule);
    }

    else if (ae.getActionCommand().toString().equals("Add Molecule")) {
      TextEntryDialog new_name = new TextEntryDialog(new Frame(),
          "Enter name for new molecule", true);
      new_name.setVisible(true);
      if (!new_name.isTextEntered()) {
        return;
      }
      String str = new_name.getValue();
      if (isMoleculeNameUnique(str) && sql_data.addNewMolecule(str)) {
        Map row = new HashMap();
        row.put(moleculeNameKey, str);
        allMolecules.add(row);
        SelectedMolecule = allMolecules.size() - 1;
        oldName = str;

        Molecules.add(str);
        Molecules.select(SelectedMolecule);

        Structures.removeAll();
      }
      else {
        MessageWindow er = new MessageWindow("ERROR",
                                             "Molecule name is not unique!", true);
        er.setVisible(true);
      }
      //dispose();

      // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      // --- Delete Molecule and corresponding structures...
    }
    else if (ae.getActionCommand().toString().equals("Delete Molecule")) {
      SelectedMolecule = Molecules.getSelectedIndex();
      if (Molecules.getSelectedIndex() == -1) {
        MessageWindow er = new MessageWindow("ERROR",
                                             "Select Molecule first!", true);
        er.setVisible(true);
        return;
      }

      Map row = (Map) allMolecules.get(SelectedMolecule);

      // --- First, delete all structures of a molecule
      for (int i = 0; i < allStructures.size(); i++) {
        row = (Map) allStructures.get(i);
        String mol_name = (String) row.get(moleculeNameKey);
        if (!mol_name.equals(Molecules.getSelectedItem())) {
          continue;
        }
        String method = (String) row.get(structureMethodKey);
        if (!sql_data.deleteStructure(Molecules.getSelectedItem(),
                                      method)) {
          MessageWindow er = new MessageWindow("ERROR",
                                               "Cannot delete structure " + method +
                                               ", so cannot delete molecule ", true);
          er.setVisible(true);
          return;
        }
      }

      for (int i = allStructures.size() - 1; i >= 0; i--) {
        row = (Map) allStructures.get(i);
        String mol_name = (String) row.get(moleculeNameKey);
        if (mol_name.equals(Molecules.getSelectedItem())) {
          allStructures.remove(i);
        }
      }

      // --- Structures deleted, now we can delete molecule

      if (!sql_data.deleteMolecule(Molecules.getSelectedItem())) {
        MessageWindow er = new MessageWindow("ERROR",
                                             "Cannot delete Molecule from database!", true);
        er.setVisible(true);
        return;
      }

      allMolecules.remove(SelectedMolecule);

      // --- now we need to select a new molecule from the list

      updateMoleculesList();

      if (SelectedMolecule >= allMolecules.size()) {
        --SelectedMolecule;
      }
      Structures.removeAll();

      if (SelectedMolecule < 0) {
        return;
      }

      Molecules.select(SelectedMolecule);
      oldName = Molecules.getSelectedItem();

      updateStructureList(Molecules.getSelectedItem());
      Structures.select(0);

      // -- Add new structure
    }
    else if (ae.getActionCommand().toString().equals("Add Structure")) {
      TextEntryDialog new_name = new TextEntryDialog(new Frame(),
          "Enter method/name for new structure", true);
      new_name.setVisible(true);
      if (!new_name.isTextEntered()) {
        return;
      }
      String str = new_name.getValue();
      logger.info("New Structure: " + str);

      if (!isStructureNameUnique(str, oldName)) {
        MessageWindow er = new MessageWindow("ERROR",
                                             "Structure name is not unique!", true);
        er.setVisible(true);
        return;
      }

      MoleculeInterface mol = parent.getMolecule();
      if (!sql_data.addNewStructure(oldName, str, mol)) {
        MessageWindow er = new MessageWindow("ERROR",
                                             "Unable to add new Structure to database!", true);
        er.setVisible(true);
        return;
      }
      Map row = new HashMap();
      row.put(moleculeNameKey, Molecules.getSelectedItem());
      row.put(structureMethodKey, str);
      allStructures.add(row);

      updateStructureList(Molecules.getSelectedItem());
      Structures.select(Structures.getItemCount() > 0 ?
                        Structures.getItemCount() - 1 : 0);

    }
    else if (ae.getActionCommand().toString().equals("Load Structure")) {
      if (Structures.getSelectedIndex() == -1) {
        MessageWindow er = new MessageWindow("ERROR",
                                             "Select Structure first!", true);
        er.setVisible(true);
        return;
      }
      Molecule mol =
          sql_data.loadStructure(Molecules.getSelectedItem(),
                                 Structures.getSelectedItem());
      if (mol.getNumberOfBonds() == 0) {
        mol.guessCovalentBonds();
      }
      parent.addMolecule(mol);

      // --- Delete Structure
    }
    else if (ae.getActionCommand().toString().equals("Delete Structure")) {
      if (Structures.getSelectedIndex() == -1) {
        MessageWindow er = new MessageWindow("ERROR",
                                             "Select Structure first!", true);
        er.setVisible(true);
        return;
      }

      if (!sql_data.deleteStructure(Molecules.getSelectedItem(),
                                    Structures.getSelectedItem())) {
        MessageWindow er = new MessageWindow("ERROR",
                                             "Cannot delete structure!", true);
        er.setVisible(true);
        return;
      }

      deleteStructureFromArray(Molecules.getSelectedItem(),
                               Structures.getSelectedItem());
      updateStructureList(Molecules.getSelectedItem());
      Structures.select(0);

      // --- OK Button
    }
    else if (ae.getActionCommand().toString().equals("Hide")) {
      //dispose();
      setVisible(false);
    }

    //logger.info("getClass: " + ae.getClass() );
    //logger.info("getClass.getName: " + ae.getClass().getName() );
    //logger.info("getClass.getSimpleName: " + ae.getClass().getSimpleName() );

    //dispose();
  }

  /**
   *
   * @param items List
   */
  public void populateItemsList(java.util.List items) {
    for (int i = 0; i < items.size(); i++) {
      Molecules.add( (String) items.get(i));
    }
    if (items.size() > 0) {
      Molecules.select(0);
    }
  }

  /**
   *
   */
  public boolean isMoleculeNameUnique(String new_name) {
    for (int i = 0; i < allMolecules.size(); i++) {
      Map row = (Map) allMolecules.get(i);
      if (new_name.compareToIgnoreCase(row.get(moleculeNameKey).toString()) ==
          0) {
        return false;
      }
    }
    return true;
  }

  public boolean isStructureNameUnique(String new_name, String mol_name) {
    for (int i = 0; i < allStructures.size(); i++) {
      Map row = (Map) allStructures.get(i);
      if (mol_name.compareToIgnoreCase(row.get(moleculeNameKey).toString()) !=
          0) {
        continue;
      }
      if (new_name.compareToIgnoreCase(row.get(structureMethodKey).
                                       toString()) == 0) {
        return false;
      }
    }
    return true;
  }

  void updateStructureList(String currentMolecule) {
    Structures.removeAll();
    for (int i = 0; i < allStructures.size(); i++) {
      Map row = (Map) allStructures.get(i);
      String name = row.get(moleculeNameKey).toString();
      if (name.compareToIgnoreCase(currentMolecule) == 0) {
        Structures.add(row.get(structureMethodKey).toString());
      }
    }
  }

  void updateMoleculesList() {
    Molecules.removeAll();
    for (int i = 0; i < allMolecules.size(); i++) {
      Map row = (Map) allMolecules.get(i);
      Molecules.add(row.get(moleculeNameKey).toString());
    }

  }

  boolean deleteStructureFromArray(String mol_name, String method) {
    for (int i = 0; i < allStructures.size(); i++) {
      Map row = (Map) allStructures.get(i);
      String name = row.get(moleculeNameKey).toString();
      String meth = row.get(structureMethodKey).toString();
      if (name.equals(mol_name) && meth.equals(method)) {
        allStructures.remove(i);
        return true;
      }
    }
    return false; // Was unable to delete. Who knows why... :-)
  }

  public void selectMolecule(int selected) {
    // !!! ERROR CHECK HERE... !!!

    Map row = (Map) allMolecules.get(selected);
    String currentMolecule = row.get(moleculeNameKey).toString();

    name.setText(currentMolecule);
    oldName = currentMolecule; // remember name

    updateStructureList(currentMolecule);
  }
  // --- Subclasses

}
