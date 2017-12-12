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

import java.util.Map;

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
public interface BondInterface {

   float DEFAULT_BONDS_AND_STICKS_RADIUS = 0.2f;
   float DEFAULT_STICKS_RADIUS = 0.15f;

   String CYLINDER_RADIUS = "BondRadius";
   String RGB_COLOR = "RGBColor";
   String COLOR_SCHEME = "ColorScheme";
   String RENDERING_STYLE = "RenderingStyle";
   String VISIBLE = "Visible";

   int RENDER_CYLINDER = 0;
   int RENDER_LINE = 1;

   int MONOCOLOR = 0;
   int BICOLOR = 1;

   int CYLINDER_MONOCOLOR = 0;
   int CYLINDER_BICOLOR = 1;
   int LINE_MONOCOLOR = 2;
   int LINE_BICOLOR = 3;

   float bondLength();

   AtomInterface getIAtomInterface();

   AtomInterface getJAtomInterface();

   BondInterface getNewBondInstance();

   BondInterface getNewBondInstance(BondInterface bond);

   BondInterface getNewBondInstance(AtomInterface a_i, AtomInterface a_j);

   Map<String, Object> getProperties();

   Object getProperty(String key);

   void setProperty(String prop, Object value);
}
