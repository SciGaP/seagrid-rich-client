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

import cct.GlobalSettings;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.MediaTracker;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.scijava.java3d.AmbientLight;
import org.scijava.java3d.Appearance;
import org.scijava.java3d.Background;
import org.scijava.java3d.BoundingLeaf;
import org.scijava.java3d.BoundingSphere;
import org.scijava.java3d.Bounds;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.Canvas3D;
import org.scijava.java3d.ColoringAttributes;
import org.scijava.java3d.DirectionalLight;
import org.scijava.java3d.Group;
import org.scijava.java3d.LinearFog;
import org.scijava.java3d.Locale;
import org.scijava.java3d.Material;
import org.scijava.java3d.Node;
import org.scijava.java3d.PhysicalBody;
import org.scijava.java3d.PhysicalEnvironment;
import org.scijava.java3d.PolygonAttributes;
import org.scijava.java3d.Screen3D;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.Switch;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.java3d.View;
import org.scijava.java3d.ViewPlatform;
import org.scijava.java3d.VirtualUniverse;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Matrix3d;
import org.scijava.vecmath.Point3d;
import org.scijava.vecmath.Point3f;
import org.scijava.vecmath.Vector3d;
import org.scijava.vecmath.Vector3f;

import cct.awtdialogs.AddNewMoleculeDialog;
import cct.awtdialogs.AddNewStructureDialog;
import cct.awtdialogs.AtomLabelsDialog;
import cct.awtdialogs.AtomTypeDialog;
import cct.awtdialogs.ConnectSQLServer;
import cct.awtdialogs.GeometrySelectDialog;
import cct.awtdialogs.MessageWindow;
import cct.awtdialogs.TextEntryDialog;
import cct.awtdialogs.YesNoDialog;
import cct.cprocessor.CommandInterface;
import cct.cprocessor.Variable;
import cct.database.ChemistryDatabaseDialog;
import cct.database.SQLChemistryDatabase;
import cct.database.SQLDatabaseAccess;
import cct.database.new_ChemistryDatabaseDialog;
import cct.database.new_SQLChemistryDatabase;
import cct.dialogs.JAddMoleculeDialog;
import cct.dialogs.JAtomTypeDialog;
import cct.dialogs.JChoiceDialog;
import cct.dialogs.JModifyAngleDialog;
import cct.dialogs.JModifyAtomDialog;
import cct.dialogs.JModifyBondDialog;
import cct.dialogs.JModifyTorsionDialog;
import cct.dialogs.JSelectTreeDialog;
import cct.dialogs.JSolvateShellDialog;
import cct.gaussian.Gaussian;
import cct.gaussian.GaussianFragment;
import cct.gaussian.GaussianJob;
import cct.gaussian.GaussianOutput;
import cct.gaussian.ui.GaussianInputEditorFrame;
import cct.gromacs.GromacsParserFactory;
import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.GraphicsObjectInterface;
import cct.interfaces.GraphicsRendererInterface;
import cct.interfaces.HelperInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.MoleculeRendererInterface;
import cct.interfaces.MonomerInterface;
import cct.interfaces.Point3fInterface;
import cct.interfaces.TreeSelectorInterface;
import cct.mdl.MDLMol;
import cct.modelling.*;
import cct.pdb.PDB;
import cct.sff.SimpleForceField;
import cct.tools.CCTParser;
import cct.tools.FragmentDictionaryParser;
import cct.tools.SimpleParserFactory;
import cct.tools.Utils;
import cct.tools.XMolXYZ;
import cct.tools.ui.DoubleSpinnerDialog;
import cct.tools.ui.JShowText;
import cct.tools.ui.JobProgressInterface;
import cct.tripos.TriposParser;
import cct.vecmath.Geometry3d;
import cct.vecmath.Segment3f;

import org.scijava.java3d.utils.behaviors.mouse.MouseBehavior;
import org.scijava.java3d.utils.behaviors.mouse.MouseTranslate;
import org.scijava.java3d.utils.behaviors.mouse.MouseZoom;
import org.scijava.java3d.utils.geometry.Cylinder;
import org.scijava.java3d.utils.geometry.Primitive;
import org.scijava.java3d.utils.geometry.Sphere;
import org.scijava.java3d.utils.geometry.Text2D;
import org.scijava.java3d.utils.picking.PickResult;
import org.scijava.java3d.utils.universe.SimpleUniverse;
import java.util.List;
import java.util.logging.Level;

import static cct.vecmath.Point3f.*;

