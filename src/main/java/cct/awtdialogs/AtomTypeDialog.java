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
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
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
public class AtomTypeDialog
    extends Dialog implements ActionListener, ItemListener {
   TextField atomName, bLength;
   Choice elements, hybrid, bondLength;
   Checkbox drawBond, useBondLength;
   Button OK, Cancel;
   boolean OKpressed = false;
   static final Logger logger = Logger.getLogger(AtomTypeDialog.class.getCanonicalName());
   public AtomTypeDialog(Frame parent, String Title, boolean modal) {
      super(parent, Title, modal);

      //FlowLayout sizer = new  FlowLayout( FlowLayout.CENTER);
      //GridLayout sizer = new  GridLayout( 0, 2, 5 ,5 );
      GridLayout sizer = new GridLayout(0, 1, 3, 3);
      setLayout(sizer);

      Panel P = new Panel();
      P.setLayout(new FlowLayout());

      Label elem = new Label("Element:", Label.RIGHT);
      elements = new Choice();
      elements.addItemListener(this);

      Label elconf = new Label("Valency:", Label.RIGHT);
      hybrid = new Choice();
      hybrid.addItemListener(this);

      Label aname = new Label("Atom Name:", Label.RIGHT);
      atomName = new TextField(" "); //, host_str.length() > 100 ? host_str.length() : 100 ) ;
      atomName.addActionListener(this);

      drawBond = new Checkbox("Draw Bond", true);
      drawBond.addItemListener(this);

      OK = new Button("OK");
      Cancel = new Button("Cancel");

      P.add(elem);
      P.add(elements);

      P.add(elconf);
      P.add(hybrid);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

      P.add(aname);
      P.add(atomName);

      P.add(drawBond);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout());
      useBondLength = new Checkbox("Predefined Bond Length", true);
      useBondLength.setState(false);
      useBondLength.addItemListener(this);
      P.add(useBondLength);
      bondLength = new Choice();
      bondLength.add("0.9");
      bondLength.add("1.0");
      bondLength.add("1.1");
      bondLength.add("1.2");
      bondLength.add("1.3");
      bondLength.add("1.4");
      bondLength.add("1.5");
      bondLength.add("1.6");
      bondLength.add("1.7");
      bondLength.add("2.0");
      bondLength.add("2.5");
      bondLength.add("3.0");
      bondLength.add("3.5");
      bondLength.select(0);
      bondLength.addItemListener(this);
      P.add(bondLength);
      bLength = new TextField(bondLength.getSelectedItem());
      bLength.addActionListener(this);

      if (!useBondLength.getState()) {
         bondLength.setEnabled(false);
         bLength.setEnabled(false);
      }
      P.add(bLength);

      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout());
      P.add(OK);
      //OK.setVisible(true);
      P.add(Cancel);
      add(P);

      OK.addActionListener(this);
      Cancel.addActionListener(this);

      setSize(300, 200);
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
      logger.info("Event: " + ie);
      logger.info("Event: getItem" + ie.getItem());
      logger.info("Event: getItemSelectable" + ie.getItemSelectable());
      String item = (String) ie.getItem();

      if (item.equals("Predefined Bond Length")) {
         bondLength.setEnabled(useBondLength.getState());
         bLength.setEnabled(useBondLength.getState());
      }

      if (ie.getStateChange() == ItemEvent.SELECTED) {
         bLength.setText(bondLength.getSelectedItem());
      }

      repaint();
   }

   public void addElement(String name) {
      elements.add(name);
   }

   public void addElements(String[] names) {
      for (int i = 0; i < names.length; i++) {
         elements.add(names[i]);
      }
   }

   public void clearElements() {
      elements.removeAll();
   }

   public String getElement() {
      return elements.getSelectedItem();
   }

   public int getElementNumber() {
      return elements.getSelectedIndex();
   }

   public String getBondLength() {
      return bLength.getText();
   }

   public boolean isDrawBond() {
      return drawBond.getState();
   }

   public boolean isUseBondLength() {
      return useBondLength.getState();
   }

   public boolean pressedOK() {
      return OKpressed;
   }

   public void setDrawBond(boolean draw) {
      drawBond.setState(draw);
   }

    public void setOKVisible(boolean enable) {
      OK.setVisible(enable);
   }

   public void setCancelVisible(boolean enable) {
      Cancel.setVisible(enable);
   }

}
