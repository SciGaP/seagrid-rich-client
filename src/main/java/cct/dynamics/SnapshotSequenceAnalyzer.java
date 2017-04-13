/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.dynamics;

import cct.interfaces.MoleculeInterface;
import cct.modelling.StructureManager;

/**
 *
 * @author vvv900
 */
public class SnapshotSequenceAnalyzer {

  private StructureManager structureManager;
  private MoleculeInterface referenceStructure;

  public SnapshotSequenceAnalyzer() {

  }

  public SnapshotSequenceAnalyzer(StructureManager structureManager) {
    this.structureManager = structureManager;
  }

  public StructureManager getStructureManager() {
    return structureManager;
  }

  public void setStructureManager(StructureManager structureManager) {
    this.structureManager = structureManager;
  }

  public void setReferenceMolecule(MoleculeInterface mol) {
    referenceStructure = mol;
    if (structureManager != null) {
      structureManager.setReferenceMolecule(mol);
    }
  }

  public MoleculeInterface getReferenceMolecule() {
    return referenceStructure;
  }
}
