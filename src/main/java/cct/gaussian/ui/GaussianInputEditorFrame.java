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
import cct.resources.images.ImageResources;
import cct.tools.IOUtils;
import cct.tools.ui.FontSelectorDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * <p>Title: Gasussian Utility Classes</p>
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
public class GaussianInputEditorFrame
    extends JFrame implements ActionListener {

  static String FontNameKey = "FontName";
  static String FontSizeKey = "FontSize";
  static String FontStyleKey = "FontStyle";
  static String defaultFontName = "default";

  Preferences prefs;
  FontSelectorDialog fontSelector = null;
  GJFParserInterface gjfParser = null;
  int numberOfEntries = 1;
  int count = 0;
  java.util.List additionalEntries = new ArrayList();
  java.util.List entryLabels = new ArrayList();
  String fileName = "";
  String workingDirectory = "";
  //String rootDir = IOUtils.getRootDirectory(this);
  String protocol = "file:";
  String images16x16Path = "cct/images/icons16x16/";
  String images32x32Path = "cct/images/icons32x32/";
  //String path = protocol + rootDir + "cct/resources/images/icons32x32/";

  int size, style;
  String fontName;
  Font newFont;

  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JToolBar jToolBar = new JToolBar();
  JButton openFileButton = new JButton();
  JButton resetButton = new JButton();
  JButton jButton3 = new JButton();
  ImageIcon image1 = new ImageIcon(GaussianInputEditorFrame.class.
                                   getResource("openFile.png"));
  ImageIcon image2 = new ImageIcon(GaussianInputEditorFrame.class.
                                   getResource("closeFile.png"));
  ImageIcon image3 = new ImageIcon(GaussianInputEditorFrame.class.
                                   getResource("help.png"));
  JLabel statusBar = new JLabel();
  SimpleG03EditorPanel simpleG03EditorPanel1 = new SimpleG03EditorPanel(gjfParser);
  JPanel mainPanel = new JPanel();
  JPanel goStepsPanel = new JPanel();
  JButton previousStepButton = new JButton();
  JButton nextStepButton = new JButton();
  BorderLayout borderLayout2 = new BorderLayout();
  ImageIcon arrowUp = new ImageIcon(ImageResources.class.
                                    getResource("icons32x32/arrowUp.gif"));

  //ImageIcon arrowUp = new ImageIcon(IOUtils.getURL(protocol, this,
  //    images32x32Path, "arrowUp.gif"));

  ImageIcon arrowUpFaded = new ImageIcon(ImageResources.class.
                                         getResource(
                                             "icons32x32/arrowUpFaded.gif"));
  ImageIcon arrowDown = new ImageIcon(ImageResources.class.
                                      getResource("icons32x32/arrowDown.gif"));
  ImageIcon arrowDownFaded = new ImageIcon(ImageResources.class.
                                           getResource(
                                               "icons32x32/arrowDownFaded.gif"));
  ImageIcon deleteStep = new ImageIcon(ImageResources.class.
                                       getResource(
                                           "icons32x32/window-exit.png"));
  ImageIcon insertNewStep = new ImageIcon(ImageResources.class.
                                          getResource(
                                              "icons32x32/window-new.png"));
  ImageIcon openFile = new ImageIcon(ImageResources.class.
                                     getResource("icons16x16/openFile.png"));
  ImageIcon saveFile = new ImageIcon(ImageResources.class.
                                     getResource("icons16x16/saveFile.png"));

  ImageIcon saveStepInFile = new ImageIcon(ImageResources.class.
                                           getResource(
                                               "icons32x32/fileSaveAs.png"));
  ImageIcon okReturnBack = new ImageIcon(ImageResources.class.
                                         getResource(
                                             "icons32x32/button-ok.png"));

  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JPanel cardPanel = new JPanel();
  JPanel topPanel = new JPanel();
  CardLayout cardLayout1 = new CardLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JComboBox stepsComboBox = new JComboBox();
  FlowLayout flowLayout2 = new FlowLayout();
  JButton deleteEntryButton = new JButton();
  JButton insertEntryButton = new JButton();
  JLabel jLabel2 = new JLabel();
  JMenuItem jMenuItem1 = new JMenuItem();
  JLabel jLabel3 = new JLabel();
  JComboBox fileComboBox = new JComboBox();
  JButton openSelectedFileButton = new JButton();
  JButton saveFileButton = new JButton();
  JButton saveStepButton = new JButton();
  public JButton returnBackButton = new JButton();
  JMenu viewMenu = new JMenu();
  JMenuItem increaseMenuItem = new JMenuItem();
  JMenuItem decreaseMenuItem = new JMenuItem();
  JMenuItem jMenuItem4 = new JMenuItem();

  public GaussianInputEditorFrame(GJFParserInterface parser) {
    gjfParser = parser;
    simpleG03EditorPanel1.setGeometryValidator(gjfParser);
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Component initialization.
   *
   * @throws Exception
   */
  private void jbInit() throws Exception {
    /*
     logger.info("Root directory : " + rootDir + "\nPath: " + path);

           URL imagesURL = null;
           try {
       imagesURL = new URL(path + "fileSaveAs.png");
       saveStepInFile = new ImageIcon(imagesURL);
       imagesURL = new URL(path + "button-ok.png");
       okReturnBack = new ImageIcon(imagesURL);
           }
           catch (java.net.MalformedURLException ex) {
       System.err.println("Linking resource images: " + ex.getMessage());
           }
     */

    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    //setSize(new Dimension(400, 300));
    setTitle("Gaussian Input Editor");
    statusBar.setText(" ");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new
                                    GaussianInputEditorFrame_jMenuFileExit_ActionAdapter(this));
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new
                                     GaussianInputEditorFrame_jMenuHelpAbout_ActionAdapter(this));
    mainPanel.setLayout(borderLayout2);
    previousStepButton.setPreferredSize(new Dimension(41, 41));
    previousStepButton.setToolTipText("Previous Step");
    previousStepButton.setIcon(arrowUp);
    previousStepButton.addActionListener(new
                                         GaussianInputEditorFrame_previousStepButton_actionAdapter(this));
    nextStepButton.setPreferredSize(new Dimension(41, 41));
    nextStepButton.setToolTipText("Next Step");
    nextStepButton.setIcon(arrowDown);
    nextStepButton.addActionListener(new
                                     GaussianInputEditorFrame_nextStepButton_actionAdapter(this));
    cardPanel.setLayout(cardLayout1);
    goStepsPanel.setLayout(gridBagLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel1.setText("   Step: ");
    deleteEntryButton.setPreferredSize(new Dimension(41, 41));
    deleteEntryButton.setToolTipText("Delete this Step");
    deleteEntryButton.setIcon(new ImageIcon(ImageResources.class.
                                            getResource(
                                                "icons32x32/window-exit.png")));
    deleteEntryButton.addActionListener(new
                                        GaussianInputEditorFrame_jButton6_actionAdapter(this));
    insertEntryButton.setPreferredSize(new Dimension(41, 41));
    insertEntryButton.setToolTipText("Insert New Step");
    insertEntryButton.setIcon(new ImageIcon(ImageResources.class.
                                            getResource(
                                                "icons32x32/window-new.png")));
    insertEntryButton.addActionListener(new
                                        GaussianInputEditorFrame_insertEntryButton_actionAdapter(this));
    jLabel2.setToolTipText("");
    jLabel2.setHorizontalAlignment(SwingConstants.LEFT);
    openFileButton.addActionListener(new
                                     GaussianInputEditorFrame_openFileButton_actionAdapter(this));
    stepsComboBox.addItemListener(new
                                  GaussianInputEditorFrame_stepsComboBox_itemAdapter(this));
    stepsComboBox.setMaximumSize(new Dimension(100, 19));
    stepsComboBox.setPreferredSize(new Dimension(60, 19));
    stepsComboBox.setToolTipText("Select Step to Edit");
    jMenuItem1.setText("Open File");
    jMenuItem1.addActionListener(new
                                 GaussianInputEditorFrame_jMenuItem1_actionAdapter(this));
    jLabel3.setToolTipText("");
    jLabel3.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel3.setText("File: ");
    fileComboBox.setToolTipText("Select Step to Edit");
    openSelectedFileButton.setMaximumSize(new Dimension(25, 25));
    openSelectedFileButton.setMinimumSize(new Dimension(25, 25));
    openSelectedFileButton.setPreferredSize(new Dimension(25, 25));
    openSelectedFileButton.setToolTipText("Click to Open Selected File");
    openSelectedFileButton.setBorderPainted(false);
    openSelectedFileButton.setIcon(openFile);
    openSelectedFileButton.setMnemonic('0');
    openSelectedFileButton.addActionListener(new
                                             GaussianInputEditorFrame_openSelectedFileButton_actionAdapter(this));
    saveFileButton.setToolTipText("Save Gaussian Job File");
    saveFileButton.setIcon(saveFile);
    saveFileButton.addActionListener(new
                                     GaussianInputEditorFrame_saveFileButton_actionAdapter(this));
    saveStepButton.setPreferredSize(new Dimension(41, 41));
    saveStepButton.setToolTipText("Save this Step in Separate File");
    saveStepButton.setIcon(saveStepInFile);
    saveStepButton.addActionListener(new
                                     GaussianInputEditorFrame_saveStepButton_actionAdapter(this));
    resetButton.addActionListener(new
                                  GaussianInputEditorFrame_resetButton_actionAdapter(this));
    returnBackButton.setPreferredSize(new Dimension(41, 41));
    returnBackButton.setToolTipText(
        "Select current Step and return back to the Parent Window");
    returnBackButton.setVisible(false);
    returnBackButton.setIcon(okReturnBack);
    viewMenu.setToolTipText("Adjust Text Appearance");
    viewMenu.setText("View");
    increaseMenuItem.setToolTipText("");
    increaseMenuItem.setText("Increase Font Size");
    increaseMenuItem.addActionListener(this);
    decreaseMenuItem.setToolTipText("");
    decreaseMenuItem.setText("Decrease Font Size");
    decreaseMenuItem.addActionListener(this);
    jMenuItem4.setToolTipText("");
    jMenuItem4.setText("Select Font");
    jMenuItem4.addActionListener(this);
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuItem1);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuFileExit);
    jMenuBar1.add(viewMenu);
    jMenuBar1.add(jMenuHelp);
    jMenuHelp.add(jMenuHelpAbout);
    setJMenuBar(jMenuBar1);
    openFileButton.setIcon(image1);
    openFileButton.setToolTipText("Open Gaussian Job File");
    resetButton.setIcon(image2);
    resetButton.setToolTipText("Close File");
    jButton3.setIcon(image3);
    jButton3.setToolTipText("Help");
    jToolBar.add(openFileButton);
    jToolBar.add(resetButton);
    jToolBar.add(saveFileButton);
    jToolBar.add(jButton3);
    mainPanel.add(topPanel, BorderLayout.NORTH);
    topPanel.add(jLabel3);
    topPanel.add(fileComboBox);
    topPanel.add(openSelectedFileButton);
    topPanel.add(jLabel1);
    topPanel.add(stepsComboBox);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    topPanel.setLayout(flowLayout2);
    contentPane.add(statusBar, BorderLayout.SOUTH);
    mainPanel.add(cardPanel, BorderLayout.CENTER);
    cardPanel.add(simpleG03EditorPanel1, "step0");
    contentPane.add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(goStepsPanel, BorderLayout.EAST);
    contentPane.add(jToolBar, BorderLayout.NORTH);
    goStepsPanel.add(jLabel2, new GridBagConstraints(0, 6, 3, 1, 0.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
        new Insets(0, 0, 0, 0), 0, 0));
    goStepsPanel.add(saveStepButton,
                     new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(2, 2, 2, 2), 0, 0));
    goStepsPanel.add(deleteEntryButton,
                     new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(2, 2, 2, 2), 0, 0));
    goStepsPanel.add(insertEntryButton,
                     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(2, 2, 2, 2), 0, 0));
    goStepsPanel.add(nextStepButton,
                     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(2, 2, 2, 2), 0, 0));
    goStepsPanel.add(previousStepButton,
                     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(0, 2, 2, 2), 0, 0));
    goStepsPanel.add(returnBackButton
                     , new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
                                              , GridBagConstraints.CENTER,
                                              GridBagConstraints.NONE,
                                              new Insets(2, 2, 2, 2), 0, 0));
    viewMenu.add(increaseMenuItem);
    viewMenu.add(decreaseMenuItem);
    viewMenu.addSeparator();
    viewMenu.add(jMenuItem4);
    stepsComboBox.addItem("     " + String.valueOf(numberOfEntries));

    // --- Retrieving font properties

    Font currentFont = simpleG03EditorPanel1.getFontForText();
    size = currentFont.getSize();
    style = currentFont.getStyle();
    fontName = currentFont.getFontName();

    try {
      prefs = Preferences.userNodeForPackage(this.getClass());
      fontName = prefs.get(FontNameKey, currentFont.getFontName());
      size = prefs.getInt(FontSizeKey, 12);
      style = prefs.getInt(FontStyleKey, 0);
    }
    catch (Exception ex) {
      System.err.println("Error retrieving Font Preferences: " +
                         ex.getMessage());
      return;
    }

    newFont = new Font(fontName, style, size);
    simpleG03EditorPanel1.setFontForText(newFont);

    this.pack();
  }

  public JMenu getFileMenu() {
    return jMenuFile;
  }

  public JToolBar getToolBar() {
    return jToolBar;
  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    System.exit(0);
  }

  public String addMenuItem(String menuName, JMenuItem menuItem) {
    for (int i = 0; i < jMenuBar1.getMenuCount(); i++) {
      JMenu menu = jMenuBar1.getMenu(i);
      //logger.info("Name: "+menu.getName()+" ActionCommand: "+menu.getActionCommand());
      if (menuName.equalsIgnoreCase(menu.getActionCommand())) {
        menu.add(menuItem);
        return null;
      }
    }
    return "No menu with name " + menuName;
  }

  public void removeMenuItem(String menuName, String menuItemName) throws
      Exception {
    for (int i = 0; i < jMenuBar1.getMenuCount(); i++) {
      JMenu menu = jMenuBar1.getMenu(i);
      if (menu == null) {
        continue;
      }
      //logger.info("Name: "+menu.getName()+" ActionCommand: "+menu.getActionCommand());
      if (menuName.equalsIgnoreCase(menu.getActionCommand())) {
        for (int j = 0; j < menu.getItemCount(); j++) {
          JMenuItem item = null;
          try {
            item = menu.getItem(j);
          }
          catch (Exception ex) {
            continue;
          }
          if (item == null) {
            continue;
          }
          if (item.getText().equalsIgnoreCase(menuItemName)) {
            menu.remove(item);
            return;
          }
        }
      }
    }
    throw new Exception("No menu item " + menuItemName + " in the menu " +
                        menuName);
  }

  /**
   * Help | About action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
    GaussianInputEditorFrame_AboutBox dlg = new
        GaussianInputEditorFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }

  public void openFileButton_actionPerformed(ActionEvent e) {
    FileDialog fd = new FileDialog(new Frame(), "Open Gaussian Job File File",
                                   FileDialog.LOAD);
    fd.setFile("*.gjf;*.com;*.g03");
    fd.setVisible(true);
    if (fd.getFile() == null) {
      return;
    }

    fileName = new String(fd.getFile());
    workingDirectory = new String(fd.getDirectory());

    parseNewFile(workingDirectory + fileName);

  }

  public void setupMolecule(MoleculeInterface mol) {
    if (gjfParser != null) {
      gjfParser.removeAllEntries();
    }
    simpleG03EditorPanel1.setupMolecule(mol);
  }

  /**
   *
   * @param gjfFile String
   */
  void parseNewFile(String gjfFile) {
    gjfParser.removeAllEntries();
    int n = gjfParser.parseGJF(gjfFile, 0);

    if (n < 1) {
      JOptionPane.showMessageDialog(null,
                                    "No entries found in the input file",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    fileComboBox.setEnabled(false);
    fileComboBox.addItem(gjfFile);
    fileComboBox.setSelectedIndex(fileComboBox.getItemCount() - 1);
    fileComboBox.setEnabled(true);

    setupEditor(gjfParser);

  }

  public void setupEditor(GJFParserInterface parser) {

    if (parser == null || parser.getNumberOfSteps() < 1) {
      JOptionPane.showMessageDialog(null,
                                    "No entries found in the input file",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    int n = parser.getNumberOfSteps();

// --- Clean up old stuff

    if (stepsComboBox.getItemCount() > 1) {
      CardLayout cl = (CardLayout) cardPanel.getLayout();
      cl.show(cardPanel, "step0");
      for (int i = stepsComboBox.getItemCount() - 1; i > 0; i--) {
        cardPanel.remove(i);
      }
      additionalEntries.clear();
      entryLabels.clear();
    }

// --- Fill in a new contents

    java.util.List buffer = parser.getLinkZeroCommands(0);
    simpleG03EditorPanel1.setLinkZeroCommands(buffer);
    simpleG03EditorPanel1.setRouteSection(parser.getRouteSection(0));
    simpleG03EditorPanel1.setTitleSection(parser.getTitleSection(0));
    simpleG03EditorPanel1.setChargeSection(parser.getChargeSection(0));
    simpleG03EditorPanel1.setMoleculeSpecsSection(parser.
                                                  getMoleculeSpecsSection(0));

    deleteEntryButton.setEnabled(true);

    stepsComboBox.setEnabled(false);
    stepsComboBox.removeAllItems();
    setStep(stepsComboBox, 0 );
//stepsComboBox.addItem("     1");
    previousStepButton.setIcon(arrowUpFaded);
    previousStepButton.setEnabled(false);
    stepsComboBox.setEnabled(true);

    if (n == 1) {
      nextStepButton.setIcon(arrowDownFaded);
      nextStepButton.setEnabled(false);
      return;
    }

    nextStepButton.setEnabled(true);
    nextStepButton.setIcon(arrowDown);

    stepsComboBox.setEnabled(false);
    for (int i = 1; i < n; i++) {
      SimpleG03EditorPanel sG03 = new SimpleG03EditorPanel(parser);
      sG03.setFontForText(newFont);
      buffer = parser.getLinkZeroCommands(i);
      sG03.setLinkZeroCommands(buffer);
      sG03.setRouteSection(parser.getRouteSection(i));
      sG03.setTitleSection(parser.getTitleSection(i));
      sG03.setChargeSection(parser.getChargeSection(i));
      sG03.setMoleculeSpecsSection(parser.getMoleculeSpecsSection(i));

      cardPanel.add(sG03, "step" + String.valueOf(i));

      setStep(stepsComboBox, i);
    }

    setActiveStep(0);

    stepsComboBox.setEnabled(true);
    numberOfEntries = n;

  }

  void setStep(JComboBox cb, int i) {
    boolean store = cb.isEnabled();
    cb.setEnabled(false);

    String zeros = "";
    if (i + 1 < 10) {
      zeros = "     ";
    }
    else if (i + 1 < 100) {
      zeros = "    ";
    }
    else if (i + 1 < 1000) {
      zeros = "   ";
    }

    if (i == 0) {
      cb.addItem(zeros + String.valueOf( (i + 1)));
    }
    else {
      cb.insertItemAt(zeros + String.valueOf( (i + 1)), i);
    }
    //

    cb.setEnabled(store);
  }

  public void stepsComboBox_itemStateChanged(ItemEvent e) {
    if (!stepsComboBox.isEnabled()) {
      return;
    }

    stepsComboBox.setEnabled(false);
    setActiveStep(stepsComboBox.getSelectedIndex());
    stepsComboBox.setEnabled(true);
    /*
     String card = "step" + String.valueOf(stepsComboBox.getSelectedIndex());
         CardLayout cl = (CardLayout) cardPanel.getLayout();
         cl.show(cardPanel, card);
         int items = stepsComboBox.getItemCount();
         int n = stepsComboBox.getSelectedIndex();

         if (items == 1) {
      previousStepButton.setIcon(arrowUpFaded);
      previousStepButton.setEnabled(false);
      nextStepButton.setIcon(arrowDownFaded);
      nextStepButton.setEnabled(false);
         }
         // At the beginning
         else if (n == 0) {
      previousStepButton.setIcon(arrowUpFaded);
      previousStepButton.setEnabled(false);
      nextStepButton.setIcon(arrowDown);
      nextStepButton.setEnabled(true);
         }

         // At the end
         else if (n == items - 1) {
      previousStepButton.setIcon(arrowUp);
      previousStepButton.setEnabled(true);
      nextStepButton.setIcon(arrowDownFaded);
      nextStepButton.setEnabled(false);
         }
         // In the middle

         else if (items > 1) {
      previousStepButton.setIcon(arrowUp);
      previousStepButton.setEnabled(true);
      nextStepButton.setIcon(arrowDown);
      nextStepButton.setEnabled(true);
         }
     */
  }

  public void previousStepButton_actionPerformed(ActionEvent e) {
    if (!previousStepButton.isEnabled()) {
      return;
    }

    int n = stepsComboBox.getSelectedIndex();
    stepsComboBox.setSelectedIndex(n - 1);
  }

  public void setActiveStep(int nstep) {
    if (nstep < 0 || nstep >= stepsComboBox.getItemCount()) {
      return;
    }

    String card = "step" + String.valueOf(stepsComboBox.getSelectedIndex());
    CardLayout cl = (CardLayout) cardPanel.getLayout();
    cl.show(cardPanel, card);
    int items = stepsComboBox.getItemCount();
    int n = stepsComboBox.getSelectedIndex();

    if (items == 1) {
      previousStepButton.setIcon(arrowUpFaded);
      previousStepButton.setEnabled(false);
      nextStepButton.setIcon(arrowDownFaded);
      nextStepButton.setEnabled(false);
    }
    // At the beginning
    else if (n == 0) {
      previousStepButton.setIcon(arrowUpFaded);
      previousStepButton.setEnabled(false);
      nextStepButton.setIcon(arrowDown);
      nextStepButton.setEnabled(true);
    }

    // At the end
    else if (n == items - 1) {
      previousStepButton.setIcon(arrowUp);
      previousStepButton.setEnabled(true);
      nextStepButton.setIcon(arrowDownFaded);
      nextStepButton.setEnabled(false);
    }
    // In the middle

    else if (items > 1) {
      previousStepButton.setIcon(arrowUp);
      previousStepButton.setEnabled(true);
      nextStepButton.setIcon(arrowDown);
      nextStepButton.setEnabled(true);
    }

    stepsComboBox.setSelectedIndex(nstep);
  }

  public void nextStepButton_actionPerformed(ActionEvent e) {
    if (!nextStepButton.isEnabled()) {
      return;
    }

    int n = stepsComboBox.getSelectedIndex();
    stepsComboBox.setSelectedIndex(n + 1);

  }

  public void jMenuItem1_actionPerformed(ActionEvent e) {
    openFileButton_actionPerformed(e);
  }

  public void jButton6_actionPerformed(ActionEvent e) {

    int index = stepsComboBox.getSelectedIndex();
    SimpleG03EditorPanel ref = getSimpleEditor(index);
    if (!ref.isEdited()) {
      return;
    }

    JOptionPane optionPane = new JOptionPane(
        "Confirm deletion",
        JOptionPane.QUESTION_MESSAGE,
        JOptionPane.YES_NO_OPTION);
    optionPane.setVisible(true);
    int n =
        JOptionPane.showConfirmDialog(null,
                                      "Are you sure to delete this step?",
                                      "Confirm Removal",
                                      JOptionPane.YES_NO_OPTION);
    if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
      return;
    }

    n = stepsComboBox.getItemCount();

    if (n == 1 || index == 0) {
      simpleG03EditorPanel1.resetPanel();
      return;
    }

    removeEntry(index);
  }

  void resetEntry(SimpleG03EditorPanel sG03) {
    sG03.resetPanel();
  }

  void removeEntry(int step) {
    stepsComboBox.setEnabled(false);
    cardPanel.remove(step);
    stepsComboBox.removeItemAt(step);

    for (int i = stepsComboBox.getItemCount() - 1; i >= step; i--) {
      Component comp = cardPanel.getComponent(i);
      cardPanel.remove(i);
      stepsComboBox.removeItemAt(i);
      cardPanel.add(comp, "step" + String.valueOf(i), i);
      setStep(stepsComboBox, i);
    }

    stepsComboBox.setEnabled(true);

    if (step >= stepsComboBox.getItemCount()) {
      setActiveStep(step - 1);
    }
    else {
      setActiveStep(step);
    }
  }

  public void insertEntryButton_actionPerformed(ActionEvent e) {
    JOptionPane optionPane = new JOptionPane(
        "Confirm copy of the previous step",
        JOptionPane.QUESTION_MESSAGE,
        JOptionPane.YES_NO_OPTION);
    optionPane.setVisible(true);
    int n =
        JOptionPane.showConfirmDialog(null,
                                      "Copy fields from the previous step?",
                                      "Confirm Copy",
                                      JOptionPane.YES_NO_OPTION);
    if (n == JOptionPane.CLOSED_OPTION) {
      return;
    }

    SimpleG03EditorPanel sG03 = new SimpleG03EditorPanel(gjfParser);
    sG03.setFontForText(newFont);

    int index = stepsComboBox.getSelectedIndex();

    if (n == JOptionPane.YES_OPTION) {
      SimpleG03EditorPanel ref = getSimpleEditor(index);
      sG03.setLinkZeroCommands(ref.getLinkZeroCommands());
      sG03.setRouteSection(ref.getRouteSection());
      sG03.setTitleSection(ref.getTitleSection());
      sG03.setChargeSection(ref.getChargeSection());
      sG03.setMoleculeSpecsSection(ref.getMoleculeSpecsSection());
    }

    insertEntry(sG03, index);
  }

  void insertEntry(SimpleG03EditorPanel sG03, int n) {
    stepsComboBox.setEnabled(false);

    int nitems = stepsComboBox.getItemCount();

    if (nitems == 1 || nitems - 1 == n) {
      cardPanel.add(sG03, "step" + String.valueOf( (n + 1)));
    }
    else {
      cardPanel.add(sG03, "step" + String.valueOf( (n + 1)), n + 1);
    }
    setStep(stepsComboBox, nitems);

    for (int i = n + 1; i < stepsComboBox.getItemCount(); i++) {
      Component comp = cardPanel.getComponent(i);
      cardPanel.remove(i);
      stepsComboBox.removeItemAt(i);
      cardPanel.add(comp, "step" + String.valueOf(i), i);
      setStep(stepsComboBox, i);
    }

    stepsComboBox.setEnabled(true);

    setActiveStep(n + 1);

  }

  SimpleG03EditorPanel getSimpleEditor(int n) {
    SimpleG03EditorPanel reference = (SimpleG03EditorPanel) cardPanel.
        getComponent(n);
    return reference;
  }

  public void saveFileButton_actionPerformed(ActionEvent e) {
    int warning = 0;
    //for (int i = 0; i < stepsComboBox.getItemCount(); i++) {
    //  SimpleG03EditorPanel ref = getSimpleEditor(i);
    //  if (ref.isEdited()) {
    //    warning++;
    //  }
    //}

    if (warning > 0) {
      JOptionPane optionPane = new JOptionPane(
          "Confirm deletion",
          JOptionPane.QUESTION_MESSAGE,
          JOptionPane.YES_NO_OPTION);
      optionPane.setVisible(true);
      int n =
          JOptionPane.showConfirmDialog(null,
                                        String.valueOf(warning) +
                                        " steps are empty. Save file while skipping empty steps?",
                                        "Confirm Saving",
                                        JOptionPane.YES_NO_OPTION);
      if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
        return;
      }
    }

    // --- Start saving, prepare a string

    // --- Select and save file

    saveFileAs(workingDirectory, fileName, getGJFAsString());

  }

  public String toString() {
    return getGJFAsString();
  }

  public String getGJFAsString() {
    StringWriter sWriter = new StringWriter();

    for (int i = 0; i < stepsComboBox.getItemCount(); i++) {
      SimpleG03EditorPanel ref = getSimpleEditor(i);
      if (i > 0) {
        sWriter.write("--Link1--\n");
      }
      sWriter.write(ref.getStepAsString());
    }

    String text = sWriter.toString();

    try {
      sWriter.close();
    }
    catch (Exception ex) {}

    return text;
  }

  void saveFileAs(String directory, String file_name, String text) {

    FileDialog fd = new FileDialog(new Frame(), "Save Gaussian Job File",
                                   FileDialog.SAVE);
    fd.setDirectory(directory);
    fd.setFile(file_name);
    fd.setVisible(true);
    if (fd.getFile() == null) {
      return;
    }

    String newFileName = new String(fd.getFile());
    String newWorkingDirectory = new String(fd.getDirectory());

    try {
      IOUtils.saveStringIntoFile(text, newWorkingDirectory + newFileName);
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
                                    ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    fileName = newFileName;
    workingDirectory = newWorkingDirectory;

  }

  public void saveStepButton_actionPerformed(ActionEvent e) {
    // --- Start saving, prepare a string

    StringWriter sWriter = new StringWriter();

    int index = stepsComboBox.getSelectedIndex();
    SimpleG03EditorPanel ref = getSimpleEditor(index);
    sWriter.write(ref.getStepAsString());

    // --- Select and save file

    String stepFile = fileName.substring(0, fileName.lastIndexOf("."));
    stepFile += "-" + String.valueOf( (index + 1)) + ".gjf";

    saveFileAs(workingDirectory, stepFile, sWriter.toString());

    try {
      sWriter.close();
    }
    catch (Exception ex) {}

  }

  public void openSelectedFileButton_actionPerformed(ActionEvent e) {
    if (fileComboBox.getItemCount() < 1) {
      return;
    }
    String file_name = (String) fileComboBox.getSelectedItem();

    if (file_name.equals(workingDirectory + fileName)) {
      JOptionPane optionPane = new JOptionPane(
          "Confirm deletion",
          JOptionPane.QUESTION_MESSAGE,
          JOptionPane.YES_NO_OPTION);
      optionPane.setVisible(true);
      int n =
          JOptionPane.showConfirmDialog(null,
                                        "Reload " + file_name,
                                        "Confirm reloading file",
                                        JOptionPane.YES_NO_OPTION);
      if (n == JOptionPane.NO_OPTION || n == JOptionPane.CLOSED_OPTION) {
        return;
      }
    }

    parseNewFile(file_name);
  }

  public void resetButton_actionPerformed(ActionEvent e) {

    CardLayout cl = (CardLayout) cardPanel.getLayout();
    cl.show(cardPanel, "step0");

    if (stepsComboBox.getItemCount() > 1) {
      for (int i = stepsComboBox.getItemCount() - 1; i > 0; i--) {
        cardPanel.remove(i);
      }
      additionalEntries.clear();
      entryLabels.clear();
    }

    simpleG03EditorPanel1.resetPanel();

    deleteEntryButton.setEnabled(true);

    stepsComboBox.setEnabled(false);
    stepsComboBox.removeAllItems();
    setStep(stepsComboBox, 0 );
    previousStepButton.setIcon(arrowUpFaded);
    previousStepButton.setEnabled(false);
    stepsComboBox.setEnabled(true);

    nextStepButton.setIcon(arrowDownFaded);
    nextStepButton.setEnabled(false);
  }

  public int getSelectedStep() {
    return stepsComboBox.getSelectedIndex();
  }

  class GaussianInputEditorFrame_saveStepButton_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_saveStepButton_actionAdapter(
        GaussianInputEditorFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.saveStepButton_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_openSelectedFileButton_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_openSelectedFileButton_actionAdapter(
        GaussianInputEditorFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.openSelectedFileButton_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_saveFileButton_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_saveFileButton_actionAdapter(
        GaussianInputEditorFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.saveFileButton_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_resetButton_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_resetButton_actionAdapter(
        GaussianInputEditorFrame
        adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.resetButton_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_insertEntryButton_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_insertEntryButton_actionAdapter(
        GaussianInputEditorFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.insertEntryButton_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_jButton6_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_jButton6_actionAdapter(GaussianInputEditorFrame
        adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jButton6_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_jMenuItem1_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_jMenuItem1_actionAdapter(
        GaussianInputEditorFrame
        adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.jMenuItem1_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_nextStepButton_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_nextStepButton_actionAdapter(
        GaussianInputEditorFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.nextStepButton_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_previousStepButton_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_previousStepButton_actionAdapter(
        GaussianInputEditorFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.previousStepButton_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_stepsComboBox_itemAdapter
      implements ItemListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_stepsComboBox_itemAdapter(
        GaussianInputEditorFrame
        adaptee) {
      this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
      adaptee.stepsComboBox_itemStateChanged(e);
    }
  }

  class GaussianInputEditorFrame_openFileButton_actionAdapter
      implements ActionListener {
    private GaussianInputEditorFrame adaptee;
    GaussianInputEditorFrame_openFileButton_actionAdapter(
        GaussianInputEditorFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
      adaptee.openFileButton_actionPerformed(e);
    }
  }

  class GaussianInputEditorFrame_jMenuFileExit_ActionAdapter
      implements ActionListener {
    GaussianInputEditorFrame adaptee;

    GaussianInputEditorFrame_jMenuFileExit_ActionAdapter(
        GaussianInputEditorFrame
        adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
      adaptee.jMenuFileExit_actionPerformed(actionEvent);
    }
  }

  class GaussianInputEditorFrame_jMenuHelpAbout_ActionAdapter
      implements ActionListener {
    GaussianInputEditorFrame adaptee;

    GaussianInputEditorFrame_jMenuHelpAbout_ActionAdapter(
        GaussianInputEditorFrame adaptee) {
      this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
      adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
    }
  }

  public void actionPerformed(ActionEvent actionEvent) {
    if (actionEvent.getSource() == increaseMenuItem) {

      Font currentFont = this.simpleG03EditorPanel1.getFontForText();
      int size = currentFont.getSize() + 1;
      newFont = new Font(currentFont.getName(), currentFont.getStyle(),
                         size);
      simpleG03EditorPanel1.setFontForText(newFont);
      saveFontPreferences();
    }
    else if (actionEvent.getSource() == decreaseMenuItem) {
      Font currentFont = simpleG03EditorPanel1.getFontForText();
      int size = currentFont.getSize();
      size = size > 8 ? size - 1 : size;
      newFont = new Font(currentFont.getName(), currentFont.getStyle(),
                         size);
      simpleG03EditorPanel1.setFontForText(newFont);
      saveFontPreferences();
    }
    else if (actionEvent.getSource() == jMenuItem4) {
      if (fontSelector == null) {
        fontSelector = new FontSelectorDialog(this, "Select Font", true);
      }
      fontSelector.setVisible(true);
      if (!fontSelector.isOKPressed()) {
        return;
      }
      newFont = new Font(fontSelector.getFontName(),
                         fontSelector.getFontStyle(),
                         fontSelector.getFontSize());
      simpleG03EditorPanel1.setFontForText(newFont);
      saveFontPreferences();
    }
  }

  private void saveFontPreferences() {
    Font currentFont = simpleG03EditorPanel1.getFontForText();
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

}
