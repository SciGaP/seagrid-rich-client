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

import cct.interfaces.AtomInterface;
import cct.interfaces.MonomerInterface;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Monomer
    extends ArrayList implements MonomerInterface {
   protected String name = "";

   public Monomer() {
      name = null;
   }

   public Monomer(String r_name) {
      name = r_name;
   }

   public boolean addAtom(AtomInterface a) {
      return add(a);
   }

   @Override
  public AtomInterface getAtom(int n) {
      if (n < 0 || n >= size()) {
         return null;
      }
      return (AtomInterface) get(n);
   }

   @Override
  public int getAtomIndex(AtomInterface atom) {
      return this.indexOf(atom);
   }

   @Override
  public String getName() {
      return name;
   }

   @Override
  public int getNumberOfAtoms() {
      return size();
   }

   public void selectAllAtoms(boolean select) {
      for (int i = 0; i < this.size(); i++) {
         AtomInterface atom = (AtomInterface) get(i);
         atom.setSelected(select);
      }
   }

   @Override
  public void setName(String r_name) {
      this.name = r_name;
   }
}
