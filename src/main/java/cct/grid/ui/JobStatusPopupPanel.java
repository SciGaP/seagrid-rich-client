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

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

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
public class JobStatusPopupPanel
    extends JPanel {
  JLabel jLabel1 = new JLabel();
  JLabel jobNameLabel = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel hostLabel = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel taskProviderLabel = new JLabel();
  JLabel jLabel7 = new JLabel();
  JLabel softLabel = new JLabel();
  JLabel jLabel9 = new JLabel();
  JLabel submitTimeLabel = new JLabel();
  GridLayout gridLayout1 = new GridLayout();
  JLabel jLabel2 = new JLabel();
  JLabel localDirLabel = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel remoteDirLabel = new JLabel();
  JLabel jLabel8 = new JLabel();
  JLabel executableLabel = new JLabel();

  JLabel outputFilesLabel = new JLabel("Output Files");
  JLabel resourcesUsedLabel = new JLabel("Resources Used");
  JLabel emptyLabel = new JLabel(" ");
  JLabel emptyLabel2 = new JLabel(" ");

  java.util.List<JLabel> labelStore = new ArrayList<JLabel> ();
  java.util.List<JLabel> resourcesLabelStore = new ArrayList<JLabel> ();

  public JobStatusPopupPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(gridLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setText("Job Name: ");
    jobNameLabel.setToolTipText("");
    jobNameLabel.setText("Unknown Name");
    jLabel3.setToolTipText("");
    jLabel3.setText("Remote Host: ");
    hostLabel.setToolTipText("");
    hostLabel.setText("Uknown Host");
    jLabel5.setToolTipText("");
    jLabel5.setText("Task Provider: ");
    taskProviderLabel.setToolTipText("");
    taskProviderLabel.setText("Unknown Task Provider");
    jLabel7.setToolTipText("");
    jLabel7.setText("Software: ");
    softLabel.setToolTipText("");
    softLabel.setText("Unknown software");
    jLabel9.setToolTipText("");
    jLabel9.setText("Submit Time: ");
    submitTimeLabel.setToolTipText("");
    submitTimeLabel.setText("Unknown Submit Time");
    gridLayout1.setColumns(2);
    gridLayout1.setRows(0);
    this.setBackground(new Color(176, 235, 228));
    this.setBorder(new TitledBorder(BorderFactory.createBevelBorder(BevelBorder.
        RAISED, Color.white, new Color(251, 255, 255), new Color(86, 114, 111),
        new Color(123, 164, 159)), "Extended Task Info"));
    jLabel2.setToolTipText("");
    jLabel2.setText("Local Directory: ");
    localDirLabel.setToolTipText("");
    localDirLabel.setText("Unknown Local Directory");
    jLabel4.setToolTipText("");
    jLabel4.setText("Remote Directory: ");
    remoteDirLabel.setToolTipText("");
    remoteDirLabel.setText("Unknown Local Directory");
    jLabel8.setToolTipText("");
    jLabel8.setText("Executable: ");
    executableLabel.setToolTipText("");
    executableLabel.setText("Unknown Executable");
    this.add(jLabel1, null);
    this.add(jobNameLabel, null);
    this.add(jLabel3);
    this.add(hostLabel);
    this.add(jLabel5);
    this.add(taskProviderLabel);
    this.add(jLabel7);
    this.add(softLabel);
    this.add(jLabel9);
    this.add(submitTimeLabel);
    this.add(jLabel2);
    this.add(localDirLabel);
    this.add(jLabel4);
    this.add(remoteDirLabel);
    this.add(jLabel8);
    this.add(executableLabel);
  }

  public void setupOutputFiles(Map outFiles) {

    if (outFiles == null || outFiles.size() < 1) {
      this.remove(outputFilesLabel);
      this.remove(emptyLabel);

      for (Iterator iter = labelStore.iterator(); iter.hasNext(); ) {
        JLabel label = (JLabel) iter.next();
        remove(label);
      }

      this.validate();
      return;
    }

    // ----  Set title

    add(outputFilesLabel);
    add(emptyLabel);

    Set set = outFiles.entrySet();
    Iterator iter = set.iterator();
    int count = 0;
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String tag = me.getKey().toString();
      if (tag.startsWith("local")) {
        continue;
      }
      Object obj = me.getValue();
      String fileName = obj.toString();

      if (labelStore.size() >= count + 2) {
        JLabel label = labelStore.get(count);
        label.setText(tag);
        add(label);
        label = labelStore.get(count + 1);
        //JMenuItem jmenu = new JMenuItem(fileName);
        label.setText(fileName);
        add(label);
        //add(jmenu);
      }
      else {
        JLabel label = new JLabel(tag);
        labelStore.add(label);
        add(label);
        label = new JLabel(fileName);
        labelStore.add(label);
        //JMenuItem jmenu = new JMenuItem(fileName);
        //add(jmenu);
        add(label);
      }

      count += 2;
    }
    this.validate();
  }

  public void setupResourcesUsed(Map rUsed) {

    if (rUsed == null || rUsed.size() < 1) {
      this.remove(resourcesUsedLabel);
      this.remove(emptyLabel2);

      for (Iterator iter = resourcesLabelStore.iterator(); iter.hasNext(); ) {
        JLabel label = (JLabel) iter.next();
        remove(label);
      }

      this.validate();
      return;
    }

    // ----  Set title

    add(resourcesUsedLabel);
    add(emptyLabel2);

    Set set = rUsed.entrySet();
    Iterator iter = set.iterator();
    int count = 0;
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String tag = me.getKey().toString();
      String value = me.getValue().toString();

      if (resourcesLabelStore.size() >= count + 2) {
        JLabel label = resourcesLabelStore.get(count);
        label.setText(tag);
        add(label);
        label = resourcesLabelStore.get(count + 1);
        //JMenuItem jmenu = new JMenuItem(fileName);
        label.setText(value);
        add(label);
        //add(jmenu);
      }
      else {
        JLabel label = new JLabel(tag);
        resourcesLabelStore.add(label);
        add(label);
        label = new JLabel(value);
        resourcesLabelStore.add(label);
        //JMenuItem jmenu = new JMenuItem(fileName);
        //add(jmenu);
        add(label);
      }

      count += 2;
    }
    this.validate();
  }

}
