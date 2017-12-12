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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import org.scijava.java3d.BranchGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import cct.interfaces.GraphicsObjectInterface;

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
public class GraphicsObjectPropertiesFrame
    extends JFrame implements TreeSelectionListener {
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane jSplitPane1 = new JSplitPane();
  JScrollPane leftScrollPane = new JScrollPane();
  JScrollPane rightScrollPane = new JScrollPane();
  JTree gObjectTree = new JTree();
  JPanel emptyPanel = new JPanel();
  Map<DefaultMutableTreeNode, Component> referenceTable = new HashMap<DefaultMutableTreeNode, Component> ();
  Map goReferenceTable = new HashMap();
  Map parentReferenceTable = new HashMap();

  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel2 = new JPanel();

  ImageIcon deleteObject = new ImageIcon(cct.resources.Resources.class.getResource(
          "cct/images/icons16x16/selection_delete.png"));
  JButton deleteButton = new JButton();
  FlowLayout flowLayout1 = new FlowLayout();
  java.util.List<GraphicsObjectInterface> graphicsObjects = null;
  JCheckBox showSelectedOnlyCheckBox = new JCheckBox();
  JPanel buttonPanel = new JPanel();
  JButton hideButton = new JButton();
  JButton helpButton = new JButton();
  public GraphicsObjectPropertiesFrame() {
    super("Graphics Object Properties");
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    if (!gObjectTree.isEnabled()) {
      return;
    }
    gObjectTree.setEnabled(false);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) gObjectTree.
        getLastSelectedPathComponent();
    if (node == null) {
      if (showSelectedOnlyCheckBox.isSelected()) {
        setAllVisible(false);
      }
      gObjectTree.setEnabled(true);
      return;
    }

    Object nodeInfo = node.getUserObject();

    //if (node.isLeaf()) {
    Component panel = referenceTable.get(node);
    Object object = goReferenceTable.get(node);
    // --- Could be GraphicsObjectInterface or BranchGroup
    if (object instanceof GraphicsObjectInterface) {
      GraphicsObjectInterface graphics = (GraphicsObjectInterface) object;
      panel = graphics.getVisualComponent();
    }
    else if (object != null) {
      System.err.println("??? kljhlkj");
    }

    if (panel == null) {
      rightScrollPane.getViewport().removeAll();
      rightScrollPane.getViewport().add(emptyPanel);
    }
    else {
      rightScrollPane.getViewport().removeAll();
      rightScrollPane.getViewport().add(panel);
    }

    if (showSelectedOnlyCheckBox.isSelected()) {
      setAllVisible(false);
      setNodeVisible(node, true);
    }

    validate();
    pack();
    //java3d.loadFragment(spec);
    //}

    gObjectTree.setEnabled(true);
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.setToolTipText("");
    jPanel1.setLayout(borderLayout2);
    deleteButton.setMaximumSize(new Dimension(23, 23));
    deleteButton.setMinimumSize(new Dimension(23, 23));
    deleteButton.setPreferredSize(new Dimension(23, 23));
    deleteButton.setToolTipText("Delete Selected Objects");
    deleteButton.setIcon(deleteObject);
    deleteButton.setMnemonic('0');
    deleteButton.addActionListener(new GraphicsObjectPropertiesFrame_deleteButton_actionAdapter(this));
    jPanel2.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    gObjectTree.setMaximumSize(new Dimension(74, 0));
    gObjectTree.setPreferredSize(new Dimension(74, 120));
    showSelectedOnlyCheckBox.setText("Display Selected Objects Only");
    showSelectedOnlyCheckBox.addActionListener(new GraphicsObjectPropertiesFrame_showSelectedOnlyCheckBox_actionAdapter(this));
    gObjectTree.setMinimumSize(new Dimension(0, 120));
    hideButton.setToolTipText("Hide Dialog");
    hideButton.setText("Hide");
    hideButton.addActionListener(new GraphicsObjectPropertiesFrame_hideButton_actionAdapter(this));
    helpButton.setEnabled(false);
    helpButton.setToolTipText("Get Help");
    helpButton.setText("Help");
    jPanel2.add(deleteButton);
    jPanel2.add(showSelectedOnlyCheckBox);
    rightScrollPane.getViewport().add(emptyPanel);
    this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(rightScrollPane, JSplitPane.RIGHT);
    jSplitPane1.add(leftScrollPane, JSplitPane.LEFT);
    leftScrollPane.getViewport().add(jPanel1);
    jPanel1.add(gObjectTree, BorderLayout.CENTER);
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    buttonPanel.add(hideButton);
    buttonPanel.add(helpButton);
    this.getContentPane().add(jPanel2, BorderLayout.NORTH);
  }

  public void setTree(java.util.List<GraphicsObjectInterface> gObjects) {
    graphicsObjects = gObjects;
    createNodes(gObjects);
  }

  public void updateTree() {
    if (graphicsObjects == null || graphicsObjects.size() < 1) {
      return;
    }
    createNodes(graphicsObjects);
  }

  private void createNodes(DefaultMutableTreeNode top, GraphicsObjectInterface graphics) {

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

  private void createNodes(java.util.List<GraphicsObjectInterface> gObjects) {

    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Graphics Objects");

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
    jTree1.addTreeSelectionListener(this);
    //jTree1.setSelectionModel(TreeSelectionModel.SINGLE_TREE_SELECTION);

    leftScrollPane.getViewport().remove(gObjectTree);
    gObjectTree = jTree1;
    leftScrollPane.getViewport().add(gObjectTree);

    validate();
    pack();
  }

  /*
   private void createNodes(DefaultMutableTreeNode top, LinkedHashMap fragTree) {

     DefaultMutableTreeNode lastDic = top;

     Set set = fragTree.entrySet();
     Iterator iter = set.iterator();

     while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        String dicName = me.getKey().toString();
        Object obj = me.getValue();

        if (obj instanceof LinkedHashMap) {
           DefaultMutableTreeNode dictionary = new DefaultMutableTreeNode(
               dicName);
           top.add(dictionary);
           //LinkedHashMap dic = (LinkedHashMap)obj;
           createNodes(dictionary, (LinkedHashMap) obj);
           lastDic = dictionary;
        }
        else if (obj instanceof String) {
           DefaultMutableTreeNode fragment = new DefaultMutableTreeNode(
               dicName);
           lastDic.add(fragment);
           String spec = (String) obj;
           referenceTable.put(fragment, spec);
        }
     }
      }
   */

  public static void main(String[] args) {
    GraphicsObjectPropertiesFrame graphicsobjectpropertiesframe = new
        GraphicsObjectPropertiesFrame();
    graphicsobjectpropertiesframe.setVisible(true);
  }

  public void deleteButton_actionPerformed(ActionEvent e) {
    if (!deleteButton.isEnabled()) {
      return;
    }

    if (graphicsObjects == null) {
      //JOptionPane.showMessageDialog(this, "Graphics objects are not set", "Warning",
      //                              JOptionPane.WARNING_MESSAGE);
      return;
    }

    TreePath paths[] = gObjectTree.getSelectionPaths();
    if (paths == null) {
      JOptionPane.showMessageDialog(this, "Select tree nodes first", "Warning",
                                    JOptionPane.WARNING_MESSAGE);
      return;
    }

    deleteButton.setEnabled(false);

    for (int i = 0; i < paths.length; i++) {
      Object obj = paths[i].getLastPathComponent(); // Get root component
      if (obj instanceof DefaultMutableTreeNode) {
        Object object = goReferenceTable.get(obj);
        // --- Could be GraphicsObjectInterface or BranchGroup
        if (object instanceof GraphicsObjectInterface) {
          GraphicsObjectInterface graphics = (GraphicsObjectInterface) object;
          graphics.removeAllGraphics();
        }
        else if (object != null) {
          GraphicsObjectInterface parent = (GraphicsObjectInterface) parentReferenceTable.get(obj);
          parent.removeGraphics(object);
        }
      }
    }

    // --- Update array list

    int n = graphicsObjects.size();
    for (int i = n - 1; i >= 0; i--) {
      GraphicsObjectInterface graphics = graphicsObjects.get(i);
      if (graphics.getNGraphicsElements() < 1) {
        graphicsObjects.remove(i);
      }
    }

    this.gObjectTree.removeAll();
    this.setTree(graphicsObjects);

    deleteButton.setEnabled(true);
  }

  public void showSelectedOnlyCheckBox_actionPerformed(ActionEvent e) {
    if (!showSelectedOnlyCheckBox.isEnabled() || !showSelectedOnlyCheckBox.isSelected()) {
      return;
    }

    if (graphicsObjects == null) {
      return;
    }

    showSelectedOnlyCheckBox.setEnabled(false);

    TreePath paths[] = gObjectTree.getSelectionPaths();

    // --- Nothing is selected, undisplay everything

    setAllVisible(false);

    if (paths == null) {
      showSelectedOnlyCheckBox.setEnabled(true);
      return;
    }

    for (int i = 0; i < paths.length; i++) {
      Object obj = paths[i].getLastPathComponent(); // Get root component
      if (obj instanceof DefaultMutableTreeNode) {
        setNodeVisible( (DefaultMutableTreeNode) obj, true);
      }
    }

    showSelectedOnlyCheckBox.setEnabled(true);
  }

  public JButton getHelpButton() {
    return helpButton;
  }

  void setAllVisible(boolean visible) {
    for (int i = 0; i < graphicsObjects.size(); i++) {
      GraphicsObjectInterface graphics = graphicsObjects.get(i);
      graphics.setVisible(visible);
    }
  }

  void setNodeVisible(DefaultMutableTreeNode node, boolean visible) {
    Object object = goReferenceTable.get(node);
    // --- Could be GraphicsObjectInterface or BranchGroup
    if (object instanceof GraphicsObjectInterface) {
      GraphicsObjectInterface graphics = (GraphicsObjectInterface) object;
      graphics.setVisible(visible);
    }
    else if (object != null) {
      GraphicsObjectInterface parent = (GraphicsObjectInterface) parentReferenceTable.get(node);
      parent.setVisible(object, visible);
    }
  }

  public void hideButton_actionPerformed(ActionEvent e) {
    this.setVisible(false);
  }

  private class GraphicsObjectPropertiesFrame_showSelectedOnlyCheckBox_actionAdapter
      implements ActionListener {
    private GraphicsObjectPropertiesFrame adaptee;
    GraphicsObjectPropertiesFrame_showSelectedOnlyCheckBox_actionAdapter(GraphicsObjectPropertiesFrame adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.showSelectedOnlyCheckBox_actionPerformed(e);
    }
  }

  private class GraphicsObjectPropertiesFrame_deleteButton_actionAdapter
      implements ActionListener {
    private GraphicsObjectPropertiesFrame adaptee;
    GraphicsObjectPropertiesFrame_deleteButton_actionAdapter(GraphicsObjectPropertiesFrame adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.deleteButton_actionPerformed(e);
    }
  }

  private class GraphicsObjectPropertiesFrame_hideButton_actionAdapter
      implements ActionListener {
    private GraphicsObjectPropertiesFrame adaptee;
    GraphicsObjectPropertiesFrame_hideButton_actionAdapter(GraphicsObjectPropertiesFrame adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.hideButton_actionPerformed(e);
    }
  }

}
