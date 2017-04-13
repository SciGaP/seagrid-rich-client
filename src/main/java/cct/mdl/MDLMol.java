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
package cct.mdl;

import java.io.BufferedReader;
import java.io.FileReader;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;
import cct.modelling.Molecule;
import cct.tools.IOUtils;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class MDLMol {

  public MDLMol() {
  }

  public MoleculeInterface parseFile(String filename, MoleculeInterface mol) throws Exception {
    String line;
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(filename));
    } catch (Exception ex) {
      throw new Exception("Error opening file " + filename + " : " + ex.getLocalizedMessage());
    }
    return parseFile(in, mol);
  }

  public MoleculeInterface parseFile(BufferedReader in, MoleculeInterface mol) throws Exception {
    return parseFile((Object) in, mol);

  }

  public MoleculeInterface parseFile(File file, MoleculeInterface mol) throws Exception {
    return parseFile(file.getAbsolutePath(), mol);
  }

  public MoleculeInterface parseFile(Object in, MoleculeInterface mol) throws Exception {
    String line;
    try {
      // --- Line 1
      // Molecule name. This line is unformatted, but like all other lines in a molfile
      // may not extend beyond column 80. If no name is available, a blank line must be present.
      if ((line = IOUtils.readLine(in)) == null) {
        //IOUtils.close(in);
        throw new Exception("parseMDLMolfile: ERROR: Unxpected End of file while reading Line 1");
      }

      mol.setName(line.trim());

      // -- Line 2 - Ignored for now !!!
      // This line has the format:
      //          IIPPPPPPPPMMDDYYHHmmddSSssssssssssEEEEEEEEEEEERRRRRR
      //(FORTRAN: A2<--A8--><---A10-->A2I2<--F10.5-><---F12.5--><-I6-> )
      // User's first and last initials (l), program name (P),
      // date/time (M/D/Y,H:m), dimensional codes (d), scaling factors (S, s),
      // energy (E) if modeling program input, internal registry number (R) if input through MDL form.
      // A blank line can be substituted for line 2.
      if ((line = IOUtils.readLine(in)) == null) {
        //IOUtils.close(in);
        throw new Exception("parseMDLMolfile: ERROR: Unxpected End of file while reading Line 2");
      }

      // --- Line 3 - Ignored
      // A line for comments. If no comment is entered, a blank line must be present.
      if ((line = IOUtils.readLine(in)) == null) {
        //IOUtils.close(in);
        throw new Exception("parseMDLMolfile: ERROR: Unxpected End of file while reading Line 2");
      }

      // -- Counts line
      // aaabbblllfffcccsssxxxrrrpppiiimmmvvvvvv
      // Where:
      // aaa = number of atoms (current max 255)* [Generic]
      // bbb = number of bonds (current max 255)* [Generic]
      // lll = number of atom lists (max 30)* [Query]
      // fff = (obsolete)
      // ccc = chiral flag: 0=not chiral, 1=chiral [Generic]
      // sss = number of stext entries [MDL ISIS/Desktop]
      // xxx = (obsolete)
      // rrr = (obsolete)
      // ppp = (obsolete)
      // iii = (obsolete)
      // mmm = number of lines of additional properties, including the M END line.
      // No longer supported, the default is set to 999. [Generic]
      // * These limits apply to MACCS-II, REACCS, and the MDL ISIS/Host Reaction Gateway,
      // but not to the MDL ISIS/Host Molecule Gateway or MDL ISIS/Desktop.
      if ((line = IOUtils.readLine(in)) == null) {
        //IOUtils.close(in);
        throw new Exception("parseMDLMolfile: ERROR: Unxpected End of file while reading Counts line");
      }

      int natoms = 0;
      int nbonds = 0;
      try {
        natoms = Integer.parseInt(line.substring(0, 3).trim());
      } catch (Exception e) {
        //IOUtils.close(in);
        throw new Exception("parseMDLMolfile: ERROR: Error while parsing number of atoms: "
            + e.getMessage());
      }

      try {
        nbonds = Integer.parseInt(line.substring(3, 6).trim());
      } catch (Exception e) {
        //IOUtils.close(in);
        throw new Exception("parseMDLMolfile: ERROR: Error while parsing number of bonds: "
            + e.getMessage());
      }

      // --- The atom block
      // The Atom Block is made up of atom lines, one line per atom with the following format:
      // xxxxx.xxxxyyyyy.yyyyzzzzz.zzzz aaaddcccssshhhbbbvvvHHHrrriiimmmnnneee
      // where the values are described in the following table:
      // x y z - atom coordinates [Generic]
      // aaa - atom symbol - entry in periodic table or L for atom list, A, Q,
      //        * for unspecified atom, and LP for lone pair, or R# for Rgroup label - [Generic, Query, 3D, Rgroup]
      // dd - mass difference - -3, -2, -1, 0, 1, 2, 3, 4 - (0 if value beyond these limits)
      //      [Generic] Difference from mass in periodic table. Wider range of values allowed by M ISO line, below.
      //      Retained for compatibility with older Ctabs, M ISO takes precedence.
      // ccc - charge - 0 = uncharged or value other than these, 1 = +3, 2 = +2, 3 = +1,
      //       4 = doublet radical, 5 = -1, 6 = -2, 7 = -3 [Generic] Wider range of values in M CHG and M RAD lines below.
      //       Retained for compatibility with older Ctabs, M CHG and M RAD lines take precedence.
      // sss - atom stereo parity - 0 = not stereo, 1 = odd, 2 = even, 3 = either or unmarked stereo center
      //       [Generic] Ignored when read.
      // hhh - hydrogen count + 1;  1 = H0, 2 = H1, 3 = H2, 4 = H3, 5 = H4
      //       [Query] H0 means no H atoms allowed unless explicitly drawn. Hn means atom must have n or more Hs in excess of explicit H�s.
      // bbb - stereo care box; 0 = ignore stereo configuration of this double bond atom, 1 = stereo configuration of double bond atom must match
      //       [Query] Double bond stereochemistry is considered during SSS only if both ends of the bond are marked with stereo care boxes.
      // vvv - valence; 0 = no marking (default) (1 to 14) = (1 to 14) 15 = zero valence
      //       [Generic] Shows number of bonds to this atom, including bonds to implied H�s.
      // HHH - H0 designator; 0 = not specified, 1 = no H atoms allowed
      // [MDL ISIS/Desktop] Redundant with hydrogen count information. May be unsupported in future releases of Elsevier MDL software.
      // rrr - Not used
      // iii - Not used
      // mmm - atom-atom mapping number; 1 - number of atoms; [Reaction]
      // nnn - inversion/retention flag; 0 = property not applied; 1 = configuration is inverted,
      //       2 = configuration is retained,; [Reaction]
      // eee - exact change flag; 0 = property not applied, 1 = change on atom must be exactly as shown; [Reaction, Query]
      // --- Reading atoms
      for (int i = 0; i < natoms; i++) {

        if ((line = IOUtils.readLine(in)) == null) {
          //IOUtils.close(in);
          throw new Exception("parseMDLMolfile: ERROR: Unxpected End of file while reading atom " + (i + 1));
        }

        AtomInterface atom = mol.getNewAtomInstance();

        // --- Getting x,y,z
        float xyz;
        try {
          xyz = Float.parseFloat(line.substring(0, 10).trim());
          atom.setX(xyz);
          xyz = Float.parseFloat(line.substring(10, 20).trim());
          atom.setY(xyz);
          xyz = Float.parseFloat(line.substring(20, 30).trim());
          atom.setZ(xyz);
        } catch (Exception ex) {
          //IOUtils.close(in);
          throw new Exception("parseMDLMolfile: ERROR: Error while parsing atom: "
              + (i + 1) + " Cannot parse atom's coordinate(s): " + line);
        }

        // --- Getting element
        //String token = line.substring(31, 34).trim();
        String token = line.substring(31, Math.min(line.length(), 34)).trim();
        int element = ChemicalElements.getAtomicNumber(token);
        atom.setAtomicNumber(element);
        atom.setName(token);

        mol.addAtom(atom);
      }

      // --- The Bond block
      // The Bond Block is made up of bond lines, one line per bond, with the following format:
      // 111222tttsssxxxrrrccc
      // where
      // 111 - first atom number; 1 - number of atoms; [Generic]
      // 222 - second atom number; 1 - number of atoms; [Generic]
      // ttt - bond type; 1 = Single, 2 = Double, 3 = Triple, 4 = Aromatic, 5 = Single or Double,
      //       6 = Single or Aromatic, 7 = Double or Aromatic, 8 = Any [Query] Values 4 through 8 are for SSS queries only.
      // sss - bond stereo; Single bonds: 0 = not stereo, 1 = Up, 4 = Either, 6 = Down, Double bonds: 0 = Use x-, y-, z-coords from atom block to determine cis or trans,
      //       3 = Cis or trans (either) double bond; [Generic] The wedge (pointed) end of the stereo bond is at the first atom (Field 111 above)
      // xxx - not used
      // rrr - bond topology; 0 = Either, 1 = Ring, 2 = Chain; [Query] SSS queries only.
      // ccc - reacting center status; 0 = unmarked, 1 = a center, -1 = not a center,
      //       Additional: 2 = no change, 4 = bond made/broken, 8 = bond order changes
      //       12 = 4+8 (both made/broken and changes); 5 = (4 + 1), 9 = (8 + 1), and 13 = (12 + 1) are also possible; [Reaction, Query]
      for (int i = 0; i < nbonds; i++) {

        if ((line = IOUtils.readLine(in)) == null) {
          //IOUtils.close(in);
          throw new Exception("parseMDLMolfile: ERROR: Unxpected End of file while reading bond " + (i + 1));
        }

        int a_i, a_j;
        try {
          a_i = Integer.parseInt(line.substring(0, 3).trim()) - 1;
          a_j = Integer.parseInt(line.substring(3, 6).trim()) - 1;
        } catch (Exception e) {
          //IOUtils.close(in);
          throw new Exception("parseMDLMolfile: ERROR: Error while parsing bond: "
              + (i + 1) + " : " + e.getMessage());
        }

        BondInterface bond = mol.getNewBondInstance(mol.getAtomInterface(
            a_i), mol.getAtomInterface(a_j));
        mol.addBond(bond);
      }

      // -- now continue to read in properties, if any
      Set<String> props = new LinkedHashSet<String>();
      String property = null;
      boolean propStart = false;
      while ((line = IOUtils.readLine(in)) != null && (!line.startsWith("$$$$"))) {
        line = line.trim();
        if (line.startsWith("> <")) {
          property = line.substring(line.indexOf("<") + 1);
          property = property.substring(0, property.indexOf(">"));
          propStart = true;
          props.clear();
          continue;
        }
        if (!propStart) {
          continue;
        }

        // -- Read in the property
        if (line.length() == 0) { // EMpty line - end of properties
          propStart = false;
          if (props.size() == 0) {

          } else if (props.size() == 1) {
            mol.addProperty("SDF_" + property, props.iterator().next());
          } else {
            mol.addProperty("SDF_" + property, props.toArray());
          }
          continue;
        }

        props.add(line);

      }

      //IOUtils.close(in);
    } catch (Exception ex) {
      if (in != null) {
        //IOUtils.close(in);
      }
      throw new Exception("parseMDLMolfile: ERROR: " + ex.getMessage());
    }

    return mol;
  }

  public static void main(String[] args) {
    FileDialog fd = new FileDialog(new Frame(), "Open SDF File", FileDialog.LOAD);
    fd.setFile("*.sdf");
    fd.setVisible(true);
    if (fd.getFile() == null) {
      System.exit(0);
    }
    String fileName = fd.getFile();
    String workingDirectory = fd.getDirectory();

    MDLMol sdf = new MDLMol();

    MoleculeInterface molec = new Molecule();
    try {
      sdf.parseFile(workingDirectory + fileName, molec);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.exit(0);
  }
}
