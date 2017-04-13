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

package cct.tools.filebrowser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 * Dialog for modifying file attributes
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class PermissionsPanel
    extends JPanel implements ItemListener {

  int permission = 0;

  Map setBit = new HashMap();
  Map zeroBit = new HashMap();
  Map checkedToolTips = new HashMap();
  Map uncheckedToolTips = new HashMap();

  JLabel readLabel = new JLabel();
  JCheckBox otherReadCheckBox = new JCheckBox();
  JTextField modeTextField = new JTextField();
  JCheckBox ownerReadCheckBox = new JCheckBox();
  JCheckBox ownerWriteCheckBox = new JCheckBox();
  JCheckBox ownerExecuteCheckBox = new JCheckBox();
  JCheckBox groupReadCheckBox = new JCheckBox();
  JCheckBox groupWriteCheckBox = new JCheckBox();
  JCheckBox groupExecuteCheckBox = new JCheckBox();
  JCheckBox otherWriteCheckBox = new JCheckBox();
  JCheckBox otherExecuteCheckBox = new JCheckBox();
  JLabel writeLabel = new JLabel();
  JLabel ownerLabel = new JLabel();
  JLabel groupLabel = new JLabel();
  JLabel otherLabel = new JLabel();
  JLabel permissionLabel = new JLabel();
  JLabel executeLabel = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  public PermissionsPanel(int perm) {
    this();
  }

  public PermissionsPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    readLabel.setToolTipText("");
    readLabel.setText("Read");
    modeTextField.setToolTipText("Enter new value (000-777) and Press Enter");
    modeTextField.setText("000");
    modeTextField.setColumns(4);
    modeTextField.addFocusListener(new
                                   PermissionsPanel_modeTextField_focusAdapter(this));
    modeTextField.addActionListener(new
                                    PermissionsPanel_modeTextField_actionAdapter(this));
    writeLabel.setToolTipText("");
    writeLabel.setText("Write");
    ownerLabel.setToolTipText("");
    ownerLabel.setText("Owner:");
    groupLabel.setToolTipText("");
    groupLabel.setText("Group:");
    otherLabel.setToolTipText("");
    otherLabel.setText("Other:");
    permissionLabel.setToolTipText("");
    permissionLabel.setText("Permission mode:");
    executeLabel.setToolTipText("");
    executeLabel.setText("Execute");
    ownerReadCheckBox.setToolTipText("Owner cannot read");
    ownerWriteCheckBox.setToolTipText("Owner cannot write");
    ownerExecuteCheckBox.setToolTipText("Owner cannot execute");
    groupReadCheckBox.setToolTipText("Group cannot read");
    groupWriteCheckBox.setToolTipText("Group cannot write");
    groupExecuteCheckBox.setToolTipText("Group cannot execute");
    otherReadCheckBox.setToolTipText("Others cannot read");
    otherWriteCheckBox.setToolTipText("Others cannot write");
    otherExecuteCheckBox.setToolTipText("Others cannot execute");
    this.add(ownerLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 5), 0, 0));
    this.add(groupLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 5), 0, 0));
    this.add(otherLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 5), 0, 0));
    this.add(permissionLabel, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(modeTextField, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(ownerReadCheckBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER,
        GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(ownerWriteCheckBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER,
        GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(ownerExecuteCheckBox,
             new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                    , GridBagConstraints.CENTER,
                                    GridBagConstraints.NONE,
                                    new Insets(5, 5, 5, 5), 0, 0));
    this.add(groupReadCheckBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER,
        GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(groupWriteCheckBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER,
        GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(groupExecuteCheckBox,
             new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
                                    , GridBagConstraints.CENTER,
                                    GridBagConstraints.NONE,
                                    new Insets(5, 5, 5, 5), 0, 0));
    this.add(otherReadCheckBox, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER,
        GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(otherWriteCheckBox, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER,
        GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(otherExecuteCheckBox,
             new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
                                    , GridBagConstraints.CENTER,
                                    GridBagConstraints.NONE,
                                    new Insets(5, 5, 5, 5), 0, 0));
    this.add(readLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                               , GridBagConstraints.CENTER,
                                               GridBagConstraints.NONE,
                                               new Insets(5, 5, 5, 5), 0, 0));
    this.add(writeLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 5), 0, 0));
    this.add(executeLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                  new Insets(5, 5, 5, 5), 0, 0));

    // --- Masks for setting
    setBit.put(otherExecuteCheckBox, new Integer(0001));
    setBit.put(otherWriteCheckBox, new Integer(0002));
    setBit.put(otherReadCheckBox, new Integer(0004));

    setBit.put(groupExecuteCheckBox, new Integer(0010));
    setBit.put(groupWriteCheckBox, new Integer(0020));
    setBit.put(groupReadCheckBox, new Integer(0040));

    setBit.put(ownerExecuteCheckBox, new Integer(0100));
    setBit.put(ownerWriteCheckBox, new Integer(0200));
    setBit.put(ownerReadCheckBox, new Integer(0400));

    // --- Masks for clearing
    zeroBit.put(otherExecuteCheckBox, new Integer(0776));
    zeroBit.put(otherWriteCheckBox, new Integer(0775));
    zeroBit.put(otherReadCheckBox, new Integer(0773));

    zeroBit.put(groupExecuteCheckBox, new Integer(0767));
    zeroBit.put(groupWriteCheckBox, new Integer(0757));
    zeroBit.put(groupReadCheckBox, new Integer(0737));

    zeroBit.put(ownerExecuteCheckBox, new Integer(0677));
    zeroBit.put(ownerWriteCheckBox, new Integer(0577));
    zeroBit.put(ownerReadCheckBox, new Integer(0377));

    // --- Adding tooltips for checked boxes

    checkedToolTips.put(otherExecuteCheckBox, "Others can execute");
    checkedToolTips.put(otherWriteCheckBox, "Others can write");
    checkedToolTips.put(otherReadCheckBox, "Others can read");

    checkedToolTips.put(groupExecuteCheckBox, "Group can execute");
    checkedToolTips.put(groupWriteCheckBox, "Group can write");
    checkedToolTips.put(groupReadCheckBox, "Group can read");

    checkedToolTips.put(ownerExecuteCheckBox, "Owner can execute");
    checkedToolTips.put(ownerWriteCheckBox, "Owner can write");
    checkedToolTips.put(ownerReadCheckBox, "Owner can read");

    // --- Adding tooltips for unchecked boxes

    uncheckedToolTips.put(otherExecuteCheckBox, "Others cannot execute");
    uncheckedToolTips.put(otherWriteCheckBox, "Others cannot write");
    uncheckedToolTips.put(otherReadCheckBox, "Others cannot read");

    uncheckedToolTips.put(groupExecuteCheckBox, "Group cannot execute");
    uncheckedToolTips.put(groupWriteCheckBox, "Group cannot write");
    uncheckedToolTips.put(groupReadCheckBox, "Group cannot read");

    uncheckedToolTips.put(ownerExecuteCheckBox, "Owner cannot execute");
    uncheckedToolTips.put(ownerWriteCheckBox, "Owner cannot write");
    uncheckedToolTips.put(ownerReadCheckBox, "Owner cannot read");

    // --- Adding item state changed listener

    otherExecuteCheckBox.addItemListener(this);
    otherWriteCheckBox.addItemListener(this);
    otherReadCheckBox.addItemListener(this);

    groupExecuteCheckBox.addItemListener(this);
    groupWriteCheckBox.addItemListener(this);
    groupReadCheckBox.addItemListener(this);

    ownerExecuteCheckBox.addItemListener(this);
    ownerWriteCheckBox.addItemListener(this);
    ownerReadCheckBox.addItemListener(this);

  }

  void setPermissionMode() {
    permission = 0;
    Set set = setBit.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      JCheckBox box = (JCheckBox) me.getKey();
      Integer mask = (Integer) me.getValue();
      if (box.isSelected()) {
        permission |= mask.intValue();
      }
    }

    modeTextField.setEnabled(false);
    modeTextField.setText(Integer.toOctalString(permission));
    modeTextField.setEnabled(true);
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    JCheckBox box = (JCheckBox) e.getItemSelectable();
    if (!box.isEnabled()) {
      return;
    }

    if (box.isSelected()) {
      Integer mask = (Integer) setBit.get(box);
      permission |= mask.intValue();
    }
    else {
      Integer mask = (Integer) zeroBit.get(box);
      permission &= mask.intValue();
    }
    modeTextField.setEnabled(false);
    modeTextField.setText(Integer.toOctalString(permission));
    modeTextField.setEnabled(true);
  }

  public void modeTextField_actionPerformed(ActionEvent e) {
    parsePermissionMode();
  }

  void parsePermissionMode() {
    if (!modeTextField.isEnabled()) {
      return;
    }
    modeTextField.setEnabled(false);
    try {
      permission = Integer.parseInt(modeTextField.getText().trim(), 8);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
                                    "Wrong permission mode: " +
                                    modeTextField.getText() +
                                    " Should be in 000-777 range",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      modeTextField.setText(String.valueOf(permission));
      modeTextField.setEnabled(true);
      return;
    }

    setCheckBoxes(permission);

    modeTextField.setEnabled(true);

  }

  public void modeTextField_focusLost(FocusEvent e) {
    parsePermissionMode();
  }

  /**
   * Sets/unsets checkboxes ticks
   */
  void setCheckBoxes(int permiss) {
    enableCheckBoxes(false);
    Set set = setBit.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      JCheckBox box = (JCheckBox) me.getKey();
      Integer mask = (Integer) me.getValue();
      if ( (permiss & mask.intValue()) > 0) {
        box.setSelected(true);
        box.setToolTipText( (String) checkedToolTips.get(box));
      }
      else {
        box.setSelected(false);
        box.setToolTipText( (String) uncheckedToolTips.get(box));
      }
    }
    enableCheckBoxes(true);
  }

  /**
   * Enables/disables all checkboxes
   * @param enable boolean
   */
  void enableCheckBoxes(boolean enable) {
    Set set = setBit.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      JCheckBox box = (JCheckBox) me.getKey();
      box.setEnabled(enable);
    }
  }

  void setupControls(int permiss) {
    if (permiss < 0 || permiss > 0777) {
      JOptionPane.showMessageDialog(null,
                                    "Wrong permission mode: " +
                                    permiss +
                                    " Should be in 000-777 range",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      permission = 0;
    }
    else {
      permission = permiss;
    }
    this.setCheckBoxes(permission);
    this.setPermissionMode();
  }

  public void setupControls(String permissions) {
    permission = 0;

    char[] perm = permissions.toCharArray();

    if (perm.length != 10) {
      JOptionPane.showMessageDialog(null,
                                    "Expecting permissions string of 10 characters. Got: " +
                                    permissions,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
    }
    else {

      if (perm[9] == 'x') {
        permission |= 0001;
      }
      if (perm[8] == 'w') {
        permission |= 0002;
      }
      if (perm[7] == 'r') {
        permission |= 0004;
      }

      if (perm[6] == 'x') {
        permission |= 0010;
      }
      if (perm[5] == 'w') {
        permission |= 0020;
      }
      if (perm[4] == 'r') {
        permission |= 0040;
      }

      if (perm[3] == 'x') {
        permission |= 0100;
      }
      if (perm[2] == 'w') {
        permission |= 0200;
      }
      if (perm[1] == 'r') {
        permission |= 0400;
      }

    }

    this.setCheckBoxes(permission);
    this.setPermissionMode();
  }

  public int getPermissions() {
    return permission;
  }
}

class PermissionsPanel_modeTextField_focusAdapter
    extends FocusAdapter {
  private PermissionsPanel adaptee;
  PermissionsPanel_modeTextField_focusAdapter(PermissionsPanel adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void focusLost(FocusEvent e) {
    adaptee.modeTextField_focusLost(e);
  }
}

class PermissionsPanel_modeTextField_actionAdapter
    implements ActionListener {
  private PermissionsPanel adaptee;
  PermissionsPanel_modeTextField_actionAdapter(PermissionsPanel adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.modeTextField_actionPerformed(e);
  }
}
