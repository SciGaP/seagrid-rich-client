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

package cct.tools.filebrowser;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 * Handles file size as unsigned integer 64
 * it's a redesign of a UnsignedInteger64 class from the sshtools
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class FileSize
    extends Number implements Serializable,
    Comparable {

   public final static BigInteger MAX_VALUE = new BigInteger(
       "18446744073709551615");

   /**  */
   public final static BigInteger MIN_VALUE = new BigInteger("0");
   private BigInteger bigInt;

   /**
    * Creates a new FileSize object.
    *
    * @param sval
    *
    * @throws NumberFormatException
    */
   public FileSize(String sval) throws NumberFormatException {
      bigInt = new BigInteger(sval);

      if ( (bigInt.compareTo(MIN_VALUE) < 0) ||
          (bigInt.compareTo(MAX_VALUE) > 0)) {
         throw new NumberFormatException();
      }
   }

   /**
    * Creates a new FileSize object.
    *
    * @param bval
    *
    * @throws NumberFormatException
    */
   public FileSize(byte[] bval) throws NumberFormatException {
      bigInt = new BigInteger(bval);

      if ( (bigInt.compareTo(MIN_VALUE) < 0) ||
          (bigInt.compareTo(MAX_VALUE) > 0)) {
         throw new NumberFormatException();
      }
   }

   /**
    * Creates a new FileSize object.
    *
    * @param input
    *
    * @throws NumberFormatException
    */
   public FileSize(BigInteger input) {
      bigInt = new BigInteger(input.toString());

      if ( (bigInt.compareTo(MIN_VALUE) < 0) ||
          (bigInt.compareTo(MAX_VALUE) > 0)) {
         throw new NumberFormatException();
      }
   }

   /**
    *
    *
    * @param o
    *
    * @return
    */
   @Override
  public boolean equals(Object o) {
      try {
         FileSize u = (FileSize) o;

         return u.bigInt.equals(this.bigInt);
      }
      catch (ClassCastException ce) {
         // This was not an UnsignedInt64, so equals should fail.
         return false;
      }
   }

   /**
    *
    *
    * @return
    */
   public BigInteger bigIntValue() {
      return bigInt;
   }

   /**
    *
    *
    * @return
    */
   @Override
  public int intValue() {
      return bigInt.intValue();
   }

   /**
    *
    *
    * @return
    */
   @Override
  public long longValue() {
      return bigInt.longValue();
   }

   /**
    *
    *
    * @return
    */
   @Override
  public double doubleValue() {
      return bigInt.doubleValue();
   }

   /**
    *
    *
    * @return
    */
   @Override
  public float floatValue() {
      return bigInt.floatValue();
   }

   /**
    *
    *
    * @param val
    *
    * @return
    */
   @Override
  public int compareTo(Object val) {
      return bigInt.compareTo( ( (FileSize) val).bigInt);
   }

   /**
    *
    *
    * @return
    */
   @Override
  public String toString() {
      return bigInt.toString();
   }

   /**
    *
    *
    * @return
    */
   @Override
  public int hashCode() {
      return bigInt.hashCode();
   }

   /**
    *
    *
    * @param x
    * @param y
    *
    * @return
    */
   public static FileSize add(FileSize x, FileSize y) {
      return new FileSize(x.bigInt.add(y.bigInt));
   }

   /**
    *
    *
    * @param x
    * @param y
    *
    * @return
    */
   public static FileSize add(FileSize x, int y) {
      return new FileSize(x.bigInt.add(BigInteger.valueOf(y)));
   }

}
