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

package cct.tools.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class SelectFromListDialog
    extends JDialog implements ActionListener {
   private JPanel panel1 = new JPanel();
   private BorderLayout borderLayout1 = new BorderLayout();
   private BorderLayout borderLayout2 = new BorderLayout();
   private JPanel jPanel1 = new JPanel();
   private JButton cancelButton = new JButton();
   private JButton okButton = new JButton();
   private JScrollPane jScrollPane1 = new JScrollPane();
   private JList jList1 = new JList();
   private JButton helpButton = new JButton();

   public SelectFromListDialog(Frame owner, String title) {
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

   public SelectFromListDialog() {
      this(new Frame(), "SelectFromListDialog");
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      this.getContentPane().setLayout(borderLayout2);
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      okButton.setToolTipText("");
      okButton.setText("  OK  ");
      this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
      helpButton.setToolTipText("");
      helpButton.setText("Help");
      helpButton.addActionListener(new SelectFromListDialog_helpButton_actionAdapter(this));
      this.getContentPane().add(panel1, BorderLayout.CENTER);
      this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(okButton);
      jPanel1.add(cancelButton);
      jPanel1.add(helpButton);
      panel1.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(jList1);
      cancelButton.addActionListener(this);
      okButton.addActionListener(this);
   }

   public void setListData(Object[] list) {
      jList1.setListData(list);
   }

   public void selectItem(Object item, boolean enable) {
      jList1.setSelectedValue(item, enable);
   }

   public void enableMultipleSelection(boolean enable) {
      if (enable) {
         jList1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      }
      else {
         jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      }
   }

   public Object getSelectedItem() {
      return jList1.getSelectedValue();
   }

   public Object[] getSelectedItems() {
      return jList1.getSelectedValuesList().toArray();
   }

   @Override
  public void actionPerformed(ActionEvent actionEvent) {
      this.setVisible(false);
      if (actionEvent.getSource() == cancelButton) {
         jList1.clearSelection();
      }
   }

   public void helpButton_actionPerformed(ActionEvent e) {
      JOptionPane.showMessageDialog(this, "To select/deselect particular items hold down the \"Ctrl\" key\n" +
                                    "To select a block of items hold down the \"Shift\" key\n", "Select Items Help",
                                    JOptionPane.INFORMATION_MESSAGE);
   }
   
   public static void main(String s[]) {
     Object[] list = {"HF","B3LYP","WB97XD","MP2","MP4", "CCSD","CCSD(T)","CBS-QB3"};
     SelectFromListDialog dial = new SelectFromListDialog();
     dial.setListData(list);
     dial.setVisible(true);
   }
}

class SelectFromListDialog_helpButton_actionAdapter
    implements ActionListener {
   private SelectFromListDialog adaptee;
   SelectFromListDialog_helpButton_actionAdapter(SelectFromListDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.helpButton_actionPerformed(e);
   }
}
