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
package cct.modelling;

import cct.gaussian.G03MDTrajectory;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import cct.interfaces.MoleculeInterface;
import cct.tools.DataSets;
import static cct.tools.DataSets.DATA_ROW_NUMBER;
import cct.tools.ui.JobProgressInterface;
import java.io.File;
import java.util.HashSet;

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
public class StructureManager {

  static final double ONE_BOHR = 0.529177249; // In Angstrom
  protected Map<String, Object> structurePool = new LinkedHashMap<String, Object>();
  protected List<TrajectorySnapshot> snapshotReference = new ArrayList<TrajectorySnapshot>();
  protected MoleculeInterface referenceStructure = null;
  private TRAJECTORY_FILE_FORMAT Format;
  static final Logger logger = Logger.getLogger(StructureManager.class.getCanonicalName());

  public StructureManager() {

  }

  public StructureManager(MoleculeInterface mol) {
    referenceStructure = mol;
  }

  public void setReferenceMolecule(MoleculeInterface mol) {
    referenceStructure = mol;
  }

  public MoleculeInterface getReferenceMolecule() {
    return referenceStructure;
  }

  public void deleteAllTrajectories() {
    structurePool.clear();
    snapshotReference.clear();
    //referenceStructure = null;
  }

  /**
   * Returns all descriptors (data property's names) across all loaded trajectories
   *
   * @return
   */
  public String[] getAllProperties() {
    if (structurePool.size() < 1) {
      return null;
    }
    Set<String> set = new HashSet<String>();
    for (String key : structurePool.keySet()) {
      Object obj = structurePool.get(key);
      if (obj instanceof DataSets) {
        for (String descr : ((DataSets) obj).getDescriptors()) {
          set.add(descr);
        }
      } else {
        logger.severe("Don't know how to handle class " + obj.getClass().getCanonicalName());
        return null;
      }
    }
    String[] props = new String[set.size()];
    return set.toArray(props);
  }

  public int getNumberOfSnapshots() {
    if (structurePool.size() < 1) {
      return 0;
    }
    int numSnaps = 0;
    for (String key : structurePool.keySet()) {
      Object obj = structurePool.get(key);
      if (obj instanceof DataSets) {
        numSnaps += ((DataSets) obj).getDataSize();

      } else {
        logger.severe("Don't know how to handle class " + obj.getClass().getCanonicalName());
        return numSnaps;
      }
    }
    return numSnaps;
  }

  public double[] getRowNumbersAsDouble() {
    if (structurePool.size() < 1 || getNumberOfSnapshots() < 1) {
      return null;
    }
    double[] rn = new double[getNumberOfSnapshots()];
    for (int i = 0; i < getNumberOfSnapshots(); i++) {
      rn[i] = (double) (i + 1);
    }
    return rn;
  }

  public float[] getRowNumbersAsFloat() {
    if (structurePool.size() < 1 || getNumberOfSnapshots() < 1) {
      return null;
    }
    float[] rn = new float[getNumberOfSnapshots()];
    for (int i = 0; i < getNumberOfSnapshots(); i++) {
      rn[i] = (float) (i + 1);
    }
    return rn;
  }

  public double[] getDataAsDouble(String descriptor) {
    if (structurePool.size() < 1 || getNumberOfSnapshots() < 1) {
      return null;
    }

    if (descriptor.equals(DATA_ROW_NUMBER)) {
      return getRowNumbersAsDouble();
    }

    double[] values = new double[getNumberOfSnapshots()];

    int counter = 0;
    for (String key : structurePool.keySet()) {
      Object obj = structurePool.get(key);
      if (obj instanceof DataSets) {
        double[] temp = ((DataSets) obj).getDataAsDouble(descriptor);
        for (int i = 0; i < temp.length; i++, counter++) {
          values[counter] = temp[i];
        }
      } else {
        logger.severe("Don't know how to handle class " + obj.getClass().getCanonicalName());
        return null;
      }
    }

    return values;

  }

  public static void main(String[] args) {
    //StructureManager structuremanager = new StructureManager();
  }
  
  public void parseTrajectoryFile(String fileName, TRAJECTORY_FILE_FORMAT format) throws Exception {
    parseTrajectoryFile(fileName, format, null);
  }

  public void parseTrajectoryFile(String fileName, TRAJECTORY_FILE_FORMAT format, JobProgressInterface progress) throws Exception {
    if (fileName == null || fileName.length() < 1) {
      throw new Exception("Trajectory file name is not set");
    }
    if (format == null) {
      throw new Exception("Trajectory file format is not set");
    }

    if (structurePool.containsKey(fileName)) {
      throw new Exception("Trajectory from file " + fileName + " is already loaded in");
    }

    Format = format;

    int additional_condition = 0;
    if (format == TRAJECTORY_FILE_FORMAT.AMBER) {
      int answer = JOptionPane.showConfirmDialog(null, "Does this trajectory contain Periodic Boundary Info?",
          "Select type of trajectory", JOptionPane.YES_NO_OPTION);
      if (answer == JOptionPane.YES_OPTION) {
        additional_condition = 1;
      } else {
        additional_condition = 0;
      }
    }

    List<TrajectorySnapshot> snapshots = null;

    if (format == TRAJECTORY_FILE_FORMAT.G03_TRAJECTORY) {
      SingleTrajectoryNew singleTraj = new G03MDTrajectory(referenceStructure);

      singleTraj.setTmpDirectory(new File("."));
      singleTraj.parseTrajectoryFile(fileName, progress);
      singleTraj.setKeepCoordinatesInMemory(true);
      singleTraj.setUseIntermediateFile(true);
      snapshots = singleTraj.getTrajectorySnapshots();
      if (snapshots.size() < 1) {
        throw new Exception("Didn't find coordinates in file " + fileName);
      }
      structurePool.put(fileName, singleTraj);
    } else {
      SingleTrajectory singleTraj = new SingleTrajectory(referenceStructure);
      singleTraj.parseTrajectoryFile(fileName, format, additional_condition);
      snapshots = singleTraj.getTrajectorySnapshots();
      if (snapshots.size() < 1) {
        throw new Exception("Didn't find coordinates in file " + fileName);
      }
      structurePool.put(fileName, singleTraj);
    }

    if (snapshotReference instanceof ArrayList) {
      ((ArrayList) snapshotReference).ensureCapacity(snapshotReference.size() + snapshots.size());
    }
    snapshotReference.addAll(snapshots);
  }

