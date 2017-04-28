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

import cct.gaussian.GJFParserInterface;
import cct.interfaces.MoleculeInterface;
import cct.tools.ui.FileChooserDialog;
import javafx.application.Platform;
import legacy.editor.commons.Settings;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

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
public class ExportSEAGridGaussian
    extends JDialog {

  final static String lastPWDKey = "lastPWD";

  boolean OK_pressed = false;
  GJFParserInterface gjfParser = null;
  SimpleG03EditorPanel simpleGaussianEditorPanel1 = new SimpleG03EditorPanel(gjfParser);
  FileChooserDialog fileChooser = null;
  String workingDirectory = null;
  JButton helpButton = new JButton();
  JPanel buttonPanel = new JPanel();
  JButton cancelButton = new JButton();
  JButton export = new JButton();

  public ExportSEAGridGaussian(Frame owner, String title, boolean modal) {
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

  private ExportSEAGridGaussian() {
    this(new Frame(), "ExportSEAGridGaussian", false);
  }

  public boolean isOKPressed() {
    return OK_pressed;
  }

  private void jbInit() throws Exception {
    helpButton.setEnabled(false);
    helpButton.setToolTipText("");
    helpButton.setText("Help");
    export.setToolTipText("");
    export.setText("Export");
    export.addActionListener(new
            SEAGridGaussianExport_actionAdapter(this));
    buttonPanel.setToolTipText("");
    cancelButton.addActionListener(new
            SEAGridExportGaussian_cancelButton_actionAdapter(this));
    cancelButton.setText("Cancel");
    buttonPanel.add(export);
    buttonPanel.add(cancelButton);
    buttonPanel.add(helpButton);
    this.getContentPane().add(simpleGaussianEditorPanel1,
                              java.awt.BorderLayout.CENTER);

    this.getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

    simpleGaussianEditorPanel1.enableValidateGeometry(false);

    Font currentFont = simpleGaussianEditorPanel1.getFontForText();
    int size = currentFont.getSize();
    int style = currentFont.getStyle();
    String fontName = currentFont.getFontName();

    if (size < 12) {
      size = 12;
      Font newFont = new Font(fontName, style, size);
      simpleGaussianEditorPanel1.setFontForText(newFont);
    }
    /*
           simpleGaussianEditorPanel1.setLinkZeroCommands(buffer);
          simpleGaussianEditorPanel1.setRouteSection(parser.getRouteSection(0));
          simpleGaussianEditorPanel1.setTitleSection(parser.getTitleSection(0));
          simpleGaussianEditorPanel1.setChargeSection(parser.getChargeSection(0));
          simpleGaussianEditorPanel1.setMoleculeSpecsSection(parser.
       getMoleculeSpecsSection(0));
     */

  }

  public void setGJFParser(GJFParserInterface parser) {
    gjfParser = parser;
    simpleGaussianEditorPanel1.setGeometryValidator(gjfParser);
  }

  public void setupMolecule(MoleculeInterface mol) {

    simpleGaussianEditorPanel1.setupMolecule(mol);

  }

  public void setLinkZeroCommands(String buffer) {
    simpleGaussianEditorPanel1.setLinkZeroCommands(buffer);
  }

  public void setRouteSection(String text) {
    simpleGaussianEditorPanel1.setRouteSection(text);
  }

  public void setTitleSection(String text) {
    simpleGaussianEditorPanel1.setTitleSection(text);
  }

  public void setChargeSection(String text) {
    simpleGaussianEditorPanel1.setChargeSection(text);
  }

  public void setMoleculeSpecsSection(String text) {
    simpleGaussianEditorPanel1.setMoleculeSpecsSection(text);
  }

  public JButton getHelpButton() {
    return this.helpButton;
  }

  public void exportButton_actionPerformed(ActionEvent e) {

    try {
      File f = new File(Settings.getApplicationDataDir() + Settings.fileSeparator
              + "tmp.txt");
      FileWriter fw = new FileWriter(f, false);
      String gaussOut = simpleGaussianEditorPanel1.getStepAsString();
      fw.write(gaussOut);
      System.err.println("gaussOut = ");
      System.err.println(gaussOut);
      fw.close();

      Platform.runLater(() -> {
        SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType
                .EXPORT_GAUSSIAN_EXP, gaussOut));
      });
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
                                    "Error exporting file to SEAGrid: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    setVisible(false);
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  class SEAGridExportGaussian_cancelButton_actionAdapter
      implements ActionListener {
    private ExportSEAGridGaussian adaptee;
    SEAGridExportGaussian_cancelButton_actionAdapter(ExportSEAGridGaussian adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.cancelButton_actionPerformed(e);
    }
  }

  class SEAGridGaussianExport_actionAdapter
      implements ActionListener {
    private ExportSEAGridGaussian adaptee;
    SEAGridGaussianExport_actionAdapter(ExportSEAGridGaussian adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.exportButton_actionPerformed(e);
    }
  }

}
