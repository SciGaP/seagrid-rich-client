/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dialogs;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import cct.GlobalSettings;
import cct.gaussian.ui.ManageCubesFrame;
import cct.interfaces.AtomInterface;
import cct.interfaces.HelperInterface;
import cct.interfaces.JamberooCoreInterface;
import cct.interfaces.MoleculeChangeListener;
import cct.interfaces.MoleculeEventObject;
import cct.interfaces.MoleculeEventObject.MOLECULE_EVENT;
import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import cct.modelling.AtomicSets;
import cct.modelling.CCTAtomTypes;
import cct.modelling.ChemicalElements;
import cct.modelling.FormatManager;
import cct.modelling.MolecularDataWizard;
import cct.modelling.MolecularFileFormats;
import cct.modelling.Molecule;
import cct.modelling.OperationsOnAtoms;
import cct.modelling.ui.EditAndPasteCoordFrame;
import cct.modelling.ui.EditAndSaveCoordDialog;
import cct.modelling.ui.SetNamePanel;
import cct.tools.FileFilterImpl;
import cct.tools.FragmentDictionaryParser;
import cct.tools.TextClipboard;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author vvv900
 */
public class EditMenu extends JMenu implements ActionListener {

  private static final String ERROR_MESSAGE = "Error";
  public static final String ADD_ATOMS_MESSAGE = "Add Atoms";
  public static final String ADD_MOLECULE_MESSAGE = "Add Molecule";
  public static final String MODIFY_ATOMS_MESSAGE = "Modify Atoms";
  public static final String DELETE_ATOMS_MESSAGE = "Delete Atoms";

  public enum EDIT_MENU_ACTIONS {

    UNKNOWN_EDIT_ACTION, SET_MOLECULE_NAME, ADD_ATOMS, ADD_MOLECULE, ADD_FRAGMENT, ADD_HYDROGENS, MODIFY_ATOMS, MODIFY_BONDS,
    MODIFY_ANGLES, MODIFY_TORSIONS, GUESS_ATOM_TYPES, RECALC_CONNECTIVITY, DELETE_ATOMS, ERASE_MOLECULE, CREATE_CENTROID,
    SOLVATE_CAP, SOLVATE_SHELL, CREATE_ATOM_SET, EDIT_ATOM_SET
  }
  private JMenuItem jMenuSetMolName = new JMenuItem();
  private Java3dUniverse java3dUniverse;
  private Java3dUniverse secondJ3D = null, thirdJ3D = null;
  private EditAndSaveCoordDialog editAndSaveCoordDialog = null;
  private Component parentComponent = null;
  private EditAndPasteCoordFrame editAndPasteCoordFrame = null;
  private JMenu jSubmenuAdd = new JMenu("Add");
  private JMenuItem jAddMolecule = new JMenuItem();
  static final Logger logger = Logger.getLogger(EditMenu.class.getCanonicalName());
  private HelperInterface helper = null;
  private JAddMoleculeDialog jAddMoleculeDialog = null;
  private JAddFragmentDialog jAddFragmentDialog = null;
  private URL fragDic = null;
  private List volumeDataList = null;
  private ManageCubesFrame manageCubesDialog = null;
  private JamberooCoreInterface jamberooCore;
  private Set<MoleculeChangeListener> moleculeChangeListeners = new LinkedHashSet<MoleculeChangeListener>();

  public EditMenu(JamberooCoreInterface core) {
    super();
    jamberooCore = core;
    java3dUniverse = jamberooCore.getJamberooRenderer();
    helper = jamberooCore.getHelper();
  }

  public void addMoleculeChangeListener(MoleculeChangeListener listener) {
    moleculeChangeListeners.add(listener);
  }

  public void removeMoleculeChangeListener(MoleculeChangeListener listener) {
    moleculeChangeListeners.remove(listener);
  }

  public void removeAllMoleculeChangeListeners() {
    moleculeChangeListeners.removeAll(moleculeChangeListeners);
  }

