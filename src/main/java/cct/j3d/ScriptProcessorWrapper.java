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

package cct.j3d;

import java.util.Arrays;

import cct.cprocessor.GenericAtom;
import cct.cprocessor.GenericBond;
import cct.cprocessor.MolProcessorInterface;
import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.Bond;
import cct.modelling.Molecule;
import cct.modelling.Atom;

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
public class ScriptProcessorWrapper
    implements MolProcessorInterface {

   Java3dUniverse editor = null;

   public ScriptProcessorWrapper(Java3dUniverse j3d) {
      editor = j3d;
   }

   public static void main(String[] args) {
      ScriptProcessorWrapper scriptprocessorwrapper = new
          ScriptProcessorWrapper(null);
   }

   @Override
  public void addAtom(Object atom) throws Exception {
      if (atom instanceof AtomInterface) {
         editor.addAtom( (AtomInterface) atom);
      }
      else if (atom instanceof AtomNode) {
         editor.addAtom( (AtomNode) atom);
      }
      else if (atom instanceof GenericAtom) {
         MoleculeInterface mol = editor.getMoleculeInterface();
         if (mol == null) {
            mol = new Molecule();
            editor.addMolecule(mol);
         }
         GenericAtom a = (GenericAtom) atom;
         AtomInterface at = new Atom(a.element, a.x, a.y, a.z);
         at.setName(a.name);
         at.setSubstructureNumber(a.substructure);
         editor.addAtom(at);
      }
      else {
         throw new Exception("addAtom: unknown class: " +
                             atom.getClass().getCanonicalName());
      }

      //editor.centerSceneOnScreen(); // !!! Remove this !!!
   }

   @Override
  public void addBond(Object bond) throws Exception {
      if (bond instanceof BondInterface) {
         editor.addBond( (BondInterface) bond);
      }
      else if (bond instanceof GenericBond) {
         GenericBond b = (GenericBond) bond;
         MoleculeInterface mol = editor.getMoleculeInterface();
         AtomInterface a1 = mol.getAtomInterface(b.i);
         AtomInterface a2 = mol.getAtomInterface(b.j);
         BondInterface bnd = new Bond(a1, a2);
         editor.addBond(bnd);
      }
      else {
         throw new Exception("addBond: unknown class: " +
                             bond.getClass().getCanonicalName());
      }
   }

   @Override
  public void eraseAtoms(Object atoms) throws Exception {
      if (atoms instanceof int[]) {
         int[] ats = (int[]) atoms;
         Arrays.sort(ats);
         //MoleculeInterface mol = editor.getMoleculeInterface();
         for (int i = ats.length - 1; i >= 0; i--) {
            editor.deleteAtom(i);
         }
      }
      else {
         throw new Exception("eraseAtoms: unknown class: " +
                             atoms.getClass().getCanonicalName());
      }
   }

   @Override
  public void centerMolecule(Object center) throws Exception {
      editor.centerSceneOnScreen();
   }

   @Override
  public void setRenderingStyle(Object style) throws Exception {
      String renStyle = (String) style;
      editor.setGlobalRenderingStyle(renStyle);
   }
}
