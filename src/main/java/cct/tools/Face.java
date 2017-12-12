package cct.tools;

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
public class Face {
   int Nv = 0; // # vertices on this face
   int v[] = null; // vertex indices
   int r, g, b, a = 255; //3 or 4 integers: RGB[A] values 0..255
   float rf, gf, bf, af = 1; //  # 3 or 4 floats: RGB[A] values 0..1
   public Face() {
   }

   public Face(int _v[]) {
      v = _v;
      Nv = v.length;
   }
}
