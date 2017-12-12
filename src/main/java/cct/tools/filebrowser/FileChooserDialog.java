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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cct.interfaces.FileBrowserInterface;

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
public class FileChooserDialog
    extends JDialog implements ActionListener {

   final public static int CONFIRM = 0;
   final public static int CANCEL = 1;
   int keyPressed = CANCEL;

   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   FileBrowserPanel jFileBrowserPanel1 = null;
   JButton cancelButton = new JButton();
   JPanel jPanel1 = new JPanel();
   JButton confirmButton = new JButton();

   public FileChooserDialog(Frame owner, String title, boolean modal,
                            FileBrowserInterface fileBrowser) {
      super(owner, title, modal);
      try {
         jFileBrowserPanel1 = new FileBrowserPanel(fileBrowser);
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private FileChooserDialog() {
      super(new Frame(), "Choose File", true);
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }

      //this(new Frame(), "FileChooserDialog", false);
   }

   public String pwd() {
      return jFileBrowserPanel1.getPWD();
   }

   public String[] getSelectedFiles() {
      return jFileBrowserPanel1.getSelectedFiles();
   }

   public String[] getSelectedFolders() {
      return jFileBrowserPanel1.getSelectedFolders();
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      if (e.getSource() == confirmButton) {
         keyPressed = CONFIRM;
      }
      else {
         keyPressed = CANCEL;
      }
      setVisible(false);
   }

   public int getPressedKey() {
      return keyPressed;
   }

   private void jbInit() throws Exception {

      panel1.setLayout(borderLayout1);
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      cancelButton.addActionListener(this);
      confirmButton.setToolTipText("");
      confirmButton.setText("Confirm Selection");
      confirmButton.addActionListener(this);
      jFileBrowserPanel1.setPreferredSize(new Dimension(452, 100));
      jFileBrowserPanel1.setToolTipText("");
      panel1.setPreferredSize(new Dimension(452, 450));
      panel1.setToolTipText("");
      jPanel1.add(confirmButton);
      jPanel1.add(cancelButton);
      panel1.add(jFileBrowserPanel1, BorderLayout.CENTER);
      panel1.add(jPanel1, BorderLayout.SOUTH);
      this.getContentPane().add(panel1, BorderLayout.CENTER);
      this.getContentPane().add(panel1, BorderLayout.CENTER);

      addWindowListener(new WindowAdapter() {
         @Override
        public void windowClosing(WindowEvent we) {
            keyPressed = CANCEL;
            setVisible(false);
         }
      });

   }

   public void setFileBrowser(FileBrowserInterface fileBrowser) {
      jFileBrowserPanel1.setFileBrowser(fileBrowser);
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

            LocalFileBrowser lfb = new LocalFileBrowser();
            FileChooserDialog dialog = new FileChooserDialog(new Frame(),
                "Select file", true, lfb);
            dialog.setVisible(true);
         }
      });
   }

}
