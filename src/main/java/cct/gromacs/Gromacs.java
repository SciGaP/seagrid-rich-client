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

package cct.gromacs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import cct.modelling.ChemicalElements;

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
public class Gromacs {

  static Map GromacsElements = new HashMap();
  static Map GromacsAtomMap = null;
  final static String DefaultGromacsAtomMapFile = "GromacsAtomMap.txt";

  static {
    GromacsElements.put("O", "O"); // ; carbonyl oxygen (C=O)
    GromacsElements.put("OM", "O"); // ; carboxyl oxygen (CO-)
    GromacsElements.put("OA", "O"); // ; hydroxyl oxygen (OH)
    GromacsElements.put("OW", "O"); // ; water oxygen
    GromacsElements.put("N", "N"); // ; peptide nitrogen (N or NH)
    GromacsElements.put("NT", "N"); // ; terminal nitrogen (NH2)
    GromacsElements.put("NL", "N"); // ; terminal nitrogen (NH3)
    GromacsElements.put("NR5", "N"); // aromatic N (5-ring,2 bonds)
    GromacsElements.put("NR5*", "N"); // ; aromatic N (5-ring,3 bonds)
    GromacsElements.put("NP", "N"); // ; porphyrin nitrogen
    GromacsElements.put("C", "C"); // ; bare carbon (peptide,C=O,C-N)
    GromacsElements.put("CH1", "C"); // ; aliphatic CH-group
    GromacsElements.put("CH2", "C"); // ; aliphatic CH2-group
    GromacsElements.put("CH3", "C"); // ; aliphatic CH3-group
    GromacsElements.put("CR51", "C"); // ; aromatic CH-group (5-ring), united
    GromacsElements.put("CR61", "C"); // ; aromatic CH-group (6-ring), united
    GromacsElements.put("CB", "C"); // ; bare carbon (5-,6-ring)
    GromacsElements.put("H", "H"); // ; hydrogen bonded to nitrogen
    GromacsElements.put("HO", "H"); // ; hydroxyl hydrogen
    GromacsElements.put("HW", "H"); // ; water hydrogen
    GromacsElements.put("HS", "H"); // ; hydrogen bonded to sulfur
    GromacsElements.put("S", "S"); // ; sulfur
    GromacsElements.put("FE", "Fe"); // ; iron
    GromacsElements.put("ZN", "Zn"); // ; zinc
    GromacsElements.put("NZ", "N"); // ; arg NH (NH2)
    GromacsElements.put("NE", "N"); // arg NE (NH)
    GromacsElements.put("P", "P"); // ; phosphor
    GromacsElements.put("OS", "O"); // ; sugar or ester oxygen
    GromacsElements.put("CS1", "C"); // ; sugar CH-group
    GromacsElements.put("NR6", "N"); // ; aromatic N (6-ring,2 bonds)
    GromacsElements.put("NR6*", "N"); // ; aromatic N (6-ring,3 bonds)
    GromacsElements.put("CS2", "C"); // ; sugar CH2-group
    GromacsElements.put("SI", "Si"); // ; silicon
    GromacsElements.put("NA", "Na"); // ; sodium (1+)
    GromacsElements.put("CL", "Cl"); // ; chlorine (1-)
    GromacsElements.put("CA", "Ca"); // ; calcium (2+)
    GromacsElements.put("MG", "Mg"); // ; magnesium (2+)
    GromacsElements.put("F", "F"); // ; fluorine (cov. bound)
    GromacsElements.put("HCR", "H"); // ; H attached to aromatic C (5 or 6 ri
    GromacsElements.put("OWT3", "O"); // ;

    GromacsElements.put("LP2", "C");
    GromacsElements.put("LP3", "C");
    GromacsElements.put("LH1", "C");
    GromacsElements.put("LH2", "C");
    GromacsElements.put("LC", "C");
    GromacsElements.put("LC2", "C");
    GromacsElements.put("LC3", "C");
    GromacsElements.put("LO", "O");
    GromacsElements.put("LOS", "O");
    GromacsElements.put("LOM", "O");
    GromacsElements.put("LP", "P");
    GromacsElements.put("LNL", "N");

  }

  private Gromacs() {
  }

  public static int getChemicalElement(String residue, String atom_name) {
    int element = 0;
    String gromacsType = getGromacsAtomType(residue, atom_name);
    if (gromacsType != null) {
      gromacsType = gromacsType.toUpperCase();
      if (GromacsElements.containsKey(gromacsType)) {
        element = ChemicalElements.getAtomicNumber( (String)
            GromacsElements.get(gromacsType));
      }
      else {
        System.err.println("Gromacs atom type " + gromacsType +
                           " has no map for element");
        element = guessElement(atom_name);
      }
    }
    else {
      element = guessElement(atom_name);
    }
    return element;
  }

