/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.chart;

import cct.interfaces.ChartDataProvider;
import cct.amber.AmberMden;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Vlad
 */
public class QuickChart {

  public static final String CHART_PROPERTY_FILE = "cct/chart/chart.properties";
  private static Properties chartProperties = null;
  private static URL cctPropsURL = null;
  private static Map<String, ChartDataProvider> dataProviders = new HashMap<String, ChartDataProvider>();
  private String fileName = null;
  private boolean showAllDescriptors = false;
  private ChartDataProvider dataProvider = null;
  private Map<String, List<String>> graphs = new LinkedHashMap<String, List<String>>();
  static final Logger logger = Logger.getLogger(QuickChart.class.getCanonicalName());

  public QuickChart() {

    logger.setLevel(Level.WARNING);

    if (chartProperties == null) {
      chartProperties = getProperties();
      for (Map.Entry entry : chartProperties.entrySet()) {
        String name = entry.getKey().toString();
        String className = entry.getValue().toString();

        Class builderClass = null;
        ClassLoader loader = null;
        try {
          // Get the Class object associated with builder
          builderClass = Class.forName(className);

          // Get the ClassLoader object associated with this Class.
          loader = builderClass.getClassLoader();

          if (loader == null) {
            logger.severe("loader == null while attempting to load class " + className + " Ignored...");
            continue;
          } else {
            // Verify that this ClassLoader is associated with the builder class.
            Class loaderClass = loader.getClass();

            if (logger.isLoggable(Level.INFO)) {
              logger.info("Class associated with ClassLoader: " + loaderClass.getName());
            }
          }
        } catch (ClassNotFoundException ex) {
          logger.severe("Cannot load class " + className + " : " + ex.getMessage() + " Ignored...");
          continue;
        }

        try {
          Class cl = loader.loadClass(className);
          Object obj = cl.newInstance();
          if (logger.isLoggable(Level.INFO)) {
            logger.info("Classr " + className + " was loaded");
          }
          // ----
          if (!(obj instanceof ChartDataProvider)) {
            logger.severe("Class " + className + " does not implement " + ChartDataProvider.class.getName()
                + " interface. Ignored...");
            continue;
          }
          // ----

          dataProviders.put(name, (ChartDataProvider) obj);
        } catch (Exception ex) {
          logger.severe("Error while loading Class " + className + ex.getMessage() + " Ignored...");
          continue;
        }

      }

    }
  }

