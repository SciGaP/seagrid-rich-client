/* ***** BEGIN LICENSE BLOCK *****
 Version: Apache 2.0/GPL 3.0/LGPL 3.0

 CCT - Computational Chemistry Tools
 Jamberoo - Java Molecules Editor

 Copyright 2012 Dr. Vladislav Vasilyev

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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dynamics;

import cct.Constants;
import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.vecmath.Point3d;
import cct.vecmath.Point3f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vlad
 */
public class Dynamics {

  public enum INITIAL_VELOCITIES {

    AMeanDistr, Restart
  }

    public enum MDAlgorithm {

    HalfStepLeapFrog, VelocityVerlet
  }

    INITIAL_VELOCITIES InitVel = INITIAL_VELOCITIES.AMeanDistr;
  MDAlgorithm StepAlgorithm; // Defines a MD algorithm
  private double T0 = 298, Tinit = 298;      // - Reference and initial temperatures (in K)
  private static double ToA = Constants.FROM_CAL_TO_J * 100.0;
  private static double GasConst_kcal = Constants.GAS_CONSTANT / (4.1868 * 1000.);
  double delta_t; // - Time step length (in ps)
  double tauT;    // - Temperature Coupling Time
  private MoleculeInterface molecule;
  private List<AtomMD> dynamicAtoms;
  int StartStep, MaxSteps, CurrentStep;
  double Etot, Epot, Ekin, Tcurr;
  double DegreesOfFreedom = 0;
  private ForcesInterface forces;
  private boolean CheckTemp;
  private double HotSystemTempExceed = 100.0;
  Point3d[] grads = null;
  DynamicsProgressInterface progressInterface = null;

  public Dynamics(MoleculeInterface molecule) {
    this.molecule = molecule;
    dynamicAtoms = new ArrayList<AtomMD>(molecule.getNumberOfAtoms());
  }

  public static void main(String[] args) {
  }

