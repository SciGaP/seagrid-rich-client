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
public class Point3f
    implements Point3fInterface {

  public static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;
  public static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
  public static final float PI_FLOAT = (float) Math.PI;
  public float x, y, z;

  // Constructors
  public Point3f() {
    x = y = z = 0.0f;
  }

  public Point3f(float xx, float yy, float zz) {
    x = xx;
    y = yy;
    z = zz;
  }

  @Override
  public Point3fInterface getInstance(float xx, float yy, float zz) {
    return new Point3f(xx, yy, zz);
  }

  public Point3f(Point3f p) {
    x = p.x;
    y = p.y;
    z = p.z;
  }

  public Point3f(Point3fInterface p) {
    x = p.getX();
    y = p.getY();
    z = p.getZ();
  }

  /**
   * Substracts vector a2 from vector a1
   *
   * @param a1 Point3fInterface
   * @param a2 Point3fInterface
   */
  @Override
  public void subtract(Point3fInterface a1, Point3fInterface a2) {
    x = a1.getX() - a2.getX();
    y = a1.getY() - a2.getY();
    z = a1.getZ() - a2.getZ();
  }

  /**
   * Vector b - a
   *
   * @param a Point3f
   * @param b Point3f
   */
  public Point3f(Point3f a, Point3f b) {
    x = b.x - a.x;
    y = b.y - a.y;
    z = b.z - a.z;
  }

  /**
   * Vector b - a
   *
   * @param a Point3f
   * @param b Point3f
   */
  public Point3f(Point3fInterface a, Point3fInterface b) {
    x = b.getX() - a.getX();
    y = b.getY() - a.getY();
    z = b.getZ() - a.getZ();
  }

  @Override
  public Point3fInterface getInstance() {
    return new Point3f();
  }

  @Override
  public Point3fInterface getInstance(Point3fInterface a) {
    return new Point3f(a);
  }

// Methods
  /**
   * Distance between two points
   *
   * @param p1
   * @param p2
   * @return
   */
  public static double distance(Point3fInterface p1, Point3fInterface p2) {
    return Math.sqrt(((p1.getX() - p2.getX())
        * (p1.getX() - p2.getX())
        + (p1.getY() - p2.getY())
        * (p1.getY() - p2.getY())
        + (p1.getZ() - p2.getZ())
        * (p1.getZ() - p2.getZ())));
  }

  public static double distance(float[] p1, float[] p2) {
    return Math.sqrt(
        (p1[0] - p2[0]) * (p1[0] - p2[0])
        + (p1[1] - p2[1]) * (p1[1] - p2[1])
        + (p1[2] - p2[2]) * (p1[2] - p2[2]));
  }

  public static double distance(Float[] p1, Float[] p2) {
    return Math.sqrt(
        (p1[0] - p2[0]) * (p1[0] - p2[0])
        + (p1[1] - p2[1]) * (p1[1] - p2[1])
        + (p1[2] - p2[2]) * (p1[2] - p2[2]));
  }

  public static double distance(double[] p1, double[] p2) {
    return Math.sqrt(
        (p1[0] - p2[0]) * (p1[0] - p2[0])
        + (p1[1] - p2[1]) * (p1[1] - p2[1])
        + (p1[2] - p2[2]) * (p1[2] - p2[2]));
  }

  public static double distance(Double[] p1, Double[] p2) {
    return Math.sqrt(
        (p1[0] - p2[0]) * (p1[0] - p2[0])
        + (p1[1] - p2[1]) * (p1[1] - p2[1])
        + (p1[2] - p2[2]) * (p1[2] - p2[2]));
  }

  public static double distance(Number[] p1, Number[] p2) {
    return Math.sqrt(
        (p1[0].doubleValue() - p2[0].doubleValue()) * (p1[0].doubleValue() - p2[0].doubleValue())
        + (p1[1].doubleValue() - p2[1].doubleValue()) * (p1[1].doubleValue() - p2[1].doubleValue())
        + (p1[2].doubleValue() - p2[2].doubleValue()) * (p1[2].doubleValue() - p2[2].doubleValue()));
  }

  public static double distanceSquared(Point3fInterface p1, Point3fInterface p2) {
    return ((p1.getX() - p2.getX())
        * (p1.getX() - p2.getX())
        + (p1.getY() - p2.getY())
        * (p1.getY() - p2.getY())
        + (p1.getZ() - p2.getZ())
        * (p1.getZ() - p2.getZ()));
  }

  @Override
  public double distanceTo(Point3fInterface point) {
    return Math.sqrt(((x - point.getX()) * (x - point.getX())
        + (y - point.getY()) * (y - point.getY())
        + (z - point.getZ()) * (z - point.getZ())));
  }

  public double distanceTo(float _x, float _y, float _z) {
    return Math.sqrt(((x - _x) * (x - _x)
        + (y - _y) * (y - _y)
        + (z - _z) * (z - _z)));
  }

  public double distanceTo(double _x, double _y, double _z) {
    return Math.sqrt(((x - _x) * (x - _x)
        + (y - _y) * (y - _y)
        + (z - _z) * (z - _z)));
  }

  public void add(Point3f p) {
    x += p.x;
    y += p.y;
    z += p.z;
  }

  public void add(Point3fInterface p) {
    x += p.getX();
    y += p.getY();
    z += p.getZ();
  }

  public void add(float px, float py, float pz) {
    x += px;
    y += py;
    z += pz;
  }

  @Override
  public float getX() {
    return x;
  }

  @Override
  public float getY() {
    return y;
  }

  @Override
  public float getZ() {
    return z;
  }

  public Point3f getXYZ() {
    Point3f temp = new Point3f(this);
    return temp;
  }

  public void multiply(double number) {
    x *= number;
    y *= number;
    z *= number;
  }

  public void multiply(float number) {
    x *= number;
    y *= number;
    z *= number;
  }

  /**
   * Normalizes vector
   */
  public void normalize() {
    float s = this.vectorNorm();
    if (s != 0.0) {
      x /= s;
      y /= s;
      z /= s;
    }
  }

  public static void normalize(Point3fInterface v) {
    float s = (float) Point3f.norm(v);
    if (s != 0.0) {
      v.setXYZ(v.getX() / s, v.getY() / s, v.getZ() / s);
    }
  }

  public Point3f crossProduct(Point3f v) {
    return new Point3f(y * v.z - z * v.y, z * v.x - x * v.z,
        x * v.y - y * v.x);
  }

  public Point3fInterface crossProduct(Point3fInterface v) {
    return v.getInstance(y * v.getZ() - z * v.getY(),
        z * v.getX() - x * v.getZ(),
        x * v.getY() - y * v.getX());
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
  public static Point3fInterface crossProduct(Point3fInterface v1,
      Point3fInterface v2,
      Point3fInterface crossVector) {
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
  }

  public float product(Point3f v) {
    return x * v.x + y * v.y + z * v.z;
  }

  public float product(Point3fInterface v) {
    return x * v.getX() + y * v.getY() + z * v.getZ();
  }

  static public float product(Point3fInterface a, Point3fInterface b) {
    return a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ();
  }

  public float vectorNorm() {
    return (float) Math.sqrt((x * x + y * y + z * z));
  }

  public float norm() {
    return (float) Math.sqrt((x * x + y * y + z * z));
  }

  public static double vectorNorm(float vec[]) {
    return Math.sqrt((vec[0] * vec[0] + vec[1] * vec[1]
        + vec[2] * vec[2]));
  }

  public static double norm(float vec[]) {
    return Math.sqrt((vec[0] * vec[0] + vec[1] * vec[1]
        + vec[2] * vec[2]));
  }

  public static double norm(Point3fInterface vec) {
    return Math.sqrt((vec.getX() * vec.getX()
        + vec.getY() * vec.getY()
        + vec.getZ() * vec.getZ()));
  }

  public static float squaredNorm(Point3fInterface vec) {
    return vec.getX() * vec.getX()
        + vec.getY() * vec.getY()
        + vec.getZ() * vec.getZ();
  }

  public float vectorSquaredNorm() {
    return x * x + y * y + z * z;
  }

  public float squaredNorm() {
    return x * x + y * y + z * z;
  }

  @Override
  public void setX(double x) {
    this.x = (float) x;
  }

  @Override
  public void setY(double y) {
    this.y = (float) y;
  }

  @Override
  public void setZ(double z) {
    this.z = (float) z;
  }

  public void setXYZ(float xx, float yy, float zz) {
    x = xx;
    y = yy;
    z = zz;
  }

  @Override
  public void setXYZ(double xx, double yy, double zz) {
    x = (float) xx;
    y = (float) yy;
    z = (float) zz;
  }

  public void setXYZ(Point3f p) {
    x = p.x;
    y = p.y;
    z = p.z;
  }

  @Override
  public void setXYZ(Point3fInterface xyz) {
    x = xyz.getX();
    y = xyz.getY();
    z = xyz.getZ();
  }

  public void substract(Point3f p) {
    x -= p.x;
    y -= p.y;
    z -= p.z;
  }

  /**
   * Angle between atoms "a"-"current"-"b"
   *
   * @param a gAtom
   * @param b gAtom
   * @return float
   */
  public float angleBetween(Point3f a, Point3f b) {
    Point3f v1 = new Point3f(a.x - x, a.y - y, a.z - z);
    Point3f v2 = new Point3f(b.x - x, b.y - y, b.z - z);
    double cosa = ((v1.x * v2.x + v1.y * v2.y + v1.z * v2.z)
        / (v1.vectorNorm() * v2.vectorNorm()));
    if (cosa > 1.0) {
      return 0;
    } else if (cosa < -1.0) {
      return PI_FLOAT;
    }
    return (float) Math.acos(cosa);
  }

  static public double angle(Point3f a, Point3f b, Point3f c) {
    Point3f v1 = new Point3f(a.x - b.x, a.y - b.y, a.z - b.z);
    Point3f v2 = new Point3f(c.x - b.x, c.y - b.y, c.z - b.z);
    double cosa = ((v1.x * v2.x + v1.y * v2.y + v1.z * v2.z)
        / (v1.vectorNorm() * v2.vectorNorm()));
    if (cosa > 1.0) {
      return 0;
    } else if (cosa < -1.0) {
      return PI_FLOAT;
    }
    return (float) Math.acos(cosa);
  }

  /**
   * Calculate angle a-x-b
   *
   * @param a Point3fInterface
   * @param x Point3fInterface
   * @param b Point3fInterface
   * @return float
   */
  public static float angleBetween(Point3fInterface a, Point3fInterface x,
      Point3fInterface b) {
    float v1_x = a.getX() - x.getX();
    float v1_y = a.getY() - x.getY();
    float v1_z = a.getZ() - x.getZ();

    float v2_x = b.getX() - x.getX();
    float v2_y = b.getY() - x.getY();
    float v2_z = b.getZ() - x.getZ();

    double v1_norm = Math.sqrt(v1_x * v1_x + v1_y * v1_y + v1_z * v1_z);
    double v2_norm = Math.sqrt(v2_x * v2_x + v2_y * v2_y + v2_z * v2_z);

    double cosa = ((v1_x * v2_x + v1_y * v2_y + v1_z * v2_z)
        / (v1_norm * v2_norm));

    if (cosa > 1.0) {
      return 0;
    } else if (cosa < -1.0) {
      return PI_FLOAT;
    }
    return (float) Math.acos(cosa);
  }

  /**
   * Calculates angle between vectors a & b It's the same as angleBetween(Point3fInterface a, Point3fInterface x,
   * Point3fInterface b) where x=(0,0,0)
   *
   * @param a Point3fInterface
   * @param b Point3fInterface
   * @return float
   */
  public static float angleBetween(Point3fInterface a, Point3fInterface b) {
    double cosa = ((a.getX() * b.getX() + a.getY() * b.getY()
        + a.getZ() * b.getZ())
        / (norm(a) * norm(b)));
    if (cosa > 1.0) {
      return 0;
    } else if (cosa < -1.0) {
      return PI_FLOAT;
    }
    return (float) Math.acos(cosa);
  }

  public static void translatePoint(Point3fInterface p, float dir[],
      float increment) {
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
  public float distanceToLine(Point3f p1, Point3f p2) {
    Point3f p21 = new Point3f(p1, p2);
    float d = p21.vectorNorm();
    if (d < 0.00001f) {
      return 0; // Line is not determined
    }
    Point3f p10 = new Point3f(this, p1);
    Point3f cross = p21.crossProduct(p10);
    return cross.vectorNorm() / d;
  }

  public static double dihedralAngle(Point3fInterface v1, Point3fInterface v2,
      Point3fInterface v3,
      Point3fInterface v4) {
    //vPoint3f r1, r2, n1, n2, n3;
    float cosang, sinang;
    //r1 = v1 - v2;
    Point3f r1 = new Point3f(v2, v1);
    //r2 = v3 - v2;
    Point3f r2 = new Point3f(v2, v3);
    //n1 = (r2 ^ r1).normalize();
    Point3f n1 = r2.crossProduct(r1);
    n1.normalize();
    //n3 = (n1 ^ r2).normalize();
    Point3f n3 = n1.crossProduct(r2);
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
    Point3f n2 = r2.crossProduct(r1);
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

  @Override
  public String toString() {
    return "X= " + x + " Y= " + y + " Z= " + z;
  }

  /**
   *
   * @param point
   * @param angle
   */
  static public void rotateAroundX(Point3f point, double angle) {
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
  static public void rotateAroundY(Point3f point, double angle) {
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
  static public void rotateAroundZ(Point3f point, double angle) {
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
}
