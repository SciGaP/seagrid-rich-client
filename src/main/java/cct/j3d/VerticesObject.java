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

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.Group;
import org.scijava.java3d.Shape3D;

import cct.interfaces.GraphicsObjectInterface;
import cct.j3d.ui.VerticesPropertyPanel;

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
public class VerticesObject
    implements GraphicsObjectInterface {

  String Name;
  List Graphics = new ArrayList();
  List Elements = new ArrayList();
  Map<Object, VerticesPropertyPanel> visualComponents = new HashMap<Object, VerticesPropertyPanel> ();
  Map AuxNames = new HashMap();
  VerticesPropertyPanel propPanel = null;

  public VerticesObject() {
  }

  public static void main(String[] args) {
    VerticesObject verticesobject = new VerticesObject();
  }

  public void setPolygonRendering(int mode) {
    for (int i = 0; i < Graphics.size(); i++) {
      Object obj = Graphics.get(i);
      if (obj instanceof VerticesObject) {
        ( (VerticesObject) obj).setPolygonRendering(mode);
      }
      else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
        VerticesPropertyPanel vComp = visualComponents.get(obj);

        if (vComp == null) {
          vComp = new VerticesPropertyPanel();
          visualComponents.put(obj, vComp);
        }

        try {
          vComp.setPolygoneMode(mode);
        }
        catch (Exception ex) {}
      }
      else {
        System.err.println(this.getClass().getCanonicalName() + ": setPolygonRendering: don't know how to handle class " +
                           obj.getClass().getCanonicalName());
      }
    }

  }

  public void setShininess(float shininess) {
    for (int i = 0; i < Graphics.size(); i++) {
      Object obj = Graphics.get(i);
      if (obj instanceof VerticesObject) {
        ( (VerticesObject) obj).setShininess(shininess);
      }
      else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
        VerticesPropertyPanel vComp = visualComponents.get(obj);

        if (vComp == null) {
          vComp = new VerticesPropertyPanel();
          visualComponents.put(obj, vComp);
        }

        vComp.setShininess(shininess);
      }
      else {
        System.err.println(this.getClass().getCanonicalName() + ": setShininess: don't know how to handle class " +
                           obj.getClass().getCanonicalName());
      }

    }
  }

  public void setTransparency(float transp) {
    for (int i = 0; i < Graphics.size(); i++) {
      Object obj = Graphics.get(i);
      if (obj instanceof VerticesObject) {
        ( (VerticesObject) obj).setTransparency(transp);
      }
      else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
        VerticesPropertyPanel vComp = visualComponents.get(obj);

        if (vComp == null) {
          vComp = new VerticesPropertyPanel();
          visualComponents.put(obj, vComp);
        }

        vComp.setTransparency(transp);
      }
      else {
        System.err.println(this.getClass().getCanonicalName() + ": setTransparency: don't know how to handle class " +
                           obj.getClass().getCanonicalName());
      }

    }
  }

  public void setTransparencyMode(int mode) {
    for (int i = 0; i < Graphics.size(); i++) {
      Object obj = Graphics.get(i);
      if (obj instanceof VerticesObject) {
        ( (VerticesObject) obj).setTransparencyMode(mode);
      }
      else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
        VerticesPropertyPanel vComp = visualComponents.get(obj);

        if (vComp == null) {
          vComp = new VerticesPropertyPanel();
          visualComponents.put(obj, vComp);
        }

        vComp.setTransparencyMode(mode);
      }
      else {
        System.err.println(this.getClass().getCanonicalName() + ": setTransparencyMode: don't know how to handle class " +
                           obj.getClass().getCanonicalName());
      }

    }
  }

  /**
   * Returns true is at least one component is opaque
   * @return boolean
   */
  public boolean isOpaque() {
    for (int i = 0; i < Graphics.size(); i++) {
      Object obj = Graphics.get(i);

      //if (obj instanceof GraphicsObjectInterface) {
      //   GraphicsObjectInterface graphics = (GraphicsObjectInterface) obj;
      //   if ( graphics.isVisible() ) return true;
      //}
      if (obj instanceof VerticesObject) {
        VerticesObject graphics = (VerticesObject) obj;
        if (graphics.isOpaque()) {
          return true;
        }
      }

      else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
        VerticesPropertyPanel vComp = visualComponents.get(obj);
        if (vComp != null && vComp.isObjectOpaque()) {
          return true;
        }
      }
      else {
        System.err.println(
            "isOpaque: don't know how to handle class " + obj.getClass().getCanonicalName());
      }
    }
    return false;
  }

  /**
   * Returns "true" if it's visible at least one component
   * @return boolean
   */
  @Override
  public boolean isVisible() {
    for (int i = 0; i < Graphics.size(); i++) {
      Object obj = Graphics.get(i);

      if (obj instanceof GraphicsObjectInterface) {
        GraphicsObjectInterface graphics = (GraphicsObjectInterface) obj;
        if (graphics.isVisible()) {
          return true;
        }
      }
      else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
        VerticesPropertyPanel vComp = visualComponents.get(obj);
        if (vComp != null && vComp.isObjectVisible()) {
          return true;
        }
      }
      else {
        System.err.println(
            "isVisible: don't know how to handle class " + obj.getClass().getCanonicalName());
      }
    }
    return false;
  }

  /*
      public void setVisible(boolean visible) {

     propPanel.setObjectOpaque(visible);

     for (int i = 0; i < Graphics.size(); i++) {
        Object obj = Graphics.get(i);

        if (obj instanceof GraphicsObjectInterface) {
           GraphicsObjectInterface graphics = (GraphicsObjectInterface) obj;
           graphics.setVisible(visible);
        }
        else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
           VerticesPropertyPanel vComp = visualComponents.get(obj);
           if (vComp != null && vComp.isObjectVisible()) {
              vComp.setObjectVisible(visible);
           }
        }
        else {
           System.err.println(
               "setVisible: don't know how to handle class " + obj.getClass().getCanonicalName());
        }
     }
      }
   */

  @Override
  public void addGraphics(GraphicsObjectInterface graphics) {

    Graphics.add(graphics);
    visualComponents.put(graphics, null);
    Elements.addAll(graphics.getShape3DElements());
  }

  @Override
  public void addGraphics(String name, Shape3D graphics) {

    BranchGroup bGroup = new BranchGroup();
    bGroup.setCapability(Group.ALLOW_CHILDREN_READ);
    bGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
    bGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    bGroup.setCapability(BranchGroup.ALLOW_DETACH);
    bGroup.addChild(graphics);
    //bGroup.setName(name); // --- >1.4 java3d feature

    Graphics.add(bGroup);
    visualComponents.put(graphics, null);
    Elements.add(bGroup);
    //Elements.add(graphics);
    AuxNames.put(graphics, name);
  }

  @Override
  public void removeAllGraphics() {
    int n = Graphics.size() - 1;

    for (int i = n; i >= 0; i--) {
      Object obj = Graphics.get(i);

      if (obj instanceof GraphicsObjectInterface) {
        GraphicsObjectInterface graphics = (GraphicsObjectInterface) obj;
        graphics.removeAllGraphics();
        Graphics.remove(i);
      }
      else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
        this._removeGraphics(obj);
        Graphics.remove(i);
      }
      else {
        System.err.println(
            "isVisible: don't know how to handle class " + obj.getClass().getCanonicalName());
      }
    }

    visualComponents.clear();
    Elements.clear();
    AuxNames.clear();
  }

  private void _removeGraphics(Object graphics) {

    visualComponents.remove(graphics);
    Elements.remove(graphics);
    AuxNames.remove(graphics);

    // --- Now detach it from root node

    if (graphics instanceof BranchGroup) {
      BranchGroup bg = (BranchGroup) graphics;
      if (bg.getParent() != null) {
        bg.detach();
        bg = null;
      }
    }
    else {
      System.err.println(
          "_removeGraphics: dont know how to handle class " + graphics.getClass().getCanonicalName());
    }
  }

  @Override
  public void removeGraphics(Object graphics) {

    if (!Graphics.contains(graphics)) {
      System.err.println(
          "removeGraphics: !Graphics.contains(graphics)");
      return;
    }

    visualComponents.remove(graphics);

    Graphics.remove(graphics);

    Elements.remove(graphics);

    AuxNames.remove(graphics);

    // --- Now detach it from root node

    if (graphics instanceof BranchGroup) {
      BranchGroup bg = (BranchGroup) graphics;
      if (bg.getParent() != null) {
        bg.detach();
        bg = null;
      }
    }
    else {
      System.err.println(
          "removeGraphics: dont know how to handle class " + graphics.getClass().getCanonicalName());
    }
  }

  @Override
  public void setVisible(Object graphics, boolean visible) {

    if (!Graphics.contains(graphics)) {
      System.err.println(
          "setVisible: !Graphics.contains(graphics)");
      return;
    }

    if (graphics instanceof GraphicsObjectInterface) {
      GraphicsObjectInterface goi = (GraphicsObjectInterface) graphics;
      goi.setVisible(visible);
    }
    else if (graphics instanceof Shape3D || graphics instanceof BranchGroup) {
      VerticesPropertyPanel vComp = visualComponents.get(graphics);
      if (vComp != null && vComp.isObjectVisible()) {
        vComp.setObjectVisible(visible);
      }
    }
    else {
      System.err.println(
          "setVisible: don't know how to handle class " + graphics.getClass().getCanonicalName());
    }

    visualComponents.remove(graphics);

    Graphics.remove(graphics);

    Elements.remove(graphics);

    AuxNames.remove(graphics);

    // --- Now detach it from root node

    if (graphics instanceof BranchGroup) {
      BranchGroup bg = (BranchGroup) graphics;
      if (bg.getParent() != null) {
        bg.detach();
        bg = null;
      }
    }
    else {
      System.err.println(
          "removeGraphics: dont know how to handle class " + graphics.getClass().getCanonicalName());
    }
  }

  @Override
  public String getName() {
    return Name;
  }

  @Override
  public int getNGraphicsElements() {
    return Graphics.size();
  }

  @Override
  public Object getGraphicsElement(int n) {
    if (Graphics == null || n < 0 || n >= Graphics.size()) {
      System.err.println(
          "getGraphicsElement: Graphics == null || n < 0 || n >=Graphics.size()");
      return null;
    }
    return Graphics.get(n);
  }

  @Override
  public List getShape3DElements() {
    return new ArrayList(Elements);
  }

  @Override
  public void setName(String name) {
    Name = name;
  }

  @Override
  public Component getVisualComponent() {
    if (propPanel == null) {

      // --- initiate all visual components

      for (int i = 0; i < Graphics.size(); i++) {
        Object obj = Graphics.get(i);

        if (obj instanceof GraphicsObjectInterface) {
          GraphicsObjectInterface graphics = (GraphicsObjectInterface) obj;
          graphics.getVisualComponent(); // just to initiate it...
        }
        else if (obj instanceof Shape3D) {
          getVisualComponent(i); // just to initiate it...
        }
        else if (obj instanceof BranchGroup) {
          getVisualComponent(i); // just to initiate it...
        }

        else {
          System.err.println(
              "getVisualComponent: don't know how to handle class " + obj.getClass().getCanonicalName());
        }
      }

      propPanel = new VerticesPropertyPanel();
      try {
        propPanel.setPanel(this);
      }
      catch (Exception ex) {
        System.err.println(this.getClass().getCanonicalName() +
                           ": getVisualComponent: " + ex.getMessage());
      }

    }

    if (Graphics.size() < 1) {
      System.err.println(this.getClass().getCanonicalName() + ": getVisualComponent: Graphics.size() < 1");
      return null;
    }
    boolean graphics = Graphics.get(0) instanceof Shape3D || Graphics.get(0) instanceof BranchGroup;
    if (getNGraphicsElements() == 1 && graphics) {
      return getVisualComponent(0);
      /*
               Shape3D shape3d = (Shape3D) Graphics.get(0);
               try {
         Appearance app = shape3d.getAppearance();
         propPanel.setPanel(app);
                }
                catch (Exception ex) {
         System.err.println(this.getClass().getCanonicalName() +
                            ": getVisualComponent: " + ex.getMessage());
                }
       */
    }

    propPanel.updatePanel();
    return propPanel;
  }

  @Override
  public Component getVisualComponent(int n) {

    if (Graphics == null || n < 0 || n >= Graphics.size()) {
      System.err.println(
          "getVisualComponent: Graphics == null || n < 0 || n >=Graphics.size()");
      return null;
    }

    Object obj = Graphics.get(n);

    if (obj instanceof GraphicsObjectInterface) {
      GraphicsObjectInterface graphics = (GraphicsObjectInterface) obj;
      return graphics.getVisualComponent();
    }

    // --- If it's Shape3D

    Shape3D shape3d = null;

    if (obj instanceof Shape3D) {
      shape3d = (Shape3D) obj;
    }
    else if (obj instanceof BranchGroup) {
      shape3d = getFirstShape3D( (BranchGroup) obj);
    }
    else {
      System.err.println(this.getClass().getCanonicalName() + ": getVisualComponent: don't know how to handle class " +
                         obj.getClass().getCanonicalName());
      return null;
    }

    // --- Special case

    VerticesPropertyPanel vComp = visualComponents.get(obj);

    if (vComp == null) {
      vComp = new VerticesPropertyPanel();
      visualComponents.put(obj, vComp);
    }

    try {
      //Appearance app = shape3d.getAppearance();
      vComp.setPanel(shape3d);
    }
    catch (Exception ex) {
      System.err.println(this.getClass().getCanonicalName() +
                         ": getVisualComponent(n): " + ex.getMessage());
    }

    return vComp;
  }

  private Shape3D getFirstShape3D(BranchGroup bg) {
    for (int i = 0; i < bg.numChildren(); i++) {
      if (bg.getChild(i) instanceof Shape3D) {
        return (Shape3D) bg.getChild(i);
      }
    }
    return null;
  }

  @Override
  public void setVisible(boolean visible) {
    for (int i = 0; i < Graphics.size(); i++) {
      Object obj = Graphics.get(i);

      //if (obj instanceof GraphicsObjectInterface) {
      //   GraphicsObjectInterface graphics = (GraphicsObjectInterface) obj;
      //   if ( graphics.isVisible() ) return true;
      //}
      if (obj instanceof VerticesObject) {
        VerticesObject graphics = (VerticesObject) obj;
        graphics.setVisible(visible);
      }

      else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
        VerticesPropertyPanel vComp = visualComponents.get(obj);
        if (vComp != null) {
          vComp.setObjectVisible(visible);
        }
      }
      else {
        System.err.println(
            "setVisible: don't know how to handle class " + obj.getClass().getCanonicalName());
      }
    }
  }

  public void setOpaque(boolean opaque) {
    for (int i = 0; i < Graphics.size(); i++) {
      Object obj = Graphics.get(i);

      //if (obj instanceof GraphicsObjectInterface) {
      //   GraphicsObjectInterface graphics = (GraphicsObjectInterface) obj;
      //   if ( graphics.isVisible() ) return true;
      //}
      if (obj instanceof VerticesObject) {
        VerticesObject graphics = (VerticesObject) obj;
        graphics.setOpaque(opaque);
      }

      else if (obj instanceof Shape3D || obj instanceof BranchGroup) {
        VerticesPropertyPanel vComp = visualComponents.get(obj);
        if (vComp != null) {
          vComp.setObjectOpaque(opaque);
        }
      }
      else {
        System.err.println(
            "setOpaque: don't know how to handle class " + obj.getClass().getCanonicalName());
      }
    }
  }

}
