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
public class Nonbonded15Table {
   int size = 0;
   boolean[] table = null;

   public Nonbonded15Table(int natoms) {
      if (natoms < 2) {
         return;
      }
      table = new boolean[natoms * (natoms - 1) / 2];
      for (int i = 0; i < table.length; i++) {
         table[i] = true;
      }
   }

   public void set15(int i, int j, boolean exist15) {
      if (i == j) {
         return;
      }
      if (i > j) {
         if (i == 0) {
            return;
         }
         table[i * (i - 1) / 2 + j] = exist15;
      }
      else if (j > 0) {
         table[j * (j - 1) / 2 + i] = exist15;
      }
   }

   public boolean get15(int i, int j) {
      if (i == j) {
         return false;
      }
      if (i > j) {
         if (i == 0) {
            return false;
         }
         return table[i * (i - 1) / 2 + j];
      }
      else if (j > 0) {
         return table[j * (j - 1) / 2 + i];
      }
      return false;
   }

}
