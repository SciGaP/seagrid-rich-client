package cct.oogl;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2007</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class Face {

  int Nv = 0; // # vertices on this face
  public int v[] = null; // vertex indices
  public int r, g, b, a = 255; //3 or 4 integers: RGB[A] values 0..255
  public float rf, gf, bf, af = 1; //  # 3 or 4 floats: RGB[A] values 0..1
  public List<Vertex> vertices = new ArrayList<Vertex>();
  public boolean visible = true;

   public Face() {
  }

  public Face(Face f) {
    if (f.v.length > 0) {
      v = new int[f.v.length];
      for (int i = 0; i < v.length; i++) {
        v[i] = f.v[i];
      }
      Nv = v.length;
    } else {
      Nv = f.vertices.size();
    }
    r = f.r;
    g = f.g;
    b = f.b;
    a = f.a;
    rf = f.rf;
    gf = f.gf;
    bf = f.bf;
    af = f.af;

    vertices.addAll(f.vertices);
  }

  public void incrementIndices(int inc) {
    for (int i = 0; i < v.length; i++) {
      v[i] += inc;
    }
  }

  public Face(Vertex v1, Vertex v2, Vertex v3) {
    addVertex(v1);
    addVertex(v2);
    addVertex(v3);
    Nv = 3;
  }

  public Face(int _v[]) {
    v = _v;
    Nv = v.length;
  }

  public Face(int i, int j, int k) {
    v = new int[]{i, j, k};
    Nv = v.length;
  }

  public void addVertex(Vertex vertex) {
    vertices.add(vertex);
  }

  public int getNVertices() {
    return Nv;
  }

 
  public void setColor(Color color) {
    rf = color.r;
    gf = color.g;
    bf = color.b;
    af = color.a;

    r = (int) (rf * 255.0f);
    g = (int) (gf * 255.0f);
    b = (int) (bf * 255.0f);
    a = (int) (af * 255.0f);
  }

  public boolean isVisible() {
    boolean visible = false;
    for (Vertex vertex : vertices) {
      if (vertex.isVisible()) {
        return true;
      }
    }
    return visible;
  }
}
