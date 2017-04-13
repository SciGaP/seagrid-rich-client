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

package cct.interfaces;

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
public interface HelperInterface {

   String JMD_ADD_ATOM_ID = "editing.adding.atom";
   String JMD_ADD_MOLECULE_ID = "editing.adding.molec";
   String JMD_ADD_FRAGMENT_ID = "editing.adding.frag";
   String JMD_MODIFY_ANGLE_ID = "editing.modifying.angle";
   String JMD_MODIFY_BOND_ID = "editing.modifying.bond";
   String JMD_MODIFY_TORSION_ID = "editing.modifying.torsion";
   String JMD_SELECT_ATOMS_ID = "generics.select_atoms";
   String JMD_MANAGE_GAUSSIAN_CUBES_ID = "managing.gaussian.cubes";
   String JMD_MANAGE_GRAPHICS_OBJECTS_ID = "managing.graphics.objects";

   void setHelpIDString(java.awt.Component comp, String helpID);

   void enableHelpKey(java.awt.Component comp, String id);

   void enableHelpKey(java.awt.Component comp, String id, String presentation, String presentationName);

   void addActionListenerForTracking(javax.swing.AbstractButton comp);

   void displayHelpFromSourceActionListener(javax.swing.AbstractButton comp);

}
