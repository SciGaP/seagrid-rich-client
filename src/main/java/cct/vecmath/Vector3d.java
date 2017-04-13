/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.vecmath;

/**
 *
 * @author vvv900
 */
public class Vector3d {

  private double[] origin = new double[]{0, 0, 0};
  private double[] dir = new double[]{0, 0, 1.0};

  public Vector3d() {
  }

  public Vector3d(double[] p1, double[] p2) {
    origin[0] = p1[0];
    origin[1] = p1[1];
    origin[2] = p1[2];
    dir[0] = p2[0] - p1[0];
    dir[1] = p2[1] - p1[1];
    dir[2] = p2[2] - p1[2];
  }

  public Vector3d(Point3f p1, Point3f p2) {
    origin[0] = p1.x;
    origin[1] = p1.y;
    origin[2] = p1.z;
    dir[0] = p2.x - p1.x;
    dir[1] = p2.y - p1.y;
    dir[2] = p2.z - p1.z;
  }

  public Vector3d(Point3d p1, Point3d p2) {
    origin[0] = p1.getX();
    origin[1] = p1.getY();
    origin[2] = p1.getZ();
    dir[0] = p2.getX() - p1.getX();
    dir[1] = p2.getY() - p1.getY();
    dir[2] = p2.getZ() - p1.getZ();
  }

  public void setDir(double xNewValue, double yNewValue, double zNewValue) {
    dir[0] = xNewValue;
    dir[1] = yNewValue;
    dir[2] = zNewValue;
  }

  public void setDirX(double newValue) {
    dir[0] = newValue;
  }

  public void setDirY(double newValue) {
    dir[1] = newValue;
  }

  public void setDirZ(double newValue) {
    dir[2] = newValue;
  }

  public double[] getDirection() {
    return dir;
  }

  public double getDirX() {
    return dir[0];
  }

  public double getDirY() {
    return dir[1];
  }

  public double getDirZ() {
    return dir[2];
  }

  public Vector3d crossProduct(Vector3d v) {
    Vector3d cross = new Vector3d();
    cross.setDir(
        dir[1] * v.getDirZ() - dir[2] * v.getDirY(),
        dir[2] * v.getDirX() - dir[0] * v.getDirZ(),
        dir[0] * v.getDirY() - dir[1] * v.getDirX());
    return cross;
  }
}
