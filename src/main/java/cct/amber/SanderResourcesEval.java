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

package cct.amber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SanderResourcesEval {
  static final String platf_01 = "SGI Altix 3700 Bx2";

  String currentPlatform = platf_01;
  int numberOfAtoms = 20000;
  List platform = new ArrayList();
  Map staticMemory = new HashMap(); // In Mb

  public SanderResourcesEval() {
    platform.add(platf_01);

    staticMemory.put(platf_01, new Float(60.0));
  }

  public int getNumberOfAtoms() {
    return numberOfAtoms;
  }

  public float evaluateMemoryRequirement(Sander8JobControl sjc) {
    float numAtoms;
    if (sjc.getNumberOfAtoms() > 0) {
      numberOfAtoms = sjc.getNumberOfAtoms();
    }
    numAtoms = numberOfAtoms;
    float cutoff = sjc.getCutoff();

    // --- Static memory size for a given platform
    Float f = (Float) staticMemory.get(currentPlatform);
    float size = f.floatValue();

    // ... plus size of dynamically allocated memory
    float size_dyn = (1.677e-3f + cutoff * ( -7.196e-5f) +
                      cutoff * cutoff * 1.264e-5f +
                      cutoff * cutoff * cutoff * 1.174e-6f) * numAtoms;
    if (size_dyn < 0) {
      size_dyn = 0.0f;
    }
    return size + size_dyn;
  }

  public float evaluateTimeRequirement(Sander8JobControl sjc) {
    float numAtoms;
    if (sjc.getNumberOfAtoms() > 0) {
      numberOfAtoms = sjc.getNumberOfAtoms();
    }
    numAtoms = numberOfAtoms;
    float cutoff = sjc.getCutoff();
    if (cutoff < 1.0f) {
      cutoff = 8.0f;
    }
    float steps = sjc.numberOfEnergySteps();

    float time = (8.7266e-6f + cutoff * ( -9.2626e-7f) +
                  cutoff * cutoff * 4.5207e-8f +
                  cutoff * cutoff * cutoff * 1.2165e-8f) * numAtoms * steps;
    if (time < 0) {
      time = 0.0f;
    }
    return time;
  }

}
