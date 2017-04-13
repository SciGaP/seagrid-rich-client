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
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import cct.tools.IOUtils;

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
public class JShowText
    extends JFrame implements ActionListener {

   public static final int QUIT_WITHOUT_SAVING = 0;
   public static final int QUIT_WITH_SAVING = 1;

   private Preferences prefs;
   private FontSelectorDialog fontSelector = null;
   private static String FontNameKey = "FontName";
   private static String FontSizeKey = "FontSize";
   private static String FontStyleKey = "FontStyle";
   private static String defaultFontName = "default";

   private JPanel panel1 = new JPanel();
   private BorderLayout borderLayout1 = new BorderLayout();
   private JScrollPane jScrollPane1 = new JScrollPane();
   private JTextArea jTextArea1 = new JTextArea();
   private JPanel jPanel1 = new JPanel();
   private JButton actionButton = new JButton();
   private JMenuBar jMenuBar1 = new JMenuBar();
   private JMenu viewMenu = new JMenu();
   private JMenuItem increaseFontMenuItem = new JMenuItem();
   private JMenuItem decreaseFontMenuItem = new JMenuItem();
   private JMenuItem selectFontMenuItem = new JMenuItem();
   private JMenu fileMenu = new JMenu();
   private JMenuItem saveAsMenuItem = new JMenuItem();

   private String defaultFileName = "jmoleditor-sysinfo.txt";
   private int buttonAction = QUIT_WITHOUT_SAVING;

   public JShowText(String title) {
      //super(owner, title, modal);
      super(title);
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public JShowText() {
      this("JShowText");
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      jTextArea1.setText("jTextArea1");
      actionButton.setToolTipText("");
      actionButton.setText("Quit");
      actionButton.addActionListener(new JShowText_jButton1_actionAdapter(this));
      viewMenu.setToolTipText("");
      viewMenu.setText("View");
      increaseFontMenuItem.setToolTipText("");
      increaseFontMenuItem.setText("Increase Font Size");
      increaseFontMenuItem.addActionListener(this);
      decreaseFontMenuItem.addActionListener(this);
      selectFontMenuItem.addActionListener(this);
      decreaseFontMenuItem.setText("Decrease Font Size");
      selectFontMenuItem.setText("Select Font");
      fileMenu.setText("File");
      saveAsMenuItem.setText("Save As ...");
      saveAsMenuItem.addActionListener(new JShowText_saveAsMenuItem_actionAdapter(this));
      this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      getContentPane().add(panel1);
      panel1.add(jScrollPane1, BorderLayout.CENTER);
      this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(actionButton);
      jScrollPane1.getViewport().add(jTextArea1);
      jMenuBar1.add(fileMenu);
      jMenuBar1.add(viewMenu);
      fileMenu.add(saveAsMenuItem);
      viewMenu.add(increaseFontMenuItem);
      viewMenu.add(decreaseFontMenuItem);
      viewMenu.addSeparator();
      viewMenu.add(selectFontMenuItem);
      jMenuBar1.add(viewMenu);

      this.setJMenuBar(jMenuBar1);

      // --- Retrieving font properties

      Font currentFont = jTextArea1.getFont();
      int size = currentFont.getSize();
      int style = currentFont.getStyle();
      String fontName = currentFont.getFontName();

      try {
         prefs = Preferences.userNodeForPackage(this.getClass());
         fontName = prefs.get(FontNameKey, currentFont.getFontName());
         size = prefs.getInt(FontSizeKey, 12);
         style = prefs.getInt(FontStyleKey, 0);
      }
      catch (Exception ex) {
         System.err.println("Error retrieving Font Preferences: " + ex.getMessage());
         return;
      }

      GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();

      Font newFont = new Font(fontName, style, size);
      jTextArea1.setFont(newFont);

      setupActionButton();
   }

   private void setupActionButton() {
      switch (buttonAction) {
         case QUIT_WITH_SAVING:
            actionButton.setText("Save & Quit");
            actionButton.setToolTipText("Quit with saving text");
            break;
         case QUIT_WITHOUT_SAVING:
         default:
            actionButton.setText("Quit");
            actionButton.setToolTipText("Quit without saving text");
      }
   }

   public void setActionButton(int action) {
      buttonAction = action;
      setupActionButton();
   }

   public void setButtonText(String text) {
      actionButton.setText(text);
   }

   public void setDefaultFileName(String name) {
      defaultFileName = name;
   }

   public void setText(String text) {
      jTextArea1.setText(text);
      jTextArea1.setCaretPosition(0);
   }

   public void appendText(String text) {
      jTextArea1.append(text);
   }

   public void setText(ByteArrayOutputStream stream) {
      jTextArea1.setText(stream.toString());
   }

   public void jButton1_actionPerformed(ActionEvent e) {
      switch (buttonAction) {
         case QUIT_WITH_SAVING:
            saveTextIntoFile();
            break;
         case QUIT_WITHOUT_SAVING:
         default:
      }
      setVisible(false);
   }

   public void enableTextEditing(boolean enable) {
      jTextArea1.setEditable(enable);
   }

   @Override
  public void actionPerformed(ActionEvent actionEvent) {
      if (actionEvent.getSource() == increaseFontMenuItem) {
         Font currentFont = jTextArea1.getFont();
         int size = currentFont.getSize() + 1;
         Font newFont = new Font(currentFont.getName(), currentFont.getStyle(),
                                 size);
         jTextArea1.setFont(newFont);
         saveFontPreferences();
      }
      else if (actionEvent.getSource() == decreaseFontMenuItem) {
         Font currentFont = jTextArea1.getFont();
         int size = currentFont.getSize();
         size = size > 8 ? size - 1 : size;
         Font newFont = new Font(currentFont.getName(), currentFont.getStyle(),
                                 size);
         jTextArea1.setFont(newFont);
         saveFontPreferences();
      }
      else if (actionEvent.getSource() == selectFontMenuItem) {
         if (fontSelector == null) {
            fontSelector = new FontSelectorDialog(this, "Select Font", true);
         }
         fontSelector.setVisible(true);
         if (!fontSelector.okPressed) {
            return;
         }
         Font newFont = new Font(fontSelector.getFontName(),
                                 fontSelector.getFontStyle(),
                                 fontSelector.getFontSize());
         jTextArea1.setFont(newFont);
         saveFontPreferences();
      }
   }

   private void saveFontPreferences() {
      Font currentFont = jTextArea1.getFont();
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

   public void saveAsMenuItem_actionPerformed(ActionEvent e) {
      saveTextIntoFile();
   }

   private void saveTextIntoFile() {
      String text = jTextArea1.getText();
      if (text == null || text.length() < 1) {
         JOptionPane.showMessageDialog(this, "Nothing to save", "Warning", JOptionPane.WARNING_MESSAGE);
         return;
      }

      FileDialog fd = new FileDialog(this, "Save File", FileDialog.SAVE);
      fd.setFile(defaultFileName);
      fd.setVisible(true);
      if (fd.getFile() != null) {
         String fileName = fd.getFile();
         String workingDirectory = fd.getDirectory();
         try {
            IOUtils.saveStringIntoFile(text, workingDirectory + fileName);
         }
         catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error Saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   class JShowText_saveAsMenuItem_actionAdapter
       implements ActionListener {
      private JShowText adaptee;
      JShowText_saveAsMenuItem_actionAdapter(JShowText adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.saveAsMenuItem_actionPerformed(e);
      }
   }

   class JShowText_jButton1_actionAdapter
       implements ActionListener {
      private JShowText adaptee;
      JShowText_jButton1_actionAdapter(JShowText adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.jButton1_actionPerformed(e);
      }
   }
}
