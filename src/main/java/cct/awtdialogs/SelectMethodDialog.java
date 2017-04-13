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

package cct.awtdialogs;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SelectMethodDialog
    extends Dialog implements ActionListener, ItemListener {
  java.awt.List Methods = null;
  java.awt.List basisSets = null;
  int SelectedMethod = 0;
  boolean OK_pressed = false;
  static final Logger logger = Logger.getLogger(SelectMethodDialog.class.getCanonicalName());
  
  public SelectMethodDialog(Frame parent, String Title, boolean modal, java.util.List allMethods, java.util.List allBasisSets) {
    super(parent, Title, modal);

    FlowLayout sizer = new FlowLayout(FlowLayout.CENTER);
    //GridLayout sizer = new GridLayout(0, 2, 5, 5);
    setLayout(sizer);

    add(new Label("Select Method", Label.LEFT));

    Methods = new java.awt.List(10, false);

    for (int i = 0; i < allMethods.size(); i++) {
      Map row = (Map) allMethods.get(i);
      Methods.add(row.get("MethodName").toString());
    }
    add(Methods);
    Methods.addActionListener(this);
    Methods.addItemListener(this);

    Button OK = new Button("OK");
    add(OK);
    OK.addActionListener(this);

    Button Cancel = new Button("Cancel");
    add(Cancel);
    Cancel.addActionListener(this);

    setSize(300, 200);

  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    String controlName = ae.getSource().toString();
    controlName = controlName.substring(controlName.indexOf('[') + 1);
    controlName = controlName.substring(0, controlName.indexOf(','));
    logger.info("controlName: " + controlName);

    // --- Molecule name is changed
    if (controlName.equals("Enter molname")) {

    }
    else if (ae.getActionCommand().toString().equals("OK")) {
      OK_pressed = true;
      setVisible(false);
      //dispose();
    }
    else if (ae.getActionCommand().toString().equals("Cancel")) {
      OK_pressed = false;
      setVisible(false);
      //dispose();
    }
  }

  @Override
  public void itemStateChanged(ItemEvent ie) {
    String controlName = ie.getItemSelectable().toString();
    controlName = controlName.substring(controlName.indexOf('[') + 1);
    controlName = controlName.substring(0, controlName.indexOf(','));

    logger.info("Item: " + ie.getItem());
    logger.info("Item Selectable: " + ie.getItemSelectable());
    logger.info("ControlName: " + controlName);

    //if ( controlName.equals(controlMolList) ) {
    SelectedMethod = Methods.getSelectedIndex();
    //selectMolecule(SelectedMolecule);
    /*
            }
            else if ( controlName.equals(controlStrList)) {

            }
     */
  }

  public boolean isOKPressed() {
    return OK_pressed;
  }

  /**
   *
   * @return String
   */
  public String getSelectedMethod() {
    String str = Methods.getSelectedItem();
    if (str.length() < 1) {
      return null;
    }
    return str;
  }

  public void updateMethods(java.util.List allMethods) {
    if (Methods == null) {
      return;
    }
    Methods.removeAll();
    for (int i = 0; i < allMethods.size(); i++) {
      Map row = (Map) allMethods.get(i);
      Methods.add(row.get("MethodName").toString());
    }

  }

}
