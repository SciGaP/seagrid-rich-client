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

package cct.gaussian.ui;

import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * <p>Title: Gasussian Utility Classes</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class TitleSectionPanel
    extends JScrollPane {

   JTextArea jTextArea1 = new JTextArea();

   public TitleSectionPanel() {
      try {
         jbInit();
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      jTextArea1.setToolTipText("");
      jTextArea1.setText("My Gaussian Job");
      jTextArea1.setRows(2);
      this.getViewport().add(jTextArea1);
   }

   public void resetPanel() {
      jTextArea1.setEnabled(false);
      jTextArea1.setText("My Gaussian Job");
      jTextArea1.setEnabled(true);
   }

   public void setFontForText(Font newFont) {
      jTextArea1.setFont(newFont);
   }

   public Font getFontForText() {
      return jTextArea1.getFont();
   }

   public void setText(String text) {
      jTextArea1.setText(text);
      jTextArea1.setCaretPosition(0);
   }

   public String getText() {
      String lines[] = jTextArea1.getText().split("\n");
      String text = "";
      for (int i = 0; i < lines.length; i++) {
         String trimmed = lines[i].trim();
         if (trimmed.length() != 0) {
            text += trimmed + "\n";
         }
      }
      return text;
   }

}
