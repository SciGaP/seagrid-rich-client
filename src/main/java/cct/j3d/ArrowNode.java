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
import org.scijava.java3d.Group;
import org.scijava.java3d.Material;
import org.scijava.java3d.Node;
import org.scijava.java3d.PolygonAttributes;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.vecmath.AxisAngle4f;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Vector3d;
import org.scijava.vecmath.Vector3f;

import org.scijava.java3d.utils.geometry.Cone;
import org.scijava.java3d.utils.geometry.Cylinder;
import org.scijava.java3d.utils.geometry.Primitive;

/**
 * <p>Title: Computational Chemistry Tookit</p>
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
public class ArrowNode
    extends BranchGroup {

  private Color3f arrowColor = new Color3f(1.0f, 0.5f, 0.7f);
  private Color3f arrowHeadColor = new Color3f(1.0f, 0.5f, 0.7f);
  private float arrowRadius = 0.2f;
  private float arrowHeadRadius = 0.2f;
  private float arrowHeadLength = 2.0f * arrowHeadRadius;
  private BranchGroup rootBranch = new BranchGroup();
  private Cylinder cylinder = null;
  private Cone arrowHead = null;
  private float arrowLength = 1.0f;

  private float vX, vY, vZ;
  private float originX = 0, originY = 0, originZ = 0;
  private float angle;
  private AxisAngle4f rot = new AxisAngle4f();

  public ArrowNode() {
    super();
    init();
  }

  public ArrowNode(float[] origin, float[] direction) {
    super();
    init();

    getOrientation(direction);
    arrowLength = (float) Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1] + direction[2] * direction[2]);

    originX = origin[0];
    originY = origin[1];
    originZ = origin[2];

  }

  private void init() {
    setCapability(Group.ALLOW_CHILDREN_READ);
    setCapability(Group.ALLOW_CHILDREN_WRITE);
    setCapability(Group.ALLOW_CHILDREN_EXTEND);
    setCapability(BranchGroup.ALLOW_DETACH);

    rootBranch.setCapability(Group.ALLOW_CHILDREN_READ);
    rootBranch.setCapability(Group.ALLOW_CHILDREN_WRITE);
    rootBranch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    rootBranch.setCapability(BranchGroup.ALLOW_DETACH);

  }

  public void setArrowLength(float new_length) {
    arrowLength = new_length;
  }

  public void setArrowHeadLength(float new_length) {
    arrowHeadLength = new_length;
  }

  public void setArrowRadius(float new_radius) {
    arrowRadius = new_radius;
  }

  public void setArrowHeadRadius(float new_radius) {
    arrowHeadRadius = new_radius;
  }

  public void createArrow() {
    Transform3D move = new Transform3D();
    move.setScale(new Vector3d(arrowRadius, arrowLength, arrowRadius));
    move.setRotation(rot);
    move.setTranslation(new Vector3f( (originX + 0.5f * arrowLength * vX),
                                     (originY + 0.5f * arrowLength * vY),
                                     (originZ + 0.5f * arrowLength * vZ)));

    TransformGroup atg = new TransformGroup(move);
    atg.setCapability(Group.ALLOW_CHILDREN_READ);
    atg.setCapability(Group.ALLOW_CHILDREN_WRITE);
    atg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    atg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    Appearance aAppear = getAppearance(arrowColor);

    cylinder = createCylinder(1, 1, aAppear);
    cylinder.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
    cylinder.setCapability(Group.ALLOW_CHILDREN_READ);
    cylinder.setCapability(Group.ALLOW_CHILDREN_WRITE);
    UserData.setUserData(cylinder, UserData.NODE_NAME, "Arrow");

    atg.addChild(cylinder);

    rootBranch.addChild(atg);

    // --- Arrow head

    TransformGroup tg = new TransformGroup();
    tg.setCapability(Group.ALLOW_CHILDREN_READ);
    tg.setCapability(Group.ALLOW_CHILDREN_WRITE);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    Transform3D transform = new Transform3D();
    transform.setScale(new Vector3d(arrowHeadRadius, arrowHeadLength / 2.0f, arrowHeadRadius));
    // Constructs a default Cone of radius of 1.0 and height of 2.0.
    arrowHead = new Cone();
    arrowHead.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
    arrowHead.setCapability(Group.ALLOW_CHILDREN_READ);
    arrowHead.setCapability(Node.ALLOW_BOUNDS_WRITE);
    UserData.setUserData(arrowHead, UserData.NODE_NAME, "Arrow_Head");
    aAppear = getAppearance(arrowHeadColor);
    arrowHead.setAppearance(aAppear);

    transform.setRotation(rot);
    transform.setTranslation(new Vector3f( (float) (originX + (arrowHeadLength / 2.0 + arrowLength) * vX),
                                          (float) (originY + (arrowHeadLength / 2.0 + arrowLength) * vY),
                                          (float) (originZ + (arrowHeadLength / 2.0 + arrowLength) * vZ)));

    tg.setTransform(transform);
    tg.addChild(arrowHead);
    rootBranch.addChild(tg);

    addChild(rootBranch);
  }

  public void setNodeName(String name) {
    if (cylinder == null) {
      return;
    }
    UserData.setUserData(cylinder, UserData.NODE_NAME, name);
    UserData.setUserData(arrowHead, UserData.NODE_NAME, name);
  }

  private Cylinder createCylinder(float radius, float height, Appearance ap) {
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

  public void setColor(Color3f color) {
    try {
      Appearance aAppear = getAppearance(color);
      cylinder.setAppearance(aAppear);
      aAppear = getAppearance(color);
      arrowHead.setAppearance(aAppear);
    }
    catch (Exception ex) {
      System.err.println(this.getClass().getCanonicalName() + ": setColor: " + ex.getMessage());
    }
  }

  private Appearance getAppearance(Color3f color) {
    ColoringAttributes ca = new ColoringAttributes();
    ca.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
    ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

    ca.setColor(color);
    Appearance aAppear = new Appearance();
    aAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
    aAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    aAppear.setCapability(Appearance.ALLOW_MATERIAL_READ);
    aAppear.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    aAppear.setColoringAttributes(ca);

    Material material = createMaterial(color);
    aAppear.setMaterial(material);

    PolygonAttributes polyAppear = new PolygonAttributes();
    polyAppear.setCullFace(PolygonAttributes.CULL_BACK);

    aAppear.setPolygonAttributes(polyAppear);
    return aAppear;
  }

  private Material createMaterial(Color3f color) {
    Material material = new Material();
    // --- Diffuse Color
    material.setDiffuseColor(color);

    // --- Ambient Color
    material.setAmbientColor(0.2f * color.x, 0.2f * color.y, 0.2f * color.z);

    // --- Emissive Color
    material.setEmissiveColor(0.0f, 0.0f, 0.0f);

    // --- Specular Color
    material.setSpecularColor(1.0f, 1.0f, 1.0f);

    // --- Set Shininess
    material.setShininess(15.0f);

    material.setCapability(Material.ALLOW_COMPONENT_READ);
    material.setCapability(Material.ALLOW_COMPONENT_WRITE);
    return material;
  }

  private void getOrientation(float[] direction) {

    // Get orientation
    vX = direction[0];
    vY = direction[1];
    vZ = direction[2];
    float norm = (float) Math.sqrt(vX * vX + vY * vY + vZ * vZ);
    vX /= norm;
    vY /= norm;
    vZ /= norm;

    angle = (float) Math.acos(vY);
    if (vY + 1.0 < 0.001) {
      rot.set(1.0f, 0.0f, 0.0f, angle);
    }
    else {
      rot.set(vZ, 0.0f, -vX, angle);
    }
  }

  public static void main(String[] args) {
    ArrowNode arrownode = new ArrowNode();
  }

}
