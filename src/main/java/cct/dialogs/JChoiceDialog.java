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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * <p>Title: Picking</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class JChoiceDialog
    extends JDialog implements ActionListener {
   //JList itemList =  new JList();
   boolean OKpressed = false;
   JComboBox itemList = new JComboBox();
   JPanel panel1 = new JPanel();
   JPanel panel2 = new JPanel();
   JPanel panel3 = new JPanel();
   JPanel mainPanel = new JPanel();
   JButton OK = new JButton("Ok");
   JButton Cancel = new JButton("Cancel");

   BorderLayout borderLayout1 = new BorderLayout();
   FlowLayout flowLayout1 = new FlowLayout();
   FlowLayout flowLayout2 = new FlowLayout();
   FlowLayout flowLayout3 = new FlowLayout();
   GridLayout gridLayout1 = new GridLayout(2, 1);

   public JChoiceDialog(Frame owner, String title, boolean modal) {
      super(owner, title, modal);
      try {
         //setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
         addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
               //setLabel("Thwarted user attempt to close window.");
            }
         });

         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public JChoiceDialog() {
      this(new Frame(), "JChoiceDialog", false);
   }

   private void jbInit() throws Exception {
      JLabel header = new JLabel("Select Item(s)");
      itemList.setMinimumSize(new Dimension(60, 19));
      itemList.setMaximumRowCount(20);
      panel1.add(header);

      panel2.add(itemList);

      OK.addActionListener(this);
      Cancel.addActionListener(this);

      panel3.add(OK);
      panel3.add(Cancel);

      panel1.setLayout(flowLayout3);
      panel2.setLayout(flowLayout3);
      panel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      panel3.setLayout(flowLayout3);

      //mainPanel.add(panel1);
      mainPanel.add(panel2);
      mainPanel.add(panel3);

      mainPanel.setLayout(gridLayout1);
      mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
      getContentPane().add(mainPanel);

      //add(panel1);
      //add(panel2);
      //add(panel3);

   }

   public void addItem(Object item) {
      itemList.addItem(item);
   }

   public void addItem(String item) {
      itemList.addItem(item);
   }

   public void selectIndex(int index) {
      if (index < 0 || index >= itemList.getItemCount()) {
         return;
      }
      itemList.setSelectedIndex(index);
   }

   public int getSelectedIndex() {
      return itemList.getSelectedIndex();
   }

   public boolean isApproveOption() {
      return OKpressed;
   }

   /**
    * Close the dialog on a button event.
    *
    * @param actionEvent ActionEvent
    */
   @Override
  public void actionPerformed(ActionEvent actionEvent) {
      if (actionEvent.getSource() == OK) {
         OKpressed = true;
         setVisible(false);
      }
      else if (actionEvent.getSource() == Cancel) {
         OKpressed = false;
         setVisible(false);
      }

   }

}
