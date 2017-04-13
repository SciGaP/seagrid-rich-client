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

package cct.tools;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.StringTokenizer;

import cct.vecmath.Point3f;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 * Syntax Common to All OOGL File Formats
 * Most OOGL object file formats are free-format ASCII -- any amount of white space
 * (blanks, tabs, newlines) may appear between tokens (numbers, key words, etc.).
 *
 * Line breaks are almost always insignificant, with a couple of exceptions as noted.
 *
 * Comments begin with # and continue to the end of the line; they're allowed anywhere a newline is.
 *
 * Binary formats are also defined for several objects; See section Binary format, and the individual object descriptions.
 *
 * Typical OOGL objects begin with a key word designating object type,
 * possibly with modifiers indicating presence of color information etc.
 * In some formats the key word is optional, for compatibility with file formats defined elsewhere. Object
 * type is then determined by guessing from the file suffix (if any) or from the data itself.
 *
 * Key words are case sensitive. Some have optional prefix letters indicating presence of color or other data;
 * in this case the order of prefixes is significant, e.g. CNMESH is meaningful but NCMESH is invalid.
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OOGL {

   int NVertices = 0, NFaces = 0, NEdges = 0;
   int Ndim;
   boolean yesSpaceDimension = false;
   boolean yesNormals = false;
   boolean yesColors = false;
   boolean yesTextures = false;
   Point3f Vertices[] = null;
   Color colors[] = null;
   Point3f normals[] = null;
   Face faces[] = null;

   public OOGL() {
   }

   public int getNFaces() {
      return this.NFaces;
   }

   public int getNVertices() {
      return this.NVertices;
   }

   public Point3f[] getVertices() {
      return Vertices;
   }

   public Color[] getColors() {
      return colors;
   }

   public Point3f[] getNormals() {
      return normals;
   }

   public Face[] getFaces() {
      return faces;
   }

   public void parseOFF(String filename, int fileType) throws Exception {
      String line;
      BufferedReader in = null;

      if (fileType == 0) { // Read from file
         try {
            in = new BufferedReader(new FileReader(filename));
         }
         catch (java.io.FileNotFoundException e) {
            throw new Exception("Error parsing file " + filename + ": " +
                                e.getMessage());
         }
      }
      else if (fileType == 1) { // Read from String
         in = new BufferedReader(new StringReader(filename));
      }
      else {
         throw new Exception("Unknown file type");
      }

      int vertexComponents = 3;
      int vertexNumbers = 0;

      try {
         //BufferedReader in = new BufferedReader(new FileReader(filename));

         while ( (line = in.readLine()) != null) {
            // [ST][C][N][4][n]OFF	# Header keyword
            StringTokenizer st = new StringTokenizer(line, " \t");
            String header = st.nextToken();
            if (!header.contains("OFF")) {
               throw new Exception("Error parsing file " + filename +
                                   ": header does not have OFF string");
            }
            if (header.contains("n")) {
               yesSpaceDimension = true;
            }

            if (header.contains("N")) {
               yesNormals = true;
               vertexNumbers += 3;
            }

            if (header.contains("C")) {
               yesColors = true;
               vertexNumbers += 4;
            }

            if (header.contains("ST")) {
               yesTextures = true;
               vertexNumbers += 1;
            }

            // --- [Ndim] # Space dimension of vertices, present only if nOFF (if any)
            if (yesSpaceDimension) {
               line = in.readLine();
               st = new StringTokenizer(line, " \t");
               try {
                  Ndim = Integer.parseInt(st.nextToken());
               }
               catch (Exception ex) {
                  throw new Exception("Error parsing file " + filename +
                                      ": wrong number for Space dimension: " + line);
               }

               if (header.contains("4OFF")) {
                  vertexComponents = 4;
               }
               else if (header.contains("nOFF")) {
                  vertexComponents = Ndim;
               }
               else if (header.contains("4nOFF")) {
                  vertexComponents = Ndim + 1;
               }
               vertexNumbers += vertexComponents;
            }

            // --- NVertices  NFaces  NEdges   # NEdges not used or checked
            line = in.readLine();

            st = new StringTokenizer(line, " \t");
            if (st.countTokens() < 3) {
               throw new Exception("Error parsing file " + filename +
                                   ": expecting three numbers for NVertices, NFaces, and NEdges. Got: " +
                                   line);
            }

            try {
               NVertices = Integer.parseInt(st.nextToken());
            }
            catch (Exception ex) {
               throw new Exception("Error parsing file " + filename +
                                   ": wrong number for NVertices: " + line);
            }

            try {
               NFaces = Integer.parseInt(st.nextToken());
            }
            catch (Exception ex) {
               throw new Exception("Error parsing file " + filename +
                                   ": wrong number for NFaces: " + line);
            }

            try {
               NEdges = Integer.parseInt(st.nextToken());
            }
            catch (Exception ex) {
               //throw new Exception("Error parsing file " + filename +
               //                    ": wrong number for NFaces: " + line);
               System.err.println("Error parsing file " + filename +
                                  ": wrong number for NFaces: " + line +
                                  " Error ignored...");
            }

            // x[0]  y[0]  z[0]	# Vertices, possibly with normals,
            //                      # colors, and/or texture coordinates, in that order,
            //                      # if the prefixes N, C, ST
            //                      # are present.
            //                      # If 4OFF, each vertex has 4 components,
            //                      # including a final homogeneous component.
            //                      # If nOFF, each vertex has Ndim components.
            //                      # If 4nOFF, each vertex has Ndim+1 components.

            Vertices = new Point3f[NVertices];

            if (yesColors) {
               colors = new Color[NVertices];
            }
            if (yesNormals) {
               normals = new Point3f[NVertices];
            }

            for (int i = 0; i < NVertices; i++) {
               line = in.readLine();

               st = new StringTokenizer(line, " \t");
               // --- Error check here
               try {
                  Vertices[i] = new Point3f(Float.parseFloat(st.nextToken()),
                                             Float.parseFloat(st.nextToken()),
                                             Float.parseFloat(st.nextToken()));
               }
               catch (Exception ex) {
                  throw new Exception("Error parsing file " + filename +
                                      ": wrong number(s) for vertex coordinate(s): " +
                                      line);

               }

               // --- Parsing normals (if any )
               // --- Error check here
               if (yesNormals) {
                  try {
                     normals[i] = new Point3f(Float.parseFloat(st.nextToken()),
                                               Float.parseFloat(st.nextToken()),
                                               Float.parseFloat(st.nextToken()));
                  }
                  catch (Exception ex) {
                     throw new Exception("Error parsing file " + filename +
                                         ": wrong number(s) for vertex coordinate(s): " +
                                         line);

                  }

               }

               // --- Parsing colors (if any )
               // --- Error check here
               if (yesColors) {
                  try {
                     colors[i] = new Color(Float.parseFloat(st.nextToken()),
                                           Float.parseFloat(st.nextToken()),
                                           Float.parseFloat(st.nextToken()),
                                           Float.parseFloat(st.nextToken()));
                  }
                  catch (Exception ex) {
                     throw new Exception("Error parsing file " + filename +
                                         ": wrong number(s) for vertex color: " +
                                         line);

                  }
               }

               // --- Ignore texture for now !!!
            }

            // # Faces
            //                # Nv = # vertices on this face
            //                # v[0] ... v[Nv-1]: vertex indices
            //                #		in range 0..NVertices-1
            // Nv  v[0] v[1] ... v[Nv-1]  colorspec
            // ...
            //                # colorspec continues past v[Nv-1]
            //                # to end-of-line; may be 0 to 4 numbers
            //                # nothing: default
            //                # integer: colormap index
            //                # 3 or 4 integers: RGB[A] values 0..255
            //                # 3 or 4 floats: RGB[A] values 0..1

            faces = new Face[NFaces];

            for (int i = 0; i < NFaces; i++) {
               line = in.readLine();

               st = new StringTokenizer(line, " \t");
               int nV = 0;

               try {
                  nV = Integer.parseInt(st.nextToken());
               }
               catch (Exception ex) {
                  throw new Exception("Error parsing file " + filename +
                                      ": wrong number for # vertices: " + line);
               }
               int v[] = new int[nV];
               for (int j = 0; j < nV; j++) {
                  try {
                     v[j] = Integer.parseInt(st.nextToken());
                  }
                  catch (Exception ex) {
                     throw new Exception("Error parsing file " + filename +
                                         ": wrong number for vertex indices: " + line);
                  }
                  faces[i] = new Face(v);
               }

            }

         }

         if (fileType == 0) {
            in.close();
         }
      }
      catch (Exception e) {
         throw new Exception("Error parsing file " + filename +
                             " : " + e.getMessage());
      }

   }

   public static void main(String[] args) {
      OOGL oogl = new OOGL();
      try {
         oogl.parseOFF("C:/ftp/IvanRostov/pcm/tesserae.ogl", 0);
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}
