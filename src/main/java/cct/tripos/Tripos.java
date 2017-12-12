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

package cct.tripos;

import java.util.List;

import cct.interfaces.AtomInterface;
import cct.modelling.ChemicalElements;

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
public class Tripos {
   private Tripos() {
   }

   /**
    * Guesses SYBYL atom types
    * @param atom AtomInterface
    * @return String
    */
   public static String guessTriposAtomType(AtomInterface atom) {
      int element = atom.getAtomicNumber();
      if (element == 0) {
         return "Du";
      }

      switch (element) {
         case 6: // Carbon
            List nb = atom.getBondedToAtoms();
            if (nb.size() == 4) {
               return "C.3";
            }
            else if (nb.size() == 3) {
               return "C.2";
            }
            else if (nb.size() == 2) {
               return "C.1";
            }
            break;

         case 7: // Nitrogen
            nb = atom.getBondedToAtoms();
            if (nb.size() == 4) {
               return "N.4";
            }
            else if (nb.size() == 3) {
               return "N.2";
            }
            else if (nb.size() == 2) {
               return "N.1";
            }
            break;

         case 8: // Oxygen
            nb = atom.getBondedToAtoms();
            if (nb.size() == 2) {
               return "O.3";
            }
            else if (nb.size() == 1) {
               return "O.2";
            }
            break;

         case 16: // Sulphur
            nb = atom.getBondedToAtoms();
            if (nb.size() == 2) {
               return "S.3";
            }
            break;

      }
      return ChemicalElements.getElementSymbol(element);
   }
}
