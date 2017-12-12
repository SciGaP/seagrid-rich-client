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

package cct.chart.jfreechart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.NumberCellRenderer;
import org.jfree.ui.RectangleInsets;

import cct.chart.DataSet;
import cct.modelling.StructureManagerInterface;

/**
 * <p>Title: Computational Chemistry Toolkit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008 Dr. V. Vasilyev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class JFreeChartPanel
    extends JPanel implements ChartChangeListener, ChartProgressListener, ActionListener {

  public static int MAX_SLIDER_VALUE = 1000;
  public Color colors[] = {
      Color.black, Color.red, Color.blue, Color.green, Color.cyan, Color.magenta, Color.orange, Color.pink, Color.yellow};
  public static boolean debug = false;
  private int minStep = 1, maxStep = 100, stepStep = 1;
  private int Step = 1;
  private static final String[] MOVIE_MODES = {
      "Once", "Loop", "Rock"};

  private BorderLayout borderLayout1 = new BorderLayout();

  private String chartTitle = "XY Chart", xAxisTitle = "X", yAxisTitle = "Y";
  private ChartPanel chartPanel;
  private XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
  private java.util.List<DataSet> dataSets = new ArrayList<DataSet> ();
  private double yMin, yMax, span = 0.05;
  private double xMin, xMax;
  private JPanel tablePanel = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private ChartTableModel chartTableModel = null;
  private BorderLayout borderLayout2 = new BorderLayout();
  //private JSplitPane jSplitPane = new JSplitPane();

  private java.util.List<java.util.List> indexFind = new ArrayList<java.util.List> ();
  private JPanel mainChartPanel = new JPanel();
  private BorderLayout borderLayout3 = new BorderLayout();
  private JPanel animationChartPanel = new JPanel();
  private JSlider chartSlider = new JSlider();
  private BorderLayout borderLayout4 = new BorderLayout();

  private JFreeChart chart = null;
  private JPanel animationControlPanel = new JPanel();
  private JPanel jPanel1 = new JPanel();
  private JButton stepBackButton = new JButton();
  private JButton playBackButton = new JButton();
  private JButton pauseButton = new JButton();
  private JButton playButton = new JButton();
  private JButton stepForwardButton = new JButton();
  private BorderLayout borderLayout5 = new BorderLayout();

  private ImageIcon mediaBeginningImage = new ImageIcon(cct.chart.ChartFrame.class.getResource("images/media_beginning.png"));
  private ImageIcon mediaEndImage = new ImageIcon(cct.chart.ChartFrame.class.getResource("images/media_end.png"));
  private ImageIcon mediaPauseImage = new ImageIcon(cct.chart.ChartFrame.class.getResource("images/media_pause.png"));
  private ImageIcon mediaPlayImage = new ImageIcon(cct.chart.ChartFrame.class.getResource("images/media_play.png"));
  private ImageIcon mediaPlayBackImage = new ImageIcon(cct.chart.ChartFrame.class.getResource("images/media_play_back.png"));
  private ImageIcon mediaStepBackImage = new ImageIcon(cct.chart.ChartFrame.class.getResource("images/media_step_back.png"));
  private ImageIcon mediaStepForwardImage = new ImageIcon(cct.chart.ChartFrame.class.getResource("images/media_step_forward.png"));
  private JButton goToEndButton = new JButton();
  private JButton goToBeginningButton = new JButton();

  private int delay;
  private int framesPerSeconds = 25;
  private SpinnerModel stepModel = new SpinnerNumberModel(Step, minStep, maxStep, stepStep);
  private SpinnerModel fpsModel = new SpinnerNumberModel(framesPerSeconds, 1, 100, 1);

  private int currentIndex = 0;
  private int selectedStructure = 0;
  private boolean animationInProgress = false;
  private int animDirection = 1;
  private Timer timer;
  private int animationMode;

  private StructureManagerInterface structureManagerInterface = null;

  private JPanel jPanel2 = new JPanel();
  private JLabel jLabel1 = new JLabel();
  private JSpinner stepSpinner = new JSpinner(stepModel);
  private JLabel jLabel2 = new JLabel();
  private JSpinner fpsSpinner = new JSpinner(fpsModel);
  private JComboBox animationComboBox = new JComboBox();

  private boolean showChartPanel = true;
  private boolean showTablePanel = true;
  static final Logger logger = Logger.getLogger(JFreeChartPanel.class.getCanonicalName());

  public JFreeChartPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public java.util.List<DataSet> getDataSets() {
    return dataSets;
  }

  public void addDataSets(java.util.List<DataSet> dataS) {
    dataSets.addAll(dataS);
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    chart = createDefaultChart();
    chartPanel = new ChartPanel(chart);
    setDefaultChartOptions();
    tablePanel.setLayout(borderLayout2);
    //jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    chartPanel.setMinimumSize(new Dimension(800, 600));
    chartPanel.setPreferredSize(new Dimension(800, 600));
    chartPanel.setZoomOutFactor(1.0);
    mainChartPanel.setLayout(borderLayout3);
    animationChartPanel.setLayout(borderLayout4);
    chartSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        chartSlider_stateChanged(e);
      }
    });
    stepBackButton.setMaximumSize(new Dimension(24, 24));
    stepBackButton.setMinimumSize(new Dimension(24, 24));
    stepBackButton.setPreferredSize(new Dimension(24, 24));
    stepBackButton.setToolTipText("Step Back");
    stepBackButton.setIcon(mediaStepBackImage);
    stepBackButton.setMargin(new Insets(0, 0, 0, 0));
    stepBackButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        stepBackButton_actionPerformed(e);
      }
    });
    playBackButton.setMaximumSize(new Dimension(24, 24));
    playBackButton.setMinimumSize(new Dimension(24, 24));
    playBackButton.setPreferredSize(new Dimension(24, 24));
    playBackButton.setToolTipText("Play Back");
    playBackButton.setIcon(mediaPlayBackImage);
    playBackButton.setMargin(new Insets(0, 0, 0, 0));
    playBackButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playBackButton_actionPerformed(e);
      }
    });
    pauseButton.setMaximumSize(new Dimension(24, 24));
    pauseButton.setMinimumSize(new Dimension(24, 24));
    pauseButton.setPreferredSize(new Dimension(24, 24));
    pauseButton.setToolTipText("Stop/Pause Animation");
    pauseButton.setIcon(mediaPauseImage);
    pauseButton.setMargin(new Insets(0, 0, 0, 0));
    pauseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        pauseButton_actionPerformed(e);
      }
    });
    playButton.setMaximumSize(new Dimension(24, 24));
    playButton.setMinimumSize(new Dimension(24, 24));
    playButton.setPreferredSize(new Dimension(24, 24));
    playButton.setToolTipText("Play Forward");
    playButton.setIcon(mediaPlayImage);
    playButton.setMargin(new Insets(0, 0, 0, 0));
    playButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playButton_actionPerformed(e);
      }
    });
    stepForwardButton.setMaximumSize(new Dimension(24, 24));
    stepForwardButton.setMinimumSize(new Dimension(24, 24));
    stepForwardButton.setPreferredSize(new Dimension(24, 24));
    stepForwardButton.setToolTipText("Step Forward");
    stepForwardButton.setIcon(mediaStepForwardImage);
    stepForwardButton.setMargin(new Insets(0, 0, 0, 0));
    stepForwardButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        stepForwardButton_actionPerformed(e);
      }
    });
    animationControlPanel.setLayout(borderLayout5);
    goToEndButton.setMaximumSize(new Dimension(24, 24));
    goToEndButton.setMinimumSize(new Dimension(24, 24));
    goToEndButton.setPreferredSize(new Dimension(24, 24));
    goToEndButton.setToolTipText("Go to Beginning");
    goToEndButton.setIcon(mediaBeginningImage);
    goToEndButton.setMargin(new Insets(0, 0, 0, 0));
    goToEndButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        goToEndButton_actionPerformed(e);
      }
    });
    goToBeginningButton.setMaximumSize(new Dimension(24, 24));
    goToBeginningButton.setMinimumSize(new Dimension(24, 24));
    goToBeginningButton.setPreferredSize(new Dimension(24, 24));
    goToBeginningButton.setToolTipText("Go to End");
    goToBeginningButton.setIcon(mediaEndImage);
    goToBeginningButton.setMargin(new Insets(0, 0, 0, 0));
    goToBeginningButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        goToBeginningButton_actionPerformed(e);
      }
    });
    jLabel1.setText("Step: ");
    jLabel2.setToolTipText("");
    jLabel2.setText("Frames per second: ");
    stepSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        stepSpinner_stateChanged(e);
      }
    });
    fpsSpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        fpsSpinner_stateChanged(e);
      }
    });
    animationComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        animationComboBox_itemStateChanged(e);
      }
    });
    mainChartPanel.setMinimumSize(new Dimension(500, 300));
    mainChartPanel.setPreferredSize(new Dimension(500, 300));
    mainChartPanel.setToolTipText("");
    tablePanel.setMinimumSize(new Dimension(500, 70));
    tablePanel.setPreferredSize(new Dimension(500, 70));
    tablePanel.setToolTipText("");
    this.setMinimumSize(new Dimension(500, 400));
    this.setPreferredSize(new Dimension(500, 400));
    this.setToolTipText("");
    jScrollPane1.setMinimumSize(new Dimension(500, 100));
    jScrollPane1.setPreferredSize(new Dimension(500, 100));
    tablePanel.add(jScrollPane1, BorderLayout.CENTER); //this.add(jSplitPane, java.awt.BorderLayout.CENTER);
    //jSplitPane.add(tablePanel, JSplitPane.BOTTOM);
    add(tablePanel, BorderLayout.SOUTH);
    mainChartPanel.add(chartPanel, BorderLayout.CENTER);
    mainChartPanel.add(animationChartPanel, BorderLayout.SOUTH);
    animationChartPanel.add(animationControlPanel, BorderLayout.SOUTH);
    jPanel1.add(goToEndButton);
    jPanel1.add(stepBackButton);
    jPanel1.add(playBackButton);
    jPanel1.add(pauseButton);
    jPanel1.add(playButton);
    jPanel1.add(stepForwardButton);
    jPanel1.add(goToBeginningButton);
    jPanel2.add(jLabel1);
    jPanel2.add(stepSpinner);
    jPanel2.add(jLabel2);
    jPanel2.add(fpsSpinner);
    jPanel2.add(animationComboBox);
    animationChartPanel.add(chartSlider, BorderLayout.NORTH);
    animationControlPanel.add(jPanel2, BorderLayout.SOUTH);
    animationControlPanel.add(jPanel1, BorderLayout.NORTH);
    this.add(mainChartPanel, BorderLayout.CENTER); //Set up a timer that calls this object's action handler.
    delay = 1000 / framesPerSeconds;
    //timer.setDelay(delay);
    //timer.setInitialDelay(delay * 10);

    timer = new Timer(delay, this);
    //timer.setInitialDelay(delay * 7); //We pause animation twice per cycle  by restarting the timer
    timer.setCoalesce(true);

    for (int i = 0; i < MOVIE_MODES.length; i++) {
      animationComboBox.addItem(MOVIE_MODES[i]);
    }
    animationComboBox.setSelectedIndex(0);
    animationMode = animationComboBox.getSelectedIndex();

    this.validate();
  }

  public void enableAnimationPanel(boolean enable) {
    animationControlPanel.setVisible(enable);
  }

  private void setDefaultChartOptions() {
    chartPanel.setPreferredSize(new Dimension(640, 480));
    chartPanel.setDomainZoomable(true);
    chartPanel.setRangeZoomable(true);
    Border border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
        BorderFactory.createEtchedBorder());
    this.chartPanel.setBorder(border);
  }

  public void startAnimation() {
    //Start (or restart) animating!
    timer.start();
  }

  private JFreeChart createDefaultChart() {

    XYSeries series1 = new XYSeries("First");
    series1.add(1.0, 1.0);
    series1.add(2.0, 4.0);
    series1.add(3.0, 3.0);
    series1.add(4.0, 5.0);
    series1.add(5.0, 5.0);
    series1.add(6.0, 7.0);
    series1.add(7.0, 7.0);
    series1.add(8.0, 8.0);
    XYSeries series2 = new XYSeries("Second");
    series2.add(1.0, 5.0);
    series2.add(2.0, 7.0);
    series2.add(3.0, 6.0);
    series2.add(4.0, 8.0);
    series2.add(5.0, 4.0);
    series2.add(6.0, 4.0);
    series2.add(7.0, 2.0);
    series2.add(8.0, 1.0);
    XYSeries series3 = new XYSeries("Third");
    series3.add(3.0, 4.0);
    series3.add(4.0, 3.0);
    series3.add(5.0, 2.0);
    series3.add(6.0, 3.0);
    series3.add(7.0, 6.0);
    series3.add(8.0, 3.0);
    series3.add(9.0, 4.0);
    series3.add(10.0, 3.0);
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);
    //dataset.addSeries(series2);
    //dataset.addSeries(series3);

    chart = ChartFactory.createXYLineChart(
        chartTitle, // chart title
        xAxisTitle, // x axis label
        yAxisTitle, // y axis label
        null, // data
        PlotOrientation.VERTICAL,
        true, // include legend
        true, // tooltips
        false // urls
        );

    XYPlot plot = (XYPlot) chart.getPlot();

    // AXIS 1
    NumberAxis axis1 = new NumberAxis("Range Axis 1");
    axis1.setFixedDimension(10.0);
    axis1.setAutoRangeIncludesZero(false);
    axis1.setLabelPaint(Color.black);
    axis1.setTickLabelPaint(Color.black);
    plot.setRangeAxis(0, axis1);
    plot.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_LEFT);

    XYSeriesCollection dataset1 = new XYSeriesCollection();
    dataset1.addSeries(series1);
    plot.setDataset(0, dataset1);
    plot.mapDatasetToRangeAxis(0, 0);
    XYItemRenderer renderer1 = new StandardXYItemRenderer();
    renderer1.setSeriesPaint(0, Color.BLACK);
    plot.setRenderer(0, renderer1);

    // AXIS 2
    NumberAxis axis2 = new NumberAxis("Range Axis 2");
    axis2.setFixedDimension(10.0);
    axis2.setAutoRangeIncludesZero(false);
    axis2.setLabelPaint(Color.red);
    axis2.setTickLabelPaint(Color.red);
    plot.setRangeAxis(1, axis2);
    plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);

    XYSeriesCollection dataset2 = new XYSeriesCollection();
    dataset2.addSeries(series2);
    plot.setDataset(1, dataset2);
    plot.mapDatasetToRangeAxis(1, 1);
    XYItemRenderer renderer2 = new StandardXYItemRenderer();
    renderer2.setSeriesPaint(0, Color.red);
    plot.setRenderer(1, renderer2);

    // AXIS 3
    NumberAxis axis3 = new NumberAxis("Range Axis 3");
    axis3.setLabelPaint(Color.blue);
    axis3.setTickLabelPaint(Color.blue);
    plot.setRangeAxis(2, axis3);

    XYSeriesCollection dataset3 = new XYSeriesCollection(series3);
    plot.setDataset(2, dataset3);
    plot.mapDatasetToRangeAxis(2, 2);
    XYItemRenderer renderer3 = new StandardXYItemRenderer();
    renderer3.setSeriesPaint(0, Color.blue);
    plot.setRenderer(2, renderer3);

    int n = 7;
    chartTableModel = new ChartTableModel(3);

    for (int row = 0; row < 3; row++) {

      //chartTableModel.setValueAt(plot.getDataset(row).getSeriesKey(0), row, 0);
      chartTableModel.setValueAt(new Double("0.00"), row, 0);
      chartTableModel.setValueAt(new Double("0.00"), row, 1);
      chartTableModel.setValueAt(new Double("0.00"), row, 2);
      chartTableModel.setValueAt(new Double("0.00"), row, 3);
      chartTableModel.setValueAt(new Double("0.00"), row, 4);
      chartTableModel.setValueAt(new Double("0.00"), row, 5);
      chartTableModel.setValueAt(new Double("0.00"), row, 6);
    }

    JTable table = new JTable(chartTableModel);
    //TableCellRenderer renderer2 = new NumberCellRenderer();
    /*
         table.getColumnModel().getColumn(1).setCellRenderer(renderer2);
         table.getColumnModel().getColumn(2).setCellRenderer(renderer2);
         table.getColumnModel().getColumn(3).setCellRenderer(renderer2);
         table.getColumnModel().getColumn(4).setCellRenderer(renderer2);
         table.getColumnModel().getColumn(5).setCellRenderer(renderer2);
         table.getColumnModel().getColumn(6).setCellRenderer(renderer2);

     */
    jScrollPane1.getViewport().add(table);
    return chart;
  }

  private JFreeChart createChart() {

    if (dataSets.size() < 1) {
      chart = ChartFactory.createXYLineChart(
          "Empty Chart (no dataset supplied)", // chart title
          "X", // x axis label
          "Y", // y axis label
          null, //
          //dataSets.get(0).getDataCollection(), //
          PlotOrientation.VERTICAL,
          true, // include legend
          true, // tooltips
          false // urls
          );

      return chart;
    }

    if (dataSets.size() == 1) {
      chart = ChartFactory.createXYLineChart(
          chartTitle, // chart title
          xAxisTitle, // x axis label
          dataSets.get(0).getDataCollection().getSeries(0).getDescription(), // y axis label
          dataSets.get(0).getDataCollection(), //
          PlotOrientation.VERTICAL,
          true, // include legend
          true, // tooltips
          false // urls
          );
      XYPlot plot = (XYPlot) chart.getPlot();
      ValueAxis rangeAxis = plot.getRangeAxis();
      if (rangeAxis instanceof NumberAxis) {
        ( (NumberAxis) rangeAxis).setAutoRangeIncludesZero(false);
      }
    }
    else {
      chart = ChartFactory.createXYLineChart(
          chartTitle, // chart title
          xAxisTitle, // x axis label
          dataSets.get(0).getDataCollection().getSeries(0).getDescription(), // y axis label
          null, //
          //dataSets.get(0).getDataCollection(), //
          PlotOrientation.VERTICAL,
          true, // include legend
          true, // tooltips
          false // urls
          );

      XYPlot plot = (XYPlot) chart.getPlot();
      /*
                NumberAxis axis1 = new NumberAxis(dataSets.get(0).getDataCollection().getSeries(0).getDescription());
                axis1.setAutoRangeIncludesZero(false);
                axis1.setLabelPaint(Color.black);
                axis1.setTickLabelPaint(Color.black);
                plot.setRangeAxis(0, axis1);

                XYItemRenderer renderer1 = new StandardXYItemRenderer();
                renderer1.setSeriesPaint(0, Color.black);
                plot.setRenderer(0, renderer1);
       */
      for (int i = 0; i < dataSets.size(); i++) {
        Color color = colors[i % colors.length];
        DataSet data_set = dataSets.get(i);
        XYSeriesCollection dataset2 = data_set.getDataCollection();
        NumberAxis axis2 = new NumberAxis(dataset2.getSeries(0).getDescription());
        //axis2.setFixedDimension(10.0);
        axis2.setAutoRangeIncludesZero(false);
        axis2.setLabelPaint(color);
        axis2.setTickLabelPaint(color);

        plot.setRangeAxis(i, axis2);
        if (i % 2 == 0) {
          plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
        }
        else {
          plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_RIGHT);
        }
        //plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
        plot.setDataset(i, dataset2);
        plot.mapDatasetToRangeAxis(i, i);
        XYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setSeriesPaint(0, color);
        plot.setRenderer(i, renderer2);

      }
      /*
             ValueAxis rangeAxis = plot.getRangeAxis();
             double range = yMax - yMin;
             rangeAxis.setRange(yMin - range * span, yMax + range * span);
             plot.setRangeAxis(rangeAxis);

       logger.info("Low: " + rangeAxis.getRange().getLowerBound() + " High: " + rangeAxis.getRange().getUpperBound());
       */

    }

    chart.addChangeListener(this);
    chart.addProgressListener(this);
    chart.setBackgroundPaint(Color.white);

    XYPlot plot = (XYPlot) chart.getPlot();

    plot.setOrientation(PlotOrientation.VERTICAL);
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

    plot.setDomainCrosshairVisible(true);
    plot.setDomainCrosshairLockedOnData(true);
    plot.setRangeCrosshairVisible(false);

    //XYItemRenderer renderer = plot.getRenderer();
    //renderer.setSeriesPaint(0, Color.black);

    return chart;
  }

  public void setChartTitle(String title) {
    chartTitle = title;
  }

  public void setXAxisTitle(String title) {
    xAxisTitle = title;
  }

  public void setYAxisTitle(String title) {
    yAxisTitle = title;
  }

  @Override
  public void chartChanged(ChartChangeEvent event) {

    /*
         if (this.chartPanel != null) {
      JFreeChart chart = this.chartPanel.getChart();
      if (chart != null) {
        XYPlot plot = (XYPlot) chart.getPlot();
        double xx = plot.getDomainCrosshairValue();
        logger.info("Domain Crosshair Value: " + xx);
      }
         }
     */
    /*
         if (this.chartPanel != null) {
      JFreeChart chart = this.chartPanel.getChart();
      if (chart != null) {
        XYPlot plot = (XYPlot) chart.getPlot();
        XYDataset dataset = plot.getDataset();
        Comparable seriesKey = dataset.getSeriesKey(0);
        double xx = plot.getDomainCrosshairValue();
        this.model.setValueAt(seriesKey, 0, 0);
        long millis = (long) xx;
        for (int row = 0; row < SERIES_COUNT; row++) {
          this.model.setValueAt(new Long(millis), row, 1);
          int[] bounds
              = this.datasets[row].getSurroundingItems(0, millis);
          long prevX = 0;
          long nextX = 0;
          double prevY = 0.0;
          double nextY = 0.0;
          if (bounds[0] >= 0) {
            TimeSeriesDataItem prevItem
                = this.series[row].getDataItem(bounds[0]);
            prevX = prevItem.getPeriod().getMiddleMillisecond();
            Number y = prevItem.getValue();
            if (y != null) {
              prevY = y.doubleValue();
              this.model.setValueAt(new Double(prevY), row,
                                    4);
            }
            else {
              this.model.setValueAt(null, row, 4);
            }
            this.model.setValueAt(new Long(prevX), row, 3);
          }
          else {
            this.model.setValueAt(new Double(0.00), row, 4);
            this.model.setValueAt(new Double(plot.getDomainAxis().getRange().getLowerBound()), row, 3);
          }
          if (bounds[1] >= 0) {
            TimeSeriesDataItem nextItem
                = this.series[row].getDataItem(bounds[1]);
            nextX = nextItem.getPeriod().getMiddleMillisecond();
            Number y = nextItem.getValue();
            if (y != null) {
              nextY = y.doubleValue();
              this.model.setValueAt(new Double(nextY), row,
                                    6);
            }
            else {
              this.model.setValueAt(null, row, 6);
            }
            this.model.setValueAt(new Long(nextX), row, 5);
          }
          else {
            this.model.setValueAt(new Double(0.00), row, 6);
            this.model.setValueAt(new Double(plot.getDomainAxis().getRange().getUpperBound()), row, 5);
          }
          double interpolatedY = 0.0;
          if ( (nextX - prevX) > 0) {
            interpolatedY = prevY + ( ( (double) millis
                                       - (double) prevX) / ( (double) nextX - (double) prevX)) * (nextY - prevY);
          }
          else {
            interpolatedY = prevY;
          }
          this.model.setValueAt(new Double(interpolatedY), row, 2);
        }
      }
         }
     */
  }

  @Override
  public void chartProgress(ChartProgressEvent event) {

    if (event.getType() != ChartProgressEvent.DRAWING_FINISHED) {
      return;
    }
    if (this.chartPanel != null) {
      JFreeChart c = this.chartPanel.getChart();
      if (c != null) {
        XYPlot plot = (XYPlot) c.getPlot();
        Double xx = plot.getDomainCrosshairValue();
        if (debug) {
          logger.info("Crosshair Value: " + xx);
        }

        ValueAxis domainAxis = plot.getDomainAxis();
        Range range = domainAxis.getRange();
        if (debug) {
          logger.info("Domain axis range: " + range.toString());
        }

        double step = range.getLength() / chartSlider.getMaximum();
        // --- Assume that minimum value of slider is zero !!!
        int slider_value = (int) ( (xx - domainAxis.getLowerBound()) / step);
        chartSlider.setEnabled(false);
        chartSlider.setValue(slider_value);
        chartSlider.setEnabled(true);

        //for (int i = 0; i < xySeriesCollection.getSeriesCount(); i++) {
        for (int i = 0; i < dataSets.size(); i++) {
          XYSeries series = dataSets.get(i).getDataCollection().getSeries(0);
          //XYSeries series = xySeriesCollection.getSeries(i);
          java.util.List<Double> toIndex = indexFind.get(i);
          int index = toIndex.indexOf(xx);
          if (index == -1) {
            index = calculateIndex(xx, toIndex);
          }
          currentIndex = index;
          XYDataItem yxItem = series.getDataItem(index);
          chartTableModel.setValueAt(new Double(yxItem.getXValue()), i, 1);
          chartTableModel.setValueAt(new Double(yxItem.getYValue()), i, 2);

          // --- Set previous values
          if (index > 0) {
            yxItem = series.getDataItem(index - 1);
            chartTableModel.setValueAt(new Double(yxItem.getXValue()), i, 3);
            chartTableModel.setValueAt(new Double(yxItem.getYValue()), i, 4);
          }
          else {
            chartTableModel.setValueAt(new Double(yxItem.getXValue()), i, 3);
            chartTableModel.setValueAt(new Double(yxItem.getYValue()), i, 4);
          }

          // --- Set next values
          if (index < toIndex.size() - 1) {
            yxItem = series.getDataItem(index + 1);
            chartTableModel.setValueAt(new Double(yxItem.getXValue()), i, 5);
            chartTableModel.setValueAt(new Double(yxItem.getYValue()), i, 6);
          }
          else {
            yxItem = series.getDataItem(index);
            chartTableModel.setValueAt(new Double(yxItem.getXValue()), i, 5);
            chartTableModel.setValueAt(new Double(yxItem.getYValue()), i, 6);
          }

          if (selectedStructure == currentIndex) {
            continue;
          }

          if (structureManagerInterface != null) {
            try {
              structureManagerInterface.selectStructure(currentIndex,
                  dataSets.get(0).getDataCollection().getSeries(0).getDescription()); // We use the first plot
              selectedStructure = currentIndex;
            }
            catch (Exception ex) {
              System.err.println("Cannot select structure " + currentIndex + " : " + ex.getMessage());
            }
          }
        }
      }
    }

    /*
         if (this.chartPanel != null) {
      JFreeChart c = this.chartPanel.getChart();
      if (c != null) {
        XYPlot plot = (XYPlot) c.getPlot();
        XYDataset dataset = plot.getDataset();
        Comparable seriesKey = dataset.getSeriesKey(0);
        double xx = plot.getDomainCrosshairValue();
        // update the table...
        this.model.setValueAt(seriesKey, 0, 0);
        long millis = (long) xx;
        this.model.setValueAt(new Long(millis), 0, 1);
        for (int i = 0; i < SERIES_COUNT; i++) {
          int itemIndex = this.series[i].getIndex(new Minute(new Date(millis)));
          if (itemIndex >= 0) {
            TimeSeriesDataItem item = this.series[i].getDataItem(Math.min(199, Math.max(0, itemIndex)));
            TimeSeriesDataItem prevItem = this.series[i].getDataItem(Math.max(0, itemIndex - 1));
            TimeSeriesDataItem nextItem = this.series[i].getDataItem(Math.min(199, itemIndex + 1));
            long x = item.getPeriod().getMiddleMillisecond();
            double y = item.getValue().doubleValue();
            long prevX = prevItem.getPeriod().getMiddleMillisecond();
            double prevY = prevItem.getValue().doubleValue();
            long nextX = nextItem.getPeriod().getMiddleMillisecond();
            double nextY = nextItem.getValue().doubleValue();
            this.model.setValueAt(new Long(x), i, 1);
            this.model.setValueAt(new Double(y), i, 2);
            this.model.setValueAt(new Long(prevX), i, 3);
            this.model.setValueAt(new Double(prevY), i, 4);
            this.model.setValueAt(new Long(nextX), i, 5);
            this.model.setValueAt(new Double(nextY), i, 6);
          }
        }
      }
         }
     */
  }

