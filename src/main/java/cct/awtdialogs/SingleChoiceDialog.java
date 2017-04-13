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
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
public class SingleChoiceDialog
    extends Dialog implements ActionListener, ItemListener {
   boolean OKpressed = false;
   Choice choice;
   Button OK, Cancel;
   static final Logger logger = Logger.getLogger(SingleChoiceDialog.class.getCanonicalName());

   public SingleChoiceDialog(Frame parent, String Title, boolean modal) {
      super(parent, Title, modal);

      GridLayout sizer = new GridLayout(0, 1, 3, 3);
      setLayout(sizer);

      Panel P = new Panel();
      P.setLayout(new FlowLayout());

      choice = new Choice();
      choice.addItemListener(this);

      P.add(choice);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout());

      OK = new Button("OK");
      Cancel = new Button("Cancel");
      OK.addActionListener(this);
      Cancel.addActionListener(this);

      P.add(OK);
      P.add(Cancel);
      add(P);

      setSize(200, 150);

   }

   @Override
  public void actionPerformed(ActionEvent ae) {
      String arg = ae.getActionCommand();
      if (arg.equals("OK")) {
         OKpressed = true;
         //dispose();
         setVisible(false);
      }
      else if (arg.equals("Cancel")) {
         OKpressed = false;
         //dispose();
         setVisible(false);
      }

      else {
         logger.info("Event: " + arg);
      }
   }

   @Override
  public void itemStateChanged(ItemEvent ie) {
      repaint();
   }

   public void addChoice(String name) {
      choice.add(name);
      choice.select(choice.getItemCount() - 1);
   }

   public void addChoices(String[] names) {
      for (int i = 0; i < names.length; i++) {
         choice.add(names[i]);
      }
      choice.select(names.length - 1);
   }

   public void clearChoices() {
      choice.removeAll();
   }

   public String getChoice() {
      return choice.getSelectedItem();
   }

   public int getChoiceIndex() {
      return choice.getSelectedIndex();
   }

   public boolean pressedOK() {
      return OKpressed;
   }

   public void setOKVisible(boolean enable) {
      OK.setVisible(enable);
   }

   public void setCancelVisible(boolean enable) {
      Cancel.setVisible(enable);
   }

}
