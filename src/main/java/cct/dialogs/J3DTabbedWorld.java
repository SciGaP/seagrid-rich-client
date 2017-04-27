package cct.dialogs;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.scijava.java3d.Canvas3D;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cct.interfaces.MoleculeInterface;
import cct.interfaces.MoleculeRendererInterface;
import cct.j3d.Java3dUniverse;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public class J3DTabbedWorld
    extends JTabbedPane implements MoleculeRendererInterface {
  public J3DTabbedWorld() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static final int SINGLE_MOLECULE = 0, MULTIPLE_MOLECULES = 1;
  private static final ImageIcon moleculeImage = new ImageIcon(cct.resources.Resources.class.getResource(
          "cct/images/icons16x16/mol-3.png"));

  private int mode = SINGLE_MOLECULE;
  private Map<Canvas3D, Java3dUniverse> windows = new HashMap<Canvas3D, Java3dUniverse> ();
  private AddMoleculeDialog addMoleculeDialog = null;
  private Java3dUniverse java3dUniverse = null;
  static final Logger logger = Logger.getLogger(J3DTabbedWorld.class.getCanonicalName());

  public J3DTabbedWorld(int mode) {
    super();

    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }

    if (mode != SINGLE_MOLECULE && mode != MULTIPLE_MOLECULES) {
      System.err.println(this.getClass().getCanonicalName() + ": Unknown mode. Set to SINGLE MOLECULE mode");
      this.mode = SINGLE_MOLECULE;
    }
    else {
      this.mode = mode;
    }

    java3dUniverse = new Java3dUniverse();

    if (mode == MULTIPLE_MOLECULES) {
      addTab("No Name", moleculeImage, java3dUniverse.getCanvas3D(), "No Name");
      validate();
      windows.put(java3dUniverse.getCanvas3D(), java3dUniverse);
    }
  }

  @Override
  public void addMolecule(MoleculeInterface molecule) {
    if (addMoleculeDialog == null) {
      Container parent = getParentContainer(this);
      if (parent instanceof Frame) {
        addMoleculeDialog = new AddMoleculeDialog( (Frame) parent, "Select How to Load a New Molecule");
      }
      else if (parent instanceof Dialog) {
        addMoleculeDialog = new AddMoleculeDialog( (Dialog) parent, "Select How to Load a New Molecule");
      }

      addMoleculeDialog.setLocationRelativeTo(parent);
      addMoleculeDialog.EnableNewWindowButton( (mode == MULTIPLE_MOLECULES));
    }

    int cond = MoleculeRendererInterface.LOAD_CANCELED;
    if (hasMolecule()) {
      cond = addMoleculeDialog.showDialog();
    }
    else {
      cond = MoleculeRendererInterface.OVERWRITE_MOLECULE;
    }

    addMolecule(molecule, cond);
  }

  public Component getComponent() {
    if (mode == MULTIPLE_MOLECULES) {
      return this;
    }
    return java3dUniverse.getCanvas3D();
  }

  private Container getParentContainer(Container child) {
    Container parent = null;
    while ( (parent = child.getParent()) != null) {
      if (parent instanceof Frame || parent instanceof Dialog) {
        return parent;
      }
      child = parent;
    }
    return parent;
  }

  @Override
  public void addMolecule(MoleculeInterface molecule, int how_to_add) {

    // --- Multiple molecules
    if (mode == MULTIPLE_MOLECULES) {
      if (how_to_add == OVERWRITE_MOLECULE) {
        Canvas3D canvas3d = (Canvas3D)this.getSelectedComponent();
        Java3dUniverse j3d = windows.get(canvas3d);
        j3d.addMolecule(molecule);
        this.setTitleAt(this.getSelectedIndex(), molecule.getName());
      }
      else if (how_to_add == MERGE_MOLECULE) {
        Canvas3D canvas3d = (Canvas3D)this.getSelectedComponent();
        Java3dUniverse j3d = windows.get(canvas3d);
        j3d.appendMolecule(molecule, true);
        this.setTitleAt(this.getSelectedIndex(), molecule.getName());
      }
      else if (how_to_add == NEW_MOLECULE) {
        Java3dUniverse j3d = new Java3dUniverse();
        this.addTab(molecule.getName(), moleculeImage, j3d.getCanvas3D(), molecule.getName());
        this.validate();
        j3d.addMolecule(molecule);
        windows.put(j3d.getCanvas3D(), j3d);
        this.setSelectedComponent(j3d.getCanvas3D());
      }
    }
    // --- Single molecule in the window
    else {
      if (how_to_add == OVERWRITE_MOLECULE) {
        this.java3dUniverse.addMolecule(molecule);
      }
      else if (how_to_add == MERGE_MOLECULE) {
        java3dUniverse.appendMolecule(molecule, true);
      }
    }
  }

  public boolean hasMolecule() {

    if (mode == SINGLE_MOLECULE) {
        return !(java3dUniverse.getMoleculeInterface() == null || java3dUniverse.getMoleculeInterface().getNumberOfAtoms() < 1);
    }

    // --- Multiple molecules

    if (windows.size() > 1) {
      return true;
    }
    else if (windows.size() == 0) {
      return false;
    }
    Java3dUniverse j3d = windows.get( windows.keySet().iterator().next());
      return j3d.getMoleculeInterface() != null && j3d.getMoleculeInterface().getNumberOfAtoms() > 0;
  }

  public static void main(String[] args) {
    //J3DTabbedWorld j3dtabbedworld = new J3DTabbedWorld();
  }

  public void this_stateChanged(ChangeEvent e) {
    logger.info("this_stateChanged");
  }

  private void jbInit() throws Exception {
    this.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        this_stateChanged(e);
      }
    });
  }
}
