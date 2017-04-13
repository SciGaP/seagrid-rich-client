/* ***** BEGIN LICENSE BLOCK *****
   Version: Apache 2.0/GPL 3.0/LGPL 3.0

   CCT - Computational Chemistry Tools
   Jamberoo - Java Molecules Editor

   Copyright 2009 Dr. Vladislav Vasilyev

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
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.MonomerInterface;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */

enum LIB_ENTRY {
  LOOKING_FOR_ENTRY, READ_INDEX, READING_UNIT_ATOMS, READING_UNIT_CONNECTIVITY, READING_UNIT_RESIDUES, READING_UNIT_POSITIONS
}

public class AmberLib {

  private Set<String> residues = new HashSet<String> ();
  private static final Map<String, LIB_ENTRY> entries = new HashMap<String, LIB_ENTRY> ();
  private Map<String, MoleculeInterface> molecules = new LinkedHashMap<String, MoleculeInterface> ();
  MoleculeInterface refMolec;

  static {
    entries.put("!!index", LIB_ENTRY.READ_INDEX);
    entries.put("unit.atoms", LIB_ENTRY.READING_UNIT_ATOMS);
    entries.put("unit.connectivity", LIB_ENTRY.READING_UNIT_CONNECTIVITY);
    entries.put("unit.residues", LIB_ENTRY.READING_UNIT_RESIDUES);
    entries.put("unit.positions", LIB_ENTRY.READING_UNIT_POSITIONS);
  }

  static final Logger logger = Logger.getLogger(AmberLib.class.getCanonicalName());

  public AmberLib(MoleculeInterface ref) {
    refMolec = ref;
  }

  public int countMolecules() {
    return molecules.size();
  }

  public Map<String, MoleculeInterface> getMolecules() {
    Map<String, MoleculeInterface> mols = new LinkedHashMap<String, MoleculeInterface> (molecules);
    return mols;
  }

  public String[] getMoleculeNames() {
    if (countMolecules() < 1) {
      return null;
    }
    String[] mol_names = new String[molecules.size()];
    molecules.keySet().toArray(mol_names);
    return mol_names;
  }

  public MoleculeInterface getMolecule(String name) {
    return molecules.get(name);
  }

