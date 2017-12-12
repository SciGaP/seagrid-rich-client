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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.Constants;
import cct.amber.AmberUtilities;
import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.MonomerInterface;
import cct.interfaces.Point3fInterface;
import cct.pdb.PDB;
import cct.vecmath.Geometry3d;
import cct.vecmath.Point3f;
import cct.vecmath.VecmathTools;

/**
 *
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2015 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class Molecule
    implements AtomProperties, MoleculeInterface {

  protected String molName;
  protected int number_of_Atoms;
  protected AtomsArray Atoms;
  protected BondsArray Bonds;
  protected Monomer Residues; // Depreciated
  protected Map Properties = null;
  protected AtomInterface atomInterface = null;
  protected BondInterface bondInterface = null;
  protected boolean boundary_done = false;
  protected float Xmin, Xmax, Ymin, Ymax, Zmin, Zmax;
  static final Logger logger = Logger.getLogger(Molecule.class.getCanonicalName());

  public Molecule() {
    molName = "Unnamed Molecule";
    number_of_Atoms = 0;
    Atoms = new AtomsArray();
    Bonds = new BondsArray();
    Residues = new Monomer();
    Properties = new HashMap();

    // --- Set default atom & bond interfaces
    setAtomInterface(new Atom());
    setBondInterface(new Bond());
  }

  public boolean IsAtomSelected(int n) {
    if (n < 0 || n >= Atoms.size()) {
      return false;
    }
    AtomInterface a = Atoms.getAtom(n);
    return a.isSelected();
  }

  public void appendMolecule(MoleculeInterface mol) {
    if (mol == null || mol.getNumberOfAtoms() == 0) {
      System.err.println("Warning: appendMolecule: empty molecule to append");
      return;
    }

    AtomInterface refAtom = getNewAtomInstance();
    int oldNumAtoms = this.getNumberOfAtoms();
    int oldNumMonomers = this.getNumberOfMonomers();
    for (int i = 0; i < mol.getNumberOfMonomers(); i++) {
      MonomerInterface mono = mol.getMonomerInterface(i);
      addMonomer(mono.getName());
      for (int j = 0; j < mono.getNumberOfAtoms(); j++) {
        AtomInterface a1 = mono.getAtom(j);
        AtomInterface atom = refAtom.getNewAtomInstance(a1);
        addAtom(atom, oldNumMonomers + i);
      }
    }

    BondInterface refBond = this.getNewBondInstance();
    for (int i = 0; i < mol.getNumberOfBonds(); i++) {
      BondInterface b = mol.getBondInterface(i);
      AtomInterface a1 = b.getIAtomInterface();
      AtomInterface a2 = b.getJAtomInterface();
      int i_index = mol.getAtomIndex(a1);
      int j_index = mol.getAtomIndex(a2);

      a1 = this.getAtomInterface(oldNumAtoms + i_index);
      a2 = this.getAtomInterface(oldNumAtoms + j_index);
      BondInterface bond = refBond.getNewBondInstance(a1, a2);
      this.addBond(bond);
    }
  }

  /*
   * public void addAtom(Atom a) { Atoms.addAtom(a); }
   */
  public void addAtom(AtomInterface a) {
    Atoms.addAtom(a);
  }

  public void addAtom(AtomInterface a, int monomer_number) {
    Atoms.addAtom(a, monomer_number);
  }

  public void addAtom(AtomInterface a, String monomer_name) {
    Atoms.addAtom(a, monomer_name);
  }

  public void addAtom(AtomInterface a, int n, String name) {
    Atoms.addAtom(a, n, name);
  }

  public void addAtom(int element, float x, float y, float z) {
    Atom a = new Atom(element, x, y, z);
    Atoms.addAtom(a);
  }

  public void addBond(Bond b) {
    Bonds.add(b);
  }

  public void addBond(BondInterface b) {
    Bonds.add(b);
  }

  public Bond addBondBetweenAtoms(int origin, int target) {
    if (origin < 0 || origin >= getNumberOfAtoms()
        || target < 0 || target >= getNumberOfAtoms()) {
      return null;
    }

    AtomInterface a_origin = getAtom(origin);
    AtomInterface a_target = getAtom(target);
    AtomInterface a_i = a_origin, a_j = a_target;
    Bond b = null;

    // Check for existence of this bond
    for (int i = 0; i < getNumberOfBonds(); i++) {
      b = getBond(i);
      a_i = b.getI();
      a_j = b.getJ();
      if ((a_origin == a_i && a_target == a_j)
          || (a_origin == a_j && a_target == a_i)) {
        return null;
      }
    }

    if (a_i == null || a_j == null) {
      return null;
    }

    // --- Create new bond
    b = createNewBond(a_origin, a_target);

    return b;
  }

  public void addMonomer(String name) {
    Atoms.addMonomer(name);
  }

  public static void addMonomer(MoleculeInterface molec,
      MonomerInterface monomer) {
    molec.addMonomer(monomer.getName());
    int n = molec.getNumberOfMonomers() - 1;
    int oldAtoms = molec.getNumberOfAtoms();
    AtomInterface refAtom = molec.getNewAtomInstance();

    // --- Adding atoms
    for (int i = 0; i < monomer.getNumberOfAtoms(); i++) {
      AtomInterface atom = refAtom.getNewAtomInstance(monomer.getAtom(i));
      molec.addAtom(atom, n);
    }

    // --- Adding bonds
    for (int i = 0; i < monomer.getNumberOfAtoms(); i++) {
      AtomInterface atom = monomer.getAtom(i);
      List bondedAtoms = monomer.getAtom(i).getBondedToAtoms();
      for (int j = 0; j < bondedAtoms.size(); j++) {
        AtomInterface a2 = (AtomInterface) bondedAtoms.get(j);
        int second_index = monomer.getAtomIndex(a2);
        if (second_index == -1) {
          continue;
        } else if (second_index < j) {
          continue;
        }

        AtomInterface a_1 = molec.getAtomInterface(oldAtoms + i);
        AtomInterface a_2 = molec.getAtomInterface(oldAtoms + second_index);
        BondInterface bond = molec.getNewBondInstance(a_1, a_2);
        molec.addBond(bond);
      }
    }
  }

  public void addProperty(String propName, Object value) {
    if (Properties.containsKey(propName)) {
      Object obj = Properties.get(propName);
      if (obj instanceof List) {
        List props = (List) obj;
        props.add(value);
      } else {
        List props = new ArrayList();
        props.add(obj);
        props.add(value);
        Properties.put(propName, props);
      }
    } else { // Add this property for the first time
      Properties.put(propName, value);
    }
  }

  public void centerMolecule(float x, float y, float z) {
    if (this.getNumberOfAtoms() < 1) {
      return;
    }
    AtomInterface a = getAtom(0);
    float x_center = a.getX();
    float y_center = a.getY();
    float z_center = a.getZ();
    for (int i = 1; i < getNumberOfAtoms(); i++) {
      a = getAtom(i);
      x_center += a.getX();
      y_center += a.getY();
      z_center += a.getZ();
    }

    x_center /= (float) getNumberOfAtoms();
    y_center /= (float) getNumberOfAtoms();
    z_center /= (float) getNumberOfAtoms();

    for (int i = 0; i < getNumberOfAtoms(); i++) {
      a = getAtom(i);
      a.setX(a.getX() - x_center + x);
      a.setY(a.getY() - y_center + y);
      a.setZ(a.getZ() - z_center + z);
    }

  }

  public static MoleculeInterface cloneMolecule(MoleculeInterface refMol) {
    return duplicateMolecule(refMol);
  }

  /**
   * No error check!!!
   *
   * @param origin int
   * @param target int
   */
  Bond createNewBond(AtomInterface a_origin, AtomInterface a_target) {
    Bond b = new Bond(a_origin, a_target);
    Bonds.add(b);
    return b;
  }

  public AtomInterface deleteAtom(int n) {
    AtomInterface atom = this.getAtomInterface(n);
    List list = atom.getBondIndex();

    //logger.info("  Number of bonds: "+list.size()+" Total number of bonds: "+this.getNumberOfBonds());
    // --- Delete all associated bonds
    int i;
    while (list.size() > 0) {
      i = list.size() - 1;
      BondInterface bond = (BondInterface) list.get(i);
      if (deleteBond(bond) == null) {
        System.err.println("    Cannot delete " + i + " bond");
      } else {
        //logger.info("    "+i+" bond deleted Total number of bonds: "+this.getNumberOfBonds());
      }

    }
    /*
    * if (list != null && list.size() > 0) { for (int i = list.size() - 1; i > -1; i--) { BondInterface bond = (BondInterface)
    * list.get(i); if (deleteBond(bond) == null) { System.err.println(" Cannot delete " + i + " bond"); } else { //logger.info("
    * "+i+" bond deleted Total number of bonds: "+this.getNumberOfBonds()); } } }
     */
    Atoms.removeAtom(atom);

    return atom;
  }

  /**
   * Deletes bond between atoms and all related information
   *
   * @param bond_to_delete BondInterface - bond to delete
   * @return BondInterface - Deleted bond or "null" if molecule does not have this bond
   */
  public BondInterface deleteBond(BondInterface bond_to_delete) {
    if (Bonds.indexOf(bond_to_delete) == -1) {
      System.err.println("Cannot delete bond between: "
          + this.getAtomIndex(bond_to_delete.getIAtomInterface())
          + " & "
          + getAtomIndex(bond_to_delete.getJAtomInterface()));
      return null; // Error
    }
    AtomInterface a_origin = bond_to_delete.getIAtomInterface();
    AtomInterface a_target = bond_to_delete.getJAtomInterface();

    a_origin.setBondedTo(a_target, false);
    a_origin.removeBond(bond_to_delete);

    a_target.setBondedTo(a_origin, false);
    a_target.removeBond(bond_to_delete);

    if (!Bonds.removeBond(bond_to_delete)) {
      System.err.println("Bonds.removeBond: Cannot delete bond");
    }
    return bond_to_delete;
  }

  public int deleteSelectedAtoms() {
    return deleteSelectedAtoms(this);
  }

  public static int deleteSelectedAtoms(MoleculeInterface molec) {

    int deleted = 0;
    int nat = molec.getNumberOfAtoms();
    for (int i = nat - 1; i > -1; i--) {
      AtomInterface atom = molec.getAtomInterface(i);
      if (!atom.isSelected()) {
        continue; // Atom is not selected
      }
      logger.info("Deleting atom: " + i);
      molec.deleteAtom(i);
      ++deleted;
    }

    if (true) {
      return deleted;
    }

    // --- Delete atoms and associated bonds
    for (int i = nat - 1; i > -1; i--) {
      AtomInterface atom = molec.getAtomInterface(i);
      if (!atom.isSelected()) {
        continue; // Atom is not selected
      }
      AtomInterface at = molec.getAtomInterface(i);
      List bi = at.getBondIndex();

      // --- First, delete bonds associated with this atom
      for (int j = 0; j < bi.size(); j++) {
        BondInterface b = (BondInterface) bi.get(j);
        if (molec.getBondIndex(b) == -1) {
          continue; // --- Bond is already deleted
        }
        //Bonds.removeBond(b);
        molec.deleteBond(b);
      }

      ++deleted;
    }

    // --- Final clean-up
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface at = molec.getAtomInterface(i);
      List bi = at.getBondIndex();
      int nb = bi.size();
      // --- remove bond references to deleted bonds
      for (int j = nb - 1; j > -1; j--) {
        BondInterface b = (BondInterface) bi.get(j);
        if (molec.getBondIndex(b) == -1) {
          bi.remove(b);
        }
      }
      // --- remove references to deleted atoms
      bi = at.getBondedToAtoms();
      nb = bi.size();
      for (int j = nb - 1; j > -1; j--) {
        Atom atom = (Atom) bi.get(j);
        if (molec.getAtomIndex(atom) == -1) {
          bi.remove(atom);
        }
      }

    }

    return deleted;
  }

  public static String distancesAsString(MoleculeInterface molecule) {
    if (molecule == null) {
      return "Molecule is not allocated";
    }

    StringWriter sWriter = new StringWriter();
    sWriter.write("Interatomic distances for " + molecule.getName() + "\n\n");
    //double *A,
    int NUMB = molecule.getNumberOfAtoms();
    int LIMIT = (NUMB * (NUMB + 1)) / 2;
    int I, K, L = 0, LL, M, MA, N, NA = 1, n;
    do {
      LL = 0;
      M = Math.min((NUMB + 1 - NA), 5);
      MA = 3 * M + 1;
      M = NA + M - 1;
      sWriter.write("\n\n  ");

      for (I = NA; I <= M; I++) {
        sWriter.write(String.format("       %5d", I));
      }
      sWriter.write("\n  ");
      for (I = NA; I <= M; I++) {
        AtomInterface a_1 = molecule.getAtomInterface(I - 1);
        n = Math.min(a_1.getName().length(), 5);
        sWriter.write(String.format("       %5s", a_1.getName().substring(0, n)));
      }

      sWriter.write("\n ");
      for (I = 1; I <= MA; I++) {
        sWriter.write("----");
      }
      for (I = NA; I <= NUMB; I++) {
        ++LL;
        K = (I * (I - 1)) / 2;
        L = Math.min((K + M), (K + I));
        K += NA;
        sWriter.write(String.format("\n%4d ", I));
        AtomInterface a_1 = molecule.getAtomInterface(I - 1);
        for (n = NA - 1, N = K; N <= L; N++, n++) {
          AtomInterface a_2 = molecule.getAtomInterface(n);
          sWriter.write(String.format("%12.4f", a_1.distanceTo(a_2)));
          //sWriter.write(String.format("%12.4lf", A[N - 1]));
        }
        sWriter.write("  " + a_1.getName());
      }
      NA = M + 1;
    } while (L < LIMIT);

    return sWriter.toString();
  }

  public static MoleculeInterface divideIntoMolecules(MoleculeInterface molec) {
    int nAtoms = molec.getNumberOfAtoms();
    if (nAtoms < 2) {
      return molec;
    }

    int atomsInMol = 0;
    MoleculeInterface molecule = molec.getInstance();

    // -- Copy all molecular properties
    molecule.setName(molec.getName());

    Map props = molec.getProperties();
    Set set = props.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      Object key = me.getKey();
      Object value = me.getValue();
      molecule.addProperty(key.toString(), value);
    }

    // --- unselect all atoms first
    List reference = new ArrayList(nAtoms); // Temporary array
    if (reference instanceof ArrayList) {
      ((ArrayList) reference).ensureCapacity(nAtoms);
    }
    for (int i = 0; i < nAtoms; i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      atom.setSelected(false);
      reference.add(null);
    }

    int nRes = 0;
    for (int i = 0; i < nAtoms; i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      if (atom.isSelected()) {
        continue;
      }
      List group = getMoleculeAtomBelongsTo(atom);
      logger.info("Found molecule " + (nRes + 1)
          + " number of atoms: " + group.size());
      atomsInMol += group.size();
      ++nRes;
      String resName = "R" + nRes;
      molecule.addMonomer(resName);
      logger.info("  New number of monomers: "
          + molecule.getNumberOfMonomers());
      for (int j = 0; j < group.size(); j++) {
        atom = (AtomInterface) group.get(j);
        int index = molec.getAtomIndex(atom);
        AtomInterface a = atom.getNewAtomInstance(atom);
        //reference.add(index, a);
        reference.set(index, a);
        //a.setSubstructureNumber(nRes-1);
        molecule.addAtom(a);
        atom.setSelected(true);
      }

      if (atomsInMol == nAtoms) {
        break;
      }
    }

    // --- now, reconstruct bonds
    if (molec.getNumberOfBonds() > 0) {
      BondInterface refB = molec.getBondInterface(0);
      for (int i = 0; i < molec.getNumberOfBonds(); i++) {
        BondInterface bond = molec.getBondInterface(i);
        AtomInterface a_i = bond.getIAtomInterface();
        AtomInterface a_j = bond.getJAtomInterface();
        int i_index = molec.getAtomIndex(a_i);
        int j_index = molec.getAtomIndex(a_j);

        a_i = (AtomInterface) reference.get(i_index);
        a_j = (AtomInterface) reference.get(j_index);
        bond = refB.getNewBondInstance(a_i, a_j);
        molecule.addBond(bond);
      }
    }

    // --- free memory
    reference = null;

    return molecule;
  }

  public static MoleculeInterface duplicateMolecule(MoleculeInterface refMol) {
    MoleculeInterface copy = refMol.getInstance();
    copy.appendMolecule(refMol);
    return copy;
  }

  private static void findAllBondedAtoms(AtomInterface target_atom, List<AtomInterface> group) {
    if (group.contains(target_atom)) {
      return;
    }
    group.add(target_atom);
    List<AtomInterface> bonded = target_atom.getBondedToAtoms();
    for (int i = 0; i < bonded.size(); i++) {
      AtomInterface a = bonded.get(i);
      if (group.contains(a)) {
        continue;
      }
      if (a.getNumberOfBondedAtoms() == 1) {
        group.add(a);
        continue;
      }
      findAllBondedAtoms(a, group);
    }
  }

  void findBoundaries() {
    Xmin = Xmax = Ymin = Ymax = Zmin = Zmax = 0.0f;
    if (Atoms.size() < 1) {
      return;
    }
    AtomInterface a = Atoms.getAtom(0);
    Xmin = Xmax = a.getX();
    Ymin = Ymax = a.getY();
    Zmin = Zmax = a.getZ();
    for (int i = 1; i < Atoms.size(); i++) {
      a = Atoms.getAtom(i);
      if (Xmin > a.getX()) {
        Xmin = a.getX();
      } else if (Xmax < a.getX()) {
        Xmax = a.getX();
      }
      if (Ymin > a.getY()) {
        Ymin = a.getY();
      } else if (Ymax < a.getY()) {
        Ymax = a.getY();
      }
      if (Zmin > a.getZ()) {
        Zmin = a.getZ();
      } else if (Zmax < a.getZ()) {
        Zmax = a.getZ();
      }
    }
    boundary_done = true;
  }

  public boolean fromArchiveFormat(InputStream in) {
    String bufer;
    byte symbol[] = new byte[1];
    int natoms = 0;

    // -- Read number of atoms
    bufer = "";
    try {
      while (in.read(symbol) != -1 && symbol[0] != '\n') {
        bufer += (char) symbol[0];
      }
      natoms = Integer.parseInt(bufer);
      logger.info("fromArchiveFormat: Number of atoms: " + natoms);
    } catch (IOException e) {
      logger.info(
          "fromArchiveFormat: cannot read number of atoms\n");
      return false;
    }

    // --- Read atoms
    try {
      for (int i = 0; i < natoms; i++) {
        bufer = "";
        while (in.read(symbol) != -1 && symbol[0] != '\n') {
          bufer += (char) symbol[0];
        }
        StringTokenizer st = new StringTokenizer(bufer, ",");
        //gAtom a = new Atom();
        AtomInterface a = this.getNewAtomInstance();
        a.setAtomicNumber(Integer.parseInt(st.nextToken()));
        a.setProperty(AtomInterface.NAME, st.nextToken());
        a.setX(Float.parseFloat(st.nextToken()));
        a.setY(Float.parseFloat(st.nextToken()));
        a.setZ(Float.parseFloat(st.nextToken()));
        addAtom(a);
      }
    } catch (IOException e) {
      logger.info("fromArchiveFormat: cannot read atom's info");
      return false;
    }

    // -- Read number of bonds if any
    bufer = "";
    int nbonds;
    try {
      while (in.read(symbol) != -1 && symbol[0] != '\n') {
        bufer += (char) symbol[0];
      }
      nbonds = Integer.parseInt(bufer);
      logger.info("fromArchiveFormat: Number of bonds: " + nbonds);
    } catch (IOException e) {
      logger.info(
          "fromArchiveFormat: cannot read number of bonds\n");
      return false;
    }

    // --- Read bonds
    try {
      for (int i = 0; i < nbonds; i++) {
        bufer = "";
        while (in.read(symbol) != -1 && symbol[0] != '\n') {
          bufer += (char) symbol[0];
        }
        StringTokenizer st = new StringTokenizer(bufer, ",");
        int at_i = Integer.parseInt(st.nextToken());
        int at_j = Integer.parseInt(st.nextToken());
        AtomInterface a_i = Atoms.getAtom(at_i);
        AtomInterface a_j = Atoms.getAtom(at_j);
        createNewBond(a_i, a_j);
        //Bond b = new Bond( a_i, a_j);
        //addBond( b );
      }
    } catch (IOException e) {
      logger.info("fromArchiveFormat: cannot read bonds info");
      return false;
    }

    // --- Close
    try {
      in.close();
    } catch (IOException e) {
      //logger.info( "toArchiveFormat: cannot write atom's info");
      //return false;
    }

    return true;
  }

  public boolean fromArchiveFormat(byte in[]) {
    String bufer;
    int offset = 0;
    int natoms = 0;

    //bufer = in;
    //logger.info("fromArchiveFormat: input string: " + bufer );
    // -- Read number of atoms
    bufer = "";
    offset = -1;
    //try {
    while (in[++offset] != '\n' && offset < in.length) {
      bufer += (char) in[offset];
      //++offset;
    }
    natoms = Integer.parseInt(bufer);
    logger.info("fromArchiveFormat: Number of atoms: " + natoms);
    //} catch ( IOException e ) {
    //    logger.info( "fromArchiveFormat: cannot read number of atoms\n");
    //    return false;
    //}

    // --- Read atoms
    //try {
    for (int i = 0; i < natoms; i++) {
      bufer = "";
      while (in[++offset] != '\n' && offset < in.length) {
        bufer += (char) in[offset];
        //++offset;
      }
      StringTokenizer st = new StringTokenizer(bufer, ",");
      Atom a = new Atom();
      a.setAtomicNumber(Integer.parseInt(st.nextToken()));
      a.setProperty(AtomInterface.NAME, st.nextToken());
      a.setX(Float.parseFloat(st.nextToken()));
      a.setY(Float.parseFloat(st.nextToken()));
      a.setZ(Float.parseFloat(st.nextToken()));
      addAtom(a);
    }
    //} catch ( IOException e ) {
    //    logger.info( "fromArchiveFormat: cannot read atom's info");
    //    return false;
    //}

    // --- Close
    //try {
    //    in.close();
    //} catch ( IOException e ) {
    //logger.info( "toArchiveFormat: cannot write atom's info");
    //return false;
    //}
    return true;
  }

  public static MoleculeInterface generateSolventShell(MoleculeInterface refMol, MoleculeInterface solvent, float radius, float closeness,
      int solvationType) {

    // --- Error check
    if (refMol.getNumberOfAtoms() < 1 || solvent.getNumberOfAtoms() < 1) {
      System.err.println(
          "generateSolventShell: reference molecule or/and solvent has no atoms: "
          + refMol.getNumberOfAtoms() + " & " + solvent.getNumberOfAtoms());
      return null;
    }
    if (solvationType != OperationsOnAtoms.SELECTED_SOLVATE_CAP
        && solvationType != OperationsOnAtoms.SELECTED_SOLVATE_SHELL) {
      System.err.println(
          "generateSolventShell: unknown solvation type: " + solvationType);
      return null;
    }
    // --- Exit if no selected atoms
    boolean no_selected = true;
    for (int i = 0; i < refMol.getNumberOfAtoms(); i++) {
      AtomInterface atom = refMol.getAtomInterface(i);
      if (atom.isSelected()) {
        no_selected = false;
        break;
      }
    }
    if (no_selected) {
      System.err.println("generateSolventShell: no selected atoms in reference molecule");
      return null;
    }

    // --- End of error check
    MoleculeInterface shell = refMol.getInstance();

    // -- Determine overall size of solvation box
    float xStart = 0, yStart = 0, zStart = 0, xEnd = 0, yEnd = 0, zEnd = 0;
    float x = 0, y = 0, z = 0;
    Point3f point = null;

    if (solvationType == OperationsOnAtoms.SELECTED_SOLVATE_CAP) {
      int n = 0;
      for (int i = 0; i < refMol.getNumberOfAtoms(); i++) {
        AtomInterface atom = refMol.getAtomInterface(i);
        if (atom.isSelected()) {
          ++n;
          x += atom.getX();
          y += atom.getY();
          z += atom.getZ();
        }
      }
      x /= (float) n;
      y /= (float) n;
      z /= (float) n;
      point = new Point3f(x, y, z);

      xStart = x - radius;
      yStart = y - radius;
      zStart = z - radius;
      xEnd = x + radius;
      yEnd = y + radius;
      zEnd = z + radius;

    } else if (solvationType == OperationsOnAtoms.SELECTED_SOLVATE_SHELL) {
      int n = 0;
      for (int i = 0; i < refMol.getNumberOfAtoms(); i++) {
        AtomInterface atom = refMol.getAtomInterface(i);
        if (atom.isSelected() && n == 0) {
          xStart = atom.getX() - radius;
          yStart = atom.getY() - radius;
          zStart = atom.getZ() - radius;
          xEnd = atom.getX() + radius;
          yEnd = atom.getY() + radius;
          zEnd = atom.getZ() + radius;
          ++n;
        } else if (atom.isSelected()) {
          if (xStart > atom.getX() - radius) {
            xStart = atom.getX() - radius;
          }
          if (yStart > atom.getY() - radius) {
            yStart = atom.getY() - radius;
          }
          if (zStart > atom.getZ() - radius) {
            zStart = atom.getZ() - radius;
          }
          if (xEnd < atom.getX() + radius) {
            xEnd = atom.getX() + radius;
          }
          if (yEnd < atom.getY() + radius) {
            yEnd = atom.getY() + radius;
          }
          if (zEnd < atom.getZ() + radius) {
            zEnd = atom.getZ() + radius;
          }
        }
      }
    }

    logger.info("Solvation box: \nX: " + xStart + " Xm:" + xEnd
        + "\nY: " + yStart + " Ym:" + yEnd + "\nZ: " + zStart
        + " Zm:" + zEnd);

    // -- Determine size of solvent
    Float XYZ[] = (Float[]) refMol.getProperty(MoleculeInterface.PeriodicBox);
    if (XYZ == null) {
      XYZ = new Float[3];
      for (int i = 0; i < solvent.getNumberOfAtoms(); i++) {
        AtomInterface atom = solvent.getAtomInterface(i);
        if (i == 0) {
          XYZ[0] = atom.getX();
          XYZ[1] = atom.getY();
          XYZ[2] = atom.getZ();
        } else {
          if (XYZ[0] > atom.getX()) {
            XYZ[0] = atom.getX();
          }
          if (XYZ[1] > atom.getY()) {
            XYZ[1] = atom.getY();
          }
          if (XYZ[2] > atom.getZ()) {
            XYZ[2] = atom.getZ();
          }
        }
      }
      for (int i = 0; i < solvent.getNumberOfAtoms(); i++) {
        AtomInterface atom = solvent.getAtomInterface(i);
        atom.setX(atom.getX() - XYZ[0]);
        atom.setY(atom.getY() - XYZ[1]);
        atom.setZ(atom.getZ() - XYZ[2]);
      }

      for (int i = 0; i < solvent.getNumberOfAtoms(); i++) {
        AtomInterface atom = solvent.getAtomInterface(i);
        if (i == 0) {
          XYZ[0] = atom.getX();
          XYZ[1] = atom.getY();
          XYZ[2] = atom.getZ();
        } else {
          if (XYZ[0] < atom.getX()) {
            XYZ[0] = atom.getX();
          }
          if (XYZ[1] < atom.getY()) {
            XYZ[1] = atom.getY();
          }
          if (XYZ[2] < atom.getZ()) {
            XYZ[2] = atom.getZ();
          }
        }
      }

    }

    logger.info("Solvent size: X: " + XYZ[0] + "  Y: " + XYZ[1]
        + "  Z: " + XYZ[2]);

    // --- Start solvation
    if (solvationType == OperationsOnAtoms.SELECTED_SOLVATE_CAP) {
      float xCount = xStart;
      while (xCount < xEnd) {
        float yCount = yStart;
        while (yCount < yEnd) {
          float zCount = zStart;
          while (zCount < zEnd) {
            // --- Translate original solvent box
            MoleculeInterface box = refMol.getInstance();
            box.appendMolecule(solvent);
            for (int i = 0; i < box.getNumberOfAtoms(); i++) {
              AtomInterface atom = box.getAtomInterface(i);
              atom.setX(atom.getX() + xCount);
              atom.setY(atom.getY() + yCount);
              atom.setZ(atom.getZ() + zCount);
            }

            for (int i = 0; i < box.getNumberOfMonomers(); i++) {
              MonomerInterface monomer = box.getMonomerInterface(i);
              if (!isHeavyAtomsWithinRange(point, monomer, radius)) {
                continue;
              }

              if (isWithinWanDerWaalsRange(refMol, monomer, closeness)) {
                continue; // Too close
              }

              Molecule.addMonomer(shell, monomer);
            }

            zCount += XYZ[2];
          }
          yCount += XYZ[1];
        }
        xCount += XYZ[0];
      }
    } // --- Make shell solvation
    else if (solvationType == OperationsOnAtoms.SELECTED_SOLVATE_SHELL) {

      // --- Prepare list of selected atoms
      List atomList = new ArrayList();
      for (int i = 0; i < refMol.getNumberOfAtoms(); i++) {
        AtomInterface atom = refMol.getAtomInterface(i);
        if (!atom.isSelected()) {
          continue;
        }
        atomList.add(atom);
      }

      float xCount = xStart;
      while (xCount < xEnd) {
        float yCount = yStart;
        while (yCount < yEnd) {
          float zCount = zStart;
          while (zCount < zEnd) {
            // --- Translate original solvent box
            MoleculeInterface box = refMol.getInstance();
            box.appendMolecule(solvent);
            for (int i = 0; i < box.getNumberOfAtoms(); i++) {
              AtomInterface atom = box.getAtomInterface(i);
              atom.setX(atom.getX() + xCount);
              atom.setY(atom.getY() + yCount);
              atom.setZ(atom.getZ() + zCount);
            }

            for (int i = 0; i < box.getNumberOfMonomers(); i++) {
              MonomerInterface monomer = box.getMonomerInterface(i);

              // --- Go through the list of selected atoms
              boolean NotWithinRange = true;
              for (int j = 0; j < atomList.size(); j++) {
                AtomInterface at = (AtomInterface) atomList.get(j);
                if (isHeavyAtomsWithinRange(at, monomer, radius)) {
                  NotWithinRange = false;
                  break;
                }

              }
              if (NotWithinRange) {
                continue;
              }

              // --- Check closeness
              if (isWithinWanDerWaalsRange(refMol, monomer, closeness)) {
                continue; // Too close
              }

              Molecule.addMonomer(shell, monomer);
            }

            zCount += XYZ[2];
          }
          yCount += XYZ[1];
        }
        xCount += XYZ[0];
      }

    } else {
      System.err.println("Uknown solvation type");
      return null;
    }

    // --- Test
    float x0 = 0, y0 = 0, z0 = 0, x1 = 0, y1 = 0, z1 = 0;
    for (int i = 0; i < shell.getNumberOfAtoms(); i++) {
      AtomInterface atom = shell.getAtomInterface(i);
      if (i == 0) {
        x0 = atom.getX();
        y0 = atom.getY();
        z0 = atom.getZ();
        x1 = atom.getX();
        y1 = atom.getY();
        z1 = atom.getZ();
      } else {
        x0 = x0 > atom.getX() ? atom.getX() : x0;
        y0 = y0 > atom.getY() ? atom.getY() : y0;
        z0 = z0 > atom.getZ() ? atom.getZ() : z0;
        x1 = x1 < atom.getX() ? atom.getX() : x1;
        y1 = y1 < atom.getY() ? atom.getY() : y1;
        z1 = z1 < atom.getZ() ? atom.getZ() : z1;
      }
    }
    logger.info("Test: Shell min: X: " + x0 + "  Y: "
        + y0
        + "  Z: " + z0 + " Max: X: " + x1 + "  Y: "
        + y1
        + "  Z: " + z1);
    // --- End Test

    return shell;
  }

  public static String geometryAsString(MoleculeInterface molecule) {
    if (molecule == null) {
      return "Molecule is not allocated";
    }

    StringWriter sWriter = new StringWriter();
    sWriter.write(molecule.getName() + "\n\n");
    sWriter.write("Number of atoms: " + molecule.getNumberOfAtoms() + "\n\n");
    sWriter.write("   N   Name       X             Y             Z\n");
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface atom = molecule.getAtomInterface(i);
      sWriter.write(String.format(" %4d %6s %12.6f  %12.6f  %12.6f\n", (i + 1), atom.getName(), atom.getX(), atom.getY(),
          atom.getZ()));
    }
    sWriter.write("\n");

    sWriter.write("Number of bonds: " + molecule.getNumberOfBonds() + "\n\n");
    for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
      BondInterface bond = molecule.getBondInterface(i);
      AtomInterface a_1 = bond.getIAtomInterface();
      AtomInterface a_2 = bond.getJAtomInterface();

      sWriter.write(String.format(" %4d %4s(%d) - %4s(%d) %12.6f\n", (i + 1), a_1.getName(), molecule.getAtomIndex(a_1) + 1,
          a_2.getName(), molecule.getAtomIndex(a_2) + 1, a_1.distanceTo(a_2)));
    }
    sWriter.write("\n");

    AngleBendsArray angles = FFMolecule.findDynamicAngles(molecule);
    sWriter.write("Number of covalent angles: " + angles.getSize() + "\n\n");

    for (int i = 0; i < angles.getSize(); i++) {
      AngleBend ab = angles.getAngleBend(i);
      AtomInterface a1 = molecule.getAtomInterface(ab.getI());
      AtomInterface a2 = molecule.getAtomInterface(ab.getJ());
      AtomInterface a3 = molecule.getAtomInterface(ab.getK());
      sWriter.write(String.format(" %4d %4s(%d) - %4s(%d) - %4s(%d) %12.6f\n", (i + 1), a1.getName(), (ab.getI() + 1),
          a2.getName(), (ab.getJ() + 1), a3.getName(), (ab.getK() + 1),
          Point3f.angleBetween(a1, a2, a3) * Constants.RADIANS_TO_DEGREES));
    }
    sWriter.write("\n");

    List torsions = FFMolecule.findDynamicTorsions(molecule);
    sWriter.write("Number of torsions: " + torsions.size() + "\n\n");
    for (int i = 0; i < torsions.size(); i++) {
      Torsion torsion = (Torsion) torsions.get(i);
      AtomInterface a1 = molecule.getAtomInterface(torsion.getI());
      AtomInterface a2 = molecule.getAtomInterface(torsion.getJ());
      AtomInterface a3 = molecule.getAtomInterface(torsion.getK());
      AtomInterface a4 = molecule.getAtomInterface(torsion.getL());
      sWriter.write(String.format(" %4d %4s(%d) - %4s(%d) - %4s(%d)  - %4s(%d) %12.6f\n", (i + 1), a1.getName(),
          (torsion.getI() + 1),
          a2.getName(), (torsion.getJ() + 1), a3.getName(), (torsion.getK() + 1), a4.getName(),
          (torsion.getL() + 1),
          Point3f.dihedralAngle(a1, a2, a3, a4) * Constants.RADIANS_TO_DEGREES));

    }

    return sWriter.toString();
  }

  public AtomInterface getAtom(int i) {
    return Atoms.getAtom(i);
  }

  public int getAtomIndex(AtomInterface a) {
    return Atoms.getAtomIndex(a);
  }

  public AtomInterface getAtomInterface(int i) {
    return Atoms.getAtom(i);
  }

  public Set<AtomInterface> getAtomListUsingAtomMaskSelection(String atomSelectionMask) throws Exception {
    return getAtomListUsingAtomMaskSelection(this, atomSelectionMask);
  }

  public static Set<AtomInterface> getAtomListUsingAtomMaskSelection(MoleculeInterface mol, String atomSelectionMask) throws Exception {
    if (mol == null || mol.getNumberOfAtoms() < 1) {
      logger.warning("mol == null || mol.getNumberOfAtoms() < 1");
    } else if (atomSelectionMask == null || atomSelectionMask.trim().length() < 1) {
      logger.warning("atomSelectionMask == null || atomSelectionMask.trim().length() < 1");
      return null;
    }

    Set<AtomInterface> set = new HashSet<AtomInterface>();
    String[] tokens = atomSelectionMask.split("\\s+");
    for (String mask : tokens) {
      String[] atomic = mask.split(",");
      for (String xxx : atomic) {
        if (xxx.contains("-")) { // Atom range
          String at1 = xxx.substring(0, xxx.indexOf("-"));
          if (at1.length() < 1) {
            throw new Exception("Wrong first atom range number description in a mask \"" + atomSelectionMask);
          } else if (xxx.indexOf("-") + 1 == xxx.length()) {
            throw new Exception("Absent the second atom in a range number description in a mask \"" + atomSelectionMask);
          }
          String at2 = xxx.substring(xxx.indexOf("-") + 1);
          int a1, a2;
          try {
            a1 = Integer.parseInt(at1);
            if (a1 < 1 || a1 > mol.getNumberOfAtoms()) {
              throw new Exception("Wrong atom number \"" + a1 + "\" in mask \"" + atomSelectionMask + "\"\n"
                  + "Should be in range 1 - " + mol.getNumberOfAtoms());
            }
          } catch (Exception ex) {
            throw new Exception("Cannot parse atom number \"" + at1 + "\" in mask \"" + atomSelectionMask + "\"");
          }
          try {
            a2 = Integer.parseInt(at2);
            if (a2 < 1 || a2 > mol.getNumberOfAtoms()) {
              throw new Exception("Wrong atom number \"" + a2 + "\" in mask \"" + atomSelectionMask + "\"\n"
                  + "Should be in range 1 - " + mol.getNumberOfAtoms());
            } else if (a2 < a1) {
              throw new Exception("The second atom number \"" + a2 + "\" should be bigger than of the first one: " + a1);
            }
            for (int i = a1; i <= a2; i++) {
              set.add(mol.getAtomInterface(i - 1));
            }
          } catch (Exception ex) {
            throw new Exception("Cannot parse atom number \"" + at1 + "\" in mask \"" + atomSelectionMask + "\"");
          }

        } else { // Individual number
          try {
            int at = Integer.parseInt(xxx);
            if (at < 1 || at > mol.getNumberOfAtoms()) {
              throw new Exception("Wrong atom number \"" + at + "\" in mask \"" + atomSelectionMask + "\"\n"
                  + "Should be in range 1 - " + mol.getNumberOfAtoms());
            }
            set.add(mol.getAtomInterface(at - 1));
          } catch (Exception ex) {
            throw new Exception("Cannot parse atom number \"" + xxx + "\" in mask \"" + atomSelectionMask + "\"");
          }
        }
      }
    }

    return set;
  }

  public Set<String> getAtomNamesInMolecule() {
    return getAtomNamesInMolecule(this);
  }

  public static Set<String> getAtomNamesInMolecule(MoleculeInterface molec) {
    Set<String> elements = new HashSet<String>();
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface a = molec.getAtomInterface(i);
      String a_name = (String) a.getProperty(AtomInterface.NAME);
      if (!elements.contains(a_name)) {
        elements.add(a_name);
      }
    }
    return elements;
  }

  public static List getAvailableAtomicProperties(MoleculeInterface molec) {
    List prop = new ArrayList();
    Map unique = new HashMap();
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      Map ap = atom.getProperties();
      //.getAtomProperties();
      Set set = ap.entrySet();
      Iterator iter = set.iterator();
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        String key = me.getKey().toString();
        if (key.startsWith("_")) {
          continue;
        }
        if (!unique.containsKey(key)) {
          prop.add(key);
          unique.put(key, null);
        }
      }
    }
    return prop;
  }

  public Bond getBond(int i) {
    return (Bond) Bonds.get(i);
  }

  public int getBond(BondInterface b) {
    return Bonds.indexOf(b);
  }

  public int getBondIndex(BondInterface b) {
    return Bonds.indexOf(b);
  }

  public BondInterface getBondInterface(int i) {
    return (BondInterface) Bonds.get(i);
  }

  public static AtomInterface getCenterOfMass(Set<AtomInterface> list) throws Exception {
    AtomInterface centroid = new Atom();
    centroid.setAtomicNumber(0);
    double mass = 0.0f;
    for (AtomInterface atom : list) {
      centroid.setXYZ(atom.getX() * atom.getAtomicMass() + centroid.getX(),
          atom.getY() * atom.getAtomicMass() + centroid.getY(),
          atom.getZ() * atom.getAtomicMass() + centroid.getZ());
      mass += atom.getAtomicMass();
    }
    centroid.setAtomicMass((float) mass);
    centroid.setXYZ(centroid.getX() / mass, centroid.getY() / mass, centroid.getZ() / mass);
    return centroid;
  }

  public AtomInterface getCenterOfMass() throws Exception {
    return getCenterOfMass(this);
  }

  public static AtomInterface getCenterOfMass(MoleculeInterface mol) throws Exception {
    AtomInterface centroid = new Atom();
    centroid.setAtomicNumber(0);
    double mass = 0.0f;
    for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
      AtomInterface atom = mol.getAtomInterface(i);
      centroid.setXYZ(atom.getX() * atom.getAtomicMass() + centroid.getX(),
          atom.getY() * atom.getAtomicMass() + centroid.getY(),
          atom.getZ() * atom.getAtomicMass() + centroid.getZ());
      mass += atom.getAtomicMass();
    }
    centroid.setXYZ(centroid.getX() / mass,
        centroid.getY() / mass,
        centroid.getZ() / mass);
    centroid.setAtomicMass((float) mass);
    return centroid;
  }

  public static AtomInterface getCentroid(Set<AtomInterface> list) throws Exception {
    AtomInterface centroid = new Atom();
    centroid.setAtomicNumber(0);
    for (AtomInterface atom : list) {
      centroid.setXYZ(atom.getX() + centroid.getX(), atom.getY() + centroid.getY(), atom.getZ() + centroid.getZ());
    }
    centroid.setXYZ(centroid.getX() / list.size(), centroid.getY() / list.size(), centroid.getZ() / list.size());
    return centroid;
  }

  public AtomInterface getCentroid() throws Exception {
    return getCentroid(this);
  }

  public static AtomInterface getCentroid(MoleculeInterface mol) throws Exception {
    AtomInterface centroid = new Atom();
    centroid.setAtomicNumber(0);
    for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
      AtomInterface atom = mol.getAtomInterface(i);
      centroid.setXYZ(atom.getX() + centroid.getX(),
          atom.getY() + centroid.getY(),
          atom.getZ() + centroid.getZ());
    }
    centroid.setXYZ(centroid.getX() / mol.getNumberOfAtoms(),
        centroid.getY() / mol.getNumberOfAtoms(),
        centroid.getZ() / mol.getNumberOfAtoms());
    return centroid;
  }

  public static String getChemicalFormula(MoleculeInterface molec) {
    String formula = "";
    int count[] = new int[ChemicalElements.getNumberOfElements()];
    for (int i = 0; i < count.length; i++) {
      count[i] = 0;
    }

    // --- Counting atoms
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      int element = atom.getAtomicNumber();
      count[element] += 1;
    }
    // --- Forming chemical formula
    for (int i = 1; i < count.length; i++) {
      if (count[i] > 0) {
        formula += ChemicalElements.getElementSymbol(i);
      }
      if (count[i] > 1) {
        formula += String.valueOf(count[i]);
      }
    }
    formula = formula.toUpperCase();
    return formula;
  }

  public static boolean getCoordinates(MoleculeInterface molec, Point3f coord[]) {
    if (coord.length < molec.getNumberOfAtoms()) {
      logger.info(
          "INTERNAL ERROR: getCoordinates: coord.length < this.getNumberOfAtoms()");
      return false;
    }
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface a = molec.getAtomInterface(i);
      if (coord[i] == null) {
        coord[i] = new Point3f(a);
      } else {
        coord[i].setXYZ(a);
      }
    }
    return true;
  }

  public static int[] getElementCount(MoleculeInterface molec) {
    int count[] = new int[ChemicalElements.getNumberOfElements()];
    for (int i = 0; i < count.length; i++) {
      count[i] = 0;
    }

    // --- Counting atoms
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      int element = atom.getAtomicNumber();
      count[element] += 1;
    }
    return count;
  }

  /**
   * Calculate number of each element in a molecule
   * @param molec
   * @return Map<String, Integer>, i.e pairs, for example, {C,5}, {H,5}
   */
  public static Map<String, Integer> getElementCountAsMap(MoleculeInterface molec) {
    Map<String, Integer> els = new HashMap<String, Integer>(molec.getNumberOfAtoms());

    // --- Counting atoms
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      String element = ChemicalElements.getElementSymbol(atom.getAtomicNumber());
      if (els.containsKey(element)) {
        els.put(element, new Integer(els.get(element) + 1));
      } else {
        els.put(element, new Integer(1));
      }
    }
    return els;
  }

  public Set<String> getElementsInMolecule() {
    return getElementsInMolecule(this);
  }

  public static Set<String> getElementsInMolecule(MoleculeInterface molec) {
    Set<String> elements = new HashSet<String>();
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface a = molec.getAtomInterface(i);
      int el = a.getAtomicNumber();
      String mnemonic = ChemicalElements.getElementSymbol(el);
      if (!elements.contains(mnemonic)) {
        elements.add(mnemonic);
      }
    }
    return elements;
  }

  public MoleculeInterface getInstance() {
    return new Molecule();
  }

  public List getMolecularSubstructure() {
    List monomers = new ArrayList(this.getNumberOfMonomers());
    for (int i = 0; i < getNumberOfMonomers(); i++) {
      Monomer m = this.getMonomer(i);
      String monomer_name = m.getName();
      monomers.add(monomer_name);
    }
    return monomers;
  }

  public static float getMolecularWeight(MoleculeInterface molec) {
    float weight = 0.0f;
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      weight += ChemicalElements.getAtomicWeight(atom.getAtomicNumber());
    }
    return weight;
  }

  public List<List<AtomInterface>> getMolecules() {
    return getMolecules(this);
  }

  public static List<List<AtomInterface>> getMolecules(MoleculeInterface molecule) {

    int natoms;
    if (molecule == null || (natoms = molecule.getNumberOfAtoms()) < 1) {
      return null;
    }

    List<List<AtomInterface>> molecules = new ArrayList<List<AtomInterface>>();
    HashSet<AtomInterface> track = new HashSet<AtomInterface>(natoms); // Temporary storage

    for (int i = 0; i < natoms; i++) {
      AtomInterface atom = molecule.getAtomInterface(i);
      if (track.contains(atom)) {
        continue;
      }
      List<AtomInterface> list = getMoleculeAtomBelongsTo(atom);
      molecules.add(list);
      for (AtomInterface at : list) { // Track atoms
        track.add(at);
      }
    }

    track.clear();
    track = null;
    return molecules;
  }

  public static List<AtomInterface> getMoleculeAtomBelongsTo(AtomInterface target_atom) {
    List group = new ArrayList<AtomInterface>();

    // --- Some special cases...
    if (target_atom.getNumberOfBondedAtoms() == 0) {
      group.add(target_atom);
      return group;
    }

    // --- General case
    findAllBondedAtoms(target_atom, group);

    return group;
  }

  public Monomer getMonomer(int n) {
    return Atoms.getMonomer(n);
  }

  public MonomerInterface getMonomerInterface(int n) {
    return Atoms.getMonomer(n);
  }

  public String getName() {
    return molName;
  }

  public AtomInterface getNewAtomInstance() throws NullPointerException {
    if (atomInterface == null && this.getNumberOfAtoms() == 0) {
      throw new NullPointerException("Atom Interface is not defined");
    } else if (atomInterface == null) {
      AtomInterface a = this.getAtomInterface(0);
      return a.getNewAtomInstance();
    }
    return atomInterface.getNewAtomInstance();
  }

  public BondInterface getNewBondInstance() throws NullPointerException {
    if (bondInterface == null && this.getNumberOfBonds() == 0) {
      throw new NullPointerException("Bond Interface is not defined");
    } else if (bondInterface == null) {
      BondInterface b = this.getBondInterface(0);
      return b.getNewBondInstance();
    }
    return bondInterface.getNewBondInstance();
  }

  public BondInterface getNewBondInstance(AtomInterface a, AtomInterface b) throws
      NullPointerException {
    if (bondInterface == null && this.getNumberOfBonds() == 0) {
      throw new NullPointerException("Bond Interface is not defined");
    } else if (bondInterface == null) {
      BondInterface bb = this.getBondInterface(0);
      return bb.getNewBondInstance(a, b);
    }

    return bondInterface.getNewBondInstance(a, b);

  }

  public static MoleculeInterface getNewInstance() {
    return new Molecule();
  }

  public int getNumberOfAtoms() {
    return Atoms.size();
  }

  public int getNumberOfBonds() {
    return Bonds.size();
  }

  public int getNumberOfMonomers() {
    return Atoms.getNumberOfMonomers();
  }

  public Map getProperties() {
    return Properties;
  }

  public Object getProperty(String key) {
    return Properties.get(key);
  }

  public Set<String> getUniqueMonomersInMolecule() {
    Set<String> monomers = new HashSet<String>();
    for (int i = 0; i < getNumberOfMonomers(); i++) {
      Monomer m = this.getMonomer(i);
      String monomer_name = m.getName();
      if (!monomers.contains(monomer_name)) {
        monomers.add(monomer_name);
      }
    }
    return monomers;
  }

  public static Set<String> getUniqueMonomersInMolecule(MoleculeInterface molec) {
    Set<String> monomers = new HashSet<String>();
    for (int i = 0; i < molec.getNumberOfMonomers(); i++) {
      MonomerInterface m = molec.getMonomerInterface(i);
      String monomer_name = m.getName();
      if (!monomers.contains(monomer_name)) {
        monomers.add(monomer_name);
      }
    }
    return monomers;
  }

  static public MoleculeInterface getWaterMolecule() {
    MoleculeInterface m = new Molecule();
    AtomInterface a = m.getNewAtomInstance();
    a.setAtomicNumber(8);
    a.setName("O");
    a.setXYZ(-0.213333f, -0.301699f, 0.000000f);
    m.addAtom(a);

    a = m.getNewAtomInstance();
    a.setAtomicNumber(1);
    a.setName("H1");
    a.setXYZ(0.746667f, -0.301699f, 0.000000f);
    m.addAtom(a);

    a = m.getNewAtomInstance();
    a.setAtomicNumber(1);
    a.setName("H2");
    a.setXYZ(-0.533333f, 0.603398f, 0.000000f);
    m.addAtom(a);

    Molecule.guessCovalentBonds(m);

    return m;
  }

  public float getXmax() {
    if (!boundary_done) {
      findBoundaries();
    }
    return Xmax;
  }

  public float getXmin() {
    if (!boundary_done) {
      findBoundaries();
    }
    return Xmin;
  }

  public float getYmax() {
    if (!boundary_done) {
      findBoundaries();
    }
    return Ymax;
  }

  public float getYmin() {
    if (!boundary_done) {
      findBoundaries();
    }
    return Ymin;
  }

  public float getZmax() {
    if (!boundary_done) {
      findBoundaries();
    }
    return Zmax;
  }

  public float getZmin() {
    if (!boundary_done) {
      findBoundaries();
    }
    return Zmin;
  }

  public static void guessAtomTypes(MoleculeInterface molec) {
    guessAtomTypes(molec, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
  }

  public static void guessAtomTypes(MoleculeInterface molec, String propName, Map elementMapping) {
    if (molec == null || molec.getNumberOfAtoms() < 1) {
      System.err.println(
          "guessAtomTypes: molec == null || molec.getNumberOfAtoms() < 1: Ignored...");
      return;
    }
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface a = molec.getAtomInterface(i);
      String type = guessAtomType(a, propName, elementMapping);
      if (type != null) {
        a.setProperty(propName, type);
      }
    }
  }

  public static String guessAtomType(AtomInterface atom, String propName, Map elementMapping) {

    String element = ChemicalElements.getElementSymbol(atom.getAtomicNumber());
    //logger.info("Atom "+i+" element: "+element);
    if (!elementMapping.containsKey(element)) {
      //logger.info("No mapping for element: " + element);
      return null;
    }

    Map map = (Map) elementMapping.get(element);
    //logger.info("Possible atom types for this element: "+map.size());

    Set set = map.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String Key = me.getKey().toString();
      CCTAtomTypes Type = (CCTAtomTypes) me.getValue();
      //logger.info("Type: "+Key+" coord number: "+Type.getCoordinationNumber()+" ligands: "+a.getNumberOfBondedAtoms());
      if (Type.getCoordinationNumber() == atom.getNumberOfBondedAtoms()) {
        //atom.setProperty(propName, Key);
        return Key;
      }
    }
    return null;
  }

  public static void redoConnectivity(MoleculeInterface molec) {
    for (int i = molec.getNumberOfBonds() - 1; i > -1; i--) {
      BondInterface bond = molec.getBondInterface(i);
      molec.deleteBond(bond);
      guessCovalentBonds(molec);
    }
  }

  public void guessCovalentBonds() {
    guessCovalentBonds(this);
  }

  /**
   * Tries to find all covalent bonds in the molecule based on the tabulated values of the covalent radii of atoms
   *
   * @param molec
   */
  public static void guessCovalentBonds(MoleculeInterface molec) {
    if (molec == null) {
      System.err.println("guessCovalentBonds: molecule is null");
      return;
    }
    for (int i = 0; i < molec.getNumberOfAtoms() - 1; i++) {
      //gAtom a_i = Atoms.getAtom(i);
      AtomInterface a_i = molec.getAtomInterface(i);
      if (a_i.getAtomicNumber() == 0) {
        continue; // Dummy atom
      }
      float radius_i = ChemicalElements.getCovalentRadius(a_i.getAtomicNumber());
      for (int j = i + 1; j < molec.getNumberOfAtoms(); j++) {
        AtomInterface a_j = molec.getAtomInterface(j);
        if (a_j.getAtomicNumber() == 0) {
          continue; // Dummy atom
        }
        float radius_j = ChemicalElements.getCovalentRadius(a_j.getAtomicNumber());
        double dist = a_i.distanceTo(a_j);
        if (dist < 0.001) {
          System.err.println("guessCovalentBonds: distance: " + dist
              + " - bond is not created");
        } else if (dist <= radius_i + radius_j) {
          //createNewBond(a_i, a_j);
          BondInterface bond = molec.getNewBondInstance(a_i, a_j);
          molec.addBond(bond);
        }
      }
    }
    //logger.info("Found " + molec.getNumberOfBonds() +
    //                   " covalent bonds");
  }

  /**
   *
   * @param n int
   * @param m int
   */
  public void guessCovalentBondsBetweenMonomers(int n, int m) {
    if (n < 0 || n >= getNumberOfMonomers()) {
      logger.info(
          "INTERNAL ERROR: guessCovalentBondsInMonomer: n < 0 || n >= getNumberOfMonomers()");
      return;
    }
    if (m < 0 || m >= getNumberOfMonomers()) {
      logger.info(
          "INTERNAL ERROR: guessCovalentBondsInMonomer: m < 0 || m >= getNumberOfMonomers()");
      return;
    }

    Monomer mono_1 = getMonomer(n);
    Monomer mono_2 = getMonomer(m);

    for (int i = 0; i < mono_1.getNumberOfAtoms(); i++) {
      AtomInterface a_i = mono_1.getAtom(i);
      if (a_i.getAtomicNumber() == 0) {
        continue; // Dummy atom
      }
      float radius_i = ChemicalElements.getCovalentRadius(a_i.getAtomicNumber());
      for (int j = 0; j < mono_2.getNumberOfAtoms(); j++) {
        AtomInterface a_j = mono_2.getAtom(j);
        if (a_j.getAtomicNumber() == 0) {
          continue; // Dummy atom
        }
        float radius_j = ChemicalElements.getCovalentRadius(a_j.getAtomicNumber());
        if (a_i.distanceTo(a_j) <= radius_i + radius_j) {
          createNewBond(a_i, a_j);
        }
      }
    }
    //logger.info("Found " + Bonds.size() + " covalent bonds");
  }

  public void guessCovalentBondsInMonomer(int n) {
    if (n < 0 || n >= getNumberOfMonomers()) {
      logger.info(
          "INTERNAL ERROR: guessCovalentBondsInMonomer: n < 0 || n >= getNumberOfMonomers()");
      return;
    }
    Monomer mono = getMonomer(n);

    for (int i = 0; i < mono.getNumberOfAtoms() - 1; i++) {
      AtomInterface a_i = mono.getAtom(i);
      if (a_i.getAtomicNumber() == 0) {
        continue; // Dummy atom
      }
      float radius_i = ChemicalElements.getCovalentRadius(a_i.getAtomicNumber());
      for (int j = i + 1; j < mono.getNumberOfAtoms(); j++) {
        AtomInterface a_j = mono.getAtom(j);
        if (a_j.getAtomicNumber() == 0) {
          continue; // Dummy atom
        }
        float radius_j = ChemicalElements.getCovalentRadius(a_j.getAtomicNumber());
        if (a_i.distanceTo(a_j) <= radius_i + radius_j) {
          createNewBond(a_i, a_j);
        }
      }
    }
    //logger.info("Found " + Bonds.size() + " covalent bonds");
  }

  public static int guessTotalCharge(MoleculeInterface molec,
      int multiplicity) throws Exception {
    if (multiplicity < 1) {
      throw new Exception("Multiplicity cannot be negative!");
    }

    int n_electrons = 0;
    // --- Count number of electrons
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      int element = atom.getAtomicNumber();
      n_electrons += element;
    }
    int unpaired = n_electrons % 2;

    if (unpaired == 0 && (multiplicity % 2 == 1)) { // Even number of electrons & odd multiplicity
      return 0;
    } else if (unpaired == 0 && (multiplicity % 2 == 0)) { // Even number of electrons & even multiplicity
      return -1;
    } else if (unpaired == 1 && (multiplicity % 2 == 0)) { // Odd number of electrons & even multiplicity
      return 0;
    }

    // Odd number of electrons & odd multiplicity
    return -1;
  }

  public static int guessTotalCharge(int elements[], int multiplicity) throws Exception {
    if (multiplicity < 1) {
      throw new Exception("Multiplicity cannot be negative!");
    }

    int n_electrons = 0;
    // --- Count number of electrons
    for (int i = 0; i < elements.length; i++) {
      n_electrons += elements[i];
    }
    int unpaired = n_electrons % 2;

    if (unpaired == 0 && (multiplicity % 2 == 1)) { // Even number of electrons & odd multiplicity
      return 0;
    } else if (unpaired == 0 && (multiplicity % 2 == 0)) { // Even number of electrons & even multiplicity
      return -1;
    } else if (unpaired == 1 && (multiplicity % 2 == 0)) { // Odd number of electrons & even multiplicity
      return 0;
    }

    // Odd number of electrons & odd multiplicity
    return -1;
  }

  public boolean hasAtom(Atom atom) {
    return Atoms.hasAtom(atom);
  }

  public static boolean isHeavyAtomsWithinRange(Point3fInterface point,
      MonomerInterface monomer,
      float radius) {
    for (int i = 0; i < monomer.getNumberOfAtoms(); i++) {
      AtomInterface atom = monomer.getAtom(i);
      if (atom.getAtomicNumber() == 1 || atom.getAtomicNumber() == 0) {
        continue;
      }
      if (Point3f.distance(point, atom) <= (double) radius) {
        return true;
      }
    }
    return false;
  }

  public static boolean isMatchChargeAndMultiplicity(MoleculeInterface molec,
      int charge, int mult) {
    if (mult < 1) {
      return false;
    }
    int n_electrons = 0;
    // --- Count number of electrons
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      int element = atom.getAtomicNumber();
      n_electrons += element;
    }
    int unpaired = (n_electrons - charge) % 2;
    if (unpaired == 0 && mult % 2 == 1) { // Even number of electrons
      return true;
    } else if (unpaired == 1 && mult % 2 == 0) { // Odd number of electrons
      return true;
    }
    return false;
  }

  public static boolean isWithinWanDerWaalsRange(MoleculeInterface molec,
      MonomerInterface monomer,
      float closeness) {
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      int el = atom.getAtomicNumber();
      if (el < 1) {
        continue; // Dummy atom
      }
      float vdw = ChemicalElements.getVanDerWaalsRadius(el);
      for (int j = 0; j < monomer.getNumberOfAtoms(); j++) {
        AtomInterface m_atom = monomer.getAtom(j);
        int m_el = m_atom.getAtomicNumber();
        if (m_el < 1) {
          continue; // Dummy atom
        }
        float m_vdw = ChemicalElements.getVanDerWaalsRadius(m_el);

        if (Point3f.distance(m_atom, atom)
            < (double) (closeness * (vdw + m_vdw))) {
          return true;
        }
      }
    }
    return false;
  }

  static public void makeInternalCoordinates(Molecule mol) throws Exception {
    if (mol.getNumberOfAtoms() < 2) {
      return;
    }
    VecmathTools.makeInternalCoord(mol.getAtom(1), mol.getAtom(0), null, null, mol.getAtom(1).getZmatrix());
    if (mol.getNumberOfAtoms() < 3) {
      return;
    }
    int[] ijk = mol.getAtom(2).getIJK();
    VecmathTools.makeInternalCoord(mol.getAtom(2), mol.getAtom(ijk[0]), mol.getAtom(ijk[1]), null,
        mol.getAtom(2).getZmatrix());
    if (mol.getNumberOfAtoms() < 4) {
      return;
    }
    for (int i = 3; i < mol.getNumberOfAtoms(); i++) {
      ijk = mol.getAtom(i).getIJK();
      VecmathTools.makeInternalCoord(mol.getAtom(i), mol.getAtom(ijk[0]), mol.getAtom(ijk[1]), mol.getAtom(ijk[2]),
          mol.getAtom(i).getZmatrix());
    }
  }

  public void markAtomAsSelected(int n, boolean mark) {
    if (n < 0 || n >= Atoms.size()) {
      return;
    }
    AtomInterface a = Atoms.getAtom(n);
    a.setSelected(mark);
  }

  public void mergeMolecule(MoleculeInterface mol) {
    if (mol == null || mol.getNumberOfAtoms() == 0) {
      System.err.println("Warning: mergeMolecule: empty molecule to append");
      return;
    }

    int oldNumMonomers = this.getNumberOfMonomers();
    for (int i = 0; i < mol.getNumberOfMonomers(); i++) {
      MonomerInterface mono = mol.getMonomerInterface(i);
      addMonomer(mono.getName());
      for (int j = 0; j < mono.getNumberOfAtoms(); j++) {
        addAtom(mono.getAtom(j), oldNumMonomers + i);
      }
    }

    for (int i = 0; i < mol.getNumberOfBonds(); i++) {
      this.addBond(mol.getBondInterface(i));
    }
  }

  public static List noncyclicAtomGroup(AtomInterface origin, AtomInterface target) {
    List group = new ArrayList();

    // --- Some special cases...
    if (origin.getNumberOfBondedAtoms() == 0) {
      group.add(origin);
      return group;
    } else if (origin.getNumberOfBondedAtoms() == 1
        && origin.isBondedTo(target)) {
      group.add(origin);
      return group;
    }

    // --- General case
    if (noncyclicAtomGroup(origin, origin, target, group)) {
      return group;
    }

    group.clear();
    group.add(origin);

    List bonded = origin.getBondedToAtoms();
    for (int i = 0; i < bonded.size(); i++) {
      Atom a = (Atom) bonded.get(i);
      if (a != target && a.getNumberOfBondedAtoms() == 1) {
        group.add(a);
      }
    }

    return group;
  }

  private static boolean noncyclicAtomGroup(AtomInterface start, AtomInterface origin, AtomInterface target, List group) {
    if (start == target) {
      return false;
    }

    if (group.contains(start)) {
      return true;
    }
    group.add(start);

    List bonded = start.getBondedToAtoms();
    for (int i = 0; i < bonded.size(); i++) {
      AtomInterface a = (AtomInterface) bonded.get(i);
      if (a == target) {
        if (start != origin) {
          return false;
        }
        continue;
      }
      if (group.contains(a)) {
        continue;
      }
      if (noncyclicAtomGroup(a, origin, target, group)) {
        continue;
      } else {
        return false;
      }
    }
    return true;
  }

  public static List noncyclicAtomGroup(AtomInterface origin, AtomInterface target, AtomInterface target_2) {
    List group = new ArrayList();

    // --- Some special cases...
    if (origin.getNumberOfBondedAtoms() == 0) {
      group.add(origin);
      return group;
    } else if (origin.getNumberOfBondedAtoms() == 1
        && (origin.isBondedTo(target) || origin.isBondedTo(target_2))) {
      group.add(origin);
      return group;
    }

    // --- General case
    if (noncyclicAtomGroup(origin, origin, target, target_2, group)) {
      return group;
    }

    group.clear();
    group.add(origin);

    List bonded = origin.getBondedToAtoms();
    for (int i = 0; i < bonded.size(); i++) {
      Atom a = (Atom) bonded.get(i);
      if (a != target && a.getNumberOfBondedAtoms() == 1) {
        group.add(a);
      }
    }

    return group;
  }

  private static boolean noncyclicAtomGroup(AtomInterface start, AtomInterface origin, AtomInterface target, AtomInterface target_2,
      List group) {
    if (start == target || start == target_2) {
      return false;
    }

    if (group.contains(start)) {
      return true;
    }
    group.add(start);

    List bonded = start.getBondedToAtoms();
    for (int i = 0; i < bonded.size(); i++) {
      Atom a = (Atom) bonded.get(i);
      if (a == target) {
        if (start != origin) {
          return false;
        }
        continue;
      }
      if (group.contains(a)) {
        continue;
      }
      if (noncyclicAtomGroup(a, origin, target, target_2, group)) {
        continue;
      } else {
        return false;
      }
    }
    return true;
  }

  public int numberOfSelectedAtoms() {
    int nsel = 0;
    if (Atoms.size() < 1) {
      return nsel;
    }
    for (int i = 0; i < Atoms.size(); i++) {
      AtomInterface a = Atoms.getAtom(i);
      if (a.isSelected()) {
        ++nsel;
      }
    }
    return nsel;
  }

  public static void rotateAtomAroundAxis(MoleculeInterface molec,
      int atom_index, float theta,
      Point3fInterface p1,
      Point3fInterface p2) {
    AtomInterface atom = molec.getAtomInterface(atom_index);
    Geometry3d.rotatePointAroundArbitraryAxis(atom, theta, p1, p2);
    //logger.info("New XYZ coord: "+atom.getX()+atom.getY()+atom.getZ());
  }

  /**
   * Rotates atom around axis defined by points p1 & p2 by angle theta
   *
   * @param atom AtomInterface
   * @param theta float
   * @param p1 Point3fInterface
   * @param p2 Point3fInterface
   */
  public static void rotateAtomAroundAxis(AtomInterface atom, float theta,
      Point3fInterface p1,
      Point3fInterface p2) {

    Geometry3d.rotatePointAroundArbitraryAxis(atom, theta, p1, p2);
    //logger.info("New XYZ coord: "+atom.getX()+atom.getY()+atom.getZ());
  }

  public static void rotateAtomsAroundAxis(List group, float theta, Point3fInterface p1, Point3fInterface p2) {
    if (group == null || group.size() < 1 || theta == 0) {
      return;
    }

    AtomInterface atom = (AtomInterface) group.get(0);
    //vPoint3f origin = new Point3f(atom); // remember old atom position

    Geometry3d.rotatePointAroundArbitraryAxis(atom, theta, p1, p2);

    // --- get direction of translation
    //origin.x = atom.getX() - origin.x;
    //origin.y = atom.getY() - origin.y;
    //origin.z = atom.getZ() - origin.z;
    //float translation = origin.vectorNorm();
    //float d = atom.distanceToLine(p1,p2);
    //origin.normalize();
    //float factor = translation/d;
    for (int i = 1; i < group.size(); i++) { // Starting from the second atom
      atom = (Atom) group.get(i);
      //if (hasAtom(atom)) {
      Geometry3d.rotatePointAroundArbitraryAxis(atom, theta, p1, p2);
      //atom.translateAtom(origin, atom.distanceToLine(p1,p2)*factor);
      //logger.info("New atom "+i+" coord: "+atom.getX()+" "+atom.getY()+" "+atom.getZ());
      //}
      //else {
      //   logger.info(
      //       "INTERNAL ERROR: rotateAtomsAroundAxis: hasAtom(atom)");
      //}
    }
  }

  public void rotateMoleculeAroundAxis(float theta, Point3fInterface p1, Point3fInterface p2) {
    rotateMoleculeAroundAxis(this, theta, p1, p2);
  }

  /**
   * Rotates all atoms of molecule around axis p1-p2 by angle theta
   *
   * @param mol MoleculeInterface
   * @param theta float
   * @param p1 Point3fInterface
   * @param p2 Point3fInterface
   */
  public static void rotateMoleculeAroundAxis(MoleculeInterface mol, float theta,
      Point3fInterface p1,
      Point3fInterface p2) {
    if (mol == null || mol.getNumberOfAtoms() < 1 || theta == 0) {
      System.err.println("rotateMoleculeAroundAxis: mol == null || mol.getNumberOfAtoms() < 1 || theta == 0");
      return;
    }

    for (int i = 0; i < mol.getNumberOfAtoms(); i++) { // Starting from the second atom
      AtomInterface atom = mol.getAtomInterface(i);
      Geometry3d.rotatePointAroundArbitraryAxis(atom, theta, p1, p2);
    }
  }

  /**
   * Randomly rotates a molecule
   */
  public void rotateMoleculeRandomly() {
    rotateMoleculeRandomly(this);
  }

  /**
   * Randomly rotates a molecule
   *
   * @param mol
   */
  public static void rotateMoleculeRandomly(MoleculeInterface mol) {
    float theta = (float) (-Math.PI + Math.random() * 2.0 * Math.PI);
    Point3f p1 = new Point3f((float) (-1.0 + Math.random() * 2.0),
        (float) (-1.0 + Math.random() * 2.0),
        (float) (-1.0 + Math.random() * 2.0));
    Point3f p2 = new Point3f((float) (-1.0 + Math.random() * 2.0),
        (float) (-1.0 + Math.random() * 2.0),
        (float) (-1.0 + Math.random() * 2.0));
    rotateMoleculeAroundAxis(mol, theta, p1, p2);
  }

  /**
   * Selects (deselects) all atoms in molecule
   *
   * @param enable boolean "true" - select all, "false" - deselect all
   */
  public void selectAllAtoms(boolean enable) {
    for (int i = 0; i < getNumberOfAtoms(); i++) {
      AtomInterface a = getAtom(i);
      a.setSelected(enable);
    }
  }

  public static void selectAllAtoms(MoleculeInterface molec, boolean enable) {
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface a = molec.getAtomInterface(i);
      a.setSelected(enable);
    }
  }

  public void setAtomInterface(AtomInterface a_interface) {
    atomInterface = a_interface;
  }

  public void setBondInterface(BondInterface b_interface) {
    bondInterface = b_interface;
  }

  public static boolean setCoordinates(MoleculeInterface molec, Point3f coord[]) {
    if (molec == null || coord == null) {
      return false;
    }
    if (coord.length < molec.getNumberOfAtoms()) {
      logger.info(
          "INTERNAL ERROR: setCoordinates: coord.length < this.getNumberOfAtoms()");
      return false;
    }
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface a = molec.getAtomInterface(i);
      a.setXYZ(coord[i]);
    }
    return true;
  }

  public void setName(String name) {
    molName = name;
  }

  public static boolean toArchiveFormat(MoleculeInterface molec,
      OutputStream out) {
    if (molec.getNumberOfAtoms() < 1) {
      return false;
    }
    int offset = 0;
    String bufer = String.valueOf(molec.getNumberOfAtoms()) + "\n";
    int len = bufer.length();

    // -- Write number of atoms
    try {
      out.write(bufer.getBytes(), offset, len);
      //offset += len;
    } catch (IOException e) {
      logger.info(
          "toArchiveFormat: cannot write number of atoms\n");
      return false;
    }

    // --- Write atoms
    try {
      for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
        AtomInterface a = molec.getAtomInterface(i);
        bufer = a.getAtomicNumber() + "," + a.getName() + "," + a.getX()
            + "," + a.getY() + "," + a.getZ() + "\n";
        len = bufer.length();
        out.write(bufer.getBytes(), offset, len);
        //offset += len;
      }
    } catch (IOException e) {
      logger.info("toArchiveFormat: cannot write atom's info");
      return false;
    }

    // -- Write number of bonds
    bufer = String.valueOf(molec.getNumberOfBonds()) + "\n";
    try {
      out.write(bufer.getBytes(), offset, bufer.length());
      //offset += len;
    } catch (IOException e) {
      logger.info(
          "toArchiveFormat: cannot write number of bonds\n");
      return false;
    }

    // --- Write bonds
    try {
      for (int i = 0; i < molec.getNumberOfBonds(); i++) {
        BondInterface b = molec.getBondInterface(i);
        AtomInterface a = b.getIAtomInterface();
        int at_i = molec.getAtomIndex(a);
        if (at_i == -1) {
          logger.info("toArchiveFormat: wrong a1 in bond"
              + (i + 1));
          continue;
        }
        a = b.getJAtomInterface();
        int at_j = molec.getAtomIndex(a);
        if (at_j == -1) {
          logger.info("toArchiveFormat: wrong a2 in bond"
              + (i + 1));
          continue;
        }

        bufer = at_i + "," + at_j + "\n";
        len = bufer.length();
        out.write(bufer.getBytes(), offset, len);
        //offset += len;
      }
    } catch (IOException e) {
      logger.info("toArchiveFormat: cannot write bonds info");
      return false;
    }

    // --- Close
    try {
      out.close();
    } catch (IOException e) {
      //logger.info( "toArchiveFormat: cannot write atom's info");
      //return false;
    }

    return true;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < this.getNumberOfAtoms(); i++) {
      AtomInterface atom = this.getAtom(i);
      sb.append(String.format("%-3d %-5s %-2d %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f %-3d %-3d %-3d",
          (i + 1), atom.getName(), atom.getAtomicNumber(), atom.getX(), atom.getY(), atom.getZ(), atom.getBond(),
          atom.getAngle(), atom.getDihedralAngle(), atom.getNA() + 1, atom.getNB() + 1, atom.getNC() + 1));
    }
    return sb.toString();
  }

  public static void translateAtom(MoleculeInterface molec, int atom_index,
      float dir[], float increment) {
    AtomInterface atom = molec.getAtomInterface(atom_index);
    //atom.translateAtom(dir, increment);
    atom.setX(atom.getX() + dir[0] * increment);
    atom.setY(atom.getY() + dir[1] * increment);
    atom.setZ(atom.getZ() + dir[2] * increment);

  }

  public static void translateAtom(AtomInterface atom, float dir[],
      float increment) {
    atom.setX(atom.getX() + dir[0] * increment);
    atom.setY(atom.getY() + dir[1] * increment);
    atom.setZ(atom.getZ() + dir[2] * increment);
  }

  public static void translateAtoms(List group, float dir[], float increment) {
    for (int i = 0; i < group.size(); i++) {
      AtomInterface atom = (AtomInterface) group.get(i);
      //if (hasAtom(atom)) {
      Point3f.translatePoint(atom, dir, increment);
      //logger.info("New atom "+i+" coord: "+atom.getX()+" "+atom.getY()+" "+atom.getZ());
      //}
      // else {
      //    logger.info(
      //        "INTERNAL ERROR: translateAtoms: hasAtom(atom)");
      // }
    }
  }

  public void translateMolecule(float dir[], float increment) {
    translateMolecule(this, dir, increment);
  }

  public static void translateMolecule(MoleculeInterface mol, float dir[], float increment) {
    for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
      AtomInterface atom = mol.getAtomInterface(i);
      Point3f.translatePoint(atom, dir, increment);
    }
  }

  public static void updateCoordinates(MoleculeInterface molecule, String file_type_description, String file_name) throws
      Exception {
    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      throw new Exception("Error updating coordinates: Reference molecule has no atoms");
    }

    if (!MolecularFileFormats.getUpdateCoordFormats().containsKey(file_type_description)) {
      throw new Exception("Error: cannot update coordinates from file type: " + file_type_description);
    }

    // --- Update from Amber coordinate file
    if (file_type_description.equalsIgnoreCase(MolecularFileFormats.amberCoordFormat)) {
      AmberUtilities.updateCoordFromInpcrd(molecule, file_name);
    } // --- Update from PDB file
    else if (file_type_description.equalsIgnoreCase(MolecularFileFormats.pdbFile)) {
      PDB.updateCoordFromPDB(molecule, file_name);
    }

  }

  public static void validateSets(MoleculeInterface mol) {
    Object obj = mol.getProperty(MoleculeInterface.AtomicSets);
    if (obj == null || !(obj instanceof AtomicSets)) {
      return;
    }
    AtomicSets sets = (AtomicSets) obj;
    if (sets.size() < 1) {
      return;
    }

    AtomicSets updated_sets = new AtomicSets();
    Iterator iter = sets.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      obj = me.getValue();
      if (!(obj instanceof AtomicSet)) {
        System.err.println("validateSets: Expecting AtomicSet, got " + obj.getClass().getCanonicalName() + " Ignored...");
        continue;
      }
      AtomicSet aset = (AtomicSet) obj;
      if (aset.size() < 1) {
        continue;
      }

      AtomicSet new_aset = new AtomicSet(aset.getName());
      for (int i = 0; i < aset.size(); i++) {
        AtomInterface atom = aset.get(i);
        if (mol.getAtomIndex(atom) == -1) {
          continue;
        }
        new_aset.add(atom);
      }
      if (new_aset.size() < 1) {
        continue;
      }

      updated_sets.put(new_aset.getName(), new_aset);
    }

    Map props = mol.getProperties();
    if (updated_sets.size() < 1) {
      props.remove(sets);
      return;
    }
    props.put(MoleculeInterface.AtomicSets, updated_sets);
  }

  public static void validateTotalCharge(int elements[], int charge, int multiplicity) throws Exception {
    if (multiplicity < 1) {
      throw new Exception("Multiplicity should be > 0");
    }

    int n_electrons = -charge;
    // --- Count number of electrons
    for (int i = 0; i < elements.length; i++) {
      n_electrons += elements[i];
    }
    int unpaired = n_electrons % 2;

    if (unpaired == 0 && (multiplicity % 2 == 1)) { // Even number of electrons & odd multiplicity
      return;
    } else if (unpaired == 0 && (multiplicity % 2 == 0)) { // Even number of electrons & even multiplicity
      throw new Exception("Charge " + charge + " and multiplicity " + multiplicity + " do not make sense");
    } else if (unpaired == 1 && (multiplicity % 2 == 0)) { // Odd number of electrons & even multiplicity
      return;
    }

    // Odd number of electrons & odd multiplicity
    throw new Exception("Charge " + charge + " and multiplicity " + multiplicity + " do not make sense");
  }

  public static void validateTotalCharge(MoleculeInterface molec, int charge, int multiplicity) throws Exception {
    if (multiplicity < 1) {
      throw new Exception("Multiplicity should be > 0");
    }

    int n_electrons = -charge;

    // --- Count number of electrons
    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      int element = atom.getAtomicNumber();
      n_electrons += element;
    }

    int unpaired = n_electrons % 2;

    if (unpaired == 0 && (multiplicity % 2 == 1)) { // Even number of electrons & odd multiplicity
      return;
    } else if (unpaired == 0 && (multiplicity % 2 == 0)) { // Even number of electrons & even multiplicity
      throw new Exception("Charge " + charge + " and multiplicity " + multiplicity + " do not make sense");
    } else if (unpaired == 1 && (multiplicity % 2 == 0)) { // Odd number of electrons & even multiplicity
      return;
    }

    // Odd number of electrons & odd multiplicity
    throw new Exception("Charge " + charge + " and multiplicity " + multiplicity + " do not make sense");
  }

  public Molecule(Map mol) {

    Integer temp = (Integer) mol.get("natoms");
    number_of_Atoms = temp.intValue();
//        number_of_Atoms = Integer.parseInt( (String) );
    logger.info("Number of atoms" + number_of_Atoms);
    String key, value;
    StringTokenizer tk;
    Atom a;

    for (int i = 0; i < number_of_Atoms; i++) {
      key = "a" + Integer.toString(i);
      value = (String) mol.get(key);
      a = new Atom();

      if (value != null) {
        tk = new StringTokenizer(value, ",");
        a.setName(tk.nextToken());
        a.setX(Float.parseFloat(tk.nextToken()));
        a.setY(Float.parseFloat(tk.nextToken()));
        a.setZ(Float.parseFloat(tk.nextToken()));
        Atoms.addAtom(a);
        logger.info(i + value);
      } else {
        break;
      }
    }
  }

}
