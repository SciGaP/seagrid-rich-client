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

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

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
public class FileTransferPanel
    extends JPanel {
   BorderLayout borderLayout1 = new BorderLayout();
   JSplitPane jSplitPane1 = new JSplitPane();
   JFileChooser jFileChooser1 = new JFileChooser();
   FileBrowserPanel jFileBrowserPanel1 = new FileBrowserPanel();
   JScrollPane jScrollPane2 = new JScrollPane();

   public FileTransferPanel() {
      this(null);
   }

   public FileTransferPanel(FileBrowserInterface fileBrowser) {
      try {
         jFileBrowserPanel1.setFileBrowser(fileBrowser);
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      jFileChooser1.setMinimumSize(new Dimension(325, 345));
      jFileChooser1.setControlButtonsAreShown(false);
      jFileChooser1.setDragEnabled(true);
      jFileChooser1.setMultiSelectionEnabled(true);
      this.setLayout(borderLayout1);
      jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jScrollPane2.setMinimumSize(new Dimension(325, 345));
      jScrollPane2.setPreferredSize(new Dimension(325, 345));
      jSplitPane1.add(jScrollPane2, JSplitPane.BOTTOM);
      jSplitPane1.add(jFileBrowserPanel1, JSplitPane.TOP);
      jScrollPane2.getViewport().add(jFileChooser1);

      this.add(jSplitPane1, BorderLayout.NORTH);
   }

   public void setFileBrowser(FileBrowserInterface fileBrowser) {
      jFileBrowserPanel1.setFileBrowser(fileBrowser);
   }

   public void switchFileBrowser(FileBrowserInterface file_browser) {
      jFileBrowserPanel1.switchFileBrowser(file_browser);

   }
}
