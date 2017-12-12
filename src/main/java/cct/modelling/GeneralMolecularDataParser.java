/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.modelling;

import cct.GlobalSettings;
import cct.interfaces.MoleculeInterface;
import cct.tools.Utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author vvv900
 */
abstract public class GeneralMolecularDataParser extends AbstractDataParser {

  private static MoleculeInterface refMolecule = null;
  private List<MoleculeInterface> molecules = new ArrayList<MoleculeInterface>();
  static final Logger logger = Logger.getLogger(GeneralMolecularDataParser.class.getCanonicalName());

  public GeneralMolecularDataParser() {
    if (refMolecule == null) {
      String className = null;
      try {
        className = GlobalSettings.getProperty(GlobalSettings.MOLECULE_INTERFACE_CLASS);
      } catch (Exception ex) {
        logger.warning("Cannot get property " + GlobalSettings.MOLECULE_INTERFACE_CLASS);
      }
      try {
        Object obj = Utils.loadClass(className);
        if (obj instanceof MoleculeInterface) {
          refMolecule = (MoleculeInterface) obj;
        } else {
          logger.warning("Class " + className + " is not an instance of MoleculeInterface");
        }
      } catch (Exception ex) {
        logger.warning("Cannot load class " + className);
      }
    }
  }

  abstract public int validFormatScore(BufferedReader in) throws Exception;

  public MoleculeInterface getMolecule() {
    if (molecules.size() == 0) {
      return null;
    }
    return molecules.get(0);
  }

  public List<MoleculeInterface> getMolecules() {
    return molecules;
  }

  public int getNumberMolecules() {
    return molecules.size();
  }

  public void setMoleculeInterface(MoleculeInterface molecule) {
    refMolecule = molecule.getInstance();
  }

  protected MoleculeInterface getMoleculeInterface() {
    return refMolecule.getInstance();
  }

  protected void addMolecule(MoleculeInterface molecule) {
    molecules.add(molecule);
  }

  @Override
  public GeneralMolecularDataParser getParserObject() {
    return this;
  }
}
