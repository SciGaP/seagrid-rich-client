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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class VaspSnapshot {

  private double latticeVectors[][] = null;
  private List<VaspAtom> atoms = null;
  private Map<String, Double> energyDecomp = new HashMap<String, Double> (10);

  public VaspSnapshot() {
  }

  public void addEnergyTerm(String type, double value) {
    energyDecomp.put(type, new Double(value));
  }

  public double getEnergyTerm(String type) throws Exception {
    if (!energyDecomp.containsKey(type)) {
      throw new Exception("Snapshot does not have energy term " + type);
    }
    return energyDecomp.get(type);
  }

  public boolean hasEnergyTerm(String type) {
    return energyDecomp.containsKey(type);
  }

  public double[][] getLatticeVectors() {
    return latticeVectors;
  }

  public void setLatticeVectors(double vectors[][]) {
    if (latticeVectors == null) {
      latticeVectors = new double[3][3];
    }
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        latticeVectors[i][j] = vectors[i][j];
      }
    }
    if (atoms != null) {
      transformFracToCart(atoms, latticeVectors);
    }
  }

  public List<VaspAtom> getAtoms() {
    return atoms;
  }

  public void setAtoms(List<VaspAtom> at) {

    atoms = new ArrayList<VaspAtom> (at.size());
    for (int i = 0; i < at.size(); i++) {
      VaspAtom atom = new VaspAtom(at.get(i));
      atoms.add(atom);
    }
    if (latticeVectors != null) {
      transformFracToCart(atoms, latticeVectors);
    }

  }

  public void transformFracToCart(List<VaspAtom> at, double vectors[][]) {
    double x, y, z, x2 = 0, y2 = 0, z2 = 0;
    for (int i = 0; i < at.size(); i++) {
      VaspAtom atom = at.get(i);
      x = atom.getX();
      y = atom.getY();
      z = atom.getZ();
      x2 = x * vectors[0][0] + y * vectors[1][0] + z * vectors[2][0];
      y2 = x * vectors[0][1] + y * vectors[1][1] + z * vectors[2][1];
      z2 = x * vectors[0][2] + y * vectors[1][2] + z * vectors[2][2];
      atom.setCoordinates(x2, y2, z2);
    }

  }
}
