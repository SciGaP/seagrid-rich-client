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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import cct.grid.CheckPoint;
import cct.grid.FileViewerInterface;
import cct.grid.GridProviderInterface;
import cct.grid.ScriptSubmitterDialogInterface;
import cct.grid.ui.CheckPointStatus;
import cct.grid.ui.JobStatusDialog;
import cct.j3d.Java3dUniverse;
import cct.modelling.MolecularFileFormats;
import cct.tools.ui.JShowText;

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
public class GridProviders
    implements ActionListener, FileViewerInterface {

  public static final String SCRIPT_SUBMITTER_ARG = "ScriptSubmitter";
  public static final String PROVIDERS_PROPERTY_FILE = "cct.grid.grid-providers";
  public static final String PROVIDER_KEY = "provider";
  public static final String SUBMITTER_KEY = "submitter";
  static final Logger logger = Logger.getLogger(GridProviders.class.getCanonicalName());
  public static String JOB_STATUS_DIALOG = "cct.grid.ui.JobStatusDialog";
  private static ResourceBundle resources = null;
  static JobStatusDialog jobStatus = null;
  static Object jobStatusSource = null;
  static Java3dUniverse java3dUniverse = null;
  static Map<String, ScriptSubmitterDialogInterface> scriptSubmitters = new LinkedHashMap<String, ScriptSubmitterDialogInterface>();
  private static Map<Object, String> actionTable = new HashMap<Object, String>();
  static Map<String, GridProviderInterface> taskProviders = new LinkedHashMap<String, GridProviderInterface>();
  static private GridProviders phantom = new GridProviders();
  static java.awt.Component parentComponent = null;

  private GridProviders() {
    try {
      resources = ResourceBundle.getBundle(PROVIDERS_PROPERTY_FILE);

    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      if (resources == null) {
        System.err.println("Resources " + PROVIDERS_PROPERTY_FILE + " are not found");
        return;
      }
    }

    getProviders();
    if (taskProviders.size() < 1) {
      System.err.println("No available task providers");
      return;
    }
    getScriptSubmitters();
  }

  public static void setParentComponent(java.awt.Component component) {
    parentComponent = component;
  }

  public static void setMolecularViewer(Java3dUniverse java3d) {
    java3dUniverse = java3d;
  }

  public void getProviders() {

    for (Enumeration<String> keys = resources.getKeys(); keys.hasMoreElements();) {
      String originalOption = keys.nextElement();
      String option = originalOption.toLowerCase();
      if (option.startsWith(PROVIDER_KEY + ".")) {
        String className = "";
        try {
          className = resources.getString(originalOption);
          Class cl = this.getClass().getClassLoader().loadClass(className);
          Object obj = cl.newInstance();
          GridProviderInterface gpi = (GridProviderInterface) obj;
          logger.info("Task provider " + className + " is loaded...");
          taskProviders.put(gpi.getName(), gpi);
        } catch (Exception ex) {
          System.err.println("Error Loading Task provider " + className + ": " + ex.getMessage());
        }
      }
    }
    try {
      //cct.grid.TaskProvider.setAvailableTaskProviders(taskProviders);
      cct.grid.TaskProvider.addTaskProviders(taskProviders);
      logger.info("Available providers: " + cct.grid.TaskProvider.getAvailableTaskProviders().toString());
    } catch (Exception ex) {
    }
    //Class cl = java.lang.ClassLoader.loadClass(provider);

  }

  static public boolean isJobStatusDialogAvailable() {
    try {
      Class cl = phantom.getClass().getClassLoader().loadClass(
          JOB_STATUS_DIALOG);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      return false;
    }
    return true;
  }

  public void invokeJobStatusDialog() {
    invokeJobStatusDialog(null, "Jobs Status", false);
    jobStatus.setFileViewer(this);
  }

  static public void invokeJobStatusDialog(Frame owner, String title,
      boolean modal) {

    if (!isJobStatusDialogAvailable()) {
      logger.warning("No Job Status Dialog is available");
      return;
    }

    if (jobStatus == null) {
      CheckPointStatus chk = new CheckPointStatus();
      jobStatus = new JobStatusDialog(owner, title, modal, chk);

      jobStatus.queryJobStatus();
      jobStatus.validate();
      /*
      jobStatus.hideButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      System.exit(0);
      }
      }
      );
       */
      jobStatus.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      jobStatus.setLocationRelativeTo(owner);

    }

    jobStatus.setVisible(true);
  }

  @Override
  public void setFileInfo(CheckPoint chk, String fileName) {
    String fullPath = null;
    String type = null;
    try {
      fullPath = chk.getFullLocalPath(fileName);
      type = chk.getOutputFileType(fileName);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      JOptionPane.showMessageDialog(new Frame(),
          "Cannot download file "
          + fullPath + ": " + ex.getMessage(),
          "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    logger.info("Full path: " + fullPath + " File type" + type);

    if (java3dUniverse != null) {
      if (chk.getProgram().equalsIgnoreCase("G03")) {
        if (type.equalsIgnoreCase("gaussianOutput")) {
          java3dUniverse.openMolecularModelingFile(MolecularFileFormats.gaussian03Output, fullPath);
        } else {
          JShowText show = new JShowText(fileName);
          show.setLocationByPlatform(true);

          try {
            BufferedReader in = new BufferedReader(new FileReader(
                fullPath));
            String str;
            while ((str = in.readLine()) != null) {
              show.appendText(str + "\n");
            }
            in.close();
          } catch (IOException e) {
            JOptionPane.showMessageDialog(new Frame(),
                "Error opening file "
                + fullPath + ": " + e.getMessage(),
                "Error opening file",
                JOptionPane.ERROR_MESSAGE);
            show.dispose();
            return;

          }
          show.setSize(640, 480);
          show.setVisible(true);
        }
      }

    }
  }

  public static ScriptSubmitterDialogInterface getScriptSubmitter(String submitter) {
    Object obj = scriptSubmitters.get(SUBMITTER_KEY + "." + submitter.toLowerCase());
    if (obj == null) {
      return null;
    }
    return ((ScriptSubmitterDialogInterface) obj).newInstance();
  }

  private void getScriptSubmitters() {
    logger.info("Loading Script Submitters...");
    logger.info("Loading Script Submitters...");
    for (Enumeration<String> keys = resources.getKeys(); keys.hasMoreElements();) {
      String originalOption = keys.nextElement();
      String option = originalOption.toLowerCase();
      if (option.startsWith(SUBMITTER_KEY + ".")) {
        String className = "";
        try {
          className = resources.getString(originalOption);
          String customOptions = "";
          int index = className.indexOf(" ");
          if (index != -1) {
            customOptions = className.substring(index).trim();
            className = className.substring(0, index);
            logger.info("Submitter Class: " + className + " Custom options: " + customOptions);
            logger.info("Submitter Class: " + className + " Custom options: " + customOptions);
          }
          Class cl = this.getClass().getClassLoader().loadClass(className);
          Object obj = cl.newInstance();
          ScriptSubmitterDialogInterface ssd = (ScriptSubmitterDialogInterface) obj;
          if (customOptions.length() > 0) {
            ssd.setCustomOptions(customOptions);
          }
          ssd.setTaskProviders(cct.grid.TaskProvider.getAvailableTaskProviders());
          //ssd.setTaskProviders(taskProviders); cct.grid.TaskProvider.getAvailableTaskProviders()
          JDialog dialog = ssd.getDialog();
          dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
          dialog.validate();
          dialog.pack();
          //if (parentComponent != null) {
          dialog.setLocationRelativeTo(parentComponent);
          //}
          logger.info("Script submitter " + className + " is loaded...");
          scriptSubmitters.put(option, ssd);
        } catch (Exception ex) {
          System.err.println("Script submitter " + className + ": " + ex.getMessage());
        }
      }
    }
  }

  public static List<String> getAvailableScriptSubmitters() {
    if (scriptSubmitters.size() < 1) {
      return null;
    }
    List<String> submitters = new ArrayList<String>();
    Set k = scriptSubmitters.keySet();
    for (Iterator keys = k.iterator(); keys.hasNext();) {
      String option = (String) keys.next();
      submitters.add(option.substring(option.indexOf(".") + 1));
    }
    return submitters;
  }

  /*
  protected synchronized static void loadProviderProperties() {
  try {
  providerProperties = new Hashtable();
  aliases = new Hashtable();
  Enumeration e = GridProviders.class.getClassLoader().getResources(
  PROVIDERS_PROPERTY_FILE);
  while (e.hasMoreElements()) {
  try {
  loadProviderProperties(((URL) e.nextElement()).openStream());
  }
  catch (Exception ee) {
  logger.debug("Error reading from provider properties", ee);
  }
  }
  }
  catch (Exception e) {
  logger.warn("No " + PROVIDER_PROPERTY_FILE
  + " resource found. You should have at least one provider.");
  }
  }
   */

  /*
  private static void loadProviderProperties(InputStream is) {
  Properties props = new Properties();
  try {
  props.load(is);
  AbstractionProperties map = null;
  AbstractionProperties common = new AbstractionProperties();
  String classLoader = null;
  String classLoaderProps = null;
  String classLoaderBoot = null;
  boolean nameFound = false;
  Iterator i = props.iterator();
  while (i.hasNext()) {
  Property prop = (Property) i.next();

  if (prop.name.equalsIgnoreCase("provider")) {
  map = new AbstractionProperties();
  map.putAll(common);
  providerProperties.put(prop.value.trim().toLowerCase(), map);
  nameFound = true;
  }
  else if (prop.name.equalsIgnoreCase("alias")) {
  String[] alias = prop.value.split(":");
  if (alias.length != 2) {
  logger.warn("Invalid alias line: " + prop.name + "=" + prop.value);
  }
  else {
  aliases.put(alias[0], alias[1]);
  }
  }
  else {
  if (map == null) {
  common.put(prop.name.trim().toLowerCase(), prop.value.trim());
  }
  else {
  map.put(prop.name.trim().toLowerCase(), prop.value.trim());
  }
  }
  }
  if (!nameFound) {
  logger.warn("Provider name missing from provider properties file");
  }
  }
  catch (Exception e) {
  logger.warn("Could not load properties", e);
  }
  }

   */
  public static void addActionListener(Object source, String eventType) {
    actionTable.put(source, eventType);

    if (eventType.equalsIgnoreCase("JobStatus")) {
      jobStatusSource = source;
    }
  }

  public static ActionListener getActionListener() {
    return phantom;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (!actionTable.containsKey(e.getSource())) {
      System.err.println("Error in actionPerformed. Ignored...");
      return;
    }

    String actionType = actionTable.get(e.getSource());
    String arg = e.getActionCommand();

    if (actionType.equalsIgnoreCase(SCRIPT_SUBMITTER_ARG)) {
      ScriptSubmitterDialogInterface ssdi = scriptSubmitters.get(SUBMITTER_KEY + "." + arg);
      JDialog dialog = ssdi.getDialog();
      dialog.setLocationRelativeTo(null);
      dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      dialog.setVisible(true);
    } else if (actionType.equalsIgnoreCase(JOB_STATUS_DIALOG)) {
      invokeJobStatusDialog();
    }

  }

  public static void main(String[] args) {
    GridProviders gridproviders = new GridProviders();
    gridproviders.getProviders();
    logger.info("Job status dialog: " + GridProviders.isJobStatusDialogAvailable());
  }
}
