package cct.interfaces;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2009 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public interface MoleculeSelectorInterface {
  int countMolecules();

  MoleculeInterface getMolecule(int n) throws Exception;

  String getMoleculeDescription(int n) throws Exception;
}
