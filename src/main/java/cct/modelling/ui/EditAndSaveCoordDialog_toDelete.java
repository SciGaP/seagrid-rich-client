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
package cct.modelling.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import cct.interfaces.CoordinateBuilderInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.FormatManager;
import cct.tools.IOUtils;
import cct.tools.TextClipboard;
import javax.swing.ScrollPaneConstants;

/**
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EditAndSaveCoordDialog_toDelete
        extends JFrame implements ItemListener {

  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JTextArea coordTextArea = new JTextArea();
  private JScrollPane jScrollPane1 = new JScrollPane(coordTextArea);
  private JButton cancelButton = new JButton();
  private JRadioButton jRadioButton1 = new JRadioButton();
  private JPanel formatsPanel = new JPanel();
  private BorderLayout borderLayout3 = new BorderLayout();
  private JButton okButton = new JButton();
  private ButtonGroup buttonGroup = new ButtonGroup();
  private JRadioButton jRadioButton2 = new JRadioButton();
  private FlowLayout flowLayout1 = new FlowLayout();
  private MoleculeInterface molecule = null;
  private String selectedBuilder = "";
  private JMenuBar jMenuBar1 = new JMenuBar();
  private JMenu jMenu1 = new JMenu();
  private JMenuItem jMenuItem1 = new JMenuItem();
  private JMenuItem jMenuItem2 = new JMenuItem();
  private String defaultFileName = "text.txt";
  private JPanel jPanel3 = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private FlowLayout flowLayout2 = new FlowLayout();

  public EditAndSaveCoordDialog_toDelete(String title) {
    super(title);
    try {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      pack();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public EditAndSaveCoordDialog_toDelete() {
    this("Edit And Save Coordinates Dialog");
  }

  private void jbInit() throws Exception {
    coordTextArea.setText("jTextArea1");
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    jRadioButton1.setSelected(true);
    jRadioButton1.setText("jRadioButton1");
    formatsPanel.setLayout(flowLayout1);
    jPanel2.setLayout(borderLayout3);
    okButton.setToolTipText("");
    okButton.setText("  OK  ");
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jRadioButton2.setText("jRadioButton1");
    flowLayout1.setAlignment(FlowLayout.LEFT);
    formatsPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow, 1), "Formats"));
    coordTextArea.setMinimumSize(new Dimension(10, 10));
    coordTextArea.setPreferredSize(new Dimension(10, 10));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setJMenuBar(jMenuBar1);
    jMenu1.setText("File");
    jMenuItem1.setText("Save As");
    jMenuItem1.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        jMenuItem1_actionPerformed(e);
      }
    });
    jMenuItem2.setText("Exit");
    jMenuItem2.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        jMenuItem2_actionPerformed(e);
      }
    });
    //jPanel3.setLayout(borderLayout2);
    jPanel1.setLayout(flowLayout2);
    jPanel1.add(okButton);
    jPanel1.add(cancelButton);
    jPanel2.add(formatsPanel, BorderLayout.NORTH);
    jPanel2.add(jPanel1, BorderLayout.SOUTH);
    formatsPanel.add(jRadioButton2, null);
    formatsPanel.add(jRadioButton1, null);
    //jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);
    //jScrollPane1.getViewport().add(coordTextArea);
    jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.setWheelScrollingEnabled(true);
    buttonGroup.add(jRadioButton2);
    buttonGroup.add(jRadioButton1);
    jMenuBar1.add(jMenu1);
    jMenu1.add(jMenuItem1);
    jMenu1.addSeparator();
    jMenu1.add(jMenuItem2);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);

    //this.getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);
    getContentPane().add(jScrollPane1, BorderLayout.CENTER);

    Font currentFont = coordTextArea.getFont();
    int size = currentFont.getSize();
    int style = currentFont.getStyle();
    String fontName = currentFont.getFontName();

    if (size < 12) {
      Font newFont = new Font(fontName, style, 12);
      coordTextArea.setFont(newFont);
    }

    setFormatManager();
  }

  public void setFormatManager() throws Exception {
    String[] builders = FormatManager.getCoordinateBuilders();

    formatsPanel.removeAll();

    if (builders == null) {
      throw new Exception("Cannot get list of coordinate builders: ");
    }

    coordTextArea.setText("");
    for (int i = 0; i < builders.length; i++) {
      JRadioButton jRadioButton = new JRadioButton();
      jRadioButton.setText(builders[i]);
      jRadioButton.setActionCommand(builders[i]);
      formatsPanel.add(jRadioButton);
      buttonGroup.add(jRadioButton);
      jRadioButton.addItemListener(this);
      if (i == 0) {
        jRadioButton.setSelected(true);
      } else {
        jRadioButton.setSelected(false);
      }
    }
    okButton.grabFocus();

    this.pack();
  }

  public void setDefaultFileName(String file_name) {
    defaultFileName = file_name;
  }

  public void setMoleculeInterface(MoleculeInterface mol) {
    molecule = mol;
    buildCoordinates();
    okButton.grabFocus();
  }

  private void buildCoordinates() {
    if (molecule == null || molecule.getNumberOfAtoms() < 1) {
      return;
    }

    try {
      CoordinateBuilderInterface cbi = FormatManager.getCoordinateBuilder(selectedBuilder);
      coordTextArea.setText(cbi.getCoordinatesAsString(molecule, true));
      this.validate();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Cannot get coordinate builder " + selectedBuilder + " : " + ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
      return;
    }

  }

  @Override
  public void itemStateChanged(ItemEvent e) {

    if (e.getStateChange() == ItemEvent.SELECTED) {
      selectedBuilder = ((JRadioButton) e.getSource()).getActionCommand();
      buildCoordinates();
    }
    okButton.grabFocus();
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  public void jMenuItem2_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  private void saveTextIntoFile() {
    String text = coordTextArea.getText();
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
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error Saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void jMenuItem1_actionPerformed(ActionEvent e) {
    saveTextIntoFile();
  }

  public void okButton_actionPerformed(ActionEvent e) {
    TextClipboard textTransfer = new TextClipboard();
    textTransfer.setClipboardContents(coordTextArea.getText());
    setVisible(false);
  }

  public static void main(String[] args) {
    EditAndSaveCoordDialog_toDelete frame = new EditAndSaveCoordDialog_toDelete();
    frame.setVisible(true);
  }
}
