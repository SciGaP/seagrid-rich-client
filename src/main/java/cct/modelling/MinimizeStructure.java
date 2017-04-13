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
import cct.interfaces.ForceFieldInterface;
import cct.interfaces.MinimizeProgressInterface;
import cct.interfaces.MoleculeInterface;
import cct.math.DFPMin;
import cct.math.MinimizerInterface;
import cct.tools.Utils;
import cct.vecmath.VecmathTools;

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
public class MinimizeStructure {

   static final int OPT_CARTESIAN_COORD = 0;
   static final int OPT_INTERNAL_COORD = OPT_CARTESIAN_COORD + 1;
   static final Logger logger = Logger.getLogger(MinimizeStructure.class.getCanonicalName());

   protected FFMolecule ffMolecule = null;
   protected MoleculeInterface Molec = null;
   protected MinimizerInterface minimizer = null;
   MolecularEnergy molEnergy = null;
   int exitCode;
   MinimizeProgressInterface progressInterface = null;
   ForceFieldInterface forceField = null;

   float[] Gradients = null;
   float[] Variables = null;
   int[][] indexes = null;
   int nVar = 0;

   public MinimizeStructure(MoleculeInterface mol) {
      Molec = mol;
   }

   public MoleculeInterface getMolecule() {
      return Molec;
   }

   public void setMinimizer(MinimizerInterface min) {
      minimizer = min;
   }

   public void setForceField(ForceFieldInterface ff) {
      forceField = ff;
   }

   public void setMinimizeProgressInterface(MinimizeProgressInterface mpi) {
      this.progressInterface = mpi;
   }

   public float[][] getCoordinates(int n, float vars[]) {
      float[][] coords = molEnergy.getCoordinates(n, vars);
      return coords;
   }

   void setupMinimization() throws Exception {
      ffMolecule = new FFMolecule(Molec);
      if (forceField != null) {
         ffMolecule.applyForceField(forceField);
      }
      ffMolecule.formFFParameters();

      int dynamicAtoms = 0;
      for (int i = 0; i < Molec.getNumberOfAtoms(); i++) {
         AtomInterface atom = Molec.getAtomInterface(i);
         if (atom.isDynamic()) {
            ++dynamicAtoms;
         }
      }

      Gradients = new float[3 * dynamicAtoms];
      Variables = new float[3 * dynamicAtoms];
      indexes = new int[3 * dynamicAtoms][2];

      dynamicAtoms = 0;
      for (int i = 0; i < Molec.getNumberOfAtoms(); i++) {
         AtomInterface atom = Molec.getAtomInterface(i);
         if (atom.isDynamic()) {

            Variables[3 * dynamicAtoms] = atom.getX();
            Variables[3 * dynamicAtoms + 1] = atom.getY();
            Variables[3 * dynamicAtoms + 2] = atom.getZ();

            indexes[3 * dynamicAtoms][0] = i;
            indexes[3 * dynamicAtoms][1] = 0;
            indexes[3 * dynamicAtoms + 1][0] = i;
            indexes[3 * dynamicAtoms + 1][1] = 1;
            indexes[3 * dynamicAtoms + 2][0] = i;
            indexes[3 * dynamicAtoms + 2][1] = 2;
            ++dynamicAtoms;
         }
      }

      nVar = 3 * dynamicAtoms;

      /*
             for (int i = 0; i < nVar; i++) {
         logger.info( (i + 1) + " " + Variables[i] + " Atom: " +
                            indexes[i][0] + " var: " + indexes[i][1]);
             }
       */

      logger.info("Number of variables: " + nVar + " # of atoms: " +
                         Molec.getNumberOfAtoms());
   }

   public int minimize() throws Exception {
      if (minimizer == null) {
         throw new Exception("Minimizer is not set");
      }

      if (progressInterface != null) {
         minimizer.setMinimizeProgressInterface(progressInterface);
      }

      setupMinimization();

      molEnergy = new MolecularEnergy(ffMolecule);
      molEnergy.setIndexes(indexes);

      exitCode = minimizer.minimizeFunction(nVar, Variables, molEnergy);
      printMinimizationSummary();
      return exitCode;
   }

   public void printMinimizationSummary() {
      logger.info(minimizer.getExitCodeDescription(exitCode));

      logger.info("RMS of gradients : " +
                         VecmathTools.getRMS(minimizer.getGradients()) +
                         " Maximum gradient: " +
                         VecmathTools.getMaxAbsValue(minimizer.getGradients()));
      molEnergy.printEnergyDecomposition();
   }

   public int minimize(int method) throws Exception {

      setupMinimization();

      molEnergy = new MolecularEnergy(ffMolecule);
      molEnergy.setIndexes(indexes);

      if (method == 1) {
         DFPMin minimizer = new DFPMin();
         minimizer.setMaximumStep(0.5);
         minimizer.setGTolerance(0.01f);
         minimizer.minimizeFunction(nVar, Variables, molEnergy);
      }

      /*
             if ( method == 0 ) {
         Amoeba simplex2 = new Amoeba();
         int cond = simplex2.amoeba(Variables, nVar, 0.01f, me);
             }
       */


      /*
       Simplex simplex = new Simplex(nVar);
       simplex.DownhillSimplex(Variables,nVar,0.01f,me,500, 0.1f);
       */

      logger.info("Minimum function value: " +
                         molEnergy.function(nVar, Variables));

      if (method == 0) {
         molEnergy.enable14Energy(true);
         molEnergy.enable1NonbondEnergy(true);
         molEnergy.enableBondStretchEnergy(false);
         molEnergy.enableAngleBendEnergy(false);
         molEnergy.enableTorsionEnergy(false);

         molEnergy.enableNumericalGradients(true);
         molEnergy.function(nVar, Variables, Gradients);
         molEnergy.enableNumericalGradients(false);
         float g[] = new float[nVar];
         molEnergy.function(nVar, Variables, g);
         Utils.printFloatVectors(nVar, Gradients, g);

         molEnergy.printEnergyDecomposition();

         molEnergy.enable14Energy(true);
         molEnergy.enable1NonbondEnergy(true);
         molEnergy.enableAngleBendEnergy(true);
         molEnergy.enableBondStretchEnergy(true);
         molEnergy.enableTorsionEnergy(true);
      }

      return 0;
   }

   /*
       public static void main(String[] args) {
      MinimizeStructure minimizestructure = new MinimizeStructure();
       }
    */
}
