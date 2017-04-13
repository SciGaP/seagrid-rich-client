/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import cct.interfaces.JamberooCoreInterface;
import cct.j3d.ChangeAtomColors;
import cct.j3d.Java3dUniverse;
import cct.j3d.SuperCell;
import cct.modelling.OperationsOnAtoms;

/**
 *
 * @author vvv900
 */
public class ViewMenu extends JMenu implements ActionListener {

  public enum VIEW_MENU_ACTIONS {

    UNKNOWN_VIEW_ACTION, CENTER_MOLECULE, NEW_ROTATION_CENTER, DISPLAY_SELECTED_ATOMS, UNDISPLAY_SELECTED_ATOMS,
    CHANGE_COLOR_FOR_SELECTED_ATOMS, LABEL_SELECTED_ATOMS, UNLABEL_SELECTED_ATOMS, LABELS_SIZE, LABELS_COLOR
  }
  private Java3dUniverse java3dUniverse;
  private JamberooCoreInterface jamberooCore;
  private JFrame crystalEditorDialog = null;
  private SelectColorDialog selecColorDialog = null;
  static final Logger logger = Logger.getLogger(EditMenu.class.getCanonicalName());

  public ViewMenu(JamberooCoreInterface core) {
    super();
    jamberooCore = core;
    java3dUniverse = jamberooCore.getJamberooRenderer();
  }

