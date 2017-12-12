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
package cct.applets;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.dialogs.JamberooCore;
import cct.dialogs.MainPopupMenu;
import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import cct.modelling.MolecularDataWizard;
import cct.modelling.Molecule;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.JOptionPane;

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
//**************************************************************
public class JamberooApplet
        extends Applet implements MouseListener,
        MouseMotionListener, ActionListener {

  boolean isStandalone = false;
  BorderLayout borderLayout1 = new BorderLayout();
  //SampleFrame f;
  String msg = "";
  int mouseX = 0, mouseY = 10;
  int movX = 0, movY = 0;
  int tempInt;
  Java3dUniverse Java3dUniverse;
  Button Numbers = null;
  static String labelAtomicNumbers = "Atomic Numbers";
  static String unlabelAtoms = "Unlabel Atoms";
  JamberooCore jamberooCore = new JamberooCore();
  static final Logger logger = Logger.getLogger(JamberooApplet.class.getCanonicalName());

  //Get a parameter value
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def)
            : (getParameter(key) != null ? getParameter(key) : def);
  }

  //Construct the applet
  public JamberooApplet() {
  }

  //Initialize the applet
  @Override
  public void init() {
    try {
      Init();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void Init() throws Exception {


    //f = new SampleFrame("Molecular Builder");
    //f.setSize(800, 600);
    //f.setVisible(true);

    //repaint();

    // register this object to receive its own mouse events
    //addMouseListener(this);
    //addMouseMotionListener(this);

    setBackground(Color.DARK_GRAY);

    GridBagLayout gridbag = new GridBagLayout();
    setLayout(gridbag);

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 10;
    c.gridheight = 10;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(1, 1, 1, 1);

    Java3dUniverse = jamberooCore.getJamberooRenderer();

    MainPopupMenu mainPopup = new MainPopupMenu(jamberooCore);
    mainPopup.createPopupMenu();

    add(Java3dUniverse.getCanvas3D());

    gridbag.setConstraints(Java3dUniverse.getCanvas3D(), c);

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 11;
    c.gridwidth = 10;
    c.gridheight = 1;
    //c.weightx = 1;
    //c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(1, 1, 1, 1);

    Panel pControls = new Panel(new FlowLayout());
    pControls.setBackground(Color.BLACK);
    pControls.setBackground(Color.DARK_GRAY);
    add(pControls);
    gridbag.setConstraints(pControls, c);

    /*
    Numbers = new Button(labelAtomicNumbers);
    Numbers.addActionListener(this);
    pControls.add(Numbers);
    
    Button unLabel = new Button(unlabelAtoms);
    unLabel.addActionListener(this);
    pControls.add(unLabel);
     * 
     */
    this.validate();

    MoleculeInterface mol = new Molecule();
    mol.addMonomer("Molecule");


    // --- Parse input parameters

    String key, format, value = getParameter("natoms");
    if (value != null) {
      logger.info("Natoms: " + value);

      AtomInterface atom = null;
      StringTokenizer st;
      float xyz[] = new float[3];

      int nat = Integer.parseInt(value);
      for (int i = 0; i < nat; i++) {
        String param = "a" + i;
        value = getParameter(param);
        logger.info(param + ": " + value);
        st = new StringTokenizer(value, ",");
        int element = Integer.parseInt(st.nextToken());
        String name = st.nextToken(); // Skip for now
        for (int j = 0; j < 3; j++) {
          xyz[j] = Float.parseFloat(st.nextToken());
        }

        atom = mol.getNewAtomInstance();
        atom.setXYZ(xyz[0], xyz[1], xyz[2]);
        atom.setAtomicNumber(element);
        atom.setName(name);

        mol.addAtom(atom);
      }

      value = getParameter("nbonds");
      if (value != null) {
        logger.info("Nbonds: " + value);
        nat = Integer.parseInt(value);
      } else {
        nat = 0;
      }

      for (int i = 0; i < nat; i++) {
        String param = "b" + i;
        value = getParameter(param);
        logger.info(param + ": " + value);
        st = new StringTokenizer(value, ",");
        int origin = Integer.parseInt(st.nextToken());
        int target = Integer.parseInt(st.nextToken());
        BondInterface bond = mol.getNewBondInstance(mol.getAtomInterface(origin), mol.getAtomInterface(target));
        //Bond b = mol.addBondBetweenAtoms(origin, target);
        logger.info("Bond: " + (i + 1) + " origin: " + origin + " target: " + target);
        mol.addBond(bond);
      }
      Java3dUniverse.setMolecule(mol);

    } else if ((value = getParameter("file")) != null) {
      format = getParameter("format");
      URL baseURL;
      try {
        baseURL = new URL(this.getCodeBase(), value);
        BufferedReader in = new BufferedReader(new InputStreamReader(baseURL.openStream()));
        //CHEMISTRY_FILE_FORMAT form = CHEMISTRY_FILE_FORMAT.valueOf(format.toUpperCase());
        //if (form == null) {
        //  throw new Exception("Unknown format " + format);
        //}
        MolecularDataWizard wizard = new MolecularDataWizard();
        wizard.parseMolecularData(format, in);
        if (wizard.getNumberMolecules() > 0) {
          Java3dUniverse.setMolecule(wizard.getMolecule());
        } else {
          throw new Exception("Din't find atoms in file " + value);
        }
      } catch (Exception ex) {
        String error = "Error opening URL: " + getCodeBase().toString() + value + " : " + ex.getMessage();
        logger.severe(error);
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
      }
    }


  }

  //Start the applet

  /*
  public void start() {
  //f.setVisible(true);
  repaint();
  }
  
  
  //Stop the applet
  public void stop() {
  //f.setVisible(false);
  }
   */
  @Override
  public void paint(Graphics g) {
    //g.drawString(msg, mouseX, mouseY);
    //g.drawString("Mouse at " + movX + ", " + movY, 0, 10);
    //if (molec != null) {
    //    g.drawString("Number of atoms " + molec.getNumberOfAtoms(), 0, 20);
    //} else {
    //    g.drawString("Molecule pointer is NULL", 0, 20);
    //}
  }

  // Handle mouse clicked.
  @Override
  public void mouseClicked(MouseEvent me) {
  }

  // Handle mouse entered.
  @Override
  public void mouseEntered(MouseEvent me) {
    // save coordinates
    mouseX = 0;
    mouseY = 24;
    msg = "Mouse just entered applet window.";
    //repaint();
  }

  // Handle mouse exited.
  @Override
  public void mouseExited(MouseEvent me) {
    // save coordinates
    mouseX = 0;
    mouseY = 24;
    msg = "Mouse just left applet window.";
    //repaint();
  }

  // Handle button pressed.
  @Override
  public void mousePressed(MouseEvent me) {
    // save coordinates
    mouseX = me.getX();
    mouseY = me.getY();
    msg = "Down";
    //repaint();
  }

  // Handle button released.
  @Override
  public void mouseReleased(MouseEvent me) {
    // save coordinates
    mouseX = me.getX();
    mouseY = me.getY();
    msg = "Up";
    //repaint();
  }

  // Handle mouse dragged.
  @Override
  public void mouseDragged(MouseEvent me) {
    // save coordinates
    mouseX = me.getX();
    mouseY = me.getY();
    movX = me.getX();
    movY = me.getY();
    msg = "*";
    //repaint();
  }

  // Handle mouse moved.
  @Override
  public void mouseMoved(MouseEvent me) {
    // save coordinates
    movX = me.getX();
    movY = me.getY();
    //repaint(0, 0, 100, 20);
  }

  //Destroy the applet
  @Override
  public void destroy() {
  }

  //Get Applet information
  @Override
  public String getAppletInfo() {
    return "Jamberoo Applet";
  }

  //Get parameter info
  @Override
  public String[][] getParameterInfo() {
    return null;
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    String controlName = ae.getSource().toString();
    controlName = controlName.substring(controlName.indexOf('[') + 1);
    controlName = controlName.substring(0, controlName.indexOf(','));
    logger.info("controlName: " + controlName);

    // --- Molecule name is changed
    if (ae.getActionCommand().toString().equals(labelAtomicNumbers)) {
      Java3dUniverse.selectAllAtoms(true);
      Java3dUniverse.labelSelectedAtoms("Atom number");
      Java3dUniverse.selectAllAtoms(false);

    } else if (ae.getActionCommand().toString().equals(unlabelAtoms)) {
      Java3dUniverse.selectAllAtoms(true);
      Java3dUniverse.unlabelSelectedAtoms();
      Java3dUniverse.selectAllAtoms(false);
    }
  }
}
