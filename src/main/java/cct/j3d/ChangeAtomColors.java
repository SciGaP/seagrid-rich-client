package cct.j3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.scijava.vecmath.Color3f;

import cct.interfaces.AtomInterface;
import cct.interfaces.ColorChangerInterface;
import cct.interfaces.MoleculeInterface;

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
public class ChangeAtomColors
    implements ColorChangerInterface {

  private Java3dUniverse j3d;
  private List<AtomInterface> selectedAtoms = null;
  private Map<AtomInterface, Color3f> colorStore = null;

  public ChangeAtomColors(Java3dUniverse j3d) {
    this.j3d = j3d;
  }

  public void getSelectedAtoms() {
    if (selectedAtoms == null) {
      selectedAtoms = new ArrayList<AtomInterface> (j3d.getMoleculeInterface().getNumberOfAtoms());
      colorStore = new HashMap<AtomInterface, Color3f> (j3d.getMoleculeInterface().getNumberOfAtoms());
    }
    else {
      selectedAtoms.clear();
      colorStore.clear();
    }

    MoleculeInterface molec = j3d.getMoleculeInterface();
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      if (atom.isSelected()) {
        selectedAtoms.add(atom);
        Object obj = atom.getProperty(Java3dUniverse.ATOM_NODE);
        if (obj == null) {
          System.err.println(this.getClass().getCanonicalName() + ": getSelectedAtoms: atom does not contain AtomNode. Ignored...");
          continue;
        }
        Color3f color3f = AtomNode.getAtomColor(atom);
        colorStore.put(atom, color3f);
      }
    }
  }

  @Override
  public void setColor(Color newColor) {
    if (selectedAtoms == null) {
      getSelectedAtoms();
    }
    if (selectedAtoms.size() < 1) {
      return;
    }
    for (int i = 0; i < selectedAtoms.size(); i++) {
      AtomInterface atom = selectedAtoms.get(i);
      j3d.setAtomColor(atom, newColor);
    }
  }

  @Override
  public void reset() {
    Iterator iter = colorStore.keySet().iterator();
    while (iter.hasNext()) {
      AtomInterface atom = (AtomInterface) iter.next();
      Color3f color3f = colorStore.get(atom);
      j3d.setAtomColor(atom, color3f);
    }
  }

  @Override
  public void stop() {
    j3d.endProcessingSelectedAtoms();
  }

  public static void main(String[] args) {
    //ChangeAtomColors changeatomcolors = new ChangeAtomColors();
  }
}
