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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

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
public class Sander9JobPanel
    extends JPanel {
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel directoriesPanel = new JPanel();
   JPanel outputPanel = new JPanel();
   JPanel inputPanel = new JPanel();
   JLabel remoteDirLabel = new JLabel();
   JTextField remoteDirTextField = new JTextField();
   JButton scriptButton = new JButton();
   JLabel localDirLabel = new JLabel();
   JLabel jobNameLabel = new JLabel();
   JTextField jobNameTextField = new JTextField();
   JTextField localDirTextField = new JTextField();
   JLabel scriptLabel = new JLabel();
   JTextField scriptTextField = new JTextField();
   JButton jButton2 = new JButton();
   JButton localDirButton = new JButton();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   JLabel prmtopLabel = new JLabel();
   JTextField mdinTextField = new JTextField();
   JCheckBox mdinCheckBox = new JCheckBox();
   JButton mdinButton = new JButton();
   JLabel mdinLabel = new JLabel();
   JLabel evbinLabel = new JLabel();
   JLabel cpinLabel = new JLabel();
   JLabel inpdipLabel = new JLabel();
   JLabel inptrajLabel = new JLabel();
   JLabel refcLabel = new JLabel();
   JLabel inpcrdLabel = new JLabel();
   JTextField evbinTextField = new JTextField();
   JTextField cpinTextField = new JTextField();
   JTextField inpdipTextField = new JTextField();
   JTextField inptrajTextField = new JTextField();
   JTextField refcTextField = new JTextField();
   JTextField inpcrdTextField = new JTextField();
   JTextField prmtopTextField = new JTextField();
   JCheckBox evbinCheckBox = new JCheckBox();
   JCheckBox cpinCheckBox = new JCheckBox();
   JCheckBox inpdipCheckBox = new JCheckBox();
   JCheckBox inptrajCheckBox = new JCheckBox();
   JCheckBox refcCheckBox = new JCheckBox();
   JCheckBox inpcrdCheckBox = new JCheckBox();
   JCheckBox prmtopCheckBox = new JCheckBox();
   JButton evbinButton = new JButton();
   JButton cpinButton = new JButton();
   JButton inpdipButton = new JButton();
   JButton inptrajButton = new JButton();
   JButton refcButton = new JButton();
   JButton inpcrdButton = new JButton();
   JButton prmtopButton = new JButton();
   GridBagLayout gridBagLayout2 = new GridBagLayout();
   JLabel jLabel1 = new JLabel();
   JTextField jTextField1 = new JTextField();
   JCheckBox jCheckBox1 = new JCheckBox();
   JLabel jLabel2 = new JLabel();
   JLabel jLabel3 = new JLabel();
   JLabel jLabel4 = new JLabel();
   JLabel mdvelLabel = new JLabel();
   JLabel mdoutLabel = new JLabel();
   JLabel mdcrdLabel = new JLabel();
   JLabel mdinfoLabel = new JLabel();
   JTextField jTextField2 = new JTextField();
   JTextField jTextField3 = new JTextField();
   JTextField mdcrdTextField = new JTextField();
   JTextField jTextField5 = new JTextField();
   JTextField mdvelTextField = new JTextField();
   JTextField mdinfoTextField = new JTextField();
   JTextField mdoutTextField = new JTextField();
   JCheckBox jCheckBox2 = new JCheckBox();
   JCheckBox jCheckBox3 = new JCheckBox();
   JCheckBox jCheckBox4 = new JCheckBox();
   JCheckBox mdvelCheckBox = new JCheckBox();
   JCheckBox mdcrdCheckBox = new JCheckBox();
   JCheckBox mdinfoCheckBox = new JCheckBox();
   JCheckBox mdoutCheckBox = new JCheckBox();
   JPanel jPanel1 = new JPanel();
   BorderLayout borderLayout2 = new BorderLayout();
   GridBagLayout gridBagLayout3 = new GridBagLayout();
   JTabbedPane jTabbedPane1 = new JTabbedPane();
   public Sander9JobPanel() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      directoriesPanel.setLayout(gridBagLayout1);
      remoteDirLabel.setToolTipText("");
      remoteDirLabel.setText("Remote Directory: ");
      remoteDirTextField.setToolTipText("");
      remoteDirTextField.setText("Remote Dir Here");
      scriptButton.setToolTipText("");
      scriptButton.setText("Edit");
      localDirLabel.setToolTipText("");
      localDirLabel.setText("Local Directory:");
      jobNameLabel.setToolTipText("");
      jobNameLabel.setText("Job Name: ");
      jobNameTextField.setToolTipText("");
      jobNameTextField.setText("Job Name Here");
      localDirTextField.setToolTipText("");
      localDirTextField.setText("Local Dir Here");
      scriptLabel.setToolTipText("");
      scriptLabel.setText("Run Script: ");
      scriptTextField.setToolTipText("");
      scriptTextField.setText("Script Here");
      jButton2.setToolTipText("");
      jButton2.setActionCommand("remoteDirButton");
      jButton2.setText("Browse");
      localDirButton.setToolTipText("");
      localDirButton.setText("Browse");
      directoriesPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.
          RAISED, Color.white, Color.white, new Color(115, 114, 105),
          new Color(165, 163, 151)));
      prmtopLabel.setToolTipText("");
      prmtopLabel.setText("Topology (prmtop): ");
      mdinTextField.setToolTipText("Control data for the min/md run");
      mdinTextField.setText("Job Name Here");
      mdinCheckBox.setToolTipText("");
      mdinCheckBox.setText("Local File");
      mdinButton.setToolTipText("");
      mdinButton.setText("Browse");
      inputPanel.setLayout(gridBagLayout2);
      mdinLabel.setToolTipText("");
      mdinLabel.setText("Control Data (mdin): ");
      evbinLabel.setText("EVB Potentials (evbin): ");
      evbinLabel.setToolTipText("");
      cpinLabel.setText("Protonation state defs (cpin): ");
      cpinLabel.setToolTipText("");
      inpdipLabel.setText("Polarizable dipoles (inpdip): ");
      inpdipLabel.setToolTipText("");
      inptrajLabel.setText("Input Coord Sets (inptraj): ");
      inptrajLabel.setToolTipText("");
      refcLabel.setText("Reference Coords (refc): ");
      refcLabel.setToolTipText("");
      inpcrdLabel.setText("Initial Coords (inpcrd): ");
      inpcrdLabel.setToolTipText("");
      evbinTextField.setText("Job Name Here");
      evbinTextField.setToolTipText("Input for EVB potentials");
      cpinTextField.setText("Job Name Here");
      cpinTextField.setToolTipText("Protonation state definitions");
      inpdipTextField.setText("Job Name Here");
      inpdipTextField.setToolTipText("Polarizable dipole file, when indmeth=3");
      inptrajTextField.setText("Job Name Here");
      inptrajTextField.setToolTipText(
          "Input coordinate sets in trajectory format, when imin=5");
      refcTextField.setText("Job Name Here");
      refcTextField.setToolTipText(
          "(Optional) reference coords for position restraints; also used for " +
          "targeted MD");
      inpcrdTextField.setText("Job Name Here");
      inpcrdTextField.setToolTipText(
          "Initial coordinates and (optionally) velocities and periodic box " +
          "size");
      prmtopTextField.setText("Job Name Here");
      prmtopTextField.setToolTipText(
          "Molecular topology, force field, periodic box type, atom and residue " +
          "names");
      evbinCheckBox.setToolTipText("");
      evbinCheckBox.setText("Local File");
      cpinCheckBox.setToolTipText("");
      cpinCheckBox.setText("Local File");
      inpdipCheckBox.setToolTipText("");
      inpdipCheckBox.setText("Local File");
      inptrajCheckBox.setToolTipText("");
      inptrajCheckBox.setText("Local File");
      refcCheckBox.setToolTipText("");
      refcCheckBox.setText("Local File");
      inpcrdCheckBox.setToolTipText("");
      inpcrdCheckBox.setText("Local File");
      prmtopCheckBox.setToolTipText("");
      prmtopCheckBox.setText("Local File");
      evbinButton.setToolTipText("");
      evbinButton.setText("Browse");
      cpinButton.setToolTipText("");
      cpinButton.setText("Browse");
      inpdipButton.setToolTipText("");
      inpdipButton.setText("Browse");
      inptrajButton.setToolTipText("");
      inptrajButton.setText("Browse");
      refcButton.setToolTipText("");
      refcButton.setText("Browse");
      inpcrdButton.setToolTipText("");
      inpcrdButton.setText("Browse");
      prmtopButton.setToolTipText("");
      prmtopButton.setText("Browse");
      inputPanel.setBorder(new TitledBorder(BorderFactory.createBevelBorder(
          BevelBorder.LOWERED, Color.white, Color.white,
          new Color(115, 114, 105), new Color(165, 163, 151)), "Input Files"));
      jLabel1.setToolTipText("");
      jLabel1.setText("Protonation State (cpout): ");
      outputPanel.setLayout(gridBagLayout3);
      jTextField1.setToolTipText(
          "final coordinates, velocity, and box dimensions if any - for restarting " +
          "run");
      jTextField1.setText("Remote Dir Here");
      outputPanel.setBorder(new TitledBorder(BorderFactory.createBevelBorder(
          BevelBorder.RAISED, Color.white, Color.white, new Color(115, 114, 105),
          new Color(165, 163, 151)), "Output Files"));
      jCheckBox1.setToolTipText("");
      jCheckBox1.setText("Copy to Local Filesystem");
      jLabel2.setToolTipText("");
      jLabel2.setText("Polarizable Dipoles (rstdip): ");
      jLabel3.setToolTipText("");
      jLabel3.setText("Final Coords (restrt): ");
      jLabel4.setToolTipText("");
      jLabel4.setText("MD Energy Data (mden): ");
      mdvelLabel.setToolTipText("");
      mdvelLabel.setText("MD Velocities (mdvel): ");
      mdoutLabel.setToolTipText("");
      mdoutLabel.setText("Program Info (mdout): ");
      mdcrdLabel.setToolTipText("");
      mdcrdLabel.setText("MD Coordinates (mdcrd): ");
      mdinfoLabel.setToolTipText("");
      mdinfoLabel.setText("Energy Info (mdinfo): ");
      jTextField2.setToolTipText("protonation state data saved over trajectory");
      jTextField2.setText("Remote Dir Here");
      jTextField3.setToolTipText("polarizable dipole file, when indmeth=3");
      jTextField3.setText("Remote Dir Here");
      mdcrdTextField.setToolTipText("coordinate sets saved over trajectory");
      mdcrdTextField.setText("Remote Dir Here");
      jTextField5.setToolTipText("extensive energy data over trajectory");
      jTextField5.setText("Remote Dir Here");
      mdvelTextField.setToolTipText("velocity sets saved over trajectory");
      mdvelTextField.setText("Remote Dir Here");
      mdinfoTextField.setToolTipText("latest mdout-format energy info");
      mdinfoTextField.setText("Remote Dir Here");
      mdoutTextField.setToolTipText("User readable state info and diagnostics ");
      mdoutTextField.setText("Remote Dir Here");
      jCheckBox2.setToolTipText("");
      jCheckBox2.setText("Copy to Local Filesystem");
      jCheckBox3.setToolTipText("");
      jCheckBox3.setText("Copy to Local Filesystem");
      jCheckBox4.setToolTipText("");
      jCheckBox4.setText("Copy to Local Filesystem");
      mdvelCheckBox.setToolTipText("");
      mdvelCheckBox.setText("Copy to Local Filesystem");
      mdcrdCheckBox.setToolTipText("");
      mdcrdCheckBox.setText("Copy to Local Filesystem");
      mdinfoCheckBox.setToolTipText("");
      mdinfoCheckBox.setText("Copy to Local Filesystem");
      mdoutCheckBox.setToolTipText("");
      mdoutCheckBox.setText("Copy to Local Filesystem");
      jPanel1.setLayout(borderLayout2);
      directoriesPanel.add(jobNameTextField,
                           new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(scriptTextField,
                           new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(localDirTextField,
                           new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(localDirButton,
                           new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(remoteDirTextField,
                           new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(jButton2,
                           new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(scriptButton,
                           new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(jobNameLabel,
                           new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(scriptLabel,
                           new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(localDirLabel,
                           new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      directoriesPanel.add(remoteDirLabel,
                           new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(mdinTextField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(mdinCheckBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(prmtopTextField,
                     new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0
                                            , GridBagConstraints.WEST,
                                            GridBagConstraints.HORIZONTAL,
                                            new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(prmtopCheckBox,
                     new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(prmtopButton, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inpcrdTextField,
                     new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0
                                            , GridBagConstraints.WEST,
                                            GridBagConstraints.HORIZONTAL,
                                            new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inpcrdCheckBox,
                     new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inpcrdButton, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(refcTextField, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(refcCheckBox, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inptrajTextField,
                     new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0
                                            , GridBagConstraints.WEST,
                                            GridBagConstraints.HORIZONTAL,
                                            new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inptrajCheckBox,
                     new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inptrajButton, new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inpdipTextField,
                     new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0
                                            , GridBagConstraints.WEST,
                                            GridBagConstraints.HORIZONTAL,
                                            new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inpdipCheckBox,
                     new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.CENTER,
                                            GridBagConstraints.NONE,
                                            new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inpdipButton, new GridBagConstraints(4, 5, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(cpinTextField, new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(cpinCheckBox, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(cpinButton, new GridBagConstraints(4, 6, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(mdinLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(prmtopLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inpcrdLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(refcLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inptrajLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(inpdipLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(cpinLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(mdinButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(refcButton, new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jPanel1.add(directoriesPanel, BorderLayout.NORTH);
      outputPanel.add(mdoutLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdoutTextField,
                      new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdoutCheckBox,
                      new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdinfoLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdinfoTextField,
                      new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.HORIZONTAL,
                                             new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdinfoCheckBox,
                      new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST,
                                             GridBagConstraints.NONE,
                                             new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdcrdLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdcrdTextField,
                      new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
                                             , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                             new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdcrdCheckBox,
                      new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST, GridBagConstraints.NONE,
                                             new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdvelLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdvelTextField,
                      new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
                                             , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                             new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(mdvelCheckBox,
                      new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST, GridBagConstraints.NONE,
                                             new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jLabel4, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jTextField5, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jCheckBox4, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jLabel3, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jTextField1, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jCheckBox3, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jLabel2, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
          , GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jTextField3, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jCheckBox2, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      this.add(jTabbedPane1, BorderLayout.CENTER);
      this.add(jPanel1, BorderLayout.NORTH);
      jTabbedPane1.add(inputPanel, "Input Files");
      jTabbedPane1.add(outputPanel, "Output Files");
      inputPanel.add(evbinButton, new GridBagConstraints(4, 7, 1, 1, 0.0, 1.0
          , GridBagConstraints.NORTH, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(evbinCheckBox, new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0
          , GridBagConstraints.NORTH, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(evbinTextField,
                     new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0
                                            , GridBagConstraints.NORTHWEST,
                                            GridBagConstraints.HORIZONTAL,
                                            new Insets(2, 2, 2, 2), 0, 0));
      inputPanel.add(evbinLabel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
          , GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jCheckBox1, new GridBagConstraints(2, 7, 1, 1, 0.0, 1.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jTextField2, new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0
          , GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
          new Insets(2, 2, 2, 2), 0, 0));
      outputPanel.add(jLabel1, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
          , GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
          new Insets(2, 2, 2, 2), 0, 0));
      jTabbedPane1.setSelectedComponent(inputPanel);
   }
}
