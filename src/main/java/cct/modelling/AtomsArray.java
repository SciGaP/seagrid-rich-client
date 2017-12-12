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

//import java.util.Vector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;

/**
 *
 * <p>Title: AtomsArray</p>
 *
 * <p>Description: This class keeps track about atoms/residues addition</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class AtomsArray {
//public class AtomsArray extends ArrayList {
  protected ArrayList Atoms = new ArrayList();
  protected ArrayList Residues = new ArrayList();
  static final Logger logger = Logger.getLogger(AtomsArray.class.getCanonicalName());
  /**
   *  Creates a new atom array.
   */
  public AtomsArray() {
    super();
  }

  public void addMonomer(String monomer_name) {
    Monomer last_monomer = new Monomer(monomer_name);
    Residues.add(last_monomer);
  }

  /** Copy constructor */
  public AtomsArray(AtomsArray av) {
    // super( av.size(), av.size() );
    Atoms = (ArrayList) av.Atoms.clone();
    Residues = (ArrayList) av.Residues.clone();
    /*
             for (int i = 0; i < av.size(); i++) {
        Atom a = new Atom(av.getAtom(i));
        append(a);
             }
     */
  }

  /**
   * Adds atom to the last residue in the list
   * @param a Atom
   */
  //public void addAtom(Atom a) {
  public void addAtom(AtomInterface a) {
    Monomer last_monomer;
    int n;
    if (Residues.isEmpty()) {
      last_monomer = new Monomer();
      Residues.add(last_monomer);
      n = 0;
    }
    else {
      n = Residues.size() - 1; // Index of the last element
      last_monomer = (Monomer) Residues.get(n);
    }
    a.setSubstructureNumber(n);
    Atoms.add(a);
    last_monomer.addAtom(a);
  }

  /**
   * Adds a new monomer and adds an atom to the monomer
   * @param a Atom Atom to be added
   * @param monomer_name String Name of new monomer to be added
   */
  public void addAtom(AtomInterface a, String monomer_name) {
    Monomer last_monomer = new Monomer(monomer_name);
    Residues.add(last_monomer);
    addAtom(a);
  }

  /**
   * Adds atom to the existing nth monomer.
   * @param a Atom Atom to be added
   * @param n int Monomer number
   */
  /*
       public void addAtom(Atom a, int n) {
      if ( n < 0 || n>= Residues.size() ) {
          logger.info(
                  "INTERNAL ERROR: addAtom: n < 0 || n>= Residues.size()");
        addAtom(a); // Just add the atom to the last residue
        return;
      }
      Monomer n_monomer = (Monomer) Residues.get(n);
      a.setSubstructureNumber(n);
      Atoms.add(a);
      n_monomer.addAtom(a);
       }
   */
  public void addAtom(AtomInterface a, int n) {
    if (n < 0 || n >= Residues.size()) {
      if (! (Residues.size() == 0 && n == 0)) {
        System.err.println("Warning: addAtom: n < 0 || n>= Residues.size(" + Residues.size() + "): " + n);
      }
      addAtom(a); // Just add the atom to the last residue
      return;
    }
    Monomer n_monomer = (Monomer) Residues.get(n);
    a.setSubstructureNumber(n);
    Atoms.add(a);
    n_monomer.addAtom(a);
  }

  /**
   *
   * @param a Atom
   * @param n int
   * @param name String
   */
  public void addAtom(AtomInterface a, int n, String name) {
    if (n < 0) {
      System.err.println(
          "INTERNAL ERROR: addAtom: n < 0 for residue " + name);
      addAtom(a); // Just add the atom to the last residue
      return;
    }

    if (name == null) {
      System.err.println(
          "INTERNAL ERROR: addAtom: name ==null, put name to XXX");
      name = "XXX";
    }

    Monomer n_monomer = null;

    if (n < Residues.size()) { // Error check for existing residue
      n_monomer = (Monomer) Residues.get(n);
      String res_name = n_monomer.getName();
      if (res_name == null) {
        n_monomer.setName(name);
      }
      else if (!res_name.equals(name)) {
        System.err.println(
            "ERROR: addAtom: residue already has name " + res_name + "; new name " + name + " is ignored");
      }
      addAtom(a, n);
      return;
    }
    else { // Allocate memory for new residue(s)
      for (int i = Residues.size(); i <= n; i++) {
        n_monomer = new Monomer();
        Residues.add(n_monomer);
      }
    }

    n_monomer.name = name;
    addAtom(a, n);
  }

  /**
   * Returns ith atom
   * @exception ArrayIndexOutOfBoundsException if index >= capacity()
   */
  public AtomInterface getAtom(int i) throws
      ArrayIndexOutOfBoundsException {
//    return (Atom)elementAt( i );
    return (AtomInterface) Atoms.get(i);
  }

  public AtomInterface getAtomInterface(int i) throws
      ArrayIndexOutOfBoundsException {
//    return (Atom)elementAt( i );
    return (AtomInterface) Atoms.get(i);
  }

  /**
   * Returns atom number in the list.
   * @param a Atom Atom in question
   * @return int Atom number. -1 if no such atom in the list.
   */
  public int getAtomIndex(AtomInterface a) {
    return Atoms.indexOf(a);
  }

  /**
   * Returns nth monomer from the list.
   * @param n int - Monomer number
   * @return Monomer - Returns nth monomer or null if n is out of bounds
   */
  public Monomer getMonomer(int n) {
    if (n < 0 || n >= Residues.size()) {
      logger.info(
          "INTERNAL ERROR: getMonomer: n < 0 || n>= Residues.size(): " + n);
      return null;
    }
    return (Monomer) Residues.get(n);
  }

  /**
   * Returns number of monomers
   * @return int Returns number of monomers
   */
  public int getNumberOfMonomers() {
    return Residues.size();
  }

  /**
   * Set an atom at specified index; atom at index is replaced
   */
  public void replace(Atom a, int i) {
//    setElementAt( a, i );
    Atoms.set(i, a);
  }

  /**
   * Insert an atom at specified index; other atoms are shifted over
   */
  public void insert(Atom a, int i) {
//    insertElementAt( a, i );
    Atoms.add(i, a);
  }

  /**
   * Removes atom from the list
   * @param a Atom - Atom to be removed
   * @return boolean Returns "true" in the case of success, "false" otherwise
   */
  public boolean removeAtom(AtomInterface a) {
    if (Atoms.indexOf(a) == -1) {
      logger.info(
          "INTERNAL ERROR: AtomArray: removeAtom: No such atom in list");
      return false;
    }
    int nres = a.getSubstructureNumber();

    if (nres < 0 || nres >= Residues.size()) {
      logger.info(
          "INTERNAL ERROR: AtomArray: removeAtom: Wrong residue number");
      return false;
    }

    Monomer monomer = (Monomer) Residues.get(nres);

    if (monomer.indexOf(a) == -1) {
      logger.info(
          "INTERNAL ERROR: AtomArray: removeAtom: No such atom in specified monomer");
      return false;
    }

    if (!Atoms.remove(a) || !monomer.remove(a)) {
      logger.info(
          "INTERNAL ERROR: AtomArray: removeAtom: !Atoms.remove(a) || !monomer.remove(a)");
      return false;
    }

    if (monomer.size() == 0) { // No more atoms in residue, i.e. delete monomer as well
      Residues.remove(monomer);
      // Re-arrange residues and update atom information
      for (int i = 0; i < Residues.size(); i++) {
        monomer = (Monomer) Residues.get(i);
        for (int j = 0; j < monomer.size(); j++) {
          AtomInterface atom = monomer.getAtom(j);
          atom.setSubstructureNumber(i);
        }
      }
    }
    return true;
  }

  /**
   * Remove atom at specified index; other atoms are shifted down
   * @param i index of atom
   * @exception ArrayIndexOutOfBoundsException if index >= capacity()
   */
  public Atom removeAtom(int i) throws ArrayIndexOutOfBoundsException {
//    removeElementAt( i );
    return remove(i);
  }

  /**
   * Remove all atoms
   * @param i index of atom
   * @exception ArrayIndexOutOfBoundsException if index >= capacity()
   */
  public void removeAllAtoms() throws ArrayIndexOutOfBoundsException {
    //removeAllElements();
    Atoms.clear();
  }

  /**
   * Returns first atom in AtomVector
   * @exception NoSuchElementException if size() == 0
   */
  public Atom firstAtom() throws NoSuchElementException {
    //return (Atom)firstElement();
    if (size() <= 0) {
      return null;
    }

    return (Atom) Atoms.get(0);
  }

  /**
   * Returns last atom in AtomVector
   * @exception NoSuchElementException if size() == 0
   */
  public Atom lastAtom() throws NoSuchElementException {
    //return (Atom)lastElement();
    int dim = size();
    if (size() <= 0) {
      return null;
    }

    return (Atom) Atoms.get(dim - 1);
  }

  /**
   * Compares this AtomArray with another
   * @param av AtomVector to compare with
   * @return <tt>true</tt> if they are equal, else <tt>false</tt>
   */
  public boolean equals(AtomsArray av) {
    if (av == null) {
      return false;
    }

    if (size() != av.size()) {
      return false;
    }

    for (int i = 0; i < size(); i++) {
      AtomInterface a1 = getAtom(i);
      AtomInterface a2 = av.getAtom(i);
      if (!a1.equals(a2)) {
        return false;
      }
    }

    return true;
  }

  Atom remove(int i) throws ArrayIndexOutOfBoundsException {
    return (Atom) Atoms.remove(i);
  }

  void set(Atom a, int i) {
    Atoms.set(i, a);
  }

  int size() {
    return Atoms.size();
  }

  public boolean hasAtom(Atom atom) {
    return Atoms.contains(atom);
  }

  public Set<String> getUniqueMonomersInMolecule() {
    Set<String> monomers = new HashSet<String> ();
    for (int i = 0; i < getNumberOfMonomers(); i++) {
      Monomer m = this.getMonomer(i);
      String monomer_name = m.getName();
      if (!monomers.contains(monomer_name)) {
        monomers.add(monomer_name);
      }
    }
    return monomers;
  }

}
