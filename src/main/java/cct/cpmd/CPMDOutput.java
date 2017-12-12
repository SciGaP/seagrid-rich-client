package cct.cpmd;

import java.io.BufferedReader;
import java.util.StringTokenizer;

import cct.GlobalConstants;
import cct.interfaces.AtomInterface;
import cct.interfaces.CoordinateParserInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;

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
public class CPMDOutput
    implements GlobalConstants, CoordinateParserInterface {

  //private ArrayList<CPMDAtom> atoms = new ArrayList<CPMDAtom> ();

  public CPMDOutput() {
  }

  @Override
  public void parseCoordinates(BufferedReader in, MoleculeInterface molecule) throws Exception {

    try {
      // --- Reading atoms
      String line;
      molecule.addMonomer("CPMD");
      while ( (line = in.readLine()) != null) {
        line = line.trim();
        if (line.length() < 1) {
          continue; // Empty line
        }

        CPMDAtom atom = CPMD.readAtom(line, ONE_BOHR);
        AtomInterface at = molecule.getNewAtomInstance();
        at.setName(atom.name);
        at.setAtomicNumber(ChemicalElements.getAtomicNumber(atom.name));
        at.setXYZ((float)atom.x,(float)atom.y,(float)atom.z);
        molecule.addAtom(at);
      }

    }
    catch (Exception ex) {
      throw ex;
    }

    //Molecule.guessCovalentBonds(molecule);
    //Molecule.guessAtomTypes(molecule, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
  }

  @Override
  public double evaluateCompliance(BufferedReader in) throws Exception {
    double score = 0;
    try {
      // --- Reading atoms
      String line;
      while ( (line = in.readLine()) != null) {

        line = line.trim();
        if (line.length() < 1) {
          continue; // Empty line
        }

        score = 1.0;
        // For ATOMS section
        //    1      C      16.586049      16.342961      20.770901       3
        // For  ATOMIC COORDINATES
        //        1       C           6.219888       7.668818       9.317632

        StringTokenizer st = new StringTokenizer(line, " \t", false);
        if (st.countTokens() < 5) {
          return 0;
        }
        else if (st.countTokens() > 6) {
          score -= 0.25;
        }

        // --- The first token should be integer number

        try {
          Integer.parseInt(st.nextToken());
        }
        catch (Exception ex) {
          score -= 0.25;
        }

        // --- Element symbol. Skipping...

        String token = st.nextToken();

        // --- Getting x,y,z

        try {
          Float.parseFloat(st.nextToken());
          Float.parseFloat(st.nextToken());
          Float.parseFloat(st.nextToken());
        }
        catch (Exception ex) {
          return 0;
        }

        return score; // i.e. we check only the first line
      }

    }
    catch (Exception ex) {
      throw ex;
    }
    return score;

  }

}
