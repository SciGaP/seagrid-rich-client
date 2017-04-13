package cct.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cct.interfaces.MoleculeSelectorInterface;
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
public class JMoleculeSelectorDialog
    extends JDialog {
  public static final int OK = 0, CANCEL = 1;
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel graphicsPanel = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JButton helpButton = new JButton();
  private JButton cancelButton = new JButton();
  private JButton okButton = new JButton();
  private BorderLayout borderLayout2 = new BorderLayout();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JList molDescrList = new JList();

  private MoleculeSelectorInterface moleculeSelector;
  private Java3dUniverse java3d = null;
  private int outcome = CANCEL;

  public JMoleculeSelectorDialog(Frame owner, String title, boolean modal) {
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

  public JMoleculeSelectorDialog() {
    this(new Frame(), "MoleculeSelectorDialog", false);
  }

  public void set3DRenderer(Java3dUniverse j3d) {
    if (java3d != null) {
      graphicsPanel.remove(java3d.getCanvas3D());
    }
    java3d = j3d;
    java3d.getCanvas3D().setMaximumSize(new Dimension(300, 200));
    java3d.getCanvas3D().setPreferredSize(new Dimension(300, 200));
    graphicsPanel.add(java3d.getCanvas3D(), BorderLayout.CENTER);
    this.validate();
    this.pack();
  }

  public void setMoleculeSelector(MoleculeSelectorInterface mSelector) {
    moleculeSelector = mSelector;
    int n = moleculeSelector.countMolecules();
    if (n < 1) {
      System.err.println(this.getClass().getCanonicalName() + " : setMoleculeSelector: MoleculeSelector has no molecules...");
      return;
    }

    molDescrList.removeAll();
    Object[] list = new Object[n];
    for (int i = 0; i < n; i++) {
      try {
        list[i] = moleculeSelector.getMoleculeDescription(i);
      }
      catch (Exception ex) {}
    }
    molDescrList.setListData(list);
    molDescrList.setSelectedIndex(0);
    update3DStructure();
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    helpButton.setToolTipText("");
    helpButton.setText(" Help ");
    cancelButton.setToolTipText("");
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    okButton.setToolTipText("");
    okButton.setText("  OK  ");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    graphicsPanel.setLayout(borderLayout2);
    jScrollPane1.setMaximumSize(new Dimension(32767, 100));
    jScrollPane1.setMinimumSize(new Dimension(0, 100));
    jScrollPane1.setPreferredSize(new Dimension(0, 100));
    molDescrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    molDescrList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        molDescrList_valueChanged(e);
      }
    });
    graphicsPanel.setMinimumSize(new Dimension(200, 100));
    getContentPane().add(panel1);
    panel1.add(graphicsPanel, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(molDescrList);
    panel1.add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(okButton);
    jPanel2.add(cancelButton);
    jPanel2.add(helpButton);
    graphicsPanel.add(jScrollPane1, BorderLayout.SOUTH);
  }

  public void molDescrList_valueChanged(ListSelectionEvent e) {
    update3DStructure();
  }

  private void update3DStructure() {
    int index = molDescrList.getSelectedIndex();
    if (index == -1) {
      return;
    }
    try {
      java3d.addMolecule(moleculeSelector.getMolecule(index));
    }
    catch (Exception ex) {}
  }

  public int showDialog() {
    if (graphicsPanel.getSize().getHeight() < 200) {
      graphicsPanel.setSize(graphicsPanel.getSize().width, 200);
    }
    this.pack();
    setVisible(true);
    return outcome;
  }

  public void okButton_actionPerformed(ActionEvent e) {
    outcome = OK;
    setVisible(false);
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    outcome = CANCEL;
    setVisible(false);
  }

  public int getSelectedIndex() {
    return molDescrList.getSelectedIndex();
  }
}
