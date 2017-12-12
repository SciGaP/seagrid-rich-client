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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import cct.globus.GRAM;
import cct.grid.FileViewerInterface;
import cct.grid.JobStatusInterface;
import cct.tools.ui.JobProgressInterface;

/**
 * <p>
 * Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>
 * Description: Computational Chemistry Toolkit</p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class JobStatusDialog
    extends JDialog {

  public final static int JSD_NO_TASK = 0;
  public final static int JSD_QUERY_STATUS = 1;
  public final static int JSD_KILL_JOBS = 2;
  public final static int JSD_DOWNLOAD_JOBS = 3;
  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JobStatusPanel jobStatusPanel1 = new JobStatusPanel();
  JPanel jPanel1 = new JPanel();
  JButton updateButton = new JButton();
  JButton hideButton = new JButton();
  boolean queryIsDone = true;
  int taskType = JSD_NO_TASK;
  JobStatusInterface jobStatusInterface = null;
  JButton removeButton = new JButton();
  JButton killButton = new JButton();
  JButton HelpButton = new JButton();
  JButton downloadButton = new JButton();

  public JobStatusDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    try {
      setDefaultCloseOperation(HIDE_ON_CLOSE);
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JobStatusDialog(Frame owner, String title, boolean modal, JobStatusInterface jsi) {
    super(owner, title, modal);
    jobStatusInterface = jsi;
    try {
      setDefaultCloseOperation(HIDE_ON_CLOSE);
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JobStatusDialog() {
    this(new Frame(), "Query Job Statuses", false);
  }

  private void jbInit() throws Exception {
    mainPanel.setLayout(borderLayout1);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Job Status");
    updateButton.setToolTipText("Update Job Status");
    updateButton.setText("Update");
    updateButton.addActionListener(new JobStatusDialog_updateButton_actionAdapter(this));
    hideButton.setToolTipText("Hide Panel");
    hideButton.setText("Hide");
    hideButton.addActionListener(new JobStatusDialog_hideButton_actionAdapter(this));
    removeButton.setToolTipText("Remove Selected Jobs from the Log");
    removeButton.setText("Remove Selected");
    removeButton.addActionListener(new JobStatusDialog_removeButton_actionAdapter(this));
    killButton.setToolTipText("Kill Selected Job(s)");
    killButton.setText("Kill Selected Jobs");
    killButton.addActionListener(new JobStatusDialog_killButton_actionAdapter(this));
    HelpButton.setToolTipText("Help");
    HelpButton.setText("Help");
    HelpButton.addActionListener(new JobStatusDialog_HelpButton_actionAdapter(this));
    downloadButton.setToolTipText("Download Output Files for Completed Jobs");
    downloadButton.setText("Download");
    downloadButton.addActionListener(new JobStatusDialog_downloadButton_actionAdapter(this));
    getContentPane().add(mainPanel);
    mainPanel.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(hideButton);
    jPanel1.add(updateButton);
    jPanel1.add(downloadButton);
    jPanel1.add(removeButton);
    jPanel1.add(killButton);
    jPanel1.add(HelpButton);
    mainPanel.add(jobStatusPanel1, BorderLayout.CENTER);
    //queryJobStatus();
  }

  public void hideButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  public void performTask(String winTitle, String labelTitle, int jobType) {
    taskType = jobType;
    performTask(winTitle, labelTitle);
  }

  void performTask(String winTitle, String labelTitle) {
    FileTranferWorker worker = new FileTranferWorker(this, taskType, labelTitle);
    jobStatusInterface.setJobProgressInterface(worker);
    worker.start();
    if (true) {
      return;
    }

    final int MAX = 10;
    final JLabel msgLabel = new JLabel(labelTitle);
    final JProgressBar progressBar = new JProgressBar(0, MAX);

    progressBar.setValue(0);

    Object[] comp = {
      msgLabel, progressBar};
    Object[] options = {
      "Cancel"};

    JOptionPane pane = new JOptionPane(comp,
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        options,
        options[0]);

    final JDialog dialog = pane.createDialog(this, winTitle);
    //dialog.setVisible(true);

    final Task task = new Task();

    Timer timer = new Timer(250, new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (task.isDone()) {
          dialog.setVisible(false);
        } else {
          progressBar.setValue((progressBar.getValue() + 1) % MAX);
        }
      }
    });

    timer.start();
    task.start();

    try {
      Thread.sleep(500);
    } catch (Exception e) {
    }

    if (task.getException() == null) {
      dialog.setVisible(true);
    } else {
    }

    timer.stop();
  }

  public void queryJobStatus() {
    taskType = JSD_QUERY_STATUS;
    performTask("Quering Job Status", "Quering job status...");
  }

  /**
   * Kills selected jobs
   */
  void killJobs() {
    int[] rows = jobStatusPanel1.jobStatusTable.getSelectedRows();
    if (jobStatusInterface == null) {
      String errors = "";
      for (int i = 0; i < rows.length; i++) {
        String handle = (String) jobStatusPanel1.jobStatusTable.getValueAt(rows[i], 0);

        try {
          //GRAM.killJob(handle);
          System.err.println("This options is not implemented");
        } catch (Exception ex) {
          //errors += GRAM.getMessage() + "\n";
          errors += ex.getMessage() + "\n";
        }

      }
      if (errors.length() > 0) {
        JOptionPane.showMessageDialog(null, errors, "Notice", JOptionPane.WARNING_MESSAGE);
      }
    } else {
      jobStatusInterface.killSelectedJobs(rows);
      updateJobStatus();
    }
  }

  public void setFileViewer(FileViewerInterface fileviewer) {
    jobStatusPanel1.setFileViewer(fileviewer);
  }

  void downloadOutputFiles() {
    int[] rows = jobStatusPanel1.jobStatusTable.getSelectedRows();
    if (jobStatusInterface == null) {
    } else {
      jobStatusInterface.downloadRemoteFiles(rows);
      updateJobStatus();
    }
  }

  synchronized void updateJobStatus() {
    if (jobStatusInterface == null) {
      ArrayList statuses = null;
      try {
        //statuses = GRAM.getStatusLoggedJobs();
        System.err.println("This option is not implemented");
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error Quering Job Statuses: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        //return;
      }

      JobStatusTableModel jstm = new JobStatusTableModel(statuses);
      jobStatusPanel1.jobStatusTable.setModel(jstm);
    } else {

      JobStatusTableModel jstm = new JobStatusTableModel(jobStatusInterface);
      jobStatusPanel1.updateTable(jstm);
    }
  }

  synchronized void getUpdatedJobStatus() {
    if (jobStatusInterface == null) {
      JOptionPane.showMessageDialog(null, "Job Status Interface is not set", "Internal Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    //if (progressBarDialog == null) {
    //  progressBarDialog = new ProgressBarDialog(null, false);
    //  progressBarDialog.setLocationRelativeTo(this);
    //}
    //progressBarDialog.setVisible(true);
    Object[][] data = jobStatusInterface.getData();
    JobStatusTableModel jstm = new JobStatusTableModel(jobStatusInterface.getColumnNames(), data, jobStatusInterface);
    jobStatusPanel1.updateTable(jstm);

    //progressBarDialog.setVisible(false);
  }

  public void updateButton_actionPerformed(ActionEvent e) {
    taskType = JSD_QUERY_STATUS;
    performTask("Quering Job Status", "Quering job status...");
    //queryJobStatus();
  }

  public void removeButton_actionPerformed(ActionEvent e) {
    int[] rows = jobStatusPanel1.jobStatusTable.getSelectedRows();
    if (jobStatusInterface == null) {
      if (jobStatusPanel1.jobStatusTable.getSelectedRowCount() < 1) {
        JOptionPane.showMessageDialog(null, "Select Row(s) First!", "Advice", JOptionPane.WARNING_MESSAGE);
        return;
      }

      //java.util.List ids = GRAM.getJobIdsFromLog();
      System.err.println("Function is not implemented");
      java.util.List ids = new ArrayList();
      ArrayList new_ids = new ArrayList();
      String errors = "";
      for (int j = 0; j < ids.size(); j++) {
        String id = (String) ids.get(j);
        boolean to_delete = false;

        for (int i = 0; i < rows.length; i++) {
          String handle = (String) jobStatusPanel1.jobStatusTable.getValueAt(
              rows[i], 0);
          if (handle.equalsIgnoreCase(id)) {
            to_delete = true;
            break;
          }
        }

        if (!to_delete) {
          new_ids.add(id);
          //errors += "Id " + handle + " is not in log file\n";
        }
      }

      System.err.println("Function is not implemented");
      if (true) {
        //if (GRAM.recordNewLog(new_ids) != 0) {
        //JOptionPane.showMessageDialog(null, GRAM.getMessage(), "Log Update", JOptionPane.ERROR_MESSAGE);
        return;
      }

      queryJobStatus();

      if (errors.length() > 0) {
        JOptionPane.showMessageDialog(null, errors, "Notice", JOptionPane.WARNING_MESSAGE);
      }
    } else {
      jobStatusInterface.removeSelectedCheckpoints(rows);
      queryJobStatus();
    }
  }

  public void killButton_actionPerformed(ActionEvent e) {
    if (jobStatusPanel1.jobStatusTable.getSelectedRowCount() < 1) {
      JOptionPane.showMessageDialog(null,
          "Select Row(s) First!",
          "Advice",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    taskType = JSD_KILL_JOBS;
    performTask("Killing Jobs", "Killing job(s)...");
    //queryJobStatus();

  }

  public void HelpButton_actionPerformed(ActionEvent e) {
    JOptionPane.showMessageDialog(null,
        jobStatusPanel1.getHelp(),
        "Help",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void downloadButton_actionPerformed(ActionEvent e) {
    if (jobStatusPanel1.jobStatusTable.getSelectedRowCount() < 1) {
      JOptionPane.showMessageDialog(null,
          "Select Row(s) First!",
          "Advice",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    taskType = JSD_DOWNLOAD_JOBS;
    performTask("Downloading Output Files", "Downloading Output File(s)...");
    //queryJobStatus();

  }

  private class FileTranferWorker
      extends cct.tools.SwingWorker implements JobProgressInterface {

    JDialog parent = null;
    int jobType;
    ProgressDialog pd;

    FileTranferWorker(JDialog p, int jobtype, String jobDesc) {
      parent = p;
      jobType = jobtype;
      pd = new ProgressDialog(new Frame(), "Progress", false);
      pd.setLocationRelativeTo(parent);
      pd.setAlwaysOnTop(true);
      pd.jobTypeLabel.setText(jobDesc);
      pd.cancelButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          cancelTranfer();
        }
      });

    }

    public boolean isCanceled() {
      return false;
    }

    public void setTaskDescription(String taskDescription) {
    }

    @Override
    public void setProgress(int p) {
      pd.setProgressValue(p);
    }

    public void setProgress(double p) {
      setProgress((int) p);
    }

    public void setProgress(float p) {
      setProgress((int) p);
    }

    @Override
    public void setProgressText(String text) {
      pd.setProgressText(text);
    }

    @Override
    public Object construct() {
      pd.setVisible(true);
      try {
        Thread.sleep(500);
      } catch (Exception ex) {
      }

      if (jobType == JSD_QUERY_STATUS) {
        getUpdatedJobStatus();
        //updateJobStatus();
      } else if (jobType == JSD_KILL_JOBS) {
        killJobs();
        queryJobStatus();
      } else if (jobType == JSD_DOWNLOAD_JOBS) {
        downloadOutputFiles();
        queryJobStatus();
      }

      pd.setVisible(false);
      return "done";
    }

    void cancelTranfer() {
      pd.setVisible(false);
      interrupt();
    }

    @Override
    public void finished() {
      pd.setVisible(false);
    }
  }

  public static void main(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
          exception.printStackTrace();
        }

        CheckPointStatus chk = new CheckPointStatus();
        JobStatusDialog frame = new JobStatusDialog(new Frame(), "Job Status", false,
            chk);
        frame.performTask("Quering Job Status...", "Quering Job Status...", JobStatusDialog.JSD_QUERY_STATUS); //.queryJobStatus();
        frame.validate();
        frame.hideButton.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);

      }
    });
  }

  //**********************************************************************
  /**
   *
   * <p>
   * Title: Molecular Structure Viewer/Editor</p>
   *
   * <p>
   * Description: Computational Chemistry Toolkit</p>
   *
   * <p>
   * Copyright: Copyright (c) 2006</p>
   *
   * <p>
   * Company: ANU</p>
   *
   * @author Dr. V. Vasilyev
   * @version 1.0
   */
  class Task
      extends Thread {

    private boolean done = false;
    private Exception exception = null;
    private boolean cancel = false;

    public boolean isDone() {
      return done;
    }

    public void cancel() {
      cancel = true;
    }

    public boolean isCancelled() {
      return cancel;
    }

    @Override
    public void run() {

      try {
        queryIsDone = false;
        if (taskType == JSD_QUERY_STATUS) {
          updateJobStatus();
          //ArrayList statuses = GRAM.getStatusLoggedJobs();
          //JobStatusTableModel jstm = new JobStatusTableModel(statuses);
          //jobStatusPanel1.jobStatusTable.setModel(jstm);
        } else if (taskType == JSD_KILL_JOBS) {
          killJobs();
        } else if (taskType == JSD_DOWNLOAD_JOBS) {
          downloadOutputFiles();
        }

        queryIsDone = true;
      } catch (Exception e) {
        exception = e;
        done = true;
        return;
      }

      /*
       * try { proxy = model.createProxy(new String(passwordTF.getPassword())); } catch (Exception e) { exception = e; done = true;
       * return; }
       */
      if (cancel) {
        return;
      }

      // send the event....
      //if (proxyListener != null) {
      //  proxyListener.proxyCreated(proxy);
      //}
      done = true;
    }

    public Exception getException() {
      return exception;
    }
  }
}

