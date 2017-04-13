/* ***** BEGIN LICENSE BLOCK *****
Version: Apache 2.0/GPL 3.0/LGPL 3.0

CCT - Computational Chemistry Tools
Jamberoo - Java Molecules Editor

Copyright 2008-2015 Dr. Vladislav Vasilyev

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributor(s):
Dr. Vladislav Vasilyev <vvv900@gmail.com>       (original author)

Alternatively, the contents of this file may be used under the terms of
either the GNU General Public License Version 2 or later (the "GPL"), or
the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
in which case the provisions of the GPL or the LGPL are applicable instead
of those above. If you wish to allow use of your version of this file only
under the terms of either the GPL or the LGPL, and not to allow others to
use your version of this file under the terms of the Apache 2.0, indicate your
decision by deleting the provisions above and replace them with the notice
and other provisions required by the GPL or the LGPL. If you do not delete
the provisions above, a recipient may use your version of this file under
the terms of any one of the Apache 2.0, the GPL or the LGPL.

 ***** END LICENSE BLOCK *****/
package cct.adf;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.math.MathExpressionParser;
import cct.modelling.CCTAtomTypes;
import cct.modelling.ChemicalElements;
import cct.modelling.GeneralMolecularDataParser;
import cct.modelling.Molecule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Computational Chemistry Tookit
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev
 * </p>
 *
 * <p>
 * Company: ANU
 * </p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
// KEY, , ,
//
enum KEY_WORDS {

  A1FIT, ADDDIFFUSEFIT, ALLOW, ALLPOINTS, ANALYTICALFREQ, ATOMS, BASIS, BONDORDER, CHARGE, COLLINEAR, COMMENT, COMMTIMING,
  CONSTRAINT, COREPOTENTIALS, CREATE, CURRENTRESPONSE, DEBUG, DEFINE, DENSPREP, DEPENDENCY, DIPOLEMAT, DISK, DRF, EFIELD,
  ENERGYFRAG, EPRINT, EPSFIT, ESR, EXACTDENSITY, EXTERNALS, FDE, FILE, FITELSTAT, FORCEALDA, FRAGMENTS, FRAGOCCUPATIONS,
  FULLFOCK, EXCITATIONS, FULLSCF, GEOSTEP, HARTREEFOCK, HESSTEST, HFEXCHANGE, INLINE, IRCSTART, LOCORB, METAGGA, MMDISPERSION,
  EXTENDEDPOPAN, MODIFYEXCITATION, NONCOLLINEAR, NOPRINT, NOSAVE, OLDGRADIENTS, PRINT, QTENS, RADIALCOREGRID,
  REMOVEFRAGORBITALS, GEOMETRY, GEOVAR, RELATIVISTIC, RESPONSE, RESTRAINT, SAVE, SFTDDFT, SICOEP, SINGULARFIT, SKIP, SOLVATION,
  STCONTRIB, STOPAFTER, SYMMETRY, TAILS, TDA, THERMO, UNRESTRICTED, VECTORLENGTH, VIBRON, SCF, TITLE, UNITS, XC, QMMM,
  INTEGRATION, HESSDIAG, LINEARSCALING, MODIFYSTARTPOTENTIAL, OCCUPATIONS, RESTART, SLATERDETERMINANTS
}

public class ADF extends GeneralMolecularDataParser {

  static final double ONE_BOHR = 0.529177249; // In Angstrom
  static final float DEGREES_TO_RADIANS = (float) (Math.PI / 180.0);
  static final String FF_LABEL_PROP = "Adf FF Label";
  static final String MM_TYPE_PROP = "Adf MM Type";
  static Map<String, KEY_WORDS> keyWords = new HashMap<String, KEY_WORDS>();

