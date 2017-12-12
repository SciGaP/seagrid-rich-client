/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.modelling;

import cct.interfaces.AtomInterface;
import cct.vecmath.Plane;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vlad
 */
public class MolecularPlane extends Plane {

  private String name;
  private SurfaceParameter surfaceParameter;
  private List<AtomSurfaceEntity> atomList = new ArrayList<AtomSurfaceEntity>();

  public List<AtomSurfaceEntity> getAtomList() {
    return atomList;
  }

  public MolecularPlane(String name) throws Exception {
    super(0, 0, 1, 0);
    setName(name);
  }

  public MolecularPlane(double A, double B, double C, double D, String name) throws Exception {
    super(A, B, C, D);
    setName(name);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SurfaceParameter getSurfaceParameter() {
    return surfaceParameter;
  }

  public void setSurfaceParameter(SurfaceParameter surfaceParameter) {
    this.surfaceParameter = surfaceParameter;
  }

  public void setSurfaceParameter(double R, double W) {
    if (surfaceParameter == null) {
      surfaceParameter = new SurfaceParameter(R, W);
    } else {
      surfaceParameter.set(R, W);
    }
  }
}
