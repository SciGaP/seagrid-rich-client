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

package cct.gromacs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;

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
public class GromacsParserFactory {
   private GromacsParserFactory() {
   }

   public static MoleculeInterface parseGromacsCoordFile(String filename,
       MoleculeInterface mol) throws Exception {
      return parseGromacsCoordFile(filename, mol, null);
   }

   public static MoleculeInterface parseGromacsCoordFile(String filename,
       MoleculeInterface mol, Object dictionary) throws
       Exception {
      String line;
      BufferedReader in = null;
      int residuenr = -1, previousRes = -1;

      try {
         in = new BufferedReader(new FileReader(filename));

         // --- Reading molecular title
         // "%s\n", Title
         if ( (line = in.readLine()) == null) {
            in.close();
            throw new Exception(
                "parseGromacsCoordFile: ERROR: Unxpected End of file while reading molecule title");
         }

         mol.setName(line);

         // --- Reading number of atoms
         //"%5d\n", natoms

         if ( (line = in.readLine()) == null) {
            in.close();
            throw new Exception(
                "parseGromacsCoordFile: ERROR: Unxpected End of file while reading number of atoms");
         }

         int natoms = 0;
         try {
            natoms = Integer.parseInt(line.trim());
         }
         catch (Exception e) {
            in.close();
            throw new Exception(
                "parseGromacsCoordFile: ERROR: Error while parsing number of atoms: " +
                e.getMessage());
         }

         // --- Reading atoms
         //"%5d%5s%5s%5d%8.3f%8.3f%8.3f%8.4f%8.4f%8.4f\n",
         //residuenr,residuename,atomname,atomnr,x,y,z,vx,vy,vz

         for (int i = 0; i < natoms; i++) {

            AtomInterface atom = mol.getNewAtomInstance();

            if ( (line = in.readLine()) == null) {
               in.close();
               throw new Exception(
                   "parseGromacsCoordFile: ERROR: Unxpected End of file while reading atom " +
                   (i + 1));
            }

            // --- Residue number

            try {
               residuenr = Integer.parseInt(line.substring(0, 5).trim());
            }
            catch (Exception ex) {
               in.close();
               throw new Exception(
                   "parseGromacsCoordFile: ERROR: Error while parsing atom: " +
                   (i + 1) +
                   " Cannot parse atom's residue number: " + line);
            }

            // --- Residue name

            String residuename = "";
            try {
               residuename = line.substring(5, 10).trim();
            }
            catch (IndexOutOfBoundsException ex) {
               throw new Exception(
                   "parseGromacsCoordFile: ERROR: Error while parsing atom: " +
                   (i + 1) +
                   " Cannot parse atom's residue name: " + ex.getMessage() +
                   " : " + line);
            }

            // --- Atom name

            String atomname = "";
            try {
               atomname = line.substring(10, 15).trim();
            }
            catch (IndexOutOfBoundsException ex) {
               throw new Exception(
                   "parseGromacsCoordFile: ERROR: Error while parsing atom: " +
                   (i + 1) +
                   " Cannot parse atom's name: " + ex.getMessage() + " : " +
                   line);
            }

            atom.setName(atomname);

            // --- Skipping atom number...

            // --- Getting x,y,z

            float xyz;
            try {
               xyz = Float.parseFloat(line.substring(20, 28).trim());
               atom.setX(xyz * 10);
               xyz = Float.parseFloat(line.substring(28, 36).trim());
               atom.setY(xyz * 10);
               xyz = Float.parseFloat(line.substring(36, 44).trim());
               atom.setZ(xyz * 10);
            }
            catch (Exception ex) {
               in.close();
               throw new Exception(
                   "parseGromacsCoordFile: ERROR: Error while parsing atom: " +
                   (i + 1) +
                   " Cannot parse atom's coordinate(s): " + line);
            }

            // --- Getting element, etc

            int element = 0; // Dummy by default
            if (dictionary == null) {
               try {
                  element = Gromacs.getChemicalElement(residuename, atomname);
               }
               catch (Exception ex) {
                  System.err.println("Resolving chemical element: " +
                                     ex.getMessage());
               }
            }

            atom.setAtomicNumber(element);

            if (residuenr != previousRes) {
               mol.addMonomer(residuename);
               int n = mol.getNumberOfMonomers();
               mol.addAtom(atom, n - 1);
               previousRes = residuenr;
            }
            else {
               mol.addAtom(atom);
            }

         }

         // --- Read periodic box vectors
         // Box vectors (free format, space separated reals), values: v1(x) v2(y) v3(z) v1(y) v1(z) v2(x) v2(z) v3(x) v3(y),
         // the last 6 values may be omitted (they will be set to zero). Gromacs only supports boxes with v1(y)=v1(z)=v2(z)=0.

         // --- Program uses only the first three number

         if ( (line = in.readLine()) != null) {
            Float xyz[] = new Float[3];
            StringTokenizer st = new StringTokenizer(line, " \t", false);
            if (st.countTokens() < 3) {
               System.err.println("Warning: Reading Box vectors: expect at least 3 numbers... Ignored...");
            }
            else {
               try {
                  xyz[0] = Float.parseFloat(st.nextToken()) * 10;
                  xyz[1] = Float.parseFloat(st.nextToken()) * 10;
                  xyz[2] = Float.parseFloat(st.nextToken()) * 10;
                  mol.addProperty(MoleculeInterface.PeriodicBox, xyz);
               }
               catch (Exception ex) {
                  System.err.println("parseGromacsCoordFile: Warning: Error while parsing Box vectors: " + line + " Ignored...");
               }
            }
         }

         in.close();

         for (int i = 0; i < mol.getNumberOfMonomers(); i++) {
            mol.guessCovalentBondsInMonomer(i);
            if (i > 0) {
               mol.guessCovalentBondsBetweenMonomers(i - 1, i);
            }
         }

      }
      catch (Exception ex) {
         if (in != null) {
            in.close();
         }
         throw new Exception("parseGromacsCoordFile: ERROR: " + ex.getMessage());
      }

      return mol;
   }

}
