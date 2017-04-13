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

package cct.grid.ui.gaussian;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cct.gaussian.Gaussian;
import cct.grid.CheckPoint;
import cct.grid.ClientProgramInterface;
import cct.grid.GridProviderInterface;
import cct.grid.JobDescription;
import cct.grid.OperationalSystems;
import cct.grid.ResourcesProviderInterface;
import cct.interfaces.FileChooserInterface;

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
public class SubmitGaussianPanel
    extends JPanel implements ClientProgramInterface {

  final static String gaussianOutputFileTag = "gaussianOutput";
  final static String softwareName = "Gaussian";

  static final String localDirKey = "localDir";
  static final String remoteDirKey = "remoteDir";
  static final String executableKey = "executableName";
  static final String scriptContentKey = "scriptContent";
  static final String execPattern = "%exec%";
  static final String inputFilePattern = "%input%";
  static final String outputFilePattern = "%output%";
  private String gaussianExecutable = "g09";
  static final java.util.List<String> PreprocessDirectives = new ArrayList<String> ();
  static String defaultScriptContent = "#!/bin/csh \n" +
      "module load gaussian\n" +
      "dos2unix " + inputFilePattern + "\n" +
      execPattern + " <" + inputFilePattern + " > " + outputFilePattern + "\n";

  static String scriptContent = defaultScriptContent;

  static {
    PreprocessDirectives.add("module load dos2unix");
    PreprocessDirectives.add("dos2unix " + inputFilePattern);
    PreprocessDirectives.add("module load gaussian");
  }

  private Gaussian gaussianInputParser = null;
  private FileChooserInterface remoteFileChooser = null;
  private String remoteDirectory = "";
  private Preferences prefs = Preferences.userNodeForPackage(getClass());
  private ScriptEditorFrame scriptEditorFrame = null;
  private ResourcesProviderInterface Resources = null;
  private GridProviderInterface Provider = null;

  JLabel ScriptLabel = new JLabel();
  JTextField ScriptTextField = new JTextField();
  JCheckBox InputCheckBox = new JCheckBox();
  JButton inputButton = new JButton();
  JButton remoteDirButton = new JButton();
  JButton executableButton = new JButton("Browse");
  JButton localDirButton = new JButton();
  JButton editScriptButton = new JButton();
  JLabel RemoteDirLabel = new JLabel();
  JTextField RemoteDirTextField = new JTextField();
  JLabel LocalDirLabel = new JLabel();
  JTextField LocalDirTextField = new JTextField();
  JLabel StderrLabel = new JLabel();
  JLabel executableLabel = new JLabel("Executable: ");
  JTextField StderrTextField = new JTextField();
  JTextField executableTextField = new JTextField();
  JLabel StdoutLabel = new JLabel();
  JTextField StdoutTextField = new JTextField();
  JCheckBox StderrCheckBox = new JCheckBox();
  JCheckBox StdoutCheckBox = new JCheckBox();
  JButton StdoutButton = new JButton();
  JButton StderrButton = new JButton();
  JTextField OutputFileTextField = new JTextField();
  JLabel OutputFileLabel = new JLabel();
  JLabel jobNameLabel = new JLabel();
  JTextField jobNameTextField = new JTextField();
  JLabel inputFileLabel = new JLabel();
  JTextField inputFileTextField = new JTextField();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  public SubmitGaussianPanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public String getSoftwareName() {
    return softwareName;
  }

  public java.util.List<String> getPreprocessDirectives() {
    java.util.List<String> dirs = new ArrayList<String> ();

    String input_file = this.getInputFile();
    String output_file = this.getOutputFile();
    gaussianExecutable = this.getExecutable();

    for (int i = 0; i < PreprocessDirectives.size(); i++) {
      String line = PreprocessDirectives.get(i);
      try {
        line = line.replaceAll(inputFilePattern, input_file);
        line = line.replaceAll(outputFilePattern, output_file);
        line = line.replaceAll(execPattern, gaussianExecutable);
        dirs.add(line);
      }
      catch (Exception ex) {
        System.err.println("Cannot do substitution in line: " + line + " : " + ex.getMessage());
      }
    }
    return dirs;
  }

  public void setRemoteFileChooser(FileChooserInterface rFC) {
    remoteFileChooser = rFC;

    if (remoteFileChooser == null) {
      if (InputCheckBox.isSelected()) {
        inputButton.setEnabled(true);
      }
      else {
        inputButton.setEnabled(false);
      }
      remoteDirButton.setEnabled(false);
      executableButton.setEnabled(false);
    }

    else {
      inputButton.setEnabled(true);
      remoteDirButton.setEnabled(true);
      executableButton.setEnabled(true);
    }

  }

  public void setResourcesInterface(ResourcesProviderInterface resources) {
    Resources = resources;
  }

  public void setTaskProviderInterface(GridProviderInterface provider) {
    Provider = provider;

    setRemoteFileChooser(provider.getRemoteFileChooser());

    this.RemoteDirTextField.setEnabled(provider.isRemoteDirectorySelectable());
    this.remoteDirButton.setEnabled(provider.isRemoteDirectorySelectable());
  }

  public String getCommandsAsString() throws Exception {
    StringWriter sWriter = new StringWriter();

    // --- Error check

    if (this.getExecutable().length() < 1) {
      throw new Exception("Gaussian executable is not set");
    }

    boolean windowsOS = false;
    if (Provider != null && Provider.getOS() == OperationalSystems.WindowsOS) {
      windowsOS = true;
    }

    String input_file = this.getInputFile();
    String output_file = this.getOutputFile();
    gaussianExecutable = this.getExecutable();

    if (!windowsOS) {
      sWriter.write("\n");
      for (int i = 0; i < PreprocessDirectives.size(); i++) {
        String line = PreprocessDirectives.get(i);
        try {
          line = line.replaceAll(inputFilePattern, input_file);
          line = line.replaceAll(outputFilePattern, output_file);
          line = line.replaceAll(execPattern, gaussianExecutable);
        }
        catch (Exception ex) {
          throw new Exception("Cannot do substitution in line: " + line + " : " + ex.getMessage());
        }
        sWriter.write(line + "\n");
      }
      sWriter.write("\n");
    }

    // --- gaussian executable & its arguments

    //sWriter.write(getExecutable() + "<" + input_file + " >" + output_file + "\n\n");

    try {
      sWriter.close();
    }
    catch (Exception ex) {}
    return sWriter.toString();
  }

  private void jbInit() throws Exception {

    this.setLayout(gridBagLayout1);
    ScriptLabel.setToolTipText("");
    ScriptLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    ScriptLabel.setText("Run Script: ");
    ScriptTextField.setSelectionStart(11);
    ScriptTextField.setText(" ");
    ScriptTextField.setColumns(15);
    InputCheckBox.setSelected(true);
    InputCheckBox.setText("Local File");
    InputCheckBox.addItemListener(new
                                  SubmitGaussianPanel_InputCheckBox_itemAdapter(this));
    inputButton.setToolTipText("Select input file");
    inputButton.setText("Browse");
    inputButton.addActionListener(new
                                  SubmitGaussianPanel_ScriptButton_actionAdapter(this));
    editScriptButton.setEnabled(false);
    editScriptButton.setToolTipText("Edit script file");
    editScriptButton.setText("Edit");
    editScriptButton.addActionListener(new
                                       SubmitGaussianPanel_editScriptButton_actionAdapter(this));
    RemoteDirLabel.setToolTipText("");
    RemoteDirLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    RemoteDirLabel.setText("Remote Directory: ");
    RemoteDirTextField.setColumns(15);
    RemoteDirTextField.addActionListener(new
                                         SubmitGaussianPanel_RemoteDirTextField_actionAdapter(this));
    LocalDirLabel.setToolTipText("");
    LocalDirLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    LocalDirLabel.setText("Local Directory: ");
    LocalDirTextField.setText(" ");
    LocalDirTextField.setColumns(15);
    StderrLabel.setToolTipText("");
    StderrLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    StderrLabel.setText("Errors Log: ");
    StderrTextField.setEnabled(true);
    StderrTextField.setToolTipText("Standard error output");
    StderrTextField.setText("");
    StderrTextField.setColumns(15);

    StdoutLabel.setToolTipText("");
    StdoutLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    StdoutLabel.setText("Output Log: ");
    StdoutTextField.setEnabled(true);
    StdoutTextField.setToolTipText("");
    StdoutTextField.setText("");
    StdoutTextField.setColumns(15);
    StderrCheckBox.setEnabled(false);
    StderrCheckBox.setSelected(true);
    StderrCheckBox.setText("Local File");
    StdoutCheckBox.setEnabled(false);
    StdoutCheckBox.setSelected(true);
    StdoutCheckBox.setText("Local File");
    StdoutCheckBox.addItemListener(new
                                   SubmitGaussianPanel_StdoutCheckBox_itemAdapter(this));
    StdoutButton.setEnabled(false);
    StdoutButton.setToolTipText("");
    StdoutButton.setText("Browse");
    StderrButton.setEnabled(false);
    StderrButton.setToolTipText("");
    StderrButton.setText("Browse");
    OutputFileTextField.setEnabled(true);
    OutputFileTextField.setEditable(false);
    OutputFileTextField.setText(" ");
    OutputFileTextField.setColumns(15);
    OutputFileLabel.setToolTipText("");
    OutputFileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    OutputFileLabel.setText("Output File: ");
    jobNameLabel.setToolTipText("");
    jobNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    jobNameLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
    jobNameLabel.setText("Job Name: ");
    jobNameTextField.setToolTipText("");
    jobNameTextField.setText(" ");
    jobNameTextField.setColumns(15);
    inputFileLabel.setToolTipText("");
    inputFileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    inputFileLabel.setText("Input File: ");
    inputFileTextField.setText(" ");
    inputFileTextField.setColumns(15);
    remoteDirButton.setToolTipText("Select Remote Directory");
    remoteDirButton.setText("Browse");
    localDirButton.setToolTipText("Select Local Directory");
    localDirButton.setText("Browse");
    localDirButton.addActionListener(new
                                     SubmitGaussianPanel_localDirButton_actionAdapter(this));
    remoteDirButton.addActionListener(new
                                      SubmitGaussianPanel_remoteDirButton_actionAdapter(this));
    executableButton.addActionListener(new SubmitGaussianPanel_executableButton_actionAdapter(this));

    this.add(jobNameTextField, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(inputFileTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(InputCheckBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(OutputFileTextField, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(StdoutTextField, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(StdoutCheckBox, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(StdoutButton, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2, 2, 2, 2), 0, 0));
    this.add(StderrCheckBox, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(StderrButton, new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2, 2, 2, 2), 0, 0));
    this.add(jobNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2, 2, 2, 2), 0, 0));
    this.add(inputFileLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(inputButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.NONE,
                                                 new Insets(2, 2, 2, 5), 0, 0));
    this.add(ScriptLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.EAST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(2, 2, 2, 2), 0, 0));

    this.add(ScriptTextField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(LocalDirLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(OutputFileLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0));
    this.add(RemoteDirLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 5, 2, 2), 0, 0));
    this.add(StdoutLabel,
             new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2),
                                    0, 0));
    this.add(StderrLabel,
             new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2),
                                    0, 0));
    this.add(StderrTextField, new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
    this.add(editScriptButton,
             new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                    new Insets(2, 2, 2, 2), 0, 0));
    this.add(RemoteDirTextField,
             new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                    new Insets(2, 2, 2, 2), 0, 0));
    this.add(remoteDirButton,
             new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                    new Insets(2, 2, 2, 5),
                                    0, 0));
    this.add(LocalDirTextField,
             new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                    new Insets(2, 2, 2, 2), 0, 0));

    this.add(localDirButton,
             new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                    new Insets(2, 2, 2, 5),
                                    0, 0));

    add(executableLabel,
        new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0,
                               0));
    add(executableTextField,
        new GridBagConstraints(1, 8, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                               new Insets(2, 2, 2, 2),
                               0, 0));
    add(executableButton,
        new GridBagConstraints(3, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 5),
                               0,
                               0));

    remoteDirectory = prefs.get(remoteDirKey, "");
    RemoteDirTextField.setText(remoteDirectory);

    String executabelName = prefs.get(executableKey, gaussianExecutable);
    executableTextField.setText(executabelName);

    String scriptText = prefs.get(scriptContentKey, "");
    if (scriptText.trim().length() == 0) {
      prefs.put(scriptContentKey, scriptContent);
    }
    else {
      scriptContent = scriptText;
    }

    if (remoteFileChooser == null) {
      inputButton.setEnabled(false);
      remoteDirButton.setEnabled(false);
      executableButton.setEnabled(false);
    }
    else {
      inputButton.setEnabled(true);
      remoteDirButton.setEnabled(true);
      executableButton.setEnabled(true);
    }

  }

  public void saveCurrentSettings() {
    prefs.put(remoteDirKey, RemoteDirTextField.getText().trim());

  }

  public String getLocalDirectory() {
    return LocalDirTextField.getText().trim();
  }

  public void ScriptButton_actionPerformed(ActionEvent e) {
    if (!inputButton.isEnabled()) {
      return;
    }
    inputButton.setEnabled(false);
    String fileName = "";
    if (InputCheckBox.isSelected()) { // -- Local file
      FileDialog fd = new FileDialog(new Frame(), "Open Gaussian Input File",
                                     FileDialog.LOAD);
      fd.setFile("*.gjf;*.com;*.g03");
      fd.setVisible(true);
      if (fd.getFile() != null) {

        if (fd.getFile().lastIndexOf(".") == -1) {
          JOptionPane.showMessageDialog(null,
                                        "Gaussian Input file should have an extention",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
          inputButton.setEnabled(true);
          return;
        }

        fileName = fd.getFile();

        Gaussian gaussianInputParser = new Gaussian();
        gaussianInputParser.parseGJF(fd.getDirectory() + fileName, 0);
        if (gaussianInputParser.getErrorMessage() != null) {
          int n =
              JOptionPane.showConfirmDialog(null,
                                            "Script file might contain error(s)\n" + gaussianInputParser.getErrorMessage() +
                                            "\n" +
                                            "Do you still want to continue?", "DO you want to continue?",
                                            JOptionPane.YES_NO_OPTION);
          if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
            return;
          }
        }

        if (this.Resources != null) {
          Resources.setNCPUs(gaussianInputParser.getNumberOfProcessors());
          int memory = gaussianInputParser.getMemory();
          if (memory < 1024) {
            memory = 100;
          }
          else {
            memory /= 1024;
            memory += 100;
          }
          Resources.setMaxMemoryInMb(memory);
        }

        setInputFile(fileName);
        LocalDirTextField.setText(fd.getDirectory());
      }
    }

    else { // Remote file
      try {
        remoteFileChooser.setFileChooserVisible(true);
        if (remoteFileChooser.getFile() != null) {
          fileName = remoteFileChooser.getFile();
          setInputFile(fileName);
        }
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }

    inputButton.setEnabled(true);
  }

  public void setInputFile(String fileName) {
    if (fileName.indexOf(".") == -1) {
      fileName += ".gjf";
    }
    inputFileTextField.setText(fileName);
    jobNameTextField.setText(fileName.substring(0, fileName.lastIndexOf(".")));
    ScriptTextField.setText(fileName.substring(0, fileName.lastIndexOf(".")) +
                            ".sh");
    OutputFileTextField.setText(fileName.substring(0, fileName.lastIndexOf(".")) +
                                ".log");
    StdoutTextField.setText(fileName.substring(0, fileName.lastIndexOf(".")) +
                            ".stdout");
    StderrTextField.setText(fileName.substring(0, fileName.lastIndexOf(".")) +
                            ".stderr");

  }

  public String getJobName() {
    return jobNameTextField.getText().trim();
  }

  public String getOutputFileName() {
    return OutputFileTextField.getText().trim();
  }

  public String getStdoutFileName() {
    return StdoutTextField.getText().trim();
  }

  public String getStderrFileName() {
    return StderrTextField.getText().trim();
  }

  public String getScriptName() {
    return ScriptTextField.getText().trim();
  }

  public String getRemoteDirectory() {
    return RemoteDirTextField.getText().trim();
  }

  public void RemoteDirTextField_actionPerformed(ActionEvent e) {
    remoteDirectory = RemoteDirTextField.getText().trim();
    try {
      prefs.put(remoteDirKey, remoteDirectory);
    }
    catch (Exception ex) {
      System.err.println("Warning: unable to save remote directory: " +
                         ex.getMessage());
    }
  }

  public void editScriptButton_actionPerformed(ActionEvent e) {
    if (ScriptTextField.getText().trim().length() == 0) {
      return;
    }

    if (scriptEditorFrame == null) {
      ConfirmEditing_ActionAdapter al = new ConfirmEditing_ActionAdapter();
      scriptEditorFrame = new ScriptEditorFrame(al);
    }
    scriptEditorFrame.setText(scriptContent);
    scriptEditorFrame.setVisible(true);
  }

  public void validateScript(String text) throws NullPointerException,
      IllegalArgumentException {

    if (text.trim().length() == 0) {
      throw new NullPointerException();
    }
    String lines[] = text.split("\n");
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].indexOf(gaussianExecutable) != -1 &&
          lines[i].indexOf(inputFilePattern) != -1) {
        return;
      }

    }
    throw new IllegalArgumentException();
  }

  public String getScriptContents() {
    String contents = scriptContent;
    gaussianExecutable = executableTextField.getText().trim();
    contents = contents.replaceAll(execPattern, gaussianExecutable);
    return contents.replaceAll(inputFilePattern, getInputFileName());
  }

  public String getInputFileName() {
    return inputFileTextField.getText().trim();
  }

  public void StdoutCheckBox_itemStateChanged(ItemEvent e) {

  }

  public void setExecutable(String executable) {
    gaussianExecutable = executable.trim();
    executableTextField.setText(gaussianExecutable);
  }

  public String getExecutable() {
    try {
      prefs.put(executableKey, executableTextField.getText().trim());
    }
    catch (Exception ex) {
      System.err.println("Warning: " + this.getClass().getCanonicalName() + ": cannot store executableKey=" +
                         executableTextField.getText().trim() + " : " + ex.getMessage());
    }
    return executableTextField.getText().trim();
  }

  public void InputCheckBox_itemStateChanged(ItemEvent e) {
    if (InputCheckBox.isSelected()) {
      inputButton.setEnabled(true);
      return;
    }

    if (remoteFileChooser == null) {
      inputButton.setEnabled(false);
    }
    else {
      inputButton.setEnabled(true);
    }
  }

  public void remoteDirButton_actionPerformed(ActionEvent e) {
    if (!remoteDirButton.isEnabled()) {
      return;
    }

    if (remoteFileChooser == null) {
      return;
    }

    remoteDirButton.setEnabled(false);
    try {
      remoteFileChooser.setFileChooserVisible(true);
      if (remoteFileChooser.getDirectory() != null) {
        RemoteDirTextField.setText(remoteFileChooser.getDirectory());
        prefs.put(remoteDirKey, remoteFileChooser.getDirectory());
      }
      else if (remoteFileChooser.getFile() != null) {
        String file = remoteFileChooser.getFile();
        if (file.indexOf("/") != -1) {
          file = file.substring(0, file.indexOf("/"));
          RemoteDirTextField.setText(file);
          prefs.put(remoteDirKey, file);
        }
      }
      else if (remoteFileChooser.pwd() != null) {
        String pwd = remoteFileChooser.pwd();
        RemoteDirTextField.setText(pwd);
        prefs.put(remoteDirKey, pwd);
      }
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    remoteDirButton.setEnabled(true);
  }

  public void localDirButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    String localDir = prefs.get(localDirKey, "");
    if (localDir.length() > 0) {
      File dir = new File(localDir);
      if (dir.isDirectory() && dir.exists()) {
        chooser.setCurrentDirectory(dir);
      }
    }
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int option = chooser.showOpenDialog(this);
    if (option == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
      localDir = chooser.getSelectedFile().getAbsolutePath();
      LocalDirTextField.setText(localDir);
      prefs.put(localDirKey, localDir);
    }
  }

  public void enableInputFile(boolean enable) {
    this.inputFileTextField.setEnabled(enable);
  }

  public void enableLocalDir(boolean enable) {
    this.LocalDirTextField.setEnabled(enable);
  }

  public String getInputFile() {
    return getInputFileName();
  }

  public String getOutputFile() {
    return getOutputFileName();
  }

  public String getScriptFile() {
    return this.ScriptTextField.getText().trim();
  }

  public Component getVisualComponent() {
    return this;
  }

  public void setLocalDirectory(String localDir) {
    this.LocalDirTextField.setText(localDir);
  }

  public void setRemoteDirectory(String remoteDir) {
    this.RemoteDirTextField.setText(remoteDir);
  }

  public void setCheckPointFile(CheckPoint chkp) {

    try {
      chkp.setOutputFile(gaussianOutputFileTag, this.getOutputFile());
    }
    catch (Exception ex) {} /// !!!
  }

  public void setJobDescription(JobDescription job) throws Exception {

    // Stdout & stderr

    String stdout = getStdoutFileName();

    String stderr = getStderrFileName();

    if (stdout.length() > 0) {
      job.setStdout(stdout);
    }
    if (stderr.length() > 0) {
      job.setStderr(stderr);
    }

    job.setExecutable(getScriptFile());
    job.setLocalExecutable(true);
    job.setRemoteDirectory(this.getRemoteDirectory());
    job.setJobName(this.getJobName());
    job.setLocalDirectory(this.getLocalDirectory());

    // --- ( File Staging In )

    job.setFileStageIn(this.getScriptFile(), this.getScriptFile()); // Run script file
    job.setFileStageIn(this.getInputFile(), this.getInputFile()); // Data input file

// --- File staging out....

    job.setFileStageOut(this.getOutputFile(), this.getOutputFile());

  }

  private class ConfirmEditing_ActionAdapter
      implements ActionListener {

    ConfirmEditing_ActionAdapter() {
    }

    public void actionPerformed(ActionEvent actionEvent) {
      String text = scriptEditorFrame.getText();
      boolean valid = true;
      try {
        validateScript(text);
      }
      catch (NullPointerException ex) {
        scriptEditorFrame.setText(defaultScriptContent);
        return;
      }
      catch (IllegalArgumentException ex) {
        int n = JOptionPane.showConfirmDialog(null, "Script file does not contain line with " + gaussianExecutable + " and " +
                                              inputFilePattern + "\n" +
                                              "Do you still want to continue?", "Confirm (possibly) non-valid script",
                                              JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
          return;
        }
        valid = false;
      }

      scriptEditorFrame.setVisible(false);
      scriptContent = scriptEditorFrame.getText();
      if (valid) {
        prefs.put(scriptContentKey, scriptContent);
      }
    }
  }

  private class ScriptEditorFrame
      extends JFrame {
    JPanel contentPane;
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea jTextArea1 = new JTextArea();
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenuFile = new JMenu("File");
    public JMenuItem jMenuFileExit = new JMenuItem("Confirm editing & Return");

    public ScriptEditorFrame(ActionListener al) {
      contentPane = (JPanel) getContentPane();
      this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      setSize(new Dimension(400, 300));
      setTitle("Edit Script File");
      jTextArea1.setToolTipText("Edit script contents");
      jTextArea1.setEditable(true);
      contentPane.add(jScrollPane1, java.awt.BorderLayout.CENTER);
      jScrollPane1.getViewport().add(jTextArea1);

      jMenuFileExit.addActionListener(al);
      jMenuFile.add(jMenuFileExit);
      jMenuBar1.add(jMenuFile);
      setJMenuBar(jMenuBar1);
    }

    public void setText(String text) {
      jTextArea1.setText(text);
    }

    public String getText() {
      return jTextArea1.getText();
    }

  }

  public void executableButton_actionPerformed(ActionEvent e) {
    if (!executableButton.isEnabled()) {
      return;
    }

    if (remoteFileChooser == null) {
      return;
    }

    executableButton.setEnabled(false);
    try {
      remoteFileChooser.setFileChooserVisible(true);
      if (remoteFileChooser.getDirectory() != null) {
        executableButton.setEnabled(true);
        return;
      }
      else if (remoteFileChooser.getFile() != null) {
        String file = remoteFileChooser.getFile();
        executableTextField.setText(file);
        prefs.put(executableKey, file);
      }
      else if (remoteFileChooser.pwd() != null) {
        executableButton.setEnabled(true);
        return;
      }
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    executableButton.setEnabled(true);

  }

  private class SubmitGaussianPanel_localDirButton_actionAdapter
      implements ActionListener {
    private SubmitGaussianPanel adaptee;
    SubmitGaussianPanel_localDirButton_actionAdapter(SubmitGaussianPanel adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.localDirButton_actionPerformed(e);
    }
  }

  private class SubmitGaussianPanel_remoteDirButton_actionAdapter
      implements ActionListener {
    private SubmitGaussianPanel adaptee;
    SubmitGaussianPanel_remoteDirButton_actionAdapter(SubmitGaussianPanel adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.remoteDirButton_actionPerformed(e);
    }
  }

  private class SubmitGaussianPanel_executableButton_actionAdapter
      implements ActionListener {
    private SubmitGaussianPanel adaptee;
    SubmitGaussianPanel_executableButton_actionAdapter(SubmitGaussianPanel adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.executableButton_actionPerformed(e);
    }
  }

  private class SubmitGaussianPanel_InputCheckBox_itemAdapter
      implements ItemListener {
    private SubmitGaussianPanel adaptee;
    SubmitGaussianPanel_InputCheckBox_itemAdapter(SubmitGaussianPanel adaptee) {
      this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
      adaptee.InputCheckBox_itemStateChanged(e);
    }
  }

  private class SubmitGaussianPanel_StdoutCheckBox_itemAdapter
      implements ItemListener {
    private SubmitGaussianPanel adaptee;
    SubmitGaussianPanel_StdoutCheckBox_itemAdapter(SubmitGaussianPanel adaptee) {
      this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
      adaptee.StdoutCheckBox_itemStateChanged(e);
    }
  }

  private class SubmitGaussianPanel_editScriptButton_actionAdapter
      implements ActionListener {
    private SubmitGaussianPanel adaptee;
    SubmitGaussianPanel_editScriptButton_actionAdapter(SubmitGaussianPanel
        adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.editScriptButton_actionPerformed(e);
    }
  }

  private class SubmitGaussianPanel_ScriptButton_actionAdapter
      implements ActionListener {
    private SubmitGaussianPanel adaptee;
    SubmitGaussianPanel_ScriptButton_actionAdapter(SubmitGaussianPanel adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.ScriptButton_actionPerformed(e);
    }
  }

  private class SubmitGaussianPanel_RemoteDirTextField_actionAdapter
      implements ActionListener {
    private SubmitGaussianPanel adaptee;
    SubmitGaussianPanel_RemoteDirTextField_actionAdapter(SubmitGaussianPanel
        adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.RemoteDirTextField_actionPerformed(e);
    }
  }

}
