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

package cct.amber;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import cct.tools.FortranNamelist;
import cct.tools.IOUtils;

/**
 * <p>Title: Preparation of input file for Sander 9 program</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class Sander9Frame
    extends JFrame implements SanderInputParserInterface {
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JToolBar jToolBar = new JToolBar();
  JButton openFile_Button = new JButton();
  JButton newFile_Button = new JButton();
  JButton help_Button = new JButton();
  ImageIcon image1 = new ImageIcon(Sander9Frame.class.getResource(
      "openFile.png"));
  ImageIcon image2 = new ImageIcon(Sander9Frame.class.getResource(
      "closeFile.png"));
  ImageIcon saveFileImage = new ImageIcon(Sander9Frame.class.
                                          getResource(
                                              "Save-Icon-16x16.gif"));
  ImageIcon image3 = new ImageIcon(Sander9Frame.class.getResource(
      "help.png"));
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel jobTypePanel = new JPanel();
  JPanel potentialFuncPanel = new JPanel();
  JTextArea descriptionPane = new JTextArea();
  JPanel descrPanel = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();

  Sander9JobControl s8jc = null;
  FortranNamelist cntrl = null;
  String fileContent = null;
  String fileName = null;
  String workingDirectory = null;
  TextEditorFrame inputEditor = null;
  Map controlsTable = new HashMap();
  boolean comboBoxAdjusting = false;

  JComboBox NTF_ComboBox = new JComboBox();
  JComboBox NTB_ComboBox = new JComboBox();
  JTextField DIELC_TextField = new JTextField();
  JTextField CUT_TextField = new JTextField();
  JTextField SCNB_TextField = new JTextField();
  JTextField SCEE_TextField = new JTextField();
  JTextField NSNB_TextField = new JTextField();
  JComboBox IPOL_ComboBox = new JComboBox();
  JPanel GB_Panel = new JPanel();
  JPanel PME_Panel = new JPanel();
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout5 = new BorderLayout();
  JComboBox IMIN_ComboBox = new JComboBox();
  JPanel ioPanel = new JPanel();
  JPanel outputPanel = new JPanel();
  JPanel inputPanel = new JPanel();
  BorderLayout borderLayout6 = new BorderLayout();
  JPanel restrainPanel = new JPanel();
  JPanel cardsPanel = new JPanel();
  CardLayout cardLayout1 = new CardLayout();
  JPanel minCard = new JPanel();
  JPanel mdCard = new JPanel();
  JLabel methodLabel = new JLabel();
  JComboBox NTMIN_ComboBox = new JComboBox();
  JLabel maxIterLabel = new JLabel();
  JTextField MAXCYC_TextField = new JTextField();
  JLabel dxLabel = new JLabel();
  JLabel switchLabel = new JLabel();
  JLabel covergLabel = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JTextField NCYC_TextField = new JTextField();
  JTextField DX0_TextField = new JTextField();
  JTextField DRMS_TextField = new JTextField();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JComboBox IREST_ComboBox = new JComboBox();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JLabel NSTLIM_Label = new JLabel();
  JTextField NSTLIM_TextField = new JTextField();
  JLabel TEMP0_Label = new JLabel();
  JTextField TEMP0_TextField = new JTextField();
  JLabel DT_Label = new JLabel();
  JTextField DT_TextField = new JTextField();
  JPanel MolDynPanel = new JPanel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JLabel NSCM_Label = new JLabel();
  JTextField NSCM_TextField = new JTextField();
  JLabel NRESPA_Label = new JLabel();
  JTextField NRESPA_TextField = new JTextField();
  JLabel T_Label = new JLabel();
  JTextField T_TextField = new JTextField();
  JLabel NTT_Label = new JLabel();
  JComboBox NTT_ComboBox = new JComboBox();
  JLabel TEMP0LES_Label = new JLabel();
  JTextField TEMP0LES_TextField = new JTextField();
  JLabel TEMPI_Label = new JLabel();
  JTextField TEMPI_TextField = new JTextField();
  JLabel IG_Label = new JLabel();
  JTextField IG_TextField = new JTextField();
  JLabel TAUTP_Label = new JLabel();
  JTextField TAUTP_TextField = new JTextField();
  JLabel GAMMA_LN_Label = new JLabel();
  JTextField GAMMA_LN_TextField = new JTextField();
  JLabel VRAND_Label = new JLabel();
  JTextField VRAND_TextField = new JTextField();
  JLabel VLIMIT_Label = new JLabel();
  JTextField VLIMIT_TextField = new JTextField();
  JPanel TempControlPanel = new JPanel();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  JLabel NTP_Label = new JLabel();
  JComboBox NTP_ComboBox = new JComboBox();
  JLabel PRES0_Label = new JLabel();
  JTextField PRES0_TextField = new JTextField();
  JLabel COMP_Label = new JLabel();
  JTextField COMP_TextField = new JTextField();
  JPanel PressureControlPanel = new JPanel();
  JLabel TAUP_Label = new JLabel();
  JTextField TAUP_TextField = new JTextField();
  JLabel NSNB_Label = new JLabel();
  JLabel SCEE_Label = new JLabel();
  JLabel IPOL_Label = new JLabel();
  JLabel NTF_Label = new JLabel();
  JLabel NTB_Label = new JLabel();
  JLabel NTXO_Label = new JLabel();
  JComboBox NTXO_ComboBox = new JComboBox();
  JLabel NTPR_Label = new JLabel();
  JTextField NTPR_TextField = new JTextField();
  JLabel NTX_Label = new JLabel();
  JComboBox NTX_ComboBox = new JComboBox();
  JLabel NTRX_Label = new JLabel();
  JComboBox NTRX_ComboBox = new JComboBox();
  JLabel NTAVE_Label = new JLabel();
  JTextField NTAVE_TextField = new JTextField();
  JLabel NTWR_Label = new JLabel();
  JTextField NTWR_TextField = new JTextField();
  JLabel IWRAP_Label = new JLabel();
  JComboBox IWRAP_ComboBox = new JComboBox();
  FlowLayout flowLayout2 = new FlowLayout();
  JLabel NTWX_Label = new JLabel();
  JTextField NTWX_TextField = new JTextField();
  JLabel NTWV_Label = new JLabel();
  JTextField NTWV_TextField = new JTextField();
  JLabel NTWE_Label = new JLabel();
  JTextField NTWE_TextField = new JTextField();
  JLabel IOUTFM_Label = new JLabel();
  JComboBox IOUTFM_ComboBox = new JComboBox();
  JLabel NTWPRT_Label = new JLabel();
  JTextField NTWPRT_TextField = new JTextField();
  JLabel IDECOMP_Label = new JLabel();
  JComboBox IDECOMP_ComboBox = new JComboBox();
  JComboBox IGB_ComboBox = new JComboBox();
  JLabel INTDIEL_Label = new JLabel();
  JTextField INTDIEL_TextField = new JTextField();
  JLabel EXTDIEL_Label = new JLabel();
  JTextField EXTDIEL_TextField = new JTextField();
  JLabel SALTCON_Label = new JLabel();
  JTextField SALTCON_TextField = new JTextField();
  JLabel RGBMAX_Label = new JLabel();
  JTextField RGBMAX_TextField = new JTextField();
  JLabel RBORNSTAT_Label = new JLabel();
  JComboBox RBORNSTAT_ComboBox = new JComboBox();
  JLabel OFFSET_Label = new JLabel();
  JTextField OFFSET_TextField = new JTextField();
  JLabel GBSA_Label = new JLabel();
  JComboBox GBSA_ComboBox = new JComboBox();
  JLabel RDT_Label = new JLabel();
  JLabel SURFTEN_Label = new JLabel();
  JTextField RDT_TextField = new JTextField();
  JTextField SURFTEN_TextField = new JTextField();
  GridBagLayout gridBagLayout7 = new GridBagLayout();
  GridBagLayout gridBagLayout8 = new GridBagLayout();
  JLabel GB_Dummy_Label = new JLabel();
  JLabel DIELC_Label = new JLabel();
  JLabel CUT_Label = new JLabel();
  JLabel SCNB_Label = new JLabel();
  JMenuItem Edit_MenuItem = new JMenuItem();
  FlowLayout flowLayout3 = new FlowLayout();
  JLabel NTR_Label = new JLabel();
  JComboBox NTR_ComboBox = new JComboBox();
  JLabel IBELLY_Label = new JLabel();
  JComboBox IBELLY_ComboBox = new JComboBox();
  JLabel BELLYMASK_Label = new JLabel();
  JLabel RESTRAINTMASK_Label = new JLabel();
  JLabel RESTRAINT_WT_Label = new JLabel();
  JTextField BELLYMASK_TextField = new JTextField();
  JTextField RESTRAINTMASK_TextField = new JTextField();
  JTextField RESTRAINT_WT_TextField = new JTextField();
  JLabel FCAP_Label = new JLabel();
  JLabel IVCAP_Label = new JLabel();
  JComboBox IVCAP_ComboBox = new JComboBox();
  JTextField FCAP_TextField = new JTextField();
  JPanel shakePanel = new JPanel();
  JLabel HWTNM2_Label = new JLabel();
  JLabel HWTNM1_Label = new JLabel();
  JLabel OWTNM_Label = new JLabel();
  JLabel WATNAM_Label = new JLabel();
  JLabel TOL_Label = new JLabel();
  JLabel JFASTW_Label = new JLabel();
  JLabel NTC_Label = new JLabel();
  JComboBox JFASTW_ComboBox = new JComboBox();
  JComboBox NTC_ComboBox = new JComboBox();
  JTextField HWTNM2_TextField = new JTextField();
  JTextField HWTNM1_TextField = new JTextField();
  JTextField WATNAM_TextField = new JTextField();
  JTextField OWTNM_TextField = new JTextField();
  JTextField TOL_TextField = new JTextField();
  GridBagLayout gridBagLayout6 = new GridBagLayout();
  GridBagLayout gridBagLayout9 = new GridBagLayout();
  JLabel jLabel6 = new JLabel();
  GridBagLayout gridBagLayout10 = new GridBagLayout();
  JLabel Dummy_MD_Label = new JLabel();
  JButton saveFile_Button = new JButton();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenuItem jMenuItem2 = new JMenuItem();
  public Sander9Frame(Sander9JobControl jc) {
    try {
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      s8jc = jc;
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
    contentPane = (JPanel) getContentPane();
    contentPane.setLayout(borderLayout1);
    setSize(new Dimension(694, 337));
    setTitle("Sander 9 Job Controls");
    jMenuFile.setText("File");
    jMenuFileExit.setToolTipText("Exit Program");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new
                                    Sander9Frame_jMenuFileExit_ActionAdapter(this));
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new
                                     Sander9Frame_jMenuHelpAbout_ActionAdapter(this));
    descriptionPane.setToolTipText("");
    descriptionPane.setEditable(false);
    descriptionPane.setText("Options description will be shown here");
    descriptionPane.setLineWrap(true);
    descriptionPane.setRows(6);
    descriptionPane.setWrapStyleWord(true);
    descrPanel.setLayout(borderLayout2);
    descrPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(
        Color.
        lightGray, 1), "Option Description"));
    potentialFuncPanel.setLayout(gridBagLayout10);
    NTF_ComboBox.addItemListener(new Sander9Frame_NTF_ComboBox_itemAdapter(this));
    NTB_ComboBox.addItemListener(new Sander9Frame_NTB_ComboBox_itemAdapter(this));
    DIELC_TextField.setToolTipText("Enter new value and press Enter");
    DIELC_TextField.setText("          ");
    DIELC_TextField.setColumns(5);
    CUT_TextField.setToolTipText("Enter new value and press Enter");
    CUT_TextField.setText("         ");
    CUT_TextField.setColumns(5);
    SCNB_TextField.setToolTipText("Enter new value and press Enter");
    SCNB_TextField.setText("      ");
    SCNB_TextField.setColumns(5);
    SCEE_TextField.setToolTipText("Enter new value and press Enter");
    SCEE_TextField.setText("     ");
    SCEE_TextField.setColumns(5);
    NSNB_TextField.setToolTipText("Enter new value and press Enter");
    NSNB_TextField.setText("      ");
    NSNB_TextField.setColumns(5);
    IPOL_ComboBox.addItemListener(new Sander9Frame_IPOL_ComboBox_itemAdapter(this));
    GB_Panel.setEnabled(false);
    GB_Panel.setToolTipText("Generalized Born/Surface Area options");
    GB_Panel.setLayout(gridBagLayout8);
    PME_Panel.setEnabled(false);
    PME_Panel.setToolTipText("The Particle Mesh Ewald (PME) method options");
    jobTypePanel.setLayout(borderLayout5);
    jPanel1.setLayout(gridBagLayout2);
    IMIN_ComboBox.addItemListener(new Sander9Frame_IMIN_ComboBox_itemAdapter(this));
    ioPanel.setToolTipText("Nature and format of the input and output");
    ioPanel.setLayout(borderLayout6);
    inputPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(
        Color.
        lightGray, 1), "Nature and format of the input"));
    inputPanel.setLayout(flowLayout2);
    outputPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(
        Color.
        lightGray, 1), "Nature and format of the output"));
    outputPanel.setLayout(gridBagLayout7);
    restrainPanel.setToolTipText("Option for the Frozen or restrained atoms");
    restrainPanel.setLayout(gridBagLayout9);
    cardsPanel.setLayout(cardLayout1);
    NTMIN_ComboBox.addItemListener(new
                                   Sander9Frame_NTMIN_ComboBox_itemAdapter(this));
    maxIterLabel.setToolTipText("");
    maxIterLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    maxIterLabel.setText("Max Iterations (MAXCYC):");
    maxIterLabel.addMouseListener(new Sander9Frame_maxIterLabel_mouseAdapter(this));
    MAXCYC_TextField.setToolTipText("");
    MAXCYC_TextField.setText("      ");
    MAXCYC_TextField.setColumns(6);
    MAXCYC_TextField.addActionListener(new
                                       Sander9Frame_MAXCYC_TextField_actionAdapter(this));
    minCard.setLayout(gridBagLayout1);
    dxLabel.setToolTipText("");
    dxLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    dxLabel.setText("Initial step length (DX0): ");
    dxLabel.addMouseListener(new Sander9Frame_dxLabel_mouseAdapter(this));
    switchLabel.setToolTipText("");
    switchLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    switchLabel.setText("Switch (NCYC): ");
    switchLabel.addMouseListener(new Sander9Frame_switchLabel_mouseAdapter(this));
    covergLabel.setToolTipText("");
    covergLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    covergLabel.setText("Convergence criterion (DRMS): ");
    covergLabel.addMouseListener(new Sander9Frame_covergLabel_mouseAdapter(this));
    NCYC_TextField.setToolTipText("");
    NCYC_TextField.setText("      ");
    NCYC_TextField.setColumns(6);
    NCYC_TextField.addActionListener(new
                                     Sander9Frame_NCYC_TextField_actionAdapter(this));
    DX0_TextField.setColumns(6);
    DX0_TextField.addActionListener(new
                                    Sander9Frame_DX0_TextField_actionAdapter(this));
    DRMS_TextField.setToolTipText("");
    DRMS_TextField.setColumns(6);
    DRMS_TextField.addActionListener(new
                                     Sander9Frame_DRMS_TextField_actionAdapter(this));
    IREST_ComboBox.addItemListener(new
                                   Sander9Frame_IREST_ComboBox_itemAdapter(this));
    mdCard.setLayout(gridBagLayout3);
    NSTLIM_Label.setToolTipText("");
    NSTLIM_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NSTLIM_Label.setText("Number of MD Steps (nstlim): ");
    NSTLIM_Label.addMouseListener(new Sander9Frame_NSTLIM_Label_mouseAdapter(this));
    NSTLIM_TextField.setText("      ");
    NSTLIM_TextField.setColumns(6);
    NSTLIM_TextField.setHorizontalAlignment(SwingConstants.LEFT);
    NSTLIM_TextField.addActionListener(new
                                       Sander9Frame_NSTLIM_TextField_actionAdapter(this));
    TEMP0_Label.setToolTipText("Reference Temperature, K");
    TEMP0_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    TEMP0_Label.setText("Temperature (temp0), K: ");
    TEMP0_Label.addMouseListener(new Sander9Frame_TEMP0_Label_mouseAdapter(this));
    TEMP0_TextField.setText("      ");
    TEMP0_TextField.setColumns(6);
    DT_Label.setToolTipText("");
    DT_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    DT_Label.setText("Time Step, psec (dt): ");
    DT_Label.addMouseListener(new Sander9Frame_DT_Label_mouseAdapter(this));
    DT_TextField.setText("      ");
    DT_TextField.setColumns(6);
    MolDynPanel.setToolTipText(
        "Additional Options for Molecular Dynamics Simulation");
    MolDynPanel.setLayout(gridBagLayout4);
    jTabbedPane1.setToolTipText("");
    NSCM_Label.setToolTipText("");
    NSCM_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NSCM_Label.setText("NSCM: ");
    NSCM_Label.addMouseListener(new Sander9Frame_jLabel10_mouseAdapter(this));
    NSCM_TextField.setColumns(6);
    NRESPA_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NRESPA_Label.setText("NRESPA: ");
    NRESPA_Label.addMouseListener(new Sander9Frame_NRESPA_Label_mouseAdapter(this));
    NRESPA_TextField.setText("      ");
    NRESPA_TextField.setColumns(6);
    T_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    T_Label.setText("T: ");
    T_Label.addMouseListener(new Sander9Frame_T_Label_mouseAdapter(this));
    T_TextField.setText("      ");
    T_TextField.setColumns(6);
    NTT_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTT_Label.setText("Temperature Scaling (ntt): ");
    NTT_Label.addMouseListener(new Sander9Frame_NTT_Label_mouseAdapter(this));
    TEMP0LES_Label.setToolTipText("");
    TEMP0LES_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    TEMP0LES_Label.setText("TEMP0LES: ");
    TEMP0LES_Label.addMouseListener(new
                                    Sander9Frame_TEMP0LES_Label_mouseAdapter(this));
    TEMP0LES_TextField.setToolTipText("");
    TEMP0LES_TextField.setColumns(6);
    TEMPI_Label.setToolTipText("");
    TEMPI_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    TEMPI_Label.setText("TEMPI: ");
    TEMPI_Label.addMouseListener(new Sander9Frame_TEMPI_Label_mouseAdapter(this));
    TEMPI_TextField.setText("      ");
    TEMPI_TextField.setColumns(6);
    IG_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    IG_Label.setText("Seed (ig): ");
    IG_Label.addMouseListener(new Sander9Frame_IG_Label_mouseAdapter(this));
    IG_TextField.setToolTipText("");
    IG_TextField.setText("      ");
    IG_TextField.setColumns(6);
    TAUTP_Label.setToolTipText("");
    TAUTP_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    TAUTP_Label.setText("Temperature coupling, ps (tautp): ");
    TAUTP_Label.addMouseListener(new Sander9Frame_TAUTP_Label_mouseAdapter(this));
    TAUTP_TextField.setToolTipText("");
    TAUTP_TextField.setText("      ");
    TAUTP_TextField.setColumns(6);
    GAMMA_LN_Label.setToolTipText("");
    GAMMA_LN_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    GAMMA_LN_Label.setText("GAMMA_LN: ");
    GAMMA_LN_Label.addMouseListener(new
                                    Sander9Frame_GAMMA_LN_Label_mouseAdapter(this));
    GAMMA_LN_TextField.setText(" ");
    GAMMA_LN_TextField.setColumns(6);
    VRAND_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    VRAND_Label.setText("VRAND: ");
    VRAND_Label.addMouseListener(new Sander9Frame_VRAND_Label_mouseAdapter(this));
    VRAND_TextField.setToolTipText("");
    VRAND_TextField.setText(" ");
    VRAND_TextField.setColumns(6);
    VLIMIT_Label.setToolTipText("");
    VLIMIT_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    VLIMIT_Label.setText("VLIMIT: ");
    VLIMIT_Label.addMouseListener(new Sander9Frame_VLIMIT_Label_mouseAdapter(this));
    VLIMIT_TextField.setText(" ");
    VLIMIT_TextField.setColumns(6);
    TempControlPanel.setLayout(gridBagLayout5);
    TempControlPanel.setBorder(new TitledBorder(BorderFactory.
                                                createLineBorder(
        Color.lightGray, 1), "Temperature Regulation"));
    NTP_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTP_Label.setText("Pressure Scaling (ntp): ");
    NTP_Label.addMouseListener(new Sander9Frame_NTP_Label_mouseAdapter(this));
    PRES0_Label.setToolTipText("Reference Pressure, bars");
    PRES0_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    PRES0_Label.setText("Pressure, bars (pres0): ");
    PRES0_Label.addMouseListener(new Sander9Frame_PRES0_Label_mouseAdapter(this));
    PRES0_TextField.setToolTipText("");
    PRES0_TextField.setText(" ");
    PRES0_TextField.setColumns(6);
    COMP_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    COMP_Label.setText("COMP: ");
    COMP_Label.addMouseListener(new Sander9Frame_COMP_Label_mouseAdapter(this));
    COMP_TextField.setToolTipText("");
    COMP_TextField.setText(" ");
    COMP_TextField.setColumns(6);
    PressureControlPanel.setLayout(flowLayout3);
    TAUP_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    TAUP_Label.setText("Pressure Coupling, ps (taup): ");
    TAUP_Label.addMouseListener(new Sander9Frame_TAUP_Label_mouseAdapter(this));
    TAUP_TextField.setToolTipText("");
    TAUP_TextField.setText(" ");
    TAUP_TextField.setColumns(6);
    PressureControlPanel.setBorder(new TitledBorder(BorderFactory.
        createLineBorder(Color.lightGray, 2), "Pressure Regulation"));
    NTT_ComboBox.addItemListener(new Sander9Frame_NTT_ComboBox_itemAdapter(this));
    NTP_ComboBox.addItemListener(new Sander9Frame_NTP_ComboBox_itemAdapter(this));
    jPanel1.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.
        lightGray, 1), "Job Control"));
    minCard.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.
        lightGray, 1), "Energy Minimization Options"));
    mdCard.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.
        lightGray, 1), "Molecular Dynamics Options"));
    NSNB_Label.setToolTipText("");
    NSNB_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NSNB_Label.setText("NB List Update (nsnb): ");
    NSNB_Label.addMouseListener(new Sander9Frame_NSNB_Label_mouseAdapter(this));
    SCEE_Label.setToolTipText("");
    SCEE_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    SCEE_Label.setText("SCEE: ");
    SCEE_Label.addMouseListener(new Sander9Frame_SCEE_Label_mouseAdapter(this));
    IPOL_Label.setToolTipText("");
    IPOL_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    IPOL_Label.setText("Polarization On/Off (ipol): ");
    IPOL_Label.addMouseListener(new Sander9Frame_IPOL_Label_mouseAdapter(this));
    NTF_Label.setToolTipText("");
    NTF_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTF_Label.setText("Force Evaluation (ntf): ");
    NTF_Label.addMouseListener(new Sander9Frame_NTF_Label_mouseAdapter(this));
    NTB_Label.setToolTipText("");
    NTB_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTB_Label.setText("Periodic Boundary (ntb): ");
    NTB_Label.addMouseListener(new Sander9Frame_NTB_Label_mouseAdapter(this));
    openFile_Button.addActionListener(new Sander9Frame_jButton1_actionAdapter(this));
    newFile_Button.addActionListener(new Sander9Frame_jButton2_actionAdapter(this));
    help_Button.addActionListener(new Sander9Frame_jButton3_actionAdapter(this));
    NTXO_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTXO_Label.setText("NTXO: ");
    NTXO_Label.addMouseListener(new Sander9Frame_NTXO_Label_mouseAdapter(this));
    NTPR_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTPR_Label.setText("NTPR: ");
    NTPR_Label.addMouseListener(new Sander9Frame_NTPR_Label_mouseAdapter(this));
    NTPR_TextField.setText(" ");
    NTPR_TextField.setColumns(6);
    NTX_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTX_Label.setText("NTX: ");
    NTX_Label.addMouseListener(new Sander9Frame_NTX_Label_mouseAdapter(this));
    NTRX_Label.setToolTipText("");
    NTRX_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTRX_Label.setText("     NTRX: ");
    NTRX_Label.addMouseListener(new Sander9Frame_NTRX_Label_mouseAdapter(this));
    NTAVE_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTAVE_Label.setText("NTAVE: ");
    NTAVE_Label.addMouseListener(new Sander9Frame_NTAVE_Label_mouseAdapter(this));
    NTAVE_TextField.setText(" ");
    NTAVE_TextField.setColumns(6);
    NTWR_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTWR_Label.setText("NTWR: ");
    NTWR_Label.addMouseListener(new Sander9Frame_NTWR_Label_mouseAdapter(this));
    NTWR_TextField.setText(" ");
    NTWR_TextField.setColumns(6);
    IWRAP_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    IWRAP_Label.setText("IWRAP: ");
    IWRAP_Label.addMouseListener(new Sander9Frame_IWRAP_Label_mouseAdapter(this));
    flowLayout2.setAlignment(FlowLayout.LEFT);
    NTWX_Label.setToolTipText("");
    NTWX_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTWX_Label.setText("NTWX: ");
    NTWX_Label.addMouseListener(new Sander9Frame_NTWX_Label_mouseAdapter(this));
    NTWX_TextField.setText(" ");
    NTWX_TextField.setColumns(6);
    NTWV_Label.setToolTipText("");
    NTWV_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTWV_Label.setText("     NTWV: ");
    NTWV_Label.addMouseListener(new Sander9Frame_NTWV_Label_mouseAdapter(this));
    NTWV_TextField.setText(" ");
    NTWV_TextField.setColumns(6);
    NTWE_Label.setToolTipText("");
    NTWE_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTWE_Label.setText("     NTWE: ");
    NTWE_Label.addMouseListener(new Sander9Frame_NTWE_Label_mouseAdapter(this));
    NTWE_TextField.setText(" ");
    NTWE_TextField.setColumns(6);
    IOUTFM_Label.setToolTipText("");
    IOUTFM_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    IOUTFM_Label.setText("     IOUTFM: ");
    IOUTFM_Label.addMouseListener(new Sander9Frame_IOUTFM_Label_mouseAdapter(this));
    NTWPRT_Label.setToolTipText("");
    NTWPRT_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTWPRT_Label.setText("     NTWPRT: ");
    NTWPRT_Label.addMouseListener(new Sander9Frame_NTWPRT_Label_mouseAdapter(this));
    NTWPRT_TextField.setText(" ");
    NTWPRT_TextField.setColumns(6);
    IDECOMP_Label.setToolTipText("");
    IDECOMP_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    IDECOMP_Label.setText("     IDECOMP: ");
    IDECOMP_Label.addMouseListener(new
                                   Sander9Frame_IDECOMP_Label_mouseAdapter(this));
    IMIN_ComboBox.setToolTipText("Type of calculation");
    IMIN_ComboBox.addMouseListener(new
                                   Sander9Frame_IMIN_ComboBox_mouseAdapter(this));
    IGB_ComboBox.setToolTipText("General Born/Surface Area");
    IGB_ComboBox.addMouseListener(new Sander9Frame_IGB_ComboBox_mouseAdapter(this));
    IGB_ComboBox.addItemListener(new Sander9Frame_IGB_ComboBox_itemAdapter(this));
    INTDIEL_Label.setToolTipText("");
    INTDIEL_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    INTDIEL_Label.setText("     INTDIEL: ");
    INTDIEL_Label.addMouseListener(new
                                   Sander9Frame_INTDIEL_Label_mouseAdapter(this));
    INTDIEL_TextField.setToolTipText("");
    INTDIEL_TextField.setText("      ");
    INTDIEL_TextField.setColumns(6);
    EXTDIEL_Label.setToolTipText("");
    EXTDIEL_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    EXTDIEL_Label.setText("     EXTDIEL: ");
    EXTDIEL_Label.addMouseListener(new
                                   Sander9Frame_EXTDIEL_Label_mouseAdapter(this));
    EXTDIEL_TextField.setToolTipText("");
    EXTDIEL_TextField.setText("      ");
    EXTDIEL_TextField.setColumns(6);
    SALTCON_Label.setToolTipText("");
    SALTCON_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    SALTCON_Label.setText("     SALTCON: ");
    SALTCON_Label.addMouseListener(new
                                   Sander9Frame_SALTCON_Label_mouseAdapter(this));
    SALTCON_TextField.setToolTipText("");
    SALTCON_TextField.setText("      ");
    SALTCON_TextField.setColumns(6);
    RGBMAX_Label.setToolTipText("");
    RGBMAX_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    RGBMAX_Label.setText("     RGBMAX: ");
    RGBMAX_Label.addMouseListener(new Sander9Frame_RGBMAX_Label_mouseAdapter(this));
    RGBMAX_TextField.setToolTipText("");
    RGBMAX_TextField.setText("      ");
    RGBMAX_TextField.setColumns(6);
    RBORNSTAT_Label.setToolTipText("");
    RBORNSTAT_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    RBORNSTAT_Label.setText("     RBORNSTAT: ");
    RBORNSTAT_Label.addMouseListener(new
                                     Sander9Frame_RBORNSTAT_Label_mouseAdapter(this));
    OFFSET_Label.setToolTipText("");
    OFFSET_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    OFFSET_Label.setText("     OFFSET: ");
    OFFSET_Label.addMouseListener(new Sander9Frame_OFFSET_Label_mouseAdapter(this));
    OFFSET_TextField.setToolTipText("");
    OFFSET_TextField.setText("      ");
    OFFSET_TextField.setColumns(6);
    GBSA_Label.setToolTipText("");
    GBSA_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    GBSA_Label.setText("     GBSA: ");
    GBSA_Label.addMouseListener(new Sander9Frame_GBSA_Label_mouseAdapter(this));
    RDT_Label.setToolTipText("");
    RDT_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    RDT_Label.setText("     RDT: ");
    RDT_Label.addMouseListener(new Sander9Frame_RDT_Label_mouseAdapter(this));
    SURFTEN_Label.setToolTipText("");
    SURFTEN_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    SURFTEN_Label.setText("     SURFTEN: ");
    SURFTEN_Label.addMouseListener(new
                                   Sander9Frame_SURFTEN_Label_mouseAdapter(this));
    RDT_TextField.setToolTipText("");
    RDT_TextField.setText("      ");
    RDT_TextField.setColumns(6);
    SURFTEN_TextField.setToolTipText("");
    SURFTEN_TextField.setText("      ");
    SURFTEN_TextField.setColumns(6);
    GB_Dummy_Label.setToolTipText("");
    GB_Dummy_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    GB_Dummy_Label.setText("    ");
    RBORNSTAT_ComboBox.addItemListener(new
                                       Sander9Frame_RBORNSTAT_ComboBox_itemAdapter(this));
    GBSA_ComboBox.addItemListener(new Sander9Frame_GBSA_ComboBox_itemAdapter(this));
    NTXO_ComboBox.addItemListener(new Sander9Frame_NTXO_ComboBox_itemAdapter(this));
    IWRAP_ComboBox.addItemListener(new
                                   Sander9Frame_IWRAP_ComboBox_itemAdapter(this));
    IOUTFM_ComboBox.addItemListener(new
                                    Sander9Frame_IOUTFM_ComboBox_itemAdapter(this));
    IDECOMP_ComboBox.addItemListener(new
                                     Sander9Frame_IDECOMP_ComboBox_itemAdapter(this));
    NTX_ComboBox.addItemListener(new Sander9Frame_NTX_ComboBox_itemAdapter(this));
    NTRX_ComboBox.addItemListener(new Sander9Frame_NTRX_ComboBox_itemAdapter(this));
    DIELC_Label.setToolTipText("");
    DIELC_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    DIELC_Label.setText("Dielectric Constant (dielc):");
    DIELC_Label.addMouseListener(new Sander9Frame_DIELC_Label_mouseAdapter(this));
    CUT_Label.setToolTipText("");
    CUT_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    CUT_Label.setText("Nonbonded Cutoff (cut):");
    CUT_Label.addMouseListener(new Sander9Frame_CUT_Label_mouseAdapter(this));
    SCNB_Label.setToolTipText("");
    SCNB_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    SCNB_Label.setText("SCNB:");
    SCNB_Label.addMouseListener(new Sander9Frame_SCNB_Label_mouseAdapter(this));
    Edit_MenuItem.setToolTipText("Edit Job Control File in text editor");
    Edit_MenuItem.setActionCommand("Edit");
    Edit_MenuItem.setText("Edit File");
    Edit_MenuItem.addActionListener(new
                                    Sander9Frame_Edit_MenuItem_actionAdapter(this));
    flowLayout3.setAlignment(FlowLayout.LEFT);
    NTR_Label.setToolTipText("");
    NTR_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTR_Label.setText("Position Restraints (ntr): ");
    NTR_Label.addMouseListener(new Sander9Frame_NTR_Label_mouseAdapter(this));
    NTR_ComboBox.addItemListener(new Sander9Frame_NTR_ComboBox_itemAdapter(this));
    IBELLY_Label.setToolTipText("");
    IBELLY_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    IBELLY_Label.setText("Belly Run (ibelly): ");
    IBELLY_Label.addMouseListener(new Sander9Frame_IBELLY_Label_mouseAdapter(this));
    IBELLY_ComboBox.addItemListener(new
                                    Sander9Frame_IBELLY_ComboBox_itemAdapter(this));
    BELLYMASK_Label.setToolTipText("");
    BELLYMASK_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    BELLYMASK_Label.setText("BELLYMASK: ");
    BELLYMASK_Label.addMouseListener(new
                                     Sander9Frame_BELLYMASK_Label_mouseAdapter(this));
    RESTRAINTMASK_Label.setToolTipText("");
    RESTRAINTMASK_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    RESTRAINTMASK_Label.setText("RESTRAINTMASK: ");
    RESTRAINTMASK_Label.addMouseListener(new
                                         Sander9Frame_RESTRAINTMASK_Label_mouseAdapter(this));
    RESTRAINT_WT_Label.setToolTipText("");
    RESTRAINT_WT_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    RESTRAINT_WT_Label.setText("RESTRAINT_WT: ");
    RESTRAINT_WT_Label.addMouseListener(new
                                        Sander9Frame_RESTRAINT_WT_Label_mouseAdapter(this));
    BELLYMASK_TextField.setText(" ");
    BELLYMASK_TextField.setColumns(6);
    RESTRAINTMASK_TextField.setText(" ");
    RESTRAINTMASK_TextField.setColumns(6);
    RESTRAINT_WT_TextField.setText(" ");
    RESTRAINT_WT_TextField.setColumns(6);
    FCAP_Label.setToolTipText("");
    FCAP_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    FCAP_Label.setText("Force Constant (fcap): ");
    FCAP_Label.addMouseListener(new Sander9Frame_FCAP_Label_mouseAdapter(this));
    IVCAP_Label.setToolTipText("");
    IVCAP_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    IVCAP_Label.setText("Water cap (ivcap): ");
    IVCAP_Label.addMouseListener(new Sander9Frame_IVCAP_Label_mouseAdapter(this));
    FCAP_TextField.setText(" ");
    FCAP_TextField.setColumns(6);
    IVCAP_ComboBox.addItemListener(new
                                   Sander9Frame_IVCAP_ComboBox_itemAdapter(this));
    HWTNM2_Label.setToolTipText("");
    HWTNM2_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    HWTNM2_Label.setText("HWTNM2: ");
    HWTNM2_Label.addMouseListener(new Sander9Frame_HWTNM2_Label_mouseAdapter(this));
    HWTNM1_Label.setToolTipText("");
    HWTNM1_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    HWTNM1_Label.setText("HWTNM1: ");
    HWTNM1_Label.addMouseListener(new Sander9Frame_HWTNM1_Label_mouseAdapter(this));
    OWTNM_Label.setToolTipText("");
    OWTNM_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    OWTNM_Label.setText("OWTNM: ");
    OWTNM_Label.addMouseListener(new Sander9Frame_OWTNM_Label_mouseAdapter(this));
    WATNAM_Label.setToolTipText("");
    WATNAM_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    WATNAM_Label.setText("WATNAM: ");
    WATNAM_Label.addMouseListener(new Sander9Frame_WATNAM_Label_mouseAdapter(this));
    TOL_Label.setToolTipText("");
    TOL_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    TOL_Label.setText("Tolerance (tol): ");
    TOL_Label.addMouseListener(new Sander9Frame_TOL_Label_mouseAdapter(this));
    JFASTW_Label.setToolTipText("");
    JFASTW_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    JFASTW_Label.setText("Fast Water Flag (jfastw): ");
    JFASTW_Label.addMouseListener(new Sander9Frame_JFASTW_Label_mouseAdapter(this));
    NTC_Label.setToolTipText("");
    NTC_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    NTC_Label.setText("Apply SHAKE (ntc): ");
    NTC_Label.addMouseListener(new Sander9Frame_NTC_Label_mouseAdapter(this));
    shakePanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(
        Color.
        lightGray, 1), "SHAKE bond length constraints"));
    shakePanel.setLayout(gridBagLayout6);
    HWTNM2_TextField.setText(" ");
    HWTNM2_TextField.setColumns(6);
    HWTNM1_TextField.setText(" ");
    HWTNM1_TextField.setColumns(6);
    WATNAM_TextField.setText(" ");
    WATNAM_TextField.setColumns(6);
    OWTNM_TextField.setText(" ");
    OWTNM_TextField.setColumns(6);
    TOL_TextField.setText(" ");
    TOL_TextField.setColumns(6);
    NTC_ComboBox.addItemListener(new Sander9Frame_NTC_ComboBox_itemAdapter(this));
    JFASTW_ComboBox.addItemListener(new
                                    Sander9Frame_JFASTW_ComboBox_itemAdapter(this));
    jLabel6.setToolTipText("");
    jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel6.setText("    ");
    Dummy_MD_Label.setToolTipText("");
    Dummy_MD_Label.setHorizontalAlignment(SwingConstants.RIGHT);
    IREST_ComboBox.addMouseListener(new
                                    Sander9Frame_IREST_ComboBox_mouseAdapter(this));
    NTMIN_ComboBox.addMouseListener(new
                                    Sander9Frame_NTMIN_ComboBox_mouseAdapter(this));
    NTT_ComboBox.addMouseListener(new Sander9Frame_NTT_ComboBox_mouseAdapter(this));
    NTP_ComboBox.addMouseListener(new Sander9Frame_NTP_ComboBox_mouseAdapter(this));
    saveFile_Button.setToolTipText("Save File");
    saveFile_Button.setIcon(saveFileImage);
    saveFile_Button.addActionListener(new
                                      Sander9Frame_saveFile_Button_actionAdapter(this));
    NTF_ComboBox.addMouseListener(new Sander9Frame_NTF_ComboBox_mouseAdapter(this));
    IPOL_ComboBox.addMouseListener(new
                                   Sander9Frame_IPOL_ComboBox_mouseAdapter(this));
    NTB_ComboBox.addMouseListener(new Sander9Frame_NTB_ComboBox_mouseAdapter(this));
    NTX_ComboBox.addMouseListener(new Sander9Frame_NTX_ComboBox_mouseAdapter(this));
    NTRX_ComboBox.addMouseListener(new
                                   Sander9Frame_NTRX_ComboBox_mouseAdapter(this));
    NTXO_ComboBox.addMouseListener(new
                                   Sander9Frame_NTXO_ComboBox_mouseAdapter(this));
    IWRAP_ComboBox.addMouseListener(new
                                    Sander9Frame_IWRAP_ComboBox_mouseAdapter(this));
    IOUTFM_ComboBox.addMouseListener(new
                                     Sander9Frame_IOUTFM_ComboBox_mouseAdapter(this));
    IDECOMP_ComboBox.addMouseListener(new
                                      Sander9Frame_IDECOMP_ComboBox_mouseAdapter(this));
    NTR_ComboBox.addMouseListener(new Sander9Frame_NTR_ComboBox_mouseAdapter(this));
    IBELLY_ComboBox.addMouseListener(new
                                     Sander9Frame_IBELLY_ComboBox_mouseAdapter(this));
    IVCAP_ComboBox.addMouseListener(new
                                    Sander9Frame_IVCAP_ComboBox_mouseAdapter(this));
    NTC_ComboBox.addMouseListener(new Sander9Frame_NTC_ComboBox_mouseAdapter(this));
    JFASTW_ComboBox.addMouseListener(new
                                     Sander9Frame_JFASTW_ComboBox_mouseAdapter(this));
    RBORNSTAT_ComboBox.addMouseListener(new
                                        Sander9Frame_RBORNSTAT_ComboBox_mouseAdapter(this));
    GBSA_ComboBox.addMouseListener(new
                                   Sander9Frame_GBSA_ComboBox_mouseAdapter(this));
    jMenuItem1.setText("Open Job Control File");
    jMenuItem1.addActionListener(new Sander9Frame_jMenuItem1_actionAdapter(this));
    jMenuItem2.setText("Save File");
    jMenuItem2.addActionListener(new Sander9Frame_jMenuItem2_actionAdapter(this));
    jMenuBar1.add(jMenuFile);
    jMenuFile.add(jMenuItem1);
    jMenuFile.add(jMenuItem2);
    jMenuFile.add(Edit_MenuItem);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuFileExit);
    jMenuBar1.add(jMenuHelp);
    jMenuHelp.add(jMenuHelpAbout);
    setJMenuBar(jMenuBar1);
    openFile_Button.setIcon(image1);
    openFile_Button.setToolTipText("Open File");
    newFile_Button.setIcon(image2);
    newFile_Button.setToolTipText(
        "Reset the variables to their default values");
    help_Button.setIcon(image3);
    help_Button.setToolTipText("Help");
    jToolBar.add(openFile_Button);
    jToolBar.add(newFile_Button);
    jToolBar.add(saveFile_Button);
    jToolBar.add(help_Button);
    descrPanel.add(descriptionPane);
    jTabbedPane1.add(jobTypePanel, "Job Type");
    jTabbedPane1.add(potentialFuncPanel, "Potential Function");
    jTabbedPane1.add(MolDynPanel, "Molecular Dynamics");
    jTabbedPane1.add(ioPanel, "Input/Output");
    jTabbedPane1.add(restrainPanel, "Restrains");
    jTabbedPane1.add(GB_Panel, "GB/SA");
    jTabbedPane1.add(PME_Panel, "PME");
    ioPanel.add(inputPanel, BorderLayout.NORTH);
    ioPanel.add(outputPanel, BorderLayout.CENTER);
    jobTypePanel.add(jPanel1, BorderLayout.WEST);
    jobTypePanel.add(cardsPanel, BorderLayout.CENTER);
    cardsPanel.add(minCard, "minCard");
    cardsPanel.add(mdCard, "mdCard");

    contentPane.add(jTabbedPane1, BorderLayout.CENTER);
    MolDynPanel.add(PressureControlPanel,
                    new GridBagConstraints(0, 4, 8, 2, 1.0, 0.0
                                           , GridBagConstraints.CENTER,
                                           GridBagConstraints.BOTH,
                                           new Insets(5, 5, 5, 5), 0, 0));
    MolDynPanel.add(TempControlPanel,
                    new GridBagConstraints(0, 1, 8, 3, 1.0, 0.0
                                           , GridBagConstraints.CENTER,
                                           GridBagConstraints.BOTH,
                                           new Insets(5, 5, 5, 5), 0, 0));
    inputPanel.add(NTX_Label, null);
    inputPanel.add(NTX_ComboBox, null);
    inputPanel.add(NTRX_Label, null);
    inputPanel.add(NTRX_ComboBox, null);
    jPanel1.add(IMIN_ComboBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(IREST_ComboBox, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
        , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(IGB_ComboBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0
        , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    GB_Panel.add(INTDIEL_TextField,
                 new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                        , GridBagConstraints.WEST,
                                        GridBagConstraints.HORIZONTAL,
                                        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(EXTDIEL_Label, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(EXTDIEL_TextField,
                 new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                                        , GridBagConstraints.WEST,
                                        GridBagConstraints.HORIZONTAL,
                                        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(SALTCON_Label, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(SALTCON_TextField,
                 new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
                                        , GridBagConstraints.WEST,
                                        GridBagConstraints.HORIZONTAL,
                                        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(RGBMAX_Label, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(RDT_TextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(RBORNSTAT_ComboBox,
                 new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                        , GridBagConstraints.CENTER,
                                        GridBagConstraints.HORIZONTAL,
                                        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(OFFSET_Label, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(OFFSET_TextField,
                 new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                        , GridBagConstraints.WEST,
                                        GridBagConstraints.HORIZONTAL,
                                        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(GBSA_Label, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(GBSA_ComboBox, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(SURFTEN_Label, new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(SURFTEN_TextField,
                 new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0
                                        , GridBagConstraints.WEST,
                                        GridBagConstraints.HORIZONTAL,
                                        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(RDT_Label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(RBORNSTAT_Label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    PressureControlPanel.add(COMP_Label, null);
    PressureControlPanel.add(COMP_TextField, null);
    restrainPanel.add(IBELLY_Label,
                      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(shakePanel,
                      new GridBagConstraints(0, 3, 12, 1, 1.0, 1.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.BOTH,
                                             new Insets(5, 0, 5, 0), 0, 0));
    shakePanel.add(OWTNM_Label, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    shakePanel.add(WATNAM_Label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    shakePanel.add(NTC_Label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 0), 0, 0));
    shakePanel.add(HWTNM1_Label, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(RESTRAINTMASK_Label,
                      new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(5, 10, 5, 0), 0, 0));
    shakePanel.add(JFASTW_Label, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 10, 5, 0), 0, 0));
    shakePanel.add(TOL_Label, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 10, 5, 0), 0, 0));
    shakePanel.add(HWTNM2_Label, new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 10, 5, 5), 0, 0));
    GB_Panel.add(INTDIEL_Label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(RGBMAX_TextField,
                 new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0
                                        , GridBagConstraints.WEST,
                                        GridBagConstraints.HORIZONTAL,
                                        new Insets(5, 0, 5, 0), 0, 0));
    GB_Panel.add(GB_Dummy_Label, new GridBagConstraints(8, 3, 1, 1, 1.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0), 0, 0));
    outputPanel.add(jLabel6, new GridBagConstraints(8, 3, 1, 1, 1.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    outputPanel.add(NTXO_Label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 0, 0), 0, 0));
    outputPanel.add(NTXO_ComboBox,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTPR_Label, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTAVE_Label, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 0, 0), 0, 0));
    outputPanel.add(NTWR_Label, new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(IWRAP_Label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(IWRAP_ComboBox,
                    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTWX_Label, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTWV_Label, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTWE_Label, new GridBagConstraints(6, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(NTF_Label,
                           new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(NTB_Label,
                           new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 10, 5, 0), 0, 0));
    potentialFuncPanel.add(NTF_ComboBox,
                           new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    minCard.add(covergLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0
        , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0), 1, 1));
    minCard.add(methodLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 1, 1));
    minCard.add(DRMS_TextField, new GridBagConstraints(1, 5, 4, 1, 1.0, 0.0
        , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
        new Insets(1, 0, 1, 0), 0, 0));
    mdCard.add(NSTLIM_Label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(NSTLIM_TextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(DT_Label, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(5, 10, 5, 0), 1, 1));
    mdCard.add(DT_TextField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 1, 1));
    mdCard.add(TEMP0_Label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(TEMP0_TextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(PRES0_Label, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 10, 5, 0), 0, 0));
    mdCard.add(PRES0_TextField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(NTT_Label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.HORIZONTAL,
                                                 new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(NTT_ComboBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 1, 0));
    mdCard.add(NTP_Label, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.CENTER,
                                                 GridBagConstraints.HORIZONTAL,
                                                 new Insets(5, 10, 5, 0), 0, 0));
    mdCard.add(NTP_ComboBox, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(TAUTP_TextField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(TAUTP_Label, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(TAUP_TextField, new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0
        , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    mdCard.add(TAUP_Label, new GridBagConstraints(2, 3, 1, 1, 0.0, 1.0
                                                  , GridBagConstraints.NORTH,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 10, 5, 0), 0, 0));
    restrainPanel.add(RESTRAINT_WT_TextField,
                      new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(BELLYMASK_TextField,
                      new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(RESTRAINTMASK_TextField,
                      new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(NTR_Label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 0), 0, 0));
    restrainPanel.add(NTR_ComboBox,
                      new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(IBELLY_ComboBox,
                      new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(IVCAP_Label,
                      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(BELLYMASK_Label,
                      new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(5, 10, 5, 0), 0, 0));
    restrainPanel.add(RESTRAINT_WT_Label,
                      new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(5, 10, 5, 0), 0, 0));
    restrainPanel.add(IVCAP_ComboBox,
                      new GridBagConstraints(1, 2, 5, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(FCAP_TextField,
                      new GridBagConstraints(7, 2, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.CENTER,
                                             GridBagConstraints.NONE,
                                             new Insets(5, 0, 5, 0), 0, 0));
    restrainPanel.add(FCAP_Label, new GridBagConstraints(6, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 10, 5, 0), 0, 0));
    shakePanel.add(NTC_ComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    shakePanel.add(JFASTW_ComboBox,
                   new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                                          , GridBagConstraints.WEST,
                                          GridBagConstraints.NONE,
                                          new Insets(5, 0, 5, 0), 0, 0));
    shakePanel.add(WATNAM_TextField,
                   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                          , GridBagConstraints.WEST,
                                          GridBagConstraints.NONE,
                                          new Insets(5, 0, 5, 0), 0, 0));
    shakePanel.add(OWTNM_TextField,
                   new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                          , GridBagConstraints.WEST,
                                          GridBagConstraints.NONE,
                                          new Insets(5, 0, 5, 0), 0, 0));
    shakePanel.add(HWTNM1_TextField,
                   new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
                                          , GridBagConstraints.WEST,
                                          GridBagConstraints.NONE,
                                          new Insets(5, 0, 5, 0), 0, 0));
    shakePanel.add(HWTNM2_TextField,
                   new GridBagConstraints(7, 1, 1, 1, 1.0, 0.0
                                          , GridBagConstraints.WEST,
                                          GridBagConstraints.NONE,
                                          new Insets(5, 0, 5, 0), 0, 0));
    shakePanel.add(TOL_TextField, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    TempControlPanel.add(VLIMIT_Label,
                         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(5, 0, 5, 0), 0, 0));
    TempControlPanel.add(VRAND_Label,
                         new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(5, 10, 5, 0), 0, 0));
    TempControlPanel.add(VRAND_TextField,
                         new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 0, 5, 5), 0, 0));
    TempControlPanel.add(GAMMA_LN_Label,
                         new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(5, 10, 5, 0), 0, 0));
    TempControlPanel.add(GAMMA_LN_TextField,
                         new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 0, 5, 5), 0, 0));
    TempControlPanel.add(TEMPI_Label,
                         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(5, 0, 5, 0), 0, 0));
    TempControlPanel.add(TEMPI_TextField,
                         new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 0, 5, 5), 0, 0));
    TempControlPanel.add(TEMP0LES_Label,
                         new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(5, 5, 5, 0), 0, 0));
    TempControlPanel.add(TEMP0LES_TextField,
                         new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 0, 5, 0), 0, 0));
    TempControlPanel.add(VLIMIT_TextField,
                         new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 0, 5, 5), 0, 0));
    TempControlPanel.add(Dummy_MD_Label,
                         new GridBagConstraints(6, 1, 1, 1, 1.0, 1.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    MolDynPanel.add(NSCM_Label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 15, 5, 0), 0, 0));
    MolDynPanel.add(NRESPA_Label, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 15, 5, 0), 0, 0));
    MolDynPanel.add(NRESPA_TextField,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 5), 0, 0));
    MolDynPanel.add(T_Label, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 15, 5, 5), 0, 0));
    MolDynPanel.add(T_TextField, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    MolDynPanel.add(IG_Label, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 15, 5, 5), 0, 0));
    MolDynPanel.add(IG_TextField, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 5, 0), 0, 0));
    MolDynPanel.add(NSCM_TextField,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 5), 0, 0));
    potentialFuncPanel.add(IPOL_Label,
                           new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0
                                                  , GridBagConstraints.NORTH,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(DIELC_Label,
                           new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(DIELC_TextField,
                           new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(SCEE_Label,
                           new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 10, 5, 0), 0, 0));
    potentialFuncPanel.add(SCEE_TextField,
                           new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(SCNB_Label,
                           new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 10, 5, 0), 0, 0));
    potentialFuncPanel.add(NSNB_Label,
                           new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 10, 5, 0), 0, 0));
    potentialFuncPanel.add(CUT_Label,
                           new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 10, 5, 0), 0, 0));
    potentialFuncPanel.add(CUT_TextField,
                           new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(IPOL_ComboBox,
                           new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0
                                                  ,
                                                  GridBagConstraints.NORTHWEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(NTB_ComboBox,
                           new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(NSNB_TextField,
                           new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    potentialFuncPanel.add(SCNB_TextField,
                           new GridBagConstraints(5, 2, 1, 1, 1.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTWE_TextField,
                    new GridBagConstraints(7, 2, 1, 1, 1.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTWR_TextField,
                    new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(IOUTFM_Label, new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0
        , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(IOUTFM_ComboBox,
                    new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.NORTHWEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTWPRT_Label, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTWPRT_TextField,
                    new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.NORTHWEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(IDECOMP_Label,
                    new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.NORTH,
                                           GridBagConstraints.HORIZONTAL,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(IDECOMP_ComboBox,
                    new GridBagConstraints(5, 3, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.NORTHWEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTPR_TextField,
                    new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTWX_TextField,
                    new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTAVE_TextField,
                    new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 0), 0, 0));
    outputPanel.add(NTWV_TextField,
                    new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(5, 0, 5, 0), 0, 0));
    minCard.add(NTMIN_ComboBox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 2, 0), 1, 1));
    minCard.add(maxIterLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 2, 0), 1, 1));
    minCard.add(MAXCYC_TextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 2, 0), 1, 1));
    minCard.add(switchLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 0, 2, 0), 1, 1));
    minCard.add(NCYC_TextField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 2, 0), 0, 0));
    minCard.add(dxLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                                                , GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(5, 0, 2, 1), 1, 1));
    minCard.add(DX0_TextField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 0, 2, 0), 0, 0));
    contentPane.add(descrPanel, BorderLayout.SOUTH);
    contentPane.add(jToolBar, BorderLayout.NORTH);
    jTabbedPane1.setSelectedIndex(0);

    // --- setup all text fields, combo boxes etc...
    setupControls();
  }

  public void setupControls() {

    Map allVars = s8jc.getAllVariablesInfo();
    SanderVariable var;

    setupList("NTF", NTF_ComboBox, true);
    setupList("NTB", NTB_ComboBox, false);
    setupValue("DIELC", DIELC_TextField);
    setupValue("CUT", CUT_TextField);
    setupValue("SCNB", SCNB_TextField);
    setupValue("SCEE", SCEE_TextField);
    setupValue("NSNB", NSNB_TextField);
    setupValue("MAXCYC", MAXCYC_TextField);
    setupValue("NCYC", NCYC_TextField);
    setupValue("DX0", DX0_TextField);
    setupValue("DRMS", DRMS_TextField);
    setupList("IPOL", IPOL_ComboBox, false);

// --- Setup mdCard Panel

    setupValue("NSTLIM", NSTLIM_TextField);
    setupValue("TEMP0", TEMP0_TextField);
    setupValue("DT", DT_TextField);
    setupValue("TAUTP", TAUTP_TextField);
    setupValue("PRES0", PRES0_TextField);
    setupValue("TAUP", TAUP_TextField);
    setupList("NTT", NTT_ComboBox, true);
    setupList("NTP", NTP_ComboBox, true);

// --- Setup Molecular Dynamics Panel

    setupValue("NSCM", NSCM_TextField);
    setupValue("NRESPA", NRESPA_TextField);
    setupValue("T", T_TextField);
    setupValue("IG", IG_TextField);
    setupValue("T", T_TextField);
    setupValue("VLIMIT", VLIMIT_TextField);
    setupValue("VRAND", VRAND_TextField);
    setupValue("TEMPI", TEMPI_TextField);
    setupValue("GAMMA_LN", GAMMA_LN_TextField);
    setupValue("TEMP0LES", TEMP0LES_TextField);
    setupValue("COMP", COMP_TextField);

// --- Input panel

    setupList("NTRX", NTRX_ComboBox, true);
    setupList("NTX", NTX_ComboBox, true);

// --- Output panel

    setupList("NTXO", NTXO_ComboBox, true);
    setupList("IWRAP", IWRAP_ComboBox, true);
    setupList("IOUTFM", IOUTFM_ComboBox, true);
    setupList("IDECOMP", IDECOMP_ComboBox, true);

    setupValue("NTPR", NTPR_TextField);
    setupValue("NTAVE", NTAVE_TextField);
    setupValue("NTWR", NTWR_TextField);
    setupValue("NTWX", NTWX_TextField);
    setupValue("NTWV", NTWV_TextField);
    setupValue("NTWE", NTWE_TextField);
    setupValue("NTWPRT", NTWPRT_TextField);

    // --- General Born

    setupList("IGB", IGB_ComboBox, false);

    setupList("RBORNSTAT", RBORNSTAT_ComboBox, true);
    setupList("GBSA", GBSA_ComboBox, true);

    setupValue("INTDIEL", INTDIEL_TextField);
    setupValue("EXTDIEL", EXTDIEL_TextField);
    setupValue("SALTCON", SALTCON_TextField);
    setupValue("RGBMAX", RGBMAX_TextField);
    setupValue("OFFSET", OFFSET_TextField);
    setupValue("SURFTEN", SURFTEN_TextField);
    setupValue("RDT", RDT_TextField);

    // --- Frozen or restrained atoms.

    setupValue("RESTRAINT_WT", RESTRAINT_WT_TextField);
    setupValue("RESTRAINTMASK", RESTRAINTMASK_TextField);
    setupValue("BELLYMASK", BELLYMASK_TextField);

    // --- Water cap

    setupList("IVCAP", IVCAP_ComboBox, false);
    setupValue("FCAP", FCAP_TextField);

    // -- SHAKE

    setupList("NTC", NTC_ComboBox, true);
    setupList("JFASTW", JFASTW_ComboBox, true);
    setupValue("TOL", TOL_TextField);
    setupValue("WATNAM", WATNAM_TextField);
    setupValue("OWTNM", OWTNM_TextField);
    setupValue("HWTNM1", HWTNM1_TextField);
    setupValue("HWTNM2", HWTNM2_TextField);

    // --- Special setup

    comboBoxAdjusting = true;
    var = (SanderVariable) allVars.get("IMIN");
    IMIN_ComboBox.removeAllItems();
    IMIN_ComboBox.addItem("Molecular Dynamics");
    IMIN_ComboBox.addItem("Energy Minimization");
    IMIN_ComboBox.setSelectedIndex(var.getSelectedIndex());

    var = (SanderVariable) allVars.get("IREST");
    IREST_ComboBox.removeAllItems();
    IREST_ComboBox.addItem("New Calculation");
    IREST_ComboBox.addItem("Restart");
    IREST_ComboBox.setSelectedIndex(var.getSelectedIndex());

    var = (SanderVariable) allVars.get("NTMIN");
    NTMIN_ComboBox.removeAllItems();
    NTMIN_ComboBox.addItem("Conjugate Gradient");
    NTMIN_ComboBox.addItem("Steepest Descent + Conjugate Gradient");
    NTMIN_ComboBox.addItem("XMIN");
    NTMIN_ComboBox.addItem("LMOD");
    NTMIN_ComboBox.setSelectedIndex(var.getSelectedIndex());

    var = (SanderVariable) allVars.get("NTR");
    NTR_ComboBox.removeAllItems();
    NTR_ComboBox.addItem("No");
    NTR_ComboBox.addItem("Yes");
    NTR_ComboBox.setSelectedIndex(var.getSelectedIndex());

    var = (SanderVariable) allVars.get("IBELLY");
    IBELLY_ComboBox.removeAllItems();
    IBELLY_ComboBox.addItem("No");
    IBELLY_ComboBox.addItem("Yes");
    IBELLY_ComboBox.setSelectedIndex(var.getSelectedIndex());
    comboBoxAdjusting = false;

    // --- More of Special setup

    if (IGB_ComboBox.getSelectedIndex() == 0) {
      jTabbedPane1.setEnabledAt(5, false);
    }
    else {
      jTabbedPane1.setEnabledAt(5, true);
    }

    if (IMIN_ComboBox.getSelectedIndex() == 0) {
      jTabbedPane1.setEnabledAt(2, true);
    }
    else {
      jTabbedPane1.setEnabledAt(2, false);
    }
  }

  /**
   *
   * @param amberVarName String
   * @param jBox JComboBox
   * @param use_values boolean - if "true" use variable's possible values,
   * otherwise use values descriptions
   */
  public void setupList(String amberVarName, JComboBox jBox,
                        boolean use_values) {
    if (s8jc == null) {
      JOptionPane.showMessageDialog(this,
                                    "INTERNAL ERROR: setupList: s8jc == null",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }
    comboBoxAdjusting = true;
    jBox.removeAllItems();

    Map allVars = s8jc.getAllVariablesInfo();
    SanderVariable var = (SanderVariable) allVars.get(amberVarName);

    if (var == null) {
      JOptionPane.showMessageDialog(this,
                                    "INTERNAL ERROR: setupList: var == null : var name: " +
                                    amberVarName,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      comboBoxAdjusting = false;
      return;
    }

    String s_value;
    for (int i = 0; i < var.getNumberOfValues(); i++) {
      SanderVariableValue value = var.getValue(i);

      if (use_values) {
        s_value = value.getValue();
      }
      else {
        s_value = value.getDescription();
      }

      if (value.isDefault()) {
        s_value += " (default)";
      }
      jBox.addItem(s_value);
    }

    if (var.getSelectedIndex() == -1) {
      JOptionPane.showMessageDialog(this,
                                    "INTERNAL ERROR: setupList: var.getSelectedIndex() == -1 : var name: " +
                                    amberVarName,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);

    }
    else {
      jBox.setSelectedIndex(var.getSelectedIndex());
    }
    comboBoxAdjusting = false;
    //logger.info("Var: " + amberVarName + " selected: " +
    //                   var.getSelectedIndex());
    controlsTable.put(amberVarName, jBox);
    this.pack();
  }

  public void setupValue(String amberVarName, JTextField jText) {
    if (s8jc == null) {
      return;
    }
    jText.removeAll();

    Map allVars = s8jc.getAllVariablesInfo();
    SanderVariable var = (SanderVariable) allVars.get(amberVarName);

    if (var == null || var.isEnumerated()) {
      JOptionPane.showMessageDialog(this,
                                    "INTERNAL ERROR: setupValue: var == null || var.isEnumerated(): var name: " +
                                    amberVarName,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    String s_value;

    //SanderVariableValue value = var.getValue(0);
    //s_value = value.getValue();

    s_value = var.getValue();

    jText.setText(s_value);
    jText.setToolTipText("Enter new Value and Press Enter");
    controlsTable.put(amberVarName, jText);
    this.pack();
  }

  void showVarDescription(String var_name) {
    Map allVars = s8jc.getAllVariablesInfo();
    SanderVariable var = (SanderVariable) allVars.get(var_name);
    descriptionPane.removeAll();
    descriptionPane.setText(var.getDescription());
  }

  void setNewValue(String amberVarName, JTextField new_value) {
    Map allVars = s8jc.getAllVariablesInfo();
    SanderVariable var = (SanderVariable) allVars.get(amberVarName);
    String message = var.setValue(new_value.getText());
    if (message == null) {
      descriptionPane.removeAll();
      descriptionPane.setText("Set " + amberVarName + " = " +
                              new_value.getText());
    }
    else {
      JOptionPane.showMessageDialog(this,
                                    message,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
    }

  }

  /**
   *
   * @param var_name String
   * @param comboBox JComboBox
   */
  void showValueDescription(String var_name, JComboBox comboBox) {
    if (comboBoxAdjusting) {
      return;
    }
    if (comboBox.getItemCount() == 0) {
      return;
    }
    Map allVars = s8jc.getAllVariablesInfo();
    SanderVariable var = (SanderVariable) allVars.get(var_name);
    SanderVariableValue value = var.getValue(comboBox.getSelectedIndex());
    descriptionPane.removeAll();
    descriptionPane.setText(value.getDescription());
    var.setSelectedIndex(comboBox.getSelectedIndex());
  }

  /**
   * File | Exit action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
    System.exit(0);
  }

  /**
   * Help | About action performed.
   *
   * @param actionEvent ActionEvent
   */
  void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
    Sander9Frame_AboutBox dlg = new Sander9Frame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }

  public void forceEvalButton_actionPerformed(ActionEvent e) {
    showVarDescription("NTF");
  }

  public void NTF_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTF", NTF_ComboBox);
  }

  public void NTB_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTB", NTB_ComboBox);
  }

  public void DIELC_Button_actionPerformed(ActionEvent e) {
    showVarDescription("DIELC");
  }

  public void CUT_Button_actionPerformed(ActionEvent e) {
    showVarDescription("CUT");
  }

  public void SCNB_Button_actionPerformed(ActionEvent e) {
    showVarDescription("SCNB");
  }

  public void NBListButton_actionPerformed(ActionEvent e) {
    showVarDescription("NSNB");
  }

  public void SCEE_Button_actionPerformed(ActionEvent e) {
    showVarDescription("SCEE");
  }

  public void jButton5_actionPerformed(ActionEvent e) {
    showVarDescription("IPOL");
  }

  public void IPOL_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("IPOL", IPOL_ComboBox);
  }

  public void IMIN_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("IMIN", IMIN_ComboBox);

    CardLayout cl = (CardLayout) cardsPanel.getLayout();
    if (IMIN_ComboBox.getSelectedIndex() == 0) {
      cl.show(cardsPanel, "mdCard");
      jTabbedPane1.setEnabledAt(2, true);
    }
    else if (IMIN_ComboBox.getSelectedIndex() == 1) {
      cl.show(cardsPanel, "minCard");
      jTabbedPane1.setEnabledAt(2, false);
    }
  }

  public void NTMIN_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTMIN", NTMIN_ComboBox);
  }

  public void jLabel6_mouseEntered(MouseEvent e) {
    showVarDescription("NTMIN");
  }

  public void maxIterLabel_mouseEntered(MouseEvent e) {
    showVarDescription("MAXCYC");
  }

  public void switchLabel_mouseEntered(MouseEvent e) {
    showVarDescription("NCYC");
  }

  public void dxLabel_mouseEntered(MouseEvent e) {
    showVarDescription("DX0");
  }

  public void covergLabel_mouseEntered(MouseEvent e) {
    showVarDescription("DRMS");
  }

  public void IREST_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("IREST", IREST_ComboBox);
  }

  public void jLabel10_mouseEntered(MouseEvent e) {
    showVarDescription("NSCM");
  }

  public void NRESPA_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NRESPA");
  }

  public void T_Label_mouseEntered(MouseEvent e) {
    showVarDescription("T");
  }

  public void NTT_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTT");
  }

  public void TEMP0LES_Label_mouseEntered(MouseEvent e) {
    showVarDescription("TEMP0LES");
  }

  public void IG_Label_mouseEntered(MouseEvent e) {
    showVarDescription("IG");
  }

  public void TAUTP_Label_mouseEntered(MouseEvent e) {
    showVarDescription("TAUTP");
  }

  public void GAMMA_LN_Label_mouseEntered(MouseEvent e) {
    showVarDescription("GAMMA_LN");
  }

  public void VRAND_Label_mouseEntered(MouseEvent e) {
    showVarDescription("VRAND");
  }

  public void NTP_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTP");
  }

  public void TEMP0_Label_mouseEntered(MouseEvent e) {
    showVarDescription("TEMP0");
  }

  public void NSTLIM_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NSTLIM");
  }

  public void DT_Label_mouseEntered(MouseEvent e) {
    showVarDescription("DT");
  }

  public void COMP_Label_mouseEntered(MouseEvent e) {
    showVarDescription("COMP");
  }

  public void TAUP_Label_mouseEntered(MouseEvent e) {
    showVarDescription("TAUP");
  }

  public void PRES0_Label_mouseEntered(MouseEvent e) {
    showVarDescription("PRES0");
  }

  public void NTT_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTT", NTT_ComboBox);
  }

  public void NTP_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTP", NTP_ComboBox);
  }

  public void VLIMIT_Label_mouseEntered(MouseEvent e) {
    showVarDescription("VLIMIT");
  }

  public void TEMPI_Label_mouseEntered(MouseEvent e) {
    showVarDescription("TEMPI");
  }

  public void NSNB_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NSNB");
  }

  public void SCEE_Label_mouseEntered(MouseEvent e) {
    showVarDescription("SCEE");
  }

  public void IPOL_Label_mouseEntered(MouseEvent e) {
    showVarDescription("IPOL");
  }

  public void NTF_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTF");
  }

  public void NTB_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTB");
  }

  public void jButton1_actionPerformed(ActionEvent e) {
    FileDialog fd = new FileDialog(this, "Open Sander 9 Job Control File",
                                   FileDialog.LOAD);
    fd.setFile("*.in;*.sh");
    fd.setVisible(true);
    if (fd.getFile() != null) {
      fileName = new String(fd.getFile());
      workingDirectory = new String(fd.getDirectory());
      s8jc = new Sander9JobControl();
      setupControls();
      parseInputData(IOUtils.loadFileIntoString(workingDirectory + fileName));
    }
  }

  public Map parseInputData(String data) {
    fileContent = data;
    if (cntrl == null) {
      cntrl = new FortranNamelist();
    }
    Map vars = Sander8JobControl.getGeneralParameters(fileContent, 1,
        cntrl, "cntrl");
    if (s8jc.setCntrlVariables(vars)) {

    }
    else {
      JOptionPane.showMessageDialog(this,
                                    s8jc.getErrorMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
    }
    setupControls();
    return vars;
  }

  public void jButton2_actionPerformed(ActionEvent e) {
    s8jc = new Sander9JobControl();
    this.setupControls();
    fileContent = null;
    JOptionPane.showMessageDialog(this,
                                  "All variables are reset to their default values",
                                  "Info",
                                  JOptionPane.INFORMATION_MESSAGE);

  }

  public void jButton3_actionPerformed(ActionEvent e) {
    JOptionPane.showMessageDialog(this,
                                  "Program allows to edit \"cntrl\" namelist for Sander 9\n" +
                                  "Select the tab corresponding to the desired component\n" +
                                  "Set options using either text fields or combo boxes\n" +
                                  "Toolbar provides access to commonly used features:\n" +
                                  "     Open Sander 9 job control file\n" +
                                  "     Reset the options to their default values\n" +
                                  "     Save File, and Help\n" +
                                  "To see the description for a particular option point your cursor to an option\n" +
                                  "     and its description will be in the Option Description panel",
                                  "Help",
                                  JOptionPane.INFORMATION_MESSAGE);
  }

  public void NTRX_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTRX");
  }

  public void NTX_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTX");
  }

  public void NTWR_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTWR");
  }

  public void NTAVE_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTAVE");
  }

  public void IWRAP_Label_mouseEntered(MouseEvent e) {
    showVarDescription("IWRAP");
  }

  public void NTXO_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTXO");
  }

  public void NTPR_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTPR");
  }

  public void NTWX_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTWX");
  }

  public void NTWV_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTWV");
  }

  public void NTWE_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTWE");
  }

  public void IOUTFM_Label_mouseEntered(MouseEvent e) {
    showVarDescription("IOUTFM");
  }

  public void NTWPRT_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTWPRT");
  }

  public void IDECOMP_Label_mouseEntered(MouseEvent e) {
    showVarDescription("IDECOMP");
  }

  public void INTDIEL_Label_mouseEntered(MouseEvent e) {
    showVarDescription("INTDIEL");
  }

  public void EXTDIEL_Label_mouseEntered(MouseEvent e) {
    showVarDescription("EXTDIEL");
  }

  public void SALTCON_Label_mouseEntered(MouseEvent e) {
    showVarDescription("SALTCON");
  }

  public void RGBMAX_Label_mouseEntered(MouseEvent e) {
    showVarDescription("RGBMAX");
  }

  public void RBORNSTAT_Label_mouseEntered(MouseEvent e) {
    showVarDescription("RBORNSTAT");
  }

  public void OFFSET_Label_mouseEntered(MouseEvent e) {
    showVarDescription("OFFSET");
  }

  public void GBSA_Label_mouseEntered(MouseEvent e) {
    showVarDescription("GBSA");
  }

  public void SURFTEN_Label_mouseEntered(MouseEvent e) {
    showVarDescription("SURFTEN");
  }

  public void RDT_Label_mouseEntered(MouseEvent e) {
    showVarDescription("RDT");
  }

  public void RBORNSTAT_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("RBORNSTAT", RBORNSTAT_ComboBox);
  }

  public void GBSA_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("GBSA", GBSA_ComboBox);
  }

  public void NTXO_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTXO", NTXO_ComboBox);
  }

  public void IWRAP_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("IWRAP", IWRAP_ComboBox);
  }

  public void IOUTFM_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("IOUTFM", IOUTFM_ComboBox);
  }

  public void IDECOMP_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("IDECOMP", IDECOMP_ComboBox);
  }

  public void NTX_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTX", NTX_ComboBox);
  }

  public void NTRX_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTRX", NTRX_ComboBox);
  }

  public void DIELC_Label_mouseEntered(MouseEvent e) {
    showVarDescription("DIELC");
  }

  public void CUT_Label_mouseEntered(MouseEvent e) {
    showVarDescription("CUT");
  }

  public void SCNB_Label_mouseEntered(MouseEvent e) {
    showVarDescription("SCNB");
  }

  public void Edit_MenuItem_actionPerformed(ActionEvent e) {
    if (inputEditor == null) {
      inputEditor = new TextEditorFrame(this, "Text Editor", "");
      inputEditor.setSize(450, 300);
    }
    //this.setEnabled(false);
    inputEditor.setVisible(true);
    if (fileContent == null) {
      fileContent = generateFileContents(
          "This is automatically generated file");
    }
    else {
      fileContent = updateFileContents(fileContent, "cntrl");
    }
    inputEditor.setTextToEdit(fileContent);
    this.setEnabled(false);
  }

  String updateFileContents(String data, String namelistName) {
    Map vars = parseInputData(data);
    if (vars.size() < 1) {
      return data; // i.e. no cntrl namelist
    }

    Map allVars = s8jc.getAllVariablesInfo();
    Map cntrlVars = cntrl.getVariables();
    String mess = Sander8JobControl.updateNamelistVariables(cntrlVars,
        allVars);
    if (mess != null) {
      JOptionPane.showMessageDialog(this,
                                    mess, "Warning",
                                    JOptionPane.WARNING_MESSAGE);
    }

    StringReader sReader = new StringReader(data);
    StringWriter sWriter = new StringWriter();

    try {
      int character, count = 0;
      char cbuf[] = new char[120];
      String line = null;
      boolean skippingNamelist = false;
      boolean theRest = false;

      while ( (character = sReader.read()) != -1) {
        if (theRest) {
          sWriter.write(character);
          continue;
        }

        if (character == '\n') {
          if (line == null) {
            line = String.copyValueOf(cbuf, 0, count);
          }
          else {
            line += String.copyValueOf(cbuf, 0, count);
          }
          count = 0;

          if (skippingNamelist &&
              cct.tools.FortranNamelist.hasNamelistEnd(line)) {
            theRest = true;
            continue;
          }
          else if (skippingNamelist) {
            continue;
          }

          if (cct.tools.FortranNamelist.hasNamelistStart(line, "cntrl")) {
            cct.tools.FortranNamelist.writeNamelistBody(sWriter, "cntrl",
                cntrlVars);
            skippingNamelist = true;
          }

          if (cct.tools.FortranNamelist.hasNamelistEnd(line)) {
            theRest = true;
            continue;
          }

          if (skippingNamelist) {
            continue;
          }

          sWriter.write(line + "\n");
          line = null;

        }
        else if (count < 120) {
          cbuf[count] = (char) character;
          ++count;
        }
        else {
          if (line == null) {
            line = String.copyValueOf(cbuf, 0, count);
          }
          else {
            line += String.copyValueOf(cbuf, 0, count);
          }
          count = 0;
        }
      }
    }
    catch (IOException er) {

    }

    return sWriter.toString();
  }

  /**
   *
   * @param title String
   * @return String
   */
  String generateFileContents(String title) {

    StringWriter sWriter = new StringWriter();
    sWriter.write(title + "\n &cntrl\n");

    Map allVars = s8jc.getAllVariablesInfo();
    SanderVariable var;

    Set set = controlsTable.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String option = me.getKey().toString();
      var = (SanderVariable) allVars.get(option);
      Object control = me.getValue();

      if (control instanceof JComboBox) {
        JComboBox jBox = (JComboBox) control;
        if (var.isDefaultValue()) {
          continue;
        }
        sWriter.write("     " + option + "=" + var.getValue() + "\n");
      }
      else if (control instanceof JTextField) {
        JTextField jText = (JTextField) control;
        String value = jText.getText().trim();
        String reference = var.getValue();

        if (!reference.equalsIgnoreCase(value)) {
          String message = var.setValue(value);
          if (message != null) {
            JOptionPane.showMessageDialog(this,
                                          option + ": " + message,
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            setupValue(option, jText);
            continue;
          }
        }
        if (var.isDefaultValue()) {
          continue;
        }
        sWriter.write("     " + option + "=" + var.getValue() + "\n");
      }

    }

    sWriter.write(" &end\n");
    return sWriter.toString();
  }

  public void IGB_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("IGB", IGB_ComboBox);
    if (IGB_ComboBox.getSelectedIndex() == 0) {
      jTabbedPane1.setEnabledAt(5, false);
    }
    else {
      jTabbedPane1.setEnabledAt(5, true);
    }
  }

  public void NTR_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTR");
  }

  public void NTR_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTR", NTR_ComboBox);
  }

  public void IBELLY_Label_mouseEntered(MouseEvent e) {
    showVarDescription("IBELLY");
  }

  public void IBELLY_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("IBELLY", IBELLY_ComboBox);
  }

  public void RESTRAINT_WT_Label_mouseEntered(MouseEvent e) {
    showVarDescription("RESTRAINT_WT");
  }

  public void RESTRAINTMASK_Label_mouseEntered(MouseEvent e) {
    showVarDescription("RESTRAINTMASK");
  }

  public void BELLYMASK_Label_mouseEntered(MouseEvent e) {
    showVarDescription("BELLYMASK");
  }

  public void IVCAP_Label_mouseEntered(MouseEvent e) {
    showVarDescription("IVCAP");
  }

  public void IVCAP_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("IVCAP", IVCAP_ComboBox);
  }

  public void FCAP_Label_mouseEntered(MouseEvent e) {
    showVarDescription("FCAP");
  }

  public void NTC_Label_mouseEntered(MouseEvent e) {
    showVarDescription("NTC");
  }

  public void NTC_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("NTC", NTC_ComboBox);
  }

  public void JFASTW_Label_mouseEntered(MouseEvent e) {
    showVarDescription("JFASTW");
  }

  public void JFASTW_ComboBox_itemStateChanged(ItemEvent e) {
    showValueDescription("JFASTW", JFASTW_ComboBox);
  }

  public void TOL_Label_mouseEntered(MouseEvent e) {
    showVarDescription("TOL");
  }

  public void WATNAM_Label_mouseEntered(MouseEvent e) {
    showVarDescription("WATNAM");
  }

  public void OWTNM_Label_mouseEntered(MouseEvent e) {
    showVarDescription("OWTNM");
  }

  public void HWTNM1_Label_mouseEntered(MouseEvent e) {
    showVarDescription("HWTNM1");
  }

  public void HWTNM2_Label_mouseEntered(MouseEvent e) {
    showVarDescription("HWTNM2");
  }

  public void NCYC_TextField_actionPerformed(ActionEvent e) {
    setNewValue("NCYC", NCYC_TextField);
  }

  public void DX0_TextField_actionPerformed(ActionEvent e) {
    setNewValue("DX0", DX0_TextField);
  }

  public void DRMS_TextField_actionPerformed(ActionEvent e) {
    setNewValue("DRMS", DRMS_TextField);
  }

  public void MAXCYC_TextField_actionPerformed(ActionEvent e) {
    setNewValue("MAXCYC", MAXCYC_TextField);
  }

  public void IMIN_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("IMIN", IMIN_ComboBox);
  }

  public void IREST_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("IREST", IREST_ComboBox);
  }

  public void IGB_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("IGB", IGB_ComboBox);
  }

  public void NTMIN_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTMIN", NTMIN_ComboBox);
  }

  public void NTT_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTT", NTT_ComboBox);
  }

  public void NTP_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTP", NTP_ComboBox);
  }

  public void NSTLIM_TextField_actionPerformed(ActionEvent e) {
    setNewValue("NSTLIM", NSTLIM_TextField);
  }

  public void saveFile_Button_actionPerformed(ActionEvent e) {
    if (fileContent == null) {
      fileContent = generateFileContents(
          "This is automatically generated file");
    }
    else {
      fileContent = updateFileContents(fileContent, "cntrl");
    }

    FileDialog fd = new FileDialog(this, "Save Sander 9 Job Control File",
                                   FileDialog.SAVE);

    if (fileName == null) {
      fileName = "sander9.in";
    }
    if (workingDirectory == null) {
      workingDirectory = "./";
    }

    fd.setFile(fileName);
    fd.setDirectory(workingDirectory);
    fd.setVisible(true);
    if (fd.getFile() != null) {
      fileName = new String(fd.getFile());
      workingDirectory = new String(fd.getDirectory());
      try {
        IOUtils.saveStringIntoFile(fileContent, workingDirectory + fileName);
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                                      ex.getMessage(), "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }

  }

  public void NTF_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTF", NTF_ComboBox);
  }

  public void IPOL_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("IPOL", IPOL_ComboBox);
  }

  public void NTB_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTB", NTB_ComboBox);
  }

  public void NTX_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTX", NTX_ComboBox);
  }

  public void NTRX_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTRX", NTRX_ComboBox);
  }

  public void NTXO_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTXO", NTXO_ComboBox);
  }

  public void IWRAP_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("IWRAP", IWRAP_ComboBox);
  }

  public void IOUTFM_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("IOUTFM", IOUTFM_ComboBox);
  }

  public void IDECOMP_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("IDECOMP", IDECOMP_ComboBox);
  }

  public void NTR_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTR", NTR_ComboBox);
  }

  public void IBELLY_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("IBELLY", IBELLY_ComboBox);
  }

  public void IVCAP_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("IVCAP", IVCAP_ComboBox);
  }

  public void NTC_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("NTC", NTC_ComboBox);
  }

  public void JFASTW_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("JFASTW", JFASTW_ComboBox);
  }

  public void RBORNSTAT_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("RBORNSTAT", RBORNSTAT_ComboBox);
  }

  public void GBSA_ComboBox_mouseEntered(MouseEvent e) {
    showValueDescription("GBSA", GBSA_ComboBox);
  }

  public void jMenuItem1_actionPerformed(ActionEvent e) {
    jButton1_actionPerformed(e);
  }

  public void jMenuItem2_actionPerformed(ActionEvent e) {
    saveFile_Button_actionPerformed(e);
  }
}

