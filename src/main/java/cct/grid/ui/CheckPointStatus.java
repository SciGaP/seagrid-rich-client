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
package cct.grid.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import cct.globus.CoGFileTransfer;
import cct.grid.CheckPoint;
import cct.grid.CheckPointInterface;
import cct.grid.FileTransferInterface;
import cct.grid.JobStatusInterface;
import cct.grid.ParseCheckpointFile;
import cct.grid.TaskProvider;
import cct.tools.ui.JobProgressInterface;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class CheckPointStatus
    implements JobStatusInterface, FileTransferInterface {

  List Tasks = null;
  private JobProgressInterface jpi;

  public CheckPointStatus() {
  }

  @Override
  public String[] getColumnNames() {
    return new String[]{
          "Job Name", "Program ", "Computer", "Job Status", "Output Downloaded"};
  }

  @Override
  public void setJobProgressInterface(JobProgressInterface jpi) {
    this.jpi = jpi;
  }

  @Override
  public Object[][] getData() {

    ParseCheckpointFile pchk = new ParseCheckpointFile();
    try {
      pchk.getJobStatuses();
    } catch (cct.grid.MalformedCheckPointException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Job Status Query Error", JOptionPane.ERROR_MESSAGE);
      return null;
    }

    Tasks = pchk.getTasks();

    Object[][] data = new Object[Tasks.size()][];

    for (int i = 0; i < Tasks.size(); i++) {
      CheckPoint chk = (CheckPoint) Tasks.get(i);
      data[i] = new Object[5];

      data[i][0] = chk.getJobName(); // Job Name
      if (data[i][0] == null) {
        data[i][0] = "Unknown";
      }

      data[i][1] = chk.getProgram(); //
      if (data[i][1] == null) {
        data[i][1] = "Unknown";
      }

      data[i][2] = chk.getComputer(); // Computer
      if (data[i][2] == null) {
        data[i][2] = "Unknown";
      }

      data[i][3] = chk.getJobStatus(); // Job Status as a String
      if (data[i][3] == null) {
        data[i][3] = "Unknown";
      }

      data[i][4] = chk.getOverallJobStatus();
      if (data[i][4] == null) {
        data[i][4] = "Unknown";
      }

    }

    return data;
  }

  @Override
  public CheckPoint getCheckPoint(int n) {
    if (Tasks != null && Tasks.size() > 0 && n >= 0 && n < Tasks.size()) {
      return (CheckPoint) Tasks.get(n);
    }
    return null;
  }

  @Override
  public void killSelectedJobs(int[] jobsToKill) {
    if (Tasks == null || Tasks.size() < 1) {
      return;
    }

    for (int i = 0; i < jobsToKill.length; i++) {
      if (jobsToKill[i] < 0 || jobsToKill[i] >= Tasks.size()) {
        // Just ingnore malformed requests
        jobsToKill[i] = -1;
        continue;
      }
      CheckPoint chk = (CheckPoint) Tasks.get(jobsToKill[i]);
      if (chk.getJobStatus().equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)) {
        jobsToKill[i] = -1;
      }
    }

    ParseCheckpointFile pchk = new ParseCheckpointFile();
    try {
      pchk.getJobStatuses();
    } catch (cct.grid.MalformedCheckPointException ex) {
      JOptionPane.showMessageDialog(null,
          ex.getMessage(),
          "Job Status Query Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    List newTasks = pchk.getTasks();
    String errors = "";

    for (int i = 0; i < jobsToKill.length; i++) {
      if (jobsToKill[i] < 0) {
        continue;
      }
      CheckPoint chk = (CheckPoint) Tasks.get(jobsToKill[i]);
      String handle = chk.getJobHandle();

      for (int j = 0; j < newTasks.size(); j++) {
        CheckPoint new_chk = (CheckPoint) newTasks.get(j);
        String new_handle = new_chk.getJobHandle();
        if (new_handle.equals(handle)) {
          if (!new_chk.getJobStatus().equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)) {

            try {
              TaskProvider.killJob(new_chk);
            } catch (Exception ex) {
              errors += ex.getMessage();
            }
            /*
            if (GRAM.killJob(handle) != 0) {
            errors += handle + " : " + GRAM.getMessage() + "\n";
            }
             */
          }
          break;
        }
      }
    }

    if (errors.length() > 0) {
      JOptionPane.showMessageDialog(null,
          errors,
          "Notice",
          JOptionPane.WARNING_MESSAGE);
    }

  }

  @Override
  public void removeSelectedCheckpoints(int[] jobsToRemove) {
    if (Tasks == null || Tasks.size() < 1) {
      return;
    }

    boolean warning = false;
    for (int i = 0; i < jobsToRemove.length; i++) {
      if (jobsToRemove[i] < 0 || jobsToRemove[i] >= Tasks.size()) {
        // Just ingnore malformed requests
        jobsToRemove[i] = -1;
        continue;
      }
      CheckPoint chk = (CheckPoint) Tasks.get(jobsToRemove[i]);
      if (chk.getJobStatus().equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)
          && chk.getOverallJobStatus().equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)) {
        continue;
      }
      warning = true;
    }

    if (warning) {
      final JOptionPane optionPane = new JOptionPane(
          "Some jobs are not completed yet\n"
          + "Do you still want to delete their checkpoints?",
          JOptionPane.QUESTION_MESSAGE,
          JOptionPane.YES_NO_OPTION);
      optionPane.setVisible(true);
      int n =
          JOptionPane.showConfirmDialog(null,
          "Some jobs are not completed yet\n"
          + "Do you still want to delete their checkpoints?",
          "Confirm Removal",
          JOptionPane.YES_NO_OPTION);
      if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
        return;
      }
    }

    ParseCheckpointFile pchk = new ParseCheckpointFile();
    try {
      pchk.parseCheckpointFile();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
          "Error parsing checkpoint file: "
          + ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    List newTasks = pchk.getTasks();

    for (int i = 0; i < jobsToRemove.length; i++) {
      if (jobsToRemove[i] < 0) {
        continue;
      }
      CheckPoint chk = (CheckPoint) Tasks.get(jobsToRemove[i]);
      String handle = chk.getJobHandle();

      int to_remove = -1;
      for (int j = 0; j < newTasks.size(); j++) {
        CheckPoint new_chk = (CheckPoint) newTasks.get(j);
        String new_handle = new_chk.getJobHandle();
        if (new_handle.equals(handle)) {
          to_remove = j;
          break;
        }
      }

      if (to_remove > -1) {
        newTasks.remove(to_remove);
      }
    }

    Tasks = newTasks;
    try {
      ParseCheckpointFile.saveCheckPointFile(Tasks);
    } catch (cct.grid.MalformedCheckPointException ex) {
      JOptionPane.showMessageDialog(null,
          "Error while saving updated checkpoint file: "
          + ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  @Override
  public void downloadRemoteFiles(int[] jobsWithDownloads) {
    if (Tasks == null || Tasks.size() < 1) {
      return;
    }

    // --- Check whether all output files are already downloaded

    for (int i = 0; i < jobsWithDownloads.length; i++) {
      if (jobsWithDownloads[i] < 0 || jobsWithDownloads[i] >= Tasks.size()) {
        // Just ingnore malformed requests
        jobsWithDownloads[i] = -1;
        continue;
      }

      CheckPoint chk = (CheckPoint) Tasks.get(jobsWithDownloads[i]);

      if (chk.getJobStatus().equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)
          && chk.getOverallJobStatus().equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)) {
        jobsWithDownloads[i] = -1;
        continue;
      }

    }

    boolean warning = true;
    for (int i = 0; i < jobsWithDownloads.length; i++) {
      if (jobsWithDownloads[i] > -1) {
        warning = false;
        break;
      }
    }

    // --- if they are already downloaded - return

    if (warning) {
      JOptionPane.showMessageDialog(null, "The output files for selected job(s) are/is already downloaded", "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    // --- Get updated job status and download output files (if needed)

    String message = "";
    warning = false;
    boolean updateCheckpoint = false;
    for (int i = 0; i < jobsWithDownloads.length; i++) {
      if (jobsWithDownloads[i] == -1) {
        continue;
      }

      CheckPoint chk = (CheckPoint) Tasks.get(jobsWithDownloads[i]);
      try {
        chk.updateJobStatus();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error Quering Job Statuses: " + e.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      // --- Ignore jobs which are not completed yet
      if (!chk.getJobStatus().equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)) {
        warning = true;
        continue;
      }
      if (warning) {
        message += "Some jobs are not completed yet\n";
      }

      if (chk.getComputer() == null) {
        message += "Uknown computer for job handle " + chk.getJobHandle()
            + "\n";
      }

      // Now try to download files
      try {
        chk.downloadOutputFiles(this);
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
        message += ex.getMessage();
      }

      if (chk.isOutputFilesStatusUpdated()) {
        updateCheckpoint = true;
      }
    }

    // --- If nothing to update - return

    if (!updateCheckpoint) {
      if (message.length() > 0) {
        JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
      }
      return;
    }

    // --- Update checkpoint file

    // --- Collect checkpoint for update

    ArrayList tasksToUpdate = new ArrayList();
    for (int i = 0; i < Tasks.size(); i++) {
      CheckPoint chk = (CheckPoint) Tasks.get(i);
      if (!chk.isOutputFilesStatusUpdated()) {
        continue;
      }
      tasksToUpdate.add(chk);
    }

    // --- Update them

    ParseCheckpointFile pchk = new ParseCheckpointFile();
    try {
      pchk.updateSelectedCheckPoints(Tasks);
    } catch (Exception ex) {
      message += ex.getMessage();
    }

    if (message.length() > 0) {
      JOptionPane.showMessageDialog(null,
          message,
          "Warning",
          JOptionPane.WARNING_MESSAGE);
    }
  }

  @Override
  public boolean tranferFile(String source, String destination, CheckPoint chkp) {
    try {
      return TaskProvider.transferFile(source, destination, chkp);
    } catch (Exception ex) {
      System.err.println(this.getClass().getCanonicalName() + " : transferFile: " + ex.getMessage());
      return false;
    }
  }

  static class FTransferWithProgress
      implements ThreadInterface {

    String Source, Destination;

    public FTransferWithProgress(String source, String destination) {
      Source = source;
      Destination = destination;
    }

    @Override
    public void performTask() {
      CoGFileTransfer transfer = new CoGFileTransfer();
    }
  }
}
