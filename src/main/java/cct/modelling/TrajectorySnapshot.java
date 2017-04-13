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

import cct.vecmath.Point3f;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
public class TrajectorySnapshot {

  private RandomAccessFile raFile = null;
  private long Offset = 0;
  private RandomAccessFile tmpFile = null;

  private SingleTrajectoryNew referenceTrajectory;
  private long tmpOffset = 0;
  private float coordinates[][];
  private Map<String, Object> properties = null;

  static final Logger logger = Logger.getLogger(TrajectorySnapshot.class.getCanonicalName());
  
  public TrajectorySnapshot() {

  }

  public TrajectorySnapshot(RandomAccessFile ra_file, long offset) {
    raFile = ra_file;
    Offset = offset;

  }

  public float[][] getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(float[][] coordinates) {
    this.coordinates = coordinates;
  }

  public void setCoordinates(List<Point3f> coordList) {
    coordinates = new float[coordList.size()][3];
    for (int i = 0; i < coordinates.length; i++) {
      Point3f p3f = coordList.get(i);
      coordinates[i][0] = p3f.getX();
      coordinates[i][1] = p3f.getY();
      coordinates[i][2] = p3f.getZ();
    }
  }

  public void clearCoordinates() {
    coordinates = null;
  }

  public RandomAccessFile getFile() {
    return raFile;
  }

  public long getOffset() {
    return Offset;
  }

  public void setOffset(long offset) {
    Offset = offset;
  }

  public SingleTrajectoryNew getReferenceTrajectory() {
    return referenceTrajectory;
  }

  public void setReferenceTrajectory(SingleTrajectoryNew referenceTrajectory) {
    this.referenceTrajectory = referenceTrajectory;
  }

  public RandomAccessFile getTmpFile() {
    return tmpFile;
  }

  public void setTmpFile(RandomAccessFile tmpFile) {
    this.tmpFile = tmpFile;
  }

  public long getTmpOffset() {
    return tmpOffset;
  }

  public void setTmpOffset(long tmpOffset) {
    this.tmpOffset = tmpOffset;
  }

  public void addProperty(String key, Object value) {
    if (properties == null) {
      properties = new HashMap<String, Object>();
    }
    properties.put(key, value);
  }

  public Object getProperty(String key) {
    if (properties == null) {
      return null;
    }
    return properties.get(key);
  }

  public Double getPropertyAsDouble(String key) {
    if (properties == null) {
      return null;
    }
    Object obj = properties.get(key);

    if (obj instanceof Number) {
      return ((Number) obj).doubleValue();
    } else if (obj instanceof String) {
      try {
        return Double.parseDouble(((String) obj).trim());
      } catch (Exception ex) {
        logger.severe("Error parsing String as a double number: "+obj.toString().trim());
      }
    }
    return null;
  }

}
