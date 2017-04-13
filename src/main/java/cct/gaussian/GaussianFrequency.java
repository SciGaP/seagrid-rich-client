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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import cct.modelling.VIBRATIONAL_SPECTRUM;
import cct.vecmath.Point3f;

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
public class GaussianFrequency {
  private static final String aliasInfraredSpectrum = "Infrared Spectrum";
  private static final String aliasVCDRot = "VCD Rotational Stregth Spectrum";
  private static final String aliasRamanSpectrum = "Raman Spectrum";
  private static final String aliasPDepSpectrum = "P-Depolarization Spectrum";
  private static final String aliasUDepSpectrum = "U-Depolarization Spectrum";

  private static final Map<String, String> aliases = new HashMap<String, String> ();
  private static final Map<String, String> valueTitles = new HashMap<String, String> ();
  private static final Map<VIBRATIONAL_SPECTRUM, String> refTable = new HashMap<VIBRATIONAL_SPECTRUM, String> ();

  static {
    aliases.put(aliasInfraredSpectrum, "IR Inten");
    aliases.put(aliasVCDRot, "Rot. str.");
    aliases.put(aliasRamanSpectrum, "Raman Activ");
    aliases.put(aliasPDepSpectrum, "Depolar (P)");
    aliases.put(aliasUDepSpectrum, "Depolar (U)");

    valueTitles.put("IR Inten", "Intensity");
    valueTitles.put("Rot. str.", "Strength");
    valueTitles.put("Raman Activ", "Activity");
    valueTitles.put("Depolar (P)", "Depolarization");
    valueTitles.put("Depolar (U)", "Depolarization");

    refTable.put(VIBRATIONAL_SPECTRUM.INFRARED_SPECTRUM, "IR Inten");
    refTable.put(VIBRATIONAL_SPECTRUM.VCD_ROTATIONAL_STRENGTH_SPECTRUM, "Rot. str.");
    refTable.put(VIBRATIONAL_SPECTRUM.RAMAN_SPECTRUM, "Raman Activ");
    refTable.put(VIBRATIONAL_SPECTRUM.P_DEPOLARIZATION_SPECTRUM, "Depolar (P)");
    refTable.put(VIBRATIONAL_SPECTRUM.U_DEPOLARIZATION_SPECTRUM, "Depolar (U)");
  }

  private Map<String, Double> properties = new HashMap<String, Double> ();
  private int nVectors;
  private String symbol;
  private double frequencyValue;
  private float reducedMass, frcConst, IR_Intencity, Raman_Activ, Depolar_P, Depolar_U;
  Point3f vectors[];

  public GaussianFrequency(int n_vectors) {
    nVectors = n_vectors;
    vectors = new Point3f[nVectors];
    for (int i = 0; i < nVectors; i++) {
      vectors[i] = new Point3f();
    }
  }

  public boolean hasDisplacementVectors() {
      return !(vectors == null || vectors.length < 1);
  }

  public float[][] getDisplacementVectors() {
    if (vectors == null || vectors.length < 1) {
      return null;
    }
    float[][] vect = new float[vectors.length][3];
    for (int i = 0; i < vectors.length; i++) {
      vect[i][0] = vectors[i].getX();
      vect[i][1] = vectors[i].getY();
      vect[i][2] = vectors[i].getZ();
    }
    return vect;
  }

  public void setSymbol(String sym) {
    symbol = sym;
  }

  public boolean hasSpectrum(VIBRATIONAL_SPECTRUM spectrum) {
    if (properties == null || properties.size() < 1) {
      return false;
    }
    if (!properties.containsKey("Frequencies")) {
      return false;
    }

    String spectrum_name = refTable.get(spectrum);
    if (spectrum_name == null) {
      return false;
    }

      return properties.containsKey( spectrum_name );

  }

  public boolean hasSpectrum(String spectrum) {
    if (properties == null || properties.size() < 1) {
      return false;
    }
    if (!properties.containsKey("Frequencies")) {
      return false;
    }

    String spectrum_name = spectrum;
    if (aliases.containsKey(spectrum)) {
      spectrum_name = aliases.get(spectrum);
    }

      return properties.containsKey( spectrum_name );

  }

