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

import cct.interfaces.Point3fInterface;
import java.lang.reflect.Array;

/**
 * <p>Title: Picking</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Geometry3d {

  static float SMALL_NUM = 0.00000001f; // anything that avoids division overflow

  private Geometry3d() {
  }

  /**
   * Rotates "point" around axis p2-p1 by angle theta
   * @param point Point3fInterface
   * @param theta float
   * @param p1 Point3fInterface
   * @param p2 Point3fInterface
   */
  static public void rotatePointAroundArbitraryAxis(Point3fInterface point, float theta, Point3fInterface p1, Point3fInterface p2) {

    Point3f q2 = null;

    // Step 1

    Point3f q1 = new Point3f(point.getX() - p1.getX(),
            point.getY() - p1.getY(),
            point.getZ() - p1.getZ());
    Point3f u = new Point3f(p2.getX() - p1.getX(), p2.getY() - p1.getY(),
            p2.getZ() - p1.getZ());
    u.normalize();
    float d = (float) Math.sqrt(u.y * u.y + u.z * u.z);

    // Step 2
    if (Math.abs(d) > 0.000001f) {
      q2 = new Point3f(q1.x,
              q1.y * u.z / d - q1.z * u.y / d,
              q1.y * u.y / d + q1.z * u.z / d);
    } else {
      q2 = new Point3f(q1);
    }

    // Step 3
    q1.x = q2.x * d - q2.z * u.x;
    q1.y = q2.y;
    q1.z = q2.x * u.x + q2.z * d;

    // Step 4
    float cos_theta = (float) Math.cos(theta);
    float sin_theta = (float) Math.sin(theta);
    q2.x = q1.x * cos_theta - q1.y * sin_theta;
    q2.y = q1.x * sin_theta + q1.y * cos_theta;
    q2.z = q1.z;

    // Inverse of step 3
    q1.x = q2.x * d + q2.z * u.x;
    q1.y = q2.y;
    q1.z = -q2.x * u.x + q2.z * d;

    // Inverse of step 2
    if (Math.abs(d) > 0.000001f) {
      q2.x = q1.x;
      q2.y = q1.y * u.z / d + q1.z * u.y / d;
      q2.z = -q1.y * u.y / d + q1.z * u.z / d;
    } else {
      q2 = q1;
    }

    // Inverse of step 1
    point.setX(q2.getX() + p1.getX());
    point.setY(q2.getY() + p1.getY());
    point.setZ(q2.getZ() + p1.getZ());
  }

  /**
   * Calculates distance between two segments
   * @param s1 Segment3f
   * @param s2 Segment3f
   * @return double
   */
  public static double segmentToSegmentDistance(Segment3f s1, Segment3f s2) {

    Point3f u = new Point3f(s1.p2, s1.p1);
    Point3f v = new Point3f(s2.p2, s2.p1);
    Point3f w = new Point3f(s1.p1, s2.p1);

    float a = Point3f.product(u, u); //dot(u,u);  // always >= 0
    float b = Point3f.product(u, v); //dot(u,v);
    float c = Point3f.product(v, v); //dot(v,v);  // always >= 0
    float d = Point3f.product(u, w); //dot(u,w);
    float e = Point3f.product(v, w); //dot(v,w);
    float D = a * c - b * b; // always >= 0
    float sc, sN, sD = D; // sc = sN / sD, default sD = D >= 0
    float tc, tN, tD = D; // tc = tN / tD, default tD = D >= 0

    // compute the line parameters of the two closest points
    if (D < SMALL_NUM) { // the lines are almost parallel
      sN = 0; // force using point P0 on segment S1
      sD = 1; // to prevent possible division by 0.0 later
      tN = e;
      tD = c;
    } else { // get the closest points on the infinite lines
      sN = (b * e - c * d);
      tN = (a * e - b * d);
      if (sN < 0) { // sc < 0 => the s=0 edge is visible
        sN = 0;
        tN = e;
        tD = c;
      } else if (sN > sD) { // sc > 1 => the s=1 edge is visible
        sN = sD;
        tN = e + b;
        tD = c;
      }
    }

    if (tN < 0.0) { // tc < 0 => the t=0 edge is visible
      tN = 0;
      // recompute sc for this edge
      if (-d < 0.0) {
        sN = 0;
      } else if (-d > a) {
        sN = sD;
      } else {
        sN = -d;
        sD = a;
      }
    } else if (tN > tD) { // tc > 1 => the t=1 edge is visible
      tN = tD;
      // recompute sc for this edge
      if ((-d + b) < 0.0) {
        sN = 0;
      } else if ((-d + b) > a) {
        sN = sD;
      } else {
        sN = (-d + b);
        sD = a;
      }
    }
    // finally do the division to get sc and tc
    sc = Math.abs(sN) < SMALL_NUM ? 0 : sN / sD;
    tc = Math.abs(tN) < SMALL_NUM ? 0 : tN / tD;

    // get the difference of the two closest points
    //Vector dP = w + (sc * u) - (tc * v); // = S1(sc) - S2(tc)
    float x = w.x + (sc * u.x) - (tc * v.x);
    float y = w.y + (sc * u.y) - (tc * v.y);
    float z = w.z + (sc * u.z) - (tc * v.z);

    //return norm(dP); // return the closest distance
    return Math.sqrt(x * x + y * y + z * z);
  }

  /**
   * Calculates distance between two segments and returns coordinates of
   * the closest points on each segment
   * @param s1 Segment3f
   * @param s2 Segment3f
   * @param p1 Point3fInterface
   * @param p2 Point3fInterface
   * @return double
   */
  public static double segmentToSegmentDistance(Segment3f s1, Segment3f s2, Point3f p1, Point3f p2) {
    //Vector   u = S1.P1 - S1.P0;
    //Vector   v = S2.P1 - S2.P0;
    //Vector   w = S1.P0 - S2.P0;

    Point3f u = new Point3f(s1.p2, s1.p1);
    Point3f v = new Point3f(s2.p2, s2.p1);
    Point3f w = new Point3f(s1.p1, s2.p1);

    float a = Point3f.product(u, u); //dot(u,u);  // always >= 0
    float b = Point3f.product(u, v); //dot(u,v);
    float c = Point3f.product(v, v); //dot(v,v);  // always >= 0
    float d = Point3f.product(u, w); //dot(u,w);
    float e = Point3f.product(v, w); //dot(v,w);
    float D = a * c - b * b; // always >= 0
    float sc, sN, sD = D; // sc = sN / sD, default sD = D >= 0
    float tc, tN, tD = D; // tc = tN / tD, default tD = D >= 0

    // compute the line parameters of the two closest points
    if (D < SMALL_NUM) { // the lines are almost parallel
      sN = 0; // force using point P0 on segment S1
      sD = 1; // to prevent possible division by 0.0 later
      tN = e;
      tD = c;
    } else { // get the closest points on the infinite lines
      sN = (b * e - c * d);
      tN = (a * e - b * d);
      if (sN < 0) { // sc < 0 => the s=0 edge is visible
        sN = 0;
        tN = e;
        tD = c;
      } else if (sN > sD) { // sc > 1 => the s=1 edge is visible
        sN = sD;
        tN = e + b;
        tD = c;
      }
    }

    if (tN < 0.0) { // tc < 0 => the t=0 edge is visible
      tN = 0;
      // recompute sc for this edge
      if (-d < 0.0) {
        sN = 0;
      } else if (-d > a) {
        sN = sD;
      } else {
        sN = -d;
        sD = a;
      }
    } else if (tN > tD) { // tc > 1 => the t=1 edge is visible
      tN = tD;
      // recompute sc for this edge
      if ((-d + b) < 0.0) {
        sN = 0;
      } else if ((-d + b) > a) {
        sN = sD;
      } else {
        sN = (-d + b);
        sD = a;
      }
    }
    // finally do the division to get sc and tc
    sc = Math.abs(sN) < SMALL_NUM ? 0 : sN / sD;
    tc = Math.abs(tN) < SMALL_NUM ? 0 : tN / tD;

    // get the difference of the two closest points
    //Vector dP = w + (sc * u) - (tc * v); // = S1(sc) - S2(tc)

    p1.setXYZ(sc * u.x, sc * u.y, sc * u.z);
    p2.setXYZ(tc * v.x, tc * v.y, tc * v.z);

    float x = w.x + p1.getX() - p2.getX();
    float y = w.y + p1.getY() - p2.getY();
    float z = w.z + p1.getZ() - p2.getZ();

    //return norm(dP); // return the closest distance
    return Math.sqrt(x * x + y * y + z * z);
  }

  /**
   * Used for the so-called Alternate Z-matrix Format i.e. allows nuclear positions
   * to be specified using two bond angles rather than a bond angle and a dihedral angle.
   * Not elegant solution but a quick fix...
   * @param a -
   * @param b
   * @param angle_1
   * @param angle_2
   * @param direction - there are two possible positions
   * @return - generated unit vector
   * @throws Exception
   */
  public static double[] getForTwoBondAngles(double[] a, double[] b, double angle_1, double angle_2, int direction) throws Exception {
    double[] target = new double[3];
    // Since we solve the quadratic equation we have two solutions

    // --- Preliminaries...

    double aLength = Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    if (aLength == 0) {
      throw new Exception("The first vector has zero length");
    }
    double A = aLength * Math.cos(angle_1);
    double bLength = Math.sqrt(b[0] * b[0] + b[1] * b[1] + b[2] * b[2]);
    if (bLength == 0) {
      throw new Exception("The second vector has zero length");
    }
    double B = bLength * Math.cos(angle_2);

    // Power(az,2)*(Power(bx,2) + Power(by,2)) - 2*ax*az*bx*bz - 2*ay*by*(ax*bx + az*bz) +
    // Power(ay,2)*(Power(bx,2) + Power(bz,2)) + Power(ax,2)*(Power(by,2) + Power(bz,2))
    // denominator is an eqivivalent of 2a
    double denominator = a[2] * a[2] * (b[0] * b[0] + b[1] * b[1])
            - 2.0 * a[0] * a[2] * b[0] * b[2] - 2.0 * a[1] * b[1]
            * (a[0] * b[0] + a[2] * b[2]) + a[1] * a[1]
            * (b[0] * b[0] + b[2] * b[2]) + a[0] * a[0] * (b[1] * b[1] + b[2] * b[2]);

    double minusB = 0;

    if (a[0] != 0 || a[1] != 0) {
      // A*az*(Power(bx,2) + Power(by,2)) + Power(ax,2)*B*bz - A*ay*by*bz - ax*bx*(az*B + A*bz) + ay*B*(-(az*by) + ay*bz)
      // minusB is an equivalent of -b
      minusB = A * a[2] * (b[0] * b[0] + b[1] * b[1]) + a[0] * a[0] * B * b[2] - A * a[1] * b[1] * b[2] - a[0] * b[0] * (a[2] * B + A * b[2])
              + a[1] * B * (-(a[2] * b[1]) + a[1] * b[2]);
    } else {
      // Power(ax,2)*B*by - ax*bx*(ay*B + A*by) + az*B*(az*by - ay*bz) + A*(-(az*by*bz) + ay*(Power(bx,2) + Power(bz,2)))
      minusB = Power2(a[0]) * B * b[1] - a[0] * b[0] * (a[1] * B + A * b[1]) + a[2] * B * (a[2] * b[1] - a[1] * b[2])
              + A * (-(a[2] * b[1] * b[2]) + a[1] * (Power2(b[0]) + Power2(b[2])));
    }

    double discriminant = 0;

    if (a[0] != 0 || a[1] != 0) {
      // Power(ay*bx - ax*by,2)*(-(Power(az,2)*(Power(B,2) - Power(bx,2) - Power(by,2))) -
      // Power(A,2)*(Power(bx,2) + Power(by,2)) + 2*A*az*B*bz - Power(A,2)*Power(bz,2) +
      // 2*ay*by*(A*B - az*bz) + 2*ax*bx*(A*B - ay*by - az*bz) +
      // Power(ay,2)*(-Power(B,2) + Power(bx,2) + Power(bz,2)) +
      // Power(ax,2)*(-Power(B,2) + Power(by,2) + Power(bz,2)))
      discriminant = Power2(a[1] * b[0] - a[0] * b[1]) * (-(Power2(a[2]) * (Power2(B) - Power2(b[0]) - Power2(b[1])))
              - Power2(A) * (Power2(b[0]) + Power2(b[1])) + 2.0 * A * a[2] * B * b[2] - Power2(A) * Power2(b[2])
              + 2.0 * a[1] * b[1] * (A * B - a[2] * b[2]) + 2.0 * a[0] * b[0] * (A * B - a[1] * b[1] - a[2] * b[2])
              + Power2(a[1]) * (-Power2(B) + Power2(b[0]) + Power2(b[2]))
              + Power2(a[0]) * (-Power2(B) + Power2(b[1]) + Power2(b[2])));
    } else {
      // Power(az*bx - ax*bz,2)*(-(Power(az,2)*(Power(B,2) - Power(bx,2) - Power(by,2))) -
      // Power(A,2)*(Power(bx,2) + Power(by,2)) + 2*A*az*B*bz - Power(A,2)*Power(bz,2) +
      // 2*ay*by*(A*B - az*bz) + 2*ax*bx*(A*B - ay*by - az*bz) +
      // Power(ay,2)*(-Power(B,2) + Power(bx,2) + Power(bz,2)) +
      // Power(ax,2)*(-Power(B,2) + Power(by,2) + Power(bz,2)))
      discriminant = Power2(a[2] * b[0] - a[0] * b[2]) * (-(Power2(a[2]) * (Power2(B) - Power2(b[0]) - Power2(b[1])))
              - Power2(A) * (Power2(b[0]) + Power2(b[1])) + 2.0 * A * a[2] * B * b[2] - Power2(A) * Power2(b[2])
              + 2.0 * a[1] * b[1] * (A * B - a[2] * b[2]) + 2.0 * a[0] * b[0] * (A * B - a[1] * b[1] - a[2] * b[2])
              + Power2(a[1]) * (-Power2(B) + Power2(b[0]) + Power2(b[2]))
              + Power2(a[0]) * (-Power2(B) + Power2(b[1]) + Power2(b[2])));
    }

    if (discriminant < 0.0) {
      throw new Exception("No solution: discriminant < 0.0");
    }

    int index = 2;
    if (a[0] == 0 && a[1] == 0) {
      index = 1;
    }

    boolean one_root = false;
    if (discriminant == 0) {
      target[index] = minusB / denominator;
      one_root = true;
    } else { // Check for the first solution
      target[index] = (minusB + Math.sqrt(discriminant)) / denominator;
    }

    if (index == 2) {
      target = doTheRest(a, b, A, B, target);
    } else {
      target = doTheRest2(a, b, A, B, target);
    }

    if (one_root) {
      return target;
    }

    double[] cross = crossProduct(a, b);
    if (product(target, cross) > 0 && direction > 0) {
      return target;
    }

    // Check the second root

    target[index] = (minusB - Math.sqrt(discriminant)) / denominator;
    if (index == 2) {
      target = doTheRest(a, b, A, B, target);
    } else {
      target = doTheRest2(a, b, A, B, target);
    }
    if (product(target, cross) < 0 && direction < 0) {
      return target;
    }

    return target;
  }

  public static float[] getForTwoBondAngles(float[] a, float[] b, float angle_1, float angle_2, int direction) throws Exception {
    double[] p = getForTwoBondAngles(new double[]{a[0], a[1], a[2]}, new double[]{b[0], b[1], b[2]}, (double) angle_1, (double) angle_2, direction);
    return new float[]{(float) p[0], (float) p[1], (float) p[2]};
  }

  private static double[] doTheRest(double[] a, double[] b, double A, double B, double[] target) {
    if (a[0] != 0.0) {
      //(ax*B - A*bx + az*bx*cz - ax*bz*cz)/(-(ay*bx) + ax*by)
      target[1] = (a[0] * B - A * b[0] + a[2] * b[0] * target[2] - a[0] * b[2] * target[2]) / (-(a[1] * b[0]) + a[0] * b[1]);

      // (A - ay cy - az cz)/ax
      target[0] = (A - a[1] * target[1] - a[2] * target[2]) / a[0];
    } else if (a[1] != 0.0) {
      // (ay*B - A*by + az*by*cz - ay*bz*cz)/(ay*bx - ax*by)
      target[0] = (a[1] * B - A * b[1] + a[2] * b[1] * target[2] - a[1] * b[2] * target[2]) / (a[1] * b[0] - a[0] * b[1]);
      // (A - ax*cx - az*cz)/ay
      target[1] = (A - a[0] * target[0] - a[2] * target[2]) / a[1];
    }
    return target;
  }

  private static double[] doTheRest2(double[] a, double[] b, double A, double B, double[] target) {
    // (az*B - A*bz - az*by*cy + ay*bz*cy)/(az*bx - ax*bz)
    target[0] = (a[2] * B - A * b[2] - a[2] * b[1] * target[1] + a[1] * b[2] * target[1]) / (a[2] * b[0] - a[0] * b[2]);

    // ((A - ax*cx - ay*cy)/az)
    target[2] = (A - a[0] * target[0] - a[1] * target[1]) / a[2];

    return target;
  }

  /**
   * Calculates a cross product of two 3d vectors
   * @param a - 1st vector
   * @param v - 2nd vector
   * @return - cross product vector
   */
  public static double[] crossProduct(double a[], double[] v) {
    return new double[]{a[1] * v[2] - a[2] * v[1], a[2] * v[0] - a[0] * v[2], a[0] * v[1] - a[1] * v[0]};
  }

  public static double product(double[] a, double[] b) {
    return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
  }

  public static double norm(double[] a) {
    return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
  }

  private static double Power2(double a) {
    return a * a;
  }
}