class Sander9Frame_jMenuItem2_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_jMenuItem2_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem2_actionPerformed(e);
  }
}

class Sander9Frame_jMenuItem1_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_jMenuItem1_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem1_actionPerformed(e);
  }
}

class Sander9Frame_NSTLIM_TextField_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_NSTLIM_TextField_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.NSTLIM_TextField_actionPerformed(e);
  }
}

class Sander9Frame_HWTNM2_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_HWTNM2_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.HWTNM2_Label_mouseEntered(e);
  }
}

class Sander9Frame_HWTNM1_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_HWTNM1_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.HWTNM1_Label_mouseEntered(e);
  }
}

class Sander9Frame_OWTNM_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_OWTNM_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.OWTNM_Label_mouseEntered(e);
  }
}

class Sander9Frame_WATNAM_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_WATNAM_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.WATNAM_Label_mouseEntered(e);
  }
}

class Sander9Frame_TOL_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_TOL_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.TOL_Label_mouseEntered(e);
  }
}

class Sander9Frame_JFASTW_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_JFASTW_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.JFASTW_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_RBORNSTAT_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_RBORNSTAT_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.RBORNSTAT_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_JFASTW_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_JFASTW_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.JFASTW_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_JFASTW_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_JFASTW_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.JFASTW_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTC_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTC_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTC_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_FCAP_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_FCAP_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.FCAP_Label_mouseEntered(e);
  }
}

