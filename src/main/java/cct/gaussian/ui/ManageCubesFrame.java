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

package cct.gaussian.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cct.chart.ChartFrame;
import cct.dialogs.JamberooCore;
import cct.gaussian.GaussianCube;
import cct.gaussian.java3d.GaussianJava3dFactory;
import cct.interfaces.AtomInterface;
import cct.interfaces.GraphicsObjectInterface;
import cct.interfaces.MoleculeInterface;
import cct.j3d.ColorRangeScheme;
import cct.j3d.Java3dUniverse;
import cct.j3d.ui.VertexObjectsDialog;
import cct.math.VolumeData;
import cct.modelling.CCTAtomTypes;
import cct.modelling.Molecule;

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
public class ManageCubesFrame
    extends JFrame {
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JList cubeList = new JList();
  private BorderLayout borderLayout2 = new BorderLayout();
  private JPanel jPanel3 = new JPanel();
  private FlowLayout flowLayout1 = new FlowLayout();
  private JPanel jPanel4 = new JPanel();
  private BorderLayout borderLayout3 = new BorderLayout();
  private JButton hideButton = new JButton();
  private JPanel jPanel5 = new JPanel();
  private JButton loadButton = new JButton();
  private JButton deleteButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton isosurfaceButton = new JButton();

  //private GraphicsObjectPropertiesFrame graphicsObjectPropertiesFrame = null;
  private JamberooCore jamberooCore = null;
  private java.util.List Cubes;
  private java.util.List info;
  private Java3dUniverse java3dUniverse = null;
  private JLabel jLabel3 = new JLabel();
  private float isovalue = 0.02f;
  private NumberFormat isovalueFormat;
  private JFormattedTextField isovalueFormattedTextField = new JFormattedTextField(isovalueFormat);
  private JCheckBox useReverseCheckBox = new JCheckBox();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JButton helpButton = new JButton();
  private JButton mappedSurfaceButton = new JButton();
  private JPanel surfaceButtonsPanel = new JPanel();
  private JButton histogramButton = new JButton();
  private int nBins = 5000;
  private ImageIcon histogramImage = new ImageIcon(cct.resources.Resources.class.getResource("cct/images/icons16x16/line-chart.png"));
  static final Logger logger = Logger.getLogger(ManageCubesFrame.class.getCanonicalName());

  public ManageCubesFrame(String title, java.util.List cubes) {
    super(title);
    Cubes = cubes;
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
    jPanel3.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    jPanel2.setLayout(borderLayout3);
    hideButton.setToolTipText("Hide Dialog");
    hideButton.setText("Hide");
    hideButton.addActionListener(new ManageCubesFrame_hideButton_actionAdapter(this));
    loadButton.setToolTipText("Load Gaussian Cube file");
    loadButton.setText("Load Cube");
    loadButton.addActionListener(new ManageCubesFrame_loadButton_actionAdapter(this));
    deleteButton.setToolTipText("Delete Gaussian Cube file from the list");
    deleteButton.setText("Delete Cube");
    deleteButton.addActionListener(new ManageCubesFrame_deleteButton_actionAdapter(this));
    saveButton.setToolTipText("Save Gaussian Cube to file");
    saveButton.setText("Save Cube");
    saveButton.addActionListener(new ManageCubesFrame_saveButton_actionAdapter(this));
    isosurfaceButton.setToolTipText("Create Isosurface for selected Cube");
    isosurfaceButton.setText("Create Isosurface");
    isosurfaceButton.addActionListener(new
                                       ManageCubesFrame_isosurfaceButton_actionAdapter(this));
    jPanel1.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlShadow, 1),
                                       "Gaussian Cubes Description"));
    jLabel3.setToolTipText("");
    jLabel3.setText("Isovalue: ");
    isovalueFormattedTextField.setToolTipText("Value for building isosurface");
    isovalueFormattedTextField.setMargin(new Insets(1, 5, 2, 10));
    isovalueFormattedTextField.setText("0.025");
    useReverseCheckBox.setToolTipText("Whether to build isosurface for -isovalue as well");
    useReverseCheckBox.setSelected(true);
    useReverseCheckBox.setText("Use Opposite Value as well");
    cubeList.addListSelectionListener(new ManageCubesFrame_cubeList_listSelectionAdapter(this));
    helpButton.setEnabled(false);
    helpButton.setToolTipText("Get Help");
    helpButton.setText("Help");
    mappedSurfaceButton.setToolTipText("Map the values on the Surface(s)");
    mappedSurfaceButton.setText("Mapped Surface");
    mappedSurfaceButton.addActionListener(new ManageCubesFrame_mappedSurfaceButton_actionAdapter(this));
    histogramButton.setToolTipText("Plot Cube Values Distribution");
    histogramButton.setText("Histogram");
    histogramButton.addActionListener(new ManageCubesFrame_histogramButton_actionAdapter(this));
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel1.add(jPanel3, BorderLayout.SOUTH);
    jPanel3.add(jLabel3);
    jPanel3.add(isovalueFormattedTextField);
    jPanel3.add(useReverseCheckBox);
    jPanel1.add(jScrollPane1, BorderLayout.NORTH);
    jScrollPane1.getViewport().add(cubeList);
    jPanel4.add(hideButton);
    jPanel4.add(helpButton);
    jPanel5.add(loadButton);
    jPanel5.add(deleteButton);
    jPanel5.add(saveButton);
    jPanel2.add(jPanel4, BorderLayout.SOUTH);
    jPanel2.add(surfaceButtonsPanel, BorderLayout.CENTER);
    surfaceButtonsPanel.add(isosurfaceButton);
    surfaceButtonsPanel.add(mappedSurfaceButton);
    surfaceButtonsPanel.add(histogramButton);
    jPanel2.add(jPanel5, BorderLayout.NORTH);
    if (Cubes != null && Cubes.size() > 0) {
      _setCubeList(Cubes);
    }

    cubeList.setVisibleRowCount(10);

    isovalueFormattedTextField.setValue(new Float(isovalue));
    isovalueFormattedTextField.setColumns(10);
  }

  public void setCubeList(java.util.List elements) {
    Cubes = elements;
    _setCubeList(elements);
  }

  private void _setCubeList(java.util.List elements) {
    cubeList.removeAll();
    if (elements == null || elements.size() < 1) {
      cubeList.setListData(new Object[0]);
      return;
    }

    info = null;
    info = new ArrayList();
    int count = 0;
    for (int i = 0; i < elements.size(); i++) {
      GaussianCube cube = (GaussianCube) elements.get(i);
      count += cube.countCubes();
    }

    String el[] = new String[count];

    count = 0;
    for (int i = 0; i < elements.size(); i++) {
      GaussianCube cube = (GaussianCube) elements.get(i);
      String name = cube.getCubeDescription();
      for (int j = 0; j < cube.countCubes(); j++, count++) {
        String cubeLabel = cube.getCubeLabel(j);
        if (cubeLabel == null) {
          el[count] = name + " " + (j + 1);
        }
        else {
          el[count] = name + " " + cubeLabel;
        }
        int ref[] = new int[2];
        ref[0] = i;
        ref[1] = j;
        info.add(ref);
      }
    }

    //Arrays.sort(el);
    cubeList.setListData(el);
    //Monomers.updateUI();
    this.pack();
  }

  public static void main(String[] args) {
    ManageCubesFrame managecubesframe = new ManageCubesFrame(
        "Manage Gaussian cubes", null);
  }

  public void hideButton_actionPerformed(ActionEvent e) {
    this.setVisible(false);
  }

  public void isosurfaceButton_actionPerformed(ActionEvent e) {
    if (!isosurfaceButton.isEnabled()) {
      return;
    }

    if (cubeList.getModel().getSize() < 1) {
      JOptionPane.showMessageDialog(this, "Load Gaussian Cube file first", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (java3dUniverse == null) {
      JOptionPane.showMessageDialog(this, "INTERNAL ERROR: Java3dUniverse is not set", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    isosurfaceButton.setEnabled(false);
    validate();

    int cubes[] = cubeList.getSelectedIndices();

    if (cubes == null || cubes.length < 1) {
      if (info.size() > 1) {
        JOptionPane.showMessageDialog(this, "Select cube(s) first!", "Warning", JOptionPane.WARNING_MESSAGE);
        isosurfaceButton.setEnabled(true);
        return;
      }

      cubes = new int[1];
      cubes[0] = 0;
    }

    isovalue = ( (Number) isovalueFormattedTextField.getValue()).floatValue();

    for (int i = 0; i < cubes.length; i++) {
      int ref[] = (int[]) info.get(cubes[i]);
      GaussianCube cube = (GaussianCube) Cubes.get(ref[0]);

      if (isovalue < 0 && cube.isPositiveValuesOnly()) {
        JOptionPane.showMessageDialog(this, "Values from cube \"" + cube.getCubeDescription() + "\" can be only positive",
                                      "Warning", JOptionPane.WARNING_MESSAGE);

        continue;
      }

      //GaussianJava3dFactory.getSurface(cube,ref[1],0.02f);
      GraphicsObjectInterface graphics = GaussianJava3dFactory.getGraphicsObject(cube, ref[1], isovalue,
          useReverseCheckBox.isSelected());
      String cubeLabel = cube.getCubeLabel(ref[1]);
      if (cubeLabel == null || cubeLabel.length() < 1) {
        graphics.setName(cube.getCubeDescription());
      }
      else {
        graphics.setName(cube.getCubeDescription() + " " + cubeLabel);
      }
      java3dUniverse.addGraphics(graphics);

      //if (graphicsObjectPropertiesFrame != null) {
      //  graphicsObjectPropertiesFrame.updateTree();
      //}
      jamberooCore.updateGraphicsObjectsDialog();

    }

    isosurfaceButton.setEnabled(true);
  }

  public void loadButton_actionPerformed(ActionEvent e) {

    if (java3dUniverse == null) {
      JOptionPane.showMessageDialog(this, "Internal Error: java3dUniverse is not set", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (Cubes == null) {
      JOptionPane.showMessageDialog(this, "Internal Error: Cubes is not set", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    GaussianCube gCube = new GaussianCube();
    try {
      MoleculeInterface mol = new Molecule();
      gCube.parseGaussianCube();
      gCube.getMolecule(mol);
      Molecule.guessCovalentBonds(mol);
      Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      java3dUniverse.addMolecule(mol);
      Cubes.add(gCube);

      JOptionPane.showMessageDialog(this, gCube.getTitle() + "\n" + gCube.getCubeDescription() + "\n" + "Number of atoms: " +
                                    mol.getNumberOfAtoms() + "\n" + "Number of cubes: " + gCube.countCubes()
                                    , "Gaussian cube info", JOptionPane.INFORMATION_MESSAGE);
      this._setCubeList(Cubes);
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
      JOptionPane.showMessageDialog(this, "Error Loading Gaussian Cube file : " + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

  }

  public void cubeList_valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }

    int cubes[] = cubeList.getSelectedIndices();
    if (cubes == null || cubes.length < 1) {
      return;
    }
    boolean positiveOnly = true;
    float isovalue = 0;
    for (int i = 0; i < cubes.length; i++) {
      int ref[] = (int[]) info.get(cubes[i]);
      GaussianCube cube = (GaussianCube) Cubes.get(ref[0]);
      positiveOnly &= cube.isPositiveValuesOnly();
      if (i == 0) {
        isovalue = cube.getDefaultIsovalue();
      }
      else if (isovalue > cube.getDefaultIsovalue()) {
        isovalue = cube.getDefaultIsovalue();
      }
    }

    this.isovalueFormattedTextField.setValue(new Float(isovalue));

    useReverseCheckBox.setSelected(!positiveOnly);
  }

  public void deleteButton_actionPerformed(ActionEvent e) {
    if (!deleteButton.isEnabled()) {
      return;
    }

    int cubes[] = cubeList.getSelectedIndices();
    if (cubes == null || cubes.length < 1) {
      JOptionPane.showMessageDialog(this, "Select cube(s) first", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }

    deleteButton.setEnabled(false);

    for (int i = cubes.length - 1; i > -1; i--) {
      int ref[] = (int[]) info.get(cubes[i]);
      GaussianCube cube = (GaussianCube) Cubes.get(ref[0]);

      if (cube.countCubes() == 1) {
        Cubes.remove(ref[0]);
        continue;
      }

      cube.removeVolumetricData(ref[1]);
    }

    _setCubeList(Cubes);
    deleteButton.setEnabled(true);
  }

  public void saveButton_actionPerformed(ActionEvent e) {
    if (!saveButton.isEnabled()) {
      return;
    }

    int cubes[] = cubeList.getSelectedIndices();
    if (cubes == null || cubes.length < 1) {
      JOptionPane.showMessageDialog(this, "Select a cube first", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (cubes.length > 1) {
      JOptionPane.showMessageDialog(this, "Select only one cube for saving", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }

    int ref[] = (int[]) info.get(cubes[0]);
    VolumeData cube = (VolumeData) Cubes.get(ref[0]);

    try {
      if (cube instanceof GaussianCube) {
        cube.saveVolumeData(ref[1]);
      }
      else {
        cube.saveVolumeData(ref[1]);
      }
      //cube.saveGaussianCube(ref[1]);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
                                    "Error saving Gaussian cube: " + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
    }

    saveButton.setEnabled(false);
    saveButton.setEnabled(true);
  }

  public JButton getHelpButton() {
    return helpButton;
  }

  public void mappedSurfaceButton_actionPerformed(ActionEvent e) {

    if (cubeList.getModel().getSize() < 1) {
      JOptionPane.showMessageDialog(this, "Load Gaussian Cube file first", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (java3dUniverse == null) {
      JOptionPane.showMessageDialog(this, "INTERNAL ERROR: Java3dUniverse is not set", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    int cubes[] = cubeList.getSelectedIndices();

    if (cubes == null || cubes.length < 1) {
      JOptionPane.showMessageDialog(this, "Select a cube first!", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }
    else if (cubes.length != 1) {
      JOptionPane.showMessageDialog(this, "Only one cube should be selected for mapped surface", "Warning",
                                    JOptionPane.WARNING_MESSAGE);
      return;
    }

    java.util.List<GraphicsObjectInterface> graphicsObjects = java3dUniverse.getGraphicsObjects();
    if (graphicsObjects == null || graphicsObjects.size() < 1) {
      JOptionPane.showMessageDialog(this, "No Surface(s) to map the values", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }
    VertexObjectsDialog vod = new VertexObjectsDialog(this, "Select Surfaces", true);
    vod.setTree(graphicsObjects);

    vod.setLocationRelativeTo(this);

    vod.setVisible(true);

    if (!vod.isOKPressed()) {
      return;
    }

    java.util.List shape3ds = vod.getSelectedGraphicsObjects();

    if (shape3ds == null || shape3ds.size() < 1) {
      JOptionPane.showMessageDialog(this, "Select surfaces first", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }

    int ref[] = (int[]) info.get(cubes[0]);
    GaussianCube cube = (GaussianCube) Cubes.get(ref[0]);

    float Min = 0, Max = 0;
    for (int i = 0; i < shape3ds.size(); ) {
      Object object = shape3ds.get(i);
      // --- Could be GraphicsObjectInterface or BranchGroup
      if (object instanceof GraphicsObjectInterface) {
        GraphicsObjectInterface graphics = (GraphicsObjectInterface) object;
        float[] minMax = GaussianJava3dFactory.calcPropertyOnSurface(graphics, cube, ref[1]);
        //GaussianJava3dFactory.mapSurface(graphics, cube, ref[1]);
        if (i == 0) {
          Min = minMax[0];
          Max = minMax[1];
        }
        else {
          if (Min > minMax[0]) {
            Min = minMax[0];
          }
          if (Max < minMax[1]) {
            Max = minMax[1];
          }
        }
      }
      else if (object != null) {
        float[] minMax = GaussianJava3dFactory.calcPropertyOnSurface(object, cube, ref[1]);
        //GaussianJava3dFactory.mapSurface(graphics, cube, ref[1]);
        if (i == 0) {
          Min = minMax[0];
          Max = minMax[1];
        }
        else {
          if (Min > minMax[0]) {
            Min = minMax[0];
          }
          if (Max < minMax[1]) {
            Max = minMax[1];
          }
        }

      }
      i++;
    }

    logger.info("Min value on surface: " + Min + " max: " + Max);
    ColorRangeScheme crs = new ColorRangeScheme(Min, Max);
    logger.info("Adjusted Min&Max values on surface : " + crs.getClipMin() + " - " + crs.getClipMax());

    for (int i = 0; i < shape3ds.size(); ) {
      Object object = shape3ds.get(i);
      // --- Could be GraphicsObjectInterface or BranchGroup
      if (object instanceof GraphicsObjectInterface) {
        GraphicsObjectInterface graphics = (GraphicsObjectInterface) object;
        GaussianJava3dFactory.mapSurface(graphics, crs);
        //GaussianJava3dFactory.mapSurface(graphics, cube, ref[1]);
      }
      else if (object != null) {

      }
      i++;
    }

  }


  public JamberooCore getJamberooCore() {
    return jamberooCore;
  }

  public void setJamberooCore(JamberooCore jamberooCore) {
    this.jamberooCore = jamberooCore;
    this.java3dUniverse = this.jamberooCore.getJamberooRenderer();
  }


  public void histogramButton_actionPerformed(ActionEvent e) {
    if (cubeList.getModel().getSize() < 1) {
      JOptionPane.showMessageDialog(this, "Load Gaussian Cube file first", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    int cubes[] = cubeList.getSelectedIndices();

    if (cubes == null || cubes.length < 1) {
      if (info.size() > 1) {
        JOptionPane.showMessageDialog(this, "Select cube(s) first!", "Warning", JOptionPane.WARNING_MESSAGE);
        isosurfaceButton.setEnabled(true);
        return;
      }

      cubes = new int[1];
      cubes[0] = 0;
    }

    for (int i = 0; i < cubes.length; i++) {
      int ref[] = (int[]) info.get(cubes[i]);
      VolumeData cube = (VolumeData) Cubes.get(ref[0]);

      double[] x = new double[nBins];
      double[] y = new double[nBins];

      ChartFrame chart = null;
      try {
        cube.getNormalizedHistogram(x, y, nBins, ref[0]);
        chart = new ChartFrame();
        chart.addDataSeries(x, y, "Values frequency");
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Cannot create chart: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      chart.setIconImage(histogramImage.getImage());
      chart.setChartTitle("Function Value Frequencies");
      chart.setXAxisTitle("Function Value");
      chart.enableAnimationPanel(false);

      String cubeLabel = cube.getCubeLabel(ref[1]);
      if (cubeLabel == null || cubeLabel.length() < 1) {
        chart.setTitle("Function Value Histogram: " + cube.getCubeDescription());
      }
      else {
        chart.setTitle("Function Value Histogram: " + cube.getCubeDescription() + " " + cubeLabel);
      }

      chart.validate();
      chart.setLocationRelativeTo(this);
      chart.showChart(true);
    }

  }

  private class ManageCubesFrame_isosurfaceButton_actionAdapter
      implements ActionListener {
    private ManageCubesFrame adaptee;
    ManageCubesFrame_isosurfaceButton_actionAdapter(ManageCubesFrame adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.isosurfaceButton_actionPerformed(e);
    }
  }

  private class ManageCubesFrame_hideButton_actionAdapter
      implements ActionListener {
    private ManageCubesFrame adaptee;
    ManageCubesFrame_hideButton_actionAdapter(ManageCubesFrame adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.hideButton_actionPerformed(e);
    }
  }

  private class ManageCubesFrame_saveButton_actionAdapter
      implements ActionListener {
    private ManageCubesFrame adaptee;
    ManageCubesFrame_saveButton_actionAdapter(ManageCubesFrame adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.saveButton_actionPerformed(e);
    }
  }

  private class ManageCubesFrame_deleteButton_actionAdapter
      implements ActionListener {
    private ManageCubesFrame adaptee;
    ManageCubesFrame_deleteButton_actionAdapter(ManageCubesFrame adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.deleteButton_actionPerformed(e);
    }
  }

  private class ManageCubesFrame_cubeList_listSelectionAdapter
      implements ListSelectionListener {
    private ManageCubesFrame adaptee;
    ManageCubesFrame_cubeList_listSelectionAdapter(ManageCubesFrame adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
      adaptee.cubeList_valueChanged(e);
    }
  }

  private class ManageCubesFrame_loadButton_actionAdapter
      implements ActionListener {
    private ManageCubesFrame adaptee;
    ManageCubesFrame_loadButton_actionAdapter(ManageCubesFrame adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.loadButton_actionPerformed(e);
    }
  }

}

class ManageCubesFrame_histogramButton_actionAdapter
    implements ActionListener {
  private ManageCubesFrame adaptee;
  ManageCubesFrame_histogramButton_actionAdapter(ManageCubesFrame adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.histogramButton_actionPerformed(e);
  }
}

class ManageCubesFrame_mappedSurfaceButton_actionAdapter
    implements ActionListener {
  private ManageCubesFrame adaptee;
  ManageCubesFrame_mappedSurfaceButton_actionAdapter(ManageCubesFrame adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.mappedSurfaceButton_actionPerformed(e);
  }
}
