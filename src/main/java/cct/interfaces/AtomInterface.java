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
 *
 * <p>Title: Computational Chemsitry Tookit</p>
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
public interface AtomInterface
    extends Point3fInterface, Cloneable {

  float COVALENT_TO_GRADIUS_FACTOR = 0.5f;

  int RENDER_SPHERE = 0;
  int RENDER_POINT = 1;
  int RENDER_SMART_POINT = 2;

  String NAME = "Name";
  String SYBYL_TYPE = "Sybyl_Type";
  String CCT_ATOM_TYPE = "CCTAtomType";
  String AMBER_NAME = "AmberName";
  String AMBER_TYPE_INDEX = "AmberTypeIndex";
  String ATOMIC_CHARGE = "AtomicCharge";
  String GR_RADIUS = "GRadius";
  String RGB_COLOR = "RGBColor";
  String AMBIENT_RGB_COLOR = "_AmbientRGBColor";
  String DIFFUSE_RGB_COLOR = "_DiffuseRGBColor";
  String SPECULAR_RGB_COLOR = "_SpecularRGBColor";
  String PICKABILITY = "Pickability";
  String VISIBLE = "Visible";
  String RENDERING_STYLE = "RenderingStyle";

  float getAtomicMass();

  int getAtomicNumber();

  List getBondedToAtoms(); // !!! to delete !!!

  List getBondIndex();

  int getNumberOfBondedAtoms();

  Map getProperties();

  Object getProperty(String key);

  void setAtomicNumber(int anumber);

  BondInterface getBondToAtom(AtomInterface a);

  String getName();

  int getSubstructureNumber();

  boolean isBondedTo(AtomInterface a);

  boolean isSelected();

  boolean isDynamic();

  boolean removeBond(BondInterface bond);

  void setAtomicMass(float mass);

  void setBondedTo(AtomInterface a, boolean set_bond);

  void setBondIndex(BondInterface a);

  void setProperty(Object prop, Object value);

  void setName(String aName);

  void setSubstructureNumber(int n);

  void setSelected(boolean select);

  AtomInterface getNewAtomInstance();

  AtomInterface getNewAtomInstance(AtomInterface a);
  
  String toString();
  
  // -- related to internal coordinates
  
  int getNA();
  int getNB();
  int getNC();
  int[] getNX();
  int[] getIJK();
  void setNA(int na);
  void setNB(int nb);
  void setNC(int nc);
  void setNX(int[] nx);
  void setIJK(int[] nx);
  double getBond();
  double getAngle();
  double getDihedralAngle();
  double[] getZmatrix();
  void setBond(double bond);
  void setAngle(double angle);
  void setDihedralAngle(double dihedral);
  void setZmatrix(double[] z);
}
