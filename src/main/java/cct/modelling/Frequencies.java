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

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class Frequencies {

   private static double inc_freq = 100;
   private static double fullWidth_at_HalfMaximum = 4;

   public Frequencies() {
   }

   static public void makeCurve(double[] freqs, double[] values, double[] x, double[] y, int dim, VIBRATIONAL_SPECTRUM_CURVE curve_type) {
      double start = freqs[0] - inc_freq;
      double step = (freqs[freqs.length - 1] + inc_freq - start) / (dim - 1);
      for (int i = 0; i < dim; i++) {
         x[i] = start;
         start += step;
         y[i] = calculateIntensity(freqs, values, x[i], curve_type);
      }
   }

   /**
    * Calculates intensity at point x
    * @param freqs double[]
    * @param values double[]
    * @param x double
    * @param curve_type VIBRATIONAL_SPECTRUM_CURVE
    * @return double
    */
   static public double calculateIntensity(double[] freqs, double[] values, double x, VIBRATIONAL_SPECTRUM_CURVE curve_type) {
      double intensity = 0;
      double half_max_sq = fullWidth_at_HalfMaximum * fullWidth_at_HalfMaximum;
      for (int i = 0; i < freqs.length; i++) {
         double displ = x - freqs[i];
         switch (curve_type) {
            case GAUSSIAN:
               intensity += values[i] * Math.exp( - (displ * displ) / (2.0 * half_max_sq));
               break;
            case LORENTZ:
               intensity += values[i] * fullWidth_at_HalfMaximum / (displ * displ + half_max_sq);
               break;
            case GAUSSIAN_LORENTZ:
            case VOIGHT:
               break;

         }
      }
      return intensity;
   }

   public static void main(String[] args) {
      Frequencies frequencies = new Frequencies();
   }
}
