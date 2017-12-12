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

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: </p>
 * Defines atom type
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class AtomTypeDef
    implements AtomProperties {
   protected String Name = null;
   protected int Element = 0;
   protected int Valence = 0;
   protected int Geometry = 0;

   // --- Optional

   Color atomColor = null;
   float Weight = -1.0f;
   float covalentRadius = -1.0f;
   String Description = null;

   public AtomTypeDef(String name, String element, int valence, int geometry) {
      int elem = ChemicalElements.getAtomicNumber(element);
      Init(name, elem, valence, geometry);
   }

   public AtomTypeDef(String name, int element, int valence, int geometry) {
      Init(name, element, valence, geometry);
   }

   public void Init(String name, int element, int valence, int geometry) {
      Name = name;
      if (element < 0 || element > ChemicalElements.getNumberOfElements()) {
         JOptionPane.showMessageDialog(new JFrame(),
                                       "Internal ERROR: AtomType: Wrong element number: " +
                                       element,
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
         element = 0;
      }
      Element = element;

      // !!! ERROR CHECK !!!
      Valence = valence;

      Geometry = geometry;
   }

   /**
    * Get name (mnemonic) of atom type
    * @return String
    */
   public String getName() {
      return Name;
   }

   /**
    * Set atomic weight
    * @param weight float
    * @return boolean "false" if atomic weight was wrong, "true" otherwise
    */
   public boolean setWeight(float weight) {
      if (weight < 0) {
         JOptionPane.showMessageDialog(new JFrame(),
                                       "Internal ERROR: setWeight: Wrong atomic weight: " +
                                       weight,
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
         Weight = 0;
         return false;
      }
      Weight = weight;
      return true;
   }

   /**
    * Sets description for this atom type
    * @param descr String
    */
   public void setDescription(String descr) {
      Description = descr;
   }
}
