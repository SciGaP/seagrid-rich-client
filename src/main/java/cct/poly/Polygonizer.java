package cct.poly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import cct.interfaces.ImplicitFunctionInterface;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *   *********************************************************************

 polygonizer.h

 This is Jules Bloomenthal's implicit surface polygonizer from GRAPHICS
 GEMS IV. Bloomenthal's polygonizer is still used and the present code
 is simply the original code morphed into C++.

 J. Andreas B�rentzen 2003.

 *********************************************************************

 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class Polygonizer {

  static final Logger logger = Logger.getLogger(Polygonizer.class.getCanonicalName());
  static boolean dotet = true;

  static int TET = 0; // use tetrahedral decomposition
  static int NOTET = 1; // no tetrahedral decomposition  */

  static int RES = 10; /* # converge iterations    */

  static int L = 0; /* left direction:	-x, -i */
  static int R = 1; /* right direction:	+x, +i */
  static int B = 2; /* bottom direction: -y, -j */
  static int T = 3; /* top direction:	+y, +j */
  static int N = 4; /* near direction:	-z, -k */
  static int F = 5; /* far direction:	+z, +k */
  static int LBN = 0; /* left bottom near corner  */
  static int LBF = 1; /* left bottom far corner   */
  static int LTN = 2; /* left top near corner     */
  static int LTF = 3; /* left top far corner      */
  static int RBN = 4; /* right bottom near corner */
  static int RBF = 5; /* right bottom far corner  */
  static int RTN = 6; /* right top near corner    */
  static int RTF = 7; /* right top far corner     */

  /* the LBN corner of cube (i, j, k), corresponds with location
   * (start.x+(i-.5)*size, start.y+(j-.5)*size, start.z+(k-.5)*size) */

//typedef POINT VERTEX;
//typedef POINT NORMAL;

  static int HASHBIT = 5;

  public static final int HASHSIZE = (1<< (3 * HASHBIT));

  static int MASK = ( (1<<HASHBIT) - 1);

  /** Polygonizer is the class used to perform polygonization.*/
  //std::vector<NORMAL> gnormals;
  List gnormals = new ArrayList();
  //std::vector<VERTEX> gvertices;
  List gvertices = new ArrayList();
  //std::vector<TRIANGLE> gtriangles;
  List gtriangles = new ArrayList();

  ImplicitFunctionInterface func;
  float size[] = null;
  int bounds[] = null;

  /** Constructor of Polygonizer. The first argument is the ImplicitFunction
   that we wish to polygonize. The second argument is the size of the
   polygonizing cell. The final arg. is the limit to how far away we will
                  look for components of the implicit surface. */


  //Polygonizer(ImplicitFunction _func, float _size, int _bounds):
  //func(_func), size(_size), bounds(_bounds) {}

  /** March erases the triangles gathered so far and builds a new
   polygonization. The first argument indicates whether the primitive
   cell is a tetrahedron (true) or a cube (fale). The final x,y,z
                  arguments indicate a point near the surface. */

  /** Return number of triangles generated after the polygonization.
                  Call this function only when march has been called. */
  public int no_triangles() {
    return gtriangles.size();
  }

  /** Return number of vertices generated after the polygonization.
                  Call this function only when march has been called. */
  int no_vertices() {
    return gvertices.size();
  }

  /** Return number of normals generated after the polygonization.
                  Of course the result of calling this function is the same as
                  no_vertices.
                  Call this function only when march has been called. */
  int no_normals() {
    return gnormals.size();
  }

  /// Return triangle with index i.
  public TRIANGLE get_triangle(int i) {
    //return (TRIANGLE) gtriangles[i];
    return (TRIANGLE) gtriangles.get(i);
  }

  /// Return vertex with index i.
  public VERTEX get_vertex(int i) {
    //return (VERTEX) gvertices[i];
    return (VERTEX) gvertices.get(i);
  }

  /// Return normal with index i.
  public NORMAL get_normal(int i) {
    return (NORMAL) gnormals.get(i);
  }

  public Polygonizer(ImplicitFunctionInterface _func, float _size[], int _bounds[]) {
    func = _func;
    size = _size;
    bounds = _bounds;
  }

  /*
    inline float RAND()
    {
      return (rand()&32767)/32767.0f;
    }
   */



  public static int HASH(int i, int j, int k) {
    return ( ( ( ( (i & MASK) << HASHBIT) | j & MASK) << HASHBIT) | k & MASK);
  }

  int BIT(int i, int bit) {
    return (i >> bit) & 1;
  }

  // flip the given bit of i
  public static int FLIP(int i, int bit) {
    return i ^ 1 << bit;
  }

  //struct TEST {		   /* test the function for a signed value */
  //  POINT p;			   /* location of test */
  //  float value;		   /* function value at p */
  //  int ok;			   /* if value is of correct sign */
  //};






  //typedef list<CENTERELEMENT> CENTERLIST;
  List CENTERLIST = new ArrayList();

  //typedef list<CORNERELEMENT> CORNERLIST;
  List CORNERLIST = new ArrayList();

  //typedef list<EDGEELEMENT> EDGELIST;
  List EDGELIST = new ArrayList();
  //typedef list<int> INTLIST;
  List INTLIST = new ArrayList();

  //typedef list<INTLIST> INTLISTS;
  List INTLISTS = new ArrayList();

  //----------------------------------------------------------------------
  // Implicit surface evaluation functions
  //----------------------------------------------------------------------

  /* converge: from two points of differing sign, converge to zero crossing */

  public static void converge(POINT p1, POINT p2, float v,
                              ImplicitFunctionInterface function, POINT p) {
    int i = 0;
    POINT pos = new POINT(), neg = new POINT();
    if (v < 0) {
      pos.x = p2.x;
      pos.y = p2.y;
      pos.z = p2.z;
      neg.x = p1.x;
      neg.y = p1.y;
      neg.z = p1.z;
    }
    else {
      pos.x = p1.x;
      pos.y = p1.y;
      pos.z = p1.z;
      neg.x = p2.x;
      neg.y = p2.y;
      neg.z = p2.z;
    }
    while (true) {
      p.x = 0.5f * (pos.x + neg.x);
      p.y = 0.5f * (pos.y + neg.y);
      p.z = 0.5f * (pos.z + neg.z);
      if (i++ == RES) {
        return;
      }
      if ( (function.eval(p.x, p.y, p.z)) > 0.0) {
        pos.x = p.x;
        pos.y = p.y;
        pos.z = p.z;
      }
      else {
        neg.x = p.x;
        neg.y = p.y;
        neg.z = p.z;
      }
    }
  }

  /* vnormal: compute unit length surface normal at point */

  public static NORMAL vnormal(ImplicitFunctionInterface function, POINT point, NORMAL n,
                               float delta) {
    float f = (float) function.eval(point.x, point.y, point.z);
    n.x = (float) function.eval(point.x + delta, point.y, point.z) - f;
    n.y = (float) function.eval(point.x, point.y + delta, point.z) - f;
    n.z = (float) function.eval(point.x, point.y, point.z + delta) - f;
    f = (float) Math.sqrt(n.x * n.x + n.y * n.y + n.z * n.z);
    if (f != 0.0) {
      n.x /= f;
      n.y /= f;
      n.z /= f;
    }
    return n;
  }

  // ----------------------------------------------------------------------




  // ----------------------------------------------------------------------



  /* setcenter: set (i,j,k) entry of table[]
   return 1 if already set; otherwise, set and return 0 */
  //int setcenter(vector<CENTERLIST>& table,
  public static boolean setcenter(List table, int i, int j, int k) {
    int index = HASH(i, j, k);
    //CENTERLIST::const_iterator q = table[index].begin();
    CENTERLIST xxx = (CENTERLIST) table.get(index);
    Iterator q = xxx.iterator();
    //for (; q != table[index].end(); ++q) {
    while (q.hasNext()) {
      CENTERELEMENT ce = (CENTERELEMENT) q.next();
      //if (q.i == i && q.j == j && q.k == k) {
      if (ce.i == i && ce.j == j && ce.k == k) {
        return true;
      }
    }

    CENTERELEMENT elem = new CENTERELEMENT(i, j, k);
    //table[index].push_front(elem);
    xxx.add(0, elem);
    return false;
  }

  INTLISTS get_cubetable_entry(int i) {
    //static CUBETABLE c;
    //return c.get_lists(i);
    return CUBETABLE.get_lists(i);
  }

  public void march(boolean tetra, float x, float y, float z) {
    gvertices.clear();
    gnormals.clear();
    gtriangles.clear();
    PROCESS p = new PROCESS(func, size, size[0] / (RES * RES), bounds, // !!! Redo
                            gvertices, gnormals, gtriangles);
    try {
      p.march(tetra ? TET : NOTET, x, y, z);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void march2(boolean tetra, float dataOrigin[], int numberOfVoxels[],
                     float xyz_step[]) {
    gvertices.clear();
    gnormals.clear();
    gtriangles.clear();
    PROCESS p = new PROCESS(func, size, size[0] / (RES * RES), bounds, // !!! Redo
                            gvertices, gnormals, gtriangles);
    try {
      p.march2(tetra ? TET : NOTET, dataOrigin, numberOfVoxels, xyz_step);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void march3(boolean tetra, float dataOrigin[], int numberOfVoxels[],
                     float xyz_step[]) {
    gvertices.clear();
    gnormals.clear();
    gtriangles.clear();
    PROCESS p = new PROCESS(func, size, size[0] / (RES * RES), bounds, // !!! Redo
                            gvertices, gnormals, gtriangles);
    try {
      p.march3(tetra ? TET : NOTET, dataOrigin, numberOfVoxels, xyz_step);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    //Torus torus = new Torus();
    float size[] = {
        .05f, .05f, .05f};
    int bounds[] = {
        30, 30, 30};
    Polygonizer polygonizer = new Polygonizer(new Torus(), size, bounds);
    polygonizer.march(dotet, 0.f, 0.f, 0.f);

    logger.info("# triangles: " + polygonizer.no_triangles());
    for (int i = 0; i < polygonizer.no_triangles(); ++i) {
      TRIANGLE t = polygonizer.get_triangle(i);
      NORMAL n0 = polygonizer.get_normal(t.v0);
      VERTEX v0 = polygonizer.get_vertex(t.v0);

      NORMAL n1 = polygonizer.get_normal(t.v1);
      VERTEX v1 = polygonizer.get_vertex(t.v1);

      NORMAL n2 = polygonizer.get_normal(t.v2);
      VERTEX v2 = polygonizer.get_vertex(t.v2);
    }

  }

}

class Torus
    implements ImplicitFunctionInterface {
  public Torus() {
    super();
  }

  @Override
  public double eval(float x, float y, float z) {
    float x2 = x * x, y2 = y * y, z2 = z * z;
    float a = x2 + y2 + z2 + (0.5f * 0.5f) - (0.1f * 0.1f);
    return - (a * a - 4.0f * (0.5f * 0.5f) * (y2 + z2));
  }
}
