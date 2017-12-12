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
package cct.gaussian;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.Constants;
import cct.interfaces.AtomInterface;
import cct.interfaces.ChartDataProvider;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;
import cct.modelling.MolecularGeometry;
import cct.modelling.SingleTrajectoryNew;
import cct.modelling.StructureManagerInterface;
import cct.modelling.TrajectorySnapshot;
import cct.tools.ui.JobProgressInterface;
import cct.vecmath.Point3f;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2015 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class G03MDTrajectory extends SingleTrajectoryNew
    implements StructureManagerInterface, ChartDataProvider {

  public static final String TOTAL_ENERGY_KEY = "Total energy (au)";
  private static final String STANDARD_GEOMETRY_KEY = "MD";
  public static final String TRJ_BLOCK_MARK = " TRJ-TRJ-TRJ-TRJ-TRJ-TRJ-TRJ-TRJ";
  public static final String TIME_STEP_KEY = " Time Step";
  public static final String ADMP_STEP_INFO_KEY = " Summary information for step";
  public static final String MD_STEP_KEY = "MD Step";
  public static final String TOTAL_TIME_FS_KEY = " Time (fs)";
  public static final String EKinC_KEY = "EKinC", EKinPA_KEY = "EKinPA", EKinPB_KEY = "EKinPB";
  public static final String EKinNucler_KEY = "Nuclear Kinetic Energy";
  public static final String EKinElectronic_KEY = "Electronic Kinetic Energy";
  public static final String EKin_KEY = "EKin ", EKin_PATTERN = "EKin       =";
  public static final String EPot_KEY = "EPot", EPot_PATTERN = "EPot   =";
  public static final String ETot_KEY = "ETot", ETot_PATTERN = "ETot   =";
  public static final String EKinTotal_KEY = "Total Kinetic Energy";
  public static final String EPotential_KEY = "Potential Energy";
  public static final String ETotal_KEY = "Total Energy";
  public static final String Temperature_KEY = "Temperature";
  public static final String ETot_minus_EKinP_KEY = "ETot-EKinP";
  public static final String CARTESIAN_COORD_KEY = " Cartesian coordinates:";

  private String originalFileName;
  private boolean debug = true;
  private List<GaussianAtom> gAtoms = null;
  //private List<GaussianSnapshot> snapshots = new ArrayList<GaussianSnapshot>();
  //private Set<String> propertiesToChart = new HashSet<String>();
  private double timeStep = 0.1; // in fs
  private double factor = Constants.ONE_BOHR;
  static final Logger logger = Logger.getLogger(G03MDTrajectory.class.getCanonicalName());

  public G03MDTrajectory() {
  }

  public G03MDTrajectory(MoleculeInterface mol) {
    super(mol);
  }

  public void readCoordsFromTrajFile(float coords[][], RandomAccessFile raf, long offset) throws Exception {
    String buffer = null;
    raf.seek(offset);

    if (originalFileName.toUpperCase().matches(".*[.](LOG|OUT)$")) {
      String numStr;
      for (int i = 0; i < coords.length; i++) {
        if ((buffer = raf.readLine()) == null) {
          System.err.println("Eof while reading in cartesian coordinates from a trajectory");
          break;
        }
        numStr = getNumberString(buffer, "X");
        coords[i][0] = (float) (factor * Double.parseDouble(numStr));
        numStr = getNumberString(buffer, "Y");
        coords[i][1] = (float) (factor * Double.parseDouble(numStr));
        numStr = getNumberString(buffer, "Z");
        coords[i][2] = (float) (factor * Double.parseDouble(numStr));
      }
    } else {

      for (int i = 0; i < coords.length; i++) {
        buffer = raf.readLine();
        buffer = fixAllNumbers(buffer);
        coords[i][0] = (float) (Constants.ONE_BOHR * Double.parseDouble(buffer.substring(11, 32).trim()));
        coords[i][1] = (float) (Constants.ONE_BOHR * Double.parseDouble(buffer.substring(35, 56).trim()));
        coords[i][2] = (float) (Constants.ONE_BOHR * Double.parseDouble(buffer.substring(59, 80).trim()));
      }
    }
  }

  public void parseTrajectoryFile(String fileName, JobProgressInterface progress) throws Exception {
    if (fileName == null || fileName.length() < 1) {
      logger.severe("Trajectory file name is not set");
      throw new Exception("Trajectory file name is not set");
    }
    originalFileName = fileName;
    if (fileName.toUpperCase().matches(".*[.](LOG|OUT)$")) {
      extractG09TrajectoryFromLog(fileName, progress);

    } else {
      parseG03Trajectory(fileName, progress);
    }
    System.out.println("Found " + super.getTrajectorySnapshots().size() + " MD snapshots in trajectory file");
  }

  /**
   * Contrary to any similar subroutines it returns the FIRST snapshot
   *
   * @param molec MoleculeInterface - molecule
   * @throws Exception
   */
  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() + " : molec == null");
    }
    if (gAtoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : no Gaussian atoms");
    }

    molec.addMonomer("GAUSS");

    for (int i = 0; i < gAtoms.size(); i++) {
      GaussianAtom ga = gAtoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      if (ga.name == null || ga.name.length() < 1) {
        atom.setName(ChemicalElements.getElementSymbol(ga.getElement()));
      } else {
        atom.setName(ga.name);
      }
      atom.setAtomicNumber(ga.getElement());
      atom.setXYZ(ga.xyz[0], ga.xyz[1], ga.xyz[2]);

      molec.addAtom(atom);
    }

  }

  public String[] getAvailPropToChart() {
    if (this.getDescriptors().size() < 1) {
      return null;
    }
    String[] sa = new String[getDescriptors().size()];
    getDescriptors().toArray(sa);
    return sa;
  }

  public MoleculeInterface parseMolecularGeometryOnly(MoleculeInterface mol, String filename) throws Exception {
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      return parseMolecularGeometryOnly(in, mol);
    } catch (Exception ex) {
      throw new Exception("parseMolecularGeometryOnly: ERROR opening file " + filename);
    }
  }

  public MoleculeInterface parseMolecularGeometryOnly(BufferedReader in, MoleculeInterface mol) throws Exception {
    /*
     Trajectory Starting Point:
     ---------------------------------------------------------------------
     Center     Atomic     Atomic              Coordinates (Angstroms)
     Number     Number      Type              X           Y           Z
     ---------------------------------------------------------------------
     1          6      20001001       -0.241185   -0.271881   -0.382124
     2          1      20001035       -1.177106   -0.664240   -0.014125
     3          1      20001035       -0.315056    0.809277   -0.444002
     4          1      20001035       -0.048133   -0.668818   -1.370969
     5          8      20001022        0.768355   -0.655283    0.526139
     6          1      20001031        1.633769   -0.371391    0.194276
     7          8      20001021       -4.053620   -1.712267   -3.472290
     8          1      20001030       -4.735666   -1.205766   -3.951382
     9          1      20001030       -4.373260   -2.632389   -3.570404
     10          8      20001021       -1.639883   -2.524523   -2.527601
     11          1      20001030       -0.987622   -1.996276   -3.026744
     12          1      20001030       -2.490365   -2.190708   -2.882675
     ....
     424          8      20001021        4.214621    4.206208    5.313912
     425          1      20001030        3.628498    4.920676    5.625427
     426          1      20001030        4.840445    4.696562    4.738038
     ---------------------------------------------------------------------

     */
    String line;
    try {

      // --- Finding "Trajectory Starting Point"
      while ((line = in.readLine()) != null) {
        if (line.contains("Trajectory Starting Point:")) {
          break;
        }
      }

      if (line == null) {
        throw new Exception("parseMolecularGeometryOnly: ERROR: Unxpected End of file while looking for the start of data");
      }

      // --- Skip 4 lines
      for (int i = 0; i < 4; i++) {
        line = in.readLine();
        if (line == null) {
          throw new Exception(
              "parseMolecularGeometryOnly: ERROR: Unxpected End of file while looking for the start of coordinates");
        }
      }

      // --- Reading atoms
      //    1          6      20001001       -0.241185   -0.271881   -0.382124
      while ((line = in.readLine()) != null) {
        if (line.contains("-------------------------")) {
          break;
        }

        StringTokenizer st = new StringTokenizer(line, " ");
        if (st.countTokens() != 6) {
          throw new Exception(
              "parseMolecularGeometryOnly: ERROR: coordinate input has 6 tokens\n  Got: " + line);
        }

        AtomInterface atom = mol.getNewAtomInstance();

        // --- Getting element
        // --- Skip 1 token
        st.nextToken();

        int element;
        try {
          element = Integer.parseInt(st.nextToken());
          if (element < 1) {
            element = 0;
          }
          atom.setAtomicNumber(element);
          atom.setName(ChemicalElements.getElementSymbol(element));
        } catch (Exception ex) {
          throw new Exception(
              "parseMolecularGeometryOnly: ERROR: Error while parsing atom: "
              + (mol.getNumberOfAtoms() + 1) + " Cannot parse atom number: " + line);
        }

        // --- Getting atomic type
        // --- Skip it for now
        st.nextToken();

        // --- Getting x,y,z
        float xyz;
        try {
          xyz = Float.parseFloat(st.nextToken());
          atom.setX(xyz);
          xyz = Float.parseFloat(st.nextToken());
          atom.setY(xyz);
          xyz = Float.parseFloat(st.nextToken());
          atom.setZ(xyz);
        } catch (Exception ex) {
          throw new Exception(
              "parseMolecularGeometryOnly: ERROR: Error while parsing atom: "
              + (mol.getNumberOfAtoms() + 1) + " Cannot parse atom's coordinate(s): " + line);
        }

        mol.addAtom(atom);

      }

      in.close();
    } catch (Exception ex) {
      throw new Exception("parseMolecularGeometryOnly: ERROR: " + ex.getMessage());
    }

    return mol;
  }

  static public List<GaussianAtom> parseMolecularGeometryOnly(RandomAccessFile raf) throws Exception {
    List<GaussianAtom> atoms = new ArrayList<GaussianAtom>();
    /*
     Trajectory Starting Point:
     ---------------------------------------------------------------------
     Center     Atomic     Atomic              Coordinates (Angstroms)
     Number     Number      Type              X           Y           Z
     ---------------------------------------------------------------------
     1          6      20001001       -0.241185   -0.271881   -0.382124
     2          1      20001035       -1.177106   -0.664240   -0.014125
     3          1      20001035       -0.315056    0.809277   -0.444002
     4          1      20001035       -0.048133   -0.668818   -1.370969
     5          8      20001022        0.768355   -0.655283    0.526139
     6          1      20001031        1.633769   -0.371391    0.194276
     7          8      20001021       -4.053620   -1.712267   -3.472290
     8          1      20001030       -4.735666   -1.205766   -3.951382
     9          1      20001030       -4.373260   -2.632389   -3.570404
     10          8      20001021       -1.639883   -2.524523   -2.527601
     11          1      20001030       -0.987622   -1.996276   -3.026744
     12          1      20001030       -2.490365   -2.190708   -2.882675
     ....
     424          8      20001021        4.214621    4.206208    5.313912
     425          1      20001030        3.628498    4.920676    5.625427
     426          1      20001030        4.840445    4.696562    4.738038
     ---------------------------------------------------------------------

     */
    String line;
    try {

      // --- Finding "Trajectory Starting Point"
      while ((line = raf.readLine()) != null) {
        if (line.contains("Trajectory Starting Point:")) {
          break;
        }
      }

      if (line == null) {
        throw new Exception("parseMolecularGeometryOnly: ERROR: Unxpected End of file while looking for the start of data");
      }

      // --- Skip 4 lines
      for (int i = 0; i < 4; i++) {
        line = raf.readLine();
        if (line == null) {
          throw new Exception(
              "parseMolecularGeometryOnly: ERROR: Unxpected End of file while looking for the start of coordinates");
        }
      }

      // --- Reading atoms
      //    1          6      20001001       -0.241185   -0.271881   -0.382124
      while ((line = raf.readLine()) != null) {
        if (line.contains("-------------------------")) {
          break;
        }

        StringTokenizer st = new StringTokenizer(line, " ");
        if (st.countTokens() != 6) {
          throw new Exception(
              "parseMolecularGeometryOnly: ERROR: coordinate input has 6 tokens\n  Got: " + line);
        }

        GaussianAtom atom = new GaussianAtom();

        // --- Getting element
        // --- Skip 1 token
        st.nextToken();

        int element;
        try {
          element = Integer.parseInt(st.nextToken());
          if (element < 1) {
            element = 0;
          }
          atom.element = element;
          atom.name = ChemicalElements.getElementSymbol(element);
        } catch (Exception ex) {
          throw new Exception(
              "parseMolecularGeometryOnly: ERROR: Error while parsing atom: "
              + (atoms.size() + 1) + " Cannot parse atom number: " + line);
        }

        // --- Getting atomic type
        // --- Skip it for now
        st.nextToken();

        // --- Getting x,y,z
        float xyz;
        try {
          xyz = Float.parseFloat(st.nextToken());
          atom.xyz[0] = xyz;
          xyz = Float.parseFloat(st.nextToken());
          atom.xyz[1] = xyz;
          xyz = Float.parseFloat(st.nextToken());
          atom.xyz[2] = xyz;
        } catch (Exception ex) {
          throw new Exception(
              "parseMolecularGeometryOnly: ERROR: Error while parsing atom: "
              + (atoms.size() + 1) + " Cannot parse atom's coordinate(s): " + line);
        }

        atoms.add(atom);
      }
    } catch (Exception ex) {
      throw new Exception("parseMolecularGeometryOnly: ERROR: " + ex.getMessage());
    }

    return atoms;
  }

  public void extractG09TrajectoryFromLog(String fileName) throws Exception {
    extractG09TrajectoryFromLog(fileName, null);
  }

  public void extractG09TrajectoryFromLog(String fileName, JobProgressInterface progress) throws Exception {
    //BufferedReader in = null;
    RandomAccessFile in = null;
    double numberOfAtoms = (double) super.getNumberOfAtoms();
    try {
      //in = new BufferedReader(new FileReader(fileName));
      in = new RandomAccessFile(fileName, "r");
    } catch (Exception ex) {
      throw new Exception("Cannot open file " + fileName + " : " + ex.getMessage());
    }
    // ---
    double fileLength = 0, pointer;
    long lineNumber = 0;
    long timeCurrent = 0;
    long timeInterval = 5000l;
    if (progress != null) {
      progress.setProgress(0);
      progress.setTaskDescription("Parsing G09 Trajectory Data...");
      fileLength = in.length();
      timeCurrent = System.currentTimeMillis();
    }

    // ---
    String line=null;
    boolean trajBlockFound = false;
    try {
      while ((line = in.readLine()) != null) {
        ++lineNumber;
        // --- Progress
        if (progress != null) {
          if (System.currentTimeMillis() - timeCurrent >= timeInterval) {
            pointer = in.getFilePointer();
            progress.setProgress(pointer / fileLength * 100.0);
            timeCurrent = System.currentTimeMillis();
          }
        }
        // ---
        if (line.startsWith(TRJ_BLOCK_MARK)) {
            trajBlockFound = !trajBlockFound;
          continue;
        }
        if (!trajBlockFound) {
          continue;
        }
        // --- Read in TRJ blocks
        if (line.startsWith(" ---------------")) { // Read MD run paramaters
          System.out.println("\nReading in MD parameters block\n");
          while ((line = in.readLine()) != null) {
            ++lineNumber;
            try {
              if (line.startsWith(TRJ_BLOCK_MARK)) {
                trajBlockFound = false;
                break;
              }
              System.out.println(line);
              // --- Parse MD parameters
              //if (line.startsWith(TIME_STEP_KEY)) {
              //  int index = line.indexOf("=");
              //  String numberStr = line.substring(index + 1).trim();
              //  numberStr = numberStr.substring(0, numberStr.indexOf(" ")).trim();
              //  timeStep = Double.parseDouble(numberStr);
              //  this.addData(TIME_STEP_KEY.trim(), timeStep);
              //}
            } catch (Exception ex) {
              System.err.println("Something wrong with a line: " + line);
            }
          }
          // ---------------
        } else if (line.trim().length() < 1) { // Blank line - probably admp step
          TrajectorySnapshot snapshot = new TrajectorySnapshot();
          snapshot.setReferenceTrajectory(this);
          int step = 0;
          double time = 0;
          double EKinC = 0, EKinPA = 0, EKinPB = 0, EKin = 0, EPot = 0, ETot = 0;
          String numStr;
          int index;
          while ((line = in.readLine()) != null) { // Skip until "Summary information for step"
            ++lineNumber;
            if (line.startsWith(ADMP_STEP_INFO_KEY)) {
              index = line.indexOf(ADMP_STEP_INFO_KEY) + ADMP_STEP_INFO_KEY.length();
              step = Integer.parseInt(line.substring(index).trim());
              snapshot.addProperty(ADMP_STEP_INFO_KEY, step);
              this.addData(MD_STEP_KEY, step);
              break;
            }
          }
          // -- go through MD step info

          while ((line = in.readLine()) != null) {
            ++lineNumber;
            if (line.startsWith(CARTESIAN_COORD_KEY)) {
              break;
            }
            try {
              if (line.startsWith(TOTAL_TIME_FS_KEY)) {
                index = line.indexOf(TOTAL_TIME_FS_KEY) + TOTAL_TIME_FS_KEY.length();
                time = Double.parseDouble(line.substring(index).trim());
                snapshot.addProperty(TOTAL_TIME_FS_KEY, time);
                this.addData(TOTAL_TIME_FS_KEY.trim(), time);
                continue;
              }
              // ---
              if (line.contains(EKinC_KEY)) {
                numStr = getNumberString(line, EKinC_KEY);
                EKinC = Double.parseDouble(numStr);
                snapshot.addProperty(EKinNucler_KEY, EKinC);
                this.addData(EKinNucler_KEY, EKinC);
              }
              if (line.contains(EKinPA_KEY)) {
                numStr = getNumberString(line, EKinPA_KEY);
                EKinPA = Double.parseDouble(numStr);
                snapshot.addProperty(EKinPA_KEY, EKinPA);
                this.addData(EKinPA_KEY, EKinPA);
              }
              if (line.contains(EKinPB_KEY)) {
                numStr = getNumberString(line, EKinPB_KEY);
                EKinPB = Double.parseDouble(numStr);
                snapshot.addProperty(EKinPB_KEY, EKinPB);
                this.addData(EKinPB_KEY, EKinPB);
                // --- Sum up A and B electronic kinetic energies
                snapshot.addProperty(EKinElectronic_KEY, EKinPA + EKinPB);
                this.addData(EKinElectronic_KEY, EKinPA + EKinPB);
              }
              if (line.contains(EKin_PATTERN)) {
                numStr = getNumberString(line, EKin_KEY);
                EKin = Double.parseDouble(numStr);
                snapshot.addProperty(EKinTotal_KEY, EKin);
                this.addData(EKinTotal_KEY, EKin);
                // --- Derive temperature from Ekin
                double Temp = 210535.14511 * EKin / numberOfAtoms;
                snapshot.addProperty(Temperature_KEY, Temp);
                this.addData(Temperature_KEY, Temp);
              }
              if (line.contains(EPot_PATTERN)) {
                numStr = getNumberString(line, EPot_KEY);
                EPot = Double.parseDouble(numStr);
                snapshot.addProperty(EPotential_KEY, EPot);
                this.addData(EPotential_KEY, EPot);
              }
              if (line.contains(ETot_PATTERN)) {
                numStr = getNumberString(line, ETot_KEY);
                ETot = Double.parseDouble(numStr);
                snapshot.addProperty(ETotal_KEY, ETot);
                this.addData(ETotal_KEY, ETot);
              }
            } catch (Exception ex) {
              System.err.println("Something was wrong with parsing MD step info in line: " + line + " :" + ex.getMessage());
            }
          }
          // -- Start to read cartesin coordinates
          if (!line.startsWith(CARTESIAN_COORD_KEY)) {
            System.err.println("Was expecting cartesian coordinates, got: " + line);
            break;
          }
          snapshot.setOffset(in.getFilePointer());
          float coords[][] = null;
          if (this.getNumberOfAtoms() == 0) { // Probably reference structure was not set...
            // I=   21 X=   8.508244543715D-02 Y=   8.112008191149D-02 Z=   3.006460701532D-02
            // MW Cartesian velocity:
            ArrayList<float[]> coordList = new ArrayList<float[]>();
            while ((line = in.readLine()) != null) {
              ++lineNumber;
              if (!line.startsWith(" I=")) {
                this.setNumberOfAtoms(coordList.size());
                coords = new float[getNumberOfAtoms()][3];
                for (int i = 0; i < getNumberOfAtoms(); i++) {
                  coords[i][0] = coordList.get(i)[0];
                  coords[i][1] = coordList.get(i)[1];
                  coords[i][2] = coordList.get(i)[2];
                }
                break;
              }
              float[] coord = new float[3];
              numStr = getNumberString(line, "X");
              coord[0] = (float) (factor * Double.parseDouble(numStr));
              numStr = getNumberString(line, "Y");
              coord[1] = (float) (factor * Double.parseDouble(numStr));
              numStr = getNumberString(line, "Z");
              coord[2] = (float) (factor * Double.parseDouble(numStr));
              coordList.add(coord);
            }
          } else {
            coords = new float[getNumberOfAtoms()][3];
            for (int i = 0; i < getNumberOfAtoms(); i++) {
              if ((line = in.readLine()) == null) {
                ++lineNumber;
                System.err.println("Eof while reading in cartesian coordinates from a trajectory");
                break;
              }
              numStr = getNumberString(line, "X");
              coords[i][0] = (float) (factor * Double.parseDouble(numStr));
              numStr = getNumberString(line, "Y");
              coords[i][1] = (float) (factor * Double.parseDouble(numStr));
              numStr = getNumberString(line, "Z");
              coords[i][2] = (float) (factor * Double.parseDouble(numStr));
            }
          }

          snapshot.setCoordinates(coords);
          this.addSnapshot(snapshot);

          // --- Now just read in until the end of TRJ block
          while ((line = in.readLine()) != null) {
            ++lineNumber;
            if (line.startsWith(TRJ_BLOCK_MARK)) {
              trajBlockFound = false;
              break;
            }
          }
          // ---
           if (progress != null && progress.isCanceled() ) {
             logger.warning("MD Trajectory loading was cancelled by user");
             break;
           }
        }
      }
    } catch (Exception ex) {
      throw new Exception("Error in line "+lineNumber+"\nLine: "+line+"\nError: "+ex.getMessage());
    }

    logger.info("Found " + this.getSnapshotsCount() + " snapshots");
    //if (logger.isLoggable(Level.INFO)) {
    //  this.printDataSets();
    //}
  }

  /**
   *
   * @param line - input string
   * @param keyWord - keyword for numerical value, for example, XXX
   * @return number as a string, i.e. it expects input .... XXX = number ... and will return a "number"
   * @throws Exception
   */
  public static String getNumberString(String line, String keyWord) throws Exception {
    String temp = line.substring(line.indexOf(keyWord) + keyWord.length());
    temp = temp.substring(temp.indexOf("=") + 1).trim();
    if (temp.indexOf(" ") != -1) {
      temp = fixNumberString(temp.substring(0, temp.indexOf(" ")));
    } else {
      temp = fixNumberString(temp);
    }
    if (temp.contains(";")) {
      temp = temp.replaceAll(";", "");
    }
    return temp;
  }

  public static String fixNumberString(String numStr) {
    if (numStr == null) {
      logger.warning("numStr is null. Ignored...");
      return numStr;
    }
    return numStr.replaceFirst("(D|d)", "E");
  }

  public static String fixAllNumbers(String numStr) {
    return numStr.replaceAll("(D|d)", "E");
  }

  public void parseG03Trajectory(String fileName) throws Exception {
    parseG03Trajectory(fileName, null);
  }

  public void parseG03Trajectory(String fileName, JobProgressInterface progress) throws Exception {
    if (fileName == null || fileName.length() < 1) {
      throw new Exception("Trajectory file name is not set");
    }
    if (fileName.toUpperCase().matches("*.[.](LOG|OUT)$")) {
      extractG09TrajectoryFromLog(fileName, progress);
      return;
    }
    /*
     Time in trajectory (femtosec)    2.000000D-01
     Total energy (au)  -1.175171068D+02
     Total angular momentum (h-bar)   2.904529344D-14
     Coordinates (Bohr)
     I=    1 X=  -4.555420059248D-01 Y=  -5.134698398949D-01 Z=  -7.216904569806D-01
     I=    2 X=  -2.221805396142D+00 Y=  -1.254178689967D+00 Z=  -2.596959873079D-02
     I=    3 X=  -5.952104662883D-01 Y=   1.532677098577D+00 Z=  -8.366401222195D-01
     */

    String line;
    long offset;
    RandomAccessFile raf = null;

    try {
      File f = new File(fileName);
      raf = new RandomAccessFile(f, "r");

      gAtoms = parseMolecularGeometryOnly(raf);
      if (gAtoms.size() < 0) {
        throw new Exception("Didn't find atoms in file");
      }

      /*
       Time in trajectory (femtosec)    2.000000D-01
       Total energy (au)  -1.175171068D+02
       Total angular momentum (h-bar)   2.904529344D-14
       Coordinates (Bohr)
       I=    1 X=  -4.555420059248D-01 Y=  -5.134698398949D-01 Z=  -7.216904569806D-01
       I=    2 X=  -2.221805396142D+00 Y=  -1.254178689967D+00 Z=  -2.596959873079D-02
       */
      while ((line = raf.readLine()) != null) {
        if (line.contains("Time in trajectory ")) {

          //GaussianSnapshot snapshot = new GaussianSnapshot();
          TrajectorySnapshot snapshot = new TrajectorySnapshot();

          // Add absolute time
          double value;
          String token = "";
          /*
           try {
           value = Double.parseDouble(line.substring(30, line.length() - 1).trim());
           snapshot.addProperty("Time in trajectory (femtosec)", value);
           }
           catch (Exception ex) {
           }
           */

          // parsing            Total energy (au)  -1.175171068D+02
          if ((line = raf.readLine()) == null) {
            break;
          }

          try {
            token = line.substring(18).trim().replaceAll("[D]", "E").replaceAll("[d]", "E");
            value = Double.parseDouble(token);
            snapshot.addProperty(TOTAL_ENERGY_KEY, value);
            addDescriptor(TOTAL_ENERGY_KEY);
          } catch (Exception ex) {
            System.err.println("Cannot parse total energy: " + ex.getMessage() + " got: " + token);
          }

          // Total angular momentum (h-bar)   1.155222129D-13
          if ((line = raf.readLine()) == null) {
            break;
          }
          try {
            token = line.substring(31).trim().replaceAll("[D]", "E").replaceAll("[d]", "E");
            value = Double.parseDouble(token);
            snapshot.addProperty("Total angular momentum (h-bar)", value);
            addDescriptor("Total angular momentum (h-bar)");
          } catch (Exception ex) {
            System.err.println("Cannot parse angular momentum: " + ex.getMessage() + " got: " + token);
          }

          // --- Coordinates (Bohr)
          if ((line = raf.readLine()) == null) {
            break;
          }

          if (!line.contains("Bohr")) {
            factor = 1.0;
          }

          offset = raf.getFilePointer();
          snapshot.setOffset(offset);

          MolecularGeometry geom = new MolecularGeometry();
          geom.setName(STANDARD_GEOMETRY_KEY);

          // ---Read atomic coordinates
          for (int i = 0; i < gAtoms.size(); i++) {
            if ((line = raf.readLine()) == null) {
              break;
            }
            Point3f point = new Point3f();
            String buffer = line.replaceAll("[D]", "E").replaceAll("[d]", "E");
            point.setX((float) (factor * Double.parseDouble(buffer.substring(11, 32).trim())));
            point.setY((float) (factor * Double.parseDouble(buffer.substring(35, 56).trim())));
            point.setZ((float) (factor * Double.parseDouble(buffer.substring(59, 80).trim())));
            geom.addCoordinates(point);
          }

          if (line != null) {
            //snapshot.addGeometry(STANDARD_GEOMETRY_KEY, geom);
            snapshot.setCoordinates(geom);
            this.addSnapshot(snapshot);
          }
        }

        if (line == null) {
          break;
        }

      }

    } catch (Exception e) {
    }

    try {
      raf.seek(0);
      raf.close();
    } catch (Exception e) {
    }

    logger.info("Found " + this.getSnapshotsCount() + " snapshots");
  }

  public float isThisFormat(String filename) throws Exception {

    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(filename));
    } catch (Exception e) {
      throw new Exception("Error opening file " + filename + " : " + e.getMessage());
    }

    float score = isThisFormat(in);

    try {
      in.close();
    } catch (Exception ex) {
    }

    return score;
  }

  public float isThisFormat(BufferedReader in) throws Exception {
    float score = 0;
    String line;

    while ((line = in.readLine()) != null) {

      if (score >= 1.0) {
        score = 1.0f;
        return score;
      }

      // --- MD info
      if (line.contains("TRJ-TRJ-TRJ-TRJ-TRJ-TRJ-TRJ ")) {
        score += 0.25;
        continue;
      }

      if (line.contains("Summary information for step")) {
        score += 0.25;
        continue;
      }

      if (line.contains("EKinC      =")) {
        score += 0.25;
        continue;
      }

      if (line.contains("I=    1 X=")) {
        score += 0.25;
        continue;
      }
      // ---
      if (score >= 1.0) {
        score = 1.0f;
        return score;
      }
    }

    return score;
  }

  public double[] getAllTerms(String term) {
    if (!this.hasDescriptor(term) || this.getSnapshotsCount() < 1) {
      return null;
    }

    List<Integer> references = new ArrayList<Integer>(getSnapshotsCount());
    List<Double> values = new ArrayList<Double>(getSnapshotsCount());
    for (int i = 0; i < getSnapshotsCount(); i++) {
      //GaussianSnapshot snapshot = snapshots.get(i);
      TrajectorySnapshot snapshot = this.getSnapshot(i);
      try {
        //Double value = snapshot.getProperty(term);
        Double value = snapshot.getPropertyAsDouble(term);
        values.add(value);
        references.add(new Integer(i));
      } catch (Exception ex) {
      }
    }
    if (values.size() < 1) {
      return null;
    }
    double[] energies = new double[values.size()];
    for (int i = 0; i < values.size(); i++) {
      energies[i] = values.get(i);
    }
    return energies;
  }

  public String getOriginalFileName() {
    return originalFileName;
  }

  @Override
  public float[][] getStructure(int n) {
    return getStructure(n, G03MDTrajectory.TOTAL_ENERGY_KEY);
  }

  @Override
  public float[][] getStructure(int n, String term) {
    if (!this.hasDescriptor(term) || this.getSnapshotsCount() < 1) {
      return null;
    }

    if (getSnapshotsCount() < 1) {
      return null;
    }

    if (debug) {
      logger.info("getStructure: # " + n);
    }

    //GaussianSnapshot snapshot = snapshots.get(n);
    //MolecularGeometry geom = snapshot.getGeometry(STANDARD_GEOMETRY_KEY);
    TrajectorySnapshot snapshot = this.getSnapshot(n);
    float[][] coords = snapshot.getCoordinates();

    //if (geom == null || geom.size() < 1) {
    //  System.err.println(this.getClass().getCanonicalName() + " : no atoms in selected snapshot");
    //  return null;
    //}
    //float[][] coords = new float[geom.size()][3];
    //for (int i = 0; i < geom.size(); i++) {
    //  Point3f c = geom.getCoordinates(i);
    //  coords[i][0] = c.getX();
    //  coords[i][1] = c.getY();
    //  coords[i][2] = c.getZ();
    //  if (debug && i == geom.size() - 1) {
    //    logger.info(i + " : x=" + coords[i][0] + " y=" + coords[i][1] + " z=" + coords[i][2]);
    //  }
    //}
    return coords;
  }

  /**
   * Not implemented yet...
   *
   * @param number int
   * @throws Exception
   */
  @Override
  public void selectStructure(int number) throws Exception {
    throw new Exception(this.getClass().getCanonicalName() + ": selectStructure(int number) is not implemented yet");
  }

  /**
   * Not implemented yet...
   *
   * @param number int
   * @param term String
   * @throws Exception
   */
  @Override
  public void selectStructure(int number, String term) throws Exception {
    throw new Exception(this.getClass().getCanonicalName() + ": selectStructure(int number, String term) is not implemented yet");
  }

  // -------------------------------------------------
  // --- Implementation of ChartDataProvider interface
  public double[] cdpGetDataAsDouble(String descriptor) {
    return this.getDataAsDouble(descriptor);
  }

  public float[] cdpGetDataAsFloat(String descriptor) {
    return this.getDataAsFloat(descriptor);
  }

  public int[] cdpGetDataAsInteger(String descriptor) {
    return this.getDataAsInteger(descriptor);
  }

  public String[] cdpGetDataAsString(String descriptor) {
    return this.getDataAsString(descriptor);
  }

  public Object[] cdpGetDataAsObject(String descriptor) {
    return this.getDataAsObject(descriptor);
  }

  public String cdpGetDescription() {
    return "Gaussian 09 MD file";
  }

  public Set<String> cdpGetDescriptors() {
    return this.getDescriptors();
  }

  public void cdpParseData(String filename) throws Exception {
    this.parseTrajectoryFile(filename);
  }

  /**
   * Checks format and returns score (0 to 1 range)
   *
   * @param filename
   * @return 0 - wrong format, 1 - for sure it's this format
   * @throws Exception
   */
  public float cdpIsThisFormat(String filename) throws Exception {
    return isThisFormat(filename);
  }
}
