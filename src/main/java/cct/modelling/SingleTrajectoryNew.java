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

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.MoleculeInterface;
import cct.tools.DataSets;
import cct.tools.ui.JobProgressInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

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
abstract public class SingleTrajectoryNew extends DataSets {

  public final static String TEMP_FILE_EXTENSION = ".trj";
  public final static String TEMP_FILE_TEMPLATE = "trj-";

  protected String Title = null;
  protected String fileName = null;
  protected RandomAccessFile raf = null;
  protected RandomAccessFile tmpRAF = null;
  private MoleculeInterface referenceStructure = null;
  private int numberOfAtoms = 0; // Set in case if referenceStructure is not set
  private File tmpDirectory;
  //private Set<String> descriptors = new LinkedHashSet<String>();

  protected boolean periodicConditions = false;
  private boolean keepCoordinatesInMemory = false;
  private boolean useIntermediateFile = true;
  private List<TrajectorySnapshot> trajectorySnapshots = new ArrayList<TrajectorySnapshot>();
  static final Logger logger = Logger.getLogger(SingleTrajectoryNew.class.getCanonicalName());

  public SingleTrajectoryNew() {
  }

  public SingleTrajectoryNew(MoleculeInterface mol) {
    referenceStructure = mol;
    if (mol != null) {
      numberOfAtoms = mol.getNumberOfAtoms();
    }
  }

  /**
   * Class specific method to parse custom trajectory file
   *
   * @param fileName - file name
   * @throws Exception
   */
  public void parseTrajectoryFile(String fileName) throws Exception {
    parseTrajectoryFile(fileName, null);
  }

  /**
   *
   * @param fileName
   * @param progress
   * @throws Exception
   */
  abstract public void parseTrajectoryFile(String fileName, JobProgressInterface progress) throws Exception;

  /**
   * This method is not implemented in this class and will cause an Exception
   *
   * @param in
   * @throws Exception
   */
  public void parseData(BufferedReader in) throws Exception {
    throw new Exception("void parseData(BufferedReader in) method is not implemented in " + this.getClass().getCanonicalName());
  }

  /**
   * Class specific method to extract a given structure (coordinates) from a custom trajectory file
   *
   * @param coords[n][3] - coordinates
   * @param raf - RandomAccessFile file handle
   * @param offset - offset in RandomAccessFile file
   * @throws Exception
   */
  abstract public void readCoordsFromTrajFile(float coords[][], RandomAccessFile raf, long offset) throws Exception;

  public void addSnapshot(TrajectorySnapshot snapshot) {
    trajectorySnapshots.add(snapshot);
    if (useIntermediateFile) {
      try {
        long offset = saveCoordsIntoTmpFile(snapshot.getCoordinates());
        snapshot.setTmpFile(tmpRAF);
        snapshot.setTmpOffset(offset);
      } catch (Exception ex) {
        Logger.getLogger(SingleTrajectoryNew.class.getName()).log(Level.SEVERE, null, ex);
      }

    }
    // --- 
    if (!keepCoordinatesInMemory) {
      snapshot.clearCoordinates();
    }
  }

  protected long saveCoordsIntoTmpFile(float coords[][]) throws Exception {
    if (tmpRAF == null) {
      tmpRAF = createRandomAccessFile();
    }
    long offset = saveCoordsIntoTmpFile(coords, tmpRAF);
    return offset;
  }

  public static long saveCoordsIntoTmpFile(float coords[][], RandomAccessFile raf) throws Exception {
    long offset = raf.getFilePointer();
    for (int i = 0; i < coords.length; i++) {
      raf.writeFloat(coords[i][0]);
      raf.writeFloat(coords[i][1]);
      raf.writeFloat(coords[i][2]);
    }
    return offset;
  }

  public static void readCoordsFromTmpFile(float coords[][], RandomAccessFile raf, long offset) throws Exception {
    raf.seek(offset);
    for (int i = 0; i < coords.length; i++) {
      coords[i][0] = raf.readFloat();
      coords[i][1] = raf.readFloat();
      coords[i][2] = raf.readFloat();
    }
  }

