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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import cct.j3d.Java3dUniverse;

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
public class GeometrySelectDialog
    extends Dialog implements ActionListener,
    ItemListener {

   List list;
   Java3dUniverse daddy;
   static final Logger logger = Logger.getLogger(GeometrySelectDialog.class.getCanonicalName());

   public GeometrySelectDialog(Java3dUniverse parent, String Title, boolean modal) {
      super(new Frame(), Title, modal);
      daddy = parent;

      //GridLayout sizer = new GridLayout(0, 1, 3, 3);
      //setLayout(sizer);

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      setLayout(gridbag);

      //c.fill = GridBagConstraints.HORIZONTAL;
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 0.0; //reset to the default
      c.gridwidth = GridBagConstraints.REMAINDER;

      //Panel P = new Panel();
      //P.setLayout(new FlowLayout());

      list = new List(10, false);
      //list.addActionListener(this);
      list.addItemListener(this);
      //P.add(list);
      //add(P);
      gridbag.setConstraints(list, c);
      add(list);

      //P = new Panel();
      //P.setLayout(new FlowLayout());

      //Button Hide = new Button("Hide");
      //Hide.addActionListener(this);
      //P.add(Hide);
      //add(P);

      c.weightx = 0.0;
      c.fill = GridBagConstraints.CENTER;
      Button Hide = new Button("Hide");
      Hide.addActionListener(this);

      gridbag.setConstraints(Hide, c);
      add(Hide);

      setSize(170, 250);
   }

   @Override
  public void actionPerformed(ActionEvent ae) {
      String arg = ae.getActionCommand();
      if (arg.equals("Hide")) {
         setVisible(false);
      }
      else {
         logger.info("Event: " + arg);
      }
   }

   @Override
  public void itemStateChanged(ItemEvent ie) {
      if (ie.getStateChange() == ItemEvent.SELECTED) {
         int n = list.getSelectedIndex();
         daddy.setNewGeometry(n);
      }
      repaint();
   }

   public void addItem(String name) {
      list.add(name);
   }

   public void addItems(String[] names) {
      for (int i = 0; i < names.length; i++) {
         list.add(names[i]);
      }
   }

   public void clearItems() {
      list.removeAll();
   }

   public String getSelectedItem() {
      return list.getSelectedItem();
   }

   public int getSelectedIndex() {
      return list.getSelectedIndex();
   }

   public void selectItem(int n) {
      if (n >= 0 && n < list.getItemCount()) {
         list.select(n);
      }
   }
}
