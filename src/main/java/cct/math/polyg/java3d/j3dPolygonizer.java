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

package cct.math.polyg.java3d;

import java.util.logging.Logger;

import org.scijava.java3d.Appearance;
import org.scijava.java3d.GeometryArray;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.TriangleArray;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Color4f;
import org.scijava.vecmath.Point3f;
import org.scijava.vecmath.Vector3f;

import cct.interfaces.ImplicitFunctionInterface;
import cct.math.polyg.GLvector;
import cct.math.polyg.Polygonizer;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class j3dPolygonizer
    extends Polygonizer {

  boolean marchingTetras = false;
  static final Logger logger = Logger.getLogger(j3dPolygonizer.class.getCanonicalName());

  public j3dPolygonizer() {
    super();
  }

  public j3dPolygonizer(float origin[], int dataSetSize[], float stepSize,
                        float isovalue, ImplicitFunctionInterface fun) {
    super(origin, dataSetSize, stepSize, isovalue, fun);
  }

  public void setMarchingTatrahedrons(boolean enable) {
    marchingTetras = enable;
  }

  public Shape3D getSurface() {

    this.marchCubes();
    TriangleArray tetra2 = this.getTriangleArray();

    Shape3D shape3d = new Shape3D(tetra2);

    shape3d.setAppearance(new Appearance());

    return shape3d;

  }

  public void marchCubes() {

    if (marchingTetras) {
      super.marchingTetrahedrons();
    }
    else {
      super.marchingCubes2();
    }
    int nVertices = this.Vertices.size();

    if (nVertices < 1) {
      logger.info("No vertices");
    }
  }

  public TriangleArray getTriangleArray() {

    boolean useColor4f = false;

    int nVertices = this.Vertices.size();

    if (nVertices < 1) {
      logger.info("No vertices");
      return null;
    }

    java.awt.Color color;

    if (this.fTargetValue > 0) {
      color = java.awt.Color.RED;
    }
    else if (this.fTargetValue < 0) {
      color = java.awt.Color.BLUE;
    }
    else {
      color = java.awt.Color.GRAY;
    }

    logger.info("N of vertices " + nVertices);

    TriangleArray tetra2 = null;

    if (useColor4f) {
      tetra2 = new TriangleArray(nVertices, GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.COLOR_4);
    }
    else {
      tetra2 = new TriangleArray(nVertices, GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.COLOR_3);
    }

    tetra2.setCapability(GeometryArray.ALLOW_COLOR_READ);
    tetra2.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
    tetra2.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    tetra2.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
    tetra2.setCapability(GeometryArray.ALLOW_COUNT_READ);
    tetra2.setCapability(GeometryArray.ALLOW_COUNT_WRITE);
    tetra2.setCapability(GeometryArray.ALLOW_FORMAT_READ);
    tetra2.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
    tetra2.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_WRITE);
    tetra2.setCapability(GeometryArray.ALLOW_NORMAL_READ);
    tetra2.setCapability(GeometryArray.ALLOW_NORMAL_WRITE);

    tetra2.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
    tetra2.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);

    Point3f[] coordinates = new Point3f[nVertices];
    Vector3f[] normals = new Vector3f[nVertices];
    Color4f[] colors_4f = null;
    Color3f[] colors_3f = null;

    if (useColor4f) {
      colors_4f = new Color4f[nVertices];
    }
    else {
      colors_3f = new Color3f[nVertices];
    }

    for (int i = 0; i < nVertices; ++i) {
      GLvector coord = (GLvector) Vertices.get(i);
      coordinates[i] = new Point3f(coord.fX, coord.fY, coord.fZ);

      float factor = 1.0f;
      //if (delta > 0) {
      //  factor = -factor;
      // }

      GLvector normal = (GLvector) Normals.get(i);
      normals[i] = new Vector3f(factor * normal.fX, factor * normal.fY, factor * normal.fZ);
    }

    if (useColor4f) {
      for (int i = 0; i < nVertices; ++i) {
        colors_4f[i] = new Color4f(color);
      }
    }
    else {
      for (int i = 0; i < nVertices; ++i) {
        colors_3f[i] = new Color3f(color);
      }
    }

    tetra2.setCoordinates(0, coordinates);
    tetra2.setNormals(0, normals);
    if (useColor4f) {
      tetra2.setColors(0, colors_4f);
    }
    else {
      tetra2.setColors(0, colors_3f);
    }

    return tetra2;
  }

  public static void main(String[] args) {
    j3dPolygonizer j3dpolygonizer = new j3dPolygonizer();
  }
}
