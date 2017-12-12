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
package cct.sff;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.ForceFieldInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.*;
import cct.vecmath.Point3f;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class SimpleForceField implements ForceFieldInterface {

  static final public int SP_SP_TORSION = 1;
  static final public int SP_SP2_TORSION = 2;
  static final public int SP_SP3_TORSION = 3;
  static final public int SP2_SP2_TORSION = 4;
  static final public int SP2_SP3_TORSION = 5;
  static final public int SP3_SP3_TORSION = 6;
  protected Map bondStretchParams = null;
  static protected Map angleBendParams = null;
  static protected Map specificTorsionParams = null;
  static protected Map generalTorsionParams = null;
  protected Map<String, SurfaceParameter> surfaceParams = null;
  static protected String ffName = "SFF";
  static protected boolean calculateElectrostatics = false;
  static protected float dielectricConstant = 1.0f;
  static protected int dielectricFunction = CONSTANT_DIELECTRIC_FUNCTION;
  static protected boolean useNBCutoff = false;
  static protected float nbCutoff = 10.0f;
  static protected float oneFourVDWScale = 1.0f;
  static protected float oneFourElsScale = 1.0f;
  public static final float FK_GRAD_2_RAD_FACTOR = (float) (Point3f.RADIANS_TO_DEGREES * Point3f.RADIANS_TO_DEGREES);
  final static String Default_SFF_File = "SFF.txt";
  static boolean Initialized = false;
  static float defaultAngleFk = 50.0f;
  static final Logger logger = Logger.getLogger(SimpleForceField.class.getCanonicalName());

  public SimpleForceField() {
    initializeSFF();
  }

  public boolean isInitialized() {
    return Initialized;
  }

  public void initializeSFF() {
    Initialized = true;
    URL sffFile = null;
    InputStream is = null;
    try {
      ClassLoader cl = SimpleForceField.class.getClassLoader();
      //logger.info("Class loader: " + cl.toString());
      sffFile = cl.getResource("cct/sff/" + Default_SFF_File);
      is = cl.getResourceAsStream("cct/sff/" + Default_SFF_File);
      //readParametersAsASCII(sffFile.getFile());
      readParametersAsASCII(is);
    } catch (Exception ex) {
      System.err.println(" : " + ex.getMessage());
    }

  }

  public void readParametersAsASCII(InputStream is) throws Exception {

    String line;
    int flag = -1;
    Map residue = null;
    String res_name = "";

    try {
      //DataInputStream in = new DataInputStream(is);
      BufferedReader in = new BufferedReader(new InputStreamReader(is));

      while ((line = in.readLine()) != null) {
        line = line.trim();
        if (line.startsWith(";") || line.startsWith("#")) {
          continue; // Comment
        }
        if (line.length() == 0) {
          continue; // Empty line
        }

        line = line.toUpperCase();

        if (line.startsWith("<BONDPARAMETERS>")) { // Start to read bond stretch parameters

          bondStretchParams = new HashMap();

          while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(";") || line.startsWith("#")) {
              continue;
            }
            if (line.length() == 0) {
              break; // End of input - empty line
            }
            line = line.toUpperCase();

            StringTokenizer st = new StringTokenizer(line, " \t", false);
            if (st.countTokens() < 4) {
              in.close();
              throw new Exception(
                  "readParametersAsASCII: ERROR while reading bond stretch parameters: "
                  + line);
            }

            String a1 = st.nextToken();
            String a2 = st.nextToken();
            String atString = "";

            if (a1.compareTo(a2) == 0) {
              atString = a1 + "-" + a2;
            } else if (a1.compareTo(a2) < 0) {
              atString = a1 + "-" + a2;
            } else {
              atString = a2 + "-" + a1;
            }

            float fk, r0;
            try {
              fk = Float.parseFloat(st.nextToken());
              r0 = Float.parseFloat(st.nextToken());
            } catch (Exception ex) {
              in.close();
              throw new Exception(
                  "readParametersAsASCII: ERROR while reading bond stretch parameters: "
                  + line + " : " + ex.getMessage());
            }

            BondParam bp = new BondParam(fk, r0);

            bondStretchParams.put(atString, bp);

          }

          logger.info("Number of Bond Stretch parameters: "
              + bondStretchParams.size());
          Set set = bondStretchParams.entrySet();
          Iterator iter = set.iterator();
          int count = 0;
          while (iter.hasNext()) {
            ++count;
            Map.Entry me = (Map.Entry) iter.next();
            String Key = me.getKey().toString();
            BondParam bp = (BondParam) me.getValue();
            logger.info(count + " : " + Key + " " + bp.Fk + " "
                + bp.R0);
          }
        } else if (line.startsWith("<ANGLEPARAMETERS>")) { // Start to read angle bend parameters
          readAngleBendParameters(in);
        } else if (line.startsWith("<SURFACE>")) { // Start to read angle bend parameters
          readSurfaceParameters(in);
        }

      }

      in.close();
    } catch (Exception ex) {
      throw new Exception("readParametersAsASCII: ERROR: " + ex.getMessage());
    }

  }

  public void readParametersAsASCII(String filename) throws Exception {
    String line;
    BufferedReader in = null;
    int flag = -1;
    Map residue = null;
    String res_name = "";

    try {
      in = new BufferedReader(new FileReader(filename));

      while ((line = in.readLine()) != null) {
        line = line.trim();
        if (line.startsWith(";") || line.startsWith("#")) {
          continue; // Comment
        }
        if (line.length() == 0) {
          continue; // Empty line
        }

        line = line.toUpperCase();

        if (line.startsWith("<BONDPARAMETERS>")) { // Start to read bond stretch parameters

          bondStretchParams = new HashMap();

          while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(";") || line.startsWith("#")) {
              continue;
            }
            if (line.length() == 0) {
              break; // End of input - empty line
            }
            line = line.toUpperCase();

            StringTokenizer st = new StringTokenizer(line, " \t", false);
            if (st.countTokens() < 4) {
              in.close();
              throw new Exception(
                  "readParametersAsASCII: ERROR while reading bond stretch parameters: "
                  + line);
            }

            String a1 = st.nextToken();
            String a2 = st.nextToken();
            String atString = "";

            if (a1.compareTo(a2) == 0) {
              atString = a1 + "-" + a2;
            } else if (a1.compareTo(a2) < 0) {
              atString = a1 + "-" + a2;
            } else {
              atString = a2 + "-" + a1;
            }

            float fk, r0;
            try {
              fk = Float.parseFloat(st.nextToken());
              r0 = Float.parseFloat(st.nextToken());
            } catch (Exception ex) {
              in.close();
              throw new Exception(
                  "readParametersAsASCII: ERROR while reading bond stretch parameters: "
                  + line + " : " + ex.getMessage());
            }

            BondParam bp = new BondParam(fk, r0);

            bondStretchParams.put(atString, bp);

          }

          logger.info("Number of Bond Stretch parameters: "
              + bondStretchParams.size());
          Set set = bondStretchParams.entrySet();
          Iterator iter = set.iterator();
          int count = 0;
          while (iter.hasNext()) {
            ++count;
            Map.Entry me = (Map.Entry) iter.next();
            String Key = me.getKey().toString();
            BondParam bp = (BondParam) me.getValue();
            logger.info(count + " : " + Key + " " + bp.Fk + " "
                + bp.R0);
          }
        } else if (line.startsWith("<ANGLEPARAMETERS>")) { // Start to read angle bend parameters
          readAngleBendParameters(in);
        }

      }

      in.close();
    } catch (Exception ex) {
      if (in != null) {
        in.close();
      }
      throw new Exception("readParametersAsASCII: ERROR: " + ex.getMessage());
    }

  }

  /**
   * Table contains angle bend parameters in the form: Fk - in kcal/(mol*grad^2), Theta0 - in grads, so a subroutine
   * transforms Theta0 in radians, Fk - into kcal/(mol*rad^2)
   *
   * @param in BufferedReader
   * @throws Exception
   */
  public static void readAngleBendParameters(BufferedReader in) throws
      Exception {
    String line;
    angleBendParams = new HashMap();

    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.startsWith(";") || line.startsWith("#")) {
        continue;
      }
      if (line.length() == 0) {
        break; // End of input - empty line
      }
      line = line.toUpperCase();

      StringTokenizer st = new StringTokenizer(line, " \t", false);
      if (st.countTokens() < 5) {
        in.close();
        throw new Exception(
            "readAngleBendParameters: ERROR while reading angle bend parameters: "
            + line);
      }

      String a1 = st.nextToken();
      String a2 = st.nextToken();
      String a3 = st.nextToken();
      String atString = "";

      if (a1.compareTo(a3) == 0) {
        atString = a1 + "-" + a2 + "-" + a3;
      } else if (a1.compareTo(a3) < 0) {
        atString = a1 + "-" + a2 + "-" + a3;
      } else {
        atString = a3 + "-" + a2 + "-" + a1;
      }

      float fk, r0;
      try {
        fk = Float.parseFloat(st.nextToken()); // * FK_GRAD_2_RAD_FACTOR;
        r0 = Float.parseFloat(st.nextToken()) * (float) Point3f.DEGREES_TO_RADIANS;
      } catch (Exception ex) {
        throw new Exception(
            "readAngleBendParameters: ERROR while reading angle bend parameters: "
            + line + " : " + ex.getMessage());
      }

      AngleParam ap = new AngleParam(fk, r0);

      angleBendParams.put(atString, ap);

    }

    logger.info("Number of Angle Bend parameters: "
        + angleBendParams.size());
    Set set = angleBendParams.entrySet();
    Iterator iter = set.iterator();
    int count = 0;
    while (iter.hasNext()) {
      ++count;
      Map.Entry me = (Map.Entry) iter.next();
      String Key = me.getKey().toString();
      AngleParam bp = (AngleParam) me.getValue();
      logger.info(count + " : " + Key + " "
          + (bp.Fk / FK_GRAD_2_RAD_FACTOR) + " "
          + (bp.Theta * (float) Point3f.RADIANS_TO_DEGREES));
    }

  }

  public void getAngleBendParams(MoleculeInterface molec, AngleBendsArray angles) {

    if (!isInitialized()) {
      initializeSFF();
    }

    for (int i = 0; i < angles.getSize(); i++) {
      AngleBend ab = angles.getAngleBend(i);

      AtomInterface a1 = molec.getAtomInterface(ab.getI());
      AtomInterface a2 = molec.getAtomInterface(ab.getJ());
      AtomInterface a3 = molec.getAtomInterface(ab.getK());

      String type_1 = (String) a1.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_1 == null) {
        type_1 = cct.modelling.Molecule.guessAtomType(a1,
            AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      String type_2 = (String) a2.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_2 == null) {
        type_2 = cct.modelling.Molecule.guessAtomType(a2,
            AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      String type_3 = (String) a3.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_3 == null) {
        type_3 = cct.modelling.Molecule.guessAtomType(a3,
            AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      // --- Start to guess the parameters
      // --- Lookup in the table
      if (type_1 != null && type_2 != null && type_3 != null
          && CCTAtomTypes.isValidCCTType(type_1)
          && CCTAtomTypes.isValidCCTType(type_2)
          && CCTAtomTypes.isValidCCTType(type_3)
          && angleBendParams != null) {

        String atString = "";

        if (type_1.compareTo(type_3) == 0) {
          atString = type_1 + "-" + type_2 + "-" + type_3;
        } else if (type_1.compareTo(type_3) < 0) {
          atString = type_1 + "-" + type_2 + "-" + type_3;
        } else {
          atString = type_3 + "-" + type_2 + "-" + type_1;
        }

        if (angleBendParams.containsKey(atString)) {
          AngleParam ap = (AngleParam) angleBendParams.get(atString);
          ab.setFk(ap.Fk);
          ab.setTheta(ap.Theta);
        } else {
          float[] params = constructAngleBendParam(type_1, type_2,
              type_3, a1, a2, a3, molec);
          ab.setFk(params[0]);
          ab.setTheta(params[1]);
        }
      } // --- Try to guess
      else {
        float[] params = constructAngleBendParam(type_1, type_2,
            type_3, a1, a2, a3, molec);
        ab.setFk(params[0]);
        ab.setTheta(params[1]);
      }
    }
  }

  public static float[] constructAngleBendParam(String a1, String a2,
      String a3,
      AtomInterface atom_1,
      AtomInterface atom_2,
      AtomInterface atom_3,
      MoleculeInterface molec) {
    float[] ab = new float[2];
    float theta = 0;

    if (a2 == null || (a1 == null && a2 == null && a3 == null)
        || (!CCTAtomTypes.isValidCCTType(a2))) {
      theta = Point3f.angleBetween(atom_1, atom_2, atom_3);

      ab[0] = defaultAngleFk; // * FK_GRAD_2_RAD_FACTOR;
      ab[1] = theta; // Already in radians
    } else {
      CCTAtomTypes info = CCTAtomTypes.getAtomTypeInfo(a2);
      int geom = info.getGeometry();
      if (geom == CCTAtomTypes.LINEAR) {
        ab[0] = 70; // * FK_GRAD_2_RAD_FACTOR;
        ab[1] = 180 * (float) Point3f.DEGREES_TO_RADIANS;
        return ab;
      } else if (geom == CCTAtomTypes.TETRAHEDRAL) {
        ab[0] = 50; // * FK_GRAD_2_RAD_FACTOR;
        ab[1] = 109.5f * (float) Point3f.DEGREES_TO_RADIANS;
        return ab;
      }
      if (geom == CCTAtomTypes.TRIGONAL) {
        ab[0] = 70; // * FK_GRAD_2_RAD_FACTOR;
        ab[1] = 120 * (float) Point3f.DEGREES_TO_RADIANS;
        return ab;
      } else {
        System.err.println("Unknown geometry type for atom " + a2);
        theta = Point3f.angleBetween(atom_1, atom_2, atom_3);

        ab[0] = defaultAngleFk; // * FK_GRAD_2_RAD_FACTOR;
        ab[1] = theta; // Already in radians
      }
    }

    logger.info("Setting " + atom_1.getName() + "("
        + molec.getAtomIndex(atom_1) + ")-"
        + atom_2.getName() + "(" + molec.getAtomIndex(atom_2)
        + ")-" + atom_3.getName() + "("
        + molec.getAtomIndex(atom_3)
        + ") to its current value: "
        + ab[1] * Point3f.RADIANS_TO_DEGREES + " Fk: "
        + ab[0] + " Kcal/(mol*rad^2");
    return ab;
  }

  public BondParam getBondStretchParam(String a1, String a2) throws Exception {

    if (a1 == null || a2 == null || a1.length() == 0 || a2.length() == 0) {
      throw new Exception("getBondStretchParam: a1 == null || a2 == null || a1.length() == 0 || a2.length() == 0");
    }

    if (!isInitialized()) {
      initializeSFF();
    }

    if (bondStretchParams == null || bondStretchParams.size() < 1) {
      return guessBondStretchParam(a1, a2);
    }

    String atString = "";

    if (a1.compareTo(a2) == 0) {
      atString = a1 + "-" + a2;
    } else if (a1.compareTo(a2) < 0) {
      atString = a1 + "-" + a2;
    } else {
      atString = a2 + "-" + a1;
    }

    if (bondStretchParams.containsKey(atString)) {
      BondParam bp = (BondParam) bondStretchParams.get(atString);
      return new BondParam(bp.Fk, bp.R0);
    }

    return guessBondStretchParam(a1, a2);
  }

  public static BondParam guessBondStretchParam(String a1, String a2) {
    if (!CCTAtomTypes.isValidCCTType(a1)) {
      System.err.println(a1 + " is not a valid CCT atom type");
      return new BondParam(0, 0);
    }
    if (!CCTAtomTypes.isValidCCTType(a2)) {
      System.err.println(a2 + " is not a valid CCT atom type");
      return new BondParam(0, 0);
    }

    String atString = "";

    if (a1.compareTo(a2) == 0) {
      atString = a1 + "-" + a2;
    } else if (a1.compareTo(a2) < 0) {
      atString = a1 + "-" + a2;
    } else {
      atString = a2 + "-" + a1;
    }

    CCTAtomTypes info = CCTAtomTypes.getAtomTypeInfo(a1);
    float r1 = info.getCovalentRadius();
    info = CCTAtomTypes.getAtomTypeInfo(a2);
    float r2 = info.getCovalentRadius();

    return new BondParam(300.0f, (float) Math.sqrt(r1 + r2));
  }

  public static float guessCovalentBondLength(String a1, String a2) {
    if (!CCTAtomTypes.isValidCCTType(a1) || !CCTAtomTypes.isValidCCTType(a2)) {
      System.err.println(a2 + " or/and " + a1 + " is/are not valid CCT atom type");
      return 0;
    }

    CCTAtomTypes info = CCTAtomTypes.getAtomTypeInfo(a1);
    float r1 = info.getCovalentRadius();
    info = CCTAtomTypes.getAtomTypeInfo(a2);
    float r2 = info.getCovalentRadius();

    /*
     * String atString = ""; if (a1.compareTo(a2) == 0) { atString = a1 + "-" + a2; } else if (a1.compareTo(a2) < 0) { atString = a1
     * + "-" + a2; } else { atString = a2 + "-" + a1; }
     *
     * if (!isInitialized()) { initializeSFF(); }
     *
     * if (bondStretchParams.containsKey(atString)) { BondParam bp = (BondParam) bondStretchParams.get(atString); return bp.R0; }
     */
    return (float) Math.sqrt(r1 + r2);
  }

  public static void setupNonbondedParams(MoleculeInterface molec, List nonbonded) {
    int element;
    for (int i = 0; i < nonbonded.size(); i++) {
      NonbondedPair nb = (NonbondedPair) nonbonded.get(i);
      AtomInterface atom_1 = molec.getAtomInterface(nb.getI());
      AtomInterface atom_2 = molec.getAtomInterface(nb.getJ());

      String type_1 = (String) atom_1.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_1 == null) {
        type_1 = cct.modelling.Molecule.guessAtomType(atom_1, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      String type_2 = (String) atom_2.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_2 == null) {
        type_2 = cct.modelling.Molecule.guessAtomType(atom_2, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      float Di = 0, Dj = 0, Ri = 0, Rj = 0;

      // For atom i
      if (!CCTAtomTypes.isValidCCTType(type_1)) {
        System.err.println(type_1 + " is not a valid CCT atom type");
        element = atom_1.getAtomicNumber();
        Ri = ChemicalElements.getUFFRadius(element);
        Di = ChemicalElements.getUFFWellDepth(element);
        System.err.println("Di and Ri are set for corresponding element: " + Di + " " + Ri);
      } else {
        CCTAtomTypes info = CCTAtomTypes.getAtomTypeInfo(type_1);
        Ri = info.getVDWDIstance();
        Di = info.getVDWWellDepth();
      }

      // For atom j
      if (!CCTAtomTypes.isValidCCTType(type_2)) {
        System.err.println(type_1 + " is not a valid CCT atom type");
        element = atom_2.getAtomicNumber();
        Rj = ChemicalElements.getUFFRadius(element);
        Dj = ChemicalElements.getUFFWellDepth(element);
        System.err.println("Di and Ri are set for corresponding element: " + Dj + " " + Rj);
      } else {
        CCTAtomTypes info = CCTAtomTypes.getAtomTypeInfo(type_2);
        Rj = info.getVDWDIstance();
        Dj = info.getVDWWellDepth();
      }

      // --- Construct Aij and Cij for nonbonded pair
      // using geometric mean
      double Dij = Math.sqrt(Di * Dj);
      double Rij = Ri * Ri * Ri * Rj * Rj * Rj;

      nb.setA(Dij * Rij * Rij);
      nb.setC(2.0 * Dij * Rij);
    }

  }

  public static void setupNonbondedParams(MoleculeInterface mol, MolecularPlane plane) {
    int element;
    float Dj = (float) plane.getSurfaceParameter().getW(), Rj = (float) plane.getSurfaceParameter().getR();
    List<AtomSurfaceEntity> list = plane.getAtomList();
    list.clear();

    for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
      AtomInterface atom = mol.getAtomInterface(i);

      String type_1 = (String) atom.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_1 == null) {
        type_1 = cct.modelling.Molecule.guessAtomType(atom, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      float Di = 0, Ri = 0;

      // For atom i
      if (!CCTAtomTypes.isValidCCTType(type_1)) {
        System.err.println(type_1 + " is not a valid CCT atom type");
        element = atom.getAtomicNumber();
        Ri = ChemicalElements.getUFFRadius(element);
        Di = ChemicalElements.getUFFWellDepth(element);
        System.err.println("Di and Ri are set for corresponding element: " + Di + " " + Ri);
      } else {
        CCTAtomTypes info = CCTAtomTypes.getAtomTypeInfo(type_1);
        Ri = info.getVDWDIstance();
        Di = info.getVDWWellDepth();
      }

      // --- Construct Aij and Cij for nonbonded pair
      // using geometric mean
      double Dij = Math.sqrt(Di * Dj);
      double Rij = Ri * Ri * Ri * Rj * Rj * Rj;

      AtomSurfaceEntity nb = new AtomSurfaceEntity(atom, Dij * Rij * Rij, 2.0 * Dij * Rij);
      list.add(nb);
      //nb.setA(Dij * Rij * Rij);
      //nb.setC(2.0 * Dij * Rij);
    }

  }

  public List getTorsionParams(MoleculeInterface molec, List torsions) {

    if (!isInitialized()) {
      initializeSFF();
    }

    List tPars = new ArrayList();

    for (int i = 0; i < torsions.size(); i++) {
      Torsion tor = (Torsion) torsions.get(i);
      boolean foundParam = false;

      AtomInterface a1 = molec.getAtomInterface(tor.ijkl[0]);
      AtomInterface a2 = molec.getAtomInterface(tor.ijkl[1]);
      AtomInterface a3 = molec.getAtomInterface(tor.ijkl[2]);
      AtomInterface a4 = molec.getAtomInterface(tor.ijkl[3]);

      String type_1 = (String) a1.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_1 == null) {
        type_1 = cct.modelling.Molecule.guessAtomType(a1,
            AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      String type_2 = (String) a2.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_2 == null) {
        type_2 = cct.modelling.Molecule.guessAtomType(a2,
            AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      String type_3 = (String) a3.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_3 == null) {
        type_3 = cct.modelling.Molecule.guessAtomType(a3,
            AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      String type_4 = (String) a4.getProperty(AtomInterface.CCT_ATOM_TYPE);
      if (type_4 == null) {
        type_4 = cct.modelling.Molecule.guessAtomType(a4,
            AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      }

      // --- Start to guess the parameters
      // --- Lookup in the table with the SPECIFIC parameters
      if (type_1 != null && type_2 != null && type_3 != null && type_4 != null
          && CCTAtomTypes.isValidCCTType(type_1)
          && CCTAtomTypes.isValidCCTType(type_2)
          && CCTAtomTypes.isValidCCTType(type_3)
          && CCTAtomTypes.isValidCCTType(type_4)
          && specificTorsionParams != null) {

        String atString = "";

        if (type_1.compareTo(type_4) == 0) {
          atString = type_1 + "-" + type_2 + "-" + type_3 + "-" + type_4;
        } else if (type_1.compareTo(type_4) < 0) {
          atString = type_1 + "-" + type_2 + "-" + type_3 + "-" + type_4;
        } else {
          atString = type_4 + "-" + type_3 + "-" + type_2 + "-" + type_1;
        }

        if (specificTorsionParams.containsKey(atString)) {
          List serie = (List) specificTorsionParams.get(atString);
          TorsionParameter tpar = new TorsionParameter(tor, serie);
          tPars.add(tpar);
          foundParam = true;
        }
      }

      if (foundParam) {
        continue;
      }

      // --- Lookup in the table with the GENERAL parameters
      if (type_2 != null && type_3 != null
          && CCTAtomTypes.isValidCCTType(type_2)
          && CCTAtomTypes.isValidCCTType(type_3)
          && generalTorsionParams != null) {

        String atString = "";

        if (type_2.compareTo(type_3) == 0) {
          atString = type_2 + "-" + type_3;
        } else if (type_2.compareTo(type_3) < 0) {
          atString = type_2 + "-" + type_3;
        } else {
          atString = type_3 + "-" + type_2;
        }

        if (generalTorsionParams.containsKey(atString)) {
          List serie = (List) generalTorsionParams.get(atString);
          TorsionParameter tpar = new TorsionParameter(tor, serie);
          tPars.add(tpar);
          foundParam = true;
        }
      }

      if (foundParam) {
        continue;
      }

      // --- Try to guess
      List serie = guessTorsionParam(a2, a3, type_2, type_3);
      TorsionParameter tpar = new TorsionParameter(tor, serie);
      tPars.add(tpar);

    }

    return tPars;
  }

  public static List guessTorsionParam(AtomInterface a2, AtomInterface a3, String type_2, String type_3) {
    List serie = new ArrayList();

    int bond_type = SP3_SP3_TORSION;
    boolean bothTrigonal = false;
    boolean bothTetrahedral = true;
    boolean trigonalAndTetrahedral = false;

    if (type_2 != null && type_3 != null
        && CCTAtomTypes.isValidCCTType(type_2)
        && CCTAtomTypes.isValidCCTType(type_3)) {

      CCTAtomTypes info2 = CCTAtomTypes.getAtomTypeInfo(type_2);
      CCTAtomTypes info3 = CCTAtomTypes.getAtomTypeInfo(type_3);

      bothTrigonal = info2.getGeometry() == CCTAtomTypes.TRIGONAL
          && info3.getGeometry() == CCTAtomTypes.TRIGONAL;
      bothTetrahedral = info2.getGeometry() == CCTAtomTypes.TETRAHEDRAL
          && info3.getGeometry() == CCTAtomTypes.TETRAHEDRAL;
      trigonalAndTetrahedral = (info2.getGeometry()
          == CCTAtomTypes.TETRAHEDRAL
          && info3.getGeometry() == CCTAtomTypes.TRIGONAL)
          || (info2.getGeometry() == CCTAtomTypes.TRIGONAL
          && info3.getGeometry() == CCTAtomTypes.TETRAHEDRAL);

    }

    if (bothTetrahedral) {
      TorsionTerm tt = new TorsionTerm(0.15f, 3, 0.0f); // Amber: HC-CT-CT-HC   1    0.15          0.0             3.         Junmei et al, 1999
      serie.add(tt);
    } else if (bothTrigonal) {
      TorsionTerm tt = new TorsionTerm(3.625f, 2,
          (float) (180.0
          * Point3f.DEGREES_TO_RADIANS)); // Amber: X -CA-CA-X    4   14.50        180.0             2.         intrpol.bsd.on C6H6
      serie.add(tt);
    } else if (trigonalAndTetrahedral) {
      TorsionTerm tt = new TorsionTerm(0.0f, 3, 0.0f);
      serie.add(tt);
    } else {
      TorsionTerm tt = new TorsionTerm(0.0f, 3, 0.0f);
      serie.add(tt);

    }

    return serie;
  }

  public static void main(String[] args) {
    SimpleForceField simpleforcefield = new SimpleForceField();
  }

  public void enableCalculateElectrostatics(boolean enable) {
    calculateElectrostatics = enable;
  }

  public void enableUseNBCutoff(boolean enable) {
    useNBCutoff = enable;
  }

  public float get14ElsScale() {
    return oneFourElsScale;
  }

  public float get14NBScale() {
    return oneFourVDWScale;
  }

  public float getDielectricConstant() {
    return dielectricConstant;
  }

  public int getDielectricFunction() {
    return dielectricFunction;
  }

  public String getName() {
    return ffName;
  }

  public float getNBCutoff() {
    return nbCutoff;
  }

  public boolean isCalculateElectrostatics() {
    return calculateElectrostatics;
  }

  public boolean isUseNBCutoff() {
    return useNBCutoff;
  }

  public void set14ElsScale(float scne) {
    oneFourElsScale = scne;
  }

  public void set14NBScale(float scnb) {
    oneFourVDWScale = scnb;
  }

  public void setDielectricConstant(float diel) {
    dielectricConstant = diel;
  }

  public void setDielectricFunction(int func) {
    if (func != CONSTANT_DIELECTRIC_FUNCTION
        && func != DISTANCE_DIELECTRIC_FUNCTION) {
      System.err.println("Unknown dielectric function. Ignored...");
      return;
    }
    dielectricFunction = func;
  }

  public void setNBCutoff(float cutoff) {
    nbCutoff = cutoff;
  }

  public void readSurfaceParameters(BufferedReader in) throws Exception {
    String line;
    surfaceParams = new HashMap<String, SurfaceParameter>();

    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.startsWith(";") || line.startsWith("#")) {
        continue;
      }
      if (line.length() == 0) {
        break; // End of input - empty line
      }
      line = line.toUpperCase();

      StringTokenizer st = new StringTokenizer(line, " \t", false);
      if (st.countTokens() < 3) {
        in.close();
        throw new Exception("readSurfaceParameters: ERROR while reading surface parameters: " + line);
      }

      String a1 = st.nextToken();
      String a2 = st.nextToken();
      String a3 = st.nextToken();

      float R, W;
      try {
        R = Float.parseFloat(a2);
        W = Float.parseFloat(a3);
      } catch (Exception ex) {
        throw new Exception("readSurfaceParameters: ERROR while reading surface parameters: " + line + " : " + ex.getMessage());
      }

      SurfaceParameter sp = new SurfaceParameter(R, W);

      surfaceParams.put(a1, sp);
    }

    logger.info("Number of Angle Bend parameters: "
        + angleBendParams.size());
    Set set = angleBendParams.entrySet();
    Iterator iter = set.iterator();
    int count = 0;
    while (iter.hasNext()) {
      ++count;
      Map.Entry me = (Map.Entry) iter.next();
      String Key = me.getKey().toString();
      AngleParam bp = (AngleParam) me.getValue();
      logger.info(count + " : " + Key + " "
          + (bp.Fk / FK_GRAD_2_RAD_FACTOR) + " "
          + (bp.Theta * (float) Point3f.RADIANS_TO_DEGREES));
    }
  }

  public void getSurfaceParam(MolecularPlane mPlane) throws Exception {

    if (!isInitialized()) {
      initializeSFF();
    }

    SurfaceParameter sp = surfaceParams.get(mPlane.getName());

    if (sp == null) {
      throw new Exception("No Surface parameter for surface type " + mPlane.getName());
    }

    mPlane.setSurfaceParameter(sp.getR(), sp.getW());

  }
}
