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
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author vvv900
 */
public class Statistics {

  public static double mean(double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = 0.0;
    for (double i : x) {
      mean += i;
    }
    return mean / (double) x.length;
  }

  public static double mean(Double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = 0.0;
    for (double i : x) {
      mean += i;
    }
    return mean / (double) x.length;
  }

  public static double mean(float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = 0.0;
    for (double i : x) {
      mean += i;
    }
    return mean / (double) x.length;
  }

  public static double mean(Float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = 0.0;
    for (double i : x) {
      mean += i;
    }
    return mean / (double) x.length;
  }

  public static double mean(List x) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = 0.0;
    for (Object i : x) {
      mean += ((Number) i).doubleValue();
    }
    return mean / (double) x.size();
  }

  public static double mean(Number[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = 0.0;
    for (Number i : x) {
      mean += i.doubleValue();
    }
    return mean / (double) x.length;
  }

  public static double mean(Object[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = 0.0;
    for (Object i : x) {
      mean += ((Number) i).doubleValue();
    }
    return mean / (double) x.length;
  }

  public static double median(double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double[] y = Arrays.copyOf(x, x.length);
    Arrays.sort(y);
    return y.length % 2 == 0 ? 0.5 * (y[y.length / 2] + y[y.length / 2 + 1]) : y[(y.length + 1) / 2];
  }

  public static double median(Double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    Double[] y = Arrays.copyOf(x, x.length);
    Arrays.sort(y);
    return y.length % 2 == 0 ? 0.5 * (y[y.length / 2] + y[y.length / 2 + 1]) : y[(y.length + 1) / 2];
  }

  public static double median(float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    float[] y = Arrays.copyOf(x, x.length);
    Arrays.sort(y);
    return y.length % 2 == 0 ? 0.5 * (y[y.length / 2] + y[y.length / 2 + 1]) : y[(y.length + 1) / 2];
  }

  public static double median(Float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    Float[] y = Arrays.copyOf(x, x.length);
    Arrays.sort(y);
    return y.length % 2 == 0 ? 0.5 * (y[y.length / 2] + y[y.length / 2 + 1]) : y[(y.length + 1) / 2];
  }

  public static double median(Number[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    Number[] y = Arrays.copyOf(x, x.length);
    Arrays.sort(y);
    return y.length % 2 == 0
        ? 0.5 * (y[y.length / 2].doubleValue() + y[y.length / 2 + 1].doubleValue())
        : y[(y.length + 1) / 2].doubleValue();
  }

  public static double median(Object[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    Object[] y = Arrays.copyOf(x, x.length);
    Arrays.sort(y);
    return y.length % 2 == 0
        ? 0.5 * (((Number) y[y.length / 2]).doubleValue() + ((Number) y[y.length / 2 + 1]).doubleValue())
        : ((Number) y[(y.length + 1) / 2]).doubleValue();
  }

  public static double median(List x) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of data array");
    }
    Object[] y = x.toArray();
    Arrays.sort(y);
    return y.length % 2 == 0
        ? 0.5 * (((Number) y[y.length / 2]).doubleValue() + ((Number) y[y.length / 2 + 1]).doubleValue())
        : ((Number) y[(y.length + 1) / 2]).doubleValue();
  }

  /**
   * estimates the mean squared deviation of x from its mean value
   *
   * @param x - 1d array
   * @return
   * @throws Exception
   */
  public static double variance(double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate variance");
    }
    double mean = mean(x);
    double variance = 0.0;
    for (double i : x) {
      variance += (i - mean) * (i - mean);
    }
    return variance / (double) (x.length - 1);
  }

  /**
   * estimates the mean squared deviation of x from its mean value
   *
   * @param x
   * @return
   * @throws Exception
   */
  public static double variance(Double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate variance");
    }
    double mean = mean(x);
    double variance = 0.0;
    for (double i : x) {
      variance += (i - mean) * (i - mean);
    }
    return variance / (double) (x.length - 1);
  }

  public static double variance(float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate variance");
    }
    double mean = mean(x);
    double variance = 0.0;
    for (double i : x) {
      variance += (i - mean) * (i - mean);
    }
    return variance / (double) (x.length - 1);
  }

  public static double variance(Float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate variance");
    }
    double mean = mean(x);
    double variance = 0.0;
    for (double i : x) {
      variance += (i - mean) * (i - mean);
    }
    return variance / (double) (x.length - 1);
  }

  public static double variance(Number[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate variance");
    }
    double mean = mean(x);
    double variance = 0.0;
    for (Number i : x) {
      variance += (i.doubleValue() - mean) * (i.doubleValue() - mean);
    }
    return variance / (double) (x.length - 1);
  }

  public static double variance(Object[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate variance");
    }
    double mean = mean(x);
    double variance = 0.0;
    for (Object i : x) {
      variance += (((Number) i).doubleValue() - mean) * (((Number) i).doubleValue() - mean);
    }
    return variance / (double) (x.length - 1);
  }

  public static double variance(List x) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.size() == 1) {
      throw new Exception("Array length should be more than 1 to calculate variance");
    }
    double mean = mean(x);
    double variance = 0.0;
    for (Object i : x) {
      variance += (((Number) i).doubleValue() - mean) * (((Number) i).doubleValue() - mean);
    }
    return variance / (double) (x.size() - 1);
  }

  public static double standardDeviation(double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Standard Deviation");
    }
    return Math.sqrt(variance(x));
  }

  public static double standardDeviation(Double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Standard Deviation");
    }
    return Math.sqrt(variance(x));
  }

  public static double standardDeviation(float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Standard Deviation");
    }
    return Math.sqrt(variance(x));
  }

  public static double standardDeviation(Float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Standard Deviation");
    }
    return Math.sqrt(variance(x));
  }

  public static double standardDeviation(Number[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Standard Deviation");
    }
    return Math.sqrt(variance(x));
  }

  public static double standardDeviation(Object[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Standard Deviation");
    }
    return Math.sqrt(variance(x));
  }

  public static double standardDeviation(List x) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.size() == 1) {
      throw new Exception("Array length should be more than 1 to calculate Standard Deviation");
    }
    return Math.sqrt(variance(x));
  }

  /**
   * A robust estimator of the width is the average deviation or mean absolute deviation, defined by Sum( abs(Xi-Mean) )
   * / N
   *
   * @param x - 1d array
   * @return mean absolute deviation
   * @throws Exception
   */
  public static double meanAbsoluteDeviation(double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double deviation = 0.0;
    for (double i : x) {
      deviation += Math.abs(i - mean);
    }
    return deviation / (double) x.length;
  }

  public static double meanAbsoluteDeviation(Double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double deviation = 0.0;
    for (double i : x) {
      deviation += Math.abs(i - mean);
    }
    return deviation / (double) x.length;
  }

  public static double meanAbsoluteDeviation(float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double deviation = 0.0;
    for (double i : x) {
      deviation += Math.abs(i - mean);
    }
    return deviation / (double) x.length;
  }

  public static double meanAbsoluteDeviation(Float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double deviation = 0.0;
    for (double i : x) {
      deviation += Math.abs(i - mean);
    }
    return deviation / (double) x.length;
  }

  public static double meanAbsoluteDeviation(Number[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double deviation = 0.0;
    for (Number i : x) {
      deviation += Math.abs(i.doubleValue() - mean);
    }
    return deviation / (double) x.length;
  }

  public static double meanAbsoluteDeviation(Object[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double deviation = 0.0;
    for (Object i : x) {
      deviation += Math.abs(((Number) i).doubleValue() - mean);
    }
    return deviation / (double) x.length;
  }

  public static double meanAbsoluteDeviation(List x) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double deviation = 0.0;
    for (Object i : x) {
      deviation += Math.abs(((Number) i).doubleValue() - mean);
    }
    return deviation / (double) x.size();
  }

  /**
   * The skewness characterizes the degree of asymmetry of a distribution around its mean. While the mean, standard
   * deviation, and average deviation are dimensional quantities, that is, have the same units as the measured
   * quantities Xj, the skewness is conventionally defined in such a way as to make it non-dimensional. It is a pure
   * number that characterizes only the shape of the distribution. A positive value of skewness signifies a distribution
   * with an asymmetric tail extending out towards more positive X; a negative value signifies a distribution whose tail
   * extends out towards more negative X
   *
   * @param x
   * @return
   * @throws Exception
   */
  public static double skew(double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Skewness");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double skew = 0.0;
    for (double i : x) {
      skew += Math.pow((i - mean) / sigma, 3);
    }
    return skew / (double) x.length;
  }

  public static double skew(Double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Skewness");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double skew = 0.0;
    for (double i : x) {
      skew += Math.pow((i - mean) / sigma, 3);
    }
    return skew / (double) x.length;
  }

  public static double skew(float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Skewness");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double skew = 0.0;
    for (double i : x) {
      skew += Math.pow((i - mean) / sigma, 3);
    }
    return skew / (double) x.length;
  }

  public static double skew(Float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Skewness");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double skew = 0.0;
    for (double i : x) {
      skew += Math.pow((i - mean) / sigma, 3);
    }
    return skew / (double) x.length;
  }

  public static double skew(Number[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Skewness");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double skew = 0.0;
    for (Number i : x) {
      skew += Math.pow((i.doubleValue() - mean) / sigma, 3);
    }
    return skew / (double) x.length;
  }

  public static double skew(Object[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.length == 1) {
      throw new Exception("Array length should be more than 1 to calculate Skewness");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double skew = 0.0;
    for (Object i : x) {
      skew += Math.pow((((Number) i).doubleValue() - mean) / sigma, 3);
    }
    return skew / (double) x.length;
  }

  public static double skew(List x) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of data array");
    } else if (x.size() == 1) {
      throw new Exception("Array length should be more than 1 to calculate Skewness");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double skew = 0.0;
    for (Object i : x) {
      skew += Math.pow((((Number) i).doubleValue() - mean) / sigma, 3);
    }
    return skew / (double) x.size();
  }

  public static double linearCorrelationCoefficient(double[] x, double[] y) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of x data array");
    } else if (x.length == 1) {
      throw new Exception("X Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (y == null || y.length == 0) {
      throw new Exception("Got null instead of y data array");
    } else if (x.length == 1) {
      throw new Exception("Y Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (x.length != y.length) {
      throw new Exception("Both data arrays should be of equal length");
    }
    double meanX = mean(x);
    double meanY = mean(y);
    double numerator = 0.0, xSq = 0.0, ySq = 0.0;

    for (int i = 0; i < x.length; i++) {
      numerator += (x[i] - meanX) * (y[i] - meanY);
      xSq += (x[i] - meanX) * (x[i] - meanX);
      ySq += (y[i] - meanY) * (y[i] - meanY);
    }
    double denominator = Math.sqrt(xSq * ySq);
    if (denominator == 0) {
      throw new Exception("Denominator is equal to zero");
    }
    return numerator / denominator;
  }

  public static double linearCorrelationCoefficient(float[] x, float[] y) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of x data array");
    } else if (x.length == 1) {
      throw new Exception("X Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (y == null || y.length == 0) {
      throw new Exception("Got null instead of y data array");
    } else if (x.length == 1) {
      throw new Exception("Y Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (x.length != y.length) {
      throw new Exception("Both data arrays should be of equal length");
    }
    double meanX = mean(x);
    double meanY = mean(y);
    double numerator = 0.0, xSq = 0.0, ySq = 0.0;

    for (int i = 0; i < x.length; i++) {
      numerator += (x[i] - meanX) * (y[i] - meanY);
      xSq += (x[i] - meanX) * (x[i] - meanX);
      ySq += (y[i] - meanY) * (y[i] - meanY);
    }
    double denominator = Math.sqrt(xSq * ySq);
    if (denominator == 0) {
      throw new Exception("Denominator is equal to zero");
    }
    return numerator / denominator;
  }

  public static double linearCorrelationCoefficient(Object[] x, Object[] y) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of x data array");
    } else if (x.length == 1) {
      throw new Exception("X Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (y == null || y.length == 0) {
      throw new Exception("Got null instead of y data array");
    } else if (x.length == 1) {
      throw new Exception("Y Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (x.length != y.length) {
      throw new Exception("Both data arrays should be of equal length");
    }
    double meanX = mean(x);
    double meanY = mean(y);
    double numerator = 0.0, xSq = 0.0, ySq = 0.0;

    for (int i = 0; i < x.length; i++) {
      numerator += (((Number) x[i]).doubleValue() - meanX) * (((Number) y[i]).doubleValue() - meanY);
      xSq += (((Number) x[i]).doubleValue() - meanX) * (((Number) x[i]).doubleValue() - meanX);
      ySq += (((Number) y[i]).doubleValue() - meanY) * (((Number) y[i]).doubleValue() - meanY);
    }
    double denominator = Math.sqrt(xSq * ySq);
    if (denominator == 0) {
      throw new Exception("Denominator is equal to zero");
    }
    return numerator / denominator;
  }

  public static double linearCorrelationCoefficient(Number[] x, Number[] y) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of x data array");
    } else if (x.length == 1) {
      throw new Exception("X Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (y == null || y.length == 0) {
      throw new Exception("Got null instead of y data array");
    } else if (x.length == 1) {
      throw new Exception("Y Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (x.length != y.length) {
      throw new Exception("Both data arrays should be of equal length");
    }
    double meanX = mean(x);
    double meanY = mean(y);
    double numerator = 0.0, xSq = 0.0, ySq = 0.0;

    for (int i = 0; i < x.length; i++) {
      numerator += (x[i].doubleValue() - meanX) * (y[i].doubleValue() - meanY);
      xSq += (x[i].doubleValue() - meanX) * (x[i].doubleValue() - meanX);
      ySq += (y[i].doubleValue() - meanY) * (y[i].doubleValue() - meanY);
    }
    double denominator = Math.sqrt(xSq * ySq);
    if (denominator == 0) {
      throw new Exception("Denominator is equal to zero");
    }
    return numerator / denominator;
  }

  /**
   * The kurtosis is a non-dimensional quantity. It measures the relative peakedness or flatness of a distribution
   * relative to a normal distribution. A distribution with positive kurtosis is termed leptokurtic; the outline of the
   * Matterhorn is an example. A distribution with negative kurtosis is termed platykurtic; the outline of a loaf of
   * bread is an example. And, as you no doubt expect, an in-between distribution is termed mesokurtic.
   *
   * @param x
   * @return
   * @throws Exception
   */
  public static double kurtosis(double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double value = 0.0;
    for (double i : x) {
      value += Math.pow((i - mean) / sigma, 4);
    }
    return value / (double) x.length - 3.0;
  }

  public static double kurtosis(Double[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double value = 0.0;
    for (double i : x) {
      value += Math.pow((i - mean) / sigma, 4);
    }
    return value / (double) x.length - 3.0;
  }

  public static double kurtosis(float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double value = 0.0;
    for (double i : x) {
      value += Math.pow((i - mean) / sigma, 4);
    }
    return value / (double) x.length - 3.0;
  }

  public static double kurtosis(Float[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double value = 0.0;
    for (double i : x) {
      value += Math.pow((i - mean) / sigma, 4);
    }
    return value / (double) x.length - 3.0;
  }

  public static double kurtosis(Number[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double value = 0.0;
    for (Number i : x) {
      value += Math.pow((i.doubleValue() - mean) / sigma, 4);
    }
    return value / (double) x.length - 3.0;
  }

  public static double kurtosis(Object[] x) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double value = 0.0;
    for (Object i : x) {
      value += Math.pow((((Number) i).doubleValue() - mean) / sigma, 4);
    }
    return value / (double) x.length - 3.0;
  }

  public static double kurtosis(List x) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of data array");
    }
    double mean = mean(x);
    double sigma = standardDeviation(x);
    double value = 0.0;
    for (Object i : x) {
      value += Math.pow((((Number) i).doubleValue() - mean) / sigma, 4);
    }
    return value / (double) x.size() - 3.0;
  }

  public static double[] timeCorrelationFunction(double[] x, double[] y) throws Exception {
    if (x == null || x.length == 0) {
      throw new Exception("Got null instead of x data array");
    } else if (x.length == 1) {
      throw new Exception("X Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (y == null || y.length == 0) {
      throw new Exception("Got null instead of y data array");
    } else if (x.length == 1) {
      throw new Exception("Y Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (x.length != y.length) {
      throw new Exception("Both data arrays should be of equal length");
    }

    int minDim = Math.max(x.length, y.length);
    double norm = 0.0;
    for (int i = 0; i < minDim; i++) {
      norm += x[i] * y[i];
    }
    norm /= (double) minDim;

    List<Double> list = new ArrayList<Double>();
    list.add(1.0);

    for (int k = 1; k < minDim - 1; k++) {
      double corr = 0.0, n = 0.0;
      for (int i = 0; i < Math.min(minDim, y.length - k); i++) {
        corr += x[i] * y[i + k];
        n += 1.0;
      }
      corr /= n * norm;
      list.add(corr);
    }

    double[] tcf = new double[list.size()];
    for (int i = 0; i < tcf.length; i++) {
      tcf[i] = list.get(i);
    }

    return tcf;
  }

  public static double[] timeCorrelationFunction(List x, List y) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of x data array");
    } else if (x.size() == 1) {
      throw new Exception("X Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (y == null || y.size() == 0) {
      throw new Exception("Got null instead of y data array");
    } else if (x.size() == 1) {
      throw new Exception("Y Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (x.size() != y.size()) {
      throw new Exception("Both data arrays should be of equal length");
    }

    int minDim = Math.max(x.size(), y.size());
    double norm = 0.0;
    for (int i = 0; i < minDim; i++) {
      norm += ((Number) x.get(i)).doubleValue() * ((Number) y.get(i)).doubleValue();
    }
    norm /= (double) minDim;

    List<Double> list = new ArrayList<Double>();
    list.add(1.0);

    for (int k = 1; k < minDim - 1; k++) {
      double corr = 0.0, n = 0.0;
        for (int i = 0; i < Math.min(minDim, y.size() - k); i++) {
        corr += ((Number) x.get(i)).doubleValue() * ((Number) y.get(i + k)).doubleValue();
        ++n;
      }
      corr /= (n * norm);
      list.add(corr);
    }

    double[] tcf = new double[list.size()];
    for (int i = 0; i < tcf.length; i++) {
      tcf[i] = list.get(i);
    }

    return tcf;
  }

  public static int[] histogram(List x, double start, double end, int nCells) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of x data array");
    } else if (x.size() == 1) {
      throw new Exception("X Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (x.size() == 1) {
      throw new Exception("Y Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    }

    int[] xxx = new int[nCells];
    double n_cells = (double) nCells;

    for (int i = 0; i < x.size(); i++) {
      double value = ((Number) x.get(i)).doubleValue();
      if (value < start || value > end) {
        continue;
      }
      int index = (int) ((value - start) / (end - start) * n_cells);
      if (index == nCells) {
        --index;
      }
      ++xxx[index];
    }

    return xxx;
  }

  public static List<Double> acf(List x) throws Exception {
    if (x == null || x.size() == 0) {
      throw new Exception("Got null instead of x data array");
    } else if (x.size() == 1) {
      throw new Exception("X Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    } else if (x.size() == 1) {
      throw new Exception("Y Array length should be more than 1 to calculate a Linear Correlation Coefficient");
    }

    double mean = mean(x);
    double normFactor = 0.0;
    for (int i = 0; i < x.size(); i++) {
      normFactor += (((Number) x.get(i)).doubleValue() - mean) * (((Number) x.get(i)).doubleValue() - mean);
    }

    List<Double> list = new ArrayList<Double>();
    list.add(1.0);

    for (int k = 1; k < x.size() - 1; k++) {
      double corr = 0.0;
      for (int i = 0; i < x.size() - k; i++) {
        corr += (((Number) x.get(i)).doubleValue() - mean) * (((Number) x.get(i + k)).doubleValue() - mean);
      }
      corr /= normFactor;
      list.add(corr);
    }

    return list;
  }

  /**
   * The Root Mean Square Error (RMSE) (also called the root mean square deviation, RMSD) is a frequently used measure
   * of the difference between values predicted by a model and the values actually observed from the environment that is
   * being modelled. These individual differences are also called residuals, and the RMSE serves to aggregate them into
   * a single measure of predictive power.
   *
   * @param v1
   * @param v2
   * @return - RMSE
   */
  public static double rootMeanSquareError(double[] v1, double[] v2) {
    double rmse = 0.0;
    for (int i = 0; i < v1.length; i++) {
      rmse += (v1[i] - v2[i]) * (v1[i] - v2[i]);
    }
    return Math.sqrt(rmse / (double) v1.length);
  }

  public static double rootMeanSquareError(float[] v1, float[] v2) {
    double rmse = 0.0;
    for (int i = 0; i < v1.length; i++) {
      rmse += (v1[i] - v2[i]) * (v1[i] - v2[i]);
    }
    return Math.sqrt(rmse / (double) v1.length);
  }
}
