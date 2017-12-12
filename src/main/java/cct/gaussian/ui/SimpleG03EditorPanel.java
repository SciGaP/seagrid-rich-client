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

package cct.gaussian.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import cct.gaussian.GJFParserInterface;
import cct.gaussian.Gaussian;
import cct.gaussian.GaussianAtom;
import cct.interfaces.MoleculeInterface;
import cct.modelling.Molecule;

/**
 * <p>Title: Gasussian Utility Classes</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class SimpleG03EditorPanel
    extends JPanel {

  Link0Panel link0Panel1 = new Link0Panel();
  RouteSectionPanel routeSectionPanel1 = new RouteSectionPanel();
  MoleculeSpecsPanel moleculeSpecsPanel1 = new MoleculeSpecsPanel();
  JLabel jLabel1 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JPanel lowerPanel = new JPanel();
  JPanel centralPanel = new JPanel();
  TitleSectionPanel titleSectionPanel1 = new TitleSectionPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  Border border1 = BorderFactory.createLineBorder(Color.gray, 1);
  Border border2 = new TitledBorder(border1, "Route section (# lines)");

  boolean fieldsEdited = false;
  GJFParserInterface gjfParser = null;

  //ImageIcon arrowUp = new ImageIcon(cct.gaussian.ui.SimpleG03EditorPanel.class.
  //                                    getResource(
  //                                        "../../resources/images/icons32x32/arrowUp.gif"));
  BorderLayout borderLayout1 = new BorderLayout();
  JButton validateGeomButton = new JButton();

  //ImageIcon arrowUp = new ImageIcon("../../resources/images/icons32x32/arrowUp.gif");


  public boolean isEdited() {
    return fieldsEdited;
  }

  public SimpleG03EditorPanel(GJFParserInterface parser) {
    gjfParser = parser;
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setGeometryValidator(GJFParserInterface parser) {
    gjfParser = parser;
    if (gjfParser != null) {
      this.enableValidateGeometry(true);
    }
  }

  private void jbInit() throws Exception {
    jLabel1.setToolTipText("");
    jLabel1.setText("Charge & Multiplicity");
    this.setLayout(borderLayout1);
    lowerPanel.setLayout(gridBagLayout2);
    centralPanel.setLayout(gridBagLayout3);
    moleculeSpecsPanel1.setBorder(new TitledBorder(BorderFactory.
        createLineBorder(Color.gray, 1), "Molecule specifications "));
    link0Panel1.setBorder(new TitledBorder(BorderFactory.createLineBorder(
        Color.
        gray, 1), "Link 0 Commands"));
    routeSectionPanel1.getViewport().setBackground(Color.black);
    routeSectionPanel1.setBorder(new TitledBorder(BorderFactory.
                                                  createLineBorder(Color.gray,
        1), "Route section (# lines)"));
    titleSectionPanel1.setBorder(new TitledBorder(BorderFactory.
                                                  createLineBorder(Color.gray,
        1), "Title section"));
    jTextField1.setToolTipText("Enter Charge and Multiplicity");
    validateGeomButton.setMaximumSize(new Dimension(300, 300));
    validateGeomButton.setMinimumSize(new Dimension(130, 23));
    validateGeomButton.setPreferredSize(new Dimension(160, 23));
    validateGeomButton.setToolTipText("Click to Check Molecular Geometry");
    validateGeomButton.setBorderPainted(false);
    validateGeomButton.setMnemonic('0');
    validateGeomButton.setText("Validate Geometry");
    validateGeomButton.addActionListener(new
                                         SimpleG03EditorPanel_validateGeomButton_actionAdapter(this));
    this.add(centralPanel, BorderLayout.NORTH);
    centralPanel.add(link0Panel1, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    centralPanel.add(routeSectionPanel1,
                     new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.HORIZONTAL,
                                            new Insets(0, 5, 0, 5), 0, 0));
    centralPanel.add(titleSectionPanel1,
                     new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.HORIZONTAL,
                                            new Insets(5, 5, 5, 5), 0, 0));
    this.add(lowerPanel, BorderLayout.CENTER);
    lowerPanel.add(validateGeomButton
                   , new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(0, 0, 0, 0), 0, 0));
    lowerPanel.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(0, 5, 5, 5), 0, 0));
    lowerPanel.add(jTextField1, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    lowerPanel.add(moleculeSpecsPanel1,
                   new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0
                                          , GridBagConstraints.CENTER,
                                          GridBagConstraints.BOTH,
                                          new Insets(5, 5, 5, 5), 0, 0));
  }

  public void setLinkZeroCommands(java.util.List cmd) {
    link0Panel1.setText(getText(cmd));
    fieldsEdited = true;
  }

  public void setLinkZeroCommands(String cmd) {
    link0Panel1.setText(cmd);
    fieldsEdited = true;
  }

  public String getLinkZeroCommands() {
    return link0Panel1.getText();
  }

  public void setRouteSection(String options) {
    routeSectionPanel1.setText(options);
    fieldsEdited = true;
  }

  public String getRouteSection() {
    return routeSectionPanel1.getText();
  }

  public void setTitleSection(String options) {
    titleSectionPanel1.setText(options);
    fieldsEdited = true;
  }

  public String getTitleSection() {
    return titleSectionPanel1.getText();
  }

  public void setChargeSection(String options) {
    jTextField1.setText(options);
    fieldsEdited = true;
  }

  public String getChargeSection() {
    String lines[] = jTextField1.getText().split("\n");
    String text = "";
    for (int i = 0; i < lines.length; i++) {
      String trimmed = lines[i].trim();
      if (trimmed.length() != 0) {
        text += trimmed + "\n";
      }
    }
    return text;
    //return jTextField1.getText();
  }

  public void setMoleculeSpecsSection(String options) {
    moleculeSpecsPanel1.setText(options);
    fieldsEdited = true;
  }

  public String getMoleculeSpecsSection() {
    return moleculeSpecsPanel1.getText();
  }

  public void resetPanel() {
    link0Panel1.resetPanel();
    routeSectionPanel1.resetPanel();
    titleSectionPanel1.resetPanel();
    jTextField1.setText("0 1");
    moleculeSpecsPanel1.resetPanel();
    fieldsEdited = false;
  }

  public String getStepAsString() {
    String step = "";
    step = getLinkZeroCommands() + getRouteSection() + "\n" + getTitleSection() +
        "\n" + getChargeSection() + getMoleculeSpecsSection();
    return step;
  }

  String getText(java.util.List cmd) {
    if (cmd == null || cmd.size() < 1) {
      return "";
    }
    String text = "";
    for (int i = 0; i < cmd.size(); i++) {
      text += (String) cmd.get(i);
    }
    return text;
  }

  public void enableValidateGeometry(boolean enable) {
    validateGeomButton.setEnabled(enable);
  }

  public void validateGeomButton_actionPerformed(ActionEvent e) {
    if (gjfParser == null) {
      JOptionPane.showMessageDialog(null,
                                    "No geometry validator was specified!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    moleculeSpecsPanel1.startEditing();
    java.util.List<GaussianAtom> atoms = null;
    try {
      atoms = gjfParser.validateMolecularGeometry(this.getMoleculeSpecsSection());
      this.validate();
      if (atoms.size() > 0) {
        /*
                 JOptionPane.showMessageDialog(null,
                                     "Found "+atoms.size()+" atoms",
                                     "Success",
                                     JOptionPane.INFORMATION_MESSAGE);
         */
      }
      else {
        JOptionPane.showMessageDialog(null,
                                      "No atoms found",
                                      "No atoms",
                                      JOptionPane.INFORMATION_MESSAGE);
        return;
      }
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
                                    ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- check charge and multiplicity

    int ch = 0, multiplicity = 1;
    String chargeSection = this.getChargeSection();
    StringTokenizer st = new StringTokenizer(chargeSection, " ,\t\n");
    if (st.countTokens() < 2) {
      ch = 0;
      multiplicity = 1;
    }
    else {
      try {
        ch = Integer.parseInt(st.nextToken());
        multiplicity = Integer.parseInt(st.nextToken());
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(null,
                                      "Wrong value(s) for charge or/and multiplicity " +
                                      chargeSection + " : " + ex.getMessage() +
                                      "\nProgram will try to fix it...",
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }

    if (multiplicity < 1) {
      JOptionPane.showMessageDialog(null,
                                    "Multiplicity cannot be less than 1\n" +
                                    "Program will fix it...",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      multiplicity = 1;
    }

    int[] elements = new int[atoms.size()];
    for (int i = 0; i < atoms.size(); i++) {
      GaussianAtom atom = atoms.get(i);
      elements[i] = atom.getElement();
    }

    int charge = ch;
    try {
      Molecule.validateTotalCharge(elements, charge, multiplicity);
    }
    catch (Exception ex) {
      try {
        charge = Molecule.guessTotalCharge(elements, multiplicity);
      }
      catch (Exception exx) {}

      JOptionPane.showMessageDialog(this,
                                    "Current values for charge " + ch +
                                    " and multiplicity " +
                                    multiplicity +
                                    " do not make sense\n" +
                                    "Check new values for charge " + charge +
                                    " and multiplicity " + multiplicity +
                                    " suggested by a program"
                                    , "Warning",
                                    JOptionPane.WARNING_MESSAGE);
    }

    if (st.hasMoreTokens()) {
      String newChargeSection = String.valueOf(charge) + " " +
          String.valueOf(multiplicity);
      while (st.hasMoreTokens()) {
        newChargeSection += " " + st.nextToken();
      }
      setChargeSection(newChargeSection);
    }
    else {
      this.setChargeSection(String.valueOf(charge) + " " +
                            String.valueOf(multiplicity));
    }

    // --- Check link0 Section

    try {
      gjfParser.validateLink0Section(this.getLinkZeroCommands());
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
                                    "Error(s) in Link0 Section:\n" +
                                    ex.getMessage(),
                                    "Error(s) in Link0 Section",
                                    JOptionPane.ERROR_MESSAGE);
    }

  }

  public void setupMolecule(MoleculeInterface mol) {

    // --- Set link0 section

    Object props = mol.getProperty(cct.gaussian.Gaussian.
                                   LINK_ZERO_COMMANDS_KEY);

    if (props != null) {
      if (props instanceof java.util.List) {
        java.util.List link0 = (java.util.List) props;
        String text = "";
        for (int i = 0; i < link0.size(); i++) {
          text += link0.get(i).toString() + "\n";
        }
        setLinkZeroCommands(text);
      }
      else {
        setLinkZeroCommands(props.toString());
      }
    }
    else {
      setLinkZeroCommands("%nproc=1");
    }

    // --- Set route section

    props = mol.getProperty(cct.gaussian.Gaussian.ROUTE_COMMANDS_KEY);
    if (props != null) {
      setRouteSection(props.toString());
    }
    else {
      setRouteSection("# Opt HF/6-31g");
    }

    // --- Set title section

    setTitleSection(mol.getName());

    // --- Set charge section

    Integer multiplicity = (Integer) mol.getProperty(MoleculeInterface.MultiplicityProperty);

    if (multiplicity == null) {
      multiplicity = 1;
      System.err.println("Set multiplicity to " + multiplicity);
    }
    else if (multiplicity < 1) {
      JOptionPane.showMessageDialog(null,
                                    "Multiplicity cannot be less than 1\n" +
                                    "Program will fix it...",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
    }

    Integer ch = (Integer) mol.getProperty(MoleculeInterface.ChargeProperty);
    if (ch == null) {
      try {
        ch = Molecule.guessTotalCharge(mol, multiplicity.intValue());
      }
      catch (Exception ex) {}

      System.err.println("Set total charge to " + ch);
    }

    int charge = ch;
    try {
      //charge = Molecule.guessTotalCharge(mol, multiplicity.intValue());
      Molecule.validateTotalCharge(mol, ch, multiplicity.intValue());
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
                                    "Current combination of  total charge " +
                                    ch.intValue() + " and multiplicity " +
                                    multiplicity + " does not make sense" +
                                    "\nCheck values " + charge + " and " +
                                    multiplicity.intValue() +
                                    " suggested by a program"
                                    , "Error",
                                    JOptionPane.ERROR_MESSAGE);

      try {
        charge = Molecule.guessTotalCharge(mol, multiplicity.intValue());
      }
      catch (Exception exx) {}
    }

    setChargeSection(charge + " " + multiplicity);

    // --- Set molecule specs

    try {
      setMoleculeSpecsSection(Gaussian.getMoleculeSpecsAsString(mol));
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
                                    "Error generating molecule specifications: " +
                                    ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

  }

  public void setFontForText(Font newFont) {
    link0Panel1.setFontForText(newFont);
    this.routeSectionPanel1.setFontForText(newFont);
    this.titleSectionPanel1.setFontForText(newFont);
    this.moleculeSpecsPanel1.setFontForText(newFont);
  }

  public Font getFontForText() {
    return link0Panel1.getFontForText();
  }

}

class SimpleG03EditorPanel_validateGeomButton_actionAdapter
    implements ActionListener {
  private SimpleG03EditorPanel adaptee;
  SimpleG03EditorPanel_validateGeomButton_actionAdapter(
      SimpleG03EditorPanel
      adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.validateGeomButton_actionPerformed(e);
  }
}
