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

import java.awt.FlowLayout;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import cct.grid.JobDescription;
import cct.grid.ResourcesProviderInterface;

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
public class ResourcesPanel
    extends JPanel implements ResourcesProviderInterface {

   public static final String SECONDS_TIME_UNITS = "Secs";
   public static final String MINUTES_TIME_UNITS = "Mins";
   public static final String HOURS_TIME_UNITS = "Hours";

   public static final String IN_MEGABYTES = "Mb";
   public static final String IN_GIGABYTES = "Gb";

   FlowLayout flowLayout1 = new FlowLayout();
   JLabel jLabel1 = new JLabel();
   public JTextField cpuTextField = new JTextField();
   JLabel jLabel2 = new JLabel();
   JLabel jLabel3 = new JLabel();
   public JTextField memoryTextField = new JTextField();
   public JTextField timeTextField = new JTextField();
   public JComboBox memoryComboBox = new JComboBox();
   public JComboBox timeComboBox = new JComboBox();

   public ResourcesPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      jLabel1.setText("CPUs: ");
      cpuTextField.setToolTipText("");
      cpuTextField.setText("1");
      cpuTextField.setColumns(4);
      jLabel2.setToolTipText("");
      jLabel2.setText("   Memory: ");
      jLabel3.setToolTipText("");
      jLabel3.setText("   Time: ");
      memoryTextField.setToolTipText("");
      memoryTextField.setText("1024");
      memoryTextField.setColumns(5);
      timeTextField.setToolTipText("");
      timeTextField.setText("1");
      timeTextField.setColumns(4);
      this.setBorder(new TitledBorder(BorderFactory.createLineBorder(
          SystemColor.
          controlDkShadow, 1), "Resources"));
      this.add(jLabel1);
      this.add(cpuTextField);
      this.add(jLabel2);
      this.add(memoryTextField);
      this.add(memoryComboBox);
      this.add(jLabel3);
      this.add(timeTextField);
      this.add(timeComboBox);

      timeComboBox.addItem(ResourcesPanel.SECONDS_TIME_UNITS);
      timeComboBox.addItem(ResourcesPanel.MINUTES_TIME_UNITS);
      timeComboBox.addItem(ResourcesPanel.HOURS_TIME_UNITS);
      timeComboBox.setSelectedItem(ResourcesPanel.HOURS_TIME_UNITS);

      memoryComboBox.addItem(ResourcesPanel.IN_MEGABYTES);
      memoryComboBox.addItem(ResourcesPanel.IN_GIGABYTES);
      memoryComboBox.setSelectedItem(ResourcesPanel.IN_MEGABYTES);
   }

   @Override
  public int getNCPUs() {
      int test = 0;
      try {

         test = Integer.parseInt(cpuTextField.getText().trim());
      }
      catch (Exception ex) {
         System.err.println("getNCPU: Internal error: wrong cpu number");
      }
      finally {
         if (test < 1) {
            System.err.println("getNCPU: Internal error: wrong cpu number: <0");
         }
      }
      return test;
   }

   @Override
  public float getMaxMemoryInMb() {
      float test = 0;
      try {
         test = Float.parseFloat(memoryTextField.getText().trim());
      }
      catch (Exception ex) {
         System.err.println("getMaxMemoryInMb: Internal conversion error");
      }
      finally {
         if (test < 0) {
            System.err.println("getMaxMemoryInMb: Internal error: <0");
         }
      }

      String units = memoryComboBox.getSelectedItem().toString();
      if (units.equals(IN_MEGABYTES)) {
         return test;
      }
      else if (units.equals(IN_GIGABYTES)) {
         return test * 1024.0f;
      }
      System.err.println("getMaxMemoryInMb: Internal error: wrong units");
      return test;
   }

   @Override
  public float getTimeInSeconds() {
      float intTime = 0;
      try {
         intTime = this.getNumber(timeTextField);
      }
      catch (Exception ex) {
         System.err.println("getTimeInSeconds: Internal conversion error");
      }

      if (intTime <= 0) {
         System.err.println("getTimeInSeconds: Internal error: <=0");
      }

      String units = timeComboBox.getSelectedItem().toString();
      if (units.equals(SECONDS_TIME_UNITS)) {
         return intTime;
      }
      else if (units.equals(MINUTES_TIME_UNITS)) {
         intTime *= 60.0f;
      }
      else if (units.equals(HOURS_TIME_UNITS)) {
         intTime *= 3600.0f;
      }
      else {
         System.err.println("getTimeInSeconds: Internal error: wrong conversion units");
      }

      return intTime;
   }

   float getNumber(JTextField field) throws Exception {
      float number = 0;
      try {
         number = Float.parseFloat(field.getText().trim());
      }
      catch (Exception ex) {
         throw ex;
      }
      return number;
   }

   public void validateValues() throws Exception {
      //getTimeInSeconds();

      float intTime = 0;
      try {
         intTime = this.getNumber(timeTextField);
      }
      catch (Exception ex) {
         throw new Exception("Wrong number for time: " + ex.getMessage());
      }

      if (intTime <= 0) {
         throw new Exception("Time should be positive number. Got " +
                             timeTextField.getText());
      }

      String units = timeComboBox.getSelectedItem().toString();
      if (units.equals(SECONDS_TIME_UNITS)) {
      }
      else if (units.equals(MINUTES_TIME_UNITS)) {
      }
      else if (units.equals(HOURS_TIME_UNITS)) {
      }
      else {
         throw new Exception("Internal error while getting time");
      }

      //getMaxMemoryInMb();

      float test = 0;
      try {
         test = Float.parseFloat(memoryTextField.getText().trim());
      }
      catch (Exception ex) {
         throw new Exception("Wrong value for memory: " + ex.getMessage());
      }
      finally {
         if (test < 0) {
            throw new Exception("Value for memory should be > 0");
         }
      }

      units = memoryComboBox.getSelectedItem().toString();
      if (units.equals(IN_MEGABYTES)) {
      }
      else if (units.equals(IN_GIGABYTES)) {
      }
      else {
         throw new Exception("Internal error while getting max memory");
      }

      //getNCPUs();

      test = 0;
      try {
         test = Integer.parseInt(cpuTextField.getText().trim());
      }
      catch (Exception ex) {
         throw new Exception("Wrong value for cpu: " + ex.getMessage());
      }
      finally {
         if (test < 1) {
            throw new Exception("Value for number of cpu's should be > 0");
         }
      }
   }

   @Override
  public void setJobDescription(JobDescription job) throws Exception {

      // --- Setup max (wall) time

      float maxWallTimeInMins = -1;
      try {
         float time_in_secs = getTimeInSeconds();
         maxWallTimeInMins = time_in_secs / 60.0f;
         job.setMaxWallTime(maxWallTimeInMins);
      }
      catch (Exception ex) {
         throw new Exception("Error getting time: " + ex.getMessage());
      }

      // --- Setup max memory

      int maxMemory = 0; // in MB
      try {
         maxMemory = (int) getMaxMemoryInMb();
         job.setMaxMemory(maxMemory);
      }
      catch (Exception ex) {
         throw new Exception("Error setting max memory: " + ex.getMessage());
      }

   }

   @Override
  public void setNCPUs(int n) {
      if (n < 1) {
         n = 1;
      }
      cpuTextField.setText(String.valueOf(n));
   }

   @Override
  public void setMaxMemoryInMb(int memory) {
      if (memory < 1) {
         memory = 1;
      }

      memoryTextField.setText(String.valueOf(memory));

      memoryComboBox.setSelectedItem(IN_MEGABYTES);
   }
}
