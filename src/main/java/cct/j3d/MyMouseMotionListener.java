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
package cct.j3d;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.logging.Logger;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class MyMouseMotionListener
        implements MouseMotionListener, ActionListener {

  public MyMouseMotionListener() {
    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  int pX, pY;
  Java3dUniverse daddy = null;
  Timer popupTimer = null;
  PopupFactory factory = null;
  findAtom find = new findAtom();
  Popup popup;
  JLabel jLabel = new JLabel();
  static final Logger logger = Logger.getLogger(MyMouseMotionListener.class.getCanonicalName());

  public MyMouseMotionListener(Java3dUniverse target) {
    daddy = target;

    jLabel.setBackground(new Color(10, 49, 255));
    jLabel.setForeground(Color.yellow);
    jLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.gray, 2),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)));
    jLabel.setDebugGraphicsOptions(0);
    jLabel.setOpaque(true);
    jLabel.setDisplayedMnemonic('0');
    jLabel.setText("Selected Atom");

  }

  /**
   * Invoked when a mouse button is pressed on a component and then dragged.
   *
   * @param e MouseEvent
   */
  @Override
  public void mouseDragged(MouseEvent e) {
    doWork(e);
  }

  /**
   * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
   *
   * @param e MouseEvent
   */
  @Override
  public void mouseMoved(MouseEvent e) {
    doWork(e);
  }

  synchronized private void doWork(MouseEvent e) {
    if (popupTimer != null && popupTimer.isRunning()) {
      popupTimer.stop();
      //if (popup != null) {
      //  popup.hide();
      //  popup = null;
      //}
      //logger.info("Stop old timer");
    } else if (popupTimer != null) {
      pX = e.getX();
      pY = e.getY();
      popupTimer.restart();
      //logger.info("Restart timer");
      return;
    }

    if (popup != null) {
      popup.hide();
      popup = null;
    }

    pX = e.getX();
    pY = e.getY();
    popupTimer = new Timer(1000, this);
    popupTimer.setRepeats(false);
    popupTimer.start();
    //logger.info("Start new timer");

    //logger.info("x="+e.getX()+" y="+e.getY());
    //daddy.findClosestAtom(e.getX(),e.getY());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (logger.isLoggable(java.util.logging.Level.INFO)) {
      logger.info("Fire popup");
    }
    if (find.isAlive()) {
      find.interrupt();
      Thread.interrupted();
    }
    find = new findAtom();
    find.start();
  }

  private void jbInit() throws Exception {
  }

  private class findAtom
          extends Thread {

    public findAtom() {
      super();
    }

    @Override
    public void run() {
      boolean isBond = false;
      int atom = daddy.findClosestAtom(pX, pY);
      if (atom == -1) {
        atom = daddy.findClosestBond(pX, pY);
        if (atom == -1) {
          return;
        }
        isBond = true;
      }
      if (factory == null) {
        factory = PopupFactory.getSharedInstance();
        //factory = new PopupFactory();
      }
      Point point = daddy.getCanvas3D().getLocationOnScreen();
      String info = "";

      if (isBond) {
        BondInterface bond = daddy.getMolecule().getBondInterface(atom);
        AtomInterface at = bond.getIAtomInterface();
        int index = daddy.getMolecule().getAtomIndex(at);
        String name = at.getName();
        int monomer = at.getSubstructureNumber();
        String monomerName = daddy.getMolecule().getMonomerInterface(
                monomer).getName();

        info = name + "(" + (index + 1) + ": " + monomerName + "-"
                + (monomer + 1) + ") - ";

        at = bond.getJAtomInterface();
        index = daddy.getMolecule().getAtomIndex(at);
        name = at.getName();
        monomer = at.getSubstructureNumber();
        monomerName = daddy.getMolecule().getMonomerInterface(
                monomer).getName();

        info += name + "(" + (index + 1) + ": " + monomerName + "-"
                + (monomer + 1) + ") : ";

        info = String.format("%s%8.4f", info, bond.bondLength());
      } else {
        String name = daddy.getMolecule().getAtomInterface(atom).getName();
        int monomer = daddy.getMolecule().getAtomInterface(atom).
                getSubstructureNumber();
        String monomerName = daddy.getMolecule().getMonomerInterface(
                monomer).
                getName();

        info = (atom + 1) + ": " + name + ": " + monomerName + "-"
                + (monomer + 1);
      }

      jLabel.setText(info);
      popup = factory.getPopup(daddy.getCanvas3D(), jLabel,
              point.x + pX + 15,
              point.y + pY - 15);
      popup.show();
    }
  }
}
