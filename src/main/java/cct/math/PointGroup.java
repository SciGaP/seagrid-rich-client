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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cct.interfaces.Point3fInterface;
import cct.vecmath.Geometry3d;
import cct.vecmath.Point3f;

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
public class PointGroup {

  static double Tolerance = 0.0001;
  static final float TWO_PI_FLOAT = (float) (2.0 * Math.PI);
  static final float PI_FLOAT = (float) Math.PI;
  static final float HALF_PI_FLOAT = (float) (Math.PI / 2.0);
  static final Set<String> validSymmetrySymbols = new HashSet<String>();

  static {
    validSymmetrySymbols.add("C1");
    validSymmetrySymbols.add("CS");
    validSymmetrySymbols.add("CI");
    validSymmetrySymbols.add("CN");
    //validSymmetrySymbols.add("S2N");
    //validSymmetrySymbols.add("CNH");
    //validSymmetrySymbols.add("NH");
    //validSymmetrySymbols.add("CNV");
    //validSymmetrySymbols.add("DN");
    //validSymmetrySymbols.add("DNH");
    //validSymmetrySymbols.add("DND");
    //validSymmetrySymbols.add("T");
    //validSymmetrySymbols.add("TH");
    //validSymmetrySymbols.add("TD");
    //validSymmetrySymbols.add("O");
    //validSymmetrySymbols.add("OH");
  }

  private PointGroup() {
  }

  /**
   * Not implemented yet....
   * @param symbol String
   * @param axisOrder int
   * @param points ArrayList
   * @throws Exception
   */
  public static void applySymmetry(String symbol, int axisOrder, List points) throws Exception {
    if (!validSymmetrySymbols.contains(symbol.toUpperCase())) {
      throw new Exception("Don't know how to apply symmetry " + symbol);
    }

  }

