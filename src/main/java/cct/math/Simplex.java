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
public class Simplex {

   static int Ok = 0;
   boolean SilentMode;
   float p[][];
   float y[], psum[], ptry[];
   int ndim;
   float F0 = 0;

   public Simplex(int n) {
      SilentMode = false;
      y = new float[n + 1];
      psum = new float[n];
      ptry = new float[n];
      p = new float[n + 1][n];
      //for(int i=0; i<n+1; i++) p[i] = new real[n];

   }

   public float getMinimumValue() {
      return F0;
   }

   private void GET_PSUM(int nvar) {
      for (int j = 0; j < nvar; j++) {
         float sum = 0.0f;
         for (int i = 0; i < nvar + 1; i++) {
            sum += p[i][j];
         }
         psum[j] = sum;
      }
   }

   /*
       Simplex(int n) {
       SilentMode = 0;
       y = new real[n+1];
       psum = new real[n];
       ptry = new real[n];
       p = new real*[n+1];
       for(int i=0; i<n+1; i++) p[i] = new real[n];
       }
    */


   public int DownhillSimplex(float set[], int nvar, float ftol,
                              MinimizedFunctionInterface func, int NMAX,
                              float gamma) {

      int i, j, k, lowest, highest, next_highest, ncalls = 0, mpts = nvar + 1;

      float sum, rtol, ytry, ysave, Fvalue = 0;
      //int ntimes = 0;

      if (!SilentMode) {
         System.out.print(
             "\n\n          ************************************************");
         System.out.print(
             "\n          ***      DOWNHILL  S I M P L E X  METHOD     ***");
         System.out.print(
             "\n          ************************************************");

         System.out.print(
             "\n\n FRACTIONAL CONVERGENCE TOLERANCE TO BE ACHIEVED: " + ftol);
      }
      //fflush(stdout);

      ndim = nvar;
// --------- Initialisation
      k = -2;
      for (j = 0; j < nvar + 1; j++) {
         ++k;
         for (i = 0; i < nvar; i++) {
            p[j][i] = set[i];
         }
         if (k >= 0) {
            p[j][k] += gamma;
         }
         y[j] = func.function(nvar, p[j]);
      }

      //GET_PSUM
      GET_PSUM(nvar);

      while (true) {
// --- First we must define which point is the highest (worst), next-highest,
// --- and lowest (best), by looping over the points in the simplex.
         lowest = 0;
         //highest = y[0] > y[1] ? (next_highest = 1, 0) : (next_highest = 0, 1);
         highest = y[0] > y[1] ? 0 : 1;
         next_highest = y[0] > y[1] ? 1 : 0;
         for (i = 0; i < mpts; i++) {
            if (y[i] <= y[lowest]) {
               lowest = i;
            }
            if (y[i] > y[highest]) {
               next_highest = highest;
               highest = i;
            }
            else if (y[i] > y[next_highest] && i != highest) {
               next_highest = i;
            }
         }

         rtol = (float) (2.0 * Math.abs(y[highest] - y[lowest]) /
                         (Math.abs(y[highest]) + Math.abs(y[lowest])));

// --- Compute the fractional range from highest to lowest and return if
//     satisfactory

         F0 = y[lowest];
         if (rtol < ftol) { // --- Put best point and value in set
            for (i = 0; i < nvar; i++) {
               set[i] = p[lowest][i];
            }
            if (!SilentMode) {
               System.out.print("\n\nOptimization finished after " + ncalls + " function calls");
               System.out.print("\nFunction value : " + F0);
               System.out.print(
                   "\n\n **********     E N D  O F  S I M P L E X     ********** ");
            }
            return Ok;
         }

         if (ncalls > 0 && (rtol >= Math.abs(F0 - Fvalue))) { // --- Check delta F
            //++ntimes;
            //if (ntimes > 20) {
            for (i = 0; i < nvar; i++) {
               set[i] = p[lowest][i];
            }
            if (!SilentMode) {
               System.out.print(
                   "\n\nConvergence on F is achieved after " + ncalls + " function calls");
               System.out.print("\nFunction value : " + F0);
               System.out.print(
                   "\n\n **********     E N D  O F  S I M P L E X     ********** ");
            }
            return Ok;
            //}
         }
         else {
            Fvalue = F0;
            //ntimes = 0;
         }

         if (ncalls > NMAX) {
            for (i = 0; i < nvar; i++) {
               set[i] = p[lowest][i];
            }
            if (!SilentMode) {
               System.out.print("\n\nNUMBER OF MAXIMUM CALLS " + NMAX + " IS EXCEEDED");
               System.out.print("\n Function value : " + F0);
               System.out.print(
                   "\n\n **********     E N D  O F  S I M P L E X     ********** ");
            }
            return Ok;
         }

         if (!SilentMode) {

            System.out.print("\n\nNCALLS " + ncalls + "  FUN: " + y[lowest] + "\n");

         }

         //fflush(stdout);

         ncalls += 2;

// --- Begin a new iteration. First extrapolate by a factor -1, i.e.
// --- reflex the simplex from the high point

         ytry = NewTrial(p, y, psum, ptry, nvar, func, highest, -1.0f);
         if (ytry <= y[lowest]) {
            ytry = NewTrial(p, y, psum, ptry, nvar, func, highest, 2.0f);
         }
         else if (ytry >= y[next_highest]) {
            ysave = y[highest];
            ytry = NewTrial(p, y, psum, ptry, nvar, func, highest, 0.5f);
            if (ytry >= ysave) {
               for (i = 0; i < mpts; i++) {
                  if (i != lowest) {
                     for (j = 0; j < nvar; j++) {
                        p[i][j] = psum[j] = 0.5f * (p[i][j] + p[lowest][j]);
                     }
                     y[i] = func.function(nvar, psum);
                  }
               }
               ncalls += nvar;
               //GET_PSUM
               GET_PSUM(nvar);
            }
         }
         else {
            --ncalls;
         }

      } // --- while( True )
   }

   float NewTrial(float p[][], float y[], float psum[], float ptry[], int nvar,
                  MinimizedFunctionInterface func, int highest, float fac) {

      int j;
      float fac1, fac2, ytry;

      fac1 = (1.0f - fac) / nvar;
      fac2 = fac1 - fac;
      for (j = 0; j < nvar; j++) {
         ptry[j] = psum[j] * fac1 - p[highest][j] * fac2;
      }
      ytry = func.function(nvar, ptry);

      if (ytry < y[highest]) { // --- Replace the highest
         y[highest] = ytry;
         for (j = 0; j < nvar; j++) {
            psum[j] += ptry[j] - p[highest][j];
            p[highest][j] = ptry[j];
         }
      }
      return ytry;
   } // --- real NewTrial

}
