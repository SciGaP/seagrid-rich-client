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

package cct.math;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import cct.GlobalConstants;
import cct.tools.FileFilterImpl;

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
public abstract class VolumeData
    implements GlobalConstants {

  protected String commentLine_1 = null, commentLine_2 = null;

  protected float dataOrigin[] = new float[3];
  protected int numberOfVoxels[] = new int[3];
  protected float axisVectors[][] = new float[3][3];

  protected int nVolumetrics = 1;
  protected int labels[] = null;
  protected double data[][][][] = null;

  protected float minFValue = 0, maxFValue = 0;

  private static final String RAW_FILE_DESCRIPTION = "Raw Volume Data (*.raw)";

  private static String lastPWDKey = "lastPWD";
  private Preferences prefs = Preferences.userNodeForPackage(getClass());
  private File currentWorkingDirectory = null;
  private JFileChooser chooser = null;
  protected FileFilterImpl[] filters = null;

  public VolumeData() {
    filters = new FileFilterImpl[1];

    filters[0] = new FileFilterImpl();
    filters[0].addExtension("raw");
    filters[0].addExtension("RAW");
    filters[0].setDescription(RAW_FILE_DESCRIPTION);
  }

  public javax.swing.filechooser.FileFilter[] getDefaultSaveFileFilters() {
    return filters;
  }

  public abstract void parseVolumetricData(String filename) throws Exception;

  public float[] getDataOrigin() {
    return dataOrigin;
  }

  public int[] getNumberOfVoxels() {
    return numberOfVoxels;
  }

  public float[][] getAxisVectors() {
    return axisVectors;
  }

  public double[][][] getVolumetricData() {
    return data[0];
  }

  public double[][][] getVolumetricData(int n) {
    return data[n];
  }

  public void removeVolumetricData(int n) {

    if (nVolumetrics < 1) {
      System.err.println("removeVolumetricData: nVolumetrics < 1");
      return;
    }

    if (n < 0 || n >= nVolumetrics) {
      System.err.println("removeVolumetricData: n < 0 || n>= nVolumetrics");
      return;
    }

    if (nVolumetrics == 1) {
      data = null;
      nVolumetrics = 0;
      return;
    }

    double[][][][] newData = new double[data.length - 1][][][];
    for (int i = 0, j = 0; i < data.length; i++) {
      if (i != n) {
        newData[j] = data[i];
        ++j;
      }
    }
    --nVolumetrics;

    data = newData;
    newData = null;
  }

  public int countCubes() {
    return nVolumetrics;
  }

  public String getCubeLabel(int n) {
    if (n < 0 || n >= nVolumetrics) {
      System.err.println(this.getClass().getCanonicalName() + " getVolumetricLabel: n<0 || n>=nVolumetrics");
      return null;
    }
    if (labels == null) {
      return "";
    }
    return "MO: " + String.valueOf(labels[n]);
  }

  public String getTitle() {
    return this.commentLine_1;
  }

  public String getCubeDescription() {
    return this.commentLine_2;
  }

  public float getMinFunValue() {
    return minFValue;
  }

  public float getMaxFunValue() {
    return maxFValue;
  }

  public void checkCubeNumber(int n) throws Exception {
    if (nVolumetrics < 1) {
      System.err.println("saveVolumeData: nVolumetrics < 1");
      throw new Exception("saveVolumeData: nVolumetrics < 1");
    }

    if (n < 0 || n >= nVolumetrics) {
      System.err.println("saveVolumeData: n < 0 || n>=nVolumetrics");
      throw new Exception("saveVolumeData: n < 0 || n>=nVolumetrics");
    }
  }

  public void saveVolumeData(int n) throws Exception {

    checkCubeNumber(n);

    if (chooser == null) {
      chooser = new JFileChooser();
      for (int i = 0; i < filters.length; i++) {
        chooser.addChoosableFileFilter(filters[i]);
      }
    }

    chooser.setDialogTitle("Save Volume Data File");
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);

    if (currentWorkingDirectory != null) {
      chooser.setCurrentDirectory(currentWorkingDirectory);
    }
    else {
      String lastPWD = prefs.get(lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory() && currentWorkingDirectory.exists()) {
          chooser.setCurrentDirectory(currentWorkingDirectory);
        }
      }
    }

    int returnVal = JFileChooser.CANCEL_OPTION;

    returnVal = chooser.showSaveDialog(null);

    if (returnVal == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (returnVal == JFileChooser.APPROVE_OPTION) {

      javax.swing.filechooser.FileFilter selectedFilter = chooser.getFileFilter();
      String fileName = chooser.getSelectedFile().getPath();

      if (selectedFilter.getDescription().equals(RAW_FILE_DESCRIPTION)) {

      }
      else {
        throw new Exception("Unknown volume data file description: " + selectedFilter.getDescription());
      }

      currentWorkingDirectory = chooser.getCurrentDirectory();
      try {
        prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
      }
      catch (Exception ex) {
        System.err.println("Cannot save cwd: " + ex.getMessage() + " Ignored...");
      }

      saveVolumeData(n, fileName, selectedFilter.getDescription());
    }
  }

  public void saveVolumeData(int n, String file_name, String format) throws Exception {
    if (format.equals(RAW_FILE_DESCRIPTION)) {

      if (file_name.endsWith(".")) {
        file_name += "raw";
      }
      else if (!file_name.endsWith(".raw") && !file_name.endsWith(".RAW")) {
        file_name += ".raw";
      }

      try {
        FileOutputStream fos = new FileOutputStream(file_name);
        // Wrap the FileOutputStream with a DataOutputStream
        DataOutputStream data_out = new DataOutputStream(fos);
        data_out.writeByte(8); // --- float - 4 bytes per voxel
        data_out.writeInt(numberOfVoxels[0]); // --- NX
        data_out.writeInt(numberOfVoxels[1]); // --- NY
        data_out.writeInt(numberOfVoxels[2]); // --- NZ
        // --- volume data is the complete volumetric data with Z as the fastest varying loop index.
        for (int i = 0; i < numberOfVoxels[0]; i++) {
          for (int j = 0; j < numberOfVoxels[1]; j++) {
            for (int k = 0; k < numberOfVoxels[2]; k++) {
              data_out.writeFloat( (float) data[n][i][j][k]);
            }
          }
        }
        data_out.close();
      }
      catch (Exception ex) {
        throw new Exception("Error saving " + file_name + " : " + ex.getMessage());
      }
    }
    else {
      throw new Exception("Unknown volume data file format: " + format);
    }

  }

  public void getNormalizedHistogram(double[] x, double[] y, int nBins, int n) throws Exception {
    getHistogram(x, y, nBins, n);
    double norm = numberOfVoxels[0] * numberOfVoxels[1] * numberOfVoxels[2];
    for (int i = 0; i < nBins; i++) {
      y[i] /= norm;
    }
  }

  public void getHistogram(double[] x, double[] y, int nBins, int n) throws Exception {
    checkCubeNumber(n);

    // -- Find min & max function values
    double fmin = data[n][0][0][0];
    double fmax = data[n][0][0][0];

    for (int i = 0; i < numberOfVoxels[0]; i++) {
      for (int j = 0; j < numberOfVoxels[1]; j++) {
        for (int k = 0; k < numberOfVoxels[2]; k++) {
          if (fmin > data[n][i][j][k]) {
            fmin = data[n][i][j][k];
          }
          else if (fmax < data[n][i][j][k]) {
            fmax = data[n][i][j][k];
          }
        }
      }
    }

    double width = (fmax - fmin) / nBins;
    for (int i = 0; i < nBins; i++) {
      x[i] = fmin + width * i;
      y[i] = 0;
    }

    for (int i = 0; i < numberOfVoxels[0]; i++) {
      for (int j = 0; j < numberOfVoxels[1]; j++) {
        for (int k = 0; k < numberOfVoxels[2]; k++) {
          int index = (int) ( (data[n][i][j][k] - fmin) / width);
          if (index >= nBins) {
            index = nBins - 1;
          }
          ++y[index];
        }
      }
    }
  }

  public static void main(String[] args) {
    //VolumeData volumedata = new VolumeData();
  }
}
