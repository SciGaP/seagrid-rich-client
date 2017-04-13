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
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.Molecule;
import cct.tools.Utils;
import static cct.tools.Utils.printMatrix;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.logging.Level;

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
public class AmberPrmtop {

  enum PRMTOP_FLAGS {

    POINTERS, ATOM_NAME, CHARGE, ATOMIC_NUMBER, MASS, ATOM_TYPE_INDEX, NUMBER_EXCLUDED_ATOMS, NONBONDED_PARM_INDEX, RESIDUE_LABEL,
    RESIDUE_POINTER, BOND_FORCE_CONSTANT, BOND_EQUIL_VALUE, ANGLE_FORCE_CONSTANT, ANGLE_EQUIL_VALUE, DIHEDRAL_FORCE_CONSTANT,
    DIHEDRAL_PERIODICITY, DIHEDRAL_PHASE, SCEE_SCALE_FACTOR, SCNB_SCALE_FACTOR, SOLTY, LENNARD_JONES_ACOEF, LENNARD_JONES_BCOEF,
    BONDS_INC_HYDROGEN, BONDS_WITHOUT_HYDROGEN, ANGLES_INC_HYDROGEN, ANGLES_WITHOUT_HYDROGEN, DIHEDRALS_INC_HYDROGEN,
    DIHEDRALS_WITHOUT_HYDROGEN, EXCLUDED_ATOMS_LIST, HBOND_ACOEF, HBOND_BCOEF, HBCUT, AMBER_ATOM_TYPE, TREE_CHAIN_CLASSIFICATION,
    JOIN_ARRAY, IROTAT, SOLVENT_POINTERS, ATOMS_PER_MOLECULE, BOX_DIMENSIONS, RADIUS_SET, RADII, SCREEN, IPOL, TITLE, UNKNOWN
  }
  public static float CHARGES_TO_KCAL_AMBER = 18.2223f;
  boolean Debug = true;
  private int repeat, dl;
  private int nat = 0, ntypes, nbonh, mbona, ntheth, mtheta, nphih, mphia,
      nhparm, nparm, nnb, nres, nbona, ntheta, nphia,
      numbnd, numang, nptra, natyp, nphb, ifpert, nbper, ngper, ndper,
      mbper, mgper, mdper, ifbox, nmxrs, ifcap, numextra;
  private double[][] Aij, Bij;
  private double[] eps, rho;
  static final Logger logger = Logger.getLogger(AmberPrmtop.class.getCanonicalName());

  public AmberPrmtop() {
  }

  public static void main(String[] args) {
    AmberPrmtop amberprmtop = new AmberPrmtop();
    MoleculeInterface mol = new Molecule();
    try {
      amberprmtop.parsePrmtop(mol, "/home/vvv900/Amber/Amber14_Benchmark_Suite/PME/FactorIX_production_NPT/prmtop");
      System.out.println("Numatoms: " + mol.getNumberOfAtoms());
    } catch (Exception ex) {
      Logger.getLogger(AmberPrmtop.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public int getNumberOfAtoms() {
    return nat;
  }

  public void exit() {
    System.gc();
    return;
  }

  public MoleculeInterface parsePrmtop(MoleculeInterface mol, String filename) throws
      Exception {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(filename));
    } catch (Exception e) {
      throw new Exception("Error opening Amber prmtop file " + filename + " : " + e.getMessage());
    }
    return parsePrmtop(mol, in);
  }

  public MoleculeInterface parsePrmtop(MoleculeInterface mol, BufferedReader in) throws
      Exception {

    int nlines, count, k;
    String line;
    AtomInterface[] atoms = null;
    int[] bonds = null;

    boolean new_format = false;

    if ((line = in.readLine()) == null) {
      throw new Exception("Error parsing Amber prmtop file: unexpected end of file while reading the first line");
    }

    // --- Determining file format
    if (Debug) {
      logger.info(line);
    }

    if (line.indexOf("%VERSION") != -1) {
      new_format = true;
      return parseNewPrmtop(mol, in);
    } else {
      new_format = false;
      //throw new Exception(
      //    "Old Amber prmtop file format is not implemented yet");
    }

    // --- Set molecule name
    if (Debug) {
      logger.info("Molecule name: " + line);
    }
    if (line.trim().length() > 0) {
      mol.setName(line.trim());
    }

    // --- Statrting to read
    try {

      //line = in.readLine();
      //if (new_format) {
      //  line = in.readLine(); // %FORMAT(20a4)
      //}
      //line = in.readLine();
      // --- Read options
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }

      int[] itemp = new int[31];

      nlines = 31 % repeat > 0 ? 31 / repeat + 1 : 31 / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      k = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = 31 - (count + 1) + 1 >= repeat ? repeat : 31 - (count + 1);
        for (int j = 0; j < n; j++, k++, count++) {
          //strncpy(buf, bufer + dl * j, dl);
          //buf[dl] = 0x00;
          //itemp[k] = atoi(buf);
          try {
            itemp[k] = Integer.parseInt(line.substring(dl * j, dl * j + dl).trim());
          } catch (Exception ex) {
            throw new Exception();
          }
        }
      }

      nat = itemp[0];
      ntypes = itemp[1];
      nbonh = itemp[2];
      mbona = itemp[3];
      ntheth = itemp[4];
      mtheta = itemp[5];
      nphih = itemp[6];
      mphia = itemp[7];
      nhparm = itemp[8];
      nparm = itemp[9];
      nnb = itemp[10];
      nres = itemp[11];

      nbona = itemp[12];
      ntheta = itemp[13];
      nphia = itemp[14];
      numbnd = itemp[15];
      numang = itemp[16];
      nptra = itemp[17];
      natyp = itemp[18];
      nphb = itemp[19];
      ifpert = itemp[20];
      nbper = itemp[21];
      ngper = itemp[22];
      ndper = itemp[23];

      mbper = itemp[24];
      mgper = itemp[25];
      mdper = itemp[26];
      ifbox = itemp[27];
      nmxrs = itemp[28];
      ifcap = itemp[29];
      numextra = itemp[30];

      if (logger.isLoggable(Level.INFO)) {
        logger.info(String.format(
            "\nnatom  = %7d ntypes = %7d nbonh = %7d mbona  = %7d"
            + "\nntheth = %7d mtheta = %7d nphih = %7d mphia  = %7d"
            + "\nnhparm = %7d nparm  = %7d nnb   = %7d nres   = %7d"
            + "\nnbona  = %7d ntheta = %7d nphia = %7d numbnd = %7d"
            + "\nnumang = %7d nptra  = %7d natyp = %7d nphb   = %7d"
            + "\nifbox  = %7d nmxrs  = %7d ifcap = %7d nextra = %7d",
            nat, ntypes, nbonh, mbona, ntheth, mtheta, nphih, mphia,
            nhparm, nparm, nnb, nres, nbona, ntheta, nphia,
            numbnd, numang, nptra, natyp, nphb, ifpert, nbper, ngper,
            ndper,
            mbper, mgper, mdper, ifbox, nmxrs, ifcap, numextra));
      }

      if (logger.isLoggable(Level.INFO)) {
        logger.log(Level.INFO, "There are " + nat + " atoms in PARM file");
      }

      atoms = new AtomInterface[nat];
      for (int i = 0; i < nat; i++) {
        atoms[i] = mol.getNewAtomInstance();
      }

      if (nbonh > 0 || nbona > 0) {
        bonds = new int[nbonh > nbona ? 3 * nbonh : 3 * nbona];
      }

      // Read Amber atom types (default 20a4)
      if (logger.isLoggable(Level.INFO)) {
        logger.log(Level.INFO, "Reading atoms types...");
      }

      repeat = 20;
      dl = 4;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }

      nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = nat - (count + 1) + 1 >= repeat ? repeat
            : nat - (count + 1) + 1;
        for (int j = 0; j < n; j++) {
          atoms[count].setName(line.substring(j * dl, j * dl + dl).
              trim());
          //strncpy(buf, bufer + j * dl, dl);
          //atom[count].Set_Atom_Name(token);
          ++count;
        }
      }

      if (logger.isLoggable(Level.INFO)) {
        logger.log(Level.INFO, "Finished reading atoms types...\nReading atomic charges...");
      }

      // --- Read charges (default 5e16.8)
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = nat - (count + 1) + 1 >= repeat ? repeat : nat - (count + 1) + 1;
        for (int j = 0; j < n; j++) {
          try {
            Float charge = Float.parseFloat(line.substring(dl * j, dl * j + dl).trim());
            charge /= CHARGES_TO_KCAL_AMBER;
            atoms[count].setProperty(AtomInterface.ATOMIC_CHARGE, charge);
          } catch (Exception ex) {
            throw new Exception();
          }

          //strncpy(buf, bufer + j * dl, dl);
          //buf[dl] = 0x00;
          //atom[count].charge = atof(buf) / CHARGES_TO_KCAL_AMBER;
          ++count;
        }
      }

      if (logger.isLoggable(Level.INFO)) {
        logger.log(Level.INFO, "Finished reading atomic charges...\nReading atomic masses...");
      }

      // -- Read atomic masses using format ( default 5e16.8)
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = nat - (count + 1) + 1 >= repeat ? repeat : nat - (count + 1) + 1;
        for (int j = 0; j < n; j++) {
          //strncpy(buf, bufer + j * dl, dl);
          try {
            float mass = Float.parseFloat(line.substring(dl * j, dl * j + dl).trim());
            atoms[count].setAtomicMass(mass);
          } catch (Exception ex) {
            throw new Exception();
          }
          ++count;
        }
      }

      if (logger.isLoggable(Level.INFO)) {
        logger.log(Level.INFO, "Finished reading atomic masses...\nReading atom type indices...");
      }

      // -- Read atom type index using format ( default 12i6)
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      Integer index;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = nat - count >= repeat ? repeat : nat - count;
        for (int j = 0; j < n; j++, count++) {
          try {
            index = Integer.parseInt(line.substring(dl * j, dl * j + dl).trim()) - 1;
            atoms[count].setProperty(AtomInterface.AMBER_TYPE_INDEX, index);
          } catch (Exception ex) {
            throw new Exception();
          }
        }
        //strncpy(buf, bufer + j * dl, dl);
        //buf[dl] = 0x00;
        //atom[count].charge = atof(buf) / CHARGES_TO_KCAL_AMBER;


        /*
         * int n = nat - count + 1 >= repeat ? repeat : nat - count + 1; for (int j = 0; j < n; j++) { //strncpy(buf, bufer + j *
         * dl, dl); buf[dl] = 0x00; atom[count].ambtype = atoi(buf) - 1; // goto to base 0 ++count; }
         */
      }

      if (logger.isLoggable(Level.INFO)) {
        logger.log(Level.INFO, "Finished reading atom type indices...\nReading excluded atoms...");
      }

      // -- Read excluded atoms using format ( default 12i6)
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 1;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); // fget_line(bufer, 256, parm);
            /*
         * int n = natoms - count + 1 >= repeat ? repeat : natoms - count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j *
         * dl, dl); buf[dl] = 0x00; //atom[count].ambtype = atoi( buf ); // TODO ++count; }
         */
      }

      int nttyp = ntypes * (ntypes + 1) / 2;
      int ntype = ntypes * ntypes;

      if (logger.isLoggable(Level.INFO)) {
        logger.log(Level.INFO, "Finished reading excluded atoms...\nReading nonbonded parm indices...");
      }

      // -- Read nonboded parm index using format ( default 12i6)
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = ntype % repeat > 0 ? ntype / repeat + 1 : ntype / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 1;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); // fget_line(bufer, 256, parm);
            /*
         * int n = ntype - count + 1 >= repeat ? repeat : ntype - count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j * dl,
         * dl); buf[dl] = 0x00; //atom[count].ambtype = atoi( buf ); // TODO ++count; }
         */
      }

      if (logger.isLoggable(Level.INFO)) {
        logger.log(Level.INFO, "Finished reading nonbonded parm indices...\nReading residue labels...");
      }

      // -- Read residue labels using format ( default 20a4)
      repeat = 20;
      dl = 4;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }

      nlines = nres % repeat > 0 ? nres / repeat + 1 : nres / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 1;
      k = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = nres - count + 1 >= repeat ? repeat
            : nres - count + 1;
        for (int j = 0; j < n; j++, k++, count++) {
          mol.addMonomer(line.substring(dl * j,
              dl * j + dl).trim());
          //strncpy(buf, bufer + j * dl, dl);
        }
      }

      // -- Read residue pointers using format ( default 12i6)
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nres % repeat > 0 ? nres / repeat + 1 : nres / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 1;
      k = 0;
      int[] start = new int[nres];
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = nres - count + 1 >= repeat ? repeat
            : nres - count + 1;
        for (int j = 0; j < n; j++, k++, count++) {
          //strncpy(buf, bufer + j * dl, dl);
          try {
            start[count - 1] = Integer.parseInt(line.substring(dl * j,
                dl * j + dl).trim());
            --start[count - 1];
          } catch (Exception ex) {
            throw new Exception();
          }
        }
      }

      for (int i = 0; i < mol.getNumberOfMonomers(); i++) {
        int begin = start[i];
        int end = i < mol.getNumberOfMonomers() - 1 ? start[i + 1]
            : nat;
        for (int j = begin; j < end; j++) {
          mol.addAtom(atoms[j], i);
        }
      }

      // --- // %FLAG BOND_FORCE_CONSTANT
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = numbnd % repeat > 0 ? numbnd / repeat + 1
          : numbnd / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- read BOND_EQUIL_VALUE
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = numbnd % repeat > 0 ? numbnd / repeat + 1
          : numbnd / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read ANGLE_FORCE_CONSTANT
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = numang % repeat > 0 ? numang / repeat + 1
          : numang / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read ANGLE_EQUIL_VALUE
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = numang % repeat > 0 ? numang / repeat + 1
          : numang / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read DIHEDRAL_FORCE_CONSTANT
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nptra % repeat > 0 ? nptra / repeat + 1
          : nptra / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read DIHEDRAL_PERIODICITY
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nptra % repeat > 0 ? nptra / repeat + 1
          : nptra / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read DIHEDRAL_PHASE
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nptra % repeat > 0 ? nptra / repeat + 1
          : nptra / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read SOLTY
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }

      nlines = natyp % repeat > 0 ? natyp / repeat + 1 : natyp / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 1;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
            /*
         * int n = natyp - count + 1 >= repeat ? repeat : natyp - count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j * dl,
         * dl); buf[dl] = 0x00; //atom[count].charge = atof( buf )/CHARGES_TO_KCAL_AMBER; ++count; }
         */
      }

      // --- Read LENNARD_JONES_ACOEF
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nttyp % repeat > 0 ? nttyp / repeat + 1
          : nttyp / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read LENNARD_JONES_BCOEF
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nttyp % repeat > 0 ? nttyp / repeat + 1
          : nttyp / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Reading BONDS_INC_HYDROGEN
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = (nbonh * 3) % repeat > 0 ? nbonh * 3 / repeat + 1
          : nbonh * 3 / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 1;
      k = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = nbonh * 3 - count + 1 >= repeat ? repeat
            : nbonh * 3 - count + 1;
        for (int j = 0; j < n; j++, k++, count++) {
          //strncpy(buf, bufer + j * dl, dl);
          try {
            bonds[k] = Integer.parseInt(line.substring(dl * j,
                dl * j + dl).trim());
          } catch (Exception ex) {
            throw new Exception();
          }
          //itemp[k] = atoi(buf);
        }
      }

      for (int i = 0; i < nbonh; i++) {
        int origin = bonds[3 * i] / 3; // + 1;
        int target = bonds[3 * i + 1] / 3; // + 1;
        AtomInterface a_i = mol.getAtomInterface(origin);
        AtomInterface a_j = mol.getAtomInterface(target);
        BondInterface bond = mol.getNewBondInstance(a_i, a_j);
        mol.addBond(bond);

        //inter12[i].i = bond[i + 1].origin;
        //inter12[i].j = bond[i + 1].target;
        //int ref = itemp[3 * i + 2] - 1;
        //inter12[i].keq = ff.bondParTable[ref].fc;
        //inter12[i].req = ff.bondParTable[ref].ebl;
      }

      // --- Reading BONDS_WITHOUT_HYDROGEN
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = (nbona * 3) % repeat > 0 ? nbona * 3 / repeat + 1
          : nbona * 3 / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 1;
      k = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = 3 * nbona - count + 1 >= repeat ? repeat
            : 3 * nbona - count + 1;
        for (int j = 0; j < n; j++, k++, count++) {
          //strncpy(buf, bufer + j * dl, dl);
          try {
            bonds[k] = Integer.parseInt(line.substring(dl * j,
                dl * j + dl).trim());
          } catch (Exception ex) {
            throw new Exception();
          }
          //itemp[k] = atoi(buf);
        }
      }

      for (int i = 0; i < nbona; i++) {
        //bond[i + 1].origin = itemp[3 * j] / 3 + 1;
        //bond[i + 1].target = itemp[3 * j + 1] / 3 + 1;

        int origin = bonds[3 * i] / 3; // + 1;
        int target = bonds[3 * i + 1] / 3; // + 1;
        AtomInterface a_i = mol.getAtomInterface(origin);
        AtomInterface a_j = mol.getAtomInterface(target);
        BondInterface bond = mol.getNewBondInstance(a_i, a_j);
        mol.addBond(bond);

        //inter12[i].i = bond[i + 1].origin;
        //inter12[i].j = bond[i + 1].target;
        //int ref = itemp[3 * j + 2] - 1;
        //inter12[i].keq = ff.bondParTable[ref].fc;
        //inter12[i].req = ff.bondParTable[ref].ebl;
      }

      // --- Read ANGLES_INC_HYDROGEN
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = ntheth * 4 % repeat > 0 ? ntheth * 4 / repeat + 1
          : ntheth * 4 / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read ANGLES_WITHOUT_HYDROGEN
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = mtheta * 4 % repeat > 0 ? mtheta * 4 / repeat + 1
          : mtheta * 4 / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read DIHEDRALS_INC_HYDROGEN
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nphih * 5 % repeat > 0 ? nphih * 5 / repeat + 1
          : nphih * 5 / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read DIHEDRALS_WITHOUT_HYDROGEN
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nphia * 5 % repeat > 0 ? nphia * 5 / repeat + 1
          : nphia * 5 / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read EXCLUDED_ATOMS_LIST
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nnb % repeat > 0 ? nnb / repeat + 1 : nnb / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read HBOND_ACOEF
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nphb % repeat > 0 ? nphb / repeat + 1 : nphb / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read HBOND_BCOEF
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nphb % repeat > 0 ? nphb / repeat + 1 : nphb / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read HBCUT
      repeat = 5;
      dl = 16;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nphb % repeat > 0 ? nphb / repeat + 1 : nphb / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read AMBER_ATOM_TYPE
      repeat = 20;
      dl = 4;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 1;
      k = 0;
      int element;
      String aType;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
        int n = nat - count + 1 >= repeat ? repeat : nat - count + 1;
        for (int j = 0; j < n; j++, k++, count++) {
          //strncpy(buf, bufer + dl * j, dl);
          //buf[dl] = 0x00;
          //atom[count].Set_Amber_Name( buf );
          //atom[count].Set_Amber_Name(strtok(buf, " "));
          aType = line.substring(dl * j, dl * j + dl).trim();
          atoms[count - 1].setProperty(AtomInterface.AMBER_NAME, aType);
          element = AmberUtilities.getElement(aType);
          atoms[count - 1].setAtomicNumber(element);
        }
      }

      // --- Read TREE_CHAIN_CLASSIFICATION
      repeat = 20;
      dl = 4;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read JOIN_ARRAY
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

      // --- Read IROTAT
      repeat = 12;
      dl = 6;
      if (new_format) {
        line = in.readLine();
        line = in.readLine(); // %FORMAT(
        parseFormat(line);
      }
      nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
      if (nlines == 0) {
        nlines = 1;
      }
      count = 0;
      for (int i = 0; i < nlines; i++) {
        line = in.readLine(); //fget_line(bufer, 256, parm);
      }

    } catch (Exception e) {
      throw new Exception("Error parsing Amber prmtop file: " + e.getMessage());
    }

    return mol;
  }

  /**
   * This subroutine parses only a NEW prmtop format
   *
   * @param mol
   * @param filename
   * @return
   * @throws Exception
   */
  public MoleculeInterface parseNewPrmtop(MoleculeInterface mol, String filename) throws Exception {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(filename));
    } catch (Exception e) {
      throw new Exception("Error opening Amber prmtop file " + filename + " : " + e.getMessage());
    }
    try {
      return parseNewPrmtop(mol, in);
    } catch (IOException e) {
      throw new Exception("Error parsing Amber prmtop file " + filename + " : " + e.getMessage());
    }
  }

  /**
   * This subroutine parses only a NEW prmtop format
   *
   * @param mol
   * @param filename
   * @return
   * @throws Exception
   */
  public MoleculeInterface parseNewPrmtop(MoleculeInterface mol, BufferedReader in) throws Exception {

    int nlines, count, k;
    String line;
    AtomInterface[] atoms = null;
    int[] bonds = null;
    int nttyp = 0;
    double[] nbTemp = null;
    boolean new_format = false;
    boolean atomicNumbersAlreadyRead = false;

    // --- Statrting to read
    String[] tokens = null;

    try {

      while ((line = in.readLine()) != null) {
        // --- Looking for line starting with %FLAG
        if (!line.startsWith("%FLAG ")) {
          continue;
        }
        // Decide what kind of flag...

        tokens = line.trim().split("\\s+");

        if (tokens.length < 2) {
          logger.severe("%FLAG does not have the second parameter! Ignoring...");
          continue;
        }

        PRMTOP_FLAGS flag = PRMTOP_FLAGS.UNKNOWN;

        tokens[1] = tokens[1].toUpperCase();
        try {
          flag = PRMTOP_FLAGS.valueOf(tokens[1]);
        } catch (Exception ex) {
          logger.severe("Uknown %FLAG \"" + tokens[1] + "\"! Ignoring...");
          continue;
        }

        // --- Reading format...
        line = in.readLine();
        if (line == null) {
          throw new Exception("Got EOF while expecting a %FORMAT line for flag " + tokens[1]);
        } else if (!line.startsWith("%FORMAT")) {
          throw new Exception("Expected a %FORMAT line for flag " + tokens[1] + ". Got \"" + line + "\"");
        }

        parseFormat(line);

        try {
          switch (flag) {
            case TITLE:
              // --- Read molecule name
              line = in.readLine();
              if (line == null) {
                throw new Exception("Unexpected EOF while reading Molecule name (TITLE section)");
              }
              if (logger.isLoggable(Level.INFO)) {
                logger.info("Molecule name: " + line);
              }
              mol.setName(line.trim());
              break;

            case POINTERS:
              // --- Read options

              int[] itemp = new int[31];

              nlines = 31 % repeat > 0 ? 31 / repeat + 1 : 31 / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 0;
              k = 0;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading POINTERS section");
                }
                int n = 31 - (count + 1) + 1 >= repeat ? repeat : 31 - (count + 1) + 1;
                for (int j = 0; j < n; j++, k++, count++) {

                  try {
                    itemp[k] = Integer.parseInt(line.substring(dl * j, dl * j + dl).trim());
                  } catch (Exception ex) {
                    throw new Exception("Error parsing POINTERS section: line: " + line + " : " + ex.getMessage());
                  }
                }
              }

              nat = itemp[0];
              ntypes = itemp[1];
              nbonh = itemp[2];
              mbona = itemp[3];
              ntheth = itemp[4];
              mtheta = itemp[5];
              nphih = itemp[6];
              mphia = itemp[7];
              nhparm = itemp[8];
              nparm = itemp[9];
              nnb = itemp[10];
              nres = itemp[11];

              nbona = itemp[12];
              ntheta = itemp[13];
              nphia = itemp[14];
              numbnd = itemp[15];
              numang = itemp[16];
              nptra = itemp[17];
              natyp = itemp[18];
              nphb = itemp[19];
              ifpert = itemp[20];
              nbper = itemp[21];
              ngper = itemp[22];
              ndper = itemp[23];

              mbper = itemp[24];
              mgper = itemp[25];
              mdper = itemp[26];
              ifbox = itemp[27];
              nmxrs = itemp[28];
              ifcap = itemp[29];
              numextra = itemp[30];

              if (logger.isLoggable(Level.INFO)) {
                logger.info(String.format(
                    "\nnatom  = %7d ntypes = %7d nbonh = %7d mbona  = %7d"
                    + "\nntheth = %7d mtheta = %7d nphih = %7d mphia  = %7d"
                    + "\nnhparm = %7d nparm  = %7d nnb   = %7d nres   = %7d"
                    + "\nnbona  = %7d ntheta = %7d nphia = %7d numbnd = %7d"
                    + "\nnumang = %7d nptra  = %7d natyp = %7d nphb   = %7d"
                    + "\nifbox  = %7d nmxrs  = %7d ifcap = %7d nextra = %7d",
                    nat, ntypes, nbonh, mbona, ntheth, mtheta, nphih, mphia,
                    nhparm, nparm, nnb, nres, nbona, ntheta, nphia,
                    numbnd, numang, nptra, natyp, nphb, ifpert, nbper, ngper,
                    ndper,
                    mbper, mgper, mdper, ifbox, nmxrs, ifcap, numextra));
              }

              nttyp = ntypes * (ntypes + 1) / 2;
              Aij = new double[ntypes][ntypes];
              Bij = new double[ntypes][ntypes];
              eps = new double[ntypes];
              rho = new double[ntypes];
              nbTemp = new double[nttyp];

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "There are " + nat + " atoms in PARM file");
              }

              atoms = new AtomInterface[nat];
              for (int i = 0; i < nat; i++) {
                atoms[i] = mol.getNewAtomInstance();
              }

              if (nbonh > 0 || nbona > 0) {
                bonds = new int[nbonh > nbona ? 3 * nbonh : 3 * nbona];
              }
              break;

            case ATOM_NAME:
              // Read Amber atom types (default 20a4)

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Reading atoms types...");
              }

              nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 0;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = nat - (count + 1) + 1 >= repeat ? repeat : nat - (count + 1) + 1;
                for (int j = 0; j < n; j++) {
                  atoms[count].setName(line.substring(j * dl, j * dl + dl).trim());
                  ++count;
                }
              }

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Finished reading atoms types...");
              }

              break;

            case CHARGE:
              // --- Read charges (default 5e16.8)
              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Started reading atomic charges...");
              }

              nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              float total_charge = 0;
              count = 0;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = nat - (count + 1) + 1 >= repeat ? repeat : nat - (count + 1) + 1;
                for (int j = 0; j < n; j++) {
                  try {
                    Float charge = Float.parseFloat(line.substring(dl * j, dl * j + dl).trim());
                    charge /= CHARGES_TO_KCAL_AMBER;
                    total_charge += charge;
                    atoms[count].setProperty(AtomInterface.ATOMIC_CHARGE, charge);
                  } catch (Exception ex) {
                    throw new Exception("Error parsing charge in line: " + line + " : " + ex.getMessage());
                  }
                  ++count;
                }
              }

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Finished reading atomic charges...");
                logger.log(Level.INFO, "Total charge: " + String.valueOf(total_charge));
              }
              break;

            //************************************************************************
            case ATOMIC_NUMBER:

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Started reading atomic numbers...");
              }

              nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 0;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = nat - (count + 1) + 1 >= repeat ? repeat : nat - (count + 1) + 1;
                for (int j = 0; j < n; j++) {
                  try {
                    Integer atNumber = Integer.parseInt(line.substring(dl * j, dl * j + dl).trim());
                    atoms[count].setAtomicNumber(atNumber);
                  } catch (Exception ex) {
                    throw new Exception("Error parsing atomic number in line: " + line + " : " + ex.getMessage());
                  }
                  ++count;
                }
              }

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Finished reading atomic numbers...");
              }

              atomicNumbersAlreadyRead = true;
              break;

            //************************************************************************
            case MASS:
              // -- Read atomic masses using format ( default 5e16.8)

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Reading atomic masses...");
              }

              nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 0;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = nat - (count + 1) + 1 >= repeat ? repeat : nat - (count + 1) + 1;
                for (int j = 0; j < n; j++) {
                  try {
                    float mass = Float.parseFloat(line.substring(dl * j, dl * j + dl).trim());
                    atoms[count].setAtomicMass(mass);
                  } catch (Exception ex) {
                    throw new Exception("Error parsing atomic mass in line: " + line + " : " + ex.getMessage());
                  }
                  ++count;
                }
              }

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Finished reading atomic masses...");
              }
              break;

            case ATOM_TYPE_INDEX:
              // -- Read atom type index using format ( default 12i6)

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Reading atom type indices...");
              }

              nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 1;
              Integer index;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = nat - count + 1 >= repeat ? repeat : nat - count + 1;
                for (int j = 0; j < n; j++, count++) {
                  try {
                    index = Integer.parseInt(line.substring(dl * j, dl * j + dl).trim()) - 1;
                    if (index < 0 || index > ntypes - 1) {
                      logger.severe("ATOM TYPE INDEX for atom " + count + " = " + (index + 1) + " Should be in 1-" + ntypes + " range");
                    }
                    atoms[count - 1].setProperty(AtomInterface.AMBER_TYPE_INDEX, index);
                  } catch (Exception ex) {
                    throw new Exception("Error parsing atom index in line: " + line + " : " + ex.getMessage());
                  }
                }
              }

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Finished reading atom type indices...");
              }
              break;

            //case NUMBER_EXCLUDED_ATOMS:
            // -- Read excluded atoms using format ( default 12i6)
            //nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 1;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //int nttyp = ntypes * (ntypes + 1) / 2;
            //int ntype = ntypes * ntypes;
            //if (logger.isLoggable(Level.INFO)) {
            //  logger.log(Level.INFO, "Finished reading excluded atoms...\nReading nonbonded parm indices...");
            //}
            //break;
            //case NONBONDED_PARM_INDEX:
            // -- Read nonboded parm index using format ( default 12i6)
            //nlines = ntype % repeat > 0 ? ntype / repeat + 1 : ntype / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 1;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //if (logger.isLoggable(Level.INFO)) {
            //  logger.log(Level.INFO, "Finished reading nonbonded parm indices...\nReading residue labels...");
            //}
            //break;
            case RESIDUE_LABEL:
              // -- Read residue labels using format ( default 20a4)

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Reading residue labels...");
              }

              nlines = nres % repeat > 0 ? nres / repeat + 1 : nres / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 1;
              k = 0;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = nres - count + 1 >= repeat ? repeat : nres - count + 1;
                for (int j = 0; j < n; j++, k++, count++) {
                  mol.addMonomer(line.substring(dl * j, dl * j + dl).trim());
                }
              }

              if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Finished reading residue labels...");
              }
              break;

            //******************************************************
            case RESIDUE_POINTER:
              // -- Read residue pointers using format ( default 12i6)

              nlines = nres % repeat > 0 ? nres / repeat + 1 : nres / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 1;
              k = 0;
              int[] start = new int[nres];
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = nres - count + 1 >= repeat ? repeat : nres - count + 1;
                for (int j = 0; j < n; j++, k++, count++) {
                  try {
                    start[k] = Integer.parseInt(line.substring(dl * j, dl * j + dl).trim());
                    --start[k];
                  } catch (Exception ex) {
                    throw new Exception("Error parsing residue pointer in line: " + line + " : " + ex.getMessage());
                  }
                }
              }

              for (int i = 0; i < mol.getNumberOfMonomers(); i++) {
                int begin = start[i];
                int end = i < mol.getNumberOfMonomers() - 1 ? start[i + 1] : nat;
                for (int j = begin; j < end; j++) {
                  mol.addAtom(atoms[j], i);
                }
              }
              break;

            //*************************************************
            //case BOND_FORCE_CONSTANT:
            // --- // %FLAG BOND_FORCE_CONSTANT
            //nlines = numbnd % repeat > 0 ? numbnd / repeat + 1 : numbnd / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //*****************************************************
            //case BOND_EQUIL_VALUE:
            // --- read BOND_EQUIL_VALUE
            //nlines = numbnd % repeat > 0 ? numbnd / repeat + 1 : numbnd / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //************************************************
            //case ANGLE_FORCE_CONSTANT:
            // --- Read ANGLE_FORCE_CONSTANT
            //nlines = numang % repeat > 0 ? numang / repeat + 1 : numang / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //************************************************
            //case ANGLE_EQUIL_VALUE:
            // --- Read ANGLE_EQUIL_VALUE
            //nlines = numang % repeat > 0 ? numang / repeat + 1 : numang / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //********************************************
            //case DIHEDRAL_FORCE_CONSTANT:
            // --- Read DIHEDRAL_FORCE_CONSTANT
            //nlines = nptra % repeat > 0 ? nptra / repeat + 1 : nptra / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //*********************************************
            //case DIHEDRAL_PERIODICITY:
            // --- Read DIHEDRAL_PERIODICITY
            //nlines = nptra % repeat > 0 ? nptra / repeat + 1 : nptra / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //************************************************
            //case DIHEDRAL_PHASE:
            // --- Read DIHEDRAL_PHASE
            //nlines = nptra % repeat > 0 ? nptra / repeat + 1 : nptra / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //**************************************
            //case SOLTY:
            // --- Read SOLTY
            //nlines = natyp % repeat > 0 ? natyp / repeat + 1 : natyp / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 1;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //*****************************************************
            case LENNARD_JONES_ACOEF:
              // --- Read LENNARD_JONES_ACOEF

              nlines = nttyp % repeat > 0 ? nttyp / repeat + 1 : nttyp / repeat;
              if (nlines == 0) {
                nlines = 1;
              }

              k = 0;
              count = 1;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                // **************
                int n = nttyp - count + 1 >= repeat ? repeat : nttyp - count + 1;
                for (int j = 0; j < n; j++, count++, k++) {
                  try {
                    String temp = line.substring(dl * j, dl * j + dl).trim();
                    nbTemp[k] = Double.parseDouble(temp);
                  } catch (Exception ex) {
                    //throw new Exception("Error parsing LJ A Coeff in line: " + line + " : " + ex.getMessage());
                    logger.severe("Error parsing LJ A Coeff in line: " + line + " : " + ex.getMessage());
                  }
                }
              }
              System.out.println("Aij");
              Utils.printVector(nbTemp);
              try {
                for (int i = 0; i < ntypes; i++) {
                  for (int j = 0; j <= i; j++) {
                    Aij[i][j] = nbTemp[i * (i + 1) / 2 + j];
                    Aij[j][i] = Aij[i][j];
                  }
                }
              } catch (Exception ex) {
                logger.severe("Error while assigning Aij: " + ex.getMessage());
              }

              break;
            //***************************************************
            case LENNARD_JONES_BCOEF:
              // --- Read LENNARD_JONES_BCOEF
              nlines = nttyp % repeat > 0 ? nttyp / repeat + 1 : nttyp / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              k = 0;
              count = 1;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                // **************
                int n = nttyp - count + 1 >= repeat ? repeat : nttyp - count + 1;
                for (int j = 0; j < n; j++, count++, k++) {
                  try {
                    nbTemp[k] = Double.parseDouble(line.substring(dl * j, dl * j + dl).trim());
                  } catch (Exception ex) {
                    //throw new Exception("Error parsing LJ A Coeff in line: " + line + " : " + ex.getMessage());
                    logger.severe("Error parsing LJ B Coeff in line: " + line + " : " + ex.getMessage());
                  }
                }
              }

              System.out.println("Bij");
              Utils.printVector(nbTemp);

              try {
                for (int i = 0; i < ntypes; i++) {
                  for (int j = 0; j <= i; j++) {
                    Bij[i][j] = nbTemp[i * (i + 1) / 2 + j];
                    Bij[j][i] = Bij[i][j];
                  }
                }
              } catch (Exception ex) {
                logger.severe("Error while assigning Bij: " + ex.getMessage());
              }
              break;
            //*********************************************************
            case BONDS_INC_HYDROGEN:
              // --- Reading BONDS_INC_HYDROGEN

              nlines = (nbonh * 3) % repeat > 0 ? nbonh * 3 / repeat + 1 : nbonh * 3 / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 1;
              k = 0;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = nbonh * 3 - count + 1 >= repeat ? repeat : nbonh * 3 - count + 1;
                for (int j = 0; j < n; j++, k++, count++) {

                  try {
                    bonds[k] = Integer.parseInt(line.substring(dl * j, dl * j + dl).trim());
                  } catch (Exception ex) {
                    throw new Exception();
                  }

                }
              }

              for (int i = 0; i < nbonh; i++) {
                int origin = bonds[3 * i] / 3; // + 1;
                int target = bonds[3 * i + 1] / 3; // + 1;
                AtomInterface a_i = mol.getAtomInterface(origin);
                AtomInterface a_j = mol.getAtomInterface(target);
                BondInterface bond = mol.getNewBondInstance(a_i, a_j);
                mol.addBond(bond);
              }
              break;

            //****************************************************
            case BONDS_WITHOUT_HYDROGEN:
              // --- Reading BONDS_WITHOUT_HYDROGEN
              nlines = (nbona * 3) % repeat > 0 ? nbona * 3 / repeat + 1 : nbona * 3 / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 1;
              k = 0;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = 3 * nbona - count + 1 >= repeat ? repeat : 3 * nbona - count + 1;
                for (int j = 0; j < n; j++, k++, count++) {
                  try {
                    bonds[k] = Integer.parseInt(line.substring(dl * j, dl * j + dl).trim());
                  } catch (Exception ex) {
                    throw new Exception();
                  }
                }
              }

              for (int i = 0; i < nbona; i++) {
                int origin = bonds[3 * i] / 3; // + 1;
                int target = bonds[3 * i + 1] / 3; // + 1;
                AtomInterface a_i = mol.getAtomInterface(origin);
                AtomInterface a_j = mol.getAtomInterface(target);
                BondInterface bond = mol.getNewBondInstance(a_i, a_j);
                mol.addBond(bond);
              }
              break;

            //***************************************************************
            //case ANGLES_INC_HYDROGEN:
            // --- Read ANGLES_INC_HYDROGEN
            //nlines = ntheth * 4 % repeat > 0 ? ntheth * 4 / repeat + 1 : ntheth * 4 / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //********************************************************************
            //case ANGLES_WITHOUT_HYDROGEN:
            // --- Read ANGLES_WITHOUT_HYDROGEN
            //nlines = mtheta * 4 % repeat > 0 ? mtheta * 4 / repeat + 1 : mtheta * 4 / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //********************************************************************
            //case DIHEDRALS_INC_HYDROGEN:
            // --- Read DIHEDRALS_INC_HYDROGEN
            //nlines = nphih * 5 % repeat > 0 ? nphih * 5 / repeat + 1 : nphih * 5 / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //*********************************************************************
            //case DIHEDRALS_WITHOUT_HYDROGEN:
            // --- Read DIHEDRALS_WITHOUT_HYDROGEN
            //nlines = nphia * 5 % repeat > 0 ? nphia * 5 / repeat + 1 : nphia * 5 / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //***********************************************************************
            //case EXCLUDED_ATOMS_LIST:
            // --- Read EXCLUDED_ATOMS_LIST
            //nlines = nnb % repeat > 0 ? nnb / repeat + 1 : nnb / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //**********************************************************************
            //case HBOND_ACOEF:
            // --- Read HBOND_ACOEF
            //nlines = nphb % repeat > 0 ? nphb / repeat + 1 : nphb / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //***********************************************************************
            //case HBOND_BCOEF:
            // --- Read HBOND_BCOEF
            //nlines = nphb % repeat > 0 ? nphb / repeat + 1 : nphb / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //************************************************************************
            //case HBCUT:
            // --- Read HBCUT
            //nlines = nphb % repeat > 0 ? nphb / repeat + 1 : nphb / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //***************************************************************************
            case AMBER_ATOM_TYPE:
              // --- Read AMBER_ATOM_TYPE

              nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
              if (nlines == 0) {
                nlines = 1;
              }
              count = 1;
              k = 0;
              int element;
              String aType;
              for (int i = 0; i < nlines; i++) {
                line = in.readLine();
                if (line == null) {
                  throw new Exception("Unexpected EOF while reading " + tokens[1] + " section");
                }
                int n = nat - count + 1 >= repeat ? repeat : nat - count + 1;
                for (int j = 0; j < n; j++, k++, count++) {
                  aType = line.substring(dl * j, dl * j + dl).trim();
                  atoms[count - 1].setProperty(AtomInterface.AMBER_NAME, aType);
                  if (!atomicNumbersAlreadyRead) {
                    element = AmberUtilities.getElement(aType);
                    atoms[count - 1].setAtomicNumber(element);
                  }
                }
              }
              break;

            //*********************************************************************
            //case TREE_CHAIN_CLASSIFICATION:
            // --- Read TREE_CHAIN_CLASSIFICATION
            //nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //*************************************************************************
            //case JOIN_ARRAY:
            // - -- Read JOIN_ARRAY
            //nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            //**************************************************************************
            //case IROTAT:
            // --- Read IROTAT
            //nlines = nat % repeat > 0 ? nat / repeat + 1 : nat / repeat;
            //if (nlines == 0) {
            //  nlines = 1;
            //}
            //count = 0;
            //for (int i = 0; i < nlines; i++) {
            //  line = in.readLine();
            //}
            //break;
            default:
              if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Ignoring section " + tokens[1]);
              }
              break;
          }
        } catch (Exception ex) {
          throw new Exception("Error parsing data for flag " + tokens[1] + ": " + ex.getMessage());
        }

      }

    } catch (Exception e) {
      throw new Exception("Error parsing Amber prmtop file: " + e.getMessage());
    }

    // --- Post-processing
    StringBuilder sb = new StringBuilder();

    if (logger.isLoggable(Level.INFO)) {
      sb.delete(0, sb.length());

      StringWriter sw = new StringWriter();
      sw.append("\nAij\n");
      printMatrix(Aij, sw);

      sw.append("\nBij\n");
      printMatrix(Bij, sw);
      sb.append(sw.toString());

      sb.append("\nRho & Eps\n");
    }
    try {
      int ambtype = 0;
      for (int i = 0; i < ntypes; i++) {
        ambtype = -1;
        if (Bij[i][i] == 0.0) {
          rho[i] = 0.0;
          eps[i] = 0.0;
        } else {
          rho[i] = 0.5 * Math.pow(2.0 * Aij[i][i] / Bij[i][i], 1.0 / 6.0);
          eps[i] = Aij[i][i] / Math.pow(2.0 * rho[i], 12);
        }
        if (logger.isLoggable(Level.INFO)) {
          sb.append(String.format("%3d: %8.4f  %12.4f", i + 1, rho[i], eps[i]));
          for (int j = 0; j < nat; j++) {
            if (atoms[j].getProperty(AtomInterface.AMBER_TYPE_INDEX) != null) {
              ambtype = ((Integer) atoms[j].getProperty(AtomInterface.AMBER_TYPE_INDEX));
              if (ambtype == i) {
                sb.append(" " + atoms[j].getProperty(AtomInterface.AMBER_NAME) + "\n");
                break;
              }
            }
          }
          if (ambtype == -1) {
            sb.append(" UNKNOWN\n");
          }
        }
      }
      if (logger.isLoggable(Level.INFO)) {
        logger.info(sb.toString());
      }

    } catch (Exception e) {
      logger.severe("Error while deriving eps and/or rho: " + e.getMessage());
    }

    return mol;
  }

  private void parseFormat(String format) throws Exception {
    StringTokenizer st = new StringTokenizer(format, "()", false);
    if (st.countTokens() < 2) {
      throw new Exception("Cannot parse format string: " + format);
    }
    st.nextToken();
    String value = st.nextToken();

    st = new StringTokenizer(value, "aAiIeEfF", false);
    if (st.countTokens() == 1) {
      repeat = 0;
    } else {
      try {
        repeat = Integer.parseInt(st.nextToken());
      } catch (Exception ex) {
        throw new Exception("Cannot parse repeat in format string: " + format);
      }
    }

    value = st.nextToken();
    if (value.contains(".")) {
      value = value.substring(0, value.indexOf("."));
    }
    try {
      dl = Integer.parseInt(value);
    } catch (Exception ex) {
      throw new Exception("Cannot parse field length in format string: " + format);
    }

  }

  /*
   * int parse_format( char *format, int &rep, int &dl) { char *s = strdup(format); char *token = strtok( s, "()"); token =
   * strtok(NULL,"()"); //printf("\n1st parse: %s",token); token = strtok( token, "aAiIeEfF"); rep = atoi( token ); dl = atoi(
   * strtok(NULL," .")); //printf("\nRepeat: %d Length: %d",rep,dl); delete [] s; if ( dl <= 0 ) return 1; else return 0; }
   */
  /*
   * int Molecule Read_AMBER_PARM(const char * fname, FFParam & ff) {
   *
   *
   * // Allocate parameter tables
   *
   * ff.nBondPar = numbnd; if (ff.bondParTable != NULL) { delete[] ff.bondParTable; } ff.bondParTable = new
   * BondParameters[ff.nBondPar]; assert (ff.bondParTable); memset(ff.bondParTable, 0, sizeof(BondParameters) * ff.nBondPar);
   *
   * ff.nAnglePar = numang; if (ff.angleParTable != NULL) { delete[] ff.angleParTable; } ff.angleParTable = new
   * AngleParameters[ff.nAnglePar]; assert (ff.angleParTable);
   *
   * ff.nTorsionPar = nptra; if (ff.torsionParTable != NULL) { delete[] ff.torsionParTable; } ff.torsionParTable = new
   * TorsionParameters[ff.nTorsionPar]; assert (ff.torsionParTable); memset(ff.torsionParTable, 0, sizeof(TorsionParameters) *
   * ff.nTorsionPar);
   *
   * //fmtin = rfmt //type = 'bond_force_constant' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (rk(i), i =
   * 1,numbnd)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG BOND_FORCE_CONSTANT fget_line(bufer, 256, parm);
   * // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse BOND_FORCE_CONSTANT format: %s\n", bufer); return -1; } }
   *
   * nlines = ff.nBondPar % repeat ? ff.nBondPar / repeat + 1 : ff.nBondPar / repeat; if (nlines == 0) { nlines = 1; } count = 1;
   * for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = ff.nBondPar - count + 1 >= repeat ? repeat : ff.nBondPar -
   * count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; ff.bondParTable[count - 1].fc = 2.0 *
   * atof(buf); // !!! Multiplied by 2 to be compatible with energy calc ++count; } }
   *
   * //fmtin = rfmt //type = 'bond_equil_value' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (req(i), i = 1,numbnd
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG BOND_EQUIL_VALUE fget_line(bufer, 256, parm); //
   * %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse BOND_EQUIL_VALUE format: %s\n", bufer); return -1; } }
   *
   * nlines = ff.nBondPar % repeat ? ff.nBondPar / repeat + 1 : ff.nBondPar / repeat; if (nlines == 0) { nlines = 1; } count = 1;
   * for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = ff.nBondPar - count + 1 >= repeat ? repeat : ff.nBondPar -
   * count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; ff.bondParTable[count - 1].ebl =
   * atof(buf); ++count; } }
   *
   * //fmtin = rfmt //type = 'angle_force_constant' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (tk(i), i =
   * 1,numang)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG ANGLE_FORCE_CONSTANT fget_line(bufer, 256, parm);
   * // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse ANGLE_FORCE_CONSTANT format: %s\n", bufer); return -1; } }
   *
   * nlines = ff.nAnglePar % repeat ? ff.nAnglePar / repeat + 1 : ff.nAnglePar / repeat; if (nlines == 0) { nlines = 1; } count = 1;
   * for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = ff.nAnglePar - count + 1 >= repeat ? repeat : ff.nAnglePar
   * - count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; ff.angleParTable[count - 1].fc =
   * atof(buf) / Pow2(RAD2DEG) * 2.0; // !!! ++count; } }
   *
   * //fmtin = rfmt //type = 'angle_equil_value' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (teq(i), i =
   * 1,numang)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG ANGLE_EQUIL_VALUE fget_line(bufer, 256, parm); //
   * %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse ANGLE_EQUIL_VALUE format: %s\n", bufer); return -1; } }
   *
   * nlines = ff.nAnglePar % repeat ? ff.nAnglePar / repeat + 1 : ff.nAnglePar / repeat; if (nlines == 0) { nlines = 1; } count = 1;
   * for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = ff.nAnglePar - count + 1 >= repeat ? repeat : ff.nAnglePar
   * - count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; ff.angleParTable[count - 1].ebl = 180.
   * / PI * atof(buf); // !!! Convert to degrees ++count; } }
   *
   * printf("\n\nAngle parameter table:\n"); for (i = 0; i < ff.nAnglePar; i++) { printf("\n%4d %8.3lf %8.3lf", i + 1,
   * ff.angleParTable[i].ebl, ff.angleParTable[i].fc); }
   *
   * //fmtin = rfmt //type = 'dihedral_force_constant' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (pk(i), i =
   * 1,nptra)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG DIHEDRAL_FORCE_CONSTANT fget_line(bufer, 256,
   * parm); // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM:
   * Cannot parse DIHEDRAL_FORCE_CONSTANT format: %s\n", bufer); return -1; } }
   *
   * nlines = ff.nTorsionPar % repeat ? ff.nTorsionPar / repeat + 1 : ff.nTorsionPar / repeat; if (nlines == 0) { nlines = 1; }
   * count = 1; for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = ff.nTorsionPar - count + 1 >= repeat ? repeat :
   * ff.nTorsionPar - count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00;
   * ff.torsionParTable[count - 1].npar = 1; ff.torsionParTable[count - 1].par[0].fc = 2.0 * atof(buf); // Mult by 2 to be
   * compatible with us ++count; } }
   *
   * //fmtin = rfmt //type = 'dihedral_periodicity' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (pn(i), i =
   * 1,nptra)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG DIHEDRAL_PERIODICITY fget_line(bufer, 256, parm);
   * // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse DIHEDRAL_PERIODICITY format: %s\n", bufer); return -1; } }
   *
   * nlines = ff.nTorsionPar % repeat ? ff.nTorsionPar / repeat + 1 : ff.nTorsionPar / repeat; if (nlines == 0) { nlines = 1; }
   * count = 1; for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = ff.nTorsionPar - count + 1 >= repeat ? repeat :
   * ff.nTorsionPar - count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00;
   * ff.torsionParTable[count - 1].par[0].per = atof(buf); ++count; } }
   *
   * //fmtin = rfmt //type = 'dihedral_phase' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (phase(i), i = 1,nptra)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG DIHEDRAL_PHASE fget_line(bufer, 256, parm); //
   * %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse DIHEDRAL_PHASE format: %s\n", bufer); return -1; } }
   *
   * nlines = ff.nTorsionPar % repeat ? ff.nTorsionPar / repeat + 1 : ff.nTorsionPar / repeat; if (nlines == 0) { nlines = 1; }
   * count = 1; for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = ff.nTorsionPar - count + 1 >= repeat ? repeat :
   * ff.nTorsionPar - count + 1; for (j = 0; j < n; j++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00;
   * ff.torsionParTable[count - 1].par[0].phase = atof(buf); ++count; } }
   *
   * printf("\n\nDihedral parameter table\n"); for (i = 0; i < ff.nTorsionPar; i++) { printf("\n%4d %8.3lf %8.3lf %8.3lf", i + 1,
   * ff.torsionParTable[i].par[0].fc, ff.torsionParTable[i].par[0].per, ff.torsionParTable[i].par[0].phase); } //fmtin = rfmt //type
   * = 'solty' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (solty(i), i = 1,natyp)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG SOLTY fget_line(bufer, 256, parm); // %FORMAT( if
   * (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI("Read_AMBER_PARM: Cannot parse SOLTY
   * format: %s\n", bufer); return -1; } }
   *
   * nlines = natyp % repeat ? natyp / repeat + 1 : natyp / repeat; if (nlines == 0) { nlines = 1; } count = 1; for (i = 0; i <
   * nlines; i++) { fget_line(bufer, 256, parm); int n = natyp - count + 1 >= repeat ? repeat : natyp - count + 1; for (j = 0; j <
   * n; j++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; //atom[count].charge = atof( buf )/CHARGES_TO_KCAL_AMBER; ++count;
   * } }
   *
   * // --- Allocate eps, rho, Aij & Bij
   *
   * ff.nAtypes = ntypes;
   *
   * if (ff.eps != NULL) { delete[] ff.eps; } ff.eps = new double[ntypes]; assert (ff.eps); if (ff.rho != NULL) { delete[] ff.rho; }
   * ff.rho = new double[ntypes]; assert (ff.rho);
   *
   * ff.Allocate_Aij_and_Bij(ntypes);
   *
   * //fmtin = rfmt //type = 'lennard_jones_acoef' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (cn1(i), i =
   * 1,nttyp)
   *
   * if (rmax < nttyp) { rmax = nttyp; rtemp = (double * ) realloc(rtemp, sizeof(double) * nttyp); assert (rtemp); }
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG LENNARD_JONES_ACOEF fget_line(bufer, 256, parm);
   * // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse LENNARD_JONES_ACOEF format: %s\n", bufer); return -1; } }
   *
   * nlines = nttyp % repeat ? nttyp / repeat + 1 : nttyp / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i = 0; i
   * < nlines; i++) { fget_line(bufer, 256, parm); int n = nttyp - count + 1 >= repeat ? repeat : nttyp - count + 1; for (j = 0; j <
   * n; j++, k++, count++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; rtemp[k] = atof(buf); } }
   *
   * for (i = 0; i < ntypes; i++) { for (j = 0; j <= i; j++) { ff.Aij[i][j] = rtemp[i * (i + 1) / 2 + j]; ff.Aij[j][i] =
   * ff.Aij[i][j]; } }
   *
   * //fmtin = rfmt //type = 'lennard_jones_bcoef' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (cn2(i), i =
   * 1,nttyp)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG LENNARD_JONES_BCOEF fget_line(bufer, 256, parm);
   * // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse LENNARD_JONES_BCOEF format: %s\n", bufer); return -1; } }
   *
   * nlines = nttyp % repeat ? nttyp / repeat + 1 : nttyp / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i = 0; i
   * < nlines; i++) { fget_line(bufer, 256, parm); int n = nttyp - count + 1 >= repeat ? repeat : nttyp - count + 1; for (j = 0; j <
   * n; j++, k++, count++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; rtemp[k] = atof(buf); } }
   *
   * for (i = 0; i < ntypes; i++) { for (j = 0; j <= i; j++) { ff.Bij[i][j] = rtemp[i * (i + 1) / 2 + j]; ff.Bij[j][i] =
   * ff.Bij[i][j]; } }
   *
   * // --- Allocate bonds
   *
   * nbonds = 0; if (bond != NULL) { delete[] bond; } bond = new Bond[nbonh + nbona + 1]; memset(bond, 0, (nbonh + nbona + 1) *
   * sizeof(Bond)); //ReAlloc_Bonds( nbonh + nbona ); nbonds = nbonh + nbona; ninter12 = nbonds; if (inter12 != NULL) { delete[]
   * inter12; } inter12 = new Inter12[ninter12]; assert (inter12); memset(inter12, 0, ninter12 * sizeof(Inter12));
   *
   * //fmtin = ifmt //type = 'bonds_inc_hydrogen' //call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt)
   * (ix(i+i12-1),ix(i+i14-1),ix(i+i16-1), //$ i = 1,nbonh)
   *
   * if (imax < nbonh * 3) { imax = nbonh * 3; itemp = (int * ) realloc(itemp, sizeof(int) * imax); assert (itemp); }
   *
   * repeat = 12; dl = 6; if (new_format) { fget_line(bufer, 256, parm); // %FLAG BONDS_INC_HYDROGEN fget_line(bufer, 256, parm); //
   * %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse BONDS_INC_HYDROGEN format: %s\n", bufer); return -1; } }
   *
   * nlines = nbonh * 3 % repeat ? nbonh * 3 / repeat + 1 : nbonh * 3 / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0;
   * for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = nbonh * 3 - count + 1 >= repeat ? repeat : nbonh * 3 -
   * count + 1; for (j = 0; j < n; j++, k++, count++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; itemp[k] = atoi(buf); } }
   *
   * for (i = 0; i < nbonh; i++) { bond[i + 1].origin = itemp[3 * i] / 3 + 1; inter12[i].i = bond[i + 1].origin; bond[i + 1].target
   * = itemp[3 * i + 1] / 3 + 1; inter12[i].j = bond[i + 1].target; int ref = itemp[3 * i + 2] - 1; inter12[i].keq =
   * ff.bondParTable[ref].fc; inter12[i].req = ff.bondParTable[ref].ebl; } //fmtin = ifmt //type = 'bonds_without_hydrogen' //call
   * nxtsec(nf, 6, 0,fmtin, type, fmt, iok) //read(nf,fmt) (ix(i+i18-1),ix(i+i20-1),ix(i+i22-1),i = 1,nbona)
   *
   * if (imax < nbona * 3) { imax = nbona * 3; itemp = (int * ) realloc(itemp, sizeof(int) * imax); assert (itemp); }
   *
   * repeat = 12; dl = 6; if (new_format) { fget_line(bufer, 256, parm); // %FLAG BONDS_WITHOUT_HYDROGEN fget_line(bufer, 256,
   * parm); // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM:
   * Cannot parse BONDS_WITHOUT_HYDROGEN format: %s\n", bufer); return -1; } }
   *
   * nlines = nbona * 3 % repeat ? nbona * 3 / repeat + 1 : nbona * 3 / repeat; if (nlines == 0) { nlines = 1; } int count_2 = nbonh
   * + 1; count = 1; k = 0; for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = 3 * nbona - count + 1 >= repeat ?
   * repeat : 3 * nbona - count + 1; for (j = 0; j < n; j++, k++, count++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00;
   * itemp[k] = atoi(buf); } }
   *
   * for (i = nbonh, j = 0; j < nbona; i++, j++) { bond[i + 1].origin = itemp[3 * j] / 3 + 1; inter12[i].i = bond[i + 1].origin;
   * bond[i + 1].target = itemp[3 * j + 1] / 3 + 1; inter12[i].j = bond[i + 1].target; int ref = itemp[3 * j + 2] - 1;
   * inter12[i].keq = ff.bondParTable[ref].fc; inter12[i].req = ff.bondParTable[ref].ebl; }
   *
   * int errors = 0; for (i = 1; i <= nbonds; i++) { if (bond[i].origin < 1 || bond[i].origin > natoms || bond[i].target < 1 ||
   * bond[i].target > natoms) { ++errors; printf("\nbond: %d i: %d j: %d natoms: %d", i, bond[i].origin, bond[i].target, natoms); }
   * } if (errors) { //SimpleExit("Error(s) in bonds",1); Log_to_GUI("Read_AMBER_PARM: Error(s) in bond definition\n"); return -1; }
   *
   * //printf("\nBond energy: %lf",Energy12() ); // assert( !feof(Mol2File) ); //MakeBondedToList(); //BuildBondMatrix(); //return
   * 0; // --- Allocate angles
   *
   * ninter13 = ntheth + ntheta; if (inter13 != NULL) { delete[] inter13; } inter13 = new Inter13[ninter13]; assert (inter13);
   *
   * // // fmtin = ifmt // type = 'angles_inc_hydrogen' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt)
   * (ix(i+i24-1),ix(i+i26-1),ix(i+i28-1),ix(i+i30-1), // + i = 1,ntheth)
   *
   *
   * if (imax < ntheth * 4) { imax = ntheth * 4; itemp = (int * ) realloc(itemp, sizeof(int) * imax); assert (itemp); }
   *
   * repeat = 12; dl = 6; if (new_format) { fget_line(bufer, 256, parm); // %FLAG ANGLES_INC_HYDROGEN fget_line(bufer, 256, parm);
   * // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse ANGLES_INC_HYDROGEN format: %s\n", bufer); return -1; } }
   *
   * nlines = ntheth * 4 % repeat ? ntheth * 4 / repeat + 1 : ntheth * 4 / repeat; if (nlines == 0) { nlines = 1; } count = 1; k =
   * 0; for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = 4 * ntheth - count + 1 >= repeat ? repeat : 4 * ntheth -
   * count + 1; for (j = 0; j < n; j++, k++, count++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; itemp[k] = atoi(buf); } }
   *
   * for (i = 0; i < ntheth; i++) { inter13[i].i = itemp[4 * i] / 3 + 1; inter13[i].j = itemp[4 * i + 1] / 3 + 1; inter13[i].k =
   * itemp[4 * i + 2] / 3 + 1; int ref = itemp[4 * i + 3] - 1; if (ref < 0) { printf("\n???: ref < 0 for ntheth: %d", ref); }
   * inter13[i].keq = ff.angleParTable[ref].fc; inter13[i].aeq = ff.angleParTable[ref].ebl; }
   *
   *
   * // fmtin = ifmt // type = 'angles_without_hydrogen' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt)
   * (ix(i+i32-1),ix(i+i34-1),ix(i+i36-1),ix(i+i38-1), // + i = 1,ntheta)
   *
   * if (imax < mtheta * 4) { imax = mtheta * 4; itemp = (int * ) realloc(itemp, sizeof(int) * imax); assert (itemp); }
   *
   * repeat = 12; dl = 6; if (new_format) { fget_line(bufer, 256, parm); // %FLAG ANGLES_WITHOUT_HYDROGEN fget_line(bufer, 256,
   * parm); // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM:
   * Cannot parse ANGLES_WITHOUT_HYDROGEN format: %s\n", bufer); return -1; } }
   *
   * nlines = mtheta * 4 % repeat ? mtheta * 4 / repeat + 1 : mtheta * 4 / repeat; if (nlines == 0) { nlines = 1; } count = 1; k =
   * 0; for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = 4 * mtheta - count + 1 >= repeat ? repeat : 4 * mtheta -
   * count + 1; for (j = 0; j < n; j++, k++, count++, count_2++) { strncpy(buf, bufer + j * dl, dl); buf[dl] = 0x00; itemp[k] =
   * atoi(buf); } }
   *
   * for (k = ntheth, i = 0; i < mtheta; i++, k++) { inter13[k].i = itemp[4 * i] / 3 + 1; inter13[k].j = itemp[4 * i + 1] / 3 + 1;
   * inter13[k].k = itemp[4 * i + 2] / 3 + 1; int ref = itemp[4 * i + 3] - 1; if (ref < 0) { printf("\n???: ref < 0 for mtheta: %d",
   * ref); } inter13[k].keq = ff.angleParTable[ref].fc; inter13[k].aeq = ff.angleParTable[ref].ebl; }
   *
   * // --- Test errors = 0; for (i = 0; i < ninter13; i++) { if (inter13[i].i < 1 || inter13[i].i > natoms || inter13[i].j < 1 ||
   * inter13[i].j > natoms || inter13[i].k < 1 || inter13[i].k > natoms) { ++errors; printf("\nangle: %d i: %d j: %d k: %d natoms:
   * %d", i, inter13[i].i, inter13[i].j, inter13[i].k, natoms); } }
   *
   * // --- Allocate torsions
   *
   * nintertor = nphih + nphia; if (intertor != NULL) { delete[] intertor; } intertor = new InterTor[nintertor]; assert (intertor);
   *
   *
   * // fmtin = ifmt // type = 'dihedrals_inc_hydrogen' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt)
   * (ix(i+i40-1),ix(i+i42-1),ix(i+i44-1),ix(i+i46-1), // + ix(i+i48-1),i = 1,nphih)
   *
   * if (5 * nphih > imax) { imax = 5 * nphih; itemp = (int * ) realloc(itemp, sizeof(int) * imax); assert (itemp); }
   *
   * repeat = 12; dl = 6; if (new_format) { fget_line(bufer, 256, parm); // %FLAG DIHEDRALS_INC_HYDROGEN fget_line(bufer, 256,
   * parm); // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM:
   * Cannot parse DIHEDRALS_INC_HYDROGEN format: %s\n", bufer); return -1; } }
   *
   * nlines = nphih * 5 % repeat ? nphih * 5 / repeat + 1 : nphih * 5 / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0;
   * for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = 5 * nphih - count + 1 >= repeat ? repeat : 5 * nphih -
   * count + 1; for (j = 0; j < n; j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00; itemp[k] = atoi(buf); } }
   *
   * for (i = 0; i < nphih; i++) { intertor[i].i = itemp[5 * i] / 3 + 1; intertor[i].j = itemp[5 * i + 1] / 3 + 1; intertor[i].k =
   * abs(itemp[5 * i + 2] / 3) + 1; intertor[i].l = abs(itemp[5 * i + 3] / 3) + 1; //intertor[i].n = (double)(itemp[5*i + 4] - 1);
   * int pos = itemp[5 * i + 4] - 1; if (pos >= ff.nTorsionPar) { printf("\npos >= ff.nTorsionPar"); }
   *
   * if (fabs(ff.torsionParTable[pos].par[0].phase - 0.0) < 0.1) { intertor[i].n = ff.torsionParTable[pos].par[0].per; } else if
   * (fabs(ff.torsionParTable[pos].par[0].phase - PI) < 0.1) { intertor[i].n = -ff.torsionParTable[pos].par[0].per; } else {
   * printf("\n\nDon't know how to work with this phase: %8.3lf\n", ff.torsionParTable[pos].par[0].phase * RAD2DEG); //exit(1);
   * Log_to_GUI("Read_AMBER_PARM: Wrong Phase definition\n"); return -1; }
   *
   * intertor[i].u = ff.torsionParTable[pos].par[0].fc;
   *
   * }
   *
   *
   * // fmtin = ifmt // type = 'dihedrals_without_hydrogen' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt)
   * (ix(i+i50-1),ix(i+i52-1),ix(i+i54-1),ix(i+i56-1), // + ix(i+i58-1),i = 1,nphia)
   *
   * if (5 * nphia > imax) { imax = 5 * nphia; itemp = (int * ) realloc(itemp, sizeof(int) * imax); assert (itemp); }
   *
   * repeat = 12; dl = 6; if (new_format) { fget_line(bufer, 256, parm); // %FLAG DIHEDRALS_WITHOUT_HYDROGEN fget_line(bufer, 256,
   * parm); // %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM:
   * Cannot parse DIHEDRALS_WITHOUT_HYDROGEN format: %s\n", bufer); return -1; } }
   *
   * nlines = nphia * 5 % repeat ? nphia * 5 / repeat + 1 : nphia * 5 / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0;
   * for (i = 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = 5 * nphia - count + 1 >= repeat ? repeat : 5 * nphia -
   * count + 1; for (j = 0; j < n; j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00; itemp[k] = atoi(buf); } }
   *
   * for (i = 0, j = nphih; i < nphia; i++, j++) { intertor[j].i = itemp[5 * i] / 3 + 1; intertor[j].j = itemp[5 * i + 1] / 3 + 1;
   * intertor[j].k = abs(itemp[5 * i + 2] / 3) + 1; intertor[j].l = abs(itemp[5 * i + 3] / 3) + 1; //intertor[i].n =
   * (double)(itemp[5*i + 4] - 1);
   *
   * int pos = itemp[5 * i + 4] - 1; if (pos >= ff.nTorsionPar) { printf("\nHeavy: pos >= ff.nTorsionPar"); }
   *
   * if (fabs(ff.torsionParTable[pos].par[0].phase - 0.0) < 0.1) { intertor[j].n = ff.torsionParTable[pos].par[0].per; } else if
   * (fabs(ff.torsionParTable[pos].par[0].phase - PI) < 0.1) { intertor[j].n = -ff.torsionParTable[pos].par[0].per; } else {
   * printf("\n\nDon't know how to work with this phase: %8.3lf\n", ff.torsionParTable[pos].par[0].phase * RAD2DEG); exit(1); }
   *
   * intertor[j].u = ff.torsionParTable[pos].par[0].fc;
   *
   * }
   *
   * // --- parameter assignment (and Test)
   *
   * errors = 0; for (i = 0; i < nintertor; i++) { //int pos = (int)(intertor[i].n); //if (
   * fabs(ff.torsionParTable[pos].par[0].phase - 0.0 ) < 0.1 ) //	intertor[i].n = ff.torsionParTable[pos].par[0].per; //else if (
   * fabs(ff.torsionParTable[pos].par[0].phase - PI ) < 0.1 ) //	intertor[i].n = -ff.torsionParTable[pos].par[0].per; //else { //
   * printf("\n\nDon't know how to work with this phase: %8.3lf\n", //	ff.torsionParTable[pos].par[0].phase*RAD2DEG); //	exit(1); //
   * } //intertor[i].u = ff.torsionParTable[pos].par[0].fc;
   *
   *
   * //printf("\n%5d %s %s %s %s %s fc: %8.3lf n: %8.3lf", //	i,atom[intertor[i].i].name, atom[intertor[i].j].name,
   * atom[intertor[i].k].name, //	atom[intertor[i].l].name, atom[intertor[i].i].sname,intertor[i].u, intertor[i].n);
   *
   * if (intertor[i].i < 1 || intertor[i].i > natoms || intertor[i].j < 1 || intertor[i].j > natoms || intertor[i].k < 1 ||
   * intertor[i].k > natoms || intertor[i].l < 1 || intertor[i].l > natoms) { ++errors; printf("\ntorsion: %d i: %d j: %d k: %d l:
   * %d natoms: %d", i, intertor[i].i, intertor[i].j, intertor[i].k, intertor[i].l, natoms); } }
   *
   *
   * // fmtin = ifmt // type = 'excluded_atoms_list' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt)
   * (ix(i+i10-1),i=1,nnb)
   *
   *
   * if (nnb > imax) { imax = nnb; itemp = (int * ) realloc(itemp, sizeof(int) * imax); assert (itemp); }
   *
   * repeat = 12; dl = 6; if (new_format) { fget_line(bufer, 256, parm); // %FLAG EXCLUDED_ATOMS_LIST fget_line(bufer, 256, parm);
   * // %FORMAT( if (parse_format(bufer, repeat, dl)) { SimpleExit("Can't parse format", 1); } }
   *
   * nlines = nnb % repeat ? nnb / repeat + 1 : nnb / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i = 0; i <
   * nlines; i++) { fget_line(bufer, 256, parm); int n = nnb - count + 1 >= repeat ? repeat : nnb - count + 1; for (j = 0; j < n;
   * j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00; itemp[k] = atoi(buf); } }
   *
   * // ----- read the h-bond parameters -----
   *
   * // fmtin = rfmt // type = 'hbond_acoef' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt) (asol(i),i=1,nphb)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG HBOND_ACOEF fget_line(bufer, 256, parm); //
   * %FORMAT( if (parse_format(bufer, repeat, dl)) { SimpleExit("Can't parse format", 1); } }
   *
   * if (nphb > rmax) { rmax = nphb; rtemp = (double * ) realloc(rtemp, sizeof(double) * rmax); assert (rtemp); }
   *
   * nlines = nphb % repeat ? nphb / repeat + 1 : nphb / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i = 0; i <
   * nlines; i++) { fget_line(bufer, 256, parm); int n = nphb - count + 1 >= repeat ? repeat : nphb - count + 1; for (j = 0; j < n;
   * j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00; rtemp[k] = atof(buf); } }
   *
   * // // fmtin = rfmt // type = 'hbond_bcoef' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt) (bsol(i),i=1,nphb)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG HBOND_BCOEF fget_line(bufer, 256, parm); //
   * %FORMAT( if (parse_format(bufer, repeat, dl)) { SimpleExit("Can't parse format", 1); } }
   *
   * if (nphb > rmax) { rmax = nphb; rtemp = (double * ) realloc(rtemp, sizeof(double) * rmax); assert (rtemp); }
   *
   * nlines = nphb % repeat ? nphb / repeat + 1 : nphb / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i = 0; i <
   * nlines; i++) { fget_line(bufer, 256, parm); int n = nphb - count + 1 >= repeat ? repeat : nphb - count + 1; for (j = 0; j < n;
   * j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00; rtemp[k] = atof(buf); } }
   *
   * // fmtin = rfmt // type = 'hbcut' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt) (hbcut(i),i=1,nphb)
   *
   * repeat = 5; dl = 16; if (new_format) { fget_line(bufer, 256, parm); // %FLAG HBCUT fget_line(bufer, 256, parm); // %FORMAT( if
   * (parse_format(bufer, repeat, dl)) { SimpleExit("Can't parse format", 1); } }
   *
   * if (nphb > rmax) { rmax = nphb; rtemp = (double * ) realloc(rtemp, sizeof(double) * rmax); assert (rtemp); }
   *
   * nlines = nphb % repeat ? nphb / repeat + 1 : nphb / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i = 0; i <
   * nlines; i++) { fget_line(bufer, 256, parm); int n = nphb - count + 1 >= repeat ? repeat : nphb - count + 1; for (j = 0; j < n;
   * j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00; rtemp[k] = atof(buf); } }
   *
   * // ----- read isymbl,itree,join and irotat arrays ----- // fmtin = afmt // type = 'amber_atom_type' // call nxtsec(nf, 6,
   * 0,fmtin, type, fmt, iok) // read(nf,fmt) (ih(i+m06-1),i=1,natom)
   *
   * repeat = 20; dl = 4; if (new_format) { fget_line(bufer, 256, parm); // %FLAG AMBER_ATOM_TYPE fget_line(bufer, 256, parm); //
   * %FORMAT( if (parse_format(bufer, repeat, dl)) { //SimpleExit("Can't parse format",1); Log_to_GUI( "Read_AMBER_PARM: Cannot
   * parse AMBER_ATOM_TYPE format: %s\n", bufer); return -1; } }
   *
   * nlines = natoms % repeat ? natoms / repeat + 1 : natoms / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i =
   * 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = natoms - count + 1 >= repeat ? repeat : natoms - count + 1; for (j =
   * 0; j < n; j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00; //atom[count].Set_Amber_Name( buf );
   * atom[count].Set_Amber_Name(strtok(buf, " ")); } }
   *
   * // // fmtin = afmt // type = 'tree_chain_classification' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt)
   * (ih(i+m08-1),i=1,natoms)
   *
   * repeat = 20; dl = 4; if (new_format) { fget_line(bufer, 256, parm); // %FLAG TREE_CHAIN_CLASSIFICATION fget_line(bufer, 256,
   * parm); // %FORMAT( if (parse_format(bufer, repeat, dl)) { SimpleExit("Can't parse format", 1); } }
   *
   * nlines = natoms % repeat ? natoms / repeat + 1 : natoms / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i =
   * 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = natoms - count + 1 >= repeat ? repeat : natoms - count + 1; for (j =
   * 0; j < n; j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00;
   *
   * }
   * }
   *
   * // // fmtin = ifmt // type = 'join_array' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt)
   * (ix(i+i64-1),i=1,natom) // repeat = 12; dl = 6; if (new_format) { fget_line(bufer, 256, parm); // %FLAG JOIN_ARRAY
   * fget_line(bufer, 256, parm); // %FORMAT( if (parse_format(bufer, repeat, dl)) { SimpleExit("Can't parse format", 1); } }
   *
   * nlines = natoms % repeat ? natoms / repeat + 1 : natoms / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i =
   * 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = natoms - count + 1 >= repeat ? repeat : natoms - count + 1; for (j =
   * 0; j < n; j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00;
   *
   * }
   * }
   *
   * // // fmtin = ifmt // type = 'irotat' // call nxtsec(nf, 6, 0,fmtin, type, fmt, iok) // read(nf,fmt) (ix(i+i66-1),i=1,natom)
   * // repeat = 12; dl = 6; if (new_format) { fget_line(bufer, 256, parm); // %FLAG IROTAT fget_line(bufer, 256, parm); // %FORMAT(
   * if (parse_format(bufer, repeat, dl)) { SimpleExit("Can't parse format", 1); } }
   *
   * nlines = natoms % repeat ? natoms / repeat + 1 : natoms / repeat; if (nlines == 0) { nlines = 1; } count = 1; k = 0; for (i =
   * 0; i < nlines; i++) { fget_line(bufer, 256, parm); int n = natoms - count + 1 >= repeat ? repeat : natoms - count + 1; for (j =
   * 0; j < n; j++, k++, count++) { strncpy(buf, bufer + dl * j, dl); buf[dl] = 0x00;
   *
   * }
   * }
   *
   * fclose(parm);
   *
   * printf("\n\nVDW Parameters\n"); for (i = 0; i < ntypes; i++) { if (ff.Bij[i][i] == 0.0) { ff.rho[i] = 0.0; ff.eps[i] = 0.0; }
   * else { ff.rho[i] = 0.5 * pow(2.0 * ff.Aij[i][i] / ff.Bij[i][i], 1.0 / 6.0); ff.eps[i] = ff.Aij[i][i] / pow(2.0 * ff.rho[i],
   * 12); }
   *
   * printf("\n%3d: %8.4lf %8.4lf", i + 1, ff.rho[i], ff.eps[i]); for (j = 1; j <= natoms; j++) { if (atom[j].ambtype == i) {
   * printf(" %s", atom[j].amber_name); break; } } }
   *
   * }
   */
}
