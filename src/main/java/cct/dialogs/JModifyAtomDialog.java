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

import cct.interfaces.GraphicsRendererInterface;
import cct.j3d.Java3dUniverse;
import cct.resources.images.ImageResources;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
public class JModifyAtomDialog
    extends JDialog implements ActionListener {

  GraphicsRendererInterface renderer = null;

  // For reset
  int originalElement = 0;
  float originalRadius = 0.2f;
  String originalLabel = "";
  String originalAtomType = "";
  int originalRed = 255;
  int originalGreen = 255;
  int originalBlue = 255;
  Map originalAtomTypes = null;
  JPanel P = new JPanel();
  JButton OK = new JButton();
  JButton Cancel = new JButton();
  JButton Reset = new JButton();

  java.util.List buttons = new ArrayList();
  boolean internalUpdate = true;
  Object daddy = null;
  int oldElement = 0;
  Color current_color = new Color(1, 1, 1);
  JModifyAtomPanel jModifyAtomPanel1 = new JModifyAtomPanel();
  JAtomTypesPanel jAtomTypesPanel1 = new JAtomTypesPanel(this);
  JPanel atomTypesPanel = new JPanel();
  JButton type_1_Button = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  BorderLayout borderLayout1 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton type_2_Button = new JButton();
  static ImageIcon emptyIcon = new ImageIcon(ImageResources.class.getResource(
      "icons48x48/emptyTransparent.png"));
  JButton type_3_Button = new JButton();
  JButton type_4_Button = new JButton();
  JButton type_5_Button = new JButton();
  JButton type_6_Button = new JButton();
  JButton type_7_Button = new JButton();
  JButton helpButton = new JButton();
  static final Logger logger = Logger.getLogger(JModifyAtomDialog.class.getCanonicalName());

  public JModifyAtomDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
    this.pack();
  }

  public JModifyAtomDialog() {
    this(new Frame(), "Modify Atoms", false);
  }

  private void jbInit() throws Exception {
    OK.setText("OK");
    OK.setHorizontalAlignment(SwingConstants.LEADING);
    OK.setHorizontalTextPosition(SwingConstants.LEADING);
    //OK.setVisible(false);
    OK.setText("OK");
    OK.setVisible(false);

    Cancel.setText("Finish");
    Cancel.addActionListener(new JModifyAtomDialog_Cancel_actionAdapter(this));
    Reset.setText("Reset");
    Reset.addActionListener(new JModifyAtomDialog_Reset_actionAdapter(this));
    Reset.setVisible(true);
    jAtomTypesPanel1.setBorder(new TitledBorder(BorderFactory.
                                                createEtchedBorder(Color.
        white, new Color(165, 163, 151)), "Available Atom Types"));
    this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                  DO_NOTHING_ON_CLOSE);
    this.getContentPane().setLayout(borderLayout1);
    type_1_Button.setVisible(true);
    type_1_Button.setPreferredSize(new Dimension(50, 50));
    type_1_Button.setToolTipText("");
    type_1_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_1_Button.setMargin(new Insets(0, 0, 0, 0));
    type_1_Button.setText("Atom");
    type_1_Button.addActionListener(new
                                    JModifyAtomDialog_type_1_Button_actionAdapter(this));
    type_1_Button.setIcon(emptyIcon);
    atomTypesPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    type_2_Button.setPreferredSize(new Dimension(50, 50));
    type_2_Button.setToolTipText("");
    type_2_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_2_Button.setIcon(emptyIcon);
    type_2_Button.setMargin(new Insets(0, 0, 0, 0));
    type_2_Button.setText("Atom");
    type_2_Button.addActionListener(new
                                    JModifyAtomDialog_type_2_Button_actionAdapter(this));
    type_3_Button.setPreferredSize(new Dimension(50, 50));
    type_3_Button.setToolTipText("");
    type_3_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_3_Button.setMargin(new Insets(0, 0, 0, 0));
    type_3_Button.setText("Atom");
    type_3_Button.addActionListener(new
                                    JModifyAtomDialog_type_3_Button_actionAdapter(this));
    type_3_Button.setIcon(emptyIcon);
    type_4_Button.setPreferredSize(new Dimension(50, 50));
    type_4_Button.setToolTipText("");
    type_4_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_4_Button.setMargin(new Insets(0, 0, 0, 0));
    type_4_Button.setText("Atom");
    type_4_Button.addActionListener(new
                                    JModifyAtomDialog_type_4_Button_actionAdapter(this));
    type_4_Button.setIcon(emptyIcon);
    type_5_Button.setPreferredSize(new Dimension(50, 50));
    type_5_Button.setToolTipText("");
    type_5_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_5_Button.setMargin(new Insets(0, 0, 0, 0));
    type_5_Button.setText("Atom");
    type_5_Button.addActionListener(new
                                    JModifyAtomDialog_type_5_Button_actionAdapter(this));
    type_5_Button.setIcon(emptyIcon);
    type_6_Button.setPreferredSize(new Dimension(50, 50));
    type_6_Button.setToolTipText("");
    type_6_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_6_Button.setMargin(new Insets(0, 0, 0, 0));
    type_6_Button.setText("Atom");
    type_6_Button.addActionListener(new
                                    JModifyAtomDialog_type_6_Button_actionAdapter(this));
    type_7_Button.setPreferredSize(new Dimension(50, 50));
    type_7_Button.setToolTipText("");
    type_7_Button.setHorizontalTextPosition(SwingConstants.CENTER);
    type_7_Button.setMargin(new Insets(0, 0, 0, 0));
    type_7_Button.setText("Atom");
    type_7_Button.addActionListener(new
                                    JModifyAtomDialog_type_7_Button_actionAdapter(this));
    helpButton.setEnabled(false);
    helpButton.setToolTipText("");
    helpButton.setText("Help");
    //jModifyAtomPanel1.setLayout(gridBagLayout1);
    P.add(OK);
    P.add(Reset);
    P.add(Cancel);
    P.add(helpButton);
    atomTypesPanel.add(type_1_Button);
    atomTypesPanel.add(type_2_Button);
    atomTypesPanel.add(type_3_Button);
    atomTypesPanel.add(type_4_Button);
    atomTypesPanel.add(type_5_Button);
    atomTypesPanel.add(type_6_Button);
    atomTypesPanel.add(type_7_Button);
    this.getContentPane().add(P, BorderLayout.SOUTH);
    this.getContentPane().add(atomTypesPanel, BorderLayout.CENTER);
    this.getContentPane().add(jModifyAtomPanel1, BorderLayout.NORTH);

    buttons.add(type_1_Button);
    buttons.add(type_2_Button);
    buttons.add(type_3_Button);
    buttons.add(type_4_Button);
    buttons.add(type_5_Button);
    buttons.add(type_6_Button);
    buttons.add(type_7_Button);
    for (int i = 1; i < buttons.size(); i++) {
      JButton butt = (JButton) buttons.get(i);
      butt.setVisible(false);
    }
  }

  public JButton getHelpButton() {
    return helpButton;
  }

  public void setTargetClass(Object target) {
    daddy = target;
  }

  public void selectElement(int elem) {
    jModifyAtomPanel1.Element.setEnabled(false);
    jModifyAtomPanel1.Element.setSelectedIndex(elem);
    originalElement = elem;
    jModifyAtomPanel1.Element.setEnabled(true);
    logger.info(getClass().getCanonicalName() + ": select element: " +
                       elem);
  }

  public void setLabel(String label) {
    jModifyAtomPanel1.Label.setEnabled(false);
    jModifyAtomPanel1.Label.setText(label);
    originalLabel = label;
    jModifyAtomPanel1.Label.setEnabled(true);
    logger.info(getClass().getCanonicalName() + ": set label: " +
                       label);
  }

  public void setElements(String[] elements) {
    jModifyAtomPanel1.Element.setEnabled(false);
    for (int i = 0; i < elements.length; i++) {
      jModifyAtomPanel1.Element.addItem(elements[i]);
    }
    jModifyAtomPanel1.Element.setSelectedIndex(0);
    jModifyAtomPanel1.Element.setEnabled(true);

  }

  public void setX(float x) {
    //jModifyAtomPanel1.Xcoord.removeAllItems();
    jModifyAtomPanel1.Xcoord.setText(String.valueOf(x));
  }

  public void setY(float x) {
    //jModifyAtomPanel1.Ycoord.removeAllItems();
    jModifyAtomPanel1.Ycoord.setText(String.valueOf(x));
  }

  public void setZ(float x) {
    //jModifyAtomPanel1.Zcoord.removeAllItems();
    jModifyAtomPanel1.Zcoord.setText(String.valueOf(x));
  }

  public void setColor(Color c) {
    setColor(c.getRed(), c.getGreen(), c.getBlue());
  }

  public void setAtomTypes(Map atomTypes) {
    /* Old stuff
           jAtomTypesPanel1.setAtomTypes(atomTypes);
     */

    int count = 0;
    Set set = atomTypes.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String type = me.getKey().toString();
      ImageIcon image = (ImageIcon) me.getValue();

      logger.info("Modifying old button: " + type);
      JButton butt = (JButton) buttons.get(count);
      butt.setIcon(image);
      butt.setText(type);
      butt.setActionCommand(type);
      butt.setSelected(false);
      butt.setEnabled(true);
      butt.setVisible(true);
      butt.validate();
      ++count;
    }

    for (int i = count; i < buttons.size(); i++) {
      JButton butt = (JButton) buttons.get(i);
      butt.setVisible(false);
      butt.validate();
    }

    originalAtomTypes = atomTypes;
  }

  private void setSelectedAtomType(int n) {
    internalUpdate = true;
    jAtomTypesPanel1.setSelectedButton(n);
    internalUpdate = false;
    logger.info(getClass().getCanonicalName() + ": set atom type: " +
                       n);
  }

  public void setSelectedAtomType(String atomType) {
    internalUpdate = true;

    //jAtomTypesPanel1.setSelectedButton(atomType);

    for (int i = 0; i < buttons.size(); i++) {
      JButton butt = (JButton) buttons.get(i);
      if (!butt.isVisible()) {
        break;
      }
      if (atomType.equalsIgnoreCase(butt.getActionCommand())) {
        butt.setSelected(true);
        butt.validate();
      }
      else {
        butt.setSelected(false);
        butt.validate();
      }
    }

    internalUpdate = false;
    originalAtomType = atomType;

    logger.info(getClass().getCanonicalName() + ": set atom type: " +
                       atomType);
    validate();
  }

  public void setColor(int red, int green, int blue) {
    jModifyAtomPanel1.Red.setEnabled(false);
    jModifyAtomPanel1.Green.setEnabled(false);
    jModifyAtomPanel1.Blue.setEnabled(false);

    jModifyAtomPanel1.Red.setValue(red);
    jModifyAtomPanel1.Green.setValue(green);
    jModifyAtomPanel1.Blue.setValue(blue);
    originalRed = red;
    originalGreen = green;
    originalBlue = blue;

    jModifyAtomPanel1.Red.setEnabled(true);
    jModifyAtomPanel1.Green.setEnabled(true);
    jModifyAtomPanel1.Blue.setEnabled(true);
  }

  public void setRadius(float r) {
    jModifyAtomPanel1.Radius.setEnabled(false);
    jModifyAtomPanel1.Radius.setValue(r);
    jModifyAtomPanel1.Radius.setEnabled(true);
    originalRadius = r;
    logger.info(getClass().getCanonicalName() + ": set radius: " + r);
  }

  public void setGraphicsRenderer(GraphicsRendererInterface r) {
    renderer = r;
    jModifyAtomPanel1.setGraphicsRenderer(r);
  }

  public void Cancel_actionPerformed(ActionEvent e) {
    if (daddy instanceof Java3dUniverse) {
      Java3dUniverse target = (Java3dUniverse) daddy;
      target.endProcessingSelectedAtoms();
      setVisible(false);
    }
  }

  /**
   * For listening the JAtomTypesPanel only
   * @param actionEvent ActionEvent
   */
  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    if (internalUpdate) {
      return;
    }
    internalUpdate = true;
    String store = originalAtomType;
    jAtomTypesPanel1.setSelectedButton(actionEvent.getActionCommand());
    originalAtomType = store;
    renderer.setAtomTypeForSelectedAtoms(actionEvent.getActionCommand());
    internalUpdate = false;
  }

  public void Reset_actionPerformed(ActionEvent e) {
    renderer.setElementForSelectedAtoms(originalElement);
    renderer.setColorForSelectedAtoms(new Color(originalRed, originalGreen,
                                                originalBlue));
    renderer.setLabelForSelectedAtoms(originalLabel);
    renderer.setRadiusForSelectedAtoms(originalRadius);
    renderer.setAtomTypeForSelectedAtoms(originalAtomType);
    setSelectedAtomType(originalAtomType);
    validate();
  }

  public void typeButtons_actionPerformed(ActionEvent e) {
    if (internalUpdate) {
      return;
    }

    for (int i = 0; i < buttons.size(); i++) {
      JButton butt = (JButton) buttons.get(i);
      if (!butt.isVisible()) {
        break;
      }
      if (e.getActionCommand().equalsIgnoreCase(butt.getActionCommand())) {
        butt.setSelected(true);
        butt.validate();
      }
      else {
        butt.setSelected(false);
        butt.validate();
      }
    }

    renderer.setAtomTypeForSelectedAtoms(e.getActionCommand());
  }
}

