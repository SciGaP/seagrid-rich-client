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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Logger;

import org.scijava.java3d.Canvas3D;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import cct.help.HelpPanel;
import cct.j3d.Java3dUniverse;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
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
public class Frame1
    extends JFrame {
   JPanel contentPane;
   BorderLayout borderLayout1 = new BorderLayout();
   JMenuBar jMenuBar1 = new JMenuBar();
   JMenu jMenuFile = new JMenu();
   JMenuItem jMenuFileExit = new JMenuItem();
   JMenu jMenuHelp = new JMenu();
   JMenuItem jMenuHelpAbout = new JMenuItem();
   JToolBar jToolBar = new JToolBar();
   JButton jButton1 = new JButton();
   JButton jButton2 = new JButton();
   JButton helpButton = new JButton();
   //ImageIcon image1 = new ImageIcon(cct.dialogs.Frame1.class.getResource(
   //    "images/openFile.png"));
   ImageIcon image2 = new ImageIcon(Frame1.class.getResource(
           "cct/images/closeFile.png"));
   ImageIcon image3 = new ImageIcon(Frame1.class.getResource(
           "cct/images/help.png"));
   ImageIcon addAtomImage = new ImageIcon(Frame1.class.getResource(
           "cct/images/add-atom.gif"));
   ImageIcon cutImage = new ImageIcon(Frame1.class.getResource(
           "cct/images/cut-16x16.gif"));
   ImageIcon hummerImage = new ImageIcon(Frame1.class.getResource(
           "cct/images/hummer-16x16.gif"));
   //ImageIcon openFileImage = new ImageIcon(cct.dialogs.Frame1.class.getResource(
   //    "images/openFie.png"));
   JLabel statusBar = new JLabel();
   JToolBar mainToolBar = new JToolBar();
   JToolBar editToolBar = new JToolBar();
   JButton jButton4 = new JButton();
   JButton jButton5 = new JButton();
   JButton jButton6 = new JButton();
   JMenu openAs = new JMenu();

   Canvas3D canvas3D;
   Java3dUniverse java3dUniverse;

   JMenu editMenu = new JMenu();
   JMenu viewMenu = new JMenu();
   JMenu databaseMenu = new JMenu();
   JMenu saveAsMenu = new JMenu();
   JMenuItem gjfMenuItem = new JMenuItem();
   HelpPanel helpPanel1 = new HelpPanel();
   JMenuItem gaussOutMenuItem = new JMenuItem();
   JMenuItem openPDBMenuItem = new JMenuItem();
   JMenuItem openMol2MenuItem = new JMenuItem();
   JMenuItem saveGJFMenuItem = new JMenuItem();
   JMenuItem savePDBMenuItem = new JMenuItem();
   JMenuItem saveMol2MenuItem = new JMenuItem();
   static final Logger logger = Logger.getLogger(Frame1.class.getCanonicalName());

   public Frame1() {
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
      java3dUniverse = new Java3dUniverse();
      //setLayout(new BorderLayout());
      canvas3D = java3dUniverse.getCanvas3D();

      contentPane = (JPanel) getContentPane();
      contentPane.setLayout(borderLayout1);
      setSize(new Dimension(400, 300));
      setTitle("Frame Title");
      statusBar.setText(" ");
      jMenuFile.setText("File");
      jMenuFileExit.setText("Exit");
      jMenuFileExit.addActionListener(new Frame1_jMenuFileExit_ActionAdapter(this));
      jMenuHelp.setText("Help");
      jMenuHelpAbout.setText("About");
      jMenuHelpAbout.addActionListener(new Frame1_jMenuHelpAbout_ActionAdapter(this));
      jButton4.setIcon(cutImage);
      jButton5.setIcon(addAtomImage);
      jButton6.setToolTipText("");
      jButton6.setIcon(hummerImage);
      openAs.setText("Open As");
      editMenu.setText("Edit");
      viewMenu.setText("View");
      databaseMenu.setText("Database");
      saveAsMenu.setText("Save As");
      gjfMenuItem.setText("Gaussian G03 GJF");
      helpButton.addActionListener(new Frame1_helpButton_actionAdapter(this));
      gaussOutMenuItem.setToolTipText("");
      gaussOutMenuItem.setActionCommand("Gaussian G03 Output");
      gaussOutMenuItem.setText("Gaussian G03 Output");
      openPDBMenuItem.setToolTipText("");
      openPDBMenuItem.setText("PDB");
      openMol2MenuItem.setToolTipText("");
      openMol2MenuItem.setText("Tripos Mol2");
      saveGJFMenuItem.setText("Gaussian G03 GJF");
      savePDBMenuItem.setToolTipText("");
      savePDBMenuItem.setText("PDB");
      saveMol2MenuItem.setToolTipText("");
      saveMol2MenuItem.setText("Tripos Mol2");
      jMenuBar1.add(jMenuFile);
      jMenuBar1.add(editMenu);
      jMenuBar1.add(viewMenu);
      jMenuBar1.add(databaseMenu);
      jMenuFile.add(openAs);
      jMenuFile.add(saveAsMenu);

      jMenuFile.addSeparator();
      jMenuFile.add(jMenuFileExit);
      jMenuBar1.add(jMenuHelp);
      jMenuHelp.add(jMenuHelpAbout);
      setJMenuBar(jMenuBar1);
      jButton1.setIcon(null);
      jButton1.setToolTipText("Open File");
      jButton2.setIcon(image2);
      jButton2.setToolTipText("Close File");
      helpButton.setIcon(image3);
      helpButton.setToolTipText("Help");
      jToolBar.add(jButton1);
      jToolBar.add(jButton2);
      jToolBar.add(helpButton);
      contentPane.add(statusBar, BorderLayout.SOUTH);
      mainToolBar.add(jToolBar);
      mainToolBar.add(editToolBar);
      editToolBar.add(jButton5);
      editToolBar.add(jButton4);
      editToolBar.add(jButton6);
      openAs.add(gjfMenuItem);
      openAs.add(gaussOutMenuItem);
      openAs.add(openPDBMenuItem);
      openAs.add(openMol2MenuItem);
      contentPane.add(helpPanel1, BorderLayout.CENTER);
      contentPane.add(mainToolBar, BorderLayout.NORTH);
      saveAsMenu.add(saveGJFMenuItem);
      saveAsMenu.add(savePDBMenuItem);
      saveAsMenu.add(saveMol2MenuItem);
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
      Frame1_AboutBox dlg = new Frame1_AboutBox(this);
      Dimension dlgSize = dlg.getPreferredSize();
      Dimension frmSize = getSize();
      Point loc = getLocation();
      dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                      (frmSize.height - dlgSize.height) / 2 + loc.y);
      dlg.setModal(true);
      dlg.pack();
      dlg.setVisible(true);
   }

   /*
       public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception exception) {
               exception.printStackTrace();
            }

            new testFrame();
         }
      });
       }
    */

   public void helpButton_actionPerformed(ActionEvent e) {
      String rootDir = getRootDirectory();
      logger.info("Root directory : " + rootDir);

      String protocol = "http://"; //"file:";
      rootDir = "sf.anu.edu.au/~vvv900/cct/appl/jmoleditor/manual/";
      String path = protocol + rootDir + "cct/html/";
      path = protocol + rootDir;
      try {
         URL helpURL = new URL(path + "index.html");
         helpPanel1.setText(helpURL);
      }
      catch (java.net.MalformedURLException ex) {
         System.err.println(ex.getMessage());
      }

   }

   private String getRootDirectory() {
      //cct.BaseClass.getClass().getName()

      String fullName = this.getClass().getName();
      logger.info("full Name : " + fullName);

      //BaseClass bc = new BaseClass();
      //String fullName = bc.getClass().getName();

      int packageLen = fullName.lastIndexOf('.');
      // get the directory where the contents of this package are stored

      //String packageDir = bc.getClass().getResource(".").getFile();
      String packageDir = this.getClass().getResource(".").getFile();
      logger.info("Package directory : " + packageDir);
      String classesDir = packageDir.substring(0,
                                               packageDir.length() - packageLen -
                                               1);
      //int index = classesDir.lastIndexOf('/');
      //String rootDir = classesDir.substring(0, index + 1);
      String rootDir = classesDir;
      return rootDir;
   }

   class testFrame {
      public testFrame() {
         Frame1 frame = new Frame1();

         //if (packFrame) {
         frame.pack();
         //}
         //else {
         //   frame.validate();
         //}

         // Center the window
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         Dimension frameSize = frame.getSize();
         if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
         }
         if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
         }
         frame.setLocation( (screenSize.width - frameSize.width) / 2,
                           (screenSize.height - frameSize.height) / 2);
         frame.setVisible(true);

      }
   }

}

class Frame1_helpButton_actionAdapter
    implements ActionListener {
   private Frame1 adaptee;
   Frame1_helpButton_actionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.helpButton_actionPerformed(e);
   }
}

class Frame1_jMenuFileExit_ActionAdapter
    implements ActionListener {
   Frame1 adaptee;

   Frame1_jMenuFileExit_ActionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent actionEvent) {
      adaptee.jMenuFileExit_actionPerformed(actionEvent);
   }
}

class Frame1_jMenuHelpAbout_ActionAdapter
    implements ActionListener {
   Frame1 adaptee;

   Frame1_jMenuHelpAbout_ActionAdapter(Frame1 adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent actionEvent) {
      adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
   }
}
