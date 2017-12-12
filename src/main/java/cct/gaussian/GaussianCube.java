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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.math.VolumeData;
import cct.modelling.ChemicalElements;
import cct.tools.FileFilterImpl;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class GaussianCube extends VolumeData {

  static int BOHR_UNITS = 0;
  static int ANGSTROM_UNITS = 1;

  private static String lastPWDKey = "lastPWD";
  private static String MO_COEFF_TAG = "MO coefficients".toUpperCase();
  private static String DENSITY_TAG = "Electron density".toUpperCase();
  private static String ESP_TAG = "Electrostatic potential".toUpperCase();
  private int units = ANGSTROM_UNITS;
  private double value_factor = 1.0;


  private boolean readPreVolumetric = false;
  private int numberAtoms;

  private AtomInfo atoms[] = null;

  private Preferences prefs = Preferences.userNodeForPackage(getClass());
  private File currentWorkingDirectory = null;
  private JFileChooser chooser = null;
  private JFileChooser save_chooser = null;
  private FileFilterImpl filter = null;
  private float defaultIsovalue = 0;
  private boolean positiveValuesOnly = false;


  public GaussianCube() {
  }

  static public void extractGeometry(String filename, MoleculeInterface molecule) throws
          Exception {

    int natoms;
    float factor = ONE_BOHR_FLOAT;

    String str, commentLine;
    StringTokenizer st;
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));

      if ((commentLine = in.readLine()) == null) {
        throw new Exception(
                "Unexpected end of file while reading the first line");
      }

      if ((str = in.readLine()) == null) {
        throw new Exception(
                "Unexpected end of file while reading the second line");
      }
      commentLine += " " + str;
      molecule.setName(commentLine);

      // --- the third line

      if ((str = in.readLine()) == null) {
        throw new Exception(
                "Unexpected end of file while reading the third line");
      }

      st = new StringTokenizer(str, " \t");

      try {
        natoms = Integer.parseInt(st.nextToken());
      } catch (Exception ex) {
        throw new Exception(
                "Error parsing number of atoms: " + str);
      }

      // --- Skip the forth-sixth lines

      for (int i = 0; i < 3; i++) {

        if ((str = in.readLine()) == null) {
          throw new Exception(
                  "Unexpected end of file while reading the " +
                  (4 + i) +
                  "th line");
        }
      }

      // --- Reading atoms

      if (natoms < 0) {
        natoms = -natoms;
        //factor = 1.0f;
      }

      for (int i = 0; i < natoms; i++) {

        AtomInterface atom = molecule.getNewAtomInstance();

        if ((str = in.readLine()) == null) {
          throw new Exception(
                  "Unexpected end of file while reading the " +
                  (i + 1) + "atom info");
        }
        st = new StringTokenizer(str, " \t");

        try {
          atom.setAtomicNumber(Integer.parseInt(st.nextToken()));
          atom.setName(ChemicalElements.getElementSymbol(atom.
                  getAtomicNumber()));
          Float.parseFloat(st.nextToken()); // Atomic charge
          atom.setX(Float.parseFloat(st.nextToken()) * factor);
          atom.setY(Float.parseFloat(st.nextToken()) * factor);
          atom.setZ(Float.parseFloat(st.nextToken()) * factor);
        } catch (Exception ex) {
          throw new Exception(
                  "Error parsing atom info data for " + (i + 1) +
                  " atom: " + str);
        }

        molecule.addAtom(atom);
      }

      in.close();
    } catch (IOException e) {
      throw new Exception("Error parsing file " + filename + " : " +
                          e.getMessage());
    }
  }

  /**
   * Loads Gaussian cube file
   * @param filename String - file name
   * @throws Exception
   */
  public void parseVolumetricData(String filename) throws Exception {

    float factor = ONE_BOHR_FLOAT;
    String str;
    StringTokenizer st;
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));

      if ((commentLine_1 = in.readLine()) == null) {
        throw new Exception(
                "Unexpected end of file while reading the first line");
      }

      if ((commentLine_2 = in.readLine()) == null) {
        throw new Exception(
                "Unexpected end of file while reading the second line");
      }

      // --- Setup default isovalue

      if (commentLine_2.toUpperCase().contains(MO_COEFF_TAG)) {
        defaultIsovalue = 0.02f;
        positiveValuesOnly = false;
        value_factor = 1.0 / ONE_BOHR;
      } else if (commentLine_2.toUpperCase().contains(DENSITY_TAG)) {
        defaultIsovalue = 0.0004f;
        positiveValuesOnly = true;
        value_factor = 1.0 / ONE_BOHR;
      } else if (commentLine_2.toUpperCase().contains(ESP_TAG)) {
        defaultIsovalue = 0.0004f;
        positiveValuesOnly = false;
        value_factor = 1.0 / ONE_BOHR;
      } else {
        defaultIsovalue = 0.0004f;
        positiveValuesOnly = false;
      }

      // --- the third line

      if ((str = in.readLine()) == null) {
        throw new Exception(
                "Unexpected end of file while reading the third line");
      }

      st = new StringTokenizer(str, " \t");

      try {
        numberAtoms = Integer.parseInt(st.nextToken());
      } catch (Exception ex) {
        throw new Exception("Error parsing number of atoms: " + str);
      }

      if (numberAtoms < 0) {
        readPreVolumetric = true;
        numberAtoms = -numberAtoms;
      }

      if (st.countTokens() < 3) {
        throw new Exception("Not enough numbers for data origin: " +
                            str);
      }

      try {
        dataOrigin[0] = Float.parseFloat(st.nextToken()) * factor;
        dataOrigin[1] = Float.parseFloat(st.nextToken()) * factor;
        dataOrigin[2] = Float.parseFloat(st.nextToken()) * factor;
      } catch (Exception ex) {
        throw new Exception("Error parsing origin of volumetric data: " +
                            str);
      }

      // --- The forth-sixth line

      for (int i = 0; i < 3; i++) {

        if ((str = in.readLine()) == null) {
          throw new Exception(
                  "Unexpected end of file while reading the forth line");
        }

        st = new StringTokenizer(str, " \t");
        try {
          numberOfVoxels[i] = Integer.parseInt(st.nextToken());
          if (numberOfVoxels[i] < 0) {
            units = BOHR_UNITS;
            numberOfVoxels[i] = -numberOfVoxels[i];
          } else {
            units = ANGSTROM_UNITS;
          }
          axisVectors[i][0] = Float.parseFloat(st.nextToken()) *
                              factor;
          axisVectors[i][1] = Float.parseFloat(st.nextToken()) *
                              factor;
          axisVectors[i][2] = Float.parseFloat(st.nextToken()) *
                              factor;
        } catch (Exception ex) {
          throw new Exception(
                  "Error parsing voxel data for " + i + "-axis: " +
                  str);
        }
      }

      // --- Reading atoms

      if (numberAtoms < 0) {
        numberAtoms = -numberAtoms;
      }

      atoms = new AtomInfo[numberAtoms];
      for (int i = 0; i < numberAtoms; i++) {
        atoms[i] = new AtomInfo();
        if ((str = in.readLine()) == null) {
          throw new Exception(
                  "Unexpected end of file while reading the " +
                  (i + 1) + "atom info");
        }
        st = new StringTokenizer(str, " \t");

        try {
          atoms[i].atomNumber = Integer.parseInt(st.nextToken());
          atoms[i].qNumber = Float.parseFloat(st.nextToken());
          atoms[i].xyz[0] = Float.parseFloat(st.nextToken()) * factor;
          atoms[i].xyz[1] = Float.parseFloat(st.nextToken()) * factor;
          atoms[i].xyz[2] = Float.parseFloat(st.nextToken()) * factor;
        } catch (Exception ex) {
          throw new Exception("Error parsing atom info data for " +
                              (i + 1) + " atom: " + str);
        }

      }

      // --- Number of volumetric cubes

      if (readPreVolumetric) {
        if ((str = in.readLine()) == null) {
          throw new Exception(
                  "Unexpected end of file while reading the pre-volumetric (???) data: " +
                  str);
        }

        st = new StringTokenizer(str, " \t");

        try {
          nVolumetrics = Integer.parseInt(st.nextToken());
        } catch (Exception ex) {
          throw new Exception("Error parsing pre-volumetric data : " +
                              str);
        }

        labels = new int[nVolumetrics];

        for (int i = 0; i < nVolumetrics; i++) {
          if (!st.hasMoreTokens()) {
            if ((str = in.readLine()) == null) {
              throw new Exception(
                      "Unexpected end of file while reading the pre-volumetric labels: " +
                      str);
            }
            st = new StringTokenizer(str, " \t");
          }

          try {
            labels[i] = Integer.parseInt(st.nextToken());
          } catch (Exception ex) {
            throw new Exception(
                    "Error parsing pre-volumetric label: " + str);
          }
        }

      }

      // --- Reading volumetric data

      data = new double[nVolumetrics][numberOfVoxels[0]][numberOfVoxels[1]][
             numberOfVoxels[2]];

      if ((str = in.readLine()) == null) {
        throw new Exception(
                "Unexpected end of file while reading the volumetric data: " +
                str);
      }
      st = new StringTokenizer(str, " \t");

      boolean firstValue = true;
      float value = 0;

      for (int ix = 0; ix < numberOfVoxels[0]; ix++) {
        for (int iy = 0; iy < numberOfVoxels[1]; iy++) {
          for (int iz = 0; iz < numberOfVoxels[2]; iz++) {

            for (int i = 0; i < nVolumetrics; i++) {
              if (!st.hasMoreTokens()) {
                if ((str = in.readLine()) == null) {
                  throw new Exception(
                          "Unexpected end of file while reading the volumetric data: " +
                          str);
                }
                st = new StringTokenizer(str, " \t");
              }

              try {
                value = Float.parseFloat(st.nextToken());
                data[i][ix][iy][iz] = value *
                                      (float) value_factor;

                if (firstValue) {
                  minFValue = value;
                  maxFValue = value;
                  firstValue = false;
                }

                if (minFValue > value) {
                  minFValue = value;
                } else if (maxFValue < value) {
                  maxFValue = value;
                }
              } catch (Exception ex) {
                throw new Exception(
                        "Error parsing volumetric data: " + str);
              }
            }
          }
        }
      }

      in.close();
    } catch (IOException e) {
      throw e;
    }
  }

  public void getMolecule(MoleculeInterface molecule) {
    molecule.setName(commentLine_1 + " " + commentLine_2);
    for (int i = 0; i < atoms.length; i++) {

      AtomInterface atom = molecule.getNewAtomInstance();

      atom.setAtomicNumber(atoms[i].atomNumber);
      atom.setName(ChemicalElements.getElementSymbol(atom.
              getAtomicNumber()));
      atom.setX(atoms[i].xyz[0]);
      atom.setY(atoms[i].xyz[1]);
      atom.setZ(atoms[i].xyz[2]);

      molecule.addAtom(atom);
    }
  }


  public void parseGaussianCube() throws Exception {
    if (chooser == null) {
      chooser = new JFileChooser();
      filter = new FileFilterImpl();

      String temp[] = {
                      "cub", "cube"}; // extensions.split(";");
      for (int i = 0; i < temp.length; i++) {
        filter.addExtension(temp[i]);
      }
      filter.setDescription("Gaussian G03 Cube Files (*.cub,*cube)");
      chooser.setFileFilter(filter);
    }

    chooser.setDialogTitle("Open Gaussian Cube File");
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);

    if (currentWorkingDirectory != null) {
      chooser.setCurrentDirectory(currentWorkingDirectory);
    } else {
      String lastPWD = prefs.get(lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory() &&
            currentWorkingDirectory.exists()) {
          chooser.setCurrentDirectory(currentWorkingDirectory);
        }
      }
    }

    int returnVal = JFileChooser.CANCEL_OPTION;

    returnVal = chooser.showOpenDialog(null);

    if (returnVal == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String fileName = chooser.getSelectedFile().getPath();
      currentWorkingDirectory = chooser.getCurrentDirectory();
      try {
        prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
      } catch (Exception ex) {
        System.err.println("Cannot save cwd: " + ex.getMessage() +
                           " Ignored...");
      }
      //logger.info("You chose to open this file: " +
      //                   fileName);


      this.parseVolumetricData(fileName);
    }

  }

  /**
   * Overwrites
   * @param n int
   * @throws Exception
   */
  public void saveVolumeData(int n) throws Exception {

    if (save_chooser == null) {
      javax.swing.filechooser.FileFilter[] filters = getDefaultSaveFileFilters();
      save_chooser = new JFileChooser();
      for (int i = 0; i < filters.length; i++) {
        save_chooser.addChoosableFileFilter(filters[i]);
      }
      filter = new FileFilterImpl();

      String temp[] = {
                      "cub", "cube"}; // extensions.split(";");
      for (int i = 0; i < temp.length; i++) {
        filter.addExtension(temp[i]);
      }
      filter.setDescription("Gaussian G03 Cube Files (*.cub,*cube)");
      save_chooser.addChoosableFileFilter(filter);
    }

    save_chooser.setDialogTitle("Save Volume Data in File");
    save_chooser.setDialogType(JFileChooser.SAVE_DIALOG);

    if (currentWorkingDirectory != null) {
      save_chooser.setCurrentDirectory(currentWorkingDirectory);
    } else {
      String lastPWD = prefs.get(lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory() &&
            currentWorkingDirectory.exists()) {
          save_chooser.setCurrentDirectory(currentWorkingDirectory);
        }
      }
    }

    int returnVal = JFileChooser.CANCEL_OPTION;

    returnVal = save_chooser.showSaveDialog(null);

    if (returnVal == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (returnVal == JFileChooser.APPROVE_OPTION) {

      javax.swing.filechooser.FileFilter selectedFilter = save_chooser.getFileFilter();
      String fileName = save_chooser.getSelectedFile().getPath();

      if (selectedFilter == filter) { // --- Save in gaussian cube format

        if (fileName.endsWith(".")) {
          fileName += "cube";
        } else if (!fileName.endsWith(".cub") && !fileName.endsWith(".CUB") &&
                   !fileName.endsWith(".cube") &&
                   !fileName.endsWith(".CUBE")) {
          fileName += ".cube";
        }
        saveGaussianCube(n, fileName);
      } else {
        super.saveVolumeData(n, fileName, selectedFilter.getDescription());
      }

      currentWorkingDirectory = chooser.getCurrentDirectory();
      try {
        prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
      } catch (Exception ex) {
        System.err.println("Cannot save cwd: " + ex.getMessage() + " Ignored...");
      }
    }
  }

  public void saveGaussianCube(int n) throws Exception {

    if (nVolumetrics < 1) {
      System.err.println("saveGaussianCube: nVolumetrics < 1");
      throw new Exception("saveGaussianCube: nVolumetrics < 1");
    }

    if (n < 0 || n >= nVolumetrics) {
      System.err.println("saveGaussianCube: n < 0 || n>=nVolumetrics");
      throw new Exception("saveGaussianCube: n < 0 || n>=nVolumetrics");
    }

    if (save_chooser == null) {
      save_chooser = new JFileChooser();
      filter = new FileFilterImpl();

      String temp[] = {
                      "cub", "cube"}; // extensions.split(";");
      for (int i = 0; i < temp.length; i++) {
        filter.addExtension(temp[i]);
      }
      filter.setDescription("Gaussian G03 Cube Files (*.cub,*cube)");
      save_chooser.setFileFilter(filter);
    }

    save_chooser.setDialogTitle("Save Gaussian Cube File");
    save_chooser.setDialogType(JFileChooser.SAVE_DIALOG);

    if (currentWorkingDirectory != null) {
      save_chooser.setCurrentDirectory(currentWorkingDirectory);
    } else {
      String lastPWD = prefs.get(lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory() &&
            currentWorkingDirectory.exists()) {
          save_chooser.setCurrentDirectory(currentWorkingDirectory);
        }
      }
    }

    int returnVal = JFileChooser.CANCEL_OPTION;

    returnVal = save_chooser.showSaveDialog(null);

    if (returnVal == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String fileName = save_chooser.getSelectedFile().getPath();
      if (fileName.endsWith(".")) {
        fileName += "cube";
      } else if (!fileName.endsWith(".cub") && !fileName.endsWith(".CUB") &&
                 !fileName.endsWith(".cube") && !fileName.endsWith(".CUBE")) {
        fileName += ".cube";
      }
      currentWorkingDirectory = chooser.getCurrentDirectory();
      try {
        prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
      } catch (Exception ex) {
        System.err.println("Cannot save cwd: " + ex.getMessage() + " Ignored...");
      }

      saveGaussianCube(n, fileName);
    }
  }

  public void saveGaussianCube(int n, String filename) throws Exception {

    float factor = ONE_BOHR_FLOAT;

    FileOutputStream out;
    try {
      out = new FileOutputStream(filename);

      out.write((commentLine_1 + "\n").getBytes());

      out.write((commentLine_2 + "\n").getBytes());

      // --- the third line

      int sign = 1;
      if (readPreVolumetric) {
        sign = -1;
      }

      out.write((String.format("%5d %11.6f %11.6f %11.6f",
                               (sign * numberAtoms),
                               dataOrigin[0] / factor,
                               dataOrigin[1] / factor,
                               dataOrigin[2] / factor) + "\n").getBytes());

      // --- The forth-sixth line

      for (int i = 0; i < 3; i++) {

        out.write((String.format("%5d", numberOfVoxels[i])).getBytes());

        out.write((String.format(" %11.6f", axisVectors[i][0] / factor)).
                  getBytes());
        out.write((String.format(" %11.6f", axisVectors[i][1] / factor)).
                  getBytes());
        out.write((String.format(" %11.6f", axisVectors[i][2] / factor)).
                  getBytes());

        out.write(("\n").getBytes());
      }

      // --- Writing atoms

      for (int i = 0; i < numberAtoms; i++) {

        out.write((String.format("%5d", atoms[i].atomNumber)).getBytes());

        out.write((String.format(" %11.6f", atoms[i].qNumber)).getBytes());

        out.write((String.format(" %11.6f", atoms[i].xyz[0] / factor)).
                  getBytes());
        out.write((String.format(" %11.6f", atoms[i].xyz[1] / factor)).
                  getBytes());
        out.write((String.format(" %11.6f", atoms[i].xyz[2] / factor)).
                  getBytes());

        out.write(("\n").getBytes());
      }

      // --- Number of volumetric cubes

      if (readPreVolumetric) {
        out.write((String.format("%5d %4d", 1, labels[n]) + "\n").getBytes());
      }

      // --- Reading volumetric data

      int items = 0;
      for (int ix = 0; ix < numberOfVoxels[0]; ix++) {
        for (int iy = 0; iy < numberOfVoxels[1]; iy++) {
          for (int iz = 0; iz < numberOfVoxels[2]; iz++) {

            out.write((String.format(" %12.5g", data[n][ix][iy][iz])).getBytes());
            ++items;
            if (items == 6) {
              items = 0;
              out.write(("\n").getBytes());
            }
          }
          if (items != 0) {
            items = 0;
            out.write(("\n").getBytes());
          }
        }
      }

      out.close();
    } catch (IOException e) {
      throw e;
    }
  }

  public float getDefaultIsovalue() {
    return defaultIsovalue;
  }

  public boolean isPositiveValuesOnly() {
    return this.positiveValuesOnly;
  }

  private class AtomInfo {
    public int atomNumber;
    public float qNumber;
    public float xyz[] = new float[3];
  }

}
