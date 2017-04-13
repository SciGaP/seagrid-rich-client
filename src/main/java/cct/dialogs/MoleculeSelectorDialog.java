package cct.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import cct.interfaces.MoleculeInterface;
import cct.interfaces.MoleculeRendererInterface;
import cct.j3d.Java3dUniverse;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2009 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public class MoleculeSelectorDialog
    extends JDialog {

  private Java3dUniverse java3d = null;
  private MoleculeRendererInterface parent = null;
  private Map<String, MoleculeInterface> molecules = null;
  private Map<DefaultMutableTreeNode, MoleculeInterface> referenceTable = new HashMap<DefaultMutableTreeNode, MoleculeInterface> ();
  private String topName = "Molecules";

  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JSplitPane jSplitPane1 = new JSplitPane();
  private BorderLayout borderLayout2 = new BorderLayout();
  private JButton cancelButton = new JButton();
  private JPanel jPanel1 = new JPanel();
  private JButton loadButton = new JButton();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JTree moleculeTree = new JTree();

  public MoleculeSelectorDialog(Frame owner, String title, boolean modal) {
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

  public MoleculeSelectorDialog() {
    this(new Frame(), "MoleculeSelectorDialog", false);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    this.getContentPane().setLayout(borderLayout2);
    cancelButton.setText("  Cancel  ");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    loadButton.setToolTipText("");
    loadButton.setText("Load Molecule");
    loadButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadButton_actionPerformed(e);
      }
    });
    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    moleculeTree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        moleculeTree_valueChanged(e);
      }
    });
    this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    jPanel1.add(loadButton);
    jPanel1.add(cancelButton);
    panel1.add(jPanel1, BorderLayout.SOUTH);
    panel1.add(jSplitPane1, BorderLayout.CENTER);
    this.getContentPane().add(panel1, BorderLayout.CENTER);
    jSplitPane1.add(jScrollPane1, JSplitPane.TOP);
    jScrollPane1.getViewport().add(moleculeTree);
    jScrollPane1.setMinimumSize(new Dimension(300, 150));
    moleculeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

  }

  public void setJ3DRenderer(Java3dUniverse renderer) {
    java3d = renderer;
    java3d.getCanvas3D().setMaximumSize(new Dimension(300, 200));
    jSplitPane1.add(java3d.getCanvas3D(), JSplitPane.BOTTOM);
  }

  public void setJ3DParent(MoleculeRendererInterface parent) {
    this.parent = parent;
  }

  public void setMoleculeTree(Map<String, MoleculeInterface> molecules) {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode(topName);
    createNodes(top, molecules);
    moleculeTree.removeAll();
    jScrollPane1.getViewport().remove(moleculeTree);
    moleculeTree = new JTree(top);
    moleculeTree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        moleculeTree_valueChanged(e);
      }
    });
    jScrollPane1.getViewport().add(moleculeTree);
  }

  private void createNodes(DefaultMutableTreeNode top, Map<String, MoleculeInterface> molecules) {

    DefaultMutableTreeNode lastDic = top;

    Set set = molecules.entrySet();
    Iterator iter = set.iterator();

    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String dicName = me.getKey().toString();
      Object obj = me.getValue();

      if (obj instanceof Map) {
        DefaultMutableTreeNode dictionary = new DefaultMutableTreeNode(dicName);
        top.add(dictionary);
        createNodes(dictionary, (Map) obj);
        lastDic = dictionary;
      }
      else if (obj instanceof MoleculeInterface) {
        DefaultMutableTreeNode fragment = new DefaultMutableTreeNode(dicName);
        lastDic.add(fragment);
        MoleculeInterface mol = (MoleculeInterface) obj;
        referenceTable.put(fragment, mol);
      }
    }

  }

  public void moleculeTree_valueChanged(TreeSelectionEvent e) {
    if (!moleculeTree.isEnabled()) {
      return;
    }
    moleculeTree.setEnabled(false);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) moleculeTree.getLastSelectedPathComponent();
    if (node == null) {
      moleculeTree.setEnabled(true);
      return;
    }

    Object nodeInfo = node.getUserObject();

    if (node.isLeaf()) {
      MoleculeInterface mol = referenceTable.get(node);
      if (java3d != null) {
        java3d.addMolecule(mol);
      }
    }

    moleculeTree.setEnabled(true);
  }

  public void loadButton_actionPerformed(ActionEvent e) {
    if (java3d == null || parent == null) {
      return;
    }
    MoleculeInterface mol = java3d.getMoleculeInterface();
    if (mol == null || mol.getNumberOfAtoms() < 1) {
      return;
    }
    parent.addMolecule(mol);
    {
    }
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }
}
