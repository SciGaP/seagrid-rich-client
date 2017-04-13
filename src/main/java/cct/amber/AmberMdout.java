/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.amber;

import cct.interfaces.AtomInterface;
import cct.interfaces.ChartDataProvider;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;
import cct.tools.DataSets;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Vlad
 */
public class AmberMdout extends DataSets implements ChartDataProvider {

  enum STYPE {

    UNKNOWN, MIN, MD
  }
  static String MIN_PATTERN = "NSTEP       ENERGY          RMS            GMAX         NAME    NUMBER";
  static String QMMM_PATTERN = "  QMMM: QM_NO.   MM_NO.  ATOM";
  static String NMR_RESTR = "NMR restraints:";
  static Pattern mdPattern = Pattern.compile("\\s+NSTEP =\\s+\\d+\\s+TIME.+");
  static Pattern mdAveragesPattern = Pattern.compile("\\s+A V E R A G E S   O V E R\\s*\\d+\\s+S T E P S.*");
  static Pattern mdRMSFPattern = Pattern.compile("\\s+R M S  F L U C T U A T I O N S\\s*");

  private int lineCount = 0;
  private boolean debug = false;
  private boolean minFinalResFound = false;
  Map<String, List<String>> descrRef = new LinkedHashMap<String, List<String>>();
  static final Logger logger = Logger.getLogger(AmberMdout.class.getCanonicalName());

  public void parseData(BufferedReader in) throws Exception {
    STYPE stype = STYPE.UNKNOWN;
    String line;

    lineCount = 0;
    //Pattern minPattern = Pattern.compile("\\s+NSTEP\\s+ENERGY\\s+RMS\\s+");
    // --- Reading in decriptors

    List<String> lines = new ArrayList<String>();
    clearDescriptors(); //.clear();
    this.clearData(); //data.clear();
    int count = 0;
    boolean mdAveragesFound = false;
    boolean mdRMSFFound = false;
    descrRef.clear();

    in.mark(132);
    while ((line = in.readLine()) != null) {
      ++lineCount;

      switch (stype) {
        case UNKNOWN:
          if (line.indexOf(MIN_PATTERN) != -1) {
            stype = STYPE.MIN;
            String[] tokens = line.trim().split("\\s+");
            List<String> desc = new ArrayList<String>();
            descrRef.put(String.valueOf(descrRef.size()), desc);
            for (int i = 0; i < tokens.length; i++) {
              desc.add(tokens[i]);
              if (this.hasDescriptor(tokens[i])) { //descriptors.contains(tokens[i]
                System.err.println("???: Warning descriptor " + tokens[i] + " occurs second time. Ignored...");
              } else {
                this.addDescriptor(tokens[i]); //descriptors.add(tokens[i]);
                //List dt = new ArrayList();
                //this.addData(tokens[i], dt); //data.put(tokens[i], dt);
              }
            }
            parseMinRecords(in);
          } else if (mdPattern.matcher(line).matches()) {
            stype = STYPE.MD;
            parseMDRecords(in, line);
          }
          break;
        // --- Minimization
        case MIN:
          if ((line.contains("FINAL RESULTS"))) {
            minFinalResFound = true;
            continue;
          }
          //
          if ((line.indexOf(MIN_PATTERN) == -1)) {
            continue;
          }
          parseMinRecords(in);
          break;
        // --- MD
        case MD:
          if (mdAveragesPattern.matcher(line).matches()) {
            mdAveragesFound = true;
            continue;
          } else if (mdRMSFPattern.matcher(line).matches()) {
            mdRMSFFound = true;
            continue;
          }
          // ---
          if (!mdPattern.matcher(line).matches()) {
            continue;
          }

          if (mdAveragesFound) {
            mdAveragesFound = false;
            continue;
          } else if (mdRMSFFound) {
            mdRMSFFound = false;
            continue;
          }
          parseMDRecords(in, line);
          break;
      }
    }

    if (debug) {
      this.printDataSets();
    }
  }

