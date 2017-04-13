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

package cct.qchem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class QChemOutput {

  Map<String, String> outputResume = new LinkedHashMap<String, String> ();
  boolean normalTermination = false;

  List<QChemAtom> Atoms = new ArrayList<QChemAtom> ();

  public QChemOutput() {
  }

  public static void main(String[] args) {
    QChemOutput qchemoutput = new QChemOutput();
  }

  public void parseQChemOutputFile(String filename, boolean update) throws Exception {
    String line, token;
    StringTokenizer st;
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));

      while ( (line = in.readLine()) != null) {

        if (line.startsWith("User input:")) {
          // --- User input ...
        }

        else if (line.contains("Q-Chem") && line.contains("Version")) {
          this.outputResume.put("Q_Chem version", line);
        }

        else if (line.contains("Standard Nuclear Orientation (Angstroms)") ||
                 line.contains("Nuclear coordinates and velocities (a.u.)")) {
          float factor = 1.0f;
          if (line.contains("(a.u.)")) {
            factor = 0.529177249f;
          }
          int linesToSkip = 2;
          if (line.contains("Nuclear coordinates and velocities (a.u.)")) {
            linesToSkip = 3;
          }

          // --- Skip n lines
          for (int i = 0; i < linesToSkip; i++) {
            if ( (line = in.readLine()) == null) {
              break;
            }
          }
          if (line == null) {
            break;
          }

          line = "";
          List<QChemAtom> atoms = new ArrayList<QChemAtom> (); // local array
          while ( (line = in.readLine()) != null && (!line.contains("--------------------"))) {

            st = new StringTokenizer(line, " \t");
            if (st.countTokens() < 5) {
              System.err.println("Reading Standard Nuclear Orientation : expecting 5 tokens, got " + line);
              break;
            }

            QChemAtom atom = new QChemAtom();

            // --- skip the first token
            st.nextToken();

            // --- Atom name

            token = st.nextToken();
            atom.setName(token);
            atom.setAtomicNumber(QChem.getAtomicNumber(token));

            // --- x,y,z coordinates

            try {
              token = st.nextToken();
              atom.setX(Float.parseFloat(token) * factor);
              token = st.nextToken();
              atom.setY(Float.parseFloat(token) * factor);
              token = st.nextToken();
              atom.setZ(Float.parseFloat(token) * factor);

            }
            catch (Exception ex) {
              System.err.println("Reading atom's coordinates in Standard Nuclear Orientation: error parsing coordinate: " +
                                 line);
              break;
            }

            atoms.add(atom);

          }

          if (Atoms.size() == 0) {
            Atoms = atoms;
          }
          else if (Atoms.size() == atoms.size()) {
            Atoms = atoms;
          }

          if (line == null) {
            break;
          }
        }

        else if (line.contains("Molecular Point Group")) {
          this.outputResume.put("Molecular Point Group", line);
        }
        else if (line.contains("Largest Abelian Subgroup")) {
          this.outputResume.put("Largest Abelian Subgroup", line);
        }

        else if (line.contains("There are") && line.contains("alpha and")) {
          this.outputResume.put("electrons", line);
        }
        else if (line.contains("Requested basis set is")) {
          this.outputResume.put("Requested basis set is", line);
        }

        else if (line.contains("AB INITIO MOLECULAR DYNAMICS")) {
          this.outputResume.put("AB INITIO MOLECULAR DYNAMICS 0", "=======================================================");
          this.outputResume.put("AB INITIO MOLECULAR DYNAMICS 1", line);
          this.outputResume.put("AB INITIO MOLECULAR DYNAMICS 2", "=======================================================");
        }

        else if (line.contains("TIME STEPS COMPLETED")) {
          this.outputResume.put("TIME STEPS COMPLETED", line);
        }

        else if (line.contains("Step-to-Step energy fluctuations")) {
          this.outputResume.put("Step-to-Step energy fluctuations", line);
          if ( (line = in.readLine()) != null) {
            this.outputResume.put("Step-to-Step energy fluctuations 1", line);
          }
        }

        else if (line.contains("Overall energy fluctuations")) {
          this.outputResume.put("Overall energy fluctuations", line);
          if ( (line = in.readLine()) != null) {
            this.outputResume.put("Overall energy fluctuations 1", line);
          }
        }

        else if (line.contains("Kinetic energy fluctuations")) {
          this.outputResume.put("Kinetic energy fluctuations", line);
          if ( (line = in.readLine()) != null) {
            this.outputResume.put("Kinetic energy fluctuations 1", line);
          }
        }

        else if (line.contains("Potential energy fluctuations")) {
          this.outputResume.put("Potential energy fluctuations", line);
          if ( (line = in.readLine()) != null) {
            this.outputResume.put("Potential energy fluctuations 1", line);
          }
        }

        else if (line.contains("OPTIMIZATION CONVERGED")) {
          outputResume.put("OPTIMIZATION CONVERGED 0", " ******************************");
          outputResume.put("OPTIMIZATION CONVERGED", line);
          outputResume.put("OPTIMIZATION CONVERGED 1", " ******************************");
        }

        else if (line.contains("Mulliken Net Atomic Charges")) {
          outputResume.put("Mulliken", "\n" + line);
          if ( (line = in.readLine()) != null) {
            outputResume.put("Mulliken a", line);
          }
          if ( (line = in.readLine()) != null) {
            outputResume.put("Mulliken b", line);
          }
          if ( (line = in.readLine()) != null) {
            outputResume.put("Mulliken c", line);
          }
          for (int i = 0; i < Atoms.size(); i++) {
            if ( (line = in.readLine()) != null) {
              outputResume.put("Mulliken " + String.valueOf(i), line);
            }
          }
          if ( (line = in.readLine()) != null) {
            outputResume.put("Mulliken d", line);
          }
          if ( (line = in.readLine()) != null) {
            outputResume.put("Mulliken e", line + "\n");
          }
        }

        else if (line.contains("Dipole Moment ")) {
          outputResume.put("Dipole 0", line);
          if ( (line = in.readLine()) != null) {
            outputResume.put("Dipole 1", line);
          }
          if ( (line = in.readLine()) != null) {
            outputResume.put("Dipole 2", line);
          }
        }

        else if (line.contains("Quadrupole Moments ")) {
          outputResume.put("Quadrupole 0", line);
          if ( (line = in.readLine()) != null) {
            outputResume.put("Quadrupole 1", line);
          }
          if ( (line = in.readLine()) != null) {
            outputResume.put("Quadrupole 2", line);
          }
        }

        else if (line.contains("Maximum     Tolerance    Cnvgd")) {
          outputResume.put("Cnvgd 0", line);
          if ( (line = in.readLine()) != null) {
            outputResume.put("Cnvgd 1", line);
          }
          if ( (line = in.readLine()) != null) {
            outputResume.put("Cnvgd 2", line);
          }
          if ( (line = in.readLine()) != null) {
            outputResume.put("Cnvgd 3", line);
          }
        }

        else if (line.contains("Convergence criterion met")) {
          outputResume.put("Energy cnv", line);
        }

        else if (line.contains("Energy is")) {
          outputResume.put("Energy is", line);
        }

        else if (line.contains("Optimization Cycle")) {
          outputResume.put("Cycle", line);
        }

        else if (line.contains("Total job time")) {
          this.outputResume.put("Total job time", line);
          normalTermination = true;
          break; // Don't read any further...
        }

      }
    }
    catch (Exception ex) {
      throw new Exception("Error reading QChem output: " + ex.getMessage());
    }
  }

  public String getOutputResume() {
    StringWriter sWriter = new StringWriter();
    Set set = outputResume.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String line = me.getValue().toString();
      sWriter.write(line + "\n");
    }

    if (!normalTermination) {
      sWriter.write(
          "\n***** !!!!! IT IS NOT A NORMAL TERMINATION of Q-Chem !!!!! *****");
    }
    return sWriter.toString();
  }

  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : molec == null");
    }
    if (Atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : no QChem atoms");
    }

    //molec.addProperty(MoleculeInterface.ChargeProperty, new Integer(netCharge));
    //molec.addProperty(MoleculeInterface.MultiplicityProperty, new Integer(spinMultiplicity));

    molec.addMonomer("QChem");

    for (int i = 0; i < Atoms.size(); i++) {
      QChemAtom ga = Atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.getName());
      atom.setAtomicNumber(ga.getAtomicNumber());
      atom.setXYZ(ga.getX(), ga.getY(), ga.getZ());
      molec.addAtom(atom);
    }
  }

}
