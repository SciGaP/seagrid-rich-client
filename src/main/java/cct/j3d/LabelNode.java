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
import java.awt.Font;

import org.scijava.java3d.Appearance;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.Group;
import org.scijava.java3d.Material;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Matrix3d;
import org.scijava.vecmath.Point3f;
import org.scijava.vecmath.Vector3d;
import org.scijava.vecmath.Vector3f;

import org.scijava.java3d.utils.geometry.Text2D;

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
/*
 A Text2D object is a representation of a string as a texture mapped rectangle.
 The texture for the rectangle shows the string as rendered in the specified color
 with a transparent background. The appearance of the characters is specified
 using the font indicated by the font name, size and style (see java.awt.Font).
 The approximate height of the rendered string will be the font size times the
 rectangle scale factor, which has a default value of 1/256.
 For example, a 12 point font will produce characters that are about
 12/256 = 0.047 meters tall. The lower left corner of the rectangle is
 located at (0,0,0) with the height extending along the positive y-axis
 and the width extending along the positive x-axis.
 */

public class LabelNode
    extends BranchGroup {

   BranchGroup branchGroupLabel = new BranchGroup();
   Text2D label;
   Transform3D transl_center;
   Vector3f coordCenter;
   TransformGroup center;
   float shiftFromCenter;
   Color3f labelColor = new Color3f(Color.green);
   Appearance labelAppearance = null;
   int fontSize = 75;
   int fontStyle = Font.ITALIC;
   String fontName = "Helvetica";
   Transform3D move = new Transform3D();
   TransformGroup shift = new TransformGroup();
   Transform3D rotate_label = new Transform3D();
   TransformGroup rotate = new TransformGroup();
   float angle = 0;
   float rectangleScaleFactor;

   public LabelNode(Vector3f coord, String text) {
      createLabel(coord, text, labelColor, fontName, fontSize,
                  Font.ITALIC);
   }

   public LabelNode(Vector3f coord, String text, Color3f color, Font font) {
      this(coord, text, color, font.getFontName(), font.getSize(),
           font.getStyle());
   }

   public LabelNode(Vector3f coord, String text, Color3f color,
                    String font_name, int font_size, int font_style) {

   }

   public void createLabel(Vector3f coord, String text, Color3f color,
                           String font_name, int font_size, int font_style) {

      fontSize = font_size;
      fontStyle = font_style;
      fontName = font_name;

      labelColor.set(color);

      coordCenter = coord;

      transl_center = new Transform3D();
      transl_center.setTranslation(coord);
      center = new TransformGroup();
      center.setTransform(transl_center);

      label = createLabel(text, color, font_name, font_size, font_style);
      rectangleScaleFactor = label.getRectangleScaleFactor();

      //label = new Text2D(text, color, "Helvetica", 50, Font.ITALIC);
      //new Color3f(0.9f, 1.0f, 1.0f),

      branchGroupLabel.setCapability(Group.ALLOW_CHILDREN_READ);
      branchGroupLabel.setCapability(Group.ALLOW_CHILDREN_WRITE);
      branchGroupLabel.setCapability(BranchGroup.ALLOW_DETACH);

      branchGroupLabel.addChild(label);

      rotate.setTransform(rotate_label);
      rotate.addChild(branchGroupLabel);

      shift.setTransform(move);
      shift.addChild(rotate);

      center.addChild(shift);
      this.addChild(center);

      setCapability(BranchGroup.ALLOW_DETACH);
      setCapability(Group.ALLOW_CHILDREN_READ);
      setCapability(Group.ALLOW_CHILDREN_WRITE);

      rotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      rotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      rotate.setCapability(Group.ALLOW_CHILDREN_READ);
      rotate.setCapability(Group.ALLOW_CHILDREN_WRITE);
      rotate.setCapability(Group.ALLOW_CHILDREN_EXTEND);

      shift.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
      shift.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
   }

   Text2D createLabel(String text, Color3f color,
                      String font_name, int font_size, int font_style) {

      Text2D label_text = new Text2D(text, color, font_name, font_size,
                                     font_style);
      label_text.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      label_text.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
      label_text.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
      label_text.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
      label_text.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
      label_text.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      label_text.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      label_text.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

      labelAppearance = label_text.getAppearance();
      labelAppearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
      labelAppearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
      labelAppearance.setCapability(Appearance.ALLOW_MATERIAL_READ);
      labelAppearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

      labelAppearance.getMaterial().setCapability(Material.ALLOW_COMPONENT_READ);
      labelAppearance.getMaterial().setCapability(Material.
                                                  ALLOW_COMPONENT_WRITE);

      labelAppearance.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
      labelAppearance.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);

      //label = new Text2D(text, color, "Helvetica", 50, Font.ITALIC);
      //new Color3f(0.9f, 1.0f, 1.0f),

      return label_text;
   }

   public void setShift(Vector3d dir) {
      shiftFromCenter = (float) dir.length();
      shift.getTransform(move);
      move.setTranslation(dir);
      shift.setTransform(move);
   }

   public float getShift() {
      return shiftFromCenter;
   }

   public void updateRotation(Matrix3d rotation) {
      rotate.getTransform(rotate_label);
      rotate_label.setRotation(rotation);
      rotate.setTransform(rotate_label);
   }

   public void getCenterCoord(Vector3f coord) {
      coord.set(coordCenter);
   }

   public void getCenterCoord(Point3f coord) {
      coord.set(coordCenter);
   }

   public void setTextSize(double value) {
      float rectangle_scale_factor = label.getRectangleScaleFactor();
      rectangleScaleFactor = (float) value / fontSize;
      label.setRectangleScaleFactor(rectangleScaleFactor);
   }

   public void setColor(Color color) {
      //ColoringAttributes ca = labelAppearance.getColoringAttributes();
      //ca.setColor(new Color3f(color));
      //labelAppearance.setColoringAttributes(ca);
      /*
             Material material = labelAppearance.getMaterial();
             Color3f color3f = new Color3f();
             material.getAmbientColor(color3f);
       logger.info("Ambient:" + color3f.x + " " + color3f.y + " " +
                         color3f.z);
             material.getDiffuseColor(color3f);
       logger.info("Diffuse:" + color3f.x + " " + color3f.y + " " +
                         color3f.z);
             material.getEmissiveColor(color3f);
       logger.info("Emissive:" + color3f.x + " " + color3f.y + " " +
                         color3f.z);
             material.getSpecularColor(color3f);
       logger.info("Specular:" + color3f.x + " " + color3f.y + " " +
                         color3f.z);

             TextureAttributes ta = labelAppearance.getTextureAttributes();
       */

      branchGroupLabel.detach();
      branchGroupLabel.removeChild(label);
      String text = label.getString();
      label = this.createLabel(text, new Color3f(color), fontName, fontSize,
                               fontStyle);
      label.setRectangleScaleFactor(rectangleScaleFactor);
      branchGroupLabel.addChild(label);
      rotate.addChild(branchGroupLabel);
   }

}
