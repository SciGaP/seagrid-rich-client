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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
public class JPEGOptionsDialog
    extends JDialog {
   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel jPanel2 = new JPanel();
   JButton helpButton = new JButton();
   JButton cancelButton = new JButton();
   JButton okButton = new JButton();
   JPEGOptionsPanel jPEGOptionsPanel1 = new JPEGOptionsPanel();

   boolean okPressed = false;

   public JPEGOptionsDialog(Frame owner, String title, boolean modal) {
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

   public JPEGOptionsDialog() {
      this(new Frame(), "JPEGOptionsDialog", false);
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      helpButton.setToolTipText("");
      helpButton.setText("Help");
      helpButton.addActionListener(new JPEGOptionsDialog_helpButton_actionAdapter(this));
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      cancelButton.addActionListener(new JPEGOptionsDialog_cancelButton_actionAdapter(this));
      okButton.setToolTipText("");
      okButton.setText("  OK  ");
      okButton.addActionListener(new JPEGOptionsDialog_okButton_actionAdapter(this));
      this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
      getContentPane().add(panel1);
      panel1.add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(okButton);
      jPanel2.add(cancelButton);
      jPanel2.add(helpButton);
      panel1.add(jPEGOptionsPanel1, BorderLayout.NORTH);

      addWindowListener(new WindowAdapter() {
         @Override
        public void windowClosing(WindowEvent we) {
            okPressed = false;
            we.getWindow().setVisible(false);
         }
      });

   }

   public int getJPEGQuality() {
      return jPEGOptionsPanel1.getJPEGQuality();
   }

   public boolean isOptimizeHuffmanCode() {
      return jPEGOptionsPanel1.isOptimizeHuffmanCode();
   }

   public boolean isProgressiveEncoding() {
      return jPEGOptionsPanel1.isProgressiveEncoding();
   }

   public void okButton_actionPerformed(ActionEvent e) {
      okPressed = true;
      jPEGOptionsPanel1.validate();
      if (jPEGOptionsPanel1.isSaveSettingsAsDefault()) {
         jPEGOptionsPanel1.saveSettingsAsDefault();
      }
      this.setVisible(false);
   }

   public void cancelButton_actionPerformed(ActionEvent e) {
      okPressed = false;
      this.setVisible(false);
   }

   public boolean isOKPressed() {
      return okPressed;
   }

   public void helpButton_actionPerformed(ActionEvent e) {
      JOptionPane.showMessageDialog(this, "JPEG is \"lossy,\" meaning that the decompressed image isn't quite the same \n"+
                                   "as the one you started with.\n"+
                                   "JPEG compressor lets you pick a file size vs. image quality tradeoff by\n"+
                                   "selecting a quality setting.\n"+
                                   "The quality scale is purely arbitrary; it's not a percentage of anything.\n"+
                                   "For good-quality, full-color source images, the default JPEG quality setting " + JPEGOptionsPanel.defaultJPEGQuality+"\n"+
                                   "is very often the best choice.  This setting is about the lowest you can go\n"+
                                   "without expecting to see defects in a typical image.  Try the default value first;\n"+
                                   "if you see defects, then go up.", "JPEG Options Help",
                                    JOptionPane.INFORMATION_MESSAGE);
   }

   private class JPEGOptionsDialog_helpButton_actionAdapter
       implements ActionListener {
      private JPEGOptionsDialog adaptee;
      JPEGOptionsDialog_helpButton_actionAdapter(JPEGOptionsDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.helpButton_actionPerformed(e);
      }
   }

   private class JPEGOptionsDialog_cancelButton_actionAdapter
       implements ActionListener {
      private JPEGOptionsDialog adaptee;
      JPEGOptionsDialog_cancelButton_actionAdapter(JPEGOptionsDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.cancelButton_actionPerformed(e);
      }
   }

   private class JPEGOptionsDialog_okButton_actionAdapter
       implements ActionListener {
      private JPEGOptionsDialog adaptee;
      JPEGOptionsDialog_okButton_actionAdapter(JPEGOptionsDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.okButton_actionPerformed(e);
      }
   }
}
