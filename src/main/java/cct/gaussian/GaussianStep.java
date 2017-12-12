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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.OutputResultsInterface;
import cct.modelling.CCTAtomTypes;
import cct.modelling.Frequencies;
import cct.modelling.MolecularGeometry;
import cct.modelling.Molecule;
import cct.modelling.StructureManagerInterface;
import cct.modelling.VIBRATIONAL_SPECTRUM;
import cct.modelling.VIBRATIONAL_SPECTRUM_CURVE;
import cct.vecmath.Point3f;
import javax.swing.JOptionPane;

/**
 * <p>
 * Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>
 * Description: Collection of Computational Chemistry related code</p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>
 * Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public class GaussianStep
    implements OutputResultsInterface, StructureManagerInterface {

  private boolean Debug = true;
  private MoleculeInterface molecule;
  private List geometries = new ArrayList();
  private List<GaussianFrequency> frequencies = null;
  private Map<String, String> outputResume;
  private Set<String> propertiesToChart = new HashSet<String>();
  private List<GaussianSnapshot> snapshots = new ArrayList<GaussianSnapshot>();
  private boolean finalStep = false;
  private boolean guessBonds = true, guessAtomTypes = true, normalTermination = true;
  private String description = "Gaussian Step";
  private String scfEnergy = null;
  private Map<String, List> termVsStrReference = new HashMap<String, List>();
  static final Logger logger = Logger.getLogger(GaussianStep.class.getCanonicalName());

  public GaussianStep() {
  }

  public void setMolecule(MoleculeInterface mol) {
    molecule = mol;
    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      return;
    }
    if (guessBonds) {
      Molecule.guessCovalentBonds(molecule);
    }
    if (guessAtomTypes) {
      Molecule.guessAtomTypes(molecule, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
    }
  }

  public void setGuessCovalentBonds(boolean guess) {
    guessBonds = guess;
  }

  @Override
  public void setNormalTermination(boolean yes) {
    normalTermination = yes;
  }

  @Override
  public boolean isNormalTermination() {
    return normalTermination;
  }

  public void setSnapshots(List<GaussianSnapshot> snaps) {
    snapshots.addAll(snaps);
  }

  public void setChartProperties(Set<String> properties) {
    propertiesToChart.addAll(properties);
  }

  public void setGuessAtomTypes(boolean guess) {
    guessAtomTypes = guess;
  }

  public void setOutputResume(Map<String, String> outputRes) {
    outputResume = new LinkedHashMap<String, String>(outputRes);
  }

  public void setGeometries(List geoms) {
    if (geoms != null) {
      geometries = new ArrayList(geoms);
    }
  }

  public void setFrequencies(List<GaussianFrequency> freq) {
    frequencies = new ArrayList<GaussianFrequency>(freq);
  }

  public boolean isFinalStep() {
    return finalStep;
  }

  public void setFinalStep(boolean yes) {
    finalStep = yes;
  }

  public MoleculeInterface getMolecule() {
    return molecule;
  }

  public List getGeometries() {
    return geometries;
  }

  @Override
  public void getSpectrum(double[] x, double[] y, int dim, VIBRATIONAL_SPECTRUM type) throws Exception {
    getSpectrum(x, y, dim, spectrumType2String(type));
  }

  private String spectrumType2String(VIBRATIONAL_SPECTRUM type) throws Exception {
    String name;

    switch (type) {
      case INFRARED_SPECTRUM:
        name = "IR Inten";
        break;
      case RAMAN_SPECTRUM:
        name = "Raman Activ";
        break;
      case VCD_ROTATIONAL_STRENGTH_SPECTRUM:
        name = "Rot. str.";
        break;
      case P_DEPOLARIZATION_SPECTRUM:
        name = "Depolar (P)";
        break;
      case U_DEPOLARIZATION_SPECTRUM:
        name = "Depolar (U)";
        break;
      default:
        throw new Exception("Unknown vibrational spectrum " + type.toString());
    }
    return name;
  }

  public void getSpectrum(double[] x, double[] y, int dim, String name) throws Exception {
    if (frequencies == null || frequencies.size() < 1) {
      throw new Exception("No frequencies info in gaussian output file");
    }
    GaussianFrequency freq = frequencies.get(0);
    if (!freq.hasSpectrum(name)) {
      throw new Exception("Does not have spectrum " + name);
    }

    double[] freqs = new double[frequencies.size()];
    double[] values = new double[frequencies.size()];

    for (int i = 0; i < frequencies.size(); i++) {
      freq = frequencies.get(i);
      freqs[i] = freq.getProperty("Frequencies");
      values[i] = freq.getProperty(name);
    }

    Frequencies.makeCurve(freqs, values, x, y, dim, VIBRATIONAL_SPECTRUM_CURVE.GAUSSIAN);
    //Frequencies.makeCurve(freqs, values, x, y, dim, VIBRATIONAL_SPECTRUM_CURVE.LORENTZ);
  }

  public double[] getFrequencies(VIBRATIONAL_SPECTRUM type) throws Exception {
    return getFrequencies(spectrumType2String(type));
  }

  public double[] getFrequencies(String name) throws Exception {
    if (frequencies == null || frequencies.size() < 1) {
      throw new Exception("No frequencies info in gaussian output file");
    }
    GaussianFrequency freq = frequencies.get(0);
    if (!freq.hasSpectrum(name)) {
      throw new Exception("Does not have spectrum " + name);
    }

    double[] freqs = new double[frequencies.size()];
    //values = new double[frequencies.size()];

    for (int i = 0; i < frequencies.size(); i++) {
      freq = frequencies.get(i);
      freqs[i] = freq.getProperty("Frequencies");
      //values[i] = freq.getProperty(name);
    }

    return freqs;
  }

  public double[] getIntensities(VIBRATIONAL_SPECTRUM type) throws Exception {
    return getIntensities(spectrumType2String(type));
  }

  public double[] getIntensities(String name) throws Exception {
    if (frequencies == null || frequencies.size() < 1) {
      throw new Exception("No frequencies info in gaussian output file");
    }
    GaussianFrequency freq = frequencies.get(0);
    if (!freq.hasSpectrum(name)) {
      throw new Exception("Does not have spectrum " + name);
    }

    //double [] freqs = new double[frequencies.size()];
    double[] values = new double[frequencies.size()];

    for (int i = 0; i < frequencies.size(); i++) {
      freq = frequencies.get(i);
      //freqs[i] = freq.getProperty("Frequencies");
      values[i] = freq.getProperty(name);
    }

    return values;
  }

  @Override
  public double getSpectrumValue(int n, VIBRATIONAL_SPECTRUM spectrum) {
    if (frequencies == null) {
      System.err.println("frequencies == null");
      return 0;
    }

    if (n < 0 || n >= frequencies.size()) {
      System.err.println("n < 0 || n >= frequencies.size()");
      return 0;
    }

    GaussianFrequency freq = frequencies.get(n);

    if (!freq.hasSpectrum(spectrum)) {
      System.err.println("Does not have spectrum " + spectrum);
      return 0;
    }

    try {
      Double value = freq.getProperty(spectrum);
      return value;
    } catch (Exception ex) {
      System.err.println("value == null");
      return 0;
    }
  }

  @Override
  public double getFrequency(int n) {
    if (frequencies == null) {
      System.err.println("frequencies == null");
      return 0;
    }

    if (n < 0 || n >= frequencies.size()) {
      System.err.println("frequencies == null");
      return 0;
    }

    GaussianFrequency freq = frequencies.get(n);
    try {
      Double value = freq.getProperty("Frequencies");
      return value;
    } catch (Exception ex) {
      System.err.println("value == null");
      return 0;
    }
  }

  @Override
  public float[][] getDisplacementVectors(int n) {
    if (frequencies == null) {
      System.err.println("getDisplacementVectors: frequencies == null");
      return null;
    }

    if (n < 0 || n >= frequencies.size()) {
      System.err.println("getDisplacementVectors: n < 0 || n >= frequencies.size()");
      return null;
    }

    GaussianFrequency freq = frequencies.get(n);
    return freq.getDisplacementVectors();
  }

  @Override
  public boolean hasInfraredSpectrum() {
    return hasSpectrum("IR Inten");
  }

  @Override
  public boolean hasVCDSpectrum() {
    return hasSpectrum("Rot. str.");
  }

  @Override
  public boolean hasUDepolSpectrum() {
    return hasSpectrum("Depolar (U)");
  }

  @Override
  public boolean hasPDepolSpectrum() {
    return hasSpectrum("Depolar (P)");
  }

  @Override
  public boolean hasRamanSpectrum() {
    return hasSpectrum("Raman Activ");
  }

  @Override
  public boolean hasJobSummary() {
    if (outputResume == null) {
      return false;
    }
    return outputResume.size() > 0;
  }

  @Override
  public boolean hasInteractiveChart() {
      return !(propertiesToChart.size() < 1 || snapshots.size() < 2);
  }

  @Override
  public boolean hasDisplacementVectors() {
    if (frequencies == null || frequencies.size() < 1) {
      return false;
    }

    GaussianFrequency freq = frequencies.get(0);
    return freq.hasDisplacementVectors();
  }

  @Override
  public int countSpectra() {
    if (frequencies == null || frequencies.size() < 1) {
      return 0;
    }
    GaussianFrequency freq = frequencies.get(0);
    return freq.vibrationalSpectraCount();
  }

  @Override
  public int countFrequencies() {
    if (frequencies == null) {
      return 0;
    }
    return frequencies.size();
  }

  /**
   * Returns names of available vibrational spectra (if any)
   *
   * @return String[]
   */
  @Override
  public VIBRATIONAL_SPECTRUM[] availableVibrationalSpectra() {
    if (frequencies == null || frequencies.size() < 1) {
      return null;
    }
    GaussianFrequency freq = frequencies.get(0);
    return freq.availableVibrationalSpectra();
  }

  public boolean hasSpectrum(String type) {
    if (frequencies == null || frequencies.size() < 1) {
      return false;
    }
    GaussianFrequency freq = frequencies.get(0);
      return freq.hasSpectrum( type );
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String descr) {
    description = descr;
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
  public String getOutputResume() {
    StringWriter sWriter = new StringWriter();
    Set set = outputResume.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String line = me.getValue().toString();
      sWriter.write(line + "\n");
    }

    if (!isNormalTermination()) {
      sWriter.write(
          "\n***** !!!!! IT IS NOT A NORMAL TERMINATION of GAUSSIAN !!!!! *****");
    }
    return sWriter.toString();
  }

  @Override
  public float[][] getStructure(int n) {
    return getStructure(n, scfEnergy);
  }

  @Override
  public float[][] getStructure(int n, String term) {
    if (!propertiesToChart.contains(term) || snapshots.size() < 1) {
      return null;
    }

    if (snapshots == null || snapshots.size() < 1) {
      return null;
    }

    List<Integer> references = termVsStrReference.get(term);
    if (references == null) {
      System.err.println("references == null");
      return null;
    } else if (n >= references.size()) {
      System.err.println("n >= references.size()");
      return null;
    }

    if (Debug) {
      logger.info("getStructure: # " + n);
    }

    GaussianSnapshot snapshot = snapshots.get(references.get(n));
    MolecularGeometry geom = snapshot.getGeometry(GaussianOutput.STANDARD_ORIENTATION_KEY); // GaussView uses standard orientation
    if (geom == null) {
      geom = snapshot.getGeometry(GaussianOutput.INPUT_ORIENTATION_KEY);
    }
    if (geom == null) {
      geom = snapshot.getGeometry(GaussianOutput.Z_MATRIX_ORIENTATION_KEY);
    }

    if (geom == null || geom.size() < 1) {
      System.err.println(this.getClass().getCanonicalName() + " : no atoms in selected snapshot");
      return null;
    }

    float[][] coords = new float[geom.size()][3];
    for (int i = 0; i < geom.size(); i++) {
      Point3f c = geom.getCoordinates(i);

      coords[i][0] = c.getX();
      coords[i][1] = c.getY();
      coords[i][2] = c.getZ();
      if (Debug && i == geom.size() - 1) {
        logger.info(i + " : x=" + coords[i][0] + " y=" + coords[i][1] + " z=" + coords[i][2]);
      }
    }
    return coords;
  }

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

  @Override
  public double[] getAllTerms(String term) {
    if (!propertiesToChart.contains(term) || snapshots.size() < 1) {
      return null;
    }

    List<Integer> references = new ArrayList<Integer>(snapshots.size());
    List<Double> values = new ArrayList<Double>(snapshots.size());
    for (int i = 0; i < snapshots.size(); i++) {
      GaussianSnapshot snapshot = snapshots.get(i);
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

}
