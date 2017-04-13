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
package cct.dialogs;

import cct.chart.ChartFrame;
import cct.chart.PropsToChartDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import cct.interfaces.MoleculeInterface;
import cct.j3d.ImageSequenceManager;
import cct.j3d.Java3dUniverse;
import cct.modelling.StructureManager;
import cct.modelling.StructureManagerInterface;
import cct.modelling.TrajectoryClientInterface;
import cct.tools.DataSets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import static javax.swing.JOptionPane.ERROR_MESSAGE;

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
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class TrajectoryAnalysisDialog
    extends JDialog implements TrajectoryClientInterface, StructureManagerInterface, ItemListener, ActionListener {

  protected JPanel panel1 = new JPanel();
  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JTabbedPane mainTabbedPane = new JTabbedPane();
  protected JPanel mainPanel = new JPanel();
  protected BorderLayout borderLayout2 = new BorderLayout();
  protected TrajectoriesManagerPanel trajectoriesManagerPanel = new TrajectoriesManagerPanel();
  protected AnimationPanel animationPanel = new AnimationPanel(this);
  private ImageSequencePanel imageSequencePanel = new ImageSequencePanel();
  protected JPanel buttonPanel = new JPanel();
  protected JButton hideButton = new JButton();
  protected JButton plotButton = new JButton();
  protected Border border1 = BorderFactory.createLineBorder(Color.darkGray, 1);
  protected Border border2 = new TitledBorder(border1, "Animation Controls");
  protected Border border3 = BorderFactory.createLineBorder(Color.darkGray, 1);
  protected Border border4 = new TitledBorder(border3, "Loaded Trajectories");
  static final Logger logger = Logger.getLogger(TrajectoryAnalysisDialog.class.getCanonicalName());

  private float[][] Coordinates = null;
  private javax.swing.JCheckBox generateImageSeqCheckBox;
  private javax.swing.JComboBox imageSeqFormatComboBox;

  //protected MoleculeInterface referenceStructure = null;
  private Java3dUniverse java3dUniverse = null;
  private ImageSequenceManager imageSequenceManager;

  public TrajectoryAnalysisDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public TrajectoryAnalysisDialog() {
    this(new Frame(), "TrajectoryAnalysisDialog", false);
  }

  public void setRenderer(Java3dUniverse j3d) {
    java3dUniverse = j3d;
    imageSequenceManager = new ImageSequenceManager(java3dUniverse);

    imageSequencePanel.setImageSeqFormatComboBox(imageSequenceManager.getAvailableFormats());
    imageSequencePanel.getImageSeqFormatComboBox().setSelectedIndex(0);

    trajectoriesManagerPanel.setReferenceMolecule(java3dUniverse.getMoleculeInterface());
    allocateCoordinates(java3dUniverse.getMoleculeInterface());
  }

  void allocateCoordinates(MoleculeInterface mol) {
    if (mol == null || mol.getNumberOfAtoms() < 1) {
      Coordinates = null;
    } else {
      Coordinates = new float[mol.getNumberOfAtoms()][3];
    }
  }

  private void jbInit() throws Exception {

    animationPanel.setStructureManagerInterface(this);

    border4 = new TitledBorder(BorderFactory.createLineBorder(Color.darkGray, 1), "Loaded Trajectories");
    border2 = new TitledBorder(BorderFactory.createLineBorder(Color.darkGray, 1), "Animation Controls");
    panel1.setLayout(borderLayout1);
    mainPanel.setLayout(borderLayout2);
    animationPanel.setBorder(border2);
    hideButton.setToolTipText("Hide Dialog");
    hideButton.setText(" Hide ");
    hideButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        hideButton_actionPerformed(e);
      }
    });
    // ---
    plotButton.setToolTipText("Select properties to Plot");
    plotButton.setText(" Plot ");
    plotButton.addActionListener(this);
    // ---
    trajectoriesManagerPanel.setBorder(border4);
    getContentPane().add(panel1);
    panel1.add(mainTabbedPane, BorderLayout.CENTER);
    mainPanel.add(animationPanel, BorderLayout.CENTER);
    mainPanel.add(trajectoriesManagerPanel, BorderLayout.NORTH);
    panel1.add(buttonPanel, BorderLayout.SOUTH);

    buttonPanel.add(plotButton);
    buttonPanel.add(hideButton);

    mainTabbedPane.add(mainPanel, "Main");
    mainTabbedPane.add(this.imageSequencePanel, "Movie");

    trajectoriesManagerPanel.setTrajectoryClientInterface(animationPanel);
    // --- 
    animationPanel.addActionListener(this);
    // --- Animation panel setup...
    generateImageSeqCheckBox = imageSequencePanel.getGenerateImageSeqCheckBox();
    generateImageSeqCheckBox.addItemListener(this);
    imageSequencePanel.getImageSeqFormatComboBox().removeAllItems();
    imageSeqFormatComboBox = imageSequencePanel.getImageSeqFormatComboBox();
  }

  public void itemStateChanged(ItemEvent e) {
    if (generateImageSeqCheckBox == e.getSource()) {
      if (!generateImageSeqCheckBox.isSelected()) {
        return;
      }
      // ---
      Object item = imageSequencePanel.getImageSeqFormatComboBox().getSelectedItem();
      if (item == null) {
        JOptionPane.showMessageDialog(this, "Image Sequence format is not set!", "Warning", JOptionPane.WARNING_MESSAGE);
        generateImageSeqCheckBox.setSelected(false);
        return;
      }
      // ---
      imageSequenceManager.setOutputFormat(item.toString());
      imageSequenceManager.initializeImageSequence();
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == animationPanel) {
      if (AnimationPanel.ANIMATION_STARTED == e.getID()) {

      } else if (AnimationPanel.ANIMATION_FINISHED == e.getID()) {
        imageSequencePanel.getGenerateImageSeqCheckBox().setSelected(false);
        imageSequenceManager.finalizeImageSequence();
      } else {
        logger.log(Level.WARNING, "Unknown AnimationPanel event" + e.getID());
      }
    } else if (e.getSource() == plotButton) {
      PropsToChartDialog propsToChartDialog = new PropsToChartDialog(new Frame(), true);
      propsToChartDialog.setLocationRelativeTo(this);
      propsToChartDialog.setDataRangePanelVisible(true);

      StructureManager sm = trajectoriesManagerPanel.getStructureManager();
      if (sm != null) {
        propsToChartDialog.setStructureManager(sm);
      } else {
        JOptionPane.showMessageDialog(this, "No Properties to chert", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      propsToChartDialog.setVisible(true);

      if (!propsToChartDialog.isOkPressed()) {
        return;
      }

      ChartFrame jchartframe = null;
      try {
        jchartframe = new ChartFrame();
        jchartframe.setChartTitle("Property Change Along Trajectory");
        Object xAxis = propsToChartDialog.getXAxisValue();
        if (xAxis == null) {
          xAxis = DataSets.DATA_ROW_NUMBER;
        }
        double x[] = sm.getDataAsDouble(xAxis.toString());
        Object[] yAxis = propsToChartDialog.getYAxisValues();
        for (Object yvalue : yAxis) {
          double y[] = sm.getDataAsDouble(yvalue.toString());
          jchartframe.addDataSeries(x, y, xAxis.toString() + " vs " + yvalue);
        }

        jchartframe.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jchartframe.showChart(true);
      } catch (Exception ex) {
        Logger.getLogger(TrajectoryAnalysisDialog.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Error initializaing Chart Frame: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      jchartframe.setSize(1024, 600);
      jchartframe.enableAnimationPanel(false);
    }
  }

  public void hideButton_actionPerformed(ActionEvent e) {
    this.setVisible(false);
  }

  @Override
  public void setSnapshotsCount(int new_max) {
    animationPanel.setSnapshotsCount(new_max);
  }

  @Override
  public void selectStructure(int number, String term) throws Exception {
    selectStructure(number);
  }

  @Override
  public void selectStructure(int number) throws Exception {
    MoleculeInterface referenceStructure = java3dUniverse.getMoleculeInterface();

    MoleculeInterface mol = trajectoriesManagerPanel.getStructureManager().getReferenceMolecule();

    if (mol == null) {
      throw new Exception("Reference structure in Structure Manager is not set");
    }

    if (mol.getNumberOfAtoms() != referenceStructure.getNumberOfAtoms()) {
      int answer = JOptionPane.showConfirmDialog(null, "Number of atoms in molecule does not match that for MD trajectory.\n"
          + "Do you want to purge MD trajectories?", "Molecules mismatch", JOptionPane.YES_NO_OPTION);
      if (answer == JOptionPane.OK_OPTION) {
        trajectoriesManagerPanel.deleteAlltrajectories();
        trajectoriesManagerPanel.setReferenceMolecule(referenceStructure);
        throw new Exception("Load trajectories first!");
      }
      throw new Exception("Number of atoms in molecule does not match that for MD trajectory");
    }

    try {
      trajectoriesManagerPanel.getStructureManager().getStructure(number, Coordinates);
    } catch (Exception ex) {
      throw ex;
    }

    for (int i = 0; i < referenceStructure.getNumberOfAtoms(); i++) {
      referenceStructure.getAtomInterface(i).setXYZ(Coordinates[i][0], Coordinates[i][1], Coordinates[i][2]);
    }
    long start = System.currentTimeMillis();
    java3dUniverse.updateMolecularGeometry();
    float secs = (System.currentTimeMillis() - start) / 1000.0f;
    logger.info("Elapsed time for java3d update: " + secs);
    imageSequenceManager.generateImage(java3dUniverse, number + 1);

    // --- Generate "image" if needed
    if (generateImageSeqCheckBox.isSelected()) {

    }
  }

  @Override
  public float[][] getStructure(int n) {
    return null;
  }

  @Override
  public float[][] getStructure(int n, String term) {
    return null;
  }

}
