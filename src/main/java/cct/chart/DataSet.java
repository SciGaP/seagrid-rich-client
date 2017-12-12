package cct.chart;

import org.jfree.data.xy.XYSeriesCollection;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2009 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public class DataSet {
  private double y_Min, y_Max;
  private XYSeriesCollection xySC;

  public DataSet(double y_min, double y_max, XYSeriesCollection xySeries) {
    this.y_Min = y_min;
    this.y_Max = y_max;
    xySC = xySeries;
  }

  public XYSeriesCollection getDataCollection() {
    return xySC;
  }

  public double getYMin() {
    return this.y_Min;
  }

  public double getYMax() {
    return this.y_Max;
  }
}