class JobStatusDialog_downloadButton_actionAdapter
    implements ActionListener {

  private JobStatusDialog adaptee;

  JobStatusDialog_downloadButton_actionAdapter(JobStatusDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.downloadButton_actionPerformed(e);
  }
}

class JobStatusDialog_HelpButton_actionAdapter
    implements ActionListener {

  private JobStatusDialog adaptee;

  JobStatusDialog_HelpButton_actionAdapter(JobStatusDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.HelpButton_actionPerformed(e);
  }
}

class JobStatusDialog_killButton_actionAdapter
    implements ActionListener {

  private JobStatusDialog adaptee;

  JobStatusDialog_killButton_actionAdapter(JobStatusDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.killButton_actionPerformed(e);
  }
}

class JobStatusDialog_removeButton_actionAdapter
    implements ActionListener {

  private JobStatusDialog adaptee;

  JobStatusDialog_removeButton_actionAdapter(JobStatusDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.removeButton_actionPerformed(e);
  }
}

class JobStatusDialog_updateButton_actionAdapter
    implements ActionListener {

  private JobStatusDialog adaptee;

  JobStatusDialog_updateButton_actionAdapter(JobStatusDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.updateButton_actionPerformed(e);
  }
}

class JobStatusDialog_hideButton_actionAdapter
    implements ActionListener {

  private JobStatusDialog adaptee;

  JobStatusDialog_hideButton_actionAdapter(JobStatusDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.hideButton_actionPerformed(e);
  }
}
