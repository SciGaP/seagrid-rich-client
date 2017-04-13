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

public interface OperationsOnAtoms {

  int SELECTION_UNLIMITED = 0;
  int SELECTION_ONE_ATOM_ONLY = 1;
  int SELECTION_SPOT_ONLY = 2;
  int SELECTION_TWO_ATOMS_ONLY = 3;
  int SELECTION_THREE_ATOMS_ONLY = 4;
  int SELECTION_FOUR_ATOMS_ONLY = 5;

  //public static final int SELECT_ATOMS = 0;
  //public static final int SELECT_MONOMERS = 1;
  //public static final int SELECT_MOLECULES = 2;

  //public static final int RULE_UNION = 0;
  //public static final int RULE_DIFFERENCE = 1;
  //public static final int RULE_INTERSECTION = 2;

  int SELECTED_NOTHING = -1;
  int SELECTED_DISPLAY_ATOMS = 0;
  int SELECTED_UNDISPLAY_ATOMS = 1;
  int SELECTED_DELETE_ATOMS = 2;
  int SELECTED_LABEL_ATOMS = 3;
  int SELECTED_ADD_ATOMS = 4;
  int SELECTED_UNLABEL_ATOMS = 5;
  int SELECTED_MODIFY_ATOMS = 6;
  int SELECTED_MODIFY_BONDS = 7;
  int SELECTED_MODIFY_ANGLES = 8;
  int SELECTED_MODIFY_DIHEDRALS = 9;
  int SELECTED_FILL_VALENCES_WITH_HYDROGENS = 10;
  int SELECTED_ADD_MOLECULE = 11;
  int SELECTED_ADD_FRAGMENT = 12;
  int SELECTED_CREATE_CENTROID = 13;
  int SELECTED_SOLVATE_CAP = 14;
  int SELECTED_SOLVATE_SHELL = 15;
  int SELECTED_CHANGE_ATOM_LABELS_SIZE = 16;
  int SELECTED_CHANGE_ATOM_LABELS_COLOR = 17;
  int SELECTED_CHANGE_ATOM_COLORS = 18;
  int SELECTED_CREATE_ATOMIC_SET = 19;
  int SELECTED_ROTATION_CENTER = 20;

  int FIXED_ATOM = 0;
  int ROTATE_GROUP = 1;
  int TRANSLATE_GROUP = 2;
  int TRANSLATE_ATOM = 3;
  int ROTATE_ATOM = 4;

}
