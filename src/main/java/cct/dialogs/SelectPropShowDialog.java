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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cct.interfaces.OutputResultsInterface;
import cct.modelling.VIBRATIONAL_SPECTRUM;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class SelectPropShowDialog
    extends JDialog {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JCheckBox jobSummaryCheckBox = new JCheckBox();
  private JPanel jPanel1 = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private JCheckBox mdCheckBox = new JCheckBox();
  private BorderLayout borderLayout3 = new BorderLayout();
  private JPanel vibrationsPanel = new JPanel();
  private JCheckBox vcdCheckBox = new JCheckBox();
  private JCheckBox uDepolCheckBox = new JCheckBox();
  private JCheckBox pDepolCheckBox = new JCheckBox();
  private JCheckBox ramanCheckBox = new JCheckBox();
  private JCheckBox infraredCheckBox = new JCheckBox();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JPanel buttonsPanel = new JPanel();
  private JButton cancelButton = new JButton();
  private JButton okButton = new JButton();

  private Map<JCheckBox, VIBRATIONAL_SPECTRUM> refTable = new HashMap<JCheckBox, VIBRATIONAL_SPECTRUM> ();

  java.util.List<JCheckBox> freq = new ArrayList<JCheckBox> ();
  private boolean okPressed = true;

  public SelectPropShowDialog(Frame owner, String title) {
    super(owner, title, true);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public SelectPropShowDialog() {
    this(new Frame(), "SelectPropShowDialog");
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    this.getContentPane().setLayout(borderLayout2);
    jobSummaryCheckBox.setText("Show Job Summary");
    mdCheckBox.setToolTipText("");
    mdCheckBox.setText("Show Interactive Chart for MD/Minimization");
    jPanel1.setLayout(borderLayout3);
    vibrationsPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1),
                                               "Vibrational Spectra"));
    vibrationsPanel.setLayout(gridBagLayout1);
    vcdCheckBox.setToolTipText("");
    vcdCheckBox.setText("VCD Rotational Strength Spectrum");
    uDepolCheckBox.setToolTipText("");
    uDepolCheckBox.setText("U-Depolarization Spectrum");
    pDepolCheckBox.setToolTipText("");
    pDepolCheckBox.setText("P-Depolarization Spectrum");
    ramanCheckBox.setToolTipText("");
    ramanCheckBox.setText("Raman Spectrum");
    infraredCheckBox.setToolTipText("");
    infraredCheckBox.setText("Infrared Spectrum");
    cancelButton.setToolTipText("");
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    okButton.setToolTipText("");
    okButton.setText("  OK  ");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    panel1.add(jPanel1, BorderLayout.CENTER);
    this.getContentPane().add(panel1, BorderLayout.CENTER);
    jPanel1.add(vibrationsPanel, BorderLayout.CENTER);
    jPanel1.add(mdCheckBox, BorderLayout.NORTH);
    vibrationsPanel.add(ramanCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    vibrationsPanel.add(pDepolCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    vibrationsPanel.add(uDepolCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    vibrationsPanel.add(infraredCheckBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    vibrationsPanel.add(vcdCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0
        , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    panel1.add(jobSummaryCheckBox, BorderLayout.NORTH);
    panel1.add(buttonsPanel, BorderLayout.SOUTH);
    buttonsPanel.add(okButton);
    buttonsPanel.add(cancelButton);

    freq.add(vcdCheckBox);
    freq.add(uDepolCheckBox);
    freq.add(pDepolCheckBox);
    freq.add(ramanCheckBox);
    freq.add(infraredCheckBox);

    refTable.put(infraredCheckBox, VIBRATIONAL_SPECTRUM.INFRARED_SPECTRUM);
    refTable.put(ramanCheckBox, VIBRATIONAL_SPECTRUM.RAMAN_SPECTRUM);
    refTable.put(pDepolCheckBox, VIBRATIONAL_SPECTRUM.P_DEPOLARIZATION_SPECTRUM);
    refTable.put(uDepolCheckBox, VIBRATIONAL_SPECTRUM.U_DEPOLARIZATION_SPECTRUM);
    refTable.put(vcdCheckBox, VIBRATIONAL_SPECTRUM.VCD_ROTATIONAL_STRENGTH_SPECTRUM);
  }

  /**
   * Dialog should be shown using this function (not by using setVisible(true))
   * @return boolean - "true" - OK button was pressed, "false" - Cancel one
   */
  public boolean showDialog() {
    boolean toShow = false;
    // --- First decide to show or not vibrational spectra panel
    for (int i = 0; i < freq.size(); i++) {
      JCheckBox jCheckBox = freq.get(i);
      toShow |= jCheckBox.isSelected();
      jCheckBox.setVisible(jCheckBox.isSelected());
    }
    vibrationsPanel.setVisible(toShow);

    // Show other checkboxes?

    toShow |= jobSummaryCheckBox.isSelected();
    jobSummaryCheckBox.setVisible(jobSummaryCheckBox.isSelected());

    toShow |= mdCheckBox.isSelected();
    mdCheckBox.setVisible(mdCheckBox.isSelected());

    if (!toShow) {
      return false;
    }

    this.setVisible(true);
    return okPressed;
  }

  public void setControls(OutputResultsInterface res) {
    enableSummary(res.hasJobSummary());
    enableInteractiveChart(res.hasInteractiveChart());
    enableVCDSpectrum(res.hasVCDSpectrum());
    enableUDepolSpectrum(res.hasUDepolSpectrum());
    enablePDepolSpectrum(res.hasPDepolSpectrum());
    enableRamanSpectrum(res.hasRamanSpectrum());
    enableInfraredSpectrum(res.hasInfraredSpectrum());
  }

  public void enableSummary(boolean enable) {
    jobSummaryCheckBox.setSelected(enable);
  }

  public boolean isShowSummary() {
    return jobSummaryCheckBox.isSelected();
  }

  public VIBRATIONAL_SPECTRUM[] spectraToShow() {
    java.util.List<VIBRATIONAL_SPECTRUM> toShow = new ArrayList<VIBRATIONAL_SPECTRUM> ();
    for (int i = 0; i < freq.size(); i++) {
      JCheckBox jCheckBox = freq.get(i);
      if (jCheckBox.isSelected()) {
        toShow.add(refTable.get(jCheckBox));
      }
    }
    if (toShow.size() < 1) {
      return null;
    }
    VIBRATIONAL_SPECTRUM[] spectra = new VIBRATIONAL_SPECTRUM[toShow.size()];
    toShow.toArray(spectra);
    return spectra;
  }

  public void enableInteractiveChart(boolean enable) {
    mdCheckBox.setSelected(enable);
  }

  public boolean isShowInteractiveChart() {
    return mdCheckBox.isSelected();
  }

  public void enableVCDSpectrum(boolean enable) {
    vcdCheckBox.setSelected(enable);
  }

  public void enableUDepolSpectrum(boolean enable) {
    uDepolCheckBox.setSelected(enable);
  }

  public void enablePDepolSpectrum(boolean enable) {
    pDepolCheckBox.setSelected(enable);
  }

  public void enableRamanSpectrum(boolean enable) {
    ramanCheckBox.setSelected(enable);
  }

  public void enableInfraredSpectrum(boolean enable) {
    infraredCheckBox.setSelected(enable);
  }

  public void okButton_actionPerformed(ActionEvent e) {
    okPressed = true;
    this.setVisible(false);
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    okPressed = false;
    this.setVisible(false);

  }
}
