/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.modelling;

import cct.interfaces.MoleculeInterface;

/**
 *
 * @author vvv900
 */
public interface MolecularDataParserInterface extends DataParserInterface {

  MoleculeInterface getMolecule();

  MoleculeInterface[] getMolecules();
  
  int getNumberMolecules();
          
  void setMoleculeInterface(MoleculeInterface molecule);
}
