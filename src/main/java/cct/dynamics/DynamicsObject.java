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
package cct.dynamics;

import cct.modelling.*;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.ForceFieldInterface;
import cct.interfaces.MinimizeProgressInterface;
import cct.interfaces.MoleculeInterface;
import cct.math.DFPMin;
import cct.math.MinimizerInterface;
import cct.tools.Utils;
import cct.vecmath.Point3d;
import cct.vecmath.VecmathTools;
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
public class DynamicsObject implements ForcesInterface {

  static final Logger logger = Logger.getLogger(DynamicsObject.class.getCanonicalName());
  protected FFMolecule ffMolecule = null;
  protected MoleculeInterface Molec = null;
  protected Dynamics integrator = null;
  MolecularEnergy molEnergy = null;
  int exitCode;
  DynamicsProgressInterface progressInterface = null;
  ForceFieldInterface forceField = null;

  public DynamicsObject(MoleculeInterface mol) {
    Molec = mol;
  }

  public MoleculeInterface getMolecule() {
    return Molec;
  }

  public void setForceField(ForceFieldInterface ff) {
    forceField = ff;
  }

  public void setDynamicsProgressInterface(DynamicsProgressInterface mpi) {
    this.progressInterface = mpi;
  }

  void setupDynamics() throws Exception {
    ffMolecule = new FFMolecule(Molec);
    if (forceField != null) {
      ffMolecule.applyForceField(forceField);
    }
    ffMolecule.formFFParameters();

    integrator = new Dynamics(Molec);
    integrator.setForces(this);

    int dynamicAtoms = 0;
    for (int i = 0; i < Molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = Molec.getAtomInterface(i);
      if (atom.isDynamic()) {
        ++dynamicAtoms;
      }
    }

    logger.info("Number of dynamic atoms: " + dynamicAtoms + " # of atoms: "
            + Molec.getNumberOfAtoms());
  }

  public int integrate() throws Exception {

    if (progressInterface != null) {
      integrator.setProgressInterface(progressInterface);
    }

    setupDynamics();

    molEnergy = new MolecularEnergy(ffMolecule);

    integrator.start();
    //printMinimizationSummary();
    return exitCode;
  }

  public void printMinimizationSummary() {
    /*
     * logger.info(minimizer.getExitCodeDescription(exitCode));
     *
     * logger.info("RMS of gradients : " + VecmathTools.getRMS(minimizer.getGradients()) + " Maximum gradient: " +
     * VecmathTools.getMaxAbsValue(minimizer.getGradients()));
     */
    molEnergy.printEnergyDecomposition();
  }

  /*
   * public int minimize(int method) throws Exception {
   *
   * setupMinimization();
   *
   * molEnergy = new MolecularEnergy(ffMolecule); molEnergy.setIndexes(indexes);
   *
   * if (method == 1) { DFPMin minimizer = new DFPMin(); minimizer.setMaximumStep(0.5); minimizer.setGTolerance(0.01f);
   * minimizer.minimizeFunction(nVar, Variables, molEnergy); }
   *
   *
   * logger.info("Minimum function value: " + molEnergy.function(nVar, Variables));
   *
   * if (method == 0) { molEnergy.enable14Energy(true); molEnergy.enable1NonbondEnergy(true);
   * molEnergy.enableBondStretchEnergy(false); molEnergy.enableAngleBendEnergy(false); molEnergy.enableTorsionEnergy(false);
   *
   * molEnergy.enableNumericalGradients(true); molEnergy.function(nVar, Variables, Gradients);
   * molEnergy.enableNumericalGradients(false); float g[] = new float[nVar]; molEnergy.function(nVar, Variables, g);
   * Utils.printFloatVectors(nVar, Gradients, g);
   *
   * molEnergy.printEnergyDecomposition();
   *
   * molEnergy.enable14Energy(true); molEnergy.enable1NonbondEnergy(true); molEnergy.enableAngleBendEnergy(true);
   * molEnergy.enableBondStretchEnergy(true); molEnergy.enableTorsionEnergy(true); }
   *
   * return 0; }
   */
  public double forces(Point3d[] grad) {
    double energy = 0;
    return energy;
  }

  /*
   * public static void main(String[] args) { MinimizeStructure minimizestructure = new MinimizeStructure(); }
   */
}
