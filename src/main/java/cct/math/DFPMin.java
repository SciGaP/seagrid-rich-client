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

package cct.math;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cct.interfaces.MinimizeProgressInterface;
import cct.tools.Utils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 * The Broyden-Fletcher-Goldfarb-Shanno variant of Davidon-Fletcher-Powell minimization.
 * Adopted from the Numerical Recipes in C
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class DFPMin
    implements MinimizerInterface {

   static final List completionMessages = new ArrayList();
   static final Logger logger = Logger.getLogger(DFPMin.class.getCanonicalName());

   int ITMAX = 1000; // Maximum allowed number of iterations.
   double EPS = 3.0e-8; // Machine precision.
   double TOLX = (4.0 * EPS); // Convergence criterion on x values.
   float MAXGRAD = 0.5f; // Convergence criterion on max gradient value.
   float MAXGRMS = 0.25f; // Convergence criterion on max rms gradient value.
   double STPMX = 100.0; // Scaled maximum step length allowed in line searches.
   int iter; // Actual number of iterations
   float fret; // Minimum value of the function
   float gTol = 0.01f;

   boolean maxGradConvergence = true;
   boolean maxGRMSConvergence = true;

   boolean debug = true;

   MinimizeProgressInterface minimizationProgress = null;

   // --- Temporary storage

   private float[] dg = null;
   private float[] g = null;
   private float[] hdg = null;
   private float[][] hessin = null;
   private float[] pnew = null;
   private float[] xi = null;

   static {
      completionMessages.add(new String("The algorithm has converged")); // =0
      completionMessages.add(new String(
          "The maximum number of function evaluations have been used")); // =1
      completionMessages.add(new String("Minimization was cancelled by user")); // =2
      //      IF NFLAG=2, THE LINEAR SEARCH HAS FAILED TO
      //                      IMPROVE THE FUNCTION VALUE. THIS IS THE
      //                      USUAL EXIT IF EITHER THE FUNCTION OR THE
      //                      GRADIENT IS INCORRECTLY CODED.
      //                   IF NFLAG=3, THE SEARCH VECTOR WAS NOT
      //                      A DESCENT DIRECTION. THIS CAN ONLY BE CAUSED
      //                      BY ROUNDOFF,AND MAY SUGGEST THAT THE
      //                      CONVERGENCE CRITERION IS TOO STRICT.
   }

   public DFPMin() {
   }

   public void setGTolerance(float gtol) {
      gTol = gtol;
   }

   public int getActualIterNum() {
      return iter;
   }

   public void setMaximumStep(double step) {
      STPMX = step;
   }

   /**
    * Given a starting point p[1..n] that is a vector of length n, the Broyden-Fletcher-Goldfarb-
    * Shanno variant of Davidon-Fletcher-Powell minimization is performed on a function func, using
    * its gradient as calculated by a routine dfunc. The convergence requirement on zeroing the
    * gradient is input as gtol. Returned quantities are p[1..n] (the location of the minimum),
    * iter (the number of iterations that were performed), and fret (the minimum value of the
    * function). The routine lnsrch is called to perform approximate line minimizations.
    *
    * @param n int
    * @param p float[]
    * @param gtol float
    * @param iter int
    * @param fret float
    * @param func MinimizedFunctionInterface
    */
   @Override
  public int minimizeFunction(int n, float[] p,
                               MinimizedFunctionInterface func) {

      boolean check = false, isConverged = false, updateVariables = false;
      int i, its, j;
      float den, fac, fad, fae, fp, stpmax, sum = 0.0f, sumdg, sumxi, temp,
          test;

      dg = new float[n];
      g = new float[n];
      hdg = new float[n];
      hessin = new float[n][n];
      pnew = new float[n];
      xi = new float[n];

      LinearSearch ls = new LinearSearch();

      if (minimizationProgress != null) {
         updateVariables = minimizationProgress.getVariablesFrequencyUpdate() >
             0;
         minimizationProgress.minimizationStarted(
             "Calculating initial function value and grads...");
      }
      /*
           dg = vector(1, n);
           g = vector(1, n);
           hdg = vector(1, n);
           hessin = matrix(1, n, 1, n);
           pnew = vector(1, n);
           xi = vector(1, n);
       */
      //fp = ( * func) (p); // Calculate starting function value and gra -
      //( * dfunc) (p, g); // dient,

      fp = func.function(n, p, g);

      if (minimizationProgress != null) {
         minimizationProgress.minimizationProgressed(String.format(
             "Function: %8.4f GRMS: %8.4f Max Grad: %8.4f", fp, getRMS(n, g),
             maxGrad(n, g)));
      }

      if (debug) {
         logger.info("Starting function value: " + fp + " norm: " +
                            gradNorm(n, g) + " Max grad: " + maxGrad(n, g));
         Utils.printFloatVector(n, g);
      }

      isConverged = false;
      // --- Check on convergence on max G
      float maxG = 0;
      if (maxGradConvergence) {
         for (i = 0; i < n; i++) {
            if (Math.abs(maxG) < Math.abs(g[i])) {
               maxG = Math.abs(g[i]);
            }
         }
         if (Math.abs(maxG) <= MAXGRAD) {
            logger.info(
                "Convergence on max gradient in initial point. Max gradient: " +
                maxG);
            if (minimizationProgress != null) {
               minimizationProgress.minimizationCompleted();
            }
            return 0;
         }
      }
      if (maxGRMSConvergence && getRMS(n, g) <= MAXGRMS) {
         logger.info(
             "Convergence on Max RMS gradient in initial point. GRMS: " +
             getRMS(n, g));
         if (minimizationProgress != null) {
            minimizationProgress.minimizationCompleted();
         }
         return 0;
      }

      /*
       for (i = 1; i <= n; i++) { // and initialize the inverse Hessian to the
         for (j = 1; j <= n; j++) { // unit matrix.
            hessin[i][j] = 0.0;
         }
         hessin[i][i] = 1.0;
         xi[i] = -g[i]; // Initial line direction.
         sum += p[i] * p[i];
             }
       */

      for (i = 0; i < n; i++) { // and initialize the inverse Hessian to the
         for (j = 0; j < n; j++) { // unit matrix.
            hessin[i][j] = 0.0f;
         }
         hessin[i][i] = 1.0f;
         xi[i] = -g[i]; // Initial line direction.
         sum += p[i] * p[i];
      }

      stpmax = (float) (STPMX * Math.max(Math.sqrt(sum), n));
      //if ( stpmax > (float)STPMX ) stpmax = (float)STPMX; // !!! VLAD

      for (its = 1; its <= ITMAX; its++) { // Main loop over the iterations.
         iter = its;
         if (debug) {
            logger.info("Iteration: " + iter + " stpmax: " + stpmax);
         }

         check = ls.lnsrch(n, p, fp, g, xi, pnew, stpmax, func);
         fret = ls.getFMin();
         if (debug) {
            logger.info("After linear search: " + fret);
         }

         //lnsrch(n, p, fp, g, xi, pnew, fret, stpmax, & check, func);

         // The new function evaluation occurs in lnsrch;
         // save the function value in fp for the
         // next line search.It is usually safe to ignore the value of check.

         fp = fret;
         for (i = 0; i < n; i++) {
            xi[i] = pnew[i] - p[i]; // Update the line direction,
            p[i] = pnew[i]; // and the current point.
         }
         test = 0.0f; // Test for convergence on deltaX.
         for (i = 0; i < n; i++) {
            temp = (float) (Math.abs(xi[i]) / Math.max(Math.abs(p[i]), 1.0));
            if (temp > test) {
               test = temp;
            }
         }

         /* Old stuff
                   if (test < TOLX) {
            //freeAll(); // FREEALL
            logger.info("Convergence on deltaX was achieved: " + test +
                               " TOLX: " + TOLX);
            return 0;
                   }
          */

         for (i = 0; i < n; i++) { // Save the old gradient,
            dg[i] = g[i];
         }

         // ( * dfunc) (p, g); // and get the new gradient. ???
         fret = func.function(n, p, g);
         if (minimizationProgress != null) {
            minimizationProgress.minimizationProgressed(String.format(
                "Iter: %d Function: %8.4f GRMS: %8.4f Max Grad: %8.4f", iter,
                fret, getRMS(n, g),
                maxGrad(n, g)));
            if (minimizationProgress.isMinimizationCancelled()) {
               return 2;
            }
            if (updateVariables &&
                iter % minimizationProgress.getVariablesFrequencyUpdate() == 0) {
               minimizationProgress.updateVariables(n, p);
            }
         }
         if (debug) {
            logger.info("Function value: " + fret);
         }

         test = 0.0f; // Test for convergence on zero gradient.
         den = (float) Math.max(fret, 1.0);
         for (i = 0; i < n; i++) {
            temp = (float) (Math.abs(g[i]) * Math.max(Math.abs(p[i]), 1.0)) /
                den;
            if (temp > test) {
               test = temp;
            }
         }
         /* --- Old stuff
                   if (test < gTol) {
            logger.info("Convergence on deltaG was achieved: " + test +
                               " Gtol: " + gTol);
            //freeAll(); //FREEALL
            return 0;
                   }
          */

         // Test for convergence on max gradient.
         if (maxGradConvergence) {
            for (i = 0; i < n; i++) {
               if (Math.abs(maxG) < Math.abs(g[i])) {
                  maxG = Math.abs(g[i]);
               }
            }
            if (Math.abs(maxG) <= MAXGRAD) {
               logger.info(
                   "Convergence on max gradient was achieved. Max gradient: " +
                   maxG);
               if (minimizationProgress != null) {
                  minimizationProgress.minimizationCompleted();
               }
               return 0;
            }
         }

         if (maxGRMSConvergence && getRMS(n, g) <= MAXGRMS) {
            logger.info(
                "Convergence on Max RMS gradient was achieved. GRMS: " +
                getRMS(n, g));
            if (minimizationProgress != null) {
               minimizationProgress.minimizationCompleted();
            }
            return 0;
         }

         for (i = 0; i < n; i++) {
            dg[i] = g[i] - dg[i]; // Compute difference of gradients,
         }

         for (i = 0; i < n; i++) { // and difference times current matrix.
            hdg[i] = 0.0f;
            for (j = 0; j < n; j++) {
               hdg[i] += hessin[i][j] * dg[j];
            }
         }
         fac = fae = sumdg = sumxi = 0.0f; // Calculate dot products for the denomitators.
         for (i = 0; i < n; i++) {
            fac += dg[i] * xi[i];
            fae += dg[i] * hdg[i];
            //sumdg += SQR(dg[i]);
            //sumxi += SQR(xi[i]);
            sumdg += dg[i] * dg[i];
            sumxi += xi[i] * xi[i];
         }

         if (fac > Math.sqrt(EPS * sumdg * sumxi)) { // Skip update if fac not sufficiently positive.
            fac = 1.0f / fac;
            fad = 1.0f / fae;

            //The vector that makes BFGS different from DFP:
            for (i = 0; i < n; i++) {
               dg[i] = fac * xi[i] - fad * hdg[i];
            }
            for (i = 0; i < n; i++) { // The BFGS updating formula:
               for (j = i; j < n; j++) {
                  hessin[i][j] += fac * xi[i] * xi[j]
                      - fad * hdg[i] * hdg[j] + fae * dg[i] * dg[j];
                  hessin[j][i] = hessin[i][j];
               }
            }
         }
         for (i = 0; i < n; i++) { // Now calculate the next direction to go,
            xi[i] = 0.0f;
            for (j = 0; j < n; j++) {
               xi[i] -= hessin[i][j] * g[j];
            }
         }

         // and go back for another iteration.

      }

      System.err.println("too many iterations in dfpmin");
      if (minimizationProgress != null) {
         minimizationProgress.minimizationCompleted();
      }
      return 1;
      //freeAll(); //FREEALL

   }

   void freeAll() {
      dg = null;
      g = null;
      hdg = null;
      hessin = null;
      pnew = null;
      xi = null;
   }

   float gradNorm(int n, float g[]) {
      float norm = 0;
      for (int i = 0; i < n; i++) {
         norm += g[i] * g[i];
      }
      return (float) Math.sqrt(norm);
   }

   float maxGrad(int n, float g[]) {
      float max = Math.abs(g[0]);
      float value = g[0];
      for (int i = 1; i < n; i++) {
         if (Math.abs(g[i]) > max) {
            max = Math.abs(g[i]);
            value = g[i];
         }
      }
      return value;
   }

   double getRMS(int n, float v[]) {
      double s = 0;
      for (int i = 0; i < n; i++) {
         s += v[i] * v[i];
      }
      return Math.sqrt(s / n);
   }

   public static void main(String[] args) {
      DFPMin dfpmin = new DFPMin();
   }

   // --- Implementation of MinimizerInterface functions

   @Override
  public void enableMaxGradConvergence(boolean enable) {
      maxGradConvergence = enable;
   }

   @Override
  public void enableMaxGRMSConvergence(boolean enable) {
      maxGRMSConvergence = enable;
   }

   @Override
  public String getExitCodeDescription(int code) {
      if (code < 0 || code >= completionMessages.size()) {
         return "Internal Error: No such exit code";
      }
      return (String) completionMessages.get(code);
   }

   @Override
  public float[] getGradients() {
      return g;
   }

   @Override
  public float getMaxGradientConv() {
      return MAXGRAD;
   }

   @Override
  public float getMaxRMSGradientConv() {
      return MAXGRMS;
   }

   @Override
  public int getMaxIterations() {
      return ITMAX;
   }

   @Override
  public String getName() {
      return "DFP";
   }

   @Override
  public boolean isMaxGradConvergence() {
      return maxGradConvergence;
   }

   @Override
  public boolean isMaxRMSGradConvergence() {
      return maxGRMSConvergence;
   }

   @Override
  public void setMaxGradientConv(float maxgrad) {
      MAXGRAD = maxgrad;
   }

   @Override
  public void setMaxRMSGradientConv(float maxgrms) {
      MAXGRMS = maxgrms;
   }

   @Override
  public void setMaxIterations(int iter) {
      ITMAX = iter;
   }

   @Override
  public boolean useGradients() {
      return true;
   }

   @Override
  public void setMinimizeProgressInterface(MinimizeProgressInterface mpi) {
      minimizationProgress = mpi;
   }
}
