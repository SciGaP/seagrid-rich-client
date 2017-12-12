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

package cct.gulp;

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
public class GULPAtom {
   String Label;
   private String speciesType = "CORE";
   int Element;
   double[] Cartesians = null;
   double[] Fractional = null;
   double[] Xyz = new double[3];

   public GULPAtom(String label, int elem) {
      Element = elem;
      Label = label;
   }

   public void setSpeciesType(String type) {
      speciesType = type;
   }

   public String getSpeciesType() {
      return speciesType;
   }

   public void setCartesians(double[] xyz) {
      if (Cartesians == null) {
         Cartesians = new double[3];
      }
      Cartesians[0] = xyz[0];
      Cartesians[1] = xyz[1];
      Cartesians[2] = xyz[2];
   }

   public void setFractionalCoord(double[] xyz) {
      if (Fractional == null) {
         Fractional = new double[3];
      }
      Fractional[0] = xyz[0];
      Fractional[1] = xyz[1];
      Fractional[2] = xyz[2];
   }

}
