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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class FilePropertiesDialog
    extends JDialog implements ActionListener {

   final static int SET = 0;
   final static int CANCEL = 1;
   int keyPressed = CANCEL;

   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   FilePropertiesPanel fileProperties = new FilePropertiesPanel();
   JPanel buttonPanel = new JPanel();
   JButton jButton1 = new JButton();
   JButton jButton2 = new JButton();
   JButton setButton = new JButton();
   public FilePropertiesDialog(Frame owner, String title) {
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

   public FilePropertiesDialog() {
      this(new Frame(), "File Properties Dialog");
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      jButton1.setEnabled(false);
      jButton1.setToolTipText("");
      jButton1.setText("Help");
      jButton2.setToolTipText("");
      jButton2.setText("Cancel");
      setButton.setToolTipText("");
      setButton.setText("Set");
      getContentPane().add(panel1);
      panel1.add(fileProperties, BorderLayout.CENTER);
      panel1.add(buttonPanel, BorderLayout.SOUTH);
      buttonPanel.add(setButton);
      buttonPanel.add(jButton2);
      buttonPanel.add(jButton1);

      jButton2.addActionListener(this);
      setButton.addActionListener(this);
   }

   public int showDialog() {
      setVisible(true);
      return keyPressed;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      if (e.getSource() == setButton) {
         keyPressed = SET;
      }
      else {
         keyPressed = CANCEL;
      }
      setVisible(false);
   }

   public void setFileName(String name) {
      fileProperties.fileNameLabel.setText(name);
   }

   public void setFileType(String type) {
      fileProperties.fileTypeLabel.setText(type);
   }

   public void setFileSize(String size) {
      fileProperties.fileSizeLabel.setText(size);
   }

   public void setFileModified(String modified) {
      fileProperties.fileModifiedLabel.setText(modified);
   }

   public void setFileLocation(String location) {
      fileProperties.fileLocationLabel.setText(location);
   }

   public void setPermissions(int perm) {
      fileProperties.permissionsPanel1.setupControls(perm);
   }

   public void setPermissions(String permissions) {
      fileProperties.permissionsPanel1.setupControls(permissions);
   }

   public int getPermissions() {
      return fileProperties.permissionsPanel1.getPermissions();
   }

   public void setFileIcon(ImageIcon icon) {
      fileProperties.emptyLabel.setIcon(icon);
   }

}
