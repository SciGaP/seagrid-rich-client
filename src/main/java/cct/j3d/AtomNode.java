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

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import org.scijava.java3d.Appearance;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.ColoringAttributes;
import org.scijava.java3d.GeometryArray;
import org.scijava.java3d.Group;
import org.scijava.java3d.Material;
import org.scijava.java3d.Node;
import org.scijava.java3d.PointArray;
import org.scijava.java3d.PointAttributes;
import org.scijava.java3d.PolygonAttributes;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Point3f;
import org.scijava.vecmath.Vector3f;

import cct.interfaces.AtomInterface;
import cct.modelling.ChemicalElements;

import org.scijava.java3d.utils.geometry.Primitive;
import org.scijava.java3d.utils.geometry.Sphere;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.scijava.java3d.*;

/**
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
public class AtomNode
        extends BranchGroup implements Cloneable {
  
  static int defaultNumberDivisions = 100;
  static final Logger logger = Logger.getLogger(AtomNode.class.getCanonicalName());
  BranchGroup rootBranch = null;
  AtomInterface atom = null;
  TransformGroup objTransform = null;
  Transform3D t3d = null;
  ColoringAttributes ca = new ColoringAttributes();
  Appearance aAppear = new Appearance();
  PolygonAttributes polyAppear = new PolygonAttributes();
  Sphere atomicSphere = null;
  Color3f atomColor = null;
  float sphereRadius = 1;
  int numberDivisions = defaultNumberDivisions;
  int renderingStyle = AtomInterface.RENDER_SPHERE;
  Material atomMaterial = null;
  float[] xyz = new float[3];
  PointArray atomPoint = null;
  PointAttributes pointAttributes = null;
  float pointSize = 4;
  static float highlightPointSize = 8;
  boolean pointAntialiasing = true;
  Shape3D atomShape = null;
  boolean visible = true;
  boolean highlighted = false;
  boolean useSwitch = true;
  private Switch showSwitch = new Switch();
  private Map<J3D_ATOM_PROPERTY, BranchGroup> switchShapes = new HashMap<J3D_ATOM_PROPERTY, BranchGroup>();
  private Map<BranchGroup, Integer> switchIndex = new HashMap<BranchGroup, Integer>();
  
  public AtomNode(AtomInterface atom) {
    this(atom, defaultNumberDivisions);
    
    setCapability(Group.ALLOW_CHILDREN_READ);
    setCapability(Group.ALLOW_CHILDREN_WRITE);
    setCapability(Group.ALLOW_CHILDREN_EXTEND);
    setCapability(Group.ALLOW_CHILDREN_WRITE);
    setCapability(Node.ALLOW_PICKABLE_READ);
    setCapability(Node.ALLOW_PICKABLE_WRITE);
    setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
    setCapability(Node.ENABLE_PICK_REPORTING);
    setCapability(BranchGroup.ALLOW_DETACH);
    setPickable(true);
    
  }
  
  @Override
  public void detach() {
    System.err.println("DO not use detach in AtomNode!!!");
    Object obj = this.getRootBranch();
    if (obj instanceof MoleculeNode) {
      ((MoleculeNode) obj).removeChild(this);
      return;
    }
    super.detach();
  }
  
  public AtomNode(AtomInterface _atom, int divisions) {
    
    atom = _atom;
    UserData.setUserData(this, UserData.NODE_NAME, "Atom_" + ChemicalElements.getElementSymbol(atom.getAtomicNumber()));
    //setName("Atom_" + atom.getName()); // --- >1.4 java3d feature

    xyz[0] = atom.getX();
    xyz[1] = atom.getY();
    xyz[2] = atom.getZ();
    
    numberDivisions = divisions;

    // --- Setup RGB color of atomic sphere
    atomColor = getAtomColor(atom);

    // --- Getting rendering style
    renderingStyle = getRenderingStyle(atom);

    // --- Getting visibility
    Object obj = atom.getProperty(AtomInterface.VISIBLE);
    if (obj != null && obj instanceof Boolean) {
      visible = (Boolean) obj;
    }

    //Transform3D move = new Transform3D();
    //move.set(new Vector3f(x, y, z));
    //TransformGroup objMove = new TransformGroup(move);
    t3d = new Transform3D();
    t3d.set(new Vector3f(xyz[0], xyz[1], xyz[2]));

    // --- Setup radius of atomic sphere
    sphereRadius = getAtomicSphereRadius(atom);
    if (renderingStyle == AtomInterface.RENDER_POINT || renderingStyle == AtomInterface.RENDER_SMART_POINT) {
      t3d.setScale(1.0);
    } else {
      t3d.setScale(sphereRadius);
    }
    objTransform = this.getTransformGroup(t3d);
    
    rootBranch = this.getRootBranch();
    rootBranch.addChild(objTransform);
    if (this.useSwitch) {
      objTransform.addChild(showSwitch);
      showSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
      showSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
      showSwitch.setCapability(Switch.ALLOW_CHILDREN_EXTEND);
      showSwitch.setCapability(Switch.ALLOW_CHILDREN_READ);
      showSwitch.setCapability(Switch.ALLOW_CHILDREN_WRITE);
    }
    
    addChild(rootBranch);

    // --- Render atom
    if (renderingStyle == AtomInterface.RENDER_POINT) {
      Node point = null;
      if (useSwitch) {
        BranchGroup bg = switchShapes.get(J3D_ATOM_PROPERTY.POINT_3D);
        if (bg == null) {
          bg = new BranchGroup();
          point = createAtomicPoint(pointSize, atomColor);
          bg.addChild(point);
          switchShapes.put(J3D_ATOM_PROPERTY.POINT_3D, bg);
          showSwitch.addChild(bg);
          switchIndex.put(bg, showSwitch.numChildren() - 1);
        }
        showSwitch.setWhichChild(switchIndex.get(bg));
      } else {
        point = createAtomicPoint(pointSize, atomColor);
        objTransform.addChild(point);
      }
      //*************************************************************
    } else if (renderingStyle == AtomInterface.RENDER_SMART_POINT) {
      Node point;
      if (useSwitch) {
        if (isRenderPoint()) {
          BranchGroup bg = switchShapes.get(J3D_ATOM_PROPERTY.POINT_3D);
          if (bg == null) {
            bg = new BranchGroup();
            point = createAtomicPoint(pointSize, atomColor);
            bg.addChild(point);
            switchShapes.put(J3D_ATOM_PROPERTY.POINT_3D, bg);
            showSwitch.addChild(bg);
            switchIndex.put(bg, showSwitch.numChildren() - 1);
          }
          showSwitch.setWhichChild(switchIndex.get(bg));
        } else {
          showSwitch.setWhichChild(Switch.CHILD_NONE);
        }
        
      } else {
        point = renderPointIfNecessary();
        if (point != null) {
          objTransform.addChild(point);
        }
      }
      
    } else if (renderingStyle == AtomInterface.RENDER_SPHERE) {
      // --- Set material of atomic sphere

      //Node sphere;
      if (this.useSwitch) {
        BranchGroup bg = switchShapes.get(J3D_ATOM_PROPERTY.SPHERE_3D);
        if (bg == null) {
          bg = new BranchGroup();
          atomicSphere = createAtomicSphere(atom);
          bg.addChild(atomicSphere);
          switchShapes.put(J3D_ATOM_PROPERTY.SPHERE_3D, bg);
          showSwitch.addChild(bg);
          switchIndex.put(bg, showSwitch.numChildren() - 1);
        }
        showSwitch.setWhichChild(switchIndex.get(bg));
      } else {
        atomicSphere = createAtomicSphere(atom);
        objTransform.addChild(atomicSphere);
      }
    }
  }
  
  Sphere createAtomicSphere(AtomInterface atom) {
    atomMaterial = ChemicalElementsColors.createMaterial(atomColor);
    
    Color3f color = new Color3f();
    atomMaterial.getAmbientColor(color);
    atom.setProperty(AtomInterface.AMBIENT_RGB_COLOR, new float[]{color.x, color.y, color.z});
    
    atomMaterial.getDiffuseColor(color);
    atom.setProperty(AtomInterface.DIFFUSE_RGB_COLOR, new float[]{color.x, color.y, color.z});
    
    atomMaterial.getSpecularColor(color);
    atom.setProperty(AtomInterface.SPECULAR_RGB_COLOR, new float[]{color.x, color.y, color.z});
    
    return createAtomicSphere(numberDivisions, atomColor, atomMaterial);
  }
  
  public boolean isVisible() {
    return visible;
  }
  
  public AtomNode(float radius, float x, float y, float z, int divisions, Color3f color, Material material) {
    super();
    
    sphereRadius = radius;
    atomColor = color;
    numberDivisions = divisions;
    atomMaterial = material;
    xyz[0] = x;
    xyz[1] = y;
    xyz[2] = z;
    
    t3d = new Transform3D();
    t3d.set(new Vector3f(xyz[0], xyz[1], xyz[2]));
    t3d.setScale(sphereRadius);
    
    atomicSphere = createAtomicSphere(divisions, color, material);
    if (useSwitch) {
      BranchGroup bg = new BranchGroup();
      bg.addChild(atomicSphere);
      switchShapes.put(J3D_ATOM_PROPERTY.SPHERE_3D, bg);
      showSwitch.addChild(bg);
      switchIndex.put(bg, showSwitch.numChildren() - 1);
      showSwitch.setWhichChild(switchIndex.get(bg));
    } else {
      objTransform.addChild(atomicSphere);
    }
  }
  
  private Node createAtomicPoint(float point_size, Color3f color) {
    
    atomPoint = new PointArray(1, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
    
    atomPoint.setCapability(GeometryArray.ALLOW_COLOR_READ);
    atomPoint.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
    atomPoint.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    atomPoint.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);

    //atomPoint.setCoordinate(0, new Point3f(atom.getX(), atom.getY(), atom.getZ()));
    atomPoint.setCoordinate(0, new Point3f(0, 0, 0)); // --- Location is already in transform3d
    atomPoint.setColor(0, color);
    
    Shape3D atomShape = new Shape3D(atomPoint);
    atomShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    atomShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    
    pointAttributes = new PointAttributes(point_size, pointAntialiasing);
    aAppear.setPointAttributes(pointAttributes);
    
    atomShape.setAppearance(aAppear);

    //addChild(atomShape);
    return atomShape;
  }
  
  boolean isRenderPoint() {
    List bondedAtoms = atom.getBondedToAtoms();
    if (bondedAtoms == null || bondedAtoms.size() < 1) {
      return true;
    }
    Iterator iter = bondedAtoms.iterator();
    while (iter.hasNext()) {
      AtomInterface a = (AtomInterface) iter.next();
      Object obj = a.getProperty(AtomInterface.VISIBLE);
      if (obj == null) { // So, it's visible
        return false;
      }
      if (obj != null && obj instanceof Boolean) {
        boolean vis = (Boolean) obj;
        if (vis) {
          return false;
        }
      }
    }
    return true;
  }
  
  Node renderPointIfNecessary() {
    if (isRenderPoint()) {
      return createAtomicPoint(pointSize, atomColor);
    } else {
      return null;
    }
  }
  
  private Sphere createAtomicSphere(int divisions, Color3f color, Material material) {
    
    ca.setColor(color);
    
    aAppear.setColoringAttributes(ca);
    
    aAppear.setMaterial(material);
    
    polyAppear.setCullFace(PolygonAttributes.CULL_BACK);
    
    aAppear.setPolygonAttributes(polyAppear);
    aAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
    aAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    aAppear.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_READ);
    aAppear.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);
    aAppear.setCapability(Appearance.ALLOW_MATERIAL_READ);
    aAppear.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    aAppear.setCapability(Appearance.ALLOW_POINT_ATTRIBUTES_READ);
    aAppear.setCapability(Appearance.ALLOW_POINT_ATTRIBUTES_WRITE);
    aAppear.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    aAppear.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
    aAppear.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
    aAppear.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);
    aAppear.setCapability(Appearance.ALLOW_TEXGEN_READ);
    aAppear.setCapability(Appearance.ALLOW_TEXGEN_WRITE);
    aAppear.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
    aAppear.setCapability(Appearance.ALLOW_TEXTURE_READ);
    aAppear.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_READ);
    aAppear.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);

    // Setup atoms
    Sphere atomicSphere = new Sphere(1, Primitive.GENERATE_NORMALS, divisions, aAppear);
    //objTransform.addChild(atomicSphere);

    Shape3D shape = atomicSphere.getShape();
    
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
    atomicSphere.setCapability(Group.ALLOW_CHILDREN_READ);
    atomicSphere.setCapability(Group.ALLOW_CHILDREN_WRITE);
    atomicSphere.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    atomicSphere.setCapability(Node.ALLOW_PICKABLE_READ);
    atomicSphere.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
    
    shape.setAppearanceOverrideEnable(true);
    
    return atomicSphere;
  }
  
  public Sphere getAtomicSphere() {
    return atomicSphere;
  }

  /**
   * returns sphere radius for drawing an atom
   *
   * @param atom AtomInterface
   * @return float
   */
  public static float getAtomicSphereRadius(AtomInterface atom) {
    
    float radius = ChemicalElements.getCovalentRadius(atom.getAtomicNumber());
    radius *= AtomInterface.COVALENT_TO_GRADIUS_FACTOR;
    Float R = (Float) atom.getProperty(AtomInterface.GR_RADIUS);
    if (R != null) {
      radius = R;
    }
    
    if (radius < 0.001f) {
      radius = 0.2f;
    }
    atom.setProperty(AtomInterface.GR_RADIUS, new Float(radius)); // GR_RADIUS "gradius" is a "graphics radius"

    return radius;
  }
  
  public AtomInterface getAtomInterface() {
    return atom;
  }
  
  public float getAtomicSphereRadius() {
    return sphereRadius;
  }
  
  public Material getMaterial() {
    return atomMaterial;
  }
  
  public void setAtomicSphereRadius(float radius) {
    try {
      int style = getRenderingStyle(atom);
      
      if (style == AtomInterface.RENDER_SPHERE && style == this.renderingStyle) {
        sphereRadius = radius;
        //t3d = new Transform3D();
        objTransform.getTransform(t3d);
        t3d.setScale(sphereRadius);
        objTransform.setTransform(t3d);
        //********************************************************************
      } else if (style == AtomInterface.RENDER_SPHERE) {
        renderingStyle = style;
        sphereRadius = radius;
        
        objTransform.getTransform(t3d);
        t3d.setScale(sphereRadius);
        objTransform.setTransform(t3d);
        
        if (useSwitch) {
          BranchGroup bg = switchShapes.get(J3D_ATOM_PROPERTY.SPHERE_3D);
          if (bg == null) {
            bg = new BranchGroup();
            atomicSphere = createAtomicSphere(atom);
            bg.addChild(atomicSphere);
            switchShapes.put(J3D_ATOM_PROPERTY.SPHERE_3D, bg);
            showSwitch.addChild(bg);
            switchIndex.put(bg, showSwitch.numChildren() - 1);
          }
          showSwitch.setWhichChild(switchIndex.get(bg));
        } else {
          removeAllChildren();
          
          atomicSphere = createAtomicSphere(atom);
          rootBranch = this.getRootBranch();
          rootBranch.addChild(objTransform);
          
          addChild(rootBranch);
        }
        //*****************************************************************************
      } else if (style == AtomInterface.RENDER_SMART_POINT && style == this.renderingStyle) {
        //*********************************************************************************
      } else if (style == AtomInterface.RENDER_SMART_POINT && renderingStyle == AtomInterface.RENDER_SPHERE) {
        renderingStyle = style;
        
        if (!useSwitch) {
          removeAllChildren();
          
          objTransform = this.getTransformGroup(t3d);
          rootBranch = this.getRootBranch();
          rootBranch.addChild(objTransform);
          
          addChild(rootBranch);
        }
        
        Node point;
        if (useSwitch) {
          if (isRenderPoint()) {
            BranchGroup bg = switchShapes.get(J3D_ATOM_PROPERTY.POINT_3D);
            if (bg == null) {
              bg = new BranchGroup();
              point = createAtomicPoint(pointSize, atomColor);
              bg.addChild(point);
              switchShapes.put(J3D_ATOM_PROPERTY.POINT_3D, bg);
              showSwitch.addChild(bg);
              switchIndex.put(bg, showSwitch.numChildren() - 1);
            } else {
            }
            showSwitch.setWhichChild(switchIndex.get(bg));
          } else {
          //point = switchShapes.get(J3D_ATOM_PROPERTY.NOTHING);
            //if (point == null) {
            //  point = new Group();
            //  switchShapes.put(J3D_ATOM_PROPERTY.NOTHING, point);
            //  showSwitch.addChild(point);
            //  switchIndex.put(point, showSwitch.numChildren() - 1);
            //}
            showSwitch.setWhichChild(Switch.CHILD_NONE);
          }
        } else {
          point = renderPointIfNecessary();
          if (point != null) {
            objTransform.addChild(point);
          }
        }
      }

      /*
       * TransformGroup scale = getAtomicSphereScale(atom_node); Transform3D value = new Transform3D(); value.setScale(radius);
       * scale.setTransform(value);
       */
    } catch (Exception ex) {
      logger.warning("Exception: " + ex.getMessage());
    }
  }
  
  public void setAtomColor(Color3f new_color, Material new_material) {
    
    aAppear.setMaterial(new_material);
    
    atomColor = new Color3f(new_color);
    
    atom.setProperty(AtomInterface.RGB_COLOR, new Integer[]{new_color.get().getRed(), new_color.get().getGreen(),
      new_color.get().getBlue()});
  }
  
  public void setAtomColor(Color new_color) {
    Color3f color3f = new Color3f(new_color);
    setAtomColor(color3f);
  }
  
  public void setAtomColor(Color3f new_color) {
    if (atomColor == null) {
      atomColor = new Color3f(new_color);
    } else {
      atomColor.set(new_color.x, new_color.y, new_color.z);
    }
    
    atom.setProperty(AtomInterface.RGB_COLOR, new Integer[]{new_color.get().getRed(), new_color.get().getGreen(),
      new_color.get().getBlue()});
    
    atomMaterial = ChemicalElementsColors.createMaterial(atomColor);
    atomMaterial.setCapability(Material.ALLOW_COMPONENT_READ);
    atomMaterial.setCapability(Material.ALLOW_COMPONENT_WRITE);
    
    if (!aAppear.getCapability(Appearance.ALLOW_MATERIAL_WRITE)) {
      aAppear.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    }
    if (!aAppear.getCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE)) {
      aAppear.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    }
    if (atomMaterial.getCapability(Material.ALLOW_COMPONENT_WRITE)) {
      atomMaterial.setCapability(Material.ALLOW_COMPONENT_WRITE);
    }
    
    aAppear.setMaterial(atomMaterial);
  }
  
  public Color3f getColor3f() {
    return atomColor;
  }
  
  public void setAtomicCoordinates(AtomInterface atom) {
    
    xyz[0] = atom.getX();
    xyz[1] = atom.getY();
    xyz[2] = atom.getZ();
    
    if (renderingStyle == AtomInterface.RENDER_SPHERE) {
      
      objTransform.getTransform(t3d);
      t3d.set(new Vector3f(xyz));
      t3d.setScale(sphereRadius);

      //t3d.set(xyz);
      objTransform.setTransform(t3d);
    } else if (renderingStyle == AtomInterface.RENDER_SMART_POINT && atomPoint != null) {
      atomPoint.setCoordinate(0,
              new Point3f(atom.getX(), atom.getY(),
                      atom.getZ()));
    }
  }
  
  public void setAtomicCoordinates(float[] coord) {
    
    xyz[0] = coord[0];
    xyz[1] = coord[1];
    xyz[2] = coord[2];
    
    objTransform.getTransform(t3d);
    t3d.set(new Vector3f(xyz));
    t3d.setScale(sphereRadius);
    objTransform.setTransform(t3d);
  }
  
  static public int getRenderingStyle(AtomInterface a) {
    int rendering_style = AtomInterface.RENDER_SPHERE;
    Object obj = a.getProperty(AtomInterface.RENDERING_STYLE);
    if (obj != null && obj instanceof Integer) {
      rendering_style = (Integer) obj;
    }
    return rendering_style;
  }
  
  static public Color3f getAtomColor(AtomInterface a) {
    Object obj = a.getProperty(AtomInterface.RGB_COLOR);
    Color3f color;
    if (obj != null && obj instanceof Integer[]) {
      Integer[] rgbColor = (Integer[]) obj;
      color = new Color3f(rgbColor[0].floatValue() / 255.0f,
              rgbColor[1].floatValue() / 255.0f,
              rgbColor[2].floatValue() / 255.0f);
    } else {
      color = ChemicalElementsColors.getElementColor(a.getAtomicNumber());
      Integer[] rgbColor = new Integer[3];
      rgbColor[0] = (int) (color.x * 255.0f);
      rgbColor[1] = (int) (color.y * 255.0f);
      rgbColor[2] = (int) (color.z * 255.0f);
      a.setProperty(AtomInterface.RGB_COLOR, rgbColor);
    }
    return color;
  }
  
  public boolean isHighlighted() {
    return highlighted;
  }
  
  public void highlightAtom(boolean highlight) {
    
    if (highlighted && highlight) {
      return;
    }
    if ((!highlighted) && (!highlight)) {
      return;
    }
    
    highlighted = highlight;
    
    int rendering_style = getRenderingStyle(atom);
    if (rendering_style != this.renderingStyle) {
      this.renderingStyle = rendering_style;
    }
    
    if (highlight && renderingStyle == AtomInterface.RENDER_SPHERE) {
      aAppear.setMaterial(ChemicalElementsColors.getHighlightMaterial());
      atomicSphere.setAppearance(aAppear);
    } else if (highlight && renderingStyle == AtomInterface.RENDER_SMART_POINT) {
      
      if (atomShape != null) {
        pointAttributes.setPointSize(highlightPointSize);
        aAppear.setPointAttributes(pointAttributes);
        atomShape.removeAllGeometries();
        atomPoint = new PointArray(1, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
        
        atomPoint.setCapability(GeometryArray.ALLOW_COLOR_READ);
        atomPoint.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
        atomPoint.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
        atomPoint.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);

        //atomPoint.setCoordinate(0, new Point3f(atom.getX(), atom.getY(), atom.getZ()));
        atomPoint.setCoordinate(0, new Point3f(0, 0, 0)); // --- Location is already in transform3d

        atomPoint.setColor(0, ChemicalElementsColors.getHighlightColor3f());
        atomShape.setGeometry(atomPoint);
        atomShape.setAppearance(aAppear);
        
      } else {
        Node point;
        if (this.useSwitch) {
          BranchGroup bg = switchShapes.get(J3D_ATOM_PROPERTY.POINT_3D);
          if (bg == null) {
            point = createAtomicPoint(pointSize, atomColor);
            bg = new BranchGroup();
            bg.addChild(point);
            switchShapes.put(J3D_ATOM_PROPERTY.POINT_3D, bg);
            showSwitch.addChild(bg);
            switchIndex.put(bg, showSwitch.numChildren() - 1);
          }
          showSwitch.setWhichChild(switchIndex.get(bg));
        } else {
          point = createAtomicPoint(highlightPointSize, ChemicalElementsColors.getHighlightColor3f());
          objTransform.addChild(point);
        }
        
      }
    } else if ((!highlight) && renderingStyle == AtomInterface.RENDER_SMART_POINT) {
      objTransform.removeAllChildren();
      renderPointIfNecessary();
    } else if ((!highlight) && renderingStyle == AtomInterface.RENDER_SPHERE) {
      aAppear.setMaterial(atomMaterial);
      atomicSphere.setAppearance(aAppear);
    }
    
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    super.clone();
    AtomNode atom_node = null;
    if (atom == null) {
      atom_node = new AtomNode(sphereRadius, xyz[0], xyz[1], xyz[2], numberDivisions, atomColor, atomMaterial);
    } else {
      atom_node = new AtomNode(atom, numberDivisions);
    }
    return atom_node;
  }
  
  private BranchGroup getRootBranch() {
    BranchGroup root = new BranchGroup();
    root.setCapability(Group.ALLOW_CHILDREN_READ);
    root.setCapability(Group.ALLOW_CHILDREN_WRITE);
    root.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    root.setCapability(BranchGroup.ALLOW_DETACH);
    return root;
  }
  
  private TransformGroup getTransformGroup() {
    TransformGroup tg = new TransformGroup();
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.setCapability(Group.ALLOW_CHILDREN_READ);
    tg.setCapability(Group.ALLOW_CHILDREN_WRITE);
    //tg.setName("Atom"); // --- >1.4 java3d feature
    return tg;
  }
  
  private TransformGroup getTransformGroup(Transform3D t3d) {
    TransformGroup tg = new TransformGroup(t3d);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.setCapability(Group.ALLOW_CHILDREN_READ);
    tg.setCapability(Group.ALLOW_CHILDREN_WRITE);
    //tg.setName("Atom"); // --- >1.4 java3d feature
    return tg;
  }
}
