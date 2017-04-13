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

package cct.chart;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.jfree.data.xy.XYSeries;

import cct.chart.jfreechart.JFreeChartPanel;
import cct.dialogs.SelectListDialog;
import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import cct.modelling.StructureManagerInterface;
import cct.tools.DXData;

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
public class ChartFrame
    extends JFrame implements ChartInterface, StructureManagerInterface, ActionListener {
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel buttonPanel = new JPanel();
  private JButton ok = new JButton("Hide");
  private JButton cancel = new JButton("Cancel");
  private JFreeChartPanel jFreeChartPanel1 = new JFreeChartPanel();
  private Java3dUniverse java3dUniverse = null;
  private StructureManagerInterface structureManagerInterface = null;
  private JMenuBar jMenuBar = new JMenuBar();
  private JMenu jMenuFile = new JMenu("File");
  private JMenu jFileSaveAsSubmenu = new JMenu("Save As");
  private JMenu jFileAddSeriesSubmenu = new JMenu("Add Data Serie As");
  private JMenuItem jFileSaveAsCSV = new JMenuItem("CSV");
  private JMenuItem addAsDX = new JMenuItem("DX");
  private JMenuItem exitChart = new JMenuItem("Exit");
  private JFileChooser CSVchooser = null;
  private JFileChooser DXchooser = null;
  static final Logger logger = Logger.getLogger(ChartFrame.class.getCanonicalName());

  public ChartFrame() throws Exception {
    try {
      jbInit();
    }
    catch (Exception exception) {
      throw exception;
    }
  }

  private void jbInit() throws Exception {
    try {
      this.getClass().getClassLoader().loadClass("org.jfree.chart.JFreeChart");
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
      throw new Exception("JFreeChart is not found in CLASSPATH: ");
    }

    ok.addActionListener(this);
    cancel.addActionListener(this);
    buttonPanel.add(ok);
    buttonPanel.add(cancel);

    cancel.setVisible(false); // !!!

    jMenuFile.setMnemonic(KeyEvent.VK_F);
    jMenuFile.add(jFileSaveAsSubmenu);

    jFileSaveAsSubmenu.add(jFileSaveAsCSV);
    jFileSaveAsCSV.addActionListener(this);

    jMenuFile.add(jFileAddSeriesSubmenu);
    jFileAddSeriesSubmenu.add(addAsDX);
    addAsDX.addActionListener(this);

    jMenuFile.add(exitChart);
    exitChart.addActionListener(this);

    jMenuBar.add(jMenuFile);

    setJMenuBar(jMenuBar);

    getContentPane().setLayout(borderLayout1);
    this.getContentPane().add(jFreeChartPanel1, BorderLayout.CENTER);
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    this.setSize(800, 600);
  }

  public void setChartTitle(String title) {
    jFreeChartPanel1.setChartTitle(title);
  }

  public void enableAnimationPanel(boolean enable) {
    jFreeChartPanel1.enableAnimationPanel(enable);
  }

  public void setXAxisTitle(String title) {
    jFreeChartPanel1.setXAxisTitle(title);
  }

  public void setYAxisTitle(String title) {
    jFreeChartPanel1.setYAxisTitle(title);
  }

  public void enableChartPanel(boolean enable) {
    jFreeChartPanel1.enableChartPanel(enable);
  }

  public void enableTablePanel(boolean enable) {
    jFreeChartPanel1.enableTablePanel(enable);
  }

  public static void main(String[] args) {
    try {
      ChartFrame jchartframe = new ChartFrame();
      int n_test = 100;
      double x[] = new double[n_test];
      double y[] = new double[n_test];
      for (int i = 0; i < n_test; i++) {
        x[i] = i;
        y[i] = Math.random();
      }
      jchartframe.addDataSeries(x, y, "My test plot");
       jchartframe.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      jchartframe.showChart(true);
    }
    catch (Exception ex) {}
  }

  @Override
  public void addDataSeries(double x[], double y[], String name) throws Exception {
    jFreeChartPanel1.addDataSeries(x, y, name);
  }

  @Override
  public void addDataSeries(double x[], double y[], int n, String name) throws Exception {
    jFreeChartPanel1.addDataSeries(x, y, n, name);
  }

  @Override
  public void showChart(boolean enable) {
    if (enable) {
      jFreeChartPanel1.setSize(this.getSize());
      jFreeChartPanel1.showChart(true);
      this.pack();
      setVisible(true);
    }
    else {
      setVisible(false);
    }
  }

  public void setStructureManagerInterface(StructureManagerInterface smi) {
    structureManagerInterface = smi;
    jFreeChartPanel1.setStructureManagerInterface(this);
  }

  @Override
  public void selectStructure(int number) throws Exception {
    throw new Exception(this.getClass().getCanonicalName() + ": selectStructure(int number) is not implemented yet");
  }

  @Override
  public void selectStructure(int number, String term) throws Exception {
    MoleculeInterface referenceStructure = java3dUniverse.getMoleculeInterface();

    float[][] coords = structureManagerInterface.getStructure(number, term);
    if (coords == null) {
      throw new Exception("No coordinates for structure " + number);
    }
    for (int i = 0; i < referenceStructure.getNumberOfAtoms(); i++) {
      referenceStructure.getAtomInterface(i).setXYZ(coords[i][0], coords[i][1], coords[i][2]);
    }
    long start = System.currentTimeMillis();
    java3dUniverse.updateMolecularGeometry();
    float secs = (System.currentTimeMillis() - start) / 1000.0f;
    logger.info("Elapsed time for java3d update: " + secs);
  }

  @Override
  public float[][] getStructure(int n) {
    return null;
  }

  @Override
  public float[][] getStructure(int n, String term) {
    return null;
  }

  public void setRenderer(Java3dUniverse j3d) {
    java3dUniverse = j3d;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == cancel) {
      this.setVisible(false);
    }
    else if (e.getSource() == ok) {
      this.setVisible(false);
    }
    else if (e.getSource() == this.exitChart) {
      this.setVisible(false);
    }

    else if (e.getSource() == this.addAsDX) {
      if (DXchooser == null) {
        DXchooser = new JFileChooser();
        ChartFileFilter chartFileFilter = new ChartFileFilter("dx DX", "DX Files");
        DXchooser.setFileFilter(chartFileFilter);
        DXchooser.setDialogTitle("Open As DX File");
      }
      int returnVal = DXchooser.showOpenDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      DXData dxData = new DXData();
      try {
        dxData.parseDXFile(DXchooser.getSelectedFile().getAbsolutePath());
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                                      "Error parsing file " + DXchooser.getSelectedFile().getAbsolutePath() + " : " + ex.getMessage(),
                                      "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      //ArrayList<DataSet> dataSets = jFreeChartPanel1.getDataSets();
      //dataSets.clear();
      //this.getContentPane().remove(jFreeChartPanel1);
      //jFreeChartPanel1 = new JFreeChartPanel();
      //this.getContentPane().add(jFreeChartPanel1, java.awt.BorderLayout.CENTER);

      //jFreeChartPanel1.addDataSets(dataSets);

      try {
        jFreeChartPanel1.addDataSeries(dxData.getXData(), dxData.getYData(), dxData.countItems(), "");
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                                      "Error adding data serie: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }


      jFreeChartPanel1.showChart(true);
      pack();
    }

    // *************************************************************************
    else if (e.getSource() == jFileSaveAsCSV) {
      if (CSVchooser == null) {
        CSVchooser = new JFileChooser();
        ChartFileFilter chartFileFilter = new ChartFileFilter("csv CSV", "CSV Files");
        CSVchooser.setFileFilter(chartFileFilter);
        CSVchooser.setDialogTitle("Save As CSV File");
      }
      int returnVal = CSVchooser.showSaveDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      String file_name = CSVchooser.getSelectedFile().getAbsolutePath();
      if (!file_name.endsWith(".csv") && !file_name.endsWith(".CSV")) {
        file_name += ".csv";
      }

      java.util.List<DataSet> dataSets = jFreeChartPanel1.getDataSets();
      if (dataSets.size() < 1) {
        JOptionPane.showMessageDialog(this, "No data set(s)", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      int index = 0;
      if (dataSets.size() > 1) {
        SelectListDialog selectListDialog = new SelectListDialog(this, "Select dataset to save");
        selectListDialog.setLocationRelativeTo(this);
        Object[] list = new Object[dataSets.size()];
        for (int i = 0; i < dataSets.size(); i++) {
          list[i] = dataSets.get(0).getDataCollection().getSeries(0).getDescription();
        }
        selectListDialog.setList(list);
        selectListDialog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boolean ok = selectListDialog.showDialog();
        if (!ok) {
          return;
        }
        index = selectListDialog.getSelectedIndex();
        dataSets.get(0).getDataCollection().getSeries(0).getDescription();
      }

      XYSeries series = dataSets.get(index).getDataCollection().getSeries(0);
      try {
        saveDataSerie(file_name, series);
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Cannot save dataset to file " + file_name + " : " + ex.getMessage(), "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void saveDataSerie(String file, XYSeries series) throws Exception {
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.write("X,Y\n");
    for (int i = 0; i < series.getItemCount(); i++) {
      writer.write(String.format("%f,%f\n", series.getX(i), series.getY(i)));
    }
    writer.close();
  }

  class ChartFileFilter
      extends javax.swing.filechooser.FileFilter {
    private String description = "";
    private Set<String> ext = new HashSet<String> ();

    public ChartFileFilter(String extentions, String description) {
      this.description = description;
      StringTokenizer st = new StringTokenizer(extentions, " ");
      while (st.hasMoreTokens()) {
        ext.add(st.nextToken());
      }
    }

    @Override
    public boolean accept(File f) {
      if (!f.isFile()) {
        return true;
      }

      String path = f.getAbsolutePath();
      int index = path.lastIndexOf(".");
      if (index == -1) {
        return false;
      }
      path = path.substring(index + 1);
      return ext.contains(path);
    }

    @Override
    public String getDescription() {
      return description;
    }
  }

}
