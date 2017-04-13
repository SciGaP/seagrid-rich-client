/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dialogs;

import cct.GlobalSettings;
import cct.dynamics.Dynamics;
import cct.interfaces.ForceFieldInterface;
import cct.interfaces.JamberooCoreInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.ParentInterface;
import cct.j3d.Java3dUniverse;
import cct.math.MinimizerInterface;
import cct.modelling.BackgroundMinimizer;
import cct.modelling.FFMolecule;
import cct.modelling.MinimizeStructure;
import cct.modelling.MolecularEnergy;
import cct.modelling.ui.JSetupEnergyDialog;
import cct.sff.SimpleForceField;
import cct.tools.ui.JShowText;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author Sap
 */
public class CalculateMenu extends JMenu implements ActionListener, ParentInterface {

  static final Logger logger = Logger.getLogger(CalculateMenu.class.getCanonicalName());
  private JamberooCoreInterface jamberooCore;
  private Java3dUniverse java3dUniverse;
  private JMenuItem jMenuCalcEnergy = new JMenuItem("Energy");
  private JMenuItem jMenuMinEnergy2 = new JMenuItem("Minimize Energy");
  private JMenuItem jMenuMinEnergy = new JMenuItem("Minimize Energy Simplex");
  private JMenuItem jMenuMD = new JMenuItem("Molecular Dynamics");
  private JSetupEnergyDialog jSetupEnergyDialog = null;
  private Component parentComponent = null;
  private MinimizeStructure mimimizeStructure = null;
  private BackgroundMinimizer backgroundMinimizer = null;
  private static String LABEL_ENERGY_SETUP_DIALOG = CalculateMenu.class.getCanonicalName() + "/EnergySetupDialog";

  public Component getParentComponent() {
    return parentComponent;
  }

  public void setParentComponent(Component parentComponent) {
    this.parentComponent = parentComponent;
  }

  public CalculateMenu(JamberooCoreInterface core) {
    super();
    jamberooCore = core;
    java3dUniverse = jamberooCore.getJamberooRenderer();

  }

  public void createMenu() {

    jMenuCalcEnergy.addActionListener(this);
    add(jMenuCalcEnergy);

    jMenuMinEnergy2.addActionListener(this);
    add(jMenuMinEnergy2);

    jMenuMinEnergy.addActionListener(this);
    jMenuMinEnergy.setEnabled(false);
    add(jMenuMinEnergy);
  }