  public void start() throws Exception {
    double coeff = 0.8 / Math.PI * Constants.GAS_CONSTANT * Tinit;
    double mean_vel, s;

    int natoms = molecule.getNumberOfAtoms();

    dynamicAtoms.clear();

    int number_of_interesting_atoms = 0;
    DegreesOfFreedom = (double) (3 * natoms);
    double Mass = 0.0;
    for (int j = 0; j < natoms; j++) {
      AtomInterface atom = molecule.getAtomInterface(j);
      if (atom.getAtomicMass() < 0.0 || !atom.isDynamic()) {
        DegreesOfFreedom -= 3.0;
      } else {
        AtomMD mda = new AtomMD(atom, j);
        dynamicAtoms.add(mda);
        Mass += atom.getAtomicMass();
        ++number_of_interesting_atoms;
      }
    }
    System.out.println("Number of interesting (dynamic) atoms " + number_of_interesting_atoms);

    grads = new Point3d[dynamicAtoms.size()];
    for (Point3d p : grads) {
      p = new Point3d();
    }

    //!!! if ( ImposeHolConstr ) DegreesOfFreedom -= (double)NHolConstr;

    DegreesOfFreedom -= 6.0;
    if (natoms == 1) {
      DegreesOfFreedom = 3.0;
    }
    if (natoms == 2) {
      DegreesOfFreedom = 6.0; // --- !!!
    }
    System.out.println("Number of degrees of freedom " + String.format("%10.0f", DegreesOfFreedom));
    System.out.println("Atomic mass of dynamic atoms " + String.format("%10.1f", Mass));
    if ((DegreesOfFreedom < 3) && (number_of_interesting_atoms > 0)) {
      DegreesOfFreedom = (double) (3 * number_of_interesting_atoms);
      System.out.println("Adjusted Number of degrees of freedom " + String.format("%10.0f", DegreesOfFreedom));
    } else if (DegreesOfFreedom < 3) {
      String msg = "Strange value of degrees of freedom: " + String.format("%10.0f", DegreesOfFreedom);
      System.err.println(msg);
      throw new Exception(msg);
    }

    if (progressInterface != null) {
      progressInterface.dynamicsStarted("Setting up initial velocities");
    }

    if (InitVel == INITIAL_VELOCITIES.AMeanDistr) {
      forces.forces(grads);

      int count = 0;
      for (AtomMD mda : dynamicAtoms) {
        Point3d gr = grads[count];
        ++count;

        mean_vel = Math.sqrt(coeff / mda.atom.getAtomicMass());  // velocity corresponding Tinit

        mda.vOld.setX(mean_vel * (2.0 * Math.random() - 1.0));
        mda.vOld.setY(Math.sqrt(mean_vel * mean_vel - mda.vOld.getX() * mda.vOld.getX()) * (2.0 * Math.random() - 1.0));
        mda.vOld.setZ(SIGN(Math.sqrt(mean_vel * mean_vel - mda.vOld.getX() * mda.vOld.getX() - mda.vOld.getY() * mda.vOld.getY()),
                2.0 * Math.random() - 1.0));

        mda.a.setX((float) (-ToA * gr.getX() / mda.atom.getAtomicMass()));
        mda.a.setY((float) (-ToA * gr.getY() / mda.atom.getAtomicMass()));
        mda.a.setZ((float) (-ToA * gr.getZ() / mda.atom.getAtomicMass()));

        mda.xOld.setXYZ(mda.atom);
      }

      /*
       * if (ExtendedPrint) { printf("\n\nInitial Accelerations:\n"); for (i = 0; i < natoms; i++) { printf("\n%3d %8.3lf %8.3lf
       * %8.3lf", i + 1, a[i].x, a[i].y, a[i].z); } }
       */

      /*
       * if (RemoveMomentum) { RemoveNetMomentum(xold, vold, amasses, int_reg, natoms); }
       */

      //ScaleVelocities(vold, amasses, int_reg, natoms, delta_t, delta_t, Tinit, DegreesOfFreedom);
      ScaleVelocities(dynamicAtoms, delta_t, delta_t, Tinit, DegreesOfFreedom);
    } else if (InitVel == INITIAL_VELOCITIES.Restart) {
      /*
       * ReadCoordAndVelocities(xold, vold, natoms); for (i = 0; i < natoms; i++) { xnew[i] = xold[i]; } } else { printf("\n\nDon't
       * know how to set initial velocities\n\n"); exit(1); }
       */
    }

    /*
     * if (ntwe > 0) { NTWE = fopen(mden_res_file, "wt"); assert (NTWE); }
     */

    double Etot_average = 0.0, Etot_square = 0.0, Etot_lowest = 0, Etot_highest = 0;
    double Epot_average = 0.0, Epot_square = 0.0, Epot_lowest = 0, Epot_highest = 0;
    double Ekin_average = 0.0, Ekin_square = 0.0, Ekin_lowest = 0, Ekin_highest = 0;
    double Temp_average = 0.0, Temp_square = 0.0, Temp_lowest = 0, Temp_highest = 0;
    double Finish_time = 0.0;
    double Elapsed_time = 0.0;

    int i = 0;
    for (i = StartStep; i < MaxSteps; i++) {
      CurrentStep = i;

      //!!!if ( Schedule ) SetSchedule(i);

      //!!!if ( NoSilent ) printf("\n\nStep: %d",i);

      switch (StepAlgorithm) {
        case HalfStepLeapFrog:
          //HalfStepLeapFrogStep(xnew, grad, amasses, int_reg, natoms, Forces,HolConstr, NHolConstr);
          HalfStepLeapFrogStep();
          break;
        case VelocityVerlet:
          //VelocityVerletStep(xnew, grad, amasses, int_reg, natoms, Forces);
          VelocityVerletStep();
          break;
      }

      /*
       * if ( StepAlgorithm == MDAlgorithm.HalfStepLeapFrog ) HalfStepLeapFrogStep(xnew, grad, amasses, int_reg, natoms, Forces,
       * HolConstr, NHolConstr ); else if ( StepAlgorithm == MDAlgorithm.VelocityVerlet ) VelocityVerletStep(xnew, grad, amasses,
       * int_reg, natoms, Forces );
       */

      if (progressInterface != null) {
        progressInterface.dynamicsProgressed(String.format("Step %d Etot=%10.2f Epot=%10.2f", i + 1, Etot, Epot));
      }


      //!!!if ( !(i%ntwe) ) PrintEnergiesAndTemp(NTWE,i+1,Etot,Epot,Ekin,Tcurr);

      /*
       * if ( (PrintCRD>0 ) && (!(i%PrintCRD)) ) { AppendToAmberCRDFile(xnew, natoms, crd_file);
       * PrintEnergiesAndTemp(energy_file,i,Etot,Epot,Ekin,Tcurr); }
       */

      /*
       * if ( (PrintMol2>0 ) && (!(i%PrintMol2)) ) { char fichier[20]; sprintf(fichier, "dyn%04d.mol2",i/PrintMol2);
       * WriteMol2UsingPattern(fichier, mol2_file, xnew, natoms); }
       */

      if (i == 0) {
        Etot_lowest = Etot;
        Etot_highest = Etot;
        Epot_lowest = Epot;
        Epot_highest = Epot;
        Ekin_lowest = Ekin;
        Ekin_highest = Ekin;
        Temp_lowest = Tcurr;
        Temp_highest = Tcurr;
      }

      Etot_average += Etot;
      Etot_square += Power2(Etot);
      if (Etot_lowest > Etot) {
        Etot_lowest = Etot;
      } else if (Etot_highest < Etot) {
        Etot_highest = Etot;
      }

      Epot_average += Epot;
      Epot_square += Power2(Epot);
      if (Epot_lowest > Epot) {
        Epot_lowest = Epot;
      } else if (Epot_highest < Epot) {
        Epot_highest = Epot;
      }

      Ekin_average += Ekin;
      Ekin_square += Power2(Ekin);
      if (Ekin_lowest > Ekin) {
        Ekin_lowest = Ekin;
      } else if (Ekin_highest < Ekin) {
        Ekin_highest = Ekin;
      }

      Temp_average += Tcurr;
      Temp_square += Power2(Tcurr);
      if (Temp_lowest > Tcurr) {
        Temp_lowest = Tcurr;
      } else if (Temp_highest < Tcurr) {
        Temp_highest = Tcurr;
      }

      /*
       * if ( NoSilent ) { printf("\nEtot: %lf Epot: %lf Ekin: %lf T: %lf",Etot,Epot,Ekin,Tcurr); fflush(stdout); }
       */

      /*
       * if ( mean_str ) for (j=0; j<natoms; j++) { xmean[j] += xold[j]; }
       */

      /*
       * if ( EndRun() ) { printf("\n\nMD RUN STOPED ACCORDING TO REQUEST\n\n"); break; }
       */

      /*
       * #ifndef WIN32 if ( !i ) { times(&elp_time); Finish_time = (double)(elp_time.tms_utime +
       * elp_time.tms_stime)/(double)CLOCKS_PER_SEC; printf("\n\nElapsed time for 1 MD step: "); TimeDecomposition(Finish_time -
       * Start_time); fflush(stdout); } #endif
       */

    } // - Main loop

    /*
     * #ifndef WIN32 times(&elp_time); Finish_time = (double)(elp_time.tms_utime + elp_time.tms_stime)/(double)CLOCKS_PER_SEC;
     * Elapsed_time = Finish_time - Start_time; #endif
     */

    /*
     * if ( PrintCRD > 0 ) fclose(energy_file); fclose(NTWE);
     */

    /*
     * if ( JobName != NULL ) { strcpy(bufer,JobName); strcat(bufer,"-restart.res"); } else strcpy(bufer,"restart.res");
     * WriteCoordAndVel(xold,vold,natoms,i,bufer,"--- Normal termination");
     */


    if (i != MaxSteps) {
      ++i;
    }
    System.out.println(i + " MD Steps have been done");
    double norm = (double) i;
    Etot_average /= norm;
    Epot_average /= norm;
    Ekin_average /= norm;
    Temp_average /= norm;

    /*
     * if ( mean_str ) { for (j=0; j<natoms; j++) { xmean[j] /= norm; }
     */

    /*
     * FILE *restart = fopen(mean_crd_file,"wt"); assert(restart); fprintf(restart,"%d Mean structure after %d MD
     * steps\n",natoms,i); for (j=0; j<natoms; j++) fprintf(restart,"%12lf %12lf %12lf\n",xmean[j].x, xmean[j].y, xmean[j].z);
     * fclose(restart); }
     */

    Etot_square = Math.sqrt(Math.abs(Etot_square / norm - Power2(Etot_average)));
    Epot_square = Math.sqrt(Math.abs(Epot_square / norm - Power2(Epot_average)));
    Ekin_square = Math.sqrt(Math.abs(Ekin_square / norm - Power2(Ekin_average)));
    Temp_square = Math.sqrt(Math.abs(Temp_square / norm - Power2(Temp_average)));

    System.out.printf("\n\n                      Average   std. dev.      Lowest     Highest");
    System.out.printf("\nTotal Energy       %10.4f  %10.4f  %10.4f  %10.4f  Kcal/mol",
            Etot_average, Etot_square, Etot_lowest, Etot_highest);
    System.out.printf("\nPotential Energy   %10.4f  %10.4f  %10.4f  %10.4f  Kcal/mol",
            Epot_average, Epot_square, Epot_lowest, Epot_highest);
    System.out.printf("\nKinetic Energy     %10.4f  %10.4f  %10.4f  %10.4f  Kcal/mol",
            Ekin_average, Ekin_square, Ekin_lowest, Ekin_highest);
    System.out.printf("\nTemperature        %10.4f  %10.4f  %10.4f  %10.4f  Degrees K",
            Temp_average, Temp_square, Temp_lowest, Temp_highest);

    System.out.printf("\n\nElapsed MD time: ");
    TimeDecomposition(Elapsed_time);
//fflush(stdout);

  }