class Sander9Frame_IVCAP_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_IVCAP_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.IVCAP_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_NTC_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTC_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTC_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_NTC_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTC_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTC_Label_mouseEntered(e);
  }
}

class Sander9Frame_IVCAP_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IVCAP_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IVCAP_Label_mouseEntered(e);
  }
}

class Sander9Frame_BELLYMASK_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_BELLYMASK_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.BELLYMASK_Label_mouseEntered(e);
  }
}

class Sander9Frame_RESTRAINTMASK_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_RESTRAINTMASK_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.RESTRAINTMASK_Label_mouseEntered(e);
  }
}

class Sander9Frame_IBELLY_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_IBELLY_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.IBELLY_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_IVCAP_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IVCAP_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IVCAP_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_IBELLY_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IBELLY_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IBELLY_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_IBELLY_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IBELLY_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IBELLY_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTR_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTR_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTR_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_RESTRAINT_WT_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_RESTRAINT_WT_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.RESTRAINT_WT_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTR_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTR_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTR_Label_mouseEntered(e);
  }
}

class Sander9Frame_IGB_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_IGB_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.IGB_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_IGB_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IGB_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IGB_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_SCNB_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_SCNB_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.SCNB_Label_mouseEntered(e);
  }
}

class Sander9Frame_CUT_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_CUT_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.CUT_Label_mouseEntered(e);
  }
}

