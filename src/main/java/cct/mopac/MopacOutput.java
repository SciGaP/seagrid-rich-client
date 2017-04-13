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

package cct.mopac;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class MopacOutput {

  public MopacOutput() {
  }

  static final float ONE_BOHR_FLOAT = (float) 0.529177249; // In Angstrom

  Map<String, String> outputResume = new LinkedHashMap<String, String> (100);
  Map<String, Object> moleculeProps = new HashMap<String, Object> ();
  boolean normalTermination = false;
  List<List> Geometries = new ArrayList<List> ();
  int natoms = -1;
  String runTitle = null;
  private float[] charges = null, noOfELECS, sPop, pPop;

  public void parseMopacOutputFile(String filename) throws Exception {
    outputResume.clear();
    normalTermination = false;
    Geometries.clear();
    moleculeProps.clear();
    String line;
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));

      while ( (line = in.readLine()) != null) {

        if (line.contains("FINAL HEAT OF FORMATION")) {
          this.outputResume.put("FINAL HEAT OF FORMATION", line + "\n");
          /*
                     FINAL HEAT OF FORMATION =     -10272.31507 KCAL =     -42979.36624 KJ
                     NO. OF FILLED LEVELS    =      4743
           */
          moleculeProps.put("HEAT OF FORMATION",
                            line.substring(35, 52).trim());
          int count = 0;
          while ( (line = in.readLine()) != null) {
            if (line.contains("MOLECULAR DIMENSIONS")) {
              break;
            }
            else if (line.trim().length() == 0) {
              continue;
            }

            if (line.contains("ELECTRONIC ENERGY")) {
              moleculeProps.put("ELECTRONIC ENERGY",
                                line.substring(35, 52).trim());
            }
            else if (line.contains("CORE-CORE REPULSION")) {
              moleculeProps.put("CORE-CORE REPULSION",
                                line.substring(35, 52).trim());
            }
            else if (line.contains("NO. OF FILLED LEVELS")) {
              moleculeProps.put("NO. OF FILLED LEVELS",
                                line.substring(35).trim());
            }

            ++count;
            outputResume.put("res" + count, line);
          }
        }

        else if (line.contains("COMPUTATION TIME")) {
          this.outputResume.put("COMPUTATION TIME", "\n" + line);
        }

        else if (line.contains("WALL CLOCK TIME")) {
          this.outputResume.put("WALL CLOCK TIME", line);
        }

        else if (line.contains("HAMILTONIAN")) {
          this.outputResume.put("HAMILTONIAN", line + "\n");
          /*
           *  AM1      - THE AM1 HAMILTONIAN TO BE USED
           */
          moleculeProps.put("METHOD", line.substring(2, 13).trim());
        }

        else if (line.contains("  CHARGE ON SYSTEM")) {
          this.outputResume.put("CHARGE ON SYSTEM", line + "\n");
          /*
           *                 CHARGE ON SYSTEM =    -9
           */
          try {
            moleculeProps.put(MoleculeInterface.ChargeProperty,
                              line.substring(37, 42).trim());
          }
          catch (Exception ex) {
            System.err.println("Cannot parse system charge: " +
                               line);
          }
        }

        else if (line.contains("CARTESIAN COORDINATES")) {
          List<MopacAtom> atoms = new ArrayList<MopacAtom> (100);

          // Skip three lines
          for (int i = 0; i < 3; i++) {
            if ( (line = in.readLine()) == null) {
              throw new Exception(
                  "Unexpected end-of-file while reading molecular geometry");
            }
          }

          // --- Start to read geometry

          while ( (line = in.readLine()) != null) {
            if (line.trim().length() == 0) {
              break;
            }
            MopacAtom atom = MopacOutput.parseCartesianCoordinates(line);
            atoms.add(atom);
          }

          Geometries.add(atoms);
        }

        else if (line.contains(
            "NET ATOMIC CHARGES AND DIPOLE CONTRIBUTIONS")) {
          if (Geometries.size() == 0) {
            System.err.println(
                "Found NET ATOMIC CHARGES AND DIPOLE CONTRIBUTIONS string but no atoms");
            continue;
          }
          List<MopacAtom> atoms = Geometries.get(0);

          charges = new float[atoms.size()];
          noOfELECS = new float[atoms.size()];
          sPop = new float[atoms.size()];
          pPop = new float[atoms.size()];

          // Skip two lines
          boolean unexpectedEnd = false;
          for (int i = 0; i < 2; i++) {
            if ( (line = in.readLine()) == null) {
              unexpectedEnd = true;
              break;
              //throw new Exception(
              //    "Unexpected end-of-file while reading molecular geometry");
            }
          }
          if (unexpectedEnd) {
            continue;
          }

          // --- Start to read charges

          for (int i = 0; i < atoms.size(); i++) {
            if ( (line = in.readLine()) == null ||
                line.trim().length() == 0) {
              break;
            }

            StringTokenizer st = new StringTokenizer(line, " \t");

            // --- Skip two tokens

            st.nextToken();
            st.nextToken();

            try {
              if (st.hasMoreTokens()) {
                charges[i] = Float.parseFloat(st.nextToken());
              }
              else {
                continue;
              }

              if (st.hasMoreTokens()) {
                noOfELECS[i] = Float.parseFloat(st.nextToken());
              }
              else {
                continue;
              }

              if (st.hasMoreTokens()) {
                sPop[i] = Float.parseFloat(st.nextToken());
              }
              else {
                continue;
              }

              if (st.hasMoreTokens()) {
                pPop[i] = Float.parseFloat(st.nextToken());
              }
              else {
                continue;
              }
            }
            catch (Exception ex) {

            }
          }

        }
      }
    }
    catch (IOException e) {
      System.err.println("parseGamessOutputFile: error parsing " + filename +
                         " : " + e.getMessage());
      throw new Exception("Error parsing " + filename + " : " + e.getMessage());
    }

  }

  /**
   * Parses MOPAC 2002 Log Files
   * @param filename String - File name
   * @throws Exception - in case of any error
   */
  public void parseMopacLogFile(String filename) throws Exception {
    outputResume.clear();
    moleculeProps.clear();
    normalTermination = false;
    Geometries.clear();
    String line;
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));

      while ( (line = in.readLine()) != null) {

        if (line.contains("HEAT OF FORMATION")) {
          this.outputResume.put("Heat of form", line + "\n");
          /*
                     HEAT OF FORMATION       =    -10272.315067 KCAL =     -42979.36624 KJ
           */
          moleculeProps.put("HEAT OF FORMATION",
                            line.substring(35, 52).trim());
          int count = 0;
          while ( (line = in.readLine()) != null) {
            if (line.trim().length() == 0) {
              break;
            }

            if (line.contains("ELECTRONIC ENERGY")) {
              moleculeProps.put("ELECTRONIC ENERGY",
                                line.substring(35, 52).trim());
            }
            else if (line.contains("CORE-CORE REPULSION")) {
              moleculeProps.put("CORE-CORE REPULSION",
                                line.substring(35, 52).trim());
            }
            else if (line.contains("DIPOLE")) {
              moleculeProps.put("DIPOLE", line.substring(35, 52).trim());
            }
            else if (line.contains("NO. OF FILLED LEVELS")) {
              moleculeProps.put("NO. OF FILLED LEVELS",
                                line.substring(35).trim());
            }
            else if (line.contains("CHARGE ON SYSTEM")) {
              try {
                moleculeProps.put(MoleculeInterface.ChargeProperty,
                                  new Integer(Integer.parseInt(line.
                    substring(40).trim())));
              }
              catch (Exception ex) {
                System.err.println("Cannot parse system charge: " +
                                   line);
              }

            }

            ++count;
            outputResume.put("res" + count, line);
          }
        }

        else if (line.contains("EMPIRICAL FORMULA")) {
          this.outputResume.put("Formula", line + "\n");
        }

        else if (line.contains("FINAL GEOMETRY OBTAINED")) {
          List<MopacAtom> atoms = new ArrayList<MopacAtom> (100);

          // Options
          if ( (line = in.readLine()) == null) {
            throw new Exception(
                "Unexpected end-of-file while reading molecular geometry");
          }
          this.outputResume.put("Options", line + "\n");

          moleculeProps.put(MoleculeInterface.MultiplicityProperty,
                            new Integer(1));
          if (line.contains("DOUBLET")) {
            moleculeProps.put(MoleculeInterface.MultiplicityProperty,
                              new Integer(2));
          }
          else if (line.contains("TRIPLET")) {
            moleculeProps.put(MoleculeInterface.MultiplicityProperty,
                              new Integer(3));
          }
          else if (line.contains("QUARTET")) {
            moleculeProps.put(MoleculeInterface.MultiplicityProperty,
                              new Integer(4));
          }
          else if (line.contains("QUINTET")) {
            moleculeProps.put(MoleculeInterface.MultiplicityProperty,
                              new Integer(5));
          }
          else if (line.contains("SEXTET")) {
            moleculeProps.put(MoleculeInterface.MultiplicityProperty,
                              new Integer(6));
          }
          else if (line.contains("SEPTET")) {
            moleculeProps.put(MoleculeInterface.MultiplicityProperty,
                              new Integer(7));
          }
          else if (line.contains("OCTET")) {
            moleculeProps.put(MoleculeInterface.MultiplicityProperty,
                              new Integer(8));
          }

          StringTokenizer st = new StringTokenizer(line, " \t");
          while (
              st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.contains("MNDO") || token.contains("AM1") ||
                token.contains("PM3") || token.contains("PM5") ||
                token.contains("MINDO")) {
              moleculeProps.put("METHOD", token);
              break;
            }

          }

          // Skip 2 lines
          if ( (line = in.readLine()) == null) {
            throw new Exception(
                "Unexpected end-of-file while reading molecular geometry");
          }
          if ( (line = in.readLine()) == null) {
            throw new Exception(
                "Unexpected end-of-file while reading molecular geometry");
          }

          while ( (line = in.readLine()) != null) {
            if (line.trim().length() == 0) {
              break;
            }
            MopacAtom atom = parseAtomData(line);
            atoms.add(atom);
          }

          Geometries.add(atoms);
        }

      }
    }
    catch (IOException e) {
      System.err.println("parseGamessOutputFile: error parsing " + filename +
                         " : " + e.getMessage());
      throw new Exception("Error parsing " + filename + " : " + e.getMessage());
    }

  }

  public static MopacAtom parseCartesianCoordinates(String data) throws
      Exception {
    MopacAtom atom = new MopacAtom();
    StringTokenizer st = new StringTokenizer(data, " \t");

    if (st.countTokens() < 5) {
      throw new Exception(
          "Expecting at least 5 tokens for reading cartesian coordinates, got " +
          data);
    }

    st.nextToken(); // Skip atom number

    atom.setName(st.nextToken());
    atom.setElement(ChemicalElements.getAtomicNumber(atom.name));

    try {
      atom.xyz[0] = Float.parseFloat(st.nextToken());
      atom.xyz[1] = Float.parseFloat(st.nextToken());
      atom.xyz[2] = Float.parseFloat(st.nextToken());
    }
    catch (Exception ex) {
      throw new Exception(
          "Error while parsing float point atom data: " + data);
    }

    return atom;
  }

  public static MopacAtom parseAtomData(String data) throws Exception {
    MopacAtom atom = new MopacAtom();
    StringTokenizer st = new StringTokenizer(data, " \t");

    if (st.countTokens() < 6) {
      throw new Exception(
          "Expecting at least 6 tokens while reading molecular geometry, got " +
          data);
    }

    atom.setName(st.nextToken());
    atom.setElement(ChemicalElements.getAtomicNumber(atom.name));

    try {
      atom.xyz[0] = Float.parseFloat(st.nextToken());
      st.nextToken();
      atom.xyz[1] = Float.parseFloat(st.nextToken());
      st.nextToken();
      atom.xyz[2] = Float.parseFloat(st.nextToken());
      if (st.hasMoreTokens()) {
        st.nextToken();
      }
      if (st.hasMoreTokens()) {
        atom.charge = Float.parseFloat(st.nextToken());
      }

    }
    catch (Exception ex) {
      throw new Exception(
          "Error while parsing float point atom data: " + data);
    }

    return atom;
  }

  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : molec == null");
    }
    if (Geometries.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : didn't find atoms in file");
    }

    molec.addMonomer("MOPAC");

    List<MopacAtom> atoms = Geometries.get(Geometries.size() - 1);

    boolean yesCharges = false;
    boolean yesNoOfELECS = false;
    boolean yesSPop = false;
    boolean yesPPop = false;

    for (int i = 0; i < atoms.size(); i++) {
      MopacAtom ga = atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.name);
      atom.setAtomicNumber(ga.element);
      atom.setXYZ(ga.xyz[0], ga.xyz[1], ga.xyz[2]);
      molec.addAtom(atom);
      if (charges != null) {
        if (!yesCharges) {
          yesCharges = Math.abs(charges[i]) > 0.00001;
        }
        if (!yesNoOfELECS) {
          yesNoOfELECS = Math.abs(noOfELECS[i]) > 0.00001;
        }
        if (!yesSPop) {
          yesSPop = Math.abs(sPop[i]) > 0.00001;
        }
        if (!yesPPop) {
          yesPPop = Math.abs(pPop[i]) > 0.00001;
        }
      }
    }

    if (charges != null) {
      for (int i = 0; i < atoms.size(); i++) {
        AtomInterface atom = molec.getAtomInterface(i);
        if (yesCharges) {
          atom.setProperty("MOPAC Mulliken Charge", new Float(charges[i]));
        }
        if (yesNoOfELECS) {
          atom.setProperty("MOPAC No. of ELECS.", new Float(noOfELECS[i]));
        }
        if (yesSPop) {
          atom.setProperty("MOPAC s-Pop", new Float(sPop[i]));
        }
        if (yesPPop) {
          atom.setProperty("MOPAC p-Pop", new Float(pPop[i]));
        }
      }
    }

    Set set = this.moleculeProps.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String key = me.getKey().toString();
      Object obj = me.getValue();
      molec.addProperty(key, obj);
    }

  }

  public String getOutputResume() {
    StringWriter sWriter = new StringWriter();
    Set set = outputResume.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String line = me.getValue().toString();
      sWriter.write(line + "\n");
    }

    //if (!normalTermination) {
    //   sWriter.write(
    //       "\n***** !!!!! IT IS NOT A NORMAL TERMINATION of GAUSSIAN !!!!! *****");
    //}
    return sWriter.toString();
  }

}
