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

import cct.gaussian.G03MDTrajectory;
import cct.gaussian.GaussianAtom;
import cct.interfaces.MoleculeInterface;

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
 * @deprecated
 */
public class SingleTrajectory {

  protected String Title = null;
  protected String fileName = null;
  protected RandomAccessFile raf = null;
  protected MoleculeInterface referenceStructure = null;
  protected boolean periodicConditions = false;
  protected List<TrajectorySnapshot> trajectorySnapshots = new ArrayList<TrajectorySnapshot>();
  static final Logger logger = Logger.getLogger(SingleTrajectory.class.getCanonicalName());

  public SingleTrajectory(MoleculeInterface mol) {
    referenceStructure = mol;
  }

  public static void main(String[] args) {
    //SingleTrajectory trajectoryfile = new SingleTrajectory();
  }

  public void parseTrajectoryFile(String fileName, TRAJECTORY_FILE_FORMAT format, int additional_condition) throws Exception {
    if (fileName == null || fileName.length() < 1) {
      throw new Exception("Trajectory file name is not set");
    }
    if (format == null) {
      throw new Exception("Trajectory file format is not set");
    }

    try {
      switch (format) {
        case AMBER:
          raf = parseAmberTrajectory(fileName, format, additional_condition);
          break;
        case G03_TRAJECTORY:
          raf = parseG03Trajectory(fileName, format, additional_condition);
          break;
        case GULP_TRAJECTORY:
          raf = parseGULPTrajectory(fileName, format, additional_condition);
          break;
      }
    } catch (Exception ex) {
      throw ex;
    }
  }

  public RandomAccessFile parseAmberTrajectory(String fileName, TRAJECTORY_FILE_FORMAT format, int flag_1) throws Exception {
    if (fileName == null || fileName.length() < 1) {
      throw new Exception("Trajectory file name is not set");
    }
    if (referenceStructure == null || referenceStructure.getNumberOfAtoms() < 1) {
      throw new Exception("Reference molecule is not set or has no atoms");
    }

    int n_atoms = referenceStructure.getNumberOfAtoms();

    //FORMAT(20A4) ITITL
    //  ITITL  : the title of the current run, from the AMBER
    //           parameter/topology file
    //The following snapshot is written every NTWX steps in the trajectory (specified in the control input file):
    //FORMAT(10F8.3)  (X(i), Y(i), Z(i), i=1,NATOM)
    //  X,Y,Z  : coordinates or velocities (velocity units: Angstroms per 1/20.455 ps)
    //IF constant pressure (in 4.1, also constant volume)
    //For each snapshot:
    //FORMAT(10F8.3)  BOX(1), BOX(2), BOX(3)
    //  BOX    : size of periodic box
    try {
      File f = new File(fileName);
      raf = new RandomAccessFile(f, "r");

      int i, nlines;

      if ((3 * n_atoms) % 10 != 0) {
        nlines = 3 * n_atoms / 10 + 1;
      } else {
        nlines = 3 * n_atoms / 10;
      }

      if (nlines == 0) {
        nlines = 1;
      }

      if (flag_1 != 0) {
        periodicConditions = true;
        ++nlines;
      } else {
        periodicConditions = false;
      }

      // --- Skip first line (run's title)
      //fget_line( bufer, 256, t_file );
      Title = raf.readLine();
      long fileLength = raf.length();

      while (fileLength > raf.getFilePointer()) {
        // start to read atomic coordinates
        long offset = raf.getFilePointer();
        TrajectorySnapshot snapshot = new TrajectorySnapshot(raf, offset);
        trajectorySnapshots.add(snapshot);

        for (i = 0; i < nlines; i++) {
          raf.readLine();
          if (fileLength == raf.getFilePointer()) {
            trajectorySnapshots.remove(snapshot);
            break;
          }
        }

      }

    } catch (Exception e) {
    }

    try {
      raf.seek(0);
      //raf.close();
    } catch (Exception e) {
    }

    logger.info("Found " + trajectorySnapshots.size() + "snapshots");
    /*
     for (i = n_snapshots - found; i < n_snapshots; i++) {
     fseek(t_file, snapshots[i].offset, SEEK_SET);
     //fgets( bufer, 254, t_file );
     //bufer[255]=0x00;
     //fget_line( bufer, 256, t_file );
     read_line(bufer, 256, t_file);
     printf("%5d: %9ld %s\n", i + 1, snapshots[i].offset, bufer);
     }
     */

    return raf;
  }