class Sander9Frame_DIELC_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_DIELC_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.DIELC_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTRX_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTRX_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTRX_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_NTRX_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTRX_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTRX_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_IDECOMP_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_IDECOMP_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.IDECOMP_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_NTR_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTR_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTR_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_IDECOMP_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IDECOMP_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IDECOMP_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_NTX_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTX_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTX_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_NTX_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTX_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTX_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_IOUTFM_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_IOUTFM_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.IOUTFM_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_IOUTFM_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IOUTFM_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IOUTFM_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_IWRAP_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_IWRAP_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.IWRAP_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_NTXO_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTXO_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTXO_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_IWRAP_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IWRAP_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IWRAP_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_NTXO_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTXO_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTXO_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_GBSA_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_GBSA_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.GBSA_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_RBORNSTAT_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_RBORNSTAT_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.RBORNSTAT_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_GBSA_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_GBSA_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.GBSA_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_RDT_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_RDT_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.RDT_Label_mouseEntered(e);
  }
}

class Sander9Frame_SURFTEN_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_SURFTEN_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.SURFTEN_Label_mouseEntered(e);
  }
}

class Sander9Frame_GBSA_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_GBSA_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.GBSA_Label_mouseEntered(e);
  }
}

class Sander9Frame_OFFSET_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_OFFSET_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.OFFSET_Label_mouseEntered(e);
  }
}

