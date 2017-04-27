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

package cct.gaussian.java3d;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.scijava.java3d.Appearance;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.GeometryArray;
import org.scijava.java3d.LineArray;
import org.scijava.java3d.Node;
import org.scijava.java3d.PointArray;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.java3d.TriangleArray;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Color4f;
import org.scijava.vecmath.Point3f;
import org.scijava.vecmath.Vector3f;

import cct.gaussian.GaussianCube;
import cct.interfaces.GraphicsObjectInterface;
import cct.interfaces.ImplicitFunctionInterface;
import cct.j3d.ColorRangeScheme;
import cct.j3d.USER_DATA_FLAG;
import cct.j3d.UserData;
import cct.j3d.VerticesObject;
import cct.j3d.VerticesObjectProperties;
import cct.math.polyg.java3d.j3dPolygonizer;
import cct.poly.NORMAL;
import cct.poly.Polygonizer;
import cct.poly.TRIANGLE;
import cct.poly.VERTEX;

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
public class GaussianJava3dFactory {

  static Color3f blackColor = new Color3f(0.0f, 0.0f, 0.0f);
  static Color3f whiteColor = new Color3f(1.0f, 1.0f, 1.0f);
  static Color3f redColor = new Color3f(1.0f, 0.0f, 0.0f);
  static Color3f greenColor = new Color3f(0.0f, 1.0f, 0.0f);
  static Color3f blueColor = new Color3f(0.0f, 0.0f, 1.0f);

  private static int isosurfaceSet = 0;
  static final Logger logger = Logger.getLogger(GaussianJava3dFactory.class.getCanonicalName());

  private GaussianJava3dFactory() {
  }

  public static void main(String[] args) {
    GaussianJava3dFactory gaussianjava3dfactory = new GaussianJava3dFactory();
  }

  public static GraphicsObjectInterface getGraphicsObject(GaussianCube gCube, int n, float delta, boolean useReverse) {

    GraphicsObjectInterface graph2;

    GraphicsObjectInterface graphics = new VerticesObject();
    graphics.setName(gCube.getCubeDescription());

    VerticesObject vObject = new VerticesObject();
    vObject.setName("Isovalue: " + String.valueOf(delta));
    Shape3D shape3d = new Shape3D();
    ++isosurfaceSet;

    String value = "";
    try {
      value = (gCube.getCubeDescription() + "_" + String.valueOf(delta)).replaceAll("[ ]", "_").replaceAll("[.]", "_");
    }
    catch (Exception ex) {
    }

    UserData.setUserData(shape3d, UserData.NODE_NAME, "ISOSURFACE_" + String.format("%03d_", isosurfaceSet) + value);
    //shape3d.setName("Isovalue: " + String.valueOf(delta)); // --- >1.4 java3d feature
    shape3d.setCapability(Node.ALLOW_PARENT_READ);
    shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
    shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);

    TriangleArray surface = null;
    if (true) {
      surface = getSurface2(gCube, n, delta);
    }
    else {
      surface = getSurface(gCube, n, delta);
    }
    shape3d.setGeometry(surface);

    VerticesObjectProperties voP = new VerticesObjectProperties();

    Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
    Appearance app = voP.getAppearence(red);

    shape3d.setAppearance(app);

    vObject.addGraphics(graphics.getNGraphicsElements() + " Red Isosurface", shape3d);

    graph2 = vObject;
    graphics.addGraphics(graph2);

    // --- Negative isovalue

    if (useReverse && Math.abs(delta) > 0.000001) {
      graph2 = new VerticesObject();
      graph2.setName("Isovalue: " + ( -delta));
      shape3d = new Shape3D();

      value = "";
      try {
        value = (gCube.getCubeDescription() + "_" + String.valueOf( -delta)).replaceAll("[ ]", "_").replaceAll("[.]", "_");
      }
      catch (Exception ex) {
      }

      UserData.setUserData(shape3d, UserData.NODE_NAME, "ISOSURFACE_" + String.format("%03d_", isosurfaceSet) + value);

      //shape3d.setName("Isovalue: " + ( -delta)); // --- >1.4 java3d feature
      shape3d.setCapability(Node.ALLOW_PARENT_READ); // java3d >= 1.4
      shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);