  public void parseLibFile(String filename, int fileType) throws Exception {
    String line, token, current_residue_name = "";
    StringTokenizer st;
    LIB_ENTRY progress = LIB_ENTRY.LOOKING_FOR_ENTRY;
    double[] xyz = new double[3];

    BufferedReader in = null;

    if (fileType == 0) { // Read from file
      try {
        in = new BufferedReader(new FileReader(filename));
      }
      catch (java.io.FileNotFoundException e) {
        throw new Exception("Error opening file " + filename + ": " + e.getMessage());
      }
    }
    else if (fileType == 1) { // Read from String
      in = new BufferedReader(new StringReader(filename));
    }
    else {
      throw new Exception("Implementation error: unknown file type");
    }

    while ( (line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() < 1) {
        continue;
      }

      if (line.startsWith("!!index")) {
        progress = LIB_ENTRY.READ_INDEX;
        continue;
      }
      else if (line.startsWith("!entry")) {
        token = line.substring(0, line.indexOf(" ")); // ERROR CHECK!
        st = new StringTokenizer(token, ".");
        if (st.countTokens() < 4) {
          System.err.println("Reading " + token + " entry: expecting 4 tokens, got " + line + " Ignored...");
          progress = LIB_ENTRY.LOOKING_FOR_ENTRY;
          continue;
        }

        st.nextToken(); // Skip !entry
        current_residue_name = st.nextToken();
        token = st.nextToken() + "." + st.nextToken();

        progress = entries.get(token);
        if (progress == null) {
          progress = LIB_ENTRY.LOOKING_FOR_ENTRY;
        }
      }

      switch (progress) {
        case LOOKING_FOR_ENTRY:
          continue;

        case READ_INDEX:
          st = new StringTokenizer(line, " \t\"");
          if (!st.hasMoreTokens()) {
            System.err.println("Error reading " + LIB_ENTRY.READ_INDEX.name() + " entry: expecting residue name, got " + line +
                               " Ignored...");
          }
          else {
            residues.add(st.nextToken());
          }
          continue;
      }

      MoleculeInterface mol = molecules.get(current_residue_name);
      if (mol == null) {
        mol = refMolec.getInstance();
        mol.setName(current_residue_name);
        molecules.put(current_residue_name, mol);
        logger.info("Created a molecule " + current_residue_name);
      }
      if (line.startsWith("!entry")) {
        continue;
      }

      switch (progress) {
        case READING_UNIT_ATOMS:
          st = new StringTokenizer(line, " \t\"");
          if (st.countTokens() < 8) {
            System.err.println("Reading " + LIB_ENTRY.READING_UNIT_ATOMS.name() + " entry: expecting 8 tokens, got " + line +
                               " Ignored...");
            continue;
          }
          AtomInterface atom = refMolec.getNewAtomInstance();
          atom.setName(st.nextToken());
          atom.setProperty(AtomInterface.NAME, atom.getName());
          atom.setProperty(AtomInterface.AMBER_NAME, st.nextToken());
          st.nextToken(); // skip int typex
          token = st.nextToken();
          int resx = 0;
          try {
            resx = Integer.parseInt(token);
          }
          catch (Exception ex) {
            System.err.println("Could not parse resx " + ex.getMessage() + ": Got " + line + " Ignored...");
          }
          for (int i = mol.getNumberOfMonomers(); i < resx; i++) {
            mol.addMonomer("UNK"); // Temporary name
          }

          --resx;
          atom.setSubstructureNumber(resx);

          token = st.nextToken();
          int flags = 0;
          try {
            flags = Integer.parseInt(token);
          }
          catch (Exception ex) {
            System.err.println("Could not parse flags " + ex.getMessage() + ": Got " + line + " Ignored...");
          }
          atom.setProperty(AmberUtilities.AmberAtomFlags, new Integer(flags));

          st.nextToken(); // skip int sequence

          token = st.nextToken();
          int elmnt = 0;
          try {
            elmnt = Integer.parseInt(token);
          }
          catch (Exception ex) {
            System.err.println("Could not parse elmnt " + ex.getMessage() + ": Got " + line + " Ignored...");
          }
          if (elmnt < 0) {
            elmnt = 0;
          }
          atom.setAtomicNumber(elmnt);

          token = st.nextToken();
          double chg = 0;
          try {
            chg = Double.parseDouble(token);
          }
          catch (Exception ex) {
            System.err.println("Could not parse chg " + ex.getMessage() + ": Got " + line + " Ignored...");
          }
          atom.setProperty(AtomInterface.ATOMIC_CHARGE, new Double(chg));
          mol.addAtom(atom);
          break;

        case READING_UNIT_CONNECTIVITY:
          st = new StringTokenizer(line, " \t");
          if (st.countTokens() < 2) {
            System.err.println("Reading " + LIB_ENTRY.READING_UNIT_CONNECTIVITY.name() +
                               " entry: expecting at least 2 tokens, got " + line +
                               " Ignored...");
            break;
          }
          token = st.nextToken();
          int i_1 = 0, i_2 = 0;
          try {
            i_1 = Integer.parseInt(token) - 1;
          }
          catch (Exception ex) {
            System.err.println("Could not parse i_1 for connectivity " + ex.getMessage() + ": Got " + line + " Ignored...");
            break;
          }
          token = st.nextToken();
          try {
            i_2 = Integer.parseInt(token) - 1;
          }
          catch (Exception ex) {
            System.err.println("Could not parse i_2 for connectivity " + ex.getMessage() + ": Got " + line + " Ignored...");
            break;
          }
          AtomInterface a1 = mol.getAtomInterface(i_1);
          AtomInterface a2 = mol.getAtomInterface(i_2);
          BondInterface bond = refMolec.getNewBondInstance(a1, a2);
          mol.addBond(bond);
          break;

        case READING_UNIT_RESIDUES:
          int nres = mol.getNumberOfMonomers();
          for (int i = 0; i < nres; i++) {
            if (i != 0) {
              line = in.readLine();
            }
            if (line == null) {
              System.err.println("Unexpected end of file while reading unit residues for " + current_residue_name + " Ignored...");
              break;
            }
            st = new StringTokenizer(line, " \t\"");
            if (st.countTokens() < 1) {
              System.err.println("Reading " + LIB_ENTRY.READING_UNIT_RESIDUES.name() + " entry: expecting at least 1 token, got " +
                                 line + " Ignored...");
              break;
            }
            String mname = st.nextToken();
            MonomerInterface monomer = mol.getMonomerInterface(i);
            monomer.setName(mname);
          }

          break;

        case READING_UNIT_POSITIONS:

          int nat = mol.getNumberOfAtoms();
          for (int i = 0; i < nat; i++) {
            if (i != 0) {
              line = in.readLine();
            }
            if (line == null) {
              System.err.println("Unexpected end of file while reading atom positions for " + current_residue_name + " Ignored...");
              break;
            }
            st = new StringTokenizer(line, " \t");
            if (st.countTokens() < 3) {
              System.err.println("Reading " + LIB_ENTRY.READING_UNIT_POSITIONS.name() + " entry: expecting at least 3 token, got " +
                                 line + " Ignored...");
              continue;
            }

            for (int j = 0; j < 3; j++) {
              token = st.nextToken();
              try {
                xyz[j] = Double.parseDouble(token);
              }
              catch (Exception ex) {
                System.err.println("Could not parse cartesian coordinate " + ex.getMessage() + ": Got " + line + " Ignored...");
                break;
              }
            }

            AtomInterface ac = mol.getAtomInterface(i);
            ac.setXYZ( (float) xyz[0], (float) xyz[1], (float) xyz[2]);
          }
          break;
      }

    }
  }

  //public static void main(String[] args) {
  //  AmberLib amberlib = new AmberLib();
  //}
}