  public static double KineticTemperature(List<AtomMD> dynamicAtoms, double dfree) {
    double ekin = 0.0;
    for (AtomMD mda : dynamicAtoms) {
      ekin += mda.atom.getAtomicMass() * (mda.vOld.getXSq() + mda.vOld.getYSq() + mda.vOld.getZSq());
    }
    ekin *= 0.5 / ToA;
    return (2.0) * ekin / (dfree * GasConst_kcal);
  }

  public static double Power2(double a) {
    return a * a;
  }

  public static double SIGN(double A, double B) {
    if (B >= 0.0) {
      return A > 0.0 ? A : -A;
    } else {
      return A < 0.0 ? A : -A;
    }
  }

  public static void ScaleVelocities(List<AtomMD> dynamicAtoms, double delta_t, double taut, double t0, double dfree) {

    double tcurr = KineticTemperature(dynamicAtoms, dfree);
    double gamma = Math.sqrt(1.0 + delta_t / taut * (t0 / tcurr - 1.0));

    for (AtomMD mda : dynamicAtoms) {
      mda.vOld.multiply(gamma);
    }
  }

  void TimeDecomposition(double seconds) {
    int days = (int) (seconds / 86400.);
    if (days > 0) {
      System.out.printf("%d days ", days);
    }
    int hours = (int) ((seconds - (double) days * 86400.) / 3600.);
    if (hours > 0) {
      System.out.printf("%d hours ", hours);
    }
    int minuits = (int) ((seconds - (double) days * 86400. - (double) hours * 3600.) / 60.);
    if (minuits > 0) {
      System.out.printf("%d min ", minuits);
    }
    double sec = seconds - (double) days * 86400. - (double) hours * 3600.
            - (double) minuits * 60.;
    System.out.printf("%f sec ", sec);
  }

