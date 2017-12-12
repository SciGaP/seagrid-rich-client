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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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
public class PBS_Panel
    extends JPanel {
   Preferences prefs = Preferences.userNodeForPackage(getClass());
   JLabel memoryLabel = new JLabel();
   JLabel timeLabel = new JLabel();
   JLabel queueLabel = new JLabel();
   JLabel jobfsLabel = new JLabel();
   JLabel softLabel = new JLabel();
   JLabel projectLabel = new JLabel();
   JLabel cpuLabel = new JLabel();
   public JTextField ProjectTextField = new JTextField();
   public JTextField QueueTextField = new JTextField();
   public JTextField TimeTextField = new JTextField();
   public JTextField MemoryTextField = new JTextField();
   public JTextField JobfsTextField = new JTextField();
   public JTextField SoftwareTextField = new JTextField();
   public JTextField CPUTextField = new JTextField();

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

   public PBS_Panel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public PBS_Panel(String project, String queue, int time, int memory,
                    int jobfs, String software) {
      try {
         jbInit();
         if (project == null) {
            setDefaultProject();
         }
         else {
            ProjectTextField.setText(project);
         }
         QueueTextField.setText(queue);
         TimeTextField.setText(String.valueOf(time));
         MemoryTextField.setText(String.valueOf(memory));
         JobfsTextField.setText(String.valueOf(jobfs));
         SoftwareTextField.setText(software);
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      memoryLabel.setToolTipText("");
      memoryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      memoryLabel.setText("Memory (MB): ");
      timeLabel.setToolTipText("");
      timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      timeLabel.setText("Time (min): ");
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
      cpuLabel.setToolTipText("");
      cpuLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      cpuLabel.setText("CPUs: ");

      extCheckBox.setText("Enable pbs extensions");
      extCheckBox.setEnabled(false);
      extCheckBox.setSelected(true);

      ProjectTextField.setToolTipText("");
      ProjectTextField.setColumns(6);
      ProjectTextField.addActionListener(new
                                         PBS_Panel_ProjectTextField_actionAdapter(this));
      QueueTextField.setToolTipText("");
      QueueTextField.setText("normal");
      QueueTextField.setColumns(6);
      TimeTextField.setToolTipText("");
      TimeTextField.setText("60");
      TimeTextField.setColumns(6);
      MemoryTextField.setToolTipText("");
      MemoryTextField.setText("1024");
      MemoryTextField.setColumns(6);
      JobfsTextField.setToolTipText("");
      JobfsTextField.setText("5");
      JobfsTextField.setColumns(6);
      SoftwareTextField.setToolTipText("");
      SoftwareTextField.setColumns(6);
      CPUTextField.setToolTipText("");
      CPUTextField.setText("1");
      CPUTextField.setColumns(6);
      jPanel1.setLayout(gridBagLayout1);
      jPanel1.add(projectLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(queueLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(timeLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST,
          GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(memoryLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(cpuLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST,
          GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(jobfsLabel, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(softLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST,
          GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      this.add(jPanel1, BorderLayout.NORTH);
      jPanel1.add(SoftwareTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(MemoryTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(ProjectTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(QueueTextField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(CPUTextField, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(TimeTextField, new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(JobfsTextField, new GridBagConstraints(5, 1, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));

      jPanel1.add(extCheckBox, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      String value;

      setDefaultProject();

      value = prefs.get(projectKey, "");
      ProjectTextField.setText(value);

      value = prefs.get(queueKey, "normal");
      QueueTextField.setText(value);

      value = prefs.get(timeKey, "60");
      TimeTextField.setText(value);

      value = prefs.get(memoryKey, "1024");
      MemoryTextField.setText(value);

      value = prefs.get(jobfsKey, "5");
      JobfsTextField.setText(value);

      value = prefs.get(softwareKey, "g03");
      SoftwareTextField.setText(value);

      value = prefs.get(cpuKey, "1");
      CPUTextField.setText(value);

   }

   public void saveCurrentSettings() {
      prefs.put(projectKey, ProjectTextField.getText());
      prefs.put(queueKey, QueueTextField.getText());
      prefs.put(timeKey, TimeTextField.getText());
      prefs.put(memoryKey, MemoryTextField.getText());
      prefs.put(jobfsKey, JobfsTextField.getText());
      prefs.put(softwareKey, SoftwareTextField.getText());
      prefs.put(cpuKey, CPUTextField.getText());

   }

   public void setDefaultProject() {
      String value = prefs.get(projectKey, "");
      ProjectTextField.setText(value);
   }

   public String getProject() {
      return ProjectTextField.getText().trim();
   }

   public String getQueue() {
      return QueueTextField.getText().trim();
   }

   public String getSoftware() {
      return SoftwareTextField.getText();
   }

   /*
      public RslAttributes getRSL() throws Exception {
     RslAttributes rsl = new RslAttributes();
     rsl.add("project", ProjectTextField.getText().trim());
     rsl.add("queue", QueueTextField.getText().trim());
     int test = 0;

     try {
       test = (int) getMaxTime();
       rsl.add("max_wall_time", String.valueOf(test));
     }
     catch (Exception ex) {
       throw ex;
     }

     try {
       test = (int) getMaxMemory();
       rsl.add("maxMemory", MemoryTextField.getText());
     }
     catch (Exception ex) {
       throw ex;
     }

     try {
       test = getJobFS();
       rsl.add("jobfs", JobfsTextField.getText());
     }
     catch (Exception ex) {
       throw ex;
     }

     rsl.add("software", SoftwareTextField.getText());

     try {
       test = getNCPUs();
       rsl.add("count", CPUTextField.getText());
     }
     catch (Exception ex) {
       throw ex;
     }

     return rsl;
      }
    */
   public int getJobFS() throws Exception {
      int test = 0;
      try {
         test = (int) Float.parseFloat(JobfsTextField.getText().trim());
      }
      catch (Exception ex) {
         throw new Exception("Wrong value for jobfs: " + ex.getMessage());
      }
      finally {
         if (test < 0) {
            throw new Exception("Value for time should be positive");
         }
      }
      return test;
   }

   public int getNCPUs() throws Exception {
      int test = 0;
      try {

         test = Integer.parseInt(CPUTextField.getText().trim());
      }
      catch (Exception ex) {
         throw new Exception("Wrong value for cpu: " + ex.getMessage());
      }
      finally {
         if (test < 1) {
            throw new Exception("Value for number of cpu's should be > 0");
         }
      }
      return test;
   }

   public float getMaxTime() throws Exception {
      float test = 0;
      try {
         test = Float.parseFloat(TimeTextField.getText().trim());
      }
      catch (Exception ex) {
         throw new Exception("Wrong value for time: " + ex.getMessage());
      }
      finally {
         if (test < 1) {
            throw new Exception("Value for time should be > 0");
         }
      }
      return test;
   }

   public int getMaxMemory() throws Exception {
      int test = 0;
      try {
         test = Integer.parseInt(MemoryTextField.getText().trim());
      }
      catch (Exception ex) {
         throw new Exception("Wrong value for memory: " + ex.getMessage());
      }
      finally {
         if (test < 1) {
            throw new Exception("Value for memory should be > 0");
         }
      }
      return test;
   }

   public void ProjectTextField_actionPerformed(ActionEvent e) {
      String value = ProjectTextField.getText();
      try {
         prefs.put(projectKey, value);
      }
      catch (Exception ex) {
         System.err.println("Warning: Unable to save project: " + ex.getMessage());
      }
   }
}

class PBS_Panel_ProjectTextField_actionAdapter
    implements ActionListener {
   private PBS_Panel adaptee;
   PBS_Panel_ProjectTextField_actionAdapter(PBS_Panel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.ProjectTextField_actionPerformed(e);
   }
}
