/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.oogl;

/**
 *
 * @author vvv900
 */
import cct.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vvv900
 */
public class Vertex extends Point3d {

  boolean visible = true;

  List<Face> faces = new ArrayList<Face>();
  double[] normals = new double[3];

  public Vertex(double x, double y, double z) {
    super(x, y, z);
    double dist = Math.sqrt(x * x + y * y + z * z);
    normals[0] = x / dist;
    normals[1] = y / dist;
    normals[2] = z / dist;
  }

  public Vertex(Point3d origin, double x, double y, double z) {
    super(x, y, z);
    double dist = origin.distanceTo(x, y, z);
    normals[0] = (x - origin.getX()) / dist;
    normals[1] = (y - origin.getY()) / dist;
    normals[2] = (z - origin.getZ()) / dist;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public List<Face> getFaces() {
    return faces;
  }

  public double[] getNormals() {
    return normals;
  }

  public void setNormals(double[] normals) {
    this.normals = normals;
  }
}
