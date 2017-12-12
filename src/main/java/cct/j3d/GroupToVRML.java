package cct.j3d;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.scijava.java3d.Alpha;
import org.scijava.java3d.AmbientLight;
import org.scijava.java3d.Appearance;
import org.scijava.java3d.Background;
import org.scijava.java3d.Behavior;
import org.scijava.java3d.BoundingLeaf;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.Canvas3D;
import org.scijava.java3d.DirectionalLight;
import org.scijava.java3d.ExponentialFog;
import org.scijava.java3d.Fog;
import org.scijava.java3d.Geometry;
import org.scijava.java3d.GeometryArray;
import org.scijava.java3d.GeometryStripArray;
import org.scijava.java3d.Group;
import org.scijava.java3d.Leaf;
import org.scijava.java3d.Light;
import org.scijava.java3d.LinearFog;
import org.scijava.java3d.Locale;
import org.scijava.java3d.Material;
import org.scijava.java3d.Node;
import org.scijava.java3d.PointLight;
import org.scijava.java3d.PolygonAttributes;
import org.scijava.java3d.SceneGraphObject;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.Switch;
import org.scijava.java3d.SwitchValueInterpolator;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.java3d.TransparencyAttributes;
import org.scijava.java3d.TriangleArray;
import org.scijava.java3d.TriangleFanArray;
import org.scijava.java3d.TriangleStripArray;
import org.scijava.java3d.View;
import org.scijava.java3d.ViewPlatform;
import javax.swing.JFileChooser;
import org.scijava.vecmath.AxisAngle4d;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Color4f;
import org.scijava.vecmath.Matrix3d;
import org.scijava.vecmath.Point3d;
import org.scijava.vecmath.Vector3d;
import org.scijava.vecmath.Vector3f;

import cct.GlobalConstants;
import cct.tools.FileFilterImpl;

