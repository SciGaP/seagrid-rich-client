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

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class AtomTypes
    implements AtomProperties {

  static final Map defaultAtomTypes = new HashMap();
  Map atomTypes;

  public AtomTypes() {
    atomTypes = new HashMap(defaultAtomTypes);
  }

  static {
    AtomTypeDef temp;
    defaultAtomTypes.put("H", new AtomTypeDef("H", "H", 1, GEOMETRY_LINEAR));
    defaultAtomTypes.put("LI", new AtomTypeDef("LI", "LI", 1, GEOMETRY_LINEAR));
    defaultAtomTypes.put("BE", new AtomTypeDef("BE", "BE", 2, GEOMETRY_LINEAR));
    defaultAtomTypes.put("B", new AtomTypeDef("B", "B", 3, GEOMETRY_TRIGONAL));
    temp = new AtomTypeDef("C.4", "C", 4, GEOMETRY_TETRAHEDRAL);
    defaultAtomTypes.put("C.4", temp);
    defaultAtomTypes.put("C.3", new AtomTypeDef("C.3", "C", 3, GEOMETRY_TRIGONAL));
    defaultAtomTypes.put("C.2", new AtomTypeDef("C.2", "C", 2, GEOMETRY_TRIGONAL));

    // --- Nitrogen atom types

    temp = new AtomTypeDef("N.4", "N", 4, GEOMETRY_TETRAHEDRAL);
    temp.setDescription("Positively charged sp3 Nitrogen (S-S-S-S)");
    defaultAtomTypes.put(temp.getName(), temp);

    temp = new AtomTypeDef("N.3", "N", 3, GEOMETRY_TETRAHEDRAL);
    temp.setDescription("Sp3 Nitrogen (S-S-S-Lp");
    defaultAtomTypes.put(temp.getName(), temp);

    temp = new AtomTypeDef("N.P3", "N", 3, GEOMETRY_TRIGONAL);
    temp.setDescription("Trigonal planar Nitrogen");
    defaultAtomTypes.put(temp.getName(), temp);

    temp = new AtomTypeDef("N.2", "N", 2, GEOMETRY_LINEAR);
    temp.setDescription("Nitrogen sp");
    defaultAtomTypes.put(temp.getName(), temp);

    // --- Oxygen atom types

    temp = new AtomTypeDef("O.4", "O", 2, GEOMETRY_TETRAHEDRAL);
    temp.setDescription("Oxygen sp3");
    defaultAtomTypes.put(temp.getName(), temp);

    temp = new AtomTypeDef("O.2", "O", 1, GEOMETRY_LINEAR);
    temp.setDescription("Oxygen sp2");
    defaultAtomTypes.put(temp.getName(), temp);

  }
}
