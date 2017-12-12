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
package cct.vecmath;

import static cct.Constants.DEGREES_TO_RADIANS;
import cct.interfaces.Point3fInterface;
import static cct.vecmath.Point3f.RADIANS_TO_DEGREES;
import java.util.logging.Logger;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class VecmathTools {

  static final String ZERO_DISTANCE = "Zero distance between atoms";
  static final String UNDERFINED_ANGLE = "Angle is 0 or 180";
  private static final Logger LOG = Logger.getLogger(VecmathTools.class.getName());

  private VecmathTools() {
  }

  /**
   * Returns element of the vector (signed) with the largest absolute value
   *
   * @param x float[]
   * @return float
   */
  public static float getMaxAbsValue(float x[]) {
    float s = 0;
    if (x == null || x.length < 1) {
      return s;
    }
    s = x[0];
    for (int i = 1; i < x.length; i++) {
      if (Math.abs(s) < Math.abs(x[i])) {
        s = x[i];
      }
    }
    return s;
  }

  /**
   * Calculates root-mean-square of the vector
   *
   * @param x float[]
   * @return double
   */
  public static double getRMS(float x[]) {
    double s = 0;
    if (x == null || x.length < 1) {
      return s;
    }
    for (int i = 0; i < x.length; i++) {
      s += x[i] * x[i];
    }
    return Math.sqrt(s / x.length);
  }

  public static void fromInternalToCartesiansInDegrees(Point3f atom, double bond, double angle, double torsion, Point3f na, Point3f nb,
      Point3f nc) {
    fromInternalToCartesians(atom, bond, DEGREES_TO_RADIANS * angle, DEGREES_TO_RADIANS * torsion, na, nb, nc);
  }

  public static void fromInternalToCartesians(Point3f atom, double bond, double angle, double torsion, Point3f na, Point3f nb,
      Point3f nc) {
    if (na == null) {
      // --- The first atom. Put it into the origin
      atom.setXYZ(0, 0, 0);
      return;
    } else if (nb == null) {
      // --- the second atom: put it along the X-axis
      atom.setXYZ(bond, 0, 0); // So, we put it along X-axis
      return;
    } else if (nc == null) {
      // --- Third atom (put it into the XOY plane
      atom.setXYZ(bond * Math.cos(angle), bond * Math.sin(angle), 0); //
      return;
    }

    double SIN2, COS2, SIN3, COS3,
        VT[] = new double[3], V1[] = new double[3],
        V2[] = new double[3];
    double R2,
        V3[] = new double[3], VA1, VB1, VC1, R3, V[] = new double[3];

    SIN2 = Math.sin(angle);
    COS2 = Math.cos(angle);
    SIN3 = Math.sin(torsion);
    COS3 = Math.cos(torsion);

    VT[0] = bond * COS2;
    VT[1] = bond * SIN2 * SIN3;
    VT[2] = bond * SIN2 * COS3;

    V1[0] = nc.getX() - nb.getX();
    V1[1] = nc.getY() - nb.getY();
    V1[2] = nc.getZ() - nb.getZ();

    V2[0] = nb.getX() - na.getX();
    V2[1] = nb.getY() - na.getY();
    V2[2] = nb.getZ() - na.getZ();

    R2 = Math.sqrt(V2[0] * V2[0] + V2[1] * V2[1] + V2[2] * V2[2]);

    V3[0] = V1[1] * V2[2] - V1[2] * V2[1];
    V3[1] = V1[2] * V2[0] - V1[0] * V2[2];
    V3[2] = V1[0] * V2[1] - V1[1] * V2[0];

    R3 = Math.sqrt(V3[0] * V3[0] + V3[1] * V3[1] + V3[2] * V3[2]);

    V2[0] = V2[0] / R2;
    V2[1] = V2[1] / R2;
    V2[2] = V2[2] / R2;

    V3[0] = V3[0] / R3;
    V3[1] = V3[1] / R3;
    V3[2] = V3[2] / R3;

    V[0] = V2[1] * V3[2] - V2[2] * V3[1];
    V[1] = V2[2] * V3[0] - V2[0] * V3[2];
    V[2] = V2[0] * V3[1] - V2[1] * V3[0];

    VA1 = V2[0] * VT[0] + V3[0] * VT[1] + V[0] * VT[2];
    VB1 = V2[1] * VT[0] + V3[1] * VT[1] + V[1] * VT[2];
    VC1 = V2[2] * VT[0] + V3[2] * VT[1] + V[2] * VT[2];

    atom.setXYZ(na.getX() + (float) VA1, na.getY() + (float) VB1, na.getZ() + (float) VC1);
  }

  public static double[] makeInternalCoordInDegrees(Point3d atom, Point3d na, Point3d nb, Point3d nc) throws Exception {
    double[] internal = makeInternalCoord(atom, na, nb, nc);
    internal[1] *= RADIANS_TO_DEGREES;
    internal[2] *= RADIANS_TO_DEGREES;
    return internal;
  }

  public static double[] makeInternalCoord(Point3d atom, Point3d na, Point3d nb, Point3d nc) throws Exception {
    double[] internal = new double[3];
    makeInternalCoord(atom, na, nb, nc, internal);
    return internal;
  }

  public static void makeInternalCoordInDegrees(Point3d atom, Point3d na, Point3d nb, Point3d nc, double[] internal) throws Exception {
    makeInternalCoord(atom, na, nb, nc, internal);
    internal[1] *= RADIANS_TO_DEGREES;
    internal[2] *= RADIANS_TO_DEGREES;
  }

  public static void makeInternalCoord(Point3d atom, Point3d na, Point3d nb, Point3d nc, double[] internal) throws Exception {

    internal[0] = atom.distanceTo(na);
    if (internal[0] == 0) {
      LOG.severe(ZERO_DISTANCE);
      throw new Exception(ZERO_DISTANCE);
    }
    internal[1] = Point3d.angleBetween(atom, na, nb);
    if (internal[1] == 0 || internal[1] == Math.PI) {
      LOG.severe(UNDERFINED_ANGLE + ": " + internal[1]);
      throw new Exception(UNDERFINED_ANGLE + ": " + internal[1]);
    }
    internal[2] = Point3d.dihedralAngle(atom, na, nb, nc);
  }

  public static void makeInternalCoord(Point3fInterface atom, Point3fInterface na, Point3fInterface nb, Point3fInterface nc, double[] internal) throws Exception {

    internal[0] = atom.distanceTo(na);
    if (internal[0] == 0) {
      LOG.severe(ZERO_DISTANCE);
      throw new Exception(ZERO_DISTANCE);
    }
    internal[1] = Point3f.angleBetween(atom, na, nb);
    if (internal[1] == 0 || internal[1] == Math.PI) {
      LOG.severe(UNDERFINED_ANGLE + ": " + internal[1]);
      throw new Exception(UNDERFINED_ANGLE + ": " + internal[1]);
    }
    internal[2] = Point3f.dihedralAngle(atom, na, nb, nc);
  }

}
