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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.scijava.vecmath.Color3f;

import cct.j3d.ColorRangeScheme;

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
public class ControlColorGradientPanel
    extends JPanel {

   private ColorGradientPanel colorGradientPanel1 = new ColorGradientPanel();
   protected JPanel jPanel1 = new JPanel();
   protected JPanel jPanel2 = new JPanel();
   protected JTextField maxClipTextField = new JTextField();
   protected JTextField minClipTextField = new JTextField();
   protected JLabel jLabel1 = new JLabel();
   protected GridBagLayout gridBagLayout1 = new GridBagLayout();
   protected JLabel minMaxInfoLabel = new JLabel();
   protected FlowLayout flowLayout1 = new FlowLayout();

   private boolean stupidBool = false;
   protected BorderLayout borderLayout2 = new BorderLayout();
   protected GridBagLayout gridBagLayout2 = new GridBagLayout();

   private MappedSurfaceInterface Mapper = null;
   private ColorRangeScheme ColorRange = null;

   private float clipMin, clipMax;

   public ControlColorGradientPanel() {
      stupidBool = true;
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(gridBagLayout2);
      colorGradientPanel1.setMinimumSize(new Dimension(100, 80));
      colorGradientPanel1.setOpaque(false);
      colorGradientPanel1.setPreferredSize(new Dimension(100, 80));
      colorGradientPanel1.setLayout(borderLayout2);
      maxClipTextField.setToolTipText("Type New Value and Press Enter");
      maxClipTextField.setText("1");
      maxClipTextField.setColumns(6);
      maxClipTextField.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            maxClipTextField_actionPerformed(e);
         }
      });
      minClipTextField.setToolTipText("Type New Value and Press Enter");
      minClipTextField.setText("-1");
      minClipTextField.setColumns(6);
      minClipTextField.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            minClipTextField_actionPerformed(e);
         }
      });
      jLabel1.setText("<- Enter New Clip Values ->");
      jPanel1.setLayout(gridBagLayout1);
      minMaxInfoLabel.setText("Min Vertex Value: -1, Max: 1");
      jPanel2.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      jPanel1.add(minClipTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(maxClipTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(jLabel1,
                  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                         new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(minMaxInfoLabel);
      this.add(colorGradientPanel1, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
          , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1));
      this.add(jPanel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                               , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1));
      this.add(jPanel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                               , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1));
   }

   public void setMappedSurfaceInterface(MappedSurfaceInterface mapper) {
      Mapper = mapper;
   }

   void setColorGradient(float min, float max, float clip_min, float clip_max, java.util.List<Color3f> colors) {
      stupidBool = false;
      colorGradientPanel1.setColorGradient(clip_min, clip_max, colors);

      setClipValue(minClipTextField, clip_min);

      setClipValue(maxClipTextField, clip_max);

      clipMin = clip_min;
      clipMax = clip_max;

      minMaxInfoLabel.setText("Min Vertex Value: " + String.format("%8.4f", min) + ", Max: " + String.format("%8.4f", max));
   }

   void setClipValue(JTextField textField, float new_value) {
      textField.setEnabled(false);
      textField.setText(String.format("%8.4f", new_value));
      textField.setEnabled(true);
   }

   public void setColorGradient(ColorRangeScheme crange) {

      ColorRange = crange;

      float min = crange.getMinFunValue();
      float max = crange.getMaxFunValue();
      float clip_min = crange.getClipMin();
      float clip_max = crange.getClipMax();
      java.util.List<Color3f> colors = crange.getColor3fPalette();

      setColorGradient(min, max, clip_min, clip_max, colors);
   }

   public void minClipTextField_actionPerformed(ActionEvent e) {
      if (!minClipTextField.isEnabled()) {
         return;
      }

      minClipTextField.setEnabled(false);

      float clip_min = 0;
      try {
         clip_min = Float.parseFloat(minClipTextField.getText().trim());
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this,
                                       "Cannot parse " + minClipTextField.getText().trim() + " : " + ex.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         setClipValue(minClipTextField, clipMin);
         minClipTextField.setEnabled(true);
         return;
      }

      if (clip_min >= clipMax) {
         JOptionPane.showMessageDialog(this,
                                       "Minimum clip distance cannot be larger than " + String.valueOf(clipMax) + " got " +
                                       String.valueOf(clip_min), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         setClipValue(minClipTextField, clipMin);
         minClipTextField.setEnabled(true);
         return;
      }

      clipMin = clip_min;
      ColorRange.setClipMin(clipMin);

      if (Mapper != null) {
         Mapper.setNewColorScheme(ColorRange);
      }
      else {
         System.err.println(this.getClass().getCanonicalName() + " : Surface Mapper is not set");
      }

      java.util.List<Color3f> colors = ColorRange.getColor3fPalette();
      colorGradientPanel1.setColorGradient(clipMin, clipMax, colors);

      minClipTextField.setEnabled(true);
   }

   /**
    *
    * @param e ActionEvent
    */
   public void maxClipTextField_actionPerformed(ActionEvent e) {
      if (!maxClipTextField.isEnabled()) {
         return;
      }

      maxClipTextField.setEnabled(false);

      float clip_max = 0;
      try {
         clip_max = Float.parseFloat(maxClipTextField.getText().trim());
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this,
                                       "Cannot parse " + maxClipTextField.getText().trim() + " : " + ex.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         setClipValue(maxClipTextField, clipMax);
         maxClipTextField.setEnabled(true);
         return;
      }

      if (clip_max <= clipMin) {
         JOptionPane.showMessageDialog(this,
                                       "Maximum clip value cannot be smaller than " + String.valueOf(clipMin) + " got " +
                                       String.valueOf(clip_max), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         setClipValue(maxClipTextField, clipMax);
         maxClipTextField.setEnabled(true);
         return;
      }

      clipMax = clip_max;
      ColorRange.setClipMax(clipMax);

      if (Mapper != null) {
         Mapper.setNewColorScheme(ColorRange);
      }
      else {
         System.err.println(this.getClass().getCanonicalName() + " : Surface Mapper is not set");
      }

      java.util.List<Color3f> colors = ColorRange.getColor3fPalette();
      colorGradientPanel1.setColorGradient(clipMin, clipMax, colors);

      maxClipTextField.setEnabled(true);

   }

}