  public RandomAccessFile parseG03Trajectory(String fileName, TRAJECTORY_FILE_FORMAT format, int flag_1) throws Exception {
    if (fileName == null || fileName.length() < 1) {
      throw new Exception("Trajectory file name is not set");
    }
    if (referenceStructure == null || referenceStructure.getNumberOfAtoms() < 1) {
      throw new Exception("Reference molecule is not set or has no atoms");
    }

    int n_atoms = referenceStructure.getNumberOfAtoms();

    /*
     Time in trajectory (femtosec)    2.000000D-01
     Total energy (au)  -1.175171068D+02
     Total angular momentum (h-bar)   2.904529344D-14
     Coordinates (Bohr)
     I=    1 X=  -4.555420059248D-01 Y=  -5.134698398949D-01 Z=  -7.216904569806D-01
     I=    2 X=  -2.221805396142D+00 Y=  -1.254178689967D+00 Z=  -2.596959873079D-02
     I=    3 X=  -5.952104662883D-01 Y=   1.532677098577D+00 Z=  -8.366401222195D-01
     */
    periodicConditions = false;
    String line;
    long offset;

    try {
      File f = new File(fileName);
      raf = new RandomAccessFile(f, "r");

      List<GaussianAtom> gAtoms = G03MDTrajectory.parseMolecularGeometryOnly(raf);
      if (n_atoms != gAtoms.size()) {
        throw new Exception("Number of atoms in molecule (" + n_atoms + ") do not match that in Trajectory file ("
            + gAtoms.size() + ")");
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

          TrajectorySnapshot snapshot = new TrajectorySnapshot(raf, 0); // Temp value for offset

          // Add absolute time
          double value;
          try {
            value = Double.parseDouble(line.substring(30, line.length() - 1).trim());
            snapshot.addProperty("Time in trajectory (femtosec)", new Double(value));
          } catch (Exception ex) {

          }

          // parsing            Total energy (au)  -1.175171068D+02
          if ((line = raf.readLine()) == null) {
            break;
          }

          try {
            value = Double.parseDouble(line.substring(18, line.length() - 1).trim());
            snapshot.addProperty("Total energy (au)", new Double(value));
          } catch (Exception ex) {

          }

          // Skip 2 lines....
          if ((line = raf.readLine()) == null) {
            break;
          }
          if ((line = raf.readLine()) == null) {
            break;
          }

          offset = raf.getFilePointer();
          snapshot.setOffset(offset);

          // ---Read atomic coordinates
          for (int i = 0; i < n_atoms; i++) {
            if ((line = raf.readLine()) == null) {
              break;
            }
          }
          if (line != null) {
            trajectorySnapshots.add(snapshot);
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
      //raf.close();
    } catch (Exception e) {
    }

    logger.info("Found " + trajectorySnapshots.size() + "snapshots");
    /*
     for (i = n_snapshots - found; i < n_snapshots; i++) {
     fseek(t_file, snapshots[i].offset, SEEK_SET);
     //fgets( bufer, 254, t_file );
     //bufer[255]=0x00;
     //fget_line( bufer, 256, t_file );
     read_line(bufer, 256, t_file);
     printf("%5d: %9ld %s\n", i + 1, snapshots[i].offset, bufer);
     }
     */

    return raf;
  }

  public RandomAccessFile parseGULPTrajectory(String fileName, TRAJECTORY_FILE_FORMAT format, int flag_1) throws Exception {
    if (fileName == null || fileName.length() < 1) {
      throw new Exception("Trajectory file name is not set");
    }
    if (referenceStructure == null || referenceStructure.getNumberOfAtoms() < 1) {
      throw new Exception("Reference molecule is not set or has no atoms");
    }

    int n_atoms = referenceStructure.getNumberOfAtoms();

    /*
     3.30
     64 3
     #  Time/KE/E/T
     1.05000000000000          2.59560693399160         -1312.08156254049          318.737956024591
     #  Coordinates
     -0.184578838443719E-01    -0.650093939728458E-01     0.605513913742120E-01
     -0.505227067033068E-01     0.238634219912673E-01      4.13998804910851
     0.257564345076467E-01      4.20040152534106         0.365911280462028E-02
     */
    periodicConditions = false;
    String line = "", token;
    long offset;

    try {
      File f = new File(fileName);
      raf = new RandomAccessFile(f, "r");

      // Skip the first line
      while ((line = raf.readLine()) != null) {
        if (line.startsWith("#")) {
          continue;
        } else {
          break;
        }
      }
      if (line == null) {
        throw new Exception("Unexpected end of file while reading the first line");
      }

      // Read the second line
      while ((line = raf.readLine()) != null) {
        if (line.startsWith("#")) {
          continue;
        } else {
          break;
        }
      }

      if (line == null) {
        throw new Exception("Unexpected end of file while reading the second line");
      }

      StringTokenizer st = new StringTokenizer(line, " ");
      token = st.nextToken();
      try {
        int nat = Integer.parseInt(token);
        if (nat != n_atoms) {
          throw new Exception("Number of atoms in reference structure (" + n_atoms + ") does not match that in trajectory ("
              + nat + ")");
        }
      } catch (Exception ex) {
        throw new Exception("Error parsing number of atoms in the second line: " + line);
      }

      // --- Start to read a snapshot info
      while (true) {

        TrajectorySnapshot snapshot = new TrajectorySnapshot(raf, 0); // Temp value for offset
        while ((line = raf.readLine()) != null) {
          if (line.startsWith("#")) {
            continue;
          } else {
            break;
          }
        }
        if (line == null) {
          return raf;
        }

        /*
         #  Time/KE/E/T
         1.05000000000000          2.59560693399160         -1312.08156254049          318.737956024591
         */
        st = new StringTokenizer(line, " ");
        double value;
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          try {
            value = Double.parseDouble(token);
            snapshot.addProperty("Time", new Double(value));
          } catch (Exception ex) {

          }
        }
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          try {
            value = Double.parseDouble(token);
            snapshot.addProperty("KE", new Double(value));
          } catch (Exception ex) {

          }
        }
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          try {
            value = Double.parseDouble(token);
            snapshot.addProperty("E", new Double(value));
          } catch (Exception ex) {

          }
        }
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          try {
            value = Double.parseDouble(token);
            snapshot.addProperty("T", new Double(value));
          } catch (Exception ex) {

          }
        }

        // --- Start to read coordinates
        //   -0.184578838443719E-01    -0.650093939728458E-01     0.605513913742120E-01
        offset = raf.getFilePointer();
        snapshot.setOffset(offset);

        int nat = 0;
        while ((line = raf.readLine()) != null) {
          if (line.startsWith("#")) {
            continue;
          }
          ++nat;
          if (nat >= n_atoms) {
            break;
          }
        }

        // --- Start to read velocities
        nat = 0;
        while ((line = raf.readLine()) != null) {
          if (line.startsWith("#")) {
            continue;
          }
          ++nat;
          if (nat >= n_atoms) {
            break;
          }
        }

        if (line != null) {
          trajectorySnapshots.add(snapshot);
        }
      } // --- End of global while

    } catch (Exception e) {
    }

    try {
      raf.seek(0);
      //raf.close();
    } catch (Exception e) {
    }

    logger.info("Found " + trajectorySnapshots.size() + "snapshots");
    /*
     for (i = n_snapshots - found; i < n_snapshots; i++) {
     fseek(t_file, snapshots[i].offset, SEEK_SET);
     //fgets( bufer, 254, t_file );
     //bufer[255]=0x00;
     //fget_line( bufer, 256, t_file );
     read_line(bufer, 256, t_file);
     printf("%5d: %9ld %s\n", i + 1, snapshots[i].offset, bufer);
     }
     */

    return raf;
  }

  public List<TrajectorySnapshot> getTrajectorySnapshots() {
    return trajectorySnapshots;
  }
}
