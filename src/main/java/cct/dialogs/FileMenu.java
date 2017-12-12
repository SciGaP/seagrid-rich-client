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

package cct.dialogs;

import bsh.Interpreter;
import cct.GlobalSettings;
import cct.adf.ADFOutput;
import cct.amber.AmberLib;
import cct.amber.AmberPrep;
import cct.amber.ReadPrmtopDialog;
import cct.chart.ChartFrame;
import cct.chart.VibrationSpectrumChart;
import cct.cpmd.CPMD;
import cct.cprocessor.CommandProcessor;
import cct.cprocessor.CommandProcessorDeprecated;
import cct.cprocessor.MolProcessorInterface;
import cct.gaussian.*;
import cct.gaussian.ui.GaussianInputEditorFrame;
import cct.gaussian.ui.SaveG03GJFDialog;
import cct.grid.ScriptSubmitterDialogInterface;
import cct.gromacs.GromacsParserFactory;
import cct.gulp.Gulp;
import cct.interfaces.*;
import cct.j3d.*;
import cct.mdl.MDLMol;
import cct.modelling.*;
import cct.modelling.ui.VibrationsControlFrame;
import cct.mopac.Mopac;
import cct.mopac.MopacOutput;
import cct.oogl.OOGL;
import cct.oogl.java3d.OoglJava3d;
import cct.pdb.PDB;
import cct.pdb.PDBBankFetcherInterface;
import cct.pdb.ui.PDBFormFrame;
import cct.qchem.QChem;
import cct.qchem.QChemOutput;
import cct.siesta.SiestaInput;
import cct.siesta.ui.SaveSiestaInputDialog;
import cct.tools.*;
import cct.tools.filebrowser.SFTPFileChooserDialog;
import cct.tools.filebrowser.ShadowClientInterface;
import cct.tools.ui.*;
import cct.tripos.TriposParser;
import cct.vasp.VasprunXML;
import cct.vasp.ui.ReadPoscarDialog;
import g03input.G03MenuTree;
import g03input.showMolEditor;
import legacy.editor.commons.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 *
 * @author vvv900
 */
public class FileMenu extends JMenu implements ActionListener, ShadowClientInterface, PDBBankFetcherInterface {

  //private static String lastPWDKey = "lastPWD";
  private static String REDIRECT_STDERR = "redirectStderr";
  private static String ERROR_MESSAGE = "Error";
  private static String OPEN_MOLECULE_FILE = "Open Molecule File";
  private static String OPENAS = "OpenAs";
  private ImageIcon saveFileImage = new ImageIcon(cct.dialogs.JamberooFrame.class.getResource("/cct/images/disk_blue.png"));
  private ImageIcon seagridFileImage = new ImageIcon(cct.dialogs.JamberooFrame.class.getResource("/cct/images/seagrid.png"));
  private JMenuItem jMenuOpenMolecule = new JMenuItem();
  private JMenuItem jMenuAppendMolecule = new JMenuItem();
  private JMenu jSubmenuFileOpen = new JMenu();
  private JMenu jSubmenuFileSave = new JMenu();
  private JMenu jSubmenuSEAGridExport = new JMenu();
  private JMenuItem jSEAGridGaussian = new JMenuItem();
  private JMenu gaussianMenu = new JMenu("Gaussian");
  private JMenu mopacMenu = new JMenu("Mopac");
  private JMenu submenuLoadRemote = new JMenu();
  private JMenuItem jExecuteScript = new JMenuItem();
  private Java3dUniverse java3dUniverse;
  private JFileChooser clobalChooser = null;
  private File currentWorkingDirectory = null;
  private Preferences prefs = null;
  private CommandProcessorDeprecated commandProcessor = null;
  private VibrationsControlFrame vcFrame = null;
  private ReadPrmtopDialog prmtopDialog = null;
  private MoleculeSelectorDialog moleculeSelectorDialog = null;
  private ReadPoscarDialog vaspDialog = null;
  private JMenuItem fetchStructure = new JMenuItem("Fetch from PDB bank");
  private Map<String, SFTPFileChooserDialog> sftpChoosers = new HashMap<String, SFTPFileChooserDialog>();
  private SFTPFileChooserDialog sftpChooserDialog = null;
  private Map<String, CHEMISTRY_FILE_FORMAT> remoteFileFormats = new HashMap<String, CHEMISTRY_FILE_FORMAT>();
  private Map<FileBrowserInterface, SFTPFileChooserDialog> ftpChoosers = new HashMap<FileBrowserInterface, SFTPFileChooserDialog>();
  private JMenu loadObjectMenu = new JMenu("Load Graphics Object");
  private SaveG03GJFDialog saveG03GJFDialog = null;
  private SaveSiestaInputDialog saveSiestaInputDialog = null;
  private SaveXYZDialog saveXYZDialog = null;
  private JMenu jSubmenuImageSave = new JMenu();
  private JMenu jSubmenuMoleculeSubmit = new JMenu();
  private JMenu jSubmenuScriptSubmit = new JMenu();
  private GaussianInputEditorFrame gaussianInputEditorFrame = null;
  private JMenuItem submitGaussianJob = new JMenuItem();
  private FileChooserDialog gaussianFileChooserDialog = null;
  private static String lastSaveGaussianInputDirKey = "LastSaveGaussianInputDir";
  private JMenuItem errLog = new JMenuItem("Errors Log");
  private LogPanel strerrLogPanel = new LogPanel("Errors Log");
  private boolean redirectStderr = false;
  static final Logger logger = Logger.getLogger(FileMenu.class.getCanonicalName());
  private PrintStream storeStderr = null;
  private PDBFormFrame pdbFormFrame = null;
  private saveImageAs_actionAdapter sia;
  private JamberooCoreInterface jamberooCore;
  private MolecularDataWizard wizard;
  private Map<Object, CHEMISTRY_FILE_FORMAT> sourceRef = new HashMap<Object, CHEMISTRY_FILE_FORMAT>();
  private Gaussian gaussianData = null;
  private Set<MoleculeChangeListener> moleculeChangeListeners = new LinkedHashSet<MoleculeChangeListener>();

  private static ResourceBundle messages;

  public FileMenu(JamberooCoreInterface jamberooCore) throws Exception {
    super();
    this.jamberooCore = jamberooCore;
    java3dUniverse = jamberooCore.getJamberooRenderer();
    wizard = new MolecularDataWizard();

    try {
      prefs = Preferences.userNodeForPackage(getClass());
    } catch (Exception ex) {
      logger.warning("Cannot get Preferences: " + ex.getMessage());
    }

    try {
      messages = GlobalSettings.getResourceBundle();
    } catch (Exception ex) {
      logger.warning("Cannot get ResourceBundle: " + ex.getMessage());
    }

    if (messages != null) {
      ERROR_MESSAGE = messages.getString("error");
      OPEN_MOLECULE_FILE = messages.getString("open_molecule_file");
      gaussianMenu.setText(messages.getString("gaussian"));
      mopacMenu.setText(messages.getString("mopac"));
      fetchStructure.setText(messages.getString("fetch_from_pdb"));
      loadObjectMenu.setText(messages.getString("load_graphics_object"));
      errLog.setText(messages.getString("errors_log"));
      strerrLogPanel.setText(messages.getString("errors_log"));
    }

    createMenu();
  }

  public void addMoleculeChangeListener(MoleculeChangeListener listener) {
    moleculeChangeListeners.add(listener);
  }

  public void removeMoleculeChangeListener(MoleculeChangeListener listener) {
    moleculeChangeListeners.remove(listener);
  }

  public void removeAllMoleculeChangeListeners() {
    moleculeChangeListeners.removeAll(moleculeChangeListeners);
  }

