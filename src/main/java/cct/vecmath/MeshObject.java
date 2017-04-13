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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cct.interfaces.Point3fInterface;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class MeshObject {

   public static int GENERAL_MESH_OBJECT = 0;
   public static int TRIANGLES_MESH_OBJECT = 1;

   List<Point3fInterface> Vertices = null;
   List<FaceIndices> faceIndices = null;
   List<Point3fInterface> normalVectors = null;
   List<Color> Colors = null;

   int nVertices = 0;
   float Opacity = 1; // Opaque...

   int objectType = GENERAL_MESH_OBJECT;

   public MeshObject() {
   }

   public int getObjectType() {
      return objectType;
   }

   public void setOpacity(float opacity) {
      if (opacity < 0) {
         Opacity = 0;
      }
      else if (opacity > 1) {
         Opacity = 1;
      }
      else {
         Opacity = opacity;
      }
   }

   public float getOpacity() {
      return Opacity;
   }

   public float getTransparency() {
      return 1.0f - Opacity;
   }

   public void setTransparency(float transparency) {
      if (transparency < 0) {
         Opacity = 1;
      }
      else if (transparency > 1) {
         Opacity = 0;
      }
      else {
         Opacity = 1.0f - transparency;
      }
   }

   /**
    * Sets MeshObject made of triangles (nVertices/3)
    * @param nVertices int
    * @param coordinates float[] - array with vertex coordinates of a size nVertices*3
    * @param face_indices int[]
    * @param normals float[]
    * @param colors float[]
    * @throws Exception
    */
   public void setMeshObjectOfTriangles(int n_vertices, float[] coordinates, int[] face_indices, float[] normals, float[] colors) throws
       Exception {

      nVertices = n_vertices;
      int nTriangles = nVertices / 3;

      Vertices = new ArrayList<Point3fInterface> (nVertices);
      faceIndices = new ArrayList<FaceIndices> (nTriangles);
      normalVectors = new ArrayList<Point3fInterface> (nVertices);
      Colors = new ArrayList<Color> (nVertices);

      objectType = TRIANGLES_MESH_OBJECT;

      for (int i = 0; i < nVertices; i++) {
         Point3fInterface vertex = new Point3f(coordinates[i * 3], coordinates[i * 3 + 1], coordinates[i * 3 + 2]);
         Vertices.add(vertex);

         Point3fInterface normal = new Point3f(normals[3 * i], normals[3 * i + 1], normals[3 * i + 2]);
         normalVectors.add(normal);

         Color color = new Color(colors[3 * i], colors[3 * i + 1], colors[3 * i + 2]);
         Colors.add(color);
      }

      for (int i = 0; i < nTriangles; i++) {
         FaceIndices face = new FaceIndices(face_indices[i * 3], face_indices[i * 3 + 1], face_indices[i * 3 + 2]);
         faceIndices.add(face);
      }
   }

   public Point3fInterface[] getVerticesAsArray() {
      Point3fInterface[] vertices = new Point3fInterface[nVertices];
      return Vertices.toArray(vertices);
   }

   public Color[] getColorsAsArray() {
      Color[] colors = new Color[nVertices];
      return Colors.toArray(colors);
   }

   public List<FaceIndices> getFaceIndices() {
      return faceIndices;
   }

   public List<Point3fInterface> getNormals() {
      return normalVectors;
   }
}
