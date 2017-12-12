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

package cct.grid.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cct.tools.ui.FontSelectorDialog;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class EditScriptDialog
    extends JDialog implements ActionListener {

   Preferences prefs;
   FontSelectorDialog fontSelector = null;
   static String FontNameKey = "FontName";
   static String FontSizeKey = "FontSize";
   static String FontStyleKey = "FontStyle";
   static String defaultFontName = "default";

   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel jPanel1 = new JPanel();
   JScrollPane jScrollPane1 = new JScrollPane();
   public JTextArea scriptTextArea = new JTextArea();
   JButton cancelButton = new JButton();
   JButton submitButton = new JButton();
   public boolean submitPressed = false;

   public EditScriptDialog(Frame owner, String title, boolean modal) {
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

   public EditScriptDialog() {
      this(new Frame(), "EditScriptDialog", false);
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      scriptTextArea.setText("Your Script is Here");
      cancelButton.setText("Cancel");
      submitButton.setToolTipText("");
      submitButton.setText("Submit");
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      this.setModal(true);
      this.setTitle("Edit Script");
      getContentPane().add(panel1);
      panel1.add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(submitButton);
      jPanel1.add(cancelButton);
      panel1.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(scriptTextArea);

      submitButton.addActionListener(this);
      cancelButton.addActionListener(this);

      this.addWindowListener(new WindowAdapter() {
         @Override
        public void windowClosing(WindowEvent e) {
            submitPressed = false;
            setVisible(false);
         }
      }
      );

      // --- Retrieving font properties

      Font currentFont = scriptTextArea.getFont();
      int size = currentFont.getSize();
      int style = currentFont.getStyle();
      String fontName = currentFont.getFontName();

      try {
         prefs = Preferences.userNodeForPackage(this.getClass());
         fontName = prefs.get(FontNameKey, currentFont.getFontName());
         size = prefs.getInt(FontSizeKey, 12);
         if (size < 12) {
            size = 12;
         }
         style = prefs.getInt(FontStyleKey, 0);
      }
      catch (Exception ex) {
         System.err.println("Error retrieving Font Preferences: " +
                            ex.getMessage());
         return;
      }

      Font newFont = new Font(fontName, style, size);
      scriptTextArea.setFont(newFont);

      currentFont = scriptTextArea.getFont();
      try {
         prefs.put(FontNameKey, currentFont.getFontName());
         prefs.putInt(FontSizeKey, currentFont.getSize());
         prefs.putInt(FontStyleKey, currentFont.getStyle());
         prefs.flush();
      }
      catch (Exception ex) {
         System.err.println("Error saving Font Preferences: " +
                            ex.getMessage());
      }

   }

   @Override
  public void actionPerformed(ActionEvent e) {
      if (e.getSource() == cancelButton) {
         submitPressed = false;
      }
      else if (e.getSource() == submitButton) {
         submitPressed = true;
      }
      setVisible(false);
   }
}