  protected void parseMDRecords(BufferedReader in, String line) throws Exception {
    boolean thefirsttime = true;
    while (true) {
      if (thefirsttime) {
        thefirsttime = false;
      } else {
        line = in.readLine();
        if (line == null) {
          throw new Exception("Unexpected eof while parsing MD data");
        }
      }
      if (line.indexOf("----------------------") != -1) {
        return;
      }
      // ---

      List<String> terms;
      if (line.startsWith(" Ewald error estimate:")) {
        terms = new ArrayList<String>();
        terms.add("Ewald error estimate");
        terms.add(line.substring(line.indexOf(":") + 1).trim());
      } else {
        terms = getTokens(line);
      }

      for (int i = 0; i < terms.size(); i += 2) {
        this.addDescriptor(terms.get(i)); //descriptors.add(terms.get(i));
        if (i + 1 >= terms.size()) {
          throw new Exception("Unexpected eof while parsing MD data");
        }
        // ---
        if (terms.get(i).equals("NSTEP")) {
          try {
            int num = Integer.parseInt(terms.get(i + 1));
            //if (!data.containsKey(terms.get(i))) {
            //  List dt = new ArrayList();
            //  data.put(terms.get(i), dt);
            //}
            this.addData(terms.get(i), num); //data.get(terms.get(i)).add(num);
          } catch (Exception ex) {
            System.err.println("Cannot parse real value for " + terms.get(i) + " in line: " + line);
          }
        } else {
          try {
            double dbl = Double.parseDouble(terms.get(i + 1));
            //if (!data.containsKey(terms.get(i))) {
            //  List dt = new ArrayList();
            //  data.put(terms.get(i), dt);
            //}
            this.addData(terms.get(i), dbl); //data.get(terms.get(i)).add(dbl);
          } catch (Exception ex) {
            System.err.println("Cannot parse real value for " + terms.get(i) + " in line: " + line);
          }
        }
      }
    }
  }

  protected void parseMinRecords(BufferedReader in) throws Exception {
    String line = null;

// --- Read the first line
    line = in.readLine();
    if (line == null) {
      throw new Exception("Unexpected eof while parsing optimization data");
    }
    ++lineCount;
    // --- Reading the first line
    List<String> desc = descrRef.get("0");
    if (desc == null) {
      desc = new ArrayList<String>();
      descrRef.put("0", desc);
    }
    String[] tokens = line.trim().split("\\s+");
    for (int i = 0; i < Math.min(tokens.length, desc.size()); i++) {
      String label = desc.get(i);
      if (label.equals("NSTEP") || label.equals("NUMBER")) {
        try {
          int num = Integer.parseInt(tokens[i]);
          if (minFinalResFound && label.equals("NSTEP")) {
            minFinalResFound = false;
            Integer prevStep = this.getLastDataAsInteger(label);
            if (prevStep != null && prevStep == num) {
              return; // Skip reading in this step
            }
          }
          this.addData(label, num); //data.get(label).add(num);
        } catch (Exception ex) {
          System.err.println("Cannot parse integer value for " + label + " in line: " + lineCount);
        }
      } else if (label.equals("NAME")) {
        this.addData(label, tokens[i]); //data.get(label).add(tokens[i]);
      } else {
        try {
          double dbl = Double.parseDouble(tokens[i]);
          this.addData(label, dbl); //data.get(label).add(dbl);
        } catch (Exception ex) {
          System.err.println("Cannot parse real value for " + label + " in line: " + lineCount);
        }
      }
    }
    // --- Skipping the second line
    line = in.readLine();
    if (line == null) {
      throw new Exception("Unexpected eof while parsing optimization data");
    }
    // --- Reading next lines
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0 || line.contains("============")) {
        return;
      }

      if (line.startsWith(NMR_RESTR)) {
        line = line.substring(NMR_RESTR.length());
      }

      List<String> terms = getTokens(line);