  static {

    keyWords.put("A1FIT", KEY_WORDS.A1FIT);
    keyWords.put("ADDDIFFUSEFIT", KEY_WORDS.ADDDIFFUSEFIT);
    keyWords.put("ALLOW", KEY_WORDS.ALLOW);
    keyWords.put("ALLPOINTS", KEY_WORDS.ALLPOINTS);
    keyWords.put("ANALYTICALFREQ", KEY_WORDS.ANALYTICALFREQ);
    keyWords.put("ATOMS", KEY_WORDS.ATOMS);
    keyWords.put("BONDORDER", KEY_WORDS.BONDORDER);
    keyWords.put("CHARGE", KEY_WORDS.CHARGE);
    keyWords.put("COMMENT", KEY_WORDS.COMMENT);
    keyWords.put("COMMTIMING", KEY_WORDS.COMMTIMING);
    keyWords.put("COLLINEAR", KEY_WORDS.COLLINEAR);
    keyWords.put("CONSTRAINT", KEY_WORDS.CONSTRAINT);
    keyWords.put("COREPOTENTIALS", KEY_WORDS.COREPOTENTIALS);
    keyWords.put("CREATE", KEY_WORDS.CREATE);
    keyWords.put("CURRENTRESPONSE", KEY_WORDS.CURRENTRESPONSE);
    keyWords.put("DEBUG", KEY_WORDS.DEBUG);
    keyWords.put("DEFINE", KEY_WORDS.DEFINE);
    keyWords.put("DENSPREP", KEY_WORDS.DENSPREP);
    keyWords.put("DEPENDENCY", KEY_WORDS.DEPENDENCY);
    keyWords.put("DIPOLEMAT", KEY_WORDS.DIPOLEMAT);
    keyWords.put("DISK", KEY_WORDS.DISK);
    keyWords.put("DRF", KEY_WORDS.DRF);
    keyWords.put("EFIELD", KEY_WORDS.EFIELD);
    keyWords.put("EXTENDEDPOPAN", KEY_WORDS.EXTENDEDPOPAN);
    keyWords.put("ENERGYFRAG", KEY_WORDS.ENERGYFRAG);
    keyWords.put("EPRINT", KEY_WORDS.EPRINT);
    keyWords.put("EPSFIT", KEY_WORDS.EPSFIT);
    keyWords.put("ESR", KEY_WORDS.ESR);
    keyWords.put("EXACTDENSITY", KEY_WORDS.EXACTDENSITY);
    keyWords.put("EXCITATIONS", KEY_WORDS.EXCITATIONS);
    keyWords.put("EXTERNALS", KEY_WORDS.EXTERNALS);
    keyWords.put("FDE", KEY_WORDS.FDE);
    keyWords.put("FILE", KEY_WORDS.FILE);
    keyWords.put("FITELSTAT", KEY_WORDS.FITELSTAT);
    keyWords.put("FORCEALDA", KEY_WORDS.FORCEALDA);
    keyWords.put("FRAGMENTS", KEY_WORDS.FRAGMENTS);
    keyWords.put("FRAGOCCUPATIONS", KEY_WORDS.FRAGOCCUPATIONS);
    keyWords.put("FULLFOCK", KEY_WORDS.FULLFOCK);
    keyWords.put("FULLSCF", KEY_WORDS.FULLSCF);
    keyWords.put("GEOMETRY", KEY_WORDS.GEOMETRY);
    keyWords.put("GEOSTEP", KEY_WORDS.GEOSTEP);
    keyWords.put("GEOVAR", KEY_WORDS.GEOVAR);
    keyWords.put("HARTREEFOCK", KEY_WORDS.HARTREEFOCK);
    keyWords.put("HESSTEST", KEY_WORDS.HESSTEST);
    keyWords.put("HFEXCHANGE", KEY_WORDS.HFEXCHANGE);
    keyWords.put("INLINE", KEY_WORDS.INLINE);
    keyWords.put("IRCSTART", KEY_WORDS.IRCSTART);
    keyWords.put("BASIS", KEY_WORDS.BASIS);
    keyWords.put("LOCORB", KEY_WORDS.LOCORB);
    keyWords.put("METAGGA", KEY_WORDS.METAGGA);
    keyWords.put("MMDISPERSION", KEY_WORDS.MMDISPERSION);
    keyWords.put("MODIFYEXCITATION", KEY_WORDS.MODIFYEXCITATION);
    keyWords.put("NONCOLLINEAR", KEY_WORDS.NONCOLLINEAR);
    keyWords.put("NOPRINT", KEY_WORDS.NOPRINT);
    keyWords.put("NOSAVE", KEY_WORDS.NOSAVE);
    keyWords.put("OLDGRADIENTS", KEY_WORDS.OLDGRADIENTS);
    keyWords.put("PRINT", KEY_WORDS.PRINT);
    keyWords.put("QTENS", KEY_WORDS.QTENS);
    keyWords.put("RADIALCOREGRID", KEY_WORDS.RADIALCOREGRID);
    keyWords.put("REMOVEFRAGORBITALS", KEY_WORDS.REMOVEFRAGORBITALS);
    keyWords.put("RELATIVISTIC", KEY_WORDS.RELATIVISTIC);
    keyWords.put("RESPONSE", KEY_WORDS.RESPONSE);
    keyWords.put("RESTRAINT", KEY_WORDS.RESTRAINT);
    keyWords.put("SAVE", KEY_WORDS.SAVE);
    keyWords.put("SFTDDFT", KEY_WORDS.SFTDDFT);
    keyWords.put("SCF", KEY_WORDS.SCF);
    keyWords.put("SICOEP", KEY_WORDS.SICOEP);
    keyWords.put("SINGULARFIT", KEY_WORDS.SINGULARFIT);
    keyWords.put("SKIP", KEY_WORDS.SKIP);
    keyWords.put("SOLVATION", KEY_WORDS.SOLVATION);
    keyWords.put("STCONTRIB", KEY_WORDS.STCONTRIB);
    keyWords.put("STOPAFTER", KEY_WORDS.STOPAFTER);
    keyWords.put("SYMMETRY", KEY_WORDS.SYMMETRY);
    keyWords.put("TAILS", KEY_WORDS.TAILS);
    keyWords.put("TDA", KEY_WORDS.TDA);
    keyWords.put("THERMO", KEY_WORDS.THERMO);
    keyWords.put("TITLE", KEY_WORDS.TITLE);
    keyWords.put("UNITS", KEY_WORDS.UNITS);
    keyWords.put("UNRESTRICTED", KEY_WORDS.UNRESTRICTED);
    keyWords.put("VECTORLENGTH", KEY_WORDS.VECTORLENGTH);
    keyWords.put("VIBRON", KEY_WORDS.VIBRON);
    keyWords.put("XC", KEY_WORDS.XC);
    keyWords.put("QMMM", KEY_WORDS.QMMM);
    keyWords.put("INTEGRATION", KEY_WORDS.INTEGRATION);
    keyWords.put("HESSDIAG", KEY_WORDS.HESSDIAG);
    keyWords.put("LINEARSCALING", KEY_WORDS.LINEARSCALING);
    keyWords.put("MODIFYSTARTPOTENTIAL", KEY_WORDS.MODIFYSTARTPOTENTIAL);
    keyWords.put("OCCUPATIONS", KEY_WORDS.OCCUPATIONS);
    keyWords.put("RESTART", KEY_WORDS.RESTART);
    keyWords.put("SLATERDETERMINANTS", KEY_WORDS.SLATERDETERMINANTS);
  }
  private float distanceUnits = 1.0f;
  private float angleUnits = DEGREES_TO_RADIANS;
  private boolean symbolicParamResolved = true;
  private boolean includesFrozenAtoms = false;
  private boolean defaultChargeUsed = true;
  private boolean internalCoordinates = false;
  private int totalCharge = 0;
  private List<ADFAtom> Atoms = new ArrayList<ADFAtom>();
  private ArrayList<int[]> connectionTable = new ArrayList<int[]>();
  private Map<String, Float> symbolicStrings = new HashMap<String, Float>(300);
  private Set<String> frozenParameters = new HashSet<String>();
  private MathExpressionParser mathExpressionParser = new MathExpressionParser();
  static final Logger logger = Logger.getLogger(ADF.class.getCanonicalName());

