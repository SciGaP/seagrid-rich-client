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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cct.interfaces.MoleculeRendererInterface;

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
public class AddMoleculeDialog
    extends JDialog {

  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JButton cancelButton = new JButton();
  private JButton jButton2 = new JButton();
  private JRadioButton overwriteRadioButton = new JRadioButton();
  private BorderLayout borderLayout2 = new BorderLayout();
  private JRadioButton newWinRadioButton = new JRadioButton();
  private JRadioButton mergeRadioButton = new JRadioButton();
  private ButtonGroup buttonGroup1 = new ButtonGroup();

  private int cond = 1;

  public AddMoleculeDialog(Frame owner, String title) {
    super(owner, title, true);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public AddMoleculeDialog(Dialog owner, String title) {
    super(owner, title, true);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public int getLoadMode() {
    if (overwriteRadioButton.isSelected()) {
      return MoleculeRendererInterface.OVERWRITE_MOLECULE;
    }
    else if (newWinRadioButton.isSelected()) {
      return MoleculeRendererInterface.NEW_MOLECULE;
    }

    return MoleculeRendererInterface.MERGE_MOLECULE;
  }

  public void EnableNewWindowButton(boolean enable) {
    newWinRadioButton.setVisible(enable);
    this.validateTree();
  }

  public AddMoleculeDialog() {
    this(new Frame(), "AddMoleculeDialog");
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    cancelButton.setToolTipText("");
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    jButton2.setToolTipText("");
    jButton2.setText(" Open ");
    jButton2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    overwriteRadioButton.setToolTipText("Erase Molecule in Active Window and Load a New One");
    overwriteRadioButton.setSelected(true);
    overwriteRadioButton.setText("Overwrite Molecule in Active Window");
    jPanel1.setLayout(borderLayout2);
    newWinRadioButton.setToolTipText("Load Molecule into New Window");
    newWinRadioButton.setText("Open in New Window");
    mergeRadioButton.setToolTipText("Append New Molecule to a Molecule in Active Window");
    mergeRadioButton.setText("Merge with Molecule in Active Window");
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.CENTER);
    panel1.add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(jButton2);
    jPanel2.add(cancelButton);
    jPanel1.add(newWinRadioButton, BorderLayout.SOUTH);
    buttonGroup1.add(overwriteRadioButton);
    jPanel1.add(mergeRadioButton, BorderLayout.CENTER);
    jPanel1.add(overwriteRadioButton, BorderLayout.NORTH);
    buttonGroup1.add(newWinRadioButton);
    buttonGroup1.add(mergeRadioButton);
  }

  public int showDialog() {
    setVisible(true);
    return cond;
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    cond = getLoadMode();
    setVisible(false);
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    cond = MoleculeRendererInterface.LOAD_CANCELED;
    setVisible(false);
  }
}
