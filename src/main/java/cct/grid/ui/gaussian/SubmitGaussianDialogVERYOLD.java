package cct.grid.ui.gaussian;/*
 package cct.grid.ui.gaussian;

 import java.util.Date;
 import java.awt.*;
 import javax.swing.*;
 import cct.grid.ui.*;
 import java.awt.BorderLayout;
 import javax.swing.border.TitledBorder;
 import cct.grid.ui.PBS_Panel;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import cct.tools.IOUtils;
 import java.util.regex.PatternSyntaxException;

 import cct.globus.*;
 import cct.globus.ui.*;
 import cct.grid.*;
 import java.awt.event.WindowEvent;
 import java.awt.event.WindowListener;
 import java.awt.event.WindowAdapter;
 //import org.globus.util.deactivator.Deactivator;

 import org.apache.log4j.Logger;
 import cct.grid.ui.JobOutputInterface;
 import cct.globus.ui.GridProxyInitDialog;
 import cct.grid.*;
 import java.util.Iterator;
 import java.util.Set;
 import java.util.Map;
 import java.util.LinkedHashMap;
 import java.io.StringWriter;
 import java.awt.Dimension;
 import cct.ssh.*;
 import cct.interfaces.FileChooserInterface;


 public class SubmitGaussianDialog
    extends JDialog implements CheckPointInterface,
    ScriptSubmitterDialogInterface {

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

  //private GridProxyInterface getGridProxy = null;
  int taskType = 0;
  private static Logger logger =
      Logger.getLogger(SubmitGaussianDialog.class.getName());

  LinkedHashMap<String,GridProviderInterface> allProviders = new LinkedHashMap<String,GridProviderInterface>();
  JobOutputInterface jobOutput = null;
  JButton getProxy_Button = new JButton();
  JPanel cardPanel = new JPanel();
  JPanel comboPanel = new JPanel();
  FlowLayout flowLayout2 = new FlowLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  CardLayout cardLayout1 = new CardLayout();
  JLabel schedulerLabel = new JLabel();
  JComboBox schedulerComboBox = new JComboBox();

  public static void main(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
          exception.printStackTrace();
        }

        SubmitGaussianDialog frame = new SubmitGaussianDialog();
        frame.setTaskProviders(TaskProvider.getAvailableTaskProviders());
        frame.validate();
        frame.Cancel_Button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        }
        );
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);

      }
    });
  }

  public JDialog getDialog() {
    return this;
  }

  public SubmitGaussianDialog(Frame owner, JobOutputInterface redir,
                              String title, boolean modal) {
    super(owner, title, modal);
    jobOutput = redir;
    try {
      setDefaultCloseOperation(HIDE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public SubmitGaussianDialog() {
    this(new Frame(), null, "SubmitGaussianDialog", false);
  }

  // public void setProxyHandler(GridProxyInterface proxyHandler) {
  //  getGridProxy = proxyHandler;
  //}

  private void jbInit() throws Exception {
    MainPanel.setLayout(borderLayout1);
    submitGaussianPanel1.setBorder(new TitledBorder(BorderFactory.
        createLineBorder(Color.lightGray, 1), "Job Specifications"));
    pBS_Panel1.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.
        lightGray, 2), "PBS Options"));
    pBS_Panel1.setMinimumSize(new Dimension(254, 150));
    Cancel_Button.setToolTipText("");
    Cancel_Button.setText("Cancel");
    Cancel_Button.addActionListener(new
                                    SubmitGaussianDialog_Cancel_Button_actionAdapter(this));
    Submit_Button.setToolTipText("");
    Submit_Button.setText("Submit");
    Submit_Button.addActionListener(new
                                    SubmitGaussianDialog_Submit_Button_actionAdapter(this));
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    getProxy_Button.setToolTipText("");
    getProxy_Button.setText("Get new Proxy");
    getProxy_Button.addActionListener(new
                                      SubmitGaussianDialog_getProxy_Button_actionAdapter(this));
    providerLabel.setToolTipText("");
    providerLabel.setText(" Provider: ");
    comboPanel.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    providerPanel.setLayout(borderLayout2);
    cardPanel.setLayout(cardLayout1);
    providerComboBox.addActionListener(new
                                       SubmitGaussianDialog_providerComboBox_actionAdapter(this));
    schedulerLabel.setToolTipText("");
    schedulerLabel.setText(" Scheduler: ");
    cardPanel.setMinimumSize(new Dimension(255, 150));
    schedulerComboBox.setEnabled(false);
    MainPanel.add(submitGaussianPanel1, java.awt.BorderLayout.SOUTH);
    //MainPanel.add(taskProviderPanel, java.awt.BorderLayout.NORTH);
    this.getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);
    ButtonPanel.add(Submit_Button);
    ButtonPanel.add(getProxy_Button);
    ButtonPanel.add(Cancel_Button);
    this.getContentPane().add(ButtonPanel, java.awt.BorderLayout.SOUTH);
    MainPanel.add(pBS_Panel1, java.awt.BorderLayout.CENTER);
    MainPanel.add(providerPanel, java.awt.BorderLayout.NORTH);
    comboPanel.add(providerLabel);
    comboPanel.add(providerComboBox);
    comboPanel.add(schedulerLabel);
    comboPanel.add(schedulerComboBox);
    providerPanel.add(comboPanel, java.awt.BorderLayout.NORTH);
    providerPanel.add(cardPanel, java.awt.BorderLayout.CENTER);
    pBS_Panel1.JobfsTextField.setEditable(true);
    pBS_Panel1.SoftwareTextField.setEditable(false);

    WindowListener l = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        logger.debug("Exiting...");
        //Deactivator.deactivateAll();
        System.exit(0);
      }
    };

    addWindowListener(l);

    setTaskSchedulers(TaskScheduler.getAvailableTaskSchedulers());

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

  public void setTaskProviders(LinkedHashMap providers) {
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
      GridProviderInterface provider =(GridProviderInterface)me.getValue();
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
  public void setTaskSchedulers(LinkedHashMap schedulers) {
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

    String provider = providerComboBox.getSelectedItem().toString();

    GridProviderInterface taskProvider = allProviders.get(provider);

    String scheduler = schedulerComboBox.getSelectedItem().toString();
    boolean use_gatekeeper = false;
    String gatekeeper = null;
    String remote_host = null;
    // --- Get Script name (executable)
    String script_file = script_file = submitGaussianPanel1.ScriptTextField.
        getText().trim();

    JobDescription jobDescription = new JobDescription();

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
      }
      catch (PatternSyntaxException ex) {
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
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Setup max (wall) time

    float maxWallTime = -1;
    try {
      float time_in_secs = pBS_Panel1.getMaxTime() * 60.0f;
      maxWallTime = time_in_secs;
      jobDescription.setMaxCpuTime(time_in_secs);
      jobDescription.setMaxTime(time_in_secs);
      jobDescription.setMaxWallTime(time_in_secs);
    }
    catch (Exception ex) {
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
    }
    catch (Exception ex) {
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
    }
    catch (Exception ex) {
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
     //if (provider.equals(TaskProvider.GLOBUS_2_PROVIDER) && use_gatekeeper) {
     // queue += "@" + remote_host.substring(0, remote_host.indexOf("."));
     //    }
     //else if (provider.equals(TaskProvider.GLOBUS_4_PROVIDER) && use_gatekeeper) {
     // queue += "@" + remote_host.substring(0, remote_host.indexOf("."));
     //    }

    jobDescription.setQueue(queue);

    // Stdout & stderr

    String stdout = submitGaussianPanel1.getStdoutFileName().trim();
    String stderr = submitGaussianPanel1.getStderrFileName().trim();

    if (provider.equals(TaskProvider.SSH_PROVIDER)) {
      if (stdout.length() > 0) {
        jobDescription.setStdout(stdout);
      }
      if (stderr.length() > 0) {
        jobDescription.setStderr(stderr);
      }
    }

    // --- Setup software

    jobDescription.setSoftware(pBS_Panel1.getSoftware());

    //input_file.substring(0, input_file.lastIndexOf('.')) +
    //    ".log";

    jobDescription.setScheduler(JobDescription.PBS_SCHEDULER);

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
                                 trim() +
                                 script_file);
    }
    catch (Exception ex) {
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

    if (provider.equals(TaskProvider.GLOBUS_2_PROVIDER)) {
      //jobDescription.setExecutable("/bin/csh");
      //jobDescription.addArgument(script_file);
      if (use_gatekeeper) {
        submitDescription.setRemoteHost(gatekeeper);
        jobDescription.setRemoteDirectory(gatekeeper);
      }
      else {
        submitDescription.setRemoteHost(remote_host);
        jobDescription.setRemoteDirectory(remote_host);
      }
      GatekeeperPanel gkp = (GatekeeperPanel) TaskProvider.getProvider(provider);
      submitDescription.setClusterAddress(gkp.HostComboBox.getSelectedItem().
                                          toString());
      submitDescription.enableGASS(gkp.gassCheckBox.isSelected());
      jobDescription.enableGASS(gkp.gassCheckBox.isSelected());
    }

    else if (provider.equals(TaskProvider.GLOBUS_4_PROVIDER)) {
      //jobDescription.setExecutable("/bin/csh");
      //jobDescription.addArgument(script_file);
      if (use_gatekeeper) {
        submitDescription.setRemoteHost(gatekeeper);
        submitDescription.setClusterAddress(remote_host);
        jobDescription.setRemoteDirectory(gatekeeper);
      }
      else {
        submitDescription.setRemoteHost(remote_host);
        jobDescription.setRemoteDirectory(remote_host);
      }
      GT4Panel gkp4 = (GT4Panel) TaskProvider.getProvider(provider);
      submitDescription.setClusterAddress(gkp4.HostComboBox.getSelectedItem().
                                          toString());
      submitDescription.setSoftware(pBS_Panel1.getSoftware());
      if (jobFS > 0) {
        submitDescription.setJobFS(jobFS);
      }
      if (maxMemory > 0) {
        submitDescription.setMaxMemory(maxMemory);
      }
      if (maxWallTime > 0) {
        submitDescription.setMaxWallTime(maxWallTime/60);
      }

      submitDescription.setPbsJobScript(false);
    }

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

    else if (provider.equals(TaskProvider.LOCAL_PROVIDER)) {

    }

    // --- Saving settings

    pBS_Panel1.saveCurrentSettings();

    // --- Submitting job

    String jobId = null;
    TaskProvider task = new TaskProvider();
    try {
      task.submitJob(submitDescription);
      //task.submitJob(jobDescription);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
                                    "Error submitting job: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    JOptionPane.showMessageDialog(null, "Job submitted successfully",
                                  "Success",
                                  JOptionPane.INFORMATION_MESSAGE);

    jobId = task.getJobHandle();

    Date subDate = new Date();

    CheckPoint chkp = null;

    if (jobId == null) {
      System.err.println(
          "No job handle was returned. Job is not included into checkpoint file");
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

    try {
      chkp = new CheckPoint(jobNameTag + "@" + jobName + "@" +
                            handleTag + "@" + jobId + "@" +
                            providerTag + "@" + provider + "@" +
                            computerTag + "@" +
                            remote_host + "@" +
                            softwareTag + "@Gaussian@" +
                            programTag + "@g03@" +
                            executableTag + "@" + script_file + "@" +
                            localExecutableTag + "@true@" +
                            remoteDirectoryTag + "@" +
                            submitGaussianPanel1.RemoteDirTextField.getText().
                            trim() + "@" +
                            localDirectoryTag + "@" +
                            submitGaussianPanel1.LocalDirTextField.getText().
                            trim() + "@" +
                            statusTag + "@Submitted@" +
                            overallJobStatusTag + "@Pending@" +
                            submittedTimeTag + "@" + subDate.toString(),
                            "@" // Divider
          );
      chkp.setOutputFile(gaussianOutputFileTag, output_file);

      if (provider.equals(TaskProvider.SSH_PROVIDER)) {
        if (stdout.length() > 0) {
          chkp.setOutputFile(stdOutputTag, stdout);
        }
        if (stderr.length() > 0) {
          chkp.setOutputFile(stdErrorTag, stderr);
        }
      }

    }
    catch (MalformedCheckPointException ex) {
      System.err.println(ex.getMessage());
      return;
    }

    ParseCheckpointFile pcf = new ParseCheckpointFile();
    try {
      pcf.addCheckPoint(chkp);
    }
    catch (cct.grid.MalformedCheckPointException ex) {
      JOptionPane.showMessageDialog(null,
                                    ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

  }

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

    public void run() {
      if (!dialog.isVisible()) {
        dialog.setVisible(true);
      }
      progressBar.setValue( (progressBar.getValue() + 1) % MAX);
    }

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
      }
      catch (Exception e) {
        exception = e;
        done = true;
        return;
      }


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

 class SubmitGaussianDialog_providerComboBox_actionAdapter
    implements ActionListener {
  private SubmitGaussianDialog adaptee;
  SubmitGaussianDialog_providerComboBox_actionAdapter(SubmitGaussianDialog
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.providerComboBox_actionPerformed(e);
  }
 }

 class SubmitGaussianDialog_getProxy_Button_actionAdapter
    implements ActionListener {
  private SubmitGaussianDialog adaptee;
  SubmitGaussianDialog_getProxy_Button_actionAdapter(SubmitGaussianDialog
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.getProxy_Button_actionPerformed(e);
  }
 }

 class SubmitGaussianDialog_Submit_Button_actionAdapter
    implements ActionListener {
  private SubmitGaussianDialog adaptee;
  SubmitGaussianDialog_Submit_Button_actionAdapter(SubmitGaussianDialog
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.Submit_Button_actionPerformed(e);
  }
 }

 class SubmitGaussianDialog_Cancel_Button_actionAdapter
    implements ActionListener {
  private SubmitGaussianDialog adaptee;
  SubmitGaussianDialog_Cancel_Button_actionAdapter(SubmitGaussianDialog
      adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.Cancel_Button_actionPerformed(e);
  }

 }

 */
