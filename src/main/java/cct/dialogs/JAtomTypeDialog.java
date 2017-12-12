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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cct.j3d.Java3dUniverse;
import cct.modelling.AtomGeometry;
import cct.modelling.CCTAtomTypes;
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
public class JAtomTypeDialog
    extends JFrame implements ActionListener, ItemListener {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  private int lastSelected[] = null;
  Map elementMapping = null;
  Map<String, JButton> elementButtons = null;
  Java3dUniverse daddy = null;
  static final Logger logger = Logger.getLogger(JAtomTypeDialog.class.getCanonicalName());

  boolean OKpressed = false;
  static Object Lengths[] = {
      "0.9", "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "2.0",
      "2.5", "3.0", "3.5"};
  JButton OK = new JButton("Ok");
  JButton Cancel = new JButton("Cancel");
  JButton Help = new JButton("Help");
  int selectedElement = 1;
  String currentElement = "H";
  JLabel selectedElem = new JLabel();
  JTextField atomName = new JTextField(6);
  JCheckBox drawBond = new JCheckBox("Draw Bond", true);
  JCheckBox userDefBondLength = new JCheckBox("User defined", true);
  JCheckBox covalentBondLength = new JCheckBox("Covalent Bond Length", false);
  JCheckBox predefBondLength = new JCheckBox("Predefined Bond Length", false);
  JComboBox bondLength = new JComboBox(Lengths);

  java.util.List<JButton> elButt = null;
  Color oldColor = null;
  Color pressedColor = new Color(1.0f, 0.0f, 0.0f);
  JButton Previous = null;
  JPanel atomTypesPanel = new JPanel();
  JButton type_1_Button = new JButton();
  JButton type_2_Button = new JButton();
  JButton type_3_Button = new JButton();
  JButton type_4_Button = new JButton();
  JButton type_5_Button = new JButton();
  JButton type_6_Button = new JButton();
  JButton type_7_Button = new JButton();
  FlowLayout flowLayout1 = new FlowLayout();
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu fontMenu = new JMenu("Font");
  JMenuItem increaseFont = new JMenuItem("Increase Font");
  JMenuItem decreaseFont = new JMenuItem("Decrease Font");
  JMenu presentationMenu = new JMenu("Show Elements");
  JMenuItem asTable = new JMenuItem("As Periodic Table");
  JMenuItem asCombobox = new JMenuItem("As Combobox (takes less space)");

  JElementsPanel ePanel = new JElementsPanel();
  java.util.List<JButton> atomTypesButtons = new ArrayList<JButton> (7);
  Color defaultAtomTypeColor = null;
  Color selectedAtomTypeColor = new Color(255, 0, 0);
  ButtonGroup buttonGroup1 = new ButtonGroup();
  public JAtomTypeDialog(Frame owner, String title, boolean modal) {
    super(title);
    //super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JAtomTypeDialog() {
    this(new Frame(), "JAtomTypeDialog", false);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);

    elButt = ePanel.getButtons();
    elementButtons = new LinkedHashMap<String, JButton> (elButt.size());
    JButton elButton = null;
    for (int i = 0; i < elButt.size(); i++) {
      elButton = elButt.get(i);
      elementButtons.put(elButton.getActionCommand(), elButton);
      elButton.addActionListener(this);
    }
    oldColor = elButton.getForeground();
    oldColor = new Color(oldColor.getRGB());
    this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                  DO_NOTHING_ON_CLOSE);

    JPanel secondP = buildSecondPanel();
    type_1_Button.setPreferredSize(new Dimension(50, 50));
    type_1_Button.setToolTipText("");
    type_1_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_1_Button.setMargin(new Insets(0, 0, 0, 0));
    type_1_Button.setText("Atom");
    type_2_Button.setPreferredSize(new Dimension(50, 50));
    type_2_Button.setToolTipText("");
    type_2_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_2_Button.setMargin(new Insets(0, 0, 0, 0));
    type_2_Button.setText("Atom");
    type_3_Button.setPreferredSize(new Dimension(50, 50));
    type_3_Button.setToolTipText("");
    type_3_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_3_Button.setMargin(new Insets(0, 0, 0, 0));
    type_3_Button.setText("Atom");
    type_4_Button.setPreferredSize(new Dimension(50, 50));
    type_4_Button.setToolTipText("");
    type_4_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_4_Button.setMargin(new Insets(0, 0, 0, 0));
    type_4_Button.setText("Atom");
    type_5_Button.setPreferredSize(new Dimension(50, 50));
    type_5_Button.setToolTipText("");
    type_5_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_5_Button.setMargin(new Insets(0, 0, 0, 0));
    type_5_Button.setText("Atom");
    type_6_Button.setPreferredSize(new Dimension(50, 50));
    type_6_Button.setToolTipText("");
    type_6_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_6_Button.setMargin(new Insets(0, 0, 0, 0));
    type_6_Button.setText("Atom");
    type_7_Button.setPreferredSize(new Dimension(50, 50));
    type_7_Button.setToolTipText("");
    type_7_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_7_Button.setMargin(new Insets(0, 0, 0, 0));
    type_7_Button.setText("Atom");
    atomTypesPanel.setBorder(BorderFactory.createLineBorder(Color.
        DARK_GRAY,
        1));
    atomTypesPanel.setLayout(flowLayout1);

    ePanel.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
    flowLayout1.setAlignment(FlowLayout.LEFT);
    atomTypesPanel.add(type_1_Button);
    atomTypesPanel.add(type_2_Button);
    atomTypesPanel.add(type_3_Button);
    atomTypesPanel.add(type_4_Button);
    atomTypesPanel.add(type_5_Button);
    atomTypesPanel.add(type_6_Button);
    atomTypesPanel.add(type_7_Button);

    getContentPane().add(panel1);
    panel1.add(ePanel, BorderLayout.NORTH);
    panel1.add(atomTypesPanel, BorderLayout.CENTER);
    panel1.add(secondP, BorderLayout.SOUTH);
    atomTypesButtons.add(type_1_Button);
    atomTypesButtons.add(type_2_Button);
    atomTypesButtons.add(type_3_Button);
    atomTypesButtons.add(type_4_Button);
    atomTypesButtons.add(type_5_Button);
    atomTypesButtons.add(type_6_Button);
    atomTypesButtons.add(type_7_Button);
    defaultAtomTypeColor = new Color(type_7_Button.getForeground().getRed(),
                                     type_7_Button.getForeground().getGreen(),
                                     type_7_Button.getForeground().getBlue(),
                                     type_7_Button.getForeground().getAlpha());

    type_1_Button.setIcon(AtomGeometry.ATOM_ICON);
    type_1_Button.setText("Atom");
    type_1_Button.setEnabled(true);
    type_1_Button.setVisible(true);
    type_1_Button.setForeground(defaultAtomTypeColor);
    type_1_Button.validate();

    AtomType_actionAdapter atad = new AtomType_actionAdapter(this);
    for (int i = 0; i < atomTypesButtons.size(); i++) {
      atomTypesButtons.get(i).addActionListener(atad);
    }

    lastSelected = new int[ChemicalElements.getNumberOfElements()];
    for (int i = 0; i < lastSelected.length; i++) {
      lastSelected[i] = -1;
    }

    elementMapping = CCTAtomTypes.getElementMapping();
    currentElement = ChemicalElements.getElementSymbol(1);
    this.selectElement(currentElement);
    buttonGroup1.add(userDefBondLength);
    buttonGroup1.add(covalentBondLength);
    buttonGroup1.add(predefBondLength);

    Cancel.addActionListener(this);

    increaseFont.addActionListener(this);
    decreaseFont.addActionListener(this);
    fontMenu.add(increaseFont);
    fontMenu.add(decreaseFont);
    jMenuBar1.add(fontMenu);

    presentationMenu = new JMenu("Show Elements");
    asTable.addActionListener(this);
    asCombobox.addActionListener(this);
    presentationMenu.add(asTable);
    presentationMenu.add(asCombobox);
    jMenuBar1.add(presentationMenu);

    this.setJMenuBar(jMenuBar1);
  }

  public void setAtomTypes(String element) {
    Map atomTypes = (Map) elementMapping.get(element);

    int count = 0;

    int atomNumber = ChemicalElements.getAtomicNumber(element);
    int selectedType = lastSelected[atomNumber];
    boolean selectDefault = true;
    if (selectedType != -1) {
      selectDefault = false;
    }

    // --- Special setup for the first atom type (Atom)

    JButton butt = atomTypesButtons.get(count);
    butt.setActionCommand(element);
    if (atomTypes == null || selectedType == count) {
      butt.setSelected(true);
    }
    else {
      butt.setSelected(false);
    }
    butt.validate();
    ++count;

    if (atomTypes == null) {
      for (int i = count; i < atomTypesButtons.size(); i++) {
        butt = atomTypesButtons.get(i);
        butt.setVisible(false);
        butt.validate();
      }
      return;
    }

    Set set = atomTypes.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String typeName = me.getKey().toString();
      CCTAtomTypes type = (CCTAtomTypes) me.getValue();

      logger.info("Modifying old button: " + type);
      butt = atomTypesButtons.get(count);
      butt.setIcon(type.getIcon());
      butt.setText(typeName);
      butt.setActionCommand(typeName);
      if (selectedType == count) {
        butt.setSelected(true);
      }
      else {
        butt.setSelected(false);
      }

      if (selectDefault && type.isDefaultType()) {
        butt.setSelected(true);
        lastSelected[atomNumber] = count;
        selectedType = count;
      }

      butt.setEnabled(true);
      butt.setVisible(true);
      butt.setForeground(defaultAtomTypeColor);
      butt.validate();
      ++count;
    }

    for (int i = count; i < atomTypesButtons.size(); i++) {
      butt = atomTypesButtons.get(i);
      butt.setVisible(false);
      butt.validate();
    }

    // --- Final check for selection

    if (selectedType == -1) {
      butt = atomTypesButtons.get(1);
      butt.setSelected(true);
      lastSelected[atomNumber] = 1;
    }
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    String arg = ae.getActionCommand();
    if (ae.getSource() == OK) {
      OKpressed = true;
      setVisible(false);
    }
    else if (ae.getSource() == Cancel) {
      OKpressed = false;
      daddy.endProcessingSelectedAtoms();
      setVisible(false);
    }
    else if (ae.getSource() == increaseFont) {
      Font font = ePanel.getElementsFont();
      Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() + 1);
      ePanel.setElementsFont(newFont);
      this.pack();
    }
    else if (ae.getSource() == decreaseFont) {
      Font font = ePanel.getElementsFont();
      if (font.getSize() <= 8) {
        return;
      }
      Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
      ePanel.setElementsFont(newFont);
      this.pack();
    }
    else if (ae.getSource() == asTable) {
      ePanel.showElementsAsTable(true);
      this.pack();
    }
    else if (ae.getSource() == asCombobox) {
      ePanel.showElementsAsTable(false);
      this.pack();
    }

    else if (arg.equals("OK")) {
    }

    else {
      currentElement = arg;
      selectElement(currentElement);
    }
  }

  void selectElement(String element) {
    selectedElem.setText("  Chemical Element Selected: " + element);
    selectedElement = ChemicalElements.getAtomicNumber(element);
    setAtomTypes(currentElement);
    JButton butt = elementButtons.get(element);
    /*
           for (int i = 0; i < elButt.size(); i++) {
       if ( (JButton) elButt.get(i) == ae.getSource()) {
          butt = (JButton) elButt.get(i);
       }
           }
     */
    if (butt != null) {
      butt.setForeground(pressedColor);
    }
    if (Previous == null) {
      Previous = butt;
    }
    else {
      Previous.setForeground(oldColor);
      Previous = butt;
    }
    //logger.info("Event: " + element);

    atomName.setText(element);

  }

  @Override
  public void itemStateChanged(ItemEvent ie) {
    //logger.info("Event: " + ie);
    //logger.info("Event: getItem" + ie.getItem());
    //logger.info("Event: getItemSelectable" + ie.getItemSelectable());
    //String item = (String) ie.getItem();

    if (ie.getSource() == predefBondLength) {
      bondLength.setEnabled(predefBondLength.isSelected());
      bondLength.setEnabled(predefBondLength.isSelected());
    }
    else if (ie.getSource() == drawBond) {
      this.processDrawBondState();
    }
    /*
           if (ie.getStateChange() == ItemEvent.SELECTED) {
       int n = bondLength.getSelectedIndex();
       bLength.setText(bondLength.getSelectedItem());
           }
     */
    repaint();
  }

  private JPanel buildSecondPanel() {
    GridLayout gridLayout = new GridLayout(4, 1);
    JPanel P = new JPanel();
    P.setLayout(gridLayout);

    // --- Add info about selected element

    if (currentElement == null) {
      selectedElem.setText("  Chemical Element Selected:   ");
    }
    else {
      selectedElem.setText("  Chemical Element Selected: " + currentElement);
    }

    P.add(selectedElem);

    //

    JPanel PP = new JPanel(new FlowLayout(FlowLayout.LEFT));

    PP.add(new JLabel("  Atom Name: "));
    PP.add(atomName);
    PP.add(drawBond);
    drawBond.addItemListener(this);
    PP.validate();

    P.add(PP);

    //

    PP = new JPanel(new FlowLayout(FlowLayout.LEFT));
    PP.add(userDefBondLength);
    PP.add(covalentBondLength);
    PP.add(predefBondLength);
    predefBondLength.addItemListener(this);
    if (predefBondLength.isSelected()) {
      bondLength.setEnabled(true);
    }
    else {
      bondLength.setEnabled(false);
    }
    PP.add(bondLength);

    processDrawBondState();

    PP.validate();

    P.add(PP);
    P.validate();

    PP = new JPanel(new FlowLayout(FlowLayout.CENTER));
    PP.add(Cancel);
    PP.add(Help);
    Help.setEnabled(false);

    P.add(PP);

    return P;
  }

  JButton getHelpButton() {
    return Help;
  }

  void processDrawBondState() {
    userDefBondLength.setEnabled(drawBond.isSelected());
    covalentBondLength.setEnabled(drawBond.isSelected());
    predefBondLength.setEnabled(drawBond.isSelected());
  }

  public void setOKVisible(boolean enable) {
    OK.setVisible(enable);
  }

  public void setCancelVisible(boolean enable) {
    Cancel.setVisible(enable);
  }

  public void setTargetClass(Java3dUniverse parent) {
    daddy = parent;
  }

  public String getAtomType() {
    if (lastSelected[getElement()] < 1) {
      return null;
    }
    JButton butt = atomTypesButtons.get(lastSelected[getElement()]);
    return butt.getActionCommand();
  }

  public int getElement() {
    return selectedElement;
  }

  public boolean isUseBondLength() {
    return predefBondLength.isSelected();
  }

  public boolean isCovalentBondLength() {
    return covalentBondLength.isSelected();
  }

  public String getBondLength() {
    return bondLength.getSelectedItem().toString();
  }

  public boolean isDrawBond() {
    return drawBond.isSelected();
  }

  public String getSelectedAtomType() {
    for (int i = 0; i < atomTypesButtons.size(); i++) {
      JButton butt = atomTypesButtons.get(i);
      if (!butt.isVisible()) {
        break;
      }
      if (!butt.isSelected()) {
        return butt.getActionCommand();
      }
    }
    return null;
  }

  void selectAtomType(JButton button) {
    for (int i = 0; i < atomTypesButtons.size(); i++) {
      JButton butt = atomTypesButtons.get(i);
      if (!butt.isVisible()) {
        break;
      }
      if (button == butt) {
        butt.setSelected(true);
        butt.setForeground(selectedAtomTypeColor);
        butt.validate();
      }
      else {
        butt.setSelected(false);
        butt.setForeground(defaultAtomTypeColor);
        butt.validate();
      }
    }

  }

  public String getAtomName() {
    return atomName.getText().trim();
  }

  public void changeAtomType(ActionEvent e) {

    for (int i = 0; i < atomTypesButtons.size(); i++) {
      JButton butt = atomTypesButtons.get(i);
      if (!butt.isVisible()) {
        break;
      }
      if (e.getSource() == butt) {
        lastSelected[selectedElement] = i;
        selectAtomType(butt);
      }

    }
  }

  private class AtomType_actionAdapter
      implements ActionListener {
    private JAtomTypeDialog adaptee;
    AtomType_actionAdapter(JAtomTypeDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.changeAtomType(e);
    }
  }

}
