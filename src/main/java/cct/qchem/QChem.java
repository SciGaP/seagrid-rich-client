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

package cct.qchem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 * Written based on version 3.1 manual
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class QChem {

  static final double ONE_BOHR = 0.529177249; // In Angstrom
  static final float DEGREES_TO_RADIANS = (float) (Math.PI / 180.0);

  static final String batchSeparator = "@@@";

  static final String endSection = "$END";
  static final String commentSection = "$COMMENT";
  static final String remSection = "$REM";
  static final String moleculeSection = "$MOLECULE";
  static final String basisSection = "$BASIS"; // User{dened basis set information (see Chapter 7).
  static final String ecpSection = "$ECP"; // User{dened eective core potentials (see Chapter 8).
  static final String external_chargesSection = "$EXTERNAL_CHARGES"; // External charges and their positions.
  static final String intraculeSection = "$INTRACULE"; // Intracule parameters (see Chapter 10).
  static final String isotopesSection = "$ISOTOPES"; // Isotopic substitutions for vibrational calculations (see Chapter 10).
  static final String multipole_fieldSection = "$MLTIPOLE_FIELD"; // Details of a multipole eld to apply.
  static final String nboSection = "$NBO"; // Natural Bond Orbital package.
  static final String occupiedSection = "$OCCUPIED"; // Guess orbitals to be occupied.
  static final String optSection = "$OPT"; // Constraint denitions for geometry optimizations.
  static final String svpSection = "$SVP"; // Special parameters for the SS(V)PE module.
  static final String svpirfSection = "$SVPIRF"; // Initial guess for SS(V)PE) module.
  static final String plotsSection = "$PLOTS"; // Generate plotting information over a grid of points (see Chapter
  static final String van_der_waalsSection = "$VAN_DER_WAALS"; // User{dened atomic radii for Langevin dipoles solvation (see Chapter
  static final String xcSection = "$XC"; // functional Details of user{dened DFT exchange{correlation functionals.

  static final Set<String> validSections = new HashSet<String> ();
  static final Logger logger = Logger.getLogger(QChem.class.getCanonicalName());

  static {
    validSections.add(endSection);
    validSections.add(commentSection);
    validSections.add(remSection);
    validSections.add(moleculeSection);
    validSections.add(basisSection);
    validSections.add(ecpSection);
    validSections.add(external_chargesSection);
    validSections.add(intraculeSection);
    validSections.add(isotopesSection);
    validSections.add(multipole_fieldSection);
    validSections.add(nboSection);
    validSections.add(occupiedSection);
    validSections.add(optSection);
    validSections.add(svpSection);
    validSections.add(svpirfSection);
    validSections.add(plotsSection);
    validSections.add(van_der_waalsSection);
    validSections.add(xcSection);

  }

  boolean yesMoleculeSection = false;
  boolean yesRemSection = false;

  int netCharge = 0;
  int spinMultiplicity = 1;
  boolean cartesianCoords = true;
  List<QChemAtom> Atoms = new ArrayList<QChemAtom> ();

  Map<String, Object> remSectionParameters = new HashMap<String, Object> ();

  public QChem() {
  }

  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : molec == null");
    }
    if (Atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : no QChem atoms");
    }

    float factor = 1.0f;
    if (remSectionParameters.containsKey("INPUT_BOHR")) {
      String value = (String) remSectionParameters.get("INPUT_BOHR");
      try {
        boolean bohr = Boolean.parseBoolean(value.toLowerCase());
        if (bohr) {
          factor = (float) ONE_BOHR;
        }
      }
      catch (Exception ex) {

      }

    }

    molec.addProperty(MoleculeInterface.ChargeProperty, new Integer(netCharge));
    molec.addProperty(MoleculeInterface.MultiplicityProperty, new Integer(spinMultiplicity));

    molec.addMonomer("QChem");

    for (int i = 0; i < Atoms.size(); i++) {
      QChemAtom ga = Atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.getName());
      atom.setAtomicNumber(ga.getAtomicNumber());
      atom.setXYZ(ga.getX() * factor, ga.getY() * factor, ga.getZ() * factor);
      molec.addAtom(atom);
    }
  }

  public void parseQChemInput(String inputfile, int fileType) throws
      Exception {

    String line;
    BufferedReader in = null;

    if (fileType == 0) { // Read from file
      try {
        in = new BufferedReader(new FileReader(inputfile));
      }
      catch (Exception ex) {
        throw ex;
      }
    }
    else if (fileType == 1) { // Read from String
      in = new BufferedReader(new StringReader(inputfile));
    }
    else {
      throw new Exception(
          "parseQChemInput: INTERNAL ERROR: Unknown file type");
    }

    try {
      //BufferedReader in = new BufferedReader(new FileReader(filename));

      while ( (line = in.readLine()) != null) {
        line = line.trim();

        if (line.length() < 1) { // Blank line
          continue;
        }

        if (line.startsWith(batchSeparator)) {
          break;
        }

        if (!line.startsWith("$")) {
          continue;
        }

        if (line.startsWith("$")) { // Get section
          StringTokenizer st = new StringTokenizer(line, " \t=,");
          String sectionName = st.nextToken().toUpperCase();
          if (sectionName.equalsIgnoreCase(moleculeSection)) {
            this.readMoleculeSection(in);
          }
          else if (sectionName.equalsIgnoreCase(remSection)) {
            this.readRemSection(in);
          }
          else {
            if (!validSections.contains(sectionName)) {
              System.err.println("parseQChemInput: Error: Unknown input section " + sectionName + " Ignored...");
            }
            readSection(in, sectionName);
          }
        }
        else {
          in.close();
          throw new Exception(
              "parseQChemInput: Error: looking for section, got " + line);
        }

      }

      in.close();
    }
    catch (IOException e) {
      throw e;
    }
  }

  void readRemSection(BufferedReader in) throws Exception {
    String line;
    while ( (line = in.readLine()) != null) {
      line = line.trim().toUpperCase();
      if (line.startsWith(endSection)) {
        return;
      }
      else if (line.startsWith("!") || line.length() < 1) {
        continue;
      }

      StringTokenizer st = new StringTokenizer(line, " \t=,");

      if (st.countTokens() < 2) {
        System.err.println("readRemSection: line should have at least two tokens. Got " + line + " Ignored...");
        continue;
      }

      remSectionParameters.put(st.nextToken(), st.nextToken());

    }

    throw new Exception(
        "parseQChemInput: Error: din't find $end keyword for $rem section");
  }

  void readSection(BufferedReader in, String section) throws Exception {
    String line;
    if (section.equalsIgnoreCase(remSection)) {
      yesRemSection = true;
    }

    while ( (line = in.readLine()) != null) {
      line = line.trim().toUpperCase();
      if (line.startsWith(endSection)) {
        return;
      }
    }
    throw new Exception(
        "parseQChemInput: Error: din't find $end keyword for input section " + section);
  }

  /**
   * Reads $molecule section
   * @param in BufferedReader
   * @throws Exception
   */
  void readMoleculeSection(BufferedReader in) throws Exception {
    yesMoleculeSection = true;
    boolean endSectionFound = false;
    Map<String, Integer> atomNames = new HashMap<String, Integer> (100);
    Map<String, Float> symbolicStrings = new HashMap<String, Float> (300);

    String line;
    if ( (line = in.readLine()) == null) {
      throw new Exception(
          "Unexpected end-of-file while reading net charge and the spin multiplicity in $molecule section");
    }

    StringTokenizer st = new StringTokenizer(line, " \t=,");

    String charge = "";
    if (st.countTokens() >= 1) {
      charge = st.nextToken();
      if (charge.equalsIgnoreCase("READ")) {
        throw new Exception(
            "Cannot read molecular geometry from another file");
      }
    }

    if (st.countTokens() < 1) {
      throw new Exception(
          "Expecting two integer numbers for net charge and the spin multiplicity in $molecule section. Got " + line);
    }

    try {
      netCharge = Integer.parseInt(charge);
      spinMultiplicity = Integer.parseInt(st.nextToken());
    }
    catch (Exception ex) {
      throw new Exception(
          "Error parsing net charge or the spin multiplicity in $molecule section.\n Expecting two integer numbers. Got " + line);
    }

    // --- Start to read coordinates

    while ( (line = in.readLine()) != null) {
      line = line.trim().toUpperCase();

      if (line.length() < 1) {
        break; // End of input
      }
      else if (line.startsWith(endSection)) {
        endSectionFound = true;
        break;
      }

      st = new StringTokenizer(line, " \t,");

      // --- for the first atom determine input format

      if (Atoms.size() == 0) {
        if (st.countTokens() == 1) { // Z-matrix
          cartesianCoords = false;
        }
        else if (st.countTokens() == 4) { // Cartesians
          cartesianCoords = true;
        }
        else {
          throw new Exception(
              "Input line for the first atom should consist of either 1 or 4 tokens. Got " + line);
        }
      }

      // --- Error check

      if (cartesianCoords && st.countTokens() < 4) {
        throw new Exception(
            "Expecting at least 4 tokens for the " + (Atoms.size() + 1) + " atom. Got " + line);
      }
      else if (!cartesianCoords) {
        if (Atoms.size() == 1 && st.countTokens() < 3) {
          throw new Exception(
              "Expecting at least 3 tokens for the " + (Atoms.size() + 1) + " atom. Got " + line);
        }
        else if (Atoms.size() == 2 && st.countTokens() < 5) {
          throw new Exception(
              "Expecting at least 5 tokens for the " + (Atoms.size() + 1) + " atom. Got " + line);
        }
        else if (Atoms.size() > 2 && st.countTokens() < 7) {
          throw new Exception(
              "Expecting at least 7 tokens for the " + (Atoms.size() + 1) + " atom. Got " + line);
        }
      }

      // --- Start to read atom

      QChemAtom atom = new QChemAtom();

      String token = st.nextToken();

      boolean isNumber = false;
      int element = 0;
      try {
        element = Integer.parseInt(token);
        isNumber = true;
      }
      catch (Exception ex) {
        isNumber = false;
      }

      atom.setName(token);
      if (isNumber) {
        atom.setAtomicNumber(element);
      }
      else {
        atom.setAtomicNumber(getAtomicNumber(atom.getName()));
      }

      // --- check atom name for uniqueness

      if (atomNames.containsKey(atom.getName())) {
        // --- it's not unique
        atomNames.put(atom.getName(), new Integer( -1));
      }
      else {
        atomNames.put(atom.getName(), new Integer(Atoms.size()));
      }

      // --- Case for cartesian coordinates

      if (cartesianCoords) {
        for (int i = 0; i < 3; i++) {
          token = st.nextToken();

          isNumber = true;
          float value = 0;

          try {
            value = Float.parseFloat(token);
          }
          catch (Exception ex) {
            isNumber = false;
          }

          // --- if it's a parameter
          if (!isNumber) {
            String symbolic = token;
            if (token.startsWith("-")) {
              symbolic = token.substring(1);
            }
            if (!symbolicStrings.containsKey(symbolic)) {
              symbolicStrings.put(symbolic, null);
            }

            switch (i) {
              case 0:
                atom.setBondLength(token);
                break;
              case 1:
                atom.setAlpha(token);
                break;
              case 2:
                atom.setBeta(token);
                break;
            }
          }
          else {
            switch (i) {
              case 0:
                atom.setBondLength(value);
                break;
              case 1:
                atom.setAlpha(value);
                break;
              case 2:
                atom.setBeta(value);
                break;
            }
          }
        }

        Atoms.add(atom);
        continue;
      }

      // --- Code below only for Z-matrix input

      // --- Special case, Z-matrix, 1st atom

      if (!cartesianCoords && Atoms.size() == 0) {
        Atoms.add(atom);
        continue;
      }

      // --- reading reference atom A

      token = st.nextToken();

      // --- if it's atom's name

      isNumber = true;
      int number = 0;

      try {
        number = Integer.parseInt(token);
      }
      catch (Exception ex) {
        isNumber = false;
      }

      if (isNumber) { // --- If it's a number
        if (number < 1 || number > Atoms.size()) {
          throw new Exception("Wrong reference atom A value " + number +
                              " for " + (Atoms.size() + 1) +
                              " atom : it should be between 1 and " +
                              Atoms.size());
        }
        atom.set_i1(number - 1);
      }
      else { // --- If it's an atom
        if (atomNames.containsKey(token)) {
          Integer i = atomNames.get(token);
          atom.set_i1(i);
        }
        else {
          throw new Exception("Wrong reference atom A value " + token +
                              " for " + (Atoms.size() + 1) +
                              " atom : no atom with such name was previously specified");
        }
      }

      // --- Reading Bond Length

      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting bond length value");
      }
      token = st.nextToken();

      isNumber = true;
      float value = 0;

      try {
        value = Float.parseFloat(token);
      }
      catch (Exception ex) {
        isNumber = false;
      }

      // --- if it's a parameter
      if (!isNumber) {
        String symbolic = token;
        if (token.startsWith("-")) {
          symbolic = token.substring(1);
        }
        if (!symbolicStrings.containsKey(symbolic)) {
          symbolicStrings.put(symbolic, null);
        }

        atom.setBondLength(token);
      }
      else {
        atom.setBondLength(value);
      }

      // --- Special case, Z-matrix, 2nd atom

      if (Atoms.size() == 1) {
        Atoms.add(atom);
        continue;
      }

      // --- Reading reference Atom B

      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting reference Atom B");
      }
      token = st.nextToken();

      // --- if it's atom's name

      isNumber = true;

      try {
        number = Integer.parseInt(token);
      }
      catch (Exception ex) {
        isNumber = false;
      }

      if (isNumber) { // --- If it's a number
        if (number < 1 || number > Atoms.size() ||
            number == atom.get_i1() + 1) {
          throw new Exception("Wrong reference Atom B value " + number + " for " +
                              (Atoms.size() + 1) +
                              " atom : it should be between 1 and " +
                              Atoms.size() + " and not " +
                              (atom.get_i1() + 1));
        }
        atom.set_i2(number - 1);
      }
      else { // --- If it's an atom
        if (atomNames.containsKey(token)) {
          Integer i = atomNames.get(token);
          atom.set_i2(i);
        }
        else {
          throw new Exception("Wrong reference Atom B value " + token +
                              " for " + (Atoms.size() + 1) +
                              " atom : no atom with such name was previously specified");
        }
      }

      // --- Reading Angle

      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting angle value");
      }
      token = st.nextToken();

      isNumber = true;

      try {
        value = Float.parseFloat(token);
      }
      catch (Exception ex) {
        isNumber = false;
      }

      // --- if it's a parameter
      if (!isNumber) {
        String symbolic = token;
        if (token.startsWith("-")) {
          symbolic = token.substring(1);
        }
        if (!symbolicStrings.containsKey(symbolic)) {
          symbolicStrings.put(symbolic, null);
        }

        atom.setAlpha(token);
      }
      else {
        atom.setAlpha(value);
      }

      if (Atoms.size() == 2) {
        Atoms.add(atom);
        continue;
      }

      // --- Reading reference atom C

      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting reference atom C");
      }
      token = st.nextToken();

      // --- if it's atom's name

      isNumber = true;

      try {
        number = Integer.parseInt(token);
      }
      catch (Exception ex) {
        isNumber = false;
      }

      // --- if it's atom's name

      if (!isNumber) {
        if (atomNames.containsKey(token)) {
          Integer i = atomNames.get(token);
          atom.set_i3(i);
        }
        else {
          throw new Exception("Wrong reference atom C value " + token +
                              " for " + (Atoms.size() + 1) +
                              " atom : no atom with such name was previously specified");
        }
      }
      // --- If it's a number
      else {
        if (number < 1 || number > Atoms.size() ||
            number == atom.get_i1() + 1 ||
            number == atom.get_i2() + 1) {
          throw new Exception("Wrong reference atom C value " + number +
                              " for " + (Atoms.size() + 1) +
                              " atom : it should be between 1 and " +
                              Atoms.size() + " and not " +
                              (atom.get_i1() + 1) + " or " +
                              (atom.get_i2() + 1));
        }
        atom.set_i3(number - 1);
      }

      // --- Reading torsion angle

      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting torsion angle");
      }
      token = st.nextToken();

      isNumber = true;

      try {
        value = Float.parseFloat(token);
      }
      catch (Exception ex) {
        isNumber = false;
      }

      // --- if it's a parameter
      if (!isNumber) {
        String symbolic = token;
        if (token.startsWith("-")) {
          symbolic = token.substring(1);
        }
        if (!symbolicStrings.containsKey(symbolic)) {
          symbolicStrings.put(symbolic, null);
        }

        atom.setBeta(token);
      }
      else {
        atom.setBeta(value);
      }

      Atoms.add(atom);

    } // --- End of while/ reading atoms

    // --- Resolve Parameters

    if (symbolicStrings.size() > 0) {
      logger.info("Expecting " + symbolicStrings.size() +
                         " unique symbolic strings");
      if (endSectionFound) {
        throw new Exception(
            "Unexpected end of $molecule section. Excepting parameters");
      }

      // --- Start to read symbolic strings

      while ( (line = in.readLine()) != null) {
        line = line.trim().toUpperCase();

        st = new StringTokenizer(line, "=, \t");

        if (st.countTokens() < 1) {
          continue;
        }

        String currentString = st.nextToken();

        if (currentString.equalsIgnoreCase(endSection)) {
          break;
        }

        if (st.countTokens() < 1) {
          throw new Exception(
              "Expecting at least two values for symbolic strings");
        }

        // --- Read symbol

        if (!symbolicStrings.containsKey(currentString)) {
          System.err.println(
              "Warning: atom string parameters do not have string " +
              currentString);
          continue;
        }

        // --- Read value

        String token = st.nextToken();
        try {
          Float f = new Float(token);
          symbolicStrings.put(currentString, f);
        }
        catch (Exception ex) {
          throw new Exception("Wrong value for symbolic string " + token);
        }
      }
    }

    if (symbolicStrings.size() > 0) {
      try {
        resolveSymbolicStrings(symbolicStrings, Atoms);
      }
      catch (Exception ex) {
        throw ex;
      }
    }

    // --- Convert to cartesian coordinates

    if (cartesianCoords) {
      for (int i = 0; i < Atoms.size(); i++) {
        QChemAtom atom = Atoms.get(i);
        atom.setX(atom.getBondLength());
        atom.setY(atom.getAlpha());
        atom.setZ(atom.getBeta());
      }
    }
    else {
      fromZMatrixToCartesians(Atoms);
    }
  }

  /**
   * Expects bond length in angstroms
   * @param atoms ArrayList
   * @throws Exception
   */
  public static void fromZMatrixToCartesians(List<QChemAtom> atoms) throws
      Exception {
    int i, ii, jj, kk, ll;
    int natoms = atoms.size();
    QChemAtom a, a1, a2, a3;
    float xyz[] = new float[3], x1[], x2[], x3[];

    double SIN2, COS2, SIN3, COS3,
        VT[] = new double[3], V1[] = new double[3],
        V2[] = new double[3];
    double R2,
        V3[] = new double[3], VA1, VB1, VC1, R3, V[] = new double[3];

    if (natoms < 1) {
      return;
    }

    float factor = 1.0f;

    // --- The first atom. Put it into the origin

    QChemAtom atom = atoms.get(0);
    atom.setXYZ(0, 0, 0);

    if (natoms < 2) {
      return;
    }

    // --- ther second atom: put it along the X-axis

    atom = atoms.get(1);
    atom.setXYZ(atom.getBondLength() * factor, 0, 0);

    if (natoms < 3) {
      return;
    }

    // --- Third atom (put it into the XOY plane

    a = atoms.get(2);

    //x1 = (float[]) cartesians.get(a.ijk[0] - 1);
    x1 = new float[3];
    int index = a.get_i1();
    a1 = atoms.get(index);
    x1[0] = a1.getX();
    x1[1] = a1.getY();
    x1[2] = a1.getZ();

    xyz[2] = 0.0f; // Z-coordinate

    if (index == 0) { // Connected to the first atom
      //logger.info("To the 1st");
      xyz[0] = a.getBondLength() * factor *
          (float) Math.cos( (a.getAlpha() * DEGREES_TO_RADIANS)); // So, we put it along X-axis
      xyz[1] = a.getBondLength() * factor *
          (float) Math.sin( (double) (a.getAlpha() * DEGREES_TO_RADIANS));
    }
    else {
      //logger.info("To the 2nd");
      xyz[0] = x1[0] -
          a.getBondLength() * factor *
          (float) Math.cos( (double) (a.getAlpha() * DEGREES_TO_RADIANS)); // So, we put it along X-axis
      xyz[1] = -a.getBondLength() * factor *
          (float) Math.sin( (double) (a.getAlpha() * DEGREES_TO_RADIANS));
    }

    a.setXYZ(xyz[0], xyz[1], xyz[2]);

    // Other atoms

    for (i = 3; i < natoms; i++) {

      a = atoms.get(i);

      a1 = atoms.get(a.get_i1());
      a2 = atoms.get(a.get_i2());
      a3 = atoms.get(a.get_i3());

      x1 = a1.xyz;
      x2 = a2.xyz;
      x3 = a3.xyz;

      SIN2 = Math.sin( (double) (a.getAlpha() * DEGREES_TO_RADIANS));
      COS2 = Math.cos( (double) (a.getAlpha() * DEGREES_TO_RADIANS));
      SIN3 = Math.sin( (double) (a.getBeta() * DEGREES_TO_RADIANS));
      COS3 = Math.cos( (double) (a.getBeta() * DEGREES_TO_RADIANS));

      VT[0] = a.getBondLength() * factor * COS2;
      VT[1] = a.getBondLength() * factor * SIN2 * SIN3;
      VT[2] = a.getBondLength() * factor * SIN2 * COS3;

      V1[0] = x3[0] - x2[0];
      V1[1] = x3[1] - x2[1];
      V1[2] = x3[2] - x2[2];

      V2[0] = x2[0] - x1[0];
      V2[1] = x2[1] - x1[1];
      V2[2] = x2[2] - x1[2];

      R2 = Math.sqrt(V2[0] * V2[0] + V2[1] * V2[1] + V2[2] * V2[2]);

      V3[0] = V1[1] * V2[2] - V1[2] * V2[1];
      V3[1] = V1[2] * V2[0] - V1[0] * V2[2];
      V3[2] = V1[0] * V2[1] - V1[1] * V2[0];

      R3 = Math.sqrt(V3[0] * V3[0] + V3[1] * V3[1] + V3[2] * V3[2]);

      V2[0] = V2[0] / R2;
      V2[1] = V2[1] / R2;
      V2[2] = V2[2] / R2;

      V3[0] = V3[0] / R3;
      V3[1] = V3[1] / R3;
      V3[2] = V3[2] / R3;

      V[0] = V2[1] * V3[2] - V2[2] * V3[1];
      V[1] = V2[2] * V3[0] - V2[0] * V3[2];
      V[2] = V2[0] * V3[1] - V2[1] * V3[0];

      VA1 = V2[0] * VT[0] + V3[0] * VT[1] + V[0] * VT[2];
      VB1 = V2[1] * VT[0] + V3[1] * VT[1] + V[1] * VT[2];
      VC1 = V2[2] * VT[0] + V3[2] * VT[1] + V[2] * VT[2];

      xyz[0] = x1[0] + (float) VA1;
      xyz[1] = x1[1] + (float) VB1;
      xyz[2] = x1[2] + (float) VC1;

      a.setXYZ(xyz);
    }
  }

  private void resolveSymbolicStrings(Map<String, Float> symbolicStrings,
      List<QChemAtom> atoms) throws Exception {

    // --- Error check

    boolean error = false;
    String Errors = "";
    Set set = symbolicStrings.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String string = me.getKey().toString();
      Object value = me.getValue();

      if (value == null) {
        error = true;
        Errors += "No value for string " + string + "\n";
      }
      else {
        logger.info(me.getKey().toString() + "=" +
                           me.getValue().toString());
      }
    }

    if (error) {
      throw new Exception(Errors);
    }

    // --- resolve strings

    int nsymbols = 0;
    for (int i = 0; i < atoms.size(); i++) {
      QChemAtom atom = atoms.get(i);

      String string = atom.getBondLengthPar();
      if (string != null) {
        boolean negative = string.startsWith("-");
        if (negative) {
          string = string.substring(1);
        }
        Float f = symbolicStrings.get(string);
        if (negative) {
          f = -f;
        }
        atom.setBondLength(f);
      }

      string = atom.getAlphaPar();
      if (string != null) {
        boolean negative = string.startsWith("-");
        if (negative) {
          string = string.substring(1);
        }
        Float f = symbolicStrings.get(string);
        if (negative) {
          f = -f;
        }
        atom.setAlpha(f);
      }

      string = atom.getBetaPar();
      if (string != null) {
        boolean negative = string.startsWith("-");
        if (negative) {
          string = string.substring(1);
        }
        Float f = symbolicStrings.get(string);
        if (negative) {
          f = -f;
        }
        atom.setBeta(f);
      }
    }

  }

  public static int getAtomicNumber(String symbol) {
    byte[] chars = symbol.getBytes();
    String atom = "";

    if (symbol.length() == 1) {
      return ChemicalElements.getAtomicNumber(symbol);
    }
    else if (chars.length > 1 && chars[1] >= '0' && chars[1] <= '9') {
      atom = symbol.substring(0, 1);
    }
    else {
      atom = symbol.substring(0, 2);
    }

    return ChemicalElements.getAtomicNumber(atom);
  }

}
