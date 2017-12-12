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

package cct.pdb.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class PDBBankFormPanel
    extends JPanel {

   static String defaultPDB_Host = "ftp.wwpdb.org";
   static String defaultPDB_EntryDir = "/pub/pdb/data/structures/divided";

   JLabel jLabel1 = new JLabel();
   JTextField pdbCodeTextField = new JTextField();
   JLabel jLabel2 = new JLabel();
   private JComboBox hostsComboBox = new JComboBox();
   JLabel jLabel3 = new JLabel();
   private JTextField entryDirTextField = new JTextField();
   JPanel controlsPanel = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JButton removeHostButton = new JButton();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   public PDBBankFormPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {

      this.setLayout(borderLayout1);
      jLabel1.setToolTipText("");
      jLabel1.setText("FTP archive: ");
      pdbCodeTextField.setToolTipText("");
      pdbCodeTextField.setCaretPosition(0);
      pdbCodeTextField.setText(" ");
      pdbCodeTextField.setColumns(6);
      jLabel2.setToolTipText("");
      jLabel2.setText("4-character PDB ID: ");
      jLabel3.setToolTipText("");
      jLabel3.setText("FTP entry directory: ");
      entryDirTextField.setToolTipText("");
      entryDirTextField.setColumns(50);
      removeHostButton.setToolTipText("Remove server from the list");
      removeHostButton.setText("Remove");
      controlsPanel.setLayout(gridBagLayout1);
      this.add(controlsPanel, BorderLayout.NORTH);
      controlsPanel.add(jLabel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      controlsPanel.add(jLabel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      controlsPanel.add(pdbCodeTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      controlsPanel.add(hostsComboBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
      controlsPanel.add(removeHostButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      controlsPanel.add(entryDirTextField, new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
      controlsPanel.add(jLabel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
          , GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

      hostsComboBox.addItem(defaultPDB_Host);
      entryDirTextField.setText(defaultPDB_EntryDir);

   }

   public String getHost() {
      if (hostsComboBox.getItemCount() < 1) {
         return defaultPDB_Host;
      }

      Object item = hostsComboBox.getSelectedItem();
      if (item != null) {
         return item.toString().trim();
      }
      return defaultPDB_Host;
   }

   public String getEntryDirectory() {
      if (entryDirTextField.getText().trim().length() < 1) {
         return defaultPDB_EntryDir;
      }
      return entryDirTextField.getText().trim();
   }
}