  public void actionPerformed(ActionEvent actionEvent) {
    // --- Calculating Energy


    if (actionEvent.getSource() == this.jMenuCalcEnergy && java3dUniverse.getMolecule() != null) {

      //if (jSetupEnergyDialog == null) {
      if (GlobalSettings.getRegisterDialog(LABEL_ENERGY_SETUP_DIALOG) == null) {
        jSetupEnergyDialog = new JSetupEnergyDialog(getParentFrame(), "Energy Setup", true);
        GlobalSettings.registerDialog(LABEL_ENERGY_SETUP_DIALOG, jSetupEnergyDialog);
        ForceFieldInterface ff[] = new ForceFieldInterface[1];
        ff[0] = new SimpleForceField();
        jSetupEnergyDialog.setForceFields(ff);
        jSetupEnergyDialog.setLocationRelativeTo(this);
      } else {
        JDialog dial = (JDialog) GlobalSettings.getRegisterDialog(LABEL_ENERGY_SETUP_DIALOG);
        jSetupEnergyDialog = (JSetupEnergyDialog) dial;
      }

      jSetupEnergyDialog.setTitle("Energy Setup");

      jSetupEnergyDialog.energySetupMode();
      jSetupEnergyDialog.setVisible(true);
      if (!jSetupEnergyDialog.isOkPressed()) {
        return;
      }

      ForceFieldInterface selectedFF = jSetupEnergyDialog.getSelectedFF();

      FFMolecule ffMol = new FFMolecule(java3dUniverse.getMolecule());
      ffMol.applyForceField(selectedFF);

      try {
        ffMol.formFFParameters();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error calculating Energy", JOptionPane.ERROR_MESSAGE);
        return;
      }

      MolecularEnergy en = new MolecularEnergy(ffMol);
      try {
        double energy = en.calculateEnergy(false);
        logger.info("Total Energy: " + energy);
        en.printEnergyDecomposition();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error calculating Energy", JOptionPane.ERROR_MESSAGE);
        return;
      }
      JShowText show = new JShowText("Energy Decomposition");
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream store = System.out;
      System.setOut(new PrintStream(out));
      en.printEnergyDecomposition();
      show.setText(out);
      show.setSize(500, 300);
      show.setLocationRelativeTo(this);
      show.setVisible(true);
      System.setOut(store);
    } // --- Minimizing Energy
    else if (actionEvent.getSource() == this.jMenuMinEnergy && java3dUniverse.getMolecule() != null) {

      MinimizeStructure ms = new MinimizeStructure(java3dUniverse.getMolecule());
      ms.setForceField(jSetupEnergyDialog.getSelectedFF());
      try {
        ms.minimize(0);
        java3dUniverse.addMolecule(ms.getMolecule());
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error minimizing Energy", JOptionPane.ERROR_MESSAGE);
        return;
      }

    } else if (actionEvent.getSource() == this.jMenuMinEnergy2 && java3dUniverse.getMolecule() != null) {
      if (GlobalSettings.getRegisterDialog(LABEL_ENERGY_SETUP_DIALOG) == null) {
        jSetupEnergyDialog = new JSetupEnergyDialog(getParentFrame(), "Minimization Setup", true);
        GlobalSettings.registerDialog(LABEL_ENERGY_SETUP_DIALOG, jSetupEnergyDialog);
        ForceFieldInterface ff[] = new ForceFieldInterface[1];
        ff[0] = new SimpleForceField();
        jSetupEnergyDialog.setForceFields(ff);
        jSetupEnergyDialog.setLocationRelativeTo(this);
      } else {
        JDialog dial = (JDialog) GlobalSettings.getRegisterDialog(LABEL_ENERGY_SETUP_DIALOG);
        jSetupEnergyDialog = (JSetupEnergyDialog) dial;
      }

      if (!jSetupEnergyDialog.areMinimizersSet()) {
        MinimizerInterface mins[] = new MinimizerInterface[1];
        mins[0] = new cct.math.DFPMin();
        jSetupEnergyDialog.setMinimizers(mins);
      }
      jSetupEnergyDialog.minimizationSetupMode();
      jSetupEnergyDialog.setTitle("Minimization Setup");
      jSetupEnergyDialog.setVisible(true);
      if (!jSetupEnergyDialog.isOkPressed()) {
        return;
      }

      MinimizerInterface min = jSetupEnergyDialog.getSelectedMinimizer();

      mimimizeStructure = new MinimizeStructure(java3dUniverse.getMolecule());
      mimimizeStructure.setForceField(jSetupEnergyDialog.getSelectedFF());
      mimimizeStructure.setMinimizer(min);

      backgroundMinimizer = new BackgroundMinimizer(jMenuMinEnergy2, this, mimimizeStructure);
      backgroundMinimizer.setVariablesFrequencyUpdate(jSetupEnergyDialog.getScreenRefreshRate());
      backgroundMinimizer.setVisualizer(java3dUniverse);

      mimimizeStructure.setMinimizeProgressInterface(backgroundMinimizer);
      jMenuMinEnergy2.setEnabled(false);
      backgroundMinimizer.start();

    } // Molecular Dynamics
    else if (actionEvent.getSource() == this.jMenuMD && java3dUniverse.getMolecule() != null) {
      /*
      if (GlobalSettings.getRegisterDialog(LABEL_ENERGY_SETUP_DIALOG) == null) {
        jSetupEnergyDialog = new JSetupEnergyDialog(getParentFrame(), "Minimization Setup", true);
        GlobalSettings.registerDialog(LABEL_ENERGY_SETUP_DIALOG, jSetupEnergyDialog);
        ForceFieldInterface ff[] = new ForceFieldInterface[1];
        ff[0] = new SimpleForceField();
        jSetupEnergyDialog.setForceFields(ff);
        jSetupEnergyDialog.setLocationRelativeTo(this);
      } else {
        JDialog dial = (JDialog) GlobalSettings.getRegisterDialog(LABEL_ENERGY_SETUP_DIALOG);
        jSetupEnergyDialog = (JSetupEnergyDialog) dial;
      }
      */

      /*
      if (!jSetupEnergyDialog.areMinimizersSet()) {
        MinimizerInterface mins[] = new MinimizerInterface[1];
        mins[0] = new cct.math.DFPMin();
        jSetupEnergyDialog.setMinimizers(mins);
      }
      jSetupEnergyDialog.minimizationSetupMode();
      jSetupEnergyDialog.setTitle("Minimization Setup");
      jSetupEnergyDialog.setVisible(true);
      if (!jSetupEnergyDialog.isOkPressed()) {
        return;
      }
      */

      
      Dynamics dynamics = new Dynamics(java3dUniverse.getMolecule());
      
      MinimizerInterface min = jSetupEnergyDialog.getSelectedMinimizer();

      mimimizeStructure = new MinimizeStructure(java3dUniverse.getMolecule());
      mimimizeStructure.setForceField(jSetupEnergyDialog.getSelectedFF());
      mimimizeStructure.setMinimizer(min);

      backgroundMinimizer = new BackgroundMinimizer(jMenuMinEnergy2, this, mimimizeStructure);
      backgroundMinimizer.setVariablesFrequencyUpdate(jSetupEnergyDialog.getScreenRefreshRate());
      backgroundMinimizer.setVisualizer(java3dUniverse);

      mimimizeStructure.setMinimizeProgressInterface(backgroundMinimizer);
      jMenuMinEnergy2.setEnabled(false);
      backgroundMinimizer.start();

    }
  }

  public void childFinished(Object child) {
    if (child instanceof JMenuItem && child == jMenuMinEnergy2) {
      jMenuMinEnergy2.setEnabled(true);
      final MoleculeInterface mol = backgroundMinimizer.getMolecule();
      if (mol != null) {
        java3dUniverse.updateMolecularGeometry();
      }
    }
  }

  public Frame getParentFrame() {
    if (parentComponent != null && parentComponent instanceof Frame) {
      return (Frame) parentComponent;
    }
    return new Frame();
  }
}
