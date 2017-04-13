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
package cct.modelling.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import cct.chart.ChartFrame;
import cct.chart.VibrationSpectrumChart;
import cct.interfaces.OutputResultsInterface;
import cct.interfaces.VibrationsRendererProvider;
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
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class VibrationsControlPanel
    extends JPanel implements ListSelectionListener {

  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel tablePanel = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JTable jTable1 = new JTable();
  private JPanel buttonPanel = new JPanel();
  private JPanel controlsPanel = new JPanel();
  private JButton spectrumButton = new JButton();
  private JPanel displacementPanel = new JPanel();
  private JSlider displacementSlider = new JSlider();
  private JPanel speedPanel = new JPanel();
  private JSlider speedSlider = new JSlider();
  private JPanel jPanel1 = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private BorderLayout borderLayout4 = new BorderLayout();
  private BorderLayout borderLayout5 = new BorderLayout();
  private JPanel jPanel2 = new JPanel();
  private JCheckBox animateCheckBox = new JCheckBox();
  private BorderLayout borderLayout6 = new BorderLayout();
  private JCheckBox showVectorsCheckBox = new JCheckBox();
  private FlowLayout flowLayout1 = new FlowLayout();
  private JComboBox spectrumComboBox = new JComboBox();
  private BorderLayout borderLayout7 = new BorderLayout();

  private OutputResultsInterface outputResultsInterface = null;
  private VibrationsRendererProvider vibrationsRendererProvider = null;
  private int previouslySelectedRow = -1;
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private Hashtable speedLabelTable = new Hashtable();
  private Hashtable displLabelTable = new Hashtable();
  private VIBRATIONAL_SPECTRUM[] spectra = null;
  private boolean savingActive = false;
  private JButton saveSceneButton = new JButton();

  public VibrationsControlPanel() {
    try {
      jbInit();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    speedLabelTable.put(new Integer(0), new JLabel("Fast"));
    speedLabelTable.put(new Integer(speedSlider.getMaximum()), new JLabel("Slow"));
    displLabelTable.put(new Integer(0), new JLabel("Small"));
    speedSlider.setInverted(true);
    speedSlider.setLabelTable(speedLabelTable);
    speedSlider.setPaintLabels(true);
    this.setLayout(borderLayout1);
    spectrumButton.setText("Show Spectrum");
    spectrumButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        spectrumButton_actionPerformed(e);
      }
    });
    speedPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1), "Animation Speed"));
    speedPanel.setLayout(borderLayout5);
    displacementPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1), "Displacement"));
    displacementPanel.setLayout(borderLayout4);
    controlsPanel.setLayout(borderLayout2);
    jPanel1.setLayout(gridBagLayout1);
    animateCheckBox.setToolTipText("");
    animateCheckBox.setText("Animate Vibrations");
    animateCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        animateCheckBox_itemStateChanged(e);
      }
    });
    jPanel2.setLayout(borderLayout6);
    showVectorsCheckBox.setToolTipText("");
    showVectorsCheckBox.setText("Show Displacement Vectors");
    showVectorsCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        showVectorsCheckBox_itemStateChanged(e);
      }
    });
    buttonPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    tablePanel.setLayout(borderLayout7);
    tablePanel.setMinimumSize(new Dimension(23, 50));
    tablePanel.setPreferredSize(new Dimension(452, 200));
    speedSlider.setEnabled(false);
    speedSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        speedSlider_stateChanged(e);
      }
    });
    displacementSlider.setLabelTable(displLabelTable);
    displacementSlider.setPaintLabels(true);
    displacementSlider.setEnabled(false);
    saveSceneButton.setEnabled(true);
    saveSceneButton.setText("Save Scene");
    saveSceneButton.addActionListener(new VibrationsControlPanel_saveSceneButton_actionAdapter(this));
    jScrollPane1.getViewport().add(jTable1);
    buttonPanel.add(spectrumComboBox);
    buttonPanel.add(spectrumButton);
    buttonPanel.add(saveSceneButton);
    this.add(controlsPanel, BorderLayout.CENTER);
    displacementPanel.add(displacementSlider, BorderLayout.CENTER);
    controlsPanel.add(jPanel2, BorderLayout.SOUTH);
    controlsPanel.add(jPanel1, BorderLayout.NORTH);
    jPanel2.add(showVectorsCheckBox, BorderLayout.CENTER);
    jPanel2.add(buttonPanel, BorderLayout.SOUTH);
    jPanel2.add(animateCheckBox, BorderLayout.NORTH);
    this.add(tablePanel, BorderLayout.NORTH);
    tablePanel.add(jScrollPane1, BorderLayout.CENTER);
    jPanel1.add(displacementPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    jPanel1.add(speedPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    speedPanel.add(speedSlider, BorderLayout.CENTER);
  }

  public void setSpectraProvider(OutputResultsInterface provider) {
    outputResultsInterface = provider;
    ChartTableModel model = new ChartTableModel(outputResultsInterface);
    jTable1 = new JTable(model);
    jTable1.getSelectionModel().addListSelectionListener(this);
    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jScrollPane1.getViewport().removeAll();
    jScrollPane1.getViewport().add(jTable1);
    spectra = provider.availableVibrationalSpectra();
    spectrumComboBox.removeAllItems();
    if (spectra != null) {
      for (int i = 0; i < spectra.length; i++) {
        spectrumComboBox.addItem(VIBRATIONAL_SPECTRUM.getSpectrumName(spectra[i]));
      }
    }
    if (spectrumComboBox.getItemCount() > 0) {
      spectrumComboBox.setSelectedIndex(0);
    }

  }

  public void setVibrationsRendererProvider(VibrationsRendererProvider provider) {
    vibrationsRendererProvider = provider;
    //speedSlider.setLabelTable(speedLabelTable);
    speedSlider.setEnabled(false);
    speedSlider.setMinimum(vibrationsRendererProvider.getMinimumDuration());
    speedSlider.setMaximum(vibrationsRendererProvider.getMaximumDuration());
    speedSlider.setValue(vibrationsRendererProvider.getDuration());

    speedLabelTable.clear();
    speedLabelTable.put(new Integer(0), new JLabel("Fast"));
    speedLabelTable.put(new Integer(speedSlider.getMaximum()), new JLabel("Slow"));
    speedSlider.setLabelTable(speedLabelTable);

    speedSlider.setEnabled(true);
  }

  public void cleanupMess() {
    if (previouslySelectedRow != -1) {
      if (animateCheckBox.isSelected()) {
        vibrationsRendererProvider.animateVibrations(previouslySelectedRow, false);
      }
      if (showVectorsCheckBox.isSelected()) {
        vibrationsRendererProvider.showDisplacementVectors(previouslySelectedRow, false);
      }
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {

    if (e.getValueIsAdjusting()) {
      return;
    }
    if (jTable1.getSelectedRowCount() == 0 || jTable1.getSelectedRowCount() > 1) {
      return;
    }
    int n = jTable1.getSelectedRow();

    if (vibrationsRendererProvider != null) {
      if (previouslySelectedRow != -1) {
        if (animateCheckBox.isSelected()) {
          vibrationsRendererProvider.animateVibrations(previouslySelectedRow, false);
        }
        if (showVectorsCheckBox.isSelected()) {
          vibrationsRendererProvider.showDisplacementVectors(previouslySelectedRow, false);
        }
      } else if (n == -1 && previouslySelectedRow != -1) {
        if (animateCheckBox.isSelected()) {
          vibrationsRendererProvider.animateVibrations(previouslySelectedRow, false);
        }
        if (showVectorsCheckBox.isSelected()) {
          vibrationsRendererProvider.showDisplacementVectors(previouslySelectedRow, false);
        }

      }

      previouslySelectedRow = n;

      if (n != -1) {
        if (animateCheckBox.isSelected()) {
          vibrationsRendererProvider.setDuration(speedSlider.getValue());
          vibrationsRendererProvider.animateVibrations(previouslySelectedRow, true);
        }
        if (showVectorsCheckBox.isSelected()) {
          vibrationsRendererProvider.showDisplacementVectors(previouslySelectedRow, true);
        }

      }
    }
  }

  public void showVectorsCheckBox_stateChanged(ChangeEvent e) {
    int n = jTable1.getSelectedRow();
    if (n == -1) {
      return;
    }
    vibrationsRendererProvider.showDisplacementVectors(n, showVectorsCheckBox.isSelected());
  }

  public void animateCheckBox_stateChanged(ChangeEvent e) {
    int n = jTable1.getSelectedRow();
    if (n == -1) {
      return;
    }
    vibrationsRendererProvider.animateVibrations(n, animateCheckBox.isSelected());
  }

  public void speedSlider_stateChanged(ChangeEvent e) {
    if (!speedSlider.isEnabled()) {
      return;
    }
    if (speedSlider.getValueIsAdjusting()) {
      return;
    }

    //if (animateCheckBox.isSelected()) {
    vibrationsRendererProvider.setDuration(speedSlider.getValue());
    //}

  }

  public void showVectorsCheckBox_itemStateChanged(ItemEvent e) {
    int n = jTable1.getSelectedRow();
    if (n == -1) {
      return;
    }
    vibrationsRendererProvider.showDisplacementVectors(n, showVectorsCheckBox.isSelected());
  }

  public void animateCheckBox_itemStateChanged(ItemEvent e) {
    int n = jTable1.getSelectedRow();
    if (n == -1) {
      return;
    }
    vibrationsRendererProvider.animateVibrations(n, animateCheckBox.isSelected());
  }

  public void spectrumButton_actionPerformed(ActionEvent e) {
    if (outputResultsInterface == null || spectrumComboBox.getItemCount() < 1) {
      return;
    }

    int n = spectrumComboBox.getSelectedIndex();
    if (n == -1) {
      return;
    }

    VibrationSpectrumChart vsChart = new VibrationSpectrumChart(outputResultsInterface);
    ChartFrame chart = null;
    try {
      chart = vsChart.createChart(spectra[n]);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Cannot plot "
          + VIBRATIONAL_SPECTRUM.getSpectrumName(spectra[n]) + " : " + ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    chart.setLocationRelativeTo(this);
    chart.showChart(true);
  }

  public void saveSceneButton_actionPerformed(ActionEvent e) {
    int n = jTable1.getSelectedRow();
    if (n == -1) {
      return;
    }

    if (savingActive) {
      return;
    }

    String[] choices = {"VRML", "PovRay"};
    String input = (String) JOptionPane.showInputDialog(null, "Choose file format...",
        "The Choice of Animation Format", JOptionPane.QUESTION_MESSAGE, null, // Use
        // default
        // icon
        choices, // Array of choices
        choices[0]); // Initial choice
    if (input == null) {
      return;
    }

    savingActive = true;
    try {
      vibrationsRendererProvider.saveScene(n, input);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error Saving " + input + " file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    savingActive = false;
  }

  class ChartTableModel
      extends AbstractTableModel implements TableModel {

    private OutputResultsInterface provider = null;
    private String[] spectra = null;
    private VIBRATIONAL_SPECTRUM[] vSpectra = null;
    //private Object[][] data;

    /**
     * Creates a new table model
     *
     * @param rows the row count.
     */
    public ChartTableModel(OutputResultsInterface p) {
      provider = p;
      vSpectra = provider.availableVibrationalSpectra();

      if (vSpectra == null || vSpectra.length < 1) {
        return;
      }

      spectra = new String[vSpectra.length];
      for (int i = 0; i < spectra.length; i++) {
        spectra[i] = VIBRATIONAL_SPECTRUM.getSpectrumName(vSpectra[i]);
      }
    }

    /**
     * Returns the column count.
     *
     * @return 7.
     */
    @Override
    public int getColumnCount() {
      return provider.countSpectra() + 2;
    }

    /**
     * Returns the row count.
     *
     * @return The row count.
     */
    @Override
    public int getRowCount() {
      return provider.countFrequencies();
    }

    /**
     * Returns the value at the specified cell in the table.
     *
     * @param row the row index.
     * @param column the column index.
     *
     * @return The value.
     */
    @Override
    public Object getValueAt(int row, int column) {
      switch (column) {
        case 0:
          return new Integer(row + 1);
        case 1:
          return provider.getFrequency(row);
        default:
          return provider.getSpectrumValue(row, vSpectra[column - 2]);
      }
    }

    /**
     * Returns the column name.
     *
     * @param column the column index.
     *
     * @return The column name.
     */
    @Override
    public String getColumnName(int column) {
      switch (column) {
        case 0:
          return "N";
        case 1:
          return "Frequency";
        default:
          return spectra[column - 2];
      }
    }

  }

}

class VibrationsControlPanel_saveSceneButton_actionAdapter
    implements ActionListener {

  private VibrationsControlPanel adaptee;

  VibrationsControlPanel_saveSceneButton_actionAdapter(VibrationsControlPanel adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.saveSceneButton_actionPerformed(e);
  }
}
