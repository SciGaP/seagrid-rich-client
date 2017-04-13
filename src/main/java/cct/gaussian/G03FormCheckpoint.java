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

package cct.gaussian;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import cct.Constants;
import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */

enum TYPE {
  REAL, INTEGER, UNKNOWN
}

enum G03_CHECKPOINT_KEY_WORDS {
  FOpt, Number_of_electrons, Number_of_alpha_electrons, SCF_Energy, MP2_Energy, Total_Energy, Atomic_numbers, Number_of_atoms,
  Current_cartesian_coordinates
}

public class G03FormCheckpoint {

  static final Map<String, G03_CHECKPOINT_KEY_WORDS> keyWords = new HashMap<String, G03_CHECKPOINT_KEY_WORDS> ();

  static {
    keyWords.put("FOpt", G03_CHECKPOINT_KEY_WORDS.FOpt);
    keyWords.put("Number of electrons", G03_CHECKPOINT_KEY_WORDS.Number_of_electrons);
    keyWords.put("Number of alpha electrons", G03_CHECKPOINT_KEY_WORDS.Number_of_alpha_electrons);
    keyWords.put("SCF Energy", G03_CHECKPOINT_KEY_WORDS.SCF_Energy);
    keyWords.put("MP2 Energy", G03_CHECKPOINT_KEY_WORDS.MP2_Energy);
    keyWords.put("Total Energy", G03_CHECKPOINT_KEY_WORDS.Total_Energy);
    keyWords.put("Atomic numbers", G03_CHECKPOINT_KEY_WORDS.Atomic_numbers);
    keyWords.put("Number of atoms", G03_CHECKPOINT_KEY_WORDS.Number_of_atoms);
    keyWords.put("Current cartesian coordinates", G03_CHECKPOINT_KEY_WORDS.Current_cartesian_coordinates);
  }

  int nAtoms = 0;
  GaussianAtom[] atoms = null;

  public G03FormCheckpoint() {
  }

  public static void main(String[] args) {
    G03FormCheckpoint g03formcheckpoint = new G03FormCheckpoint();
  }

  public void parseFile(String fileName) throws Exception {
    String line, keyword, token;

    try {
      BufferedReader in = new BufferedReader(new FileReader(fileName));

      while ( (line = in.readLine()) != null) {
        if (line.length() < 44) {
          continue;
        }
        keyword = line.substring(0, 43).trim();
        if (!keyWords.containsKey(keyword)) {
          continue;
        }

        G03_CHECKPOINT_KEY_WORDS keyWord = keyWords.get(keyword);

        DataBunch data = new DataBunch();
          switch (keyWord) {
          case Number_of_atoms:
            data.extractNumericalData(line, in);
            nAtoms = data.int_vector[0];
            atoms = new GaussianAtom[nAtoms];
            for (int i = 0; i < nAtoms; i++) {
              atoms[i] = new GaussianAtom();
            }
            break;
          case Number_of_electrons:
            break;
          case Number_of_alpha_electrons:
            break;
          case SCF_Energy:
            break;
          case Total_Energy:
            break;
          case Atomic_numbers:
            data.extractNumericalData(line, in);
            for (int i = 0; i < nAtoms; i++) {
              atoms[i].element = data.int_vector[i];
            }
            break;
          case Current_cartesian_coordinates:
            data.extractNumericalData(line, in);
            for (int i = 0; i < nAtoms; i++) {
              atoms[i].xyz[0] = (float) data.real_vector[i * 3] * Constants.ONE_BOHR_FLOAT;
              atoms[i].xyz[1] = (float) data.real_vector[i * 3 + 1] * Constants.ONE_BOHR_FLOAT;
              atoms[i].xyz[2] = (float) data.real_vector[i * 3 + 2] * Constants.ONE_BOHR_FLOAT;
            }
            break;
        }

      }
    }
    catch (Exception ex) {
      throw new Exception("Cannot open file " + fileName + " : " + ex.getMessage());
    }
  }

  public void getMolecule(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() + " : molec == null");
    }
    if (nAtoms < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : no atoms");
    }

    molec.addMonomer("G03");

    for (int i = 0; i < nAtoms; i++) {
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ChemicalElements.getElementSymbol(atoms[i].getElement()));
      atom.setAtomicNumber(atoms[i].getElement());
      atom.setXYZ(atoms[i].getX(), atoms[i].getY(), atoms[i].getZ());
      molec.addAtom(atom);
    }

  }

  private class DataBunch {
    int[] int_vector = null;
    double[] real_vector = null;

    public DataBunch() {}

    public void extractNumericalData(String line, BufferedReader in) throws Exception {
      boolean vector;
      String token;
      TYPE valueType;
      int intValue, vecLength = 1;
      double realValue;

      vector = line.substring(47, 49).equals("N=");
      if (vector) {
        /*
         Info1-9                                    I   N=           9
         */
        vecLength = Integer.parseInt(line.substring(49, 61).trim());

      }
      else {
        vecLength = 1;
      }

      token = line.substring(43, 44);

      if (token.equals("R")) {
        valueType = TYPE.REAL;

        real_vector = new double[vecLength];

        if (vector) {
          readRealVector(in, real_vector);
        }
        else { // Single real number
          token = line.substring(49, 71).trim();
          try {
            real_vector[0] = Double.parseDouble(token);
          }
          catch (Exception ex) {
            throw new Exception("Cannot parse real value type: " + line + " " + ex.getMessage());
          }
        }
      }
      else if (token.equals("I")) {
        /*
         Nuclear charges                            R   N=          14
         */
        valueType = TYPE.INTEGER;

        int_vector = new int[vecLength];

        if (vector) {
          readIntVector(in, int_vector);
        }
        else {
          token = line.substring(49, 61).trim();
          try {
            int_vector[0] = Integer.parseInt(token);
          }
          catch (Exception ex) {
            throw new Exception("Cannot parse integer value type: " + line + "" + ex.getMessage());
          }
        }
      }

      else {
        valueType = TYPE.UNKNOWN;
        throw new Exception("Unknown value type: " + line);
      }
    }

    public void readRealVector(BufferedReader in, double[] r_vector) throws Exception {
      String line;
      int k, n = r_vector.length;
      int n_lines = n / 5;
      if (n_lines == 0) {
        n_lines = 1;
      }
      else if (n % 5 != 0) {
        ++n_lines;
      }
      /*
         2.99050349E+00 -1.06469478E+00 -2.46420243E-01  2.14195397E+00 -2.86706022E+00

       */
      k = 0;
      for (int i = 0; i < n_lines; i++) {
        line = in.readLine();
        int nnum = n - i * 5 > 5 ? 5 : n - i * 5;
        for (int j = 0; j < nnum; j++) {
          r_vector[k] = Double.parseDouble(line.substring(j * 16, j * 16 + 16).trim());
          ++k;
        }
      }
    }

    public void readIntVector(BufferedReader in, int[] i_vector) throws Exception {
      String line;
      int k, n = i_vector.length;
      int n_lines = n / 6;
      if (n_lines == 0) {
        n_lines = 1;
      }
      else if (n % 6 != 0) {
        ++n_lines;
      }
      k = 0;
      for (int i = 0; i < n_lines; i++) {
        line = in.readLine();
        int nnum = n - i * 6 > 6 ? 6 : n - i * 6;
        for (int j = 0; j < nnum; j++) {
          i_vector[k] = Integer.parseInt(line.substring(j * 12, j * 12 + 12).trim());
          ++k;
        }
      }
    }

  }

}
