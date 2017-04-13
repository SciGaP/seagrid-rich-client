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
package cct.modelling;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.ForceFieldInterface;
import cct.interfaces.MoleculeInterface;
import cct.sff.BondParam;
import cct.sff.SimpleForceField;
import cct.vecmath.Point3f;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Level;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class FFMolecule {

  static final Logger logger = Logger.getLogger(FFMolecule.class.getCanonicalName());
  static public final int HARMONIC_BOND_TERM = 0;
  static public float defaultFk = 300.0f;
  protected MoleculeInterface Molecule = null;
  // --- Internal energy related variables
  protected Object BondStretchInteractions[] = null;
  protected Object AngleBendInteractions[] = null;
  protected boolean Calculate_1_4 = true;
  protected ForceFieldInterface forceField;
  List Torsions = null;
  List torsionInteractions = null;
  List oneFourInteractions = null;
  List nonbodedPairs = null;
  List<MolecularPlane> surfaces = null;
  Nonbonded15Table nonbondedTable = null;
  float SCNB = 1, SCEE = 1;

  private FFMolecule() {
  }

  public FFMolecule(MoleculeInterface molec) {
    Molecule = molec;
  }

  public void applyForceField(ForceFieldInterface ff) {
    forceField = ff;
    SCNB = 1.0f / ff.get14NBScale();
    SCEE = 1.0f / ff.get14ElsScale();
  }

  public boolean isCalculate14() {
    return Calculate_1_4;
  }

  public List get14Interactions() {
    return oneFourInteractions;
  }

  public List getTorsionInteractions() {
    return torsionInteractions;
  }

  public List getNonbondInteractions() {
    return nonbodedPairs;
  }

  public Object[] getBondStretchInteractions() {
    return BondStretchInteractions;
  }

  public Object[] getAngleBendInteractions() {
    return AngleBendInteractions;
  }

  public MoleculeInterface getMolecule() {
    return Molecule;
  }

  public void formFFParameters() throws Exception {
    long start = 0;
    double secs;

    // --- Form Bond stretch parameters
    try {
      int n = 0;
      n = formBondStretchInteractions(HARMONIC_BOND_TERM, MolecularEnergy.SFF_PARAMETERS);
    } catch (Exception ex) {
      throw ex;
    }

    // --- Form angle bend parameters

    start = System.currentTimeMillis();
    AngleBendsArray angles = findDynamicAngles(Molecule);
    secs = (System.currentTimeMillis() - start) / 1000.0;
    logger.info("Number of angle bend interactions: " + angles.getSize() + " Elapsed time: "
            + String.format("%10.4f secs", secs));
    start = System.currentTimeMillis();
    formAngleBendInteractions(MolecularEnergy.SFF_PARAMETERS, angles);
    secs = (System.currentTimeMillis() - start) / 1000.0;
    logger.info(" Elapsed time for forming angle bend parameters: " + String.format("%10.4f secs", secs));

    // --- Form regular torsions

    start = System.currentTimeMillis();
    Torsions = findDynamicTorsions(Molecule);
    secs = (System.currentTimeMillis() - start) / 1000.0;
    logger.info("Number of torsions: " + Torsions.size() + " Elapsed time: "
            + String.format("%10.4f secs", secs));

    if (logger.isLoggable(Level.INFO)) {
      StringWriter sr = new StringWriter();
      for (int i = 0; i < Torsions.size(); i++) {
        Torsion torsion = (Torsion) Torsions.get(i);
        sr.write(String.format("%3d - %3d - %3d - %3d\n", torsion.ijkl[0], torsion.ijkl[1], torsion.ijkl[2], torsion.ijkl[3]));
      }
      logger.info(sr.toString());
      sr = null;
    }

    start = System.currentTimeMillis();
    torsionInteractions = formTorsionInteractions(MolecularEnergy.SFF_PARAMETERS, Torsions);
    secs = (System.currentTimeMillis() - start) / 1000.0;
    logger.info(" Elapsed time for assigning torsion parameters: " + String.format("%10.4f secs", secs));

    // --- Form 1-4 pairs...

    if (Calculate_1_4) {
      start = System.currentTimeMillis();
      oneFourInteractions = find14Interactions(Molecule, Torsions);
      secs = (System.currentTimeMillis() - start) / 1000.0;
      logger.info("Number of 1-4 interactions: " + oneFourInteractions.size() + " Elapsed time: "
              + String.format("%10.4f secs", secs));
      start = System.currentTimeMillis();
      setupNonbondedInteractions(MolecularEnergy.SFF_PARAMETERS, oneFourInteractions);
      secs = (System.currentTimeMillis() - start) / 1000.0;
      logger.info(" Elapsed time for assigning 1-4 NB parameters: " + String.format("%10.4f secs", secs));
    }

    // --- Form 1>4 interactions

    start = System.currentTimeMillis();
    nonbondedTable = new Nonbonded15Table(Molecule.getNumberOfAtoms());

    BondStretchPairs bondPairs = (BondStretchPairs) BondStretchInteractions[1];
    List bsp = bondPairs.getBondStretchPairs();
    for (int i = 0; i < bsp.size(); i++) {
      HarmonicBondStretch hbs = (HarmonicBondStretch) bsp.get(i);
      nonbondedTable.set15(hbs.i, hbs.j, false);
    }

    for (int i = 0; i < angles.getSize(); i++) {
      AngleBend ab = angles.getAngleBend(i);
      nonbondedTable.set15(ab.getI(), ab.getK(), false);
    }

    if (Calculate_1_4) {
      for (int i = 0; i < oneFourInteractions.size(); i++) {
        NonbondedPair nb = (NonbondedPair) oneFourInteractions.get(i);
        nonbondedTable.set15(nb.ij[0], nb.ij[1], false);
      }
    }

    nonbodedPairs = findNonbondedPairs(Molecule, nonbondedTable);
    secs = (System.currentTimeMillis() - start) / 1000.0;
    logger.info("Number of nonbonded interactions: " + nonbodedPairs.size() + " Elapsed time: "
            + String.format("%10.4f secs", secs));
    start = System.currentTimeMillis();
    setupNonbondedInteractions(MolecularEnergy.SFF_PARAMETERS, nonbodedPairs);
    secs = (System.currentTimeMillis() - start) / 1000.0;
    logger.info(" Elapsed time for assigning 1>4 NB parameters: " + String.format("%10.4f secs", secs));

    // --- Surface parameters, if any

    Object s = Molecule.getProperty(MoleculeInterface.SurfacesProperty);
    if (s != null) {
      System.out.println("Molecule has surface property of a class " + s.getClass().getCanonicalName());
      surfaces = new ArrayList<MolecularPlane>();
      try {
        Map ss = (Map) s;
        System.out.println("Number of surface(s): " + ss.size());
        for (Object key : ss.keySet()) {
          String name = (String) key;
          MolecularPlane plane = (MolecularPlane) ss.get(key);
          System.out.println("  Surface " + name);
          forceField.getSurfaceParam(plane);
          System.out.println("  Surface Params " + plane.getSurfaceParameter().getR() + " & " + plane.getSurfaceParameter().getW());
          setupNonbondedInteractions(Molecule, plane);
          surfaces.add(plane);
          System.out.println("Plane " + name + ": " + plane);
        }
      } catch (Exception ex) {
        logger.severe("Error Getting surface(s) from molecule: " + ex.getMessage());
      }
    }
  }

  /**
   *
   * @param paramType int
   * @param intType int- type of bond stretch interactions: 0 - harmonic K*(r-r0)
   * @return int - number of bond stretch interactions
   * @throws Exception
   */
  public int formBondStretchInteractions(int paramType, int intType) throws
          Exception {
    int n = 0;
    if (Molecule == null) {
      throw new Exception("Forming Bond Stretch Interactions: null pointer for molecule");
    }

    if (paramType != MolecularEnergy.SFF_PARAMETERS) {
      throw new Exception("Forming Bond Stretch Interactions: unknown type of parameters");
    }

    if (intType != HARMONIC_BOND_TERM) {
      throw new Exception("Forming Bond Stretch Interactions: unknown type of bond interactions");
    }

    // --- Go through all bonds
    long start = System.currentTimeMillis();

    if (BondStretchInteractions != null) {
      BondStretchInteractions = null;
    }

    BondStretchInteractions = new Object[2];

    BondStretchPairs bondPairs = new BondStretchPairs();

    if (intType == HARMONIC_BOND_TERM
            && paramType == MolecularEnergy.SFF_PARAMETERS) {
      BondStretchInteractions[0] = new Integer(HARMONIC_BOND_TERM);
      for (int i = 0; i < Molecule.getNumberOfBonds(); i++) {
        BondInterface bond = Molecule.getBondInterface(i);
        AtomInterface a1 = bond.getIAtomInterface();
        AtomInterface a2 = bond.getJAtomInterface();
        if (a1.isDynamic() || a2.isDynamic()) {

          int index_1 = Molecule.getAtomIndex(a1);
          int index_2 = Molecule.getAtomIndex(a2);

          String type_1 = (String) a1.getProperty(AtomInterface.CCT_ATOM_TYPE);
          if (type_1 == null) {
            type_1 = cct.modelling.Molecule.guessAtomType(a1, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          }

          String type_2 = (String) a2.getProperty(AtomInterface.CCT_ATOM_TYPE);
          if (type_2 == null) {
            type_2 = cct.modelling.Molecule.guessAtomType(a2, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          }

          if (type_1 == null || type_2 == null) {
            float r0 = (float) Point3f.distance(a1, a2);
            HarmonicBondStretch hbs = new HarmonicBondStretch(index_1, index_2, defaultFk, r0);
            bondPairs.addBondPair(hbs);
          } else {
            BondParam bp = forceField.getBondStretchParam(type_1, type_2);
            HarmonicBondStretch hbs = new HarmonicBondStretch(index_1, index_2, bp.Fk, bp.R0);
            bondPairs.addBondPair(hbs);
          }
        }
      }

      BondStretchInteractions[1] = bondPairs;

      float secs = (System.currentTimeMillis() - start) / 1000.0f;
      logger.info("Number of bond stretch interactions: " + bondPairs.getSize() + " Elapsed time: " + secs + " secs");
      List pairs = bondPairs.getBondStretchPairs();

      if (logger.isLoggable(Level.INFO)) {
        StringWriter sr = new StringWriter();
        for (int i = 0; i < pairs.size(); i++) {
          HarmonicBondStretch hbs = (HarmonicBondStretch) pairs.get(i);
          sr.write((i + 1) + " : " + hbs.i + " " + hbs.j + " " + hbs.Fk + " " + hbs.R0 + "\n");
        }
        logger.info(sr.toString());
        sr = null;
      }
    }

    return n;
  }

  public void setupNonbondedInteractions(int paramType, List nonbonded) throws Exception {

    if (nonbonded == null) {
      return;
    }

    if (paramType != MolecularEnergy.SFF_PARAMETERS) {
      throw new Exception("setupNonbondedInteractions: unknown type of parameters");
    }

    if (paramType == MolecularEnergy.SFF_PARAMETERS) {
      SimpleForceField.setupNonbondedParams(Molecule, nonbonded);
    }

  }

  public void setupNonbondedInteractions(MoleculeInterface mol, MolecularPlane plane) throws Exception {

    SimpleForceField.setupNonbondedParams(Molecule, plane);
  }

  public int formAngleBendInteractions(int paramType, AngleBendsArray angles) throws
          Exception {
    if (Molecule == null) {
      throw new Exception("formAngleBendInteractions: null pointer for molecule");
    }

    if (paramType != MolecularEnergy.SFF_PARAMETERS) {
      throw new Exception("formAngleBendInteractions: unknown type of parameters");
    }

    if (AngleBendInteractions != null) {
      AngleBendInteractions = null;
    }

    AngleBendInteractions = new Object[2];

    if (paramType == MolecularEnergy.SFF_PARAMETERS) {

      AngleBendInteractions[0] = new Integer(HARMONIC_BOND_TERM);

      forceField.getAngleBendParams(Molecule, angles);

      AngleBendInteractions[1] = angles;

      if (logger.isLoggable(Level.INFO)) {
        StringWriter sr = new StringWriter();
        logger.info("Number of angle bend interactions: "
                + angles.getSize() + "\nAngle Bend Parameters: ");
        for (int i = 0; i < angles.getSize(); i++) {
          AngleBend ab = angles.getAngleBend(i);
          sr.write((i + 1) + " : " + ab.getI() + " " + ab.getJ()
                  + " " + ab.getK() + " "
                  + (ab.Fk / SimpleForceField.FK_GRAD_2_RAD_FACTOR)
                  + " "
                  + (ab.Theta * (float) Point3f.RADIANS_TO_DEGREES) + "\n");
        }
        logger.info(sr.toString());
        sr = null;
      }
    }

    return angles.getSize();
  }

  public List formTorsionInteractions(int paramType, List torsions) throws
          Exception {

    if (Molecule == null) {
      throw new Exception("formTorsionInteractions: null pointer for molecule");
    }

    if (paramType != MolecularEnergy.SFF_PARAMETERS) {
      throw new Exception("formTorsionInteractions: unknown type of parameters");
    }

    if (paramType == MolecularEnergy.SFF_PARAMETERS) {

      torsionInteractions = forceField.getTorsionParams(Molecule, Torsions);

      if (logger.isLoggable(Level.INFO)) { // 800 is for the INFO level
        StringWriter sr = new StringWriter();
        sr.write("Number of torsion interactions: " + torsionInteractions.size()
                + "\nTorsion Parameters: \n");
        for (int i = 0; i < torsionInteractions.size(); i++) {
          TorsionParameter tp = (TorsionParameter) torsionInteractions.get(i);
          sr.write((i + 1) + " : " + tp.getI() + " " + tp.getJ() + " " + tp.getK() + " " + tp.getL() + " ");
          List serie = tp.getSerie();
          for (int j = 0; j < serie.size(); j++) {
            TorsionTerm tt = (TorsionTerm) serie.get(j);
            sr.write(tt.V + " " + tt.Periodicity + " " + (tt.Phase * (float) Point3f.RADIANS_TO_DEGREES));
          }
          sr.write("\n");
        }

        logger.info(sr.toString());
        sr.close();
        sr = null;
      }

    }

    return torsionInteractions;
  }

  /**
   * Finds all valent angles where at least one atom forming angle is dynamic
   *
   * @param molec MoleculeInterface - Molecule
   * @return AngleBendsArray
   */
  public static AngleBendsArray findDynamicAngles(MoleculeInterface molec) {

    AngleBendsArray aba = new AngleBendsArray();
    if (molec.getNumberOfAtoms() < 3) {
      return aba;
    }

    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface a1 = molec.getAtomInterface(i);
      int index_1 = molec.getAtomIndex(a1);
      List a1_list = a1.getBondedToAtoms();

      for (int j = 0; j < a1_list.size(); j++) {
        AtomInterface a2 = (AtomInterface) a1_list.get(j);
        int index_2 = molec.getAtomIndex(a2);
        List a2_list = a2.getBondedToAtoms();

        for (int k = 0; k < a2_list.size(); k++) {
          AtomInterface a3 = (AtomInterface) a2_list.get(k);

          if (a1.isDynamic() || a2.isDynamic() || a3.isDynamic()) {

            int index_3 = molec.getAtomIndex(a3);

            if (a1 == a3) {
              continue;
            }

            if (index_3 < index_1) {
              continue; // always go in descending order
            }

            // Check for 3-member ring
            if (a1.getBondToAtom(a3) != null) {
              continue;
            }

            AngleBend ab = new AngleBend(index_1, index_2, index_3, -1,
                    -1);
            aba.addAngleBend(ab);
          }
        }
      }
    }

    //logger.info("Number of angle bend interactions: " + aba.getSize());

    if (logger.isLoggable(Level.INFO)) {
      StringWriter sr = new StringWriter();
      for (int i = 0; i < aba.getSize(); i++) {
        if (i == 0) {
          sr.write("\nAngles: \n");
        }
        AngleBend ab = aba.getAngleBend(i);
        sr.write((i + 1) + " : " + ab.ijk[0] + " " + ab.ijk[1]
                + " "
                + ab.ijk[2] + " " + ab.Fk + " " + ab.Theta + "\n");
      }
      logger.info(sr.toString());
      try {
        sr.close();
      } catch (Exception ex) {
      }
      sr = null;
    }

    return aba;
  }

  /**
   * Finds all regular torsions in molecule
   *
   * @param molec MoleculeInterface - Molecule
   * @return ArrayList - array of class Torsion
   */
  public static List findDynamicTorsions(MoleculeInterface molec) {
    List torsions = new ArrayList();
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      //at[0] = i;
      AtomInterface a1 = molec.getAtomInterface(i);
      int index_1 = molec.getAtomIndex(a1);
      List a1_list = a1.getBondedToAtoms();

      for (int j = 0; j < a1_list.size(); j++) {
        //a2 = atom[i].br[j];
        AtomInterface a2 = (AtomInterface) a1_list.get(j);
        int index_2 = molec.getAtomIndex(a2);
        List a2_list = a2.getBondedToAtoms();

        for (int k = 0; k < a2_list.size(); k++) {
          //a3 = atom[a2].br[k];
          AtomInterface a3 = (AtomInterface) a2_list.get(k);
          int index_3 = molec.getAtomIndex(a3);
          List a3_list = a3.getBondedToAtoms();

          //if (a3 == i) {
          if (a3 == a1) {
            continue;
          }

          for (int l = 0; l < a3_list.size(); l++) {
            //a4 = atom[a3].br[l];
            AtomInterface a4 = (AtomInterface) a3_list.get(l);

            if (a4 == a2 || a4 == a1) {
              continue;
            }

            if (!a1.isDynamic() && !a2.isDynamic() && !a3.isDynamic()
                    && !a4.isDynamic()) {
              continue;
            }

            // Check for 4-member ring
            if (a1.getBondToAtom(a4) != null) {
              continue;
            }

            int index_4 = molec.getAtomIndex(a4);

            //if (a4 < i) {
            if (index_4 < index_1) {
              continue; // always go in descending order
            }

            Torsion torsion = new Torsion(index_1, index_2, index_3,
                    index_4);
            torsions.add(torsion);
            //printf("\n%3d - %3d - %3d - %3d", at[0], at[1], at[2], at[3]);
          }
        }
      }
    }

    return torsions;
  }

  public static List find14Interactions(MoleculeInterface molec, List torsions) {
    List int14 = new ArrayList();
    for (int i = 0; i < torsions.size(); i++) {
      Torsion tor = (Torsion) torsions.get(i);
      AtomInterface a1 = molec.getAtomInterface(tor.ijkl[0]);
      AtomInterface a4 = molec.getAtomInterface(tor.ijkl[3]);
      if ((!a1.isDynamic()) && (!a4.isDynamic())) {
        continue;
      }
      NonbondedPair nb = new NonbondedPair(tor.ijkl[0], tor.ijkl[3]);
      int14.add(nb);
    }
    return int14;
  }

  public static List findNonbondedPairs(MoleculeInterface molec, Nonbonded15Table nbTable) {
    List nb = new ArrayList();

    for (int i = 1; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface a1 = molec.getAtomInterface(i);
      for (int j = 0; j < i; j++) {
        AtomInterface a2 = molec.getAtomInterface(j);
        if (!a1.isDynamic() && !a2.isDynamic()) {
          continue;
        }
        if (nbTable.get15(i, j)) {
          NonbondedPair nbp = new NonbondedPair(molec.getAtomIndex(a1), molec.getAtomIndex(a2));
          nb.add(nbp);
        }
      }
    }
    return nb;
  }

  public static void main(String[] args) {
    FFMolecule ffmolecule = new FFMolecule();
  }

  public List<MolecularPlane> getSurfaces() {
    return surfaces;
  }
}
