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
import java.util.StringTokenizer;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
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
public class GaussianFragment {
   public GaussianFragment() {
   }

   public static void main(String[] args) {
      GaussianFragment parsegaussianfragment = new GaussianFragment();
   }

   public static MoleculeInterface parseGaussianFragmentFile(MoleculeInterface
       mol,
       String filename) throws Exception {
      String line;
      try {
         BufferedReader in = new BufferedReader(new FileReader(filename));

         // --- Reading fragment name
         if ( (line = in.readLine()) == null) {
            throw new Exception("parseGaussianOutputFile: ERROR: Unxpected End of file while reading fragment name");
         }
         mol.setName(line);

         // --- Reading number of atoms
         if ( (line = in.readLine()) == null) {
            throw new Exception("parseGaussianOutputFile: ERROR: Unxpected End of file while reading number of atoms");
         }

         int natoms = 0;
         try {
            natoms = Integer.parseInt(line.trim());
         }
         catch (Exception e) {
            throw new Exception(
                "parseGaussianOutputFile: ERROR: Error while parsing number of atoms: " +
                e.getMessage());
         }

         // --- Reading atoms

         for (int i = 0; i < natoms; i++) {

            AtomInterface atom = mol.getNewAtomInstance();

            if ( (line = in.readLine()) == null) {
               throw new Exception(
                   "parseGaussianOutputFile: ERROR: Unxpected End of file while atom " +
                   (i + 1));
            }
            StringTokenizer st = new StringTokenizer(line, " ");
            if (st.countTokens() < 4) {
               throw new Exception(
                   "parseGaussianOutputFile: ERROR: Error while parsing atom: " +
                   (i + 1) +
                   " Number of tokens < 4: " + line);
            }

            // --- Getting element

            int element;
            try {
               element = Integer.parseInt(st.nextToken());
               if (element < 1) {
                  element = 0;
               }
               atom.setAtomicNumber(element);
            }
            catch (Exception ex) {
               throw new Exception(
                   "parseGaussianOutputFile: ERROR: Error while parsing atom: " +
                   (i + 1) +
                   " Cannot parse atom number: " + line);
            }

            // --- Getting x,y,z

            float xyz;
            try {
               xyz = Float.parseFloat(st.nextToken());
               atom.setX(xyz);
               xyz = Float.parseFloat(st.nextToken());
               atom.setY(xyz);
               xyz = Float.parseFloat(st.nextToken());
               atom.setZ(xyz);
            }
            catch (Exception ex) {
               throw new Exception(
                   "parseGaussianOutputFile: ERROR: Error while parsing atom: " +
                   (i + 1) +
                   " Cannot parse atom's coordinate(s): " + line);
            }

            mol.addAtom(atom);
         }

         // --- Reading number of bonds (if any)
         if ( (line = in.readLine()) == null) {
            System.err.println(
                "parseGaussianOutputFile: Warning: No covalent bonds in fragment");
            return mol;
         }

         int bonds = 0;
         try {
            bonds = Integer.parseInt(line.trim());
         }
         catch (Exception e) {
            throw new Exception(
                "parseGaussianOutputFile: ERROR: Error while parsing number of bonds: " +
                e.getMessage());
         }

         // --- Reading bonds

         for (int i = 0; i < bonds; i++) {
            if ( (line = in.readLine()) == null) {
               throw new Exception(
                   "parseGaussianOutputFile: ERROR: Unxpected End of file while reading bond " +
                   (i + 1));
            }
            StringTokenizer st = new StringTokenizer(line, " ");
            if (st.countTokens() < 2) {
               throw new Exception(
                   "parseGaussianOutputFile: ERROR: Error while parsing bond: " +
                   (i + 1) +
                   " Number of tokens < 2: " + line);
            }

            // --- Getting i & j

            int i_atom, j_atom;
            try {
               i_atom = Integer.parseInt(st.nextToken()) - 1;
               j_atom = Integer.parseInt(st.nextToken()) - 1;

               AtomInterface a_i = mol.getAtomInterface(i_atom);
               AtomInterface a_j = mol.getAtomInterface(j_atom);

               BondInterface bond = mol.getNewBondInstance(a_i, a_j);

               mol.addBond(bond);
            }
            catch (Exception ex) {
               throw new Exception(
                   "parseGaussianOutputFile: ERROR: Error while parsing bond: " +
                   (i + 1) +
                   " Cannot parse atom numbers: " + line);
            }

         }

      }
      catch (Exception ex) {
         throw new Exception("parseGaussianOutputFile: ERROR: " + ex.getMessage());
      }

      return mol;
   }
}
