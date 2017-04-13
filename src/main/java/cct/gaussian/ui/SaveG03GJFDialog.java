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

package cct.gaussian.ui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cct.gaussian.GJFParserInterface;
import cct.interfaces.MoleculeInterface;
import cct.tools.FileFilterImpl;
import cct.tools.IOUtils;
import cct.tools.Utils;
import cct.tools.ui.FileChooserDialog;

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
public class SaveG03GJFDialog
    extends JDialog {

  final static String lastPWDKey = "lastPWD";

  boolean OK_pressed = false;
  GJFParserInterface gjfParser = null;
  SimpleG03EditorPanel simpleG03EditorPanel1 = new SimpleG03EditorPanel(gjfParser);
  FileChooserDialog fileChooser = null;
  String workingDirectory = null;
  JButton helpButton = new JButton();
  JPanel buttonPanel = new JPanel();
  JButton cancelButton = new JButton();
  JButton saveButton = new JButton();

  public SaveG03GJFDialog(Frame owner, String title, boolean modal) {
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

  private SaveG03GJFDialog() {
    this(new Frame(), "SaveG03GJFDialog", false);
  }

  public boolean isOKPressed() {
    return OK_pressed;
  }

  private void jbInit() throws Exception {
    helpButton.setEnabled(false);
    helpButton.setToolTipText("");
    helpButton.setText("Help");
    saveButton.setToolTipText("");
    saveButton.setText("Save");
    saveButton.addActionListener(new
                                 SaveG03GJFDialog_saveButton_actionAdapter(this));
    buttonPanel.setToolTipText("");
    cancelButton.addActionListener(new
                                   SaveG03GJFDialog_cancelButton_actionAdapter(this));
    cancelButton.setText("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(helpButton);
    this.getContentPane().add(simpleG03EditorPanel1,
                              java.awt.BorderLayout.CENTER);

    this.getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

    simpleG03EditorPanel1.enableValidateGeometry(false);

    Font currentFont = simpleG03EditorPanel1.getFontForText();
    int size = currentFont.getSize();
    int style = currentFont.getStyle();
    String fontName = currentFont.getFontName();

    if (size < 12) {
      size = 12;
      Font newFont = new Font(fontName, style, size);
      simpleG03EditorPanel1.setFontForText(newFont);
    }
    /*
           simpleG03EditorPanel1.setLinkZeroCommands(buffer);
          simpleG03EditorPanel1.setRouteSection(parser.getRouteSection(0));
          simpleG03EditorPanel1.setTitleSection(parser.getTitleSection(0));
          simpleG03EditorPanel1.setChargeSection(parser.getChargeSection(0));
          simpleG03EditorPanel1.setMoleculeSpecsSection(parser.
       getMoleculeSpecsSection(0));
     */

  }

  public void setGJFParser(GJFParserInterface parser) {
    gjfParser = parser;
    simpleG03EditorPanel1.setGeometryValidator(gjfParser);
  }

  public void setupMolecule(MoleculeInterface mol) {

    simpleG03EditorPanel1.setupMolecule(mol);

  }

  public void setLinkZeroCommands(String buffer) {
    simpleG03EditorPanel1.setLinkZeroCommands(buffer);
  }

  public void setRouteSection(String text) {
    simpleG03EditorPanel1.setRouteSection(text);
  }

  public void setTitleSection(String text) {
    simpleG03EditorPanel1.setTitleSection(text);
  }

  public void setChargeSection(String text) {
    simpleG03EditorPanel1.setChargeSection(text);
  }

  public void setMoleculeSpecsSection(String text) {
    simpleG03EditorPanel1.setMoleculeSpecsSection(text);
  }

  public JButton getHelpButton() {
    return this.helpButton;
  }

  public void saveButton_actionPerformed(ActionEvent e) {

    if (fileChooser == null) {
      FileFilterImpl filters[] = new FileFilterImpl[1];
      filters[0] = new FileFilterImpl();
      filters[0].addExtension("gjf");
      filters[0].addExtension("GJF");
      filters[0].addExtension("com");
      filters[0].addExtension("COM");
      filters[0].setDescription("Gaussian Input Files");

      workingDirectory = Utils.getPreference(this, lastPWDKey);

      fileChooser = new FileChooserDialog(filters, this, "Select file to save", JFileChooser.SAVE_DIALOG);
      fileChooser.setWorkingDirectory(workingDirectory);
    }

    String fileName = fileChooser.getFile();

    if (fileName == null) {
      return;
    }

    String pwd = fileChooser.getCurrentDirectory().getAbsolutePath();
    Utils.savePreference(this, lastPWDKey, pwd);

    /*
           FileDialog fd = new FileDialog(new Frame(), "Save Gaussian Job File",
                                   FileDialog.SAVE);
           //fd.setDirectory(directory);
           fd.setFile("*.gjf;*.com");

           fd.setVisible(true);
           if (fd.getFile() == null) {
       return;
           }
           String fileName = fd.getFile().indexOf(".") == -1 ? fd.getFile() + ".gjf" :
        fd.getFile();
     */

    if (fileName.indexOf(".") == -1) {
      fileName += ".gjf";
    }

    try {
      //IOUtils.saveStringIntoFile(simpleG03EditorPanel1.getStepAsString(),
      //                           fd.getDirectory() + fileName);
      IOUtils.saveStringIntoFile(simpleG03EditorPanel1.getStepAsString(),
                                 fileName);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
                                    "Error saving file: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    setVisible(false);
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  class SaveG03GJFDialog_cancelButton_actionAdapter
      implements ActionListener {
    private SaveG03GJFDialog adaptee;
    SaveG03GJFDialog_cancelButton_actionAdapter(SaveG03GJFDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.cancelButton_actionPerformed(e);
    }
  }

  class SaveG03GJFDialog_saveButton_actionAdapter
      implements ActionListener {
    private SaveG03GJFDialog adaptee;
    SaveG03GJFDialog_saveButton_actionAdapter(SaveG03GJFDialog adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.saveButton_actionPerformed(e);
    }
  }

}
