package cct.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import cct.interfaces.ColorChangerInterface;

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
public class SelectColorDialog
    extends JDialog implements ColorChangerInterface {
  private JPanel panel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel1 = new JPanel();
  private SelectColorPanel selectColorPanel1 = new SelectColorPanel();
  private JButton hideButton = new JButton();
  private JButton resetButton = new JButton();
  private ColorChangerInterface colorChangerInterface = null;
  private JCheckBox previewCheckBox = new JCheckBox();
  private Color finalColor = null;

  public SelectColorDialog(Frame owner, String title, boolean modal) {
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

  public SelectColorDialog() {
    this(new Frame(), "SelecColorDialog", false);
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    hideButton.setToolTipText("");
    hideButton.setText(" Hide ");
    hideButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        hideButton_actionPerformed(e);
      }
    });
    resetButton.setToolTipText("");
    resetButton.setText("Reset");
    resetButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    previewCheckBox.setToolTipText("");
    previewCheckBox.setSelected(true);
    previewCheckBox.setText("Preview");
    previewCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        previewCheckBox_itemStateChanged(e);
      }
    });
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(resetButton);
    jPanel1.add(hideButton);
    panel1.add(selectColorPanel1, BorderLayout.NORTH);
    panel1.add(previewCheckBox, BorderLayout.CENTER);

    selectColorPanel1.setColorChangerInterface(this);
  }

  public void setColorChangerInterface(ColorChangerInterface cci) {
    colorChangerInterface = cci;
  }

  @Override
  public void setColor(Color newColor) {
    finalColor = newColor;
    if (colorChangerInterface != null && previewCheckBox.isSelected()) {
      colorChangerInterface.setColor(finalColor);
    }
  }

  @Override
  public void reset() {
    if (colorChangerInterface != null) {
      colorChangerInterface.reset();
    }
  }

  @Override
  public void stop() {
    if (colorChangerInterface != null) {
      colorChangerInterface.stop();
    }
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    reset();
  }

  public void hideButton_actionPerformed(ActionEvent e) {
    if (colorChangerInterface != null && !previewCheckBox.isSelected()) {
      colorChangerInterface.setColor(finalColor);
    }
    stop();
    this.setVisible(false);
  }

  public void previewCheckBox_itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      if (colorChangerInterface != null && finalColor != null) {
        colorChangerInterface.setColor(finalColor);
      }
    }
  }

}
