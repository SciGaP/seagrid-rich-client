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
package cct.modelling;

import java.util.Map;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.vecmath.Point3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class Atom
    extends Point3f implements AtomInterface {

  protected int element;
  protected float mass;
  protected String name = "";
  protected int monomer_id = 0;
  protected boolean selected = false;
  protected boolean dynamic = true; // Whether it's allowed to be optimized
  protected Map properties = new HashMap();
  protected List bondedToAtoms = new ArrayList();
  protected List bondsIndex = new ArrayList();
  protected int[] ijk = new int[3]; // Used for definition of Z-matrix
  protected double[] zmat = new double[3];

  public Atom() {
    super();
    element = -1;
  }

  public AtomInterface getNewAtom() {
    return new Atom();
  }

  @Override
  public AtomInterface getNewAtomInstance() {
    return new Atom();
  }

  @Override
  public AtomInterface getNewAtomInstance(AtomInterface a) {
    return new Atom(a);
  }

  Atom(Atom a) {
    super();
    element = a.getAtomicNumber();
    name = a.getName();
    mass = a.getAtomicMass();
    monomer_id = a.getSubstructureNumber();
    selected = a.isSelected();
    dynamic = a.isDynamic();
    x = a.getX();
    y = a.getY();
    z = a.getZ();
    properties.putAll(a.getProperties());
    if (a == null) {
      a = new Atom();
    }
  }

  Atom(AtomInterface atom) {
    super();
    element = atom.getAtomicNumber();
    mass = atom.getAtomicMass();
    name = atom.getName();
    monomer_id = atom.getSubstructureNumber();
    selected = atom.isSelected();
    dynamic = atom.isDynamic();
    x = atom.getX();
    y = atom.getY();
    z = atom.getZ();
    properties.putAll(atom.getProperties());

  }

  public Atom(int element, float x, float y, float z) {
    super();
    this.x = x;
    this.y = y;
    this.z = z;
    this.element = element;
    mass = ChemicalElements.getAtomicWeight(element);
  }

  public float distanceTo(Atom a) {
    float x = this.x - a.getX();
    float y = this.y - a.getY();
    float z = this.z - a.getZ();
    return (float) Math.sqrt((x * x + y * y + z * z));
  }

// public String getProperty( String key ) { return (String)properties.get( key); }
  public Map getAtomProperties() {
    return properties;
  }

  public int getNA() {
    return ijk[0];
  }

  public int getNB() {
    return ijk[1];
  }

  public int getNC() {
    return ijk[2];
  }

  public int[] getNX() {
    return ijk;
  }

  public int[] getIJK() {
    return ijk;
  }

  public void setNA(int na) {
    ijk[0] = na;
  }

  public void setNB(int nb) {
    ijk[1] = nb;
  }

  public void setNC(int nc) {
    ijk[2] = nc;
  }

  public void setNX(int[] nx) {
    ijk[0] = nx[0];
    ijk[1] = nx[1];
    ijk[2] = nx[2];
  }

  public void setIJK(int[] nx) {
    ijk[0] = nx[0];
    ijk[1] = nx[1];
    ijk[2] = nx[2];
  }

  public double getBond() {
    return zmat[0];
  }

  public double getAngle() {
    return zmat[1];
  }

  public double getDihedralAngle() {
    return zmat[2];
  }

  public double[] getZmatrix() {
    return zmat;
  }

  public void setBond(double bond) {
    zmat[0] = bond;
  }

  public void setAngle(double angle) {
    zmat[1] = angle;
  }

  public void setDihedralAngle(double dihedral) {
    zmat[2] = dihedral;
  }

  public void setZmatrix(double[] z) {
    zmat[0] = z[0];
    zmat[1] = z[1];
    zmat[2] = z[2];
  }

  @Override
  public float getX() {
    return super.getX();
  }

  @Override
  public float getY() {
    return super.getY();
  }

  @Override
  public float getZ() {
    return super.getZ();
  }

  public float[] getCoordinates() {
    float xyz[] = {
      getX(), getY(), getZ()};
    return xyz;
  }

  public boolean IsSelected() {
    return selected;
  }

  /*
   * @Override public void setXYZ(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
   *
   * @Override public void setXYZ(Point3f xyz) { this.x = xyz.x; this.y = xyz.y; this.z = xyz.z; }
   *
   *
   * @Override public void setX(double x) { this.x = (float)x; }
   *
   * @Override public void setY( double x) { this.y = (float)x; }
   *
   * @Override public void setZ(double x) { this.z = (float)x; }
   */
  @Override
  public float vectorNorm() {
    return vectorNorm();
  }

  public float[] getDirectionTo(Point3f a) {
    float dir[] = {
      0, 0, 0}; //new float[3];
    float dist = distanceTo(a);
    if (dist == 0) {
      return dir;
    }
    dir[0] = (a.getX() - getX()) / dist;
    dir[1] = (a.getY() - getY()) / dist;
    dir[2] = (a.getZ() - getZ()) / dist;
    return dir;
  }

  public void translateAtom(float dir[], float increment) {
    setX(getX() + dir[0] * increment);
    setY(getY() + dir[1] * increment);
    setZ(getZ() + dir[2] * increment);
  }

  public void translateAtom(Point3f dir, float increment) {
    setX(getX() + dir.x * increment);
    setY(getY() + dir.y * increment);
    setZ(getZ() + dir.z * increment);
  }

  @Override
  public Atom clone() {
    return new Atom(this);
  }

  //***********************************************
  @Override
  public boolean isSelected() {
    return selected;
  }

  @Override
  public boolean isDynamic() {
    return dynamic;
  }

  @Override
  public boolean isBondedTo(AtomInterface a) {
      return bondedToAtoms.contains( a );
  }

  @Override
  public void setName(String aName) {
    //if (name != null) {
    //properties.put(AtomProperties.NAME, name);
    //}
    this.name = aName;
  }

  @Override
  public void setSubstructureNumber(int n) {
    monomer_id = n;
  }

  @Override
  public void setSelected(boolean select) {
    selected = select;
  }

  @Override
  public int getSubstructureNumber() {
    return monomer_id;
  }

  public float distanceTo(Point3f a) {
    float x = this.x - a.x;
    float y = this.y - a.y;
    float z = this.z - a.z;
    return (float) Math.sqrt((x * x + y * y + z * z));
  }

  public float distanceTo(AtomInterface a) {
    float x = this.x - a.getX();
    float y = this.y - a.getY();
    float z = this.z - a.getZ();
    return (float) Math.sqrt((x * x + y * y + z * z));
  }

  @Override
  public List getBondedToAtoms() {
    return bondedToAtoms;
  }

  @Override
  public int getNumberOfBondedAtoms() {
    return bondedToAtoms.size();
  }

  /**
   * Used internally !!!
   *
   * @param bond BondInterface
   * @return BondInterface
   */
  @Override
  public boolean removeBond(BondInterface bond) {
    if (!bondsIndex.contains(bond)) {
      return false;
    }
    return bondsIndex.remove(bond);
  }

  @Override
  public List getBondIndex() {
    return bondsIndex;
  }

  @Override
  public BondInterface getBondToAtom(AtomInterface a) {
    if (!bondedToAtoms.contains(a)) {
      return null; // not bonded to it
    }
    try {
      return (BondInterface) bondsIndex.get(bondedToAtoms.indexOf(a));
    } catch (IndexOutOfBoundsException e) {
      System.err.println("getBondtoAtom: IndexOutOfBoundsException: " + e.getMessage());
      return null;
    }
  }

  public void setBondedTo(AtomInterface a) {
    bondedToAtoms.add(a);
  }

  // !!! Function to delete !!!
  @Override
  public void setBondedTo(AtomInterface a, boolean set_bond) {
    if (set_bond) {
      if (!bondedToAtoms.contains(a)) {
        bondedToAtoms.add(a);
      }
    } else {
      if (!bondedToAtoms.contains(a)) {
        return;
      }
      int index = bondedToAtoms.indexOf(a);
      bondedToAtoms.remove(index);
      bondsIndex.remove(index);
    }
  }

  public void setUnbondedTo(AtomInterface a) {
    if (!bondedToAtoms.contains(a)) {
      return;
    }
    int index = bondedToAtoms.indexOf(a);
    bondedToAtoms.remove(index);
    bondsIndex.remove(index);
  }

  @Override
  public void setBondIndex(BondInterface a) {
    bondsIndex.add(a);
  }

  @Override
  public int getAtomicNumber() {
    return element;
  }

  @Override
  public void setAtomicNumber(int anumber) {
    element = anumber;
    mass = ChemicalElements.getAtomicWeight(element);
  }

  @Override
  public String getName() {
    //return (String) properties.get(AtomProperties.NAME);
    return name;
  }

  @Override
  public void setAtomicMass(float mass) {
    this.mass = mass;
  }

  @Override
  public void setProperty(Object prop, Object value) {
    if (value == null) {
      properties.remove(prop);
      return;
    }
    properties.put(prop, value);
  }

  @Override
  public Object getProperty(String key) {
    return properties.get(key);
  }

  @Override
  public Map getProperties() {
    return properties;
  }

  @Override
  public float getAtomicMass() {
    return mass;
  }

  public String toString() {
    return this.getClass().getCanonicalName() + ": Element: " + String.valueOf(element)
        + " X: " + String.valueOf(this.getX()) + " Y: " + String.valueOf(this.getY()) + " Z: " + String.valueOf(this.getZ());
  }
}
