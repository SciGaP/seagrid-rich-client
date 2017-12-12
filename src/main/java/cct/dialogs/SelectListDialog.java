package cct.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

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
public class SelectListDialog
    extends JDialog {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JList jList1 = new JList();
  private JPanel jPanel2 = new JPanel();
  private JButton cancelButton = new JButton();
  private JButton okButton = new JButton();

  private boolean ok = false;
  private JButton helpButton = new JButton();

  public SelectListDialog(Frame owner, String title) {
    super(owner, title, true);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public boolean showDialog() {
    setVisible(true);
    return ok;
  }

  public void setList(Object[] list) {
    jList1.setListData(list);
    this.pack();
  }

  /**
   * Sets selection mode for the JList. Determines whether single-item or multiple-item selections are allowed.
   * @param mode int - selection mode. Can be
   *      ListSelectionModel.SINGLE_SELECTION - Only one list index can be selected at a time.
   *                                            In this mode the setSelectionInterval and addSelectionInterval methods are equivalent,
   *                                            and only the second index argument is used.
   *      ListSelectionModel.SINGLE_INTERVAL_SELECTION - One contiguous index interval can be selected at a time. In this mode setSelectionInterval and addSelectionInterval are equivalent.
   *      ListSelectionModel.MULTIPLE_INTERVAL_SELECTION - In this mode, there's no restriction on what can be selected. This is the default.
   */
  public void setSelectionMode(int mode) {
    jList1.setSelectionMode(mode);
  }

  public SelectListDialog() {
    this(new Frame(), "SelectListDialog");
  }

  public int getSelectedIndex() {
    return jList1.getSelectedIndex();
  }

  public int[] getSelectedIndeces() {
    return jList1.getSelectedIndices();
  }

  public Object[] getSelectedValues() {
    if (!ok) {
      return null;
    }
    if (jList1.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION) {
      if (jList1.getSelectedValue() == null) {
        return null;
      }
      return new Object[] {
          jList1.getSelectedValue()};
    }
    if (jList1.getSelectedValues() == null || jList1.getSelectedValues().length < 1) {
      return null;
    }
    return jList1.getSelectedValues();
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    cancelButton.setToolTipText("Cancel Selection");
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    okButton.setToolTipText("Confirm Selection");
    okButton.setText("  OK  ");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    helpButton.setToolTipText("Get Quick Help forl Selection");
    helpButton.setActionCommand("help");
    helpButton.setText(" Help ");
    helpButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        helpButton_actionPerformed(e);
      }
    });
    getContentPane().add(panel1);
    panel1.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jList1);
    jPanel2.add(okButton);
    jPanel2.add(cancelButton);
    jPanel2.add(helpButton);
    panel1.add(jPanel2, BorderLayout.SOUTH);
  }

  public void okButton_actionPerformed(ActionEvent e) {
    ok = true;
    setVisible(false);
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    ok = false;
    setVisible(false);
  }

  public void helpButton_actionPerformed(ActionEvent e) {
    if (jList1.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION) {
      JOptionPane.showMessageDialog(this,
                                    "Only one list value can be selected at a time\n" +
                                    "OK - to confirm selection\nCancel - to cancel selection", "Help",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    else if (jList1.getSelectionMode() == ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
      JOptionPane.showMessageDialog(this,
                                    "One contiguous index interval can be selected at a time.\n" +
                                    "Use mouse in combination with Shift and Ctrl keys to select/unselect blocks of values\n" +
                                    "OK - to confirm selection\nCancel - to cancel selection", "Help",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    else if (jList1.getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
      JOptionPane.showMessageDialog(this,
                                    "There's no restriction on what can be selected.\n" +
                                    "Use mouse in combination with Shift and Ctrl keys to select/unselect blocks of values\n" +
                                    "OK - to confirm selection\nCancel - to cancel selection", "Help",
                                    JOptionPane.INFORMATION_MESSAGE);
    }

  }
}
