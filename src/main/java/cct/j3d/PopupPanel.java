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

package cct.j3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.DebugGraphics;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
public class PopupPanel
    extends JPanel {
   BorderLayout borderLayout1 = new BorderLayout();
   JLabel jLabel = new JLabel();
   JPanel jPanel1 = new JPanel();

   public PopupPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      jLabel.setBackground(new Color(10, 49, 255));
      jLabel.setForeground(Color.yellow);
      jLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
          createLineBorder(Color.gray, 2),
          BorderFactory.createEmptyBorder(0, 5, 0, 5)));
      jLabel.setDebugGraphicsOptions(0);
      jLabel.setOpaque(true);
      jLabel.setDisplayedMnemonic('0');
      jLabel.setText("Selected Atom");

      this.setDebugGraphicsOptions(DebugGraphics.LOG_OPTION);
      jPanel1.setBackground(new Color(100, 222, 222));
      jPanel1.setBorder(BorderFactory.createLineBorder(SystemColor.
          controlShadow, 1));
      this.add(jPanel1, BorderLayout.NORTH);
      jPanel1.add(jLabel);
   }

   public void setText(String text) {
      jLabel.setText(text);
   }

}
