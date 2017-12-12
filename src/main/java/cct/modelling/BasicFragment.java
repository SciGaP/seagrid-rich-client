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

import java.util.HashMap;
import java.util.Map;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;

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

// Ordinal-based typesafe enum
public class BasicFragment
    implements Comparable {
  private final String name = "";
  // Ordinal of next suit to be created
  private static int nextOrdinal = 0;
  // Assign an ordinal to this suit
  private final int ordinal = nextOrdinal++;

  private static Map Fragments = new HashMap();

  private BasicFragment() {

    MoleculeInterface molecule = Molecule.getNewInstance();
    AtomInterface atom = molecule.getNewAtomInstance();

    molecule.setName("Methyl");

    atom.setAtomicNumber(6);
    atom.setXYZ(0, 0, 0);
    molecule.addAtom(atom);

    atom.setAtomicNumber(1);
    atom.setXYZ(0.624883f, 0.624883f, 0.624883f);
    molecule.addAtom(atom);

    atom.setAtomicNumber(1);
    atom.setXYZ( -0.624883f, -0.624883f, 0.624883f);
    molecule.addAtom(atom);

    atom.setAtomicNumber(1);
    atom.setXYZ( -0.624883f, 0.624883f, -0.624883f);
    molecule.addAtom(atom);

    atom.setAtomicNumber(1);
    atom.setXYZ(0.624883f, -0.624883f, -0.624883f);
    molecule.addAtom(atom);

    Fragments.put(molecule.getName(), molecule);

  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(Object o) {
    return ordinal - ( (BasicFragment) o).ordinal;
  }

  public int compareTo(String str) {
    return this.toString().compareTo(str);
  }

  public int compareToIgnoreCase(String str) {
    return this.toString().compareToIgnoreCase(str);
  }

  public boolean equalsIgnoreCase(String str) {
    return this.toString().equalsIgnoreCase(str);
  }

  public int getEnum() {
    return ordinal;
  }

  // CCT Tags



  class CarbonDivalentStr {
    MoleculeInterface mol = Molecule.getNewInstance();

    public MoleculeInterface getMolecule() {
      return mol;
    }
  }

}