  protected RandomAccessFile createRandomAccessFile() throws Exception {
    File file = createIntermediateFile();
    RandomAccessFile raf = new RandomAccessFile(file, "rw");
    return raf;
  }

  protected File createIntermediateFile() throws Exception {
    File tmpFile = null;
    try {

      if (tmpDirectory == null) { // Use fefault location
        tmpFile = File.createTempFile(TEMP_FILE_TEMPLATE, TEMP_FILE_EXTENSION);
      } else {
        tmpFile = File.createTempFile(TEMP_FILE_TEMPLATE, TEMP_FILE_EXTENSION, tmpDirectory);
      }
    } catch (IOException ex) {
      Logger.getLogger(SingleTrajectoryNew.class.getName()).log(Level.SEVERE, null, ex);
      throw new Exception(ex.getLocalizedMessage());
    }
    tmpFile.deleteOnExit();
    return tmpFile;
  }

  public int getNumberOfAtoms() {
    if (referenceStructure != null) {
      return referenceStructure.getNumberOfAtoms();
    }
    return numberOfAtoms;
  }

  public void setNumberOfAtoms(int numberOfAtoms) {
    if (numberOfAtoms < 0) {
      numberOfAtoms = 0;
    }
    this.numberOfAtoms = numberOfAtoms;
  }

  //public Set<String> getDescriptors() {
  //  return this.getDescriptors();
  //}
  //public void addDescriptor(String descriptor) {
  //  descriptors.add(descriptor);
  //}
  //public boolean hasDescriptor(String descriptor) {
  //  return descriptors.contains(descriptor);
  //}
  public File getTmpDirectory() {
    return tmpDirectory;
  }

  public void setTmpDirectory(File tmpDirectory) {
    this.tmpDirectory = tmpDirectory;
  }

  public void setTmpDirectory(String tmpDir) throws Exception {
    tmpDirectory = new File(tmpDir);
    if (!tmpDirectory.isDirectory()) {
      useIntermediateFile = false;
      throw new Exception(tmpDirectory.getAbsolutePath() + " is not a directory!");
    }
    if (!tmpDirectory.exists()) {
      try {
        tmpDirectory.mkdirs();
      } catch (Exception ex) {
        useIntermediateFile = false;
        throw new Exception("Error creating directory: " + tmpDirectory.getAbsolutePath() + " : " + ex.getLocalizedMessage());
      }
    }
  }

  public List<TrajectorySnapshot> getTrajectorySnapshots() {
    return trajectorySnapshots;
  }

  public TrajectorySnapshot getSnapshot(int n) {
    if (trajectorySnapshots == null) {
      return null;
    } else if (n < 0 || n >= trajectorySnapshots.size()) {
      return null;
    }
    return trajectorySnapshots.get(n);
  }

  public int getSnapshotsCount() {
    if (trajectorySnapshots == null) {
      return 0;
    }
    return trajectorySnapshots.size();
  }

  public MoleculeInterface getReferenceStructure() {
    return referenceStructure;
  }

  public void setReferenceStructure(MoleculeInterface referenceStructure) {
    this.referenceStructure = referenceStructure;
    numberOfAtoms = referenceStructure.getNumberOfAtoms();
  }

  public boolean isKeepCoordinatesInMemory() {
    return keepCoordinatesInMemory;
  }

  public void setKeepCoordinatesInMemory(boolean keepCoordinatesInMemory) {
    this.keepCoordinatesInMemory = keepCoordinatesInMemory;
  }

  public boolean isUseIntermediateFile() {
    return useIntermediateFile;
  }

  public void setUseIntermediateFile(boolean useIntermediateFile) {
    this.useIntermediateFile = useIntermediateFile;
  }

  public static void main(String[] args) {
    //SingleTrajectory trajectoryfile = new SingleTrajectory();
  }
}
