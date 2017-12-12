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
package cct.tripos;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.MonomerInterface;
import cct.modelling.GeneralMolecularDataParser;
import cct.modelling.AtomProperties;
import cct.modelling.AtomicSet;
import cct.modelling.AtomicSets;
import cct.modelling.ChemicalElements;
import cct.modelling.Molecule;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import javax.swing.JFileChooser;

/**
 *
 * <p>
 * Title: ParseTriposMol2</p>
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
public class TriposParser extends GeneralMolecularDataParser
    implements AtomProperties {

  public static final Map<String, Color> colorMap = new HashMap<String, Color>();
  private boolean Debug = false;
  private List<RES_INFO> residues = new ArrayList<RES_INFO>();
  private List<MoleculeInfo> molecules = new ArrayList<MoleculeInfo>();

  static {
    colorMap.put("WHITE", Color.WHITE);
    colorMap.put("BLUE", Color.BLUE);
    colorMap.put("RED", Color.RED);
    colorMap.put("YELLOW", Color.YELLOW);
    colorMap.put("ORANGE", Color.ORANGE);
    colorMap.put("CYAN", Color.CYAN);
    colorMap.put("GREEN", Color.GREEN);
    colorMap.put("PURPLE", new Color(160, 32, 240));
    colorMap.put("MAGENTA", Color.MAGENTA);
    colorMap.put("GREENBLUE", Color.CYAN);
  }
  static final Logger logger = Logger.getLogger(TriposParser.class.getCanonicalName());

  public TriposParser() {
  }

  public void setLoggerLevel(Level level) {
    logger.setLevel(level);
  }

  public int getNumberOfMolecules() {
    return molecules.size();
  }

  public int validFormatScore(BufferedReader in) throws Exception {
    TriposParser t = new TriposParser();
    try {
      t.parseData(in);
    } catch (Exception ex) {
      return 0;
    }

    if (t.getMolecule() == null || t.getMolecule().getNumberOfAtoms() < 1) {
      return 0;
    }
    return 10;
  }

  public void parseData(BufferedReader in) throws Exception {
    MoleculeInterface mol = getMoleculeInterface();
    this.addMolecule(mol);
    AtomInterface atom;
    BondInterface bond;
    String line = "", token = "";
    StringTokenizer st;
    AtomicSets sets = new AtomicSets();
    Integer natoms = 0, nbonds = 0, nresidues = 0;
    Float cartes = new Float(0.0f);
    double totalCharge = 0;

    try {
      //BufferedReader in = new BufferedReader(new FileReader(filename));
      boolean read_line_first = true;
      while (true) {

        if (read_line_first) {
          if ((line = in.readLine()) == null) {
            break;
          }
        } else {
          read_line_first = true;
        }

        line = line.trim();

        if (line.compareToIgnoreCase("@<TRIPOS>MOLECULE") == 0) {
          // Read Molecule name
          line = in.readLine();
          //line.trim();
          mol.setName(line);

          // read number of atoms & bonds
          line = in.readLine();
          st = new StringTokenizer(line, " ");

          try {
            natoms = new Integer(st.nextToken());
            nbonds = new Integer(st.nextToken());
            nresidues = new Integer(st.nextToken());
          } catch (NumberFormatException e) {
            break;
          }

        } else if (line.compareToIgnoreCase("@<TRIPOS>ATOM") == 0) {
          //@<TRIPOS>ATOM
          //  1 P           5.9733   -6.8476   27.4300 P.3       1 A1          0.0000 BACKBONE|DICT|DIRECT
          //  2 O1P         6.2573   -8.2786   27.2100 O.co2     1 A1          0.0000 BACKBONE|DICT
          int current_subst_id = 0;

          for (int i = 0; i < natoms.intValue(); i++) {
            if ((line = in.readLine()) == null) {
              break;
            }
            st = new StringTokenizer(line, " ");

            boolean new_residue = false;
            String substr = null;

            st.nextToken(); // skip first token

            atom = mol.getNewAtomInstance(); // gAtom();
            atom.setName(st.nextToken());

            // --- Read in coordinates
            if (st.countTokens() < 3) {
              break; // Actually it's an error
            }

            try {
              atom.setX(Float.parseFloat(st.nextToken()));
              atom.setY(Float.parseFloat(st.nextToken()));
              atom.setZ(Float.parseFloat(st.nextToken()));

            } catch (NumberFormatException e) {
              System.err.println("Error converting cartesin coordinates: " + e.getMessage());
              break;
            }

            if (st.countTokens() > 0) { // Parse Tripos Atom Type
              String triposName = st.nextToken();
              atom.setProperty(AtomInterface.SYBYL_TYPE, triposName);
              atom.setAtomicNumber(triposAtomTypeToElement(triposName));
            }
            // ---
            int subst_id = current_subst_id;
            if (st.countTokens() > 0) { // Parse the ID number of the substructure containing the atom
              try {
                subst_id = Integer.parseInt(st.nextToken());
                --subst_id;
              } catch (NumberFormatException e) {
                System.err.println("Error converting ID number of the substructure containing the atom");
                break;
              }

              if (subst_id < 0) {
                System.err.println("Error in MOL2 file: substructure ID is negative: " + subst_id);
                break;
              }

              if (subst_id > current_subst_id) {
                new_residue = true;
                current_subst_id = subst_id;
              }
            }

            if (st.hasMoreTokens()) { // Parse the name of the substructure containing the atom
              token = st.nextToken();
              substr = "";
              if (token.startsWith("<")) {
                StringTokenizer res = new StringTokenizer(token, "<>");
                if (res.hasMoreTokens()) {
                  substr = res.nextToken();
                }
              } else {
                StringTokenizer res = new StringTokenizer(token, "0123456789");
                if (res.hasMoreTokens()) {
                  substr = res.nextToken();
                }
              }
              if (substr.length() < 1) {
                substr = "RES";
              }
            }

            if (st.hasMoreTokens()) {
              Double charge = 0.0;
              try {
                token = st.nextToken();
                charge = Double.parseDouble(token);
              } catch (Exception ex) {
                System.err.println("Error in MOL2 file: cannot parse atom charge: " + token + " Set it to zero...");
              }
              totalCharge += charge;
              atom.setProperty(AtomInterface.ATOMIC_CHARGE, charge);
            }

            if (Debug) {
              logger.info(atom.getAtomicNumber() + " XYZ: " + atom.getX() + " " + atom.getY() + " " + atom.getZ());
            }
            if (substr == null) {
              substr = "UNK";
            }
            mol.addAtom(atom, subst_id, substr);
          }

          mol.addProperty(MoleculeInterface.ChargeProperty, totalCharge);
          logger.info("Number of atoms: " + mol.getNumberOfAtoms());
          for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
            atom = mol.getAtomInterface(i);
            logger.info(i + " " + atom.getName() + " " + atom.getSubstructureNumber());
          }
          for (int i = 0; i < mol.getNumberOfMonomers(); i++) {
            MonomerInterface m = mol.getMonomerInterface(i);
            logger.info("Res: " + i + " " + m.getName() + " atoms:" + m.getNumberOfAtoms());
          }

        } else if (line.compareToIgnoreCase("@<TRIPOS>BOND") == 0) {

          for (int i = 0; i < nbonds.intValue(); i++) {
            if ((line = in.readLine()) == null) {
              break;
            }
            st = new StringTokenizer(line, " ");

            // Inform about error and continue....
            if (st.countTokens() < 3) {
              logger.info(
                  "Uncomplete BOND record... Continuing...\n");
              continue;
            }

            st.nextToken(); // skip first token

            int a1, a2;
            try {
              a1 = Integer.parseInt(st.nextToken()) - 1;
              a2 = Integer.parseInt(st.nextToken()) - 1;
            } catch (NumberFormatException e) {
              logger.info(
                  "Error converting bonded atoms\n");
              break;
            }

            AtomInterface a_i = mol.getAtomInterface(a1);
            AtomInterface a_j = mol.getAtomInterface(a2);
            bond = mol.getNewBondInstance(a_i, a_j);
            mol.addBond(bond);
            //mol.createNewBond(a_i, a_j);

          }

          logger.info("Number of bonds: " + mol.getNumberOfBonds());

        } else if (false && line.compareToIgnoreCase("@<TRIPOS>SUBSTRUCTURE") == 0) { // Skip for now
          while (true) {
            if ((line = in.readLine()) == null) {
              break;
            }
            line = line.trim();
            if (line.startsWith("@<TRIPOS>") || line.length() < 1) {
              read_line_first = false;
              break;
            }

            st = new StringTokenizer(line, " ");
            if (st.countTokens() < 3) {
              System.err.println("Error reading set in mol2 file: expecting at least 3 token to SUBSTRUCTURE info, got " + line
                  + " Ignored...");
              break;
            }

            st.nextToken(); // Skip first token
            String subst_name = st.nextToken();
            String root_atom = st.nextToken();
            String subst_type = null;
            if (st.hasMoreTokens()) {
              subst_type = st.nextToken();
            }
            String dict_type = null;
            if (st.hasMoreTokens()) {
              dict_type = st.nextToken();
            }
            String chain = null;
            if (st.hasMoreTokens()) {
              chain = st.nextToken();
            }
            String sub_type = null;
            if (st.hasMoreTokens()) {
              sub_type = st.nextToken();
            }
            String inter_bonds = null;
            if (st.hasMoreTokens()) {
              inter_bonds = st.nextToken();
            }
            String status = null;
            if (st.hasMoreTokens()) {
              status = st.nextToken();
            }
            String comment = null;
            if (st.hasMoreTokens()) {
              comment = st.nextToken();
            }

            int root_at = 0;
            try {
              root_at = Integer.parseInt(root_atom) - 1;
            } catch (Exception ex) {
              System.err.println("Error reading set in mol2 file: cannot parse root atom number, got " + line + " Ignored...");
              break;
            }
            RES_INFO res_info;
            if (sub_type == null) {
              st = new StringTokenizer(subst_name, "0123456789");
              if (st.hasMoreTokens()) {
                sub_type = st.nextToken();
              } else {
                System.err.println("Warning: set residue name (possibly non-standard) to" + subst_name);
                sub_type = subst_name;
              }
            }
            res_info = new RES_INFO(root_at, sub_type);
            residues.add(res_info);
          }
        } else if (line.compareToIgnoreCase("@<TRIPOS>SET") == 0) {

          while (true) {
            if ((line = in.readLine()) == null) {
              break;
            }
            line = line.trim();

            if (line.startsWith("@<TRIPOS>") || line.length() < 1) {
              read_line_first = false;
              break;
            }

            st = new StringTokenizer(line, " ");
            if (st.countTokens() < 3) {
              System.err.println("Error reading set in mol2 file: expecting at least 3 token, got " + line + " Ignored...");
              break;
            }

            String set_name = st.nextToken();
            String set_type = st.nextToken();
            String obj_type = st.nextToken();

            if (!set_type.equalsIgnoreCase("STATIC")) {
              logger.info("Problem reading set in mol2 file: currently can handle only STATIC sets. Ignored...");
              break;
            }

            if (!obj_type.equalsIgnoreCase("ATOMS")) {
              logger.info("Problem reading set in mol2 file: currently can handle only ATOMS sets. Ignored...");
              break;
            }

            boolean colorgroup = false;
            Color color = null;
            if (st.hasMoreTokens() && st.nextToken().equalsIgnoreCase("COLORGROUP")) {
              colorgroup = true;
              color = getColor(set_name);
            }

            AtomicSet aset = new AtomicSet(set_name);

            if ((line = in.readLine()) == null) {
              break;
            }
            st = new StringTokenizer(line, " ");
            if (st.countTokens() < 1) {
              System.err.println(
                  "Error reading set in mol2 file: expecting at least 1 token for reading num_members member..., got "
                  + line + " Ignored...");
              break;
            }
            int num_members = 0;
            token = st.nextToken();
            try {
              num_members = Integer.parseInt(token);
            } catch (Exception ex) {
              System.err.println("Error reading set in mol2 file: expecting int value for num_members, got "
                  + token + " Ignored...");
              break;
            }

            if (num_members < 1) {
              logger.info("Reading mol2 file: set " + set_name + " is empty. Ignored...");
              continue;
            }
            int n_read = 0;
            do {
              if (!st.hasMoreTokens()) {
                System.err.println(
                    "Error reading set in mol2 file: unexpected end of set: expecting int value for set member. Ignored...");
                break;
              }

              token = st.nextToken();
              if (token.equals("\\")) {
                if ((line = in.readLine()) == null) {
                  System.err.println(
                      "Error reading set in mol2 file: unexpected end of file while reading set members. Ignored...");
                  break;
                }
                st = new StringTokenizer(line, " ");
                continue;
              }

              int member = 0;
              try {
                member = Integer.parseInt(token);
              } catch (Exception ex) {
                System.err.println(
                    "Error reading set in mol2 file: cannot parse set member number: Expecting int number, got " + token
                    + " Ignored...");
                break;
              }

              --member;
              if (member >= 0 && member < mol.getNumberOfAtoms()) {
                atom = mol.getAtomInterface(member);
                if (colorgroup && color != null) {
                  atom.setProperty(AtomInterface.RGB_COLOR, new Integer[]{color.getRed(), color.getGreen(), color.getBlue()});
                }
                aset.add(atom);
              }

              ++n_read;
              if (n_read == num_members && aset.size() > 0) {
                sets.put(aset.getName(), aset);
              }
            } while (n_read < num_members);
            if (n_read != num_members) {
              break;
            }
          }
        }

      } // --- End of while

    } catch (IOException e) {
      logger.info("parseMol2File: " + e.getMessage() + "\n");
      return;
    }

    if (sets.size() > 0) {
      Map props = mol.getProperties();
      props.put(MoleculeInterface.AtomicSets, sets);
    }
  }

  /**
   *
   * @param filename
   * @param mol
   * @throws Exception
   * @deprecated Use parseData methods instead
   */
  @Deprecated
  public void parseMol2File(String filename, MoleculeInterface mol) throws Exception {
    BufferedReader in = new BufferedReader(new FileReader(filename));
    parseMol2File(in, mol);
    try {
      in.close();
    } catch (Exception ex) {
      logger.warning("Warning: Error closing file " + filename + " : " + ex.getMessage());
    }
  }

  /**
   *
   * @param in
   * @param mol
   * @throws Exception
   * @deprecated Use parseData methods instead
   */
  @Deprecated
  public void parseMol2File(BufferedReader in, MoleculeInterface mol) throws Exception {
    parseData(in);
  }

  /**
   * Saves molecule in the Tripos mol2 format
   *
   * @param m MoleculeInterface
   * @param filename String
   * @throws Exception
   */
  public static void saveTriposMol2File(MoleculeInterface m, String filename) throws
      Exception {

    BufferedWriter out;
    try {
      out = new BufferedWriter(new FileWriter(filename));
    } catch (java.io.FileNotFoundException e) {
      System.err.println(filename + " not found");
      throw new Exception("saveTriposMol2File: ERROR: " + e.getMessage());
      //return;
    } catch (SecurityException e) {
      System.err.println(filename + ": " + e.getMessage());
      throw new Exception("saveTriposMol2File: ERROR: " + e.getMessage());
      //return;
    } catch (IOException e) {
      System.err.println(filename + ": " + e.getMessage());
      throw new Exception("saveTriposMol2File: ERROR: " + e.getMessage());
      //return;
    }

    try {
      out.write("@<TRIPOS>MOLECULE");
      out.write("\n");

      // mol_name (all strings on the line) = the name of the molecule.
      out.write(m.getName());
      out.write("\n");

      // num_atoms [num_bonds [num_subst [num_feat[num_sets]]]]
      //  num_atoms (integer) = the number of atoms in the molecule.
      //  num_bonds (integer) = the number of bonds in the molecule.
      //  num_subst (integer) = the number of substructures in the molecule.
      //  num_feat (integer) = the number of features in the molecule.
      //  num_sets (integer) = the number of sets in the molecule.
      out.write(String.format("%5d %5d %5d %5d %5d\n", m.getNumberOfAtoms(), m.getNumberOfBonds(), m.getNumberOfMonomers(), 0, 0));
      out.write("\n");

      // mol_type (string) = the molecule type: SMALL, BIOPOLYMER,
      // PROTEIN, NUCLEIC_ACID, SACCHARIDE
      out.write("SMALL");
      out.write("\n");

      // charge_type (string) = the type of charges associated with the molecule:
      // NO_CHARGES, DEL_RE, GASTEIGER, GAST_HUCK, HUCKEL,
      // PULLMAN, GAUSS80_CHARGES, AMPAC_CHARGES,
      // MULLIKEN_CHARGES, DICT_ CHARGES, MMFF94_CHARGES,
      // USER_CHARGES
      boolean no_charges = true;
      for (int i = 0; i < m.getNumberOfAtoms(); i++) {
        AtomInterface atom = m.getAtomInterface(i);
        Object charge = atom.getProperty(AtomInterface.ATOMIC_CHARGE);
        if (charge != null) {
          no_charges = false;
          break;
        }
      }

      if (no_charges) {
        out.write("NO_CHARGES");
      } else {
        out.write("USER_CHARGES");
      }
      out.write("\n");

      // status_bits (string) = the internal SYBYL status bits associated with the
      // molecule. These should never be set by the user. Valid status bits are
      // system, invalid_charges, analyzed, substituted, altered or ref_angle.
      out.write("\n");

      // mol_comment (all strings on data line) = the comment associated with the molecule.
      out.write("\n");

      // Writing atoms
      // atom_id atom_name x y z atom_type [subst_id [subst_name [charge [status_bit]]]]
      // � atom_id (integer) = the ID number of the atom at the time the file was
      // created. This is provided for reference only and is not used when the
      // .mol2 file is read into SYBYL.
      // � atom_name (string) = the name of the atom.
      // � x (real) = the x coordinate of the atom.
      // � y (real) = the y coordinate of the atom.
      // � z (real) = the z coordinate of the atom.
      // � atom_type (string) = the SYBYL atom type for the atom.
      // � subst_id (integer) = the ID number of the substructure containing the atom.
      // � subst_name (string) = the name of the substructure containing the atom.
      // � charge (real) = the charge associated with the atom.
      // � status_bit (string) = the internal SYBYL status bits associated with the
      // atom. These should never be set by the user. Valid status bits are
      // DSPMOD, TYPECOL, CAP, BACKBONE, DICT, ESSENTIAL, WATER and
      // DIRECT.
      // Example:
      // 1 CA -0.149 0.299 0.000 C.3 1 ALA1 0.000 BACKBONE|DICT|DIRECT
      // 1 CA -0.149 0.299 0.000 C.3
      // In the first example the atom has ID number 1. It is named CA and is
      // located at (-0.149, 0.299, 0.000). Its atom type is C.3. It belongs to the
      // substructure with ID 1 which is named ALA1. The charge associated with
      // the atom is 0.000 and the SYBYL status bits associated with the atom are
      // BACKBONE, DICT, and DIRECT. Example two is the minimal information
      // necessary for the MOL2 command to create an atom.
      out.write("@<TRIPOS>ATOM");
      out.write("\n");

      Object obj;
      String str;
      for (int i = 0; i < m.getNumberOfAtoms(); i++) {
        AtomInterface a = m.getAtomInterface(i);

        str = a.getName();
        if (str == null || str.length() == 0) {
          str = "UNK";
        }

        out.write(String.format("%5d %-5s", (i + 1), str));

        // --- Writing X,Y & Z
        out.write(String.format(" %9.4f %9.4f %9.4f", a.getX(), a.getY(), a.getZ()));

        // --- Writing Tripos atom type
        str = (String) a.getProperty(AtomInterface.SYBYL_TYPE);
        if (str == null || str.length() == 0) {
          str = Tripos.guessTriposAtomType(a);
        }
        out.write(String.format(" %-5s", str));

        // --- Writing substructure number
        int substr = a.getSubstructureNumber();
        out.write(String.format(" %5d", (substr + 1)));

        MonomerInterface mono = m.getMonomerInterface(substr);
        str = mono.getName();
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null")) {
          str = "XXX";
        }

        out.write(String.format(" %-5s", str));

        // --- Writing atomic charge (if any)
        if (!no_charges) {
          float charge = 0;
          obj = a.getProperty(AtomInterface.ATOMIC_CHARGE);
          if (obj != null) {
            if (obj instanceof Float) {
              Float f = (Float) obj;
              charge = f.floatValue();
            } else if (obj instanceof Double) {
              Double d = (Double) obj;
              charge = d.floatValue();
            } else {
              System.err.println(
                  "saveTriposMol2File: Warning: atomic charge is not Float or Double: "
                  + obj.getClass().getCanonicalName());
              System.err.println("  Action: Ignored: set to zero");
            }
          }
          out.write(String.format(" %9.4f", charge));
        }

        out.write("\n");
      }

      // --- Writing bonds
      // bond_id origin_atom_id target_atom_id bond_type [status_bits]
      // � bond_id (integer) = the ID number of the bond at the time the file was
      //   created. This is provided for reference only and is not used when the
      //   .mol2 file is read into SYBYL.
      // � origin_atom_id (integer) = the ID number of the atom at one end of the bond.
      // � target_atom_id (integer) = the ID number of the atom at the other end of the bond.
      // � bond_type (string) = the SYBYL bond type (see below).
      // � status_bits (string) = the internal SYBYL status bits associated with the
      // bond. These should never be set by the user. Valid status bit values are
      // TYPECOL, GROUP, CAP, BACKBONE, DICT and INTERRES.
      // Bond Types:
      // � 1 = single
      // � 2 = double
      // � 3 = triple
      // � am = amide
      // � ar = aromatic
      // � du = dummy
      // � un = unknown (cannot be determined from the parameter tables)
      // � nc = not connected
      // Example:
      // 5 4 9 am BACKBONE|DICT|INTERRES
      // 5 4 9 am
      // The bond in the first example has ID number 5 and connects atoms 4 and 9.
      // It is an amide bond. The status bits indicate the bond is part of the backbone
      // chain, joins two residues, and that a dictionary was used when creating the
      // molecule. The second example is a minimal representation of the same bond.
      if (m.getNumberOfBonds() > 0) {
        out.write("@<TRIPOS>BOND");
        out.write("\n");
      }

      for (int i = 0; i < m.getNumberOfBonds(); i++) {
        BondInterface bond = m.getBondInterface(i);
        AtomInterface a_i = bond.getIAtomInterface();
        AtomInterface a_j = bond.getJAtomInterface();

        out.write(String.format("%6d %5d %5d un", (i + 1), (m.getAtomIndex(a_i) + 1), (m.getAtomIndex(a_j) + 1)));
        out.write("\n");
      }

      // --- Writing substructure
      // subst_id subst_name root_atom [subst_type [dict_type[chain [sub_type [inter_bonds [status[comment]]]]]]]
      // subst_id (integer) = the ID number of the substructure. This is provided
      //   for reference only and is not used by the MOL2 command when reading the file.
      // subst_name (string) = the name of the substructure.
      // root_atom (integer) = the ID number of the substructure�s root atom.
      // subst_type - string) = the substructure type: temp, perm, residue, group or domain.
      // dict_type (integer) = the type of dictionary associated with the substructure.
      // chain (string) = the chain to which the substructure belongs (� 4 chars).
      // sub_type (string) = the subtype of the chain.
      // inter_bonds (integer) = the number of inter substructure bonds.
      // status (string) = the internal SYBYL status bits. These should never be
      //   set by the user. Valid status bit values are LEAF, ROOT, TYPECOL, DICT,
      //   BACKWARD and BLOCK.
      // comment (remaining strings on data line) = the comment for the substructure.
      // Example:
      // 1 ALA1 1 RESIDUE 1 A ALA 1 ROOT|DICT Comment here
      // The substructure has 1 as ID, ALA1 as name and atom 1 as root atom. It is
      // of type RESIDUE and the associated dictionary type is 1 (protein). It is part
      // of the A chain in the molecule and it is an ALAnine. There is only one inter
      // substructure bonds. The SYBYL status bits indicate it is the ROOT
      // substructure of the chain and it came from a dictionary. The comment reads
      // �Comment here�.
      // 1 ALA1 1
      // Minimal representation of a substructure.
      out.write("@<TRIPOS>SUBSTRUCTURE");
      out.write("\n");

      for (int i = 0; i < m.getNumberOfMonomers(); i++) {
        MonomerInterface mono = m.getMonomerInterface(i);
        str = mono.getName();
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null")) {
          str = "XXX";
        }

        out.write((i + 1) + " " + str);

        if (mono.getNumberOfAtoms() < 1) {
          out.write(" 1\n");
          System.err.println("saveTriposMol2File: Warning: residue # " + (i + 1) + " " + str + " has no atoms");
        } else {
          AtomInterface a = mono.getAtom(0);
          out.write(" " + (m.getAtomIndex(a) + 1) + "\n");
        }
      }

      Molecule.validateSets(m);
      obj = m.getProperty(MoleculeInterface.AtomicSets);
      if (obj != null && obj instanceof AtomicSets) {
        out.write("@<TRIPOS>SET\n");
        AtomicSets sets = (AtomicSets) obj;
        Iterator iter = sets.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry me = (Map.Entry) iter.next();
          obj = me.getValue();
          if (!(obj instanceof AtomicSet)) {
            continue;
          }
          AtomicSet aset = (AtomicSet) obj;
          if (aset.size() < 1) {
            continue;
          }
          out.write(aset.getName() + " STATIC   ATOMS   <user>   ****\n" + aset.size());
          int index, count = 0;
          for (int i = 0; i < aset.size(); i++) {
            AtomInterface atom = aset.get(i);
            index = m.getAtomIndex(atom) + 1;
            out.write(" " + index);
            ++count;
            if ((count % 20) == 0 && i < aset.size() - 1) {
              out.write(" \\\n");
            }
          }
          out.write("\n");
        }
      }

      out.close();
    } catch (IOException e) {
      System.err.println("Error writing into " + filename);
      throw new Exception("saveTriposMol2File: ERROR: " + e.getMessage());
      //return;
    }

  }

  public static Color getColor(String name) {
    StringTokenizer st = new StringTokenizer(name, "$");
    if (st.countTokens() != 2) {
      return null;
    }
    st.nextElement();
    String color = st.nextToken();
    return colorMap.get(color);
  }

  private void processResidueInfo(MoleculeInterface mol, List<RES_INFO> res) {
    if (mol.getNumberOfAtoms() < 1 || res.size() < 1) {
      return;
    }
  }

  public int parseMultipleMolecules(String fileName) throws Exception {
    File file = new File(fileName);
    return parseMultipleMolecules(file);
  }

  public int parseMultipleMolecules(File file) throws Exception {
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    String line;
    long pointer = 0L;
    boolean found = false;
    while (true) {
      pointer = raf.getFilePointer();
      line = raf.readLine();
      if (line == null) {
        break;
      }
      line = line.trim();

      if (line.compareToIgnoreCase("@<TRIPOS>MOLECULE") == 0) {
        // Read Molecule name
        line = raf.readLine();
        if (line == null) {
          System.err.println("Warning: unexpected eof while reading a molecule name");
          break;
        }
        line = line.trim();
        MoleculeInfo mol = new MoleculeInfo(line, pointer);
        molecules.add(mol);
      }

    }

    raf.close();
    return molecules.size();
  }

  public void splitIntoMultipleFiles(String fileName, boolean overwrite) throws Exception {
    File file = new File(fileName);
    splitIntoMultipleFiles(file, overwrite);
  }

  public void splitIntoMultipleFiles(File file, boolean overwrite) throws Exception {
    if (molecules.size() < 2) {
      System.err.println("Nothing to split: Number of molecules in file - " + molecules.size());
      return;
    }

    String line;
    boolean found = false;
    int count = 0;
    BufferedWriter out = null;
    BufferedReader in = new BufferedReader(new FileReader(file));
    try {
      while ((line = in.readLine()) != null) {
        line = line.trim();
        if (line.compareToIgnoreCase("@<TRIPOS>MOLECULE") == 0) {
          if (found) {
            out.close();
          }
          found = true;
          ++count;
          out = new BufferedWriter(new FileWriter(String.format("%06d.mol2", count)));
          out.write(line + "\n");
        } else if (found) {
          out.write(line + "\n");
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    if (out != null) {
      out.close();
    }
    in.close();
  }

  public static void main(String[] args) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(true);
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    int result = fileChooser.showOpenDialog(null);

    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }

    File[] files = fileChooser.getSelectedFiles();
    if (files.length < 1) {
      System.err.println("No files selected");
      return;
    }
    System.out.println(files.length + " files selected");
    FilesComparator fc = new FilesComparator();
    Arrays.sort(files, fc);
    //
    File currentDir = fileChooser.getCurrentDirectory();
    System.out.println("Current working directory: " + currentDir.getAbsolutePath());
    System.setProperty("user.dir", fileChooser.getCurrentDirectory().getAbsolutePath());

    for (int i = 0; i < files.length; i++) {
      TriposParser tp = new TriposParser();
      int n = 0;
      try {
        n = tp.parseMultipleMolecules(files[i]);
        tp.splitIntoMultipleFiles(files[i], true);
      } catch (Exception ex) {
        Logger.getLogger(TriposParser.class.getName()).log(Level.SEVERE, null, ex);
      }
      System.out.println("Number of molecules in file: " + n);
    }

    System.exit(0);
  }

//**************************************************************************
  public static int triposAtomTypeToElement(String atomType) {
    StringTokenizer tok = new StringTokenizer(atomType, " .");
    return ChemicalElements.getAtomicNumber(tok.nextToken());

  }

  class RES_INFO {

    int root_atom;
    String sub_type;

    public RES_INFO(int root_atom, String sub_type) {
      this.root_atom = root_atom;
      this.sub_type = sub_type;
    }
  }

  class MoleculeInfo {

    String name;
    long pointer;

    public MoleculeInfo(String name, long pointer) {
      this.name = name;
      this.pointer = pointer;
    }
  }

}

class FilesComparator implements Comparator<File> {

  public int compare(File o1, File o2) {
    return o1.getName().compareTo(o2.getName());
  }

  public boolean equals(Object obj) {
    return this == obj;
  }

}