  public int vibrationalSpectraCount() {
    if (properties == null || properties.size() < 1) {
      return 0;
    }
    if (!properties.containsKey("Frequencies")) {
      return 0;
    }
    int count = 0;
    if (properties.containsKey("IR Inten")) {
      ++count;
    }
    if (properties.containsKey("Rot. str.")) {
      ++count;
    }

    return count;
  }

  public VIBRATIONAL_SPECTRUM[] availableVibrationalSpectra() {
    if (properties == null || properties.size() < 1) {
      return null;
    }
    if (!properties.containsKey("Frequencies")) {
      return null;
    }

    List<VIBRATIONAL_SPECTRUM> spectra = new ArrayList<VIBRATIONAL_SPECTRUM> ();
    if (properties.containsKey("IR Inten")) {
      spectra.add(VIBRATIONAL_SPECTRUM.INFRARED_SPECTRUM);
    }
    if (properties.containsKey("Rot. str.")) {
      spectra.add(VIBRATIONAL_SPECTRUM.VCD_ROTATIONAL_STRENGTH_SPECTRUM);
    }
    if (properties.containsKey("Raman Activ")) {
      spectra.add(VIBRATIONAL_SPECTRUM.RAMAN_SPECTRUM);
    }
    if (properties.containsKey("Depolar (P)")) {
      spectra.add(VIBRATIONAL_SPECTRUM.P_DEPOLARIZATION_SPECTRUM);
    }
    if (properties.containsKey("Depolar (U)")) {
      spectra.add(VIBRATIONAL_SPECTRUM.U_DEPOLARIZATION_SPECTRUM);
    }

    if (spectra.size() < 1) {
      return null;
    }

    VIBRATIONAL_SPECTRUM[] sp = new VIBRATIONAL_SPECTRUM[spectra.size()];
    spectra.toArray(sp);

    return sp;
  }

  public void setProperty(String name, double value) {
    properties.put(name, new Double(value));
  }

  public double getProperty(VIBRATIONAL_SPECTRUM spectrum) throws Exception {
    String name = refTable.get(spectrum);
    if (name == null) {
      throw new Exception("Unknown spectrum " + spectrum.toString());
    }
    return getProperty(name);
  }

  public double getProperty(String name) throws Exception {

    if (aliases.containsKey(name)) {
      name = aliases.get(name);
    }

    if (!properties.containsKey(name)) {
      throw new Exception("No such frequencies property " + name);
    }
    return properties.get(name);
  }

  public String getXTitle(String name) throws Exception {

    if (aliases.containsKey(name)) {
      name = aliases.get(name);
    }

    if (!properties.containsKey(name)) {
      throw new Exception("No such frequencies property " + name);
    }
    return "Frequency";
  }

  public String getYTitle(String name) throws Exception {

    if (aliases.containsKey(name)) {
      name = aliases.get(name);
    }

    if (!properties.containsKey(name)) {
      throw new Exception("No such frequencies property " + name);
    }
    return valueTitles.get(name);
  }

  /**
   * Parses lines of type
   * Frequencies --  1739.7799              1739.7799              3186.7640
   * @param line String - line to be parsed
   * @param values double[3]
   * @return String - property name
   * @throws Exception
   */
  static public String parsePropLine(String line, double[] values) throws Exception {
    int n = line.indexOf("--");
    if (n == -1 || n == 0) {
      throw new Exception("Cannot find frequency property");
    }
    else if (n + 2 >= line.length()) {
      throw new Exception("No values for frequency property");
    }
    String name = line.substring(0, n - 1).trim();
    String val = line.substring(n + 2).trim();
    StringTokenizer st = new StringTokenizer(val, " \t");

    if (st.countTokens() < 1 || st.countTokens() > 3) {
      throw new Exception("Expecting 1 to 3 values, got " + st.countTokens());
    }

    //values = new double[st.countTokens()];

    int num = st.countTokens();
    for (int i = 0; i < num; i++) {
      String token = st.nextToken();
      try {
        values[i] = Double.parseDouble(token);
      }
      catch (Exception ex) {
        throw new Exception("Cannot parse value (" + token + ") for property " + name + " : " + ex.getMessage());
      }
    }

    return name;
  }
}
