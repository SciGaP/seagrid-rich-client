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
 * Data-sets consist of at least three parts. Later on in this Chapter we will elaborate on these parts, but for now it is sufficient to identify them.
 *
 * In order, the three essential parts of a MOPAC data-set are:
 *
 * Line 1: Keywords (one line). These control the calculation. The number of keywords used can range from none to about 20, space permitting. When more keywords are needed than will fit on one line, then more lines can be used (see Keyword & ).
 *
 * Line 2: Description of the calculation, e.g. the name of the molecule or ion.
 *
 * Line 3: Any other information describing the calculation.
 *
 * Line 4: Internal or Cartesian coordinates (many lines). See Geometry Specification.
 *
 * Line 5: Blank line to terminate the geometry definition.
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */


// In addition to the elements, other symbols used in geometry definition are:
// XX A dummy atom for assisting with geometry specification
// Tv A translation vector for use with polymers, layer systems, and solids
// Cb The "capped bond" atom
// +3 	A "sparkle" with a charge of +3
// ++ A "sparkle" with a charge of +2
// + A "sparkle" with a charge of +1
// Fr 	A "sparkle" with a charge of +1/2
// At 	A "sparkle" with a charge of -1/2
// -  A "sparkle" with a charge of -1
// -- A "sparkle" with a charge of -2
// -3 	A "sparkle" with a charge of -3


public class Mopac {

  static final float DEGREES_TO_RADIANS = (float) (Math.PI / 180.0);
  static Set<String> otherSymbols = new HashSet<String> ();
  static final Logger logger = Logger.getLogger(Mopac.class.getCanonicalName());

  static {
    otherSymbols.add("99");
    otherSymbols.add("X");
    otherSymbols.add("XX");
    otherSymbols.add("TV");
    otherSymbols.add("CB");
    otherSymbols.add("+3");
    otherSymbols.add("++");
    otherSymbols.add("+");
    otherSymbols.add("FR");
    otherSymbols.add("AT");
    otherSymbols.add("-");
    otherSymbols.add("--");
    otherSymbols.add("-3");
  }

  String Keywords = "";
  int keywordLines = 0;
  boolean gaussianInput = false;
  boolean yesSymmetryData = false;
  boolean yesXYZ = false;
  boolean yesTv = false;
  String Comment_1 = "", Comment_2 = "";
  int netCharge = 0;
  int spinMultiplicity = 1;
  List<MopacAtom> Atoms = new ArrayList<MopacAtom> (100);
  List<String> originalGeometry = new ArrayList<String> (100);
  List<String> originalSymmetry = new ArrayList<String> (10);
  List<String> originalTv = new ArrayList<String> (3);
  Map<String, Float> symbolicStrings = new HashMap<String, Float> (300);
  Symmetry symmetryRelations[] = null;
  Tv Tvs[] = null;

