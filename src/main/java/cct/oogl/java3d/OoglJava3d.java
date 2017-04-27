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

package cct.oogl.java3d;

import java.util.logging.Logger;

import org.scijava.java3d.Appearance;
import org.scijava.java3d.GeometryArray;
import org.scijava.java3d.Material;
import org.scijava.java3d.Node;
import org.scijava.java3d.PolygonAttributes;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.TriangleArray;
import org.scijava.vecmath.Color4f;
import org.scijava.vecmath.Point3f;
import org.scijava.vecmath.Vector3f;

import cct.interfaces.GraphicsObjectInterface;
import cct.j3d.VerticesObject;
import cct.oogl.Color;
import cct.oogl.Face;
import cct.oogl.OOGL;

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
public class OoglJava3d {
  static final Logger logger = Logger.getLogger(OoglJava3d.class.getCanonicalName());

   private OoglJava3d() {
   }

   public static GraphicsObjectInterface getGraphicsObject(OOGL oogl_object) {
      GraphicsObjectInterface graph = null;

      GraphicsObjectInterface graphics = new VerticesObject();
      graphics.setName("OOGL Object");

      Shape3D shape3d = new Shape3D();
      shape3d.setCapability(Node.ALLOW_PARENT_READ);
      shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      shape3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      shape3d.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);

      TriangleArray surface = getSurface(oogl_object);

      Appearance app = new Appearance();

      //ColoringAttributes ca = new ColoringAttributes();
      //ca.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
      //app.setColoringAttributes(ca);

      // --- Polygon attributes

      PolygonAttributes pa = new PolygonAttributes();
      pa.setCullFace(PolygonAttributes.CULL_NONE);
      pa.setBackFaceNormalFlip(true);

