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
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.vecmath.Point3f;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class AngleBendEnergy {

  static final Logger logger = Logger.getLogger(AngleBendEnergy.class.getCanonicalName());

  private AngleBendEnergy() {
  }

  public static double angleBendEnergy(MoleculeInterface molec,
      Object AngleBendInteractions[]) {
    return angleBendEnergy(molec, AngleBendInteractions, null);
  }

  public static double angleBendEnergy(MoleculeInterface molec, Object AngleBendInteractions[],
      Point3f[] Gradients) {
    double energy = 0;

    Integer intType = (Integer) AngleBendInteractions[0];

    // --- Harmonic angle bend energy without gradients
    if (intType == FFMolecule.HARMONIC_BOND_TERM && Gradients == null) {
      AngleBendsArray angles = (AngleBendsArray) AngleBendInteractions[1];

      energy += HarmonicBendEnergy(molec, angles);
    } // --- Harmonic bond stretch energy with gradients
    else if (intType == FFMolecule.HARMONIC_BOND_TERM) {
      AngleBendsArray angles = (AngleBendsArray) AngleBendInteractions[1];

      energy += HarmonicStretchEnergy(molec, angles, Gradients);
    }

    return energy;
  }

  /**
   * Calculates harmonic angle bend energy
   *
   * @param molec MoleculeInterface
   * @param angles AngleBendsArray
   * @return double
   */
  public static double HarmonicBendEnergy(MoleculeInterface molec, AngleBendsArray angles) {
    double energy = 0;

    List triples = angles.getAngleBends();

    for (int i = 0; i < triples.size(); i++) {
      AngleBend ab = (AngleBend) triples.get(i);
      AtomInterface a1 = molec.getAtomInterface(ab.getI());
      AtomInterface a2 = molec.getAtomInterface(ab.getJ());
      AtomInterface a3 = molec.getAtomInterface(ab.getK());

      double dif = Point3f.angleBetween(a1, a2, a3) - ab.Theta;
      energy += ab.Fk * dif * dif;
    }

    return 0.5 * energy;
  }

  public static double HarmonicBendEnergyAnalysis(MoleculeInterface molec,
      AngleBendsArray angles, float threshold) {
    double energy = 0;

    List triples = angles.getAngleBends();

    for (int i = 0; i < triples.size(); i++) {
      AngleBend ab = (AngleBend) triples.get(i);
      AtomInterface a1 = molec.getAtomInterface(ab.getI());
      AtomInterface a2 = molec.getAtomInterface(ab.getJ());
      AtomInterface a3 = molec.getAtomInterface(ab.getK());

      double dif = Point3f.angleBetween(a1, a2, a3) - ab.Theta;
      double en = 0.5 * ab.Fk * dif * dif;
      if (en >= threshold) {
        logger.info(a1.getName() + "(" + (ab.getI() + 1) + ")-"
            + a2.getName() + "(" + (ab.getJ() + 1) + ")-"
            + a3.getName() + "(" + (ab.getK() + 1)
            + ") Energy: "
            + en + " R="
            + Point3f.angleBetween(a1, a2, a3)
            * Point3f.RADIANS_TO_DEGREES);

      }
      energy += en;
    }

    return 0.5 * energy;
  }

  /**
   * Calculates Bond stretch energy with gradients
   *
   * @param molec MoleculeInterface
   * @param bondPairs BondStretchPairs
   * @param Gradients Point3f[] - Final gradients
   * @return double
   */
  public static double HarmonicStretchEnergy(MoleculeInterface molec, AngleBendsArray angles, Point3f[] Gradients) {
    double energy = 0;

    List triples = angles.getAngleBends();

    for (int i = 0; i < triples.size(); i++) {
      AngleBend ab = (AngleBend) triples.get(i);
      AtomInterface a1 = molec.getAtomInterface(ab.getI());
      AtomInterface a2 = molec.getAtomInterface(ab.getJ());
      AtomInterface a3 = molec.getAtomInterface(ab.getK());

      /*
                xyzvector<T> a, b, c1, c2;
               a = v1 - v2;
                 asq = a.normsq();
                 anorm = sqrt(asq);

               b = v3 - v2;
                 bsq = b.normsq();
                 bnorm = sqrt(bsq);

               ab = a * b;

               c1 = RAD2DEG * (a * ab - b * asq).normalize() / anorm;
               c2 = RAD2DEG * (b * ab - a * bsq).normalize() / bnorm;

               g1 = c1;
               g2 = -(c1 + c2);
               g3 = c2;

               return ( RAD2DEG * acos( ab / (a.norm() * b.norm()) ) );
       }

       */
      float v1_x = a1.getX() - a2.getX();
      float v1_y = a1.getY() - a2.getY();
      float v1_z = a1.getZ() - a2.getZ();

      float v2_x = a3.getX() - a2.getX();
      float v2_y = a3.getY() - a2.getY();
      float v2_z = a3.getZ() - a2.getZ();

      float v12 = v1_x * v2_x + v1_y * v2_y + v1_z * v2_z;

      float v1_normsq = v1_x * v1_x + v1_y * v1_y + v1_z * v1_z;
      float v1_norm = (float) Math.sqrt(v1_normsq);

      float v2_normsq = v2_x * v2_x + v2_y * v2_y + v2_z * v2_z;
      float v2_norm = (float) Math.sqrt(v2_normsq);

      float c1_x = v1_x * v12 - v2_x * v1_normsq;
      float c1_y = v1_y * v12 - v2_y * v1_normsq;
      float c1_z = v1_z * v12 - v2_z * v1_normsq;
      float c1_norm = (float) Math.sqrt(c1_x * c1_x + c1_y * c1_y
          + c1_z * c1_z);

      float factor = 1 / (c1_norm * v1_norm);
      c1_x *= factor;
      c1_y *= factor;
      c1_z *= factor;

      float c2_x = v2_x * v12 - v1_x * v2_normsq;
      float c2_y = v2_y * v12 - v1_y * v2_normsq;
      float c2_z = v2_z * v12 - v1_z * v2_normsq;
      float c2_norm = (float) Math.sqrt(c2_x * c2_x + c2_y * c2_y
          + c2_z * c2_z);

      factor = 1 / (c2_norm * v2_norm);
      c2_x *= factor;
      c2_y *= factor;
      c2_z *= factor;

      double cosa = ((double) (v1_x * v2_x + v1_y * v2_y + v1_z * v2_z)
          / (v1_norm * v2_norm));

      float angle = 0;
      if (cosa > 1.0) {
        angle = 0;
      } else if (cosa < -1.0) {
        angle = Point3f.PI_FLOAT;
      } else {
        angle = (float) Math.acos(cosa);
      }

      float dif = angle - ab.Theta;
      float fac = ab.Fk * dif;

      energy += ab.Fk * dif * dif;

      Gradients[ab.getI()].add(fac * c1_x, fac * c1_y, fac * c1_z);
      Gradients[ab.getJ()].add(-fac * (c1_x + c2_x), -fac * (c1_y + c2_y),
          -fac * (c1_z + c2_z));
      Gradients[ab.getK()].add(fac * c2_x, fac * c2_y, fac * c2_z);

      /*
                fac = p->keq * dif;
                       atom[p->i].grad += fac * dphidri;
                       atom[p->j].grad += fac * dphidrj;
                       atom[p->k].grad += fac * dphidrk;

       */
    }

    return 0.5 * energy;
  }

}