  public Mopac() {
  }

  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : molec == null");
    }
    if (Atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : no Mopac atoms");
    }

    float factor = 1.0f;

    molec.addProperty(MoleculeInterface.ChargeProperty, new Integer(netCharge));
    molec.addProperty(MoleculeInterface.MultiplicityProperty, new Integer(spinMultiplicity));

    molec.addMonomer("Mopac");

    for (int i = 0; i < Atoms.size(); i++) {
      MopacAtom ga = Atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.getName());
      atom.setAtomicNumber(ga.getAtomicNumber());
      atom.setXYZ(ga.getX() * factor, ga.getY() * factor, ga.getZ() * factor);
      molec.addAtom(atom);
    }
  }

  public void parseMopacInput(String inputfile, int fileType) throws
      Exception {

    String line = null;
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
          "parseMopacInput: INTERNAL ERROR: Unknown file type");
    }

    try {

      // --- Read keywords

      Keywords = readKeywords(in);
      parseKeywords();

      // --- Read comments (if any)

      int count = 3 - keywordLines;

      for (int i = 0; i < count; i++) {
        line = in.readLine();
        if (line == null) {
          throw new Exception("Unexpected end-of-file while reading comment...");
        }
        if (i == 0) {
          Comment_1 = line;
        }
        else if (i == 1) {
          Comment_2 = line;
        }
      }

      // The molecular or unit cell geometry is supplied in the data-set as one atom per line.
      // Sometimes it is useful to have points within a geometry defined.
      // These points need not represent atoms. To allow for this, users can add entities called "dummy atoms".

      //  The format in which the data is supplied is essentially the "Free-Format" style of FORTRAN.
      // In fact, a character input is used in order to accommodate the chemical symbols,
      // but the numeric data can be regarded as "free-format".
      // This means that integers and real numbers can be interspersed,
      // and numbers can be separated by one or more spaces, a tab and/or by one comma.
      // If a number is not specified, its value is set to zero.

      // The geometry can be defined in terms of either internal or Cartesian coordinates,
      // or a mixture of the two, or it can be in PDB or Gaussian format.

      // --- Reading geometry

      while ( (line = in.readLine()) != null) {
        if (line.startsWith("*")) {
          continue;
        }
        line = line.trim();

        if (line.length() == 0 || line.startsWith("0")) { // Blank line after geometry input
          break;
        }
        originalGeometry.add(line);
      }

      gaussianInput = isGaussianInput(originalGeometry);

      if (gaussianInput) {
        parseGaussianGeometry(originalGeometry, in);
      }
      else {
        parseMopacGeometry(originalGeometry);
      }

      // --- Read symmetry data (if any)

      if (yesSymmetryData) {
        while ( (line = in.readLine()) != null) {
          if (line.startsWith("*")) {
            continue;
          }
          line = line.trim();

          if (line.length() == 0) { // Blank line after geometry input
            break;
          }
          originalSymmetry.add(line);
        }

        parseSymmetryRelations(originalSymmetry);
        if (!yesXYZ) {
          imposeInternalCoordinateSymmetry(Atoms, symmetryRelations);
        }
      }

      fromZMatrixToCartesians(Atoms);
      if (yesXYZ && yesSymmetryData) {
        imposeCartesianCoordinateSymmetry(Atoms, symmetryRelations);
      }

      if (yesTv) {
        parseTranslationVectors(originalTv, Atoms);
        for (int i = 0; i < Tvs.length; i++) { // Remove vectors from atom list
          Atoms.remove(Atoms.size() - 1);
        }
      }

      in.close();
    }
    catch (Exception e) {
      in.close();
      throw e;
    }

  }

  void parseTranslationVectors(List<String> originalTvs, List<MopacAtom> atoms) throws Exception {
    if (originalTvs.size() < 1 || atoms.size() < 1) {
      return;
    }
    if (originalTvs.size() > 3) {
      throw new Exception("parseTranslationVectors: Can be no more that 3 translation vectors. Got " + originalTvs.size());
    }

    Tvs = new Tv[originalTvs.size()];

    String token;
    for (int i = 0; i < originalTvs.size(); i++) {
      String line = originalTvs.get(i);
      String streppedLine = this.stripAtomLabel(line);
      StringTokenizer st = new StringTokenizer(streppedLine, " \t,");
      if (st.countTokens() < 8) {
        throw new Exception("parseTranslationVectors: Translation vector should have at least 9 tokens\nGot " + line);
      }

      Tvs[i] = new Tv();

      token = st.nextToken();
      try {
        Tvs[i].length = Float.parseFloat(token);
      }
      catch (Exception ex) {
        throw new Exception("parseTranslationVectors: Cannot parse Translation vector length\nGot " + line);
      }

      // --- Skip 5 tokens
      st.nextToken();
      st.nextToken();
      st.nextToken();
      st.nextToken();
      st.nextToken();

      // --- Get direction

      try {
        token = st.nextToken();
        int first = Integer.parseInt(token) - 1;

        if (first < 0 || first >= atoms.size()) {
          throw new Exception("parseTranslationVectors: Wrong number for the first connectivity\nGot " + line);
        }

        token = st.nextToken();
        int second = Integer.parseInt(token) - 1;
        if (second < 0 || second >= atoms.size() || second == first) {
          throw new Exception("parseTranslationVectors: Wrong number for the second connectivity\nGot " + line);
        }

        MopacAtom a1 = atoms.get(first);
        MopacAtom a2 = atoms.get(second);

        float dx = a2.xyz[0] - a1.xyz[0];
        float dy = a2.xyz[1] - a1.xyz[1];
        float dz = a2.xyz[2] - a1.xyz[2];
        float s = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        Tvs[i].dir[0] = dx / s;
        Tvs[i].dir[1] = dy / s;
        Tvs[i].dir[2] = dz / s;
      }
      catch (Exception ex) {
        throw new Exception("parseTranslationVectors: Cannot parse Translation vector connectivity\nGot " + line);
      }

    }
  }

  void imposeInternalCoordinateSymmetry(List<MopacAtom> geometry, Symmetry symmetry[]) throws Exception {
    if (geometry == null || geometry.size() < 1 || symmetry == null || symmetry.length < 1) {
      return;
    }
    for (int i = 0; i < symmetry.length; i++) {
      int ref = symmetry[i].definingAtom;
      MopacAtom refAtom = geometry.get(ref);
      int func = symmetry[i].symmetryRelation;
      float multiple = symmetry[i].multiplyingFactor;
      int atoms[] = symmetry[i].definedAtoms;
      for (int j = 0; j < atoms.length; j++) {
        MopacAtom def_atom = geometry.get(atoms[j]);
        switch (func) {
          case 1: //        	Bond length is set equal to the reference bond length
            def_atom.bondLength = refAtom.bondLength;
            break;
          case 2: // 	  	Bond angle is set equal to the reference bond angle
            def_atom.alpha = refAtom.alpha;
            break;
          case 3: //	  	Dihedral angle is set equal to the reference dihedral angle
            def_atom.beta = refAtom.beta;
            break;
          case 4: // 	  	Dihedral angle varies as 90o - reference dihedral
            def_atom.beta = 90.0f - refAtom.beta;
            break;
          case 5: // 	  	Dihedral angle varies as 90o + reference dihedral
            def_atom.beta = 90.0f + refAtom.beta;
            break;
          case 6: // 	  	Dihedral angle varies as 120o - reference dihedral
            def_atom.beta = 120.0f - refAtom.beta;
            break;
          case 7: // 	  	Dihedral angle varies as 120o + reference dihedral
            def_atom.beta = 120.0f + refAtom.beta;
            break;
          case 8: // 	  	Dihedral angle varies as 180o - reference dihedral
            def_atom.beta = 180.0f - refAtom.beta;
            break;
          case 9: // 	  	Dihedral angle varies as 180o + reference dihedral
            def_atom.beta = 180.0f + refAtom.beta;
            break;
          case 10: // 	  	Dihedral angle varies as 240o - reference dihedral
            def_atom.beta = 240.0f - refAtom.beta;
            break;
          case 11: // 	  	Dihedral angle varies as 240o + reference dihedral
            def_atom.beta = 240.0f + refAtom.beta;
            break;
          case 12: // 	  	Dihedral angle varies as 270o - reference dihedral
            def_atom.beta = 270.0f - refAtom.beta;
            break;
          case 13: // 	  	Dihedral angle varies as 270o + reference dihedral
            def_atom.beta = 270.0f + refAtom.beta;
            break;
          case 14: // 	  	Dihedral angle varies as the negative of the reference dihedral
            def_atom.beta = -refAtom.beta;
            break;
          case 15: // 	  	Bond length varies as half the reference bond length
            def_atom.bondLength = refAtom.bondLength / 2.0f;
            break;
          case 16: // 	  	Bond angle varies as half the reference bond angle
            def_atom.alpha = refAtom.alpha / 2.0f;
            break;
          case 17: // 	  	Bond angle varies as 180o -reference bond angle
            def_atom.alpha = 180.0f - refAtom.alpha;
            break;
          case 18: // 	  	(not used)
            break;
          case 19: // 	  	Bond length is a multiple of the reference bond length
            def_atom.bondLength = refAtom.bondLength * multiple;
            //throw new Exception("Function 19 is not implemented yet");
            //break;
        }
      }
    }
  }

  void imposeCartesianCoordinateSymmetry(List<MopacAtom> geometry, Symmetry symmetry[]) throws Exception {
    if (geometry == null || geometry.size() < 1 || symmetry == null || symmetry.length < 1) {
      return;
    }
    for (int i = 0; i < symmetry.length; i++) {
      int ref = symmetry[i].definingAtom;
      MopacAtom refAtom = geometry.get(i);
      int func = symmetry[i].symmetryRelation;
      int atoms[] = symmetry[i].definedAtoms;
      for (int j = 0; j < atoms.length; j++) {
        MopacAtom def_atom = geometry.get(atoms[j]);
        switch (func) {
          case 1: //      	X coordinate is set equal to the reference X coordinate
            def_atom.xyz[0] = refAtom.xyz[0];
            break;
          case 2: //	  	Y coordinate is set equal to the reference Y coordinate
            def_atom.xyz[1] = refAtom.xyz[1];
            break;
          case 3: // 	  	Z coordinate is set equal to the reference Z coordinate
            def_atom.xyz[2] = refAtom.xyz[2];
            break;
          case 4: //	  	X coordinate is set equal to - the reference X coordinate
            def_atom.xyz[0] = -refAtom.xyz[0];
            break;
          case 5: //	  	Y coordinate is set equal to - the reference Y coordinate
            def_atom.xyz[1] = -refAtom.xyz[1];
            break;
          case 6: //	  	Z coordinate is set equal to - the reference Z coordinate
            def_atom.xyz[2] = -refAtom.xyz[2];
            break;
          case 7: //	  	X coordinate is set equal to the reference Y coordinate
            def_atom.xyz[0] = refAtom.xyz[1];
            break;
          case 8: //	  	Y coordinate is set equal to the reference Z coordinate
            def_atom.xyz[1] = refAtom.xyz[2];
            break;
          case 9: //	  	Z coordinate is set equal to the reference X coordinate
            def_atom.xyz[2] = refAtom.xyz[0];
            break;
          case 10: // 	  	X coordinate is set equal to - the reference Y coordinate
            def_atom.xyz[0] = refAtom.xyz[1];
            break;
          case 11: // 	  	Y coordinate is set equal to - the reference Z coordinate
            def_atom.xyz[1] = -refAtom.xyz[2];
            break;
          case 12: // 	  	Z coordinate is set equal to - the reference X coordinate
            def_atom.xyz[2] = -refAtom.xyz[0];
            break;
          case 13: // 	  	X coordinate is set equal to the reference Z coordinate
            def_atom.xyz[0] = refAtom.xyz[2];
            break;
          case 14: // 	  	Y coordinate is set equal to the reference X coordinate
            def_atom.xyz[1] = refAtom.xyz[0];
            break;
          case 15: // 	  	Z coordinate is set equal to the reference Y coordinate
            def_atom.xyz[2] = refAtom.xyz[1];
            break;
          case 16: // 	  	X coordinate is set equal to - the reference Z coordinate
            def_atom.xyz[0] = -refAtom.xyz[2];
            break;
          case 17: // 	  	Y coordinate is set equal to - the reference X coordinatebreak;
            def_atom.xyz[1] = -refAtom.xyz[0];
            break;
          case 18: // 	  	Z coordinate is set equal to - the reference Y coordinate
            def_atom.xyz[2] = -refAtom.xyz[1];
            break;
        }
      }
    }
  }

  String stripAtomLabel(String label) {
    if (label.matches("(?i).{1,2}[(]{1}.*[)]{1}.*")) {
      label = label.substring(label.indexOf(")") + 1);
      if (label.length() == 0) {
        return label;
      }
      if (!label.startsWith(" ") && !label.startsWith("\t") && !label.startsWith(",")) {
        StringTokenizer st = new StringTokenizer(label, " \t,");
        String token = st.nextToken();
        label = label.substring(label.indexOf(token) + token.length());
      }
    }
    else {
      StringTokenizer st = new StringTokenizer(label, " \t,");
      if (st.countTokens() == 0) {
        return label;
      }
      String token = st.nextToken();
      label = label.substring(label.indexOf(token) + token.length());
    }
    return label.trim();
  }

  String getAtomLabel(String label) {
    if (label.matches("(?i).{1,2}[(]{1}.*[)]{1}.*")) {
      int index = label.indexOf(")") + 1;
      StringTokenizer st = new StringTokenizer(label.substring(index), " \t,");
      if (st.countTokens() == 0) {
        return label.substring(0, index).trim();
      }

      return label.substring(0, index + st.nextToken().length()).trim();
    }
    else {
      StringTokenizer st = new StringTokenizer(label, " \t,");
      if (st.countTokens() == 0) {
        return label;
      }
      return st.nextToken();
    }
  }

  boolean isGaussianInput(List<String> geometry) throws Exception {

    StringTokenizer st = null;
    // Checking the first atom

    String strippedLine = null;
    String line = geometry.get(0);
    strippedLine = this.stripAtomLabel(line);

    st = new StringTokenizer(strippedLine, " \t,");
    if (st.countTokens() == 6) {
      return false; // Cartesian
    }
    //if (st.countTokens() != 1) {
    //   throw new Exception("The first atom in internal coordinates input should have only one token. Got: " + line);
    //}

    if (geometry.size() == 1) {
      return false;
    }

    // --- Checking the second atom

    line = geometry.get(1);
    strippedLine = this.stripAtomLabel(line);

    st = new StringTokenizer(strippedLine, " \t,");
    if (st.countTokens() == 6) {
      return false; // Cartesian
    }
    if (st.countTokens() < 1) {
      throw new Exception("The second atom in internal coordinates input should have at least 2 tokens. Got: " + line);
    }

    //st.nextToken(); // Skip
    String token = st.nextToken(); // Second token
    if (token.contains(".") || (!token.equals("1"))) {
      return false; // it's MOPAC bond length...
    }

    token = st.nextToken(); // Third token

    try {
      float bond = Float.parseFloat(token);
    }
    catch (Exception ex) {
      if (token.length() > 8) {
        throw new Exception("Symbolic length in Gaussian input should be up to 8 characters. Got: " + line);
      }
      return true; // Symbolic bond length
      //throw new Exception("Error parsing the second token for the second atom\nExpecting a number, got: " + line);
    }

    if (geometry.size() == 2) {
      return false;
    }

    // --- Checking the third atom...

    line = geometry.get(2);
    strippedLine = stripAtomLabel(line);
    st = new StringTokenizer(strippedLine, " \t,");
    if (st.countTokens() == 6) {
      return false; // Cartesian
    }
    else if (st.countTokens() == 4) {
      return true; // Gaussian internal
    }
    else if (st.countTokens() >= 4) {
      return false; // Mopac internal
    }
    else {
      throw new Exception("The third atom should have at least 4 tokens. Got: " + line);
    }
  }

  public String readKeywords(BufferedReader in) throws Exception {
    String keywords = "";
    String line;
    try {
      keywordLines = 0;
      while ( (line = in.readLine()) != null) {
        if (line.startsWith("*")) {
          continue;
        }

        line = line.trim();
        if (keywords.length() > 0) {
          keywords += " ";
        }
        keywords += line;

        if (line.contains("+")) {
          continue;
        }

        ++keywordLines;
        if (!line.contains("&") && !line.contains("+")) {
          break;
        }

        if (keywordLines == 3) {
          break;
        }
      }

      if (line == null && keywordLines == 0) {
        throw new Exception("Unexpected end-of-file while reading Mopac keywords...");
      }
    }
    catch (Exception ex) {
      throw new Exception("Error reading Mopac keywords: " + ex.getMessage());
    }
    return keywords;
  }

  void parseKeywords() throws Exception {
    String token = null, upToken = null;
      StringTokenizer st = new StringTokenizer(Keywords, " \t,");
    while (st.hasMoreTokens()) {
      token = st.nextToken();
      upToken = token.toUpperCase();

      // parse system charge
      if (upToken.startsWith("CHARGE=")) {
        String charge = upToken.substring("CHARGE=".length());
        try {
          netCharge = Integer.parseInt(charge);
        }
        catch (Exception ex) {
          throw new Exception("Error parsing system charge " + token + " : " + ex.getMessage());
        }
        //spinMultiplicity = 1;
      }

      // parse multiplicity

      else if (upToken.equals("SINGLET")) {
        spinMultiplicity = 1;
      }
      else if (upToken.equals("DOUBLET")) {
        spinMultiplicity = 2;
      }
      else if (upToken.equals("TRIPLET")) {
        spinMultiplicity = 3;
      }
      else if (upToken.equals("QUARTET")) {
        spinMultiplicity = 4;
      }
      else if (upToken.equals("QUINTET")) {
        spinMultiplicity = 5;
      }
      else if (upToken.equals("SEXTET")) {
        spinMultiplicity = 6;
      }
      else if (upToken.equals("SEPTET")) {
        spinMultiplicity = 7;
      }
      else if (upToken.equals("OCTET")) {
        spinMultiplicity = 8;
      }

      // --- To read symmetry information ?

      else if (upToken.equals("SYMMETRY") || upToken.equals("SYM")) {
        yesSymmetryData = true;
      }

      // --- To optimize in cartesians

      else if (upToken.equals("XYZ")) {
        yesXYZ = true;
      }

    }
  }

  void parseSymmetryRelations(List<String> symmetry) throws Exception {
    if (symmetry.size() < 1) {
      return;
    }
    symmetryRelations = new Symmetry[symmetry.size()];
    String line, token;
    for (int i = 0; i < symmetry.size(); i++) {
      line = symmetry.get(i);
      StringTokenizer st = new StringTokenizer(line, " \t,");
      if (st.countTokens() < 3) {
        throw new Exception("Symmetry relation should have at least three tokens. Got " + line);
      }

      symmetryRelations[i] = new Symmetry();

      // --- parse reference atom

      try {
        token = st.nextToken();
        symmetryRelations[i].definingAtom = Integer.parseInt(token) - 1;
        if (symmetryRelations[i].definingAtom < 0 || symmetryRelations[i].definingAtom >= Atoms.size()) {
          throw new Exception("Reference atom in Symmetry relations should be > 0 and < " + (Atoms.size() + 1) + " Got: " +
                              line);
        }
      }
      catch (Exception ex) {
        throw new Exception("Error parsing reference atom in Symmetry relations: " + line + " : " + ex.getMessage());
      }

      // --- Parse symmetry relation

      try {
        token = st.nextToken();
        symmetryRelations[i].symmetryRelation = Integer.parseInt(token);
        if (symmetryRelations[i].symmetryRelation < 1 || symmetryRelations[i].symmetryRelation > 19) {
          throw new Exception("Symmetry relation should be > 0 and < 20. Got: " + line);
        }

        if (symmetryRelations[i].symmetryRelation == 19 && st.countTokens() < 2) {
          throw new Exception("Symmetry relation 19 should have at least 4 tokens. Got: " + line);
        }
      }
      catch (Exception ex) {
        throw new Exception("Error parsing Symmetry relation: " + line + " : " + ex.getMessage());
      }

      // --- Parse Bond length multiple  (if any)

      if (symmetryRelations[i].symmetryRelation == 19) {
        try {
          token = st.nextToken();
          symmetryRelations[i].multiplyingFactor = Float.parseFloat(token);
        }
        catch (Exception ex) {
          throw new Exception("Error: Cannot parse Bond length multiple for Symmetry relation 19\n" + line + " : " +
                              ex.getMessage());
        }
      }

      // --- Parse defined atoms

      symmetryRelations[i].definedAtoms = new int[st.countTokens()];

      for (int j = 0; j < symmetryRelations[i].definedAtoms.length; j++) {
        try {
          token = st.nextToken();
          symmetryRelations[i].definedAtoms[j] = Integer.parseInt(token) - 1;
          if (symmetryRelations[i].definedAtoms[j] < 0 || symmetryRelations[i].definedAtoms[j] >= Atoms.size()) {
            throw new Exception("Defined atom in Symmetry relations should be > 0 and < " + (Atoms.size() + 1) + " Got: " +
                                line);
          }
        }
        catch (Exception ex) {
          throw new Exception("Error defined atom in Symmetry relations: " + line + " : " + ex.getMessage());
        }
      }

    }
  }

  public void parseMopacGeometry(List<String> geometry) throws Exception {
    // The order of the data is thus: (real and dummy atoms) (translation vector(s)) (blank line)
    // (symmetry data or MECI data etc, if needed).
    String line;

    // Comment lines are already removed...

    for (int i = 0; i < geometry.size(); i++) {
      line = geometry.get(i);

      String label = this.getAtomLabel(line);
      line = this.stripAtomLabel(line);
      StringTokenizer st = new StringTokenizer(line, " \t,");
      //if (st.countTokens() < 1) {
      //   throw new Exception("Expecting Mopac geometry, got blank line...");
      //}

      // --- Process Tv

      if (label.equalsIgnoreCase("TV")) {
        yesTv = true;
        if (originalTv.size() >= 3) {
          throw new Exception("Should be no more than 3 translation vectors");
        }
        originalTv.add(line);
      }
      else if (yesTv) {
        throw new Exception("No real or dummy atoms should come after a Tv");
      }

      MopacAtom atom = new MopacAtom();
      parseElement(label, atom);

      // --- The first atom
      if (Atoms.size() == 0) { // Read the first atom
        if (st.countTokens() == 0 || st.countTokens() != 6) { // Internal coordinates
          atom.cartesian = false;
          parseInternalCoord(st, atom);
        }
        else if (st.countTokens() == 6) {
          parseCartesianCoord(st, atom);
        }
        //else {
        //   throw new Exception("the first atom may have one or 7 tokens. Got " + line);
        //}
      }

      // --- the second atom
      else if (Atoms.size() == 1) { // Read the second atom
        if (st.countTokens() == 6) { // Cartesian coordinates
          parseCartesianCoord(st, atom);
        }
        else if (st.countTokens() >= 1) {
          parseInternalCoord(st, atom);
        }
        else {
          throw new Exception("The second atom should have at least two tokens. Got " + line);
        }
      }

      // --- the third atom
      else if (Atoms.size() == 2) { // Read the third atom
        MopacAtom a = Atoms.get(1);
        if (st.countTokens() == 6 && a.cartesian) { // Cartesian coordinates
          parseCartesianCoord(st, atom);
        }
        else if (st.countTokens() >= 3) {
          parseInternalCoord(st, atom);
        }
        else {
          throw new Exception("The third atom should have at least 4 tokens. Got " + line);
        }
      }

      // --- >3 atoms
      else if (Atoms.size() > 2) {
        if (st.countTokens() == 6) { // Cartesian coordinates
          parseCartesianCoord(st, atom);
        }
        else if (st.countTokens() >= 9) {
          parseInternalCoord(st, atom);
        }
        else {
          throw new Exception("Atom " + (Atoms.size() + 1) + " should have at least 6 tokens. Got " + line);
        }
      }

      Atoms.add(atom);
    }

    /*
           try {
       while ( (line = in.readLine()) != null) {
          if (line.startsWith("*")) {
             continue;
          }

       }
           }
           catch (Exception ex) {
       throw new Exception("Error reading Mopac geometry: " + ex.getMessage());
           }
     */
  }

  public void parseInternalCoord(StringTokenizer st, MopacAtom atom) throws Exception {
    /*
           MINDO/3
     Formic acid
     Example of normal geometry definition
        O
        C    1.20 1
        O    1.32 1  116.8 1    0.0 0   2  1
        H    0.98 1  123.9 1    0.0 0   3  2  1
        H    1.11 1  127.3 1  180.0 0   2  1  3
        0    0.00 0    0.0 0    0.0 0   0  0  0

     */
    atom.cartesian = false;
    String token = null;

    // --- The first atom
    if (Atoms.size() == 0) {
      return;
    }
    //else if (Atoms.size() == 0 && st.countTokens() > 0) {
    //   throw new Exception("The first atom in internal coordinate input should have only one token...");
    //}

    // --- The second atom
    else if (Atoms.size() == 1 && st.countTokens() >= 1) {
      token = st.nextToken();
      try {
        atom.bondLength = Float.parseFloat(token);

        if (st.countTokens() == 0) {
          atom.opt[0] = 0;
          return;
        }

        token = st.nextToken();
        if (token.equalsIgnoreCase("T")) {
          atom.opt[0] = -2;
        }
        else {
          if (token.startsWith("+")) {
            token = token.substring(1);
          }
          atom.opt[0] = Integer.parseInt(token);
        }
      }
      catch (Exception ex) {
        throw new Exception("Error parsing internal coordinate for the second atoms: " + token);
      }

    }
    else if (Atoms.size() == 1 && st.countTokens() < 1) {
      throw new Exception("The second atom in internal coordinate input should have at least two tokens...");
    }

    // --- The third atom

    else if (Atoms.size() == 2 && st.countTokens() >= 3) {
      token = st.nextToken();
      try {
        atom.bondLength = Float.parseFloat(token);
        token = st.nextToken();
        if (token.equalsIgnoreCase("T")) {
          atom.opt[0] = -2;
        }
        else {
          if (token.startsWith("+")) {
            token = token.substring(1);
          }
          atom.opt[0] = Integer.parseInt(token);
        }

        token = st.nextToken();
        atom.alpha = Float.parseFloat(token);

        if (st.countTokens() == 0) {
          atom.opt[1] = 0;
          atom.i1 = 1;
          atom.i2 = 0;
          return;
        }

        token = st.nextToken();
        if (token.equalsIgnoreCase("T")) {
          atom.opt[1] = -2;
        }
        else {
          if (token.startsWith("+")) {
            token = token.substring(1);
          }
          atom.opt[1] = Integer.parseInt(token);
        }

        if (st.countTokens() == 0) {
          atom.i1 = 1;
          atom.i2 = 0;
          return;
        }

        // --- Skip two tokens...
        if (st.countTokens() >= 4) {
          st.nextToken();
          st.nextToken();
        }

        // --- Connectivity

        token = st.nextToken();
        atom.i1 = Integer.parseInt(token) - 1;
        token = st.nextToken();
        atom.i2 = Integer.parseInt(token) - 1;

      }
      catch (Exception ex) {
        throw new Exception("Error parsing internal coordinate for the third atom: " + token);
      }

    }
    else if (Atoms.size() == 2 && st.countTokens() < 3) {
      throw new Exception("The third atom in internal coordinate input should have at least 4 tokens...");
    }

    // --- >3 atoms

    else if (Atoms.size() > 2 && st.countTokens() >= 9) {
      token = st.nextToken();
      try {
        atom.bondLength = Float.parseFloat(token);
        token = st.nextToken();
        if (token.equalsIgnoreCase("T")) {
          atom.opt[0] = -2;
        }
        else {
          if (token.startsWith("+")) {
            token = token.substring(1);
          }
          atom.opt[0] = Integer.parseInt(token);
        }

        token = st.nextToken();
        atom.alpha = Float.parseFloat(token);
        token = st.nextToken();
        if (token.equalsIgnoreCase("T")) {
          atom.opt[1] = -2;
        }
        else {
          if (token.startsWith("+")) {
            token = token.substring(1);
          }
          atom.opt[1] = Integer.parseInt(token);
        }

        token = st.nextToken();
        atom.beta = Float.parseFloat(token);
        token = st.nextToken();
        if (token.equalsIgnoreCase("T")) {
          atom.opt[2] = -2;
        }
        else {
          if (token.startsWith("+")) {
            token = token.substring(1);
          }
          atom.opt[2] = Integer.parseInt(token);
        }

        // --- Connectivity

        token = st.nextToken();
        atom.i1 = Integer.parseInt(token) - 1;
        token = st.nextToken();
        atom.i2 = Integer.parseInt(token) - 1;
        token = st.nextToken();
        atom.i3 = Integer.parseInt(token) - 1;

      }
      catch (Exception ex) {
        throw new Exception("Error parsing internal coordinate for the second atoms: " + token);
      }

    }
    else if (Atoms.size() > 2 && st.countTokens() < 9) {
      throw new Exception("Atom " + (Atoms.size() + 1) + " in internal coordinate input should have 10 tokens...");
    }

  }

  public void parseCartesianCoord(StringTokenizer st, MopacAtom atom) throws Exception {
    if (st.countTokens() < 6) {
      throw new Exception("Cartesian coordinates should have at least 6 tokens. Got " + st.countTokens());
    }

    String token = null;
    try {
      token = st.nextToken();
      atom.xyz[0] = Float.parseFloat(token);

      token = st.nextToken();
      if (token.equalsIgnoreCase("T")) {
        atom.opt[0] = -2;
      }
      else if (token.matches("[^0-9]+")) {
        throw new Exception("Unknown flag (could be 0, 1, -1, and T): " + token);
      }
      if (token.startsWith("+")) {
        token = token.substring(1);
      }
      atom.opt[0] = Integer.parseInt(token);

      token = st.nextToken();
      atom.xyz[1] = Float.parseFloat(token);

      token = st.nextToken();
      if (token.equalsIgnoreCase("T")) {
        atom.opt[1] = -2;
      }
      else if (token.matches("[^0-9]+")) {
        throw new Exception("Unknown flag (could be 0, 1, -1, and T): " + token);
      }

      if (token.startsWith("+")) {
        token = token.substring(1);
      }
      atom.opt[1] = Integer.parseInt(token);

      token = st.nextToken();
      atom.xyz[2] = Float.parseFloat(token);

      token = st.nextToken();
      if (token.equalsIgnoreCase("T")) {
        atom.opt[2] = -2;
      }
      else if (token.matches("[^0-9]+")) {
        throw new Exception("Unknown flag (could be 0, 1, -1, and T): " + token);
      }

      if (token.startsWith("+")) {
        token = token.substring(1);
      }
      atom.opt[2] = Integer.parseInt(token);
    }
    catch (Exception ex) {
      throw new Exception("Error parsing Cartesian coordinate or flag " + token + " : " + ex.getMessage());
    }

  }

  public void parseElement(String label, MopacAtom atom) throws Exception {
    if ( (label.contains("(") && !label.contains(")")) || (!label.contains("(") && label.contains(")"))) {
      throw new Exception("Parentheses are not matched in atom label: " + label);
    }

    String upperLabel = label.toUpperCase();
    //otherSymbols.add("TV");

    // --- Process special cases

    if (upperLabel.startsWith("99") || upperLabel.startsWith("XX") || upperLabel.startsWith("X")) {
      atom.charge = 0;
      atom.element = 0;
      atom.name = label;
      return;
    }
    else if (upperLabel.startsWith("CB")) {
      atom.charge = 0;
      atom.element = 0;
      atom.name = label;
      return;
    }
    else if (upperLabel.startsWith("+3")) {
      atom.charge = 3.0f;
      atom.element = 0;
      atom.name = label;
      return;
    }
    else if (upperLabel.startsWith("++")) {
      atom.charge = 2.0f;
      atom.element = 0;
      atom.name = label;
      return;
    }
    else if (upperLabel.startsWith("+")) {
      atom.charge = 1.0f;
      atom.element = 0;
      atom.name = label;
      return;
    }
    else if (upperLabel.startsWith("-3")) {
      atom.charge = -3.0f;
      atom.element = 0;
      atom.name = label;
      return;
    }
    else if (upperLabel.startsWith("--")) {
      atom.charge = -2.0f;
      atom.element = 0;
      atom.name = label;
      return;
    }
    else if (upperLabel.startsWith("-")) {
      atom.charge = -1.0f;
      atom.element = 0;
      atom.name = label;
      return;
    }
    else if (upperLabel.startsWith("FR")) {
      atom.charge = 0.5f;
      atom.element = 0;
      atom.name = label;
      return;
    }
    else if (upperLabel.startsWith("AT")) {
      atom.charge = -0.5f;
      atom.element = 0;
      atom.name = label;
      return;
    }

    // --- "Normal" atom

    if (label.contains("(") && label.matches("(?i).{1,2}[(]{1}.*[)]{1}.*")) {
      StringTokenizer st = new StringTokenizer(label, "()");
      String token = st.nextToken();
      atom.charge = 0.0f;
      atom.element = ChemicalElements.getAtomicNumber(token);
      atom.name = token;

    }
    else if (label.contains("(") && !label.matches("(?i).{1,2}[(]{1}.*[)]{1}.*")) {
      throw new Exception("Wrong atom label format: " + label);
    }
    else {
      try {
        int element = Integer.parseInt(label);
        atom.charge = 0.0f;
        atom.element = element;
        atom.name = ChemicalElements.getElementSymbol(element);
        return;
      }
      catch (Exception ex) {}

      //line.matches("(?i).*job_state.*=.*")

      if (label.length() == 1) {
        atom.charge = 0.0f;
        atom.element = ChemicalElements.getAtomicNumber(label);
        atom.name = label;
        return;
      }
      else if (label.length() == 2 && upperLabel.matches("[A-Z]{2}")) {
        atom.charge = 0.0f;
        atom.element = ChemicalElements.getAtomicNumber(label);
        atom.name = label;
        return;
      }
      else if (label.length() == 2 && upperLabel.matches("[A-Z]{1}[0-9]{1}")) {
        atom.charge = 0.0f;
        atom.element = ChemicalElements.getAtomicNumber(label.substring(0, 1));
        atom.name = label.substring(0, 1);
        try {
          atom.isotopeMass = Float.parseFloat(label.substring(1));
        }
        catch (Exception ex) {
          throw new Exception("Error extracting isotope mass from atom label " + label + " : " + ex.getMessage());
        }
        return ;
      }
      else if (upperLabel.substring(0, 2).matches("[A-Z]{2}")) {
        atom.charge = 0.0f;
        atom.element = ChemicalElements.getAtomicNumber(label.substring(0, 2));
        atom.name = label.substring(0, 2);
        try {
          atom.isotopeMass = Float.parseFloat(label.substring(2));
        }
        catch (Exception ex) {
          throw new Exception("Error extracting isotope mass from atom label " + label + " : " + ex.getMessage());
        }
        return ;
      }
      else if (upperLabel.substring(0, 1).matches("[A-Z]{1}")) {
        atom.charge = 0.0f;
        atom.element = ChemicalElements.getAtomicNumber(label.substring(0, 1));
        atom.name = label.substring(0, 1);
        try {
          atom.isotopeMass = Float.parseFloat(label.substring(1));
        }
        catch (Exception ex) {
          throw new Exception("Error extracting isotope mass from atom label " + label + " : " + ex.getMessage());
        }
        return ;
      }
      else {
        throw new Exception("Cannot parse atom label " + label);
      }
    }
  }

  public static void fromZMatrixToCartesians(List<MopacAtom> atoms) throws
      Exception {
    int i, ii, jj, kk, ll;
    int natoms = atoms.size();
    MopacAtom a, a1, a2, a3, a_first;
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

    MopacAtom atom = atoms.get(0);
    a_first = atom;
    if (!atom.cartesian) {
      atom.setXYZ(0, 0, 0);
    }

    if (natoms < 2) {
      return;
    }

    // --- ther second atom: put it along the X-axis

    atom = atoms.get(1);
    if (!atom.cartesian) {
      atom.setXYZ(a_first.xyz[0] + atom.getBondLength() * factor, a_first.xyz[1] + 0, a_first.xyz[2] + 0);
    }

    if (natoms < 3) {
      return;
    }

    // --- Third atom (put it into the XOY plane

    a = atoms.get(2);
    if (!atom.cartesian) {

      x1 = new float[3];
      int index = a.get_i1();
      a1 = atoms.get(index);
      x1[0] = a1.getX();
      x1[1] = a1.getY();
      x1[2] = a1.getZ();

      xyz[2] = a_first.xyz[2] + 0.0f; // Z-coordinate

      if (index == 0) { // Connected to the first atom
        //logger.info("To the 1st");
        xyz[0] = a_first.xyz[0] + a.getBondLength() * factor *
            (float) Math.cos( (double) (a.getAlpha() * DEGREES_TO_RADIANS)); // So, we put it along X-axis
        xyz[1] = a_first.xyz[1] + a.getBondLength() * factor *
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
    }

    // Other atoms

    for (i = 3; i < natoms; i++) {

      a = atoms.get(i);

      if (atom.cartesian) {
        continue;
      }

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
      if (R2 == 0.0) {
        R2 = 0.0001;
      }

      V3[0] = V1[1] * V2[2] - V1[2] * V2[1];
      V3[1] = V1[2] * V2[0] - V1[0] * V2[2];
      V3[2] = V1[0] * V2[1] - V1[1] * V2[0];

      R3 = Math.sqrt(V3[0] * V3[0] + V3[1] * V3[1] + V3[2] * V3[2]);
      if (R3 == 0.0) {
        R3 = 0.0001;
      }

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

  public void parseGaussianGeometry(List<String> geometry, BufferedReader in) throws Exception {
    // The information contained in the Gaussian Z-matrix is identical to that in a MOPAC Z-matrix,
    // but the order of presentation is different. Atom N, (real or dummy) is specified in the format:
    // Element N1 Length N2 Alpha N3 Beta
    // where Element is the same as for the MOPAC Z-matrix. N1, N2, and N3 are the connectivity,
    // the same as the MOPAC Z-matrix NA, NB, and NC: bond lengths are between N and N1,
    // angles are between N, N1 and N2, and dihedrals are between N, N1, N2, and N3.
    // The same rules apply to N1, N2, and N3 as to NA, NB, and NC.

    // Length, Alpha, and Beta are the bond lengths, the angle, and dihedral.
    // They can be 'real', e.g. 1.45, 109.4, 180.0, or 'symbolic'.
    // A symbolic is an alphanumeric string of up to 8 characters,
    // e.g. R51, A512, D5213, CH, CHO, CHOC, etc.
    // Two or more symbolics can be the same. Dihedral symbolics can optionally be preceded by a minus sign,
    //  in which case the value of the dihedral is the negative of the value of the symbolic.
    // This is the equivalent of the normal MOPAC SYMMETRY operations 1, 2, 3, and 14.

    // If an internal coordinate is real, it will not be optimized.
    // This is the equivalent of the MOPAC optimization flag "0".
    // If an internal coordinate is symbolic, it can be optimized.

    // The Z-matrix is terminated by a blank line, after which comes the starting values of the symbolics,
    // one per line. If there is a blank line in this set,
    // then all symbolics after the blank line are considered fixed; that is, they will not be optimized.
    // The set before the blank line will be optimized.

    String line = null, token;

    // Comment lines are already removed...

    for (int i = 0; i < geometry.size(); i++) {
      line = geometry.get(i);

      String label = this.getAtomLabel(line);
      line = this.stripAtomLabel(line).toUpperCase();
      StringTokenizer st = new StringTokenizer(line, " \t,");

      // --- Process Tv

      if (label.equalsIgnoreCase("TV")) {
        yesTv = true;
        if (originalTv.size() >= 3) {
          throw new Exception("Should be no more than 3 translation vectors");
        }
        originalTv.add(line);
      }
      else if (yesTv) {
        throw new Exception("No real or dummy atoms should come after a Tv");
      }

      MopacAtom atom = new MopacAtom();
      atom.cartesian = false;
      parseElement(label, atom);

      if (Atoms.size() == 0) { // The first atom
        Atoms.add(atom);
        continue;
      }

      if (Atoms.size() == 1 && st.countTokens() < 2) {
        throw new Exception("The second atom in Gaussian Z-matrix should have at least 3 tokens. Got: " + line);
      }

      // --- reading reference atom A

      token = st.nextToken();
      try {
        atom.i1 = Integer.parseInt(token) - 1;
        if (atom.i1 < 0 || atom.i1 >= Atoms.size()) {
          throw new Exception("Wrong value for N1: " + (atom.i1 + 1) + " Should be between 1 and " + Atoms.size());
        }
      }
      catch (Exception ex) {
        throw new Exception("Cannot parse N1 for " + (Atoms.size() + 1) + " atom\nInput line: " + line + "\nError message: " +
                            ex.getMessage());
      }

      // --- Reading Bond Length

      token = st.nextToken();

      boolean isNumber = true;
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

      if (Atoms.size() == 1) { // The second atom
        Atoms.add(atom);
        continue;
      }

      if (st.countTokens() < 2) {
        throw new Exception("Expecting NB and alpha. Got: " + line);
      }

      // --- Reading reference Atom B

      token = st.nextToken();
      try {
        atom.i2 = Integer.parseInt(token) - 1;
        if (atom.i2 < 0 || atom.i2 >= Atoms.size() || atom.i2 == atom.i1) {
          throw new Exception("Wrong value for N2: " + (atom.i2 + 1) + " Should be between 1 and " + Atoms.size() +
                              " and not equal to " + (atom.i1 + 1));
        }
      }
      catch (Exception ex) {
        throw new Exception("Cannot parse N2 for " + (Atoms.size() + 1) + " atom\nInput line: " + line + "\nError message: " +
                            ex.getMessage());
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

      // --- Special case - the third atom

      if (Atoms.size() == 2) {
        Atoms.add(atom);
        continue;
      }

      // --- Reading reference atom N3

      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting reference atom parameter N3");
      }
      token = st.nextToken();
      try {
        atom.i3 = Integer.parseInt(token) - 1;
        if (atom.i3 < 0 || atom.i3 >= Atoms.size() || atom.i3 == atom.i1 || atom.i3 == atom.i2) {
          throw new Exception("Wrong value for N3: " + (atom.i3 + 1) + " Should be between 1 and " + Atoms.size() +
                              " and not equal to " + (atom.i1 + 1) + " and " + (atom.i2 + 1));
        }
      }
      catch (Exception ex) {
        throw new Exception("Cannot parse N3 for " + (Atoms.size() + 1) + " atom\nInput line: " + line + "\nError message: " +
                            ex.getMessage());
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
    } // --- End of parsing geometry

    // --- Resolve Parameters

    if (symbolicStrings.size() > 0) {
      logger.info("Expecting " + symbolicStrings.size() +
                         " unique symbolic strings");

      // --- Start to read symbolic strings

      int symbolicsFound = 0;
      boolean optimizedSymbolics = true;

      while ( (line = in.readLine()) != null) {
        line = line.trim().toUpperCase();

        if (line.startsWith("*")) {
          continue;
        }

        if (line.length() == 0) { // blank line
          if (optimizedSymbolics) {
            logger.info("Finished to read symbolics to optimize");
            optimizedSymbolics = false;
            if (isReadAllSymbolics(symbolicStrings)) {
              break;
            }
            continue;
          }

          if (!isReadAllSymbolics(symbolicStrings)) {
            throw new Exception("Found blank line before all symbolic's values are read in...");
          }
          break;
        }

        StringTokenizer st = new StringTokenizer(line, "=, \t");

        String currentString = st.nextToken();

        if (st.countTokens() < 1) {
          throw new Exception(
              "Expecting at least two values for symbolic strings");
        }

        // --- Read symbol

        if (!symbolicStrings.containsKey(currentString)) {
          System.err.println(
              "Warning: atom parameters do not have symbolic " +
              currentString);
          continue;
        }

        // --- Read value

        ++symbolicsFound;
        token = st.nextToken();
        try {
          Float f = new Float(token);
          symbolicStrings.put(currentString, f);
        }
        catch (Exception ex) {
          throw new Exception("Wrong value for symbolic string " + token);
        }
      }
    }

    if (!isReadAllSymbolics(symbolicStrings) && line == null) {
      throw new Exception("Found end-of-file before all symbolic's values are read in...");
    }

    if (symbolicStrings.size() > 0) {
      try {
        resolveSymbolicStrings(symbolicStrings, Atoms);
      }
      catch (Exception ex) {
        throw ex;
      }
    }

  }

  boolean isReadAllSymbolics(Map<String, Float> symbolicStrings) {

    if (symbolicStrings.containsValue(null)) {
      return false;
    }
    Set set = symbolicStrings.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String string = me.getKey().toString();
      Object value = me.getValue();

      if (value == null) {
        return false;
      }
    }
    return true;
  }

  private void resolveSymbolicStrings(Map<String, Float> symbolicStrings, List<MopacAtom> atoms) throws Exception {

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
      MopacAtom atom = atoms.get(i);

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

  private class Symmetry {
    int definingAtom;
    int symmetryRelation;
    int[] definedAtoms = null;
    float multiplyingFactor = 1.0f;
  }

  private class Tv {
    float length;
    float dir[] = new float[3];
  }
}
