/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.modelling;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.MonomerInterface;
import cct.tools.ui.JobProgressInterface;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vlad
 */
public class AtomSelection {

  static final Logger logger = Logger.getLogger(AtomSelection.class.getCanonicalName());

  static public void applySelectionMask(MoleculeInterface molecule, boolean selectedAtoms[], SELECTION_TYPE selectionType,
          SELECTION_RULE selectionRule, JobProgressInterface progress) throws Exception {

    // --- Error check
    if (molecule == null) {
      throw new Exception("INTERNAL ERROR: makeSelection: molecule == null");
    }

    int natoms = molecule.getNumberOfAtoms();

    if (natoms != selectedAtoms.length) {
      throw new Exception("INTERNAL ERROR: makeSelection: molecule.getNumberOfAtoms() != selectedAtoms.length");
    }

    // --- Make selection
    switch (selectionRule) {
      case UNION:
        for (int i = 0; i < natoms; i++) {
          if (!selectedAtoms[i]) {
            continue;
          }
          AtomInterface atom = molecule.getAtomInterface(i);
          atom.setSelected(true);
        }
        break;
      //*****************************************************
      case DIFFERENCE:
        for (int i = 0; i < natoms; i++) {
          if (!selectedAtoms[i]) {
            continue;
          }
          AtomInterface atom = molecule.getAtomInterface(i);

          if (atom.isSelected()) {
            atom.setSelected(false);
          }
        }
        break;
      //**********************************************************
      case INTERSECTION:
        for (int i = 0; i < natoms; i++) {
          AtomInterface atom = molecule.getAtomInterface(i);
          if (selectedAtoms[i] && atom.isSelected()) {
            continue;
          }
          atom.setSelected(false);
        }
        break;
    }
  }

  static public boolean[] selectAtomsWithinSphere(MoleculeInterface molecule, double radius, SELECTION_TYPE selectionType,
          JobProgressInterface progress) throws Exception {

    int natoms = molecule.getNumberOfAtoms();
    AtomInterface atom;
    StringBuilder sb = null;
    if (logger.isLoggable(Level.INFO) && selectionType == SELECTION_TYPE.MONOMERS) {
      sb = new StringBuilder();
    }

    boolean selectedAtoms[] = new boolean[natoms];
    int nsel = 0;
    for (int i = 0; i < selectedAtoms.length; i++) {
      selectedAtoms[i] = molecule.getAtomInterface(i).isSelected();
      ++nsel;
    }

    if (nsel < 1) {
      return selectedAtoms;
    }

    switch (selectionType) {
      case ATOMS:

        for (int i = 0; i < natoms; i++) {
          AtomInterface at = molecule.getAtomInterface(i);
          if (!at.isSelected()) {
            continue;
          }

          for (int j = 0; j < natoms; j++) {
            atom = molecule.getAtomInterface(j);
            if (atom.isSelected()) {
              continue;
            }
            if (atom.distanceTo(at) <= radius) {
              selectedAtoms[j] = true;
            }
          }
        }

        break;
      //#######################################################################
      case MONOMERS:

        int nmonomers = molecule.getNumberOfMonomers();
        boolean[] alreadySelected = null;
        if (nmonomers > 0) {
          alreadySelected = new boolean[nmonomers];
        }

        for (int i = 0; i < natoms; i++) {
          AtomInterface at = molecule.getAtomInterface(i);
          if (!at.isSelected()) {
            continue;
          }

          for (int j = 0; j < molecule.getNumberOfMonomers(); j++) {
            if (alreadySelected[j]) {
              continue;
            }

            MonomerInterface mono = molecule.getMonomerInterface(j);
            for (int k = 0; k < mono.getNumberOfAtoms(); k++) {
              atom = mono.getAtom(k);
              if (atom.distanceTo(at) <= radius) {
                alreadySelected[j] = true;
                if (logger.isLoggable(Level.INFO) && selectionType == SELECTION_TYPE.MONOMERS) {
                  sb.append(mono.getName() + String.valueOf(j + 1) + "\n");
                }
                for (int kk = 0; kk < mono.getNumberOfAtoms(); kk++) {
                  atom = mono.getAtom(kk);
                  int n = molecule.getAtomIndex(atom);
                  selectedAtoms[n] = true;
                }
                break;
              }
            }
          }
        }

        if (logger.isLoggable(Level.INFO) && selectionType == SELECTION_TYPE.MONOMERS) {
          logger.log(Level.INFO, "Selected monomers:\n" + sb.toString());
        }
        break;
      //#######################################################################
      case MOLECULES:

        int nmols;
        List<List<AtomInterface>> mols = Molecule.getMolecules(molecule);
        if (mols == null || (nmols = mols.size()) < 1) {
          break;
        }
        alreadySelected = null;
        if (nmols > 0) {
          alreadySelected = new boolean[nmols];
        }

        for (int i = 0; i < natoms; i++) {
          AtomInterface at = molecule.getAtomInterface(i);
          if (!at.isSelected()) {
            continue;
          }

          for (int j = 0; j < nmols; j++) {
            if (alreadySelected[j]) {
              continue;
            }

            List<AtomInterface> mol = mols.get(j);

            for (int k = 0; k < mol.size(); k++) {
              atom = mol.get(k);
              if (atom.distanceTo(at) <= radius) {
                alreadySelected[j] = true;
                for (int kk = 0; kk < mol.size(); kk++) {
                  atom = mol.get(kk);
                  int n = molecule.getAtomIndex(atom);
                  selectedAtoms[n] = true;
                }
                break;
              }
            }
          }
        }
        break;
      default:
        throw new Exception("Selection type " + selectionType.name() + " is not implemented");
    }

    return selectedAtoms;
  }
}
