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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cct.pdb.PDBBankFetcherInterface;

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
public class PDBFormFrame
    extends JFrame implements ActionListener {
   BorderLayout borderLayout1 = new BorderLayout();
   PDBBankFormPanel pDBBankFormPanel1 = new PDBBankFormPanel();
   JPanel jPanel1 = new JPanel();
   JPanel jPanel2 = new JPanel();
   JPanel jPanel3 = new JPanel();
   BorderLayout borderLayout2 = new BorderLayout();
   JButton helpButton = new JButton();
   JButton cancelButton = new JButton();
   JButton fetchButton = new JButton();
   JCheckBox hideCheckBox = new JCheckBox();
   FlowLayout flowLayout1 = new FlowLayout();

   PDBBankFetcherInterface Fetcher = null;

   public PDBFormFrame(PDBBankFetcherInterface fetcher) {
      Fetcher = fetcher;
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      getContentPane().setLayout(borderLayout1);
      jPanel1.setLayout(borderLayout2);
      helpButton.setToolTipText("");
      helpButton.setText(" Help ");
      helpButton.addActionListener(this);
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      cancelButton.addActionListener(this);
      fetchButton.setToolTipText("Fetch structure");
      fetchButton.setText("Fetch");
      fetchButton.addActionListener(this);
      hideCheckBox.setToolTipText("");
      hideCheckBox.setText("Don\'t hide dialog after Fetch");
      jPanel3.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(jPanel3, BorderLayout.NORTH);
      jPanel3.add(hideCheckBox);
      jPanel1.add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(fetchButton);
      jPanel2.add(cancelButton);
      jPanel2.add(helpButton);
      this.getContentPane().add(pDBBankFormPanel1, BorderLayout.CENTER);
   }

   public static void main(String[] args) {
      PDBBankFetcherInterface fetcher = null;
      PDBFormFrame pdbformframe = new PDBFormFrame(fetcher);
      pdbformframe.pack();
      pdbformframe.setVisible(true);
      //System.exit(0);
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      if (e.getSource() == this.cancelButton) {
         setVisible(false);
      }
      else if (e.getSource() == this.helpButton) {
         JOptionPane.showMessageDialog(this,
                                       "1) Type 4-character PDB Code\n" +
                                       "2) (Optional) Enter PDB archive mirror host name\n" +
                                       "3) (Optional) Enter PDB archive entry directory\n",
                                       "Help",
                                       JOptionPane.INFORMATION_MESSAGE);
      }
      else if (e.getSource() == this.fetchButton) {
         // --- Error check
         String code = pDBBankFormPanel1.pdbCodeTextField.getText().trim();
         if (code.length() != 4) {
            JOptionPane.showMessageDialog(this, "PDB code should consist of 4 characters",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }

         code = code.toLowerCase();

         if (Fetcher == null) {
            JOptionPane.showMessageDialog(this, "PDBBankFetcherInterface is not set...",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }
         Fetcher.fetchStructure(pDBBankFormPanel1.getHost(), pDBBankFormPanel1.getEntryDirectory(), code);
         if (!hideCheckBox.isSelected()) {
            setVisible(false);
         }
      }
   }
}