  public static int guessElement(String atom_name) {
    String name = atom_name.trim();
    if (name.length() == 0) {
      return 0;
    }
    if (name.length() == 1) {
      return ChemicalElements.getAtomicNumber(name);
    }
    if (name.length() == 2) {
      if (name.matches("[a-zA-Z]{2}")) {
        return ChemicalElements.getAtomicNumber(name);
      }
      else if (name.matches("[a-zA-Z]{1}.")) {
        return ChemicalElements.getAtomicNumber(name.substring(0, 1));
      }
      else {
        System.err.println("Cannot guess atomic element for atom named " + name + " Set to dummy one...");
        return 0;
      }
    }

    name = name.substring(0, 2);
    if (name.matches("[a-zA-Z]{2}")) {
      return ChemicalElements.getAtomicNumber(name);
    }
    else if (name.matches("[a-zA-Z]{1}.")) {
      return ChemicalElements.getAtomicNumber(name.substring(0, 1));
    }
    else {
      System.err.println("Cannot guess atomic element for atom named " + atom_name + " Set to dummy one...");
      return 0;
    }
  }

  public static String getGromacsAtomType(String residue_name,
                                          String atom_name) {
    String atom_type = null;
    if (GromacsAtomMap == null) {
      URL gromacsMap = null;
      InputStream is = null;
      try {
        ClassLoader cl = Gromacs.class.getClassLoader();
        //logger.info("Class loader: " + cl.toString());
        gromacsMap = cl.getResource("cct/gromacs/" +
                                    DefaultGromacsAtomMapFile);
        GromacsAtomMap = readGromacsAtomMap(gromacsMap.getFile());
      }
      catch (Exception ex) {
        System.err.println(" : " + ex.getMessage());
        return atom_type;
      }
    }

    if (GromacsAtomMap.containsKey(residue_name)) {
      Map residue = (Map) GromacsAtomMap.get(residue_name);
      if (residue.containsKey(atom_name)) {
        atom_type = (String) residue.get(atom_name);
      }
      else {
        System.err.println(" No atom " + atom_name + " in residue " +
                           residue_name + " in atom's map");
      }
    }
    else {
      System.err.println(" No residue " + residue_name + " in atom's map");
    }
    return atom_type;
  }

  /**
   * Reads gromacs atom map
   * @param filename String
   * @return Map contains Maps of residues (map.put(String residue_name, Map residue))
   * where each residue Map contains gromacs atom map residue.put(String atom_name, String gromacs_atom_type)
   * @throws Exception
   */
  public static Map readGromacsAtomMap(String filename) throws Exception {
    Map map = new HashMap();
    String line;
    BufferedReader in = null;
    int flag = -1;
    Map residue = null;
    String res_name = "";

    try {
      in = new BufferedReader(new FileReader(filename));

      // --- Reading residues and atom names
      // [ SOL ]
      // OW    OW   -0.820     0
      // HW1     H    0.410     0
      // HW2     H    0.410     0

      while ( (line = in.readLine()) != null) {
        line = line.trim();
        if (line.startsWith(";")) {
          continue; // Comment
        }
        if (line.length() == 0) {
          continue; // Empty line
        }

        if (line.startsWith("[")) { // Start to read residue

          if (flag == 0) { // End reading previous residue
            map.put(res_name, residue);
          }
          flag = 0;

          int startIndex = line.indexOf("[");
          int endIndex = line.indexOf("]");
          if (startIndex == -1 || endIndex == -1) {
            in.close();
            throw new Exception(
                "readGromacsAtomMap: ERROR while reading residue name: " +
                line);
          }
          res_name = line.substring(startIndex + 1, endIndex).trim();
          residue = new HashMap();
        }

        // --- Read atom
        else if (flag == 0) {
          StringTokenizer st = new StringTokenizer(line, " \t", false);
          if (st.countTokens() < 2) {
            in.close();
            throw new Exception(
                "readGromacsAtomMap: ERROR while reading atoms: " + line);
          }
          String aName = st.nextToken();
          String gromacsType = st.nextToken();
          residue.put(aName, gromacsType);
        }

        // --- Error
        else {
          in.close();
          throw new Exception(
              "readGromacsAtomMap: ERROR: Unexpected keyword");
        }

      }

      in.close();
    }
    catch (Exception ex) {
      if (in != null) {
        in.close();
      }
      throw new Exception("readGromacsAtomMap: ERROR: " + ex.getMessage());
    }

    return map;
  }

}
