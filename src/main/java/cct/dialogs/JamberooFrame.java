/* ***** BEGIN LICENSE BLOCK *****
 Version: Apache 2.0/GPL 3.0/LGPL 3.0

 Jamberoo - Java Molecules Editor
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

 ***** END LICENSE BLOCK *****
 */
package cct.dialogs;

import cct.GlobalSettings;
import cct.gaussian.GaussianCube;
import cct.gaussian.ui.ManageCubesFrame;
import cct.help.JavaHelp;
import cct.interfaces.*;
import cct.interfaces.MoleculeEventObject.MOLECULE_EVENT;
import cct.j3d.Java3dUniverse;
import cct.j3d.VibrationsRenderer;
import cct.modelling.*;
import cct.modelling.ui.VibrationsControlFrame;
import cct.resources.images.ImageResources;
import cct.tools.GridProviders;
import cct.tools.ui.JShowText;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 *
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p> This is the main interface for the Jamberoo (JMolEditor) By default, is redirected to the internal
 * window. To disable this feature set cct.dialogs.JamberooFrame@redirectStderr = false in the cct.properties file
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 *
 */
public class JamberooFrame
    extends JFrame implements OperationsOnAtoms, AtomProperties, ActionListener, ChangeListener, MoleculeChangeListener {

  private static String version = "15";
  private static String build = "0804";
  private static final String ENABLE_DATABASE = "enableDatabase";
  private static final String ERROR_MESSAGE = "Error";
  private static final String LICENSE_FILE = "Jamberoo-license.txt";
  private boolean useTabbedPane = false;
  private J3DTabbedWorld j3dTabbedWorld = null;
  private JPanel contentPane;
  private BorderLayout borderLayout1 = new BorderLayout();
  private JMenuBar jamberooMenuBar = new JMenuBar();
  private JMenu jMenuFile = new JMenu();
  private JMenu jMenuGrid = new JMenu("Grid");
  private JMenuItem jMenuFileExit = new JMenuItem();
  private JMenuItem postSuggestionMenuItem = new JMenuItem("Post Your Suggestion");
  private JMenuItem postBugMenuItem = new JMenuItem("Post a Bug");
  private JMenu jMenuEdit = new JMenu("Edit");
  private JMenu jMenuView = new JMenu("View");
  private JMenu jMenuDatabase = new JMenu("Database");
  private JMenu jMenuSetup; // = new JMenu("Setup");
  private JMenu jMenuHelp = new JMenu();
  private JMenu jMenuAnalyze = new JMenu("Analyze");
  private JMenuItem jMenuHelpAbout = new JMenuItem();
  private JMenuItem jMenuHelpLicense = new JMenuItem("License");
  private JMenuItem jMenuDBLogin = new JMenuItem("Login");
  private JMenuItem jMenuDBLogout = new JMenuItem("Logout");
  private JMenuItem loadGaussianCube = new JMenuItem("Load Gaussian Cube");
  private JMenuItem manageGaussianCubes = new JMenuItem("Manage Gaussian Cubes");
  private java.util.List volumeDataList = new ArrayList();
  private ManageCubesFrame manageCubesDialog = null;
  private JMenuItem jMenuItemHelp = new JMenuItem("Help");
  private JToolBar mainToolbar = new JToolBar();
  private JToolBar jFileToolBar = new JToolBar("File");
  private JToolBar jEditToolBar = new JToolBar("Edit");
  private JToolBar jDBToolBar = new JToolBar("Database");
  private JButton openFileButton = new JButton();
  private JButton deleteMoleculeButton = new JButton();
  private JButton jButton3 = new JButton();
  private JButton trackingHelpButton = new JButton();
  private JButton modifyBondsButton = new JButton();
  private JButton modifyAnglesButton = new JButton();
  private JButton modifyDihedralsButton = new JButton();
  private JMenuItem vibrationalAnalysis = new JMenuItem("Frequencies");
  protected JMenuItem trajectoryAnalysis = new JMenuItem("Analyse MD Trajectory");
  protected TrajectoryAnalysisDialog trajectoryAnalysisDialog = null;
  private ImageIcon tableImage = new ImageIcon(cct.resources.Resources.class.getResource(
          "/cct/images/icons16x16/table_selection_all.png"));
  private ImageIcon image2 = new ImageIcon(JamberooFrame.class.getResource("/cct/images/closeFile.png"));
  private ImageIcon image3 = new ImageIcon(JamberooFrame.class.getResource("/cct/images/help.png"));
  private ImageIcon exitImage = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/door2.gif"));
  private ImageIcon docPulseImage = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/document_pulse.png"));
  private ImageIcon dbImage = new ImageIcon(JamberooFrame.class.getResource("/cct/images/db-16x16.gif"));
  private JButton jAddAtoms = new JButton();
  private JButton jAddFragments = new JButton();
  private JButton jAddMoleculeButton = new JButton();
  private ImageIcon addAtomsImage = new ImageIcon(JamberooFrame.class.getResource("/cct/images/add-atom.gif"));
  private ImageIcon getHelpOn = new ImageIcon(ImageResources.class.getResource("/cct/images/icons16x16/getHelpOn.png"));
  private JButton jDeleteAtoms = new JButton();
  private JButton jModifyAtoms = new JButton();
  private JButton jDBLogin = new JButton();
  private JLabel statusBar = new JLabel();
  private VibrationsControlFrame vcFrame = null;
  private Java3dUniverse java3dUniverse;
  private JEditorFrame_jMenuAnalyze_ActionAdapter processAnalyzeMenu;
  //private JEditorFrame_jMenuDB_ActionAdapter processDBMenu;
  private JScrollBar jScrollBar1 = new JScrollBar();
  private JScrollBar jScrollBar2 = new JScrollBar();
  private JPanel lowerPanel = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private Component dad = this;
  private FileMenu fileMenu = null;
  private EditMenu editMenu = null;
  static final Logger logger = Logger.getLogger(JamberooFrame.class.getCanonicalName());
  private JamberooCore jamberooCore = new JamberooCore();
  private ResourceBundle messages;

  public JamberooFrame(String title, String[] args) {
    super(title);
    processArguments(args);
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      initializeJamberooFrame();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JamberooFrame(String[] args) {
    processArguments(args);
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      initializeJamberooFrame();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public JamberooFrame(String title) {
    super(title);
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      initializeJamberooFrame();
    } catch (Exception exception) {
      exception.printStackTrace();
    }

  }

  void processArguments(final String[] args) {
    if (args == null) {
      return;
    }
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-v")) {
        logger.info(version + "." + build);
        System.exit(0);
      } else if (args[i].equals("-V")) {
        logger.info("Version " + version + " build " + build);
        System.exit(0);
      }

    }

  }

  public void setDefaultHelper() {
    String helpHS = "JMolEditorHelpSet.hs";
    JavaHelp javahelp = null;
    try {
      javahelp = new JavaHelp(helpHS);
      setHelper(javahelp);
    } catch (Exception ex) {
      System.err.println("Activating Help System: " + ex.getMessage());
    }

  }

  /**
   * Component initialization.
   *
   * @throws Exception
   */
  private void initializeJamberooFrame() throws Exception {
    // Get System Properties
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

    jamberooCore.setComponet(this);

    messages = GlobalSettings.getResourceBundle();

    boolean enableDatabase = false;
    //URL custom = null;
    String className = this.getClass().getCanonicalName();
    try {
      //custom = JamberooFrame.class.getClassLoader().getResource(CCT_PROPERTY_FILE);
      Properties props = GlobalSettings.getProperties();
      //props.load(custom.openStream());

      //--- Whether redirect stderr ?...
      String customCommand = "";

      //--- Whether to enable database facility ?...
      customCommand = props.getProperty(className + GlobalSettings.DIVIDER + ENABLE_DATABASE, String.valueOf(enableDatabase));
      if (customCommand != null) {
        try {
          enableDatabase = Boolean.parseBoolean(customCommand);
        } catch (Exception exx) {
          System.err.println("Error parsing " + className + GlobalSettings.DIVIDER + ENABLE_DATABASE + " value from " + GlobalSettings.CCT_PROPERTY_FILE
              + " file. Ignored...");
        }
      }

    } catch (Exception ex) {
      System.err.println("Warning: cannot open " + GlobalSettings.CCT_PROPERTY_FILE + " : " + ex.getMessage());
    }

    // --- Init memory monitor
    //memorymonitor = new MemoryMonitor();
    GridProviders.setParentComponent(this);

    // --- create event processing
    //processViewMenu = new JEditorFrame_jMenuView_ActionAdapter(this);
    //processDBMenu = new JEditorFrame_jMenuDB_ActionAdapter(this);
    processAnalyzeMenu = new JEditorFrame_jMenuAnalyze_ActionAdapter(this);

    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);

    java3dUniverse = jamberooCore.getJamberooRenderer();
    //setLayout(new BorderLayout());
    //Canvas3D canvas3D = java3dUniverse.getCanvas3D();

    //add("Center",canvas3D);
    // Comment this line before starting Design !!!
    if (useTabbedPane) {
      j3dTabbedWorld = new J3DTabbedWorld((useTabbedPane ? J3DTabbedWorld.MULTIPLE_MOLECULES : J3DTabbedWorld.SINGLE_MOLECULE));
      getContentPane().add(j3dTabbedWorld.getComponent());
      validate();
    } else {
      logger.info("Double Buffer Available: " + java3dUniverse.getCanvas3D().getDoubleBufferAvailable());
      logger.info("Double Buffer Enable: " + java3dUniverse.getCanvas3D().getDoubleBufferEnable());
      getContentPane().add(java3dUniverse.getCanvas3D());
    }

    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    statusBar.setText("Hold down Left mouse button for Rotation, Middle button for Zooming and Right button for Translation: ");

    fileMenu = new FileMenu(jamberooCore);
    fileMenu.addMoleculeChangeListener(this);
    //shadowSFTPManager = fileMenu.getShadowSFTPManager();
    vcFrame = fileMenu.getVcFrame();
    jMenuFile = fileMenu;
    jamberooMenuBar.add(jMenuFile);

    jMenuFile.setText(messages != null ? messages.getString("file_menu") : "File");
    jMenuFile.setMnemonic(KeyEvent.VK_F);

    jMenuFileExit.setText("Exit");
    jMenuFileExit.setMnemonic(KeyEvent.VK_X);
    jMenuFileExit.setIcon(exitImage);
    jMenuFileExit.addActionListener(new JEditorFrame_jMenuFileExit_ActionAdapter(this));

    jScrollBar1.setOrientation(JScrollBar.HORIZONTAL);
    jScrollBar1.setValue(50);
    jScrollBar1.setEnabled(false);
    lowerPanel.setLayout(borderLayout2);
    deleteMoleculeButton.addActionListener(new JEditorFrame_deleteMoleculeButton_actionAdapter(this));
    jScrollBar2.setEnabled(false);
    jScrollBar2.setForeground(Color.orange); // --- Assemble menu bar
    //openFileButton.addActionListener(new JEditorFrame_openFileButton_actionAdapter(this)); // --- File Menu
    openFileButton.addActionListener(this); // --- File Menu
    jButton3.addActionListener(new JEditorFrame_jButton3_actionAdapter(this));

    jMenuFile.add(jMenuFileExit);

    // --- Edit Menu
    editMenu = new EditMenu(jamberooCore);
    editMenu.addMoleculeChangeListener(this);
    editMenu.setParentComponent(this);
    editMenu.setVolumeDataList(volumeDataList);
    editMenu.setManageCubesDialog(manageCubesDialog);
    editMenu.createMenu();
    jMenuEdit = editMenu;

    jamberooMenuBar.add(jMenuEdit);

    jMenuEdit.setMnemonic(KeyEvent.VK_E);
    jMenuEdit.setText(messages != null ? messages.getString("edit_menu") : "Edit");

    // --- View Menu
    ViewMenu viewMenu = new ViewMenu(jamberooCore);
    viewMenu.createMenu();
    jMenuView = viewMenu;
    jMenuView.setText(messages != null ? messages.getString("view_menu") : "View");
    jMenuView.setMnemonic(KeyEvent.VK_V);

    jamberooMenuBar.add(jMenuView);

    // --- Calculate menu
    CalculateMenu calculateMenu = new CalculateMenu(this.jamberooCore);
    calculateMenu.setParentComponent(this);
    calculateMenu.createMenu();
    calculateMenu.setText(messages != null ? messages.getString("calculate_menu") : "Calculate");
    calculateMenu.setMnemonic(KeyEvent.VK_C);

    jamberooMenuBar.add(calculateMenu);

    // --- Database Menu
    if (enableDatabase) {
      jMenuDBLogin.setToolTipText("Login Database");
      jMenuDatabase.add(jMenuDBLogin);
      jMenuDBLogin.addActionListener(this);
      jMenuDBLogout.setEnabled(false);
      jMenuDatabase.add(jMenuDBLogout);
      jMenuDBLogout.addActionListener(this);
      jamberooMenuBar.add(jMenuDatabase);
    }

    // --- Grid menu
    jMenuGrid.setMnemonic(KeyEvent.VK_G);
    jMenuGrid.setText(messages != null ? messages.getString("grid_menu") : "Grid");

    JMenuItem jMenuItem;
    if (GridProviders.isJobStatusDialogAvailable()) {
      jMenuItem = new JMenuItem("Job Status");
      jMenuItem.addActionListener(GridProviders.getActionListener());
      GridProviders.addActionListener(jMenuItem, GridProviders.JOB_STATUS_DIALOG);
      GridProviders.setMolecularViewer(this.java3dUniverse);
      jMenuGrid.add(jMenuItem);
    }

    if (jMenuGrid.getItemCount() > 0) {
      jamberooMenuBar.add(jMenuGrid);
    }

    // --- Analyze Menu
    jMenuAnalyze.setMnemonic(KeyEvent.VK_A);
    jMenuAnalyze.setText(messages != null ? messages.getString("analyze_menu") : "Analyze");
    buildAnalyzeMenu();
    jMenuAnalyze.setEnabled(true);
    jamberooMenuBar.add(jMenuAnalyze);

    // --- Tools menu
    ToolsMenu toolsMenu = new ToolsMenu(this.jamberooCore);
    toolsMenu.setParentComponent(this);
    toolsMenu.createMenu();
    toolsMenu.setText(messages != null ? messages.getString("tools_menu") : "Tools");
    toolsMenu.setMnemonic(KeyEvent.VK_T);

    jamberooMenuBar.add(toolsMenu);

    // --- Setup Menu
    SetupMenu setupMenu = new SetupMenu(this.jamberooCore);
    setupMenu.setParentComponent(this);
    setupMenu.createMenu();
    jMenuSetup = setupMenu;
    jMenuSetup.setText(messages != null ? messages.getString("setup_menu") : "Setup");
    jMenuSetup.setMnemonic(KeyEvent.VK_S);

    jamberooMenuBar.add(jMenuSetup);

    // --- Help Menu
    jMenuHelp.setText(messages != null ? messages.getString("help_menu") : "Help");
    jMenuHelp.setMnemonic(KeyEvent.VK_H);

    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.setIcon(new ImageIcon(ImageResources.class.getResource(
            "/cct/images/icons16x16/help2.png")));
    jMenuHelpAbout.addActionListener(new JEditorFrame_jMenuHelpAbout_ActionAdapter(this));

    jMenuHelpLicense.setIcon(new ImageIcon(ImageResources.class.getResource(
            "/cct/images/icons16x16/document_certificate.png")));

    jMenuHelpLicense.addActionListener(new ActionListener() {

      private JShowText showLicense = new JShowText("Jamberoo License agreement");
      private boolean init = true;

      public void actionPerformed(ActionEvent e) {
        if (init) {
          showLicense.enableTextEditing(false);
          showLicense.setSize(600, 640);
          showLicense.setLocationByPlatform(true);
          showLicense.setText(cct.tools.Utils.getResourceAsString(LICENSE_FILE));
          init = false;
        }
        showLicense.setVisible(true);
      }
    });

    postSuggestionMenuItem.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {

        try {
          GlobalSettings.mailUsingDefaultClient(GlobalSettings.getProperty(GlobalSettings.HELP_EMAIL), "Suggestion for Jamberoo",
              "Please, type your suggestion(s) here...");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          System.err.println(ex.getMessage());
        }
        /*
         * if (postSuggestionDialog == null) { postSuggestionDialog = new PostMessageDialog(thisFrame, "Post Your Suggestion",
         * false); PostMessageType type = PostMessageType.SUGGESTION; postSuggestionDialog.setDialogType(type);
         * postSuggestionDialog.validate(); postSuggestionDialog.pack(); postSuggestionDialog.setLocationRelativeTo(thisFrame); }
         * postSuggestionDialog.setVisible(true);
         */
      }
    });
    postBugMenuItem.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {

        try {
          GlobalSettings.mailUsingDefaultClient(GlobalSettings.getProperty(GlobalSettings.HELP_EMAIL), "Jamberoo Bug",
              "Please, report a bug here.\nTell exactly what you did\n"
              + "Which GUI controls you selected and what order you selected them in.\n"
              + "If the program reads from a file, you will probably need to send a copy of the file.\n"
              + "Above all, be precise. Programmers like precision... :-)");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          System.err.println(ex.getMessage());
        }
        /*
         * if (postABugDialog == null) { postABugDialog = new PostMessageDialog(thisFrame, "Post A Bug", false);
         * postABugDialog.setEmailImportance(PostMessageDialog.EMAIL_IS_OPTIONAL_BUT_WARN); PostMessageType type =
         * PostMessageType.BUG_REPORT; postABugDialog.setDialogType(type); postABugDialog.validate(); postABugDialog.pack();
         * postABugDialog.setLocationRelativeTo(thisFrame); } postABugDialog.setVisible(true);
         */
      }
    });

    jMenuItemHelp.setIcon(new ImageIcon(ImageResources.class.getResource(
            "/cct/images/icons16x16/help-file.png")));

    jMenuHelp.add(jMenuItemHelp);
    jMenuItemHelp.setEnabled(false);

    postSuggestionMenuItem.setIcon(new ImageIcon(ImageResources.class.getResource("/cct/images/icons16x16/document_edit.png")));
    jMenuHelp.add(postSuggestionMenuItem);

    postBugMenuItem.setIcon(new ImageIcon(ImageResources.class.getResource(
            "/cct/images/icons16x16/bug_red.png")));
    jMenuHelp.add(postBugMenuItem);

    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuHelpLicense);
    jMenuHelp.add(jMenuHelpAbout);

    jamberooMenuBar.add(Box.createHorizontalGlue());

    jamberooMenuBar.add(jMenuHelp);

    setJMenuBar(jamberooMenuBar);

    // --- Make "File" toolbar
    openFileButton.setIcon(GlobalSettings.ICON_16x16_OPEN_FILE);
    openFileButton.setToolTipText("Open File");
    openFileButton.setInheritsPopupMenu(true);
    deleteMoleculeButton.setIcon(image2);
    deleteMoleculeButton.setToolTipText("Erase Molecule");
    jButton3.setIcon(image3);
    jButton3.setToolTipText("Help");
    jFileToolBar.add(openFileButton);
    jFileToolBar.add(deleteMoleculeButton);
    jFileToolBar.add(jButton3);

    jFileToolBar.addSeparator();

    jFileToolBar.add(fileMenu.getSnapshotButton());

    // --- Make "Edit" toolbar
    jAddAtoms.setIcon(addAtomsImage);
    jAddAtoms.setToolTipText(EditMenu.ADD_ATOMS_MESSAGE);
    jAddAtoms.setActionCommand(EditMenu.EDIT_MENU_ACTIONS.ADD_ATOMS.name());
    jAddAtoms.addActionListener(editMenu);
    jEditToolBar.add(jAddAtoms);

    jAddFragments.setIcon(GlobalSettings.ICON_16x16_ADD_FRAGMENT);
    jAddFragments.setToolTipText("Add Fragments");
    jAddFragments.setActionCommand(EditMenu.EDIT_MENU_ACTIONS.ADD_FRAGMENT.name());
    jAddFragments.addActionListener(editMenu);
    jEditToolBar.add(jAddFragments);

    jAddMoleculeButton.setIcon(GlobalSettings.ICON_16x16_MOLECULE);
    jAddMoleculeButton.setToolTipText(EditMenu.ADD_MOLECULE_MESSAGE);
    jAddMoleculeButton.setActionCommand(EditMenu.EDIT_MENU_ACTIONS.ADD_MOLECULE.name());
    jAddMoleculeButton.addActionListener(editMenu);
    jEditToolBar.add(jAddMoleculeButton);

    jEditToolBar.addSeparator();

    jDeleteAtoms.setIcon(GlobalSettings.ICON_16x16_CUT);
    jDeleteAtoms.setToolTipText(EditMenu.DELETE_ATOMS_MESSAGE);
    jDeleteAtoms.setActionCommand(EditMenu.EDIT_MENU_ACTIONS.DELETE_ATOMS.name());
    jDeleteAtoms.addActionListener(editMenu);
    jEditToolBar.add(jDeleteAtoms);

    jEditToolBar.addSeparator();

    jModifyAtoms.setIcon(GlobalSettings.ICON_16x16_ATOM);
    jModifyAtoms.setToolTipText(EditMenu.MODIFY_ATOMS_MESSAGE);
    jModifyAtoms.setActionCommand(EditMenu.EDIT_MENU_ACTIONS.MODIFY_ATOMS.name());
    jModifyAtoms.addActionListener(editMenu);
    jEditToolBar.add(jModifyAtoms);

    modifyBondsButton.setIcon(GlobalSettings.ICON_16x16_BOND);
    modifyBondsButton.setToolTipText("Modify Interatomic Distances");
    modifyBondsButton.setActionCommand(EditMenu.EDIT_MENU_ACTIONS.MODIFY_BONDS.name());
    modifyBondsButton.addActionListener(editMenu);
    jEditToolBar.add(modifyBondsButton);

    modifyAnglesButton.setIcon(GlobalSettings.ICON_16x16_ANGLE);
    modifyAnglesButton.setToolTipText("Modify Angles between atoms");
    modifyAnglesButton.setActionCommand(EditMenu.EDIT_MENU_ACTIONS.MODIFY_ANGLES.name());
    modifyAnglesButton.addActionListener(editMenu);
    jEditToolBar.add(modifyAnglesButton);

    modifyDihedralsButton.setIcon(GlobalSettings.ICON_16x16_DIHEDRAL);
    modifyDihedralsButton.setToolTipText("Modify Dihedral angles");
    modifyDihedralsButton.setActionCommand(EditMenu.EDIT_MENU_ACTIONS.MODIFY_TORSIONS.name());
    modifyDihedralsButton.addActionListener(editMenu);
    jEditToolBar.add(modifyDihedralsButton);

    // --- Make database toolbar
    if (enableDatabase) {
      jDBLogin.setIcon(dbImage);
      jDBLogin.setToolTipText("Login/Logout Database");
      jDBLogin.addActionListener(this);
      jDBToolBar.add(jDBLogin);
    }

    mainToolbar.add(jFileToolBar);
    mainToolbar.add(jEditToolBar);
    if (enableDatabase) {
      mainToolbar.add(jDBToolBar);
    }
    trackingHelpButton.setIcon(getHelpOn);
    trackingHelpButton.setToolTipText("Tracking Help");
    mainToolbar.addSeparator();
    mainToolbar.add(trackingHelpButton);

    /*
     * Test button JButton temp = new JButton("Ca",tetra); temp.setHorizontalAlignment(SwingConstants.CENTER);
     * temp.setHorizontalTextPosition(SwingConstants.CENTER); temp.setVerticalAlignment(SwingConstants.CENTER);
     * temp.setVerticalTextPosition(SwingConstants.CENTER); temp.setSize(35,35); temp.setFont(new Font("Monospaced", Font.BOLD,
     * 16));
     *
     * mainToolbar.add(temp);
     */
    jScrollBar2.setValue(50);
    contentPane.add(jScrollBar2, BorderLayout.EAST);
    contentPane.add(lowerPanel, BorderLayout.SOUTH);
    lowerPanel.add(statusBar, BorderLayout.SOUTH);
    lowerPanel.add(jScrollBar1, BorderLayout.CENTER);
    contentPane.add(mainToolbar, BorderLayout.NORTH);

    // Comment this line before starting Design !!!
    //contentPane.add(graphicsPanel, java.awt.BorderLayout.CENTER);
    //contentPane.add(canvas3D, java.awt.BorderLayout.CENTER);
  }

  public void setHelper(HelperInterface h) {
    jamberooCore.setHelper(h);
    HelperInterface helper = jamberooCore.getHelper();
    JRootPane rootpane = this.getRootPane();

    java3dUniverse.setHelper(jamberooCore.getHelper());

    // --- Set help for root pane
    //helper.enableHelpKey(rootpane, "top");
    jamberooCore.getHelper().enableHelpKey(rootpane, "top", "javax.help.SecondaryWindow", null);
    //helper.enableHelpKey(rootpane, "top", "javax.help.Popup", null);

    // --- Set help for menues
    helper.setHelpIDString(jamberooMenuBar, "menues.top");
    helper.setHelpIDString(jMenuFile, "menues.file");
    helper.setHelpIDString(jMenuEdit, "menues.edit");
    helper.setHelpIDString(jMenuView, "menues.view");
    helper.setHelpIDString(jMenuAnalyze, "menues.analyze");
    helper.setHelpIDString(jMenuSetup, "menues.setup");

    // --- Main help in tool bar
    helper.setHelpIDString(jButton3, "top");
    helper.displayHelpFromSourceActionListener(jButton3);

    helper.setHelpIDString(this.jAddAtoms, HelperInterface.JMD_ADD_ATOM_ID);
    //helper.displayHelpFromSourceActionListener(jAddAtoms);

    helper.setHelpIDString(this.jAddFragments, HelperInterface.JMD_ADD_FRAGMENT_ID);
    //helper.displayHelpFromSourceActionListener(jAddFragments);

    helper.setHelpIDString(this.jAddMoleculeButton, HelperInterface.JMD_ADD_MOLECULE_ID);
    //helper.displayHelpFromSourceActionListener(jAddMoleculeButton);

    helper.setHelpIDString(this.modifyBondsButton, HelperInterface.JMD_MODIFY_BOND_ID);

    helper.setHelpIDString(this.modifyAnglesButton, HelperInterface.JMD_MODIFY_ANGLE_ID);

    helper.setHelpIDString(this.modifyDihedralsButton, HelperInterface.JMD_MODIFY_TORSION_ID);

    jMenuItemHelp.setEnabled(true);
    helper.setHelpIDString(jMenuItemHelp, "top");
    helper.displayHelpFromSourceActionListener(jMenuItemHelp);

    helper.addActionListenerForTracking(trackingHelpButton);
  }

  /**
   * Checks whether molecule loaded or not
   *
   * @return boolean "true" if loaded and "false" otherwise
   */
  public boolean isMoleculeLoaded() {
    MoleculeInterface m = java3dUniverse.getMolecule();
    if (m == null) {
      JOptionPane.showMessageDialog(this, "Load Molecule first!", "Warning",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }

  void jMenuAnalyze_actionPerformed(ActionEvent actionEvent) {
    String arg = actionEvent.getActionCommand();

    // --- process menues
    if (arg.equals("Measure Bond Length")) {
      //java3dUniverse.enableMousePicking(true); // Enable mouse picking
    } else if (actionEvent.getSource() == vibrationalAnalysis) {
      if (java3dUniverse.getMolecule() == null
          || java3dUniverse.getMolecule().getNumberOfAtoms() < 1) {
        JOptionPane.showMessageDialog(this, "Load Molecule first!", ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        return;
      }
      MoleculeInterface mol = java3dUniverse.getMoleculeInterface();
      Object obj = mol.getProperty(MoleculeInterface.OUTPUT_RESULTS);
      if (obj == null || (!(obj instanceof OutputResultsInterface))) {
        if (!(obj instanceof OutputResultsInterface) && obj != null) {
          System.err.println("Possible internal inconsistency: ! (obj instanceof OutputResultsInterface)");
        }
        JOptionPane.showMessageDialog(this, "No frequencies information for a molecule", ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        return;
      }
      OutputResultsInterface results = (OutputResultsInterface) obj;
      if (!results.hasDisplacementVectors() && results.countSpectra() == 0) {
        JOptionPane.showMessageDialog(this, "No frequencies information for a molecule", ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
        return;
      }
      obj = mol.getProperty(MoleculeInterface.VIBRATION_RENDERER);
      VibrationsRenderer vRenderer = null;
      if (obj == null) {
        try {
          vRenderer = new VibrationsRenderer(mol, results, java3dUniverse);
          mol.addProperty(MoleculeInterface.VIBRATION_RENDERER, vRenderer);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Cannot initialize vibration renderer", ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
          return;
        }
      } else {
        vRenderer = (VibrationsRenderer) obj;
      }

      if (vcFrame == null || (!vcFrame.isVisible())) {
        vcFrame = new VibrationsControlFrame();
        vcFrame.setTitle("Vibrations Control");
        vcFrame.setVibrationsRendererProvider(vRenderer);
        vcFrame.setSpectraProvider(results);
        vcFrame.pack();
        vcFrame.setLocationRelativeTo(this);
        vcFrame.setVisible(true);
      } else {
        vcFrame.toFront();
      }
    } else if (actionEvent.getSource() == trajectoryAnalysis) {
      if (java3dUniverse.getMolecule() == null
          || java3dUniverse.getMolecule().getNumberOfAtoms() < 1) {
        JOptionPane.showMessageDialog(this, "Load Molecule first!",
            ERROR_MESSAGE,
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (this.trajectoryAnalysisDialog == null) {
        trajectoryAnalysisDialog = new TrajectoryAnalysisDialog(this,
            "MD Trajectory Analysis", false);
        trajectoryAnalysisDialog.setLocationRelativeTo(this);
        trajectoryAnalysisDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
      }
      trajectoryAnalysisDialog.setRenderer(java3dUniverse);
      trajectoryAnalysisDialog.setVisible(true);
    } else if (actionEvent.getSource() == loadGaussianCube) {
      String fileName = fileMenu.chooseFileDialog(MolecularFileFormats.gaussian03Cube,
          "Load Gaussian G03 Cube File (*.cub;*.cube)",
          JFileChooser.OPEN_DIALOG);
      if (fileName == null) {
        return;
      }

      GaussianCube gCube = new GaussianCube();
      try {
        MoleculeInterface mol = new Molecule();
        gCube.parseVolumetricData(fileName);
        gCube.getMolecule(mol);
        Molecule.guessCovalentBonds(mol);
        Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE,
            CCTAtomTypes.getElementMapping());
        //MoleculeInterface m2 = Molecule.divideIntoMolecules(mol);
        java3dUniverse.addMolecule(mol);
        volumeDataList.add(gCube);

        JOptionPane.showMessageDialog(this, gCube.getTitle() + "\n"
            + gCube.getCubeDescription() + "\n" + "Number of atoms: "
            + mol.getNumberOfAtoms() + "\n" + "Number of cubes: "
            + gCube.countCubes(), "Gaussian cube info",
            JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
        JOptionPane.showMessageDialog(this,
            "Error Loading Gaussian Cube file : "
            + ex.getMessage(), ERROR_MESSAGE,
            JOptionPane.ERROR_MESSAGE);
        return;
      }

    } else if (actionEvent.getSource() == manageGaussianCubes) {
      if (manageCubesDialog == null) {
        manageCubesDialog = new ManageCubesFrame("Manage Volumetric Data (Cubes)", this.volumeDataList);
        //if (gopDialog == null) {
        //  initGraphObjDialog(this.java3dUniverse);
        //}
        jamberooCore.showGraphicsObjectsDialog(false);

        //manageCubesDialog.setGraphicsObjectPropertiesFrame(gopDialog);
        manageCubesDialog.setJamberooCore(jamberooCore);
        if (jamberooCore.getHelper() != null) {
          JButton h = manageCubesDialog.getHelpButton();
          h.setEnabled(true);
          jamberooCore.getHelper().setHelpIDString(h, HelperInterface.JMD_MANAGE_GAUSSIAN_CUBES_ID);
          jamberooCore.getHelper().displayHelpFromSourceActionListener(h);
        }
        //manageCubesDialog.setJava3dUniverse(java3dUniverse);
        manageCubesDialog.pack();
        manageCubesDialog.setLocationRelativeTo(this);
      } else {
        manageCubesDialog.setCubeList(volumeDataList);
      }
      //if (gopDialog != null) {
      //  gopDialog.setVisible(false);
      //}
      manageCubesDialog.setVisible(true);
    }

  }

  void jMenuDB_actionPerformed(ActionEvent actionEvent) {
    if (jMenuDBLogin == actionEvent.getSource() || jDBLogin == actionEvent.getSource()) {

      if (java3dUniverse.connectToDatabase()) {
        jMenuDBLogin.setText("Save/Retrieve Data");
        jMenuDBLogin.setToolTipText(
            "Save/Retrieve Data to/from Database");
        jMenuDBLogout.setEnabled(true);
      }
    } else if (jMenuDBLogout == actionEvent.getSource()) {
      java3dUniverse.logoutDatabase();
      jMenuDBLogin.setText("Login Database");
      jMenuDBLogin.setToolTipText("Login Database");
      jMenuDBLogout.setEnabled(false);
    }
  }

  public Component getComponent() {
    return this;
  }

  void buildAnalyzeMenu() {

    JMenuItem jMenuItem;

    /*
     * // --- "Measure Bond Length" item jMenuItem = new JMenuItem("Measure Bond Length", new ImageIcon(cct.dialogs.
     * JamberooFrame.class. getResource( "/images/empty-transp-16x16.png")));
     *
     * jMenuItem.addActionListener(processAnalyzeMenu); jMenuAnalyze.add(jMenuItem);
     */
    // --- Molecular geometry
    jMenuItem = new JMenuItem("Geometry", GlobalSettings.ICON_16x16_DOCUMENT_INFO);
    jMenuItem.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        if (java3dUniverse.getMolecule() == null
            || java3dUniverse.getMoleculeInterface().getNumberOfAtoms()
            < 1) {
          JOptionPane.showMessageDialog(null, "Load molecule first",
              ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (java3dUniverse.getMoleculeInterface().getNumberOfAtoms()
            > 100) {
          int n = JOptionPane.showConfirmDialog(dad,
              "Molecule has "
              + java3dUniverse.getMoleculeInterface().
              getNumberOfAtoms()
              + " - output could be lengthy.\n"
              + "Do you want to continue?\n",
              "Output could be lengthy",
              JOptionPane.YES_NO_OPTION);
          if (n == JOptionPane.NO_OPTION
              || n == JOptionPane.CLOSED_OPTION) {
            return;
          }
        }

        JShowText showResume = new JShowText("Molecular Geometry");
        showResume.setIconImage(GlobalSettings.ICON_16x16_DOCUMENT_INFO.getImage());
        showResume.enableTextEditing(false);
        showResume.setSize(600, 640);
        showResume.setLocationRelativeTo(dad);
        showResume.setText(Molecule.geometryAsString(java3dUniverse.getMoleculeInterface()));
        showResume.setDefaultFileName("geometry.txt");
        showResume.setVisible(true);
      }
    });
    jMenuAnalyze.add(jMenuItem);

    // --- Interatomic distances
    jMenuItem = new JMenuItem("Interatomic Distances", tableImage);
    jMenuItem.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        if (java3dUniverse.getMolecule() == null
            || java3dUniverse.getMoleculeInterface().getNumberOfAtoms()
            < 1) {
          JOptionPane.showMessageDialog(null, "Load molecule first",
              ERROR_MESSAGE,
              JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (java3dUniverse.getMoleculeInterface().getNumberOfAtoms()
            > 100) {
          int n = JOptionPane.showConfirmDialog(dad,
              "Molecule has "
              + java3dUniverse.getMoleculeInterface().
              getNumberOfAtoms()
              + " - output could be lengthy.\n"
              + "Do you want to continue?\n",
              "Output could be lengthy",
              JOptionPane.YES_NO_OPTION);
          if (n == JOptionPane.NO_OPTION
              || n == JOptionPane.CLOSED_OPTION) {
            return;
          }
        }

        JShowText showResume = new JShowText("Interatomic Distances");
        showResume.setIconImage(tableImage.getImage());
        showResume.enableTextEditing(false);
        showResume.setSize(600, 640);
        showResume.setLocationRelativeTo(dad);
        showResume.setText(Molecule.distancesAsString(java3dUniverse.getMoleculeInterface()));
        showResume.setDefaultFileName("distances.txt");
        showResume.setVisible(true);
      }
    });
    jMenuAnalyze.add(jMenuItem);

    // --- Frequencies
    vibrationalAnalysis.setIcon(docPulseImage);
    jMenuAnalyze.add(vibrationalAnalysis);
    vibrationalAnalysis.addActionListener(processAnalyzeMenu);

    // --- MD Trajectories
    trajectoryAnalysis.setIcon(new ImageIcon(cct.resources.Resources.class.getResource(
            "/cct/images/icons16x16/line-chart.png")));
    trajectoryAnalysis.addActionListener(processAnalyzeMenu);

    jMenuAnalyze.add(trajectoryAnalysis);
    jMenuAnalyze.addSeparator();

    // --- Load Gaussian cube
    ImageIcon cubeIcon = new ImageIcon(cct.resources.Resources.class.getResource(
            "/cct/images/icons16x16/gaussian-cube-16x16.png"));
    loadGaussianCube.setIcon(cubeIcon);
    loadGaussianCube.addActionListener(processAnalyzeMenu);
    jMenuAnalyze.add(loadGaussianCube);

    manageGaussianCubes.setIcon(cubeIcon);
    manageGaussianCubes.addActionListener(processAnalyzeMenu);
    jMenuAnalyze.add(manageGaussianCubes);

  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    int whatToDo = this.getDefaultCloseOperation();
    switch (whatToDo) {
      case JFrame.EXIT_ON_CLOSE:
        System.exit(0);
        break;
      case JFrame.DISPOSE_ON_CLOSE:
        this.dispose();
        break;
      case JFrame.DO_NOTHING_ON_CLOSE:
        break;
      case JFrame.HIDE_ON_CLOSE:
        setVisible(false);
        break;
    }

  }

  /**
   * Help | About action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
    JEditorFrame_AboutBox dlg = new JEditorFrame_AboutBox(this, version,
        build);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
        (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }

  public void deleteMoleculeButton_actionPerformed(ActionEvent e) {
    Molecule m = new Molecule();
    java3dUniverse.addMolecule(m);
  }

  /**
   * Program tries to guess and open all supported molecular file formats
   *
   * @param e ActionEvent
   */
  //public void openFileButton_actionPerformed(ActionEvent e) {
  //  guessAndOpenFile(java3dUniverse);
  //}
  //@Deprecated
  //boolean guessAndOpenFile(Java3dUniverse j3d) {
  //  JFileChooser chooser = new JFileChooser();
  //  FileFilterImpl filter = new FileFilterImpl();
  //  String extensions = MolecularFileFormats.allMolecularExtensions;
  //  String temp[] = extensions.split(";");
  //  for (int i = 0; i < temp.length; i++) {
  //    filter.addExtension(temp[i]);
  //  }
  //  filter.setDescription(MolecularFileFormats.allMolecularFormats);
  //  chooser.setFileFilter(filter);
  //  chooser.setDialogType(JFileChooser.OPEN_DIALOG);
  //  chooser.setDialogTitle("Open Molecular Modeling File");
  //  File currentWorkingDirectory = GlobalSettings.getCurrentWorkingDirectory();
  //  if (currentWorkingDirectory != null) {
  //    chooser.setCurrentDirectory(currentWorkingDirectory);
  //  }
  //  int returnVal = chooser.showOpenDialog(this);
  //  if (returnVal == JFileChooser.APPROVE_OPTION) {
  //    String fileName = chooser.getSelectedFile().getPath();
  //    currentWorkingDirectory = chooser.getCurrentDirectory();
  //    GlobalSettings.setCurrentWorkingDirectory(currentWorkingDirectory);
  //    logger.info("You chose to open this file: "
  //            + fileName);
  //    String extension = FileFilterImpl.getExtension(fileName);
  //    int fileFormat = MolecularFileFormats.guessMolecularFileFormat(
  //            fileName, extension);
  //    if (fileFormat == MolecularFileFormats.formatUnknown) {
  //      JOptionPane.showMessageDialog(this, "Unknown Molecular Modeling File Format", ERROR_MESSAGE,
  //              JOptionPane.ERROR_MESSAGE);
  //      return false;
  //    }
  //    j3d.openMolecularModelingFile(fileFormat, fileName);
  //    return true;
  //  }
  //  return false;
  //}
  /**
   * Transforms coordinates of atoms of the arbitrary molecule into current virtual world space
   *
   * @param molec MoleculeInterface - Molecule
   * @return float[][] - cartesian coordinates float[number_of_atoms][3]
   */
  public float[][] getVirtualWorldCoordinates(MoleculeInterface molec) {
    return java3dUniverse.getVirtualWorldCoordinates(molec);
  }

  public String addMenuItem(String menuName, JMenuItem menuItem) {
    for (int i = 0; i < jamberooMenuBar.getMenuCount(); i++) {
      JMenu menu = jamberooMenuBar.getMenu(i);
      //logger.info("Name: "+menu.getName()+" ActionCommand: "+menu.getActionCommand());
      if (menuName.equalsIgnoreCase(menu.getActionCommand())) {
        menu.add(menuItem);
        return null;
      }
    }
    return "No menu with name " + menuName;
  }

  public JToolBar getJToolBar() {
    return mainToolbar;
  }

  public void stateChanged(ChangeEvent e) {

  }

  public void moleculeChanged(MoleculeEventObject event) {
    if (event.getMoleculeEvent() == MOLECULE_EVENT.MOLECULE_DELETED) {
      this.setTitle("");
    } else if (event.getMoleculeEvent() == MOLECULE_EVENT.MOLECULE_LOADED) {
      this.setTitle(event.getUrlString());
    }
  }

  public void actionPerformed(ActionEvent actionEvent) {
    Object source = actionEvent.getSource();

    if (jMenuDBLogin == source || jDBLogin == source) {

      if (java3dUniverse.connectToDatabase()) {
        jMenuDBLogin.setText("Save/Retrieve Data");
        jMenuDBLogin.setToolTipText("Save/Retrieve Data to/from Database");
        jMenuDBLogout.setEnabled(true);
      }
    } else if (jMenuDBLogout == actionEvent.getSource()) {
      java3dUniverse.logoutDatabase();
      jMenuDBLogin.setText("Login Database");
      jMenuDBLogin.setToolTipText("Login Database");
      jMenuDBLogout.setEnabled(false);
    }

    if (source == openFileButton) {
      fileMenu.openMoleculeFile("Open Molecule file", this.java3dUniverse, GlobalSettings.ADD_MOLECULE_MODE.SET);
      //fileMenu.o
      return;
    }

    String eventName = "";
    if (source instanceof JMenuItem) {
      JMenuItem jMenuItem = (JMenuItem) source;
      eventName = jMenuItem.getName();
    }

    // --- Open Molecule Menuitem
    if (false) {
    }
  }

  /**
   * Check whether menu with ActionCommand "menuName" has menu item with ActionCommand "menuItemName"
   *
   * @param menuName String - menu ActionCommand name
   * @param menuItemName String menu item ActionCommand name
   * @return boolean true - a menu has a given menu item, false otherwise
   * @throws Exception if there is no such menu in menubar
   */
  public boolean hasMenuItem(String menuName, String menuItemName) throws
      Exception {
    for (int i = 0; i < jamberooMenuBar.getMenuCount(); i++) {
      JMenu menu = jamberooMenuBar.getMenu(i);
      //logger.info("Name: "+menu.getName()+" ActionCommand: "+menu.getActionCommand());
      if (menuName.equalsIgnoreCase(menu.getActionCommand())) {
        for (int j = 0; j < menu.getItemCount(); j++) {
          JMenuItem item = menu.getItem(j);
          if (menuItemName.equalsIgnoreCase(item.getActionCommand())) {
            return true;
          }
        }
        return false;
      }
    }
    throw new Exception("No menu " + menuName + " in menu bar");
  }

  /**
   * Gets menu item with ActionCommand name "menuItemName" from the menu with ActionCommand name "menuName"
   *
   * @param menuName String
   * @param menuItemName String
   * @return JMenuItem
   * @throws Exception
   */
  public JMenuItem getMenuItem(String menuName, String menuItemName) throws
      Exception {
    for (int i = 0; i < jamberooMenuBar.getMenuCount(); i++) {
      JMenu menu = jamberooMenuBar.getMenu(i);

      if (menuName.equalsIgnoreCase(menu.getActionCommand())) {
        for (int j = 0; j < menu.getItemCount(); j++) {
          Object obj = null;
          try {
            obj = menu.getItem(j);
            //logger.info("Name: " + obj.getClass().getSimpleName());
          } catch (Exception ex) {
            //System.err.println(ex.getMessage());
            continue;
          }

          if (obj instanceof JMenuItem) {
            final JMenuItem item = (JMenuItem) obj;

            if (menuItemName.equalsIgnoreCase(item.getActionCommand())) {
              return item;
            }
          }
        }
        throw new Exception("No menu item " + menuItemName
            + " in menu " + menuName);
        //return null;
      }
    }
    throw new Exception("No menu " + menuName + " in menu bar");
  }

  public JMenuItem removeMenuItem(String menuName, String menuItemName) throws
      Exception {
    for (int i = 0; i < jamberooMenuBar.getMenuCount(); i++) {
      final JMenu menu = jamberooMenuBar.getMenu(i);

      if (menuName.equalsIgnoreCase(menu.getActionCommand())) {
        for (int j = 0; j < menu.getItemCount(); j++) {
          Object obj = null;
          try {
            obj = menu.getItem(j);
            //logger.info("Name: " + obj.getClass().getSimpleName());
          } catch (Exception ex) {
            //System.err.println(ex.getMessage());
            continue;
          }

          if (obj instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) obj;

            if (menuItemName.equalsIgnoreCase(item.getActionCommand())) {
              menu.remove(item);
              return item;
            }
          }
        }
        throw new Exception("No menu item " + menuItemName
            + " in menu " + menuName);
        //return null;
      }
    }
    throw new Exception("No menu " + menuName + " in menu bar");
  }

  public JMenu getMenu(String menuName) throws Exception {
    for (int i = 0; i < jamberooMenuBar.getMenuCount(); i++) {
      JMenu menu = jamberooMenuBar.getMenu(i);
      if (menuName.equalsIgnoreCase(menu.getActionCommand())) {
        return menu;
      }
    }
    return null;
  }

  public MoleculeInterface getMolecule() {
    return java3dUniverse.getMolecule();
  }

  public Java3dUniverse getJ3DUniverse() {
    return java3dUniverse;
  }

  public void setMolecule(Molecule m) {
    java3dUniverse.addMolecule(m);
  }

  public void setMolecule(MoleculeInterface m) {
    java3dUniverse.addMolecule(m);
  }

  public java.util.List getVolumeDataList() {
    return volumeDataList;
  }

  public void jButton3_actionPerformed(ActionEvent e) {
    //JavaHelp jh = new JavaHelp();
    //jh.showHelp();
  }

  class JEditorFrame_jButton3_actionAdapter
      implements ActionListener {

    private JamberooFrame adaptee;

    JEditorFrame_jButton3_actionAdapter(JamberooFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jButton3_actionPerformed(e);
    }
  }

  //class JEditorFrame_openFileButton_actionAdapter
  //        implements ActionListener {
  //  private JamberooFrame adaptee;
  //  JEditorFrame_openFileButton_actionAdapter(JamberooFrame adaptee) {
  //    this.adaptee = adaptee;
  //  }
  //  public void actionPerformed(ActionEvent e) {
  //    adaptee.openFileButton_actionPerformed(e);
  //  }
  //}
  class JEditorFrame_jMenuFileExit_ActionAdapter
      implements ActionListener {

    JamberooFrame adaptee;

    JEditorFrame_jMenuFileExit_ActionAdapter(JamberooFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
      adaptee.jMenuFileExit_actionPerformed(actionEvent);
    }
  }

  /**
   *
   * <p>
   * Title: Picking</p>
   *
   * <p>
   * Description: </p>
   *
   * <p>
   * Copyright: Copyright (c) 2005</p>
   *
   * <p>
   * Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  class JEditorFrame_jMenuAnalyze_ActionAdapter
      implements ActionListener {

    JamberooFrame adaptee;

    JEditorFrame_jMenuAnalyze_ActionAdapter(JamberooFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
      adaptee.jMenuAnalyze_actionPerformed(actionEvent);
    }
  }

  class JEditorFrame_jMenuHelpAbout_ActionAdapter
      implements ActionListener {

    JamberooFrame adaptee;

    JEditorFrame_jMenuHelpAbout_ActionAdapter(JamberooFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
      adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
    }
  }

  //class JEditorFrame_jMenuDB_ActionAdapter
  //    implements ActionListener {
  //
  //  JamberooFrame adaptee;
  //  JEditorFrame_jMenuDB_ActionAdapter(JamberooFrame adaptee) {
  //    this.adaptee = adaptee;
  //  }
  //  public void actionPerformed(ActionEvent actionEvent) {
  //    adaptee.jMenuDB_actionPerformed(actionEvent);
  //  }
  //}
  class JEditorFrame_deleteMoleculeButton_actionAdapter
      implements
      ActionListener {

    private JamberooFrame adaptee;

    JEditorFrame_deleteMoleculeButton_actionAdapter(JamberooFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.deleteMoleculeButton_actionPerformed(e);
    }
  }
}
