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
package cct.grid.ui.gaussian;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import cct.globus.ui.GatekeeperPanel;
import cct.globus.ui.GridProxyInitDialog;
import cct.grid.CheckPoint;
import cct.grid.CheckPointInterface;
import cct.grid.GridProviderInterface;
import cct.grid.JobDescription;
import cct.grid.ParseCheckpointFile;
import cct.grid.ScriptSubmitterDialogInterface;
import cct.grid.TaskProvider;
import cct.grid.TaskScheduler;
import cct.grid.ui.JobOutputInterface;
import cct.grid.ui.PBS_Panel;
import cct.grid.ui.ProgressDialog;
import cct.grid.ui.ResourcesPanel;
import cct.interfaces.FileChooserInterface;
import cct.tools.IOUtils;

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
public class SubmitGaussianDialog
        extends JDialog implements ScriptSubmitterDialogInterface {

  JPanel MainPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  SubmitGaussianPanel submitGaussianPanel1 = new SubmitGaussianPanel();
  PBS_Panel pBS_Panel1 = new PBS_Panel(null, "normal", 60, 1024, 5,
          "g03");
  JPanel ButtonPanel = new JPanel();
  JButton Cancel_Button = new JButton();
  JButton Submit_Button = new JButton();
  JComboBox providerComboBox = new JComboBox();
  JPanel providerPanel = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel providerLabel = new JLabel();
  ProgressDialog progressDialog = null;
  //private GridProxyInterface getGridProxy = null;
  int taskType = 0;
  private static Logger logger = Logger.getLogger(SubmitGaussianDialog.class.getName());
  Map<String, GridProviderInterface> allProviders = new LinkedHashMap<String, GridProviderInterface>();
  JobOutputInterface jobOutput = null;
  JButton getProxy_Button = new JButton();
  JPanel cardPanel = new JPanel();
  JPanel comboPanel = new JPanel();
  FlowLayout flowLayout2 = new FlowLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  CardLayout cardLayout1 = new CardLayout();
  JLabel schedulerLabel = new JLabel();
  JComboBox schedulerComboBox = new JComboBox();
  ResourcesPanel resourcesPanel1 = new ResourcesPanel();
  JPanel topPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();

  public static void main(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
          exception.printStackTrace();
        }

        SubmitGaussianDialog frame = new SubmitGaussianDialog();
        frame.setTaskProviders(TaskProvider.getAvailableTaskProviders());
        frame.validate();
        frame.pack();
        frame.Cancel_Button.addActionListener(new ActionListener() {

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

  @Override
  public ScriptSubmitterDialogInterface newInstance() {
    return new SubmitGaussianDialog(new Frame(), null, "Submit Gaussian Script", false);
  }

  @Override
  public void setCustomOptions(String options) throws Exception {
    throw new Exception("setCustomOptions is not implemented in " + this.getClass().getCanonicalName());
  }

  @Override
  public JDialog getDialog() {
    return this;
  }

  @Override
  public void setLocalDirectory(String localDir) {
    submitGaussianPanel1.LocalDirTextField.setText(localDir);
  }

  @Override
  public void setRemoteDirectory(String remoteDir) {
    submitGaussianPanel1.RemoteDirTextField.setText(remoteDir);
  }

  public SubmitGaussianDialog(Frame owner, JobOutputInterface redir,
          String title, boolean modal) {
    super(owner, title, modal);
    jobOutput = redir;
    try {
      setDefaultCloseOperation(HIDE_ON_CLOSE);
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public SubmitGaussianDialog() {
    this(new Frame(), null, "Submit Gaussian Script", false);
  }

  // public void setProxyHandler(GridProxyInterface proxyHandler) {
  //  getGridProxy = proxyHandler;
  //}
  private void jbInit() throws Exception {
    MainPanel.setLayout(borderLayout1);
    submitGaussianPanel1.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1), "Job Specifications"));
    pBS_Panel1.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.lightGray, 2), "PBS Options"));
    pBS_Panel1.setMinimumSize(new Dimension(254, 150));
    Cancel_Button.setToolTipText("");
    Cancel_Button.setText("Cancel");
    Cancel_Button.addActionListener(new SubmitGaussianDialog_Cancel_Button_actionAdapter(this));
    Submit_Button.setToolTipText("");
    Submit_Button.setText("Submit");
    Submit_Button.addActionListener(new SubmitGaussianDialog_Submit_Button_actionAdapter(this));
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    getProxy_Button.setToolTipText("");
    getProxy_Button.setText("Get new Proxy");
    getProxy_Button.addActionListener(new SubmitGaussianDialog_getProxy_Button_actionAdapter(this));
    providerLabel.setToolTipText("");
    providerLabel.setText(" Provider: ");
    comboPanel.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    providerPanel.setLayout(borderLayout2);
    cardPanel.setLayout(cardLayout1);
    providerComboBox.addActionListener(new SubmitGaussianDialog_providerComboBox_actionAdapter(this));
    schedulerLabel.setToolTipText("");
    schedulerLabel.setText(" Scheduler: ");
    cardPanel.setMinimumSize(new Dimension(255, 150));
    schedulerComboBox.setEnabled(false);
    topPanel.setLayout(borderLayout3);
    MainPanel.add(submitGaussianPanel1, BorderLayout.SOUTH);
    //MainPanel.add(taskProviderPanel, java.awt.BorderLayout.NORTH);
    this.getContentPane().add(MainPanel, BorderLayout.CENTER);
    ButtonPanel.add(Submit_Button);
    ButtonPanel.add(getProxy_Button);
    ButtonPanel.add(Cancel_Button);
    this.getContentPane().add(ButtonPanel, BorderLayout.SOUTH);
    MainPanel.add(pBS_Panel1, BorderLayout.CENTER);
    MainPanel.add(resourcesPanel1, BorderLayout.NORTH);
    comboPanel.add(providerLabel);
    comboPanel.add(providerComboBox);
    comboPanel.add(schedulerLabel);
    comboPanel.add(schedulerComboBox);
    providerPanel.add(cardPanel, BorderLayout.CENTER);
    providerPanel.add(comboPanel, BorderLayout.NORTH);
    this.getContentPane().add(topPanel, BorderLayout.NORTH);
    topPanel.add(providerPanel, BorderLayout.CENTER);
    pBS_Panel1.JobfsTextField.setEditable(true);
    pBS_Panel1.SoftwareTextField.setEditable(false);

    WindowListener l = new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) {
        logger.info("Exiting...");
        //Deactivator.deactivateAll();
        System.exit(0);
      }
    };

    addWindowListener(l);

    setTaskSchedulers(TaskScheduler.getAvailableTaskSchedulers());

    progressDialog = new ProgressDialog(new Frame(), "Task Submission Progress", false);
    progressDialog.cancelButton.setVisible(false);
    progressDialog.setAlwaysOnTop(true);
    progressDialog.setLocationRelativeTo(this);

    this.validate();
  }

  public void enableLocalDir(boolean enable) {
    submitGaussianPanel1.LocalDirTextField.setEnabled(enable);
  }

  public void enableInputFile(boolean enable) {
    submitGaussianPanel1.inputFileTextField.setEnabled(enable);
  }

  public void setLocalDir(String locDir) {
    submitGaussianPanel1.LocalDirTextField.setText(locDir);
  }

  @Override
  public void setInputFile(String inputFile) {
    submitGaussianPanel1.setInputFile(inputFile);
  }

  public void setGatekeepers(String[] gk) {
  }

  public void setHosts(String[] gk) {
  }

  public void Cancel_Button_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  @Override
  public void setTaskProviders(Map providers) {
    providerComboBox.setEnabled(false);
    providerComboBox.removeAllItems();

    allProviders = new LinkedHashMap(providers);

    Set set = providers.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String providerName = me.getKey().toString();
      providerComboBox.addItem(providerName);
      //Component panel = (Component) me.getValue();
      GridProviderInterface provider = (GridProviderInterface) me.getValue();
      Component panel = provider.getVisualComponent();
      panel.validate();

      cardPanel.add(panel, providerName);
    }

    providerComboBox.setSelectedIndex(0);

    CardLayout cl = (CardLayout) cardPanel.getLayout();
    cl.show(cardPanel, providerComboBox.getSelectedItem().toString());
    cardPanel.validate();
    cardPanel.updateUI();

    providerComboBox.setEnabled(true);

    this.validate();
  }

  // !!! It's only a prototype yet !!!
  public void setTaskSchedulers(Map schedulers) {
    schedulerComboBox.setEnabled(false);
    schedulerComboBox.removeAllItems();

    Set set = schedulers.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String schedulerName = me.getKey().toString();
      schedulerComboBox.addItem(schedulerName);
      //Component panel = (Component) me.getValue();

      //cardPanel.add(panel, schedulerName);
    }

    schedulerComboBox.setSelectedIndex(0);

    //CardLayout cl = (CardLayout) cardPanel.getLayout();
    //cl.show(cardPanel, providerComboBox.getSelectedItem().toString());

    schedulerComboBox.setEnabled(true);
  }

  public void Submit_Button_actionPerformed(ActionEvent e) {
    validate();
    /*
    if (gass == null) {
    int server_options =
    org.globus.io.gass.server.GassServer.STDOUT_ENABLE |
    org.globus.io.gass.server.GassServer.STDERR_ENABLE;

    //if ( (options & GLOBUSRUN_ARG_ALLOW_READS) != 0) {
    server_options |=
    org.globus.io.gass.server.GassServer.READ_ENABLE;
    //}

    //if ( (options & GLOBUSRUN_ARG_ALLOW_WRITES) != 0) {
    server_options |=
    org.globus.io.gass.server.GassServer.WRITE_ENABLE;
    //}

    try {
    //gass = new GassServer(true, 0);
    gass = new org.globus.io.gass.server.GassServer();
    gass.setOptions(server_options);
    gass.setTimeout(0); // keep sockets open forever

    gass.registerDefaultDeactivator();
    }
    catch (java.io.IOException ex) {
    //logger.info("GassServer: IOException: " + ex.getMessage());
    System.err.println("Gass server initialization failed: " +
    ex.getMessage());
    JOptionPane.showMessageDialog(null,
    "Gass server initialization failed: " +
    ex.getMessage(),
    "Error",
    JOptionPane.ERROR_MESSAGE);
    return;
    }
    logger.info("GassServer: Passed...");
    }
     */

    String provider = providerComboBox.getSelectedItem().toString();

    GridProviderInterface taskProvider = allProviders.get(provider);
    boolean passOptionsToScheduler = taskProvider.isPassOptionsToScheduler();

    String scheduler = schedulerComboBox.getSelectedItem().toString();
    boolean use_gatekeeper = false;
    String gatekeeper = null;
    String remote_host = taskProvider.getRemoteHost();
    // --- Get Script name (executable)
    String script_file = script_file = submitGaussianPanel1.ScriptTextField.getText().trim();

    JobDescription jobDescription = new JobDescription();

    /*
    if (provider.equals(TaskProvider.GLOBUS_2_PROVIDER)) {
    GatekeeperPanel gkp = (GatekeeperPanel) TaskProvider.getProvider(provider);
    gatekeeper = gkp.GatekeeperComboBox.getSelectedItem().toString();
    remote_host = gkp.HostComboBox.getSelectedItem().toString();

    if (gatekeeper.length() < 1) {
    use_gatekeeper = false;
    }
    else {
    use_gatekeeper = true;
    }

    if (!use_gatekeeper && remote_host.length() < 1) {
    JOptionPane.showMessageDialog(null,
    "Neither gatekeeper nor remote host are set",
    "Error",
    JOptionPane.ERROR_MESSAGE);
    return;

    }
    }

    else if (provider.equals(TaskProvider.GLOBUS_4_PROVIDER)) {
    GT4Panel gkp = (GT4Panel) TaskProvider.getProvider(provider);
    gatekeeper = gkp.GatekeeperComboBox.getSelectedItem().toString();
    remote_host = gkp.HostComboBox.getSelectedItem().toString();

    if (gatekeeper.length() < 1) {
    use_gatekeeper = false;
    }
    else {
    use_gatekeeper = true;
    }

    if (!use_gatekeeper && remote_host.length() < 1) {
    JOptionPane.showMessageDialog(null,
    "Neither gatekeeper nor remote host are set",
    "Error",
    JOptionPane.ERROR_MESSAGE);
    return;

    }
    }

    else if (provider.equals(TaskProvider.SSH_PROVIDER)) {
    SSHPanel sshp = (SSHPanel) TaskProvider.getProvider(provider);
    try {
    sshp.checkConnection();
    }
    catch (Exception ex) {
    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
    JOptionPane.ERROR_MESSAGE);
    return;
    }
    }

    else if (provider.equals(TaskProvider.LOCAL_PROVIDER)) {
    remote_host = "local";

    }

    else {
    JOptionPane.showMessageDialog(null,
    "Unknown service provider: " + provider,
    "Error",
    JOptionPane.ERROR_MESSAGE);
    return;
    }
     */

    // --- Preparing PBS Script

    // Setting up job description

    // --- Set Remote directory

    String remoteDirectory = submitGaussianPanel1.RemoteDirTextField.getText().
            trim();

    // --- Job name
    String jobName = submitGaussianPanel1.jobNameTextField.getText().trim();

    // --- Get Local directory
    String localDirectory = submitGaussianPanel1.LocalDirTextField.getText().
            trim();

    if (localDirectory.indexOf("\\") != -1) {
      localDirectory = "/" + localDirectory;
      try {
        //valid_dir = valid_dir.replaceAll("\\", "/");
        localDirectory = localDirectory.replace('\\', '/');
      } catch (PatternSyntaxException ex) {
        while (localDirectory.indexOf("\\") != -1) {
        }
        logger.info("Regex error: " + localDirectory);

      }
    }

    if (localDirectory.endsWith("/")) {
      localDirectory = localDirectory.substring(0, localDirectory.length() - 1);
    }
    jobDescription.setLocalDirectory(localDirectory);

    // --- Get CPU number

    try {
      jobDescription.setNCPU(pBS_Panel1.getNCPUs());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Setup max (wall) time

    float maxWallTime = -1;
    try {
      float time_in_secs = pBS_Panel1.getMaxTime();
      maxWallTime = time_in_secs;
      jobDescription.setMaxCpuTime(time_in_secs * 60.0f);
      jobDescription.setMaxTime(time_in_secs * 60.0f);
      jobDescription.setMaxWallTime(time_in_secs * 60.0f);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Setup max memory

    int maxMemory = 0; // in MB
    try {
      maxMemory = pBS_Panel1.getMaxMemory();
      jobDescription.setMaxMemory(maxMemory);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Setup project

    jobDescription.setProject(pBS_Panel1.getProject());

    // --- Setup jobfs

    int jobFS = 0;
    try {
      jobFS = pBS_Panel1.getJobFS();
      jobDescription.setJobFS(jobFS);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    String input_file = submitGaussianPanel1.inputFileTextField.getText().trim();
    String output_file = submitGaussianPanel1.OutputFileTextField.getText().
            trim();

    // --- Setup queue

    String queue = pBS_Panel1.getQueue();
    /*
    if (provider.equals(TaskProvider.GLOBUS_2_PROVIDER) && use_gatekeeper) {
    queue += "@" + remote_host.substring(0, remote_host.indexOf("."));
    }
    else if (provider.equals(TaskProvider.GLOBUS_4_PROVIDER) && use_gatekeeper) {
    queue += "@" + remote_host.substring(0, remote_host.indexOf("."));
    }
     */

    jobDescription.setQueue(queue);

    // Stdout & stderr

    String stdout = submitGaussianPanel1.getStdoutFileName().trim();
    String stderr = submitGaussianPanel1.getStderrFileName().trim();

    //if (provider.equals(TaskProvider.SSH_PROVIDER)) {
    if (stdout.length() > 0) {
      jobDescription.setStdout(stdout);
    }
    if (stderr.length() > 0) {
      jobDescription.setStderr(stderr);
    }
    //}


    // --- Setup software

    jobDescription.setSoftware(pBS_Panel1.getSoftware());

    // --- Setup scheduler

    String sched = this.schedulerComboBox.getSelectedItem().toString();
    if (passOptionsToScheduler) {
      jobDescription.setScheduler(sched);
    }

    jobDescription.addPreprocessDirective("module load dos2unix");
    jobDescription.addPreprocessDirective("dos2unix " + input_file);
    jobDescription.addPreprocessDirective("module load gaussian");

    jobDescription.setExecutable("g03");

    jobDescription.addArgument("<" + input_file);
    jobDescription.addArgument(">" + output_file);

    jobDescription.setPBSOther("chemgrid");

    jobDescription.setShell("/bin/bash");

    // --- Save script file
    try {
      String scriptContent = JobDescription.createUnixScript(jobDescription);
      IOUtils.saveStringIntoFile(scriptContent,
              submitGaussianPanel1.LocalDirTextField.getText().
              trim()
              + script_file);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
              "Error saving script file" + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

    jobDescription.setTaskProvider(provider);
    jobDescription.setScheduler(JobDescription.PBS_SCHEDULER);

    // --- Preparing final job description for submitting

    JobDescription submitDescription = new JobDescription();
    submitDescription.setTaskProvider(provider);
    submitDescription.setScheduler(JobDescription.PBS_SCHEDULER);
    submitDescription.setPbsJobScript(true);
    submitDescription.setExecutable(script_file);
    submitDescription.setRemoteHost(remote_host);
    submitDescription.setRemoteDirectory(remoteDirectory);
    submitDescription.setJobName(jobName);
    submitDescription.setLocalDirectory(localDirectory);
    submitDescription.setProject(pBS_Panel1.getProject());
    submitDescription.setQueue(queue);

    // --- ( File Staging In )

    submitDescription.setFileStageIn(script_file, script_file); // Run script file
    submitDescription.setFileStageIn(input_file, input_file); // Data input file

    // --- File staging out....

    submitDescription.setFileStageOut(output_file, output_file);

    submitDescription.setExecutable(script_file);
    submitDescription.setLocalExecutable(true);

    submitDescription.setSoftware(pBS_Panel1.getSoftware());
    if (jobFS > 0) {
      submitDescription.setJobFS(jobFS);
    }
    if (maxMemory > 0) {
      submitDescription.setMaxMemory(maxMemory);
    }
    if (maxWallTime > 0) {
      submitDescription.setMaxWallTime(maxWallTime);
    }

    submitDescription.setPbsJobScript(false);

    if (provider.equals(TaskProvider.GLOBUS_2_PROVIDER)) {
      //jobDescription.setExecutable("/bin/csh");
      //jobDescription.addArgument(script_file);
      if (use_gatekeeper) {
        submitDescription.setRemoteHost(gatekeeper);
        jobDescription.setRemoteDirectory(gatekeeper); // ????? BUG
      } else {
        submitDescription.setRemoteHost(remote_host);
        jobDescription.setRemoteDirectory(remote_host);
      }
      GatekeeperPanel gkp = (GatekeeperPanel) TaskProvider.getProvider(provider);
      submitDescription.setClusterAddress(gkp.HostComboBox.getSelectedItem().
              toString());
      submitDescription.enableGASS(gkp.gassCheckBox.isSelected());
      jobDescription.enableGASS(gkp.gassCheckBox.isSelected());
    } //else if (provider.equals(TaskProvider.GLOBUS_4_PROVIDER)) {
    //jobDescription.setExecutable("/bin/csh");
    //jobDescription.addArgument(script_file);
    //if (use_gatekeeper) {
    //  submitDescription.setRemoteHost(gatekeeper);
    //  submitDescription.setClusterAddress(remote_host);
    //}
    //else {
    //  submitDescription.setRemoteHost(remote_host);
    //  jobDescription.setRemoteDirectory(remote_host);
    //}
    //GT4Panel gkp4 = (GT4Panel) TaskProvider.getProvider(provider);
    //submitDescription.setClusterAddress(gkp4.HostComboBox.getSelectedItem().
    //                                    toString());
    //}
    /*
    else if (provider.equals(TaskProvider.SSH_PROVIDER)) {
    if (stdout.length() > 0) {
    submitDescription.setStdout(stdout);
    }
    if (stderr.length() > 0) {
    submitDescription.setStderr(stderr);
    }

    SSHPanel sshp = (SSHPanel) TaskProvider.getProvider(provider);
    Object ssh_provider = sshp.getSSHProvider();
    submitDescription.setTaskProviderObject(ssh_provider);
    remote_host = sshp.getHost();
    }
     */ else if (provider.equals(TaskProvider.LOCAL_PROVIDER)) {
    }

    // --- Saving settings

    pBS_Panel1.saveCurrentSettings();

    // --- Submitting job

    //progressDialog.setLocationRelativeTo(this);
    //progressDialog.setVisible(true);
    //progressDialog.jobTypeLabel.setText("Submitting " +
    //                                    submitDescription.getJobName() + "...");

    JustProgress progress = new JustProgress(this, "Submitting " + submitDescription.getJobName() + "...");
    progress.start();

    String jobId = null;
    //TaskProvider task = new TaskProvider();
    try {
      jobId = taskProvider.submitTask(submitDescription);
      //task.submitJob(submitDescription);
    } catch (Exception ex) {
      //progressDialog.setVisible(false);
      progress.cancelShow();
      String message = "Error submitting job: " + ex.getMessage();
      JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
      System.err.println(message);
      return;
    }
    //progressDialog.setVisible(false);

    progress.cancelShow();

    JOptionPane.showMessageDialog(null, "Job submitted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

    //jobId = task.getJobHandle();

    Date subDate = new Date();

    CheckPoint chkp = null;

    if (jobId == null) {
      System.err.println("No job handle was returned. Job is not included into checkpoint file");
      return;
    }

    if (provider.equals(TaskProvider.LOCAL_PROVIDER)) {
      System.err.println("Checkpoint is not written for a local provider");
      return;
    }

    if (provider.equals(TaskProvider.GLOBUS_4_PROVIDER)) {
      System.err.println("Checkpoint is not written for a gt4 provider");
      return;
    }

    /*
    if (provider.equals(TaskProvider.SSH_PROVIDER)) {
    System.err.println("Checkpoint is not written for a SSH provider");
    return;
    }
     */

    try {
      chkp = new CheckPoint(CheckPointInterface.jobNameTag + "@" + jobName + "@"
              + CheckPointInterface.handleTag + "@" + jobId + "@"
              + CheckPointInterface.providerTag + "@" + provider + "@"
              + CheckPointInterface.computerTag + "@"
              + remote_host + "@"
              + CheckPointInterface.softwareTag + "@Gaussian@"
              + CheckPointInterface.programTag + "@g03@"
              + CheckPointInterface.executableTag + "@" + script_file + "@"
              + CheckPointInterface.localExecutableTag + "@true@"
              + CheckPointInterface.remoteDirectoryTag + "@"
              + submitGaussianPanel1.RemoteDirTextField.getText().
              trim() + "@"
              + CheckPointInterface.localDirectoryTag + "@"
              + submitGaussianPanel1.LocalDirTextField.getText().
              trim() + "@"
              + CheckPointInterface.statusTag + "@Submitted@"
              + CheckPointInterface.overallJobStatusTag + "@Pending@"
              + CheckPointInterface.submittedTimeTag + "@" + subDate.toString(),
              "@" // Divider
              );
      chkp.setOutputFile(SubmitGaussianPanel.gaussianOutputFileTag, output_file);

      if (provider.equals(TaskProvider.SSH_PROVIDER)) {
        if (stdout.length() > 0) {
          chkp.setOutputFile(CheckPointInterface.stdOutputTag, stdout);
        }
        if (stderr.length() > 0) {
          chkp.setOutputFile(CheckPointInterface.stdErrorTag, stderr);
        }
      }

    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      return;
    }

    ParseCheckpointFile pcf = new ParseCheckpointFile();
    try {
      pcf.addCheckPoint(chkp);
    } catch (cct.grid.MalformedCheckPointException ex) {
      JOptionPane.showMessageDialog(null,
              ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

  }

  /*
  void shutdownGASS(GassServer gass) {
  try {
  gass.shutdown();
  }
  catch (Exception ex) {
  ex.printStackTrace();
  }
  }
   */
  public void getProxy_Button_actionPerformed(ActionEvent e) {
    GridProxyInitDialog.openGetProxyDialog();
  }

  public void providerComboBox_actionPerformed(ActionEvent e) {
    if (!providerComboBox.isEnabled()) {
      return;
    }
    providerComboBox.setEnabled(false);

    CardLayout cl = (CardLayout) cardPanel.getLayout();
    cl.show(cardPanel, providerComboBox.getSelectedItem().toString());

    String provider = providerComboBox.getSelectedItem().toString();
    //if (provider.equalsIgnoreCase(cct.grid.TaskProvider.SSH_PROVIDER)) {
    //SSHPanel ssh = (SSHPanel) allProviders.get(provider);
    GridProviderInterface gpi = allProviders.get(provider);
    FileChooserInterface fci = gpi.getRemoteFileChooser();
    submitGaussianPanel1.setRemoteFileChooser(fci);
    //submitGaussianPanel1.setRemoteFileChooser(ssh);
    //}

    providerComboBox.setEnabled(true);

  }

  private class JustProgress
          extends cct.tools.SwingWorker {

    JDialog parent = null;
    int jobType;
    ProgressDialog pd;

    JustProgress(JDialog p, String jobDesc) {
      parent = p;
      pd = new ProgressDialog(new Frame(), "Job Submission Progress", false);
      pd.setLocationRelativeTo(parent);
      pd.setAlwaysOnTop(true);
      pd.jobTypeLabel.setText(jobDesc);
      pd.cancelButton.setVisible(false);
      /*
      pd.cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      cancelTranfer();
      }
      }
      );
       */

    }

    @Override
    public Object construct() {
      pd.setVisible(true);
      try {
        while (true) {
          Thread.sleep(1000);
        }
      } catch (Exception ex) {
      }
      return "done";
    }

    void cancelShow() {
      pd.setVisible(false);
      interrupt();
    }

    @Override
    public void finished() {
      pd.setVisible(false);
    }
  }

  // Does not work properly
  class JobSubProgress
          extends java.util.TimerTask {

    int MAX = 10;
    JLabel msgLabel = new JLabel("Submitting Job...");
    JProgressBar progressBar = new JProgressBar(0, MAX);
    JOptionPane pane = null;
    JDialog dialog = null;

    public JobSubProgress() {
      progressBar.setValue(0);

      Object[] comp = {
        msgLabel, progressBar};
      //Object[] options = {
      //    "Cancel"};

      pane = new JOptionPane(comp,
              JOptionPane.DEFAULT_OPTION,
              JOptionPane.INFORMATION_MESSAGE,
              null);
      //options,
      //options[0]);

      dialog = pane.createDialog(null, "Submitting a Job");
    }

    @Override
    public void run() {
      if (!dialog.isVisible()) {
        dialog.setVisible(true);
      }
      progressBar.setValue((progressBar.getValue() + 1) % MAX);
    }

    @Override
    public boolean cancel() {
      dialog.setVisible(false);
      return false;
    }
  }

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
        //queryIsDone = false;
        //if (taskType == 0) {
        //  updateJobStatus();
        //        }
        //else if ( taskType == JSD_KILL_JOBS ) {
        //  killJobs();
        //}
        //queryIsDone = true;
      } catch (Exception e) {
        exception = e;
        done = true;
        return;
      }

      /*
      try {
      proxy = model.createProxy(new String(passwordTF.getPassword()));
      }
      catch (Exception e) {
      exception = e;
      done = true;
      return;
      }
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

  /*
  class gaussianJobSubOutputListener
  implements JobOutputListener {
  JobOutputInterface textPane = null;

  public gaussianJobSubOutputListener(JobOutputInterface text_pane) {
  textPane = text_pane;
  }

  public void outputClosed() {
  //Job has finished; no more output is available
  //String text = textPane.getText();
  //text += "\nEnd of output\n";
  textPane.appendOutput("\nEnd of output\n");

  }

  public void outputChanged(String output) {
  logger.info("output: " + output);
  //String text = textPane.getText();
  //text += output;
  textPane.appendOutput(output);
  }
  }
   */
}

class SubmitGaussianDialog_providerComboBox_actionAdapter
        implements ActionListener {

  private SubmitGaussianDialog adaptee;

  SubmitGaussianDialog_providerComboBox_actionAdapter(SubmitGaussianDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.providerComboBox_actionPerformed(e);
  }
}

class SubmitGaussianDialog_getProxy_Button_actionAdapter
        implements ActionListener {

  private SubmitGaussianDialog adaptee;

  SubmitGaussianDialog_getProxy_Button_actionAdapter(SubmitGaussianDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.getProxy_Button_actionPerformed(e);
  }
}

class SubmitGaussianDialog_Submit_Button_actionAdapter
        implements ActionListener {

  private SubmitGaussianDialog adaptee;

  SubmitGaussianDialog_Submit_Button_actionAdapter(SubmitGaussianDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Submit_Button_actionPerformed(e);
  }
}

class SubmitGaussianDialog_Cancel_Button_actionAdapter
        implements ActionListener {

  private SubmitGaussianDialog adaptee;

  SubmitGaussianDialog_Cancel_Button_actionAdapter(SubmitGaussianDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Cancel_Button_actionPerformed(e);
  }
}

/*
class JobOutputListenerImpl
implements JobOutputListener {
String jobOutput = "";

public JobOutputListenerImpl() {
}

public void outputClosed() {
//Job has finished; no more output is available
logger.info("Job finished: " + jobOutput);
}

public void outputChanged(String output) {
jobOutput += output;
logger.info("output: " + output);
}
}
 */