import org.scijava.java3d.utils.geometry.Cone;
import org.scijava.java3d.utils.geometry.Cylinder;
import org.scijava.java3d.utils.geometry.Primitive;
import org.scijava.java3d.utils.geometry.Sphere;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public class GroupToVRML
    implements GlobalConstants {

  public enum DUMP_SWITCH {
    ALL, ACTIVE_CHILD_ONLY}

    DUMP_SWITCH dumpSwitch = DUMP_SWITCH.ALL;

    private static String lastPWDKey = "lastPWD";

    private boolean skipLight = true;

    private boolean writeColorsPerVertex = true;
    private Preferences prefs = Preferences.userNodeForPackage(getClass());
    private File currentWorkingDirectory = null;
    private JFileChooser chooser = null;
    private FileFilterImpl filter = null;

    private Node node = null;
    private Locale locale = null;
    private Canvas3D canvas3D = null;
    private String prefix = "";
    static final Logger logger = Logger.getLogger(GroupToVRML.class.getCanonicalName());

    public GroupToVRML(Node nd) {
      node = nd;
    }

    public GroupToVRML(Locale loc) {
      locale = loc;
    }

    public void setCanvas3D(Canvas3D c3d) {
      canvas3D = c3d;
    }

    public void setPrefix(String pref) {
      prefix = pref;
    }

    public void enableColorsPerVertex(boolean enable) {
      writeColorsPerVertex = enable;
    }

    public static void main(String[] args) {
      //BranchGroupToVRML java3dtovrml = new BranchGroupToVRML();
    }

    public void getNode(Node node, OutputStream vrml) {
      try {
        if (node instanceof Group) {
          vrml.write("\n".getBytes());
          getGroup( (Group) node, vrml);
          vrml.write("\n".getBytes());
        }
        else if (node instanceof Leaf) {
          vrml.write("\n".getBytes());
          getLeaf( (Leaf) node, vrml);
          vrml.write("\n".getBytes());
        }
        else {
          System.err.println("getNode: don't know how to handle class: " + node.getClass().getCanonicalName());
          return;
        }
      }
      catch (Exception ex) {
        System.err.println("getNode: I/O Error: " + ex.getMessage());
      }
    }

    /**
     * Writes Locale into OutputStream
     * @param locale Locale
     * @param vrml OutputStream
     */
    public void getLocale(Locale locale, OutputStream vrml) throws Exception {
      int n = locale.numBranchGraphs();
      if (n < 1) {
        return;
      }

      Enumeration branchGraphs = null;
      try {
        branchGraphs = locale.getAllBranchGraphs();
      }
      catch (Exception ex) {
        System.err.println("getLocale: " + ex.getMessage());
        return;
      }

      while (branchGraphs.hasMoreElements()) {
        BranchGroup bg = (BranchGroup) branchGraphs.nextElement();
        getBranchGroup(bg, vrml);
      }
    }

    /**
     * Converts Group into VRML String
     * @param group Group
     * @return String
     */
    public void getGroup(Group group, OutputStream vrml) throws Exception {
      Enumeration children = group.getAllChildren();
      if (!children.hasMoreElements()) {
        return;
      }

      while (children.hasMoreElements()) {
        Object obj = children.nextElement();

        if (obj instanceof BranchGroup) {
          vrml.write("\n".getBytes());
          getBranchGroup( (BranchGroup) obj, vrml);
          vrml.write("\n".getBytes());
        }
        else if (obj instanceof Primitive) {
          vrml.write("\n".getBytes());
          getPrimitive( (Primitive) obj, vrml);
          vrml.write("\n".getBytes());
        }
        else if (obj instanceof TransformGroup) {
          vrml.write("\n".getBytes());
          getTransformGroup( (TransformGroup) obj, vrml);
          vrml.write("\n".getBytes());
        }
        else if (obj instanceof Switch) {
          vrml.write("\n".getBytes());
          getSwitch( (Switch) obj, vrml);
          vrml.write("\n".getBytes());
        }
        else if (obj instanceof Leaf) {
          vrml.write("\n".getBytes());
          getLeaf( (Leaf) obj, vrml);
          vrml.write("\n".getBytes());
        }
        else {
          System.err.println("getGroup: don't know how to handle class: " + obj.getClass().getCanonicalName());
        }
      }
    }

    public void getPrimitive(Primitive prim, OutputStream vrml) throws Exception {

      if (prim instanceof Sphere) {
        getSphere( (Sphere) prim, vrml);
      }
      else if (prim instanceof Cylinder) {
        getCylinder( (Cylinder) prim, vrml);
      }
      else if (prim instanceof Cone) {
        getCone( (Cone) prim, vrml);
      }
      else {
        System.err.println("getPrimitiveAsVRMLString: do not know how to handle class " + prim.getClass().getCanonicalName());
      }
    }

    public void getSphere(Sphere sphere, OutputStream vrml) throws Exception {

      vrml.write("\n  Shape { # ".getBytes());
      vrml.write(createDEF(sphere, UserData.NODE_NAME).getBytes());
      vrml.write("\n".getBytes());
      try {
        Appearance app = sphere.getAppearance();
        getAppearance(app, vrml);
      }
      catch (Exception ex) {
        System.err.println("getSphereGroupAsVRMLString: " + ex.getMessage());
      }

      vrml.write( ("    geometry Sphere { radius " + sphere.getRadius() + " }\n").getBytes());

        vrml.write("  }\n".getBytes());
    }

    public void getCylinder(Cylinder cylinder, OutputStream vrml) throws Exception {

      vrml.write( ("\n  " + createDEF(cylinder, UserData.NODE_NAME) + " Shape { # " +
                   UserData.getUserDataAsString(cylinder, UserData.NODE_NAME) + "\n").getBytes());
      try {
        Appearance app = cylinder.getAppearance();
        getAppearance(app, vrml);
      }
      catch (Exception ex) {
        System.err.println("getCylinderGroupAsVRMLString: " + ex.getMessage());
      }

      vrml.write( ("    geometry Cylinder { radius " + cylinder.getRadius() + " height " + cylinder.getHeight() + " }\n").getBytes());

        vrml.write("  }\n".getBytes());
    }

    public void getCone(Cone cone, OutputStream vrml) throws Exception {

      vrml.write( ("\n  " + createDEF(cone, UserData.NODE_NAME) + " Shape { # " +
                   UserData.getUserDataAsString(cone, UserData.NODE_NAME) + "\n").getBytes());
      try {
        Appearance app = cone.getAppearance();
        getAppearance(app, vrml);
      }
      catch (Exception ex) {
        System.err.println("getConeGroupAsVRMLString: " + ex.getMessage());
      }

      vrml.write( ("    geometry Cone { bottomRadius " + cone.getRadius() + " height " + cone.getHeight() + " }\n").getBytes());

      vrml.write("  }\n".getBytes());
    }

    /*
       public String getVRMLAsString(Group gr) throws Exception {
      StringWriter vrml = new StringWriter(1024);
      Enumeration children = gr.getAllChildren();
      if (!children.hasMoreElements()) {
        return "";
      }

      if (gr instanceof BranchGroup) {
        vrml.write("\n" + getBranchGroupAsVRMLString( (BranchGroup) gr) + "\n");
      }
      else if (gr instanceof TransformGroup) {
        vrml.write("\n" + getTransformGroupAsVRMLString( (TransformGroup) gr) + "\n");
      }
      else if (gr instanceof Switch) {
        vrml.write("\n" + getSwitchAsVRMLString( (Switch) gr) + "\n");
      }

      else {
        System.err.println("getVRMLAsString: don't know how to handle class: " + gr.getClass().getCanonicalName());
      }

      return vrml.toString();
       }
     */

    public void getBranchGroup(BranchGroup bg, OutputStream vrml) throws Exception {
      Enumeration children = bg.getAllChildren();
      if (!children.hasMoreElements()) {
        return;
      }

      vrml.write( ("\n" + createDEF(bg, UserData.NODE_NAME) + " Group { children [\n").getBytes());

      while (children.hasMoreElements()) {
        Object obj = children.nextElement();
        if (obj instanceof Group) {
          //vrml.write("\n" + getGroupAsVRMLString( (Group) obj) + "\n");
          vrml.write("\n".getBytes());
          getGroup( (Group) obj, vrml);
          vrml.write("\n".getBytes());
        }
        else if (obj instanceof Leaf) {
          vrml.write("\n".getBytes());
          getLeaf( (Leaf) obj, vrml);
          vrml.write("\n".getBytes());
        }
        else {
          System.err.println("getBranchGroupAsVRMLString: don't know how to handle class: " + obj.getClass().getCanonicalName());
        }
      }
      vrml.write("\n ] }\n".getBytes());
    }

    public void getTransformGroup(TransformGroup tg, OutputStream vrml) throws Exception {
      Enumeration children = tg.getAllChildren();
      if (!children.hasMoreElements()) {
        return;
      }

      vrml.write( ("\n" + createDEF(tg, UserData.NODE_NAME) + " Transform {\n").getBytes());

      Transform3D t3d = new Transform3D();
      tg.getTransform(t3d);

      vrml.write("\n".getBytes());
      getTransform3D(t3d, vrml);
      vrml.write("\n".getBytes());

      vrml.write("\n  children [\n".getBytes());
      while (children.hasMoreElements()) {
        Object obj = children.nextElement();
        if (obj instanceof Primitive) { // This is a special case
          vrml.write("\n ".getBytes());
          getPrimitive( (Primitive) obj, vrml);
          vrml.write(" \n".getBytes());
        }
        else if (obj instanceof Group) {
          try {
            vrml.write("\n ".getBytes());
            getGroup( (Group) obj, vrml);
            vrml.write(" \n".getBytes());
          }
          catch (Exception ex) {
            System.err.println("getTransformGroup: " + ex.getMessage());
          }
        }
        else if (obj instanceof Leaf) {
          vrml.write("\n ".getBytes());
          getLeaf( (Leaf) obj, vrml);
          vrml.write(" \n".getBytes());
        }

        else {
          System.err.println("getTransformGroup: don't know how to handle class: " + obj.getClass().getCanonicalName());
        }
      }
      vrml.write("\n  ]\n".getBytes());

      vrml.write("\n}\n".getBytes());
    }

    private void getTransform3D(Transform3D t3d, OutputStream vrml) throws Exception {

      Vector3f v3f = new Vector3f();
      t3d.get(v3f);
      vrml.write( ("\n  translation " + v3f.x + " " + v3f.y + " " + v3f.z + "\n").getBytes());

      Matrix3d m = new Matrix3d();
      t3d.get(m);
      AxisAngle4d axisAngle4d = new AxisAngle4d();
      Java3dUtils.toAxisAngle(m, axisAngle4d);
      vrml.write( ("  rotation " + axisAngle4d.x + " " + axisAngle4d.y + " " + axisAngle4d.z + " " + axisAngle4d.angle + "\n").
                 getBytes());

      Vector3d v3d = new Vector3d();
      t3d.getScale(v3d);
      vrml.write( ("  scale " + v3d.x + " " + v3d.y + " " + v3d.z + "\n").getBytes());
    }

    public void getAppearance(Appearance app, OutputStream vrml) throws Exception {

      vrml.write("    appearance Appearance { # --- Start of Appearance\n".getBytes());
        try {
        Material material = app.getMaterial();
        vrml.write("      material Material {\n".getBytes());
          Color3f color = new Color3f();
        material.getAmbientColor(color);
        vrml.write( ("        ambientIntensity " + Math.sqrt(color.x * color.x + color.y * color.y + color.z * color.z) + "\n").
                   getBytes());
          material.getDiffuseColor(color);
        vrml.write( ("        diffuseColor " + color.x + " " + color.y + " " + color.z + "\n").getBytes());
          material.getEmissiveColor(color);
        vrml.write( ("        emissiveColor " + color.x + " " + color.y + " " + color.z + "\n").getBytes());
          material.getSpecularColor(color);
        vrml.write( ("        specularColor " + color.x + " " + color.y + " " + color.z + "\n").getBytes());
          vrml.write( ("        shininess " + material.getShininess() + "\n").getBytes());
        TransparencyAttributes ta = app.getTransparencyAttributes();
        float transp = 0;
        if (ta != null) {
          transp = ta.getTransparency();
        }
        vrml.write( ("        transparency " + transp + "\n").getBytes());

        vrml.write( ("      }\n").getBytes());
      }
      catch (Exception ex) {
        System.err.println("getAppearanceAsVRMLString: " + ex.getMessage());
      }

      vrml.write("    } # --- End of Appearance\n".getBytes());
    }

    private void getSwitch(Switch sw, OutputStream vrml) throws Exception {
      int which_child = 0;
      try {
        which_child = sw.getWhichChild();
      }
      catch (Exception ex) {
        System.err.println(ex.getMessage() + " : ignored...");
        return;
      }

      Object obj = null;

      switch (which_child) {
        case Switch.CHILD_NONE:
          return;

        case Switch.CHILD_ALL:
          Enumeration children = sw.getAllChildren();
          if (!children.hasMoreElements()) {
            return;
          }
          while (children.hasMoreElements()) {
            obj = children.nextElement();
            if (obj instanceof Group) {
              try {
                getGroup( (Group) obj, vrml);
              }
              catch (Exception ex) {
                System.err.println(ex.getMessage());
              }
            }
            else if (obj instanceof Leaf) {
              vrml.write("\n".getBytes());
              getLeaf( (Leaf) obj, vrml);
              vrml.write("\n".getBytes());
            }
          }
          return;

        case Switch.CHILD_MASK:
          System.err.println("getSwitchAsVRMLString: handling CHILD_MASK is not implemented yet");
          break;

        default:
          obj = sw.getChild(which_child);
          if (obj instanceof Group) {
            try {
              getGroup( (Group) obj, vrml);
              return;
            }
            catch (Exception ex) {}
          }
          else {
            System.err.println("getSwitchAsVRMLString: don't know how to handle class: " + obj.getClass().getCanonicalName());
          }

      }
      return;
    }

    public void getLeaf(Leaf leaf, OutputStream vrml) throws Exception {
      // Leaf could be   AlternateAppearance, Background, Behavior, BoundingLeaf, Clip, Fog, Light, Link, ModelClip, Morph, Shape3D, Sound, Soundscape, ViewPlatform

      Object obj = leaf;
      if (obj instanceof Shape3D) {
        getShape3D( (Shape3D) leaf, vrml);
        return;
      }
      else if (obj instanceof Fog) {
        getFog( (Fog) leaf, vrml);
        return;
      }
      else if (obj instanceof Light) {
        getLight( (Light) leaf, vrml);
        return;
      }
      else if (obj instanceof Background) {
        getBackground( (Background) leaf, vrml);
        return;
      }

      else if (obj instanceof ViewPlatform) {
        getViewPlatform(vrml);
        return;
      }

      else if (obj instanceof SwitchValueInterpolator) {
        getSwitchValueInterpolator( (SwitchValueInterpolator) obj, vrml);
        return;
      }

      else if (obj instanceof BoundingLeaf) {
        logger.info("getLeafAsVRMLString: skipping " + obj.getClass().getCanonicalName());
      }
      else if (obj instanceof Behavior) {
        logger.info("getLeafAsVRMLString: skipping " + obj.getClass().getCanonicalName());
      }

      else {
        System.err.println("getLeafAsVRMLString: don't know how to handle class: " + obj.getClass().getCanonicalName());
      }
    }

    public void getSwitchValueInterpolator(SwitchValueInterpolator svi, OutputStream vrml) throws Exception {
      if (canvas3D == null) {
        return;
      }
      Switch sw = svi.getTarget();
      Alpha alpha = svi.getAlpha();
      vrml.write("\n# --- Start of SwitchValueInterpolator\n".getBytes());

      int which_child = 0;
      try {
        which_child = sw.getWhichChild();
      }
      catch (Exception ex) {
        System.err.println(ex.getMessage() + " : ignored...");
        return;
      }

      //if ( which_child != Switch.CHILD_ALL ) sw.setWhichChild(Switch.CHILD_ALL);

      switch (dumpSwitch) {
        case ACTIVE_CHILD_ONLY:
          this.getSwitch(sw, vrml);
          break;

        case ALL:
          //String store = prefix;
          for (int i = 0; i < sw.numChildren(); i++) {
            //prefix = String.format("%03d_STR_", (i + 1)) + store;
            sw.setWhichChild(i);
            this.getSwitch(sw, vrml);
          }
          //prefix = store;
          sw.setWhichChild(which_child);
          break;
      }
      vrml.write("# --- End of SwitchValueInterpolator\n".getBytes());
    }

    public void getViewPlatform(OutputStream vrml) throws Exception {
      if (canvas3D == null) {
        return;
      }
      if (false) { // --- Skip

        vrml.write("\n          Viewpoint { # --- Start of Viewpoint\n".getBytes());

        vrml.write("          description \"Default Viewpoint\"\n".getBytes());
        vrml.write("          jump TRUE\n".getBytes());
        vrml.write("          set_bind TRUE\n".getBytes());

        View view = canvas3D.getView();
        int pPolicy = view.getProjectionPolicy();

        vrml.write( ("          fieldOfView " + view.getFieldOfView() + " # In degrees: " +
                     (view.getFieldOfView() * RADIANS_TO_DEGREES) + "\n").getBytes());

        Point3d position = new Point3d();
        canvas3D.getCenterEyeInImagePlate(position);
        Transform3D motion = new Transform3D();
        canvas3D.getImagePlateToVworld(motion);
        motion.transform(position);

        vrml.write( ("          position " + position.x + " " + position.y + " " + position.z + "\n").getBytes());

        vrml.write("          } # --- End of Viewpoint\n".getBytes());

        vrml.write("\n          NavigationInfo { # --- Start of NavigationInfo\n".getBytes());
        vrml.write("          type \"EXAMINE\"\n".getBytes());
        vrml.write("          speed 10.0\n".getBytes());
        vrml.write("          avatarSize [0.5, 0.5, 0.5]\n".getBytes());
        vrml.write("          headlight FALSE\n".getBytes());
        vrml.write("          set_bind TRUE\n".getBytes());
        vrml.write("          } # --- End of NavigationInfo\n".getBytes());
      }
    }

    public void getFog(Fog fog, OutputStream vrml) throws Exception {
      if (fog instanceof ExponentialFog) {
        System.err.println("getFogAsVRMLString: ExponentialFog is not implemented yet");
        return;
      }
      else if (fog instanceof LinearFog) {
        getLinearFog( (LinearFog) fog, vrml);
        return;
      }
      System.err.println("getFogAsVRMLString: don't know how to handle class: " + fog.getClass().getCanonicalName());
      return;
    }

    public void getLight(Light light, OutputStream vrml) throws Exception {
      if (skipLight) {
        return;
      }
      if (light instanceof AmbientLight) {
        logger.info("getLightAsVRMLString: AmbientLight is not implemented yet");
        return;
      }
      else if (light instanceof DirectionalLight) {
        getDirectionalLight( (DirectionalLight) light, vrml);
        return;
      }
      else if (light instanceof PointLight) {
        System.err.println("getLightAsVRMLString: PointLight is not implemented yet");
        return;
      }

      System.err.println("getLightAsVRMLString: don't know how to handle class: " + light.getClass().getCanonicalName());
    }

    public void getLinearFog(LinearFog fog, OutputStream vrml) throws Exception {

      Color3f color = new Color3f();
      double backDist = 0;

      vrml.write("\n          Fog { # --- Start of Linear Fog\n".getBytes());
      vrml.write("            fogType \"LINEAR\"\n".getBytes());
      try {
        fog.getColor(color);
        vrml.write( ("            color " + color.x + " " + color.y + " " + color.z + "\n").getBytes());
      }
      catch (Exception ex) {
        System.err.println("getLinearFogAsVRMLString: " + ex.getMessage());
      }
      try {
        backDist = fog.getBackDistance();
      }
      catch (Exception ex) {
        System.err.println("getLinearFogAsVRMLString: " + ex.getMessage());
      }
      vrml.write( ("            visibilityRange " + String.format("%8.3f", backDist) + "\n").getBytes());

      vrml.write("          } # --- End of Linear Fog\n".getBytes());
    }

    public void getBackground(Background bg, OutputStream vrml) throws Exception {
      Color3f color = new Color3f(0, 0, 0);
      double backDist = 0;

      vrml.write("\n          Background { # --- Start of Background\n".getBytes());
      try {
        bg.getColor(color);
      }
      catch (Exception ex) {
        System.err.println("getBackgroundAsVRMLString: " + ex.getMessage());
      }
      vrml.write( ("            skyColor " + color.x + " " + color.y + " " + color.z + "\n").getBytes());

      vrml.write("          } # --- End of Background\n".getBytes());
    }

    public void getDirectionalLight(DirectionalLight light, OutputStream vrml) throws Exception {

      boolean lightOn = true; // default
      Color3f color = new Color3f(1, 1, 1); // default
      Vector3f direction = new Vector3f(0, 0, -1); // default

      vrml.write("\n          DirectionalLight { # --- Start of DirectionalLight\n".getBytes());
      try {
        lightOn = light.getEnable();
      }
      catch (Exception ex) {
        System.err.println("getDirectionalLightAsVRMLString: " + ex.getMessage());
      }
      vrml.write("            on ".getBytes());
      if (lightOn) {
        vrml.write("TRUE\n".getBytes());
      }
      else {
        vrml.write("FALSE\n".getBytes());
      }

      try {
        light.getColor(color);
      }
      catch (Exception ex) {
        System.err.println("getDirectionalLightAsVRMLString: " + ex.getMessage());
      }
      vrml.write( ("            color " + color.x + " " + color.y + " " + color.z + "\n").getBytes());

      try {
        light.getDirection(direction);
      }
      catch (Exception ex) {
        System.err.println("getDirectionalLightAsVRMLString: " + ex.getMessage());
      }
      vrml.write( ("            direction " + direction.x + " " + direction.y + " " + direction.z + "\n").getBytes());

      vrml.write("            intensity 1.0\n".getBytes()); // Default
      vrml.write("            ambientIntensity 0.0\n".getBytes()); // Default

      vrml.write("          } # --- End of DirectionalLight\n".getBytes());
    }

    /**
     * The Shape3D leaf node specifies all geometric objects. It contains a list of one or more
     * Geometry component objects and a single Appearance component object.
     * @param shape3d Shape3D
     * @return String
     */
    public void getShape3D(Shape3D shape3d, OutputStream vrml) throws Exception {

      int n_shapes = 0;
      try {
        n_shapes = shape3d.numGeometries();
      }
      catch (Exception ex) {
        System.err.println("getShape3DAsVRMLString: " + ex.getMessage());
        return;
      }

      if (n_shapes < 1) {
        return;
      }

      //if (shape3d.getCapability(Shape3D.ALLOW_GEOMETRY_READ)) {
      //  System.err.println("getShape3DAsVRMLString: ALLOW_GEOMETRY_READ is not set");
      //  return "";
      //}

      vrml.write( ("\n" + createDEF(shape3d, UserData.NODE_NAME) + " Shape { # --- Start of Shape " +
                   createDEF(shape3d, UserData.NODE_NAME) + "\n").
                 getBytes());

      // Write down Appearance
      try {
        Appearance app = shape3d.getAppearance();
        vrml.write("\n".getBytes());
        getAppearance(app, vrml);
        vrml.write("\n".getBytes());
        //RenderingAttributes ra = app.getRenderingAttributes();
      }
      catch (Exception ex) {
        System.err.println("getShape3DAsVRMLString: " + ex.getMessage());
      }

      // --- Get polygon attributes
      int rasterizationMode = PolygonAttributes.POLYGON_FILL; // Default for java3d
      try {
        Appearance app = shape3d.getAppearance();
        PolygonAttributes pa = app.getPolygonAttributes();
        if (pa != null) {
          rasterizationMode = pa.getPolygonMode();
        }
      }
      catch (Exception ex) {
        System.err.println("getShape3DAsVRMLString: " + ex.getMessage());
      }

      // Write down geometries

      for (int i = 0; i < n_shapes; i++) {
        Geometry geometry = shape3d.getGeometry(i);
        vrml.write("\n".getBytes());
        getGeometry(geometry, rasterizationMode, vrml);
        vrml.write("\n".getBytes());
      }

      vrml.write("\n} # --- End of Shape\n".getBytes());
    }

    /**
     * Geometry is an abstract class that specifies the geometry component information required by a Shape3D node.
     * Geometry objects describe both the geometry and topology of the Shape3D nodes that reference them.
     * Geometry objects consist of four generic geometric types:
     * Compressed Geometry
     * GeometryArray
     * Raster
     * Text3D
     * @param geometry Geometry
     * @return String
     */
    public void getGeometry(Geometry geometry, int rasterizationMode, OutputStream vrml) throws Exception {
      if (geometry instanceof GeometryArray) {
        vrml.write("\n".getBytes());
        getGeometryArray( (GeometryArray) geometry, rasterizationMode, vrml);
        vrml.write("\n".getBytes());
      }
      else {
        System.err.println("getGeometryAsVRMLString: don't know how to handle class: " + geometry.getClass().getCanonicalName());
        return;
      }
    }

    /**
     * The GeometryArray object contains separate arrays of positional coordinates, colors,
     * normals, texture coordinates, and vertex attributes that describe point, line, or polygon geometry.
     * This class is extended to create the various primitive types (such as lines, triangle strips, etc.).
     * Vertex data may be passed to this geometry array in one of two ways:
     * by copying the data into the array using the existing methods, or by passing a reference to the data.
     * @param ga GeometryArray
     * @return String
     */
    public void getGeometryArray(GeometryArray ga, int rasterizationMode, OutputStream vrml) throws Exception {

      if (ga instanceof TriangleArray) {
        getTriangleArray( (TriangleArray) ga, rasterizationMode, vrml);
      }
      else if (ga instanceof GeometryStripArray) {
        getGeometryStripArray( (GeometryStripArray) ga, vrml);
      }

      else {
        System.err.println("getGeometryArrayAsVRMLString: don't know how to handle class: " + ga.getClass().getCanonicalName());
        return;
      }
    }

    public void getGeometryStripArray(GeometryStripArray gsa, OutputStream vrml) throws Exception {

      if (gsa instanceof TriangleFanArray) {
        getTriangleFanArray( (TriangleFanArray) gsa, vrml);
      }
      else if (gsa instanceof TriangleStripArray) {
        getTriangleStripArray( (TriangleStripArray) gsa, vrml);
      }

      else {
        System.err.println("getGeometryStripArrayAsVRMLString: don't know how to handle class: " + gsa.getClass().getCanonicalName());
        return;
      }
    }

    /**
     * Note that only one additional vertex is needed to draw the second triangle.
     * In OpenGL, the order in which the vertices are specified is important so that surface normals are consistent.
     * Quoted directly from the OpenGL redbook:
     *     GL_TRIANGLE_STRIP Draws a series of triangles (three-sided polygons) using vertices v0, v1, v2, then v2, v1, v3 (note the order), then v2, v3, v4, and so on. The ordering is to ensure that the triangles are all drawn with the same orientation so that the strip can correctly form part of a surface.
     *
     * @param tsa TriangleStripArray
     * @return String
     */
    public void getTriangleStripArray(TriangleStripArray tsa, OutputStream vrml) throws Exception {

      int format = 0;
      try {
        format = tsa.getVertexFormat();
      }
      catch (Exception ex) {
        System.err.println("getTriangleStripArrayAsVRMLString: " + ex.getMessage());
        format |= GeometryArray.COORDINATES;
      }

      // --- Get number of vertices

      int n_vertices = 0;
      try {
        n_vertices = tsa.getVertexCount();
      }
      catch (Exception ex) {
        System.err.println("getTriangleStripArrayAsVRMLString: " + ex.getMessage());
        return;
      }

      if (n_vertices < 1) {
        System.err.println("getTriangleStripArrayAsVRMLString: number of vertices < 1");
        return;
      }

      // --- Get coordinates, colors, and normals, if any

      double[] coordinates = this.getCoordinates(tsa);
      Color3f[] colors3f = this.getColors3f(tsa);
      Color4f[] colors4f = this.getColors4f(tsa);
      Vector3f[] normals = this.getNormals(tsa);

      // --- Create VRML

      vrml.write("\n          geometry IndexedFaceSet { # --- Start of TriangleStripArray\n".getBytes());
      vrml.write("            ccw TRUE\n".getBytes());
      vrml.write("            solid FALSE\n".getBytes());
      vrml.write("            coord Coordinate { # --- Start of Coordinates\n".getBytes());

      vrml.write("              point [\n".getBytes());
      for (int i = 0; i < n_vertices; i++) {
        if (i != 0) {
          vrml.write(",\n".getBytes());
        }
        vrml.write( ("                " +
                     String.format("%10.4f %10.4f %10.4f", coordinates[3 * i], coordinates[3 * i + 1], coordinates[3 * i + 2])).
                   getBytes());
      }
      vrml.write("\n              ]\n".getBytes());

      vrml.write("            } # --- End of Coordinates\n".getBytes());

      // --- Write triangle indices

      vrml.write("            coordIndex [ # --- Start of Triangles indices\n".getBytes());
      for (int i = 0; i < n_vertices - 2; i++) {
        if (i != 0) {
          vrml.write(",\n".getBytes());
        }
        if (i % 2 == 0) {
          vrml.write( ("              " + i + ", " + (i + 1) + ", " + (i + 2) + ", -1").getBytes());
        }
        else {
          vrml.write( ("              " + (i + 1) + ", " + i + ", " + (i + 2) + ", -1").getBytes());
        }
      }
      vrml.write("\n            ] # --- End of Triangles indices\n".getBytes());

      // --- write colors, if any

      if (writeColorsPerVertex && (colors3f != null || colors4f != null)) {
        vrml.write("            colorPerVertex TRUE\n".getBytes());
        vrml.write("            color Color {\n".getBytes());
        vrml.write("              color [\n".getBytes());
        for (int i = 0; i < n_vertices; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          if (colors3f != null) {
            vrml.write( ("                " + String.format("%7.4f %7.4f %7.4f", colors3f[i].x, colors3f[i].y, colors3f[i].z)).
                       getBytes());
          }
          else if (colors4f != null) {
            vrml.write( ("                " + String.format("%7.4f %7.4f %7.4f", colors4f[i].x, colors4f[i].y, colors4f[i].z)).
                       getBytes());
          }
        }

        vrml.write("\n              ]\n".getBytes());
        vrml.write("            }\n".getBytes());

        vrml.write("            colorIndex [\n".getBytes());
        for (int i = 0; i < n_vertices - 2; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          if (i % 2 == 0) {
            vrml.write( ("              " + i + ", " + (i + 1) + ", " + (i + 2) + ", -1").getBytes());
          }
          else {
            vrml.write( ("              " + (i + 1) + ", " + i + ", " + (i + 2) + ", -1").getBytes());
          }

        }
        vrml.write("\n            ]\n".getBytes());
      }
      else {
        vrml.write("            colorPerVertex FALSE\n".getBytes());
      }

      // --- write normals, if any

      if (normals != null) {
        vrml.write("            normalPerVertex TRUE\n".getBytes());
        vrml.write("            normal Normal {\n".getBytes());
        vrml.write("              vector [\n".getBytes());
        for (int i = 0; i < n_vertices; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          vrml.write( ("                " + String.format("%7.4f %7.4f %7.4f", normals[i].x, normals[i].y, normals[i].z)).getBytes());
        }

        vrml.write("\n              ]\n".getBytes());
        vrml.write("            }\n".getBytes());

        vrml.write("            normalIndex [\n".getBytes());
        for (int i = 0; i < n_vertices - 2; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          if (i % 2 == 0) {
            vrml.write( ("              " + i + ", " + (i + 1) + ", " + (i + 2) + ", -1").getBytes());
          }
          else {
            vrml.write( ("              " + (i + 1) + ", " + i + ", " + (i + 2) + ", -1").getBytes());
          }

        }
        vrml.write( ("\n            ]\n").getBytes());

      }
      else {
        vrml.write("            normalPerVertex FALSE\n".getBytes());
      }

      vrml.write("\n          } # --- End of TriangleStripArray\n".getBytes());
    }

    /**
     * A triangle fan is a primitive in 3D computer graphics that saves on storage and processing time.
     * It describes a set of connected triangles that share one central vertex.
     * If N is the number of triangles in the fan, the number of vertices describing it is N+2.
     * This is a considerable improvement over the 3N vertices that are necessary to describe the triangles separately.
     * @param tfa TriangleFanArray
     * @return String
     */
    private void getTriangleFanArray(TriangleFanArray tfa, OutputStream vrml) throws Exception {
      int format = 0;
      try {
        format = tfa.getVertexFormat();
      }
      catch (Exception ex) {
        System.err.println("getTriangleFanArrayAsVRMLString: " + ex.getMessage());
        format |= GeometryArray.COORDINATES;
      }

      // --- Get number of vertices

      int n_vertices = 0;
      try {
        n_vertices = tfa.getVertexCount();
      }
      catch (Exception ex) {
        System.err.println("getTriangleFanArrayAsVRMLString: " + ex.getMessage());
        return;
      }

      if (n_vertices < 1) {
        System.err.println("getTriangleFanArrayAsVRMLString: number of vertices < 1");
        return;
      }

      // --- Get coordinates, colors, and normals, if any

      double[] coordinates = this.getCoordinates(tfa);
      Color3f[] colors3f = this.getColors3f(tfa);
      Color4f[] colors4f = this.getColors4f(tfa);
      Vector3f[] normals = this.getNormals(tfa);

      // --- Create VRML

      vrml.write("\n          geometry IndexedFaceSet { # --- Start of TriangleFanArray\n".getBytes());
      vrml.write("            ccw TRUE\n".getBytes());
      vrml.write("            solid FALSE\n".getBytes());
      vrml.write("            coord Coordinate { # --- Start of Coordinates\n".getBytes());

      vrml.write("              point [\n".getBytes());
      for (int i = 0; i < n_vertices; i++) {
        if (i != 0) {
          vrml.write(",\n".getBytes());
        }
        vrml.write( ("                " +
                     String.format("%10.4f %10.4f %10.4f", coordinates[3 * i], coordinates[3 * i + 1], coordinates[3 * i + 2])).
                   getBytes());
      }
      vrml.write("\n              ]\n".getBytes());

      vrml.write("            } # --- End of Coordinates\n".getBytes());

      // --- Write triangle indices

      vrml.write("            coordIndex [ # --- Start of Triangles indices\n".getBytes());
      for (int i = 0; i < n_vertices - 2; i++) {
        if (i != 0) {
          vrml.write(",\n".getBytes());
        }
        vrml.write( ("              " + 0 + ", " + (i + 1) + ", " + (i + 2) + ", -1").getBytes());
      }
      vrml.write("\n            ] # --- End of Triangles indices\n".getBytes());

// --- write colors, if any

      if (writeColorsPerVertex && (colors3f != null || colors4f != null)) {
        vrml.write("            colorPerVertex TRUE\n".getBytes());
        vrml.write("            color Color {\n".getBytes());
        vrml.write("              color [\n".getBytes());
        for (int i = 0; i < n_vertices; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          if (colors3f != null) {
            vrml.write( ("                " + String.format("%7.4f %7.4f %7.4f", colors3f[i].x, colors3f[i].y, colors3f[i].z)).
                       getBytes());
          }
          else if (colors4f != null) {
            vrml.write( ("                " + String.format("%7.4f %7.4f %7.4f", colors4f[i].x, colors4f[i].y, colors4f[i].z)).
                       getBytes());
          }
        }

        vrml.write("\n              ]\n".getBytes());
        vrml.write("            }\n".getBytes());

        vrml.write("            colorIndex [\n".getBytes());
        for (int i = 0; i < n_vertices - 2; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          vrml.write( ("              " + 0 + ", " + (i + 1) + ", " + (i + 2) + ", -1").getBytes());
        }
        vrml.write("\n            ]\n".getBytes());
      }
      else {
        vrml.write("            colorPerVertex FALSE\n".getBytes());
      }

// --- write normals, if any

      if (normals != null) {
        vrml.write("            normalPerVertex TRUE\n".getBytes());
        vrml.write("            normal Normal {\n".getBytes());
        vrml.write("              vector [\n".getBytes());
        for (int i = 0; i < n_vertices; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          vrml.write( ("                " + String.format("%7.4f %7.4f %7.4f", normals[i].x, normals[i].y, normals[i].z)).getBytes());
        }

        vrml.write("\n              ]\n".getBytes());
        vrml.write("            }\n".getBytes());

        vrml.write("            normalIndex [\n".getBytes());
        for (int i = 0; i < n_vertices - 2; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          vrml.write( ("              " + 0 + ", " + (i + 1) + ", " + (i + 2) + ", -1").getBytes());
        }
        vrml.write("\n            ]\n".getBytes());

      }
      else {
        vrml.write("            normalPerVertex FALSE\n".getBytes());
      }

      vrml.write("\n          } # --- End of TriangleFanArray\n".getBytes());
    }

    /**
     * The TriangleArray object draws the array of vertices as individual triangles. Each group of three vertices defines a triangle to be drawn.
     * @param ta TriangleArray
     * @return String
     */
    public void getTriangleArray(TriangleArray ta, int rasterizationMode, OutputStream vrml) throws Exception {

      int format = 0;
      try {
        format = ta.getVertexFormat();
      }
      catch (Exception ex) {
        System.err.println("getTriangleArrayAsVRMLString: " + ex.getMessage());
        format |= GeometryArray.COORDINATES;
      }

      // --- Get number of vertices

      int n_vertices = 0;
      try {
        n_vertices = ta.getVertexCount();
      }
      catch (Exception ex) {
        System.err.println("getTriangleArrayAsVRMLString: " + ex.getMessage());
        return;
      }

      if (n_vertices < 1) {
        System.err.println("getTriangleArrayAsVRMLString: number of vertices < 1");
        return;
      }

      // --- Get coordinates, colors, and normals, if any

      double[] coordinates = this.getCoordinates(ta);
      Color3f[] colors3f = this.getColors3f(ta);
      Color4f[] colors4f = this.getColors4f(ta);
      Vector3f[] normals = this.getNormals(ta);

      if (rasterizationMode != PolygonAttributes.POLYGON_LINE && rasterizationMode != PolygonAttributes.POLYGON_POINT) {
        rasterizationMode = PolygonAttributes.POLYGON_FILL; // Set to the default value
      }

      // --- Create VRML

      if (rasterizationMode == PolygonAttributes.POLYGON_FILL) {
        vrml.write("\n          geometry IndexedFaceSet { # --- Start of geometry\n".getBytes());
        vrml.write("            ccw TRUE\n".getBytes());
        vrml.write("            solid FALSE\n".getBytes());
      }
      else if (rasterizationMode == PolygonAttributes.POLYGON_LINE) {
        vrml.write("\n          geometry IndexedLineSet { # --- Start of geometry\n".getBytes());
      }
      else if (rasterizationMode == PolygonAttributes.POLYGON_POINT) {
        vrml.write("\n          geometry PointSet { # --- Start of geometry\n".getBytes());
      }

      vrml.write("            coord Coordinate { # --- Start of Coordinates\n".getBytes());

      vrml.write("              point [\n".getBytes());
      for (int i = 0; i < n_vertices; i++) {
        if (i != 0) {
          vrml.write(",\n".getBytes());
        }
        vrml.write( ("                " +
                     String.format("%10.4f %10.4f %10.4f", coordinates[3 * i], coordinates[3 * i + 1], coordinates[3 * i + 2])).
                   getBytes());
      }
      vrml.write("\n              ]\n".getBytes());

      vrml.write("            } # --- End of Coordinates\n".getBytes());

      // --- Write triangle indices

      if (rasterizationMode != PolygonAttributes.POLYGON_POINT) {
        vrml.write("            coordIndex [ # --- Start of Triangles indices\n".getBytes());
        for (int i = 0; i < n_vertices / 3; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          vrml.write( ("              " + (3 * i) + ", " + (3 * i + 1) + ", " + (3 * i + 2) + ", -1").getBytes());
        }
        vrml.write("\n            ] # --- End of Triangles indices\n".getBytes());
      }

      // --- write colors, if any

      if (writeColorsPerVertex && (colors3f != null || colors4f != null)) {
        vrml.write("            colorPerVertex TRUE\n".getBytes());
        vrml.write("            color Color {\n".getBytes());
        vrml.write("              color [\n".getBytes());
        for (int i = 0; i < n_vertices; i++) {
          if (i != 0) {
            vrml.write(",\n".getBytes());
          }
          if (colors3f != null) {
            vrml.write( ("                " + String.format("%7.4f %7.4f %7.4f", colors3f[i].x, colors3f[i].y, colors3f[i].z)).
                       getBytes());
          }
          else if (colors4f != null) {
            vrml.write( ("                " + String.format("%7.4f %7.4f %7.4f", colors4f[i].x, colors4f[i].y, colors4f[i].z)).
                       getBytes());
          }
        }

        vrml.write("\n              ]\n".getBytes());
        vrml.write("            }\n".getBytes());

        if (rasterizationMode != PolygonAttributes.POLYGON_POINT) {
          vrml.write("            colorIndex [\n".getBytes());
          for (int i = 0; i < n_vertices / 3; i++) {
            if (i != 0) {
              vrml.write(",\n".getBytes());
            }
            vrml.write( ("              " + (3 * i) + ", " + (3 * i + 1) + ", " + (3 * i + 2) + ", -1").getBytes());
          }
          vrml.write("\n            ]\n".getBytes());
        }
        else {
          vrml.write("            colorPerVertex FALSE\n".getBytes());
        }
      }

      // --- write normals, if any

      if (rasterizationMode == PolygonAttributes.POLYGON_FILL) {
        if (normals != null) {
          vrml.write("            normalPerVertex TRUE\n".getBytes());
          vrml.write("            normal Normal {\n".getBytes());
          vrml.write("              vector [\n".getBytes());
          for (int i = 0; i < n_vertices; i++) {
            if (i != 0) {
              vrml.write(",\n".getBytes());
            }
            vrml.write( ("                " + String.format("%7.4f %7.4f %7.4f", normals[i].x, normals[i].y, normals[i].z)).
                       getBytes());
          }

          vrml.write("\n              ]\n".getBytes());
          vrml.write("            }\n".getBytes());

          vrml.write("            normalIndex [\n".getBytes());
          for (int i = 0; i < n_vertices / 3; i++) {
            if (i != 0) {
              vrml.write(",\n".getBytes());
            }
            vrml.write( ("              " + (3 * i) + ", " + (3 * i + 1) + ", " + (3 * i + 2) + ", -1").getBytes());
          }
          vrml.write("\n            ]\n".getBytes());

        }
        else {
          vrml.write("            normalPerVertex FALSE\n".getBytes());
        }
      }

      vrml.write("\n          } # --- End of geometry\n".getBytes());
    }

    private double[] getCoordinates(GeometryArray ga) {
      int format = 0;
      try {
        format = ga.getVertexFormat();
      }
      catch (Exception ex) {
        System.err.println("getCoordinates: " + ex.getMessage());
        format |= GeometryArray.COORDINATES;
      }

      int n_vertices = 0;
      try {
        n_vertices = ga.getVertexCount();
      }
      catch (Exception ex) {
        System.err.println("getCoordinates: " + ex.getMessage());
        return null;
      }

// --- Get coordinates

      double[] coordinates = new double[3 * n_vertices];

      try {
        ga.getCoordinates(0, coordinates);
      }
      catch (Exception ex) {
        System.err.println("getCoordinates: " + ex.getMessage());
        return null;
      }
      return coordinates;
    }

    private Color3f[] getColors3f(GeometryArray ga) {
      int format = 0;
      try {
        format = ga.getVertexFormat();
      }
      catch (Exception ex) {
        System.err.println("getColors3f: " + ex.getMessage());
        return null;
      }

      int n_vertices = 0;
      try {
        n_vertices = ga.getVertexCount();
      }
      catch (Exception ex) {
        System.err.println("getColors3f: " + ex.getMessage());
        return null;
      }
      Color3f[] colors3f = null;
      if ( (format & TriangleArray.COLOR_3) == TriangleArray.COLOR_3) {
        colors3f = new Color3f[n_vertices];
        for (int i = 0; i < n_vertices; i++) {
          colors3f[i] = new Color3f();
        }
        try {
          ga.getColors(0, colors3f);
        }
        catch (Exception ex) {
          System.err.println("getColors3f: " + ex.getMessage());
          colors3f = null;
        }
      }
      return colors3f;
    }

    private Color4f[] getColors4f(GeometryArray ga) {
      int format = 0;
      try {
        format = ga.getVertexFormat();
      }
      catch (Exception ex) {
        System.err.println("getColors4f: " + ex.getMessage());
        return null;
      }

      int n_vertices = 0;
      try {
        n_vertices = ga.getVertexCount();
      }
      catch (Exception ex) {
        System.err.println("getColors4f: " + ex.getMessage());
        return null;
      }
      Color4f[] colors4f = null;
      if ( (format & TriangleArray.COLOR_4) == TriangleArray.COLOR_4) {
        colors4f = new Color4f[n_vertices];
        for (int i = 0; i < n_vertices; i++) {
          colors4f[i] = new Color4f();
        }

        try {
          ga.getColors(0, colors4f);
        }
        catch (Exception ex) {
          System.err.println("getColors4f: " + ex.getMessage());
          colors4f = null;
        }
      }
      return colors4f;
    }

    private Vector3f[] getNormals(GeometryArray ga) {
      int format = 0;
      try {
        format = ga.getVertexFormat();
      }
      catch (Exception ex) {
        System.err.println("getNormals: " + ex.getMessage());
        return null;
      }

      int n_vertices = 0;
      try {
        n_vertices = ga.getVertexCount();
      }
      catch (Exception ex) {
        System.err.println("getNormals: " + ex.getMessage());
        return null;
      }

      // --- Get normals, if any
      Vector3f[] normals = null;
      if ( (format & TriangleArray.NORMALS) == TriangleArray.NORMALS) {
        normals = new Vector3f[n_vertices];
        for (int i = 0; i < n_vertices; i++) {
          normals[i] = new Vector3f();
        }

        try {
          ga.getNormals(0, normals);
        }
        catch (Exception ex) {
          System.err.println("getNormals: " + ex.getMessage());
          normals = null;
        }
      }
      return normals;
    }

    public void saveVRMLFile() throws Exception {
      if (chooser == null) {
        chooser = new JFileChooser();
        filter = new FileFilterImpl();

        String temp[] = {
            "wrl", "WRL"}; // extensions.split(";");
        for (int i = 0; i < temp.length; i++) {
          filter.addExtension(temp[i]);
        }
        filter.setDescription("VRML Files (*.wrl)");
        chooser.setFileFilter(filter);
      }

      chooser.setDialogTitle("Save as VRML File");
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);

      if (currentWorkingDirectory != null) {
        chooser.setCurrentDirectory(currentWorkingDirectory);
      }
      else {
        String lastPWD = prefs.get(lastPWDKey, "");
        if (lastPWD.length() > 0) {
          currentWorkingDirectory = new File(lastPWD);
          if (currentWorkingDirectory.isDirectory() &&
              currentWorkingDirectory.exists()) {
            chooser.setCurrentDirectory(currentWorkingDirectory);
          }
        }
      }

      int returnVal = JFileChooser.CANCEL_OPTION;

      returnVal = chooser.showSaveDialog(null);

      if (returnVal == JFileChooser.CANCEL_OPTION) {
        return;
      }

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        String fileName = chooser.getSelectedFile().getPath();
        if (fileName.endsWith(".")) {
          fileName += "wrl";
        }
        else if (!fileName.endsWith(".wrl") && !fileName.endsWith(".WRL")) {
          fileName += ".wrl";
        }
        currentWorkingDirectory = chooser.getCurrentDirectory();
        try {
          prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
        }
        catch (Exception ex) {
          System.err.println("Cannot save cwd: " + ex.getMessage() +
                             " Ignored...");
        }
        //logger.info("You chose to open this file: " +
        //                   fileName);

        saveVRMLFile(fileName);
      }
    }

    public void saveVRMLFile(String filename) throws Exception {
      FileOutputStream out;
      try {
        out = new FileOutputStream(filename);

        out.write("#VRML V2.0 utf8\n".getBytes());
        out.write("# --- File is generated by Jamberoo\n".getBytes());
        out.write("\n".getBytes());

        if (locale != null) {
          getLocale(locale, out);
          //out.write(this.getLocaleAsVRMLString(locale).getBytes());
        }
        else if (node != null) {
          getNode(node, out);
        }
        else {
          throw new Exception("Neither Locale nor Node is set");
        }

        out.close();
        Runtime.getRuntime().gc();
      }

      catch (IOException e) {
        throw e;
      }

    }

    String createDEF(SceneGraphObject sgo, String key) {
      String pref = "";
      if (prefix != null && prefix.length() > 0) {
        pref = prefix + "_";
      }
      String def = pref + UserData.getUserDataAsString(sgo, key);
      if (def.length() > 0) {
        def = "DEF " + def + " ";
      }
      return def;
    }

  }
