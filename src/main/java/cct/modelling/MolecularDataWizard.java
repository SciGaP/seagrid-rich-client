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
 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.modelling;

import cct.GlobalSettings;
import cct.config.FormatObject;
import cct.interfaces.MoleculeInterface;
import cct.tools.FileFilterImpl;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author vvv900
 */
public class MolecularDataWizard {

  private boolean useOpenFileDialog = true;
  private Component parent;
  private Object parserObject;
  private JFileChooser chooser;
  private FileFilterImpl filter;
  private File currentWorkingDirectory = null;
  static private Preferences prefs = null;
  static final Logger logger = Logger.getLogger(MolecularDataWizard.class.getCanonicalName());
  private MoleculeInterface reference;
  private List<MoleculeInterface> molecules = new ArrayList<MoleculeInterface>();
  private static Map<String, FormatObject> parsers;

  public MolecularDataWizard(MoleculeInterface mol) {
    this();
    reference = mol;
  }

  public MolecularDataWizard() {
    if (parsers == null) {
      parsers = GlobalSettings.getParsers();
    }
    if (prefs == null) {
      try {
        prefs = Preferences.userNodeForPackage(getClass());
      } catch (Exception ex) {
        logger.warning("Cannot get Preferences: " + ex.getMessage());
      }
    }
  }

  private void reset() {
    //molecule = reference.getInstance();
    molecules.clear();
  }

  /*
   * public void parseMolecularData(CHEMISTRY_FILE_FORMAT format) throws Exception { if (!useOpenFileDialog) { throw new
   * Exception("This method can be used only with setUseOpenFileDialog(true)"); } String filename = chooseFileDialog(format);
   * BufferedReader in = new BufferedReader(new FileReader(filename)); parseMolecularData(format, in); }
   *
   * public void parseMolecularData(CHEMISTRY_FILE_FORMAT format, String filename) throws Exception { BufferedReader in = new
   * BufferedReader(new FileReader(filename)); parseMolecularData(format, in); }
   *
   *
   * public void parseMolecularData(CHEMISTRY_FILE_FORMAT format, BufferedReader in) throws Exception { reset();
   *
   * switch (format) {
   *
   * case MOL2: TriposParser triposParser = new TriposParser(); parserObject = triposParser; triposParser.parseMol2File(in,
   * molecule); break;
   *
   * default: throw new Exception("Format " + format.toString() + " is not implemented yet!"); } }
   */
  public void parseMolecularData(String format) throws Exception {
    reset();
    if (!useOpenFileDialog) {
      throw new Exception("This method can be used only with setUseOpenFileDialog(true)");
    }
    GeneralMolecularDataParser parser = this.getParser(format);
    String filename = chooseFileDialog(parser);
    parseMolecularData(parser, filename);
    //BufferedReader in = new BufferedReader(new FileReader(filename));
    //parseMolecularData(format, in);
  }

  public void parseMolecularData(File file) throws Exception {
    List<FormatObject> ps = GlobalSettings.getParsersForFile(file);
    String format = ps.get(0).getName();
    //if (ps.size() > 1) {
    //  logger.warning("There are " + ps.size() + " parsers for file " + file.getName() + " Using the first one only...");
    //}

    //BufferedReader in = new BufferedReader(new FileReader(file));
    //if (!in.markSupported()) {
    //  System.err.println("A stream associated with " + file + " does not support marking");
    //}
    //in.mark(65536);
    int score = 0;
    for (FormatObject parser : ps) {

      //in.reset();
      if (parser.getParser() instanceof GeneralMolecularDataParser) {
        BufferedReader in = new BufferedReader(new FileReader(file));
        score = ((GeneralMolecularDataParser) parser.getParser()).validFormatScore(in);
        in.close();
        System.out.println("Testing format: " + ((GeneralMolecularDataParser) parser.getParser()).getName() + " Score: " + score);
        if (score == 10) {
          format = ((GeneralMolecularDataParser) parser.getParser()).getName();
          break;
        }
      } else {
        System.err.println("Parser of unknown class: "
            + (parser.getParser() == null ? "null" : parser.getParser().getClass().getCanonicalName())
            + " Expected class: " + GeneralMolecularDataParser.class.getCanonicalName());
      }
    }

    if (score == 0) {
      throw new Exception("File " + file.getAbsolutePath() + " is not recognized as a valid or implemented format");
    }

    System.out.println("Guessed format: " + format);
    BufferedReader in = new BufferedReader(new FileReader(file));
    parseMolecularData(format, in);
  }

  public void parseMolecularData(String format, String filename) throws Exception {
    reset();
    GeneralMolecularDataParser parser = this.getParser(format);
    parseMolecularData(parser, filename);
    //BufferedReader in = new BufferedReader(new FileReader(filename));
    //parseMolecularData(format, in);
  }

  public void parseMolecularData(String format, BufferedReader in) throws Exception {
    reset();
    parseMolecularData(getParser(format), in);
  }