class Sander9Frame_RBORNSTAT_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_RBORNSTAT_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.RBORNSTAT_Label_mouseEntered(e);
  }
}

class Sander9Frame_RGBMAX_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_RGBMAX_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.RGBMAX_Label_mouseEntered(e);
  }
}

class Sander9Frame_SALTCON_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_SALTCON_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.SALTCON_Label_mouseEntered(e);
  }
}

class Sander9Frame_EXTDIEL_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_EXTDIEL_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.EXTDIEL_Label_mouseEntered(e);
  }
}

class Sander9Frame_INTDIEL_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_INTDIEL_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.INTDIEL_Label_mouseEntered(e);
  }
}

class Sander9Frame_IDECOMP_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IDECOMP_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IDECOMP_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTWPRT_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTWPRT_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTWPRT_Label_mouseEntered(e);
  }
}

class Sander9Frame_IOUTFM_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IOUTFM_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IOUTFM_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTWE_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTWE_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTWE_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTWV_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTWV_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTWV_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTPR_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTPR_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTPR_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTXO_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTXO_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTXO_Label_mouseEntered(e);
  }
}

class Sander9Frame_IWRAP_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IWRAP_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IWRAP_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTWX_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTWX_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTWX_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTWR_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTWR_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTWR_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTAVE_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTAVE_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTAVE_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTX_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTX_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTX_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTRX_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTRX_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTRX_Label_mouseEntered(e);
  }
}

