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

import javax.swing.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
public class FileUtilities {

  static String defaultProperiesFile_16x16 = "cct.tools.FileTypeIcons16x16";
  static String defaultProperiesFile_32x32 = "cct.tools.FileTypeIcons32x32";
  private static ResourceBundle resources;
  private static String icons_16x16_base = "/cct/images/icons16x16";
  private static String icons_32x32_base = "/cct/images/icons32x32";
  private static boolean isInitialized_16x16 = false;
  private static boolean isInitialized_32x32 = false;

  static String icons_16x16_postfix = "_16x16";
  static String icons_32x32_postfix = "_32x32";

  final static ImageIcon folder = new ImageIcon(cct.resources.Resources.class.
                                                getResource(
                                                        "/cct/images/icons16x16/folder.png"));

  final static ImageIcon folder_32x32 = new ImageIcon(cct.resources.Resources.class.
      getResource(
              "/cct/images/icons32x32/folder_closed.png"));

  final static ImageIcon multipleItems_32x32 = new ImageIcon(cct.resources.
      Resources.class.
      getResource(
              "/cct/images/icons32x32/folder_window.png"));

  final static ImageIcon document = new ImageIcon(cct.resources.Resources.class.
                                                  getResource(
                                                          "/cct/images/icons16x16/document.png"));

  final static ImageIcon document_32x32 = new ImageIcon(cct.resources.
      Resources.class.
      getResource(
              "/cct/images/icons32x32/document_plain.png"));

  final static ImageIcon gaussianImage = new ImageIcon(cct.resources.Resources.class.
      getResource(
              "/cct/images/icons16x16/gaussian-16x16.png"));

  final static ImageIcon triposImage = new ImageIcon(cct.resources.Resources.class.
      getResource("/cct/images/icons16x16/tripos-transp-16x16.png"));

  static ImageIcon mdlImage = new ImageIcon(cct.resources.Resources.class.
                                            getResource(
                                                    "/cct/images/icons16x16/MDL-16x16.png"));

  static ImageIcon imageFileImage = new ImageIcon(cct.resources.Resources.class.
                                                  getResource(
                                                          "/cct/images/icons16x16/image-file.png"));

  static ImageIcon javaFileImage = new ImageIcon(cct.resources.Resources.class.
                                                 getResource(
                                                         "/cct/images/icons16x16/java-file.png"));

  static ImageIcon htmlFileImage = new ImageIcon(cct.resources.Resources.class.
                                                 getResource(
                                                         "/cct/images/icons16x16/html-file.png"));

  static ImageIcon audioFileImage = new ImageIcon(cct.resources.Resources.class.
                                                  getResource(
                                                          "/cct/images/icons16x16/audio-file.png"));

  //static HashMap icons16x16 = new HashMap<String, ImageIcon> ();
  static Map icons16x16 = new HashMap();
  //static HashMap extensions16x16 = new HashMap<String, ImageIcon> ();
  static Map extensions16x16 = new HashMap();

  //static HashMap icons32x32 = new HashMap<String, ImageIcon> ();
  static Map icons32x32 = new HashMap();
  //static HashMap extensions32x32 = new HashMap<String, ImageIcon> ();
  static Map extensions32x32 = new HashMap();

  static {
    extensions16x16.put("cct/html", htmlFileImage);
    extensions16x16.put("htm", htmlFileImage);

    extensions16x16.put("java", javaFileImage);
    extensions16x16.put("class", javaFileImage);

    extensions16x16.put("gjf", gaussianImage);
    extensions16x16.put("g03", gaussianImage);
    extensions16x16.put("mol2", triposImage);
    extensions16x16.put("mol", mdlImage);

    extensions16x16.put("jpe", imageFileImage);
    extensions16x16.put("jpg", imageFileImage);
    extensions16x16.put("jpeg", imageFileImage);
    extensions16x16.put("gif", imageFileImage);
    extensions16x16.put("png", imageFileImage);
    extensions16x16.put("tif", imageFileImage);
    extensions16x16.put("tiff", imageFileImage);
  }

  private FileUtilities() {
  }

  /**
   * Returns image icon
   * @param filename String
   * @return ImageIcon
   */
  static public ImageIcon getIcon16x16(String filename) {
    if (!isInitialized_16x16) {
      initializeIcons_16x16(defaultProperiesFile_16x16);
    }

    String ext = getFileExtension(filename).toLowerCase();
    if (ext.length() < 1) {
      return document;
    }

    if (extensions16x16.containsKey(ext)) {
      return (ImageIcon) extensions16x16.get(ext);
    }

    return document;
  }

  /**
   * Returns image icon
   * @param filename String
   * @return ImageIcon
   */
  static public ImageIcon getIcon32x32(String filename) {
    if (!isInitialized_32x32) {
      initializeIcons_32x32(defaultProperiesFile_32x32);
    }

    String ext = getFileExtension(filename).toLowerCase();
    if (ext.length() < 1) {
      return document_32x32;
    }

    if (extensions32x32.containsKey(ext)) {
      return (ImageIcon) extensions32x32.get(ext);
    }

    return document_32x32;
  }

