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

package cct.mopac;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class MopacAtom {

   String name = "";
   String comment = "";
   int element = 0;
   boolean cartesian = true;
   float xyz[] = new float[3], charge;
   float isotopeMass = 0;
   float bondLength, alpha, beta;
   String bondLengthPar = null, alphaPar = null, betaPar = null;
   int opt[] = new int[3];
   int i1 = -1, i2 = -1, i3 = -1;

   public MopacAtom() {
   }

   public int getAtomicNumber() {
      return element;
   }

   public void setName(String Name) {
      name = Name;
   }

   public void setXYZ(float x, float y, float z) {
      this.xyz[0] = x;
      this.xyz[1] = y;
      this.xyz[2] = z;
   }

   public float getBondLength() {
      return bondLength;
   }

   public void setBondLength(String bond) {
      bondLengthPar = bond;
   }

   public void setBondLength(float bond) {
      bondLength = bond;
   }

   public void setAlpha(float a) {
      alpha = a;
   }

   public float getAlpha() {
      return alpha;
   }

   public void setAlpha(String a) {
      alphaPar = a;
   }

   public String getAlphaPar() {
      return alphaPar;
   }

   public void setBeta(float b) {
      beta = b;
   }

   public float getBeta() {
      return beta;
   }

   public void setBeta(String b) {
      betaPar = b;
   }

   public String getBetaPar() {
      return betaPar;
   }

   public int get_i1() {
      return i1;
   }

   public int get_i2() {
      return i2;
   }

   public int get_i3() {
      return i3;
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

   public void setXYZ(float[] xyz) {
      this.xyz[0] = xyz[0];
      this.xyz[1] = xyz[1];
      this.xyz[2] = xyz[2];
   }

   public String getName() {
      return name;
   }

   public void setElement(int el) {
      element = el;
   }

   public String getBondLengthPar() {
      return bondLengthPar;
   }

}
