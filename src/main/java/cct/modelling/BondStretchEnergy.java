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
public class BondStretchEnergy {
  static final Logger logger = Logger.getLogger(BondStretchEnergy.class.getCanonicalName());

   private BondStretchEnergy() {
   }

   public static double bondStretchEnergy(MoleculeInterface molec,
                                          Object BondStretchInteractions[]) {
      return bondStretchEnergy(molec, BondStretchInteractions, null);
   }

   public static double bondStretchEnergy(MoleculeInterface molec,
                                          Object BondStretchInteractions[],
                                          Point3f[] Gradients) {
      double energy = 0;

      Integer intType = (Integer) BondStretchInteractions[0];

      // --- Harmonic bond stretch energy without gradients
      if (intType == FFMolecule.HARMONIC_BOND_TERM && Gradients == null) {
         BondStretchPairs bondPairs = (BondStretchPairs)
             BondStretchInteractions[1];

         energy += HarmonicStretchEnergy(molec, bondPairs);
      }

      // --- Harmonic bond stretch energy with gradients
      else if (intType == FFMolecule.HARMONIC_BOND_TERM) {
         BondStretchPairs bondPairs = (BondStretchPairs)
             BondStretchInteractions[1];

         energy += HarmonicStretchEnergy(molec, bondPairs, Gradients);
      }

      return energy;
   }

   /**
    * Calculates Bond stretch energy without gradients
    * @param molec MoleculeInterface - molecule
    * @param bondPairs BondStretchPairs - bond stretch parameters
    * @return double - Energy
    */
   public static double HarmonicStretchEnergy(MoleculeInterface molec,
                                              BondStretchPairs bondPairs) {
      double energy = 0;

      List pairs = bondPairs.getBondStretchPairs();

      for (int i = 0; i < pairs.size(); i++) {
         HarmonicBondStretch hbs = (HarmonicBondStretch) pairs.get(i);
         AtomInterface a1 = molec.getAtomInterface(hbs.i);
         AtomInterface a2 = molec.getAtomInterface(hbs.j);

         double dif = Point3f.distance(a1, a2) - hbs.R0;
         energy += hbs.Fk * dif * dif;
      }

      return 0.5 * energy;
   }

   public static double HarmonicStretchEnergyAnalysis(MoleculeInterface molec,
       BondStretchPairs bondPairs, float threshold) {
      double energy = 0;

      List pairs = bondPairs.getBondStretchPairs();

      for (int i = 0; i < pairs.size(); i++) {
         HarmonicBondStretch hbs = (HarmonicBondStretch) pairs.get(i);
         AtomInterface a1 = molec.getAtomInterface(hbs.i);
         AtomInterface a2 = molec.getAtomInterface(hbs.j);

         double dif = Point3f.distance(a1, a2) - hbs.R0;
         double en = 0.5 * hbs.Fk * dif * dif;
         if (en >= threshold) {
            logger.info(a1.getName() + "(" + (hbs.i + 1) + ")-" +
                               a2.getName() + "(" + (hbs.j + 1) + ") Energy: " +
                               en + " R=" + Point3f.distance(a1, a2));
         }
         energy += en;
      }

      return 0.5 * energy;
   }

   /**
    * Calculates Bond stretch energy with gradients
    * @param molec MoleculeInterface
    * @param bondPairs BondStretchPairs
    * @param Gradients Point3f[] - Final gradients
    * @return double
    */
   public static double HarmonicStretchEnergy(MoleculeInterface molec,
                                              BondStretchPairs bondPairs,
                                              Point3f[] Gradients) {
      double energy = 0;

      List pairs = bondPairs.getBondStretchPairs();
      Point3f rijvec = new Point3f();

      for (int i = 0; i < pairs.size(); i++) {
         HarmonicBondStretch hbs = (HarmonicBondStretch) pairs.get(i);
         AtomInterface a1 = molec.getAtomInterface(hbs.i);
         AtomInterface a2 = molec.getAtomInterface(hbs.j);

         rijvec.subtract(a1, a2);
         double r = rijvec.vectorNorm();
         double dif = r - hbs.R0;
         energy += hbs.Fk * dif * dif;

         rijvec.multiply(hbs.Fk * dif / r);
         Gradients[hbs.i].add(rijvec);
         Gradients[hbs.j].substract(rijvec);
      }

      return 0.5 * energy;
   }

}
