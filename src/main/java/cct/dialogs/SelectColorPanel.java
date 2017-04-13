package cct.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
public class SelectColorPanel
    extends JPanel implements ChangeListener {
  private BorderLayout borderLayout1 = new BorderLayout();
  private JColorChooser jColorChooser1 = new JColorChooser();
  private ColorChangerInterface colorChangerInterface = null;

  public SelectColorPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    this.add(jColorChooser1, BorderLayout.NORTH);
    jColorChooser1.getSelectionModel().addChangeListener(this);
  }

  public void setColorChangerInterface(ColorChangerInterface cci) {
    colorChangerInterface = cci;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    Color newColor = jColorChooser1.getColor();
    if (colorChangerInterface != null) {
      colorChangerInterface.setColor(newColor);
    }
  }

}
