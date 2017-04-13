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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

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
public class LogPanel
    extends JFrame implements ActionListener {

  Preferences prefs;
  FontSelectorDialog fontSelector = null;
  static String FontNameKey = "FontName";
  static String FontSizeKey = "FontSize";
  static String FontStyleKey = "FontStyle";
  static String defaultFontName = "default";
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea jTextArea1 = new JTextArea();
  JPanel jPanel1 = new JPanel();
  JButton clearButton = new JButton();
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu viewMenu = new JMenu();
  JMenuItem increaseFontMenuItem = new JMenuItem();
  JMenuItem decreaseFontMenuItem = new JMenuItem();
  JMenuItem selectFontMenuItem = new JMenuItem();
  JButton hideButton = new JButton();
  JMenu fileMenu = new JMenu();
  JMenuItem saveAsMenuItem = new JMenuItem();
  JMenuItem hideMenuItem = new JMenuItem();
  LogPanel_jTextArea1_caretAdapter caretUpdateAdaptor = null;

  public LogPanel(String title) {
    super(title);
    //super(owner, title, modal);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public LogPanel() {
    this("LogPanel");
  }

  private void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jTextArea1.setEditable(false);
    jTextArea1.setText("jTextArea1");
    clearButton.setToolTipText("Clear Log");
    clearButton.setText("Clear Log");
    clearButton.addActionListener(new LogPanel_jButton1_actionAdapter(this));
    viewMenu.setToolTipText("");
    viewMenu.setText("View");
    increaseFontMenuItem.setToolTipText("");
    increaseFontMenuItem.setText("Increase Font Size");
    increaseFontMenuItem.addActionListener(this);
    decreaseFontMenuItem.addActionListener(this);
    selectFontMenuItem.addActionListener(this);
    decreaseFontMenuItem.setText("Decrease Font Size");
    selectFontMenuItem.setText("Select Font");
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    hideButton.setToolTipText("Hide a Panel");
    hideButton.setText("Hide");
    hideButton.addActionListener(new LogPanel_hideButton_actionAdapter(this));
    fileMenu.setText("File");
    saveAsMenuItem.setText("Save As");
    saveAsMenuItem.addActionListener(new LogPanel_saveAsMenuItem_actionAdapter(this));
    hideMenuItem.setText("Hide");
    hideMenuItem.addActionListener(new LogPanel_hideMenuItem_actionAdapter(this));
    getContentPane().add(panel1);
    panel1.add(jScrollPane1, BorderLayout.CENTER);
    this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(hideButton);
    jPanel1.add(clearButton);
    jScrollPane1.getViewport().add(jTextArea1);
    jMenuBar1.add(fileMenu);
    jMenuBar1.add(viewMenu);
    fileMenu.add(saveAsMenuItem);
    fileMenu.add(hideMenuItem);
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
    } catch (Exception ex) {
      System.err.println("Error retrieving Font Preferences: " + ex.getMessage());
      return;
    }

    GraphicsEnvironment gEnv =
        GraphicsEnvironment.getLocalGraphicsEnvironment();

    Font newFont = new Font(fontName, style, size);
    jTextArea1.setFont(newFont);
  }

  public JTextArea getTextArea() {
    return jTextArea1;
  }

  public void popupOnUpdate(boolean popup) {
    if (popup) {
      if (caretUpdateAdaptor == null) {
        caretUpdateAdaptor = new LogPanel_jTextArea1_caretAdapter(this);
      }
      jTextArea1.addCaretListener(caretUpdateAdaptor);
    } else {
      if (caretUpdateAdaptor != null) {
        jTextArea1.removeCaretListener(caretUpdateAdaptor);
      }
    }
  }

  public void addButton(JButton button) {
    jPanel1.add(button);
  }

  public void setText(String text) {
    jTextArea1.setText(text);
  }

  public String getText() {
    return jTextArea1.getText();
  }

  public void appendText(String text) {
    jTextArea1.append(text);
  }

  public void setText(ByteArrayOutputStream stream) {
    jTextArea1.setText(stream.toString());
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    this.jTextArea1.setText("");
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
    } else if (actionEvent.getSource() == decreaseFontMenuItem) {
      Font currentFont = jTextArea1.getFont();
      int size = currentFont.getSize();
      size = size > 8 ? size - 1 : size;
      Font newFont = new Font(currentFont.getName(), currentFont.getStyle(),
          size);
      jTextArea1.setFont(newFont);
      saveFontPreferences();
    } else if (actionEvent.getSource() == selectFontMenuItem) {
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
    } catch (Exception ex) {
      System.err.println("Error saving Font Preferences: "
          + ex.getMessage());
    }
  }

  public void hideButton_actionPerformed(ActionEvent e) {
    this.setVisible(false);
  }

  public void hideMenuItem_actionPerformed(ActionEvent e) {
    this.setVisible(false);
  }

  public void saveAsMenuItem_actionPerformed(ActionEvent e) {
    String text = jTextArea1.getText();
    if (text == null || text.length() < 1) {
      JOptionPane.showMessageDialog(this,
          "Nothing to save", "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    FileDialog fd = new FileDialog(this, "Save Errors Log into Text File",
        FileDialog.SAVE);
    fd.setFile("jmoleditor-stderr.txt");
    fd.setVisible(true);
    if (fd.getFile() != null) {
      String fileName = new String(fd.getFile());
      String workingDirectory = new String(fd.getDirectory());
      try {
        IOUtils.saveStringIntoFile(text, workingDirectory + fileName);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Error Saving Errors Log: " + ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void jTextArea1_caretUpdate(CaretEvent e) {
    this.setVisible(true);
  }

  private class LogPanel_hideButton_actionAdapter
      implements ActionListener {

    private LogPanel adaptee;

    LogPanel_hideButton_actionAdapter(LogPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.hideButton_actionPerformed(e);
    }
  }

  private class LogPanel_jButton1_actionAdapter
      implements ActionListener {

    private LogPanel adaptee;

    LogPanel_jButton1_actionAdapter(LogPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.jButton1_actionPerformed(e);
    }
  }

  class LogPanel_jTextArea1_caretAdapter
      implements CaretListener {

    private LogPanel adaptee;

    LogPanel_jTextArea1_caretAdapter(LogPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void caretUpdate(CaretEvent e) {
      adaptee.jTextArea1_caretUpdate(e);
    }
  }

  class LogPanel_saveAsMenuItem_actionAdapter
      implements ActionListener {

    private LogPanel adaptee;

    LogPanel_saveAsMenuItem_actionAdapter(LogPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.saveAsMenuItem_actionPerformed(e);
    }
  }

  class LogPanel_hideMenuItem_actionAdapter
      implements ActionListener {

    private LogPanel adaptee;

    LogPanel_hideMenuItem_actionAdapter(LogPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.hideMenuItem_actionPerformed(e);
    }
  }
}
