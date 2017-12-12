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

import cct.tools.Utils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 * Adapted from the Numerical Recipes in C
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class LinearSearch {
   public LinearSearch() {
   }

   double ALF = 1.0e-4; // Ensures sufficient decrease in function value.
   double TOLX = 1.0e-7; // Convergence criterion on \u0394x.
   float fMin; // Final minimum function value
   boolean debug = false;
   static final Logger logger = Logger.getLogger(LinearSearch.class.getCanonicalName());

   /**
    * Given an n-dimensional point xold[1..n], the value of the function and gradient there, fold
    * and g[1..n], and a direction p[1..n], finds a new point x[1..n] along the direction p from
    * xold where the function func has decreased �sufficiently.� The new function value is returned
    * in f. stpmax is an input quantity that limits the length of the steps so that you do not try to
    * evaluate the function in regions where it is undefined or subject to overflow. p is usually the
    * Newton direction. The output quantity check is false (0) on a normal exit. It is true (1) when
    * x is too close to xold. In a minimization algorithm, this usually signals convergence and can
    * be ignored. However, in a zero-finding algorithm the calling program should check whether the
    * convergence is spurious. Some �difficult� problems may require double precision in this routine.
    * @param n int
    * @param xold float[]
    * @param fold float
    * @param g float[]
    * @param p float[]
    * @param x float[]
    * @param <any> float
    */
   public boolean lnsrch(int n, float[] xold, float fold, float[] g,
                         float[] p,
                         float[] x,
                         float stpmax, MinimizedFunctionInterface func) {
      int i;
      double a, alam, alam2 = 0, alamin, b, disc, f2 = 0, rhs1, rhs2, slope,
          sum, temp,
          test, tmplam;
      boolean check = false;
      for (sum = 0.0, i = 0; i < n; i++) {
         sum += p[i] * p[i];
      }
      sum = Math.sqrt(sum);
      if (sum > stpmax) {
         for (i = 0; i < n; i++) {
            p[i] *= stpmax / sum; // Scale if attempted step is too big.
         }
      }
      for (slope = 0.0, i = 0; i < n; i++) {
         slope += g[i] * p[i];
      }

      if (debug) {
         logger.info("Vector of directions: ");
         Utils.printFloatVector(n, p);
      }

      if (slope >= 0.0) {
         System.err.println("Roundoff problem in lnsrch.");
      }
      test = 0.0; // Compute Lambda min.
      for (i = 0; i < n; i++) {
         temp = Math.abs(p[i]) / Math.max(Math.abs(xold[i]), 1.0);
         if (temp > test) {
            test = temp;
         }
      }
      alamin = TOLX / test;
      alam = 1.0; // Always try full Newton step first.
      for (int iter = 1; ; iter++) { // Start of iteration loop.
         for (i = 0; i < n; i++) {
            x[i] = xold[i] + (float) alam * p[i]; // !!! precision !!!
         }

         fMin = func.function(n, x);

         if (debug) {
            logger.info("Linsearch: Iteration: " + iter + " Func: " +
                               fMin + " alam: " + alam);
         }

         if (alam < alamin) { // Convergence on deltaX. For zero finding, the calling program should verify the convergence.
            for (i = 0; i < n; i++) {
               x[i] = xold[i];
            }
            check = true;
            return check;
         }
         else if (fMin <= fold + ALF * alam * slope) {
            return check; // Sufficient function decrease.
         }
         else { // Backtrack.
            if (alam == 1.0) {
               tmplam = -slope / (2.0 * (fMin - fold - slope)); // First time.
            }
            else { // Subsequent backtracks.
               rhs1 = fMin - fold - alam * slope;
               rhs2 = f2 - fold - alam2 * slope;
               a = (rhs1 / (alam * alam) - rhs2 / (alam2 * alam2)) /
                   (alam - alam2);
               b = ( -alam2 * rhs1 / (alam * alam) +
                    alam * rhs2 / (alam2 * alam2)) / (alam - alam2);
               if (a == 0.0) {
                  tmplam = -slope / (2.0 * b);
               }
               else {
                  disc = b * b - 3.0 * a * slope;
                  if (disc < 0.0) {
                     tmplam = 0.5 * alam;
                  }
                  else if (b <= 0.0) {
                     tmplam = ( -b + Math.sqrt(disc)) / (3.0 * a);
                  }
                  else {
                     tmplam = -slope / (b + Math.sqrt(disc));
                  }
               }
               if (tmplam > 0.5 * alam) {
                  tmplam = 0.5 * alam; // \u03BB \u2264 0.5\u03BB1.
               }
            }
         }
         alam2 = alam;
         f2 = fMin;
         alam = Math.max(tmplam, 0.1 * alam); // \u03BB \u2265 0.1\u03BB1.
      } // Try again.
   }

   public float getFMin() {
      return fMin;
   }

   public static void main(String[] args) {
      LinearSearch minimization = new LinearSearch();
   }
}
