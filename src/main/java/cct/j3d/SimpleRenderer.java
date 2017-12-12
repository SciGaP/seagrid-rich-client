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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import org.scijava.java3d.Canvas3D;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cct.interfaces.GraphicsRendererInterface;
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
public class SimpleRenderer
    extends JFrame implements GraphicsRendererInterface {

   Canvas3D canvas3D;
   Java3dUniverse java3dUniverse;
   JPanel contentPane;
   BorderLayout borderLayout1 = new BorderLayout();

   public SimpleRenderer() {
      try {
         setDefaultCloseOperation(HIDE_ON_CLOSE);
         java3dUniverse = new Java3dUniverse();
         //setLayout(new BorderLayout());
         canvas3D = java3dUniverse.getCanvas3D();
         contentPane = (JPanel) getContentPane();
         contentPane.setLayout(borderLayout1);
         setSize(new Dimension(600, 450));
         setTitle("Molecular Renderer");
         contentPane.add(canvas3D, BorderLayout.CENTER);
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }

   }

   @Override
  public MoleculeInterface getMoleculeInterface() {
      return java3dUniverse.getMoleculeInterface();
   }

   @Override
  public void renderMolecule(MoleculeInterface m) {
      java3dUniverse.addMolecule(m);
      setVisible(true);
   }

   @Override
  public void setElementForSelectedAtoms(int new_element) {
      java3dUniverse.setElementForSelectedAtoms(new_element);
   }

   @Override
  public void setColorForSelectedAtoms(Color new_color) {
      java3dUniverse.setColorForSelectedAtoms(new_color);
   }

   @Override
  public void setLabelForSelectedAtoms(String new_label) {
      java3dUniverse.setLabelForSelectedAtoms(new_label);
   }

   @Override
  public void setRadiusForSelectedAtoms(float radius) {
      java3dUniverse.setRadiusForSelectedAtoms(radius);
   }

   @Override
  public void setAtomTypeForSelectedAtoms(String new_atomType) {
      java3dUniverse.setAtomTypeForSelectedAtoms(new_atomType);
   }

}