class Sander9Frame_jButton3_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_jButton3_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton3_actionPerformed(e);
  }
}

class Sander9Frame_jButton2_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_jButton2_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton2_actionPerformed(e);
  }
}

class Sander9Frame_jButton1_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_jButton1_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class Sander9Frame_TEMPI_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_TEMPI_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.TEMPI_Label_mouseEntered(e);
  }
}

class Sander9Frame_VLIMIT_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_VLIMIT_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.VLIMIT_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTP_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTP_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTP_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_NTP_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTP_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTP_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_NTT_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTT_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTT_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_NTT_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTT_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTT_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_PRES0_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_PRES0_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.PRES0_Label_mouseEntered(e);
  }
}

class Sander9Frame_TAUP_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_TAUP_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.TAUP_Label_mouseEntered(e);
  }
}

class Sander9Frame_COMP_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_COMP_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.COMP_Label_mouseEntered(e);
  }
}

class Sander9Frame_DT_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_DT_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.DT_Label_mouseEntered(e);
  }
}

class Sander9Frame_NSTLIM_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NSTLIM_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NSTLIM_Label_mouseEntered(e);
  }
}

class Sander9Frame_TEMP0_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_TEMP0_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.TEMP0_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTP_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTP_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTP_Label_mouseEntered(e);
  }
}