  protected void HalfStepLeapFrogStep() {//
    //,      Inter12 *constraints , int nc

    double dt2_2 = delta_t * delta_t / 2.0, dt_2 = delta_t / 2.0, gamma;
    int j;

    Epot = forces.forces(grads);

    int count = 0;
    for (AtomMD mda : dynamicAtoms) {
      float amass = mda.atom.getAtomicMass();
      Point3d grad = grads[count];
      mda.a.setX(-ToA * grad.getX() / amass);
      mda.a.setY(-ToA * grad.getY() / amass);
      mda.a.setZ(-ToA * grad.getZ() / amass);
      ++count;
    }

    /*
     * if ( ExtendedPrint ) { printf("\n\nAccelerations:\n"); for(j=0; j<natoms; j++) printf("\n%3d %8.3lf %8.3lf %8.3lf
     * %8.3lf",j+1,a[j].x,a[j].y,a[j].z,a[j].norm()); }
     */

    /*
     * if ( RemoveMomentum && !(CurrentStep%RemoveMomentum) ) RemoveNetMomentum(xold, vold, amasses, int_reg, natoms);
     */

// --- Evaluate all half time step quantities

    Ekin = 0.0;
    for (AtomMD mda : dynamicAtoms) {
      float amass = mda.atom.getAtomicMass();
      Ekin += amass * (mda.vOld.getXSq() + mda.vOld.getYSq() + mda.vOld.getZSq());
    }
    Ekin *= 0.5 / ToA;
    Tcurr = 2.0 * Ekin / (DegreesOfFreedom * GasConst_kcal);
    gamma = Math.sqrt( 1.0 + delta_t / tauT * (T0 / Tcurr - 1.0));

    /*
     * if (NoSilent) { printf("\n Half Step Ekin (vold): %8.3lf T: %8.3lf gamma: %lf", Ekin, Tcurr, gamma); }
     */

// Leap the velocities and scale them

    for (AtomMD mda : dynamicAtoms) {
      mda.vNew.setX(mda.vOld.getX() + delta_t * mda.a.getX());
      mda.vNew.setY(mda.vOld.getY() + delta_t * mda.a.getY());
      mda.vNew.setZ(mda.vOld.getZ() + delta_t * mda.a.getZ());
      mda.vNew.multiply(gamma);
    }

    /*
     * if (ExtendedPrint) { printf("\n\nVelocities (A/ps):\n"); for (j = 0; j < natoms; j++) { printf("\n%3d %8.3lf %8.3lf %8.3lf
     * %8.3lf", j + 1, vnew[j].x, vnew[j].y, vnew[j].z, vnew[j].norm()); } }
     */

// --- Compute the new unconstrained coordinates

    for (AtomMD mda : dynamicAtoms) {
      mda.atom.setX(mda.xOld.getX() + delta_t * mda.vNew.getX());
      mda.atom.setY(mda.xOld.getY() + delta_t * mda.vNew.getY());
      mda.atom.setZ(mda.xOld.getZ() + delta_t * mda.vNew.getZ());
    }

// --- Apply any SHAKE constraints to the coordinates

    /*
     * if (ImposeHolConstr == ImposeShake) { DoShake(constraints, nc, xold, xnew, amasses, int_reg, natoms); } else if
     * (ImposeHolConstr == ImposeLinks) { DoLinks(constraints, nc, xold, xnew, amasses, int_reg, natoms); }
     */

    /*
     * if (ImposeHolConstr) {
     *
     * // --- Compute the constrained velocities
     *
     * for (j = 0; j < natoms; j++) { if (amasses[j] < 0.0 || (!int_reg[j])) { continue; } vnew[j].x = (xnew[j].x - xold[j].x) /
     * delta_t; vnew[j].y = (xnew[j].y - xold[j].y) / delta_t; vnew[j].z = (xnew[j].z - xold[j].z) / delta_t; } }
     */

// --- Some checks 

//Ekin = 0.0;
      /*
     * if (CheckVel) { printf("\n\nVelocity checking is disabled...\n"); exit(1); //Ekin = CheckHotAtoms(amasses,int_reg,natoms); }
     */
//else {
//  for (j=0; j<natoms; j++) {
//    if ( amasses[j] < 0.0 || (!int_reg[j]) ) continue;
//    Ekin += amasses[j] * (
//      Power2(vnew[j].x) + Power2(vnew[j].y) + Power2(vnew[j].z) );
//    }
//  Ekin *= (double)0.5/ToA;
//  }

    /*
     * if (CheckTemp && (Tcurr > T0 + HotSystemTempExceed)) { System.err.printf("\n\nCheckTemp && (Tcurr > T0 + HotSystemTempExceed)
     * are disabled...\n"); System.exit(1); System.out.printf("\nTcurr > T0 + %lf, Make correction for hot atoms...",
     * HotSystemTempExceed); WriteCoordAndVel(xold, vnew, natoms, CurrentStep, "hot.res", " Hot atoms, Tcurr > T0 + ???"); //Ekin =
     * CheckHotAtoms(amasses,int_reg,natoms); }
     */

//Tcurr = (double)2.0 * Ekin / ( DegreesOfFreedom * GasConst_kcal );
//gamma = sqrt((double)1.0 + delta_t/tauT *(T0/Tcurr - (double)1.0));
//if ( NoSilent ) printf("\nVnew: Unscaled T: %8.3lf  Gamma: %8.5lf",Tcurr, gamma);

//for (j=0; j<natoms; j++) {
//  if ( amasses[j] < 0.0 || (!int_reg[j]) ) continue;
//  vnew[j] *= gamma;
//  xnew[j].x = xold[j].x + delta_t * vnew[j].x;
//  xnew[j].y = xold[j].y + delta_t * vnew[j].y;
//  xnew[j].z = xold[j].z + delta_t * vnew[j].z;
//  }

// --- Accumulate all the quantities...

    Ekin = 0.0;
    for (AtomMD mda : dynamicAtoms) {
      Ekin += mda.atom.getAtomicMass() * Power2(0.5 * (mda.vOld.norm() + mda.vNew.norm()));
    }
    /*
     * Implementation is above for (j = 0; j < natoms; j++) { if (amasses[j] < 0.0 || (!int_reg[j])) { continue; } Ekin +=
     * amasses[j] * Power2((double) 0.5 * (sqrt(Power2(vold[j].x) + Power2(vold[j].y) + Power2(vold[j].z)) + sqrt(Power2(vnew[j].x)
     * + Power2(vnew[j].y) + Power2(vnew[j].z)))); }
     */

    Ekin *= 0.5 / ToA;
    Tcurr = 2.0 * Ekin / (DegreesOfFreedom * GasConst_kcal);

    Etot = Ekin + Epot;

    for (AtomMD mda : dynamicAtoms) {
      mda.xOld.setXYZ(mda.atom.getX(), mda.atom.getY(), mda.atom.getZ());
      mda.vOld = mda.vNew;
    }

  }

