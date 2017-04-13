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
package cct.oogl;

import cct.vecmath.Point3d;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.StringTokenizer;
import cct.vecmath.Point3f;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;

/**
 * <p>
 * Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>
 * Description: Computational Chemistry Toolkit</p>
 * Syntax Common to All OOGL File Formats Most OOGL object file formats are free-format ASCII -- any amount of white
 * space (blanks, tabs, newlines) may appear between tokens (numbers, key words, etc.).
 *
 * Line breaks are almost always insignificant, with a couple of exceptions as noted.
 *
 * Comments begin with # and continue to the end of the line; they're allowed anywhere a newline is.
 *
 * Binary formats are also defined for several objects; See section Binary format, and the individual object
 * descriptions.
 *
 * Typical OOGL objects begin with a key word designating object type, possibly with modifiers indicating presence of
 * color information etc. In some formats the key word is optional, for compatibility with file formats defined
 * elsewhere. Object type is then determined by guessing from the file suffix (if any) or from the data itself.
 *
 * Key words are case sensitive. Some have optional prefix letters indicating presence of color or other data; in this
 * case the order of prefixes is significant, e.g. CNMESH is meaningful but NCMESH is invalid.
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
enum PARSE_STATUS {
  READING_COMMENT, LOOKING_FOR_HEADER, LOOKING_FOR_SPACE_DIM,
  LOOKING_FOR_NFACES, LOOKING_FOR_NVERTICES, LOOKING_FOR_NEDGES,
  LOOKING_FOR_VERTICES, LOOKING_FOR_NORMALS, LOOKING_FOR_COLORS,
  LOOKING_FOR_FACES, LOOKING_FOR_FACE_COLORS
}

public class OOGL {

  int NVertices = 0, NFaces = 0, NEdges = 0;
  int Ndim;
  boolean yesSpaceDimension = false;
  boolean yesNormals = false;
  boolean yesColors = false;
  boolean yesTextures = false;
  Point3f Vertices[] = null;
  Point3d Vertices3d[] = null;
  Color colors[] = null;
  Point3f normals[] = null;
  Point3d normals3d[] = null;
  Face faces[] = null;

  int vertexComponents = 3;
  int vertexNumbers = 0;

  public OOGL() {
  }

  public OOGL(Point3d Vertices3d[], Point3d normals3d[], List<Face> faces) {
    this.Vertices3d = Vertices3d;
    this.normals3d = normals3d;
    this.faces = new Face[faces.size()];
    for (int i = 0; i < faces.size(); i++) {
      this.faces[i] = faces.get(i);
    }
  }

  public boolean hasNormals() {
    return yesNormals;
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
      } catch (java.io.FileNotFoundException e) {
        throw new Exception("Error parsing file " + filename + ": "
            + e.getMessage());
      }
    } else if (fileType == 1) { // Read from String
      in = new BufferedReader(new StringReader(filename));
    } else {
      throw new Exception("Unknown file type");
    }

    vertexComponents = 3;
    vertexNumbers = 0;

    try {
      //BufferedReader in = new BufferedReader(new FileReader(filename));

      while ((line = in.readLine()) != null) {
        // [ST][C][N][4][n]OFF	# Header keyword

        line = line.trim();
        if (line.length() < 1) {
          continue;
        }

        StringTokenizer st = new StringTokenizer(line, " \t");
        String header = st.nextToken();

        if (header.startsWith("#")) {
          continue;
        }

        if (!header.contains("OFF")) {
          throw new Exception("Error parsing file " + filename
              + ": header does not have OFF string");
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
          } catch (Exception ex) {
            throw new Exception("Error parsing file " + filename
                + ": wrong number for Space dimension: " + line);
          }

          if (header.contains("4OFF")) {
            vertexComponents = 4;
          } else if (header.contains("nOFF")) {
            vertexComponents = Ndim;
          } else if (header.contains("4nOFF")) {
            vertexComponents = Ndim + 1;
          }
          vertexNumbers += vertexComponents;
        }

        // --- NVertices  NFaces  NEdges   # NEdges not used or checked
        line = in.readLine();

        st = new StringTokenizer(line, " \t");
        if (st.countTokens() < 3) {
          throw new Exception("Error parsing file " + filename
              + ": expecting three numbers for NVertices, NFaces, and NEdges. Got: "
              + line);
        }

        try {
          NVertices = Integer.parseInt(st.nextToken());
        } catch (Exception ex) {
          throw new Exception("Error parsing file " + filename
              + ": wrong number for NVertices: " + line);
        }

        try {
          NFaces = Integer.parseInt(st.nextToken());
        } catch (Exception ex) {
          throw new Exception("Error parsing file " + filename
              + ": wrong number for NFaces: " + line);
        }

        try {
          NEdges = Integer.parseInt(st.nextToken());
        } catch (Exception ex) {
          //throw new Exception("Error parsing file " + filename +
          //                    ": wrong number for NFaces: " + line);
          System.err.println("Error parsing file " + filename
              + ": wrong number for NFaces: " + line
              + " Error ignored...");
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
          } catch (Exception ex) {
            throw new Exception("Error parsing file " + filename
                + ": wrong number(s) for vertex coordinate(s): "
                + line);

          }

          // --- Parsing normals (if any )
          // --- Error check here
          if (yesNormals) {
            try {
              normals[i] = new Point3f(Float.parseFloat(st.nextToken()),
                  Float.parseFloat(st.nextToken()),
                  Float.parseFloat(st.nextToken()));
            } catch (Exception ex) {
              throw new Exception("Error parsing file " + filename
                  + ": wrong number(s) for vertex coordinate(s): "
                  + line);

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
            } catch (Exception ex) {
              throw new Exception("Error parsing file " + filename
                  + ": wrong number(s) for vertex color: "
                  + line);

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
          } catch (Exception ex) {
            throw new Exception("Error parsing file " + filename
                + ": wrong number for # vertices: " + line);
          }
          int v[] = new int[nV];
          for (int j = 0; j < nV; j++) {
            try {
              v[j] = Integer.parseInt(st.nextToken());
            } catch (Exception ex) {
              throw new Exception("Error parsing file " + filename
                  + ": wrong number for vertex indices: " + line);
            }
            faces[i] = new Face(v);
          }

        }

      }

      if (fileType == 0) {
        in.close();
      }
    } catch (Exception e) {
      throw new Exception("Error parsing file " + filename
          + " : " + e.getMessage());
    }

  }

  public void parseOFF(String inputfile) throws Exception {

    PARSE_STATUS status = PARSE_STATUS.LOOKING_FOR_HEADER;

    try {
      // Create the tokenizer to read from a file
      FileReader rd = new FileReader(inputfile);
      StreamTokenizer st = new StreamTokenizer(rd);

      // Prepare the tokenizer for Java-style tokenizing rules
      //st.parseNumbers();
      st.wordChars('1', '9');
      st.wordChars('0', '0');
      st.wordChars('_', '_');
      st.wordChars('$', '$');
      st.wordChars('.', '.');

      st.eolIsSignificant(true);

      // If whitespace is not to be discarded, make this call
      //st.ordinaryChars(0, ' ');
      st.whitespaceChars('=', '=');
      //st.ordinaryChar('$');

      // These calls caused comments to be discarded
      st.slashSlashComments(false);
      st.slashStarComments(false);

      boolean reading_comment = false;
      boolean finished_reading_vertices = false;
      boolean finished_reading_faces = false;
      boolean finished_reading_file = false;
      int vertex_index = 0;
      int normal_index = 0;
      int color_index = 0;
      int vertices_count = 0;
      int faces_count = -1;
      int face_index = 0;
      int n_faces = 0;
      int v[] = null;
      Color face_color = null;

      // Parse the file
      int token = st.nextToken();
      while (token != StreamTokenizer.TT_EOF) {

        if (token != StreamTokenizer.TT_EOL && reading_comment) {
          token = st.nextToken();
          continue;
        }

        switch (token) {

          case StreamTokenizer.TT_EOL:

            // End of line character found
            if (reading_comment) {
              reading_comment = false;
              break;
            } else if (status == PARSE_STATUS.LOOKING_FOR_COLORS) {
              if (color_index == 2) {
                throw new Exception(
                    "Number of color values for vertex should be 1, 3, or 4, got "
                    + color_index);
              } else if (color_index > 0) {
                color_index = 0; // Reset
                adjustColor(colors[vertices_count - 1]);
                colors[vertices_count - 1].a = 1.0f;
              }

              if (finished_reading_vertices) {
                status = PARSE_STATUS.LOOKING_FOR_FACES;
              } else {
                status = PARSE_STATUS.LOOKING_FOR_VERTICES;
              }
            } else if (status == PARSE_STATUS.LOOKING_FOR_FACE_COLORS) {
              if (color_index == 2) {
                throw new Exception(
                    "Number of color values for the face should be 1, 3, or 4, got "
                    + color_index);
              } else if (color_index > 0) {
                color_index = 0; // Reset
                adjustColor(face_color);
                face_color.a = 1.0f;
                faces[faces_count - 1].setColor(face_color);
              }

              if (finished_reading_faces) {
                finished_reading_file = true;
              } else {
                status = PARSE_STATUS.LOOKING_FOR_FACES;
              }
            }

            break;

          case StreamTokenizer.TT_NUMBER:

            // A number was found; the value is in nval
            double num = st.nval;

            if (status == PARSE_STATUS.LOOKING_FOR_HEADER
                || // No header...
                status == PARSE_STATUS.LOOKING_FOR_NVERTICES) {
              NVertices = (int) num;
              if (NVertices < 3) {
                throw new Exception("Number of vertices should >2, got "
                    + NVertices);
              }

              Vertices = new Point3f[NVertices];

              if (yesColors) {
                colors = new Color[NVertices];
              }
              if (yesNormals) {
                normals = new Point3f[NVertices];
              }

              status = PARSE_STATUS.LOOKING_FOR_NFACES;
            } else if (status == PARSE_STATUS.LOOKING_FOR_NFACES) {
              NFaces = (int) num;
              status = PARSE_STATUS.LOOKING_FOR_NEDGES;
            } else if (status == PARSE_STATUS.LOOKING_FOR_NEDGES) {
              NEdges = (int) num;
              status = PARSE_STATUS.LOOKING_FOR_VERTICES;
            } else if (status == PARSE_STATUS.LOOKING_FOR_SPACE_DIM) {
              Ndim = (int) num;
              status = PARSE_STATUS.LOOKING_FOR_NVERTICES;
            } else if (status == PARSE_STATUS.LOOKING_FOR_VERTICES
                && !(finished_reading_vertices)) {
              // --- !!! REDo here !!!!
              if (vertex_index == 0) {
                Vertices[vertices_count] = new Point3f();
                Vertices[vertices_count].setX((float) num);
              } else if (vertex_index == 1) {
                Vertices[vertices_count].setY((float) num);
              } else if (vertex_index == 2) {
                Vertices[vertices_count].setZ((float) num);
              }

              boolean reading_vertex = true;
              ++vertex_index;
              if (vertex_index == 3) { // !!!!!
                reading_vertex = false;
                vertex_index = 0; // Reset
                ++vertices_count;
              }

              if (vertices_count == NVertices) {
                finished_reading_vertices = true;
              } else if (reading_vertex) {
                break;
              }

              if (yesNormals) {
                status = PARSE_STATUS.LOOKING_FOR_NORMALS;
              } else if (yesColors) {
                status = PARSE_STATUS.LOOKING_FOR_COLORS;
              } else if (finished_reading_vertices) {
                status = PARSE_STATUS.LOOKING_FOR_FACES;
              }

            } else if (status == PARSE_STATUS.LOOKING_FOR_NORMALS) {

              if (normal_index == 0) {
                normals[vertices_count - 1] = new Point3f();
                normals[vertices_count - 1].setX((float) num);
              } else if (normal_index == 1) {
                normals[vertices_count - 1].setY((float) num);
              } else if (normal_index == 2) {
                normals[vertices_count - 1].setZ((float) num);
              }

              ++normal_index;
              if (normal_index == 3) {
                normal_index = 0; // Reset
              }

              if (yesColors) {
                status = PARSE_STATUS.LOOKING_FOR_COLORS;
              } else if (finished_reading_vertices) {
                status = PARSE_STATUS.LOOKING_FOR_FACES;
              } else {
                status = PARSE_STATUS.LOOKING_FOR_VERTICES;
              }
            } else if (status == PARSE_STATUS.LOOKING_FOR_COLORS) {

              if (color_index == 0) {
                colors[vertices_count - 1] = new Color();
                colors[vertices_count - 1].r = (float) num;
              } else if (color_index == 1) {
                colors[vertices_count - 1].g = (float) num;
              } else if (color_index == 2) {
                colors[vertices_count - 1].b = (float) num;
              } else if (color_index == 3) {
                colors[vertices_count - 1].a = (float) num;
              }

              ++color_index;
              if (color_index == 4) {
                reading_comment = true;
                color_index = 0; // Reset
                adjustColor(colors[vertices_count - 1]);
                if (finished_reading_vertices) {
                  status = PARSE_STATUS.LOOKING_FOR_FACES;
                } else {
                  status = PARSE_STATUS.LOOKING_FOR_VERTICES;
                }
              }
            } else if ((status == PARSE_STATUS.LOOKING_FOR_VERTICES
                && finished_reading_vertices)
                || status == PARSE_STATUS.LOOKING_FOR_FACES) { // Start to read faces
              if (faces_count == -1) {
                faces_count = 0;
                faces = new Face[NFaces];
                face_index = 0;
                n_faces = -1;
              }

              if (n_faces == -1) {
                n_faces = (int) st.nval;
                if (n_faces < 3) {
                  throw new Exception("Number of faces should be > 2, got"
                      + n_faces);
                }
                v = new int[n_faces];
                face_index = 0;
              } else {
                v[face_index] = (int) st.nval;
                if (v[face_index] < 0 || v[face_index] >= NVertices) {
                  throw new Exception("Vertex number should be >= 0 and <="
                      + (NVertices - 1) + " got" + v[face_index]);
                }
                ++face_index;
              }

              if (face_index == n_faces) {
                faces[faces_count] = new Face(v);
                n_faces = -1;
                ++faces_count;
              } else {
                break;
              }

              if (faces_count == this.NFaces) {
                finished_reading_faces = false;
              }

              color_index = 0;
              status = PARSE_STATUS.LOOKING_FOR_FACE_COLORS;
            } else if (status == PARSE_STATUS.LOOKING_FOR_FACE_COLORS) {
              if (color_index == 0) {
                face_color = new Color();
                face_color.r = (float) num;
              } else if (color_index == 1) {
                face_color.g = (float) num;
              } else if (color_index == 2) {
                face_color.b = (float) num;
              } else if (color_index == 3) {
                face_color.a = (float) num;
              }

              ++color_index;
              if (color_index == 4) {
                reading_comment = true;
                color_index = 0; // Reset
                adjustColor(face_color);
                faces[faces_count - 1].setColor(face_color);
                if (finished_reading_faces) {
                  finished_reading_file = true;
                } else {
                  status = PARSE_STATUS.LOOKING_FOR_FACES;
                }
              }

            }

            break;

          case StreamTokenizer.TT_WORD:

            if (st.sval.startsWith("#")) { // Start to read a comment
              if (status == PARSE_STATUS.LOOKING_FOR_COLORS) {
                if (color_index == 2) {
                  throw new Exception(
                      "Number of color values for vertex should be 1, 3, or 4, got "
                      + color_index);
                } else if (color_index > 0) {
                  color_index = 0; // Reset
                  adjustColor(colors[vertices_count - 1]);
                  colors[vertices_count - 1].a = 1.0f;
                }

                if (finished_reading_vertices) {
                  status = PARSE_STATUS.LOOKING_FOR_FACES;
                } else {
                  status = PARSE_STATUS.LOOKING_FOR_VERTICES;
                }
              } else if (status == PARSE_STATUS.LOOKING_FOR_FACE_COLORS) {
                if (color_index == 2) {
                  throw new Exception(
                      "Number of color values for the face should be 1, 3, or 4, got "
                      + color_index);
                } else if (color_index > 0) {
                  color_index = 0; // Reset
                  adjustColor(face_color);
                  face_color.a = 1.0f;
                  faces[faces_count - 1].setColor(face_color);
                }

                if (finished_reading_faces) {
                  finished_reading_file = true;
                } else {
                  status = PARSE_STATUS.LOOKING_FOR_FACES;
                }
              }
              reading_comment = true;
              break;
            } else if (status == PARSE_STATUS.LOOKING_FOR_HEADER) {
              parseOFFHeader(st.sval);
              if (yesSpaceDimension) {
                status = PARSE_STATUS.LOOKING_FOR_SPACE_DIM;
              } else {
                status = PARSE_STATUS.LOOKING_FOR_NVERTICES;
              }
              break;
            } // --- Error check
            else if (status == PARSE_STATUS.LOOKING_FOR_SPACE_DIM) {
              throw new Exception("Expecting Space Dimension, got "
                  + st.sval);
            } else if (status == PARSE_STATUS.LOOKING_FOR_NVERTICES) {
              throw new Exception("Expecting Number of Vertices, got "
                  + st.sval);
            } else if (status == PARSE_STATUS.LOOKING_FOR_VERTICES) {
              throw new Exception("Expecting vertex coordinate, got "
                  + st.sval);
            } else if (status == PARSE_STATUS.LOOKING_FOR_NORMALS) {
              throw new Exception("Expecting normal coordinate, got " + st.sval);
            } else if (status == PARSE_STATUS.LOOKING_FOR_COLORS) {
              throw new Exception("Expecting color value, got " + st.sval);
            } else if (status == PARSE_STATUS.LOOKING_FOR_FACES) {
              throw new Exception("Expecting integer for faces, got " + st.sval);
            } else if (status == PARSE_STATUS.LOOKING_FOR_FACE_COLORS) {
              throw new Exception("Expecting value for face color, got "
                  + st.sval);
            } else if (status == PARSE_STATUS.LOOKING_FOR_NEDGES) {
              //throw new Exception("Expecting Number of Vertices, got " + st.sval);
              System.err.println("Expecting Number of Edges, got " + st.sval
                  + " Ignored...");
              status = PARSE_STATUS.LOOKING_FOR_VERTICES;
            }

            // A word was found; the value is in sval
            String word = st.sval;
            break;
          case '"':

            // A double-quoted string was found; sval contains the contents
            String dquoteVal = st.sval;
            break;
          case '\'':

            // A single-quoted string was found; sval contains the contents
            String squoteVal = st.sval;
            break;

          case StreamTokenizer.TT_EOF:

            // End of file has been reached
            break;
          default:

            // A regular character was found; the value is the token itself
            char ch = (char) st.ttype;
            if ("#".equals(String.valueOf(ch))) {
              status = PARSE_STATUS.READING_COMMENT;
            }

            break;
        }

        if (finished_reading_file) {
          break;
        }

        token = st.nextToken();

      }

      rd.close();

    } catch (Exception e) {
      throw new Exception("Error parsing OFF input file: " + e.getMessage());
    }

    // --- If no colors in file, we set "default" color R,G,B,A=.666.
    if (!yesColors) {
      colors = new Color[NVertices];
      for (int i = 0; i < NVertices; i++) {
        colors[i] = new Color(0.666f, 0.666f, 0.666f, 0.666f);
      }
    }

  }

  void adjustColor(Color color) {
    if (color.r < 0) {
      color.r = 0;
    }
    if (color.g < 0) {
      color.g = 0;
    }
    if (color.b < 0) {
      color.b = 0;
    }
    if (color.a < 0) {
      color.a = 0;
    }
    if (color.r > 1.0f || color.g > 1.0f || color.b > 1.0f || color.a > 1.0f) {
      if (color.r > 255.0f) {
        color.r = 255.0f;
      }
      if (color.g > 255.0f) {
        color.g = 255.0f;
      }
      if (color.b > 255.0f) {
        color.b = 255.0f;
      }
      if (color.a > 255.0f) {
        color.a = 255.0f;
      }

      color.r /= 255.0f;
      color.g /= 255.0f;
      color.b /= 255.0f;
      color.a /= 255.0f;
    } else {
      if (color.r > 1.0f) {
        color.r = 1.0f;
      }
      if (color.g > 1.0f) {
        color.g = 1.0f;
      }
      if (color.b > 1.0f) {
        color.b = 1.0f;
      }
      if (color.a > 1.0f) {
        color.a = 1.0f;
      }

    }

  }

  public static void saveAsOff(List<Vertex> vertices3d, List<Face> faces, String filename) throws Exception {

    Vertex[] v = new Vertex[vertices3d.size()];
    for (int i = 0; i < vertices3d.size(); i++) {
      v[i] = vertices3d.get(i);
    }

    Face[] facesArray = new Face[faces.size()];
    for (int i = 0; i < faces.size(); i++) {
      facesArray[i] = faces.get(i);
    }
    saveAsOff(v, facesArray, filename);
  }

  public static void saveAsOff(Vertex vertices3d[], Face faces[], String filename) throws Exception {

    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(filename));
      writer.write("NOFF\n");
      writer.write(vertices3d.length + " " + faces.length + " 0\n");
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    for (int i = 0; i < vertices3d.length; i++) {
      writer.write(
          String.format("%8.4f", vertices3d[i].getX()) + " "
          + String.format("%8.4f", vertices3d[i].getY()) + " "
          + String.format("%8.4f", vertices3d[i].getZ()) + " "
          + String.format("%8.4f", vertices3d[i].getNormals()[0]) + " "
          + String.format("%8.4f", vertices3d[i].getNormals()[1]) + " "
          + String.format("%8.4f", vertices3d[i].getNormals()[2]) + "\n");
    }

    for (int i = 0; i < faces.length; i++) {
      writer.write(String.valueOf(faces[i].Nv));
      for (int j = 0; j < faces[i].Nv; j++) {
        writer.write(" " + faces[i].v[j]);
      }
      writer.newLine();
    }

    writer.close();
  }

  public static void main(String[] args) {

    String fileName = "", workingDirectory = "";
    FileDialog fd = new FileDialog(new Frame(),
        "Open OOGL File",
        FileDialog.LOAD);
    fd.setFile("*.off;*.ogl");
    fd.setVisible(true);
    if (fd.getFile() != null) {
      fileName = new String(fd.getFile());
      workingDirectory = new String(fd.getDirectory());
    }

    OOGL oogl = new OOGL();
    try {
      oogl.parseOFF(workingDirectory + fileName);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void parseOFFHeader(String header) throws Exception {
    if (!header.contains("OFF")) {
      throw new Exception("Header does not have OFF string");
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

  }
}
