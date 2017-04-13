package cct.modelling.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import cct.modelling.AtomicSet;
import cct.modelling.AtomicSets;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public class SetNamePanel
    extends JPanel {
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel1 = new JPanel();
  private JLabel jLabel1 = new JLabel();
  private JTextField nameTextField = new JTextField();
  private FlowLayout flowLayout1 = new FlowLayout();
  private JButton nameButton = new JButton();

  private Java3dUniverse j3d = null;

  public SetNamePanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void setJava3dUniverse(Java3dUniverse j3d) {
    this.j3d = j3d;
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setText("Set Name: ");
    jPanel1.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    nameButton.setToolTipText("");
    nameButton.setText("Set Name");
    nameButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        nameButton_actionPerformed(e);
      }
    });
    nameTextField.setToolTipText("Type Set Name and Press Enter");
    nameTextField.setColumns(20);
    nameTextField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jTextField1_actionPerformed(e);
      }
    });
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel1);
    jPanel1.add(nameTextField);
    jPanel1.add(nameButton);
  }

  public void nameButton_actionPerformed(ActionEvent e) {
    String name = nameTextField.getText().trim().toUpperCase().replaceAll("[ ]", "_");
    setSetName(name);
  }

  public void jTextField1_actionPerformed(ActionEvent e) {
    String name = nameTextField.getText().trim().toUpperCase().replaceAll("[ ]", "_");
    setSetName(name);
  }

  private void setSetName(String name) {
    if (name.length() < 1) {
      return;
    }
    if (j3d == null) {
      JOptionPane.showMessageDialog(null, "Java3dUniverse is not set!", "Internal Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    Map properties = j3d.getMoleculeInterface().getProperties();
    Object obj = properties.get(MoleculeInterface.AtomicSets);
    AtomicSets sets;
    if (obj == null) {
      sets = new AtomicSets();
    }
    else if (! (obj instanceof AtomicSets)) {
      System.err.println(this.getClass().getCanonicalName() + ": setSetName: expected AtomicSets, got " +
                         obj.getClass().getCanonicalName() + " Ignored...");
      sets = new AtomicSets();
    }
    else {
      sets = (AtomicSets) obj;
    }

    AtomicSet aset = sets.get(name);

    if (aset != null) {
      int n = JOptionPane.showConfirmDialog(this, "Atomic Set " + name + " already exists\n" +
                                            "Do you want to overwrite it? - Press Yes\n" +
                                            "To give it another name - Press No",
                                            "Name is already in use", JOptionPane.YES_NO_OPTION);
      if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
        return;
      }
    }
    else {
      aset = new AtomicSet(name);
    }

    aset.clear();
    aset.addAll(j3d.getSelectedAtoms());

    sets.put(name, aset);

    properties.put(MoleculeInterface.AtomicSets, sets);

    JOptionPane.showMessageDialog(null, "Set " + name + " of " + j3d.getSelectedAtoms().size() + " atom(s) was created", "Info",
                                  JOptionPane.INFORMATION_MESSAGE);
    nameTextField.setText("");
  }
}