  public ADF() {
    mathExpressionParser.setInDegrees(true);
  }

  public static void main(String[] args) {
    ADF adf = new ADF();
  }

  public void getMolecularInterface(MoleculeInterface molec) throws Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() + " : molec == null");
    }
    if (Atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : no ADF atoms");
    }

    // molec.addProperty(MoleculeInterface.ChargeProperty, new
    // Integer(netCharge));
    // molec.addProperty(MoleculeInterface.MultiplicityProperty, new
    // Integer(spinMultiplicity));
    molec.addMonomer("ADF");

    for (int i = 0; i < Atoms.size(); i++) {
      ADFAtom ga = Atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.name);
      atom.setAtomicNumber(ga.atomicNumber);
      atom.setXYZ(ga.xyz[0], ga.xyz[1], ga.xyz[2]);

      if (ga.FF_Label != null) {
        atom.setProperty(FF_LABEL_PROP, ga.FF_Label);
      }

      if (ga.MM_Type != null) {
        atom.setProperty(MM_TYPE_PROP, ga.MM_Type);
      }

      molec.addAtom(atom);
    }

  }

  public int validFormatScore(BufferedReader in) throws Exception {
    //in.mark(65536);
    String line = "";
    int score = 0;
    ADF adf = new ADF();
    try {
      adf.parseData(in);
    } catch (Exception ex) {
      return 0;
    }
    if (adf.getNumberMolecules() == 0) {
      return 0;
    }
    
    return 10;
  }

  public void parseData(BufferedReader in) throws Exception {
    MoleculeInterface mol = getMoleculeInterface();
    this.addMolecule(mol);

    String line, token;
    Atoms.clear();

    /*
     * Input for ADF is structured by keywords, in short: keys. A key is a
     * string of characters that does not contain a delimiter (blank, comma
     * or equal sign). Keys are not case sensitive. Input is read until
     * either the end-of-file condition (eof) becomes true, or until a
     * record end input is encountered, whichever comes first. (end input is
     * not a key.)
     *
     * Key-controlled input occurs with two formats. In the first you have
     * only one record, which contains both the key and - depending on the
     * case - associated data: the key argument:
     *
     * KEY argument
     *
     * The whole part of the line that follows after the key is the
     * argument. It may consist of more than one item.
     *
     * The alternative format is a sequence of records, collectively denoted
     * as a key block. The first record of the block gives the key (which
     * may have an argument). The block is closed by a record containing
     * (only) the word end. The other records in the block constitute the
     * data block, and provide information related to the key.
     *
     * KEY {argument} data record data record ...(etc.)... ... end
     *
     * Block type keys may have subkeys in their data block. The subkeys may
     * themselves also be block type keys. The data blocks of block type
     * subkeys, however, do not end with end, but with subend:
     *
     * KEY {argument} data data subkey {argument} subkey data subkey data
     * ... subend data data ... end
     *
     * Layout features such as an open line, indentation, or the number of
     * spaces between items are not significant.
     *
     */
    try {

      while ((line = in.readLine()) != null) {
        line = line.trim();

        if (line.length() < 1) { // Blank line
          continue;
        }

        StringTokenizer st = new StringTokenizer(line.toUpperCase(), " =,");
        token = st.nextToken();

        if (token.equals("ENDINPUT") || (token.equals("END") && st.nextToken().equals("INPUT"))) {
          break;
        }

        if (!keyWords.containsKey(token)) {
          continue;
        }

        KEY_WORDS keyWord = keyWords.get(token);

        switch (keyWord) {
          case UNITS:
            parseUNITS(in);
            break;
          case ATOMS:
            parseATOMS(line, in);
            break;
          case GEOVAR:
            parseGEOVAR(in);
            break;
          case CHARGE:
            if (st.hasMoreTokens()) {
              parseCHARGE(st.nextToken());
            }
            break;
          case DEFINE:
            parseDEFINE(in);
            break;

          case LINEARSCALING:
          case INTEGRATION:
          case MODIFYSTARTPOTENTIAL:
          case SLATERDETERMINANTS:
            if (st.hasMoreTokens()) {
              continue;
            } else {
              skipSection(in);
            }
            break;

          case HESSDIAG:
          case OCCUPATIONS:
          case RESTART:
            if (!st.hasMoreTokens()) {
              skipSection(in);
            } else if (line.endsWith(" &")) {
              skipSection(in);
            } else {
              continue;
            }

            break;

          case QMMM:
            this.parseQMMM(in);
            break;

          case ANALYTICALFREQ:
          case BASIS:
          case COMMENT:
          case CONSTRAINT:
          case COREPOTENTIALS:
          case CURRENTRESPONSE:
          case DRF:
          case EFIELD:
          case ENERGYFRAG:
          case EPRINT:
          case ESR:
          case EXCITATIONS:
          case EXTERNALS:
          case FDE:
          case FRAGMENTS:
          case FRAGOCCUPATIONS:
          case GEOMETRY:
          case IRCSTART:
          case LOCORB:
          case MMDISPERSION:
          case MODIFYEXCITATION:
          case REMOVEFRAGORBITALS:
          case RESPONSE:
          case RESTRAINT:
          case SCF:
          case SICOEP:
          case SOLVATION:
          case VIBRON:
          case XC:
            skipSection(in);
            break;

          case A1FIT:
          case ADDDIFFUSEFIT:
          case ALLOW:
          case ALLPOINTS:
          case BONDORDER:
          case COLLINEAR:
          case COMMTIMING:
          case CREATE:
          case DEBUG:
          case DENSPREP:
          case DEPENDENCY:
          case DIPOLEMAT:
          case DISK:
          case EPSFIT:
          case EXACTDENSITY:
          case EXTENDEDPOPAN:
          case FILE:
          case FITELSTAT:
          case FORCEALDA:
          case FULLFOCK:
          case FULLSCF:
          case GEOSTEP:
          case HARTREEFOCK:
          case HESSTEST:
          case HFEXCHANGE:
          case INLINE:
          case METAGGA:
          case NONCOLLINEAR:
          case NOPRINT:
          case NOSAVE:
          case OLDGRADIENTS:
          case PRINT:
          case QTENS:
          case RADIALCOREGRID:
          case RELATIVISTIC:
          case SAVE:
          case SFTDDFT:
          case SINGULARFIT:
          case SKIP:
          case STCONTRIB:
          case STOPAFTER:
          case SYMMETRY:
          case TAILS:
          case TDA:
          case THERMO:
          case TITLE:
          case UNRESTRICTED:
          case VECTORLENGTH:
            break;
          default:

        }

      }

      in.close();
    } catch (Exception ex) {
      if (Atoms.size() < 1) {
        throw new Exception("Error while reading ADF input file: " + ex.getMessage());
      }
    }

    // --- Check out whether all symbolic parameters are resolved
    if (!symbolicParamResolved) {
      // --- Resolve parameters
      resolveSymbolicParamaters();

    }

    if (internalCoordinates) {
      fromZMatrixToCartesians(Atoms);
    }

    getMolecularInterface(mol);
    Molecule.guessCovalentBonds(mol);
    Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
  }

  /**
   *
   * @param file_name
   * @param fileType
   * @throws Exception
   * @deprecated Use parseData methods instead
   */
  @Deprecated
  public void parseInput(String file_name, int fileType) throws Exception {
    String line, token;
    BufferedReader in = null;
    Atoms.clear();

    if (fileType == 0) { // Read from file
      try {
        in = new BufferedReader(new FileReader(file_name));
      } catch (Exception ex) {
        throw ex;
      }
    } else if (fileType == 1) { // Read from String
      in = new BufferedReader(new StringReader(file_name));
    } else {
      throw new Exception("parsing ADF Input: INTERNAL ERROR: Unknown file type");
    }

    /*
     * Input for ADF is structured by keywords, in short: keys. A key is a
     * string of characters that does not contain a delimiter (blank, comma
     * or equal sign). Keys are not case sensitive. Input is read until
     * either the end-of-file condition (eof) becomes true, or until a
     * record end input is encountered, whichever comes first. (end input is
     * not a key.)
     *
     * Key-controlled input occurs with two formats. In the first you have
     * only one record, which contains both the key and - depending on the
     * case - associated data: the key argument:
     *
     * KEY argument
     *
     * The whole part of the line that follows after the key is the
     * argument. It may consist of more than one item.
     *
     * The alternative format is a sequence of records, collectively denoted
     * as a key block. The first record of the block gives the key (which
     * may have an argument). The block is closed by a record containing
     * (only) the word end. The other records in the block constitute the
     * data block, and provide information related to the key.
     *
     * KEY {argument} data record data record ...(etc.)... ... end
     *
     * Block type keys may have subkeys in their data block. The subkeys may
     * themselves also be block type keys. The data blocks of block type
     * subkeys, however, do not end with end, but with subend:
     *
     * KEY {argument} data data subkey {argument} subkey data subkey data
     * ... subend data data ... end
     *
     * Layout features such as an open line, indentation, or the number of
     * spaces between items are not significant.
     *
     */
    try {

      while ((line = in.readLine()) != null) {
        line = line.trim();

        if (line.length() < 1) { // Blank line
          continue;
        }

        StringTokenizer st = new StringTokenizer(line.toUpperCase(), " =,");
        token = st.nextToken();

        if (token.equals("ENDINPUT") || (token.equals("END") && st.nextToken().equals("INPUT"))) {
          break;
        }

        if (!keyWords.containsKey(token)) {
          continue;
        }

        KEY_WORDS keyWord = keyWords.get(token);

        switch (keyWord) {
          case UNITS:
            parseUNITS(in);
            break;
          case ATOMS:
            parseATOMS(line, in);
            break;
          case GEOVAR:
            parseGEOVAR(in);
            break;
          case CHARGE:
            if (st.hasMoreTokens()) {
              parseCHARGE(st.nextToken());
            }
            break;
          case DEFINE:
            parseDEFINE(in);
            break;

          case LINEARSCALING:
          case INTEGRATION:
          case MODIFYSTARTPOTENTIAL:
          case SLATERDETERMINANTS:
            if (st.hasMoreTokens()) {
              continue;
            } else {
              skipSection(in);
            }
            break;

          case HESSDIAG:
          case OCCUPATIONS:
          case RESTART:
            if (!st.hasMoreTokens()) {
              skipSection(in);
            } else if (line.endsWith(" &")) {
              skipSection(in);
            } else {
              continue;
            }

            break;

          case QMMM:
            this.parseQMMM(in);
            break;

          case ANALYTICALFREQ:
          case BASIS:
          case COMMENT:
          case CONSTRAINT:
          case COREPOTENTIALS:
          case CURRENTRESPONSE:
          case DRF:
          case EFIELD:
          case ENERGYFRAG:
          case EPRINT:
          case ESR:
          case EXCITATIONS:
          case EXTERNALS:
          case FDE:
          case FRAGMENTS:
          case FRAGOCCUPATIONS:
          case GEOMETRY:
          case IRCSTART:
          case LOCORB:
          case MMDISPERSION:
          case MODIFYEXCITATION:
          case REMOVEFRAGORBITALS:
          case RESPONSE:
          case RESTRAINT:
          case SCF:
          case SICOEP:
          case SOLVATION:
          case VIBRON:
          case XC:
            skipSection(in);
            break;

          case A1FIT:
          case ADDDIFFUSEFIT:
          case ALLOW:
          case ALLPOINTS:
          case BONDORDER:
          case COLLINEAR:
          case COMMTIMING:
          case CREATE:
          case DEBUG:
          case DENSPREP:
          case DEPENDENCY:
          case DIPOLEMAT:
          case DISK:
          case EPSFIT:
          case EXACTDENSITY:
          case EXTENDEDPOPAN:
          case FILE:
          case FITELSTAT:
          case FORCEALDA:
          case FULLFOCK:
          case FULLSCF:
          case GEOSTEP:
          case HARTREEFOCK:
          case HESSTEST:
          case HFEXCHANGE:
          case INLINE:
          case METAGGA:
          case NONCOLLINEAR:
          case NOPRINT:
          case NOSAVE:
          case OLDGRADIENTS:
          case PRINT:
          case QTENS:
          case RADIALCOREGRID:
          case RELATIVISTIC:
          case SAVE:
          case SFTDDFT:
          case SINGULARFIT:
          case SKIP:
          case STCONTRIB:
          case STOPAFTER:
          case SYMMETRY:
          case TAILS:
          case TDA:
          case THERMO:
          case TITLE:
          case UNRESTRICTED:
          case VECTORLENGTH:
            break;
          default:

        }

      }

      in.close();
    } catch (Exception ex) {
      if (Atoms.size() < 1) {
        throw new Exception("Error while reading ADF input file: " + ex.getMessage());
      }
    }

    // --- Check out whether all symbolic parameters are resolved
    if (!symbolicParamResolved) {
      // --- Resolve parameters
      resolveSymbolicParamaters();

    }

    if (internalCoordinates) {
      fromZMatrixToCartesians(Atoms);
    }
  }

  public void resolveSymbolicParamaters() throws Exception {
    String symbolic = null;
    String errorMessage = "";
    for (int i = 0; i < Atoms.size(); i++) {
      ADFAtom atom = Atoms.get(i);
      for (int j = 0; j < 3; j++) {
        symbolic = atom.Symbolics[j];
        if (symbolic == null) {
          continue;
        }
        // if (token.startsWith("-")) {
        // symbolic = token.substring(1);
        // sign = -1.0f;
        // }

        // if (symbolicStrings.get(symbolic) == null) {
        // errorMessage += "Symbolic parameter " + symbolic + " is not
        // resolved\n";
        // continue;
        // }
        double value;
        try {
          value = this.mathExpressionParser.resolveExpression(symbolic);
          logger.info(symbolic + " = " + value);
        } catch (Exception ex) {
          throw new Exception("Cannot resolve expression for atom " + (i + 1) + " : " + symbolic + " : " + ex.getMessage());
        }
        // float value = symbolicStrings.get(symbolic);

        if (atom.Cartesian) {
          atom.xyz[j] = distanceUnits * (float) value;
        } else if (j == 0) {
          atom.internal[j] = distanceUnits * (float) value;
        } else {
          atom.internal[j] = angleUnits * (float) value;
        }
      }
    }

    if (errorMessage.length() > 0) {
      throw new Exception(errorMessage);
    }

  }

  public void parseCHARGE(String charge) throws Exception {
    defaultChargeUsed = false;
    try {
      String token = charge;
      if (token.startsWith("+")) {
        token = token.substring(1);
      }
      totalCharge = Integer.parseInt(token); // (int)Float.parseFloat(charge);
    } catch (Exception ex) {
      throw new Exception("Cannot parse charge " + charge + " : " + ex.getMessage());
    }
  }

  public void parseQMMM(BufferedReader in) throws Exception {
    String line, token;
    while ((line = in.readLine()) != null) {
      line = line.trim().toUpperCase();
      if (line.length() < 1) { // Blank line
        continue;
      }
      if (line.equalsIgnoreCase("END")) {
        return;
      }

      if (line.equals("MM_CONNECTION_TABLE")) {
        connectionTable.clear();
        connectionTable.ensureCapacity(Atoms.size());
        for (int i = 0; i < Atoms.size(); i++) {
          if ((line = in.readLine()) == null) {
            throw new Exception(
                "Unexpecting end-of-file while reading MM_CONNECTION_TABLE");
          }

          StringTokenizer st = new StringTokenizer(line, " ,\t");
          if (st.countTokens() < 3) {
            throw new Exception(
                "Each MM_CONNECTION_TABLE record should have at least 3 tokens, got "
                + line);
          }

          st.nextToken(); // Skip atom number

          ADFAtom atom = Atoms.get(i);
          atom.FF_Label = st.nextToken();
          atom.MM_Type = st.nextToken();

          if (st.hasMoreTokens()) {
            int[] links = new int[st.countTokens()];
            int count = 0;
            while (st.hasMoreTokens()) {
              token = st.nextToken();
              try {
                links[count] = Integer.parseInt(token);
                --links[count];
                ++count;
              } catch (Exception ex) {
                throw new Exception(
                    "Error parsing connection within MM_CONNECTION_TABLE block. Expecting integer number, got "
                    + line);
              }
            }
            connectionTable.add(links);
          }
        }
      }
    }
  }

  void skipSection(BufferedReader in) throws Exception {
    String line;
    while ((line = in.readLine()) != null) {
      line = line.trim().toUpperCase();
      if (line.length() < 1) { // Blank line
        continue;
      }

      if (line.equalsIgnoreCase("END") || line.startsWith("END ")) {
        return;
      }

    }
  }

  /**
   * Geometric lengths and angles are in units defined by: UNITS length Angstrom / Bohr angle Degree / Radian end
   *
   * @param in BufferedReader
   * @throws Exception
   */
  public void parseUNITS(BufferedReader in) throws Exception {
    String line, token;
    while ((line = in.readLine()) != null) {
      line = line.trim().toUpperCase();
      if (line.length() < 1) { // Blank line
        continue;
      }

      if (line.equalsIgnoreCase("END")) {
        return;
      }

      StringTokenizer st = new StringTokenizer(line, " =,");
      if (st.countTokens() < 2) {
        throw new Exception("Usage of UNITS keyword:\n  UNITS\n" + "  LENGTH angstrom\n  ANGLE degree\nEND");
      }
      token = st.nextToken();

      if (token.equals("LENGTH")) {
        token = st.nextToken();
        if (token.equalsIgnoreCase("Angstrom")) {
          distanceUnits = 1.0f;
        } else if (token.equalsIgnoreCase("Bohr")) {
          distanceUnits = (float) ONE_BOHR;
        } else {
          throw new Exception("Unknown units for LENGTH: " + token + "\nValid keywords: Angstrom and Bohr");
        }
      } else if (token.equals("ANGLE")) {
        token = st.nextToken();
        if (token.equalsIgnoreCase("Degree")) {
          angleUnits = DEGREES_TO_RADIANS;
          mathExpressionParser.setInDegrees(true);
        } else if (token.equalsIgnoreCase("Radian")) {
          angleUnits = 1.0f;
          mathExpressionParser.setInDegrees(false);
        } else {
          throw new Exception("Unknown units for ANGLE: " + token
              + "\nValid keywords: Degree and Radian");
        }

      } else {
        throw new Exception("Unknown keyword in UNITS block: " + token
            + "\nValid keywords: LENGTH and ANGLE");
      }

    }
  }

  /**
   * ATOMS {Cartesian / Zmatrix / MOPAC / Internal / ZCart} {N} Atom Coords {F=Fragment} ... End
   *
   * @param keyword String
   * @param in BufferedReader
   * @throws Exception
   */
  public void parseATOMS(String keyword, BufferedReader in) throws Exception {
    String line, token = null;
    line = keyword.trim().toUpperCase();
    StringTokenizer st = new StringTokenizer(line, " =,");
    st.nextToken();
    if (st.hasMoreTokens()) {
      token = st.nextToken();
    }

    if (token == null || token.equalsIgnoreCase("Cartesian")
        || token.equalsIgnoreCase("Cart") || token.startsWith("CART")) {
      parseCartesianCoordinates(in);
    } else if (token.equalsIgnoreCase("Zmatrix") || token.equalsIgnoreCase("Z-matrix") || token.equalsIgnoreCase("Internal")
        || token.startsWith("Z-MAT") || token.startsWith("ZMAT")) {
      internalCoordinates = true;
      parseZmatrix(in);
    } else {
      throw new Exception("Unknown atom coordinates type: " + line);
    }
  }

  public void parseZmatrix(BufferedReader in) throws Exception {
    String line, token = null;
    while ((line = in.readLine()) != null) {
      line = line.trim();
      StringTokenizer st = new StringTokenizer(line, " =,");
      if (line.length() < 2) {
        throw new Exception("Expecting internal coordinates, got: " + line);
      }

      if (line.equalsIgnoreCase("END")
          || line.toUpperCase().startsWith("END")) {
        return;
      }

      // --- Skip the first token it it's a number
      token = st.nextToken();
      try {
        Float.parseFloat(token);
        token = st.nextToken();
      } catch (Exception ex) {
        // Do nothing
      }

      ADFAtom atom = new ADFAtom();
      atom.name = token;
      atom.atomicNumber = parseAtomLabel(atom.name);
      atom.Cartesian = false;

      if (Atoms.size() == 0) { // The first atom
        Atoms.add(atom);
        continue;
      }

      // --- Parding ia
      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting atom's number current atom is connected to. Got " + line);
      }

      token = st.nextToken();
      int ijk = 0;
      try {
        ijk = Integer.parseInt(token);
      } catch (Exception ex) {
        throw new Exception("Error parsing atom's number current atom is connected to. Got " + line);
      }

      if (ijk < 1 || ijk > Atoms.size()) {
        throw new Exception("Wrong atom's number current atom is connected to. Should be > 0 and < " + (Atoms.size() + 1)
            + "\nGot " + line);
      }

      atom.i1 = ijk - 1;

      // --- Parsing ib
      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting atom's number current atom is forming angle with. Got " + line);
      }

      token = st.nextToken();

      if (Atoms.size() > 1) {
        ijk = 0;
        try {
          ijk = Integer.parseInt(token);
        } catch (Exception ex) {
          throw new Exception(
              "Error parsing atom's number current atom is forming angle with. Got "
              + line);
        }

        if (ijk < 1 || ijk > Atoms.size() || ijk == atom.i1 + 1) {
          throw new Exception(
              "Wrong atom's number current atom is forming angle with. Should be > 0, < "
              + (Atoms.size() + 1) + " and not equal to "
              + (atom.i1 + 1) + "\nGot " + line);
        }

        atom.i2 = ijk - 1;
      }

      // --- Parsing ic
      if (!st.hasMoreTokens()) {
        throw new Exception(
            "Expecting atom's number current atom is forming torsion with. Got "
            + line);
      }

      token = st.nextToken();

      if (Atoms.size() > 2) {
        ijk = 0;
        try {
          ijk = Integer.parseInt(token);
        } catch (Exception ex) {
          throw new Exception(
              "Error parsing atom's number current atom is forming torsion with. Got "
              + line);
        }

        if (ijk < 1 || ijk > Atoms.size() || ijk == atom.i1 + 1 || ijk == atom.i2 + 1) {
          throw new Exception("Wrong atom's number current atom is forming torsion with. Should be > 0, <= "
              + (Atoms.size() + 1) + " and not equal to " + (atom.i1 + 1) + " and " + (atom.i2 + 1) + "\nGot "
              + line);
        }

        atom.i3 = ijk - 1;
      }

      // --- Parsing bond length
      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting bond length. Got " + line);
      }

      token = st.nextToken();

      float value = 0;
      try {
        value = Float.parseFloat(token);
        if (value <= 0) {
          throw new Exception("Wrong value for atomic distance. Should be > 0. Got " + line);
        }
        atom.internal[0] = value * this.distanceUnits;
      } catch (Exception ex) {
        symbolicParamResolved = false;
        atom.Symbolics[0] = token.toUpperCase();
      }

      if (Atoms.size() == 1) { // The Second
        Atoms.add(atom);
        continue;
      }

      // --- Parsing angle
      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting angle. Got " + line);
      }

      token = st.nextToken();

      value = 0;
      try {
        value = Float.parseFloat(token);
        atom.internal[1] = value * this.angleUnits;
      } catch (Exception ex) {
        symbolicParamResolved = false;
        atom.Symbolics[1] = token.toUpperCase();
      }

      if (Atoms.size() == 2) { // The third
        Atoms.add(atom);
        continue;
      }

      // --- Parsing torsion
      if (!st.hasMoreTokens()) {
        throw new Exception("Expecting torsion. Got " + line);
      }

      token = st.nextToken();

      value = 0;
      try {
        value = Float.parseFloat(token);
        atom.internal[2] = value * this.angleUnits;
      } catch (Exception ex) {
        symbolicParamResolved = false;
        atom.Symbolics[2] = token.toUpperCase();
      }

      Atoms.add(atom);

    }
  }

  boolean isNumber(String token) {
    try {
      Float.parseFloat(token);
      return true;
    } catch (Exception ex) {
    }
    return false;
  }

  public void parseCartesianCoordinates(BufferedReader in) throws Exception {
    String line, token = null;
    while ((line = in.readLine()) != null) {
      line = line.trim();
      StringTokenizer st = new StringTokenizer(line, " =,\t");
      if (line.length() < 1) {
        throw new Exception("Expecting cartesian coordinates, got: " + line);
      }

      if (line.equalsIgnoreCase("END") || line.toUpperCase().startsWith("END")) {
        return;
      }

      if (st.countTokens() < 4) {
        throw new Exception("Cartesian coordinates input should contain at least 4 tokens, got: " + line);
      }

      token = st.nextToken();
      if (isNumber(token)) {
        // Skip atom number
        token = st.nextToken();
      }

      ADFAtom atom = new ADFAtom();
      atom.name = token;
      atom.atomicNumber = parseAtomLabel(atom.name);
      atom.Cartesian = true;

      // --- Parse X,Y,Z-coordinates
      for (int i = 0; i < 3; i++) {
        token = st.nextToken();
        boolean isNumber = true;
        float value = 0;

        try {
          value = Float.parseFloat(token);
          atom.xyz[i] = value * distanceUnits;
        } catch (Exception ex) {
          isNumber = false;
        }

        // --- if it's a parameter
        if (!isNumber) {
          symbolicParamResolved = false;
          token = token.toUpperCase();
          // String symbolic = token;
          // if (token.startsWith("-")) {
          // symbolic = token.substring(1);
          // }
          // if (!symbolicStrings.containsKey(symbolic)) {
          // symbolicStrings.put(symbolic, null);
          // }
          atom.Symbolics[i] = token;
        }
      }

      Atoms.add(atom);
    }
  }

  /**
   * Returns element number (0 for dummy atom)
   *
   * @param label String
   * @return int
   */
  static public int parseAtomLabel(String label) throws Exception {
    int element = 0;

    if (label.length() == 1) {
      element = ChemicalElements.getAtomicNumber(label);
    } else if (label.indexOf(".") != -1) {
      label = label.substring(0, label.indexOf("."));
      element = ChemicalElements.getAtomicNumber(label);
    } else {
      element = ChemicalElements.getAtomicNumber(label);
    }

    return element;
  }

  /**
   * GEOVAR Name Data ... end
   *
   * @param in BufferedReader
   * @throws Exception
   */
  public void parseGEOVAR(BufferedReader in) throws Exception {
    /*
     * Data Either of the following three formats:
     *
     * 1 A single value simply assigns the value to the corresponding atomic
     * coordinate(s).
     *
     * 2 Two or more values (separated by a delimiter) imply that the
     * corresponding atomic coordinate is a Linear Transit or a Nudged
     * Elastic Band parameter. For Linear Transit, only two values are
     * allowed in which case they specify initial and final values of the LT
     * path, respectively. In case of a NEB calculation one can provide more
     * than just initial and final values to get a better initial
     * approximation of the reaction path. It is generally recommended (and
     * in some cases necessary) to use more values. Intermediate images will
     * be obtained by polynomial interpolation of degree N-1, where N is the
     * number of values.
     *
     * 3 A single value followed by a letter F assigns the value to the
     * corresponding atomic coordinates and specifies that these coordinates
     * are frozen: they will not be optimized.
     */

    String line, token = null;
    while ((line = in.readLine()) != null) {
      line = line.trim();
      StringTokenizer st = new StringTokenizer(line, " =,");
      if (line.length() < 1) {
        throw new Exception("Expecting data in GEOVAR block, got empty line");
      }

      if (line.equalsIgnoreCase("END")
          || line.toUpperCase().startsWith("END")) {
        return;
      }

      if (st.countTokens() < 2) {
        throw new Exception("Data in GEOVAR block should consist of at least 2 tokens, got: " + line);
      }

      // --- Get parameter name
      String symbol = st.nextToken().toUpperCase();

      if (mathExpressionParser.hasSymbol(symbol)) {
        throw new Exception("GEOVAR: Symbol " + symbol + " is already defined in the DEFINE block");
      }

      // if (symbolicStrings.get(symbol) != null) {
      // System.err.println("Symbolic parameter " + symbol + " is already
      // resolved. Ignored...");
      // continue;
      // }
      // --- Getting number
      mathExpressionParser.addLine(symbol + " = " + st.nextToken());

      // token = st.nextToken();
      // try {
      // float value = Float.parseFloat(token);
      // symbolicStrings.put(symbol, new Float(value));
      // }
      // catch (Exception ex) {
      // throw new Exception("Cannot parse value for symbolic parameter "
      // + symbol + " : " + token + " : " + ex.getMessage());
      // }
      if (!st.hasMoreTokens()) {
        continue;
      }

      token = st.nextToken();

      if (token.equalsIgnoreCase("F")) {
        frozenParameters.add(symbol);
        includesFrozenAtoms = true;
        continue;
      }

      System.err.println("Linear Transit or a Nudged Elastic Band parameters are currently not supported");

    }
  }

  public void parseDEFINE(BufferedReader in) throws Exception {
    /*
     */

    String line;
    while ((line = in.readLine()) != null) {
      line = line.trim();
      StringTokenizer st = new StringTokenizer(line, " =,");
      if (line.length() < 1) {
        continue;
        // throw new Exception("Expecting data in DEFINE block, got
        // empty line");
      }

      if (line.equalsIgnoreCase("END")
          || line.toUpperCase().startsWith("END")) {
        break;
      }

      if (st.countTokens() < 2) {
        throw new Exception("Data in DEFINE block should consist of at least 2 tokens, got: " + line);
      }

      try {
        mathExpressionParser.addLine(line);
      } catch (Exception ex) {
        throw new Exception("Error parsing expression " + line + " : " + ex.getMessage());
      }
    }

    String errorMessage = "";
    Iterator iter = symbolicStrings.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String key = me.getValue().toString();
      if (mathExpressionParser.hasSymbol(key)) {
        errorMessage += "Symbolic string " + key + " is already defined in GEOVAR block\n";
      }
    }
    if (errorMessage.length() > 0) {
      throw new Exception("Duplicate definition " + errorMessage);
    }

  }

  public static void fromZMatrixToCartesians(List<ADFAtom> atoms) throws Exception {
    int i;
    int natoms = atoms.size();
    ADFAtom a, a1, a2, a3;
    float xyz[] = new float[3], x1[], x2[], x3[];

    double SIN2, COS2, SIN3, COS3, VT[] = new double[3], V1[] = new double[3], V2[] = new double[3];
    double R2, V3[] = new double[3], VA1, VB1, VC1, R3, V[] = new double[3];

    if (natoms < 1) {
      return;
    }

    float factor = 1.0f;

    // --- The first atom. Put it into the origin
    ADFAtom atom = atoms.get(0);
    atom.setXYZ(0, 0, 0);

    if (natoms < 2) {
      return;
    }

    // --- ther second atom: put it along the X-axis
    atom = atoms.get(1);
    atom.setXYZ(atom.internal[0], 0, 0);

    if (natoms < 3) {
      return;
    }

    // --- Third atom (put it into the XOY plane
    a = atoms.get(2);

    // x1 = (float[]) cartesians.get(a.ijk[0] - 1);
    x1 = new float[3];
    int index = a.i1;
    a1 = atoms.get(index);
    x1[0] = a1.getX();
    x1[1] = a1.getY();
    x1[2] = a1.getZ();

    xyz[2] = 0.0f; // Z-coordinate

    if (index == 0) { // Connected to the first atom
      // logger.info.println("To the 1st");
      xyz[0] = a.getBondLength() * (float) Math.cos((double) a.getAngle()); // So, we put it
      // along X-axis
      xyz[1] = a.getBondLength() * (float) Math.sin((double) a.getAngle());
    } else {
      // logger.info.println("To the 2nd");
      xyz[0] = x1[0] - a.getBondLength() * (float) Math.cos((double) a.getAngle()); // So, we put it
      // along X-axis
      xyz[1] = -a.getBondLength() * (float) Math.sin((double) a.getAngle());
    }

    a.setXYZ(xyz[0], xyz[1], xyz[2]);

    // Other atoms
    for (i = 3; i < natoms; i++) {

      a = atoms.get(i);

      a1 = atoms.get(a.i1);
      a2 = atoms.get(a.i2);
      a3 = atoms.get(a.i3);

      x1 = a1.xyz;
      x2 = a2.xyz;
      x3 = a3.xyz;

      SIN2 = Math.sin((double) a.getAngle());
      COS2 = Math.cos((double) a.getAngle());
      SIN3 = Math.sin((double) a.getTorsion());
      COS3 = Math.cos((double) a.getTorsion());

      VT[0] = a.getBondLength() * factor * COS2;
      VT[1] = a.getBondLength() * factor * SIN2 * SIN3;
      VT[2] = a.getBondLength() * factor * SIN2 * COS3;

      V1[0] = x3[0] - x2[0];
      V1[1] = x3[1] - x2[1];
      V1[2] = x3[2] - x2[2];

      V2[0] = x2[0] - x1[0];
      V2[1] = x2[1] - x1[1];
      V2[2] = x2[2] - x1[2];

      R2 = Math.sqrt(V2[0] * V2[0] + V2[1] * V2[1] + V2[2] * V2[2]);

      V3[0] = V1[1] * V2[2] - V1[2] * V2[1];
      V3[1] = V1[2] * V2[0] - V1[0] * V2[2];
      V3[2] = V1[0] * V2[1] - V1[1] * V2[0];

      R3 = Math.sqrt(V3[0] * V3[0] + V3[1] * V3[1] + V3[2] * V3[2]);

      V2[0] = V2[0] / R2;
      V2[1] = V2[1] / R2;
      V2[2] = V2[2] / R2;

      V3[0] = V3[0] / R3;
      V3[1] = V3[1] / R3;
      V3[2] = V3[2] / R3;

      V[0] = V2[1] * V3[2] - V2[2] * V3[1];
      V[1] = V2[2] * V3[0] - V2[0] * V3[2];
      V[2] = V2[0] * V3[1] - V2[1] * V3[0];

      VA1 = V2[0] * VT[0] + V3[0] * VT[1] + V[0] * VT[2];
      VB1 = V2[1] * VT[0] + V3[1] * VT[1] + V[1] * VT[2];
      VC1 = V2[2] * VT[0] + V3[2] * VT[1] + V[2] * VT[2];

      xyz[0] = x1[0] + (float) VA1;
      xyz[1] = x1[1] + (float) VB1;
      xyz[2] = x1[2] + (float) VC1;

      a.setXYZ(xyz);
    }
  }
}
