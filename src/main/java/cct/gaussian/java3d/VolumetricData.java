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

package cct.gaussian.java3d;

import java.util.logging.Logger;

import cct.interfaces.ImplicitFunctionInterface;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class VolumetricData implements ImplicitFunctionInterface {

    double data[][][] = null;
    float dataOrigin[] = null;
    int numberOfVoxels[];
    float axisVectors[][];
    double delta = 0.0005f;
    double max = 0, min = 0;

    float[] xvalues = new float[4];
    float[] yvalues = new float[4];
    float[] fun = new float[4];

    float[] xv = new float[8];
    float[] yv = new float[8];
    float[] zv = new float[8];
    float[] fv = new float[8];

    int maxErrorsOutput = 5;
    int errorsCount = 0;
    static final Logger logger = Logger.getLogger(VolumetricData.class.getCanonicalName());

    public VolumetricData(double _data[][][], float _dataOrigin[], int _numberOfVoxels[], float _axisVectors[][], float _delta) {
        data = _data;
        dataOrigin = _dataOrigin;
        numberOfVoxels = _numberOfVoxels;
        axisVectors = _axisVectors;
        delta = _delta;

        min = data[0][0][0];
        max = data[0][0][0];
        for (int i = 0; i < numberOfVoxels[0]; i++) {
            for (int j = 0; j < numberOfVoxels[1]; j++) {
                for (int k = 0; k < numberOfVoxels[2]; k++) {
                    if (data[i][j][k] > max) {
                        max = data[i][j][k];
                    } else if (data[i][j][k] < min) {
                        min = data[i][j][k];
                    }
                }
            }
        }
        logger.info("Min=" + min + " Max=" + max);
    }

    @Override
    public double eval(float x, float y, float z) {

        if (x < dataOrigin[0]) {
            if (errorsCount <= maxErrorsOutput) {
                System.err.println("eval: x out of lower bound " + dataOrigin[0] + " : " + x);
            }
            if (errorsCount == maxErrorsOutput) {
                System.err.println("Further errors output will be supressed");
            }
            ++errorsCount;
            return 0;
        }

        if (y < dataOrigin[1]) {
            if (errorsCount <= maxErrorsOutput) {
                System.err.println("eval: y out of lower bound " + dataOrigin[1] + " : " + y);
            }
            if (errorsCount == maxErrorsOutput) {
                System.err.println("Further errors output will be supressed");
            }
            ++errorsCount;
            return 0;
        }

        if (z < dataOrigin[2]) {
            if (errorsCount <= maxErrorsOutput) {
                System.err.println("eval: z out of lower bound " + dataOrigin[2] + " : " + z);
            }
            if (errorsCount == maxErrorsOutput) {
                System.err.println("Further errors output will be supressed");
            }
            ++errorsCount;
            return 0;
        }

        float local_x = (x - dataOrigin[0]) / axisVectors[0][0];
        float local_y = (y - dataOrigin[1]) / axisVectors[1][1];
        float local_z = (z - dataOrigin[2]) / axisVectors[2][2];

        int start_x = (int) local_x;
        if ((start_x + 1) - local_x < 0.001) {
            start_x += 1;
        }
        int end_x = start_x;
        float x_start = dataOrigin[0] + (start_x) * axisVectors[0][0];
        //if (Math.abs(x_start - x) > 0.0001) {
        if ((x - x_start) > 0.001) {
            end_x = start_x + 1;
        }

        int start_y = (int) local_y;
        if ((start_y + 1) - local_y < 0.001) {
            start_y += 1;
        }
        int end_y = start_y;
        float y_start = dataOrigin[1] + start_y * axisVectors[1][1];
        if (Math.abs(y_start - y) > 0.0001) {
            end_y = start_y + 1;
        }

        int start_z = (int) local_z;
        if ((start_z + 1) - local_z < 0.001) {
            start_z += 1;
        }
        int end_z = start_z;
        float z_start = dataOrigin[2] + start_z * axisVectors[2][2];
        if (Math.abs(z_start - z) > 0.0001) {
            end_z = start_z + 1;
        }

        if (end_x == start_x && end_y == start_y && end_z == start_z) {
            //logger.info("Exact");
            return data[start_x][start_y][start_z] - delta;
        }

        // --- Error check

        if (start_x < 0 || start_x >= data.length) {
            if (errorsCount <= maxErrorsOutput) {
                System.err.println("start_x(" + start_x + ") < 0 || start_x >= data.length(" + data.length + ")");
            }
            if (errorsCount == maxErrorsOutput) {
                System.err.println("Further errors output will be supressed");
            }
            ++errorsCount;
            return 0;
        }

        if (end_x >= data.length) {
            if (errorsCount <= maxErrorsOutput) {
                System.err.println("end_x(" + end_x + ") >= data.length(" + data.length + ")");
            }
            if (errorsCount == maxErrorsOutput) {
                System.err.println("Further errors output will be supressed");
            }
            ++errorsCount;
            return 0;
        }

        if (start_y < 0 || start_y >= data[0].length) {
            if (errorsCount <= maxErrorsOutput) {
                System.err.println("start_y(" + start_y + ") < 0 || start_y >= data.length(" + data[0].length + ")");
            }
            if (errorsCount == maxErrorsOutput) {
                System.err.println("Further errors output will be supressed");
            }
            ++errorsCount;
            return 0;
        }

        if (end_y >= data[0].length) {
            if (errorsCount <= maxErrorsOutput) {
                System.err.println("end_y(" + end_y + ") >= data.length(" + data[0].length + ")");
            }
            if (errorsCount == maxErrorsOutput) {
                System.err.println("Further errors output will be supressed");
            }
            ++errorsCount;
            return 0;
        }

        if (start_z < 0 || start_z >= data[0][0].length) {
            if (errorsCount <= maxErrorsOutput) {
                System.err.println("start_z(" + start_z + ") < 0 || start_z >= data.length(" + data[0][0].length + ")");
            }
            if (errorsCount == maxErrorsOutput) {
                System.err.println("Further errors output will be supressed");
            }
            ++errorsCount;
            return 0;
        }

        if (end_z >= data[0][0].length) {
            if (errorsCount <= maxErrorsOutput) {
                System.err.println("end_z(" + end_z + ") >= data.length(" + data[0][0].length + ")");
            }
            if (errorsCount == maxErrorsOutput) {
                System.err.println("Further errors output will be supressed");
            }
            ++errorsCount;
            return 0;
        }

        /*
               // --- Along X
               if (end_y == start_y && end_z == start_z) {
           float a = (data[end_x][start_y][start_z] -
                      data[start_x][start_y][start_z]) / axisVectors[0][0];
           float c = data[start_x][start_y][start_z] - a * x_start;
           float fun = a * x + c;
           logger.info("Exact Along X: f(" + x + ")=" + fun + " Y0(" + x_start + ")=" +
                              data[start_x][start_y][start_z] + " Y1(" + (x_start + axisVectors[0][0]) + ")=" +
                              data[end_x][start_y][start_z]);
           return fun - delta;
               }

               // --- Along Y
               if (end_x == start_x && end_z == start_z) {
           float a = (data[start_x][end_y][start_z] -
                      data[start_x][start_y][start_z]) / axisVectors[1][1];
           float c = data[start_x][start_y][start_z] - a * y_start;
           float fun = a * y + c;
           //logger.info("Exact Along Y: " + fun+ " Y0=" +
           //                   data[start_x][start_y][start_z] + " Y1=" +
           //                   data[start_x][end_y][start_z]);
           return fun - delta;
               }

               // --- Along Z
               if (end_x == start_x && end_y == start_y) {
           float a = (data[start_x][start_y][end_z] -
                      data[start_x][start_y][start_z]) / axisVectors[2][2];
           float c = data[start_x][start_y][start_z] - a * z_start;
           float fun = a * z + c;
           //logger.info("Exact Along Z: " + fun+ " Y0=" +
           //                   data[start_x][start_y][start_z] + " Y1=" +
           //                   data[start_x][start_y][end_z]);
           return fun - delta;
               }

               // --- In Z-plane
               if (end_z == start_z) {
           xvalues[0] = x_start;
           yvalues[0] = y_start;
           fun[0] = data[start_x][start_y][start_z];

           xvalues[1] = x_start + axisVectors[0][0];
           yvalues[1] = y_start;
           fun[1] = data[start_x + 1][start_y][start_z];

           xvalues[2] = x_start + axisVectors[0][0];
           yvalues[2] = y_start + axisVectors[1][1];
           fun[2] = data[start_x + 1][start_y + 1][start_z];

           xvalues[3] = x_start;
           yvalues[3] = y_start + axisVectors[1][1];
           fun[3] = data[start_x][start_y + 1][start_z];

           try {
              float func = interpolate(xvalues, yvalues, fun, x, y);
              logger.info("In Z-plane : " + func + "\n Y0=" + fun[0] + " Y1=" + fun[1] + " Y2=" + fun[2] + " Y3=" + fun[3]);
              return func - delta;
           }
           catch (Exception ex) {
              System.err.println("In Z-plane: Error: " + ex.getMessage());
              return 0;
           }
               }

               // --- In Y-plane
               else if (end_y == start_y) {
           xvalues[0] = x_start;
           yvalues[0] = z_start;
           fun[0] = data[start_x][start_y][start_z];

           xvalues[1] = x_start + axisVectors[0][0];
           yvalues[1] = z_start;
           fun[1] = data[start_x + 1][start_y][start_z];

           xvalues[2] = x_start + axisVectors[0][0];
           yvalues[2] = z_start + axisVectors[2][2];
           fun[2] = data[start_x + 1][start_y][start_z + 1];

           xvalues[3] = x_start;
           yvalues[3] = z_start + axisVectors[2][2];
           fun[3] = data[start_x][start_y][start_z + 1];

           try {
              float func = interpolate(xvalues, yvalues, fun, x, z);
              logger.info("In Y-plane : " + func + "\n Y0=" + fun[0] + " Y1=" + fun[1] + " Y2=" + fun[2] + " Y3=" + fun[3]);
              return func - delta;
           }
           catch (Exception ex) {
              System.err.println("In Y-plane: Error: " + ex.getMessage());
              return 0;
           }
               }

               // --- In X-plane
               else if (end_x == start_x) {
           xvalues[0] = y_start;
           yvalues[0] = z_start;
           fun[0] = data[start_x][start_y][start_z];

           xvalues[1] = y_start + axisVectors[1][1];
           yvalues[1] = z_start;
           fun[1] = data[start_x][start_y + 1][start_z];

           xvalues[2] = y_start + axisVectors[1][1];
           yvalues[2] = z_start + axisVectors[2][2];
           fun[2] = data[start_x][start_y + 1][start_z + 1];

           xvalues[3] = y_start;
           yvalues[3] = z_start + axisVectors[2][2];
           fun[3] = data[start_x][start_y][start_z + 1];

           try {
              float func = interpolate(xvalues, yvalues, fun, y, z);
              logger.info("In X-plane : " + func + "\n Y0=" + fun[0] + " Y1=" + fun[1] + " Y2=" + fun[2] + " Y3=" + fun[3]);
              return func - delta;
           }
           catch (Exception ex) {
              System.err.println("In X-plane: Error: " + ex.getMessage());
              return 0;
           }
               }

               // --- General case...

               xv[0] = x_start;
               yv[0] = y_start;
               zv[0] = z_start;
               fv[0] = data[start_x][start_y][start_z];

               xv[1] = x_start + axisVectors[0][0];
               yv[1] = y_start;
               zv[1] = z_start;
               fv[1] = data[start_x + 1][start_y][start_z];

               xv[2] = x_start + axisVectors[0][0];
               yv[2] = y_start + axisVectors[1][1];
               zv[2] = z_start;
               fv[2] = data[start_x + 1][start_y + 1][start_z];

               xv[3] = x_start;
               yv[3] = y_start + axisVectors[1][1];
               zv[3] = z_start;
               fv[3] = data[start_x][start_y + 1][start_z];

               xv[4] = x_start;
               yv[4] = y_start;
               zv[4] = z_start + axisVectors[2][2];
               fv[4] = data[start_x][start_y][start_z + 1];

               xv[5] = x_start + axisVectors[0][0];
               yv[5] = y_start;
               zv[5] = z_start + axisVectors[2][2];
               fv[5] = data[start_x + 1][start_y][start_z + 1];

               xv[6] = x_start + axisVectors[0][0];
               yv[6] = y_start + axisVectors[1][1];
               zv[6] = z_start + axisVectors[2][2];
               fv[6] = data[start_x + 1][start_y + 1][start_z + 1];

               xv[7] = x_start;
               yv[7] = y_start + axisVectors[1][1];
               zv[7] = z_start + axisVectors[2][2];
               fv[7] = data[start_x][start_y + 1][start_z + 1];

               try {
           float func = interpolate(xv, yv, zv, fv, x, y, z);
           return func - delta;
               }
               catch (Exception ex) {
           System.err.println("General case exception: " + ex.getMessage());
               }
         */
        // --- Old stuff

        double fun_y[] = {
                        0, 0};
        double fun_x[] = {
                        0, 0};

        for (int ix = start_x, x_count = 0; ix <= end_x; ix++, x_count++) {

            for (int iy = start_y, y_count = 0; iy <= end_y; iy++, y_count++) {
                if (start_z == end_z) {
                    fun_y[y_count] = data[ix][iy][start_z];
                } else {
                    fun_y[y_count] = data[ix][iy][start_z] +
                                     (z - z_start) / axisVectors[2][2] *
                                     (data[ix][iy][end_z] - data[ix][iy][start_z]);
                    //logger.info("Interpolated Z: "+fun_y[y_count]+" At the corners: "+data[ix][iy][start_z]+" & "+data[ix][iy][end_z]);
                }
            }

            if (start_y == end_y) {
                fun_x[x_count] = fun_y[0];
            } else {
                fun_x[x_count] = fun_y[0] + (y - y_start) / axisVectors[1][1] *
                                 (fun_y[1] - fun_y[0]);
                //logger.info("Interpolated Y: "+fun_x[x_count]+" At the corners: "+fun_y[1]+" & "+fun_y[0]);
            }

        }

        double fun;
        if (start_x == end_x) {
            fun = fun_x[0];
        } else {
            fun = fun_x[0] + (x - x_start) / axisVectors[0][0] *
                  (fun_x[1] - fun_x[0]);
        }

        if (fun < min || fun > max) {
            logger.info("Wrong function: " + fun + " at i, j, k " + start_x +
                               " " + start_y + " " + start_z);
            if (fun < min) {
                fun = min;
            } else if (fun > max) {
                fun = max;
            }
        }

        return fun - delta;
    }

    private float interpolate(float[] xvalues, float[] yvalues, float[] fun, float x, float y) throws Exception {
        if (xvalues.length != yvalues.length || xvalues.length != fun.length) {
            throw new Exception("xvalues.length != yvalues.length || xvalues.length != fun.length");
        }

        float sumXi = 0, sumYi = 0, sumZi = 0, sumXSq_i = 0, sumYSq_i = 0;
        float sumXiYi = 0, sumXiZi = 0, sumYiZi = 0;

        for (int i = 0; i < xvalues.length; i++) {
            sumXi += xvalues[i];
            sumYi += yvalues[i];
            sumZi += fun[i];
            sumXSq_i += xvalues[i] * xvalues[i];
            sumYSq_i += yvalues[i] * yvalues[i];
            sumXiYi += xvalues[i] * yvalues[i];
            sumXiZi += xvalues[i] * fun[i];
            sumYiZi += yvalues[i] * fun[i];
        }

        float d11 = xvalues.length;
        float d12 = sumXi;
        float d13 = sumYi;
        float D1 = sumZi;

        float d21 = sumXi;
        float d22 = sumXSq_i;
        float d23 = sumXiYi;
        float D2 = sumXiZi;

        float d31 = sumYi;
        float d32 = sumXiYi;
        float d33 = sumYSq_i;
        float D3 = sumYiZi;

        float det = d13 * d22 * d31 - d12 * d23 * d31 -
                    d13 * d21 * d32 + d11 * d23 * d32 + d12 * d21 * d33 - d11 * d22 * d33;

        float a = -(d13 * d21 * D3 - d11 * d23 * D3 - d13 * D2 * d31 + D1 * d23 * d31 +
                    d11 * D2 * d33 - D1 * d21 * d33) / det;

        float b = (d12 * d21 * D3 - d11 * d22 * D3 - d12 * D2 * d31 + D1 * d22 * d31 +
                   d11 * D2 * d32 - D1 * d21 * d32) / det;

        float c = (d13 * d22 * D3 - d12 * d23 * D3 - d13 * D2 * d32 + D1 * d23 * d32 +
                   d12 * D2 * d33 - D1 * d22 * d33) / det;

        float func = a * x + b * y + c;

        // --- Error check (debug)



        float min = fun[0], max = fun[0];
        for (int i = 1; i < fun.length; i++) {
            if (min > fun[i]) {
                min = fun[i];
            } else if (max < fun[i]) {
                max = fun[i];
            }
        }
        if (func < min || func > max) {
            //throw new Exception("func < min || func > max : func="+func+" min="+min+" max="+max);
            System.err.println("func < min || func > max : func=" + func + " min=" + min + " max=" + max);
            logger.info("a=" + a + " b=" + b + " c=" + c);
            float err = 0;
            for (int i = 1; i < fun.length; i++) {
                float t = a * xvalues[i] + b * yvalues[i] + c - fun[i];
                err += t * t;
            }
            logger.info("Error: " + err);
            logger.info("x0=" + xvalues[0] + " y0=" + yvalues[0] + " fun=" + fun[0]);
            logger.info("x1=" + xvalues[1] + " y1=" + yvalues[1] + " fun=" + fun[1]);
            logger.info("x2=" + xvalues[2] + " y2=" + yvalues[2] + " fun=" + fun[2]);
            logger.info("x3=" + xvalues[3] + " y3=" + yvalues[3] + " fun=" + fun[3]);
            logger.info("Interpolated: x=" + x + " y=" + y + " fun=" + func);
            //System.exit(0);

            int imin = 0;
            double mind = Math.sqrt((x - xvalues[0]) * (x - xvalues[0]) + (y - yvalues[0]) * (y - yvalues[0]));
            for (int i = 1; i < fun.length; i++) {
                double dist = Math.sqrt((x - xvalues[i]) * (x - xvalues[i]) + (y - yvalues[i]) * (y - yvalues[i]));
                if (mind > dist) {
                    mind = dist;
                    imin = i;
                }
            }
            func = fun[imin];
        }

        return func;
    }

    private float interpolate(float[] xvalues, float[] yvalues, float[] zvalues, float[] fun, float x, float y, float z) throws
            Exception {
        if (xvalues.length != yvalues.length || xvalues.length != fun.length) {
            throw new Exception("xvalues.length != yvalues.length || xvalues.length != fun.length");
        }

        float sumXi = 0, sumYi = 0, sumZi = 0, sumXSq_i = 0, sumYSq_i = 0, sumZSq_i = 0;
        float sumXiYi = 0, sumXiZi = 0, sumYiZi = 0, sumFi = 0, sumXiFi = 0, sumYiFi = 0;
        float sumZiFi = 0;

        for (int i = 0; i < xvalues.length; i++) {
            sumXi += xvalues[i];
            sumYi += yvalues[i];
            sumZi += zvalues[i];
            sumFi += fun[i];

            sumXSq_i += xvalues[i] * xvalues[i];
            sumYSq_i += yvalues[i] * yvalues[i];
            sumZSq_i += zvalues[i] * zvalues[i];

            sumXiYi += xvalues[i] * yvalues[i];
            sumXiZi += xvalues[i] * zvalues[i];
            sumYiZi += yvalues[i] * zvalues[i];

            sumXiFi += xvalues[i] * fun[i];
            sumYiFi += yvalues[i] * fun[i];
            sumZiFi += zvalues[i] * fun[i];
        }

        float d11 = xvalues.length;
        float d12 = sumXi;
        float d13 = sumYi;
        float d14 = sumZi;
        float D1 = sumFi;

        float d21 = sumXi;
        float d22 = sumXSq_i;
        float d23 = sumXiYi;
        float d24 = sumXiZi;
        float D2 = sumXiFi;

        float d31 = sumYi;
        float d32 = sumXiYi;
        float d33 = sumYSq_i;
        float d34 = sumYiZi;
        float D3 = sumYiFi;

        float d41 = sumZi;
        float d42 = sumXiZi;
        float d43 = sumYiZi;
        float d44 = sumZSq_i;
        float D4 = sumZiFi;

        float a = (d14 * d23 * d31 * D4 - d13 * d24 * d31 * D4 - d14 * d21 * d33 * D4 +
                   d11 * d24 * d33 * D4 + d13 * d21 * d34 * D4 -
                   d11 * d23 * d34 * D4 - d14 * d23 * D3 * d41 +
                   d13 * d24 * D3 * d41 + d14 * D2 * d33 * d41 -
                   D1 * d24 * d33 * d41 - d13 * D2 * d34 * d41 +
                   D1 * d23 * d34 * d41 + d14 * d21 * D3 * d43 -
                   d11 * d24 * D3 * d43 - d14 * D2 * d31 * d43 +
                   D1 * d24 * d31 * d43 + d11 * D2 * d34 * d43 -
                   D1 * d21 * d34 * d43 - d13 * d21 * D3 * d44 +
                   d11 * d23 * D3 * d44 + d13 * D2 * d31 * d44 -
                   D1 * d23 * d31 * d44 - d11 * D2 * d33 * d44 + D1 * d21 * d33 * d44) /
                  ( -(d14 * d23 * d32 * d41) + d13 * d24 * d32 * d41 +
                   d14 * d22 * d33 * d41 - d12 * d24 * d33 * d41 -
                   d13 * d22 * d34 * d41 + d12 * d23 * d34 * d41 +
                   d14 * d23 * d31 * d42 - d13 * d24 * d31 * d42 -
                   d14 * d21 * d33 * d42 + d11 * d24 * d33 * d42 +
                   d13 * d21 * d34 * d42 - d11 * d23 * d34 * d42 -
                   d14 * d22 * d31 * d43 + d12 * d24 * d31 * d43 +
                   d14 * d21 * d32 * d43 - d11 * d24 * d32 * d43 -
                   d12 * d21 * d34 * d43 + d11 * d22 * d34 * d43 +
                   d13 * d22 * d31 * d44 - d12 * d23 * d31 * d44 -
                   d13 * d21 * d32 * d44 + d11 * d23 * d32 * d44 +
                   d12 * d21 * d33 * d44 - d11 * d22 * d33 * d44);

        float b = ( -(d14 * d22 * d31 * D4) + d12 * d24 * d31 * D4 + d14 * d21 * d32 * D4 -
                   d11 * d24 * d32 * D4 - d12 * d21 * d34 * D4 +
                   d11 * d22 * d34 * D4 + d14 * d22 * D3 * d41 -
                   d12 * d24 * D3 * d41 - d14 * D2 * d32 * d41 +
                   D1 * d24 * d32 * d41 + d12 * D2 * d34 * d41 -
                   D1 * d22 * d34 * d41 - d14 * d21 * D3 * d42 +
                   d11 * d24 * D3 * d42 + d14 * D2 * d31 * d42 -
                   D1 * d24 * d31 * d42 - d11 * D2 * d34 * d42 +
                   D1 * d21 * d34 * d42 + d12 * d21 * D3 * d44 -
                   d11 * d22 * D3 * d44 - d12 * D2 * d31 * d44 +
                   D1 * d22 * d31 * d44 + d11 * D2 * d32 * d44 - D1 * d21 * d32 * d44) /
                  ( -(d14 * d23 * d32 * d41) + d13 * d24 * d32 * d41 +
                   d14 * d22 * d33 * d41 - d12 * d24 * d33 * d41 -
                   d13 * d22 * d34 * d41 + d12 * d23 * d34 * d41 +
                   d14 * d23 * d31 * d42 - d13 * d24 * d31 * d42 -
                   d14 * d21 * d33 * d42 + d11 * d24 * d33 * d42 +
                   d13 * d21 * d34 * d42 - d11 * d23 * d34 * d42 -
                   d14 * d22 * d31 * d43 + d12 * d24 * d31 * d43 +
                   d14 * d21 * d32 * d43 - d11 * d24 * d32 * d43 -
                   d12 * d21 * d34 * d43 + d11 * d22 * d34 * d43 +
                   d13 * d22 * d31 * d44 - d12 * d23 * d31 * d44 -
                   d13 * d21 * d32 * d44 + d11 * d23 * d32 * d44 +
                   d12 * d21 * d33 * d44 - d11 * d22 * d33 * d44);

        float c = ( -(d13 * d22 * d31 * D4) + d12 * d23 * d31 * D4 + d13 * d21 * d32 * D4 -
                   d11 * d23 * d32 * D4 - d12 * d21 * d33 * D4 +
                   d11 * d22 * d33 * D4 + d13 * d22 * D3 * d41 -
                   d12 * d23 * D3 * d41 - d13 * D2 * d32 * d41 +
                   D1 * d23 * d32 * d41 + d12 * D2 * d33 * d41 -
                   D1 * d22 * d33 * d41 - d13 * d21 * D3 * d42 +
                   d11 * d23 * D3 * d42 + d13 * D2 * d31 * d42 -
                   D1 * d23 * d31 * d42 - d11 * D2 * d33 * d42 +
                   D1 * d21 * d33 * d42 + d12 * d21 * D3 * d43 -
                   d11 * d22 * D3 * d43 - d12 * D2 * d31 * d43 +
                   D1 * d22 * d31 * d43 + d11 * D2 * d32 * d43 - D1 * d21 * d32 * d43) /
                  (d14 * d23 * d32 * d41 - d13 * d24 * d32 * d41 -
                   d14 * d22 * d33 * d41 + d12 * d24 * d33 * d41 +
                   d13 * d22 * d34 * d41 - d12 * d23 * d34 * d41 -
                   d14 * d23 * d31 * d42 + d13 * d24 * d31 * d42 +
                   d14 * d21 * d33 * d42 - d11 * d24 * d33 * d42 -
                   d13 * d21 * d34 * d42 + d11 * d23 * d34 * d42 +
                   d14 * d22 * d31 * d43 - d12 * d24 * d31 * d43 -
                   d14 * d21 * d32 * d43 + d11 * d24 * d32 * d43 +
                   d12 * d21 * d34 * d43 - d11 * d22 * d34 * d43 -
                   d13 * d22 * d31 * d44 + d12 * d23 * d31 * d44 +
                   d13 * d21 * d32 * d44 - d11 * d23 * d32 * d44 -
                   d12 * d21 * d33 * d44 + d11 * d22 * d33 * d44);

        float d = ( -(d14 * d23 * d32 * D4) + d13 * d24 * d32 * D4 + d14 * d22 * d33 * D4 -
                   d12 * d24 * d33 * D4 - d13 * d22 * d34 * D4 +
                   d12 * d23 * d34 * D4 + d14 * d23 * D3 * d42 -
                   d13 * d24 * D3 * d42 - d14 * D2 * d33 * d42 +
                   D1 * d24 * d33 * d42 + d13 * D2 * d34 * d42 -
                   D1 * d23 * d34 * d42 - d14 * d22 * D3 * d43 +
                   d12 * d24 * D3 * d43 + d14 * D2 * d32 * d43 -
                   D1 * d24 * d32 * d43 - d12 * D2 * d34 * d43 +
                   D1 * d22 * d34 * d43 + d13 * d22 * D3 * d44 -
                   d12 * d23 * D3 * d44 - d13 * D2 * d32 * d44 +
                   D1 * d23 * d32 * d44 + d12 * D2 * d33 * d44 - D1 * d22 * d33 * d44) /
                  ( -(d14 * d23 * d32 * d41) + d13 * d24 * d32 * d41 +
                   d14 * d22 * d33 * d41 - d12 * d24 * d33 * d41 -
                   d13 * d22 * d34 * d41 + d12 * d23 * d34 * d41 +
                   d14 * d23 * d31 * d42 - d13 * d24 * d31 * d42 -
                   d14 * d21 * d33 * d42 + d11 * d24 * d33 * d42 +
                   d13 * d21 * d34 * d42 - d11 * d23 * d34 * d42 -
                   d14 * d22 * d31 * d43 + d12 * d24 * d31 * d43 +
                   d14 * d21 * d32 * d43 - d11 * d24 * d32 * d43 -
                   d12 * d21 * d34 * d43 + d11 * d22 * d34 * d43 +
                   d13 * d22 * d31 * d44 - d12 * d23 * d31 * d44 -
                   d13 * d21 * d32 * d44 + d11 * d23 * d32 * d44 +
                   d12 * d21 * d33 * d44 - d11 * d22 * d33 * d44);

        float func = a * x + b * y + c * z + d;

        // --- Error check

        float min = fun[0], max = fun[0];
        for (int i = 1; i < fun.length; i++) {
            if (min > fun[i]) {
                min = fun[i];
            } else if (max < fun[i]) {
                max = fun[i];
            }
        }
        if (func < min || func > max) {
            //throw new Exception("func < min || func > max : func="+func+" min="+min+" max="+max);
            System.err.println("General case: func < min || func > max : func=" + func + " min=" + min + " max=" + max);
            int imin = 0;
            double mind = Math.sqrt((x - xvalues[0]) * (x - xvalues[0]) + (y - yvalues[0]) * (y - yvalues[0]) +
                                    (z - zvalues[0]) * (z - zvalues[0]));
            for (int i = 1; i < fun.length; i++) {
                double dist = Math.sqrt((x - xvalues[i]) * (x - xvalues[i]) + (y - yvalues[i]) * (y - yvalues[i]) +
                                        (z - zvalues[i]) * (z - zvalues[i]));
                if (mind > dist) {
                    mind = dist;
                    imin = i;
                }
            }
            func = fun[imin];

        }

        return func;
    }

}
