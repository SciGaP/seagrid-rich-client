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

import cct.GlobalSettings;
import cct.globus.ui.GridProxyInitDialog;
import cct.grid.*;
import cct.grid.ui.*;
import cct.interfaces.FileChooserInterface;
import cct.tools.IOUtils;
import cct.tools.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

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
enum CustomOptions {

  executable
}

public class SubmitGaussianDialogNew
    extends JDialog implements ScriptSubmitterDialogInterface {

  static public final String SCRIPT = "script";
  private HashMap<String, String> scripts = new HashMap<String, String>();
  private JPanel MainPanel = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private SubmitGaussianPanel submitGaussianPanel = new SubmitGaussianPanel();
  private JPanel ButtonPanel = new JPanel();
  private JButton Cancel_Button = new JButton();
  private JButton Submit_Button = new JButton();
  static String devider = "@";
  JComboBox providerComboBox = new JComboBox();
  JPanel providerPanel = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel providerLabel = new JLabel();
  ProgressDialog progressDialog = null;
  //private GridProxyInterface getGridProxy = null;
  int taskType = 0;
  private static Logger logger = Logger.getLogger(SubmitGaussianDialogNew.class.getName());
  Map<String, GridProviderInterface> allProviders = new LinkedHashMap<String, GridProviderInterface>();
  JobOutputInterface jobOutput = null;
  JPanel cardPanel = new JPanel();
  JPanel comboPanel = new JPanel();
  FlowLayout flowLayout2 = new FlowLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  CardLayout cardLayout1 = new CardLayout();
  JLabel schedulerLabel = new JLabel();
  JComboBox schedulerComboBox = new JComboBox();
  ResourcesPanel resourcesPanel = new ResourcesPanel();
  JPanel topPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  PBSPanel pbsPanel = new PBSPanel();
  Border border1 = BorderFactory.createLineBorder(Color.darkGray, 2);
  Border border2 = new TitledBorder(border1, "PBS Parameters");
  JButton submitWithEditButton = new JButton();
  EditScriptDialog editScriptDialog = new EditScriptDialog(new Frame(),
      "Edit Script before Submission", true);

  public static void main(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
          exception.printStackTrace();
        }

        SubmitGaussianDialogNew frame = new SubmitGaussianDialogNew();
        frame.setTaskProviders(TaskProvider.getAvailableTaskProviders());
        frame.validate();
        frame.pack();
        frame.Cancel_Button.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setVisible(true);

      }
    });
  }

  public ScriptSubmitterDialogInterface newInstance() {
    return new SubmitGaussianDialogNew(new Frame(), null, "Submit Gaussian Script", false);
  }

  public JDialog getDialog() {
    return this;
  }

  /**
   * Parses a string with custom options. Options are divided with "@".
   * @param options String - options
   * @throws Exception
   */
  public void setCustomOptions(String options) throws Exception {
    if (options == null || options.trim().length() == 0) {
      throw new Exception("No parameters");
    }
    String[] tokens = options.split(devider);
    int count = 0;
    while (count < tokens.length) {
      if (count < tokens.length && tokens[count].startsWith(CustomOptions.executable.name())) {
        String[] opts = tokens[count].split("\\s*=\\s*");
        if (opts.length < 2) {
          logger.severe("Valid form: " + CustomOptions.executable.name() + " = exec-name. Got " + tokens[count]);
          logger.severe("Entire string: " + options);
          throw new Exception("Valid form: " + CustomOptions.executable.name() + " = exec-name. Got " + tokens[count]);
        }
        submitGaussianPanel.setExecutable(opts[1]);
        logger.info("Setting executable to " + opts[1]);
        ++count;
        continue;
      }
      logger.severe("Unknown option " + tokens[count]);
      logger.severe("Entire string: " + options);
      throw new Exception("Unknown option " + tokens[count]);
    }
  }

  public void setLocalDirectory(String localDir) {
    submitGaussianPanel.LocalDirTextField.setText(localDir);
  }

  public void setRemoteDirectory(String remoteDir) {
    submitGaussianPanel.RemoteDirTextField.setText(remoteDir);
  }

  public SubmitGaussianDialogNew(Frame owner, JobOutputInterface redir, String title, boolean modal) {
    super(owner, title, modal);
    jobOutput = redir;
    try {
      jbInit();
      setDefaultCloseOperation(HIDE_ON_CLOSE);
      pack();

      String className = this.getClass().getCanonicalName();
      try {
        Properties props = GlobalSettings.getCustomProperties();

        if (props != null) {

          //--- Default enable extensions

          Set<String> properties = props.stringPropertyNames();
          Iterator<String> iter = properties.iterator();
          String key = className + GlobalSettings.DIVIDER + SCRIPT;
          logger.info("\nIterating through properties...");
          while (iter.hasNext()) {
            String p = iter.next();
            logger.info("Property: " + p);
            if (!p.startsWith(key)) {
              continue;
            }

            String new_key = "initial_new_KEY";
            try {
              new_key = buildKey(p);
              String body = loadScript(props.getProperty(p));
              if (scripts.containsKey(new_key)) {
                logger.warning("Warning: script for key " + new_key + " already exists. Overwriting it...");
              }
              scripts.put(new_key, body);
              logger.info("\nKey: " + new_key + " script file: " + props.getProperty(p) + "\n=====\n" + body + "\n=====");
            } catch (Exception ex) {
              logger.severe("Error: Cannot load script " + props.getProperty(p) + " for key " + new_key + ": " + ex.getMessage());
            }
          }

          String enableExt = props.getProperty(className + GlobalSettings.DIVIDER + SCRIPT);
          if (enableExt != null) {
            Boolean editable;
            try {
              editable = Boolean.parseBoolean(enableExt);
            } catch (Exception exx) {
            }
          }
        }
      } catch (Exception ex) {
        //System.err.println("Warning: cannot open " + CUSTOM_PROPERTY_FILE +
        //                   " : " +
        //                   ex.getMessage());
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public SubmitGaussianDialogNew() {
    this(new Frame(), null, "Submit Gaussian Script", false);
  }

  // public void setProxyHandler(GridProxyInterface proxyHandler) {
  //  getGridProxy = proxyHandler;
  //}
  private void jbInit() throws Exception {
    MainPanel.setLayout(borderLayout1);
    submitGaussianPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.darkGray, 1), "Job Specifications"));
    Cancel_Button.setToolTipText("Cancel Submission");
    Cancel_Button.setText("Cancel");
    Cancel_Button.addActionListener(new SubmitGaussianDialogNew_Cancel_Button_actionAdapter(this));
    Submit_Button.setToolTipText("Submit Job for Execution");
    Submit_Button.setText("Submit");
    Submit_Button.addActionListener(new SubmitGaussianDialogNew_Submit_Button_actionAdapter(this));
    submitWithEditButton.addActionListener(new SubmitGaussianDialogNew_Submit_Button_actionAdapter(this));
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    providerLabel.setToolTipText("");
    providerLabel.setText(" Provider: ");
    comboPanel.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    providerPanel.setLayout(borderLayout2);
    cardPanel.setLayout(cardLayout1);
    providerComboBox.addActionListener(new SubmitGaussianDialogNew_providerComboBox_actionAdapter(this));
    schedulerLabel.setToolTipText("");
    schedulerLabel.setText(" Scheduler: ");
    cardPanel.setMinimumSize(new Dimension(255, 150));
    schedulerComboBox.setEnabled(false);
    topPanel.setLayout(borderLayout3);
    pbsPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.darkGray, 1), "PBS Parameters"));
    submitWithEditButton.setToolTipText("Edit Script Before Submission");
    submitWithEditButton.setText("Edit & Submit");
    //MainPanel.add(taskProviderPanel, java.awt.BorderLayout.NORTH);
    this.getContentPane().add(MainPanel, BorderLayout.CENTER);
    ButtonPanel.add(Submit_Button);
    ButtonPanel.add(submitWithEditButton);
    ButtonPanel.add(Cancel_Button);
    this.getContentPane().add(ButtonPanel, BorderLayout.SOUTH);
    comboPanel.add(providerLabel);
    comboPanel.add(providerComboBox);
    comboPanel.add(schedulerLabel);
    comboPanel.add(schedulerComboBox);
    providerPanel.add(cardPanel, BorderLayout.CENTER);
    providerPanel.add(comboPanel, BorderLayout.NORTH);
    topPanel.add(providerPanel, BorderLayout.CENTER);
    MainPanel.add(pbsPanel, BorderLayout.CENTER);
    MainPanel.add(resourcesPanel, BorderLayout.NORTH);
    this.getContentPane().add(topPanel, BorderLayout.NORTH);
    MainPanel.add(submitGaussianPanel, BorderLayout.SOUTH);
    WindowListener l = new WindowAdapter() {

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

    editScriptDialog.setLocationRelativeTo(this);
    editScriptDialog.setSize(320, 480);

    this.validate();
  }

  public void enableLocalDir(boolean enable) {
    submitGaussianPanel.LocalDirTextField.setEnabled(enable);
  }

  public void enableInputFile(boolean enable) {
    submitGaussianPanel.inputFileTextField.setEnabled(enable);
  }

  public void setLocalDir(String locDir) {
    submitGaussianPanel.LocalDirTextField.setText(locDir);
  }

  public void setInputFile(String inputFile) {
    submitGaussianPanel.setInputFile(inputFile);
  }

  public void setGatekeepers(String[] gk) {
  }

  public void setHosts(String[] gk) {
  }

  public void Cancel_Button_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

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

    String provider = providerComboBox.getSelectedItem().toString();
    GridProviderInterface gpi = allProviders.get(provider);
    FileChooserInterface fci = gpi.getRemoteFileChooser();
    submitGaussianPanel.setRemoteFileChooser(fci);

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

    // --- test all data

    try {
      resourcesPanel.validateValues();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Wrong input data: " + ex.getMessage(), "Error in input data", JOptionPane.ERROR_MESSAGE);
      return;
    }

    boolean submitWithEdit = false;
    if (e.getSource() == submitWithEditButton) {
      submitWithEdit = true;
    }

    String provider = providerComboBox.getSelectedItem().toString();

    GridProviderInterface taskProvider = allProviders.get(provider);

    String scheduler = schedulerComboBox.getSelectedItem().toString();
    boolean use_gatekeeper = false;
    String gatekeeper = null;
    String remote_host = taskProvider.getRemoteHost();
    // --- Get Script name (executable)
    String script_file = submitGaussianPanel.ScriptTextField.getText().trim();

    String executable_file = submitGaussianPanel.getExecutable();

    JobDescription jobDescription = new JobDescription();
    JobDescription submitDescription = new JobDescription();

    // --- Set Remote directory

    String remoteDirectory = submitGaussianPanel.RemoteDirTextField.getText().trim();

    // --- Job name
    String jobName = submitGaussianPanel.jobNameTextField.getText().trim();

    // --- Get Local directory
    String localDirectory = submitGaussianPanel.LocalDirTextField.getText().trim();

    if (localDirectory.indexOf("\\") != -1) {
      localDirectory = "/" + localDirectory;
      try {
        //valid_dir = valid_dir.replaceAll("\\", "/");
        localDirectory = localDirectory.replace('\\', '/');
      } catch (PatternSyntaxException ex) {
        while (localDirectory.indexOf("\\") != -1) {
        }
        logger.warning("Regex error: " + localDirectory);
      }
    }

    if (localDirectory.endsWith("/")) {
      localDirectory = localDirectory.substring(0, localDirectory.length() - 1);
    }
    jobDescription.setLocalDirectory(localDirectory);

    // --- Get CPU number

    try {
      jobDescription.setNCPU(this.resourcesPanel.getNCPUs());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Setup max (wall) time

    float maxWallTimeInMins = -1;
    float time_in_secs = 0;
    try {
      time_in_secs = this.resourcesPanel.getTimeInSeconds();
      maxWallTimeInMins = time_in_secs / 60.0f;
      jobDescription.setMaxCpuTime(time_in_secs);
      jobDescription.setMaxTime(time_in_secs);
      jobDescription.setMaxWallTime(time_in_secs);

      submitDescription.setMaxWallTime(maxWallTimeInMins);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Setup max memory


    int maxMemory = 0; // in MB
    String memoryUnits = "";
    try {
      maxMemory = (int) resourcesPanel.getMaxMemoryInMb();
      memoryUnits = "MB";
      jobDescription.setMaxMemory(maxMemory);
      submitDescription.setMaxMemory(maxMemory);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Setup project

    jobDescription.setProject(pbsPanel.getProject());

    // --- Setup jobfs

    int jobFS = 0;
    if (this.pbsPanel.areExtensionsEnabled()) {
      try {
        jobFS = pbsPanel.getJobFS();
        if (jobFS > 0) {
          jobDescription.setJobFS(jobFS);
          submitDescription.setJobFS(jobFS);
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }

    String input_file = submitGaussianPanel.inputFileTextField.getText().trim();
    String output_file = submitGaussianPanel.OutputFileTextField.getText().trim();

    // --- Setup queue

    String queue = this.pbsPanel.getQueue();

    jobDescription.setQueue(queue);

    // Stdout & stderr

    String stdout = submitGaussianPanel.getStdoutFileName().trim();
    String stderr = submitGaussianPanel.getStderrFileName().trim();

    //if (provider.equals(TaskProvider.SSH_PROVIDER)) {
    if (stdout.length() > 0) {
      jobDescription.setStdout(stdout);
      submitDescription.setStdout(stdout);
    }
    if (stderr.length() > 0) {
      jobDescription.setStderr(stderr);
      submitDescription.setStderr(stderr);
    }
    //}


    // --- Setup software

    if (this.pbsPanel.areExtensionsEnabled()
        && pbsPanel.getSoftware().length() > 0) {
      jobDescription.setSoftware(pbsPanel.getSoftware());
      submitDescription.setSoftware(pbsPanel.getSoftware());

    }
    // --- Setup scheduler

    String sched = this.schedulerComboBox.getSelectedItem().toString();
    if (taskProvider.isPassOptionsToScheduler()) {
      jobDescription.setScheduler(sched);
    }

    java.util.List<String> prepDirs = this.submitGaussianPanel.getPreprocessDirectives();
    for (int i = 0; i < prepDirs.size(); i++) {
      jobDescription.addPreprocessDirective(prepDirs.get(i));
    }
    //jobDescription.addPreprocessDirective("module load dos2unix");
    //jobDescription.addPreprocessDirective("dos2unix " + input_file);
    //jobDescription.addPreprocessDirective("module load gaussian");

    jobDescription.setExecutable(executable_file);

    jobDescription.addArgument("<" + input_file);
    jobDescription.addArgument(">" + output_file);

    jobDescription.setPBSOther("jamberoo");

    jobDescription.setShell("/bin/bash");

    // --- Save script file

    try {
      String scriptContent = "If you see this text something is wrong with script generation...";
      if (scripts.size() < 1) {

        scriptContent = JobDescription.createUnixScript(jobDescription);

      } else {
        Map<String, String> patterns = new HashMap<String, String>();
        patterns.put("@@JOBNAME@@", jobName);
        patterns.put("@@EXECUTABLE@@", executable_file);
        patterns.put("@@INPUT@@", input_file);
        patterns.put("@@OUTPUT@@", output_file);
        patterns.put("@@STDOUT@@", stdout);
        patterns.put("@@STDERR@@", stderr);
        patterns.put("@@NCPUS@@", String.valueOf(resourcesPanel.getNCPUs()));
        patterns.put("@@WALLTIME@@", Utils.timeInHours(time_in_secs));
        patterns.put("@@MEMORY@@", String.valueOf(maxMemory));
        patterns.put("@@MEMORY-UNITS@@", "MB");

        String template = scripts.get("");
        scriptContent = JobDescription.createUnixScript(jobDescription, template, patterns);
      }

      if (submitWithEdit) {
        editScriptDialog.scriptTextArea.setText(scriptContent);
        editScriptDialog.setVisible(true);
        if (!editScriptDialog.submitPressed) {
          return;
        }
        scriptContent = editScriptDialog.scriptTextArea.getText();
      }

      IOUtils.saveStringIntoFile(scriptContent, submitGaussianPanel.LocalDirTextField.getText().trim() + script_file);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "Error saving script file" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    jobDescription.setTaskProvider(provider);
    jobDescription.setScheduler(JobDescription.PBS_SCHEDULER);

    // --- Preparing final job description for submitting

    submitDescription.setTaskProvider(provider);
    submitDescription.setScheduler(JobDescription.PBS_SCHEDULER);
    submitDescription.setPbsJobScript(true);
    submitDescription.setExecutable(script_file);
    submitDescription.setRemoteHost(remote_host);
    submitDescription.setRemoteDirectory(remoteDirectory);
    submitDescription.setJobName(jobName);
    submitDescription.setLocalDirectory(localDirectory);
    submitDescription.setProject(this.pbsPanel.getProject());
    submitDescription.setQueue(queue);

    // --- ( File Staging In )

    submitDescription.setFileStageIn(script_file, script_file); // Run script file
    submitDescription.setFileStageIn(input_file, input_file); // Data input file

    // --- File staging out....

    submitDescription.setFileStageOut(output_file, output_file);

    submitDescription.setExecutable(script_file);
    submitDescription.setLocalExecutable(true);

    // --- Non-standard

    submitDescription.setPbsJobScript(taskProvider.isPassOptionsToScheduler());

    if (provider.equals(TaskProvider.LOCAL_PROVIDER)) {
    } // --- Submitting job

    String jobId = null;
    //TaskProvider task = new TaskProvider();
    try {
      jobId = taskProvider.submitTask(submitDescription);
      //task.submitJob(submitDescription);
    } catch (Exception ex) {
      //progressDialog.setVisible(false);
      //progress.cancelShow();
      String message = "Error submitting job: " + ex.getMessage();
      System.err.println(message);
      JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    //progressDialog.setVisible(false);

    //progress.cancelShow();

    JOptionPane.showMessageDialog(null, "Job submitted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

    //jobId = task.getJobHandle();

    Date subDate = new Date();
    CheckPointInterface chkp = null;

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
          + CheckPointInterface.programTag + "@g09@"
          + CheckPointInterface.executableTag + "@" + script_file + "@"
          + CheckPointInterface.localExecutableTag + "@true@"
          + CheckPointInterface.remoteDirectoryTag + "@"
          + submitGaussianPanel.RemoteDirTextField.getText().
          trim() + "@"
          + CheckPointInterface.localDirectoryTag + "@"
          + submitGaussianPanel.LocalDirTextField.getText().
          trim() + "@"
          + CheckPointInterface.statusTag + "@Submitted@"
          + CheckPointInterface.overallJobStatusTag + "@Pending@"
          + CheckPointInterface.submittedTimeTag + "@" + subDate.toString(),
          "@" // Divider
          );
      chkp.setOutputFile(SubmitGaussianPanel.gaussianOutputFileTag, output_file);

      //if (provider.equals(TaskProvider.SSH_PROVIDER)) {
      if (stdout.length() > 0) {
        chkp.setOutputFile(CheckPointInterface.stdOutputTag, stdout);
      }
      if (stderr.length() > 0) {
        chkp.setOutputFile(CheckPointInterface.stdErrorTag, stderr);
      }

      chkp = taskProvider.setCheckPoint(chkp);
      //}

    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      return;
    }

    ParseCheckpointFile pcf = new ParseCheckpointFile();
    try {
      pcf.addCheckPoint((CheckPoint) chkp);
    } catch (cct.grid.MalformedCheckPointException ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    submitGaussianPanel.setRemoteFileChooser(fci);
    //submitGaussianPanel1.setRemoteFileChooser(ssh);
    //}

    providerComboBox.setEnabled(true);

  }

  /**
   *
   * @param raw
   * @return
   */
  public String buildKey(String raw) throws Exception {
    int index = raw.indexOf(GlobalSettings.DIVIDER + SCRIPT);
    if (index == -1) {
      throw new Exception("Property does not contain sequence: " + GlobalSettings.DIVIDER + SCRIPT);
    }
    index += (GlobalSettings.DIVIDER + SCRIPT).length() + 1;
    if (raw.length() <= index) {
      return "";
    }

    String buffer = raw.substring(index);
    if (buffer.length() < 1) {
      return "";
    }

    return buffer;
  }

  public String loadScript(String name) throws Exception {
    InputStream is = null;
    try {
      ClassLoader cl = cct.modelling.FormatManager.class.getClassLoader();
      //logger.info("Class loader: " + cl.toString());
      //builderURL = cl.getResource(file_name);
      is = cl.getResourceAsStream(name);
    } catch (Exception ex) {
      throw new Exception(this.getClass().getCanonicalName() + ": loadScript: cannot get resource : " + name + " : " + ex.getMessage());
    }

    StringWriter sw = new StringWriter();


    try {
      int symbol;
      while ((symbol = is.read()) != -1) {
        sw.write(symbol);
      }
    } catch (Exception ex) {
      throw new Exception(this.getClass().getCanonicalName() + ": loadScript: cannot read resource : " + name + " : " + ex.getMessage());
    }

    return sw.toString();
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

    public void run() {
      if (!dialog.isVisible()) {
        dialog.setVisible(true);
      }
      progressBar.setValue((progressBar.getValue() + 1) % MAX);
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

class SubmitGaussianDialogNew_providerComboBox_actionAdapter
    implements ActionListener {

  private SubmitGaussianDialogNew adaptee;

  SubmitGaussianDialogNew_providerComboBox_actionAdapter(
      SubmitGaussianDialogNew adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.providerComboBox_actionPerformed(e);
  }
}

class SubmitGaussianDialogNew_Submit_Button_actionAdapter
    implements ActionListener {

  private SubmitGaussianDialogNew adaptee;

  SubmitGaussianDialogNew_Submit_Button_actionAdapter(SubmitGaussianDialogNew adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.Submit_Button_actionPerformed(e);
  }
}

class SubmitGaussianDialogNew_Cancel_Button_actionAdapter
    implements ActionListener {

  private SubmitGaussianDialogNew adaptee;

  SubmitGaussianDialogNew_Cancel_Button_actionAdapter(SubmitGaussianDialogNew adaptee) {
    this.adaptee = adaptee;
  }

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
