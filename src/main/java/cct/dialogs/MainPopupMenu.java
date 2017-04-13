/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cct.interfaces.JamberooCoreInterface;
import cct.j3d.Java3dUniverse;

/**
 *
 * @author vvv900
 */
public class MainPopupMenu extends JPopupMenu implements ActionListener {

  private Java3dUniverse j3d = null;
  private int currX = 0, currY = 0;
  private Component component;
  private JamberooCoreInterface jamberooCore;
  static final Logger logger = Logger.getLogger(MainPopupMenu.class.getCanonicalName());

  public MainPopupMenu(JamberooCoreInterface jamberooCore) {
    this.jamberooCore = jamberooCore;
    j3d = jamberooCore.getJamberooRenderer();
  }

  public void createPopupMenu() {
    JMenuItem menuItem;

    //Create the popup menu.
    JPopupMenu popup = new JPopupMenu("Jamberoo Controls");

    try {
      FileMenu fm = new FileMenu(jamberooCore);
      fm.setText("File");
      popup.add(fm);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    EditMenu em = new EditMenu(jamberooCore);
    em.createMenu();
    em.setText("Edit");
    popup.add(em);

    ViewMenu vm = new ViewMenu(jamberooCore);
    vm.createMenu();
    vm.setText("View");
    popup.add(vm);

    CalculateMenu cm = new CalculateMenu(jamberooCore);
    cm.setParentComponent(this);
    cm.createMenu();
    cm.setText("Calculate");
    popup.add(cm);

    ToolsMenu tm = new ToolsMenu(jamberooCore);
    tm.createMenu();
    tm.setText("Tools");
    popup.add(tm);

    SetupMenu sm = new SetupMenu(jamberooCore);
    sm.createMenu();
    sm.setText("Setup");
    popup.add(sm);

    //Add listener to the text area so the popup menu can come up.
    MouseListener popupListener = new PopupListener(popup);
    j3d.getCanvas3D().addMouseListener(popupListener);
    //this.addMouseListener(popupListener);
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
  }

  public JPopupMenu createFilePopupMenu() {
    JMenuItem menuItem;
    //Create the popup menu.
    JPopupMenu popup = new JPopupMenu("File");
    menuItem = new JMenuItem("Open File");
    menuItem.addActionListener(this);
    popup.add(menuItem);
    menuItem = new JMenuItem("Save As");
    menuItem.addActionListener(this);
    popup.add(menuItem);

    //Add listener to the text area so the popup menu can come up.
    //MouseListener popupListener = new PopupListener(popup);
    //j3d.getCanvas3D().addMouseListener(popupListener);
    //this.addMouseListener(popupListener);
    return popup;
  }

  class PopupListener extends MouseAdapter {

    JPopupMenu popup;

    PopupListener(JPopupMenu popupMenu) {

      popup = popupMenu;
    }

    @Override
    public void mousePressed(MouseEvent e) {
      logger.info("mousePressed");
      currX = e.getX();
      currY = e.getY();
      component = e.getComponent();
      maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      logger.info("mouseReleased");
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }
}
