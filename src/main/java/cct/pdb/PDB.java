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
package cct.pdb;

import cct.interfaces.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import java.util.zip.GZIPInputStream;

import cct.modelling.ChemicalElements;
import cct.tools.UncompressInputStream;
import java.io.*;

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
public class PDB
        implements CoordinateBuilderInterface, CoordinateParserInterface {

  static boolean Debug = false;
  static final Logger logger = Logger.getLogger(PDB.class.getCanonicalName());

  public PDB() {
  }

  public static MoleculeInterface parsePDBFile(URL url, MoleculeInterface mol) throws
          Exception {

    boolean Zcompressed = false;
    boolean gzipCompressed = false;
    if (url.getFile().endsWith(".Z")) {
      Zcompressed = true;
    } else if (url.getFile().endsWith(".gz")) {
      gzipCompressed = true;
    }

    try {
      if (Zcompressed) {
        UncompressInputStream uis = new UncompressInputStream(url.openStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(uis));
        return parsePDBFile(in, mol);
      } else if (gzipCompressed) {
        GZIPInputStream gzip = new GZIPInputStream(url.openStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(gzip));
        return parsePDBFile(in, mol);
      } else {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        return parsePDBFile(in, mol);
      }
    } catch (Exception ex) {
      throw new Exception("Error opening url " + url.toString() + " : "
              + ex.getMessage());
    }
  }

  public static MoleculeInterface parsePDBFile(String filename, MoleculeInterface mol) throws Exception {
    boolean Zcompressed = false;
    if (filename.endsWith(".Z")) {
      Zcompressed = true;
    }
    try {
      if (Zcompressed) {
        UncompressInputStream uis = new UncompressInputStream(new FileInputStream(filename));
        BufferedReader in = new BufferedReader(new InputStreamReader(uis));
        MoleculeInterface m = parsePDBFile(in, mol);
        try {
          in.close();
        } catch (Exception ex) {
        }
        try {
          uis.close();
        } catch (Exception ex) {
        }
        return m;
      } else {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        MoleculeInterface m = parsePDBFile(in, mol);
        try {
          in.close();
        } catch (Exception ex) {
        }
        return m;
      }
    } catch (Exception ex) {
      throw new Exception("Error opening PDB File: " + filename + " : " + ex.getMessage());
    }
  }

  public static MoleculeInterface parsePDBFile(BufferedReader in, MoleculeInterface mol) throws Exception {
    //Molecule mol = null;
    //mol = null;
    AtomInterface atom;
    BondInterface bond;
    String line, bufer;
    StringTokenizer st;
    List serialNumbers = new ArrayList(); // Temporary array
    Integer serialN;
    Float cartes = new Float(0.0f);
    int current_residue = 0;

    try {
      //BufferedReader in = new BufferedReader(new FileReader(filename));
      //mol = new Molecule();
      //mol = mol.getInstance();
      while ((line = in.readLine()) != null) {
        line = line.trim();
        /*
         * COLUMNS DATA TYPE FIELD DEFINITION --------------------------------------------------------------------------------- 1 -
         * 6 Record name "ATOM " 7 - 11 Integer serial Atom serial number. 13 - 16 Atom name Atom name. 17 Character altLoc
         * Alternate location indicator. 18 - 20 Residue name resName Residue name. 22 Character chainID Chain identifier. 23 - 26
         * Integer resSeq Residue sequence number. 27 AChar iCode Code for insertion of residues. 31 - 38 Real(8.3) x Orthogonal
         * coordinates for X in Angstroms. 39 - 46 Real(8.3) y Orthogonal coordinates for Y in Angstroms. 47 - 54 Real(8.3) z
         * Orthogonal coordinates for Z in Angstroms. 55 - 60 Real(6.2) occupancy Occupancy. 61 - 66 Real(6.2) tempFactor
         * Temperature factor. 73 - 76 LString(4) segID Segment identifier, left-justified. 77 - 78 LString(2) element Element
         * symbol, right-justified. 79 - 80 LString(2) charge Charge on the atom.
         *
         *
         * COLUMNS DATA TYPE FIELD DEFINITION -------------------------------------------------------------------------------- 1 - 6
         * Record name "HETATM" 7 - 11 Integer serial Atom serial number. 13 - 16 Atom name Atom name. 17 Character altLoc Alternate
         * location indicator. 18 - 20 Residue name resName Residue name. 22 Character chainID Chain identifier. 23 - 26 Integer
         * resSeq Residue sequence number. 27 AChar iCode Code for insertion of residues. 31 - 38 Real(8.3) x Orthogonal coordinates
         * for X. 39 - 46 Real(8.3) y Orthogonal coordinates for Y. 47 - 54 Real(8.3) z Orthogonal coordinates for Z. 55 - 60
         * Real(6.2) occupancy Occupancy. 61 - 66 Real(6.2) tempFactor Temperature factor. 73 - 76 LString(4) segID Segment
         * identifier; left-justified. 77 - 78 LString(2) element Element symbol; right-justified. 79 - 80 LString(2) charge Charge
         * on the atom.
         */

        /*
         * Example: 1 2 3 4 5 6 7 8 12345678901234567890123456789012345678901234567890123456789012345678901234567890 ATOM 145 N VAL
         * A 25 32.433 16.336 57.540 1.00 11.92 N ATOM 146 CA VAL A 25 31.132 16.439 58.160 1.00 11.85 C ATOM 147 C VAL A 25 30.447
         * 15.105 58.363 1.00 12.34 C ATOM 148 O VAL A 25 29.520 15.059 59.174 1.00 15.65 O ATOM 149 CB AVAL A 25 30.385 17.437
         * 57.230 0.28 13.88 C ATOM 150 CB BVAL A 25 30.166 17.399 57.373 0.72 15.41 C ATOM 151 CG1AVAL A 25 28.870 17.401 57.336
         * 0.28 12.64 C ATOM 152 CG1BVAL A 25 30.805 18.788 57.449 0.72 15.11 C ATOM 153 CG2AVAL A 25 30.835 18.826 57.661 0.28
         * 13.58 C ATOM 154 CG2BVAL A 25 29.909 16.996 55.922 0.72 13.25 C
         */

        if (line.startsWith("ATOM  ") || line.startsWith("HETATM")) {

          //a = new gAtom();
          atom = mol.getNewAtomInstance();
          if (Debug) {
            System.out.print("Atom: " + (mol.getNumberOfAtoms() + 1));
          }

          // --- Parsing atom serial number

          bufer = line.substring(6, 11);
          try {
            serialN = new Integer(bufer);
            serialNumbers.add(serialN);
          } catch (NumberFormatException e) {
            serialNumbers.add(new Integer(0));
            // Just skip it
          }

          // --- Parse Atom name
          boolean first_blank = false;
          String atom_name = line.substring(12, 16);
          if (atom_name.startsWith(" ")) {
            first_blank = true;
          }
          String element = line.substring(12, 13).trim();
          atom_name = atom_name.trim();
          if (!first_blank && atom_name.matches("[0-9].*")) {
            atom_name = atom_name.substring(1) + atom_name.substring(0, 1);
            first_blank = true;
          }
          atom.setName(atom_name);
          atom.setProperty(AtomInterface.NAME, atom_name);
          if (Debug) {
            System.out.print(" Name: " + atom_name);
          }

          // Parse Alternate location indicator. (to be done)

          // Parse Residue name

          String res_name = line.substring(17, 20);
          res_name = res_name.trim();
          if (Debug) {
            System.out.print(" Res: " + res_name);
          }

          if (line.length() < 78) { // We need to deduct element from atom name or using dictionary
            int elem = guessElement(atom_name, first_blank);
            atom.setAtomicNumber(elem);
          }

          // Parse Chain identifier.

          String chainID = line.substring(21, 22);

          // Parse Residue sequence number
          // 23 - 26        Integer         resSeq        Residue sequence number.

          bufer = line.substring(22, 26);
          bufer = bufer.trim();

          int resSeq;
          try {
            resSeq = Integer.parseInt(bufer); // Base of 1
            --resSeq; // Switch to base of 0
          } catch (NumberFormatException e) {
            System.err.println(
                    "Error converting parsing Residue sequence number");
            break;
          }
          if (Debug) {
            System.out.print(" resSeq: " + resSeq);
          }

          if (mol.getNumberOfAtoms() == 0) {
            current_residue = resSeq;
          }

          // Parse Code for insertion of residues (to be done)
          // 27             AChar           iCode         Code for insertion of residues.

          // Parse rthogonal coordinates for X
          // 31 - 38        Real(8.3)       x              Orthogonal coordinates for X

          bufer = line.substring(30, 38);
          try {
            atom.setX(Float.parseFloat(bufer));
          } catch (NumberFormatException e) {
            throw new Exception("Error converting X-coordinate : " + bufer);
            //System.err.println("Error converting X-coordinate");
            //break;
          }

          // Parse Orthogonal coordinates for Y
          // 39 - 46        Real(8.3)       y             Orthogonal coordinates for Y

          bufer = line.substring(38, 46);
          try {
            atom.setY(Float.parseFloat(bufer));
          } catch (NumberFormatException e) {
            throw new Exception("Error converting Y-coordinate : " + bufer);
            //System.err.println("Error converting Y-coordinate");
            //break;
          }

          // Parse Orthogonal coordinates for Z
          // 47 - 54        Real(8.3)       z             Orthogonal coordinates for Z

          bufer = line.substring(46, 54);
          try {
            atom.setZ(Float.parseFloat(bufer));
          } catch (NumberFormatException e) {
            throw new Exception("Error converting Z-coordinate : " + bufer);
            //System.err.println("Error converting Z-coordinate");
            //break;
          }

          // Parse Occupancy (to be done)
          // 55 - 60        Real(6.2)       occupancy     Occupancy.

          // Parse Temperature factor (to be done)
          // 61 - 66        Real(6.2)       tempFactor     Temperature factor.

          // Parse Segment identifier (to be done)
          // 73 - 76        LString(4)      segID          Segment identifier;

          // Parse Element symbol
          // 77 - 78        LString(2)      element        Element symbol; right-justified

          try {
            bufer = line.substring(76, 78);
            bufer = bufer.trim();
            int elem = ChemicalElements.getAtomicNumber(bufer);
            if (elem == 0) {
              elem = guessElement(atom_name, first_blank);
            }
            atom.setAtomicNumber(elem);
            if (Debug) {
              System.out.print(" elem: " + bufer + " " + element);
            }
          } catch (IndexOutOfBoundsException e) {
            // try to guess from the atom name
            try {
              if (element.matches("\\d.")) {
                atom_name = atom_name.substring(1)
                        + atom_name.substring(0, 1);
                atom.setName(atom_name);
              }

            } catch (PatternSyntaxException ex) {
              System.err.println(ex.getMessage());
              atom.setAtomicNumber(guessElement(atom_name, first_blank));
            }

            // --- just do nothing
          }

          // Parse Charge on the atom
          // 79 - 80        LString(2)      charge         Charge on the atom.

          try {
            bufer = line.substring(78, 80);
            try {
              float charge = Float.parseFloat(bufer);
              atom.setProperty(AtomInterface.ATOMIC_CHARGE,
                      new Float(charge));
            } catch (NumberFormatException e) {
              // Do nothing
            }

          } catch (IndexOutOfBoundsException e) {
            // --- just do nothing
          }

          if (Debug) {
            logger.info(" ");
          }

          // Resolving residue information

          if (mol.getNumberOfMonomers() == 0) {
            //mol.addAtom(a, res_name);
            mol.addMonomer(res_name);
            mol.addAtom(atom);
          } else if (current_residue != resSeq) { // New residue
            int n = mol.getNumberOfMonomers() - 1;
            mol.guessCovalentBondsInMonomer(n);
            if (n > 0) {
              mol.guessCovalentBondsBetweenMonomers(n - 1, n);
            }
            //mol.addAtom(a, res_name);
            mol.addMonomer(res_name);
            mol.addAtom(atom);
            current_residue = resSeq;
          } else {
            mol.addAtom(atom);
          }

        } /*
         * COLUMNS DATA TYPE FIELD DEFINITION --------------------------------------------------------------------------------- 1 -
         * 6 Record name "CONECT" 7 - 11 Integer serial Atom serial number 12 - 16 Integer serial Serial number of bonded atom 17 -
         * 21 Integer serial Serial number of bonded atom 22 - 26 Integer serial Serial number of bonded atom 27 - 31 Integer serial
         * Serial number of bonded atom 32 - 36 Integer serial Serial number of hydrogen bonded atom 37 - 41 Integer serial Serial
         * number of hydrogen bonded atom 42 - 46 Integer serial Serial number of salt bridged atom 47 - 51 Integer serial Serial
         * number of hydrogen bonded atom 52 - 56 Integer serial Serial number of hydrogen bonded atom 57 - 61 Integer serial Serial
         * number of salt bridged atom
         */ else if (line.startsWith("CONECT")) {
        }

      } // --- End of while

      mol.guessCovalentBondsInMonomer(mol.getNumberOfMonomers() - 1);

      if (Debug) {
        logger.info("Number of atoms : "
                + mol.getNumberOfAtoms());
        for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
          atom = mol.getAtomInterface(i);
          logger.info(i + " " + atom.getName() + " "
                  + atom.getSubstructureNumber());
        }
        for (int i = 0; i < mol.getNumberOfMonomers(); i++) {
          MonomerInterface m = mol.getMonomerInterface(i);
          logger.info("Res: " + i + " " + m.getName()
                  + " atoms:" + m.getNumberOfAtoms());
        }
      }
    } catch (IOException e) {
      throw new Exception("I/O Error : " + e.getMessage());
      //System.err.println("parsePDBFile: " + e.getMessage() + "\n");
      //return mol;
    }

    return mol;
  }

  /**
   * @param mol
   * @param filename
   *
   */
  @Deprecated
  public static void saveAsPDBFile(MoleculeInterface mol, String filename) {

    FileWriter out = null;
    try {
      out = new FileWriter(filename);
    } catch (FileNotFoundException e) {
      System.err.println(filename + " not found\n");
      return;
    } catch (SecurityException e) {
      System.err.println(filename + ": Security Eception\n");
      return;
    } catch (IOException e) {
      System.err.println(filename + ": Read-only file\n");
      return;
    }

    try {
      savePDB(mol, out);
    } catch (Exception ex) {
      System.err.println("Error saving PDB file " + filename + " : " + ex.getMessage() + "\n");
      return;
    }
  }

  static int guessElement(String name, boolean first_blank) {
    if (first_blank || name.length() == 1) {
      return ChemicalElements.getAtomicNumber(name.substring(0, 1));
    } else {
      if (name.matches(".{1}[0-9]{1}.*")) {
        return ChemicalElements.getAtomicNumber(name.substring(0, 1));
      } else {
        return ChemicalElements.getAtomicNumber(name.substring(0, 2));
      }
    }
  }

  /**
   * Update coordinates of atom from PDB file
   *
   * @param molecule MoleculeInterface
   * @param file_name String
   * @throws Exception
   */
  public static void updateCoordFromPDB(MoleculeInterface molecule,
          String file_name) throws Exception {
    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      throw new Exception(
              "Error updating coordinates: Reference molecule has no atoms");
    }

    String line;
    int natoms = 0;
    BufferedReader in;
    float xyz[][] = new float[molecule.getNumberOfAtoms()][3];

    try {
      in = new BufferedReader(new FileReader(file_name));

      while ((line = in.readLine()) != null) {
        line = line.toUpperCase();
        if (line.startsWith("ATOM  ") || line.startsWith("HETATM")) {

          if (natoms >= molecule.getNumberOfAtoms()) {
            xyz = null;
            in.close();
            throw new Exception(
                    "Error: PDB file contains more atoms than a molecule");
          }

          if (line.length() < 54) {
            throw new Exception(
                    "Error: uncomplete line in PDB file: " + line);
          }

          // Parse rthogonal coordinates for X, Y, Z
          // 31 - 38        Real(8.3)       x              Orthogonal coordinates for X

          try {
            xyz[natoms][0] = Float.parseFloat(line.substring(30, 38).trim());
          } catch (NumberFormatException e) {
            throw new Exception("Error converting X-coordinate: "
                    + e.getMessage());
          }

          // Parse Orthogonal coordinates for Y
          // 39 - 46        Real(8.3)       y             Orthogonal coordinates for Y

          try {
            xyz[natoms][1] = Float.parseFloat(line.substring(38, 46).trim());
          } catch (NumberFormatException e) {
            throw new Exception("Error converting Y-coordinate: "
                    + e.getMessage());
          }

          // Parse Orthogonal coordinates for Z
          // 47 - 54        Real(8.3)       z             Orthogonal coordinates for Z

          try {
            xyz[natoms][2] = Float.parseFloat(line.substring(46, 54).trim());
          } catch (NumberFormatException e) {
            throw new Exception("Error converting Z-coordinate: "
                    + e.getMessage());
          }

          ++natoms;
        }
      }

      if (natoms != molecule.getNumberOfAtoms()) {
        xyz = null;
        in.close();
        throw new Exception(
                "Error: PDB file does not contain the same number of atoms ("
                + natoms + ") as a molecule: " + molecule.getNumberOfAtoms());
      }

      for (int i = 0; i < natoms; i++) {
        AtomInterface atom = molecule.getAtomInterface(i);
        atom.setXYZ(xyz[i][0], xyz[i][1], xyz[i][2]);
      }

      xyz = null;
      in.close();
    } catch (Exception ex) {
      xyz = null;
      throw new Exception("Error updating coordinates from PDB file: "
              + ex.getMessage());
    }

  }

  @Override
  public void parseCoordinates(BufferedReader in, MoleculeInterface molecule) throws Exception {

    try {
      parsePDBFile(in, molecule);
    } catch (Exception ex) {
      throw ex;
    }

  }

  @Override
  public double evaluateCompliance(BufferedReader in) throws Exception {
    double score = 0;
    try {
      String line;
      while ((line = in.readLine()) != null) {

        if (!line.startsWith("ATOM  ") && !line.startsWith("HETATM")) {
          continue;
        }

        score = 1;

        if (line.length() < 54) { // Minimum line length
          return 0;
        }

        // --- Getting x,y,z

        try {
          Float.parseFloat(line.substring(30, 38));
          Float.parseFloat(line.substring(38, 46));
          Float.parseFloat(line.substring(46, 54));
        } catch (Exception ex) {
          return 0;
        }
        return score;
      }

    } catch (Exception ex) {
      throw ex;
    }
    return score;
  }

  public void getCoordinates(MoleculeInterface mol, boolean inAngstroms, Writer writer) throws Exception {
    savePDB(mol, writer);
  }

  public String getCoordinatesAsString(MoleculeInterface molec, boolean inAngstroms) throws Exception {
    StringWriter sw = new StringWriter();
    savePDB(molec, sw);
    return sw.toString();
  }

  public static void savePDB(String filename, MoleculeInterface mol) throws Exception {
    try {
      savePDB(mol, new FileWriter(filename));
    } catch (Exception ex) {
      throw new Exception("Error saving into file " + filename + " : " + ex.getMessage());
    }
  }

  public static void savePDB(MoleculeInterface mol, Writer writer) throws Exception {
    AtomInterface atom;
    BondInterface bond;
    String line, bufer;
    StringTokenizer st;
    List serialNumbers = new ArrayList(); // Temporary array
    Integer serialN;
    Float cartes = new Float(0.0f);
    int current_residue = 0;

    BufferedWriter out;
    try {
      out = new BufferedWriter(writer);
    } catch (Exception ex) {
      throw new Exception("Cannot write :" + ex.getMessage());
    }


    try {
      //out.write((mol.getName() + "\n").getBytes());
      for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
        atom = mol.getAtomInterface(i);
        int res = atom.getSubstructureNumber();
        MonomerInterface m = mol.getMonomerInterface(res);
        int element = atom.getAtomicNumber();
        String symbol = ChemicalElements.getElementSymbol(element);
        if (symbol == null) {
          symbol = "DU";
        }
        symbol = symbol.toUpperCase();

        String atom_name = atom.getName();
        if (atom_name == null) {
          atom_name = "XX";
        }
        atom_name = atom_name.toUpperCase();

        if (atom_name.startsWith(symbol)) {
          if (symbol.length() == 1) {
            atom_name = " " + atom_name;
          }
          if (atom_name.length() > 4) {
            atom_name = atom_name.substring(0, 4);
          }
        } else {
          if (symbol.length() == 1) {
            symbol = " " + symbol;
          }
          if (atom_name.length() > 2) {
            atom_name = atom_name.substring(0, 2);
          }
          atom_name = symbol.concat(atom_name);
        }

        String resName = m.getName();
        if (resName == null) {
          resName = "UNK";
        }
        if (resName.length() > 3) {
          resName = resName.substring(0, 3);
        }

        // COLUMNS DATA TYPE FIELD DEFINITION
        // --------------------------------------------------------------------------------- 
        // 1 - 6 Record name "ATOM " 
        // 7 - 11 Integer serial Atom serial number. 
        // 13 - 16 Atom name Atom name. 
        // 17 Character altLoc Alternate location indicator. 
        // 18 - 20 Residue name resName Residue name. 
        // 22 Character chainID Chain identifier. 
        // 23 - 26 Integer resSeq Residue sequence number. 
        // 27 AChar iCode Code for insertion of residues. 
        // 31 - 38 Real(8.3) x Orthogonal coordinates for X in Angstroms. 
        // 39 - 46 Real(8.3) y Orthogonal coordinates for Y in Angstroms. 
        // 47 - 54 Real(8.3) z Orthogonal coordinates for Z in Angstroms. 
        // 55 - 60 Real(6.2) occupancy Occupancy. 
        // 61 - 66 Real(6.2) tempFactor Temperature factor. 
        // 73 - 76 LString(4) segID Segment identifier, left-justified. 
        // 77 - 78 LString(2) element Element symbol, right-justified. 
        // 79 - 80 LString(2) charge Charge on the atom.

        line = String.format("ATOM  %5d %-4s %3s  %4d    %8.3f%8.3f%8.3f\n",
                (i + 1), atom_name, resName, (res + 1),
                atom.getX(),
                atom.getY(), atom.getZ());

        out.write(line);

      }
    } catch (IOException e) {
      throw e;
    }

    // --- Finally, close a stream
    try {
      out.close();
    } catch (Exception e) {
      throw e;
    }
  }
}