class Sander9Frame_VRAND_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_VRAND_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.VRAND_Label_mouseEntered(e);
  }
}

class Sander9Frame_GAMMA_LN_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_GAMMA_LN_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.GAMMA_LN_Label_mouseEntered(e);
  }
}

class Sander9Frame_TAUTP_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_TAUTP_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.TAUTP_Label_mouseEntered(e);
  }
}

class Sander9Frame_IG_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IG_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IG_Label_mouseEntered(e);
  }
}

class Sander9Frame_TEMP0LES_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_TEMP0LES_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.TEMP0LES_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTT_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTT_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTT_Label_mouseEntered(e);
  }
}

class Sander9Frame_T_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_T_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.T_Label_mouseEntered(e);
  }
}

class Sander9Frame_NRESPA_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NRESPA_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NRESPA_Label_mouseEntered(e);
  }
}

class Sander9Frame_jLabel10_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_jLabel10_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.jLabel10_mouseEntered(e);
  }
}

class Sander9Frame_IREST_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_IREST_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.IREST_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_IREST_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IREST_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IREST_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_covergLabel_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_covergLabel_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.covergLabel_mouseEntered(e);
  }
}

class Sander9Frame_dxLabel_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_dxLabel_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.dxLabel_mouseEntered(e);
  }
}

