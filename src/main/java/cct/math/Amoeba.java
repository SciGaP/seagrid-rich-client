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

import java.util.logging.Logger;

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
public class Amoeba {

   float TINY = 1.0e-10f; // A small number.
   float gamma = 0.01f; //
   int NMAX = 5000; // Maximum allowed number of function evaluations
   int nfunk; // nfunk gives the number of function evaluations taken.
   float psum[];
   float p[][];
   float y[];
   float ptry[];
   boolean print = true;

   float Fmin = 0;
   static final Logger logger = Logger.getLogger(Amoeba.class.getCanonicalName());

   public Amoeba() {
   }

   void GET_PSUM(int ndim) {
      for (int j = 0; j < ndim; j++) {
         float sum = 0.0f;
         for (int i = 0; i < ndim + 1; i++) {
            sum += p[i][j];
         }
         psum[j] = sum;
      }
   }

   /**
    * Multidimensional minimization of the function func(x) where x[ndim] is a vector in ndim
    * dimensions, by the downhill simplex method of Nelder and Mead. The matrix p[ndim+1]
    * [ndim] is input. Its ndim+1 rows are ndim-dimensional vectors which are the vertices of
    * the starting simplex. Also input is the vector y[ndim+1], whose components must be preinitialized
    * to the values of func evaluated at the ndim+1 vertices (rows) of p; and ftol the
    * fractional convergence tolerance to be achieved in the function value (n.b.!). On output, p and
    * y will have been reset to ndim+1 new points all within ftol of a minimum function value, and
    * nfunk gives the number of function evaluations taken.
    * @param <any> float
    */
   public int amoeba(float x[], int ndim, float ftol,
                     MinimizedFunctionInterface func) {

      int i, ihi, ilo, inhi, j, cond = 0;
      float rtol, swap, ysave, ytry;

      //psum=vector(1,ndim);
      psum = new float[ndim];
      ptry = new float[ndim];
      p = new float[ndim + 1][ndim];
      y = new float[ndim + 1];

      // --- Initialization

      // --- Calculate function in initial point
      y[0] = func.function(ndim, x);
      for (i = 0; i < ndim; i++) {
         p[0][i] = x[i];
      }
      if (print) {
         logger.info("Initial fun value: " + y[0]);
      }

      // --- Calculate function in N more points
      for (j = 0, i = 1; i < ndim + 1; i++, j++) {
         swap = x[j];
         x[j] += gamma;
         y[i] = func.function(ndim, x);

         for (int k = 0; k < ndim; k++) {
            p[i][k] = x[k];
         }

         x[j] = swap;
      }

      // --- Start minimization

      nfunk = 0;
      GET_PSUM(ndim);

      for (; ; ) {
         ilo = 0;
         //First we must determine which point is the highest(worst),
         // next - highest, and lowest
         // (best), by looping over the points in the simplex.

         //ihi = y[1] > y[2] ? (inhi = 2, 1) : (inhi = 1, 2);
         ihi = y[0] > y[1] ? 0 : 1;
         inhi = y[0] > y[1] ? 1 : 0;
         for (i = 0; i < ndim + 1; i++) {
            if (y[i] <= y[ilo]) {
               ilo = i;
            }
            if (y[i] > y[ihi]) {
               inhi = ihi;
               ihi = i;
            }
            else if (y[i] > y[inhi] && i != ihi) {
               inhi = i;
            }
         }

         rtol = 2.0f * Math.abs(y[ihi] - y[ilo]) /
             ( (Math.abs(y[ihi]) + Math.abs(y[ilo])) + TINY);

         if (print) {
            logger.info("Fun value: " + y[ilo] + " rtol: " + rtol);
         }

         //Compute the fractional range from highest to lowest and return if satisfactory.
         if (rtol < ftol || nfunk >= NMAX) { // If returning, put best point and value in slot 0.
            if (nfunk >= NMAX) {
               System.err.println("NMAX exceeded");
               cond = 1;
            }

            //SWAP(y[1], y[ilo])
            swap = y[0];
            y[0] = y[ilo];
            Fmin = y[ilo];
            y[ilo] = swap;

            for (i = 0; i < ndim; i++) {
               // SWAP(p[1][i], p[ilo][i])
               swap = p[0][i];
               p[0][i] = p[ilo][i];
               x[i] = p[ilo][i];
               p[ilo][i] = swap;
            }
            break;
         }

         nfunk += 2;

         // Begin a new iteration.First extrapolate by a factor \u22121through the
         // face of the simplex across from the high point, i.e.,
         // reflect the simplex from the high point.

         ytry = amotry(p, y, psum, ndim, func, ihi, -1.0f);

         if (ytry <= y[ilo]) {
            // Gives a result better than the best point, so try
            // an additional extrapolation by a factor 2.
            ytry = amotry(p, y, psum, ndim, func, ihi, 2.0f);
         }
         else if (ytry >= y[inhi]) {
            // The reflected point is worse than the second - highest,
            // so look for an intermediate  lower point, i.e., do
            // a one - dimensional contraction.
            ysave = y[ihi];
            ytry = amotry(p, y, psum, ndim, func, ihi, 0.5f);
            if (ytry >= ysave) {
               // Can �t seem to get rid of that high point.Better
               for (i = 0; i < ndim + 1; i++) { // contract around the lowest(best) point.
                  if (i != ilo) {
                     for (j = 0; j < ndim; j++) {
                        p[i][j] = psum[j] = 0.5f * (p[i][j] + p[ilo][j]);
                     }
                     y[i] = func.function(ndim, psum);
                  }
               }
               nfunk += ndim; // Keep track of function evaluations.
               GET_PSUM(ndim); // Recompute psum.
            }
         }
         else {
            --nfunk; // Correct the evaluation count.
         }
      }
      return cond;
   }

   // Go back for the test of doneness and the next iteration.
   //free_vector(psum, 1, ndim);


   /**
    * Extrapolates by a factor fac through the face of the simplex across
    * from the high point, tries it, and replaces the high point if the new point is better.
    *
    * @param p float[][]
    * @param y float[]
    * @param psum float[]
    * @param ndim int
    * @param <any> float
    * @return float
    */
   float amotry(float p[][], float y[], float psum[], int ndim,
                MinimizedFunctionInterface func, int ihi, float fac) {
      int j;
      float fac1, fac2, ytry;
      fac1 = (1.0f - fac) / ndim;
      fac2 = fac1 - fac;
      for (j = 0; j < ndim; j++) {
         ptry[j] = psum[j] * fac1 - p[ihi][j] * fac2;
      }
      ytry = func.function(ndim, ptry); // Evaluate the function at the trial point.
      if (ytry < y[ihi]) { //If it �s better than the highest, then replace the highest.
         y[ihi] = ytry;
         for (j = 0; j < ndim; j++) {
            psum[j] += ptry[j] - p[ihi][j];
            p[ihi][j] = ptry[j];
         }
      }
//free_vector(ptry, 1, ndim);
      return ytry;
   }

   public static void main(String[] args) {
      Amoeba amoeba = new Amoeba();
   }

}
