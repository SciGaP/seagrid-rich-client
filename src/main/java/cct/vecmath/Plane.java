/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.vecmath;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vlad
 */
public class Plane {
  
  public static final double HALF_PI = Math.PI / 2.0;
  private double A = 0, B = 0, C = 1, D = 0;
  private double[] unitNormal = new double[]{0, 0, 1.0};

  /**
   * General definition of a plane: Ax + By + Cz + D = 0
   *
   * @param A
   * @param B
   * @param C
   * @param D
   * @throws Exception
   */
  public Plane(double A, double B, double C, double D) throws Exception {
    setPlane(A, B, C, D);
  }
  
  public Plane(double[] x1, double[] x2, double[] x3) throws Exception {
    Vector3d v1 = new Vector3d(x1, x2);
    Vector3d v2 = new Vector3d(x1, x3);
    Vector3d cross = v1.crossProduct(v2);
    setPlane(x1, cross.getDirection());
  }
  
  public Plane(Point3f x1, Point3f x2, Point3f x3) throws Exception {
    Vector3d v1 = new Vector3d(x1, x2);
    Vector3d v2 = new Vector3d(x1, x3);
    Vector3d cross = v1.crossProduct(v2);
    setPlane(x1, cross.getDirection());
  }
  
  public Plane(double[] point, double[] vector) throws Exception {
    setPlane(point, vector);
  }
  
  public void setPlane(double A, double B, double C, double D) throws Exception {
    
    if (A == 0 && B == 0 && C == 0) {
      throw new Exception("A==0 && B==0 && C== 0");
    }
    
    double S = Math.sqrt(A * A + B * B + C * C);
    
    this.A = A / S;
    this.B = B / S;
    this.C = C / S;
    this.D = D / S;
    
    unitNormal[0] = this.A;
    unitNormal[1] = this.B;
    unitNormal[2] = this.C;
  }
  
  public void setPlane(double[] point, double[] vector) throws Exception {
    this.A = vector[0];
    this.B = vector[1];
    this.C = vector[2];
    this.D = -vector[0] * point[0] - vector[1] * point[1] - vector[2] * point[2];
    
    double S = Math.sqrt(A * A + B * B + C * C);
    
    this.A = A / S;
    this.B = B / S;
    this.C = C / S;
    this.D = D / S;
    
    unitNormal[0] = this.A;
    unitNormal[1] = this.B;
    unitNormal[2] = this.C;
  }
  
  public void setPlane(Point3d point, double[] vector) throws Exception {
    this.A = vector[0];
    this.B = vector[1];
    this.C = vector[2];
    this.D = -vector[0] * point.getX() - vector[1] * point.getY() - vector[2] * point.getZ();
    
    double S = Math.sqrt(A * A + B * B + C * C);
    
    this.A = A / S;
    this.B = B / S;
    this.C = C / S;
    this.D = D / S;
    
    unitNormal[0] = this.A;
    unitNormal[1] = this.B;
    unitNormal[2] = this.C;
  }
  
  public void setPlane(Point3f point, double[] vector) throws Exception {
    this.A = vector[0];
    this.B = vector[1];
    this.C = vector[2];
    this.D = -vector[0] * point.getX() - vector[1] * point.getY() - vector[2] * point.getZ();
    
    double S = Math.sqrt(A * A + B * B + C * C);
    
    this.A = A / S;
    this.B = B / S;
    this.C = C / S;
    this.D = D / S;
    
    unitNormal[0] = this.A;
    unitNormal[1] = this.B;
    unitNormal[2] = this.C;
  }
  
  public double distanceToPoint(double x, double y, double z) {
    return Math.abs(x * A + y * B + z * C + D);
  }
  
  public double distanceToPoint(double[] point) {
    return Math.abs(point[0] * A + point[1] * B + point[2] * C + D);
  }
  
  public double distanceToPoint(float x, float y, float z) {
    return Math.abs(x * A + y * B + z * C + D);
  }
  
  public double distanceToPointSigned(double x, double y, double z) {
    return x * A + y * B + z * C + D;
  }
  
  public double distanceToPointSigned(float x, float y, float z) {
    return x * A + y * B + z * C + D;
  }
  
  public boolean isNormal(Plane plane) {
    return A * plane.getA() + B * plane.getB() + C * plane.getC() == 0;
  }
  
  public boolean isNormal(Plane plane, double inaccuracy) {
    return Math.abs(A * plane.getA() + B * plane.getB() + C * plane.getC()) <= inaccuracy;
  }
  
  public double angle(Plane plane) throws Exception {
    
    double phi = (A * plane.getA() + B * plane.getB() + C * plane.getC());
    phi = Math.acos(phi);
    if (phi > HALF_PI) {
      phi = Math.PI - phi;
    }
    return phi;
  }
  
  public double getA() {
    return A;
  }
  
  public double getB() {
    return B;
  }
  
  public double getC() {
    return C;
  }
  
  public double getD() {
    return D;
  }
  
  public static void main(String[] args) {
    try {
      Plane plane_1 = new Plane(new double[]{0, 0, 0}, new double[]{1, 0, 0}, new double[]{0, 1, 0});
      Plane plane_2 = new Plane(new double[]{0, 0, 0}, new double[]{1, 0, 0}, new double[]{0, 0, 1});
      System.out.println("Angle: " + Math.toDegrees(plane_1.angle(plane_2)));
      //
      plane_1 = new Plane(5, 7, 6, 8);
      plane_2 = new Plane(9, 4, 3, 2);
      double[] point = new double[3];
      
      System.out.println("Angle: " + Math.toDegrees(plane_1.angle(plane_2))
          + " Ref: " + Math.toDegrees(Math.acos(0.8427371072309235)));
      System.out.println("Distance from point (0,0,0): Plane_1: " + plane_1.distanceToPoint(point)
          + " Ref: 0.7627700713964738 " + "Plane_2: " + plane_2.distanceToPoint(point) + " Ref: 0.19425717247145283");
    } catch (Exception ex) {
      Logger.getLogger(Plane.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.exit(0);
  }
  
}
