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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class SaveXYZDialog
    extends JDialog {

   boolean okPressed = false;

   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel atomTypesPanel = new JPanel();
   JPanel jPanel2 = new JPanel();
   JPanel unitsPanel = new JPanel();
   ButtonGroup atomsButtonGroup = new ButtonGroup();
   JRadioButton numberRadioButton = new JRadioButton();
   FlowLayout flowLayout1 = new FlowLayout();
   JRadioButton symbolRadioButton = new JRadioButton();
   ButtonGroup unitsButtonGroup = new ButtonGroup();
   JRadioButton angstromRadioButton = new JRadioButton();
   FlowLayout flowLayout2 = new FlowLayout();
   JRadioButton bohrRadioButton = new JRadioButton();
   JButton cancelButton = new JButton();
   JButton okButton = new JButton();

   public SaveXYZDialog(Frame owner, String title) {
      super(owner, title, true);
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public SaveXYZDialog() {
      this(new Frame(), "SaveXYZDialog");
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
      numberRadioButton.setToolTipText("");
      numberRadioButton.setText("Element Numbers");
      atomTypesPanel.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      symbolRadioButton.setToolTipText("");
      symbolRadioButton.setSelected(true);
      symbolRadioButton.setText("Element Symbols");
      atomTypesPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1), "Atom Type Format"));
      unitsPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1), "Units"));
      unitsPanel.setLayout(flowLayout2);
      angstromRadioButton.setToolTipText("");
      angstromRadioButton.setSelected(true);
      angstromRadioButton.setText("Angstroms");
      flowLayout2.setAlignment(FlowLayout.LEFT);
      bohrRadioButton.setToolTipText("");
      bohrRadioButton.setText("Bohrs");
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      cancelButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            cancelButton_actionPerformed(e);
         }
      });
      okButton.setToolTipText("");
      okButton.setText("   OK   ");
      okButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            okButton_actionPerformed(e);
         }
      });
      getContentPane().add(panel1);
      panel1.add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(okButton);
      jPanel2.add(cancelButton);
      panel1.add(unitsPanel, BorderLayout.CENTER);
      unitsPanel.add(angstromRadioButton);
      unitsPanel.add(bohrRadioButton);
      panel1.add(atomTypesPanel, BorderLayout.NORTH);
      atomTypesPanel.add(symbolRadioButton);
      atomTypesPanel.add(numberRadioButton);
      atomsButtonGroup.add(symbolRadioButton);
      atomsButtonGroup.add(numberRadioButton);
      unitsButtonGroup.add(angstromRadioButton);
      unitsButtonGroup.add(bohrRadioButton);

      addWindowListener(new WindowAdapter() {
         @Override
        public void windowClosing(WindowEvent we) {
            okPressed = false;
            we.getWindow().setVisible(false);
         }
      });

   }

   public void okButton_actionPerformed(ActionEvent e) {
      okPressed = true;
      setVisible(false);
   }

   public void cancelButton_actionPerformed(ActionEvent e) {
      okPressed = false;
      setVisible(false);
   }

   public boolean isAtomicNumbers() {
      return numberRadioButton.isSelected();
   }

   public boolean isAtomicSymbols() {
      return this.symbolRadioButton.isSelected();
   }

   public boolean isInAngstroms() {
      return this.angstromRadioButton.isSelected();
   }

   public boolean isInBohrs() {
      return this.bohrRadioButton.isSelected();
   }
}
