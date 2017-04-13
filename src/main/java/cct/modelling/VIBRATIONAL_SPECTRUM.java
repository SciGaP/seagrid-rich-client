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

public enum VIBRATIONAL_SPECTRUM {
  INFRARED_SPECTRUM, RAMAN_SPECTRUM, VCD_ROTATIONAL_STRENGTH_SPECTRUM, P_DEPOLARIZATION_SPECTRUM, U_DEPOLARIZATION_SPECTRUM;

  static final Map<VIBRATIONAL_SPECTRUM, String> nameReference = new HashMap<VIBRATIONAL_SPECTRUM, String> ();
  static {
    nameReference.put(INFRARED_SPECTRUM, "Infrared Spectrum");
    nameReference.put(RAMAN_SPECTRUM, "Raman Spectrum");
    nameReference.put(VCD_ROTATIONAL_STRENGTH_SPECTRUM, "VCD Rotational Strength Spectrum");
    nameReference.put(P_DEPOLARIZATION_SPECTRUM, "P-Depolarization Spectrum");
    nameReference.put(U_DEPOLARIZATION_SPECTRUM, "U-Depolarization Spectrum");
  }

  public static String getSpectrumName(VIBRATIONAL_SPECTRUM spectrum) {
    return nameReference.get(spectrum);
  }

}
