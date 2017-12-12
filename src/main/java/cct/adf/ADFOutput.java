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
package cct.adf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.OutputResultsInterface;
import cct.modelling.GeneralMolecularDataParser;
import cct.modelling.StructureManagerInterface;
import cct.modelling.VIBRATIONAL_SPECTRUM;

/**
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class ADFOutput extends GeneralMolecularDataParser
    implements OutputResultsInterface, StructureManagerInterface {

  static private final String scfEnergy = "Total Energy";
  private List<ADFOutputSection> sections = new ArrayList<ADFOutputSection>();
  private Map<String, ArrayList> termVsStrReference = new HashMap<String, ArrayList>();
  private boolean debug = true;
  private boolean normalTermination = true;
  private boolean skipBody = false; // Change with care!
  private boolean skipSectionWithoutGeometry = true;
  private Set<String> propertiesToChart = new HashSet<String>();
  private int selectedSection = 0;
  static final Logger logger = Logger.getLogger(ADFOutput.class.getCanonicalName());

  public ADFOutput() {
  }

  public void selectSection(int n) {
    selectedSection = n;
  }

  @Override
  public boolean isNormalTermination() {
    return normalTermination;
  }

  public int validFormatScore(BufferedReader in) throws Exception {
    ADFOutput adf = new ADFOutput();
    try {
      adf.parseData(in);
    } catch (Exception ex) {
      return 0;
    }

    if (adf.getMolecule() == null || adf.getMolecule().getNumberOfAtoms() == 0) {
      return 0;
    }
    return 10;
  }

  public void parseData(BufferedReader in) throws Exception {
    if (true) {
      throw new Exception("parseData for ADF output is not implemented yet");
    }
    MoleculeInterface mol = getMoleculeInterface();
    this.addMolecule(mol);

    try {
      while (parseSection(in)) {
      }
    } catch (Exception ex) {
      System.err.println("It was error while parsing ADF output file: " + ex.getMessage());
      throw new Exception("Error parsing ADF output: " + ex.getMessage());
    }
  }

  public ADFOutputSection getSection(int section_number) throws Exception {
    if (section_number >= sections.size() || section_number < 0) {
      throw new Exception(this.getClass().getCanonicalName() + " : wrong section number is requested " + section_number);
    }
    return sections.get(section_number);
  }

  public void getMolecularInterface(MoleculeInterface molec, int section_number) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() + " : molec == null");
    }

    ADFOutputSection section = getSection(section_number);

    if (!section.hasGeometry()) {
      throw new Exception(this.getClass().getCanonicalName() + " : section " + section_number + " does not contain geometry");
    }

    List<ADFSnapshot> snapshots = section.getSnapshots();
    ADFSnapshot snapshot = snapshots.get(snapshots.size() - 1); // Take the last snapshot
    if (debug) {
      logger.info("getMolecularInterface: snapshot " + (snapshots.size() - 1));
    }

    List<ADFAtom> atoms = snapshot.getAtoms();

    if (atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : no ADF atoms in snapshot");
    }

    molec.addMonomer("ADF");

    for (int i = 0; i < atoms.size(); i++) {
      ADFAtom ga = atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.name);
      atom.setAtomicNumber(ga.atomicNumber);
      atom.setXYZ(ga.xyz[0], ga.xyz[1], ga.xyz[2]);
      /*
      if (ga.FF_Label != null) {
      atom.setProperty(FF_LABEL_PROP, ga.FF_Label);
      }
      
      if (ga.MM_Type != null) {
      atom.setProperty(MM_TYPE_PROP, ga.MM_Type);
      }
       */
      molec.addAtom(atom);
    }

  }

  /**
   * Parses ADF output file
   *
   * @param fileName String - file name
   * @throws Exception
   */
  public void parseFile(String fileName) throws Exception {

    try {
      BufferedReader in = new BufferedReader(new FileReader(fileName));

      try {
        while (parseSection(in)) {
        }
      } catch (Exception ex) {
        System.err.println("It was error while parsing ADF output file: " + ex.getMessage());
        return;
      }
    } catch (Exception ex) {
      throw new Exception("Cannot open file " + fileName + " : " + ex.getMessage());
    }
  }

  public boolean parseSection(BufferedReader in) throws Exception {
    String line;

    if (skipBody) { // Look for log information only
      ADFOutputSection section = new ADFOutputSection();
      return parseLogInfo(section, in);
    }

    // --- Looking for start of the output section
    boolean condition = false;
    while ((line = in.readLine()) != null) {
      if (line.startsWith(" *   Amsterdam Density Functional  (ADF)")) {
        condition = true;
        break;
      }
    }
    if (!condition) {
      return false;
    }

    ArrayList<ADFAtom> initialGeometry = null;
    ADFOutputSection section = new ADFOutputSection();
    section.addSummary(line);

    while ((line = in.readLine()) != null) {
      if (line.startsWith(" NORMAL TERMINATION")) { // Do nothing
        //break;
      } else if (line.startsWith(" G E O M E T R Y  ***  3D  Molecule  ***")) {
        initialGeometry = parseInitialGeometry(in);
      } else if (line.startsWith(" (LOGFILE)")) {
        parseLogInfo(section, in);
        if (skipSectionWithoutGeometry) {
          if (section != null && section.hasGeometry()) {
            sections.add(section);
          } else if (section != null && (!section.hasGeometry()) && initialGeometry != null) {
            ADFSnapshot snapshot = new ADFSnapshot();
            snapshot.setAtoms(initialGeometry);
            section.addSummary("Initial Geometry");
            section.addSnapshot(snapshot);
            sections.add(section);
          }
        } else if (section != null) {
          sections.add(section);
        }
        return true;
      } else if (line.contains("Vibrations and Normal Modes")) {
        ArrayList<ADFFrequency> freqs = parseFrequenciesInfo(in);
        section.addFrequencies(freqs);
      } else if (line.contains("*  R U N   T Y P E :")) {
        section.addSummary(line);
      } else if (line.startsWith(" SCF CONVERGED")) {
      }

    }

    return true;
  }

  private ArrayList<ADFFrequency> parseFrequenciesInfo(BufferedReader in) throws Exception {
    /*
    Vibrations and Normal Modes  ***  (cartesian coordinates, NOT mass-weighted)  ***
    ===========================
    
    The headers on the normal mode eigenvectors below give the Frequency in cm-1
    (a negative value means an imaginary frequency, no output for (almost-)zero frequencies)
    
    
    
     */

    // --- skip 6 lines
    String line;
    for (int i = 0; i < 6; i++) {
      if ((line = in.readLine()) == null) {
        throw new Exception("Unexpected end of file while reading frequencies info");
      }
    }

    // --- Start to read freqiencies and displacement vectors

    /*
    -194.118                       161.727                       199.897
    ------------------------      ------------------------      ------------------------
    1.N          0.000   0.000   0.000         0.000   0.000  -0.170         0.000  -0.076   0.000
    2.Cr         0.000   0.000   0.000         0.000   0.000   0.175         0.000   0.000   0.000
    3.N          0.000   0.000   0.000         0.000   0.000  -0.170        -0.065   0.038   0.000
    4.N          0.000   0.000   0.000         0.000   0.000  -0.170         0.065   0.038   0.000
    5.H          0.000  -0.408   0.000        -0.212   0.000  -0.320         0.000   0.405   0.000
    6.H          0.000   0.408   0.000         0.212   0.000  -0.320         0.000   0.405   0.000
    7.H         -0.354  -0.204   0.000        -0.106   0.183  -0.320        -0.351  -0.202   0.000
    8.H          0.354   0.204   0.000         0.106  -0.183  -0.320        -0.351  -0.202   0.000
    9.H         -0.354   0.204   0.000         0.106   0.183  -0.320         0.351  -0.202   0.000
    10.H          0.354  -0.204   0.000        -0.106  -0.183  -0.320         0.351  -0.202   0.000
    
    
    
     */
    float[] vector = new float[3];
    ArrayList<ADFFrequency> freqs = new ArrayList<ADFFrequency>();
    while (true) {
      // --- Read frequency's values
      if ((line = in.readLine()) == null) {
        throw new Exception("Unexpected end of file while reading frequencies values");
      }
      StringTokenizer st = new StringTokenizer(line, " \t");
      if (st.countTokens() < 1) { // End of freq info
        break;
      }

      String token;
      int num_freqs = st.countTokens();
      ADFFrequency[] temp_freq = new ADFFrequency[num_freqs];

      // --- parse values
      for (int i = 0; i < num_freqs; i++) {
        token = st.nextToken();
        try {
          double value = Double.parseDouble(token);
          temp_freq[i] = new ADFFrequency(value);
        } catch (Exception ex) {
          throw new Exception("Error while parsing frequencies values. Got " + line + " : " + ex.getMessage());
        }
      }

      // --- Skip line
      if ((line = in.readLine()) == null) {
        throw new Exception("Unexpected end of file after reading frequencies values");
      }

      // --- Read vectors
      while (true) {
        if ((line = in.readLine()) == null) {
          throw new Exception("Unexpected end of file while reading vectors");
        }
        line = line.trim();

        // --- Check for end of vectors (two blank lines)
        if (line.length() < 1) { // First blank line
          // --- Skip line
          if ((line = in.readLine()) == null) {
            throw new Exception("Unexpected end of file while reading blank line after vectors");
          }
          break;
        }

        // --- Parse vectors
        // 1.N          0.000   0.000   0.000         0.000   0.000  -0.170         0.000  -0.076   0.000
        st = new StringTokenizer(line, " \t");

        if (st.countTokens() < num_freqs * 3 + 1) {
          throw new Exception("Expected at least " + (num_freqs * 3 + 1) + " tokens for reading vectors, got " + line);
        }

        // --- Parse atom name
        String atom_name = st.nextToken();
        if (atom_name.contains(".")) {
          atom_name = atom_name.substring(atom_name.indexOf(".") + 1);
        }

        // --- Parse vector's values
        try {
          for (int i = 0; i < num_freqs; i++) {
            for (int j = 0; j < 3; j++) {
              vector[j] = Float.parseFloat(st.nextToken());
            }
            temp_freq[i].addVector(atom_name, vector[0], vector[1], vector[2]);
          }
        } catch (Exception ex) {
          throw new Exception("Error while parsing vector's values. Got " + line);
        }
      }

      // --- Add frequencies to the container
      for (int i = 0; i < num_freqs; i++) {
        freqs.add(temp_freq[i]);
      }
    }
    return freqs;
  }

  public boolean parseLogInfo(ADFOutputSection section, BufferedReader in) throws Exception {
    String line, token;
    boolean condition = false;

    // --- Find " (LOGFILE)"
    /*
    while ( (line = in.readLine()) != null) {
    if (line.startsWith(" (LOGFILE)")) {
    condition = true;
    break;
    }
    }
    if (!condition) {
    return false;
    }
     */
    ADFSnapshot snapshot = null;

    boolean read_header = true;
    while ((line = in.readLine()) != null) {

      if (read_header) {
        //section = new ADFOutputSection();
        section.addSummary(line);
        read_header = false;
        continue;
      }

      if (line.contains(">  END")) {
        read_header = true;
        continue;
      }

      line = line.trim();
      if (line.length() < 1) {
        sections.add(section);
        return true;
      }

      if (line.startsWith("Coordinates in Geometry Cycle") || line.startsWith("Coordinates in Frequency Displacement")) {
        /*
        Coordinates in Geometry Cycle 1
        Atom         X           Y           Z   (Angstrom)
        1.H         0.000000    0.000000    1.700000
        2.C         0.000000    0.000000    0.700000
        3.C         0.000000    0.000000   -0.700000
        4.H         0.000000    0.000000   -1.700000
        <Mar20-2008> <15:54:59>  >>>> CORORT
        
         */
        // Skip a line
        if ((line = in.readLine()) == null) {
          break;
        }
        snapshot = new ADFSnapshot();
        ArrayList<ADFAtom> atoms = new ArrayList<ADFAtom>();
        while (true) {
          line = in.readLine();
          if (line == null || line.contains(">>>>")) {
            break;
          }
          ADFAtom atom = new ADFAtom();
          StringTokenizer st = new StringTokenizer(line, " ");
          if (st.countTokens() < 4) {
            throw new Exception("Expected at least 4 tokens to parse initial coordinates, got " + line);
          }
          token = st.nextToken();
          atom.name = token.substring(token.indexOf(".") + 1);
          atom.atomicNumber = ADF.parseAtomLabel(atom.name);
          atom.Cartesian = true;

          // --- Parse X,Y,Z-coordinates
          for (int i = 0; i < 3; i++) {
            token = st.nextToken();
            float value = 0;

            try {
              value = Float.parseFloat(token);
              atom.xyz[i] = value;
            } catch (Exception ex) {
              throw new Exception("Cannot parse Cartesian coordinate, got " + line);
            }
          }
          atoms.add(atom);
        }
        snapshot.setAtoms(atoms);
        section.addSnapshot(snapshot);
      } else if (line.contains(">  E-test:  old,new=")) {
        // <Mar20-2008> <15:55:01>  E-test:  old,new=   0.00000,  -0.77752 hartree
        line = line.substring(line.indexOf("=") + 1);
        StringTokenizer st = new StringTokenizer(line, " ");
        if (st.countTokens() < 2) {
          System.err.println("Expected at least 2 tokens to get energy, got " + line);
          continue;
        }
        // --- Skip token
        st.nextToken();
        token = st.nextToken();
        try {
          double value = Double.parseDouble(token);
          propertiesToChart.add(scfEnergy);
          if (snapshot != null) {
            snapshot.addProperty(scfEnergy, value);
          }
        } catch (Exception ex) {
          System.err.println("Cannot parse total energy, got " + line);
        }
      } else if (line.contains(">  max gradient:")) {
        // <Mar20-2008> <15:55:01>  max gradient:      0.55590637 au/angstrom,radian
        line = line.substring(line.indexOf("t:") + 2);
        StringTokenizer st = new StringTokenizer(line, " ");
        if (st.countTokens() < 1) {
          System.err.println("Expected at least 1 token to get max gradient, got " + line);
          continue;
        }
        // --- Skip token
        token = st.nextToken();
        try {
          double value = Double.parseDouble(token);
          propertiesToChart.add("Max gradient");
          if (snapshot != null) {
            snapshot.addProperty("Max gradient", value);
          }
        } catch (Exception ex) {
          System.err.println("Cannot parse max gradient, got " + line);
        }
      }

    }
    return false;
  }

  public ArrayList<ADFAtom> parseInitialGeometry(BufferedReader in) throws Exception {
    /*
    G E O M E T R Y  ***  3D  Molecule  ***
    ===============
    
    
    ATOMS
    =====                            X Y Z                    CHARGE
    (Angstrom)             Nucl     +Core       At.Mass
    --------------------------    ----------------       -------
    1  H               0.0000    0.0000    1.7000      1.00      1.00        1.0078
    2  C               0.0000    0.0000    0.7000      6.00      6.00       12.0000
    3  C               0.0000    0.0000   -0.7000      6.00      6.00       12.0000
    4  H               0.0000    0.0000   -1.7000      1.00      1.00        1.0078
    
     */

    // --- Line with "G E O M E T R Y  ***  3D  Molecule  ***" is read in already... Skip 7 lines...
    String token, line;
    for (int i = 0; i < 7; i++) {
      if ((line = in.readLine()) == null) {
        throw new Exception("Unexpected end-of-file while reading initial geometry");
      }
    }
    // --- PArse geometry
    ArrayList<ADFAtom> atoms = new ArrayList<ADFAtom>();
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() < 1) {
        break;
      }
      ADFAtom atom = new ADFAtom();
      StringTokenizer st = new StringTokenizer(line, " ");
      if (st.countTokens() < 5) {
        throw new Exception("Expected at least 5 tokens to parse initial coordinates, got " + line);
      }
      // --- Skip token...
      st.nextToken();
      atom.name = st.nextToken();
      atom.atomicNumber = ADF.parseAtomLabel(atom.name);
      atom.Cartesian = true;

      // --- Parse X,Y,Z-coordinates
      for (int i = 0; i < 3; i++) {
        token = st.nextToken();
        float value = 0;

        try {
          value = Float.parseFloat(token);
          atom.xyz[i] = value;
        } catch (Exception ex) {
          throw new Exception("Cannot parse Cartesian coordinate, got " + line);
        }
      }
      atoms.add(atom);
    }
    return atoms;
  }

  public ArrayList<ADFAtom> parseCoordinates(BufferedReader in) throws Exception {
    /*
    Coordinates (Cartesian, in Input Orientation)
    =======================
    
    Atom                    bohr                                 angstrom                 Geometric Variables
    X           Y           Z              X           Y           Z       (0:frozen, *:LT par.)
    --------------------------------------------------------------------------------------------------------------
    1 Br      0.000000    0.159318    0.000000       0.000000    0.084307    0.000000      1       2       3
    2 F       0.000000   -0.097415    3.760555       0.000000   -0.051550    1.990000      4       5       6
    3 F       3.760555   -0.097415    0.000000       1.990000   -0.051550    0.000000      7       8       9
    4 F       0.000000   -0.097415   -3.760555       0.000000   -0.051550   -1.990000     10      11      12
    5 F       0.000000    3.771879    0.000000       0.000000    1.995993    0.000000     13      14      15
    6 F      -3.760555   -0.097415    0.000000      -1.990000   -0.051550    0.000000     16      17      18
    --------------------------------------------------------------------------------------------------------------
    
     */

    // --- Line with "Coordinates..." is read in already... Skip five lines...
    String token, line;
    for (int i = 0; i < 5; i++) {
      if ((line = in.readLine()) == null) {
        throw new Exception("Unexpected end-of-file while reading initial geometry");
      }
    }

    // --- Parse geometry
    ArrayList<ADFAtom> atoms = new ArrayList<ADFAtom>();
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() < 1 || line.contains("----------")) {
        break;
      }
      ADFAtom atom = new ADFAtom();
      StringTokenizer st = new StringTokenizer(line, " ");
      if (st.countTokens() < 8) {
        throw new Exception("Expected at least 8 tokens to parse initial coordinates, got " + line);
      }
      // --- Skip token...
      st.nextToken();
      atom.name = st.nextToken();
      atom.atomicNumber = ADF.parseAtomLabel(atom.name);
      atom.Cartesian = true;

      // --- Skip 3 tokens
      st.nextToken();
      st.nextToken();
      st.nextToken();

      // --- Parse X,Y,Z-coordinates
      for (int i = 0; i < 3; i++) {
        token = st.nextToken();
        float value = 0;

        try {
          value = Float.parseFloat(token);
          atom.xyz[i] = value;
        } catch (Exception ex) {
          throw new Exception("Cannot parse Cartesian coordinate, got " + line);
        }
      }
      atoms.add(atom);
    }
    return atoms;
  }

  public int countSectionsWithGeometry() {
    if (sections.size() < 1) {
      return 0;
    }
    int count = 0;
    for (int i = 0; i < sections.size(); i++) {
      ADFOutputSection section = sections.get(i);
      if (section.hasGeometry()) {
        ++count;
      }
    }
    return count;
  }

  public int[] getSectionsWithGeometry() {
    if (sections.size() < 1) {
      return null;
    }

    int count = countSectionsWithGeometry();
    if (count < 1) {
      return null;
    }

    int[] secs = new int[count];

    count = 0;
    for (int i = 0; i < sections.size(); i++) {
      ADFOutputSection section = sections.get(i);
      if (section.hasGeometry()) {
        secs[count] = i;
        ++count;
      }
    }
    return secs;
  }

  @Override
  public String[] getAvailPropToChart() {
    if (propertiesToChart.size() < 1) {
      return null;
    }
    String[] sa = new String[propertiesToChart.size()];
    propertiesToChart.toArray(sa);
    return sa;
  }

  @Override
  public double[] getAllTerms(String term) {
    if (!propertiesToChart.contains(term)) {
      return null;
    }

    if (!hasInteractiveChart()) {
      return null;
    }

    ADFOutputSection section = sections.get(selectedSection);
    if (!section.hasGeometry()) {
      return null;
    }

    List<ADFSnapshot> snapshots = section.getSnapshots();

    ArrayList<Integer> references = new ArrayList<Integer>(snapshots.size());
    ArrayList<Double> values = new ArrayList<Double>(snapshots.size());

    for (int i = 0; i < snapshots.size(); i++) {
      ADFSnapshot snapshot = snapshots.get(i);
      try {
        Double value = snapshot.getProperty(term);
        values.add(value);
        references.add(new Integer(i));
      } catch (Exception ex) {
      }
    }
    if (values.size() < 1) {
      return null;
    }

    termVsStrReference.put(term, references);
    double[] energies = new double[values.size()];
    for (int i = 0; i < values.size(); i++) {
      energies[i] = values.get(i);
    }
    return energies;
  }

  @Override
  public boolean hasJobSummary() {
    return false;
  }

  @Override
  public boolean hasInteractiveChart() {
    if (propertiesToChart.size() < 1) {
      return false;
    }

    ADFOutputSection section = sections.get(selectedSection);
    if (!section.hasGeometry()) {
      return false;
    }

    List<ADFSnapshot> snapshots = section.getSnapshots();

      return snapshots.size() >= 2;
  }

  @Override
  public boolean hasVCDSpectrum() {
    return false;
  }

  @Override
  public boolean hasUDepolSpectrum() {
    return false;
  }

  @Override
  public boolean hasPDepolSpectrum() {
    return false;
  }

  @Override
  public boolean hasRamanSpectrum() {
    return false;
  }

  @Override
  public boolean hasInfraredSpectrum() {
    return false;
  }

  @Override
  public float[][] getStructure(int n) {
    return getStructure(n, scfEnergy);
  }

  @Override
  public float[][] getStructure(int n, String term) {
    if (!propertiesToChart.contains(term)) {
      return null;
    }

    ADFOutputSection section = sections.get(selectedSection);
    List<ADFSnapshot> snapshots = section.getSnapshots();

    if (snapshots == null || snapshots.size() < 1) {
      return null;
    }

    ArrayList<Integer> references = termVsStrReference.get(term);
    if (references == null) {
      System.err.println("references == null");
      return null;
    } else if (n >= references.size()) {
      System.err.println("n >= references.size()");
      return null;
    }

    if (debug) {
      logger.info("getStructure: # " + n);
    }

    ADFSnapshot snapshot = snapshots.get(references.get(n));
    List<ADFAtom> geom = snapshot.getAtoms();

    if (geom == null || geom.size() < 1) {
      System.err.println(this.getClass().getCanonicalName() + " : no atoms in selected snapshot");
      return null;
    }

    float[][] coords = new float[geom.size()][3];
    for (int i = 0; i < geom.size(); i++) {
      ADFAtom atom = geom.get(i);

      coords[i][0] = atom.getX();
      coords[i][1] = atom.getY();
      coords[i][2] = atom.getZ();
      if (debug && i == geom.size() - 1) {
        logger.info(i + " : x=" + coords[i][0] + " y=" + coords[i][1] + " z=" + coords[i][2]);
      }

    }
    return coords;

  }

  @Override
  public void selectStructure(int number) throws Exception {
    throw new Exception(this.getClass().getCanonicalName() + ": selectStructure(int number) is not implemented yet");
  }

  @Override
  public void selectStructure(int number, String term) throws Exception {
    throw new Exception(this.getClass().getCanonicalName() + ": selectStructure(int number, String term) is not implemented yet");
  }

  @Override
  public VIBRATIONAL_SPECTRUM[] availableVibrationalSpectra() {
    return null;
  }

  @Override
  public int countFrequencies() {
    try {
      ADFOutputSection section = getSection(selectedSection);
      if (section.getFrequencies() == null) {
        return 0;
      }
      return section.getFrequencies().size();
    } catch (Exception ex) {
      return 0;
    }
  }

  @Override
  public int countSpectra() {
    return 0;
  }

  @Override
  public double getFrequency(int n) {
    try {
      ADFOutputSection section = getSection(selectedSection);
      if (section.getFrequencies() == null) {
        return 0;
      }
      ArrayList<ADFFrequency> frequencies = section.getFrequencies();
      if (n < 0 || n >= frequencies.size()) {
        return 0;
      }
      return frequencies.get(n).getFrequencyValue();
    } catch (Exception ex) {
      return 0;
    }
  }

  @Override
  public double getSpectrumValue(int n, VIBRATIONAL_SPECTRUM spectrum) {
    return 0;
  }

  @Override
  public boolean hasDisplacementVectors() {
    try {
      ADFOutputSection section = getSection(selectedSection);
      if (section.getFrequencies() == null) {
        return false;
      }
      ArrayList<ADFFrequency> frequencies = section.getFrequencies();
      if (frequencies == null || frequencies.size() < 1) {
        return false;
      }
      return frequencies.get(0).hasDisplacementVectors();
    } catch (Exception ex) {
      return false;
    }
  }

  @Override
  public float[][] getDisplacementVectors(int n) {
    try {
      ADFOutputSection section = getSection(selectedSection);
      if (section.getFrequencies() == null) {
        return null;
      }
      ArrayList<ADFFrequency> frequencies = section.getFrequencies();
      if (n < 0 || n >= frequencies.size()) {
        return null;
      }
      return frequencies.get(n).getDisplacementVectors();
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  public void getSpectrum(double[] x, double[] y, int dim, VIBRATIONAL_SPECTRUM type) throws Exception {
  }

  @Override
  public void setNormalTermination(boolean yes) {
    normalTermination = yes;
  }

  @Override
  public String getOutputResume() {
    return "No output resume";
  }

  public static void main(String[] args) {
    ADFOutput adfoutput = new ADFOutput();
  }
}
