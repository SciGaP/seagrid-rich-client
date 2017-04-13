/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.interfaces;

import javax.swing.JComponent;
import javax.swing.JDialog;

import cct.j3d.Java3dUniverse;
import cct.tools.filebrowser.ShadowSFTPManager;

/**
 *
 * @author vvv900
 */
public interface JamberooCoreInterface {

  HelperInterface getHelper();

  void setHelper(HelperInterface helper);

  Java3dUniverse getJamberooRenderer();
  ShadowSFTPManager getShadowSFTPManager();

  void openAtomSelectionDialog(Java3dUniverse j3d, int jobType, int mode, boolean select_and_process);
  void openAtomSelectionDialog(Java3dUniverse j3d, int jobType, int mode, boolean select_and_process, JComponent component);
  void openAtomSelectionDialog(Java3dUniverse j3d, int jobType, int mode, boolean select_and_process, JDialog nextDialog);

  void showGraphicsObjectsDialog(boolean visible);

  void updateGraphicsObjectsDialog();
}
