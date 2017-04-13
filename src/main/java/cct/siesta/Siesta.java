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

package cct.siesta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
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
 * @author not attributable
 * @version 1.0
 */
public class Siesta {
  protected Siesta() {
  }

  public static AtomicCoordinatesFormat getDefaultAtomicCoordinatesFormat() {
    return AtomicCoordinatesFormat.NotScaledCartesianBohr;
  }

  /**
   * Creates a new set of ChemicalSpeciesLabels
   * @param molec MoleculeInterface - molecule
   * @return Map - ChemicalSpeciesLabels
   */
  public static Map<Integer, ChemicalSpeciesLabel> formChemicalSpeciesLabels(MoleculeInterface molec) {
    Map<Integer, ChemicalSpeciesLabel> chemicalSpeciesLabels = new HashMap<Integer, ChemicalSpeciesLabel> ();
    Map<String, Integer> labels = new HashMap<String, Integer> ();

    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);
      int element = atom.getAtomicNumber();
      String symbol = ChemicalElements.getElementSymbol(element);

      atom.setProperty(SiestaInterface.SIESTA_SPECIES_LABEL, symbol);
      if (labels.containsKey(symbol)) {
        atom.setProperty(SiestaInterface.SIESTA_SPECIES_NUMBER, labels.get(symbol));
        continue;
      }

      Integer number = chemicalSpeciesLabels.size() + 1;
      atom.setProperty(SiestaInterface.SIESTA_SPECIES_NUMBER, number);
      ChemicalSpeciesLabel species = new ChemicalSpeciesLabel(number, element, symbol);
      chemicalSpeciesLabels.put(number, species);
      labels.put(symbol, number);
    }

    labels = null;
    return chemicalSpeciesLabels;
  }

  public static Map<Integer, ChemicalSpeciesLabel> updateChemicalSpeciesLabels(MoleculeInterface molec, Map<Integer,
      ChemicalSpeciesLabel> chemicalSpeciesLabels) {

    Map<String, Integer> labels = new HashMap<String, Integer> ();
    Set set = chemicalSpeciesLabels.entrySet();
    Iterator iter = set.iterator();

    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      Integer number = (Integer) me.getKey();
      ChemicalSpeciesLabel species = (ChemicalSpeciesLabel) me.getValue();
      labels.put(species.label, number);
    }

    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface atom = molec.getAtomInterface(i);

      Object obj = atom.getProperty(SiestaInterface.SIESTA_SPECIES_LABEL);
      int element = atom.getAtomicNumber();

      if (obj == null) {

        String symbol = ChemicalElements.getElementSymbol(element);
        if (labels.containsKey(symbol)) {

        }
        else {

          Integer number = getUniqueLabelNumber(labels.values().toArray());
          labels.put(symbol, number);
          ChemicalSpeciesLabel species = new ChemicalSpeciesLabel(number, element, symbol);
          chemicalSpeciesLabels.put(number, species);
        }
        atom.setProperty(SiestaInterface.SIESTA_SPECIES_LABEL, symbol);
        atom.setProperty(SiestaInterface.SIESTA_SPECIES_NUMBER, labels.get(symbol));
        continue;
      }

      String symbol = obj.toString();
      if (labels.containsKey(symbol)) {

      }
      else {
        Integer number = getUniqueLabelNumber(labels.values().toArray());
        labels.put(symbol, number);
        ChemicalSpeciesLabel species = new ChemicalSpeciesLabel(number, element, symbol);
        chemicalSpeciesLabels.put(number, species);
      }

      atom.setProperty(SiestaInterface.SIESTA_SPECIES_LABEL, symbol);
      atom.setProperty(SiestaInterface.SIESTA_SPECIES_NUMBER, labels.get(symbol));
    }

    labels = null;
    return chemicalSpeciesLabels;
  }

  private static Integer getUniqueLabelNumber(Object[] integers) {
    Arrays.sort(integers);
    if (integers == null) {
      return 1;
    }
    for (int i = 1; i <= integers.length; i++) {
      if (i == (Integer) integers[i]) {
        return i;
      }
    }
    return integers.length + 1;
  }
}
