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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
public class EditHostsDialog
    extends JDialog {

   boolean okPressed = false;

   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JScrollPane jScrollPane1 = new JScrollPane();
   JPanel jPanel1 = new JPanel();
   JList hostsList = new JList();
   JButton cancelButton = new JButton();
   JButton removeButton = new JButton();

   public EditHostsDialog(Frame owner, String title, boolean modal) {
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

   public void setListData(String[] hosts) {
      hostsList.removeAll();
      hostsList.setListData(hosts);
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      cancelButton.addActionListener(new EditHostsDialog_cancelButton_actionAdapter(this));
      removeButton.setToolTipText("");
      removeButton.setText("Remove Selected");
      removeButton.addActionListener(new EditHostsDialog_removeButton_actionAdapter(this));
      this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
      getContentPane().add(panel1);
      panel1.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(hostsList);
      panel1.add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(removeButton);
      jPanel1.add(cancelButton);

      addWindowListener(new WindowAdapter() {
         @Override
        public void windowClosing(WindowEvent we) {
            okPressed = false;
            we.getWindow().setVisible(false);
         }
      });

   }

   public void removeButton_actionPerformed(ActionEvent e) {
      okPressed = true;
      setVisible(false);
   }

   public int[] getSelectedIndices() {
      return hostsList.getSelectedIndices();
   }

   public boolean isOKPressed() {
      return okPressed;
   }

   public void cancelButton_actionPerformed(ActionEvent e) {
      okPressed = false;
      setVisible(false);
   }

   private class EditHostsDialog_cancelButton_actionAdapter
       implements ActionListener {
      private EditHostsDialog adaptee;
      EditHostsDialog_cancelButton_actionAdapter(EditHostsDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.cancelButton_actionPerformed(e);
      }
   }

   private class EditHostsDialog_removeButton_actionAdapter
       implements ActionListener {
      private EditHostsDialog adaptee;
      EditHostsDialog_removeButton_actionAdapter(EditHostsDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.removeButton_actionPerformed(e);
      }
   }

}
