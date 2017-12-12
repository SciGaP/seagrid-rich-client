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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

import cct.interfaces.MoleculeInterface;
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
public class SQLChemistryDatabase {
  SQLDatabaseAccess database = null;
  boolean wasError = true;
  String errorMessage = "No error(s) yet";
  List moleculeTableInfo = null;
  List moleculeTable = null;
  List structureTableInfo = null;
  List structureTable = null;

  static String MoleculeTable = "molecule";
  static String StructureTable = "structure";

  static String moleculeNameKey = "name";
  static String structureIdKey = "str_id";
  static String methodNameKey = "method";
  static String structureKey = "structure";
  static final Logger logger = Logger.getLogger(SQLChemistryDatabase.class.getCanonicalName());

  public SQLChemistryDatabase(SQLDatabaseAccess db, String host, String user, String pass, String datab) {
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
    if (database == null) {
      return null;
    }
    Connection conn = database.getConnection();
    if (conn == null) {
      return null;
    }

    Statement stmt = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    String str;

    // --- Get info about table
    moleculeTableInfo = database.getTableMetaData(MoleculeTable);

    moleculeTable = database.getColumnsFromTable(MoleculeTable, "*");

    return moleculeTable;
  }

  /**
   *
   * @return ArrayList
   */
  public List getStructures() {
    if (database == null) {
      return null;
    }
    if (database.getConnection() == null) {
      return null;
    }

    // --- Get info about table
    structureTableInfo = database.getTableMetaData(StructureTable);

    structureTable = database.getColumnsFromTable(StructureTable, "*");

    return structureTable;
  }

  public boolean changeMoleculeName(String oldName, String newName) {
    if (!database.updateItem(StructureTable, moleculeNameKey, oldName, newName)) {
      return false;
    }
    return database.updateItem(MoleculeTable, moleculeNameKey, oldName, newName);
  }

  public boolean addNewMolecule(String name) {
    return database.addStringItem(MoleculeTable, moleculeNameKey, name);
  }

  public boolean addNewStructure(String name, String method, MoleculeInterface mol) {
    // --- First add new row to "structure" table
    // --- insert into structure (name, method) values('Acetone',�STO-3G�);
    if (!database.addManyStringItems(StructureTable,
                                     structureIdKey + "," + moleculeNameKey + "," + methodNameKey,
                                     name + "_" + method + "," + name + "," + method)) {
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
      Molecule.toArchiveFormat(mol, out);
    }
    catch (SQLException ex) {
      logger.info("addNewStructure: SQLException: " + ex.getMessage());
      return false;
    }

    //boolean result = database.setBlob( StructureTable, structureKey,
    //                                   structureIdKey,
    //                                   name + "_" + method, structure );
    boolean result = database.setClob(StructureTable, structureKey,
                                      structureIdKey,
                                      name + "_" + method, structure);

    if (!result) {
      database.deleteRows(StructureTable, structureIdKey, name + "_" + method);
    }
    return result;
  }

  public Molecule loadStructure(String name, String method) {

    // --- get InputStream to write structure
    //Blob structure = database.getBlob(StructureTable, structureKey,
    //                                  structureIdKey,
    //                                  name + "_" + method );
    Clob structure = database.getClob(StructureTable, structureKey,
                                      structureIdKey,
                                      name + "_" + method);

    //byte arch[] = database.getBytes( StructureTable, structureKey,
    //                                  structureIdKey,
    //                                  name + "_" + method );

    try {
      String str = structure.getSubString(1L, structure.length() > 255 ? 255 : (int) structure.length());
      logger.info("loadStructure: substring: " + str);

      //InputStream in = structure.getBinaryStream( );
      InputStream in = structure.getAsciiStream();
      Molecule mol = new Molecule();
      mol.fromArchiveFormat(in);
      return mol;
    }
    catch (SQLException ex) {
      logger.info("addNewStructure: SQLException: " + ex.getMessage());
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

  public boolean deleteStructure(String name, String method) {
      return database.deleteRows( StructureTable, structureIdKey, name + "_" + method ) != 0;
  }

  public boolean deleteMolecule(String name) {
      return database.deleteRows( MoleculeTable, moleculeNameKey, name ) != 0;
  }

  public boolean isError() {
    return wasError;
  }
}
