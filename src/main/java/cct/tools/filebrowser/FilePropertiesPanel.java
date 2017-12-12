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
import javax.swing.border.TitledBorder;
import java.awt.*;

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
public class FilePropertiesPanel
    extends JPanel {

   static ImageIcon emptyImage = new ImageIcon(cct.resources.Resources.class.
                                               getResource(
                                                       "/cct/images/icons32x32/emptyTransparent.png"));

   BorderLayout borderLayout1 = new BorderLayout();
   JPanel topPanel = new JPanel();
   JPanel centralPanel = new JPanel();
   JLabel emptyLabel = new JLabel();
   public JLabel fileNameLabel = new JLabel();
   JPanel permissionPanel = new JPanel();
   JPanel infoPanel = new JPanel();
   BorderLayout borderLayout2 = new BorderLayout();
   public JLabel fileModifiedLabel = new JLabel();
   JLabel modifiedLabel = new JLabel();
   public JLabel fileSizeLabel = new JLabel();
   JLabel sizeLabel = new JLabel();
   public JLabel fileLocationLabel = new JLabel();
   JLabel locationLabel = new JLabel();
   public JLabel fileTypeLabel = new JLabel();
   JLabel typeLabel = new JLabel();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   FlowLayout flowLayout1 = new FlowLayout();
   PermissionsPanel permissionsPanel1 = new PermissionsPanel();

   public FilePropertiesPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      emptyLabel.setToolTipText("");
      emptyLabel.setIcon(emptyImage);
      emptyLabel.setText("                    ");
      fileNameLabel.setToolTipText("");
      fileNameLabel.setText("File Name");
      centralPanel.setLayout(borderLayout2);
      fileModifiedLabel.setToolTipText("");
      fileModifiedLabel.setText("File Modified date is here");
      modifiedLabel.setToolTipText("");
      modifiedLabel.setText("Modified Date: ");
      fileSizeLabel.setToolTipText("");
      fileSizeLabel.setText("File size is here");
      sizeLabel.setToolTipText("");
      sizeLabel.setText("Size:");
      fileLocationLabel.setToolTipText("");
      fileLocationLabel.setText("File Location is here");
      locationLabel.setToolTipText("");
      locationLabel.setText("Location:");
      fileTypeLabel.setToolTipText("");
      fileTypeLabel.setText("File type is here");
      typeLabel.setToolTipText("");
      typeLabel.setText("Type:");
      infoPanel.setLayout(gridBagLayout1);
      topPanel.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      permissionPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(
          Color.GRAY, 1), "Permissions"));
      this.add(centralPanel, BorderLayout.CENTER);
      topPanel.add(emptyLabel);
      topPanel.add(fileNameLabel);
      infoPanel.add(typeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      infoPanel.add(fileTypeLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      infoPanel.add(locationLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      infoPanel.add(fileLocationLabel,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST, GridBagConstraints.NONE,
                                           new Insets(5, 5, 5, 5), 0, 0));
      infoPanel.add(sizeLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      infoPanel.add(fileSizeLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      infoPanel.add(modifiedLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));
      infoPanel.add(fileModifiedLabel,
                    new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0
                                           , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                                           new Insets(5, 5, 5, 5), 0, 0));
      this.add(topPanel, BorderLayout.NORTH);
      centralPanel.add(permissionPanel, BorderLayout.CENTER);
      permissionPanel.add(permissionsPanel1);
      centralPanel.add(infoPanel, BorderLayout.NORTH);
   }
}