/**
 *
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p> Some conventions: Atomic sphere: BranchGroup -> Transform Group (move) -> TransformGroup (scale
 * radius) -> Sphere
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class Java3dUniverse
    extends MolecularEditor implements MolecularProperties,
    OperationsOnAtoms, GraphicsRendererInterface, TreeSelectorInterface,
    ActionListener, MoleculeRendererInterface {

  static double RADS_TO_DEGS = 180.00 / Math.PI;
  static final Logger logger = Logger.getLogger(Java3dUniverse.class.getCanonicalName());
  static Map renderStyles = new LinkedHashMap<String, Integer>();

  static {
    renderStyles.put("Wireframe", MoleculeInterface.WIREFRAME);
    renderStyles.put("Sticks", MoleculeInterface.STICKS);
    renderStyles.put("Ball & Sticks", MoleculeInterface.BALL_AND_STICKS);
    renderStyles.put("Spacefill", MoleculeInterface.SPACEFILL);
  }
  public static final String ATOM_NODE = "_AtomNode";
  public static final String BOND_NODE = "_BondNode";
  public static final String ATOM_NODE_MATERIAL_COPY = "_ANMMatCopy";
  public static final String PERSPECTIVE_PROJECTION = "Perspective";
  public static final String PARALLEL_PROJECTION = "Parallel";
  private boolean debug = false;
  HelperInterface helper = null;
  private List<RenderingListener> renderingListeners = new ArrayList<RenderingListener>();
  //SampleFrame parentFrame = null; // TODO to remove lately...
  Frame guiFrame = null; // TODO to remove lately...
  int renderingStyle = MoleculeInterface.BALL_AND_STICKS;
  private VirtualUniverse universe;
  private Locale locale;
  private View view;
  private int projectionPolicy = View.PERSPECTIVE_PROJECTION;
  private GraphicsConfiguration config;
  private Background bgNode;
  Canvas3D canvas3D;
  OffScreenCanvas3D offScreenCanvas3D;
  BranchGroup scene;
  Transform3D rotate;
  Transform3D translate;
  TransformGroup objRotate;
  TransformGroup sceneTrans;
  TransformGroup moleculeTrans = new TransformGroup();
  TransformGroup sceneOffset;
  Switch sceneSwitch;
  private ProcessMouseRotate behavior;
  private MouseZoom myMouseZoom;
  private Appearance atomAppear;
  private BranchGroup fastGroup;
  private BranchGroup niceGroup;
  private ViewPlatform vp = new ViewPlatform();
  private BranchGroup root = null;
  private TransformGroup rootTrans = null;
  private BranchGroup localeRoot = null;
  private BoundingSphere bounds = null;
  private LinearFog fog = null;
  Java3dUniverse otherRenderer = null;
  // --- Fragments related
  String fragContext = null;
  URL fragmentDictionary = null;
  Map fragmentsReferences = new HashMap();
  String solventContext = null;
  URL solventDictionary = null;
  boolean HighlightSelectedAtoms = false;
  boolean atomInfoPopup = true;
  MyMouseMotionListener tracer = new MyMouseMotionListener(this);
  //BranchGroup mol = new BranchGroup();
  MoleculeNode mol = new MoleculeNode();
  List<BondNode> bondNodes = new ArrayList<BondNode>();
  public String selectionTypes[] = {
    "Atom", "Monomer", "Molecule"
  };
  public String selectionRules[] = {
    "Union", "Difference", "Intersection"
  };
  List<GraphicsObjectInterface> graphicsObjects = null;
  Color3f atomLabelsColor = new Color3f(Color.PINK);
  int atomLabelsFontSize = 75;
  int atomLabelsFontStyle = Font.ITALIC;
  String atomLabelsFontName = "Helvetica";
  Font atomLabelsFont = null;
  List atomLabels = new ArrayList();
  boolean enableLabels = false;
  boolean otherLabelMethod = true; // For tesing
  AtomLabelsDialog atomLabelsDialog = null;
  Text2D referenceText2D = new Text2D("", atomLabelsColor, atomLabelsFontName,
      atomLabelsFontSize, atomLabelsFontStyle);
  int bondRenderingStyle = BondInterface.CYLINDER_BICOLOR;
  public int selectedRule = 0;
  public boolean selectionInProgress = false;
  MousePicking mouse_picking_behavior = null;
  public Object oneAtomSelectionDialog = null;
  public GeometrySelectDialog geometrySelectioDialog = null;
  JSelectTreeDialog selectDefaultSolvent = null;
  public JModifyAtomDialog jModifyAtomDialog = null;
  public JModifyBondDialog jModifyBondDialog = null;
  public JModifyAngleDialog jModifyAngleDialog = null;
  public JAddMoleculeDialog jAddMoleculeDialog = null;
  JSolvateShellDialog jSolvateShellDialog = null;
  Map defaultSolventList = null;
  public Object chooseAtomType = null;
  GaussianInputEditorFrame gaussianInputEditorFrame = null;
  public int lastSelectedAtom = -1;
  private List selectedAtoms = new ArrayList();
  private List<AtomInterface> trackSelectedAtoms = new ArrayList<AtomInterface>();
  //private java.util.Set<AtomInterface> trackSelectedAtoms = new HashSet<AtomInterface>();
  protected List Geometries = null;
  protected int selectedGeometry = 0;
// --- Menu Items
  public MenuItem geometrySelection = null;
// --- Working structures
  Matrix3d temp_Matrix = null;
  Sphere temp_Sphere = null;
  List temp_Group_1 = null, temp_Group_2 = null, temp_Group_3 = null;
  cct.vecmath.Point3f temp_axis = null;
  cct.vecmath.Point3f temp_structure[] = null; //

  //public SampleFrame(String title, String [] args) {
  //     new SampleFrame(title);
  //     startBulkDatabaseLoad(args);
  //}

  /*
   * public Java3dUniverse(SampleFrame parent) { this(); parentFrame = parent;
   *
   * }
   */
  public Java3dUniverse() {

    config = SimpleUniverse.getPreferredConfiguration();
    if (config == null) {
      System.err.println(config);
      return;
    }
    canvas3D = new Canvas3D(config);
    //logger.info("Java3dUniverse: " + canvas3D);
    if (canvas3D == null) {
      logger.info(canvas3D.toString());
      return;
    }

    if (atomInfoPopup) {
      canvas3D.addMouseMotionListener(tracer);
    }

    offScreenCanvas3D = new OffScreenCanvas3D(config, true);

    //add("Center", canvas3D); // To be returned
    createUniverse();

    initSceneGraph();
    //initSceneGraph(universe);

    initLighting();

    moleculeTrans.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    moleculeTrans.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
    moleculeTrans.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
    moleculeTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    moleculeTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    niceGroup.addChild(moleculeTrans);

    root.compile();
    localeRoot.addChild(root);
    try {
      locale.addBranchGraph(localeRoot);
      //simpleU.addBranchGraph(localeRoot);
    } catch (Exception ex) {
      //ex.printStackTrace();
    }

    try {
      jbInit();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    //mol.setCapability(BranchGroup.ALLOW_DETACH);
    moleculeTrans.addChild(mol);
    UserData.setUserData(moleculeTrans, UserData.NODE_NAME, "Molecule");
    //moleculeTrans.setName("Molecule"); // --- >1.4 java3d feature
  }

  public View getView() {
    return view;
  }

  public void enableMousePicking(boolean flag) {
    if (mouse_picking_behavior == null) {
      return;
    }
    mouse_picking_behavior.setEnable(flag);
    selectionInProgress = flag;
    logger.info("Mouse picking: " + Boolean.toString(flag));
  }

  public boolean getMousePickingStatus() {
    if (mouse_picking_behavior == null) {
      return false;
    }
    return mouse_picking_behavior.getEnable();
  }

  public void setOtherRenderer(Java3dUniverse otherJ3D) {
    otherRenderer = otherJ3D;
  }

  public Java3dUniverse getOtherRenderer() {
    return otherRenderer;
  }

  /**
   * Add listener for change rendering style events
   *
   * @param listener RenderingListener - listener
   */
  public void addRenderingListener(RenderingListener listener) {
    if (renderingListeners.contains(listener)) {
      return;
    }
    renderingListeners.add(listener);
  }

  public void removeRenderingListener(RenderingListener listener) {
    if (renderingListeners.contains(listener)) {
      renderingListeners.remove(listener);
    }
  }

  void createUniverse() {
    // Establish a virtual universe, with a single hi-res Locale
    universe = new VirtualUniverse();
    //simpleU = new SimpleUniverse(canvas3D);
    locale = new Locale(universe);
    //locale = new Locale(simpleU);

    Screen3D sOn = canvas3D.getScreen3D();
    Screen3D sOff = offScreenCanvas3D.getScreen3D();
    Dimension dim = sOn.getSize();
    dim.width *= 3;
    dim.height *= 3;
    sOff.setSize(dim);
    sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth() * 3);
    sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight() * 3);

    // Create a PhysicalBody and Physical Environment object
    PhysicalBody body = new PhysicalBody();
    PhysicalEnvironment environment = new PhysicalEnvironment();

    // Create a View and attach the Canvas3D and the physical
    // body and environment to the view.
    view = new View();
    //View view = simpleU.getViewer().getView();
    view.addCanvas3D(canvas3D);
    view.setPhysicalBody(body);
    view.setPhysicalEnvironment(environment);
    view.setBackClipDistance(500.0);
    setProjection(projectionPolicy);
    //view.setProjectionPolicy(projectionPolicy);

    //simpleU.getViewer().getView().setPhysicalBody(body);
    //simpleU.getViewer().getView().setPhysicalEnvironment(environment);
    //simpleU.getViewer().getView().setBackClipDistance(500.0);
    //simpleU.getViewer().getView().addCanvas3D(offScreenCanvas3D);
    view.addCanvas3D(offScreenCanvas3D);

    //logger.info("Clip front/back: "+view.getFrontClipDistance()+"/"+
    //			 view.getBackClipDistance());
    // Create a branch group node for the view platform
    root = new BranchGroup();
    root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    root.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    root.setCapability(BranchGroup.ALLOW_DETACH);
    root.setCapability(BranchGroup.ALLOW_COLLISION_BOUNDS_READ);
    root.setCapability(BranchGroup.ALLOW_COLLISION_BOUNDS_WRITE);

    localeRoot = new BranchGroup();
    localeRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    localeRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    localeRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    localeRoot.setCapability(BranchGroup.ALLOW_DETACH);

    vp.setCapability(ViewPlatform.ALLOW_POLICY_READ);
    vp.setCapability(ViewPlatform.ALLOW_POLICY_WRITE);
    vp.setViewAttachPolicy(View.NOMINAL_SCREEN);
    vp.setActivationRadius(10);

    rootTrans = new TransformGroup();
    rootTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    bounds
        = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);

    /*
     * Does not work Background bg = new Background(new Color3f(Color.BLUE)); bg.setBounds(bounds); rootTrans.addChild(bg);
     */
    rootTrans.addChild(vp);
    BoundingLeaf boundingLeaf = new BoundingLeaf(bounds);
    rootTrans.addChild(boundingLeaf);
    localeRoot.addChild(rootTrans);
    //simpleU.getViewer().getView().attachViewPlatform(vp);
    view.attachViewPlatform(vp);

    // Set up the background
    Color3f bgColor = new Color3f(0.0f, 0.0f, 0.0f);
    bgNode = new Background(bgColor);
    bgNode.setApplicationBounds(bounds);
    bgNode.setCapability(Background.ALLOW_COLOR_READ);
    bgNode.setCapability(Background.ALLOW_COLOR_WRITE);
    root.addChild(bgNode);

  }

  public BoundingSphere getBoundingSphere() {
    return bounds;
  }

  /**
   * Sets projection policy
   *
   * @param mode int - either View.PERSPECTIVE_PROJECTION or View.PARALLEL_PROJECTION. Do nothing if mode != one of this
   * values
   */
  public void setProjection(int mode) {
    switch (mode) {
      case View.PERSPECTIVE_PROJECTION:
        view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
        break;
      case View.PARALLEL_PROJECTION:
        view.setProjectionPolicy(View.PARALLEL_PROJECTION);
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        view.setScreenScale(0.05);
        break;
      default:
        logger.warning("Projection mode should be equal to either View.PERSPECTIVE_PROJECTION or View.PARALLEL_PROJECTION");
    }
  }

  public void setProjection(String mode) {
    if (mode.equalsIgnoreCase(PERSPECTIVE_PROJECTION)) {
      setProjection(View.PERSPECTIVE_PROJECTION);
    } else if (mode.equalsIgnoreCase(PARALLEL_PROJECTION)) {
      setProjection(View.PARALLEL_PROJECTION);
    }
  }

  public int getProjectionMode() {
    return view.getProjectionPolicy();
  }

  public String getProjectionModeAsString() {
    if (view.getProjectionPolicy() == View.PERSPECTIVE_PROJECTION) {
      return PERSPECTIVE_PROJECTION;
    } else {
      return PARALLEL_PROJECTION;
    }
  }

  public String[] getProjectionModes() {
    return new String[]{
      PERSPECTIVE_PROJECTION, PARALLEL_PROJECTION};
  }

  public void setBackgroundColor(Color3f color) {
    if (color == null) {
      logger.warning("Background color cannot be null");
      return;
    }
    if (bgNode.getCapability(Background.ALLOW_COLOR_WRITE)) {
      bgNode.setColor(color);
    } else {
      System.err.println("No capability to set background color");
    }
  }

  public void setBackgroundColor(float red, float green, float blue) {
    red = red < 0 ? 0 : red > 1 ? 1 : red;
    green = green < 0 ? 0 : green > 1 ? 1 : green;
    blue = blue < 0 ? 0 : blue > 1 ? 1 : blue;
    if (bgNode.getCapability(Background.ALLOW_COLOR_WRITE)) {
      bgNode.setColor(red, green, blue);
    } else {
      logger.severe("No capability to set background color");
    }
  }

  public void setBackgroundColor(int red, int green, int blue) {
    red = red < 0 ? 0 : red > 1 ? 1 : red;
    green = green < 0 ? 0 : green > 1 ? 1 : green;
    blue = blue < 0 ? 0 : blue > 1 ? 1 : blue;
    if (bgNode.getCapability(Background.ALLOW_COLOR_WRITE)) {
      bgNode.setColor((float) red / 255.0f, (float) green / 255.0f,
          (float) blue / 255.0f);
    } else {
      logger.severe("No capability to set background color");
    }
  }

  public Color getBackgroundColor() {
    if (bgNode.getCapability(Background.ALLOW_COLOR_READ)) {
      Color3f bgColor = new Color3f();
      bgNode.getColor(bgColor);
      Color color = new Color(bgColor.x, bgColor.y, bgColor.z);
      return color;
    } else {
      logger.severe("No capability to get background color");
      return null;
    }

  }

  public Color3f getBackgroundColor3f() {
    if (bgNode.getCapability(Background.ALLOW_COLOR_READ)) {
      Color3f bgColor = new Color3f();
      bgNode.getColor(bgColor);
      return bgColor;
    } else {
      logger.severe("No capability to get background color");
      return null;
    }

  }

  void initSceneGraph() {
    Transform3D t = new Transform3D();
    t.set(new Vector3f(0.0f, 0.0f, -100.0f));
    sceneOffset = new TransformGroup(t);
    sceneOffset.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    sceneOffset.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    sceneTrans = new TransformGroup();
    sceneTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    sceneTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    sceneTrans.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);

    sceneSwitch = new Switch(Switch.CHILD_MASK);
    sceneSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
    sceneSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);

    fastGroup = new BranchGroup();
    niceGroup = new BranchGroup();
    fastGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    fastGroup.setCapability(Group.ALLOW_CHILDREN_READ);
    fastGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);

    niceGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    niceGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    niceGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    niceGroup.setPickable(true);

    sceneSwitch.addChild(niceGroup);
    sceneSwitch.addChild(fastGroup);
    sceneSwitch.setWhichChild(0);

    // Create the drag behavior node !!! Commented
    //MouseFastRotate behavior = new MouseFastRotate(sceneTrans, this );
    //behavior.setSchedulingBounds(bounds);
    //sceneTrans.addChild(behavior);
    //MouseRotate behavior = new MouseRotate(sceneTrans);
    behavior = new ProcessMouseRotate(this, sceneTrans);
    behavior.setSchedulingBounds(bounds);
    sceneTrans.addChild(behavior);

    //behavior.setParentFrame(this);
    // Create the zoom behavior node
    //MouseZoom behavior2 = new MouseZoom(sceneTrans);
    //behavior2.setSchedulingBounds(bounds);
    //sceneTrans.addChild(behavior2);
    MyMouseZoom myMouseZoom = new MyMouseZoom(sceneTrans);
    myMouseZoom.setSchedulingBounds(bounds);
    sceneTrans.addChild(myMouseZoom);

    // Create the zoom behavior node
    MouseTranslate behavior3 = new MouseTranslate(sceneTrans);
    behavior3.setSchedulingBounds(bounds);
    sceneTrans.addChild(behavior3);

    // --- Keyborad behavior (experimental phase)
    KeyHandler k_behavior = new KeyHandler(sceneTrans);
    //TransformGroup vpTrans = su.getViewingPlatform().getViewPlatformTransform();
    //myKeyNavigatorBehavior k_behavior = new myKeyNavigatorBehavior(sceneTrans);
    k_behavior.setSchedulingBounds(bounds);
    sceneTrans.addChild(k_behavior);

    // --- Mouse behavior (experimental)
    //MousePickingHandler m_behavior = new MousePickingHandler(canvas3D, root, bounds);
    //root.addChild( m_behavior );
    //root.setPickable( true );
    sceneTrans.addChild(sceneSwitch);

    sceneOffset.addChild(sceneTrans);
    rootTrans.addChild(sceneOffset);
  }

  void initLighting() {

    Color3f dlColor = new Color3f(.75f, 0.75f, 0.75f);

    Vector3f lDirect1 = new Vector3f(-1.0f, -1.0f, -1.0f);
    //Vector3f lDirect1 = new Vector3f(1.0f, -1.0f, -1.0f);
    //Vector3f lDirect2 = new Vector3f( -1.0f, 1.0f, 1.0f);

    DirectionalLight lgt1 = new DirectionalLight(dlColor, lDirect1);
    lgt1.setInfluencingBounds(bounds);
    //DirectionalLight lgt2 = new DirectionalLight(dlColor, lDirect2);
    //lgt2.setInfluencingBounds(bounds);

    Color3f alColor = new Color3f(0.65f, 0.65f, 0.65f);
    AmbientLight aLgt = new AmbientLight(alColor);
    aLgt.setInfluencingBounds(bounds);

    root.addChild(aLgt);
    root.addChild(lgt1);
    //root.addChild(lgt2);

    // Try some fog for depth cueing
    fog = new LinearFog();
    fog.setCapability(LinearFog.ALLOW_DISTANCE_WRITE);
    fog.setColor(new Color3f(0.0f, 0.0f, 0.0f));
    fog.setFrontDistance(10.0);
    fog.setBackDistance(500.0);
    fog.setInfluencingBounds(bounds);
    fog.addScope(sceneTrans);
    sceneTrans.addChild(fog);

  }

  public void renderMolecule(MoleculeInterface m) {
    addMolecule(m);
  }

  public void addMolecule(Molecule m) {
    addMolecule((MoleculeInterface) m);
  }

  public void appendMolecule(MoleculeInterface referenceMol,
      MoleculeInterface m, float coord[][]) {
    // --- Add atoms to the real molecule

    int oldNumAtoms = referenceMol.getNumberOfAtoms();
    int oldNumBonds = referenceMol.getNumberOfBonds();
    logger.info("Old # atoms: " + oldNumAtoms + " Old # bonds: "
        + oldNumBonds);
    referenceMol.appendMolecule(m);
    logger.info("New # atoms: " + referenceMol.getNumberOfAtoms()
        + " New # bonds: " + referenceMol.getNumberOfBonds());

    // --- Update their coordinates
    logger.info("Updating coords " + oldNumAtoms + " - " + (oldNumAtoms + m.getNumberOfAtoms() - 1));
    for (int i = 0; i < m.getNumberOfAtoms(); i++) {
      AtomInterface atom = referenceMol.getAtomInterface(oldNumAtoms + i);
      atom.setXYZ(coord[i][0], coord[i][1], coord[i][2]);
    }

    // --- create graphics
    // --- Add new atoms
    for (int i = oldNumAtoms; i < referenceMol.getNumberOfAtoms(); i++) {
      AtomInterface atom = referenceMol.getAtomInterface(i);
      //BranchGroup at = createAtom(atom);
      AtomNode at = new AtomNode(atom);
      atom.setProperty(ATOM_NODE, at);
      atomLabels.add(i, null);
      mol.addChild(at);
      //atomNodes.add(at);
      Material store = cloneMaterial(at.getMaterial());
      store.setCapability(Material.ALLOW_COMPONENT_READ);
      store.setCapability(Material.ALLOW_COMPONENT_WRITE);
      atom.setProperty(ATOM_NODE_MATERIAL_COPY, store);
      //materialStore.add(store);
    }

    // --- Add new bonds
    for (int i = oldNumBonds; i < referenceMol.getNumberOfBonds(); i++) {
      BondInterface b = referenceMol.getBondInterface(i);
      BondNode bond = new BondNode(b);
      mol.addChild(bond);
      bondNodes.add(bond);
    }

  }

  public void addGraphicsToMolecule(Group object, boolean enable) {
    if (object == null) {
      return;
    }
    if (enable && object.getParent() == null) {
      moleculeTrans.addChild(object);
    } else if (!enable) {
      //logger.info("Object: allow detach " + object.getCapability(BranchGroup.ALLOW_DETACH));
      //if (!object.getCapability(BranchGroup.ALLOW_DETACH)) {
      //object.setCapability(BranchGroup.ALLOW_DETACH);
      //}
      //object.detach();
      moleculeTrans.removeChild(object);
    }
  }

  public void appendMolecule(MoleculeInterface m, boolean merge_molecules) {

    if (m == null || m.getNumberOfAtoms() < 1) {
      return;
    }

    // --- Add atoms to the real molecule
    boolean empty = molecule == null || molecule.getNumberOfAtoms() == 0;
    if (empty) {
      this.setMolecule(m);
      return;
    }

    int oldNumAtoms = empty ? 0 : molecule.getNumberOfAtoms();
    int oldNumBonds = empty ? 0 : molecule.getNumberOfBonds();
    System.out.println("To be appended # atoms: " + m.getNumberOfAtoms() + " # bonds: " + m.getNumberOfBonds());
    System.out.println("Old # atoms: " + oldNumAtoms + " Old # bonds: " + oldNumBonds);
    logger.info("Old # atoms: " + oldNumAtoms + " Old # bonds: " + oldNumBonds);
    if (merge_molecules) {
      molecule.mergeMolecule(m);
    } else {
      molecule.appendMolecule(m);
    }
    logger.info("New # atoms: " + molecule.getNumberOfAtoms() + " New # bonds: " + molecule.getNumberOfBonds());

    // --- create graphics
    // --- Add new atoms
    for (int i = oldNumAtoms; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface atom = molecule.getAtomInterface(i);
      AtomNode at = new AtomNode(atom); // createAtom(atom);
      atom.setProperty(ATOM_NODE, at);
      atomLabels.add(i, null);
      mol.addChild(at);
      //atomNodes.add(at);
      Material store = cloneMaterial(at.getMaterial());
      atom.setProperty(ATOM_NODE_MATERIAL_COPY, store);
      //materialStore.add(store);

    }

    // --- Add new bonds
    for (int i = oldNumBonds; i < molecule.getNumberOfBonds(); i++) {
      BondInterface b = molecule.getBondInterface(i);
      BondNode bond = new BondNode(b);
      mol.addChild(bond);
      bondNodes.add(bond);
    }

  }

  /**
   * Clones material
   *
   * @param material Material
   * @return Material
   */
  public static Material cloneMaterial(Material material) {
    if (material == null) {
      return null;
    }
    Color3f ambientColor = new Color3f();
    Color3f emissiveColor = new Color3f();
    Color3f diffuseColor = new Color3f();
    Color3f specularColor = new Color3f();

    material.getAmbientColor(ambientColor);
    material.getEmissiveColor(emissiveColor);
    material.getDiffuseColor(diffuseColor);
    material.getSpecularColor(specularColor);
    float shininess = material.getShininess();
    Material store = new Material(ambientColor, emissiveColor,
        diffuseColor, specularColor, shininess);
    store.setCapability(Material.ALLOW_COMPONENT_READ);
    store.setCapability(Material.ALLOW_COMPONENT_WRITE);
    return store;
  }

  public void appendMolecule(Java3dUniverse otherRenderer) {
  }

  /**
   * General version.
   *
   * @param m
   * @param read_mode
   */
  public void setMolecule(MoleculeInterface m, GlobalSettings.ADD_MOLECULE_MODE read_mode) {
    switch (read_mode) {
      case SET:
        removeMolecule();
        addMolecule(m);
        break;
      case APPEND:
        appendMolecule(m, false);
        break;
      default:
        logger.warning(read_mode.toString() + " is not implemented");
    }
  }

  /**
   * Default version First deletes molecule and all related graphics, then adds a new molecule
   *
   * @param m MoleculeInterface
   */
  public void setMolecule(MoleculeInterface m) {
    setMolecule(m, GlobalSettings.ADD_MOLECULE_MODE.SET);
  }

  public void addMolecule(MoleculeInterface molecule, int how_to_load_molecule) {
    addMolecule(molecule);
  }

  public void setRenderingParameters(int style) {
    if (style == MoleculeInterface.WIREFRAME) {
      int atomRenderingStyle = AtomInterface.RENDER_SMART_POINT;
      bondRenderingStyle = BondInterface.LINE_BICOLOR;

      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface a = molecule.getAtomInterface(i);
        a.setProperty(AtomInterface.RENDERING_STYLE, new Integer(atomRenderingStyle));
        a.setProperty(AtomInterface.GR_RADIUS, new Float(0.01f)); // GR_RADIUS "gradius" is a "graphics radius"
      }

      for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
        BondInterface b = molecule.getBondInterface(i);
        b.setProperty(BondInterface.RENDERING_STYLE, new Integer(BondInterface.LINE_BICOLOR));
        b.setProperty(BondInterface.COLOR_SCHEME, new Integer(BondInterface.BICOLOR));
        // --- Decide whether it's visible
        AtomInterface a = b.getIAtomInterface();
        Object obj = a.getProperty(AtomInterface.VISIBLE);
        if (obj == null) {
          a.setProperty(AtomInterface.VISIBLE, new Boolean(true));
        }

        if (!(Boolean) obj) {
          b.setProperty(BondInterface.VISIBLE, new Boolean(false));
          continue;
        }

        a = b.getJAtomInterface();
        obj = a.getProperty(AtomInterface.VISIBLE);
        if (obj == null) {
          a.setProperty(AtomInterface.VISIBLE, new Boolean(true));
        }

        if (!(Boolean) obj) {
          b.setProperty(BondInterface.VISIBLE, new Boolean(false));
          continue;
        }
        b.setProperty(BondInterface.VISIBLE, new Boolean(true));
      }

    } else if (style == MoleculeInterface.STICKS) {
      int atomRenderingStyle = AtomInterface.RENDER_SPHERE;
      bondRenderingStyle = BondInterface.CYLINDER_BICOLOR;
      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface a = molecule.getAtomInterface(i);
        a.setProperty(AtomInterface.RENDERING_STYLE, new Integer(atomRenderingStyle));
        a.setProperty(AtomInterface.GR_RADIUS, new Float(BondInterface.DEFAULT_STICKS_RADIUS)); // GR_RADIUS "gradius" is a "graphics radius"
      }
      for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
        BondInterface b = molecule.getBondInterface(i);

        b.setProperty(BondInterface.CYLINDER_RADIUS, new Float(BondInterface.DEFAULT_STICKS_RADIUS));
        b.setProperty(BondInterface.RENDERING_STYLE, new Integer(BondInterface.CYLINDER_BICOLOR));
        b.setProperty(BondInterface.COLOR_SCHEME, new Integer(BondInterface.BICOLOR));

        // --- Decide whether it's visible
        AtomInterface a = b.getIAtomInterface();
        Object obj = a.getProperty(AtomInterface.VISIBLE);
        if (obj == null) {
          a.setProperty(AtomInterface.VISIBLE, new Boolean(true));
        }

        if (!(Boolean) obj) {
          b.setProperty(BondInterface.VISIBLE, new Boolean(false));
          continue;
        }

        a = b.getJAtomInterface();
        obj = a.getProperty(AtomInterface.VISIBLE);
        if (obj == null) {
          a.setProperty(AtomInterface.VISIBLE, new Boolean(true));
        }

        if (!(Boolean) obj) {
          b.setProperty(BondInterface.VISIBLE, new Boolean(false));
          continue;
        }

        b.setProperty(BondInterface.VISIBLE, new Boolean(true));
      }

    } else if (style == MoleculeInterface.BALL_AND_STICKS) {
      int atomRenderingStyle = AtomInterface.RENDER_SPHERE;
      bondRenderingStyle = BondInterface.CYLINDER_BICOLOR;
      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface a = molecule.getAtomInterface(i);
        a.setProperty(AtomInterface.RENDERING_STYLE, new Integer(atomRenderingStyle));
        float radius = ChemicalElements.getCovalentRadius(a.getAtomicNumber());
        radius *= AtomInterface.COVALENT_TO_GRADIUS_FACTOR;
        a.setProperty(AtomInterface.GR_RADIUS, new Float(radius)); // GR_RADIUS "gradius" is a "graphics radius"
      }
      for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
        BondInterface b = molecule.getBondInterface(i);
        b.setProperty(BondInterface.CYLINDER_RADIUS, new Float(BondInterface.DEFAULT_BONDS_AND_STICKS_RADIUS));
        b.setProperty(BondInterface.RENDERING_STYLE, new Integer(BondInterface.CYLINDER_BICOLOR));
        b.setProperty(BondInterface.COLOR_SCHEME, new Integer(BondInterface.BICOLOR));
        //b.setProperty(BondInterface.COLOR_SCHEME,
        //              new Integer(BondInterface.BICOLOR));

        // --- Decide whether it's visible
        AtomInterface a = b.getIAtomInterface();
        Object obj = a.getProperty(AtomInterface.VISIBLE);
        if (obj == null) {
          a.setProperty(AtomInterface.VISIBLE, new Boolean(true));
        }

        if (!(Boolean) obj) {
          b.setProperty(BondInterface.VISIBLE, new Boolean(false));
          continue;
        }

        a = b.getJAtomInterface();
        obj = a.getProperty(AtomInterface.VISIBLE);
        if (obj == null) {
          a.setProperty(AtomInterface.VISIBLE, new Boolean(true));
        }

        if (!(Boolean) obj) {
          b.setProperty(BondInterface.VISIBLE, new Boolean(false));
          continue;
        }

        b.setProperty(BondInterface.VISIBLE, new Boolean(true));

      }
    } else if (style == MoleculeInterface.SPACEFILL) {
      int atomRenderingStyle = AtomInterface.RENDER_SPHERE;
      bondRenderingStyle = BondInterface.LINE_BICOLOR;
      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface a = molecule.getAtomInterface(i);
        a.setProperty(AtomInterface.RENDERING_STYLE,
            new Integer(atomRenderingStyle));
        float radius = ChemicalElements.getVanDerWaalsRadius(a.getAtomicNumber());
        //radius *= AtomInterface.COVALENT_TO_GRADIUS_FACTOR;
        a.setProperty(AtomInterface.GR_RADIUS, new Float(radius)); // GR_RADIUS "gradius" is a "graphics radius"
      }
      for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
        BondInterface b = molecule.getBondInterface(i);
        b.setProperty(BondInterface.VISIBLE, new Boolean(false));
        b.setProperty(BondInterface.RENDERING_STYLE, new Integer(BondInterface.LINE_BICOLOR));
        b.setProperty(BondInterface.COLOR_SCHEME, new Integer(BondInterface.BICOLOR));

      }

    }

    // --- Update them
    updateAtomicRadii();
    updateBondRadii();
  }

  /**
   * Updates atomic radii (for drawing)
   */
  public void updateAtomicRadii() {
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      AtomNode atom_node = (AtomNode) a.getProperty(ATOM_NODE);
      //AtomNode atom_node = atomNodes.get(i);

      Object obj = a.getProperty(AtomInterface.VISIBLE);
      if (obj == null) {
        a.setProperty(AtomInterface.VISIBLE, new Boolean(true));
      }
      if (!(Boolean) obj) {
        continue;
      }

      Float R = (Float) a.getProperty(AtomInterface.GR_RADIUS);
      if (R != null) {
        setAtomicSphereRadius(atom_node, R);
      }
    }
  }

  public void updateBondRadii() {
    for (Iterator iter = bondNodes.iterator(); iter.hasNext();) {
      BondNode bond = (BondNode) iter.next();
      if (bond == null) {
        continue;
      }

      BondInterface bi = bond.getBond();
      Object obj = bi.getProperty(BondInterface.VISIBLE);
      if (obj == null) {
        bi.setProperty(BondInterface.VISIBLE, new Boolean(true));
      }
      if (!(Boolean) obj) {
        continue;
      }

      bond.updateBondRadius();
    }
  }

  /**
   * Translates geometric center of a molecule to a point with coordinates x,y,z
   *
   * @param molec MoleculeInterface
   * @param x float
   * @param y float
   * @param z float
   */
  public static void centerMolecule(MoleculeInterface molec, float x, float y,
      float z) {
    if (molec.getNumberOfAtoms() < 1) {
      return;
    }
    AtomInterface a = molec.getAtomInterface(0);
    float x_center = a.getX();
    float y_center = a.getY();
    float z_center = a.getZ();
    for (int i = 1; i < molec.getNumberOfAtoms(); i++) {
      a = molec.getAtomInterface(i);
      x_center += a.getX();
      y_center += a.getY();
      z_center += a.getZ();
    }

    x_center /= (float) molec.getNumberOfAtoms();
    y_center /= (float) molec.getNumberOfAtoms();
    z_center /= (float) molec.getNumberOfAtoms();

    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      a = molec.getAtomInterface(i);
      a.setX(a.getX() - x_center + x);
      a.setY(a.getY() - y_center + y);
      a.setZ(a.getZ() - z_center + z);
    }

  }

  public void resetMoleculeGraphics() {
    //detachRoot();
    //root.detach();

    // --- Clean nodes
    mol.detach();
    mol.removeAllChildren();

    //atomNodes.clear();
    bondNodes.clear();

    //materialStore.clear();
    atomLabels.clear();
  }

  public void removeMolecule() {

    moleculeTrans.removeAllChildren();
    if (graphicsObjects != null) {
      this.graphicsObjects.clear();
    }
    molecule = new Molecule();
    addMolecule(molecule);
  }

  public void addMolecule(MoleculeInterface m) {
    if (molecule != null) {
      molecule = null;
    }
    molecule = m;

    drawMolecule(molecule);

    centerSceneOnScreen();
  }

  public void drawMolecule(MoleculeInterface m) {
    Object obj;
    //removeMolecule();
    resetMoleculeGraphics();

    //gAtom a;
    AtomInterface a;

    //setBounds(m);
    float x = m.getXmax() - m.getXmin();
    float y = m.getYmax() - m.getYmin();
    float ex = Math.max(x, y);
    logger.info("ex: " + ex + " " + m.getZmin() + " " + m.getZmax());
    float backPlane = m.getZmax() > 150.0f ? (m.getZmax() + 50.0f) : 150.0f;
    //simpleU.getViewer().getView().setBackClipDistance(backPlane);

    //view.setBackClipDistance(backPlane);
    //fog.setFrontDistance(0.5f * (m.getZmin() + m.getZmax()));
    //fog.setFrontDistance( -5.0f);
    //fog.setBackDistance(backPlane);
    Transform3D t = new Transform3D();

    double xField = view.getFieldOfView();
    double yField = view.getFieldOfView();
    if ((float) canvas3D.getWidth() / (float) canvas3D.getHeight() > 1.0f) {
      yField = 2.0
          * Math.atan(Math.tan(view.getFieldOfView() / 2.0) * (double) canvas3D.getHeight() / (double) canvas3D.getWidth());
    } else {
      xField = 2.0
          * Math.atan(Math.tan(view.getFieldOfView() / 2.0) * (double) canvas3D.getWidth() / (double) canvas3D.getHeight());
    }

    double minX = x / (2.0 * Math.tan(xField / 2.0));
    double minY = y / (2.0 * Math.tan(yField / 2.0));
    double minZ = m.getZmax() - m.getZmin();
    float shift = (float) Math.max(Math.max(minX, minY), minZ);
    if (debug) {
      logger.info("Field of view: " + view.getFieldOfView() * RADS_TO_DEGS + " X field: " + xField * RADS_TO_DEGS
          + " Y field: " + yField * RADS_TO_DEGS);
    }
    //canvas3D.g

    //t.set(new Vector3f(0.0f, 0.0f, (m.getZmin() - 2.0f * ex)));
    t.set(new Vector3f(0.0f, 0.0f, -shift - 5.0f));
    //t.setScale(15.0f/ex);
    sceneOffset.setTransform(t);

    fog.setFrontDistance(-5.0f); // !!! Make better with fog
    if (shift > 50) {
      fog.setBackDistance(3 * shift);
    } else {
      fog.setBackDistance(150);
    }

    if (bondNodes instanceof ArrayList) {
      ((ArrayList) bondNodes).ensureCapacity(m.getNumberOfBonds());
    }

    if (atomLabels instanceof ArrayList) {
      ((ArrayList) atomLabels).ensureCapacity(m.getNumberOfAtoms());
    }

    for (int i = 0; i < m.getNumberOfAtoms(); i++) {
      atomLabels.add(i, null);
    }

    // --- Setup rendering style
    int atomRenderingStyle = AtomInterface.RENDER_SPHERE;
    bondRenderingStyle = BondInterface.CYLINDER_BICOLOR;

    Object rStyle = m.getProperty(MoleculeInterface.RenderingStyle);
    if (rStyle != null && rStyle instanceof Integer) {
      renderingStyle = ((Integer) rStyle).intValue();
    } else {
      if (m.getNumberOfAtoms() > 150) {
        renderingStyle = MoleculeInterface.WIREFRAME;
      } else {
        renderingStyle = MoleculeInterface.BALL_AND_STICKS;
      }
    }

    if (renderingStyle == MoleculeInterface.WIREFRAME) {
      atomRenderingStyle = AtomInterface.RENDER_SMART_POINT;
      bondRenderingStyle = BondInterface.LINE_BICOLOR;
    }

    // Draw Atoms (Spheres)
    for (int i = 0; i < m.getNumberOfAtoms(); i++) {
      a = m.getAtomInterface(i);
      obj = a.getProperty(AtomInterface.VISIBLE);
      if (obj == null) {
        a.setProperty(AtomInterface.VISIBLE, new Boolean(true));
      }

      obj = a.getProperty(AtomInterface.RENDERING_STYLE);
      if (obj == null) {
        a.setProperty(AtomInterface.RENDERING_STYLE, new Integer(atomRenderingStyle));
      }
      AtomNode at = new AtomNode(a); // createAtom(a);

      a.setProperty(ATOM_NODE, at);
      mol.addChild(at);
      //atomNodes.add(at);

      //Material mater = getAtomicNodeMaterial(at);
      Material store = cloneMaterial(at.getMaterial());
      a.setProperty(ATOM_NODE_MATERIAL_COPY, store);
      //materialStore.add(store);
    }

    drawBonds(m);

    //mol.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    //mol.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    //mol.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
    //mol.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    //mol.setCapability(BranchGroup.ALLOW_DETACH);
    //mol.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
    //mol.setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
    //mol.setCapability(BranchGroup.ALLOW_LOCAL_TO_VWORLD_READ);
    //mol.setPickable(true);
    // --- Mouse behavior (experimental)
    /*
     * mouse_picking_behavior = new MousePicking(this, mol, canvas3D, bounds); mouse_picking_behavior.setTolerance(0);
     * mouse_picking_behavior.setEnable(false); mol.addChild(mouse_picking_behavior);
     */
    addMousePicking(this, mol, canvas3D, bounds);

    Transform3D move = new Transform3D();
    moleculeTrans.setTransform(move);

    //mol.compile();
    //niceGroup.addChild(mol);
    moleculeTrans.addChild(mol);

  }

  public void drawBonds(MoleculeInterface m) {
    // Draw bonds (Cylinders)

    for (int i = 0; i < m.getNumberOfBonds(); i++) {
      BondInterface b = m.getBondInterface(i);
      Object obj = b.getProperty(BondInterface.RENDERING_STYLE);
      int bRendStyle = bondRenderingStyle;
      if (obj == null) {
        b.setProperty(BondInterface.RENDERING_STYLE, new Integer(bondRenderingStyle));
      } else if (obj instanceof Integer) {
        bRendStyle = ((Integer) obj).intValue();
      }

      BondNode bond = new BondNode(b, bRendStyle);
      mol.addChild(bond);
      b.setProperty(BOND_NODE, bond);
      bondNodes.add(bond);
    }

  }

  public void addMousePicking(Java3dUniverse dad, BranchGroup root, Canvas3D canvas, Bounds bounds) {
    mouse_picking_behavior = new MousePicking(dad, root, canvas, bounds);
    mouse_picking_behavior.setTolerance(0);
    mouse_picking_behavior.setEnable(false);
    root.addChild(mouse_picking_behavior);
  }

  public void centerSceneOnScreen() {
    centerMolecule(0, 0, 0);
    centerScene();
    //vp.setViewAttachPolicy(View.NOMINAL_SCREEN);
  }

  public void setRotationCenterOnSelectedAtoms() {

    if (trackSelectedAtoms.size() < 1) {
      return;
    }

    AtomInterface a = trackSelectedAtoms.get(0);
    float x_center = a.getX();
    float y_center = a.getY();
    float z_center = a.getZ();

    for (int i = 1; i < trackSelectedAtoms.size(); i++) {
      a = trackSelectedAtoms.get(i);
      x_center += a.getX();
      y_center += a.getY();
      z_center += a.getZ();
    }

    x_center /= (float) trackSelectedAtoms.size();
    y_center /= (float) trackSelectedAtoms.size();
    z_center /= (float) trackSelectedAtoms.size();

    Transform3D move = new Transform3D();
    moleculeTrans.getTransform(move);
    move.setTranslation(new Vector3f(-x_center, -y_center, -z_center));
    moleculeTrans.setTransform(move);

    //centerMolecule( -x_center, -y_center, -z_center);
    centerScene();
  }

  public void centerMolecule(float x, float y, float z) {

    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      return;
    }
    AtomInterface a = molecule.getAtomInterface(0);
    float x_center = a.getX();
    float y_center = a.getY();
    float z_center = a.getZ();
    for (int i = 1; i < molecule.getNumberOfAtoms(); i++) {
      a = molecule.getAtomInterface(i);
      x_center += a.getX();
      y_center += a.getY();
      z_center += a.getZ();
    }

    x_center /= (float) molecule.getNumberOfAtoms();
    y_center /= (float) molecule.getNumberOfAtoms();
    z_center /= (float) molecule.getNumberOfAtoms();

    Transform3D move = new Transform3D();
    move.setTranslation(new Vector3f(-x_center + x, -y_center + y, -z_center + z));
    moleculeTrans.setTransform(move);
  }

  public void centerScene() {
    Transform3D transl = new Transform3D();
    sceneTrans.getTransform(transl);
    transl.setTranslation(new Vector3f(0, 0, 0));
    sceneTrans.setTransform(transl);

  }

  public void fillEmptyValences(AtomInterface atom) {
    Map store = null;
    try {
      store = AtomGenerator.fillEmptyValences(molecule, atom, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getAtomTypes());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Start to add atoms & bonds
    List bunch = (List) store.get("atoms");
    for (int i = 0; i < bunch.size(); i++) {
      AtomInterface a = (AtomInterface) bunch.get(i);
      addAtom(a);
    }

    bunch = (List) store.get("bonds");
    for (int i = 0; i < bunch.size(); i++) {
      BondInterface bond = (BondInterface) bunch.get(i);
      addBond(bond);
    }

  }

  public AtomNode createAtom(AtomInterface atom) {

    return new AtomNode(atom);
    /*
     * // --- Setup radius of atomic sphere float atomRadius = getAtomicSphereRadius(atom);
     *
     * // --- Setup RGB color of atomic sphere !!! To be done !!!
     *
     * Color3f color = ChemicalElementsColors.getElementColor(atom. getAtomicNumber());
     *
     * // --- Set material of atomic sphere !!! --- To be done !!!
     *
     * Material material = ChemicalElementsColors.getElementMaterial(atom. getAtomicNumber());
     *
     * return createAtom(atom.getX(), atom.getY(), atom.getZ(), atom.getAtomicNumber(), material, atomRadius, color);
     */
  }

  /*
   * public AtomNode createAtom(float x, float y, float z, int element) { Material material =
   * ChemicalElementsColors.getElementMaterial(element); material.setCapability(Material.ALLOW_COMPONENT_READ);
   * material.setCapability(Material.ALLOW_COMPONENT_WRITE); float radius = ChemicalElements.getCovalentRadius(element); radius *=
   * AtomInterface.COVALENT_TO_GRADIUS_FACTOR; if (radius < 0.001f) { radius = 0.2f; } Color3f color =
   * ChemicalElementsColors.getElementColor(element); return createAtom(x, y, z, element, material, radius, color); }
   */
  /**
   *
   */
  /*
   * public AtomNode createAtom(float x, float y, float z, int element, Material material, float atomRadius, Color3f color) {
   *
   * AtomNode atom = createAtomicSphere(atomRadius, x, y, z, 100, color, material);
   *
   * return atom; } public AtomNode createAtomicSphere(float radius, float x, float y, float z, int divisions, Color3f color,
   * Material material) { AtomNode at = new AtomNode(radius, x, y, z, divisions, color, material);
   *
   *
   * temp_Sphere = at.getAtomicSphere();
   *
   * return at; }
   */
  /**
   * Returns BranchGroup of a molecule
   *
   * @return BranchGroup
   */
  public BranchGroup getMoleculeBranchGroup() {
    return mol;
  }

  public BranchGroup getRootBranchGroup() {
    return niceGroup;
  }

  public Locale getLocale() {
    return locale;
  }

  public MoleculeInterface getMolecule() {
    return molecule;
  }

  public MoleculeInterface getMoleculeInterface() {
    return molecule;
  }

  public BranchGroup createSceneGraph() {
    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();

    // rotate object has composite transformation matrix
    rotate = new Transform3D();
    rotate.rotX(Math.PI / 4.0d);

    translate = new Transform3D();
    translate.set(new Vector3f(1.0f, 0.0f, 0.0f));

    ColoringAttributes ca = new ColoringAttributes();
    ca.setColor(1.0f, 0.5f, 0.7f);
    atomAppear = new Appearance();
    atomAppear.setColoringAttributes(ca);

    Material material = new Material();
    material.setCapability(Material.ALLOW_COMPONENT_READ);
    material.setCapability(Material.ALLOW_COMPONENT_WRITE);
    atomAppear.setMaterial(material);

    PolygonAttributes polyAppear = new PolygonAttributes();
    polyAppear.setCullFace(PolygonAttributes.CULL_BACK);

    atomAppear.setPolygonAttributes(polyAppear);

    // Setup atoms
    Sphere atom1 = new Sphere(AtomInterface.COVALENT_TO_GRADIUS_FACTOR,
        Sphere.GENERATE_NORMALS, atomAppear);
//          atom1.setAppearance( atomAppear );

    Sphere atom2 = new Sphere();
    atom2.setAppearance(atomAppear);

    // We need only translate a sphere
    TransformGroup tr1 = new TransformGroup(translate);
    tr1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tr1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    objRoot.addChild(tr1);
    tr1.addChild(atom1);

    translate.set(new Vector3f(-1.0f, 0.0f, 0.0f));

    TransformGroup tr2 = new TransformGroup(translate);
    tr2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tr2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    objRoot.addChild(tr2);
    tr2.addChild(atom2);

    objRotate = new TransformGroup(rotate);
    objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

//          objRotate.addChild(new ColorCube(0.4));
//          objRoot.addChild(objRotate);
    //myMouseRotate = new MyMouseRotate();
    //myMouseRotate.setTransformGroup(tr1);
    //myMouseRotate.setSchedulingBounds(new BoundingSphere());
    //objRoot.addChild(myMouseRotate);
    myMouseZoom = new MouseZoom(MouseBehavior.INVERT_INPUT);
    myMouseZoom.setTransformGroup(tr1);
    myMouseZoom.setSchedulingBounds(new BoundingSphere(new Point3d(),
        1000.0));
    objRoot.addChild(myMouseZoom);

    // Lighting
    AmbientLight lightA = new AmbientLight();
    lightA.setInfluencingBounds(new BoundingSphere());
    objRoot.addChild(lightA);

    DirectionalLight lightD1 = new DirectionalLight();
    lightD1.setInfluencingBounds(new BoundingSphere());
    // customize DirectionalLight object
    objRoot.addChild(lightD1);

    // Let Java 3D perform optimizations on this scene graph.
    objRoot.compile();

    // Create a simple shape leaf node, add it to the scene graph.
    // ColorCube is a Convenience Utility class
    //objRoot.addChild(new ColorCube(0.4));
    return objRoot;
  } // end of createSceneGraph method of HelloJava3Da

  public String getSQLHostName() {
    return "jdbc:mysql://localhost/";
  }

  public void doAtomSelection(Point3d origin, Vector3d dir, BranchGroup bgr,
      PickResult[] pickingResults) {

    //if (molecule == null || molecule.getNumberOfAtoms() < 1) {
    //   return;
    //}
    int closest_atom = findClosestAtom(origin, dir);
    doAtomSelection(closest_atom);
  }

  public boolean isAtomVisible(AtomInterface a) {
    AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);

      return mol.indexOfChild( atom ) != -1;
  }

  public int findClosestAtom(Point3d origin, Vector3d dir) {

    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      return -1;
    }

    if (dir == null || dir.z == 0.0) { // View direction is parallel to the VW's Z-plane
      System.err.println("dir == null || dir.z == 0.0. Ignoring...");
      return -1;
    }

    Point3f xyz = new Point3f();

    // Get transform to the virtual world (VW)
    Transform3D toVW = getVWTransform();

    // --- Checking all atoms for intersection
    int closest_atom = -1;
    boolean intersect_sphere = false;
    double closest_distance = -1;

    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface a = molecule.getAtomInterface(i);

      if (!isAtomVisible(a)) { // Atom is invisible
        continue;
      }

      xyz.set(a.getX(), a.getY(), a.getZ());
      //logger.info("Local xyz: " + xyz);

      // --- Coordinates of atom in the VW
      toVW.transform(xyz);
      //logger.info("VW xyz: " + xyz);

      // --- knowing viewing point and direction of view we can find
      // --- intersection point of a direction vector with Z-plane of the VW
      // --- which contains a given atom
      double t = -(-xyz.z + origin.z) / dir.z;
      double x = origin.x + t * dir.x;
      double y = origin.y + t * dir.y;
      double z = origin.z + t * dir.z;
      //logger.info("Intersection with VW Z-plane: " + xyz);

      // --- Get graphics radius
      double gr_radius = 0.1;
      Object obj = a.getProperty(AtomInterface.GR_RADIUS);
      if (obj != null) {
        if (obj instanceof Float) {
          gr_radius = ((Float) obj).doubleValue();
        } else if (obj instanceof Double) {
          gr_radius = ((Double) obj).doubleValue();
        } else if (obj instanceof String) {
          try {
            gr_radius = Double.parseDouble(obj.toString());
          } catch (Exception ex) {
            System.err.println(
                "Wrong value for graphical radius: " + obj.toString() + " Ignoring...");
          }
        } else {
          System.err.println(
              "Unsupported data type for graphical radius: "
              + obj.getClass().getCanonicalName() + " Ignoring...");
        }

        if (gr_radius < 0.1) {
          gr_radius = 0.1;
        }
      }

      // --- Calculate distance between atom and mouse pick
      double dist = Math.sqrt((xyz.x - x) * (xyz.x - x)
          + (xyz.y - y) * (xyz.y - y));

      // ---
      // --- Initialization #1
      if (closest_atom == -1 && dist <= gr_radius) {
        closest_atom = i;
        intersect_sphere = true;
        closest_distance = origin.z - z > 0 ? origin.z - z : z - origin.z;
      } // --- Initialization #2
      else if (closest_atom == -1 && dist <= 0.1) {
        closest_atom = i;
        intersect_sphere = false;
        closest_distance = origin.z - z > 0 ? origin.z - z : z - origin.z;
      } else if (dist <= gr_radius) {
        if (!intersect_sphere) {
          closest_atom = i;
          intersect_sphere = true;
          closest_distance = Math.abs(origin.z - z);
        } else if (closest_distance > Math.abs(origin.z - z)) {
          closest_atom = i;
          intersect_sphere = true;
          closest_distance = Math.abs(origin.z - z);
        }
      }
    }

    if (debug) {
      logger.info("Closest atom: " + closest_atom + " Intersect: " + intersect_sphere + " Dist: " + closest_distance);
    }

    return closest_atom;
  }

  public int findClosestBond(Point3d origin, Vector3d dir) {

    if (molecule == null || molecule.getNumberOfBonds() < 1) {
      return -1;
    }

    if (dir == null || dir.z == 0.0) { // View direction is parallel to the VW's Z-plane
      System.err.println("dir == null || dir.z == 0.0. Ignoring...");
      return -1;
    }

    cct.vecmath.Point3f xyz = new cct.vecmath.Point3f();
    cct.vecmath.Point3f ray_xyz = new cct.vecmath.Point3f();

    Segment3f ray = new Segment3f();
    ray.p1.setXYZ(origin.x, origin.y, origin.z);
    Segment3f bond_segment = new Segment3f();

    // Get transform to the virtual world (VW)
    Transform3D toVW = getVWTransform();

    // --- Checking all atoms for intersection
    int closest_bond = -1;
    boolean intersect_bond = false;
    double closest_distance_to_ray = -1;
    double closest_distance_to_origin = -1;
    double b_radius = 0.1;
    double minZ = 10000;
    Object obj;
    BondInterface bond;

    Point3f vw_coords[] = new Point3f[molecule.getNumberOfAtoms()];

    // --- First fill in array with the VW coordinates
    Set<AtomInterface> visibleAtoms = new HashSet<AtomInterface>(molecule.getNumberOfAtoms());

    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      if (!isAtomVisible(a)) { // Atom is invisible
        continue;
      }
      visibleAtoms.add(a);

      vw_coords[i] = new Point3f(a.getX(), a.getY(), a.getZ());
      //logger.info("Local xyz: " + vw_coords[i]);

      // --- Coordinates of atom in the VW
      toVW.transform(vw_coords[i]);
      a.setProperty("_VW_COORD", vw_coords[i]);
      //logger.info("VW xyz: " + vw_coords[i]);
      if (i == 0) {
        minZ = vw_coords[i].z;
      } else if (minZ > vw_coords[i].z) {
        minZ = vw_coords[i].z;
      }
    }

    double t = -(-minZ + origin.z) / dir.z;
    double x = origin.x + t * dir.x;
    double y = origin.y + t * dir.y;
    double z = origin.z + t * dir.z;
    ray.p2.setXYZ(x, y, z);

    // --- knowing viewing point and direction of view we can find
    // --- intersection point of a direction vector with Z-plane of the VW
    // --- which contains a given atom
    for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
      bond = molecule.getBondInterface(i);
      AtomInterface a1 = bond.getIAtomInterface();
      if (!visibleAtoms.contains(a1)) {
        continue;
      }
      AtomInterface a2 = bond.getJAtomInterface();
      if (!visibleAtoms.contains(a2)) {
        continue;
      }
      Point3f c1 = (Point3f) a1.getProperty("_VW_COORD");
      Point3f c2 = (Point3f) a2.getProperty("_VW_COORD");
      bond_segment.p1.setXYZ(c1.x, c1.y, c1.z);
      bond_segment.p2.setXYZ(c2.x, c2.y, c2.z);

      // --- Calculate distance between bond and mouse pick
      //double dist = Geometry3d.segmentToSegmentDistance(ray, bond_segment);
      double dist = Geometry3d.segmentToSegmentDistance(ray, bond_segment, ray_xyz, xyz);

      // --- Get bond radius
      b_radius = 0.1;
      obj = bond.getProperty(BondInterface.CYLINDER_RADIUS);
      if (obj != null) {
        if (obj instanceof Float) {
          b_radius = ((Float) obj).doubleValue();
        } else if (obj instanceof Double) {
          b_radius = ((Double) obj).doubleValue();
        } else if (obj instanceof String) {
          try {
            b_radius = Double.parseDouble(obj.toString());
          } catch (Exception ex) {
            System.err.println(
                "Wrong value for bond radius: " + obj.toString()
                + " Ignoring...");
          }
        } else {
          System.err.println(
              "Unsupported data type for graphical radius: "
              + obj.getClass().getCanonicalName() + " Ignoring...");
        }

        if (b_radius < 0.1) {
          b_radius = 0.1;
        }
      }

      // --- Initialization
      if (closest_bond == -1) {
        // --- Initialization #1
        if (dist <= b_radius) {
          closest_bond = i;
          intersect_bond = true;
          closest_distance_to_ray = dist;
          closest_distance_to_origin = ray_xyz.distanceTo(origin.x,
              origin.y, origin.z);
        } // --- Initialization #2
        else if (dist <= 0.1) {
          closest_bond = i;
          intersect_bond = false;
          closest_distance_to_ray = dist;
          closest_distance_to_origin = ray_xyz.distanceTo(origin.x,
              origin.y, origin.z);
        }
      } else if (dist <= b_radius) {
        if (!intersect_bond) {
          closest_bond = i;
          intersect_bond = true;
          closest_distance_to_ray = dist;
          closest_distance_to_origin = ray_xyz.distanceTo(origin.x,
              origin.y, origin.z);
        } else if (closest_distance_to_origin > ray_xyz.distanceTo(origin.x,
            origin.y, origin.z)) {
          closest_bond = i;
          intersect_bond = true;
          closest_distance_to_origin = ray_xyz.distanceTo(origin.x,
              origin.y, origin.z);
        }
      }

      //logger.info("Bond: " + i + " to ray distance: " + dist);
    }

    if (debug) {
      logger.info("Closest bond: " + closest_bond + " Intersect: " + intersect_bond + " Dist ro ray: "
          + closest_distance_to_ray + " Dist to origin: " + closest_distance_to_origin);
    }

    if (closest_bond < 0) {
      return closest_bond;
    }
    if (intersect_bond) {
      return closest_bond;
    }
    return -1;
  }

  public int getNumberSelectedAtoms() {
    return trackSelectedAtoms.size();
  }

  public void doAtomSelection(int nsel) {

    if (nsel != -1) {
      if (selectedMode == SELECTION_ONE_ATOM_ONLY) {
        // --- Is atom already selected?
        AtomInterface a = molecule.getAtomInterface(nsel);
        //if ( molecule.IsAtomSelected(nsel)) {
        if (a.isSelected()) {
          return;
        }
        selectAllAtoms(false);

        lastSelectedAtom = nsel;
        a.setSelected(true);
        AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
        highlightSelectedAtom(mol, atom, true);
      } // --- Select only two atoms at a time
      else if (selectedMode == SELECTION_TWO_ATOMS_ONLY) {
        // --- Is atom already selected?
        AtomInterface a = molecule.getAtomInterface(nsel);
        if (a.isSelected()) {
          return;
        }

        if (lastSelectedAtom == nsel) {
            // Do nothing
        } else if (selectedAtoms.size() == 0) {
          selectedAtoms.add(new Integer(nsel));
        } else if (selectedAtoms.size() == 1) {
          selectedAtoms.add(new Integer(nsel));
        } else if (selectedAtoms.size() >= 2) {
          //Integer lastSel = (Integer) selectedAtoms.get(1);
          // --- unselect atom
          //molecule.markAtomAsSelected(lastSel, false);
          //BranchGroup atom = (BranchGroup) atomNodes.get(lastSel);
          //highlightSelectedAtom(mol, atom, lastSel, false);
          //selectedAtoms.remove(1);
          // --- new atom in front
          //lastSel = nsel;
          //selectedAtoms.add(0, lastSel);

          selectAllAtoms(false);
          selectedAtoms.clear();
          selectedAtoms.add(new Integer(nsel));

        }

        lastSelectedAtom = nsel;
        a.setSelected(true);
        AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
        highlightSelectedAtom(mol, atom, HighlighSelectedAtoms);
      } // --- Select only three atoms at a time
      else if (selectedMode == SELECTION_THREE_ATOMS_ONLY) {
        // --- Is atom already selected?
        AtomInterface a = molecule.getAtomInterface(nsel);
        if (a.isSelected()) {
          return;
        }

        if (lastSelectedAtom == nsel) {
            // Do nothing
        } else if (selectedAtoms.size() == 0) {
          selectedAtoms.add(new Integer(nsel));
        } else if (selectedAtoms.size() == 1) {
          if (lastSelectedAtom != nsel) {
            selectedAtoms.add(new Integer(nsel));
          }
        } else if (selectedAtoms.size() == 2) {
          Integer sel = (Integer) selectedAtoms.get(0);
          if (lastSelectedAtom != nsel && sel != nsel) {
            selectedAtoms.add(new Integer(nsel));
          }
        } else if (selectedAtoms.size() >= 3) {
          selectAllAtoms(false);
          selectedAtoms.clear();
          selectedAtoms.add(new Integer(nsel));
        }

        lastSelectedAtom = nsel;
        //molecule.markAtomAsSelected(nsel, true);
        a.setSelected(true);
        AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
        //AtomNode atom = atomNodes.get(nsel);
        highlightSelectedAtom(mol, atom, HighlighSelectedAtoms);
      } // --- Select only four atoms at a time
      else if (selectedMode == SELECTION_FOUR_ATOMS_ONLY) {
        // --- Is atom already selected?
        AtomInterface a = molecule.getAtomInterface(nsel);
        if (a.isSelected()) {
          return;
        }

        if (selectedAtoms.size() >= 4) {
          selectAllAtoms(false);
          selectedAtoms.clear();
          selectedAtoms.add(new Integer(nsel));
        } else {
          selectedAtoms.add(new Integer(nsel));
        }

        lastSelectedAtom = nsel;
        //molecule.markAtomAsSelected(nsel, true);
        a.setSelected(true);
        AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
        //AtomNode atom = atomNodes.get(nsel);
        highlightSelectedAtom(mol, atom, HighlighSelectedAtoms);
      } // --- End of special cases....
      // --- General case selection
      else {
        doProperSelection(nsel);
      }
    }

    // --- Process selected atom(s) immediately if it's select&process dialog
    if (selectAndProcessDialog) {
      processSelectedAtoms();
    }
  }

  /**
   * @deprecated Deprecated function. To be deleted...
   * @param bgr BranchGroup
   * @param pickingResult PickResult
   */
  public void doAtomSelection(BranchGroup bgr, PickResult pickingResult) {
    if (pickingResult == null || bgr == null) {
      logger.info("No picking...");
      return;
    }

    BranchGroup sel_bgr = (BranchGroup) pickingResult.getNode(PickResult.BRANCH_GROUP);
    TransformGroup atg = (TransformGroup) pickingResult.getNode(PickResult.TRANSFORM_GROUP);
    if (atg != null) {
      logger.info("TransformGroup: # children" + atg.numChildren());
    }
    Primitive p = (Primitive) pickingResult.getNode(PickResult.PRIMITIVE);
    Sphere sel_sphere;

    // --- Atom (Sphere) is selected
    if (p instanceof Sphere) {
      sel_sphere = (Sphere) pickingResult.getNode(PickResult.PRIMITIVE);
      if (sel_sphere != null) {
        //int nsel = getSelectedAtomNumber(bgr, sel_sphere);
        //int nsel = atomNodes.indexOf(sel_bgr); --- Stupid stuff !!!
        int nsel = 0; // Stupid stuff !!!
        doAtomSelection(nsel);

        logger.info("Sphere: # children " + sel_sphere.numChildren());
        for (int i = 0; i < sel_sphere.numChildren(); i++) {
          Node sc = sel_sphere.getChild(i);
          logger.info("Sphere Child: " + sc.getClass().toString());
        }
        logger.info("Sphere: Selected atom: " + nsel);
      }
    } // --- Bond (cylinder) is selected
    else if (p instanceof Cylinder) {
      Cylinder c = (Cylinder) pickingResult.getNode(PickResult.PRIMITIVE);
      if (c != null) {
        logger.info("Cylinder: # children" + c.numChildren());
      }
    }
  }

  public void doProperSelection(int selected_atom) {
    // --- Error check
    if (selected_atom < 0 || selected_atom >= molecule.getNumberOfAtoms()) {
      System.err.println(getClass().getCanonicalName() + ": Internal Error: Wrong selected atom: " + selected_atom + " Ignored...");
      return;
    }

    // --- Atom selection
    if (selectionType == SELECTION_TYPE.ATOMS) {

      AtomInterface a;
      AtomNode atom;

      switch (selectionRule) {
        case UNION:
          logger.info("Selection: One Atom; Rule: Union");
          lastSelectedAtom = selected_atom;
          a = molecule.getAtomInterface(selected_atom);
          a.setSelected(true);
          atom = (AtomNode) a.getProperty(ATOM_NODE);
          highlightSelectedAtom(mol, atom, true);
          if (trackSelectedAtoms.indexOf(a) == -1) {
            trackSelectedAtoms.add(a);
          }
          break;
        //*********************************************************
        case DIFFERENCE:
          a = molecule.getAtomInterface(selected_atom);
          if (a.isSelected()) {
            a.setSelected(false);
            atom = (AtomNode) a.getProperty(ATOM_NODE);
            highlightSelectedAtom(mol, atom, false);
            trackSelectedAtoms.remove(a);
          }
          logger.info("Selection: One Atom; Rule: Difference");
          break;
        //********************************************************
        case INTERSECTION:
          a = molecule.getAtomInterface(selected_atom);
          boolean selected = a.isSelected();
          selectAllAtoms(false);
          trackSelectedAtoms.clear();
          Molecule.selectAllAtoms(molecule, false);
          if (selected) {
            a.setSelected(true);
            highlightSelectedAtom(a, true);
            //highlightSelectedAtom(selected_atom, true);
            trackSelectedAtoms.add(a);
          }
          logger.info("Selection: One Atom; Rule: Intersection");
          break;
      }

      //if (selectionRule == RULE_UNION) {
      //  logger.info("Selection: One Atom; Rule: Union");
      //  lastSelectedAtom = selected_atom;
      //  AtomInterface a = molecule.getAtomInterface(selected_atom);
      //  a.setSelected(true);
      //  AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
      //  highlightSelectedAtom(mol, atom, true);
      //  if (trackSelectedAtoms.indexOf(a) == -1) {
      //    trackSelectedAtoms.add(a);
      //  }
      //} else if (selectionRule == RULE_DIFFERENCE) {
      //  AtomInterface a = molecule.getAtomInterface(selected_atom);
      //  if (a.isSelected()) {
      //    a.setSelected(false);
      //    AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
      //    highlightSelectedAtom(mol, atom, false);
      //    trackSelectedAtoms.remove(a);
      //  }
      //  logger.info("Selection: One Atom; Rule: Difference");
      //} else if (selectionRule == RULE_INTERSECTION) {
      //  AtomInterface a = molecule.getAtomInterface(selected_atom);
      //  boolean selected = a.isSelected();
      //  selectAllAtoms(false);
      //  trackSelectedAtoms.clear();
      //  Molecule.selectAllAtoms(molecule, false);
      //  if (selected) {
      //    a.setSelected(true);
      //    highlightSelectedAtom(selected_atom, true);
      //    trackSelectedAtoms.add(a);
      //  }
      //  logger.info("Selection: One Atom; Rule: Intersection");
      //}
    } // --- Select monomers
    else if (selectionType == SELECTION_TYPE.MONOMERS) {

      AtomInterface atom;
      MonomerInterface mono;
      int n;

      switch (selectionRule) {
        case UNION:
          atom = molecule.getAtomInterface(selected_atom);
          n = atom.getSubstructureNumber();
          mono = molecule.getMonomerInterface(n);
          for (int i = 0; i < mono.getNumberOfAtoms(); i++) {
            atom = mono.getAtom(i);
            atom.setSelected(true);
            highlightSelectedAtom(atom, true);
            //n = molecule.getAtomIndex(atom);
            //highlightSelectedAtom(n, true);
            if (trackSelectedAtoms.indexOf(atom) == -1) {
              trackSelectedAtoms.add(atom);
            }
          }
          break;
        //***********************************************************
        case DIFFERENCE:
          atom = molecule.getAtomInterface(selected_atom);
          n = atom.getSubstructureNumber();
          mono = molecule.getMonomerInterface(n);
          for (int i = 0; i < mono.getNumberOfAtoms(); i++) {
            atom = mono.getAtom(i);
            if (!atom.isSelected()) {
              continue;
            }
            atom.setSelected(false);
            highlightSelectedAtom(atom, false);
            //n = molecule.getAtomIndex(atom);
            //highlightSelectedAtom(n, false);
            trackSelectedAtoms.remove(atom);
          }
          break;
        //***********************************************************
        case INTERSECTION:
          atom = molecule.getAtomInterface(selected_atom);
          n = atom.getSubstructureNumber();

          for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
            atom = molecule.getAtomInterface(i);
            if (atom.getSubstructureNumber() != n) {
              atom.setSelected(false);
              trackSelectedAtoms.remove(atom);
            }
          }
          highlightSelectedAtoms();
          break;
      }

      //if (selectionRule == RULE_UNION) {
      //  AtomInterface atom = molecule.getAtomInterface(selected_atom);
      //  int n = atom.getSubstructureNumber();
      //  MonomerInterface mono = molecule.getMonomerInterface(n);
      //  for (int i = 0; i < mono.getNumberOfAtoms(); i++) {
      //    atom = mono.getAtom(i);
      //    atom.setSelected(true);
      //    n = molecule.getAtomIndex(atom);
      //    highlightSelectedAtom(n, true);
      //    if (trackSelectedAtoms.indexOf(atom) == -1) {
      //      trackSelectedAtoms.add(atom);
      //    }
      //  }
      //} else if (selectionRule == RULE_DIFFERENCE) {
      //  AtomInterface atom = molecule.getAtomInterface(selected_atom);
      //  int n = atom.getSubstructureNumber();
      //  MonomerInterface mono = molecule.getMonomerInterface(n);
      //  for (int i = 0; i < mono.getNumberOfAtoms(); i++) {
      //    atom = mono.getAtom(i);
      //    if (!atom.isSelected()) {
      //      continue;
      //    }
      //    atom.setSelected(false);
      //    n = molecule.getAtomIndex(atom);
      //    highlightSelectedAtom(n, false);
      //    trackSelectedAtoms.remove(atom);
      //  }
      //} else if (selectionRule == RULE_INTERSECTION) {
      //  AtomInterface atom = molecule.getAtomInterface(selected_atom);
      //  int n = atom.getSubstructureNumber();
      //  for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      //    atom = molecule.getAtomInterface(i);
      //    if (atom.getSubstructureNumber() != n) {
      //      atom.setSelected(false);
      //      trackSelectedAtoms.remove(atom);
      //    }
      //  }
      //  highlightSelectedAtoms();
      //}
    } else if (selectionType == SELECTION_TYPE.MOLECULES) {

      AtomInterface atom;
      List atoms;

      switch (selectionRule) {
        case UNION:
          atom = molecule.getAtomInterface(selected_atom);
          atoms = Molecule.getMoleculeAtomBelongsTo(atom);
          for (int i = 0; i < atoms.size(); i++) {
            atom = (AtomInterface) atoms.get(i);
            atom.setSelected(true);
            highlightSelectedAtom(atom, true);
            //int n = molecule.getAtomIndex(atom);
            //highlightSelectedAtom(n, true);
            if (trackSelectedAtoms.indexOf(atom) == -1) {
              trackSelectedAtoms.add(atom);
            }
          }
          break;
        //************************************************************
        case DIFFERENCE:
          atom = molecule.getAtomInterface(selected_atom);
          atoms = Molecule.getMoleculeAtomBelongsTo(atom);
          for (int i = 0; i < atoms.size(); i++) {
            atom = (AtomInterface) atoms.get(i);
            if (!atom.isSelected()) {
              continue;
            }
            atom.setSelected(false);
            trackSelectedAtoms.remove(atom);
            highlightSelectedAtom(atom, false);
            //int n = molecule.getAtomIndex(atom);
            //highlightSelectedAtom(n, false);
          }
          break;
        //**************************************************************
        case INTERSECTION:
          atom = molecule.getAtomInterface(selected_atom);
          atoms = Molecule.getMoleculeAtomBelongsTo(atom);
          for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
            atom = molecule.getAtomInterface(i);
            if (!atoms.contains(atom)) {
              atom.setSelected(false);
              trackSelectedAtoms.remove(atom);
            }
          }
          highlightSelectedAtoms();
          break;
      }

      //if (selectionRule == RULE_UNION) {
      //  AtomInterface atom = molecule.getAtomInterface(selected_atom);
      //  java.util.List atoms = Molecule.getMoleculeAtomBelongsTo(atom);
      //  for (int i = 0; i < atoms.size(); i++) {
      //    atom = (AtomInterface) atoms.get(i);
      //    int n = molecule.getAtomIndex(atom);
      //    atom.setSelected(true);
      //    highlightSelectedAtom(n, true);
      //    if (trackSelectedAtoms.indexOf(atom) == -1) {
      //      trackSelectedAtoms.add(atom);
      //    }
      //  }
      //} else if (selectionRule == RULE_DIFFERENCE) {
      //  AtomInterface atom = molecule.getAtomInterface(selected_atom);
      //  java.util.List atoms = Molecule.getMoleculeAtomBelongsTo(atom);
      //  for (int i = 0; i < atoms.size(); i++) {
      //    atom = (AtomInterface) atoms.get(i);
      //    if (!atom.isSelected()) {
      //      continue;
      //    }
      //    atom.setSelected(false);
      //    int n = molecule.getAtomIndex(atom);
      //    trackSelectedAtoms.remove(atom);
      //    highlightSelectedAtom(n, false);
      //  }
      //} else if (selectionRule == RULE_INTERSECTION) {
      //  AtomInterface atom = molecule.getAtomInterface(selected_atom);
      //  java.util.List atoms = Molecule.getMoleculeAtomBelongsTo(atom);
      //  for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      //    atom = molecule.getAtomInterface(i);
      //    if (!atoms.contains(atom)) {
      //      atom.setSelected(false);
      //      trackSelectedAtoms.remove(atom);
      //    }
      //  }
      //  highlightSelectedAtoms();
      //}
    } else {
      System.err.println(getClass().getCanonicalName() + ": Internal Error: unknown selection type. Ignored...");
    }

  }

  public void highlighSelectedAtoms(boolean enable) {
    HighlighSelectedAtoms = enable;
    highlightSelectedAtoms();
  }

  public void highlightSelectedAtoms() {
    highlightSelectedAtoms(null);
  }

  public void highlightSelectedAtoms(JobProgressInterface progress) {
    AtomNode atom;

    if (progress != null) {
      //System.out.println("Initiating progress bar...");
      progress.setProgress(0);
    }

    int natoms = molecule.getNumberOfAtoms();

    for (int i = 0; i < natoms; i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      atom = (AtomNode) a.getProperty( ATOM_NODE);
      if (atom == null) {
        continue;
      }
      highlightSelectedAtom(mol, atom, a.isSelected());

      if (progress != null && i % 1000 == 0) {
        int progr = (int) ((double) i / (double) natoms * 100.0);
        //System.out.println("Progress done: " + progr + "%");
        progress.setProgress(progr);

        if (progress.isCanceled() && progr < 100) {
          progress.setProgressText("Stopping highlighting...");
          break;
        }
      }
    }
  }

  public void enableAtomPicking(int n, boolean enable) {
    if (n < 0 || n >= molecule.getNumberOfAtoms()) { //n >= atomNodes.size()) {
      System.err.println("enablePickingAtom: Warning: n < 0 || n >= atomNodes.size()");
      return;
    }
    AtomInterface a = molecule.getAtomInterface(n);
    AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
    //AtomNode atom = (BranchGroup) atomNodes.get(n);
    if (mol.indexOfChild(atom) == -1) {
      System.err.println("enablePickingAtom: Warning: mol.indexOfChild(atom) == -1 for " + n + " atom");
      return;
    }

    //atom.detach();
    mol.removeChild(atom);

    if (enable) {
      atom.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
      atom.setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
      atom.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
    } else {
      atom.clearCapability(BranchGroup.ENABLE_PICK_REPORTING);
    }

    mol.addChild(atom);
  }

  public void highlightSelectedAtom(AtomInterface a, boolean highlight) {
    AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
    if (mol.indexOfChild(atom) == -1) {
      return;
    }
    highlightSelectedAtom(mol, atom, highlight);
  }

  @Deprecated
  public void highlightSelectedAtom(int nsel, boolean highlight) {
    if (nsel < 0 || nsel >= molecule.getNumberOfAtoms()) { // atomNodes.size()) {
      logger.severe("nsel < 0 || nsel >= molecule.getNumberOfAtoms(): Ignoring... Continueing...");
      return;
    }
    AtomInterface a = molecule.getAtomInterface(nsel);
    AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
    //AtomNode atom = atomNodes.get(nsel);
    if (mol.indexOfChild(atom) == -1) {
      return;
    }
    highlightSelectedAtom(mol, atom, highlight);
  }

  /**
   *
   * @param parent BranchGroup
   * @param atom BranchGroup
   * @param nsel int
   * @param highlight boolean
   */
  public void highlightSelectedAtom(MoleculeNode parent, AtomNode atom, boolean enableHighlight) {

    boolean highlight = enableHighlight;

    if (!HighlighSelectedAtoms) {
      if (atom.isHighlighted()) {
        highlight = false;
      } else {
        return;
      }
    }

    if (highlight && atom.isHighlighted()) {
      return;
    }
    if (!highlight && !atom.isHighlighted()) {
      return;
    }

    parent.removeChild(atom);

    atom.highlightAtom(highlight);

    parent.addChild(atom);
  }

  /**
   * Finds and returns Sphere in the atom's BranchGroup
   *
   * @param atom BranchGroup
   * @return Sphere - atomic sphere or null
   */
  /*
   * public static Sphere getAtomicSphere(BranchGroup atom) { // --- general implementation. Assumes that Sphere is in the
   * TransformGroup for (int i = 0; i < atom.numChildren(); i++) { Node node = atom.getChild(i); if (! (node instanceof
   * TransformGroup)) { continue; } TransformGroup atom_tg = (TransformGroup) node; for (int j = 0; j < atom_tg.numChildren(); j++)
   * { Node node2 = atom_tg.getChild(j); if (node2 instanceof Sphere) { Sphere atom_sph = (Sphere) node2; return atom_sph; } else if
   * (node2 instanceof TransformGroup) { TransformGroup g2 = (TransformGroup) node2; for (int k = 0; k < g2.numChildren(); k++) {
   * Node node3 = g2.getChild(k); if (node3 instanceof Sphere) { return (Sphere) node3; } } }
   *
   * }
   * }
   * System.err.println( "getAtomicSphere: Warning: No Sphere in atom's BranchGroup"); return null;
   *
   *
   * }
   */
  /**
   * Finds TransformGroup which scales sphere's redius
   *
   * @param atom BranchGroup
   * @return TransformGroup
   */
  public static TransformGroup getAtomicSphereScale(BranchGroup atom) {
    // --- Assumes that Sphere radius is in the TransformGroup
    for (int i = 0; i < atom.numChildren(); i++) {
      Node node = atom.getChild(i);
      if (!(node instanceof TransformGroup)) {
        continue;
      }
      // Assumes that this is a MOVE transform group which contains Scale trasnform group
      TransformGroup atom_tg = (TransformGroup) node;
      for (int j = 0; j < atom_tg.numChildren(); j++) {
        Node node2 = atom_tg.getChild(j);
        if (node2 instanceof TransformGroup) {
          TransformGroup g2 = (TransformGroup) node2;
          return g2;
        }
      }
    }
    System.err.println("getAtomicSphereScale: Warning: No SCALE transform group in atom's BranchGroup");
    return null;
  }

  public TransformGroup getAtomicTranformGroup(BranchGroup atom) {
    return (TransformGroup) atom.getChild(0);
  }

  public void setAtomicSphere(BranchGroup atom, Sphere sphere) {
    TransformGroup atom_tg = (TransformGroup) atom.getChild(0);
    Sphere atom_sph = (Sphere) atom_tg.getChild(0);
    atom_tg.removeChild(atom_sph);
    atom_tg.insertChild(sphere, 0);
  }

  /**
   *
   * @param enable boolean
   */
  public void selectAllAtoms(boolean enable) {
    selectAllAtoms(enable, null);
  }

  public void selectAllAtoms(boolean enable, JobProgressInterface progress) {
    if (!enable) {
      lastSelectedAtom = -1;
      selectedAtoms.clear();
      trackSelectedAtoms.clear();
    }

    if (molecule == null) {
      return;
    }

    int natoms = molecule.getNumberOfAtoms();

    if (progress != null) {
      System.out.println("Initiating progress bar...");
      progress.setProgress(0);
    }

    for (int i = 0; i < natoms; i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      a.setSelected(enable);
      highlightSelectedAtom(a, enable);
      if (enable && trackSelectedAtoms.indexOf(a) == -1) {
        trackSelectedAtoms.add(a);
      }

      if (progress != null && i % 1000 == 0) {
        int progr = (int) ((double) i / (double) natoms * 100.0);
        System.out.println("Progress done: " + progr + "%");
        progress.setProgress(progr);

        if (progress.isCanceled() && progr < 100) {
          progress.setProgressText("Stopping selection...");
          break;
        }
      }
    }
  }

  public void invertSelectedAtoms() {
    invertSelectedAtoms(null);
  }

  /**
   *
   */
  public void invertSelectedAtoms(JobProgressInterface progress) {
    boolean highLight;
    int natoms = molecule.getNumberOfAtoms();
    if (natoms < 1) {
      return;
    }
    if (progress != null) {
      progress.setProgress(0);
    }

    for (int i = 0; i < natoms; i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      if (a.isSelected()) {
        highLight = false;
        this.trackSelectedAtoms.remove(a);
      } else {
        highLight = true;
        if (trackSelectedAtoms.indexOf(a) == -1) {
          trackSelectedAtoms.add(a);
        }
      }
      //molecule.markAtomAsSelected(i, highLight);
      a.setSelected(highLight);
      highlightSelectedAtom(a, highLight);

      if (progress != null && i % 1000 == 0) {
        int progr = (int) ((double) i / (double) natoms * 100.0);
        //System.out.println("Progress done: " + progr + "%");
        progress.setProgress(progr);

        if (progress.isCanceled() && progr < 100) {
          progress.setProgressText("Stopping selection...");
          break;
        }
      }
    }
  }

  /**
   *
   * @param enable
   */
  public void displaySelectedAtoms(boolean enable) {
    displaySelectedAtoms(enable, null);
  }

  /**
   *
   * @param enable
   * @param progress
   */
  public void displaySelectedAtoms(boolean enable, JobProgressInterface progress) {
    if (progress != null) {
      progress.setProgress(0);
      progress.setTaskDescription(enable ? "Displaying selected atoms..." : "Undisplaying selected atoms...");
    }
    int natoms = molecule.getNumberOfAtoms();

    for (int i = 0; i < natoms; i++) {
      AtomInterface at = molecule.getAtomInterface(i);
      AtomNode atom = (AtomNode) at.getProperty(ATOM_NODE);
      //BranchGroup atom = (BranchGroup) atomNodes.get(i);

      List bi = at.getBondIndex();
      //logger.info("Atom " + i + " " + atom + "  selected: " + molecule.IsAtomSelected(i) );
      if (at.isSelected() && enable) { // Display Atoms
        if (mol.indexOfChild(atom) != -1) {
          continue; // It's already visible
        }
        mol.addChild(atom);
        for (int j = 0; j < bi.size(); j++) {
          BondInterface b = (BondInterface) bi.get(j);
          int bindex = molecule.getBondIndex(b);
          if (bindex == -1) {
            continue;
          }
          BranchGroup bond = bondNodes.get(bindex);
          if (mol.indexOfChild(bond) != -1) {
            continue; // It's already visible
          }
          mol.addChild(bond);
        }

      } else if (at.isSelected() && !enable) { // Undisplay Atoms
        //logger.info("indexOfChild: " + mol.indexOfChild(atom) );
        if (mol.indexOfChild(atom) == -1) {
          continue; // It's already invisible
        }
        //atom.detach();
        mol.removeChild(atom);
        for (int j = 0; j < bi.size(); j++) {
          BondInterface b = (BondInterface) bi.get(j);
          int bindex = molecule.getBondIndex(b);
          if (bindex == -1) {
            continue;
          }
          BondNode bond = bondNodes.get(bindex);
          if (mol.indexOfChild(bond) == -1) {
            continue; // It's already invisible
          }

          //bond.detach();
          mol.removeChild(bond);
        }
      }

      if (progress != null && i % 1000 == 0) {
        progress.setProgress((int) ((double) i / (double) natoms * 100.0));
      }

    }
    //mol.compile();

    if (progress != null) {
      progress.setProgress(100);
    }
  }

  public void createdCentroidForSelectedAtoms() {
    float x = 0, y = 0, z = 0;
    float count = 0;
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface at = molecule.getAtomInterface(i);
      if (!at.isSelected()) {
        continue;
      }
      count += 1;
      x += at.getX();
      y += at.getY();
      z += at.getZ();
    }
    x /= count;
    y /= count;
    z /= count;
    AtomInterface atom = molecule.getNewAtomInstance();
    atom.setXYZ(x, y, z);
    atom.setAtomicNumber(0);
    atom.setName("Du");

    molecule.addMonomer("Centroid");
    int n = molecule.getNumberOfMonomers() - 1;
    atom.setSubstructureNumber(n);

    addAtom(atom);
  }

  public void deleteAtom(AtomInterface at) {
    int n = molecule.getAtomIndex(at);
    if (n == -1) {
      System.err.println(this.getClass().getCanonicalName() + "deleteAtom: no such atom in molecule. Ignored...");
      return;
    }
    deleteAtom(n);
  }

  public void deleteAtom(int n) {

    // --- Delete Atom Node
    AtomInterface at = molecule.getAtomInterface(n);

    List bi = at.getBondIndex();
    AtomNode atom = (AtomNode) at.getProperty(ATOM_NODE);
    //AtomNode atom = atomNodes.get(n);
    if (mol.indexOfChild(atom) != -1) {
      at.setProperty(ATOM_NODE, null);
      mol.removeChild(atom);
    } else {
      System.err.println("deleteAtom: " + "mol.indexOfChild(atom) == -1 Ignored...");
    }
    //atomNodes.remove(atom);
    //materialStore.remove(n);
    atomLabels.remove(n);

    // --- mark bond nodes for deletion
    for (int j = 0; j < bi.size(); j++) {
      BondInterface b = (BondInterface) bi.get(j);
      int bindex = molecule.getBondIndex(b);
      if (bindex == -1) {
        System.err.println("deleteAtom: " + "bindex == -1 Ignored...");
        continue;
      }

      try {
        BondNode bond = bondNodes.get(bindex);
        bond.markForDeletion(true);
      } catch (IndexOutOfBoundsException ex) {
        System.err.println("deleteAtom: " + ex.getMessage() + " Ignored...");
      }
    }

    // --- Delete marked bond nodes
    int del_bonds = 0;
    int nb = bondNodes.size();
    for (int i = nb - 1; i > -1; i--) {
      BondNode bond = bondNodes.get(i);
      if (!bond.isToDelete()) {
        continue;
      }
      bond = bondNodes.remove(i);
      if (mol.indexOfChild(bond) != -1) {
        mol.removeChild(bond);
        ++del_bonds;
      } else {
        System.err.println("deleteAtom : " + "mol.indexOfChild(bond) == -1 Ignored...");
      }
    }

    // --- Now delete "real" atoms in molecule
    molecule.deleteAtom(n);
  }

  /**
   * Deletes selected atoms and associated bonds
   */
  public void deleteSelectedAtoms() {
    deleteSelectedAtoms(null);
  }

  public void deleteSelectedAtoms(JobProgressInterface progress) {
    // --- One needs to delete separately Nodes and Atoms

    // --- Some internal error check
    int bondsOld = molecule.getNumberOfBonds();
    if (bondNodes.size() != molecule.getNumberOfBonds()) {
      System.err.println(getClass().getCanonicalName() + " : " + "bondNodes.size() != molecule.getNumberOfBonds(): "
          + bondNodes.size() + " and " + molecule.getNumberOfBonds() + " Ignored...");
    }

    if (progress != null) {
      progress.setProgress(0);
      progress.setTaskDescription("Deleting selected atoms...");
    }

    // --- Delete Atom Nodes
    int natoms = molecule.getNumberOfAtoms();

    for (int i = natoms - 1, j = 1; i >= 0; i--, j++) {
      AtomInterface at = molecule.getAtomInterface(i);
      if (!at.isSelected()) {
        continue;
      }

      deleteAtom(i);

      if (progress != null && i % 1000 == 0) {
        progress.setProgress((int) ((double) j / (double) natoms * 100.0));
      }
    }

  }

  public void unlabelSelectedAtoms() {

    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface at = molecule.getAtomInterface(i);
      //logger.info("Atom: " + i + " Selected: " + molecule.IsAtomSelected(i));
      if (!at.isSelected()) {
        continue;
      }

      //AtomNode atom = (AtomNode) atomNodes.get(i);
      //BranchGroup atom = (BranchGroup) atomNodes.get(i);
      //if (mol.indexOfChild(atom) == -1) { // Atom is invisible
      //   continue;
      //}
      LabelNode old_label = (LabelNode) atomLabels.get(i);
      if (old_label != null) {
        //atom.detach();
        old_label.detach();
        //atom.removeChild(old_label);
        //if (atom.numChildren() > 1) {
        //   atom.removeChild(1); // !!! Buggy place
        //}
        //atom.removeChild(old_label);
        //mol.addChild(atom);
      }

      atomLabels.set(i, null);
    }

    boolean noMoreLabeled = true;
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      LabelNode old_label = (LabelNode) atomLabels.get(i);
      if (old_label != null) {
        noMoreLabeled = false;
        break;
      }
    }
    if (noMoreLabeled) {
      enableLabels = false;
    }
  }

  public void labelSelectedAtoms(String property) {

    enableLabels = false;

    if (otherLabelMethod) {
      Point3d origin = new Point3d();
      canvas3D.getCenterEyeInImagePlate(origin);
      Transform3D motion = new Transform3D();
      canvas3D.getImagePlateToVworld(motion);
      motion.transform(origin);

      Point3f xyz = new Point3f();
      Vector3d dir = new Vector3d();

      // Get transform to the virtual world (VW)
      Transform3D toVW = getVWTransform();

      // --- Adding atom node
      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface at = molecule.getAtomInterface(i);
        //logger.info("Atom: " + i + " Selected: " + molecule.IsAtomSelected(i));
        if (!at.isSelected()) {
          continue;
        }

        enableLabels = true;

        String aname = null;
        if (property.equals("Element") && at.getAtomicNumber() > -1) {
          aname = ChemicalElements.getElementSymbol(at.getAtomicNumber());
        } else if (property.equals("Atom number")) {
          aname = String.valueOf(i + 1);
        } else {
          Object obj = at.getProperty(property);
          if (obj == null) {
            continue;
          }
          if (obj instanceof String) {
            aname = (String) obj;
          } else {
            aname = obj.toString();
          }
          if (aname == null) {
            continue;
          }
        }

        // --- Check for old label...
        Object old_label = atomLabels.get(i);
        if (old_label != null) {
          ((LabelNode) old_label).detach();
        }

        // --- prepare a new label
        LabelNode label = new LabelNode(new Vector3f(at.getX(), at.getY(), at.getZ()), aname);

        xyz.set(at.getX(), at.getY(), at.getZ());
        //logger.info("Local xyz: " + xyz);

        // --- Coordinates of atom in the VW
        toVW.transform(xyz);
        //logger.info("VW xyz: " + xyz);

        // --- calculate direction from view point to atom in VW
        dir.x = origin.x - xyz.x;
        dir.y = origin.y - xyz.y;
        dir.z = origin.z - xyz.z;

        // -- transform it into local space
        Transform3D t3d = new Transform3D();
        t3d.invert(toVW);
        t3d.transform(dir);
        dir.normalize();

        AtomNode atom = (AtomNode) at.getProperty(ATOM_NODE);
        //AtomNode atom = (AtomNode) atomNodes.get(i);

        float radius = atom.getAtomicSphereRadius() + 0.1f;
        dir.scale(radius);
        label.setShift(dir);

        atomLabels.set(i, label);

        mol.addChild(label);
      }

      return;
    }

    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface at = molecule.getAtomInterface(i);
      //logger.info("Atom: " + i + " Selected: " + molecule.IsAtomSelected(i));
      if (!at.isSelected()) {
        continue;
      }
      AtomNode atom = (AtomNode) at.getProperty(ATOM_NODE);
      //AtomNode atom = (AtomNode) atomNodes.get(i);
      //if (mol.indexOfChild(atom) == -1) { // Atom is invisible
      //   continue;
      //}
      enableLabels = true;

      //AtomInterface a = molecule.getAtomInterface(i);
      AtomInterface a = atom.getAtomInterface();
      String aname = null;
      if (property.equals("Element") && a.getAtomicNumber() > -1) {
        aname = ChemicalElements.getElementSymbol(a.getAtomicNumber());
      } else if (property.equals("Atom number")) {
        aname = String.valueOf(i + 1);
      } else {
        Object obj = a.getProperty(property);
        if (obj instanceof String) {
          aname = (String) obj;
        } else {
          aname = obj.toString();
        }
        if (aname == null) {
          continue;
        }
      }
      float radius = AtomNode.getAtomicSphereRadius(a);

      BranchGroup label = new BranchGroup();
      label.setCapability(BranchGroup.ALLOW_DETACH);
      //label.setCapability( BranchGroup.);

      //atom.detach();
      mol.removeChild(atom);

      LabelNode old_label = (LabelNode) atomLabels.get(i);
      if (old_label != null) {
        //atom.removeChild(1);
        mol.removeChild(old_label);
      }

      Transform3D rotate = new Transform3D();
      Transform3D move = new Transform3D();
      move.set(new Vector3f( a.getX() + radius * (float) 0.7,
          a.getY() + radius * (float) 0.7,
              a.getZ() ));

      TransformGroup rotate_label = new TransformGroup(move);
      rotate_label.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      rotate_label.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

      Text2D text;
      if (aname == null) {
        text = new Text2D("",
            new Color3f(0.9f, 1.0f, 1.0f),
            "Helvetica", 50, Font.ITALIC);
      } else {
        text = new Text2D(aname,
            new Color3f(0.9f, 1.0f, 1.0f),
            "Helvetica", 50, Font.ITALIC);
      }

      rotate_label.addChild(text);

      label.addChild(rotate_label);

      atomLabels.set(i, label);
      atom.addChild(label);
      mol.addChild(atom);
    }
  }

  public synchronized void updateLabels() {
    if (!areLabels()) {
      return;
    }

    if (temp_Matrix == null) {
      temp_Matrix = new Matrix3d();
    }

    Transform3D t3d = new Transform3D();
    Transform3D toVW = getVWTransform();
    toVW.invert();
    toVW.get(temp_Matrix);

    if (otherLabelMethod) {

      Point3d origin = new Point3d();
      canvas3D.getCenterEyeInImagePlate(origin);
      Transform3D motion = new Transform3D();
      canvas3D.getImagePlateToVworld(motion);
      motion.transform(origin);

      Point3f xyz = new Point3f();
      Vector3d dir = new Vector3d();

      // Get transform to the virtual world (VW)
      Transform3D toVWord = getVWTransform();

      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface a = molecule.getAtomInterface(i);
        AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
        //AtomNode atom = (AtomNode) atomNodes.get(i);
        //System.out.print("Atom: " + i + " indexOfChild(atom): " + mol.indexOfChild(atom));

        if (mol.indexOfChild(atom) == -1) { // Atom is invisible
          continue;
        }

        LabelNode label = (LabelNode) atomLabels.get(i);
        //logger.info(" label: " + label);
        if (label == null) {
          continue;
        }

        label.getCenterCoord(xyz);
        //logger.info("Local xyz: " + xyz);

        // --- Coordinates of atom in the VW
        toVWord.transform(xyz);
        //logger.info("VW xyz: " + xyz);

        // --- calculate direction from view point to atom in VW
        dir.x = origin.x - xyz.x;
        dir.y = origin.y - xyz.y;
        dir.z = origin.z - xyz.z;

        // -- transform it into local space
        Transform3D trans = new Transform3D();
        trans.invert(toVWord);
        trans.transform(dir);
        dir.normalize();

        float shift = label.getShift();
        dir.scale(shift);
        label.setShift(dir);

        label.detach();

        label.updateRotation(temp_Matrix);

        //atom.addChild(label);
        mol.addChild(label);

      }
      return;
    }

    // --- Old stuff
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {

      AtomInterface a = molecule.getAtomInterface(i);
      AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
      //BranchGroup atom = (BranchGroup) atomNodes.get(i);
      //System.out.print("Atom: " + i + " indexOfChild(atom): " + mol.indexOfChild(atom));

      if (mol.indexOfChild(atom) == -1) { // Atom is invisible
        continue;
      }

      BranchGroup label = (BranchGroup) atomLabels.get(i);
      //logger.info(" label: " + label);
      if (label == null) {
        continue;
      }

      label.detach();

      TransformGroup move_label = (TransformGroup) label.getChild(0);

      move_label.getTransform(t3d);
      t3d.setRotation(temp_Matrix);

      move_label.setTransform(t3d);

      atom.addChild(label);
    }

  }

  public boolean areLabels() {
    return enableLabels;
  }

  /**
   *
   * @param bgr BranchGroup
   * @param sel_sphere Sphere
   * @return int
   */
  public int getSelectedAtomNumber(BranchGroup bgr, Sphere sel_sphere) {
    int selected_atom = -1;
    if (bgr != null) {
      //PickShape shape = pickCanvas.getPickShape();
      //SceneGraphPath sgp = bgr.pickClosest(shape);
      //logger.info("Got Branch group: # children " +
      //                   bgr.numChildren());
      for (int i = 0; i < bgr.numChildren(); i++) {
        Node n = bgr.getChild(i);
        if (n instanceof BranchGroup) {
          //logger.info("Child: " + i + " :BranchGroup");
          BranchGroup bg = (BranchGroup) n;
          for (int j = 0; j < bg.numChildren(); j++) {
            Node n2 = bg.getChild(j);
            if (n2 instanceof TransformGroup) {
              TransformGroup tg2 = (TransformGroup) n2;
              //logger.info("SubChild: " + j +
              //                   " :TransformGroup");
              for (int k = 0; k < tg2.numChildren(); k++) {
                Node n3 = tg2.getChild(k);
                if (n3 instanceof Sphere) {
                  //logger.info("SubSubChild: " + k +
                  //        " :Sphere");
                  if (sel_sphere == n3) {
                    //logger.info(
                    //        ">>> Selected Sphere <<<");
                    return i;
                  }
                } else if (n3 instanceof Cylinder) {
                  //logger.info("SubSubChild: " + k +
                  //        " :Cylinder");
                } else {
                  //logger.info("SubSubChild: " + k +
                  //        " :Unknown");
                }

              }
            } else {
              //logger.info("SubChild: " + j + " :Unknown");
            }
          }
        } else {
          //logger.info("Child: " + i + " :Unknown");
        }
      }
    }

    return selected_atom;
  }

  /*
   * public Transform3D getVWTranform() { if (T3d_1 == null) { T3d_1 = new Transform3D(); } if (T3d_2 == null) { T3d_2 = new
   * Transform3D(); }
   *
   * sceneOffset.getTransform(T3d_1); T3d_2.set(T3d_1); sceneTrans.getTransform(T3d_1); T3d_2.mul(T3d_1);
   *
   * return T3d_2; }
   */
  public Transform3D getVWTransform() {
    Transform3D t3d_1 = new Transform3D();
    Transform3D t3d_2 = new Transform3D();

    sceneOffset.getTransform(t3d_1);
    t3d_2.set(t3d_1);
    sceneTrans.getTransform(t3d_1);
    t3d_2.mul(t3d_1);
    moleculeTrans.getTransform(t3d_1);
    t3d_2.mul(t3d_1);

    t3d_1 = null;
    return t3d_2;
  }

  /**
   * Transforms coordinates of atoms of the arbitrary molecule into current virtual world space
   *
   * @param molec MoleculeInterface - Molecule
   * @return float[][] - cartesian coordinates float[number_of_atoms][3]
   */
  public float[][] getVirtualWorldCoordinates(MoleculeInterface molec) {
    if (molec.getNumberOfAtoms() == 0) {
      return null;
    }
    float cartes[][] = new float[molec.getNumberOfAtoms()][3];
    Point3f xyz = new Point3f();

    // Get transform to the virtual world (VW)
    Transform3D toVW = getVWTransform();

    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      xyz.set(a.getX(), a.getY(), a.getZ());
      toVW.transform(xyz);
      cartes[i][0] = xyz.x;
      cartes[i][1] = xyz.y;
      cartes[i][2] = xyz.z;
    }
    xyz = null;
    toVW = null;
    return cartes;
  }

  /**
   * Returns Virtual World coordinates of an atom
   *
   * @param atom AtomInterface
   * @return Point3f
   */
  public Point3f getVirtualWorldCoordinates(AtomInterface atom) {

    Point3f xyz = new Point3f();

    // Get transform to the virtual world (VW)
    Transform3D toVW = getVWTransform();

    xyz.set(atom.getX(), atom.getY(), atom.getZ());
    toVW.transform(xyz);

    return xyz;
  }

  public void addAtom(AtomNode new_atom) {
    AtomInterface atom = new_atom.getAtomInterface();
    Material store = cloneMaterial(new_atom.getMaterial());
    atom.setProperty(ATOM_NODE_MATERIAL_COPY, store);
    //materialStore.add(store);

    // --- Add a "real" atom to the molecule
    if (new_atom != null) {
      mol.addChild(new_atom);
      //atomNodes.add(new_atom);
      //atomSpheres.add(getAtomicSphere(new_atom));
      atomLabels.add(null);
    }

    //molec.addAtom(atom);
    atom.setProperty(ATOM_NODE, new_atom);
    molecule.addAtom(atom, atom.getSubstructureNumber());
  }

  public AtomNode addAtom(AtomInterface atom) {

    /*
     * BranchGroup new_atom = createAtom(atom.getX(), atom.getY(), atom.getZ(), atom.getAtomicNumber()); Material store =
     * ChemicalElementsColors.getElementMaterial(atom. getAtomicNumber()); store.setCapability(Material.ALLOW_COMPONENT_READ);
     * store.setCapability(Material.ALLOW_COMPONENT_WRITE);
     */
    AtomNode new_atom = new AtomNode(atom);
    Material store = cloneMaterial(new_atom.getMaterial());
    atom.setProperty(ATOM_NODE_MATERIAL_COPY, store);
    //materialStore.add(store);

    // --- Add a "real" atom to the molecule
    if (new_atom != null) {
      mol.addChild(new_atom);
      //atomNodes.add(new_atom);
      //atomSpheres.add(getAtomicSphere(new_atom));
      atomLabels.add(null);
    }

    //molec.addAtom(atom);
    atom.setProperty(ATOM_NODE, new_atom);
    molecule.addAtom(atom, atom.getSubstructureNumber());

    return new_atom;

  }

  public AtomNode addAtom(Point3d origin, Vector3d dir, float length, boolean scale) {
    Point3f xyz = new Point3f();

    // Get transform to the virtual world (VW)
    Transform3D toVW = getVWTransform();

    // --- Adding atom node
    AtomInterface a = molecule.getAtomInterface(lastSelectedAtom);
    xyz.set(a.getX(), a.getY(), a.getZ());
    logger.info("Local xyz: " + xyz);

    // --- Coordinates of atom in the VW
    toVW.transform(xyz);
    logger.info("VW xyz: " + xyz);

    // --- knowing viewing point and direction of view we can find
    // --- intersection point of a direction vector with Z-plane of the VW
    // --- which contains a given atom
    if (dir.z == 0.0) { // View direction is parallel to the VW's Z-plane
      return null;
    }

    double t = -(-xyz.z + origin.z) / dir.z;
    xyz.x = (float) (origin.x + t * dir.x);
    xyz.y = (float) (origin.y + t * dir.y);
    xyz.z = (float) (origin.z + t * dir.z);
    logger.info("Intersection with VW Z-plane: " + xyz);

    Transform3D t3d = new Transform3D();
    t3d.invert(toVW);
    t3d.transform(xyz);

    //T3d_1.invert(toVW);
    //T3d_1.transform(xyz);
    logger.info("New point in Local space: " + xyz);

    if (scale) {
      float trueDir[] = new float[3];
      trueDir[0] = xyz.x - a.getX();
      trueDir[1] = xyz.y - a.getY();
      trueDir[2] = xyz.z - a.getZ();
      float norm = (float) Math.sqrt(trueDir[0] * trueDir[0]
          + trueDir[1] * trueDir[1]
          + trueDir[2] * trueDir[2]);
      for (int i = 0; i < 3; i++) {
        trueDir[i] /= norm;
      }
      xyz.x = a.getX() + trueDir[0] * length;
      xyz.y = a.getY() + trueDir[1] * length;
      xyz.z = a.getZ() + trueDir[2] * length;
    }

    int elementNumber = 0;
    String atomType = null;
    String atomName = null;
    if (chooseAtomType instanceof AtomTypeDialog) {
      AtomTypeDialog dlg = (AtomTypeDialog) chooseAtomType;
      elementNumber = dlg.getElementNumber();
    } else if (chooseAtomType instanceof JAtomTypeDialog) {
      JAtomTypeDialog dlg = (JAtomTypeDialog) chooseAtomType;
      elementNumber = dlg.getElement();
      atomType = dlg.getAtomType();
      atomName = dlg.getAtomName();
    } else {
      logger.info(
          "INTERNAL ERROR: Unknown instance of chooseAtomType");
    }
    /*
     * BranchGroup new_atom = createAtom(xyz.x, xyz.y, xyz.z, elementNumber); Material store =
     * ChemicalElementsColors.getElementMaterial(elementNumber);
     */

    // --- Add a "real" atom to the molecule
    AtomInterface atom = molecule.getNewAtomInstance();
    atom.setAtomicNumber(elementNumber);
    if (atomName != null && atomName.length() > 0) {
      atom.setName(atomName);
    } else {
      atom.setName(ChemicalElements.getElementSymbol(elementNumber));
    }
    atom.setXYZ(xyz.x, xyz.y, xyz.z);
    if (atomType != null) {
      atom.setProperty(AtomInterface.CCT_ATOM_TYPE, atomType);
    }
    molecule.addAtom(atom);
    //molecule.addAtom(elementNumber, Xyz.x, Xyz.y,
    //                 Xyz.z);

    AtomNode new_atom = new AtomNode(atom);
    atom.setProperty(ATOM_NODE, new_atom);

    Material store = cloneMaterial(new_atom.getMaterial());
    atom.setProperty(ATOM_NODE_MATERIAL_COPY, store);
    //materialStore.add(store);

    xyz = null;
    toVW = null;
    t3d = null;

    return new_atom;
  }

  /**
   * Adds a molecule to the main window
   *
   * @param origin Point3d
   * @param dir Vector3d
   * @param length float
   * @param scale boolean
   * @param otherUniverse Java3dUniverse
   */
  public void addMolecule(Point3d origin, Vector3d dir, float length, boolean scale, Java3dUniverse otherUniverse) {

    AtomInterface a = molecule.getAtomInterface(lastSelectedAtom);
    Point3f xyz = this.getVirtualWorldCoordinates(a);

    //xyz.set(a.getX(), a.getY(), a.getZ());
    //if (Xyz == null) {
    //Point3f xyz = new Point3f();
    //}
    // Get transform to the virtual world (VW) for main window
    //Transform3D toVW = getVWTransform();
    // --- Getting coordinates of the selected atom from the main window
    //AtomInterface a = molecule.getAtomInterface(lastSelectedAtom);
    //xyz.set(a.getX(), a.getY(), a.getZ());
    //logger.info("Local xyz: " + xyz);
    // --- Coordinates of atom in the VW
    //toVW.transform(xyz);
    logger.info("VW xyz: " + xyz);

    float x_vw = xyz.x;
    float y_vw = xyz.y;
    float z_vw = xyz.z;

    // --- knowing viewing point and direction of view we can find
    // --- intersection point of a direction vector with Z-plane of the VW
    // --- which contains a given atom
    if (dir.z == 0.0) { // View direction is parallel to the VW's Z-plane
      System.err.println("dir.z == 0.0");
      return;
    }

    double t = -(-xyz.z + origin.z) / dir.z;
    xyz.x = (float) (origin.x + t * dir.x);
    xyz.y = (float) (origin.y + t * dir.y);
    xyz.z = (float) (origin.z + t * dir.z);
    logger.info("Intersection with VW Z-plane: " + xyz);

    // --- Scale distance
    if (scale) {
      float trueDir[] = new float[3];
      trueDir[0] = xyz.x - x_vw;
      trueDir[1] = xyz.y - y_vw;
      trueDir[2] = xyz.z - z_vw;
      float norm = (float) Math.sqrt(trueDir[0] * trueDir[0]
          + trueDir[1] * trueDir[1]
          + trueDir[2] * trueDir[2]);
      for (int i = 0; i < 3; i++) {
        trueDir[i] /= norm;
      }
      xyz.x = x_vw + trueDir[0] * length;
      xyz.y = y_vw + trueDir[1] * length;
      xyz.z = z_vw + trueDir[2] * length;
    }

    addMolecule(xyz, otherUniverse);

    // Now translate a molecule to be added into Xyz

    /*
     * AtomInterface otherAtom = otherMol.getAtomInterface(otherSelected); float x = virtualXYZ[otherSelected][0]; float y =
     * virtualXYZ[otherSelected][1]; float z = virtualXYZ[otherSelected][2]; for (int i = 0; i < otherMol.getNumberOfAtoms(); i++) {
     * virtualXYZ[i][0] += Xyz.x - x; virtualXYZ[i][1] += Xyz.y - y; virtualXYZ[i][2] += Xyz.z - z; }
     *
     * // --- transform other molecule into the local coordinates of a main window
     *
     * T3d_1.invert(toVW);
     *
     * for (int i = 0; i < otherMol.getNumberOfAtoms(); i++) { Xyz.x = virtualXYZ[i][0]; Xyz.y = virtualXYZ[i][1]; Xyz.z =
     * virtualXYZ[i][2];
     *
     * T3d_1.transform(Xyz);
     *
     * virtualXYZ[i][0] = Xyz.x; virtualXYZ[i][1] = Xyz.y; virtualXYZ[i][2] = Xyz.z; }
     *
     * logger.info("New point in Local space: " + Xyz);
     *
     * appendMolecule(molecule, otherMol, virtualXYZ);
     */
  }

  float[][] transformToLocalCoords(Point3f origin, Java3dUniverse otherUniverse) {
    int otherSelected = otherUniverse.getLastSelectedAtom();
    if (otherSelected == -1) {
      System.err.println("transformToLocalCoords: otherSelected == -1");
      return null;
    }

    // --- Get VW coordinates of a molecule to be added
    MoleculeInterface otherMol = otherUniverse.getMoleculeInterface();
    float virtualXYZ[][] = otherUniverse.getVirtualWorldCoordinates(otherMol);

    // Now translate a molecule to be added into origin
    AtomInterface otherAtom = otherMol.getAtomInterface(otherSelected);
    float x = virtualXYZ[otherSelected][0];
    float y = virtualXYZ[otherSelected][1];
    float z = virtualXYZ[otherSelected][2];
    for (int i = 0; i < otherMol.getNumberOfAtoms(); i++) {
      virtualXYZ[i][0] += origin.x - x;
      virtualXYZ[i][1] += origin.y - y;
      virtualXYZ[i][2] += origin.z - z;
    }

    // Get transform to the virtual world (VW) for main window
    Transform3D toVW = getVWTransform();

    // --- transform other molecule into the local coordinates of a main window
    Transform3D t3d = new Transform3D();
    t3d.invert(toVW);

    Point3f xyz = new Point3f();

    for (int i = 0; i < otherMol.getNumberOfAtoms(); i++) {
      xyz.x = virtualXYZ[i][0];
      xyz.y = virtualXYZ[i][1];
      xyz.z = virtualXYZ[i][2];

      t3d.transform(xyz);

      virtualXYZ[i][0] = xyz.x;
      virtualXYZ[i][1] = xyz.y;
      virtualXYZ[i][2] = xyz.z;
    }

    logger.info("New point in Local space: " + xyz);
    xyz = null;
    toVW = null;
    t3d = null;

    return virtualXYZ;
  }

  public void addMolecule(Point3f origin, Java3dUniverse otherUniverse) {

    int otherSelected = otherUniverse.getLastSelectedAtom();
    if (otherSelected == -1) {
      System.err.println("addMolecule: otherSelected == -1");
      return;
    }

    MoleculeInterface otherMol = otherUniverse.getMoleculeInterface();
    float virtualXYZ[][] = transformToLocalCoords(origin, otherUniverse);

    appendMolecule(molecule, otherMol, virtualXYZ);
  }

  public void addFragment() {
    if (this.otherRenderer == null) {
      JOptionPane.showMessageDialog(null, "Internal Error: other renderer is not set!",
          "Internal Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      try {
        addFragment(otherRenderer);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error Adding a fragment: "
            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
      return;
    } else if (lastSelectedAtom == -1) {
      //System.err.println("addFragment: lastSelectedAtom == -1");
      return;
    }

    try {
      addFragment(otherRenderer);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
          "Error Adding a fragment: " + ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  /**
   * Adds a fragment to a molecule
   *
   * @param otherUniverse Java3dUniverse
   * @throws Exception
   */
  public void addFragment(Java3dUniverse otherUniverse) throws Exception {

    if (otherUniverse == null || otherUniverse.getMolecule() == null
        || otherUniverse.getMolecule().getNumberOfAtoms() < 1) {
      System.err.println(
          "addFragment: otherUniverse == null || otherUniverse.getMolecule() == null ||  otherUniverse.getMolecule().getNumberOfAtoms() < 1");
      throw new Exception("No atoms to add");
    }

    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      MoleculeInterface fragment = otherUniverse.getMoleculeInterface().getInstance();
      fragment.appendMolecule(otherUniverse.getMoleculeInterface());
      this.addMolecule(fragment);
      this.enableMousePicking(true);
      //appendMolecule(otherUniverse.getMoleculeInterface());
      mol.setPickable(true);
      //addMousePicking(this, mol, canvas3D, bounds);
      return;
    }

    if (lastSelectedAtom == -1) {
      System.err.println("addFragment: lastSelectedAtom == -1");
      throw new Exception("Select atom in the main window first!");
    }

    int otherSelected = otherUniverse.getLastSelectedAtom();
    /*
     * if (otherSelected == -1) { System.err.println("addFragment: otherSelected == -1"); throw new Exception("addFragment:
     * otherSelected == -1"); }
     */

    // --- Get VW coordinates of a molecule to be added
    MoleculeInterface fragment = otherUniverse.getMoleculeInterface().getInstance();
    fragment.appendMolecule(otherUniverse.getMoleculeInterface());

    AtomInterface otherAtom = fragment.getAtomInterface(otherSelected);

    if (otherAtom.getNumberOfBondedAtoms() > 1 && lastSelectedAtom != -1) {
      System.err.println("addFragment: selected atom in fragment should have only one neighbour");
      throw new Exception("Select atom in Fragment/Molecule first!");
    }

    // --- Getting coordinates of the selected atom from the main window
    AtomInterface atom = molecule.getAtomInterface(lastSelectedAtom);
    if (atom.getNumberOfBondedAtoms() > 1) {
      System.err.println("addFragment: selected atom in main window should have only one neighbour");
      throw new Exception("addFragment: selected atom in main wondow should have only one neighbour");
    }

    // --- Consider particular cases....
    // --- Both selected atoms have no neighbours...
    // So, they are just connected with a covalent bond
    if (otherAtom.getNumberOfBondedAtoms() == 0 && atom.getNumberOfBondedAtoms() == 0) {
      return;
      //addMolecule(Point3f origin, otherUniverse);
    } // --- Atom in main window has no neighbours...
    else if (otherAtom.getNumberOfBondedAtoms() == 1 && atom.getNumberOfBondedAtoms() == 0) {

      List bonded = otherAtom.getBondedToAtoms();
      AtomInterface otherParent = (AtomInterface) bonded.get(0);
      int otherParentIndex = fragment.getAtomIndex(otherParent);

      // --- Translate fragment
      // First, preserve orientation of the fragment
      Point3f origin = new Point3f(atom.getX(), atom.getY(), atom.getZ());
      float virtualXYZ[][] = transformToLocalCoords(origin, otherUniverse);
      //for (int i=0; i<fragment.getNumberOfAtoms(); i++) {
      //   AtomInterface at = fragment.getAtomInterface(i);
      //   at.setXYZ(virtualXYZ[i][0],virtualXYZ[i][1],virtualXYZ[i][2]);
      //}
      otherAtom.setXYZ(virtualXYZ[otherSelected][0], virtualXYZ[otherSelected][1], virtualXYZ[otherSelected][2]);
      otherParent.setXYZ(virtualXYZ[otherParentIndex][0], virtualXYZ[otherParentIndex][1], virtualXYZ[otherParentIndex][2]);

      // --- Now translate it so that an atom in the main window is on the
      // bond vector of the fragment
      float length = ChemicalElements.getCovalentRadius(otherParent.getAtomicNumber())
          + ChemicalElements.getCovalentRadius(atom.getAtomicNumber());
      float bondLength = (float) otherParent.distanceTo(otherAtom);

      float x_dir = (otherParent.getX() - otherAtom.getX()) / bondLength
          * length;
      float y_dir = (otherParent.getY() - otherAtom.getY()) / bondLength
          * length;
      float z_dir = (otherParent.getZ() - otherAtom.getZ()) / bondLength
          * length;

      for (int i = 0; i < fragment.getNumberOfAtoms(); i++) {
        AtomInterface a = fragment.getAtomInterface(i);
        a.setX(atom.getX() + virtualXYZ[i][0]
            - virtualXYZ[otherParentIndex][0] + x_dir);
        a.setY(atom.getY() + virtualXYZ[i][1]
            - virtualXYZ[otherParentIndex][1] + y_dir);
        a.setZ(atom.getZ() + virtualXYZ[i][2]
            - virtualXYZ[otherParentIndex][2] + z_dir);
      }

      // --- Delete selected atoms
      fragment.deleteAtom(otherSelected);
      //deleteSelectedAtoms();

      int oldAtNum = molecule.getNumberOfAtoms();

      this.appendMolecule(fragment, false);

      // --- Add bond between atoms
      if (otherParentIndex > otherSelected) {
        --otherParentIndex;
      }
      AtomInterface newAtom = molecule.getAtomInterface(oldAtNum
          + otherParentIndex);
      BondInterface bond = molecule.getNewBondInstance(atom, newAtom);
      addBond(bond);

    } // --- Both selected atoms have 1 neighbour...
    else if (otherAtom.getNumberOfBondedAtoms() == 1 && atom.getNumberOfBondedAtoms() == 1) {

      List bonded = otherAtom.getBondedToAtoms();
      AtomInterface otherParent = (AtomInterface) bonded.get(0);
      int otherParentIndex = fragment.getAtomIndex(otherParent);

      List neighb = atom.getBondedToAtoms();
      AtomInterface parent = (AtomInterface) neighb.get(0);

      cct.vecmath.Point3f otherAxis = new cct.vecmath.Point3f(otherAtom, otherParent);
      cct.vecmath.Point3f axis = new cct.vecmath.Point3f(parent, atom);

      cct.vecmath.Point3f rotAxis = axis.crossProduct(otherAxis);

      if (rotAxis.vectorNorm() < 0.00001f) { // So, they are already co-linear

        if (axis.product(otherAxis) < 0) { // Vectors have opposite direction
          // Invert coordinates of the fragment
          for (int i = 0; i < fragment.getNumberOfAtoms(); i++) {
            AtomInterface a = fragment.getAtomInterface(i);
            a.setXYZ(-a.getX(), -a.getY(), -a.getZ());
          }
        }
        //JOptionPane.showMessageDialog(null,
        //                              "addFragment: Rotation Axis cannot be determined",
        //                              "Error",
        //                              JOptionPane.ERROR_MESSAGE);
        //return;
      } else {

        float angle = angleBetween( (Point3fInterface) axis, otherAxis );

        // --- Rotate fragment to align both main window and fragment bonds
        Molecule.rotateMoleculeAroundAxis(fragment, -angle, rotAxis.getInstance(0, 0, 0), rotAxis);
      }

      // --- Translate fragment
      float length = ChemicalElements.getCovalentRadius(otherParent.getAtomicNumber())
          + ChemicalElements.getCovalentRadius(parent.getAtomicNumber());
      float bondLength = (float) parent.distanceTo(atom);
      float x_dir = (atom.getX() - parent.getX()) / bondLength * length;
      float y_dir = (atom.getY() - parent.getY()) / bondLength * length;
      float z_dir = (atom.getZ() - parent.getZ()) / bondLength * length;

      Point3f origin = new Point3f(parent.getX() + x_dir,
          parent.getY() + y_dir,
          parent.getZ() + z_dir);

      float x = otherParent.getX();
      float y = otherParent.getY();
      float z = otherParent.getZ();

      for (int i = 0; i < fragment.getNumberOfAtoms(); i++) {
        AtomInterface a = fragment.getAtomInterface(i);
        a.setX(origin.x + a.getX() - x);
        a.setY(origin.y + a.getY() - y);
        a.setZ(origin.z + a.getZ() - z);
      }

      // --- Delete selected atoms
      fragment.deleteAtom(otherSelected);
      deleteSelectedAtoms();

      int oldAtNum = molecule.getNumberOfAtoms();

      this.appendMolecule(fragment, false);

      // --- Add bond between atoms
      if (otherParentIndex > otherSelected) {
        --otherParentIndex;
      }
      AtomInterface newAtom = molecule.getAtomInterface(oldAtNum + otherParentIndex);
      BondInterface bond = molecule.getNewBondInstance(parent, newAtom);
      addBond(bond);

      //addMolecule(Point3f origin, otherUniverse);
    }

  }

  public BondNode addBond(BondInterface bond) {
    molecule.addBond(bond);
    BondNode bn = new BondNode(bond);
    mol.addChild(bn);
    bondNodes.add(bn);
    return bn;
  }

  public BondNode createBond(int origin, int target) {
    BondNode bn = null;
    AtomInterface a_i = molecule.getAtomInterface(origin);
    AtomInterface a_j = molecule.getAtomInterface(target);
    BondInterface bond = molecule.getNewBondInstance(a_i, a_j);
    molecule.addBond(bond);
    //Bond b = molecule.addBondBetweenAtoms(origin, target);
    if (bond == null) {
      return null;
    }
    bn = new BondNode(bond);
    return bn;
  }

  public void makeMonomersSelection(Object monomers[]) {
    makeMonomersSelection(monomers, null);
  }

  public void makeMonomersSelection(Object monomers[], JobProgressInterface progress) {
    if (monomers == null || monomers.length < 1) {
      logger.warning("monomers == null || monomers.length < 1");
      return;
    }

    StringBuilder sb = null;
    if (logger.isLoggable(Level.INFO)) {
      sb = new StringBuilder();
    }

    int natoms = molecule.getNumberOfAtoms();

    boolean selectedAtoms[] = new boolean[natoms];
    for (int i = 0; i < selectedAtoms.length; i++) {
      selectedAtoms[i] = false;
    }

    for (int i = 0; i < molecule.getNumberOfMonomers(); i++) {
      MonomerInterface m = molecule.getMonomerInterface(i);
      String monName = m.getName();
      for (int j = 0; j < monomers.length; j++) {
        if (monName.compareToIgnoreCase(monomers[j].toString()) == 0) {
          if (logger.isLoggable(Level.INFO)) {
            sb.append(monName + String.valueOf(i + 1) + "\n");
          }
          for (int k = 0; k < m.getNumberOfAtoms(); k++) {
            AtomInterface atom = m.getAtom(k);
            int n = molecule.getAtomIndex(atom);
            if (n != -1) {
              selectedAtoms[n] = true;
            }
          }
          break;
        }
      }
    }

    makeSelection(selectedAtoms, progress);

    if (logger.isLoggable(Level.INFO)) {
      logger.log(Level.INFO, "Selected monomers:\n" + sb.toString());
    }
  }

  public void makeMonomersSelection(int monomers[]) {
    makeMonomersSelection(monomers, null);
  }

  public void makeMonomersSelection(int monomers[], JobProgressInterface progress) {
    if (monomers == null || monomers.length < 1) {
      return;
    }

    boolean selectedAtoms[] = new boolean[molecule.getNumberOfAtoms()];
    for (int i = 0; i < selectedAtoms.length; i++) {
      selectedAtoms[i] = false;
    }

    for (int i = 0; i < monomers.length; i++) {
      MonomerInterface m = molecule.getMonomerInterface(monomers[i]);
      for (int k = 0; k < m.getNumberOfAtoms(); k++) {
        AtomInterface atom = m.getAtom(k);
        int n = molecule.getAtomIndex(atom);
        if (n != -1) {
          selectedAtoms[n] = true;
        }
      }
    }

    makeSelection(selectedAtoms, progress);
  }

  /**
   *
   * @param elements Object[]
   * @param atom_names Object[]
   */
  public void makeSelection(Object elements[], Object atom_names[]) {
    makeSelection(elements, atom_names, null);
  }

  public void makeSelection(Object elements[], Object atom_names[], JobProgressInterface progress) {

    if (elements == null && atom_names == null) {
      return;
    }
    if (elements.length == 0 && atom_names.length == 0) {
      return;
    }

    int natoms = molecule.getNumberOfAtoms();

    boolean selectedAtoms[] = new boolean[natoms];
    for (int i = 0; i < selectedAtoms.length; i++) {
      selectedAtoms[i] = false;
    }

    boolean select_elements = elements != null && elements.length > 0;
    boolean select_atom_names = atom_names != null && atom_names.length > 0;

    // --- Process selected elements
    if (select_elements) {
      int elems[] = new int[elements.length];
      for (int i = 0; i < elements.length; i++) {
        String element = (String) elements[i];
        elems[i] = ChemicalElements.getAtomicNumber(element);
      }

      for (int i = 0; i < selectedAtoms.length; i++) {
        AtomInterface atom = molecule.getAtomInterface(i);
        for (int j = 0; j < elements.length; j++) {
          if (atom.getAtomicNumber() == elems[j]) {
            selectedAtoms[i] = true;
            break;
          }
        }
      }
    }

    // --- process selected atom names
    if (select_atom_names) {
      for (int i = 0; i < selectedAtoms.length; i++) {
        AtomInterface atom = molecule.getAtomInterface(i);
        String atomName = atom.getName();
        for (int j = 0; j < atom_names.length; j++) {
          if (atomName.compareToIgnoreCase(atom_names[j].toString()) == 0) {
            selectedAtoms[i] = true;
            break;
          }
        }
      }
    }

    makeSelection(selectedAtoms, progress);
  }

  /**
   *
   * @param selectedAtoms boolean[]
   */
  public void makeSelection(boolean selectedAtoms[]) {
    makeSelection(selectedAtoms, null);
  }

  public void makeSelection(boolean selectedAtoms[], JobProgressInterface progress) {

    // --- Error check
    if (molecule.getNumberOfAtoms() != selectedAtoms.length) {
      logger.info("INTERNAL ERROR: makeSelection: molecule.getNumberOfAtoms() != selectedAtoms.length");
      JOptionPane.showMessageDialog(new JFrame(),
          "INTERNAL ERROR: makeSelection: molecule.getNumberOfAtoms() != selectedAtoms.length",
          "Internal Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Make selection
    switch (selectionRule) {
      case UNION:
        for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
          if (selectedAtoms[i]) {
            AtomInterface atom = molecule.getAtomInterface(i);
            atom.setSelected(true);
            if (trackSelectedAtoms.indexOf(atom) == -1) {
              trackSelectedAtoms.add(atom);
            }
          }
        }
        break;
      //*****************************************************
      case DIFFERENCE:
        for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
          AtomInterface atom = molecule.getAtomInterface(i);
          if (!selectedAtoms[i]) {
            continue;
          }
          if (atom.isSelected()) {
            atom.setSelected(false);
            trackSelectedAtoms.remove(atom);
          }
        }
        break;
      //**********************************************************
      case INTERSECTION:
        for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
          AtomInterface atom = molecule.getAtomInterface(i);
          if (selectedAtoms[i] && atom.isSelected()) {
            continue;
          }
          atom.setSelected(false);
          trackSelectedAtoms.remove(atom);
        }
        break;
    }

    //if (selectionRule == RULE_UNION) {
    //  for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
    //    if (selectedAtoms[i]) {
    //      AtomInterface atom = molecule.getAtomInterface(i);
    //      atom.setSelected(true);
    //      if (trackSelectedAtoms.indexOf(atom) == -1) {
    //        trackSelectedAtoms.add(atom);
    //      }
    //    }
    //  }
    //} else if (selectionRule == RULE_DIFFERENCE) {
    //  for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
    //    AtomInterface atom = molecule.getAtomInterface(i);
    //    if (!selectedAtoms[i]) {
    //      continue;
    //    }
    //    if (atom.isSelected()) {
    //      atom.setSelected(false);
    //      trackSelectedAtoms.remove(atom);
    //    }
    //  }
    //}
    //if (selectionRule == RULE_INTERSECTION) {
    //  for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
    //    AtomInterface atom = molecule.getAtomInterface(i);
    //    if (selectedAtoms[i] && atom.isSelected()) {
    //      continue;
    //    }
    //    atom.setSelected(false);
    //    trackSelectedAtoms.remove(atom);
    //  }
    //}
    highlightSelectedAtoms(progress);
  }

  public void endProcessingSelectedAtoms() {

    switch (processingSelected) {
      case SELECTED_ADD_ATOMS:
        selectAllAtoms(false);
        break;

      case SELECTED_MODIFY_BONDS:
        temp_Group_1 = null;
        temp_Group_2 = null;
        break;

      case SELECTED_MODIFY_ANGLES:
        temp_Group_1 = null;
        temp_Group_2 = null;
        temp_Group_3 = null;
        temp_axis = null; // Rotation axis
        break;

      case SELECTED_MODIFY_DIHEDRALS:
        temp_Group_1 = null;
        temp_Group_2 = null;
        temp_axis = null; // Rotation axis
        break;
      case SELECTED_ADD_MOLECULE:
        selectAllAtoms(false);
        break;

    }

    selectAllAtoms(false);
    enableMousePicking(false);
    lastSelectedAtom = -1; // Reset
    trackSelectedAtoms.clear();
  }

  public void processSelectedAtoms() {
    processSelectedAtoms(null);
  }

  /**
   *
   */
  public void processSelectedAtoms(JobProgressInterface progress) {
    switch (processingSelected) {

      case SELECTED_DISPLAY_ATOMS:
        displaySelectedAtoms(true, progress);
        endProcessingSelectedAtoms();
        break;
      case SELECTED_UNDISPLAY_ATOMS:
        displaySelectedAtoms(false, progress);
        endProcessingSelectedAtoms();
        break;
      case SELECTED_DELETE_ATOMS:
        deleteSelectedAtoms(progress);
        endProcessingSelectedAtoms();
        break;
      case SELECTED_LABEL_ATOMS:
        List props = Molecule.getAvailableAtomicProperties(molecule);
        props.add("Atom number");
        props.add("Element");
        if (atomLabelsDialog == null) {
          atomLabelsDialog = new AtomLabelsDialog(new Frame(), "Select Atom Labels", props, true);
          atomLabelsDialog.setLocationByPlatform(true);
        }
        atomLabelsDialog.setProperties(props);
        atomLabelsDialog.setVisible(true);
        if (atomLabelsDialog.pressedOK()) {
          String property = atomLabelsDialog.getSelectedItem();
          labelSelectedAtoms(property);
        }
        endProcessingSelectedAtoms();
        break;

      case SELECTED_UNLABEL_ATOMS:
        unlabelSelectedAtoms();
        endProcessingSelectedAtoms();
        break;

      case SELECTED_CHANGE_ATOM_LABELS_SIZE:
        DoubleSpinnerDialog dial = new DoubleSpinnerDialog(new Frame(), "Choose Value", true,
            (double) atomLabelsFontSize * (double) referenceText2D.getRectangleScaleFactor(), 0.1, 2.0, 0.05);
        dial.setLocationByPlatform(true);
        dial.setVisible(true);
        if (dial.isOKPressed()) {
          double value = dial.getValue();
          updateSelectedAtomLabelsSize(value);
          endProcessingSelectedAtoms();
        }
        break;

      case SELECTED_CHANGE_ATOM_LABELS_COLOR:
        Color c = JColorChooser.showDialog(new Frame(), "Choose Atom Labels Color", this.getAtomLabelsColor());
        if (c != null) {
          updateSelectedAtomLabelsColor(c);
        }
        endProcessingSelectedAtoms();
        break;

      case SELECTED_MODIFY_ATOMS:
        setupModifyAtomDialog();

        //selectAllAtoms(false);
        //enableMousePicking(false);
        break;

      case SELECTED_MODIFY_BONDS:
        if (selectedAtoms.size() == 2) {
          setupModifyBondDialog();
          enableMousePicking(false);
          temp_Group_1 = null;
          temp_Group_2 = null;
          if (temp_structure == null
              || temp_structure.length < molecule.getNumberOfAtoms()) {
            temp_structure = new cct.vecmath.Point3f[molecule.getNumberOfAtoms()];
          }
          Molecule.getCoordinates(molecule, temp_structure);
        }
        break;

      case SELECTED_MODIFY_ANGLES:
        if (selectedAtoms.size() == 3) {
          setupModifyAngleDialog();
          enableMousePicking(false);
          temp_Group_1 = null;
          temp_Group_2 = null;
          temp_Group_3 = null;
          temp_axis = null; // Rotation axis
          if (temp_structure == null
              || temp_structure.length < molecule.getNumberOfAtoms()) {
            temp_structure = new cct.vecmath.Point3f[molecule.getNumberOfAtoms()];
          }
          Molecule.getCoordinates(molecule, temp_structure);
        }
        break;

      case SELECTED_FILL_VALENCES_WITH_HYDROGENS:

        //molecule.setBondInterface(new Bond());
        Atom hydrogen = new Atom();
        hydrogen.setAtomicNumber(1);
        hydrogen.setName("H");
        hydrogen.setProperty(AtomInterface.CCT_ATOM_TYPE, "H");
        fillEmptyValences(hydrogen);
        endProcessingSelectedAtoms();
        break;

      case SELECTED_MODIFY_DIHEDRALS:
        if (selectedAtoms.size() == 4) {
          setupModifyDihedralDialog();
          enableMousePicking(false);
          temp_Group_1 = null;
          temp_Group_2 = null;
          temp_axis = null; // Rotation axis
          if (temp_structure == null
              || temp_structure.length < molecule.getNumberOfAtoms()) {
            temp_structure = new cct.vecmath.Point3f[molecule.getNumberOfAtoms()];
          }
          Molecule.getCoordinates(molecule, temp_structure);
        }
        break;

      case SELECTED_ADD_ATOMS:
        break;

      case SELECTED_ADD_MOLECULE:
        break;

      case SELECTED_ADD_FRAGMENT:
        addFragment();
        selectAllAtoms(false);

        //endProcessingSelectedAtoms();
        break;
      case SELECTED_CREATE_CENTROID:
        createdCentroidForSelectedAtoms();
        endProcessingSelectedAtoms();
        break;
      case SELECTED_SOLVATE_CAP:
      case SELECTED_SOLVATE_SHELL:
        openSolvateMoleculeDialog(processingSelected);
        endProcessingSelectedAtoms();
        break;

      case SELECTED_ROTATION_CENTER:
        setRotationCenterOnSelectedAtoms();
        endProcessingSelectedAtoms();
        break;

      default:
        endProcessingSelectedAtoms();
    }

  }

  public void handleSpotPicking(Point3d origin, Vector3d dir) {
    if (origin == null || dir == null) {
      logger.info(
          "handleSpotPicking: origin == null || dir == null");
      return;
    }

    // --- Special cases:
    if (processingSelected == SELECTED_ADD_ATOMS
        && molecule.getNumberOfAtoms() < 1) {
      int elementNumber = 0;
      String atomType = null;
      String atomName = null;
      if (chooseAtomType instanceof AtomTypeDialog) {
        AtomTypeDialog dlg = (AtomTypeDialog) chooseAtomType;
        elementNumber = dlg.getElementNumber();
      } else if (chooseAtomType instanceof JAtomTypeDialog) {
        JAtomTypeDialog dlg = (JAtomTypeDialog) chooseAtomType;
        elementNumber = dlg.getElement();
        atomType = dlg.getAtomType();
        if (dlg.getAtomName().length() < 1) {
          atomName = ChemicalElements.getElementSymbol(elementNumber);
        } else {
          atomName = dlg.getAtomName();
        }
      }

      AtomInterface atom = molecule.getNewAtomInstance();
      //molecule.addMonomer("no-name");
      atom.setAtomicNumber(elementNumber);
      atom.setName(atomName);
      atom.setXYZ(0, 0, 0);
      //atom.setSubstructureNumber(0);
      if (atomType != null) {
        atom.setProperty(AtomInterface.CCT_ATOM_TYPE, atomType);
      }
      //molecule.addAtom(atom);
      //this.addMolecule(molecule);

      MoleculeInterface mol = molecule.getInstance();
      mol.addAtom(atom);
      this.addMolecule(mol); //.addAtom(atom);
      enableMousePicking(true);
      return;
    }

    // --- Error check
    if (lastSelectedAtom == -1
        || lastSelectedAtom >= molecule.getNumberOfAtoms()) {
      logger.info("lastSelectedAtom == -1 || lastSelectedAtom >= molecule.getNumberOfAtoms()");
      return;
    }

    boolean scale = false;
    float length = 1.0f;

    switch (processingSelected) {
      case SELECTED_ADD_ATOMS:

        boolean drawBond = false;
        boolean useBondLength = false;
        boolean covalentBondLength = false;
        String bl = null;
        String atomType = null;
        int element = 0;

        if (chooseAtomType instanceof AtomTypeDialog) {
          AtomTypeDialog dlg = (AtomTypeDialog) chooseAtomType;
          useBondLength = dlg.isUseBondLength();
          if (useBondLength) {
            bl = dlg.getBondLength();
          }
        } else if (chooseAtomType instanceof JAtomTypeDialog) {
          JAtomTypeDialog dlg = (JAtomTypeDialog) chooseAtomType;
          drawBond = dlg.isDrawBond();
          useBondLength = dlg.isUseBondLength();
          covalentBondLength = dlg.isCovalentBondLength();
          if (useBondLength) {
            bl = dlg.getBondLength();
          }
          atomType = dlg.getAtomType();
          element = dlg.getElement();
        } else {
          logger.info(
              "INTERNAL ERROR: Unknown instance of chooseAtomType");
        }

        if (useBondLength) {
          //bl = chooseAtomType.getBondLength();
          try {
            length = Float.parseFloat(bl);
          } catch (NumberFormatException e) {
            MessageWindow d = new MessageWindow("Error", "Bond length " + bl + " is not a real number", true);
            d.setVisible(true);
            //logger.info( "Error converting cartesin coordinates");
            return;
          }
          scale = true;
        } else if (covalentBondLength) {
          scale = true;
          AtomInterface a1 = molecule.getAtomInterface(lastSelectedAtom);
          if (a1.getProperty(AtomInterface.CCT_ATOM_TYPE) == null
              || atomType == null) {
            length = ChemicalElements.guessCovalentBondLength(a1.getAtomicNumber(), element);
          } else if (CCTAtomTypes.isValidCCTType(atomType) && CCTAtomTypes.isValidCCTType((String) a1.getProperty(
              AtomInterface.CCT_ATOM_TYPE))) {
            length = SimpleForceField.guessCovalentBondLength(atomType, (String) a1.getProperty(AtomInterface.CCT_ATOM_TYPE));
          } else {
            length = ChemicalElements.guessCovalentBondLength(a1.getAtomicNumber(), element);
          }

          if (length < 0.1) {
            System.err.println("Covalent bond is too small. Ignoring...");
            scale = false;
          }
        }

        AtomNode new_atom = addAtom(origin, dir, length, scale);
        if (new_atom != null) {
          mol.addChild(new_atom);
          //atomNodes.add(new_atom);
          atomLabels.add(null);
        }

        if (chooseAtomType instanceof AtomTypeDialog) {
          AtomTypeDialog dlg = (AtomTypeDialog) chooseAtomType;
          drawBond = dlg.isDrawBond();
        } else if (chooseAtomType instanceof JAtomTypeDialog) {
          JAtomTypeDialog dlg = (JAtomTypeDialog) chooseAtomType;
          drawBond = dlg.isDrawBond();
        } else {
          logger.info(
              "INTERNAL ERROR: Unknown instance of chooseAtomType");
        }

        if (drawBond && new_atom != null) {
          BondNode bond = createBond(lastSelectedAtom,
              molecule.getNumberOfAtoms() - 1);
          if (bond != null) {
            mol.addChild(bond);
            bondNodes.add(bond);
          }
        }

        /*
         * if ( oneAtomSelectionDialog.pressedOK() ) { enableMousePicking( true ); selectedMode = SELECTION_ONE_ATOM_ONLY;
         * processingSelected = SELECTED_ADD_ATOMS; oneAtomSelectionDialog.setMessage("Select Atom to Connect to");
         * oneAtomSelectionDialog.setVisible(true); }
         */
        break;

      case SELECTED_ADD_MOLECULE:
        scale = this.jAddMoleculeDialog.isPredefinedDistance();
        if (scale) {
          length = this.jAddMoleculeDialog.getPredefinedDistance();
          if (length < 0.0f) {
            return;
          }
        }
        addMolecule(origin, dir, length, scale, otherRenderer);

        break;
      /*
       * case SELECTED_MODIFY_DIHEDRALS: if (selectedAtoms.size() == 4) { setupModifyDihedralDialog(); enableMousePicking(false);
       * temp_Group_1 = null; temp_Group_2 = null; temp_axis = null; // Rotation axis if (temp_structure == null ||
       * temp_structure.length < molecule.getNumberOfAtoms()) { temp_structure = new Point3f[molecule.getNumberOfAtoms()]; }
       * Molecule.getCoordinates(molecule, temp_structure); } break;
       */
    }
  }

  public int setNewGeometry(int n) {
    List geoms = getGeometries();
    if (n < 0 || n >= geoms.size()) {
      logger.info(
          "setNewGeometry: n < 0 || n >= Geometries.size()");
      return -1;
    }

    MolecularGeometry g = (MolecularGeometry) geoms.get(0);
    if (molecule.getNumberOfAtoms() != g.size()) {
      logger.info(
          "setNewGeometry: molecule.getNumberOfAtoms() != g.size()");
      MessageWindow d = new MessageWindow("Warning Message",
          "Geometries do not correspond to the Molecule any more", true);
      //molecule.setGeometries(null);
      geometrySelectioDialog.setVisible(false);
      geometrySelection.setEnabled(false);
      d.setVisible(true);
      return -1;
    }

    setupSelectedGeometry(molecule, n);
    addMolecule(molecule);
    return n;
  }

  public boolean logoutDatabase() {
    if (databaseAccess == null) {
      return true;
    }
    connectedToDB = false;
    new_chemistryDatabase.setVisible(false);
    new_chemistryDatabase = null;
    new_sqlChemistryDatabase = null;
    return databaseAccess.closeConnection();
  }

  public boolean connectToDatabase() {

    // Invoke a dialog to get hostname, user name, etc...
    //ConnectSQLServer server = new ConnectSQLServer(menuFrame,
    // So, now we can open only one database during a session
    if (sqlServerDialog == null) {
      sqlServerDialog = new ConnectSQLServer("Connect to Database Server", true);
      if (sqlServerDialog.getDatabase().length() == 0) {
        sqlServerDialog.setDatabase("chemistry");
      }

      //server.dispose();
      //menuFrame.sqlServerDialog.setVisible(false);
    }

    if (!connectedToDB) {
      sqlServerDialog.setVisible(true);
    }

    if (!sqlServerDialog.pressedOK()) {
      return false;
    }

    String host = sqlServerDialog.getHostname();
    String user = sqlServerDialog.getUsername();
    String pass = sqlServerDialog.getPassword();
    String database = sqlServerDialog.getDatabase();

    // --- Connect to the SQL Server
    // --- Load connector and make connection
    if (databaseAccess == null) {
      databaseAccess = new SQLDatabaseAccess();
      if (databaseAccess.wasError()) {
        JOptionPane.showMessageDialog(new JFrame(),
            databaseAccess.getErrorMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return false;

      }
    }
    if (!databaseAccess.isDriverLoaded()) {
      return false;
    }
    if (!databaseAccess.getConnection(host, user, pass)) {
      return false;
    }
    if (!databaseAccess.selectDatabase(database)) {
      return false;
    }

    connectedToDB = true;

    //SQLChemistryDatabase sql_data = new SQLChemistryDatabase(host,
    // --- Testing new database
    if (cct.GlobalSettings.isNewDatabase()) {
      if (new_sqlChemistryDatabase == null) {
        new_sqlChemistryDatabase = new new_SQLChemistryDatabase(
            databaseAccess, host,
            user, pass, database);
        //new_sqlChemistryDatabase.setParentFrame(parentFrame);
      }
      if (new_sqlChemistryDatabase.isError()) {
        logger.info("connectToDatabase: "
            + new_sqlChemistryDatabase.getErrorMessage());
        MessageWindow er = new MessageWindow("ERROR",
            new_sqlChemistryDatabase.getErrorMessage(), true);
        er.setVisible(true);
      } else {
        //ConnectSQLServer sql = new ConnectSQLServer( menuFrame, "Connect to SQL Server", true );
        if (new_chemistryDatabase == null) {
          new_chemistryDatabase = new new_ChemistryDatabaseDialog(this,
              new_sqlChemistryDatabase,
              "Chemical Database", false);
        }
        new_chemistryDatabase.setVisible(true);

      }

    } else {
      // --- Old Database Design
      if (sqlChemistryDatabase == null) {
        sqlChemistryDatabase = new SQLChemistryDatabase(
            databaseAccess, host,
            user, pass, database);
      }

      if (sqlChemistryDatabase.isError()) {
        logger.info(": "
            + sqlChemistryDatabase.getErrorMessage());
        MessageWindow er = new MessageWindow("ERROR",
            sqlChemistryDatabase.getErrorMessage(), true);
        er.setVisible(true);
      } else {
        //ConnectSQLServer sql = new ConnectSQLServer( menuFrame, "Connect to SQL Server", true );
        if (chemistryDatabase == null) {
          chemistryDatabase = new ChemistryDatabaseDialog(
              this,
              sqlChemistryDatabase,
              "Chemical Database", false);
        }
        chemistryDatabase.setVisible(true);

      }
    }

    return true;
  }

  public void startBulkDatabaseLoad(String[] files) {

    // --- First, connect to database
    AddNewStructureDialog new_str = null;
    AddNewMoleculeDialog new_mol = null;

    if (!connectToDatabase()) {
      logger.info(
          "startBulkDatabaseLoad: Cannot connect to database");
      return;
    }

    String str_name = null;
    String notes = null;

    TextEntryDialog str_names = new TextEntryDialog(new Frame(),
        "Enter name for all Structures", true);
    str_names.setVisible(true);
    if (str_names.isTextEntered()) {
      str_name = str_names.getValue();
    } else {
      return;
    }

    YesNoDialog inter = new YesNoDialog(new Frame(), "Do you want to add structures interactively?");
    inter.setVisible(true);
    boolean isInteractive = inter.isYes();

    for (int i = 0; i < files.length; i++) {
      String fileName = files[i];
      //String directory = new String("./");
      String directory = new String("");
      MoleculeInterface m = Molecule.getNewInstance();
      GaussianOutput parseGaussianOutput = new GaussianOutput();
      GaussianJob results;
      try {
        results = parseGaussianOutput.parseFile(m, directory + fileName, false);
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
        continue;
      }
      //m = (MoleculeInterface) results.get("molecule");
      m = results.getMolecule(0); // !!! i.e. take molecule from the first step
      //Geometries = (ArrayList) results.get("geometries");
      Geometries = results.getGeometries(0);

      if (Geometries.size() > 0) {
        // --- Select last geometry
        setupSelectedGeometry(m, Geometries.size() - 1);
        Molecule.guessCovalentBonds(m);
        addMolecule(m);
      } else {
        MessageWindow d = new MessageWindow("Error Message", "No Geometries...", true);
        d.setVisible(true);
        continue;
      }

      Map prop = m.getProperties();

      Integer ch = (Integer) prop.get(MoleculeInterface.ChargeProperty);
      Integer multiplicity = (Integer) prop.get(MoleculeInterface.MultiplicityProperty);

      String mol_name = fileName.substring(0, fileName.indexOf("."));
      mol_name = mol_name.toUpperCase();

      // --- Add new molecule
      if (new_mol == null) {
        new_mol = new AddNewMoleculeDialog("Adding New Molecule", true, mol_name);
      }
      new_mol.setName(mol_name);

      if (ch == null) {
        new_mol.setCharge(0);
      } else {
        new_mol.setCharge(ch.intValue());
      }

      if (multiplicity == null) {
        new_mol.setMultiplicity(1);
      } else {
        new_mol.setMultiplicity(multiplicity.intValue());
      }

      if (isInteractive) {
        new_mol.setVisible(true);
      }
      if (!new_mol.isOKPressed() && isInteractive) {
        continue;
      }
      String aliases = new_mol.getAliases();
      notes = new_mol.getNotes();
      boolean is_model = new_mol.isModel();
      int charge = new_mol.getCharge();
      int mult = new_mol.getMultiplicity();

      if (!new_sqlChemistryDatabase.addNewMolecule(mol_name, aliases,
          notes, is_model,
          charge, mult)) {
        continue;
      }

      // --- Add structure
      if (new_str == null) {
        new_str = new AddNewStructureDialog("New Structure",
            true, mol_name,
            new_sqlChemistryDatabase);
        new_str.setName(str_name);
      }

      if (isInteractive || i == 0) {
        new_str.setVisible(true);
      }
      if (!new_str.isOKPressed() && isInteractive) {
        continue;
      }

      str_name = new_str.getName();
      notes = new_str.getNotes();

      logger.info("New Structure: " + str_name);

      String method = new_str.getMethod();

      MoleculeInterface mol = getMolecule();
      Map row = new_sqlChemistryDatabase.addNewStructure(mol_name,
          str_name, notes,
          method, mol);
      if (row == null) {
        MessageWindow er = new MessageWindow("ERROR",
            new_sqlChemistryDatabase.getErrorMessage(), true);
        er.setVisible(true);
        continue;
      }
    }

    new_chemistryDatabase.updateData();
  }

  public void addGaussianData(Gaussian gData) {
    gaussianData = gData;
  }

  public Gaussian getGaussianData() {
    return gaussianData;
  }

  public Canvas3D getCanvas3D() {
    return canvas3D;
  }

  public void setRadiusForSelectedAtoms(float radius) {
    if (lastSelectedAtom < 0) { //|| lastSelectedAtom >= atomSpheres.size()) {
      return;
    }
    AtomInterface a = molecule.getAtomInterface(lastSelectedAtom);
    AtomNode atom_node = (AtomNode) a.getProperty(ATOM_NODE);
    //AtomNode atom_node = (AtomNode) atomNodes.get(lastSelectedAtom);

    setAtomicSphereRadius(atom_node, radius);
    /*
     * atom_node.detach();
     *
     * Sphere atom = (Sphere) atomSpheres.get(lastSelectedAtom); Appearance ap = atom.getAppearance(); int divisions =
     * atom.getDivisions(); Sphere new_atom = new Sphere(radius, Sphere.GENERATE_NORMALS, divisions, ap);
     */

    Float R = (Float) a.getProperty(AtomInterface.GR_RADIUS);
    R = radius;
    a.setProperty(AtomInterface.GR_RADIUS, R);
    /*
     * setAtomicSphere(atom_node, new_atom); atomSpheres.remove(lastSelectedAtom); atomSpheres.add(lastSelectedAtom, new_atom);
     *
     * mol.addChild(atom_node);
     */
  }

  public void setAtomicSphereRadius(AtomNode atom_node, float radius) {

    //atom_node.detach();
    atom_node.setAtomicSphereRadius(radius);
    /*
     * TransformGroup scale = getAtomicSphereScale(atom_node);
     *
     * Transform3D value = new Transform3D(); value.setScale(radius); scale.setTransform(value);
     */
    //mol.addChild(atom_node);
  }

  public void setColorForSelectedAtoms(Color new_color) {
    if (lastSelectedAtom < 0) { //|| lastSelectedAtom >= atomSpheres.size()) {
      return;
    }

    AtomInterface atom = molecule.getAtomInterface(lastSelectedAtom);
    AtomNode atom_node = (AtomNode) atom.getProperty(ATOM_NODE);
    //AtomNode atom_node = atomNodes.get(lastSelectedAtom);

    highlightSelectedAtom(mol, atom_node, false);

    //atom_node.detach();
    atom_node.setAtomColor(new_color);

    Material store = cloneMaterial(atom_node.getMaterial());
    //materialStore.remove(lastSelectedAtom);
    atom.setProperty(ATOM_NODE_MATERIAL_COPY, store);
    //materialStore.add(lastSelectedAtom, store);

    //mol.addChild(atom_node);
    List bonds = atom.getBondIndex();
    for (int j = 0; j < bonds.size(); j++) {
      Bond b = (Bond) bonds.get(j);
      int index = molecule.getBondIndex(b);
      BondNode bond = bondNodes.get(index);
      //bond.detach();
      bond.updateBondColor();
      //mol.addChild(bond);
    }

  }

  /**
   * Reset rotation of the scene to the identity matrix
   */
  public void resetSceneRotation() {
    Transform3D reset = new Transform3D();
    sceneTrans.getTransform(reset);
    Matrix3d unit = new Matrix3d();
    unit.setIdentity();
    reset.setRotation(unit);
    sceneTrans.setTransform(reset);
  }

  /**
   * Reset the whole scene to the identity matrix
   */
  public void resetScene() {
    Transform3D reset = new Transform3D();
    sceneTrans.getTransform(reset);
    reset.setIdentity();
    sceneTrans.setTransform(reset);

    reset = new Transform3D();
    moleculeTrans.getTransform(reset);
    reset.setIdentity();
    moleculeTrans.setTransform(reset);
  }

  public void setAtomColor(int natom, Color new_color) {
    if (natom < 0 || natom >= molecule.getNumberOfAtoms()) {
      return;
    }
    AtomInterface atom = molecule.getAtomInterface(natom);
    setAtomColor(atom, new_color);
  }

  public void setAtomColor(AtomInterface atom, Color3f new_color) {
    Object obj = atom.getProperty(ATOM_NODE);
    if (obj == null) {
      System.err.println(this.getClass().getCanonicalName() + ": setAtomColor: atom does not contain AtomNode. Ignored...");
      return;
    }
    AtomNode atom_node = (AtomNode) obj;
    atom_node.setAtomColor(new_color);
    atom.setProperty(ATOM_NODE_MATERIAL_COPY, atom_node.getMaterial());

    List bonds = atom.getBondIndex();
    for (int j = 0; j < bonds.size(); j++) {
      Bond b = (Bond) bonds.get(j);
      int index = molecule.getBondIndex(b);
      BondNode bond = bondNodes.get(index);
      bond.updateBondColor();
    }
  }

  public void setAtomColor(AtomInterface atom, Color new_color) {
    Color3f new_color3f = new Color3f(new_color);
    setAtomColor(atom, new_color3f);
  }

  public void setAtomColor(int natom, Color3f new_color, Material new_material) {
    if (natom < 0) { //|| natom >= atomSpheres.size()) {
      return;
    }

    AtomInterface atom = molecule.getAtomInterface(natom);
    AtomNode atom_node = (AtomNode) atom.getProperty(ATOM_NODE);
    //AtomNode atom_node = atomNodes.get(natom);

    highlightSelectedAtom(mol, atom_node, false);

    //atom_node.detach();
    mol.removeChild(atom_node);

    atom_node.setAtomColor(new_color, new_material);

    Material store = cloneMaterial(atom_node.getMaterial());
    atom.setProperty(ATOM_NODE_MATERIAL_COPY, store);
    //materialStore.set(natom, store);

    mol.addChild(atom_node);

  }

  public void setLabelForSelectedAtoms(String new_label) {
    if (molecule == null) {
      return;
    }
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface atom = molecule.getAtomInterface(i);
      if (!atom.isSelected()) {
        continue;
      }
      atom.setName(new_label);

      // !!! now update label nodes !!! to be done
    }
  }

  /**
   *
   */
  private void setupModifyAtomDialog() {
    if (lastSelectedAtom < 0) {
      return;
    }
    AtomInterface a = molecule.getAtomInterface(lastSelectedAtom);
    logger.info("Selected atom: " + lastSelectedAtom + " :" + ChemicalElements.getElementSymbol(a.getAtomicNumber()));

    int element = a.getAtomicNumber();
    jModifyAtomDialog.selectElement(element);
    jModifyAtomDialog.setLabel(a.getName());
    jModifyAtomDialog.setX(a.getX());
    jModifyAtomDialog.setY(a.getY());
    jModifyAtomDialog.setZ(a.getZ());
    Float R = (Float) a.getProperty(AtomInterface.GR_RADIUS);
    if (R == null) {
      R = ChemicalElements.getCovalentRadius(a.getAtomicNumber());
      a.setProperty(AtomInterface.GR_RADIUS, R);
    }
    jModifyAtomDialog.setRadius(R);
    AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
    //AtomNode atom = atomNodes.get(lastSelectedAtom);
    Color3f c = atom.getColor3f(); //getAtomicNodeColor(atom);
    int red = (int) (c.x * 255.0f);
    int green = (int) (c.y * 255.0f);
    int blue = (int) (c.z * 255.0f);
    jModifyAtomDialog.setColor(red, green, blue);

    String currentType = "Atom";
    Map atomTypes = CCTAtomTypes.getPictureMapping(a.getAtomicNumber()); // LinkedHashMap();

    Object obj = a.getProperty(AtomInterface.CCT_ATOM_TYPE);
    String atomType = null;
    if (obj != null) {
      atomType = (String) obj;
    }

    Set aTypes = atomTypes.keySet();
    Iterator iter = aTypes.iterator();
    while (iter.hasNext()) {
      String Key = (String) iter.next();
      if (Key.equalsIgnoreCase(atomType)) {
        currentType = Key;
      }
    }

    logger.info("Size of atom types: " + atomTypes.size());
    jModifyAtomDialog.setAtomTypes(atomTypes);
    jModifyAtomDialog.setSelectedAtomType(currentType);
    jModifyAtomDialog.validate();
  }

  public void setAtomTypeForSelectedAtoms(String new_atomType) {
    if (molecule == null) {
      return;
    }
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface atom = molecule.getAtomInterface(i);
      if (!atom.isSelected()) {
        continue;
      }
      if (new_atomType.equalsIgnoreCase("Atom")
          || new_atomType.length() == 0) {
        atom.setProperty(AtomInterface.CCT_ATOM_TYPE, null);
      } else {
        atom.setProperty(AtomInterface.CCT_ATOM_TYPE, new_atomType);
      }

      // !!! now update label nodes !!! to be done
    }

  }

  private void setupModifyBondDialog() {
    // --- Error check

    if (selectedAtoms.size() != 2) {
      System.err.println(
          "setupModifyBondDialog: selectedAtoms.size() != 2. Ignored...");
      return;
    }
    Integer I = (Integer) selectedAtoms.get(0);
    Integer J = (Integer) selectedAtoms.get(1);

    AtomInterface a_i = molecule.getAtomInterface(I);
    AtomInterface a_j = molecule.getAtomInterface(J);

    float length = (float) a_i.distanceTo(a_j);
    jModifyBondDialog.setBondLenght(length);

    jModifyBondDialog.setDrawBond(a_i.isBondedTo(a_j));
  }

  private void setupModifyAngleDialog() {

    if (selectedAtoms.size() < 3) {
      return;
    }

    Integer I = (Integer) selectedAtoms.get(0);
    Integer J = (Integer) selectedAtoms.get(1);
    Integer K = (Integer) selectedAtoms.get(2);

    AtomInterface a_i = molecule.getAtomInterface(I);
    AtomInterface a_j = molecule.getAtomInterface(J);
    AtomInterface a_k = molecule.getAtomInterface(K);

    //float angle = a_j.angleBetween(a_i, a_k) / 1.74532925199e-2f;
    float angle = angleBetween(a_i, a_j, a_k) / 1.74532925199e-2f;
    jModifyAngleDialog.setAngle(angle);

  }

  /**
   *
   * @param new_element int
   */
  public void setElementForSelectedAtoms(int new_element) {
    if (lastSelectedAtom < 0) { //|| lastSelectedAtom >= atomSpheres.size()) {
      return;
    }

    AtomInterface a = molecule.getAtomInterface(lastSelectedAtom);
    a.setAtomicNumber(new_element);
    /*
     * float radius = ChemicalElements.getCovalentRadius(a. getAtomicNumber()); radius *= AtomInterface.COVALENT_TO_GRADIUS_FACTOR;
     * if (radius < 0.001f) { radius = 0.2f; } Float R = (Float) a.getProperty(AtomInterface.GR_RADIUS); R = radius;
     */
    //a.setProperty(AtomInterface.GR_RADIUS, R);

    AtomNode new_atom = new AtomNode(a); //createAtom(a.getX(), a.getY(), a.getZ(),
    // new_element);
    //Material store = ChemicalElementsColors.getElementMaterial(new_element);
    //materialStore.remove(lastSelectedAtom);
    //materialStore.add(lastSelectedAtom, new_atom.getMaterial());
    a.setProperty(ATOM_NODE_MATERIAL_COPY, new_atom.getMaterial());

    AtomNode atom_node = (AtomNode) a.getProperty(ATOM_NODE);
    //AtomNode atom_node = atomNodes.get(lastSelectedAtom);

    //atom_node.detach();
    mol.removeChild(atom_node);

    atomLabels.remove(lastSelectedAtom);
    atomLabels.add(lastSelectedAtom, null);

    a.setProperty(ATOM_NODE, new_atom);
    mol.addChild(new_atom);

    setupModifyAtomDialog();
  }

  public void drawBondBetweenSelectedAtoms(boolean draw_bond) {

    if (selectedAtoms.size() < 2) {
      return; // Simple error check
    }

    Integer I = (Integer) selectedAtoms.get(0);
    Integer J = (Integer) selectedAtoms.get(1);

    AtomInterface a_i = molecule.getAtomInterface(I.intValue());
    AtomInterface a_j = molecule.getAtomInterface(J.intValue());

    if (draw_bond && (!a_i.isBondedTo(a_j))) {
      BondNode bond = createBond(I.intValue(), J.intValue());
      if (bond != null) {
        mol.addChild(bond);
        bondNodes.add(bond);
      }
    } else if ((!draw_bond) && a_i.isBondedTo(a_j)) {
      deleteBondBetweenAtoms(a_i, a_j);
    }
  }

  public void deleteBondBetweenAtoms(AtomInterface a_i, AtomInterface a_j) {
    BondInterface b = a_i.getBondToAtom(a_j);
    int bindex = molecule.getBondIndex(b);
    if (bindex == -1) {
      logger.info("deleteBondBetweenAtoms: bindex == -1");
      return;
    }

    // --- Delete bond node
    BondNode bond = bondNodes.get(bindex);
    if (mol.indexOfChild(bond) != -1) {
      mol.removeChild(bond);
    }
    bondNodes.remove(bond);

    // --- Now delete "real" bond in molecule
    molecule.deleteBond(b);

  }

  public void deleteAllBonds() {

    // --- Delete bond nodes
    for (int i = bondNodes.size() - 1; i > -1; i--) {
      BondNode bond = bondNodes.get(i);
      if (mol.indexOfChild(bond) != -1) {
        mol.removeChild(bond);
      }
      bondNodes.remove(bond);
    }

    // --- Now delete "real" bonds in molecule
    for (int i = molecule.getNumberOfBonds() - 1; i > -1; i--) {
      BondInterface bond = molecule.getBondInterface(i);
      molecule.deleteBond(bond);
    }
  }

  /**
   * Status: 0 - Fixed, 1 - translate Atom; 2 - translate group
   *
   * @param new_distance float
   * @param I_atom_status int
   * @param J_atom_status int
   */
  public void changeDistanceBetweenSelectedAtoms(float new_distance,
      int I_atom_status,
      int J_atom_status) {
    if (selectedAtoms.size() < 2) {
      return; // Simple error check
    }

    Integer I = (Integer) selectedAtoms.get(0);
    Integer J = (Integer) selectedAtoms.get(1);

    AtomInterface a_i = molecule.getAtomInterface(I.intValue());
    AtomInterface a_j = molecule.getAtomInterface(J.intValue());

    float bondIncrenment = new_distance - (float) a_i.distanceTo(a_j);
    float i_inc = bondIncrenment / 2.0f;
    float j_inc = bondIncrenment / 2.0f;
    if (I_atom_status == 0) {
      i_inc = 0;
      j_inc = bondIncrenment;
    } else if (J_atom_status == 0) {
      i_inc = bondIncrenment;
      j_inc = 0;
    }

    // --- Form unit translation vector
    float direction[] = {
      0, 0, 0}; //new float[3];
    float dist = (float) a_i.distanceTo(a_j);
    direction[0] = (a_j.getX() - a_i.getX()) / dist;
    direction[1] = (a_j.getY() - a_i.getY()) / dist;
    direction[2] = (a_j.getZ() - a_i.getZ()) / dist;

    // --- Translate 1st Atom
    if (I_atom_status == 1) {
      translateAtom(I.intValue(), direction, -i_inc);
    } else if (I_atom_status == 2) {
      if (temp_Group_1 == null) {
        temp_Group_1 = Molecule.noncyclicAtomGroup(a_i, a_j);
      }
      /*
       * logger.info("I: group: "+group.size()); for (int i = 0; i < group.size(); i++) { Atom a = (Atom) group.get(i); int
       * atom_index = molecule.getAtomIndex(a); System.out.print(atom_index+"; "); }
       */
      translateAtomicGroup(temp_Group_1, direction, -i_inc);
    }

    // --- Translate 2nd Atom
    if (J_atom_status == 1) {
      translateAtom(J.intValue(), direction, j_inc);
    } else if (J_atom_status == 2) {
      if (temp_Group_2 == null) {
        temp_Group_2 = Molecule.noncyclicAtomGroup(a_j, a_i);
      }
      /*
       * logger.info("J: group: "+group.size()); for (int i = 0; i < group.size(); i++) { Atom a = (Atom) group.get(i); int
       * atom_index = molecule.getAtomIndex(a); System.out.print(atom_index+"; "); }
       */
      translateAtomicGroup(temp_Group_2, direction, j_inc);
    }

    //jModifyBondDialog.setBondLenght(new_distance);
  }

  public void translateAtomicGroup(List group, float dir[], float increment) {

    // --- Update "real" molecule
    if (norm(dir) < 0.001f) {
      return;
    }
    Molecule.translateAtoms(group, dir, increment);

    // --- Update nodes
    // --- Update Atom's Coordinates
    for (int i = 0; i < group.size(); i++) {
      AtomInterface a = (AtomInterface) group.get(i);
      int atom_index = molecule.getAtomIndex(a);

      AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
      //AtomNode atom = atomNodes.get(atom_index);
      /*
       * TransformGroup tg = getAtomicTranformGroup(atom);
       *
       * Transform3D t3d = new Transform3D(); tg.getTransform(t3d); Vector3f trans = new Vector3f(a.getCoordinates());
       * t3d.set(trans);
       */
      //atom.detach();
      //tg.setTransform(t3d);
      atom.setAtomicCoordinates(a);

      //mol.addChild(atom);
    }

    // --- Update bonds of the atom
    List bonds_to_modify = new ArrayList();

    for (int i = 0; i < group.size(); i++) {
      Atom a = (Atom) group.get(i);
      List bonds = a.getBondIndex();
      for (int j = 0; j < bonds.size(); j++) {
        Bond b = (Bond) bonds.get(j);
        if (bonds_to_modify.contains(b)) {
          continue;
        }
        bonds_to_modify.add(b);
      }
    }

    for (int i = 0; i < bonds_to_modify.size(); i++) {
      Bond b = (Bond) bonds_to_modify.get(i);

      int index = molecule.getBondIndex(b);

      BondNode bnode = bondNodes.get(index);
      //bnode.detach();
      bnode.updateBond();
      //mol.addChild(bnode);
    }

  }

  public void rotateAtomicGroupAroundAxis(List group, float theta, Point3fInterface p1, Point3fInterface p2) {

    // --- Update "real" molecule
    Molecule.rotateAtomsAroundAxis(group, theta, p1, p2);

    // --- Update nodes
    // --- Update Atom's Coordinates
    for (int i = 0; i < group.size(); i++) {
      AtomInterface a = (AtomInterface) group.get(i);
      int atom_index = molecule.getAtomIndex(a);

      AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
      //AtomNode atom = atomNodes.get(atom_index);
      /*
       * TransformGroup tg = getAtomicTranformGroup(atom); Transform3D t3d = new Transform3D(); tg.getTransform(t3d); Vector3f trans
       * = new Vector3f(a.getCoordinates()); t3d.set(trans);
       */
      //atom.detach();
      //tg.setTransform(t3d);
      atom.setAtomicCoordinates(a);

      //mol.addChild(atom);
    }

    // --- Update bonds of the atom
    List bonds_to_modify = new ArrayList();

    for (int i = 0; i < group.size(); i++) {
      Atom a = (Atom) group.get(i);
      List bonds = a.getBondIndex();
      for (int j = 0; j < bonds.size(); j++) {
        Bond b = (Bond) bonds.get(j);
        if (bonds_to_modify.contains(b)) {
          continue;
        }
        bonds_to_modify.add(b);
      }
    }

    for (int i = 0; i < bonds_to_modify.size(); i++) {
      Bond b = (Bond) bonds_to_modify.get(i);

      int index = molecule.getBondIndex(b);

      BondNode bnode = bondNodes.get(index);
      //bnode.detach();
      bnode.updateBond();
      //mol.addChild(bnode);
    }

  }

  /**
   * Updates geometry
   */
  public void updateMolecularGeometry() {

    if (molecule == null) {
      return;
    }

    boolean debug = true;

    // --- Update Atom's Coordinates
    long start = System.currentTimeMillis();

    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface a = molecule.getAtomInterface(i);

      AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
      //AtomNode atom = atomNodes.get(i);
      /*
       * TransformGroup tg = getAtomicTranformGroup(atom);
       *
       * Transform3D t3d = new Transform3D(); tg.getTransform(t3d); //Vector3f trans = new Vector3f(a.getCoordinates()); Vector3f
       * trans = new Vector3f(a.getX(), a.getY(), a.getZ()); t3d.set(trans);
       */

      //atom.detach();
      //tg.setTransform(t3d);
      atom.setAtomicCoordinates(a);
      //mol.addChild(atom);

    }
    float secs = (float) (System.currentTimeMillis() - start) / 1000.0f;
    logger.info("Elapsed time for java3d atoms update: " + secs);

    // --- Update bonds of the atom
    start = System.currentTimeMillis();
    for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
      BondInterface b = molecule.getBondInterface(i);

      BondNode bnode = bondNodes.get(i);
      //bnode.detach();
      bnode.updateBond();
      //mol.addChild(bnode);
    }
    secs = (float) (System.currentTimeMillis() - start) / 1000.0f;
    logger.info("Elapsed time for java3d bonds update: " + secs);

  }

  public void translateAtom(int atom_index, float dir[], float increment) {

    // --- Update "real" molecule
    Molecule.translateAtom(molecule, atom_index, dir, increment);
    AtomInterface a = molecule.getAtomInterface(atom_index);

    // --- Update nodes
    updateAtomNode(atom_index, a);

  }

  public void rotateAtomAroundAxis(int atom_index, float theta,
      Point3fInterface p1,
      Point3fInterface p2) {

    // --- Update "real" molecule
    Molecule.rotateAtomAroundAxis(molecule, atom_index, theta, p1, p2);
    AtomInterface a = molecule.getAtomInterface(atom_index);

    // --- Update nodes
    updateAtomNode(atom_index, a);

  }

  void updateAtomNode(int atom_index, AtomInterface a) {
    // --- Update Atom's Coordinates

    AtomNode atom = (AtomNode) a.getProperty(ATOM_NODE);
    //AtomNode atom = atomNodes.get(atom_index);
    /*
     * TransformGroup tg = getAtomicTranformGroup(atom);
     *
     * Transform3D t3d = new Transform3D(); tg.getTransform(t3d); Vector3f trans = new Vector3f(a.getX(), a.getY(), a.getZ());
     * t3d.set(trans);
     */
    //atom.detach();
    //tg.setTransform(t3d);
    atom.setAtomicCoordinates(a);
    //mol.addChild(atom);

    // --- Update bonds of the atom
    List bonds = a.getBondedToAtoms();
    List bond_index = a.getBondIndex();
    for (int i = 0; i < bonds.size(); i++) {
      Atom a_j = (Atom) bonds.get(i);
      Bond b = (Bond) bond_index.get(i);

      int index = molecule.getBondIndex(b);

      BondNode bnode = bondNodes.get(index);
      //bnode.detach();
      mol.removeChild(bnode);
      bnode.updateBond();
      mol.addChild(bnode);
    }

  }

  public void changeAngleBetweenSelectedAtoms(float new_angle,
      int I_atom_status,
      int J_atom_status,
      int K_atom_status) {
    if (selectedAtoms.size() < 3) {
      return; // Simple error check
    }

    Integer I = (Integer) selectedAtoms.get(0);
    Integer J = (Integer) selectedAtoms.get(1);
    Integer K = (Integer) selectedAtoms.get(2);

    AtomInterface a_i = molecule.getAtomInterface(I.intValue());
    AtomInterface a_j = molecule.getAtomInterface(J.intValue());
    AtomInterface a_k = molecule.getAtomInterface(K.intValue());

    // --- Form axis of rotation
    if (temp_axis == null) {
      cct.vecmath.Point3f v_ji = new cct.vecmath.Point3f(a_j, a_i);
      cct.vecmath.Point3f v_jk = new cct.vecmath.Point3f(a_j, a_k);
      temp_axis = v_ji.crossProduct(v_jk); // Rotation axis

      if (temp_axis.vectorNorm() < 0.00001f) {
        JOptionPane.showMessageDialog(new JFrame(),
            "Rotation Axis cannot be determined",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        //logger.info("axis.vectorSquaredNorm() < 0.00001f");
        return;
      }
      temp_axis.add(a_j);
      logger.info("Rotation Axis determined");
    }

    if (new_angle < 0.01f) {
      new_angle = -0.01f;
    } else if (new_angle > 179.9f) {
      new_angle = 180.1f;
    }

    //float angle = a_j.angleBetween(a_i, a_k);
    float angle = angleBetween(a_i, a_j, a_k);
    float angleIncrement = 1.74532925199e-2f * new_angle - angle;
    //logger.info("Angle: "+(angle/1.74532925199e-2f)+" new Angle: "+new_angle+" Increment: "+angleIncrement);
    if (Math.abs(angleIncrement) < 0.01f) {
      return;
    }
    //else if (angle + angleIncrement < 0) {
    //   return;
    //}
    float i_inc = angleIncrement / 2.0f;
    float k_inc = angleIncrement / 2.0f;
    /*
     * FIXED_ATOM = 0; public static final int ROTATE_GROUP = 1; public static final int TRANSLATE_GROUP = 2; public static final
     * int TRANSLATE_ATOM = 3; public static final int ROTATE_ATOM
     *
     */

    if (I_atom_status == FIXED_ATOM) {
      i_inc = 0;
      k_inc = angleIncrement;
    } else if (K_atom_status == FIXED_ATOM) {
      i_inc = angleIncrement;
      k_inc = 0;
    }

    // --- Rotate 1st Atom
    if (I_atom_status == ROTATE_ATOM) {
      rotateAtomAroundAxis(I.intValue(), -i_inc, a_j, temp_axis);
    } else if (I_atom_status == ROTATE_GROUP) {
      if (temp_Group_1 == null) {
        temp_Group_1 = Molecule.noncyclicAtomGroup(a_i, a_j, a_k);
      }
      rotateAtomicGroupAroundAxis(temp_Group_1, -i_inc, a_j, temp_axis);
      //translateAtomicGroup(temp_Group_1, direction, -i_inc);
    } else if (I_atom_status == TRANSLATE_GROUP) {
      if (temp_Group_1 == null) {
        temp_Group_1 = Molecule.noncyclicAtomGroup(a_i, a_j, a_k);
      }
      cct.vecmath.Point3f point = new cct.vecmath.Point3f(a_i);
      Geometry3d.rotatePointAroundArbitraryAxis(point, -i_inc, a_j,
          temp_axis);
      //logger.info("New XYZ coord: "+point.getX()+point.getY()+point.getZ());

      float move = (float) a_i.distanceTo(point);
      if (move >= 0.01f) {
        float direction[] = {
          0, 0, 0}; //new float[3];
        float dist = (float) a_i.distanceTo(point);
        direction[0] = (point.getX() - a_i.getX()) / dist;
        direction[1] = (point.getY() - a_i.getY()) / dist;
        direction[2] = (point.getZ() - a_i.getZ()) / dist;
        //float direction[] = a_i.getDirectionTo(point);
        translateAtomicGroup(temp_Group_1, direction, move);
      }
      //logger.info("New angle: "+a_j.angleBetween(a_i, a_k)+" move: "+move);
    }

    // --- Rotate 3d Atom
    if (K_atom_status == ROTATE_ATOM) {
      rotateAtomAroundAxis(K.intValue(), k_inc, a_j, temp_axis);
    } else if (K_atom_status == ROTATE_GROUP) {
      if (temp_Group_3 == null) {
        temp_Group_3 = Molecule.noncyclicAtomGroup(a_k, a_j, a_i);
      }
      rotateAtomicGroupAroundAxis(temp_Group_3, k_inc, a_j, temp_axis);
      //translateAtomicGroup(temp_Group_1, direction, -i_inc);
    } else if (K_atom_status == TRANSLATE_GROUP) {
      if (temp_Group_3 == null) {
        temp_Group_3 = Molecule.noncyclicAtomGroup(a_k, a_j, a_i);
      }
      cct.vecmath.Point3f point = new cct.vecmath.Point3f(a_k);
      Geometry3d.rotatePointAroundArbitraryAxis(point, k_inc, a_j,
          temp_axis);
      //logger.info("New XYZ coord: "+point.getX()+point.getY()+point.getZ());
      float move = (float) a_k.distanceTo(point);
      if (move >= 0.01f) {
        float direction[] = {
          0, 0, 0}; //new float[3];
        float dist = (float) a_k.distanceTo(point);
        direction[0] = (point.getX() - a_k.getX()) / dist;
        direction[1] = (point.getY() - a_k.getY()) / dist;
        direction[2] = (point.getZ() - a_k.getZ()) / dist;
        //float direction[] = a_k.getDirectionTo(point);
        translateAtomicGroup(temp_Group_3, direction, move);
      }
      //logger.info("New angle: "+a_j.angleBetween(a_i, a_k)+" move: "+move);
    }

    //jModifyBondDialog.setBondLenght(new_distance);
  }

  public void undoLastSelection() {
    if (selectedAtoms.size() == 0) {
      return;
    }
    enableMousePicking(true);
    Integer I = (Integer) selectedAtoms.get(selectedAtoms.size() - 1);
    AtomInterface at = molecule.getAtomInterface(I);
    at.setSelected(false);
    //molecule.markAtomAsSelected(I, false);
    AtomNode atom = (AtomNode) at.getProperty(ATOM_NODE);
    //AtomNode atom = atomNodes.get(I);
    highlightSelectedAtom(mol, atom, false);
    selectedAtoms.remove(selectedAtoms.size() - 1);

    if (selectedAtoms.size() == 0) {
      lastSelectedAtom = -1;
    } else {
      I = (Integer) selectedAtoms.get(selectedAtoms.size() - 1);
      lastSelectedAtom = I;
    }
    //logger.info("undoLastSelection: selectedAtoms "+selectedAtoms.size());

    temp_Group_1 = null;
    temp_Group_2 = null;
    temp_Group_3 = null;
    temp_axis = null;
  }

  public void resetGeometry() {
    Molecule.setCoordinates(molecule, temp_structure);
    updateMolecularGeometry();

    switch (processingSelected) {
      case SELECTED_MODIFY_BONDS:
        setupModifyBondDialog();
        break;

      case SELECTED_MODIFY_ANGLES:
        setupModifyAngleDialog();
        break;

      case SELECTED_MODIFY_DIHEDRALS:
        setupModifyDihedralDialog();
        break;

    }
  }

  public void confirmChanges() {
    selectAllAtoms(false);
    enableMousePicking(true);
  }

  public void modifyDihedralAngleDialog() {
    modifyDihedralAngleDialog(new Frame());
  }

  public void modifyDihedralAngleDialog(Frame parent) {

    if (molecule == null) {
      JOptionPane.showMessageDialog(parent, "Load Molecule first!", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (getMousePickingStatus()) {
      JOptionPane.showMessageDialog(parent, "Another Selection is already in progress!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (jModifyTorsionDialog == null) {
      jModifyTorsionDialog = new JModifyTorsionDialog(parent, "Modify Selected Dihedral Angle", false);
      jModifyTorsionDialog.setLocationRelativeTo(parent);
      jModifyTorsionDialog.setTargetClass(this);
    }
    enableMousePicking(true);
    selectedMode = SELECTION_FOUR_ATOMS_ONLY;
    processingSelected = SELECTED_MODIFY_DIHEDRALS;
    selectAndProcessDialog = true;
    jModifyTorsionDialog.setVisible(true);
  }

  private void setupModifyDihedralDialog() {

    if (selectedAtoms.size() < 4) {
      return;
    }

    Integer I = (Integer) selectedAtoms.get(0);
    Integer J = (Integer) selectedAtoms.get(1);
    Integer K = (Integer) selectedAtoms.get(2);
    Integer L = (Integer) selectedAtoms.get(3);

    AtomInterface a_i = molecule.getAtomInterface(I);
    AtomInterface a_j = molecule.getAtomInterface(J);
    AtomInterface a_k = molecule.getAtomInterface(K);
    AtomInterface a_l = molecule.getAtomInterface(L);

    double angle = dihedralAngle(a_i, a_j, a_k, a_l)
        / 1.74532925199e-2;
    jModifyTorsionDialog.setAngle(angle);
    logger.info("jModifyTorsionDialog is setup");
  }

  public void changeDihedralForSelectedAtoms(float new_angle,
      int I_atom_status,
      int L_atom_status) {
    if (selectedAtoms.size() < 4) {
      return; // Simple error check
    }

    Integer I = (Integer) selectedAtoms.get(0);
    Integer J = (Integer) selectedAtoms.get(1);
    Integer K = (Integer) selectedAtoms.get(2);
    Integer L = (Integer) selectedAtoms.get(3);

    AtomInterface a_i = molecule.getAtomInterface(I.intValue());
    AtomInterface a_j = molecule.getAtomInterface(J.intValue());
    AtomInterface a_k = molecule.getAtomInterface(K.intValue());
    AtomInterface a_l = molecule.getAtomInterface(L.intValue());

    // --- Form axis of rotation
    if (temp_axis == null) {
      temp_axis = new cct.vecmath.Point3f(a_j, a_k); // Rotation axis

      if (temp_axis.vectorNorm() < 0.00001f) {
        JOptionPane.showMessageDialog(new JFrame(),
            "Rotation Axis cannot be determined",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        //logger.info("axis.vectorSquaredNorm() < 0.00001f");
        return;
      }
      logger.info("Rotation Axis determined");
    }

    if (new_angle < -180.0f) {
      new_angle = -180.0f;
    } else if (new_angle > 180.0f) {
      new_angle = 180.0f;
    }

    float angle = (float) dihedralAngle(a_i, a_j, a_k, a_l);
    float angleIncrement = 1.74532925199e-2f * new_angle - angle;
    //logger.info("Angle: "+(angle/1.74532925199e-2f)+" new Angle: "+new_angle+" Increment: "+angleIncrement);
    if (Math.abs(angleIncrement) < 0.01f) {
      return;
    }
    //else if (angle + angleIncrement < 0) {
    //   return;
    //}
    float i_inc = angleIncrement / 2.0f;
    float l_inc = angleIncrement / 2.0f;

    if (I_atom_status == FIXED_ATOM) {
      i_inc = 0;
      l_inc = angleIncrement;
    } else if (L_atom_status == FIXED_ATOM) {
      i_inc = angleIncrement;
      l_inc = 0;
    }

    // --- Rotate 1st Atom
    if (I_atom_status == ROTATE_ATOM) {
      rotateAtomAroundAxis(I.intValue(), -i_inc, a_j, a_k);
    } else if (I_atom_status == ROTATE_GROUP) {
      if (temp_Group_1 == null) {
        if (a_i.isBondedTo(a_j)) {
          temp_Group_1 = Molecule.noncyclicAtomGroup(a_j, a_k);
        } else {
          temp_Group_1 = Molecule.noncyclicAtomGroup(a_i, a_j, a_k);
        }
        //logger.info("I Group size: "+ temp_Group_1.size());
      }

      rotateAtomicGroupAroundAxis(temp_Group_1, -i_inc, a_j, a_k);
      //translateAtomicGroup(temp_Group_1, direction, -i_inc);
    }

    // --- Rotate 3d Atom
    if (L_atom_status == ROTATE_ATOM) {
      rotateAtomAroundAxis(L.intValue(), l_inc, a_j, a_k);
    } else if (L_atom_status == ROTATE_GROUP) {
      if (temp_Group_2 == null) {
        if (a_l.isBondedTo(a_k)) {
          temp_Group_2 = Molecule.noncyclicAtomGroup(a_k, a_j);
        } else {
          temp_Group_2 = Molecule.noncyclicAtomGroup(a_l, a_k, a_j);
        }
        //logger.info("L Group size: "+ temp_Group_2.size());
      }

      rotateAtomicGroupAroundAxis(temp_Group_2, l_inc, a_j, a_k);
      //translateAtomicGroup(temp_Group_1, direction, -i_inc);
    }

    //jModifyBondDialog.setBondLenght(new_distance);
  }

  public void cancelSelection() {
    cancelSelection(null);
  }

  public void cancelSelection(JobProgressInterface progress) {
    selectAllAtoms(false, progress);
    enableMousePicking(false);
  }

  public void clearSelection() {
    clearSelection(null);
    //selectAllAtoms(false);
  }

  public void clearSelection(JobProgressInterface progress) {
    selectAllAtoms(false, progress);
  }

  public void selectAll() {
    selectAll(null);
  }

  public void selectAll(JobProgressInterface progress) {
    selectAllAtoms(true, progress);
  }

  public void invertSelection() {
    invertSelection(null);
  }

  public void invertSelection(JobProgressInterface progress) {
    invertSelectedAtoms(progress);
  }

  public Set getElementsInMolecule() {
    return Molecule.getElementsInMolecule(molecule);
  }

  public Set<String> getAtomNamesInMolecule() {
    return Molecule.getAtomNamesInMolecule(molecule);
  }

  @Deprecated
  public boolean openMolecularModelingFile(int fileType, String fileName, GlobalSettings.ADD_MOLECULE_MODE read_mode) {
    if (fileType == MolecularFileFormats.formatG03_GJF) { // G03 Input file

      if (gaussianInputEditorFrame != null && gaussianInputEditorFrame.isVisible()) {
        gaussianInputEditorFrame.setVisible(false);
      }

      //Gaussian g = new Gaussian();
      gaussianData = new Gaussian();
      int n = gaussianData.parseGJF(fileName, 0);
      logger.info("Number of molecules: " + n);
      if (n > 1) {
        if (gaussianInputEditorFrame == null) {
          gaussianData.setGraphicsRenderer(this);
          gaussianInputEditorFrame = new GaussianInputEditorFrame(gaussianData);
          gaussianInputEditorFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
          try {
            gaussianInputEditorFrame.removeMenuItem("File", "Exit");
          } catch (Exception ex) {
          }
          JMenuItem item = new JMenuItem("Select current step & return back to Editor");
          //GaussianInputEditorFrame_selectStep_actionAdapter al = new
          //    GaussianInputEditorFrame_selectStep_actionAdapter();

          item.addActionListener(this);
          gaussianInputEditorFrame.addMenuItem("File", item);
          gaussianInputEditorFrame.returnBackButton.setVisible(true);
          gaussianInputEditorFrame.returnBackButton.addActionListener(this);

        }
        gaussianInputEditorFrame.setupEditor(gaussianData);
        JOptionPane.showMessageDialog(null, "Gaussian Input file has more than 1 step\n" + "Select step in the next dialog",
            "Info", JOptionPane.INFORMATION_MESSAGE);

        gaussianInputEditorFrame.setVisible(true);
        //logger.info("Loading 1st Molecule...");
      } else {
        MoleculeInterface m = Molecule.getNewInstance();
        m = gaussianData.getMolecule(m, 0);
        if (m == null || m.getNumberOfAtoms() < 1) {
          JOptionPane.showMessageDialog(null, "Didn't find atoms in file", "Warning", JOptionPane.WARNING_MESSAGE);
          return false;
        }
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        //MoleculeInterface m2 = Molecule.divideIntoMolecules(m);
        setMolecule(m, read_mode);
      }
      //addGaussianData(g);

    } else if (fileType == MolecularFileFormats.formatG03_Output) { // G03 output file
      MoleculeInterface m = Molecule.getNewInstance();
      GaussianOutput parseGaussianOutput = new GaussianOutput();
      //HashMap results = (HashMap) parseGaussianOutput.parseFile(m, fileName, false);
      GaussianJob results;
      try {
        results = parseGaussianOutput.parseFile(m, fileName, false);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Cannot open file " + fileName + ": " + ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
      }
      //m = (MoleculeInterface) results.get("molecule");
      m = results.getMolecule(0);
      //Geometries = (ArrayList) results.get("geometries");
      Geometries = results.getGeometries(0);
      if (Geometries == null || Geometries.size() == 0) {
        JOptionPane.showMessageDialog(null, "No molecule found in file", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }

      Frame parent = new Frame();

      JShowText showResume = new JShowText("Gaussian Results");
      showResume.setSize(600, 640);
      showResume.setTitle("Gaussian Results: " + fileName);
      showResume.setLocationByPlatform(true);
      showResume.setText(parseGaussianOutput.getOutputResume());
      showResume.setVisible(true);

      if (Geometries.size() > 1) {

        JChoiceDialog selectG = new JChoiceDialog(parent,
            "Select Structure", true);
        for (int i = 0; i < Geometries.size(); i++) {
          MolecularGeometry gm = (MolecularGeometry) Geometries.get(i);
          selectG.addItem(gm.getName());
        }
        selectG.selectIndex(Geometries.size() - 1);
        selectG.pack();

        selectG.setLocationRelativeTo(parent);
        selectG.setVisible(true);
        if (selectG.isApproveOption()) {
          int n = selectG.getSelectedIndex();
          if (n != -1) {
            setupSelectedGeometry(m, n);
            Molecule.guessCovalentBonds(m);
            Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE,
                CCTAtomTypes.getElementMapping());
            //MoleculeInterface m2 = Molecule.divideIntoMolecules(m);
            setMolecule(m, read_mode);
          }

        }
      } else if (Geometries.size() == 1) {
        //geometrySelection.setEnabled(false); deactivate geometry selection menu
        setupSelectedGeometry(m, 0);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        //MoleculeInterface m2 = Molecule.divideIntoMolecules(m);
        setMolecule(m, read_mode);
      } else {
        JOptionPane.showMessageDialog(parent, "Didn't find geometries in output file", "Warning", JOptionPane.WARNING_MESSAGE);
      }

    } else if (fileType == MolecularFileFormats.formatG03_Fragment) { // Gaussian fragment file
      MoleculeInterface m = new Molecule();
      try {
        m = GaussianFragment.parseGaussianFragmentFile(m, fileName);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.format_XMol_XYZ) { // XMol XYZ file
      MoleculeInterface m = new Molecule();
      try {
        XMolXYZ xMolXYZ = new XMolXYZ();
        m = xMolXYZ.parseXMolXYZ(fileName, m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.format_MDL_Molfile) { // MDL Molfile file
      MoleculeInterface m = new Molecule();
      try {
        MDLMol mol = new MDLMol();
        m = mol.parseFile(fileName, m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.format_GRO) { // Gromacs GRO file
      MoleculeInterface m = new Molecule();
      try {
        m = GromacsParserFactory.parseGromacsCoordFile(fileName, m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.formatPDB) { // PDB file
      Molecule m = new Molecule();
      try {
        PDB.parsePDBFile(fileName, m);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.formatTripos_Mol2) { // Tripos Mol2 file
      Molecule m = new Molecule();
      TriposParser triposParser = new TriposParser();
      try {
        triposParser.parseMol2File(fileName, m);
        setMolecule(m, read_mode);
      } catch (Exception ex) {
        logger.severe("Error opening " + fileName + " : " + ex.getMessage());
      }
    } else if (fileType == MolecularFileFormats.formatCCT) { // CCT file
      Molecule m = new Molecule();
      m.setAtomInterface(new Atom());
      m.setBondInterface(new Bond());
      CCTParser cctParser = new CCTParser(m);
      List mols = cctParser.parseCCTFile(fileName, m);
      m = (Molecule) mols.get(0);
      logger.info("Number of atoms: " + m.getNumberOfAtoms());
      setMolecule(m, read_mode);
    }

    return true;
  }

  @Deprecated
  public boolean openMolecularModelingFile(int fileType, String fileName) {
    return openMolecularModelingFile(fileType, fileName, GlobalSettings.ADD_MOLECULE_MODE.SET);
  }

  @Deprecated
  public boolean openMolecularModelingFile(String fileType, String fileName) {
    if (fileType == null || fileName == null) {
      return false;
    }
    int formatType = MolecularFileFormats.formatUnknown;
    if (fileType.equals(MolecularFileFormats.gaussian03GJF)) {
      formatType = MolecularFileFormats.formatG03_GJF;
    } else if (fileType.equals(MolecularFileFormats.gaussian03Output)) {
      formatType = MolecularFileFormats.formatG03_Output;
    } else if (fileType.equals(MolecularFileFormats.gaussianFragment)) {
      formatType = MolecularFileFormats.formatG03_Fragment;
    } else if (fileType.equals(MolecularFileFormats.pdbFile)) {
      formatType = MolecularFileFormats.formatPDB;
    } else if (fileType.equals(MolecularFileFormats.triposMol2)) {
      formatType = MolecularFileFormats.formatTripos_Mol2;
    } else if (fileType.equals(MolecularFileFormats.mdlMolfileFormat)) {
      formatType = MolecularFileFormats.format_MDL_Molfile;
    } else if (fileType.equals(MolecularFileFormats.gromacsGROFormat)) {
      formatType = MolecularFileFormats.format_GRO;
    } else if (fileType.equals(MolecularFileFormats.cctFileFormat)) {
      formatType = MolecularFileFormats.formatCCT;
    } else if (fileType.equals(MolecularFileFormats.xmolXYZFileFormat)) {
      formatType = MolecularFileFormats.format_XMol_XYZ;
    }

    return openMolecularModelingFile(formatType, fileName);
  }

  public void setParentFrame(Frame parent) {
    guiFrame = parent;
  }

  private void jbInit() throws Exception {
    atomLabelsFont = new Font(atomLabelsFontName, atomLabelsFontStyle,
        atomLabelsFontSize);
  }

  public void setHelper(HelperInterface h) {
    helper = h;
    super.setHelper(h);
  }

  public Color getAtomLabelsColor() {
    return new Color(atomLabelsColor.x, atomLabelsColor.y, atomLabelsColor.z);
  }

  public void setAtomLabelsColor(Color color) {
    if (color == null) {
      return;
    }
    atomLabelsColor.set(color);
  }

  public Font getAtomLabelsFont() {
    return atomLabelsFont;
  }

  public void getAtomLabelsFont(Font font) {
    if (font == null) {
      return;
    }
    atomLabelsFont = font;
    atomLabelsFontSize = atomLabelsFont.getSize();
    atomLabelsFontStyle = atomLabelsFont.getStyle();
    atomLabelsFontName = atomLabelsFont.getFontName();
  }

  public List getGeometries() {
    return Geometries;
  }

  public int getSelectedGeometry() {
    return selectedGeometry;
  }

  public void setGeometries(List geoms) {
    Geometries = geoms;
  }

  public int getLastSelectedAtom() {
    return lastSelectedAtom;
  }

  @Deprecated
  public int setupSelectedGeometry(MoleculeInterface molec, int n) {
    if (n < 0 || n >= Geometries.size()) {
      logger.info(
          "setupSelectedGeometry: n < 0 || n >= Geometries.size()");
      return -1;
    }
    MolecularGeometry geom = (MolecularGeometry) Geometries.get(n);
    if (geom.size() != molec.getNumberOfAtoms()) {
      logger.info(
          "setupSelectedGeometry: geom.size() != getNumberOfAtoms() "
          + geom.size() + " != " + molec.getNumberOfAtoms());
      return -1;
    }

    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      cct.vecmath.Point3f point = geom.getCoordinates(i);
      AtomInterface atom = molec.getAtomInterface(i);
      atom.setXYZ(point);
    }
    selectedGeometry = n;
    return n;
  }

  /**
   * @deprecated - use from fragmentDictionaryParser
   * @param fragmentDic URL
   * @throws Exception
   */
  public void setFragmentDictionary(URL fragmentDic) throws Exception {

    fragmentDictionary = fragmentDic;
    fragmentsReferences.clear();

    if (fragmentDic.getProtocol().equalsIgnoreCase("jar") || fragmentDic.getProtocol().equalsIgnoreCase("zip")) {
      fragContext = fragmentDic.toString();
      fragContext = fragContext.substring(0, fragContext.indexOf("!/") + 2);
    } else {
      throw new Exception("setFragmentsBaseDirectory: don't know how to work with protocol: " + fragmentDic.getProtocol());
    }

    logger.info("Protocol: " + fragmentDic.getProtocol() + " Context: " + fragContext);
  }

  /**
   * @deprecated. Use loadFragment(MoleculeInterface m)
   * @param specification String
   */
  public void loadFragment(String specification) {
    String urlAddress = fragContext + specification;
    URL url = null;
    try {
      url = new URL(urlAddress);
    } catch (java.net.MalformedURLException ex) {
      JOptionPane.showMessageDialog(null, "Loading fragment: " + specification + " : " + ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    InputStream is = null;
    try {
      is = url.openStream();
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(null, "Loading fragment: " + specification + " : " + ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    MoleculeInterface m = new Molecule();
    CCTParser cctParser = new CCTParser(m);
    List mols = cctParser.parseCCTFile(is, m);
    m = (MoleculeInterface) mols.get(0);
    logger.info("Number of atoms: " + m.getNumberOfAtoms());

    if (m.getNumberOfAtoms() < 1) {
      return;
    }

    addMolecule(m);

    AtomInterface atom = null;
    int nPickable = 0;
    int lastPickable = 0;
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      atom = molecule.getAtomInterface(i);
      Boolean pickable = (Boolean) atom.getProperty(AtomInterface.PICKABILITY);
      logger.info("Atom " + i + " : " + pickable);
      if (pickable == null || !pickable.booleanValue()) {
        enableAtomPicking(i, false);
        logger.info("Atom " + i + " non-pickable");
      } else {
        ++nPickable;
        lastPickable = i;
      }
    }

    if (nPickable == 1) {
      lastSelectedAtom = lastPickable;
      atom.setSelected(true);
      this.highlightSelectedAtom(lastPickable, true);
    } else if (nPickable == 0) { // Enable picking for H's and Du's
      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        atom = molecule.getAtomInterface(i);
        if (atom.getAtomicNumber() == 0 || atom.getAtomicNumber() == 1) {
          enableAtomPicking(i, true);
        }
      }
      this.enableMousePicking(true);
    } else {
      this.enableMousePicking(true);
    }
  }

  public void loadFragment(MoleculeInterface m) {

    if (m.getNumberOfAtoms() < 1) {
      return;
    }

    addMolecule(m);

    AtomInterface atom = null;
    int nPickable = 0;
    int lastPickable = 0;
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      atom = molecule.getAtomInterface(i);
      Boolean pickable = (Boolean) atom.getProperty(AtomInterface.PICKABILITY);
      logger.info("Atom " + i + " : " + pickable);
      if (pickable == null || !pickable.booleanValue()) {
        enableAtomPicking(i, false);
        logger.info("Atom " + i + " non-pickable");
      } else {
        ++nPickable;
        lastPickable = i;
      }
    }

    if (nPickable == 1) {
      lastSelectedAtom = lastPickable;
      atom.setSelected(true);
      this.highlightSelectedAtom(lastPickable, true);
    } else if (nPickable == 0) { // Enable picking for H's and Du's
      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        atom = molecule.getAtomInterface(i);
        if (atom.getAtomicNumber() == 0 || atom.getAtomicNumber() == 1) {
          enableAtomPicking(i, true);
        }
      }
      this.enableMousePicking(true);
    } else {
      this.enableMousePicking(true);
    }
  }

  /**
   * Does solvation
   *
   * @param solvationType int
   */
  void openSolvateMoleculeDialog(int solvationType) {

    InputStream is = null;
    //if (selectDefaultSolvent == null) {
    if (jSolvateShellDialog == null) {
      URL fragDic = null;
      //ClassLoader cl = cct.resources.Resources.class.getClassLoader();
      ClassLoader cl = Java3dUniverse.class.getClassLoader();

      try {
        //fragDic = cl.getResource(cct.GlobalSettings.
        //                         getDefaultSolventDictionary());
        fragDic = cl.getResource(cct.GlobalSettings.getDefaultSolventDictionary());
        logger.info("File: " + fragDic.getFile() + " path: " + fragDic.getPath() + " " + fragDic.toString());
        logger.info("Protocol: " + fragDic.getProtocol());

        setSolventDictionary(fragDic);
      } catch (Exception ex) {
        logger.info(
            "Unable to get Resources " + cct.GlobalSettings.getDefaultSolventDictionary() + " Trying System Resources...");
        try {
          fragDic = ClassLoader.getSystemResource(cct.GlobalSettings.getDefaultSolventDictionary());
          setSolventDictionary(fragDic);
        } catch (Exception e) {
          System.err.println(getClass().getCanonicalName() + " : " + ex.getMessage());
          return;
        }
      }

      try {

        //logger.info("Query: " + fragDic.getQuery());
        is = cl.getResourceAsStream(cct.GlobalSettings.getDefaultSolventDictionary());

      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error opening default solvent dictionary: "
            + ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);

        System.err.println(getClass().getCanonicalName() + " : "
            + ex.getMessage());
        return;
      }
      FragmentDictionaryParser fdp = new FragmentDictionaryParser();
      Map fragTree = FragmentDictionaryParser.parseFragmentDictionary(is);
      // We use only a simpified list... !!!
      //ArrayList simpleList = new ArrayList();
      defaultSolventList = new HashMap();
      defaultSolventList = Utils.toSimpleList(fragTree, defaultSolventList);
      jSolvateShellDialog = new JSolvateShellDialog(null, "Select Solvation Parameters", true, defaultSolventList);

      jSolvateShellDialog.setAlwaysOnTop(
          true);
    }
    jSolvateShellDialog.setVisible(true);

    if (!jSolvateShellDialog.isOKpressed()) {
      return; // Cancel was pressed
    }

    float radius = 0;
    try {
      radius = jSolvateShellDialog.getRadius();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "ERROR: Wrong value for radius: " + jSolvateShellDialog.getRadiusAsText(),
          "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    float closeness = 0;
    try {
      closeness = jSolvateShellDialog.getCloseness();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "ERROR: Wrong value for closeness: " + jSolvateShellDialog.getClosenessAsText(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    String solvent = jSolvateShellDialog.getSelectedItem().toString();
    String value = (String) defaultSolventList.get(solvent);

    MoleculeInterface m = null;

    if (processingSelected == SELECTED_SOLVATE_CAP
        || processingSelected == SELECTED_SOLVATE_SHELL) {
      if (jSolvateShellDialog.isPredefinedSolvent()) {
        String urlAddress = solventContext + value;
        URL url = null;
        try {
          url = new URL(urlAddress);
        } catch (java.net.MalformedURLException ex) {
          JOptionPane.showMessageDialog(null, "Loading solvent: " + value + " : " + ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        try {
          is = url.openStream();
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(null, "Loading fragment: " + value + " : " + ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }
        m = new Molecule();
        CCTParser cctParser = new CCTParser(m);
        List mols = cctParser.parseCCTFile(is, m);
        m = (MoleculeInterface) mols.get(0);
      } else { // Custom molecule
        Object[] obj = jSolvateShellDialog.getCustomObjects();
        if (obj == null || obj.length < 1 || !(obj[0] instanceof MoleculeInterface)) {
          JOptionPane.showMessageDialog(null, "No custom solvent molecule", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        m = (MoleculeInterface) obj[0];
      }

      MoleculeInterface shell = Molecule.generateSolventShell(molecule, m, radius, closeness, processingSelected);

      this.appendMolecule(shell, false);
    }

  }

  /**
   * Setup base directory(URL) for the default Solvent dictionary
   *
   * @param fragmentDic URL
   * @throws Exception
   */
  public void setSolventDictionary(URL fragmentDic) throws Exception {

    solventDictionary = fragmentDic;

    if (fragmentDic.getProtocol().equalsIgnoreCase("jar")) {
      solventContext = fragmentDic.toString();
      solventContext = solventContext.substring(0,
          solventContext.indexOf("!/")
          + 2);
    } else {
      throw new Exception(
          "setSolventBaseDirectory: don't know how to work with protocol: "
          + fragmentDic.getProtocol());
    }

    logger.info("Protocol: " + fragmentDic.getProtocol()
        + " Context: " + solventContext);

  }

  public void getSelectedBranch(String value) {
    //SELECTED_SOLVATE_CAP:
    //SELECTED_SOLVATE_SHELL:
  }

  public void cancelTreeSelection() {
    endProcessingSelectedAtoms();
  }

  public void solvateMolecule(MoleculeInterface solvent, int solvationType) {
  }

  public void updateAtomColorScheme() {
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      //BranchGroup atom = (BranchGroup) atomNodes.get(i);
      Color3f color3f = ChemicalElementsColors.getElementColor(a.getAtomicNumber());
      Material material = ChemicalElementsColors.getElementMaterial(a.getAtomicNumber());
      setAtomColor(i, color3f, material);

      List bonds = a.getBondIndex();
      for (int j = 0; j < bonds.size(); j++) {
        BondInterface bond = (BondInterface) bonds.get(j);
        int bindex = molecule.getBondIndex(bond);
        if (bindex == -1) {
          continue;
        }
        BondNode bondNode = this.bondNodes.get(bindex);
        //bondNode.detach();
        mol.removeChild(bondNode);
        bondNode.updateBondColor();
        mol.addChild(bondNode);
      }
    }
  }

  public BufferedImage getImageCapture() {
    BufferedImage bImage = null;
    /*
     * // Create the off-screen Canvas3D object OffScreenCanvas3D offScreenCanvas3D = new OffScreenCanvas3D(config, true); // Set
     * the off-screen size based on a scale factor times the // on-screen size Screen3D sOn = canvas3D.getScreen3D(); Screen3D sOff
     * = offScreenCanvas3D.getScreen3D(); Dimension dim = sOn.getSize(); //dim.width *= OFF_SCREEN_SCALE; //dim.height *=
     * OFF_SCREEN_SCALE; sOff.setSize(dim); sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth() ); // OFF_SCREEN_SCALE);
     * sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight() ); // OFF_SCREEN_SCALE);
     *
     * // attach the offscreen canvas to the view //view.addCanvas3D(offScreenCanvas3D);
     * simpleU.getViewer().getView().addCanvas3D(offScreenCanvas3D);
     */

    Screen3D sOn = canvas3D.getScreen3D();
    Screen3D sOff = offScreenCanvas3D.getScreen3D();
    Dimension dim = sOn.getSize();
    sOff.setSize(dim);
    sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth());

    sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight());

    Point loc = canvas3D.getLocationOnScreen();
    offScreenCanvas3D.setOffScreenLocation(loc);
    dim = canvas3D.getSize();
    //dim.width *= OFF_SCREEN_SCALE;
    //dim.height *= OFF_SCREEN_SCALE;
    bImage = offScreenCanvas3D.doRender(dim.width, dim.height);

    MediaTracker mediaTracker = new MediaTracker(new Container());
    mediaTracker.addImage(bImage, 0);
    try {
      mediaTracker.waitForID(0);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }

    return bImage;
  }

  public int findClosestAtom(int x, int y) {

    Point3d origin = new Point3d();
    Point3d mouse_pos = new Point3d();

    canvas3D.getCenterEyeInImagePlate(origin);
    canvas3D.getPixelLocationInImagePlate(x, y, mouse_pos);

    Transform3D motion = new Transform3D();
    canvas3D.getImagePlateToVworld(motion);
    motion.transform(origin);
    motion.transform(mouse_pos);

    Vector3d dir = new Vector3d(mouse_pos);
    dir.sub(origin);

    return findClosestAtom(origin, dir);
  }

  public ProcessMouseRotate getMouseRotate() {
    return behavior;
  }

  public int findClosestBond(int x, int y) {

    Point3d origin = new Point3d();
    Point3d mouse_pos = new Point3d();

    canvas3D.getCenterEyeInImagePlate(origin);
    canvas3D.getPixelLocationInImagePlate(x, y, mouse_pos);

    Transform3D motion = new Transform3D();
    canvas3D.getImagePlateToVworld(motion);
    motion.transform(origin);
    motion.transform(mouse_pos);

    Vector3d dir = new Vector3d(mouse_pos);
    dir.sub(origin);

    return findClosestBond(origin, dir);
  }

  public boolean atomInfoPopupEnabled() {
    return atomInfoPopup;
  }

  public void enableAtomInfoPopup(boolean enable) {
    atomInfoPopup = enable;
    if (atomInfoPopup) {
      canvas3D.addMouseMotionListener(tracer);
    } else {
      canvas3D.removeMouseMotionListener(tracer);
    }
  }

  public List<AtomInterface> getSelectedAtoms() {
    return trackSelectedAtoms;
  }

  public void setGlobalRenderingStyle(int style) throws Exception {
    if (!renderStyles.containsValue(new Integer(style))) {
      throw new Exception("Unknown rendering style");
    }

    setRenderingParameters(style);
  }

  public void setGlobalRenderingStyle(String style) throws Exception {
    if (!renderStyles.containsKey(style)) {
      throw new Exception("Unknown rendering style: " + style);
    }
    Integer st = (Integer) renderStyles.get(style);
    setRenderingParameters(st);

    // --- Inform listeners about event
    if (renderingListeners.size() > 0) {
      RenderingObject rObj = new RenderingObject(this);
      for (int i = 0; i < renderingListeners.size(); i++) {
        renderingListeners.get(i).renderingChanged(rObj);
      }
    }
  }

  public String[] getRenderingStyles() {
    String[] styles = new String[renderStyles.size()];
    Set<String> keys = renderStyles.keySet();
    int i = 0;
    for (Iterator iter = keys.iterator(); iter.hasNext(); i++) {
      styles[i] = (String) iter.next();
    }
    return styles;
  }

  public void actionPerformed(ActionEvent e) {
    gaussianInputEditorFrame.setVisible(false);
    int n = gaussianInputEditorFrame.getSelectedStep();
    MoleculeInterface m = Molecule.getNewInstance();
    m = gaussianData.getMolecule(m, n);
    Molecule.guessCovalentBonds(m);
    //MoleculeInterface m2 = Molecule.divideIntoMolecules(m);
    addMolecule(m);
    if (this.processingSelected == SELECTED_ADD_MOLECULE) {
      enableMousePicking(true);
    }
  }

  public void updateSelectedAtomLabelsSize(double value) {
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      if (!a.isSelected()) {
        continue;
      }

      LabelNode label = (LabelNode) this.atomLabels.get(i);
      if (label == null) {
        continue;
      }
      label.setTextSize(value);
    }
  }

  public void updateSelectedAtomLabelsColor(Color color) {
    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      if (!a.isSelected()) {
        continue;
      }

      LabelNode label = (LabelNode) this.atomLabels.get(i);
      if (label == null) {
        continue;
      }
      label.setColor(color);
    }
  }

  public void addGraphics(GraphicsObjectInterface graphics) {
    if (graphicsObjects == null) {
      graphicsObjects = new ArrayList<GraphicsObjectInterface>();
    }

    List shape3ds = graphics.getShape3DElements();
    if (shape3ds == null || shape3ds.size() < 1) {
      return;
    }

    for (int i = 0; i < shape3ds.size(); i++) {
      Object obj = shape3ds.get(i);
      if (obj instanceof Shape3D) {
        Shape3D shape3d = (Shape3D) obj;
        BranchGroup bGroup = new BranchGroup();
        bGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        bGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        bGroup.setCapability(BranchGroup.ALLOW_DETACH);
        try {
          bGroup.addChild(shape3d);
          moleculeTrans.addChild(bGroup);
        } catch (Exception ex) {
          System.err.println("Error adding shape: " + ex.getMessage());
        }
      } else if (obj instanceof BranchGroup) {
        moleculeTrans.addChild((BranchGroup) obj);
      }
    }
    graphicsObjects.add(graphics);
  }

  public int getNumberOfGraphicsObjects() {
    if (graphicsObjects == null) {
      return 0;
    }
    return graphicsObjects.size();
  }

  public GraphicsObjectInterface getGraphicsObject(int n) {
    if (graphicsObjects == null || n < 0 || n >= graphicsObjects.size()) {
      System.err.println("graphicsObjects == null || n <0 || n>=graphicsObjects.size()");
      return null;
    }
    return graphicsObjects.get(n);
  }

  public List<GraphicsObjectInterface> getGraphicsObjects() {
    return graphicsObjects;

  }


  private class GaussianInputEditorFrame_selectStep_actionAdapter
      implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      gaussianInputEditorFrame.setVisible(false);
      int n = gaussianInputEditorFrame.getSelectedStep();
      MoleculeInterface m = Molecule.getNewInstance();
      m = gaussianData.getMolecule(m, n);
      Molecule.guessCovalentBonds(m);
      //MoleculeInterface m2 = Molecule.divideIntoMolecules(m);
      addMolecule(m);
      if (getMousePickingStatus()) {
        enableMousePicking(true);
      }
    }
  }
} // End of SampleFrame class

