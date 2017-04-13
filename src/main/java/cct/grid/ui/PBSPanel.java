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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cct.GlobalSettings;
import cct.grid.ClientProgramInterface;
import cct.grid.JobDescription;
import cct.grid.ResourcesProviderInterface;
import cct.grid.SchedulerInterface;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * Call validateValues() before querying for values
 * Setup ResourcesProviderInterface and ClientProgramInterface before calling getCommandsAsString()
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class PBSPanel
    extends JPanel implements SchedulerInterface {

  static final String ENABLE_EXTENSIONS = "enableExtensions";
  static final String ENABLE_EDIT_EXTENSIONS = "enableEditExtensions";
  static final String DEFAULT_QUEUE = "defaultQueue";
  static final String ENABLE_EDIT_QUEUE = "enableEditQueue";
  //cct.grid.ui.PBSPanel@enableExtensions = true
  //cct.grid.ui.PBSPanel@enableEditExtensions = true
  //cct.grid.ui.PBSPanel@defaultQueue = titan
  //cct.grid.ui.PBSPanel@enableEditQueue = false
  ResourcesProviderInterface resourcesProvider = null;
  ClientProgramInterface clientProgram = null;
  Preferences prefs = Preferences.userNodeForPackage(getClass());
  JLabel queueLabel = new JLabel();
  JLabel jobfsLabel = new JLabel();
  JLabel softLabel = new JLabel();
  JLabel projectLabel = new JLabel();
  public JTextField ProjectTextField = new JTextField();
  public JTextField QueueTextField = new JTextField();
  public JTextField JobfsTextField = new JTextField();
  public JTextField SoftwareTextField = new JTextField();
  public JCheckBox extCheckBox = new JCheckBox();
  static final String projectKey = "project";
  static final String queueKey = "queue";
  static final String timeKey = "time";
  static final String memoryKey = "memory";
  static final String jobfsKey = "jobfs";
  static final String softwareKey = "software";
  static final String cpuKey = "cpu";
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  BorderLayout borderLayout1 = new BorderLayout();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel jPanel2 = new JPanel();
  JPanel extensionsPanel = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();

  public PBSPanel() {
    try {
      jbInit();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public PBSPanel(String project, String queue, int time, int memory,
      int jobfs, String software) {
    try {
      jbInit();
      if (project == null) {
        setDefaultProject();
      } else {
        ProjectTextField.setText(project);
      }
      QueueTextField.setText(queue);
      JobfsTextField.setText(String.valueOf(jobfs));
      SoftwareTextField.setText(software);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    queueLabel.setToolTipText("");
    queueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    queueLabel.setText("Queue: ");
    jobfsLabel.setToolTipText("");
    jobfsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    jobfsLabel.setText("Jobfs (GB): ");
    softLabel.setToolTipText("");
    softLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    softLabel.setText("Software: ");
    projectLabel.setToolTipText("");
    projectLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    projectLabel.setText("Project: ");
    extCheckBox.setText("Enable pbs extensions");
    extCheckBox.setSelected(true);

    ProjectTextField.setToolTipText("");
    ProjectTextField.setColumns(6);
    ProjectTextField.addActionListener(new PBSPanel_ProjectTextField_actionAdapter(this));
    QueueTextField.setToolTipText("");
    QueueTextField.setText("normal");
    QueueTextField.setColumns(6);
    JobfsTextField.setToolTipText("");
    JobfsTextField.setText("5");
    JobfsTextField.setColumns(6);
    SoftwareTextField.setToolTipText("");
    SoftwareTextField.setColumns(6);
    jPanel1.setLayout(gridBagLayout1);
    extensionsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    this.add(jTabbedPane1, BorderLayout.NORTH);
    jTabbedPane1.add(jPanel1, "Basic");
    jTabbedPane1.add(extensionsPanel, "Extensions");
    extensionsPanel.add(jobfsLabel);
    extensionsPanel.add(JobfsTextField);
    extensionsPanel.add(softLabel);
    extensionsPanel.add(SoftwareTextField);
    jTabbedPane1.add(jPanel2, "Other");
    jPanel1.add(projectLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    jPanel1.add(queueLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    jPanel1.add(QueueTextField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    jPanel1.add(extCheckBox, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    jPanel1.add(ProjectTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    String value;

    setDefaultProject();

    value = prefs.get(projectKey, "");
    ProjectTextField.setText(value);

    value = prefs.get(queueKey, "normal");
    QueueTextField.setText(value);

    value = prefs.get(timeKey, "60");
    value = prefs.get(memoryKey, "1024");
    value = prefs.get(jobfsKey, "5");
    JobfsTextField.setText(value);

    value = prefs.get(softwareKey, "g03");
    SoftwareTextField.setText(value);

    value = prefs.get(cpuKey, "1");

    // ---

    URL custom = null;
    String className = this.getClass().getCanonicalName();
    try {
      custom = PBSPanel.class.getClassLoader().getResource(GlobalSettings.CCT_PROPERTY_FILE);
      Properties props = new Properties();
      props.load(custom.openStream());

      //--- Default enable extensions

      String enableExt = props.getProperty(className + GlobalSettings.DIVIDER + ENABLE_EXTENSIONS);
      if (enableExt != null) {
        Boolean editable;
        try {
          editable = Boolean.parseBoolean(enableExt);
          extCheckBox.setSelected(editable);
        } catch (Exception exx) {
        }
      }

      // --- Enable edit extension
      enableExt = props.getProperty(className + GlobalSettings.DIVIDER + ENABLE_EDIT_EXTENSIONS);
      if (enableExt != null) {
        Boolean editable;
        try {
          editable = Boolean.parseBoolean(enableExt);
          extCheckBox.setEnabled(editable);
        } catch (Exception exx) {
        }
      }

      // --- Default queue

      enableExt = props.getProperty(className + GlobalSettings.DIVIDER + DEFAULT_QUEUE);
      if (enableExt != null) {
        QueueTextField.setText(enableExt);
      }

      // --- Enable to edit queue

      enableExt = props.getProperty(className + GlobalSettings.DIVIDER + ENABLE_EDIT_QUEUE);
      if (enableExt != null) {
        Boolean editable;
        try {
          editable = Boolean.parseBoolean(enableExt);
          QueueTextField.setEditable(editable);
        } catch (Exception exx) {
        }
      }

    } catch (Exception ex) {
    }

  }

  public void saveCurrentSettings() {
    prefs.put(projectKey, ProjectTextField.getText());
    prefs.put(queueKey, QueueTextField.getText());
    prefs.put(jobfsKey, JobfsTextField.getText());
    prefs.put(softwareKey, SoftwareTextField.getText());

  }

  public void setDefaultProject() {
    String value = prefs.get(projectKey, "");
    ProjectTextField.setText(value);
  }

  @Override
  public String getProject() {
    return ProjectTextField.getText().trim();
  }

  public String getQueue() {
    return QueueTextField.getText().trim();
  }

  public String getSoftware() {
    return SoftwareTextField.getText().trim();
  }

  /*
  public RslAttributes getRSL() throws Exception {
  RslAttributes rsl = new RslAttributes();
  rsl.add("project", ProjectTextField.getText().trim());
  rsl.add("queue", QueueTextField.getText().trim());
  int test = 0;

  try {
  test = getJobFS();
  rsl.add("jobfs", JobfsTextField.getText());
  }
  catch (Exception ex) {
  throw ex;
  }

  rsl.add("software", SoftwareTextField.getText());

  return rsl;
  }
   */
  public int getJobFS() {
    if (JobfsTextField.getText().trim().length() < 1) {
      return 0;
    }
    int test = 0;
    try {
      test = (int) Float.parseFloat(JobfsTextField.getText().trim());
    } catch (Exception ex) {
      System.err.println("Internal error: Wrong value for jobfs: "
          + ex.getMessage()
          + " Use validateValues() before calling getJobFS");
      //throw new Exception("Wrong value for jobfs: " + ex.getMessage());
    } finally {
      if (test < 0) {
        System.err.println(
            "Internal error: Wrong value for jobfs: it should be positive. Use validateValues() before calling getJobFS");
        //throw new Exception("Value for time should be positive");
      }
    }
    return test;
  }

  public boolean areExtensionsEnabled() {
    return this.extCheckBox.isSelected();
  }

  public void ProjectTextField_actionPerformed(ActionEvent e) {
    String value = ProjectTextField.getText();
    try {
      prefs.put(projectKey, value);
    } catch (Exception ex) {
      System.err.println("Warning: Unable to save project: " + ex.getMessage());
    }
  }

  @Override
  public void setResourcesProvider(ResourcesProviderInterface resources) {
    resourcesProvider = resources;
  }

  public void setClientProgram(ClientProgramInterface client) {
    clientProgram = client;
  }

  /**
   * Returns time in the format [[hours:]minutes:]seconds
   * @return String
   */
  static public String getMaxWallTimeFormatted(int timeInSeconds) {
    String time = "";

    if ((timeInSeconds / 3600) > 0) {
      time += String.valueOf((timeInSeconds / 3600)) + ":";
      timeInSeconds -= (timeInSeconds / 3600) * 3600; // What left in minutes
      if ((timeInSeconds / 60) > 0) {
        String minutes = String.valueOf((timeInSeconds / 60));
        if (minutes.length() == 1) {
          minutes = "0" + minutes;
        }
        time += minutes + ":";
        timeInSeconds -= (timeInSeconds / 60) * 60; // What left in seconds
        time += timeInSeconds;
      } else {
        if (timeInSeconds >= 10) {
          time += "00:" + timeInSeconds;
        } else {
          time += "00:0" + timeInSeconds;
        }
      }
    } // --- Time is in minutes (and seconds)
    else if ((timeInSeconds / 60) > 0) {
      String minutes = String.valueOf((timeInSeconds / 60));
      if (minutes.length() == 1) {
        minutes = "0" + minutes;
      }
      time += minutes + ":";
      timeInSeconds -= (timeInSeconds / 60) * 60; // What left in seconds
      if (timeInSeconds >= 10) {
        time += timeInSeconds;
      } else {
        time += "0" + timeInSeconds;
      }

    } // --- Time in seconds only
    else {
      if (timeInSeconds >= 10) {
        time += timeInSeconds;
      } else {
        time += "0" + timeInSeconds;
      }
    }

    return time;
  }

  @Override
  public String getCommandsAsString() {
    StringWriter sWriter = new StringWriter();
    if (resourcesProvider == null) {
      System.err.println("Warning: resources provider is not set");
    }

    if (ProjectTextField.getText().trim().length() > 0) {
      sWriter.write("#PBS -P " + ProjectTextField.getText().trim() + "\n");
    }
    if (QueueTextField.getText().trim().length() > 0) {
      sWriter.write("#PBS -q " + QueueTextField.getText().trim() + "\n");
    }

    if (resourcesProvider != null) {

      sWriter.write("#PBS -l walltime="
          + getMaxWallTimeFormatted((int) resourcesProvider.getTimeInSeconds()) + "\n");
      sWriter.write("#PBS -l vmem="
          + ((int) resourcesProvider.getMaxMemoryInMb()) + "MB\n");

      sWriter.write("#PBS -l ncpus=" + resourcesProvider.getNCPUs() + "\n");

    }

    if (this.extCheckBox.isSelected()) {

      if (this.SoftwareTextField.getText().trim().length() > 0) {
        sWriter.write("#PBS -l software="
            + SoftwareTextField.getText().trim() + "\n");
      }

      if (this.JobfsTextField.getText().trim().length() > 0) {
        sWriter.write("#PBS -l jobfs=" + this.getJobFS() + "Gb\n");
      }

      if (this.SoftwareTextField.getText().trim().length() > 0) {
        sWriter.write("#PBS -l other=" + SoftwareTextField.getText().trim()
            + "\n");
      }
    }

    if (clientProgram != null) {
      if (clientProgram.getStdoutFileName() != null
          && clientProgram.getStdoutFileName().length() > 0) {
        sWriter.write("#PBS -o " + clientProgram.getStdoutFileName() + "\n");
      }
      if (clientProgram.getStderrFileName() != null
          && clientProgram.getStderrFileName().length() > 0) {
        sWriter.write("#PBS -e " + clientProgram.getStderrFileName() + "\n");
      }

    }

    sWriter.write("#PBS -wd\n\n");

    return sWriter.toString();
  }

  public void validateValues() throws Exception {

    // --- Check jobfs field

    if (JobfsTextField.getText().trim().length() > 0) {
      int test = 0;
      try {
        test = (int) Float.parseFloat(JobfsTextField.getText().trim());
      } catch (Exception ex) {
        throw new Exception("Wrong value for jobfs: " + ex.getMessage());
      } finally {
        if (test < 0) {
          throw new Exception("Value for time should be positive");
        }
      }
    }

  }

  @Override
  public Component getVisualComponent() {
    return this;
  }

  @Override
  public Object getOption(String option) {
    return null;
  }

  @Override
  public void setJobDescription(JobDescription job) throws Exception {

    // --- Setup jobfs

    int jobFS = 0;
    if (areExtensionsEnabled()) {
      try {
        jobFS = getJobFS();
        if (jobFS > 0) {
          job.setJobFS(jobFS);
        }
      } catch (Exception ex) {
        throw new Exception(ex.getMessage());
      }
    }

    // --- Setup software

    if (this.areExtensionsEnabled() && getSoftware().length() > 0) {
      job.setSoftware(getSoftware());
    }

    job.setPbsJobScript(true);
    job.setProject(this.getProject());
    job.setQueue(this.getQueue());
  }

  private class PBSPanel_ProjectTextField_actionAdapter
      implements ActionListener {

    private PBSPanel adaptee;

    PBSPanel_ProjectTextField_actionAdapter(PBSPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.ProjectTextField_actionPerformed(e);
    }
  }
}
