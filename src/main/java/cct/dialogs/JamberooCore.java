/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dialogs;

import cct.GlobalSettings;
import cct.interfaces.GraphicsObjectInterface;
import cct.interfaces.HelperInterface;
import cct.interfaces.JamberooCoreInterface;
import cct.j3d.Java3dUniverse;
import cct.j3d.ui.GraphicsObjectPropertiesFrame;
import cct.tools.filebrowser.ShadowSFTPManager;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author vvv900
 */
public class JamberooCore implements JamberooCoreInterface {

  static private HelperInterface helper = null;
  private Java3dUniverse java3dUniverse = new Java3dUniverse();
  private GraphicsObjectPropertiesFrame gopDialog = null;
  private AtomSelectionDialog jAtomSelectionDialog = null;
  private ShadowSFTPManager shadowSFTPManager = null;
  private Component componet = null;

  public Component getComponet() {
    return componet;
  }

  public void setComponet(Component componet) {
    this.componet = componet;
  }

  public ShadowSFTPManager getShadowSFTPManager() {
    if (shadowSFTPManager == null) {
      shadowSFTPManager = new ShadowSFTPManager();
      shadowSFTPManager.setExitOnAllClose(false);
    }
    return shadowSFTPManager;
  }

  public Java3dUniverse getJamberooRenderer() {
    return java3dUniverse;
  }

  public HelperInterface getHelper() {
    return helper;
  }

  public void setHelper(HelperInterface helper) {
    JamberooCore.helper = helper;
  }

  public void updateGraphicsObjectsDialog() {
    if (gopDialog != null) {
      gopDialog.updateTree();
    }
  }

  public void showGraphicsObjectsDialog(boolean visible) {
    if (gopDialog == null) {
      gopDialog = new GraphicsObjectPropertiesFrame();
      gopDialog.setIconImage(GlobalSettings.ICON_16x16_JAVA_FILE.getImage());
      if (componet != null) {
        gopDialog.setLocationRelativeTo(componet);
      }
    }
    if (helper != null) {
      JButton h = gopDialog.getHelpButton();
      h.setEnabled(true);
      helper.setHelpIDString(h, HelperInterface.JMD_MANAGE_GRAPHICS_OBJECTS_ID);
      helper.displayHelpFromSourceActionListener(h);
    }
    java.util.List<GraphicsObjectInterface> gops = java3dUniverse.getGraphicsObjects();
    gopDialog.setTree(gops);
    gopDialog.setVisible(visible);
  }

  public boolean setupAtomSelectionDialog(Java3dUniverse j3d, int jobType, int mode,
          boolean select_and_process) {

    // --- Common for all selections

    if (j3d.getMoleculeInterface() == null) {
      JOptionPane.showMessageDialog(null, "Load Molecule first!", "Warning", JOptionPane.WARNING_MESSAGE);
      return false;
    }

    if (j3d.getMousePickingStatus()) {
      JOptionPane.showMessageDialog(null, "Another Selection is already in progress!", "Error",
              JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (jAtomSelectionDialog == null) {
      jAtomSelectionDialog = new AtomSelectionDialog(null, "Atom Selection", false);
      jAtomSelectionDialog.setTargetClass(j3d);
      //jAtomSelectionDialog.setLocationRelativeTo();
      if (helper != null) {
        JButton h = jAtomSelectionDialog.getHelpButton();
        h.setEnabled(true);
        helper.setHelpIDString(h, HelperInterface.JMD_SELECT_ATOMS_ID);
        helper.displayHelpFromSourceActionListener(h);
      }
    }

    jAtomSelectionDialog.setNextDialog(null);

    j3d.enableMousePicking(true);
    j3d.selectedMode = mode;
    j3d.setJobType(jobType);
    j3d.selectAndProcessDialog = select_and_process;

    return true;
  }

  public void openAtomSelectionDialog(Java3dUniverse j3d, int jobType,
          int mode, boolean select_and_process, JDialog nextDialog) {

    if (!setupAtomSelectionDialog(j3d, jobType, mode, select_and_process)) {
      return;
    }

    if (nextDialog != null) {
      jAtomSelectionDialog.setNextDialog(nextDialog);
      //nextDialog.setLocationRelativeTo(this);
    }

    jAtomSelectionDialog.setVisible(true);
  }

  public void openAtomSelectionDialog(Java3dUniverse j3d, int jobType,
          int mode, boolean select_and_process) {

    if (!setupAtomSelectionDialog(j3d, jobType, mode, select_and_process)) {
      return;
    }

    jAtomSelectionDialog.setVisible(true);
  }

  public void openAtomSelectionDialog(Java3dUniverse j3d, int jobType, int mode, boolean select_and_process, JComponent component) {
    if (!setupAtomSelectionDialog(j3d, jobType, mode, select_and_process)) {
      return;
    }

    if (component != null) {
      jAtomSelectionDialog.addUserComponent(component);
    }

    jAtomSelectionDialog.setVisible(true);
  }
}
