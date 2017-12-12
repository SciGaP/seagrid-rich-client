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

package cct.tools;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.Molecule;

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
public class Crystal {
   public float a[] = {
       0, 0, 5.2065f};
   public float b[] = {
       0, 5.7088f, 0};
   public float c[] = {
       4.9440f, 2.8544f, 0};
   public float d[] = {
       -4.9440f, 2.8544f, 0};

   public Crystal() {
   }

   public static void main(String[] args) {
      Crystal crystal = new Crystal();
      MoleculeInterface m = new Molecule();
      try {
         XMolXYZ xMolXYZ = new XMolXYZ();
         m = xMolXYZ.parseXMolXYZ(args[0], m);
      }
      catch (Exception ex) {
         //JOptionPane.showMessageDialog(null,
         //                              ex.getMessage(),
         //                              "Error",
         //                              JOptionPane.ERROR_MESSAGE);
         System.err.println(ex.getMessage());
         System.exit(1);
      }

      MoleculeInterface refMol = m.getInstance();
      refMol.appendMolecule(m);

      for (int i = -2; i <= 2; i++) {

         for (int k = -1; k <= 1; k++) {
            if (i == 0 && k == 0) {
               continue;
            }
            MoleculeInterface newMol = m.getInstance();
            newMol.appendMolecule(refMol);

            for (int j = 0; j < newMol.getNumberOfAtoms(); j++) {
               AtomInterface atom = newMol.getAtomInterface(j);
               atom.setX(atom.getX() + i * crystal.a[0] +
                         k * crystal.b[0]);
               atom.setY(atom.getY() + i * crystal.a[1] +
                         k * crystal.b[1]);
               atom.setZ(atom.getZ() + i * crystal.a[2] +
                         k * crystal.b[2]);
            }
            m.appendMolecule(newMol);

         }

         for (int k = -1; k <= 1; k++) {
            if (k == 0) {
               continue;
            }
            MoleculeInterface newMol = m.getInstance();
            newMol.appendMolecule(refMol);

            for (int j = 0; j < newMol.getNumberOfAtoms(); j++) {
               AtomInterface atom = newMol.getAtomInterface(j);
               atom.setX(atom.getX() + i * crystal.a[0] +
                         k * crystal.c[0]);
               atom.setY(atom.getY() + i * crystal.a[1] +
                         k * crystal.c[1]);
               atom.setZ(atom.getZ() + i * crystal.a[2] +
                         k * crystal.c[2]);
            }
            m.appendMolecule(newMol);

         }

         for (int k = -1; k <= 1; k++) {
            if (k == 0) {
               continue;
            }
            MoleculeInterface newMol = m.getInstance();
            newMol.appendMolecule(refMol);

            for (int j = 0; j < newMol.getNumberOfAtoms(); j++) {
               AtomInterface atom = newMol.getAtomInterface(j);
               atom.setX(atom.getX() + i * crystal.a[0] +
                         k * crystal.d[0]);
               atom.setY(atom.getY() + i * crystal.a[1] +
                         k * crystal.d[1]);
               atom.setZ(atom.getZ() + i * crystal.a[2] +
                         k * crystal.d[2]);
            }
            m.appendMolecule(newMol);

         }

         /*
                   for (int k = -1; k <= 1; k++) {

            for (int k2 = -1; k2 <= 1; k2++) {
               for (int k3 = -1; k3 <= 1; k3++) {
                  if (i == 0 && k == 0 && k2 == 0 && k3 == 0) {
                     continue;
                  }
                  MoleculeInterface newMol = m.getInstance();
                  newMol.appendMolecule(refMol);

                  for (int j = 0; j < newMol.getNumberOfAtoms(); j++) {
                     AtomInterface refAtom = m.getAtomInterface(j);
                     AtomInterface atom = newMol.getAtomInterface(j);
                     atom.setX(atom.getX() + (float) i * crystal.\u0441[0] +
                               (float) k * crystal.\u04301[0] +
                               (float) k2 * crystal.\u04302[0] +
                               (float) k3 * crystal.\u04303[0]);
                     atom.setY(atom.getY() + (float) i * crystal.\u0441[1] +
                               (float) k * crystal.\u04301[1] +
                               (float) k2 * crystal.\u04302[1] +
                               (float) k3 * crystal.\u04303[1]);
                     atom.setZ(atom.getZ() + (float) i * crystal.\u0441[2] +
                               (float) k * crystal.\u04301[2] +
                               (float) k2 * crystal.\u04302[2] +
                               (float) k3 * crystal.\u04303[2]);
                  }
                  m.appendMolecule(newMol);
               }
            }
                   }
          */

      }

      Molecule.guessCovalentBonds(m);
      try {
         CCTParser.saveCCTFile(m, args[1]);
      }
      catch (Exception ex) {
         //JOptionPane.showMessageDialog(null,
         //                              ex.getMessage(),
         //                              "Error",
         //                              JOptionPane.ERROR_MESSAGE);
         System.err.println(ex.getMessage());
         System.exit(1);
      }

   }
}