      pa.setCapability(PolygonAttributes.ALLOW_MODE_READ);
      pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
      pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_READ);
      pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

      app.setPolygonAttributes(pa);

      // ---  Set up the material properties

      Material material = new Material();
      material.setLightingEnable(true);
      material.setColorTarget(Material.DIFFUSE);

      // --- Set Shininess
      material.setShininess(15.0f);

      // --- Diffuse Color
      material.setDiffuseColor(0.666f, 0.666f, 0.666f);

      // --- Ambient Color
      material.setAmbientColor(0.2f, 0.2f, 0.2f);

      // --- Emissive Color
      material.setEmissiveColor(0.0f, 0.0f, 0.0f);

      // --- Specular Color
      material.setSpecularColor(1.0f, 1.0f, 1.0f);


      material.setCapability(Material.ALLOW_COMPONENT_READ);
      material.setCapability(Material.ALLOW_COMPONENT_WRITE);

      app.setMaterial(material);

      shape3d.setGeometry(surface);
      shape3d.setAppearance(app);

      graph = new VerticesObject();
      graph.addGraphics("OOGL Object", shape3d);
      graphics.addGraphics(graph);

      return graphics;
   }

   public static TriangleArray getSurface(OOGL oogl) {
      int nTriangles = 0;
      Face[] faces = oogl.getFaces();
      for (int i = 0; i < oogl.getNFaces(); i++) {
         if (faces[i].getNVertices() == 3) {
            ++nTriangles;
         }
         else {
            nTriangles += faces[i].getNVertices() - 2;
         }
      }
      logger.info("# Triangles: " + nTriangles);
      TriangleArray geometry = new TriangleArray(nTriangles * 3,
                                                 GeometryArray.COORDINATES |
                                                 GeometryArray.COLOR_4 | GeometryArray.NORMALS);
      Point3f[] coordinates = new Point3f[nTriangles * 3];
      Color4f[] Colors = new Color4f[nTriangles * 3];

      cct.vecmath.Point3f[] vertices = oogl.getVertices();
      Color[] colors = oogl.getColors();
      cct.vecmath.Point3f oogl_normals[] = null;
      if (oogl.hasNormals()) {
         oogl_normals = oogl.getNormals();
      }

      Vector3f[] normals = new Vector3f[nTriangles * 3];

      int count = 0;
      for (int i = 0; i < oogl.getNFaces(); i++) {
         if (faces[i].getNVertices() == 3) {
            coordinates[3 * count] = new Point3f(vertices[faces[i].v[0]].getX(),
                                                 vertices[faces[i].v[0]].getY(),
                                                 vertices[faces[i].v[0]].getZ());
            Colors[3 *
                count] = new Color4f(colors[faces[i].v[0]].r,
                                     colors[faces[i].v[0]].g,
                                     colors[faces[i].v[0]].b,
                                     colors[faces[i].v[0]].a);

            coordinates[3 * count +
                1] = new Point3f(vertices[faces[i].v[1]].getX(),
                                 vertices[faces[i].v[1]].getY(),
                                 vertices[faces[i].v[1]].getZ());
            Colors[3 * count +
                1] = new Color4f(colors[faces[i].v[1]].r, colors[faces[i].v[1]].g,
                                 colors[faces[i].v[1]].b, colors[faces[i].v[1]].a);

            coordinates[3 * count +
                2] = new Point3f(vertices[faces[i].v[2]].getX(),
                                 vertices[faces[i].v[2]].getY(),
                                 vertices[faces[i].v[2]].getZ());
            Colors[3 * count +
                2] = new Color4f(colors[faces[i].v[2]].r, colors[faces[i].v[2]].g,
                                 colors[faces[i].v[2]].b, colors[faces[i].v[2]].a);
            if (oogl.hasNormals()) {
               normals[3 * count] = new Vector3f(oogl_normals[faces[i].v[0]].getX(),
                                                 oogl_normals[faces[i].v[0]].getY(),
                                                 oogl_normals[faces[i].v[0]].getZ());
               normals[3 * count + 1] = new Vector3f(oogl_normals[faces[i].v[1]].getX(),
                   oogl_normals[faces[i].v[1]].getY(),
                   oogl_normals[faces[i].v[1]].getZ());
               normals[3 * count + 2] = new Vector3f(oogl_normals[faces[i].v[2]].getX(),
                   oogl_normals[faces[i].v[2]].getY(),
                   oogl_normals[faces[i].v[2]].getZ());
            }
            else {
               normals[3 * count] = getNormal(coordinates[3 * count],
                                              coordinates[3 * count + 1],
                                              coordinates[3 * count + 2]);
               normals[3 * count + 1] = new Vector3f(normals[3 * count]);
               normals[3 * count + 2] = new Vector3f(normals[3 * count]);
            }
            ++count;
         }
         else {
            for (int j = 0; j <= faces[i].getNVertices() - 3; j++, count++) {
               coordinates[3 * count] = new Point3f(vertices[faces[i].v[0]].getX(),
                   vertices[faces[i].v[0]].getY(),
                   vertices[faces[i].v[0]].getZ());
               Colors[3 *
                   count] = new Color4f(colors[faces[i].v[0]].r,
                                        colors[faces[i].v[0]].g,
                                        colors[faces[i].v[0]].b,
                                        colors[faces[i].v[0]].a);

               coordinates[3 * count +
                   1] = new Point3f(vertices[faces[i].v[j + 1]].getX(),
                                    vertices[faces[i].v[j + 1]].getY(),
                                    vertices[faces[i].v[j + 1]].getZ());
               Colors[3 * count +
                   1] = new Color4f(colors[faces[i].v[j + 1]].r,
                                    colors[faces[i].v[j + 1]].g,
                                    colors[faces[i].v[j + 1]].b,
                                    colors[faces[i].v[j + 1]].a);

               coordinates[3 * count +
                   2] = new Point3f(vertices[faces[i].v[j + 2]].getX(),
                                    vertices[faces[i].v[j + 2]].getY(),
                                    vertices[faces[i].v[j + 2]].getZ());
               Colors[3 * count +
                   2] = new Color4f(colors[faces[i].v[j + 2]].r,
                                    colors[faces[i].v[j + 2]].g,
                                    colors[faces[i].v[j + 2]].b,
                                    colors[faces[i].v[j + 2]].a);

               if (oogl.hasNormals()) {
                  normals[3 * count] = new Vector3f(oogl_normals[faces[i].v[0]].getX(),
                      oogl_normals[faces[i].v[0]].getY(),
                      oogl_normals[faces[i].v[0]].getZ());
                  normals[3 * count +
                      1] = new Vector3f(oogl_normals[faces[i].v[j + 1]].getX(),
                                        oogl_normals[faces[i].v[j + 1]].getY(),
                                        oogl_normals[faces[i].v[j + 1]].getZ());
                  normals[3 * count +
                      2] = new Vector3f(oogl_normals[faces[i].v[j + 2]].getX(),
                                        oogl_normals[faces[i].v[j + 2]].getY(),
                                        oogl_normals[faces[i].v[j + 2]].getZ());
               }
               else {
                  normals[3 * count] = getNormal(coordinates[3 * count],
                                                 coordinates[3 * count + 1],
                                                 coordinates[3 * count + 2]);
                  normals[3 * count + 1] = new Vector3f(normals[3 * count]);
                  normals[3 * count + 2] = new Vector3f(normals[3 * count]);
               }

            }
         }
      }

      geometry.setCoordinates(0, coordinates);
      geometry.setColors(0, Colors);
      geometry.setNormals(0, normals);

      return geometry;
   }

   /**
    * Normal points toward the side from which the vertices p1, p2, and p3
    * appear in counterclockwise order.
    * @param p1 Point3f
    * @param p2 Point3f
    * @param p3 Point3f
    * @return Point3f - normal
    */
   public static Vector3f getNormal(Point3f p1, Point3f p2, Point3f p3) {
      Vector3f crossVector = new Vector3f();
      Point3f v1 = new Point3f(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
      Point3f v2 = new Point3f(p3.x - p2.x, p3.y - p2.y, p3.z - p2.z);
      crossVector.x = v1.y * v2.z - v1.z * v2.y;
      crossVector.y = v1.z * v2.x - v1.x * v2.z;
      crossVector.z = v1.x * v2.y - v1.y * v2.x;

      crossVector.normalize();
      crossVector.x = -crossVector.x;
      crossVector.y = -crossVector.y;
      crossVector.z = -crossVector.z;

      return crossVector;
   }

}
