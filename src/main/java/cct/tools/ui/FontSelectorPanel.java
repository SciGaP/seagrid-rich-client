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

package cct.tools.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
public class FontSelectorPanel
    extends JPanel implements ChangeListener, ItemListener {

   String[] styleNames = {
       "Plain", "Bold", "Italic", "Bold Italic"};

   Color currentColor = new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(),
                                  Color.BLACK.getBlue());
   BorderLayout borderLayout1 = new BorderLayout();
   public JColorChooser colorChooser = new JColorChooser(currentColor);
   JPanel jPanel1 = new JPanel();
   JPanel jPanel2 = new JPanel();
   JLabel jLabel1 = new JLabel();
   FlowLayout flowLayout2 = new FlowLayout();
   public JComboBox styleComboBox = new JComboBox(styleNames);
   JLabel jLabel2 = new JLabel();
   public JComboBox fontComboBox = new JComboBox();
   JLabel jLabel3 = new JLabel();
   public JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(12, 6, 24,
       1));

   String fontChoice = "Dialog";
   JLabel textLabel = new JLabel();
   GridLayout gridLayout1 = new GridLayout();

   public FontSelectorPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      jLabel1.setToolTipText("");
      jLabel1.setText("Size:");
      jPanel1.setLayout(gridLayout1);
      jPanel2.setLayout(flowLayout2);
      flowLayout2.setAlignment(FlowLayout.LEFT);
      jLabel2.setToolTipText("");
      jLabel2.setText("Style:");
      jLabel3.setToolTipText("");
      jLabel3.setText("Font family:");
      textLabel.setToolTipText("");
      textLabel.setHorizontalAlignment(SwingConstants.CENTER);
      textLabel.setHorizontalTextPosition(SwingConstants.CENTER);
      textLabel.setText("The quick brown fox jumped over the lazy dog");
      gridLayout1.setColumns(1);
      gridLayout1.setHgap(10);
      jPanel1.setBackground(Color.white);
      jPanel2.add(jLabel3);
      jPanel2.add(fontComboBox);
      jPanel2.add(jLabel2);
      jPanel2.add(styleComboBox);
      jPanel2.add(jLabel1);
      jPanel2.add(sizeSpinner);
      this.add(colorChooser, BorderLayout.SOUTH);
      this.add(jPanel1, BorderLayout.CENTER);
      this.add(jPanel2, BorderLayout.NORTH);
      jPanel1.add(textLabel, null);
      GraphicsEnvironment gEnv =
          GraphicsEnvironment.getLocalGraphicsEnvironment();
      String[] families = gEnv.getAvailableFontFamilyNames();
      for (int i = 0; i < families.length; i++) {
         fontComboBox.addItem(families[i]);
      }
      fontComboBox.setSelectedItem(fontChoice);
      fontComboBox.addItemListener(this);

      styleComboBox.setSelectedIndex(0);
      styleComboBox.addItemListener(this);

      sizeSpinner.addChangeListener(this);

      colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
         @Override
        public void stateChanged(ChangeEvent e) {
            updatePreviewColor();
         }
      });

      setFont();
   }

   // Get the appropriate color from our chooser and update previewLabel.
   protected void updatePreviewColor() {
      currentColor = colorChooser.getColor();
      textLabel.setForeground(currentColor);
      // Manually force the label to repaint.
      textLabel.repaint();
   }

   public int getFontStyle() {
      return styleComboBox.getSelectedIndex();
   }

   public String getFontStyleAsString() {
      return styleComboBox.getSelectedItem().toString();
   }

   public int getFontSize() {
      int sizeChoice = 12;
      try {
         String size = sizeSpinner.getModel().getValue().toString();
         sizeChoice = Integer.parseInt(size);
      }
      catch (NumberFormatException nfe) {
      }
      return sizeChoice;
   }

   public void setFont() {
      textLabel.setFont(new Font(fontComboBox.getSelectedItem().
                                 toString(),
                                 styleComboBox.getSelectedIndex(),
                                 getFontSize()));
   }

   /*
    * Detect a state change in any of the settings and create a new
    * Font with the corresponding settings. Set it on the test component.
    */
   @Override
  public void itemStateChanged(ItemEvent e) {

      if (e.getStateChange() != ItemEvent.SELECTED) {
         return;
      }
      /*
              if (e.getSource() == fonts) {
          fontChoice = (String)fonts.getSelectedItem();
              } else {
          styleChoice = styles.getSelectedIndex();
              }
       */
      //textTestPanel.setFont(new Font(fontChoice, styleChoice, sizeChoice));
      setFont();

   }

   @Override
  public void stateChanged(ChangeEvent e) {

      setFont();

      /*
       try {
           String size = sizes.getModel().getValue().toString();
           sizeChoice = Integer.parseInt(size);
           textTestPanel.setFont(new Font(fontChoice,styleChoice,sizeChoice));
       } catch (NumberFormatException nfe) {
       }
       */
   }

}
