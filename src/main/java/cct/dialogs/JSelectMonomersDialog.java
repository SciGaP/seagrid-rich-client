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
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class JSelectMonomersDialog
    extends JDialog {
   JPanel mainPanel = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel buttonsPanel = new JPanel();
   JButton Help = new JButton();
   JButton Cancel = new JButton();
   JButton OK = new JButton();
   JList Monomers = new JList();
   boolean okPressed = false;
   Frame owner = null;
   JScrollPane jScrollPane1 = new JScrollPane();

   public JSelectMonomersDialog(Frame owner, String title, boolean modal) {
      super(owner, title, modal);
      this.owner = owner;
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

    public JSelectMonomersDialog(String title, boolean modal) {
      this(new Frame(), title, modal);
   }
   
   public JSelectMonomersDialog() {
      this(new Frame(), "JSelectMonomersDialog", false);
   }

   private void jbInit() throws Exception {
      mainPanel.setLayout(borderLayout1);
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      Help.setText("Help");
      Help.addActionListener(new JSelectMonomersDialog_Help_actionAdapter(this));
      Cancel.setText("Cancel");
      Cancel.addActionListener(new JSelectMonomersDialog_Cancel_actionAdapter(this));
      OK.setText("Ok");
      OK.addActionListener(new JSelectMonomersDialog_OK_actionAdapter(this));
      jScrollPane1.setBorder(new TitledBorder(BorderFactory.createLineBorder(
          Color.lightGray, 1), "Monomers"));
      getContentPane().add(mainPanel);
      mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
      buttonsPanel.add(OK);
      buttonsPanel.add(Cancel);
      buttonsPanel.add(Help);
      mainPanel.add(jScrollPane1, BorderLayout.NORTH);
      jScrollPane1.getViewport().add(Monomers);
   }

   public void OK_actionPerformed(ActionEvent e) {
      okPressed = true;
      setVisible(false);
   }

   public void Cancel_actionPerformed(ActionEvent e) {
      okPressed = false;
      setVisible(false);
   }

   public void Help_actionPerformed(ActionEvent e) {
      if (owner == null) {
         owner = new JFrame();
      }
      JOptionPane.showMessageDialog(owner,
                                    "Select Monomer(s) from the list\n" +
                                    "For multiple selection use Shift and/or Ctrl keys",
                                    "Help",
                                    JOptionPane.INFORMATION_MESSAGE);

   }

   public void setupMonomers(Set<String> elements) {
      Monomers.removeAll();
      Iterator<String> i = elements.iterator();
      String el[] = new String[elements.size()];
      int count = 0;
      while (i.hasNext()) {
         el[count] = i.next();
         ++count;
      }
      Arrays.sort(el);
      Monomers.setListData(el);
      //Monomers.updateUI();
      this.pack();
   }

   public boolean isOK() {
      return okPressed;
   }

   public Object[] getSelectedMonomers() {
      return Monomers.getSelectedValues();
   }

}

class JSelectMonomersDialog_Help_actionAdapter
    implements ActionListener {
   private JSelectMonomersDialog adaptee;
   JSelectMonomersDialog_Help_actionAdapter(JSelectMonomersDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.Help_actionPerformed(e);
   }
}

class JSelectMonomersDialog_Cancel_actionAdapter
    implements ActionListener {
   private JSelectMonomersDialog adaptee;
   JSelectMonomersDialog_Cancel_actionAdapter(JSelectMonomersDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.Cancel_actionPerformed(e);
   }
}

class JSelectMonomersDialog_OK_actionAdapter
    implements ActionListener {
   private JSelectMonomersDialog adaptee;
   JSelectMonomersDialog_OK_actionAdapter(JSelectMonomersDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.OK_actionPerformed(e);
   }
}
