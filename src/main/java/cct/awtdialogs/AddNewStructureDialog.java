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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import cct.database.new_SQLChemistryDatabase;

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
public class AddNewStructureDialog
    extends Dialog implements ActionListener {
   TextField name = null;
   TextArea notes = null;
   Choice method = null;
   new_SQLChemistryDatabase database = null;
   AddNewMethodDialog addMethod = null;
   boolean OK_pressed = false;
   static final Logger logger = Logger.getLogger(AddNewStructureDialog.class.getCanonicalName());

   public AddNewStructureDialog(String Title, boolean modal,
                                String molName, new_SQLChemistryDatabase db) {
      super(new Frame(), Title, modal);

      this.database = db;
      //FlowLayout sizer = new  FlowLayout( FlowLayout.CENTER);
      //GridLayout sizer = new GridLayout(0, 2, 5, 5);
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
      Label mol_name = new Label("Molecule: " + molName, Label.LEFT);
      sizer.setConstraints(mol_name, c);
      add(mol_name);

      c.gridy += c.gridheight;
      Label str_name = new Label("Structure Name:", Label.LEFT);
      sizer.setConstraints(str_name, c);
      add(str_name);

      c.gridy += c.gridheight;
      c.gridwidth = 6;
      name = new TextField(60);
      sizer.setConstraints(name, c);
      add(name);

      c.gridy += c.gridheight;
      c.gridwidth = 1;
      Label method_l = new Label("Method:", Label.LEFT);
      sizer.setConstraints(method_l, c);
      add(method_l);

      c.gridx += c.gridwidth;
      c.gridwidth = 4;
      c.weightx = 1.0;
      method = new Choice();
      //method.add("                              ");
      String[] available_methods = database.getAvailableMethods();
      for (int i = 0; i < available_methods.length; i++) {
         method.add(available_methods[i]);
      }
      if (available_methods.length > 0) {
         method.select(0);
      }
      sizer.setConstraints(method, c);
      add(method);

      c.gridx += c.gridwidth;
      c.gridwidth = 1;
      c.weightx = 0;
      Button new_method = new Button("New Method");
      sizer.setConstraints(new_method, c);
      add(new_method);
      new_method.addActionListener(this);

      c.gridx = 0; // reset
      c.gridy += c.gridheight;
      c.gridwidth = 1;
      Label note = new Label("Notes:", Label.LEFT);
      sizer.setConstraints(note, c);
      add(note);

      c.gridy += c.gridheight;
      c.gridwidth = 6;
      c.gridheight = 4;
      notes = new TextArea(4, 60);
      sizer.setConstraints(notes, c);
      add(notes);

      c.gridy += c.gridheight;
      c.gridwidth = 6;
      c.gridheight = 1;

      Panel p = new Panel();
      p.setLayout(new FlowLayout());

      Button OK = new Button("OK");
      p.add(OK);
      OK.addActionListener(this);

      Button Cancel = new Button("Cancel");
      p.add(Cancel);
      Cancel.addActionListener(this);
      sizer.setConstraints(p, c);
      add(p);

      setSize(500, 400);

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
      else if (ae.getActionCommand().toString().equals("New Method")) {
         if (addMethod == null) {
            addMethod = new AddNewMethodDialog("Define New Method", true,
                                               database);
         }
         addMethod.setVisible(true);
         if (addMethod.isOKPressed() && addMethod.isEddedSuccessfully()) {
            String new_method = addMethod.getNewMethod();
            method.removeAll();
            String[] available_methods = database.getAvailableMethods();
            for (int i = 0; i < available_methods.length; i++) {
               method.add(available_methods[i]);
            }
            method.select(new_method);
         }

      }
   }

   public boolean isOKPressed() {
      return OK_pressed;
   }

   /**
    *
    * @return String
    */
   @Override
  public String getName() {
      String str = name.getText();
      if (str.length() < 1) {
         return null;
      }
      return str;
   }

   /**
    *
    * @return String
    */
   public String getMethod() {
      String str = method.getSelectedItem();
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
    * @param value String
    */
   @Override
  public void setName(String value) {
      if (value == null || value.length() < 1) {
         return;
      }
      if (name == null) {
         name = new TextField();
      }
      name.setText(value);
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
         notes = new TextArea(3, 30);
      }
      notes.setText(value);
   }

}
