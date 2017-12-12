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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

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
public class JSpecialSelectionPanel
    extends JPanel {
  JButton Sets = new JButton();
  JButton substructure = new JButton();
  JButton monomer_types = new JButton();
  JButton Sphere_selection = new JButton();
  JButton atom_types = new JButton();

  Map Controls = new HashMap();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  public JSpecialSelectionPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    this.setMinimumSize(new Dimension(0, 0));
    Sets.setToolTipText("Select Atoms in Set(s)");
    Sets.setText("Sets");
    substructure.setToolTipText("Select Atoms in Substructure");
    substructure.setVerifyInputWhenFocusTarget(false);
    substructure.setText("Substructure");
    monomer_types.setToolTipText("Select all Atoms of particular Monomers");
    monomer_types.setVerifyInputWhenFocusTarget(false);
    monomer_types.setText("Monomer Types");
    Sphere_selection.setToolTipText(
        "Select all Atoms within specified radius");
    Sphere_selection.setText("Sphere");
    atom_types.setToolTipText("Select Atoms of particular Type");
    atom_types.setText("Atom Types");
    this.add(atom_types, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(2, 2, 2, 2), 0, 0));
    this.add(monomer_types, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(substructure, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
                                                  , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                                  new Insets(2, 2, 2, 2), 0, 0));
    this.add(Sets, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0
                                          , GridBagConstraints.CENTER,
                                          GridBagConstraints.HORIZONTAL,
                                          new Insets(2, 2, 2, 2), 0, 0));
    this.add(Sphere_selection, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0
        , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
        new Insets(2, 2, 2, 2), 0, 0));
    Controls.put("atom_types", atom_types);
    Controls.put("monomer_types", monomer_types);
    Controls.put("substructure", substructure);
    Controls.put("Sets", Sets);
    Controls.put("Sphere_selection", Sphere_selection);
  }

  public Map getControls() {
    return Controls;
  }
}
