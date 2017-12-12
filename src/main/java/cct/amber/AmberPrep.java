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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import cct.Constants;
import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.vecmath.VecmathTools;
import cct.vecmath.Point3f;

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
public class AmberPrep {

  private int IDBGEN = 0; // Flag for data base generation
  //         = 0  No database generation.  Output will be individual files.
  //              This is the standard procedure if you want to create a
  //              single small molecule.
  //         = 1  A new data base will be generated or the existing database
  //              will be appended.
  private int IREST = 0; //Flag for the type of generation (assuming IDBGEN = 1)
  //         = 0  New data base
  //         = 1  Appending an existing data base
  private int ITYPF = 2; // Force field type code (used in LINK stage)
  /*
   * Ignored if IDBGEN = 0 The following codes are used in the standard database: = 1 United atom model = 2 All atom model = 100
   * United atom charged N-terminal amino acid residues = 101 United atom charged C-terminal amino acid residues = 200 All atom
   * charged N-terminal amino acid residues = 201 All atom charged C-terminal amino acid residues
   *
   * Note: This variable allows you to have several different models for the same residue name stored in one database. These models
   * could differ in topology, charge, or other factors. The charged terminal residues are selected internally by LINK if the IFTPRO
   * flag is set. The database can hold up to 510 residues.
   */
  private String NAMDBF = "No name"; // Name of the data base file (maximum 80 characters)
  private String TITLE = "No name"; // Descriptive header for the residue
  private String NAMF = ""; // Name of the output file if an individual residue file is
  //             being generated.  If database is being generated or
  //             appended this card IS read but ignored.
  private String NAMRES = "UNK"; //  A unique name for the residue of maximum 4 characters
  private String INTX; // Flag for the type of coordinates to be saved for the LINK module
  //      'INT'  internal coordinates will be output (preferable)
  //      'XYZ'  cartesian coordinates will be output
  private int KFORM = 0; // Format of output for individual residue files
  //        = 0  formatted output (recommended for debugging)
  //        = 1  binary output
  private String IFIXC = "CORRECT"; // Flag for the type of input geometry of the residue(s)
  //      'CORRECT' The geometry is input as internal coordinates with
  //                correct order according to the tree structure.
  //                NOTE: the tree structure types ('M', 'S', etc) and order
  //                must be defined correctly: NA(I), NB(I), and NC(I) on card
  //                8 are always ignored.
  //      'CHANGE'  It is input as cartesian coordinates or part cartesian
  //                and part internal.  Cartesians should precede internals
  //                to ensure that the resulting coordinates are correct.
  //                Coordinates need not be in correct order, since each
  //                is labeled with its atom number. NOTE: NA(I), NB(I), and
  //                NC(I) on card 8 must be omitted for cartesian coordinates
  //                with this option.
  private String IOMIT = "OMIT"; // Flag for the omission of dummy atoms
  //       'OMIT'    dummy atoms will be deleted after generating all the
  //                information (this is used for all but the first residue
  //                in the system)
  //      'NOMIT'   they will not be deleted (dummy atoms are retained for
  //                the first residue of the system.  others are omitted)
  private String ISYMDU = "DU"; // Symbol for the dummy atoms.  The symbol must be
  //             be unique.  It is preferable to use 'DU' for it
  private String IPOS = "BEG"; // Flag for the position of dummy atoms to be deleted
  //      'ALL'     all the dummy atoms will be deleted
  //      'BEG'     only the beginning dummy atoms will be deleted
  private double CUT = 0; // The cutoff distance for loop closing bonds which
  //             cannot be defined by the tree structure.  Any pair of
  //             atoms within this distance is assumed to be bonded.
  //             We recommend that CUT be set to 0.0 and explicit loop
  //             closing bonds be defined below.
  private List<AmberPrepAtom> atoms = new ArrayList<AmberPrepAtom>(10);
  private List<String[]> loops = new ArrayList<String[]>(5);
  private List<String[]> impropers = new ArrayList<String[]>(10);
  private List<Double> charges = new ArrayList<Double>(10);

