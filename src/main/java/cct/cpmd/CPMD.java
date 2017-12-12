package cct.cpmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.GlobalConstants;
import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.OutputResultsInterface;
import cct.modelling.ChemicalElements;
import cct.modelling.StructureManagerInterface;
import cct.modelling.VIBRATIONAL_SPECTRUM;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */

enum CPMD_JOB_TYPE {
  MOLECULAR_DYNAMICS, VIBRATIONAL_ANALYSIS}

public class CPMD
      implements GlobalConstants, StructureManagerInterface, OutputResultsInterface {

    private CPMD_JOB_TYPE jobType = null;
    private StringWriter outputResume = null;
    private StringWriter warnings = null;
    private String trajFile = null, outputFile = null;
    private int netCharge = 0;
    private List<CPMDAtom> atoms = new ArrayList<CPMDAtom> ();
    private List<CPMDSnapshot> snapshots = null;
    private Map<String, List> nfiProperties = new HashMap<String, List> ();
    private boolean normalTermination = true;
    static final Logger logger = Logger.getLogger(CPMD.class.getCanonicalName());

    public CPMD() {
    }

    public int countAtoms() {
      return atoms.size();
    }

    public int getNumberOfAtoms() {
      return countAtoms();
    }

    public int countSnapshots() {
      return snapshots.size();
    }

    @Override
    public boolean isNormalTermination() {
      return normalTermination;
    }

    @Override
    public void setNormalTermination(boolean yes) {
      normalTermination = yes;
    }

    @Override
    public String getOutputResume() {
      if (outputResume == null) {
        return "No Output Resume";
      }
      return outputResume.toString();
    }

    @Override
    public String[] getAvailPropToChart() {
      if (nfiProperties.size() < 1) {
        return null;
      }
      String[] sa = new String[nfiProperties.size()];
      nfiProperties.keySet().toArray(sa);
      return sa;
    }

    public double[] getAllTerms(String term) {
      if (!nfiProperties.containsKey(term)) {
        return null;
      }

      List<Double> values = nfiProperties.get(term);
      double[] energies = new double[values.size()];
      for (int i = 0; i < values.size(); i++) {
        energies[i] = values.get(i);
      }
      return energies;
    }

    public void getMolecule(MoleculeInterface molec) throws
        Exception {
      if (molec == null) {
        throw new Exception(this.getClass().getCanonicalName() + " : molec == null");
      }
      if (atoms.size() < 1) {
        throw new Exception(this.getClass().getCanonicalName() + " : no CPMD atoms");
      }

      molec.addProperty(MoleculeInterface.ChargeProperty, new Integer(netCharge));
      //molec.addProperty(MoleculeInterface.MultiplicityProperty, new Integer(spinMultiplicity));

      molec.addMonomer("CPMD");

      for (int i = 0; i < atoms.size(); i++) {
        CPMDAtom ga = atoms.get(i);
        AtomInterface atom = molec.getNewAtomInstance();
        atom.setName(ga.name);
        atom.setAtomicNumber(ga.element);
        atom.setXYZ( (float) ga.x, (float) ga.y, (float) ga.z);
        molec.addAtom(atom);
      }
    }

    public void parseOutputFile(String filename) throws Exception {
      BufferedReader in = null;
      try {
        in = new BufferedReader(new FileReader(filename));
      }
      catch (Exception ex) {
        throw new Exception("Error opening CPMD output: " + ex.getMessage());
      }

      outputFile = filename;
      parseOutputFile(in);
    }

    public void parseOutputFile(BufferedReader in) throws Exception {
      String line;
      outputResume = new StringWriter(1024);
      try {
        while ( (line = in.readLine()) != null) {
          if (line.contains("VERSION ")) {
            outputResume.write(line + "\n");
          }

          else if (line.contains("PERFORM A VIBRATIONAL ANALYSIS ")) {
            outputResume.write(line + "\n");
            jobType = CPMD_JOB_TYPE.VIBRATIONAL_ANALYSIS;
          }
          else if (line.contains("CAR-PARRINELLO MOLECULAR DYNAMICS")) {
            outputResume.write(line + "\n");
            jobType = CPMD_JOB_TYPE.MOLECULAR_DYNAMICS;
          }

          else if (line.contains("CHARGE:")) {
            outputResume.write(line + "\n");
            try {
              float ch = Float.parseFloat(line.substring(line.indexOf(":") + 1).trim());
              netCharge = (int) ch;
            }
            catch (Exception ex) {
              addWarning("Encountered Error while parsing total charge: " + ex.getMessage() + " got: " + line);
            }
          }

          else if (line.contains("PROGRAM CPMD ")) {
            outputResume.write(line + "\n");
          }
          else if (line.contains("THE INPUT FILE IS:")) {
            outputResume.write(line + "\n");
          }
          else if (line.contains("THIS JOB RUNS ON:")) {
            outputResume.write(line + "\n");
          }
          else if (line.contains("CPU TIME :")) {
            outputResume.write(line + "\n");
          }
          else if (line.contains("ELAPSED TIME :")) {
            outputResume.write(line + "\n");
          }

          else if (line.contains("THE CURRENT DIRECTORY IS:")) {
            outputResume.write(line + "\n");
            if ( (line = in.readLine()) == null) {
              break;
            }
            outputResume.write(line + "\n");
          }
          else if (line.contains("THE JOB WAS SUBMITTED BY:")) {
            outputResume.write(line + "\n");
          }
          else if (line.contains(" FINAL RESULTS ")) {
            outputResume.write(line + "\n");
          }

          else if (line.contains("TRAJECTORIES ARE SAVED ON FILE")) {
            if ( (line = in.readLine()) == null) {
              break;
            }
            if (!line.contains("IS SAVED ON FILE")) {
              continue;
            }
            line = line.trim();
            trajFile = line.trim().substring(0, line.indexOf(" "));
          }

          else if (line.contains("***** ATOMS *****")) {
            readAtoms(in);
          }
          else if (line.contains("ATOMIC COORDINATES")) {
            readAtomCoordinates(in);
          }

          else if (line.contains(" NFI ")) {
            readFiniteIterations(in, line);
          }
          else if (line.contains("TOTAL ENERGY =")) {
            outputResume.write("\n" + line + "\n");
            while ( (line = in.readLine()) != null && line.trim().length() > 0) {
              outputResume.write(line + "\n");
            }
            outputResume.write("\n");
          }

        }
      }
      catch (Exception ex) {
        throw new Exception("Error reading CPMD output: " + ex.getMessage());
      }

      // --- Postprocessing

      if (trajFile != null) {
        try {
          String trajPath = getPath(outputFile) + trajFile;
          logger.info("Built a path to the trajectory file: " + trajPath);
          File tfile = new File(trajPath);
          if (tfile.exists() && tfile.canRead()) {
            this.parseTrajectoryFile(trajPath);
          }
        }
        catch (Exception ex) {
          System.err.println("CPMD Output file has a reference to the trajectory file. Error reading it: " + ex.getMessage() +
                             " Ignored...");
        }
      }

    }

    private void readFiniteIterations(BufferedReader in, String line) throws Exception {
      // --- parse properties
      //        NFI    EKINC   TEMPP           EKS      ECLASSIC          EHAM         DIS    TCPU

      StringTokenizer st = new StringTokenizer(line, " \t");
      if (st.countTokens() < 2) {
        return;
      }
      String[] keys = new String[st.countTokens() - 1];
      st.nextToken(); // Skip the first token - NFI

      int count = 0;
      while (st.hasMoreTokens()) {
        keys[count] = st.nextToken();
        nfiProperties.put(keys[count], new ArrayList<Double> ());
        ++count;
      }

      try {
        while ( (line = in.readLine()) != null) {
          if (line.trim().length() < 1) {
            return;
          }

          if (nfiProperties.size() == 7) { // MD Info
            try {
              nfiProperties.get(keys[0]).add(Double.parseDouble(line.substring(10, 19).trim())); // EKINC
              nfiProperties.get(keys[1]).add(Double.parseDouble(line.substring(19, 27).trim())); // TEMPP
              nfiProperties.get(keys[2]).add(Double.parseDouble(line.substring(27, 41).trim())); // EKS
              nfiProperties.get(keys[3]).add(Double.parseDouble(line.substring(41, 55).trim())); // ECLASSIC
              nfiProperties.get(keys[4]).add(Double.parseDouble(line.substring(55, 69).trim())); // EHAM
              nfiProperties.get(keys[5]).add(Double.parseDouble(line.substring(69, 81).trim())); // DIS
              nfiProperties.get(keys[6]).add(Double.parseDouble(line.substring(81, 89).trim())); // TCPU
            }
            catch (Exception ex) {
              throw ex;
            }
          }
        }
      }
      catch (Exception ex) {
        throw new Exception("Error reading header  finite itereations info: " + ex.getMessage());
      }
    }

    private void readAtomCoordinates(BufferedReader in) throws Exception {
      String line;
      double factor = ONE_BOHR;
      try {
        // Skipping the first line
        //**************************************************************

         if ( (line = in.readLine()) == null) {
           throw new Exception("Unexpected end-of-file while reading header  ATOMIC COORDINATES in CPMD output");
         }

        if (atoms.size() > 0) { // So, we need just to update atomic coordinates
          CPMDAtom[] atom_array = new CPMDAtom[atoms.size()];
          for (int i = 0; i < atoms.size(); i++) {
            if ( (line = in.readLine()) == null) {
              addWarning("Encountered an unxpected end-of-file while reading ATOMIC COORDINATES section...");
              break;
            }
            if (line.contains("*****************")) {
              addWarning("Encountered an unxpected end-of-data while reading ATOMIC COORDINATES section...");
              break;
            }

            try {
              atom_array[i] = readAtom(line, factor);
            }
            catch (Exception ex) {
              addWarning("Encountered Error(s) while parsing ATOMIC COORDINATES section: " + ex.getMessage());
              break;
            }
          }

          for (int i = 0; i < atoms.size(); i++) {
            CPMDAtom atom = atoms.get(i);
            atom.x = atom_array[i].x;
            atom.y = atom_array[i].y;
            atom.z = atom_array[i].z;
          }
        }

        // --- Read atoms' info for the first time
        else {
          while ( (line = in.readLine()) != null && !line.contains("**********")) {
            try {
              CPMDAtom atom = readAtom(line, factor);
              atoms.add(atom);
            }
            catch (Exception ex) {
              throw new Exception("Error while parsing ATOMIC COORDINATES section: " + ex.getMessage());
            }
          }
        }

      }
      catch (Exception ex) {
        throw new Exception("Error reading  ATOMIC COORDINATES in CPMD output: " + ex.getMessage());
      }

    }

    public static CPMDAtom readAtom(String line, double factor) throws Exception {
      StringTokenizer st = new StringTokenizer(line, " \t");
      if (st.countTokens() < 5) {
        throw new Exception("Expecting at least 5 tokens while reading atom info, got " + line);
      }
      st.nextToken(); // Skip the first
      String type = st.nextToken();
      int element = ChemicalElements.getAtomicNumber(type);
      double x = 0, y = 0, z = 0;
      try {
        x = Double.parseDouble(st.nextToken()) * factor;
        y = Double.parseDouble(st.nextToken()) * factor;
        z = Double.parseDouble(st.nextToken()) * factor;
      }
      catch (Exception ex) {
        throw new Exception("Error parsing atom coordinate, got " + line);
      }

      CPMDAtom atom = new CPMDAtom();
      atom.name = type;
      atom.element = element;
      atom.x = x;
      atom.y = y;
      atom.z = z;

      return atom;
    }

    void readAtoms(BufferedReader in) throws Exception {
      String line;
      double factor = 1.0;
      try {
        // Reading the header
        //   NR   TYPE        X(bohr)        Y(bohr)        Z(bohr)     MBL

        if ( (line = in.readLine()) == null) {
          throw new Exception("Unxpected end-of-file while reading header ATOMS in CPMD output");
        }
        if (line.contains("X(bohr)")) {
          factor = ONE_BOHR;
        }

        // --- Reading atoms
        //   1      H       8.800000       8.000000       8.000000       3

        while ( (line = in.readLine()) != null && !line.contains("**********")) {
          CPMDAtom atom = readAtom(line, factor);
          atoms.add(atom);
        }
      }
      catch (Exception ex) {
        throw new Exception("Error reading ATOMS in CPMD output: " + ex.getMessage());
      }
    }

    public boolean hasWarningMessages() {
      return warnings != null;
    }

    public String getWarnings() {
      if (hasWarningMessages()) {
        return warnings.toString();
      }
      return "No Warning Messages";
    }

    private void addWarning(String message) {
      if (warnings == null) {
        warnings = new StringWriter(512);
      }
      warnings.write(message + "\n");
    }

    public void parseTrajectoryFile(String filename) throws Exception {
      BufferedReader in = null;
      try {
        in = new BufferedReader(new FileReader(filename));
      }
      catch (Exception ex) {
        throw new Exception("Error opening CPMD trajectory: " + ex.getMessage());
      }

      parseTrajectoryFile(in);
    }

    public void parseTrajectoryFile(BufferedReader in) throws Exception {
      int natoms = 0;
      String line, type;
      double x, y, z;
      List<CPMDAtom> temp_atoms = null;

      try {
        while ( (line = in.readLine()) != null) {

          // --- Read number of atoms

          StringTokenizer st = new StringTokenizer(line, " \t");
          if (!st.hasMoreTokens()) {
            logger.info("Warning: expected number of atoms, got empty line...");
            break;
          }

          try {
            natoms = Integer.parseInt(st.nextToken());
          }
          catch (Exception ex) {
            logger.info("Warning: cannot parse number of atoms...");
            break;
          }

          if (atoms.size() > 0 && atoms.size() != natoms) {
            logger.info("Warning: expected " + atoms.size() + " atoms, trajectory is for " + natoms + " atoms...");
            break;
          }

          // --- Read STEP number

          if ( (line = in.readLine()) == null) {
            logger.info("Warning: unexpected end-of-file while reading in STEP number...");
            break;
          }

          if (!line.contains("STEP")) {
            logger.info("Warning: expected a line with a \"STEP\" word, got " + line);
            break;
          }

          st = new StringTokenizer(line, " \t");

          if (st.countTokens() < 2) {
            logger.info("Warning: expected at least two tokens for a \"STEP\" line, got " + line);
            break;
          }

          int snapshot = 0;
          st.nextToken();
          try {
            snapshot = Integer.parseInt(st.nextToken());
          }
          catch (Exception ex) {
            logger.info("Warning: cannot parse STEP number, got " + line);
            break;
          }

          // --- Reading atoms

          CPMDSnapshot snap = new CPMDSnapshot(natoms);
          boolean need_mol = atoms.size() < 1;
          if (need_mol) {
            temp_atoms = new ArrayList<CPMDAtom> (natoms);
          }
          int i = 0;
          for (; i < natoms; i++) {
            if ( (line = in.readLine()) == null) {
              logger.info("Warning: unexpected end-of-file while reading " + (i + 1) + " atom...");
              break;
            }
            st = new StringTokenizer(line, " \t");
            if (st.countTokens() < 4) {
              logger.info("Warning: expected at least 4 tokens while reading " + (i + 1) + " atom, got " + line);
              break;
            }

            type = st.nextToken();

            try {
              x = Double.parseDouble(st.nextToken());
              y = Double.parseDouble(st.nextToken());
              z = Double.parseDouble(st.nextToken());
            }
            catch (Exception ex) {
              logger.info("Warning: error parsing atom coordinate(s) of " + (i + 1) + " atom, got " + line);
              break;
            }

            snap.setCoordinates(x, y, z, i);

            if (need_mol) {
              CPMDAtom atom = new CPMDAtom();
              atom.name = type;
              atom.element = ChemicalElements.getAtomicNumber(type);
              atom.x = x;
              atom.y = y;
              atom.z = z;
              temp_atoms.add(atom);
            }

          }

          if (i < natoms) {
            break;
          }

          if (snapshots == null) {
            snapshots = new ArrayList<CPMDSnapshot> ();
          }
          snapshots.add(snap);
          if (need_mol) {
            atoms.addAll(temp_atoms);
          }

        }
      }
      catch (Exception ex) {
        throw new Exception("Error reading CPMD trajectory: " + ex.getMessage());
      }
    }

    private String getPath(String abs_path) {
      int index = 0;
      if ( (index = abs_path.lastIndexOf("/")) != -1) {
        return abs_path.substring(0, index + 1);
      }
      else if ( (index = abs_path.lastIndexOf("\\")) != -1) {
        return abs_path.substring(0, index + 1);
      }
      return "";
    }

    public float[][] getStructure(int n) {
      return getStructure(n, null);
    }

    public float[][] getStructure(int n, String term) {
      if (snapshots == null || snapshots.size() < 1 || n >= snapshots.size()) {
        System.err.println(this.getClass().getCanonicalName() +
                           ": snapshots == null || snapshots.size() < 1 || n >= snapshots.size()");
        return null;
      }

      CPMDSnapshot snap = snapshots.get(n);
      return snap.getCoordinates();
    }

    public void selectStructure(int number) throws Exception {
      throw new Exception(this.getClass().getCanonicalName() + ": selectStructure(int number) is not implemented yet");
    }

    public void selectStructure(int number, String term) throws Exception {
      throw new Exception(this.getClass().getCanonicalName() + ": selectStructure(int number, String term) is not implemented yet");
    }

    public VIBRATIONAL_SPECTRUM[] availableVibrationalSpectra() {
      return null;
    }

    public int countFrequencies() {
      return 0;
    }

    public int countSpectra() {
      return 0;
    }

    public boolean hasJobSummary() {
      return outputResume != null;
    }

    public boolean hasDisplacementVectors() {
      return false;
    }

    public boolean hasInteractiveChart() {
      return false;
    }

    public boolean hasVCDSpectrum() {
      return false;
    }

    public boolean hasUDepolSpectrum() {
      return false;
    }

    public boolean hasPDepolSpectrum() {
      return false;
    }

    public boolean hasRamanSpectrum() {
      return false;
    }

    public boolean hasInfraredSpectrum() {
      return false;
    }

    public float[][] getDisplacementVectors(int n) {
      return null;
    }

    public double getFrequency(int n) {
      return 0;
    }

    public double getSpectrumValue(int n, VIBRATIONAL_SPECTRUM spectrum) {
      return 0;
    }

    public void getSpectrum(double[] x, double[] y, int dim, VIBRATIONAL_SPECTRUM type) throws Exception {

    }

  }

// *****************************************************************************
  class CPMDAtom {
    String name;
    int element;
    double x, y, z;
  }

  class CPMDSnapshot {
    private float[][] coord;

    public CPMDSnapshot(int n_atoms) {
      coord = new float[n_atoms][3];
    }

    public void setCoordinates(double[] xyz, int n) throws Exception {
      if (n < 0 || n >= coord.length) {
        throw new Exception(this.getClass().getCanonicalName() + ": setCoordinates: index out of range");
      }
      coord[n][0] = (float) xyz[0];
      coord[n][1] = (float) xyz[1];
      coord[n][2] = (float) xyz[2];
    }

    public void setCoordinates(double x, double y, double z, int n) throws Exception {
      if (n < 0 || n >= coord.length) {
        throw new Exception(this.getClass().getCanonicalName() + ": setCoordinates: index out of range");
      }
      coord[n][0] = (float) x;
      coord[n][1] = (float) y;
      coord[n][2] = (float) z;
    }

    public float[][] getCoordinates() {
      return coord;
    }

  }