  public int getTrajectoryCount() {
    return structurePool.size();
  }

  public int getSnapshotsCount() {
    return snapshotReference.size();
  }

  public String[] getAvailableTrajectories() {
    if (structurePool.size() < 1) {
      return null;
    }
    Set<String> keys = structurePool.keySet();
    String[] files = new String[keys.size()];
    keys.toArray(files);
    return files;
  }

  public float[][] getStructure(int n) throws Exception {
    float coords[][] = new float[referenceStructure.getNumberOfAtoms()][3];
    getStructure(n, coords);
    return coords;
  }

  public void getStructure(int n, float coords[][]) throws Exception {
    if (n < 0 || n >= snapshotReference.size()) {
      throw new Exception(this.getClass().getCanonicalName() + " getStructure: n < 0 || n >= snapshotReference.size()");
    }

    if (referenceStructure == null || referenceStructure.getNumberOfAtoms() < 1) {
      throw new Exception(this.getClass().getCanonicalName()
          + " getStructure: Reference structure is not set or number of atoms < 1");
    }

    if (coords.length < referenceStructure.getNumberOfAtoms()) {
      throw new Exception(this.getClass().getCanonicalName()
          + " getStructure: array for coordinates is less than number of atoms");
    }

    TrajectorySnapshot snapshot = snapshotReference.get(n);

    RandomAccessFile raFile = snapshot.getFile();
    long Offset = snapshot.getOffset();
    //TRAJECTORY_FILE_FORMAT Format = snapshot.getFormat();
    String buffer = null;

    long start = System.currentTimeMillis();
    switch (Format) {
      case AMBER:
        raFile.seek(Offset);
        int counter = 10;
        for (int i = 0; i < referenceStructure.getNumberOfAtoms(); i++) {
          for (int j = 0; j < 3; j++) {
            if (counter >= 10) {
              buffer = raFile.readLine();
              counter = 0;
            }
            coords[i][j] = Float.parseFloat(buffer.substring(counter * 8, 8 * (counter + 1)).trim());
            ++counter;
          }
        }
        break;

      case G03_TRAJECTORY:
        // --- Maybe cooridnates are already in memory...
        if (snapshot.getCoordinates() != null) {
          float[][] trajCrd = snapshot.getCoordinates();
          for (int i = 0; i < referenceStructure.getNumberOfAtoms(); i++) {
            coords[i][0] = trajCrd[i][0];
            coords[i][1] = trajCrd[i][1];
            coords[i][2] = trajCrd[i][2];
          }
          break;
        }
        // --- If we use an intermediate file..
        if (snapshot.getTmpFile() != null) {
          SingleTrajectoryNew.readCoordsFromTmpFile(coords, snapshot.getTmpFile(), snapshot.getTmpOffset());
          break;
        }
        // --- And finally
        SingleTrajectoryNew traj = snapshot.getReferenceTrajectory();
        traj.readCoordsFromTrajFile(coords, snapshot.getFile(), snapshot.getOffset());
        // --- Old (disabled) stuff below
        if (false) { // Old stuff
          raFile.seek(Offset);
          for (int i = 0; i < referenceStructure.getNumberOfAtoms(); i++) {
            buffer = raFile.readLine();
            buffer = buffer.replaceAll("[D]", "E");
            buffer = buffer.replaceAll("[d]", "E");
            coords[i][0] = (float) (ONE_BOHR * Double.parseDouble(buffer.substring(11, 32).trim()));
            coords[i][1] = (float) (ONE_BOHR * Double.parseDouble(buffer.substring(35, 56).trim()));
            coords[i][2] = (float) (ONE_BOHR * Double.parseDouble(buffer.substring(59, 80).trim()));
          }
        }
        break;

      case GULP_TRAJECTORY:
        raFile.seek(Offset);
        int nat = 0;
        while (true) {
          buffer = raFile.readLine();
          if (buffer.startsWith("#")) {
            continue;
          }
          buffer = buffer.replaceAll("[D]", "E");
          buffer = buffer.replaceAll("[d]", "E");
          coords[nat][0] = (float) Double.parseDouble(buffer.substring(0, 25).trim());
          coords[nat][1] = (float) Double.parseDouble(buffer.substring(25, 51).trim());
          coords[nat][2] = (float) Double.parseDouble(buffer.substring(51, 77).trim());
          ++nat;
          if (nat >= referenceStructure.getNumberOfAtoms()) {
            break;
          }
        }
        break;

    }
    float secs = (System.currentTimeMillis() - start) / 1000.0f;
    logger.info("Elapsed time to retrive coords for " + n + " snapshot: " + secs);

  }
}