  public AmberPrep() {
  }

  public void getMolecularInterface(MoleculeInterface molec) throws Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() + " : Internal Error: molec == null");
    }
    if (atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : didn't find atoms in file");
    }

    molec.addMonomer(NAMRES);

    int start = 0;
    boolean omit_all_dummies = false;
    if (IPOS.equals("BEG")) {
      start = 3;
    } else if (IPOS.equals("ALL")) {
      omit_all_dummies = true;
    }
    for (int i = start; i < atoms.size(); i++) {
      AmberPrepAtom at = atoms.get(i);
      if (omit_all_dummies && at.ISYMBL.equals("ISYMDU")) {
        continue;
      }
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(at.IGRAPH);
      atom.setAtomicNumber(AmberUtilities.getElement(at.ISYMBL));
      atom.setXYZ(at.getX(), at.getY(), at.getZ());
      atom.setProperty(AtomInterface.ATOMIC_CHARGE, new Double(at.CHRG));
      atom.setProperty(AtomInterface.AMBER_NAME, at.ISYMBL);
      atom.setProperty(AtomInterface.NAME, at.IGRAPH);

      molec.addAtom(atom);
    }
  }

  public static void main(String[] args) {
    AmberPrep amberprep = new AmberPrep();
  }

  public void parsePrepFile(String filename, int fileType) throws Exception {
    String line, token;
    StringTokenizer st;

    BufferedReader in = null;

    if (fileType == 0) { // Read from file
      try {
        in = new BufferedReader(new FileReader(filename));
      } catch (java.io.FileNotFoundException e) {
        throw new Exception("Error opening file " + filename + ": " + e.getMessage());
      }
    } else if (fileType == 1) { // Read from String
      in = new BufferedReader(new StringReader(filename));
    } else {
      throw new Exception("Implementation error: unknown file type");
    }

    // read IDBGEN , IREST , ITYPF in  FORMAT(3I)
    line = in.readLine();
    if (line == null) {
      throw new Exception("Unexpected end-of-file while reading the first line");
    }

    st = new StringTokenizer(line, " \t");

    if (st.hasMoreTokens()) {
      try {
        IDBGEN = Integer.parseInt(st.nextToken());
      } catch (Exception ex) {
        System.err.println("Cannot parse value for IDBGEN: " + ex.getMessage() + " Ignored...");
      }
      if (IDBGEN != 0 && IDBGEN != 1) {
        System.err.println("IDBGEN = " + IDBGEN + " Should be either 0 or 1. Ignored...");
      }
    }

    if (st.hasMoreTokens()) {
      try {
        IREST = Integer.parseInt(st.nextToken());
      } catch (Exception ex) {
        System.err.println("Cannot parse value for IREST: " + ex.getMessage() + " Ignored...");
      }
      if (IREST != 0 && IREST != 1) {
        System.err.println("IREST = " + IREST + " Should be either 0 or 1. Ignored...");
      }
    }

    if (st.hasMoreTokens()) {
      try {
        ITYPF = Integer.parseInt(st.nextToken());
      } catch (Exception ex) {
        System.err.println("Cannot parse value for ITYPF: " + ex.getMessage() + " Ignored...");
      }
      if (ITYPF != 1 && ITYPF != 2 && ITYPF != 100 && ITYPF != 101 && ITYPF != 200 && ITYPF != 201) {
        System.err.println("ITYPF = " + ITYPF + " Should be 1, 2, 100, 101, 200 or 201. Ignored...");
      }
    }

    // --- read in NAMDBF

    line = in.readLine();
    if (line == null) {
      throw new Exception("Unexpected end-of-file while reading the second line");
    }
    NAMDBF = line.substring(0, Math.min(line.length(), 80));

    // --- read in TITLE

    line = in.readLine();
    if (line == null) {
      throw new Exception("Unexpected end-of-file while reading the third line (title)");
    }
    TITLE = line.substring(0, Math.min(line.length(), 80));

    // --- read in NAMF -  FORMAT(A80

    line = in.readLine();
    if (line == null) {
      throw new Exception("Unexpected end-of-file while reading the forth line (NAMF)");
    }
    NAMF = line.substring(0, Math.min(line.length(), 80));

    // --- Reading NAMRES , INTX , KFORM - FORMAT(2A,I)

    line = in.readLine();
    if (line == null) {
      throw new Exception("Unexpected end-of-file while reading the fifth line");
    }
    st = new StringTokenizer(line, " \t");

    if (st.hasMoreTokens()) {
      token = st.nextToken();
      NAMRES = token.substring(0, Math.min(token.length(), 4));
    }

    if (st.hasMoreTokens()) {
      INTX = st.nextToken().toUpperCase();
      if ((!INTX.startsWith("INT")) && (!INTX.startsWith("XYZ"))) {
        throw new Exception("Unknown coordinate format: " + INTX + " Should be INT or XYZ");
      }
    }

    if (st.hasMoreTokens()) {
      try {
        KFORM = Integer.parseInt(st.nextToken());
      } catch (Exception ex) {
        System.err.println("Cannot parse value for KFORM: " + ex.getMessage() + " Ignored...");
      }
      if (KFORM != 0 && KFORM != 1) {
        System.err.println("KFORM = " + KFORM + " Should be 0 or 1. Ignored...");
      }
    }

    // --- Reading IFIXC , IOMIT , ISYMDU , IPOS - FORMAT(4A)

    line = in.readLine();
    if (line == null) {
      throw new Exception("Unexpected end-of-file while reading the sixth line");
    }
    st = new StringTokenizer(line, " \t");

    if (st.hasMoreTokens()) {
      IFIXC = st.nextToken().toUpperCase();
      if ((!IFIXC.startsWith("CORRECT")) && (!IFIXC.startsWith("CHANGE"))) {
        throw new Exception("Unknown Flag for the type of input geometry: " + IFIXC + " Should be CORRECT or CHANGE");
      }
    }

    if (st.hasMoreTokens()) {
      IOMIT = st.nextToken().toUpperCase();
      if ((!IOMIT.startsWith("OMIT")) && (!IOMIT.startsWith("NOMIT"))) {
        throw new Exception("Unknown Flag for the omission of dummy atoms: " + IOMIT + " Should be OMIT or NOMIT");
      }
    }

    if (st.hasMoreTokens()) {
      ISYMDU = st.nextToken().toUpperCase();
    }

    if (st.hasMoreTokens()) {
      IPOS = st.nextToken().toUpperCase();
      if ((!IPOS.startsWith("ALL")) && (!IPOS.startsWith("BEG"))) {
        throw new Exception("Unknown Flag for the position of dummy atoms to be deleted: " + IPOS + "Should be ALL or BEG");
      }
    }

    // --- Reading cutoff - FORMAT(F)

    line = in.readLine();
    if (line == null) {
      throw new Exception("Unexpected end-of-file while reading the seventh line");
    }
    st = new StringTokenizer(line, " \t");
    if (st.hasMoreTokens()) {
      try {
        CUT = Double.parseDouble(st.nextToken());
      } catch (Exception ex) {
        System.err.println("Cannot parse value for CUT: " + ex.getMessage() + " Ignored...");
      }
      if (CUT < 0) {
        System.err.println("CUT Should be >= 0. Ignored...");
      }
    }

    // --- Reading atoms - FORMAT(I,3A,3I,4F)

    while ((line = in.readLine()) != null) {
      st = new StringTokenizer(line, " \t");
      if (!st.hasMoreTokens()) {
        break; //  End of input
      }

      if (st.countTokens() < 11) {
        System.err.println("Expected at least 11 tokens while reading atoms. Got: " + line);
        break;
      }

      AmberPrepAtom atom = new AmberPrepAtom();
      token = st.nextToken();

      try {
        atom.I = Integer.parseInt(token);
      } catch (Exception ex) {
        System.err.println("Cannot parse value for The actual number of the atom in the tree: " + ex.getMessage() + ": " + token
                + " Ignored...");
      }

      // A unique atom name for the atom I

      token = st.nextToken();
      atom.IGRAPH = token.substring(0, Math.min(token.length(), 4)).trim();

      // --- A symbol for the atom I which defines its force field atom type

      token = st.nextToken();
      atom.ISYMBL = token.substring(0, Math.min(token.length(), 4));

      // --- The topological type (tree symbol) for atom I (M, S, B, E, or 3)

      token = st.nextToken();
      atom.ITREE = token;

      // --- The atom number to which atom I is connected.

      token = st.nextToken();
      try {
        atom.NA = Integer.parseInt(token) - 1;
      } catch (Exception ex) {
        System.err.println("Cannot parse value for The atom number to which atom I is connected.: " + ex.getMessage() + ": "
                + token);
        break;
      }

      // --- The atom number to which atom I makes an angle along with NA(I)

      token = st.nextToken();
      try {
        atom.NB = Integer.parseInt(token) - 1;
      } catch (Exception ex) {
        System.err.println("Cannot parse value for The atom number to which atom I makes an angle along with NA: " + ex.getMessage()
                + ": " + token);
        break;
      }

      // --- The atom number to which atom I makes a dihedral along with NA and NB

      token = st.nextToken();
      try {
        atom.NC = Integer.parseInt(token) - 1;
      } catch (Exception ex) {
        System.err.println("Cannot parse value for The atom number to which atom I makes a dihedral along with NA and NB: "
                + ex.getMessage() + ": " + token);
        break;
      }

      // --- Read R

      token = st.nextToken();
      try {
        atom.R = Double.parseDouble(token);
      } catch (Exception ex) {
        System.err.println("Cannot parse value for R: " + ex.getMessage() + ": " + token);
        break;
      }

      // --- Read THETA

      token = st.nextToken();
      try {
        atom.THETA = Double.parseDouble(token) * Constants.DEGREES_TO_RADIANS;
      } catch (Exception ex) {
        System.err.println("Cannot parse value for THETA: " + ex.getMessage() + ": " + token);
        break;
      }

      // --- Read PHI

      token = st.nextToken();
      try {
        atom.PHI = Double.parseDouble(token) * Constants.DEGREES_TO_RADIANS;
      } catch (Exception ex) {
        System.err.println("Cannot parse value for PHI: " + ex.getMessage() + ": " + token);
        break;
      }

      // --- Read charge

      token = st.nextToken();
      try {
        atom.CHRG = Double.parseDouble(token);
      } catch (Exception ex) {
        System.err.println("Cannot parse value for charge: " + ex.getMessage() + ": " + token + " Ignored...");
      }

      atoms.add(atom);
    }

    if (atoms.size() < 4) {
      throw new Exception("Should be at least 4 atoms in file. Got " + atoms.size());
    }

    if (line == null) {
      System.err.println("End-of-file is reached. File could be truncated. Ignored...");
    }

    // --- Reading information about improper torsions, loop and so on...

    while ((line = in.readLine()) != null) {
      if (line.trim().length() < 1) {
        continue;
      }
      token = line.toUpperCase();
      if (token.equals("DONE") || token.equals("STOP")) {
        break;
      }

      if (token.equals("LOOP")) {
        while ((line = in.readLine()) != null) {
          st = new StringTokenizer(line, " \t");
          if (st.countTokens() < 1) {
            break;
          }
          if (st.countTokens() < 2) {
            System.err.println("Expected at least two tokens while reading LOOP info. Got " + line);
          }
          String[] loopA = new String[2];
          loopA[0] = st.nextToken();
          loopA[1] = st.nextToken();
          loops.add(loopA);
        }
      } else if (token.equals("CHARGE")) {
      } else if (token.equals("IMPROPER")) {
        while ((line = in.readLine()) != null) {
          st = new StringTokenizer(line, " \t");
          if (st.countTokens() < 1) {
            break;
          }
          if (st.countTokens() < 4) {
            System.err.println("Expected at least 4 tokens while reading IMPROPER section. Got " + line);
          }
          String[] impr = new String[4];
          impr[0] = st.nextToken();
          impr[1] = st.nextToken();
          impr[2] = st.nextToken();
          impr[3] = st.nextToken();
          impropers.add(impr);
        }
      }
    }

    // --- resolving coordinates, etc.

    // --- Check uniqueness of atom names

    Set<String> anames = new HashSet<String>(atoms.size() - 3);
    for (int i = 3; i < atoms.size(); i++) {
      AmberPrepAtom atom = atoms.get(i);
      if (anames.contains(atom.IGRAPH)) {
        System.err.println("Atom name " + atom.IGRAPH + " is not unique");
      } else {
        anames.add(atom.IGRAPH);
      }
    }

    // --- Resolve cartesian coordinates

    AmberPrepAtom a1 = null, a2 = null, a3 = null;
    for (int i = 0; i < atoms.size(); i++) {
      AmberPrepAtom atom = atoms.get(i);
      switch (i) {
        case 0:
          a1 = a2 = a3 = null;
          break;
        case 1:
          a1 = atoms.get(atom.NA);
          break;
        case 2:
          a1 = atoms.get(atom.NA);
          a2 = atoms.get(atom.NB);
          break;
        default:
          a1 = atoms.get(atom.NA);
          a2 = atoms.get(atom.NB);
          a3 = atoms.get(atom.NC);
      }
      VecmathTools.fromInternalToCartesians(atom, atom.R, atom.THETA, atom.PHI, a1, a2, a3);
    }

  }
}

