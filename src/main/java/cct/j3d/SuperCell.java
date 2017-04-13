/* ***** BEGIN LICENSE BLOCK *****
   Version: Apache 2.0/GPL 2.0/LGPL 2.1

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

 ***** END LICENSE BLOCK *****
 */

package cct.j3d;

import java.util.ArrayList;
import java.util.List;

import cct.interfaces.AtomInterface;
import cct.interfaces.CellViewManagerInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.CELL_PARAMETER;

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
public class SuperCell
    implements CellViewManagerInterface {

  private double latticeVectors[][] = null;
  private MoleculeInterface molecule = null;
  private MoleculeInterface referenceMolecule = null;
  private Java3dUniverse java3dUniverse = null;
  private boolean drawBondsBetweenCells = false; //!!!
  private SuperCube superCube = new SuperCube();

  private int[] repetitionFactors = {
      1, 1, 1, 0, 0, 0}; // Keeps information about repetition factors in a,b,c,-a,-b,-c directions

  public SuperCell(Java3dUniverse j3d) {

    java3dUniverse = j3d;

  }

  private void initialize() throws Exception {

    if (referenceMolecule != null) {
      return;
    }

    molecule = java3dUniverse.getMoleculeInterface();

    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      throw new Exception("No molecule");
    }

    if (latticeVectors != null) {
      return;
    }

    referenceMolecule = molecule.getInstance();
    referenceMolecule.appendMolecule(molecule);

    List<AtomInterface> atoms = new ArrayList<AtomInterface> (molecule.getNumberOfAtoms());
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      atoms.add(molecule.getAtomInterface(i));
    }
    CellObject cellObj = new CellObject(atoms);
    superCube.setValue(0, 0, 0, cellObj);

    Object obj = molecule.getProperty(MoleculeInterface.LATTICE_VECTORS);
    if (obj instanceof double[][]) {
      latticeVectors = new double[3][3];
      double[][] vectors = (double[][]) obj;
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          latticeVectors[i][j] = vectors[i][j];
        }
      }
    }
    else if (obj instanceof float[][]) {
      latticeVectors = new double[3][3];
      float[][] vectors = (float[][]) obj;
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          latticeVectors[i][j] = vectors[i][j];
        }
      }
    }
    else {
      System.err.println(this.getClass().getCanonicalName() +
                         ": no lattice vectors info in molecule. Generating the default one");
      latticeVectors = new double[3][3];
      latticeVectors[0][0] = (molecule.getXmax() - molecule.getXmin());
      latticeVectors[1][1] = (molecule.getYmax() - molecule.getYmin());
      latticeVectors[2][2] = (molecule.getZmax() - molecule.getZmin());
    }

  }

  @Override
  public void replicateCell(CELL_PARAMETER direction, int repetition_factor) throws Exception {

    try {
      initialize();
    }
    catch (Exception ex) {
      throw new Exception(ex.getMessage());
    }

    int index = 0;
    int index_1, index_2, index_3;
    int factor = Math.abs(repetition_factor);
    switch (direction) {
      case ALPHA:
      case BETHA:
      case GAMMA:
        return;
      case A:
        index = 0;
        if (repetition_factor < 0) {
          index = 3;
        }
        index_1 = 0;
        index_2 = 1;
        index_3 = 2;
        break;
      case B:
        index = 1;
        if (repetition_factor < 0) {
          index = 4;
        }
        index_1 = 1;
        index_2 = 0;
        index_3 = 2;
        break;
      case C:
        index = 2;
        if (repetition_factor < 0) {
          index = 5;
        }
        index_1 = 2;
        index_2 = 0;
        index_3 = 1;
        break;

      default:
        return;
    }

    if (repetitionFactors[index] == factor) {
      return; // Nothing to replicate
    }
    else if (repetitionFactors[index] < factor) { // Add replicas

      MoleculeInterface cell = referenceMolecule.getInstance();
      cell.appendMolecule(referenceMolecule);

      replicateMolecule(direction, repetition_factor);
    }

    else if (repetitionFactors[index] > factor) { // remove replicas
      deReplicateMolecule(direction, repetition_factor);
    }
  }

  private void replicateMolecule(CELL_PARAMETER direction, int repetition_factor) {

    int i_start = -repetitionFactors[3];
    int i_end = repetitionFactors[0];
    int j_start = -repetitionFactors[4];
    int j_end = repetitionFactors[1];
    int k_start = -repetitionFactors[5];
    int k_end = repetitionFactors[2];

    double x2, y2, z2, an, bn, cn;

    if (repetition_factor < 0) {
      switch (direction) {
        case A:
          i_start = repetition_factor;
          break;
        case B:
          j_start = repetition_factor;
          break;
        case C:
          k_start = repetition_factor;
          break;
      }
    }
    else {
      switch (direction) {
        case A:
          i_end = repetition_factor;
          break;
        case B:
          j_end = repetition_factor;
          break;
        case C:
          k_end = repetition_factor;
          break;
      }
    }

    int factor = Math.abs(repetition_factor);

    for (int i = i_start; i < i_end; i++) {
      an = i;
      for (int j = j_start; j < j_end; j++) {
        bn = j;
        for (int k = k_start; k < k_end; k++) {

          try {
            if (superCube.getValue(i, j, k) != null) {
              continue;
            }
          }
          catch (Exception ex) {

          }
          if (i == 0 && j == 0 && k == 0) {
            continue;
          }

          cn = k;

          MoleculeInterface cell = referenceMolecule.getInstance();
          cell.appendMolecule(referenceMolecule);

          List<AtomInterface> atoms = new ArrayList<AtomInterface> (cell.getNumberOfAtoms());

          for (int ind = 0; ind < cell.getNumberOfAtoms(); ind++) {
            AtomInterface atom = cell.getAtomInterface(ind);
            atoms.add(atom);

            x2 = an * latticeVectors[0][0] + bn * latticeVectors[1][0] + cn * latticeVectors[2][0];
            y2 = an * latticeVectors[0][1] + bn * latticeVectors[1][1] + cn * latticeVectors[2][1];
            z2 = an * latticeVectors[0][2] + bn * latticeVectors[1][2] + cn * latticeVectors[2][2];

            atom.setX(atom.getX() + (float) x2);
            atom.setY(atom.getY() + (float) y2);
            atom.setZ(atom.getZ() + (float) z2);
          }

          // --- We MERGE molecules, NOT just APPENDING
          java3dUniverse.appendMolecule(cell, true);

          CellObject cellObj = new CellObject(atoms);
          try {
            superCube.setValue(i, j, k, cellObj);
          }
          catch (Exception ex) {
            System.err.println(ex.getMessage() + " Ignored...");
          }
        }
      }
    }

    //x2 = x * vectors[0][0] + y * vectors[1][0] + z * vectors[2][0];
    //y2 = x * vectors[0][1] + y * vectors[1][1] + z * vectors[2][1];
    //z2 = x * vectors[0][2] + y * vectors[1][2] + z * vectors[2][2];

    if (repetition_factor > 0) {
      switch (direction) {
        case A:
          repetitionFactors[0] = repetition_factor;
          break;
        case B:
          repetitionFactors[1] = repetition_factor;
          break;
        case C:
          repetitionFactors[2] = repetition_factor;
          break;
      }
      //repetitionFactors[0] = factor;
    }
    else {
      switch (direction) {
        case A:
          repetitionFactors[3] = factor;
          break;
        case B:
          repetitionFactors[4] = factor;
          break;
        case C:
          repetitionFactors[5] = factor;
          break;
      }
      //repetitionFactors[3] = factor;
    }
  }

  private void deReplicateMolecule(CELL_PARAMETER direction, int repetition_factor) {

    int i_start = -repetitionFactors[3];
    int i_end = repetitionFactors[0];
    int j_start = -repetitionFactors[4];
    int j_end = repetitionFactors[1];
    int k_start = -repetitionFactors[5];
    int k_end = repetitionFactors[2];

    if (repetition_factor <= 0) {
      switch (direction) {
        case A:
          i_end = repetition_factor;
          break;
        case B:
          j_end = repetition_factor;
          break;
        case C:
          k_end = repetition_factor;
          break;
      }
    }
    else {
      switch (direction) {
        case A:
          i_start = repetition_factor;
          break;
        case B:
          j_start = repetition_factor;
          break;
        case C:
          k_start = repetition_factor;
          break;
      }

    }

    int factor = Math.abs(repetition_factor);

    for (int i = i_start; i < i_end; i++) {
      for (int j = j_start; j < j_end; j++) {
        for (int k = k_start; k < k_end; k++) {

          try {
            if (superCube.getValue(i, j, k) == null) {
              continue;
            }
          }
          catch (Exception ex) {

          }
          if (i == 0 && j == 0 && k == 0) {
            continue;
          }

          CellObject cellObj = null;
          try {
            cellObj = (CellObject) superCube.getValue(i, j, k);
          }
          catch (Exception ex) {

          }
          List<AtomInterface> atoms = cellObj.getAtoms();

          for (int ind = 0; ind < atoms.size(); ind++) {
            AtomInterface atom = atoms.get(ind);
            java3dUniverse.deleteAtom(atom);
          }

          try {
            superCube.setValue(i, j, k, null);
          }
          catch (Exception ex) {
            System.err.println(ex.getMessage() + " Ignored...");
          }
        }
      }
    }

    //x2 = x * vectors[0][0] + y * vectors[1][0] + z * vectors[2][0];
    //y2 = x * vectors[0][1] + y * vectors[1][1] + z * vectors[2][1];
    //z2 = x * vectors[0][2] + y * vectors[1][2] + z * vectors[2][2];

    if (repetition_factor > 0) {
      switch (direction) {
        case A:
          repetitionFactors[0] = repetition_factor;
          break;
        case B:
          repetitionFactors[1] = repetition_factor;
          break;
        case C:
          repetitionFactors[2] = repetition_factor;
          break;
      }
      //repetitionFactors[0] = factor;
    }
    else {
      switch (direction) {
        case A:
          repetitionFactors[3] = factor;
          break;
        case B:
          repetitionFactors[4] = factor;
          break;
        case C:
          repetitionFactors[5] = factor;
          break;
      }
      //repetitionFactors[3] = factor;
    }
  }

  class SuperCube {
    private int iLow = -50, iHigh = 50;
    private int jLow = -50, jHigh = 50;
    private int kLow = -50, kHigh = 50;

    private boolean reallocatable = true;

    private Object[][][] cube = null;

    public SuperCube() {
      cube = new Object[iHigh - iLow][jHigh - jLow][kHigh - kLow];
    }

    public void setValue(int i, int j, int k, Object value) throws Exception {

      checkIndexes(i, j, k);

      int ii = i - iLow;
      int jj = j - jLow;
      int kk = k - kLow;

      cube[ii][jj][kk] = value;
    }

    public Object getValue(int i, int j, int k) throws Exception {

      checkIndexes(i, j, k);

      int ii = i - iLow;
      int jj = j - jLow;
      int kk = k - kLow;

      return cube[ii][jj][kk];
    }

    private void checkIndexes(int i, int j, int k) throws Exception {
      if (i < iLow) {
        throw new Exception("i < iLow");
      }
      if (j < jLow) {
        throw new Exception("j < jLow");
      }
      if (k < kLow) {
        throw new Exception("k < kLow");
      }
      if (i > iHigh) {
        throw new Exception("i > iHigh");
      }
      if (j > jHigh) {
        throw new Exception("j > jHigh");
      }
      if (k > kHigh) {
        throw new Exception("k > kHigh");
      }
    }
  }

  class CellObject {
    private List<AtomInterface> atoms = null;
    public CellObject(List<AtomInterface> at) {
      atoms = at;
    }

    public List<AtomInterface> getAtoms() {
      return atoms;
    }
  }
}
