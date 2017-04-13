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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
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
public class JAtomSelectionPanel
    extends JPanel {
  JSelectionRulePanel jSelectionRulePanel1 = new JSelectionRulePanel();
  JQuickSelectionPanel jQuickSelectionPanel1 = new JQuickSelectionPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JSpecialSelectionPanel jSpecialSelectionPanel1 = new JSpecialSelectionPanel();
  JSelectionTypePanel jSelectionTypePanel1 = new JSelectionTypePanel();

  Map Controls = new HashMap();

  public JAtomSelectionPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    jSelectionTypePanel1.setBorder(new TitledBorder(BorderFactory.
        createLineBorder(Color.lightGray, 1), "Selection"));
    jSpecialSelectionPanel1.setBorder(BorderFactory.createEmptyBorder());
    jQuickSelectionPanel1.setBorder(new TitledBorder(BorderFactory.
        createLineBorder(Color.lightGray, 1), "Quick Selection"));
    jSelectionRulePanel1.setBorder(new TitledBorder(BorderFactory.
        createLineBorder(Color.lightGray, 1), "Selection Rule"));
    this.add(jSelectionRulePanel1,
             new GridBagConstraints(1, 0, 1, 3, 0.5, 0.5
                                    , GridBagConstraints.CENTER,
                                    GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(jSelectionTypePanel1,
             new GridBagConstraints(0, 0, 1, 3, 0.5, 0.5
                                    , GridBagConstraints.NORTHWEST,
                                    GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(jSpecialSelectionPanel1,
             new GridBagConstraints(2, 0, 1, 5, 1.0, 1.0
                                    , GridBagConstraints.NORTHEAST,
                                    GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(jQuickSelectionPanel1,
             new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0
                                    , GridBagConstraints.WEST,
                                    GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));

    Controls.putAll(jSelectionRulePanel1.getControls());
    Controls.putAll(jQuickSelectionPanel1.getControls());
    Controls.putAll(jSpecialSelectionPanel1.getControls());
    Controls.putAll(jSelectionTypePanel1.getControls());

  }

  public Map getControls() {
    return Controls;
  }
}