class Sander9Frame_switchLabel_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_switchLabel_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.switchLabel_mouseEntered(e);
  }
}

class Sander9Frame_maxIterLabel_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_maxIterLabel_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.maxIterLabel_mouseEntered(e);
  }
}

class Sander9Frame_NTMIN_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTMIN_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTMIN_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_NTMIN_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTMIN_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTMIN_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_saveFile_Button_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_saveFile_Button_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.saveFile_Button_actionPerformed(e);
  }
}

class Sander9Frame_SCEE_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_SCEE_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.SCEE_Label_mouseEntered(e);
  }
}

class Sander9Frame_NSNB_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NSNB_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NSNB_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTF_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTF_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTF_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_IPOL_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IPOL_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IPOL_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_NTF_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTF_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTF_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_NTF_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTF_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTF_Label_mouseEntered(e);
  }
}

class Sander9Frame_IPOL_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IPOL_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IPOL_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTB_Label_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTB_Label_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTB_Label_mouseEntered(e);
  }
}

class Sander9Frame_NTB_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_NTB_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.NTB_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_IPOL_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_IPOL_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.IPOL_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_NTB_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_NTB_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.NTB_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_IMIN_ComboBox_itemAdapter
    implements ItemListener {
  private Sander9Frame adaptee;
  Sander9Frame_IMIN_ComboBox_itemAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void itemStateChanged(ItemEvent e) {
    adaptee.IMIN_ComboBox_itemStateChanged(e);
  }
}

class Sander9Frame_IMIN_ComboBox_mouseAdapter
    extends MouseAdapter {
  private Sander9Frame adaptee;
  Sander9Frame_IMIN_ComboBox_mouseAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.IMIN_ComboBox_mouseEntered(e);
  }
}

class Sander9Frame_jMenuFileExit_ActionAdapter
    implements ActionListener {
  Sander9Frame adaptee;

  Sander9Frame_jMenuFileExit_ActionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.jMenuFileExit_actionPerformed(actionEvent);
  }
}

class Sander9Frame_Edit_MenuItem_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_Edit_MenuItem_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.Edit_MenuItem_actionPerformed(e);
  }
}

class Sander9Frame_NCYC_TextField_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_NCYC_TextField_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.NCYC_TextField_actionPerformed(e);
  }
}

class Sander9Frame_DX0_TextField_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_DX0_TextField_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.DX0_TextField_actionPerformed(e);
  }
}

class Sander9Frame_DRMS_TextField_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_DRMS_TextField_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.DRMS_TextField_actionPerformed(e);
  }
}

class Sander9Frame_MAXCYC_TextField_actionAdapter
    implements ActionListener {
  private Sander9Frame adaptee;
  Sander9Frame_MAXCYC_TextField_actionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.MAXCYC_TextField_actionPerformed(e);
  }
}

class Sander9Frame_jMenuHelpAbout_ActionAdapter
    implements ActionListener {
  Sander9Frame adaptee;

  Sander9Frame_jMenuHelpAbout_ActionAdapter(Sander9Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent actionEvent) {
    adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
  }
}