class JModifyAtomDialog_type_7_Button_actionAdapter
    implements ActionListener {
  private JModifyAtomDialog adaptee;
  JModifyAtomDialog_type_7_Button_actionAdapter(JModifyAtomDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.typeButtons_actionPerformed(e);
  }
}

class JModifyAtomDialog_type_6_Button_actionAdapter
    implements ActionListener {
  private JModifyAtomDialog adaptee;
  JModifyAtomDialog_type_6_Button_actionAdapter(JModifyAtomDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.typeButtons_actionPerformed(e);
  }
}

class JModifyAtomDialog_type_1_Button_actionAdapter
    implements ActionListener {
  private JModifyAtomDialog adaptee;
  JModifyAtomDialog_type_1_Button_actionAdapter(JModifyAtomDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.typeButtons_actionPerformed(e);
  }
}

class JModifyAtomDialog_type_2_Button_actionAdapter
    implements ActionListener {
  private JModifyAtomDialog adaptee;
  JModifyAtomDialog_type_2_Button_actionAdapter(JModifyAtomDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.typeButtons_actionPerformed(e);
  }
}

class JModifyAtomDialog_type_3_Button_actionAdapter
    implements ActionListener {
  private JModifyAtomDialog adaptee;
  JModifyAtomDialog_type_3_Button_actionAdapter(JModifyAtomDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.typeButtons_actionPerformed(e);
  }
}

class JModifyAtomDialog_type_4_Button_actionAdapter
    implements ActionListener {
  private JModifyAtomDialog adaptee;
  JModifyAtomDialog_type_4_Button_actionAdapter(JModifyAtomDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.typeButtons_actionPerformed(e);
  }
}

class JModifyAtomDialog_type_5_Button_actionAdapter
    implements ActionListener {
  private JModifyAtomDialog adaptee;
  JModifyAtomDialog_type_5_Button_actionAdapter(JModifyAtomDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.typeButtons_actionPerformed(e);
  }
}

class JModifyAtomDialog_Reset_actionAdapter
    implements ActionListener {
  private JModifyAtomDialog adaptee;
  JModifyAtomDialog_Reset_actionAdapter(JModifyAtomDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Reset_actionPerformed(e);
  }
}

class JModifyAtomDialog_Cancel_actionAdapter
    implements ActionListener {
  private JModifyAtomDialog adaptee;
  JModifyAtomDialog_Cancel_actionAdapter(JModifyAtomDialog adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.Cancel_actionPerformed(e);
  }
}
