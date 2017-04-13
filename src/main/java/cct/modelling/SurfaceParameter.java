/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.modelling;

/**
 *
 * @author Vlad
 */
public class SurfaceParameter {

  private double R = 1.0, W = 0.0;

  public SurfaceParameter(double R, double W) {
    this.R = R;
    this.W = W;
  }

  public double getR() {
    return R;
  }

  public void setR(double R) {
    this.R = R;
  }

  public double getW() {
    return W;
  }

  public void setW(double W) {
    this.W = W;
  }

  public void set(double R, double W) {
    this.R = R;
    this.W = W;
  }
}
