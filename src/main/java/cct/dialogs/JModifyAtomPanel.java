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

import cct.interfaces.AtomInterface;
import cct.interfaces.GraphicsRendererInterface;
import cct.j3d.ChemicalElementsColors;
import cct.modelling.ChemicalElements;
import org.scijava.vecmath.Color3f;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
public class JModifyAtomPanel
    extends JPanel {

   GraphicsRendererInterface renderer = null;
   Color colorBuffer = new Color(0, 0, 0);

   JPanel jPanel1 = new JPanel();
   JLabel jLabel1 = new JLabel();
   JComboBox Element = new JComboBox(ChemicalElements.getAllElements());
   JLabel jLabel2 = new JLabel();
   JTextField Label = new JTextField();
   FlowLayout flowLayout1 = new FlowLayout();
   JPanel jPanel2 = new JPanel();
   JTextField Zcoord = new JTextField();
   JLabel Z = new JLabel();
   FlowLayout flowLayout2 = new FlowLayout();
   JTextField Ycoord = new JTextField();
   JTextField Xcoord = new JTextField();
   JLabel Y = new JLabel();
   JLabel X = new JLabel();
   JPanel jPanel3 = new JPanel();
   FlowLayout flowLayout3 = new FlowLayout();
   JLabel jLabel3 = new JLabel();
   JLabel jLabel4 = new JLabel();
   JLabel jLabel5 = new JLabel();
   JButton Choose_color = new JButton();
   JLabel jLabel6 = new JLabel();

   JSpinner Radius = new JSpinner(new SpinnerNumberModel(1.2, 0.01, 5.0, 0.1));
   JSpinner Red = new JSpinner(new SpinnerNumberModel(1, 0, 255, 1));
   JSpinner Green = new JSpinner(new SpinnerNumberModel(1, 0, 255, 1));
   JSpinner Blue = new JSpinner(new SpinnerNumberModel(1, 0, 255, 1));
   GridBagLayout gridBagLayout1 = new GridBagLayout();

   public JModifyAtomPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(gridBagLayout1);
      jLabel1.setHorizontalTextPosition(SwingConstants.LEFT);
      jLabel1.setText("Element: ");
      jLabel2.setPreferredSize(new Dimension(50, 15));
      jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
      jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
      jLabel2.setText("Label: ");
      Element.setMinimumSize(new Dimension(48, 40));
      Element.setPreferredSize(new Dimension(60, 19));
      Element.setEditable(false);
      Element.addItemListener(new JModifyAtomPanel_Element_itemAdapter(this));
      Element.setToolTipText("Select Element Symbol");
      jPanel1.setLayout(flowLayout1);
      jPanel1.setMinimumSize(new Dimension(376, 30));
      jPanel1.setPreferredSize(new Dimension(372, 30));
      flowLayout1.setAlignment(FlowLayout.LEFT);
      Label.setPreferredSize(new Dimension(80, 19));
      Label.setToolTipText("Type Atom\'s Name and Press Enter");
      Label.addActionListener(new JModifyAtomPanel_Label_actionAdapter(this));
      Z.setHorizontalAlignment(SwingConstants.RIGHT);
      Z.setHorizontalTextPosition(SwingConstants.RIGHT);
      Z.setText("Z: ");
      jPanel2.setLayout(flowLayout2);
      flowLayout2.setAlignment(FlowLayout.LEFT);
      Zcoord.setEnabled(false);
      Zcoord.setPreferredSize(new Dimension(100, 19));
      Zcoord.setToolTipText("Z-coordinate");
      Ycoord.setEnabled(false);
      Ycoord.setPreferredSize(new Dimension(100, 19));
      Ycoord.setToolTipText("Y-coordinate");
      Xcoord.setEnabled(false);
      Xcoord.setPreferredSize(new Dimension(100, 19));
      Xcoord.setToolTipText("X-coordinate");
      Y.setHorizontalAlignment(SwingConstants.RIGHT);
      Y.setHorizontalTextPosition(SwingConstants.RIGHT);
      Y.setText("Y: ");
      X.setHorizontalAlignment(SwingConstants.RIGHT);
      X.setHorizontalTextPosition(SwingConstants.RIGHT);
      X.setText("X: ");
      jPanel3.setLayout(flowLayout3);
      jLabel3.setText("Color: Red: ");
      jLabel4.setText("Green: ");
      jLabel5.setText("Blue: ");
      flowLayout3.setAlignment(FlowLayout.LEFT);
      Choose_color.setText("Choose Color");
      Choose_color.addActionListener(new
                                     JModifyAtomPanel_Choose_color_actionAdapter(this));
      jLabel6.setText("Radius: ");
      jLabel6.setPreferredSize(new Dimension(50, 15));
      jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
      jLabel6.setHorizontalTextPosition(SwingConstants.RIGHT);
      Red.addChangeListener(new JModifyAtomPanel_Red_changeAdapter(this));
      Green.addChangeListener(new JModifyAtomPanel_Green_changeAdapter(this));
      Blue.addChangeListener(new JModifyAtomPanel_Blue_changeAdapter(this));
      Radius.addChangeListener(new JModifyAtomPanel_Radius_changeAdapter(this));
      jPanel1.add(jLabel1, null);
      jPanel1.add(Element, null);
      jPanel1.add(jLabel2, null);
      jPanel1.add(Label, null);
      jPanel1.add(jLabel6);
      jPanel1.add(Radius);
      jPanel2.add(X);
      jPanel2.add(Xcoord);
      jPanel2.add(Y);
      jPanel2.add(Ycoord);
      jPanel2.add(Z);
      jPanel2.add(Zcoord);

      jPanel3.add(jLabel3);
      jPanel3.add(Red);
      jPanel3.add(jLabel4);
      jPanel3.add(Green);
      jPanel3.add(jLabel5);
      jPanel3.add(Blue);
      jPanel3.add(Choose_color);

      this.add(jPanel2, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
                                               , GridBagConstraints.CENTER,
                                               GridBagConstraints.BOTH,
                                               new Insets(0, 0, 0, 0), 0, 0));
      this.add(jPanel3, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
                                               , GridBagConstraints.CENTER,
                                               GridBagConstraints.BOTH,
                                               new Insets(0, 0, 0, 0), 0, 0));
      this.add(jPanel1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
                                               , GridBagConstraints.CENTER,
                                               GridBagConstraints.BOTH,
                                               new Insets(0, 0, 0, 0), 0, 0));
   }

   public void jComboBox1_actionPerformed(ActionEvent e) {

   }

   public void setGraphicsRenderer(GraphicsRendererInterface r) {
      renderer = r;
   }

   public void Element_itemStateChanged(ItemEvent e) {
      if (renderer == null) {
         showRendererError();
         return;
      }
      if (e.getStateChange() == ItemEvent.DESELECTED) {
         return;
      }

      if (!Element.isEnabled()) {
         return;
      }
      Element.setEnabled(false);
      int element = Element.getSelectedIndex();
      this.Label.setText(ChemicalElements.getElementSymbol(element));
      Double radius = new Double(ChemicalElements.getCovalentRadius(element));
      radius *= AtomInterface.COVALENT_TO_GRADIUS_FACTOR;

      renderer.setElementForSelectedAtoms(element);
      renderer.setLabelForSelectedAtoms(ChemicalElements.getElementSymbol(element));

      //Radius.setEnabled(false);
      SpinnerNumberModel smodel = (SpinnerNumberModel) Radius.getModel();
      if (smodel.getMaximum().compareTo(radius) >= 0 && smodel.getMinimum().compareTo(radius) <= 0) {
         smodel.setValue(radius);
      }

      Red.setEnabled(false);
      Green.setEnabled(false);
      Blue.setEnabled(false);

      Color3f color = ChemicalElementsColors.getElementColor(element);
      Color new_color = new Color(color.x, color.y, color.z);
      renderer.setColorForSelectedAtoms(new_color);

      Red.setValue(new_color.getRed());
      Green.setValue(new_color.getGreen());
      Blue.setValue(new_color.getBlue());

      Red.setEnabled(true);
      Green.setEnabled(true);
      Blue.setEnabled(true);

      //smodel.getValue();
      //Radius.setEnabled(true);

      Element.setEnabled(true);
   }

   void showRendererError() {
      JOptionPane.showMessageDialog(this,
                                    "Internal ERROR: Graphis Renderer is not set!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
   }

   public void Choose_color_actionPerformed(ActionEvent e) {
      if (renderer == null) {
         showRendererError();
         return;
      }

      Red.setEnabled(false);
      Green.setEnabled(false);
      Blue.setEnabled(false);

      Color c;

      String red = Red.getValue().toString();
      String green = Green.getValue().toString();
      String blue = Blue.getValue().toString();

      Color initialColor = new Color(Integer.parseInt(red),
                                     Integer.parseInt(green),
                                     Integer.parseInt(blue));

      c = JColorChooser.showDialog(this.getParent(), "Choose Atom Color",
                                   initialColor);
      if (c != null) {

         renderer.setColorForSelectedAtoms(c);

         Red.setValue(c.getRed());
         Green.setValue(c.getGreen());
         Blue.setValue(c.getBlue());
      }

      Red.setEnabled(true);
      Green.setEnabled(true);
      Blue.setEnabled(true);

   }

   private void changeColor() {
      String red = Red.getValue().toString();
      String green = Green.getValue().toString();
      String blue = Blue.getValue().toString();

      Color c = new Color(Integer.parseInt(red),
                          Integer.parseInt(green),
                          Integer.parseInt(blue));
      renderer.setColorForSelectedAtoms(c);
      validate();
   }

   public void Red_stateChanged(ChangeEvent e) {
      if (!Red.isEnabled()) {
         return;
      }
      if (renderer == null) {
         showRendererError();
         return;
      }
      changeColor();
   }

   public void Green_stateChanged(ChangeEvent e) {
      if (!Green.isEnabled()) {
         return;
      }
      if (renderer == null) {
         showRendererError();
         return;
      }
      changeColor();
   }

   public void Blue_stateChanged(ChangeEvent e) {
      if (!Blue.isEnabled()) {
         return;
      }
      if (renderer == null) {
         showRendererError();
         return;
      }
      changeColor();
   }

   public void Label_actionPerformed(ActionEvent e) {
      if (!Label.isEnabled()) {
         return;
      }
      if (renderer == null) {
         showRendererError();
         return;
      }
      renderer.setLabelForSelectedAtoms(Label.getText());
      validate();
   }

   public void Radius_stateChanged(ChangeEvent e) {
      if (!Radius.isEnabled()) {
         return;
      }

      Radius.setEnabled(false);
      float radius = Float.parseFloat(Radius.getValue().toString());
      Radius.setEnabled(true);
      renderer.setRadiusForSelectedAtoms(radius);
      validate();
   }

   private class JModifyAtomPanel_Label_actionAdapter
       implements ActionListener {
      private JModifyAtomPanel adaptee;
      JModifyAtomPanel_Label_actionAdapter(JModifyAtomPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.Label_actionPerformed(e);
      }
   }

   private class JModifyAtomPanel_Radius_changeAdapter
       implements ChangeListener {
      private JModifyAtomPanel adaptee;
      JModifyAtomPanel_Radius_changeAdapter(JModifyAtomPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void stateChanged(ChangeEvent e) {
         adaptee.Radius_stateChanged(e);
      }
   }

}

class JModifyAtomPanel_Blue_changeAdapter
    implements ChangeListener {
   private JModifyAtomPanel adaptee;
   JModifyAtomPanel_Blue_changeAdapter(JModifyAtomPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void stateChanged(ChangeEvent e) {
      adaptee.Blue_stateChanged(e);
   }
}

class JModifyAtomPanel_Choose_color_actionAdapter
    implements ActionListener {
   private JModifyAtomPanel adaptee;
   JModifyAtomPanel_Choose_color_actionAdapter(JModifyAtomPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.Choose_color_actionPerformed(e);
   }
}

class JModifyAtomPanel_Red_changeAdapter
    implements ChangeListener {
   private JModifyAtomPanel adaptee;
   JModifyAtomPanel_Red_changeAdapter(JModifyAtomPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void stateChanged(ChangeEvent e) {
      adaptee.Red_stateChanged(e);
   }
}

class JModifyAtomPanel_Green_changeAdapter
    implements ChangeListener {
   private JModifyAtomPanel adaptee;
   JModifyAtomPanel_Green_changeAdapter(JModifyAtomPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void stateChanged(ChangeEvent e) {
      adaptee.Green_stateChanged(e);
   }
}

class JModifyAtomPanel_Element_itemAdapter
    implements ItemListener {
   private JModifyAtomPanel adaptee;
   JModifyAtomPanel_Element_itemAdapter(JModifyAtomPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void itemStateChanged(ItemEvent e) {
      adaptee.Element_itemStateChanged(e);
   }
}
