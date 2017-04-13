/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dynamics;

import cct.vecmath.Point3d;
import java.util.List;

/**
 *
 * @author Vlad
 */
public interface ForcesInterface {
  double forces(Point3d[] grad);
}
