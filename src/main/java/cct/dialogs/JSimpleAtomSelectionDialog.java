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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import cct.j3d.Java3dUniverse;

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
public class JSimpleAtomSelectionDialog
    extends JDialog implements ActionListener {

   JLabel message;
   JButton OK, Cancel;
   boolean OKpressed = false;
   Object daddy = null;

   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();

   public JSimpleAtomSelectionDialog(Frame owner, String title, boolean modal) {
      super(owner, title, modal);
      try {
         setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public JSimpleAtomSelectionDialog() {
      this(new Frame(), "JSimpleAtomSelectionDialog", false);
   }

   private void jbInit() throws Exception {

      panel1.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
      panel1.setLayout(borderLayout1);

      message = new JLabel("Your message is here ",
                           javax.swing.SwingConstants.CENTER);
      //add(message, FlowLayout.CENTER );
      panel1.add(message, BorderLayout.CENTER);

      JPanel p = new JPanel();
      p.setLayout(new FlowLayout());
      OK = new JButton("OK");
      p.add(OK);
      OK.addActionListener(this);

      Cancel = new JButton("Cancel");
      p.add(Cancel);
      Cancel.addActionListener(this);

      //panel1.add(message, BorderLayout.NORTH );
      panel1.add(p, BorderLayout.SOUTH);

      getContentPane().add(panel1);
   }

   @Override
  public void actionPerformed(ActionEvent actionEvent) {
      if (actionEvent.getSource() == OK) {
         OKpressed = true;
         //setVisible(false);
         if (daddy instanceof Java3dUniverse) {
            Java3dUniverse target = (Java3dUniverse) daddy;
            target.enableMousePicking(false);
            //target.processSelectedAtoms();
            target.endProcessingSelectedAtoms();
         }
         else {
            JOptionPane.showMessageDialog(this,
                                          "Internal ERROR: JSimpleAtomSelectionDialog: Unknown instance!",
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);

         }
      }
      else if (actionEvent.getSource() == Cancel) {
         OKpressed = false;
         if (daddy instanceof Java3dUniverse) {
            Java3dUniverse target = (Java3dUniverse) daddy;
            target.selectAllAtoms(false);
            target.enableMousePicking(false);
            //target.processSelectedAtoms();
            target.endProcessingSelectedAtoms();
         }
         else {
            JOptionPane.showMessageDialog(this,
                                          "Internal ERROR: JSimpleAtomSelectionDialog: Unknown instance!",
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);

         }

         //setVisible(false);
      }

   }

   public void setTargetClass(Object parent) {
      daddy = parent;
   }

   public boolean pressedOK() {
      return OKpressed;
   } // May be used in modal dialog

   public void setMessage(String mess) {
      message.setText(mess);
      pack();
   }

   public void setOKVisible(boolean enable) {
      OK.setVisible(enable);
   }

}