  /**
   *
   * @param symbol String
   * @param axisOrder int
   * @param point Point3fInterface
   * @return Point3fInterface[]
   */
  public static Object[] generatePoints(String symbol, int axisOrder, Point3fInterface point) {

    Object[] p = null;

    // --- C1 group
    if (symbol.toUpperCase().equals("C1")) {
      return p;
    } // --- Ci group
    else if (symbol.toUpperCase().equals("CI")) {
      Point3fInterface testPoint = point.getInstance(point);
      // invert a point
      testPoint.setXYZ(-point.getX(), -point.getY(), -point.getZ());
      if (point.distanceTo(testPoint) > Tolerance) {
        p = new Object[1];
        p[0] = testPoint;
        return p;
      }
    } // --- Cs group
    else if (symbol.toUpperCase().equals("CS")) {

      if (Math.abs(point.getZ()) < Tolerance) {
        return null;
      }

      Point3fInterface testPoint = point.getInstance(point);
      testPoint.setXYZ(point.getX(), point.getY(), -point.getZ());
      p = new Object[1];
      p[0] = testPoint;
      return p;
    } // --- CN group
    else if (symbol.toUpperCase().equals("CN")) {

      if (Math.abs(point.getX()) < Tolerance && Math.abs(point.getY()) < Tolerance) {
        // --- Point is on the Z axis
        return null;
      }

      float theta = TWO_PI_FLOAT / axisOrder;
      Point3fInterface p1 = new Point3f(0, 0, 0);
      Point3fInterface p2 = new Point3f(0, 0, 1);

      p = new Object[axisOrder - 1];

      for (int i = 1, j = 0; i < axisOrder; i++, j++) {
        Point3fInterface testPoint = point.getInstance(point);
        Geometry3d.rotatePointAroundArbitraryAxis(testPoint,
                theta * i, p1, p2);
        p[j] = testPoint;
      }
      return p;
    } else if (symbol.toUpperCase().equals("CNV")) {
      if (Math.abs(point.getX()) < Tolerance
              && Math.abs(point.getY()) < Tolerance) {
        // --- Point is on the Z axis
        return null;
      }

      float theta = TWO_PI_FLOAT / axisOrder;
      Point3fInterface p1 = new Point3f(0, 0, 0);
      Point3fInterface p2 = new Point3f(0, 0, 1);

      List allP = new ArrayList();
      allP.add(point);
      p = new Object[axisOrder - 1];
      //p[axisOrder - 1] = point;

      for (int i = 1, j = 0; i < axisOrder; i++, j++) {
        Point3fInterface testPoint = point.getInstance(point);
        Geometry3d.rotatePointAroundArbitraryAxis(testPoint, theta * i, p1, p2);
        p[j] = testPoint;
        allP.add(testPoint);
      }

      theta /= 2.0f;
      float finalTheta;
      for (int j = 0; j < axisOrder; j++) {

        // --- Normals of Sigma(v) plane
        finalTheta = theta * j + HALF_PI_FLOAT;
        float xn = (float) Math.cos(finalTheta);
        float yn = (float) Math.sin(finalTheta);

        Point3fInterface testPoint = point.getInstance(point);
        float d = testPoint.getX() * xn + testPoint.getY() * yn;
        if (Math.abs(d) > Tolerance) { // Point is not on the plane
          testPoint.setX(testPoint.getX() - 2 * d * xn);
          testPoint.setY(testPoint.getY() - 2 * d * yn);
          allP.add(testPoint);
        }

        // Now reflect points in planes
        for (int i = 0; i < p.length; i++) {
          testPoint = (Point3fInterface) p[i];
          // - Distance to the plane
          d = testPoint.getX() * xn + testPoint.getY() * yn;
          if (Math.abs(d) < Tolerance) { // Point is on the plane
            continue;
          }

          // --- Reflect point

          Point3fInterface reflectedPoint = testPoint.getInstance(
                  testPoint);
          reflectedPoint.setX(reflectedPoint.getX() - 2 * d * xn);
          reflectedPoint.setY(reflectedPoint.getY() - 2 * d * yn);
          allP.add(reflectedPoint);
        }
      }

      // --- remove degenerated points

      return removeDegeneratePoints(point, p, allP);
      /*
      int degeneratedPoints = 0;
      for (int j = 0; j < allP.size(); j++) {
      Point3fInterface testPoint = (Point3fInterface) allP.get(j);
      if (testPoint.distanceTo(point) < Tolerance) {
      allP.set(j, null);
      ++degeneratedPoints;
      continue;
      }
      for (int i = 0; i < p.length; i++) {
      Point3fInterface rotatedPoint = (Point3fInterface) p[i];
      if (testPoint.distanceTo(rotatedPoint) < Tolerance) {
      allP.set(j, null);
      ++degeneratedPoints;
      break;
      }
      }
      }
      
      if (degeneratedPoints == allP.size()) {
      return p;
      }
      
      Object[] finalPoints = new Object[p.length + allP.size() -
      degeneratedPoints];
      for (int i = 0; i < p.length; i++) {
      finalPoints[i] = p[i];
      }
      int count = p.length;
      for (int j = 0; j < allP.size(); j++) {
      Point3fInterface testPoint = (Point3fInterface) allP.get(j);
      if (testPoint == null) {
      continue;
      }
      finalPoints[count] = testPoint;
      ++count;
      }
      return finalPoints;
       */
    } else if (symbol.toUpperCase().equals("CNH")) {
      if (Math.abs(point.getX()) < Tolerance
              && Math.abs(point.getY()) < Tolerance
              && Math.abs(point.getZ()) < Tolerance) {
        // --- Point is in the origin
        return null;
      }

      boolean notOnZAxis = true;

      if (Math.abs(point.getX()) < Tolerance
              && Math.abs(point.getY()) < Tolerance) {
        // --- Point is on the Z axis
        notOnZAxis = false;
      }

      float theta = TWO_PI_FLOAT / axisOrder;
      List allP = new ArrayList();
      allP.add(point);
      p = null;

      // --- Rotate around principal axis

      if (notOnZAxis) {
        Point3fInterface p1 = new Point3f(0, 0, 0);
        Point3fInterface p2 = new Point3f(0, 0, 1);

        p = new Object[axisOrder - 1];
        //p[axisOrder - 1] = point;

        for (int i = 1, j = 0; i < axisOrder; i++, j++) {
          Point3fInterface testPoint = point.getInstance(point);
          Geometry3d.rotatePointAroundArbitraryAxis(testPoint, theta * i, p1, p2);
          p[j] = testPoint;
          allP.add(testPoint);
        }

      }

      // -- Reflect in Sigma-h

      if (Math.abs(point.getZ()) > Tolerance) {
        Point3fInterface testPoint = point.getInstance(point);
        testPoint.setZ(testPoint.getZ());
        allP.add(testPoint);
      }

      if (p != null) {
        for (int i = 0; i < p.length; i++) {
          Point3fInterface testPoint = (Point3fInterface) p[i];
          if (Math.abs(point.getZ()) < Tolerance) {
            continue;
          }
          Point3fInterface reflectedPoint = point.getInstance(testPoint);
          reflectedPoint.setZ(reflectedPoint.getZ());
          allP.add(reflectedPoint);

        }

      }

      // --- remove degenerated points

      return removeDegeneratePoints(point, p, allP);
    } // --- DN Symmetry transformations
    else if (symbol.toUpperCase().equals("DN")) {
      if (Math.abs(point.getX()) < Tolerance
              && Math.abs(point.getY()) < Tolerance
              && Math.abs(point.getZ()) < Tolerance) {
        // --- Point is in the origin
        return null;
      }

      boolean notOnZAxis = true;

      if (Math.abs(point.getX()) < Tolerance
              && Math.abs(point.getY()) < Tolerance) {
        // --- Point is on the Z axis
        notOnZAxis = false;
      }

      float theta = TWO_PI_FLOAT / axisOrder;
      Point3fInterface p1 = new Point3f(0, 0, 0);
      Point3fInterface p2 = new Point3f(0, 0, 1);
      List allP = new ArrayList();
      allP.add(point);
      p = null;

      if (notOnZAxis) {

        p = new Object[axisOrder - 1];
        //p[axisOrder - 1] = point;

        for (int i = 1, j = 0; i < axisOrder; i++, j++) {
          Point3fInterface testPoint = point.getInstance(point);
          Geometry3d.rotatePointAroundArbitraryAxis(testPoint,
                  theta * i, p1, p2);
          p[j] = testPoint;
          allP.add(testPoint);
        }
      }

      // Rotate around two-fold axis

      float finalTheta;

      p2.setZ(0);
      for (int j = 0; j < axisOrder; j++) {

        finalTheta = theta / 2.0f * j;
        p2.setX((float) Math.cos(finalTheta));
        p2.setY((float) Math.sin(finalTheta));

        Point3fInterface testPoint = point.getInstance(point);
        Geometry3d.rotatePointAroundArbitraryAxis(testPoint, PI_FLOAT,
                p1, p2);
        allP.add(testPoint);

        if (p != null) {
          for (int i = 0; i < p.length; i++) {
            Point3fInterface rotatedPoint = point.getInstance((Point3fInterface) p[i]);
            Geometry3d.rotatePointAroundArbitraryAxis(rotatedPoint,
                    PI_FLOAT, p1, p2);
            allP.add(rotatedPoint);
          }
        }
      }

      // --- remove degenerated points

      return removeDegeneratePoints(point, p, allP);
      
    } // --- DNH Symmetry transformations
    else if (symbol.toUpperCase().equals("DNH")) {
      if (Math.abs(point.getX()) < Tolerance
              && Math.abs(point.getY()) < Tolerance
              && Math.abs(point.getZ()) < Tolerance) {
        // --- Point is in the origin
        return null;
      }

      boolean notOnZAxis = true;

      if (Math.abs(point.getX()) < Tolerance
              && Math.abs(point.getY()) < Tolerance) {
        // --- Point is on the Z axis
        notOnZAxis = false;
      }

      float theta = TWO_PI_FLOAT / axisOrder;
      Point3fInterface p1 = new Point3f(0, 0, 0);
      Point3fInterface p2 = new Point3f(0, 0, 1);
      List allP = new ArrayList();
      allP.add(point);
      p = null;

      if (notOnZAxis) {

        p = new Object[axisOrder - 1];
        //p[axisOrder - 1] = point;

        for (int i = 1, j = 0; i < axisOrder; i++, j++) {
          Point3fInterface testPoint = point.getInstance(point);
          Geometry3d.rotatePointAroundArbitraryAxis(testPoint,
                  theta * i, p1, p2);
          p[j] = testPoint;
          allP.add(testPoint);
        }
      }

      // Rotate around two-fold axis

      float finalTheta;

      p2.setZ(0);
      for (int j = 0; j < axisOrder; j++) {

        finalTheta = theta / 2.0f * j;
        p2.setX((float) Math.cos(finalTheta));
        p2.setY((float) Math.sin(finalTheta));

        Point3fInterface testPoint = point.getInstance(point);
        Geometry3d.rotatePointAroundArbitraryAxis(testPoint, PI_FLOAT,
                p1, p2);
        allP.add(testPoint);

        if (p != null) {
          for (int i = 0; i < p.length; i++) {
            Point3fInterface rotatedPoint = point.getInstance((Point3fInterface) p[i]);
            Geometry3d.rotatePointAroundArbitraryAxis(rotatedPoint,
                    PI_FLOAT, p1, p2);
            allP.add(rotatedPoint);
          }
        }
      }

      // Reflect in Sigma-h

      if (Math.abs(point.getZ()) > Tolerance) {
        Point3fInterface testPoint = point.getInstance(point);
        testPoint.setZ(-testPoint.getZ());
        allP.add(testPoint);
      }

      if (p != null) {
        for (int i = 0; i < p.length; i++) {
          Point3fInterface reflectedPoint = point.getInstance((Point3fInterface) p[i]);
          if (Math.abs(reflectedPoint.getZ()) < Tolerance) {
            continue;
          }
          reflectedPoint.setZ(-reflectedPoint.getZ());
          allP.add(reflectedPoint);
        }
      }

      // --- remove degenerated points
      return removeDegeneratePoints(point, p, allP);

    } // --- DND Symmetry transformations
    else if (symbol.toUpperCase().equals("DND")) {
      if (Math.abs(point.getX()) < Tolerance
              && Math.abs(point.getY()) < Tolerance
              && Math.abs(point.getZ()) < Tolerance) {
        // --- Point is in the origin
        return null;
      }

      boolean notOnZAxis = true;

      if (Math.abs(point.getX()) < Tolerance
              && Math.abs(point.getY()) < Tolerance) {
        // --- Point is on the Z axis
        notOnZAxis = false;
      }

      float theta = TWO_PI_FLOAT / axisOrder;
      Point3fInterface p1 = new Point3f(0, 0, 0);
      Point3fInterface p2 = new Point3f(0, 0, 1);
      List allP = new ArrayList();
      allP.add(point);
      p = null;

      if (notOnZAxis) {

        p = new Object[axisOrder - 1];
        //p[axisOrder - 1] = point;

        for (int i = 1, j = 0; i < axisOrder; i++, j++) {
          Point3fInterface testPoint = point.getInstance(point);
          Geometry3d.rotatePointAroundArbitraryAxis(testPoint,
                  theta * i, p1, p2);
          p[j] = testPoint;
          allP.add(testPoint);
        }
      }

      // Rotate around two-fold axis

      float finalTheta;

      p2.setZ(0);
      for (int j = 0; j < axisOrder; j++) {

        finalTheta = theta / 2.0f * j;
        p2.setX((float) Math.cos(finalTheta));
        p2.setY((float) Math.sin(finalTheta));

        Point3fInterface testPoint = point.getInstance(point);
        Geometry3d.rotatePointAroundArbitraryAxis(testPoint, PI_FLOAT,
                p1, p2);
        allP.add(testPoint);

        if (p != null) {
          for (int i = 0; i < p.length; i++) {
            Point3fInterface rotatedPoint = point.getInstance((Point3fInterface) p[i]);
            Geometry3d.rotatePointAroundArbitraryAxis(rotatedPoint,
                    PI_FLOAT, p1, p2);
            allP.add(rotatedPoint);
          }
        }
      }

      // --- Reflect in mirror planes Sigma-d

      theta /= 2.0f;
      for (int j = 0; j < axisOrder; j++) {

        // --- Normals of Sigma(v) plane
        finalTheta = theta * j + theta / 2.0f + HALF_PI_FLOAT;
        float xn = (float) Math.cos(finalTheta);
        float yn = (float) Math.sin(finalTheta);

        if (Math.abs(point.getZ()) > Tolerance) {
          Point3fInterface testPoint = point.getInstance(point);
          float d = testPoint.getX() * xn + testPoint.getY() * yn;
          testPoint.setX(testPoint.getX() - 2 * d * xn);
          testPoint.setY(testPoint.getY() - 2 * d * yn);
          allP.add(testPoint);
        }

        // Now reflect points in planes
        if (p != null) {
          for (int i = 0; i < p.length; i++) {
            Point3fInterface testPoint = (Point3fInterface) p[i];
            // - Distance to the plane
            float d = testPoint.getX() * xn + testPoint.getY() * yn;
            if (Math.abs(d) < Tolerance) { // Point is on the plane
              continue;
            }

            // --- Reflect point

            Point3fInterface reflectedPoint = testPoint.getInstance(
                    testPoint);
            reflectedPoint.setX(reflectedPoint.getX() - 2 * d * xn);
            reflectedPoint.setY(reflectedPoint.getY() - 2 * d * yn);
            allP.add(reflectedPoint);
          }
        }
      }

      // --- remove degenerated points

      return removeDegeneratePoints(point, p, allP);

    }

    return null;
  }

