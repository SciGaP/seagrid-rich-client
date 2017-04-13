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

package cct.vasp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

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
public class Vasp {

  static final int CARTESIAN_COORDINATES = 0;
  static final int DIRECT_COORDINATES = 1;

  static int colorMap[][] = {
      {
      227, 38, 54}, { //   0.8900   0.1500   0.2100 alizarin_crimson
      240, 248, 255}, { //   0.9412   0.9725   1.0000 alice_blue
      127, 255, 0}, { //   0.4980   1.0000   0.0000 chartreuse
      127, 255, 212}, { //   0.4980   1.0000   0.8314 aquamarine
      255, 97, 3}, //   1.0000   0.3800   0.0100 cadmium_orange

      {
      156, 102, 31}, { //   0.6100   0.4000   0.1200 brick
      0, 0, 255}, { //   0.0000   0.0000   1.0000 blue
      102, 128, 20}, { //   0.4000   0.5000   0.0800 chromeoxidegreen
      102, 205, 170}, { //   0.4000   0.8039   0.6667 aquamarinemedium
      255, 3, 13}, //   1.0000   0.0100   0.0500 cadmium_red_light

      {
      227, 23, 13}, { //  0.8900   0.0900   0.0500 cadmium_red_deep
      173, 216, 230}, { //   0.6784   0.8471   0.9020 blue_light
      97, 179, 41}, { //   0.3800   0.7000   0.1600 cinnabar_green
      0, 255, 255}, { //   0.0000   1.0000   1.0000 cyan
      237, 145, 33} //   0.9300   0.5700   0.1300 carrot
  };

  String Comment = "";
  float latticeConstant = 1.0f;
  float latticeVectors[][] = new float[3][3];
  int[] atomicSpecies = null;
  int numberOfAtoms = 0;

  boolean selectiveDynamicsSwitch = false;
  String selectiveDynamicsKey = "";
  String coordinatesKey = "";
  VaspAtom[] Atoms = null;

  int atomicPositionsType = CARTESIAN_COORDINATES;

  List<String> potcarAtomSymbols = null;
  List<Integer> potcarAtomNumbers = null;
  static final Logger logger = Logger.getLogger(Vasp.class.getCanonicalName());

  public Vasp() {
  }

  public int getNumberOfAtoms() {
    return this.numberOfAtoms;
  }

  public float[][] getLatticeVectors() {
    return latticeVectors;
  }