  public void execute(String[] args) throws Exception {

    boolean interactiveMode = false;

    if (args.length < 1) {
      printUsage();
      System.exit(0);
    }

    int count = 0;
    while (count < args.length) {
      if (args[count].equals("-f")) {
        ++count;
        if (count >= args.length) {
          throw new Exception("Expected file format after -f option");
        }
        dataProvider = dataProviders.get(args[count]);
        if (dataProvider == null) {
          throw new Exception("File format " + args[count] + " is not supported");
        }
      } else if (args[count].equals("-s")) {
        showAllDescriptors = true;
      } else if (args[count].equals("-gui")) {
        interactiveMode = true;
        break;
      } else if (args[count].equals("-i")) {
        ++count;
        if (count >= args.length) {
          throw new Exception("Expected file name after -i option");
        }
        fileName = args[count];
      } else if (args[count].startsWith("-x")) {
        ++count;
        if (count >= args.length) {
          throw new Exception("Expected descriptor label after " + args[count - 1] + " option");
        }
        String graphLabel = args[count - 1].length() == 2 ? "" : args[count - 1].substring(2);
        List<String> graphList = graphs.get(graphLabel);
        if (graphList != null && graphList.get(0) != null && graphList.get(0).equals(args[count])) {
          throw new Exception("May be only one dataset for X-ccordinate " + graphLabel);
        } else if (graphList != null && graphList.get(0) == null) {
          graphList.set(0, args[count]);
        } else {
          graphList = new ArrayList<String>();
          graphList.add(args[count]);
          graphs.put(graphLabel, graphList);
        }
      } // ***********************************************
      else if (args[count].startsWith("-y")) {
        ++count;
        if (count >= args.length) {
          throw new Exception("Expected descriptor label after " + args[count - 1] + " option");
        }
        String graphLabel = args[count - 1].length() == 2 ? "" : args[count - 1].substring(2);
        List<String> graphList = graphs.get(graphLabel);
        if (graphList == null) {
          graphList = new ArrayList<String>();
          graphList.add(null);
          graphs.put(graphLabel, graphList);
        }
        graphList.add(args[count]);
      } else if (args[count].equals("-v")) {
        logger.setLevel(Level.INFO);
      } else {
        throw new Exception("Uknown option " + args[count]);
      }
      //
      ++count;
    }

    // --- 
    if (interactiveMode) {
      interactiveFileSelection();
    }

    // --- Final Error check for the input options
    if (fileName == null) {
      throw new Exception("File name is not set");
    }

    if (dataProvider == null) {
      throw new Exception("File format is not specified");
    }

    //AmberMden amberMden = new AmberMden();
    //amberMden.parseData(fileName);
    dataProvider.cdpParseData(fileName);
    Set<String> descriptors = dataProvider.cdpGetDescriptors();

    if (interactiveMode) {
      interactiveDescriptorSelection(descriptors);
    } else if (showAllDescriptors) {
      printDescriptors(descriptors);
      System.exit(0);
    }

    // -- check that we have all descriptors in file
    StringBuilder sb = new StringBuilder();
    for (String key : graphs.keySet()) {
      List<String> graphList = graphs.get(key);
      for (String desc : graphList) {
        if (!descriptors.contains(desc)) {
          sb.append("Data does not contain descriptor " + desc + "\n");
        }
      }
    }
    if (sb.length() > 0) {
      throw new Exception(sb.toString());
    }

    // --- Finally, show chart
    try {
      ChartFrame jchartframe = new ChartFrame();
      jchartframe.setSize(1024, 600);
      jchartframe.enableAnimationPanel(false);

      String xSeriesName = "X";
      String ySeriesName = "Y";
      double x[] = null, y[] = null;
      count = 0;
      for (String key : graphs.keySet()) {
        List<String> graphList = graphs.get(key);
        xSeriesName = "X";
        ySeriesName = "Y";
        for (String desc : graphList) {
          if (count == 0) {
            x = dataProvider.cdpGetDataAsDouble(desc);
            xSeriesName = desc;
            ++count;
            continue;
          } else {
            y = dataProvider.cdpGetDataAsDouble(desc);
            ySeriesName = desc;
          }
            jchartframe.addDataSeries(x, y, xSeriesName + " vs " + ySeriesName);
        }
        break;
      }

      //int n_test = 100;
      //double x[] = new double[n_test];
      //double y[] = new double[n_test];
      //for (int i = 0; i < n_test; i++) {
      //  x[i] = i;
      //  y[i] = Math.random();
      //}
      //
      //jchartframe.addDataSeries(x, y, xSeriesName + " vs " + ySeriesName);
      jchartframe.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      jchartframe.showChart(true);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void printUsage() {
    System.out.println("program -i input-file-name -f format [-s] [-v]");
    System.out.println("  -s - Show properties/descriptors in input file. Programs exits after showing.");
    System.out.println("  -v - Verbose mode");
    System.out.println("Available formats:");
    for (Map.Entry<String, ChartDataProvider> entry : dataProviders.entrySet()) {
      ChartDataProvider cdp = entry.getValue();
      System.out.println("  " + entry.getKey() + " - " + cdp.cdpGetDescription());
    }
  }

  public void printDescriptors(Set<String> descriptors) {
    if (descriptors == null || descriptors.size() < 1) {
      System.err.println("No descriptors were found");
      return;
    }

    System.out.println("Number of descriptors: " + descriptors.size());
    for (String key : descriptors) {
      System.out.print(key + " ");
    }
    System.out.println();
  }

  public static void main(String[] args) {
    QuickChart quickChart = new QuickChart();
    try {
      //quickChart.execute(args);

      //quickChart.execute(args);
      // ---
      //args = new String[]{"-i", "01_min.out", "-s", "-f", "mdout"};
      //quickChart.execute(args);
      // ---
      //args = new String[]{"-i", "01_min.out", "-f", "mdout", "-x", "NSTEP", "-y", "ENERGY"};
      //quickChart.execute(args);
      // ---
      //args = new String[]{"-i", "mden", "-s", "-f", "mden"};
      //quickChart.execute(args);
      // ---
      //args = new String[]{"-i", "mden", "-f", "mden", "-x", "time(ps)", "-y", "Etot"};
      args = new String[]{"-gui"};
      quickChart.execute(args);
    } catch (Exception ex) {
      Logger.getLogger(QuickChart.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static Properties getProperties() {
    if (chartProperties != null) {
      return chartProperties;
    }

    chartProperties = getProperties(CHART_PROPERTY_FILE);
    return chartProperties;
  }

  public static Properties getProperties(String propertiesFile) {

    String file = propertiesFile;
    if (file == null || file.trim().length() < 1) {
      file = CHART_PROPERTY_FILE;
    }
    chartProperties = null;
    cctPropsURL = null;
    try {
      cctPropsURL = QuickChart.class.getClassLoader().getResource(QuickChart.CHART_PROPERTY_FILE);
      chartProperties = new Properties();
      chartProperties.load(cctPropsURL.openStream());
      logger.info("Loaded chart properties file " + file);
    } catch (Exception ex) {
      logger.warning("Cannot open chart properties file " + file + ": " + ex.getMessage());
    }
    return chartProperties;
  }

  public void interactiveFileSelection() throws Exception {
    //Create a file chooser
    final JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory(new java.io.File("."));
    int returnVal = fc.showOpenDialog(null);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      throw new Exception("No file was selected");
    }

    fileName = fc.getSelectedFile().getAbsolutePath();

    float highestScore = 0;
    ChartDataProvider mostProbableCDP = null;
    for (Map.Entry<String, ChartDataProvider> entry : dataProviders.entrySet()) {
      ChartDataProvider cdp = entry.getValue();
      float score = cdp.cdpIsThisFormat(fileName);
      if (score > highestScore) {
        highestScore = score;
        mostProbableCDP = cdp;
      }
    }

    dataProvider = mostProbableCDP;

    // --- Cannot determine. Ask user about that..
    if (dataProvider == null) {
      Object[] possibleValues = dataProviders.keySet().toArray();
      Object selectedValue = JOptionPane.showInputDialog(null,
          "Program was unable to determine format of input file\nSelect from available ones:", "Select input format",
          JOptionPane.INFORMATION_MESSAGE, null,
          possibleValues, possibleValues[0]);
      if (selectedValue == null) {
        throw new Exception("Unknown input format");
      }

      dataProvider = dataProviders.get(selectedValue);
    }
  }

  public void interactiveDescriptorSelection(Set<String> descriptors) throws Exception {

    Object[] possibleValues = descriptors.toArray();

    if (possibleValues == null || possibleValues.length < 1) {
      throw new Exception("possibleValues == null || possibleValues.length < 1");
    }

    PropsToChartDialog propsToChartDialog = new PropsToChartDialog(true);
    propsToChartDialog.setXAxisValues(possibleValues);
    propsToChartDialog.setYAxisValues(possibleValues);
    propsToChartDialog.doLayout();
    propsToChartDialog.validate();
    propsToChartDialog.setLocationByPlatform(true);
    propsToChartDialog.setVisible(true);
    if (!propsToChartDialog.isOkPressed()) {
      throw new Exception("User cancelled selection");
    }

    Object selectedValue = propsToChartDialog.getXAxisValue();

    // --- Old stuff...
    if (false) {
      selectedValue = JOptionPane.showInputDialog(null,
          "Select descriptor for X-axis", "Select X-axis data", JOptionPane.INFORMATION_MESSAGE, null,
          possibleValues, possibleValues[0]);
      if (selectedValue == null) {
        throw new Exception("User cancelled selection");
      }
    }
    // --- End of old stuff

    String graphLabel = "";
    List<String> graphList = graphs.get(graphLabel);
    if (graphList != null && graphList.get(0) != null && graphList.get(0).equals(selectedValue.toString())) {
      throw new Exception("May be only one dataset for X-ccordinate " + graphLabel);
    } else if (graphList != null && graphList.get(0) == null) {
      graphList.set(0, selectedValue.toString());
    } else {
      graphList = new ArrayList<String>();
      graphList.add(selectedValue.toString());
      graphs.put(graphLabel, graphList);
    }

    // ---- Select Y-axis
    Object[] yaxisItems = propsToChartDialog.getYAxisValues();

    if (false) {
      selectedValue = JOptionPane.showInputDialog(null,
          "Select descriptor for Y-axis", "Select Y-axis data", JOptionPane.INFORMATION_MESSAGE, null,
          possibleValues, possibleValues[0]);
      if (selectedValue == null) {
        throw new Exception("User cancelled selection");
      }
    }

    for (Object obj : yaxisItems) {
      graphLabel = "";
      graphList = graphs.get(graphLabel);
      if (graphList == null) {
        graphList = new ArrayList<String>();
        graphList.add(null);
        graphs.put(graphLabel, graphList);
      }
      //graphList.add(selectedValue.toString());
      graphList.add(obj.toString());
    }
  }
}
