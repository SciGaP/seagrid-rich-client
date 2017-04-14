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

package cct.dialogs;

import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import cct.tools.FragmentDictionaryParser;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;

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
public class JAddFragmentDialog
    extends JFrame {

  private static ImageIcon saveFile = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/saveFile.png"));
  private static ImageIcon addNewFragment = new ImageIcon(cct.resources.Resources.class.getResource(
          "/cct/images/icons16x16/add-fragment.png"));
  public static final String CUSTOM_DICTIONARY_NAME = "Custom";

  Java3dUniverse java3d = null;
  Java3dUniverse parent = null;
  JScrollPane jScrollPane1 = new JScrollPane();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton finishButton = new JButton();
  JButton addFragButton = new JButton();
  JButton helpButton = new JButton();
  JTree fragmentsTree = null;
  Map referenceTable = new HashMap();
  Map nameReferenceTable = new HashMap();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  private JButton saveDicButton = new JButton();
  private FragmentDictionaryParser fdp = null;
  private Map fragmentTree;
  private JButton newFragButton = new JButton();
  private FlowLayout flowLayout1 = new FlowLayout();
  private AddNewFragmentDialog addNewFragmentDialog;
  private DefaultMutableTreeNode rootNode;
  private DefaultTreeModel treeModel;
  private boolean inDevelopment = true;

  public JAddFragmentDialog(String title, Map fragTree, Java3dUniverse j3d) {
    super(title);
    java3d = j3d;
    fragmentTree = fragTree;

    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit(fragmentTree);
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void setFragmentDictionaryParser(FragmentDictionaryParser parser) {
    fdp = parser;
  }

  private void jbInit(Map fragTree) throws Exception {

    rootNode = new DefaultMutableTreeNode("Fragments");
    treeModel = new DefaultTreeModel(rootNode);

    createNodes(rootNode, fragTree);
    JTree jTree1 = new JTree(treeModel);
    jTree1.setEditable(true);
    jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    jTree1.setShowsRootHandles(true);
    fragmentsTree = jTree1;

    jScrollPane1.setMinimumSize(new Dimension(300, 150));
    saveDicButton.setMaximumSize(new Dimension(24, 24));
    saveDicButton.setMinimumSize(new Dimension(24, 24));
    saveDicButton.setPreferredSize(new Dimension(24, 24));
    saveDicButton.setToolTipText("Save Dictionary");

    saveDicButton.setIcon(saveFile);
    JScrollPane jScrollPane2 = new JScrollPane();
    jScrollPane2.setMinimumSize(new Dimension(300, 200));
    jPanel1.setLayout(borderLayout2);
    //saveDicButton.setText("Save Dictionary");
    saveDicButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveDicButton_actionPerformed(e);
      }
    });
    newFragButton.setMaximumSize(new Dimension(24, 24));
    newFragButton.setMinimumSize(new Dimension(24, 24));
    newFragButton.setPreferredSize(new Dimension(24, 24));
    newFragButton.setToolTipText("Add new Fragment");
    newFragButton.setIcon(addNewFragment);
    newFragButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        newFragButton_actionPerformed(e);
      }
    });
    jPanel3.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    jScrollPane2.getViewport().add(java3d.getCanvas3D());

    JSplitPane jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, //jScrollPane1, java3d.getCanvas3D());
                                            jScrollPane1, jScrollPane2);

    //jScrollPane1.
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent we) {
        //setLabel("Thwarted user attempt to close window.");
        finishDialog();
      }
    });

    this.getContentPane().setLayout(borderLayout1);
    finishButton.setText("Finish");
    finishButton.addActionListener(new JAddFragmentDialog_jButton1_actionAdapter(this));
    addFragButton.setText("Add Fragment");
    addFragButton.addActionListener(new JAddFragmentDialog_addFragButton_actionAdapter(this));
    helpButton.setEnabled(false);
    helpButton.setToolTipText("");
    helpButton.setText("Help");
    jTree1.addTreeSelectionListener(new
                                    JAddFragmentDialog_jTree1_treeSelectionAdapter(this));
    this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel3.add(saveDicButton);
    jPanel3.add(newFragButton);
    jPanel2.add(addFragButton);
    jPanel2.add(finishButton);
    jPanel2.add(helpButton);
    jScrollPane1.getViewport().add(jTree1);
    jPanel1.add(jPanel3, BorderLayout.NORTH);
    jPanel1.add(jPanel2, BorderLayout.SOUTH);
    addFragButton.setVisible(false);

    if (inDevelopment) {
      saveDicButton.setVisible(false);
      newFragButton.setVisible(false);
    }
  }

  public JButton getHelpButton() {
    return this.helpButton;
  }

  private void createNodes(DefaultMutableTreeNode top, Map fragTree) {

    DefaultMutableTreeNode lastDic = top;

    Set set = fragTree.entrySet();
    Iterator iter = set.iterator();

    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String dicName = me.getKey().toString();
      Object obj = me.getValue();

      if (obj instanceof Map) {
        DefaultMutableTreeNode dictionary = new DefaultMutableTreeNode(dicName);
        top.add(dictionary);
        nameReferenceTable.put(dicName, dictionary);
        //LinkedHashMap dic = (LinkedHashMap)obj;
        createNodes(dictionary, (Map) obj);
        lastDic = dictionary;
      }
      else if (obj instanceof String) {
        DefaultMutableTreeNode fragment = new DefaultMutableTreeNode(dicName);
        lastDic.add(fragment);
        String spec = (String) obj;
        referenceTable.put(fragment, spec);
      }
    }
  }

  public static void main(String[] args) {
    //JAddFragmentDialog jaddfragmentdialog = new JAddFragmentDialog();
  }

  public void setParent(Java3dUniverse j3d) {
    parent = j3d;
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    finishDialog();
  }

  private void finishDialog() {
    parent.endProcessingSelectedAtoms();
    setVisible(false);
  }

  public void jTree1_valueChanged(TreeSelectionEvent e) {
    if (!fragmentsTree.isEnabled()) {
      return;
    }
    fragmentsTree.setEnabled(false);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) fragmentsTree.getLastSelectedPathComponent();
    if (node == null) {
      fragmentsTree.setEnabled(true);
      return;
    }

    Object nodeInfo = node.getUserObject();

    if (node.isLeaf()) {
      String spec = (String) referenceTable.get(node);
      //java3d.loadFragment((String)nodeInfo );
      try {
        MoleculeInterface m = fdp.loadFragment(spec);
        java3d.loadFragment(m);
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error adding a fragment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }

    fragmentsTree.setEnabled(true);
  }

  public void addFragButton_actionPerformed(ActionEvent e) {
    try {
      parent.addFragment(java3d);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error adding a fragment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

      System.err.println(getClass().getCanonicalName() + " : " + ex.getMessage());
      //ex.printStackTrace();
      return;
    }
  }

  public void saveDicButton_actionPerformed(ActionEvent e) {
    final JFileChooser fc = new JFileChooser();
    String curDir = System.getProperty("user.dir");
    File cwd = new File(curDir);
    if (cwd.isDirectory()) {
      try {
        fc.setCurrentDirectory(cwd);
      }
      catch (Exception ex) {
        System.err.println("Problem listing cwd: " + cwd + " : " + ex.getMessage());
      }
    }
    int returnVal = fc.showSaveDialog(this);
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File file = fc.getSelectedFile();
    try {
      fdp.saveFragmentDictionary(fragmentTree, file.getAbsolutePath());
    }
    catch (Exception ex) {
      System.err.println("Problem saving dictionary: " + file.getAbsoluteFile() + " : " + ex.getMessage());
    }

  }

  public void newFragButton_actionPerformed(ActionEvent e) {
    if (parent == null) {
      JOptionPane.showMessageDialog(this, "Parent is not set", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    MoleculeInterface molecule = parent.getMoleculeInterface();
    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      JOptionPane.showMessageDialog(this, "No molecule in main window", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    TreePath treePath = fragmentsTree.getSelectionPath();

    String dicPath = "";
    String fragName = "Magic Frag";
    if (treePath != null && treePath.getPathCount() > 1) {
      Object[] path = treePath.getPath();

      for (int i = 1; i < treePath.getPathCount(); i++) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path[i];
        if (!node.isLeaf()) {
          dicPath += node.getUserObject().toString() + "/";
        }
        else {
          fragName = node.getUserObject().toString();
        }
      }
    }
    else {

    }

    if (addNewFragmentDialog == null) {
      addNewFragmentDialog = new AddNewFragmentDialog(null, "Fragment Name", true);
      addNewFragmentDialog.setLocationRelativeTo(this);
      addNewFragmentDialog.setAlwaysOnTop(true);
    }

    addNewFragmentDialog.setFragmentName(fragName);
    String[] paths = getOrderedPaths(fragmentsTree);
    addNewFragmentDialog.setExistingPaths(paths);
    addNewFragmentDialog.setPath(dicPath);

    int cond = addNewFragmentDialog.showDialog();
    if (cond == AddNewFragmentDialog.CANCELED) {
      return;
    }

    dicPath = addNewFragmentDialog.getFragmentName();
    if (dicPath == null || dicPath.length() < 1) {
      dicPath = CUSTOM_DICTIONARY_NAME;
    }
    fragName = addNewFragmentDialog.getPath();

    DefaultMutableTreeNode dictionary = null;
    if (nameReferenceTable.containsKey(dicPath)) {
      dictionary = (DefaultMutableTreeNode) nameReferenceTable.get(dicPath);
    }
    else {
      dictionary = new DefaultMutableTreeNode(dicPath);
      rootNode.add(dictionary);
    }

    DefaultMutableTreeNode fragment = new DefaultMutableTreeNode(fragName);
    treeModel.insertNodeInto(fragment, dictionary, dictionary.getChildCount());
    //dictionary.add(fragment);
    referenceTable.put(fragment, molecule);
    fragmentsTree.scrollPathToVisible(new TreePath(fragment.getPath()));
  }

  class JAddFragmentDialog_jTree1_treeSelectionAdapter
      implements TreeSelectionListener {
    private JAddFragmentDialog adaptee;
    JAddFragmentDialog_jTree1_treeSelectionAdapter(JAddFragmentDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
      adaptee.jTree1_valueChanged(e);
    }
  }

  class JAddFragmentDialog_addFragButton_actionAdapter
      implements ActionListener {
    private JAddFragmentDialog adaptee;
    JAddFragmentDialog_addFragButton_actionAdapter(JAddFragmentDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.addFragButton_actionPerformed(e);
    }
  }

  class JAddFragmentDialog_jButton1_actionAdapter
      implements ActionListener {
    private JAddFragmentDialog adaptee;
    JAddFragmentDialog_jButton1_actionAdapter(JAddFragmentDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }

  private String[] getOrderedPaths(JTree jtree) {
    Set<String> upaths = getAllPaths(jtree);
    if (upaths.size() < 1) {
      return null;
    }
    String[] paths = new String[upaths.size()];
    upaths.toArray(paths);
    Arrays.sort(paths);
    return paths;
  }

  private Set<String> getAllPaths(JTree jtree) {
    Set<String> paths = new HashSet<String> ();
    TreeNode treeNode = (TreeNode) jtree.getModel().getRoot();
    for (Enumeration e = treeNode.children(); e.hasMoreElements(); ) {
      TreeNode n = (TreeNode) e.nextElement();
      if (n.isLeaf()) {
        continue;
      }
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) n;
      paths.add(node.getUserObject().toString());
    }
    return paths;
  }

}
