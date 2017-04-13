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

package cct.siesta.ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import cct.j3d.UnitCellGraphics;
import cct.math.Crystal;
import cct.modelling.CCTAtomTypes;
import cct.modelling.Molecule;
import cct.siesta.AtomicCoordinatesFormat;
import cct.siesta.ChemicalSpeciesLabel;
import cct.siesta.Siesta;
import cct.siesta.SiestaInput;
import cct.siesta.SiestaInterface;
import cct.tools.FileUtilities;
import cct.tools.IOUtils;
import cct.tools.ui.JShowText;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SiestaPanel
    extends JPanel {

   private static String FontNameKey = "FontName";
   private static String FontSizeKey = "FontSize";
   private static String FontStyleKey = "FontStyle";
   private static String defaultFontName = "default";

   private NumberFormat aFormat = NumberFormat.getNumberInstance();
   private NumberFormat bFormat = NumberFormat.getNumberInstance();
   private NumberFormat cFormat = NumberFormat.getNumberInstance();
   private NumberFormat alphaFormat = NumberFormat.getNumberInstance();
   private NumberFormat betaFormat = NumberFormat.getNumberInstance();
   private NumberFormat gammaFormat = NumberFormat.getNumberInstance();
   private NumberFormat lcConst = NumberFormat.getNumberInstance();

   private BorderLayout borderLayout1 = new BorderLayout();
   private JPanel mainPanel = new JPanel();
   private BorderLayout borderLayout2 = new BorderLayout();
   private JPanel jPanel1 = new JPanel();
   private JPanel jPanel2 = new JPanel();
   private JPanel jPanel3 = new JPanel();
   private BorderLayout borderLayout3 = new BorderLayout();
   private JLabel jLabel1 = new JLabel();
   private FlowLayout flowLayout1 = new FlowLayout();
   private JLabel jLabel2 = new JLabel();
   private FlowLayout flowLayout2 = new FlowLayout();
   private JTextField systemNameTextField = new JTextField();
   private JTextField systemLabelTextField = new JTextField();
   private JPanel speciesPanel = new JPanel();
   private JScrollPane jScrollPane1 = new JScrollPane();
   private JTextArea speciesTextArea = new JTextArea();
   private BorderLayout borderLayout4 = new BorderLayout();
   private JPanel coordPanel = new JPanel();
   private JScrollPane jScrollPane2 = new JScrollPane();
   private JTextArea coordTextArea = new JTextArea();
   private BorderLayout borderLayout5 = new BorderLayout();
   private JTabbedPane jTabbedPane1 = new JTabbedPane();
   private JPanel otherPanel = new JPanel();
   private JScrollPane jScrollPane3 = new JScrollPane();
   private JTextArea otherTextArea = new JTextArea();
   private BorderLayout borderLayout6 = new BorderLayout();
   private JPanel jPanel4 = new JPanel();
   private BorderLayout borderLayout7 = new BorderLayout();
   private JPanel jPanel5 = new JPanel();
   private BorderLayout borderLayout8 = new BorderLayout();
   private JPanel jPanel6 = new JPanel();
   private JPanel latticePanel = new JPanel();
   private BorderLayout borderLayout9 = new BorderLayout();
   private JLabel jLabel3 = new JLabel();
   private JLabel jLabel4 = new JLabel();
   private JFormattedTextField latticeConstantTextField = new JFormattedTextField(lcConst);
   private JComboBox formatComboBox = new JComboBox();
   private JLabel jLabel6 = new JLabel();
   private JLabel jLabel7 = new JLabel();
   private JLabel jLabel8 = new JLabel();
   private JLabel jLabel9 = new JLabel();
   private JLabel jLabel10 = new JLabel();
   private JFormattedTextField betaTextField = new JFormattedTextField(betaFormat);
   private JFormattedTextField alphaTextField = new JFormattedTextField(alphaFormat);
   private JFormattedTextField cTextField = new JFormattedTextField(cFormat);
   private JFormattedTextField bTextField = new JFormattedTextField(bFormat);
   private JFormattedTextField aTextField = new JFormattedTextField(aFormat);
   private JFormattedTextField gammaTextField = new JFormattedTextField(gammaFormat);
   private JPanel jPanel7 = new JPanel();
   private JPanel auxPanel = new JPanel();
   private GridBagLayout gridBagLayout1 = new GridBagLayout();
   private JLabel jLabel11 = new JLabel();
   private GridBagLayout gridBagLayout2 = new GridBagLayout();

   private MoleculeInterface molecule = null;
   private Preferences prefs;
   private AtomicCoordinatesFormat atomicCoordinatesFormat;
   private SiestaInput siestaInput = null;
   private String defaultFileName = "siesta.fdf";
   private Java3dUniverse j3d = null;
   private JButton centerMolButton = new JButton();
   private FlowLayout flowLayout3 = new FlowLayout();

   public SiestaPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      mainPanel.setLayout(borderLayout2);
      jPanel1.setLayout(borderLayout3);
      jLabel1.setToolTipText("");
      jLabel1.setText("System Label:");
      jPanel3.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      jLabel2.setToolTipText("");
      jLabel2.setText("System Name:");
      jPanel2.setLayout(flowLayout2);
      flowLayout2.setAlignment(FlowLayout.LEFT);
      systemLabelTextField.setToolTipText("");
      systemLabelTextField.setText("siesta");
      systemLabelTextField.setColumns(20);
      systemNameTextField.setColumns(20);
      speciesPanel.setLayout(borderLayout4);
      speciesPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1),
                                              "%block ChemicalSpeciesLabel"));
      coordPanel.setLayout(borderLayout5);
      coordPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1),
                                            "%block AtomicCoordinatesAndAtomicSpecies"));
      otherPanel.setLayout(borderLayout6);
      otherTextArea.setToolTipText("");
      coordTextArea.setToolTipText("");
      coordTextArea.setRows(10);
      jPanel4.setLayout(borderLayout7);
      jPanel5.setLayout(borderLayout8);
      jPanel6.setLayout(borderLayout9);
      jLabel3.setToolTipText("");
      jLabel3.setText("Atomic Coordinates Format:");
      latticePanel.setLayout(gridBagLayout1);
      jLabel4.setToolTipText("");
      jLabel4.setText("Lattice Constant:");
      latticeConstantTextField.setValue(new Double(1.0));
      latticeConstantTextField.setColumns(10);
      jLabel6.setToolTipText("");
      jLabel6.setText("beta:");
      jLabel7.setToolTipText("");
      jLabel7.setText("a:");
      jLabel8.setToolTipText("");
      jLabel8.setText("b:");
      jLabel9.setToolTipText("");
      jLabel9.setText("c:");
      jLabel10.setToolTipText("");
      jLabel10.setText("gamma:");
      betaTextField.setValue(new Double(90.0));
      alphaTextField.setValue(new Double(90.0));
      cTextField.setValue(new Double(1.0));
      bTextField.setValue(new Double(1.0));
      aTextField.setValue(new Double(1.0));
      gammaTextField.setValue(new Double(90.0));
      jLabel11.setToolTipText("");
      jLabel11.setText("alpha:");
      jPanel7.setLayout(gridBagLayout2);
      speciesTextArea.setRows(5);
      jPanel7.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1), "Lattice Parameters"));
      formatComboBox.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            formatComboBox_actionPerformed(e);
         }
      });
      otherTextArea.setRows(20);
      centerMolButton.setToolTipText("Center Molecule in Cell");
      centerMolButton.setText("Center");
      centerMolButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            centerMolButton_actionPerformed(e);
         }
      });
      auxPanel.setLayout(flowLayout3);
      jPanel2.add(jLabel2);
      jPanel2.add(systemNameTextField);
      jScrollPane3.getViewport().add(otherTextArea);

      jPanel3.add(jLabel1);
      jPanel3.add(systemLabelTextField);
      jPanel1.add(jPanel2, BorderLayout.NORTH);
      jPanel1.add(jPanel3, BorderLayout.CENTER);

      mainPanel.add(jPanel4, BorderLayout.NORTH);
      jPanel4.add(jPanel5, BorderLayout.NORTH);
      speciesPanel.add(jScrollPane1, BorderLayout.CENTER);
      jPanel5.add(jPanel6, BorderLayout.NORTH);
      jScrollPane1.getViewport().add(speciesTextArea);
      mainPanel.add(coordPanel, BorderLayout.CENTER);
      jScrollPane2.getViewport().add(coordTextArea);
      this.add(jTabbedPane1, BorderLayout.CENTER);
      jTabbedPane1.add(mainPanel, "Basic Options");
      jTabbedPane1.add(otherPanel, "Other");
      otherPanel.add(jScrollPane3, BorderLayout.CENTER);
      coordPanel.add(jScrollPane2, BorderLayout.CENTER);
      jPanel5.add(speciesPanel, BorderLayout.SOUTH);
      jPanel6.add(jPanel1, BorderLayout.NORTH);
      jPanel6.add(latticePanel, BorderLayout.CENTER);
      latticePanel.add(jLabel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      latticePanel.add(jPanel7, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0
          , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(aTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(bTextField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(cTextField, new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(alphaTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(betaTextField, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(gammaTextField, new GridBagConstraints(5, 1, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(jLabel7, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

      jPanel7.add(jLabel11, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(jLabel8, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(jLabel6, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(jLabel9, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(jLabel10, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      latticePanel.add(latticeConstantTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      latticePanel.add(formatComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      latticePanel.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      jPanel7.add(auxPanel, new GridBagConstraints(0, 2, 6, 1, 1.0, 0.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
      auxPanel.add(centerMolButton);
      jTabbedPane1.setSelectedComponent(mainPanel);

      AtomicCoordinatesFormat[] formats = AtomicCoordinatesFormat.values();
      formatComboBox.removeAllItems();
      for (int i = 0; i < formats.length; i++) {
         formatComboBox.addItem(formats[i]);
      }
      atomicCoordinatesFormat = Siesta.getDefaultAtomicCoordinatesFormat();
      formatComboBox.setSelectedItem(atomicCoordinatesFormat);

      Font currentFont = speciesTextArea.getFont();
      int size = currentFont.getSize();
      int style = currentFont.getStyle();
      String fontName = currentFont.getFontName();

      try {
         prefs = Preferences.userNodeForPackage(this.getClass());
         fontName = prefs.get(FontNameKey, currentFont.getFontName());
         size = prefs.getInt(FontSizeKey, 12);
         style = prefs.getInt(FontStyleKey, 0);
      }
      catch (Exception ex) {
         //System.err.println("Error retrieving Font Preferences: " +                            ex.getMessage());
         //return;
      }

      Font newFont = new Font(fontName, style, size);
      speciesTextArea.setFont(newFont);
      coordTextArea.setFont(newFont);
      otherTextArea.setFont(newFont);

      aFormat.setMaximumFractionDigits(6);
      bFormat.setMaximumFractionDigits(6);
      cFormat.setMaximumFractionDigits(6);
      alphaFormat.setMaximumFractionDigits(6);
      betaFormat.setMaximumFractionDigits(6);
      gammaFormat.setMaximumFractionDigits(6);
   }

   public void setJava3dUniverse(Java3dUniverse j3dU) {
      j3d = j3dU;
   }

   private void setLatticeParameters(double[] latticeParameters) {
      aTextField.setValue(new Double(latticeParameters[0]));
      bTextField.setValue(new Double(latticeParameters[1]));
      cTextField.setValue(new Double(latticeParameters[2]));
      alphaTextField.setValue(new Double(latticeParameters[3]));
      betaTextField.setValue(new Double(latticeParameters[4]));
      gammaTextField.setValue(new Double(latticeParameters[5]));
      siestaInput.setLatticeParameters(latticeParameters[0], latticeParameters[1], latticeParameters[2], latticeParameters[3],
                                       latticeParameters[4], latticeParameters[5]);
   }

   public void setMolecularInterface(MoleculeInterface molec) {
      molecule = molec;
      systemNameTextField.setText(molecule.getName());

      siestaInput = new SiestaInput();
      siestaInput.setSystemName(molecule.getName());

      Object obj = molec.getProperty(MoleculeInterface.LATTICE_PARAMETERS);
      boolean no_cell_params = true;
      if (obj != null) {
         if (obj instanceof float[]) {
            float[] latticeParameters = (float[]) obj;
            aTextField.setValue(new Double(latticeParameters[0]));
            bTextField.setValue(new Double(latticeParameters[1]));
            cTextField.setValue(new Double(latticeParameters[2]));
            alphaTextField.setValue(new Double(latticeParameters[3]));
            betaTextField.setValue(new Double(latticeParameters[4]));
            gammaTextField.setValue(new Double(latticeParameters[5]));
            siestaInput.setLatticeParameters(latticeParameters[0], latticeParameters[1], latticeParameters[2], latticeParameters[3],
                                             latticeParameters[4], latticeParameters[5]);
            no_cell_params = false;
         }
         else if (obj instanceof double[]) {
            double[] latticeParameters = (double[]) obj;
            setLatticeParameters(latticeParameters);
            no_cell_params = false;
         }
         else {
            JOptionPane.showMessageDialog(this, "Unknown container(class) for lattice parameters: " +
                                          obj.getClass().getCanonicalName(), "Error", JOptionPane.ERROR_MESSAGE);
            obj = null;
         }
      }

      // --- try to work with lattice vectors

      if (obj == null) {
         obj = molec.getProperty(MoleculeInterface.LATTICE_VECTORS);
         if (obj != null) {
            double[][] latticeVectors = (double[][]) obj;
            double[] latticeParameters = Crystal.latticeParamFromLatticeVectors(latticeVectors);
            setLatticeParameters(latticeParameters);
         }
         else { // == null
            double[] latticeParameters = Crystal.getDefaultLatticeParameters(molec, 0.25);
            setLatticeParameters(latticeParameters);
            setLatticeParameters(latticeParameters);
         }
      }

      // --- Set format
      atomicCoordinatesFormat = AtomicCoordinatesFormat.NotScaledCartesianAng;
      formatComboBox.setEnabled(false);
      formatComboBox.setSelectedItem(atomicCoordinatesFormat);
      formatComboBox.setEnabled(true);
      siestaInput.setAtomicCoordinatesFormat(atomicCoordinatesFormat);

      // --- Resolve species labels

      Map<Integer, ChemicalSpeciesLabel> chemicalSpeciesLabels = null;
      SiestaInterface siesta = null;
      obj = molecule.getProperty(SiestaInterface.SIESTA_INTERFACE);
      if (obj != null) {
         if (! (obj instanceof SiestaInterface)) {
            JOptionPane.showMessageDialog(this, "Unknown container(class) for siesta interface: " +
                                          obj.getClass().getCanonicalName(), "Error", JOptionPane.ERROR_MESSAGE);
         }
         else { // use siesta info
            siesta = (SiestaInterface) obj;
            chemicalSpeciesLabels = siesta.getChemicalSpeciesLabels();
            java.util.List<String> otherOptions = siesta.getOtherOptions();
            siestaInput.setOtherOptions(otherOptions);
         }
      }
      else { // Default options
         chemicalSpeciesLabels = Siesta.formChemicalSpeciesLabels(molecule);
      }

      siestaInput.setChemicalSpeciesLabels(chemicalSpeciesLabels);

      // --- Setup species

      speciesTextArea.setText(siestaInput.getSpeciesLabelsAsString());
      speciesTextArea.setCaretPosition(0);

      // --- Setup coordinates

      coordTextArea.setText(siestaInput.getAtomicCoordinatesAsString(molecule));
      coordTextArea.setCaretPosition(0);

      // --- Setup other options

      otherTextArea.setText(siestaInput.getOtherOptionsAsString());
      otherTextArea.setCaretPosition(0);

      if (j3d != null) {
         UnitCellGraphics siestaUC = new UnitCellGraphics("Siesta Unit Cell");
         j3d.addGraphics(siestaUC.getCellGraphicsObject(siestaInput.getLatticeVectors()));
      }

   }

   public void formatComboBox_actionPerformed(ActionEvent e) {
      if (!formatComboBox.isEnabled()) {
         return;
      }

      if (molecule == null) {
         return;
      }

      AtomicCoordinatesFormat format = (AtomicCoordinatesFormat) formatComboBox.getSelectedItem();
      siestaInput.setAtomicCoordinatesFormat(format);

      switch (format) {
         case Fractional:
         case ScaledByLatticeVectors:
            if (atomicCoordinatesFormat == AtomicCoordinatesFormat.Fractional ||
                atomicCoordinatesFormat == AtomicCoordinatesFormat.ScaledByLatticeVectors) {
               return;
            }
            break;

         case Ang:
         case NotScaledCartesianAng:
            if (atomicCoordinatesFormat == AtomicCoordinatesFormat.Ang ||
                atomicCoordinatesFormat == AtomicCoordinatesFormat.NotScaledCartesianAng) {
               return;
            }
            break;

         case Bohr:
         case NotScaledCartesianBohr:
            if (atomicCoordinatesFormat == AtomicCoordinatesFormat.Bohr ||
                atomicCoordinatesFormat == AtomicCoordinatesFormat.NotScaledCartesianBohr) {
               return;
            }
            break;

      }
      coordTextArea.setText(siestaInput.getAtomicCoordinatesAsString(molecule));
      coordTextArea.setCaretPosition(0);
      atomicCoordinatesFormat = format;
   }

   public void validateData() throws Exception {
      SiestaInput siestaInput = createSiestaObject();
      updateScene(siestaInput);
   }

   private SiestaInput createSiestaObject() throws Exception {
      //if (siestaInput == null) {
      SiestaInput siesta = new SiestaInput();
      //}
      siesta.setSystemName(systemNameTextField.getText());
      siesta.setSystemLabel(systemLabelTextField.getText());

      double lC = ( (Number) latticeConstantTextField.getValue()).doubleValue();
      if (lC <= 0.0) {
         throw new Exception("Lattice Constant cannot be less than 0. Got " + lC);
      }
      siesta.setLatticeConstant(lC);

      AtomicCoordinatesFormat atomicCoordinatesFormat = (AtomicCoordinatesFormat) formatComboBox.getSelectedItem();
      siesta.setAtomicCoordinatesFormat(atomicCoordinatesFormat);

      double a = ( (Number) aTextField.getValue()).doubleValue();
      double b = ( (Number) bTextField.getValue()).doubleValue();
      double c = ( (Number) cTextField.getValue()).doubleValue();

      double alpha = ( (Number) alphaTextField.getValue()).doubleValue();
      double beta = ( (Number) betaTextField.getValue()).doubleValue();
      double gamma = ( (Number) gammaTextField.getValue()).doubleValue();

      Crystal.validateLatticeParameters(a, b, c, alpha, beta, gamma);

      siesta.setLatticeParameters(a, b, c, alpha, beta, gamma);

      siesta.parseChemicalSpeciesLabel(speciesTextArea.getText());

      siesta.parseAtomicCoordinatesAndAtomicSpecies(coordTextArea.getText());

      siesta.setOtherOptions(otherTextArea.getText());
      return siesta;
   }

   private void updateScene(SiestaInput siestaInput) {
      if (j3d == null) {
         return;
      }
      try {
         molecule = molecule.getInstance();
         siestaInput.getMolecularInterface(molecule);
         Molecule.guessCovalentBonds(molecule);
         Molecule.guessAtomTypes(molecule, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());

         j3d.setMolecule(molecule);
         UnitCellGraphics siestaUC = new UnitCellGraphics("Siesta Unit Cell");
         j3d.addGraphics(siestaUC.getCellGraphicsObject(siestaInput.getLatticeVectors()));
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Cannot render scene: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
   }

   public void saveData(boolean withEditing) throws Exception {
      if (withEditing) {
         JShowText editSave = new JShowText("SIESTA Input Data");
         editSave.setDefaultFileName(defaultFileName);
         editSave.setActionButton(JShowText.QUIT_WITH_SAVING);
         editSave.enableTextEditing(false);
         editSave.setLocationRelativeTo(this);
         editSave.setText(getInputAsString());
         editSave.pack();
         editSave.setSize(600, 640);
         editSave.setVisible(true);
      }
      else {
         String text = getInputAsString();
         FileDialog fd = new FileDialog(new Frame(), "Save File", FileDialog.SAVE);
         fd.setFile(defaultFileName);
         fd.setVisible(true);
         if (fd.getFile() != null) {
            String fileName = fd.getFile();
            String name = FileUtilities.getFileName(fileName);
            if (name.indexOf(".") == -1) {
               fileName += ".fdf";
            }

            String workingDirectory = fd.getDirectory();
            try {
               IOUtils.saveStringIntoFile(text, workingDirectory + fileName);
            }
            catch (Exception ex) {
               throw new Exception("Error Saving file: " + ex.getMessage());
            }
         }
      }
   }

   public String getInputAsString() throws Exception {
      if (siestaInput == null) {
         siestaInput = new SiestaInput();
      }
      return siestaInput.getInputAsString();
   }

   public void centerMolButton_actionPerformed(ActionEvent e) {
      try {
         siestaInput = createSiestaObject();
         siestaInput.centerContentsInCell();
         coordTextArea.setText(siestaInput.getAtomicCoordinatesAsString());
         coordTextArea.setCaretPosition(0);
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Error in data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
      if (j3d == null) {
         return;
      }
      updateScene(siestaInput);
   }
}