class AmberPrepAtom
        extends Point3f {

  int I; // The actual number of the atom in the tree.
  String IGRAPH; // A unique atom name for the atom I. If coordinates are
  //             read in at the EDIT stage, this name will be used for
  //             matching atoms.  Maximum 4 characters.
  String ISYMBL; // A symbol for the atom I which defines its force field
  //             atom type and is used in the module PARM for assigning
  //             the force field parameters.
  String ITREE; // The topological type (tree symbol) for atom I
  //             (M, S, B, E, or 3)
  int NA; // The atom number to which atom I is connected.
  //             Read but ignored for internal coordinates; If cartesian
  //             coordinates are used, this must be omitted.
  int NB; //  The atom number to which atom I makes an angle along
  //             with NA(I).
  //             Read but ignored for internal coordinates; If cartesian
  //             coordinates are used, this must be omitted.
  int NC; // The atom number to which atom I makes a dihedral along
  //             with NA(I) and NB(I).
  //             Read but ignored for internal coordinates; If cartesian
  //             coordinates are used, this must be omitted.
  double R; // If IFIXC .eq. 'CORRECT' then this is the bond length
  //             between atoms I and NA(I)
  //             If IFIXC .eq. 'CHANGE' then this is the X coordinate
  //             of atom I
  double THETA; // If IFIXC .eq. 'CORRECT' then it is the bond angle
  //             between atom NB(I), NA(I) and I
  //             If IFIXC .eq. 'CHANGE' then it is the Y coordinate of
  //             atom I
  double PHI; // If IFIXC .eq. 'CORRECT' then it is the dihedral angle
  //             between NC(I), NB(I), NA(I) and I
  //             If IFIXC .eq. 'CHANGE' then it is the Z coordinate of
  //             atom I
  double CHRG; // The partial atomic charge on atom I
}
