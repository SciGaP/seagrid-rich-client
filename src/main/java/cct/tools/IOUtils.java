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
package cct.tools;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

public class IOUtils {

  static final Logger logger = Logger.getLogger(IOUtils.class.getCanonicalName());

  public IOUtils() {
  }

  public static String useFormatTemplate(String format, int number) {
    return String.format(format, number);
  }

  public static String loadFileIntoString(String filename) {
    String line;
    StringWriter sWriter = null;

    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      sWriter = new StringWriter();
      while ((line = in.readLine()) != null) {
        sWriter.write(line + "\n");
      }
      in.close();
    } catch (IOException e) {
      logger.info("loadFileIntoString: " + e.getMessage());
      return null;
    }
    return sWriter.toString();
  }

  public static boolean saveStringIntoFile(String Text, String fileName) throws
      Exception {
    FileOutputStream out;
    try {
      out = new FileOutputStream(fileName);
    } catch (java.io.FileNotFoundException er) {
      logger.info(er.getMessage());
      //JOptionPane.showMessageDialog(new Frame(),
      //                      er.getMessage(),"Error",
      //                      JOptionPane.ERROR_MESSAGE);
      throw new Exception(er.getMessage());
      //return false;
    } catch (SecurityException se) {
      logger.info(fileName + ": Security Eception\n");
      //JOptionPane.showMessageDialog(new Frame(),
      //                      se.getMessage(),"Error",
      //                      JOptionPane.ERROR_MESSAGE);
      throw new Exception(se.getMessage());
      //return false;
    } catch (IOException ioe) {
      logger.info(ioe.getMessage());
      //JOptionPane.showMessageDialog(new Frame(),
      //                      ioe.getMessage(),"Error",
      //                      JOptionPane.ERROR_MESSAGE);
      throw new Exception(ioe.getMessage());
      //return false;
    }

    try {
      out.write(Text.getBytes());
    } catch (IOException ioe) {
      logger.info("Error writing into " + fileName);
      //JOptionPane.showMessageDialog(new Frame(),
      //                       ioe.getMessage(),"Error",
      //                       JOptionPane.ERROR_MESSAGE);
      throw new Exception(ioe.getMessage());
      //return false;
    }

    // --- Finally, close a stream
    try {
      out.close();
    } catch (IOException ioe) {
      logger.info("Error closing " + fileName);
      //JOptionPane.showMessageDialog(new Frame(),
      //                       ioe.getMessage(),"Error",
      //                       JOptionPane.ERROR_MESSAGE);
      throw new Exception(ioe.getMessage());
      //return false;
    }

    return true;
  }

  public static URL findFragmentSet(ClassLoader cl, String fragSetName) throws
      Exception {
    URL loc = null;
    loc = cl.getResource(fragSetName);
    /*
             try {
         loc = new URL("");
             } catch ( Exception ex ) {
         throw new Exception("findFragmentSet: ERROR: "+ex.getMessage());
             }
     */
    return loc;
  }

  public static String getRootDirectory(Object obj) {
    //cct.BaseClass.getClass().getName()

    String fullName = obj.getClass().getName();
    logger.info("full Name : " + fullName);

    //BaseClass bc = new BaseClass();
    //String fullName = bc.getClass().getName();
    int packageLen = fullName.lastIndexOf('.');
    // get the directory where the contents of this package are stored

    //String packageDir = bc.getClass().getResource(".").getFile();
    String packageDir = obj.getClass().getResource(".").getFile();
    logger.info("Package directory : " + packageDir);
    String classesDir = packageDir.substring(0,
        packageDir.length() - packageLen
        - 1);
    //int index = classesDir.lastIndexOf('/');
    //String rootDir = classesDir.substring(0, index + 1);
    String rootDir = classesDir;
    return rootDir;
  }

  public static URL getURL(String protocol, Object obj, String path,
      String file) {

    String rootDir = getRootDirectory(obj);

    String fullURLPath = protocol + rootDir + path + file;

    URL url = null;
    try {
      url = new URL(fullURLPath);
      return url;
    } catch (java.net.MalformedURLException ex) {
      System.err.println("Creating URL : " + fullURLPath + " : "
          + ex.getMessage());
    }

    return url;
  }

  /**
   *
   * @param filters FileFilter[]
   * @param title String
   * @param workingDirectory String
   * @param mode int - JFileChooser.OPEN_DIALOG or JFileChooser.SAVE_DIALOG
   * @return String
   * @throws Exception
   */
  public static String chooseFileDialog(javax.swing.filechooser.FileFilter[] filters, Component parent, String title,
      String workingDirectory, int mode) throws
      Exception {

    if (mode != JFileChooser.OPEN_DIALOG && mode != JFileChooser.SAVE_DIALOG) {
      throw new Exception("Unknown mode");
    }

    JFileChooser chooser = new JFileChooser();

    chooser.setAcceptAllFileFilterUsed(false);

    chooser.setDialogTitle(title);
    chooser.setDialogType(mode);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(false);

    if (workingDirectory != null & workingDirectory.length() > 0) {
      File cwd = new File(workingDirectory);
      if (cwd.isDirectory() && cwd.exists()) {
        chooser.setCurrentDirectory(cwd);
      }
    }

    for (int i = 0; i < filters.length; i++) {
      chooser.addChoosableFileFilter(filters[i]);
      logger.info("Adding filter: " + filters[i].getDescription());
    }
    chooser.setFileFilter(filters[0]);
    logger.info("# of filters: " + chooser.getChoosableFileFilters().length);

    int answer = JFileChooser.CANCEL_OPTION;

    if (mode == JFileChooser.OPEN_DIALOG) {
      answer = chooser.showOpenDialog(parent);
    } else if (mode == JFileChooser.SAVE_DIALOG) {
      answer = chooser.showSaveDialog(parent);
    }

    if (answer == JFileChooser.APPROVE_OPTION) {
      return chooser.getSelectedFile().getAbsolutePath();
    }
    return null;
  }

  static public String readLine(Object reader) throws Exception {
    if (reader instanceof DataInput) {
      return ((DataInput) reader).readLine();
    } else if (reader instanceof BufferedReader) {
      return ((BufferedReader) reader).readLine();
    }
    throw new Exception("Object " + reader.getClass().getCanonicalName() + " does not support line reading in");
  }

  static public void close(Object reader) throws Exception {
    if (reader instanceof Closeable) {
      ((Closeable) reader).close();
    }
    System.err.println("Warning: object "+reader.getClass().getCanonicalName()+" does not implement Closable interface");
  }
}