  void VelocityVerletStep() {

    double dt2_2 = delta_t * delta_t / 2.0, dt_2 = delta_t / 2.0, gamma;
    int j;

    for (AtomMD mda : dynamicAtoms) {
      mda.xOld.setXYZ(
              mda.atom.getX(),
              mda.atom.getY(),
              mda.atom.getZ());
      mda.atom.setXYZ(
              mda.atom.getX() + mda.vOld.getX() * delta_t + mda.a.getX() * dt2_2,
              mda.atom.getY() + mda.vOld.getY() * delta_t + mda.a.getY() * dt2_2,
              mda.atom.getZ() + mda.vOld.getZ() * delta_t + mda.a.getZ() * dt2_2);
      mda.vOld.add(mda.a.getX() * dt_2, mda.a.getY() * dt_2, mda.a.getZ() * dt_2);
    }

    Epot = forces.forces(grads);

    int count = 0;
    for (AtomMD mda : dynamicAtoms) {
      Point3d grad = grads[count];
      float amass = mda.atom.getAtomicMass();
      mda.a.setXYZ(
              -ToA * grad.getX() / amass,
              -ToA * grad.getY() / amass,
              -ToA * grad.getZ() / amass);
      mda.vOld.add(
              mda.a.getX() * dt_2,
              mda.a.getY() * dt_2,
              mda.a.getZ() * dt_2);
      ++count;
    }

//Ekin = KineticEnergy(vold, amasses, int_reg, natoms);
    Ekin = KineticEnergyVOld(dynamicAtoms);
    Ekin *= 0.5 / ToA;
    Tcurr = 2.0 * Ekin / (DegreesOfFreedom * GasConst_kcal);
    Etot = Ekin = Epot;

    /*
     * if ( ExtendedPrint ) { printf("\n\nAccelerations:\n"); for(j=0; j<natoms; j++) printf("\n%3d %8.3lf %8.3lf %8.3lf
     * %8.3lf",j+1,a[j].x,a[j].y,a[j].z,a[j].norm()); }
     */

    gamma = Math.sqrt( 1.0 + delta_t / tauT * (T0 / Tcurr - 1.0));

    /*
     * if ( NoSilent ) printf("\n Ekin: %lf T: %lf",Ekin,Tcurr);
     */

    for (AtomMD mda : dynamicAtoms) {
      mda.vOld.multiply(gamma);
    }

  }

  double KineticEnergyVOld(List<AtomMD> dynamicAtoms) {
    double Ekin = 0.0;
    for (AtomMD mda : dynamicAtoms) {
      Ekin += mda.atom.getAtomicMass() * mda.vOld.squaredNorm();
    }
    /*
     * Implementation is above for (int j=0; j<natoms; j++) { if ( amasses[j] < 0.0 || (!int_reg[j]) ) continue; Ekin += amasses[j]
     * * ( Power2(v[j].x) + Power2(v[j].y) + Power2(v[j].z) ); }
     */
    return Ekin;
  }

  public ForcesInterface getForces() {
    return forces;
  }

  public void setForces(ForcesInterface forces) {
    this.forces = forces;
  }

  public DynamicsProgressInterface getProgressInterface() {
    return progressInterface;
  }

  public void setProgressInterface(DynamicsProgressInterface progressInterface) {
    this.progressInterface = progressInterface;
  }
//************************************************

  class AtomMD {

    AtomInterface atom;
    int index;
    Point3d vOld, vNew, a;
    Point3f xOld;

    public AtomMD(AtomInterface atom, int index) {
      this.atom = atom;
      this.index = index;
    }
  }
}