      //line = line.replaceAll("=", "");
      //tokens = line.split("\\s+");
      for (int i = 0; i < terms.size(); i += 2) {
        //this.addDescriptor(terms.get(i)); // descriptors.add(terms.get(i));
        if (i + 1 >= terms.size()) {
          throw new Exception("Unexpected eof while parsing optimization data");
        }
        try {
          double dbl = Double.parseDouble(terms.get(i + 1));
          //if (!data.containsKey(terms.get(i))) {
          //  List dt = new ArrayList();
          //  data.put(terms.get(i), dt);
          //}
          this.addData(terms.get(i), dbl); //data.get(terms.get(i)).add(dbl);
        } catch (Exception ex) {
          System.err.println("Cannot parse real value for " + terms.get(i) + " in line: " + line);
        }
      }
    }
    if (line == null) {
      throw new Exception("Unexpected eof while parsing optimization data");
    }
  }

  public List<String> getTokens(String line) {
    List<String> terms = new ArrayList<String>();
    String[] tokens = line.split("=");
    for (int i = 0; i < tokens.length; i++) {
      if (i == 0) {
        terms.add(tokens[0].trim());
        continue;
      } else if (i + 1 == tokens.length) {
        terms.add(tokens[i].trim());
        continue;
      }
      tokens[i] = tokens[i].trim();
      terms.add(tokens[i].substring(0, tokens[i].indexOf(" ")));
      terms.add(tokens[i].substring(tokens[i].indexOf(" ")).trim());
    }
    return terms;
  }

  public static void main(String[] args) {
    AmberMdout amberMden = new AmberMdout();
    try {
      amberMden.parseData("01_min.out");
      System.out.println("Descriptors");
      for (String desc : amberMden.getDescriptors()) {
        System.out.println(desc);
      }
      System.out.println("Data size: " + amberMden.getDataSize());
      // ---
      amberMden.parseData("02_heat.out");
      System.out.println("Descriptors");
      for (String desc : amberMden.getDescriptors()) {
        System.out.println(desc);
      }
      System.out.println("Data size: " + amberMden.getDataSize());
      // ---
      amberMden.parseData("03_equil.out");
      System.out.println("Descriptors");
      for (String desc : amberMden.getDescriptors()) {
        System.out.println(desc);
      }
      System.out.println("Data size: " + amberMden.getDataSize());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
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
      if (line.contains(" NSTEP =") && line.contains("TIME(PS) =") && line.contains("TEMP(K) =")) {
        score += 0.25;
        line = in.readLine();
        if (line == null) {
          return score;
        }
        if (line.contains(" Etot   =") && line.contains("EKtot   =") && line.contains("EPtot      =")) {
          score += 0.25;
        }
        line = in.readLine();
        if (line == null) {
          return score;
        }
        if (line.contains(" BOND   =") && line.contains("ANGLE   =") && line.contains("DIHED      =")) {
          score += 0.25;
        }
        line = in.readLine();
        if (line == null) {
          return score;
        }
        if (line.contains(" 1-4 NB =") && line.contains("1-4 EEL =") && line.contains("VDWAALS    =")) {
          score += 0.25;
        }
        if (score >= 0.5) {
          return score;
        }
      }
      // ---- minimization info
      if (line.contains("NSTEP       ENERGY") && line.contains("RMS            GMAX") && line.contains("NAME    NUMBER")) {
        score += 0.25;
        line = in.readLine();
        if (line == null) {
          return score;
        }

        if (line.split("\\s").length == 6) {
          score += 0.25;
        }
        line = in.readLine();
        if (line == null) {
          return score;
        }
        if (line.trim().length() == 0) {
          score += 0.25;
        }
        line = in.readLine();
        if (line == null) {
          return score;
        }
        if (line.contains("BOND    =") && line.contains("ANGLE   =") && line.contains("DIHED      =")) {
          score += 0.25;
        }
        if (score >= 0.5) {
          return score;
        }
      }
      // ---
      if (score >= 1.0) {
        score = 1.0f;
        return score;
      }
    }

    return score;
  }

  public MoleculeInterface extractQMMMAtoms(MoleculeInterface mol, String filename) throws Exception {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(filename));
    } catch (Exception e) {
      throw new Exception("Error opening Amber prmtop file " + filename + " : " + e.getMessage());
    }
    try {
      return extractQMMMAtoms(mol, in);
    } catch (Exception e) {
      throw new Exception("Error while extracting QM/MM atoms from mdout file " + filename + " : " + e.getMessage());
    }
  }

  public MoleculeInterface extractQMMMAtoms(MoleculeInterface mol, BufferedReader in) throws Exception {

    int nlines, count, k;
    String line;
    AtomInterface atom = null;
    boolean qmmm_found = false;

    // --- Statrting to read
    String[] tokens = null;

    while ((line = in.readLine()) != null) {
      // --- Looking for line starting with %FLAG
      if (line.startsWith(QMMM_PATTERN)) {
        qmmm_found = true;
        break;
      }
    }

    if (!qmmm_found) {
      throw new Exception("Didn't find QM/MM atoms");
    }

    // -----------
    while ((line = in.readLine()) != null) {
      if (!line.startsWith("  QMMM:")) {
        break;
      }
      //  QMMM:     1        1      C       -0.0686    1.5103   -0.2115

      tokens = line.substring(22).trim().split("\\s+");
      if (tokens.length < 4) {
        throw new Exception("Error parsing line: " + line);
      }
      atom = mol.getNewAtomInstance();
      atom.setName(tokens[0]);
      atom.setAtomicNumber(ChemicalElements.getAtomicNumber(tokens[0]));
      atom.setAtomicMass(ChemicalElements.getAtomicNumber(tokens[0]));
      try {
        double xyz = Double.parseDouble(tokens[1]);
        atom.setX(xyz);
        xyz = Double.parseDouble(tokens[2]);
        atom.setY(xyz);
        xyz = Double.parseDouble(tokens[3]);
        atom.setZ(xyz);
      } catch (Exception ex) {
        throw new Exception("Error parsing Cartesian cooridnates in line " + line + " : " + ex.getMessage());
      }

      mol.addAtom(atom);
    }

    return mol;
  }

  // --- Implementation of ChartDataProvider interface
  public String cdpGetDescription() {
    return "Amber mdout file";
  }

  public void cdpParseData(String filename) throws Exception {
    parseData(filename);
  }

  public Set<String> cdpGetDescriptors() {
    return this.getDescriptors();
  }

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

  public float cdpIsThisFormat(String filename) throws Exception {

    return isThisFormat(filename);

  }
}
