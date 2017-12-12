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

package cct.qchem;

import cct.interfaces.Point3fInterface;

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
public class QChemAtom {
   String name;
   int atomicNumber;
   float xyz[] = new float[3];
   float bondLength, alpha, beta;
   String bondLengthPar = null, alphaPar = null, betaPar = null;
   int i1 = -1, i2 = -1, i3 = -1;

   public void setName(String _name) {
      name = _name;
   }

   public String getName() {
      return name;
   }

   public int getAtomicNumber() {
      return atomicNumber;
   }

   public void setAtomicNumber(int number) {
      atomicNumber = number;
   }

   public void setBondLength(float bond) {
      bondLength = bond;
   }

   public float getBondLength() {
      return bondLength;
   }

   public void setBondLength(String bond) {
      bondLengthPar = bond;
   }

   public String getBondLengthPar() {
      return bondLengthPar;
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

   public void set_i1(int i1) {
      this.i1 = i1;
   }

   public int get_i1() {
      return this.i1;
   }

   public void set_i2(int i2) {
      this.i2 = i2;
   }

   public int get_i2() {
      return this.i2;
   }

   public void set_i3(int i3) {
      this.i3 = i3;
   }

   public int get_i3() {
      return this.i3;
   }

   public void setX(float x) {
      xyz[0] = x;
   }

   public void setX(double x) {
      xyz[0] = (float) x;
   }

   public void setY(float y) {
      xyz[1] = y;
   }

   public void setY(double y) {
      xyz[1] = (float) y;
   }

   public void setZ(float z) {
      xyz[2] = z;
   }

   public void setZ(double z) {
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

   public double distanceTo(Point3fInterface point) {
      return Math.sqrt( (xyz[0] - point.getX()) * (xyz[0] - point.getX()) +
                       (xyz[1] - point.getY()) * (xyz[1] - point.getY()) +
                       (xyz[2] - point.getZ()) * (xyz[2] - point.getZ()));
   }

   public void setXYZ(float xx, float yy, float zz) {
      xyz[0] = xx;
      xyz[1] = yy;
      xyz[2] = zz;
   }

   public void setXYZ(float xx[]) {
      xyz[0] = xx[0];
      xyz[1] = xx[1];
      xyz[2] = xx[2];
   }

   public void setXYZ(Point3fInterface xyz) {
      this.xyz[0] = xyz.getX();
      this.xyz[1] = xyz.getY();
      this.xyz[2] = xyz.getZ();
   }

   public void subtract(Point3fInterface a1, Point3fInterface a2) {
      xyz[0] = a1.getX() - a2.getX();
      xyz[1] = a1.getY() - a2.getY();
      xyz[2] = a1.getZ() - a2.getZ();

   }

}
