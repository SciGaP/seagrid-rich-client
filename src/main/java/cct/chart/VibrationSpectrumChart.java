package cct.chart;

import cct.interfaces.OutputResultsInterface;
import cct.modelling.VIBRATIONAL_SPECTRUM;

/**
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class VibrationSpectrumChart {

  private OutputResultsInterface outputResultsInterface = null;
  private int dimension = 5000;

  public VibrationSpectrumChart() {
  }

  public VibrationSpectrumChart(OutputResultsInterface output) {
    outputResultsInterface = output;
  }

  public void setOutputResultsInterface(OutputResultsInterface output) {
    outputResultsInterface = output;
  }

  public static void main(String[] args) {
    VibrationSpectrumChart vibrationspectrumchart = new VibrationSpectrumChart();
  }

  public ChartFrame createChart(VIBRATIONAL_SPECTRUM spectrum) throws Exception {

    if (outputResultsInterface == null) {
      throw new Exception("createChart: OutputResultsInterface is not set");
    }

    double x[] = new double[dimension];
    double y[] = new double[dimension];

    try {
      outputResultsInterface.getSpectrum(x, y, dimension, spectrum);
    } catch (Exception ex) {
      throw ex;
    }

    return createChart(x, y, spectrum);

    //ChartFrame chart = new ChartFrame();
    //chart.enableAnimationPanel(false);
    //chart.setTitle(ChartReference.getTitle(spectrum));
    //chart.setChartTitle(ChartReference.getTitle(spectrum));
    //chart.setYAxisTitle(ChartReference.getYAxisTitle(spectrum));
    //chart.setXAxisTitle(ChartReference.getXAxisTitle(spectrum));
    //chart.addDataSeries(x, y, ChartReference.getYAxisTitle(spectrum));
    //return chart;
  }

  public ChartFrame createChart(double[] x, double[] y, VIBRATIONAL_SPECTRUM spectrum) throws Exception {
    ChartFrame chart = new ChartFrame();
    chart.enableAnimationPanel(false);
    chart.setTitle(ChartReference.getTitle(spectrum));
    chart.setChartTitle(ChartReference.getTitle(spectrum));
    chart.setYAxisTitle(ChartReference.getYAxisTitle(spectrum));
    chart.setXAxisTitle(ChartReference.getXAxisTitle(spectrum));

    chart.addDataSeries(x, y, ChartReference.getYAxisTitle(spectrum));

    return createChart(x, y, ChartReference.getTitle(spectrum), ChartReference.getTitle(spectrum), 
        ChartReference.getXAxisTitle(spectrum), ChartReference.getYAxisTitle(spectrum));
  }

  public ChartFrame createChart(double[] x, double[] y, String winTitle, String title, String xAxis, String yAxis) throws Exception {
    ChartFrame chart = new ChartFrame();
    chart.enableAnimationPanel(false);
    chart.setTitle(winTitle);
    chart.setChartTitle(title);
    chart.setYAxisTitle(yAxis);
    chart.setXAxisTitle(xAxis);

    chart.addDataSeries(x, y, yAxis);

    return chart;
  }
}
