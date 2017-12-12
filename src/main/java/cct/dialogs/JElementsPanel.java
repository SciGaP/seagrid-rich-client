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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cct.modelling.ChemicalElements;

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
enum ELEMENTS_PRESENTATION {
   SHOW_AS_TABLE, SHOW_AS_COMBOBOX
}

public class JElementsPanel
    extends JPanel implements ActionListener, ItemListener {
   BorderLayout borderLayout1 = new BorderLayout();
   java.util.List Buttons;
   java.util.List<JButton> buttonStore = new ArrayList<JButton> (110);
   JComboBox elementsCombobox = new JComboBox();

   JButton selectedButton = null;
   JPanel tablePanel = null;
   JPanel comboboxPanel = null;

   public JElementsPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);

      createButtons();
      adjustButtons();

      //Buttons = buildTableForMainElements(tablePanel);
      tablePanel = buildTableForMainElements(tablePanel);
      buildComboxBox();

      this.add(tablePanel);
   }

   void createButtons() {
      Insets buttonInsets = new Insets(2, 1, 2, 1);

      for (int i = 0; i < ChemicalElements.getNumberOfElements(); i++) {
         JButton elemButton = new JButton(ChemicalElements.getElementSymbol(i));
         buttonStore.add(elemButton);
         elemButton.setMargin(buttonInsets);
         elemButton.addActionListener(this);
         elemButton.validate();
      }
   }

   void adjustButtons() {
      Insets buttonInsets = new Insets(2, 1, 2, 1);
      Dimension maxMin = new Dimension(0, 0);

      for (int i = 0; i < buttonStore.size(); i++) {
         JButton elemButton = buttonStore.get(i);
         Dimension min = elemButton.getMinimumSize();
         if (min.height > maxMin.height) {
            maxMin.height = min.height;
         }
         if (min.width > maxMin.width) {
            maxMin.width = min.width;
         }
      }
      if (maxMin.width < maxMin.height) {
         maxMin.width = maxMin.height;
      }
      else {
         maxMin.height = maxMin.width;
      }
      //logger.info("height: " + maxMin.height + " width: " +
      //                   maxMin.width);

      for (int i = 0; i < buttonStore.size(); i++) {
         JButton elemButton = buttonStore.get(i);
         elemButton.setMargin(buttonInsets);
         elemButton.setPreferredSize(maxMin);
         elemButton.setMaximumSize(maxMin);
         elemButton.setMinimumSize(maxMin);
      }

   }

   void buildComboxBox() {
      elementsCombobox.removeAllItems();
      elementsCombobox.addItemListener(this);

      for (int i = 0; i < buttonStore.size(); i++) {
         JButton elemButton = buttonStore.get(i);
         elementsCombobox.addItem(ChemicalElements.getElementSymbol(i) + " - " + ChemicalElements.getElementName(i));
      }

      comboboxPanel = new JPanel();
      comboboxPanel.add(new JLabel("Elements: "));
      comboboxPanel.add(elementsCombobox);

      FlowLayout flowLayout1 = new FlowLayout(FlowLayout.LEFT);
      comboboxPanel.setLayout(flowLayout1);
   }

   public void showElementsAsTable(boolean show) {
      Component[] comp = this.getComponents();
      for (int i = 0; i < comp.length; i++) {
         if (comp[i] == comboboxPanel) {
            if (!show) {
               return;
            }
            this.remove(comboboxPanel);
            this.add(tablePanel);
            this.validate();
            return;
         }
         else if (comp[i] == tablePanel) {
            if (show) {
               return;
            }
            this.remove(tablePanel);
            this.add(comboboxPanel);
            this.validate();
            return;

         }
      }
   }

   //public ArrayList buildTableForMainElements(JPanel P) {
   public JPanel buildTableForMainElements(JPanel P) {
      java.util.List buttons = new ArrayList();
      String tooltip;
      int elem, hight;
      Dimension dim;

      if (P == null) {
         P = new JPanel();
      }

      // java.awt.Insets[top=4,left=16,bottom=4,right=16]

      JLabel label;
      JButton button;
      GridBagLayout gridbag = new GridBagLayout();
      P.setLayout(gridbag);
      GridBagConstraints c;

      // --- Build group number labels

      c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 1;
      c.gridheight = 1;
      //c.weightx = 1;
      //c.weighty = 1;
      //c.fill = GridBagConstraints.BOTH;
      c.fill = GridBagConstraints.CENTER;
      c.insets = new Insets(1, 1, 1, 1);

      label = new JLabel("Group");
      gridbag.setConstraints(label, c);
      P.add(label);

      for (c.gridx = 1; c.gridx < 19; c.gridx++) {
         label = new JLabel(String.valueOf(c.gridx),
                            javax.swing.SwingConstants.CENTER);
         gridbag.setConstraints(label, c);
         P.add(label);
      }

      // --- Add "Period" label

      c.gridx = 0;
      c.gridy += c.gridheight;
      label = new JLabel("Period");
      gridbag.setConstraints(label, c);
      P.add(label);

      // --- Add first period of elements

      c.gridx = 0;
      c.gridy += c.gridheight;
      label = new JLabel("1", javax.swing.SwingConstants.CENTER);
      gridbag.setConstraints(label, c);
      P.add(label);

      c.gridx = 1;
      //symbol = ChemicalElements.getElementSymbol(1);
      button = buttonStore.get(1); //new JButton(symbol);
      tooltip = ChemicalElements.getElementName(1) + " " + String.valueOf(1) +
          " " + ChemicalElements.getAtomicWeight(1);
      button.setToolTipText(tooltip);
      //button.setMargin(buttonInsets);
      hight = button.getMinimumSize().height;
      //button.setSize(hight,hight);
      dim = button.getMinimumSize();
      dim.width = hight;
      //button.setPreferredSize(dim);
      //button.setMaximumSize(dim);
      //button.setPreferredSize(maxMin);
      //button.setMaximumSize(maxMin);
      Rectangle r = button.getBounds();
      Insets ins = button.getInsets();
      //logger.info("dim min: " + dim + " bounds: " + r + "  Hight: " +
      //                   hight + "  insets: " + ins);
      //button.setMaximumSize(d);
      //button.setSize(d);
      gridbag.setConstraints(button, c);
      P.add(button);
      buttons.add(button);

      c.gridx = 18;
      //symbol = ChemicalElements.getElementSymbol(2);
      button = buttonStore.get(2); //new JButton(symbol);
      tooltip = ChemicalElements.getElementName(2) + " " + String.valueOf(2) +
          " " + ChemicalElements.getAtomicWeight(2);
      button.setToolTipText(tooltip);

      //button.setMargin(buttonInsets);
      //button.setPreferredSize(dim);
      //button.setPreferredSize(maxMin);
      //button.setMaximumSize(dim);
      //button.setMaximumSize(maxMin);
      //button.setMinimumSize(maxMin);


      gridbag.setConstraints(button, c);
      P.add(button);
      buttons.add(button);

      // --- Add second period of elements

      c.gridx = 0;
      c.gridy += c.gridheight;
      label = new JLabel("2", javax.swing.SwingConstants.CENTER);
      gridbag.setConstraints(label, c);
      P.add(label);

      for (c.gridx = 1, elem = 3; c.gridx < 3; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);
         //button.setMinimumSize(maxMin);


         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      for (c.gridx = 13, elem = 5; c.gridx < 19; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);
         //button.setMinimumSize(maxMin);


         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      // --- Add the third period of elements

      c.gridx = 0;
      c.gridy += c.gridheight;
      label = new JLabel("3", javax.swing.SwingConstants.CENTER);
      gridbag.setConstraints(label, c);
      P.add(label);

      for (c.gridx = 1, elem = 11; c.gridx < 3; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      for (c.gridx = 13, elem = 13; c.gridx < 19; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      // --- Add the forth period of elements

      c.gridx = 0;
      c.gridy += c.gridheight;
      label = new JLabel("4", javax.swing.SwingConstants.CENTER);
      gridbag.setConstraints(label, c);
      P.add(label);

      for (c.gridx = 1, elem = 19; c.gridx < 19; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      // --- Add the fifth period of elements

      c.gridx = 0;
      c.gridy += c.gridheight;
      label = new JLabel("5", javax.swing.SwingConstants.CENTER);
      gridbag.setConstraints(label, c);
      P.add(label);

      for (c.gridx = 1, elem = 37; c.gridx < 19; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      // --- Add the sixth period of elements

      c.gridx = 0;
      c.gridy += c.gridheight;
      label = new JLabel("6", javax.swing.SwingConstants.CENTER);
      gridbag.setConstraints(label, c);
      P.add(label);

      for (c.gridx = 1, elem = 55; c.gridx < 4; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }
      // --- Skip Lanthanides
      for (c.gridx = 4, elem = 72; c.gridx < 19; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      // --- Add the seventh period of elements

      c.gridx = 0;
      c.gridy += c.gridheight;
      label = new JLabel("7", javax.swing.SwingConstants.CENTER);
      gridbag.setConstraints(label, c);
      P.add(label);

      for (c.gridx = 1, elem = 87; c.gridx < 4; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }
      // --- Skip Actinides
      for (c.gridx = 4, elem = 104;
           c.gridx < 19 && elem < ChemicalElements.getNumberOfElements();
           c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      // --- Add Lanthanides

      c.gridx = 1;
      c.gridwidth = 2;
      c.gridy += c.gridheight + 1;
      label = new JLabel("Lanthanide:", javax.swing.SwingConstants.RIGHT);
      gridbag.setConstraints(label, c);
      P.add(label);

      c.gridwidth = 1;

      for (c.gridx = 4, elem = 58; c.gridx < 18; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      // --- Add Actinide

      c.gridx = 1;
      c.gridwidth = 2;
      c.gridy += c.gridheight;
      label = new JLabel("Actinide:", javax.swing.SwingConstants.RIGHT);
      gridbag.setConstraints(label, c);
      P.add(label);

      c.gridwidth = 1;

      for (c.gridx = 4, elem = 90; c.gridx < 18; c.gridx++, elem++) {
         //symbol = ChemicalElements.getElementSymbol(elem);
         button = buttonStore.get(elem); //new JButton(symbol);
         tooltip = ChemicalElements.getElementName(elem) + " " +
             String.valueOf(elem) + " " +
             ChemicalElements.getAtomicWeight(elem);
         button.setToolTipText(tooltip);

         //button.setMargin(buttonInsets);
         //button.setPreferredSize(dim);
         //button.setPreferredSize(maxMin);
         //button.setMaximumSize(dim);
         //button.setMaximumSize(maxMin);

         gridbag.setConstraints(button, c);
         P.add(button);
         buttons.add(button);
      }

      //return buttons;
      return P;
   }

   public Font getElementsFont() {
      JButton elemButton = buttonStore.get(0);
      return elemButton.getFont();
   }

   public void setElementsFont(Font newFont) {
      for (int i = 0; i < buttonStore.size(); i++) {
         JButton elemButton = buttonStore.get(i);
         elemButton.setFont(newFont);
      }

      adjustButtons();
      this.validate();

   }

   public java.util.List getButtons() {
      //return Buttons;
      return buttonStore;
   }

   @Override
  public void actionPerformed(ActionEvent ae) {
      selectedButton = (JButton) ae.getSource();
      int index = buttonStore.indexOf(selectedButton);
      if (index == -1) {
         index = 0;
      }
      if (elementsCombobox.isEnabled()) {
         elementsCombobox.setEnabled(false);
         elementsCombobox.setSelectedIndex(index);
         elementsCombobox.setEnabled(true);
      }
   }

   @Override
  public void itemStateChanged(ItemEvent e) {
      if (!elementsCombobox.isEnabled()) {
         return;
      }
      if (e.getStateChange() == ItemEvent.DESELECTED) {
         return;
      }
      elementsCombobox.setEnabled(false);
      int index = elementsCombobox.getSelectedIndex();
      selectedButton = buttonStore.get(index);
      selectedButton.doClick();
      elementsCombobox.setEnabled(true);
   }
}
