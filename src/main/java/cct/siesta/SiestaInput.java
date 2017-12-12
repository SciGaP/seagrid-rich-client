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

package cct.siesta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import cct.Constants;
import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.math.Crystal;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class SiestaInput
    implements SiestaInterface {
  // --- based on Siesta 2.0
  public final static String BLOCK_TOKEN = "%block";

  private String systemName = "";
  private String systemLabel = "siesta";
  // Number of different atomic species in the simulation. Atoms of the same species, but with a different pseudopotential or basis set are counted as different species
  private int numberOfSpecies = 0; //
  // Number of atoms in the simulation.
  private int numberOfAtoms = 0;
  // Specify the net charge of the system
  private float netCharge = 0;
  // attice constant. This is just to define the scale of the lattice vectors.
  private double latticeConstant = 1.0;

  // --- The cell vectors are read in units of the lattice constant defined above. They are read as a matrix CELL(ixyz,ivector), each vector being one line
  private double latticeVectors[][] = {
      {
      1.0, 0.0, 0.0}, {
      0.0, 1.0, 0.0}, {
      0.0, 0.0, 1.0}
  };

  // --- Crystallographic way of specifying the lattice vectors, by giving six real numbers: the three vector modules, $a$, $b$, and $c$, and the three angles
  private double latticeParameters[] = {
      1.0, 1.0, 1.0, 90., 90., 90.};
  private boolean useLatticeParameters = true;
  private boolean defaultLatticeParameters = true;

  private Map<Integer, ChemicalSpeciesLabel> chemicalSpeciesLabels = new HashMap<Integer, ChemicalSpeciesLabel> ();
  private List<SiestaAtom> atoms = new ArrayList<SiestaAtom> ();
  private List<String> otherOptions = new ArrayList<String> ();

  // Character string to specify the format of the atomic positions in input.
  private AtomicCoordinatesFormat atomicCoordinatesFormat = AtomicCoordinatesFormat.NotScaledCartesianBohr;

  public SiestaInput() {
  }

  public static void main(String[] args) {
    SiestaInput siestainput = new SiestaInput();
  }

  public double[][] getLatticeVectors() {
    if (useLatticeParameters) {
      return latticeVectors;
    }
    else {
      double latticeVect[][] = new double[3][3];
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          latticeVect[i][j] = latticeVectors[i][j] * latticeConstant;
        }
      }
      return latticeVect;
    }
  }

  public void setLatticeConstant(double latticeConst) {
    latticeConstant = latticeConst;
  }

  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() + " : molec == null");
    }
    if (atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : no SIESTA atoms");
    }

    molec.addMonomer("SIESTA");

    float[][] coords = null;
    if (useLatticeParameters) {
      coords = calcAbsoluteCoordinates(atoms, latticeVectors);
      molec.addProperty(MoleculeInterface.LATTICE_VECTORS, latticeVectors);
    }
    else {
      double latticeVect[][] = new double[3][3];
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          latticeVect[i][j] = latticeVectors[i][j] * latticeConstant;
        }
      }
      coords = calcAbsoluteCoordinates(atoms, latticeVect);
      molec.addProperty(MoleculeInterface.LATTICE_VECTORS, latticeVect);
    }

    for (int i = 0; i < atoms.size(); i++) {
      SiestaAtom ga = atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.label);
      atom.setAtomicNumber(ga.element);

      atom.setXYZ(coords[i][0], coords[i][1], coords[i][2]);

      ChemicalSpeciesLabel label = chemicalSpeciesLabels.get(ga.species);
      atom.setProperty(SiestaInterface.SIESTA_SPECIES_LABEL, label.label);
      atom.setProperty(SiestaInterface.SIESTA_SPECIES_NUMBER, ga.species);

      molec.addAtom(atom);
    }

    if (systemName.length() > 0) {
      molec.setName(systemName);
    }
    else {
      molec.setName(systemLabel);
    }

    if (!defaultLatticeParameters) {
      molec.addProperty(MoleculeInterface.LATTICE_PARAMETERS, latticeParameters);
    }

    molec.addProperty(MoleculeInterface.ChargeProperty, new Float(netCharge));
    molec.addProperty(SiestaInterface.SIESTA_INTERFACE, this);
  }

  public float[][] calcAbsoluteCoordinates(List<SiestaAtom> atoms, double vectors[][]) throws Exception {

    if (atoms.size() < 1) {
      return null;
    }
    float[][] coords = new float[atoms.size()][3];

    for (int i = 0; i < atoms.size(); i++) {
      SiestaAtom ga = atoms.get(i);
      switch (atomicCoordinatesFormat) {
        case Fractional:
        case ScaledByLatticeVectors:

          /*
           coords[i][0] = (float) (ga.xyz[0] * vectors[0][0] + ga.xyz[1] * vectors[0][1] + ga.xyz[2] * vectors[0][2]);
           coords[i][1] = (float) (ga.xyz[0] * vectors[1][0] + ga.xyz[1] * vectors[1][1] + ga.xyz[2] * vectors[1][2]);
           coords[i][2] = (float) (ga.xyz[0] * vectors[2][0] + ga.xyz[1] * vectors[2][1] + ga.xyz[2] * vectors[2][2]);
           */
          coords[i][0] = (float) (ga.xyz[0] * vectors[0][0] + ga.xyz[1] * vectors[1][0] + ga.xyz[2] * vectors[2][0]);
          coords[i][1] = (float) (ga.xyz[0] * vectors[0][1] + ga.xyz[1] * vectors[1][1] + ga.xyz[2] * vectors[2][1]);
          coords[i][2] = (float) (ga.xyz[0] * vectors[0][2] + ga.xyz[1] * vectors[1][2] + ga.xyz[2] * vectors[2][2]);
          break;

        case Ang:
        case NotScaledCartesianAng:
          coords[i][0] = (float) ga.xyz[0];
          coords[i][1] = (float) ga.xyz[1];
          coords[i][2] = (float) ga.xyz[2];
          break;
        case Bohr:
        case NotScaledCartesianBohr:
          coords[i][0] = (float) (ga.xyz[0] * Constants.ONE_BOHR);
          coords[i][1] = (float) (ga.xyz[1] * Constants.ONE_BOHR);
          coords[i][2] = (float) (ga.xyz[2] * Constants.ONE_BOHR);
          break;

      }
      /*
                if (ga.Cartesians != null) {
         ga.Xyz[0] = ga.Cartesians[0] * norm_vectors[0][0] + ga.Cartesians[1] * norm_vectors[0][1] +
             ga.Cartesians[2] * norm_vectors[0][2];
         ga.Xyz[1] = ga.Cartesians[0] * norm_vectors[1][0] + ga.Cartesians[1] * norm_vectors[1][1] +
             ga.Cartesians[2] * norm_vectors[1][2];
         ga.Xyz[2] = ga.Cartesians[0] * norm_vectors[2][0] + ga.Cartesians[1] * norm_vectors[2][1] +
             ga.Cartesians[2] * norm_vectors[2][2];
                }
                else if (ga.Fractional != null) {
         ga.Xyz[0] = ga.Fractional[0] * vectors[0][0] + ga.Fractional[1] * vectors[0][1] + ga.Fractional[2] * vectors[0][2];
         ga.Xyz[1] = ga.Fractional[0] * vectors[1][0] + ga.Fractional[1] * vectors[1][1] + ga.Fractional[2] * vectors[1][2];
         ga.Xyz[2] = ga.Fractional[0] * vectors[2][0] + ga.Fractional[1] * vectors[2][1] + ga.Fractional[2] * vectors[2][2];
                }
                else {
         throw new Exception("Neither cartesian nor fractional coordinates are set for " + (i + 1) + " atoms");
                }
       */
    }

    return coords;
  }

  public void parseFDF(String filename, int fileType) throws Exception {
    String line, label;
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

    while ( (line = in.readLine()) != null) {
      line = line.trim();

      st = new StringTokenizer(line, " \t");
      if (st.countTokens() < 1) {
        continue;
      }

      label = st.nextToken();

      // --- Read block of data
      if (label.equalsIgnoreCase(BLOCK_TOKEN)) { // block data
        if (!st.hasMoreTokens()) {
          throw new Exception("Unexpected end of line while reading %block, got " + line);
        }
        label = st.nextToken();

        if (label.equalsIgnoreCase("ChemicalSpeciesLabel")) {
          parseChemicalSpeciesLabel(in);
        }
        else if (label.equalsIgnoreCase("LatticeVectors")) {
          parseLatticeVectors(in);
        }
        else if (label.equalsIgnoreCase("LatticeParameters")) {
          parseLatticeParameters(in);
        }
        else if (label.equalsIgnoreCase("AtomicCoordinatesAndAtomicSpecies")) {
          parseAtomicCoordinatesAndAtomicSpecies(in);
        }

        else { // just skip the block
          otherOptions.add(line);
          while ( (line = in.readLine()) != null) {
            otherOptions.add(line);
            line = line.trim().toLowerCase();
            if (line.startsWith("%endblock")) {
              break;
            }
          }
          if (line == null) {
            break; // end of file, exit main loop
          }
        }
      }

      // --- Test for different labels...
      else if (label.equalsIgnoreCase("SystemName")) {
        int n = line.indexOf(" ");
        if (n == -1) {
          continue;
        }
        systemName = line.substring(n).trim();
      }

      else if (label.equalsIgnoreCase("SystemLabel")) {
        int n = line.indexOf(" ");
        if (n == -1) {
          continue;
        }
        systemLabel = line.substring(n).trim();
      }

      else if (label.equalsIgnoreCase("NumberOfSpecies")) {
        if (!st.hasMoreTokens()) {
          throw new Exception("Unexpected end of line while reading NumberOfSpecies, got " + line);
        }
        try {
          numberOfSpecies = Integer.parseInt(st.nextToken());
        }
        catch (Exception ex) {
          throw new Exception("Error while parsing NumberOfSpecies, got " + line);
        }
      }

      else if (label.equalsIgnoreCase("NumberOfAtoms")) {
        if (!st.hasMoreTokens()) {
          throw new Exception("Unexpected end of line while reading NumberOfAtoms, got " + line);
        }
        try {
          numberOfAtoms = Integer.parseInt(st.nextToken());
          if (atoms instanceof ArrayList) {
            ( (ArrayList) atoms).ensureCapacity(numberOfAtoms);
          }
        }
        catch (Exception ex) {
          throw new Exception("Error while parsing NumberOfAtoms, got " + line);
        }
      }

      else if (label.equalsIgnoreCase("LatticeConstant")) {
        if (!st.hasMoreTokens()) {
          throw new Exception("Unexpected end of line while reading LatticeConstant, got " + line);
        }
        try {
          latticeConstant = Float.parseFloat(st.nextToken());
        }
        catch (Exception ex) {
          throw new Exception("Error while parsing LatticeConstant, got " + line);
        }
      }

      else if (label.equalsIgnoreCase("AtomicCoordinatesFormat")) {
        if (!st.hasMoreTokens()) {
          throw new Exception("Unexpected end of line while reading AtomicCoordinatesFormat, got " + line);
        }

        label = st.nextToken();

        if (label.equalsIgnoreCase("Bohr")) {
          atomicCoordinatesFormat = AtomicCoordinatesFormat.Bohr;
        }
        else if (label.equalsIgnoreCase("NotScaledCartesianBohr")) {
          atomicCoordinatesFormat = AtomicCoordinatesFormat.NotScaledCartesianBohr;
        }
        else if (label.equalsIgnoreCase("Ang")) {
          atomicCoordinatesFormat = AtomicCoordinatesFormat.Ang;
        }
        else if (label.equalsIgnoreCase("NotScaledCartesianAng")) {
          atomicCoordinatesFormat = AtomicCoordinatesFormat.NotScaledCartesianAng;
        }
        //else if (label.equalsIgnoreCase("ScaledCartesian")) {
        //   atomicCoordinatesFormat = AtomicCoordinatesFormat.ScaledCartesian;
        //}
        else if (label.equalsIgnoreCase("Fractional")) {
          atomicCoordinatesFormat = AtomicCoordinatesFormat.Fractional;
        }
        else if (label.equalsIgnoreCase("ScaledByLatticeVectors")) {
          atomicCoordinatesFormat = AtomicCoordinatesFormat.ScaledByLatticeVectors;
        }
        else {
          throw new Exception("Unknown AtomicCoordinatesFormat: " + line);
        }
      }

      else { // Line to ignore
        otherOptions.add(line);
      }
    } // --- End of main while

  }

  public void parseAtomicCoordinatesAndAtomicSpecies(String text) throws Exception {
    BufferedReader in = new BufferedReader(new StringReader(text));
    parseAtomicCoordinatesAndAtomicSpecies(in);
  }

  public void parseAtomicCoordinatesAndAtomicSpecies(BufferedReader in) throws Exception {
    String line, label;
    StringTokenizer st;
    double[] xyz = new double[3];

    while ( (line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() < 1) {
        continue;
      }

      st = new StringTokenizer(line, " \t");
      label = st.nextToken().toLowerCase();
      if (label.equals("%endblock")) {
        return;
      }

      if (st.countTokens() < 3) {
        throw new Exception("Expected at least 4 tokens to read AtomicCoordinatesAndAtomicSpecies, got " + line);
      }

      // --- Parse coordinates
      try {
        for (int i = 0; i < 3; i++) {
          if (i == 0) {
            xyz[i] = Double.parseDouble(label);
          }
          else {
            xyz[i] = Double.parseDouble(st.nextToken());
          }
        }
      }
      catch (Exception ex) {
        throw new Exception("Error while parsing AtomicCoordinatesAndAtomicSpecies coordinate, got " + line);
      }

      // --- Parse species number
      Integer number;
      try {
        number = Integer.parseInt(st.nextToken());
      }
      catch (Exception ex) {
        throw new Exception("Error while parsing AtomicCoordinatesAndAtomicSpecies number, got " + line);
      }

      if (!chemicalSpeciesLabels.containsKey(number)) {
        throw new Exception("There is no Chemical Species number " + number);
      }

      ChemicalSpeciesLabel species = chemicalSpeciesLabels.get(number);
      SiestaAtom atom = new SiestaAtom(species.atomicNumber, species.label, xyz[0], xyz[1], xyz[2], species.number);
      atoms.add(atom);
    }
  }

  private void parseLatticeParameters(BufferedReader in) throws Exception {
    String line, label;
    StringTokenizer st;

    if ( (line = in.readLine()) == null) {
      throw new Exception("Unexpected end of file while reading LatticeParameters");
    }
    st = new StringTokenizer(line, " \t");
    if (st.countTokens() < 6) {
      throw new Exception("Expected at least 6 tokens to read LatticeParameters, got " + line);
    }
    for (int j = 0; j < 6; j++) {
      try {
        latticeParameters[j] = Double.parseDouble(st.nextToken());
      }
      catch (Exception ex) {
        throw new Exception("Error while parsing LatticeParameters, got " + line);
      }
    }

    Crystal.validateLatticeParameters(latticeParameters[0], latticeParameters[1], latticeParameters[2], latticeParameters[3],
                                      latticeParameters[4], latticeParameters[5]);

    // --- Read until end of block

    while (true) {
      if ( (line = in.readLine()) == null) {
        throw new Exception("Unexpected end of file while looking for end of block");
      }
      line = line.trim().toLowerCase();
      if (line.startsWith("%endblock ")) {
        break;
      }
    }

    defaultLatticeParameters = false;
    useLatticeParameters = true;

    // --- Calculate lattice verctors

    buildLatticeVectors(latticeParameters[0] * latticeConstant, latticeParameters[1] * latticeConstant,
                        latticeParameters[2] * latticeConstant, latticeParameters[3],
                        latticeParameters[4], latticeParameters[5]);
  }

  private void buildLatticeVectors(double a, double b, double c, double alpha, double beta, double gamma) {
    alpha *= Constants.DEGREES_TO_RADIANS;
    beta *= Constants.DEGREES_TO_RADIANS;
    gamma *= Constants.DEGREES_TO_RADIANS;

    double factor = Math.sqrt(1.0 - Math.cos(alpha) * Math.cos(alpha) - Math.cos(beta) * Math.cos(beta) -
                              Math.cos(gamma) * Math.cos(gamma) + 2.0 * Math.cos(alpha) * Math.cos(beta) * Math.cos(gamma));
    /*
           latticeVectors[0][0] = a;
           latticeVectors[0][1] = b * Math.cos(gamma);
           latticeVectors[0][2] = c * Math.cos(beta);

           latticeVectors[1][0] = 0;
           latticeVectors[1][1] = b * Math.sin(gamma);
           latticeVectors[1][2] = c * (Math.cos(alpha) - Math.cos(beta) * Math.cos(gamma)) / Math.sin(gamma);

           latticeVectors[2][0] = 0;
           latticeVectors[2][1] = 0;
           latticeVectors[2][2] = c * factor / Math.sin(gamma);
     */

    latticeVectors[0][0] = a;
    latticeVectors[0][1] = 0;
    latticeVectors[0][2] = 0;

    latticeVectors[1][0] = b * Math.cos(gamma);
    latticeVectors[1][1] = b * Math.sin(gamma);
    latticeVectors[1][2] = 0;

    latticeVectors[2][0] = c * Math.cos(beta);
    latticeVectors[2][1] = c * (Math.cos(alpha) - Math.cos(beta) * Math.cos(gamma)) / Math.sin(gamma);
    latticeVectors[2][2] = c * factor / Math.sin(gamma);
  }

  private void parseLatticeVectors(BufferedReader in) throws Exception {
    String line, label;
    StringTokenizer st;

    for (int i = 0; i < 3; i++) {
      if ( (line = in.readLine()) == null) {
        throw new Exception("Unexpected end of file while reading " + (i + 1) + " LatticeVectors");
      }
      st = new StringTokenizer(line, " \t");
      if (st.countTokens() < 3) {
        throw new Exception("Expected at least 3 tokens to read parseLatticeVector, got " + line);
      }
      for (int j = 0; j < 3; j++) {
        try {
          latticeVectors[i][j] = Double.parseDouble(st.nextToken());
        }
        catch (Exception ex) {
          throw new Exception("Error while parsing LatticeVectors, got " + line);
        }

      }
    }
    useLatticeParameters = false;
  }

  public void parseChemicalSpeciesLabel(String text) throws Exception {
    BufferedReader in = new BufferedReader(new StringReader(text));
    parseChemicalSpeciesLabel(in);
  }

  public void parseChemicalSpeciesLabel(BufferedReader in) throws Exception {
    String line, label;
    StringTokenizer st;
    while ( (line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() < 1) {
        continue;
      }

      st = new StringTokenizer(line, " \t");
      label = st.nextToken().toLowerCase();
      if (label.equals("%endblock")) {
        return;
      }

      if (st.countTokens() < 2) {
        throw new Exception("Expected at least 3 tokens to read Chemical Species Labels, got " + line);
      }

      Integer number;
      try {
        number = Integer.parseInt(label);
      }
      catch (Exception ex) {
        throw new Exception("Error while parsing Chemical Species number, got " + line);
      }

      if (chemicalSpeciesLabels.containsKey(number)) {
        throw new Exception("Duplicate definition for Chemical Species number " + number + " Got " + line);
      }

      int atomicNumber = 0;
      try {
        atomicNumber = Integer.parseInt(st.nextToken());
      }
      catch (Exception ex) {
        throw new Exception("Error while parsing Chemical Species atomic number, got " + line);
      }

      ChemicalSpeciesLabel species = new ChemicalSpeciesLabel(number, atomicNumber, st.nextToken());
      chemicalSpeciesLabels.put(number, species);
    }
  }

  public void setLatticeParameters(float a, float b, float c, float alpha, float beta, float gamma) {
    latticeParameters[0] = a;
    latticeParameters[1] = b;
    latticeParameters[2] = c;
    latticeParameters[3] = alpha;
    latticeParameters[4] = beta;
    latticeParameters[5] = gamma;

    defaultLatticeParameters = false;
    useLatticeParameters = true;

    // --- Calculate lattice verctors

    buildLatticeVectors(latticeParameters[0] * latticeConstant, latticeParameters[1] * latticeConstant,
                        latticeParameters[2] * latticeConstant, latticeParameters[3],
                        latticeParameters[4], latticeParameters[5]);
  }

  public void setLatticeParameters(double a, double b, double c, double alpha, double beta, double gamma) {
    latticeParameters[0] = a;
    latticeParameters[1] = b;
    latticeParameters[2] = c;
    latticeParameters[3] = alpha;
    latticeParameters[4] = beta;
    latticeParameters[5] = gamma;

    defaultLatticeParameters = false;
    useLatticeParameters = true;

    // --- Calculate lattice verctors

    buildLatticeVectors(latticeParameters[0] * latticeConstant, latticeParameters[1] * latticeConstant,
                        latticeParameters[2] * latticeConstant, latticeParameters[3],
                        latticeParameters[4], latticeParameters[5]);
  }

  public void setSystemLabel(String name) {
    systemLabel = name;
  }

  public void setSystemName(String name) {
    systemName = name;
  }

  public void setAtomicCoordinatesFormat(AtomicCoordinatesFormat format) {
    atomicCoordinatesFormat = format;
  }

  public void setChemicalSpeciesLabels(Map<Integer, ChemicalSpeciesLabel> speciesLabels) {
    chemicalSpeciesLabels.clear();
    if (speciesLabels == null || speciesLabels.size() < 1) {
      return;
    }
    chemicalSpeciesLabels.putAll(speciesLabels);
  }

  public void setOtherOptions(List<String> other) {
    otherOptions.clear();
    if (other == null || other.size() < 1) {
      return;
    }
    otherOptions.addAll(other);
  }

  public void setOtherOptions(String other) {
    otherOptions.clear();
    if (other == null || other.length() < 1) {
      return;
    }

    String line;
    try {
      BufferedReader in = new BufferedReader(new StringReader(other));
      while ( (line = in.readLine()) != null) {
        otherOptions.add(line);
      }
      in.close();
    }
    catch (Exception ex) {
      System.err.println("setOtherOptions(String other): " + ex.getMessage());
    }
  }

  public String getOtherOptionsAsString() {
    if (otherOptions.size() < 1) {
      return "\n";
    }
    StringWriter sWriter = new StringWriter();
    for (int i = 0; i < otherOptions.size(); i++) {
      sWriter.write(otherOptions.get(i) + "\n");
    }
    return sWriter.toString();
  }

  public String getSpeciesLabelsAsString() {
    if (chemicalSpeciesLabels == null || chemicalSpeciesLabels.size() < 1) {
      return "\n";
    }
    Object[] ints = chemicalSpeciesLabels.keySet().toArray();
    Arrays.sort(ints);
    StringWriter sWriter = new StringWriter();
    for (int i = 0; i < ints.length; i++) {
      Integer number = (Integer) ints[i];
      ChemicalSpeciesLabel label = chemicalSpeciesLabels.get(number);
      sWriter.write(String.format(" %3d %2d %s\n", number, label.getAtomicNumber(), label.getSpeciesLabel()));
    }
    return sWriter.toString();
  }

  public String getAtomicCoordinatesAsString() {
    if (atoms == null || atoms.size() < 1) {

    }
    StringWriter sWriter = new StringWriter(1024);
    for (int i = 0; i < atoms.size(); i++) {
      SiestaAtom ga = atoms.get(i);
      ChemicalSpeciesLabel label = chemicalSpeciesLabels.get(ga.species);
      sWriter.write(String.format(" %12.6f  %12.6f  %12.6f  %3d     %4d  %2s\n", ga.getCoordinates()[0], ga.getCoordinates()[1],
                                  ga.getCoordinates()[2], ga.species, (i + 1), ga.getLabel()));
    }

    return sWriter.toString();
  }

  public String getAtomicCoordinatesAsString(MoleculeInterface molec) {
    if (molec == null || molec.getNumberOfAtoms() < 1) {
      return "\n";
    }

    // --- Resolve species labels

    chemicalSpeciesLabels = Siesta.updateChemicalSpeciesLabels(molec, chemicalSpeciesLabels);

    // --- Print out coordinates

    float factor = 1.0f;
    if (atomicCoordinatesFormat == AtomicCoordinatesFormat.Bohr ||
        atomicCoordinatesFormat == AtomicCoordinatesFormat.NotScaledCartesianBohr) {
      factor = (float) (1.0 / Constants.ONE_BOHR);
    }

    float[][] matrix = null;
    if (atomicCoordinatesFormat == AtomicCoordinatesFormat.Fractional ||
        atomicCoordinatesFormat == AtomicCoordinatesFormat.ScaledByLatticeVectors) {
      double[][] m = Crystal.getCartesianToFractionalTransMatrix(latticeParameters);
      matrix = new float[3][3];
      for (int i = 0; i < 3; i++) {
        matrix[i][0] = (float) m[i][0];
        matrix[i][1] = (float) m[i][1];
        matrix[i][2] = (float) m[i][2];
      }
    }

    Object obj;
    StringWriter sWriter = new StringWriter(1024);
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      obj = atom.getProperty(SiestaInterface.SIESTA_SPECIES_NUMBER);
      Integer number = (Integer) obj;
      obj = atom.getProperty(SiestaInterface.SIESTA_SPECIES_LABEL);
      switch (atomicCoordinatesFormat) {
        case Fractional:
        case ScaledByLatticeVectors:
          float[] frac = getFractionalCoordinates(atom.getX(), atom.getY(), atom.getZ(), matrix);
          sWriter.write(String.format(" %12.6f  %12.6f  %12.6f  %3d     %4d  %2s\n", frac[0], frac[1], frac[2], number, (i + 1),
                                      obj.toString()));
          break;

        case Ang:
        case NotScaledCartesianAng:
        case Bohr:
        case NotScaledCartesianBohr:

          sWriter.write(String.format(" %12.6f  %12.6f  %12.6f  %3d     %4d  %2s\n", atom.getX() * factor,
                                      atom.getY() * factor, atom.getZ() * factor, number, (i + 1), obj.toString()));
          break;
      }
    }
    return sWriter.toString();
  }

  public String getInputAsString() throws Exception {
    if (atoms.size() < 1) {
      throw new Exception("No atoms in SIESTA input");
    }
    StringWriter sWriter = new StringWriter();

    sWriter.write("SystemName " + systemName + "\n");
    sWriter.write("SystemLabel " + systemLabel + "\n");
    sWriter.write("\n");

    sWriter.write("NumberOfSpecies " + chemicalSpeciesLabels.size() + "\n");
    sWriter.write("%block ChemicalSpeciesLabel\n");
    sWriter.append(this.getSpeciesLabelsAsString());
    sWriter.write("%endblock ChemicalSpeciesLabel\n");
    sWriter.write("\n");

    sWriter.write("LatticeConstant " + latticeConstant + "\n");
    sWriter.write("%block LatticeParameters\n");
    sWriter.write("  " + latticeParameters[0] + "  " + latticeParameters[1] + "  " + latticeParameters[2] + "  " +
                  latticeParameters[3] + "  " + latticeParameters[4] + "  " + latticeParameters[5] + "\n");
    sWriter.write("%endblock LatticeParameters\n");
    sWriter.write("\n");

    sWriter.write("AtomicCoordinatesFormat " + atomicCoordinatesFormat.toString() + "\n");
    sWriter.write("NumberOfAtoms " + atoms.size() + "\n");
    sWriter.write("%block AtomicCoordinatesAndAtomicSpecies\n");
    sWriter.append(this.getAtomicCoordinatesAsString());
    sWriter.write("%endblock AtomicCoordinatesAndAtomicSpecies\n");
    sWriter.write("\n");

    for (int i = 0; i < otherOptions.size(); i++) {
      sWriter.write(otherOptions.get(i) + "\n");
    }
    return sWriter.toString();
  }

  public static void centerContentsInCell(double[][] fracCoord, int nCenters) {
    double x = 0, y = 0, z = 0;
    for (int i = 0; i < nCenters; i++) {
      x += fracCoord[i][0];
      y += fracCoord[i][1];
      z += fracCoord[i][2];
    }

    x = -x / (double) nCenters + 0.5;
    y = -y / (double) nCenters + 0.5;
    z = -z / (double) nCenters + 0.5;

    for (int i = 0; i < nCenters; i++) {
      fracCoord[i][0] += x;
      fracCoord[i][1] += y;
      fracCoord[i][2] += z;
    }
  }

  public void centerContentsInCell() throws Exception {
    double factor = 1.0;
    switch (atomicCoordinatesFormat) {
      case Fractional:
      case ScaledByLatticeVectors:
        double[][] fracCoord = new double[atoms.size()][3];
        for (int i = 0; i < atoms.size(); i++) {
          SiestaAtom ga = atoms.get(i);
          fracCoord[i][0] = ga.xyz[0];
          fracCoord[i][1] = ga.xyz[1];
          fracCoord[i][2] = ga.xyz[2];
        }
        centerContentsInCell(fracCoord, atoms.size());

        for (int i = 0; i < atoms.size(); i++) {
          SiestaAtom ga = atoms.get(i);
          ga.xyz[0] = fracCoord[i][0];
          ga.xyz[1] = fracCoord[i][1];
          ga.xyz[2] = fracCoord[i][2];
        }

        break;

      case Bohr:
      case NotScaledCartesianBohr:
        factor = 1.0 / Constants.ONE_BOHR;
      case Ang:
      case NotScaledCartesianAng:
        double[][] m = Crystal.getCartesianToFractionalTransMatrix(latticeParameters);
        double[][] frac = getFractionalCoordinates(m);
        centerContentsInCell(frac, atoms.size());
        double[][] cartesian = Crystal.getCartesianFromFractional(frac, atoms.size(), latticeVectors);
        for (int i = 0; i < atoms.size(); i++) {
          SiestaAtom ga = atoms.get(i);
          ga.xyz[0] = cartesian[i][0] * factor;
          ga.xyz[1] = cartesian[i][1] * factor;
          ga.xyz[2] = cartesian[i][2] * factor;
        }
        break;
    }
  }

  public double[][] getFractionalCoordinates(double[][] transMatrix) {

    double factor = 1.0;
    double[][] frac = new double[atoms.size()][3];

    switch (atomicCoordinatesFormat) {
      case Fractional:
      case ScaledByLatticeVectors:
        for (int i = 0; i < atoms.size(); i++) {
          SiestaAtom ga = atoms.get(i);
          frac[i][0] = ga.getX();
          frac[i][1] = ga.getY();
          frac[i][2] = ga.getZ();
        }
        break;

      case Bohr:
      case NotScaledCartesianBohr:
        factor = Constants.ONE_BOHR;
      case Ang:
      case NotScaledCartesianAng:
        for (int i = 0; i < atoms.size(); i++) {
          SiestaAtom ga = atoms.get(i);
          frac[i][0] = (ga.getX() * transMatrix[0][0] + ga.getY() * transMatrix[1][0] + ga.getZ() * transMatrix[2][0]) *
              factor;
          frac[i][1] = (ga.getX() * transMatrix[0][1] + ga.getY() * transMatrix[1][1] + ga.getZ() * transMatrix[2][1]) *
              factor;
          frac[i][2] = (ga.getX() * transMatrix[0][2] + ga.getY() * transMatrix[1][2] + ga.getZ() * transMatrix[2][2]) *
              factor;
        }
        break;
    }

    return frac;
  }

  public static float[] getFractionalCoordinates(float x, float y, float z, float[][] transMatrix) {
    float[] frac = new float[3];
    frac[0] = x * transMatrix[0][0] + y * transMatrix[1][0] + z * transMatrix[2][0];
    frac[1] = x * transMatrix[0][1] + y * transMatrix[1][1] + z * transMatrix[2][1];
    frac[2] = x * transMatrix[0][2] + y * transMatrix[1][2] + z * transMatrix[2][2];
    return frac;
  }

  class SiestaAtom {
    private double[] xyz = new double[3];
    int element = 0;
    private Integer species = 0;
    private String label;

    public SiestaAtom(int el, String label, double x, double y, double z, Integer species) {
      xyz[0] = x;
      xyz[1] = y;
      xyz[2] = z;
      element = el;
      this.species = species;
      this.label = label;
    }

    public double getX() {
      return xyz[0];
    }

    public double getY() {
      return xyz[1];
    }

    public double getZ() {
      return xyz[2];
    }

    public Integer getSpecies() {
      return species;
    }

    public void setSpecies(Integer species) {
      this.species = species;
    }

    public String getLabel() {
      return label;
    }

    public double[] getCoordinates() {
      return xyz;
    }

  }

  public String getSystemLabel() {
    return systemLabel;
  }

  public Map<Integer, ChemicalSpeciesLabel> getChemicalSpeciesLabels() {
    return chemicalSpeciesLabels;
  }

  public List<String> getOtherOptions() {
    return otherOptions;
  }
}