  private static Object[] removeDegeneratePoints(Point3fInterface originalPoint, Object p[], List points) {
    removeDegeneratePoints(points);

    int degeneratedPoints = 0;
    for (int j = 0; j < points.size(); j++) {
      Point3fInterface testPoint = (Point3fInterface) points.get(j);
      if (testPoint == null) {
        ++degeneratedPoints;
        continue;
      }
      if (testPoint.distanceTo(originalPoint) < Tolerance) {
        points.set(j, null);
        ++degeneratedPoints;
        continue;
      }
      if (p != null) {
        for (int i = 0; i < p.length; i++) {
          Point3fInterface rotatedPoint = (Point3fInterface) p[i];
          if (rotatedPoint == null) {
            ++degeneratedPoints;
            continue;
          }
          if (testPoint.distanceTo(rotatedPoint) < Tolerance) {
            points.set(j, null);
            ++degeneratedPoints;
            break;
          }
        }
      }
    }

    if (degeneratedPoints == points.size()) {
      return p;
    }

    Object[] finalPoints;
    if (p != null) {
      finalPoints = new Object[p.length + points.size()
              - degeneratedPoints];
      for (int i = 0; i < p.length; i++) {
        finalPoints[i] = p[i];
      }
      int count = p.length;
      for (int j = 0; j < points.size(); j++) {
        Point3fInterface testPoint = (Point3fInterface) points.get(j);
        if (testPoint == null) {
          continue;
        }
        finalPoints[count] = testPoint;
        ++count;
      }

    } else {
      finalPoints = new Object[points.size() - degeneratedPoints];
      int count = 0;
      for (int j = 0; j < points.size(); j++) {
        Point3fInterface testPoint = (Point3fInterface) points.get(j);
        if (testPoint == null) {
          continue;
        }
        finalPoints[count] = testPoint;
        ++count;
      }

    }

    return finalPoints;

  }

  private static void removeDegeneratePoints(List points) {
    for (int i = 0; i < points.size() - 1; i++) {
      Point3fInterface Point = (Point3fInterface) points.get(i);
      if (Point == null) {
        continue;
      }
      for (int j = i + 1; j < points.size(); j++) {
        Point3fInterface testPoint = (Point3fInterface) points.get(j);
        if (testPoint == null) {
          continue;
        }
        if (Point.distanceTo(testPoint) < Tolerance) {
          points.set(j, null);
        }
      }
    }
  }
}
