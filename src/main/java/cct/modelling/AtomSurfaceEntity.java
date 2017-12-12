/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.modelling;

import cct.interfaces.AtomInterface;

/**
 *
 * @author Vlad
 */
public class AtomSurfaceEntity {

  private AtomInterface atom;
  private double C = 0, A = 0;

  public AtomSurfaceEntity(AtomInterface atom, double A, double C) {
    this.atom = atom;
    this.A = A;
    this.C = C;
  }

  public double getA() {
    return A;
  }

  public void setA(double A) {
    this.A = A;
  }

  public double getC() {
    return C;
  }

  public void setC(double C) {
    this.C = C;
  }

  public AtomInterface getAtom() {
    return atom;
  }

  public void setAtom(AtomInterface atom) {
    this.atom = atom;
  }
}