  private void createMenu() {

    String className = this.getClass().getCanonicalName();

    try {
      //custom = JamberooFrame.class.getClassLoader().getResource(CCT_PROPERTY_FILE);
      Properties props = GlobalSettings.getProperties();
      //props.load(custom.openStream());

      //--- Whether redirect stderr ?...
      String customCommand = props.getProperty(className + GlobalSettings.DIVIDER
          + REDIRECT_STDERR, String.valueOf(redirectStderr));
      if (customCommand != null) {
        try {
          redirectStderr = Boolean.parseBoolean(customCommand);
          logger.info("Stderr redirection: " + redirectStderr);
        } catch (Exception exx) {
          logger.warning("Error parsing " + className + GlobalSettings.DIVIDER + REDIRECT_STDERR + " value from " + GlobalSettings.CCT_PROPERTY_FILE
              + " file. Set to " + redirectStderr);
        }
      }
    } catch (Exception ex) {
      logger.warning("Cannot get " + className + GlobalSettings.DIVIDER + REDIRECT_STDERR + " value from the " + GlobalSettings.CCT_PROPERTY_FILE
          + " file. Set to " + redirectStderr);
    }

    // --- Setting up errors log panel...
    strerrLogPanel.getTextArea().setText("");
    strerrLogPanel.setIconImage(GlobalSettings.ICON_16x16_ERROR.getImage());
    strerrLogPanel.setSize(640, 480);
    strerrLogPanel.setLocationRelativeTo(this);
    TextToPost poster = new TextToPost(strerrLogPanel, "Post a Problem", false);
    JButton button = new JButton("Post Error Message");
    button.addActionListener(poster);
    strerrLogPanel.addButton(button);
    strerrLogPanel.popupOnUpdate(true);
    storeStderr = System.err; // Store stderr...

    // --- Redirect stderr
    if (redirectStderr) {
      JTextAreaOutputStream stderrSink = new JTextAreaOutputStream(strerrLogPanel.getTextArea());
      System.setErr(new PrintStream(stderrSink));
    }

    jMenuOpenMolecule.setText(OPEN_MOLECULE_FILE);
    jMenuOpenMolecule.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    jMenuOpenMolecule.setIcon(GlobalSettings.ICON_16x16_OPEN_FILE);
    jMenuOpenMolecule.addActionListener(this);

    jMenuAppendMolecule.setText(messages != null ? messages.getString("append_molecule_file") : "Append Molecule File");
    jMenuAppendMolecule.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    jMenuAppendMolecule.setIcon(GlobalSettings.ICON_16x16_OPEN_FILE);
    jMenuAppendMolecule.addActionListener(this);

    jSubmenuFileOpen.setText(messages != null ? messages.getString("open_as") : "Open As");
    jSubmenuFileOpen.setIcon(GlobalSettings.ICON_16x16_OPEN_FILE);
    jSubmenuFileSave.setText(messages != null ? messages.getString("save_as") : "Save As");
    jSubmenuFileSave.setIcon(saveFileImage);

    // -- SEAGrid Export
    jSubmenuSEAGridExport.setText(messages != null ? messages.getString("seagrid_export") : "SEAGrid Export");
    jSubmenuSEAGridExport.setIcon(seagridFileImage);
    String seagridGaussianText = messages != null ? messages.getString("seagrid_gaussian_export") : "Gaussian";
    jSEAGridGaussian.setText(seagridGaussianText);
    jSEAGridGaussian.setIcon(new ImageIcon(cct.dialogs.JamberooFrame.class.getResource("/cct/images/gaussian-16x16.png")));
    jSubmenuSEAGridExport.add(jSEAGridGaussian);
    jSEAGridGaussian.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        String arg = actionEvent.getActionCommand();

        if (java3dUniverse.getMolecule() == null
                || java3dUniverse.getMolecule().getNumberOfAtoms() < 1) {
          JOptionPane.showMessageDialog(null, "Load Molecule first!",
                  ERROR_MESSAGE,
                  JOptionPane.ERROR_MESSAGE);
          return;
        }

        if(arg.equals(seagridGaussianText)){
          Gaussian g = new Gaussian();
          try {
            String gaussOut = Gaussian.getMoleculeSpecsAsString(java3dUniverse.getMolecule());
            File f = new File(Settings.getApplicationDataDir() + Settings.fileSeparator
                    + "tmp.txt");
            FileWriter fw = new FileWriter(f, false);
            fw.write(gaussOut);
            System.err.println("gaussOut = ");
            System.err.println(gaussOut);
            fw.close();

            G03MenuTree.showG03MenuTree();
            showMolEditor forString = new showMolEditor();
            forString.tempmol = gaussOut;

            JOptionPane.showMessageDialog(null, "WARNING: Molecule information" +
                            " has been exported correctly. Make sure\n" +
                            "to edit other sections of GUI.",
                    "GridChem: Gaussian GUI",
                    JOptionPane.WARNING_MESSAGE);
          } catch (Exception e) {
            e.printStackTrace();
          }

        }
      }
    });

    // --- "Open As" Submenu
    gaussianMenu.setIcon(GlobalSettings.ICON_16x16_GAUSSIAN);
    mopacMenu.setIcon(GlobalSettings.ICON_16x16_MOPAC);

    //JEditorFrame_jMenuFileOpenAs_ActionAdapter processFileMenu = new JEditorFrame_jMenuFileOpenAs_ActionAdapter(this);
    Map readF = MolecularFileFormats.getReadCoordFormats();
    Set set = readF.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String formatName = me.getKey().toString();
      JMenuItem jMenuItem;
      if (formatName.equals(MolecularFileFormats.gaussian03GJF)
          || formatName.equals(MolecularFileFormats.gaussian03Output)
          || formatName.equals(MolecularFileFormats.gaussianFragment)
          || formatName.equals(MolecularFileFormats.gaussian03Cube)
          || formatName.equals(MolecularFileFormats.gaussianTrajectory)
          || formatName.equals(MolecularFileFormats.gaussianFormCheckpoint)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_GAUSSIAN);
        //sourceRef.put(jMenuItem, CHEMISTRY_FILE_FORMAT.PDB)

        if (!jSubmenuFileOpen.isMenuComponent(gaussianMenu)) {
          jSubmenuFileOpen.add(gaussianMenu);
        }
        gaussianMenu.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.gamessInput)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_GAMESS);
        jMenuItem.setVisible(true);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.gamessOutput)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_GAMESS);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.mopacInput)
          || formatName.equals(MolecularFileFormats.mopacLog)
          || formatName.equals(MolecularFileFormats.mopacOutput)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_MOPAC);
        if (!jSubmenuFileOpen.isMenuComponent(mopacMenu)) {
          jSubmenuFileOpen.add(mopacMenu);
        }
        mopacMenu.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.adfInput)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_ADF);
        jMenuItem.setVisible(true);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.adfOutput)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_ADF);
        jMenuItem.setVisible(true);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.siestaInputFormat)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_SIESTA);
        jMenuItem.setVisible(true);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.triposMol2)) {
        jMenuItem = new JMenuItem(formatName,
            new ImageIcon(cct.dialogs.JamberooFrame.class.getResource("/cct/images/tripos-transp-16x16.png")));
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.pdbFile)) {
        jMenuItem = new JMenuItem(formatName,
            new ImageIcon(cct.dialogs.JamberooFrame.class.getResource("/cct/images/pdb-transp-16x16.png")));
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.mdlMolfileFormat)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_MDL);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.gromacsGROFormat)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_GROMACS);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.vaspPoscarFormat)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_VASP);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.vaspVasprunFormat)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_VASP);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.qchemFormat)
          || formatName.equals(MolecularFileFormats.qchemOutputFormat)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_QCHEM);
        jSubmenuFileOpen.add(jMenuItem);
      } else if (formatName.equals(MolecularFileFormats.gulpInputFormat)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_GULP);
        jSubmenuFileOpen.add(jMenuItem);
      } else {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_MOLECULE);
        jSubmenuFileOpen.add(jMenuItem);
      }
      //jMenuItem.addActionListener(processFileMenu);
      jMenuItem.addActionListener(this);
      jMenuItem.setName(OPENAS);
    }

    // --- Load from sftp host
    jamberooCore.getShadowSFTPManager().addClient(this);
    submenuLoadRemote.setText(messages != null ? messages.getString("load_from_sftp") : "Load from sftp server");
    submenuLoadRemote.setIcon(GlobalSettings.ICON_16x16_REMOTE_DOWNLOAD);
    submenuLoadRemote.setVisible(true);

    // --- Fetch
    fetchStructure.setIcon(GlobalSettings.ICON_16x16_REMOTE_DOWNLOAD);
    fetchStructure.addActionListener(this);

    // --- Load Object Submenu
    JMenuItem jMenuItem = new JMenuItem("off File");
    jMenuItem.setName(GraphicsFormat.OOGL_OFF_FORMAT.name());
    jMenuItem.addActionListener(this);
    loadObjectMenu.add(jMenuItem);
    loadObjectMenu.setIcon(GlobalSettings.ICON_16x16_ADD_COMPONENT);

    // --- "Save As" Submenu
    JEditorFrame_jMenuFileSaveAs_ActionAdapter processSaveFileMenu = new JEditorFrame_jMenuFileSaveAs_ActionAdapter(this);
    set = MolecularFileFormats.getSaveCoordFormats().entrySet();
    iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String formatName = me.getKey().toString();
      if (formatName.equals(MolecularFileFormats.gaussian03GJF)) {
        jMenuItem = new JMenuItem(formatName, new ImageIcon(cct.dialogs.JamberooFrame.class.getResource("/cct/images/gaussian-16x16.png")));
      } else if (formatName.equals(MolecularFileFormats.triposMol2)) {
        jMenuItem = new JMenuItem(formatName,
            new ImageIcon(cct.dialogs.JamberooFrame.class.getResource("/cct/images/tripos-transp-16x16.png")));
      } else if (formatName.equals(MolecularFileFormats.pdbFile)) {
        jMenuItem = new JMenuItem(formatName,
            new ImageIcon(cct.dialogs.JamberooFrame.class.getResource("/cct/images/pdb-transp-16x16.png")));
      } else if (formatName.equals(MolecularFileFormats.povrayFormat)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_POVRAY);
      } else if (formatName.equals(MolecularFileFormats.siestaInputFormat)) {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_SIESTA);
      } else {
        jMenuItem = new JMenuItem(formatName, GlobalSettings.ICON_16x16_MOLECULE);
      }

      jMenuItem.addActionListener(processSaveFileMenu);
      jSubmenuFileSave.add(jMenuItem);
    }

    // --- Save Image as submenu
    sia = new saveImageAs_actionAdapter(this);
    String formats[] = ImageTools.getSaveImageFormatNames();
    for (int i = 0; i < formats.length; i++) {
      jMenuItem = new JMenuItem(formats[i].toUpperCase());
      jMenuItem.setIcon(FileUtilities.getIcon16x16("." + formats[i]));
      jMenuItem.addActionListener(sia);
      jSubmenuImageSave.add(jMenuItem);
    }
    jSubmenuImageSave.setText(messages != null ? messages.getString("save_image_as") : "Save Image As");
    jSubmenuImageSave.setIcon(GlobalSettings.ICON_16x16_IMAGE_FILE);

    boolean executeScript = true; // !!! Development option
    if (executeScript) {
      jExecuteScript.setText("Execute Script (in development)");
      jExecuteScript.setIcon(GlobalSettings.ICON_16x16_GEAR);
      jExecuteScript.addActionListener(this);
    }

    addSeparator();

    add(jMenuOpenMolecule);
    add(jSubmenuFileOpen);
    add(jMenuAppendMolecule);
    add(submenuLoadRemote);
    add(fetchStructure);
    add(loadObjectMenu);
    addSeparator();
    add(jSubmenuFileSave);
    add(jSubmenuImageSave);
    addSeparator();

    add(jSubmenuSEAGridExport);
    addSeparator();

    if (executeScript) {
      add(jExecuteScript);
      addSeparator();
    }

    List<String> submitters = GridProviders.getAvailableScriptSubmitters();
    if (submitters != null && submitters.size() > 0) {
      jSubmenuMoleculeSubmit.setText("Submit Molecule");
      jSubmenuMoleculeSubmit.setIcon(GlobalSettings.ICON_16x16_SERVER_INTO);
      submitMolecule_actionAdapter sm = new submitMolecule_actionAdapter(this);
      for (int i = 0; i < submitters.size(); i++) {
        jMenuItem = new JMenuItem(submitters.get(i));
        jMenuItem.addActionListener(sm);
        jSubmenuMoleculeSubmit.add(jMenuItem);
      }
      add(jSubmenuMoleculeSubmit);

      jSubmenuScriptSubmit.setText("Submit Existing Input File");
      jSubmenuScriptSubmit.setIcon(GlobalSettings.ICON_16x16_SERVER_INTO);
      for (int i = 0; i < submitters.size(); i++) {
        jMenuItem = new JMenuItem(submitters.get(i));
        GridProviders.addActionListener(jMenuItem, GridProviders.SCRIPT_SUBMITTER_ARG);
        jMenuItem.addActionListener(GridProviders.getActionListener());
        jSubmenuScriptSubmit.add(jMenuItem);
      }
      add(jSubmenuScriptSubmit);
    }

    addSeparator();

    // --- Errors log menuitem
    errLog.setIcon(GlobalSettings.ICON_16x16_DOCUMENT_WARNING);
    errLog.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        strerrLogPanel.setVisible(true);
      }
    });
    add(errLog);

    if (redirectStderr) {
      errLog.setVisible(true);
    } else {
      errLog.setVisible(false);
    }
  }

  public JButton getSnapshotButton() {
    JButton snapshotButton = new JButton();

    snapshotButton.setEnabled(true);
    snapshotButton.setToolTipText(messages != null ? messages.getString("save_image") : "Save Image");
    snapshotButton.setActionCommand(null);
    snapshotButton.addActionListener(sia);
    snapshotButton.setIcon(new ImageIcon(cct.resources.Resources.class.getResource(
            "/cct/images/icons16x16/camera.png")));
    return snapshotButton;
  }

  public void fetchStructure(String host, String entry_directory, String code) {

    String slash = "/";
    if (entry_directory.endsWith("/")) {
      slash = "";
    }
    // Now rcsb uses gz instead of Z
    String url_address = "ftp://" + host + entry_directory + slash + "pdb/"
        + code.substring(1, 3) + "/pdb" + code + ".ent.gz";
    logger.info("Pdb entry: " + url_address);
    MoleculeInterface mol = new Molecule();
    try {
      URL url = new URL(url_address);
      mol = PDB.parsePDBFile(url, mol);
      this.java3dUniverse.addMolecule(mol);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
    }
  }

  public void actionPerformed(ActionEvent actionEvent) {

    Object source = actionEvent.getSource();
    String arg = actionEvent.getActionCommand();
    String eventName = "";
    boolean openAsMenuItem = false;
    if (source instanceof JMenuItem) {
      JMenuItem jMenuItem = (JMenuItem) source;
      eventName = jMenuItem.getName();
      if (eventName != null) {
        openAsMenuItem = eventName.equals(OPENAS);
      }
    }

    // --- Open Molecule Menuitem
    if (openAsMenuItem) {
      try {
        openFile(java3dUniverse, arg);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
      }
    } else if (actionEvent.getSource() == jMenuOpenMolecule) {
      openMoleculeFile(OPEN_MOLECULE_FILE, this.java3dUniverse, GlobalSettings.ADD_MOLECULE_MODE.SET);
    } else if (actionEvent.getSource() == jMenuAppendMolecule) {
      openMoleculeFile(OPEN_MOLECULE_FILE, this.java3dUniverse, GlobalSettings.ADD_MOLECULE_MODE.APPEND);
    } else if (actionEvent.getSource() == fetchStructure) {
      if (pdbFormFrame == null) {
        pdbFormFrame = new PDBFormFrame(this);
        pdbFormFrame.pack();
        pdbFormFrame.setLocationRelativeTo(this);
      }
      pdbFormFrame.setVisible(true);
    } else if (actionEvent.getSource() == jExecuteScript) {

      FileDialog fd = new FileDialog(new Frame(), "Open Script File", FileDialog.LOAD);
      fd.setFile("*.txt;*.scr");
      fd.setVisible(true);
      if (fd.getFile() == null) {
        return;
      }

      try {
        Interpreter i = new Interpreter();  // Construct an interpreter
        i.source(fd.getDirectory() + fd.getFile());
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error Executing Script : " + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (false) {
        try {
          CommandProcessor cp = new CommandProcessor();
          cp.setRenderer(java3dUniverse);
          cp.parseScript(fd.getDirectory() + fd.getFile(), 0);
          cp.executeScript();
        } catch (Exception ex) {
          Logger.getLogger(FileMenu.class.getName()).log(Level.SEVERE, null, ex);
          JOptionPane.showMessageDialog(this, "Error Executing Script : " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
      if (false) {
        MolProcessorInterface mpi = new ScriptProcessorWrapper(java3dUniverse);
        commandProcessor = new CommandProcessorDeprecated(mpi);
        try {
          commandProcessor.parseScript(fd.getDirectory() + fd.getFile(), 0);
          commandProcessor.executeScript();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error Executing Script : " + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
      // ----
    } else if (eventName.equals(GraphicsFormat.OOGL_OFF_FORMAT.name())) {
      FileDialog fd = new FileDialog(new Frame(), "Open OOGL File", FileDialog.LOAD);
      fd.setFile("*.off;*.ogl");
      fd.setVisible(true);
      if (fd.getFile() == null) {
        return;
      }

      OOGL oogl = new OOGL();
      try {
        oogl.parseOFF(fd.getDirectory() + fd.getFile());
        java3dUniverse.addGraphics(OoglJava3d.getGraphicsObject(oogl));
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error Opening off file: " + ex.getMessage(),
            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        return;
      }

    } else if (gaussianInputEditorFrame != null && actionEvent.getSource() == gaussianInputEditorFrame.returnBackButton) {
      gaussianInputEditorFrame.setVisible(false);
      int n = gaussianInputEditorFrame.getSelectedStep();
      MoleculeInterface m = Molecule.getNewInstance();
      m = gaussianData.getMolecule(m, n);
      Molecule.guessCovalentBonds(m);
      //MoleculeInterface m2 = Molecule.divideIntoMolecules(m);
      java3dUniverse.addMolecule(m);
    }

  }

  /**
   * Enables/disables redirection of stderr.
   *
   * @param enable boolean - if true, redirects sdterr to the errors log panel and adds "Errors Log" menu item to the
   * File menu. if false, remove menu item from the File menu and directs sdterr to the standard stream
   */
  public void interceptStderr(final boolean enable) {
    if (redirectStderr && enable) {
      return;
    } else if (!redirectStderr && !enable) {
      return;
    } else if (enable && !redirectStderr) {
      final JTextAreaOutputStream stderrSink = new JTextAreaOutputStream(
          strerrLogPanel.getTextArea());
      System.setErr(new PrintStream(stderrSink));
      errLog.setVisible(true);
    } else if (!enable && redirectStderr) {
      System.setErr(storeStderr);
      errLog.setVisible(false);
    }

  }

  public void openMoleculeFile(String title, Java3dUniverse j3d, GlobalSettings.ADD_MOLECULE_MODE add_mode) {

    if (clobalChooser == null) {
      clobalChooser = new JFileChooser();
      clobalChooser.setAcceptAllFileFilterUsed(false);
      clobalChooser.setDialogTitle(title);
      clobalChooser.setDialogType(JFileChooser.OPEN_DIALOG);
      clobalChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      clobalChooser.setMultiSelectionEnabled(false);

      // --- First, add filter for all molecular formats
      Map allFormats = MolecularFileFormats.getReadCoordFormats();

      FileFilterImpl filter = new FileFilterImpl();
      filter.setDescription("All Molecular Formats");

      Set set = allFormats.entrySet();
      Iterator iter = set.iterator();
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        String description = me.getKey().toString();
        String ext = me.getValue().toString();

        String temp[] = ext.split(";");
        for (int i = 0; i < temp.length; i++) {
          filter.addExtension(temp[i]);
        }
      }

      clobalChooser.addChoosableFileFilter(filter);

      // --- Add separate filters
      javax.swing.filechooser.FileFilter[] filters = FileFilterImpl.getFileFilters(MolecularFileFormats.getReadCoordFormats());

      for (int i = 0; i < filters.length; i++) {
        clobalChooser.addChoosableFileFilter(filters[i]);
        logger.info("Adding filter: " + filters[i].getDescription());
      }

      // --- Set default filter
      clobalChooser.setFileFilter(filter);
    }

    currentWorkingDirectory = GlobalSettings.getCurrentWorkingDirectory();
    if (currentWorkingDirectory != null) {
      clobalChooser.setCurrentDirectory(currentWorkingDirectory);
    } else if (prefs != null) {
      String lastPWD = prefs.get(GlobalSettings.lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory()
            && currentWorkingDirectory.exists()) {
          clobalChooser.setCurrentDirectory(currentWorkingDirectory);
        }
      }
    } else {
      logger.warning("Cannot get currentWorkingDirectory: prefs == null");
    }

    // --- Show dialog
    int returnVal = JFileChooser.CANCEL_OPTION;

    returnVal = clobalChooser.showOpenDialog(this);

    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }

    String fileName = null;

    fileName = clobalChooser.getSelectedFile().getPath();
    currentWorkingDirectory = clobalChooser.getCurrentDirectory();
    GlobalSettings.setCurrentWorkingDirectory(currentWorkingDirectory);
    try {
      prefs.put(GlobalSettings.lastPWDKey, currentWorkingDirectory.getAbsolutePath());
    } catch (Exception ex) {
      System.err.println("Cannot save cwd: " + ex.getMessage() + " Ignored...");
    }
    logger.info("You chose to open this file: " + fileName);

    // --- Open File
    int fileFormat = MolecularFileFormats.formatUnknown;
    javax.swing.filechooser.FileFilter selectedFilter = clobalChooser.getFileFilter();

    javax.swing.filechooser.FileFilter[] allFilters = clobalChooser.getChoosableFileFilters();

    if (selectedFilter == allFilters[0]) {
      String extension = FileFilterImpl.getExtension(fileName);
      fileFormat = MolecularFileFormats.guessMolecularFileFormat(fileName, extension);
      if (fileFormat == MolecularFileFormats.formatUnknown) {
        JOptionPane.showMessageDialog(this, "Unknown Molecular Modeling File Format: " + extension, ERROR_MESSAGE,
            JOptionPane.ERROR_MESSAGE);
        return;
      }
    } else if (selectedFilter instanceof FileFilterImpl) {
      FileFilterImpl fltr = (FileFilterImpl) selectedFilter;
      fileFormat = MolecularFileFormats.getReadFileType(fltr.getDescription());
      if (fileFormat == MolecularFileFormats.formatUnknown) {
        JOptionPane.showMessageDialog(this, "Unknown Molecular Modeling File Format: " + fltr.getDescription(),
            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        return;
      }
    } else {
      String descr = selectedFilter.getDescription();
      JOptionPane.showMessageDialog(this, "Cannot open file of type: " + descr, ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
      return;
    }

    //j3d.openMolecularModelingFile(fileFormat, fileName, add_mode);
    openMolecularModelingFile(j3d, fileFormat, fileName, add_mode);
  }

  protected void sendMoleculeChangedEvent(String fileName, MoleculeEventObject.MOLECULE_EVENT moleculeEvent) {
    MoleculeEventObject event = new MoleculeEventObject(this, moleculeEvent, fileName);
    for (MoleculeChangeListener listener : moleculeChangeListeners) {
      listener.moleculeChanged(event);
    }
  }

  public boolean openMolecularModelingFile(Java3dUniverse j3d, int fileType, String fileName,
      GlobalSettings.ADD_MOLECULE_MODE read_mode) {
    if (fileType == MolecularFileFormats.formatG03_GJF) { // G03 Input file

      if (gaussianInputEditorFrame != null && gaussianInputEditorFrame.isVisible()) {
        gaussianInputEditorFrame.setVisible(false);
      }

      gaussianData = new Gaussian();
      int n = gaussianData.parseGJF(fileName, 0);
      logger.info("Number of molecules: " + n);
      if (n > 1) {
        if (gaussianInputEditorFrame == null) {
          gaussianData.setGraphicsRenderer(j3d);
          gaussianInputEditorFrame = new GaussianInputEditorFrame(gaussianData);
          gaussianInputEditorFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
          try {
            gaussianInputEditorFrame.removeMenuItem("File", "Exit");
          } catch (Exception ex) {
          }
          JMenuItem item = new JMenuItem("Select current step & return back to Editor");

          item.addActionListener(this);
          gaussianInputEditorFrame.addMenuItem("File", item);
          gaussianInputEditorFrame.returnBackButton.setVisible(true);
          gaussianInputEditorFrame.returnBackButton.addActionListener(this);
        }
        gaussianInputEditorFrame.setupEditor(gaussianData);
        JOptionPane.showMessageDialog(null, "Gaussian Input file has more than 1 step\n" + "Select step in the next dialog",
            "Info", JOptionPane.INFORMATION_MESSAGE);

        gaussianInputEditorFrame.setVisible(true);
        //logger.info("Loading 1st Molecule...");
      } else {
        MoleculeInterface m = Molecule.getNewInstance();
        m = gaussianData.getMolecule(m, 0);
        if (m == null || m.getNumberOfAtoms() < 1) {
          JOptionPane.showMessageDialog(null, "Didn't find atoms in file", "Warning", JOptionPane.WARNING_MESSAGE);
          return false;
        }
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        if (GlobalSettings.ADD_MOLECULE_MODE.SET == read_mode) {
          sendMoleculeChangedEvent(fileName, MoleculeEventObject.MOLECULE_EVENT.MOLECULE_DELETED);
        }
        java3dUniverse.setMolecule(m, read_mode);
        sendMoleculeChangedEvent(fileName, MoleculeEventObject.MOLECULE_EVENT.MOLECULE_LOADED);
      }
      //addGaussianData(g);

    } else if (fileType == MolecularFileFormats.formatG03_Output) { // G03 output file
      MoleculeInterface m = Molecule.getNewInstance();
      GaussianOutput gaussianOutput = new GaussianOutput();
      GaussianJob results;
      try {
        results = gaussianOutput.parseFile(m, fileName, false);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Cannot open file " + fileName + ": " + ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
      }
      m = results.getMolecule(0);
      List<MolecularGeometry> Geometries = results.getGeometries(0);
      if (Geometries == null || Geometries.size() == 0) {
        JOptionPane.showMessageDialog(null, "No molecule found in file", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }

      Frame parent = new Frame();

      JShowText showResume = new JShowText("Gaussian Results");
      showResume.setSize(600, 640);
      showResume.setTitle("Gaussian Results: " + fileName);
      showResume.setLocationByPlatform(true);
      showResume.setText(gaussianOutput.getOutputResume());
      showResume.setVisible(true);

      if (Geometries.size() > 1) {

        JChoiceDialog selectG = new JChoiceDialog(parent, "Select Structure", true);
        for (int i = 0; i < Geometries.size(); i++) {
          MolecularGeometry gm = Geometries.get(i);
          selectG.addItem(gm.getName());
        }
        selectG.selectIndex(Geometries.size() - 1);
        selectG.pack();

        selectG.setLocationRelativeTo(parent);
        selectG.setVisible(true);
        if (selectG.isApproveOption()) {
          int n = selectG.getSelectedIndex();
          if (n != -1) {
            setupSelectedGeometry(m, Geometries.get(n));
            Molecule.guessCovalentBonds(m);
            Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
            java3dUniverse.setMolecule(m, read_mode);
          }

        }
      } else if (Geometries.size() == 1) {
        setupSelectedGeometry(m, Geometries.get(0));
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        java3dUniverse.setMolecule(m, read_mode);
      } else {
        JOptionPane.showMessageDialog(parent, "Didn't find geometries in output file", "Warning", JOptionPane.WARNING_MESSAGE);
      }

    } else if (fileType == MolecularFileFormats.formatG03_Fragment) { // Gaussian fragment file
      MoleculeInterface m = new Molecule();
      try {
        m = GaussianFragment.parseGaussianFragmentFile(m, fileName);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      java3dUniverse.setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.format_XMol_XYZ) { // XMol XYZ file
      MoleculeInterface m = new Molecule();
      try {
        XMolXYZ xMolXYZ = new XMolXYZ();
        m = xMolXYZ.parseXMolXYZ(fileName, m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      java3dUniverse.setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.format_MDL_Molfile) { // MDL Molfile file
      MoleculeInterface m = new Molecule();
      try {
        MDLMol mol = new MDLMol();
        m = mol.parseFile(fileName, m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      java3dUniverse.setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.format_GRO) { // Gromacs GRO file
      MoleculeInterface m = new Molecule();
      try {
        m = GromacsParserFactory.parseGromacsCoordFile(fileName, m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      java3dUniverse.setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.formatPDB) { // PDB file
      Molecule m = new Molecule();
      try {
        PDB.parsePDBFile(fileName, m);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      java3dUniverse.setMolecule(m, read_mode);
    } else if (fileType == MolecularFileFormats.formatTripos_Mol2) { // Tripos Mol2 file
      Molecule m = new Molecule();
      TriposParser triposParser = new TriposParser();
      try {
        triposParser.parseMol2File(fileName, m);
        java3dUniverse.setMolecule(m, read_mode);
      } catch (Exception ex) {
        logger.severe("Error opening " + fileName + " : " + ex.getMessage());
      }
    } else if (fileType == MolecularFileFormats.formatCCT) { // CCT file
      Molecule m = new Molecule();
      m.setAtomInterface(new Atom());
      m.setBondInterface(new Bond());
      CCTParser cctParser = new CCTParser(m);
      List mols = cctParser.parseCCTFile(fileName, m);
      m = (Molecule) mols.get(0);
      logger.info("Number of atoms: " + m.getNumberOfAtoms());
      java3dUniverse.setMolecule(m, read_mode);
    }

    return true;
  }

  public int setupSelectedGeometry(MoleculeInterface molec, MolecularGeometry geom) {

    if (geom.size() != molec.getNumberOfAtoms()) {
      logger.info("setupSelectedGeometry: geom.size() != getNumberOfAtoms() " + geom.size() + " != " + molec.getNumberOfAtoms());
      return -1;
    }

    for (int i = 0; i < molec.getNumberOfAtoms(); i++) {
      cct.vecmath.Point3f point = geom.getCoordinates(i);
      AtomInterface atom = molec.getAtomInterface(i);
      atom.setXYZ(point);
    }
    //selectedGeometry = n;
    return 1;
  }

  /**
   * Opens Molecule File in default mode (erasing old molecule and then adding a new one)
   *
   * @param title - dialog title
   * @param j3d - java3d renderer
   */
  public void openMoleculeFile(String title, Java3dUniverse j3d) {

    openMoleculeFile(title, j3d, GlobalSettings.ADD_MOLECULE_MODE.SET);

  }

  //void jMenuFileOpenAs_actionPerformed(ActionEvent actionEvent) {
  //  String arg = actionEvent.getActionCommand();
  //  try {
  //    openFile(java3dUniverse, arg);
  //  } catch (Exception ex) {
  //    JOptionPane.showMessageDialog(null, ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
  //  }
  //}
  void openFile(Java3dUniverse j3d, String fileType) throws Exception {
    if (fileType.equals(MolecularFileFormats.gaussian03GJF)) { // G03 Input file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.G03_GJF, null);
    } else if (fileType.equals(MolecularFileFormats.gaussian03Output)) { // G03 output file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.G03_OUTPUT, null);
    } else if (fileType.equals(MolecularFileFormats.gaussian03Cube)) { // G03 cube file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.G03_CUBE, null);
    } else if (fileType.equals(MolecularFileFormats.gaussianFragment)) { // Gaussian fragment file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.G03_FRAGMENT, null);
    } else if (fileType.equals(MolecularFileFormats.gaussianTrajectory)) {
      openFile(j3d, CHEMISTRY_FILE_FORMAT.G03_TRAJECTORY, null);
    } else if (fileType.equals(MolecularFileFormats.gaussianFormCheckpoint)) {
      openFile(j3d, CHEMISTRY_FILE_FORMAT.G03_FORM_CHECKPOINT, null);
    } else if (fileType.equals(MolecularFileFormats.CPMDOutputFormat)) {
      openFile(j3d, CHEMISTRY_FILE_FORMAT.CPMD_OUTPUT, null);
    } else if (fileType.equals(MolecularFileFormats.CPMDTrajectoryFormat)) {
      openFile(j3d, CHEMISTRY_FILE_FORMAT.CPMD_TRAJECTORY, null);
    } else if (fileType.equals(MolecularFileFormats.gamessInput)) { // GAMESS Input file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.GAMESS_INPUT, null);
    } else if (fileType.equals(MolecularFileFormats.gamessOutput)) { // GAMESS Output file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.GAMESS_OUTPUT, null);
    } else if (fileType.equals(MolecularFileFormats.mopacLog)) { // MOPAC Log file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.MOPAC2002_LOG, null);
    } else if (fileType.equals(MolecularFileFormats.mopacInput)) { // MOPAC Input file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.MOPAC_INPUT, null);
    } else if (fileType.equals(MolecularFileFormats.mopacOutput)) { // MOPAC Output file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.MOPAC_OUTPUT, null);
    } else if (fileType.equals(MolecularFileFormats.siestaInputFormat)) { // SIESTA input file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.SIESTA_INPUT, null);
    } else if (fileType.equals(MolecularFileFormats.adfInput)) { // ADF input file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.ADF_INPUT, null);
    } else if (fileType.equals(MolecularFileFormats.adfOutput)) { // ADF output file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.ADF_OUTPUT, null);
    } else if (fileType.equals(MolecularFileFormats.pdbFile)) { // PDB file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.PDB, null);
    } else if (fileType.equals(MolecularFileFormats.triposMol2)) { // Tripos Mol2 file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.MOL2, null);
    } else if (fileType.equals(MolecularFileFormats.mdlMolfileFormat)) { // MDL Molfile file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.MDL_MOLFILE, null);
    } else if (fileType.equals(MolecularFileFormats.gromacsGROFormat)) { // Gromacs GRO file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.GROMACS_GRO, null);
    } else if (fileType.equals(MolecularFileFormats.amberPrmtopFormat)) { // Amber topology file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.AMBER_PRMTOP, null);
    } else if (fileType.equals(MolecularFileFormats.amberPrepFormat)) { // Amber PREP file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.AMBER_PREP, null);
    } else if (fileType.equals(MolecularFileFormats.amberLibFormat)) { // Amber Lib (off) file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.AMBER_LIB, null);
    } else if (fileType.equals(MolecularFileFormats.cctFileFormat)) { // CCT file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.CCT, null);
    } else if (fileType.equals(MolecularFileFormats.xmolXYZFileFormat)) { // XMol XYZ file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.XMOL_XYZ, null);
    } else if (fileType.equals(MolecularFileFormats.vaspPoscarFormat)) { // VASP poscar file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.VASP_POSCAR, null);
    } else if (fileType.equals(MolecularFileFormats.vaspVasprunFormat)) { // VASP Vasprun file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.VASP_VASPRUN, null);
    } else if (fileType.equals(MolecularFileFormats.qchemFormat)) { // QChem input file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.QCHEM_INPUT, null);
    } else if (fileType.equals(MolecularFileFormats.qchemOutputFormat)) { // QChem Output file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.QCHEM_OUTPUT, null);
    } else if (fileType.equals(MolecularFileFormats.gulpInputFormat)) { // GULP Input file
      openFile(j3d, CHEMISTRY_FILE_FORMAT.GULP_INPUT, null);
    }
  }

  public boolean openMolecularModelingFile(Java3dUniverse j3d, String fileType, String fileName) {
    if (fileType == null || fileName == null) {
      return false;
    }
    int formatType = MolecularFileFormats.formatUnknown;
    if (fileType.equals(MolecularFileFormats.gaussian03GJF)) {
      formatType = MolecularFileFormats.formatG03_GJF;
    } else if (fileType.equals(MolecularFileFormats.gaussian03Output)) {
      formatType = MolecularFileFormats.formatG03_Output;
    } else if (fileType.equals(MolecularFileFormats.gaussianFragment)) {
      formatType = MolecularFileFormats.formatG03_Fragment;
    } else if (fileType.equals(MolecularFileFormats.pdbFile)) {
      formatType = MolecularFileFormats.formatPDB;
    } else if (fileType.equals(MolecularFileFormats.triposMol2)) {
      formatType = MolecularFileFormats.formatTripos_Mol2;
    } else if (fileType.equals(MolecularFileFormats.mdlMolfileFormat)) {
      formatType = MolecularFileFormats.format_MDL_Molfile;
    } else if (fileType.equals(MolecularFileFormats.gromacsGROFormat)) {
      formatType = MolecularFileFormats.format_GRO;
    } else if (fileType.equals(MolecularFileFormats.cctFileFormat)) {
      formatType = MolecularFileFormats.formatCCT;
    } else if (fileType.equals(MolecularFileFormats.xmolXYZFileFormat)) {
      formatType = MolecularFileFormats.format_XMol_XYZ;
    }

    return openMolecularModelingFile(j3d, formatType, fileName);
  }

  public boolean openMolecularModelingFile(Java3dUniverse j3d, int fileType, String fileName) {
    return openMolecularModelingFile(j3d, fileType, fileName, GlobalSettings.ADD_MOLECULE_MODE.SET);
  }

  void openFile(Java3dUniverse j3d, CHEMISTRY_FILE_FORMAT fileType, String fileName) throws Exception {

    MoleculeInterface m;
    SelectPropShowDialog gaussProp = null;
    MoleculeInterface mol;

    switch (fileType) {
      case ALL_FORMATS:
        break;
      case G03_GJF:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.gaussian03GJF, "Gaussian G03 Job Files (gjf)", JFileChooser.OPEN_DIALOG);
        }
        if (fileName != null) {
          openMolecularModelingFile(j3d, MolecularFileFormats.gaussian03GJF, fileName);

        }

        break;

      case G03_OUTPUT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.gaussian03Output, "Gaussian G03 Output Files (*.log;*.out)",
              JFileChooser.OPEN_DIALOG);
        }

        mol = new Molecule();
        GaussianJob gaussianJob = null;
        try {
          GaussianOutput gaussianOutput = new GaussianOutput();
          gaussianJob = gaussianOutput.parseFile(mol, fileName, false);
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading Gaussian Output file : " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (gaussianJob.countSteps() < 1) {
          JOptionPane.showMessageDialog(this, "Didn't find gaussian steps in output file...", "Warning", JOptionPane.WARNING_MESSAGE);
          return;
        }

        int index = 0;

        if (gaussianJob.countSteps() > 1) {
          JMoleculeSelectorDialog molSelectorDialog = new JMoleculeSelectorDialog(null, "Select Gaussian Step", true);
          molSelectorDialog.setSize(320, 240);
          molSelectorDialog.setLocationRelativeTo(this);
          Java3dUniverse j3dRenderer = new Java3dUniverse();
          molSelectorDialog.set3DRenderer(j3dRenderer);
          molSelectorDialog.setMoleculeSelector(gaussianJob);
          molSelectorDialog.pack();

          int cond = molSelectorDialog.showDialog();
          if (cond == JMoleculeSelectorDialog.CANCEL) {
            return;
          }
          index = molSelectorDialog.getSelectedIndex();
        }

        GaussianStep step = gaussianJob.getStep(index);

        step.getMolecule().addProperty(MoleculeInterface.OUTPUT_RESULTS, step);
        j3d.setMolecule(step.getMolecule());

        gaussProp = new SelectPropShowDialog(null, "Select calculated properties to show");
        gaussProp.setLocationRelativeTo(this);

        //gaussProp.setControls(gaussianOutput);
        gaussProp.setControls(step);
        gaussProp.pack();

        if (!gaussProp.showDialog()) {
          return;
        }

        // --- Show summary
        if (gaussProp.isShowSummary()) {
          JShowText showResume = new JShowText("Gaussian Results");
          showResume.setIconImage(GlobalSettings.ICON_16x16_GAUSSIAN.getImage());
          showResume.setSize(600, 640);
          showResume.setTitle("Gaussian Results: " + fileName);
          showResume.setLocationRelativeTo(this);
          showResume.setText(step.getOutputResume());
          showResume.setVisible(true);
        }

        // --- Show interactive chart
        if (gaussProp.isShowInteractiveChart()) {
          try {
            String[] terms = step.getAvailPropToChart();
            if (terms == null || terms.length < 1) {
              JOptionPane.showMessageDialog(this, "Didn't find any energy terms in output file...", "Warning",
                  JOptionPane.WARNING_MESSAGE);
              //return;
            } else {
              SelectFromListDialog list = new SelectFromListDialog(null, "Select Energy Terms to Plot");
              list.setListData(terms);
              //list.selectItem(vasprunXML.TOTAL_ENERGY_KEY, true);
              list.enableMultipleSelection(true);
              list.setLocationRelativeTo(this);
              list.setVisible(true);
              Object[] objs = list.getSelectedItems();
              if (objs == null || objs.length < 1) {
                JOptionPane.showMessageDialog(this, "No Energy terms have been selected...", "Warning", JOptionPane.WARNING_MESSAGE);
                //return;
              } else {

                double x[] = null;
                ChartFrame chart = null;
                for (int i = 0; i < objs.length; i++) {
                  double[] energies = step.getAllTerms(objs[i].toString());
                  if (energies == null) {
                    JOptionPane.showMessageDialog(this, "No Information about energy terms is found in output file...", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                  } else if (energies.length < 2) {
                    JOptionPane.showMessageDialog(this, "File has less than 2 snapshots with energy info. Chart won't be shown",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                  } else {
                    if (i == 0) {
                      chart = new ChartFrame();
                      chart.setIconImage(GlobalSettings.ICON_16x16_GAUSSIAN.getImage());
                      chart.setTitle("Gaussian Chart");
                      x = new double[energies.length];
                      for (int j = 0; j < x.length; j++) {
                        x[j] = j;
                      }
                      chart.setChartTitle("Energy Terms vs Steps");
                      chart.setXAxisTitle("Step");
                    }

                    chart.addDataSeries(x, energies, objs[i].toString());
                  }
                  //chart.setYAxisTitle("Energy");
                }

                if (chart != null) {
                  chart.setRenderer(j3d);
                  chart.setStructureManagerInterface(step);
                  chart.showChart(true);
                }
              }
            }
          } catch (Exception ex) {
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error Creating chart : " + ex.getMessage(), ERROR_MESSAGE,
                JOptionPane.ERROR_MESSAGE);
            //return;
          }
        }

        // --- frequencies
        VIBRATIONAL_SPECTRUM[] vibSpectra = gaussProp.spectraToShow();
        if (vibSpectra != null && vibSpectra.length > 0) {

          VibrationsRenderer vRenderer = null;
          try {
            vRenderer = new VibrationsRenderer(j3d.getMoleculeInterface(), step, j3d);
            j3d.getMoleculeInterface().addProperty(MoleculeInterface.VIBRATION_RENDERER, vRenderer);
          } catch (Exception ex) {
          }
          vcFrame = new VibrationsControlFrame();
          vcFrame.setTitle("Vibrations Control");
          vcFrame.setVibrationsRendererProvider(vRenderer);
          vcFrame.setSpectraProvider(step);
          vcFrame.pack();
          vcFrame.setLocationRelativeTo(this);
          vcFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          vcFrame.setVisible(true);

          try {
            ChartFrame ref_chart = null;
            for (int i = 0; i < vibSpectra.length; i++) {
              VibrationSpectrumChart vsChart = new VibrationSpectrumChart(step);
              ChartFrame chart = vsChart.createChart(vibSpectra[i]);

              if (ref_chart == null) {
                chart.setLocationRelativeTo(this);
              } else {
                chart.setLocationRelativeTo(ref_chart);
              }
              ref_chart = chart;
              chart.showChart(true);
            }
          } catch (Exception ex) {
            System.err.println("frequencies: " + ex.getMessage());
          }
        }
        break;

      case G03_FORM_CHECKPOINT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.gaussianFormCheckpoint,
              "Gaussian G03 Formatted Checkpoint Files (*.fchk;*.fch)",
              JFileChooser.OPEN_DIALOG);
        }

        G03FormCheckpoint g03FormCheckpoint = new G03FormCheckpoint();
        try {
          mol = new Molecule();
          g03FormCheckpoint.parseFile(fileName);
          g03FormCheckpoint.getMolecule(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.setMolecule(mol);
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading Gaussian formatted checkpoint file : "
              + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case G03_CUBE:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.gaussian03Cube,
              "Extract geometry from Gaussian G03 Cube File (*.cub;*.cube)",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        try {
          mol = new Molecule();
          GaussianCube.extractGeometry(fileName, mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.setMolecule(mol);
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading Gaussian Cube file : "
              + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }
        break;
      case G03_FRAGMENT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.gaussianFragment,
              "Gaussian Fragment Files (*.frg)",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName != null) {
          openMolecularModelingFile(j3d, MolecularFileFormats.gaussianFragment, fileName);
        }
        break;

      case G03_TRAJECTORY:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.gaussianTrajectory,
              "Extract geometry from Gaussian G03 Trajectory File (*.trj;*.7;*.log;*.out)",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        try {
          int n = JOptionPane.showConfirmDialog(this, "MD trajectory could contain many snapshots.\n"
              + "Do you want to load all snapshots? (could take a while) - Press Yes\n"
              + "The first snapshot only (fast) - Press No",
              "Select one snapshot or a whole trajectory", JOptionPane.YES_NO_OPTION);
          mol = new Molecule();
          G03MDTrajectory g03MDTrajectory = new G03MDTrajectory();
          if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
            g03MDTrajectory.parseMolecularGeometryOnly(mol, fileName);
          } else {
            //g03MDTrajectory.parseG03Trajectory(fileName, 0);
            g03MDTrajectory.parseG03Trajectory(fileName);
            g03MDTrajectory.getMolecularInterface(mol);
          }
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          //MoleculeInterface m2 = Molecule.divideIntoMolecules(mol);
          j3d.setMolecule(mol);

          if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
            break;
          }

          String[] terms = g03MDTrajectory.getAvailPropToChart();
          if (terms == null || terms.length < 2) {
            break;
          }

          n = JOptionPane.showConfirmDialog(this, "Do you want to plot some properties available in file?",
              "Properties for charting are available", JOptionPane.YES_NO_OPTION);
          if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
            break;
          }
          SelectFromListDialog list = new SelectFromListDialog(null, "Select Energy Terms to Plot");
          list.setListData(terms);
          list.selectItem( G03MDTrajectory.TOTAL_ENERGY_KEY, true);
          list.enableMultipleSelection(true);
          list.setLocationRelativeTo(this);
          list.setVisible(true);
          Object[] objs = list.getSelectedItems();
          if (objs == null || objs.length < 1) {
            JOptionPane.showMessageDialog(this, "No terms have been selected...", "Warning", JOptionPane.WARNING_MESSAGE);
            break;
          } else {
            double x[] = null;
            ChartFrame chart = null;
            for (int i = 0; i < objs.length; i++) {
              double[] energies = g03MDTrajectory.getAllTerms(objs[i].toString());
              if (energies == null) {
                JOptionPane.showMessageDialog(this, "No Information about energy terms is found in output file...",
                    "Warning", JOptionPane.WARNING_MESSAGE);
                return;
              } else if (energies.length < 2) {
                JOptionPane.showMessageDialog(this, "File has less than 2 snapshots with energy info. Chart won't be shown",
                    "Warning", JOptionPane.WARNING_MESSAGE);
              } else {
                if (i == 0) {
                  chart = new ChartFrame();
                  x = new double[energies.length];
                  for (int j = 0; j < x.length; j++) {
                    x[j] = j;
                  }
                  chart.setChartTitle("Property vs Steps");
                  chart.setXAxisTitle("Step");
                }

                chart.addDataSeries(x, energies, objs[i].toString());
              }
              //chart.setYAxisTitle("Energy");
            }

            if (chart != null) {
              chart.setRenderer(j3d);
              chart.setStructureManagerInterface(g03MDTrajectory);
              chart.showChart(true);
            }
          }

        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading Gaussian Trajectory file : " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }
        break;

      case CPMD_OUTPUT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.CPMDOutputFormat, "CPMD Output File (*.out)", JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        CPMD cpmd_output = new CPMD();
        try {
          cpmd_output.parseOutputFile(fileName);
          mol = new Molecule();
          cpmd_output.getMolecule(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.setMolecule(mol);

          JShowText showResume = new JShowText("CPMD Results");
          showResume.setIconImage(GlobalSettings.ICON_16x16_MOLECULE.getImage());
          showResume.setSize(600, 640);
          showResume.setTitle("CPMD Results: " + fileName);
          showResume.setLocationRelativeTo(this);
          showResume.setText(cpmd_output.getOutputResume());
          showResume.setVisible(true);

          String[] terms = null;
          if ((terms = cpmd_output.getAvailPropToChart()) != null && cpmd_output.countSnapshots() > 1) {

            int n = JOptionPane.showConfirmDialog(this,
                "CPMD output refer to the MD trajectory with " + cpmd_output.countSnapshots()
                + " snapshots.\n" + "Do you want to animate MD trajectory? - Press Yes\n",
                "Do you want to animate trajectory", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
              SelectFromListDialog list = new SelectFromListDialog(null, "Select Properties to Plot");
              list.setListData(terms);
              list.enableMultipleSelection(true);
              list.setLocationRelativeTo(this);
              list.setVisible(true);
              Object[] objs = list.getSelectedItems();
              if (objs == null || objs.length < 1) {
                JOptionPane.showMessageDialog(this, "No Properties have been selected...", "Warning", JOptionPane.WARNING_MESSAGE);
              } else {

                double x[] = null;
                ChartFrame chart = null;
                for (int i = 0; i < objs.length; i++) {
                  double[] energies = cpmd_output.getAllTerms(objs[i].toString());
                  if (energies == null) {
                    JOptionPane.showMessageDialog(this, "No Information about energy terms is found in output file...", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                    continue;
                  } else if (energies.length < 2) {
                    JOptionPane.showMessageDialog(this,
                        "File has less than 2 snapshots with property " + objs[i].toString()
                        + ". Chart won't be shown",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                  } else {
                    if (i == 0) {
                      chart = new ChartFrame();
                      chart.setIconImage(GlobalSettings.ICON_16x16_LINE_CHART.getImage());
                      chart.setTitle("MD Properties");
                      x = new double[energies.length];
                      for (int j = 0; j < x.length; j++) {
                        x[j] = j;
                      }
                      chart.setChartTitle("Properties vs Steps");
                      chart.setXAxisTitle("Step");
                    }

                    chart.addDataSeries(x, energies, objs[i].toString());
                  }
                  //chart.setYAxisTitle("Energy");
                }

                if (chart != null) {
                  chart.setRenderer(j3d);
                  chart.setStructureManagerInterface(cpmd_output);
                  chart.showChart(true);
                }
              }

            }

          }

          if (cpmd_output.hasWarningMessages()) {
            JOptionPane.showMessageDialog(this, cpmd_output.getWarnings(), "You have warnings...", JOptionPane.WARNING_MESSAGE);
          }
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading CPMD Output file : " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case CPMD_TRAJECTORY:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.CPMDTrajectoryFormat, "CPMD MD Trajectory File (*.xyz)",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        CPMD cpmd_traj = new CPMD();
        try {
          cpmd_traj.parseTrajectoryFile(fileName);
          mol = new Molecule();
          cpmd_traj.getMolecule(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.setMolecule(mol);

          if (cpmd_traj.hasWarningMessages()) {
            JOptionPane.showMessageDialog(this, cpmd_traj.getWarnings(), "You have warnings...", JOptionPane.WARNING_MESSAGE);
          }

          if (cpmd_traj.countSnapshots() > 1) {
            ChartFrame chart = new ChartFrame();
            chart.setIconImage(GlobalSettings.ICON_16x16_LINE_CHART.getImage());
            chart.setTitle("CPMD MD Control");
            chart.setChartTitle("Energies (no Energy Info)");
            chart.setXAxisTitle("Steps");
            //chart.enableChartPanel(false);
            //chart.enableTablePanel(false);

            double x[] = new double[cpmd_traj.countSnapshots()];
            double[] energies = new double[cpmd_traj.countSnapshots()];

            for (int i = 0; i < cpmd_traj.countSnapshots(); i++) {
              x[i] = i;
              energies[i] = 0;
            }

            chart.addDataSeries(x, energies, "Energies");

            if (chart != null) {
              chart.setRenderer(j3d);
              chart.setStructureManagerInterface(cpmd_traj);
              chart.pack();
              chart.setLocationRelativeTo(this);
              chart.showChart(true);
            }
          }
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading CPMD Output file : " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case GAMESS_INPUT:
        try {
          wizard.parseMolecularData("gamessInput");
          if (wizard.getMolecule() == null) {
            return;
          }
          j3d.setMolecule(wizard.getMolecule());
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading GAMESS Input file : " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }
        break;

      case GAMESS_OUTPUT:

        try {
          wizard.parseMolecularData("gamessOutput");
          if (wizard.getMolecule() == null) {
            return;
          }
          j3d.setMolecule(wizard.getMolecule());
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading GAMESS Output file : " + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }
        break;

      case MOPAC2002_LOG:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.mopacLog,
              "MOPAC Log Files (*.log)", JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        MopacOutput mopac = new MopacOutput();
        try {
          mopac.parseMopacLogFile(fileName);
          mol = new Molecule();
          mopac.getMolecularInterface(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.setMolecule(mol);

          JShowText showMopResume = new JShowText("MOPAC Log File Resume");
          showMopResume.setSize(600, 640);
          showMopResume.setTitle("MOPAC Log File Resume: " + fileName);
          showMopResume.setLocationRelativeTo(this);
          showMopResume.setText(mopac.getOutputResume());
          showMopResume.setVisible(true);

        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading MOPAC Log file : "
              + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }
        break;

      case MOPAC_INPUT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.mopacInput,
              "MOPAC Input Files (*.dat;*.inp)", JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        Mopac mopac_in = new Mopac();
        try {
          mopac_in.parseMopacInput(fileName, 0);
          mol = new Molecule();
          mopac_in.getMolecularInterface(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.setMolecule(mol);
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading MOPAC Input file : "
              + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case MOPAC_OUTPUT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.mopacOutput, "MOPAC Output Files (*.out)",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        MopacOutput mopac_out = new MopacOutput();
        try {
          mopac_out.parseMopacOutputFile(fileName);
          mol = new Molecule();
          mopac_out.getMolecularInterface(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.setMolecule(mol);

          JShowText showMopacResume = new JShowText("MOPAC Output File Resume");
          showMopacResume.setSize(600, 640);
          showMopacResume.setTitle("MOPAC Output File Resume: " + fileName);
          showMopacResume.setLocationRelativeTo(this);
          showMopacResume.setText(mopac_out.getOutputResume());
          showMopacResume.setVisible(true);

        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading MOPAC Output file : "
              + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }
        break;

      case PDB:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.pdbFile, "Brookhaven Protein Bank (PDB) Files",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName != null) {
          openMolecularModelingFile(j3d, MolecularFileFormats.pdbFile, fileName);
        }

        break;

      case SIESTA_INPUT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.siestaInputFormat, "SIESTA Input Files",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }
        SiestaInput siesta_input = new SiestaInput();
        try {
          siesta_input.parseFDF(fileName, 0);
          mol = new Molecule();
          siesta_input.getMolecularInterface(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.setMolecule(mol);
          UnitCellGraphics siestaUC = new UnitCellGraphics("Siesta Unit Cell");
          j3d.addGraphics(siestaUC.getCellGraphicsObject(siesta_input.getLatticeVectors()));
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading SIESTA input file : "
              + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case ADF_INPUT:
        if (fileName == null) {
          wizard.parseMolecularData("adfInput");
          if (wizard.getMolecule() == null) {
            return;
          }
        }
        try {
          j3d.setMolecule(wizard.getMolecule());

        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading ADF input file : " + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case ADF_OUTPUT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.adfOutput,
              "Amsterdam Density Functional package (ADF) Files",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }
        ADFOutput adf_output = new ADFOutput();
        try {
          adf_output.parseFile(fileName);
          if (adf_output.countSectionsWithGeometry() < 1) {
            JOptionPane.showMessageDialog(this, "Didn't find geometry in ADF output file", ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
            return;
          }
          int[] sections = adf_output.getSectionsWithGeometry();
          int nsect;
          if (sections.length == 1) {
            nsect = sections[0];
          } else {
            String[] choices = new String[sections.length];
            for (int i = 0; i < sections.length; i++) {
              choices[i] = String.valueOf(sections[i] + 1);
            }
            String input = (String) JOptionPane.showInputDialog(null,
                "ADF Output has several section with geommetry\nSelect a section",
                "Select Section", JOptionPane.QUESTION_MESSAGE, null,
                choices, choices[choices.length - 1]);
            if (input == null) {
              return;
            }
            nsect = Integer.valueOf(input) - 1;
          }
          adf_output.selectSection(nsect);
          mol = new Molecule();
          adf_output.getMolecularInterface(mol, nsect);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE,
              CCTAtomTypes.getElementMapping());
          mol.addProperty(MoleculeInterface.OUTPUT_RESULTS, adf_output);
          j3d.setMolecule(mol);

          if (adf_output.hasDisplacementVectors()) {
            VibrationsRenderer vRenderer = null;
            try {
              vRenderer = new VibrationsRenderer(j3d.getMoleculeInterface(), adf_output, j3d);
              j3d.getMoleculeInterface().addProperty(
                  MoleculeInterface.VIBRATION_RENDERER, vRenderer);
            } catch (Exception ex) {
            }
            vcFrame = new VibrationsControlFrame();
            vcFrame.setTitle("Vibrations Control");
            vcFrame.setVibrationsRendererProvider(vRenderer);
            vcFrame.setSpectraProvider(adf_output);
            vcFrame.pack();
            vcFrame.setLocationRelativeTo(this);
            vcFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            vcFrame.setVisible(true);
          }

          gaussProp = new SelectPropShowDialog(null, "Select calculated properties to show");
          gaussProp.setLocationRelativeTo(this);
          gaussProp.setControls(adf_output);
          gaussProp.pack();

          if (!gaussProp.showDialog()) {
            return;
          }

          // --- Show interactive chart
          if (gaussProp.isShowInteractiveChart()) {
            try {
              String[] terms = adf_output.getAvailPropToChart();
              if (terms == null || terms.length < 1) {
                JOptionPane.showMessageDialog(this, "Didn't find any properties to chart in output file...",
                    "Warning", JOptionPane.WARNING_MESSAGE);
                //return;
              } else {
                SelectFromListDialog list = new SelectFromListDialog(null, "Select Energy Terms to Plot");
                list.setListData(terms);
                list.enableMultipleSelection(true);
                list.setLocationRelativeTo(this);
                list.setVisible(true);
                Object[] objs = list.getSelectedItems();
                if (objs == null || objs.length < 1) {
                  JOptionPane.showMessageDialog(this, "No Energy terms have been selected...",
                      "Warning", JOptionPane.WARNING_MESSAGE);
                } else {

                  double x[] = null;
                  ChartFrame chart = null;
                  for (int i = 0; i < objs.length; i++) {
                    double[] energies = adf_output.getAllTerms(
                        objs[i].toString());
                    if (energies == null) {
                      JOptionPane.showMessageDialog(this, "No Information about energy terms is found in output file...",
                          "Warning", JOptionPane.WARNING_MESSAGE);
                      return;
                    } else if (energies.length < 2) {
                      JOptionPane.showMessageDialog(this, "File has less than 2 snapshots with energy info. Chart won't be shown",
                          "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                      if (i == 0) {
                        chart = new ChartFrame();
                        x = new double[energies.length];
                        for (int j = 0; j < x.length; j++) {
                          x[j] = j;
                        }
                        chart.setChartTitle("Properties vs Steps");
                        chart.setXAxisTitle("Step");
                      }

                      chart.addDataSeries(x, energies,
                          objs[i].toString());
                    }
                  }

                  if (chart != null) {
                    chart.setRenderer(j3d);
                    chart.setStructureManagerInterface(adf_output);
                    chart.showChart(true);
                  }
                }
              }
            } catch (Exception ex) {
              System.err.println(ex.getMessage());
              JOptionPane.showMessageDialog(this, "Error Creating chart : " + ex.getMessage(),
                  ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
            }
          }

        } catch (Exception ex) {
          //System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading ADF output file : " + ex.getMessage(),
              ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case MOL2:
        if (fileName == null) {
          wizard.parseMolecularData("triposmol2");
        }
        if (fileName != null) {
          wizard.parseMolecularData("triposmol2", fileName);
        }
        if (wizard.getNumberMolecules() > 0) {
          j3d.addMolecule(wizard.getMolecule());
        }
        break;

      case MDL_MOLFILE:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.mdlMolfileFormat, "MDL Molfile Files", JFileChooser.OPEN_DIALOG);
        }
        if (fileName != null) {
          openMolecularModelingFile(j3d, MolecularFileFormats.mdlMolfileFormat, fileName);
        }

        break;

      case GROMACS_GRO:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.gromacsGROFormat, "Gromacs Coordinate Files",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName != null) {
          openMolecularModelingFile(j3d, MolecularFileFormats.gromacsGROFormat, fileName);
        }

        break;

      case AMBER_PRMTOP:
        if (prmtopDialog == null) {
          prmtopDialog = new ReadPrmtopDialog(null, "Open Amber Topology File (prmtop)", true, null);
          prmtopDialog.setLocationRelativeTo(this);
        }
        m = new Molecule();
        prmtopDialog.setMolecule(m);

        prmtopDialog.setVisible(true);

        if (prmtopDialog.isOKPressed() && prmtopDialog.getMolecule().getNumberOfAtoms() > 0) {
          j3d.addMolecule(prmtopDialog.getMolecule());
        }

        break;

      case AMBER_PREP:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.amberPrepFormat, "Amber PREP Files", JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }
        AmberPrep amberPrep = new AmberPrep();
        try {
          amberPrep.parsePrepFile(fileName, 0);
          mol = new Molecule();
          amberPrep.getMolecularInterface(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.setMolecule(mol);
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading Amber prep file : " + ex.getMessage(),
              ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case AMBER_LIB:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.amberLibFormat, "Amber Lib (Off) Files",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }
        MoleculeInterface mol_lib = new Molecule();
        AmberLib amberLib = new AmberLib(mol_lib);
        try {
          amberLib.parseLibFile(fileName, 0);
          logger.info("Number of molecules: " + amberLib.countMolecules());
          if (amberLib.countMolecules() == 1) {
            String[] res_name = amberLib.getMoleculeNames();
            mol_lib = amberLib.getMolecule(res_name[0]);
            Molecule.guessAtomTypes(mol_lib, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
            j3d.addMolecule(mol_lib);
          } else {
            Map<String, MoleculeInterface> molecules = amberLib.getMolecules();
            if (moleculeSelectorDialog == null) {
              moleculeSelectorDialog = new MoleculeSelectorDialog(null, "Select molecule", false);
              Java3dUniverse libJ3D = new Java3dUniverse();
              moleculeSelectorDialog.setJ3DParent(j3d);
              moleculeSelectorDialog.setJ3DRenderer(libJ3D);
              moleculeSelectorDialog.setSize(350, 500);
              moleculeSelectorDialog.setLocationRelativeTo(this);
              moleculeSelectorDialog.setAlwaysOnTop(true);
            }
            moleculeSelectorDialog.setMoleculeTree(molecules);
            moleculeSelectorDialog.setVisible(true);
          }
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading Amber Lib file : " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case CCT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.cctFileFormat, "Computational Chemistry Toolkit Format",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName != null) {
          openMolecularModelingFile(j3d, MolecularFileFormats.cctFileFormat, fileName);
        }

        break;

      case XMOL_XYZ:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.xmolXYZFileFormat, "XMol XYZ Format",
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName != null) {
          openMolecularModelingFile(j3d, MolecularFileFormats.xmolXYZFileFormat, fileName);
        }
        break;

      case VASP_POSCAR:
        if (vaspDialog == null) {
          vaspDialog = new ReadPoscarDialog(null, "Open VASP Poscar File (poscar)", true, null);
          vaspDialog.setLocationRelativeTo(this);
        }
        m = new Molecule();
        vaspDialog.setMolecule(m);

        vaspDialog.setVisible(true);

        if (vaspDialog.isOKPressed() && vaspDialog.getMolecule().getNumberOfAtoms() > 0) {
          m = vaspDialog.getMolecule();
          Molecule.guessCovalentBonds(m);
          j3d.addMolecule(m);
          UnitCellGraphics poscarUC = new UnitCellGraphics("VASP Unit Cell");
          j3d.addGraphics(poscarUC.getCellGraphicsObject(vaspDialog.getLatticeVectors()));
        }

        break;

      case VASP_VASPRUN:

        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.vaspVasprunFormat, MolecularFileFormats.vaspVasprunFormat,
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }
        VasprunXML vasprunXML = null;
        try {
          vasprunXML = new VasprunXML();
          vasprunXML.parseVasprunXML(fileName);
          mol = new Molecule();
          vasprunXML.getMolecularInterface(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.addMolecule(mol);
          if (vasprunXML.getLatticeVectors() != null) {
            UnitCellGraphics vasprunUC = new UnitCellGraphics("VASP Unit Cell");
            j3d.addGraphics(vasprunUC.getCellGraphicsObject(vasprunXML.getLatticeVectors()));
          }
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading Vasprun file : " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        try {
          String[] terms = vasprunXML.getEnergyTerms();
          if (terms == null || terms.length < 1) {
            JOptionPane.showMessageDialog(this, "Didn't find any energy terms in Vasprun file...",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
          }
          SelectFromListDialog list = new SelectFromListDialog(null, "Select Energy Terms to Plot");
          list.setListData(terms);
          list.selectItem( VasprunXML.TOTAL_ENERGY_KEY, true);
          list.enableMultipleSelection(true);
          list.setLocationRelativeTo(this);
          list.setVisible(true);
          Object[] objs = list.getSelectedItems();
          if (objs == null || objs.length < 1) {
            JOptionPane.showMessageDialog(this, "No Energy terms have been selected...", "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
          }

          List arrays = new ArrayList(objs.length);
          double x[] = null;
          ChartFrame chart = null;
          for (int i = 0; i < objs.length; i++) {
            double[] energies = vasprunXML.getAllTerms(objs[i].toString());
            if (energies == null) {
              JOptionPane.showMessageDialog(this, "No Information about energy terms is found in Vasprun file...",
                  "Warning", JOptionPane.WARNING_MESSAGE);
              return;
            } else if (energies.length < 2) {
              JOptionPane.showMessageDialog(this, "File has less than 2 snapshots with energy info. Chart won't be shown",
                  "Warning", JOptionPane.WARNING_MESSAGE);
              return;
            }

            if (i == 0) {
              chart = new ChartFrame();
              x = new double[energies.length];
              for (int j = 0; j < x.length; j++) {
                x[j] = j;
              }
              chart.setChartTitle("Energy Terms vs Steps");
              chart.setXAxisTitle("Step");
            }

            chart.addDataSeries(x, energies, objs[i].toString());
          }

          chart.setRenderer(j3d);
          chart.setStructureManagerInterface(vasprunXML);
          chart.showChart(true);
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Creating chart : "
              + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case QCHEM_INPUT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.qchemFormat, MolecularFileFormats.qchemFormat,
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        try {
          QChem qchem = new QChem();
          qchem.parseQChemInput(fileName, 0);
          mol = new Molecule();
          qchem.getMolecularInterface(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.addMolecule(mol);
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading QChem Input file : "
              + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case QCHEM_OUTPUT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.qchemOutputFormat,
              MolecularFileFormats.qchemOutputFormat, JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        try {
          QChemOutput qchem = new QChemOutput();
          qchem.parseQChemOutputFile(fileName, false);
          mol = new Molecule();
          qchem.getMolecularInterface(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.addMolecule(mol);

          JShowText showQChemResume = new JShowText("Q-Chem Output File Resume");
          showQChemResume.setSize(600, 640);
          showQChemResume.setTitle("Q-Chem Calculation Summary: " + fileName);
          showQChemResume.setLocationRelativeTo(this);
          showQChemResume.setText(qchem.getOutputResume());
          showQChemResume.setVisible(true);

        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading QChem Output file : "
              + ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;

      case GULP_INPUT:
        if (fileName == null) {
          fileName = chooseFileDialog(MolecularFileFormats.gulpInputFormat, MolecularFileFormats.gulpInputFormat,
              JFileChooser.OPEN_DIALOG);
        }
        if (fileName == null) {
          return;
        }

        try {
          Gulp gulp = new Gulp();
          gulp.parseInput(fileName, 0);
          mol = new Molecule();
          gulp.getMolecularInterface(mol);
          Molecule.guessCovalentBonds(mol);
          Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
          j3d.addMolecule(mol);
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
          JOptionPane.showMessageDialog(this, "Error Loading GULP input file : " + ex.getMessage(), ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        break;
    }
  }

  String chooseFileDialog(String fileType, String formatDescr, int dialogType) {
    JFileChooser chooser = new JFileChooser();
    FileFilterImpl filter = new FileFilterImpl();
    String extensions = MolecularFileFormats.readFormats.get(fileType).
        toString();
    String temp[] = extensions.split(";");
    for (int i = 0; i < temp.length; i++) {
      filter.addExtension(temp[i]);
    }
    filter.setDescription(formatDescr);
    chooser.setFileFilter(filter);
    chooser.setDialogType(dialogType);
    if (dialogType == JFileChooser.OPEN_DIALOG) {
      chooser.setDialogTitle("Open File");
      chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    } else if (dialogType == JFileChooser.SAVE_DIALOG) {
      chooser.setDialogTitle("Save File");
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    }

    currentWorkingDirectory = GlobalSettings.getCurrentWorkingDirectory();
    if (currentWorkingDirectory != null) {
      chooser.setCurrentDirectory(currentWorkingDirectory);
    } else if (prefs != null) {
      String lastPWD = prefs.get(GlobalSettings.lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory()
            && currentWorkingDirectory.exists()) {
          chooser.setCurrentDirectory(currentWorkingDirectory);
        }
      }
    }

    int returnVal = JFileChooser.CANCEL_OPTION;

    if (dialogType == JFileChooser.OPEN_DIALOG) {
      returnVal = chooser.showOpenDialog(this);
    } else if (dialogType == JFileChooser.SAVE_DIALOG) {
      returnVal = chooser.showSaveDialog(this);
    }

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

  public void setKnownHosts(final String[] hosts) {

    final loadFromSFTP_actionAdapter lfsftp = new loadFromSFTP_actionAdapter(this);
    submenuLoadRemote.removeAll();
    for (int i = 0; i < hosts.length; i++) {
      final JMenuItem jMenuItem = new JMenuItem(hosts[i]);
      jMenuItem.addActionListener(lfsftp);
      submenuLoadRemote.add(jMenuItem);
    }

  }

  public void setFileBrowser(final String host, final String username,
      final FileBrowserInterface file_browser) {
    final String key = username + "@" + host;

    if (sftpChoosers.containsKey(key)) {
      sftpChooserDialog = sftpChoosers.get(key);
    } else if (ftpChoosers.containsKey(file_browser)) {
      sftpChooserDialog = ftpChoosers.get(file_browser);
    } else {
      sftpChooserDialog = new SFTPFileChooserDialog(null, "Select file", true);

      jamberooCore.getShadowSFTPManager().addClient(sftpChooserDialog);
      sftpChooserDialog.setLocationRelativeTo(this);

      final Map<CHEMISTRY_FILE_FORMAT, String> readFormats = MolecularFileFormats.remoteReadFormats;
      final Map<CHEMISTRY_FILE_FORMAT, String> readFormatsDescr = MolecularFileFormats.remoteReadFormatDescription;
      int count = 0;
      final FileFilterImpl[] filter = new FileFilterImpl[readFormats.size()];

      //filter[count] = new FileFilterImpl();
      //filter[count].setDescription("All files");
      //++count;
      remoteFileFormats.clear();
      final Set set = readFormats.entrySet();
      final Iterator iter = set.iterator();
      while (iter.hasNext()) {
        final Map.Entry me = (Map.Entry) iter.next();
        final CHEMISTRY_FILE_FORMAT format = (CHEMISTRY_FILE_FORMAT) me.getKey();
        final String extensions = readFormats.get(format);
        final String temp[] = extensions.split(";");
        filter[count] = new FileFilterImpl();
        //String descr = readFormatsDescr.get(format);
        StringBuilder descr = new StringBuilder(readFormatsDescr.get(
            format));

        for (int i = 0; i < temp.length; i++) {
          if (i == 0) {
            descr.append(" (");
          } else {
            descr.append(";");
          }
          filter[count].addExtension(temp[i]);
          descr.append("*." + temp[i]);
          if (i == temp.length - 1) {
            descr.append(")");
          }
        }
        filter[count].setDescription(descr.toString());
        remoteFileFormats.put(descr.toString(), format);

        ++count;
      }

      sftpChooserDialog.setTitle("sftp: " + host + " as " + username);
      sftpChooserDialog.setFileBrowser(file_browser);
      sftpChooserDialog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      sftpChooserDialog.setFileFilters(filter);

      ftpChoosers.put(file_browser, sftpChooserDialog);
      sftpChoosers.put(key, sftpChooserDialog);

    }
    sftpChooserDialog.setVisible(true);

    if (!sftpChooserDialog.isLoadPressed()) {
      return;
    }

    File local = sftpChooserDialog.getLocalFile();
    if (local == null) {
      return;
    }
    String filter = sftpChooserDialog.getSeletedFilterDescription();
    CHEMISTRY_FILE_FORMAT rFormat = remoteFileFormats.get(filter);
    try {
      this.openFile(java3dUniverse, rFormat, local.getAbsolutePath());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void loadFromSFTP(ActionEvent e) {
    String command = e.getActionCommand();

    if (command.contains(" as ")) {
      final String host = command.substring(0, command.indexOf(" "));
      final String username = command.substring(command.lastIndexOf(" ") + 1);
      final String hostKey = username + "@" + host;
      if (sftpChoosers.containsKey(hostKey)) {
        setFileBrowser(host, username, null);
        return;
      }
    }

    if (jamberooCore.getShadowSFTPManager() == null) {
      System.err.println("No Shadow Manager is set...");
      return;
    }
    try {
      jamberooCore.getShadowSFTPManager().processConnection(command, this);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), ERROR_MESSAGE,
          JOptionPane.ERROR_MESSAGE);
    }
  }

  void jMenuFileSaveAs_actionPerformed(ActionEvent actionEvent) {
    String arg = actionEvent.getActionCommand();

    if (java3dUniverse.getMolecule() == null
        || java3dUniverse.getMolecule().getNumberOfAtoms() < 1) {
      JOptionPane.showMessageDialog(this, "Load Molecule first!",
          ERROR_MESSAGE,
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Gaussian G03 GJF Files
    if (arg.equals(MolecularFileFormats.gaussian03GJF)) { // G03 Input file
      if (saveG03GJFDialog == null) {
        saveG03GJFDialog = new SaveG03GJFDialog(null, "Save Gaussian Input File", false);
        //saveG03GJFDialog.setLinkZeroCommands("%nproc=1");
        //saveG03GJFDialog.setRouteSection("# Opt HF/6-31g");
        Gaussian g = new Gaussian();
        g.setGraphicsRenderer(java3dUniverse);
        saveG03GJFDialog.setGJFParser(g);
        saveG03GJFDialog.setLocationRelativeTo(this);
      }
      SwingUtilities.updateComponentTreeUI(saveG03GJFDialog);
      saveG03GJFDialog.setupMolecule(java3dUniverse.getMolecule());
      saveG03GJFDialog.setVisible(true);

    } else if (arg.equals(MolecularFileFormats.siestaInputFormat)) { // Siesta Input file
      if (saveSiestaInputDialog == null) {
        saveSiestaInputDialog = new SaveSiestaInputDialog(null, "Save Siesta input file", false);
        saveSiestaInputDialog.setJava3dUniverse(java3dUniverse);
        saveSiestaInputDialog.setMolecularInterface(java3dUniverse.getMolecule());
        saveSiestaInputDialog.pack();
        saveSiestaInputDialog.setLocationRelativeTo(this);
      } else if (!saveSiestaInputDialog.isVisible()) {
        saveSiestaInputDialog.setMolecularInterface(java3dUniverse.getMolecule());
      }

      saveSiestaInputDialog.setVisible(true);
    } else if (arg.equals(MolecularFileFormats.xmolXYZFileFormat)) { // XMOL XYZ file

      String fileName = chooseFileDialog(MolecularFileFormats.xmolXYZFileFormat,
          "XMol XYZ Files (*.xyz)",
          JFileChooser.SAVE_DIALOG);
      if (fileName != null) {
        String name = FileUtilities.getFileName(fileName);
        if (name.indexOf(".") == -1) {
          fileName += ".xyz";
        }

        if (saveXYZDialog == null) {
          saveXYZDialog = new SaveXYZDialog(null, "Select Save Options");
          saveXYZDialog.setLocationRelativeTo(this);
        }
        saveXYZDialog.setVisible(true);
        if (!saveXYZDialog.okPressed) {
          return;
        }
        try {
          XMolXYZ xMolXYZ = new XMolXYZ();
          xMolXYZ.saveXMolXYZ(fileName, java3dUniverse.getMolecule(),
              saveXYZDialog.isAtomicSymbols(),
              saveXYZDialog.isInAngstroms());
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage(),
              ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
        }
      }
    } else if (arg.equals(MolecularFileFormats.pdbFile)) { // PDB file
      String fileName = chooseFileDialog(MolecularFileFormats.pdbFile,
          "Brookhaven Protein Bank (PDB) Files (*.pdb)",
          JFileChooser.SAVE_DIALOG);
      if (fileName != null) {
        String name = FileUtilities.getFileName(fileName);
        if (name.indexOf(".") == -1) {
          fileName += ".pdb";
        }

        PDB.saveAsPDBFile(java3dUniverse.getMolecule(),
            fileName);
      }
    } else if (arg.equals(MolecularFileFormats.triposMol2)) { // Tripos Mol2 file
      String fileName = chooseFileDialog(MolecularFileFormats.triposMol2,
          "Tripos Mol2 Files (*.mol2)",
          JFileChooser.SAVE_DIALOG);
      if (fileName != null) {
        String name = FileUtilities.getFileName(fileName);
        if (name.indexOf(".") == -1) {
          fileName += ".mol2";
        }

        try {
          TriposParser.saveTriposMol2File(java3dUniverse.getMolecule(),
              fileName);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage(),
              ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
        }
      }
    } else if (arg.equals(MolecularFileFormats.cctFileFormat)) { // CCT file
      String fileName = chooseFileDialog(MolecularFileFormats.cctFileFormat,
          "Computational Chemistry Toolkit Files (*.cct)",
          JFileChooser.SAVE_DIALOG);
      if (fileName != null) {
        String name = FileUtilities.getFileName(fileName);
        if (name.indexOf(".") == -1) {
          fileName += ".cct";
        }
        try {
          CCTParser.saveCCTFile(java3dUniverse.getMolecule(),
              fileName);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this,
              "Error Saving CCT file: "
              + ex.getMessage(),
              ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
    } else if (arg.equals(MolecularFileFormats.povrayFormat)) { // Povray file
      PovrayJava3d povray = new PovrayJava3d(java3dUniverse);
      try {
        povray.savePovrayFile();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error Saving Pov-Ray file: " + ex.getMessage(),
            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        return;
      }
    } else if (arg.equals(MolecularFileFormats.geomFormat)) { // Geom file
      GeomFormat geom = new GeomFormat();
      geom.setMolecule(java3dUniverse.getMoleculeInterface());
      try {
        geom.saveGeomFormatFile();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Error Saving Geom Format file: "
            + ex.getMessage(),
            ERROR_MESSAGE,
            JOptionPane.ERROR_MESSAGE);
        return;
      }
    } else if (arg.equals(MolecularFileFormats.vrmlFormat)) { // VRML file
      //GroupToVRML vrml = new GroupToVRML(java3dUniverse.getMoleculeBranchGroup());
      //GroupToVRML vrml = new GroupToVRML(java3dUniverse.getRootBranchGroup());
      GroupToVRML vrml = new GroupToVRML(java3dUniverse.getLocale());
      vrml.enableColorsPerVertex(false); // Don't write colors per vertex (to be compatible with Acrobat 9,xx)
      vrml.setCanvas3D(java3dUniverse.getCanvas3D());
      try {
        vrml.saveVRMLFile();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error Saving VRML file: " + ex.getMessage(),
            ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
  }

  public void saveImageAs(ActionEvent e) {
    jSubmenuImageSave.setEnabled(false);

    String arg = e.getActionCommand();
    BufferedImage bImage = java3dUniverse.getImageCapture();
    MediaTracker mediaTracker = new MediaTracker(this);
    mediaTracker.addImage(bImage, 0);
    try {
      mediaTracker.waitForID(0);
      ImageTools.saveImageAsDialog(bImage, arg, null);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error Saving Image: " + ex.getMessage(),
          ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
      System.err.println(ex.getMessage());
    }
    jSubmenuImageSave.setEnabled(true);
  }

  public void submitMolecule(ActionEvent e) {
    this.jSubmenuMoleculeSubmit.setEnabled(false);
    String arg = e.getActionCommand();

    if (arg.equalsIgnoreCase("gaussian") || arg.equalsIgnoreCase("g03") || arg.equalsIgnoreCase("g09")) { // Redo this!!!
      if (gaussianInputEditorFrame == null) {
        Gaussian g = new Gaussian();
        g.setGraphicsRenderer(java3dUniverse);
        gaussianInputEditorFrame = new GaussianInputEditorFrame(g);
        gaussianInputEditorFrame.setTitle("Edit Options and Submit to Grid");
        gaussianInputEditorFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        gaussianInputEditorFrame.setLocationRelativeTo(this);
        try {
          gaussianInputEditorFrame.removeMenuItem("File", "Exit");
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
        }
        submitGaussianJob.setText("Submit to Grid");
        submitGaussianJob.setActionCommand(arg);
        submitGaussian_actionAdapter al = new submitGaussian_actionAdapter(this);

        submitGaussianJob.addActionListener(al);
        gaussianInputEditorFrame.addMenuItem("File", submitGaussianJob);
        gaussianInputEditorFrame.returnBackButton.setVisible(true);
        gaussianInputEditorFrame.returnBackButton.setToolTipText("Submit to Grid");
        gaussianInputEditorFrame.returnBackButton.setIcon(GlobalSettings.ICON_32x32_SERVER_FROM_CLIENT);
        gaussianInputEditorFrame.returnBackButton.setActionCommand(arg);
        gaussianInputEditorFrame.returnBackButton.addActionListener(al); // ???

      }

      if (java3dUniverse.getMoleculeInterface() != null
          && java3dUniverse.getMoleculeInterface().getNumberOfAtoms() > 0) {
        gaussianInputEditorFrame.setupMolecule(java3dUniverse.getMoleculeInterface());
      }

      gaussianInputEditorFrame.setVisible(true);

    }
    this.jSubmenuMoleculeSubmit.setEnabled(true);
  }

  public void submitGaussianJob(ActionEvent e) {
    // --- Save gjf file
    String arg = e.getActionCommand();

    if (gaussianFileChooserDialog == null) {
      FileFilterImpl filters[] = new FileFilterImpl[1];
      filters[0] = new FileFilterImpl();
      filters[0].addExtension("gjf");
      filters[0].addExtension("GJF");
      filters[0].addExtension("com");
      filters[0].addExtension("COM");
      filters[0].setDescription(
          "Gaussian Input Files (*.gjf;*.com;*.g03)");

      String pwd = Utils.getPreference(this, lastSaveGaussianInputDirKey);

      gaussianFileChooserDialog = new FileChooserDialog(filters, this, "Select file to save", JFileChooser.SAVE_DIALOG);
      gaussianFileChooserDialog.setWorkingDirectory(pwd);
    }

    String fileName = gaussianFileChooserDialog.getFile();

    if (fileName == null || fileName.trim().length() < 1) {
      return;
    }

    if (fileName.indexOf(".") == -1) {
      fileName += ".gjf";
    }

    String pwd = gaussianFileChooserDialog.getCurrentDirectory().
        getAbsolutePath();
    Utils.savePreference(this, lastSaveGaussianInputDirKey, pwd);

    //FileDialog fd = new FileDialog(new Frame(), "Save Gaussian Job File",
    //                               FileDialog.SAVE);
    //fd.setFile("*.gjf;*.com");
    //fd.setDirectory(directory);
    //fd.setFile(file_name);
    //fd.setVisible(true);
    //if (fd.getFile() == null) {
    //   return;
    //}
    //String newFileName = new String(fd.getFile());
    if (fileName.indexOf(".") == -1) {
      fileName += ".gjf";
    } else if (fileName.endsWith(".")) {
      fileName += "gjf";
    }
    //String newWorkingDirectory = new String(fd.getDirectory());

    try {
      IOUtils.saveStringIntoFile(gaussianInputEditorFrame.toString(), fileName);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
      return;
    }

    // --- Open job submit dialog
    ScriptSubmitterDialogInterface ssd = GridProviders.getScriptSubmitter(
        arg);
    JDialog dialog = ssd.getDialog();
    dialog.setModal(true);
    if (fileName.contains("/")) {
      ssd.setInputFile(fileName.substring(fileName.lastIndexOf("/") + 1));
    } else if (fileName.contains("\\")) {
      ssd.setInputFile(fileName.substring(fileName.lastIndexOf("\\") + 1));
    } else {
      ssd.setInputFile(fileName);
    }

    //ssd.setLocalDirectory(newWorkingDirectory);
    ssd.setLocalDirectory(pwd + "/");

    dialog.setVisible(true);
    gaussianInputEditorFrame.setVisible(false);
  }

  public File getCurrentWorkingDirectory() {
    return currentWorkingDirectory;
  }

  public void setCurrentWorkingDirectory(File currentWorkingDirectory) {
    this.currentWorkingDirectory = currentWorkingDirectory;
  }

  public VibrationsControlFrame getVcFrame() {
    return vcFrame;
  }

  public void setVcFrame(VibrationsControlFrame vcFrame) {
    this.vcFrame = vcFrame;
  }

  //class JEditorFrame_jMenuFileOpenAs_ActionAdapter
  //        implements ActionListener {
  //  FileMenu adaptee;
  //  JEditorFrame_jMenuFileOpenAs_ActionAdapter(FileMenu adaptee) {
  //    this.adaptee = adaptee;
  //  }
  //  public void actionPerformed(ActionEvent actionEvent) {
  //    adaptee.jMenuFileOpenAs_actionPerformed(actionEvent);
  //  }
  //}
  private class loadFromSFTP_actionAdapter
      implements ActionListener {

    private FileMenu adaptee;

    loadFromSFTP_actionAdapter(FileMenu adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.loadFromSFTP(e);
    }
  }

  class JEditorFrame_jMenuFileSaveAs_ActionAdapter
      implements ActionListener {

    FileMenu adaptee;

    JEditorFrame_jMenuFileSaveAs_ActionAdapter(FileMenu adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
      adaptee.jMenuFileSaveAs_actionPerformed(actionEvent);
    }
  }

  private class saveImageAs_actionAdapter
      implements ActionListener {

    private FileMenu adaptee;

    saveImageAs_actionAdapter(FileMenu adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.saveImageAs(e);
    }
  }

  private class submitMolecule_actionAdapter
      implements ActionListener {

    private FileMenu adaptee;

    submitMolecule_actionAdapter(FileMenu adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.submitMolecule(e);
    }
  }

  private class submitGaussian_actionAdapter
      implements ActionListener {

    private FileMenu adaptee;

    submitGaussian_actionAdapter(FileMenu adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.submitGaussianJob(e);
    }
  }
}
