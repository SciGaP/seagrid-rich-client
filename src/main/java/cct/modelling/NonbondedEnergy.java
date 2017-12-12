/* ***** BEGIN LICENSE BLOCK *****
 Version: Apache 2.0/GPL 3.0/LGPL 3.0

 CCT - Computational Chemistry Tools
 Jamberoo - Java Molecules Editor

 Copyright 2008-2015 Dr. Vladislav Vasilyev

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Contributor(s):
 Dr. Vladislav Vasilyev <vvv900@gmail.com>       (original author)

 Alternatively, the contents of this file may be used under the terms of
 either the GNU General Public License Version 2 or later (the "GPL"), or
 the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 in which case the provisions of the GPL or the LGPL are applicable instead
 of those above. If you wish to allow use of your version of this file only
 under the terms of either the GPL or the LGPL, and not to allow others to
 use your version of this file under the terms of the Apache 2.0, indicate your
 decision by deleting the provisions above and replace them with the notice
 and other provisions required by the GPL or the LGPL. If you do not delete
 the provisions above, a recipient may use your version of this file under
 the terms of any one of the Apache 2.0, the GPL or the LGPL.

 ***** END LICENSE BLOCK *****/
package cct.modelling;

import java.util.List;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.vecmath.Point3f;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class NonbondedEnergy {

  private NonbondedEnergy() {
  }

  public static double LennardJonesEnergy(MoleculeInterface molec, List nonbodedPairs, double SCNB) {
    double energy = 0;
    for (int i = 0; i < nonbodedPairs.size(); i++) {
      NonbondedPair nb = (NonbondedPair) nonbodedPairs.get(i);
      AtomInterface a1 = molec.getAtomInterface(nb.getI());
      AtomInterface a2 = molec.getAtomInterface(nb.getJ());
      double Rsq = 1.0 / Point3f.distanceSquared(a1, a2);
      Rsq = Rsq * Rsq * Rsq;

      energy += (nb.A * Rsq - nb.C) * Rsq;
    }
    return SCNB * energy;
  }

  /**
   * Energy between atoms in molecule and surface(s)
   *
   * @param molec - Molecule
   * @param surfaces - Surfaces list
   * @param SCNB
   * @return - non-bonded energy
   */
  public static double LennardJonesSurfaceEnergy(MoleculeInterface molec, List<MolecularPlane> surfaces) {
    double energy = 0;
    for (MolecularPlane mp : surfaces) {
      List<AtomSurfaceEntity> atomList = mp.getAtomList();
      for (AtomSurfaceEntity ase : atomList) {
        AtomInterface atom = ase.getAtom();
        double dist = mp.distanceToPoint(atom.getX(), atom.getY(), atom.getZ());
        dist = Math.pow(1.0 / (dist * dist), 3);
        energy += (ase.getA() * dist - ase.getC()) * dist;
      }
    }
    return energy;
  }

  public static double LennardJonesEnergy(MoleculeInterface molec, List nonbodedPairs, double SCNB, Point3f[] Gradients) {
    double energy = 0;
    for (int k = 0; k < nonbodedPairs.size(); k++) {
      NonbondedPair nb = (NonbondedPair) nonbodedPairs.get(k);
      int i = nb.getI();
      int j = nb.getJ();
      AtomInterface a1 = molec.getAtomInterface(i);
      AtomInterface a2 = molec.getAtomInterface(j);

      float dx = a1.getX() - a2.getX();
      float dy = a1.getY() - a2.getY();
      float dz = a1.getZ() - a2.getZ();

      double R6 = dx * dx + dy * dy + dz * dz;
      //float r = (float)Math.sqrt(R6);
      double R2 = R6;
      R6 *= R6 * R6;
      float coeff = (float) (6.0 * SCNB * (-2.0 * (nb.A - nb.C * R6) - nb.C * R6) / (R6 * R6 * R2));

      Gradients[i].add(dx * coeff, dy * coeff, dz * coeff);
      Gradients[j].add(-dx * coeff, -dy * coeff, -dz * coeff);

      energy += (nb.A - nb.C * R6) / (R6 * R6);
    }
    return SCNB * energy;
  }

  public static double LennardJonesSurfaceEnergy(MoleculeInterface molec, List<MolecularPlane> surfaces, Point3f[] Gradients) {
    double energy = 0;

    for (MolecularPlane mp : surfaces) {
      List<AtomSurfaceEntity> atomList = mp.getAtomList();
      int count = 0;
      for (AtomSurfaceEntity ase : atomList) {
        AtomInterface atom = ase.getAtom();

        double distSigned = mp.distanceToPointSigned(atom.getX(), atom.getY(), atom.getZ());
        double dist = 1.0 / Math.abs(distSigned);
        double R6 = Math.pow(dist, 6);
        energy += (ase.getA() * R6 - ase.getC()) * R6;

        double deriv = 6.0 * (-2.0 * ase.getA() + R6 * ase.getC()) * R6 * R6 * dist;
        Gradients[count].add((float) (sign(mp.getA(), distSigned) * deriv),
                (float) (sign(mp.getB(), distSigned) * deriv),
                (float) (sign(mp.getC(), distSigned) * deriv));
        ++count;
      }
    }

    return energy;
  }

  private static double sign(double value, double sign) {
    return sign >= 0 ? value : -value;
  }
}
