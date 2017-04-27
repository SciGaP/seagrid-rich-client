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

import java.util.LinkedHashMap;
import java.util.Map;

import org.scijava.java3d.Appearance;
import org.scijava.java3d.Material;
import org.scijava.java3d.PolygonAttributes;
import org.scijava.java3d.TransparencyAttributes;
import org.scijava.vecmath.Color3f;

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
public class VerticesObjectProperties {

  public static final int SOLID = 0;
  public static final int WIREFRAME = 1;
  public static final int POINTS = 2;

  static final String[] Style_name = {
      "Solid", "Wireframe", "Points"};

  static final String[] Transp_modes = {
      "Fastest", "Nicest", "Screen Door", "Blended"};

  static final Map availableTransp = new LinkedHashMap();

  static {
    availableTransp.put("Fastest", new Integer(TransparencyAttributes.FASTEST));
    availableTransp.put("Nicest", new Integer(TransparencyAttributes.NICEST));
    availableTransp.put("Screen Door",
                        new Integer(TransparencyAttributes.SCREEN_DOOR));
    availableTransp.put("Blended", new Integer(TransparencyAttributes.BLENDED));
  }

  boolean Visible = true;
  boolean Lit = true;
  int Style = SOLID;
  int transparencyMode = TransparencyAttributes.BLENDED;
  float Transparency = 0;
  int polygonMode = PolygonAttributes.POLYGON_FILL;
  int cullFace = PolygonAttributes.CULL_NONE;
  boolean backFaceNormalFlip = true;

  public VerticesObjectProperties() {
  }

  public static void main(String[] args) {
    VerticesObjectProperties verticesobjectproperties = new
        VerticesObjectProperties();
  }

  public void setLit(boolean lit) {
    Lit = lit;
  }

  public boolean isLit() {
    return Lit;
  }

  public int getStyle() {
    return Style;
  }

  public void setStyle(int style) {
    if (style < SOLID || style > POINTS) {
      System.err.println(this.getClass().getCanonicalName() +
                         ": setStyle: style < SOLID || style > POINTS: ignored...");
    }
    else {
      Style = style;
    }
  }

  public String getStyleAsString() {
    return Style_name[Style];
  }

  public int getTransparencyMode() {
    return transparencyMode;
  }

  public void setTransparencyMode(int mode) {
    if (mode != TransparencyAttributes.NONE &&
        mode != TransparencyAttributes.FASTEST &&
        mode != TransparencyAttributes.NICEST &&
        mode != TransparencyAttributes.SCREEN_DOOR &&
        mode != TransparencyAttributes.BLENDED) {
      System.err.println(this.getClass().getCanonicalName() +
                         ": setTransparency: Wrong Transparency value: ignored...");
      return;
    }
    transparencyMode = mode;
  }

  public float getTransparency() {
    return Transparency;
  }

  /**
   *
   * @param value float - the appearance's transparency in the range [0.0, 1.0]
   * with 0.0 being fully opaque and 1.0 being fully transparent
   */
  public void setTransparency(float value) {
    if (value < 0) {
      value = 0;
    }
    else if (value > 1) {
      value = 1;
    }
    Transparency = value;
  }

  public int getPolygonMode() {
    return polygonMode;
  }

  /**
   * Sets the polygon rasterization mode.
   * @param mode int - Could be PolygonAttributes.POLYGON_FILL, PolygonAttributes.POLYGON_LINE,
   * or PolygonAttributes.POLYGON_POINT
   */
  public void setPolygonMode(int mode) {
    if (mode != PolygonAttributes.POLYGON_FILL &&
        mode != PolygonAttributes.POLYGON_LINE &&
        mode != PolygonAttributes.POLYGON_POINT) {
      System.err.println(this.getClass().getCanonicalName() +
                         ": setPolygonMode: Wrong Transparency value: ignored...");
      return;
    }
    polygonMode = mode;
  }

  public Appearance getAppearence(Color3f objColor) {

    Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    Appearance app = new Appearance();

    app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
    app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);

