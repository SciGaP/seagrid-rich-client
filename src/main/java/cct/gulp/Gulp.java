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

package cct.gulp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */

enum GULP_KEY_WORDS {
  CELL, FRACTIONAL, TITLE, VECTORS, CARTESIAN
}

public class Gulp {

  static final double ONE_BOHR = 0.529177249; // In Angstrom
  static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
  static final String GULP_SPECIES_TYPE_SPECIFIER = "Gulp-species-type-specifier";

  static Map<String, GULP_KEY_WORDS> keyWords = new HashMap<String, GULP_KEY_WORDS> ();

  static {
    keyWords.put("CELL", GULP_KEY_WORDS.CELL);
    keyWords.put("CART", GULP_KEY_WORDS.CARTESIAN);
    keyWords.put("FRAC", GULP_KEY_WORDS.FRACTIONAL);
    keyWords.put("TITL", GULP_KEY_WORDS.TITLE);
    keyWords.put("VECT", GULP_KEY_WORDS.VECTORS);
  }

  private boolean fractional = true;
  private String Title = "No title";
  private double Vectors[][] = null;
  private List<GULPAtom> Atoms = null;

  public Gulp() {
  }

  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() + " : molec == null");
    }
    if (Atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : no GULP atoms");
    }

    molec.addMonomer("GULP");

    calcAbsoluteCoordinates(Atoms, Vectors);

    for (int i = 0; i < Atoms.size(); i++) {
      GULPAtom ga = Atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.Label);
      atom.setProperty(GULP_SPECIES_TYPE_SPECIFIER, ga.getSpeciesType());
      atom.setAtomicNumber(ga.Element);

      atom.setXYZ( (float) ga.Xyz[0], (float) ga.Xyz[1], (float) ga.Xyz[2]);

      molec.addAtom(atom);
    }

  }

  public void parseInput(String file_name, int fileType) throws Exception {
    String line, token;
    BufferedReader in = null;

    if (fileType == 0) { // Read from file
      try {
        in = new BufferedReader(new FileReader(file_name));
      }
      catch (Exception ex) {
        throw ex;
      }
    }
    else if (fileType == 1) { // Read from String
      in = new BufferedReader(new StringReader(file_name));
    }
    else {
      throw new Exception(
          "parsing ADF Input: INTERNAL ERROR: Unknown file type");
    }

    try {

      while ( (line = in.readLine()) != null) {
        line = line.trim();

        if (line.length() < 1) { // Blank line
          continue;
        }

        if (line.startsWith("#")) { // comment
          continue;
        }

        //StringTokenizer st = new StringTokenizer(line.toUpperCase(), " ");
        if (line.length() > 3) {
          token = line.substring(0, 4).toUpperCase();
        }
        else {
          token = line.substring(0, line.length()).toUpperCase();
        }

        if (!keyWords.containsKey(token)) {
          continue;
        }

        GULP_KEY_WORDS keyWord = keyWords.get(token);

        switch (keyWord) {
          case TITLE:
            Title = parseTITLE(line, in);
            break;
          case VECTORS:
            Vectors = parseVECTORS(line, in);
            break;
          case CELL:
            Vectors = parseCELL(line, in);
            break;

          case CARTESIAN:
          case FRACTIONAL:
            Atoms = this.parseCoordinates(line, in);
            break;
          default:
            break;
        }

      }

      in.close();
    }
    catch (Exception ex) {
      throw new Exception("Error while reading GULP input file: " + ex.getMessage());
    }

    if (Atoms.size() < 1) {
      throw new Exception("No atoms found in GULP input file");
    }

  }

  public String parseTITLE(String title, BufferedReader in) throws Exception {
    StringTokenizer st = new StringTokenizer(title, " ");
    String line;
    String gulp_title = "";
    if (st.countTokens() == 1) { // Read until end

      while ( (line = in.readLine()) != null) {
        line = line.trim();
        if (line.toUpperCase().startsWith("END")) {
          return gulp_title;
        }
        if (gulp_title.length() > 0) {
          gulp_title += "\n";
        }
        gulp_title += line;
      }
    }

    else if (st.countTokens() > 1) { // Read n lines
      st.nextToken();
      String token = st.nextToken();
      try {
        int n = Integer.parseInt(token);
        if (n < 0) {
          throw new Exception("Number of title lines should be positive number: " + title);
        }
        for (int i = 0; i < n; i++) {
          if ( (line = in.readLine()) == null) {
            return gulp_title;
          }
          if (gulp_title.length() > 0) {
            gulp_title += "\n";
          }
          gulp_title += line;
        }
      }
      catch (Exception ex) {
        throw new Exception("Cannot parse number of title lines: " + title + " : " + ex.getMessage());
      }
    }
    return gulp_title;
  }

  public double[][] parseVECTORS(String line, BufferedReader in) throws Exception {
    /*
      vectors <angs/au> x y z for vector 1 x y z for vector 2 x y z for vector 3 <6 x optimisation flags>
      Units:	Angstrom (default) or au
      Use:	Specifies the cartesian components of the lattice vectors.
      Either "vectors" or "cell" must be included. Strain optimsation flags appear on last line.
     */
    double vectors[][] = new double[3][3];
    StringTokenizer st = new StringTokenizer(line, " ");
    String token;
    double factor = 1.0;
    if (st.countTokens() > 1) {
      st.nextToken();
      token = st.nextToken().toUpperCase();
      if (token.startsWith("ANGS")) {
        factor = 1.0;
      }
      else if (token.startsWith("AU")) {
        factor = ONE_BOHR;
      }
      else {
        throw new Exception("Unknown units for vectors: " + token);
      }
    }

    for (int i = 0; i < 3; i++) {
      if ( (line = in.readLine()) == null) {
        throw new Exception("Unexpected end of file while reading vectors");
      }
      st = new StringTokenizer(line, " ");
      if (st.countTokens() < 3) {
        throw new Exception("Expected at least tokens for vectors, got " + line);
      }
      for (int j = 0; j < 3; j++) {
        token = st.nextToken();
        try {
          vectors[i][j] = Double.parseDouble(token) * factor;
        }
        catch (Exception ex) {
          throw new Exception("Cannot parse vectors data " + line + " : " + ex.getMessage());
        }
      }
    }
    return vectors;
  }

  public List<GULPAtom> parseCoordinates(String line, BufferedReader in) throws Exception {
    List<GULPAtom> atoms = new ArrayList<GULPAtom> ();

    String token, speciesType = "CORE";
    double factor = 1.0;
    double xyz[] = new double[3];
    StringTokenizer st = new StringTokenizer(line, " ");
    token = st.nextToken().toUpperCase();
    if (token.startsWith("FRAC")) {
      fractional = true;
    }
    else if (token.startsWith("CART")) {
      fractional = false;
    }
    else {
      throw new Exception("Unknown coordinate input type: " + line);
    }

    while ( (line = in.readLine()) != null) {
      line = line.trim();
      if (line.startsWith("#")) {
        continue;
      }
      if (line.length() < 1) {
        break;
      }
      st = new StringTokenizer(line, " ");
      String label = st.nextToken();

      if (label.length() > 2) {
        break; // End of input
      }

      int element = parseAtomLabel(label);

      token = st.nextToken();

      // Parse cartesians

      boolean already_read = true;
      try {
        xyz[0] = Double.parseDouble(token) * factor;
      }
      catch (Exception ex) {
        already_read = false;
        speciesType = token;
      }

      if (!already_read) {
        if (!st.hasMoreTokens()) {
          throw new Exception("Unexpected end of line while reading coordinates: " + line);
        }
        token = st.nextToken();

        try {
          xyz[0] = Double.parseDouble(token) * factor;
        }
        catch (Exception ex) {
          throw new Exception("Cannot parse coordinate: " + line + " : " + ex.getMessage());
        }
      }

      for (int i = 1; i < 3; i++) {
        if (!st.hasMoreTokens()) {
          throw new Exception("Unexpected end of line while reading coordinates: " + line);
        }
        token = st.nextToken();
        try {
          xyz[i] = Double.parseDouble(token) * factor;
        }
        catch (Exception ex) {
          throw new Exception("Cannot parse coordinate: " + line + " : " + ex.getMessage());
        }
      }

      GULPAtom atom = new GULPAtom(label, element);
      atom.setSpeciesType(speciesType);
      if (fractional) {
        atom.setFractionalCoord(xyz);
      }
      else {
        atom.setCartesians(xyz);
      }
      atoms.add(atom);

    }
    return atoms;
  }

  public int parseAtomLabel(String label) throws Exception {
    int element = 0;

    try {
      element = Integer.parseInt(label);
      if (element < 0 || element > ChemicalElements.getNumberOfElements()) {
        throw new Exception("Wrong element number " + label);
      }
      return element;
    }
    catch (Exception ex) {
      // Label is not a number
    }

    if (label.length() == 1 || label.length() == 2) {
      element = ChemicalElements.getAtomicNumber(label);
      return element;
    }
    throw new Exception("Cannot parse atomic label " + label);
  }

  public void calcAbsoluteCoordinates(List<GULPAtom> atoms, double vectors[][]) throws Exception {

    double norm_vectors[][] = new double[3][3];
    if (vectors != null) {
      for (int i = 0; i < 3; i++) {
        double sum = Math.sqrt(vectors[i][0] * vectors[i][0] + vectors[i][1] * vectors[i][1] + vectors[i][2] * vectors[i][2]);
        norm_vectors[i][0] = vectors[i][0] / sum;
        norm_vectors[i][1] = vectors[i][1] / sum;
        norm_vectors[i][2] = vectors[i][2] / sum;
      }
    }
    else {
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          norm_vectors[i][j] = 0.0;
        }
        norm_vectors[i][i] = 1.0;
      }
    }

    for (int i = 0; i < Atoms.size(); i++) {
      GULPAtom ga = Atoms.get(i);
      if (ga.Cartesians != null) {
        ga.Xyz[0] = ga.Cartesians[0] * norm_vectors[0][0] + ga.Cartesians[1] * norm_vectors[0][1] +
            ga.Cartesians[2] * norm_vectors[0][2];
        ga.Xyz[1] = ga.Cartesians[0] * norm_vectors[1][0] + ga.Cartesians[1] * norm_vectors[1][1] +
            ga.Cartesians[2] * norm_vectors[1][2];
        ga.Xyz[2] = ga.Cartesians[0] * norm_vectors[2][0] + ga.Cartesians[1] * norm_vectors[2][1] +
            ga.Cartesians[2] * norm_vectors[2][2];
      }
      else if (ga.Fractional != null) {
        ga.Xyz[0] = ga.Fractional[0] * vectors[0][0] + ga.Fractional[1] * vectors[0][1] +
            ga.Fractional[2] * vectors[0][2];
        ga.Xyz[1] = ga.Fractional[0] * vectors[1][0] + ga.Fractional[1] * vectors[1][1] +
            ga.Fractional[2] * vectors[1][2];
        ga.Xyz[2] = ga.Fractional[0] * vectors[2][0] + ga.Fractional[1] * vectors[2][1] +
            ga.Fractional[2] * vectors[2][2];
      }
      else {
        throw new Exception("Neither cartesian nor fractional coordinates are set for " + (i + 1) + " atoms");
      }
    }
  }

  public double[][] parseCELL(String line, BufferedReader in) throws Exception {
    /*
      cell <angs/au> a b c alpha beta gamma <6 x optimisation flags>
     Units:	Angstrom (default) or au for a, b, c and degrees for angles
     Use:	Crystallographic unit cell. Either "vectors" or "cell" must be given.
     For optimisations or fitting, flags must be set unless cellonly, conp or conv are specified.
     */
    double a, b, c, alpha, beta, gamma;
    StringTokenizer st = new StringTokenizer(line, " ");
    String token;
    double factor = 1.0;
    if (st.countTokens() > 1) {
      st.nextToken();
      token = st.nextToken().toUpperCase();
      if (token.startsWith("ANGS")) {
        factor = 1.0;
        token = st.nextToken();
      }
      else if (token.startsWith("AU")) {
        factor = ONE_BOHR;
        token = st.nextToken();
      }
      else {
        throw new Exception("Expected cell parameters, got " + line);
      }
    }

    // --- Read next line with cell parameters

    while ( (line = in.readLine()) != null) {
      line = line.trim();
      if (line.startsWith("#")) {
        continue;
      }
      else {
        break;
      }
    }

    st = new StringTokenizer(line, " ");
    token = st.nextToken();

    // --- Get a

    try {
      a = Double.parseDouble(token) * factor;
    }
    catch (Exception ex) {
      throw new Exception("Cannot parse a in cell: " + line + " : " + ex.getMessage());
    }

    // --- Get b

    token = st.nextToken();
    try {
      b = Double.parseDouble(token) * factor;
    }
    catch (Exception ex) {
      throw new Exception("Cannot parse b in cell: " + line + " : " + ex.getMessage());
    }

    // --- Get c

    token = st.nextToken();
    try {
      c = Double.parseDouble(token) * factor;
    }
    catch (Exception ex) {
      throw new Exception("Cannot parse c in cell: " + line + " : " + ex.getMessage());
    }

    // --- Get alpha

    token = st.nextToken();
    try {
      alpha = Double.parseDouble(token) * DEGREES_TO_RADIANS;
    }
    catch (Exception ex) {
      throw new Exception("Cannot parse alpha in cell: " + line + " : " + ex.getMessage());
    }

    // --- Get beta

    token = st.nextToken();
    try {
      beta = Double.parseDouble(token) * DEGREES_TO_RADIANS;
    }
    catch (Exception ex) {
      throw new Exception("Cannot parse beta in cell: " + line + " : " + ex.getMessage());
    }

    // --- Get gamma

    token = st.nextToken();
    try {
      gamma = Double.parseDouble(token) * DEGREES_TO_RADIANS;
    }
    catch (Exception ex) {
      throw new Exception("Cannot parse gamma in cell: " + line + " : " + ex.getMessage());
    }

    // --- Get crystal to Cartesian transformation matrix

    double[][] transform = getCrystalToCartesianMatrix(a, b, c, alpha, beta, gamma);

    return transform;
  }

  public double[][] getCrystalToCartesianMatrix(double a, double b, double c, double alpha, double beta, double gamma) {
    double[][] matrix = new double[3][3];

    double factor = Math.sqrt(1.0 - Math.cos(alpha) * Math.cos(alpha) - Math.cos(beta) * Math.cos(beta) -
                              Math.cos(gamma) * Math.cos(gamma) + 2.0 * Math.cos(alpha) * Math.cos(beta) * Math.cos(gamma));

    matrix[0][0] = a;
    matrix[0][1] = b * Math.cos(gamma);
    matrix[0][2] = c * Math.cos(beta);

    matrix[1][0] = 0;
    matrix[1][1] = b * Math.sin(gamma);
    matrix[1][2] = c * (Math.cos(alpha) - Math.cos(beta) * Math.cos(gamma)) / Math.sin(gamma);

    matrix[2][0] = 0;
    matrix[2][1] = 0;
    matrix[2][2] = c * factor / Math.sin(gamma);

    return matrix;
  }

  public static void main(String[] args) {
    Gulp gulp = new Gulp();
  }
}
