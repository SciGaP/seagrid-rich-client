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
import java.util.NoSuchElementException;

import cct.interfaces.BondInterface;

/**
 *
 * <p>Title: BondsArray</p>
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
public class BondsArray
    extends ArrayList {
   public BondsArray() {
      super();
   }

   /** Copy constructor */
   public BondsArray(BondsArray ab) {
      // super( av.size(), av.size() );

      for (int i = 0; i < ab.size(); i++) {
         BondInterface bond = ab.getBond(i);
         //Bond b = new Bond( ab.getBond(i) );
         BondInterface b = bond.getNewBondInstance(bond);
         add(b);
      }
   }

   /**
    * Append an atom to the end of vector
    */

   public void append(BondInterface b) {
      //addElement( a );
      add(b);
   }

   /**
    * Returns atom at specified index
    * @exception ArrayIndexOutOfBoundsException if index >= capacity()
    */
   public BondInterface getBond(int i) throws
       ArrayIndexOutOfBoundsException {
//    return (gAtom)elementAt( i );
      return (BondInterface) get(i);
   }

   /**
    * Set an atom at specified index; atom at index is replaced
    */
   public void replace(BondInterface a, int i) {
//    setElementAt( a, i );
      set(i, a);
   }

   /**
    * Insert an atom at specified index; other atoms are shifted over
    */
   public void insert(BondInterface a, int i) {
//    insertElementAt( a, i );
      add(i, a);
   }

   /**
    * Remove an atom; other atoms are shifted down
    */
   public boolean removeBond(BondInterface a) {
//    return removeElement( a );
      return remove(a);
   }

   /**
    * Remove atom at specified index; other atoms are shifted down
    * @param i index of atom
    * @exception ArrayIndexOutOfBoundsException if index >= capacity()
    */
   public BondInterface removeBond(int i) throws ArrayIndexOutOfBoundsException {
//    removeElementAt( i );
      return (BondInterface) remove(i);
   }

   /**
    * Remove all atoms
    * @param i index of atom
    * @exception ArrayIndexOutOfBoundsException if index >= capacity()
    */
   public void removeAllBonds() throws ArrayIndexOutOfBoundsException {
      //removeAllElements();
      clear();
   }

   /**
    * Returns first atom in AtomVector
    * @exception NoSuchElementException if size() == 0
    */
   public BondInterface firstBond() throws NoSuchElementException {
      //return (gAtom)firstElement();
      if (size() <= 0) {
         return null;
      }

      return (BondInterface) get(0);
   }

   /**
    * Returns last atom in AtomVector
    * @exception NoSuchElementException if size() == 0
    */
   public BondInterface lastBond() throws NoSuchElementException {
      //return (gAtom)lastElement();
      int dim = size();
      if (size() <= 0) {
         return null;
      }

      return (BondInterface) get(dim - 1);
   }

   /**
    * Compares this AtomVector with another
    * @param av AtomVector to compare with
    * @return <tt>true</tt> if they are equal, else <tt>false</tt>
    */
   public boolean equals(BondsArray av) {
      if (av == null) {
         return false;
      }

      if (size() != av.size()) {
         return false;
      }

      for (int i = 0; i < size(); i++) {
         BondInterface a1 = getBond(i);
         BondInterface a2 = av.getBond(i);
         if (!a1.equals(a2)) {
            return false;
         }
      }

      return true;
   }

}