  public void parsePoscar(String filename, int fileType) throws Exception {

    String line;
    StringTokenizer st;

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

    try {

      // --- 1st line

      if ( (line = in.readLine()) == null) {
        throw new Exception("Error reading file " + filename + ": unexpected end of file while reading comment");
      }
      Comment = line.trim();

      // --- 2nd line - lattice constant
      // If this value is negative it is interpreted as the total volume of the cell

      if ( (line = in.readLine()) == null) {
        throw new Exception("Error reading file " + filename + ": unexpected end of file while reading lattice constant");
      }

      st = new StringTokenizer(line, " \t");

      if (st.countTokens() < 1) {
        throw new Exception("Error reading file " + filename + ": expecting lattice constant, got blank line");
      }

      try {
        latticeConstant = Float.parseFloat(st.nextToken());
      }
      catch (Exception ex) {
        throw new Exception("Error reading file " + filename + ": error parsing lattice constant: " + ex.getMessage());
      }

      // --- 3-5 lines - lattice vectors
      // On the following three lines the three lattice vectors defining
      // the unit cell of the system are given (first line corresponding to
      // the first lattice vector, second to the second, and third to the third).

      for (int i = 0; i < 3; i++) {
        if ( (line = in.readLine()) == null) {
          throw new Exception("Error reading file " + filename + ": unexpected end of file while reading lattice vectors");
        }

        st = new StringTokenizer(line, " \t");

        if (st.countTokens() < 3) {
          throw new Exception("Error reading file " + filename + ": expecting at least three numbers for lattice vector, got " +
                              st.countTokens() + " numbers");
        }

        for (int j = 0; j < 3; j++) {
          try {
            latticeVectors[i][j] = Float.parseFloat(st.nextToken()) * latticeConstant;
          }
          catch (Exception ex) {
            throw new Exception("Error reading file " + filename + ": error parsing lattice vector value: " + ex.getMessage());
          }
        }
      }

      // --- 6th line
      // The sixth line supplies the number of atoms per atomic species
      // (one number for each atomic species).  The ordering must be consistent with the POTCAR and the INCAR file.

      if ( (line = in.readLine()) == null) {
        throw new Exception("Error reading file " + filename +
                            ": unexpected end of file while reading the number of atoms per atomic species");
      }

      st = new StringTokenizer(line, " \t");

      if (st.countTokens() < 1) {
        throw new Exception("Error reading file " + filename +
                            ": expecting the number of atoms per atomic species, got blank line");
      }

      atomicSpecies = new int[st.countTokens()];
      numberOfAtoms = 0;

      for (int i = 0; i < atomicSpecies.length; i++) {
        try {
          atomicSpecies[i] = Integer.parseInt(st.nextToken());
          numberOfAtoms += atomicSpecies[i];
        }
        catch (Exception ex) {
          throw new Exception("Error reading file " + filename + ": error parsing the number of atoms per atomic species: " +
                              ex.getMessage());
        }
      }

      // --- 7st line

      if ( (line = in.readLine()) == null) {
        throw new Exception("Error reading file " + filename + ": unexpected end of file while reading 7th line");
      }
      selectiveDynamicsKey = line.trim();

      if (selectiveDynamicsKey.startsWith("S") || selectiveDynamicsKey.startsWith("s")) {
        selectiveDynamicsSwitch = true;
        if ( (line = in.readLine()) == null) {
          throw new Exception("Error reading file " + filename + ": unexpected end of file while reading 8th line");
        }
        coordinatesKey = line.trim();
      }
      else {
        selectiveDynamicsSwitch = false;
        coordinatesKey = selectiveDynamicsKey;
        selectiveDynamicsKey = "";
      }

      if (coordinatesKey.startsWith("C") || coordinatesKey.startsWith("c") || coordinatesKey.startsWith("K") ||
          coordinatesKey.startsWith("k")) {
        atomicPositionsType = CARTESIAN_COORDINATES;
      }
      else if (coordinatesKey.startsWith("D") || coordinatesKey.startsWith("d")) {
        atomicPositionsType = DIRECT_COORDINATES;
      }
      else {
        throw new Exception("Error reading file " + filename + ": unknown type of atom input coordinates" + coordinatesKey);
      }

      if (atomicPositionsType == CARTESIAN_COORDINATES) {
        for (int i = 0; i < 3; i++) {
          for (int j = 0; j < 3; j++) {
            latticeVectors[i][j] *= latticeConstant;
          }
        }
      }

      // --- Start to read atomic coordinates

      float x, y, z;
      boolean[] lf = new boolean[3];
      Atoms = new VaspAtom[numberOfAtoms];

      int nat = 0;
      for (int k = 0; k < atomicSpecies.length; k++) {
        for (int i = 0; i < atomicSpecies[k]; i++, nat++) {
          if ( (line = in.readLine()) == null) {
            throw new Exception("Error reading file " + filename + ": unexpected end of file while reading " + (i + 1) +
                                " atom");
          }
          st = new StringTokenizer(line, " \t");

          if (st.countTokens() < 3) {
            throw new Exception("Error reading file " + filename +
                                ": should be at least 3 numbers for atomic coordinates for " +
                                (i + 1) + " atom");
          }

          try {
            x = Float.parseFloat(st.nextToken());
            y = Float.parseFloat(st.nextToken());
            z = Float.parseFloat(st.nextToken());
          }
          catch (Exception ex) {
            throw new Exception("Error reading file " + filename + ": wrong number for atomic coordinate for " +
                                (i + 1) + " atom");
          }

          if (atomicPositionsType == CARTESIAN_COORDINATES) {
            x *= latticeConstant;
            y *= latticeConstant;
            z *= latticeConstant;
          }
          else if (atomicPositionsType == DIRECT_COORDINATES) {
            float x2 = 0, y2 = 0, z2 = 0;
            x2 = x * latticeVectors[0][0] + y * latticeVectors[1][0] + z * latticeVectors[2][0];
            y2 = x * latticeVectors[0][1] + y * latticeVectors[1][1] + z * latticeVectors[2][1];
            z2 = x * latticeVectors[0][2] + y * latticeVectors[1][2] + z * latticeVectors[2][2];
            x = x2;
            y = y2;
            z = z2;
          }

          if (st.countTokens() > 0 && st.countTokens() < 3) {
            throw new Exception("Error reading file " + filename + ": should be at least 3 letters for logical flags for " +
                                (i + 1) + " atom");
          }

          lf[0] = lf[1] = lf[2] = false;
          if (st.countTokens() > 0) {
            for (int j = 0; j < 3; j++) {
              String flag = st.nextToken();
              if (flag.equalsIgnoreCase("T")) {
                lf[j] = true;
              }
              else if (flag.equalsIgnoreCase("F")) {
                lf[j] = false;
              }
              else {
                throw new Exception("Error reading file " + filename + ": unknown logical flag for " +
                                    (i + 1) + " atom");
              }
            }
          }

          Atoms[nat] = new VaspAtom(x, y, z, lf[0], lf[1], lf[2]);
          Atoms[nat].species = k;
        }
      }
    }

    catch (IOException e) {
      throw new Exception("Error reading file " + filename + ": " + e.getMessage());
    }
  }

