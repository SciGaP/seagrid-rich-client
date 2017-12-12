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

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import cct.awtdialogs.MessageWindow;
import cct.interfaces.MoleculeInterface;
import cct.modelling.MolecularProperties;
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
public class new_SQLChemistryDatabase
    implements TablesDescription, MolecularProperties {
  SQLDatabaseAccess database = null;

  boolean wasError = true;
  String errorMessage = "No error(s) yet";
  List moleculeTableInfo = null;
  List moleculeTable = null;
  List structureTableInfo = null;
  List structureTable = null;
  List methodsTableInfo = null;
  List methodsTable = null;
  List basisSetsTableInfo = null;
  List basisSetsTable = null;
  List propertiesTableInfo = null;
  List propertiesTable = null;
  static final Logger logger = Logger.getLogger(new_SQLChemistryDatabase.class.getCanonicalName());

  public new_SQLChemistryDatabase(SQLDatabaseAccess db, String host,
                                  String user, String pass, String datab) {
    wasError = true;
    database = db;
    if (!database.isDriverLoaded()) {
      return;
    }
    if (!database.getConnection(host, user, pass)) {
      return;
    }
    if (!database.selectDatabase(datab)) {
      return;
    }
    wasError = false;
  }

  public String getErrorMessage() {
    return database.getErrorMessage();
  }

  public List getMolecules() {
    return getTable(MoleculeTable, moleculeTableInfo);
  }

  public List getTable(String tableName, List metadataTable) {
    if (database == null) {
      return null;
    }
    Connection conn = database.getConnection();
    if (conn == null) {
      return null;
    }

    // --- Get info about table

    metadataTable = database.getTableMetaData(tableName);

    List table = database.getColumnsFromTable(tableName, "*");

    return table;
  }

  /**
   *
   * @return ArrayList
   */
  public List getStructures() {
    return getTable(StructureTable, structureTableInfo);
  }

  public List getStructuresForMolecule(String mol_name) {
    if (database == null) {
      return null;
    }
    if (database.getConnection() == null) {
      return null;
    }

    // --- Get info about table
    if (structureTableInfo == null) {
      structureTableInfo = database.getTableMetaData(StructureTable);
    }

    String condition = moleculeNameKey + "='" + mol_name + "'";

    List given_str = database.getSelectedColumnsFromTable(
        StructureTable, "*", condition);

    if (given_str == null && database.wasError()) {
      MessageWindow mess = new MessageWindow("SQL Error Message",
                                             getErrorMessage(), true);
      mess.setVisible(true);
    }

    return given_str;
  }

  public List getMethods() {
    return getTable(MethodsTable, methodsTableInfo);
  }

  /**
   *
   * @return String[]
   */
  public String[] getAvailableMethods() {
    List methods = getTable(MethodsTable, methodsTableInfo);
    if (methods == null || methods.size() < 1) {
      return null;
    }
    String allMethods[] = new String[methods.size()];
    for (int i = 0; i < methods.size(); i++) {
      Map row = (Map) methods.get(i);
      allMethods[i] = row.get(methodNameKey).toString();
    }

    return allMethods;
  }

  /**
   *
   * @return String[]
   */
  public String[] getAvailableBasisSets() {
    List methods = getTable(BasisSetsTable, basisSetsTableInfo);
    if (methods == null || methods.size() < 1) {
      return null;
    }
    String allMethods[] = new String[methods.size()];
    for (int i = 0; i < methods.size(); i++) {
      Map row = (Map) methods.get(i);
      allMethods[i] = row.get(basisSetsNameKey).toString();
    }

    return allMethods;
  }

  public List getBasisSets() {
    return getTable(BasisSetsTable, basisSetsTableInfo);
  }

  public boolean changeMoleculeName(String oldName, String newName) {
    if (!database.updateItem(StructureTable, moleculeNameKey, oldName,
                             newName)) {
      return false;
    }
    return database.updateItem(MoleculeTable, moleculeNameKey, oldName,
                               newName);
  }

  // Depreciated!!!
  public boolean addNewMolecule(String name) {
    return database.addStringItem(MoleculeTable, moleculeNameKey, name);
  }

  public boolean addNewMolecule(String name, String aliases, String notes,
                                boolean model, int charge, int mult) {
    String user = database.getUser();
    if (database.wasError()) {
      MessageWindow mess = new MessageWindow("SQL Error Message",
                                             getErrorMessage(), true);
      mess.setVisible(true);
      // For a moment we ignore the error
    }

    String columns = moleculeNameKey;
    String values = name;

    if (aliases != null && aliases.length() > 0) {
      columns += "," + moleculeAliasesKey;
      values += "," + aliases;
    }
    if (notes != null && notes.length() > 0) {
      columns += "," + moleculeNotesKey;
      values += "," + notes;
    }
    if (user != null && user.length() > 0) {
      columns += "," + moleculeUserKey;
      values += "," + user;
    }
    if (model) {
      columns += "," + moleculeIsModelKey;
      values += ",1";
    }
    else {
      columns += "," + moleculeIsModelKey;
      values += ",0";
      columns += "," + moleculeChargeKey;
      values += "," + String.valueOf(charge);
      columns += "," + moleculeMultiplicityKey;
      values += "," + String.valueOf(mult);
    }

    if (!database.addManyStringItems(MoleculeTable, columns, values)) {
      if (database.wasError()) {
        MessageWindow mess = new MessageWindow("SQL Error Message",
                                               getErrorMessage(), true);
        mess.setVisible(true);
      }
      return false;
    }

    return true; // OK
  }

  // Old subroutine
  /*
   public boolean addNewStructure(String name, String method, Molecule mol) {
      // --- First add new row to "structure" table
      // --- insert into structure (name, method) values('Acetone',�STO-3G�);
      if (!database.addManyStringItems(StructureTable,
   structureIdKey + "," + moleculeNameKey +
                                       "," + methodNameKey,
   name + "_" + method + " " + name + " " +
                                       method)) {
          return false;
      }
      // --- get OuputStream to write structure
      //Blob structure = database.getBlob(StructureTable, structureKey,
      //                                  structureIdKey,
      //                                  name + "_" + method );
      Clob structure = database.getClob(StructureTable, structureKey,
                                        structureIdKey,
                                        name + "_" + method);

      try {
          //OutputStream out = structure.setBinaryStream(1);
          OutputStream out = structure.setAsciiStream(1);
          mol.toArchiveFormat(out);
      } catch (SQLException ex) {
          logger.info("addNewStructure: SQLException: " +
                             ex.getMessage());
          return false;
      }

      //boolean result = database.setBlob( StructureTable, structureKey,
      //                                   structureIdKey,
      //                                   name + "_" + method, structure );
      boolean result = database.setClob(StructureTable, structureKey,
                                        structureIdKey,
                                        name + "_" + method, structure);

      if (!result) {
          database.deleteRows(StructureTable, structureIdKey,
                              name + "_" + method);
      }
      return result;
       }
   */
  // New subroutine
  public Map addNewStructure(String mol_name, String str_name,
                             String notes, String method, MoleculeInterface mol) {

    // First, query whether it is the first structure to be added or not

    List existedStr = getStructuresForMolecule(mol_name);
    if (database.wasError()) {
      return null;
    }

    // Get current user info
    String user = database.getUser();
    if (database.wasError()) {
      MessageWindow mess = new MessageWindow("SQL Error Message",
                                             getErrorMessage(), true);
      mess.setVisible(true);
      // For a moment we ignore the error
    }

    // Get Molecule Id

    List mol_id = database.getSelectedColumnsFromTable(MoleculeTable,
        moleculeIdKey + ", " +
        moleculeIsModelKey + ", " +
        moleculeChargeKey + ", " +
        moleculeMultiplicityKey,
        moleculeNameKey + "='" + mol_name + "'");
    if (database.wasError()) {
      MessageWindow mess = new MessageWindow("SQL Error Message",
                                             getErrorMessage(), true);
      mess.setVisible(true);
      return null;
    }
    else if (mol_id == null) {
      MessageWindow mess = new MessageWindow("SQL Error Message",
                                             "SQL Query returned nothing. Look at debug output", true);
      mess.setVisible(true);
      return null;
    }

    // Assuming we have only one row (as it should be!)
    Map row = (Map) mol_id.get(0);
    logger.info("Molec_Id: " + row.get(moleculeIdKey).toString());
    // --- Get Molecular ID
    int molec_id = Integer.parseInt(row.get(moleculeIdKey).toString());
    int charge = Integer.parseInt(row.get(moleculeChargeKey).toString());
    int multiplicity = Integer.parseInt(row.get(moleculeMultiplicityKey).
                                        toString());
    // --- Whether it's a model or regular molecule
    boolean not_a_model =
        Integer.parseInt(row.get(moleculeIsModelKey).toString()) == 0;
    // --- Check charge and multiplicity
    if (not_a_model) {
      Map prop = mol.getProperties();
      // --- Check total charge
      if (prop.containsKey(MoleculeInterface.ChargeProperty)) {
        Integer ch = (Integer) prop.get(MoleculeInterface.ChargeProperty);
        if (charge != ch.intValue()) {
          MessageWindow mess = new MessageWindow("Error Message",
                                                 "Molecule charge " + charge + " and structure's charge " +
                                                 ch.intValue() + " are different", true);
          mess.setVisible(true);
          logger.info("Molecule charge " + charge +
                             " and structure's charge " + ch.intValue() +
                             " are different");
          return null;
        }
      }
      // --- Check multiplicity
      if (prop.containsKey(MoleculeInterface.MultiplicityProperty)) {
        Integer mult = (Integer) prop.get(MoleculeInterface.MultiplicityProperty);
        if (multiplicity != mult.intValue()) {
          MessageWindow mess = new MessageWindow("Error Message",
                                                 "Molecule multiplicity " + multiplicity +
                                                 " and structure's one " +
                                                 mult.intValue() + " are different", true);
          mess.setVisible(true);
          logger.info("Molecule multiplicity " + charge +
                             " and structure's one " + mult.intValue() +
                             " are different");
          return null;
        }
      }
      // --- try to guess from molecule

      if (!Molecule.isMatchChargeAndMultiplicity(mol, charge, multiplicity)) {
        MessageWindow mess = new MessageWindow("Error Message",
                                               "Charge and multiplicity (" +
                                               charge + " and " +
                                               multiplicity +
                                               ") do not match current structure", true);
        mess.setVisible(true);
        logger.info("Charge and multiplicity (" + charge +
                           " and " +
                           multiplicity +
                           ") do not match current structure");
        return null;
      }
    }

    float weight = Molecule.getMolecularWeight(mol);
    String formula = Molecule.getChemicalFormula(mol);
    int count[] = Molecule.getElementCount(mol);

    // Special case for the first added structure. Fill in some fields in
    // Molecules table for a given molecule

    if (existedStr == null || existedStr.size() == 0) { // The first structure
      String values = moleculeFormulaKey + "='" + formula + "'";
      String condition = moleculeNameKey + "='" + mol_name + "'";

      values += "," + moleculeWeightKey + "='" + String.valueOf(weight) +
          "'";

      if (count[1] > 0) {
        values += ",H_atoms='" + String.valueOf(count[1]) + "'";
      }
      if (count[6] > 0) {
        values += ",C_atoms='" + String.valueOf(count[6]) + "'";
      }
      if (count[7] > 0) {
        values += ",N_atoms='" + String.valueOf(count[7]) + "'";
      }
      if (count[8] > 0) {
        values += ",O_atoms='" + String.valueOf(count[8]) + "'";
      }
      if (count[9] > 0) {
        values += ",F_atoms='" + String.valueOf(count[9]) + "'";
      }
      if (count[15] > 0) {
        values += ",P_atoms='" + String.valueOf(count[15]) + "'";
      }
      if (count[16] > 0) {
        values += ",S_atoms='" + String.valueOf(count[16]) + "'";
      }
      if (count[17] > 0) {
        values += ",Cl_atoms='" + String.valueOf(count[17]) + "'";
      }

      if (!database.updateItemsUsingCondition(MoleculeTable, values,
                                              condition)) {
        if (database.wasError()) {
          MessageWindow mess = new MessageWindow("SQL Error Message",
                                                 getErrorMessage(), true);
          mess.setVisible(true);
        }
        return null;
      }
    }
    // --- For the next added structures make a check
    else {
      List to_formula = database.getSelectedColumnsFromTable(
          MoleculeTable,
          moleculeFormulaKey, moleculeNameKey + "='" + mol_name + "'");
      if (database.wasError()) {
        return null;
      }
      row = (Map) to_formula.get(0);
      String original_formula = row.get(moleculeFormulaKey).toString();
      if (!original_formula.contentEquals(formula)) {
        MessageWindow mess = new MessageWindow("Error Message",
                                               "Molecule formula (" + original_formula +
                                               ") is different from structure's one " +
                                               formula, true);
        mess.setVisible(true);
        logger.info("Molecule formula (" + original_formula +
                           ") is different from structure's one " +
                           formula
            );
        return null;
      }
    }

    // --- Form internal representation of structure name

    Random rand = new Random();
    int random_number = rand.nextInt();
    String Structure_name = str_name + "@" + mol_name + "_" + method + "_" +
        String.valueOf(random_number);

    // --- First add new row to "structure" table
    // --- insert into Structure (StrName,Notes,User,MolName) values(...�);

    String str_col[] = {
        structureNameKey,
        structureNotesKey,
        structureUserKey,
        moleculeNameKey,
        moleculeIdKey
    };
    String str_val[] = {
        Structure_name,
        notes,
        user,
        mol_name,
        String.valueOf(molec_id)
    };

    if (!database.addManyStringItems(StructureTable, str_col, str_val)) {
      if (database.wasError()) {
        MessageWindow mess = new MessageWindow("SQL Error Message",
                                               getErrorMessage(), true);
        mess.setVisible(true);
      }
      return null;
    }

    // now we need to know Struct_Id

    int lastStrId = database.getLastInsertId(StructureTable);
    logger.info("Last inserted Struct_Id: " + lastStrId);

    if (lastStrId == -1) {
      logger.info(getErrorMessage());
    }

    // Get Structure Id

    List str_id = database.getSelectedColumnsFromTable(StructureTable,
        structureIdKey, structureNameKey + "='" + Structure_name + "'");
    if (database.wasError()) {
      MessageWindow mess = new MessageWindow("SQL Error Message",
                                             getErrorMessage(), true);
      mess.setVisible(true);
      return null;
    }
    else if (str_id == null) {
      MessageWindow mess = new MessageWindow("SQL Error Message",
                                             "SQL Query returned nothing. Look at debug output", true);
      mess.setVisible(true);
      return null;
    }

    // Assuming we have only one row (as it should be!)
    Map str_row = (Map) str_id.get(0);

    logger.info("Struct_Id: " + str_row.get(structureIdKey).toString());

    int Struct_Id = Integer.parseInt(str_row.get(structureIdKey).toString());

    // --- get OuputStream to write structure
    //Blob structure = database.getBlob(StructureTable, structureKey,
    //                                  structureNameKey,
    //                                  name + "_" + method );
    Clob structure = database.getClob(StructureTable, structureKey,
                                      structureNameKey,
                                      Structure_name);
    if (structure == null) {
      logger.info("Add Structure: structure == null");
      if (database.wasError()) {
        MessageWindow mess = new MessageWindow("SQL Error Message",
                                               getErrorMessage(), true);
        mess.setVisible(true);
      }

      return null;
    }

    try {
      //OutputStream out = structure.setBinaryStream(1);
      OutputStream out = structure.setAsciiStream(1);
      Molecule.toArchiveFormat(mol, out);
    }
    catch (SQLException ex) {
      logger.info("addNewStructure: SQLException: " +
                         ex.getMessage());
      return null;
    }

    //boolean result = database.setBlob( StructureTable, structureKey,
    //                                   structureIdKey,
    //                                   name + "_" + method, structure );
    boolean result = database.setClob(StructureTable, structureKey,
                                      structureNameKey,
                                      Structure_name, structure);

    if (!result) {
      database.deleteRows(StructureTable, structureNameKey,
                          Structure_name);
    }

    // --- Now we need to save molecular properties

    // --- First, get property types

    List prop_types = database.getColumnsFromTable(PropertiesTypeTable,
        PropertyTypeNameKey + "," +
        PropertyTypeIsTextKey + "," +
        PropertyTypeIsVectorKey + "," +
        PropertyTypeIsFloatKey);

    if (database.wasError()) {
      return null;
    }

    String columns[] = {
        propertiesNameKey,
        propertiesMethodKey,
        propertiesValueKey,
        propertiesVectorKey,
        propertiesNotesKey,
        propertiesStructId
    };
    String values[] = new String[6];

    String Value = new String(), Values = new String(), Notes = new String();
    Map prop = mol.getProperties();
    Set set = prop.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      List data;
      Map.Entry me = (Map.Entry) iter.next();
      String name = me.getKey().toString();
      Object something = me.getValue();
      String class_type = something.getClass().getSimpleName();

      // --- Method exists??? --- Assume that exists

      // --- Property type exists???

      boolean isText = true;
      boolean isFloat = false;
      boolean isVector = false;

      row = getSelectedRowFromQuery(prop_types, PropertyTypeNameKey, name);

      if (row != null) {
        isText = Integer.parseInt( row.get( PropertyTypeIsTextKey ).toString() ) ==
                1;
        isFloat = Integer.parseInt( row.get( PropertyTypeIsFloatKey ).toString() ) ==
                1;
        isVector = Integer.parseInt( row.get( PropertyTypeIsVectorKey ).
                toString() ) == 1;
      }
      else {
        MessageWindow mess = new MessageWindow("ERROR",
                                               "No such property - " + name + " - in database", true);
        mess.setVisible(true);
        continue;
      }
      /*
       data = database.getSelectedColumnsFromTable(PropertiesTypeTable,
          PropertyTypeNameKey,
          PropertyTypeNameKey + "='" +
          name + "'");
                if (database.wasError()) {
         if (menuFrame != null) {
            MessageWindow mess = new MessageWindow(menuFrame,
                "SQL Error Message",
                "Checking PropertyType: " +
                getErrorMessage(), true);
            mess.setVisible(true);
         }
         return null;
                }
                if (data == null) { // Property Type is not in database yet
         // Add property type with some "generic" parameters
         if (!database.addManyStringItems(PropertiesTypeTable,

                                          PropertyTypeNameKey + "," +
                                          PropertyTypeIsTextKey + "," +
                                          PropertyTypeIsVectorKey + ","
                                          + PropertyTypeIsFloatKey + "," +
                                          PropertyTypeNotesKey,

                                          name + "," + "1,0" + "," +
                                          "0" + "," +
       "Automatically added - edit manually")) {
            if (database.wasError() && menuFrame != null) {
               MessageWindow mess = new MessageWindow(menuFrame,
                   "SQL Error Message",
                   "Adding new property type: " +
                   getErrorMessage(), true);
               mess.setVisible(true);
            }
            return null;
         }
                }
       */

      // --- Now, add property
      /*
                if (class_type.equals("Float")) {
         Value = something.toString();
         Values = "";
                }
                else if (class_type.equals("Integer")) {
         Value = something.toString();
         Values = "";
                }
       else if (class_type.equals("String")) { // Vector values or Text
         Values = something.toString();
         if (Values.indexOf(' ') != -1) {
            Value = Values.substring(0, Values.indexOf(' '));
            try {
               float temp = Float.parseFloat(Value);
            }
            catch (NumberFormatException e) {
               Value = "";
            }
         }
         else {
            Value = "";
         }
                }
       */

      if (isText) {
        Value = "";
        Values = something.toString();
      }
      else if (isFloat) {
        Value = something.toString();
        Values = Value;
      }
      else if (isVector) { // Vector values or Text
        Values = something.toString().trim();
        String temp[] = Values.split(" "); // Number of element in vector
        Value = String.valueOf(temp.length);
      }

      values[0] = name;
      values[1] = method;
      values[2] = Value;
      values[3] = Values;
      values[4] = Notes;
      values[5] = String.valueOf(Struct_Id);

      if (!database.addManyStringItems(PropertiesTable, columns, values)) {

        if (database.wasError()) {
          MessageWindow mess = new MessageWindow("SQL Error Message",
                                                 "Adding new property type: " +
                                                 getErrorMessage(), true);
          mess.setVisible(true);
        }
        return null;
      }

    }

    // --- Fimanally get info about new added structure

    mol_id = database.getSelectedColumnsFromTable(StructureTable,
                                                  "*",
                                                  structureNameKey + "='" +
                                                  Structure_name +
                                                  "' AND " +
                                                  moleculeNameKey + "='" +
                                                  mol_name + "'");
    row = (Map) mol_id.get(0); // !!! ERROR CHECK !!!

    return row;
  }

  /**
   *
   * @param molecule_name String
   * @param struct_name String
   * @return MoleculeInterface
   */
  //public Molecule loadStructure(String molecule_name, String struct_id) {
  public MoleculeInterface loadStructure(String molecule_name, String struct_name) {
    // --- get InputStream to write structure
    //Blob structure = database.getBlob(StructureTable, structureKey,
    //                                  structureIdKey,
    //                                  name + "_" + method );
    //Clob structure = database.getClob(StructureTable, structureKey,
    //                                  structureIdKey,
    //                                  struct_id);
    Clob structure = database.getClob(StructureTable, structureKey,
                                      structureNameKey,
                                      struct_name);

    //byte arch[] = database.getBytes( StructureTable, structureKey,
    //                                  structureIdKey,
    //                                  name + "_" + method );

    try {
      String str = structure.getSubString(1L,
                                          structure.length() > 255 ? 255 :
                                          (int) structure.length());
      logger.info("loadStructure: substring: " + str);

      //InputStream in = structure.getBinaryStream( );
      InputStream in = structure.getAsciiStream();
      Molecule mol = new Molecule();
      mol.fromArchiveFormat(in);
      return mol;
    }
    catch (SQLException ex) {
      logger.info("addNewStructure: SQLException: " +
                         ex.getMessage());
      return null;
    }

    /*
                 try {
                 //char arch[] = structure.getSubString(1L, (int)structure.length()).toCharArray();
     byte arch[] = structure.getSubString(1L, (int)structure.length()).getBytes();
     logger.info( "addNewStructure: s " + structure.getSubString(1L, (int)structure.length()) );
     logger.info( "addNewStructure: b " + arch[0] + arch[1] + arch[2] );
                 Molecule mol = new Molecule();
                 mol.fromArchiveFormat( arch );
                 return mol;
             }  catch (SQLException ex) {
     logger.info( "addNewStructure: SQLException: " + ex.getMessage() );
                 return null;
             }
     */

  }

  public boolean deleteStructure(String mol_name, String struct_id) {
      return database.deleteRows( StructureTable, structureIdKey,
              struct_id ) != 0;
  }

  public boolean deleteMolecule(String name) {
      return database.deleteRows( MoleculeTable, moleculeNameKey, name ) != 0;
  }

  public boolean isError() {
    return wasError;
  }

  public boolean addNewMethod(String Name, String notes, boolean exp,
                              boolean fixedb, boolean dft, boolean effcore) {
    int i_exp = exp ? 1 : 0;
    int i_fixedb = fixedb ? 1 : 0;
    int i_dft = dft ? 1 : 0;
    int i_effcore = effcore ? 1 : 0;
    String columns = methodNameKey + "," +
        methodExpKey + "," +
        methodFixedBasisKey + "," +
        methodEffectiveCorePKey + "," +
        methodDFTPKey;
    String values = Name + "," + i_exp + "," +
        i_fixedb + "," + i_effcore + "," +
        i_dft;

    if (notes != null && notes.trim().length() > 0) {
      columns += "," + methodNotesKey;
      values += "," + notes;
    }

    if (!database.addManyStringItems(MethodsTable, columns, values)) {
      if (database.wasError()) {
        MessageWindow mess = new MessageWindow("SQL Error Message",
                                               "Adding new property type: " +
                                               getErrorMessage(), true);
        mess.setVisible(true);
      }
      return false;
    }
    return true;
  }

  /**
   *
   * @param query ArrayList
   * @param key String
   * @param value String
   * @return Map
   */
  Map getSelectedRowFromQuery(List query, String key, String value) {
    if (query == null || query.size() < 1) {
      return null;
    }
    for (int i = 0; i < query.size(); i++) {
      Map row = (Map) query.get(i);
      if (row.containsKey(key)) {
        String val = row.get(key).toString();
        if (value.equals(val)) {
          return row;
        }
      }
    }
    return null;
  }
}