  public void createMenu() {

    jMenuSetMolName.setText("Set Molecule Name");
    jMenuSetMolName.setActionCommand(EDIT_MENU_ACTIONS.SET_MOLECULE_NAME.name());
    jMenuSetMolName.addActionListener(this);
    jMenuSetMolName.setIcon(GlobalSettings.ICON_16x16_DOCUMENT_EDIT);
    add(jMenuSetMolName);
    // --- Clipboard stuff

    addSeparator();

    if (parentComponent == null) {
      parentComponent = this;
    }

    JMenuItem jMenuItem = new JMenuItem("Copy to Clipboard");
    jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    jMenuItem.setIcon(GlobalSettings.ICON_16x16_DOCUMENT_INTO);
    jMenuItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          if (editAndSaveCoordDialog == null) {
            editAndSaveCoordDialog = new EditAndSaveCoordDialog("Clipboard content");
            editAndSaveCoordDialog.setFormatManager(new FormatManager());
            editAndSaveCoordDialog.setIconImage(GlobalSettings.ICON_16x16_DOCUMENT_INTO.getImage());
            editAndSaveCoordDialog.setDefaultFileName("coordinates.txt");
            editAndSaveCoordDialog.setLocationRelativeTo(parentComponent);
            editAndSaveCoordDialog.pack();
            editAndSaveCoordDialog.setSize(320, 480);
          }
          editAndSaveCoordDialog.setMoleculeInterface(java3dUniverse.getMolecule());
          editAndSaveCoordDialog.setVisible(true);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    add(jMenuItem);

    jMenuItem = new JMenuItem("Paste from Clipboard");
    jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
        ActionEvent.CTRL_MASK));
    jMenuItem.setIcon(GlobalSettings.ICON_16x16_DOCUMENT_OUT);
    jMenuItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        if (editAndPasteCoordFrame != null
            && editAndPasteCoordFrame.isVisible()) {
          return;
        }

        TextClipboard textTransfer = new TextClipboard();
        try {
          String contents = textTransfer.getClipboardContents();
          if (contents.trim().length() < 1) {
            JOptionPane.showMessageDialog(parentComponent, "Clipboad contents is empty", "error",
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          if (editAndPasteCoordFrame == null) {
            editAndPasteCoordFrame = new EditAndPasteCoordFrame();
            editAndPasteCoordFrame.setTitle("Clipboard contents");
            editAndPasteCoordFrame.setIconImage(GlobalSettings.ICON_16x16_DOCUMENT_OUT.getImage());
            editAndPasteCoordFrame.setDefaultFileName("coordinates.txt");
            editAndPasteCoordFrame.setLocationRelativeTo(parentComponent);
            editAndPasteCoordFrame.pack();
            int width = 320;
            if (editAndPasteCoordFrame.getSize().width > 320) {
              width = editAndPasteCoordFrame.getSize().width;
            }
            editAndPasteCoordFrame.setSize(width, 480);
          }
          editAndPasteCoordFrame.setJava3dUniverse(java3dUniverse);
          editAndPasteCoordFrame.setCoordinates(contents);
          editAndPasteCoordFrame.setVisible(true);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(parentComponent, ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    add(jMenuItem);

    addSeparator();

    // --- "Add Atoms" item
    jMenuItem = new JMenuItem(ADD_ATOMS_MESSAGE, GlobalSettings.ICON_16x16_ADD_ATOM);
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.ADD_ATOMS.name());
    jMenuItem.addActionListener(this);
    jSubmenuAdd.add(jMenuItem);

    // --- Add Molecule
    jAddMolecule.setText(ADD_MOLECULE_MESSAGE);
    jAddMolecule.setIcon(GlobalSettings.ICON_16x16_MOLECULE);
    jAddMolecule.setActionCommand(EDIT_MENU_ACTIONS.ADD_MOLECULE.name());
    jAddMolecule.addActionListener(this);
    jSubmenuAdd.add(jAddMolecule);

    // --- Add Fragment
    jMenuItem = new JMenuItem();
    jMenuItem.setText("Add Fragment");
    jMenuItem.setIcon(GlobalSettings.ICON_16x16_ADD_FRAGMENT);
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.ADD_FRAGMENT.name());
    jMenuItem.addActionListener(this);
    jSubmenuAdd.add(jMenuItem);

    // --- Generate Hydrogens
    jMenuItem = new JMenuItem("Add Hydrogens", GlobalSettings.ICON_16x16_ADD_ATOM);
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.ADD_HYDROGENS.name());
    jMenuItem.addActionListener(this);
    jSubmenuAdd.add(jMenuItem);
    jSubmenuAdd.setIcon(GlobalSettings.ICON_16x16_ADD_ATOM);
    add(jSubmenuAdd);

    // --- Modify submenu
    JMenu jSubmenuModify = new JMenu();
    jSubmenuModify.setText("Modify");
    jSubmenuModify.setIcon(GlobalSettings.ICON_16x16_WRENCH);
    add(jSubmenuModify);

    // --- "Modify Atoms" item
    jMenuItem = new JMenuItem(MODIFY_ATOMS_MESSAGE, GlobalSettings.ICON_16x16_ATOM);
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.MODIFY_ATOMS.name());
    jMenuItem.addActionListener(this);
    jSubmenuModify.add(jMenuItem);

    // --- "Modify Bond"
    JMenuItem jMenuModifyBond = new JMenuItem("Modify Bonds");
    jMenuModifyBond.setIcon(GlobalSettings.ICON_16x16_BOND);
    jMenuModifyBond.setActionCommand(EDIT_MENU_ACTIONS.MODIFY_BONDS.name());
    jMenuModifyBond.addActionListener(this);
    jSubmenuModify.add(jMenuModifyBond);

    // --- "Modify Angle"
    JMenuItem jMenuModifyAngle = new JMenuItem("Modify Angles");
    jMenuModifyAngle.setIcon(GlobalSettings.ICON_16x16_ANGLE);
    jMenuModifyAngle.setActionCommand(EDIT_MENU_ACTIONS.MODIFY_ANGLES.name());
    jMenuModifyAngle.addActionListener(this);
    jSubmenuModify.add(jMenuModifyAngle);

    // --- Modify Dihedral
    JMenuItem jMenuModifyTorsion = new JMenuItem("Modify Torsions");
    jMenuModifyTorsion.setIcon(GlobalSettings.ICON_16x16_HUMMER);
    jMenuModifyTorsion.setActionCommand(EDIT_MENU_ACTIONS.MODIFY_TORSIONS.name());
    jMenuModifyTorsion.addActionListener(this);
    jSubmenuModify.add(jMenuModifyTorsion);

    // --- Guess atom types
    JMenuItem jMenuGuessAtomTypes = new JMenuItem("Guess Atom Types");
    jMenuGuessAtomTypes.setIcon(GlobalSettings.ICON_16x16_HUMMER);
    jMenuGuessAtomTypes.setActionCommand(EDIT_MENU_ACTIONS.GUESS_ATOM_TYPES.name());
    jMenuGuessAtomTypes.addActionListener(this);
    jSubmenuModify.add(jMenuGuessAtomTypes);

    JMenuItem recalculateConnectivity = new JMenuItem("Redo Connectivity");
    recalculateConnectivity.setIcon(GlobalSettings.ICON_16x16_HUMMER);
    recalculateConnectivity.setActionCommand(EDIT_MENU_ACTIONS.RECALC_CONNECTIVITY.name());
    recalculateConnectivity.addActionListener(this);
    jSubmenuModify.add(recalculateConnectivity);

    // --- "Delete Atoms" item
    jMenuItem = new JMenuItem(DELETE_ATOMS_MESSAGE, GlobalSettings.ICON_16x16_CUT);
    jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.DELETE_ATOMS.name());
    jMenuItem.addActionListener(this);
    add(jMenuItem);

    // --- Erase Molecule
    JMenuItem jMenuEraseMolecule = new JMenuItem("Erase Molecule");
    jMenuEraseMolecule.setIcon(GlobalSettings.ICON_16x16_CUT);
    jMenuEraseMolecule.setActionCommand(EDIT_MENU_ACTIONS.ERASE_MOLECULE.name());
    jMenuEraseMolecule.addActionListener(this);
    add(jMenuEraseMolecule);

    // --- "Create Centroid" item
    jMenuItem = new JMenuItem("Create Centroid", GlobalSettings.ICON_16x16_CENTROID);
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.CREATE_CENTROID.name());
    jMenuItem.addActionListener(this);
    add(jMenuItem);

    // --- Solvate submenu
    JMenu jSubmenuSolvate = new JMenu();
    jSubmenuSolvate.setText("Solvate");
    jSubmenuSolvate.setIcon(GlobalSettings.ICON_16x16_WRENCH);
    add(jSubmenuSolvate);

    // --- Cap solvate
    jMenuItem = new JMenuItem("Cap", GlobalSettings.ICON_16x16_HUMMER);
    jMenuItem.addActionListener(this);
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.SOLVATE_CAP.name());
    jSubmenuSolvate.add(jMenuItem);

    // --- Shell solvate
    jMenuItem = new JMenuItem("Shell", GlobalSettings.ICON_16x16_HUMMER);
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.SOLVATE_SHELL.name());
    jMenuItem.addActionListener(this);
    jSubmenuSolvate.add(jMenuItem);

    // --- Handling sets
    addSeparator();

    jMenuItem = new JMenuItem("Create Atomic Set", GlobalSettings.ICON_16x16_ADD_ENTRY);
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.CREATE_ATOM_SET.name());
    jMenuItem.addActionListener(this);
    add(jMenuItem);

    jMenuItem = new JMenuItem("Edit Atomic Set", GlobalSettings.ICON_16x16_REMOVE_ENTRY);
    jMenuItem.setActionCommand(EDIT_MENU_ACTIONS.EDIT_ATOM_SET.name());
    jMenuItem.addActionListener(this);
    add(jMenuItem);
  }

  private boolean isOtherSelectionInProgress() {
    if (java3dUniverse.getMousePickingStatus()) {
      JOptionPane.showMessageDialog(this.getParentFrame(),
          "Another Selection is already in progress!", ERROR_MESSAGE,
          JOptionPane.ERROR_MESSAGE);
      return true;
    }
    return false;
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    String actionCommand = actionEvent.getActionCommand();

    logger.info("actionEvent.getActionCommand(): " + actionEvent.getActionCommand());

    // --- Get action command
    EDIT_MENU_ACTIONS action = EDIT_MENU_ACTIONS.UNKNOWN_EDIT_ACTION;
    try {
      action = EDIT_MENU_ACTIONS.valueOf(actionCommand);
    } catch (Exception ex) {
      logger.warning("Unknown edit menu action: " + actionCommand);
      return;
    }

    // --- Switch to action...
    switch (action) {

      case SET_MOLECULE_NAME:
        if (java3dUniverse.getMolecule() != null) {
          Object input = JOptionPane.showInputDialog(null,
              "Please enter new Molecule name",
              "Set Molecule name",
              JOptionPane.QUESTION_MESSAGE, null, null,
              java3dUniverse.getMolecule().getName());
          if (input == null) {
            return;
          }
          java3dUniverse.getMolecule().setName(input.toString());
        }
        break;

      // --- Add atoms
      case ADD_ATOMS:
        if (isOtherSelectionInProgress()) {
          return;
        }

        if (java3dUniverse.getMolecule() == null) {
          MoleculeInterface molecule = Molecule.getNewInstance();
          java3dUniverse.addMolecule(molecule);
        }
        java3dUniverse.enableMousePicking(true); // Enable mouse picking

        if (java3dUniverse.chooseAtomType == null) {
          JAtomTypeDialog temp = new JAtomTypeDialog(parentComponent instanceof JFrame ? (JFrame) parentComponent : new Frame(),
              "Select Atom Type", false);
          if (helper != null) {
            JButton h = temp.getHelpButton();
            h.setEnabled(true);
            helper.setHelpIDString(h, HelperInterface.JMD_ADD_ATOM_ID);
            helper.displayHelpFromSourceActionListener(h);
          }

          temp.setOKVisible(false);
          temp.setCancelVisible(true);
          temp.setTargetClass(java3dUniverse);

          temp.validate();
          temp.pack();
          temp.setLocationByPlatform(true);
          java3dUniverse.chooseAtomType = temp;
          //menuFrame.new_universe.chooseAtomType.addElements(ChemicalElements.
          //      getAllElements());
          //         }
        }
        JAtomTypeDialog atd = (JAtomTypeDialog) java3dUniverse.chooseAtomType;
        SwingUtilities.updateComponentTreeUI(atd);
        atd.setVisible(true);

        java3dUniverse.enableMousePicking(true);
        java3dUniverse.selectedMode = OperationsOnAtoms.SELECTION_ONE_ATOM_ONLY;
        java3dUniverse.setJobType(OperationsOnAtoms.SELECTED_ADD_ATOMS);
        java3dUniverse.selectAndProcessDialog = true;

        break;

      // --- Add molecule
      case ADD_MOLECULE:
        if (isOtherSelectionInProgress()) {
          return;
        }

        if (!isMoleculeLoaded()) {
          return;
        }

        if (secondJ3D == null) {
          secondJ3D = new Java3dUniverse();
        } else {
          secondJ3D.removeMolecule();
        }

        if (jAddMoleculeDialog == null) {
          jAddMoleculeDialog = new JAddMoleculeDialog(parentComponent instanceof JFrame ? (JFrame) parentComponent : new Frame(),
              ADD_MOLECULE_MESSAGE, false, secondJ3D);
          //jAddMoleculeDialog.setRenderer(secondJ3D);
          jAddMoleculeDialog.setParent(java3dUniverse);
          jAddMoleculeDialog.setSize(400, 400);
          jAddMoleculeDialog.setLocationRelativeTo(this.getParentFrame());

          if (helper != null) {
            JButton h = jAddMoleculeDialog.getHelpButton();
            h.setEnabled(true);
            helper.setHelpIDString(h, HelperInterface.JMD_ADD_MOLECULE_ID);
            helper.displayHelpFromSourceActionListener(h);
          }

        }
        SwingUtilities.updateComponentTreeUI(jAddMoleculeDialog);
        secondJ3D.selectedMode = OperationsOnAtoms.SELECTION_ONE_ATOM_ONLY;

        java3dUniverse.setOtherRenderer(secondJ3D);
        java3dUniverse.jAddMoleculeDialog = jAddMoleculeDialog;

        if (!guessAndOpenFile(secondJ3D)) {
          return;
        }

        secondJ3D.setJobType(OperationsOnAtoms.SELECTED_ADD_MOLECULE);
        secondJ3D.enableMousePicking(true);

        java3dUniverse.enableMousePicking(true);
        java3dUniverse.selectedMode = OperationsOnAtoms.SELECTION_ONE_ATOM_ONLY;
        java3dUniverse.setJobType(OperationsOnAtoms.SELECTED_ADD_MOLECULE);
        java3dUniverse.selectAndProcessDialog = true;

        jAddMoleculeDialog.setVisible(true);
        break;

      // --- Add molecule
      case ADD_FRAGMENT:
        if (isOtherSelectionInProgress()) {
          return;
        }

        if (java3dUniverse.getMolecule() == null) {
          MoleculeInterface molecule = Molecule.getNewInstance();
          java3dUniverse.addMolecule(molecule);
        }

        if (thirdJ3D == null) {
          thirdJ3D = new Java3dUniverse();
        }

        if (jAddFragmentDialog == null) {

          InputStream is = null;
          FragmentDictionaryParser fdp = new FragmentDictionaryParser();
          try {
            ClassLoader cl = cct.resources.Resources.class.getClassLoader();
            logger.info("Class loader: " + cl.toString());
            fragDic = cl.getResource(cct.GlobalSettings.getDefaultFragmentDictionary());

            logger.info("File: " + fragDic.getFile() + " path: " + fragDic.getPath() + " " + fragDic.toString());
            logger.info("Protocol: " + fragDic.getProtocol());
            //logger.info("Query: " + fragDic.getQuery());

            is = cl.getResourceAsStream(cct.GlobalSettings.getDefaultFragmentDictionary());

            fdp.setFragmentDictionary(fragDic);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(this.getParentFrame(), "Error opening default fragment dictionary: " + ex.getMessage(),
                ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);

            System.err.println(getClass().getCanonicalName() + " : " + ex.getMessage());
            return;
          }

          Map fragTree = FragmentDictionaryParser.parseFragmentDictionary(is);

          jAddFragmentDialog = new JAddFragmentDialog("Add Fragment", fragTree, thirdJ3D);
          jAddFragmentDialog.setFragmentDictionaryParser(fdp);
          jAddFragmentDialog.setParent(java3dUniverse);
          jAddFragmentDialog.setSize(350, 500);
          jAddFragmentDialog.setLocationRelativeTo(this.getParentFrame());
          jAddFragmentDialog.setAlwaysOnTop(true);

          if (helper != null) {
            JButton h = jAddFragmentDialog.getHelpButton();
            h.setEnabled(true);
            helper.setHelpIDString(h, HelperInterface.JMD_ADD_FRAGMENT_ID);
            helper.displayHelpFromSourceActionListener(h);
          }

          //java3dUniverse.jAddMoleculeDialog = jAddMoleculeDialog;
        }
        SwingUtilities.updateComponentTreeUI(jAddFragmentDialog);

        try {
          thirdJ3D.setFragmentDictionary(fragDic);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this.getParentFrame(), "Error setting default fragment dictionary: " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          System.err.println(getClass().getCanonicalName() + " : " + ex.getMessage());
          return;
        }

        thirdJ3D.selectedMode = OperationsOnAtoms.SELECTION_ONE_ATOM_ONLY;
        java3dUniverse.setOtherRenderer(thirdJ3D);

        java3dUniverse.enableMousePicking(true);
        java3dUniverse.selectedMode = OperationsOnAtoms.SELECTION_ONE_ATOM_ONLY;
        java3dUniverse.setJobType(OperationsOnAtoms.SELECTED_ADD_FRAGMENT);
        java3dUniverse.selectAndProcessDialog = true;

        jAddFragmentDialog.setVisible(true);
        break;

      // --- Add hydrogens
      case ADD_HYDROGENS:
        if (isOtherSelectionInProgress()) {
          return;
        }

        if (!isMoleculeLoaded()) {
          return;
        }

        MoleculeInterface molec = java3dUniverse.getMolecule();
        if (molec == null || molec.getNumberOfAtoms() < 1) {
          JOptionPane.showMessageDialog(this.getParentFrame(), "Load Molecule first!", "Warning",
              JOptionPane.WARNING_MESSAGE);
          return;
        }
        jamberooCore.openAtomSelectionDialog(java3dUniverse,
            OperationsOnAtoms.SELECTED_FILL_VALENCES_WITH_HYDROGENS,
            OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      // --- Modify atoms
      case MODIFY_ATOMS:
        if (isOtherSelectionInProgress()) {
          return;
        }

        if (java3dUniverse.jModifyAtomDialog == null) {
          java3dUniverse.jModifyAtomDialog = new JModifyAtomDialog(parentComponent instanceof JFrame ? (JFrame) parentComponent : new Frame(),
              "Select atoms to modify", false);
          java3dUniverse.jModifyAtomDialog.setTargetClass(java3dUniverse);
          java3dUniverse.jModifyAtomDialog.setGraphicsRenderer(
              java3dUniverse);
          java3dUniverse.jModifyAtomDialog.setElements(ChemicalElements.getAllElements());
          java3dUniverse.jModifyAtomDialog.setLocationRelativeTo(this.getParentFrame());
        }
        SwingUtilities.updateComponentTreeUI(java3dUniverse.jModifyAtomDialog);
        java3dUniverse.enableMousePicking(true);
        java3dUniverse.selectedMode = OperationsOnAtoms.SELECTION_ONE_ATOM_ONLY;
        java3dUniverse.setJobType(OperationsOnAtoms.SELECTED_MODIFY_ATOMS);
        java3dUniverse.selectAndProcessDialog = true;
        java3dUniverse.jModifyAtomDialog.setVisible(true);
        break;

      // --- Modify bonds
      case MODIFY_BONDS:
        if (isOtherSelectionInProgress()) {
          return;
        }

        if (java3dUniverse.jModifyBondDialog == null) {
          java3dUniverse.jModifyBondDialog = new JModifyBondDialog(parentComponent instanceof JFrame ? (JFrame) parentComponent : new Frame(),
              "Modify selected Bond", false);
          java3dUniverse.jModifyBondDialog.setLocationRelativeTo(this.getParentFrame());
          java3dUniverse.jModifyBondDialog.setTargetClass(java3dUniverse);
          if (helper != null) {
            JButton h = java3dUniverse.jModifyBondDialog.Help;
            h.setEnabled(true);
            helper.setHelpIDString(h, HelperInterface.JMD_MODIFY_BOND_ID);
            helper.displayHelpFromSourceActionListener(h);
          }
        }
        java3dUniverse.enableMousePicking(true);
        java3dUniverse.selectedMode = OperationsOnAtoms.SELECTION_TWO_ATOMS_ONLY;
        java3dUniverse.setJobType(OperationsOnAtoms.SELECTED_MODIFY_BONDS);
        java3dUniverse.selectAndProcessDialog = true;
        java3dUniverse.jModifyBondDialog.setVisible(true);
        SwingUtilities.updateComponentTreeUI(java3dUniverse.jModifyBondDialog);
        break;

      // --- Modify Angles
      case MODIFY_ANGLES:
        if (isOtherSelectionInProgress()) {
          return;
        }

        if (java3dUniverse.jModifyAngleDialog == null) {
          java3dUniverse.jModifyAngleDialog = new JModifyAngleDialog(getParentFrame(),
              "Modify selected Angle", false);
          java3dUniverse.jModifyAngleDialog.setLocationRelativeTo(getParentFrame());
          java3dUniverse.jModifyAngleDialog.setTargetClass(java3dUniverse);
          if (helper != null) {
            JButton h = java3dUniverse.jModifyAngleDialog.getHelpButton();
            h.setEnabled(true);
            helper.setHelpIDString(h, HelperInterface.JMD_MODIFY_ANGLE_ID);
            helper.displayHelpFromSourceActionListener(h);
          }

        }
        SwingUtilities.updateComponentTreeUI(java3dUniverse.jModifyAngleDialog);
        java3dUniverse.enableMousePicking(true);
        java3dUniverse.selectedMode = OperationsOnAtoms.SELECTION_THREE_ATOMS_ONLY;
        java3dUniverse.setJobType(OperationsOnAtoms.SELECTED_MODIFY_ANGLES);
        java3dUniverse.selectAndProcessDialog = true;
        java3dUniverse.jModifyAngleDialog.setVisible(true);
        break;

      // --- Modify torsions
      case MODIFY_TORSIONS:
        if (isOtherSelectionInProgress()) {
          return;
        }

        java3dUniverse.modifyDihedralAngleDialog(this.getParentFrame());

        if (helper != null) {
          JButton h = java3dUniverse.jModifyTorsionDialog.getHelpButton();
          h.setEnabled(true);
          helper.setHelpIDString(h, HelperInterface.JMD_MODIFY_TORSION_ID);
          helper.displayHelpFromSourceActionListener(h);
        }
        break;

      // --- Guess atom types
      case GUESS_ATOM_TYPES:
        if (isOtherSelectionInProgress()) {
          return;
        }
        Molecule.guessAtomTypes(java3dUniverse.getMolecule(),
            AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        break;

      // --- Recalculate connectivity
      case RECALC_CONNECTIVITY:
        if (isOtherSelectionInProgress()) {
          return;
        }
        java3dUniverse.deleteAllBonds();
        Molecule.guessCovalentBonds(java3dUniverse.getMoleculeInterface());
        java3dUniverse.drawBonds(java3dUniverse.getMoleculeInterface());
        break;

      // --- Delete selected atoms
      case DELETE_ATOMS:
        if (isOtherSelectionInProgress()) {
          return;
        }
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_DELETE_ATOMS,
            OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      // --- Erase molecule
      case ERASE_MOLECULE:
        java3dUniverse.removeMolecule();
        sendMoleculeChangedEvent("", MOLECULE_EVENT.MOLECULE_DELETED);
        if (volumeDataList != null) {
          volumeDataList.clear();
        }
        if (manageCubesDialog != null) {
          manageCubesDialog.setVisible(false);
        }
        jamberooCore.showGraphicsObjectsDialog(false);
        //if (gopDialog != null) {
        //  gopDialog.setVisible(false);
        //}
        break;

      // --- Create centroid
      case CREATE_CENTROID:
        if (isOtherSelectionInProgress()) {
          return;
        }

        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_CREATE_CENTROID,
            OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      // --- Solvate with a cap
      case SOLVATE_CAP:
        if (isOtherSelectionInProgress()) {
          return;
        }
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_SOLVATE_CAP,
            OperationsOnAtoms.SELECTION_UNLIMITED, false);

        break;

      // --- Solvate with a shell
      case SOLVATE_SHELL:
        if (isOtherSelectionInProgress()) {
          return;
        }
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_SOLVATE_SHELL,
            OperationsOnAtoms.SELECTION_UNLIMITED, false);
        break;

      // --- Create atom set
      case CREATE_ATOM_SET:
        if (isOtherSelectionInProgress()) {
          return;
        }
        SetNamePanel setNamePanel = new SetNamePanel();
        setNamePanel.setJava3dUniverse(java3dUniverse);
        jamberooCore.openAtomSelectionDialog(java3dUniverse, OperationsOnAtoms.SELECTED_CREATE_ATOMIC_SET,
            OperationsOnAtoms.SELECTION_UNLIMITED, false, setNamePanel);
        break;

      // --- Edit atom set
      case EDIT_ATOM_SET:
        if (isOtherSelectionInProgress()) {
          return;
        }

        if (java3dUniverse.getMoleculeInterface().getNumberOfAtoms() < 1) {
          return;
        }

        Object obj = java3dUniverse.getMoleculeInterface().getProperty(MoleculeInterface.AtomicSets);
        if (obj == null) {
          JOptionPane.showMessageDialog(this.getParentFrame(), "Molecule has no atom set(s)",
              "Warning", JOptionPane.WARNING_MESSAGE);
          return;
        } else if (!(obj instanceof AtomicSets)) {
          System.err.println(this.getClass().getCanonicalName()
              + ": expected AtomicSets, got " + obj.getClass().getCanonicalName()
              + " Ignored...");
          return;
        }
        AtomicSets sets = (AtomicSets) obj;
        if (sets.size() < 1) {
          JOptionPane.showMessageDialog(this.getParentFrame(), "No atom set(s) in molecule", "No sets", JOptionPane.WARNING_MESSAGE);
          return;
        }

        String[] names = new String[sets.size()];
        sets.keySet().toArray(names);
        Arrays.sort(names);

        SelectListDialog selectListDialog = new SelectListDialog(new Frame(),
            "Select available set(s) to delete");
        selectListDialog.setList(names);
        selectListDialog.setLocationRelativeTo(this.getParentFrame());
        if (!selectListDialog.showDialog()) {
          return;
        }

        Object[] selSets = selectListDialog.getSelectedValues();
        if (selSets == null || selSets.length < 1) {
          return;
        }

        for (int i = 0; i < selSets.length; i++) {
          sets.remove(selSets[i]);
        }
        Map props = java3dUniverse.getMoleculeInterface().getProperties();
        props.put(MoleculeInterface.AtomicSets, sets);
        break;
    }

  }

  protected void sendMoleculeChangedEvent(String fileName, MoleculeEventObject.MOLECULE_EVENT moleculeEvent) {
    MoleculeEventObject event = new MoleculeEventObject(this, moleculeEvent, fileName);
    for (MoleculeChangeListener listener : moleculeChangeListeners) {
      listener.moleculeChanged(event);
    }
  }

  private Frame getParentFrame() {
    return parentComponent instanceof JFrame ? (JFrame) parentComponent : new Frame();
  }

  public Component getParentComponent() {
    return parentComponent;
  }

  public void setParentComponent(Component parentComponent) {
    this.parentComponent = parentComponent;
  }

  boolean guessAndOpenFile(Java3dUniverse j3d) {

    JFileChooser chooser = new JFileChooser();
    FileFilterImpl filter = new FileFilterImpl();
    String extensions = MolecularFileFormats.allMolecularExtensions;
    String temp[] = extensions.split(";");
    for (int i = 0; i < temp.length; i++) {
      filter.addExtension(temp[i]);
    }
    filter.setDescription(MolecularFileFormats.allMolecularFormats);
    chooser.setFileFilter(filter);
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setDialogTitle("Open Molecular Modeling File");

    File currentWorkingDirectory = GlobalSettings.getCurrentWorkingDirectory();

    if (currentWorkingDirectory != null) {
      chooser.setCurrentDirectory(currentWorkingDirectory);
    }

    int returnVal = chooser.showOpenDialog(this.getParentFrame());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String fileName = chooser.getSelectedFile().getPath();
      currentWorkingDirectory = chooser.getCurrentDirectory();
      GlobalSettings.setCurrentWorkingDirectory(currentWorkingDirectory);
      logger.info("You chose to open this file: " + fileName);
      String extension = FileFilterImpl.getExtension(fileName);
      int fileFormat = MolecularFileFormats.guessMolecularFileFormat(
          fileName, extension);
      if (fileFormat == MolecularFileFormats.formatUnknown) {
        JOptionPane.showMessageDialog(this.getParentFrame(), "Unknown Molecular Modeling File Format", ERROR_MESSAGE,
            JOptionPane.ERROR_MESSAGE);

        return false;
      }

      j3d.openMolecularModelingFile(fileFormat, fileName);
      return true;
    }
    return false;
  }

  /**
   * Checks whether molecule loaded or not
   *
   * @return boolean "true" if loaded and "false" otherwise
   */
  public boolean isMoleculeLoaded() {
    MoleculeInterface m = java3dUniverse.getMolecule();
    if (m == null) {
      JOptionPane.showMessageDialog(this.getParentFrame(), "Load Molecule first!", "Warning",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }

  public List getVolumeDataList() {
    return volumeDataList;
  }

  public void setVolumeDataList(List volumeDataList) {
    this.volumeDataList = volumeDataList;
  }

  public ManageCubesFrame getManageCubesDialog() {
    return manageCubesDialog;
  }

  public void setManageCubesDialog(ManageCubesFrame manageCubesDialog) {
    this.manageCubesDialog = manageCubesDialog;
  }
}
