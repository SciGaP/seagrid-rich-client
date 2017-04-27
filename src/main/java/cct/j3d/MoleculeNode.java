/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.j3d;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scijava.java3d.BranchGroup;

/**
 *
 * @author Vlad
 */
public class MoleculeNode extends BranchGroup {

  static final Logger logger = Logger.getLogger(MoleculeNode.class.getCanonicalName());
  private Level WARNING_LEVEL = Level.WARNING;
  private Set<AtomNode> atoms = new HashSet<AtomNode>();
  private Set<BondNode> bonds = new HashSet<BondNode>();

  public MoleculeNode() {
    super();

    setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    setCapability(BranchGroup.ALLOW_PICKABLE_READ);
    setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    setCapability(BranchGroup.ALLOW_DETACH);
    setCapability(BranchGroup.ALLOW_PICKABLE_READ);
    setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
    setCapability(BranchGroup.ALLOW_LOCAL_TO_VWORLD_READ);

    setPickable(true);
  }

  public void addChild(AtomNode atom) {
    if (atoms.contains(atom)) {
      if (logger.isLoggable(WARNING_LEVEL)) {
        logger.warning("Attempt to add already attached atom node. Ignored. Continuing...");
      }
      return;
    }
    atoms.add(atom);
    super.addChild(atom);
  }

  public void removeChild(AtomNode atom) {
    atoms.remove(atom);
    super.removeChild(atom);
  }

  public void removeChild(BondNode bond) {
    bonds.remove(bond);
    super.removeChild(bond);
  }

  public void addChild(BondNode bond) {
    if (bonds.contains(bond)) {
      if (logger.isLoggable(WARNING_LEVEL)) {
        logger.warning("Attempt to add already attached bond node. Ignored. Continuing...");
      }
      return;
    }
    bonds.add(bond);
    super.addChild(bond);
  }

  @Override
  public void removeAllChildren() {
    atoms.clear();
    bonds.clear();
    super.removeAllChildren();
  }

  public boolean contains(AtomNode atom) {
    return atoms.contains(atom);
  }

  public boolean contains(BondNode bond) {
    return bonds.contains(bond);
  }
}
