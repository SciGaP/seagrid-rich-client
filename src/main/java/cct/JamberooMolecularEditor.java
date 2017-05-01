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
package cct;

import cct.dialogs.JamberooFrame;
import cct.help.JavaHelp;
import cct.tools.SwingLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 *
 * <p>
 * Title: Computational Chemsitry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class JamberooMolecularEditor implements ActionListener {

  static String JAMBEROO_ICON = "/cct/images/icons16x16/cube_molecule.png";
  static String JAMBEROO_NEW_EDITOR_ACTION_COMMAND = "New Editor";
  private static String Version = "07";
  private static String Build = "0703";
  private boolean packFrame = false;
  private ImageIcon icon = null;
  private static List<JamberooFrame> windows = null;
  static final Logger logger = Logger.getLogger(JamberooMolecularEditor.class.getCanonicalName());
  private int Number_of_open_windows = 0;

  /**
   * Construct and show the application.
   */
  public JamberooMolecularEditor(String[] args) {
    Logger.getLogger("").setLevel(Level.WARNING);

    try {
      String javaVersion = System.getProperty("java.version");
      if (javaVersion != null) {
        String version[] = javaVersion.split("\\.");
        if (version.length > 1 && Integer.parseInt(version[1]) < 5) {
          JOptionPane.showMessageDialog(null, "Java Virtual Machine version should be 1.5 and later\nYour JVM Version is "
              + javaVersion, "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    } catch (Exception ex) {
      logger.warning("Cannot query Java VM version: " + ex.getMessage());
    }

    try {
      icon = new ImageIcon(cct.resources.Resources.class.getResource(JAMBEROO_ICON));
    } catch (Exception ex) {
      logger.severe("Cannot get Jamberoo icon: " + ex.getMessage() + " Ignored... Continued...");
    }

    try {
      //logger.info("JMolEditor: checking for java3d...");
      Class.forName("org.scijava.java3d.VirtualUniverse");
      //logger.info("JMolEditor: java3d ok...");
    } catch (Exception ex) {
      ex.printStackTrace();
        String variable = System.getenv("CLASSPATH");
        logger.info("CLASSPATH=" + variable);
        System.out.println("CLASSPATH=" + variable);
        logger.severe("Cannot load " + ex.getMessage() + " Either java3d is not installed or it is not in the CLASSPATH");

      JOptionPane.showMessageDialog(null, "Cannot load " + ex.getMessage() + "\nEither java3d is not installed or it is not in the CLASSPATH", "Error", JOptionPane.ERROR_MESSAGE);
//      System.exit(1);
    }

    JFrame.setDefaultLookAndFeelDecorated(false);
    Class frame_class = null;
    try {
      frame_class = this.getClass().getClassLoader().loadClass("cct.dialogs.JEditorFrame");
    } catch (Exception ex) {
      logger.severe(ex.getMessage());
//      System.exit(1);
    }

    //String look = SwingLookAndFeel.retrieveLookAndFeelPrefs(cct.dialogs.JamberooFrame.class);
    String look = SwingLookAndFeel.retrieveLookAndFeelPrefs(frame_class);
    try {
      if (look != null) {
        SwingLookAndFeel.setLookAndFeel(look);
      } else {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    // --- Create Jamberoo Window
    JamberooFrame frame = createJamberooInstance("Jamberoo - Main Window", args);
    ++Number_of_open_windows;
    windows.add(frame);

    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addNewEditorMenuItem(frame);
    // Center the window
    centerOnScreen(frame);
    frame.setVisible(true);

  }

  protected void addNewEditorMenuItem(JamberooFrame frame) {
    try {
      JMenu fileMenu = frame.getMenu("File");
      JMenuItem newEditorMenuItem = new JMenuItem("New Editor Window");
      newEditorMenuItem.setActionCommand(JAMBEROO_NEW_EDITOR_ACTION_COMMAND);
      newEditorMenuItem.setIcon(icon);
      newEditorMenuItem.addActionListener(this);
      fileMenu.add(newEditorMenuItem, 0);
      fileMenu.addSeparator();
    } catch (Exception ex) {
      logger.severe("Cannot get File Menu: " + ex.getMessage() + " Ignored... Continued...");
    }
  }

  public void centerOnScreen(JamberooFrame frame) {
    try {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
      }
      frame.setLocation((screenSize.width - frameSize.width) / 2,
          (screenSize.height - frameSize.height) / 2);
    } catch (Exception ex) {
      logger.severe("Cannot center frame on a window: " + ex.getMessage() + " Ignored & continued...");
    }
  }

  public JamberooFrame createJamberooInstance(String title, String[] args) {
    JamberooFrame frame = new JamberooFrame(title, args);
    frame.setIconImage(icon.getImage());
    //frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.setSize(new Dimension(800, 600));

    String helpHS = "JMolEditorHelpSet.hs";
    JavaHelp javahelp = null;
    try {
      javahelp = new JavaHelp(helpHS);
      frame.setHelper(javahelp);
    } catch (Exception ex) {
      logger.warning("Activating Help System: " + ex.getMessage());
    }

    // Validate frames that have preset sizes
    // Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    } else {
      frame.validate();
    }

    frame.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent we) {
        Window window = we.getWindow();
        windows.remove(window);
        --Number_of_open_windows;
        window.dispose();
        if(windows.size() == 0){
            JamberooFrame frame = createJamberooInstance("Jamberoo - Main Window", args);
            windows.add(frame);
            ++Number_of_open_windows;

            frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addNewEditorMenuItem(frame);
            // Center the window
            centerOnScreen(frame);
            frame.setVisible(false);
        }

      }
    });

    return frame;
  }

  /**
   * Application entry point.
   *
   * @param args String[]
   */
  public static void main(String[] args) {
      windows = new ArrayList<>();
    processArguments(args);
    Locale.setDefault(Locale.ENGLISH);
    final String[] Args = args;
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {

        new JamberooMolecularEditor(Args);

      }
    });
  }

  static void processArguments(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-v")) {
        logger.info(Version + "." + Build);
//        System.exit(0);
      } else if (args[i].equals("-V")) {
        logger.info("Version " + Version + " build " + Build);
//        System.exit(0);
      }
    }
  }

  public List<JamberooFrame> getJamberooFrames() {
    return windows;
  }

  public void actionPerformed(ActionEvent actionEvent) {

    Object source = actionEvent.getSource();
    String arg = actionEvent.getActionCommand();

    if (arg.equals(JAMBEROO_NEW_EDITOR_ACTION_COMMAND)) {
      JamberooFrame frame = createJamberooInstance(String.valueOf(Number_of_open_windows++) + ": Jamberoo Molecular Editor", null);
      windows.add(frame);
      frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      addNewEditorMenuItem(frame);
      centerOnScreen(frame);
      frame.setVisible(true);
    }
  }

    public static void showJamberoo() {
        if(windows == null){
          windows = new ArrayList<>();
          Locale.setDefault(Locale.ENGLISH);
          new JamberooMolecularEditor(new String []{});
        }else{
            for(JamberooFrame w : windows){
                w.setVisible(true);
            }
        }

    }
}
