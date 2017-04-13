/* ***** BEGIN LICENSE BLOCK *****
 Version: Apache 2.0/GPL 3.0/LGPL 3.0

 CCT - Computational Chemistry Tools
 Jamberoo - Java Molecules Editor

 Copyright 2012 Dr. Vladislav Vasilyev

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

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2004</p>
 *
 * <p>
 * Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Point3d {

  public static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;
  public static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
  public static final float PI_FLOAT = (float) Math.PI;
  private double x, y, z;
  private double xSq, ySq, zSq;
  private double norm, normSq;

  // Constructors
  public Point3d() {
    x = y = z = 0.0;
    xSq = ySq = zSq = 0.0;
    norm = normSq = 0;
  }

  public Point3d(double xx, double yy, double zz) {
    x = xx;
    y = yy;
    z = zz;
    precalc();
  }

  public Point3d getInstance(double xx, double yy, double zz) {
    return new Point3d(xx, yy, zz);
  }

  public Point3d(Point3d p) {
    x = p.x;
    y = p.y;
    z = p.z;

    precalc();
  }

  public Point3d(Point3f p) {
    x = p.getX();
    y = p.getY();
    z = p.getZ();

    precalc();
  }

  /**
   * Vector b - a
   *
   * @param a Point3f
   * @param b Point3f
   */
  public Point3d(Point3d a, Point3d b) {
    x = b.x - a.x;
    y = b.y - a.y;
    z = b.z - a.z;

    precalc();
  }

  private void precalc() {
    xSq = x * x;
    ySq = y * y;
    zSq = z * z;
    normSq = xSq + ySq + zSq;
    norm = Math.sqrt(normSq);
  }

  private void precalcX() {
    xSq = x * x;
    normSq = xSq + ySq + zSq;
    norm = Math.sqrt(normSq);
  }

  private void precalcY() {
    ySq = y * y;
    normSq = xSq + ySq + zSq;
    norm = Math.sqrt(normSq);
  }

  private void precalcZ() {
    zSq = z * z;
    normSq = xSq + ySq + zSq;
    norm = Math.sqrt(normSq);
  }

  /**
   * Substracts vector a2 from vector a1
   *
   * @param a1 Point3d
   * @param a2 Point3d
   */
  public void subtract(Point3d a1, Point3d a2) {
    x = a1.getX() - a2.getX();
    y = a1.getY() - a2.getY();
    z = a1.getZ() - a2.getZ();

    precalc();
  }

  public Point3d getInstance() {
    return new Point3d();
  }

  public Point3d getInstance(Point3d a) {
    return new Point3d(a);
  }

  /**
   * Distance between two points
   *
   * @param p1
   * @param p2
   * @return
   */
  public static double distance(Point3d p1, Point3d p2) {
    return Math.sqrt(((p1.getX() - p2.getX())
        * (p1.getX() - p2.getX())
        + (p1.getY() - p2.getY())
        * (p1.getY() - p2.getY())
        + (p1.getZ() - p2.getZ())
        * (p1.getZ() - p2.getZ())));
  }

  /**
   * Squared distance between two points
   *
   * @param p1
   * @param p2
   * @return
   */
  public static double distanceSquared(Point3d p1, Point3d p2) {
    return ((p1.getX() - p2.getX())
        * (p1.getX() - p2.getX())
        + (p1.getY() - p2.getY())
        * (p1.getY() - p2.getY())
        + (p1.getZ() - p2.getZ())
        * (p1.getZ() - p2.getZ()));
  }

  public double distanceSquared(Point3d p2) {
    return distanceSquared(this, p2);
  }

  /**
   * Distance from this point to point "point"
   *
   * @param point
   * @return
   */
  public double distanceTo(Point3d point) {
    return Math.sqrt(((x - point.getX()) * (x - point.getX())
        + (y - point.getY()) * (y - point.getY())
        + (z - point.getZ()) * (z - point.getZ())));
  }

  public double distanceTo(Point3fInterface point) {
    return Math.sqrt(((x - point.getX()) * (x - point.getX())
        + (y - point.getY()) * (y - point.getY())
        + (z - point.getZ()) * (z - point.getZ())));
  }

  public double distanceTo(double _x, double _y, double _z) {
    return Math.sqrt(((x - _x) * (x - _x)
        + (y - _y) * (y - _y)
        + (z - _z) * (z - _z)));
  }

  public void add(Point3d p) {
    x += p.x;
    y += p.y;
    z += p.z;

    precalc();
  }

  public void add(Point3fInterface p) {
    x += p.getX();
    y += p.getY();
    z += p.getZ();

    precalc();
  }

  public void add(double px, double py, double pz) {
    x += px;
    y += py;
    z += pz;

    precalc();
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public double getXSq() {
    return xSq;
  }

  public double getYSq() {
    return ySq;
  }

  public double getZSq() {
    return zSq;
  }

  public Point3d getXYZ() {
    Point3d temp = new Point3d(this);
    return temp;
  }

  public void multiply(double number) {
    x *= number;
    y *= number;
    z *= number;

    precalc();
  }

  /**
   * Normalizes vector
   */
  public void normalize() {
    double s = this.vectorNorm();
    if (s != 0.0) {
      x /= s;
      y /= s;
      z /= s;

      precalc();
    }
  }

  /**
   * Normalize vector v
   *
   * @param v
   */
  public static void normalize(Point3d v) {
    double s = Point3d.norm(v);
    if (s != 0.0) {
      v.setXYZ(v.getX() / s, v.getY() / s, v.getZ() / s);
    }
  }

  public Point3d crossProduct(Point3d v) {
    return new Point3d(y * v.z - z * v.y, z * v.x - x * v.z,
        x * v.y - y * v.x);
  }

  /**
   * Calculate cross vector for vectors v1 and v2 and put result into the crossVector No any memory allocation in the
   * subroutine
   *
   * @param v1 Point3fInterface
   * @param v2 Point3fInterface
   * @param crossVector Point3fInterface
   * @return Point3fInterface - cross product vector
   */
  public static Point3d crossProduct(Point3d v1, Point3d v2, Point3d crossVector) {
    crossVector.setX(v1.getY() * v2.getZ() - v1.getZ() * v2.getY());
    crossVector.setY(v1.getZ() * v2.getX() - v1.getX() * v2.getZ());
    crossVector.setZ(v1.getX() * v2.getY() - v1.getY() * v2.getX());
    return crossVector;
  }

  /*
   * inline void VectorProduct(real *x, real *y, real *p) { p[0] = x[1]*y[2] - x[2]*y[1]; p[1] = x[2]*y[0] - x[0]*y[2]; p[2] =
   * x[0]*y[1] - x[1]*y[0];
   *
   */
  public void crossProduct(Point3fInterface v1, Point3fInterface v2) {
    x = v1.getY() * v2.getZ() - v1.getZ() * v2.getY();
    y = v1.getZ() * v2.getX() - v1.getX() * v2.getZ();
    z = v1.getX() * v2.getY() - v1.getY() * v2.getX();

    precalc();
  }

  public double product(Point3d v) {
    return x * v.x + y * v.y + z * v.z;
  }

  public double product(Point3fInterface v) {
    return x * v.getX() + y * v.getY() + z * v.getZ();
  }

  static public double product(Point3d a, Point3d b) {
    return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ();
  }

  public double vectorNorm() {
    return norm;
    //return Math.sqrt((x * x + y * y + z * z));
  }

  public double norm() {
    return norm;
    //return Math.sqrt((x * x + y * y + z * z));
  }

  public static double vectorNorm(double vec[]) {
    return Math.sqrt((vec[0] * vec[0] + vec[1] * vec[1]
        + vec[2] * vec[2]));
  }

  public static double vectorNorm(float vec[]) {
    return Math.sqrt((vec[0] * vec[0] + vec[1] * vec[1]
        + vec[2] * vec[2]));
  }

  public static double norm(float vec[]) {
    return Math.sqrt((vec[0] * vec[0] + vec[1] * vec[1]
        + vec[2] * vec[2]));
  }

  public static double norm(double vec[]) {
    return Math.sqrt((vec[0] * vec[0] + vec[1] * vec[1]
        + vec[2] * vec[2]));
  }

  public static double norm(Point3d vec) {
    return Math.sqrt((vec.getX() * vec.getX()
        + vec.getY() * vec.getY()
        + vec.getZ() * vec.getZ()));
  }

  public static double squaredNorm(Point3d vec) {
    return vec.getX() * vec.getX()
        + vec.getY() * vec.getY()
        + vec.getZ() * vec.getZ();
  }

  public double vectorSquaredNorm() {
    return normSq;
    //return x * x + y * y + z * z;
  }

  public double squaredNorm() {
    return normSq;
    //return x * x + y * y + z * z;
  }

  public void setX(double x) {
    this.x = x;
    this.precalcX();
  }

  public void setY(double y) {
    this.y = y;
    precalcY();
  }

  public void setZ(double z) {
    this.z = z;
    precalcZ();
  }

  public void setXYZ(double xx, double yy, double zz) {
    x = xx;
    y = yy;
    z = zz;

    precalc();
  }

  public void setXYZ(Point3d p) {
    x = p.x;
    y = p.y;
    z = p.z;

    precalc();
  }

  public void setXYZ(Point3fInterface xyz) {
    x = xyz.getX();
    y = xyz.getY();
    z = xyz.getZ();

    precalc();
  }

  public void substract(Point3d p) {
    x -= p.x;
    y -= p.y;
    z -= p.z;

    precalc();
  }

  public void substract(Point3f p) {
    x -= p.x;
    y -= p.y;
    z -= p.z;

    precalc();
  }

  /**
   * Angle between atoms "a"-"current"-"b"
   *
   * @param a gAtom
   * @param b gAtom
   * @return float
   */
  public double angleBetween(Point3d a, Point3d b) {
    Point3d v1 = new Point3d(a.x - x, a.y - y, a.z - z);
    Point3d v2 = new Point3d(b.x - x, b.y - y, b.z - z);
    double cosa = ((v1.x * v2.x + v1.y * v2.y + v1.z * v2.z)
        / (v1.vectorNorm() * v2.vectorNorm()));
    if (cosa > 1.0) {
      return 0;
    } else if (cosa < -1.0) {
      return Math.PI;
    }
    return Math.acos(cosa);
  }

  /**
   * Calculate angle a-x-b
   *
   * @param a Point3fInterface
   * @param x Point3fInterface
   * @param b Point3fInterface
   * @return float
   */
  public static double angleBetween(Point3d a, Point3d x, Point3d b) {
    double v1_x = a.getX() - x.getX();
    double v1_y = a.getY() - x.getY();
    double v1_z = a.getZ() - x.getZ();

    double v2_x = b.getX() - x.getX();
    double v2_y = b.getY() - x.getY();
    double v2_z = b.getZ() - x.getZ();

    double v1_norm = Math.sqrt(v1_x * v1_x + v1_y * v1_y + v1_z * v1_z);
    double v2_norm = Math.sqrt(v2_x * v2_x + v2_y * v2_y + v2_z * v2_z);

    double cosa = ((v1_x * v2_x + v1_y * v2_y + v1_z * v2_z)
        / (v1_norm * v2_norm));

    if (cosa > 1.0) {
      return 0;
    } else if (cosa < -1.0) {
      return Math.PI;
    }
    return Math.acos(cosa);
  }

  public static void translatePoint(Point3d p, double dir[], double increment) {
    p.setX(p.getX() + dir[0] * increment);
    p.setY(p.getY() + dir[1] * increment);
    p.setZ(p.getZ() + dir[2] * increment);
  }

  /**
   * !!! Check out that it works properly!!!
   *
   * @param p1 Point3f
   * @param p2 Point3f
   * @return float
   */
  public double distanceToLine(Point3d p1, Point3d p2) {
    Point3d p21 = new Point3d(p1, p2);
    double d = p21.vectorNorm();
    if (d < 0.00001f) {
      return 0; // Line is not determined
    }
    Point3d p10 = new Point3d(this, p1);
    Point3d cross = p21.crossProduct(p10);
    return cross.vectorNorm() / d;
  }

  public static double dihedralAngleInDegrees(Point3d v1, Point3d v2, Point3d v3, Point3d v4) {
    return RADIANS_TO_DEGREES * dihedralAngle(v1, v2, v3, v4);
  }

  public static double dihedralAngle(Point3d v1, Point3d v2, Point3d v3, Point3d v4) {
    //vPoint3f r1, r2, n1, n2, n3;
    double cosang, sinang;
    //r1 = v1 - v2;
    Point3d r1 = new Point3d(v2, v1);
    //r2 = v3 - v2;
    Point3d r2 = new Point3d(v2, v3);
    //n1 = (r2 ^ r1).normalize();
    Point3d n1 = r2.crossProduct(r1);
    n1.normalize();
    //n3 = (n1 ^ r2).normalize();
    Point3d n3 = n1.crossProduct(r2);
    n3.normalize();
    //r1 = v2 - v3;
    r1.x = v2.getX() - v3.getX();
    r1.y = v2.getY() - v3.getY();
    r1.z = v2.getZ() - v3.getZ();

    //r2 = v4 - v3;
    r2.x = v4.getX() - v3.getX();
    r2.y = v4.getY() - v3.getY();
    r2.z = v4.getZ() - v3.getZ();

    //n2 = (r2 ^ r1).normalize();
    Point3d n2 = r2.crossProduct(r1);
    n2.normalize();

    //cosang = n1 * n2;
    cosang = n1.product(n2);

    //sinang = n2 * n3;
    sinang = n2.product(n3);

    if (cosang < -1.0f) {
      cosang = -1.0f;
    } else if (cosang > 1.0f) {
      cosang = 1.0f;
    }

    if (sinang >= 0) {
      return -Math.acos(cosang);
    } else {
      return Math.acos(cosang);
    }
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundX(Point3d point, double angle) {
    double newY = point.getY() * Math.cos(angle) + point.getZ() * Math.sin(angle);
    double newZ = -point.getY() * Math.sin(angle) + point.getZ() * Math.cos(angle);
    point.setY(newY);
    point.setZ(newZ);
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundXinDegrees(Point3d point, double angle) {
    angle = angle * DEGREES_TO_RADIANS;
    double newY = point.getY() * Math.cos(angle) + point.getZ() * Math.sin(angle);
    double newZ = -point.getY() * Math.sin(angle) + point.getZ() * Math.cos(angle);
    point.setY(newY);
    point.setZ(newZ);
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundX(Point3fInterface point, double angle) {
    double newY = point.getY() * Math.cos(angle) + point.getZ() * Math.sin(angle);
    double newZ = -point.getY() * Math.sin(angle) + point.getZ() * Math.cos(angle);
    point.setY(newY);
    point.setZ(newZ);
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundY(Point3d point, double angle) {
    double newX = point.getX() * Math.cos(angle) + point.getZ() * Math.sin(angle);
    double newZ = -point.getX() * Math.sin(angle) + point.getZ() * Math.cos(angle);
    point.setX(newX);
    point.setZ(newZ);
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundYinDegrees(Point3d point, double angle) {
    angle = angle * DEGREES_TO_RADIANS;
    double newX = point.getX() * Math.cos(angle) + point.getZ() * Math.sin(angle);
    double newZ = -point.getX() * Math.sin(angle) + point.getZ() * Math.cos(angle);
    point.setX(newX);
    point.setZ(newZ);
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundY(Point3fInterface point, double angle) {
    double newX = point.getX() * Math.cos(angle) + point.getZ() * Math.sin(angle);
    double newZ = -point.getX() * Math.sin(angle) + point.getZ() * Math.cos(angle);
    point.setX(newX);
    point.setZ(newZ);
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundZ(Point3d point, double angle) {
    double newX = point.getX() * Math.cos(angle) - point.getY() * Math.sin(angle);
    double newY = point.getX() * Math.sin(angle) + point.getY() * Math.cos(angle);
      point.setX(newX);
    point.setY(newY);
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundZ(Point3fInterface point, double angle) {
    double newX = point.getX() * Math.cos(angle) - point.getY() * Math.sin(angle);
    double newY = point.getX() * Math.sin(angle) + point.getY() * Math.cos(angle);
      point.setX(newX);
    point.setY(newY);
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundZinDegrees(Point3d point, double angle) {
    angle = angle * DEGREES_TO_RADIANS;
    double newX = point.getX() * Math.cos(angle) - point.getY() * Math.sin(angle);
    double newY = point.getX() * Math.sin(angle) + point.getY() * Math.cos(angle);
      point.setX(newX);
    point.setY(newY);
  }

  public String toString() {
    return "(" + x + ", " + y + ", " + z + ") d=" + norm;
  }

  public static void main(String[] args) {
    Point3d p1 = new Point3d(1f, 2f, 3f);
    Point3d p2 = new Point3d(0, 0, 0);
    Point3d p3 = new Point3d(2, 2, 2);
    p2 = p1;
    p1 = p3;
    System.out.println("p1: " + p1.toString() + " p2: " + p2.toString() + " p3: " + p3.toString());
  }
}