      if (true) {
        surface = getSurface2(gCube, n, -delta);
      }
      else {
        surface = getSurface(gCube, n, -delta);
      }
      shape3d.setGeometry(surface);

      voP = new VerticesObjectProperties();

      Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
      app = voP.getAppearence(blue);

      shape3d.setAppearance(app);
      graph2.addGraphics(graphics.getNGraphicsElements() + " Blue Isosurface", shape3d);

      graphics.addGraphics(graph2);
    }

    /*
           graph2 = new VerticesObject();
           graph2.setName("Grid Box");
           shape3d = new Shape3D();
           shape3d.setCapability(Shape3D.ALLOW_PARENT_READ); // java3d >= 1.4
           shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
           shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
           shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
           shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
           shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
           shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);

           app = new Appearance();
           PointAttributes pa = new PointAttributes(3,true);
           app.setPointAttributes(pa);

           shape3d.setGeometry(getGridAsPoints(gCube));
           shape3d.setAppearance(app);
           graph2.addGraphics("Grid Box", shape3d);

           graphics.addGraphics(graph2);
     */

    return graphics;
  }

  /**
   * Creates two surfaces for values delta and -delta and returns them as a Shape3D
   * @param gCube GaussianCube
   * @param n int
   * @param delta float
   * @return Shape3D
   */
  public static Shape3D getSymmetricSurfaces(GaussianCube gCube, int n,
                                             float delta) {
    Shape3D shape3d = new Shape3D();
    TriangleArray surface;
    surface = getSurface(gCube, n, delta);
    shape3d.setGeometry(surface);
    surface = getSurface(gCube, n, -delta);
    shape3d.addGeometry(surface);
    return shape3d;
  }

  public static PointArray getGridAsPoints(GaussianCube gCube) {
    float[] origin = gCube.getDataOrigin();
    int[] voxels = gCube.getNumberOfVoxels();
    float axisVectors[][] = gCube.getAxisVectors();
    int nPoints = voxels[0] * voxels[1] * voxels[2];

    PointArray points = new PointArray(nPoints,
                                       GeometryArray.COORDINATES |
                                       GeometryArray.COLOR_3);

    points.setCapability(GeometryArray.ALLOW_COLOR_READ);
    points.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
    points.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    points.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);

    int count = 0;

    for (int i = 0; i < voxels[0]; i++) {
      float x = origin[0] + axisVectors[0][0] * i;
      for (int j = 0; j < voxels[1]; j++) {
        float y = origin[1] + axisVectors[1][1] * j;
        for (int k = 0; k < voxels[2]; k++, count++) {
          float z = origin[2] + axisVectors[2][2] * k;
          points.setCoordinate(count, new Point3f(x, y, z));
          points.setColor(count, whiteColor);

        }
      }
    }
    return points;
  }

  public static LineArray getGridAsLines(GaussianCube gCube) {
    float[] origin = gCube.getDataOrigin();
    int[] voxels = gCube.getNumberOfVoxels();
    float axisVectors[][] = gCube.getAxisVectors();
    int nPoints = 2 * (voxels[1] * voxels[2] + voxels[0] * voxels[2] +
                       voxels[0] * voxels[1]);

    LineArray box = new LineArray(nPoints,
                                  GeometryArray.COORDINATES | GeometryArray.COLOR_3);
    Point3f[] coordinates = new Point3f[nPoints];

    int count = 0;

    // -- Creates lines parallel to X Axis

    float x_end = origin[0] + axisVectors[0][0] * (voxels[0] - 1);
    for (int i = 0; i < voxels[1]; i++) {
      float y = origin[1] + axisVectors[1][1] * i;
      for (int j = 0; j < voxels[2]; j++) {
        float z = origin[2] + axisVectors[2][2] * j;
        coordinates[count] = new Point3f(origin[0], y, z);
        box.setColor(count, redColor);
        ++count;
        coordinates[count] = new Point3f(x_end, y, z);
        box.setColor(count, redColor);
        ++count;
      }
    }

    // -- Creates lines parallel to Y Axis

    float y_end = origin[1] + axisVectors[1][1] * (voxels[1] - 1);
    for (int i = 0; i < voxels[0]; i++) {
      float x = origin[0] + axisVectors[0][0] * i;
      for (int j = 0; j < voxels[2]; j++) {
        float z = origin[2] + axisVectors[2][2] * j;
        coordinates[count] = new Point3f(x, origin[1], z);
        box.setColor(count, greenColor);
        ++count;
        coordinates[count] = new Point3f(x, y_end, z);
        box.setColor(count, greenColor);
        ++count;
      }
    }

    // -- Creates lines parallel to Z Axis

    float z_end = origin[2] + axisVectors[2][2] * (voxels[2] - 1);
    for (int i = 0; i < voxels[0]; i++) {
      float x = origin[0] + axisVectors[0][0] * i;
      for (int j = 0; j < voxels[1]; j++) {
        float y = origin[1] + axisVectors[1][1] * j;
        coordinates[count] = new Point3f(x, y, origin[2]);
        box.setColor(count, blueColor);
        ++count;
        coordinates[count] = new Point3f(x, y, z_end);
        box.setColor(count, blueColor);
        ++count;
      }
    }

    box.setCoordinates(0, coordinates);

    return box;
  }

  /**
   * Creates surface as a TriangleArray
   * @param gCube GaussianCube
   * @param n int
   * @param delta float
   * @return TriangleArray
   */
  public static TriangleArray getSurface(GaussianCube gCube, int n,
                                         float delta) {

    java.awt.Color color;
    if (delta >= 0) {
      color = java.awt.Color.RED;
    }
    else {
      color = java.awt.Color.BLUE;
    }

    float size[] = {
        gCube.getAxisVectors()[0][0], gCube.getAxisVectors()[1][1],
        gCube.getAxisVectors()[2][2]};
    int bounds[] = {
        gCube.getNumberOfVoxels()[0] / 2 - 3,
        gCube.getNumberOfVoxels()[1] / 2 - 3,
        gCube.getNumberOfVoxels()[2] / 2 - 3};

    float xOrigin = gCube.getDataOrigin()[0] + (gCube.getAxisVectors()[0][0] * gCube.getNumberOfVoxels()[0]) / 2;
    float yOrigin = gCube.getDataOrigin()[1] + (gCube.getAxisVectors()[1][1] * gCube.getNumberOfVoxels()[1]) / 2;
    float zOrigin = gCube.getDataOrigin()[2] + (gCube.getAxisVectors()[2][2] * gCube.getNumberOfVoxels()[2]) / 2;

    boolean dotet = true;

    ImplicitFunctionInterface function = new VolumetricData(gCube.getVolumetricData(n), gCube.getDataOrigin(),
        gCube.getNumberOfVoxels(), gCube.getAxisVectors(), delta);

    Polygonizer polygonizer = new Polygonizer(function, size, bounds);

    //polygonizer.march(dotet, xOrigin, yOrigin, zOrigin);
    float xyz_step[] = {
        gCube.getAxisVectors()[0][0], gCube.getAxisVectors()[1][1],
        gCube.getAxisVectors()[2][2]};
    polygonizer.march2(dotet, gCube.getDataOrigin(), gCube.getNumberOfVoxels(), xyz_step);

    logger.info("# triangles: " + polygonizer.no_triangles());
    if (polygonizer.no_triangles() < 1) {
      return null;
    }

    TriangleArray tetra2 = new TriangleArray(polygonizer.no_triangles() * 3,
                                             GeometryArray.COORDINATES |
                                             GeometryArray.NORMALS |
                                             GeometryArray.COLOR_3);
    Point3f[] coordinates = new Point3f[polygonizer.no_triangles() * 3];
    Vector3f[] normals = new Vector3f[polygonizer.no_triangles() * 3];
    Color3f[] colors = new Color3f[polygonizer.no_triangles() * 3];

    int count = 0;
    for (int i = 0; i < polygonizer.no_triangles(); ++i) {
      TRIANGLE t = polygonizer.get_triangle(i);
      NORMAL n0 = polygonizer.get_normal(t.v0);
      VERTEX v0 = polygonizer.get_vertex(t.v0);

      NORMAL n1 = polygonizer.get_normal(t.v1);
      VERTEX v1 = polygonizer.get_vertex(t.v1);

      NORMAL n2 = polygonizer.get_normal(t.v2);
      VERTEX v2 = polygonizer.get_vertex(t.v2);

      coordinates[3 * i] = new Point3f(v0.x, v0.y, v0.z);
      coordinates[3 * i + 1] = new Point3f(v1.x, v1.y, v1.z);
      coordinates[3 * i + 2] = new Point3f(v2.x, v2.y, v2.z);

      float factor = 1.0f;
      if (delta > 0) {
        factor = -factor;
      }

      normals[3 *
          i] = new Vector3f(factor * n0.x, factor * n0.y, factor * n0.z);
      normals[3 * i +
          1] = new Vector3f(factor * n1.x, factor * n1.y, factor * n1.z);
      normals[3 * i +
          2] = new Vector3f(factor * n2.x, factor * n2.y, factor * n2.z);

      if (v0.rgb[1] != 0) {
        colors[3 * i] = new Color3f(v0.rgb);
        colors[3 * i + 1] = new Color3f(v0.rgb);
        colors[3 * i + 2] = new Color3f(v0.rgb);
      }
      else {
        colors[3 * i] = new Color3f(color);
        colors[3 * i + 1] = new Color3f(color);
        colors[3 * i + 2] = new Color3f(color);
      }

      count += 3;
      //logger.info("Triangle: "+i+" v1, v2, v3 "+t.v0+" "+t.v1+" "+t.v2);
    }

    tetra2.setCoordinates(0, coordinates);
    tetra2.setNormals(0, normals);
    tetra2.setColors(0, colors);

    return tetra2;
  }

  public static float[] calcPropertyOnSurface(Object obj, GaussianCube gCube, int n) {

    if (! (obj instanceof BranchGroup)) {
      System.err.println("Warning: calcPropertyOnSurface: Uknown object " + obj.getClass().getCanonicalName() + " Ignored...");
      return new float[] {
          0, 0};
    }
    BranchGroup bg = (BranchGroup) obj;

    ImplicitFunctionInterface fun = new VolumetricData(gCube.getVolumetricData(n), gCube.getDataOrigin(),
        gCube.getNumberOfVoxels(), gCube.getAxisVectors(), 0);
    return calcPropertyOnSurface(bg, fun);
  }

  /**
   *
   * @param bg BranchGroup
   * @param fun ImplicitFunctionInterface
   * @return float[] - two-dimensional array, 0 - min, 1 - max
   */
  public static float[] calcPropertyOnSurface(BranchGroup bg, ImplicitFunctionInterface fun) {
    boolean minMaxSet = false;
    float fMin = 0;
    float fMax = 0;

    Enumeration enumer = bg.getAllChildren();
    while (enumer.hasMoreElements()) {
      Object bgChild = enumer.nextElement();
      if (bgChild instanceof Shape3D) {
        Shape3D shape3D = (Shape3D) bgChild;
        boolean shMinMaxSet = false;
        float shMin = 0, shMax = 0;
        Enumeration geoms = shape3D.getAllGeometries();
        while (geoms.hasMoreElements()) {
          Object geometry = geoms.nextElement();

          // --- TriangleArray
          if (geometry instanceof TriangleArray) {
            TriangleArray ta = (TriangleArray) geometry;
            int nVertices = ta.getValidVertexCount();
            Point3f[] coords = new Point3f[nVertices];
            for (int j = 0; j < nVertices; j++) {
              coords[j] = new Point3f();
            }

            float[] values = new float[nVertices];
            ta.getCoordinates(0, coords);
            float min = 0, max = 0;
            for (int j = 0; j < nVertices; j++) {
              values[j] = (float) fun.eval(coords[j].x, coords[j].y, coords[j].z);

              if (j == 0) {
                min = values[j];
                max = values[j];
              }
              else if (min > values[j]) {
                min = values[j];
              }
              else if (max < values[j]) {
                max = values[j];
              }

            }

            // -- Storing user data in TriangleArray
            Map<USER_DATA_FLAG, Object> userData = new HashMap<USER_DATA_FLAG, Object> ();
            userData.put(USER_DATA_FLAG.FUN_VALUES_AT_VERTICES, values);
            userData.put(USER_DATA_FLAG.MIN_FUN_VALUE_AT_VERTICES, new Float(min));
            userData.put(USER_DATA_FLAG.MAX_FUN_VALUE_AT_VERTICES, new Float(max));
            ta.setUserData(userData);

            // --- Setting min & max for shape3d
            if (!shMinMaxSet) {
              shMin = min;
              shMax = max;
              shMinMaxSet = true;
            }
            else {
              if (shMin > min) {
                shMin = min;
              }
              if (shMax < max) {
                fMax = max;
              }
            }

            // -- Setting "global" min & max

            if (!minMaxSet) {
              fMin = min;
              fMax = max;
              minMaxSet = true;
            }
            else {
              if (fMin > min) {
                fMin = min;
              }
              if (fMax < max) {
                fMax = max;
              }
            }

          }
          else { // --- end of TriangleArray
            System.err.println(
                "Warning: calcPropertyOnSurface(BranchGroup, ImplicitFunctionInterface): ignoring geometry type " +
                geometry.getClass().getCanonicalName());
          }
        } // --- End of while

        Map<USER_DATA_FLAG, Object> userData = new HashMap<USER_DATA_FLAG, Object> ();
        userData.put(USER_DATA_FLAG.MIN_FUN_VALUE_AT_VERTICES, new Float(shMin));
        userData.put(USER_DATA_FLAG.MAX_FUN_VALUE_AT_VERTICES, new Float(shMax));
        shape3D.setUserData(userData);

      }
    }

    return new float[] {
        fMin, fMax};

  }

  /**
   *
   * @param graphics GraphicsObjectInterface
   * @param gCube GaussianCube
   * @param n int - Cube number
   * @return float[] - two dimensional array, index 0 - min value, 1 - max value
   */
  public static float[] calcPropertyOnSurface(GraphicsObjectInterface graphics, GaussianCube gCube, int n) {

    ImplicitFunctionInterface fun = new VolumetricData(gCube.getVolumetricData(n), gCube.getDataOrigin(),
        gCube.getNumberOfVoxels(), gCube.getAxisVectors(), 0);

    boolean minMaxSet = false;
    float fMin = 0;
    float fMax = 0;

    List ges = graphics.getShape3DElements();
    for (int i = 0; i < ges.size(); i++) {
      Object obj = ges.get(i);
      if (obj instanceof BranchGroup) {
        BranchGroup bg = (BranchGroup) obj;
        float[] minMax = calcPropertyOnSurface(bg, fun);
        if (!minMaxSet) {
          fMin = minMax[0];
          fMax = minMax[1];
          minMaxSet = true;
        }
        else {
          if (fMin > minMax[0]) {
            fMin = minMax[0];
          }
          if (fMax < minMax[1]) {
            fMax = minMax[1];
          }
        }

      }
      else {
        System.err.println("Warning: calcPropertyOnSurface: ignoring graphics object " + obj.getClass().getCanonicalName());
      }
    }

    return new float[] {
        fMin, fMax};
  }

  public static void mapSurface(GraphicsObjectInterface graphics, ColorRangeScheme crange) {

    List ges = graphics.getShape3DElements();
    for (int i = 0; i < ges.size(); i++) {
      Object obj = ges.get(i);
      if (obj instanceof BranchGroup) {
        BranchGroup bg = (BranchGroup) obj;
        Enumeration enumer = bg.getAllChildren();

        Node parent = bg.getParent();
        if (parent != null && enumer.hasMoreElements()) {
          bg.detach();
        }

        while (enumer.hasMoreElements()) {
          Object bgChild = enumer.nextElement();
          if (bgChild instanceof Shape3D) {
            Shape3D shape3D = (Shape3D) bgChild;
            mapSurface(shape3D, crange);
          }
        }

        if (parent != null) {
          if (parent instanceof TransformGroup) {
            ( (TransformGroup) parent).addChild(bg);
          }
          else if (parent instanceof BranchGroup) {
            ( (BranchGroup) parent).addChild(bg);
          }
        }

      }
      else {
        System.err.println("Warning: mapSurface: ignoring graphics object " + obj.getClass().getCanonicalName());
      }
    }

  }

  public static void mapSurface(Shape3D shape3D, ColorRangeScheme crange) {

    Map<USER_DATA_FLAG, Object> shape3dUserData = null;
    if (shape3D.getUserData() == null) {
      shape3dUserData = new HashMap<USER_DATA_FLAG, Object> ();
    }
    else {
      shape3dUserData = (Map<USER_DATA_FLAG, Object>) shape3D.getUserData();
    }
    shape3dUserData.put(USER_DATA_FLAG.COLOR_RANGE_SCHEME_OBJECT, crange);

    Enumeration geoms = shape3D.getAllGeometries();
    while (geoms.hasMoreElements()) {
      Object geometry = geoms.nextElement();
      if (geometry instanceof TriangleArray) {
        TriangleArray ta = (TriangleArray) geometry;

        Object uObj = ta.getUserData();

        if (uObj == null) {
          System.err.println("Warning: mapSurface: No user data (values on surface). Ignoring...");
          continue;
        }

        float[] values = null;
        try {
          Map<USER_DATA_FLAG, Object> userData = (Map<USER_DATA_FLAG, Object>) uObj;
          values = (float[]) userData.get(USER_DATA_FLAG.FUN_VALUES_AT_VERTICES);
          List<Color3f> palette = crange.getColor3fPalette();
          userData.put(USER_DATA_FLAG.COLOR_PALETTE_ARRAY, palette);

          userData.put(USER_DATA_FLAG.MIN_CLIP_FUN_VALUE_AT_VERTICES, new Float(crange.getClipMin()));
          userData.put(USER_DATA_FLAG.MAX_CLIP_FUN_VALUE_AT_VERTICES, new Float(crange.getClipMax()));
          userData.put(USER_DATA_FLAG.COLOR_RANGE_SCHEME_OBJECT, crange);
        }
        catch (Exception ex) {
          System.err.println("Warning: mapSurface: Cannot get user data (values on surface): " + ex.getMessage() +
                             " Ignoring...");
          continue;
        }

        int nVertices = ta.getValidVertexCount();

        if (nVertices != values.length) {
          System.err.println("Warning: mapSurface: nVertices != values.length : " + nVertices + " != " +
                             values.length + " Ignoring...");
          continue;
        }

        int vertexFormat = ta.getVertexFormat();
        boolean useColor4f = (vertexFormat & GeometryArray.COLOR_4) == GeometryArray.COLOR_4;

        Color3f[] colors = null;
        Color4f[] colors4f = null;

        if (useColor4f) {
          colors4f = new Color4f[nVertices];
          Color4f color4f = new Color4f();
          for (int j = 0; j < nVertices; j++) {
            ta.getColor(j, color4f);
            colors4f[j] = new Color4f(color4f);
            crange.getColor4f(values[j], colors4f[j]);
          }
        }
        else {
          colors = new Color3f[nVertices];
          for (int j = 0; j < nVertices; j++) {
            colors[j] = new Color3f();
            crange.getColor3f(values[j], colors[j]);
          }
        }

        if (useColor4f) {
          ta.setColors(0, colors4f);
        }
        else {
          ta.setColors(0, colors);
        }

      }
      else {
        System.err.println("Warning: mapSurface: ignoring geometry type " + geometry.getClass().getCanonicalName());
      }
    }

  }

  public static TriangleArray getSurface2(GaussianCube gCube, int n, float delta) {

    int bounds[] = {
        gCube.getNumberOfVoxels()[0] - 1,
        gCube.getNumberOfVoxels()[1] - 1,
        gCube.getNumberOfVoxels()[2] - 1};

    boolean dotet = false;

    ImplicitFunctionInterface fun = new VolumetricData(gCube.getVolumetricData(n), gCube.getDataOrigin(),
        gCube.getNumberOfVoxels(), gCube.getAxisVectors(), delta);

    j3dPolygonizer polygonizer = new j3dPolygonizer(gCube.getDataOrigin(), bounds, gCube.getAxisVectors()[0][0], delta, fun);
    polygonizer.setMarchingTatrahedrons(dotet);

    polygonizer.marchCubes();
    TriangleArray tetra2 = polygonizer.getTriangleArray();

    logger.info("# triangles: " + polygonizer.noTriangles());
    if (polygonizer.noTriangles() < 1) {
      return null;
    }

    // --- Release memory
    polygonizer.dispose();

    return tetra2;
  }

}