  public void createMenu() {

    // --- Center molecule

    JMenuItem jMenuCenterMolecule = new JMenuItem("Center Molecule");
    jMenuCenterMolecule.setActionCommand(VIEW_MENU_ACTIONS.CENTER_MOLECULE.name());
    jMenuCenterMolecule.addActionListener(this);
    add(jMenuCenterMolecule);


    // --- New center of rotation

    JMenuItem jMenuItem = new JMenuItem("New Rotation Center");
    jMenuItem.setActionCommand(VIEW_MENU_ACTIONS.NEW_ROTATION_CENTER.name());
    jMenuItem.addActionListener(this);
    add(jMenuItem);


    // --- rendering styles

    JMenu jSubmenuRenderStyle = new JMenu("Rendering Styles");
    String[] styles = java3dUniverse.getRenderingStyles();
    if (styles.length > 0) {
      ButtonGroup stylesGroup = new ButtonGroup();
      ActionListener stylesAL = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          JRadioButtonMenuItem atomInfo = (JRadioButtonMenuItem) e.getSource();
          String arg = e.getActionCommand();
          try {
            java3dUniverse.setGlobalRenderingStyle(arg);
          } catch (Exception ex) {
            System.err.println(ex.getMessage());
          }
          atomInfo.setSelected(true);
        }
      };
      for (int i = 0; i < styles.length; i++) {
        JRadioButtonMenuItem jRadio = new JRadioButtonMenuItem(styles[i]);
        jRadio.addActionListener(stylesAL);
        stylesGroup.add(jRadio);
        jSubmenuRenderStyle.add(jRadio);
      }
      add(jSubmenuRenderStyle);
    }

    // --- Manage graphics object

    JMenuItem manageObjects = new JMenuItem("Manage Objects");
    manageObjects.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        //JEditorFrame.initGraphObjDialog(java3dUniverse);
        jamberooCore.showGraphicsObjectsDialog(true);
        //gopDialog.setVisible(true);
      }
    });
    manageObjects.setEnabled(true);
    add(manageObjects);

    // --- Crystal editor menu

    addSeparator();
    JMenuItem crystalEditor = new JMenuItem("View Periodic Image");
    crystalEditor.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (crystalEditorDialog == null) {
          crystalEditorDialog = new JFrame("Periodic Image View Control");
          ViewPeriodicImagePanel manager = new ViewPeriodicImagePanel();
          manager.setCellViewManager(new SuperCell(java3dUniverse));
          crystalEditorDialog.add(manager);
          crystalEditorDialog.pack();
          crystalEditorDialog.setLocationRelativeTo(java3dUniverse.getCanvas3D());
        }
        crystalEditorDialog.setVisible(true);
      }
    });

    crystalEditor.setEnabled(true);
    add(crystalEditor);

    addSeparator();

    JMenuItem jMenuDisplayAtoms = new JMenuItem("Display Atoms");
    jMenuDisplayAtoms.setActionCommand(VIEW_MENU_ACTIONS.DISPLAY_SELECTED_ATOMS.name());
    jMenuDisplayAtoms.addActionListener(this);
    add(jMenuDisplayAtoms);

    JMenuItem jMenuUndisplayAtoms = new JMenuItem("Undisplay Selected Atoms");
    jMenuUndisplayAtoms.setActionCommand(VIEW_MENU_ACTIONS.UNDISPLAY_SELECTED_ATOMS.name());
    jMenuUndisplayAtoms.addActionListener(this);
    add(jMenuUndisplayAtoms);

    addSeparator();

    JMenuItem jMenuAtomColors = new JMenuItem("Change Atom Colors");
    jMenuAtomColors.setActionCommand(VIEW_MENU_ACTIONS.CHANGE_COLOR_FOR_SELECTED_ATOMS.name());
    jMenuAtomColors.addActionListener(this);
    add(jMenuAtomColors);
    addSeparator();

    JMenuItem jMenuLabelAtoms = new JMenuItem("Label Selected Atoms");
    jMenuLabelAtoms.setActionCommand(VIEW_MENU_ACTIONS.LABEL_SELECTED_ATOMS.name());
    jMenuLabelAtoms.addActionListener(this);
    add(jMenuLabelAtoms);

    JMenuItem jMenuUnlabelAtoms = new JMenuItem("Unlabel Selected Atoms");
    jMenuUnlabelAtoms.setActionCommand(VIEW_MENU_ACTIONS.UNLABEL_SELECTED_ATOMS.name());
    jMenuUnlabelAtoms.addActionListener(this);
    add(jMenuUnlabelAtoms);

    JMenuItem jMenuAtomLabelsSize = new JMenuItem("Change Atom Labels Size");
    jMenuAtomLabelsSize.setActionCommand(VIEW_MENU_ACTIONS.LABELS_SIZE.name());
    jMenuAtomLabelsSize.addActionListener(this);
    add(jMenuAtomLabelsSize);

    JMenuItem jMenuAtomLabelsColor = new JMenuItem("Change Atom Labels Color");
    jMenuAtomLabelsColor.setActionCommand(VIEW_MENU_ACTIONS.LABELS_COLOR.name());
    jMenuAtomLabelsColor.addActionListener(this);
    add(jMenuAtomLabelsColor);

  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    String actionCommand = actionEvent.getActionCommand();

    logger.info("actionEvent.getActionCommand(): " + actionEvent.getActionCommand());

    // --- Get action command

    VIEW_MENU_ACTIONS action = VIEW_MENU_ACTIONS.UNKNOWN_VIEW_ACTION;
    try {
      action = VIEW_MENU_ACTIONS.valueOf(actionCommand);
    } catch (Exception ex) {
      logger.warning("Unknown view menu action: " + actionCommand);
      return;
    }

    switch (action) {
      case CENTER_MOLECULE:

        if (java3dUniverse.getMolecule() == null) {
          return;
        }
        java3dUniverse.centerSceneOnScreen();
        break;

      case NEW_ROTATION_CENTER:
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_ROTATION_CENTER,
                OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      case DISPLAY_SELECTED_ATOMS:
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_DISPLAY_ATOMS,
                OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      case UNDISPLAY_SELECTED_ATOMS:
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_UNDISPLAY_ATOMS,
                OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      case CHANGE_COLOR_FOR_SELECTED_ATOMS:
        if (selecColorDialog == null) {
          selecColorDialog = new SelectColorDialog(null, "Select Color for Atoms", false);
        }
        ChangeAtomColors changeAtomColors = new ChangeAtomColors(java3dUniverse);
        selecColorDialog.setColorChangerInterface(changeAtomColors);
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_CHANGE_ATOM_COLORS,
                OperationsOnAtoms.SELECTION_UNLIMITED, false,
                selecColorDialog);
        break;

      case LABEL_SELECTED_ATOMS:
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_LABEL_ATOMS, OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      case UNLABEL_SELECTED_ATOMS:
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_UNLABEL_ATOMS,
                OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      case LABELS_SIZE:
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_CHANGE_ATOM_LABELS_SIZE, OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      case LABELS_COLOR:
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_CHANGE_ATOM_LABELS_COLOR, OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;
    }
  }

  public Java3dUniverse getJava3dUniverse() {
    return java3dUniverse;
  }

  public void setJava3dUniverse(Java3dUniverse java3dUniverse) {
    this.java3dUniverse = java3dUniverse;
  }
}
