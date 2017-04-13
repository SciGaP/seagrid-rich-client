package cct.interfaces;

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
public interface MoleculeRendererInterface {
  int LOAD_CANCELED = 0, OVERWRITE_MOLECULE = 1, MERGE_MOLECULE = 2, NEW_MOLECULE = 3;

  void addMolecule(MoleculeInterface molecule);

  void addMolecule(MoleculeInterface molecule, int how_to_load_molecule);
}
