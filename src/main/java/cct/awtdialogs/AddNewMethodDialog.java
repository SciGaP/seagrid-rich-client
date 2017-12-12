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
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class AddNewMethodDialog
        extends Dialog implements ActionListener,
        ItemListener, KeyListener {

    TextField final_name = null;
    TextField name = null;
    TextField custom_basis = null;
    TextArea Notes = null;
    static String bs_control = "Basis Set";
    Choice method = null, basisSets = null;
    Checkbox Exp = null, fixedBasis = null, EffectiveCorepot = null;
    Checkbox isDFT = null;
    new_SQLChemistryDatabase database = null;
    boolean OK_pressed = false;
    boolean addedSuccessfully = false;
    static final Logger logger = Logger.getLogger(AddNewMethodDialog.class.getCanonicalName());

    public AddNewMethodDialog(String Title, boolean modal,
            new_SQLChemistryDatabase db) {
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
        Label mol_name = new Label("Final Name: ", Label.LEFT);
        sizer.setConstraints(mol_name, c);
        add(mol_name);

        c.gridx += c.gridwidth;
        c.gridwidth = 5;
        c.weightx = 1;
        final_name = new TextField(60);
        final_name.setEditable(false);
        sizer.setConstraints(final_name, c);
        add(final_name);

        // Next row

        c.weightx = 0;
        c.gridx = 0; // Reset
        c.gridwidth = 1;
        c.gridy += c.gridheight;

        Label m_name = new Label("Name: ", Label.LEFT);
        sizer.setConstraints(m_name, c);
        add(m_name);

        c.weightx = 1;
        c.gridx += c.gridwidth;
        //c.gridwidth = 5;

        name = new TextField(60);
        name.addActionListener(this);
        name.addKeyListener(this);
        //name.setEditable(false);
        sizer.setConstraints(name, c);
        add(name);

        // Next row

        c.gridx = 0;
        c.gridy += c.gridheight;
        c.weightx = 0;
        c.gridwidth = 2;

        Exp = new Checkbox("Experiment", true);
        Exp.addItemListener(this);
        sizer.setConstraints(Exp, c);
        add(Exp);

        // Next row

        c.gridx = 0;
        c.gridy += c.gridheight;
        c.weightx = 0;
        c.gridwidth = 2;

        isDFT = new Checkbox("Density Function", false);
        if (Exp.getState()) {
            isDFT.setEnabled(false);
        }
        isDFT.addItemListener(this);
        sizer.setConstraints(isDFT, c);
        add(isDFT);

        // Next row

        c.gridy += c.gridheight;
        c.weightx = 0;
        c.gridwidth = 2;

        fixedBasis = new Checkbox("Fixed Basis Set", false);
        if (Exp.getState()) {
            fixedBasis.setEnabled(false);
        }
        fixedBasis.addItemListener(this);
        sizer.setConstraints(fixedBasis, c);
        add(fixedBasis);

        // Next row

        c.gridy += c.gridheight;
        c.weightx = 0;
        c.gridwidth = 2;

        EffectiveCorepot = new Checkbox("Effective Core Potential", false);
        if (Exp.getState()) {
            EffectiveCorepot.setEnabled(false);
        }
        EffectiveCorepot.addItemListener(this);
        sizer.setConstraints(EffectiveCorepot, c);
        add(EffectiveCorepot);

        // Next row

        c.gridy += c.gridheight;
        c.weightx = 0;
        c.gridwidth = 1;

        Label bset = new Label("Basis Set:", Label.LEFT);
        sizer.setConstraints(bset, c);
        add(bset);

        c.gridx += c.gridwidth;
        c.gridwidth = 1;
        c.weightx = 0;
        basisSets = new Choice();
        basisSets.setName(bs_control);
        String[] available_bs = database.getAvailableBasisSets();
        if (available_bs != null) {
            for (int i = 0; i < available_bs.length; i++) {
                basisSets.add(available_bs[i]);
            }
            if (available_bs.length > 0) {
                basisSets.select(0);
            }
        }
        if (Exp.getState() || fixedBasis.getState()) {
            basisSets.setEnabled(false);
        }
        basisSets.addItemListener(this);
        sizer.setConstraints(basisSets, c);
        add(basisSets);

        c.gridx += c.gridwidth;
        Label custom_l = new Label("Custom:", Label.LEFT);
        sizer.setConstraints(custom_l, c);
        add(custom_l);

        c.gridx += c.gridwidth;
        custom_basis = new TextField("                    ");
        custom_basis.addActionListener(this);
        custom_basis.addKeyListener(this);
        sizer.setConstraints(custom_basis, c);
        add(custom_basis);
        if (Exp.getState() || fixedBasis.getState()) {
            custom_basis.setEnabled(false);
        }

        // Next row

        c.weightx = 0;
        c.gridx = 0; // Reset
        c.gridwidth = 2;
        c.gridy += c.gridheight;

        Label notes_l = new Label("Description: ", Label.LEFT);
        sizer.setConstraints(notes_l, c);
        add(notes_l);

        // Next row

        c.weightx = 1;
        c.gridx = 0;
        c.gridy += c.gridheight;
        c.gridwidth = 6;
        c.gridheight = 3;

        Notes = new TextArea(3, 60);
        sizer.setConstraints(Notes, c);
        add(Notes);

        // Next row

        c.gridx = 0;
        c.gridy += c.gridheight;
        c.gridwidth = 6;
        c.gridheight = 1;
        c.weightx = 0;

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

    /** Handle the key pressed event from the text field. */
    @Override
    public void keyPressed(KeyEvent e) {
        //displayInfo(e, "KEY PRESSED: ");
    }

    /** Handle the key released event from the text field. */
    @Override
    public void keyReleased(KeyEvent e) {
        //displayInfo(e, "KEY RELEASED: ");
        String temp = name.getText();
        if (basisSets.isEnabled()) {
            //temp += "/" + basisSets.getSelectedItem();
            temp += "/" + custom_basis.getText();
        }
        final_name.setText(temp);

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        String controlName = ie.getItemSelectable().toString();
        controlName = controlName.substring(controlName.indexOf('[') + 1);
        controlName = controlName.substring(0, controlName.indexOf(','));

        logger.info("Item: " + ie.getItem());
        logger.info("Item Selectable: " + ie.getItemSelectable());
        logger.info("ControlName: " + controlName);

        if (ie.getItem().equals("Experiment")) {
            if (Exp.getState()) { // Enabled
                fixedBasis.setEnabled(false);
                basisSets.setEnabled(false);
                custom_basis.setEnabled(false);
                EffectiveCorepot.setEnabled(false);
                isDFT.setEnabled(false);
                final_name.setText(name.getText());
            } else {
                processFixedBasisCheckbox();
                fixedBasis.setEnabled(true);
                isDFT.setEnabled(true);
            }

        } else if (ie.getItem().equals("Fixed Basis Set")) {
            processFixedBasisCheckbox();
        } else if (controlName.equals(bs_control)) {
            processBasisSetChoice(ie.getItem().toString());
        }

    }

    void processBasisSetChoice(String item) {
        custom_basis.setText(item);
        String f_name = name.getText();
        if (item.trim().length() == 0 || item.equals("No Basis Set")) {
            final_name.setText(f_name);
        } else {
            f_name += "/" + custom_basis.getText();
            final_name.setText(f_name);
        }
    }

    void processFixedBasisCheckbox() {
        if (fixedBasis.getState()) {
            basisSets.setEnabled(false);
            custom_basis.setEnabled(false);
            EffectiveCorepot.setEnabled(false);
            final_name.setText(name.getText());
        } else {
            basisSets.setEnabled(true);
            custom_basis.setEnabled(true);
            EffectiveCorepot.setEnabled(true);
            final_name.setText(name.getText() + "/" + custom_basis.getText());
        }
    }

    /**
     *
     * @param ae ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String controlName = ae.getSource().toString();
        controlName = controlName.substring(controlName.indexOf('[') + 1);
        controlName = controlName.substring(0, controlName.indexOf(','));
        logger.info("controlName: " + controlName);

        // --- Molecule name is changed
        if (ae.getActionCommand().toString().equals("OK")) {
            addedSuccessfully = database.addNewMethod( final_name.getText(), Notes.getText(),
                    Exp.getState(),
                    fixedBasis.getState(), isDFT.getState(),
                    EffectiveCorepot.getState() );
            OK_pressed = true;
            setVisible(false);
        } else if (ae.getActionCommand().toString().equals("Cancel")) {
            OK_pressed = false;
            setVisible(false);
        }
    }

    public boolean isOKPressed() {
        return OK_pressed;
    }

    public boolean isEddedSuccessfully() {
        return addedSuccessfully;
    }

    /**
     *
     * @return String
     */
    public String getNewMethod() {
        String str = final_name.getText();
        if (str.length() < 1) {
            return null;
        }
        return str;
    }
}
