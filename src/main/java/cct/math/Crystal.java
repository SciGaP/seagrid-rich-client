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

import cct.Constants;
import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Crystal {

   public static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;

   protected Crystal() {
   }

   public static double[] latticeParamFromLatticeVectors(double[][] latticeVectors) {
      double[] latticeParameters = new double[6];
      double[] d = new double[3];
      for (int i = 0; i < 3; i++) {
         d[i] = Math.sqrt(latticeVectors[i][0] * latticeVectors[i][0] + latticeVectors[i][1] * latticeVectors[i][1] +
                          latticeVectors[i][2] * latticeVectors[i][2]);
         if (d[i] < 0.01) {
            d[i] = 1.0;
         }
         latticeParameters[i] = d[i];
      }

      // --- alpha (between b & c)
      latticeParameters[3] = Math.acos( (latticeVectors[2][0] * latticeVectors[1][0] + latticeVectors[2][1] * latticeVectors[1][1] +
                                         latticeVectors[2][2] * latticeVectors[1][2]) / (d[2] * d[1])) * RADIANS_TO_DEGREES;
      // --- beta (between a & c )
      latticeParameters[4] = Math.acos( (latticeVectors[0][0] * latticeVectors[2][0] + latticeVectors[0][1] * latticeVectors[2][1] +
                                         latticeVectors[0][2] * latticeVectors[2][2]) / (d[0] * d[2])) * RADIANS_TO_DEGREES;
      // --- gamma (between a & b )

      latticeParameters[5] = Math.acos( (latticeVectors[0][0] * latticeVectors[1][0] + latticeVectors[0][1] * latticeVectors[1][1] +
                                         latticeVectors[0][2] * latticeVectors[1][2]) / (d[0] * d[1])) * RADIANS_TO_DEGREES;

      return latticeParameters;
   }

   public static double[] getDefaultLatticeParameters(MoleculeInterface molec, double space) {
      double[] latticeParameters = new double[6];
      latticeParameters[3] = 90.0;
      latticeParameters[4] = 90.0;
      latticeParameters[5] = 90.0;

      if (molec == null || molec.getNumberOfAtoms() < 1) {
         latticeParameters[0] = 1.0 + 2.0 * space;
         latticeParameters[1] = 1.0 + 2.0 * space;
         latticeParameters[2] = 1.0 + 2.0 * space;
         return latticeParameters;
      }
      if (space < 0) {
         space = 0.001;
      }

      AtomInterface atom = molec.getAtomInterface(0);
      double xMin = atom.getX();
      double xMax = atom.getX();
      double yMin = atom.getY();
      double yMax = atom.getY();
      double zMin = atom.getZ();
      double zMax = atom.getZ();

      for (int i = 1; i < molec.getNumberOfAtoms(); i++) {
         atom = molec.getAtomInterface(i);
         if (xMin > atom.getX()) {
            xMin = atom.getX();
         }
         else if (xMax < atom.getX()) {
            xMax = atom.getX();
         }
         if (yMin > atom.getY()) {
            yMin = atom.getY();
         }
         else if (yMax < atom.getY()) {
            yMax = atom.getY();
         }
         if (zMin > atom.getZ()) {
            zMin = atom.getZ();
         }
         else if (zMax < atom.getZ()) {
            zMax = atom.getZ();
         }

      }

      latticeParameters[0] = xMax - xMin + 2.0 * space;
      latticeParameters[1] = yMax - yMin + 2.0 * space;
      latticeParameters[2] = zMax - zMin + 2.0 * space;
      latticeParameters[3] = 90.0;
      latticeParameters[4] = 90.0;
      latticeParameters[5] = 90.0;

      return latticeParameters;
   }

   public static double[][] getCartesianToFractionalTransMatrix(double[] latticePars) {
      double[][] matrix = new double[3][3];

      double a = latticePars[0];
      double b = latticePars[1];
      double c = latticePars[2];
      double alpha = latticePars[3] * Constants.DEGREES_TO_RADIANS;
      double beta = latticePars[4] * Constants.DEGREES_TO_RADIANS;
      double gamma = latticePars[5] * Constants.DEGREES_TO_RADIANS;

      double factor = Math.sqrt(1.0 - Math.cos(alpha) * Math.cos(alpha) - Math.cos(beta) * Math.cos(beta) -
                                Math.cos(gamma) * Math.cos(gamma) + 2.0 * Math.cos(alpha) * Math.cos(beta) * Math.cos(gamma));

      matrix[0][0] = 1.0 / a;
      matrix[0][1] = 0;
      matrix[0][2] = 0;

      matrix[1][0] = -Math.cos(gamma) / (a * Math.sin(gamma));
      matrix[1][1] = 1.0 / (b * Math.sin(gamma));
      matrix[1][2] = 0;

      matrix[2][0] = (Math.cos(alpha) * Math.cos(gamma) - Math.cos(beta)) / (a * Math.sin(gamma) * factor);
      matrix[2][1] = (Math.cos(beta) * Math.cos(gamma) - Math.cos(alpha)) / (b * Math.sin(gamma) * factor);
      matrix[2][2] = Math.sin(gamma) / (c * factor);

      return matrix;
   }

   public static double[][] getCartesianFromFractional(double[][] fractional, int nCenters, double latticeVectors[][]) throws
       Exception {

      double[][] coords = new double[nCenters][3];

      for (int i = 0; i < nCenters; i++) {
         coords[i][0] = fractional[i][0] * latticeVectors[0][0] + fractional[i][1] * latticeVectors[1][0] +
             fractional[i][2] * latticeVectors[2][0];
         coords[i][1] = fractional[i][0] * latticeVectors[0][1] + fractional[i][1] * latticeVectors[1][1] +
             fractional[i][2] * latticeVectors[2][1];
         coords[i][2] = fractional[i][0] * latticeVectors[0][2] + fractional[i][1] * latticeVectors[1][2] +
             fractional[i][2] * latticeVectors[2][2];
      }
      return coords;
   }

   /**
    * Validates lattice parameters. Alpha, beta and gamma are expected in degrees
    * @param a double
    * @param b double
    * @param c double
    * @param alpha double
    * @param beta double
    * @param gamma double
    * @throws Exception
    */
   public static void validateLatticeParameters(double a, double b, double c, double alpha, double beta, double gamma) throws
       Exception {
      if (a <= 0.0) {
         throw new Exception("Lattice parameter \"a\" cannot be less than 0. Got " + a);
      }

      if (b <= 0.0) {
         throw new Exception("Lattice parameter \"b\" cannot be less than 0. Got " + b);
      }

      if (c <= 0.0) {
         throw new Exception("Lattice parameter \"c\" cannot be less than 0. Got " + c);
      }

      if (alpha <= 0.0 || alpha > 180.0) {
         throw new Exception("Lattice parameter \"alpha\" should be 0 < alpha < 180. Got " + alpha);
      }

      if (beta <= 0.0 || beta > 180.0) {
         throw new Exception("Lattice parameter \"beta\" should be 0 < beta < 180. Got " + beta);
      }

      if (gamma <= 0.0 || gamma > 180.0) {
         throw new Exception("Lattice parameter \"gamma\" should be 0 < gamma < 180. Got " + gamma);
      }
   }
}
