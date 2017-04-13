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
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cct.interfaces.CellViewManagerInterface;
import cct.modelling.CELL_PARAMETER;

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
public class ViewPeriodicImagePanel
    extends JPanel implements ChangeListener {

  static final Map<JSpinner, CELL_PARAMETER> typeTable = new HashMap<JSpinner, CELL_PARAMETER> ();
  static final Map<JSpinner, Integer> directionTable = new HashMap<JSpinner, Integer> ();

  private int minusMin = 0;
  private int plusMin = 1;
  private int max = 100;
  private int step = 1;
  private int initMinusValue = 0;
  private int initPlusValue = 1;
  private SpinnerModel minusModel_1 = new SpinnerNumberModel(initMinusValue, minusMin, max, step);
  private SpinnerModel minusModel_2 = new SpinnerNumberModel(initMinusValue, minusMin, max, step);
  private SpinnerModel minusModel_3 = new SpinnerNumberModel(initMinusValue, minusMin, max, step);
  private SpinnerModel plusModel_1 = new SpinnerNumberModel(initPlusValue, plusMin, max, step);
  private SpinnerModel plusModel_2 = new SpinnerNumberModel(initPlusValue, plusMin, max, step);
  private SpinnerModel plusModel_3 = new SpinnerNumberModel(initPlusValue, plusMin, max, step);

  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel cellReplicationPanel = new JPanel();
  private JPanel buttonPanel = new JPanel();
  private JPanel abcPanel = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private JLabel jLabel1 = new JLabel();
  private JSpinner cSpinner = new JSpinner();
  private JLabel jLabel2 = new JLabel();
  private JLabel jLabel3 = new JLabel();
  private JLabel jLabel4 = new JLabel();
  private JLabel jLabel5 = new JLabel();
  private JSpinner cMinusSpinner = new JSpinner();
  private JSpinner bSpinner = new JSpinner();
  private JSpinner bMinusSpinner = new JSpinner();
  private JSpinner aSpinner = new JSpinner();
  private JSpinner aMinusSpinner = new JSpinner();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JButton resetButton = new JButton();
  private JButton makeSupercellButton = new JButton();

  private CellViewManagerInterface cellViewManager = null;

  public ViewPeriodicImagePanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }

    if (typeTable.size() < 1) {
      typeTable.put(aSpinner, CELL_PARAMETER.A);
      directionTable.put(aSpinner, new Integer(1));

      typeTable.put(aMinusSpinner, CELL_PARAMETER.A);
      directionTable.put(aMinusSpinner, new Integer( -1));

      typeTable.put(bSpinner, CELL_PARAMETER.B);
      directionTable.put(bSpinner, new Integer(1));

      typeTable.put(bMinusSpinner, CELL_PARAMETER.B);
      directionTable.put(bMinusSpinner, new Integer( -1));

      typeTable.put(cSpinner, CELL_PARAMETER.C);
      directionTable.put(cSpinner, new Integer(1));

      typeTable.put(cMinusSpinner, CELL_PARAMETER.C);
      directionTable.put(cMinusSpinner, new Integer( -1));

    }
  }

  public void setCellViewManager(CellViewManagerInterface manager) {
    cellViewManager = manager;
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    cellReplicationPanel.setLayout(borderLayout2);
    jLabel1.setToolTipText("");
    jLabel1.setText("c");
    jLabel2.setToolTipText("");
    jLabel2.setText("b");
    jLabel3.setToolTipText("");
    jLabel3.setText("a");
    jLabel4.setToolTipText("");
    jLabel4.setText("+");
    jLabel5.setToolTipText("");
    jLabel5.setText("-");
    abcPanel.setLayout(gridBagLayout1);
    resetButton.setText("Reset");
    resetButton.addActionListener(new ViewPeriodicImagePanel_resetButton_actionAdapter(this));
    makeSupercellButton.setToolTipText("");
    makeSupercellButton.setText("Make Supercell");
    aMinusSpinner.setModel(minusModel_1);
    bMinusSpinner.setModel(minusModel_2);
    cMinusSpinner.setModel(minusModel_3);
    aSpinner.setModel(plusModel_1);

    aSpinner.addChangeListener(this);
    aMinusSpinner.addChangeListener(this);
    bSpinner.addChangeListener(this);
    bMinusSpinner.addChangeListener(this);
    cSpinner.addChangeListener(this);
    cMinusSpinner.addChangeListener(this);

    bSpinner.setModel(plusModel_2);
    cSpinner.setModel(plusModel_3);
    this.add(cellReplicationPanel, BorderLayout.CENTER);
    cellReplicationPanel.add(buttonPanel, BorderLayout.SOUTH);
    buttonPanel.add(resetButton);
    buttonPanel.add(makeSupercellButton);
    cellReplicationPanel.add(abcPanel, BorderLayout.CENTER);
    abcPanel.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    abcPanel.add(jLabel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    abcPanel.add(jLabel4, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    abcPanel.add(jLabel5, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    abcPanel.add(aMinusSpinner, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
    abcPanel.add(aSpinner, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0
                                                  , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2),
                                                  0, 0));
    abcPanel.add(bMinusSpinner, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
    abcPanel.add(bSpinner, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0
                                                  , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2),
                                                  0, 0));
    abcPanel.add(cSpinner, new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0
                                                  , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2),
                                                  0, 0));
    abcPanel.add(cMinusSpinner, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
        , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
    abcPanel.add(jLabel1, new GridBagConstraints(0, 3, GridBagConstraints.REMAINDER, 1, 0.0, 0.0
                                                 , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0,
                                                 0));

    Font font = aSpinner.getEditor().getFont();
    if (font.getSize() < 15) {
      Font new_font = new Font(font.getName(), font.getStyle(), 15);
      aSpinner.getEditor().setFont(new_font);
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
          exception.printStackTrace();
        }

        JDialog diag = new JDialog(new Frame(), "Test dialog", true);
        diag.add(new ViewPeriodicImagePanel());
        diag.pack();
        diag.setVisible(true);
        System.exit(0);
      }
    });
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    if (cellViewManager == null) {
      return;
    }
    CELL_PARAMETER param = CELL_PARAMETER.A;
    JSpinner spinner = (JSpinner) e.getSource();
    if (!spinner.isEnabled()) {
      return;
    }

    param = typeTable.get(spinner);
    int direction = directionTable.get(spinner);

    SpinnerModel dateModel = spinner.getModel();

    if (dateModel instanceof SpinnerNumberModel) {
      int value = ( (SpinnerNumberModel) spinner.getModel()).getNumber().intValue();
      try {
        cellViewManager.replicateCell(param, value * direction);
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }

  }

  private void enableSpinners(boolean enable) {
    Set<JSpinner> spinners = typeTable.keySet();
    Iterator<JSpinner> iter = spinners.iterator();
    while (iter.hasNext()) {
      iter.next().setEnabled(enable);
    }
  }

  private void resetSpinners() {
    Set<JSpinner> spinners = directionTable.keySet();
    Iterator<JSpinner> iter = spinners.iterator();
    while (iter.hasNext()) {
      JSpinner spinner = iter.next();
      if (directionTable.get(spinner) == -1) {
        spinner.getModel().setValue(0);
      }
      else {
        spinner.getModel().setValue(1);
      }
    }

  }

  public void resetButton_actionPerformed(ActionEvent e) {
    resetSpinners();
  }
}

class ViewPeriodicImagePanel_resetButton_actionAdapter
    implements ActionListener {
  private ViewPeriodicImagePanel adaptee;
  ViewPeriodicImagePanel_resetButton_actionAdapter(ViewPeriodicImagePanel adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.resetButton_actionPerformed(e);
  }
}
