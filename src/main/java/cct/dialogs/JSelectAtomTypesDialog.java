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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
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
public class JSelectAtomTypesDialog
    extends JDialog {
   JPanel mainPanel = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JList Elements = new JList();
   JList atomNames = new JList();
   JPanel buttonPanel = new JPanel();
   JButton OK = new JButton();
   JButton Cancel = new JButton();
   JButton Help = new JButton();
   JPanel P1 = new JPanel();
   boolean okPressed = false;
   GridLayout gridLayout1 = new GridLayout();
   JScrollPane jScrollPane1 = new JScrollPane();
   JScrollPane jScrollPane2 = new JScrollPane();

   public JSelectAtomTypesDialog(Frame owner, String title, boolean modal) {
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

   public JSelectAtomTypesDialog(String title, boolean modal) {
      this(new Frame(), title, modal);
   }
   
   public JSelectAtomTypesDialog() {
      this(new Frame(), "JSelectAtomTypesDialog", false);
   }

   private void jbInit() throws Exception {
      mainPanel.setLayout(borderLayout1);
      OK.setText("OK");
      OK.addActionListener(new JSelectAtomTypesDialog_OK_actionAdapter(this));
      Cancel.setText("Cancel");
      Cancel.addActionListener(new JSelectAtomTypesDialog_Cancel_actionAdapter(this));
      Help.setToolTipText("");
      Help.setText("Help");
      Elements.setBorder(BorderFactory.createEmptyBorder());
      Elements.setMaximumSize(new Dimension(0, 200));
      Elements.setVisibleRowCount(10);
      atomNames.setBorder(BorderFactory.createEmptyBorder());
      atomNames.setMaximumSize(new Dimension(200, 200));
      atomNames.setVisibleRowCount(10);
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      P1.setLayout(gridLayout1);
      P1.setMaximumSize(new Dimension(32767, 200));
      gridLayout1.setColumns(2);
      jScrollPane1.setBorder(new TitledBorder(BorderFactory.createLineBorder(
          Color.lightGray, 1), "Atom Names"));
      jScrollPane2.setBorder(new TitledBorder(BorderFactory.createLineBorder(new
          Color(127, 157, 185), 1), "Elements"));
      getContentPane().add(mainPanel);
      buttonPanel.add(OK);
      buttonPanel.add(Cancel);
      buttonPanel.add(Help);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      mainPanel.add(P1, BorderLayout.NORTH);
      P1.add(jScrollPane2);
      jScrollPane2.getViewport().add(Elements);
      P1.add(jScrollPane1);
      jScrollPane1.getViewport().add(atomNames);

   }

   public void setupElements(Set<String> elements) {
      Elements.removeAll();
      Iterator<String> i = elements.iterator();
      String el[] = new String[elements.size()];
      int count = 0;
      while (i.hasNext()) {
         el[count] = i.next();
         ++count;
      }
      Arrays.sort(el);
      Elements.setListData(el);
      Elements.updateUI();
      this.pack();
   }

   public void setupAtomNames(Set<String> atom_names) {
      atomNames.removeAll();
      Iterator<String> i = atom_names.iterator();
      String el[] = new String[atom_names.size()];
      int count = 0;
      while (i.hasNext()) {
         el[count] = i.next();
         ++count;
      }
      Arrays.sort(el);
      atomNames.setListData(el);
      this.pack();
   }

   public void OK_actionPerformed(ActionEvent e) {
      okPressed = true;
      setVisible(false);
   }

   public void Cancel_actionPerformed(ActionEvent e) {
      okPressed = false;
      setVisible(false);
   }

   public boolean isOK() {
      return okPressed;
   }

   public Object[] getSelectedElements() {
      return Elements.getSelectedValues();
   }

   public Object[] getSelectedAtomNames() {
      return atomNames.getSelectedValues();
   }
}

class JSelectAtomTypesDialog_Cancel_actionAdapter
    implements ActionListener {
   private JSelectAtomTypesDialog adaptee;
   JSelectAtomTypesDialog_Cancel_actionAdapter(JSelectAtomTypesDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.Cancel_actionPerformed(e);
   }
}

class JSelectAtomTypesDialog_OK_actionAdapter
    implements ActionListener {
   private JSelectAtomTypesDialog adaptee;
   JSelectAtomTypesDialog_OK_actionAdapter(JSelectAtomTypesDialog adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.OK_actionPerformed(e);
   }
}
