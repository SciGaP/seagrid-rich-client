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

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;
import cct.modelling.GeneralMolecularDataParser;
import cct.modelling.MolecularGeometry;
import cct.modelling.MolecularProperties;
import cct.vecmath.Point3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2004</p>
 *
 * <p>
 * Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GaussianOutput extends GeneralMolecularDataParser
    implements MolecularProperties {

  static final int VERSION_UNKNOWN = 0;
  static final int VERSION_G94 = 1;
  static final int VERSION_G98 = 2;
  private static final int VERSION_G03 = 3;

  public static final String INPUT_ORIENTATION_KEY = "Input orientation";
  public static final String STANDARD_ORIENTATION_KEY = "Standard orientation";
  public static final String Z_MATRIX_ORIENTATION_KEY = "Z-Matrix orientation";

  static Pattern freqVecPattern = Pattern.compile("\\s*Atom\\s+AN\\s+X\\s+Y.*");

  static boolean Debug = true;

  private GaussianStep step = null;
  private String scfEnergy = null;
  private Set<String> availableStructures = new HashSet<String>();
  private Set<String> availableProperties = new HashSet<String>();
  private Set<String> propertiesToChart = new HashSet<String>();
  private Map<String, String> outputResume = new LinkedHashMap<String, String>();
  private List<GaussianFrequency> frequencies = new ArrayList<GaussianFrequency>();
  private boolean normalTermination = false;
  private Map<String, List> diffGeometries = new HashMap<String, List>();
  private List<GaussianSnapshot> snapshots = new ArrayList<GaussianSnapshot>();
  private GaussianMolecule gaussMol;
  private Level logLevel = Level.WARNING;
  static final Logger logger = Logger.getLogger(GaussianOutput.class.getCanonicalName());

  public GaussianOutput() {
  }

  public void setLoggerLevel(Level level) {
    logLevel = level;
    logger.setLevel(level);
    GaussianMolecule.setLoggerLevel(level);
  }

  public String[] getAvailPropToChart() {
    if (propertiesToChart.size() < 1) {
      return null;
    }
    String[] sa = new String[propertiesToChart.size()];
    propertiesToChart.toArray(sa);
    return sa;
  }

  public int getSnapshotsCount() {
    return snapshots.size();
  }

  public List<GaussianFrequency> getFrequencies() {
    return frequencies;
  }

  public boolean isNormalTermination() {
    return normalTermination;
  }

  public GaussianJob parseFile(MoleculeInterface mol, String filename, boolean update) throws Exception {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(filename));
    } catch (Exception ex) {
      throw new Exception("Cannot open file " + filename + ": " + ex.getMessage());
    }
    GaussianJob gaussianJob = new GaussianJob();
    step = null;
    int count = 1;
    do {
      MoleculeInterface molecule = mol.getInstance();
      step = parseFile(molecule, in, update);

      if (step == null) {
        break;
      }

      step.setNormalTermination(this.isNormalTermination());

      if (this.countFrequencies() > 0) {
        step.setFrequencies(this.getFrequencies());
      }
      frequencies.clear();

      GaussianKeywords gk = gaussMol.getRouteSection();
      step.setDescription(String.valueOf(count) + ": " + gk.getOptions());

      gaussMol = null;

      step.setChartProperties(propertiesToChart);
      propertiesToChart.clear();

      step.setOutputResume(outputResume);
      outputResume.clear();

      step.setSnapshots(snapshots);
      snapshots.clear();

      if (step.getMolecule().getNumberOfAtoms() < 1 && step.countFrequencies() < 1) {
        break;
      }

      gaussianJob.addStep(step);
      ++count;
    } while (!step.isFinalStep());

    try {
      in.close();
    } catch (Exception ex) {
      System.err.println("Warning: Cannot close file " + filename + ": " + ex.getMessage());
    }

    return gaussianJob;
  }

  public GaussianStep parseFile(MoleculeInterface mol, BufferedReader in, boolean update) {
    String line, token;
    String Info = new String();
    String mulliken = new String();
    String espCharges = new String();
    StringTokenizer st;
    int g_version = VERSION_UNKNOWN;
    MolecularGeometry geom = null;
    Map outputResults = new HashMap();
    step = null;
    gaussMol = new GaussianMolecule();

    List Geometries = new ArrayList();
    boolean gotCharge = false, gotMulliken = false, gotESP = false;
    Double SCF_Energy = null, S_Squared = null, ZeroPointCorr = null;
    int errorMessage = 0;
    int nBasisFuncs = 0;
    GaussianSnapshot snapshot = new GaussianSnapshot();

    Float EnergyThermCorr = null, EnthalpyThermCorr = null, GibbsThermCorr = null;

    frequencies.clear();
    propertiesToChart.clear();
    outputResume.clear();
    snapshots.clear();

    try {

      if (!update) {
        //mol = new Molecule();
      }
      while ((line = in.readLine()) != null) {
        //line.trim();
        //logger.info("Parsing line: " + line);

        if (line.contains("Gaussian") && line.contains("Revision")) {
          Info.concat(line + "\n");
          this.outputResume.put("Gaussian Rev", line);
        } else if (line.indexOf("Normal termination ") != -1) {
          normalTermination = true;
          Info.concat(line + "\n");
          if (Debug) {
            logger.info("Reached Normal termination line");
          }
          this.outputResume.put("Normal termination", line);
          /*
           if (mol.getNumberOfAtoms() == 0) {
           if (Debug) {
           logger.info("Since number of atoms = 0, continuing...");
           }
           continue;
           }
           */
          break;
        } // --- Trying to get the route section
        else if (line.startsWith(" #")) {
          gaussMol.addRouteOptions(line);
          while ((line = in.readLine()) != null && !line.startsWith(" ----------")) {
            gaussMol.addRouteOptions(line);
          }
          gaussMol.parseRouteOptions();
        } // --- Trying to get the %% section
        else if (line.startsWith(" %")) {
          try {
            gaussMol.addLinkZeroCommands(line.trim());
          } catch (Exception ex) {
            System.err.println("Getting link0 command: " + line + " : " + ex.getMessage());
          }
        } else if (line.contains("basis functions") && line.contains("primitive gaussians")) {
          this.outputResume.put("basis functions", line);
          st = new StringTokenizer(line, " ");
          if (st.countTokens() < 3) {
            continue;
          }
          token = st.nextToken();
          try {
            nBasisFuncs = Integer.parseInt(token);
            if (Debug) {
              logger.info("Number of basis functions " + nBasisFuncs);
            }
          } catch (Exception ex) {
            System.err.println("Warning: cannot parse number of basis functions " + token + ": " + ex.getMessage());
            continue;
          }
        } else if (line.contains("Step number") && line.contains("out of a maximum of")) {
          this.outputResume.put("Step number", line);
        } else if (line.contains("Error termination")) {
          outputResume.put("Error termination" + String.valueOf(errorMessage), line);
          ++errorMessage;
        } else if (line.contains("Optimization completed")) {
          this.outputResume.put("Optimization completed", line);
        } else if (line.contains("Job cpu time")) {
          this.outputResume.put("Job cpu time", "\n" + line);
        } else if (line.contains("Gaussian 09")) {
          g_version = VERSION_G03;
          if (Debug) {
            logger.info("Gaussian 09");
          }
        } else if (line.contains("Gaussian 98")) {
          g_version = VERSION_G98;
        } else if (line.contains("Gaussian 94")) {
          g_version = VERSION_G94;
        } else if (line.contains("Job cpu time")) {
          Info.concat(line + "\n");
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Job cpu time");
          }
        } // Reading in Vibration related data
        else if (line.contains("Molecular mass:")) {
          appendOutputResume(outputResume, "Vibrations-related", "\n" + line);
          while ((line = in.readLine()) != null) {
            if (line.trim().length() < 1) {
              break;
            }
            appendOutputResume(outputResume, "Vibrations-related", line);
          }
        } // Thermo-chemistry
        else if (line.contains("Zero-point correction=")) {
          String label, svalue;
          Double value;
          double Enthalpies = 0.0, FreeEnergies = 0.0;
          appendOutputResume(outputResume, "Thermochemistry", "\n" + line);
          try {
            label = line.substring(0, line.indexOf("=")).trim();
            svalue = line.substring(line.indexOf("=") + 1, line.indexOf("(")).trim();
            value = new Double(svalue);
            availableProperties.add(label);
            addToLastSnapshot(label, value);
          } catch (Exception ex) {
            logger.severe("Cannot parse " + line);
          }
          // ---
          while ((line = in.readLine()) != null) {
            if (line.trim().length() < 1) {
              break;
            }
            appendOutputResume(outputResume, "Thermochemistry", line);
            try {
              label = line.substring(0, line.indexOf("=")).trim();
              svalue = line.substring(line.indexOf("=") + 1).trim();
              value = new Double(svalue);
              availableProperties.add(label);
              addToLastSnapshot(label, value);
              if (line.contains("electronic and thermal Enthalpies")) {
                Enthalpies = value;
              } else if (line.contains("electronic and thermal Free Energies")) {
                FreeEnergies = value;
              }
            } catch (Exception ex) {
              logger.severe("Cannot parse " + line);
            }
          }
          // ---
          String[] tokens = null;
          line = in.readLine();
          appendOutputResume(outputResume, "Thermochemistry", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Thermochemistry", line);
          // --- Parse TOTAL contributions
          line = in.readLine();
          appendOutputResume(outputResume, "Thermochemistry", line);
          try {
            label = line.substring(0, 16).trim();
            tokens = line.substring(label.length() + 1).trim().split("\\s+");
            value = new Double(tokens[0]);
            availableProperties.add(label + " E(Thermal) KCal/Mol");
            addToLastSnapshot(label + " E(Thermal) KCal/Mol", value);
            value = new Double(tokens[1]);
            availableProperties.add(label + " CV (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " CV (Cal/Mol-Kelvin)", value);
            value = new Double(tokens[2]);
            availableProperties.add(label + " S (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " S (Cal/Mol-Kelvin)", value);
          } catch (Exception ex) {
            logger.severe("Cannot parse " + line + " Tokens: " + Arrays.toString(tokens));
          }
          // --- Parse Electronic contributions
          line = in.readLine();
          appendOutputResume(outputResume, "Thermochemistry", line);
          try {
            label = line.substring(0, 16).trim();
            tokens = line.substring(16).trim().split("\\s+");
            value = new Double(tokens[0]);
            availableProperties.add(label + " E(Thermal) KCal/Mol");
            addToLastSnapshot(label + " E(Thermal) KCal/Mol", value);
            value = new Double(tokens[1]);
            availableProperties.add(label + " CV (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " CV (Cal/Mol-Kelvin)", value);
            value = new Double(tokens[2]);
            availableProperties.add(label + " S (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " S (Cal/Mol-Kelvin)", value);
          } catch (Exception ex) {
            logger.severe("Cannot parse " + line + " Tokens: " + Arrays.toString(tokens));
          }
          // --- Parse Translational contributions
          line = in.readLine();
          appendOutputResume(outputResume, "Thermochemistry", line);
          try {
            label = line.substring(0, 16).trim();
            tokens = line.substring(16).trim().split("\\s+");
            value = new Double(tokens[0]);
            availableProperties.add(label + " E(Thermal) KCal/Mol");
            addToLastSnapshot(label + " E(Thermal) KCal/Mol", value);
            value = new Double(tokens[1]);
            availableProperties.add(label + " CV (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " CV (Cal/Mol-Kelvin)", value);
            value = new Double(tokens[2]);
            availableProperties.add(label + " S (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " S (Cal/Mol-Kelvin)", value);
          } catch (Exception ex) {
            logger.severe("Cannot parse " + line + " Tokens: " + Arrays.toString(tokens));
          }
          // --- Parse Rotational contributions
          line = in.readLine();
          appendOutputResume(outputResume, "Thermochemistry", line);
          try {
            label = line.substring(0, 16).trim();
            tokens = line.substring(16).trim().split("\\s+");
            value = new Double(tokens[0]);
            availableProperties.add(label + " E(Thermal) KCal/Mol");
            addToLastSnapshot(label + " E(Thermal) KCal/Mol", value);
            value = new Double(tokens[1]);
            availableProperties.add(label + " CV (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " CV (Cal/Mol-Kelvin)", value);
            value = new Double(tokens[2]);
            availableProperties.add(label + " S (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " S (Cal/Mol-Kelvin)", value);
          } catch (Exception ex) {
            logger.severe("Cannot parse " + line + " Tokens: " + Arrays.toString(tokens));
          }
          // --- Parse Vibrational contributions
          line = in.readLine();
          appendOutputResume(outputResume, "Thermochemistry", line);
          try {
            label = line.substring(0, 16).trim();
            tokens = line.substring(16).trim().split("\\s+");
            value = new Double(tokens[0]);
            availableProperties.add(label + " E(Thermal) KCal/Mol");
            addToLastSnapshot(label + " E(Thermal) KCal/Mol", value);
            value = new Double(tokens[1]);
            availableProperties.add(label + " CV (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " CV (Cal/Mol-Kelvin)", value);
            value = new Double(tokens[2]);
            availableProperties.add(label + " S (Cal/Mol-Kelvin)");
            addToLastSnapshot(label + " S (Cal/Mol-Kelvin)", value);
          } catch (Exception ex) {
            logger.severe("Cannot parse " + line + " Tokens: " + Arrays.toString(tokens));
          }
          // ---
          double EThermal = 0, CV = 0, S = 0;
          while ((line = in.readLine()) != null) {
            if (line.contains("Q            Log10(Q)")) {
              break;
            }
            appendOutputResume(outputResume, "Thermochemistry", line);
            // Vibration    10          0.689              1.684              1.448
            tokens = line.substring(16).trim().split("\\s+");
            try {
              value = new Double(tokens[0]);
              EThermal += value;
              value = new Double(tokens[1]);
              CV += value;
              value = new Double(tokens[2]);
              S += value;
            } catch (Exception ex) {
              logger.severe("Cannot parse " + line + " Tokens: " + Arrays.toString(tokens));
            }
          }
          // --- Correction for internal rotation
          appendOutputResume(outputResume, "Thermochemistry", "Thermal Energy correction for internal rotations: "
              + String.valueOf(EThermal) + " kcal/mol");
          EThermal /= 627.51;
          // Temperature   298.150 Kelvin.  Pressure   1.00000 Atm.
          double TdS = S * 298.150 / 1000.0;
          appendOutputResume(outputResume, "Thermochemistry", "TdS correction for internal rotations: "
              + String.valueOf(TdS) + " kcal/mol");
          TdS /= 627.51;
          double corrEnthalpies = Enthalpies - EThermal;
          double corrFreeEnergies = FreeEnergies - EThermal + TdS;
          appendOutputResume(outputResume, "Thermochemistry", "Internal rotations correction: Enthalpies: "
              + String.format("%15.6f", corrEnthalpies) + " Free Energies: "
              + String.format("%15.6f", corrFreeEnergies) + " Hartrees");
        } // --- Getting archive data
        else if (line.contains("\\GINC-") || line.contains("|UNPC-UNK")) { // Different on PC !!!
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Reading archive data...");
          }
          try {

            String delim = "\\";
            if (line.contains("|UNPC-UNK")) {
              delim = "|";
            }
            String archive = line;
            // Accumulate archive
            while ((line = in.readLine()) != null) {
              //logger.info("Parsing line: " + line);
              if (line.trim().length() == 0) {
                break;
              }
              archive += line.substring(1);
            }

            mol.addProperty(MoleculeInterface.ProgramProperty, "Gaussian");

            // First, extract information from archive variable
            if (archive.length() == 0) {
              continue;
            }
            //mol.addProperty(NotesProperty,archive); // Can be too long

            Double value = null;
            st = new StringTokenizer(archive, delim);
            //logger.info("Archive string: " + archive);
            //String tokens[] = archive.split(backsl);
            // Go through tokens
            //for (int i = 0; i < tokens.length; i++) {
            while (st.hasMoreTokens()) {
              token = st.nextToken();
              try {
                if (token.startsWith("HF=")) {
                  String temp[] = token.split("=");
                  SCF_Energy = new Double(temp[1]);
                  availableProperties.add("HF");
                  this.addToLastSnapshot("HF", SCF_Energy);
                } else if (token.startsWith("MP2=")) {
                  String temp[] = token.split("=");
                  value = new Double(temp[1]);
                  mol.addProperty(MoleculeInterface.MP2EnergyProperty, value);
                  availableProperties.add("MP2");
                  addToLastSnapshot("MP2", value);
                } else if (token.startsWith("MP3=")) {
                  String temp[] = token.split("=");
                  value = new Double(temp[1]);
                  mol.addProperty(MoleculeInterface.MP3EnergyProperty, value);
                  availableProperties.add("MP3");
                  addToLastSnapshot("MP3", value);
                } else if (token.startsWith("MP4D=")) {
                  String temp[] = token.split("=");
                  value = new Double(temp[1]);
                  mol.addProperty(MoleculeInterface.MP4DEnergyProperty, value);
                  availableProperties.add("MP4D");
                  addToLastSnapshot("MP4D", value);
                } else if (token.startsWith("MP4DQ=")) {
                  String temp[] = token.split("=");
                  value = new Double(temp[1]);
                  mol.addProperty(MoleculeInterface.MP4DQEnergyProperty, value);
                  availableProperties.add("MP4DQ");
                  addToLastSnapshot("MP4DQ", value);
                } else if (token.startsWith("MP4SDQ=")) {
                  String temp[] = token.split("=");
                  value = new Double(temp[1]);
                  mol.addProperty(MoleculeInterface.MP4SDQEnergyProperty, value);
                  availableProperties.add("MP4SDQ");
                  addToLastSnapshot("MP4SDQ", value);
                } else if (token.startsWith("CCSD=")) {
                  String temp[] = token.split("=");
                  value = new Double(temp[1]);
                  mol.addProperty(MoleculeInterface.CCSDEnergyProperty, value);
                  availableProperties.add("CCSD");
                  addToLastSnapshot("CCSD", value);
                } else if (token.startsWith("RMSD=")) {
                  String temp[] = token.split("=");
                  value = new Double(temp[1]);
                  mol.addProperty("RMSD", value);
                  availableProperties.add("RMSD");
                  addToLastSnapshot("CCSD", value);
                } else if (token.startsWith("CCSD(T)=")) {
                  String temp[] = token.split("=");
                  value = new Double(temp[1]);
                  mol.addProperty(MoleculeInterface.CCSDTEnergyProperty, value);
                  availableProperties.add("CCSD(T)");
                  addToLastSnapshot("CCSD(T)", value);
                } else if (token.startsWith("Version=")) {
                  String temp[] = token.split("=");
                  mol.addProperty(MoleculeInterface.VersionProperty, temp[1]);
                } else if (token.startsWith("Dipole=")) {
                  String temp[] = token.split("=");
                  Float dp = parseDipoleMoment(temp[1]);
                  if (dp != null) {
                    mol.addProperty(MoleculeInterface.DipoleMomentProperty, dp);
                  }
                }

              } catch (Exception ex) {
                System.err.println("Cannot parse token " + token + " : " + ex.getMessage() + " Ignored...");
              }

            }
          } catch (Exception ex) {
            logger.severe("Error readin archive data: " + ex.getMessage());
          }
        } // --- Getting framework group
        else if (line.contains("Framework Group")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Framework Group: line: " + line);
          }
          try {
            st = new StringTokenizer(line, " ");
            if (st.countTokens() != 3) {
              continue;
            }
            st.nextToken(); // Skip a token
            st.nextToken(); // Skip a token
            mol.addProperty(MoleculeInterface.FrameGroupProperty, st.nextToken());
            outputResume.put(MoleculeInterface.FrameGroupProperty, line);
          } catch (Exception ex) {
            logger.severe("Error reading Framework group: " + ex.getMessage());
          }
        } else if (line.contains("Gross orbital populations:") && nBasisFuncs > 0) {
          appendOutputResume(outputResume, "Gross orbital populations", "\n" + line);
          try {
            //                          Total     Alpha     Beta      Spin
            line = in.readLine();
            appendOutputResume(outputResume, "Gross orbital populations", line);

            for (int i = 0; i < nBasisFuncs && (line = in.readLine()) != null; i++) {
              appendOutputResume(outputResume, "Gross orbital populations", line);
            }
          } catch (Exception ex) {
            logger.severe("Error reading Gross orbital populations: " + ex.getMessage());
          }
        } else if (line.contains("Scan the potential surface")) {
          appendOutputResume(outputResume, "Scan the potential surface", "\n" + line);
        } else if (line.contains("Variable   Value     No.")) {
          appendOutputResume(outputResume, "Variable   Value     No.", line);
          for (int i = 0; i < 3; i++) {
            line = in.readLine();
            appendOutputResume(outputResume, "Variable   Value     No.", line);
          }
        } else if (line.contains("Variable Step   Value")) {
          appendOutputResume(outputResume, "Variable Step   Value", "\n" + line);
          line = in.readLine();
          appendOutputResume(outputResume, "Variable Step   Value", line);
          while ((line = in.readLine()) != null) {
            if (line.trim().length() < 1 || line.contains("-----")) {
              break;
            }
            appendOutputResume(outputResume, "Variable Step   Value", line);
          }
        } else if (line.contains("Summary of the potential surface scan")) {
          appendOutputResume(outputResume, "Summary of the potential surface scan", "\n" + line);
          line = in.readLine();
          appendOutputResume(outputResume, "Summary of the potential surface scan", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Summary of the potential surface scan", line);
          while ((line = in.readLine()) != null) {
            if (line.trim().length() < 1 || line.contains("----")) {
              break;
            }
            appendOutputResume(outputResume, "Summary of the potential surface scan", line);
          }
        } else if (line.contains("EUMP2=") || line.contains("EUMP3=")) {
          try {
            // E2 =    -0.3718352303D+00 EUMP2 =    -0.15680202189090D+03
            st = new StringTokenizer(line, " =");
            if (st.countTokens() < 4) {
              continue;
            }
            token = st.nextToken();
            String token2 = st.nextToken();
            token2 = token2.replaceAll("[D]", "E");
            token2 = token2.replaceAll("[d]", "E");
            try {
              addToLastSnapshot(token, new Double(token2));
              propertiesToChart.add(token);
            } catch (Exception ex) {
            }
            token = st.nextToken();
            token2 = st.nextToken();
            token2 = token2.replaceAll("[D]", "E");
            token2 = token2.replaceAll("[d]", "E");
            try {
              propertiesToChart.add(token);
              addToLastSnapshot(token, new Double(token2));
            } catch (Exception ex) {
            }

            if (line.contains("EUMP3=")) {
              appendOutputResume(outputResume, "EUMP3=", line);
            } else {
              appendOutputResume(outputResume, "EUMP2 =", line);
            }
          } catch (Exception ex) {
            logger.severe("Error reading EUMP2= or EUMP3= " + ex.getMessage());
          }
        } else if (line.contains("MP4(R+Q)")) {
          //MP4(R+Q)=  0.45510290D-03
          try {
            st = new StringTokenizer(line, " =");
            if (st.countTokens() < 2) {
              continue;
            }
            token = st.nextToken();
            String token2 = st.nextToken();
            token2 = token2.replaceAll("[D]", "E");
            token2 = token2.replaceAll("[d]", "E");
            try {
              propertiesToChart.add(token);
              addToLastSnapshot(token, new Double(token2));
            } catch (Exception ex) {
            }
            appendOutputResume(outputResume, "MP4(R+Q)", line);
          } catch (Exception ex) {
            logger.severe("Error reading MP4(R+Q) " + ex.getMessage());
          }
        } else if (line.contains("E4(DQ)") || line.contains("E4(SDQ)") || line.contains("DE(Corr)") || line.contains("DE(Z)")) {
          try {
            st = new StringTokenizer(line, " =");
            if (st.countTokens() < 4) {
              continue;
            }
            token = st.nextToken();
            String token2 = st.nextToken();
            token2 = token2.replaceAll("[D]", "E");
            token2 = token2.replaceAll("[d]", "E");
            try {
              propertiesToChart.add(token);
              addToLastSnapshot(token, new Double(token2));
            } catch (Exception ex) {
            }
            token = st.nextToken();
            token2 = st.nextToken();
            token2 = token2.replaceAll("[D]", "E");
            token2 = token2.replaceAll("[d]", "E");
            try {
              propertiesToChart.add(token);
              addToLastSnapshot(token, new Double(token2));
            } catch (Exception ex) {
            }
            appendOutputResume(outputResume, "E4(DQ)", line);
          } catch (Exception ex) {
            logger.severe("Error reading E4(DQ) " + ex.getMessage());
          }
        } else if (line.contains("UMP4(DQ)=")) {
          appendOutputResume(outputResume, "UMP4(DQ)=", line);
        } else if (line.contains("UMP4(SDQ)=")) {
          appendOutputResume(outputResume, "UMP4(SDQ)=", line);
        } else if (line.contains("UMP4(SDTQ)=")) {
          appendOutputResume(outputResume, "UMP4(SDTQ)=", line);
        } else if (line.contains("MP5 =")) {
          appendOutputResume(outputResume, "MP5 =", line);
        } else if (line.contains("Counterpoise: ")) {
          appendOutputResume(outputResume, "Counterpoise: ", "\n" + line);
        } // Thermal correction to Gibbs Free Energy= 0.307428
        else if (line.contains("Thermal correction to Gibbs Free Energy=")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Thermal correction to Gibbs Free Energy=: line: " + line);
          }
          try {
            String bufer = line.substring(line.indexOf("Energy=") + "Energy=".length());
            st = new StringTokenizer(bufer, " ");
            if (st.countTokens() != 1) {
              continue;
            }
            GibbsThermCorr = new Float(st.nextToken()); // Stupid stuff
            outputResume.put("Thermal correction to Gibbs Free Energy=", line);
          } catch (Exception ex) {
            logger.severe("Error reading Thermal correction to Gibbs Free Energy: " + ex.getMessage());
          }
        } // Thermal correction to Enthalpy=  0.386909
        else if (line.contains("Thermal correction to Enthalpy=")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Thermal correction to Enthalpy=: line: " + line);
          }
          st = new StringTokenizer(line, " ");
          if (st.countTokens() != 5) {
            continue;
          }
          st.nextToken(); // Skip a token
          st.nextToken(); // Skip a token
          st.nextToken(); // Skip a token
          st.nextToken(); // Skip a token
          EnthalpyThermCorr = new Float(st.nextToken()); // Stupid stuff
          outputResume.put("Thermal correction to Enthalpy=", line);
        } // Thermal correction to Energy= 0.385965
        else if (line.contains("Thermal correction to Energy=")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Thermal correction to Energy=: line: " + line);
          }
          st = new StringTokenizer(line, " ");
          if (st.countTokens() != 5) {
            continue;
          }
          st.nextToken(); // Skip a token
          st.nextToken(); // Skip a token
          st.nextToken(); // Skip a token
          st.nextToken(); // Skip a token
          EnergyThermCorr = new Float(st.nextToken()); // Stupid stuff
          outputResume.put("Thermal correction to Energy=", line);
        } // --- Getting Zero-point correction
        // Zero-point correction=                           0.361162 (Hartree/Particle)
        else if (line.contains("Zero-point correction")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Zero-point correction: line: " + line);
          }
          st = new StringTokenizer(line, " ");
          if (st.countTokens() != 4) {
            continue;
          }
          st.nextToken(); // Skip a token
          st.nextToken(); // Skip a token
          ZeroPointCorr = new Double(st.nextToken()); // Stupid stuff
          outputResume.put("Zero-point correction", line);
        } // In semiempirical calc we have
        // Energy=   -0.094390560183 NIter=  17.
        else if (line.matches(" Energy=(.*?) NIter=(.*?)")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing  Energy=(.*?) NIter=(.*?): line: " + line);
          }
          snapshots.add(snapshot);
          snapshot = new GaussianSnapshot();
          st = new StringTokenizer(line, " =");
          if (st.countTokens() < 2) {
            System.err.println("Warning: parsing Energy=(.*?) NIter=(.*?): got less than 2 tokens...");
            continue;
          }
          token = st.nextToken(); // Energy
          String token2 = st.nextToken(); // number
          SCF_Energy = new Double(token2); // Stupid stuff
          addToLastSnapshot(token, SCF_Energy);
          propertiesToChart.add(token);
          if (scfEnergy == null) {
            scfEnergy = token;
          }
          appendOutputResume(outputResume, "Semiempirical Energy:", "\n" + line);
        } // --- Getting SCF Energy and S**2
        else if (line.contains("SCF Done:")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing SCF Done: line: " + line);
          }

          token = null;
          String token2 = null;
          snapshots.add(snapshot);
          snapshot = new GaussianSnapshot();

          // --- process the first line
          line = line.trim();
          st = new StringTokenizer(line, " =");
          if (st.countTokens() < 5) {
            System.err.println("Warning: parsing SCF Done: got less than 5 tokens...");
            continue;
          }
          st.nextToken(); // Skip a token: SCF
          st.nextToken(); // Skip a token: Done:
          token = st.nextToken(); // E(bla-bla-bla)
          token2 = st.nextToken(); // number
          SCF_Energy = new Double(token2); // Stupid stuff
          addToLastSnapshot(token, SCF_Energy);
          propertiesToChart.add(token);
          if (scfEnergy == null) {
            scfEnergy = token;
          }
          appendOutputResume(outputResume, "SCF Done:", "\n" + line);

          // --- process the second line
          line = in.readLine();
          st = new StringTokenizer(line, " =");
          if (st.countTokens() < 4) {
            System.err.println("Warning: parsing SCF Done: Convg  =  : got less than 4 tokens...");
            continue;
          } else {
            token = st.nextToken();
            token2 = st.nextToken();
            try {
              token2 = token2.replaceAll("[D]", "E");
              token2 = token2.replaceAll("[d]", "E");
              addToLastSnapshot(token, new Double(token2));
              propertiesToChart.add(token);
            } catch (Exception ex) {
              // --- Do nothing
            }

            token = st.nextToken();
            token2 = st.nextToken();
            try {
              token2 = token2.replaceAll("[D]", "E");
              token2 = token2.replaceAll("[d]", "E");
              addToLastSnapshot(token, new Double(token2));
              propertiesToChart.add(token);
            } catch (Exception ex) {
              // --- Do nothing
            }
          }

          // --- The third line
          line = in.readLine();
          if (line.contains("S**2")) {
            st = new StringTokenizer(line, " =");
            if (st.countTokens() < 2) {
              continue;
            }
            token = st.nextToken(); // S**2
            S_Squared = new Double(st.nextToken()); // Stupid stuff
            appendOutputResume(outputResume, "S**2", line);
            addToLastSnapshot(token, S_Squared);
            propertiesToChart.add(token);
          }
        } // --- Getting Dipole Moment (for final structure only)
        else if (line.contains("Dipole moment")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Dipole moment");
          }
          appendOutputResume(outputResume, "Dipole moment", "\n" + line);
          line = in.readLine();
          appendOutputResume(outputResume, "Dipole moment2", line);
          st = new StringTokenizer(line, " ");
          if (st.countTokens() != 8) {
            if (logger.isLoggable(Level.INFO)) {
              logger.info("Parsing Dipole moment: temp.length != 8: " + st.countTokens());
            }
            continue;
          }
          // We rearrange dipole moment values (total value first)
          st.nextToken(); // Skip a token
          String dipole = st.nextToken(); // X-compomnent
          st.nextToken(); // Skip a token
          dipole += " " + st.nextToken(); // Y-compomnent
          st.nextToken(); // Skip a token
          dipole += " " + st.nextToken(); // Z-compomnent
          st.nextToken(); // Skip a token
          String total_dipole = st.nextToken();
          dipole = total_dipole + " " + dipole; // Total compomnent in front
          //mol.addProperty(DipoleMomentProperty, dipole); !!!
          // --- Now save only a total dipole moment (scalar)
          try {
            Double dp = new Double(total_dipole);
            availableProperties.add("Dipole Moment");
            mol.addProperty(MoleculeInterface.DipoleMomentProperty, dp);
            this.addToLastSnapshot("Dipole Moment", dp);
          } catch (NumberFormatException e) {
            if (Debug) {
              System.err.println("Cannot parse total dipole moment: " + e.getMessage());
            }
            continue;
          }

        } else if (line.startsWith("         Item               Value     Threshold  Converged?")) {
          appendOutputResume(outputResume, "Opt", "\n" + line);
          line = in.readLine();
          appendOutputResume(outputResume, "Opt", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Opt", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Opt", line);
        } else if (line.contains("Quadrupole moment (field-independent basis, Debye-Ang)")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info(line);
          }
          appendOutputResume(outputResume, "Quadrupole moment", "\n" + line);
          line = in.readLine();
          appendOutputResume(outputResume, "Quadrupole moment", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Quadrupole moment", line);
        } else if (line.contains("Traceless Quadrupole moment (field-independent basis, Debye-Ang)")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info(line);
          }
          appendOutputResume(outputResume, "Traceless Quadrupole moment", "\n" + line);
          line = in.readLine();
          appendOutputResume(outputResume, "Traceless Quadrupole moment", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Traceless Quadrupole moment", line);
        } else if (line.contains("Octapole moment (field-independent basis, Debye-Ang**2)")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info(line);
          }
          appendOutputResume(outputResume, "Octapole moment", "\n" + line);
          line = in.readLine();
          appendOutputResume(outputResume, "Octapole moment", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Octapole moment", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Octapole moment", line);
        } else if (line.contains("Hexadecapole moment (field-independent basis, Debye-Ang**3)")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info(line);
          }
          appendOutputResume(outputResume, "Hexadecapole moment", "\n" + line);
          line = in.readLine();
          appendOutputResume(outputResume, "Hexadecapole moment", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Hexadecapole moment", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Hexadecapole moment", line);
          line = in.readLine();
          appendOutputResume(outputResume, "Hexadecapole moment", line);
        } // --- Mulliken atomic charges
        else if (line.contains("Mulliken atomic charges:")) {
          Info.concat(line + "\n");
          appendOutputResume(outputResume, "Mulliken atomic charges", "\n" + line);
          gotMulliken = false;
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Mulliken atomic charges:");
          }
          line = in.readLine(); // Skiping a line
          mulliken = "";
          int count = 0;
          while ((line = in.readLine()) != null) {
            if (logger.isLoggable(Level.INFO)) {
              logger.info("Parsing line: " + line);
            }
            if (line.length() == 0 || line.contains("Sum of Mulliken charges=")) {
              break;
            }
            st = new StringTokenizer(line, " ");
            if (st.countTokens() != 3) {
              if (logger.isLoggable(Level.INFO)) {
                logger.info("Error Parsing Mulliken atomic charges: " + line);
              }
              break;
            }
            st.nextToken();
            st.nextToken();
            mulliken += st.nextToken() + " ";

            ++count;
            appendOutputResume(outputResume, "Mulliken atomic charges"
                + String.valueOf(count), line);
          }
          gotMulliken = true;
        } // --- ESP fitted atomic charges
        else if (line.contains("Charges from ESP fit")) {
          Info.concat(line + "\n");
          gotESP = false;
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Charges from ESP fit");
          }
          line = in.readLine(); // Skiping a line
          line = in.readLine(); // Skiping a line
          espCharges = "";
          while ((line = in.readLine()) != null) {
            if (logger.isLoggable(Level.INFO)) {
              logger.info("Parsing line: " + line);
            }
            if (line.length() == 0 || line.contains("--------------------")) {
              break;
            }
            st = new StringTokenizer(line, " ");
            if (st.countTokens() != 3) {
              if (logger.isLoggable(Level.INFO)) {
                logger.info("Error Parsing ESP atomic charges: " + line);
              }
              break;
            }
            st.nextToken();
            st.nextToken();
            espCharges += st.nextToken() + " ";
          }
          gotESP = true;
        } // Extracting
        /*
         else if (line.contains("imaginary frequencies (negative Signs)")) {
         if (Debug) {
         logger.info("Parsing imaginary frequencies (negative Signs), line:" + line);
         }
         appendOutputResume(outputResume, "imaginary frequencies", line);
         line = line.substring(line.indexOf("******") + "******".length());
         st = new StringTokenizer(line, " ");
         int n_freqs = 0;
         try {
         n_freqs = Integer.parseInt(st.nextToken());
         }
         catch (NumberFormatException e) {
         if (Debug) {
         logger.info("Cannot parse number of imaginary frequences:" + e.getMessage());
         }
         continue;
         }

         String freqs = String.valueOf(n_freqs);

         for (int i = 0; i < 8; i++) {
         line = in.readLine(); // Skiping 8 lines
         }
         line = in.readLine();
         line = line.substring(line.indexOf("Frequencies --") + "Frequencies --".length());
         st = new StringTokenizer(line, " ");

         for (int i = 0; i < n_freqs; i++) {
         freqs += " " + st.nextToken(); ;
         }
         appendOutputResume(outputResume, "imaginary frequencies2", line);
         mol.addProperty(MoleculeInterface.ImaginaryFreqsProperty, freqs);
         }
         */ // --- // --- Extracting charge and multiplicity (to be sure)
        else if (line.contains("Multiplicity")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Charge, line:" + line);
          }
          st = new StringTokenizer(line, " ");
          if (st.countTokens() >= 3 && (!gotCharge)) {
            try {
              st.nextToken();
              st.nextToken();
              Integer charge = new Integer(st.nextToken());
              mol.addProperty(MoleculeInterface.ChargeProperty, charge);
              gotCharge = true;
            } catch (NumberFormatException e) {
              if (logger.isLoggable(Level.INFO)) {
                logger.info("Cannot parse integer:" + e.getMessage());
              }
            }
          }

          if (st.countTokens() >= 3) {
            try {
              st.nextToken();
              st.nextToken();
              Integer mult = new Integer(st.nextToken());
              mol.addProperty(MoleculeInterface.MultiplicityProperty, mult);
            } catch (NumberFormatException e) {
              if (logger.isLoggable(Level.INFO)) {
                logger.info("Mult: Cannot parse integer:" + e.getMessage());
              }
            }
          }
        } // --- Reading in BSSE info...
        else if (line.contains("Counterpoise corrected energy =")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Counterpoise corrected energy info, line:" + line);
          }
          appendOutputResume(outputResume, "Counterpoise corrected energy", "\n" + line);
          for (int i = 0; i < 4; i++) {
            line = in.readLine();
            if (line == null) {
              if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Parsing Counterpoise corrected energy info: unexpected eof");
              }
              break;
            }
            appendOutputResume(outputResume, "Counterpoise corrected energy", line);
          }
        } // Redundant internal coordinates taken from checkpoint file
        else if (line.contains("Redundant internal coordinates taken from checkpoint file")) { // Not always present
          // Redundant internal coordinates taken from checkpoint file:
          //  path/file-name.chk
          //  Charge =  0 Multiplicity = 1
          //  C,0,0.0000000027,1.3986013041,0.
          // ...
          //  H,0,2.1526510788,1.2428336743,0.
          //  Recover connectivity data from disk.

          line = in.readLine(); // skipping 1st line
          line = in.readLine(); // Charge and Mult
          if (line.contains("Charge =") && (!gotCharge)) {
            int[] cam = parseChargeAndMultiplicityLine(line);
            //Info.concat(line + "\n");
            Integer charge = new Integer(cam[0]);
            mol.addProperty(MoleculeInterface.ChargeProperty, charge);
            gotCharge = true;
            Integer mult = new Integer(cam[1]);
            mol.addProperty(MoleculeInterface.MultiplicityProperty, mult);
          }

          while ((line = in.readLine()) != null) {
            if (logger.isLoggable(Level.INFO)) {
              logger.info("Parsing line: " + line);
            }
            line = line.trim();
            if (line.contains("Charge =")) {
              continue;
            } else if (line.contains("Recover")) {
              break;
            }

            st = new StringTokenizer(line, ",");
            if (st.countTokens() < 5) {
              System.err.println("Warning: Expected at least 5 tokens, got: " + line);
              break;
            }

            if (!update) {
              token = st.nextToken();
              GaussianAtom a = new GaussianAtom();
              try {
                a.parseAtomName(token);
              } catch (Exception ex) {
              }
              AtomInterface atom = mol.getNewAtomInstance();
              atom.setName(token);
              atom.setAtomicNumber(a.getElement());

              st.nextToken(); // Skip

              try {
                atom.setX(Float.parseFloat(st.nextToken()));
                atom.setY(Float.parseFloat(st.nextToken()));
                atom.setZ(Float.parseFloat(st.nextToken()));
              } catch (Exception ex) {
                System.err.println("Cannot parse atom coordinate(s) for: " + line);
                break;
              }

              mol.addAtom(atom);
            }

          }

        } // Z-Matrix No Z-matrix found on checkpoint file
        else if (line.contains("Symbolic Z-matrix") || line.contains("No Z-matrix found on checkpoint file")) { // Not always present
          if (mol.getNumberOfAtoms() > 0) {
            continue;
          }
          if (line.contains("No Z-matrix found on checkpoint file")) {
            // No Z-matrix found on checkpoint file.
            // Cartesian coordinates read from the checkpoint file:
            // /tmp/transp/k300a-12.chk
            // Charge =  1 Multiplicity = 1

            line = in.readLine(); // skipping 1st line
            line = in.readLine(); // skipping 2nd line
          }

          line = in.readLine(); // Charge and Mult

          // --- Extracting charge and multiplicity
          if (line.contains("Charge =") && (!gotCharge)) {
            int[] cam = parseChargeAndMultiplicityLine(line);
            //Info.concat(line + "\n");
            Integer charge = new Integer(cam[0]);
            mol.addProperty(MoleculeInterface.ChargeProperty, charge);
            gotCharge = true;
            Integer mult = new Integer(cam[1]);
            mol.addProperty(MoleculeInterface.MultiplicityProperty, mult);
          }

          if (!update) {
            Info.concat(line + "\n");
          }
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Parsing Symbolic Z-matrix records");
          }

          while ((line = in.readLine()) != null) {
            if (logger.isLoggable(Level.INFO)) {
              logger.info("Parsing line: " + line);
            }
            String bufer = line.trim();
            if (bufer.contains("Charge =")) {
              continue;
            }
            if (bufer.length() == 0 || bufer.startsWith("Recover connectivity data")) {
              break;
            }

            if (bufer.compareToIgnoreCase("Variables:") == 0) {

            }

            gaussMol.addMoleculeSpecsItem(line);

            if (false) {
              st = new StringTokenizer(line, " ");
              token = st.nextToken();
              if (token.compareToIgnoreCase("X") == 0) {
                continue;
              }
              int element;
              try { // --- Is it number?
                element = Integer.parseInt(token);
              } catch (NumberFormatException e) {
                StringTokenizer st2 = new StringTokenizer(token, "0123456789");
                String truncatedName = st2.nextToken();

                if (truncatedName.length() == 1) {
                  element = ChemicalElements.getAtomicNumber(truncatedName);
                } else {
                  int initialGuess = ChemicalElements.getAtomicNumber(truncatedName.substring(0, 2));
                  element = ChemicalElements.getAtomicNumber(truncatedName.substring(0, 1));
                  element = initialGuess != 0 ? initialGuess : element;
                }

              }
              if (!update) {
                AtomInterface atom = mol.getNewAtomInstance();
                atom.setName(token);
                atom.setAtomicNumber(element);
                mol.addAtom(atom);
              }
            }
          }

          try {
            gaussMol.parseMoleculeGeometry();
            mol = gaussMol.getMolecule(mol);
          } catch (Exception ex) {
            System.err.println("Error Parsing Molecular Specifications...");
          }

          if (logger.isLoggable(Level.INFO)) {
            logger.info("Setup " + mol.getNumberOfAtoms() + " atoms from Symbolic Z-matrix information");
          }
        } else if (line.contains("Standard orientation****")) {
          /* --- For G03
           Input orientation:
           ---------------------------------------------------------------------
           Center     Atomic     Atomic              Coordinates (Angstroms)
           Number     Number      Type              X           Y           Z
           ---------------------------------------------------------------------
           1          8             0        0.000000    0.000000    0.000000
           2          1             0        0.000000    0.000000    0.960000
           3          1             0        0.905097    0.000000   -0.320000
           ---------------------------------------------------------------------
           */

        } else if (line.contains(INPUT_ORIENTATION_KEY)
            || line.contains(Z_MATRIX_ORIENTATION_KEY)
            || line.contains(STANDARD_ORIENTATION_KEY)) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Found " + line);
          }
          /* For G03
           Standard orientation:
           ---------------------------------------------------------------------
           Center     Atomic     Atomic              Coordinates (Angstroms)
           Number     Number      Type              X           Y           Z
           ---------------------------------------------------------------------
           1          8             0        0.000000    0.000000    0.110851
           2          1             0        0.000000    0.783837   -0.443405
           3          1             0        0.000000   -0.783837   -0.443405
           ---------------------------------------------------------------------
           */
          boolean setupAtoms = false;
          if (!update && Geometries.size() == 0 && mol.getNumberOfAtoms() == 0) {
            setupAtoms = true;
          }

          geom = new MolecularGeometry();

          // --- Input orientation
          if (line.contains(INPUT_ORIENTATION_KEY)) {
            if (!availableStructures.contains(INPUT_ORIENTATION_KEY)) {
              availableStructures.add(INPUT_ORIENTATION_KEY);
            }
            // Java 1.5
            //geom.setName(String.format("Input orientation %1$d",
            //                           Geometries.size()));
            geom.setName(INPUT_ORIENTATION_KEY + " " + Geometries.size());

            List<MolecularGeometry> g = diffGeometries.get(INPUT_ORIENTATION_KEY);
            if (g == null) {
              g = new ArrayList<MolecularGeometry>();
            }
            g.add(geom);
            diffGeometries.put(INPUT_ORIENTATION_KEY, g);
            snapshot.addGeometry(INPUT_ORIENTATION_KEY, geom);
          } // --- Z-matrix orientation
          else if (line.contains(Z_MATRIX_ORIENTATION_KEY)) {
            if (!availableStructures.contains(Z_MATRIX_ORIENTATION_KEY)) {
              availableStructures.add(Z_MATRIX_ORIENTATION_KEY);
            }

            // Java 1.5
            //geom.setName(String.format("Z-Matrix orientation %1$d",
            //                           Geometries.size()));
            geom.setName(Z_MATRIX_ORIENTATION_KEY + " " + Geometries.size());

            List<MolecularGeometry> g = diffGeometries.get(Z_MATRIX_ORIENTATION_KEY);
            if (g == null) {
              g = new ArrayList<MolecularGeometry>();
            }
            g.add(geom);
            diffGeometries.put(Z_MATRIX_ORIENTATION_KEY, g);
            snapshot.addGeometry(Z_MATRIX_ORIENTATION_KEY, geom);
          } else if (line.contains(STANDARD_ORIENTATION_KEY)) {
            if (!availableStructures.contains(STANDARD_ORIENTATION_KEY)) {
              availableStructures.add(STANDARD_ORIENTATION_KEY);
            }

            // Java 1.5
            //geom.setName(String.format("Standard orientation %1$d",
            //                           Geometries.size()));
            geom.setName(STANDARD_ORIENTATION_KEY + " " + Geometries.size());

            List<MolecularGeometry> g = diffGeometries.get(STANDARD_ORIENTATION_KEY);
            if (g == null) {
              g = new ArrayList<MolecularGeometry>();
            }
            g.add(geom);
            diffGeometries.put(STANDARD_ORIENTATION_KEY, g);
            snapshot.addGeometry(STANDARD_ORIENTATION_KEY, geom);
          }

          // Skip 4 lines
          in.readLine();
          in.readLine();
          in.readLine();
          in.readLine();

          // --- Start to read geometry
          while ((line = in.readLine()) != null && !line.contains("-----------")) {
            if (logger.isLoggable(Level.INFO)) {
              logger.info("Parsing line" + line);
            }
            st = new StringTokenizer(line, " ");
            token = st.nextToken(); // Skip Center number

            if (setupAtoms) {
              int element;
              token = st.nextToken();
              try { // --- Is it number? have to!...
                element = Integer.parseInt(token);
              } catch (NumberFormatException e) {
                StringTokenizer st2 = new StringTokenizer(token, "0123456789");
                element = ChemicalElements.getAtomicNumber(st2.nextToken());
              }
              AtomInterface atom = mol.getNewAtomInstance();
              atom.setName(token);
              atom.setAtomicNumber(element);
              mol.addAtom(atom);
            } else {
              st.nextToken(); // Skip Atomic Number
            }

            st.nextToken(); // Skip Atomic Type
            //token = st.nextToken();
            Point3f point = new Point3f();
            try {
              point.setX(Float.parseFloat(st.nextToken()));
              point.setY(Float.parseFloat(st.nextToken()));
              point.setZ(Float.parseFloat(st.nextToken()));

            } catch (NumberFormatException e) {
              if (logger.isLoggable(Level.INFO)) {
                logger.info("parseGaussianOutputFile: Error converting cartesian coordinates\n");
              }
              break;
            }
            geom.add(point);
          }
          Geometries.add(geom);
        } // --- Read frequencies
        else if (line.contains("and normal coordinates:")) {
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Starting to read frequences...");
          }
          try {
            frequencies = new ArrayList<GaussianFrequency>();
            if (frequencies instanceof ArrayList) {
              ((ArrayList) frequencies).ensureCapacity(mol.getNumberOfAtoms());
            }
            boolean freq_error = false;

            while ((line = in.readLine()) != null && line.trim().length() > 0) {

              st = new StringTokenizer(line, " \t");
              int num_freq = st.countTokens();
              GaussianFrequency freqs[] = new GaussianFrequency[num_freq];

              for (int i = 0; i < num_freq; i++) {
                freqs[i] = new GaussianFrequency(mol.getNumberOfAtoms());
              }

              // --- Reading symbols
              if ((line = in.readLine()) == null) {
                logger.severe("Error: expecting symbols for frequencies, got eof. Ignored...");
                frequencies.clear();
                frequencies = null;
                break;
              }
              st = new StringTokenizer(line, " \t");
              if (st.countTokens() != num_freq) {
                logger.severe("Error: expecting " + num_freq + " symbols for frequencies, got " + st.countTokens()
                    + " Ignored...");
                frequencies.clear();
                frequencies = null;
                break;
              }

              for (int i = 0; i < num_freq; i++) {
                freqs[i].setSymbol(st.nextToken());
              }

              while (true) {
                if ((line = in.readLine()) == null && line.trim().length() == 0) {
                  freq_error = true;
                  break;
                }

                if (freqVecPattern.matcher(line).matches()) {
                  break;
                }

                double[] values = new double[3];
                String prop_name = null;
                try {
                  prop_name = GaussianFrequency.parsePropLine(line, values);
                } catch (Exception ex) {
                  logger.severe("Error parsing frequency properties " + line + " : " + ex.getMessage());
                  freq_error = true;
                  break;
                }

                for (int i = 0; i < num_freq; i++) {
                  freqs[i].setProperty(prop_name, values[i]);
                }
              }

              if (freq_error) {
                frequencies.clear();
                frequencies = null;
                break;
              }

              // --- Read vectors
              for (int j = 0; j < mol.getNumberOfAtoms(); j++) { // Read vectors
                if ((line = in.readLine()) == null) {
                  logger.severe("End of file while reading frequencies' vectors. Ignored...");
                  break;
                }
                st = new StringTokenizer(line, " \t");
                if (st.countTokens() != 3 * num_freq + 2) {
                  logger.severe("Error: expecting " + (3 * num_freq + 2) + " values for vectors, got " + st.countTokens()
                      + " Ignored...");
                  break;
                }
                // --- Skip two tokens...
                st.nextToken();
                st.nextToken();
                for (int i = 0; i < num_freq; i++) {
                  try {
                    freqs[i].vectors[j].setX(Float.parseFloat(st.nextToken()));
                    freqs[i].vectors[j].setY(Float.parseFloat(st.nextToken()));
                    freqs[i].vectors[j].setZ(Float.parseFloat(st.nextToken()));
                  } catch (Exception ex) {
                    logger.severe("Error parsing vector value: " + ex.getMessage() + " Ignored...");
                    freq_error = true;
                    break;
                  }
                }
              }

              if (freq_error) {
                frequencies.clear();
                frequencies = null;
                break;
              }

              for (int i = 0; i < num_freq; i++) {
                frequencies.add(freqs[i]);
              }
            }
          } catch (Exception ex) {
            logger.severe("Error reading frequences: " + ex.getMessage());
          }
        }

      } // --- End of while

      // in.close();
    } catch (IOException e) {
      logger.severe("parseGaussianOutFile: " + e.getMessage());
      //outputResults.put("molecule", mol);
      //outputResults.put("geometries", Geometries);
      //return outputResults;
      step = new GaussianStep();
      step.setMolecule(mol);
      step.setGeometries(Geometries);
      step.setFinalStep(true);
      return step;
    }

    // --- One needs to decide what to return...
    if (update) {
      //outputResults.put("molecule", mol);
      //outputResults.put("geometries", Geometries);
      //return outputResults;
      step = new GaussianStep();
      step.setMolecule(mol);
      step.setGeometries(Geometries);
      return step;
    }

    if (logger.isLoggable(Level.INFO)) {
      for (int i = 0; i < Geometries.size(); i++) {

        logger.info("Geometry: " + i);

        MolecularGeometry g = (MolecularGeometry) Geometries.get(i);
        for (int j = 0; j < g.size(); j++) {
          Point3f point = g.get(j);
          logger.info(j + " " + point.x + " " + point.y + " " + point.z);
        }
      }
    }
    if (SCF_Energy != null) {
      mol.addProperty(MoleculeInterface.SCFEnergyProperty, SCF_Energy);
    }
    if (S_Squared != null) {
      mol.addProperty(MoleculeInterface.S_SquaredProperty, S_Squared);
    }

    if (ZeroPointCorr != null) {
      mol.addProperty(MoleculeInterface.ZeroPointCorrProperty, ZeroPointCorr);
    }
    if (EnergyThermCorr != null) {
      mol.addProperty(MoleculeInterface.EnergyThermCorrProperty, EnergyThermCorr);
    }
    if (EnthalpyThermCorr != null) {
      mol.addProperty(MoleculeInterface.EnthalpyThermCorrProperty, EnthalpyThermCorr);
    }
    if (GibbsThermCorr != null) {
      mol.addProperty(MoleculeInterface.GibbsThermCorrProperty, GibbsThermCorr);
    }
    if (gotMulliken) {
      String temp[] = mulliken.split(" ");
      if (temp.length == mol.getNumberOfAtoms()) {
        mol.addProperty(MoleculeInterface.MullikenChargesProperty, mulliken);
      } else if (logger.isLoggable(Level.INFO)) {
        logger.info("Number of atoms and mulliken charges do not match");
      }
    }
    if (gotESP) {
      String temp[] = espCharges.split(" ");
      if (temp.length == mol.getNumberOfAtoms()) {
        mol.addProperty(MoleculeInterface.ESPChargesProperty, espCharges);
      } else if (logger.isLoggable(Level.INFO)) {
        logger.info("Number of atoms and ESP charges do not match");
      }
    }

    //mol.setGeometries(Geometries);
    if (logger.isLoggable(Level.INFO)) {
      logger.info("Molecular Properties");
    }
    Map prop = mol.getProperties();
    Set set = prop.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String name = me.getKey().toString();
      String values = me.getValue() == null ? "No value" : me.getValue().toString();
      if (logger.isLoggable(Level.INFO)) {
        logger.info(name + " : " + values);
      }
    }
    //outputResults.put("molecule", mol);
    //outputResults.put("geometries", Geometries);
    step = new GaussianStep();

    if (snapshots.size() != 0) {
      snapshot = snapshots.get(snapshots.size() - 1);
      geom = snapshot.getGeometry(STANDARD_ORIENTATION_KEY);
      if (geom == null) {
        geom = snapshot.getGeometry( INPUT_ORIENTATION_KEY);
      }
      if (geom == null) {
        geom = snapshot.getGeometry( Z_MATRIX_ORIENTATION_KEY);
      }
      if (geom != null) {
        int j = 0; // --- Cound only "real" atom skipping dummy ones
        for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
          AtomInterface atom = mol.getAtomInterface(i);
          if (atom.getName().toUpperCase().startsWith("X")) {
            continue;
          }
          Point3f at = geom.getCoordinates(j);
          atom.setXYZ(at);
          ++j;
        }
      }
    }
    step.setMolecule(mol);
    step.setGeometries(Geometries);

    //return outputResults;
    return step;
  }

  /**
   * Parses line "Charge = 0 Multiplicity = 1"
   *
   * @return int[] - int[0] - charge, int[1] - multiplicity
   */
  public static int[] parseChargeAndMultiplicityLine(String line) {
    int[] cam = new int[]{
      0, 1};
    if (Debug) {
      logger.info("Parsing Charge and Mult line:" + line);
    }

    if (!line.contains("Charge =")) {
      System.err.println("parseChargeAndMultiplicityLine: cannot parse line: " + line);
      return cam;
    }

    StringTokenizer st = new StringTokenizer(line, " ");
    if (st.countTokens() < 3) {
      System.err.println("parseChargeAndMultiplicityLine: Expected at least 3 tokens, got: " + line);
      return cam;
    }

    try {
      st.nextToken();
      st.nextToken();
      Integer charge = new Integer(st.nextToken());
      cam[0] = charge;
    } catch (NumberFormatException e) {
      System.err.println("parseChargeAndMultiplicityLine: Cannot parse integer for charge: " + e.getMessage());
      return cam;
    }

    if (st.countTokens() < 3) {
      System.err.println("parseChargeAndMultiplicityLine: Expected \"Multiplicity =\" record, got: " + line);
      return cam;
    }

    try {
      st.nextToken();
      st.nextToken();
      Integer mult = new Integer(st.nextToken());
      cam[1] = mult;
    } catch (NumberFormatException e) {
      System.err.println("parseChargeAndMultiplicityLine: Cannot parse integer for multiplicity: " + e.getMessage());
      return cam;
    }

    return cam;
  }

  static Float parseDipoleMoment(String values) {
    String dxyz[] = values.split(",");
    try {
      float dx = Float.parseFloat(dxyz[0]);
      float dy = Float.parseFloat(dxyz[1]);
      float dz = Float.parseFloat(dxyz[2]);
      Float dp = new Float(Math.sqrt((double) (dx * dx + dy * dy + dz * dz)));
      return dp;
    } catch (NumberFormatException e) {
      if (Debug) {
        logger.info("parseDipoleMoment: Cannot parse dipole moment: " + e.getMessage());
      }
    }
    return null;
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

    if (!normalTermination) {
      sWriter.write(
          "\n***** !!!!! IT IS NOT A NORMAL TERMINATION of GAUSSIAN !!!!! *****");
    }
    return sWriter.toString();
  }

  public void appendOutputResume(Map<String, String> resume, String key, String value) {
    if (!resume.containsKey(key)) {
      resume.put(key, value);
      return;
    }

    for (int i = 0; i < 1000; i++) {
      String new_key = key + String.valueOf(i);
      if (!resume.containsKey(new_key)) {
        resume.put(new_key, value);
        return;
      }
    }
    String new_key = key + String.valueOf(1001);
    if (!resume.containsKey(new_key)) {
      resume.put(new_key, value);

    }
  }

  /**
   * Adds property to the last snapshot
   *
   * @param property String
   * @param value double
   */
  private void addToLastSnapshot(String property, double value) {
    if (snapshots.size() < 1) {
      System.err.println("addToLastSnapshot: snapshots.size() < 1");
      return;
    }
    snapshots.get(snapshots.size() - 1).addProperty(property, new Double(value));
  }

  public int vibrationalSpectraCount() {
    if (frequencies == null || frequencies.size() < 1) {
      return 0;
    }
    GaussianFrequency freq = frequencies.get(0);
    return freq.vibrationalSpectraCount();
  }

  public String getYTitle(String name) {
    if (frequencies == null || frequencies.size() < 1) {
      System.err.println("No frequencies info in gaussian output file. Ignored...");
      return "Values";
    }
    GaussianFrequency freq = frequencies.get(0);
    if (!freq.hasSpectrum(name)) {
      System.err.println("Does not have spectrum " + name + " Ignored...");
      return "Values";
    }
    try {
      return freq.getYTitle(name);
    } catch (Exception ex) {
      System.err.println(ex.getMessage() + " Ignored...");
    }
    return "Values";
  }

  public String getXTitle(String name) {
    if (frequencies == null || frequencies.size() < 1) {
      System.err.println("No frequencies info in gaussian output file. Ignored...");
      return "Frequencies";
    }
    GaussianFrequency freq = frequencies.get(0);
    if (!freq.hasSpectrum(name)) {
      System.err.println("Does not have spectrum " + name + " Ignored...");
      return "Frequencies";
    }
    try {
      return freq.getXTitle(name);
    } catch (Exception ex) {
      System.err.println(ex.getMessage() + " Ignored...");
    }
    return "Frequencies";
  }

  public int countFrequencies() {
    if (frequencies == null) {
      return 0;
    }
    return frequencies.size();
  }

  public GaussianStep getStep() {
    return step;
  }

  public int validFormatScore(BufferedReader in) throws Exception {
    String line = null;
    int score = 0;
    for (int i = 0; i < 20; i++) {
      line = in.readLine();
      if (line == null) {
        break;
      } else if (line.contains("Gaussian, Inc.  All Rights Reserved.")) return 10; // For g09
      else if (line.contains("This is the Gaussian(R) 03 program.  It is based on the")) return 10; // For g03
      else if (line.contains("This is part of the Gaussian(R) 98 program.  It is based on")) return 10; // For g98
      else if (line.contains("This is part of the Gaussian 94(TM) system of programs. It is")) return 10; // For g94
    }
    return score;
  }

  public void parseData(BufferedReader in) throws Exception {
    step = parseFile(this.getMoleculeInterface(), in, false);
  }
}