  public void parsePotcar(String filename, int fileType) throws Exception {

    String line;
    StringTokenizer st;

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

    try {

      while ( (line = in.readLine()) != null) {
        // --- 1st line; header
        boolean elementResoved = false;
        st = new StringTokenizer(line, " \t");
        // assume, that the first token is a type of a potential, and the second is element symbol
        String element = null, potential = null;
        if (st.countTokens() < 2 && st.countTokens() > 0) {
          element = st.nextToken();
        }
        else {
          potential = st.nextToken();
          element = st.nextToken();
        }

        if (element != null && element.contains("_")) {
          element = element.substring(0, element.indexOf("_"));
        }

        if (element != null) {
          elementResoved = true;
        }

        // --- Read potential information

        while ( (line = in.readLine()) != null) {
          line = line.trim().toUpperCase();
          if (line.equalsIgnoreCase("End of Dataset")) {
            break;
          }
          if (line.startsWith("VRHFIN")) {
            st = new StringTokenizer(line, " =:\t");
            if (st.countTokens() > 1) {
              st.nextToken();
              String el = st.nextToken();
              if (el.contains("_")) {
                el = el.substring(0, el.indexOf("_"));
              }
              if (elementResoved) { // Check
                if (!el.equalsIgnoreCase(element)) {
                  element = el;
                  logger.info("parsePotcar: Warning: element in VRHFIN " + el + " does not match element " +
                                     element + " in header");
                }
              }
              else {
                element = el;
              }
            }
          }
        }

        if (element != null) {
          Integer atomNumber = ChemicalElements.getAtomicNumber(element);
          if (potcarAtomSymbols == null) {
            potcarAtomSymbols = new ArrayList<String> ();
            potcarAtomNumbers = new ArrayList<Integer> ();
          }

          potcarAtomSymbols.add(element);
          potcarAtomNumbers.add(atomNumber);
        }
        else {
          System.err.println("parsePotcar: Warning: cannot resolve element after reading potential information...");
        }

      }

    }

    catch (IOException e) {
      throw new Exception("Error reading file " + filename + ": " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    Vasp vasp = new Vasp();
  }

  public void resolveAtomTypes() throws Exception {
    if (atomicSpecies == null) {
      throw new Exception("resolveAtomTypes: atomicSpecies == null");
    }
    else if (potcarAtomSymbols == null) {
      throw new Exception("resolveAtomTypes: potcarAtomSymbols == null");
    }
    else if (atomicSpecies.length != potcarAtomSymbols.size()) {
      throw new Exception("resolveAtomTypes: amount of atomic species is different in POSCAR and POTCAR files");
    }

    int nat = 0;
    for (int i = 0; i < atomicSpecies.length; i++) {
      String symbol = potcarAtomSymbols.get(i);
      int element = potcarAtomNumbers.get(i);
      for (int j = 0; j < atomicSpecies[i]; j++, nat++) {
        Atoms[nat].element = element;
      }
    }
  }

  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() + " : molec == null");
    }
    if (Atoms == null || Atoms.length < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : no VASP atoms");
    }

    molec.addMonomer("VASP");

    boolean typesNotSet = false;

    for (int i = 0; i < Atoms.length; i++) {
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(Atoms[i].getName());
      atom.setAtomicNumber(Atoms[i].element);
      atom.setXYZ(Atoms[i].xyz[0], Atoms[i].xyz[1], Atoms[i].xyz[2]);

      molec.addAtom(atom);

      if (Atoms[i].element > 0) {
        continue;
      }
      typesNotSet = true;

      int index = Atoms[i].species;
      if (index >= colorMap.length) {
        index = index % colorMap.length;
      }

      Integer[] rgbColor = new Integer[3];
      rgbColor[0] = colorMap[index][0];
      rgbColor[1] = colorMap[index][1];
      rgbColor[2] = colorMap[index][2];

      atom.setProperty(AtomInterface.RGB_COLOR, rgbColor);

      float radius = ChemicalElements.getCovalentRadius(14);
      radius *= AtomInterface.COVALENT_TO_GRADIUS_FACTOR;
      atom.setProperty(AtomInterface.GR_RADIUS, new Float(radius)); // GR_RADIUS "gradius" is a "graphics radius"
    }

    if (typesNotSet) {
      for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
        AtomInterface atom = molec.getAtomInterface(i);
        atom.setAtomicNumber(4);
      }
      for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
        AtomInterface atom = molec.getAtomInterface(i);
        atom.setAtomicNumber(0);
      }
    }

    molec.addProperty(MoleculeInterface.LATTICE_VECTORS, latticeVectors);
  }

}
