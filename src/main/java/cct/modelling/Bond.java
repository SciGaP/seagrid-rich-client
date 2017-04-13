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

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;

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
public class Bond
    implements BondInterface {

   protected AtomInterface a1, a2;
   Map<String, Object> properties = new HashMap<String, Object> ();

   public Bond() {
      a1 = a2 = null;
   }

   @Override
  public BondInterface getNewBondInstance() {
      return new Bond();
   }

   public Bond(AtomInterface a1, AtomInterface a2) {
      this.a1 = a1;
      this.a2 = a2;

      a1.setBondedTo(a2, true);
      a2.setBondedTo(a1, true);

      a1.setBondIndex(this);
      a2.setBondIndex(this);
   }

   @Override
  public BondInterface getNewBondInstance(AtomInterface a_i, AtomInterface a_j) {
      return new Bond(a_i, a_j);
   }

   @Override
  public BondInterface getNewBondInstance(BondInterface bond) {
      return new Bond(bond);
   }

   public Bond(Bond b) {
      a1 = b.a1;
      a2 = b.a2;
   }

   public Bond(BondInterface b) {
      a1 = b.getIAtomInterface();
      a2 = b.getJAtomInterface();
   }

   @Override
  public float bondLength() {
      return (float) Math.sqrt( (
          (a1.getX() - a2.getX()) * (a1.getX() - a2.getX()) +
          (a1.getY() - a2.getY()) * (a1.getY() - a2.getY()) +
          (a1.getZ() - a2.getZ()) * (a1.getZ() - a2.getZ())));
   }

   public Bond getBond() {
      return this;
   }

   public AtomInterface getI() {
      return a1;
   }

   public AtomInterface getJ() {
      return a2;
   }

   @Override
  public AtomInterface getIAtomInterface() {
      return a1;
   }

   @Override
  public AtomInterface getJAtomInterface() {
      return a2;
   }

   @Override
  public Map<String, Object> getProperties() {
      return properties;
   }

   @Override
  public Object getProperty(String key) {
      return properties.get(key);
   }

   @Override
  public void setProperty(String prop, Object value) {
      properties.put(prop, value);
   }
}
