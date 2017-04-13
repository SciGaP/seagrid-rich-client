package cct.interfaces;

import java.awt.Component;

/**
 * <p>Title: Jamberoo + MSMS interface</p>
 *
 * <p>Description: Interfacing MSMS in Jamberoo</p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company: </p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public interface ContractorInterface {
  int SUCCESS = 0, FAILURE = 1;

  void setMolecule(MoleculeInterface molecule);

  int execute() throws Exception;

  void setParentComponent(Component parent);
}
