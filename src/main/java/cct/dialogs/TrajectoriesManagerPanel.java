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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import cct.interfaces.MoleculeInterface;
import cct.modelling.MolecularFileFormats;
import cct.modelling.StructureManager;
import cct.modelling.TRAJECTORY_FILE_FORMAT;
import cct.modelling.TrajectoryClientInterface;
import cct.tools.FileFilterImpl;
import cct.tools.ui.TaskProgessDialog;
import java.awt.Container;
import java.awt.Cursor;
import javax.accessibility.AccessibleContext;
import javax.swing.JComponent;
import javax.swing.SwingWorker;

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
public class TrajectoriesManagerPanel
    extends JPanel {

  private static String lastPWDKey = "lastPWD";

  enum JOB_TYPE {

    LOAD_TRAJECTORY
  }

  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JPanel jPanel1 = new JPanel();
  protected JPanel jPanel2 = new JPanel();
  protected JScrollPane jScrollPane1 = new JScrollPane();
  protected JList trajList = new JList();
  protected JButton loadButton = new JButton();
  protected JButton deleteButton = new JButton();
  protected JButton helpButton = new JButton();

  protected JFileChooser fileChooser = null;
  protected Preferences prefs = Preferences.userNodeForPackage(getClass());
  protected File currentWorkingDirectory = null;

  protected StructureManager structureManager = null;
  protected Map<String, TRAJECTORY_FILE_FORMAT> validFormats = new LinkedHashMap<String, TRAJECTORY_FILE_FORMAT>();
  protected Map loadedTrajectories = new LinkedHashMap();
  protected TrajectoryClientInterface trajectoryClientInterface = null;
  protected JButton delAllButton = new JButton();
  private TaskProgessDialog taskProgessDialog = null;
  private TRAJECTORY_FILE_FORMAT format;
  private String fileName;
  static final Logger logger = Logger.getLogger(TrajectoriesManagerPanel.class.getCanonicalName());

  public TrajectoriesManagerPanel(MoleculeInterface referenceStructure) {
    structureManager = new StructureManager(referenceStructure);
    try {
      jbInit();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public TrajectoriesManagerPanel() {
    try {
      jbInit();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void setTrajectoryClientInterface(TrajectoryClientInterface traj) {
    trajectoryClientInterface = traj;
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    loadButton.setToolTipText("Load New Trajectory");
    loadButton.setText(" Load ");
    loadButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadButton_actionPerformed(e);
      }
    });
    deleteButton.setToolTipText("Delete selected Trajectories from the List");
    deleteButton.setText("Delete Selected");
    helpButton.setToolTipText("");
    helpButton.setText(" Help ");
    delAllButton.setToolTipText("Delete selected Trajectories from the List");
    delAllButton.setText("Delete All");
    delAllButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        delAllButton_actionPerformed(e);
      }
    });
    jPanel2.add(jScrollPane1);
    jScrollPane1.getViewport().add(trajList);
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(loadButton);
    jPanel1.add(deleteButton);
    jPanel1.add(delAllButton);
    jPanel1.add(helpButton);
    this.add(jPanel2, BorderLayout.NORTH);
  }

  public void loadButton_actionPerformed(ActionEvent e) {

    if (fileChooser == null) {
      fileChooser = new JFileChooser();
      FileFilterImpl[] filter = MolecularFileFormats.formTrajectoryFileFilters();
      validFormats = MolecularFileFormats.formTrajectoryFileRef();
      for (int i = 0; i < filter.length; i++) {
        fileChooser.setFileFilter(filter[i]);
      }
      fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
      fileChooser.setDialogTitle("Open Trajectory file");
      fileChooser.setAcceptAllFileFilterUsed(false);

      if (currentWorkingDirectory != null) {
        fileChooser.setCurrentDirectory(currentWorkingDirectory);
      } else {
        String lastPWD = prefs.get(lastPWDKey, "");
        if (lastPWD.length() > 0) {
          currentWorkingDirectory = new File(lastPWD);
          if (currentWorkingDirectory.isDirectory()
              && currentWorkingDirectory.exists()) {
            fileChooser.setCurrentDirectory(currentWorkingDirectory);
          }
        }
      }

    }

    int returnVal = JFileChooser.CANCEL_OPTION;

    returnVal = fileChooser.showOpenDialog(this);

    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }

    fileName = fileChooser.getSelectedFile().getPath();
    currentWorkingDirectory = fileChooser.getCurrentDirectory();
    try {
      prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
    } catch (Exception ex) {
      System.err.println("Cannot save cwd: " + ex.getMessage() + " Ignored...");
    }
    logger.info("You chose to open a file: " + fileName);

    FileFilter ff = fileChooser.getFileFilter();

    format = validFormats.get(ff.getDescription());
    if (structureManager == null) {
      JOptionPane.showMessageDialog(this, "Structure Manager is not set", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    initTaskProgressDialog();
    PerformTrajectoryTask task
        = new PerformTrajectoryTask(this, taskProgessDialog, JOB_TYPE.LOAD_TRAJECTORY);
    task.execute();

    /*
     try {
     structureManager.parseTrajectoryFile(fileName, format);
     } catch (Exception ex) {
     JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
     return;
     }
     
     String[] files = structureManager.getAvailableTrajectories();
     trajList.removeAll();
     if (files != null) {
     trajList.setListData(files);
     }

     if (trajectoryClientInterface != null) {
     trajectoryClientInterface.setSnapshotsCount(structureManager.getSnapshotsCount());
     }
     */
  }

  public void initTaskProgressDialog() {
    if (taskProgessDialog == null) {
      java.awt.Frame parentFrame = null;
      Object parentObj = null;
      Container cont = this.getParent();
      while ((parentObj = cont) != null) {
        if (parentObj instanceof java.awt.Frame) {
          parentFrame = (java.awt.Frame) parentObj;
          break;
        }
        cont = ((Container) parentObj).getParent();
      }
      taskProgessDialog = new TaskProgessDialog(parentFrame, false);
      taskProgessDialog.setLocationRelativeTo(this);
      taskProgessDialog.showHelpButton(false);
      taskProgessDialog.setAlwaysOnTop(true);
    } else {
      taskProgessDialog.setProgress(0); // Reset progress bar
    }
  }

  public StructureManager getStructureManager() {
    return structureManager;
  }

  public void delAllButton_actionPerformed(ActionEvent e) {
    this.deleteAlltrajectories();
  }

  public void deleteAlltrajectories() {
    String[] files = new String[0];
    trajList.removeAll();
    trajList.setListData(files);
    structureManager.deleteAllTrajectories();
    if (trajectoryClientInterface != null) {
      trajectoryClientInterface.setSnapshotsCount(structureManager.getSnapshotsCount());
    }
    this.validate();
  }

  public void setReferenceMolecule(MoleculeInterface mol) {
    if (structureManager == null) {
      structureManager = new StructureManager(mol);
    } else {
      structureManager.setReferenceMolecule(mol);
    }
  }

  class PerformTrajectoryTask extends SwingWorker<Void, Integer> {

    JComponent jComponent;
    TaskProgessDialog progress;
    boolean done = false;
    JOB_TYPE job;

    PerformTrajectoryTask(JComponent jComponent, TaskProgessDialog progress, JOB_TYPE job) {
      //initialize
      this.progress = progress;
      this.job = job;
      this.jComponent = jComponent;

      AccessibleContext accessibleContext = jComponent.getAccessibleContext();

    }

    @Override
    public Void doInBackground() {
      jComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      jComponent.setEnabled(false);
      progress.setCanceled(false);
      progress.setVisible(true);
      try {
        switch (job) {
          case LOAD_TRAJECTORY:
            try {
              structureManager.parseTrajectoryFile(fileName, format, progress);
            } catch (Exception ex) {
              JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            String[] files = structureManager.getAvailableTrajectories();
            trajList.removeAll();
            if (files != null) {
              trajList.setListData(files);
            }

            if (trajectoryClientInterface != null) {
              trajectoryClientInterface.setSnapshotsCount(structureManager.getSnapshotsCount());
            }
            progress.setProgress(0);
            if (progress.isCanceled()) {
              JOptionPane.showMessageDialog(null, "Trajectory loading was cancelled. Not all snapshots might be loaded.", 
                  "Warning", JOptionPane.WARNING_MESSAGE);
            }
            return null;
          default:
            JOptionPane.showMessageDialog(null, "Option " + job.toString() + " is not implemented yet", "Error", JOptionPane.ERROR_MESSAGE);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      return null;
    }

    @Override
    public void done() {
      //parentDialog.setEnabled(true);
      jComponent.setEnabled(true);
      jComponent.setCursor(null);
      progress.setVisible(false);
      progress.setCanceled(false);
      done = true;
    }
  }

}
