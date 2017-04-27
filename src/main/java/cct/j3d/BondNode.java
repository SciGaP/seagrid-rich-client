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

import org.scijava.java3d.Appearance;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.ColoringAttributes;
import org.scijava.java3d.GeometryArray;
import org.scijava.java3d.Group;
import org.scijava.java3d.LineArray;
import org.scijava.java3d.Material;
import org.scijava.java3d.Node;
import org.scijava.java3d.PolygonAttributes;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.vecmath.AxisAngle4f;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Point3f;
import org.scijava.vecmath.Vector3d;
import org.scijava.vecmath.Vector3f;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.modelling.ChemicalElements;

import org.scijava.java3d.utils.geometry.Cylinder;
import org.scijava.java3d.utils.geometry.Primitive;
import java.util.HashMap;
import java.util.Map;
import org.scijava.java3d.*;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BondNode
    extends BranchGroup implements Cloneable {
  
  enum BOND_TYPE { LINE, CYLINDER }
  
  protected boolean delete = false;
  //int bondColoring = MONOCOLOR;
  int bondColoring = BondInterface.BICOLOR;
  int renderingStyle;
  int colorScheme;
  float cylinderRadius = BondInterface.DEFAULT_BONDS_AND_STICKS_RADIUS;

  BranchGroup rootBranch = null;
  Color3f defaultMonoColor = new Color3f(1.0f, 0.5f, 0.7f);
  Point3f coord = new Point3f();
  Point3f[] coordinates = null;
  LineArray lineBond;
  Shape3D line_shape = null;
  Shape3D shape;
  private float vX, vY, vZ;
  private float angle;
  private AxisAngle4f rot = new AxisAngle4f();
  private BondInterface bond = null;
  
  boolean useSwitch = true;
  private Switch showSwitch = new Switch();
  private Map<Integer, BranchGroup> switchShapes = new HashMap<Integer, BranchGroup>();
  private Map<BranchGroup, Integer> switchIndex = new HashMap<BranchGroup, Integer>();

  BondNode() {
    super();
    //setName("Bond"); // --- >1.4 java3d feature
  }

  BondNode(BondInterface b) {
    this(b, BondInterface.BICOLOR);
  }

  BondNode(BondInterface b, int bondFormat) {
    super();
    //setName("Bond "); // --- >1.4 java3d feature

    if (bondFormat == BondInterface.CYLINDER_BICOLOR) {
      bondColoring = BondInterface.BICOLOR;
      renderingStyle = BondInterface.CYLINDER_BICOLOR;
      colorScheme = BondInterface.BICOLOR;
    }
    else if (bondFormat == BondInterface.CYLINDER_MONOCOLOR) {
      bondColoring = BondInterface.MONOCOLOR;
      renderingStyle = BondInterface.CYLINDER_MONOCOLOR;
      colorScheme = BondInterface.MONOCOLOR;
    }
    else if (bondFormat == BondInterface.LINE_MONOCOLOR) {
      bondColoring = BondInterface.MONOCOLOR;
      renderingStyle = BondInterface.LINE_MONOCOLOR;
      colorScheme = BondInterface.MONOCOLOR;
    }
    else if (bondFormat == BondInterface.LINE_BICOLOR) {
      bondColoring = BondInterface.BICOLOR;
      renderingStyle = BondInterface.LINE_BICOLOR;
      colorScheme = BondInterface.BICOLOR;
    }

    b.setProperty(BondInterface.RENDERING_STYLE, new Integer(bondFormat));

    bond = b;
    setCapability(Group.ALLOW_CHILDREN_READ);
    setCapability(Group.ALLOW_CHILDREN_WRITE);
    setCapability(Group.ALLOW_CHILDREN_EXTEND);
    setCapability(BranchGroup.ALLOW_DETACH);
    setPickable(false);

    rootBranch = getRootBranch();

    //gAtom a1 = (gAtom) b.getI();
    //gAtom a2 = (gAtom) b.getJ();
    AtomInterface a1 = b.getIAtomInterface();
    AtomInterface a2 = b.getJAtomInterface();

    // Adjust bond radius. Should be <= then the smalest atomic radius

    Float R = (Float) b.getProperty(BondInterface.CYLINDER_RADIUS);
    if (R != null) {
      cylinderRadius = R;
    }

    float r1 = ChemicalElements.getCovalentRadius(a1.getAtomicNumber());
    r1 *= AtomInterface.COVALENT_TO_GRADIUS_FACTOR;
    R = (Float) a1.getProperty(AtomInterface.GR_RADIUS);
    if (R != null) {
      r1 = R;
    }

    cylinderRadius = Math.min(r1, cylinderRadius);

    r1 = ChemicalElements.getCovalentRadius(a2.getAtomicNumber());
    r1 *= AtomInterface.COVALENT_TO_GRADIUS_FACTOR;
    R = (Float) a1.getProperty(AtomInterface.GR_RADIUS);
    if (R != null) {
      r1 = R;
    }

    cylinderRadius = Math.min(r1, cylinderRadius);

    b.setProperty(BondInterface.CYLINDER_RADIUS, new Float(cylinderRadius));

    if (bondFormat == BondInterface.CYLINDER_MONOCOLOR) {
      renderingStyle = BondInterface.CYLINDER_MONOCOLOR;
      colorScheme = BondInterface.MONOCOLOR;

      TransformGroup atg = createMonoCylinder(bond);
      rootBranch.addChild(atg);
      addChild(rootBranch);
    }
    else if (bondFormat == BondInterface.LINE_MONOCOLOR) {
      renderingStyle = BondInterface.LINE_MONOCOLOR;
      colorScheme = BondInterface.MONOCOLOR;

      // Setup bonds

      shape = getMonocolorLine(bond);
      rootBranch.addChild(shape);
      addChild(rootBranch);

      //addChild(shape);
    }

    else if (bondFormat == BondInterface.LINE_BICOLOR) {
      renderingStyle = BondInterface.LINE_BICOLOR;
      colorScheme = BondInterface.BICOLOR;

      // Setup bonds

      shape = getBicolorLine(bond);
      rootBranch.addChild(shape);
      addChild(rootBranch);
    }

    // --- Bicolor scheme
    else if (bondFormat == BondInterface.CYLINDER_BICOLOR) {
      renderingStyle = BondInterface.CYLINDER_BICOLOR;
      colorScheme = BondInterface.BICOLOR;

      getOrientation(a1, a2);

      // --- First cylinder

      TransformGroup atg = getFirstCylinder(bond);
      rootBranch.addChild(atg);

      // --- Second cylinder

      atg = getSecondCylinder(bond);
      rootBranch.addChild(atg);

      addChild(rootBranch);
    }

    setNodeName("Bond_" + ChemicalElements.getElementSymbol(a1.getAtomicNumber()) + "_" +
                ChemicalElements.getElementSymbol(a2.getAtomicNumber()));
  }

  public BondInterface getBond() {
    return bond;
  }

  public void setNodeName(String name) {
    if (rootBranch.numChildren() < 1) {
      return;
    }
    for (int i = 0; i < rootBranch.numChildren(); i++) {
      Node child = rootBranch.getChild(i);
      UserData.setUserData(child, UserData.NODE_NAME, name);
    }
  }

  Shape3D getMonocolorLine(BondInterface b) {
    AtomInterface a1 = b.getIAtomInterface();
    AtomInterface a2 = b.getJAtomInterface();

    lineBond = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

    lineBond.setCapability(GeometryArray.ALLOW_COLOR_READ);
    lineBond.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
    lineBond.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    lineBond.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
    lineBond.setCapability(GeometryArray.ALLOW_COUNT_READ);
    lineBond.setCapability(GeometryArray.ALLOW_COUNT_WRITE);
    lineBond.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
    lineBond.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_WRITE);

    coordinates = new Point3f[2];
    coordinates[0] = new Point3f(a1.getX(), a1.getY(), a1.getZ());
    coordinates[1] = new Point3f(a2.getX(), a2.getY(), a2.getZ());
    lineBond.setCoordinates(0, coordinates);
    lineBond.setColor(0, defaultMonoColor);
    lineBond.setColor(1, defaultMonoColor);

    line_shape = new Shape3D();
    line_shape.setGeometry(lineBond);
    line_shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
    line_shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
    line_shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    line_shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    line_shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    line_shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

    int rgb_color[] = {
        defaultMonoColor.get().getRed(), defaultMonoColor.get().getGreen(), defaultMonoColor.get().getBlue()};
    b.setProperty(BondInterface.RGB_COLOR, rgb_color);

    return line_shape;
  }

  Shape3D getBicolorLine(BondInterface b) {
    AtomInterface a1 = b.getIAtomInterface();
    AtomInterface a2 = b.getJAtomInterface();

    lineBond = new LineArray(4, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

    lineBond.setCapability(GeometryArray.ALLOW_COLOR_READ);
    lineBond.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
    lineBond.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    lineBond.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
    lineBond.setCapability(GeometryArray.ALLOW_COUNT_READ);
    lineBond.setCapability(GeometryArray.ALLOW_COUNT_WRITE);
    lineBond.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
    lineBond.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_WRITE);

    coordinates = new Point3f[4];
    coordinates[0] = new Point3f(a1.getX(), a1.getY(), a1.getZ());
    coordinates[1] = new Point3f(a1.getX() +
                                 (a2.getX() - a1.getX()) / 2.0f,
                                 a1.getY() +
                                 (a2.getY() - a1.getY()) / 2.0f,
                                 a1.getZ() +
                                 (a2.getZ() - a1.getZ()) / 2.0f);
    coordinates[2] = new Point3f(a1.getX() +
                                 (a2.getX() - a1.getX()) / 2.0f,
                                 a1.getY() +
                                 (a2.getY() - a1.getY()) / 2.0f,
                                 a1.getZ() +
                                 (a2.getZ() - a1.getZ()) / 2.0f
        );

    coordinates[3] = new Point3f(a2.getX(), a2.getY(), a2.getZ());
    lineBond.setCoordinates(0, coordinates);

    Color3f color = this.getColor3f(a1);
    lineBond.setColor(0, color);
    lineBond.setColor(1, color);
    color = this.getColor3f(a2);
    lineBond.setColor(2, color);
    lineBond.setColor(3, color);

    Shape3D line_shape = new Shape3D();
    line_shape.setGeometry(lineBond);
    return line_shape;
  }

  TransformGroup getSecondCylinder(BondInterface b) {
    // --- Second cylinder
    AtomInterface a1 = b.getIAtomInterface();
    AtomInterface a2 = b.getJAtomInterface();

    Transform3D move = new Transform3D();
    move.setScale(new Vector3d(cylinderRadius, b.bondLength() / 2,
                               cylinderRadius));
    move.setRotation(rot);
    move.setTranslation(new Vector3f( (a1.getX() +
                                               0.75f * b.bondLength() * vX),
                                     (a1.getY() +
                                              0.75f * b.bondLength() * vY),
                                     (a1.getZ() +
                                              0.75f * b.bondLength() * vZ)));

    TransformGroup atg = new TransformGroup(move);
    atg.setCapability(Group.ALLOW_CHILDREN_READ);
    atg.setCapability(Group.ALLOW_CHILDREN_WRITE);
    atg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    atg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    //atg.setName("Bond"); // --- >1.4 java3d feature

    Appearance aAppear = getAppearance(a2);

    // Setup bonds

    //Cylinder cyl = new Cylinder(0.15f, b.bondLength(), aAppear);
    Cylinder cyl = createCylinder(1, 1, aAppear);

    atg.addChild(cyl);
    return atg;
  }

  TransformGroup getFirstCylinder(BondInterface b) {
    // --- First cylinder
    AtomInterface a1 = b.getIAtomInterface();

    Transform3D move = new Transform3D();
    move.setScale(new Vector3d(cylinderRadius, 0.5 * b.bondLength(),
                               cylinderRadius));
    move.setRotation(rot);
    move.setTranslation(new Vector3f( (a1.getX() +
                                               0.25f * b.bondLength() * vX),
                                     (a1.getY() +
                                              0.25f * b.bondLength() * vY),
                                     (a1.getZ() +
                                              0.25f * b.bondLength() * vZ)));

    TransformGroup atg = new TransformGroup(move);
    atg.setCapability(Group.ALLOW_CHILDREN_READ);
    atg.setCapability(Group.ALLOW_CHILDREN_WRITE);
    atg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    atg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    //atg.setName("Bond"); // --- >1.4 java3d feature

    Appearance aAppear = getAppearance(a1);

    // Setup bonds

    //Cylinder cyl = new Cylinder(0.15f, b.bondLength(), aAppear);
    Cylinder cyl = createCylinder(1, 1, aAppear);

    atg.addChild(cyl);
    return atg;
  }

  /**
   * Creates TransformGroup with a cylinder for monocolored bond
   * @param b BondInterface
   * @return TransformGroup
   */
  TransformGroup createMonoCylinder(BondInterface b) {
    AtomInterface a1 = b.getIAtomInterface();
    AtomInterface a2 = b.getJAtomInterface();

    getOrientation(a1, a2);

    Transform3D move = new Transform3D();
    move.setScale(new Vector3d(cylinderRadius, b.bondLength(),
                               cylinderRadius));
    move.setRotation(rot);
    move.setTranslation(new Vector3f( (a1.getX() +
                                               0.5f * b.bondLength() * vX),
                                     (a1.getY() +
                                              0.5f * b.bondLength() * vY),
                                     (a1.getZ() +
                                              0.5f * b.bondLength() * vZ)));

    TransformGroup atg = new TransformGroup(move);
    atg.setCapability(Group.ALLOW_CHILDREN_READ);
    atg.setCapability(Group.ALLOW_CHILDREN_WRITE);
    atg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    atg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    //atg.setName("Bond"); // --- >1.4 java3d feature

    ColoringAttributes ca = new ColoringAttributes();
    ca.setColor(defaultMonoColor);

    int rgb_color[] = {
        defaultMonoColor.get().getRed(), defaultMonoColor.get().getGreen(), defaultMonoColor.get().getBlue()};
    b.setProperty(BondInterface.RGB_COLOR, rgb_color);

    Appearance aAppear = new Appearance();
    aAppear.setColoringAttributes(ca);

    Material material = ChemicalElementsColors.getElementMaterial(0);
    aAppear.setMaterial(material);

    PolygonAttributes polyAppear = new PolygonAttributes();
    polyAppear.setCullFace(PolygonAttributes.CULL_BACK);

    aAppear.setPolygonAttributes(polyAppear);

    // Setup bonds

    Cylinder cyl = createCylinder(1, 1, aAppear);

    atg.addChild(cyl);
    return atg;
  }

  LineArray createLine() {
    LineArray line = new LineArray(2,
                                   GeometryArray.COORDINATES | GeometryArray.COLOR_3);
    line.setCapability(GeometryArray.ALLOW_COLOR_READ);
    line.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
    line.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    line.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
    return line;
  }

  Cylinder createCylinder(float radius, float height, Appearance ap) {
    Cylinder cyl = new Cylinder(radius, height, ap);
    cyl.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
    cyl.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
    Shape3D shape = cyl.getShape(Cylinder.BODY);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    shape = cyl.getShape(Cylinder.BOTTOM);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    shape = cyl.getShape(Cylinder.TOP);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

    return cyl;
  }

  Appearance getAppearance(AtomInterface atom) {
    ColoringAttributes ca = new ColoringAttributes();
    ca.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
    ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

    Color3f a1_color = null;
    Object obj = atom.getProperty(AtomInterface.RGB_COLOR);
    if (obj != null && obj instanceof Integer[]) {
      Integer[] rgbColor = (Integer[]) obj;
      a1_color = new Color3f(rgbColor[0].floatValue() / 255.0f,
                             rgbColor[1].floatValue() / 255.0f,
                             rgbColor[2].floatValue() / 255.0f);
    }
    else {
      a1_color = ChemicalElementsColors.getElementColor(atom.
          getAtomicNumber());
    }

    ca.setColor(a1_color);
    Appearance aAppear = new Appearance();
    aAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
    aAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    aAppear.setCapability(Appearance.ALLOW_MATERIAL_READ);
    aAppear.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    aAppear.setColoringAttributes(ca);

    Material material = ChemicalElementsColors.createMaterial(a1_color);
    aAppear.setMaterial(material);

    PolygonAttributes polyAppear = new PolygonAttributes();
    polyAppear.setCullFace(PolygonAttributes.CULL_BACK);

    aAppear.setPolygonAttributes(polyAppear);
    return aAppear;
  }

  Color3f getColor3f(AtomInterface atom) {
    Color3f a1_color = null;
    Object obj = atom.getProperty(AtomInterface.RGB_COLOR);
    if (obj != null && obj instanceof Integer[]) {
      Integer[] rgbColor = (Integer[]) obj;
      a1_color = new Color3f(rgbColor[0].floatValue() / 255.0f,
                             rgbColor[1].floatValue() / 255.0f,
                             rgbColor[2].floatValue() / 255.0f);
    }
    else {
      a1_color = ChemicalElementsColors.getElementColor(atom.
          getAtomicNumber());
    }

    return a1_color;
  }

  private void getOrientation(AtomInterface a1, AtomInterface a2) {

    // Get orientation
    vX = a2.getX() - a1.getX();
    vY = a2.getY() - a1.getY();
    vZ = a2.getZ() - a1.getZ();
    float norm = (float) Math.sqrt(vX * vX + vY * vY + vZ * vZ);
    vX /= norm;
    vY /= norm;
    vZ /= norm;

    angle = (float) Math.acos(vY);
    rot.set(vZ, 0.0f, -vX, angle);
  }

  private void getOrientation(float[] a1, float[] a2) {

    // Get orientation
    vX = a2[0] - a1[0];
    vY = a2[1] - a1[1];
    vZ = a2[2] - a1[2];
    float norm = (float) Math.sqrt(vX * vX + vY * vY + vZ * vZ);
    vX /= norm;
    vY /= norm;
    vZ /= norm;

    angle = (float) Math.acos(vY);
    rot.set(vZ, 0.0f, -vX, angle);
  }

  public boolean isToDelete() {
    return delete;
  }

  public void markForDeletion(boolean flag) {
    delete = flag;
  }

  public void updateBond() {
    AtomInterface a1 = bond.getIAtomInterface();
    AtomInterface a2 = bond.getJAtomInterface();

    float[] v1 = {
        a1.getX(), a1.getY(), a1.getZ()};
    float[] v2 = {
        a2.getX(), a2.getY(), a2.getZ()};
    updateTransform3D(v1, v2);
  }

  public void updateBond(float[] a1, float[] a2) {
    updateTransform3D(a1, a2);
  }

  Cylinder getCylinder(int i) {
    if (i < 0) {
      return null;
    }
    TransformGroup atg = (TransformGroup) rootBranch.getChild(i);
    Object obj = atg.getChild(0);
    if (obj == null) {
      return null;
    }
    if (! (obj instanceof Cylinder)) {
      return null;
    }
    return (Cylinder) obj;
  }

  /*
      void updateBondLength(float height) {

     if (numChildren() < 1) {
        return;
     }

     if (renderingStyle == BondInterface.RENDER_CYLINDER &&
         bondColoring == BondInterface.MONOCOLOR) {

        TransformGroup atg = (TransformGroup) rootBranch.getChild(0); // !!1 Only 1 child now
        Cylinder cyl = (Cylinder) atg.getChild(0); // !!! Only 1 child now
        float radius = cyl.getRadius();

        atg.removeChild(cyl);

        Appearance aAppear = cyl.getAppearance();
        cyl = new Cylinder(radius, height, aAppear);
        cyl.setCapability(Cylinder.ENABLE_APPEARANCE_MODIFY);
        cyl.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);

        atg.addChild(cyl);

     }

      }
   */
  void updateBondColor() {
    if (bondColoring == BondInterface.MONOCOLOR) {
      return;
    }
    AtomInterface a1 = bond.getIAtomInterface();
    AtomInterface a2 = bond.getJAtomInterface();

    if (renderingStyle == BondInterface.CYLINDER_BICOLOR || renderingStyle == BondInterface.CYLINDER_MONOCOLOR) {

      Cylinder cylinder = getCylinder(0);
      Appearance atomAppear = getAppearance(a1);
      ColoringAttributes ca = atomAppear.getColoringAttributes();
      Color3f color = new Color3f();
      ca.getColor(color);
      Material material = atomAppear.getMaterial();

      Appearance cylAppear = cylinder.getAppearance();
      ColoringAttributes cylCA = cylAppear.getColoringAttributes();
      cylCA.setColor(color);
      cylAppear.setMaterial(material);

      cylinder = getCylinder(1);
      atomAppear = this.getAppearance(a2);
      ca = atomAppear.getColoringAttributes();
      color = new Color3f();
      ca.getColor(color);
      material = atomAppear.getMaterial();

      cylAppear = cylinder.getAppearance();
      cylCA = cylAppear.getColoringAttributes();
      cylCA.setColor(color);
      cylAppear.setMaterial(material);
    }
    else if (renderingStyle == BondInterface.LINE_BICOLOR || renderingStyle == BondInterface.LINE_MONOCOLOR) {

      Color3f color = this.getColor3f(a1);
      lineBond.setColor(0, color);
      lineBond.setColor(1, color);
      color = this.getColor3f(a2);
      lineBond.setColor(2, color);
      lineBond.setColor(3, color);

    }
  }

  void updateBondRadius() {
    Object obj;

    obj = bond.getProperty(BondInterface.VISIBLE);
    if (obj != null && obj instanceof Boolean && ! ( (Boolean) obj).booleanValue()) {
      this.removeAllChildren();
      return;
    }

    obj = bond.getProperty(BondInterface.RENDERING_STYLE);
    int rendering_style = renderingStyle;
    if (obj != null && obj instanceof Integer) {
      rendering_style = (Integer) obj;
    }

    obj = bond.getProperty(BondInterface.COLOR_SCHEME);
    if (obj != null && obj instanceof Integer) {
      colorScheme = (Integer) obj;
    }

    if ( (rendering_style == BondInterface.CYLINDER_BICOLOR || rendering_style == BondInterface.CYLINDER_MONOCOLOR) &&
        this.numChildren() > 0 &&
        rootBranch.getChild(0) instanceof TransformGroup) {

      renderingStyle = rendering_style;

      obj = bond.getProperty(BondInterface.CYLINDER_RADIUS);
      if (obj != null) {
        Float R = (Float) obj;
        cylinderRadius = R;
      }

      if (bondColoring == BondInterface.MONOCOLOR) {
        Transform3D move = new Transform3D();
        TransformGroup atg = (TransformGroup) rootBranch.getChild(0);
        atg.getTransform(move);
        move.setScale(new Vector3d(cylinderRadius, bond.bondLength(), cylinderRadius));
        atg.setTransform(move);
      }
      else if (bondColoring == BondInterface.BICOLOR) {
        Transform3D move = new Transform3D();
        TransformGroup atg = (TransformGroup) rootBranch.getChild(0);
        atg.getTransform(move);
        move.setScale(new Vector3d(cylinderRadius, bond.bondLength() / 2, cylinderRadius));
        atg.setTransform(move);
        move = new Transform3D();
        atg = (TransformGroup) rootBranch.getChild(1);
        atg.getTransform(move);
        move.setScale(new Vector3d(cylinderRadius, bond.bondLength() / 2, cylinderRadius));
        atg.setTransform(move);
      }
    }

    else if (rendering_style == BondInterface.CYLINDER_BICOLOR || rendering_style == BondInterface.CYLINDER_MONOCOLOR) { //&&
      //! (rootBranch.getChild(0) instanceof TransformGroup)) {
      //rendering_style != renderingStyle) {
      renderingStyle = rendering_style;

      obj = bond.getProperty(BondInterface.CYLINDER_RADIUS);
      if (obj != null) {
        Float R = (Float) obj;
        cylinderRadius = R;
      }

      if (colorScheme == BondInterface.BICOLOR) {
        removeAllChildren();
        AtomInterface a1 = bond.getIAtomInterface();
        AtomInterface a2 = bond.getJAtomInterface();

        getOrientation(a1, a2);

        rootBranch = null;
        rootBranch = this.getRootBranch();

        // --- First cylinder

        TransformGroup atg = getFirstCylinder(bond);
        rootBranch.addChild(atg);

        // --- Second cylinder

        atg = getSecondCylinder(bond);
        rootBranch.addChild(atg);

        addChild(rootBranch);
      }
      else if (colorScheme == BondInterface.MONOCOLOR) {
        TransformGroup atg = createMonoCylinder(bond);
        removeAllChildren();
        rootBranch = this.getRootBranch();
        rootBranch.addChild(atg);
        addChild(rootBranch);
      }
    }

    /*
           else if (rendering_style == BondInterface.RENDER_LINE &&
             rendering_style == renderingStyle) {

       if (colorScheme == BondInterface.BICOLOR) {

       }
       else if (colorScheme == BondInterface.MONOCOLOR) {

       }

           }
     */

    // Not elegant but simple
    else if (rendering_style == BondInterface.LINE_BICOLOR || rendering_style == BondInterface.LINE_MONOCOLOR) { //&&
      //rendering_style != renderingStyle) {

      renderingStyle = rendering_style;

      if (colorScheme == BondInterface.BICOLOR) {
        removeAllChildren();
        rootBranch = this.getRootBranch();
        shape = getBicolorLine(bond);
        rootBranch.addChild(shape);
        addChild(rootBranch);
      }
      else if (colorScheme == BondInterface.MONOCOLOR) {
        removeAllChildren();
        shape = getMonocolorLine(bond);
        rootBranch = this.getRootBranch();
        rootBranch.addChild(shape);
        addChild(rootBranch);
      }

    }
  }

  void updateTransform3D(float[] origin_1, float[] origin_2) {

    if (numChildren() < 1) {
      return;
    }

    float dX = origin_2[0] - origin_1[0];
    float dY = origin_2[1] - origin_1[1];
    float dZ = origin_2[2] - origin_1[2];

    float bondLength = (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);

    if (renderingStyle == BondInterface.CYLINDER_MONOCOLOR) {
      // Get orientation
      getOrientation(origin_1, origin_2);

      Transform3D move = new Transform3D();
      move.setScale(new Vector3d(cylinderRadius, bondLength, cylinderRadius));
      move.setRotation(rot);
      move.setTranslation(new Vector3f( (origin_1[0] + 0.5f * bondLength * vX),
                                       (origin_1[1] + 0.5f * bondLength * vY),
                                       (origin_1[2] + 0.5f * bondLength * vZ)));

      TransformGroup atg = (TransformGroup) rootBranch.getChild(0); // !!1 Only 1 child now
      atg.setTransform(move);
    }

    else if (renderingStyle == BondInterface.CYLINDER_BICOLOR) {
      // Get orientation
      getOrientation(origin_1, origin_2);

      Transform3D move = new Transform3D();
      move.setScale(new Vector3d(cylinderRadius, bondLength / 2, cylinderRadius));
      move.setRotation(rot);
      move.setTranslation(new Vector3f( (origin_1[0] + 0.25f * bondLength * vX),
                                       (origin_1[1] + 0.25f * bondLength * vY),
                                       (origin_1[2] + 0.25f * bondLength * vZ)));

      TransformGroup atg = (TransformGroup) rootBranch.getChild(0);
      atg.setTransform(move);

      move = new Transform3D();
      move.setScale(new Vector3d(cylinderRadius, bondLength / 2, cylinderRadius));
      move.setRotation(rot);
      move.setTranslation(new Vector3f( (origin_1[0] + 0.75f * bondLength * vX),
                                       (origin_1[1] + 0.75f * bondLength * vY),
                                       (origin_1[2] + 0.75f * bondLength * vZ)));

      atg = (TransformGroup) rootBranch.getChild(1);
      atg.setTransform(move);
    }

    else if (renderingStyle == BondInterface.LINE_MONOCOLOR) {
      System.err.println("BondInterface.LINE_MONOCOLOR is not implemented yet...");
    }

    else if (renderingStyle == BondInterface.LINE_BICOLOR) {
      coordinates[0].set(origin_1[0], origin_1[1], origin_1[2]);
      coordinates[1].set(origin_1[0] + (origin_2[0] - origin_1[0]) / 2.0f,
                         origin_1[1] + (origin_2[1] - origin_1[1]) / 2.0f,
                         origin_1[2] + (origin_2[2] - origin_1[2]) / 2.0f);
      coordinates[2].set(origin_1[0] + (origin_2[0] - origin_1[0]) / 2.0f,
                         origin_1[1] + (origin_2[1] - origin_1[1]) / 2.0f,
                         origin_1[2] + (origin_2[2] - origin_1[2]) / 2.0f);
      coordinates[3].set(origin_2[0], origin_2[1], origin_2[2]);

      if (lineBond.getCapability(GeometryArray.ALLOW_COORDINATE_WRITE)) {
        lineBond.setCoordinates(0, coordinates);
      }
      else {
        System.err.println("updateTransform3D: no capability to set coordinates");
      }
    }

  }

  public void setBondColoring(int coloring) throws Exception {
    if (coloring != BondInterface.MONOCOLOR &&
        coloring != BondInterface.BICOLOR) {
      throw new Exception("Wrong bond coloring scheme");
    }
    bondColoring = coloring;
  }

  public float getCylinderRadius() {
    return cylinderRadius;
  }

  public void setCylinderRadius(float radius) {
    cylinderRadius = radius;
  }

  BranchGroup getRootBranch() {
    BranchGroup root = new BranchGroup();
    root.setCapability(Group.ALLOW_CHILDREN_READ);
    root.setCapability(Group.ALLOW_CHILDREN_WRITE);
    root.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    root.setCapability(BranchGroup.ALLOW_DETACH);
    return root;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    if (bond != null) {
      return new BondNode(bond);
    }
    return null;
  }
}
