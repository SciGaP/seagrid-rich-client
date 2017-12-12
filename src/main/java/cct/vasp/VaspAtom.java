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

package cct.vasp;

import cct.modelling.ChemicalElements;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class VaspAtom {
   public int element = 0;
   public int species = 0;
   public float xyz[] = new float[3];
   public boolean opt[] = new boolean[3];

   VaspAtom() {}

   VaspAtom(VaspAtom atom) {
      element = atom.element;
      species = atom.species;
      xyz[0] = atom.getX();
      xyz[1] = atom.getY();
      xyz[2] = atom.getZ();
      opt[0] = atom.opt[0];
      opt[1] = atom.opt[1];
      opt[2] = atom.opt[2];
   }

   VaspAtom(float x, float y, float z) {
      xyz[0] = x;
      xyz[1] = y;
      xyz[2] = z;
   }

   VaspAtom(float x, float y, float z, boolean a1, boolean a2, boolean a3) {
      xyz[0] = x;
      xyz[1] = y;
      xyz[2] = z;
      opt[0] = a1;
      opt[1] = a2;
      opt[2] = a3;
   }

   public int getAtomicNumber() {
      return element;
   }

   public String getName() {
      if (element == 0) {
         return String.valueOf( (species + 1));
      }
      return ChemicalElements.getElementSymbol(element);
   }

   public void setElement(String el) {
      element = ChemicalElements.getAtomicNumber(el);
   }

   public void setCoordinates(float x, float y, float z) {
      xyz[0] = x;
      xyz[1] = y;
      xyz[2] = z;
   }

   public void setCoordinates(double x, double y, double z) {
      xyz[0] = (float) x;
      xyz[1] = (float) y;
      xyz[2] = (float) z;
   }

   public float getX() {
      return xyz[0];
   }

   public float getY() {
      return xyz[1];
   }

   public float getZ() {
      return xyz[2];
   }

}
