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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.SystemColor;
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
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import cct.globus.ui.GridProxyInitDialog;
import cct.grid.CheckPoint;
import cct.grid.CheckPointInterface;
import cct.grid.ClientProgramInterface;
import cct.grid.GridProviderInterface;
import cct.grid.JobDescription;
import cct.grid.ParseCheckpointFile;
import cct.grid.SchedulerInterface;
import cct.grid.ScriptSubmitterDialogInterface;
import cct.grid.TaskProvider;
import cct.grid.TaskScheduler;
import cct.grid.ui.gaussian.SubmitGaussianPanel;
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
public class SubmitTaskDialog
    extends JDialog implements ScriptSubmitterDialogInterface {

  JPanel MainPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  ClientProgramInterface ClientProgram = null;
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
  private static Logger logger = Logger.getLogger(SubmitTaskDialog.class.getName());

  Map<String, GridProviderInterface> allProviders = new LinkedHashMap<String, GridProviderInterface> ();
  Map<String, SchedulerInterface> allSchedulers = new LinkedHashMap<String, SchedulerInterface> ();

  JobOutputInterface jobOutput = null;
  JPanel cardPanel = new JPanel();
  JPanel schedulerCardPanel = new JPanel();
  JPanel comboPanel = new JPanel();
  FlowLayout flowLayout2 = new FlowLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  CardLayout cardLayout1 = new CardLayout();
  CardLayout cardLayout2 = new CardLayout();
  JLabel schedulerLabel = new JLabel();
  JComboBox schedulerComboBox = new JComboBox();
  ResourcesPanel resourcesPanel = new ResourcesPanel();
  JPanel topPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  //PBSPanel pbsPanel = new PBSPanel();
  Border border1 = BorderFactory.createLineBorder(Color.darkGray, 2);
  Border border2 = new TitledBorder(border1, "PBS Parameters");
  JButton submitWithEditButton = new JButton();
  EditScriptDialog editScriptDialog = new EditScriptDialog(new Frame(),
      "Edit Script before Submission", true);

  public static void main(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
          exception.printStackTrace();
        }

        ClientProgramInterface client_program = new SubmitGaussianPanel();
        SubmitTaskDialog frame = new SubmitTaskDialog(null, client_program, null, "Task Submission", true);

        Map<String, SchedulerInterface> schedulers = new LinkedHashMap<String, SchedulerInterface> ();
        SchedulerInterface pbs = new PBSPanel();
        schedulers.put("pbs", pbs);
        pbs = new NoSchedulerPanel();
        schedulers.put("none", pbs);
        frame.setSchedulers(schedulers);

        frame.setTaskProviders(TaskProvider.getAvailableTaskProviders());
        frame.validate();
        frame.pack();
        frame.Cancel_Button.addActionListener(new ActionListener() {
          @Override
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

  @Override
  public JDialog getDialog() {
    return this;
  }

  @Override
  public ScriptSubmitterDialogInterface newInstance() {
    return new SubmitTaskDialog(new Frame(), null, null, "Submit Gaussian Script", false);
  }

  @Override
  public void setCustomOptions(String options) throws Exception {
    throw new Exception("setCustomOptions is not implemented in " + this.getClass().getCanonicalName());
  }

  @Override
  public void setLocalDirectory(String localDir) {
    ClientProgram.setLocalDirectory(localDir);
    //submitGaussianPanel.LocalDirTextField.setText(localDir);
  }

  @Override
  public void setRemoteDirectory(String remoteDir) {
    ClientProgram.setRemoteDirectory(remoteDir);
    //submitGaussianPanel.RemoteDirTextField.setText(remoteDir);
  }

  public JButton getCancelButton() {
    return this.Cancel_Button;
  }

  public SubmitTaskDialog(Frame owner, ClientProgramInterface client_program, JobOutputInterface redir,
                          String title, boolean modal) {
    super(owner, title, modal);
    jobOutput = redir;
    ClientProgram = client_program;
    ClientProgram.setResourcesInterface(resourcesPanel);

    try {
      setDefaultCloseOperation(HIDE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private SubmitTaskDialog() {
    this(new Frame(), null, null, "Submit Gaussian Script", false);
  }

  // public void setProxyHandler(GridProxyInterface proxyHandler) {
  //  getGridProxy = proxyHandler;
  //}

  private void jbInit() throws Exception {
    MainPanel.setLayout(borderLayout1);
    //submitGaussianPanel.setBorder(new TitledBorder(BorderFactory.
    //    createLineBorder(Color.darkGray, 1), "Job Specifications"));
    Cancel_Button.setToolTipText("Cancel Submission");
    Cancel_Button.setText("Cancel");
    Cancel_Button.addActionListener(new
                                    SubmitTaskDialog_Cancel_Button_actionAdapter(this));
    Submit_Button.setToolTipText("Submit Job for Execution");
    Submit_Button.setText("Submit");
    Submit_Button.addActionListener(new
                                    SubmitTaskDialog_Submit_Button_actionAdapter(this));
    submitWithEditButton.addActionListener(new
                                           SubmitTaskDialog_Submit_Button_actionAdapter(this));
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    providerLabel.setToolTipText("");
    providerLabel.setText(" Provider: ");
    comboPanel.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    providerPanel.setLayout(borderLayout2);
    cardPanel.setLayout(cardLayout1);
    schedulerCardPanel.setLayout(cardLayout2);
    providerComboBox.addActionListener(new
                                       SubmitTaskDialog_providerComboBox_actionAdapter(this));
    schedulerLabel.setToolTipText("");
    schedulerLabel.setText(" Scheduler: ");
    cardPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1), "Task Provider"));
    cardPanel.setMinimumSize(new Dimension(255, 150));
    schedulerComboBox.setEnabled(false);
    schedulerComboBox.addActionListener(new
                                        SubmitTaskDialog_schedulerComboBox_actionAdapter(this));
    topPanel.setLayout(borderLayout3);
    //pbsPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.
    //    darkGray, 1), "PBS Parameters"));
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
    MainPanel.add(schedulerCardPanel, BorderLayout.CENTER);
    MainPanel.add(resourcesPanel, BorderLayout.NORTH);
    this.getContentPane().add(topPanel, BorderLayout.NORTH);
    if (ClientProgram == null) {
      MainPanel.add(new JPanel(), BorderLayout.SOUTH);
    }
    else {
      MainPanel.add(ClientProgram.getVisualComponent(), BorderLayout.SOUTH);
    }
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

    progressDialog = new ProgressDialog(new Frame(),
                                        "Task Submission Progress", false);
    progressDialog.cancelButton.setVisible(false);
    progressDialog.setAlwaysOnTop(true);
    progressDialog.setLocationRelativeTo(this);

    editScriptDialog.setLocationRelativeTo(this);
    editScriptDialog.setSize(320, 480);

    schedulerCardPanel.add(new JPanel(), "Test");
    cardPanel.add(new JPanel(), "Test");

    this.validate();
  }

  public void enableLocalDir(boolean enable) {
    ClientProgram.enableLocalDir(enable);
  }

  public void enableInputFile(boolean enable) {
    ClientProgram.enableInputFile(enable);
  }

  public void setLocalDir(String locDir) {
    ClientProgram.setLocalDirectory(locDir);
    //submitGaussianPanel.LocalDirTextField.setText(locDir);
  }

  @Override
  public void setInputFile(String inputFile) {
    ClientProgram.setInputFile(inputFile);
  }

  public void setGatekeepers(String[] gk) {

  }

  public void setHosts(String[] gk) {
  }

  public void Cancel_Button_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  public void setClientProgram(ClientProgramInterface client_program) {
    ClientProgram = client_program;
  }

  @Override
  public void setTaskProviders(Map providers) {
    providerComboBox.setEnabled(false);
    providerComboBox.removeAllItems();
    cardPanel.removeAll();
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
    ClientProgram.setTaskProviderInterface(gpi);

    this.validate();
  }

  public void setSchedulers(Map<String, SchedulerInterface> providers) {
    schedulerComboBox.setEnabled(false);
    schedulerCardPanel.removeAll();
    schedulerComboBox.removeAllItems();

    allSchedulers = new LinkedHashMap(providers);

    Set set = providers.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String schedulerName = me.getKey().toString();
      schedulerComboBox.addItem(schedulerName);
      //Component panel = (Component) me.getValue();
      SchedulerInterface scheduler = (SchedulerInterface) me.getValue();
      Component panel = scheduler.getVisualComponent();
      panel.validate();

      schedulerCardPanel.add(panel, schedulerName);
    }

    schedulerComboBox.setSelectedIndex(0);

    CardLayout cl = (CardLayout) schedulerCardPanel.getLayout();
    cl.show(schedulerCardPanel, schedulerComboBox.getSelectedItem().toString());
    schedulerCardPanel.validate();
    schedulerCardPanel.updateUI();

    schedulerComboBox.setEnabled(true);

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
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Wrong input data: " + ex.getMessage(), "Error in input data",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    boolean submitWithEdit = false;
    if (e.getSource() == submitWithEditButton) {
      submitWithEdit = true;
    }

    String provider = providerComboBox.getSelectedItem().toString();

    GridProviderInterface taskProvider = allProviders.get(provider);

    String scheduler = schedulerComboBox.getSelectedItem().toString();
    SchedulerInterface schedulerProvider = allSchedulers.get(scheduler);

    // --- "Inform" scheduler about chosen resources...

    schedulerProvider.setResourcesProvider(resourcesPanel);

    // --- "Inform" client program about task provider

    ClientProgram.setTaskProviderInterface(taskProvider);

    JobDescription jobDescription = new JobDescription();
    JobDescription submitDescription = new JobDescription();

    // --- Start to build job description for submission...

    try {
      resourcesPanel.setJobDescription(submitDescription);
      schedulerProvider.setJobDescription(submitDescription);
      ClientProgram.setJobDescription(submitDescription);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "Cannot create job description object: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Start to build a run script

    String script_file = ClientProgram.getScriptFile();
    try {
      String scriptContent = JobDescription.createRunScript(taskProvider, schedulerProvider, ClientProgram);
      if (submitWithEdit) {
        editScriptDialog.scriptTextArea.setText(scriptContent);
        editScriptDialog.setVisible(true);
        if (!editScriptDialog.submitPressed) {
          return;
        }
        scriptContent = editScriptDialog.scriptTextArea.getText();
      }

      IOUtils.saveStringIntoFile(scriptContent, ClientProgram.getLocalDirectory() + script_file);

    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "Cannot create script file: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Preparing final job description for submitting

    submitDescription.setTaskProvider(provider);
    submitDescription.setScheduler(scheduler);

    //submitDescription.setRemoteHost(remote_host); // Task provider already knows about that...




    // --- Non-standard

    // submitDescription.setPbsJobScript(taskProvider.isPassOptionsToScheduler()); /// Disabled

    // --- Submitting job

    String jobId = null;

    try {
      jobId = taskProvider.submitTask(submitDescription);
    }
    catch (Exception ex) {
      String message = "Error submitting job: " + ex.getMessage();
      JOptionPane.showMessageDialog(null, "Error submitting job: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      System.err.println(message);
      return;
    }

    JOptionPane.showMessageDialog(null, "Job submitted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

    Date subDate = new Date();

    CheckPointInterface chkp = null;

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

    try {
      chkp = new CheckPoint(CheckPointInterface.jobNameTag + "@" + ClientProgram.getJobName() + "@" +
                            CheckPointInterface.handleTag + "@" + jobId + "@" +
                            CheckPointInterface.providerTag + "@" + provider + "@" +
                            CheckPointInterface.computerTag + "@" +
                            taskProvider.getRemoteHost() + "@" +
                            CheckPointInterface.softwareTag + "@" + ClientProgram.getSoftwareName() + "@" +
                            CheckPointInterface.programTag + "@" + ClientProgram.getExecutable() + "@" +
                            CheckPointInterface.executableTag + "@" + script_file + "@" +
                            CheckPointInterface.localExecutableTag + "@true@" +
                            CheckPointInterface.remoteDirectoryTag + "@" +
                            ClientProgram.getRemoteDirectory() +
                            "@" +
                            CheckPointInterface.localDirectoryTag + "@" +
                            ClientProgram.getLocalDirectory() +
                            "@" +
                            CheckPointInterface.statusTag + "@Submitted@" +
                            CheckPointInterface.overallJobStatusTag + "@Pending@" +
                            CheckPointInterface.submittedTimeTag + "@" + subDate.toString(),
                            "@" // Divider
          );

      ClientProgram.setCheckPointFile( (CheckPoint) chkp);

      if (ClientProgram.getStdoutFileName() != null && ClientProgram.getStdoutFileName().length() > 0) {
        chkp.setOutputFile(CheckPointInterface.stdOutputTag, ClientProgram.getStdoutFileName());
      }
      if (ClientProgram.getStderrFileName() != null && ClientProgram.getStderrFileName().length() > 0) {
        chkp.setOutputFile(CheckPointInterface.stdErrorTag, ClientProgram.getStderrFileName());
      }

      chkp = taskProvider.setCheckPoint(chkp);

    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
      return;
    }

    ParseCheckpointFile pcf = new ParseCheckpointFile();
    try {
      pcf.addCheckPoint( (CheckPoint) chkp); /// !!! Bad !!!
    }
    catch (cct.grid.MalformedCheckPointException ex) {
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
    ClientProgram.setTaskProviderInterface(gpi);
    //ClientProgram.setRemoteFileChooser(fci);

    providerComboBox.setEnabled(true);
    pack();
  }

  public void schedulerComboBox_actionPerformed(ActionEvent e) {
    if (!schedulerComboBox.isEnabled()) {
      return;
    }
    schedulerComboBox.setEnabled(false);

    CardLayout cl = (CardLayout) schedulerCardPanel.getLayout();
    cl.show(schedulerCardPanel, schedulerComboBox.getSelectedItem().toString());

    String provider = schedulerComboBox.getSelectedItem().toString();
    SchedulerInterface si = allSchedulers.get(provider);
    schedulerComboBox.setEnabled(true);
    pack();
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
      }
      catch (Exception ex) {}
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
      progressBar.setValue( (progressBar.getValue() + 1) % MAX);
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
      }
      catch (Exception e) {
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

  private class SubmitTaskDialog_providerComboBox_actionAdapter
      implements ActionListener {
    private SubmitTaskDialog adaptee;
    SubmitTaskDialog_providerComboBox_actionAdapter(
        SubmitTaskDialog
        adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.providerComboBox_actionPerformed(e);
    }
  }

  private class SubmitTaskDialog_schedulerComboBox_actionAdapter
      implements ActionListener {
    private SubmitTaskDialog adaptee;
    SubmitTaskDialog_schedulerComboBox_actionAdapter(SubmitTaskDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.schedulerComboBox_actionPerformed(e);
    }
  }

  private class SubmitTaskDialog_Submit_Button_actionAdapter
      implements ActionListener {
    private SubmitTaskDialog adaptee;
    SubmitTaskDialog_Submit_Button_actionAdapter(SubmitTaskDialog
                                                 adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.Submit_Button_actionPerformed(e);
    }
  }

  private class SubmitTaskDialog_Cancel_Button_actionAdapter
      implements ActionListener {
    private SubmitTaskDialog adaptee;
    SubmitTaskDialog_Cancel_Button_actionAdapter(SubmitTaskDialog
                                                 adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.Cancel_Button_actionPerformed(e);
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
