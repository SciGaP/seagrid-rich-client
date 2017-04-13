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

package cct.pdb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PDBResidue {

  MONOMER_TYPE Type;
  String residueName;
  private Map<String, PDBAtom> Atoms = new LinkedHashMap<String, PDBAtom> ();
  private Map<String, Set> bondedTo = new LinkedHashMap<String, Set> ();
  private List<PDBAtom> atomArray = new ArrayList<PDBAtom> ();
  private List<PDBBond> Bonds = new ArrayList<PDBBond> ();

  public PDBResidue(String name, MONOMER_TYPE type) {
    residueName = name.toUpperCase();
    Type = type;
  }

  public boolean hasAtom(String atom_name) {
    String name = atom_name.toUpperCase();
    return Atoms.containsKey(name);
  }

  public int countAtoms() {
    return Atoms.size();
  }

  public PDBAtom getAtom(int n) {
    return atomArray.get(n);
  }

  public PDBAtom getAtom(String atom_name) {
    atom_name = atom_name.toUpperCase().trim();
    return Atoms.get(atom_name);
  }

  public void addAtom(PDBAtom atom) throws Exception {
    if (Atoms.containsKey(atom.name)) {
      throw new Exception("Residue " + residueName + " already contains atom " + atom.name);
    }
    Atoms.put(atom.name, atom);
    bondedTo.put(atom.name, new HashSet());
    atomArray.add(atom);
  }

  public void addBond(int i, int j) throws Exception {
    if (i < 0 || i >= Atoms.size()) {
      throw new Exception("i < 0 || i >= Atoms.size()");
    }
    if (j < 0 || j >= Atoms.size()) {
      throw new Exception("j < 0 || j >= Atoms.size()");
    }
    if (j == i) {
      throw new Exception("j == i");
    }

    Set i_bonds = bondedTo.get(i);
    Set j_bonds = bondedTo.get(j);

    PDBAtom atom_i = Atoms.get(i);
    PDBAtom atom_j = Atoms.get(j);

    if (i_bonds.contains(atom_j.name)) {
      throw new Exception("Atom " + i + " " + atom_i.name + " is already bonded to atom " + j + " " + atom_j.name);
    }

    if (j_bonds.contains(atom_i.name)) {
      throw new Exception("Atom " + j + " " + atom_j.name + " is already bonded to atom " + i + " " + atom_i.name);
    }

    i_bonds.add(atom_j.name);
    j_bonds.add(atom_i.name);

    PDBBond bond = new PDBBond(atom_i.name, atom_j.name);
    Bonds.add(bond);
  }

  public int countBonds() {
    return Bonds.size();
  }

  public PDBBond getBond(int n) {
    return Bonds.get(n);
  }

  public void addBond(String i_name, String j_name) throws Exception {
    i_name = i_name.toUpperCase().trim();
    j_name = j_name.toUpperCase().trim();

    if (i_name.equals(j_name)) {
      throw new Exception("i_name.equals(j_name)");
    }

    if (!this.hasAtom(i_name)) {
      throw new Exception("Residue " + residueName + " does not have atom named " + i_name);
    }

    if (!this.hasAtom(j_name)) {
      throw new Exception("Residue " + residueName + " does not have atom named " + j_name);
    }

    Set i_bonds = bondedTo.get(i_name);
    Set j_bonds = bondedTo.get(j_name);

    i_bonds.add(j_name);
    j_bonds.add(i_name);

    PDBBond bond = new PDBBond(i_name, j_name);
    Bonds.add(bond);
  }
}
