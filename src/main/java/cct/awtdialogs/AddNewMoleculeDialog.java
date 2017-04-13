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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
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
public class AddNewMoleculeDialog
    extends Dialog implements ActionListener, ItemListener {
   TextArea aliases = null;
   TextArea notes = null;
   TextArea keywords = null;
   Checkbox isModel = null;
   Choice Charge = null, Mult = null;
   Label name = null;
   boolean OK_pressed = false;
   static final Logger logger = Logger.getLogger(AddNewMoleculeDialog.class.getCanonicalName());

   public AddNewMoleculeDialog(String Title, boolean modal,
                               String molName) {
      super(new Frame(), Title, modal);

      //FlowLayout sizer = new  FlowLayout( FlowLayout.CENTER);
      //GridLayout sizer = new GridLayout(0, 1, 3, 3);
      Panel p;
      GridBagLayout sizer = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      setLayout(sizer);

      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 1;
      c.gridheight = 1;
      //c.weightx = 1;
      //c.weighty = 1;
      c.fill = GridBagConstraints.BOTH;
      c.insets = new Insets(3, 3, 3, 3);

      name = new Label("Name: " + molName, Label.LEFT);
      sizer.setConstraints(name, c);
      add(name);

      // --- Charge, Multiplicity etc

      p = new Panel();
      p.setLayout(new FlowLayout(FlowLayout.LEADING));

      isModel = new Checkbox("Model", false);
      isModel.addItemListener(this);
      p.add(isModel);

      p.add(new Label("   Charge: ", Label.LEFT));
      Charge = new Choice();
      Charge.setName("charge");
      for (int i = -10, j = 0; i <= 10; i++, j++) {
         Charge.add(String.valueOf(i));
         if (i == 0) {
            Charge.select(j);
         }
      }
      Charge.addItemListener(this);
      p.add(Charge);

      p.add(new Label("   Multiplicity: ", Label.LEFT));
      Mult = new Choice();
      Mult.setName("mult");
      for (int i = 1; i <= 5; i++) {
         Mult.add(String.valueOf(i));
      }
      Mult.select(0);
      Mult.addItemListener(this);
      p.add(Mult);

      if (isModel.getState()) {
         Charge.setEnabled(false);
         Mult.setEnabled(false);
      }

      c.gridx = 0;
      c.gridy += c.gridheight;
      c.gridwidth = 6;
      c.gridheight = 1;
      sizer.setConstraints(p, c);
      add(p);

      // --- Add aliases

      c.weightx = 0.0; //reset to the default
      c.gridy += c.gridheight;
      c.gridheight = 1;
      Label alias = new Label("Aliases (max 255 chars):", Label.LEFT);
      sizer.setConstraints(alias, c);
      add(alias);

      c.gridx = 0;
      c.gridy += c.gridheight;
      c.gridwidth = 6;
      c.gridheight = 3;
      c.ipadx = 20;
      aliases = new TextArea(3, 60);
      sizer.setConstraints(aliases, c);
      add(aliases);
      //aliases.addActionListener(this);

      c.gridx = 0;
      c.gridy += c.gridheight;
      c.gridwidth = 1;
      c.gridheight = 1;
      c.weightx = 0.0; //reset to the default
      Label keyw = new Label("Keywords (max 255 chars):", Label.LEFT);
      sizer.setConstraints(keyw, c);
      add(keyw);

      c.gridx = 0;
      c.gridy += c.gridheight;
      c.gridwidth = 6;
      c.gridheight = 3;
      aliases = new TextArea(3, 60);
      sizer.setConstraints(aliases, c);
      add(aliases);
      //aliases.addActionListener(this);


      c.gridx = 0;
      c.gridy += c.gridheight;
      c.gridwidth = 1;
      c.gridheight = 1;

      Label note = new Label("Notes:", Label.LEFT);
      sizer.setConstraints(note, c);
      add(note);

      c.gridx = 0;
      c.gridy += c.gridheight;
      c.gridwidth = 6;
      c.gridheight = 4;
      notes = new TextArea(4, 60);
      sizer.setConstraints(notes, c);
      add(notes);

      p = new Panel();
      p.setLayout(new FlowLayout());

      Button OK = new Button("OK");
      p.add(OK);
      OK.addActionListener(this);

      Button Cancel = new Button("Cancel");
      p.add(Cancel);
      Cancel.addActionListener(this);

      c.gridx = 0;
      c.gridy += c.gridheight;
      c.gridwidth = 6;
      c.gridheight = 1;
      sizer.setConstraints(p, c);
      add(p);

      setSize(500, 450);

   }

   @Override
  public void itemStateChanged(ItemEvent ie) {
      String controlName = ie.getItemSelectable().toString();
      controlName = controlName.substring(controlName.indexOf('[') + 1);
      controlName = controlName.substring(0, controlName.indexOf(','));

      logger.info("Item: " + ie.getItem());
      logger.info("Item Selectable: " + ie.getItemSelectable());
      logger.info("ControlName: " + controlName);

      if (ie.getItem().equals("Model")) {
         if (isModel.getState()) {
            Charge.setEnabled(false);
            Mult.setEnabled(false);
         }
         else {
            Charge.setEnabled(true);
            Mult.setEnabled(true);
         }
      }
      /*
               else if (controlName.equals(bs_control)) {

               }
       */

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

   public boolean isOKPressed() {
      return OK_pressed;
   }

   public boolean isModel() {
      return isModel.getState();
   }

   public int getCharge() {
      return Integer.parseInt(Charge.getSelectedItem());
   }

   public int getMultiplicity() {
      return Integer.parseInt(Mult.getSelectedItem());
   }

   /**
    *
    * @return String
    */
   public String getAliases() {
      String str = aliases.getText();
      if (str.length() < 1) {
         return null;
      }
      return str;
   }

   /**
    *
    * @return String
    */
   public String getNotes() {
      String str = notes.getText();
      if (str.length() < 1) {
         return null;
      }
      return str;
   }

   /**
    *
    * @return String
    */
   public String getKeywords() {
      String str = keywords.getText();
      if (str.length() < 1) {
         return null;
      }
      return str;
   }

   /**
    *
    * @param value String
    */
   public void setAliases(String value) {
      if (value == null || value.length() < 1) {
         return;
      }
      if (aliases == null) {
         aliases = new TextArea(3, 60);
      }
      aliases.setText(value);
   }

   /**
    *
    * @param value String
    */
   public void setNotes(String value) {
      if (value == null || value.length() < 1) {
         return;
      }
      if (notes == null) {
         notes = new TextArea(3, 60);
      }
      notes.setText(value);
   }

   /**
    *
    * @param value String
    */
   public void setKeywords(String value) {
      if (value == null || value.length() < 1) {
         return;
      }
      if (keywords == null) {
         keywords = new TextArea(4, 60);
      }
      keywords.setText(value);
   }

   @Override
  public void setName(String value) {
      if (value == null || value.length() < 1) {
         return;
      }
      if (name == null) {
         name = new Label("Name: " + value, Label.LEFT);
      }
      name.setText("Name: " + value);
   }

   public void setCharge(int charge) {
      int start_charge = Integer.parseInt(Charge.getItem(0));
      int end_charge = Integer.parseInt(Charge.getItem(Charge.getItemCount() - 1));
      String ch = String.valueOf(charge);

      //logger.info("Charge="+charge+"  ch="+ch+" start: "+start_charge+" end: "+end_charge);

      if (charge < start_charge) {
         Charge.insert(ch, 0);
      }
      else if (charge > end_charge) {
         Charge.add(ch);
      }

      Charge.select(ch);

   }

   public void setMultiplicity(int mult) {

      if (mult < 1) {
         return;
      }

      int start_mult = Integer.parseInt(Mult.getItem(0));
      int end_mult = Integer.parseInt(Mult.getItem(Mult.getItemCount() - 1));
      String m = String.valueOf(mult);

      if (mult < start_mult) {
         Mult.insert(m, 0);
      }
      else if (mult > end_mult) {
         Mult.add(m);
      }
      Mult.select(m);
   }

}
