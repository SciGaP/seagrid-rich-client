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

package cct.siesta.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SaveSiestaInputDialog
    extends JDialog {
   private JPanel panel1 = new JPanel();
   private BorderLayout borderLayout1 = new BorderLayout();
   private SiestaPanel siestaPanel1 = new SiestaPanel();
   private JPanel jPanel1 = new JPanel();
   private JButton cancelButton = new JButton();
   private JButton validateButton = new JButton();
   private JButton okButton = new JButton();
   private JButton saveEditButton = new JButton();

   public SaveSiestaInputDialog(Frame owner, String title, boolean modal) {
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

   public SaveSiestaInputDialog() {
      this(new Frame(), "SaveSiestaInputDialog", false);
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      cancelButton.setToolTipText("Cancel & hide Dialog");
      cancelButton.setText("Cancel");
      cancelButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            cancelButton_actionPerformed(e);
         }
      });
      validateButton.setToolTipText("");
      validateButton.setText("Validate Data");
      validateButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            jButton2_actionPerformed(e);
         }
      });
      okButton.setToolTipText("");
      okButton.setText("  Save  ");
      okButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            okButton_actionPerformed(e);
         }
      });
      saveEditButton.setToolTipText("");
      saveEditButton.setText("Edit & Save");
      saveEditButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            saveEditButton_actionPerformed(e);
         }
      });
      this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
      getContentPane().add(panel1);
      panel1.add(siestaPanel1, BorderLayout.CENTER);
      panel1.add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(okButton);
      jPanel1.add(saveEditButton);
      jPanel1.add(validateButton);
      jPanel1.add(cancelButton);
   }

   public void setMolecularInterface(MoleculeInterface molec) {
      siestaPanel1.setMolecularInterface(molec);
   }

   public void cancelButton_actionPerformed(ActionEvent e) {
      setVisible(false);
   }

   public void setJava3dUniverse(Java3dUniverse j3dU) {
      siestaPanel1.setJava3dUniverse(j3dU);
   }

   public void okButton_actionPerformed(ActionEvent e) {
      try {
         siestaPanel1.validateData();
         siestaPanel1.saveData(false);
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
      setVisible(false);
   }

   public void jButton2_actionPerformed(ActionEvent e) {
      try {
         siestaPanel1.validateData();
         JOptionPane.showMessageDialog(this, "OK", "No errors found", JOptionPane.INFORMATION_MESSAGE);
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Error in data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
   }

   public void saveEditButton_actionPerformed(ActionEvent e) {
      try {
         siestaPanel1.validateData();
         siestaPanel1.saveData(true);
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
   }
}
