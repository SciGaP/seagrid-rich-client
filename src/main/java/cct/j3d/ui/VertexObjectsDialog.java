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

import cct.interfaces.GraphicsObjectInterface;
import org.scijava.java3d.BranchGroup;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
public class VertexObjectsDialog
    extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel buttonPanel = new JPanel();
  JList surfacesList = new JList();
  JButton cancelButton = new JButton();
  JButton okButton = new JButton();

  JTree gObjectTree = new JTree();
  JScrollPane leftScrollPane = new JScrollPane();

  Map<DefaultMutableTreeNode, Component> referenceTable = new HashMap<DefaultMutableTreeNode, Component> ();
  Map goReferenceTable = new HashMap();
  Map parentReferenceTable = new HashMap();
  protected boolean okPressed = false;

  java.util.List<GraphicsObjectInterface> graphicsObjects = null;

  public VertexObjectsDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public VertexObjectsDialog() {
    this(new Frame(), "VertexObjectsDialog", false);
  }

  private void jbInit() throws Exception {

    leftScrollPane.getViewport().remove(gObjectTree);

    panel1.setLayout(borderLayout1);
    surfacesList.setVisibleRowCount(10);
    cancelButton.setToolTipText("");
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    okButton.setToolTipText("");
    okButton.setText("   OK   ");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    //getContentPane().add(panel1);
    getContentPane().add(leftScrollPane);
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    panel1.add(surfacesList, BorderLayout.CENTER);
    panel1.add(buttonPanel, BorderLayout.SOUTH);
    this.getContentPane().add(panel1, BorderLayout.SOUTH);
  }

  /*
      public void setList(ArrayList<GraphicsObjectInterface> g_objects) {
     graphicsObjects = new ArrayList<GraphicsObjectInterface> (g_objects);
     if (graphicsObjects == null || graphicsObjects.size() < 1) {
        return;
     }
     ArrayList list = new ArrayList(graphicsObjects.size());
     for (int i = 0; i < graphicsObjects.size(); i++) {
        GraphicsObjectInterface goi = graphicsObjects.get(i);
     }
      }
   */

  public boolean isOKPressed() {
    return okPressed;
  }

  public void setTree(java.util.List<GraphicsObjectInterface> gObjects) {
    graphicsObjects = new ArrayList<GraphicsObjectInterface> (gObjects);
    createNodes(gObjects);
  }

  private void createNodes(java.util.List<GraphicsObjectInterface> gObjects) {

    DefaultMutableTreeNode top = new DefaultMutableTreeNode(
        "Graphics Objects");

    if (gObjects != null) {
      for (int i = 0; i < gObjects.size(); i++) {
        GraphicsObjectInterface graphics = gObjects.get(i);
        if (graphics.getNGraphicsElements() < 1) {
          continue;
        }

        createNodes(top, graphics);
      }
    }

    JTree jTree1 = new JTree(top);
    //jTree1.addTreeSelectionListener(this);

    leftScrollPane.getViewport().remove(gObjectTree);
    gObjectTree = jTree1;
    leftScrollPane.getViewport().add(gObjectTree);

    validate();
    pack();
  }

  private void createNodes(DefaultMutableTreeNode top,
                           GraphicsObjectInterface graphics) {

    DefaultMutableTreeNode gObject = new DefaultMutableTreeNode(graphics.getName());
    top.add(gObject);
    referenceTable.put(gObject, graphics.getVisualComponent());
    goReferenceTable.put(gObject, graphics);

    if (graphics.getNGraphicsElements() == 1) {
      return;
    }

    for (int j = 0; j < graphics.getNGraphicsElements(); j++) {
      Object obj = graphics.getGraphicsElement(j);

      if (obj instanceof GraphicsObjectInterface) {
        GraphicsObjectInterface go = (GraphicsObjectInterface) obj;
        if (go.getNGraphicsElements() < 1) {
          continue;
        }

        createNodes(gObject, go);
      }
      else if (obj instanceof BranchGroup) {
        DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(String.valueOf(j));
        gObject.add(leaf);
        referenceTable.put(leaf, graphics.getVisualComponent(j));
        goReferenceTable.put(leaf, graphics.getGraphicsElement(j));
        parentReferenceTable.put(leaf, graphics);
      }
      else {
        System.err.println(getClass().getCanonicalName() + ": createNodes: don't now how to handle class " +
                           obj.getClass().getCanonicalName());
      }
    }

  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    okPressed = false;
    setVisible(false);
  }

  public void okButton_actionPerformed(ActionEvent e) {
    okPressed = true;
    setVisible(false);
  }

  public TreePath[] getSelectionPaths() {
    return gObjectTree.getSelectionPaths();
  }

  public java.util.List getSelectedGraphicsObjects() {
    TreePath paths[] = gObjectTree.getSelectionPaths();
    if (paths == null) {
      return null;
    }

    java.util.List shape3ds = new ArrayList();

    for (int i = 0; i < paths.length; i++) {
      Object obj = paths[i].getLastPathComponent(); // Get root component
      if (obj instanceof DefaultMutableTreeNode) {
        Object object = goReferenceTable.get(obj);
        // --- Could be GraphicsObjectInterface or BranchGroup
        if (object instanceof GraphicsObjectInterface) {
          GraphicsObjectInterface graphics = (GraphicsObjectInterface) object;
          //graphics.removeAllGraphics();
          shape3ds.add(graphics);
          //ArrayList shape3d = graphics.getShape3DElements();
        }
        else if (object != null) {
          shape3ds.add(object);
          GraphicsObjectInterface parent = (GraphicsObjectInterface) parentReferenceTable.get(obj);
          shape3ds.add(parent);
          //parent.removeGraphics(object);
        }
      }
    }
    return shape3ds;
  }

}
