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

package cct.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public class AddNewFragmentDialog
    extends JDialog {
  public static final int CANCELED = 0;
  public static final int APPROVED = 1;

  private int outcome = APPROVED;

  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JButton cancelButton = new JButton();
  private JButton okButton = new JButton();
  private JLabel jLabel1 = new JLabel();
  private JTextField nameTextField = new JTextField();
  private JLabel jLabel2 = new JLabel();
  private JTextField pathTextField = new JTextField();
  private JComboBox pathComboBox = new JComboBox();
  private ButtonGroup buttonGroup1 = new ButtonGroup();
  private JRadioButton customRadioButton = new JRadioButton();
  private JRadioButton existingRadioButton = new JRadioButton();
  private JLabel jLabel3 = new JLabel();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();

  public AddNewFragmentDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public AddNewFragmentDialog() {
    this(new Frame(), "AddNewFragmentDialog", false);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    cancelButton.setSelectedIcon(null);
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    okButton.setToolTipText("");
    okButton.setText("  OK  ");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jLabel1.setToolTipText("");
    jLabel1.setText("Fragment Name: ");
    nameTextField.setToolTipText("");
    nameTextField.setText("Magic");
    nameTextField.setColumns(40);
    jLabel2.setToolTipText("");
    jLabel2.setText("Custom Path: ");
    pathTextField.setToolTipText("");
    pathTextField.setText("Toxic Residues");
    pathTextField.setColumns(40);
    jPanel1.setLayout(gridBagLayout1);
    customRadioButton.setToolTipText("");
    customRadioButton.setText("Custom Path");
    customRadioButton.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        customRadioButton_itemStateChanged(e);
      }
    });
    existingRadioButton.setToolTipText("");
    existingRadioButton.setText("Existing Path");
    existingRadioButton.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        existingRadioButton_itemStateChanged(e);
      }
    });
    jLabel3.setToolTipText("");
    jLabel3.setText("Existing Path(s): ");
    pathComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        pathComboBox_itemStateChanged(e);
      }
    });

    existingRadioButton.setSelected(true);
    customRadioButton.setSelected(false);

    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.CENTER);
    jPanel2.add(okButton);
    jPanel2.add(cancelButton);
    panel1.add(jPanel2, BorderLayout.SOUTH);
    buttonGroup1.add(customRadioButton);
    buttonGroup1.add(existingRadioButton);
    jPanel1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
    jPanel1.add(jLabel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(pathTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(customRadioButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(existingRadioButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(pathComboBox, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0
        , GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jLabel3, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0,
                                                0));
    jPanel1.add(nameTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }

  public void setFragmentName(String name) {
    nameTextField.setText(name);
  }

  public String getFragmentName() {
    return nameTextField.getText().trim();
  }

  public void setPath(String path) {
    pathTextField.setText(path);
    pathComboBox.setSelectedItem(path);
  }

  public String getPath() {
    return pathTextField.getText().trim();
  }

  public int showDialog() {
    this.setModal(true);
    setVisible(true);
    return outcome;
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    outcome = AddNewFragmentDialog.CANCELED;
    setVisible(false);
  }

  public void okButton_actionPerformed(ActionEvent e) {
    outcome = AddNewFragmentDialog.APPROVED;
    setVisible(false);
  }

  public void setExistingPaths(String[] paths) {
    pathComboBox.removeAllItems();
    for (String item : paths) {
      pathComboBox.addItem(item);
    }
    pathComboBox.setSelectedIndex(0);
  }

  public void customRadioButton_itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.DESELECTED) {
      return;
    }
    pathTextField.setEnabled(true);
  }

  public void pathComboBox_itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.DESELECTED) {
      pathTextField.setText("");
      return;
    }
    pathTextField.setText(pathComboBox.getSelectedItem().toString());
  }

  public void existingRadioButton_itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.DESELECTED) {
      return;
    }
    pathTextField.setEnabled(false);
  }

}
