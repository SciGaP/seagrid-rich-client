package cct.math;

import java.util.logging.Logger;


/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 * Interpolates functions of two variables on a regular grid.
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class Interpolator2D {
  private double[] origin = new double[2];
  private int[] dimension = new int[2];
  private double[][] funValues = null;
  private double step = 1;
  private double xMax, yMax;
  private double tolerance;
  static final Logger logger = Logger.getLogger(Interpolator2D.class.getCanonicalName());

  private Interpolator2D() {
  }

  public Interpolator2D(double xMin, double yMin, int xDim, int yDim, double dx, double[][] values) throws Exception {
    origin[0] = xMin;
    origin[1] = yMin;

    logger.info("Xmin=" + origin[0] + " Ymin=" + origin[1]);

    if (xDim < 2) {
      throw new Exception(this.getClass().getCanonicalName() + ": xDim < 2");
    }
    if (yDim < 2) {
      throw new Exception(this.getClass().getCanonicalName() + ": yDim < 2");
    }

    dimension[0] = xDim;
    dimension[1] = yDim;
    logger.info("Dimension: X=" + dimension[0] + " Y=" + dimension[1]);

    if (dx <= 0) {
      throw new Exception(this.getClass().getCanonicalName() + ": dx <= 0");
    }
    step = dx;
    tolerance = 0.001 * step;

    xMax = origin[0] + (xDim - 1) * step;
    yMax = origin[1] + (yDim - 1) * step;
    logger.info("Xmax=" + xMax + " Ymax" + yMax);

    if (values.length < xDim) {
      throw new Exception(this.getClass().getCanonicalName() + ": values.length < xDim");
    }
    if (values[0].length < yDim) {
      throw new Exception(this.getClass().getCanonicalName() + ": values[0].length < yDim");
    }
    funValues = new double[xDim][yDim];
    for (int i = 0; i < xDim; i++) {
      for (int j = 0; j < yDim; j++) {
        funValues[i][j] = values[i][j];
      }

    }
  }

  public void testBounds(double x, double y) throws ValueOutOfBoundsException {
    if (x < origin[0] || x > xMax) {
      throw new ValueOutOfBoundsException(this.getClass().getCanonicalName() + ": x(" + x + ") < origin[0](" + origin[0] +
                                          ") || x > xMax(" + xMax + ")");
    }
    if (y < origin[1] || y > yMax) {
      throw new ValueOutOfBoundsException(this.getClass().getCanonicalName() + ": y(" + y + ") < origin[1](" + origin[1] +
                                          ") || y > yMax(" + yMax + ")");
    }
  }

  public boolean checkBounds(double x, double y) {
    if (x < origin[0] || x > xMax) {
      return false;
    }
      return !(y < origin[1] || y > yMax);
  }

  /**
   * http://en.wikipedia.org/wiki/Bilinear_interpolation
   * @param x double
   * @param y double
   * @return double
   * @throws ValueOutOfBoundsException
   */
  public double bilinearInterpolation(double x, double y) throws ValueOutOfBoundsException {
    testBounds(x, y);
    double indexX = ( (int) ( (x - origin[0]) / step));
    double indexY = ( (int) ( (y - origin[1]) / step));

    int ind_x = (int) indexX;
    int ind_y = (int) indexY;

    double x_floor = indexX * step;
    double y_floor = indexY * step;

    // --- Special cases

    if (x - x_floor <= tolerance) {
      if (y - y_floor <= tolerance) {
        return funValues[ind_x][ind_y];
      }
      return ( (y_floor + step - y) * funValues[ind_x][ind_y] + (y - y_floor) * funValues[ind_x][ind_y + 1]) / step;
    }

    if (y - y_floor <= tolerance) {
      return ( (x_floor + step - x) * funValues[ind_x][ind_y] + (x - x_floor) * funValues[ind_x + 1][ind_y]) / step;
    }

    // --- general case

    return ( (x_floor + step - x) * (y_floor + step - y) * funValues[ind_x][ind_y] +
            (x - x_floor) * (y_floor + step - y) * funValues[ind_x + 1][ind_y] +
            (x_floor + step - x) * (y - y_floor) * funValues[ind_x][ind_y + 1] +
            (x - x_floor) * (y - y_floor) * funValues[ind_x + 1][ind_y + 1]) / (step * step);
  }

  public static void main(String[] args) {
    double xMin = 0;
    double yMin = 0;
    int xDim = 2;
    int yDim = 2;
    double dx = 1;
    double[][] values = new double[xDim][yDim];

    values[0][0] = 0;
    values[1][0] = 1;
    values[1][1] = 2;
    values[0][1] = 3;

    Interpolator2D interpolator2d = null;
    try {
      interpolator2d = new Interpolator2D(xMin, yMin, xDim, yDim, dx, values);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    try {
      logger.info("x,y = 0,0: " + interpolator2d.bilinearInterpolation(0.0, 0.0));
      logger.info("x,y = 0,1: " + interpolator2d.bilinearInterpolation(0.0, 1.0));
      logger.info("x,y = 1,0: " + interpolator2d.bilinearInterpolation(1.0, 0.0));
      logger.info("x,y = 1,1: " + interpolator2d.bilinearInterpolation(1.0, 1.0));
      logger.info("x,y = 0, 0.5: " + interpolator2d.bilinearInterpolation(0.0, 0.5));
      logger.info("x,y = 1, 0.5: " + interpolator2d.bilinearInterpolation(1.0, 0.5));
      logger.info("x,y = 0.5, 0: " + interpolator2d.bilinearInterpolation(0.5, 0.0));
      logger.info("x,y = 0.5 ,1: " + interpolator2d.bilinearInterpolation(0.5, 1));
      logger.info("x,y = 0.5 ,0.5: " + interpolator2d.bilinearInterpolation(0.5, 0.5));
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }

  }
}
