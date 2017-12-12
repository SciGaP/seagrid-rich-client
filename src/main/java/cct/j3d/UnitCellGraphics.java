package cct.j3d;

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

import org.scijava.java3d.Appearance;
import org.scijava.java3d.GeometryArray;
import org.scijava.java3d.LineArray;
import org.scijava.java3d.LineAttributes;
import org.scijava.java3d.Node;
import org.scijava.java3d.QuadArray;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.TransparencyAttributes;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Point3f;

import cct.interfaces.GraphicsObjectInterface;

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
public class UnitCellGraphics {

   private String graphicsName = "Unit Cell";

   public UnitCellGraphics(String name) {
      graphicsName = name;
   }

   public void setGraphicsName(String name) {
      graphicsName = name;
   }

   public GraphicsObjectInterface getCellGraphicsObject(double[][] vecs) {
      float[][] latticeVectors = new float[3][3];
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            latticeVectors[i][j] = (float) vecs[i][j];
         }
      }
      return getCellGraphicsObject(latticeVectors);
   }

   public GraphicsObjectInterface getCellGraphicsObject(float[][] latticeVectors) {

      GraphicsObjectInterface graphics = new VerticesObject();
      graphics.setName(graphicsName);

      GraphicsObjectInterface cellFrame = new VerticesObject();
      cellFrame.setName("Lattice Frame");

      Shape3D shape3d = new Shape3D();
      shape3d.setCapability(Node.ALLOW_PARENT_READ);
      shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);

      VerticesObjectProperties voP = new VerticesObjectProperties();

      Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
      Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
      Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
      Color3f grey = new Color3f(0.5f, 0.5f, 0.5f);

      Appearance app = voP.getAppearence(red);

      LineAttributes la = new LineAttributes(2.0f, LineAttributes.PATTERN_SOLID, true);
      app.setLineAttributes(la);

      //TransparencyAttributes ta = new TransparencyAttributes();
      //ta.setTransparency(0.7f);
      //app.setTransparencyAttributes(ta);

      // --- Create frame cell

      LineArray frame = new LineArray(24, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

      // --- OX
      Point3f[] coordinates = new Point3f[2];
      coordinates[0] = new Point3f(0, 0, 0);
      coordinates[1] = new Point3f(latticeVectors[0][0], latticeVectors[0][1], latticeVectors[0][2]);
      frame.setCoordinates(0, coordinates);
      frame.setColor(0, red);
      frame.setColor(1, red);
      // --- OY
      coordinates = new Point3f[2];
      coordinates[0] = new Point3f(0, 0, 0);
      coordinates[1] = new Point3f(latticeVectors[1][0], latticeVectors[1][1], latticeVectors[1][2]);
      frame.setCoordinates(2, coordinates);
      frame.setColor(2, green);
      frame.setColor(3, green);
      // --- OZ
      coordinates = new Point3f[2];
      coordinates[0] = new Point3f(0, 0, 0);
      coordinates[1] = new Point3f(latticeVectors[2][0], latticeVectors[2][1], latticeVectors[2][2]);
      frame.setCoordinates(4, coordinates);
      frame.setColor(4, blue);
      frame.setColor(5, blue);

      int offset = 6;
      float x, y, z;

      // --- Parallel OZ
      for (int i = 0; i < 2; i++) {
         if (i == 0) {
            x = y = z = 0;
         }
         else {
            x = latticeVectors[0][0];
            y = latticeVectors[0][1];
            z = latticeVectors[0][2];
         }
         for (int j = 0; j < 2; j++) {
            if (i == 0 && j == 0) {
               continue;
            }

            if (j != 0) {
               x += latticeVectors[1][0];
               y += latticeVectors[1][1];
               z += latticeVectors[1][2];

            }
            coordinates = new Point3f[2];
            coordinates[0] = new Point3f(x, y, z);
            coordinates[1] = new Point3f(x + latticeVectors[2][0], y + latticeVectors[2][1], z + latticeVectors[2][2]);

            frame.setCoordinates(offset, coordinates);
            frame.setColor(offset, grey);
            frame.setColor(offset + 1, grey);
            offset += 2;
         }
      }

      // --- Parallel OY
      for (int i = 0; i < 2; i++) {
         if (i == 0) {
            x = y = z = 0;
         }
         else {
            x = latticeVectors[0][0];
            y = latticeVectors[0][1];
            z = latticeVectors[0][2];
         }
         for (int j = 0; j < 2; j++) {
            if (i == 0 && j == 0) {
               continue;
            }

            if (j != 0) {
               x += latticeVectors[2][0];
               y += latticeVectors[2][1];
               z += latticeVectors[2][2];

            }
            coordinates = new Point3f[2];
            coordinates[0] = new Point3f(x, y, z);
            coordinates[1] = new Point3f(x + latticeVectors[1][0], y + latticeVectors[1][1], z + latticeVectors[1][2]);

            frame.setCoordinates(offset, coordinates);
            frame.setColor(offset, grey);
            frame.setColor(offset + 1, grey);
            offset += 2;
         }
      }

      // --- Parallel OX
      for (int i = 0; i < 2; i++) {
         if (i == 0) {
            x = y = z = 0;
         }
         else {
            x = latticeVectors[1][0];
            y = latticeVectors[1][1];
            z = latticeVectors[1][2];
         }
         for (int j = 0; j < 2; j++) {
            if (i == 0 && j == 0) {
               continue;
            }

            if (j != 0) {
               x += latticeVectors[2][0];
               y += latticeVectors[2][1];
               z += latticeVectors[2][2];

            }
            coordinates = new Point3f[2];
            coordinates[0] = new Point3f(x, y, z);
            coordinates[1] = new Point3f(x + latticeVectors[0][0], y + latticeVectors[0][1], z + latticeVectors[0][2]);

            frame.setCoordinates(offset, coordinates);
            frame.setColor(offset, grey);
            frame.setColor(offset + 1, grey);
            offset += 2;
         }
      }

      shape3d.setAppearance(app);
      shape3d.setGeometry(frame);

      cellFrame.addGraphics("Cell Frame", shape3d);

      // --- Create quads...

      Color3f cyan = new Color3f(0.0f, 1.0f, 1.0f);
      Color3f magenta = new Color3f(1.0f, 0.0f, 1.0f);
      Color3f yellow = new Color3f(1.0f, 1.0f, 0.0f);

      GraphicsObjectInterface cellWalls = new VerticesObject();
      cellWalls.setName("Lattice faces");

      shape3d = new Shape3D();
      shape3d.setCapability(Node.ALLOW_PARENT_READ);
      shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);

      voP = new VerticesObjectProperties();

      app = voP.getAppearence(red);
      TransparencyAttributes ta = new TransparencyAttributes();
      ta.setTransparency(0.9f);
      ta.setTransparencyMode(TransparencyAttributes.BLENDED);
      app.setTransparencyAttributes(ta);

      QuadArray quadArray = new QuadArray(6 * 4, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

      float[] quadCoords = new float[12];

      // 1st plane XOY

      quadCoords[0] = 0;
      quadCoords[1] = 0;
      quadCoords[2] = 0;

      quadCoords[3] = latticeVectors[0][0];
      quadCoords[4] = latticeVectors[0][1];
      quadCoords[5] = latticeVectors[0][2];

      quadCoords[6] = latticeVectors[0][0] + latticeVectors[1][0];
      quadCoords[7] = latticeVectors[0][1] + latticeVectors[1][1];
      quadCoords[8] = latticeVectors[0][2] + latticeVectors[1][2];

      quadCoords[9] = latticeVectors[1][0];
      quadCoords[10] = latticeVectors[1][1];
      quadCoords[11] = latticeVectors[1][2];

      quadArray.setCoordinates(0, quadCoords);

      quadArray.setColor(0, yellow);
      quadArray.setColor(1, yellow);
      quadArray.setColor(2, yellow);
      quadArray.setColor(3, yellow);

      // 2st plane XOZ

      quadCoords = new float[12];

      quadCoords[0] = 0;
      quadCoords[1] = 0;
      quadCoords[2] = 0;

      quadCoords[3] = latticeVectors[0][0];
      quadCoords[4] = latticeVectors[0][1];
      quadCoords[5] = latticeVectors[0][2];

      quadCoords[6] = latticeVectors[0][0] + latticeVectors[2][0];
      quadCoords[7] = latticeVectors[0][1] + latticeVectors[2][1];
      quadCoords[8] = latticeVectors[0][2] + latticeVectors[2][2];

      quadCoords[9] = latticeVectors[2][0];
      quadCoords[10] = latticeVectors[2][1];
      quadCoords[11] = latticeVectors[2][2];

      quadArray.setCoordinates(4, quadCoords);

      quadArray.setColor(4, magenta);
      quadArray.setColor(5, magenta);
      quadArray.setColor(6, magenta);
      quadArray.setColor(7, magenta);

      // 3st plane YOZ

      quadCoords = new float[12];

      quadCoords[0] = 0;
      quadCoords[1] = 0;
      quadCoords[2] = 0;

      quadCoords[3] = latticeVectors[1][0];
      quadCoords[4] = latticeVectors[1][1];
      quadCoords[5] = latticeVectors[1][2];

      quadCoords[6] = latticeVectors[1][0] + latticeVectors[2][0];
      quadCoords[7] = latticeVectors[1][1] + latticeVectors[2][1];
      quadCoords[8] = latticeVectors[1][2] + latticeVectors[2][2];

      quadCoords[9] = latticeVectors[2][0];
      quadCoords[10] = latticeVectors[2][1];
      quadCoords[11] = latticeVectors[2][2];

      quadArray.setCoordinates(8, quadCoords);

      quadArray.setColor(8, cyan);
      quadArray.setColor(9, cyan);
      quadArray.setColor(10, cyan);
      quadArray.setColor(11, cyan);

      // 4th plane XOY upper

      quadCoords[0] = latticeVectors[2][0];
      quadCoords[1] = latticeVectors[2][1];
      quadCoords[2] = latticeVectors[2][2];

      quadCoords[3] = latticeVectors[2][0] + latticeVectors[0][0];
      quadCoords[4] = latticeVectors[2][1] + latticeVectors[0][1];
      quadCoords[5] = latticeVectors[2][2] + latticeVectors[0][2];

      quadCoords[6] = latticeVectors[2][0] + latticeVectors[0][0] + latticeVectors[1][0];
      quadCoords[7] = latticeVectors[2][1] + latticeVectors[0][1] + latticeVectors[1][1];
      quadCoords[8] = latticeVectors[2][2] + latticeVectors[0][2] + latticeVectors[1][2];

      quadCoords[9] = latticeVectors[2][0] + latticeVectors[1][0];
      quadCoords[10] = latticeVectors[2][1] + latticeVectors[1][1];
      quadCoords[11] = latticeVectors[2][2] + latticeVectors[1][2];

      quadArray.setCoordinates(12, quadCoords);

      quadArray.setColor(12, grey);
      quadArray.setColor(13, grey);
      quadArray.setColor(14, grey);
      quadArray.setColor(15, grey);

      // 5th plane XOZ upper

      quadCoords = new float[12];

      quadCoords[0] = latticeVectors[1][0];
      quadCoords[1] = latticeVectors[1][1];
      quadCoords[2] = latticeVectors[1][2];

      quadCoords[3] = latticeVectors[1][0] + latticeVectors[0][0];
      quadCoords[4] = latticeVectors[1][1] + latticeVectors[0][1];
      quadCoords[5] = latticeVectors[1][2] + latticeVectors[0][2];

      quadCoords[6] = latticeVectors[1][0] + latticeVectors[0][0] + latticeVectors[2][0];
      quadCoords[7] = latticeVectors[1][1] + latticeVectors[0][1] + latticeVectors[2][1];
      quadCoords[8] = latticeVectors[1][2] + latticeVectors[0][2] + latticeVectors[2][2];

      quadCoords[9] = latticeVectors[1][0] + latticeVectors[2][0];
      quadCoords[10] = latticeVectors[1][1] + latticeVectors[2][1];
      quadCoords[11] = latticeVectors[1][2] + latticeVectors[2][2];

      quadArray.setCoordinates(16, quadCoords);

      quadArray.setColor(16, grey);
      quadArray.setColor(17, grey);
      quadArray.setColor(18, grey);
      quadArray.setColor(19, grey);

      // 6st plane YOZ upper

      quadCoords = new float[12];

      quadCoords[0] = latticeVectors[0][0];
      quadCoords[1] = latticeVectors[0][1];
      quadCoords[2] = latticeVectors[0][2];

      quadCoords[3] = latticeVectors[0][0] + latticeVectors[1][0];
      quadCoords[4] = latticeVectors[0][1] + latticeVectors[1][1];
      quadCoords[5] = latticeVectors[0][2] + latticeVectors[1][2];

      quadCoords[6] = latticeVectors[0][0] + latticeVectors[1][0] + latticeVectors[2][0];
      quadCoords[7] = latticeVectors[0][1] + latticeVectors[1][1] + latticeVectors[2][1];
      quadCoords[8] = latticeVectors[0][2] + latticeVectors[1][2] + latticeVectors[2][2];

      quadCoords[9] = latticeVectors[0][0] + latticeVectors[2][0];
      quadCoords[10] = latticeVectors[0][1] + latticeVectors[2][1];
      quadCoords[11] = latticeVectors[0][2] + latticeVectors[2][2];

      quadArray.setCoordinates(20, quadCoords);

      quadArray.setColor(20, grey);
      quadArray.setColor(21, grey);
      quadArray.setColor(22, grey);
      quadArray.setColor(23, grey);

      shape3d.setGeometry(quadArray);
      shape3d.setAppearance(app);

      cellWalls.addGraphics("VASP Lattice faces", shape3d);
      cellWalls.setVisible(false);

      graphics.addGraphics(cellFrame);
      graphics.addGraphics(cellWalls);

      return graphics;
   }
}
