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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JPanel;
import org.scijava.vecmath.Color3f;

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
public class ColorGradientPanel
    extends JPanel {

   final static Color bg = Color.white;
   final static Color fg = Color.black;
   final static Color red = Color.red;
   final static Color white = Color.white;

   FontMetrics fontMetrics;
   float fMin = 0, fMax;
  java.util.List<Color3f> Colors = null;

   final static int maxCharHeight = 25;
   final static int minFontSize = 6;

   int[] charWidth = null;

   private boolean stupidBool = false;
   protected BorderLayout borderLayout1 = new BorderLayout();
   static final Logger logger = Logger.getLogger(ColorGradientPanel.class.getCanonicalName());

   public ColorGradientPanel() {
      stupidBool = true;
      fMin = -1;
      fMax = 1;
      Colors = new ArrayList<Color3f> ();
      Colors.add(new Color3f(0, 0, 1)); // Blue
      Colors.add(new Color3f(0, 1, 0)); // Green
      Colors.add(new Color3f(1, 0, 0)); // Red

      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }

   }

   public ColorGradientPanel(float min, float max, java.util.List<Color3f> colors) {
      stupidBool = false;
      setColorGradient(min, max, colors);

      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void setColorGradient(float min, float max, java.util.List<Color3f> colors) {
      stupidBool = false;

      if (colors != null) {
         fMin = min;
         fMax = max;
         Colors = colors;
      }
      else {
         fMin = -1;
         fMax = 1;
         Colors = new ArrayList<Color3f> ();
         Colors.add(new Color3f(0, 0, 1)); // Blue
         //Colors.add(new Color3f(0, 0.5f, 1)); //
         Colors.add(new Color3f(0, 1, 0)); // Green
         //Colors.add(new Color3f(0.5f, 1, 0)); // greenyellow(SVG) rgb(173, 255, 47)
         Colors.add(new Color3f(1, 0, 0)); // Red
      }

      this.repaint();
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      this.setMinimumSize(new Dimension(100, 80));
      this.setPreferredSize(new Dimension(100, 80));
      this.setToolTipText("");
   }

   FontMetrics pickFont(Graphics2D g2, String longString, int xSpace) {
      boolean fontFits = false;
      Font font = g2.getFont();
      FontMetrics fontMetrics = g2.getFontMetrics();
      int size = font.getSize();
      String name = font.getName();
      int style = font.getStyle();

      while (!fontFits) {
         if ( (fontMetrics.getHeight() <= maxCharHeight)
             && (fontMetrics.stringWidth(longString) <= xSpace)) {
            fontFits = true;
         }
         else {
            if (size <= minFontSize) {
               fontFits = true;
            }
            else {
               g2.setFont(font = new Font(name, style, --size));
               fontMetrics = g2.getFontMetrics();
            }
         }
      }

      return fontMetrics;
   }

   @Override
  public void paint(Graphics g) {

      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      Dimension d = getSize();

      Color fg3D = Color.lightGray;
      g2.setBackground(fg3D);
      //g2.setPaint(fg3D);
      //g2.draw3DRect(0, 0, d.width - 1, d.height - 1, true);

      int numberWidth = 60;
      fontMetrics = pickFont(g2, "-100.0000", numberWidth);
      float fontHight = fontMetrics.getHeight();
      charWidth = fontMetrics.getWidths();

      float rectHeight = d.height - fontHight - 5;
      float rectWidth = (float)d.width / (float)(Colors.size() - 1);
      float x = 0;
      float y = fontHight + 5;

      for (int i = 0; i < Colors.size() - 1; i++) {
         Color3f startColor = Colors.get(i);
         Color3f endColor = Colors.get(i + 1);
         Color startC = new Color(startColor.x, startColor.y, startColor.z);
         Color endC = new Color(endColor.x, endColor.y, endColor.z);
         GradientPaint gradientColor = new GradientPaint(x, y, startC, x + rectWidth, y, endC);
         g2.setPaint(gradientColor);
         g2.fill(new Rectangle2D.Float(x, y, rectWidth, rectHeight));

         x += rectWidth;
      }

      // --- Mark extreme values
      g2.setPaint(red);

      String value = null;
      if (stupidBool) {
         g2.drawString(String.valueOf(fMin), 1, fontHight + 2);
         value = String.valueOf(fMax);
      }
      else {
         g2.drawString(String.format("%8.5f", fMin).trim(), 1, fontHight + 2);
         value = String.format("%8.5f", fMax).trim();
      }

      int stringLength = getStringLength(value);
      g2.drawString(value, d.width - stringLength - 1, fontHight + 2);

      float middle = (fMax - fMin) / 2.0f;
      value = String.format("%8.5f", middle).trim();
      stringLength = getStringLength(value);
      logger.info("middle=" + middle + " value: " + value + " fmin=" + fMin + " fmax=" + fMax);
      g2.drawString(value, d.width / 2 - stringLength / 2 - 1, fontHight + 2);

      // Draw ticks

      GeneralPath polyline;

      polyline = new GeneralPath(Path2D.WIND_EVEN_ODD, 2);
      polyline.moveTo(d.width / 2, y);
      polyline.lineTo(d.width / 2, y - 2);
      g2.draw(polyline);

   }

   int getStringLength(String string) {
      byte[] bytes = string.getBytes();
      int length = 0;
      for (int i = 0; i < bytes.length; i++) {
         length += charWidth[bytes[i]];
      }
      return length;
   }
}
