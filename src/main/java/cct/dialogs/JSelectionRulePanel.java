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
import java.awt.SystemColor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

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
public class JSelectionRulePanel
    extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  ButtonGroup buttonGroup1 = new ButtonGroup();
  JRadioButton Difference = new JRadioButton();
  JRadioButton Intersection = new JRadioButton();
  JRadioButton Union = new JRadioButton();
  Border border1 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
  Border border2 = new TitledBorder(border1, "Selection Rule");

  Map Controls = new HashMap();

  public JSelectionRulePanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    this.setBorder(border2);
    this.setToolTipText("");
    Difference.setToolTipText("Subtract from Selection");
    Difference.setText("Difference");
    Intersection.setToolTipText("Intersect with Selection");
    Intersection.setText("Intersection");
    Union.setToolTipText("Add to Selection");
    Union.setSelected(true);
    Union.setText("Union");
    this.add(Difference, BorderLayout.CENTER);
    this.add(Intersection, BorderLayout.SOUTH);
    this.add(Union, BorderLayout.NORTH);

    buttonGroup1.add(Difference);
    buttonGroup1.add(Intersection);
    buttonGroup1.add(Union);

    Controls.put("Difference", Difference);
    Controls.put("Intersection", Intersection);
    Controls.put("Union", Union);
  }

  public Map getControls() {
    return Controls;
  }
}
