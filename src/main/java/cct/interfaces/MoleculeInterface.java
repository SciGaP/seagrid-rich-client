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
package cct.interfaces;

import java.util.List;
import java.util.Map;

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
public interface MoleculeInterface {

  Integer WIREFRAME = 0;
  Integer STICKS = 1;
  Integer BALL_AND_STICKS = 2;
  Integer SPACEFILL = 3;
  String OUTPUT_RESULTS = "_OutputResults";
  String VIBRATION_RENDERER = "_VibrationRenderer";
  String RenderingStyle = "RenderingStyle";
  String AtomicSets = "AtomicSets";
  String ChargeProperty = "Charge";
  String MultiplicityProperty = "Multiplicity";
  String SCFEnergyProperty = "SCF Energy";
  String MP2EnergyProperty = "MP2 Energy";
  String MP3EnergyProperty = "MP3 Energy";
  String MP4DEnergyProperty = "MP4D Energy";
  String MP4DQEnergyProperty = "MP4DQ Energy";
  String MP4SDQEnergyProperty = "MP4SDQ Energy";
  String CCSDEnergyProperty = "CCSD Energy";
  String CCSDTEnergyProperty = "CCSDT Energy";
  String DipoleMomentProperty = "Dipole";
  String PolarizabilityProperty = "Polarizability";
  String MullikenChargesProperty = "Mulliken charges";
  String ESPChargesProperty = "ESP charges";
  String FrameGroupProperty = "FrameGroup";
  String S_SquaredProperty = "S**2";
  String ZeroPointCorrProperty = "ZPCorr";
  String EnergyThermCorrProperty = "EnergyTC";
  String EnthalpyThermCorrProperty = "EnthalpyTC";
  String GibbsThermCorrProperty = "GibbsTC";
  String ImaginaryFreqsProperty = "ImaginaryFreqs";
  String SurfacesProperty = "Surfaces";
  String NotesProperty = "Notes";
  String ProgramProperty = "Program";
  String VersionProperty = "Version";
  String PeriodicBox = "PeriodicBox";
  String LATTICE_VECTORS = "LatticeVectors";
  String LATTICE_PARAMETERS = "LatticeParamaters";

  void addAtom(AtomInterface a);

  void addAtom(AtomInterface a, int monomer_number);

  void addAtom(AtomInterface a, int n, String name);

  void addBond(BondInterface b);

  void addMonomer(String monomer_name);

  void addProperty(String propName, Object value);

  /**
   * Adds atoms from molecule "mol" (not their copies)
   *
   * @param mol MoleculeInterface
   */
  void mergeMolecule(MoleculeInterface mol);

  /**
   * Adds COPIES of atoms from molecule "mol"
   *
   * @param mol MoleculeInterface
   */
  void appendMolecule(MoleculeInterface mol);

  void centerMolecule(float x, float y, float z);

  AtomInterface deleteAtom(int n);

  BondInterface deleteBond(BondInterface bond_to_delete);

  int getAtomIndex(AtomInterface a);

  AtomInterface getAtomInterface(int i);

  int getBondIndex(BondInterface b);

  BondInterface getBondInterface(int i);

  MonomerInterface getMonomerInterface(int n);

  String getName();

  AtomInterface getNewAtomInstance() throws NullPointerException;

  BondInterface getNewBondInstance() throws NullPointerException;

  BondInterface getNewBondInstance(AtomInterface a, AtomInterface b) throws
          NullPointerException;

  int getNumberOfAtoms();

  int getNumberOfBonds();

  int getNumberOfMonomers();

  List getMolecularSubstructure();

  Map getProperties();

  Object getProperty(String key);

  float getXmax();

  float getXmin();

  float getYmax();

  float getYmin();

  float getZmax();

  float getZmin();

  MoleculeInterface getInstance();

  void guessCovalentBondsBetweenMonomers(int n, int m);

  void guessCovalentBondsInMonomer(int n);

  void setName(String name);
}
