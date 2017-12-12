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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import cct.interfaces.TreeSelectorInterface;

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
public class JSelectTreeDialog
    extends JFrame {

  TreeSelectorInterface parent = null;
  JScrollPane jScrollPane1 = new JScrollPane();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton finishButton = new JButton();
  JButton addFragButton = new JButton();
  JButton helpButton = new JButton();
  JTree fragmentsTree = null;
  Map referenceTable = new HashMap();
  String rootNodeName = "";

  public JSelectTreeDialog(String title, String rootnodeName, Map fragTree, TreeSelectorInterface p) {
    super(title);
    parent = p;
    rootNodeName = rootnodeName;

    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit(fragTree);
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }

  }

  private void jbInit(Map fragTree) throws Exception {

    DefaultMutableTreeNode top =
        new DefaultMutableTreeNode(rootNodeName);
    createNodes(top, fragTree);
    JTree jTree1 = new JTree(top);
    //jTree1.setSelectionModel(TreeSelectionModel.SINGLE_TREE_SELECTION);
    fragmentsTree = jTree1;

    jScrollPane1.setMinimumSize(new Dimension(300, 150));

    //jScrollPane1.
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.getContentPane().setLayout(borderLayout1);
    finishButton.setToolTipText("");
    finishButton.setText("Cancel");
    finishButton.addActionListener(new
                                   JSelectTreeDialog_finishButton_actionAdapter(this));
    addFragButton.setText("Ok");
    addFragButton.addActionListener(new
                                    JSelectTreeDialog_addFragButton_actionAdapter(this));
    helpButton.setEnabled(false);
    helpButton.setToolTipText("");
    helpButton.setText("Help");
    jTree1.addTreeSelectionListener(new
                                    JSelectTreeDialog_jTree1_treeSelectionAdapter(this));
    this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(addFragButton);
    jPanel1.add(finishButton);
    jPanel1.add(helpButton);
    jScrollPane1.getViewport().add(jTree1);
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
        DefaultMutableTreeNode dictionary = new DefaultMutableTreeNode(
            dicName);
        top.add(dictionary);
        //LinkedHashMap dic = (LinkedHashMap)obj;
        createNodes(dictionary, (Map) obj);
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

  public void setParent(TreeSelectorInterface p) {
    parent = p;
  }

  public void jTree1_valueChanged(TreeSelectionEvent e) {
    /*
           if (!fragmentsTree.isEnabled()) {
       return;
           }
           fragmentsTree.setEnabled(false);

           DefaultMutableTreeNode node = (DefaultMutableTreeNode) fragmentsTree.
        getLastSelectedPathComponent();
           if (node == null) {
       return;
           }

           Object nodeInfo = node.getUserObject();

           if (node.isLeaf()) {
       String spec = (String) referenceTable.get(node);
       //java3d.loadFragment((String)nodeInfo );
       parent.getSelectedBranch(spec);
           }

           fragmentsTree.setEnabled(true);
     */
  }

  public void addFragButton_actionPerformed(ActionEvent e) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) fragmentsTree.
        getLastSelectedPathComponent();
    if (node == null) {
      JOptionPane.showMessageDialog(null,
                                    "Select Item First!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    Object nodeInfo = node.getUserObject();

    if (node.isLeaf()) {
      String spec = (String) referenceTable.get(node);
      //java3d.loadFragment((String)nodeInfo );
      setVisible(false);
      parent.getSelectedBranch(spec);
    }

  }

  public void finishButton_actionPerformed(ActionEvent e) {
    setVisible(false);
    parent.cancelTreeSelection();
  }

  class JSelectTreeDialog_finishButton_actionAdapter
      implements ActionListener {
    private JSelectTreeDialog adaptee;
    JSelectTreeDialog_finishButton_actionAdapter(JSelectTreeDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.finishButton_actionPerformed(e);
    }
  }

  class JSelectTreeDialog_addFragButton_actionAdapter
      implements ActionListener {
    private JSelectTreeDialog adaptee;
    JSelectTreeDialog_addFragButton_actionAdapter(JSelectTreeDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.addFragButton_actionPerformed(e);
    }
  }

  class JSelectTreeDialog_jTree1_treeSelectionAdapter
      implements TreeSelectionListener {
    private JSelectTreeDialog adaptee;
    JSelectTreeDialog_jTree1_treeSelectionAdapter(JSelectTreeDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
      adaptee.jTree1_valueChanged(e);
    }
  }

}
