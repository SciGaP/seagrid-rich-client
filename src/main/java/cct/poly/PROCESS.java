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
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class PROCESS {

  static final Logger logger = Logger.getLogger(PROCESS.class.getCanonicalName());
  static int facebit[] = {
      2, 2, 1, 1, 0, 0};

  int TET = 0; // use tetrahedral decomposition
  int NOTET = 1; // no tetrahedral decomposition  */

  /* parameters, function, storage */

  //std::vector<NORMAL>* gnormals;
  List gnormals = new ArrayList();
  //std::vector<VERTEX>* gvertices;
  List gvertices = new ArrayList();
  //std::vector<TRIANGLE> *gtriangles;
  List gtriangles = new ArrayList();

  ImplicitFunctionInterface function = null; /* implicit surface function */

  static float greenColor[] = {
      0, 1, 0};
  float size[] = null, delta; /* cube size, normal delta */
  int bounds[] = null; /* cube range within lattice */
  POINT start = new POINT(); /* start point on surface */

  // Global list of corners (keeps track of memory)
  //list<CORNER> corner_lst;
  List corner_lst = new ArrayList();

  //list<CUBE> cubes;		   /* active cubes */
  List cubes = new ArrayList();
  //vector<CENTERLIST> centers;	   /* cube center hash table */
  List centers = new ArrayList();
  //vector<CORNERLIST> corners;	   /* corner value hash table */
  ArrayList corners = new ArrayList();
  EDGETABLE edges = new EDGETABLE();

  boolean triangle(int i1, int i2, int i3) {
    TRIANGLE t = new TRIANGLE();
    t.v0 = i1;
    t.v1 = i2;
    t.v2 = i3;
    //gtriangles.push_back(t);
    gtriangles.add(t);
    return true;
  }

  public PROCESS() {
  }

  /* setcorner: return corner with the given lattice location
    set (and cache) its function value */
  CORNER setcorner(int i, int j, int k) {
    /* for speed, do corner value caching here */
    //corner_lst.push_back(CORNER());
    corner_lst.add(new CORNER());
    //CORNER c = corner_lst.back();
    CORNER c = (CORNER) corner_lst.get(corner_lst.size() - 1);
    int index = Polygonizer.HASH(i, j, k);

    c.i = i;
    c.x = start.x + ( i - .5f) * size[0];
    c.j = j;
    c.y = start.y + ( j - .5f) * size[1];
    c.k = k;
    c.z = start.z + ( k - .5f) * size[2];

    //CORNERLIST::const_iterator l = corners[index].begin();
    CORNERLIST cl = (CORNERLIST) corners.get(index);
    Iterator l = cl.iterator();
    //for (; l != corners[index].end(); ++l) {
    while (l.hasNext()) {
      CORNERELEMENT ce = (CORNERELEMENT) l.next();
      //if (l.i == i && l.j == j && l.k == k) {
      if (ce.i == i && ce.j == j && ce.k == k) {
        //c.value = l.value;
        c.value = ce.value;
        return c;
      }
    }

    c.value = (float) function.eval(c.x, c.y, c.z);
    CORNERELEMENT elem = new CORNERELEMENT(i, j, k, c.value);
    //corners[index].push_front(elem);
    cl.push_front(elem);
    return c;
  }

  /* setcorner: return corner with the given lattice location
    set (and cache) its function value */
  CORNER setcorner2(int i, int j, int k, int ix, int iy, int iz, float x,
                    float y, float z) {

    // for speed, do corner value caching here
    //corner_lst.push_back(CORNER());
    corner_lst.add(new CORNER());
    //CORNER c = corner_lst.back();
    CORNER c = (CORNER) corner_lst.get(corner_lst.size() - 1);
    /*
         //int index = Polygonizer.HASH(i, j, k);
         int index = Polygonizer.HASH(i + ix, j + iy, k + iz);
     */
    /*
     n, i,j,k: 0 0 0 0
     n, i,j,k: 1 0 0 1
     n, i,j,k: 2 0 1 0
     n, i,j,k: 3 0 1 1
     n, i,j,k: 4 1 0 0
     n, i,j,k: 5 1 0 1
     n, i,j,k: 6 1 1 0
     n, i,j,k: 7 1 1 1

     */

    c.i = ix + i;
    //c.x = start.x + ( (float) i - .5f) * size[0];
    c.x = x + i * size[0];
    c.j = iy + j;
    //c.y = start.y + ( (float) j - .5f) * size[1];
    c.y = y + j * size[1];
    c.k = iz + k;
    //c.z = start.z + ( (float) k - .5f) * size[2];
    c.z = z + k * size[2];

    /*
         //CORNERLIST::const_iterator l = corners[index].begin();
         CORNERLIST cl = (CORNERLIST) corners.get(index);
         Iterator l = cl.iterator();
         //for (; l != corners[index].end(); ++l) {
         while (l.hasNext()) {
      CORNERELEMENT ce = (CORNERELEMENT) l.next();
      //if (l.i == i && l.j == j && l.k == k) {
      if (ce.i == i && ce.j == j && ce.k == k) {
        //c.value = l.value;
        c.value = ce.value;
        return c;
      }
         }
     */

    c.value = (float) function.eval(c.x, c.y, c.z);

    /*
         CORNERELEMENT elem = new CORNERELEMENT(i, j, k, c.value);
         //corners[index].push_front(elem);
         cl.push_front(elem);
     */

    return c;
  }

  int FLIP(int i, int bit) {
    return i ^ 1 << bit;
  }

  int BIT(int i, int bit) {
    return (i >> bit) & 1;
  }

  /* testface: given cube at lattice (i, j, k), and four corners of face,
   * if surface crosses face, compute other four corners of adjacent cube
   * and add new cube to cube stack */

  void testface(int i, int j, int k, CUBE old, int face, int c1, int c2, int c3,
                int c4) {
    //static int facebit[6] = {
    //    2, 2, 1, 1, 0, 0};
    int n, bit = facebit[face];
    //int pos = old.corners[c1].value > 0.0 ? 1 : 0;
    boolean pos = old.corners[c1].value > 0.0;

    /* test if no surface crossing, cube out of bounds, or already visited: */
    if ( (old.corners[c2].value > 0) == pos &&
        (old.corners[c3].value > 0) == pos &&
        (old.corners[c4].value > 0) == pos) {
      return;
    }
    if (Math.abs(i) > bounds[0] || Math.abs(j) > bounds[1] ||
        Math.abs(k) > bounds[2]) {
      return;
    }
    //if (setcenter(centers, i, j, k)) {
    if (Polygonizer.setcenter(centers, i, j, k)) {
      return;
    }

    CUBE new_obj = new CUBE();

    /* create new_obj cube: */
    new_obj.i = i;
    new_obj.j = j;
    new_obj.k = k;
    for (n = 0; n < 8; n++) {
      new_obj.corners[n] = null;
    }
    new_obj.corners[FLIP(c1, bit)] = old.corners[c1];
    new_obj.corners[FLIP(c2, bit)] = old.corners[c2];
    new_obj.corners[FLIP(c3, bit)] = old.corners[c3];
    new_obj.corners[FLIP(c4, bit)] = old.corners[c4];
    for (n = 0; n < 8; n++) {
      if (new_obj.corners[n] == null) {
        new_obj.corners[n] = setcorner(i + BIT(n, 2), j + BIT(n, 1),
                                       k + BIT(n, 0));
      }
    }

    // Add new cube to top of stack
    //cubes.push_front(new_obj);
    cubes.add(0, new_obj);
  }

  /* find: search for point with value of given sign (0: neg, 1: pos) */

  TEST find(int sign, float x, float y, float z) {
    int i;
    boolean sign2 = sign != 0;
    TEST test = new TEST();
    float range[] = {
        size[0], size[1], size[2]};
    test.ok = 1;
    for (i = 0; i < 10000; i++) {

      //test.p.x = x + range * (RAND() - 0.5);
      //test.p.y = y + range * (RAND() - 0.5);
      //test.p.z = z + range * (RAND() - 0.5);
      test.p.x = x + range[0] * (float) (Math.random() - 0.5);
      test.p.y = y + range[1] * (float) (Math.random() - 0.5);
      test.p.z = z + range[2] * (float) (Math.random() - 0.5);

      test.value = (float) function.eval(test.p.x, test.p.y, test.p.z);
      //if (sign == (test.value > 0.0)) {
      if (sign2 && (test.value > 0.0)) {
        return test;
      }
      else if ( (!sign2) && (! (test.value > 0.0))) {
        return test;
      }

      range[0] = range[0] * 1.0005f; /* slowly expand search outwards */
      range[1] = range[1] * 1.0005f; /* slowly expand search outwards */
      range[2] = range[2] * 1.0005f; /* slowly expand search outwards */
    }
    test.ok = 0;
    return test;
  }

  /* vertid: return index for vertex on edge:
   * c1.value and c2.value are presumed of different sign
   * return saved index if any; else compute vertex and save */

  int vertid(CORNER c1, CORNER c2) {
    VERTEX v = new VERTEX();
    NORMAL n = new NORMAL();
    POINT a = new POINT(), b = new POINT();
    int vid = edges.getedge(c1.i, c1.j, c1.k, c2.i, c2.j, c2.k);
    if (vid != -1) {
      return vid; /* previously computed */
    }
    a.x = c1.x;
    a.y = c1.y;
    a.z = c1.z;
    b.x = c2.x;
    b.y = c2.y;
    b.z = c2.z;
    Polygonizer.converge(a, b, c1.value, function, v); /* position */
    //vnormal(function, & v, & n, delta); /* normal */
    n = Polygonizer.vnormal(function, v, n, delta); /* normal */
    //gvertices.push_back(v); /* save vertex */
    gvertices.add(v);
    //gnormals.push_back(n); /* save vertex */
    gnormals.add(n);
    vid = gvertices.size() - 1;
    edges.setedge(c1.i, c1.j, c1.k, c2.i, c2.j, c2.k, vid);
    return vid;
  }

  /* vertid: return index for vertex on edge:
   * c1.value and c2.value are presumed of different sign
   * return saved index if any; else compute vertex and save */

  int vertid2(CORNER c1, CORNER c2) {
    VERTEX v = new VERTEX();
    NORMAL n = new NORMAL();
    POINT a = new POINT(), b = new POINT();
    int vid;
    //int vid = edges.getedge(c1.i, c1.j, c1.k, c2.i, c2.j, c2.k);
    //if (vid != -1) {
    //  return vid; /* previously computed */
    //}
    a.x = c1.x;
    a.y = c1.y;
    a.z = c1.z;
    b.x = c2.x;
    b.y = c2.y;
    b.z = c2.z;
    if (false) { // Use extra function calls
      Polygonizer.converge(a, b, c1.value, function, v); /* position */
    }
    else { // Use liniar interpolation
      v.x = c1.x - c1.value * (c2.x - c1.x) / (c2.value - c1.value);
      v.y = c1.y - c1.value * (c2.y - c1.y) / (c2.value - c1.value);
      v.z = c1.z - c1.value * (c2.z - c1.z) / (c2.value - c1.value);
    }
    //vnormal(function, & v, & n, delta); /* normal */
    n = Polygonizer.vnormal(function, v, n, delta); /* normal */
    //gvertices.push_back(v); /* save vertex */
    gvertices.add(v);
    //gnormals.push_back(n); /* save vertex */
    gnormals.add(n);
    vid = gvertices.size() - 1;
    //edges.setedge(c1.i, c1.j, c1.k, c2.i, c2.j, c2.k, vid);
    return vid;
  }

  /**** Tetrahedral Polygonization ****/


  /* dotet: triangulate the tetrahedron
   * b, c, d should appear clockwise when viewed from a
   * return 0 if client aborts, 1 otherwise */

  boolean dotet(CUBE cube, int c1, int c2, int c3, int c4) {
    CORNER a = cube.corners[c1];
    CORNER b = cube.corners[c2];
    CORNER c = cube.corners[c3];
    CORNER d = cube.corners[c4];
    int index = 0, e1 = -1, e2 = -1, e3 = -1, e4 = -1, e5 = -1, e6 = -1;
    boolean apos, bpos, cpos, dpos;
    if (apos = (a.value > 0.0)) {
      index += 8;
    }
    if (bpos = (b.value > 0.0)) {
      index += 4;
    }
    if (cpos = (c.value > 0.0)) {
      index += 2;
    }
    if (dpos = (d.value > 0.0)) {
      index += 1;
    }
    /* index is now 4-bit number representing one of the 16 possible cases */
    if (apos != bpos) {
      e1 = vertid(a, b);
    }
    if (apos != cpos) {
      e2 = vertid(a, c);
    }
    if (apos != dpos) {
      e3 = vertid(a, d);
    }
    if (bpos != cpos) {
      e4 = vertid(b, c);
    }
    if (bpos != dpos) {
      e5 = vertid(b, d);
    }
    if (cpos != dpos) {
      e6 = vertid(c, d);
    }
    /* 14 productive tetrahedral cases (0000 and 1111 do not yield polygons */
    switch (index) {
      case 1:
        return triangle(e5, e6, e3);
      case 2:
        return triangle(e2, e6, e4);
      case 3:
        return triangle(e3, e5, e4) &&
            triangle(e3, e4, e2);
      case 4:
        return triangle(e1, e4, e5);
      case 5:
        return triangle(e3, e1, e4) &&
            triangle(e3, e4, e6);
      case 6:
        return triangle(e1, e2, e6) &&
            triangle(e1, e6, e5);
      case 7:
        return triangle(e1, e2, e3);
      case 8:
        return triangle(e1, e3, e2);
      case 9:
        return triangle(e1, e5, e6) &&
            triangle(e1, e6, e2);
      case 10:
        return triangle(e1, e3, e6) &&
            triangle(e1, e6, e4);
      case 11:
        return triangle(e1, e5, e4);
      case 12:
        return triangle(e3, e2, e4) &&
            triangle(e3, e4, e5);
      case 13:
        return triangle(e6, e2, e4);
      case 14:
        return triangle(e5, e3, e6);
    }
    return true;
  }

  /* dotet: triangulate the tetrahedron
   * b, c, d should appear clockwise when viewed from a
   * return 0 if client aborts, 1 otherwise */

  boolean dotet2(CUBE cube, int c1, int c2, int c3, int c4) {
    CORNER a = cube.corners[c1];
    CORNER b = cube.corners[c2];
    CORNER c = cube.corners[c3];
    CORNER d = cube.corners[c4];
    int index = 0, e1 = -1, e2 = -1, e3 = -1, e4 = -1, e5 = -1, e6 = -1;
    boolean apos, bpos, cpos, dpos;
    if (apos = (a.value > 0.0)) {
      index += 8;
    }
    if (bpos = (b.value > 0.0)) {
      index += 4;
    }
    if (cpos = (c.value > 0.0)) {
      index += 2;
    }
    if (dpos = (d.value > 0.0)) {
      index += 1;
    }

    // index is now 4-bit number representing one of the 16 possible cases
    if (apos != bpos) {
      e1 = vertid2(a, b);
    }
    if (apos != cpos) {
      e2 = vertid2(a, c);
    }
    if (apos != dpos) {
      e3 = vertid2(a, d);
    }
    if (bpos != cpos) {
      e4 = vertid2(b, c);
    }
    if (bpos != dpos) {
      e5 = vertid2(b, d);
    }
    if (cpos != dpos) {
      e6 = vertid2(c, d);
    }
    /* 14 productive tetrahedral cases (0000 and 1111 do not yield polygons */
    switch (index) {
      case 1:

        //((VERTEX)gvertices.get(e5)).rgb = greenColor;
        //((VERTEX)gvertices.get(e6)).rgb = greenColor;
        //((VERTEX)gvertices.get(e3)).rgb = greenColor;
        return triangle(e5, e6, e3);
      case 2:

        //((VERTEX)gvertices.get(e2)).rgb = greenColor;
        //((VERTEX)gvertices.get(e6)).rgb = greenColor;
        //((VERTEX)gvertices.get(e4)).rgb = greenColor;
        return triangle(e2, e6, e4);
      case 3:
        return triangle(e3, e5, e4) &&
            triangle(e3, e4, e2);
      case 4:
        return triangle(e1, e4, e5);
      case 5:
        return triangle(e3, e1, e4) &&
            triangle(e3, e4, e6);
      case 6:
        return triangle(e1, e2, e6) &&
            triangle(e1, e6, e5);
      case 7:
        return triangle(e1, e2, e3);
      case 8:
        return triangle(e1, e3, e2);
      case 9:
        return triangle(e1, e5, e6) &&
            triangle(e1, e6, e2);
      case 10:
        return triangle(e1, e3, e6) &&
            triangle(e1, e6, e4);
      case 11:
        return triangle(e1, e5, e4);
      case 12:
        return triangle(e3, e2, e4) &&
            triangle(e3, e4, e5);
      case 13:
        return triangle(e6, e2, e4);
      case 14:
        return triangle(e5, e3, e6);
    }
    return true;
  }

  /* docube: triangulate the cube directly, without decomposition */

  boolean docube(CUBE cube) {
    int index = 0;
    for (int i = 0; i < 8; i++) {
      if (cube.corners[i].value > 0.0) {
        index += (1 << i);
      }
    }

    INTLISTS intlists = CUBETABLE.get_cubetable_entry(index);
    //INTLISTS::const_iterator polys = intlists.begin();
    Iterator polys = intlists.iterator();
    //for (; polys != intlists.end(); ++polys) {
    while (polys.hasNext()) {
      //INTLIST::const_iterator edges = polys.begin();
      INTLIST intlist = (INTLIST) polys.next();
      Iterator edges = intlists.iterator();
      int a = -1, b = -1, count = 0;
      //for (; edges != polys.end(); ++edges) {
      while (edges.hasNext()) {
        int edge = ( (Integer) edges.next()).intValue();
        //CORNER c1 = cube.corners[corner1[ ( * edges)]];
        //CORNER c2 = cube.corners[corner2[ ( * edges)]];
        CORNER c1 = cube.corners[CUBETABLE.corner1[edge]];
        CORNER c2 = cube.corners[CUBETABLE.corner2[edge]];
        int c = vertid(c1, c2);
        if (++count > 2 && !triangle(a, b, c)) {
          return false;
        }
        if (count < 3) {
          a = b;
        }
        b = c;
      }
    }
    return true;
  }

  /**** An Implicit Surface Polygonizer ****/


  /* polygonize: polygonize the implicit surface function
   *   arguments are:
   *	 ImplicitFunction
   *	     the implicit surface function
   *	     return negative for inside, positive for outside
   *	 float size
   *	     width of the partitioning cube
   *   float delta
   *       a small step - used for gradient computation
   *	 int bounds
   *	     max. range of cubes (+/- on the three axes) from first cube
   *   _gvertices, _gnormals, _gtriangles
   *       the data structures into which information is put.
   */

  public PROCESS(ImplicitFunctionInterface _function, float _size[], float _delta,
                 int _bounds[], List _gvertices, List _gnormals, List _gtriangles) {
    function = _function;
    size = _size;
    delta = _delta;
    bounds = _bounds;
    centers = new ArrayList(Polygonizer.HASHSIZE);
    corners = new ArrayList(Polygonizer.HASHSIZE);
    for (int i = 0; i < Polygonizer.HASHSIZE; i++) {
      corners.add(new CORNERLIST());
      centers.add(new CENTERLIST());
    }

    gvertices = _gvertices;
    gnormals = _gnormals;
    gtriangles = _gtriangles;
  }

  void march(int mode, float x, float y, float z) throws Exception {
    boolean noabort;
    TEST in = new TEST(), out = new TEST();

    /* find point on surface, beginning search at (x, y, z): */
    //srand(1);
    //Math.random();
    in = find(1, x, y, z);
    out = find(0, x, y, z);
    //if (!in.ok || !out.ok) {
    if (in.ok == 0 || out.ok == 0) {
      throw new Exception("can't find starting point");
    }

    //converge( & in.p, & out.p, in.value, function, & start);
    Polygonizer.converge(in.p, out.p, in.value, function, start);

    /* push initial cube on stack: */
    CUBE cube = new CUBE();
    cube.i = cube.j = cube.k = 0;
    //cubes.push_front(cube);
    cubes.add(0, cube);

    /* set corners of initial cube: */
    //for (int n = 0; n < 8; n++) {
    //  cubes.front().corners[n] = setcorner(BIT(n, 2), BIT(n, 1), BIT(n, 0));
    //}
    CUBE cc = (CUBE) cubes.get(0);
    for (int n = 0; n < 8; n++) {
      cc.corners[n] = setcorner(BIT(n, 2), BIT(n, 1), BIT(n, 0));
      logger.info("n, i,j,k: " + n + " " + BIT(n, 2) + " " +
                         BIT(n, 1) + " " + BIT(n, 0) + " x,y,z, value: " +
                         cc.corners[n].x + " " + cc.corners[n].y + " " +
                         cc.corners[n].z + " " + cc.corners[n].value);
    }

    Polygonizer.setcenter(centers, 0, 0, 0);

    while (cubes.size() != 0) {
      /* process active cubes till none left */
      //CUBE c = cubes.front();
      CUBE c = (CUBE) cubes.get(0);

      noabort = mode == TET ?
          /* either decompose into tetrahedra and polygonize: */
          dotet(c, CUBETABLE.LBN, CUBETABLE.LTN, CUBETABLE.RBN, CUBETABLE.LBF) &&
          dotet(c, CUBETABLE.RTN, CUBETABLE.LTN, CUBETABLE.LBF, CUBETABLE.RBN) &&
          dotet(c, CUBETABLE.RTN, CUBETABLE.LTN, CUBETABLE.LTF, CUBETABLE.LBF) &&
          dotet(c, CUBETABLE.RTN, CUBETABLE.RBN, CUBETABLE.LBF, CUBETABLE.RBF) &&
          dotet(c, CUBETABLE.RTN, CUBETABLE.LBF, CUBETABLE.LTF, CUBETABLE.RBF) &&
          dotet(c, CUBETABLE.RTN, CUBETABLE.LTF, CUBETABLE.RTF, CUBETABLE.RBF)
          :
          /* or polygonize the cube directly: */
          docube(c);
      if (!noabort) {
        throw new Exception("aborted");
      }

      /* pop current cube from stack */
      //cubes.pop_front();
      cubes.remove(0);

      /* test six face directions, maybe add to stack: */
      testface(c.i - 1, c.j, c.k, c, CUBETABLE.L, CUBETABLE.LBN, CUBETABLE.LBF,
               CUBETABLE.LTN, CUBETABLE.LTF);
      testface(c.i + 1, c.j, c.k, c, CUBETABLE.R, CUBETABLE.RBN, CUBETABLE.RBF,
               CUBETABLE.RTN, CUBETABLE.RTF);
      testface(c.i, c.j - 1, c.k, c, CUBETABLE.B, CUBETABLE.LBN, CUBETABLE.LBF,
               CUBETABLE.RBN, CUBETABLE.RBF);
      testface(c.i, c.j + 1, c.k, c, CUBETABLE.T, CUBETABLE.LTN, CUBETABLE.LTF,
               CUBETABLE.RTN, CUBETABLE.RTF);
      testface(c.i, c.j, c.k - 1, c, CUBETABLE.N, CUBETABLE.LBN, CUBETABLE.LTN,
               CUBETABLE.RBN, CUBETABLE.RTN);
      testface(c.i, c.j, c.k + 1, c, CUBETABLE.F, CUBETABLE.LBF, CUBETABLE.LTF,
               CUBETABLE.RBF, CUBETABLE.RTF);
    }
  }

  void march3(int mode, float dataOrigin[], int numberOfVoxels[],
              float xyz_step[]) throws Exception {
    boolean noabort;
    TEST in = new TEST(), out = new TEST();
    float origin[] = new float[3];

    for (int ix = 0; ix < numberOfVoxels[0] - 2; ix++) {
      for (int iy = 0; iy < numberOfVoxels[1] - 2; iy++) {
        for (int iz = 0; iz < numberOfVoxels[2] - 2; iz++) {

          if (Polygonizer.setcenter(centers, ix, iy, iz)) {
            continue;
          }

          origin[0] = dataOrigin[0] + xyz_step[0] * ix;
          origin[1] = dataOrigin[1] + xyz_step[1] * iy;
          origin[2] = dataOrigin[2] + xyz_step[2] * iz;

          in = find2(1, origin, xyz_step);
          out = find2(0, origin, xyz_step);

          if (in.ok == 0 || out.ok == 0) {
            continue;
          }

          Polygonizer.converge(in.p, out.p, in.value, function, start);

          CUBE cube = new CUBE();
          cube.i = cube.j = cube.k = 0;
          cubes.add(0, cube);

          CUBE cc = (CUBE) cubes.get(0);
          for (int n = 0; n < 8; n++) {
            cc.corners[n] = setcorner(BIT(n, 2), BIT(n, 1), BIT(n, 0));
          }

          //Polygonizer.setcenter(centers, ix, iy, iz);

          while (cubes.size() != 0) {
            /* process active cubes till none left */
            //CUBE c = cubes.front();
            CUBE c = (CUBE) cubes.get(0);

            noabort = mode == TET ?
                /* either decompose into tetrahedra and polygonize: */
                dotet(c, CUBETABLE.LBN, CUBETABLE.LTN, CUBETABLE.RBN,
                      CUBETABLE.LBF) &&
                dotet(c, CUBETABLE.RTN, CUBETABLE.LTN, CUBETABLE.LBF,
                      CUBETABLE.RBN) &&
                dotet(c, CUBETABLE.RTN, CUBETABLE.LTN, CUBETABLE.LTF,
                      CUBETABLE.LBF) &&
                dotet(c, CUBETABLE.RTN, CUBETABLE.RBN, CUBETABLE.LBF,
                      CUBETABLE.RBF) &&
                dotet(c, CUBETABLE.RTN, CUBETABLE.LBF, CUBETABLE.LTF,
                      CUBETABLE.RBF) &&
                dotet(c, CUBETABLE.RTN, CUBETABLE.LTF, CUBETABLE.RTF,
                      CUBETABLE.RBF)
                :
                /* or polygonize the cube directly: */
                docube(c);
            if (!noabort) {
              throw new Exception("aborted");
            }

            cubes.remove(0);

            /* test six face directions, maybe add to stack: */
            testface(c.i - 1, c.j, c.k, c, CUBETABLE.L, CUBETABLE.LBN,
                     CUBETABLE.LBF, CUBETABLE.LTN, CUBETABLE.LTF);
            testface(c.i + 1, c.j, c.k, c, CUBETABLE.R, CUBETABLE.RBN,
                     CUBETABLE.RBF, CUBETABLE.RTN, CUBETABLE.RTF);
            testface(c.i, c.j - 1, c.k, c, CUBETABLE.B, CUBETABLE.LBN,
                     CUBETABLE.LBF, CUBETABLE.RBN, CUBETABLE.RBF);
            testface(c.i, c.j + 1, c.k, c, CUBETABLE.T, CUBETABLE.LTN,
                     CUBETABLE.LTF, CUBETABLE.RTN, CUBETABLE.RTF);
            testface(c.i, c.j, c.k - 1, c, CUBETABLE.N, CUBETABLE.LBN,
                     CUBETABLE.LTN, CUBETABLE.RBN, CUBETABLE.RTN);
            testface(c.i, c.j, c.k + 1, c, CUBETABLE.F, CUBETABLE.LBF,
                     CUBETABLE.LTF, CUBETABLE.RBF, CUBETABLE.RTF);
          }

        }
      }
    }

    return;
  }

  void march2(int mode, float dataOrigin[], int numberOfVoxels[], float xyz_step[]) throws Exception {
    boolean noabort;
    TEST in = new TEST(), out = new TEST();
    float origin[] = new float[3];
    int count = 0;

    for (int ix = 0; ix < numberOfVoxels[0] - 2; ix++) {
      for (int iy = 0; iy < numberOfVoxels[1] - 2; iy++) {
        for (int iz = 0; iz < numberOfVoxels[2] - 2; iz++) {

          origin[0] = dataOrigin[0] + xyz_step[0] * ix;
          origin[1] = dataOrigin[1] + xyz_step[1] * iy;
          origin[2] = dataOrigin[2] + xyz_step[2] * iz;

          in = find2(1, origin, xyz_step);
          out = find2(0, origin, xyz_step);

          if (in.ok == 0 || out.ok == 0) {
            continue;
          }
          ++count;

          //Polygonizer.converge(in.p, out.p, in.value, function, start);

          CUBE cube = new CUBE();
          cube.i = ix;
          cube.j = iy;
          cube.k = iz;
          //cubes.add(0, cube);

          /*
                n, i,j,k: 0 0 0 0
                n, i,j,k: 1 0 0 1
                n, i,j,k: 2 0 1 0
                n, i,j,k: 3 0 1 1
                n, i,j,k: 4 1 0 0
                n, i,j,k: 5 1 0 1
                n, i,j,k: 6 1 1 0
                n, i,j,k: 7 1 1 1
           */

          //CUBE cc = (CUBE) cubes.get(0);
          for (int n = 0; n < 8; n++) {
            cube.corners[n] = setcorner2(BIT(n, 2), BIT(n, 1), BIT(n, 0), ix,
                                         iy, iz,
                                         origin[0], origin[1], origin[2]);
            //logger.info("n, i,j,k: " + n + " " + BIT(n, 2) + " " +
            //                   BIT(n, 1) + " " + BIT(n, 0));
            if (ix == 0 && iy == 0 && iz == 0) {
              logger.info("n, i,j,k: " + n + " " + BIT(n, 2) + " " +
                                 BIT(n, 1) + " " + BIT(n, 0) +
                                 " x,y,z, value: " +
                                 cube.corners[n].x + " " + cube.corners[n].y +
                                 " " +
                                 cube.corners[n].z + " " +
                                 cube.corners[n].value);
            }

          }

          //Polygonizer.setcenter(centers, ix, iy, iz);

          noabort = mode == TET ?
              /* either decompose into tetrahedra and polygonize: */
              dotet2(cube, CUBETABLE.LBN, CUBETABLE.LTN, CUBETABLE.RBN,
                     CUBETABLE.LBF) &&
              dotet2(cube, CUBETABLE.RTN, CUBETABLE.LTN, CUBETABLE.LBF,
                     CUBETABLE.RBN) &&
              dotet2(cube, CUBETABLE.RTN, CUBETABLE.LTN, CUBETABLE.LTF,
                     CUBETABLE.LBF) &&
              dotet2(cube, CUBETABLE.RTN, CUBETABLE.RBN, CUBETABLE.LBF,
                     CUBETABLE.RBF) &&
              dotet2(cube, CUBETABLE.RTN, CUBETABLE.LBF, CUBETABLE.LTF,
                     CUBETABLE.RBF) &&
              dotet2(cube, CUBETABLE.RTN, CUBETABLE.LTF, CUBETABLE.RTF,
                     CUBETABLE.RBF)
              :
              /* or polygonize the cube directly: */
              docube(cube);
          if (!noabort) {
            throw new Exception("aborted");
          }

          //cubes.remove(0);

          //logger.info("Cube: "+count+" new # trinangles: "+gtriangles.size());

          // test six face directions, maybe add to stack:
          /*
           testface(cc.i - 1, cc.j, cc.k, cc, CUBETABLE.L, CUBETABLE.LBN,
                   CUBETABLE.LBF, CUBETABLE.LTN, CUBETABLE.LTF);
           testface(cc.i + 1, cc.j, cc.k, cc, CUBETABLE.R, CUBETABLE.RBN,
                   CUBETABLE.RBF, CUBETABLE.RTN, CUBETABLE.RTF);
           testface(cc.i, cc.j - 1, cc.k, cc, CUBETABLE.B, CUBETABLE.LBN,
                   CUBETABLE.LBF, CUBETABLE.RBN, CUBETABLE.RBF);
           testface(cc.i, cc.j + 1, cc.k, cc, CUBETABLE.T, CUBETABLE.LTN,
                   CUBETABLE.LTF, CUBETABLE.RTN, CUBETABLE.RTF);
           testface(cc.i, cc.j, cc.k - 1, cc, CUBETABLE.N, CUBETABLE.LBN,
                   CUBETABLE.LTN, CUBETABLE.RBN, CUBETABLE.RTN);
           testface(cc.i, cc.j, cc.k + 1, cc, CUBETABLE.F, CUBETABLE.LBF,
                   CUBETABLE.LTF, CUBETABLE.RBF, CUBETABLE.RTF);
           */
        }
      }
    }
    logger.info("# cells to be polygomized: " + count);
    return;
    /*
         // find point on surface, beginning search at (x, y, z):
         in = find(1, x, y, z);
         out = find(0, x, y, z);
         if (in.ok == 0 || out.ok == 0) {
      throw new Exception("can't find starting point");
         }

         Polygonizer.converge(in.p, out.p, in.value, function, start);

         // push initial cube on stack:
         CUBE cube = new CUBE();
         cube.i = cube.j = cube.k = 0;
         cubes.add(0, cube);

         // set corners of initial cube:
         CUBE cc = (CUBE) cubes.get(0);
         for (int n = 0; n < 8; n++) {
      cc.corners[n] = setcorner(BIT(n, 2), BIT(n, 1), BIT(n, 0));
         }

         Polygonizer.setcenter(centers, 0, 0, 0);

         while (cubes.size() != 0) {
      // process active cubes till none left
      CUBE c = (CUBE) cubes.get(0);

      noabort = mode == TET ?
          // either decompose into tetrahedra and polygonize:
     dotet(c, CUBETABLE.LBN, CUBETABLE.LTN, CUBETABLE.RBN, CUBETABLE.LBF) &&
     dotet(c, CUBETABLE.RTN, CUBETABLE.LTN, CUBETABLE.LBF, CUBETABLE.RBN) &&
     dotet(c, CUBETABLE.RTN, CUBETABLE.LTN, CUBETABLE.LTF, CUBETABLE.LBF) &&
     dotet(c, CUBETABLE.RTN, CUBETABLE.RBN, CUBETABLE.LBF, CUBETABLE.RBF) &&
     dotet(c, CUBETABLE.RTN, CUBETABLE.LBF, CUBETABLE.LTF, CUBETABLE.RBF) &&
          dotet(c, CUBETABLE.RTN, CUBETABLE.LTF, CUBETABLE.RTF, CUBETABLE.RBF)
          :
          // or polygonize the cube directly:
          docube(c);
      if (!noabort) {
        throw new Exception("aborted");
      }

      // pop current cube from stack
      cubes.remove(0);

      // test six face directions, maybe add to stack:
      testface(c.i - 1, c.j, c.k, c, CUBETABLE.L, CUBETABLE.LBN, CUBETABLE.LBF,
               CUBETABLE.LTN, CUBETABLE.LTF);
      testface(c.i + 1, c.j, c.k, c, CUBETABLE.R, CUBETABLE.RBN, CUBETABLE.RBF,
               CUBETABLE.RTN, CUBETABLE.RTF);
      testface(c.i, c.j - 1, c.k, c, CUBETABLE.B, CUBETABLE.LBN, CUBETABLE.LBF,
               CUBETABLE.RBN, CUBETABLE.RBF);
      testface(c.i, c.j + 1, c.k, c, CUBETABLE.T, CUBETABLE.LTN, CUBETABLE.LTF,
               CUBETABLE.RTN, CUBETABLE.RTF);
      testface(c.i, c.j, c.k - 1, c, CUBETABLE.N, CUBETABLE.LBN, CUBETABLE.LTN,
               CUBETABLE.RBN, CUBETABLE.RTN);
      testface(c.i, c.j, c.k + 1, c, CUBETABLE.F, CUBETABLE.LBF, CUBETABLE.LTF,
               CUBETABLE.RBF, CUBETABLE.RTF);
         }
     */
  }

  /**
   *  find: search for point with value of given sign (0: neg, 1: pos)
   *
   */

  TEST find2(int sign, float origin[], float range[]) {
    boolean sign2 = sign != 0;
    TEST test = new TEST();
    test.ok = 1;

    for (int ix = 0; ix < 2; ix++) {
      for (int iy = 0; iy < 2; iy++) {
        for (int iz = 0; iz < 2; iz++) {
          test.p.x = origin[0] + range[0] * ix;
          test.p.y = origin[1] + range[1] * iy;
          test.p.z = origin[2] + range[2] * iz;

          test.value = (float) function.eval(test.p.x, test.p.y, test.p.z);
          //if (sign == (test.value > 0.0)) {
          if (sign2 && (test.value > 0.0)) {
            return test;
          }
          else if ( (!sign2) && (! (test.value > 0.0))) {
            return test;
          }
        }
      }
    }

    test.ok = 0;
    return test;
  }

}