  static public ImageIcon getFolderIcon32x32() {
    return folder_32x32;
  }

  static public ImageIcon getMultipleItemsIcon32x32() {
    return multipleItems_32x32;
  }

  /**
   * Returns file extension
   * @param filename String - [path]/file name
   * @return String File extension
   */
  static public String getFileExtension(String filename) {

    int n = filename.lastIndexOf(".");
    if (n == -1) {
      return "";
    }

    int m = filename.lastIndexOf("/");
    if (m != -1 && m > n) {
      return "";
    }
    if (filename.endsWith(".")) {
      return "";
    }
    return filename.substring(filename.lastIndexOf(".") + 1, filename.length());
  }

  /**
   * Initializes 16x16 icons
   * @param propertiesName String
   */
  static public void initializeIcons_16x16(String propertiesName) {

    try {
      resources = ResourceBundle.getBundle(propertiesName);
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
      if (resources == null) {
        System.err.println("Resources " + propertiesName +
                           " are not found");
        return;
      }
    }

    // --- First get 16x16 Icons
    //for (Enumeration<String> keys = resources.getKeys(); keys.hasMoreElements(); ) {
    for (Enumeration keys = resources.getKeys(); keys.hasMoreElements(); ) {
      String key = (String) keys.nextElement();
      if (!key.endsWith(icons_16x16_postfix)) {
        continue;
      }
      String iconName = resources.getString(key);
      //String iconName = key.substring(0, key.lastIndexOf(icons_16x16_postfix));
      URL resource = cct.resources.Resources.class.
          getResource(icons_16x16_base + "/" + iconName);

      if (resource == null) {
        System.err.println("Cannot find icon: " + icons_16x16_base + "/" +
                           iconName);
        continue;
      }

      ImageIcon icon = new ImageIcon(resource);

      //ImageIcon icon = new ImageIcon(cct.resources.Resources.class.
      //                               getResource(icons_16x16_base +
      //    File.separator + iconName));
      //logger.info("Adding icon: " + icons_16x16_base + "/" +
      //                   iconName);
      icons16x16.put(key, icon);
    }

    // -- Now go through file extensions

    //for (Enumeration<String> keys = resources.getKeys(); keys.hasMoreElements(); ) {
    for (Enumeration keys = resources.getKeys(); keys.hasMoreElements(); ) {
      String key = (String) keys.nextElement();
      if (key.endsWith(icons_16x16_postfix)) { // It's an icon decription
        continue;
      }
      String icon = resources.getString(key);

      if (!icons16x16.containsKey(icon)) {
        System.err.println("Error: No icon " + icon + " for extension: " +
                           key);
        continue;
      }

      ImageIcon imageIcon = (ImageIcon) icons16x16.get(icon);
      extensions16x16.put(key, imageIcon);

    }

    isInitialized_16x16 = true;
  }

  /**
   * Initializes 32x32 icons
   * @param propertiesName String
   */
  static public void initializeIcons_32x32(String propertiesName) {

    try {
      resources = ResourceBundle.getBundle(propertiesName);
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
      if (resources == null) {
        System.err.println("Resources " + propertiesName +
                           " are not found");
        return;
      }
    }

    // --- First get 32x32 Icons
    //for (Enumeration<String> keys = resources.getKeys(); keys.hasMoreElements(); ) {
    for (Enumeration keys = resources.getKeys(); keys.hasMoreElements(); ) {
      String key = (String) keys.nextElement();
      if (!key.endsWith(icons_32x32_postfix)) {
        continue;
      }
      String iconName = resources.getString(key);
      //String iconName = key.substring(0, key.lastIndexOf(icons_16x16_postfix));
      try {
        ImageIcon icon = new ImageIcon(cct.resources.Resources.class.
                                       getResource(icons_32x32_base +
            "/" + iconName));
        //logger.info("Adding icon: " + icons_32x32_base +
        //                   "/" +
        //                   iconName);
        icons32x32.put(key, icon);
      }
      catch (Exception ex) {
        System.err.println("Cannot add icon: " + icons_32x32_base +
                           "/" + iconName);

      }
    }

    // -- Now go through file extensions

    //for (Enumeration<String> keys = resources.getKeys(); keys.hasMoreElements(); ) {
    for (Enumeration keys = resources.getKeys(); keys.hasMoreElements(); ) {
      String key = (String) keys.nextElement();
      if (key.endsWith(icons_32x32_postfix)) { // It's an icon decription
        continue;
      }
      String icon = resources.getString(key);

      if (!icons32x32.containsKey(icon)) {
        System.err.println("Error: No icon " + icon + " for extension: " +
                           key);
        continue;
      }

      ImageIcon imageIcon = (ImageIcon) icons32x32.get(icon);
      extensions32x32.put(key, imageIcon);

    }

    isInitialized_32x32 = true;
  }

  public static String getFileName(String path) {
    String filename = null;
    if (path.contains("/")) {
      filename = path.substring(path.lastIndexOf("/") + 1);
    }
    else if (path.contains("\\")) {
      filename = path.substring(path.lastIndexOf("\\") + 1);
    }
    else {
      return path;
    }
    return filename;
  }

}