  public GeneralMolecularDataParser getParser(String format) throws Exception {
    if (parsers == null || parsers.size() < 1) {
      throw new Exception("No parsers");
    }

    if (!parsers.containsKey(format.toUpperCase())) {
      throw new Exception("No parser for a format " + format);
    }

    FormatObject obj = parsers.get(format.toUpperCase());

    if (!(obj.getParser() instanceof GeneralMolecularDataParser)) {
      throw new Exception("Parser for a format " + format + " is not an instanceof " + GeneralMolecularDataParser.class.getCanonicalName());
    }

    GeneralMolecularDataParser parser = null;
    try {
      parser = (GeneralMolecularDataParser) obj.newParserInstance();
    } catch (Exception ex) {
      throw new Exception("Cannot get a new instance of " + obj.getClass().getCanonicalName() + " : " + ex.getMessage());
    }

    if (parser == null) {
      throw new Exception("Uanble to get a new instance of " + obj.getClass().getCanonicalName());
    }

    //try {
    //  parser = (GeneralMolecularDataParser) obj;
    //} catch (Exception ex) {
    //  throw new Exception("Cannot cast " + obj.getClass().getCanonicalName() + " to " + GeneralMolecularDataParser.class.getCanonicalName() + " : " + ex.getMessage());
    //}
    return parser;
  }

  public void parseMolecularData(GeneralMolecularDataParser parser, BufferedReader in) throws Exception {
    parser.parseData(in);
    parserObject = parser;
    molecules.addAll(parser.getMolecules());
  }

  public void parseMolecularData(GeneralMolecularDataParser parser, String filename) throws Exception {
    parser.parseData(filename);
    parserObject = parser;
    molecules.addAll(parser.getMolecules());
  }

  String chooseFileDialog(GeneralMolecularDataParser parser) {
    if (chooser == null) {
      chooser = new JFileChooser();
      chooser.setDialogTitle("Open File");
    }
    filter = new FileFilterImpl();
    String extensions = parser.getExtensions();
    String temp[] = extensions.split(";");
    for (int i = 0; i < temp.length; i++) {
      filter.addExtension(temp[i]);
    }
    filter.setDescription(parser.getDescription());
    chooser.setFileFilter(filter);
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);

    currentWorkingDirectory = GlobalSettings.getCurrentWorkingDirectory();
    if (currentWorkingDirectory != null) {
      chooser.setCurrentDirectory(currentWorkingDirectory);
    } else if (prefs != null) {
      String lastPWD = prefs.get(GlobalSettings.lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory() && currentWorkingDirectory.exists()) {
          chooser.setCurrentDirectory(currentWorkingDirectory);
        }
      }
    }

    int returnVal = chooser.showOpenDialog(parent);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String fileName = chooser.getSelectedFile().getPath();
      currentWorkingDirectory = chooser.getCurrentDirectory();
      GlobalSettings.setCurrentWorkingDirectory(currentWorkingDirectory);
      if (prefs != null) {
        try {
          prefs.put(GlobalSettings.lastPWDKey, currentWorkingDirectory.getAbsolutePath());
        } catch (Exception ex) {
          System.err.println("Cannot save cwd: " + ex.getMessage() + " Ignored...");
        }
      }
      logger.info("You chose to open this file: " + fileName);
      return fileName;
    }
    return null;
  }

  public boolean isUseOpenFileDialog() {
    return useOpenFileDialog;
  }

  public void setUseOpenFileDialog(boolean useOpenFileDialog) {
    this.useOpenFileDialog = useOpenFileDialog;
  }

  public Component getParent() {
    return parent;
  }

  public void setParent(Component parent) {
    this.parent = parent;
  }

  public MoleculeInterface getMolecule() {
    return molecules.get(0);
  }

  public int getNumberMolecules() {
    return molecules.size();
  }

  public Object getParserObject() {
    return parserObject;
  }

  public static void main(String[] args) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    int result = fileChooser.showOpenDialog(null);

    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }

    //File[] files = fileChooser.getSelectedFiles();
    File file = fileChooser.getSelectedFile();
    if (file == null) {
      System.err.println("No files selected");
      return;
    }

    System.out.println("Selected file: " + file.getAbsolutePath());

    //System.out.println(files.length + " files selected");
    //FilesComparator fc = new FilesComparator();
    //Arrays.sort(files, fc);
    //
    File currentDir = fileChooser.getCurrentDirectory();
    System.out.println("Current working directory: " + currentDir.getAbsolutePath());
    System.setProperty("user.dir", fileChooser.getCurrentDirectory().getAbsolutePath());

    // ---
    MoleculeInterface mol = new Molecule();
    MolecularDataWizard mdw = new MolecularDataWizard(mol);
    try {
      mdw.parseMolecularData(file);
    } catch (Exception ex) {
      Logger.getLogger(MolecularDataWizard.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
