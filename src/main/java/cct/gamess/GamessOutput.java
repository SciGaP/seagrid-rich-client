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
package cct.gamess;

import cct.Constants;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.CCTAtomTypes;
import cct.modelling.GeneralMolecularDataParser;
import cct.modelling.Molecule;
import java.util.logging.Logger;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2007</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class GamessOutput extends GeneralMolecularDataParser {

  static final float ONE_BOHR_FLOAT = Constants.ONE_BOHR_FLOAT; // In Angstrom
  static final Logger logger = Logger.getLogger(GamessOutput.class.getCanonicalName());
  Map<String, String> outputResume = new LinkedHashMap<String, String>(100);
  boolean normalTermination = false;
  List<List> Geometries = new ArrayList<List>();
  int natoms = -1;
  String runTitle = null;

  public GamessOutput() {
  }

  /**
   *
   * @param filename
   * @throws Exception
   * @deprecated Use parseData methods instead
   */
  @Deprecated
  public void parseGamessOutputFile(String filename) throws Exception {
    String line;
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      parseData(in);
    } catch (IOException e) {
      System.err.println("parseGamessOutputFile: error parsing " + filename + " : " + e.getMessage());
      throw new Exception("Error parsing " + filename + " : " + e.getMessage());
    }

  }

  public static GamessAtom parseAtomData(String data) throws Exception {
    GamessAtom atom = new GamessAtom();
    StringTokenizer st = new StringTokenizer(data, " \t");

    if (st.countTokens() < 5) {
      throw new Exception(
          "Expecting 5 tokens while reading molecular geometry, got " + data);
    }

    atom.setName(st.nextToken());

    try {
      atom.setNuclearCharge(Float.parseFloat(st.nextToken()));
      atom.setAtomicNumber((int) atom.getNuclearCharge());
      atom.setX(Float.parseFloat(st.nextToken()) * ONE_BOHR_FLOAT);
      atom.setY(Float.parseFloat(st.nextToken()) * ONE_BOHR_FLOAT);
      atom.setZ(Float.parseFloat(st.nextToken()) * ONE_BOHR_FLOAT);
    } catch (Exception ex) {
      throw new Exception("Error while parsing float point atom data: " + data);
    }

    return atom;
  }

  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() + " : molec == null");
    }
    if (Geometries.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : didn't find atoms in file");
    }

    molec.addMonomer("GAMESS");

    List<GamessAtom> atoms = Geometries.get(Geometries.size() - 1);

    for (int i = 0; i < atoms.size(); i++) {
      GamessAtom ga = atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.getName());
      atom.setAtomicNumber(ga.getAtomicNumber());
      atom.setXYZ(ga.getX(), ga.getY(), ga.getZ());
      molec.addAtom(atom);
    }
  }

  public int validFormatScore(BufferedReader in) throws Exception {
    GamessOutput g = new GamessOutput();
    try {
      g.parseData(in);
    } catch (Exception ex) {
      return 0;
    }

    if (g.getMoleculeInterface() == null || g.getMoleculeInterface().getNumberOfAtoms() < 1) {
      return 0;
    }
    return 10;
  }

  public void parseData(BufferedReader in) throws Exception {

    String line;
    try {

      while ((line = in.readLine()) != null) {

        if (line.contains("GAMESS VERSION")) {
          this.outputResume.put("Gamess Ver", line);
        } else if (line.contains("RUNNING WITH")) {
          this.outputResume.put("Parallel", line);
        } else if (line.contains("THE POINT GROUP")) {
          this.outputResume.put("point group", line);
        } else if (line.contains("THE ORDER OF")) {
          this.outputResume.put("order", line);
        } else if (line.contains("ATOM      ATOMIC")) {
          if (Geometries.size() == 0) {
            List<GamessAtom> atoms = new ArrayList<GamessAtom>();
            // Skip a line
            if ((line = in.readLine()) == null) {
              throw new Exception("Unexpected end-of-file while reading molecular geometry");
            }

            while ((line = in.readLine()) != null) {
              if (line.trim().length() == 0) {
                break;
              }
              GamessAtom atom = parseAtomData(line);
              atoms.add(atom);
            }

            Geometries.add(atoms);
          }
        } else if (line.contains("BASIS OPTIONS")) {
          this.outputResume.put("basis0", line);
          if ((line = in.readLine()) == null) {
            outputResume.put("basis1", line);
            break;
          }
          int count = 1;
          while ((line = in.readLine()) != null) {
            if (line.trim().length() == 0) {
              break;
            }
            ++count;
            outputResume.put("basis" + count, line);
          }
        } else if (line.contains("RUN TITLE")) {
          outputResume.put("title0", line);
          if ((line = in.readLine()) == null) {
            outputResume.put("basis1", line);
          }
          if ((line = in.readLine()) == null) {
            outputResume.put("basis2", line);
            runTitle = line.trim();
          }
        }
      }
    } catch (IOException e) {
      logger.severe("Error parsing gamess output: " + e.getMessage());
      throw new Exception("Error parsing gamess output: " + e.getMessage());
    }

    MoleculeInterface mol = getMoleculeInterface();
    this.addMolecule(mol);
    getMolecularInterface(mol);
    Molecule.guessCovalentBonds(mol);
    Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
  }
}
