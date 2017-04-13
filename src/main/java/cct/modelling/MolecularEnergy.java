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

import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.math.MinimizedFunctionInterface;
import cct.vecmath.Point3f;
import java.util.List;

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
public class MolecularEnergy
        implements MinimizedFunctionInterface {

  static final public int SFF_PARAMETERS = 0;
  static final Logger logger = Logger.getLogger(MolecularEnergy.class.getCanonicalName());
  protected boolean gradAllocated = false;
  protected FFMolecule ffMolecule = null;
  protected Point3f[] Gradients = null;
  protected int dynamicAtoms = 0;
  protected double totalEnergy = 0;
  protected double bondStretchEnergy = 0;
  protected double angleBendEnergy = 0;
  protected double torsionEnergy = 0;
  protected double nonbond_1_4_Energy = 0;
  protected double nonbondEnergy = 0;
  protected double surfaceAtomEnergy = 0;
  protected boolean calculateBondStretchEnergy = true;
  protected boolean calculateAngleBendEnergy = true;
  protected boolean calculateTorsionEnergy = true;
  protected boolean calculate14Energy = true;
  protected boolean calculateNonbondEnergy = true;
  protected boolean calculateAtomSurfaceEnergy = true;
  boolean numericalGradients = false;
  // Related to the MinimizedFunctionInterface interface
  protected int indexes[][] = null;

  public MolecularEnergy(FFMolecule ffmol) {
    ffMolecule = ffmol;
    gradAllocated = false;
    Point3f Gradients = null;
  }

  public void enableNumericalGradients(boolean enable) {
    numericalGradients = enable;
  }

  public void enableBondStretchEnergy(boolean enable) {
    calculateBondStretchEnergy = enable;
  }

  public void enableAngleBendEnergy(boolean enable) {
    calculateAngleBendEnergy = enable;
  }

  public void enableTorsionEnergy(boolean enable) {
    calculateTorsionEnergy = enable;
  }

  public void enable14Energy(boolean enable) {
    calculate14Energy = enable;
  }

  public void enable1NonbondEnergy(boolean enable) {
    calculateNonbondEnergy = enable;
  }

  public Point3f[] getGradients() {
    return Gradients;
  }

  public void setIndexes(int[][] ind) {
    indexes = ind;
  }

  public double calculateEnergy(boolean andGrads) throws Exception {

    if (ffMolecule == null || ffMolecule.getMolecule() == null) {
      throw new Exception("calculateEnergy: ffMolecule == null || ffMolecule.getMolecule() == null");
    }

    totalEnergy = 0;

    MoleculeInterface molec = ffMolecule.getMolecule();

    // --- Allocate gradients (only once)

    if (andGrads && (!gradAllocated)) {
      dynamicAtoms = 0;
      for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
        AtomInterface atom = molec.getAtomInterface(i);
        if (atom.isDynamic()) {
          ++dynamicAtoms;
        }
      }

      if (dynamicAtoms < 1) {
        throw new Exception("calculateEnergy: no dynamic atoms in molecule");
      }

      /*
       * Gradients = new Point3f[dynamicAtoms]; for (int i = 0; i < dynamicAtoms; i++) { Gradients[i] = new Point3f(0, 0, 0); }
       */

      Gradients = new Point3f[molec.getNumberOfAtoms()];
      for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
        Gradients[i] = new Point3f(0, 0, 0);
      }

    }

    // --- Clear up gradients (if needed)

    if (andGrads) {
      for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
        Gradients[i].setXYZ(0, 0, 0);
      }
    }

    // --- Start to calculate energy (and gradients)

    // --- Bond stretch energy
    bondStretchEnergy = 0;
    if (calculateBondStretchEnergy) {
      Object BondStretchInteractions[] = ffMolecule.getBondStretchInteractions();
      if (andGrads) {
        bondStretchEnergy =
                BondStretchEnergy.bondStretchEnergy(molec,
                BondStretchInteractions,
                Gradients);
      } else {
        bondStretchEnergy =
                BondStretchEnergy.bondStretchEnergy(molec,
                BondStretchInteractions);
      }

      totalEnergy += bondStretchEnergy;
    }

    // --- Angle bend energy

    angleBendEnergy = 0;
    if (calculateAngleBendEnergy) {
      Object AngleBendInteractions[] = ffMolecule.getAngleBendInteractions();
      if (andGrads) {
        angleBendEnergy =
                AngleBendEnergy.angleBendEnergy(molec,
                AngleBendInteractions,
                Gradients);
      } else {
        angleBendEnergy =
                AngleBendEnergy.angleBendEnergy(molec,
                AngleBendInteractions);
      }
      totalEnergy += angleBendEnergy;
    }

    // --- Torsion energy

    torsionEnergy = 0;
    if (calculateTorsionEnergy && ffMolecule.getTorsionInteractions() != null) {
      if (andGrads) {
        torsionEnergy =
                TorsionEnergy.TorsionEnergy(molec,
                ffMolecule.getTorsionInteractions(),
                Gradients);
      } else {
        torsionEnergy =
                TorsionEnergy.TorsionEnergy(molec,
                ffMolecule.getTorsionInteractions());
      }

      totalEnergy += torsionEnergy;
    }

    // --- 1-4 Nonbonded energy

    nonbond_1_4_Energy = 0;
    if (calculate14Energy && ffMolecule.isCalculate14()
            && ffMolecule.get14Interactions() != null) {

      if (andGrads) {
        nonbond_1_4_Energy = NonbondedEnergy.LennardJonesEnergy(ffMolecule.getMolecule(), ffMolecule.get14Interactions(), ffMolecule.SCNB,
                Gradients);
      } else {
        nonbond_1_4_Energy = NonbondedEnergy.LennardJonesEnergy(ffMolecule.getMolecule(), ffMolecule.get14Interactions(), ffMolecule.SCNB);
      }
    }
    totalEnergy += nonbond_1_4_Energy;

    // --- Nonbonded energy

    nonbondEnergy = 0;
    if (calculateNonbondEnergy && ffMolecule.getNonbondInteractions() != null) {
      if (andGrads) {
        nonbondEnergy = NonbondedEnergy.LennardJonesEnergy(ffMolecule.getMolecule(), ffMolecule.getNonbondInteractions(), 1.0,
                Gradients);
      } else {
        nonbondEnergy = NonbondedEnergy.LennardJonesEnergy(ffMolecule.getMolecule(), ffMolecule.getNonbondInteractions(), 1.0);
      }
    }
    totalEnergy += nonbondEnergy;

    // --- Atom-Surface Energy

    surfaceAtomEnergy = 0;
    List<MolecularPlane> surfaces = ffMolecule.getSurfaces();
    if (calculateAtomSurfaceEnergy && surfaces != null && surfaces.size() > 0) {
      if (andGrads) {
        surfaceAtomEnergy = NonbondedEnergy.LennardJonesSurfaceEnergy(ffMolecule.getMolecule(), surfaces, Gradients);
      } else {
        surfaceAtomEnergy = NonbondedEnergy.LennardJonesSurfaceEnergy(ffMolecule.getMolecule(), surfaces);
      }
      totalEnergy += surfaceAtomEnergy;
    }

    return totalEnergy;
  }

  public void printEnergyDecomposition() {
    System.out.println("\nEnergy Decomposition:");
    /*
     * printf("\n BOND ENERGY: %12.4lf",energy12); printf("\n ANGLE ENERGY: %12.4lf",energy13); printf("\n TORSION ENERGY:
     * %12.4lf",etor); printf("\n IMPROPER TORSION ENERGY: %12.4lf",eimptor); if ( N_dist_constr ) printf("\n CONSTRAIN DISTANCE
     * ENERGY: %12.4lf",DistConstrEnergy); if ( N_Space_Constr ) printf("\n SPACE CONSTRAIN ENERGY: %12.4lf",SpaceConstr_Energy); if
     * ( N_TorsConstr) printf("\n CONSTRAIN TORSION ENERGY: %12.4lf",TorsConstrEnergy); if ( N_Range_Constr ) printf("\n RANGE
     * CONSTRAIN ENERGY: %12.4lf",RangeConstr_Energy); printf("\n"); printf("\n NB VDW ENERGY: %12.4lf",vdw15); printf("\n NB EL/ST
     * ENERGY: %12.4lf",elec15); printf("\n 1-4 NB VDW ENERGY: %12.4lf",vdw14); printf("\n 1-4 NB EL/ST ENERGY: %12.4lf",elec14);
     *
     */

    if (calculateBondStretchEnergy) {
      System.out.println(String.format(
              "     Bond Energy:                  %12.4f",
              bondStretchEnergy));
    }
    if (calculateAngleBendEnergy) {
      System.out.println(String.format(
              "     Angle Energy:                 %12.4f", angleBendEnergy));
    }

    if (calculateTorsionEnergy) {
      System.out.println(String.format(
              "     Torsion Energy:               %12.4f", torsionEnergy));
    }

    if (ffMolecule.isCalculate14()) {
      System.out.println(String.format(
              "     1-4 NB VDW ENERGY:            %12.4f", nonbond_1_4_Energy));
    }

    System.out.println(String.format(
            "     NB VDW ENERGY:                %12.4f", nonbondEnergy));


    if (calculateAtomSurfaceEnergy) {
      System.out.println(String.format(
              "     SURFACE ENERGY:                %12.4f", this.surfaceAtomEnergy));
    }

// --- And now the total energy
    for (int i = 0; i < 8; i++) {
      System.out.print("--------");
    }
    System.out.print("\n");
    System.out.println(String.format(
            "\n     Total Energy:                 %12.4f", totalEnergy));

    Object obj[] = ffMolecule.BondStretchInteractions;
    BondStretchPairs bp = (BondStretchPairs) obj[1];
    BondStretchEnergy.HarmonicStretchEnergyAnalysis(ffMolecule.getMolecule(),
            bp, 10);

    obj = ffMolecule.AngleBendInteractions;
    AngleBendsArray at = (AngleBendsArray) obj[1];
    AngleBendEnergy.HarmonicBendEnergyAnalysis(ffMolecule.getMolecule(), at,
            10);

  }

  // --- Implementation of MinimizedFunctionInterface interface
  @Override
  public double function(int n, double[] X, double[] Grads) {
    return 0;
  }

  @Override
  public float function(int n, float[] X, float[] Grads) {

    float energy = 0;
    try {
      // --- Numerical gradients
      if (numericalGradients) {
        float delta = 0.0001f;
        energy = (float) calculateEnergy(false);
        for (int i = 0; i < n; i++) {
          float store = X[i];
          X[i] += delta;
          setupCoordinates(n, X);
          float energyDelta = (float) calculateEnergy(false);
          Grads[i] = (energyDelta - energy) / delta;
          if (energyDelta < energy) {
            energy = energyDelta;
          } else {
            X[i] = store;
          }
        }
      } // --- Analytical gradients
      else {
        energy = (float) calculateEnergy(true);
        setupGradinents(n, Grads);
      }
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }

    return energy;
  }

  @Override
  public double function(int n, double[] X) {
    return 0;
  }

  @Override
  public float function(int n, float[] X) {

    setupCoordinates(n, X);
    float energy = 0;
    try {
      energy = (float) calculateEnergy(false);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
    logger.info("function= " + energy);
    return energy;
  }

  void setupCoordinates(int n, float[] X) {
    MoleculeInterface molec = ffMolecule.getMolecule();
    for (int i = 0; i < n; i++) {
      int at_n = indexes[i][0];
      int var = indexes[i][1];

      AtomInterface atom = molec.getAtomInterface(at_n);
      switch (var) {
        case 0:
          atom.setX(X[i]);
          break;
        case 1:
          atom.setY(X[i]);
          break;
        case 2:
          atom.setZ(X[i]);
          break;
      }
    }
  }

  public float[][] getCoordinates(int n, float[] X) {

    MoleculeInterface molec = ffMolecule.getMolecule();
    float[][] coords = new float[molec.getNumberOfAtoms()][3];

    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      coords[i][0] = atom.getX();
      coords[i][1] = atom.getY();
      coords[i][2] = atom.getZ();
    }

    for (int i = 0; i < n; i++) {
      int at_n = indexes[i][0];
      int var = indexes[i][1];

      AtomInterface atom = molec.getAtomInterface(at_n);
      switch (var) {
        case 0:
          coords[i][0] = X[i];
          break;
        case 1:
          coords[i][1] = X[i];
          break;
        case 2:
          coords[i][2] = X[i];
          break;
      }
    }

    return coords;
  }

  void setupGradinents(int n, float[] Grads) {
    MoleculeInterface molec = ffMolecule.getMolecule();
    for (int i = 0; i < n; i++) {
      int at_n = indexes[i][0];
      int var = indexes[i][1];

      AtomInterface atom = molec.getAtomInterface(at_n);
      switch (var) {
        case 0:
          Grads[i] = Gradients[at_n].getX();
          break;
        case 1:
          Grads[i] = Gradients[at_n].getY();
          break;
        case 2:
          Grads[i] = Gradients[at_n].getZ();
          break;
      }
    }

  }

  public boolean isCalculateAtomSurfaceEnergy() {
    return calculateAtomSurfaceEnergy;
  }

  public void setCalculateAtomSurfaceEnergy(boolean calculateAtomSurfaceEnergy) {
    this.calculateAtomSurfaceEnergy = calculateAtomSurfaceEnergy;
  }
}
