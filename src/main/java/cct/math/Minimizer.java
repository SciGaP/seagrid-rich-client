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
 * MINIMIZATION OF UNCONSTRAINED MULTIVARIATE FUNCTIONS
 * BY D.F. SHANNO AND K.H. PHUA
 * ACM TRANSACTIONS ON MATHEMATICAL SOFTWARE 6 (DECEMBER 1980), 618-622
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class Minimizer {

   double D0 = 0.01, D1 = 0.05; // convergence for func & RMS grads
   double EPS = 0.001;
   double ACC = 10.e-20;
   boolean fun_and_grad = true;
   int ninterp = 100; // maximum number of linear interpolations
   int MXFUN = 10000;
   int IOUT = 1;
   int NMETH = 0;
   int STEALTH = 0; // Overwrites IOUT...

   // --- Result of optimization

   double F; // THE LOWEST VALUE OF THE OBJECT FUNCTION OBTAINED
   int IFUN; // UPON EXITING FROM CONMIN,IFUN CONTAINS THE  NUMBER OF TIMES
   // THE FUNCTION AND GRADIENT HAVE BEEN EVALUATED.
   int ITER; // UPON EXITING FROM CONMIN,ITER CONTAINS THE
   // TOTAL NUMBER OF SEARCH DIRECTIONS CALCULATED
   // TO OBTAIN THE CURRENT ESTIMATE TO THE MINIZER.


   public Minimizer() {
   }

   /*
      public int minimizeFun(double[] X, double DMAX,
                             MinimizedFunctionInterface CALCFG) throws Exception {
//
// PURPOSE:    SUBROUTINE CONMIN MINIMIZES AN UNCONSTRAINED NONLINEAR
//             SCALAR VALUED FUNCTION OF A VECTOR VARIABLE X
//            EITHER BY THE BFGS VARIABLE METRIC ALGORITHM OR BY A
//            BEALE RESTARTED CONJUGATE GRADIENT ALGORITHM.
//
//USAGE:      CONMIN(N,X,F,G,IFUN,ITER,EPS,NFLAG,MXFUN,W,
//            IOUT,MDIM,IDEV,ACC,NMETH)
//
//PARAMETERS: N      THE NUMBER OF VARIABLES IN THE FUNCTION TO
//                   BE MINIMIZED.
//            X      THE VECTOR CONTAINING THE CURRENT ESTIMATE TO
//                   THE MINIMIZER. ON ENTRY TO CONMIN,X MUST CONTAIN
//                   AN INITIAL ESTIMATE SUPPLIED BY THE USER.
//                   ON EXITING,X WILL HOLD THE BEST ESTIMATE TO THE
//                   MINIMIZER OBTAINED BY CONMIN. X MUST BE DOUBLE
//                   PRECISIONED AND DIMENSIONED N.
//            F      ON EXITING FROM CONMIN,F WILL CONTAIN THE LOWEST
//                   VALUE OF THE OBJECT FUNCTION OBTAINED.
//                   F IS DOUBLE PRECISIONED.
//*           G      ON EXITING FROM CONMIN,G WILL CONTAIN THE
//*                  ELEMENTS OF THE GRADIENT OF F EVALUATED AT THE
//*                  POINT CONTAINED IN X. G MUST BE DOUBLE
//*                  PRECISIONED AND DIMENSIONED N.
//            IFUN   UPON EXITING FROM CONMIN,IFUN CONTAINS THE
//                   NUMBER OF TIMES THE FUNCTION AND GRADIENT
//                   HAVE BEEN EVALUATED.
//            ITER   UPON EXITING FROM CONMIN,ITER CONTAINS THE
//                   TOTAL NUMBER OF SEARCH DIRECTIONS CALCULATED
//                   TO OBTAIN THE CURRENT ESTIMATE TO THE MINIZER.
//            EPS    EPS IS THE USER SUPPLIED CONVERGENCE PARAMETER.
//                   CONVERGENCE OCCURS WHEN THE NORM OF THE GRADIENT
//                   IS LESS THAN OR EQUAL TO EPS TIMES THE MAXIMUM
//                   OF ONE AND THE NORM OF THE VECTOR X. EPS
//                   MUST BE DOUBLE PRECISIONED.
//            NFLAG  UPON EXITING FROM CONMIN,NFLAG STATES WHICH
//                   CONDITION CAUSED THE EXIT.
//                   IF NFLAG=0, THE ALGORITHM HAS CONVERGED.
//                   IF NFLAG=1, THE MAXIMUM NUMBER OF FUNCTION
//                      EVALUATIONS HAVE BEEN USED.
//                   IF NFLAG=2, THE LINEAR SEARCH HAS FAILED TO
//                      IMPROVE THE FUNCTION VALUE. THIS IS THE
//                      USUAL EXIT IF EITHER THE FUNCTION OR THE
//                      GRADIENT IS INCORRECTLY CODED.
//                   IF NFLAG=3, THE SEARCH VECTOR WAS NOT
//                      A DESCENT DIRECTION. THIS CAN ONLY BE CAUSED
//                      BY ROUNDOFF,AND MAY SUGGEST THAT THE
//                      CONVERGENCE CRITERION IS TOO STRICT.
//            MXFUN  MXFUN IS THE USER SUPPLIED MAXIMUM NUMBER OF
//                   FUNCTION AND GRADIENT CALLS THAT CONMIN WILL
//                   BE ALLOWED TO MAKE.
//*            W      W IS A VECTOR OF WORKING STORAGE.IF NMETH=0,
//*                   W MUST BE DIMENSIONED 5*N+2. IF NMETH=1,
//*                   W MUST BE DIMENSIONED N*(N+7)/2. IN BOTH CASES,
//*                   W MUST BE DOUBLE PRECISIONED.
//            IOUT   IOUT IS A USER  SUPPLIED OUTPUT PARAMETER.
//                   IF IOUT = 0, THERE IS NO PRINTED OUTPUT FROM
//                   CONMIN. IF IOUT > 0,THE VALUE OF F AND THE
//                   NORM OF THE GRADIENT SQUARED,AS WELL AS ITER
//                   AND IFUN,ARE WRITTEN EVERY IOUT ITERATIONS.
//*            MDIM   MDIM IS THE USER SUPPLIED DIMENSION OF THE
//*                   VECTOR W. IF NMETH=0,MDIM=5*N+2. IF NMETH=1,
//*                   MDIM=N*(N+7)/2.
//*            IDEV   IDEV IS THE USER SUPPLIED NUMBER OF THE OUTPUT
//*                   DEVICE ON WHICH OUTPUT IS TO BE WRITTEN WHEN
//*                   IOUT>0.
//            ACC    ACC IS A USER SUPPLIED ESTIMATE OF MACHINE
//                   ACCURACY. A LINEAR SEARCH IS UNSUCCESSFULLY
//                   TERMINATED WHEN THE NORM OF THE STEP SIZE
//                   BECOMES SMALLER THAN ACC. IN PRACTICE,
//                   ACC=10.D-20 HAS PROVED SATISFACTORY. ACC IS
//                   DOUBLE PRECISIONED.
//            NMETH  NMETH IS THE USER SUPPLIED VARIABLE WHICH
//                   CHOOSES THE METHOD OF OPTIMIZATION. IF
//                   NMETH=0,A CONJUGATE GRADIENT METHOD IS
//                   USED. IF NMETH=1, THE BFGS METHOD IS USED.
//
//REMARKS:    IN ADDITION TO THE SPECIFIED VALUES IN THE ABOVE
//            ARGUMENT LIST, THE USER MUST SUPPLY A SUBROUTINE
//            CALCFG WHICH CALCULATES THE FUNCTION AND GRADIENT AT
//            X AND PLACES THEM IN F AND G(1),...,G(N) RESPECTIVELY.
//            THE SUBROUTINE MUST HAVE THE FORM:
//                   SUBROUTINE CALCFG(N,X,F,G)
//                   DOUBLE PRECISION X(N),G(N),F
//
//            AN EXAMPLE SUBROUTINE FOR THE ROSENBROCK FUNCTION IS:
//
//                   SUBROUTINE CALCFG(N,X,F,G)
//                   DOUBLE PRECISION X(N),G(N),F,T1,T2
//                   T1=X(2)-X(1)*X(1)
//                   T2=1.0-X(1)
//                   F=100.0*T1*T1+T2*T2
//                   G(1)=-400.0*T1*X(1)-2.0*T2
//                   G(2)=200.0*T1
//                   RETURN
//                   END
//

                                     int NFLAG, I, J, IJ, II, NCONS, NCONS1, NCONS2, NRST, IOUTK;
                       int NRDPI, NRYPI, NGPI, NRD, NCALLS, NX, NG, NRY, NXPI, NGPJ;

// --- Error check...

                       if (X == null || X.length < 1) {
                          throw new Exception(this.getClass().getCanonicalName() +
                                              " : no varibles to optimize: X == null || X.length < 1");
                       }

                       if (CALCFG == null) {
                          throw new Exception(this.getClass().getCanonicalName() +
                                              " : no function was passed to the minimizer");
                       }

                       int N = X.length;

                       double[] G = new double[N];

                       //  W IS A VECTOR OF WORKING STORAGE.IF NMETH=0,
                       //  W MUST BE DIMENSIONED 5*N+2. IF NMETH=1
                       //  W MUST BE DIMENSIONED N*(N+7)/2. IN BOTH CASES,
                       //  W MUST BE DOUBLE PRECISIONED.

                       double[] W;

                       if (NMETH == 0) {
                          W = new double[5 * N + 2];
                       }
                       else if (NMETH == 1) {
                          W = new double[N * (N + 7) / 2];
                       }

                       double FP, FMIN, ALPHA = 1.0, AT, AP, GSQ, DG = 1.0, DG1;
                       // BTW (Vlad) DG was not set in original version... May cause error...
                       // BTW (Vlad) ALPHA was not set in original version... May cause error...
                       double DP, STEP, DAL, U1, U2, U3, U4;
                       double XSQ, RTST;
                       double gmax, grms;
                       int RSW;

                       if (!STEALTH) {
                          if (IOUT != 0) {
                             logger.info(
                                 "\n\n\n     *****************************************************");
                             logger.info(
                                 "     *                                                   *");
                             if (NMETH == 0) {
                                logger.info(
                                    "     *   A BEALE RESTARTED CONJUGATE GRADIENT ALGORITHM  *");
                             }
                             else {
                                logger.info(
                                    "     *          BFGS VARIABLE METRIC ALGORITHM           *");
                             }
                             logger.info(
                                 "     *                                                   *");
                             logger.info(
                                 "     *****************************************************\n\n");
                             logger.info(
                                 "NUMBER OF VARIABLES: %d   GRMS CONVERGENCE: %10.6lf\n", N,
                                 D1);
                          }
                       } // end of if ( !STEALTH )

//
// INITIALIZE ITER,IFUN,NFLAG,AND IOUTK,WHICH COUNTS OUTPUT ITERATIONS.
//
                       ITER = 0;
                       IFUN = 0;
                       IOUTK = 0;
                       NFLAG = 0;
//
// SET PARAMETERS TO EXTRACT VECTORS FROM W.
// W(I) HOLDS THE SEARCH VECTOR,W(NX+I) HOLDS THE BEST CURRENT
// ESTIMATE TO THE MINIMIZER,AND W(NG+I) HOLDS THE GRADIENT
// AT THE BEST CURRENT ESTIMATE.
//
                       NX = N;
                       NG = NX + N;
//
// TEST WHICH METHOD IS BEING USED.
// IF NMETH=0, W(NRY+I) HOLDS THE RESTART Y VECTOR AND
// W(NRD+I) HOLDS THE RESTART SEARCH VECTOR.
//
                       if (NMETH == 1) {
                          //
                          // IF NMETH=1,W(NCONS+I) HOLDS THE APPROXIMATE INVERSE HESSIAN.
                          //
                          NCONS = 3 * N;
                          //goto L10;
                       }
                       else {
                          NRY = NG + N;
                          NRD = NRY + N;
                          NCONS = 5 * N;
                          NCONS1 = NCONS;
                          NCONS2 = NCONS + 1;
                          //goto L20;
                       }

//
//  CALCULATETHE FUNCTION AND GRADIENT AT THE INITIAL
// POINT AND INITIALIZE NRST,WHICH IS USED TO DETERMINE
// WHETHER A BEALE RESTART IS BEING DONE. NRST=N MEANS THAT THIS
// ITERATION IS A RESTART ITERATION. INITIALIZE RSW, WHICH INDICATES
// THAT THE CURRENT SEARCH DIRECTION IS A GRADIENT DIRECTION.
//

                       L20:F = CALCFG(X, G);
                       ++IFUN;
                       NRST = N;
                       RSW = true;
//
// CALCULATE THE INITIAL SEARCH DIRECTION , THE NORM OF X SQUARED,
// AND THE NORM OF G SQUARED. DG1 IS THE CURRENT DIRECTIONAL
// DERIVATIVE,WHILE XSQ AND GSQ ARE THE SQUARED NORMS.
//
                       DG1 = 0.0;
                       XSQ = 0.0;
                       gmax = Math.abs(G[0]);
                       for (I = 0; I < N; I++) {
                          W[I] = -G[I];
                          XSQ += X[I] * X[I];
                          DG1 -= G[I] * G[I];
                          if (gmax < Math.abs(G[I])) {
                             gmax = Math.abs(G[I]);
                          }
                       }
                       GSQ = -DG1;
                       grms = Math.sqrt(GSQ / (double) N);
//
// Check for gradients
//
                       if (grms < D1) {
                          if (!STEALTH) {
                             System.out.print(
                                 "\n\n ***** RMS GRADIENTS AT INITIAL POINT SATISFIES ");
                             System.out.print("CONVERGENCE CRITERIUM\n");
                             PrintConMinRes(F, IFUN, grms, gmax, NFLAG);
                          }
                          return NFLAG;
                       }
                       else if (IOUT != 0) {
                          if (!STEALTH) {
                             String.format(
                                 "\n\n ***** RMS GRADIENTS AT INITIAL POINT: %8.4lf  %lf",
                                 grms, GSQ);
                             String.format("\nMAXIMUM GRADIENT (ABSOLUTE VALUE): %8.3lf\n", gmax);

                          }
                       }
//
// TEST IF THE INITIAL POINT IS THE MINIMIZER. | VLAD - SKIP THIS CHECK
//
//      if ( GSQ <= EPS*EPS*MAX(1.0,XSQ) ) {
//        cout<<"\n\n ***** THE ALGORITHM HAS CONVERGED *****\n\n  RMS GRADIENT : "
//            <<grms;
//        cout.flush();
//        return NFLAG;
//        }
//
// BEGIN THE MAJOR ITERATION LOOP. NCALLS IS USED TO GUARANTEE THAT
// AT LEAST TWO POINTS HAVE BEEN TRIED WHEN NMETH=0. FMIN IS THE
// CURRENT FUNCTION VALUE.
//

                       while (true) {
                          //L40:FMIN = F;
                          FMIN = F;
                          NCALLS = IFUN;

                          //
                          // IF OUTPUT IS DESIRED,TEST IF THIS IS THE CORRECT ITERATION
                          // AND IF SO, WRITE OUTPUT.
                          //

                          //if (IOUT == 0) {
                          //   goto L60;
                          //}
                          if (IOUTK != 0) {
                             //goto L50;
                             //}
                             if (!STEALTH) {
                                System.out.print("\n ITERATION " + ITER + "     FUNCTION CALLS " +
                                                 IFUN + "\n F = " + FMIN);
                                System.out.print(" G-RMS = " + Math.sqrt(GSQ / (double) N) +
                                                 "\n");
                             }

                             //L50:++IOUTK;
                             ++IOUTK;
                             if (IOUTK == IOUT) {
                                IOUTK = 0;
                             }
                          }
                          //
                          // BEGIN LINEAR SEARCH. ALPHA IS THE STEPLENGTH.
                          // SET ALPHA TO THE NONRESTART CONJUGATE GRADIENT ALPHA.
                          //

                          //L60:ALPHA *= DG / DG1;
                          ALPHA *= DG / DG1;

                          //
                          // IF NMETH=1 OR A RESTART HAS BEEN PERFORMED, SET ALPHA=1.0.
                          //

                          if (NRST == 1 || NMETH == 1) {
                             ALPHA = 1.0;
                          }

                          //
                          // IF A GRADIENT DIRECTION IS USED, SET ALPHA=1.0/DSQRT(GSQ),
                          // WHICH SCALES THE INITIAL SEARCH VECTOR TO UNITY.
                          //

                          if (RSW == true) {
                             ALPHA = 1.0 / Math.sqrt(GSQ);
                          }
                          //cout<<"\n ALPHA = "<<ALPHA;

                          //
                          // THE LINEAR SEARCH FITS A CUBIC TO F AND DAL, THE FUNCTION AND ITS
                          // DERIVATIVE AT ALPHA, AND TO FP AND DP,THE FUNCTION
                          // AND DERIVATIVE AT THE PREVIOUS TRIAL POINT AP.
                          // INITIALIZE AP ,FP,AND DP.
                          //
                          AP = 0.0;
                          FP = FMIN;
                          DP = DG1;
                          //
                          // SAVE THE CURRENT DERIVATIVE TO SCALE THE NEXT SEARCH VECTOR.
                          //
                          DG = DG1;
                          //
                          // UPDATE THE ITERATION.
                          //
                          ++ITER;
                          //
                          // CALCULATE THE CURRENT STEPLENGTH  AND STORE THE CURRENT X AND G.
                          //
                          STEP = 0.0;
                          for (I = 0; I < N; I++) {
                             STEP += W[I] * W[I];
                             NXPI = NX + I;
                             NGPI = NG + I;
                             W[NXPI] = X[I];
                             W[NGPI] = G[I];
                          }
                          STEP = Math.sqrt(STEP);
                          //cout<<"\n Step: "<<STEP;
                          //
                          // BEGIN THE LINEAR SEARCH ITERATION.
                          // TEST FOR FAILURE OF THE LINEAR SEARCH.
                          //

                          L80:if (ALPHA * STEP > ACC) {
                             //cout<<"\n goto L90";
                             goto L90;
                          }

                          //
                          // TEST IF DIRECTION IS A GRADIENT DIRECTION.
                          //
                          if (RSW == false) {
                             //cout<<"\n goto L20";
                             goto L20;
                          }

                          if (!STEALTH) {
                             System.out.print(
                                 "\n\n ---- THE LINEAR SEARCH HAS FAILED TO IMPROVE THE FUNCTION VALUE");
                             System.out.print(
                                 "\n        THIS IS THE USUAL EXIT IF EITHER THE FUNCTION OR ");
                             System.out.print("\n        THE GRADIENT IS INCORRECTLY CODED");
                          }

                          NFLAG = 2;
                          grms = Math.sqrt(GSQ / (double) N);
                          if (!STEALTH) {
                             System.out.print("\n\n  CURRENT RMS GRADIENT : %lf", grms);
                             PrintConMinRes(F, IFUN, grms, gmax, NFLAG);
                          }
                          return NFLAG;
                          //
                          // CALCULATE THE TRIAL POINT.
                          //

                          double step;
                          L90:for (I = 0; I < N; I++) {
                             NXPI = NX + I;
                             step = ALPHA * W[I];
                             if (Math.abs(step) > DMAX[I]) {
                                step = SIGN(DMAX[I], step);
                             }
                             X[I] = W[NXPI] + step;
                          }
                          //
                          // EVALUATE THE FUNCTION AT THE TRIAL POINT.
                          //
                          F = CALCFG(X, G);
                          //
                          // TEST IF THE MAXIMUM NUMBER OF FUNCTION CALLS HAVE BEEN USED.
                          //
                          ++IFUN;
                          if (IFUN > MXFUN) {
                             NFLAG = 1;

                             gmax = Math.abs(G[0]);
                             for (I = 0; I < N; I++) {
                                GSQ += G[I] * G[I];
                                if (gmax < Math.abs(G[I])) {
                                   gmax = Math.abs(G[I]);
                                }
                             }
                             grms = Math.sqrt(GSQ / (double) N);
                             if (!STEALTH) {
                                PrintConMinRes(F, IFUN, grms, gmax, NFLAG);
                             }
                             return NFLAG;
                          }
                          //
                          // COMPUTE THE DERIVATIVE OF F AT ALPHA.
                          //
                          DAL = 0.0;
                          for (I = 0; I < N; I++) {
                             DAL += G[I] * W[I];
                          }
                          //
                          // TEST WHETHER THE NEW POINT HAS A NEGATIVE SLOPE BUT A HIGHER
                          // FUNCTION VALUE THAN ALPHA=0. IF THIS IS THE CASE,THE SEARCH
                          // HAS PASSED THROUGH A LOCAL MAX AND IS HEADING FOR A DISTANT LOCAL
                          // MINIMUM.
                          //
                          if (F > FMIN && DAL < 0.0) {
                             //goto L160;
                             //
                             // A RELATIVE MAX HAS BEEN PASSED.REDUCE ALPHA AND RESTART THE SEARCH.
                             //

                             //L160:ALPHA /= 3.0;
                             ALPHA /= 3.0;
                             AP = 0.0;
                             FP = FMIN;
                             DP = DG;
                             goto L80;
                          }
                          //
                          // IF NOT, TEST WHETHER THE STEPLENGTH CRITERIA HAVE BEEN MET.
                          //
                          if (F > (FMIN + (double) .0001 * ALPHA * DG) ||
                              Math.abs(DAL / DG) > 0.9) {
                             goto L130;
                          }

                          if (F < FMIN) {
                             goto L170; // --- Vlad
                          }

                          //
                          // IF THEY HAVE BEEN MET, TEST IF TWO POINTS HAVE BEEN TRIED
                          // IF NMETH=0 AND IF THE TRUE LINE MINIMUM HAS NOT BEEN FOUND.
                          //
                          if ( (IFUN - NCALLS) <= 1 && Math.abs(DAL / DG) > EPS &&
                              NMETH == 0) {
                             goto L130;
                          }
                          goto L170;
                          //
                          // A NEW POINT MUST BE TRIED. USE CUBIC INTERPOLATION TO FIND
                          // THE TRIAL POINT AT.
                          //
                          L130:U1 = DP + DAL - 3.0 * (FP - F) / (AP - ALPHA);
                          U2 = U1 * U1 - DP * DAL;
                          if (U2 < 0.0) {
                             U2 = 0.0;
                          }
                          U2 = Math.sqrt(U2);
                          AT = ALPHA -
                              (ALPHA - AP) * (DAL + U2 - U1) / (DAL - DP + 2.0 * U2);
                          //
                          // TEST WHETHER THE LINE MINIMUM HAS BEEN BRACKETED.
                          //
                          if ( (DAL / DP) > 0.0) {
                             goto L140;
                          }
                          //
                          // THE MINIMUM HAS BEEN BRACKETED. TEST WHETHER THE TRIAL POINT LIES
                          // SUFFICIENTLY WITHIN THE BRACKETED INTERVAL.
                          // IF IT DOES NOT, CHOOSE AT AS THE MIDPOINT OF THE INTERVAL.
                          //
                          if (AT < (1.01 * Math.min(ALPHA, AP)) ||
                              AT > (0.99 * Math.max(ALPHA, AP))) {
                             AT = (ALPHA + AP) / 2.0;
                          }
                          goto L150;
                          //
                          // THE MINIMUM HAS NOT BEEN BRACKETED. TEST IF BOTH POINTS ARE
                          // GREATER THAN THE MINIMUM AND THE TRIAL POINT IS SUFFICIENTLY
                          // SMALLER THAN EITHER.
                          //

                          L140:if (DAL > 0.0 && 0.0 < AT && AT < (0.99 * Math.min(AP, ALPHA))) {
                             goto L150;
                          }

                          //
                          // TEST IF BOTH POINTS ARE LESS THAN THE MINIMUM AND THE TRIAL POINT
                          // IS SUFFICIENTLY LARGE.
                          //

                          if (DAL <= 0.0 && AT > ( (double) 1.01 * Math.max(AP, ALPHA))) {
                             goto L150;
                          }

                          //
                          // IF THE TRIAL POINT IS TOO SMALL,DOUBLE THE LARGEST PRIOR POINT.
                          //

                          if (DAL <= 0.0) {
                             AT = (double) 2.0 * Math.max(AP, ALPHA);
                          }

                          //
                          // IF THE TRIAL POINT IS TOO LARGE, HALVE THE SMALLEST PRIOR POINT.
                          //

                          if (DAL > 0.0) {
                             AT = Math.min(AP, ALPHA) / 2.0;
                          }

                          //
                          // SET AP=ALPHA, ALPHA=AT,AND CONTINUE SEARCH.
                          //

                          L150:AP = ALPHA;
                          FP = F;
                          DP = DAL;
                          ALPHA = AT;
                          goto L80;

                          //
                          // A RELATIVE MAX HAS BEEN PASSED.REDUCE ALPHA AND RESTART THE SEARCH.
                          //

                          //L160:ALPHA /= 3.0;
                          //AP = 0.0;
                          //FP = FMIN;
                          //DP = DG;
                          //goto L80;

                          //
                          // THE LINE SEARCH HAS CONVERGED. TEST FOR CONVERGENCE OF THE ALGORITHM.
                          //

                          L170:GSQ = 0.0;
                          XSQ = 0.0;
                          gmax = Math.abs(G[0]);
                          for (I = 0; I < N; I++) {
                             GSQ += G[I] * G[I];
                             XSQ += X[I] * X[I];
                             if (gmax < Math.abs(G[I])) {
                                gmax = Math.abs(G[I]);
                             }
                          }
                          grms = Math.sqrt(GSQ / (double) N);

                          if (Math.abs(F - FMIN) < D0 && grms < D1) {
                             if (!STEALTH) {
                                System.out.print(
                                    "\n\n          ************************************************");
                                System.out.print(
                                    "\n          ***     TESTS ON F AND G WERE SATISFIED      ***");
                                System.out.print(
                                    "\n          ************************************************");
                                PrintConMinRes(F, IFUN, grms, gmax, NFLAG);
                             }
                             return NFLAG;
                          }

                          if (grms < D1) {
                             if (!STEALTH) {
                                System.out.print(
                                    "\n\n          ************************************************");
                                System.out.print(
                                    "\n          ***        TESTS ON G WERE SATISFIED         ***");
                                System.out.print(
                                    "\n          ************************************************");
                                PrintConMinRes(F, IFUN, grms, gmax, NFLAG);
                             }
                             return NFLAG;
                          }

                          if (GSQ <= EPS * EPS * Math.max(1.0, XSQ)) {
                             if (!STEALTH) {
                                System.out.print("\n\n ***** THE ALGORITHM HAS CONVERGED *****");
                                PrintConMinRes(F, IFUN, grms, gmax, NFLAG);
                             }
                             return NFLAG;
                          }

                          //
                          // SEARCH CONTINUES. SET W(I)=ALPHA*W(I),THE FULL STEP VECTOR.
                          //

                          for (I = 0; I < N; I++) {
                             W[I] *= ALPHA;
                          }

                          //
                          // COMPUTE THE NEW SEARCH VECTOR. FIRST TEST WHETHER A
                          // CONJUGATE GRADIENT OR A VARIABLE METRIC VECTOR IS USED.
                          //
                          if (NMETH == 1) {
                             goto L330;
                          }

                          //
                          // CONJUGATE GRADIENT UPDATE SECTION.
                          // TEST IF A POWELL RESTART IS INDICATED.
                          //
                          RTST = 0.0;
                          for (I = 0; I < N; I++) {
                             NGPI = NG + I;
                             RTST += G[I] * W[NGPI];
                          }
                          if (Math.abs(RTST / GSQ) > 0.2) {
                             NRST = N;
                          }

                          //
                          // IF A RESTART IS INDICATED, SAVE THE CURRENT D AND Y
                          // AS THE BEALE RESTART VECTORS AND SAVE D'Y AND Y'Y
                          // IN W(NCONS+1) AND W(NCONS+2).
                          //

                          if (NRST != N) {
                             goto L220;
                          }
                          //      W(NCONS+1)=0.
                          //      W(NCONS+2)=0.
                          W[NCONS] = 0.0;
                          W[NCONS + 1] = 0.0;
                          for (I = 0; I < N; I++) {
                             NRDPI = NRD + I;
                             NRYPI = NRY + I;
                             NGPI = NG + I;
                             W[NRYPI] = G[I] - W[NGPI];
                             W[NRDPI] = W[I];
                             W[NCONS1] += W[NRYPI] * W[NRYPI];
                             W[NCONS2] += W[I] * W[NRYPI];
                          }

                          //
                          // CALCULATE  THE RESTART HESSIAN TIMES THE CURRENT GRADIENT.
                          //

                          L220:U1 = 0.0;
                          U2 = 0.0;
                          for (I = 0; I < N; I++) {
                             NRDPI = NRD + I;
                             NRYPI = NRY + I;
                             U1 -= W[NRDPI] * G[I] / W[NCONS1];
                             U2 += W[NRDPI] * G[I] * 2. / W[NCONS2] -
                                 W[NRYPI] * G[I] / W[NCONS1];
                          }

                          U3 = W[NCONS2] / W[NCONS1];
                          for (I = 0; I < N; I++) {
                             NXPI = NX + I;
                             NRDPI = NRD + I;
                             NRYPI = NRY + I;
                             W[NXPI] = -U3 * G[I] - U1 * W[NRYPI] - U2 * W[NRDPI];
                          }
                          //
                          // IF THIS IS A RESTART ITERATION,W(NX+I) CONTAINS THE NEW SEARCH
                          // VECTOR.
                          //

                          if (NRST == N) {
                             goto L300;
                          }

                          //
                          // NOT A RESTART ITERATION. CALCULATE THE RESTART HESSIAN
                          // TIMES THE CURRENT Y.
                          //

                          U1 = 0.0;
                          U2 = 0.0;
                          U3 = 0.0;
                          U4 = 0.0;
                          for (I = 0; I < N; I++) {
                             NGPI = NG + I;
                             NRDPI = NRD + I;
                             NRYPI = NRY + I;
                             U1 -= (G[I] - W[NGPI]) * W[NRDPI] / W[NCONS1];
                             U2 -= (G[I] - W[NGPI]) * W[NRYPI] / W[NCONS1] -
                                 2.0 * W[NRDPI] * (G[I] - W[NGPI]) / W[NCONS2];
                             U3 += W[I] * (G[I] - W[NGPI]);
                          }

                          STEP = 0.0;
                          for (I = 0; I < N; I++) {
                             NGPI = NG + I;
                             NRDPI = NRD + I;
                             NRYPI = NRY + I;
                             STEP = (W[NCONS2] / W[NCONS1]) * (G[I] - W[NGPI]) +
                                 U1 * W[NRYPI] + U2 * W[NRDPI];
                             U4 += STEP * (G[I] - W[NGPI]);
                             W[NGPI] = STEP;
                          }
                          //
                          // CALCULATE THE DOUBLY UPDATED HESSIAN TIMES THE CURRENT
                          // GRADIENT TO OBTAIN THE SEARCH VECTOR.
                          //
                          U1 = 0.0;
                          U2 = 0.0;
                          for (I = 0; I < N; I++) {
                             U1 -= W[I] * G[I] / U3;
                             NGPI = NG + I;
                             U2 += (1.0 + U4 / U3) * W[I] * G[I] / U3 - W[NGPI] * G[I] / U3;
                          }
                          for (I = 0; I < N; I++) {
                             NGPI = NG + I;
                             NXPI = NX + I;
                             W[NXPI] = W[NXPI] - U1 * W[NGPI] - U2 * W[I];
                          }
                          //
                          // CALCULATE THE DERIVATIVE ALONG THE NEW SEARCH VECTOR.
                          //

                          L300:DG1 = 0.0;
                          for (I = 0; I < N; I++) {
                             NXPI = NX + I;
                             W[I] = W[NXPI];
                             DG1 += W[I] * G[I];
                          }
                          //
                          // IF THE NEW DIRECTION IS NOT A DESCENT DIRECTION,STOP.
                          //

                          if (DG1 > 0.0) {
                             //goto L320;
                             //L320:
                             //      printf("\n\n ----- THE SEARCH VECTOR WAS NOT A DESCENT DIRECTION");
                             //      printf("\n          THIS CAN ONLY BE CAUSED BY ROUNDOFF, AND MAY SUGGEST");
                             //      printf("\n          THAT THE CONVERGENCE CRITERION IS TOO STRICT");
                             //      printf("\n\n  CURRENT RMS GRADIENT : %lf",sqrt(GSQ/(double)N));
                             NFLAG = 3;
                             gmax = Math.abs(G[0]);
                             for (I = 0; I < N; I++) {
                                GSQ += G[I] * G[I];
                                if (gmax < Math.abs(G[I])) {
                                   gmax = Math.abs(G[I]);
                                }
                             }
                             grms = Math.sqrt(GSQ / (double) N);
                             if (!STEALTH) {
                                PrintConMinRes(F, IFUN, grms, gmax, NFLAG);
                             }
                             return NFLAG;
                          }

                          //
                          // UPDATE NRST TO ASSURE AT LEAST ONE RESTART EVERY N ITERATIONS.
                          //
                          if (NRST == N) {
                             NRST = 0;
                          }
                          ++NRST;
                          RSW = false;
                          //goto L40;
                          continue;
                          //
                          // ROUNDOFF HAS PRODUCED A BAD DIRECTION.
                          //

                          L320:
                              //      printf("\n\n ----- THE SEARCH VECTOR WAS NOT A DESCENT DIRECTION");
                              //      printf("\n          THIS CAN ONLY BE CAUSED BY ROUNDOFF, AND MAY SUGGEST");
                              //      printf("\n          THAT THE CONVERGENCE CRITERION IS TOO STRICT");
                              //      printf("\n\n  CURRENT RMS GRADIENT : %lf",sqrt(GSQ/(double)N));
                              //      fflush(stdout);
                              NFLAG = 3;
                          gmax = Math.abs(G[0]);
                          for (I = 0; I < N; I++) {
                             GSQ += G[I] * G[I];
                             if (gmax < Math.abs(G[I])) {
                                gmax = Math.abs(G[I]);
                             }
                          }
                          grms = Math.sqrt(GSQ / (double) N);
                          if (!STEALTH) {
                             PrintConMinRes(F, IFUN, grms, gmax, NFLAG);
                          }
                          return NFLAG;

                          //
                          // A VARIABLE METRIC ALGORITM IS BEING USED. CALCULATE Y AND D'Y.
                          //

                          L330:U1 = 0.0;
                          for (I = 0; I < N; I++) {
                             NGPI = NG + I;
                             W[NGPI] = G[I] - W[NGPI];
                             U1 += W[I] * W[NGPI];
                          }
                          //cout<<"\n L330: U1 "<<U1;
                          //
                          // IF RSW=.TRUE.,SET UP THE INITIAL SCALED APPROXIMATE HESSIAN.
                          //

                          if (RSW == false) {
                             goto L380;
                          }

                          //C
                          //C CALCULATE Y'Y.
                          //C
                          U2 = 0.0;
                          for (I = 0; I < N; I++) {
                             NGPI = NG + I;
                             U2 += W[NGPI] * W[NGPI];
                          }
                          //
                          // CALCULATE THE INITIAL HESSIAN AS H=(P'Y/Y'Y)*I
                          // AND THE INITIAL U2=Y'HY AND W(NX+I)=HY.
                          //
                          IJ = 0;
                          U3 = U1 / U2;
                          for (I = 0; I < N; I++) {
                             for (J = I; J < N; J++) {
                                NCONS1 = NCONS + IJ;
                                W[NCONS1] = 0.0;
                                if (I == J) {
                                   W[NCONS1] = U3;
                                }
                                ++IJ;
                             }
                             NXPI = NX + I;
                             NGPI = NG + I;
                             W[NXPI] = U3 * W[NGPI];
                          }
                          U2 = U3 * U2;
                          goto L430;

                          //
                          // CALCULATE W(NX+I)=HY AND U2=Y'HY.
                          //

                          L380:U2 = 0.0;
                          for (I = 0; I < N; I++) {
                             U3 = 0.0;
                             IJ = I;
                             if (I == 0) {
                                goto L400;
                             }
                             II = I - 1;
                             for (J = 0; J <= II; J++) {
                                NGPJ = NG + J;
                                NCONS1 = NCONS + IJ;
                                U3 += W[NCONS1] * W[NGPJ];
                                IJ += N - J - 1; // ??????????????
                             }
                             L400:for (J = I; J < N; J++) {
                                NCONS1 = NCONS + IJ;
                                NGPJ = NG + J;
                                U3 += W[NCONS1] * W[NGPJ];
                                ++IJ;
                             }
                             NGPI = NG + I;
                             U2 += U3 * W[NGPI];
                             NXPI = NX + I;
                             W[NXPI] = U3;
                          }
                          //
                          // CALCULATE THE UPDATED APPROXIMATE HESSIAN.
                          //
                          L430:U4 = 1.0 + U2 / U1;
                          for (I = 0; I < N; I++) {
                             NXPI = NX + I;
                             NGPI = NG + I;
                             W[NGPI] = U4 * W[I] - W[NXPI];
                          }
                          IJ = 0;
                          for (I = 0; I < N; I++) {
                             NXPI = NX + I;
                             U3 = W[I] / U1;
                             U4 = W[NXPI] / U1;
                             for (J = I; J < N; J++) {
                                NCONS1 = NCONS + IJ;
                                NGPJ = NG + J;
                                W[NCONS1] += U3 * W[NGPJ] - U4 * W[J];
                                ++IJ; // ?????
                             }
                          }
                          //
                          // CALCULATE THE NEW SEARCH DIRECTION W(I)=-HG AND ITS DERIVATIVE.
                          //
                          DG1 = 0.0;
                          for (I = 0; I < N; I++) {
                             U3 = 0.0;
                             IJ = I;
                             //if (I == 0) {
                             //   goto L470;
                             //}
                             if (I != 0) {
                                II = I - 1;
                                for (J = 0; J <= II; J++) {
                                   NCONS1 = NCONS + IJ;
                                   U3 -= W[NCONS1] * G[J];
                                   IJ += N - J - 1;
                                }
                             }
                             //L470:for (J = I; J < N; J++) {
                             for (J = I; J < N; J++) {
                                NCONS1 = NCONS + IJ;
                                U3 -= W[NCONS1] * G[J];
                                ++IJ;
                             }
                             DG1 += U3 * G[I];
                             W[I] = U3;
                          }
                          //
                          // TEST FOR A DOWNHILL DIRECTION.
                          //
                          if (DG1 > 0.0) {
                             //goto L320;
                             //L320:
                             //      printf("\n\n ----- THE SEARCH VECTOR WAS NOT A DESCENT DIRECTION");
                             //      printf("\n          THIS CAN ONLY BE CAUSED BY ROUNDOFF, AND MAY SUGGEST");
                             //      printf("\n          THAT THE CONVERGENCE CRITERION IS TOO STRICT");
                             //      printf("\n\n  CURRENT RMS GRADIENT : %lf",sqrt(GSQ/(double)N));

                             NFLAG = 3;
                             gmax = Math.abs(G[0]);
                             for (I = 0; I < N; I++) {
                                GSQ += G[I] * G[I];
                                if (gmax < Math.abs(G[I])) {
                                   gmax = Math.abs(G[I]);
                                }
                             }
                             grms = Math.sqrt(GSQ / (double) N);
                             if (!STEALTH) {
                                PrintConMinRes(F, IFUN, grms, gmax, NFLAG);
                             }
                             return NFLAG;
                          }

                          RSW = false;
                          //goto L40;
                       }
                    } // --- End of CONMIN

                  */

                 void PrintConMinRes(double FUN, int NGRAD, double DGG, double GMAX,
                                     int NFLAG) {

//       *
//       PRINT RESULTS OF OPTIMIZATION
//       *

                    System.out.println("\n\n***  OPTIMIZATION SUMMARY   ***");
                    System.out.println("\n   FUNCTION       GRMS      MAX GRAD   # CALLS   COND");
                    System.out.printf(" %10.4f   %10.4f  %10.4f   %6d     %2d\n", FUN, DGG,
                                  GMAX,NGRAD, NFLAG);
                    System.out.println("===> ");

                    if (NFLAG == 0) {
                       System.out.print("THE ALGORITHM HAS CONVERGED");
                    }
                    else if (NFLAG == 1) {
                       System.out.print(
                           "THE MAXIMUM NUMBER OF FUNCTION EVALUATIONS HAVE BEEN USED");
                    }
                    else if (NFLAG == 2) {
                       System.out.print(
                           "THE LINEAR SEARCH HAS FAILED TO IMPROVE THE FUNCTION VALUE. THIS IS THE");
                       System.out.print(
                           "\nUSUAL EXIT IF EITHER THE FUNCTION OR THE GRADIENT IS INCORRECTLY CODED");

                    }
                    else if (NFLAG == 3) {
                       System.out.print(
                           "THE SEARCH VECTOR WAS NOT A DESCENT DIRECTION. THIS CAN ONLY BE CAUSED");
                       System.out.print(
                           "\nBY ROUNDOFF,AND MAY SUGGEST THAT THE CONVERGENCE CRITERION IS TOO STRICT");

                    }
                    System.out.print("\n\n");
                 }

   double SIGN(double A, double B) {
      if (B >= 0.0) {
         return A > 0.0 ? A : -A;
      }
      else {
         return A < 0.0 ? A : -A;
      }
   }

   public static void main(String[] args) {
      Minimizer minimizer = new Minimizer();
   }
}
