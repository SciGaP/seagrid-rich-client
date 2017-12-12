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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
public class FontSelectorDialog
    extends JDialog implements ActionListener {
   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   FontSelectorPanel fontSelectorPanel1 = new FontSelectorPanel();
   JPanel jPanel1 = new JPanel();
   JButton cancelButton = new JButton();
   JButton okButton = new JButton();
   boolean okPressed = false;

   public FontSelectorDialog(Frame owner, String title, boolean modal) {
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

   public boolean isOKPressed() {
      return okPressed;
   }

   public FontSelectorDialog() {
      this(new Frame(), "FontSelectorDialog", false);
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      cancelButton.addActionListener(this);
      okButton.setToolTipText("");
      okButton.setText("OK");
      okButton.addActionListener(this);
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      getContentPane().add(panel1);
      panel1.add(fontSelectorPanel1, BorderLayout.CENTER);
      panel1.add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(okButton);
      jPanel1.add(cancelButton);
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      if (e.getSource() == okButton) {
         okPressed = true;
         setVisible(false);
      }
      else if (e.getSource() == cancelButton) {
         okPressed = false;
         setVisible(false);
      }

   }

   public static void main(String[] args) {

      SwingUtilities.invokeLater(new Runnable() {
         @Override
        public void run() {
            try {
               UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception exception) {
               exception.printStackTrace();
            }

            FontSelectorDialog frame = new FontSelectorDialog(new Frame(),
                "Select Font", false);
            frame.validate();
            /*
                     frame.hideButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                System.exit(0);
              }
                     }
                     );
             */
            frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
            frame.setVisible(true);

         }
      });
   }

   public String getFontName() {
      return fontSelectorPanel1.fontComboBox.getSelectedItem().toString();
   }

   public int getFontSize() {
      return fontSelectorPanel1.getFontSize();
   }

   public int getFontStyle() {
      return fontSelectorPanel1.getFontStyle();
   }

}