// --- ChartInterface functions

  public void addDataSeries(double x[], double y[], String name) throws Exception {
    addDataSeries(x, y, x.length, name);
  }

  public void addDataSeries(double x[], double y[], int n, String name) throws Exception {
    if (n > Math.min(x.length, y.length)) {
      throw new Exception("n(" + n + ") > Math.min(x.length, y.length)(" + Math.min(x.length, y.length) + ")");
    }

    double yMin = y[0], yMax = y[0];
    if (dataSets.size() < 1) {
      //yMin = yMax = y[0];
      xMin = xMax = x[0];
    }

    java.util.List<Double> toIndex = new ArrayList<Double> (x.length);
    XYSeries series = new XYSeries(name);
    series.setDescription(name);

    for (int i = 0; i < n; i++) {
      series.add(x[i], y[i]);
      toIndex.add(new Double(x[i]));
      if (yMin > y[i]) {
        yMin = y[i];
      }
      else if (yMax < y[i]) {
        yMax = y[i];
      }
      if (xMin > x[i]) {
        xMin = x[i];
      }
      else if (xMax < x[i]) {
        xMax = x[i];
      }

    }

    if (chartSlider.getMaximum() < x.length && chartSlider.getMaximum() < MAX_SLIDER_VALUE) {
      chartSlider.setMaximum(x.length);
    }

    XYSeriesCollection xySC = new XYSeriesCollection();
    xySC.addSeries(series);
    indexFind.add(toIndex);

    DataSet dataSet = new DataSet(yMin, yMax, xySC);
    dataSets.add(dataSet);

    /*
           if (xySeriesCollection.getSeriesCount() == 0) {
       //jSplitPane.removeAll();
           }

           if (xySeriesCollection.getSeriesCount() < 1) {
       yMin = yMax = y[0];
       xMin = xMax = x[0];
           }

           ArrayList<Double> toIndex = new ArrayList<Double> (x.length);
           XYSeries series = new XYSeries(name);
           series.setDescription(name);

           for (int i = 0; i < n; i++) {
       series.add(x[i], y[i]);
       toIndex.add(new Double(x[i]));
       if (yMin > y[i]) {
          yMin = y[i];
       }
       else if (yMax < y[i]) {
          yMax = y[i];
       }
       if (xMin > x[i]) {
          xMin = x[i];
       }
       else if (xMax < x[i]) {
          xMax = x[i];
       }

           }

           if (chartSlider.getMaximum() < x.length && chartSlider.getMaximum() < MAX_SLIDER_VALUE) {
       chartSlider.setMaximum(x.length);
           }

           xySeriesCollection.addSeries(series);
           this.indexFind.add(toIndex);
     */
  }

  public void showChart(boolean enable) {
    //if ( true) return;
    mainChartPanel.remove(chartPanel);

    if (showChartPanel) {

      JFreeChart chart = createChart();
      chartPanel = new ChartPanel(chart);
      setDefaultChartOptions();
      mainChartPanel.add(chartPanel, BorderLayout.CENTER);

      //chartTableModel = new ChartTableModel(xySeriesCollection.getSeriesCount());
      chartTableModel = new ChartTableModel(dataSets.size());

      for (int row = 0; row < dataSets.size(); row++) {
        DataSet data_set = dataSets.get(row);
        chartTableModel.setValueAt(data_set.getDataCollection().getSeries(0).getDescription(), row, 0);
        chartTableModel.setValueAt(new Double("0.00"), row, 1);
        chartTableModel.setValueAt(new Double("0.00"), row, 2);
        chartTableModel.setValueAt(new Double("0.00"), row, 3);
        chartTableModel.setValueAt(new Double("0.00"), row, 4);
        chartTableModel.setValueAt(new Double("0.00"), row, 5);
        chartTableModel.setValueAt(new Double("0.00"), row, 6);
      }
    }
    /*
           for (int row = 0; row < xySeriesCollection.getSeriesCount(); row++) {
       XYPlot plot = (XYPlot) chart.getPlot();
       XYSeries series = xySeriesCollection.getSeries(row);
       chartTableModel.setValueAt(series.getDescription(), row, 0);
       chartTableModel.setValueAt(new Double("0.00"), row, 1);
       chartTableModel.setValueAt(new Double("0.00"), row, 2);
       chartTableModel.setValueAt(new Double("0.00"), row, 3);
       chartTableModel.setValueAt(new Double("0.00"), row, 4);
       chartTableModel.setValueAt(new Double("0.00"), row, 5);
       chartTableModel.setValueAt(new Double("0.00"), row, 6);
           }
     */

    jScrollPane1.getViewport().removeAll();
    if (showTablePanel) {
      JTable table = new JTable(chartTableModel);
      TableCellRenderer renderer2 = new NumberCellRenderer();
      table.getColumnModel().getColumn(1).setCellRenderer(renderer2);
      table.getColumnModel().getColumn(2).setCellRenderer(renderer2);
      table.getColumnModel().getColumn(3).setCellRenderer(renderer2);
      table.getColumnModel().getColumn(4).setCellRenderer(renderer2);
      table.getColumnModel().getColumn(5).setCellRenderer(renderer2);
      table.getColumnModel().getColumn(6).setCellRenderer(renderer2);
      jScrollPane1.getViewport().add(table);
    }

    //jSplitPane.add(tablePanel, JSplitPane.BOTTOM);

    chartSlider.setEnabled(false);
    chartSlider.setValue(0);
    chartSlider.setEnabled(true);

    this.validate();
  }

  static class ChartTableModel
      extends AbstractTableModel implements TableModel {

    private Object[][] data;

    /**
     * Creates a new table model
     *
     * @param rows  the row count.
     */
    public ChartTableModel(int rows) {
      this.data = new Object[rows][7];
    }

    /**
     * Returns the column count.
     *
     * @return 7.
     */
    @Override
    public int getColumnCount() {
      return 7;
    }

    /**
     * Returns the row count.
     *
     * @return The row count.
     */
    @Override
    public int getRowCount() {
      return this.data.length;
    }

    /**
     * Returns the value at the specified cell in the table.
     *
     * @param row  the row index.
     * @param column  the column index.
     *
     * @return The value.
     */
    @Override
    public Object getValueAt(int row, int column) {
      return this.data[row][column];
    }

    /**
     * Sets the value at the specified cell.
     *
     * @param value  the value.
     * @param row  the row index.
     * @param column  the column index.
     */
    @Override
    public void setValueAt(Object value, int row, int column) {
      this.data[row][column] = value;
      fireTableDataChanged();
    }

    /**
     * Returns the column name.
     *
     * @param column  the column index.
     *
     * @return The column name.
     */
    @Override
    public String getColumnName(int column) {
      switch (column) {
        case 0:
          return "Series Name:";
        case 1:
          return "X:";
        case 2:
          return "Y:";
        case 3:
          return "X (prev)";
        case 4:
          return "Y (prev):";
        case 5:
          return "X (next):";
        case 6:
          return "Y (next):";
      }
      return null;
    }

  }

  public void chartSlider_stateChanged(ChangeEvent e) {
    if (!chartSlider.isEnabled()) {
      return;
    }
    int value = chartSlider.getValue();
    XYPlot plot = (XYPlot)this.chartPanel.getChart().getPlot();
    ValueAxis domainAxis = plot.getDomainAxis();
    Range range = domainAxis.getRange();
    double c = domainAxis.getLowerBound() + (value / (double) chartSlider.getMaximum()) * range.getLength();
    plot.setDomainCrosshairValue(c);
  }

  private int calculateIndex(double value, java.util.List<Double> toIndex) {
    int index = 0;
    double x_min = toIndex.get(0);
    if (value <= x_min) {
      return 0;
    }
    double x_max = toIndex.get(toIndex.size() - 1);
    if (value >= x_max) {
      return toIndex.size() - 1;
    }

    double step = (x_max - x_min) / (toIndex.size() - 1);
    double real_ind = (value - x_min) / step;
    int lower_ind = (int) Math.floor(real_ind);
    if (lower_ind == toIndex.size() - 1) {
      return lower_ind;
    }

    double low = toIndex.get(lower_ind);
    double high = toIndex.get(lower_ind + 1);

    if (value >= low && value <= high) { // Simple case (uniform distribution)
      if ( (value - low) < (high - value)) {
        return lower_ind;
      }
      return lower_ind + 1;
    }

    else if (value < low) {
      for (index = lower_ind - 1; index >= 0; index--) {
        low = toIndex.get(index);
        high = toIndex.get(index + 1);
        if (value >= low && value <= high) { // Simple case
          if ( (value - low) < (high - value)) {
            return index;
          }
          return index + 1;
        }
      }
    }
    else if (value > high) {
      for (index = lower_ind + 1; index < toIndex.size(); index++) {
        low = toIndex.get(index);
        high = toIndex.get(index + 1);
        if (value >= low && value <= high) { // Simple case
          if ( (value - low) < (high - value)) {
            return index;
          }
          return index + 1;
        }
      }

    }

    return index;
  }

  public void goToEndButton_actionPerformed(ActionEvent e) {
    XYPlot plot = (XYPlot)this.chartPanel.getChart().getPlot();
    plot.setDomainCrosshairValue(xMin);
  }

  public void goToBeginningButton_actionPerformed(ActionEvent e) {
    XYPlot plot = (XYPlot)this.chartPanel.getChart().getPlot();
    plot.setDomainCrosshairValue(xMax);

  }

  public void stepBackButton_actionPerformed(ActionEvent e) {
    if (currentIndex <= 0) {
      return;
    }
    --currentIndex;
    java.util.List<Double> toIndex = indexFind.get(0);
    XYPlot plot = (XYPlot)this.chartPanel.getChart().getPlot();
    plot.setDomainCrosshairValue(toIndex.get(currentIndex));
  }

  public void stepForwardButton_actionPerformed(ActionEvent e) {
    java.util.List<Double> toIndex = indexFind.get(0);
    if (currentIndex >= toIndex.size() - 1) {
      return;
    }
    ++currentIndex;
    XYPlot plot = (XYPlot)this.chartPanel.getChart().getPlot();
    plot.setDomainCrosshairValue(toIndex.get(currentIndex));

  }

  public void playButton_actionPerformed(ActionEvent e) {
    if (animationInProgress && animDirection == 1) {
      return;
    }

    animDirection = 1;

    startAnimation();
  }

  public void stepSpinner_stateChanged(ChangeEvent e) {
    Integer value = (Integer) stepSpinner.getValue();
    stepStep = value;
  }

  public void fpsSpinner_stateChanged(ChangeEvent e) {
    Integer value = (Integer) fpsSpinner.getValue();
    framesPerSeconds = value;
    delay = 1000 / framesPerSeconds;
    timer.setDelay(delay);
  }

  /**
   * Called when the Timer fires.
   * @param e ActionEvent
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    if (animationInProgress) {
      logger.info("Lost frame...");
      return;
    }
    animationInProgress = true;

    java.util.List<Double> toIndex = indexFind.get(0);

    int value = Step + stepStep * animDirection;
    if (value < 1) {
      value = 1;
    }
    else if (value > toIndex.size()) {
      value = toIndex.size() - 1;
    }

    Step = value;

    try {
      long start = System.currentTimeMillis();
      XYPlot plot = (XYPlot)this.chartPanel.getChart().getPlot();
      currentIndex = Step - 1;
      plot.setDomainCrosshairValue(toIndex.get(currentIndex));
      //selectStructure(Step);
      //structureManagerInterface.selectStructure(currentIndex);
      float secs = (System.currentTimeMillis() - start) / 1000.0f;
      logger.info("Elapsed time: " + secs);
      //setSnapshotValue(Step);
    }
    catch (Exception ex) {
      animationInProgress = false;
      timer.stop();
      JOptionPane.showMessageDialog(this, "Error during animation: " + ex.getMessage() + "\nAnimation stopped", "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (animationMode == 0) { // Animate once
      if (Step == 1 && animDirection == -1) {
        stopAnimation();
      }
      else if (Step == toIndex.size() && animDirection == 1) {
        stopAnimation();
      }
    }

    else if (animationMode == 1) { // Animate in loop
      if (Step == 1 && animDirection == -1) {
        Step = toIndex.size();
      }
      else if (Step == toIndex.size() && animDirection == 1) {
        Step = 1;
      }
    }

    else if (animationMode == 2) { // Animate back & forth
      if (Step == 1 && animDirection == -1) {
        if (toIndex.size() > 1) {
          Step = 1;
          animDirection = 1;
        }
      }
      else if (Step == toIndex.size() && animDirection == 1) {
        if (toIndex.size() > 1) {
          Step = toIndex.size() - 1;
          animDirection = -1;
        }
      }
    }

    animationInProgress = false;
  }

  public void stopAnimation() {
    //Stop animation
    timer.stop();
    animationInProgress = false;
  }

  public void playBackButton_actionPerformed(ActionEvent e) {
    if (animationInProgress && animDirection == -1) {
      return;
    }

    animDirection = -1;
    startAnimation();
  }

  public void pauseButton_actionPerformed(ActionEvent e) {
    stopAnimation();
  }

  public void animationComboBox_itemStateChanged(ItemEvent e) {
    if (!animationComboBox.isEnabled()) {
      return;
    }
    if (e.getStateChange() == ItemEvent.DESELECTED) {
      return;
    }
    else if (e.getStateChange() == ItemEvent.SELECTED) {
      animationMode = animationComboBox.getSelectedIndex();
    }
  }

  public void setStructureManagerInterface(StructureManagerInterface smi) {
    structureManagerInterface = smi;
  }

  public void enableChartPanel(boolean enable) {
    showChartPanel = enable;
  }

  public void enableTablePanel(boolean enable) {
    showTablePanel = enable;
  }
}
