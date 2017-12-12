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

import cct.config.FormatObject;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalSettings {

  public enum ADD_MOLECULE_MODE {

    SET, APPEND
  }
  static final String MESSAGES_BUNDLE = "cct/MessagesBundle";
  static final boolean PublicRelease = false;
  static final boolean New_Database = true;
  public static final String CUSTOM_PROPERTIES_FILE = "cct/custom.properties";
  public static final String DIVIDER = "@";
  public static final String CWD_KEY = "cwd";
  public static final String URL_HOME_PAGE = "urlHomePage";
  public static final String HELP_EMAIL = "helpEMail";
  public static final String lastPWDKey = "lastPWD";
  static final String defaultFragmentDictionary = "defaultFragments.dic";
  static final String defaultSolventDictionary = "defaultSolvent.dic";
  public static final String CCT_PROPERTY_FILE = "cct/cct.properties";
  public static final String FORMATS_XML_FILE = "cct/formats.xml";
  public static final String CCT_CONFIG_XML_FILE = "cct/cct-config.xml";
  public static final String MOLECULE_INTERFACE_CLASS = "moleculeInterfaceClass";
  private static Properties customProperties = null;
  private static boolean customPropsInitiated = false;
  private static URL customPropsURL = null;
  private static Properties cctProperties = null;
  private static boolean cctPropsInitiated = false;
  private static URL cctPropsURL = null;
  private static Map<String, Object> sessionProps = new HashMap<String, Object>();
  private static Document cctConfig;
  private static Map<String, FormatObject> formats;
  private static Map<String, List<FormatObject>> extensionToParser;
  private static Map<String, Component> registeredDialogs = new HashMap<String, Component>();
  // --- Icon images
  public static final ImageIcon ICON_16x16_OPEN_FILE = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/openFile.png"));
  public static final ImageIcon ICON_16x16_GAUSSIAN = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/gaussian-16x16.png"));
  public static final ImageIcon ICON_16x16_MOPAC = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/mopac-16x16.png"));
  public static final ImageIcon ICON_16x16_GAMESS = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/banned-by-gaussian-16x16.png"));
  public static final ImageIcon ICON_16x16_ADF = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/adf-16x16.png"));
  public static final ImageIcon ICON_16x16_SIESTA = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/siesta-16x16.png"));
  public static final ImageIcon ICON_16x16_MDL = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/MDL-16x16.png"));
  public static final ImageIcon ICON_16x16_GROMACS = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/gromacs-16x16.png"));
  public static final ImageIcon ICON_16x16_VASP = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/vasp-16x16.png"));
  public static final ImageIcon ICON_16x16_QCHEM = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/qchem-16x16.png"));
  public static final ImageIcon ICON_16x16_GULP = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/gulp-16x16.png"));
  public static final ImageIcon ICON_16x16_MOLECULE = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/mol-3.png"));
  public static final ImageIcon ICON_16x16_LINE_CHART = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/line-chart.png"));
  public static final ImageIcon ICON_16x16_ADD_COMPONENT = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/component_add.png"));
  public static final ImageIcon ICON_16x16_POVRAY = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/povray-file.png"));
  public static final ImageIcon ICON_16x16_REMOTE_DOWNLOAD = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/307-web_download.png"));
  public static final ImageIcon ICON_16x16_IMAGE_FILE = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/image-file.png"));
  public static final ImageIcon ICON_16x16_GEAR = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/gear.png"));
  public static final ImageIcon ICON_16x16_SERVER_INTO = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/server_into.png"));
  public static final ImageIcon ICON_16x16_DOCUMENT_WARNING = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/document_warning.png"));
  public static final ImageIcon ICON_16x16_ERROR = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/error.png"));
  public static final ImageIcon ICON_16x16_DOCUMENT_EDIT = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/document_edit.png"));
  public static final ImageIcon ICON_16x16_DOCUMENT_INTO = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/document_into.png"));
  public static final ImageIcon ICON_16x16_DOCUMENT_OUT = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/document_out.png"));
  public static final ImageIcon ICON_16x16_ADD_ATOM = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/add-atom.gif"));
  public static final ImageIcon ICON_16x16_ADD_FRAGMENT = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/add-fragment.png"));
  public static final ImageIcon ICON_16x16_WRENCH = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/wrench.png"));
  public static final ImageIcon ICON_16x16_ATOM = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/atom.png"));
  public static final ImageIcon ICON_16x16_BOND = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/bond-16x16.gif"));
  public static final ImageIcon ICON_16x16_ANGLE = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/angle-16x16.gif"));
  public static final ImageIcon ICON_16x16_HUMMER = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/hummer-16x16.gif"));
  public static final ImageIcon ICON_16x16_CUT = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/cut-16x16.gif"));
  public static final ImageIcon ICON_16x16_CENTROID = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/centroid.png"));
  public static final ImageIcon ICON_16x16_ADD_ENTRY = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/addEntry.png"));
  public static final ImageIcon ICON_16x16_REMOVE_ENTRY = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/removeEntry.png"));
  public static final ImageIcon ICON_16x16_DIHEDRAL = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/dihedral.png"));
  public static final ImageIcon ICON_16x16_JAVA_FILE = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/java-file.png"));
  public static final ImageIcon ICON_16x16_SERVER_CLIENT = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/server_client.png"));
  public static final ImageIcon ICON_16x16_DOCUMENT_GEAR = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/document_gear.png"));
  public static final ImageIcon ICON_16x16_DOCUMENT_INFO = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/document_info.png"));
  public static final ImageIcon ICON_16x16_MEMORY_MONITOR = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/memory-monitor-16x16.png"));
  public static final ImageIcon ICON_16x16_RECYCLE = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons16x16/recycle.png"));
  public static final ImageIcon ICON_32x32_SERVER_FROM_CLIENT = new ImageIcon(cct.resources.Resources.class.getResource("/cct/images/icons32x32/server_from_client.png"));
  //
  static final Logger logger = Logger.getLogger(GlobalSettings.class.getCanonicalName());
  static private GlobalSettings mock = new GlobalSettings();

  private static Locale currentLocale;
  private static ResourceBundle messages;

  private GlobalSettings() {
    getProperties();
    getCustomProperties();
    loadConfig();
    try {
      //currentLocale = new Locale("ru", "RU");
      currentLocale = Locale.getDefault();
      if (logger.isLoggable(Level.INFO)) {
        logger.log(Level.INFO, "Current Locale: " + currentLocale.toString());
      }
      messages = ResourceBundle.getBundle(MESSAGES_BUNDLE, currentLocale);
    } catch (Exception ex) {
      logger.warning("Cannot get resource : " + MESSAGES_BUNDLE + " : " + ex.getMessage());
    }
  }

  static void loadConfig() {
    loadConfig(CCT_CONFIG_XML_FILE);
  }

  public static ResourceBundle getResourceBundle() {
    return messages;
  }

  public static Map<String, FormatObject> getParsers() {
    Map<String, FormatObject> parsers = new HashMap<String, FormatObject>();
    if (formats == null || formats.size() < 1) {
      return parsers;
    }
    for (Map.Entry<String, FormatObject> entry : formats.entrySet()) {
      String key = entry.getKey();
      FormatObject fo = entry.getValue();
      if (fo.getParser() != null) {
        parsers.put(key, fo);
      }
    }
    return parsers;
  }

  static void loadConfig(String fileName) {
    InputStream is = null;
    try {
      ClassLoader cl = cct.modelling.FormatManager.class.getClassLoader();
      //logger.info("Class loader: " + cl.toString());
      //builderURL = cl.getResource(file_name);
      is = cl.getResourceAsStream(fileName);
    } catch (Exception ex) {
      logger.warning("Cannot get resource : " + fileName + " : " + ex.getMessage());
      return;
    }

    // ---  Using DOM
    try {
      // Create a factory
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // Use the factory to create a builder
      DocumentBuilder builder = factory.newDocumentBuilder();
      cctConfig = builder.parse(is);
    } catch (Exception ex) {
      logger.warning("Error parsing file " + fileName + " : " + ex.getMessage());
    }

    // --- Get formats
    try {
      formats = FormatObject.getFormatObjects(cctConfig);
      setupFormatsAndParsers(formats);
    } catch (Exception ex) {
      logger.severe("Cannot get formats: " + ex.getMessage());
    }
  }

  public static List<FormatObject> getParsersForFile(File file) {
    return getParsersForFile(file.getName());
  }

  public static List<FormatObject> getParsersForFile(String fileName) {
    int ind = fileName.lastIndexOf(".");
    if (ind == -1) {
      logger.warning("Input file has no extension");
      return null;
    }
    String extension = fileName.substring(ind + 1);
    return extensionToParser.get(extension);
  }

  static private void setupFormatsAndParsers(Map<String, FormatObject> formats) {
    if (extensionToParser == null) {
      extensionToParser = new HashMap<String, List<FormatObject>>();
    } else {
      extensionToParser.clear();
    }

    for (String key : formats.keySet()) {
      FormatObject fo = formats.get(key);
      String ext = fo.getExtensions();
      if (ext == null) {
        System.err.println("Format " + key + " has no extension(s) information");
        continue;
      }
      String[] tokens = ext.split(";");
      for (String e : tokens) {
        if (!extensionToParser.containsKey(e)) {
          extensionToParser.put(e, new ArrayList<FormatObject>());
        }

        List pl = extensionToParser.get(e);
        pl.add(fo);
      }
    }
  }

  static private Document getConfig() {
    return cctConfig;
  }

  static public boolean isPublicRelease() {
    return PublicRelease;
  }

  static public boolean isNewDatabase() {
    return New_Database;
  }

  static public String getDefaultFragmentDictionary() {
    return defaultFragmentDictionary;
  }

  static public String getDefaultSolventDictionary() {
    return defaultSolventDictionary;
  }

  public static Properties getCustomProperties(String propertiesFile) {

    String file = propertiesFile;
    if (file == null || file.trim().length() < 1) {
      file = CUSTOM_PROPERTIES_FILE;
    }
    customProperties = null;
    customPropsURL = null;
    try {
      customPropsURL = GlobalSettings.class.getClassLoader().getResource(GlobalSettings.CUSTOM_PROPERTIES_FILE);
      customProperties = new Properties();
      customProperties.load(customPropsURL.openStream());
      customPropsInitiated = true;
      logger.info("Loaded custom properties file " + file);
    } catch (Exception ex) {
      logger.warning("Cannot open custom properties file " + file + ": " + ex.getMessage());
    }
    return customProperties;
  }

  public static Properties getCustomProperties() {
    if (customPropsInitiated) {
      return customProperties;
    }

    customProperties = getCustomProperties(CUSTOM_PROPERTIES_FILE);
    customPropsInitiated = true;
    return customProperties;
  }

  public static String getCustomPropertiesURL() {
    if (customPropsURL == null) {
      return "";
    }
    return customPropsURL.getPath();
  }

  public static Properties getProperties() {
    if (cctPropsInitiated) {
      return cctProperties;
    }

    cctProperties = getProperties(CCT_PROPERTY_FILE);
    cctPropsInitiated = true;
    return cctProperties;
  }

  public static String getProperty(String key) {
    if (cctPropsInitiated) {
      return cctProperties.getProperty(key);
    }

    cctProperties = getProperties(CCT_PROPERTY_FILE);
    cctPropsInitiated = true;
    return cctProperties.getProperty(key);
  }

  public static String getProperty(String key, String defaultValue) {
    String prop = cctProperties.getProperty(key);
    if (prop == null) {
      return defaultValue;
    }
    return prop;
  }

  public static Properties getProperties(String propertiesFile) {

    String file = propertiesFile;
    if (file == null || file.trim().length() < 1) {
      file = CCT_PROPERTY_FILE;
    }
    cctProperties = null;
    cctPropsURL = null;
    try {
      cctPropsURL = GlobalSettings.class.getClassLoader().getResource(GlobalSettings.CCT_PROPERTY_FILE);
      cctProperties = new Properties();
      cctProperties.load(cctPropsURL.openStream());
      cctPropsInitiated = true;
      logger.info("Loaded cct properties file " + file);
    } catch (Exception ex) {
      logger.warning("Cannot open cct properties file " + file + ": " + ex.getMessage());
    }
    return cctProperties;
  }

  public static String getPropertiesURL() {
    if (cctPropsURL == null) {
      return "";
    }
    return cctPropsURL.getPath();
  }

  public static void setSessionProperty(String key, Object value) {
    sessionProps.put(key, value);
  }

  public static Object getSessionProperty(String key) {
    return sessionProps.get(key);
  }

  public static File getCurrentWorkingDirectory() {
    Object obj = sessionProps.get(CWD_KEY);
    if (obj == null) {
      return null;
    }
    if (obj instanceof File) {
      return (File) obj;
    }
    logger.warning("Current Working Directory should be a File, got: " + obj.getClass().getCanonicalName());
    return null;
  }

  public static void setCurrentWorkingDirectory(File pwd) {
    sessionProps.put(CWD_KEY, pwd);
  }

  public static void showInDefaultBrowser(String url) throws Exception {
    Desktop desktop = null;
    if (Desktop.isDesktopSupported()) {
      desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.BROWSE)) {
        URI uri = null;
        try {
          uri = new URI(url);
        } catch (Exception ex) {
          throw new Exception("Wrong Uniform Resource Identifier (URI) reference: " + url + " : " + ex.getMessage());
        }
        try {
          desktop.browse(uri);
        } catch (Exception ex) {
          throw new Exception("The default browser cannot display " + url + " : " + ex.getMessage());
        }
      } else {
        throw new Exception("Desktop does not support the BROWSE action");
      }
    } else {
      throw new Exception("The Native Desktop is not supported");
    }
  }

  public static void mailUsingDefaultClient(String address, String subject, String body) throws Exception {
    Desktop desktop = null;
    if (Desktop.isDesktopSupported()) {
      desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.MAIL)) {
        StringBuilder sb = new StringBuilder("mailto:");
        if (address != null && address.length() > 0) {
          sb.append(address);
        }

        if (subject != null) {
          sb.append("?subject=" + subject.replaceAll("[&]", "&amp"));
        }

        if (body != null && body.length() > 1) {
          if (subject == null) {
            sb.append("?");
          } else {
            sb.append("&");
          }
          sb.append("body=" + body.replaceAll("[&]", "&amp"));
        }

        String uriString = sb.toString().replaceAll("[ ]", "%20").replaceAll("[\n]", "%0D%0A");
        System.out.println(uriString);
        URI uri = null;
        try {
          uri = new URI(uriString);
        } catch (Exception ex) {
          throw new Exception("Wrong Uniform Resource Identifier (URI) reference: " + uriString + " : " + ex.getMessage());
        }
        try {
          desktop.mail(uri);
        } catch (Exception ex) {
          throw new Exception("The default mail client cannot parse " + uriString + " : " + ex.getMessage());
        }
      } else {
        throw new Exception("Desktop does not support the MAIL action");
      }
    } else {
      throw new Exception("The Native Desktop is not supported");
    }
  }

  public static void registerDialog(String label, Component dialog) {
    if (registeredDialogs.get(label) != null) {
      logger.warning("Dialog with label " + label + " is already registered. Replacing...");
    }
    registeredDialogs.put(label, dialog);
    logger.info("Registered dialog: " + label);
  }

  public static Component getRegisterDialog(String label) {
    return registeredDialogs.get(label);
  }
}