    app.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_READ);
    app.setCapability(Appearance.ALLOW_LINE_ATTRIBUTES_WRITE);

    app.setCapability(Appearance.ALLOW_MATERIAL_READ);
    app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

    app.setCapability(Appearance.ALLOW_POINT_ATTRIBUTES_READ);
    app.setCapability(Appearance.ALLOW_POINT_ATTRIBUTES_WRITE);

    app.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
    app.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

    app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
    app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);

    // ---  Set up the material properties

    Material material = new Material();

    // --- Diffuse Color
    material.setDiffuseColor(objColor);

    // --- Ambient Color
    //material.setAmbientColor(0.1f * objColor.x, 0.1f * objColor.y, 0.1f * objColor.z);

    // --- Emissive Color
    //material.setEmissiveColor(0.2f * objColor.x, 0.2f * objColor.y, 0.2f * objColor.z);
    material.setEmissiveColor(0, 0, 0);

    // --- Specular Color
    //material.setSpecularColor(1.0f, 1.0f, 1.0f);
    //material.setSpecularColor(objColor);

    // --- Set Shininess
    material.setShininess(15.0f);

    material.setCapability(Material.ALLOW_COMPONENT_READ);
    material.setCapability(Material.ALLOW_COMPONENT_WRITE);

    material.setColorTarget(Material.AMBIENT_AND_DIFFUSE);

    app.setMaterial(material);
    //app.setMaterial(new Material(objColor, black, objColor,
    //                             white, 80.0f));

    // --- Set up the transparency properties

    /*
           TransparencyAttributes ta = new TransparencyAttributes();
           ta.setTransparencyMode(transparencyMode);
           ta.setTransparency(Transparency);
           app.setTransparencyAttributes(ta);
     */

    // --- Set up the polygon attributes

    PolygonAttributes pa = new PolygonAttributes();

    pa.setCapability(PolygonAttributes.ALLOW_MODE_READ);
    pa.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);

    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_READ);
    pa.setCapability(PolygonAttributes.ALLOW_CULL_FACE_WRITE);

    pa.setPolygonMode(polygonMode);
    pa.setCullFace(cullFace);
    pa.setBackFaceNormalFlip(backFaceNormalFlip);
    app.setPolygonAttributes(pa);

    return app;
  }

  public static String[] getAvailablePolygonModes() {
    return Style_name;
  }

  public static String[] getAvailableTranspModes() {
    return Transp_modes;
  }

  public static int getDefaultPolygonMode() {
    return 0;
  }

  public static int getDefaultTranspMode() {
    return 3;
  }

  public static String getTransparencyModeAsString(int mode) {
    //static final String[] Transp_modes = {
    // "Fastest", "Nicest", "Screen Door", "Blended"};

    switch (mode) {
      case TransparencyAttributes.BLENDED:
        return Transp_modes[3];
      case TransparencyAttributes.FASTEST:
        return Transp_modes[0];
      case TransparencyAttributes.NICEST:
        return Transp_modes[1];
      case TransparencyAttributes.SCREEN_DOOR:
        return Transp_modes[2];
    }
    return null;
  }

  public static int getRenderingMode(String mode) {
    //"Solid", "Wireframe", "Points"
    if (mode.equals(Style_name[0])) {
      return PolygonAttributes.POLYGON_FILL;
    }
    else if (mode.equals(Style_name[1])) {
      return PolygonAttributes.POLYGON_LINE;
    }
    else if (mode.equals(Style_name[2])) {
      return PolygonAttributes.POLYGON_POINT;
    }

    System.err.println("Unknown Polygon Attribute: " + mode + " Set to PolygonAttributes.POLYGON_FILL");
    return PolygonAttributes.POLYGON_FILL;
  }

  public static String getRenderingModeAsString(int mode) throws Exception {
    switch (mode) {
      case PolygonAttributes.POLYGON_FILL:
        return Style_name[0];
      case PolygonAttributes.POLYGON_LINE:
        return Style_name[1];
      case PolygonAttributes.POLYGON_POINT:
        return Style_name[2];
    }

    System.err.println("getRenderingModeAsString: unknown polygon mode");
    throw new Exception("getRenderingModeAsString: unknown polygon mode");
  }

  public static void validateRenderingMode(int mode) throws Exception {
    switch (mode) {
      case PolygonAttributes.POLYGON_FILL:
      case PolygonAttributes.POLYGON_LINE:
      case PolygonAttributes.POLYGON_POINT:
        return;
    }

    System.err.println("validateRenderingMode: unknown polygon mode");
    throw new Exception("validateRenderingMode: unknown polygon mode");
  }

  public static int getTransparencyMode(String mode) {
    //"Fastest", "Nicest", "Screen Door", "Blended"};
    if (mode.equals(Transp_modes[0])) {
      return TransparencyAttributes.FASTEST;
    }
    else if (mode.equals(Transp_modes[1])) {
      return TransparencyAttributes.NICEST;
    }
    else if (mode.equals(Transp_modes[2])) {
      return TransparencyAttributes.SCREEN_DOOR;
    }
    else if (mode.equals(Transp_modes[3])) {
      return TransparencyAttributes.BLENDED;
    }

    System.err.println("Unknown Transparency Attribute: " + mode + " Set to TransparencyAttributes.BLENDED");
    return TransparencyAttributes.BLENDED;
  }

}
