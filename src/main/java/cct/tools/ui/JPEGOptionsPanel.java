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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
public class JPEGOptionsPanel
    extends JPanel {

   static String qualityKey = "JPEG_QUALITY";
   static String optimizeHuffmanKey = "OPTIMIZE_HUFFMAN_CODE";
   static String progressiveKey = "PROGRESSIVE_ENCODING";

   final static int defaultJPEGQuality = 80;
   boolean defaultOptimizeHuffmanCode = false;
   boolean progressiveEncoding = false;

   Preferences prefs;

   BorderLayout borderLayout1 = new BorderLayout();
   JPanel imageQualityPanel = new JPanel();
   JSlider qualitySlider = new JSlider();
   JSpinner qualitySpinner = new JSpinner();
   JLabel jLabel1 = new JLabel();
   JLabel jLabel2 = new JLabel();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   JPanel jPanel1 = new JPanel();
   JPanel jPanel2 = new JPanel();
   JPanel jPanel3 = new JPanel();
   JCheckBox progressiveCheckBox = new JCheckBox();
   JCheckBox saveCheckBox = new JCheckBox();
   JCheckBox huffmanCheckBox = new JCheckBox();
   FlowLayout flowLayout1 = new FlowLayout();
   FlowLayout flowLayout2 = new FlowLayout();

   public JPEGOptionsPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      imageQualityPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlShadow, 1), "Image Quality"));
      imageQualityPanel.setLayout(gridBagLayout1);
      qualitySlider.setMajorTickSpacing(10);
      qualitySlider.setMinorTickSpacing(5);
      qualitySlider.setPaintTicks(true);
      qualitySlider.addChangeListener(new JPEGOptionsPanel_qualitySlider_changeAdapter(this));
      jLabel1.setToolTipText("");
      jLabel1.setText("Best Quality");
      jLabel2.setText("Best Compression");
      progressiveCheckBox.setMargin(new Insets(2, 2, 2, 20));
      progressiveCheckBox.setMnemonic('0');
      progressiveCheckBox.setText("Progressive");
      saveCheckBox.setToolTipText("");
      saveCheckBox.setText("Save these settings as the default");
      huffmanCheckBox.setToolTipText("");
      huffmanCheckBox.setText("Optimize Huffman codes");
      jPanel3.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      jPanel2.setLayout(flowLayout2);
      flowLayout2.setAlignment(FlowLayout.LEFT);
      jPanel3.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlShadow, 1), "Encoding"));
      qualitySpinner.addChangeListener(new JPEGOptionsPanel_qualitySpinner_changeAdapter(this));
      imageQualityPanel.add(jLabel2, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      imageQualityPanel.add(qualitySlider, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
      imageQualityPanel.add(jLabel1, new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0
          , GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(qualitySpinner);
      imageQualityPanel.add(jPanel1, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(saveCheckBox);
      jPanel3.add(progressiveCheckBox);
      jPanel3.add(huffmanCheckBox);
      this.add(imageQualityPanel, BorderLayout.NORTH);
      this.add(jPanel3, BorderLayout.CENTER);
      this.add(jPanel2, BorderLayout.SOUTH);

      //qualitySlider.setValue(defaultJPEGQuality);
      SpinnerNumberModel snm = new SpinnerNumberModel(85, 0, 100, 1);
      qualitySpinner.setModel(snm);
      //JSpinner.NumberEditor ne = new JSpinner.NumberEditor(qualitySpinner);

      int quality = defaultJPEGQuality;
      boolean optHuffman = defaultOptimizeHuffmanCode;
      boolean progressive_encode = progressiveEncoding;

      try {
         prefs = Preferences.userNodeForPackage(this.getClass());
         quality = prefs.getInt(qualityKey, defaultJPEGQuality);
         if (quality < 0 || quality > 100) {
            quality = defaultJPEGQuality;
         }
         optHuffman = prefs.getBoolean(optimizeHuffmanKey, defaultOptimizeHuffmanCode);
         progressive_encode = prefs.getBoolean(progressiveKey, progressiveEncoding);
      }
      catch (Exception ex) {
         System.err.println("Error retrieving JPEG Preferences: " +
                            ex.getMessage() + " Ignored...");
      }

      qualitySpinner.setValue(new Integer(quality));
      qualitySlider.setValue(quality);
      progressiveCheckBox.setSelected(progressive_encode);
      huffmanCheckBox.setSelected(optHuffman);

      Font font = qualitySpinner.getFont();
      if (font.getSize() < 12) {
         Font new_font = new Font(font.getName(), font.getStyle(), 12);
         qualitySpinner.setFont(new_font);
      }
   }


   public int getJPEGQuality() {
      return qualitySlider.getValue();
   }

   public boolean isOptimizeHuffmanCode() {
      return huffmanCheckBox.isSelected();
   }

   public boolean isProgressiveEncoding() {
      return progressiveCheckBox.isSelected();
   }

   public boolean isSaveSettingsAsDefault() {
      return saveCheckBox.isSelected();
   }

   public void saveSettingsAsDefault() {
      try {
         prefs = Preferences.userNodeForPackage(this.getClass());
         prefs.putInt(qualityKey, qualitySlider.getValue());
         prefs.putBoolean(optimizeHuffmanKey, huffmanCheckBox.isSelected());
         prefs.putBoolean(progressiveKey, progressiveCheckBox.isSelected());
      }
      catch (Exception ex) {
         System.err.println("Error saving JPEG Preferences: " +
                            ex.getMessage() + " Ignored...");
      }
   }

   public void setJPEGQuality(int quality) {
      if (quality < 0) {
         quality = 0;
      }
      else if (quality > 100) {
         quality = 100;
      }
   }

   public void qualitySpinner_stateChanged(ChangeEvent e) {
      if (!qualitySpinner.isEnabled()) {
         return;
      }
      qualitySpinner.setEnabled(false);
      //SpinnerNumberModel snm = (SpinnerNumberModel)qualitySpinner.getModel();
      Object obj = qualitySpinner.getValue();
      int number = 0;
      if (obj instanceof Integer) {
         number = ( (Integer) obj).intValue();
      }
      else {
         number = Integer.parseInt(obj.toString());
      }
      setSlider(number);

      qualitySpinner.setEnabled(true);
   }

   private void setSlider(int number) {
      qualitySlider.setEnabled(false);
      qualitySlider.setValue(number);
      qualitySlider.setEnabled(true);
   }

   public void qualitySlider_stateChanged(ChangeEvent e) {
      if (!qualitySlider.isEnabled()) {
         return;
      }
      qualitySlider.setEnabled(false);
      int value = qualitySlider.getValue();
      setSpinner(value);
      qualitySlider.setEnabled(true);
   }

   private void setSpinner(int value) {
      qualitySpinner.setEnabled(false);
      qualitySpinner.setValue(new Integer(value));
      qualitySpinner.setEnabled(true);
   }

   private class JPEGOptionsPanel_qualitySpinner_changeAdapter
       implements ChangeListener {
      private JPEGOptionsPanel adaptee;
      JPEGOptionsPanel_qualitySpinner_changeAdapter(JPEGOptionsPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void stateChanged(ChangeEvent e) {
         adaptee.qualitySpinner_stateChanged(e);
      }
   }

   private class JPEGOptionsPanel_qualitySlider_changeAdapter
       implements ChangeListener {
      private JPEGOptionsPanel adaptee;
      JPEGOptionsPanel_qualitySlider_changeAdapter(JPEGOptionsPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void stateChanged(ChangeEvent e) {
         adaptee.qualitySlider_stateChanged(e);
      }
   }

}
