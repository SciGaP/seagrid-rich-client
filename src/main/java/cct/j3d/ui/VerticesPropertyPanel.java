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

package cct.j3d.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.scijava.java3d.Appearance;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.Material;
import org.scijava.java3d.PolygonAttributes;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.java3d.TransparencyAttributes;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.scijava.vecmath.Color3f;

import cct.gaussian.java3d.GaussianJava3dFactory;
import cct.j3d.ColorRangeScheme;
import cct.j3d.USER_DATA_FLAG;
import cct.j3d.VerticesObject;
import cct.j3d.VerticesObjectProperties;

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
public class VerticesPropertyPanel
    extends JPanel implements MappedSurfaceInterface {

  static String MIXED_RENDERING = "Mixed Rendering";

  JLabel jLabel1 = new JLabel();
  JPanel mainPanel = new JPanel();
  JComboBox renderingComboBox = new JComboBox();
  JCheckBox opaqueCheckBox = new JCheckBox();
  JPanel jPanel2 = new JPanel();
  JPanel transparencyPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JComboBox transpComboBox = new JComboBox();
  JLabel jLabel3 = new JLabel();
  BorderLayout borderLayout3 = new BorderLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  JSlider opacitySlider = new JSlider();
  BorderLayout borderLayout4 = new BorderLayout();
  JCheckBox visibleCheckBox = new JCheckBox();

  Appearance App = null;
  Shape3D Shape3d = null;
  VerticesObject vObject = null;
  Object shape3d_Parent = null;
  Object grandParent = null;
  Color3f diffuseColorStore = new Color3f();
  Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
  float shininessStore = 1.0f;
  JPanel jPanel3 = new JPanel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  BorderLayout borderLayout5 = new BorderLayout();
  JPanel shininessPanel = new JPanel();
  JPanel topPanel = new JPanel();
  BorderLayout borderLayout6 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  JSlider shininessSlider = new JSlider();
  JPanel jPanel5 = new JPanel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel4 = new JLabel();
  BorderLayout borderLayout7 = new BorderLayout();
  BorderLayout borderLayout8 = new BorderLayout();
  protected ControlColorGradientPanel controlColorGradientPanel1 = new ControlColorGradientPanel();
  protected Border border1 = BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1);
  protected Border border2 = new TitledBorder(border1, "Color Gradient Control");
  protected GridBagLayout gridBagLayout1 = new GridBagLayout();
  static final Logger logger = Logger.getLogger(VerticesPropertyPanel.class.getCanonicalName());

  public VerticesPropertyPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setText("Rendering Style: ");
    mainPanel.setLayout(borderLayout6);
    opaqueCheckBox.setToolTipText("Opaque/Transparent Object");
    opaqueCheckBox.setMargin(new Insets(2, 5, 2, 2));
    opaqueCheckBox.setSelected(true);
    opaqueCheckBox.setText("Opaque");
    opaqueCheckBox.addActionListener(new
                                     VerticesPropertyPanel_opaqueCheckBox_actionAdapter(this));
    jPanel2.setLayout(borderLayout2);
    transparencyPanel.setLayout(borderLayout3);
    transparencyPanel.setBorder(new TitledBorder(BorderFactory.
                                                 createLineBorder(
        SystemColor.controlShadow, 1), "Transparency Panel"));
    jLabel3.setToolTipText("");
    jLabel3.setText("Transparency Mode: ");
    jPanel4.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    opacitySlider.setMajorTickSpacing(10);
    opacitySlider.setPaintLabels(true);
    opacitySlider.setPaintTicks(true);
    opacitySlider.addChangeListener(new VerticesPropertyPanel_opacitySlider_changeAdapter(this));
    jPanel1.setLayout(borderLayout4);
    visibleCheckBox.setToolTipText("Show/Hide Object");
    visibleCheckBox.setMargin(new Insets(2, 2, 2, 5));
    visibleCheckBox.setSelected(true);
    visibleCheckBox.setText("Visible");
    visibleCheckBox.addActionListener(new VerticesPropertyPanel_visibleCheckBox_actionAdapter(this));
    renderingComboBox.addItemListener(new VerticesPropertyPanel_renderingComboBox_itemAdapter(this));
    transpComboBox.addItemListener(new VerticesPropertyPanel_transpComboBox_itemAdapter(this));
    renderingComboBox.setToolTipText("Polygon rendering mode");
    transpComboBox.setToolTipText("Transparency mode");
    jLabel5.setToolTipText("");
    jLabel5.setText("Opaque");
    jLabel6.setToolTipText("");
    jLabel6.setText("Transparent");
    jPanel3.setLayout(borderLayout5);
    topPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);

    shininessPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlShadow, 1), "Shininess"));
    shininessPanel.setLayout(borderLayout7);

    shininessSlider.setMajorTickSpacing(7);
    shininessSlider.setMaximum(128);
    shininessSlider.setMinimum(1);
    shininessSlider.setPaintLabels(true);
    shininessSlider.setPaintTicks(true);
    shininessSlider.setValue(1);
    Hashtable shininessLabelTable = new Hashtable();
    shininessLabelTable.put(new Integer(1), new JLabel("1"));
    shininessLabelTable.put(new Integer(32), new JLabel("32"));
    shininessLabelTable.put(new Integer(64), new JLabel("64"));
    shininessLabelTable.put(new Integer(96), new JLabel("96"));
    shininessLabelTable.put(new Integer(128), new JLabel("128"));
    shininessSlider.setLabelTable(shininessLabelTable);

    shininessSlider.addChangeListener(new VerticesPropertyPanel_shininessSlider_changeAdapter(this));

    //shininessSlider.setBackground(new UIManager(236, 233, 216));
    jLabel2.setToolTipText("");
    jLabel2.setText("Very Shiny");
    jLabel4.setToolTipText("");
    jLabel4.setText("Not Shiny");
    jPanel5.setLayout(borderLayout8);
    controlColorGradientPanel1.setBorder(border2);
    controlColorGradientPanel1.setMinimumSize(new Dimension(100, 80));
    controlColorGradientPanel1.setPreferredSize(new Dimension(100, 80));
    controlColorGradientPanel1.setToolTipText("");
    jPanel4.add(jLabel3);
    jPanel4.add(transpComboBox);
    jPanel1.add(opacitySlider, BorderLayout.CENTER);
    jPanel3.add(jLabel5, BorderLayout.WEST);
    jPanel3.add(jLabel6, BorderLayout.EAST);
    mainPanel.add(shininessPanel, BorderLayout.CENTER);
    mainPanel.add(topPanel, BorderLayout.NORTH);
    topPanel.add(visibleCheckBox);
    topPanel.add(jLabel1);
    topPanel.add(renderingComboBox);
    topPanel.add(opaqueCheckBox);
    jPanel5.add(jLabel2, BorderLayout.WEST);
    jPanel5.add(jLabel4, BorderLayout.EAST);
    shininessPanel.add(shininessSlider, BorderLayout.NORTH);
    shininessPanel.add(jPanel5, BorderLayout.SOUTH);
    jPanel2.add(transparencyPanel, BorderLayout.CENTER);
    transparencyPanel.add(jPanel4, BorderLayout.NORTH);
    transparencyPanel.add(jPanel1, BorderLayout.SOUTH);
    transparencyPanel.add(jPanel3, BorderLayout.CENTER);
    this.add(controlColorGradientPanel1, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1));
    this.add(mainPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                               , GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1));
    this.add(jPanel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1));
    Hashtable labelTable = new Hashtable();
    labelTable.put(new Integer(0), new JLabel("0"));
    labelTable.put(new Integer(50), new JLabel("0.5"));
    labelTable.put(new Integer(100), new JLabel("1.0"));
    opacitySlider.setLabelTable(labelTable);

    String[] modes = VerticesObjectProperties.getAvailablePolygonModes();

    renderingComboBox.setEnabled(false);
    for (int i = 0; i < modes.length; i++) {
      renderingComboBox.addItem(modes[i]);
    }
    renderingComboBox.setSelectedIndex(VerticesObjectProperties.
                                       getDefaultPolygonMode());
    renderingComboBox.setEnabled(true);

    modes = VerticesObjectProperties.getAvailableTranspModes();
    transpComboBox.setEnabled(false);
    for (int i = 0; i < modes.length; i++) {
      transpComboBox.addItem(modes[i]);
    }
    transpComboBox.setSelectedIndex(VerticesObjectProperties.
                                    getDefaultTranspMode());
    transpComboBox.setEnabled(true);

    // --- Final touches

    if (opaqueCheckBox.isSelected()) {
      opacitySlider.setEnabled(false);
      opacitySlider.setValue(0);
      transpComboBox.setEnabled(false);
    }
    else {
      transpComboBox.setEnabled(true);
      opacitySlider.setEnabled(true);
    }

    controlColorGradientPanel1.setMappedSurfaceInterface(this);
  }

  public void setPanel(Shape3D shape3d) throws Exception {

    Shape3d = shape3d;
    vObject = null;
    shape3d_Parent = null;
    grandParent = null;

    try {
      Shape3d.setAppearanceOverrideEnable(true);
      shape3d_Parent = Shape3d.getParent();
      if (shape3d_Parent instanceof BranchGroup) {
        grandParent = ( (BranchGroup) shape3d_Parent).getParent();
      }
    }
    catch (Exception ex) {
      System.err.println(this.getClass().getCanonicalName() + ": setPanel: " + ex.getMessage());
    }

    if (shape3d_Parent == null) {
      visibleCheckBox.setSelected(false);
    }

    try {

      App = Shape3d.getAppearance();

      if (!App.getCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ)) {
        App.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
      }

      PolygonAttributes pa = App.getPolygonAttributes();
      int polygonMode = pa.getPolygonMode();

      String renderingMode = VerticesObjectProperties.getRenderingModeAsString(polygonMode);

      this.renderingComboBox.setEnabled(false);
      this.renderingComboBox.setSelectedItem(renderingMode);
      this.renderingComboBox.setEnabled(true);

      if (!App.getCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ)) {
        App.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
      }

      Material material = App.getMaterial();
      if (material != null) {
        float shininess = material.getShininess();
        logger.info("Shininess in Material: " + (int) shininess);
        shininessSlider.setEnabled(false);
        shininessSlider.setValue( (int) shininess);
        shininessSlider.setEnabled(true);
      }
      else {
        this.shininessSlider.setEnabled(false);
      }

      TransparencyAttributes ta = App.getTransparencyAttributes();

      this.opaqueCheckBox.setEnabled(false);
      this.opaqueCheckBox.setSelected(ta == null);
      this.opaqueCheckBox.setEnabled(true);

      this.opacitySlider.setEnabled(!opaqueCheckBox.isSelected());
      this.transpComboBox.setEnabled(!opaqueCheckBox.isSelected());

      if (ta != null) {
        float transp = ta.getTransparency();
        int transpMode = ta.getTransparencyMode();

        this.opacitySlider.setEnabled(false);
        this.transpComboBox.setEnabled(false);
        opacitySlider.setValue( (int) (transp * opacitySlider.getMaximum()));
        String transpM = VerticesObjectProperties.getTransparencyModeAsString(transpMode);
        if (transpM != null) {
          transpComboBox.setSelectedItem(transpM);
        }
        this.opacitySlider.setEnabled(true);
        this.transpComboBox.setEnabled(true);
      }
      else {

      }

      processOptionalControls();
    }
    catch (Exception ex) {
      throw new Exception(this.getClass().getCanonicalName() + ": setPanel: " + ex.getMessage());
    }

    validate();
  }

  public void updatePanel() {
    // -- check for color gradient panel
    processOptionalControls();
  }

  void processOptionalControls() {

    if (Shape3d == null) {
      return;
    }

    // --- Now we process only the first geometry
    Enumeration gIter = Shape3d.getAllGeometries();
    if (!gIter.hasMoreElements()) {
      this.controlColorGradientPanel1.setVisible(false);
      return;
    }

    Object obj = Shape3d.getUserData();
    if (obj instanceof Map) {
      Map<USER_DATA_FLAG, Object> shape3dUserData = (Map<USER_DATA_FLAG, Object>) obj;
      if (shape3dUserData != null) {
        ColorRangeScheme crange = (ColorRangeScheme) shape3dUserData.get(USER_DATA_FLAG.COLOR_RANGE_SCHEME_OBJECT);
        if (crange != null) {
          controlColorGradientPanel1.setColorGradient(crange);
        }
      }
    }
    /*
           while (gIter.hasMoreElements()) {
       Geometry geom = (Geometry) gIter.nextElement();
       Object ud = geom.getUserData();
       if (ud == null) {
          this.controlColorGradientPanel1.setVisible(false);
          break;
       }
       else if (! (ud instanceof HashMap)) {
          this.controlColorGradientPanel1.setVisible(false);
          break;
       }

       HashMap uData = (HashMap) ud;
       ArrayList<Color3f> palette = (ArrayList<Color3f>) uData.get(USER_DATA_FLAG.COLOR_PALETTE_ARRAY);
       Float min = (Float) uData.get(USER_DATA_FLAG.MIN_FUN_VALUE_AT_VERTICES);
       Float max = (Float) uData.get(USER_DATA_FLAG.MAX_FUN_VALUE_AT_VERTICES);

       Float clipMin = (Float) uData.get(USER_DATA_FLAG.MIN_CLIP_FUN_VALUE_AT_VERTICES);
       Float clipMax = (Float) uData.get(USER_DATA_FLAG.MAX_CLIP_FUN_VALUE_AT_VERTICES);

       controlColorGradientPanel1.setColorGradient(min, max, clipMin, clipMax, palette);
       this.controlColorGradientPanel1.setVisible(true);
       break;
           }
     */

  }

  @Override
  public void setNewColorScheme(ColorRangeScheme new_color_range) {
    if (Shape3d == null) {
      System.err.println(this.getClass().getCanonicalName() + ": setNewColorScheme: Shape3d == null");
      return;
    }

    Map<USER_DATA_FLAG, Object> shape3dUserData = (Map<USER_DATA_FLAG, Object>) Shape3d.getUserData();
    shape3dUserData.put(USER_DATA_FLAG.COLOR_RANGE_SCHEME_OBJECT, new_color_range);

    GaussianJava3dFactory.mapSurface(Shape3d, new_color_range);
  }

  public void setPanel(VerticesObject v_object) throws Exception {

    App = null;
    vObject = v_object;

    // --- Add a new item to rendering...

    renderingComboBox.setEnabled(false);
    renderingComboBox.addItem(MIXED_RENDERING);
    renderingComboBox.setSelectedItem(MIXED_RENDERING);
    renderingComboBox.setEnabled(true);

    // --- Setup visible checkbox

    visibleCheckBox.setEnabled(false);
    visibleCheckBox.setSelected(vObject.isVisible());
    visibleCheckBox.setEnabled(true);

    // --- Set opaque checkbox

    this.opaqueCheckBox.setEnabled(false);
    this.opaqueCheckBox.setSelected(vObject.isOpaque());
    this.opaqueCheckBox.setEnabled(true);

    this.opacitySlider.setEnabled(!opaqueCheckBox.isSelected());
    this.transpComboBox.setEnabled(!opaqueCheckBox.isSelected());

    // --- Set color palette

    java.util.List shape3ds = v_object.getShape3DElements();
    if (shape3ds.size() != 1) {
      this.controlColorGradientPanel1.setVisible(false);
    }
    else {
      Object obj = shape3ds.get(0);
      if (obj instanceof BranchGroup) {
        BranchGroup bg = (BranchGroup) obj;
        Enumeration enumer = bg.getAllChildren();
        while (enumer.hasMoreElements()) {
          Object bgChild = enumer.nextElement();
          if (bgChild instanceof Shape3D) {
            Shape3D shape3D = (Shape3D) bgChild;
            this.Shape3d = shape3D;
            processOptionalControls();
          }
        }
        //controlColorGradientPanel1.setVisible(false);
      }
      else {
        System.err.println("setPanel(VerticesObject v_object): unknown object " + obj.getClass().getCanonicalName() +
                           " Ignored...");
        controlColorGradientPanel1.setVisible(false);
      }

    }

    validate();
  }

  public void opaqueCheckBox_actionPerformed(ActionEvent e) {

    if (opaqueCheckBox.isSelected()) {
      //opacitySlider.setValue(0);
      transpComboBox.setEnabled(false);
      opacitySlider.setEnabled(false);

      opacitySlider.setEnabled(false);
      opacitySlider.setValue(opacitySlider.getMinimum());
      opacitySlider.setEnabled(true);

      if (App != null) {
        Appearance newApp = new Appearance();
        newApp.duplicateNodeComponent(App, false);
        newApp.setTransparencyAttributes(null);

        //Material material = newApp.getMaterial();
        //material.setDiffuseColor(diffuseColorStore);
        //material.setShininess(shininessStore);
        //newApp.setMaterial(material);
        Shape3d.setAppearance(newApp);
        App = newApp;
      }
      else if (vObject != null) {
        vObject.setOpaque(opaqueCheckBox.isSelected());
      }
    }

    // --- Set transparent
    else {
      transpComboBox.setEnabled(true);
      opacitySlider.setEnabled(true);
      if (App != null) {
        try {
          Appearance newApp = new Appearance();
          newApp.duplicateNodeComponent(App, false);
          TransparencyAttributes ta = newApp.getTransparencyAttributes();

          //Material material = App.getMaterial();

          //material.getDiffuseColor(diffuseColorStore);
          //material.setDiffuseColor(black);

          //shininessStore = material.getShininess();
          //material.setShininess(1.0f);

          //App.setMaterial(material);

          if (ta == null) {
            ta = new TransparencyAttributes();
          }

          float transp = ta.getTransparency();
          int value = (int) (transp * 100.0f);

          opacitySlider.setEnabled(false);
          opacitySlider.setValue(value);
          opacitySlider.setEnabled(true);

          int transpMode = ta.getTransparencyMode();

          String transpName = VerticesObjectProperties.
              getTransparencyModeAsString(transpMode);
          if (transpName != null) {
            transpComboBox.setEnabled(false);
            transpComboBox.setSelectedItem(transpName);
            transpComboBox.setEnabled(true);
          }

          Shape3d.setAppearance(newApp);
          App = newApp;
        }
        catch (Exception ex) {
          JOptionPane.showMessageDialog(this,
                                        "Cannot get transperancy attributes: " +
                                        ex.getMessage(), "Error",
                                        JOptionPane.ERROR_MESSAGE);
        }
      }

      else if (vObject != null) {
        vObject.setOpaque(opaqueCheckBox.isSelected());
      }

    }

  }

  public void renderingComboBox_itemStateChanged(ItemEvent e) {

    if (e.getStateChange() != ItemEvent.SELECTED) {
      return;
    }

    if (!renderingComboBox.isEnabled()) {
      return;
    }

    renderingComboBox.setEnabled(false);

    int polygonMode = VerticesObjectProperties.getRenderingMode(renderingComboBox.getSelectedItem().toString());

    if (App != null) {
      Appearance newApp = new Appearance();
      newApp.duplicateNodeComponent(App, false);
      try {
        PolygonAttributes pa = newApp.getPolygonAttributes();
        if (pa == null) {
          pa = new PolygonAttributes();
          pa.setCapability(PolygonAttributes.ALLOW_MODE_READ);
          pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
          pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_READ);
          pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);
        }
        pa.setPolygonMode(polygonMode);
        Shape3d.setAppearance(newApp);
        App = newApp;
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                                      "Cannot set polygon attributes: " +
                                      ex.getMessage(), "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }

    // --- Now effect if a mixed rendering is selected
    else if (vObject != null && !renderingComboBox.getSelectedItem().toString().equals(MIXED_RENDERING)) {
      vObject.setPolygonRendering(polygonMode);
    }

    renderingComboBox.setEnabled(true);
  }

  public void setPolygoneMode(int mode) throws Exception {

    try {
      VerticesObjectProperties.validateRenderingMode(mode);
    }
    catch (Exception ex) {
      mode = PolygonAttributes.POLYGON_FILL;
    }

    if (App != null) {
      Appearance newApp = new Appearance();
      newApp.duplicateNodeComponent(App, false);
      try {
        PolygonAttributes pa = newApp.getPolygonAttributes();
        if (pa == null) {
          pa = new PolygonAttributes();
          pa.setBackFaceNormalFlip(true);
          pa.setCullFace(PolygonAttributes.CULL_NONE);
        }
        pa.setPolygonMode(mode);
        newApp.setPolygonAttributes(pa);
        Shape3d.setAppearance(newApp);
        App = newApp;
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                                      "Cannot set polygon attributes: " +
                                      ex.getMessage(), "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }

    String modeName = VerticesObjectProperties.getRenderingModeAsString(mode);
    renderingComboBox.setEnabled(false);
    renderingComboBox.setSelectedItem(modeName);
    renderingComboBox.setEnabled(true);
  }

  public void visibleCheckBox_actionPerformed(ActionEvent e) {
    enableAllControls(visibleCheckBox.isSelected());

    // --- If it's a Shape3d
    if (Shape3d != null) {
      if (shape3d_Parent == null || grandParent == null) {
        return;
      }
      if (shape3d_Parent instanceof BranchGroup) {
        if (visibleCheckBox.isSelected()) {
          //( (BranchGroup) shape3d_Parent).addChild(Shape3d);
          if (grandParent instanceof BranchGroup) {
            ( (BranchGroup) grandParent).addChild( (BranchGroup) shape3d_Parent);
          }
          else if (grandParent instanceof TransformGroup) {
            ( (TransformGroup) grandParent).addChild( (BranchGroup) shape3d_Parent);
          }

        }
        else {
          ( (BranchGroup) shape3d_Parent).detach();
        }
      }
      else {
        System.err.println(this.getClass().getCanonicalName() + ": don't know how to handle class " +
                           shape3d_Parent.getClass().getCanonicalName());
      }
    }

    // --- if it's a VerticesObject object

    else if (vObject != null) {
      vObject.setVisible(visibleCheckBox.isSelected());
    }
  }

  public void setObjectVisible(boolean visible) {
    if (visible != visibleCheckBox.isSelected()) {
      //visibleCheckBox.setSelected(visible);
      visibleCheckBox.doClick();
      validate();
    }
  }

  public void setShininess(float shininess) {

    if (shininess < 1.0f) {
      shininess = 1.0f;
    }
    else if (shininess > 128.0f) {
      shininess = 128.0f;
    }
    logger.info("Setting Shininess: " + (int) shininess);
    //shininessSlider.setEnabled(false);
    this.shininessSlider.setValue( (int) shininess);
    //shininessSlider.setEnabled(true);
  }

  public void setTransparency(float transp) {
    if (opaqueCheckBox.isSelected()) {
      return;
    }

    if (transp < 0) {
      transp = 0;
    }
    else if (transp > 1) {
      transp = 1;
    }

    this.opacitySlider.setValue( (int) (transp * opacitySlider.getMaximum()));
  }

  public void setTransparencyMode(int mode) {
    if (opaqueCheckBox.isSelected()) {
      return;
    }

    String modeName = VerticesObjectProperties.getTransparencyModeAsString(mode);
    if (modeName != null) {
      this.transpComboBox.setSelectedItem(modeName);
      transpComboBox.revalidate();
    }
  }

  public void setObjectOpaque(boolean opaque) {
    if (opaque != opaqueCheckBox.isSelected()) {
      //visibleCheckBox.setSelected(visible);
      opaqueCheckBox.doClick();
      validate();
    }
  }

  public void enableAllControls(boolean enable) {
    renderingComboBox.setEnabled(enable);
    opaqueCheckBox.setEnabled(enable);
    transpComboBox.setEnabled(enable);
    opacitySlider.setEnabled(enable);
  }

  private class VerticesPropertyPanel_renderingComboBox_itemAdapter
      implements ItemListener {
    private VerticesPropertyPanel adaptee;
    VerticesPropertyPanel_renderingComboBox_itemAdapter(VerticesPropertyPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
      adaptee.renderingComboBox_itemStateChanged(e);
    }
  }

  public void transpComboBox_itemStateChanged(ItemEvent e) {

    if (e.getStateChange() != ItemEvent.SELECTED) {
      return;
    }

    if (!transpComboBox.isEnabled()) {
      return;
    }
    transpComboBox.setEnabled(false);

    int transpMode = VerticesObjectProperties.getTransparencyMode(transpComboBox.getSelectedItem().toString());

    // --- set shape3d

    if (App != null) {
      Appearance newApp = new Appearance();
      newApp.duplicateNodeComponent(App, false);

      try {
        TransparencyAttributes ta = newApp.getTransparencyAttributes();

        if (ta == null) {
          ta = new TransparencyAttributes();
          ta.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_READ);
          ta.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_WRITE);
          ta.setCapability(TransparencyAttributes.ALLOW_MODE_READ);
          ta.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
          ta.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
          ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
          newApp.setTransparencyAttributes(ta);
        }

        //PolygonAttributes pa = newApp.getPolygonAttributes();
        //pa.setCullFace(pa.CULL_FRONT);

        float value = opacitySlider.getValue();

        float transp = value / opacitySlider.getMaximum();
        ta.setTransparency(transp);
        ta.setTransparencyMode(transpMode);

        Shape3d.setAppearance(newApp);
        App = newApp;
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                                      "Cannot set transperancy attributes: " +
                                      ex.getMessage(), "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }

    // --- Setup visual object

    else if (vObject != null) {
      vObject.setTransparencyMode(transpMode);
    }

    transpComboBox.setEnabled(true);
  }

  public void opacitySlider_stateChanged(ChangeEvent e) {
    if (!opacitySlider.isEnabled()) {
      return;
    }

    if (opacitySlider.getValueIsAdjusting()) {
      return;
    }

    opacitySlider.setEnabled(false);

    float value = (float) opacitySlider.getValue() / (float) opacitySlider.getMaximum();

    // --- Adjust shape3d...
    if (Shape3d != null && App != null) {
      try {
        Appearance newApp = new Appearance();
        newApp.duplicateNodeComponent(App, false);

        TransparencyAttributes ta = newApp.getTransparencyAttributes();

        if (ta == null) {
          ta = new TransparencyAttributes();
          ta.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_READ);
          ta.setCapability(TransparencyAttributes.ALLOW_BLEND_FUNCTION_WRITE);
          ta.setCapability(TransparencyAttributes.ALLOW_MODE_READ);
          ta.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
          ta.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
          ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
          //App.setTransparencyAttributes(ta);
          newApp.setTransparencyAttributes(ta);
        }

        //PolygonAttributes pa = App.getPolygonAttributes();
        PolygonAttributes pa = newApp.getPolygonAttributes();
        //pa.setCullFace(pa.CULL_FRONT);

        ta.setTransparency(value);

        int transpMode = VerticesObjectProperties.getTransparencyMode(transpComboBox.getSelectedItem().toString());
        ta.setTransparencyMode(transpMode);

        Shape3d.setAppearance(newApp);
        App = newApp;
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                                      "Cannot set transperancy attributes: " +
                                      ex.getMessage(), "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }

    // --- Adjust visual object

    else if (vObject != null) {
      vObject.setTransparency(value);
    }

    opacitySlider.setEnabled(true);
  }

  public boolean isObjectVisible() {
    return visibleCheckBox.isSelected();
  }

  public boolean isObjectOpaque() {
    return this.opaqueCheckBox.isSelected();
  }

  public void shininessSlider_stateChanged(ChangeEvent e) {
    if (!shininessSlider.isEnabled()) {
      return;
    }

    if (shininessSlider.getValueIsAdjusting()) {
      return;
    }

    shininessSlider.setEnabled(false);

    float shininess = shininessSlider.getValue();
    logger.info("New Shininess: " + (int) shininess);

    // --- Adjust shape3d...
    if (Shape3d != null && App != null) {
      try {
        Appearance newApp = new Appearance();
        newApp.duplicateNodeComponent(App, false);

        Material material = newApp.getMaterial();
        if (material == null) {
          material = new Material();
          material.setCapability(Material.ALLOW_COMPONENT_READ);
          material.setCapability(Material.ALLOW_COMPONENT_WRITE);
        }
        material.setShininess(shininess);

        Shape3d.setAppearance(newApp);
        App = newApp;
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                                      "Cannot set shininess in Material: " +
                                      ex.getMessage(), "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }

    // --- Adjust visual object

    else if (vObject != null) {
      vObject.setShininess(shininess);
    }

    shininessSlider.setEnabled(true);

  }

  private class VerticesPropertyPanel_opaqueCheckBox_actionAdapter
      implements ActionListener {
    private VerticesPropertyPanel adaptee;
    VerticesPropertyPanel_opaqueCheckBox_actionAdapter(VerticesPropertyPanel
        adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.opaqueCheckBox_actionPerformed(e);
    }
  }

  private class VerticesPropertyPanel_visibleCheckBox_actionAdapter
      implements ActionListener {
    private VerticesPropertyPanel adaptee;
    VerticesPropertyPanel_visibleCheckBox_actionAdapter(VerticesPropertyPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.visibleCheckBox_actionPerformed(e);
    }
  }

  private class VerticesPropertyPanel_opacitySlider_changeAdapter
      implements ChangeListener {
    private VerticesPropertyPanel adaptee;
    VerticesPropertyPanel_opacitySlider_changeAdapter(VerticesPropertyPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      adaptee.opacitySlider_stateChanged(e);
    }
  }

  private class VerticesPropertyPanel_transpComboBox_itemAdapter
      implements ItemListener {
    private VerticesPropertyPanel adaptee;
    VerticesPropertyPanel_transpComboBox_itemAdapter(VerticesPropertyPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
      adaptee.transpComboBox_itemStateChanged(e);
    }
  }

}

class VerticesPropertyPanel_shininessSlider_changeAdapter
    implements ChangeListener {
  private VerticesPropertyPanel adaptee;
  VerticesPropertyPanel_shininessSlider_changeAdapter(VerticesPropertyPanel adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    adaptee.shininessSlider_stateChanged(e);
  }
}
