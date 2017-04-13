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
package cct.amber;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;

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
public class AmberUtilities {

  public static final String AmberAtomFlags = "Amber Atom Flags";

  static String defaultAtomTypesTableFile = "cct.amber.amber-elements";

  static Map<String, Integer> defaultElementsTable = null;

  private static ResourceBundle resources;

  private AmberUtilities setDefault = new AmberUtilities();
  static final Logger logger = Logger.getLogger(AmberUtilities.class.getCanonicalName());

  public AmberUtilities() {
    defaultElementsTable = readAmberElementsTable(defaultAtomTypesTableFile);
  }

  public static int getElement(String amberType) {
    if (defaultElementsTable == null) {
      defaultElementsTable = readAmberElementsTable(
              defaultAtomTypesTableFile);
      if (defaultElementsTable == null) {
        return 0;
      }
    }

    if (amberType == null) {
      return 0;
    }

    if (defaultElementsTable.containsKey(amberType.toUpperCase())) {
      Integer el = defaultElementsTable.get(amberType);
      return el;
    }
    return 0;
  }

  public static Map<String, Integer> readAmberElementsTable(String propertiesName) {
    try {
      resources = ResourceBundle.getBundle(propertiesName);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      if (resources == null) {
        System.err.println("Resources " + propertiesName + " are not found");
        return null;
      }
    }

    Map<String, Integer> table = new HashMap<String, Integer>(100);
    Enumeration<String> en = resources.getKeys();
    int el;
    while (en.hasMoreElements()) {
      String key = en.nextElement();
      String element = resources.getString(key);
      if (element == null) {
        System.err.println("Warning: no element matching for Amber atom type " + key);
        el = 0;
      } else {
        el = ChemicalElements.getAtomicNumber(element.toUpperCase());
      }
      table.put(key.toUpperCase(), new Integer(el));
    }

    return table;
  }

  public static int getNAtomsFromCoordFile(String filename) {
    String line;
    int natoms = 0;
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      line = in.readLine(); // Skip first line
      line = in.readLine();
      if (line == null) {
        return 0;
      }

      StringTokenizer st = new StringTokenizer(line, " ");

      try {
        natoms = Integer.parseInt(st.nextToken());
      } catch (NumberFormatException e) {
        return 0;
      }

      in.close();
    } catch (IOException e) {
      logger.info("\nERROR: getNAtomsFromCoordFile: " + e.getMessage()
              + "\n");
      return 0;
    }

    return natoms;
  }

  public static void updateCoordFromInpcrd(MoleculeInterface molecule,
          String file_name) throws Exception {
    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      throw new Exception(
              "Error updating coordinates: Reference molecule has no atoms");
    }

    String line;
    int natoms = 0;
    try {
      BufferedReader in = new BufferedReader(new FileReader(file_name));
      line = in.readLine(); // Skip first line
      line = in.readLine();
      if (line == null) {
        throw new Exception(
                "Error: updateCoordFromInpcrd: unexpected eof while reading number of atoms");
      }

      StringTokenizer st = new StringTokenizer(line, " ");

      try {
        natoms = Integer.parseInt(st.nextToken());
      } catch (NumberFormatException e) {
        throw new Exception(
                "Error: updateCoordFromInpcrd: error parsing number of atoms: "
                + e.getMessage());
      }

      if (natoms != molecule.getNumberOfAtoms()) {
        throw new Exception("Number of atoms in reference molecule ("
                + molecule.getNumberOfAtoms()
                + ") does not match that in inpcrd file ("
                + natoms);
      }

      int repeat = 6;
      int dl = 12;
      int nlines = natoms * 3 % repeat > 0 ? 3 * natoms / repeat + 1
              : 3 * natoms / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      int count = 0;
      int nat = 0;
      int xyz = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine();
        int n = 3 * natoms - count >= repeat ? repeat : 3 * natoms - count;
        for (int j = 0; j < n; j++) {
          try {
            float coord = Float.parseFloat(line.substring(dl * j,
                    dl * j + dl).trim());

            if (xyz == 3) {
              xyz = 0;
              ++nat;
            }

            AtomInterface atom = molecule.getAtomInterface(nat);

            switch (xyz) {
              case 0:
                atom.setX(coord);
                break;
              case 1:
                atom.setY(coord);
                break;
              case 2:
                atom.setZ(coord);
                break;
            }

          } catch (Exception ex) {
            throw new Exception("Cannot parse " + line.substring(dl * j,
                    dl * j + dl).trim() + " : " + ex.getMessage());
          }

          ++xyz;
          ++count;
        }
      }

      in.close();
    } catch (IOException e) {
      throw new Exception("Error: updateCoordFromInpcrd: cannot read file "
              + file_name + " : " + e.getMessage());
    }

  }

  public static void main(String[] args) {
    String prmtop = "prmtop";
    String mdcrd = "mdcrd";
    String input = "input";
    AmberUtilities amberUtilities = new AmberUtilities();
    int count = 0;
    while (args.length < count) {
      if (args[count].equals("-p")) {
        if (args.length <= count + 1) {
          System.err.println("Expected prmtop file name");
          System.exit(1);
        }
        ++count;
        prmtop = args[count];
      }
      else if (args[count].equals("-x")) {
        if (args.length <= count + 1) {
          System.err.println("Expected mdcrd file name");
          System.exit(1);
        }
        ++count;
        mdcrd = args[count];
      }
      // --- Increment ++count;
      ++count;
    }
  }

}
